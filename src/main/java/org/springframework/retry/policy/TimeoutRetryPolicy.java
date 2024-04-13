package org.springframework.retry.policy;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;

/**
 * 超时时间重试策略：在指定的超时时间内允许重试
 *
 * @author Dave Syer
 *
 */
@SuppressWarnings("serial")
public class TimeoutRetryPolicy implements RetryPolicy {

	/** 默认超时时间 */
	public static final long DEFAULT_TIMEOUT = 1000;

	/** 超时时间 */
	private long timeout = DEFAULT_TIMEOUT;
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public long getTimeout() {
		return timeout;
	}

	@Override
	public RetryContext open(RetryContext parent) {
		return new TimeoutRetryContext(parent, timeout);
	}
	private static class TimeoutRetryContext extends RetryContextSupport {
		/** 超时时间、起始时间戳 */
		private long timeout;
		private long start;
		public TimeoutRetryContext(RetryContext parent, long timeout) {
			super(parent);
			this.start = System.currentTimeMillis();
			this.timeout = timeout;
		}
		public boolean isAlive() {
			return (System.currentTimeMillis() - start) <= timeout;
		}
	}

	@Override
	public boolean canRetry(RetryContext context) {
		return ((TimeoutRetryContext) context).isAlive();
	}

	@Override
	public void close(RetryContext context) {
	}

	@Override
	public void registerThrowable(RetryContext context, Throwable throwable) {
		((RetryContextSupport) context).registerThrowable(throwable);
	}

}
