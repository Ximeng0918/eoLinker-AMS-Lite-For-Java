package com.eolinker.service;

import java.util.Map;

public interface MessageService {

	/**
	 * 获取消息列表
	 * @param page
	 * @return
	 */
	public Map<String,Object> getMessageList(int page);
	
	/**
	 * 查阅消息
	 * @param msgID
	 * @return
	 */
	public int readMessage(int msgID);
	
	/**
	 * 删除消息
	 * @param msgID
	 * @return
	 */
	public int delMessage(int msgID);
	
	
	/**
	 * 清空消息
	 * @return
	 */
	public int cleanMessage();
	
	
	/**
	 * 获取消息列表
	 * @return
	 */
	public int getUnreadMessageNum();

	public int getUnreadMessageNum(Integer userID);
	

	
}
