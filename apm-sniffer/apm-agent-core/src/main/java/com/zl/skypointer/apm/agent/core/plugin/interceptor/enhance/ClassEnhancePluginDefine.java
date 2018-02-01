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
 * SkyWalking 类增强插件定义抽象类。它注重在增强( Enhance )的抽象与实现。
 *
 * @author zhouliang
 * @version v1.0 2018/1/10 23:43
 * @since JDK1.8
 */
public abstract class ClassEnhancePluginDefine extends AbstractClassEnhancePluginDefine {
    private static final ILog logger = LogManager.getLogger(ClassEnhancePluginDefine.class);

    /**
     * New field name.
     */
    public static final String CONTEXT_ATTR_NAME = "_$EnhancedClassField_ws";

    /**
     * Begin to define how to enhance class.
     * After invoke this method, only means definition is finished.
     *
     * @param enhanceOriginClassName target class name
     * @param newClassBuilder byte-buddy's builder to manipulate class bytecode.
     * @return new byte-buddy's builder for further manipulation.
     */
    @Override
    protected DynamicType.Builder<?> enhance(String enhanceOriginClassName,
                                             DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader,
                                             EnhanceContext context) throws PluginException {
        /**
         * 增强静态方法
         */
        newClassBuilder = this.enhanceClass(enhanceOriginClassName, newClassBuilder, classLoader);

        /**
         * 增强构造方法和实例方法
         */
        newClassBuilder = this.enhanceInstance(enhanceOriginClassName, newClassBuilder, classLoader, context);

        return newClassBuilder;
    }

    /**
     * Enhance a class to intercept constructors and class instance methods.
     *
     * @param enhanceOriginClassName target class name
     * @param newClassBuilder byte-buddy's builder to manipulate class bytecode.
     * @return new byte-buddy's builder for further manipulation.
     */
    private DynamicType.Builder<?> enhanceInstance(String enhanceOriginClassName,
                                                   DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader,
                                                   EnhanceContext context) throws PluginException {
        /**
         * 调用 #getConstructorsInterceptPoints() / #getInstanceMethodsInterceptPoints() 方法，
         * 获得 ConstructorInterceptPoint / InstanceMethodsInterceptPoint 数组。若都为空，不进行增强。
         */
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
         * Manipulate class source code.<br/>
         *
         * new class need:<br/>
         * 1.Add field, name {@link #CONTEXT_ATTR_NAME}.
         * 2.Add a field accessor for this field.
         *
         * And make sure the source codes manipulation only occurs once.
         *
         * 为目标 Java 类“自动”实现 org.skywalking.apm.agent.core.plugin.interceptor.enhance.EnhancedInstance 接口。
         * 这样，目标 Java 类就有一个私有变量，拦截器在执行过程中，可以存储状态到该私有变量。
         */
        if (!context.isObjectExtended()) {
            newClassBuilder = newClassBuilder.defineField(CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE)
                    .implement(EnhancedInstance.class)
                    .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
            context.extendObjectCompleted();
        }

        /**
         * 2. enhance constructors
         * 遍历 ConstructorInterceptPoint 数组，逐个增强 ConstructorInterceptPoint 对应的构造方法。使用 ConstructorInter 处理拦截逻辑
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
         * 遍历 InstanceMethodsInterceptPoint 数组，逐个增强 InstanceMethodsInterceptPoint 对应的实例方法。
         */
        if (existedMethodsInterceptPoints) {
            for (InstanceMethodsInterceptPoint instanceMethodsInterceptPoint : instanceMethodsInterceptPoints) {
                /**
                 * 获得拦截器的类名。拦截器的实例，在 Inter 类里获取。
                 */
                String interceptor = instanceMethodsInterceptPoint.getMethodsInterceptor();
                if (StringUtil.isEmpty(interceptor)) {
                    throw new EnhanceException("no InstanceMethodsAroundInterceptor define to enhance class " + enhanceOriginClassName);
                }

                /**
                 * 当 InstanceMethodsInterceptPoint#isOverrideArgs() 方法返回 true 时，使用 InstMethodsInterWithOverrideArgs 处理拦截逻辑。
                 */
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
                }
                /**
                 * 当 InstanceMethodsInterceptPoint#isOverrideArgs() 方法返回 false 时，使用 InstMethodsInter 处理拦截逻辑
                 */
                else {
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
     * Enhance a class to intercept class static methods.
     *
     * @param enhanceOriginClassName target class name
     * @param newClassBuilder byte-buddy's builder to manipulate class bytecode.
     * @return new byte-buddy's builder for further manipulation.
     */
    private DynamicType.Builder<?> enhanceClass(String enhanceOriginClassName,
                                                DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader) throws PluginException {
        /**
         * 获得 StaticMethodsInterceptPoint 数组。若为空，不进行增强。
         */
        StaticMethodsInterceptPoint[] staticMethodsInterceptPoints = getStaticMethodsInterceptPoints();

        if (staticMethodsInterceptPoints == null || staticMethodsInterceptPoints.length == 0) {
            return newClassBuilder;
        }

        /**
         * 遍历 StaticMethodsInterceptPoint 数组，逐个增强StaticMethodsInterceptPoint 对应的静态方法。
         */
        for (StaticMethodsInterceptPoint staticMethodsInterceptPoint : staticMethodsInterceptPoints) {
            /**
             * 获得拦截器的类名。拦截器的实例，在 Inter 类里获取。
             */
            String interceptor = staticMethodsInterceptPoint.getMethodsInterceptor();
            if (StringUtil.isEmpty(interceptor)) {
                throw new EnhanceException("no StaticMethodsAroundInterceptor define to enhance class " + enhanceOriginClassName);
            }

            /**
             * 当 StaticMethodsInterceptPoint#isOverrideArgs() 方法返回 true 时，使用 StaticMethodsInterWithOverrideArgs 处理拦截逻辑。
             */
            if (staticMethodsInterceptPoint.isOverrideArgs()) {
                newClassBuilder = newClassBuilder.method(isStatic().and(staticMethodsInterceptPoint.getMethodsMatcher()))
                        .intercept(
                                MethodDelegation.withDefaultConfiguration()
                                        .withBinders(
                                                Morph.Binder.install(OverrideCallable.class)
                                        )
                                        .to(new StaticMethodsInterWithOverrideArgs(interceptor))
                        );
            }
            /**
             * 当 StaticMethodsInterceptPoint#isOverrideArgs() 方法返回 false 时，使用 StaticMethodsInter 处理拦截逻辑
             */
            else {
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
     * Static methods intercept point. See {@link StaticMethodsInterceptPoint}
     *
     * @return collections of {@link StaticMethodsInterceptPoint}
     */
    protected abstract StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints();
}

