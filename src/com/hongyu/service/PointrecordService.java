package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.Pointrecord;
import com.hongyu.entity.WechatAccount;

public interface PointrecordService extends BaseService<Pointrecord,Long> {
	
	public void changeUserPoint(Long wechatId,Integer changeValue,String reason) throws Exception;
	

}
