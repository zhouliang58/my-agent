package com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance;

import java.lang.reflect.Method;

import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import com.zl.skypointer.apm.agent.core.plugin.loader.InterceptorInstanceLoader;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/12 0:06
 * @since JDK1.8
 */
public class StaticMethodsInterWithOverrideArgs {
    private static final ILog logger = LogManager.getLogger(StaticMethodsInterWithOverrideArgs.class);

    /**
     * A class full name, and instanceof {@link StaticMethodsAroundInterceptor}
     * This name should only stay in {@link String}, the real {@link Class} type will trigger classloader failure.
     * If you want to know more, please check on books about Classloader or Classloader appointment mechanism.
     */
    private String staticMethodsAroundInterceptorClassName;

    /**
     * Set the name of {@link StaticMethodsInterWithOverrideArgs#staticMethodsAroundInterceptorClassName}
     *
     * @param staticMethodsAroundInterceptorClassName class full name.
     */
    public StaticMethodsInterWithOverrideArgs(String staticMethodsAroundInterceptorClassName) {
        this.staticMethodsAroundInterceptorClassName = staticMethodsAroundInterceptorClassName;
    }

    /**
     * Intercept the target static method.
     *
     * @param clazz target class
     * @param allArguments all method arguments
     * @param method method description.
     * @param zuper the origin call ref.
     * @return the return value of target static method.
     * @throws Exception only throw exception because of zuper.call() or unexpected exception in sky-walking ( This is a
     * bug, if anything triggers this condition ).
     */
    @RuntimeType
    public Object intercept(@Origin Class<?> clazz, @AllArguments Object[] allArguments, @Origin Method method,
                            @Morph OverrideCallable zuper) throws Throwable {
        StaticMethodsAroundInterceptor interceptor = InterceptorInstanceLoader
                .load(staticMethodsAroundInterceptorClassName, clazz.getClassLoader());

        MethodInterceptResult result = new MethodInterceptResult();
        try {
            interceptor.beforeMethod(clazz, method, allArguments, method.getParameterTypes(), result);
        } catch (Throwable t) {
            logger.error(t, "class[{}] before static method[{}] intercept failure", clazz, method.getName());
        }

        Object ret = null;
        try {
            if (!result.isContinue()) {
                ret = result._ret();
            } else {
                ret = zuper.call(allArguments);
            }
        } catch (Throwable t) {
            try {
                interceptor.handleMethodException(clazz, method, allArguments, method.getParameterTypes(), t);
            } catch (Throwable t2) {
                logger.error(t2, "class[{}] handle static method[{}] exception failure", clazz, method.getName(), t2.getMessage());
            }
            throw t;
        } finally {
            try {
                ret = interceptor.afterMethod(clazz, method, allArguments, method.getParameterTypes(), ret);
            } catch (Throwable t) {
                logger.error(t, "class[{}] after static method[{}] intercept failure:{}", clazz, method.getName(), t.getMessage());
            }
        }
        return ret;
    }
}
