package com.zl.skypointer.apm.collector.jetty.manager.service;

import com.zl.skypointer.apm.collector.core.module.Service;
import com.zl.skypointer.apm.collector.server.Server;
import com.zl.skypointer.apm.collector.server.ServerHandler;

/**
 * Created by zhouliang
 * 2018-02-10 19:39
 */
public interface JettyManagerService extends Service {
    Server createIfAbsent(String host, int port, String contextPath);

    void addHandler(String host, int port, ServerHandler serverHandler);
}
