package org.springframework.retry;

import java.io.Serializable;

/**
 * 重试策略接口
 *
 * @author Dave Syer
 *
 */
public interface RetryPolicy extends Serializable {

	/** 打开重试上下文 */
	RetryContext open(RetryContext parent);

	/** 检查是否允许重试 */
	boolean canRetry(RetryContext context);

	/** 关闭重试上下文 */
	void close(RetryContext context);

	/** 注册重试后的异常 */
	void registerThrowable(RetryContext context, Throwable throwable);

}
