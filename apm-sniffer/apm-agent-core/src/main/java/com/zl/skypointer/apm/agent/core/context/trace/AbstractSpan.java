package com.zl.skypointer.apm.agent.core.context.trace;

import com.zl.skypointer.apm.network.trace.component.Component;

import java.util.Map;

/**
 * The <code>AbstractSpan</code> represents the span's skeleton, which contains all open methods.
 *
 * @author zhouliang
 * @version v1.0 2018/1/30 19:11
 * @since JDK1.8
 */
public interface AbstractSpan {
    /**
     * Set the component id, which defines in {@link ComponentsDefine}
     *
     * @param component
     * @return the span for chaining.
     */
    AbstractSpan setComponent(Component component);

    /**
     * Only use this method in explicit instrumentation, like opentracing-skywalking-bridge. It it higher recommend
     * don't use this for performance consideration.
     *
     * @param componentName
     * @return the span for chaining.
     */
    AbstractSpan setComponent(String componentName);

    AbstractSpan setLayer(SpanLayer layer);

    /**
     * Set a key:value tag on the Span.
     *
     * @return this Span instance, for chaining
     */
    AbstractSpan tag(String key, String value);

    /**
     * Record an exception event of the current walltime timestamp.
     *
     * @param t any subclass of {@link Throwable}, which occurs in this span.
     * @return the Span, for chaining
     */
    AbstractSpan log(Throwable t);

    AbstractSpan errorOccurred();

    /**
     * @return true if the actual span is an entry span.
     */
    boolean isEntry();

    /**
     * @return true if the actual span is an exit span.
     */
    boolean isExit();

    /**
     * Record an event at a specific timestamp.
     *
     * @param timestamp The explicit timestamp for the log record.
     * @param event the events
     * @return the Span, for chaining
     */
    AbstractSpan log(long timestamp, Map<String, ?> event);

    /**
     * Sets the string name for the logical operation this span represents.
     *
     * @return this Span instance, for chaining
     */
    AbstractSpan setOperationName(String operationName);

    /**
     * Start a span.
     *
     * @return this Span instance, for chaining
     */
    AbstractSpan start();

    /**
     * Get the id of span
     * 一个整数，在 TraceSegment 内唯一，从 0 开始自增
     * @return id value.
     */
    int getSpanId();

    /**
     * 设置操作编号。考虑到操作名是字符串，Agent 发送给 Collector 占用流量较大。
     * 因此，Agent 会将操作注册到 Collector ，生成操作编号
     * @return
     */
    int getOperationId();

    String getOperationName();

    AbstractSpan setOperationId(int operationId);

    /**
     * Reference other trace segment.
     *
     * @param ref segment ref
     */
    void ref(TraceSegmentRef ref);
}

