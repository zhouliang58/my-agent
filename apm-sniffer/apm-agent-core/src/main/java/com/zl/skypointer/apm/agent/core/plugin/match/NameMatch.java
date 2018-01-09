package com.zl.skypointer.apm.agent.core.plugin.match;

/**
 * 通过类名匹配
 *
 * @author zhouliang
 * @version v1.0 2018/1/9 16:33
 * @since JDK1.8
 */
public class NameMatch implements ClassMatch {
    private String className;

    private NameMatch(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public static NameMatch byName(String className) {
        return new NameMatch(className);
    }
}
