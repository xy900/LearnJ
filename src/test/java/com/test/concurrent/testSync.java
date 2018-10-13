package com.test.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/** 测试StringBuffer和StringBuilder是否线程安全;
 * 	使用synchronized关键字和ReentrantLock可重入锁实现线程安全的类;
 * 
 * StringBuffer 线程安全  
 * StringBuilder 线程非安全
 * @author 909974
 * 
 */
public class testSync {
	public static void main(String[] argaa) {
		MyString my = new MyString();
		StringBuilder sbBuilder = new StringBuilder();
		StringBuffer stringBuffer = new StringBuffer();
		//创建线程池
		ExecutorService pool = Executors.newCachedThreadPool();
		//创建多个线程去处理MyString, StringBuilder, StringBuffer三个对象
		for (int i = 0; i < 200; i++) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					for (int j = 0; j < 1000; j++) {
						my.append(1);
						sbBuilder.append("1");
						stringBuffer.append("1");
						System.out.println("MyString:" + my.getNum() + ", StringBuilder:" + sbBuilder.length() + 
								", StringBuffer:" + stringBuffer.length());
					}
				}
			});
		}
		pool.shutdown();
	}
}


/** 创建测试类
 *  使用不同的方法保证线程安全
 * @author xieyu
 * 
 */
class MyString {
	final ReentrantLock lock = new ReentrantLock(); //使用可重入锁保证线程安全
	
	private volatile Integer num = 0; //volatile关键字   不能保证线程安全

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	
	public void append(Integer num) {       //1.synchronized(作用在方法上或者代码块中均可)关键字保证线程安全，每个只有一个线程可以进入此方法
		//this.num = this.num + num;
		
		/*synchronized (MyString.class) {   //2.synchronized作用在类上，统一保存线程安全
			this.num = this.num + num;
		}*/
		
		lock.lock();                         //3.使用可重入锁保证线程安全
		this.num = this.num + num; 
		lock.unlock();
	}
}