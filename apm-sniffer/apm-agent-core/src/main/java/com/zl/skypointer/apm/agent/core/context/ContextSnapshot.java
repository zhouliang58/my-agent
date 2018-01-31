package com.zl.skypointer.apm.agent.core.context;

import com.zl.skypointer.apm.agent.core.context.ids.DistributedTraceId;
import com.zl.skypointer.apm.agent.core.context.ids.ID;
import com.zl.skypointer.apm.agent.core.dictionary.DictionaryUtil;
import com.zl.skypointr.apm.util.StringUtil;

import java.util.List;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 11:35
 * @since JDK1.8
 */
public class ContextSnapshot {
    /**
     * trace segment id of the parent trace segment.
     */
    private ID traceSegmentId;

    /**
     * span id of the parent span, in parent trace segment.
     */
    private int spanId = -1;

    private String entryOperationName;

    private String parentOperationName;

    /**
     * {@link DistributedTraceId}
     */
    private DistributedTraceId primaryDistributedTraceId;

    private int entryApplicationInstanceId = DictionaryUtil.nullValue();

    ContextSnapshot(ID traceSegmentId, int spanId,
                    List<DistributedTraceId> distributedTraceIds) {
        this.traceSegmentId = traceSegmentId;
        this.spanId = spanId;
        if (distributedTraceIds != null) {
            this.primaryDistributedTraceId = distributedTraceIds.get(0);
        }
    }

    public void setEntryOperationName(String entryOperationName) {
        this.entryOperationName = "#" + entryOperationName;
    }

    public void setEntryOperationId(int entryOperationId) {
        this.entryOperationName = entryOperationId + "";
    }

    public void setParentOperationName(String parentOperationName) {
        this.parentOperationName = "#" + parentOperationName;
    }

    public void setParentOperationId(int parentOperationId) {
        this.parentOperationName = parentOperationId + "";
    }

    public DistributedTraceId getDistributedTraceId() {
        return primaryDistributedTraceId;
    }

    public ID getTraceSegmentId() {
        return traceSegmentId;
    }

    public int getSpanId() {
        return spanId;
    }

    public String getParentOperationName() {
        return parentOperationName;
    }

    public boolean isValid() {
        return traceSegmentId != null
                && spanId > -1
                && entryApplicationInstanceId != DictionaryUtil.nullValue()
                && primaryDistributedTraceId != null
                && !StringUtil.isEmpty(entryOperationName)
                && !StringUtil.isEmpty(parentOperationName);
    }

    public String getEntryOperationName() {
        return entryOperationName;
    }

    public void setEntryApplicationInstanceId(int entryApplicationInstanceId) {
        this.entryApplicationInstanceId = entryApplicationInstanceId;
    }

    public int getEntryApplicationInstanceId() {
        return entryApplicationInstanceId;
    }

    public boolean isFromCurrent() {
        return traceSegmentId.equals(ContextManager.capture().getTraceSegmentId());
    }
}
