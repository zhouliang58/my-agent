package com.zl.skypointer.apm.collector.core.module;

import java.util.HashMap;
import java.util.Properties;

/**
 * Created by zhouliang
 * 2018-02-06 19:06
 */
public class ApplicationConfiguration {

    /**
     * 一个应用配置类包含多个组件配置类( ModuleConfiguration )。每个组件对应一个组件配置类。
     */
    private HashMap<String, ModuleConfiguration> modules = new HashMap<>();

    public String[] moduleList() {
        return modules.keySet().toArray(new String[0]);
    }

    public ModuleConfiguration addModule(String moduleName) {
        ModuleConfiguration newModule = new ModuleConfiguration();
        modules.put(moduleName, newModule);
        return newModule;
    }

    public boolean has(String moduleName) {
        return modules.containsKey(moduleName);
    }

    public ModuleConfiguration getModuleConfiguration(String name) {
        return modules.get(name);
    }

    /**
     * The configurations about a certain module.
     */
    public class ModuleConfiguration {
        /**
         * 一个组件配置类包含多个组件服务提供者配置( ProviderConfiguration )。每个组件服务提供者对应一个组件配置类。
         * 注意：因为一个组件只允许同时使用一个组件服务提供者，所以一个组件配置类只设置一个组件服务提供者配置。
         */
        private HashMap<String, ProviderConfiguration> providers = new HashMap<>();

        private ModuleConfiguration() {
        }

        public Properties getProviderConfiguration(String name) {
            return providers.get(name).getProperties();
        }

        public boolean has(String name) {
            return providers.containsKey(name);
        }

        public ModuleConfiguration addProviderConfiguration(String name, Properties properties) {
            ProviderConfiguration newProvider = new ProviderConfiguration(properties);
            providers.put(name, newProvider);
            return this;
        }
    }

    /**
     * The configuration about a certain provider of a module.
     */
    public class ProviderConfiguration {
        private Properties properties;

        ProviderConfiguration(Properties properties) {
            this.properties = properties;
        }

        public Properties getProperties() {
            return properties;
        }
    }
}

