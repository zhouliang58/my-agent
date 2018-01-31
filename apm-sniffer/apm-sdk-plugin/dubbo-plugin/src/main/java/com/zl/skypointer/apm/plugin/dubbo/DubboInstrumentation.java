package com.zl.skypointer.apm.plugin.dubbo;

import com.zl.skypointer.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import com.zl.skypointer.apm.agent.core.plugin.match.ClassMatch;
import com.zl.skypointer.apm.agent.core.plugin.match.NameMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/30 16:02
 * @since JDK1.8
 */
public class DubboInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "com.alibaba.dubbo.monitor.support.MonitorFilter";
    private static final String INTERCEPT_CLASS = "org.apache.skywalking.apm.plugin.dubbo.DubboInterceptor";

    /**
     * 指定拦截的类
     * @return
     */
    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }

    @Override
    protected ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return null;
    }

    @Override
    protected InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[] {
                new InstanceMethodsInterceptPoint() {
                    /**
                     * 指定拦截的方法名
                     * @return
                     */
                    @Override
                    public ElementMatcher<MethodDescription> getMethodsMatcher() {
                        return named("invoke");
                    }

                    /**
                     * 指定拦截器
                     * @return
                     */
                    @Override
                    public String getMethodsInterceptor() {
                        return INTERCEPT_CLASS;
                    }

                    @Override
                    public boolean isOverrideArgs() {
                        return false;
                    }
                }
        };
    }
}
