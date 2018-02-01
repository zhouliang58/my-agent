package com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance;

import com.zl.skypointer.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.StaticMethodsInterceptPoint;

/**
 * 类增强构造方法和实例方法的插件定义抽象类
 * 实现 #getStaticMethodsInterceptPoints() 抽象方法，返回空，表示不增强静态方法。即只增强构造方法和实例方法。
 *
 * This class controls all enhance operations, including enhance constructors, instance methods and static methods. All
 * the enhances base on three types interceptor point: {@link ConstructorInterceptPoint}, {@link
 * InstanceMethodsInterceptPoint} and {@link StaticMethodsInterceptPoint} If plugin is going to enhance constructors,
 * instance methods, or both, {@link ClassEnhancePluginDefine} will add a field of {@link
 * Object} type.
 *
 * @author zhouliang
 * @version v1.0 2018/1/10 23:44
 * @since JDK1.8
 */
public abstract class ClassInstanceMethodsEnhancePluginDefine extends ClassEnhancePluginDefine {
    /**
     * @return null, means enhance no static methods.
     */
    @Override
    protected StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints() {
        return null;
    }

}
