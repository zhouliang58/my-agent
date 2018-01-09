package com.zl.skypointer.apm.agent.core.plugin.bytebuddy;

import net.bytebuddy.matcher.ElementMatcher;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/9 23:44
 * @since JDK1.8
 */
public abstract class AbstractJunction<V> implements ElementMatcher.Junction<V> {
    @Override
    public <U extends V> Junction<U> and(ElementMatcher<? super U> other) {
        return new Conjunction<U>(this, other);
    }

    @Override
    public <U extends V> Junction<U> or(ElementMatcher<? super U> other) {
        return new Disjunction<U>(this, other);
    }
}
