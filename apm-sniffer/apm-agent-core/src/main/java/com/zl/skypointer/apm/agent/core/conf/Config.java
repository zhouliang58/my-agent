package com.zl.skypointer.apm.agent.core.conf;

import com.zl.skypointer.apm.agent.core.context.trace.TraceSegment;
import com.zl.skypointer.apm.agent.core.logging.core.LogLevel;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 15:24
 * @since JDK1.8
 */
public class Config {
    public static class Agent {
        /**
         * Application code is showed in sky-walking-ui. Suggestion: set an unique name for each application, one
         * application's nodes share the same code.
         */
        public static String APPLICATION_CODE = "";

        /**
         * Negative or zero means off, by default. {@link #SAMPLE_N_PER_3_SECS} means sampling N {@link TraceSegment} in
         * 10 seconds tops.
         */
        public static int SAMPLE_N_PER_3_SECS = -1;

        /**
         * If the operation name of the first span is included in this set, this segment should be ignored.
         */
        public static String IGNORE_SUFFIX = ".jpg,.jpeg,.js,.css,.png,.bmp,.gif,.ico,.mp3,.mp4,.html,.svg";

        /**
         * The max number of spans in a single segment. Through this config item, skypointer keep your application
         * memory cost estimated.
         */
        public static int SPAN_LIMIT_PER_SEGMENT = 300;

        /**
         * If true, skypointer agent will save all instrumented classes files in `/debugging` folder.
         * skypointer team may ask for these files in order to resolve compatible problem.
         */
        public static boolean IS_OPEN_DEBUGGING_CLASS = false;
    }

    public static class Collector {
        /**
         * grpc channel status check interval
         */
        public static long GRPC_CHANNEL_CHECK_INTERVAL = 30;
        /**
         * application and service registry check interval
         */
        public static long APP_AND_SERVICE_REGISTER_CHECK_INTERVAL = 3;
        /**
         * discovery rest check interval
         */
        public static long DISCOVERY_CHECK_INTERVAL = 60;
        /**
         * Collector REST-Service address. e.g. SERVERS="127.0.0.1:8080"  for single collector node.
         * SERVERS="10.2.45.126:8080,10.2.45.127:7600"  for multi collector nodes.
         */
        public static String SERVERS = "";

        /**
         * Collector service discovery REST service name
         */
        public static String DISCOVERY_SERVICE_NAME = "/agent/gRPC";
    }

    public static class Logging {
        /**
         * Log file name.
         */
        public static String FILE_NAME = "skypointer-api.log";

        /**
         * Log files directory. Default is blank string, means, use "system.out" to output logs.
         */
        public static String DIR = "";

        /**
         * The max size of log file. If the size is bigger than this, archive the current file, and write into a new
         * file.
         */
        public static int MAX_FILE_SIZE = 300 * 1024 * 1024;

        /**
         * The log level. Default is debug.
         */
        public static LogLevel LEVEL = LogLevel.DEBUG;
    }

    public static class Dictionary {
        /**
         * The buffer size of application codes and peer
         */
        public static int APPLICATION_CODE_BUFFER_SIZE = 10 * 10000;

        public static int OPERATION_NAME_BUFFER_SIZE = 1000 * 10000;
    }
}
