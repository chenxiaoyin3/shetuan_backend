package com.hongyu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.OrderTransaction;
import com.hongyu.service.OrderTransactionService;

@Controller
@RequestMapping("/admin/business/ordertransaction")
public class OrderTransactionController {
	
	@Resource(name = "orderTransactionServiceImpl")
	OrderTransactionService orderTransactionServiceImpl;
	
	@RequestMapping({"/page/view"})
	@ResponseBody
	public Json orderTransactionPage(OrderTransaction query, Pageable pageable, HttpSession session) {
		Json j = new Json();
		
		try {
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<OrderTransaction> page = orderTransactionServiceImpl.findPage(pageable, query);
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(page);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		return j;
	}
	
	@RequestMapping({"/detail/view"})
	@ResponseBody
	public Json orderTransactionDetail(Long id, HttpSession session) {
		Json j = new Json();
		
		try {
			if (id == null) {
				j.setSuccess(false);
				j.setMsg("缺少参数");
				j.setObj(null);
				return j;
			}
			OrderTransaction transaction = orderTransactionServiceImpl.find(id);
			if (transaction == null) {
				j.setSuccess(false);
				j.setMsg("指定的交易记录不存在");
				j.setObj(null);
				return j;
			}
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(transaction);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		return j;
	}
	
}
