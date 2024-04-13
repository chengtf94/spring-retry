package org.springframework.retry.policy;

import java.util.Map;

import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.util.ClassUtils;

/**
 * 简单固定次数重试策略：RetryTemplate默认使用，默认最大重试次数是3
 *
 * @author Dave Syer
 * @author Rob Harrop
 * @author Gary Russell
 * @author Aleksandr Shamukov
 */
@SuppressWarnings("serial")
public class SimpleRetryPolicy implements RetryPolicy {

	/** 默认最大重试次数 */
	public final static int DEFAULT_MAX_ATTEMPTS = 3;

	/** 最大重试次数、重试 */
	private volatile int maxAttempts;
	private BinaryExceptionClassifier retryableClassifier = new BinaryExceptionClassifier(false);
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}
	public int getMaxAttempts() {
		return this.maxAttempts;
	}

	/** 构造方法 */
	public SimpleRetryPolicy() {
		this(DEFAULT_MAX_ATTEMPTS, BinaryExceptionClassifier.defaultClassifier());
	}
	public SimpleRetryPolicy(int maxAttempts) {
		this(maxAttempts, BinaryExceptionClassifier.defaultClassifier());
	}
	public SimpleRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions) {
		this(maxAttempts, retryableExceptions, false);
	}
	public SimpleRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions,
			boolean traverseCauses) {
		this(maxAttempts, retryableExceptions, traverseCauses, false);
	}
	public SimpleRetryPolicy(int maxAttempts, Map<Class<? extends Throwable>, Boolean> retryableExceptions,
			boolean traverseCauses, boolean defaultValue) {
		super();
		this.maxAttempts = maxAttempts;
		this.retryableClassifier = new BinaryExceptionClassifier(retryableExceptions, defaultValue);
		this.retryableClassifier.setTraverseCauses(traverseCauses);
	}
	public SimpleRetryPolicy(int maxAttempts, BinaryExceptionClassifier classifier) {
		super();
		this.maxAttempts = maxAttempts;
		this.retryableClassifier = classifier;
	}

	@Override
	public RetryContext open(RetryContext parent) {
		return new SimpleRetryContext(parent);
	}
	private static class SimpleRetryContext extends RetryContextSupport {
		public SimpleRetryContext(RetryContext parent) {
			super(parent);
		}
	}

	@Override
	public boolean canRetry(RetryContext context) {
		Throwable t = context.getLastThrowable();
		return (t == null || retryForException(t)) && context.getRetryCount() < this.maxAttempts;
	}
	private boolean retryForException(Throwable ex) {
		return this.retryableClassifier.classify(ex);
	}

	@Override
	public void close(RetryContext status) {
	}

	@Override
	public void registerThrowable(RetryContext context, Throwable throwable) {
		SimpleRetryContext simpleContext = ((SimpleRetryContext) context);
		simpleContext.registerThrowable(throwable);
	}

	@Override
	public String toString() {
		return ClassUtils.getShortName(getClass()) + "[maxAttempts=" + this.maxAttempts + "]";
	}

}
