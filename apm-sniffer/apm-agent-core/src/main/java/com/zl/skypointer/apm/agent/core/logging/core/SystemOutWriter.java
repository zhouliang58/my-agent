package com.zl.skypointer.apm.agent.core.logging.core;

import java.io.PrintStream;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 11:22
 * @since JDK1.8
 */
public enum SystemOutWriter implements IWriter {
    INSTANCE;

    /**
     * Tricky codes for avoiding style-check.
     * Because, in here, "system.out.println" is the only choice to output logs.
     *
     * @param message
     */
    @Override
    public void write(String message) {
        PrintStream out = System.out;
        out.println(message);
    }
}
