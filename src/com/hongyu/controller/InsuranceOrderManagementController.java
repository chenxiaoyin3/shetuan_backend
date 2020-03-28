package com.hongyu.controller;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.crypto.hash.Hash;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.ConfirmMessage;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyPolicyHolderInfo;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.InsuranceOrder;
import com.hongyu.entity.JtOrderResponse;
import com.hongyu.entity.Store;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyPolicyHolderInfoService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.hongyu.util.JiangtaiUtil;
import com.hongyu.util.XStreamUtil;



/**
 * 修改与2018年9月26日，整体修改保险订单表结构
 * 保单管理
 * @author li_yang
 *
 */
@Controller
@RequestMapping("/admin/insuranceOrderManagement/")
public class InsuranceOrderManagementController {
	@Resource(name = "insuranceServiceImpl")
	InsuranceService insuranceService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyPolicyHolderInfoServiceImpl")
	HyPolicyHolderInfoService hyPolicyHolderInfoService;
//	
//	/**
//	 * 返回保单管理的列表   初代版本
//	 * @param pageable		分页信息
//	 * @param id			产品id(其实是产品的编号pn)
//	 * @param status		状态
//	 * @param startDate		团开始日期
//	 * @param endDate		团结束日期
//	 * @param session		
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping("list/view")
//	@ResponseBody
//	public Json getInsuranceOrderList(Pageable pageable,String id,Integer status ,@DateTimeFormat(iso=ISO.DATE)Date startDate,
//			@DateTimeFormat(iso=ISO.DATE)Date endDate,HttpSession session, HttpServletRequest request){
//		Json json = new Json();
//		try{
//			HashMap<String, Object> hm = new HashMap<String, Object>();
//			List<HashMap<String, Object>> result = new ArrayList<>();
//			/**
//			 * 获取当前用户
//			 */
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin admin = hyAdminService.find(username);
//
//			/**
//			 * 获取用户权限范围
//			 */
//			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
//			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
//			/** 将数据按照时间排序 */
//			List<Order> orders = new ArrayList<Order>();
//			Order order = Order.desc("createDate");
//			orders.add(order);
//
//			/** 数据按照创建人筛选 */
//			List<Filter> filters = new ArrayList<Filter>();
//			//Filter filter = Filter.in("operator", hyAdmins);
//			
//			//filters.add(filter);
//			if(status != null){
//				filters.add(Filter.eq("status", status));
//			}else{
//				filters.add(Filter.ne("status", 0));
//				filters.add(Filter.ne("status", 2));
//			}	
//			pageable.setFilters(filters);
//			pageable.setOrders(orders);
//			/*
//			 * 这里只筛选了状态为status的所有保单
//			 */
//			List<InsuranceOrder> orderList = insuranceOrderService.findList(null, filters, orders);
//			
//			//Page<InsuranceOrder> page = insuranceOrderService.findPage(pageable);
//			/*
//			 * finallist是最终将所有筛选条件加入之后筛选得到的列表
//			 */
//			List<InsuranceOrder> finalList = new ArrayList<>();
//			
//			//判断当时间区间为空的时候
//			if(endDate == null && startDate == null){
//				if( id == null){
//					finalList.addAll(orderList);
//				}else{
//					for(InsuranceOrder tmp:orderList){
//						if(tmp.getType() == 0 && tmp.getGroupId()!=null){
//							HyGroup group = hyGroupService.find(tmp.getGroupId());
//							if(id.equals(group.getGroupLinePn())){
//								finalList.add(tmp);
//							}
//						}
//						//如果是自主投保，应该怎么获取信息
//						if(tmp.getType() == 1){
//							
//						}
//					}
//				}	
//			}
//			//只传递了结束日期参数
//			if(endDate != null && startDate == null){
//				for(InsuranceOrder tmp:orderList){
//					if(tmp.getGroupId()!=null){
//						HyGroup group = hyGroupService.find(tmp.getGroupId());
//						if(id != null){
//							if((endDate.after(group.getStartDay()) || endDate.equals(group.getStartDay())) && id.equals(group.getGroupLinePn())){
//								finalList.add(tmp);
//							}
//						}else{
//							if(endDate.after(group.getStartDay()) || endDate.equals(group.getStartDay())){
//								finalList.add(tmp);
//							}
//						}
//					}
//				}
//			}
//			//只传递了开始日期参数
//			if(endDate == null && startDate != null){
//				for(InsuranceOrder tmp:orderList){
//					if(tmp.getGroupId()!=null){
//						HyGroup group = hyGroupService.find(tmp.getGroupId());
//						if(id != null){
//							if((startDate.before(group.getStartDay()) || startDate.equals(group.getStartDay())) && id.equals(group.getGroupLinePn())){
//								finalList.add(tmp);
//							}
//						}else{
//							if(startDate.before(group.getStartDay()) || startDate.equals(group.getStartDay())){
//								finalList.add(tmp);
//							}
//						}
//					}
//				}
//			}
//			//开始和结束日期都有
//			if(endDate != null && startDate != null){
//				for(InsuranceOrder tmp:orderList){
//					if(tmp.getGroupId()!=null){
//						HyGroup group = hyGroupService.find(tmp.getGroupId());
//						if(id != null){
//							if((startDate.before(group.getStartDay()) || startDate.equals(group.getStartDay())) && (endDate.after(group.getStartDay()) || endDate.equals(group.getStartDay())) && id.equals(group.getGroupLinePn())){
//								finalList.add(tmp);
//							}
//						}else{
//							if((startDate.before(group.getStartDay()) || startDate.equals(group.getStartDay())) && (endDate.after(group.getStartDay()) || endDate.equals(group.getStartDay()))){
//								finalList.add(tmp);
//							}
//						}		
//					}
//				}
//			}
//			
//			
//			
//			
//			//遍历finallist然后封装数据
//			for (InsuranceOrder tmp : finalList) {
//				HyGroup group = hyGroupService.find(tmp.getGroupId());
//				//根据订单Id获取门店和计调
//				HyOrder hyOrder = hyOrderService.find(tmp.getOrderId());
//				for(HyPolicyHolderInfo policyHolderInfo:tmp.getPolicyHolders()){
//					HashMap<String, Object> m = new HashMap<String, Object>();
//					
//					//在此处即可判定，该保单属于检索日期范围内的。
//					m.put("id", tmp.getId());
//					m.put("orderId", tmp.getOrderId());
//					
//					if(hyOrder != null){
//						m.put("orderNumber", hyOrder.getOrderNumber());
//						m.put("operator", hyOrder.getOperator().getName());
//						//获取电子保单下载地址
//						m.put("orderDownUrl",hyOrder.getInsuranceOrderDownloadUrl());
//						Store store = storeService.find(hyOrder.getStoreId());
//						if(store != null){
//							m.put("storeName", store.getStoreName());
//						}
//					}
//					Insurance insurance = insuranceService.find(tmp.getInsuranceId());
//					//保险方案
//					if(insurance != null){
//						m.put("insuranceCode", insurance.getInsuranceCode());
//						m.put("insurancePlan", insurance.getRemark());
//					}
//					if(group!=null){
//						m.put("groupId", tmp.getGroupId());
//						m.put("startGroupDate", group.getStartDay());
//						m.put("endGroupDate",group.getEndDay());
//						//相减转换成天数
//						m.put("groupDays", (group.getEndDay().getTime()-group.getStartDay().getTime())/ (1000 * 60 * 60 * 24));
//						m.put("groupLinePn", group.getGroupLinePn());
//						m.put("groupLineName", group.getGroupLineName());
//					}
//					m.put("insuranceStartTime", tmp.getInsuranceStarttime());
//					m.put("insuranceEndTime", tmp.getInsuranceEndtime());
//					m.put("policyHolderId", policyHolderInfo.getId());
//					m.put("name", policyHolderInfo.getName());
//					m.put("age", policyHolderInfo.getAge());
//					m.put("sex", policyHolderInfo.getSex());
//					m.put("certificate", policyHolderInfo.getCertificate());
//					m.put("certificateNumber", policyHolderInfo.getCertificateNumber());
//					m.put("dowmloadUrl", policyHolderInfo.getDownloadUrl());
//					m.put("type", tmp.getType());
//					m.put("status", tmp.getStatus());
//					result.add(m);
//				}
//										
//			}
//			int pg = pageable.getPage();
//			int rows = pageable.getRows();
//			hm.put("total", result.size());
//			hm.put("pageNumber", pg);
//			hm.put("pageSize", rows);
//			hm.put("rows", result.subList((pg-1)*rows, pg*rows>result.size()?result.size():pg*rows));
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(hm);
//		}catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("获取失败: " + e.getMessage());
//			e.printStackTrace();
//		}
//		return json;
//	}
	/**
	 * 返回保单管理的列表   二代版本
	 * @param pageable		分页信息
	 * @param id			产品id(其实是产品的编号pn)
	 * @param status		状态
	 * @param startDate		团开始日期
	 * @param endDate		团结束日期
	 * @param session		
	 * @param request
	 * @return
	 */
//	@RequestMapping("list/view")
//	@ResponseBody
//	public Json insuranceList(Pageable pageable,String id,Integer status ,String customerName,
//			@DateTimeFormat(iso=ISO.DATE)Date startDate,
//			@DateTimeFormat(iso=ISO.DATE)Date endDate,
//			HttpSession session, HttpServletRequest request){
//		Json json = new Json();
//		try{
//			HashMap<String, Object> hm = new HashMap<String, Object>();
//	
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin admin = hyAdminService.find(username);
//			/** 将数据按照时间排序 */
//			List<Order> orders = new ArrayList<Order>();
//			Order order = Order.desc("createDate");
//			orders.add(order);
//			List<Filter> filters = new ArrayList<Filter>();
//			if(status != null){
//				filters.add(Filter.eq("status", status));
//			}else{
//				filters.add(Filter.ne("status", 0));
//				filters.add(Filter.ne("status", 2));
//			}
//			if(startDate!=null){
//				filters.add(Filter.ge("insuranceStarttime", startDate));
//			}
//			if(endDate!=null){
//				filters.add(Filter.le("insuranceEndtime", endDate));
//			}
//			pageable.setFilters(filters);
//			pageable.setOrders(orders);
//			//List<InsuranceOrder> orderList = insuranceOrderService.findList(null, filters, orders);		
//			List<InsuranceOrder> list = insuranceOrderService.findList(null,filters,orders);
//			List<HashMap<String, Object>> result = new ArrayList<>();
//			for (InsuranceOrder tmp : list) {
//				HyGroup group = hyGroupService.find(tmp.getGroupId());
//				//根据订单Id获取门店和计调
//				HyOrder hyOrder = hyOrderService.find(tmp.getOrderId());
//				Store store = storeService.find(hyOrder.getStoreId());
//				Insurance insurance = insuranceService.find(tmp.getInsuranceId());
//				List<HashMap<String, Object>> tmpList = new ArrayList<>();
//				boolean ifContainsCustomer = false;
//				for(HyPolicyHolderInfo policyHolderInfo:tmp.getPolicyHolders()){
//					HashMap<String, Object> m = new HashMap<String, Object>();
//					if(policyHolderInfo.getName()==null)
//						throw new Exception("游客姓名为空！");
//					if(customerName != null && policyHolderInfo.getName().contains(customerName))
//						ifContainsCustomer = true;
//					
//					m.put("id", tmp.getId());
//					m.put("orderId", tmp.getOrderId());
//					
//					if(hyOrder != null){
//						m.put("orderNumber", hyOrder.getOrderNumber());
//						m.put("operator", hyOrder.getOperator().getName());
//						//获取电子保单下载地址
//						m.put("orderDownUrl",hyOrder.getInsuranceOrderDownloadUrl());
//						
//						if(store != null){
//							m.put("storeName", store.getStoreName());
//						}
//					}
//					
//					//保险方案
//					if(insurance != null){
//						m.put("insuranceCode", insurance.getInsuranceCode());
//						m.put("insurancePlan", insurance.getRemark());
//					}
//					if(group!=null){
//						m.put("groupId", tmp.getGroupId());
//						m.put("startGroupDate", group.getStartDay());
//						m.put("endGroupDate",group.getEndDay());
//						//相减转换成天数
//						m.put("groupDays", (group.getEndDay().getTime()-group.getStartDay().getTime())/ (1000 * 60 * 60 * 24));
//						m.put("groupLinePn", group.getGroupLinePn());
//						m.put("groupLineName", group.getGroupLineName());
//					}
//					m.put("insuranceStartTime", tmp.getInsuranceStarttime());
//					m.put("insuranceEndTime", tmp.getInsuranceEndtime());
//					m.put("policyHolderId", policyHolderInfo.getId());
//					m.put("name", policyHolderInfo.getName());
//					m.put("age", policyHolderInfo.getAge());
//					m.put("sex", policyHolderInfo.getSex());
//					m.put("certificate", policyHolderInfo.getCertificate());
//					m.put("certificateNumber", policyHolderInfo.getCertificateNumber());
//					m.put("dowmloadUrl", policyHolderInfo.getDownloadUrl());
//					m.put("type", tmp.getType());
//					m.put("status", tmp.getStatus());
//					tmpList.add(m);
//				}
//				if(customerName==null || ifContainsCustomer)
//					result.addAll(tmpList);
//				tmpList = null;										
//			}
//			int pg = pageable.getPage();
//			int rows = pageable.getRows();
//			hm.put("total", result.size());
//			hm.put("pageNumber", pg);
//			hm.put("pageSize", rows);
//			//System.out.println("pg = "+pg+"  rows = "+rows+"  result.size =" +result.size());
//			hm.put("rows", result.subList((pg-1)*rows, pg*rows>result.size()?result.size():pg*rows));
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(hm);
//		}catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("获取失败: " + e.getMessage());
//			e.printStackTrace();
//		}
//		return json;
//	}
	/**
	 * 返回保单管理的列表 最新版本  2019.06.16
	 * @param pageable		分页信息
	 * @param id			产品id(其实是产品的编号pn)
	 * @param status		状态
	 * @param startDate		团开始日期
	 * @param endDate		团结束日期
	 * @param session		
	 * @param request
	 * @return
	 */
	@RequestMapping("list/view")
	@ResponseBody
	public Json insuranceList1(Pageable pageable,String id,Integer status ,String customerName,
			@DateTimeFormat(iso=ISO.DATE)Date startDate,
			@DateTimeFormat(iso=ISO.DATE)Date endDate,
			HttpSession session, HttpServletRequest request){
		Json json = new Json();
		
		try{	
			/*将筛选条件转化成sql条件*/
			StringBuilder conditions = new StringBuilder();
			
			if(status!=null){
				conditions.append(" and io.status = "+status);
			}else{
				conditions.append(" and io.status != 2 and io.status != 0");
			}
			if(customerName!=null){
				conditions.append(" and p.name like '%"+customerName+"%'");
			}
			
			if(startDate!=null){
				Date date = DateUtil.getStartOfDay(startDate);
				String starttime = DateUtil.getSimpleDate(date);
				conditions.append(" and unix_timestamp(io.insurance_starttime) >= ")
						.append("unix_timestamp('"+starttime+"')");
			}
			if(endDate!=null){
				Date date = DateUtil.getEndOfDay(endDate);
				String endtime = DateUtil.getSimpleDate(date);
				conditions.append(" and unix_timestamp(io.insurance_starttime) <= ")
						.append("unix_timestamp('"+endtime+"')");
			}
			System.out.println("conditions = "+conditions.toString());
			/*团期投保的查询*/
			StringBuilder sql_tuanqi = new StringBuilder();
			String tq_columns = "select io.id as boxianid,io.order_id,"
					+ "p.name as policyname,p.age,p.sex,p.certificate,"
					+ "p.certificate_number,io.insurance_starttime,"
					+ "io.insurance_endtime,io.type,io.status,o.order_number,"
					+ "am.name as operatorname,io.download_url as iodurl,"
					+ "s.store_name,i.insurance_code,i.remark,io.group_id,"
					+ "o.tianshu,g.group_line_pn as linepn,g.group_line_name as linename,"
					+ "p.id as policyid,p.download_url as pdurl ,io.create_date";
			String tq_tables = " from hy_order as o,hy_insurance_order as io,"
					+ "hy_group as g,hy_insurance as i,hy_store as s,"
					+ "hy_policyholder_info as p, hy_admin as am";
			String tq_where = " where io.id = p.insurance_order_id and g.id = io.group_id"
					+ " and o.id = io.order_id and i.id = io.insurance_id "
					+ "and s.id = o.store_id "
					+ "and am.username = o.operator_id and io.type = 0"
					+ conditions;
			sql_tuanqi.append(tq_columns).append(tq_tables).append(tq_where);
			
			/*门店单买投保的查询*/
			StringBuilder sql_danmai = new StringBuilder();
			String dm_columns = "select io.id as boxianid,io.order_id,p.name as policyname,"
					+ "p.age,p.sex,p.certificate,p.certificate_number,"
					+ "io.insurance_starttime,io.insurance_endtime,io.type,io.status,"
					+ "o.order_number,am.name as operatorname,io.download_url as iodurl,"
					+ "s.store_name,i.insurance_code,i.remark,io.group_id,o.tianshu,"
					+ "NULL as linepn,NULL as linename,p.id as policyid,"
					+ "p.download_url as pdurl ,io.create_date";
			String dm_tables = " from hy_order as o,hy_insurance_order as io,"
					+ "hy_insurance as i,hy_store as s,hy_policyholder_info "
					+ "as p, hy_admin as am";
			String dm_where = " where io.id = p.insurance_order_id and o.id = io.order_id"
					+ " and i.id = io.insurance_id  and s.id = o.store_id "
					+ "and am.username = o.operator_id and io.type=1"
					+ conditions;
			sql_danmai.append(dm_columns).append(dm_tables).append(dm_where);
						
			StringBuilder sql_main = new StringBuilder();
			/*如果筛选条件有pn，那么很明显自主投保的数据都不包括在里面*/
			if(id != null){
				sql_main.append(sql_tuanqi)
						.append(" and g.group_line_pn like '%"+id+"%'")
						.append(" order by io.create_date DESC");
			}else{
			
				sql_main.append("select * from ")
						.append("(")
						.append("(").append(sql_tuanqi).append(")")
						.append(" union ")
						.append("(").append(sql_danmai).append(")")
						.append(")")
						.append("as final")
						.append(" order by final.create_date DESC");
			}
			List<Object[]> list = insuranceOrderService.statis(sql_main.toString());
			/*封装返回结果*/
			List<HashMap<String,Object>> result = new ArrayList<>();
			for(Object[] objects:list){
				HashMap<String, Object> m = new HashMap<String, Object>();
				
				m.put("id", objects[0]);
				m.put("orderId", objects[1]);
				m.put("name", objects[2]);
				m.put("age", objects[3]);
				m.put("sex", objects[4]);
				m.put("certificate", objects[5]);
				m.put("certificateNumber", objects[6]);
				m.put("insuranceStartTime", objects[7]);
				m.put("insuranceEndTime", objects[8]);
				m.put("type", objects[9]);
				m.put("status", objects[10]);
				m.put("orderNumber", objects[11]);
				m.put("operator", objects[12]);
				m.put("orderDownUrl", objects[13]);
				m.put("storeName", objects[14]);
				m.put("insuranceCode", objects[15]);
				m.put("insurancePlan", objects[16]);
				m.put("groupId", objects[17]);
				m.put("groupDays", objects[18]);
				m.put("groupLinePn", objects[19]);
				m.put("groupLineName", objects[20]);
				m.put("policyHolderId", objects[21]);
				m.put("dowmloadUrl", objects[22]);
				m.put("createTime", objects[23]);
				
				result.add(m);			
			}
			
			HashMap<String , Object> hm = new HashMap<>();
			int pg = pageable.getPage();
			int rows = pageable.getRows();
			hm.put("total", result.size());
			hm.put("pageNumber", pg);
			hm.put("pageSize", rows);
			//System.out.println("pg = "+pg+"  rows = "+rows+"  result.size =" +result.size());
			hm.put("rows", result.subList((pg-1)*rows, pg*rows>result.size()?result.size():pg*rows));
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hm);
		}catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 根据orderId来投保，需要处理投保成功之后的操作----更新保单状态。
	 * @param orderIds
	 * @return
	 */
	@RequestMapping("insure")
	@ResponseBody
	public Json insuredToJT(Long[] orderIds){
		Json json = new Json();
		//将那些投保失败的订单再依次投保一遍，每个订单应该中包含有好几个被保人
		//1、如果投保类型是团投，就是加入了旅游团，就是封装所有团成员到ConfirmMessageOrder中的InsuranceInfo里面
		//2、如果投保类型是自主投保，那就是他去门店自己买保险。
		try {
			json = insuranceOrderService.postOrderToJT(orderIds);
			HashMap<String, Object> map = (HashMap<String, Object>)json.getObj();
			List<HashMap<String , Object>> successIds = (List<HashMap<String , Object>>)map.get("successIds");
			List<HashMap<String , Object>> failIds = (List<HashMap<String , Object>>)map.get("failIds");
			//逻辑待定
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("投保出错 "+e.getMessage());
			json.setSuccess(false);
		}
		
		return json;
	}
	/**
	 * 根据订单id来取消保险订单。
	 * 1、只能是指定orderId的团一起取消，就是在因为各种原因下，虹宇自行撤销该团
	 * 2、个人在门店自己购买的保险，在保险生效之前可以退保险。
	 * @param orderIds
	 * @return
	 */
	@RequestMapping("cancel")
	@ResponseBody
	public Json cancelOrder(Long[] orderIds){
		Json json = new Json();
		//将那些投保失败的订单再依次投保一遍，每个订单应该有好几个投保人
		//1、如果投保类型是团投，就是封装所有团成员到ConfirmMessageOrder中的InsuranceInfo里面
		//2、如果投保类型是们门店自主投保
		try {
			long startTime = System.currentTimeMillis();
			//将传入的所有的单子全部撤保
			Map<String, Object> map = new HashMap<>();
			List<Map<String,Object>> cancelSuccessIds = new ArrayList<>();
			List<Map<String,Object>> cancelFailIds = new ArrayList<>();
			for(int i=0;i<orderIds.length;i++){
				Long orderId = orderIds[i];
				//根据该订单id去获取所有的保险人对应的保单
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("orderId", orderId));
				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,filters,null);
				//因为撤单只需要提供这个订单的投保流水号就行了，而这个订单对应的那些人的投保流水号是一样的，所以取第一个就好了
				InsuranceOrder insuranceOrder = insuranceOrders.get(0);
				//如果当前订单已经撤保，直接跳过
				if(insuranceOrder.getStatus() == 4){
					continue;
				}
				//封装撤保参数
				ConfirmMessage cmoc  = new ConfirmMessage();
				cmoc.setChannelTradeSerialNo(insuranceOrder.getJtChannelTradeSerialNo());
				cmoc.setChannelTradeDate(insuranceOrder.getJtChannelTradeDate());
				cmoc.setChannelTradeCode(Constants.CHANNEL_TRADE_CODE_CANCEL_ORDER);
				//调用撤保接口来撤保
				json = JiangtaiUtil.orderCancel(cmoc);
				JtOrderResponse jtOrderResponse = (JtOrderResponse) XStreamUtil.xmlToBean(json.getObj().toString());
				if("000001".equals(jtOrderResponse.getResponseCode())){
					//设置为4，就是已撤保状态	
					insuranceOrder.setStatus(4);
					insuranceOrderService.update(insuranceOrder); 					
					Map<String, Object> tmp = new HashMap<>();
					tmp.put("orderId",orderId);
					tmp.put("responseMessage", jtOrderResponse.getResponseMessage());
					cancelSuccessIds.add(tmp);
				}else{
					Map<String, Object> tmp = new HashMap<>();
					tmp.put("orderId",orderId);
					tmp.put("responseMessage", jtOrderResponse.getResponseMessage());
					cancelFailIds.add(tmp);
				}
			}
			map.put("cancelSuccessIds", cancelSuccessIds);
			map.put("cancelFailIds", cancelFailIds);
			long endTime = System.currentTimeMillis();
			json.setMsg("以下订单退保成功,共耗时 "+ (endTime-startTime) + "ms");
			json.setObj(map);
		} catch (Exception e) {
			json.setMsg("撤销保单出错 "+e.getMessage());
			json.setSuccess(false);
		}
		return json;
	}
	/**
	 * 按照订单id来下载电子保单
	 * @param orderId	订单ID
	 * @return
	 */
	@RequestMapping("orderDown")
	@ResponseBody
	public Json orderDown(Long orderId){
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderId", orderId));
			//只需要一个保单就好了，为了拿到交易流水号
			List<InsuranceOrder> orders = insuranceOrderService.findList(1,filters,null);
			//直接将下载保单的url封装好返回给前端。
			String serialNo = orders.get(0).getJtChannelTradeSerialNo();
			String param = "serialNo="+serialNo+"&travelCode="+Constants.TRAVEL_AGENCY_CODE;
			String result = Constants.JT_ORDER_DOWN_URL+"?"+param;
			json.setMsg("有该订单对应的保单，可以下载");
			json.setSuccess(true);
			json.setObj(result);
		
		} catch (Exception e) {
			json.setMsg("查询时出错 "+e.getMessage());
			json.setSuccess(false);
		}
		return json;
	}
	/**
	 * 根据InsuranceOrder中的主键id来下载指定保险人的保险单。
	 * @param id 保险订单表InsuranceOrder中的主键id。
	 * @return
	 */
	@RequestMapping("orderDownCertificate")
	@ResponseBody
	public Json downloadCertificate(Long id,Long policyHolderId){
		Json json = new Json();
		//这里需要获取保险人的交易流水号，旅行社编号和被保险人证件号
		try {
			InsuranceOrder insuranceOrder = insuranceOrderService.find(id);
			HyPolicyHolderInfo hyPolicyHolderInfo = hyPolicyHolderInfoService.find(policyHolderId);
			//证件号码
			String cardNo = hyPolicyHolderInfo.getCertificateNumber();
			//渠道交易流水号
			String serialNo = insuranceOrder.getJtChannelTradeSerialNo();
			//旅行社代码
			String travelCode = Constants.TRAVEL_AGENCY_CODE;
			//需要按规格组装URL
			String suffix = serialNo+"-"+travelCode+"-"+cardNo;
			//得到下载个人凭证的URL
			String result = Constants.JT_CERTIFICATE_DOWN_URL+suffix;
			json.setMsg("可以下载");
			json.setSuccess(true);
			json.setObj(result);

		} catch (Exception e) {
			json.setMsg("获取个人保单出错");
			json.setSuccess(false);
		}
		return json;
	}
	@RequestMapping("updateOldData")
	@ResponseBody
	public Json updateOldData(){
		Json json = new Json();
		try {
			Long btime = System.currentTimeMillis();
			insuranceOrderService.updateOldDataMoney();
			Long atime = System.currentTimeMillis();
			json.setMsg("更新成功,用时 "+(btime-atime));
			json.setSuccess(true);
			json.setObj(null);

		} catch (Exception e) {
			json.setMsg("更新出错");
			json.setSuccess(false);
		}
		return json;
	}
}
