package com.zl.skypointer.apm.agent.core.context.trace;

import com.zl.skypointer.apm.agent.core.conf.RemoteDownstreamConfig;
import com.zl.skypointer.apm.agent.core.context.ids.DistributedTraceId;
import com.zl.skypointer.apm.agent.core.context.ids.DistributedTraceIds;
import com.zl.skypointer.apm.agent.core.context.ids.GlobalIdGenerator;
import com.zl.skypointer.apm.agent.core.context.ids.ID;
import com.zl.skypointer.apm.agent.core.context.ids.NewDistributedTraceId;
import com.zl.skypointer.apm.network.proto.TraceSegmentObject;
import com.zl.skypointer.apm.network.proto.UpstreamSegment;

import java.util.LinkedList;
import java.util.List;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/30 19:39
 * @since JDK1.8
 */
public class TraceSegment {
    /**
     * The id of this trace segment. Every segment has its unique-global-id.
     */
    private ID traceSegmentId;

    /**
     * The refs of parent trace segments, except the primary one. For most RPC call, {@link #refs} contains only one
     * element, but if this segment is a start span of batch process, the segment faces multi parents, at this moment,
     * we use this {@link #refs} to link them.
     *
     * This field will not be serialized. Keeping this field is only for quick accessing.
     */
    private List<TraceSegmentRef> refs;

    /**
     * The spans belong to this trace segment. They all have finished. All active spans are hold and controlled by
     * "skywalking-api" com.zl.skypointer.apm.collector.core.
     */
    private List<AbstractTracingSpan> spans;

    /**
     * The <code>relatedGlobalTraces</code> represent a set of all related trace. Most time it contains only one
     * element, because only one parent {@link TraceSegment} exists, but, in batch scenario, the num becomes greater
     * than 1, also meaning multi-parents {@link TraceSegment}. <p> The difference between
     * <code>relatedGlobalTraces</code> and {@link #refs} is: {@link #refs} targets this {@link TraceSegment}'s direct
     * parent, <p> and <p> <code>relatedGlobalTraces</code> targets this {@link TraceSegment}'s related call chain, a
     * call chain contains multi {@link TraceSegment}s, only using {@link #refs} is not enough for analysis and ui.
     */
    private DistributedTraceIds relatedGlobalTraces;

    private boolean ignore = false;

    private boolean isSizeLimited = false;

    /**
     * Create a default/empty trace segment, with current time as start time, and generate a new segment id.
     */
    public TraceSegment() {
        this.traceSegmentId = GlobalIdGenerator.generate();
        this.spans = new LinkedList<AbstractTracingSpan>();
        this.relatedGlobalTraces = new DistributedTraceIds();
        this.relatedGlobalTraces.append(new NewDistributedTraceId());
    }

    /**
     * Establish the link between this segment and its parents.
     *
     * @param refSegment {@link TraceSegmentRef}
     */
    public void ref(TraceSegmentRef refSegment) {
        if (refs == null) {
            refs = new LinkedList<TraceSegmentRef>();
        }
        if (!refs.contains(refSegment)) {
            refs.add(refSegment);
        }
    }

    /**
     * Establish the line between this segment and all relative global trace ids.
     */
    public void relatedGlobalTraces(DistributedTraceId distributedTraceId) {
        relatedGlobalTraces.append(distributedTraceId);
    }

    /**
     * After {@link AbstractSpan} is finished, as be controller by "skywalking-api" com.zl.skypointer.apm.collector.core, notify the {@link
     * TraceSegment} to archive it.
     *
     * @param finishedSpan
     */
    public void archive(AbstractTracingSpan finishedSpan) {
        spans.add(finishedSpan);
    }

    /**
     * Finish this {@link TraceSegment}. <p> return this, for chaining
     */
    public TraceSegment finish(boolean isSizeLimited) {
        this.isSizeLimited = isSizeLimited;
        return this;
    }

    public ID getTraceSegmentId() {
        return traceSegmentId;
    }

    public int getApplicationId() {
        return RemoteDownstreamConfig.Agent.APPLICATION_ID;
    }

    public boolean hasRef() {
        return !(refs == null || refs.size() == 0);
    }

    public List<TraceSegmentRef> getRefs() {
        return refs;
    }

    public List<DistributedTraceId> getRelatedGlobalTraces() {
        return relatedGlobalTraces.getRelatedGlobalTraces();
    }

    public boolean isSingleSpanSegment() {
        return this.spans != null && this.spans.size() == 1;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    /**
     * This is a high CPU cost method, only called when sending to collector or test cases.
     *
     * @return the segment as GRPC service parameter
     */
    public UpstreamSegment transform() {
        UpstreamSegment.Builder upstreamBuilder = UpstreamSegment.newBuilder();
        for (DistributedTraceId distributedTraceId : getRelatedGlobalTraces()) {
            upstreamBuilder = upstreamBuilder.addGlobalTraceIds(distributedTraceId.toUniqueId());
        }
        TraceSegmentObject.Builder traceSegmentBuilder = TraceSegmentObject.newBuilder();
        /**
         * Trace Segment
         */
        traceSegmentBuilder.setTraceSegmentId(this.traceSegmentId.transform());
        // Don't serialize TraceSegmentReference

        // SpanObject
        for (AbstractTracingSpan span : this.spans) {
            traceSegmentBuilder.addSpans(span.transform());
        }
        traceSegmentBuilder.setApplicationId(RemoteDownstreamConfig.Agent.APPLICATION_ID);
        traceSegmentBuilder.setApplicationInstanceId(RemoteDownstreamConfig.Agent.APPLICATION_INSTANCE_ID);
        traceSegmentBuilder.setIsSizeLimited(this.isSizeLimited);

        upstreamBuilder.setSegment(traceSegmentBuilder.build().toByteString());
        return upstreamBuilder.build();
    }

    @Override
    public String toString() {
        return "TraceSegment{" +
                "traceSegmentId='" + traceSegmentId + '\'' +
                ", refs=" + refs +
                ", spans=" + spans +
                ", relatedGlobalTraces=" + relatedGlobalTraces +
                '}';
    }

    public int getApplicationInstanceId() {
        return RemoteDownstreamConfig.Agent.APPLICATION_INSTANCE_ID;
    }
}
