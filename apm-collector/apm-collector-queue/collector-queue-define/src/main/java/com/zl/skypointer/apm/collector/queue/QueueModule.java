package com.zl.skypointer.apm.collector.queue;

import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.queue.service.QueueCreatorService;

/**
 * Created by zhouliang
 * 2018-02-11 9:35
 */
public class QueueModule extends Module {

    public static final String NAME = "queue";

    @Override public String name() {
        return NAME;
    }

    @Override public Class[] services() {
        return new Class[] {QueueCreatorService.class};
    }
}
