package org.seckill.dto;

//用于封装json结果
public class SeckillResult<T> {
	private boolean success;

	private T data;

	private String error;

	public SeckillResult(T data) {
		super();
		this.success = true;
		this.data = data;
	}

	public SeckillResult(String error) {
		super();
		this.success = false;
		this.error = error;
	}

	public boolean isSuccess() {
		return success;
	}

	public T getData() {
		return data;
	}

	public String getError() {
		return error;
	}

}
