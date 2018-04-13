package com.eolinker.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import com.eolinker.mapper.ConnDatabaseMapper;
import com.eolinker.mapper.DatabaseMapper;
import com.eolinker.mapper.DatabaseTableFieldMapper;
import com.eolinker.mapper.DatabaseTableMapper;
import com.eolinker.pojo.ConnDatabase;
import com.eolinker.pojo.Database;
import com.eolinker.pojo.DatabaseTable;
import com.eolinker.pojo.DatabaseTableField;
import com.eolinker.service.DatabaseService;
import com.eolinker.util.RegexMatch;
import com.eolinker.vo.DatabaseInfoVO;
import com.eolinker.vo.DatabaseTableFieldVO;
import com.eolinker.vo.DatabaseTableVO;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class DatabaseServiceImpl implements DatabaseService {

	@Autowired
	private DatabaseMapper databaseMapper;

	@Autowired
	private DatabaseTableMapper databaseTableMapper;

	@Autowired
	private DatabaseTableFieldMapper databaseTableFieldMapper;

	@Autowired
	private ConnDatabaseMapper connDatabaseMapper;

	@Override
	public ConnDatabase getUserType(int dbID) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		ConnDatabase connDatabase = new ConnDatabase();
		connDatabase.setDbID(dbID);
		connDatabase.setUserID(userID);

		ConnDatabase currentConnDatabase = connDatabaseMapper.getDatabaseUserType(connDatabase);

		return currentConnDatabase;
	}

	@Override
	public Integer addDatabase(String dbName, double dbVersion) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Database database = new Database();
		database.setDbName(dbName);
		database.setDbVersion(dbVersion);

		Integer affectedRow = databaseMapper.addDatabase(database);
		if (affectedRow != null) {
			affectedRow = this.connDatabaseMapper.addDatabaseConnection(database.getDbID(), userID);
		}

		return (affectedRow != 0) ? database.getDbID() : null;

	}

	@Override
	public Integer deleteDatabase(int dbID) {
		Integer affectedRow = this.databaseMapper.deleteDatabase(dbID);
		return (affectedRow != null) ? affectedRow : null;
	}

	@Override
	public List<Database> getDatabase() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		List<Database> databases = this.databaseMapper.getDatabase(userID);
		return (databases != null && !databases.isEmpty()) ? databases : null;
	}

	@Override
	public Integer editDatabase(int dbID, String dbName, Double dbVersion) {
		Database database = new Database();
		database.setDbID(dbID);
		database.setDbName(dbName);
		database.setDbVersion(dbVersion);
		int affectedRow = this.databaseMapper.editDatabase(database);
		return (affectedRow != 0) ? affectedRow : null;
	}

	@Override
	public int importDatabase(int dbID, Map<String, Object> tables) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		Integer projectID = this.connDatabaseMapper.checkDatabasePermission(dbID, userID);
		if (projectID == null || projectID < 1)
			return -1;

		String tableName = (String)tables.get("tableName");
		
		// 将各字段信息分割成一行一个
		RegexMatch tableFields = new RegexMatch("\\s{2,5}`(.+?)`\\s(.+?)\\s{0,1}(.+?),", tables.get("tableField").toString());

		for (String field : tableFields.getList()) {
			// 提取字段名
			RegexMatch fieldNameMatch = new RegexMatch("`(.*?)`", field);
			String fieldName = fieldNameMatch.getList().get(0).replaceAll("`", "");

			// 提取字段长度
			RegexMatch fieldLengthMatch = new RegexMatch("[0-9]{1,10}", field);
			String fieldLength = "0";
			if(!fieldLengthMatch.getList().isEmpty()){
				fieldLength = fieldLengthMatch.getList().get(0);
				System.out.println(fieldLengthMatch.getList().get(0));
			}
			
			// 提取字段类型
			String fieldType = null;
			if(!"0".equals(fieldLength)) {
				RegexMatch fieldTypeMatch = new RegexMatch("`\\s(.*?)\\(", field);
				fieldType = fieldTypeMatch.getList().get(0).replaceAll("[\\s\\(`]", "");
			} else {
				RegexMatch fieldTypeMatch = new RegexMatch("`\\s{1,10}(.+?)(\\s|,)", field);
				fieldType = fieldTypeMatch.getList().get(0).replaceAll("[\\s`,]", "");
			}
			
			// 提取isNotNull
			RegexMatch isNotNullMatch = new RegexMatch("NOT NULL", field);
			int isNotNull = 0;
			if(!isNotNullMatch.getList().isEmpty() && isNotNullMatch.getList().get(0) != null)
				isNotNull = 1;
			else
				isNotNull = 0;
			
			int isPrimaryKey = 0;
			for(String pk : (List<String>)tables.get("primaryKey")) {
				if(fieldName.equalsIgnoreCase(pk)){
					isPrimaryKey = 1;
					break;
				}
			}
			
			DatabaseTable newTable = new DatabaseTable();
			newTable.setDbID(dbID);
			newTable.setTableName(tableName);
			int affectedRow = this.databaseTableMapper.addTable(newTable);
			if(affectedRow < 1)
				throw new RuntimeException("addTable error");
			
			int tableID = newTable.getTableID();
			
			DatabaseTableField newField = new DatabaseTableField();
			newField.setTableID(String.valueOf(tableID));
			newField.setFieldName(fieldName);
			newField.setFieldLength(fieldLength);
			newField.setFieldType(fieldType);
			newField.setIsNotNull(String.valueOf(isNotNull));
			newField.setIsPrimaryKey(String.valueOf(isPrimaryKey));
			
			affectedRow = this.databaseTableFieldMapper.addField(newField);
			if(affectedRow < 1)
				throw new RuntimeException("addField error");
		}

		return 1;
	}

	/**
	 * 数据表导出成为json格式
	 */
	@Override
	public String exportDatabase(HttpServletRequest request, int dbID) throws Exception {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		String fileName = null;

		if (this.connDatabaseMapper.checkDatabasePermission(dbID, userID) != null) {
			HttpSession session =  request.getSession(true);
			String path = session.getServletContext().getRealPath("/dump") + "/";
			String dataBaseInfo = JSON.toJSONString(this.databaseMapper.getDatabaseInfo(dbID));
			fileName = "eolinker_export_mysql_"
					+ session.getAttribute("userName") + "_" + System.currentTimeMillis() + ".export";
			File file = new File(path+fileName);
			file.createNewFile();

			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			writer.write(dataBaseInfo);
			writer.flush();
			writer.close();
			return request.getContextPath()+"/dump/"+fileName;
		}

		return fileName;
	}

	/**
	 * 导入数据字典界面数据库
	 */
	@Override
	public Integer importDatabseByJson(com.eolinker.vo.DatabaseVO databaseVO) throws RuntimeException {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		DatabaseInfoVO databaseInfo = databaseVO.getDatabaseInfo();
		DatabaseTableVO[] tableList = databaseVO.getTableList();

		Database database = new Database();
		database.setDbName(databaseInfo.getDatabaseName());
		database.setDbVersion(databaseInfo.getDatabaseVersion());

		// 创建新数据库
		int affectedRow = this.databaseMapper.addDatabase(database);
		if (affectedRow < 1)
			throw new RuntimeException("insert database error");

		// 生成数据库与用户的联系
		affectedRow = this.connDatabaseMapper.addDatabaseConnection(database.getDbID(), userID);
		if (affectedRow < 1)
			throw new RuntimeException("insert conn error");

		for (DatabaseTableVO table : tableList) {
			DatabaseTable databaseTable = new DatabaseTable();
			databaseTable.setDbID(database.getDbID());
			databaseTable.setTableName(table.getTableName());
			databaseTable.setTableDescription(table.getTableDesc());

			// 生成数据库表
			affectedRow = this.databaseTableMapper.importDBTable(databaseTable);
			if (affectedRow < 1)
				throw new RuntimeException("insert table error");

			DatabaseTableFieldVO[] fieldList = table.getFieldList();
			for (DatabaseTableFieldVO vo : fieldList) {
				DatabaseTableField databaseTableField = new DatabaseTableField();

				databaseTableField.setFieldName(vo.getFieldName());
				databaseTableField.setFieldType(String.valueOf(vo.getFieldType()));
				databaseTableField.setFieldLength(String.valueOf(vo.getFieldLength()));
				databaseTableField.setIsNotNull(String.valueOf(vo.getIsNotNull()));
				databaseTableField.setIsPrimaryKey(String.valueOf(vo.getIsPrimaryKey()));
				databaseTableField.setFieldDescription(vo.getFieldDesc());
				databaseTableField.setTableID(String.valueOf(table.getTableID()));
				databaseTableField.setDefaultValue(vo.getDefaultValue());

				// 生成字段表
				affectedRow = this.databaseTableFieldMapper.importTableField(databaseTableField);
				if (affectedRow < 1)
					throw new RuntimeException("insert field error");
			}
		}
		return 1;
	}

}
