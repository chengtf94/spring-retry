package org.springframework.retry.backoff;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.retry.RetryContext;
import org.springframework.util.ClassUtils;

/**
 * 指数时间退避策略
 *
 * @author Rob Harrop
 * @author Dave Syer
 * @author Gary Russell
 * @author Artem Bilan
 */
@SuppressWarnings("serial")
public class ExponentialBackOffPolicy implements SleepingBackOffPolicy<ExponentialBackOffPolicy> {
	protected final Log logger = LogFactory.getLog(this.getClass());

	/** 初始退避周期（默认是0.1s）、最大退避周期（默认是30s）、乘数、等待策略（默认是Thread.sleep） */
	private volatile long initialInterval = DEFAULT_INITIAL_INTERVAL;
	public static final long DEFAULT_INITIAL_INTERVAL = 100L;
	private volatile long maxInterval = DEFAULT_MAX_INTERVAL;
	public static final long DEFAULT_MAX_INTERVAL = 30000L;
	private volatile double multiplier = DEFAULT_MULTIPLIER;
	public static final double DEFAULT_MULTIPLIER = 2;
	private Sleeper sleeper = new ThreadWaitSleeper();

	@Override
	public ExponentialBackOffPolicy withSleeper(Sleeper sleeper) {
		ExponentialBackOffPolicy res = newInstance();
		cloneValues(res);
		res.setSleeper(sleeper);
		return res;
	}
	protected ExponentialBackOffPolicy newInstance() {
		return new ExponentialBackOffPolicy();
	}
	protected void cloneValues(ExponentialBackOffPolicy target) {
		target.setInitialInterval(getInitialInterval());
		target.setMaxInterval(getMaxInterval());
		target.setMultiplier(getMultiplier());
		target.setSleeper(this.sleeper);
	}

	@Override
	public BackOffContext start(RetryContext context) {
		return new ExponentialBackOffContext(this.initialInterval, this.multiplier, this.maxInterval);
	}

	@Override
	public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
		ExponentialBackOffContext context = (ExponentialBackOffContext) backOffContext;
		try {
			long sleepTime = context.getSleepAndIncrement();
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Sleeping for " + sleepTime);
			}
			this.sleeper.sleep(sleepTime);
		} catch (InterruptedException e) {
			throw new BackOffInterruptedException("Thread interrupted while sleeping", e);
		}
	}

	/** 指数时间退避策略上下文 */
	static class ExponentialBackOffContext implements BackOffContext {

		/** 乘数、当前退避周期、最大退避周期 */
		private final double multiplier;
		private long interval;
		private long maxInterval;

		public ExponentialBackOffContext(long interval, double multiplier, long maxInterval) {
			this.interval = interval;
			this.multiplier = multiplier;
			this.maxInterval = maxInterval;
		}

		public synchronized long getSleepAndIncrement() {
			long sleep = this.interval;
			if (sleep > this.maxInterval) {
				sleep = this.maxInterval;
			} else {
				this.interval = getNextInterval();
			}
			return sleep;
		}

		protected long getNextInterval() {
			return (long) (this.interval * this.multiplier);
		}

		public double getMultiplier() {
			return this.multiplier;
		}
		public long getInterval() {
			return this.interval;
		}
		public long getMaxInterval() {
			return this.maxInterval;
		}

	}

	public void setSleeper(Sleeper sleeper) {
		this.sleeper = sleeper;
	}
	public void setInitialInterval(long initialInterval) {
		this.initialInterval = (initialInterval > 1 ? initialInterval : 1);
	}
	public void setMultiplier(double multiplier) {
		this.multiplier = (multiplier > 1.0 ? multiplier : 1.0);
	}
	public void setMaxInterval(long maxInterval) {
		this.maxInterval = maxInterval > 0 ? maxInterval : 1;
	}
	public long getInitialInterval() {
		return this.initialInterval;
	}
	public long getMaxInterval() {
		return this.maxInterval;
	}
	public double getMultiplier() {
		return this.multiplier;
	}

	@Override
	public String toString() {
		return ClassUtils.getShortName(getClass()) + "[initialInterval=" + this.initialInterval + ", multiplier="
				+ this.multiplier + ", maxInterval=" + this.maxInterval + "]";
	}

}
