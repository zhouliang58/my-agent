package com.zl.skypointer.apm.collector.grpc.manager.service;

import com.zl.skypointer.apm.collector.core.module.Service;
import com.zl.skypointer.apm.collector.server.Server;

/**
 * Created by zhouliang
 * 2018-02-10 19:51
 */
public interface GRPCManagerService extends Service {
    Server createIfAbsent(String host, int port);
}
