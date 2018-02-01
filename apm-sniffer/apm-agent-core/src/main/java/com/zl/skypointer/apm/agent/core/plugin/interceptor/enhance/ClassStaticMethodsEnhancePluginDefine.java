package com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance;

import com.zl.skypointer.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;

/**
 * 类增强静态方法的插件定义抽象类
 * 实现 #getConstructorsInterceptPoints() / #getInstanceMethodsInterceptPoints() 抽象方法，返回空，表示不增强构造方法和实例方法。即只增强静态方法。
 *
 * @author zhouliang
 * @version v1.0 2018/1/30 16:07
 * @since JDK1.8
 */
public abstract class ClassStaticMethodsEnhancePluginDefine extends ClassEnhancePluginDefine {

    /**
     * @return null, means enhance no constructors.
     */
    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return null;
    }

    /**
     * @return null, means enhance no instance methods.
     */
    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return null;
    }
}

