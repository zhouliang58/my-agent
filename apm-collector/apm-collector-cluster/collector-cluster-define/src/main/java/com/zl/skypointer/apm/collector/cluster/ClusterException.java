package com.zl.skypointer.apm.collector.cluster;

import com.zl.skypointer.apm.collector.core.module.CollectorException;

/**
 * Created by zhouliang
 * 2018-02-07 12:16
 */
public abstract class ClusterException extends CollectorException {

    public ClusterException(String message) {
        super(message);
    }

    public ClusterException(String message, Throwable cause) {
        super(message, cause);
    }
}
