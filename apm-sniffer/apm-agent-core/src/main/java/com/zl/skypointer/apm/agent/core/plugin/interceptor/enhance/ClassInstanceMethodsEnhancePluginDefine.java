package com.zl.skypointer.apm.agent.core.plugin.interceptor.enhance;

import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import com.zl.skypointer.apm.agent.core.plugin.AbstractClassEnhancePluginDefine;
import com.zl.skypointer.apm.agent.core.plugin.EnhanceContext;
import com.zl.skypointer.apm.agent.core.plugin.PluginException;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.EnhanceException;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.zl.skypointer.apm.agent.core.plugin.interceptor.StaticMethodsInterceptPoint;
import com.zl.skypointr.apm.util.StringUtil;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Morph;

import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * 控制增强操作
 * This class controls all enhance operations, including enhance constructors, instance methods and static methods. All
 * the enhances base on three types interceptor point: {@link ConstructorInterceptPoint}, {@link
 * InstanceMethodsInterceptPoint} and {@link StaticMethodsInterceptPoint} If plugin is going to enhance constructors,
 * instance methods, or both, {@link ClassEnhancePluginDefine} will add a field of {@link
 * Object} type.
 *
 * @author zhouliang
 * @version v1.0 2018/1/10 23:44
 * @since JDK1.8
 */
public abstract class ClassInstanceMethodsEnhancePluginDefine extends AbstractClassEnhancePluginDefine {
    private static final ILog logger = LogManager.getLogger(ClassEnhancePluginDefine.class);

    /**
     * New field name.
     */
    public static final String CONTEXT_ATTR_NAME = "_$EnhancedClassField_ws";

    /**
     * 调用完此方法后，仅意味定义完成
     * Begin to define how to enhance class.
     * After invoke this method, only means definition is finished.
     *
     * @param enhanceOriginClassName target class name
     * @param newClassBuilder        byte-buddy's builder to manipulate class bytecode.
     * @return new byte-buddy's builder for further manipulation.
     */
    @Override
    protected DynamicType.Builder<?> enhance(String enhanceOriginClassName,
                                             DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader,
                                             EnhanceContext context) throws PluginException {
        newClassBuilder = this.enhanceClass(enhanceOriginClassName, newClassBuilder, classLoader);

        newClassBuilder = this.enhanceInstance(enhanceOriginClassName, newClassBuilder, classLoader, context);

        return newClassBuilder;
    }

    /**
     * 增强一个类来拦截构造器和类实例方法
     * Enhance a class to intercept constructors and class instance methods.
     *
     * @param enhanceOriginClassName target class name 目标类
     * @param newClassBuilder byte-buddy's builder to manipulate class bytecode.
     * @return new byte-buddy's builder for further manipulation.
     */
    private DynamicType.Builder<?> enhanceInstance(String enhanceOriginClassName,
                                                   DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader,
                                                   EnhanceContext context) throws PluginException {

        ConstructorInterceptPoint[] constructorInterceptPoints = getConstructorsInterceptPoints();
        InstanceMethodsInterceptPoint[] instanceMethodsInterceptPoints = getInstanceMethodsInterceptPoints();

        boolean existedConstructorInterceptPoint = false;
        if (constructorInterceptPoints != null && constructorInterceptPoints.length > 0) {
            existedConstructorInterceptPoint = true;
        }
        boolean existedMethodsInterceptPoints = false;
        if (instanceMethodsInterceptPoints != null && instanceMethodsInterceptPoints.length > 0) {
            existedMethodsInterceptPoints = true;
        }


        /**
         * nothing need to be enhanced in class instance, maybe need enhance static methods.
         */
        if (!existedConstructorInterceptPoint && !existedMethodsInterceptPoints) {
            return newClassBuilder;
        }

        /**
         * 操作类的源代码
         * Manipulate class source code.<br/>
         *
         * new class need:<br/>
         * 1.Add field, name {@link #CONTEXT_ATTR_NAME}.
         * 2.Add a field accessor for this field.
         *
         * And make sure the source codes manipulation only occurs once.
         *
         */
        if (!context.isObjectExtended()) {
            newClassBuilder = newClassBuilder.defineField(CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE)
                    .implement(EnhancedInstance.class)
                    .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
            context.extendObjectCompleted();
        }

        /**
         * 2. enhance constructors
         */
        if (existedConstructorInterceptPoint) {
            for (ConstructorInterceptPoint constructorInterceptPoint : constructorInterceptPoints) {
                newClassBuilder = newClassBuilder.constructor(constructorInterceptPoint.getConstructorMatcher()).intercept(SuperMethodCall.INSTANCE
                        .andThen(MethodDelegation.withDefaultConfiguration()
                                .to(new ConstructorInter(constructorInterceptPoint.getConstructorInterceptor(), classLoader))
                        )
                );
            }
        }

        /**
         * 3. enhance instance methods
         */
        if (existedMethodsInterceptPoints) {
            for (InstanceMethodsInterceptPoint instanceMethodsInterceptPoint : instanceMethodsInterceptPoints) {
                String interceptor = instanceMethodsInterceptPoint.getMethodsInterceptor();
                if (StringUtil.isEmpty(interceptor)) {
                    throw new EnhanceException("no InstanceMethodsAroundInterceptor define to enhance class " + enhanceOriginClassName);
                }

                if (instanceMethodsInterceptPoint.isOverrideArgs()) {
                    newClassBuilder =
                            newClassBuilder.method(not(isStatic()).and(instanceMethodsInterceptPoint.getMethodsMatcher()))
                                    .intercept(
                                            MethodDelegation.withDefaultConfiguration()
                                                    .withBinders(
                                                            Morph.Binder.install(OverrideCallable.class)
                                                    )
                                                    .to(new InstMethodsInterWithOverrideArgs(interceptor, classLoader))
                                    );
                } else {
                    newClassBuilder =
                            newClassBuilder.method(not(isStatic()).and(instanceMethodsInterceptPoint.getMethodsMatcher()))
                                    .intercept(
                                            MethodDelegation.withDefaultConfiguration()
                                                    .to(new InstMethodsInter(interceptor, classLoader))
                                    );
                }
            }
        }

        return newClassBuilder;
    }

