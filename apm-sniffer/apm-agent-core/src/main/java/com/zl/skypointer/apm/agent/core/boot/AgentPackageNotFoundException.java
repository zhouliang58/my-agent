package com.zl.skypointer.apm.agent.core.boot;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 23:13
 * @since JDK1.8
 */
public class AgentPackageNotFoundException extends Exception {
    public AgentPackageNotFoundException(String message) {
        super(message);
    }
}