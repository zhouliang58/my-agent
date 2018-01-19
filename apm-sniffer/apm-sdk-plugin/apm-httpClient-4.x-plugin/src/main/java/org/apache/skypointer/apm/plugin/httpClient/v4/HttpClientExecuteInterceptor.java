package org.apache.skypointer.apm.plugin.httpClient.v4;

import com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;

import java.lang.reflect.Method;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/10 23:40
 * @since JDK1.8
 */
public class HttpClientExecuteInterceptor implements InstanceMethodsAroundInterceptor {
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
    }

    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        return null;
    }

    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

    }
}
