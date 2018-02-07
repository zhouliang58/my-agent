package com.zl.skypointer.apm.collector.core.module;

/**
 * Created by zhouliang
 * 2018-02-07 10:11
 */
public class ServiceNotProvidedException extends Exception {
    public ServiceNotProvidedException(String message) {
        super(message);
    }
}
