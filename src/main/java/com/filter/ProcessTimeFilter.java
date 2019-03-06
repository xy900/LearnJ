package com.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessTimeFilter implements Filter{
	
	private static Logger Logger = LoggerFactory.getLogger(ProcessTimeFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println(getClass());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		long beginTime = System.currentTimeMillis();
//		System.out.println(getClass() + " request url:" + req.getRequestURI() + ", begin at:" + beginTime);
		Logger.info("\n    |Begin:   request url:{}, begin at:{}", req.getRequestURL(), beginTime);
		chain.doFilter(request, response);
		long endTime = System.currentTimeMillis();
//		System.out.println(getClass() + " request url:" + req.getRequestURI() + ", end at:" 
//								+ endTime + ", spend " + (endTime - beginTime) + " ms");
		Logger.info("request uri:{}, end at:{}, spend {}ms", req.getRequestURI(), beginTime, endTime - beginTime);
		//response.setCharacterEncoding("UTF-8");
		//response.setContentType("application/json;charset=UTF-8");
	}

	@Override
	public void destroy() {
	}

}
