package org.springframework.retry.backoff;

import java.io.Serializable;

/**
 * 暂停睡眠接口
 *
 * @author Dave Syer
 *
 */
public interface Sleeper extends Serializable {

	/** 暂停睡眠 */
	void sleep(long backOffPeriod) throws InterruptedException;

}
