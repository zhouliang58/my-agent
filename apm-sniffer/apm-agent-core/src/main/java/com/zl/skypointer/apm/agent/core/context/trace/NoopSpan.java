


package com.zl.skypointer.apm.agent.core.context.trace;



import com.zl.skypointer.apm.network.trace.component.Component;

import java.util.Map;

/**
 * The <code>NoopSpan</code> represents a span implementation without any actual operation.
 * This span implementation is for {@link IgnoredTracerContext},
 * for keeping the memory and gc cost as low as possible.
 *
 * @author wusheng
 */
public class NoopSpan implements AbstractSpan {
    public NoopSpan() {
    }

    @Override
    public AbstractSpan log(Throwable t) {
        return this;
    }

    @Override public AbstractSpan errorOccurred() {
        return this;
    }

    public void finish() {

    }

    @Override public AbstractSpan setComponent(Component component) {
        return this;
    }

    @Override public AbstractSpan setComponent(String componentName) {
        return this;
    }

    @Override public AbstractSpan setLayer(SpanLayer layer) {
        return this;
    }

    @Override
    public AbstractSpan tag(String key, String value) {
        return this;
    }

    @Override public boolean isEntry() {
        return false;
    }

    @Override public boolean isExit() {
        return false;
    }

    @Override public AbstractSpan log(long timestamp, Map<String, ?> event) {
        return this;
    }

    @Override public AbstractSpan setOperationName(String operationName) {
        return this;
    }

    @Override public AbstractSpan start() {
        return this;
    }

    @Override public int getSpanId() {
        return 0;
    }

    @Override public int getOperationId() {
        return 0;
    }

    @Override public String getOperationName() {
        return "";
    }

    @Override public AbstractSpan setOperationId(int operationId) {
        return this;
    }

    @Override public void ref(TraceSegmentRef ref) {
    }
}
