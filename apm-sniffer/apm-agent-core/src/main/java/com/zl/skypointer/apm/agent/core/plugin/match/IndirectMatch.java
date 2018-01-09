package com.zl.skypointer.apm.agent.core.plugin.match;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * All implementations can't direct match the class like {@link NameMatch} did.
 *
 * @author zhouliang
 * @version v1.0 2018/1/9 23:42
 * @since JDK1.8
 */
public interface IndirectMatch extends ClassMatch {
    ElementMatcher.Junction buildJunction();

    boolean isMatch(TypeDescription typeDescription);
}
