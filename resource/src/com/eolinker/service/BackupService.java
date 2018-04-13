package com.eolinker.service;

import java.util.Map;

public interface BackupService
{

	public int backupProject(Integer userID, String userCall, String userPassword, Integer projectID, String verifyCode, Map<String, Object> data);

}
