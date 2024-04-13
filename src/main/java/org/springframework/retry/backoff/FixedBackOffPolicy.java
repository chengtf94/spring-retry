package org.springframework.retry.backoff;

/**
 * 固定时间退避策略：默认退避周期为1s，等待策略为Thread.sleep
 *
 * @author Rob Harrop
 * @author Dave Syer
 * @author Artem Bilan
 */
public class FixedBackOffPolicy extends StatelessBackOffPolicy implements SleepingBackOffPolicy<FixedBackOffPolicy> {

	/** 退避周期（默认是1s）、等待策略（默认是Thread.sleep） */
	private volatile long backOffPeriod = DEFAULT_BACK_OFF_PERIOD;
	private static final long DEFAULT_BACK_OFF_PERIOD = 1000L;
	private Sleeper sleeper = new ThreadWaitSleeper();

	@Override
	public FixedBackOffPolicy withSleeper(Sleeper sleeper) {
		FixedBackOffPolicy res = new FixedBackOffPolicy();
		res.setBackOffPeriod(backOffPeriod);
		res.setSleeper(sleeper);
		return res;
	}

	@Override
	protected void doBackOff() throws BackOffInterruptedException {
		try {
			sleeper.sleep(backOffPeriod);
		} catch (InterruptedException e) {
			throw new BackOffInterruptedException("Thread interrupted while sleeping", e);
		}
	}

	public void setBackOffPeriod(long backOffPeriod) {
		this.backOffPeriod = (backOffPeriod > 0 ? backOffPeriod : 1);
	}
	public long getBackOffPeriod() {
		return backOffPeriod;
	}
	public void setSleeper(Sleeper sleeper) {
		this.sleeper = sleeper;
	}

	public String toString() {
		return "FixedBackOffPolicy[backOffPeriod=" + backOffPeriod + "]";
	}

}
