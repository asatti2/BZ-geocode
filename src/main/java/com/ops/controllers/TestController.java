package com.ops.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ops.constants.ApplicationConstants;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping(ApplicationConstants.SECURE_URI)
public class TestController extends ResponseController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	@RequestMapping(value = ApplicationConstants.TEST_URI, method = RequestMethod.GET)
	public String testingFlow() {
		logger.info("Checking test flow...");
		String message = "You are seeing first api published." + "\n";
		logger.info(message.toString());
		return message;
	}
}