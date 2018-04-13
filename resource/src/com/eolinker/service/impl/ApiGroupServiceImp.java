package com.eolinker.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eolinker.mapper.*;
import com.eolinker.pojo.Api;
import com.eolinker.pojo.ApiCache;
import com.eolinker.pojo.ApiGroup;
import com.eolinker.pojo.ApiHeader;
import com.eolinker.pojo.ApiRequestParam;
import com.eolinker.pojo.ApiRequestValue;
import com.eolinker.pojo.ApiResultParam;
import com.eolinker.pojo.ApiResultValue;
import com.eolinker.pojo.ProjectOperationLog;
import com.eolinker.service.ApiGroupService;

@Service
@Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="java.lang.Exception")
public class ApiGroupServiceImp implements ApiGroupService
{

	@Autowired
	ApiGroupMapper apiGroupMapper;
	@Autowired
	ApiMapper apiMapper;
	@Autowired
	ProjectOperationLogMapper projectOperationLogMapper;
	@Autowired
	ProjectMapper projectMapper;
	@Autowired
	ApiCacheMapper apiCacheMapper;

	@Override
	public Integer checkGroupPermission(Integer groupID, Integer userID)
	{
		// TODO Auto-generated method stub
		return apiGroupMapper.checkGroupPermission(groupID, userID);
	}

	@Override
	public boolean addApiGroup(ApiGroup apiGroup)
	{
		// TODO Auto-generated method stub
		if (apiGroup.getParentGroupID() != null)
		{
			apiGroup.setIsChild(1);
		}
		else
		{
			apiGroup.setParentGroupID(0);
			apiGroup.setIsChild(0);
		}
		int result = apiGroupMapper.addApiGroup(apiGroup);
		if (result > 0)
		{
			Date date = new Date();
			Timestamp nowTime = new Timestamp(date.getTime());
			projectMapper.updateProjectUpdateTime(apiGroup.getProjectID(), nowTime);
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(apiGroup.getProjectID());
			projectOperationLog.setOpDesc("添加项目分组 '" + apiGroup.getGroupName() + "'");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_API_GROUP);
			projectOperationLog.setOpTargetID(apiGroup.getGroupID());
			projectOperationLog.setOpTime(nowTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
			projectOperationLog.setOpUerID(apiGroup.getUserID());
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return true;
		}
		else
			return false;

	}

