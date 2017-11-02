package com.sap.cloud.lm.sl.cf.process.metadata;

import java.util.HashSet;
import java.util.Set;

import com.sap.cloud.lm.sl.cf.process.Constants;
import com.sap.cloud.lm.sl.cf.web.api.model.OperationMetadata;
import com.sap.cloud.lm.sl.cf.web.api.model.ParameterMetadata;

public class UndeployMetadata {

    private final static Set<ParameterMetadata> PARAMS = new HashSet<ParameterMetadata>();

    // TODO: for each of the params put the type...
    static {
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_DELETE_SERVICES).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_DELETE_SERVICE_BROKERS).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_MTA_ID).required(true).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_NO_RESTART_SUBSCRIBED_APPS).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_NO_FAIL_ON_MISSING_PERMISSIONS).defaultValue(false).build());
    }

    public static OperationMetadata getMetadata() {
        return OperationMetadata.builder().parameters(PARAMS).processId(Constants.UNDEPLOY_SERVICE_ID).versions(
            Constants.SERVICE_VERSION_1_0).build();
    }
}
