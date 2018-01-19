package com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/12 0:03
 * @since JDK1.8
 */
public interface OverrideCallable {
    Object call(Object[] args);
}
