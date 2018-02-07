package com.zl.skypointer.apm.collector.core.module;

/**
 * Created by zhouliang
 * 2018-02-07 10:19
 */
public class CycleDependencyException extends RuntimeException {
    public CycleDependencyException(String message) {
        super(message);
    }
}
