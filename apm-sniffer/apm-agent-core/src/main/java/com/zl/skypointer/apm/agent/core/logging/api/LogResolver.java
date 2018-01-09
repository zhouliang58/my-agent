package com.zl.skypointer.apm.agent.core.logging.api;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 11:14
 * @since JDK1.8
 */
public interface LogResolver {
    /**
     * @param clazz, the class is showed in log message.
     * @return {@link ILog} implementation.
     */
    ILog getLogger(Class<?> clazz);
}
