package com.zl.skypointer.apm.agent.core.context;

import com.zl.skypointer.apm.agent.core.boot.ServiceManager;
import com.zl.skypointer.apm.agent.core.conf.Config;
import com.zl.skypointer.apm.agent.core.context.trace.AbstractSpan;
import com.zl.skypointer.apm.agent.core.context.trace.AbstractTracingSpan;
import com.zl.skypointer.apm.agent.core.context.trace.EntrySpan;
import com.zl.skypointer.apm.agent.core.context.trace.ExitSpan;
import com.zl.skypointer.apm.agent.core.context.trace.LocalSpan;
import com.zl.skypointer.apm.agent.core.context.trace.NoopExitSpan;
import com.zl.skypointer.apm.agent.core.context.trace.NoopSpan;
import com.zl.skypointer.apm.agent.core.context.trace.TraceSegment;
import com.zl.skypointer.apm.agent.core.context.trace.TraceSegmentRef;
import com.zl.skypointer.apm.agent.core.context.trace.WithPeerInfo;
import com.zl.skypointer.apm.agent.core.dictionary.DictionaryManager;
import com.zl.skypointer.apm.agent.core.dictionary.DictionaryUtil;
import com.zl.skypointer.apm.agent.core.dictionary.PossibleFound;
import com.zl.skypointer.apm.agent.core.sampling.SamplingService;

import java.util.LinkedList;
import java.util.List;

/**
 * The <code>TracingContext</code> represents a core tracing logic controller. It build the final {@link
 * TracingContext}, by the stack mechanism, which is similar with the codes work.
 *
 * In opentracing concept, it means, all spans in a segment tracing context(thread) are CHILD_OF relationship, but no
 * FOLLOW_OF.
 *
 * In skywalking core concept, FOLLOW_OF is an abstract concept when cross-process MQ or cross-thread async/batch tasks
 * happen, we used {@link TraceSegmentRef} for these scenarios. Check {@link TraceSegmentRef} which is from {@link
 * ContextCarrier} or {@link ContextSnapshot}.
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 15:41
 * @since JDK1.8
 */
public class TracingContext implements AbstractTracerContext {
    /**
     * @see {@link SamplingService}
     */
    private SamplingService samplingService;

    /**
     * 上下文对应的 TraceSegment 对象。
     * The final {@link TraceSegment}, which includes all finished spans.
     */
    private TraceSegment segment;

    /**
     * AbstractSpan 链表数组，收集当前活跃的 Span 对象。正如方法的调用与执行一样，在一个调用栈中，先执行的方法后结束。
     *
     * Active spans stored in a Stack, usually called 'ActiveSpanStack'. This {@link LinkedList} is the in-memory
     * storage-structure. <p> I use {@link LinkedList#removeLast()}, {@link LinkedList#addLast(Object)} and {@link
     * LinkedList#last} instead of {@link #pop()}, {@link #push(AbstractSpan)}, {@link #peek()}
     */
    private LinkedList<AbstractSpan> activeSpanStack = new LinkedList<AbstractSpan>();

    /**
     * Span 编号自增序列。创建的 Span 的编号，通过该变量自增生成。
     *
     * A counter for the next span.
     */
    private int spanIdGenerator;

    /**
     * Initialize all fields with default value.
     */
    TracingContext() {
        this.segment = new TraceSegment();
        this.spanIdGenerator = 0;
        if (samplingService == null) {
            samplingService = ServiceManager.INSTANCE.findService(SamplingService.class);
        }
    }

