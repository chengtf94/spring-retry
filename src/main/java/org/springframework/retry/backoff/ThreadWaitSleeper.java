package org.springframework.retry.backoff;

/**
 * 线程等待
 *
 * @author Artem Bilan
 * @since 1.1
 */
@SuppressWarnings("serial")
public class ThreadWaitSleeper implements Sleeper {

	@Override
	public void sleep(long backOffPeriod) throws InterruptedException {
		Thread.sleep(backOffPeriod);
	}

}
