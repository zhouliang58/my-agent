package com.zl.skypointer.apm.collector.grpc.manager;

import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.grpc.manager.service.GRPCManagerService;

/**
 * Created by zhouliang
 * 2018-02-10 19:50
 */
public class GRPCManagerModule extends Module {

    public static final String NAME = "gRPC_manager";

    @Override public String name() {
        return NAME;
    }

    @Override public Class[] services() {
        return new Class[] {GRPCManagerService.class};
    }
}
