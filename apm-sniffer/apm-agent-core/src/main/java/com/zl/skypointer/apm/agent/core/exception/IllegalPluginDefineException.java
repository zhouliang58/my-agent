package com.zl.skypointer.apm.agent.core.exception;

/**
 * Thrown to indicate that a illegal format plugin definition has been defined in skywalking-plugin.define.
 *
 * @author zhouliang
 * @version v1.0 2018/1/9 23:27
 * @since JDK1.8
 */
public class IllegalPluginDefineException extends Exception {
    public IllegalPluginDefineException(String define) {
        super("Illegal plugin define : " + define);
    }
}
