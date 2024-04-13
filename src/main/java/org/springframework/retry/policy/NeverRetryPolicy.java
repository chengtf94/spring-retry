package org.springframework.retry.policy;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;

/**
 * 不允许重试策略：只允许调用RetryCallback一次
 *
 * @author Dave Syer
 *
 */
@SuppressWarnings("serial")
public class NeverRetryPolicy implements RetryPolicy {

	@Override
	public RetryContext open(RetryContext parent) {
		return new NeverRetryContext(parent);
	}

	@Override
	public boolean canRetry(RetryContext context) {
		return !((NeverRetryContext) context).isFinished();
	}

	@Override
	public void close(RetryContext context) {
	}

	@Override
	public void registerThrowable(RetryContext context, Throwable throwable) {
		((NeverRetryContext) context).setFinished();
		((RetryContextSupport) context).registerThrowable(throwable);
	}

	/** 不允许重试策略上下文 */
	private static class NeverRetryContext extends RetryContextSupport {

		/** 是否已完成 */
		private boolean finished = false;
		public boolean isFinished() {
			return finished;
		}
		public void setFinished() {
			this.finished = true;
		}

		public NeverRetryContext(RetryContext parent) {
			super(parent);
		}

	}

}
