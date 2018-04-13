package com.eolinker.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.eolinker.mapper.ConnDatabaseMapper;
import com.eolinker.mapper.DatabaseMapper;
import com.eolinker.mapper.DatabaseTableFieldMapper;
import com.eolinker.mapper.DatabaseTableMapper;
import com.eolinker.pojo.ConnDatabase;
import com.eolinker.pojo.DatabaseTableField;
import com.eolinker.service.DatabaseTableFieldService;

@Service
@Transactional
public class DatabaseTableFieldServiceImpl implements DatabaseTableFieldService {

	@Autowired
	private DatabaseMapper databaseMapper;
	
	@Autowired
	private DatabaseTableMapper databaseTableMapper;
	
	@Autowired
	private DatabaseTableFieldMapper databaseTableFieldMapper;
	
	@Autowired
	private ConnDatabaseMapper connDatabaseMapper;
	
	@Override
	public int getUserType(int fieldID) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);		
		
		Integer dbID = databaseTableFieldMapper.checkFieldPermission(fieldID, userID);
		
		if(dbID == null)
			return -1;
		else{
			ConnDatabase connDatabase = new ConnDatabase();
			connDatabase.setUserID(userID);
			connDatabase.setDbID(dbID);
			
			ConnDatabase userType = this.connDatabaseMapper.getDatabaseUserType(connDatabase);
			if(userType == null)
				return -1;
			else
				return userType.getUserType();
		}
	}

	
	
	@Override
	public int addField(DatabaseTableField databaseTableField) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);	
		
		Integer dbID = this.databaseTableMapper.checkTablePermission(Integer.valueOf(databaseTableField.getTableID()), userID);
		
		if(dbID != null && dbID > 0) {
			this.databaseMapper.updateDatabaseUpdateTime(dbID);
			int affectedRow = this.databaseTableFieldMapper.addField(databaseTableField);
			
			if(affectedRow > 0)
				return Integer.valueOf(databaseTableField.getFieldID());
			else
				return -1;
		} else
			return -1;
	}



	@Override
	public int deleteField(int fieldID) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);	
		
		Integer dbID = this.databaseTableFieldMapper.checkFieldPermission(fieldID, userID);
		if(dbID != null && dbID > 0) {
			this.databaseMapper.updateDatabaseUpdateTime(dbID);
			int affectedRow = this.databaseTableFieldMapper.deleteField(fieldID);
			
			if(affectedRow > 0)
				return 1;
			else
				return -1;			
		} else
			return -1;
	}



	@Override
	public List<DatabaseTableField> getField(int tableID) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);			
		Integer dbID = this.databaseTableFieldMapper.checkFieldPermission(tableID, userID);
		
		if(dbID != null && dbID > 0) {
			this.databaseMapper.updateDatabaseUpdateTime(dbID);
			List<DatabaseTableField> fieldList = this.databaseTableFieldMapper.getField(tableID);
			
			if(fieldList == null || fieldList.isEmpty())
				return null;
			else
				return fieldList;
		} else
			return null;
	}



	@Override
	public int editField(DatabaseTableField databaseTableField) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);			
		Integer dbID = this.databaseTableFieldMapper.checkFieldPermission(Integer.valueOf(databaseTableField.getFieldID()), userID);
		
		if(dbID != null && dbID > 0) {
			this.databaseMapper.updateDatabaseUpdateTime(dbID);
			int affectedRow = this.databaseTableFieldMapper.editField(databaseTableField);
			
			return (affectedRow > 0) ? 1 : -1;
		} else
			return -1;
	}

	
}
