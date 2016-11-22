package com.ops.constants;

public enum ResultType {

	SUCCESS(0), FAILURE(1);

	private final int resultCode;

	private ResultType(int resultCode) {
		this.resultCode = resultCode;
	}

	public int getResultCode() {
		return resultCode;
	}

}
