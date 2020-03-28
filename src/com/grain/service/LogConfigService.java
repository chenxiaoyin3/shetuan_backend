package com.grain.service;

import java.util.List;

import com.hongyu.LogConfig;

/**
 * Service - 日志配置
 * 
 */
public interface LogConfigService {

	/**
	 * 获取所有日志配置
	 * 
	 * @return 所有日志配置
	 */
	List<LogConfig> getAll();

}