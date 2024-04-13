package org.springframework.retry.backoff;

import org.springframework.retry.RetryContext;

import java.util.Random;

/**
 * 随机指数时间退避策略
 *
 * @author Jon Travis
 * @author Dave Syer
 * @author Chase Diem
 */
@SuppressWarnings("serial")
public class ExponentialRandomBackOffPolicy extends ExponentialBackOffPolicy {

	@Override
	public BackOffContext start(RetryContext context) {
		return new ExponentialRandomBackOffContext(getInitialInterval(), getMultiplier(), getMaxInterval());
	}
	protected ExponentialBackOffPolicy newInstance() {
		return new ExponentialRandomBackOffPolicy();
	}

	/** 随机指数时间退避策略 */
	static class ExponentialRandomBackOffContext extends ExponentialBackOffPolicy.ExponentialBackOffContext {

		private final Random r = new Random();

		public ExponentialRandomBackOffContext(long expSeed, double multiplier, long maxInterval) {
			super(expSeed, multiplier, maxInterval);
		}

		@Override
		public synchronized long getSleepAndIncrement() {
			long next = super.getSleepAndIncrement();
			next = (long) (next * (1 + r.nextFloat() * (getMultiplier() - 1)));
			if (next > super.getMaxInterval()) {
				next = super.getMaxInterval();
			}
			return next;
		}

	}

}
