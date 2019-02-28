package com.test.concurrent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 	模拟死锁, 并通过反射获取当前占有Lock锁的线程
 * @author XieYu
 *
 */
public class DeadLock {
	public static void main(String[] args) {
		System.out.println(">>>begin");
		Method method = null;
		try {
			//反射获取LinkedBlockingQueueDemo类的scanThread方法
			method = LinkedBlockingQueueDemo.class.getDeclaredMethod("scanThread");
			method.setAccessible(true);
			
			//声明两个可重入锁
			ReentrantLock lock1 = new ReentrantLock();
			ReentrantLock lock2 = new ReentrantLock();
			getLockOfWhichThread(lock1, "lock1");
			getLockOfWhichThread(lock2, "lock2");
			
			//Thread1线程获取lock1的锁, 然后再去获取lock2的锁
			new Thread(()->{
				System.out.println("currnt Thread1: " + Thread.currentThread());
				lock1.lock();
				try {
					Thread.sleep(1000);//确保Thread2获取到了lock2的锁
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock2.lock();
			}).start();
			
			//Thread2线程获取lock2的锁, 然后再去获取lock1的锁
			new Thread(()->{
				System.out.println("currnt Thread2: " + Thread.currentThread());
				lock2.lock();
				lock1.lock();
			}).start();
			
			
			//循环检测线程状态与是否有死锁
			while (true) {
				System.out.println("\n");
				method.invoke(null);
				getLockOfWhichThread(lock1, "lock1");
				getLockOfWhichThread(lock2, "lock2");
				Thread.sleep(2000);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	/** 获取当前占有Lock锁的线程
	 * @param lock
	 * @param name
	 */
	public static void getLockOfWhichThread(ReentrantLock lock, String name) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		//反射获取Lock中的sync对象
		Field syncFiled = ReentrantLock.class.getDeclaredField("sync");
		syncFiled.setAccessible(true);
		//向上转型为AbstractOwnableSynchronizer对象
		AbstractOwnableSynchronizer aws = (AbstractOwnableSynchronizer) syncFiled.get(lock);
		
		//获取AbstractOwnableSynchronizer的exclusiveOwnerThread对象(该对象为当前获取锁的线程)
		Field eotfiled = AbstractOwnableSynchronizer.class.getDeclaredField("exclusiveOwnerThread");
		eotfiled.setAccessible(true);
		Thread thread = (Thread) eotfiled.get(aws);
		System.out.println(name + " is : " + thread +
				(thread==null?"":", ID : " + thread.getId()));//调用thread的任何方法均会报空指针异常  (原因: 有时候Lock并没有被其他线程获取锁, 即该thread可能为空)
	}
	
}
