package com.zl.skypointer.apm.collector.core.module;

/**
 * Created by zhouliang
 * 2018-02-07 10:16
 */
public class ServiceNotProvidedRuntimeException extends RuntimeException {
    public ServiceNotProvidedRuntimeException(String message) {
        super(message);
    }
}
