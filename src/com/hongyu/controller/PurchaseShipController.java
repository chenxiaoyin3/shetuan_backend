package com.hongyu.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.entity.Purchase;
import com.hongyu.entity.PurchaseShip;
import com.hongyu.service.PurchaseService;
import com.hongyu.service.PurchaseShipService;

@RequestMapping("/admin/business/purchase/ship")
@Controller
public class PurchaseShipController {
	@Resource(name = "purchaseShipServiceImpl")
	PurchaseShipService purchaseShipServiceImpl;
	
	@Resource(name = "purchaseServiceImpl")
	PurchaseService purchaseServiceImpl;
	
	@RequestMapping(value = "/list")
	@ResponseBody
	public Json purchaseShipList(Long purchaseid, HttpSession session) {
		Json json = new Json();
		
		try {
			Purchase purchase = purchaseServiceImpl.find(purchaseid);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("purchase", purchase));
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			List<PurchaseShip> ships = purchaseShipServiceImpl.findList(null, filters, orders);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(ships);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		
		return json;
	}
	
	static class PurchaseShipWrapper{
		Long purchaseid;
		List<PurchaseShip> purchaseships;
		public Long getPurchaseid() {
			return purchaseid;
		}
		public void setPurchaseid(Long purchaseid) {
			this.purchaseid = purchaseid;
		}
		public List<PurchaseShip> getPurchaseships() {
			return purchaseships;
		}
		public void setPurchaseships(List<PurchaseShip> purchaseships) {
			this.purchaseships = purchaseships;
		}
	}
	
	@RequestMapping(value = "/add")
	@ResponseBody
	public Json purchaseShipAdd(@RequestBody PurchaseShipWrapper wrapper, HttpSession session) {
		Json json = new Json();
		
		try {
			Purchase purchase = purchaseServiceImpl.find(wrapper.getPurchaseid());
			if (purchase == null) {
				json.setSuccess(true);
				json.setMsg("采购单不存在");
				json.setObj(null);
				return json;
			} else {
				for (PurchaseShip ship : wrapper.getPurchaseships()) {
					ship.setPurchase(purchase);
					purchaseShipServiceImpl.save(ship);
				}
				json.setSuccess(true);
				json.setMsg("添加成功");
				json.setObj(null);
			}
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败");
			json.setObj(e);
		}
		
		return json;
	}
}
