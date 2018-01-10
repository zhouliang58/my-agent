package com.zl.skypointer.apm.agent.core.plugin.interceptor;

import com.zl.skypointer.apm.agent.core.plugin.PluginException;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/10 23:42
 * @since JDK1.8
 */
public class EnhanceException extends PluginException {
    private static final long serialVersionUID = -5197801505587706750L;

    public EnhanceException(String message) {
        super(message);
    }

    public EnhanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
