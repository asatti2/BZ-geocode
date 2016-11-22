package com.ops.controllers;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ops.constants.ErrorType;
import com.ops.constants.ResultType;
import com.ops.dto.ErrorInfoTO;
import com.ops.dto.ResponseTO;
import com.ops.exceptions.ApplicationException;
import com.ops.exceptions.BusinessException;

@RestController
public class ResponseController {

	private static final Logger logger = LoggerFactory.getLogger(ResponseController.class);

	@Autowired
	private MessageSource messageSource;
	private Locale locale;
	private String loggedInUserId;
	boolean selfProfile;

	public ResponseController() {
		super();
	}

	public Locale getLocale() {
		locale = LocaleContextHolder.getLocale();
		return locale;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}
	
	public String getLoggedInUserId() {
		return loggedInUserId;
	}

	public void setLoggedInUserId(String loggedInUserId) {
		this.loggedInUserId = loggedInUserId;
	}

	public boolean isSelfProfile() {
		return selfProfile;
	}

	public void setSelfProfile(boolean selfProfile) {
		this.selfProfile = selfProfile;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseTO processValidationError(MethodArgumentNotValidException ex) {
		logger.info("Processing validation errors...");
		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		logger.info("Returning validation response...");
		return processFieldErrors(fieldErrors);
	}

	private ErrorInfoTO processFieldErrors(List<FieldError> fieldErrors) {
		ErrorInfoTO errorInfo = new ErrorInfoTO();
		logger.info("Adding field level validation messages...");
		for (FieldError fieldError : fieldErrors) {
			String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
			errorInfo.addFieldError(fieldError.getField(),
					localizedErrorMessage);
		}
		errorInfo.setErrorType(ErrorType.VALIDATION);
		errorInfo.setStatus(ResultType.FAILURE);
		return errorInfo;
	}

	private String resolveLocalizedErrorMessage(FieldError fieldError) {
		String localizedErrorMessage = messageSource.getMessage(
				fieldError.getDefaultMessage(), null, getLocale());

		// If the message was not found, return the most accurate field error
		// code instead.
		if (localizedErrorMessage.equals(fieldError.getDefaultMessage())) {
			String[] fieldErrorCodes = fieldError.getCodes();
			localizedErrorMessage = fieldErrorCodes[0];
		}

		return localizedErrorMessage;
	}

	@ExceptionHandler({ ApplicationException.class, BusinessException.class})
	@ResponseBody
	public ResponseTO handleException(HttpServletRequest req,
			Exception ex) {
		Locale locale = LocaleContextHolder.getLocale();
		String errorMessage = null;
		if (ex instanceof ApplicationException) {
			errorMessage = messageSource.getMessage("application.exception",
					null, locale);
		} else if (ex instanceof BusinessException) {
			errorMessage = messageSource.getMessage(ex.getMessage(), null,
					locale);
		}

		ErrorInfoTO errorInfo = new ErrorInfoTO();
		errorInfo.setErrorType(ErrorType.EXCEPTION);
		errorInfo.setStatus(ResultType.FAILURE);
		errorInfo.setMessage(errorMessage);

		return errorInfo;
	}

}