package com.eolinker.service;

import java.util.List;

import com.eolinker.pojo.DatabaseTableField;

public interface DatabaseTableFieldService {

	/**
	 * 获取数据字典用户类型
	 * @param fieldID
	 * @return
	 */
	public int getUserType(int fieldID);
	
	
	/**
	 * 添加字段
	 * @param databaseTableField
	 * @return
	 */
	public int addField(DatabaseTableField databaseTableField);
	
	
	/**
	 * 删除字段
	 * @param fieldID
	 * @return
	 */
	public int deleteField(int fieldID);
	
	
	
	/**
	 * 获取字段列表
	 * @param tableID
	 * @return
	 */
	public List<DatabaseTableField> getField(int tableID);
	
	
	/**
	 * 修改字段
	 * @param databaseTableField
	 * @return
	 */
	public int editField(DatabaseTableField databaseTableField);
	
}
