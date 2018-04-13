package com.eolinker.util;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class Proxy
{
	public Map<String, Object> proxyToDesURL(String method, String completeURL,
			List<Map<String, String>> headerList, List<Map<String, String>> paramList)
	{
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<String, Object>();
		Date nowTime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String testTime = dateFormat.format(nowTime);
		long startTime = 0;
		long endTime = 0;
		long deny = 0;
		try
		{
			 SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			 RestTemplate restTemplate = new RestTemplate(requestFactory);
			 HttpHeaders requestHeaders = new HttpHeaders();
			 MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
			 HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, requestHeaders);
			 if(headerList != null && !headerList.isEmpty())
			 {
				 for(Map<String, String> header : headerList)
				 {
					 requestHeaders.add(header.get("name"), header.get("value"));
				 }
			 }
			 if(!method.equals("GET"))
			 {
				 if(paramList != null && !paramList.isEmpty())
				 {
					 for(Map<String, String> param : paramList)
					 {
						 params.add(param.get("key"), param.get("value"));
					 }
				 }
				 requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				 requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, requestHeaders);
			 }
			 HttpMethod requestType = HttpMethod.GET;
			 switch (method)
			{
				case "GET":
					requestType = HttpMethod.GET;
					break;
				case "POST":
					requestType = HttpMethod.POST;
					break;
				case "PUT":
					requestType = HttpMethod.PUT;
					break;
				case "DELETE":
					requestType = HttpMethod.DELETE;
					break;
				case "HEAD":
					requestType = HttpMethod.HEAD;
					break;
				case "OPTIONS":
					requestType = HttpMethod.OPTIONS;
					break;
				default:
					break;
			}
			 Map<String, Object> testResult = new HashMap<String, Object>();
			 startTime = System.currentTimeMillis();
			 ResponseEntity<String> responseEntity = restTemplate.exchange(completeURL, requestType, requestEntity, String.class);
			 endTime = System.currentTimeMillis();
			 deny = endTime - startTime;
			 HttpStatus statusCode = responseEntity.getStatusCode();
			 HttpHeaders headers = responseEntity.getHeaders();
			 List<Map<String, String>> responseHeaders = new ArrayList<Map<String, String>>();
			 for(String key : headers.keySet())
			 {
				 Map<String, String> responseHeader = new HashMap<String, String>();
				 responseHeader.put("key", key);
				 responseHeader.put("value", StringUtils.join(headers.get(key).toArray()));
				 responseHeaders.add(responseHeader);
			 }
			 String body = responseEntity.getBody();
			 testResult.put("headers", responseHeaders);
			 testResult.put("body", body);
			 result.put("testTime", testTime);
			 result.put("testHttpCode", new Integer(statusCode.toString()));
			 result.put("testResult", testResult);
			 result.put("testDeny", deny);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			 endTime = System.currentTimeMillis();
			 deny = endTime - startTime;
			 Map<String, Object> testResult = new HashMap<String, Object>();
			 testResult.put("headers", null);
			 testResult.put("body", e.getMessage());
			 result.put("testTime", testTime);
			 result.put("testHttpCode", 500);
			 result.put("testResult", testResult);
			 result.put("testDeny", deny);
		}
		 return result;
	}
}
