package com.zl.skypointer.apm.collector.core.module;

/**
 * Created by zhouliang
 * 2018-02-06 19:06
 */
public class CollectorException extends Exception {

    public CollectorException(String message) {
        super(message);
    }

    public CollectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
