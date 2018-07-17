package com.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ProcessTimeFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println(getClass());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		long beginTime = System.currentTimeMillis();
		System.out.println(getClass() + " request url:" + req.getRequestURI() + ", begin at:" + beginTime);
		chain.doFilter(request, response);
		long endTime = System.currentTimeMillis();
		System.out.println(getClass() + " request url:" + req.getRequestURI() + ", end at:" 
								+ endTime + ", spend " + (endTime - beginTime) + " ms");
		//response.setCharacterEncoding("UTF-8");
		//response.setContentType("application/json;charset=UTF-8");
	}

	@Override
	public void destroy() {
	}

}
