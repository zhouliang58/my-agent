package com.zl.skypointer.apm.collector.queue.datacarrier;

import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.core.module.ModuleProvider;
import com.zl.skypointer.apm.collector.core.module.ServiceNotProvidedException;
import com.zl.skypointer.apm.collector.queue.QueueModule;
import com.zl.skypointer.apm.collector.queue.datacarrier.service.DataCarrierQueueCreatorService;
import com.zl.skypointer.apm.collector.queue.service.QueueCreatorService;

import java.util.Properties;

/**
 * Created by zhouliang
 * 2018-02-11 9:33
 */
public class QueueModuleDataCarrierProvider extends ModuleProvider {

    @Override public String name() {
        return "datacarrier";
    }

    @Override public Class<? extends Module> module() {
        return QueueModule.class;
    }

    @Override public void prepare(Properties config) throws ServiceNotProvidedException {
        this.registerServiceImplementation(QueueCreatorService.class, new DataCarrierQueueCreatorService());
    }

    @Override public void start(Properties config) throws ServiceNotProvidedException {

    }

    @Override public void notifyAfterCompleted() throws ServiceNotProvidedException {

    }

    @Override public String[] requiredModules() {
        return new String[0];
    }
}
