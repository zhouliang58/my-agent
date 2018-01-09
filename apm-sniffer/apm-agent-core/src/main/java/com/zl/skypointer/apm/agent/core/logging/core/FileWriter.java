package com.zl.skypointer.apm.agent.core.logging.core;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.zl.skypointer.apm.agent.core.conf.Config;
import com.zl.skypointer.apm.agent.core.conf.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * 采用disruptor队列异步写日志
 *
 * @author zhouliang
 * @version v1.0 2018/1/5 11:21
 * @since JDK1.8
 */
public class FileWriter implements IWriter, EventHandler<LogMessageHolder> {
    private static FileWriter INSTANCE;
    private static final Object CREATE_LOCK = new Object();
    private Disruptor<LogMessageHolder> disruptor;
    private RingBuffer<LogMessageHolder> buffer;
    private FileOutputStream fileOutputStream;
    private volatile boolean started = false;
    private volatile int fileSize;
    private volatile int lineNum;

    public static FileWriter get() {
        if (INSTANCE == null) {
            synchronized (CREATE_LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = new FileWriter();
                }
            }
        }
        return INSTANCE;
    }

    private FileWriter() {
        // 创建disruptor,采用DaemonThreadFactory策略
        disruptor = new Disruptor<LogMessageHolder>(new EventFactory<LogMessageHolder>() {
            @Override
            public LogMessageHolder newInstance() {
                return new LogMessageHolder();
            }
        }, 1024, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(this);
        buffer = disruptor.getRingBuffer();
        lineNum = 0;
        disruptor.start();
    }

    // 消费Event的handler
    @Override
    public void onEvent(LogMessageHolder event, long sequence, boolean endOfBatch) throws Exception {
        if (hasWriteStream()) {
            try {
                lineNum++;
                write(event.getMessage() + Constants.LINE_SEPARATOR, endOfBatch);
            } finally {
                event.setMessage(null);
            }
        }
    }

    /**
     * 执行真正把日志写到文件的动作
     * @param message
     * @param forceFlush
     */
    private void write(String message, boolean forceFlush) {
        try {
            fileOutputStream.write(message.getBytes());
            fileSize += message.length();
            if (forceFlush || lineNum % 20 == 0) {
                fileOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            switchFile();
        }
    }

    private void switchFile() {
        if (fileSize > Config.Logging.MAX_FILE_SIZE) {
            forceExecute(new Callable() {
                @Override
                public Object call() throws Exception {
                    fileOutputStream.flush();
                    return null;
                }
            });
            forceExecute(new Callable() {
                @Override
                public Object call() throws Exception {
                    fileOutputStream.close();
                    return null;
                }
            });
            forceExecute(new Callable() {
                @Override
                public Object call() throws Exception {
                    new File(Config.Logging.DIR, Config.Logging.FILE_NAME)
                            .renameTo(new File(Config.Logging.DIR,
                                    Config.Logging.FILE_NAME + new SimpleDateFormat(".yyyy_MM_dd_HH_mm_ss").format(new Date())));
                    return null;
                }
            });
            forceExecute(new Callable() {
                @Override
                public Object call() throws Exception {
                    fileOutputStream = null;
                    started = false;
                    return null;
                }
            });
        }
    }

    private void forceExecute(Callable callable) {
        try {
            callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化fileOutputStream，指定日志目录logFilePath
     * @return
     */
    private boolean hasWriteStream() {
        if (fileOutputStream != null) {
            return true;
        }
        if (!started) {
            File logFilePath = new File(Config.Logging.DIR);
            if (!logFilePath.exists()) {
                logFilePath.mkdirs();
            } else if (!logFilePath.isDirectory()) {
                System.err.println("Log dir(" + Config.Logging.DIR + ") is not a directory.");
            }
            try {
                fileOutputStream = new FileOutputStream(new File(logFilePath, Config.Logging.FILE_NAME), true);
                fileSize = Long.valueOf(new File(logFilePath, Config.Logging.FILE_NAME).length()).intValue();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            started = true;
        }

        return fileOutputStream != null;
    }

    /**
     * 往disruptor中写日志，会触发onEvent进行消费
     * @param message
     */
    @Override
    public void write(String message) {
        long next = buffer.next();
        try {
            LogMessageHolder messageHolder = buffer.get(next);
            messageHolder.setMessage(message);
        } finally {
            buffer.publish(next);
        }
    }
}

