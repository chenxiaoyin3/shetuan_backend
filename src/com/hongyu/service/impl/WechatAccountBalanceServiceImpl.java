package com.hongyu.service.impl;

import javax.annotation.Resource;

import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.WechatAccountBalanceDao;
import com.hongyu.entity.WechatAccountBalance;
import com.hongyu.service.WechatAccountBalanceService;

import java.util.LinkedList;
import java.util.List;

@Service("wechatAccountBalanceServiceImpl")
public class WechatAccountBalanceServiceImpl extends BaseServiceImpl<WechatAccountBalance, Long> implements WechatAccountBalanceService {
    @Resource(name = "wechatAccountBalanceDaoImpl")
    WechatAccountBalanceDao dao;

    @Resource(name = "wechatAccountBalanceDaoImpl")
    public void setBaseDao(WechatAccountBalanceDao dao) {
        super.setBaseDao(dao);
    }

    @Override
    public Page<WechatAccountBalance> getWechatAccountBalanceList(Pageable pageable, Long wechatId) throws Exception{
        // 倒序
        List<Order> orders = new LinkedList<>();
        orders.add(Order.desc("id"));
        pageable.setOrders(orders);
        // 筛选条件
        List<Filter> filters = new LinkedList<>();
        filters.add(Filter.eq("wechatAccountId", wechatId));
        pageable.setFilters(filters);

        Page<WechatAccountBalance> page = this.findPage(pageable);
        return page;
    }
}