package com.hongyu.controller.lbc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
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
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.PayServicer;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.entity.StoreApplication;
import com.hongyu.entity.StorePreSave;
import com.hongyu.entity.WithDrawCash;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.GroupPlaceholderService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.PayServicerService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StoreApplicationService;
import com.hongyu.service.StorePreSaveService;
import com.hongyu.service.StoreService;
import com.hongyu.service.WithDrawCashService;
import com.hongyu.util.SendMessageEMY;


@Controller
public class WithDrawCashController {
	@Resource(name = "GroupPlaceholderServiceImpl")
	GroupPlaceholderService groupPlaceholderService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "withDrawCashServiceImpl")
	WithDrawCashService withDrawCashService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	CommonEdushenheService commonEdushenheService;
	
	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;
	
	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	
	@Resource(name = "storeApplicationServiceImpl")
	StoreApplicationService storeApplicationService;
	
	
	/**
	 * 额度的详情
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/admin/storeAccountLog/withdraw_cash/shenheedu/detail/view")
	@ResponseBody
	public Json detail() {
		Json j = new Json();
		try {
			List<CommonShenheedu> commonShenheedus = commonEdushenheService.findAll();
			j.setObj(commonShenheedus);
			j.setMsg("查看成功");		
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	

	//财务列表显示
	@RequestMapping(value="/admin/storeAccountLog/withdraw_cash/list/view")
	@ResponseBody
	public Json WithDrawCashCaiWuList(Pageable pageable, HttpSession session, String storeName, Integer status,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date applyStartTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date applyEndTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date payStartTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date payEndTime){
		Json json = new Json();
		try {
			Calendar calendar = Calendar.getInstance();
			
			
			
			
			
			
			
			
			
			List<HashMap<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			if(storeName != null) {
				filters.add(Filter.eq("storeName", storeName));
			}
			if(status != null) {
				filters.add(Filter.eq("status", status));
			}
			if(applyStartTime != null) {
				filters.add(Filter.ge("applyTime", applyStartTime));
			}
			if(applyEndTime != null) {
				calendar.setTime(applyEndTime);
				calendar.add(Calendar.DATE, 1);
				applyEndTime = calendar.getTime();
				filters.add(Filter.le("applyTime", applyEndTime));
			}
			if(payStartTime != null) {
				filters.add(Filter.ge("payTime", payStartTime));
			}
			if(payEndTime != null) {
				calendar.setTime(payEndTime);
				calendar.add(Calendar.DATE, 1);
				payEndTime = calendar.getTime();
				filters.add(Filter.le("payTime", payEndTime));
			}
			
			 
//			if(store_type != null) {
//				filters.add(Filter.eq("store_type", store_type));
//			}
			
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("applyTime"));
			pageable.setOrders(orders);
			Page<WithDrawCash> page = withDrawCashService.findPage(pageable);
			
			if(page.getTotal() > 0){
				for(WithDrawCash withDrawCash : page.getRows()){
					HashMap<String,Object> comMap=new HashMap<String,Object>();
					
					comMap.put("id", withDrawCash.getId());
					comMap.put("storeId", withDrawCash.getStoreId());
					comMap.put("storeName", withDrawCash.getStoreName());
					comMap.put("cash", withDrawCash.getCash());
					comMap.put("status", withDrawCash.getStatus());
					comMap.put("applyTime", withDrawCash.getApplyTime());
					comMap.put("financeAuditTime", withDrawCash.getFinanceAuditTime());
					comMap.put("auditor", withDrawCash.getAuditor());
					comMap.put("payTime", withDrawCash.getPayTime());
					comMap.put("payer", withDrawCash.getPayer());
					comMap.put("rejectRemark", withDrawCash.getRejectRemark());
//					
//					/** 当前用户对本条数据的操作权限 */
//					if(creator.equals(admin)){
//				    	if(co==CheckedOperation.view){
//				    		comMap.put("privilege", "view");
//				    	}
//				    	else{
//				    		comMap.put("privilege", "edit");
//				    	}
//				    }
//				    else{
//				    	if(co==CheckedOperation.edit){
//				    		comMap.put("privilege", "edit");
//				    	}
//				    	else{
//				    		comMap.put("privilege", "view");
//				    	}
//				    }
					list.add(comMap);
				}
			}
			map.put("rows", list);
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));
		    map.put("total",Long.valueOf(page.getTotal()));
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(Collections.EMPTY_LIST);
			e.printStackTrace();
		}
		
		return json;
	}
	
	//财务列表显示
	@RequestMapping(value="/admin/storeAccountLog/withdraw_cash/audit/list/view")
	@ResponseBody
	public Json WithDrawCashAuditList(Pageable pageable, HttpSession session, String storeName, Integer status,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date applyStartTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date applyEndTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date payStartTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date payEndTime){
		Json json = new Json();
		try {
			Calendar calendar = Calendar.getInstance();
			
			
			
			
			
			
			List<HashMap<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			if(storeName != null) {
				filters.add(Filter.eq("storeName", storeName));
			}
			if(status != null) {
				if(status == 0) {
				filters.add(Filter.eq("status", status));
				}
				else if(status == 1){
					//>=1 <=2
					filters.add(Filter.ge("status", 1));
					filters.add(Filter.le("status", 2));
				}
				else {
					filters.add(Filter.eq("status", 3));
				}
			}
			if(applyStartTime != null) {
				filters.add(Filter.ge("applyTime", applyStartTime));
			}
			if(applyEndTime != null) {
				calendar.setTime(applyEndTime);
				calendar.add(Calendar.DATE, 1);
				applyEndTime = calendar.getTime();
				filters.add(Filter.le("applyTime", applyEndTime));
			}
			if(payStartTime != null) {
				filters.add(Filter.ge("payTime", payStartTime));
			}
			if(payEndTime != null) {
				calendar.setTime(payEndTime);
				calendar.add(Calendar.DATE, 1);
				payEndTime = calendar.getTime();
				filters.add(Filter.le("payTime", payEndTime));
			}
			
			 
//				if(store_type != null) {
//					filters.add(Filter.eq("store_type", store_type));
//				}
			
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("applyTime"));
			pageable.setOrders(orders);
			Page<WithDrawCash> page = withDrawCashService.findPage(pageable);
			
			if(page.getTotal() > 0){
				for(WithDrawCash withDrawCash : page.getRows()){
					HashMap<String,Object> comMap=new HashMap<String,Object>();
					
					comMap.put("id", withDrawCash.getId());
					comMap.put("storeId", withDrawCash.getStoreId());
					comMap.put("storeName", withDrawCash.getStoreName());
					comMap.put("cash", withDrawCash.getCash());
					comMap.put("status", withDrawCash.getStatus());
					comMap.put("applyTime", withDrawCash.getApplyTime());
					comMap.put("financeAuditTime", withDrawCash.getFinanceAuditTime());
					comMap.put("auditor", withDrawCash.getAuditor());
					comMap.put("payTime", withDrawCash.getPayTime());
					comMap.put("payer", withDrawCash.getPayer());
					comMap.put("rejectRemark", withDrawCash.getRejectRemark());
//						
//						/** 当前用户对本条数据的操作权限 */
//						if(creator.equals(admin)){
//					    	if(co==CheckedOperation.view){
//					    		comMap.put("privilege", "view");
//					    	}
//					    	else{
//					    		comMap.put("privilege", "edit");
//					    	}
//					    }
//					    else{
//					    	if(co==CheckedOperation.edit){
//					    		comMap.put("privilege", "edit");
//					    	}
//					    	else{
//					    		comMap.put("privilege", "view");
//					    	}
//					    }
					list.add(comMap);
				}
			}
			map.put("rows", list);
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));
		    map.put("total",Long.valueOf(page.getTotal()));
			
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
	
	//财务列表显示
	@RequestMapping(value="/admin/storeAccountLog/withdraw_cash/record/list/view")
	@ResponseBody
	public Json WithDrawCashRecordList(Pageable pageable, HttpSession session, String storeName, Integer status,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date applyStartTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date applyEndTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date payStartTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date payEndTime){
		
		//看提现记录的时候只能看到自己门店的提现记录
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		Json json = new Json();
		try {
			Calendar calendar = Calendar.getInstance();
			
			
			
			
			
			
			HyAdmin admin = hyAdminService.find(username);
			List<Filter> filters1 = new ArrayList<>();
			filters1.add(Filter.eq("hyAdmin", admin));
			//筛选出门店
			List<Store> stores = storeService.findList(null, filters1, null);
			Store store = stores.get(0);
			
			List<HashMap<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			
			filters.add(Filter.eq("storeId", store.getId()));
			
			
			if(status != null) {
				filters.add(Filter.eq("status", status));
			}
			if(applyStartTime != null) {
				filters.add(Filter.ge("applyTime", applyStartTime));
			}
			if(applyEndTime != null) {
				calendar.setTime(applyEndTime);
				calendar.add(Calendar.DATE, 1);
				applyEndTime = calendar.getTime();
				filters.add(Filter.le("applyTime", applyEndTime));
			}
			if(payStartTime != null) {
				filters.add(Filter.ge("payTime", payStartTime));
			}
			if(payEndTime != null) {
				calendar.setTime(payEndTime);
				calendar.add(Calendar.DATE, 1);
				payEndTime = calendar.getTime();
				filters.add(Filter.le("payTime", payEndTime));
			}
			
			 
//				if(store_type != null) {
//					filters.add(Filter.eq("store_type", store_type));
//				}
			
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("applyTime"));
			pageable.setOrders(orders);
			Page<WithDrawCash> page = withDrawCashService.findPage(pageable);
			
			if(page.getTotal() > 0){
				for(WithDrawCash withDrawCash : page.getRows()){
					HashMap<String,Object> comMap=new HashMap<String,Object>();
					
					comMap.put("id", withDrawCash.getId());
					comMap.put("storeId", withDrawCash.getStoreId());
					comMap.put("storeName", withDrawCash.getStoreName());
					comMap.put("cash", withDrawCash.getCash());
					comMap.put("status", withDrawCash.getStatus());
					comMap.put("applyTime", withDrawCash.getApplyTime());
					comMap.put("financeAuditTime", withDrawCash.getFinanceAuditTime());
					comMap.put("auditor", withDrawCash.getAuditor());
					comMap.put("payTime", withDrawCash.getPayTime());
					comMap.put("payer", withDrawCash.getPayer());
					comMap.put("rejectRemark", withDrawCash.getRejectRemark());
//						
//						/** 当前用户对本条数据的操作权限 */
//						if(creator.equals(admin)){
//					    	if(co==CheckedOperation.view){
//					    		comMap.put("privilege", "view");
//					    	}
//					    	else{
//					    		comMap.put("privilege", "edit");
//					    	}
//					    }
//					    else{
//					    	if(co==CheckedOperation.edit){
//					    		comMap.put("privilege", "edit");
//					    	}
//					    	else{
//					    		comMap.put("privilege", "view");
//					    	}
//					    }
					list.add(comMap);
				}
			}
			map.put("rows", list);
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));
		    map.put("total",Long.valueOf(page.getTotal()));
			
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
	
	@RequestMapping(value="/admin/storeAccountLog/withdraw_cash/insert")
	@ResponseBody
	public Json Insert(Integer cash, HttpSession session) {
		Json j = new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("hyAdmin", admin));
			List<Store> stores = storeService.findList(null, filters, null);
			Store store = stores.get(0);
			if(store == null) {
				j.setSuccess(false);
				j.setMsg("该账号下无门店！");
				return j;
			}
			List<Filter> filters2=new LinkedList<>();
			filters2.add(Filter.eq("store", store));
			List<StoreApplication> ans=storeApplicationService.findList(null, filters2, null);
			for(StoreApplication application : ans) {
				if(application.getType() == 4 && application.getApplicationStatus() >= 0 && application.getApplicationStatus() < 3) {
					j.setSuccess(false);
					j.setMsg("门店正在退出申请中，无法提现！");
					return j;
				}
			}
			WithDrawCash withDrawCash = new WithDrawCash();
			//未审核
			withDrawCash.setStatus(0);
			withDrawCash.setApplyTime(new Date());
			withDrawCash.setStoreName(store.getStoreName());
			withDrawCash.setStoreId(store.getId());
			withDrawCash.setCash(cash);
		
			withDrawCashService.save(withDrawCash);
			j.setSuccess(true);
			j.setMsg("提现申请已提交，待审核！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	//审核
	@RequestMapping(value="/admin/storeAccountLog/withdraw_cash/audit")
	@ResponseBody
	public Json Audit(Long id, HttpSession session) {
		Json j = new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			WithDrawCash withDrawCash = withDrawCashService.find(id);
			//已审核，待支付
			withDrawCash.setStatus(1);
			withDrawCash.setFinanceAuditTime(new Date());
			withDrawCash.setAuditor(admin.getName());
		
			withDrawCashService.update(withDrawCash);
			
			//通过withdrawcash找到门店
			Store store = storeService.find(withDrawCash.getStoreId());
			
			
			//预存款余额减少 相应值
			//预存款余额表
			// 3、修改门店预存款表      并发情况下的数据一致性！
			
			BigDecimal TiXian = new BigDecimal(withDrawCash.getCash()); 
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("store", store));
			List<StoreAccount> list = storeAccountService.findList(null, filters, null);
			if(list.size()!=0){
				StoreAccount storeAccount = list.get(0);
				storeAccount.setBalance(storeAccount.getBalance().subtract((TiXian)));
				storeAccountService.update(storeAccount);
			}else{			
				//此门店无预存款表
				j.setSuccess(false);
				j.setMsg("此门店没有预存款表，无法提现");
				return j;
			}
			
			// 4、修改门店预存款记录表
			StoreAccountLog storeAccountLog = new StoreAccountLog();
			storeAccountLog.setStatus(1);
			storeAccountLog.setCreateDate(new Date());
			storeAccountLog.setMoney(TiXian);
			storeAccountLog.setStore(store);
			//14门店提现
			storeAccountLog.setType(14);
			storeAccountLog.setProfile("门店提现");
			storeAccountLogService.update(storeAccountLog);
			
			// 5、修改 总公司-财务中心-门店预存款表
			StorePreSave storePreSave = new StorePreSave();
			storePreSave.setStoreName(store.getStoreName());
			storePreSave.setType(22); ////1:门店充值 2:报名退款  3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵 9:签证退款 10:酒店/酒加景销售 11:酒店/酒加景退款 12:门店后返 13:供应商驳回订单 14:
			//19保险退款 22门店提现
			storePreSave.setDate(new Date());
			storePreSave.setAmount(TiXian);
			storePreSave.setStoreId(store.getId());
			storePreSave.setPreSaveBalance(storeAccountService.findList(null, filters, null).get(0).getBalance());
			storePreSaveService.save(storePreSave);
			
			//像付款记录表中添加一项
			PayServicer payServicer = new PayServicer();
			payServicer.setReviewId(id);
			payServicer.setHasPaid(0); // 0:未付  1:已付
			// 1:分公司预付款 2:T+N 3:提前打款 4:旅游元素供应商尾款 5:向酒店/门票/酒加景供应商付款 6:江泰预充值 7:总公司预付款 8:门店提现
			payServicer.setType(8); 
			payServicer.setApplyDate(withDrawCash.getApplyTime());
			payServicer.setAppliName(withDrawCash.getStoreName());
			payServicer.setServicerId(withDrawCash.getStoreId()); 
			payServicer.setServicerName(withDrawCash.getStoreName());
			payServicer.setAmount(new BigDecimal(withDrawCash.getCash()));
			//payServicer.setRemark(hyPaymentSupplier.getRemark());
			
			BankList bankList = store.getBankList();
			
			payServicer.setBankListId(bankList.getId());  
			payServicer.setAccountName(bankList.getAccountName());
			payServicer.setBankName(bankList.getBankName());
			payServicer.setBankCode(bankList.getBankCode()); 
			payServicer.setBankType(bankList.getBankType() == false ? 1 : 0); //0:对私  1:对公  
			payServicer.setBankAccount(bankList.getBankAccount());
			
			payServicerService.save(payServicer);
			
			
			
			j.setSuccess(true);
			j.setMsg("审核成功，待付款！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	//审核
	@RequestMapping(value="/admin/storeAccountLog/withdraw_cash/reject")
	@ResponseBody
	public Json Reject(Long id, String rejectRemark, HttpSession session) {
		Json j = new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			WithDrawCash withDrawCash = withDrawCashService.find(id);
			//已驳回
			withDrawCash.setStatus(3);
			withDrawCash.setRejectRemark(rejectRemark);
			withDrawCash.setFinanceAuditTime(new Date());
			withDrawCash.setAuditor(admin.getName());
		
			withDrawCashService.update(withDrawCash);
			
			//add by wj 2019-07-07 提现驳回给门店负责人发送短信提醒
			Store store = storeService.find(withDrawCash.getStoreId());
			String phone = store.getHyAdmin().getMobile();
			SendMessageEMY.sendMessage(phone,"",17);
			
			j.setSuccess(true);
			j.setMsg("已驳回！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	//查看详情
	@RequestMapping(value="/admin/storeAccountLog/withdraw_cash/check")
	@ResponseBody
	public Json Check(Long id) {
		Json j = new Json();
		try{
			WithDrawCash withDrawCash = withDrawCashService.find(id);
			HashMap<String,Object> comMap=new HashMap<String,Object>();
			HashMap<String,HashMap<String,Object>> map = new HashMap<String,HashMap<String,Object>>();
			
			comMap.put("id", withDrawCash.getId());
			comMap.put("storeId", withDrawCash.getStoreId());
			comMap.put("storeName", withDrawCash.getStoreName());
			comMap.put("cash", withDrawCash.getCash());
			comMap.put("status", withDrawCash.getStatus());
			comMap.put("applyTime", withDrawCash.getApplyTime());
			comMap.put("financeAuditTime", withDrawCash.getFinanceAuditTime());
			comMap.put("auditor", withDrawCash.getAuditor());
			comMap.put("payTime", withDrawCash.getPayTime());
			comMap.put("payer", withDrawCash.getPayer());
			comMap.put("rejectRemark", withDrawCash.getRejectRemark());
			
			map.put("auditInformation", comMap);
//			HashMap<String,Object> payMap=new HashMap<String,Object>();
//			
//			payMap.put(key, value)
		
			j.setObj(comMap);
			j.setSuccess(true);
			j.setMsg("查看成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	//付款
	@RequestMapping(value="/admin/caiwu/withdraw_cash/pay")
	@ResponseBody
	public Json Pay(Long id, HttpSession session) {
		Json j = new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			//HyAdmin admin = hyAdminService.find(username);
			WithDrawCash withDrawCash = withDrawCashService.find(id);
			//已付款
			withDrawCash.setStatus(2);
			withDrawCash.setPayTime(new Date());
			withDrawCash.setPayer(username);
		
			withDrawCashService.update(withDrawCash);
			j.setSuccess(true);
			j.setMsg("付款成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
