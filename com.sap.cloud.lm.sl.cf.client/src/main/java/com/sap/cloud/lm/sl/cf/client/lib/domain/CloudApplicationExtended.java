package com.sap.cloud.lm.sl.cf.client.lib.domain;

import java.util.List;
import java.util.Map;

import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudRoute;

import com.google.gson.annotations.JsonAdapter;
import com.sap.cloud.lm.sl.common.model.json.PropertiesAdapterFactory;

public class CloudApplicationExtended extends CloudApplication {

    private String moduleName;
    private List<String> idleUris;
    @JsonAdapter(PropertiesAdapterFactory.class)
    private Map<String, Map<String, Object>> bindingParameters;
    private List<CloudTask> tasks;
    private List<CloudRoute> routes;
    private List<ServiceKeyToInject> serviceKeysToInject;
    private List<ApplicationPort> applicationPorts;
    private List<String> domains;

    public CloudApplicationExtended(Meta meta, String name) {
        super(meta, name);
    }

    public CloudApplicationExtended(String name, String command, String buildpackUrl, int memory, int instances, List<String> uris,
        List<String> serviceNames, AppState state, List<ApplicationPort> applicationPorts, List<String> domains) {
        super(name, command, buildpackUrl, memory, instances, uris, serviceNames, state);
        this.applicationPorts = applicationPorts;
        this.domains = domains;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public List<String> getIdleUris() {
        return idleUris;
    }

    public void setIdleUris(List<String> idleUris) {
        this.idleUris = idleUris;
    }

    public Map<String, Map<String, Object>> getBindingParameters() {
        return bindingParameters;
    }

    public void setBindingParameters(Map<String, Map<String, Object>> bindingParameters) {
        this.bindingParameters = bindingParameters;
    }

    public List<CloudTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<CloudTask> tasks) {
        this.tasks = tasks;
    }

    public List<CloudRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<CloudRoute> routes) {
        this.routes = routes;
    }

    public List<ServiceKeyToInject> getServiceKeysToInject() {
        return serviceKeysToInject;
    }

    public void setServiceKeysToInject(List<ServiceKeyToInject> serviceKeysToInject) {
        this.serviceKeysToInject = serviceKeysToInject;
    }

    public List<ApplicationPort> getApplicationPorts() {
        return applicationPorts;
    }

    public void setApplicationPorts(List<ApplicationPort> applicationPorts) {
        this.applicationPorts = applicationPorts;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }
}
