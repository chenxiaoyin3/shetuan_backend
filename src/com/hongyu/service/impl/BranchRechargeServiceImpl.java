package com.hongyu.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.dao.BranchRechargeDao;
import com.hongyu.entity.BranchBalance;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.entity.BranchRecharge;
import com.hongyu.entity.BranchRechargeRecord;
import com.hongyu.entity.PayDetailsBranch;
import com.hongyu.entity.ReceiptBranchRecharge;
import com.hongyu.entity.ReceiptDetail;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.BranchRechargeRecordService;
import com.hongyu.service.BranchRechargeService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.PayDetailsBranchService;
import com.hongyu.service.ReceiptBranchRechargeService;
import com.hongyu.service.ReceiptDetailsService;


@Service("branchRechargeServiceImpl")
public class BranchRechargeServiceImpl extends BaseServiceImpl<BranchRecharge, Long> implements BranchRechargeService {

	@Resource(name = "branchRechargeDaoImpl")
	BranchRechargeDao dao;

	@Resource(name = "branchRechargeDaoImpl")
	public void setBaseDao(BranchRechargeDao dao) {
		super.setBaseDao(dao);
	}
	@Resource(name = "branchBalanceServiceImpl")
	BranchBalanceService branchBalanceService;

	@Resource(name = "branchRechargeServiceImpl")
	BranchRechargeService branchRechargeService;

	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;

