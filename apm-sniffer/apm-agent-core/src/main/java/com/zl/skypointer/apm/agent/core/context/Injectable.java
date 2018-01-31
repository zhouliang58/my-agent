package com.zl.skypointer.apm.agent.core.context;

/**
 * The <code>Injectable</code> represents a provider, which gives the reference of {@link ContextCarrier} and peer
 * for the agent core, for cross-process propagation.
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 15:48
 * @since JDK1.8
 */
public interface Injectable {
    ContextCarrier getCarrier();

    /**
     * @return peer, represent ipv4, ipv6, hostname, or cluster addresses list.
     */
    String getPeer();
}