    /**
     * Enhance a class to intercept class static methods.
     *
     * @param enhanceOriginClassName target class name
     * @param newClassBuilder byte-buddy's builder to manipulate class bytecode.
     * @return new byte-buddy's builder for further manipulation.
     */
    private DynamicType.Builder<?> enhanceClass(String enhanceOriginClassName,
                                                DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader) throws PluginException {
        StaticMethodsInterceptPoint[] staticMethodsInterceptPoints = getStaticMethodsInterceptPoints();

        if (staticMethodsInterceptPoints == null || staticMethodsInterceptPoints.length == 0) {
            return newClassBuilder;
        }

        for (StaticMethodsInterceptPoint staticMethodsInterceptPoint : staticMethodsInterceptPoints) {
            String interceptor = staticMethodsInterceptPoint.getMethodsInterceptor();
            if (StringUtil.isEmpty(interceptor)) {
                throw new EnhanceException("no StaticMethodsAroundInterceptor define to enhance class " + enhanceOriginClassName);
            }

            if (staticMethodsInterceptPoint.isOverrideArgs()) {
                newClassBuilder = newClassBuilder.method(isStatic().and(staticMethodsInterceptPoint.getMethodsMatcher()))
                        .intercept(
                                MethodDelegation.withDefaultConfiguration()
                                        .withBinders(
                                                Morph.Binder.install(OverrideCallable.class)
                                        )
                                        .to(new StaticMethodsInterWithOverrideArgs(interceptor))
                        );
            } else {
                newClassBuilder = newClassBuilder.method(isStatic().and(staticMethodsInterceptPoint.getMethodsMatcher()))
                        .intercept(
                                MethodDelegation.withDefaultConfiguration()
                                        .to(new StaticMethodsInter(interceptor))
                        );
            }

        }

        return newClassBuilder;
    }

    /**
     * Constructor methods intercept point. See {@link ConstructorInterceptPoint}
     *
     * @return collections of {@link ConstructorInterceptPoint}
     */
    protected abstract ConstructorInterceptPoint[] getConstructorsInterceptPoints();

    /**
     * Instance methods intercept point. See {@link InstanceMethodsInterceptPoint}
     *
     * @return collections of {@link InstanceMethodsInterceptPoint}
     */
    protected abstract InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints();

    /**
     * Static methods intercept point. See {@link StaticMethodsInterceptPoint}
     *
     * @return collections of {@link StaticMethodsInterceptPoint}
     */
    protected abstract StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints();
}
