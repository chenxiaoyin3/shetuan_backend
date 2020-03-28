package com.hongyu.controller.lbc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.controller.BaseController;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.SpecialtyCategoryService;
import com.hongyu.service.SpecialtyService;


@Controller
@RequestMapping("admin/business/specialty_turnover/annual_list_compare")
public class AnnualSalesVolumeContrastController {
	
	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderService;
	
	@Resource(name = "businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;
	
	@Resource(name = "specialtyServiceImpl")
	SpecialtyService specialtyService;
	
	@Resource(name = "specialtyCategoryServiceImpl")
	SpecialtyCategoryService specialtyCategoryService;
	
	BaseController baseController = new BaseController();
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value="/view")
	@ResponseBody
	public Json GroupMemberList(HttpSession session, Integer startYear, Integer endYear, Integer type, Long value, Pageable pageable){
		Json json = new Json();
		try {

			List<HashMap<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			
			//年份，月份，每月营业额
			Map<Integer, ArrayList<BigDecimal>> yearMap = new HashMap<Integer, ArrayList<BigDecimal>>();
			
			int pageRow = pageable.getRows();
			int pageNumber = pageable.getPage();
			//全部
			if(type == null || type == 0 || value == null) {
				//得到全部的订单
				List<Order> orders = new ArrayList<Order>();
				//按时间升序排列
				orders.add(Order.desc("orderTime"));
				
				//得到全部状态为已结算或者已退款的订单
				List<Filter> filters1 = new ArrayList<Filter>();
				List<Filter> filters2 = new ArrayList<Filter>();

				filters1.add(Filter.eq("orderState", 6));
				filters2.add(Filter.eq("orderState", 12));
				List<BusinessOrder> businessOrders1 = businessOrderService.findList(null, filters1, orders);
				List<BusinessOrder> businessOrders2 = businessOrderService.findList(null, filters2, orders);
				
				businessOrders1.addAll(businessOrders2);
				
				
				List<Filter> businessOrderItemFilters = new ArrayList<Filter>();
				
				if(!businessOrders1.isEmpty()){

					businessOrderItemFilters.add(Filter.in("businessOrder", businessOrders1));
				}
				List<Order> businessOrderItemOrders = new ArrayList<Order>();
				businessOrderItemOrders.add(Order.asc("createTime"));
				//2018-8-7 cwz dubug出现问题
				//找出这些订单条目
				List<BusinessOrderItem> businessOrderItems = businessOrderItemService.findList(null,businessOrderItemFilters,businessOrderItemOrders);
				
				//对每一个订单条目
				for(BusinessOrderItem businessOrderItem:businessOrderItems) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(businessOrderItem.getCreateTime());
					int year = calendar.get(Calendar.YEAR);
					if(startYear != null && startYear > year) {
						continue;
					}
					if(endYear != null && endYear < year) {
						continue;
					}
					if(!yearMap.containsKey(year)) {
						ArrayList<BigDecimal> bdArray = new ArrayList<BigDecimal>();
						//初始化12个月份
						for(int i = 0; i < 12; i++) {
							//0--11
							bdArray.add(new BigDecimal(0));
						}
						yearMap.put(year, bdArray);
					}
					BigDecimal money;
					//计算订单的收入
					if(businessOrderItem.getReturnQuantity() == null) {
						money = businessOrderItem.getSalePrice().multiply(new BigDecimal(businessOrderItem.getQuantity()));
					}
					else {
						money = businessOrderItem.getSalePrice().multiply(new BigDecimal(businessOrderItem.getQuantity() - businessOrderItem.getReturnQuantity()));
					}
					
					ArrayList<BigDecimal> bdArray2 = yearMap.get(year);
					bdArray2.set(calendar.get(Calendar.MONTH), bdArray2.get(calendar.get(Calendar.MONTH)).add(money));
					
					yearMap.put(year, bdArray2);
					
				}
				
			}
			else if(type == 1) {
				//按类别查 value为类别的值
				
				List<Order> orders = new ArrayList<Order>();
				//按时间升序排列
				orders.add(Order.asc("orderTime"));
				
				//得到全部状态为已结算或者已退款的订单
				List<Filter> filters1 = new ArrayList<Filter>();
				List<Filter> filters2 = new ArrayList<Filter>();
				
				//List<Integer> states = new ArrayList<Integer>();
				//已结算
				//states.add(6);
				//退款已完成
				//states.add(12);
				
				 
				
				
				filters1.add(Filter.eq("orderState", 6));
				filters2.add(Filter.eq("orderState", 12));
				List<BusinessOrder> businessOrders1 = businessOrderService.findList(null, filters1, orders);
				List<BusinessOrder> businessOrders2 = businessOrderService.findList(null, filters2, orders);
				
				businessOrders1.addAll(businessOrders2);
				//List<Filter> businessOrderItemFilters = new ArrayList<Filter>();
				//businessOrderItemFilters.add(Filter.in("businessOrder", businessOrders1));
				
				//根据value找到特产类别实体
				SpecialtyCategory specialtyCategory = specialtyCategoryService.find(value);
				//通过类别找到特产 list
				List<Filter> specialtyFilters = new ArrayList<Filter>();
				specialtyFilters.add(Filter.eq("category", specialtyCategory));
				List<Specialty> specialties = specialtyService.findList(null, specialtyFilters, null);
				
				List<Long> specialty_ids = new ArrayList<Long>();
				//通过特产list生成特产id list
				for(Specialty specialty:specialties) {
					specialty_ids.add(specialty.getId());
				}
				
				
				List<Filter> businessOrderFilters = new ArrayList<Filter>();
				businessOrderFilters.add(Filter.in("businessOrder", businessOrders1));
				
				
				//寻找特产id在特产id list中的businessorderitem
				//businessOrderFilters.add(Filter.in("specialty", specialty_ids));
				
				List<Order> businessOrderItemOrders = new ArrayList<Order>();
				businessOrderItemOrders.add(Order.asc("createTime"));
				//找出这些订单条目
				List<BusinessOrderItem> businessOrderItems = businessOrderItemService.findList(null,businessOrderFilters,businessOrderItemOrders);
				
				List<BusinessOrderItem> businessOrderItems2 = new ArrayList<BusinessOrderItem>();
				
				for(BusinessOrderItem businessOrderItem:businessOrderItems) {
					for(Long specialty_id:specialty_ids) {
						if(businessOrderItem.getSpecialty() == specialty_id) {
							businessOrderItems2.add(businessOrderItem);
						}
					}
				}
				
				
				//对每一个订单条目
				for(BusinessOrderItem businessOrderItem:businessOrderItems2) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(businessOrderItem.getCreateTime());
					int year = calendar.get(Calendar.YEAR);
					if(startYear != null && startYear > year) {
						continue;
					}
					if(endYear != null && endYear < year) {
						continue;
					}
					if(!yearMap.containsKey(year)) {
						ArrayList<BigDecimal> bdArray = new ArrayList<BigDecimal>();
						//初始化12个月份
						for(int i = 0; i < 12; i++) {
							//0--11
							bdArray.add(new BigDecimal(0));
						}
						yearMap.put(year, bdArray);
					}
					//计算订单的收入
					BigDecimal money;
					if(businessOrderItem.getReturnQuantity() == null) {
						money = businessOrderItem.getSalePrice().multiply(new BigDecimal(businessOrderItem.getQuantity()));
					}
					else {
						money = businessOrderItem.getSalePrice().multiply(new BigDecimal(businessOrderItem.getQuantity() - businessOrderItem.getReturnQuantity()));
					}
					ArrayList<BigDecimal> bdArray2 = yearMap.get(year);
					bdArray2.set(calendar.get(Calendar.MONTH), bdArray2.get(calendar.get(Calendar.MONTH)).add(money));
					
					yearMap.put(year, bdArray2);
					
				}
			}
			else if(type == 2) {
				//按产品查 value为产品id				
				List<Order> orders = new ArrayList<Order>();
				//按时间升序排列
				orders.add(Order.asc("orderTime"));
				
				//得到全部状态为已结算或者已退款的订单
				List<Filter> filters1 = new ArrayList<Filter>();
				List<Filter> filters2 = new ArrayList<Filter>();
				
				//List<Integer> states = new ArrayList<Integer>();
				//已结算
				//states.add(6);
				//退款已完成
				//states.add(12);
				
				 
				
				
				filters1.add(Filter.eq("orderState", 6));
				filters2.add(Filter.eq("orderState", 12));
				List<BusinessOrder> businessOrders1 = businessOrderService.findList(null, filters1, orders);
				List<BusinessOrder> businessOrders2 = businessOrderService.findList(null, filters2, orders);
				
				businessOrders1.addAll(businessOrders2);
				//List<Filter> businessOrderItemFilters = new ArrayList<Filter>();
				//businessOrderItemFilters.add(Filter.in("businessOrder", businessOrders1));
				//List<BusinessOrder> businessOrders = businessOrderService.findList(null, businessOrderItemFilters, orders);
				
				List<Filter> businessOrderFilters = new ArrayList<Filter>();
				businessOrderFilters.add(Filter.in("businessOrder", businessOrders1));
				
				//添加类别过滤
				businessOrderFilters.add(Filter.eq("specialty", value));
				
				List<Order> businessOrderItemOrders = new ArrayList<Order>();
				businessOrderItemOrders.add(Order.asc("createTime"));
				//找出这些订单条目
				List<BusinessOrderItem> businessOrderItems = businessOrderItemService.findList(null,businessOrderFilters,businessOrderItemOrders);
				
				//对每一个订单条目
				for(BusinessOrderItem businessOrderItem:businessOrderItems) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(businessOrderItem.getCreateTime());
					int year = calendar.get(Calendar.YEAR);
					if(startYear != null && startYear > year) {
						continue;
					}
					if(endYear != null && endYear < year) {
						continue;
					}
					if(!yearMap.containsKey(year)) {
						ArrayList<BigDecimal> bdArray = new ArrayList<BigDecimal>();
						//初始化12个月份
						for(int i = 0; i < 12; i++) {
							//0--11
							bdArray.add(new BigDecimal(0));
						}
						yearMap.put(year, bdArray);
					}
					//计算订单的收入
					//计算订单的收入
					BigDecimal money;
					if(businessOrderItem.getReturnQuantity() == null) {
						money = businessOrderItem.getSalePrice().multiply(new BigDecimal(businessOrderItem.getQuantity()));
					}
					else {
						money = businessOrderItem.getSalePrice().multiply(new BigDecimal(businessOrderItem.getQuantity() - businessOrderItem.getReturnQuantity()));
					}
					
					ArrayList<BigDecimal> bdArray2 = yearMap.get(year);
					bdArray2.set(calendar.get(Calendar.MONTH), bdArray2.get(calendar.get(Calendar.MONTH)).add(money));
					
					yearMap.put(year, bdArray2);
					
				}
				
				
				
			}
			
			//遍历yearmap
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			
			
			for (Integer key : yearMap.keySet()) {
				//年和bigdemical数组
				resultMap.put("saleTime", key);
				List<BigDecimal> monthMoney= yearMap.get(key);
				resultMap.put("saleJan", monthMoney.get(0));
				resultMap.put("saleFeb", monthMoney.get(1));
				resultMap.put("saleMar", monthMoney.get(2));
				resultMap.put("saleApr", monthMoney.get(3));
				resultMap.put("saleMay", monthMoney.get(4));
				resultMap.put("saleJun", monthMoney.get(5));
				resultMap.put("saleJul", monthMoney.get(6));
				resultMap.put("saleAug", monthMoney.get(7));
				resultMap.put("saleSep", monthMoney.get(8));
				resultMap.put("saleOct", monthMoney.get(9));
				resultMap.put("saleNov", monthMoney.get(10));
				resultMap.put("saleDec", monthMoney.get(11));
				list.add(resultMap);
			}
			
			List<HashMap<String, Object>> pageList = new ArrayList<>();
			
			//分页
			for(int i = (pageNumber - 1) * pageRow; i < list.size() && i < pageNumber * pageRow; i++) {
				pageList.add(list.get(i));	
			}
			
			map.put("rows", pageList);
		    map.put("pageNumber", pageable.getPage());
		    map.put("pageSize", pageable.getRows());
		    map.put("total",list.size());
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);

			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		
		return json;
	}
	
	//导出excel
	@RequestMapping(value="/get_excel")
	//@ResponseBody
	public void export2Excel(HttpServletRequest request,
			HttpServletResponse response, String fileName,
			String tableTitle, String configFile,
			HttpSession session, Integer startYear, Integer endYear, 
			Integer type, Long value) {
		
		Json json = new Json();
		
		try {
			
			List<HashMap<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			
			//年份，月份，每月营业额
			Map<Integer, ArrayList<BigDecimal>> yearMap = new HashMap<Integer, ArrayList<BigDecimal>>();
			
			//全部
			if(type == null || type == 0 || value == null) {
				//得到全部的订单
				List<Order> orders = new ArrayList<Order>();
				//按时间升序排列
				orders.add(Order.asc("orderTime"));
				
				//得到全部状态为已结算或者已退款的订单
				List<Filter> filters1 = new ArrayList<Filter>();
				List<Filter> filters2 = new ArrayList<Filter>();

				filters1.add(Filter.eq("orderState", 6));
				filters2.add(Filter.eq("orderState", 12));
				List<BusinessOrder> businessOrders1 = businessOrderService.findList(null, filters1, orders);
				List<BusinessOrder> businessOrders2 = businessOrderService.findList(null, filters2, orders);
				
				businessOrders1.addAll(businessOrders2);
				
				
				List<Filter> businessOrderItemFilters = new ArrayList<Filter>();
				businessOrderItemFilters.add(Filter.in("businessOrder", businessOrders1));
				List<Order> businessOrderItemOrders = new ArrayList<Order>();
				businessOrderItemOrders.add(Order.asc("createTime"));
				//找出这些订单条目
				List<BusinessOrderItem> businessOrderItems = businessOrderItemService.findList(null,businessOrderItemFilters,businessOrderItemOrders);
				
				//对每一个订单条目
				for(BusinessOrderItem businessOrderItem:businessOrderItems) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(businessOrderItem.getCreateTime());
					int year = calendar.get(Calendar.YEAR);
					if(startYear != null && startYear > year) {
						continue;
					}
					if(endYear != null && endYear < year) {
						continue;
					}
					if(!yearMap.containsKey(year)) {
						ArrayList<BigDecimal> bdArray = new ArrayList<BigDecimal>();
						//初始化12个月份
						for(int i = 0; i < 12; i++) {
							//0--11
							bdArray.add(new BigDecimal(0));
						}
						yearMap.put(year, bdArray);
					}
					BigDecimal money;
					//计算订单的收入
					if(businessOrderItem.getReturnQuantity() == null) {
						money = businessOrderItem.getSalePrice().multiply(new BigDecimal(businessOrderItem.getQuantity()));
					}
					else {
						money = businessOrderItem.getSalePrice().multiply(new BigDecimal(businessOrderItem.getQuantity() - businessOrderItem.getReturnQuantity()));
					}
					
					ArrayList<BigDecimal> bdArray2 = yearMap.get(year);
					bdArray2.set(calendar.get(Calendar.MONTH), bdArray2.get(calendar.get(Calendar.MONTH)).add(money));
					
					yearMap.put(year, bdArray2);
					
				}
				
			}
			else if(type == 1) {
				//按类别查 value为类别的值
				
				List<Order> orders = new ArrayList<Order>();
				//按时间升序排列
				orders.add(Order.asc("orderTime"));
				
				//得到全部状态为已结算或者已退款的订单
				List<Filter> filters1 = new ArrayList<Filter>();
				List<Filter> filters2 = new ArrayList<Filter>();
				
				//List<Integer> states = new ArrayList<Integer>();
				//已结算
				//states.add(6);
				//退款已完成
				//states.add(12);
				
				 
				
				
				filters1.add(Filter.eq("orderState", 6));
				filters2.add(Filter.eq("orderState", 12));
				List<BusinessOrder> businessOrders1 = businessOrderService.findList(null, filters1, orders);
				List<BusinessOrder> businessOrders2 = businessOrderService.findList(null, filters2, orders);
				
				businessOrders1.addAll(businessOrders2);
				//List<Filter> businessOrderItemFilters = new ArrayList<Filter>();
				//businessOrderItemFilters.add(Filter.in("businessOrder", businessOrders1));
				
				//根据value找到特产类别实体
				SpecialtyCategory specialtyCategory = specialtyCategoryService.find(value);
				//通过类别找到特产 list
				List<Filter> specialtyFilters = new ArrayList<Filter>();
				specialtyFilters.add(Filter.eq("category", specialtyCategory));
				List<Specialty> specialties = specialtyService.findList(null, specialtyFilters, null);
				
				List<Long> specialty_ids = new ArrayList<Long>();
				//通过特产list生成特产id list
				for(Specialty specialty:specialties) {
					specialty_ids.add(specialty.getId());
				}
				
				
				List<Filter> businessOrderFilters = new ArrayList<Filter>();
				businessOrderFilters.add(Filter.in("businessOrder", businessOrders1));
				
				
				//寻找特产id在特产id list中的businessorderitem
				//businessOrderFilters.add(Filter.in("specialty", specialty_ids));
				
				List<Order> businessOrderItemOrders = new ArrayList<Order>();
				businessOrderItemOrders.add(Order.asc("createTime"));
				//找出这些订单条目
				List<BusinessOrderItem> businessOrderItems = businessOrderItemService.findList(null,businessOrderFilters,businessOrderItemOrders);
				
				List<BusinessOrderItem> businessOrderItems2 = new ArrayList<BusinessOrderItem>();
				
				for(BusinessOrderItem businessOrderItem:businessOrderItems) {
					for(Long specialty_id:specialty_ids) {
						if(businessOrderItem.getSpecialty() == specialty_id) {
							businessOrderItems2.add(businessOrderItem);
						}
					}
				}
				
				
				//对每一个订单条目
				for(BusinessOrderItem businessOrderItem:businessOrderItems2) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(businessOrderItem.getCreateTime());
					int year = calendar.get(Calendar.YEAR);
					if(startYear != null && startYear > year) {
						continue;
					}
					if(endYear != null && endYear < year) {
						continue;
					}
					if(!yearMap.containsKey(year)) {
						ArrayList<BigDecimal> bdArray = new ArrayList<BigDecimal>();
						//初始化12个月份
						for(int i = 0; i < 12; i++) {
							//0--11
							bdArray.add(new BigDecimal(0));
						}
						yearMap.put(year, bdArray);
					}
					//计算订单的收入
					BigDecimal money;
					if(businessOrderItem.getReturnQuantity() == null) {
						money = businessOrderItem.getSalePrice().multiply(new BigDecimal(businessOrderItem.getQuantity()));
					}
					else {
						money = businessOrderItem.getSalePrice().multiply(new BigDecimal(businessOrderItem.getQuantity() - businessOrderItem.getReturnQuantity()));
					}
					ArrayList<BigDecimal> bdArray2 = yearMap.get(year);
					bdArray2.set(calendar.get(Calendar.MONTH), bdArray2.get(calendar.get(Calendar.MONTH)).add(money));
					
					yearMap.put(year, bdArray2);
					
				}
			}
			else if(type == 2) {
				//按产品查 value为产品id				
				List<Order> orders = new ArrayList<Order>();
				//按时间升序排列
				orders.add(Order.asc("orderTime"));
				
				//得到全部状态为已结算或者已退款的订单
				List<Filter> filters1 = new ArrayList<Filter>();
				List<Filter> filters2 = new ArrayList<Filter>();
				
				//List<Integer> states = new ArrayList<Integer>();
				//已结算
				//states.add(6);
				//退款已完成
				//states.add(12);
				
				 
				
				
				filters1.add(Filter.eq("orderState", 6));
				filters2.add(Filter.eq("orderState", 12));
				List<BusinessOrder> businessOrders1 = businessOrderService.findList(null, filters1, orders);
				List<BusinessOrder> businessOrders2 = businessOrderService.findList(null, filters2, orders);
				
				businessOrders1.addAll(businessOrders2);
				//List<Filter> businessOrderItemFilters = new ArrayList<Filter>();
				//businessOrderItemFilters.add(Filter.in("businessOrder", businessOrders1));
				//List<BusinessOrder> businessOrders = businessOrderService.findList(null, businessOrderItemFilters, orders);
				
				List<Filter> businessOrderFilters = new ArrayList<Filter>();
				businessOrderFilters.add(Filter.in("businessOrder", businessOrders1));
				
				//添加类别过滤
				businessOrderFilters.add(Filter.eq("specialty", value));
				
				List<Order> businessOrderItemOrders = new ArrayList<Order>();
				businessOrderItemOrders.add(Order.asc("createTime"));
				//找出这些订单条目
				List<BusinessOrderItem> businessOrderItems = businessOrderItemService.findList(null,businessOrderFilters,businessOrderItemOrders);
				
				//对每一个订单条目
				for(BusinessOrderItem businessOrderItem:businessOrderItems) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(businessOrderItem.getCreateTime());
					int year = calendar.get(Calendar.YEAR);
					if(startYear != null && startYear > year) {
						continue;
					}
					if(endYear != null && endYear < year) {
						continue;
					}
					if(!yearMap.containsKey(year)) {
						ArrayList<BigDecimal> bdArray = new ArrayList<BigDecimal>();
						//初始化12个月份
						for(int i = 0; i < 12; i++) {
							//0--11
							bdArray.add(new BigDecimal(0));
						}
						yearMap.put(year, bdArray);
					}
					//计算订单的收入
					//计算订单的收入
					BigDecimal money;
					if(businessOrderItem.getReturnQuantity() == null) {
						money = businessOrderItem.getSalePrice().multiply(new BigDecimal(businessOrderItem.getQuantity()));
					}
					else {
						money = businessOrderItem.getSalePrice().multiply(new BigDecimal(businessOrderItem.getQuantity() - businessOrderItem.getReturnQuantity()));
					}
					
					ArrayList<BigDecimal> bdArray2 = yearMap.get(year);
					bdArray2.set(calendar.get(Calendar.MONTH), bdArray2.get(calendar.get(Calendar.MONTH)).add(money));
					
					yearMap.put(year, bdArray2);
					
				}
				
				
				
			}
			
			//遍历yearmap
			List<Wrap> tempList = new ArrayList<Wrap>();
			for (Integer key : yearMap.keySet()) {
				//年和bigdemical数组
				List<BigDecimal> monthMoney= yearMap.get(key);		
				Wrap wrap = new Wrap();
				wrap.setSaleTime(key);
				wrap.setSaleJan(monthMoney.get(0));
				wrap.setSaleFeb(monthMoney.get(1));
				wrap.setSaleMar(monthMoney.get(2));
				wrap.setSaleApr(monthMoney.get(3));
				wrap.setSaleMay(monthMoney.get(4));
				wrap.setSaleJun(monthMoney.get(5));
				wrap.setSaleJul(monthMoney.get(6));
				wrap.setSaleAug(monthMoney.get(7));
				wrap.setSaleSep(monthMoney.get(8));
				wrap.setSaleNov(monthMoney.get(9));
				wrap.setSaleOct(monthMoney.get(10));
				wrap.setSaleDec(monthMoney.get(11));
				
				tempList.add(wrap);
			}
			
			String title = "";
			
			//类别
			if(type==1) {
				SpecialtyCategory specialtyCategory1 = specialtyCategoryService.find(value);
				if(specialtyCategory1.getName()!=null) {
					title += specialtyCategory1.getName();
				}
			}
			//产品id
			if(type==2) {
				//特产类别实体
				Specialty specialty = specialtyService.find(value);
				if(specialty.getName()!=null) {
					title += specialty.getName();
				}
			}
			
			
			baseController.export2Excel(request, response, tempList, "年营业额对比表.xls", title + "年营业额对比表", "AnnualSalesVolumeContrast.xml");
			json.setSuccess(true);
			json.setMsg("导出excel成功");
			json.setObj(null);
			
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("导出excel失败");
			json.setObj(null);
			e.printStackTrace();
		}
		//return null;
		
	}
	
	
	public static class Wrap{
		BigDecimal saleJan = new BigDecimal(0);
		BigDecimal saleFeb = new BigDecimal(0);
		BigDecimal saleMar = new BigDecimal(0);
		BigDecimal saleApr = new BigDecimal(0);
		BigDecimal saleMay = new BigDecimal(0);
		BigDecimal saleJun = new BigDecimal(0);
		BigDecimal saleJul = new BigDecimal(0);
		BigDecimal saleAug = new BigDecimal(0);
		BigDecimal saleSep = new BigDecimal(0);
		BigDecimal saleOct = new BigDecimal(0);
		BigDecimal saleNov = new BigDecimal(0);
		BigDecimal saleDec = new BigDecimal(0);
		Integer saleTime = new Integer(0);
		public Integer getSaleTime() {
			return saleTime;
		}
		public void setSaleTime(Integer saleTime) {
			this.saleTime = saleTime;
		}
		public BigDecimal getSaleJan() {
			return saleJan;
		}
		public void setSaleJan(BigDecimal saleJan) {
			this.saleJan = saleJan;
		}
		public BigDecimal getSaleFeb() {
			return saleFeb;
		}
		public void setSaleFeb(BigDecimal saleFeb) {
			this.saleFeb = saleFeb;
		}
		public BigDecimal getSaleMar() {
			return saleMar;
		}
		public void setSaleMar(BigDecimal saleMar) {
			this.saleMar = saleMar;
		}
		public BigDecimal getSaleApr() {
			return saleApr;
		}
		public void setSaleApr(BigDecimal saleApr) {
			this.saleApr = saleApr;
		}
		public BigDecimal getSaleMay() {
			return saleMay;
		}
		public void setSaleMay(BigDecimal saleMay) {
			this.saleMay = saleMay;
		}
		public BigDecimal getSaleJun() {
			return saleJun;
		}
		public void setSaleJun(BigDecimal saleJun) {
			this.saleJun = saleJun;
		}
		public BigDecimal getSaleJul() {
			return saleJul;
		}
		public void setSaleJul(BigDecimal saleJul) {
			this.saleJul = saleJul;
		}
		public BigDecimal getSaleAug() {
			return saleAug;
		}
		public void setSaleAug(BigDecimal saleAug) {
			this.saleAug = saleAug;
		}
		public BigDecimal getSaleSep() {
			return saleSep;
		}
		public void setSaleSep(BigDecimal saleSep) {
			this.saleSep = saleSep;
		}
		public BigDecimal getSaleOct() {
			return saleOct;
		}
		public void setSaleOct(BigDecimal saleOct) {
			this.saleOct = saleOct;
		}
		public BigDecimal getSaleNov() {
			return saleNov;
		}
		public void setSaleNov(BigDecimal saleNov) {
			this.saleNov = saleNov;
		}
		public BigDecimal getSaleDec() {
			return saleDec;
		}
		public void setSaleDec(BigDecimal saleDec) {
			this.saleDec = saleDec;
		}
		
		
		
	}
	
}
