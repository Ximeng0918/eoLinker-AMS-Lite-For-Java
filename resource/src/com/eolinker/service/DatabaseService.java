package com.eolinker.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.eolinker.pojo.ConnDatabase;
import com.eolinker.pojo.Database;

public interface DatabaseService {
	
	public ConnDatabase getUserType(int dbID);
	
	public Integer addDatabase(String dbName,double dbVersion);
	
	public Integer deleteDatabase(int dbID);
	
	public List<Database> getDatabase();
	
	public Integer editDatabase(int dbID, String dbName, Double dbVersion);
	
	public int importDatabase(int dbID, Map<String,Object> tables);
	
	public Integer importDatabseByJson(com.eolinker.vo.DatabaseVO database) throws Exception ;
	
	public String exportDatabase(HttpServletRequest request, int dbID) throws Exception ;
}
