package com.zl.skypointer.apm.agent;

import com.zl.skypointer.apm.agent.core.boot.ServiceManager;
import com.zl.skypointer.apm.agent.core.conf.SnifferConfigInitializer;
import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import com.zl.skypointer.apm.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.zl.skypointer.apm.agent.core.plugin.EnhanceContext;
import com.zl.skypointer.apm.agent.core.plugin.PluginBootstrap;
import com.zl.skypointer.apm.agent.core.plugin.PluginFinder;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * 主入口
 *
 * @author zhouliang
 * @version v1.0 2017/12/20 23:23
 * @since JDK1.8
 */
public class SkyPointerAgent {
    private static final ILog logger = LogManager.getLogger(SkyPointerAgent.class);


    public static void premain(String agentArgs, Instrumentation instrumentation) {
        final PluginFinder pluginFinder;
        try {
            SnifferConfigInitializer.initialize();

            pluginFinder = new PluginFinder(new PluginBootstrap().loadPlugins());

            ServiceManager.INSTANCE.boot();
        } catch (Exception e) {
            logger.error(e, "skypointer agent initialized failure. Shutting down.");
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override public void run() {
                ServiceManager.INSTANCE.shutdown();
            }
        }, "skywalking service shutdown thread"));

        /**
         * AgentBuilder#type(ElementMatcher) 方法，实现 net.bytebuddy.matcher.ElementMatcher 接口，设置需要拦截的类
         *
         * 调用 AgentBuilder#transform(Transformer) 方法，设置 Java 类的修改逻辑。
         */
        new AgentBuilder.Default().type(pluginFinder.buildMatch()).transform(new AgentBuilder.Transformer() {

            /**
             * 例如
             * typeDescription ——class org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
             */
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription,
                                                    ClassLoader classLoader, JavaModule module) {

                /**
                 * PluginFinder#find(TypeDescription, ClassLoader) 方法，获得匹配的 AbstractClassEnhancePluginDefine 数组。
                 */
                List<AbstractClassEnhancePluginDefine> pluginDefines = pluginFinder.find(typeDescription, classLoader);
                if (pluginDefines.size() > 0) {
                    DynamicType.Builder<?> newBuilder = builder;
                    EnhanceContext context = new EnhanceContext();
                    for (AbstractClassEnhancePluginDefine define : pluginDefines) {
                        /**
                         * 调用 AbstractClassEnhancePluginDefine#define(...) 方法，设置 net.bytebuddy.dynamic.DynamicType.Builder 对象。
                         * 通过该对象，定义如何拦截需要修改的 Java 类。
                         * 在 AbstractClassEnhancePluginDefine#define(...) 方法的内部，
                         * 会调用 net.bytebuddy.dynamic.DynamicType.ImplementationDefinition#intercept(Implementation)
                         */
                        DynamicType.Builder<?> possibleNewBuilder = define.define(typeDescription.getTypeName(), newBuilder, classLoader, context);
                        if (possibleNewBuilder != null) {
                            newBuilder = possibleNewBuilder;
                        }
                    }
                    if (context.isEnhanced()) {
                        logger.debug("Finish the prepare stage for {}.", typeDescription.getName());
                    }

                    return newBuilder;
                }

                logger.debug("Matched class {}, but ignore by finding mechanism.", typeDescription.getTypeName());
                return builder;
            }
        }).with(new AgentBuilder.Listener() {
            @Override
            public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

            }

            /**
             * 当 Java 类的修改成功，进行调用。
             *
             */
            @Override
            public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
                                         boolean loaded, DynamicType dynamicType) {
                if (logger.isDebugEnable()) {
                    logger.debug("On Transformation class {}.", typeDescription.getName());
                }

                InstrumentDebuggingClass.INSTANCE.log(typeDescription, dynamicType);
            }

            @Override
            public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module,
                                  boolean loaded) {

            }

            /**
             * 当 Java 类的修改失败，进行调用。
             */
            @Override public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded,
                                          Throwable throwable) {
                logger.error("Enhance class " + typeName + " error.", throwable);
            }

            @Override
            public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
            }
        }).installOn(instrumentation);
    }
}
