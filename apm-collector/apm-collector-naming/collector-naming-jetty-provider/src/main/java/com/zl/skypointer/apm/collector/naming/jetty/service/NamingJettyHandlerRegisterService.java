package com.zl.skypointer.apm.collector.naming.jetty.service;

import com.zl.skypointer.apm.collector.core.module.ModuleManager;
import com.zl.skypointer.apm.collector.jetty.manager.JettyManagerModule;
import com.zl.skypointer.apm.collector.jetty.manager.service.JettyManagerService;
import com.zl.skypointer.apm.collector.naming.service.NamingHandlerRegisterService;
import com.zl.skypointer.apm.collector.server.ServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouliang
 * 2018-02-10 20:04
 */
public class NamingJettyHandlerRegisterService implements NamingHandlerRegisterService {

    private final Logger logger = LoggerFactory.getLogger(NamingJettyHandlerRegisterService.class);

    private final ModuleManager moduleManager;
    private final String host;
    private final int port;

    public NamingJettyHandlerRegisterService(String host, int port, ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        this.host = host;
        this.port = port;
    }

    @Override public void register(ServerHandler namingHandler) {
        JettyManagerService managerService = moduleManager.find(JettyManagerModule.NAME).getService(JettyManagerService.class);
        managerService.addHandler(this.host, this.port, namingHandler);
    }
}
