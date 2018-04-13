package com.eolinker.service;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.eolinker.pojo.DocumentGroup;

public interface DocumentGroupService {

	public int getUserType(int groupID);
	
	/**
	 * 添加文档分组
	 * @param documentGroup
	 * @return
	 * @throws RuntimeException
	 */
	public int addGroup(DocumentGroup documentGroup) throws RuntimeException;
	
	/**
	 * 删除文档分组
	 * @param groupID
	 * @return
	 * @throws RuntimeException
	 */
	public int deleteGroup(int groupID) throws RuntimeException;
	
	/**
	 * 获取所有文档列表
	 * @param projectID
	 * @return
	 * @throws RuntimeException
	 */
	public Map<String,Object> getGroupList(int projectID) throws RuntimeException; 
	
	
	/**
	 * 修改文档
	 * @param statusCode
	 * @return
	 * @throws RuntimeException
	 */
	public int editGroup(DocumentGroup documentGroup) throws RuntimeException; 
	
	
	/**
	 * 修改文档分组列表排序
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
	 */
	public String exportGroup(HttpServletRequest request, int groupID) throws RuntimeException, IOException;
	
	/**
	 * 导入分组
	 * @param projectID
	 * @param data
	 * @return
	 * @throws RuntimeException
	 */
	public int importGroup(int projectID, String data) throws RuntimeException;
	
	
}
