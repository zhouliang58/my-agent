package com.zl.skypointer.apm.agent.core.context.ids;

/**
 * 传播的分布式链路追踪编号。例如，A 服务调用 B 服务时，A 服务会将 DistributedTraceId 对象带给 B 服务，
 * B 服务会调用 PropagatedTraceId(String id) 构造方法 ，创建 PropagatedTraceId 对象。
 * 该构造方法内部会解析 id ，生成 ID 对象。
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 10:43
 * @since JDK1.8
 */
public class PropagatedTraceId extends DistributedTraceId {
    public PropagatedTraceId(String id) {
        super(id);
    }
}
