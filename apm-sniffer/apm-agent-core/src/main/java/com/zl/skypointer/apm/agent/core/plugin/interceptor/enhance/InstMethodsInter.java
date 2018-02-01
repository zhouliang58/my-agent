package com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import com.zl.skypointer.apm.agent.core.plugin.PluginException;
import com.zl.skypointer.apm.agent.core.plugin.loader.InterceptorInstanceLoader;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/12 0:04
 * @since JDK1.8
 */
public class InstMethodsInter {
    private static final ILog logger = LogManager.getLogger(InstMethodsInter.class);

    /**
     * An {@link InstanceMethodsAroundInterceptor}
     * This name should only stay in {@link String}, the real {@link Class} type will trigger classloader failure.
     * If you want to know more, please check on books about Classloader or Classloader appointment mechanism.
     */
    private InstanceMethodsAroundInterceptor interceptor;

    /**
     * @param instanceMethodsAroundInterceptorClassName class full name.
     */
    public InstMethodsInter(String instanceMethodsAroundInterceptorClassName, ClassLoader classLoader) {
        try {
            interceptor = InterceptorInstanceLoader.load(instanceMethodsAroundInterceptorClassName, classLoader);
        } catch (Throwable t) {
            throw new PluginException("Can't create InstanceMethodsAroundInterceptor.", t);
        }
    }

    /**
     * 此处会调用拦截器beforeMethod、afterMethod、handleMethodException方法，也会执行目标方法
     *
     * Intercept the target instance method.
     * 1、调用 InstanceMethodsAroundInterceptor#beforeMethod(...) 方法，执行在实例方法之前的逻辑。
     *      org.skywalking.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult ，方法拦截器执行结果。
     *      当调用 MethodInterceptResult#defineReturnValue(Object) 方法，设置执行结果，并标记不再继续执行。
     * 2、当 MethodInterceptResult 已经有执行结果，不再执行原有方法，直接返回结果。
     * 3、调用 Callable#call() 方法，执行原有实例方法。
     * 4、调用 InstanceMethodsAroundInterceptor#handleMethodException(...) 方法，处理异常。
     * 5、调用 InstanceMethodsAroundInterceptor#afterMethod(...) 方法，执行后置逻辑。
     *
     * @param obj target class instance.
     * @param allArguments all method arguments
     * @param method method description.
     * @param zuper the origin call ref.
     * @return the return value of target instance method.
     * @throws Exception only throw exception because of zuper.call() or unexpected exception in sky-walking ( This is a
     * bug, if anything triggers this condition ).
     */
    @RuntimeType
    public Object intercept(@This Object obj,
                            @AllArguments Object[] allArguments,
                            @SuperCall Callable<?> zuper,
                            @Origin Method method
    ) throws Throwable {
        EnhancedInstance targetObject = (EnhancedInstance)obj;

        MethodInterceptResult result = new MethodInterceptResult();
        try {
            interceptor.beforeMethod(targetObject, method, allArguments, method.getParameterTypes(),
                    result);
        } catch (Throwable t) {
            logger.error(t, "class[{}] before method[{}] intercept failure", obj.getClass(), method.getName());
        }

        Object ret = null;
        try {
            if (!result.isContinue()) {
                ret = result._ret();
            } else {
                ret = zuper.call();
            }
        } catch (Throwable t) {
            try {
                interceptor.handleMethodException(targetObject, method, allArguments, method.getParameterTypes(),
                        t);
            } catch (Throwable t2) {
                logger.error(t2, "class[{}] handle method[{}] exception failure", obj.getClass(), method.getName());
            }
            throw t;
        } finally {
            try {
                ret = interceptor.afterMethod(targetObject, method, allArguments, method.getParameterTypes(),
                        ret);
            } catch (Throwable t) {
                logger.error(t, "class[{}] after method[{}] intercept failure", obj.getClass(), method.getName());
            }
        }
        return ret;
    }
}
