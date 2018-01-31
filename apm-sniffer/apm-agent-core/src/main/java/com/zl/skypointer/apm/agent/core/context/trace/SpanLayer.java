package com.zl.skypointer.apm.agent.core.context.trace;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/30 19:13
 * @since JDK1.8
 */
public enum SpanLayer {
    DB(1),
    RPC_FRAMEWORK(2),
    HTTP(3),
    MQ(4);

    private int code;

    SpanLayer(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static void asDB(AbstractSpan span) {
        span.setLayer(SpanLayer.DB);
    }

    public static void asRPCFramework(AbstractSpan span) {
        span.setLayer(SpanLayer.RPC_FRAMEWORK);
    }

    public static void asHttp(AbstractSpan span) {
        span.setLayer(SpanLayer.HTTP);
    }

    public static void asMQ(AbstractSpan span) {
        span.setLayer(SpanLayer.MQ);
    }
}

