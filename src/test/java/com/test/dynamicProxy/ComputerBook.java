package com.test.dynamicProxy;

class ComputerBook implements IBook {
	
	@Override
	public void readBook(String book) {
		System.out.println("read book:" + book);
	}
}