package org.springframework.retry.backoff;

import java.util.Random;

/**
 * 随机时间退避策略
 *
 * @author Rob Harrop
 * @author Dave Syer
 */
public class UniformRandomBackOffPolicy extends StatelessBackOffPolicy
		implements SleepingBackOffPolicy<UniformRandomBackOffPolicy> {

	/** 最小退避周期（默认是0.5s）、最大退避周期（默认是1.5s）、随机数对象、等待策略（默认是Thread.sleep） */
	private volatile long minBackOffPeriod = DEFAULT_BACK_OFF_MIN_PERIOD;
	private volatile long maxBackOffPeriod = DEFAULT_BACK_OFF_MAX_PERIOD;
	private static final long DEFAULT_BACK_OFF_MIN_PERIOD = 500L;
	private static final long DEFAULT_BACK_OFF_MAX_PERIOD = 1500L;
	private Random random = new Random(System.currentTimeMillis());
	private Sleeper sleeper = new ThreadWaitSleeper();

	@Override
	public UniformRandomBackOffPolicy withSleeper(Sleeper sleeper) {
		UniformRandomBackOffPolicy res = new UniformRandomBackOffPolicy();
		res.setMinBackOffPeriod(minBackOffPeriod);
		res.setSleeper(sleeper);
		return res;
	}

	@Override
	protected void doBackOff() throws BackOffInterruptedException {
		try {
			long delta = maxBackOffPeriod == minBackOffPeriod
					? 0
					: random.nextInt((int) (maxBackOffPeriod - minBackOffPeriod));
			sleeper.sleep(minBackOffPeriod + delta);
		} catch (InterruptedException e) {
			throw new BackOffInterruptedException("Thread interrupted while sleeping", e);
		}
	}

	public void setSleeper(Sleeper sleeper) {
		this.sleeper = sleeper;
	}
	public void setMinBackOffPeriod(long backOffPeriod) {
		this.minBackOffPeriod = (backOffPeriod > 0 ? backOffPeriod : 1);
	}
	public long getMinBackOffPeriod() {
		return minBackOffPeriod;
	}
	public void setMaxBackOffPeriod(long backOffPeriod) {
		this.maxBackOffPeriod = (backOffPeriod > 0 ? backOffPeriod : 1);
	}
	public long getMaxBackOffPeriod() {
		return maxBackOffPeriod;
	}

	public String toString() {
		return "RandomBackOffPolicy[backOffPeriod=" + minBackOffPeriod + ", " + maxBackOffPeriod + "]";
	}

}
