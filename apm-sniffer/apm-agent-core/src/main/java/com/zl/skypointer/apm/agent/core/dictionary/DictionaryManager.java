package com.zl.skypointer.apm.agent.core.dictionary;

/**
 * //TODO
 *
 * @author zhouliang
 * @version v1.0 2018/1/31 15:17
 * @since JDK1.8
 */
public class DictionaryManager {
    /**
     * @return {@link ApplicationDictionary} to find application id for application code and network address.
     */
    public static ApplicationDictionary findApplicationCodeSection() {
        return ApplicationDictionary.INSTANCE;
    }

    /**
     * @return {@link OperationNameDictionary} to find service id.
     */
    public static OperationNameDictionary findOperationNameCodeSection() {
        return OperationNameDictionary.INSTANCE;
    }
}
