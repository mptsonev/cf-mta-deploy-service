package com.sap.cloud.lm.sl.cf.core.cf.clients;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cloudfoundry.client.lib.CloudFoundryOperations;
import org.cloudfoundry.client.lib.domain.CloudServiceBroker;
import org.cloudfoundry.client.lib.util.CloudEntityResourceMapper;
import org.springframework.web.client.RestTemplate;

import com.sap.cloud.lm.sl.cf.client.lib.domain.CloudServiceBrokerExtended;

public class ServiceBrokersGetter extends CustomControllerClient {

    private static final String SERVICE_BROKERS_URL = "/v2/service_brokers";
    private CloudEntityResourceMapper resourceMapper = new CloudEntityResourceMapper();

    public List<CloudServiceBrokerExtended> getServiceBrokers(CloudFoundryOperations client) {
        return new CustomControllerClientErrorHandler().handleErrorsOrReturnResult(() -> attemptToGetServiceBrokers(client));
    }

    private List<CloudServiceBrokerExtended> attemptToGetServiceBrokers(CloudFoundryOperations client) {
        String controllerUrl = client.getCloudControllerUrl().toString();
        RestTemplate restTemplate = getRestTemplate(client);
        List<Map<String, Object>> resources = getAllResources(restTemplate, controllerUrl, SERVICE_BROKERS_URL);
        return toCloudServiceBrokers(resources);
    }

    private List<CloudServiceBrokerExtended> toCloudServiceBrokers(List<Map<String, Object>> resources) {
        return resources.stream().map(resource -> toCloudServiceBroker(resource)).collect(Collectors.toList());
    }

    private CloudServiceBrokerExtended toCloudServiceBroker(Map<String, Object> resource) {
        CloudServiceBroker serviceBroker = resourceMapper.mapResource(resource, CloudServiceBroker.class);
        String spaceGuid = CloudEntityResourceMapper.getEntityAttribute(resource, "space_guid", String.class);
        return toCloudServiceBrokerExtended(serviceBroker, spaceGuid);
    }

    protected CloudServiceBrokerExtended toCloudServiceBrokerExtended(CloudServiceBroker serviceBroker, String spaceGuid) {
        return new CloudServiceBrokerExtended(serviceBroker.getMeta(), serviceBroker.getName(), serviceBroker.getUrl(),
            serviceBroker.getUsername(), serviceBroker.getPassword(), spaceGuid);
    }

}
