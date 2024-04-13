package org.springframework.retry.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.retry.backoff.BackOffPolicy;

/**
 * 退避策略注解
 *
 * @author Dave Syer
 * @author Gary Russell
 * @since 1.1
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Backoff {

	/** 退避周期 */
	long value() default 1000;

	/** 退避周期 */
	long delay() default 0;

	/** 最大退避周期 */
	long maxDelay() default 0;

	/** 乘数 */
	double multiplier() default 0;

	/** 延迟表达式 */
	String delayExpression() default "";

	/** 最大延迟表达式 */
	String maxDelayExpression() default "";

	/** 乘数表达式 */
	String multiplierExpression() default "";

	/** 是否随机 */
	boolean random() default false;

}
