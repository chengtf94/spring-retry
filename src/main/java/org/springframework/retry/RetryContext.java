package org.springframework.retry;

import org.springframework.core.AttributeAccessor;

/**
 * 重试上下文接口
 *
 * @author Dave Syer
 *
 */
public interface RetryContext extends AttributeAccessor {

	String NAME = "context.name";
	String STATE_KEY = "context.state";
	String CLOSED = "context.closed";
	String RECOVERED = "context.recovered";
	String EXHAUSTED = "context.exhausted";

	/** 设置终止（不再允许重试） */
	void setExhaustedOnly();

	/** 判断是否终止（不再允许重试） */
	boolean isExhaustedOnly();

	/** 获取父重试上下文 */
	RetryContext getParent();

	/** 获取重试次数 */
	int getRetryCount();

	/** 获取导致当前重试的异常对象 */
	Throwable getLastThrowable();

}
