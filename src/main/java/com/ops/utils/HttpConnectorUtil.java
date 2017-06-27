package com.ops.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnectorUtil {
	private static final Logger logger = LoggerFactory.getLogger(HttpConnectorUtil.class);

	private static URL httpUrl = null;

	public  static String callAPI(String url, String params) {
		logger.info("URL - "+url+" | Params - "+params);

		StringBuffer response = new StringBuffer();
		try {
			httpUrl = new URL(url+"?"+params);
			HttpURLConnection con = (HttpURLConnection) httpUrl.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			int responseCode = con.getResponseCode();
			logger.info("Response Code - "+responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}

		logger.info("Response - "+response.toString());
		return response.toString();
	}
}