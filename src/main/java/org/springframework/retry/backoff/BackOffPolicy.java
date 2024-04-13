package org.springframework.retry.backoff;

import org.springframework.retry.RetryContext;

/**
 * 退避策略接口
 *
 * @author Rob Harrop
 * @author Dave Syer
 */
public interface BackOffPolicy {

	/** 基于重试策略上下文创建退避策略上下文 */
	BackOffContext start(RetryContext context);

	/** 基于退避策略上下文执行退避操作 */
	void backOff(BackOffContext backOffContext) throws BackOffInterruptedException;

}
