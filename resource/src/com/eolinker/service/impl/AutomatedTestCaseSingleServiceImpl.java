package com.eolinker.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eolinker.mapper.ApiMapper;
import com.eolinker.mapper.AutomatedTestCaseSingleMapper;
import com.eolinker.mapper.ProjectMapper;
import com.eolinker.mapper.ProjectOperationLogMapper;
import com.eolinker.pojo.AutomatedTestCaseSingle;
import com.eolinker.pojo.ProjectOperationLog;
import com.eolinker.service.AutomatedTestCaseSingleService;
@Service
@Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="java.lang.Exception")
public class AutomatedTestCaseSingleServiceImpl implements AutomatedTestCaseSingleService
{

	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private ProjectOperationLogMapper projectOperationLogMapper;
	@Autowired
	private AutomatedTestCaseSingleMapper automatedTestCaseSingleMapper;
	@Autowired
	private ApiMapper apiMapper;
	
	@Override
	public Integer addSingleTestCase(com.eolinker.pojo.AutomatedTestCaseSingle automatedTestCaseSingle, Integer projectID, Integer userID)
	{
		// TODO Auto-generated method stub
		if (automatedTestCaseSingleMapper.addSingleTestCase(automatedTestCaseSingle) > 0)
		{
			// 添加操作记录
			Date date = new Date();
			Timestamp nowTime = new Timestamp(date.getTime());
			projectMapper.updateProjectUpdateTime(projectID, nowTime);
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpDesc("新增自动化用例单例'" + automatedTestCaseSingle.getApiName() + "'");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_AUTOMATED_TEST_CASE);
			projectOperationLog.setOpTargetID(automatedTestCaseSingle.getConnID());
			projectOperationLog.setOpTime(nowTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
			projectOperationLog.setOpUerID(userID);
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return automatedTestCaseSingle.getConnID();
		}
		else
		{
			return 0;
		}
	}

	@Override
	public boolean editSingleTestCase(AutomatedTestCaseSingle automatedTestCaseSingle, Integer projectID,
			Integer userID)
	{
		// TODO Auto-generated method stub
		if (automatedTestCaseSingleMapper.editSingleTestCase(automatedTestCaseSingle) > 0)
		{
			// 添加操作记录
			Date date = new Date();
			Timestamp nowTime = new Timestamp(date.getTime());
			projectMapper.updateProjectUpdateTime(projectID, nowTime);
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpDesc("修改自动化用例单例'" + automatedTestCaseSingle.getApiName() + "'");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_AUTOMATED_TEST_CASE);
			projectOperationLog.setOpTargetID(automatedTestCaseSingle.getConnID());
			projectOperationLog.setOpTime(nowTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_UPDATE);
			projectOperationLog.setOpUerID(userID);
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return true;
		}
		return false;
	}

	@Override
	public List<Map<String, Object>> getSingleTestCaseList(Integer projectID, Integer caseID)
	{
		// TODO Auto-generated method stub
		List<Map<String, Object>> singleCaseList = new ArrayList<Map<String, Object>>();
		if(caseID != null && caseID > 0)
		{
			singleCaseList = automatedTestCaseSingleMapper.getSingleCaseList(caseID);
		}
		else
		{
			singleCaseList = automatedTestCaseSingleMapper.getAllSingleCase(projectID);
		}
		if(singleCaseList != null && !singleCaseList.isEmpty())
		{
			for(Map<String, Object> singleCase : singleCaseList)
			{
				if((int)singleCase.get("matchType") == 2 && singleCase.get("matchRule") != null)
				{
					singleCase.put("matchRule", JSONObject.parse((String) singleCase.get("matchRule")));
				}
			}
		}
		return singleCaseList;
	}

	@Override
	public Map<String, Object> getSingleTestCaseInfo(Integer projectID, Integer connID)
	{
		// TODO Auto-generated method stub
		Map<String, Object> singleCase = automatedTestCaseSingleMapper.getSingleTestCaseInfo(projectID, connID);
		if(singleCase != null && !singleCase.isEmpty())
		{
			if((int)singleCase.get("matchType") == 2 && singleCase.get("matchRule") != null)
			{
				singleCase.put("matchRule", JSONObject.parse((String) singleCase.get("matchRule")));
			}
		}
		return singleCase;
	}

	@Override
	public boolean deleteSingleTestCase(Integer projectID, String connID, Integer userID)
	{
		// TODO Auto-generated method stub
		String apiName = "";
		JSONArray jsonArray = JSONArray.parseArray(connID);
		List<Integer> connIDs = new ArrayList<Integer>();
		if (jsonArray != null && !jsonArray.isEmpty())
		{
			for (Iterator<Object> iterator = jsonArray.iterator(); iterator.hasNext();)
			{
				connIDs.add((Integer) iterator.next());
			}
			apiName = automatedTestCaseSingleMapper.getApiNameByIDs(connIDs);
			if(automatedTestCaseSingleMapper.deleteSingleTestCase(projectID, connIDs) < 1)
				throw new RuntimeException("deleteSingleTestCase");
			Date date = new Date();
			Timestamp nowTime = new Timestamp(date.getTime());
			projectMapper.updateProjectUpdateTime(projectID, nowTime);
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpDesc("删除自动化用例单例'" + apiName + "'");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_AUTOMATED_TEST_CASE);
			projectOperationLog.setOpTargetID(projectID);
			projectOperationLog.setOpTime(nowTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
			projectOperationLog.setOpUerID(userID);
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return true;
		}
		return false;
	}

	@Override
	public List<Map<String, Object>> getApiList(Integer projectID)
	{
		// TODO Auto-generated method stub
		List<Map<String, Object>> apiList= apiMapper.getAllApi(projectID);
		for(Map<String, Object> api : apiList)
		{
			JSONObject apiJson = JSONObject.parseObject((String) api.get("apiJson"));
			api.put("headerInfo", apiJson.get("headerInfo"));
			api.put("requestInfo", apiJson.get("requestInfo"));
			api.put("resultInfo", apiJson.get("resultInfo"));
			api.remove("apiJson");
		}
		return apiList;
	}

}
