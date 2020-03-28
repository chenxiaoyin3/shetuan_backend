package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.dao.PrePaySupplyDao;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.Department;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.PayServicer;
import com.hongyu.entity.PrePaySupply;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.PayServicerService;
import com.hongyu.service.PrePaySupplyService;
import com.hongyu.util.liyang.EmployeeUtil;

@Service("prePaySupplyServiceImpl")
public class PrePaySupplyServiceImpl extends BaseServiceImpl<PrePaySupply, Long> implements PrePaySupplyService {

	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource
	private TaskService taskService;

	@Resource(name = "prePaySupplyServiceImpl")
	PrePaySupplyService prePaySupplyService;

	@Resource(name = "commonEdushenheServiceImpl")
	CommonEdushenheService commonEdushenheService;

	@Resource(name = "hySupplierElementServiceImpl")
	HySupplierElementService hySupplierElementService;

	@Resource(name = "prePaySupplyDaoImpl")
	PrePaySupplyDao dao;

	@Resource(name = "prePaySupplyDaoImpl")
	public void setBaseDao(PrePaySupplyDao dao) {
		super.setBaseDao(dao);
	}

	@Override
	public Json insertPrePaySupplyAudit(Long id, String comment, Integer audit_state, HttpSession session)
			throws Exception {
		Json json = new Json();

		String username = (String) session.getAttribute(CommonAttributes.Principal);
		// HyAdmin hyAdmin = hyAdminService.find(username);
		PrePaySupply prePaySupply = prePaySupplyService.find(id);
		String processInstanceId = prePaySupply.getProcessInstanceId();
		
		// 2019/3/1  modified by wangjie
		boolean ifBranch = true;
		Department company = EmployeeUtil.getCompany(hyAdminService.find(username));
		if(company.getHyDepartmentModel().getName().contains("总公司")) ifBranch = false;
		

		if (processInstanceId == null || processInstanceId == "") {
			json.setSuccess(false);
			json.setMsg("审核出错，信息不完整，请重新申请");
		} else {
			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();

			HashMap<String, Object> map = new HashMap<>();		

			if (audit_state == 1) { // 审核通过
				map.put("result", "tongguo");
				
				
				//2019/3/1 改  by wangjie
				if(prePaySupply.getStep() == 4){  //待部门经理审核    判断是否需要分公司产品中心经理限额审核
					List<Filter> filters = new ArrayList<>();
					if(ifBranch){  //如果是分公司
						filters.add(Filter.eq("eduleixing", Eduleixing.prepayBranch));
					}else {
						filters.add(Filter.eq("eduleixing", Eduleixing.prepayCompany));
					}
					List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
					BigDecimal money1 = edu.get(0).getMoney();
					if(prePaySupply.getMoney().compareTo(money1)>0){  //超过额度
						prePaySupply.setStep(0);  //待产品中心经理审核
						map.put("money1", "more");
					}else{
						map.put("money1", "less");
						filters.clear();
						filters.add(Filter.eq("eduleixing", Eduleixing.prePayLimit));
						List<CommonShenheedu> edu2 = commonEdushenheService.findList(null, filters, null);
						BigDecimal money2 = edu2.get(0).getMoney();
						if (prePaySupply.getMoney().compareTo(money2) > 0) { // 超过额度
							map.put("money2", "more");
							prePaySupply.setStep(1);// 待副总限额审核
						} else {
							map.put("money2", "less");
							prePaySupply.setStep(2); // 待财务审核
						}
					}
				}	
				else if (prePaySupply.getStep() == 0) { // 若当前状态为"0:待产品中心经理审核"
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("eduleixing", Eduleixing.prePayLimit));
					List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
					BigDecimal money = edu.get(0).getMoney();
					if (prePaySupply.getMoney().doubleValue() > money.doubleValue()) { // 超过额度
						map.put("money", "more");
						prePaySupply.setStep(1); // 步骤置为 1:待公司副总审核
					} else {
						map.put("money", "less");
						prePaySupply.setStep(2); // 步骤置为 2:待总公司财务审核
					}
				} else if (prePaySupply.getStep() == 1) { // 若当前状态为"1:待公司副总审核"
					prePaySupply.setStep(2); // 步骤置为 2:待总公司财务审核
				} else if (prePaySupply.getStep() == 2) {
					// 修改AddedServiceTransfer的状态
					prePaySupply.setState(1); // 状态 置为"1:已通过-未付"
					prePaySupply.setStep(3);// 步骤为审核完成

					/*List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("supplierElement", prePaySupply.getSupplierElement().getId()));
					List<HySupplierElement> lists = hySupplierElementService.findList(null, filters, null);*/

					// 生成 总公司财务中心 - 待付款 - 预付款
					PayServicer payServicer = new PayServicer();
					
					payServicer.setDepartmentId(prePaySupply.getOperator().getDepartment().getId()); // 申请人部门
					payServicer.setReviewId(id);
					payServicer.setHasPaid(0); // 0未付
					payServicer.setType(prePaySupply.getOperator().getDepartment().getHyDepartmentModel().getName().startsWith("分公司") ? 1 : 7); //1:分公司预付款 2:T+N 3:提前打款 4:旅游元素供应商尾款 5:向酒店/门票/酒加景供应商付款 6:江泰预充值 7:总公司预付款
					payServicer.setApplyDate(prePaySupply.getCreateTime()); 
					payServicer.setAppliName(prePaySupply.getOperator().getName());
					payServicer.setServicerId(prePaySupply.getSupplierElement().getId());
					payServicer.setServicerName(prePaySupply.getSupplierElement().getName());
					payServicer.setAmount(prePaySupply.getMoney());
					payServicer.setBankListId(prePaySupply.getSupplierElement().getBankList().getId());

					// PayServicer.setRemark(remark);
					payServicerService.save(payServicer);

				}
			} else if (audit_state == 0) { // 审核未通过
				map.put("result", "bohui");
				prePaySupply.setState(3); // 0审核中-未付 1 已通过-未付 2已通过-已付 3已驳回-未付
			}
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), processInstanceId,(comment == null ? " " : comment) + ":" + audit_state);
			taskService.claim(task.getId(), username);
			taskService.complete(task.getId(), map);
			prePaySupplyService.update(prePaySupply);

			json.setSuccess(true);
			json.setMsg("审核完成");
		}
		return json;
	}
}