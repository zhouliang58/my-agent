package com.zl.skypointer.apm.collector.queue.datacarrier.service;

import com.zl.skypointer.apm.collector.queue.base.QueueEventHandler;
import com.zl.skypointer.apm.collector.queue.base.QueueExecutor;
import com.zl.skypointer.apm.collector.queue.service.QueueCreatorService;

/**
 * Created by zhouliang
 * 2018-02-11 9:33
 */
public class DataCarrierQueueCreatorService implements QueueCreatorService {

    @Override public QueueEventHandler create(int queueSize, QueueExecutor executor) {
        return null;
    }
}
