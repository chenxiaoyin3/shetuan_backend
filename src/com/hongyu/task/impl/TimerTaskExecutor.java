package com.hongyu.task.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

@Component("timerTaskExecutor")
public class TimerTaskExecutor {
	private ExecutorService threadPool;

	public TimerTaskExecutor() {
		this.threadPool = Executors.newFixedThreadPool(3);
	}
	
	public class TimerTaskRunnable implements Runnable {
		private TimerTask task;
		
		public TimerTaskRunnable(TimerTask task) {
			this.task = task;
		}
		
		@Override
		public void run() {
			task.execute();
		}
		
	}
	
	public void addTimerTask(TimerTask task) {
		threadPool.execute(new TimerTaskRunnable(task));
	}
	
	
}
