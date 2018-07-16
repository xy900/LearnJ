package com.cms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class Test {
	
	@RequestMapping("/1.do")
	public String main(HttpServletRequest request, HttpServletResponse response, Model model) {
		System.out.println(request.getRequestURI());
		System.out.println(request.getContextPath());
		model.addAttribute("title", "1.jtp");
		return "1";
	}
}
