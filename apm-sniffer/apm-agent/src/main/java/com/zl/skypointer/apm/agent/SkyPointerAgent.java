package com.zl.skypointer.apm.agent;

import com.zl.skypointer.apm.agent.core.conf.SnifferConfigInitializer;
import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import com.zl.skypointer.apm.agent.core.plugin.PluginBootstrap;
import com.zl.skypointer.apm.agent.core.plugin.PluginFinder;

import java.lang.instrument.Instrumentation;

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
        final PluginFinder plunginFiner;
        try {
            SnifferConfigInitializer.initialize();

            pluginFinder = new PluginFinder(new PluginBootstrap().loadPlugins());

        } catch (Exception e) {
            logger.error(e, "Skywalking agent initialized failure. Shutting down.");
            return;
        }
    }
}
