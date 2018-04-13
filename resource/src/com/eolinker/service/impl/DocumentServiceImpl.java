package com.eolinker.service.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.eolinker.dto.DocumentDTO;
import com.eolinker.mapper.DocumentGroupMapper;
import com.eolinker.mapper.DocumentMapper;
import com.eolinker.mapper.ProjectMapper;
import com.eolinker.mapper.PartnerMapper;
import com.eolinker.mapper.ProjectOperationLogMapper;
import com.eolinker.pojo.Document;
import com.eolinker.pojo.DocumentGroup;
import com.eolinker.pojo.Project;
import com.eolinker.pojo.Partner;
import com.eolinker.pojo.ProjectOperationLog;
import com.eolinker.service.DocumentService;

@Service
@Transactional(propagation=Propagation.REQUIRED,rollbackForClassName="java.lang.Exception")
public class DocumentServiceImpl implements DocumentService {

	@Autowired
	private DocumentMapper documentMapper;
	
	@Autowired
	private DocumentGroupMapper documentGroupMapper;
	
	@Autowired
	private PartnerMapper partnerMapper;
	
	@Autowired
	private ProjectMapper projectMapper;

	@Autowired
	private ProjectOperationLogMapper projectOperationLogMapper;
	
	@Override
	public int getUserType(int documentID) throws RuntimeException {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Integer projectID = this.documentMapper.checkDocumentPermission(documentID, userID);
		if(projectID == null || projectID < 1)
			return -1;
		
		Partner projectUserType = this.partnerMapper.getProjectUserType(userID, projectID);
		if(projectUserType != null)
			return projectUserType.getUserType();
		else
			return -1;
	}
	
	
	@Override
	public int addDocument(Document document) throws RuntimeException {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Integer projectID = this.documentGroupMapper.checkGroupPermission(document.getGroupID(), userID);
		if(projectID == null || projectID < 1)
			return -1;
		else {
			this.projectMapper.updateProjectUpdateTime(projectID, null);
			document.setProjectID(projectID);
			document.setUserID(userID);
			if(this.documentMapper.addDocument(document) < 1)
				throw new RuntimeException("addDocument error");
			else{
				ProjectOperationLog projectOperationLog = new ProjectOperationLog();
				projectOperationLog.setOpProjectID(projectID);
				projectOperationLog.setOpUerID(userID);
				projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT_DOCUMENT);
				projectOperationLog.setOpTargetID(document.getDocumentID());
				projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
				projectOperationLog.setOpDesc("添加项目文档:"+document.getTitle());
				
				this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
				
				return document.getDocumentID();
			}
		}
	}


	@Override
	public int editDocument(Document document) throws RuntimeException {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Integer projectID = this.documentGroupMapper.checkGroupPermission(document.getGroupID(), userID);
		if(projectID == null || projectID  < 1)
			return -1;
		projectID = this.documentMapper.checkDocumentPermission(document.getDocumentID(), userID);
		if(projectID == null || projectID  < 1)
			return -1;
		
		this.projectMapper.updateProjectUpdateTime(document.getProjectID(), null);
		
		document.setProjectID(projectID);
		document.setUserID(userID);
		int affectedRow = this.documentMapper.editDocument(document);
		
		if(affectedRow < 1) 
			throw new RuntimeException("editCode error");
		else {
			
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpUerID(userID);
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT_DOCUMENT);
			projectOperationLog.setOpTargetID(document.getDocumentID());
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_DELETE);
			projectOperationLog.setOpDesc("修改项目文档:"+document.getTitle());
			
			this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			
			return 1;
		}
	}


	@Override
	public List<DocumentDTO> getDocumentList(int groupID) throws RuntimeException {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Integer projectID = this.documentGroupMapper.checkGroupPermission(groupID, userID);
		if(projectID == null || projectID < 1)
			return null;
		else {
			List<DocumentDTO> documentList = this.documentMapper.getDocumentList(groupID);
			return (documentList == null || documentList.isEmpty()) ? null : documentList;
		}
		
	}


	@Override
	public List<DocumentDTO> getAllDocumentList(int projectID) throws RuntimeException {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Project project = this.projectMapper.getProject(userID, projectID);
		if(project == null)
			return null;
		else {
			List<DocumentDTO> documentList = this.documentMapper.getAllDocumentList(projectID);
			return (documentList == null || documentList.isEmpty()) ? null : documentList;
		}
	}


	@Override
	public List<DocumentDTO> searchDocument(int projectID, String tips) throws RuntimeException {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Project project = this.projectMapper.getProject(userID, projectID);
		if(project == null)
			return null;
		else {
			List<DocumentDTO> resultList = this.documentMapper.searchDocument(projectID, tips);
			return (resultList == null || resultList.isEmpty()) ? null : resultList;
		}
	}


	@Override
	public Map<String, Object> getDocument(int documentID) throws RuntimeException {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Integer projectID = this.documentMapper.checkDocumentPermission(documentID, userID);
		if(projectID == null || projectID < 1)
			return null;
		else {
			Map<String, Object> document = documentMapper.getDocument(documentID);
			if(document != null && !document.isEmpty()){
				DocumentGroup parentGroupInfo = documentMapper.getParentGroupInfo(new Integer( document.get("groupID").toString()));
				if(parentGroupInfo != null)
				{
					document.put("parentGroupID", parentGroupInfo.getParentGroupID());
					document.put("parentGroupName", parentGroupInfo.getGroupName());
				}
				return document;
			}else 
				return null;
		}
	}


	@Override
	public int deleteBatchDocument(List<Integer> documentIDs, int projectID) throws RuntimeException {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Partner member = this.partnerMapper.getProjectUserType(userID, projectID);
		if(member == null)
			return -1;
		String documentTitles = this.documentMapper.getDocumentTitle(documentIDs);
		if(this.documentMapper.deleteDocuments(documentIDs, projectID) < 1)
			throw new RuntimeException("deleteDocuments error");
		else{
			this.projectMapper.updateProjectUpdateTime(projectID, null);
			
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpUerID(userID);
			projectOperationLog.setOpTargetID(0);
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT_DOCUMENT);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_DELETE);
			projectOperationLog.setOpDesc("删除项目文档:" + documentTitles);
			
			this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return 1;
		}
		
	}

	
	
	
	
}
