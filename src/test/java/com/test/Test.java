package com.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import com.entity.TestEntityClass;

public class Test {

	@org.junit.Test
	public void test() {
		String FS = System.getProperty("file.separator");//  \
		System.out.println(FS);
		String work_dir = System.getProperty("user.dir");
		System.out.println(work_dir);
		
		System.out.println(TestEntityClass.class);
		System.out.println(TestEntityClass.class.getName());
	}
	
	@org.junit.Test
	public void test1() {
		System.out.println("\n===============test1==================反射");
		//Class<?> clazz = new TestEntityClass("2", "hehe", 8054, new Date()).getClass();
		Class<?> clazz = TestEntityClass.class;
		System.out.println(clazz.getName());
		Field[] field = clazz.getDeclaredFields();
		System.out.println("--------------Filed--------------Modifiers(修饰符)---------");
		for (Field ele : field) {
			System.out.println(ele.getName() + ": " + ele.getModifiers());
		}
		System.out.println("--------------Constructor-----------------------");
		for (Constructor<?> ele : clazz.getDeclaredConstructors()) {
			System.out.println(ele);
		}
		System.out.println("--------------Method----------------Modifiers(修饰符)------");
		Method[] method = clazz.getDeclaredMethods();
		for (Method ele : method) {
			System.out.println(ele.getName() + ": " + ele.getModifiers());
		}
		try {
			Constructor<?> constructor = clazz.getDeclaredConstructor(String.class, String.class, Integer.class, Date.class);
			try {
				System.out.println("--------------Constructor----------------------");
				constructor.setAccessible(true);//禁用Java权限修饰符的作用
				Object object = constructor.newInstance("222", "hehehe", 8054, new Date());//constructor.newInstance(initargs)
				System.out.println(object);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException | SecurityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			System.out.println("-------------newInstance----------");
			Object object = clazz.newInstance();//默认的构造函数
			System.out.println(object);
			try {
				Field field2 = clazz.getDeclaredField("name");
				field2.setAccessible(true);//禁用Java权限修饰符的作用，无视方法权限限制进行访问
				field2.set(object, "after set");//设置object对象的属性
				System.out.println("------------field.set------------");
				System.out.println(field2.get(object));//获得object对象的属性
				System.out.println(object);
			} catch (NoSuchFieldException | SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Method method2 = clazz.getDeclaredMethod("getName");//所有方法， 但不包括继承的方法
				method2.setAccessible(true);//禁用Java权限限定符的作用，使私有函数可访问
				try {
					System.out.println("------------invoke------------");
					System.out.println(method2.invoke(object));//调用object对象的方法
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
