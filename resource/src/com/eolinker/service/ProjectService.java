package com.eolinker.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;


import com.eolinker.pojo.Project;
import com.eolinker.pojo.Partner;

public interface ProjectService
{

	//新增项目
	public Map<String, Object> addProject(Project project, HttpSession session);
	
	//获取用户类型
	public Partner getProjectUserType(Integer userID, Integer projectID);
	
	//删除项目
	public int deleteProject(Integer projectID);

	//获取项目列表
	public List<Map<String, Object>> getProjectList(Integer userID, Integer projectType);

	//修改项目
	public boolean editProject(Project project);

	public Map<String, Object> getProject(Integer userID, Integer projectID);

	public List<Map<String, Object>> getProjectLogList(Integer projectID, Integer page, Integer pageSize);

	public int getProjectLogCount(Integer projectID, int dayOffset);

	public int getApiNum(Integer projectID);

	public Map<String, Object> exportProjectData(Integer projectID, Integer userID);

}
