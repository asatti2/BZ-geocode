package com.ops.dto;

import java.util.ArrayList;
import java.util.List;

import com.ops.constants.ErrorType;

public class ErrorInfoTO extends ResponseTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6711966484067827076L;
	
	private ErrorType errorType;
	
	private List<FieldErrorTO> errorList;

	public ErrorType getErrorType() {
		return errorType;
	}

	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}

	public List<FieldErrorTO> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<FieldErrorTO> errorList) {
		this.errorList = errorList;
	}
	
	public void addFieldError(String field, String fieldMessage)
	{
		if(errorList==null)
		{
			errorList = new ArrayList<FieldErrorTO>();
		}
		FieldErrorTO fieldErrorTO = new FieldErrorTO(field,fieldMessage);
		errorList.add(fieldErrorTO);
	}
}
