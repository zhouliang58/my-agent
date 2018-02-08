package com.zl.skypointer.apm.collector.client;

/**
 * Created by zhouliang
 * 2018-02-07 12:26
 */
public interface Client {
    void initialize() throws ClientException;

    void shutdown();
}
