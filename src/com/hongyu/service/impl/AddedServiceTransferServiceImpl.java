package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.dao.AddedServiceTransferDao;
import com.hongyu.entity.AddedService;
import com.hongyu.entity.AddedServiceAndServiceTransfer;
import com.hongyu.entity.AddedServiceTransfer;
import com.hongyu.entity.BranchPayServicer;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.service.AddedServiceAndServiceTransferService;
import com.hongyu.service.AddedServiceService;
import com.hongyu.service.AddedServiceTransferService;
import com.hongyu.service.BranchPayServicerService;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAddedServiceSupplierService;
import com.hongyu.service.HyAdminService;

@Service("addedServiceTransferServiceImpl")
public class AddedServiceTransferServiceImpl extends BaseServiceImpl<AddedServiceTransfer, Long>
		implements AddedServiceTransferService {
	
	@Resource(name = "commonEdushenheServiceImpl")
	 CommonEdushenheService commonEdushenheService;
	
	@Resource(name = "branchPayServicerServiceImpl")
	BranchPayServicerService branchPayServicerService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

//	@Resource(name = "addedServiceTransferServiceImpl")
//	AddedServiceTransferService addedServiceTransferService;

	@Resource(name = "addedServiceAndServiceTransferServiceImpl")
	AddedServiceAndServiceTransferService addedServiceAndServiceTransferService;

	@Resource(name = "addedServiceServiceImpl")
	AddedServiceService addedServiceService;

	@Resource(name = "hyAddedServiceSupplierServiceImpl")
	HyAddedServiceSupplierService hyAddedServiceSupplierService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource
	private TaskService taskService;

	@Resource(name = "addedServiceTransferDaoImpl")
	AddedServiceTransferDao dao;

	@Resource(name = "addedServiceTransferDaoImpl")
	public void setBaseDao(AddedServiceTransferDao dao) {
		super.setBaseDao(dao);
	}

	/** 增值业务审核 */
	@Override
	public Json insertAddedValueAudit(Long id, String comment, Integer state, HttpSession session) throws Exception {
		Json json = new Json();

		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);
		AddedServiceTransfer addedServiceTransfer = this.find(id);
		String processInstanceId = addedServiceTransfer.getProcessInstanceId();

		if (processInstanceId == null || processInstanceId == "") {
			json.setSuccess(false);
			json.setMsg("审核出错，信息不完整，请重新申请");
		} else {
			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();

			HashMap<String, Object> map = new HashMap<>();

			if (state == 1) { // 审核通过
				map.put("result", "tongguo");
				
				if(addedServiceTransfer.getStep() == 1){ // 若当前状态为"1:待门店经理审核"
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("eduleixing", Eduleixing.valueAddedLimit));
					List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
					BigDecimal money = edu.get(0).getMoney();
					if(addedServiceTransfer.getMoney().doubleValue() > money.doubleValue()){ // 超过额度
						map.put("money", "more");
						addedServiceTransfer.setStep(2);  // 步骤置为 2:待分公司副总审核
					}else{
						map.put("money", "less");
						addedServiceTransfer.setStep(3);  // 步骤置为 3:待分公司财务审核
					}
				}else if(addedServiceTransfer.getStep() == 2){ // 若当前状态为"2:待分公司副总审核"
					addedServiceTransfer.setStep(3);   // 步骤置为 3:待分公司财务审核 
				}else if(addedServiceTransfer.getStep() == 3){
					// 修改AddedServiceTransfer的状态
					addedServiceTransfer.setStatus(2);  // 状态 置为"2:已通过-未付"
					
					// 修改AddedSevice的状态
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("addedServiceTransferId", id));
					List<AddedServiceAndServiceTransfer> list = addedServiceAndServiceTransferService.findList(null, filters, null);
					for(AddedServiceAndServiceTransfer a :list){
						AddedService addedService = addedServiceService.find(a.getAddedServiceId());
						addedService.setStatus(2);  // 状态置为 "2:已通过-未付"
						addedServiceService.update(addedService);
					}
					
					
					// 生成 分公司财务中心 - 待付款
					BranchPayServicer branchPayServicer = new BranchPayServicer();
					branchPayServicer.setHasPaid(0); // 0未付
					branchPayServicer.setApplyDate(addedServiceTransfer.getCreateTime());
					branchPayServicer.setAppliName(addedServiceTransfer.getOperator().getName());
					branchPayServicer.setServicerId(addedServiceTransfer.getSupplier().getId());
					branchPayServicer.setServicerName(addedServiceTransfer.getSupplier().getName());
					branchPayServicer.setAmount(addedServiceTransfer.getMoney());
					branchPayServicer.setStoreId(addedServiceTransfer.getStoreId());
					branchPayServicer.setBranchId(hyAdmin.getDepartment().getHyDepartment().getId());
//					branchPayServicer.setRemark(remark);
					branchPayServicer.setAddedServiceTransferId(id);
					branchPayServicerService.save(branchPayServicer);
					
				}
			} else if (state == 0) { // 审核未通过
				map.put("result", "bohui");
				addedServiceTransfer.setStatus(4);  // 1审核中-未付 2 已通过-未付 3已通过-已付  4已驳回-未付
				
				// 修改AddedSevice的状态
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("addedServiceTransferId", id));
				List<AddedServiceAndServiceTransfer> list = addedServiceAndServiceTransferService.findList(null, filters, null);
				for(AddedServiceAndServiceTransfer a :list){
					AddedService addedService = addedServiceService.find(a.getAddedServiceId());
					// 状态置为 "0 未审核-未付 (已驳回 - 未付)"
					addedService.setStatus(0);  
					addedServiceService.update(addedService);
				}
			}
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), processInstanceId, (comment == null ? " " : comment) + ":" + state);
			taskService.claim(task.getId(), username);
			taskService.complete(task.getId(), map);
			this.update(addedServiceTransfer);
			
			json.setSuccess(true);
			json.setMsg("审核完成");
		}

		return json;
	}
}