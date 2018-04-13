package com.eolinker.service;

import java.util.List;
import java.util.Map;

import com.eolinker.pojo.EnvHeader;
import com.eolinker.pojo.EnvParam;
import com.eolinker.pojo.EnvParamAdditional;

public interface EnvService {

	/**
	 * 获取环境列表
	 * @param projectID
	 * @return
	 */
	public Map<String,Object> getEnvList(int projectID);
	
	/**
	 * 添加环境
	 * @param projectID
	 * @param envName
	 * @param frontUri
	 * @param applyProtocol
	 * @param headers
	 * @param params
	 * @param additionalParams
	 * @return
	 */
	public int addEnv(int projectID, String envName, String frontUri, int applyProtocol, List<EnvHeader> headers, List<EnvParam> params, List<EnvParamAdditional> additionalParams) throws RuntimeException;

	/**
	 * 删除环境
	 * @param projectID
	 * @param envID
	 * @return
	 */
	public int deleteEnv(int projectID, int envID) throws RuntimeException;
	
	
	/**
	 * 修改环境
	 * @param projectID
	 * @param envName
	 * @param frontUri
	 * @param applyProtocol
	 * @param headers
	 * @param params
	 * @param additionalParams
	 * @return
	 * @throws RuntimeException
	 */
	public int editEnv(int envID, String envName, String frontUri, int applyProtocol, List<EnvHeader> headers, List<EnvParam> params, List<EnvParamAdditional> additionalParams) throws RuntimeException;



}
