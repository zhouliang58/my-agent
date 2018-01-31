package com.zl.skypointer.apm.agent.core.context.trace;

import com.zl.skypointer.apm.agent.core.dictionary.DictionaryManager;
import com.zl.skypointer.apm.agent.core.dictionary.DictionaryUtil;
import com.zl.skypointer.apm.agent.core.dictionary.PossibleFound;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 11:05
 * @since JDK1.8
 */
public abstract class StackBasedTracingSpan extends AbstractTracingSpan {
    protected int stackDepth;

    protected StackBasedTracingSpan(int spanId, int parentSpanId, String operationName) {
        super(spanId, parentSpanId, operationName);
        this.stackDepth = 0;
    }

    protected StackBasedTracingSpan(int spanId, int parentSpanId, int operationId) {
        super(spanId, parentSpanId, operationId);
        this.stackDepth = 0;
    }

    @Override
    public boolean finish(TraceSegment owner) {
        if (--stackDepth == 0) {
            if (this.operationId == DictionaryUtil.nullValue()) {
                this.operationId = (Integer) DictionaryManager.findOperationNameCodeSection()
                        .findOrPrepare4Register(owner.getApplicationId(), operationName)
                        .doInCondition(
                                new PossibleFound.FoundAndObtain() {
                                    @Override public Object doProcess(int value) {
                                        return value;
                                    }
                                },
                                new PossibleFound.NotFoundAndObtain() {
                                    @Override public Object doProcess() {
                                        return DictionaryUtil.nullValue();
                                    }
                                }
                        );
            }
            return super.finish(owner);
        } else {
            return false;
        }
    }
}
