package com.eolinker.service;

import java.util.List;
import java.util.Map;

import com.eolinker.pojo.AutomatedTestCaseSingle;

public interface AutomatedTestCaseSingleService
{

	public Integer addSingleTestCase(AutomatedTestCaseSingle automatedTestCaseSingle, Integer projectID, Integer userID);

	public boolean editSingleTestCase(AutomatedTestCaseSingle automatedTestCaseSingle, Integer projectID,
			Integer userID);

	public List<Map<String, Object>> getSingleTestCaseList(Integer projectID, Integer caseID);

	public Map<String, Object> getSingleTestCaseInfo(Integer projectID, Integer connID);

	public boolean deleteSingleTestCase(Integer projectID, String connID, Integer userID);

	public List<Map<String, Object>> getApiList(Integer projectID);

}
