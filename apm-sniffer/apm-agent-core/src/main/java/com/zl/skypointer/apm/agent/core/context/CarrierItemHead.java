package com.zl.skypointer.apm.agent.core.context;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 15:37
 * @since JDK1.8
 */
public class CarrierItemHead extends CarrierItem {
    public CarrierItemHead(CarrierItem next) {
        super("", "", next);
    }
}
