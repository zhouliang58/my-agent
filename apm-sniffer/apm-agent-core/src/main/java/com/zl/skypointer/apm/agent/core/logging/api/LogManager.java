package com.zl.skypointer.apm.agent.core.logging.api;

import com.zl.skypointer.apm.agent.core.logging.core.EasyLogResolver;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 11:10
 * @since JDK1.8
 */
public class LogManager {
    private static LogResolver RESOLVER = new EasyLogResolver();

    public static void setLogResolver(LogResolver resolver) {
        LogManager.RESOLVER = resolver;
    }

    public static ILog getLogger(Class<?> clazz) {
        if (RESOLVER == null) {
            return NoopLogger.INSTANCE;
        }
        return LogManager.RESOLVER.getLogger(clazz);
    }
}
