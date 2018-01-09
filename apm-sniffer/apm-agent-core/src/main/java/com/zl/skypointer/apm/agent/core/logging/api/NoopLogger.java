package com.zl.skypointer.apm.agent.core.logging.api;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 11:15
 * @since JDK1.8
 */
public enum NoopLogger implements ILog {
    INSTANCE {

    };

    @Override
    public void info(String message) {

    }

    @Override
    public void info(String format, Object... arguments) {

    }

    @Override
    public void warn(String format, Object... arguments) {

    }

    @Override
    public void error(String format, Throwable e) {

    }

    @Override
    public boolean isDebugEnable() {
        return false;
    }

    @Override
    public boolean isInfoEnable() {
        return false;
    }

    @Override
    public boolean isWarnEnable() {
        return false;
    }

    @Override
    public boolean isErrorEnable() {
        return false;
    }

    @Override
    public void debug(String format) {

    }

    @Override
    public void debug(String format, Object... arguments) {

    }

    @Override
    public void error(String format) {

    }

    @Override
    public void error(Throwable e, String format, Object... arguments) {

    }
}
