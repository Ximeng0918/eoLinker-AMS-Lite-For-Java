package com.eolinker.service;

import java.io.InputStream;
import java.util.List;

import com.eolinker.dto.CodeListDTO;
import com.eolinker.pojo.StatusCode;

public interface StatusCodeService {
	
	/**
	 * 获取项目用户类型
	 * @param codeID
	 * @return
	 */
	public int getUserType(int codeID) throws RuntimeException;
	
	/**
	 * 添加状态码
	 * @param statusCode
	 * @return
	 * @throws RuntimeException
	 */
	public int addCode(StatusCode statusCode) throws RuntimeException;

	/**
	 * 删除状态码
	 * @param codeID
	 * @return
	 * @throws RuntimeException
	 */
	public int deleteCode(int codeID) throws RuntimeException;
	
	/**
	 * 批量删除状态码
	 * @return
	 * @throws RuntimeException
	 */
	public int deleteBatchCode(List<Integer> codeIDs) throws RuntimeException;
	
	
	/**
	 * 获取某组状态码列表
	 * @param groupID
	 * @return
	 * @throws RuntimeException
	 */
	public List<StatusCode> getCodeList(int groupID) throws RuntimeException;
	
	/**
	 * 获取所有状态码列表
	 * @param projectID
	 * @return
	 * @throws RuntimeException
	 */
	public List<CodeListDTO> getAllCodeList(int projectID) throws RuntimeException; 
	
	
	/**
	 * 修改状态码
	 * @param statusCode
	 * @return
	 * @throws RuntimeException
	 */
	public int editCode(StatusCode statusCode) throws RuntimeException; 

	/**
	 * 搜索状态码
	 * @param projectID
	 * @param tips
	 * @return
	 * @throws RuntimeException
	 */
	public List<CodeListDTO> searchStatusCode(int projectID, String tips) throws RuntimeException;
	
	
	/**
	 * 获取状态码数量
	 * @param projectID
	 * @return
	 */
	public int getStatusCodeNum(int projectID) throws RuntimeException;

	//导入excel表
	public boolean addStatusCodeByExcel(Integer projectID, Integer groupID, Integer userID, InputStream inputStream);

	
}
