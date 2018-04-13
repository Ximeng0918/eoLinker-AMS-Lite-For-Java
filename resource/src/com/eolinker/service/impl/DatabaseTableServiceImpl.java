package com.eolinker.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.eolinker.mapper.ConnDatabaseMapper;
import com.eolinker.mapper.DatabaseMapper;
import com.eolinker.mapper.DatabaseTableMapper;
import com.eolinker.pojo.ConnDatabase;
import com.eolinker.pojo.DatabaseTable;
import com.eolinker.service.DatabaseTableService;

@Service
@Transactional
public class DatabaseTableServiceImpl implements DatabaseTableService {

	@Autowired
	private DatabaseTableMapper databaseTableMapper;
	
	@Autowired
	private ConnDatabaseMapper connDatabaseTableMapper;	
	
	@Autowired
	private DatabaseMapper databaseMapper;
	
	
	
	@Override
	public int addTable(Integer dbID, String tableName, String tableDesc) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Integer databaseID = connDatabaseTableMapper.checkDatabasePermission(Integer.valueOf(dbID), userID);
		
		if(databaseID != null && databaseID > 0){
			this.databaseMapper.updateDatabaseUpdateTime(Integer.valueOf(dbID));
			
			DatabaseTable databaseTable = new DatabaseTable();
			databaseTable.setDbID(databaseID);
			databaseTable.setTableName(tableName);
			databaseTable.setTableDescription(tableDesc);
			
			int affectedRow = this.databaseTableMapper.addTable(databaseTable);
			if(affectedRow > 0) 
				return databaseTable.getDbID();
			 else
				return 0;
		} else return 0;
	}



	@Override
	public Integer checkTablePermission(int tableID, int userID) {
		Integer dbID = this.databaseTableMapper.checkTablePermission(tableID, userID);
		if(dbID == null || dbID <= 0) {
			return 0;
		}else 
			return dbID;
	}



	@Override
	public int deleteTable(int tableID) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Integer dbID = this.databaseTableMapper.checkTablePermission(tableID, userID);
		if(dbID != null && dbID > 0){
			this.databaseMapper.updateDatabaseUpdateTime(dbID);
			int affectedRow = this.databaseTableMapper.deleteTable(tableID);
			if(affectedRow > 0)
				return 1;
			else
				return -1;
		} else{
			return -1;
		}
	}



	@Override
	public int getUserType(int tableID) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Integer dbID = this.databaseTableMapper.checkTablePermission(tableID, userID);
		if(dbID == null || dbID < 1)
			return -1;
		
		ConnDatabase connDatabase = new ConnDatabase();
		connDatabase.setUserID(userID);
		connDatabase.setDbID(dbID);
		
		ConnDatabase databaseUserType = this.connDatabaseTableMapper.getDatabaseUserType(connDatabase);
		if(databaseUserType == null)
			return -1;
		else
			return databaseUserType.getUserID();
	}



	@Override
	public List<DatabaseTable> getTable(int dbID) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Integer databaseID = this.connDatabaseTableMapper.checkDatabasePermission(dbID, userID);
		
		if(databaseID != null && databaseID > 0){
			this.databaseMapper.updateDatabaseUpdateTime(dbID);
			List<DatabaseTable> tableList = this.databaseTableMapper.getTable(dbID);
			return tableList;
		} else
			return null;
	}



	@Override
	public int editTable(int tableID, String tableName, String tableDesc) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int)requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		
		Integer dbID = this.databaseTableMapper.checkTablePermission(tableID, userID);
		
		if(dbID != null && dbID > 0){
			this.databaseMapper.updateDatabaseUpdateTime(dbID);
			
			DatabaseTable databaseTable = new DatabaseTable();
			databaseTable.setTableID(tableID);
			databaseTable.setTableName(tableName);
			databaseTable.setTableDescription(tableDesc);
			
			int affectedRow = this.databaseTableMapper.editTable(databaseTable);
			return (affectedRow > 0) ? 1 : -1;
		} else 
			return -1;
	}

}
