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

        new AgentBuilder.Default().type(pluginFinder.buildMatch()).transform(new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
                List<AbstractClassEnhancePluginDefine> pluginDefines = pluginFinder.find(typeDescription, classLoader);
                if (pluginDefines.size() > 0) {
                    DynamicType.Builder<?> newBuilder = builder;
                    EnhanceContext context = new EnhanceContext();
                    for (AbstractClassEnhancePluginDefine define : pluginDefines) {
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
