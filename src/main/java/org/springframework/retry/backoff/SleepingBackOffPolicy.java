package org.springframework.retry.backoff;

/**
 * 暂停睡眠退避策略接口
 */
public interface SleepingBackOffPolicy<T extends SleepingBackOffPolicy<T>> extends BackOffPolicy {

	/** 创建暂停睡眠退避策略 */
	T withSleeper(Sleeper sleeper);

}
