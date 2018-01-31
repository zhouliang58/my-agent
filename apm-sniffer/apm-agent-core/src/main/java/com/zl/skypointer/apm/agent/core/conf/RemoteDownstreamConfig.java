package com.zl.skypointer.apm.agent.core.conf;

import com.zl.skypointer.apm.agent.core.dictionary.DictionaryUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 10:14
 * @since JDK1.8
 */
public class RemoteDownstreamConfig {
    public static class Agent {
        public volatile static int APPLICATION_ID = DictionaryUtil.nullValue();

        public volatile static int APPLICATION_INSTANCE_ID = DictionaryUtil.nullValue();
    }

    public static class Collector {
        /**
         * Collector GRPC-Service address.
         */
        public volatile static List<String> GRPC_SERVERS = new LinkedList<String>();
    }
}
