package com.zl.skypointer.apm.collector.cluster.zookeeper;

import com.zl.skypointer.apm.collector.client.zookeeper.ZookeeperClient;
import com.zl.skypointer.apm.collector.client.zookeeper.ZookeeperClientException;
import com.zl.skypointer.apm.collector.cluster.ClusterModule;
import com.zl.skypointer.apm.collector.cluster.service.ModuleListenerService;
import com.zl.skypointer.apm.collector.cluster.service.ModuleRegisterService;
import com.zl.skypointer.apm.collector.cluster.zookeeper.service.ZookeeperModuleListenerService;
import com.zl.skypointer.apm.collector.cluster.zookeeper.service.ZookeeperModuleRegisterService;
import com.zl.skypointer.apm.collector.core.UnexpectedException;
import com.zl.skypointer.apm.collector.core.module.CollectorException;
import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.core.module.ModuleProvider;
import com.zl.skypointer.apm.collector.core.module.ServiceNotProvidedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by zhouliang
 * 2018-02-08 16:38
 */
public class ClusterModuleZookeeperProvider extends ModuleProvider {

    private final Logger logger = LoggerFactory.getLogger(ClusterModuleZookeeperProvider.class);

    private static final String HOST_PORT = "hostPort";
    private static final String SESSION_TIMEOUT = "sessionTimeout";

    private ZookeeperClient zookeeperClient;
    private ClusterZKDataMonitor dataMonitor;

    @Override public String name() {
        return "zookeeper";
    }

    @Override public Class<? extends Module> module() {
        return ClusterModule.class;
    }

    @Override public void prepare(Properties config) throws ServiceNotProvidedException {
        dataMonitor = new ClusterZKDataMonitor();

        final String hostPort = config.getProperty(HOST_PORT);
        final int sessionTimeout = (Integer)config.get(SESSION_TIMEOUT);
        zookeeperClient = new ZookeeperClient(hostPort, sessionTimeout, dataMonitor);
        dataMonitor.setClient(zookeeperClient);

        this.registerServiceImplementation(ModuleListenerService.class, new ZookeeperModuleListenerService(dataMonitor));
        this.registerServiceImplementation(ModuleRegisterService.class, new ZookeeperModuleRegisterService(dataMonitor));
    }

    @Override public void start(Properties config) throws ServiceNotProvidedException {
        try {
            zookeeperClient.initialize();
        } catch (ZookeeperClientException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override public void notifyAfterCompleted() throws ServiceNotProvidedException {
        try {
            dataMonitor.start();
        } catch (CollectorException e) {
            throw new UnexpectedException(e.getMessage());
        }
    }

    @Override public String[] requiredModules() {
        return new String[0];
    }

}