	@Override
	public boolean deleteGroup(Integer projectID, Integer groupID, Integer userID)
	{
		// TODO Auto-generated method stub
		ApiGroup apiGroup = apiGroupMapper.getGroupByID(groupID);
		Date date = new Date();
		Timestamp nowTime = new Timestamp(date.getTime());
		apiMapper.deleteApiByGroupID(groupID, nowTime);
		int result = apiGroupMapper.deleteGroup(groupID);
		if (result > 0)
		{
			// 添加操作记录
			projectMapper.updateProjectUpdateTime(projectID, nowTime);
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpDesc("删除项目分组  '" + apiGroup.getGroupName() + "'");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_API_GROUP);
			projectOperationLog.setOpTargetID(groupID);
			projectOperationLog.setOpTime(nowTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_DELETE);
			projectOperationLog.setOpUerID(userID);
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return true;
		}
		else
		{
			throw new RuntimeException("deleteGroup error");
		}
	}

	@Override
	public List<Map<String, Object>> getGroupList(Integer projectID)
	{
		// TODO Auto-generated method stub
		List<Map<String, Object>> groupList = apiGroupMapper.getParentGroupList(projectID);
		for (Map<String, Object> apiGroup : groupList)
		{
			apiGroup.put("childGroupList",
					(apiGroupMapper.getChildGroupList(projectID, new Integer(apiGroup.get("groupID").toString()))));
		}
		return (groupList != null && !groupList.isEmpty()) ? groupList : null;
	}

	@Override
	public String getGroupOrderList(Integer projectID)
	{
		// TODO Auto-generated method stub
		return apiGroupMapper.getGroupOrderList(projectID);
	}

	@Override
	public boolean editGroup(ApiGroup apiGroup)
	{
		// TODO Auto-generated method stub
		int result = apiGroupMapper.editGroup(apiGroup);
		if (result > 0)
		{
			Date date = new Date();
			Timestamp nowTime = new Timestamp(date.getTime());
			projectMapper.updateProjectUpdateTime(apiGroup.getProjectID(), nowTime);
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(apiGroup.getProjectID());
			projectOperationLog.setOpDesc("修改项目分组  '" + apiGroup.getGroupName() + "'");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_API_GROUP);
			projectOperationLog.setOpTargetID(apiGroup.getGroupID());
			projectOperationLog.setOpTime(nowTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_UPDATE);
			projectOperationLog.setOpUerID(apiGroup.getUserID());
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return true;
		}
		else
			return false;

	}

	@Override
	public boolean sortGroup(Integer projectID, Integer userID, String orderList)
	{
		// TODO Auto-generated method stub
		int result = apiGroupMapper.sortGroup(projectID, orderList);
		if (result > 0)
		{
			Date date = new Date();
			Timestamp nowTime = new Timestamp(date.getTime());
			projectMapper.updateProjectUpdateTime(projectID, nowTime);
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpDesc("修改项目分组排序");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_API_GROUP);
			projectOperationLog.setOpTargetID(projectID);
			projectOperationLog.setOpTime(nowTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_UPDATE);
			projectOperationLog.setOpUerID(userID);
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return true;
		}
		else
			return false;
	}

	@Override
	public Map<String, Object> exportGroup(Integer projectID, Integer groupID, Integer userID)
	{
		// TODO Auto-generated method stub
		ApiGroup apiGroup = apiGroupMapper.getGroupByID(groupID);
		Map<String, Object> result = new HashMap<String, Object>();
		if (apiGroup != null)
		{
			result.put("groupName", apiGroup.getGroupName());
			List<ApiCache> apiCacheList = apiCacheMapper.getApiCacheByGroupID(projectID, groupID);
			if (apiCacheList != null && !apiCacheList.isEmpty())
			{
				List<Map<String, Object>> apiList = new ArrayList<Map<String, Object>>();
				int i = 0;
				for (ApiCache apiCache : apiCacheList)
				{
					Map<String, Object> apiJson = JSONObject.parseObject((String) apiCache.getApiJson());
					Map<String, Object> baseInfo = (JSONObject) apiJson.get("baseInfo");
					baseInfo.put("starred", apiCache.getStarred());
					apiJson.put("baseInfo", baseInfo);
					apiList.add(i, apiJson);
					i++;
				}
				result.put("apiList", apiList);
			}
			if (apiGroup.getIsChild() == 0)
			{
				List<Map<String, Object>> data = apiGroupMapper.getChildGroupList(projectID, apiGroup.getGroupID());
				List<Map<String, Object>> childGroupList = new ArrayList<Map<String, Object>>();
				if (data != null && !data.isEmpty())
				{
					int i = 0;
					for (Map<String, Object> childGroup : data)
					{
						Map<String, Object> group = new HashMap<String, Object>();
						group.put("groupName", childGroup.get("groupName"));
						List<Map<String, Object>> apiList = new ArrayList<Map<String, Object>>();
						List<ApiCache> apiCaches = apiCacheMapper.getApiCacheByGroupID(projectID,
								new Integer(childGroup.get("groupID").toString()));
						if (apiCaches != null && !apiCaches.isEmpty())
						{
							int j = 0;
							for (ApiCache apiCache : apiCaches)
							{
								Map<String, Object> apiJson = JSONObject.parseObject((String) apiCache.getApiJson());
								Map<String, Object> baseInfo = (JSONObject) apiJson.get("baseInfo");
								baseInfo.put("starred", apiCache.getStarred());
								apiJson.put("baseInfo", baseInfo);
								apiList.add(j, apiJson);
								j++;
							}
							group.put("apiList", apiList);
						}
						childGroupList.add(i, group);
						i++;
					}
					result.put("childGroupList", childGroupList);
				}
			}
			Date date = new Date();
			Timestamp updateTime = new Timestamp(date.getTime());
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpDesc("导出接口分组'" + apiGroup.getGroupName() + "'");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_API_GROUP);
			projectOperationLog.setOpTargetID(projectID);
			projectOperationLog.setOpTime(updateTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_OTHERS);
			projectOperationLog.setOpUerID(userID);
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
		}
		return result;
	}

	@Override
	public boolean importGroup(Integer projectID, Integer userID, String data)
	{
		// TODO Auto-generated method stub
		JSONObject groupInfo = JSONObject.parseObject(data);
		if (groupInfo != null && !groupInfo.isEmpty())
		{
			ApiGroup apiGroup = new ApiGroup();
			apiGroup.setGroupName(groupInfo.getString("groupName"));
			apiGroup.setProjectID(projectID);
			apiGroup.setIsChild(0);
			apiGroup.setParentGroupID(0);
			if (apiGroupMapper.addApiGroup(apiGroup) < 1)
				throw new RuntimeException("addApiGroup error");
			JSONArray apiList = JSONArray.parseArray(groupInfo.getString("apiList"));
			if (apiList != null && !apiList.isEmpty())
			{
				for (Iterator<Object> iterator = apiList.iterator(); iterator.hasNext();)
				{
					JSONObject apiInfo = (JSONObject) iterator.next();
					JSONObject baseInfo = (JSONObject) apiInfo.get("baseInfo");
					JSONObject mockInfo = (JSONObject) apiInfo.get("mockInfo");
					Api api = new Api();
					api.setApiName(baseInfo.getString("apiName"));
					api.setApiURI(baseInfo.getString("apiURI"));
					api.setApiProtocol(baseInfo.getInteger("apiProtocol"));
					api.setApiSuccessMock(baseInfo.getString("apiSuccessMock"));
					api.setApiFailureMock(baseInfo.getString("apiFailureMock"));
					api.setApiRequestType(baseInfo.getInteger("apiRequestType"));
					api.setApiStatus(baseInfo.getInteger("apiStatus"));
					api.setStarred(baseInfo.getInteger("starred"));
					api.setGroupID(apiGroup.getGroupID());
					api.setProjectID(projectID);
					api.setApiNoteType(baseInfo.getInteger("apiNoteType"));
					api.setApiNoteRaw(baseInfo.getString("apiNoteRaw"));
					api.setApiNote(baseInfo.getString("apiNote"));
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = null;
					try
					{
						date = dateFormat.parse(baseInfo.getString("apiUpdateTime"));
					}
					catch (ParseException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Timestamp updateTime = new Timestamp(date.getTime());
					api.setApiUpdateTime(updateTime);
					api.setApiRequestParamType(baseInfo.getInteger("apiRequestParamType"));
					api.setApiRequestRaw(baseInfo.getString("apiRequestRaw"));
					api.setUpdateUserID(userID);
					api.setMockConfig(mockInfo.getString("mockConfig"));
					api.setMockRule(mockInfo.getString("mockRule"));
					api.setMockResult(mockInfo.getString("mockResult"));
					if (apiMapper.addApi(api) < 1)
						throw new RuntimeException("addApi error");
					ApiCache apiCache = new ApiCache();
					apiCache.setApiID(api.getApiID());
					apiCache.setApiJson(JSON.toJSONString(apiInfo));
					apiCache.setGroupID(api.getGroupID());
					apiCache.setProjectID(api.getProjectID());
					apiCache.setStarred(api.getStarred());
					apiCache.setUpdateUserID(api.getUpdateUserID());
					if (apiCacheMapper.addApiCache(apiCache) < 1)
						throw new RuntimeException("addApiCache error");
					JSONArray headerList = (JSONArray) apiInfo.get("headerInfo");
					if (headerList != null && !headerList.isEmpty())
					{
						for (Iterator<Object> iterator1 = headerList.iterator(); iterator1.hasNext();)
						{
							JSONObject headerInfo = (JSONObject) iterator1.next();
							ApiHeader header = new ApiHeader();
							header.setHeaderName(headerInfo.getString("headerName"));
							header.setHeaderValue(headerInfo.getString("headerValue"));
							header.setApiID(api.getApiID());
							if (apiMapper.addApiHeader(header) < 1)
								throw new RuntimeException("addApiHeader error");
						}
					}
					JSONArray requestParamList = (JSONArray) apiInfo.get("requestInfo");
					if (requestParamList != null && !requestParamList.isEmpty())
					{
						for (Iterator<Object> iterator1 = requestParamList.iterator(); iterator1.hasNext();)
						{
							JSONObject requestInfo = (JSONObject) iterator1.next();
							ApiRequestParam requestParam = new ApiRequestParam();
							requestParam.setApiID(api.getApiID());
							requestParam.setParamName(requestInfo.getString("paramName"));
							requestParam.setParamKey(requestInfo.getString("paramKey"));
							requestParam.setParamValue(requestInfo.getString("paramValue"));
							requestParam.setParamType(requestInfo.getInteger("paramType"));
							requestParam.setParamLimit(requestInfo.getString("paramLimit"));
							requestParam.setParamNotNull(requestInfo.getInteger("paramNotNull"));
							if (apiMapper.addRequestParam(requestParam) < 1)
								throw new RuntimeException("addRequestParam error");
							JSONArray paramValueList = (JSONArray) requestInfo.get("paramValueList");
							for (Iterator<Object> iterator2 = paramValueList.iterator(); iterator2.hasNext();)
							{
								JSONObject paramValue = (JSONObject) iterator2.next();
								ApiRequestValue apiRequestValue = new ApiRequestValue();
								apiRequestValue.setParamID(requestParam.getParamID());
								apiRequestValue.setValue(paramValue.getString("value"));
								apiRequestValue.setValueDescription(paramValue.getString("valueDescription"));
								if (apiMapper.addRequestValue(apiRequestValue) < 1)
									throw new RuntimeException("apiRequestValue error");
							}
						}
					}
					JSONArray resultParamList = (JSONArray) apiInfo.get("resultInfo");
					if (resultParamList != null && !resultParamList.isEmpty())
					{
						for (Iterator<Object> iterator1 = resultParamList.iterator(); iterator1.hasNext();)
						{
							JSONObject resultInfo = (JSONObject) iterator1.next();
							ApiResultParam resultParam = new ApiResultParam();
							resultParam.setApiID(api.getApiID());
							resultParam.setParamName(resultInfo.getString("paramName"));
							resultParam.setParamKey(resultInfo.getString("paramKey"));
							resultParam.setParamNotNull(resultInfo.getInteger("paramNotNull"));
							if (apiMapper.addResultParam(resultParam) < 1)
								throw new RuntimeException("addResultParam error");
							JSONArray paramValueList = (JSONArray) resultInfo.get("paramValueList");
							for (Iterator<Object> iterator2 = paramValueList.iterator(); iterator2.hasNext();)
							{
								JSONObject paramValue = (JSONObject) iterator2.next();
								ApiResultValue apiResultValue = new ApiResultValue();
								apiResultValue.setParamID(resultParam.getParamID());
								apiResultValue.setValue(paramValue.getString("value"));
								apiResultValue.setValueDescription(paramValue.getString("valueDescription"));
								if (apiMapper.addResultValue(apiResultValue) < 1)
									throw new RuntimeException("addResultValue error");
							}
						}
					}
				}
			}
			JSONArray childGroupList = JSONArray.parseArray(groupInfo.getString("childGroupList"));
			if (childGroupList != null && !childGroupList.isEmpty())
			{
				for (Iterator<Object> iterator = childGroupList.iterator(); iterator.hasNext();)
				{
					JSONObject childGroupInfo = (JSONObject) iterator.next();
					ApiGroup apiChildGroup = new ApiGroup();
					apiChildGroup.setGroupName(childGroupInfo.getString("groupName"));
					apiChildGroup.setIsChild(1);
					apiChildGroup.setProjectID(projectID);
					apiChildGroup.setParentGroupID(apiGroup.getGroupID());
					if (apiGroupMapper.addApiGroup(apiChildGroup) < 1)
						throw new RuntimeException("addApiChildGroup error");
					JSONArray apiList1 = JSONArray.parseArray(childGroupInfo.getString("apiList"));
					if (apiList1 != null && !apiList1.isEmpty())
					{
						for (Iterator<Object> iterator1 = apiList1.iterator(); iterator1.hasNext();)
						{
							JSONObject apiInfo = (JSONObject) iterator1.next();
							JSONObject baseInfo = (JSONObject) apiInfo.get("baseInfo");
							JSONObject mockInfo = (JSONObject) apiInfo.get("mockInfo");
							Api api = new Api();
							api.setApiName(baseInfo.getString("apiName"));
							api.setApiURI(baseInfo.getString("apiURI"));
							api.setApiProtocol(baseInfo.getInteger("apiProtocol"));
							api.setApiSuccessMock(baseInfo.getString("apiSuccessMock"));
							api.setApiFailureMock(baseInfo.getString("apiFailureMock"));
							api.setApiRequestType(baseInfo.getInteger("apiRequestType"));
							api.setApiStatus(baseInfo.getInteger("apiStatus"));
							api.setStarred(baseInfo.getInteger("starred"));
							api.setGroupID(apiChildGroup.getGroupID());
							api.setProjectID(projectID);
							api.setApiNoteType(baseInfo.getInteger("apiNoteType"));
							api.setApiNoteRaw(baseInfo.getString("apiNoteRaw"));
							api.setApiNote(baseInfo.getString("apiNote"));
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date date = null;
							try
							{
								date = dateFormat.parse(baseInfo.getString("apiUpdateTime"));
							}
							catch (ParseException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Timestamp updateTime = new Timestamp(date.getTime());
							api.setApiUpdateTime(updateTime);
							api.setApiRequestParamType(baseInfo.getInteger("apiRequestParamType"));
							api.setApiRequestRaw(baseInfo.getString("apiRequestRaw"));
							api.setUpdateUserID(userID);
							api.setMockConfig(mockInfo.getString("mockConfig"));
							api.setMockRule(mockInfo.getString("mockRule"));
							api.setMockResult(mockInfo.getString("mockResult"));
							if (apiMapper.addApi(api) < 1)
								throw new RuntimeException("addApi error");
							ApiCache apiCache = new ApiCache();
							apiCache.setApiID(api.getApiID());
							apiCache.setApiJson(JSON.toJSONString(apiInfo));
							apiCache.setGroupID(api.getGroupID());
							apiCache.setProjectID(api.getProjectID());
							apiCache.setStarred(api.getStarred());
							apiCache.setUpdateUserID(api.getUpdateUserID());
							if (apiCacheMapper.addApiCache(apiCache) < 1)
								throw new RuntimeException("addApiCache error");
							JSONArray headerList = (JSONArray) apiInfo.get("headerInfo");
							if (headerList != null && !headerList.isEmpty())
							{
								for (Iterator<Object> iterator11 = headerList.iterator(); iterator11.hasNext();)
								{
									JSONObject headerInfo = (JSONObject) iterator11.next();
									ApiHeader header = new ApiHeader();
									header.setHeaderName(headerInfo.getString("headerName"));
									header.setHeaderValue(headerInfo.getString("headerValue"));
									header.setApiID(api.getApiID());
									if (apiMapper.addApiHeader(header) < 1)
										throw new RuntimeException("addApiHeader error");
								}
							}
							JSONArray requestParamList = (JSONArray) apiInfo.get("requestInfo");
							if (requestParamList != null && !requestParamList.isEmpty())
							{
								for (Iterator<Object> iterator11 = requestParamList.iterator(); iterator11.hasNext();)
								{
									JSONObject requestInfo = (JSONObject) iterator11.next();
									ApiRequestParam requestParam = new ApiRequestParam();
									requestParam.setApiID(api.getApiID());
									requestParam.setParamName(requestInfo.getString("paramName"));
									requestParam.setParamKey(requestInfo.getString("paramKey"));
									requestParam.setParamValue(requestInfo.getString("paramValue"));
									requestParam.setParamType(requestInfo.getInteger("paramType"));
									requestParam.setParamLimit(requestInfo.getString("paramLimit"));
									requestParam.setParamNotNull(requestInfo.getInteger("paramNotNull"));
									if (apiMapper.addRequestParam(requestParam) < 1)
										throw new RuntimeException("addRequestParam error");
									JSONArray paramValueList = (JSONArray) requestInfo.get("paramValueList");
									for (Iterator<Object> iterator2 = paramValueList.iterator(); iterator2.hasNext();)
									{
										JSONObject paramValue = (JSONObject) iterator2.next();
										ApiRequestValue apiRequestValue = new ApiRequestValue();
										apiRequestValue.setParamID(requestParam.getParamID());
										apiRequestValue.setValue(paramValue.getString("value"));
										apiRequestValue.setValueDescription(paramValue.getString("valueDescription"));
										if (apiMapper.addRequestValue(apiRequestValue) < 1)
											throw new RuntimeException("apiRequestValue error");
									}
								}
							}
							JSONArray resultParamList = (JSONArray) apiInfo.get("resultInfo");
							if (resultParamList != null && !resultParamList.isEmpty())
							{
								for (Iterator<Object> iterator11 = resultParamList.iterator(); iterator11.hasNext();)
								{
									JSONObject resultInfo = (JSONObject) iterator11.next();
									ApiResultParam resultParam = new ApiResultParam();
									resultParam.setApiID(api.getApiID());
									resultParam.setParamName(resultInfo.getString("paramName"));
									resultParam.setParamKey(resultInfo.getString("paramKey"));
									resultParam.setParamNotNull(resultInfo.getInteger("paramNotNull"));
									if (apiMapper.addResultParam(resultParam) < 1)
										throw new RuntimeException("addResultParam error");
									JSONArray paramValueList = (JSONArray) resultInfo.get("paramValueList");
									for (Iterator<Object> iterator2 = paramValueList.iterator(); iterator2.hasNext();)
									{
										JSONObject paramValue = (JSONObject) iterator2.next();
										ApiResultValue apiResultValue = new ApiResultValue();
										apiResultValue.setParamID(resultParam.getParamID());
										apiResultValue.setValue(paramValue.getString("value"));
										apiResultValue.setValueDescription(paramValue.getString("valueDescription"));
										if (apiMapper.addResultValue(apiResultValue) < 1)
											throw new RuntimeException("addResultValue error");
									}
								}
							}
						}
					}
				}
			}
			Date date = new Date();
			Timestamp updateTime = new Timestamp(date.getTime());
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpDesc("导入接口分组'" + apiGroup.getGroupName() + "'");
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_API_GROUP);
			projectOperationLog.setOpTargetID(projectID);
			projectOperationLog.setOpTime(updateTime);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
			projectOperationLog.setOpUerID(userID);
			projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return true;

		}
		return false;
	}

}
