package com.hongyu.controller.gdw;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.TaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.entity.StoreRecharge;
import com.hongyu.entity.BankList.BankType;
import com.hongyu.entity.BankList.Yinhangleixing;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.service.BankListService;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StoreRechargeService;
import com.hongyu.service.StoreService;
import com.thoughtworks.xstream.mapper.Mapper.Null;

@Controller
@RequestMapping("/admin/storeAccountLog/")
public class StoreAccountLogController {

	@Resource(name="storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;
	
	@Resource(name="storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name="storeServiceImpl")
	StoreService storeService;
	
	@Resource(name="storeRechargeServiceImpl")
	StoreRechargeService storeRechargeService;
	
	@Resource(name="bankListServiceImpl")
	BankListService bankListService;
	
	@Resource(name="hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	
	@RequestMapping("czlist/view")
	@ResponseBody
	public Json czlist(Pageable pageable,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("department", department));
			List<Store> stores=storeService.findList(null, filters, null);
			if(stores!=null&&stores.size()>0){
				Store store=stores.get(0);
				List<Filter> filters2=new LinkedList<>();
				filters2.add(Filter.eq("store", store));
				if(startDate!=null){
					filters2.add(Filter.ge("createDate", startDate));
				}
				if(endDate!=null){
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					calendar.add(Calendar.DATE, 1);
					endDate  = calendar.getTime();
					filters2.add(Filter.lt("createDate", endDate));
//					filters2.add(Filter.le("createDate", endDate));
				}
				filters2.add(Filter.ne("type", 1));//1报名抵扣记录
				filters2.add(Filter.ne("type", 6));//6海报设计抵扣
				pageable.setFilters(filters2);
				List<Order> orders = new ArrayList<>();
				orders.add(Order.desc("createDate"));
				pageable.setOrders(orders);
				Page<StoreAccountLog> page=storeAccountLogService.findPage(pageable);
				List<Map<String, Object>> result=new LinkedList<>();
				for(StoreAccountLog tmp:page.getRows()){
					Map<String, Object> map=new HashMap<>();
					map.put("id", tmp.getId());
					map.put("status", tmp.getStatus());
					map.put("type", tmp.getType());
					map.put("money", tmp.getMoney());
					map.put("createDate", tmp.getCreateDate());
					map.put("profile", tmp.getProfile());
					map.put("orderSn", tmp.getOrderSn());
					result.add(map);
				}
				Map<String, Object> hMap=new HashMap<>();
				hMap.put("total", page.getTotal());
				hMap.put("pageNumber", page.getPageNumber());
				hMap.put("pageSize", page.getPageSize());
				hMap.put("rows", page.getRows());
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(hMap);
			}else{
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	@RequestMapping("dklist/view")
	@ResponseBody
	public Json dklist(Pageable pageable,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("department", department));
			List<Store> stores=storeService.findList(null, filters, null);
			if(stores!=null&&stores.size()>0){
				Store store=stores.get(0);
				List<Filter> filters2=new LinkedList<>();
				filters2.add(Filter.eq("store", store));
				if(startDate!=null){
					filters2.add(Filter.ge("createDate", startDate));
				}
				if(endDate!=null){
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					calendar.add(Calendar.DATE, 1);
					endDate  = calendar.getTime();
					filters2.add(Filter.lt("createDate", endDate));
//					filters2.add(Filter.le("createDate", endDate));
				}
				filters2.add(Filter.ge("type", 1));
				filters2.add(Filter.le("type", 6));
//				filters2.add(Filter.ne("type", 0));//0充值
				filters2.add(Filter.ne("type", 2));//2分成
				filters2.add(Filter.ne("type", 3));//3退团
				filters2.add(Filter.ne("type", 4));//2消团
				filters2.add(Filter.ne("type", 5));//2供应商驳回
//				filters2.add(Filter.ne("type", 7));//7租借导游退款
				pageable.setFilters(filters2);
				List<Order> orders = new ArrayList<>();
				orders.add(Order.desc("createDate"));
				pageable.setOrders(orders);
				Page<StoreAccountLog> page=storeAccountLogService.findPage(pageable);
				List<Map<String, Object>> result=new LinkedList<>();
				for(StoreAccountLog tmp:page.getRows()){
					Map<String, Object> map=new HashMap<>();
					map.put("id", tmp.getId());
					map.put("status", tmp.getStatus());
					map.put("type", tmp.getType());
					map.put("money", tmp.getMoney());
					map.put("createDate", tmp.getCreateDate());
					map.put("profile", tmp.getProfile());
					map.put("orderSn", tmp.getOrderSn());
					List<Filter> filters3 = new ArrayList<>();
					filters3.add(Filter.eq("orderNumber", tmp.getOrderSn()));
					List<HyOrder> orders2 = hyOrderService.findList(null,filters3,null);
					if(!orders2.isEmpty() &&orders2.size()>0){
						map.put("orderId", orders2.get(0).getId());
						map.put("orderType", orders2.get(0).getType());
					}
					result.add(map);
				}
				Map<String, Object> hMap=new HashMap<>();
				hMap.put("total", page.getTotal());
				hMap.put("pageNumber", page.getPageNumber());
				hMap.put("pageSize", page.getPageSize());
				hMap.put("rows", result);
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(hMap);
			}else{
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	@RequestMapping("add")
	@ResponseBody
	public Json add(@RequestBody StoreRecharge storeRecharge,HttpSession session){
		Json json=new Json();
		json=storeRechargeService.add(storeRecharge, session);
		return json;
	}
	@RequestMapping("getRechargeLimit/view")
	@ResponseBody
	public Json getLimit(){
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("eduleixing", Eduleixing.storeChongZhiLimit));
			List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
			CommonShenheedu xiane = edu.get(0);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(xiane.getMoney());
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取失败");
		}
		return json;
	}
	@RequestMapping("getBalance/view")
	@ResponseBody
	public Json getBalance(HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("department", department));
			List<Store> stores=storeService.findList(null, filters, null);
			if(stores==null||stores.size()==0){
				json.setSuccess(false);
				json.setMsg("门店不存在");
			}else{
				Store store=stores.get(0);
				List<Filter> filters2=new LinkedList<>();
				filters2.add(Filter.eq("store", store));
				List<StoreAccount> storeAccounts=storeAccountService.findList(null,filters2,null);
				if(storeAccounts!=null&&storeAccounts.size()>0){
					StoreAccount storeAccount=storeAccounts.get(0);
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(storeAccount.getBalance());
				}else{
					json.setSuccess(false);
					json.setMsg("账户不存在");
				}
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("getZGSBankList/view")
	@ResponseBody
	public Json getZGSBankList(Integer payment,HttpSession session){
		Json json=new Json();
		try {
//			String usernmae=(String)session.getAttribute(CommonAttributes.Principal);
//			HyAdmin hyAdmin=hyAdminService.find(usernmae);
//			Department department=hyAdmin.getDepartment();
//			String treePath=department.getTreePath();
//			String[] ids=treePath.split(",");
//			Long id=null;
//			if(ids.length<2){
//				id=1l;
//			}else{
//				id=Long.parseLong(ids[1]);
//			}
//			Department department2=departmentService.find(id);
//			List<Filter> filters=new LinkedList<>();
//			filters.add(Filter.eq("hyDepartment", department2));
//			List<HyCompany> hyCompanies=hyCompanyService.findList(null, filters,null);
//			
			List<Filter> filters=new LinkedList<>();
//			if(hyCompanies!=null&&hyCompanies.size()>0){
//				HyCompany hyCompany=hyCompanies.get(0);
//				filters2.add(Filter.eq("hyCompany", hyCompany));
//			}else{
//				json.setSuccess(false);
//				json.setMsg("公司不存在");
//				return json;
//			}
			filters.add(Filter.eq("yhlx", Yinhangleixing.zbcw));
			filters.add(Filter.eq("type", BankType.bank));
			List<BankList> bankLists=bankListService.findList(null,filters,null);
			List<Map<String, Object>> ans=new LinkedList<>();
			for(BankList tmp:bankLists){
				Map<String, Object> map=new HashMap<>();
				map.put("id", tmp.getId());
				map.put("accountName", tmp.getAccountName());
				map.put("bankName", tmp.getBankName());
				map.put("bankCode", tmp.getBankCode());
//				map.put("bankType", tmp.getType());
				map.put("bankAccount", tmp.getBankAccount());
				map.put("alias", tmp.getAlias());
				ans.add(map);
				
			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(ans);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	
	@RequestMapping("orderDetail/view")
	@ResponseBody
	public Json orderDetail(String orderSn) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderNumber", orderSn));
			List<HyOrder> orders = hyOrderService.findList(null,filters,null);
			if(orders==null || orders.isEmpty()) {
				json.setSuccess(false);
				json.setMsg("订单编号无效");
				json.setObj(null);
				return json;
			}
			
			json.setSuccess(true);
			json.setMsg("查找成功");
			json.setObj(orders.get(0));
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找失败");
			json.setObj(e);
		}
		return json;
		
	}
}
