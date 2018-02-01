package com.zl.skypointer.apm.plugin.dubbo;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.zl.skypointer.apm.agent.core.context.CarrierItem;
import com.zl.skypointer.apm.agent.core.context.ContextCarrier;
import com.zl.skypointer.apm.agent.core.context.ContextManager;
import com.zl.skypointer.apm.agent.core.context.tag.Tags;
import com.zl.skypointer.apm.agent.core.context.trace.AbstractSpan;
import com.zl.skypointer.apm.agent.core.context.trace.SpanLayer;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import com.zl.skypointer.apm.network.trace.component.ComponentsDefine;

import java.lang.reflect.Method;

/**
 * 定义如何加强com.alibaba.dubbo.monitor.support.MonitorFilter中的invoke方法
 * trace的运输通过
 * {@link DubboInterceptor} define how to enhance class {@link com.alibaba.dubbo.monitor.support.MonitorFilter#invoke(Invoker,
 * Invocation)}. the trace context transport to the provider side by {@link RpcContext#attachments}.but all the version
 * of dubbo framework below 2.8.3 don't support {@link RpcContext#attachments}, we support another way to support it.
 *
 * @author zhouliang
 * @version v1.0 2018/1/30 16:15
 * @since JDK1.8
 */
public class DubboInterceptor implements InstanceMethodsAroundInterceptor {

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
                             Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {
        Invoker invoker = (Invoker)allArguments[0];
        Invocation invocation = (Invocation)allArguments[1];
        RpcContext rpcContext = RpcContext.getContext();
        boolean isConsumer = rpcContext.isConsumerSide();
        URL requestURL = invoker.getUrl();

        AbstractSpan span;

        final String host = requestURL.getHost();
        final int port = requestURL.getPort();
        if (isConsumer) {
            final ContextCarrier contextCarrier = new ContextCarrier();
            /**
             * 创建 ExitSpan 对象，并将链路追踪上下文注入到 ContextCarrier 对象，用于跨进程的链路追踪。
             * 其中，调用 #generateOperationName(URL, Invocation) 方法，生成操作名，
             * 例如："org.skywalking.apm.plugin.test.Test.test(String)" 。
             */
            span = ContextManager.createExitSpan(generateOperationName(requestURL, invocation), contextCarrier, host + ":" + port);
            //invocation.getAttachments().put("contextData", contextDataStr);
            //@see https://github.com/alibaba/dubbo/blob/dubbo-2.5.3/dubbo-rpc/dubbo-rpc-api/src/main/java/com/alibaba/dubbo/rpc/RpcInvocation.java#L154-L161
            CarrierItem next = contextCarrier.items();
            while (next.hasNext()) {
                next = next.next();
                /**
                 * 设置 ContextCarrier 对象到 RPCContext ，从而将 ContextCarrier 对象隐式传参
                 */
                rpcContext.getAttachments().put(next.getHeadKey(), next.getHeadValue());
            }
        } else {
            /**
             * 解析 ContextCarrier 对象，用于跨进程的链路追踪。
             */
            ContextCarrier contextCarrier = new ContextCarrier();
            CarrierItem next = contextCarrier.items();
            while (next.hasNext()) {
                next = next.next();
                next.setHeadValue(rpcContext.getAttachment(next.getHeadKey()));
            }

            /**
             * 创建 EntrySpan 对象。
             */
            span = ContextManager.createEntrySpan(generateOperationName(requestURL, invocation), contextCarrier);
        }
        /**
         * 设置 EntrySpan 对象的 url 标签键值对。
         * 其中，调用 #generateRequestURL(URL, Invocation) 方法，生成链接。
         * 例如，"dubbo://127.0.0.1:20880/org.skywalking.apm.plugin.test.Test.test(String)"。
         */
        Tags.URL.set(span, generateRequestURL(requestURL, invocation));
        /**
         * 设置 EntrySpan 对象的组件类型。
         */
        span.setComponent(ComponentsDefine.DUBBO);
        /**
         * 设置 EntrySpan 对象的分层。
         */
        SpanLayer.asRPCFramework(span);
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
                              Class<?>[] argumentsTypes, Object ret) throws Throwable {
        /**
         * 当返回结果包含异常时，调用 #dealException(Throwable) 方法，处理异常。
         */
        Result result = (Result)ret;
        if (result != null && result.getException() != null) {
            dealException(result.getException());
        }

        /**
         * 完成 EntrySpan 对象
         */
        ContextManager.stopSpan();
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
                                      Class<?>[] argumentsTypes, Throwable t) {
        dealException(t);
    }

    /**
     * Log the throwable, which occurs in Dubbo RPC service.
     */
    private void dealException(Throwable throwable) {
        AbstractSpan span = ContextManager.activeSpan();
        span.errorOccurred();
        span.log(throwable);
    }

    /**
     * Format operation name. e.g. org.apache.skywalking.apm.plugin.test.Test.test(String)
     *
     * @return operation name.
     */
    private String generateOperationName(URL requestURL, Invocation invocation) {
        StringBuilder operationName = new StringBuilder();
        operationName.append(requestURL.getPath());
        operationName.append("." + invocation.getMethodName() + "(");
        for (Class<?> classes : invocation.getParameterTypes()) {
            operationName.append(classes.getSimpleName() + ",");
        }

        if (invocation.getParameterTypes().length > 0) {
            operationName.delete(operationName.length() - 1, operationName.length());
        }

        operationName.append(")");

        return operationName.toString();
    }

    /**
     * Format request url.
     * e.g. dubbo://127.0.0.1:20880/org.apache.skywalking.apm.plugin.test.Test.test(String).
     *
     * @return request url.
     */
    private String generateRequestURL(URL url, Invocation invocation) {
        StringBuilder requestURL = new StringBuilder();
        requestURL.append(url.getProtocol() + "://");
        requestURL.append(url.getHost());
        requestURL.append(":" + url.getPort() + "/");
        requestURL.append(generateOperationName(url, invocation));
        return requestURL.toString();
    }
}
