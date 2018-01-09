package com.zl.skypointer.apm.agent.core.logging.core;

/**
 * 用于向Disruptor中写入日志
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 11:21
 * @since JDK1.8
 */
public class LogMessageHolder {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
