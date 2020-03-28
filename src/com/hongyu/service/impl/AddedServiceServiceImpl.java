package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.dao.AddedServiceDao;
import com.hongyu.entity.AddedService;
import com.hongyu.entity.AddedServiceAndServiceTransfer;
import com.hongyu.entity.AddedServiceTransfer;
import com.hongyu.entity.HyAddedServiceSupplier;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Store;
import com.hongyu.service.AddedServiceAndServiceTransferService;
import com.hongyu.service.AddedServiceService;
import com.hongyu.service.AddedServiceTransferService;
import com.hongyu.service.HyAddedServiceSupplierService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StoreService;
import com.hongyu.util.DateUtil;

import oracle.net.aso.f;

@Service("addedServiceServiceImpl")
public class AddedServiceServiceImpl extends BaseServiceImpl<AddedService, Long> implements AddedServiceService {
	@Resource(name = "addedServiceAndServiceTransferServiceImpl")
	AddedServiceAndServiceTransferService addedServiceAndServiceTransferService;
	
	@Resource(name = "addedServiceTransferServiceImpl")
	AddedServiceTransferService addedServiceTransferService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyAddedServiceSupplierServiceImpl")
	HyAddedServiceSupplierService hyAddedServiceSupplierService;
	
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name = "addedServiceDaoImpl")
	AddedServiceDao dao;
	
	@Resource(name = "addedServiceDaoImpl")
	public void setBaseDao(AddedServiceDao dao) {
		super.setBaseDao(dao);
	}

	/** 增值业务 - 新建*/
	@Override
	public Json addValueAddedService(List<AddedService> adddedServices) throws Exception {
		Json json = new Json();
		for (AddedService addedService : adddedServices) {
			this.save(addedService);
		}
		json.setMsg("操作成功");
		json.setSuccess(true);
		return json;
	}

	/** 增值业务 - 付款申请 - 手动提交*/
	@Override
	public Json insertApplySubmit(List<Long> ids, HttpSession session) throws Exception {
		Json json = new Json();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);
		
		// 启动流程 完成task
		HashMap<String, Object> map = new HashMap<>();
		map.put("startType", "manual");  // 传入流程变量 使右分支
		
		List<Filter> store_filter = new ArrayList<>();
		store_filter.add(Filter.eq("department", hyAdmin.getDepartment().getId()));
		List<Store> storeList = storeService.findList(null, store_filter, null);
		
		
		map.put("storeId", storeList.get(0).getId());  // 传入门店id,使能指定门店经理
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("valueAdded",map);
		Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
		Authentication.setAuthenticatedUserId(username);
		taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
		taskService.complete(task.getId());
		
		// 修改各AddedService的状态
		BigDecimal total = new BigDecimal("0.00");
		for(Long id : ids){
			AddedService addedService = this.find(id);
			total = total.add(addedService.getMoney());
			addedService.setStatus(1); // 状态置为 "1:审核中-未付"
			this.update(addedService);
		}
		
		// 新建申请AddedServiceTransfer
		AddedServiceTransfer addedServiceTransfer = new AddedServiceTransfer();
		addedServiceTransfer.setStoreId(storeList.get(0).getId());
		HyAddedServiceSupplier supplier = this.find(ids.get(0)).getSupplier();
		addedServiceTransfer.setSupplier(supplier);
		addedServiceTransfer.setMoney(total);
		addedServiceTransfer.setOperator(hyAdmin);
		addedServiceTransfer.setCreateTime(new Date());
		addedServiceTransfer.setProcessInstanceId(pi.getProcessInstanceId());
		addedServiceTransfer.setStatus(1); //审核状态: 审核中
		addedServiceTransfer.setStep(1);  //审核步骤: 待门店经理审核
		addedServiceTransferService.save(addedServiceTransfer);
		
		// 用中间表关联AddedService和AddedServiceTransfer
		for(Long id : ids){
			AddedServiceAndServiceTransfer addedServiceAndServiceTransfer = new AddedServiceAndServiceTransfer();
			addedServiceAndServiceTransfer.setAddedServiceId(id);
			addedServiceAndServiceTransfer.setAddedServiceTransferId(addedServiceTransfer.getId());
			addedServiceAndServiceTransferService.save(addedServiceAndServiceTransfer);
		}
		
		json.setMsg("操作成功");
		json.setSuccess(true);
		return json;
	}
	
	/** 增值业务 - 付款申请 - 定时器提交*/
	@Override
	public void insertApplySubmitAuto() throws Exception{
		String sql = "SELECT has.store_id, has.supplier_id, SUM(money) FROM hy_added_service has WHERE status = 0 AND has.checkout_time < date_add(NOW(), interval 1 day) GROUP BY has.store_id, has.supplier_id;";
		List<Object[]> list = this.statis(sql);
		if(CollectionUtils.isEmpty(list)){
			return;
		}
		for(Object[] tmp : list){
			Long storeId = ((BigInteger)tmp[0]).longValue();
			Long supplierId = ((BigInteger)tmp[1]).longValue();
			BigDecimal bd = (BigDecimal)tmp[2];
			
			// 启动流程 完成task
			HashMap<String, Object> map = new HashMap<>();
			map.put("startType", "auto");  // 传入流程变量 使左分支
			
			map.put("storeId", storeId);  // 传入门店id,使能指定门店经理
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("valueAdded",map);
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			Authentication.setAuthenticatedUserId("系统自动生成");
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId());
			
			// 修改各AddedService的状态
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("storeId", storeId));
			filters.add(Filter.eq("supplier", supplierId));
			filters.add(Filter.eq("status", 0));
			filters.add(Filter.le("checkoutTime", DateUtil.getDateAfterSpecifiedDays(new Date(), 1)));
			List<AddedService> addedServiceList = this.findList(null, filters, null);
			
			BigDecimal total = new BigDecimal(0);
			for(AddedService a : addedServiceList){
				total = total.add(a.getMoney());
				a.setStatus(1); // 状态置为 "1:审核中-未付"
				this.update(a);
			}
			
			// 新建申请AddedServiceTransfer
			AddedServiceTransfer addedServiceTransfer = new AddedServiceTransfer();
			addedServiceTransfer.setStoreId(storeId);
			HyAddedServiceSupplier supplier = hyAddedServiceSupplierService.find(supplierId);
			addedServiceTransfer.setSupplier(supplier);
			addedServiceTransfer.setMoney(total);
			addedServiceTransfer.setOperator(null); // 对前台的展示是否有影响？
			addedServiceTransfer.setCreateTime(new Date());
			addedServiceTransfer.setProcessInstanceId(pi.getProcessInstanceId());
			addedServiceTransfer.setStatus(1); //审核状态: 审核中
			addedServiceTransfer.setStep(1);  //审核步骤: 待门店经理审核
			addedServiceTransferService.save(addedServiceTransfer);
			
			// 用中间表关联AddedService和AddedServiceTransfer
			for(AddedService a : addedServiceList){
				AddedServiceAndServiceTransfer addedServiceAndServiceTransfer = new AddedServiceAndServiceTransfer();
				addedServiceAndServiceTransfer.setAddedServiceId(a.getId());
				addedServiceAndServiceTransfer.setAddedServiceTransferId(addedServiceTransfer.getId());
				addedServiceAndServiceTransferService.save(addedServiceAndServiceTransfer);
			}
		}
	}
}