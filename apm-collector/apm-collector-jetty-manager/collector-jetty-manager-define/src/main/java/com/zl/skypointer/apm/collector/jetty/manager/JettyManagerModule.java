package com.zl.skypointer.apm.collector.jetty.manager;

import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.jetty.manager.service.JettyManagerService;

/**
 * Created by zhouliang
 * 2018-02-10 19:39
 */
public class JettyManagerModule extends Module {

    public static final String NAME = "jetty_manager";

    @Override public String name() {
        return NAME;
    }

    @Override public Class[] services() {
        return new Class[] {JettyManagerService.class};
    }
}
