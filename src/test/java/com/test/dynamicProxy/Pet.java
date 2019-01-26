package com.test.dynamicProxy;

import com.myinterface.MethodInterface;

public interface Pet {
	
	@MethodInterface("pet")
	String say(String what);
}
