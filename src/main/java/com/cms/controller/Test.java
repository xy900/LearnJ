package com.cms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cms.component.TestPoint;
import com.cms.entity.TestEntity;
import com.cms.service.TestService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/test")
public class Test {
	
	@Autowired
	private TestPoint point;
	
	@Autowired
	private TestPoint testPoint;
	
	@Autowired
	private TestService testService;
	
	@RequestMapping("/1.do")
	public String test1(HttpServletRequest request, HttpServletResponse response, Model model) {
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
	
	@ResponseBody
	@RequestMapping(value="/driud.do", produces = "application/json;charset=utf-8")
	public String druid(HttpServletRequest request, HttpServletResponse response, Model model) {
		System.out.println(request.getRequestURI());
		TestEntity entity = testService.get("test", 1);
		System.out.println(entity);
		return "";
	}
}
