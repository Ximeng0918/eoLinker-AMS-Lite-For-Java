package com.eolinker.service.impl;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;

import java.io.InputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.eolinker.dto.CodeListDTO;
import com.eolinker.mapper.ProjectMapper;
import com.eolinker.mapper.PartnerMapper;
import com.eolinker.mapper.ProjectOperationLogMapper;
import com.eolinker.mapper.StatusCodeGroupMapper;
import com.eolinker.mapper.StatusCodeMapper;
import com.eolinker.pojo.Project;
import com.eolinker.pojo.Partner;
import com.eolinker.pojo.ProjectOperationLog;
import com.eolinker.pojo.StatusCode;
import com.eolinker.service.StatusCodeService;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "java.lang.Exception")
public class StatusCodeServiceImpl implements StatusCodeService
{

	@Autowired
	private StatusCodeMapper statusCodeMapper;

	@Autowired
	private StatusCodeGroupMapper statusCodeGroupMapper;

	@Autowired
	private PartnerMapper partnerMapper;

	@Autowired
	private ProjectMapper projectMapper;

	@Autowired
	private ProjectOperationLogMapper projectOperationLogMapper;

	@Override
	public int getUserType(int codeID) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Integer projectID = this.statusCodeMapper.checkStatusCodePermission(codeID, userID);
		if (projectID == null || projectID < 1)
			return -1;

		Partner projectUserType = this.partnerMapper.getProjectUserType(userID, projectID);
		if (projectUserType != null)
			return projectUserType.getUserType();
		else
			return -1;
	}

	@Override
	public int addCode(StatusCode statusCode) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Integer projectID = this.statusCodeGroupMapper.checkStatusCodeGroupPermission(statusCode.getGroupID(), userID);
		if (projectID == null || projectID < 1)
			return -1;
		else
		{
			this.projectMapper.updateProjectUpdateTime(projectID, null);
			if (this.statusCodeMapper.addCode(statusCode) < 1)
				throw new RuntimeException("addCode error");
			else
			{
				ProjectOperationLog projectOperationLog = new ProjectOperationLog();
				projectOperationLog.setOpProjectID(projectID);
				projectOperationLog.setOpUerID(userID);
				projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_STATUS_CODE);
				projectOperationLog.setOpTargetID(statusCode.getCodeID());
				projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
				projectOperationLog.setOpDesc("添加状态码:" + statusCode.getCode());

				this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);

				return statusCode.getCodeID();
			}
		}
	}

	@Override
	public int deleteCode(int codeID) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Integer projectID = this.statusCodeMapper.checkStatusCodePermission(codeID, userID);
		if (projectID == null || projectID < 1)
			return -1;
		else
		{
			if (this.statusCodeMapper.deleteCode(codeID) < 1)
				throw new RuntimeException("deleteCode error");
			else
			{
				this.projectMapper.updateProjectUpdateTime(projectID, null);

				ProjectOperationLog projectOperationLog = new ProjectOperationLog();
				projectOperationLog.setOpProjectID(projectID);
				projectOperationLog.setOpUerID(userID);
				projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_STATUS_CODE);
				projectOperationLog.setOpTargetID(codeID);
				projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_DELETE);
				projectOperationLog.setOpDesc("删除状态码:" + codeID);

				this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);

				return 1;
			}
		}
	}

	@Override
	public int deleteBatchCode(List<Integer> codeIDs) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Integer projectID = null;
		for (int codeID : codeIDs)
		{
			projectID = this.statusCodeMapper.checkStatusCodePermission(codeID, userID);
			if (projectID == null || projectID < 1)
				return -1;
			else
				continue;
		}
		String statusCodes = this.statusCodeMapper.getStatusCodes(codeIDs);
		if (this.statusCodeMapper.deleteCodes(codeIDs) < 1)
			throw new RuntimeException("deleteCodes error");
		else
		{
			this.projectMapper.updateProjectUpdateTime(projectID, null);

			ProjectOperationLog projectOperationLog = new ProjectOperationLog();
			projectOperationLog.setOpProjectID(projectID);
			projectOperationLog.setOpUerID(userID);
			projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_STATUS_CODE);
			projectOperationLog.setOpTargetID(0);
			projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_DELETE);
			projectOperationLog.setOpDesc("删除状态码:" + statusCodes);

			this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
			return 1;
		}

	}

	@Override
	public List<StatusCode> getCodeList(int groupID) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Integer projectID = this.statusCodeGroupMapper.checkStatusCodeGroupPermission(groupID, userID);
		if (projectID == null || projectID < 1)
			return null;
		else
		{
			List<StatusCode> codeList = this.statusCodeMapper.getCodeList(groupID);
			return (codeList == null || codeList.isEmpty()) ? null : codeList;
		}

	}

	@Override
	public List<CodeListDTO> getAllCodeList(int projectID) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Project project = this.projectMapper.getProject(userID, projectID);
		if (project == null)
			return null;
		else
		{
			List<CodeListDTO> codeList = this.statusCodeMapper.getAllCodeList(projectID);
			return (codeList == null || codeList.isEmpty()) ? null : codeList;
		}
	}

	@Override
	public int editCode(StatusCode statusCode) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Integer projectID = this.statusCodeMapper.checkStatusCodePermission(statusCode.getCodeID(), userID);
		if (projectID == null || projectID < 1)
			return -1;
		else
		{
			this.projectMapper.updateProjectUpdateTime(statusCode.getGroupID(), null);
			int affectedRow = this.statusCodeMapper.editCode(statusCode);

			if (affectedRow < 1)
				throw new RuntimeException("editCode error");
			else
			{
				ProjectOperationLog projectOperationLog = new ProjectOperationLog();
				projectOperationLog.setOpProjectID(projectID);
				projectOperationLog.setOpUerID(userID);
				projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_STATUS_CODE);
				projectOperationLog.setOpTargetID(statusCode.getCodeID());
				projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_UPDATE);
				projectOperationLog.setOpDesc("修改状态码:" + statusCode.getCodeID());

				this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);

				return 1;
			}
		}
	}

	@Override
	public List<CodeListDTO> searchStatusCode(int projectID, String tips) throws RuntimeException
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Project project = this.projectMapper.getProject(userID, projectID);
		if (project == null)
			return null;
		else
		{
			List<CodeListDTO> resultList = this.statusCodeMapper.searchStatusCode(projectID, tips);
			return (resultList == null || resultList.isEmpty()) ? null : resultList;
		}
	}

	@Override
	public int getStatusCodeNum(int projectID)
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Project project = this.projectMapper.getProject(userID, projectID);
		if (project == null)
			return -1;
		else
		{
			Integer count = this.statusCodeMapper.getStatusCodeCount(projectID);
			return (count != null) ? count : -1;
		}
	}

	@Override
	public boolean addStatusCodeByExcel(Integer projectID, Integer groupID, Integer userID, InputStream inputStream)
	{
		// TODO Auto-generated method stub
		try
		{
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
			for(int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet ++)
			{
				HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
				if(hssfSheet == null)
				{
					content();
				}
				for(int rowNum = 3; rowNum <= hssfSheet.getLastRowNum(); rowNum++)
				{
				}
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		return false;
	}

}
