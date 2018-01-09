package com.zl.skypointer.apm.agent.core.logging.core;

import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogResolver;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 11:18
 * @since JDK1.8
 */
public class EasyLogResolver implements LogResolver {
    @Override
    public ILog getLogger(Class<?> clazz) {
        return new EasyLogger(clazz);
    }
}
