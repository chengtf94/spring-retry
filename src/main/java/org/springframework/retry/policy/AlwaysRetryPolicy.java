package org.springframework.retry.policy;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;

/**
 * 允许无限重试策略：直到成功为止
 *
 * @author Dave Syer
 *
 */
@SuppressWarnings("serial")
public class AlwaysRetryPolicy extends NeverRetryPolicy {

	@Override
	public boolean canRetry(RetryContext context) {
		return true;
	}

}
