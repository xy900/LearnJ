package com.cms.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cms.entity.User;
import com.cms.service.UserService;

@Controller
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserService userService;
	
	@RequestMapping("/user/find/{userName}")
	@ResponseBody
	public User findUser(HttpServletRequest request, @PathVariable String userName) {
		if (StringUtils.isBlank(userName)) return null;
		log.info("userName[{}]", userName);
		List<User> list = userService.findByUserName(userName);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
	@RequestMapping(value = "/user/admin/index.do")
	public String index(HttpServletRequest request) {
		Subject subject = SecurityUtils.getSubject();
		log.info("current subject[{}], isAuthenticated[{}]", (String)subject.getPrincipal(), subject.isAuthenticated());
		return "index";
	}

	/** 登陆页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/user/admin/login.do", method = RequestMethod.GET)
	public String loginInput(HttpServletRequest request) {
		return "login";
	}
	
	/** 登陆提交
	 *  是有登陆失败会进入该方法
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/user/admin/login.do", method = RequestMethod.POST)
	public String login(HttpServletRequest request) {
		log.info("login false");
		return "login";
	}
}
