package com.eolinker.service;

import java.util.List;

import com.eolinker.dto.PartnerListDTO;

public interface DatabasePartnerService {
	
	
	/**
	 * 获取数据字典用户类型
	 * @param dbID
	 * @return
	 */
	public int getUserType(int dbID);
	
	/**
	 * 邀请协作人员
	 * @param connDatabase
	 * @return
	 */
	public int invitePartner(int dbID, int inviteUserID);
	
	
	/**
	 * 移除协作人员
	 * @param dbID
	 * @param connID
	 * @return
	 */
	public int removePartner(int dbID, int connID);
	
	
	/**
	 * 查询是否已经加入过项目
	 * @param dbID
	 * @param userName
	 * @return
	 */
	public int checkIsInvited(int dbID, String userName);
	
	
	/**
	 * 获取协作人员列表
	 * @param dbID
	 * @return
	 */
	public List<PartnerListDTO> getPartnerList(int dbID);
	
	
	/**
	 * 退出协作项目
	 * @param dbID
	 * @return
	 */
	public int quitPartner(int dbID);
	
	
	/**
	 * 修改协作成员的昵称
	 * @param dbID
	 * @param connID
	 * @param nickName
	 * @return
	 */
	public int editPartnerNickName(int dbID, int connID, String nickName);
	
	
	/**
	 * 修改协作成员的类型
	 * @param dbID
	 * @param connID
	 * @param userType
	 * @return
	 */
	public int editPartnerType(int dbID, int connID, int userType);
	
}
