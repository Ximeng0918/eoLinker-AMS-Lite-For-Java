package com.eolinker.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.eolinker.pojo.StatusCodeGroup;

public interface StatusCodeGroupService {

	/**
	 * 获取项目用户类型
	 * @param groupID
	 * @return
	 */
	public int getUserType(int groupID) throws RuntimeException;
	
	/**
	 * 添加状态码分组
	 * @param statusCodeGroup
	 * @return
	 * @throws RuntimeException
	 */
	public int addGroup(StatusCodeGroup statusCodeGroup) throws RuntimeException;
	
	/**
	 * 删除分组
	 * @param groupID
	 * @return
	 * @throws RuntimeException
	 */
	public int deleteGroup(int groupID) throws RuntimeException;
	
	/**
	 * 获取状态码分组列表
	 * @param projectID
	 * @return
	 * @throws RuntimeException
	 */
	public Map<String, Object> getGroupList(int projectID) throws RuntimeException;
	
	
	/**
	 * 修改状态码分组
	 * @param statusCodeGroup
	 * @return
	 * @throws RuntimeException
	 */
	public int editGroup(StatusCodeGroup statusCodeGroup) throws RuntimeException;
	
	
	/**
	 * 修改状态码分组列表排序
	 * @param projectID
	 * @return
	 * @throws RuntimeException
	 */
	public int sortGroup(int projectID, String orderList) throws RuntimeException;
	
	
	/**
	 * 导出分组
	 * @param groupID
	 * @return
	 * @throws RuntimeException
	 * @throws IOException
	 */
	public String exportGroup(HttpServletRequest request,int groupID) throws RuntimeException, IOException;
	
	
	
	/**
	 * 导入分组
	 * @param projectID
	 * @param data
	 * @return
	 * @throws RuntimeException
	 */
	public int importGroup(int projectID, String data) throws RuntimeException;
	
}
