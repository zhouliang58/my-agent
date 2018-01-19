package com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/11 23:32
 * @since JDK1.8
 */
public class MethodInterceptResult {
    private boolean isContinue = true;

    private Object ret = null;

    /**
     * define the new return value.
     *
     * @param ret new return value.
     */
    public void defineReturnValue(Object ret) {
        this.isContinue = false;
        this.ret = ret;
    }

    /**
     * @return true, will trigger method interceptor({@link InstMethodsInter} and {@link StaticMethodsInter}) to invoke
     * the origin method. Otherwise, not.
     */
    public boolean isContinue() {
        return isContinue;
    }

    /**
     * @return the new return value.
     */
    Object _ret() {
        return ret;
    }
}
