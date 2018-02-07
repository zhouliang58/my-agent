package com.zl.skypointer.apm.collector.core.module;

/**
 * Created by zhouliang
 * 2018-02-07 10:14
 */
public class ProviderNotFoundException extends Exception {
    public ProviderNotFoundException(String message) {
        super(message);
    }

    public ProviderNotFoundException(Throwable e) {
        super(e);
    }
}
