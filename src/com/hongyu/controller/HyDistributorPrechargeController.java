package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.BankList;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDistributorManagement;
import com.hongyu.entity.HyDistributorPrechargeRecord;
import com.hongyu.entity.HyDistributorSettlement;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BankListService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDistributorManagementService;
import com.hongyu.service.HyDistributorPrechargeRecordService;
import com.hongyu.service.HyDistributorSettlementService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/ticket/distributor/channelPrecharge/")
public class HyDistributorPrechargeController {
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	
	@Resource(name="hyDistributorPrechargeRecordServiceImpl")
	HyDistributorPrechargeRecordService hyDistributorPrechargeRecordService;
	@Resource(name="hyDistributorSettlementServiceImpl")
	HyDistributorSettlementService hyDistributorSettlementService;
	@Resource(name="hyDistributorManagementServiceImpl")
	HyDistributorManagementService hyDistributorManagementService;
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	@Resource(name = "bankListServiceImpl")
	BankListService  bankListService;
	
	@RequestMapping(value="accountList")
	@ResponseBody
	public Json accountList()
	{
		Json json=new Json();
		try{
		    List<Order> orders = new ArrayList<Order>();
	        orders.add(Order.asc("id"));
		    List<BankList> list=this.bankListService.findList(null,null,orders);
		    List<HashMap<String,Object>> obj=new ArrayList<HashMap<String,Object>>();
		    for(BankList bankList:list)
		    {
			    HashMap<String, Object> map=new HashMap<String, Object>();
			    map.put("id",bankList.getId());
			    map.put("account", bankList.getBankAccount());
			    map.put("accountName", bankList.getAccountName());
			    map.put("bankName", bankList.getBankName());
			    map.put("alias",bankList.getAlias());
			    map.put("bankCode", bankList.getBankCode());
			    map.put("accountType", bankList.getYhlx());
			    obj.add(map);
		    }
		    json.setSuccess(true);
	        json.setMsg("列表成功");
	        json.setObj(obj);
		}
	    catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	
	@RequestMapping(value="charge", method = RequestMethod.POST)
	@ResponseBody
	public Json charge(HyDistributorPrechargeRecord record,Long bankId,Long managementId,HttpSession httpSession)
	{
		Json json=new Json();
		try{			
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("distributorPrechargePro");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 分销商充值申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
		   
		    BankList bankList=bankListService.find(bankId);
		    record.setBankList(bankList);
		    record.setCheckStatus(1);
		    HyDistributorManagement distributorManagement=hyDistributorManagementService.find(managementId);
		    record.setDistributor(distributorManagement);
		    record.setBalance(distributorManagement.getPrechargeBalance());
		    record.setProcessInstanceId(pi.getProcessInstanceId());
		    record.setOperator(admin);
		    hyDistributorPrechargeRecordService.save(record);	
			json.setSuccess(true);
		    json.setMsg("充值提交成功！");
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	

	@RequestMapping(value="list/view", method = RequestMethod.POST)
	@ResponseBody
	public Json listview(Pageable pageable, HyDistributorManagement queryParam,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try{
			Map<String,Object> map=new HashMap<String,Object>();
			List<HashMap<String, Object>> list = new ArrayList<>();
			
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co=(CheckedOperation) request.getAttribute("co");
			
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			
			List<Filter> filters = new ArrayList<Filter>();
			
			filters.add(Filter.in("creator", hyAdmins));
			
			pageable.setFilters(filters);
			Page<HyDistributorManagement> page=this.hyDistributorManagementService.findPage(pageable, queryParam);
			if(page.getTotal()>0){
			    for(HyDistributorManagement distributorManagement:page.getRows())
			    {
				    HashMap<String,Object> shMap=new HashMap<String,Object>();
				    HyAdmin creator=distributorManagement.getCreator();
				    shMap.put("id",distributorManagement.getId());
				    shMap.put("name",distributorManagement.getName());
				    shMap.put("principal",distributorManagement.getPrincipal());
				    shMap.put("telephone",distributorManagement.getTelephone());
				    shMap.put("address",distributorManagement.getAddress());
				    shMap.put("remark",distributorManagement.getRemark());
				    shMap.put("balance",distributorManagement.getPrechargeBalance());
				    if(distributorManagement.getCreator()!=null){
				    	shMap.put("creator", creator.getName());
				    }
				    /** 当前用户对本条数据的操作权限 */
				    if(creator.equals(admin)){
				    	if(co==CheckedOperation.view){
				    		shMap.put("privilege", "view");
				    	}
				    	else{
				    		shMap.put("privilege", "edit");
				    	}
				    }
				    else{
				    	if(co==CheckedOperation.edit){
				    		shMap.put("privilege", "edit");
				    	}
				    	else{
				    		shMap.put("privilege", "view");
				    	}
				    }
				    list.add(shMap);
			    }
		    }
			map.put("rows", list);
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));
		    map.put("total",Long.valueOf(page.getTotal()));
		    json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="prechargeRecord/list", method = RequestMethod.POST)
	@ResponseBody
	public Json preRecordList(Long distributorId,Pageable pageable,HyDistributorPrechargeRecord queryParam)
	{
		Json json=new Json();
		try{
			HyDistributorManagement management=hyDistributorManagementService.find(distributorId);
			List<Filter> filters=new ArrayList<Filter>();
		    Filter filter=new Filter("distributor",Operator.eq,management);
		    filters.add(filter);
		    pageable.setFilters(filters);
		    List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("chargeDate"));
			pageable.setOrders(orders);
		    Page<HyDistributorPrechargeRecord> page=hyDistributorPrechargeRecordService.findPage(pageable, queryParam);
		    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		    if(page.getTotal()>0)
		    {
		        for(HyDistributorPrechargeRecord record:page.getRows())
		        {
			        Map<String, Object> map = new HashMap<String, Object>();
			        map.put("id", record.getId());
			        map.put("chargeDate", record.getChargeDate());
			        map.put("chargeMoney", record.getChargeMoney());
			        map.put("chargeType", record.getChargeType());
			        map.put("checkStatus", record.getCheckStatus());
			        list.add(map);
		        }
		    }
		    Page<Map<String,Object>> prechargePage=new Page<Map<String,Object>>(list,page.getTotal(),pageable);	
		    json.setMsg("查询成功");
	        json.setSuccess(true);
	        json.setObj(prechargePage);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="tradeRecord/list", method = RequestMethod.POST)
	@ResponseBody
	public Json settleRecordList(Long distributorId,Pageable pageable,@DateTimeFormat(pattern="yyyy-MM-dd") Date startTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime,Integer summary)
	{
		Json json=new Json();
		try{
			HyDistributorManagement management=hyDistributorManagementService.find(distributorId);
			if(summary!=null)
			{
			    if(summary==1) //charge
			    {
				    HyDistributorPrechargeRecord prechargeRecord=new HyDistributorPrechargeRecord();
				    List<Filter> filters=new ArrayList<Filter>();
				    Filter filter=new Filter("distributor",Operator.eq,management);
				    filters.add(filter);
				    filters.add(new Filter("checkStatus",Operator.eq,2)); //审核通过
				    if (startTime != null) {
					    filters.add(new Filter("chargeDate", Operator.ge, DateUtil.getStartOfDay(startTime)));
				    }
				    if (endTime != null) {
					    filters.add(new Filter("chargeDate", Operator.le, DateUtil.getEndOfDay(endTime)));
				    }
				    pageable.setFilters(filters);
				    Page<HyDistributorPrechargeRecord> page=hyDistributorPrechargeRecordService.findPage(pageable, prechargeRecord);
				    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				    if(page.getTotal()>0)
				    {
				        for(HyDistributorPrechargeRecord record:page.getRows())
				        {
					        Map<String, Object> map = new HashMap<String, Object>();
					        map.put("tradeDate", record.getChargeDate());
					        map.put("summary", 1);
					        map.put("type", record.getChargeType());
					        map.put("tradeMoney", record.getChargeMoney());
					        map.put("balance", record.getDistributor().getPrechargeBalance());
					        list.add(map);
				        }
				    }
				    Page<Map<String,Object>> prechargePage=new Page<Map<String,Object>>(list,page.getTotal(),pageable);	
			        json.setMsg("查询成功");
		            json.setSuccess(true);
		            json.setObj(prechargePage);
			    }
			    else if(summary==2) //settlement
			    {
				    HyDistributorSettlement settlement=new HyDistributorSettlement();
				    List<Filter> filters=new ArrayList<Filter>();
				    Filter filter=new Filter("distributor",Operator.eq,management);
				    filters.add(filter);
				    filters.add(new Filter("checkStatus",Operator.eq,2));
				    if (startTime != null) {
					    filters.add(new Filter("payDate", Operator.ge, DateUtil.getStartOfDay(startTime)));
				    }
				    if (endTime != null) {
				     	filters.add(new Filter("payDate", Operator.le, DateUtil.getEndOfDay(endTime)));
				    }
				    pageable.setFilters(filters);
				    Page<HyDistributorSettlement> page=hyDistributorSettlementService.findPage(pageable, settlement);
				    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				    if(page.getTotal()>0)
				    {
				        for(HyDistributorSettlement settle:page.getRows())
				        {
					        Map<String, Object> map = new HashMap<String, Object>();
					        map.put("tradeDate", settle.getPayDate());
					        map.put("summary", 2);
					        map.put("type", 4);
					        map.put("tradeMoney", settle.getMoney());
					        map.put("balance", settle.getDistributor().getPrechargeBalance());
					        list.add(map);
				        }
				    }
				    Page<Map<String,Object>> settlePage=new Page<Map<String,Object>>(list,page.getTotal(),pageable);	
			        json.setMsg("查询成功");
		            json.setSuccess(true);
		            json.setObj(settlePage);
			    }		
		    }
			else{
				    List<Filter> filters=new ArrayList<Filter>();
				    Filter filter=new Filter("distributor",Operator.eq,management);
				    filters.add(filter);
				    filters.add(new Filter("checkStatus",Operator.eq,2));
				    if (startTime != null) {
					    filters.add(new Filter("chargeDate", Operator.ge, DateUtil.getStartOfDay(startTime)));
				    }
				    if (endTime != null) {
					    filters.add(new Filter("chargeDate", Operator.le, DateUtil.getEndOfDay(endTime)));
				    }
				    List<HyDistributorPrechargeRecord> prechargeList=hyDistributorPrechargeRecordService.findList(null,filters,null);
				    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				    if(prechargeList.size()>0)
				    {
				        for(HyDistributorPrechargeRecord record:prechargeList)
			            {
				            Map<String, Object> map = new HashMap<String, Object>();
				            map.put("tradeDate", record.getChargeDate());
				            map.put("summary", 1);
				            map.put("type", record.getChargeType());
				            map.put("tradeMoney", record.getChargeMoney());
				            map.put("balance", record.getDistributor().getPrechargeBalance());
				            list.add(map);
			            }
				    }				    
				    List<Filter> settleFilters=new ArrayList<Filter>();
				    Filter settFilter=new Filter("distributor",Operator.eq,management);
				    settleFilters.add(settFilter);
				    settleFilters.add(new Filter("checkStatus",Operator.eq,2));
				    if (startTime != null) {
				    	settleFilters.add(new Filter("payDate", Operator.ge, DateUtil.getStartOfDay(startTime)));
				    }
				    if (endTime != null) {
				    	settleFilters.add(new Filter("payDate", Operator.le, DateUtil.getEndOfDay(endTime)));
				    }
				    List<HyDistributorSettlement> settleList=hyDistributorSettlementService.findList(null,settleFilters,null);
				    if(settleList.size()>0)
				    {
				        for(HyDistributorSettlement settlement:settleList)
			            {
				            Map<String, Object> map = new HashMap<String, Object>();
				            map.put("tradeDate", settlement.getPayDate());
				            map.put("summary", 2);
				            map.put("type", 4);
				            map.put("tradeMoney", settlement.getMoney());
				            map.put("balance", settlement.getDistributor().getPrechargeBalance());
				            list.add(map);
			            }
				    }		
				    Collections.sort(list, new Comparator<Map<String, Object>>() {
						@Override
						public int compare(Map<String, Object> o1, Map<String, Object> o2) {
							Date date1 = (Date) o1.get("tradeDate");
							Date date2 = (Date) o2.get("tradeDate");
							return date1.compareTo(date2); 
						}
					});
				    Collections.reverse(list);
				    Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);	
				    json.setMsg("查询成功");
		            json.setSuccess(true);
		            json.setObj(page);
			}
		}	
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="detail/view", method = RequestMethod.GET)
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try{
			HyDistributorPrechargeRecord hyDistributorPrechargeRecord=hyDistributorPrechargeRecordService.find(id);
			//List<HashMap<String, Object>> obj = new ArrayList<HashMap<String, Object>>();
			Map<String, Object> map = new HashMap<>();
		    List<Map<String, Object>> list = new LinkedList<>();
		    map.put("step", "财务审核");
		    map.put("auditor", hyDistributorPrechargeRecord.getAuditor().getName());
		    map.put("checkComment", hyDistributorPrechargeRecord.getCheckComment());
		    map.put("checkDate",hyDistributorPrechargeRecord.getCheckDate());
		    map.put("checkStatus",hyDistributorPrechargeRecord.getCheckStatus());
		    list.add(map);
		    HashMap<String, Object> obj = new HashMap<>();
		    obj.put("checkList", list);
		    obj.put("chargeMoney", hyDistributorPrechargeRecord.getChargeMoney());
		    obj.put("chargeType", hyDistributorPrechargeRecord.getChargeType());
		    obj.put("chargeDate", hyDistributorPrechargeRecord.getChargeDate());
		    obj.put("account", hyDistributorPrechargeRecord.getBankList().getBankAccount());
		    obj.put("accountName", hyDistributorPrechargeRecord.getBankList().getAccountName());
		    obj.put("bankName", hyDistributorPrechargeRecord.getBankList().getBankName());
		    obj.put("alias",hyDistributorPrechargeRecord.getBankList().getAlias());
		    obj.put("bankCode", hyDistributorPrechargeRecord.getBankList().getBankCode());
		    obj.put("accountType", hyDistributorPrechargeRecord.getBankList().getYhlx());
		    obj.put("remark", hyDistributorPrechargeRecord.getRemark());
		    //obj.add(map);
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(obj);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
//	@RequestMapping(value="modify")
//	@ResponseBody
//	public Json modify(HyDistributorPrechargeRecord hyDistributorPrechargeRecord,Long id,Long bankId,HttpSession httpSession)
//	{
//		Json json=new Json();
//		try{		
//			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
//			HyAdmin admin = hyAdminService.find(username);
//			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("distributorPrechargePro");
//			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
//			// 完成 分销商充值申请
//			Authentication.setAuthenticatedUserId(username);
//			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :2"); 
//			taskService.complete(task.getId());
//			
//			hyDistributorPrechargeRecord.setId(id);
//		    BankList bankList=bankListService.find(bankId);
//		    hyDistributorPrechargeRecord.setBankList(bankList);
//		    hyDistributorPrechargeRecord.setCheckStatus(1);
//		    //  Integer balance=distributorManagement.getPrechargeBalance()+record.getChargeMoney();
//		    hyDistributorPrechargeRecord.setProcessInstanceId(pi.getProcessInstanceId());
//		    hyDistributorPrechargeRecord.setOperator(admin);
//		    hyDistributorPrechargeRecordService.update(hyDistributorPrechargeRecord,"distributor","balance");	
//			json.setSuccess(true);
//			json.setMsg("修改成功！");			
//			
//		}
//		catch(Exception e)
//		{
//			json.setSuccess(false);
//			json.setMsg(e.getMessage());
//		}
//		return json;
//	}
//	
//	@RequestMapping(value="delete")
//	@ResponseBody
//	public Json delete(Long id)
//	{
//		Json json=new Json();
//		try{
//			id=6L;
//			hyDistributorPrechargeRecordService.delete(id);
//			json.setSuccess(true);
//		    json.setMsg("删除成功！");
//		}
//		catch(Exception e){
//			json.setSuccess(false);
//			json.setMsg(e.getMessage());
//		}
//		return json;
//	}
}
