package com.eolinker.service.impl;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eolinker.mapper.TestHistoryMapper;
import com.eolinker.pojo.TestHistory;
import com.eolinker.service.TestHistoryService;
@Service
@Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="java.lang.Exception")
public class TestHistoryServiceImpl implements TestHistoryService
{

	@Autowired
	private TestHistoryMapper testHistoryMapper;
	@Override
	public Integer addTestHistory(Integer projectID, Integer apiID, String requestInfo, String resultInfo,
			Timestamp testTime)
	{
		// TODO Auto-generated method stub
		TestHistory testHistory = new TestHistory();
		testHistory.setApiID(apiID);
		testHistory.setProjectID(projectID);
		testHistory.setRequestInfo(requestInfo);
		testHistory.setResultInfo(resultInfo);
		testHistory.setTestTime(testTime);
		if(testHistoryMapper.addTestHistory(testHistory) > 0)
			return testHistory.getTestID();
		return null;
	}
	@Override
	public boolean deleteTestHistory(Integer projectID, Integer userID, Integer testID)
	{
		// TODO Auto-generated method stub
		if (testHistoryMapper.deleteTestHistory(projectID, testID) > 0)
			return true;
		else 
			return false;
	}
	@Override
	public boolean deleteAllTestHistory(Integer projectID, Integer userID, Integer apiID)
	{
		// TODO Auto-generated method stub
		if (testHistoryMapper.deleteAllTestHistory(projectID, apiID) > 0)
			return true;
		else 
			return false;
	}
	
	@Override
	public Integer getProjectID(Integer apiID, Integer testID)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
