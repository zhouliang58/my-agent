package com.zl.skypointer.apm.agent.core.plugin.interceptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * One of the three "Intercept Point".
 * "Intercept Point" is a definition about where and how intercept happens.
 * In this "Intercept Point", the definition targets class's static methods, and the interceptor.
 * <p>
 * ref to two others: {@link ConstructorInterceptPoint} and {@link InstanceMethodsInterceptPoint}
 *
 * @author zhouliang
 * @version v1.0 2018/1/11 23:52
 * @since JDK1.8
 */
public interface StaticMethodsInterceptPoint {
    /**
     * static methods matcher.
     *
     * @return matcher instance.
     */
    ElementMatcher<MethodDescription> getMethodsMatcher();

    /**
     * @return represents a class name, the class instance must instanceof StaticMethodsAroundInterceptor.
     */
    String getMethodsInterceptor();

    boolean isOverrideArgs();
}
