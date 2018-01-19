package com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance;

/**
 * The instance constructor's interceptor interface.
 * Any plugin, which wants to intercept constructor, must implement this interface.
 *
 * @author zhouliang
 * @version v1.0 2018/1/11 23:50
 * @since JDK1.8
 */
public interface InstanceConstructorInterceptor {
    /**
     * Called before the origin constructor invocation.
     */
    void onConstruct(EnhancedInstance objInst, Object[] allArguments);
}
