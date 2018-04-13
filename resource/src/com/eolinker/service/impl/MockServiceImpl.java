package com.eolinker.service.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eolinker.mapper.MockMapper;
import com.eolinker.service.MockService;
@Service
public class MockServiceImpl implements MockService
{
	@Autowired
	private MockMapper mockMapper;
	
	@Override
	public String simple(Integer projectID, String uri, Integer requstType, String resultType)
	{
		// TODO Auto-generated method stub
		String result = "";
		if(resultType.equals("success"))
		{
			result = mockMapper.getSuccessResult(projectID, uri, requstType);
			if(result == null || result.equals("") || result.length() <= 0)
			{
				Map<String, Object> data = getRestfulMock(projectID, uri, requstType);
				if(data != null && !data.isEmpty())
				{
					result = (String) data.get("apiSuccessMock");
				}
			}
		}
		else
		{
			result = mockMapper.getFailureResult(projectID, uri, requstType);
			if(result == null || result.equals("") || result.length() <= 0)
			{
				Map<String, Object> data = getRestfulMock(projectID, uri, requstType);
				if(data != null && !data.isEmpty())
				{
					result = (String) data.get("apiFailureMock");
				}
			}
		}
		return result;
	}
	
	private Map<String, Object> getRestfulMock(Integer projectID, String uri, Integer requstType)
	{
		List<Map<String, Object>> data = mockMapper.getRestfulMock(projectID, requstType);
		if(data != null && !data.isEmpty())
		{
			for(Map<String, Object> api : data)
			{
				String str = api.get("apiURI").toString().replaceAll("\\{[^\\/]+\\}", "[^/]+");
				str = str.replace("amp;", "");
				str = str.replaceAll("/:[^\\/]+/", "[^/]+");
				str = str.replaceAll("//", "\\/");
				str = str.replace("/\\?/", "\\?");
				str = "^"+str+"$";
				uri = uri.replace("amp;", "");
				Pattern r = Pattern.compile(str);
				Matcher m = r.matcher(uri);
				if(m.find())
				{
					return api;
				}
			}
		}
		return null;
	}

	@Override
	public String getMockResult(Integer projectID, String uri, Integer requstType)
	{
		// TODO Auto-generated method stub
		String result = mockMapper.getMockResult(projectID, uri, requstType);
		if(result == null || result.equals("") || result.length() <= 0)
		{
			Map<String, Object> data = getRestfulMock(projectID, uri, requstType);
			if(data != null && !data.isEmpty())
			{
				result = (String) data.get("mockResult");
			}
		}
		return result;
	}

}
