package com.zl.skypointer.apm.collector.boot.config;

/**
 * Created by zhouliang
 * 2018-02-06 19:16
 */
public interface ConfigLoader<T> {
    T load() throws ConfigFileNotFoundException;
}
