package com.zl.skypointer.apm.collector.client;

import com.zl.skypointer.apm.collector.core.module.CollectorException;

/**
 * Created by zhouliang
 * 2018-02-07 12:26
 */
public abstract class ClientException extends CollectorException {
    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
