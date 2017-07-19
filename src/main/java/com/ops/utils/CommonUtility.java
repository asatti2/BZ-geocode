package com.ops.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;

@Component
public class CommonUtility{
	
	private static final Logger logger = LoggerFactory.getLogger(CommonUtility.class);
	
	private static final String ALPHA_NUM ="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		
	/**
	 * Generates Random alpha numeric password with given length
	 * @param len
	 * @return String
	 */
	public static String getRandomPassword(int len) {
	      StringBuffer sb = new StringBuffer(len);
	      for (int i=0;  i<len;  i++) {
	         int ndx = (int)(Math.random()*ALPHA_NUM.length());
	         sb.append(ALPHA_NUM.charAt(ndx));
	     }
	    return sb.toString();
	  }
	
	
	/**
	 * Gives Properties object for the given property file name.
	 * @param fileName
	 * @return Properties
	 * @throws IOException
	 */
	public Properties getProperty(String fileName) throws IOException{
		Properties prop = new Properties();
    	InputStream input  = getClass().getClassLoader().getResourceAsStream(fileName);
    	if(input != null){
    		prop.load(input);
    		return prop;
    	}else{
    		logger.error("");
    		throw new FileNotFoundException("Property File '"+fileName+"' not found in classpath");
    	} 
	}
	
	/**
	 * Generate Access Token
	 * @return String
	 */
	public static String generateAccessToken(String str){
		byte[] accessToken = Base64.encode(str.getBytes());
		return new String(accessToken);
	}
	
	public String getApplicationProperties(String propertyName) throws IOException{
		Properties prop = getProperty("app.properties");
		return prop.getProperty(propertyName);
	}
	
	public String getReadableDate(Timestamp myTimestamp){
		return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(myTimestamp);
	}
	
	public long convertToTimestamp(String date) throws ParseException{
		return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(date).getTime();
	}
	
	public String getRazorpayProperties(String propertyName) throws IOException{
		Properties prop = getProperty("razorpay.properties");
		return prop.getProperty(propertyName);
	}
}
