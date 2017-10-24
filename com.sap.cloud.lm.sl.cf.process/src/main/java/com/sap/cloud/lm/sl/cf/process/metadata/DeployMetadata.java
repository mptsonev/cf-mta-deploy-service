package com.sap.cloud.lm.sl.cf.process.metadata;

import java.util.HashSet;
import java.util.Set;

import com.sap.cloud.lm.sl.cf.process.Constants;
import com.sap.cloud.lm.sl.cf.web.api.model.OperationMetadata;
import com.sap.cloud.lm.sl.cf.web.api.model.ParameterMetadata;
import com.sap.cloud.lm.sl.mta.model.VersionRule;

public class DeployMetadata {

    private final static Set<ParameterMetadata> PARAMS = new HashSet<ParameterMetadata>();

    static {
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_APP_ARCHIVE_ID).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_TARGET_NAME).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_EXT_DESCRIPTOR_FILE_ID).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_NO_START).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_START_TIMEOUT).defaultValue(Constants.DEFAULT_START_TIMEOUT).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_UPLOAD_TIMEOUT).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_USE_NAMESPACES).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_USE_NAMESPACES_FOR_SERVICES).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_ALLOW_INVALID_ENV_NAMES).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_VERSION_RULE).defaultValue(VersionRule.SAME_HIGHER.toString()).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_DELETE_SERVICES).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_DELETE_SERVICE_KEYS).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_DELETE_SERVICE_BROKERS).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_FAIL_ON_CRASHED).defaultValue(true).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_MTA_ID).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_KEEP_FILES).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_NO_RESTART_SUBSCRIBED_APPS).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_NO_FAIL_ON_MISSING_PERMISSIONS).defaultValue(false).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_GIT_URI).defaultValue("").build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_GIT_REF).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_GIT_REPO_PATH).build());
        PARAMS.add(ParameterMetadata.builder().id(Constants.PARAM_GIT_SKIP_SSL).defaultValue(false).build());
    }

    public static OperationMetadata getMetadata() {
        return OperationMetadata.builder().parameters(PARAMS).processId(Constants.DEPLOY_SERVICE_ID).versions(Constants.SERVICE_VERSION_1_1,
            Constants.SERVICE_VERSION_1_2).build();
    }

}