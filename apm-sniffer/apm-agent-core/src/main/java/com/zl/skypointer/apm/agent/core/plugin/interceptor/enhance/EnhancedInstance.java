package com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/11 23:31
 * @since JDK1.8
 */
public interface EnhancedInstance {
    Object getSkyWalkingDynamicField();

    void setSkyWalkingDynamicField(Object value);
}
