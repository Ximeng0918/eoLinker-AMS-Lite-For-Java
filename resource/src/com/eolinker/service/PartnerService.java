package com.eolinker.service;

import java.util.List;
import java.util.Map;


public interface PartnerService
{

	public int checkIsInvited(Integer projectID, String userName);

	public int invitePartner(Integer projectID, Integer inviteUserID, Integer userID, String userName);

	public boolean removePartner(Integer projectID, Integer connID, Integer userID);

	public List<Map<String, Object>> getPartnerList(Integer projectID, Integer userID);

	public boolean quitPartner(Integer projectID, Integer userID, String userName);

	public boolean editPartnerNickName(Integer projectID, Integer connID, Integer userID, String nickName);

	public boolean editPartnerType(Integer projectID, Integer connID, Integer userID, Integer userType);

}
