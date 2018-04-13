package com.eolinker.test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eolinker.mapper.*;
import com.eolinker.pojo.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class MybatisTest {

	@Autowired
	private UserMapper userMapper;

	@Test
	public void testAdd(User user) {
		userMapper.addUser(user);
	}

	@Test
	public void testList() {
		
	}
	
	public static void main(String args[])
	{
		Date date = new Date();
		Timestamp updateTime = new Timestamp(date.getTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = df.format(updateTime);
		System.out.println(time);
	}

}