	@Resource
	private RuntimeService runtimeService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "receiptBranchRechargeServiceImpl")
	ReceiptBranchRechargeService receiptBranchRechargeService;

	@Resource(name = "receiptDetailsServiceImpl")
	ReceiptDetailsService receiptDetailService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	
	@Resource(name = "branchPreSaveServiceImpl")
	BranchPreSaveService branchPreSaveService;

	@Resource(name = "branchRechargeRecordServiceImpl")
	BranchRechargeRecordService branchRechargeRecordService;
	
	@Resource(name = "payDetailsBranchServiceImpl")
	PayDetailsBranchService payDetailsBranchService;
	

	@Override
	public Json branchRechargeAudit(Long id, String comment, Integer state, HttpSession session) {
	    Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		BranchRecharge branchRecharge = branchRechargeService.find(id);
		String processInstanceId = branchRecharge.getProcessInstanceId();

		if (processInstanceId == null || processInstanceId == "") {
			json.setSuccess(false);
			json.setMsg("审核出错，信息不完整，请重新申请");
		} else {
			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			if (state == 1) {
				System.out.println("*******************已修改********");
				// 总公司财务审核通过，直接生成已充值记录
				// 1、在hy_receipt_branch_recharge表中写数据
				ReceiptBranchRecharge receiptBranchRecharge = new ReceiptBranchRecharge();
				receiptBranchRecharge.setState(1);
				receiptBranchRecharge.setBranchName(
						hyAdminService.find(branchRecharge.getUsername()).getDepartment().getFullName());

				receiptBranchRecharge.setPayer(hyAdminService.find(branchRecharge.getUsername()).getName());
				receiptBranchRecharge.setAmount(branchRecharge.getAmount());
				receiptBranchRecharge.setDate(new Date());
				receiptBranchRecharge.setReceiver(username);
				receiptBranchRechargeService.save(receiptBranchRecharge);

				// 2、在hy_receipt_details表中写入数据
				ReceiptDetail receiptDetail = new ReceiptDetail();
				receiptDetail.setReceiptType(4); // 4:ReceiptBranchRecharge
				receiptDetail.setReceiptId(receiptBranchRecharge.getId());
				receiptDetail.setAmount(branchRecharge.getAmount());
				receiptDetail.setPayMethod((long) 1); // 付款方式 1:转账 2:支付宝
														// 3:微信支付 4:现金 5:预存款
														// 6:刷卡

				receiptDetail.setAccountName(branchRecharge.getAccountAlias());
				receiptDetail.setShroffAccount(branchRecharge.getBankAccount());
				receiptDetail.setBankName(branchRecharge.getBankName());
				receiptDetail.setDate(receiptBranchRecharge.getDate());

				receiptDetailService.save(receiptDetail);
				
				//4、修改分公司预存款余额
				List<Filter> filters2 = new  ArrayList<>();
		    	filters2.add(Filter.eq("branchId",branchRecharge.getBranchId()));
				List<BranchBalance> lists = branchBalanceService.findList(null,filters2,null);
		    	if(!lists.isEmpty()){  //如果当前分公司余额表有记录
		    		BranchBalance b = lists.get(0);//拿到当前分公司的余额
		    		b.setBranchBalance(b.getBranchBalance().add(branchRecharge.getAmount()));
		    		branchBalanceService.update(b);
		    	}else{
		    		BranchBalance br = new BranchBalance();
		    		br.setBranchId(branchRecharge.getBranchId());
		    		br.setBranchBalance(branchRecharge.getAmount());
		    		branchBalanceService.save(br);
		    	}
				
				
				//3、修改充值记录branchpresave表（冲抵记录）
				BranchPreSave branchPreSave = new BranchPreSave();
				branchPreSave.setBranchId(branchRecharge.getBranchId());
				branchPreSave.setBranchName(departmentService.find(branchRecharge.getBranchId()).getName());
				branchPreSave.setDate(branchRecharge.getCreateDate());
				branchPreSave.setDepartmentName(departmentService.find(branchRecharge.getDepartmentId()).getFullName());
				branchPreSave.setType(1);
				branchPreSave.setRemark(branchRecharge.getRemark());
				branchPreSave.setAmount(branchRecharge.getAmount());
				
				
				List<Filter> f = new  ArrayList<>();
		    	f.add(Filter.eq("branchId",branchRecharge.getBranchId()));
//				List<BranchBalance> ls = branchBalanceService.findList(null,f,null);//xiugai by cqx
//				BranchBalance b = lists.get(0);//拿到当前分公司余额//xiugai by cqx
	    		branchPreSave.setPreSaveBalance(branchRecharge.getAmount());
		    	branchPreSaveService.save(branchPreSave);
		    	
		    	//分公司收款记录
		    	BranchRechargeRecord branchRechargeRecord = new BranchRechargeRecord();
		    	branchRechargeRecord.setHasPaid(true);
		    	branchRechargeRecord.setPayDate(receiptBranchRecharge.getDate());
		    	branchRechargeRecord.setAppliName(branchRecharge.getUsername());
		    	branchRechargeRecord.setAmount(branchRecharge.getAmount());
		    	branchRechargeRecord.setRemark(branchRecharge.getRemark());
		    	branchRechargeRecord.setBranchId(branchRecharge.getBranchId());
		    	branchRechargeRecord.setBranchRechargeId(branchRecharge.getId());
		    	branchRechargeRecordService.save(branchRechargeRecord);
				
		    	PayDetailsBranch payDetailsBranch = new PayDetailsBranch();
		    	payDetailsBranch.setAccount(branchRecharge.getAccountAlias());
		    	payDetailsBranch.setAmount(branchRecharge.getAmount());
		    	payDetailsBranch.setDate(receiptBranchRecharge.getDate());
		    	payDetailsBranch.setOperator(username);
		    	payDetailsBranch.setPayId(branchRechargeRecord.getId());
		    	payDetailsBranch.setPayMethod((long)1);
		    	payDetailsBranch.setSort(1);
		    	payDetailsBranchService.save(payDetailsBranch);
    	
					


			} else if (state == 0) {
				branchRecharge.setStatus(2);
			}

			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), processInstanceId, comment == null ? " " : comment + ":" + state);
			taskService.claim(task.getId(), username);
			taskService.complete(task.getId());
			branchRechargeService.update(branchRecharge);
			json.setSuccess(true);
			json.setMsg("审核成功");
		}
		return json;
	}
}

	
	
	
	

