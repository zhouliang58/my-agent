package com.zl.skypointer.apm.collector.cluster.redis;

import com.zl.skypointer.apm.collector.cluster.redis.service.RedisModuleRegisterService;
import com.zl.skypointer.apm.collector.cluster.ClusterModule;
import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.core.module.ModuleProvider;
import com.zl.skypointer.apm.collector.core.module.ServiceNotProvidedException;
import com.zl.skypointer.apm.collector.cluster.service.ModuleRegisterService;

import java.util.Properties;

/**
 * Created by zhouliang
 * 2018-02-08 15:51
 */
public class ClusterModuleRedisProvider extends ModuleProvider {

    @Override public String name() {
        return "redis";
    }

    @Override public Class<? extends Module> module() {
        return ClusterModule.class;
    }

    @Override public void prepare(Properties config) throws ServiceNotProvidedException {
        this.registerServiceImplementation(ModuleRegisterService.class, new RedisModuleRegisterService());
    }

    @Override public void start(Properties config) throws ServiceNotProvidedException {

    }

    @Override public void notifyAfterCompleted() throws ServiceNotProvidedException {

    }

    @Override public String[] requiredModules() {
        return new String[0];
    }
}