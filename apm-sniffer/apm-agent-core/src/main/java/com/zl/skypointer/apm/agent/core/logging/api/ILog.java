package com.zl.skypointer.apm.agent.core.logging.api;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 11:09
 * @since JDK1.8
 */
public interface ILog {
    void info(String format);

    void info(String format, Object... arguments);

    void warn(String format, Object... arguments);

    void error(String format, Throwable e);

    void error(Throwable e, String format, Object... arguments);

    boolean isDebugEnable();

    boolean isInfoEnable();

    boolean isWarnEnable();

    boolean isErrorEnable();

    void debug(String format);

    void debug(String format, Object... arguments);

    void error(String format);
}
