package com.zl.skypointer.apm.agent;

import com.zl.skypointer.apm.agent.core.boot.AgentPackageNotFoundException;
import com.zl.skypointer.apm.agent.core.boot.AgentPackagePath;
import com.zl.skypointer.apm.agent.core.conf.Config;
import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;

import java.io.File;
import java.io.IOException;

/**
 *Instrument 调试类，用于将被 JavaAgent 修改的所有类存储到 ${JAVA_AGENT_PACKAGE}/debugger 目录下。
 * 需要配置 agent.is_open_debugging_class = true
 *
 * @author zhouliang
 * @version v1.0 2018/1/9 23:47
 * @since JDK1.8
 */
public enum InstrumentDebuggingClass {
    INSTANCE;

    private static final ILog logger = LogManager.getLogger(InstrumentDebuggingClass.class);
    private File debuggingClassesRootPath;

    public void log(TypeDescription typeDescription, DynamicType dynamicType) {
        if (!Config.Agent.IS_OPEN_DEBUGGING_CLASS) {
            return;
        }

        /**
         * try to do I/O things in synchronized way, to avoid unexpected situations.
         */
        synchronized (INSTANCE) {
            try {
                if (debuggingClassesRootPath == null) {
                    try {
                        debuggingClassesRootPath = new File(AgentPackagePath.getPath(), "/debugging");
                        if (!debuggingClassesRootPath.exists()) {
                            debuggingClassesRootPath.mkdir();
                        }
                    } catch (AgentPackageNotFoundException e) {
                        logger.error(e, "Can't find the root path for creating /debugging folder.");
                    }
                }

                try {
                    dynamicType.saveIn(debuggingClassesRootPath);
                } catch (IOException e) {
                    logger.error(e, "Can't save class {} to file." + typeDescription.getActualName());
                }
            } catch (Throwable t) {
                logger.error(t, "Save debugging classes fail.");
            }
        }
    }
}

