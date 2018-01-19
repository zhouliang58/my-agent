package com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance;

import java.lang.reflect.Method;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/12 0:07
 * @since JDK1.8
 */
public interface StaticMethodsAroundInterceptor {
    /**
     * called before target method invocation.
     *
     * @param method
     * @param result change this result, if you want to truncate the method.
     */
    void beforeMethod(Class clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes,
                      MethodInterceptResult result);

    /**
     * called after target method invocation. Even method's invocation triggers an exception.
     *
     * @param method
     * @param ret the method's original return value.
     * @return the method's actual return value.
     */
    Object afterMethod(Class clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes, Object ret);

    /**
     * called when occur exception.
     *
     * @param method
     * @param t the exception occur.
     */
    void handleMethodException(Class clazz, Method method, Object[] allArguments, Class<?>[] parameterTypes,
                               Throwable t);
}