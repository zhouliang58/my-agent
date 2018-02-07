package com.zl.skypointer.apm.collector.core.module;

/**
 * Created by zhouliang
 * 2018-02-07 10:17
 */
public class ModuleNotFoundException extends Exception {

    public ModuleNotFoundException(Throwable cause) {
        super(cause);
    }

    public ModuleNotFoundException(String message) {
        super(message);
    }
}
