package com.zl.skypointer.apm.agent.core.context.trace;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 15:30
 * @since JDK1.8
 */
public class LocalSpan extends AbstractTracingSpan {

    public LocalSpan(int spanId, int parentSpanId, int operationId) {
        super(spanId, parentSpanId, operationId);
    }

    public LocalSpan(int spanId, int parentSpanId, String operationName) {
        super(spanId, parentSpanId, operationName);
    }

    @Override
    public LocalSpan tag(String key, String value) {
        super.tag(key, value);
        return this;
    }

    @Override
    public LocalSpan log(Throwable t) {
        super.log(t);
        return this;
    }

    @Override public boolean isEntry() {
        return false;
    }

    @Override public boolean isExit() {
        return false;
    }
}