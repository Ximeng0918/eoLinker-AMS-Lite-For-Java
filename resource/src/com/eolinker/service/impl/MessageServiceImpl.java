package com.eolinker.service.impl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.eolinker.mapper.MessageMapper;
import com.eolinker.service.MessageService;
import com.eolinker.util.Arithmetic;

@Service
@Transactional
public class MessageServiceImpl implements MessageService
{

	@Autowired
	private MessageMapper messageMapper;

	@Override
	public Map<String, Object> getMessageList(int page)
	{
		Map<String, Object> map = new HashMap<String, Object>();

		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);

		List<Map<String, Object>> messageList = messageMapper.getMessageList((page - 1) * 15, userID);
		if (messageList != null && !messageList.isEmpty())
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (Map<String, Object> message : messageList)
			{
				String msgSendTime = dateFormat.format(message.get("msgSendTime"));
				message.put("msgSendTime", msgSendTime);
			}
			int msgCount = messageMapper.getMessageListCount(userID);
			map.put("messageList", messageList);
			map.put("msgCount", msgCount);
			map.put("pageCount", Math.ceil(Arithmetic.div(msgCount, 15, 3)));
			map.put("pageNow", page);
		}
		else
			return null;

		return map;
	}

	@Override
	public int readMessage(int msgID)
	{
		return this.messageMapper.readMessage(msgID);
	}

	@Override
	public int delMessage(int msgID)
	{
		return this.messageMapper.delMessage(msgID);
	}

	@Override
	public int cleanMessage()
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		return this.messageMapper.cleanMessage(userID);
	}

	@Override
	public int getUnreadMessageNum()
	{
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		int userID = (int) requestAttributes.getAttribute("userID", RequestAttributes.SCOPE_SESSION);
		return this.messageMapper.getUnreadMessageNum(userID);
	}

	@Override
	public int getUnreadMessageNum(Integer userID)
	{
		// TODO Auto-generated method stub
		return messageMapper.getUnreadMessageNum(userID);
	}

}
