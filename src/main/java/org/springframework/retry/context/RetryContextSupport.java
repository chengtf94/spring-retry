/*
 * Copyright 2006-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.retry.context;

import org.springframework.core.AttributeAccessorSupport;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;

/**
 * 重试上下文辅助类
 *
 * @author Dave Syer
 */
@SuppressWarnings("serial")
public class RetryContextSupport extends AttributeAccessorSupport implements RetryContext {

	/** 父重试上下文、是否终止、重试次数、导致当前重试的异常 */
	private final RetryContext parent;
	private volatile boolean terminate = false;
	private volatile int count;
	private volatile Throwable lastException;

	public RetryContextSupport(RetryContext parent) {
		super();
		this.parent = parent;
	}

	@Override
	public RetryContext getParent() {
		return this.parent;
	}

	@Override
	public void setExhaustedOnly() {
		terminate = true;
	}

	@Override
	public boolean isExhaustedOnly() {
		return terminate;
	}

	@Override
	public int getRetryCount() {
		return count;
	}

	/** 注册重试后的异常 */
	public void registerThrowable(Throwable throwable) {
		this.lastException = throwable;
		if (throwable != null)
			count++;
	}

	@Override
	public Throwable getLastThrowable() {
		return lastException;
	}

	@Override
	public String toString() {
		return String.format("[RetryContext: count=%d, lastException=%s, exhausted=%b]", count, lastException,
				terminate);
	}

}
