package com.sap.cloud.lm.sl.cf.core.parser;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sap.cloud.lm.sl.cf.client.lib.domain.CloudTask;
import com.sap.cloud.lm.sl.cf.core.helpers.MapToEnvironmentConverter;
import com.sap.cloud.lm.sl.cf.core.validators.parameters.TasksValidator;
import com.sap.cloud.lm.sl.mta.util.PropertiesUtil;

public class TaskParametersParser implements ParametersParser<List<CloudTask>> {

    private String parameterName;
    private boolean prettyPrinting;

    public TaskParametersParser(String parameterName, boolean prettyPrinting) {
        this.parameterName = parameterName;
        this.prettyPrinting = prettyPrinting;
    }

    @Override
    public List<CloudTask> parse(List<Map<String, Object>> parametersList) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> tasks = (List<Map<String, Object>>) PropertiesUtil.getPropertyValue(parametersList, parameterName,
            Collections.emptyList());

        return tasks.stream().map(task -> toCloudTask(task)).collect(Collectors.toList());
    }

    private CloudTask toCloudTask(Map<String, Object> rawTask) {
        CloudTask task = new CloudTask(null, getProperty(rawTask, TasksValidator.TASK_NAME_KEY));
        task.setCommand(getProperty(rawTask, TasksValidator.TASK_COMMAND_KEY));
        task.setEnvironmentVariables(getEnvironmentVariables(rawTask));
        return task;
    }

    private Map<String, String> getEnvironmentVariables(Map<String, Object> rawTask) {
        Map<String, Object> env = getProperty(rawTask, TasksValidator.TASK_ENV_KEY);
        return env == null ? null : new MapToEnvironmentConverter(prettyPrinting).asEnv(env);
    }

    @SuppressWarnings("unchecked")
    private <T> T getProperty(Map<String, Object> rawTask, String key) {
        return (T) rawTask.get(key);
    }

}
