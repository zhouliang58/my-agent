package com.zl.skypointer.apm.collector.core.module;

import java.util.*;

/**
 * Created by zhouliang
 * 2018-02-06 19:32
 */
public class ModuleManager {
    private Map<String, Module> loadedModules = new HashMap<>();

    /**
     * Init the given modules
     *
     * @param applicationConfiguration
     */
    public void init(
            ApplicationConfiguration applicationConfiguration)
            throws ModuleNotFoundException, ProviderNotFoundException,
            ServiceNotProvidedException, CycleDependencyException {
        String[] moduleNames = applicationConfiguration.moduleList();

        /**
         * 加载所有 Module 实现类的实例数组。ServiceManager 基于 SPI (Service Provider Interface) 机制
         */
        ServiceLoader<Module> moduleServiceLoader = ServiceLoader.load(Module.class);
        LinkedList<String> moduleList = new LinkedList(Arrays.asList(moduleNames));

        /**
         * 遍历所有 Module 实现类的实例数组，创建在配置中的 Module 实现类的实例，并执行 Module 准备阶段的逻辑，
         * 后添加到加载的组件实例的映射( loadedModules )。
         */
        for (Module module : moduleServiceLoader) {
            for (String moduleName : moduleNames) {
                if (moduleName.equals(module.name())) {
                    Module newInstance;
                    try {
                        newInstance = module.getClass().newInstance();
                    } catch (InstantiationException e) {
                        throw new ModuleNotFoundException(e);
                    } catch (IllegalAccessException e) {
                        throw new ModuleNotFoundException(e);
                    }
                    newInstance.prepare(this, applicationConfiguration.getModuleConfiguration(moduleName));
                    loadedModules.put(moduleName, newInstance);
                    moduleList.remove(moduleName);
                }
            }
        }

        /**
         * 校验在配置中的 Module 实现类的实例都创建了，否则抛出异常。
         */
        if (moduleList.size() > 0) {
            throw new ModuleNotFoundException(moduleList.toString() + " missing.");
        }

        /**
         * 执行 Module 启动逻辑
         */
        BootstrapFlow bootstrapFlow = new BootstrapFlow(loadedModules, applicationConfiguration);

        /**
         * 执行 Module 启动完成，通知 ModuleProvider
         */
        bootstrapFlow.start(this, applicationConfiguration);
        bootstrapFlow.notifyAfterCompleted();
    }

    public boolean has(String moduleName) {
        return loadedModules.get(moduleName) != null;
    }

    public Module find(String moduleName) throws ModuleNotFoundRuntimeException {
        Module module = loadedModules.get(moduleName);
        if (module != null)
            return module;
        throw new ModuleNotFoundRuntimeException(moduleName + " missing.");
    }
}
