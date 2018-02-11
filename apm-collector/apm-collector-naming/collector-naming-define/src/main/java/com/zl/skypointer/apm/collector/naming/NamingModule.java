package com.zl.skypointer.apm.collector.naming;

import com.zl.skypointer.apm.collector.core.module.Module;
import com.zl.skypointer.apm.collector.core.module.Service;
import com.zl.skypointer.apm.collector.naming.service.NamingHandlerRegisterService;

/**
 * Created by zhouliang
 * 2018-02-10 20:01
 */
public class NamingModule extends Module {

    public static final String NAME = "naming";

    @Override public String name() {
        return NAME;
    }

    @Override public Class<? extends Service>[] services() {
        return new Class[] {NamingHandlerRegisterService.class};
    }
}
