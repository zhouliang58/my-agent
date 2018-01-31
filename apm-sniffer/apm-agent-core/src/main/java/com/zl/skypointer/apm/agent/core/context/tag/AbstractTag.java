package com.zl.skypointer.apm.agent.core.context.tag;

import com.zl.skypointer.apm.agent.core.context.trace.AbstractSpan;

/**
 * 这个类的用途是将标签属性设置到 Span 上，或者说，它是设置 Span 的标签的工具类
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 10:50
 * @since JDK1.8
 */
public abstract class AbstractTag<T> {
    /**
     * The key of this Tag.
     */
    protected final String key;

    public AbstractTag(String tagKey) {
        this.key = tagKey;
    }

    protected abstract void set(AbstractSpan span, T tagValue);

    /**
     * @return the key of this tag.
     */
    public String key() {
        return this.key;
    }
}
