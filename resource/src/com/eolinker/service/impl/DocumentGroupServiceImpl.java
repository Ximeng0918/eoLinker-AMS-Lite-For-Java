package com.eolinker.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.eolinker.service.DocumentGroupService;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "java.lang.Exception")
public class DocumentGroupServiceImpl implements DocumentGroupService
{

	@Autowired
	private DocumentGroupMapper documentGroupMapper;

	@Autowired
	private PartnerMapper partnerMapper;

	@Autowired
	private DocumentMapper documentMapper;

	@Autowired
	private ProjectMapper projectMapper;

	@Autowired
	private ProjectOperationLogMapper projectOperationLogMapper;

	@Override
	public int getUserType(int groupID)
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Integer projectID = this.documentGroupMapper.checkGroupPermission(groupID, userID);
		if (projectID == null)
			return -1;

		Partner userType = this.partnerMapper.getProjectUserType(userID, projectID);
		if (userType == null)
			return -1;
		else
			return userType.getUserType();
	}

	@Override
	public int addGroup(DocumentGroup documentGroup) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Project project = this.projectMapper.getProject(userID, documentGroup.getProjectID());
		if (project == null)
			return -1;

		if (documentGroup.getParentGroupID() == null)
		{
			Integer affectedRow = this.documentGroupMapper.addDocumentGroup(documentGroup);

			if (affectedRow < 1)
				throw new RuntimeException("addDocumentGroup error");
			else
			{
				this.projectMapper.updateProjectUpdateTime(documentGroup.getProjectID(), null);

				ProjectOperationLog projectOperationLog = new ProjectOperationLog();
				projectOperationLog.setOpProjectID(documentGroup.getProjectID());
				projectOperationLog.setOpUerID(userID);
				projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT_DOCUMENT_GROUP);
				projectOperationLog.setOpTargetID(documentGroup.getGroupID());
				projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
				projectOperationLog.setOpDesc("添加项目文档分组:" + documentGroup.getGroupName());

				this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);

				return documentGroup.getGroupID();
			}
		}
		else
		{
			int affectedRow = this.documentGroupMapper.addChildGroup(documentGroup);
			if (affectedRow < 1)
				throw new RuntimeException("addChildGroup error");
			else
			{
				Integer projectID = this.documentGroupMapper.checkGroupPermission(documentGroup.getParentGroupID(),
						userID);
				if (projectID == null || projectID < 1)
					throw new RuntimeException("addChildGroup error");

				this.projectMapper.updateProjectUpdateTime(documentGroup.getProjectID(), null);

				String parentGroupName = this.documentGroupMapper.getGroupName(documentGroup.getParentGroupID());

				ProjectOperationLog projectOperationLog = new ProjectOperationLog();
				projectOperationLog.setOpProjectID(documentGroup.getProjectID());
				projectOperationLog.setOpUerID(userID);
				projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT_DOCUMENT_GROUP);
				projectOperationLog.setOpTargetID(documentGroup.getGroupID());
				projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
				projectOperationLog.setOpDesc("添加项目文档子分组" + parentGroupName + ">>" + documentGroup.getGroupName());

				this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);

				return documentGroup.getGroupID();
			}
		}

	}

	@Override
	public int deleteGroup(int groupID) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Integer projectID = this.documentGroupMapper.checkGroupPermission(groupID, userID);
		if (projectID == null)
			return -1;

		String groupName = this.documentGroupMapper.getGroupName(groupID);

		int affectedRow = this.documentGroupMapper.deleteParentalGroup(groupID);
		if (affectedRow < 1)
			throw new RuntimeException("deleteParentalGroup error");
		else
		{
			documentGroupMapper.deleteChildrenGroup(groupID);
			documentGroupMapper.deleteGroupDocument(groupID);
		}
		ProjectOperationLog projectOperationLog = new ProjectOperationLog();
		projectOperationLog.setOpProjectID(projectID);
		projectOperationLog.setOpUerID(userID);
		projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT_DOCUMENT_GROUP);
		projectOperationLog.setOpTargetID(groupID);
		projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_DELETE);
		projectOperationLog.setOpDesc("删除项目文档分组:" + groupName);

		this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);

		return 1;
	}

	@Override
	public Map<String, Object> getGroupList(int projectID) throws RuntimeException
	{
		Map<String, Object> groupList = new HashMap<String, Object>();

		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Project project = this.projectMapper.getProject(userID, projectID);
		if (project == null)
			return null;

		List<Object> resultList = new ArrayList<Object>();
		List<DocumentGroup> parentList = this.documentGroupMapper.getParentalGroup(projectID);
		if (parentList != null && !parentList.isEmpty())
		{
			for (DocumentGroup group : parentList)
			{
				List<DocumentGroup> childList = this.documentGroupMapper.getChildrenGroup(projectID,
						group.getGroupID());
				Map<String, Object> tempMap = new HashMap<String, Object>();

				tempMap.put("groupID", group.getGroupID());
				tempMap.put("groupName", group.getGroupName());
				tempMap.put("childGroupList", childList);

				resultList.add(tempMap);
			}
		}

		groupList.put("groupList", resultList);
		groupList.put("groupOrder", this.documentGroupMapper.getOrderList(projectID));

		return groupList;
	}

	@Override
	public int editGroup(DocumentGroup documentGroup) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Integer projectID = this.documentGroupMapper.checkGroupPermission(documentGroup.getGroupID(), userID);
		if (projectID == null || projectID < 1)
			return -1;
		if (documentGroup.getParentGroupID() != null)
		{
			projectID = this.documentGroupMapper.checkGroupPermission(documentGroup.getParentGroupID(), userID);
			if (projectID == null || projectID < 1)
				return -1;
		}

		this.projectMapper.updateProjectUpdateTime(projectID, null);

		int affectedRow = -1;
		if (documentGroup.getParentGroupID() == null || documentGroup.getParentGroupID() < 1)
		{
			affectedRow = this.documentGroupMapper.editParentalGroup(documentGroup);
		}
		else
		{
			affectedRow = this.documentGroupMapper.editChildrenGroup(documentGroup);
		}

		if (affectedRow < 1)
			throw new RuntimeException("editGroup error");
		else
		{
			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpUerID(userID);
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT_DOCUMENT_GROUP);
			projectOperationLog.setOpTargetID(documentGroup.getGroupID());
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_UPDATE);
			projectOperationLog.setOpDesc("修改项目文档分组:" + documentGroup.getGroupName());

			this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
		}

		return 1;
	}

	@Override
	public int sortGroup(int projectID, String orderList) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Project project = this.projectMapper.getProject(userID, projectID);
		if (project == null)
			return -1;
		else
		{
			int affectedRow = this.documentGroupMapper.updateGroupOrder(projectID, orderList);
			if (affectedRow < 1)
				throw new RuntimeException("sortGroup error");
			else
			{
				this.projectMapper.updateProjectUpdateTime(projectID, null);

				ProjectOperationLog projectOperationLog = new ProjectOperationLog();
				projectOperationLog.setOpProjectID(projectID);
				projectOperationLog.setOpUerID(userID);
				projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_STATUS_CODE_GROUP);
				projectOperationLog.setOpTargetID(projectID);
				projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_UPDATE);
				projectOperationLog.setOpDesc("修改项目文档分组排序");

				this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);

				return 1;
			}
		}
	}

	@Override
	public String exportGroup(HttpServletRequest request, int groupID) throws RuntimeException, IOException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		String userName = (String) requestAttributes.getAttribute("userName", RequestAttributes.SCOPE_SESSION);

		Map<String, Object> data = new HashMap<String, Object>();

		Integer projectID = this.documentGroupMapper.checkGroupPermission(groupID, userID);
		if (projectID == null || projectID < 1)
			return null;

		DocumentGroup parentGroup = this.documentGroupMapper.getGroupData(projectID, groupID);
		List<Document> pageList = this.documentGroupMapper.getDocumentData(projectID, groupID);

		List<Map<String, Object>> childrenList = new ArrayList<Map<String, Object>>();
		if (parentGroup != null && parentGroup.getIsChild() == 0)
		{
			List<DocumentGroup> childrenGroup = this.documentGroupMapper.getChildrenGroupData(projectID, groupID);
			if (childrenGroup != null && !childrenGroup.isEmpty())
			{
				for (DocumentGroup group : childrenGroup)
				{
					List<Document> documentData = this.documentGroupMapper.getDocumentData(projectID,
							group.getGroupID());
					Map<String, Object> tempMap = new HashMap<String, Object>();

					tempMap.put("pageList", documentData);
					tempMap.put("groupName", group.getGroupName());

					childrenList.add(tempMap);
				}
			}
		}

		data.put("pageList", pageList);
		data.put("groupName", parentGroup.getGroupName());
		data.put("childGroupList", childrenList);

		String documentInfo = JSON.toJSONString(data);
		HttpSession session =  request.getSession(true);
		String path = session.getServletContext().getRealPath("/dump") + "/";
		String fileName = "eoLinker_document_group_export_" + userName + "_" + new Date().getTime() + ".export";
		File file = new File(path+fileName);
		file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(documentInfo);
		writer.flush();
		writer.close();

		ProjectOperationLog projectOperationLog = new ProjectOperationLog();
		projectOperationLog.setOpProjectID(projectID);
		projectOperationLog.setOpUerID(userID);
		projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT_DOCUMENT_GROUP);
		projectOperationLog.setOpTargetID(groupID);
		projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_OTHERS);
		projectOperationLog.setOpDesc("导出文档分组：" + this.documentGroupMapper.getGroupName(groupID));

		this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);

		return request.getContextPath()+"/dump/"+fileName;
	}

	@Override
	public int importGroup(int projectID, String data) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		JSONObject parseObject = JSON.parseObject(data);
		String groupName = parseObject.getString("groupName");
		List<Document> pageList = JSON.parseArray(parseObject.getString("pageList"), Document.class);
		JSONArray childGroupList = JSON.parseArray(parseObject.getString("childGroupList"));

		Partner member = this.partnerMapper.getProjectUserType(userID, projectID);
		if (member == null)
			return -1;

		// 插入分组
		DocumentGroup documentGroup = new DocumentGroup();
		documentGroup.setProjectID(projectID);
		documentGroup.setGroupName(groupName);
		int affectedRow = this.documentGroupMapper.addDocumentGroup(documentGroup);
		if (affectedRow < 1)
			throw new RuntimeException("addDocumentGroup error");

		int groupID = documentGroup.getGroupID();

		// 插入文档
		if (pageList != null && !pageList.isEmpty())
		{
			for (Document document : pageList)
			{

				document.setGroupID(groupID);
				document.setProjectID(projectID);
				document.setUserID(userID);

				affectedRow = this.documentMapper.addDocument(document);
				if (affectedRow < 1)
					throw new RuntimeException("addDocument error");
			}
		}

		if (childGroupList != null && !childGroupList.isEmpty())
		{
			int parentGroupID = groupID;
			Iterator<Object> iterator = childGroupList.iterator();
			while (iterator.hasNext())
			{
				JSONObject jsonObject = (JSONObject) iterator.next();
				String childGroupName = jsonObject.getString("groupName");

				// 插入子分组
				DocumentGroup childDocumentGroup = new DocumentGroup();
				childDocumentGroup.setProjectID(projectID);
				childDocumentGroup.setGroupName(childGroupName);
				childDocumentGroup.setParentGroupID(parentGroupID);

				affectedRow = this.documentGroupMapper.addChildGroup(childDocumentGroup);
				if (affectedRow < 1)
					throw new RuntimeException("addChildGroup error");

				int childGroupID = childDocumentGroup.getGroupID();
				List<Document> childPageList = JSON.parseArray(jsonObject.getString("pageList"), Document.class);
				// 插入子分组文档
				for (Document childDoc : childPageList)
				{

					childDoc.setGroupID(childGroupID);
					childDoc.setProjectID(projectID);
					childDoc.setUserID(userID);

					affectedRow = this.documentMapper.addDocument(childDoc);
					if (affectedRow < 1)
						throw new RuntimeException("addDocument error");
				}
			}
		}

		ProjectOperationLog projectOperationLog = new ProjectOperationLog();
		projectOperationLog.setOpProjectID(projectID);
		projectOperationLog.setOpUerID(userID);
		projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT_DOCUMENT_GROUP);
		projectOperationLog.setOpTargetID(projectID);
		projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_OTHERS);
		projectOperationLog.setOpDesc("导入文档分组：" + groupName);

		this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);

		return 1;
	}

}