    /**
     * Inject the context into the given carrier, only when the active span is an exit one.
     *
     * @param carrier to carry the context for crossing process.
     * @throws IllegalStateException, if the active span isn't an exit one.
     * @see {@link AbstractTracerContext#inject(ContextCarrier)}
     */
    @Override
    public void inject(ContextCarrier carrier) {
        AbstractSpan span = this.activeSpan();
        if (!span.isExit()) {
            throw new IllegalStateException("Inject can be done only in Exit Span");
        }

        // TODO为什么可以直接强转
        WithPeerInfo spanWithPeer = (WithPeerInfo)span;
        String peer = spanWithPeer.getPeer();
        int peerId = spanWithPeer.getPeerId();

        carrier.setTraceSegmentId(this.segment.getTraceSegmentId());
        carrier.setSpanId(span.getSpanId());

        carrier.setParentApplicationInstanceId(segment.getApplicationInstanceId());

        if (DictionaryUtil.isNull(peerId)) {
            carrier.setPeerHost(peer);
        } else {
            carrier.setPeerId(peerId);
        }
        List<TraceSegmentRef> refs = this.segment.getRefs();
        int operationId;
        String operationName;
        int entryApplicationInstanceId;
        if (refs != null && refs.size() > 0) {
            TraceSegmentRef ref = refs.get(0);
            operationId = ref.getEntryOperationId();
            operationName = ref.getEntryOperationName();
            entryApplicationInstanceId = ref.getEntryApplicationInstanceId();
        } else {
            AbstractSpan firstSpan = first();
            operationId = firstSpan.getOperationId();
            operationName = firstSpan.getOperationName();
            entryApplicationInstanceId = this.segment.getApplicationInstanceId();
        }
        carrier.setEntryApplicationInstanceId(entryApplicationInstanceId);

        if (operationId == DictionaryUtil.nullValue()) {
            carrier.setEntryOperationName(operationName);
        } else {
            carrier.setEntryOperationId(operationId);
        }

        int parentOperationId = first().getOperationId();
        if (parentOperationId == DictionaryUtil.nullValue()) {
            carrier.setParentOperationName(first().getOperationName());
        } else {
            carrier.setParentOperationId(parentOperationId);
        }

        carrier.setDistributedTraceIds(this.segment.getRelatedGlobalTraces());
    }


    /**
     * Extract the carrier to build the reference for the pre segment.
     *
     * @param carrier carried the context from a cross-process segment.
     * @see {@link AbstractTracerContext#extract(ContextCarrier)}
     */
    @Override
    public void extract(ContextCarrier carrier) {
        TraceSegmentRef ref = new TraceSegmentRef(carrier);
        this.segment.ref(ref);
        this.segment.relatedGlobalTraces(carrier.getDistributedTraceId());
        AbstractSpan span = this.activeSpan();
        if (span instanceof EntrySpan) {
            span.ref(ref);
        }
    }

    /**
     * Capture the snapshot of current context.
     *
     * @return the snapshot of context for cross-thread propagation
     * @see {@link AbstractTracerContext#capture()}
     */
    @Override
    public ContextSnapshot capture() {
        List<TraceSegmentRef> refs = this.segment.getRefs();
        ContextSnapshot snapshot = new ContextSnapshot(segment.getTraceSegmentId(),
                activeSpan().getSpanId(),
                segment.getRelatedGlobalTraces());
        int entryOperationId;
        String entryOperationName;
        int entryApplicationInstanceId;
        AbstractSpan firstSpan = first();
        if (refs != null && refs.size() > 0) {
            TraceSegmentRef ref = refs.get(0);
            entryOperationId = ref.getEntryOperationId();
            entryOperationName = ref.getEntryOperationName();
            entryApplicationInstanceId = ref.getEntryApplicationInstanceId();
        } else {
            entryOperationId = firstSpan.getOperationId();
            entryOperationName = firstSpan.getOperationName();
            entryApplicationInstanceId = this.segment.getApplicationInstanceId();
        }
        snapshot.setEntryApplicationInstanceId(entryApplicationInstanceId);

        if (entryOperationId == DictionaryUtil.nullValue()) {
            snapshot.setEntryOperationName(entryOperationName);
        } else {
            snapshot.setEntryOperationId(entryOperationId);
        }

        if (firstSpan.getOperationId() == DictionaryUtil.nullValue()) {
            snapshot.setParentOperationName(firstSpan.getOperationName());
        } else {
            snapshot.setParentOperationId(firstSpan.getOperationId());
        }
        return snapshot;
    }

    /**
     * Continue the context from the given snapshot of parent thread.
     *
     * @param snapshot from {@link #capture()} in the parent thread.
     * @see {@link AbstractTracerContext#continued(ContextSnapshot)}
     */
    @Override
    public void continued(ContextSnapshot snapshot) {
        TraceSegmentRef segmentRef = new TraceSegmentRef(snapshot);
        this.segment.ref(segmentRef);
        this.activeSpan().ref(segmentRef);
        this.segment.relatedGlobalTraces(snapshot.getDistributedTraceId());
    }

    /**
     * @return the first global trace id.
     */
    @Override
    public String getReadableGlobalTraceId() {
        return segment.getRelatedGlobalTraces().get(0).toString();
    }

