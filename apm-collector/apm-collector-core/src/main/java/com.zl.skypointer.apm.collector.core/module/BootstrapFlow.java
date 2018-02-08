package com.zl.skypointer.apm.collector.core.module;

import com.zl.skypointer.apm.collector.core.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 组件启动流程
 * Created by zhouliang
 * 2018-02-07 10:18
 */
public class BootstrapFlow {
    private final Logger logger = LoggerFactory.getLogger(BootstrapFlow.class);

    private Map<String, Module> loadedModules;
    private ApplicationConfiguration applicationConfiguration;
    private List<ModuleProvider> startupSequence;

    /**
     * 调用 #makeSequence() 方法，获得 ModuleProvider 启动顺序，这个是该类的重点。
     */
    public BootstrapFlow(Map<String, Module> loadedModules,
                         ApplicationConfiguration applicationConfiguration) throws CycleDependencyException {
        this.loadedModules = loadedModules;
        this.applicationConfiguration = applicationConfiguration;
        startupSequence = new LinkedList<>();

        makeSequence();
    }

    /**
     * 校验依赖 Module 已经都存在。
     * 校验 ModuleProvider 包含的 Service 们都创建成功。
     * 调用 ModuleProvider#start(...) 方法，执行 ModuleProvider 启动阶段逻辑。
     */
    void start(ModuleManager moduleManager,
               ApplicationConfiguration configuration) throws ProviderNotFoundException, ModuleNotFoundException, ServiceNotProvidedException {
        for (ModuleProvider provider : startupSequence) {
            String[] requiredModules = provider.requiredModules();
            if (requiredModules != null) {
                for (String module : requiredModules) {
                    if (!moduleManager.has(module)) {
                        throw new ModuleNotFoundException(module + " is required by " + provider.getModuleName()
                                + "." + provider.name() + ", but not found.");
                    }
                }
            }
            logger.info("start the provider {} in {} module.", provider.name(), provider.getModuleName());
            provider.requiredCheck(provider.getModule().services());

            provider.start(configuration.getModuleConfiguration(provider.getModuleName()).getProviderConfiguration(provider.name()));
        }
    }

    /**
     * 调用 ModuleProvider#notifyAfterCompleted() 方法，执行 ModuleProvider 启动完成阶段的逻辑。
     */
    void notifyAfterCompleted() throws ProviderNotFoundException, ModuleNotFoundException, ServiceNotProvidedException {
        for (ModuleProvider provider : startupSequence) {
            provider.notifyAfterCompleted();
        }
    }

    private void makeSequence() throws CycleDependencyException {
        List<ModuleProvider> allProviders = new ArrayList<>();
        loadedModules.forEach((moduleName, module) -> {
            module.providers().forEach(provider -> {
                allProviders.add(provider);
            });
        });

        while (true) {
            int numOfToBeSequenced = allProviders.size();
            for (int i = 0; i < allProviders.size(); i++) {
                ModuleProvider provider = allProviders.get(i);
                String[] requiredModules = provider.requiredModules();
                if (CollectionUtils.isNotEmpty(requiredModules)) {
                    boolean isAllRequiredModuleStarted = true;
                    for (String module : requiredModules) {
                        // find module in all ready existed startupSequence
                        boolean exist = false;
                        for (ModuleProvider moduleProvider : startupSequence) {
                            if (moduleProvider.getModuleName().equals(module)) {
                                exist = true;
                                break;
                            }
                        }
                        if (!exist) {
                            isAllRequiredModuleStarted = false;
                            break;
                        }
                    }

                    if (isAllRequiredModuleStarted) {
                        startupSequence.add(provider);
                        allProviders.remove(i);
                        i--;
                    }
                } else {
                    startupSequence.add(provider);
                    allProviders.remove(i);
                    i--;
                }
            }

            if (numOfToBeSequenced == allProviders.size()) {
                StringBuilder unsequencedProviders = new StringBuilder();
                allProviders.forEach(provider -> {
                    unsequencedProviders.append(provider.getModuleName()).append("[provider=").append(provider.getClass().getName()).append("]\n");
                });
                throw new CycleDependencyException("Exist cycle module dependencies in \n" + unsequencedProviders.substring(0, unsequencedProviders.length() - 1));
            }
            if (allProviders.size() == 0) {
                break;
            }
        }
    }
}
