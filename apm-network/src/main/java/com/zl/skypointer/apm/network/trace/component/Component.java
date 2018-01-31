package com.zl.skypointer.apm.network.trace.component;

/**
 * The <code>Component</code> represents component library,
 * which has been supported by skywalking sniffer.
 *
 * @author zhouliang
 * @version v1.0 2018/1/30 19:15
 * @since JDK1.8
 */
public interface Component {
    int getId();

    String getName();
}