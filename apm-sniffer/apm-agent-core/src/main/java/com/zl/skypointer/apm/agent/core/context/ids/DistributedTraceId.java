package com.zl.skypointer.apm.agent.core.context.ids;

import com.zl.skypointer.apm.network.proto.UniqueId;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 10:21
 * @since JDK1.8
 */
public class DistributedTraceId {
    private ID id;

    public DistributedTraceId(ID id) {
        this.id = id;
    }

    public DistributedTraceId(String id) {
        this.id = new ID(id);
    }

    public String encode() {
        return id.encode();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public UniqueId toUniqueId() {
        return id.transform();
    }

    /**
     * Compare the two <code>DistributedTraceId</code> by its {@link #id},
     * even these two <code>DistributedTraceId</code>s are not the same instances.
     *
     * @param o target <code>DistributedTraceId</code>
     * @return return if they have the same {@link #id}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DistributedTraceId id1 = (DistributedTraceId)o;

        return id != null ? id.equals(id1.id) : id1.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
