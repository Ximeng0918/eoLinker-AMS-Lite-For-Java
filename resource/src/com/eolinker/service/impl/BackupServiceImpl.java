package com.eolinker.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eolinker.service.BackupService;
import com.eolinker.util.Proxy;
@Service
public class BackupServiceImpl implements BackupService
{

	@Override
	public int backupProject(Integer userID, String userCall, String userPassword, Integer projectID, String verifyCode, Map<String, Object> data)
	{
		// TODO Auto-generated method stub
		String loginUrl = "https://api.eolinker.com/common/Guest/login";
		String backupUrl = "https://api.eolinker.com/apiManagement/Import/importEoapi";
		String referUrl = "https://api.eolinker.com/openSource";
		List<Map<String, String>> headerList = new ArrayList<Map<String, String>>();
		Map<String, String> header = new HashMap<String, String>();
		header.put("name", "Referer");
		header.put("value", referUrl);
		headerList.add(header);
		List<Map<String, String>> paramList = new ArrayList<Map<String, String>>();
		Map<String, String> loginCall = new HashMap<String, String>();
		Map<String, String> loginPassword = new HashMap<String, String>();
		Map<String, String> code = new HashMap<String, String>();
		loginCall.put("key", "loginCall");
		loginCall.put("value", userCall);
		paramList.add(loginCall);
		loginPassword.put("key", "loginPassword");
		loginPassword.put("value", userPassword);
		paramList.add(loginPassword);
		code.put("key", "verifyCode");
		code.put("value", verifyCode);
		paramList.add(code);
		Proxy proxy = new Proxy();
		Map<String, Object> result = proxy.proxyToDesURL("POST", loginUrl, headerList, paramList);
		if(result != null && !result.isEmpty())
		{
			JSONObject testResult = (JSONObject) JSONObject.toJSON(result.get("testResult"));
			JSONObject body = JSONObject.parseObject(testResult.getString("body"));
			if(body != null && body.getString("statusCode").equals("000000"))
			{
				String cookie = "verifyCode="+verifyCode+";";
				JSONArray headers = JSONArray.parseArray(testResult.getString("headers"));
				for(Iterator<Object> iterator = headers.iterator(); iterator.hasNext();)
				{
					JSONObject head = (JSONObject) iterator.next();
					if(head.getString("key").equals("Set-Cookie"))
					{
						cookie = cookie+head.getString("value")+";";
					}
				}
				List<Map<String, String>> headerList1 = new ArrayList<Map<String, String>>();
				Map<String, String> header1 = new HashMap<String, String>();
				header1.put("name", "Referer");
				header1.put("value", referUrl);
				headerList1.add(header1);
				Map<String, String> header2 = new HashMap<String, String>();
				header2.put("name", "Cookie");
				header2.put("value", cookie);
				headerList1.add(header2);
				Map<String, Object> projectInfo = (JSONObject) JSONObject.toJSON(data.get("projectInfo"));
				Date nowTime = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = dateFormat.format(nowTime);
				projectInfo.put("projectName",  "开源备份-"+projectInfo.get("projectName").toString()+time);
				data.put("projectInfo", projectInfo);
				List<Map<String, String>> params = new ArrayList<Map<String, String>>();
				Map<String, String> param = new HashMap<String, String>();
				param.put("key", "data");
				param.put("value", JSONObject.toJSONString(data));
				params.add(param);
				Map<String, Object> res = proxy.proxyToDesURL("POST", backupUrl, headerList1, params);
				JSONObject testResult1 = (JSONObject) JSONObject.toJSON(res.get("testResult"));
				JSONObject body1 = JSONObject.parseObject(testResult1.getString("body"));
				if(body1 != null && body1.getString("statusCode").equals("000000"))				
					return 0;				
				else
					return -6;
			}
			else if(body != null && body.getString("statusCode").equals("120001"))
			{
				return -3;
			}
			else if(body != null && body.getString("statusCode").equals("120003"))
			{
				return -4;
			}
			else {
				return -5;
			}
		}
		else {
			return -2;
		}
	}

}
