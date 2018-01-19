package com.zl.skypointer.apm.agent.core.plugin.interceptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * One of the three "Intercept Point".
 * "Intercept Point" is a definition about where and how intercept happens.
 * In this "Intercept Point", the definition targets class's instance methods, and the interceptor.
 * <p>
 * ref to two others: {@link ConstructorInterceptPoint} and {@link StaticMethodsInterceptPoint}
 *
 * @author zhouliang
 * @version v1.0 2018/1/11 23:51
 * @since JDK1.8
 */
public interface InstanceMethodsInterceptPoint {
    /**
     * class instance methods matcher.
     *
     * @return methods matcher
     */
    ElementMatcher<MethodDescription> getMethodsMatcher();

    /**
     * @return represents a class name, the class instance must instanceof InstanceMethodsAroundInterceptor.
     */
    String getMethodsInterceptor();

    boolean isOverrideArgs();
}
