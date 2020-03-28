package com.hongyu.controller.lbc;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aliyun.openservices.ots.model.Row;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.entity.HyTicketScene;
import com.hongyu.entity.HyTicketSubscribe;
import com.hongyu.entity.HyUser;
import com.hongyu.entity.HyVisa;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.MhAppraise;
import com.hongyu.entity.MhAppraiseImage;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyAppraise;
import com.hongyu.entity.SpecialtyAppraiseImage;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyTicketHotelService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketSceneService;
import com.hongyu.service.HyTicketSubscribeService;
import com.hongyu.service.HyUserService;
import com.hongyu.service.HyVisaService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.MhAppraiseService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.util.ArrayHandler;
import com.hongyu.util.DateUtil;



@Controller
@RequestMapping("/admin/business/mhAppraiseManagement/")
public class MhAppraiseManagementController {
	@Resource(name = "mhAppraiseServiceImpl")
	MhAppraiseService mhAppraiseService;

	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;
	
	@Resource(name = "hyOrderItemServiceImpl")
	HyOrderItemService hyOrderItemService;
	
	@Resource(name = "specialtyServiceImpl")
	SpecialtyService specialtyService;
	
	@Resource(name = "hyUserServiceImpl")
	HyUserService hyUserService;
	
	//保险
	@Resource(name = "insuranceServiceImpl")
	InsuranceService insuranceService;
	
	//签证
	@Resource(name = "hyVisaServiceImpl")
	HyVisaService hyVisaService;

	//认购门票
	@Resource(name = "hyTicketSubscribeServiceImpl")
	HyTicketSubscribeService hyTicketSubscribeService;
	
	//线路
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	//酒店
	@Resource(name = "hyTicketHotelServiceImpl")
	HyTicketHotelService hyTicketHotelService;
	
	//酒加景
	@Resource(name = "hyTicketHotelandsceneServiceImpl")
	HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	//门票
	@Resource(name = "hyTicketSceneServiceImpl")
	HyTicketSceneService hyTicketSceneService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyServiceImpl;
	
	
	static final int APPRAISE_SORT_BY_ID = 0;
    static final int APPRAISE_SORT_BY_CONTENT_LEVEL = 1;
    static final int APPRAISE_SORT_BY_TIME = 2;
    
