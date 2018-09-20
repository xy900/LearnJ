package com.test.dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理类
 */
public class DynamicProxy implements InvocationHandler{
	
	private Object subject;//接受任意类型的对象
	
	public DynamicProxy(Object subject) {
		this.subject = subject;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("begin!");
		System.out.println("method: " + method.getName());
		System.out.print("args: ");
		if (args != null) {
			for (Object o : args) {
				System.out.print(o + "; ");
			}	
		}
		System.out.println();
		method.invoke(subject, args);//代理对象调用真实对象的方法
		
		System.out.println("end!");
		return null;
	}
	
	public static void main(String[] args) {
		IBook book = new ComputerBook();
		DynamicProxy dynamic = new DynamicProxy(book);
		IBook proxy = (IBook) Proxy.newProxyInstance(IBook.class.getClassLoader(), new Class<?>[]{IBook.class}, dynamic);
		proxy.readBook("Think In JAVA");
		
		System.out.println("-----------------");
		
		Pet pet = new Dog();//委托类
		DynamicProxy dyProxyPet = new DynamicProxy(pet);//动态代理类
		Pet petProxy = (Pet) Proxy.newProxyInstance(Pet.class.getClassLoader(), new Class<?>[]{Pet.class}, dyProxyPet);
		petProxy.say();
	}
}
