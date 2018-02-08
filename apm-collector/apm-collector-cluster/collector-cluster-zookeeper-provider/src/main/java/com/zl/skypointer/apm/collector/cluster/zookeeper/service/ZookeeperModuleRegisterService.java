package com.zl.skypointer.apm.collector.cluster.zookeeper.service;

import com.zl.skypointer.apm.collector.cluster.ModuleRegistration;
import com.zl.skypointer.apm.collector.cluster.service.ModuleRegisterService;
import com.zl.skypointer.apm.collector.cluster.zookeeper.ClusterZKDataMonitor;

/**
 * Created by zhouliang
 * 2018-02-08 16:40
 */
public class ZookeeperModuleRegisterService implements ModuleRegisterService {

    private final ClusterZKDataMonitor dataMonitor;

    public ZookeeperModuleRegisterService(ClusterZKDataMonitor dataMonitor) {
        this.dataMonitor = dataMonitor;
    }

    @Override public void register(String moduleName, String providerName, ModuleRegistration registration) {
        String path = "/" + moduleName + "/" + providerName;
        dataMonitor.register(path, registration);
    }
}