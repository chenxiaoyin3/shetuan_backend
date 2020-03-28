package com.hongyu.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.node.BigIntegerNode;
import org.bouncycastle.crypto.macs.HMac;
import org.hibernate.annotations.Parameter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.BusinessStore;
import com.hongyu.entity.OrderItemDivide;
import com.hongyu.entity.Store;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessStoreService;
import com.hongyu.service.OrderItemDivideService;
import com.hongyu.service.StoreService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;


@Controller
@RequestMapping("/ymmall/webusiness")
public class YmmallWeBusinessController {

	@Resource(name = "weBusinessServiceImpl")
	private WeBusinessService weBusinessServiceImpl;
	@Resource(name = "businessStoreServiceImpl")
	private BusinessStoreService businessStoreServiceImpl;
	@Resource(name = "storeServiceImpl")
	private StoreService storeServiceImpl;
	@Resource(name = "orderItemDivideServiceImpl")
	OrderItemDivideService orderItemDivideService;
	
	@Resource(name = "businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@ResponseBody
	public Object weBusinessDetail(Long id, String callback) {
		Json json = new Json();

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			WeBusiness weBusiness = weBusinessServiceImpl.find(id);
			if (weBusiness == null) {
				json.setMsg("微商不存在");
				json.setSuccess(false);
				json.setObj(null);
			} else {

				Map<String, Object> weBusinessMap = new HashMap<String, Object>();
				weBusinessMap.put("id", weBusiness.getId());
				weBusinessMap.put("name", weBusiness.getName());
				weBusinessMap.put("typeId", weBusiness.getType());
				weBusinessMap.put("mobile", weBusiness.getMobile());
				weBusinessMap.put("address", weBusiness.getAddress());
				weBusinessMap.put("isActive", weBusiness.getIsActive());
				weBusinessMap.put("openid", weBusiness.getWechatOpenId());
				weBusinessMap.put("wechatAccount", weBusiness.getWechatAccount());
				weBusinessMap.put("isLineWechatBusiness", weBusiness.getIsLineWechatBusiness());
				weBusinessMap.put("logo", weBusiness.getLogo());
				weBusinessMap.put("shopName", weBusiness.getShopName());
				map.put("weBusiness", weBusinessMap);

				// get store info
				if (weBusiness.getStoreId() == null) {
					map.put("store", null);
				} else if (weBusiness.getType() == Constants.WeBusinessType.Interior) {
					Store inStore = storeServiceImpl.find(weBusiness.getStoreId());
					Map<String, Object> store = new HashMap<String, Object>();
					store.put("id", inStore.getId());
					store.put("type", Constants.WeBusinessType.Interior);
					store.put("name", inStore.getStoreName());
					store.put("address", inStore.getAddress());
					map.put("store", store);
				} else if (weBusiness.getType() == Constants.WeBusinessType.Exterior) {
					BusinessStore exStore = businessStoreServiceImpl.find(weBusiness.getStoreId());
					Map<String, Object> store = new HashMap<String, Object>();
					store.put("id", exStore.getId());
					store.put("type", Constants.WeBusinessType.Exterior);
					store.put("name", exStore.getStoreName());
					store.put("address", exStore.getAddress());
					map.put("store", store);
				}

				// get introducer info
				WeBusiness introducer = weBusiness.getIntroducer();

				if (introducer == null) {
					map.put("introducer", null);
				} else {
					Map<String, Object> introducerMap = new HashMap<String, Object>();
					introducerMap.put("id", introducer.getId());
					introducerMap.put("name", introducer.getName());
					introducerMap.put("mobile", introducer.getMobile());
					introducerMap.put("wechatAccount", introducer.getWechatAccount());

					map.put("introducer", introducerMap);
				}

				json.setMsg("查询成功");
				json.setSuccess(true);
				json.setObj(map);
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("微商不存在");
			json.setSuccess(false);
			json.setObj(null);
		}
		if (callback != null)
			return new JSONPObject(callback, json);
		return json;
	}

	@RequestMapping("/total_divide")
	@ResponseBody
	public Json total_divide(/* HttpSession session */Long webusiness_id) {
		Json json = new Json();
		try {
			// Long webusiness_id=(Long)session.getAttribute("webusiness_id");
			WeBusiness weBusiness = weBusinessServiceImpl.find(webusiness_id);
			if (weBusiness == null) {
				json.setSuccess(false);
				json.setMsg("微商不存在");
			} else {
				BigDecimal total = new BigDecimal("0");
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("weBusiness", weBusiness));
				List<OrderItemDivide> orderItemDivides = orderItemDivideService.findList(null, filters, null);
				for (OrderItemDivide orderItem : orderItemDivides) {
					total = total.add(orderItem.getWeBusinessAmount());
				}
				List<Filter> filters1 = new ArrayList<>();
				filters1.add(Filter.eq("rWeBusiness", weBusiness));
				List<OrderItemDivide> orderItemDivides1 = orderItemDivideService.findList(null, filters1, null);
				for (OrderItemDivide orderItem : orderItemDivides1) {
					total = total.add(orderItem.getrWeBusinessAmount());
				}
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("mWeBusiness", weBusiness));
				List<OrderItemDivide> orderItemDivides2 = orderItemDivideService.findList(null, filters2, null);
				for (OrderItemDivide orderItem : orderItemDivides2) {
					total = total.add(orderItem.getmWeBusinessAmount());
				}
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(total);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("/daily_divide")
	@ResponseBody
	public Json daily_divide(/* HttpSession session */Long webusiness_id) {
		Json json = new Json();
		try {
			// Long webusiness_id=(Long)session.getAttribute("webusiness_id");
			WeBusiness weBusiness = weBusinessServiceImpl.find(webusiness_id);
			if (weBusiness == null) {
				json.setSuccess(false);
				json.setMsg("微商不存在");
			} else {
				BigDecimal total = new BigDecimal("0");
				Date today = new Date();
				today = DateUtil.getPreDay(today);	//改成前一天的
				Date todayStart = DateUtil.getStartOfDay(today);
				Date todayEnd = DateUtil.getEndOfDay(today);
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("weBusiness", weBusiness));
				filters.add(Filter.ge("acceptTime", todayStart));
				filters.add(Filter.le("acceptTime", todayEnd));
				List<OrderItemDivide> orderItemDivides = orderItemDivideService.findList(null, filters, null);
				for (OrderItemDivide orderItem : orderItemDivides) {
					total = total.add(orderItem.getWeBusinessAmount());
				}
				List<Filter> filters1 = new ArrayList<>();
				filters1.add(Filter.eq("rWeBusiness", weBusiness));
				filters1.add(Filter.ge("acceptTime", todayStart));
				filters1.add(Filter.le("acceptTime", todayEnd));
				List<OrderItemDivide> orderItemDivides1 = orderItemDivideService.findList(null, filters1, null);
				for (OrderItemDivide orderItem : orderItemDivides1) {
					total = total.add(orderItem.getrWeBusinessAmount());
				}
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("mWeBusiness", weBusiness));
				filters2.add(Filter.ge("acceptTime", todayStart));
				filters2.add(Filter.le("acceptTime", todayEnd));
				List<OrderItemDivide> orderItemDivides2 = orderItemDivideService.findList(null, filters2, null);
				for (OrderItemDivide orderItem : orderItemDivides2) {
					total = total.add(orderItem.getmWeBusinessAmount());
				}
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(total);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("/total_divide_list")
	@ResponseBody
	public Json total_divide_list(Pageable pageable, /* HttpSession session */Long webusiness_id) {
		Json json = new Json();
		try {
			// Long webusiness_id=(Long)session.getAttribute("webusiness_id");
			WeBusiness weBusiness = weBusinessServiceImpl.find(webusiness_id);
			if (weBusiness == null) {
				json.setSuccess(false);
				json.setMsg("微商不存在");
			} else {
				HashMap<String, Object>hm=new HashMap<>();
				List<HashMap<String, Object>> result=new ArrayList<>();
				List<Filter> filters1 = new ArrayList<>();
				filters1.add(Filter.eq("weBusiness", weBusiness));
				List<Order> orders = new ArrayList<Order>();
				Order order = Order.desc("ordertime");
				orders.add(order);
				List<OrderItemDivide> list1 = orderItemDivideService.findList(null,filters1,orders);
				for(OrderItemDivide tmp:list1){
					HashMap<String, Object> m = new HashMap<String, Object>();
					m.put("id", tmp.getId());
					m.put("ordertime", tmp.getOrdertime());
					m.put("totalAmount", tmp.getTotalAmount());
					m.put("weBusinessAmount", tmp.getWeBusinessAmount());
					BusinessOrderItem businessOrderItem=tmp.getBusinessOrderItem();
					//获取订单条目的特产名称
					m.put("itemName", businessOrderItemService.getSpecialtyName(businessOrderItem));
					BusinessOrder businessOrder=businessOrderItem.getBusinessOrder();
					WechatAccount wechatAccount=businessOrder.getWechatAccount();
					m.put("wechatName", wechatAccount.getWechatName());
					result.add(m);
					
				}
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("rWeBusiness", weBusiness));
				List<Order> orders1 = new ArrayList<Order>();
				Order order1 = Order.desc("ordertime");
				orders1.add(order1);
				List<OrderItemDivide> list2 = orderItemDivideService.findList(null,filters2,orders1);
				for(OrderItemDivide tmp:list2){
					HashMap<String, Object> m = new HashMap<String, Object>();
					m.put("id", tmp.getId());
					m.put("ordertime", tmp.getOrdertime());
					m.put("totalAmount", tmp.getTotalAmount());
					m.put("weBusinessAmount", tmp.getrWeBusinessAmount());
					BusinessOrderItem businessOrderItem=tmp.getBusinessOrderItem();
					//获取订单条目的特产名称
					m.put("itemName", businessOrderItemService.getSpecialtyName(businessOrderItem));
					BusinessOrder businessOrder=businessOrderItem.getBusinessOrder();
					WechatAccount wechatAccount=businessOrder.getWechatAccount();
					m.put("wechatName", wechatAccount.getWechatName());
					result.add(m);
				}
				List<Filter> filters3 = new ArrayList<>();
				filters3.add(Filter.eq("mWeBusiness", weBusiness));
//				pageable.setFilters(filters3);
				List<Order> orders2 = new ArrayList<Order>();
				Order order2 = Order.desc("ordertime");
				orders2.add(order2);
				List<OrderItemDivide> list3 = orderItemDivideService.findList(null,filters3,orders2);
				for(OrderItemDivide tmp:list3){
					HashMap<String, Object> m = new HashMap<String, Object>();
					m.put("id", tmp.getId());
					m.put("ordertime", tmp.getOrdertime());
					m.put("totalAmount", tmp.getTotalAmount());
					m.put("weBusinessAmount", tmp.getmWeBusinessAmount());
					//产品名称
					BusinessOrderItem businessOrderItem=tmp.getBusinessOrderItem();
					//获取订单条目的特产名称
					m.put("itemName", businessOrderItemService.getSpecialtyName(businessOrderItem));
					BusinessOrder businessOrder=businessOrderItem.getBusinessOrder();
					WechatAccount wechatAccount=businessOrder.getWechatAccount();
					m.put("wechatName", wechatAccount.getWechatName());
					result.add(m);
				}
				int pageNumber=pageable.getPage();
				int pagesize=pageable.getRows();
				List<HashMap<String, Object>> ans=result.subList((pageNumber-1)*pagesize, pageNumber*pagesize>result.size()?result.size():pageNumber*pagesize);
				hm.put("total", result.size());
				hm.put("pageNumber", pageNumber);
				hm.put("pageSize", pagesize);
				hm.put("rows", ans);
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(hm);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("/daily_divide_list")
	@ResponseBody
	public Json daily_divide_list(Pageable pageable, /* HttpSession session */Long webusiness_id) {
		Json json = new Json();
		try {
			// Long webusiness_id=(Long)session.getAttribute("webusiness_id");
			WeBusiness weBusiness = weBusinessServiceImpl.find(webusiness_id);
			if (weBusiness == null) {
				json.setSuccess(false);
				json.setMsg("微商不存在");
			} else {
				HashMap<String, Object>hm=new HashMap<>();
				List<HashMap<String, Object>> result=new ArrayList<>();
				Date today = DateUtil.getPreDay(new Date());
				Date todayStart = DateUtil.getStartOfDay(today);
				Date todayEnd = DateUtil.getEndOfDay(today);
				
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("weBusiness", weBusiness));
				filters.add(Filter.ge("acceptTime", todayStart));
				filters.add(Filter.le("acceptTime", todayEnd));
//				pageable.setFilters(filters);
				List<Order> orders = new ArrayList<Order>();
				Order order = Order.desc("ordertime");
				orders.add(order);
				List<OrderItemDivide> lists = orderItemDivideService.findList(null,filters,orders);
				for(OrderItemDivide tmp:lists){
					HashMap<String, Object> m = new HashMap<String, Object>();
					m.put("id", tmp.getId());
					m.put("ordertime", tmp.getOrdertime());
					m.put("totalAmount", tmp.getTotalAmount());
					m.put("weBusinessAmount", tmp.getWeBusinessAmount());
					//产品名称
					BusinessOrderItem businessOrderItem=tmp.getBusinessOrderItem();
					//获取订单条目的特产名称
					m.put("itemName", businessOrderItemService.getSpecialtyName(businessOrderItem));
					BusinessOrder businessOrder=businessOrderItem.getBusinessOrder();
					WechatAccount wechatAccount=businessOrder.getWechatAccount();
					m.put("wechatName", wechatAccount.getWechatName());
					result.add(m);
				}
				List<Filter> filters1 = new ArrayList<>();
				filters1.add(Filter.eq("rWeBusiness", weBusiness));
				filters1.add(Filter.ge("acceptTime", todayStart));
				filters1.add(Filter.le("acceptTime", todayEnd));
//				pageable.setFilters(filters);
				List<Order> orders1 = new ArrayList<Order>();
				Order order1 = Order.desc("ordertime");
				orders1.add(order1);
				List<OrderItemDivide> lists1 = orderItemDivideService.findList(null,filters1,orders1);
				for(OrderItemDivide tmp:lists1){
					HashMap<String, Object> m = new HashMap<String, Object>();
					m.put("id", tmp.getId());
					m.put("ordertime", tmp.getOrdertime());
					m.put("totalAmount", tmp.getTotalAmount());
					m.put("weBusinessAmount", tmp.getrWeBusinessAmount());
					//产品名称
					BusinessOrderItem businessOrderItem=tmp.getBusinessOrderItem();
					//获取订单条目的特产名称
					m.put("itemName", businessOrderItemService.getSpecialtyName(businessOrderItem));
					BusinessOrder businessOrder=businessOrderItem.getBusinessOrder();
					WechatAccount wechatAccount=businessOrder.getWechatAccount();
					m.put("wechatName", wechatAccount.getWechatName());
					result.add(m);
				}
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("mWeBusiness", weBusiness));
				filters2.add(Filter.ge("acceptTime", todayStart));
				filters2.add(Filter.le("acceptTime", todayEnd));
//				pageable.setFilters(filters);
				List<Order> orders2 = new ArrayList<Order>();
				Order order2 = Order.desc("ordertime");
				orders2.add(order2);
				List<OrderItemDivide> lists2 = orderItemDivideService.findList(null,filters2,orders2);
				for(OrderItemDivide tmp:lists2){
					HashMap<String, Object> m = new HashMap<String, Object>();
					m.put("id", tmp.getId());
					m.put("ordertime", tmp.getOrdertime());
					m.put("totalAmount", tmp.getTotalAmount());
					m.put("weBusinessAmount", tmp.getmWeBusinessAmount());
					//产品名称
					BusinessOrderItem businessOrderItem=tmp.getBusinessOrderItem();
					//获取订单条目的特产名称
					m.put("itemName", businessOrderItemService.getSpecialtyName(businessOrderItem));
					BusinessOrder businessOrder=businessOrderItem.getBusinessOrder();
					WechatAccount wechatAccount=businessOrder.getWechatAccount();
					m.put("wechatName", wechatAccount.getWechatName());
					result.add(m);
				}
				int pageNumber=pageable.getPage();
				int pagesize=pageable.getRows();
				List<HashMap<String, Object>> ans=result.subList((pageNumber-1)*pagesize, pageNumber*pagesize>result.size()?result.size():pageNumber*pagesize);
				hm.put("total", result.size());
				hm.put("pageNumber", pageNumber);
				hm.put("pageSize", pagesize);
				hm.put("rows", ans);
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(hm);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	//设置微商的店铺名称和logo
		@RequestMapping("logo/edit")
		@ResponseBody
		public Json logoedit(Long weBusinessId,String logoUrl)
		{
			Json json=new Json();
			try {
				WeBusiness weBusiness=weBusinessServiceImpl.find(weBusinessId);
				weBusiness.setLogo(logoUrl);
				weBusinessServiceImpl.update(weBusiness);
				json.setSuccess(true);
				json.setMsg("设置成功");
			}
			catch(Exception e) {
				json.setSuccess(false);
				json.setMsg(e.getMessage());
			}
			return json;
		}
		
		@RequestMapping("shopName/edit")
		@ResponseBody
		public Json shopNameedit(Long weBusinessId,String shopName)
		{
			Json json=new Json();
			try {
				WeBusiness weBusiness=weBusinessServiceImpl.find(weBusinessId);
				weBusiness.setShopName(shopName);
				weBusinessServiceImpl.update(weBusiness);
				json.setSuccess(true);
				json.setMsg("设置成功");
			}
			catch(Exception e) {
				json.setSuccess(false);
				json.setMsg(e.getMessage());
			}
			return json;
		}

	@RequestMapping("get_id_by_oid")
	@ResponseBody
	public Json getIdByOId(Long oid,Long cid){
		Json json = new Json();
		try{

			StringBuilder sb = new StringBuilder();
			sb.append("select id from hy_we_business where origin_url like '%uid="+oid+"&companyuid="+cid+"'");
			List objects = weBusinessServiceImpl.statis(sb.toString());

			if(objects!=null && !objects.isEmpty()){
				Long id = ((BigInteger)objects.get(0)).longValue();
				json.setObj(id);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");

		}catch (Exception e){
			json.setSuccess(true);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
		}
		return json;
	}

}
