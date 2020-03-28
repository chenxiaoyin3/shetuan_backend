package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
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
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyCreditFhy;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HyStoreFhynew;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyCreditFhyService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.HyStoreFhynewService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/storefhy/")
public class StoreFhyManagementController {
	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name="hyStoreFhynewServiceImpl")
	private HyStoreFhynewService hyStoreFhynewService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	@Resource(name="hyRoleServiceImpl")
	private HyRoleService hyRoleService;
	@Resource(name="hyCreditFhyServiceImpl")
	private HyCreditFhyService hyCreditFhyService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable,HyStoreFhynew queryParam,@DateTimeFormat(pattern="yyyy-MM-dd") Date startTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime,HttpSession session,HttpServletRequest request)
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
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.in("applyName",hyAdmins));
			if (startTime != null && !startTime.equals("")) {
			    filters.add(new Filter("createTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
		    }
		    if (endTime != null && !endTime.equals("")) {
			    filters.add(new Filter("createTime", Operator.le, DateUtil.getEndOfDay(endTime)));
		    }
		    pageable.setFilters(filters);
		    List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
		    Page<HyStoreFhynew> page=hyStoreFhynewService.findPage(pageable, queryParam);
		    if(page.getTotal()>0){
		    	for(HyStoreFhynew storeFhy:page.getRows()){
		    		HashMap<String,Object> storeMap=new HashMap<String,Object>();
		    		HyAdmin creator=storeFhy.getApplyName();
		    		storeMap.put("id",storeFhy.getId());
		    		storeMap.put("name", storeFhy.getName());
		    		storeMap.put("person", storeFhy.getPerson().getName());
		    		storeMap.put("type", storeFhy.getType());
		    		storeMap.put("province", storeFhy.getArea().getHyArea().getHyArea().getName());
		    		storeMap.put("city", storeFhy.getArea().getHyArea().getName());
		    		storeMap.put("createTime", storeFhy.getCreateTime());
		    		storeMap.put("creator", storeFhy.getApplyName().getName());
		    		storeMap.put("auditStatus", storeFhy.getAuditStatus());
		    		storeMap.put("creditMoney", storeFhy.getCreditMoney());
		    		storeMap.put("isCancel", storeFhy.getIsCancel());
		    		//write by lbc
		    		storeMap.put("isLock", storeFhy.getPerson().getIsEnabled());
		    		/** 当前用户对本条数据的操作权限 */
				    if(creator.equals(admin)){
				    	if(co==CheckedOperation.view){
				    		storeMap.put("privilege", "view");
				    	}
				    	else{
				    		storeMap.put("privilege", "edit");
				    	}
				    }
				    else{
				    	if(co==CheckedOperation.edit){
				    		storeMap.put("privilege", "edit");
				    	}
				    	else{
				    		storeMap.put("privilege", "view");
				    	}
				    }
				    list.add(storeMap);
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
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(HyStoreFhynew hyStoreFhynew,Long areaId,Long roleId,String personName,String personAccount,String mobilephone,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("storefhyProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成非虹宇门店创建提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			hyStoreFhynew.setApplyName(admin);
			hyStoreFhynew.setCreateTime(new Date());
			hyStoreFhynew.setIsCancel(true);
			hyStoreFhynew.setApplyTime(new Date());
			hyStoreFhynew.setProcessInstanceId(pi.getProcessInstanceId());
			hyStoreFhynew.setAuditStatus(1);
			hyStoreFhynew.setCreditMoney(new BigDecimal(0));
			hyStoreFhynew.setMoney(new BigDecimal(0));
			HyArea hyArea=hyAreaService.find(areaId);
			hyStoreFhynew.setArea(hyArea);
			HyRole role=hyRoleService.find(roleId);
			Department department=admin.getDepartment();
			HyAdmin hyAdmin=new HyAdmin();
			hyAdmin.setUsername(personAccount);
			hyAdmin.setName(personName);
			hyAdmin.setMobile(mobilephone);
			hyAdmin.setIsEnabled(false);
			hyAdmin.setCreateDate(new Date());
			hyAdmin.setDepartment(department);
			hyAdmin.setRole(role);
			hyAdminService.save(hyAdmin);
			hyStoreFhynew.setPerson(hyAdmin);
			hyStoreFhynewService.save(hyStoreFhynew);
			json.setMsg("添加成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
	    return json;
	}
	
	@RequestMapping(value="edit", method = RequestMethod.POST)
	@ResponseBody
	public Json edit(HyStoreFhynew hyStoreFhynew,Long storeId,Long areaId,Long roleId,String personName,String mobilephone,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HyStoreFhynew storenew=hyStoreFhynewService.find(storeId);
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("storefhyProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成非虹宇门店创建提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			hyStoreFhynew.setId(storeId);
			hyStoreFhynew.setApplyName(admin);
			hyStoreFhynew.setIsCancel(true);
			hyStoreFhynew.setApplyTime(new Date());
			hyStoreFhynew.setProcessInstanceId(pi.getProcessInstanceId());
			hyStoreFhynew.setAuditStatus(1);
			HyArea hyArea=hyAreaService.find(areaId);
			hyStoreFhynew.setArea(hyArea);
			HyRole role=hyRoleService.find(roleId);
			Department department=admin.getDepartment();
			HyAdmin hyAdmin=storenew.getPerson();
			String personAccount=hyAdmin.getUsername();
			hyAdmin.setUsername(personAccount);
			hyAdmin.setName(personName);
			hyAdmin.setMobile(mobilephone);
			hyAdmin.setIsEnabled(false);
			hyAdmin.setCreateDate(new Date());
			hyAdmin.setDepartment(department);
			hyAdmin.setRole(role);
			hyAdmin.setModifyDate(new Date());
			hyAdminService.update(hyAdmin,"password","createDate","department","role");
			hyStoreFhynew.setPerson(hyAdmin);
			hyStoreFhynewService.update(hyStoreFhynew,"creditMoney","money","createTime");
			/*改变门店类型,从授信改成现付*/
			if(storenew.getType()==1 && hyStoreFhynew.getType()==2){
				HyCreditFhy hyCreditFhy=new HyCreditFhy();
				hyCreditFhy.setPaymentType(2);
				hyCreditFhy.setApplyTime(new Date());
				hyCreditFhy.setMoney(new BigDecimal(0));
				hyCreditFhy.setAuditStatus(2);
				hyCreditFhy.setHyStoreFhynew(hyStoreFhynew);
				hyCreditFhyService.save(hyCreditFhy);
			}
			json.setMsg("编辑成功");
		    json.setSuccess(true);
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
			HyStoreFhynew storeFhy=hyStoreFhynewService.find(id);
			HashMap<String,Object> storeMap=new HashMap<String,Object>();
    		storeMap.put("name", storeFhy.getName());
    		storeMap.put("type", storeFhy.getType());
    		storeMap.put("area", storeFhy.getArea().getFullName());
    		storeMap.put("areaId", storeFhy.getArea().getId());
    		storeMap.put("address", storeFhy.getAddress());
    		storeMap.put("creditMoney", storeFhy.getCreditMoney());
    		storeMap.put("xinyongdaima", storeFhy.getXinyongdaima());
    		storeMap.put("xydmUrl", storeFhy.getXydmUrl());
    		storeMap.put("personName", storeFhy.getPerson().getName());
    		storeMap.put("mobilephone", storeFhy.getPerson().getMobile());
    		storeMap.put("personAccount", storeFhy.getPerson().getUsername());
    		storeMap.put("roleName", storeFhy.getPerson().getRole().getName());
    		storeMap.put("roleId", storeFhy.getPerson().getRole().getId());
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(storeMap);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="roleList")
	@ResponseBody
	public Json roleList(HttpSession httpSession)
	{
		Json json=new Json();
		try{
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			HyRole hyRole=admin.getRole();
			Set<HyRole> hyRoleSet=hyRole.getHyRolesForSubroles();
			List<HyRole> roleList=new ArrayList<HyRole>(hyRoleSet);
			List<HashMap<String,Object>> obj=new ArrayList<HashMap<String,Object>>();
			for(HyRole role:roleList)
			{
				 HashMap<String, Object> map=new HashMap<String, Object>();
				 map.put("roleId", role.getId());
				 map.put("roleName", role.getName());
				 obj.add(map);
			}
			json.setMsg("列表成功");
		    json.setSuccess(true);
		    json.setObj(obj);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 由父区域的ID得到全部的子区域
	 * @param id
	 * @return
	 */
	@RequestMapping(value="areacomboxlist/view", method = RequestMethod.GET)
	@ResponseBody
	public Json getSubAreas(Long id) {
		Json j = new Json();
		try {
			HashMap<String, Object> hashMap = new HashMap<>();
			HyArea parent = hyAreaService.find(id);
			List<HashMap<String, Object>> obj = new ArrayList<>();
			if(parent != null && parent.getHyAreas().size() > 0) {
				for (HyArea child : parent.getHyAreas()) {
					if(child.getStatus()) {
						HashMap<String, Object> hm = new HashMap<>();
						hm.put("value", child.getId());
						hm.put("label", child.getName());
						hm.put("isLeaf", child.getHyAreas().size() == 0);
						obj.add(hm);
					}
				}
			}
			hashMap.put("total", parent.getHyAreas().size());
			hashMap.put("data", obj);
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(obj);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping("cancel")
	@ResponseBody
	public Json cancel(Long id)
	{
		Json json=new Json();
		try{
			HyStoreFhynew hyStoreFhynew=hyStoreFhynewService.find(id);
			hyStoreFhynew.setIsCancel(false);
			hyStoreFhynewService.update(hyStoreFhynew);
			json.setMsg("取消成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//write by lbc
	@RequestMapping("lock_login")
	@ResponseBody
	public Json lockLogin(Long id)
	{
		Json json=new Json();
		try{
			HyStoreFhynew hyStoreFhynew=hyStoreFhynewService.find(id);
			//hyStoreFhynew.setIsCancel(false);
			//锁定登录
			HyAdmin hyAdmin = hyStoreFhynew.getPerson();
			hyAdmin.setIsEnabled(false);
			hyAdminService.update(hyAdmin);
			hyStoreFhynew.setPerson(hyAdmin);
			hyStoreFhynewService.update(hyStoreFhynew);
			
			json.setMsg("锁定登录成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//write by lbc
	@RequestMapping("unlock_login")
	@ResponseBody
	public Json unlockLogin(Long id)
	{
		Json json=new Json();
		try{
			HyStoreFhynew hyStoreFhynew=hyStoreFhynewService.find(id);
			//hyStoreFhynew.setIsCancel(true);
			//解锁登录
			HyAdmin hyAdmin = hyStoreFhynew.getPerson();
			hyAdmin.setIsEnabled(true);
			hyAdminService.update(hyAdmin);
			hyStoreFhynew.setPerson(hyAdmin);
			hyStoreFhynewService.update(hyStoreFhynew);
			json.setMsg("解锁登录成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping("restore")
	@ResponseBody
	public Json restore(Long id)
	{
		Json json=new Json();
		try{
			HyStoreFhynew hyStoreFhynew=hyStoreFhynewService.find(id);
			hyStoreFhynew.setIsCancel(true);
			hyStoreFhynewService.update(hyStoreFhynew);
			json.setMsg("恢复成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="credit",method = RequestMethod.POST)
	@ResponseBody
	public Json credit(Long storeId,BigDecimal creditMoney,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			HyStoreFhynew hyStoreFhynew=hyStoreFhynewService.find(storeId);
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			HyCreditFhy hyCreditFhy=new HyCreditFhy();
			hyCreditFhy.setHyStoreFhynew(hyStoreFhynew);
			hyCreditFhy.setMoney(creditMoney);
			hyCreditFhy.setAuditStatus(1); //提交审核
			hyCreditFhy.setApplyTime(new Date());
			hyCreditFhy.setApplyName(admin);
			hyCreditFhy.setPaymentType(1);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("creditfhyProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成非虹宇门店授信提交申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId());
			hyCreditFhy.setProcessInstanceId(pi.getProcessInstanceId());
			hyCreditFhyService.save(hyCreditFhy);
			json.setMsg("授信成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="creditRecord/view")
	@ResponseBody
	public Json creditRecord(Long storeId)
	{
		Json json=new Json();
		try{
			HyStoreFhynew hyStoreFhynew=hyStoreFhynewService.find(storeId);
			List<HashMap<String, Object>> list = new ArrayList<>();
			List<HyCreditFhy> creditRecordLists=new ArrayList<>(hyStoreFhynew.getHyCreditFhys());
			for(HyCreditFhy creditFhy:creditRecordLists){
				HashMap<String, Object> map=new HashMap<String, Object>();
				map.put("id",creditFhy.getId());
				map.put("paymentType", creditFhy.getPaymentType());
				map.put("applyName", creditFhy.getApplyName().getName());
				map.put("applyTime", creditFhy.getApplyTime());
				map.put("money", creditFhy.getMoney());
				map.put("auditStatus", creditFhy.getAuditStatus());
				if(creditFhy.getPaymentType()==1){
					String processInstanceId=creditFhy.getProcessInstanceId();
					List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
					Comment comment=commentList.get(0);
					String str = comment.getFullMessage();
					int index = str.lastIndexOf(":");
					if (index < 0) {
						map.put("comment", " ");
					} else {
						map.put("comment", str.substring(0, index));
					}
				}
				else if(creditFhy.getPaymentType()==2){
					map.put("comment", "免审通过");
				}
				list.add(map);
			}
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(list);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
