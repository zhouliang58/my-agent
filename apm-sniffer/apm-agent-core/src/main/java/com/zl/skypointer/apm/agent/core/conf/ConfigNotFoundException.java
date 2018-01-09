package com.zl.skypointer.apm.agent.core.conf;

/**
 * 通过不同的方式初始胡所有的配置
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 23:27
 * @since JDK1.8
 */
public class ConfigNotFoundException extends Exception {
    public ConfigNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigNotFoundException(String message) {
        super(message);
    }
}
