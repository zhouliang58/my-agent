package com.zl.skypointer.apm.agent.core.context.ids;

/**
 * 新建的分布式链路追踪编号。当全局链路追踪开始，创建 TraceSegment 对象的过程中，
 * 会调用 DistributedTraceId() 构造方法，创建 DistributedTraceId 对象。
 * 该构造方法内部会调用 GlobalIdGenerator#generate() 方法，创建 ID 对象。
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 10:39
 * @since JDK1.8
 */
public class NewDistributedTraceId extends DistributedTraceId {
    public NewDistributedTraceId() {
        super(GlobalIdGenerator.generate());
    }
}