package com.ops.utils;

import java.io.IOException;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class GoogleMatrixRequest {
  
  public static String callAPI(String url, String params, OkHttpClient client) throws IOException{
	 
	  String urlRequest = url+"?"+params;
	  Request request = new Request.Builder().url(urlRequest).build();
	  System.out.println("Request:"+request);
	  Response response = client.newCall(request).execute();
	  System.out.println(response.code());
	  return response.body().string();
  }

}
