package com.test.dynamicProxy;

import com.myinterface.MethodInterface;

public interface IBook {
	
	@MethodInterface(value = "IBook")
	void readBook(String book);
}
