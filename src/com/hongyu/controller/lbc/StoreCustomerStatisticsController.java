package com.hongyu.controller.lbc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.Store;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.LineCatagoryService;
import com.hongyu.service.StoreService;
import com.hongyu.util.ArrayHandler;

@Controller
@RequestMapping("admin/store_customer_statistics")
public class StoreCustomerStatisticsController {
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	
	//统计新门店
	//统计新的门店
	//月度
	//目前只统计线路
	@RequestMapping(value="/month_view")
	@ResponseBody
	public Json getList(HttpSession session, @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, String storeName, Integer sort){
		
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try{
	
			StringBuilder sb = new StringBuilder("");
			sb.append("select hy_store.id as id, hy_store.store_name as store_name, count(hy_order_customer.id) as customer_num, sum(ifnull(hy_order.jiesuan_money1, 0) - ifnull(hy_order.jiesuan_tuikuan,0)) as money");
			//线路订单 供应商通过 
			sb.append(" from hy_store, hy_order_customer, hy_order, hy_order_item " + 
					" where hy_order.store_id=hy_store.id and hy_order_item.order_id=hy_order.id and hy_order_customer.item_id=hy_order_item.id and hy_order.status=3 " + 
					"  and hy_order.type=1 and hy_order.paystatus=1 and hy_order_item.status=0 ");
			if(start==null){
				start = new Date();
			}
			if(end==null){
				end = new Date();
			}
			
			sb.append(" and DATE_FORMAT(hy_order.createtime,'%Y-%m-%d') between '"+format.format(start)+"' and '"+format.format(end)+"'");
			sb.append(" and hy_order.supplier = '" + username + "'" );
			//按月groupby了
			//sb.append(" group by DATE_FORMAT(o1.register_date,'%Y-%m')");
			if(storeName != null) {
				sb.append(" and hy_store.store_name='"+ storeName + "'");
			}
			
			sb.append(" group by hy_store.id");
			
			if(sort != null) {
				if(sort == 0) {
					sb.append(" order by customer_num desc");
				}
				else {
					sb.append(" order by money desc");
				}
			}
			System.out.println(sb.toString());
			
			List<Object[]> list = hyOrderService.statis(sb.toString());
			if(list == null || list.size() == 0) {
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(list);
				return json;
			}
			
			String[] keys = new String[]{"id","storeName","customerNum","money"};
			List<Map<String, Object>> maps = new LinkedList<>();
			
			for(Object[] objects : list){
				maps.add(ArrayHandler.toMap(keys, objects));
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(maps);
		
		}catch (Exception e){
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	@RequestMapping(value="/detail")
	@ResponseBody
	public Json detail(HttpSession session, Long id,@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @DateTimeFormat(pattern = "yyyy-MM-dd")Date end){
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try{
			if(start==null){
				start = new Date();
			}
			if(end==null){
				end = new Date();
			}
			//storeId
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("storeId", id));
			filters.add(Filter.eq("status", 3));
			filters.add(Filter.eq("paystatus", 1));
			filters.add(Filter.le("createtime", end));
			filters.add(Filter.ge("createtime", start));
			filters.add(Filter.eq("supplier", admin));
			
			List<HyOrder> hyOrders = hyOrderService.findList(null, filters, null);
			List<Map<String, Object>> results = new LinkedList<>();
			for(HyOrder hyOrder : hyOrders) {
				Map<String, Object> map = new HashMap<>();
				map.put("orderNumber", hyOrder.getOrderNumber());
				Store store = storeService.find(hyOrder.getStoreId());
				if(store != null) {
					map.put("storeName", store.getStoreName());
				}
				
				map.put("createTime", hyOrder.getCreatetime());
				map.put("storeAdmin", store.getHyAdmin().getName());
				map.put("fatuanTime", hyOrder.getFatuandate());
				if(hyOrder.getJiesuanTuikuan() != null) {
					map.put("money", hyOrder.getJiesuanMoney1().subtract(hyOrder.getJiesuanTuikuan()));
				}
				else {
					map.put("money", hyOrder.getJiesuanMoney1());
				}
				int customerNum = 0;
				for(HyOrderItem hyOrderItem : hyOrder.getOrderItems()) {
					if(hyOrderItem.getStatus() == 0) {
						customerNum += hyOrderItem.getHyOrderCustomers().size();
					}
				}
				map.put("customerNum", customerNum);
				HyGroup hyGroup = hyGroupService.find(hyOrder.getGroupId());
				HyLine hyLine = hyGroup.getLine();
				if(hyLine != null) {
					map.put("productNumber", hyLine.getPn());
					map.put("productName", hyLine.getName());
				}
				
				results.add(map);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(results);
		
		}catch (Exception e){
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
}
