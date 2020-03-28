package com.hongyu.task.impl;

import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.stereotype.Component;

@Component("timerManagement")
public class TimerManagement {
	private PriorityBlockingQueue<TimerTask> taskQueue;
	
	public TimerManagement() {
		this.taskQueue = new PriorityBlockingQueue<TimerTask>();
	}
	
	public boolean addTimerTask(TimerTask task) {
		return taskQueue.add(task);
	}
	
	public boolean cancelTimerTask(TimerTask task) {
		return this.taskQueue.remove(task);
	}
	
	//if taskQueue.size()>0, return expiretime of peek, else return -1
	public long getHeapTopExpireTime() {
		if (taskQueue.size() > 0) {
			return this.taskQueue.peek().getExpireTime();
		} else {
			return -1;
		}
	}
	
	public TimerTask removeTaskIfExpired() {
		long expireTime = getHeapTopExpireTime();
		if (expireTime != -1 && expireTime < System.currentTimeMillis()) {
			return this.taskQueue.poll();
		} else {
			return null;
		}
	}
	
	
	
}
