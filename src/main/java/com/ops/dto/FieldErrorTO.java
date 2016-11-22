package com.ops.dto;

import java.io.Serializable;

public class FieldErrorTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5610872245861801750L;
	private String field;
	private String message;

	public FieldErrorTO(String field, String message) {
		super();
		this.field = field;
		this.message = message;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
