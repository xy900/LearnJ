package com.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cms.component.TestPoint;
import com.cms.utils.ApplicationContextHelper;
import com.cms.utils.ServletContextHelper;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet(urlPatterns = "/TestServlet", loadOnStartup = 1)
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		System.out.println("==================servletContext===========");
		System.out.println(getServletContext());
		System.out.println(getServletConfig().getServletContext());
		System.out.println(request.getServletContext());
		System.out.println(request.getSession().getServletContext());
		System.out.println(ServletContextHelper.servletContext);
		System.out.println("==================applicationContext===========");
		System.out.println(ApplicationContextHelper.applicationContext);//通过实现ApplicationContextAware接口来获取applicationContext
		System.out.println(WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext()));//通过WebApplicationContextUtils与servletContext获取
		TestPoint point = (TestPoint) ApplicationContextHelper.applicationContext.getBean("point");
		System.out.println(point);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
