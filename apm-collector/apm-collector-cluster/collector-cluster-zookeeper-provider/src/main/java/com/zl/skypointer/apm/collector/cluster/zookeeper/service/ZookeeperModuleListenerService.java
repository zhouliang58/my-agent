package com.zl.skypointer.apm.collector.cluster.zookeeper.service;

import com.zl.skypointer.apm.collector.cluster.ClusterModuleListener;
import com.zl.skypointer.apm.collector.cluster.service.ModuleListenerService;
import com.zl.skypointer.apm.collector.cluster.zookeeper.ClusterZKDataMonitor;

/**
 * Created by zhouliang
 * 2018-02-08 16:39
 */
public class ZookeeperModuleListenerService implements ModuleListenerService {

    private final ClusterZKDataMonitor dataMonitor;

    public ZookeeperModuleListenerService(ClusterZKDataMonitor dataMonitor) {
        this.dataMonitor = dataMonitor;
    }

    @Override public void addListener(ClusterModuleListener listener) {
        dataMonitor.addListener(listener);
    }
}
