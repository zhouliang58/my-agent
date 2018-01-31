package com.zl.skypointer.apm.agent.core.context.trace;

import com.zl.skypointer.apm.agent.core.conf.RemoteDownstreamConfig;
import com.zl.skypointer.apm.agent.core.context.ContextCarrier;
import com.zl.skypointer.apm.agent.core.context.ContextSnapshot;
import com.zl.skypointer.apm.agent.core.context.ids.ID;
import com.zl.skypointer.apm.agent.core.dictionary.DictionaryUtil;
import com.zl.skypointer.apm.network.proto.RefType;
import com.zl.skypointer.apm.network.proto.TraceSegmentReference;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 11:10
 * @since JDK1.8
 */
public class TraceSegmentRef {
    private SegmentRefType type;

    private ID traceSegmentId;

    private int spanId = -1;

    private int peerId = DictionaryUtil.nullValue();

    private String peerHost;

    private int entryApplicationInstanceId = DictionaryUtil.nullValue();

    private int parentApplicationInstanceId = DictionaryUtil.nullValue();

    private String entryOperationName;

    private int entryOperationId = DictionaryUtil.nullValue();

    private String parentOperationName;

    private int parentOperationId = DictionaryUtil.nullValue();

    /**
     * Transform a {@link ContextCarrier} to the <code>TraceSegmentRef</code>
     *
     * @param carrier the valid cross-process propagation format.
     */
    public TraceSegmentRef(ContextCarrier carrier) {
        this.type = SegmentRefType.CROSS_PROCESS;
        this.traceSegmentId = carrier.getTraceSegmentId();
        this.spanId = carrier.getSpanId();
        this.parentApplicationInstanceId = carrier.getParentApplicationInstanceId();
        this.entryApplicationInstanceId = carrier.getEntryApplicationInstanceId();
        String host = carrier.getPeerHost();
        if (host.charAt(0) == '#') {
            this.peerHost = host.substring(1);
        } else {
            this.peerId = Integer.parseInt(host);
        }
        String entryOperationName = carrier.getEntryOperationName();
        if (entryOperationName.charAt(0) == '#') {
            this.entryOperationName = entryOperationName.substring(1);
        } else {
            this.entryOperationId = Integer.parseInt(entryOperationName);
        }
        String parentOperationName = carrier.getParentOperationName();
        if (parentOperationName.charAt(0) == '#') {
            this.parentOperationName = parentOperationName.substring(1);
        } else {
            this.parentOperationId = Integer.parseInt(parentOperationName);
        }
    }

    public TraceSegmentRef(ContextSnapshot snapshot) {
        this.type = SegmentRefType.CROSS_THREAD;
        this.traceSegmentId = snapshot.getTraceSegmentId();
        this.spanId = snapshot.getSpanId();
        this.parentApplicationInstanceId = RemoteDownstreamConfig.Agent.APPLICATION_INSTANCE_ID;
        this.entryApplicationInstanceId = snapshot.getEntryApplicationInstanceId();
        String entryOperationName = snapshot.getEntryOperationName();
        if (entryOperationName.charAt(0) == '#') {
            this.entryOperationName = entryOperationName.substring(1);
        } else {
            this.entryOperationId = Integer.parseInt(entryOperationName);
        }
        String parentOperationName = snapshot.getParentOperationName();
        if (parentOperationName.charAt(0) == '#') {
            this.parentOperationName = parentOperationName.substring(1);
        } else {
            this.parentOperationId = Integer.parseInt(parentOperationName);
        }
    }

    public String getEntryOperationName() {
        return entryOperationName;
    }

    public int getEntryOperationId() {
        return entryOperationId;
    }

    public int getEntryApplicationInstanceId() {
        return entryApplicationInstanceId;
    }

    public TraceSegmentReference transform() {
        TraceSegmentReference.Builder refBuilder = TraceSegmentReference.newBuilder();
        if (SegmentRefType.CROSS_PROCESS.equals(type)) {
            refBuilder.setRefType(RefType.CrossProcess);
            refBuilder.setParentApplicationInstanceId(parentApplicationInstanceId);
            if (peerId == DictionaryUtil.nullValue()) {
                refBuilder.setNetworkAddress(peerHost);
            } else {
                refBuilder.setNetworkAddressId(peerId);
            }
        } else {
            refBuilder.setRefType(RefType.CrossThread);
        }

        refBuilder.setEntryApplicationInstanceId(entryApplicationInstanceId);
        refBuilder.setParentTraceSegmentId(traceSegmentId.transform());
        refBuilder.setParentSpanId(spanId);
        if (entryOperationId == DictionaryUtil.nullValue()) {
            refBuilder.setEntryServiceName(entryOperationName);
        } else {
            refBuilder.setEntryServiceId(entryOperationId);
        }
        if (parentOperationId == DictionaryUtil.nullValue()) {
            refBuilder.setParentServiceName(parentOperationName);
        } else {
            refBuilder.setParentServiceId(parentOperationId);
        }
        return refBuilder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TraceSegmentRef ref = (TraceSegmentRef)o;

        if (spanId != ref.spanId)
            return false;
        return traceSegmentId.equals(ref.traceSegmentId);
    }

    @Override
    public int hashCode() {
        int result = traceSegmentId.hashCode();
        result = 31 * result + spanId;
        return result;
    }

    public enum SegmentRefType {
        CROSS_PROCESS,
        CROSS_THREAD
    }
}
