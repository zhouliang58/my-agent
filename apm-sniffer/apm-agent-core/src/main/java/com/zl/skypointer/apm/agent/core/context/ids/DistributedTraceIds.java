package com.zl.skypointer.apm.agent.core.context.ids;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 10:44
 * @since JDK1.8
 */
public class DistributedTraceIds {
    private LinkedList<DistributedTraceId> relatedGlobalTraces;

    public DistributedTraceIds() {
        relatedGlobalTraces = new LinkedList<DistributedTraceId>();
    }

    public List<DistributedTraceId> getRelatedGlobalTraces() {
        return Collections.unmodifiableList(relatedGlobalTraces);
    }

    /**
     * 添加分布式链路追踪编号
     * @param distributedTraceId 分布式链路追踪编号
     */
    public void append(DistributedTraceId distributedTraceId) {
        /**
         * 移除首个 NewDistributedTraceId 对象。为什么呢？在 「2.4 TraceSegment」 的构造方法中，会默认创建 NewDistributedTraceId 对象。
         * 在跨线程、或者跨进程的情况下时，创建的 TraceSegment 对象，需要指向父 Segment 的 DistributedTraceId ，所以需要移除默认创建的。
         */
        if (relatedGlobalTraces.size() > 0 && relatedGlobalTraces.getFirst() instanceof NewDistributedTraceId) {
            relatedGlobalTraces.removeFirst();
        }
        if (!relatedGlobalTraces.contains(distributedTraceId)) {
            relatedGlobalTraces.add(distributedTraceId);
        }
    }
}
