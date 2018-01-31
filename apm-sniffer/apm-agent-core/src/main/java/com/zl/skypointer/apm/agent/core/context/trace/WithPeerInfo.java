package com.zl.skypointer.apm.agent.core.context.trace;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 15:29
 * @since JDK1.8
 */
public interface WithPeerInfo {
    int getPeerId();

    String getPeer();
}