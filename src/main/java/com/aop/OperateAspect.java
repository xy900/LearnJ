package com.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@org.aspectj.lang.annotation.Aspect
public class OperateAspect {
	private static final Logger log = LoggerFactory.getLogger(OperateAspect.class);
	
	@Pointcut(value = "execution(* com.cms..*.*(..))")
	public void doSomething() {}
	
	@Around(value = "doSomething()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		StringBuilder sb = new StringBuilder();
		Object[] args = pjp.getArgs();//被通知方法参数列表
		Object target = pjp.getTarget();//目标对象
		Signature signature = pjp.getSignature();//前连接点签名
		
		sb.append("Execute:").append(target.getClass()+".").append(signature.getName()).append("(");//获取当前类名与方法名
		if (args != null) {
			for (Object o : args) {
				sb.append(o).append(",");
			}
		}
		log.info((sb.lastIndexOf(",")==sb.length()-1?sb.substring(0, sb.length()-1):sb.toString())+")");
		
		Object result = pjp.proceed();//执行方法
		log.info("End: result[{}], from [{}]", result, target.getClass()+"."+signature.getName());
		return result;
	}

}
