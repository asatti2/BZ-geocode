package com.ops.dto;

import java.io.Serializable;

import com.ops.constants.ResultType;

public class ResponseTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private ResultType status;
	private String message;
	private Object responseData;
	
	
	public ResultType getStatus() {
		return status;
	}
	public void setStatus(ResultType status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getResponseData() {
		return responseData;
	}
	public void setResponseData(Object responseData) {
		this.responseData = responseData;
	}
	@Override
	public String toString() {
		return "ResponseTO [status=" + status + ", message=" + message + ", responseData=" + responseData + "]";
	}
	
	

}
