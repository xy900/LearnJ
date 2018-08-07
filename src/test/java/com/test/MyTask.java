package com.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MyTask extends TimerTask{
	
	@Override
	public void run() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");
		System.out.println(format.format(date));
	}
	
	public static void main(String[] args) {
		System.out.println(new Date().toLocaleString());
		Timer timer = new Timer();
		timer.schedule(new MyTask(), 1000, 10000);
	}
}
