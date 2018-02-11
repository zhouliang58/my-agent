package com.zl.skypointer.apm.collector.queue.disruptor;

import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.core.module.ModuleProvider;
import com.zl.skypointer.apm.collector.core.module.ServiceNotProvidedException;
import com.zl.skypointer.apm.collector.queue.QueueModule;
import com.zl.skypointer.apm.collector.queue.disruptor.service.DisruptorQueueCreatorService;
import com.zl.skypointer.apm.collector.queue.service.QueueCreatorService;

import java.util.Properties;

/**
 * Created by zhouliang
 * 2018-02-11 9:44
 */
public class QueueModuleDisruptorProvider extends ModuleProvider {

    @Override public String name() {
        return "disruptor";
    }

    @Override public Class<? extends Module> module() {
        return QueueModule.class;
    }

    @Override public void prepare(Properties config) throws ServiceNotProvidedException {
        this.registerServiceImplementation(QueueCreatorService.class, new DisruptorQueueCreatorService());
    }

    @Override public void start(Properties config) throws ServiceNotProvidedException {

    }

    @Override public void notifyAfterCompleted() throws ServiceNotProvidedException {

    }

    @Override public String[] requiredModules() {
        return new String[0];
    }
}
