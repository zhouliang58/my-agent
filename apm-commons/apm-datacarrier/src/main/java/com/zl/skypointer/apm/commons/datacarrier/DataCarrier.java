package com.zl.skypointer.apm.commons.datacarrier;

import com.zl.skypointer.apm.commons.datacarrier.buffer.BufferStrategy;
import com.zl.skypointer.apm.commons.datacarrier.buffer.Channels;
import com.zl.skypointer.apm.commons.datacarrier.consumer.ConsumerPool;
import com.zl.skypointer.apm.commons.datacarrier.consumer.IConsumer;
import com.zl.skypointer.apm.commons.datacarrier.partition.IDataPartitioner;
import com.zl.skypointer.apm.commons.datacarrier.partition.SimpleRollingPartitioner;


/**
 * Created by zhouliang
 * 2018-02-13 10:41
 */
public class DataCarrier<T> {
    private final int bufferSize;
    private final int channelSize;
    private Channels<T> channels;
    private ConsumerPool<T> consumerPool;

    public DataCarrier(int channelSize, int bufferSize) {
        this.bufferSize = bufferSize;
        this.channelSize = channelSize;
        channels = new Channels<T>(channelSize, bufferSize, new SimpleRollingPartitioner<T>(), BufferStrategy.BLOCKING);
    }

    /**
     * set a new IDataPartitioner.
     * It will cover the current one or default one.(Default is {@link SimpleRollingPartitioner)}
     *
     * @param dataPartitioner
     * @return
     */
    public DataCarrier setPartitioner(IDataPartitioner<T> dataPartitioner) {
        this.channels.setPartitioner(dataPartitioner);
        return this;
    }

    /**
     * override the strategy at runtime.
     * Notice, {@link Channels<T>} will override several channels one by one.
     *
     * @param strategy
     */
    public DataCarrier setBufferStrategy(BufferStrategy strategy) {
        this.channels.setStrategy(strategy);
        return this;
    }

    /**
     * produce data to buffer, using the givven {@link BufferStrategy}.
     *
     * @param data
     * @return false means produce data failure. The data will not be consumed.
     */
    public boolean produce(T data) {
        if (consumerPool != null) {
            if (!consumerPool.isRunning()) {
                return false;
            }
        }

        return this.channels.save(data);
    }

    /**
     * set consumers to this Carrier.
     * consumer begin to run when {@link DataCarrier<T>#produce(T)} begin to work.
     *
     * @param consumerClass class of consumer
     * @param num number of consumer threads
     */
    public DataCarrier consume(Class<? extends IConsumer<T>> consumerClass, int num) {
        if (consumerPool != null) {
            consumerPool.close();
        }
        consumerPool = new ConsumerPool<T>(this.channels, consumerClass, num);
        consumerPool.begin();
        return this;
    }

    /**
     * set consumers to this Carrier.
     * consumer begin to run when {@link DataCarrier<T>#produce(T)} begin to work.
     *
     * @param consumer single instance of consumer, all consumer threads will all use this instance.
     * @param num number of consumer threads
     * @return
     */
    public DataCarrier consume(IConsumer<T> consumer, int num) {
        if (consumerPool != null) {
            consumerPool.close();
        }
        consumerPool = new ConsumerPool<T>(this.channels, consumer, num);
        consumerPool.begin();
        return this;
    }

    /**
     * shutdown all consumer threads, if consumer threads are running. Notice {@link BufferStrategy}: if {@link
     * BufferStrategy} == {@link BufferStrategy#BLOCKING}, shutdown consumers maybe cause blocking when producing.
     * Better way to change consumers are use {@link DataCarrier#consume}
     */
    public void shutdownConsumers() {
        if (consumerPool != null) {
            consumerPool.close();
        }
    }
}
