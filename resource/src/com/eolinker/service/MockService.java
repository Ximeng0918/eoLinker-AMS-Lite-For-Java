package com.eolinker.service;
public interface MockService
{

	public String simple(Integer projectID, String uri, Integer requstType, String resultType);

	public String getMockResult(Integer projectID, String uri, Integer requstType);

}
