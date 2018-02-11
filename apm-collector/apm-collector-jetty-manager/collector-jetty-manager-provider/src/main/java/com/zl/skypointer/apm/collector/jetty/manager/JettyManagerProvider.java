package com.zl.skypointer.apm.collector.jetty.manager;

import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.core.module.ModuleProvider;
import com.zl.skypointer.apm.collector.core.module.ServiceNotProvidedException;
import com.zl.skypointer.apm.collector.jetty.manager.service.JettyManagerService;
import com.zl.skypointer.apm.collector.jetty.manager.service.JettyManagerServiceImpl;
import com.zl.skypointer.apm.collector.server.ServerException;
import com.zl.skypointer.apm.collector.server.jetty.JettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhouliang
 * 2018-02-10 19:41
 */
public class JettyManagerProvider extends ModuleProvider {

    private final Logger logger = LoggerFactory.getLogger(JettyManagerProvider.class);

    private Map<String, JettyServer> servers = new HashMap<>();

    @Override public String name() {
        return "jetty";
    }

    @Override public Class<? extends Module> module() {
        return JettyManagerModule.class;
    }

    @Override public void prepare(Properties config) throws ServiceNotProvidedException {
        this.registerServiceImplementation(JettyManagerService.class, new JettyManagerServiceImpl(servers));
    }

    @Override public void start(Properties config) throws ServiceNotProvidedException {

    }

    @Override public void notifyAfterCompleted() throws ServiceNotProvidedException {
        servers.values().forEach(server -> {
            try {
                server.start();
            } catch (ServerException e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    @Override public String[] requiredModules() {
        return new String[0];
    }
}
