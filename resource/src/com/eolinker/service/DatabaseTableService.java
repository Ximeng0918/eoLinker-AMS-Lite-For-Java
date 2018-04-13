package com.eolinker.service;

import java.util.List;

import com.eolinker.pojo.DatabaseTable;

public interface DatabaseTableService {

	public int addTable(Integer dbID, String tableName, String tableDesc);
	
	public Integer checkTablePermission(int tableID,int userID);
	
	public int deleteTable(int tableID);
	
	public int getUserType(int tableID);
	
	public List<DatabaseTable> getTable(int dbID);
	
	public int editTable(int tableID, String tableName, String tableDesc);
	
}
