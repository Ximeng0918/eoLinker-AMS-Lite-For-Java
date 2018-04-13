package com.eolinker.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.eolinker.pojo.*;

public interface UserService
{
	//新增用户
	public Integer addUser(User user);
	
	//通过用户名查找用户
	public User getUserByUserName(String userName);
	
	//登录
	public Map <String, Object> login(HttpServletRequest request, String userName, String userPassword);
	

	//修改密码
	public boolean changePassword(String userName, String oldPassword, String newPassword);
	
	//修改昵称
	public boolean changeNickName(Integer userID, String nickName);
	
	//获取用户信息
	public Map<String, Object> getUserInfo(HttpSession session);
}
