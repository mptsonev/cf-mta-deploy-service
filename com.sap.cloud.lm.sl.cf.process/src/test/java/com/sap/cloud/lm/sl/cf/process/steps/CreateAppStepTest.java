package com.sap.cloud.lm.sl.cf.process.steps;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.domain.ServiceKey;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import com.sap.cloud.lm.sl.cf.client.ClientExtensions;
import com.sap.cloud.lm.sl.cf.client.lib.domain.CloudApplicationExtended;
import com.sap.cloud.lm.sl.cf.client.lib.domain.CloudServiceExtended;
import com.sap.cloud.lm.sl.cf.core.cf.PlatformType;
import com.sap.cloud.lm.sl.cf.core.cf.clients.ApplicationStagingUpdater;
import com.sap.cloud.lm.sl.cf.core.dao.ContextExtensionDao;
import com.sap.cloud.lm.sl.cf.process.Constants;
import com.sap.cloud.lm.sl.common.util.GenericArgumentMatcher;
import com.sap.cloud.lm.sl.common.util.JsonUtil;
import com.sap.cloud.lm.sl.common.util.ListUtil;
import com.sap.cloud.lm.sl.common.util.TestUtil;

@RunWith(Parameterized.class)
public class CreateAppStepTest extends SyncActivitiStepTest<CreateAppStep> {

    private final StepInput stepInput;
    private final String expectedExceptionMessage;

    private CloudApplicationExtended application;

