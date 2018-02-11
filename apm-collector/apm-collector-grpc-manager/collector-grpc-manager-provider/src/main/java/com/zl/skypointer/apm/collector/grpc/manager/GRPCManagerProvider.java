package com.zl.skypointer.apm.collector.grpc.manager;

import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.core.module.ModuleProvider;
import com.zl.skypointer.apm.collector.core.module.ServiceNotProvidedException;
import com.zl.skypointer.apm.collector.grpc.manager.service.GRPCManagerService;
import com.zl.skypointer.apm.collector.grpc.manager.service.GRPCManagerServiceImpl;
import com.zl.skypointer.apm.collector.server.ServerException;
import com.zl.skypointer.apm.collector.server.grpc.GRPCServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhouliang
 * 2018-02-10 19:54
 */
public class GRPCManagerProvider extends ModuleProvider {

    private final Logger logger = LoggerFactory.getLogger(GRPCManagerProvider.class);

    private Map<String, GRPCServer> servers = new HashMap<>();

    @Override public String name() {
        return "gRPC";
    }

    @Override public Class<? extends Module> module() {
        return GRPCManagerModule.class;
    }

    @Override public void prepare(Properties config) throws ServiceNotProvidedException {
        this.registerServiceImplementation(GRPCManagerService.class, new GRPCManagerServiceImpl(servers));
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
