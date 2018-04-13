package com.eolinker.service;

import java.sql.Timestamp;
public interface TestHistoryService
{

	public Integer addTestHistory(Integer projectID, Integer apiID, String requestInfo, String resultInfo, Timestamp testTime);

	public boolean deleteTestHistory(Integer projectID, Integer userID, Integer testID);

	public boolean deleteAllTestHistory(Integer projectID, Integer userID, Integer apiID);
	
	public Integer getProjectID(Integer apiID, Integer testID);

}
