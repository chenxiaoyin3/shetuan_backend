package com.hongyu.controller;

import java.util.ArrayList;
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
import com.hongyu.entity.HyDistributorSettlement;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BankListService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDistributorManagementService;
import com.hongyu.service.HyDistributorSettlementService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/ticket/distributor/channelSettlement/")
public class HyDistributorSettlementController {
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	
	@Resource(name="hyDistributorSettlementServiceImpl")
	HyDistributorSettlementService hyDistributorSettlementService;
	@Resource(name="hyDistributorManagementServiceImpl")
	HyDistributorManagementService hyDistributorManagementService;
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	@Resource(name = "bankListServiceImpl")
	BankListService  bankListService;
	
	
	@RequestMapping(value="nameList")
	@ResponseBody
	public Json nameList()
	{
		Json json=new Json();
		try{
		    List<Order> orders = new ArrayList<Order>();
	        orders.add(Order.asc("id"));
		    List<HyDistributorManagement> list=this.hyDistributorManagementService.findList(null,null,orders);
		    List<HashMap<String,Object>> obj=new ArrayList<HashMap<String,Object>>();
		    for(HyDistributorManagement distributor:list)
		    {
		    	HashMap<String, Object> map=new HashMap<String, Object>();
			    map.put("id",distributor.getId());
			    map.put("name", distributor.getName());
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
	
	

	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(HyDistributorSettlement hyDistributorSettlement,Long bankId,Long distributorId,HttpSession httpSession)
	{
		Json json=new Json();
		try{
		        String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
				HyAdmin admin = hyAdminService.find(username);
				ProcessInstance pi  = runtimeService.startProcessInstanceByKey("distributorSettlementPro");
				Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
				// 完成 分销商充值申请
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
				taskService.complete(task.getId());
				
			    BankList bankList=bankListService.find(bankId);
			    HyDistributorManagement distributorManagement=hyDistributorManagementService.find(distributorId);
			    if(distributorManagement.getSettleType()==1){
			    	hyDistributorSettlement.setChargeBalance(distributorManagement.getPrechargeBalance());
			    }
			    hyDistributorSettlement.setBankList(bankList);
			    hyDistributorSettlement.setDistributor(distributorManagement);
			    hyDistributorSettlement.setCheckStatus(1);
			    hyDistributorSettlement.setProcessInstanceId(pi.getProcessInstanceId());
			    hyDistributorSettlement.setOperator(admin);
			    hyDistributorSettlementService.save(hyDistributorSettlement);
			    json.setSuccess(true);
			    json.setMsg("添加成功！");
		}
		catch(Exception e)
		{
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="list/view", method = RequestMethod.POST)
	@ResponseBody
	public Json listview(Pageable pageable, Integer checkStatus,String distributorName,@DateTimeFormat(pattern="yyyy-MM-dd") Date startTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try{
			HyDistributorSettlement hyDistributorSettlement=new HyDistributorSettlement();
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
			if(checkStatus != null)
			{
				filters.add(new Filter("checkStatus",Operator.eq,checkStatus));
			}
			if(distributorName!=null&&!distributorName.equals(""))
			{
				Filter filte=new Filter("name",Operator.like,distributorName);
				List<Filter> fil = new ArrayList<Filter>();
				fil.add(filte);
				List<HyDistributorManagement> distributorList=hyDistributorManagementService.findList(null,fil,null);
				filters.add(new Filter("distributor",Operator.in,distributorList));
			}
			filters.add(Filter.in("operator", hyAdmins));
			if (startTime != null) {
				filters.add(new Filter("payDate", Operator.ge, DateUtil.getStartOfDay(startTime)));
			}
			if (endTime != null) {
				filters.add(new Filter("payDate", Operator.le, DateUtil.getEndOfDay(endTime)));
			}
			
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("payDate"));
			pageable.setOrders(orders);
			Page<HyDistributorSettlement> page=this.hyDistributorSettlementService.findPage(pageable, hyDistributorSettlement);
			if(page.getTotal()>0){
			    for(HyDistributorSettlement distributorSettlement:page.getRows())
			    {
				    HashMap<String,Object> shMap=new HashMap<String,Object>();
				    HyAdmin operator=distributorSettlement.getOperator();
				    shMap.put("id",distributorSettlement.getId());
				    shMap.put("distributorName",distributorSettlement.getDistributor().getName());
				    shMap.put("payDate",distributorSettlement.getPayDate());
				    shMap.put("startDate",distributorSettlement.getStartDate());
				    shMap.put("endDate",distributorSettlement.getEndDate());
				    shMap.put("money",distributorSettlement.getMoney());
				    shMap.put("checkStatus",distributorSettlement.getCheckStatus());
				    if(distributorSettlement.getOperator()!=null){
				    	shMap.put("operator", operator.getName());
				    }
				    /** 当前用户对本条数据的操作权限 */
				    if(operator.equals(admin)){
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
	
	@RequestMapping(value="detail/view", method = RequestMethod.GET)
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try{
			HyDistributorSettlement hyDistributorSettlement=hyDistributorSettlementService.find(id);
			Map<String, Object> map = new HashMap<>();
		    List<Map<String, Object>> list = new LinkedList<>();
		    map.put("step", "财务审核");
		    map.put("auditor", hyDistributorSettlement.getAuditor().getName());
		    map.put("checkComment", hyDistributorSettlement.getComment());
		    map.put("checkTime",hyDistributorSettlement.getCheckTime());
		    map.put("checkStatus",hyDistributorSettlement.getCheckStatus());
		    list.add(map);
		    HashMap<String, Object> obj = new HashMap<>();
		    obj.put("checkList", list);
		    obj.put("distributorName",hyDistributorSettlement.getDistributor().getName());
		    obj.put("money", hyDistributorSettlement.getMoney());
		    obj.put("startDate", hyDistributorSettlement.getStartDate());
		    obj.put("endDate", hyDistributorSettlement.getEndDate());
		    obj.put("payDate", hyDistributorSettlement.getPayDate());
		    obj.put("account", hyDistributorSettlement.getBankList().getBankAccount());
		    obj.put("accountName", hyDistributorSettlement.getBankList().getAccountName());
		    obj.put("bankName", hyDistributorSettlement.getBankList().getBankName());
		    obj.put("alias",hyDistributorSettlement.getBankList().getAlias());
		    obj.put("bankCode", hyDistributorSettlement.getBankList().getBankCode());
		    obj.put("accountType", hyDistributorSettlement.getBankList().getYhlx());
		    obj.put("remark", hyDistributorSettlement.getRemark());
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
}
