package com.zl.skypointer.apm.collector.naming.jetty;

import com.zl.skypointer.apm.collector.cluster.ClusterModule;
import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.core.module.ModuleProvider;
import com.zl.skypointer.apm.collector.core.module.ServiceNotProvidedException;
import com.zl.skypointer.apm.collector.jetty.manager.JettyManagerModule;
import com.zl.skypointer.apm.collector.jetty.manager.service.JettyManagerService;
import com.zl.skypointer.apm.collector.naming.NamingModule;
import com.zl.skypointer.apm.collector.naming.jetty.service.NamingJettyHandlerRegisterService;
import com.zl.skypointer.apm.collector.naming.service.NamingHandlerRegisterService;

import java.util.Properties;

/**
 * Created by zhouliang
 * 2018-02-10 20:04
 */
public class NamingModuleJettyProvider extends ModuleProvider {

    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String CONTEXT_PATH = "context_path";

    @Override public String name() {
        return "jetty";
    }

    @Override public Class<? extends Module> module() {
        return NamingModule.class;
    }

    @Override public void prepare(Properties config) throws ServiceNotProvidedException {
        final String host = config.getProperty(HOST);
        final Integer port = (Integer)config.get(PORT);
        this.registerServiceImplementation(NamingHandlerRegisterService.class, new NamingJettyHandlerRegisterService(host, port, getManager()));
    }

    @Override public void start(Properties config) throws ServiceNotProvidedException {
        String host = config.getProperty(HOST);
        Integer port = (Integer)config.get(PORT);
        String contextPath = config.getProperty(CONTEXT_PATH);

        JettyManagerService managerService = getManager().find(JettyManagerModule.NAME).getService(JettyManagerService.class);
        managerService.createIfAbsent(host, port, contextPath);
    }

    @Override public void notifyAfterCompleted() throws ServiceNotProvidedException {
    }

    @Override public String[] requiredModules() {
        return new String[] {ClusterModule.NAME, JettyManagerModule.NAME};
    }
}