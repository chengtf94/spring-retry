package org.springframework.retry.backoff;

import org.springframework.retry.RetryContext;

/**
 * 退避策略基类
 *
 * @author Rob Harrop
 * @author Dave Syer
 */
public abstract class StatelessBackOffPolicy implements BackOffPolicy {

	@Override
	public BackOffContext start(RetryContext status) {
		return null;
	}

	@Override
	public final void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
		doBackOff();
	}

	/** 执行退避操作 */
	protected abstract void doBackOff() throws BackOffInterruptedException;

}
