package com.zl.skypointer.apm.collector.core;

/**
 * Created by zhouliang
 * 2018-02-08 16:35
 */
public class UnexpectedException extends RuntimeException {
    public UnexpectedException(String message) {
        super(message);
    }
}
