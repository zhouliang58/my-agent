package com.zl.skypointer.apm.agent.core.plugin;

import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import com.zl.skypointer.apm.agent.core.plugin.loader.AgentClassLoader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Use the current classloader to read all plugin define file.
 * The file must be named 'skypointer-plugin.def'
 *
 * @author zhouliang
 * @version v1.0 2018/1/9 17:09
 * @since JDK1.8
 */
public class PluginResourcesResolver {
    private static final ILog logger = LogManager.getLogger(PluginResourcesResolver.class);

    public List<URL> getResources() {
        List<URL> cfgUrlPaths = new ArrayList<URL>();
        Enumeration<URL> urls;
        try {
            urls = AgentClassLoader.getDefault().getResources("skypointer-plugin.def");

            while (urls.hasMoreElements()) {
                URL pluginUrl = urls.nextElement();
                cfgUrlPaths.add(pluginUrl);
                logger.info("find skypointer plugin define in {}", pluginUrl);
            }

            return cfgUrlPaths;
        } catch (IOException e) {
            logger.error("read resources failure.", e);
        }
        return null;
    }

    /**
     * Get the classloader.
     * First getDefault current thread's classloader,
     * if fail, getDefault {@link PluginResourcesResolver}'s classloader.
     *
     * @return the classloader to find plugin definitions.
     */
    private ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = PluginResourcesResolver.class.getClassLoader();
        }
        return cl;
    }

}

