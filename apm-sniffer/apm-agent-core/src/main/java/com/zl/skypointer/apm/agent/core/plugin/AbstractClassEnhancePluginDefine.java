package com.zl.skypointer.apm.agent.core.plugin;

import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import com.zl.skypointer.apm.agent.core.plugin.match.ClassMatch;
import com.zl.skypointr.apm.util.StringUtil;
import net.bytebuddy.dynamic.DynamicType;

/**
 * SkyWalking 类增强插件定义抽象基类。它注重在定义( Define )的抽象与实现。
 * Basic abstract class of all sky-walking auto-instrumentation plugins.
 * <p>
 * It provides the outline of enhancing the target class.
 * If you want to know more about enhancing, you should go to see  ClassEnhancePluginDefine
 *
 * @author zhouliang
 * @version v1.0 2018/1/8 23:30
 * @since JDK1.8
 */
public abstract class AbstractClassEnhancePluginDefine {

    private static final ILog logger = LogManager.getLogger(AbstractClassEnhancePluginDefine.class);

    /**
     * 设置 net.bytebuddy.dynamic.DynamicType.Builder 对象。
     * 通过该对象，定义如何拦截需要修改的目标 Java 类(方法的 transformClassName 参数)
     *
     * Main entrance of enhancing the class.
     *
     * @param transformClassName target class.
     * @param builder byte-buddy's builder to manipulate target class's bytecode.
     * @param classLoader load the given transformClass
     * @return the new builder, or <code>null</code> if not be enhanced.
     * @throws PluginException, when set builder failure.
     */
    public DynamicType.Builder<?> define(String transformClassName,
                                         DynamicType.Builder<?> builder, ClassLoader classLoader, EnhanceContext context) throws PluginException {
        /**
         * transformClassName——org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
         * interceptorDefineClassName——org.apache.skywalking.apm.plugin.spring.patch.define.AutowiredAnnotationProcessorInstrumentation
         */
        String interceptorDefineClassName = this.getClass().getName();

        if (StringUtil.isEmpty(transformClassName)) {
            logger.warn("classname of being intercepted is not defined by {}.", interceptorDefineClassName);
            return null;
        }

        logger.debug("prepare to enhance class {} by {}.", transformClassName, interceptorDefineClassName);

        /**
         * find witness classes for enhance class
         */
        String[] witnessClasses = witnessClasses();
        if (witnessClasses != null) {
            for (String witnessClass : witnessClasses) {
                if (!WitnessClassFinder.INSTANCE.exist(witnessClass, classLoader)) {
                    logger.warn("enhance class {} by plugin {} is not working. Because witness class {} is not existed.", transformClassName, interceptorDefineClassName,
                            witnessClass);
                    return null;
                }
            }
        }

        /**
         * 调用 #enhance(...) 抽象方法，使用拦截器增强目标类
         * find origin class source code for interceptor
         */
        DynamicType.Builder<?> newClassBuilder = this.enhance(transformClassName, builder, classLoader, context);

        context.initializationStageCompleted();
        logger.debug("enhance class {} by {} completely.", transformClassName, interceptorDefineClassName);

        return newClassBuilder;
    }

    protected abstract DynamicType.Builder<?> enhance(String enhanceOriginClassName,
                                                      DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader, EnhanceContext context) throws PluginException;

    /**
     * 定义了类匹配( ClassMatch )
     * Define the {@link ClassMatch} for filtering class.
     *
     * @return {@link ClassMatch}
     */
    protected abstract ClassMatch enhanceClass();

    /**
     * 见证类列表。当且仅当应用存在见证类列表，插件才生效。什么意思？让我们看看这种情况：
     * 一个类库存在两个发布的版本( 如 1.0 和 2.0 )，其中包括相同的目标类，但不同的方法或不同的方法参数列表。
     * 所以我们需要根据库的不同版本使用插件的不同版本。然而版本显然不是一个选项，这时需要使用见证类列表，判断出当前引用类库的发布版本。
     *
     * Witness classname list. Why need witness classname? Let's see like this: A library existed two released versions
     * (like 1.0, 2.0), which include the same target classes, but because of version iterator, they may have the same
     * name, but different methods, or different method arguments list. So, if I want to target the particular version
     * (let's say 1.0 for example), version number is obvious not an option, this is the moment you need "Witness
     * classes". You can add any classes only in this particular release version ( something like class
     * com.company.1.x.A, only in 1.0 ), and you can achieve the goal.
     *
     * @return
     */
    protected String[] witnessClasses() {
        return new String[] {};
    }
}
