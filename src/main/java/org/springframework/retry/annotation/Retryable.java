package org.springframework.retry.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可重试注解
 *
 * @author Dave Syer
 * @author Artem Bilan
 * @author Gary Russell
 * @author Maksim Kita
 * @since 1.1
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retryable {

	/** 重试拦截器Bean名称 */
	String interceptor() default "";

	/** 是否需要重试的异常类型数组，exclude也为空默认所有类型均重试 */
	Class<? extends Throwable>[] value() default {};
	Class<? extends Throwable>[] include() default {};
	Class<? extends Throwable>[] exclude() default {};

	/** 最大重试次数，默认为3 */
	int maxAttempts() default 3;

	/** 最大重试次数表达式 */
	String maxAttemptsExpression() default "";

	/** 退避延迟策略：用于自定义重试间隔 */
	Backoff backoff() default @Backoff();

	/** 重试失败后的恢复方法名称（本类中） */
	String recover() default "";

	/** 统计标识 */
	String label() default "";

	/** 是否保持重试状态 */
	boolean stateful() default false;

	/** 异常表达式 */
	String exceptionExpression() default "";

	/** 重试监听器Bean名称数组 */
	String[] listeners() default {};

}
