package com.zl.skypointer.apm.collector.core.module;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 执行 ModuleProvider 准备阶段的逻辑。在改方法内部，会创建 ModuleProvider 对应的 Service
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
     * 获得组件服务提供者名。
     * eg：redis、jetty等
     * @return the name of this provider.
     */
    public abstract String name();

    /**
     * 获得 ModuleProvider 对应的 Module 类。注意，ModuleProvider 的名字可以重复
     * @return the module name
     */
    public abstract Class<? extends Module> module();

    /**
     * 执行 ModuleProvider 准备阶段的逻辑：Service 的创建，私有变量的创建等等
     * In prepare stage, the module should initialize things which are irrelative other modules.
     *
     * @param config from `application.yml`
     */
    public abstract void prepare(Properties config) throws ServiceNotProvidedException;

    /**
     * 执行 ModuleProvider 启动阶段的逻辑：私有变量的初始化等等。
     * In start stage, the module has been ready for interop.
     *
     * @param config from `application.yml`
     */
    public abstract void start(Properties config) throws ServiceNotProvidedException;

    /**
     * 执行 ModuleProvider 启动完成阶段的逻辑：私有变量的初始化等等。
     * This callback executes after all modules start up successfully.
     *
     * @throws ServiceNotProvidedException
     */
    public abstract void notifyAfterCompleted() throws ServiceNotProvidedException;

    /**
     * 获得 ModuleProvider 依赖的 Module 名字数组
     * @return module names which does this module require?
     */
    public abstract String[] requiredModules();

    /**
     * 注册 Service 对象。一个 ModuleProvider 可以有 0 到 N 个 Service 对象。
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
     * 校验 ModuleProvider 包含的 Service 们都创建成功
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

    /**
     * 获得 Service 对象
     */
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
