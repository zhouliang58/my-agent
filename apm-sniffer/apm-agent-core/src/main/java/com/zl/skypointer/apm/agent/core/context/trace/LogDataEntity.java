package com.zl.skypointer.apm.agent.core.context.trace;

import com.zl.skypointer.apm.agent.core.context.util.KeyValuePair;
import com.zl.skypointer.apm.network.proto.LogMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 11:33
 * @since JDK1.8
 */
public class LogDataEntity {
    private long timestamp = 0;
    private List<KeyValuePair> logs;

    private LogDataEntity(long timestamp, List<KeyValuePair> logs) {
        this.timestamp = timestamp;
        this.logs = logs;
    }

    public List<KeyValuePair> getLogs() {
        return logs;
    }

    public static class Builder {
        protected List<KeyValuePair> logs;

        public Builder() {
            logs = new LinkedList<KeyValuePair>();
        }

        public Builder add(KeyValuePair... fields) {
            for (KeyValuePair field : fields) {
                logs.add(field);
            }
            return this;
        }

        public LogDataEntity build(long timestamp) {
            return new LogDataEntity(timestamp, logs);
        }
    }

    public LogMessage transform() {
        LogMessage.Builder logMessageBuilder = LogMessage.newBuilder();
        for (KeyValuePair log : logs) {
            logMessageBuilder.addData(log.transform());
        }
        logMessageBuilder.setTime(timestamp);
        return logMessageBuilder.build();
    }
}
