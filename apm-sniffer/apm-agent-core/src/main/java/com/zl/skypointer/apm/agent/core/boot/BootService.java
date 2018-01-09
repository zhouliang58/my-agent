package com.zl.skypointer.apm.agent.core.boot;

/**
 * The <code>BootService</code> is an interface to all remote, which need to boot when plugin mechanism begins to
 * work.
 * {@link #boot()} will be called when <code>BootService</code> start up.
 *
 * @author zhouliang
 * @version v1.0 2018/1/9 23:33
 * @since JDK1.8
 */
public interface BootService {
    void beforeBoot() throws Throwable;

    void boot() throws Throwable;

    void afterBoot() throws Throwable;

    void shutdown() throws Throwable;
}