package com.zl.skypointer.apm.agent.core.plugin;

import com.zl.skypointer.apm.agent.core.boot.AgentPackageNotFoundException;
import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import com.zl.skypointer.apm.agent.core.plugin.loader.AgentClassLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Plugins finder.
 * Use {@link PluginResourcesResolver} to find all plugins,
 * and ask {@link PluginCfg} to load all plugin definitions.
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

        if (resources == null || resources.size() == 0) {
            logger.info("no plugin files (skywalking-plugin.def) found, continue to start application.");
            return new ArrayList<AbstractClassEnhancePluginDefine>();
        }

        /**
         * 将插件中的skywalking-plugin.def实例化成PluginDefine对象
         */
        for (URL pluginUrl : resources) {
            try {
                PluginCfg.INSTANCE.load(pluginUrl.openStream());
            } catch (Throwable t) {
                logger.error(t, "plugin file [{}] init failure.", pluginUrl);
            }
        }

        List<PluginDefine> pluginClassList = PluginCfg.INSTANCE.getPluginClassList();


        List<AbstractClassEnhancePluginDefine> plugins = new ArrayList<AbstractClassEnhancePluginDefine>();
        for (PluginDefine pluginDefine : pluginClassList) {
            try {
                logger.debug("loading plugin class {}.", pluginDefine.getDefineClass());
                /**
                 * Class.forName 实例化插件增强类
                 * pluginDefine.getDefineClass() 指定类名  org.apache.skypointer.apm.plugin.httpClient.v4.define.HttpClientInstrumentation
                 * AgentClassLoader.getDefault() 指定类加载器
                 */
                AbstractClassEnhancePluginDefine plugin =
                        (AbstractClassEnhancePluginDefine)Class.forName(pluginDefine.getDefineClass(),
                                true,
                                AgentClassLoader.getDefault())
                                .newInstance();
                plugins.add(plugin);
            } catch (Throwable t) {
                logger.error(t, "load plugin [{}] failure.", pluginDefine.getDefineClass());
            }
        }

        return plugins;
    }

}
