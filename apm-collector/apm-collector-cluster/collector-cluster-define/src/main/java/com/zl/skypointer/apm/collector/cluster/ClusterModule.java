package com.zl.skypointer.apm.collector.cluster;

import com.zl.skypointer.apm.collector.cluster.service.ModuleRegisterService;
import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.core.module.Service;
import com.zl.skypointer.apm.collector.cluster.service.ModuleListenerService;

/**
 * Created by zhouliang
 * 2018-02-07 12:19
 */
public class ClusterModule extends Module {
    public static final String NAME = "cluster";

    @Override public String name() {
        return NAME;
    }

    @Override public Class<? extends Service>[] services() {
        return new Class[] {ModuleListenerService.class, ModuleRegisterService.class};
    }
}
