package com.hongyu.service.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.entity.StoreRecharge;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreRechargeService;
import com.hongyu.service.StoreService;

@Service("storeRechargeServiceImpl")
public class StoreRechargeServiceImpl extends BaseServiceImpl<StoreRecharge, Long> implements StoreRechargeService {

	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource
	private RuntimeService runtimeService;

	@Resource
	private TaskService taskService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource(name = "storeRechargeDaoImpl")
	@Override
	public void setBaseDao(BaseDao<StoreRecharge, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

	@Override
	public Json add(StoreRecharge storeRecharge, HttpSession session) {
		// TODO Auto-generated method stub
		Json json = new Json();
		try {
			Map<String, Object> variables = new HashMap<String, Object>();
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeRecharge", variables);
			// 根据流程实例Id查询任务
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成 门店经理交押金申请
			
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId());
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department = hyAdmin.getDepartment();
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("department", department));
			List<Store> stores = storeService.findList(null, filters, null);
			if (stores == null || stores.size() == 0) {
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
			} else {
				Store store = stores.get(0);
				storeRecharge.setStore(store);
			}
			storeRecharge.setOperator(hyAdmin);
			StoreAccountLog storeAccountLog = new StoreAccountLog();
			storeAccountLog.setMoney(storeRecharge.getMoney());
			storeAccountLog.setStore(storeRecharge.getStore());
			storeAccountLog.setStatus(0);
			storeAccountLog.setProfile("预存款充值");
			storeAccountLog.setType(0);
			storeAccountLogService.save(storeAccountLog);
			storeRecharge.setStoreAccountLogId(storeAccountLog.getId());
			storeRecharge.setProcessInstanceId(task.getProcessInstanceId());
			storeRecharge.setStatus(0);			
			this.save(storeRecharge);
			json.setSuccess(true);
			json.setMsg("申请成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("申请错误： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

}
