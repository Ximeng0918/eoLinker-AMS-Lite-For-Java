package com.eolinker.service;

import java.util.List;
import java.util.Map;

import com.eolinker.dto.DocumentDTO;
import com.eolinker.pojo.Document;

public interface DocumentService {

	/**
	 * 获取项目用户类型
	 * @param documentID
	 * @return
	 */
	public int getUserType(int documentID) throws RuntimeException;
	
	/**
	 * 添加文档
	 * @param document
	 * @return
	 * @throws RuntimeException
	 */
	public int addDocument(Document document) throws RuntimeException;
	
	/**
	 * 修改文档
	 * @param document
	 * @return
	 * @throws RuntimeException
	 */
	public int editDocument(Document document) throws RuntimeException; 
	
	/**
	 * 获取文档列表
	 * @return
	 * @throws RuntimeException
	 */
	public List<DocumentDTO> getDocumentList(int groupID) throws RuntimeException; 
	
	/**
	 * 获取文档列表
	 * @param projectID
	 * @return
	 * @throws RuntimeException
	 */
	public List<DocumentDTO> getAllDocumentList(int projectID) throws RuntimeException; 
	
	/**
	 * 搜索文档
	 * @param projectID
	 * @param tips
	 * @return
	 * @throws RuntimeException
	 */
	public List<DocumentDTO> searchDocument(int projectID, String tips) throws RuntimeException;
	
	
	/**
	 * 获取文档详情
	 * @param documentID
	 * @return
	 * @throws RuntimeException
	 */
	public Map<String, Object> getDocument(int documentID) throws RuntimeException;
	
	
	
	/**
	 * 批量文档
	 * @return
	 * @throws RuntimeException
	 */
	public int deleteBatchDocument(List<Integer> documentIDs, int projectID) throws RuntimeException;
}
