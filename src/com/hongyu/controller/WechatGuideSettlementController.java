package com.hongyu.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.GuideSettlement;
import com.hongyu.entity.GuideSettlementDetail;
import com.hongyu.service.GuideSettlementDetailService;
import com.hongyu.service.GuideSettlementService;

@Controller
@RequestMapping("/wechat/guideSettlement/")
public class WechatGuideSettlementController {

	@Resource(name="guideSettlementServiceImpl")
	GuideSettlementService guideSettlementService;
	
	@Resource(name="guideSettlementDetailServiceImpl")
	GuideSettlementDetailService guideSettlementDetailService;
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Integer id,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,Pageable pageable){
		Json json=new Json();
		try {
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("guideId", id));
			if(startDate!=null){
				filters.add(Filter.ge("startDate", startDate));
			}
			if(endDate!=null){
				filters.add(Filter.le("endDate", endDate));
			}
			List<Order> orders=new LinkedList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			pageable.setFilters(filters);
			Page<GuideSettlement> page=guideSettlementService.findPage(pageable);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id,Pageable pageable){
		Json json=new Json();
		try {
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("settlementId", id));
			pageable.setFilters(filters);
			List<Order> orders=new LinkedList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<GuideSettlementDetail> page=guideSettlementDetailService.findPage(pageable);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page);
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
