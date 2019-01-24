package com.test.concurrent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>Callable,FutureTask,Future,RunnableFuture: 执行完毕后可以得到结果,并且可以捕获异常(相对于Thread而言).
 * <p>RunnableFuture 继承 Runnable, Future.
 * <p>FutureTask 继承 RunnableFuture, FutureTask对象可以判断任务有没有执行完毕,并获取返回值的信息等.
 * @author xieyu
 *
 */
public class FutureCallable {
	private static SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS E");
	
	public static void main(String[] args) {
		
		FutureTask<String> task = new FutureTask<String>(
				new Callable<String>() {
					private String val = "1";
					@Override
					public String call() throws Exception {
						Thread.sleep(5000);
						return Thread.currentThread().getName() + ":" + val;
					}
				});
		
		//Executors.newFixedThreadPool(5);
		//task.cancel(true);
		new Thread(task).start();//开始执行
		
		/*while (!task.isDone()) {//isDone() 任务是否完成
			System.out.println("任务正在执行..." + sdFormat.format(new Date()));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		System.out.println("任务执行完毕!!!" + sdFormat.format(new Date()));
		try {
			System.out.println(task.get());//得到返回值,得不到则一直阻塞
			//System.out.println(task.get(2000, TimeUnit.MILLISECONDS));//指定时间还没有获取结果,则直接抛出异常
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} /*catch (TimeoutException e) {
			e.printStackTrace();
		}*/
		
		System.out.println("over");
	}
}