    public static Date getEndOfDay(Date date) {
        return DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), -1);
    }

    public static Date getStartOfDay(Date date) {
        return DateUtils.truncate(date, Calendar.DATE);
    }
    
    @RequestMapping("appraise/page/view")
    @ResponseBody
    public Json mhAppraisePage(Long specialtyid, Boolean isSpecialty, Integer type, String userName, @DateTimeFormat(pattern="yyyy-MM-dd") Date startdate, @DateTimeFormat(pattern="yyyy-MM-dd") Date enddate, Integer sorttype, Pageable pageable) {
    	Json json = new Json();
    	
    	try {
    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    		String startdatestr = "";
    		String enddatestr = "";
    		if(startdate!=null) {
    			startdatestr = formatter.format(startdate);
    		}
    	    if(enddate!=null) {
    	    	enddatestr = formatter.format(enddate); 
    	    }
    	    
       		//是特产
    		if(isSpecialty == true) {
    			Specialty s = specialtyServiceImpl.find(specialtyid);
        		if (s == null) {
        			json.setSuccess(false);
        			json.setMsg("指定特产不存在");
        			json.setObj(null);
        			return json;
        		}
        		String[] attrs = new String[]{
    					"id","appraiseTime","appraiseContent","appraiser","contentLevel","isShow","isValid","isAnonymous","updateDate"
    			};
        		//"id","appraiseTime","appraiseContent","appraiser","contentLevel","isShow","isValid","isAnonymous","updateDate"
        		//特产
        		StringBuilder totalSb = new StringBuilder("select count(*)");
    			StringBuilder pageSb = new StringBuilder("select appraise.id, appraise.appraise_time, appraise.appraise_content, appraise.appraiser_id, appraise.content_level, appraise.is_show, appraise.is_valid, appraise.is_anonymous, appraise.update_date");
    			StringBuilder sb = new StringBuilder(" from mh_appraise appraise, hy_business_order_item orderitem, hy_portal_user hyuser"
    					+ " where appraise.order_item_id = orderitem.ID and appraise.appraiser_id = hyuser.id and appraise.order_item_type = 1 and orderitem.specialty_id = " + specialtyid);
    			
    			
    			if(userName != null){
    				sb.append(" and hyuser.phone=" + userName);
    			}
    			if(startdate!=null){
    				sb.append(" and appraise.update_date>='" + startdatestr+"'");
    			}
    			if(enddate != null) {
    				sb.append(" and appraise.update_date<='" + enddatestr+"'");
    			}
    			
    			
    			
//    			if(hyAdmins!=null && !hyAdmins.isEmpty()){
//    				List<String> adminStrArr = new ArrayList<>();
//    				for(HyAdmin hyAdmin:hyAdmins){
//    					adminStrArr.add("'"+hyAdmin.getUsername()+"'");
//    					
//    				}
//    				String adminStr = String.join(",",adminStrArr);
//    				sb.append(" and o1.operator_id in ("+adminStr+")");
//    			}
    			System.out.println("查询语句为：" + sb);

    			List totals = mhAppraiseService.statis(totalSb.append(sb).toString());
    			Integer total = ((BigInteger)totals.get(0)).intValue();
    			
    			if (sorttype != null) {
            		if (sorttype == APPRAISE_SORT_BY_ID) {
            			sb.append(" order by appraise.id asc");
                	} else if (sorttype == APPRAISE_SORT_BY_CONTENT_LEVEL) {
                		sb.append(" order by appraise.content_level desc");
                	} else if (sorttype == APPRAISE_SORT_BY_TIME) {
                		sb.append(" order by appraise.appraise_time desc");
                	}
            	}
    			
    			
    			Integer sqlStart = (pageable.getPage()-1)*pageable.getRows();
    			Integer sqlEnd = pageable.getPage()*pageable.getRows();
    			sb.append(" limit "+sqlStart+","+sqlEnd);
    			
    			//有效
    			//filters.add(Filter.eq("isValid", 1));
//    			List<Order> orders = new ArrayList<Order>();
//    			orders.add(Order.desc("createTime"));
    			
    			List<Object[]> objs = mhAppraiseService.statis(pageSb.append(sb).toString());
    			
    			List<Map<String, Object>> list = new ArrayList<>();
    			
    			
    			for(Object[] obj : objs){
    				Map<String, Object> map1 = ArrayHandler.toMap(attrs, obj);
    				list.add(map1);
    			}
    	
    			Page<Map<String, Object>> page = new Page<>(list, total, pageable);
    			json.setSuccess(true);
    			json.setMsg("查询成功");
    			json.setObj(page);
    		}
    		else {
    			
    			String[] attrs = new String[]{
    					"id","appraiseTime","appraiseContent","appraiser","contentLevel","isShow","isValid","isAnonymous","updateDate"
    			};
    			
    			StringBuilder totalSb = new StringBuilder("select count(*)");
    			StringBuilder pageSb = new StringBuilder("select appraise.id, appraise.appraise_time, appraise.appraise_content, appraise.appraiser_id, appraise.content_level, appraise.is_show, appraise.is_valid, appraise.is_anonymous, appraise.update_date"
    					+ "");
    			StringBuilder sb = new StringBuilder(" from mh_appraise appraise, hy_order_item orderitem, hy_portal_user hyuser"
    					+ " where appraise.order_item_id = orderitem.id and appraise.appraiser_id = hyuser.id and appraise.order_item_type = 2 and orderitem.product_id = " + specialtyid + "");
    			//不是特产
    			//其他所有
				//订单条目类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证,8线路其他价格
				switch (type) {
				//导游租赁
				case 0:
					
					break;
				//线路
				case 1:
					//找到团名称
					HyGroup hyGroup = hyGroupService.find(specialtyid);
					if(hyGroup == null) {
						json.setSuccess(false);
	        			json.setMsg("指定团不存在");
	        			json.setObj(null);
	        			return json;
					}
					break;
				//认购门票
				case 2:
					HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(specialtyid);
					if(hyTicketSubscribe == null) {
						json.setSuccess(false);
	        			json.setMsg("指定认购门票不存在");
	        			json.setObj(null);
	        			return json;
					}
					break;
				//酒店
				case 3:
					HyTicketHotel hyTicketHotel = hyTicketHotelService.find(specialtyid);
					if(hyTicketHotel == null) {
						json.setSuccess(false);
	        			json.setMsg("指定酒店不存在");
	        			json.setObj(null);
	        			return json;
					}
					break;
				//门票
				case 4:
					HyTicketScene hyTicketScene = hyTicketSceneService.find(specialtyid);
					if(hyTicketScene == null) {
						json.setSuccess(false);
	        			json.setMsg("指定门票不存在");
	        			json.setObj(null);
	        			return json;
					}
					break;
				//酒加景
				case 5:
					HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(specialtyid);
					if(hyTicketHotelandscene == null) {
						json.setSuccess(false);
	        			json.setMsg("酒加景不存在");
	        			json.setObj(null);
	        			return json;
					}
					break;
				//保险
				case 6:
					Insurance insurance = insuranceService.find(specialtyid);
					if(insurance == null) {
						json.setSuccess(false);
	        			json.setMsg("指定保险不存在");
	        			json.setObj(null);
	        			return json;
					}
					break;
				//签证
				case 7:
					HyVisa hyVisa = hyVisaService.find(specialtyid);
					if(hyVisa == null) {
						json.setSuccess(false);
	        			json.setMsg("指定签证不存在");
	        			json.setObj(null);
	        			return json;
					}
					break;

				default:
					break;
				}
        		
        		
        		//"id","appraiseTime","appraiseContent","appraiser","contentLevel","isShow","isValid","isAnonymous","updateDate"
        		//特产
        		
				sb.append(" and orderitem.type=" + type);
    			
    			
    			if(userName != null){
    				sb.append(" and hyuser.phone=" + userName);
    			}
    			if(startdate!=null){
    				sb.append(" and appraise.update_date>='" + startdatestr+"'");
    			}
    			if(enddate != null) {
    				sb.append(" and appraise.update_date<='" + enddatestr+"'");
    			}
    			
    			
    			
//    			if(hyAdmins!=null && !hyAdmins.isEmpty()){
//    				List<String> adminStrArr = new ArrayList<>();
//    				for(HyAdmin hyAdmin:hyAdmins){
//    					adminStrArr.add("'"+hyAdmin.getUsername()+"'");
//    					
//    				}
//    				String adminStr = String.join(",",adminStrArr);
//    				sb.append(" and o1.operator_id in ("+adminStr+")");
//    			}
    			System.out.println("查询语句为：" + sb);
    			

    			List totals = mhAppraiseService.statis(totalSb.append(sb).toString());
    			Integer total = ((BigInteger)totals.get(0)).intValue();
    			
    			if (sorttype != null) {
            		if (sorttype == APPRAISE_SORT_BY_ID) {
            			sb.append(" order by appraise.id asc");
                	} else if (sorttype == APPRAISE_SORT_BY_CONTENT_LEVEL) {
                		sb.append(" order by appraise.content_level desc");
                	} else if (sorttype == APPRAISE_SORT_BY_TIME) {
                		sb.append(" order by appraise.appraise_time desc");
                	}
            	}
    			
    			
    			Integer sqlStart = (pageable.getPage()-1)*pageable.getRows();
    			Integer sqlEnd = pageable.getPage()*pageable.getRows();
    			sb.append(" limit "+sqlStart+","+sqlEnd);
    			
    			//有效
    			//filters.add(Filter.eq("isValid", 1));
//    			List<Order> orders = new ArrayList<Order>();
//    			orders.add(Order.desc("createTime"));
    			
    			List<Object[]> objs = mhAppraiseService.statis(pageSb.append(sb).toString());
    			
    			List<Map<String, Object>> list = new ArrayList<>();
    			
    			
    			for(Object[] obj : objs){
    				Map<String, Object> map1 = ArrayHandler.toMap(attrs, obj);
    				list.add(map1);
    			}
    	
    			Page<Map<String, Object>> page = new Page<>(list, total, pageable);
    			json.setSuccess(true);
    			json.setMsg("查询成功");
    			json.setObj(page);
    		}
    		
    		
//			
//			//订单type 2 认购门票
//			
//			
//        	
//        	
//        	List<Filter> filters = new ArrayList<Filter>();
//        	if(userName!=null){
//				List<Filter>filters2=new ArrayList<>();
//				filters2.add(Filter.like("phone", userName));
//				List<HyUser> hyUsers = hyUserService.findList(null, filters2, null);
//				if(hyUsers!=null && hyUsers.size()>0){
//					filters.add(Filter.in("appraiser", hyUsers));
//				}else{
//					Page<SpecialtyAppraise> page=new Page<>(new LinkedList<SpecialtyAppraise>(),0,pageable);
//					json.setSuccess(true);
//					json.setMsg("查询成功");
//					json.setObj(page);
//					return json;
//				}
//			}
//        	if (startdate != null) {
//        		filters.add(new Filter("appraiseTime", Operator.ge, startdate));
//        	}
//        	
//        	if (enddate != null) {
//        		filters.add(new Filter("appraiseTime", Operator.le, getEndOfDay(enddate)));
//        	}
//        	
//        	List<Order> orders = new ArrayList<Order>();
//        	if (sorttype != null) {
//        		if (sorttype == APPRAISE_SORT_BY_ID) {
//            		orders.add(Order.asc("id"));
//            	} else if (sorttype == APPRAISE_SORT_BY_CONTENT_LEVEL) {
//            		orders.add(Order.desc("contentLevel"));
//            	} else if (sorttype == APPRAISE_SORT_BY_TIME) {
//            		orders.add(Order.desc("appraiseTime"));
//            	}
//        	}
//        	
//        	pageable.setFilters(filters);
//        	pageable.setOrders(orders);
//			Page<MhAppraise> page = mhAppraiseService.findPage(pageable, query);
//			
//			Map<String, Object> pageMap = new HashMap<String, Object>();
//			pageMap.put("total", page.getTotal());
//			pageMap.put("pageNumber", page.getPageNumber());
//			pageMap.put("pageSize", page.getPageSize());
//			
//			List<Map> list = new ArrayList<Map>();
//			for (MhAppraise appraise : page.getRows()) {
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("id", appraise.getId());
//				map.put("appraiseTime", appraise.getAppraiseTime());
//				map.put("appraiseContent", appraise.getAppraiseContent());
//				map.put("contentLevel", appraise.getContentLevel());
//				map.put("isShow", appraise.getIsShow());
//				map.put("isAnonymous", appraise.getIsAnonymous());
//				map.put("isValid", appraise.getIsValid());
//				map.put("deleteTime", appraise.getDeleteTime());
//				list.add(map);
//			}
//    		pageMap.put("rows", list);
//			
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(pageMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
		}
    	
    	return json;
    }
    
    //产品列表
    @RequestMapping("product/page/view")
    @ResponseBody
    public Json productToAppraisePage(String specialtyName, Boolean isSpecialty, Integer type, String userName, @DateTimeFormat(pattern="yyyy-MM-dd") Date startdate, @DateTimeFormat(pattern="yyyy-MM-dd") Date enddate, Integer sorttype, Pageable pageable) {
    	Json json = new Json();
    	
    	try {
    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    		String startdatestr = "";
    		String enddatestr = "";
    		if(startdate!=null) {
    			startdatestr = formatter.format(startdate);
    		}
    	    if(enddate!=null) {
    	    	enddatestr = formatter.format(enddate); 
    	    }
    		List<Filter> filters = new ArrayList<>();
       		//是特产
    		if(isSpecialty == true) {
    			
    			if(specialtyName != null) {
    				filters.add(Filter.like("name", specialtyName));
    			}
    			pageable.setFilters(filters);
    			List<Order> orders = new ArrayList<Order>();
    			orders.add(Order.desc("id"));
    			pageable.setOrders(orders);
    			
    			List<Map<String, Object>> list = new ArrayList<>();
    			Page<Map<String, Object>> pageResult;
    			
    			Page<Specialty> page = specialtyServiceImpl.findPage(pageable);
        		
    			for(Specialty row : page.getRows()) {
					Map<String, Object> map1 = new HashMap<>();
					map1.put("id", row.getId());
					map1.put("productName", row.getName());
					
					StringBuilder totalSb1 = new StringBuilder("select count(*)");
					StringBuilder sumSb1 = new StringBuilder("select sum(appraise.content_level)");
	    			StringBuilder sb1 = new StringBuilder(" from mh_appraise appraise, hy_business_order_item orderitem, hy_portal_user hyuser"
	    					+ " where appraise.order_item_id = orderitem.ID and appraise.appraiser_id = hyuser.id and appraise.order_item_type = 1 and orderitem.specialty_id = " + row.getId() + "");
	    			

	        		//"id","appraiseTime","appraiseContent","appraiser","contentLevel","isShow","isValid","isAnonymous","updateDate"
	        		//特产
	    			
	    			
	    			if(userName != null){
	    				sb1.append(" and hyuser.phone=" + userName);
	    			}
	    			if(startdate!=null){
	    				sb1.append(" and appraise.update_date>='" + startdatestr+"'");
	    			}
	    			if(enddate != null) {
	    				sb1.append(" and appraise.update_date<='" + enddatestr+"'");
	    			}
	    				

	    			List totals1 = mhAppraiseService.statis(totalSb1.append(sb1).toString());
	    			Integer total1 = ((BigInteger)totals1.get(0)).intValue();
	    			
	    			List sums1 = mhAppraiseService.statis(sumSb1.append(sb1).toString());
	    			Integer sum1 = ((BigInteger)totals1.get(0)).intValue();
					
					
					map1.put("appraiseCount", total1);
					if(total1 == 0) {
						map1.put("appraiseAverageScore", 0);
					}
					else {
						map1.put("appraiseAverageScore", Double.valueOf(sum1) / Double.valueOf(total1));
					}
					list.add(map1);
				}
				
				pageResult = new Page<>(list, page.getTotal(), pageable);
				
				json.setSuccess(true);
    			json.setMsg("查询成功");
    			json.setObj(pageResult);
    		}
    		else {
    			
    			
//    			List<Order> orders = new ArrayList<Order>();
//    			orders.add(Order.desc("id"));
//    			pageable.setOrders(orders);
    			List<Map<String, Object>> list = new ArrayList<>();
    			Page<Map<String, Object>> pageResult;
    			//不是特产
    			//其他所有
				//订单条目类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证,8线路其他价格
				switch (type) {
				//导游租赁
				case 0:
					
					break;
				//线路
				case 1:
					if(specialtyName != null) {
	    				filters.add(Filter.like("groupLineName", specialtyName));
	    			}
					pageable.setFilters(filters);
					//找到团名称
					Page<HyGroup> page1 = hyGroupService.findPage(pageable);
					
					
					for(HyGroup row : page1.getRows()) {
						Map<String, Object> map1 = new HashMap<>();
						map1.put("id", row.getId());
						map1.put("productName", row.getGroupLineName());
						
						StringBuilder totalSb1 = new StringBuilder("select count(*)");
						StringBuilder sumSb1 = new StringBuilder("select sum(appraise.content_level)");
		    			StringBuilder sb1 = new StringBuilder(" from mh_appraise appraise, hy_order_item orderitem, hy_portal_user hyuser"
		    					+ " where appraise.order_item_id = orderitem.id and appraise.appraiser_id = hyuser.id and appraise.order_item_type = 2 and orderitem.product_id = " + row.getId() + "");
		    			

		        		//"id","appraiseTime","appraiseContent","appraiser","contentLevel","isShow","isValid","isAnonymous","updateDate"
		        		//特产
		        		
						sb1.append(" and orderitem.type=" + type);
		    			
		    			
		    			if(userName != null){
		    				sb1.append(" and hyuser.phone=" + userName);
		    			}
		    			if(startdate!=null){
		    				sb1.append(" and appraise.update_date>='" + startdatestr+"'");
		    			}
		    			if(enddate != null) {
		    				sb1.append(" and appraise.update_date<='" + enddatestr+"'");
		    			}
		    				

		    			List totals1 = mhAppraiseService.statis(totalSb1.append(sb1).toString());
		    			Integer total1 = ((BigInteger)totals1.get(0)).intValue();
		    			
		    			List sums1 = mhAppraiseService.statis(sumSb1.append(sb1).toString());
		    			Integer sum1 = ((BigInteger)totals1.get(0)).intValue();
						
						
						map1.put("appraiseCount", total1);
						if(total1 == 0) {
							map1.put("appraiseAverageScore", 0);
						}
						else {
							map1.put("appraiseAverageScore", Double.valueOf(sum1) / Double.valueOf(total1));
						}
						list.add(map1);
					}
					
					pageResult = new Page<>(list, page1.getTotal(), pageable);
					
					json.setSuccess(true);
	    			json.setMsg("查询成功");
	    			json.setObj(pageResult);
	    			return json;
				//认购门票
	    			
	    			
				case 2:
					if(specialtyName != null) {
	    				filters.add(Filter.like("sceneName", specialtyName));
	    			}
					pageable.setFilters(filters);
					Page<HyTicketSubscribe> page2 = hyTicketSubscribeService.findPage(pageable);
					
					for(HyTicketSubscribe row : page2.getRows()) {
						Map<String, Object> map1 = new HashMap<>();
						map1.put("id", row.getId());
						map1.put("productName", row.getSceneName());
						
						StringBuilder totalSb1 = new StringBuilder("select count(*)");
						StringBuilder sumSb1 = new StringBuilder("select sum(appraise.content_level)");
		    			StringBuilder sb1 = new StringBuilder(" from mh_appraise appraise, hy_order_item orderitem, hy_portal_user hyuser"
		    					+ " where appraise.order_item_id = orderitem.id and appraise.appraiser_id = hyuser.id and appraise.order_item_type = 2 and orderitem.product_id = " + row.getId() + "");
		    			

		        		//"id","appraiseTime","appraiseContent","appraiser","contentLevel","isShow","isValid","isAnonymous","updateDate"
		        		//特产
		        		
						sb1.append(" and orderitem.type=" + type);
		    			
		    			
		    			if(userName != null){
		    				sb1.append(" and hyuser.phone=" + userName);
		    			}
		    			if(startdate!=null){
		    				sb1.append(" and appraise.update_date>='" + startdatestr+"'");
		    			}
		    			if(enddate != null) {
		    				sb1.append(" and appraise.update_date<='" + enddatestr+"'");
		    			}
		    				

		    			List totals1 = mhAppraiseService.statis(totalSb1.append(sb1).toString());
		    			Integer total1 = ((BigInteger)totals1.get(0)).intValue();
		    			
		    			List sums1 = mhAppraiseService.statis(sumSb1.append(sb1).toString());
		    			Integer sum1 = ((BigInteger)totals1.get(0)).intValue();
						
						
						map1.put("appraiseCount", total1);
						if(total1 == 0) {
							map1.put("appraiseAverageScore", 0);
						}
						else {
							map1.put("appraiseAverageScore", Double.valueOf(sum1) / Double.valueOf(total1));
						}
						list.add(map1);
					}
					
					
					
					pageResult = new Page<>(list, page2.getTotal(), pageable);
					
					json.setSuccess(true);
	    			json.setMsg("查询成功");
	    			json.setObj(pageResult);
	    			return json;
				//酒店
				case 3:
					if(specialtyName != null) {
	    				filters.add(Filter.like("hotelName", specialtyName));
	    			}
					pageable.setFilters(filters);
					Page<HyTicketHotel> page3 = hyTicketHotelService.findPage(pageable);
					
					for(HyTicketHotel row : page3.getRows()) {
						Map<String, Object> map1 = new HashMap<>();
						map1.put("id", row.getId());
						map1.put("productName", row.getHotelName());
						
						StringBuilder totalSb1 = new StringBuilder("select count(*)");
						StringBuilder sumSb1 = new StringBuilder("select sum(appraise.content_level)");
		    			StringBuilder sb1 = new StringBuilder(" from mh_appraise appraise, hy_order_item orderitem, hy_portal_user hyuser"
		    					+ " where appraise.order_item_id = orderitem.id and appraise.appraiser_id = hyuser.id and appraise.order_item_type = 2 and orderitem.product_id = " + row.getId() + "");
		    			

		        		//"id","appraiseTime","appraiseContent","appraiser","contentLevel","isShow","isValid","isAnonymous","updateDate"
		        		//特产
		        		
						sb1.append(" and orderitem.type=" + type);
		    			
		    			
		    			if(userName != null){
		    				sb1.append(" and hyuser.phone=" + userName);
		    			}
		    			if(startdate!=null){
		    				sb1.append(" and appraise.update_date>='" + startdatestr+"'");
		    			}
		    			if(enddate != null) {
		    				sb1.append(" and appraise.update_date<='" + enddatestr+"'");
		    			}
		    				

		    			List totals1 = mhAppraiseService.statis(totalSb1.append(sb1).toString());
		    			Integer total1 = ((BigInteger)totals1.get(0)).intValue();
		    			
		    			List sums1 = mhAppraiseService.statis(sumSb1.append(sb1).toString());
		    			Integer sum1 = ((BigInteger)totals1.get(0)).intValue();
						
						
						map1.put("appraiseCount", total1);
						if(total1 == 0) {
							map1.put("appraiseAverageScore", 0);
						}
						else {
							map1.put("appraiseAverageScore", Double.valueOf(sum1) / Double.valueOf(total1));
						}
						list.add(map1);
					}
					pageResult = new Page<>(list, page3.getTotal(), pageable);
					
					json.setSuccess(true);
	    			json.setMsg("查询成功");
	    			json.setObj(pageResult);
	    			return json;
				//门票
				case 4:
					if(specialtyName != null) {
	    				filters.add(Filter.like("mhSceneName", specialtyName));
	    			}
					pageable.setFilters(filters);
					Page<HyTicketScene> page4 = hyTicketSceneService.findPage(pageable);
					
					for(HyTicketScene row : page4.getRows()) {
						Map<String, Object> map1 = new HashMap<>();
						map1.put("id", row.getId());
						map1.put("productName", row.getMhSceneName());
						
						StringBuilder totalSb1 = new StringBuilder("select count(*)");
						StringBuilder sumSb1 = new StringBuilder("select sum(appraise.content_level)");
		    			StringBuilder sb1 = new StringBuilder(" from mh_appraise appraise, hy_order_item orderitem, hy_portal_user hyuser"
		    					+ " where appraise.order_item_id = orderitem.id and appraise.appraiser_id = hyuser.id and appraise.order_item_type = 2 and orderitem.product_id = " + row.getId() + "");
		    			

		        		//"id","appraiseTime","appraiseContent","appraiser","contentLevel","isShow","isValid","isAnonymous","updateDate"
		        		//特产
		        		
						sb1.append(" and orderitem.type=" + type);
		    			
		    			
		    			if(userName != null){
		    				sb1.append(" and hyuser.phone=" + userName);
		    			}
		    			if(startdate!=null){
		    				sb1.append(" and appraise.update_date>='" + startdatestr+"'");
		    			}
		    			if(enddate != null) {
		    				sb1.append(" and appraise.update_date<='" + enddatestr+"'");
		    			}
		    				

		    			List totals1 = mhAppraiseService.statis(totalSb1.append(sb1).toString());
		    			Integer total1 = ((BigInteger)totals1.get(0)).intValue();
		    			
		    			List sums1 = mhAppraiseService.statis(sumSb1.append(sb1).toString());
		    			Integer sum1 = ((BigInteger)totals1.get(0)).intValue();
						
						
						map1.put("appraiseCount", total1);
						if(total1 == 0) {
							map1.put("appraiseAverageScore", 0);
						}
						else {
							map1.put("appraiseAverageScore", Double.valueOf(sum1) / Double.valueOf(total1));
						}
						list.add(map1);
					}
					pageResult = new Page<>(list, page4.getTotal(), pageable);
					
					json.setSuccess(true);
	    			json.setMsg("查询成功");
	    			json.setObj(pageResult);
	    			return json;

				//酒加景
				case 5:
					if(specialtyName != null) {
	    				filters.add(Filter.like("productName", specialtyName));
	    			}
					pageable.setFilters(filters);
					Page<HyTicketHotelandscene> page5 = hyTicketHotelandsceneService.findPage(pageable);
					for(HyTicketHotelandscene row : page5.getRows()) {
						Map<String, Object> map1 = new HashMap<>();
						map1.put("id", row.getId());
						map1.put("productName", row.getProductName());
						
						StringBuilder totalSb1 = new StringBuilder("select count(*)");
						StringBuilder sumSb1 = new StringBuilder("select sum(appraise.content_level)");
		    			StringBuilder sb1 = new StringBuilder(" from mh_appraise appraise, hy_order_item orderitem, hy_portal_user hyuser"
		    					+ " where appraise.order_item_id = orderitem.id and appraise.appraiser_id = hyuser.id and appraise.order_item_type = 2 and orderitem.product_id = " + row.getId() + "");
		    			

		        		//"id","appraiseTime","appraiseContent","appraiser","contentLevel","isShow","isValid","isAnonymous","updateDate"
		        		//特产
		        		
						sb1.append(" and orderitem.type=" + type);
		    			
		    			
		    			if(userName != null){
		    				sb1.append(" and hyuser.phone=" + userName);
		    			}
		    			if(startdate!=null){
		    				sb1.append(" and appraise.update_date>='" + startdatestr+"'");
		    			}
		    			if(enddate != null) {
		    				sb1.append(" and appraise.update_date<='" + enddatestr+"'");
		    			}
		    				

		    			List totals1 = mhAppraiseService.statis(totalSb1.append(sb1).toString());
		    			Integer total1 = ((BigInteger)totals1.get(0)).intValue();
		    			
		    			List sums1 = mhAppraiseService.statis(sumSb1.append(sb1).toString());
		    			Integer sum1 = ((BigInteger)totals1.get(0)).intValue();
						
						
						map1.put("appraiseCount", total1);
						if(total1 == 0) {
							map1.put("appraiseAverageScore", 0);
						}
						else {
							map1.put("appraiseAverageScore", Double.valueOf(sum1) / Double.valueOf(total1));
						}
						list.add(map1);
					}
					pageResult = new Page<>(list, page5.getTotal(), pageable);
					
					json.setSuccess(true);
	    			json.setMsg("查询成功");
	    			json.setObj(pageResult);
	    			return json;
				//保险
				case 6:
					if(specialtyName != null) {
	    				filters.add(Filter.like("remark", specialtyName));
	    			}
					pageable.setFilters(filters);
					Page<Insurance> page6 = insuranceService.findPage(pageable);
					
					for(Insurance row : page6.getRows()) {
						Map<String, Object> map1 = new HashMap<>();
						map1.put("id", row.getId());
						map1.put("productName", row.getRemark());
						
						StringBuilder totalSb1 = new StringBuilder("select count(*)");
						StringBuilder sumSb1 = new StringBuilder("select sum(appraise.content_level)");
		    			StringBuilder sb1 = new StringBuilder(" from mh_appraise appraise, hy_order_item orderitem, hy_portal_user hyuser"
		    					+ " where appraise.order_item_id = orderitem.id and appraise.appraiser_id = hyuser.id and appraise.order_item_type = 2 and orderitem.product_id = " + row.getId() + "");
		    			

		        		//"id","appraiseTime","appraiseContent","appraiser","contentLevel","isShow","isValid","isAnonymous","updateDate"
		        		//特产
		        		
						sb1.append(" and orderitem.type=" + type);
		    			
		    			
		    			if(userName != null){
		    				sb1.append(" and hyuser.phone=" + userName);
		    			}
		    			if(startdate!=null){
		    				sb1.append(" and appraise.update_date>='" + startdatestr+"'");
		    			}
		    			if(enddate != null) {
		    				sb1.append(" and appraise.update_date<='" + enddatestr+"'");
		    			}
		    				

		    			List totals1 = mhAppraiseService.statis(totalSb1.append(sb1).toString());
		    			Integer total1 = ((BigInteger)totals1.get(0)).intValue();
		    			
		    			List sums1 = mhAppraiseService.statis(sumSb1.append(sb1).toString());
		    			Integer sum1 = ((BigInteger)totals1.get(0)).intValue();
						
						
						map1.put("appraiseCount", total1);
						if(total1 == 0) {
							map1.put("appraiseAverageScore", 0);
						}
						else {
							map1.put("appraiseAverageScore", Double.valueOf(sum1) / Double.valueOf(total1));
						}
						list.add(map1);
					}
					pageResult = new Page<>(list, page6.getTotal(), pageable);
					
					json.setSuccess(true);
	    			json.setMsg("查询成功");
	    			json.setObj(pageResult);
	    			return json;
				//签证
				case 7:
					if(specialtyName != null) {
	    				filters.add(Filter.like("productName", specialtyName));
	    			}
					pageable.setFilters(filters);
					Page<HyVisa> page7 = hyVisaService.findPage(pageable);
					
					for(HyVisa row : page7.getRows()) {
						Map<String, Object> map1 = new HashMap<>();
						map1.put("id", row.getId());
						map1.put("productName", row.getProductName());
						
						StringBuilder totalSb1 = new StringBuilder("select count(*)");
						StringBuilder sumSb1 = new StringBuilder("select sum(appraise.content_level)");
		    			StringBuilder sb1 = new StringBuilder(" from mh_appraise appraise, hy_order_item orderitem, hy_portal_user hyuser"
		    					+ " where appraise.order_item_id = orderitem.id and appraise.appraiser_id = hyuser.id and appraise.order_item_type = 2 and orderitem.product_id = " + row.getId() + "");
		    			

		        		//"id","appraiseTime","appraiseContent","appraiser","contentLevel","isShow","isValid","isAnonymous","updateDate"
		        		//特产
		        		
						sb1.append(" and orderitem.type=" + type);
		    			
		    			
		    			if(userName != null){
		    				sb1.append(" and hyuser.phone=" + userName);
		    			}
		    			if(startdate!=null){
		    				sb1.append(" and appraise.update_date>='" + startdatestr+"'");
		    			}
		    			if(enddate != null) {
		    				sb1.append(" and appraise.update_date<='" + enddatestr+"'");
		    			}
		    				

		    			List totals1 = mhAppraiseService.statis(totalSb1.append(sb1).toString());
		    			Integer total1 = ((BigInteger)totals1.get(0)).intValue();
		    			
		    			List sums1 = mhAppraiseService.statis(sumSb1.append(sb1).toString());
		    			Integer sum1 = ((BigInteger)totals1.get(0)).intValue();
						
						
						map1.put("appraiseCount", total1);
						if(total1 == 0) {
							map1.put("appraiseAverageScore", 0);
						}
						else {
							map1.put("appraiseAverageScore", Double.valueOf(sum1) / Double.valueOf(total1));
						}
						list.add(map1);
					}
					pageResult = new Page<>(list, page7.getTotal(), pageable);
					
					json.setSuccess(true);
	    			json.setMsg("查询成功");
	    			json.setObj(pageResult);
	    			return json;

				default:
					break;
				}
    	
    			pageResult = new Page<>(new ArrayList<Map<String, Object>>(), 0, pageable);
    			json.setSuccess(true);
    			json.setMsg("查询成功");
    			json.setObj(pageResult);
    		}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
		}
    	
    	return json;
    }
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(HttpSession session,Pageable pageable,@DateTimeFormat(iso=ISO.DATE_TIME)Date start,@DateTimeFormat(iso=ISO.DATE_TIME)Date end,String userName,String appraiseContent){
		Json json=new Json();
		try {
			List<Filter> filters=new ArrayList<>();
			String adminUsername = (String) session.getAttribute(CommonAttributes.Principal);
			if(adminUsername != null) {
				//是管理员
//				HyAdmin hyAdmin = hyAdminService.find(adminUsername);
				
			}
			else {
				//是官网用户
				//用户名是手机号
				String portalUsername = (String) session.getAttribute(CommonAttributes.PORTAL_LOGIN);
				List<Filter> filters123 = new ArrayList<>();
				filters123.add(Filter.eq("phone", portalUsername));
				HyUser hyUser = hyUserService.findList(null, filters, null).get(0);
				//筛选这个用户的
				filters.add(Filter.eq("appraiser", hyUser));
			}
			if(start!=null){
				start=DateUtil.getStartOfDay(start);
				filters.add(Filter.ge("appraiseTime", start));
			}
			if(end!=null){
				end=DateUtil.getEndOfDay(end);
				filters.add(Filter.le("appraiseTime", end));
			}
			if(userName!=null){
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.like("userName", userName));
				List<HyUser> HyUsers = hyUserService.findList(null,filters2,null);
				filters.add(Filter.in("appraiser", HyUsers));
			}
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<MhAppraise> page=mhAppraiseService.findPage(pageable);
			HashMap<String, Object> hashMap=new HashMap<>();
			hashMap.put("total", page.getTotal());
			hashMap.put("pageNumber", page.getPageNumber());
			hashMap.put("pageSize", page.getPageSize());
			List<HashMap<String, Object>> result=new ArrayList<>();
			for(MhAppraise mhAppraise:page.getRows()){
				HashMap<String, Object> hm=new HashMap<>();
				hm.put("id", mhAppraise.getId());
				hm.put("accountName",mhAppraise.getAppraiser().getUserName());
				hm.put("appraiseTime", mhAppraise.getAppraiseTime());
				hm.put("appraiseContent", mhAppraise.getAppraiseContent());
				hm.put("contentLevel", mhAppraise.getContentLevel());
				hm.put("isAnonymous", mhAppraise.getIsAnonymous());
				hm.put("isShow", mhAppraise.getIsShow());
				hm.put("isValid", mhAppraise.getIsValid());
				
				HyOrderItem orderItem = hyOrderItemService.find(mhAppraise.getOrderItemId());
				String productName = "";
				//特产
				if(mhAppraise.getOrderItemType() == 1) {
					 Specialty specialty = specialtyService.find(orderItem.getProductId());
					 if(specialty != null) {
						 productName = specialty.getName();
					 }
				}
				else {
					//其他所有
					//订单条目类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证,8线路其他价格
					switch (orderItem.getType()) {
					//导游租赁
					case 0:
						
						break;
					//线路
					case 1:
						//找到团名称
						HyGroup hyGroup = hyGroupService.find(orderItem.getProductId());
						if(hyGroup != null) {
							productName = hyGroup.getGroupLineName();
						}
						break;
					//认购门票
					case 2:
						HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(orderItem.getProductId());
						if(hyTicketSubscribe != null) {
							productName = hyTicketSubscribe.getSceneName();
						}
						break;
					//酒店
					case 3:
						HyTicketHotel hyTicketHotel = hyTicketHotelService.find(orderItem.getProductId());
						if(hyTicketHotel != null) {
							productName = hyTicketHotel.getHotelName();
						}
						break;
					//门票
					case 4:
						HyTicketScene hyTicketScene = hyTicketSceneService.find(orderItem.getProductId());
						if(hyTicketScene != null) {
							productName = hyTicketScene.getSceneName();
						}
						break;
					//酒加景
					case 5:
						HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(orderItem.getProductId());
						if(hyTicketHotelandscene != null) {
							productName = hyTicketHotelandscene.getProductName();
						}
						break;
					//保险
					case 6:
						Insurance insurance = insuranceService.find(orderItem.getProductId());
						if(insurance != null) {
							productName = insurance.getRemark();
						}
						break;
					//签证
					case 7:
						HyVisa hyVisa = hyVisaService.find(orderItem.getProductId());
						if(hyVisa != null) {
							productName = hyVisa.getProductName();
						}
						break;

					default:
						break;
					}
				}
				hm.put("productName", productName);
//				hm.put("specification", specialtyAppraise.getSpecification().getSpecification());
//				hm.put("orderCode", specialtyAppraise.getBusinessOrder().getOrderCode());
				result.add(hm);
			}
			hashMap.put("rows", result);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hashMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("appraise/changeStatus")
	@ResponseBody
	public Json changeStatus(Long id, HttpSession session){
		Json json=new Json();
		try {
			MhAppraise mhAppraise = mhAppraiseService.find(id);
			String adminUsername = (String) session.getAttribute(CommonAttributes.Principal);
			if(adminUsername != null) {
				//是管理员
//				HyAdmin hyAdmin = hyAdminService.find(adminUsername);
				mhAppraise.setIsValid(mhAppraise.getIsValid() == 1 ? 2 : 1);
			}
			else {
				//是官网用户
//				String portalUsername = (String) session.getAttribute(CommonAttributes.PORTAL_LOGIN);
//				List<Filter> filters = new ArrayList<>();
//				filters.add(Filter.eq("userName", portalUsername));
//				HyUser hyUser = hyUserService.findList(null, filters, null).get(0);
				mhAppraise.setIsValid(mhAppraise.getIsValid() == 1 ? 0 : 1);
			}
			
			
			
			//to do
			//mhAppraise.setIsValid((mhAppraise.getIsValid() ? false:true));
			mhAppraiseService.update(mhAppraise);
			json.setSuccess(true);
			json.setMsg("修改成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("修改失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("appraise/changeShowAndAnonymous")
	@ResponseBody
	public Json changeShowAndAnonymous(Long id, HttpSession session,Boolean isShow, Boolean isAnonymous){
		Json json=new Json();
		try {
			MhAppraise mhAppraise = mhAppraiseService.find(id);
			mhAppraise.setIsShow(isShow);
			mhAppraise.setIsAnonymous(isAnonymous);
			mhAppraiseService.update(mhAppraise);
			json.setSuccess(true);
			json.setMsg("修改成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("修改失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	//评论查看图片
    @RequestMapping("/appraise/images/view")
    @ResponseBody
    public Json appraiseImages(Long id)
    {
    	Json json=new Json();
    	try {
    		MhAppraise appraise = mhAppraiseService.find(id);
    		List<MhAppraiseImage> images=appraise.getImages();
    		json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(images);
    	}
    	catch(Exception e) {
    		json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
    	}
    	return json;
    }
	
	@RequestMapping("appraise/detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try {
			MhAppraise Appraise=mhAppraiseService.find(id);
			if(Appraise!=null){
				Map<String,Object> map = new HashMap<>();
				map.put("contentLevel", Appraise.getContentLevel());
				
				
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(Appraise);
			}else{
				json.setSuccess(false);
				json.setMsg("评价不存在");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("appraise/delete")
	@ResponseBody
	public Json delete(Long id, HttpSession session){
		Json json=new Json();
		try {
			MhAppraise mhAppraise = mhAppraiseService.find(id);
			
			if(mhAppraise==null){
				json.setSuccess(false);
				json.setMsg("评价不存在");
			}
			else{
				//to do
				String adminUsername = (String) session.getAttribute(CommonAttributes.Principal);
				if(adminUsername != null) {
					//是管理员
//					HyAdmin hyAdmin = hyAdminService.find(adminUsername);
					//管理员删除
					mhAppraise.setIsValid(2);
				}
				else {
					//是官网用户
//					String portalUsername = (String) session.getAttribute(CommonAttributes.PORTAL_LOGIN);
//					List<Filter> filters = new ArrayList<>();
//					filters.add(Filter.eq("userName", portalUsername));
//					HyUser hyUser = hyUserService.findList(null, filters, null).get(0);
					//用户删除
					mhAppraise.setIsValid(0);
				}
				mhAppraiseService.update(mhAppraise);
				json.setSuccess(true);
				json.setMsg("删除成功");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

}
