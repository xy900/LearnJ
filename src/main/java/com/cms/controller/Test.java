package com.cms.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cms.component.TestPoint;
import com.cms.entity.TestEntity;
import com.cms.service.TestService;
import com.cms.utils.ApplicationContextHelper;
import com.cms.utils.JedisUtils;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

@Controller
@RequestMapping("/test")
public class Test {
	
	private static Logger logger = LoggerFactory.getLogger(Test.class);
	
	@Autowired
	private TestPoint point;
	
	@Autowired
	private TestPoint testPoint;
	
	@Autowired
	private TestService testService;
	
	@RequestMapping("/1.do")
	public String test1(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.debug("1.do 哈哈");
		logger.info("11.do 蛤蛤");
		model.addAttribute("title", "1.jtp");
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value="/2.do", produces = "application/json;charset=utf-8")
	public String test2(HttpServletRequest request, HttpServletResponse response, Model model) {
		System.out.println(request.getRequestURI());
		//System.out.println(request.getContextPath());
		model.addAttribute("title", "1.jtp");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", "李三");
		jsonObject.put("age", 25);
		System.out.println(point.toString());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	@ResponseBody
	@RequestMapping(value="/2.jspx", produces = "application/json;charset=utf-8")
	public String test2jspx(HttpServletRequest request, HttpServletResponse response, Model model) {
		System.out.println(request.getRequestURI());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", "李三");
		jsonObject.put("age", 25);
		System.out.println(testPoint);
		return jsonObject.toString();
	}
	
	/**
	 * 测试回滚
	 */
	@ResponseBody
	@RequestMapping(value="/druid.do", produces = "application/json;charset=utf-8")
	public String druid(HttpServletRequest request, HttpServletResponse response, Model model) {
		System.out.println(request.getRequestURI());
		TestEntity entity = testService.get("test", 1);
		System.out.println(entity);
		TestEntity test = new TestEntity();
		test.setId(1);
		test.setName("test");
		TestEntity entity2 = testService.get("test", test);
		System.out.println(entity2);
		
		testService.update("update", 1);//测试事务回滚是否生效
		return "";
	}
	
	@ResponseBody
	@RequestMapping(value = "resource.do", produces = "application/json;charset=utf-8")
	public String resource(HttpServletRequest request, HttpServletResponse response, Model model) {
		Resource resource = ApplicationContextHelper.applicationContext.getResource("classpath:config/properties/jdbc.properties");
		try {
			InputStream in = resource.getInputStream();
			StringBuilder sb = new StringBuilder();
			int n = 0;
			while ( (n=in.read())!= -1 ) {
				sb.append((char)n);
			}
			System.out.println(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Resource resource1 = ApplicationContextHelper.applicationContext.getResource("/WEB-INF/classes/config/spring.xml");
		System.out.println("\n是否存在文件：" + resource1.exists());
		try {
			InputStream in = resource1.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
			BufferedReader reader = new BufferedReader(inputStreamReader);
			StringBuilder sb = new StringBuilder();
			char[] cbuf = new char[1000];
			/*while ( reader.read(cbuf, 0, 1000) != -1 ) {
				sb.append(cbuf);
			}*/
			while ( reader.read(cbuf) != -1 ) {
				sb.append(cbuf);
			}
			//sb.append(cbuf);
			/*String string = "";
			while ( (string=reader.readLine()) != null ) {
				sb.append(string);
			}*/
			System.out.println(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return "success";
	}
	
	/**
	 * 测试redis
	 */
	@ResponseBody
	@RequestMapping(value="/redis.do", produces = "application/json;charset=utf-8")
	public String redis(HttpServletRequest request, String id, HttpServletResponse response, Model model) {
		JSONObject jsonObject = new JSONObject();
		String value = null;
		//获取redis缓存
		value = JedisUtils.getString(TestEntity.class.getSimpleName() + "_" + id);
		if (StringUtils.isBlank(value)) {//缓存不存在,查库
			logger.info("Get redis failed");
			TestEntity entity = testService.get("test", Integer.valueOf(id));
			jsonObject = JSONObject.fromObject(entity);
			value = jsonObject.toString();
			JedisUtils.setString(TestEntity.class.getSimpleName() + "_" + id, value);
			logger.info("Set redis [{}, {}]", TestEntity.class.getSimpleName() + "_" + id, value);
		} else {
			logger.info("Get redis success");
		}
	
		return value;
	}
}
