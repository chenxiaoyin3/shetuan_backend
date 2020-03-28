package com.hongyu.controller.gdw;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.CommonUploadFileEntity;
import com.hongyu.entity.BankList.BankType;
import com.hongyu.entity.BankList.Yinhangleixing;
import com.hongyu.entity.CommonUploadFileEntity.UploadTypeEnum;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreApplication;
import com.hongyu.service.BankListService;
import com.hongyu.service.CommonUploadFileService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StoreApplicationService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;


@Controller
@RequestMapping("/admin/storeInfo/")
public class StoreInfoController {

	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource
	private HistoryService historyService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource(name = "storeApplicationServiceImpl")
	StoreApplicationService storeApplicationService;
	
	@Resource(name="bankListServiceImpl")
	BankListService bankListService;
	
	@Resource(name="hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name="storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	
	@Resource(name = "commonUploadFileServiceImpl")
	private CommonUploadFileService commonUploadFileService;
	
	
	
	
	
	@RequestMapping("tuichufile")
	@ResponseBody
	public Json menDianTuiChuFile(HttpSession session) {
		Json json = new Json();
		try {
			List<Filter> filters = new LinkedList<>();
			Map<String, Object> map = new HashMap<String, Object>();
			filters.add(Filter.eq("type", UploadTypeEnum.mendiantuichu));
			List<CommonUploadFileEntity> files = commonUploadFileService.findList(null, filters, null);
			if(files.get(0) != null) {
				map.put("quitFileUrl", files.get(0).getFileUrl());
			}
			json.setSuccess(true);
			json.setMsg("查找成功");
			json.setObj(map);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping("isInQuit")
	@ResponseBody
	public Json isInQuit(HttpSession session) {
		Json json = new Json();
		try {
			//List<Filter> filters = new LinkedList<>();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department = hyAdmin.getDepartment();
			if (department == null) {
				json.setSuccess(false);
				json.setMsg("所属部门不存在");
				return json;
			}
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("department", department));
			
			List<Store> stores = storeService.findList(null, filters, null);
			if (stores == null || stores.size() == 0) {
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
				return json;
			} else {
				Store store = stores.get(0);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("isInXuQian", false);
				map.put("isInQuit", false);
				List<Filter> filters2=new LinkedList<>();
				filters2.add(Filter.eq("store", store));
				List<StoreApplication> ans=storeApplicationService.findList(null, filters2, null);
				for(StoreApplication application : ans) {
					if(application.getType() == 4 && application.getApplicationStatus() >= 0 && application.getApplicationStatus() < 3) {
						map.put("isInQuit", true);
						//json.setObj(map);
						//json.setSuccess(true);
						//json.setMsg("门店正在申请中");
						//return json;
					}
					if(application.getType() == 2 && application.getApplicationStatus() >= 0 && application.getApplicationStatus() < 3) {
						map.put("isInXuQian", true);
						//json.setObj(map);
						//json.setSuccess(true);
						//json.setMsg("门店正在申请中");
						//return json;
					}
					
				}
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(map);
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department = hyAdmin.getDepartment();
			if (department == null) {
				json.setSuccess(false);
				json.setMsg("所属部门不存在");
			}
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("department", department));
			
			List<Store> stores = storeService.findList(null, filters, null);
			if (stores == null || stores.size() == 0) {
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
			} else {
				Store store = stores.get(0);
				json.setSuccess(true);
				json.setMsg("查找成功");
				json.setObj(store);
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("payPledge")
	@ResponseBody
	
	public Json payPledge(Long storeId, StoreApplication storeApplication, HttpSession httpSession) {
		Json json = new Json();
		try {
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("inputUser", username);
			variables.put("storeType", storeService.find(storeId).getStoreType());
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("jiaoyajin", variables);
			// 根据流程实例Id查询任务
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 门店经理交押金申请

			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId());

			HyAdmin hyAdmin = hyAdminService.find(username);
			
			storeApplication.setOperator(hyAdmin);
			// storeApplication.setDepartmentId(hyAdmin.getDepartment().getId());
//			Store store =storeApplication.getStore();
			Store store=storeService.find(storeId);
			store.setPstatus(1);//交纳中
			storeService.update(store);
			storeApplication.setPreStatus(store.getStatus());
			storeApplication.setStore(store);
			storeApplication.setMoney(store.getPledge());
			storeApplication.setProcessInstanceId(task.getProcessInstanceId());
			storeApplication.setType(1);
			storeApplicationService.save(storeApplication);
			json.setSuccess(true);
			json.setMsg("申请成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("申请失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;

	}

	@RequestMapping("storeRenew")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json storeRenew(Long storeId, StoreApplication storeApplication, HttpSession httpSession) {
		Json json = new Json();
		try {
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("inputUser", username);
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeRenew", variables);
			// 根据流程实例Id查询任务
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 门店经理申请续签

			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId());

			HyAdmin hyAdmin = hyAdminService.find(username);
			storeApplication.setOperator(hyAdmin);
			Store store=storeService.find(storeId);
			store.setMstatus(1);//填写中
			// storeApplication.setDepartmentId(hyAdmin.getDepartment().getId());
			storeApplication.setPreStatus(store.getStatus());
			storeApplication.setStore(store);
			storeApplication.setType(2);
			storeApplication.setProcessInstanceId(task.getProcessInstanceId());
			storeApplicationService.save(storeApplication);
			json.setSuccess(true);
			json.setMsg("申请成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("申请失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("payManagementFee")
	@ResponseBody
	public Json payManagementFee(Long storeId, StoreApplication storeApplication, HttpSession httpSession) {
		Json json = new Json();
		try {
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			Map<String, Object> variables = new HashMap<>(2);
			Store store=storeService.find(storeId);
			variables.put("inputUser", username);
			variables.put("storeType", store.getStoreType());
			// 启动流程
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("jiaoguanlifei", variables);
			// 根据流程实例Id查询任务
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 门店经理交管理费

			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId());
			
			HyAdmin hyAdmin = hyAdminService.find(username);
			storeApplication.setOperator(hyAdmin);
			//交纳中
			store.setMstatus(3);
			storeService.update(store);
			storeApplication.setPreStatus(store.getStatus());
			storeApplication.setStore(store);
			storeApplication.setMoney(store.getManagementFee());
			storeApplication.setValidDate(store.getMpayday());
			storeApplication.setProcessInstanceId(task.getProcessInstanceId());
			storeApplication.setType(3);
			storeApplicationService.save(storeApplication);
			json.setSuccess(true);
			json.setMsg("申请成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("申请失败");
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("logout")
	@ResponseBody
	public Json logout(Long storeId, StoreApplication storeApplication, HttpSession httpSession) {
		Json json = new Json();
		try {
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("storeType", storeService.find(storeId).getStoreType());
			variables.put("inputUser", username);
			// 启动流程
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeLogout", variables);
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 门店经理交管理费

			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId());

			HyAdmin hyAdmin = hyAdminService.find(username);
			storeApplication.setOperator(hyAdmin);
			Store store=storeService.find(storeId);
			storeApplication.setPreStatus(store.getStatus());
			// storeApplication.setDepartmentId(hyAdmin.getDepartment().getId());
			store.setStatus(Constants.STORE_TUI_CHU_DAI_SHEN_HE);
			store.setPstatus(3);//退出中
			storeApplication.setStore(store);
			
			//退出款项=押金+余额
			BigDecimal pledge=store.getPledge();
			BigDecimal balance=new BigDecimal(0);
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("store", store));
			List<StoreAccount> storeAccounts=storeAccountService.findList(null,filters,null);
			if(storeAccounts!=null&&storeAccounts.size()>0){
					StoreAccount storeAccount=storeAccounts.get(0);
					balance=storeAccount.getBalance();
			}else{
				json.setSuccess(false);
				json.setMsg("账户不存在");
				return json;
			}
			storeApplication.setBalance(balance);
			storeApplication.setPledge(pledge);
			storeApplication.setMoney(pledge.add(balance));
			storeApplication.setProcessInstanceId(task.getProcessInstanceId());
			storeApplication.setType(4);
			storeApplicationService.save(storeApplication);
			storeService.update(store);
			json.setSuccess(true);
			json.setMsg("申请成功");
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("申请失败");
			e.printStackTrace();
		}
		return json;
	}
	@RequestMapping("detailLog/view")
	@ResponseBody
	public Json detailLog(Long id){
		Json json=new Json();
		try {
			StoreApplication storeApplication=storeApplicationService.find(id);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(storeApplication);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("list/view")
	@ResponseBody
	public  Json list(Pageable pageable,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("department", department));
			List<Store> stores=storeService.findList(null,filters,null);
			if(stores!=null&&stores.size()>0){
				Store store=stores.get(0);
				List<Filter> filters2=new LinkedList<>();
				filters2.add(Filter.eq("store", store));
				List<Order> orders = new ArrayList<Order>();
				Order order = Order.desc("createtime");
				orders.add(order);
				List<StoreApplication> ans=storeApplicationService.findList(null, filters2, orders);
				Map<String, Object> answer=new HashMap<>();
				int page=pageable.getPage();
				int rows=pageable.getRows();
				answer.put("total", ans.size());
				answer.put("pageNumber",page);
				answer.put("pageSize", rows);
				answer.put("rows", ans.subList((page-1)*rows, page*rows>ans.size()?ans.size():page*rows));
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(answer);
			}else{
				json.setSuccess(false);
				json.setMsg("门店不存在");
			}
		}catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
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
	@RequestMapping("getHistoryComments/view")
	@ResponseBody
	public Json getHistoryComments(Long id) {
		Json json = new Json();
		try {
			StoreApplication storeApplication = storeApplicationService.find(id);
			String processInstanceId = storeApplication.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> result = new LinkedList<>();
			for (Comment comment : commentList) {
				Map<String, Object> map = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				String step = "";
				if (task != null) {
					step = task.getName();
				}
				map.put("step", step);
				String username = comment.getUserId();
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				map.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				Integer reslut;
				if (index < 0) {
					map.put("comment", " ");
					map.put("result", 1);
				} else {
					map.put("comment", str.substring(0, index));
					reslut = Integer.parseInt(str.substring(index + 1));
					map.put("result", reslut);
				}
				map.put("time", comment.getTime());
				result.add(map);
			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
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
			List<Filter> filters2=new LinkedList<>();
//			if(hyCompanies!=null&&hyCompanies.size()>0){
//				HyCompany hyCompany=hyCompanies.get(0);
//				filters2.add(Filter.eq("hyCompany", hyCompany));
//			}else{
//				json.setSuccess(false);
//				json.setMsg("公司不存在");
//				return json;
//			}
			filters2.add(Filter.eq("yhlx", Yinhangleixing.zbcw));
			filters2.add(Filter.eq("type", BankType.bank));
			List<BankList> bankLists=bankListService.findList(null,filters2,null);
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
	@RequestMapping("getFGSBankList/view")
	@ResponseBody
	public Json getFGSBankList(Integer payment,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			while(!department.getIsCompany()){
				department=department.getHyDepartment();
			}
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("hyDepartment", department));
			List<HyCompany> hyCompanies=hyCompanyService.findList(null, filters,null);

			List<Filter> filters2=new LinkedList<>();
			if(hyCompanies!=null&&hyCompanies.size()>0){
				HyCompany hyCompany=hyCompanies.get(0);
				filters2.add(Filter.eq("hyCompany", hyCompany));
			}else{
				json.setSuccess(false);
				json.setMsg("公司不存在");
				return json;
			}
			filters2.add(Filter.eq("type", BankType.bank));
			List<BankList> bankLists=bankListService.findList(null,filters2,null);
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
}