    /**
     * Create an entry span
     *
     * @param operationName most likely a service name
     * @return span instance.
     * @see {@link EntrySpan}
     */
    @Override
    public AbstractSpan createEntrySpan(final String operationName) {
        if (isLimitMechanismWorking()) {
            NoopSpan span = new NoopSpan();
            return push(span);
        }
        AbstractSpan entrySpan;
        final AbstractSpan parentSpan = peek();
        final int parentSpanId = parentSpan == null ? -1 : parentSpan.getSpanId();
        if (parentSpan == null) {
            entrySpan = (AbstractTracingSpan) DictionaryManager.findOperationNameCodeSection()
                    .findOnly(segment.getApplicationId(), operationName)
                    .doInCondition(new PossibleFound.FoundAndObtain() {
                        @Override public Object doProcess(int operationId) {
                            return new EntrySpan(spanIdGenerator++, parentSpanId, operationId);
                        }
                    }, new PossibleFound.NotFoundAndObtain() {
                        @Override public Object doProcess() {
                            return new EntrySpan(spanIdGenerator++, parentSpanId, operationName);
                        }
                    });
            entrySpan.start();
            return push(entrySpan);
        } else if (parentSpan.isEntry()) {
            entrySpan = (AbstractTracingSpan)DictionaryManager.findOperationNameCodeSection()
                    .findOnly(segment.getApplicationId(), operationName)
                    .doInCondition(new PossibleFound.FoundAndObtain() {
                        @Override public Object doProcess(int operationId) {
                            return parentSpan.setOperationId(operationId);
                        }
                    }, new PossibleFound.NotFoundAndObtain() {
                        @Override public Object doProcess() {
                            return parentSpan.setOperationName(operationName);
                        }
                    });
            return entrySpan.start();
        } else {
            throw new IllegalStateException("The Entry Span can't be the child of Non-Entry Span");
        }
    }

    /**
     * Create a local span
     *
     * @param operationName most likely a local method signature, or business name.
     * @return the span represents a local logic block.
     * @see {@link LocalSpan}
     */
    @Override
    public AbstractSpan createLocalSpan(final String operationName) {
        if (isLimitMechanismWorking()) {
            NoopSpan span = new NoopSpan();
            return push(span);
        }
        AbstractSpan parentSpan = peek();
        final int parentSpanId = parentSpan == null ? -1 : parentSpan.getSpanId();
        AbstractTracingSpan span = (AbstractTracingSpan)DictionaryManager.findOperationNameCodeSection()
                .findOrPrepare4Register(segment.getApplicationId(), operationName)
                .doInCondition(new PossibleFound.FoundAndObtain() {
                    @Override
                    public Object doProcess(int operationId) {
                        return new LocalSpan(spanIdGenerator++, parentSpanId, operationId);
                    }
                }, new PossibleFound.NotFoundAndObtain() {
                    @Override
                    public Object doProcess() {
                        return new LocalSpan(spanIdGenerator++, parentSpanId, operationName);
                    }
                });
        span.start();
        return push(span);
    }

    /**
     * Create an exit span
     *
     * @param operationName most likely a service name of remote
     * @param remotePeer the network id(ip:port, hostname:port or ip1:port1,ip2,port, etc.)
     * @return the span represent an exit point of this segment.
     * @see {@link ExitSpan}
     */
    @Override
    public AbstractSpan createExitSpan(final String operationName, final String remotePeer) {
        AbstractSpan exitSpan;
        AbstractSpan parentSpan = peek();
        if (parentSpan != null && parentSpan.isExit()) {
            exitSpan = parentSpan;
        } else {
            final int parentSpanId = parentSpan == null ? -1 : parentSpan.getSpanId();
            exitSpan = (AbstractSpan)DictionaryManager.findApplicationCodeSection()
                    .find(remotePeer).doInCondition(
                            new PossibleFound.FoundAndObtain() {
                                @Override
                                public Object doProcess(final int peerId) {
                                    if (isLimitMechanismWorking()) {
                                        return new NoopExitSpan(peerId);
                                    }

                                    return DictionaryManager.findOperationNameCodeSection()
                                            .findOnly(segment.getApplicationId(), operationName)
                                            .doInCondition(
                                                    new PossibleFound.FoundAndObtain() {
                                                        @Override
                                                        public Object doProcess(int operationId) {
                                                            return new ExitSpan(spanIdGenerator++, parentSpanId, operationId, peerId);
                                                        }
                                                    }, new PossibleFound.NotFoundAndObtain() {
                                                        @Override
                                                        public Object doProcess() {
                                                            return new ExitSpan(spanIdGenerator++, parentSpanId, operationName, peerId);
                                                        }
                                                    });
                                }
                            },
                            new PossibleFound.NotFoundAndObtain() {
                                @Override
                                public Object doProcess() {
                                    if (isLimitMechanismWorking()) {
                                        return new NoopExitSpan(remotePeer);
                                    }

                                    return DictionaryManager.findOperationNameCodeSection()
                                            .findOnly(segment.getApplicationId(), operationName)
                                            .doInCondition(
                                                    new PossibleFound.FoundAndObtain() {
                                                        @Override
                                                        public Object doProcess(int operationId) {
                                                            return new ExitSpan(spanIdGenerator++, parentSpanId, operationId, remotePeer);
                                                        }
                                                    }, new PossibleFound.NotFoundAndObtain() {
                                                        @Override
                                                        public Object doProcess() {
                                                            return new ExitSpan(spanIdGenerator++, parentSpanId, operationName, remotePeer);
                                                        }
                                                    });
                                }
                            });
            push(exitSpan);
        }
        exitSpan.start();
        return exitSpan;
    }

