package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.WechatAccountBalance;

public interface WechatAccountBalanceService extends BaseService<WechatAccountBalance, Long> {
    /**
     * 根据wechatid获取WechatAccountBalance的分页
     */
    Page<WechatAccountBalance> getWechatAccountBalanceList(Pageable pageable, Long wechatId) throws Exception;
}