package com.zl.skypointer.apm.collector.boot.config;

import com.zl.skypointer.apm.collector.core.module.CollectorException;

/**
 * Created by zhouliang
 * 2018-02-06 19:17
 */
public class ConfigFileNotFoundException extends CollectorException {

    public ConfigFileNotFoundException(String message) {
        super(message);
    }

    public ConfigFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
