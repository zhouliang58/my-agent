package com.zl.skypointer.apm.agent.core.context;

import com.zl.skypointer.apm.agent.core.context.trace.TraceSegment;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 15:39
 * @since JDK1.8
 */
public interface TracingContextListener {
    void afterFinished(TraceSegment traceSegment);
}