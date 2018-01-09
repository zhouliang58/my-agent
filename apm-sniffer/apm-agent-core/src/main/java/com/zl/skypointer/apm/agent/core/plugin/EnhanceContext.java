package com.zl.skypointer.apm.agent.core.plugin;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/9 16:46
 * @since JDK1.8
 */
public class EnhanceContext {
    private boolean isEnhanced = false;
    /**
     * The object has already been enhanced or extended.
     * e.g. added the new field, or implemented the new interface
     */
    private boolean objectExtended = false;

    public boolean isEnhanced() {
        return isEnhanced;
    }

    public void initializationStageCompleted() {
        isEnhanced = true;
    }

    public boolean isObjectExtended() {
        return objectExtended;
    }

    public void extendObjectCompleted() {
        objectExtended = true;
    }
}
