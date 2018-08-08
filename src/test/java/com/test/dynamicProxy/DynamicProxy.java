package com.test.dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理类
 */
public class DynamicProxy implements InvocationHandler{
	
	private IBook book;
	
	public DynamicProxy(IBook book) {
		this.book = book;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("begin!");
		System.out.println("method: " + method.getName());
		System.out.print("args: ");
		for (Object o : args) {
			System.out.print(o + "; ");
		}
		System.out.println();
		method.invoke(book, args);//代理对象调用真实对象的方法
		
		System.out.println("end!");
		return null;
	}
	
	public static void main(String[] args) {
		IBook book = new ComputerBook();
		DynamicProxy dynamic = new DynamicProxy(book);
		IBook proxy = (IBook) Proxy.newProxyInstance(IBook.class.getClassLoader(), new Class<?>[]{IBook.class}, dynamic);
		proxy.readBook("Think In JAVA");
	}
}
