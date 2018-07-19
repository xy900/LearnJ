package com.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@org.aspectj.lang.annotation.Aspect
public class Aspect {
	
	@Pointcut(value = "execution(* com.cms.component..*.*(..))")
	public void doSomething() {}
	
	@Before(value = "doSomething()")
	public void before() {
		System.out.println("=======before=====@Aspect===");
	}
	
	@After(value = "doSomething()")
	public void after() {
		System.out.println("=======after======@Aspect==");
	}

}
