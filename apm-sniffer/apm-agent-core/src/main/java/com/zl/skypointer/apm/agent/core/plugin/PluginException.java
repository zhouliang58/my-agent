package com.zl.skypointer.apm.agent.core.plugin;

import java.io.Serializable;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/9 16:39
 * @since JDK1.8
 */
public class PluginException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -5708895068823904353L;

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
