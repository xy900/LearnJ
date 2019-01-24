package com.test;

/**
 * 多态
 * @author xieyu
 */
public class Polym {

	public static void main(String[] args) {
		System.out.println("A>>>");
		A a = new A();
		a.say();
		
		System.out.println("\nB>>>");
		B b = new B();
		b.say();
	}
}


class A {
	public void say() {
		System.out.println("say A say()");
		sayWhat();
	}
	
	public void sayWhat() {
		System.out.println("say A sayWhat()");
	}
}

class B extends A {
	public void sayWhat() {
		System.out.println("say B sayWhat()");
	}
}