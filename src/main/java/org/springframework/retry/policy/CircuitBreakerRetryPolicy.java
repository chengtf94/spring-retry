package org.springframework.retry.policy;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;

/**
 * 带有熔断的重试策略
 *
 * @author Dave Syer
 *
 */
@SuppressWarnings("serial")
public class CircuitBreakerRetryPolicy implements RetryPolicy {
	private static Log logger = LogFactory.getLog(CircuitBreakerRetryPolicy.class);

	public static final String CIRCUIT_OPEN = "circuit.open";
	public static final String CIRCUIT_SHORT_COUNT = "circuit.shortCount";

	/** 被代理的重试策略、 */
	private final RetryPolicy delegate;
	private long resetTimeout = 20000;
	private long openTimeout = 5000;
	public void setResetTimeout(long timeout) {
		this.resetTimeout = timeout;
	}
	public void setOpenTimeout(long timeout) {
		this.openTimeout = timeout;
	}

	/** 构造方法 */
	public CircuitBreakerRetryPolicy() {
		this(new SimpleRetryPolicy());
	}
	public CircuitBreakerRetryPolicy(RetryPolicy delegate) {
		this.delegate = delegate;
	}

	@Override
	public RetryContext open(RetryContext parent) {
		return new CircuitBreakerRetryContext(parent, this.delegate, this.resetTimeout, this.openTimeout);
	}

	@Override
	public boolean canRetry(RetryContext context) {
		CircuitBreakerRetryContext circuit = (CircuitBreakerRetryContext) context;
		if (circuit.isOpen()) {
			circuit.incrementShortCircuitCount();
			return false;
		} else {
			circuit.reset();
		}
		return this.delegate.canRetry(circuit.context);
	}

	@Override
	public void close(RetryContext context) {
		CircuitBreakerRetryContext circuit = (CircuitBreakerRetryContext) context;
		this.delegate.close(circuit.context);
	}

	@Override
	public void registerThrowable(RetryContext context, Throwable throwable) {
		CircuitBreakerRetryContext circuit = (CircuitBreakerRetryContext) context;
		circuit.registerThrowable(throwable);
		this.delegate.registerThrowable(circuit.context, throwable);
	}

	/** 带有熔断的重试策略上下文 */
	static class CircuitBreakerRetryContext extends RetryContextSupport {

		/** 被代理的重试上下文、被代理的重试策略、起始时间戳、 */
		private volatile RetryContext context;
		private final RetryPolicy policy;
		private volatile long start = System.currentTimeMillis();
		private final long timeout;
		private final long openWindow;
		private final AtomicInteger shortCircuitCount = new AtomicInteger();

		/** 构造方法 */
		public CircuitBreakerRetryContext(RetryContext parent, RetryPolicy policy, long timeout, long openWindow) {
			super(parent);
			this.policy = policy;
			this.timeout = timeout;
			this.openWindow = openWindow;
			this.context = createDelegateContext(policy, parent);
			setAttribute("state.global", true);
		}

		/** 创建被代理的重试上下文 */
		private RetryContext createDelegateContext(RetryPolicy policy, RetryContext parent) {
			RetryContext context = policy.open(parent);
			reset();
			return context;
		}

		public void reset() {
			shortCircuitCount.set(0);
			setAttribute(CIRCUIT_SHORT_COUNT, shortCircuitCount.get());
		}

		public void incrementShortCircuitCount() {
			shortCircuitCount.incrementAndGet();
			setAttribute(CIRCUIT_SHORT_COUNT, shortCircuitCount.get());
		}

		public boolean isOpen() {
			long time = System.currentTimeMillis() - this.start;
			boolean retryable = this.policy.canRetry(this.context);
			if (!retryable) {
				if (time > this.timeout) {
					logger.trace("Closing");
					this.context = createDelegateContext(policy, getParent());
					this.start = System.currentTimeMillis();
					retryable = this.policy.canRetry(this.context);
				} else if (time < this.openWindow) {
					if (!hasAttribute(CIRCUIT_OPEN) || (Boolean) getAttribute(CIRCUIT_OPEN) == false) {
						logger.trace("Opening circuit");
						setAttribute(CIRCUIT_OPEN, true);
						this.start = System.currentTimeMillis();
					}
					return true;
				}
			} else {
				if (time > this.openWindow) {
					logger.trace("Resetting context");
					this.start = System.currentTimeMillis();
					this.context = createDelegateContext(policy, getParent());
				}
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Open: " + !retryable);
			}
			setAttribute(CIRCUIT_OPEN, !retryable);
			return !retryable;
		}

		@Override
		public int getRetryCount() {
			return this.context.getRetryCount();
		}

		@Override
		public String toString() {
			return this.context.toString();
		}

	}

}
