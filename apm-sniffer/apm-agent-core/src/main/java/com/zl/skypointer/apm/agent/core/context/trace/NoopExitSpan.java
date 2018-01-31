


package com.zl.skypointer.apm.agent.core.context.trace;

public class NoopExitSpan extends NoopSpan implements WithPeerInfo {

    private String peer;
    private int peerId;

    public NoopExitSpan(int peerId) {
        this.peerId = peerId;
    }

    public NoopExitSpan(String peer) {
        this.peer = peer;
    }

    @Override
    public int getPeerId() {
        return peerId;
    }

    @Override
    public String getPeer() {
        return peer;
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
