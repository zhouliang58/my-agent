package com.zl.skypointer.apm.collector.naming.service;

import com.zl.skypointer.apm.collector.core.module.Service;
import com.zl.skypointer.apm.collector.server.ServerHandler;

/**
 * Created by zhouliang
 * 2018-02-10 20:01
 */
public interface NamingHandlerRegisterService extends Service {
    void register(ServerHandler namingHandler);
}
