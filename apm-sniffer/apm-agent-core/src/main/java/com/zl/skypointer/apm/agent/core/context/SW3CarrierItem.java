package com.zl.skypointer.apm.agent.core.context;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 15:37
 * @since JDK1.8
 */
public class SW3CarrierItem extends CarrierItem {
    public static final String HEADER_NAME = "sw3";
    private ContextCarrier carrier;

    public SW3CarrierItem(ContextCarrier carrier, CarrierItem next) {
        super(HEADER_NAME, carrier.serialize(), next);
        this.carrier = carrier;
    }

    @Override
    public void setHeadValue(String headValue) {
        carrier.deserialize(headValue);
    }
}