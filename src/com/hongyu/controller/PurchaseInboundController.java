package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
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
import com.hongyu.entity.PurchaseInbound;
import com.hongyu.entity.PurchaseItem;
import com.hongyu.service.PurchaseInboundService;
import com.hongyu.service.PurchaseItemService;
import com.hongyu.service.PurchaseService;
import com.hongyu.util.Constants;

@RequestMapping("/admin/business/purchase/inbound")
@Controller
public class PurchaseInboundController {

	@Resource(name = "purchaseServiceImpl")
	PurchaseService purchaseServiceImpl;
	
	@Resource(name = "purchaseInboundServiceImpl")
	PurchaseInboundService purchaseInboundServiceImpl;
	
	@Resource(name = "purchaseItemServiceImpl")
	PurchaseItemService purchaseItemServiceImpl;
	
	static class PurchaseInboundWrapper{
		Long purchaseid;
		List<PurchaseInbound> purchaseInbounds;
		
		public Long getPurchaseid() {
			return purchaseid;
		}
		public void setPurchaseid(Long purchaseid) {
			this.purchaseid = purchaseid;
		}
		public List<PurchaseInbound> getPurchaseInbounds() {
			return purchaseInbounds;
		}
		public void setPurchaseInbounds(List<PurchaseInbound> purchaseInbounds) {
			this.purchaseInbounds = purchaseInbounds;
		}
	}
	
	@RequestMapping(value = "/add")
	@ResponseBody
	public Json purchaseInboundAdd(@RequestBody PurchaseInboundWrapper wrapper, HttpSession session) {
		Json json = new Json();
		
		try {
			Purchase purchase = purchaseServiceImpl.find(wrapper.getPurchaseid());
			if (purchase == null) {
				json.setSuccess(true);
				json.setMsg("采购单不存在");
				json.setObj(null);
				return json;
			} else {
				if (purchase.getStatus() > Constants.PURCHASE_STATUS_INBOUNDING) {
					json.setSuccess(true);
					json.setMsg("该采购已完成入库");
					json.setObj(null);
					return json;
				}
				for (PurchaseInbound inbound : wrapper.getPurchaseInbounds()) {
					inbound.setPurchase(purchase);
					PurchaseItem item = purchaseItemServiceImpl.find(inbound.getPurchaseItem().getId());
					inbound.setPurchaseItem(item);
					inbound.setSpecification(item.getSpecification());
					
					
					purchaseInboundServiceImpl.save(inbound);
					if (inbound.getId() == null) {
						//入库时间由系统添加，为当前时间
						inbound.setInboundDate(new Date());
						purchaseInboundServiceImpl.save(inbound);
					} else {
						purchaseInboundServiceImpl.update(inbound);
					}
					
				}
				//判断采购状态是否已经到达入库中状态，若否，转移状态到入库中
				if (purchase.getStatus() != Constants.PURCHASE_STATUS_INBOUNDING) {
					purchase.setStatus(Constants.PURCHASE_STATUS_INBOUNDING);
					purchaseServiceImpl.save(purchase);
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
	
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json purchaseInboundList(Long purchaseid, HttpSession session) {
		Json json = new Json();
		
		try {
			Purchase purchase = purchaseServiceImpl.find(purchaseid);
			if (purchase == null) {
				json.setSuccess(true);
				json.setMsg("采购单不存在");
				json.setObj(null);
				return json;
			} else {
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(Filter.eq("purchase", purchase));
				List<Order> orders = new ArrayList<Order>();
				orders.add(Order.desc("id"));
				List<PurchaseInbound> items = purchaseInboundServiceImpl.findList(null, filters, orders);
				json.setSuccess(true);
				json.setMsg("添加成功");
				json.setObj(items);
			}
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败");
			json.setObj(e);
		}
		
		return json;
	}
}
