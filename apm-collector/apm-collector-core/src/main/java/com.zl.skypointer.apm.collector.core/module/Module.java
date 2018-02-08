package com.zl.skypointer.apm.collector.core.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 通过实现 Module 抽象类，实现不同功能的组件。
 * Created by zhouliang
 * 2018-02-07 10:09
 */
public abstract class Module {

    private final Logger logger = LoggerFactory.getLogger(Module.class);

    private LinkedList<ModuleProvider> loadedProviders = new LinkedList<>();

    /**
     *  抽象方法，获得组件名。
     *  eg： ui,remote,storage等
     * @return the module name
     */
    public abstract String name();

    /**
     * 获得 Service 类数组。具体 Service 对象，在 ModuleProvider 对象里获取
     * @return the {@link Service} provided by this module.
     */
    public abstract Class[] services();

    /**
     * Run the prepare stage for the module, including finding all potential providers, and asking them to prepare.
     * 执行 ModuleProvider 准备阶段的逻辑。在改方法内部，会创建 ModuleProvider 对应的 Service
     * @param moduleManager of this module
     * @param configuration of this module
     * @throws ProviderNotFoundException when even don't find a single one providers.
     */
    void prepare(ModuleManager moduleManager,
                 ApplicationConfiguration.ModuleConfiguration configuration) throws ProviderNotFoundException, ServiceNotProvidedException {
        ServiceLoader<ModuleProvider> moduleProviderLoader = ServiceLoader.load(ModuleProvider.class);
        boolean providerExist = false;
        for (ModuleProvider provider : moduleProviderLoader) {
            if (!configuration.has(provider.name())) {
                continue;
            }

            providerExist = true;
            if (provider.module().equals(getClass())) {
                ModuleProvider newProvider;
                try {
                    newProvider = provider.getClass().newInstance();
                } catch (InstantiationException e) {
                    throw new ProviderNotFoundException(e);
                } catch (IllegalAccessException e) {
                    throw new ProviderNotFoundException(e);
                }
                newProvider.setManager(moduleManager);
                newProvider.setModule(this);
                loadedProviders.add(newProvider);
            }
        }

        if (!providerExist) {
            throw new ProviderNotFoundException(this.name() + " module no provider exists.");
        }

        for (ModuleProvider moduleProvider : loadedProviders) {
            logger.info("Prepare the {} provider in {} module.", moduleProvider.name(), this.name());
            moduleProvider.prepare(configuration.getProviderConfiguration(moduleProvider.name()));
        }
    }

    /**
     * 获得 ModuleProvider 数组。实际上，一个 Module 同时只能有一个 ModuleProvider
     * @return providers of this module
     */
    final List<ModuleProvider> providers() {
        return loadedProviders;
    }

    final ModuleProvider provider() throws ProviderNotFoundException, DuplicateProviderException {
        if (loadedProviders.size() > 1) {
            throw new DuplicateProviderException(this.name() + " module exist " + loadedProviders.size() + " providers");
        }

        return loadedProviders.getFirst();
    }

    public final <T extends Service> T getService(Class<T> serviceType) throws ServiceNotProvidedRuntimeException {
        try {
            return provider().getService(serviceType);
        } catch (ProviderNotFoundException | DuplicateProviderException | ServiceNotProvidedException e) {
            throw new ServiceNotProvidedRuntimeException(e.getMessage());
        }
    }
}
