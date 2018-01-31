package com.zl.skypointer.apm.agent.core.context.trace;

import com.zl.skypointer.apm.agent.core.dictionary.DictionaryUtil;
import com.zl.skypointer.apm.network.proto.SpanObject;
import com.zl.skypointer.apm.network.trace.component.Component;

/**
 * 继承 StackBasedTracingSpan 抽象类，出口 Span ，用于服务消费者( Service Consumer ) ，例如 HttpClient 、MongoDBClient 。
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 15:28
 * @since JDK1.8
 */
public class ExitSpan extends StackBasedTracingSpan implements WithPeerInfo {
    private String peer;
    private int peerId;

    public ExitSpan(int spanId, int parentSpanId, String operationName, String peer) {
        super(spanId, parentSpanId, operationName);
        this.peer = peer;
        this.peerId = DictionaryUtil.nullValue();
    }

    public ExitSpan(int spanId, int parentSpanId, int operationId, int peerId) {
        super(spanId, parentSpanId, operationId);
        this.peer = null;
        this.peerId = peerId;
    }

    public ExitSpan(int spanId, int parentSpanId, int operationId, String peer) {
        super(spanId, parentSpanId, operationId);
        this.peer = peer;
        this.peerId = DictionaryUtil.nullValue();
    }

    public ExitSpan(int spanId, int parentSpanId, String operationName, int peerId) {
        super(spanId, parentSpanId, operationName);
        this.peer = null;
        this.peerId = peerId;
    }

    /**
     * Set the {@link #startTime}, when the first start, which means the first service provided.
     */
    @Override
    public ExitSpan start() {
        if (++stackDepth == 1) {
            super.start();
        }
        return this;
    }

    @Override
    public ExitSpan tag(String key, String value) {
        if (stackDepth == 1) {
            super.tag(key, value);
        }
        return this;
    }

    @Override
    public AbstractTracingSpan setLayer(SpanLayer layer) {
        if (stackDepth == 1) {
            return super.setLayer(layer);
        } else {
            return this;
        }
    }

    @Override
    public AbstractTracingSpan setComponent(Component component) {
        if (stackDepth == 1) {
            return super.setComponent(component);
        } else {
            return this;
        }
    }

    @Override
    public AbstractTracingSpan setComponent(String componentName) {
        if (stackDepth == 1) {
            return super.setComponent(componentName);
        } else {
            return this;
        }
    }

    @Override
    public ExitSpan log(Throwable t) {
        if (stackDepth == 1) {
            super.log(t);
        }
        return this;
    }

    @Override public SpanObject.Builder transform() {
        SpanObject.Builder spanBuilder = super.transform();
        if (peerId != DictionaryUtil.nullValue()) {
            spanBuilder.setPeerId(peerId);
        } else {
            if (peer != null) {
                spanBuilder.setPeer(peer);
            }
        }
        return spanBuilder;
    }

    @Override
    public AbstractTracingSpan setOperationName(String operationName) {
        if (stackDepth == 1) {
            return super.setOperationName(operationName);
        } else {
            return this;
        }
    }

    @Override
    public AbstractTracingSpan setOperationId(int operationId) {
        if (stackDepth == 1) {
            return super.setOperationId(operationId);
        } else {
            return this;
        }
    }

    @Override
    public int getPeerId() {
        return peerId;
    }

    @Override
    public String getPeer() {
        return peer;
    }

    @Override public boolean isEntry() {
        return false;
    }

    @Override public boolean isExit() {
        return true;
    }
}