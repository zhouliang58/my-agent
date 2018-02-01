package com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance;

import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import com.zl.skypointer.apm.agent.core.plugin.PluginException;
import com.zl.skypointer.apm.agent.core.plugin.loader.InterceptorInstanceLoader;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * ConstructorInter 构造方法，调用 InterceptorInstanceLoader#load(String, classLoader) 方法，加载构造方法拦截器。

 #intercept(Object) 方法，在构造方法执行完成后进行拦截，调用 InstanceConstructorInterceptor#onConstruct(...) 方法。

 为什么没有 ConstructorInterWithOverrideArgs？InstanceConstructorInterceptor#onConstruct(...) 方法，是在构造方法执行完成后进行调用拦截，
 OverrideArgs 用于在调用方法之前，改变传入方法的参数。所以，在此处暂时没这块需要，因而没有 ConstructorInterWithOverrideArgs 。
 *
 * @author zhouliang
 * @version v1.0 2018/1/11 23:59
 * @since JDK1.8
 */
public class ConstructorInter {
    private static final ILog logger = LogManager.getLogger(ConstructorInter.class);

    /**
     * An {@link InstanceConstructorInterceptor}
     * This name should only stay in {@link String}, the real {@link Class} type will trigger classloader failure.
     * If you want to know more, please check on books about Classloader or Classloader appointment mechanism.
     */
    private InstanceConstructorInterceptor interceptor;

    /**
     * @param constructorInterceptorClassName class full name.
     */
    public ConstructorInter(String constructorInterceptorClassName, ClassLoader classLoader) throws PluginException {
        try {
            interceptor = InterceptorInstanceLoader.load(constructorInterceptorClassName, classLoader);
        } catch (Throwable t) {
            throw new PluginException("Can't create InstanceConstructorInterceptor.", t);
        }
    }
    /**
     * Intercept the target constructor.
     *
     * @param obj target class instance.
     * @param allArguments all constructor arguments
     */
    @RuntimeType
    public void intercept(@This Object obj,
                          @AllArguments Object[] allArguments) {
        try {
            EnhancedInstance targetObject = (EnhancedInstance)obj;

            interceptor.onConstruct(targetObject, allArguments);
        } catch (Throwable t) {
            logger.error("ConstructorInter failure.", t);
        }

    }
}
