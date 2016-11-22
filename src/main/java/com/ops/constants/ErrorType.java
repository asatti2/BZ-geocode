package com.ops.constants;

public enum ErrorType {

	EXCEPTION(1), VALIDATION(2);

	private final int errorCode;

	private ErrorType(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
