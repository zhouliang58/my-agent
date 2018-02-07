package com.zl.skypointer.apm.collector.core.module;

/**
 * Created by zhouliang
 * 2018-02-07 10:18
 */
public class ModuleNotFoundRuntimeException extends RuntimeException {

    public ModuleNotFoundRuntimeException(Throwable cause) {
        super(cause);
    }

    public ModuleNotFoundRuntimeException(String message) {
        super(message);
    }
}
