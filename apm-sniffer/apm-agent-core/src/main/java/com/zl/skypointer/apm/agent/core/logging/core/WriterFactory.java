package com.zl.skypointer.apm.agent.core.logging.core;

import com.zl.skypointer.apm.agent.core.boot.AgentPackageNotFoundException;
import com.zl.skypointer.apm.agent.core.boot.AgentPackagePath;
import com.zl.skypointer.apm.agent.core.conf.Config;
import com.zl.skypointr.apm.util.StringUtil;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 11:20
 * @since JDK1.8
 */
public class WriterFactory {
    public static IWriter getLogWriter() {
        if (AgentPackagePath.isPathFound()) {
            if (StringUtil.isEmpty(Config.Logging.DIR)) {
                try {
                    Config.Logging.DIR = AgentPackagePath.getPath() + "/logs";
                } catch (AgentPackageNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return FileWriter.get();
        } else {
            return SystemOutWriter.INSTANCE;
        }
    }
}
