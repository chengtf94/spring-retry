package org.springframework.retry.backoff;

/**
 * 无退避策略：也就是指当重试时，则立即重试
 *
 * @author Rob Harrop
 * @since 2.1
 */
public class NoBackOffPolicy extends StatelessBackOffPolicy {

	@Override
	protected void doBackOff() throws BackOffInterruptedException {
	}

	@Override
	public String toString() {
		return "NoBackOffPolicy []";
	}

}
