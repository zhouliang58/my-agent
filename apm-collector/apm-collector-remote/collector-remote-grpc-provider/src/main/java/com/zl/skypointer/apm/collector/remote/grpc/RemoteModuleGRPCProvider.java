package com.zl.skypointer.apm.collector.remote.grpc;

import com.zl.skypointer.apm.collector.cluster.ClusterModule;
import com.zl.skypointer.apm.collector.cluster.service.ModuleListenerService;
import com.zl.skypointer.apm.collector.cluster.service.ModuleRegisterService;
import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.core.module.ModuleProvider;
import com.zl.skypointer.apm.collector.core.module.ServiceNotProvidedException;
import com.zl.skypointer.apm.collector.grpc.manager.GRPCManagerModule;
import com.zl.skypointer.apm.collector.grpc.manager.service.GRPCManagerService;
import com.zl.skypointer.apm.collector.remote.RemoteModule;
import com.zl.skypointer.apm.collector.remote.grpc.handler.RemoteCommonServiceHandler;
import com.zl.skypointer.apm.collector.remote.grpc.service.GRPCRemoteSenderService;
import com.zl.skypointer.apm.collector.remote.service.CommonRemoteDataRegisterService;
import com.zl.skypointer.apm.collector.remote.service.RemoteDataRegisterService;
import com.zl.skypointer.apm.collector.remote.service.RemoteSenderService;
import com.zl.skypointer.apm.collector.server.Server;

import java.util.Properties;

/**
 * Created by zhouliang
 * 2018-02-13 10:34
 */
public class RemoteModuleGRPCProvider extends ModuleProvider {

    public static final String NAME = "gRPC";

    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String CHANNEL_SIZE = "channel_size";
    private static final String BUFFER_SIZE = "buffer_size";

    private GRPCRemoteSenderService remoteSenderService;
    private CommonRemoteDataRegisterService remoteDataRegisterService;

    @Override public String name() {
        return NAME;
    }

    @Override public Class<? extends Module> module() {
        return RemoteModule.class;
    }

    @Override public void prepare(Properties config) throws ServiceNotProvidedException {
        String host = config.getProperty(HOST);
        Integer port = (Integer)config.get(PORT);
        Integer channelSize = (Integer)config.getOrDefault(CHANNEL_SIZE, 5);
        Integer bufferSize = (Integer)config.getOrDefault(BUFFER_SIZE, 1000);

        remoteDataRegisterService = new CommonRemoteDataRegisterService();
        remoteSenderService = new GRPCRemoteSenderService(host, port, channelSize, bufferSize, remoteDataRegisterService);
        this.registerServiceImplementation(RemoteSenderService.class, remoteSenderService);
        this.registerServiceImplementation(RemoteDataRegisterService.class, remoteDataRegisterService);
    }

    @Override public void start(Properties config) throws ServiceNotProvidedException {
        String host = config.getProperty(HOST);
        Integer port = (Integer)config.get(PORT);

        GRPCManagerService managerService = getManager().find(GRPCManagerModule.NAME).getService(GRPCManagerService.class);
        Server gRPCServer = managerService.createIfAbsent(host, port);
        gRPCServer.addHandler(new RemoteCommonServiceHandler(remoteDataRegisterService));

        ModuleRegisterService moduleRegisterService = getManager().find(ClusterModule.NAME).getService(ModuleRegisterService.class);
        moduleRegisterService.register(RemoteModule.NAME, this.name(), new RemoteModuleGRPCRegistration(host, port));

        ModuleListenerService moduleListenerService = getManager().find(ClusterModule.NAME).getService(ModuleListenerService.class);
        moduleListenerService.addListener(remoteSenderService);
    }

    @Override public void notifyAfterCompleted() throws ServiceNotProvidedException {

    }

    @Override public String[] requiredModules() {
        return new String[] {ClusterModule.NAME, GRPCManagerModule.NAME};
    }
}