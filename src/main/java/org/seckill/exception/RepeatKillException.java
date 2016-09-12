package org.seckill.exception;

/**
 * 重复秒杀异常（运行期异常，Spring事务只接受运行期异常）
 */
public class RepeatKillException extends SeckillException {

	public RepeatKillException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepeatKillException(String message) {
		super(message);
	}

}
