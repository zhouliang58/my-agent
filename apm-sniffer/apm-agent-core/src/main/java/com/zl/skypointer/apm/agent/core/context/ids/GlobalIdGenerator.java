package com.zl.skypointer.apm.agent.core.context.ids;

import com.zl.skypointer.apm.agent.core.conf.RemoteDownstreamConfig;
import com.zl.skypointer.apm.agent.core.dictionary.DictionaryUtil;

import java.util.Random;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 10:10
 * @since JDK1.8
 */
public class GlobalIdGenerator {
    private static final ThreadLocal<IDContext> THREAD_ID_SEQUENCE = new ThreadLocal<IDContext>() {
        @Override
        protected IDContext initialValue() {
            return new IDContext(System.currentTimeMillis(), (short)0);
        }
    };

    private GlobalIdGenerator() {
    }

    /**
     * Generate a new id, combined by three long numbers.
     *
     * The first one represents application instance id. (most likely just an integer value, would be helpful in
     * protobuf)
     *
     * The second one represents thread id. (most likely just an integer value, would be helpful in protobuf)
     *
     * The third one also has two parts,<br/>
     * 1) a timestamp, measured in milliseconds<br/>
     * 2) a seq, in current thread, between 0(included) and 9999(included)
     *
     * Notice, a long costs 8 bytes, three longs cost 24 bytes. And at the same time, a char costs 2 bytes. So
     * sky-walking's old global and segment id like this: "S.1490097253214.-866187727.57515.1.1" which costs at least 72
     * bytes.
     *
     * @return an array contains three long numbers, which represents a unique id.
     */
    public static ID generate() {
        if (RemoteDownstreamConfig.Agent.APPLICATION_INSTANCE_ID == DictionaryUtil.nullValue()) {
            throw new IllegalStateException();
        }
        IDContext context = THREAD_ID_SEQUENCE.get();

        return new ID(
                // ID.part1 属性，应用编号实例。
                RemoteDownstreamConfig.Agent.APPLICATION_INSTANCE_ID,
                // ID.part2 属性，线程编号。
                Thread.currentThread().getId(),
                // ID.part3 属性，调用 IDContext#nextSeq() 方法，生成带有时间戳的序列号。
                context.nextSeq()
        );
    }

    private static class IDContext {
        private long lastTimestamp;
        private short threadSeq;

        // Just for considering time-shift-back only.
        private long runRandomTimestamp;
        private int lastRandomValue;
        private Random random;

        private IDContext(long lastTimestamp, short threadSeq) {
            this.lastTimestamp = lastTimestamp;
            this.threadSeq = threadSeq;
        }

        private long nextSeq() {
            return timestamp() * 10000 + nextThreadSeq();
        }

        private long timestamp() {
            long currentTimeMillis = System.currentTimeMillis();

            if (currentTimeMillis < lastTimestamp) {
                // Just for considering time-shift-back by Ops or OS. @hanahmily 's suggestion.
                if (random == null) {
                    random = new Random();
                }
                if (runRandomTimestamp != currentTimeMillis) {
                    lastRandomValue = random.nextInt();
                    runRandomTimestamp = currentTimeMillis;
                }
                return lastRandomValue;
            } else {
                lastTimestamp = currentTimeMillis;
                return lastTimestamp;
            }
        }

        private short nextThreadSeq() {
            if (threadSeq == 10000) {
                threadSeq = 0;
            }
            return threadSeq++;
        }
    }
}
