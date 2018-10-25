package com.test.dynamicProxy;

public class Dog implements Pet{

	@Override
	public String say(String what) {
		System.out.println("wang wang wang!");
		return what;
	}
}
