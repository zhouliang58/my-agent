package com.zl.skypointer.apm.agent.core.context;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 15:48
 * @since JDK1.8
 */
public class TraceContextCarrierItem extends CarrierItem {
    private static final String HEAD_NAME = "Trace-Context";

    public TraceContextCarrierItem(String headValue, CarrierItem next) {
        super(HEAD_NAME, headValue, next);
    }
}