    private ApplicationStagingUpdater applicationUpdater = Mockito.mock(ApplicationStagingUpdater.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    ContextExtensionDao dao;

    @Parameters
    public static Iterable<Object[]> getParameters() {
        return Arrays.asList(new Object[][] {
// @formatter:off
            // (0) Disk quota is 0:
            {
                "create-app-step-input-00.json", null, PlatformType.XS2
            },
            // (1) Memory is 0:
            {
                "create-app-step-input-01.json", null, PlatformType.CF
            },
            // (2) Everything is specified properly:
            {
                "create-app-step-input-02.json", null, PlatformType.XS2
            },
            // (3) Binding parameters exist, and the services do too:
            {
                "create-app-step-input-03.json", null, PlatformType.CF
            },
            // (4) Binding parameters exist, but the services do not:
            {
                "create-app-step-input-04.json", "Could not bind application \"application\" to service \"service-2\": 500 Internal Server Error: Something happened!", null,
            },
            // (5) Binding parameters exist, but the services do not and service-2 is optional - so no exception should be thrown:
            {
                "create-app-step-input-05.json", null, PlatformType.CF,
            },
            // (6) Service keys to inject are specified:
            { "create-app-step-input-06.json", null, PlatformType.XS2 },
            // (7) Service keys to inject are specified but not exist:
            { "create-app-step-input-07.json",
                "Unable to retrieve required service key element \"expected-service-key\" for service \"existing-service\"",
                PlatformType.XS2
            },
// @formatter:on
        });
    }

    public CreateAppStepTest(String stepInput, String expectedExceptionMessage, PlatformType platform) throws Exception {
        this.stepInput = JsonUtil.fromJson(TestUtil.getResourceAsString(stepInput, CreateAppStepTest.class), StepInput.class);
        this.stepInput.platform = platform;
        this.expectedExceptionMessage = expectedExceptionMessage;
    }

    @Before
    public void setUp() throws Exception {
        loadParameters();
        prepareContext();
        prepareClient();
    }

    @Test
    public void testExecute() throws Exception {
        step.execute(context);

        assertStepFinishedSuccessfully();

        validateClient();
        validateApplicationUpdate();
    }

    private void validateApplicationUpdate() {
        if (stepInput.platform == PlatformType.CF) {
            Mockito.verify(applicationUpdater).updateApplicationStaging(eq(client), eq(application.getName()),
                eq(application.getStaging()));
        }
    }

    private void loadParameters() {
        if (expectedExceptionMessage != null) {
            expectedException.expectMessage(expectedExceptionMessage);
        }
        application = stepInput.applications.get(stepInput.applicationIndex);
        application.setModuleName("test");
    }

    private void prepareContext() {
        StepsUtil.setAppsToDeploy(context, stepInput.applications);
        StepsTestUtil.mockApplicationsToDeploy(stepInput.applications, context);
        StepsUtil.setServicesToBind(context, mapToCloudServiceExtended());
        context.setVariable(Constants.PARAM_APP_ARCHIVE_ID, "dummy");
        context.setVariable(Constants.VAR_APPS_INDEX, stepInput.applicationIndex);
        byte[] serviceKeysToInjectByteArray = JsonUtil.getAsBinaryJson(new HashMap<>());
        context.setVariable(Constants.VAR_SERVICE_KEYS_CREDENTIALS_TO_INJECT, serviceKeysToInjectByteArray);
    }

    private List<CloudServiceExtended> mapToCloudServiceExtended() {
        return application.getServices().stream().map(serviceName -> extracted(serviceName)).collect(Collectors.toList());
    }

    private CloudServiceExtended extracted(String serviceName) {
        for (SimpleService simpleService : stepInput.services) {
            if (simpleService.name.equals(serviceName)) {
                return simpleService.toCloudServiceExtended();
            }
        }
        return new CloudServiceExtended(null, serviceName);
    }

    private void prepareClient() {
        Mockito.when(configuration.getPlatformType()).thenReturn(stepInput.platform);
        for (SimpleService simpleService : stepInput.services) {
            CloudServiceExtended service = simpleService.toCloudServiceExtended();
            if (!service.isOptional()) {
                Mockito.when(client.getService(service.getName())).thenReturn(service);
            }
        }

        for (String appName : stepInput.bindingErrors.keySet()) {
            String serviceName = stepInput.bindingErrors.get(appName);
            Mockito
                .doThrow(new CloudFoundryException(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    "Something happened!"))
                .when((ClientExtensions) client)
                .bindService(Mockito.eq(appName), Mockito.eq(serviceName), Mockito.any());
        }

        for (String serviceName : stepInput.existingServiceKeys.keySet()) {
            List<ServiceKey> serviceKeys = stepInput.existingServiceKeys.get(serviceName);
            Mockito.when(client.getServiceKeys(eq(serviceName))).thenReturn(ListUtil.upcast(serviceKeys));
        }
    }

    private void validateClient() {
        Integer diskQuota = (application.getDiskQuota() != 0) ? application.getDiskQuota() : null;
        Integer memory = (application.getMemory() != 0) ? application.getMemory() : null;

        Mockito.verify(client).createApplication(eq(application.getName()),
            argThat(GenericArgumentMatcher.forObject(application.getStaging())), eq(diskQuota), eq(memory), eq(application.getUris()),
            eq(Collections.emptyList()));
        for (String service : application.getServices()) {
            if (!isOptional(service)) {
                if (application.getBindingParameters() == null || application.getBindingParameters().get(service) == null) {
                    Mockito.verify(client).bindService(application.getName(), service);
                } else {
                    Mockito.verify(clientExtensions).bindService(application.getName(), service,
                        application.getBindingParameters().get(service));
                }
            }
        }
        Mockito.verify(client).updateApplicationEnv(eq(application.getName()), eq(application.getEnvAsMap()));
    }

    private boolean isOptional(String service) {
        for (SimpleService simpleService : stepInput.services) {
            if (simpleService.name.equals(service)) {
                return simpleService.isOptional;
            }
        }
        return false;
    }

    private static class StepInput {
        List<CloudApplicationExtended> applications = Collections.emptyList();
        List<SimpleService> services = Collections.emptyList();
        int applicationIndex;
        PlatformType platform;
        Map<String, String> bindingErrors = new HashMap<>();
        Map<String, List<ServiceKey>> existingServiceKeys = new HashMap<>();
    }

    private static class SimpleService {
        String name;
        boolean isOptional;

        CloudServiceExtended toCloudServiceExtended() {
            CloudServiceExtended service = new CloudServiceExtended();
            service.setName(name);
            service.setOptional(isOptional);
            return service;
        }
    }

    @Override
    protected CreateAppStep createStep() {
        return new CreateAppStep();
    }

}
