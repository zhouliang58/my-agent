package com.zl.skypointer.apm.agent.core.context.tag;

import com.zl.skypointer.apm.agent.core.context.trace.AbstractSpan;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 10:51
 * @since JDK1.8
 */
public class StringTag extends AbstractTag<String> {
    public StringTag(String tagKey) {
        super(tagKey);
    }

    @Override
    public void set(AbstractSpan span, String tagValue) {
        span.tag(key, tagValue);
    }
}

