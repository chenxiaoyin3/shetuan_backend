package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.HyBusinessPV;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.HyBusinessPVService;
import com.hongyu.service.SpecialtyCategoryService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.Filter.Operator;
import com.hongyu.controller.PurchaseController.ProviderOrder;
import com.hongyu.util.DateUtil;

import static com.hongyu.util.Constants.BUSINESS_ORDER_STATUS_FINISH_REFUND;
import static com.hongyu.util.Constants.BUSINESS_ORDER_STATUS_FINISH;

/**特产销量分析报表*/
@Controller
@RequestMapping("/admin/business/specialty_turnover")
public class SpecialtyTurnoverController {
	@Resource(name="businessOrderServiceImpl")
	private BusinessOrderService businessOrderService;
	
	@Resource(name="businessOrderItemServiceImpl")
	private BusinessOrderItemService businessOrderItemService;
	
	@Resource(name="specialtyServiceImpl")
	private SpecialtyService specialtyService;
	
	@Resource(name="specialtyCategoryServiceImpl")
	private SpecialtyCategoryService specialtyCategoryService;
	
	@Resource(name="hyBusinessPVServiceImpl")
	HyBusinessPVService hyBusinessPVService;
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable,@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endTime,Integer type,Long value)
	{
		Json json=new Json();
		try {
		    if(type==0) {
		    	//查特产表获取所有特产
		    	List<Map<String, Object>> list = new ArrayList<>();
		    	List<Specialty> specialtys=specialtyService.findAll();
		    	Set<BusinessOrder> orderSet=new HashSet<>();
		    	List<Filter> orderFilter=new ArrayList<Filter>();
		    	orderFilter.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_FINISH)); //筛选已完成订单
		    	if (startTime != null && !startTime.equals("")) {
		    		orderFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
			    }
			    if (endTime != null && !endTime.equals("")) {
			    	orderFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
			    }	
		    	List<BusinessOrder> businessOrders=businessOrderService.findList(null,orderFilter,null);
		    	orderFilter.clear();
		    	orderFilter.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_FINISH_REFUND)); //筛选已退款订单
		    	if (startTime != null && !startTime.equals("")) {
		    		orderFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
			    }
			    if (endTime != null && !endTime.equals("")) {
			    	orderFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
			    }	
		    	List<BusinessOrder> busiOrders=businessOrderService.findList(null,orderFilter,null);
		    	orderSet.addAll(businessOrders);
		    	orderSet.addAll(busiOrders);
		    	List<BusinessOrder> orderList=new ArrayList<>(orderSet);
		    	for(Specialty specialty:specialtys) {
		    		List<Filter> filters=new ArrayList<Filter>();
		    		filters.add(Filter.eq("itemId", specialty.getId()));
	    			filters.add(Filter.eq("clickType", 1)); //只查找clickType为1的
	    			if (startTime != null && !startTime.equals("")) {
					    filters.add(new Filter("clickTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				    }
				    if (endTime != null && !endTime.equals("")) {
					    filters.add(new Filter("clickTime", Operator.le, DateUtil.getEndOfDay(endTime)));
				    }		
//				    List<HyBusinessPV> pvList=hyBusinessPVService.findList(null,filters,null);
				    Filter[] pvFilter=new Filter[filters.size()];
				    filters.toArray(pvFilter);
				    filters.clear();
				    long pvCount=hyBusinessPVService.count(pvFilter);				    
				    if(pvCount>0) {
				    	HashMap<String,Object> map=new HashMap<String,Object>();
			    		map.put("specialtyId",specialty.getId());
			    		map.put("specialtyName", specialty.getName());
			    		map.put("visitCount",pvCount); //访客量			
			    		if(orderList.isEmpty()) { //如果没有符合条件的订单
			    			map.put("saleNumber",0); //销量
			    			map.put("saleAmount",0); //销售额
			    			map.put("refundRate",0); //退货率
			    			map.put("visitBuyRate",0); //访购率
			    		}
			    		else {
			    			filters.add(Filter.eq("specialty", specialty.getId()));
				    		filters.add(Filter.eq("type", 0));
			    			filters.add(Filter.in("businessOrder", orderList));
			    			List<BusinessOrderItem> orderItems=businessOrderItemService.findList(null,filters,null);
			    			if(orderItems.isEmpty()) {
			    				map.put("saleNumber",0); //销量
				    			map.put("saleAmount",0); //销售额
				    			map.put("refundRate",0); //退货率
				    			map.put("visitBuyRate",0); //访购率
			    			}
			    			else {
			    				Integer saleNumber=0,returnQuantity=0;
			    				BigDecimal saleAmount=new BigDecimal(0);
			    				for(BusinessOrderItem businessOrderItem:orderItems) {
			    					if(businessOrderItem.getQuantity()!=null) {
			    						saleNumber=saleNumber+businessOrderItem.getQuantity(); //将每一个条目的销量相加
			    						//将每一个条目的退货数量相加
				    					if(businessOrderItem.getReturnQuantity()!=null) {
				    						returnQuantity=returnQuantity+businessOrderItem.getReturnQuantity();
				    						BigDecimal realNumber=new BigDecimal(businessOrderItem.getQuantity()-businessOrderItem.getReturnQuantity());
				    		    			if(businessOrderItem.getSalePrice()!=null) {
				    		    				saleAmount=saleAmount.add(businessOrderItem.getSalePrice().multiply(realNumber));
				    		    			}
				    					}
			    					}			    					
			    				}
			    				map.put("saleNumber",saleNumber);
			    				map.put("saleAmount",saleAmount);
			    				BigDecimal bSaleNumber=new BigDecimal(saleNumber);
			    				BigDecimal bReturnQuantity=new BigDecimal(returnQuantity);
			    				if(saleNumber==0) {
			    					map.put("refundRate",0);
			    				}
			    				else {
			    					BigDecimal refundRate=bReturnQuantity.divide(bSaleNumber,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
				    				map.put("refundRate",refundRate);
			    				}
			    				BigDecimal bVisitCount=new BigDecimal(pvCount);
			    				//算出访购率,销量/访客量,结果以百分数表示
			    				BigDecimal visitBuyRate=bSaleNumber.divide(bVisitCount,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
			    				map.put("visitBuyRate",visitBuyRate);		    							    								    				   				
			    			}
			    		}
			    		list.add(map);	
				    } 				    					    	        							    		    				    		    		
		    	}
		    	Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);
		    	json.setMsg("查询成功");
				json.setSuccess(true);
				json.setObj(page);
		    }	
		    /*type=1时,value为传入的特产类别id*/
		    else if(type==1) {
		    	if(value != null) {
		    		List<Filter> spefilters=new ArrayList<Filter>();
		    		SpecialtyCategory specialtyCategory=specialtyCategoryService.find(value);
		    		List<SpecialtyCategory> childSpecialtyCategorys=specialtyCategory.getChildSpecialtyCategory();
		    		childSpecialtyCategorys.add(specialtyCategory);
		    		spefilters.add(Filter.in("category", childSpecialtyCategorys));
		    		List<Specialty> specialtys=specialtyService.findList(null,spefilters,null);
		    		if(specialtys.isEmpty()) {
		    			json.setMsg("查询成功");
						json.setSuccess(true);
						json.setObj(new Page<Specialty>());
		    		}
		    		else {
		    			List<Map<String, Object>> list = new ArrayList<>();
			    		Set<BusinessOrder> orderSet=new HashSet<>();
				    	List<Filter> orderFilter=new ArrayList<Filter>();
				    	orderFilter.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_FINISH)); //筛选已完成订单
				    	if (startTime != null && !startTime.equals("")) {
				    		orderFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
					    }
					    if (endTime != null && !endTime.equals("")) {
					    	orderFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
					    }	
				    	List<BusinessOrder> businessOrders=businessOrderService.findList(null,orderFilter,null);
				    	orderFilter.clear();
				    	orderFilter.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_FINISH_REFUND)); //筛选已退款订单
				    	if (startTime != null && !startTime.equals("")) {
				    		orderFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
					    }
					    if (endTime != null && !endTime.equals("")) {
					    	orderFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
					    }	
				    	List<BusinessOrder> busiOrders=businessOrderService.findList(null,orderFilter,null);
				    	orderSet.addAll(businessOrders);
				    	orderSet.addAll(busiOrders);
				    	List<BusinessOrder> orderList=new ArrayList<>(orderSet);
				    	for(Specialty specialty:specialtys) {
				    		List<Filter> filters=new ArrayList<Filter>();
				    		filters.add(Filter.eq("itemId", specialty.getId()));
			    			filters.add(Filter.eq("clickType", 1)); //只查找clickType为0的
			    			if (startTime != null && !startTime.equals("")) {
							    filters.add(new Filter("clickTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
						    }
						    if (endTime != null && !endTime.equals("")) {
							    filters.add(new Filter("clickTime", Operator.le, DateUtil.getEndOfDay(endTime)));
						    }		
//						    List<HyBusinessPV> pvList=hyBusinessPVService.findList(null,filters,null);
						    Filter[] pvFilter=new Filter[filters.size()];
						    filters.toArray(pvFilter);
						    filters.clear();
						    long pvCount=hyBusinessPVService.count(pvFilter);
						    if(pvCount>0) {
						    	HashMap<String,Object> map=new HashMap<String,Object>();
					    		map.put("specialtyId",specialty.getId());
					    		map.put("specialtyName", specialty.getName());
							    map.put("visitCount",pvCount); //访客量				        							    		    		
					    		if(orderList.isEmpty()) { //如果没有符合条件的订单
					    			map.put("saleNumber",0); //销量
					    			map.put("saleAmount",0); //销售额
					    			map.put("refundRate",0); //退货率
					    			map.put("visitBuyRate",0); //访购率
					    		}
					    		else {
					    			filters.add(Filter.eq("specialty", specialty.getId()));
						    		filters.add(Filter.eq("type", 0));
					    			filters.add(Filter.in("businessOrder", orderList));
					    			List<BusinessOrderItem> orderItems=businessOrderItemService.findList(null,filters,null);
					    			if(orderItems.isEmpty()) {
					    				map.put("saleNumber",0); //销量
						    			map.put("saleAmount",0); //销售额
						    			map.put("refundRate",0); //退货率
						    			map.put("visitBuyRate",0); //访购率
					    			}
					    			else {
					    				Integer saleNumber=0,returnQuantity=0;
					    				BigDecimal saleAmount=new BigDecimal(0);
					    				for(BusinessOrderItem businessOrderItem:orderItems) {
					    					if(businessOrderItem.getQuantity()!=null) {
					    						saleNumber=saleNumber+businessOrderItem.getQuantity(); //将每一个条目的销量相加
					    						//将每一个条目的退货数量相加
						    					if(businessOrderItem.getReturnQuantity()!=null) {
						    						returnQuantity=returnQuantity+businessOrderItem.getReturnQuantity();
						    						BigDecimal realNumber=new BigDecimal(businessOrderItem.getQuantity()-businessOrderItem.getReturnQuantity());
						    		    			if(businessOrderItem.getSalePrice()!=null) {
						    		    				saleAmount=saleAmount.add(businessOrderItem.getSalePrice().multiply(realNumber));
						    		    			}
						    					}
					    					}			    					
					    				}
					    				map.put("saleNumber",saleNumber);
					    				map.put("saleAmount",saleAmount);
					    				BigDecimal bSaleNumber=new BigDecimal(saleNumber);
					    				BigDecimal bReturnQuantity=new BigDecimal(returnQuantity);
					    				if(saleNumber==0) {
					    					map.put("refundRate",0);
					    				}
					    				else {
					    					BigDecimal refundRate=bReturnQuantity.divide(bSaleNumber,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
						    				map.put("refundRate",refundRate);
					    				}
					    				if(pvCount==0) {
					    					map.put("visitBuyRate",0);
					    				}
					    				else {
					    					BigDecimal bVisitCount=new BigDecimal(pvCount);
						    				//算出访购率,销量/访客量,结果以百分数表示
						    				BigDecimal visitBuyRate=bSaleNumber.divide(bVisitCount,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
						    				map.put("visitBuyRate",visitBuyRate);
					    				}    				
					    			}
					    		}
					    		list.add(map);	    	
						    }			    			
				    	}
				    	Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);
				    	json.setMsg("查询成功");
						json.setSuccess(true);
						json.setObj(page);
		    		}		    		
		    	}
		    }
		    /*type=2时,传入特产id*/
		    else if(type==2) {
		    	if(value!=null) {
		    		List<Map<String, Object>> list = new ArrayList<>();
		    		Set<BusinessOrder> orderSet=new HashSet<>();
			    	List<Filter> orderFilter=new ArrayList<Filter>();
			    	orderFilter.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_FINISH)); //筛选已完成订单
			    	if (startTime != null && !startTime.equals("")) {
			    		orderFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				    }
				    if (endTime != null && !endTime.equals("")) {
				    	orderFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
				    }	
			    	List<BusinessOrder> businessOrders=businessOrderService.findList(null,orderFilter,null);
			    	orderFilter.clear();
			    	orderFilter.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_FINISH_REFUND)); //筛选已退款订单
			    	if (startTime != null && !startTime.equals("")) {
			    		orderFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				    }
				    if (endTime != null && !endTime.equals("")) {
				    	orderFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
				    }	
			    	List<BusinessOrder> busiOrders=businessOrderService.findList(null,orderFilter,null);
			    	orderSet.addAll(businessOrders);
			    	orderSet.addAll(busiOrders);
			    	List<BusinessOrder> orderList=new ArrayList<>(orderSet);
		    		Specialty specialty=specialtyService.find(value);
		    		List<Filter> filters=new ArrayList<Filter>();
		    		filters.add(Filter.eq("itemId", value));
	    			filters.add(Filter.eq("clickType", 1)); //只查找clickType为1的
	    			if (startTime != null && !startTime.equals("")) {
					    filters.add(new Filter("clickTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				    }
				    if (endTime != null && !endTime.equals("")) {
					    filters.add(new Filter("clickTime", Operator.le, DateUtil.getEndOfDay(endTime)));
				    }		
//				    List<HyBusinessPV> pvList=hyBusinessPVService.findList(null,filters,null);
				    Filter[] pvFilter=new Filter[filters.size()];
				    filters.toArray(pvFilter);
				    filters.clear();
				    long pvCount=hyBusinessPVService.count(pvFilter);
				    if(pvCount>0) {
				    	HashMap<String,Object> map=new HashMap<String,Object>();
			    		map.put("specialtyId",value);
			    		map.put("specialtyName", specialty.getName());		    		
					    map.put("visitCount",pvCount); //访客量				        							    		    		
			    		if(orderList.isEmpty()) { //如果没有符合条件的订单
			    			map.put("saleNumber",0); //销量
			    			map.put("saleAmount",0); //销售额
			    			map.put("refundRate",0); //退货率
			    			map.put("visitBuyRate",0); //访购率
			    		}
			    		else {
			    			filters.add(Filter.eq("specialty", specialty.getId()));
				    		filters.add(Filter.eq("type", 0));
			    			filters.add(Filter.in("businessOrder", orderList));
			    			List<BusinessOrderItem> orderItems=businessOrderItemService.findList(null,filters,null);
			    			if(orderItems.isEmpty()) {
			    				map.put("saleNumber",0); //销量
				    			map.put("saleAmount",0); //销售额
				    			map.put("refundRate",0); //退货率
				    			map.put("visitBuyRate",0); //访购率
			    			}
			    			else {
			    				Integer saleNumber=0,returnQuantity=0;
			    				BigDecimal saleAmount=new BigDecimal(0);
			    				for(BusinessOrderItem businessOrderItem:orderItems) {
			    					if(businessOrderItem.getQuantity()!=null) {
			    						saleNumber=saleNumber+businessOrderItem.getQuantity(); //将每一个条目的销量相加
			    						//将每一个条目的退货数量相加
				    					if(businessOrderItem.getReturnQuantity()!=null) {
				    						returnQuantity=returnQuantity+businessOrderItem.getReturnQuantity();
				    						BigDecimal realNumber=new BigDecimal(businessOrderItem.getQuantity()-businessOrderItem.getReturnQuantity());
				    		    			if(businessOrderItem.getSalePrice()!=null) {
				    		    				saleAmount=saleAmount.add(businessOrderItem.getSalePrice().multiply(realNumber));
				    		    			}
				    					}
			    					}			    					
			    				}
			    				map.put("saleNumber",saleNumber);
			    				map.put("saleAmount",saleAmount);
			    				BigDecimal bSaleNumber=new BigDecimal(saleNumber);
			    				BigDecimal bReturnQuantity=new BigDecimal(returnQuantity);
			    				if(saleNumber==0) {
			    					map.put("refundRate",0);
			    				}
			    				else {
			    					BigDecimal refundRate=bReturnQuantity.divide(bSaleNumber,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
				    				map.put("refundRate",refundRate);
			    				}
			    				if(pvCount==0) {
			    					map.put("visitBuyRate",0);
			    				}
			    				else {
			    					BigDecimal bVisitCount=new BigDecimal(pvCount);
				    				//算出访购率,销量/访客量,结果以百分数表示
				    				BigDecimal visitBuyRate=bSaleNumber.divide(bVisitCount,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
				    				map.put("visitBuyRate",visitBuyRate);
			    				}    				
			    			}
			    		}	
			    		list.add(map);
				    }	    		
		    		Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);
		    		json.setMsg("查询成功");
					json.setSuccess(true);
					json.setObj(page);
		    	}    	
		    }
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	public class SpecialtySaleAnalysis{
		private String specialtyName; //特产名称
		private Integer saleNumber; //销量
		private BigDecimal saleAmount; //销售额
		private BigDecimal refundRate; //退货率
		private Integer visitCount; //访客量
		private BigDecimal visitBuyRate;//访购率
		public String getSpecialtyName() {
			return specialtyName;
		}
		public void setSpecialtyName(String specialtyName) {
			this.specialtyName = specialtyName;
		}
		public Integer getSaleNumber() {
			return saleNumber;
		}
		public void setSaleNumber(Integer saleNumber) {
			this.saleNumber = saleNumber;
		}
		public BigDecimal getSaleAmount() {
			return saleAmount;
		}
		public void setSaleAmount(BigDecimal saleAmount) {
			this.saleAmount = saleAmount;
		}
		public BigDecimal getRefundRate() {
			return refundRate;
		}
		public void setRefundRate(BigDecimal refundRate) {
			this.refundRate = refundRate;
		}
		public Integer getVisitCount() {
			return visitCount;
		}
		public void setVisitCount(Integer visitCount) {
			this.visitCount = visitCount;
		}
		public BigDecimal getVisitBuyRate() {
			return visitBuyRate;
		}
		public void setVisitBuyRate(BigDecimal visitBuyRate) {
			this.visitBuyRate = visitBuyRate;
		}
	}
	/*导出特产销量分析的excel*/
	@RequestMapping(value="specialtySale/analysisExcel")
	public String specialtySaleExcel(@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date startTime, @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date endTime,
			Integer type,Long value,HttpServletRequest request, HttpServletResponse response)
	{
		try {
		    if(type==0) {
		    	//查特产表获取所有特产
		    	List<Specialty> specialtys=specialtyService.findAll();
		    	Set<BusinessOrder> orderSet=new HashSet<>();
		    	List<Filter> orderFilter=new ArrayList<Filter>();
		    	orderFilter.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_FINISH)); //筛选已完成订单
		    	if (startTime != null && !startTime.equals("")) {
		    		orderFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
			    }
			    if (endTime != null && !endTime.equals("")) {
			    	orderFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
			    }	
		    	List<BusinessOrder> businessOrders=businessOrderService.findList(null,orderFilter,null);
		    	orderFilter.clear();
		    	orderFilter.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_FINISH_REFUND)); //筛选已退款订单
		    	if (startTime != null && !startTime.equals("")) {
		    		orderFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
			    }
			    if (endTime != null && !endTime.equals("")) {
			    	orderFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
			    }	
		    	List<BusinessOrder> busiOrders=businessOrderService.findList(null,orderFilter,null);
		    	orderSet.addAll(businessOrders);
		    	orderSet.addAll(busiOrders);
		    	List<BusinessOrder> orderList=new ArrayList<>(orderSet);
		    	List<SpecialtySaleAnalysis> results = new ArrayList<SpecialtySaleAnalysis>();
		    	for(Specialty specialty:specialtys) {
		    		List<Filter> filters=new ArrayList<Filter>();
		    		filters.add(Filter.eq("itemId", specialty.getId()));
	    			filters.add(Filter.eq("clickType", 1)); //只查找clickType为1的
	    			if (startTime != null && !startTime.equals("")) {
					    filters.add(new Filter("clickTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				    }
				    if (endTime != null && !endTime.equals("")) {
					    filters.add(new Filter("clickTime", Operator.le, DateUtil.getEndOfDay(endTime)));
				    }		
//				    List<HyBusinessPV> pvList=hyBusinessPVService.findList(null,filters,null);
				    Filter[] pvFilter=new Filter[filters.size()];
				    filters.toArray(pvFilter);
				    filters.clear();
				    long pvCount=hyBusinessPVService.count(pvFilter);
				    String str=new Long(pvCount).toString();
				    Integer count=Integer.valueOf(str);
				    if(count>0) {
				    	SpecialtySaleAnalysis map=new SpecialtySaleAnalysis();
			    		map.setSpecialtyName(specialty.getName());		    		
					    map.setVisitCount(count);; //访客量				        							    		    		
			    		if(orderList.isEmpty()) { //如果没有符合条件的订单
							map.setRefundRate(new BigDecimal(0));
							map.setSaleNumber(0);
							map.setSaleAmount(new BigDecimal(0));
							map.setVisitBuyRate(new BigDecimal(0));
			    		}
			    		else {
			    			filters.add(Filter.eq("specialty", specialty.getId()));
				    		filters.add(Filter.eq("type", 0));
			    			filters.add(Filter.in("businessOrder", orderList));
			    			List<BusinessOrderItem> orderItems=businessOrderItemService.findList(null,filters,null);
			    			if(orderItems.isEmpty()) {
			    				map.setRefundRate(new BigDecimal(0));
								map.setSaleNumber(0);
								map.setSaleAmount(new BigDecimal(0));
								map.setVisitBuyRate(new BigDecimal(0));
			    			}
			    			else {
			    				Integer saleNumber=0,returnQuantity=0;
			    				BigDecimal saleAmount=new BigDecimal(0);
			    				for(BusinessOrderItem businessOrderItem:orderItems) {
			    					if(businessOrderItem.getQuantity()!=null) {
			    						saleNumber=saleNumber+businessOrderItem.getQuantity(); //将每一个条目的销量相加
			    						//将每一个条目的退货数量相加
				    					if(businessOrderItem.getReturnQuantity()!=null) {
				    						returnQuantity=returnQuantity+businessOrderItem.getReturnQuantity();
				    						BigDecimal realNumber=new BigDecimal(businessOrderItem.getQuantity()-businessOrderItem.getReturnQuantity());
				    		    			if(businessOrderItem.getSalePrice()!=null) {
				    		    				saleAmount=saleAmount.add(businessOrderItem.getSalePrice().multiply(realNumber));
				    		    			}
				    					}
			    					}			    					
			    				}
			    				map.setSaleNumber(saleNumber);
			    				map.setSaleAmount(saleAmount);
			    				BigDecimal bSaleNumber=new BigDecimal(saleNumber);
			    				BigDecimal bReturnQuantity=new BigDecimal(returnQuantity);
			    				if(saleNumber==0) {
			    					map.setRefundRate(new BigDecimal(0));
			    				}
			    				else {
			    					BigDecimal refundRate=bReturnQuantity.divide(bSaleNumber,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
				    				map.setRefundRate(refundRate);
			    				}
			    				if(pvCount==0) {
			    					map.setVisitBuyRate(new BigDecimal(0));
			    				}
			    				else {
			    					BigDecimal bVisitCount=new BigDecimal(pvCount);
				    				//算出访购率,销量/访客量,结果以百分数表示
				    				BigDecimal visitBuyRate=bSaleNumber.divide(bVisitCount,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
				    				map.setVisitBuyRate(visitBuyRate);
			    				}    				
			    			}
			    		}
			    		results.add(map);	  
				    }  		
		    	}
		    	// 生成Excel表标题
				StringBuffer sb2 = new StringBuffer();
				sb2.append("特产销售分析");
				String fileName = "特产销售分析报表.xls";  // Excel文件名
				String tableTitle = sb2.toString();   // Excel表标题
				String configFile = "specialtySaleAnalysis.xml"; // 配置文件
				com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
				excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
		    }	
		    /*type=1时,value为传入的特产类别id*/
		    else if(type==1) {
		    	if(value != null) {
		    		List<Filter> spefilters=new ArrayList<Filter>();
		    		SpecialtyCategory specialtyCategory=specialtyCategoryService.find(value);
		    		List<SpecialtyCategory> childSpecialtyCategorys=specialtyCategory.getChildSpecialtyCategory();
		    		childSpecialtyCategorys.add(specialtyCategory);
		    		spefilters.add(Filter.in("category", childSpecialtyCategorys));
		    		List<Specialty> specialtys=specialtyService.findList(null,spefilters,null);
		    		if(specialtys.isEmpty()) {
		    			List<SpecialtySaleAnalysis> results = new ArrayList<SpecialtySaleAnalysis>();
		    			// 生成Excel表标题
						StringBuffer sb2 = new StringBuffer();
						sb2.append("特产销售分析");
						String fileName = "特产销售分析报表.xls";  // Excel文件名
						String tableTitle = sb2.toString();   // Excel表标题
						String configFile = "specialtySaleAnalysis.xml"; // 配置文件
						com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
						excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
		    		}
		    		else {
			    		Set<BusinessOrder> orderSet=new HashSet<>();
				    	List<Filter> orderFilter=new ArrayList<Filter>();
				    	orderFilter.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_FINISH)); //筛选已完成订单
				    	if (startTime != null && !startTime.equals("")) {
				    		orderFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
					    }
					    if (endTime != null && !endTime.equals("")) {
					    	orderFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
					    }	
				    	List<BusinessOrder> businessOrders=businessOrderService.findList(null,orderFilter,null);
				    	orderFilter.clear();
				    	orderFilter.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_FINISH_REFUND)); //筛选已退款订单
				    	if (startTime != null && !startTime.equals("")) {
				    		orderFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
					    }
					    if (endTime != null && !endTime.equals("")) {
					    	orderFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
					    }	
				    	List<BusinessOrder> busiOrders=businessOrderService.findList(null,orderFilter,null);
				    	orderSet.addAll(businessOrders);
				    	orderSet.addAll(busiOrders);
				    	List<BusinessOrder> orderList=new ArrayList<>(orderSet);
				    	List<SpecialtySaleAnalysis> results = new ArrayList<SpecialtySaleAnalysis>();
				    	for(Specialty specialty:specialtys) {
				    		List<Filter> filters=new ArrayList<Filter>();
				    		filters.add(Filter.eq("itemId", specialty.getId()));
			    			filters.add(Filter.eq("clickType", 1)); //只查找clickType为1的
			    			if (startTime != null && !startTime.equals("")) {
							    filters.add(new Filter("clickTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
						    }
						    if (endTime != null && !endTime.equals("")) {
							    filters.add(new Filter("clickTime", Operator.le, DateUtil.getEndOfDay(endTime)));
						    }		
//						    List<HyBusinessPV> pvList=hyBusinessPVService.findList(null,filters,null);						    
						    Filter[] pvFilter=new Filter[filters.size()];
						    filters.toArray(pvFilter);
						    filters.clear();
						    long pvCount=hyBusinessPVService.count(pvFilter);
						    String str=new Long(pvCount).toString();
						    Integer count=Integer.valueOf(str);
						    if(count>0) {
						    	SpecialtySaleAnalysis map=new SpecialtySaleAnalysis();
		                        map.setSpecialtyName(specialty.getName());				    		
							    map.setVisitCount(count); //访客量				        							    		    		
					    		if(orderList.isEmpty()) { //如果没有符合条件的订单
					    			map.setRefundRate(new BigDecimal(0)); //退货率
									map.setSaleNumber(0); //销量
									map.setSaleAmount(new BigDecimal(0)); //销售额
									map.setVisitBuyRate(new BigDecimal(0)); //访购率
					    		}
					    		else {
					    			filters.add(Filter.eq("specialty", specialty.getId()));
						    		filters.add(Filter.eq("type", 0));
					    			filters.add(Filter.in("businessOrder", orderList));
					    			List<BusinessOrderItem> orderItems=businessOrderItemService.findList(null,filters,null);
					    			if(orderItems.isEmpty()) {
					    				map.setRefundRate(new BigDecimal(0)); //退货率
										map.setSaleNumber(0); //销量
										map.setSaleAmount(new BigDecimal(0)); //销售额
										map.setVisitBuyRate(new BigDecimal(0)); //访购率
					    			}
					    			else {
					    				Integer saleNumber=0,returnQuantity=0;
					    				BigDecimal saleAmount=new BigDecimal(0);
					    				for(BusinessOrderItem businessOrderItem:orderItems) {
					    					if(businessOrderItem.getQuantity()!=null) {
					    						saleNumber=saleNumber+businessOrderItem.getQuantity(); //将每一个条目的销量相加
					    						//将每一个条目的退货数量相加
						    					if(businessOrderItem.getReturnQuantity()!=null) {
						    						returnQuantity=returnQuantity+businessOrderItem.getReturnQuantity();
						    						BigDecimal realNumber=new BigDecimal(businessOrderItem.getQuantity()-businessOrderItem.getReturnQuantity());
						    		    			if(businessOrderItem.getSalePrice()!=null) {
						    		    				saleAmount=saleAmount.add(businessOrderItem.getSalePrice().multiply(realNumber));
						    		    			}
						    					}
					    					}			    					
					    				}
					    				map.setSaleNumber(saleNumber);
					    				map.setSaleAmount(saleAmount);
					    				BigDecimal bSaleNumber=new BigDecimal(saleNumber);
					    				BigDecimal bReturnQuantity=new BigDecimal(returnQuantity);
					    				if(saleNumber==0) {
					    					map.setRefundRate(new BigDecimal(0));
					    				}
					    				else {
					    					BigDecimal refundRate=bReturnQuantity.divide(bSaleNumber,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
						    				map.setRefundRate(refundRate);
					    				}
					    				if(count==0) {
					    					map.setVisitBuyRate(new BigDecimal(0));
					    				}
					    				else {
					    					BigDecimal bVisitCount=new BigDecimal(count);
						    				//算出访购率,销量/访客量,结果以百分数表示
						    				BigDecimal visitBuyRate=bSaleNumber.divide(bVisitCount,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
						    				map.setVisitBuyRate(visitBuyRate);
					    				}    				
					    			}
					    		}
					    		results.add(map);
						    }	    		
				    	}
				    	// 生成Excel表标题
						StringBuffer sb2 = new StringBuffer();
						sb2.append("特产销售分析");
						String fileName = "特产销售分析报表.xls";  // Excel文件名
						String tableTitle = sb2.toString();   // Excel表标题
						String configFile = "specialtySaleAnalysis.xml"; // 配置文件
						com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
						excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
		    		}		    		
		    	}
		    }
		    /*type=2时,传入特产id*/
		    else if(type==2) {
		    	if(value!=null) {
		    		Set<BusinessOrder> orderSet=new HashSet<>();
			    	List<Filter> orderFilter=new ArrayList<Filter>();
			    	orderFilter.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_FINISH)); //筛选已完成订单
			    	if (startTime != null && !startTime.equals("")) {
			    		orderFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				    }
				    if (endTime != null && !endTime.equals("")) {
				    	orderFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
				    }	
			    	List<BusinessOrder> businessOrders=businessOrderService.findList(null,orderFilter,null);
			    	orderFilter.clear();
			    	orderFilter.add(Filter.eq("orderState", BUSINESS_ORDER_STATUS_FINISH_REFUND)); //筛选已退款订单
			    	if (startTime != null && !startTime.equals("")) {
			    		orderFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				    }
				    if (endTime != null && !endTime.equals("")) {
				    	orderFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
				    }	
			    	List<BusinessOrder> busiOrders=businessOrderService.findList(null,orderFilter,null);
			    	orderSet.addAll(businessOrders);
			    	orderSet.addAll(busiOrders);
			    	List<BusinessOrder> orderList=new ArrayList<>(orderSet);
		    		Specialty specialty=specialtyService.find(value);
		    		List<SpecialtySaleAnalysis> results = new ArrayList<SpecialtySaleAnalysis>();
		    		List<Filter> filters=new ArrayList<Filter>();
		    		filters.add(Filter.eq("itemId", value));
	    			filters.add(Filter.eq("clickType", 1)); //只查找clickType为1的
	    			if (startTime != null && !startTime.equals("")) {
					    filters.add(new Filter("clickTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				    }
				    if (endTime != null && !endTime.equals("")) {
					    filters.add(new Filter("clickTime", Operator.le, DateUtil.getEndOfDay(endTime)));
				    }		
//				    List<HyBusinessPV> pvList=hyBusinessPVService.findList(null,filters,null);
				    Filter[] pvFilter=new Filter[filters.size()];
				    filters.toArray(pvFilter);
				    filters.clear();
				    long pvCount=hyBusinessPVService.count(pvFilter);
				    String str=new Long(pvCount).toString();
				    Integer count=Integer.valueOf(str);	
				    if(count>0) {
				    	SpecialtySaleAnalysis map=new SpecialtySaleAnalysis();
			    		map.setSpecialtyName(specialty.getName()); //特产名称		    		
					    map.setVisitCount(count); //访客量
			    		if(orderList.isEmpty()) { //如果没有符合条件的订单
			    			map.setRefundRate(new BigDecimal(0)); //退货率
							map.setSaleNumber(0); //销量
							map.setSaleAmount(new BigDecimal(0)); //销售额
							map.setVisitBuyRate(new BigDecimal(0)); //访购率
			    		}
			    		else {
			    			filters.add(Filter.eq("specialty", specialty.getId()));
				    		filters.add(Filter.eq("type", 0));
			    			filters.add(Filter.in("businessOrder", orderList));
			    			List<BusinessOrderItem> orderItems=businessOrderItemService.findList(null,filters,null);
			    			if(orderItems.isEmpty()) {
			    				map.setRefundRate(new BigDecimal(0)); //退货率
								map.setSaleNumber(0); //销量
								map.setSaleAmount(new BigDecimal(0)); //销售额
								map.setVisitBuyRate(new BigDecimal(0)); //访购率
			    			}
			    			else {
			    				Integer saleNumber=0,returnQuantity=0;
			    				BigDecimal saleAmount=new BigDecimal(0);
			    				for(BusinessOrderItem businessOrderItem:orderItems) {
			    					if(businessOrderItem.getQuantity()!=null) {
			    						saleNumber=saleNumber+businessOrderItem.getQuantity(); //将每一个条目的销量相加
			    						//将每一个条目的退货数量相加
				    					if(businessOrderItem.getReturnQuantity()!=null) {
				    						returnQuantity=returnQuantity+businessOrderItem.getReturnQuantity();
				    						BigDecimal realNumber=new BigDecimal(businessOrderItem.getQuantity()-businessOrderItem.getReturnQuantity());
				    		    			if(businessOrderItem.getSalePrice()!=null) {
				    		    				saleAmount=saleAmount.add(businessOrderItem.getSalePrice().multiply(realNumber));
				    		    			}
				    					}
			    					}			    					
			    				}
			    				map.setSaleNumber(saleNumber);
			    				map.setSaleAmount(saleAmount);
			    				BigDecimal bSaleNumber=new BigDecimal(saleNumber);
			    				BigDecimal bReturnQuantity=new BigDecimal(returnQuantity);
			    				if(saleNumber==0) {
			    					map.setRefundRate(new BigDecimal(0));
			    				}
			    				else {
			    					BigDecimal refundRate=bReturnQuantity.divide(bSaleNumber,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
				    				map.setRefundRate(refundRate);
			    				}
			    				if(count==0) {
			    					map.setVisitBuyRate(new BigDecimal(0));
			    				}
			    				else {
			    					BigDecimal bVisitCount=new BigDecimal(count);
				    				//算出访购率,销量/访客量,结果以百分数表示
				    				BigDecimal visitBuyRate=bSaleNumber.divide(bVisitCount,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
				    				map.setVisitBuyRate(visitBuyRate);
			    				}    				
			    			}
			    		}	
			    		results.add(map);
				    }		    		
		    		// 生成Excel表标题
					StringBuffer sb2 = new StringBuffer();
					sb2.append("特产销售分析");
					String fileName = "特产销售分析报表.xls";  // Excel文件名
					String tableTitle = sb2.toString();   // Excel表标题
					String configFile = "specialtySaleAnalysis.xml"; // 配置文件
					com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
					excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
		    	}    	
		    }
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}
