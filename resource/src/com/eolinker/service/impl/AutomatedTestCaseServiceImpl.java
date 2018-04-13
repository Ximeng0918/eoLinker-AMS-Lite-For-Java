package com.eolinker.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import com.eolinker.mapper.AutomatedTestCaseGroupMapper;
import com.eolinker.mapper.AutomatedTestCaseMapper;
import com.eolinker.mapper.AutomatedTestCaseSingleMapper;
import com.eolinker.mapper.ProjectMapper;
import com.eolinker.mapper.ProjectOperationLogMapper;
import com.eolinker.pojo.AutomatedTestCase;
import com.eolinker.pojo.ProjectOperationLog;
import com.eolinker.service.AutomatedTestCaseService;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "java.lang.Exception")
public class AutomatedTestCaseServiceImpl implements AutomatedTestCaseService
{
	@Autowired
	private AutomatedTestCaseGroupMapper automtedTestCaseGroupMapper;
	@Autowired
	private AutomatedTestCaseSingleMapper automatedTestCaseSingleMapper;
	@Autowired
	private AutomatedTestCaseMapper automatedTestCaseMapper;
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private ProjectOperationLogMapper projectOperationLogMapper;

	@Override
	public Integer checkGroupPermission(Integer groupID, Integer userID)
	{
		// TODO Auto-generated method stub
		return automtedTestCaseGroupMapper.checkGroupPermission(groupID, userID);
	}

	@Override
	public Integer addTestCase(AutomatedTestCase automatedTestCase)
	{
		// TODO Auto-generated method stub
		Date date = new Date();
		Timestamp nowTime = new Timestamp(date.getTime());
		automatedTestCase.setCreateTime(nowTime);
		automatedTestCase.setUpdateTime(nowTime);
		if (automatedTestCaseMapper.addTestCase(automatedTestCase) > 0)
		{
			// 添加操作记录
			projectMapper.updateProjectUpdateTime(automatedTestCase.getProjectID(), nowTime);
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(automatedTestCase.getProjectID());
			projectOperationLog.setOpDesc("新增自动化用例'" + automatedTestCase.getCaseName() + "'");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_AUTOMATED_TEST_CASE);
			projectOperationLog.setOpTargetID(automatedTestCase.getCaseID());
			projectOperationLog.setOpTime(nowTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
			projectOperationLog.setOpUerID(automatedTestCase.getUserID());
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return automatedTestCase.getCaseID();
		}
		else
		{
			return 0;
		}
	}

	@Override
	public boolean editTestCase(AutomatedTestCase automatedTestCase)
	{
		// TODO Auto-generated method stub
		Date date = new Date();
		Timestamp nowTime = new Timestamp(date.getTime());
		automatedTestCase.setUpdateTime(nowTime);
		if (automatedTestCaseMapper.editTestCase(automatedTestCase) > 0)
		{
			// 添加操作记录
			projectMapper.updateProjectUpdateTime(automatedTestCase.getProjectID(), nowTime);
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(automatedTestCase.getProjectID());
			projectOperationLog.setOpDesc("修改自动化用例'" + automatedTestCase.getCaseName() + "'");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_AUTOMATED_TEST_CASE);
			projectOperationLog.setOpTargetID(automatedTestCase.getCaseID());
			projectOperationLog.setOpTime(nowTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_UPDATE);
			projectOperationLog.setOpUerID(automatedTestCase.getUserID());
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public List<Map<String, Object>> getTestCaseList(Integer projectID, Integer groupID)
	{
		// TODO Auto-generated method stub
		List<Map<String, Object>> caseList = automatedTestCaseMapper.getTestCaseList(projectID, groupID);
		if (caseList != null && !caseList.isEmpty())
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (Map<String, Object> caseData : caseList)
			{
				String updateTime = dateFormat.format(caseData.get("updateTime"));
				caseData.put("updateTime", updateTime);
			}
		}
		return caseList;
	}

	@Override
	public Map<String, Object> getTestCaseInfo(Integer projectID, Integer caseID)
	{
		// TODO Auto-generated method stub
		return automatedTestCaseMapper.getTestCaseInfo(projectID, caseID);
	}

	@Override
	public boolean deleteTestCase(Integer projectID, String caseID, Integer userID)
	{
		// TODO Auto-generated method stub
		String caseName = "";
		Integer result = 0;
		JSONArray jsonArray = JSONArray.parseArray(caseID);
		List<Integer> caseIDs = new ArrayList<Integer>();
		if (jsonArray != null && !jsonArray.isEmpty())
		{
			for (Iterator<Object> iterator = jsonArray.iterator(); iterator.hasNext();)
			{
				caseIDs.add((Integer) iterator.next());
			}
			caseName = automatedTestCaseMapper.getCaseNameByIDs(caseIDs);
			result = automatedTestCaseMapper.deleteTestCase(projectID, caseIDs);
		}
		if (result > 0)
		{
			automatedTestCaseSingleMapper.batchDeleteSingle(caseIDs);
			Date date = new Date();
			Timestamp updateTime = new Timestamp(date.getTime());
			projectMapper.updateProjectUpdateTime(projectID, updateTime);
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpDesc("删除用例：'" + caseName + "'");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_API);
			projectOperationLog.setOpTargetID(projectID);
			projectOperationLog.setOpTime(updateTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_DELETE);
			projectOperationLog.setOpUerID(userID);
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return true;
		}
		return false;

	}

	@Override
	public List<Map<String, Object>> searchTestCase(Integer projectID, String tips)
	{
		// TODO Auto-generated method stub
		return automatedTestCaseMapper.searchTestCase(projectID, tips);
	}

	@Override
	public List<Map<String, Object>> getTestCaseDataList(Integer projectID, Integer groupID)
	{
		// TODO Auto-generated method stub
		List<Map<String, Object>> result = automatedTestCaseMapper.getTestCaseDataList(projectID, groupID);
		if (result != null && !result.isEmpty())
		{
			for (Map<String, Object> testCase : result)
			{
				List<Map<String, Object>> singleCaseList = automatedTestCaseSingleMapper
						.getSingleCaseList((Integer) testCase.get("caseID"));
				if (singleCaseList != null && !singleCaseList.isEmpty())
				{
					for (Map<String, Object> singleCase : singleCaseList)
					{
						if ((int) singleCase.get("matchType") == 2 && singleCase.get("matchRule") != null)
						{
							singleCase.put("matchRule", JSONObject.parse((String) singleCase.get("matchRule")));
						}
					}
				}
				testCase.put("singleCaseList", singleCaseList);
			}
		}
		return result;
	}

	@Override
	public Integer getProjectIDByCaseID(Integer caseID)
	{
		// TODO Auto-generated method stub
		return automatedTestCaseMapper.getProjectIDByCaseID(caseID);
	}

}
