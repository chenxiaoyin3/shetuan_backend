package com.hongyu.task.impl;

public interface TimerTask extends Comparable<TimerTask> {
	void execute();
	long getExpireTime();
}