    @Override
    public AbstractSpan activeSpan() {
        AbstractSpan span = peek();
        if (span == null) {
            throw new IllegalStateException("No active span.");
        }
        return span;
    }

    /**
     * Stop the given span, if and only if this one is the top element of {@link #activeSpanStack}. Because the tracing
     * core must make sure the span must match in a stack com.zl.skypointer.apm.collector.core, like any program did.
     *
     * @param span to finish
     */
    @Override
    public void stopSpan(AbstractSpan span) {
        AbstractSpan lastSpan = peek();
        if (lastSpan == span) {
            if (lastSpan instanceof AbstractTracingSpan) {
                AbstractTracingSpan toFinishSpan = (AbstractTracingSpan)lastSpan;
                if (toFinishSpan.finish(segment)) {
                    pop();
                }
            } else {
                pop();
            }
        } else {
            throw new IllegalStateException("Stopping the unexpected span = " + span);
        }

        if (activeSpanStack.isEmpty()) {
            this.finish();
        }
    }

    /**
     * Finish this context, and notify all {@link TracingContextListener}s, managed by {@link
     * TracingContext.ListenerManager}
     */
    private void finish() {
        TraceSegment finishedSegment = segment.finish(isLimitMechanismWorking());
        /**
         * Recheck the segment if the segment contains only one span.
         * Because in the runtime, can't sure this segment is part of distributed trace.
         *
         * @see {@link #createSpan(String, long, boolean)}
         */
        if (!segment.hasRef() && segment.isSingleSpanSegment()) {
            if (!samplingService.trySampling()) {
                finishedSegment.setIgnore(true);
            }
        }
        TracingContext.ListenerManager.notifyFinish(finishedSegment);
    }
    /**
     * The <code>ListenerManager</code> represents an event notify for every registered listener, which are notified
     * when the <cdoe>TracingContext</cdoe> finished, and {@link #segment} is ready for further process.
     */
    public static class ListenerManager {
        private static List<TracingContextListener> LISTENERS = new LinkedList<TracingContextListener>();

        /**
         * Add the given {@link TracingContextListener} to {@link #LISTENERS} list.
         *
         * @param listener the new listener.
         */
        public static synchronized void add(TracingContextListener listener) {
            LISTENERS.add(listener);
        }

        /**
         * Notify the {@link TracingContext.ListenerManager} about the given {@link TraceSegment} have finished. And
         * trigger {@link TracingContext.ListenerManager} to notify all {@link #LISTENERS} 's {@link
         * TracingContextListener#afterFinished(TraceSegment)}
         *
         * @param finishedSegment
         */
        static void notifyFinish(TraceSegment finishedSegment) {
            for (TracingContextListener listener : LISTENERS) {
                listener.afterFinished(finishedSegment);
            }
        }

        /**
         * Clear the given {@link TracingContextListener}
         */
        public static synchronized void remove(TracingContextListener listener) {
            LISTENERS.remove(listener);
        }

    }
    /**
     * @return the top element of 'ActiveSpanStack', and remove it.
     */
    private AbstractSpan pop() {
        return activeSpanStack.removeLast();
    }

    /**
     * Add a new Span at the top of 'ActiveSpanStack'
     *
     * @param span
     */
    private AbstractSpan push(AbstractSpan span) {
        activeSpanStack.addLast(span);
        return span;
    }
    /**
     * @return the top element of 'ActiveSpanStack' only.
     */
    private AbstractSpan peek() {
        if (activeSpanStack.isEmpty()) {
            return null;
        }
        return activeSpanStack.getLast();
    }

    private AbstractSpan first() {
        return activeSpanStack.getFirst();
    }

    private boolean isLimitMechanismWorking() {
        return spanIdGenerator >= Config.Agent.SPAN_LIMIT_PER_SEGMENT;
    }
}
