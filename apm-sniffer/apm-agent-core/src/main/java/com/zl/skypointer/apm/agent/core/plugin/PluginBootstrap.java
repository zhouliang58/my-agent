package com.zl.skypointer.apm.agent.core.plugin;

import com.zl.skypointer.apm.agent.core.boot.AgentPackageNotFoundException;
import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import com.zl.skypointer.apm.agent.core.plugin.loader.AgentClassLoader;

import java.net.URL;
import java.util.List;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 23:38
 * @since JDK1.8
 */
public class PluginBootstrap {
    private static final ILog logger = LogManager.getLogger(PluginBootstrap.class);


    /**
     * load all plugins.
     *
     * @return plugin definition list.
     */
    public List<AbstractClassEnhancePluginDefine> loadPlugins() throws AgentPackageNotFoundException {
        AgentClassLoader.initDefaultLoader();

        PluginResourcesResolver resolver = new PluginResourcesResolver();
        List<URL> resources = resolver.getResources();
        return null;
    }

}
