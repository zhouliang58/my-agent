package com.zl.skypointer.apm.agent.core.plugin;

import com.zl.skypointer.apm.agent.core.exception.IllegalPluginDefineException;
import com.zl.skypointr.apm.util.StringUtil;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/9 17:47
 * @since JDK1.8
 */
public class PluginDefine {
    /**
     * Plugin name.
     * eg:  dubbo
     */
    private String name;

    /**
     * The class name of plugin defined.
     * eg : org.apache.skywalking.apm.plugin.dubbo.DubboInstrumentation
     */
    private String defineClass;

    private PluginDefine(String name, String defineClass) {
        this.name = name;
        this.defineClass = defineClass;
    }

    // dubbo=org.apache.skywalking.apm.plugin.dubbo.DubboInstrumentation
    public static PluginDefine build(String define) throws IllegalPluginDefineException {
        if (StringUtil.isEmpty(define)) {
            throw new IllegalPluginDefineException(define);
        }

        String[] pluginDefine = define.split("=");
        if (pluginDefine.length != 2) {
            throw new IllegalPluginDefineException(define);
        }
        // dubbo
        String pluginName = pluginDefine[0];
        // org.apache.skywalking.apm.plugin.dubbo.DubboInstrumentation
        String defineClass = pluginDefine[1];
        return new PluginDefine(pluginName, defineClass);
    }

    public String getDefineClass() {
        return defineClass;
    }
}
