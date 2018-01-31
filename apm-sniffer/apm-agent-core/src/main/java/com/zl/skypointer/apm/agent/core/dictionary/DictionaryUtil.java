package com.zl.skypointer.apm.agent.core.dictionary;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 9:15
 * @since JDK1.8
 */
public class DictionaryUtil {
    public static int nullValue() {
        return 0;
    }

    public static boolean isNull(int id) {
        return id == nullValue();
    }
}
