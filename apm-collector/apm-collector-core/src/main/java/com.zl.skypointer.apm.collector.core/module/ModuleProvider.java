package com.zl.skypointer.apm.collector.core.module;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhouliang
 * 2018-02-07 10:10
 */
public abstract class ModuleProvider {
    private ModuleManager manager;
    private Module module;
    private Map<Class<? extends Service>, Service> services = new HashMap<>();

    public ModuleProvider() {
    }

    void setManager(ModuleManager manager) {
        this.manager = manager;
    }

    void setModule(Module module) {
        this.module = module;
    }

    protected final ModuleManager getManager() {
        return manager;
    }

    /**
     * @return the name of this provider.
     */
    public abstract String name();

    /**
     * @return the module name
     */
    public abstract Class<? extends Module> module();

    /**
     * In prepare stage, the module should initialize things which are irrelative other modules.
     *
     * @param config from `application.yml`
     */
    public abstract void prepare(Properties config) throws ServiceNotProvidedException;

    /**
     * In start stage, the module has been ready for interop.
     *
     * @param config from `application.yml`
     */
    public abstract void start(Properties config) throws ServiceNotProvidedException;

    /**
     * This callback executes after all modules start up successfully.
     *
     * @throws ServiceNotProvidedException
     */
    public abstract void notifyAfterCompleted() throws ServiceNotProvidedException;

    /**
     * @return module names which does this module require?
     */
    public abstract String[] requiredModules();

    /**
     * Register a implementation for the service of this module provider.
     *
     * @param serviceType
     * @param service
     */
    protected final void registerServiceImplementation(Class<? extends Service> serviceType,
                                                       Service service) throws ServiceNotProvidedException {
        if (serviceType.isInstance(service)) {
            this.services.put(serviceType, service);
        } else {
            throw new ServiceNotProvidedException(serviceType + " is not implemented by " + service);
        }
    }

    /**
     * Make sure all required services have been implemented.
     *
     * @param requiredServices must be implemented by the module.
     * @throws ServiceNotProvidedException when exist unimplemented service.
     */
    void requiredCheck(Class<? extends Service>[] requiredServices) throws ServiceNotProvidedException {
        if (requiredServices == null)
            return;

        for (Class<? extends Service> service : requiredServices) {
            if (!services.containsKey(service)) {
                throw new ServiceNotProvidedException("Service:" + service.getName() + " not provided");
            }
        }

        if (requiredServices.length != services.size()) {
            throw new ServiceNotProvidedException("The " + this.name() + " provider in " + module.name() + " module provide more service implementations than Module requirements.");
        }
    }

    <T extends Service> T getService(Class<T> serviceType) throws ServiceNotProvidedException {
        Service serviceImpl = services.get(serviceType);
        if (serviceImpl != null) {
            return (T) serviceImpl;
        }

        throw new ServiceNotProvidedException("Service " + serviceType.getName() + " should not be provided, based on module define.");
    }

    Module getModule() {
        return module;
    }

    String getModuleName() {
        return module.name();
    }
}
