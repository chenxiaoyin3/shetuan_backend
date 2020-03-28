package com.hongyu.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.dao.BranchPayServicerDao;
import com.hongyu.entity.AddedService;
import com.hongyu.entity.AddedServiceAndServiceTransfer;
import com.hongyu.entity.AddedServiceTransfer;
import com.hongyu.entity.BranchPayServicer;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.PayDetailsBranch;
import com.hongyu.service.AddedServiceAndServiceTransferService;
import com.hongyu.service.AddedServiceService;
import com.hongyu.service.AddedServiceTransferService;
import com.hongyu.service.BranchPayServicerService;
import com.hongyu.service.HyAddedServiceSupplierService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.PayDetailsBranchService;

@Service("branchPayServicerServiceImpl")
public class BranchPayServicerServiceImpl extends BaseServiceImpl<BranchPayServicer, Long>
		implements BranchPayServicerService {
	@Resource(name = "payDetailsBranchServiceImpl")
	PayDetailsBranchService payDetailsBranchService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "addedServiceServiceImpl")
	AddedServiceService addedServiceService;
	
	@Resource(name = "addedServiceAndServiceTransferServiceImpl")
	AddedServiceAndServiceTransferService addedServiceAndServiceTransferService;
	
	@Resource(name = "addedServiceTransferServiceImpl")
	AddedServiceTransferService addedServiceTransferService;
	
	@Resource(name = "hyAddedServiceSupplierServiceImpl")
	HyAddedServiceSupplierService hyAddedServiceSupplierService;
	
	@Resource(name = "branchPayServicerServiceImpl")
	BranchPayServicerService branchPayServicerService;
	
	
	@Resource(name = "branchPayServicerDaoImpl")
	BranchPayServicerDao dao;

	@Resource(name = "branchPayServicerDaoImpl")
	public void setBaseDao(BranchPayServicerDao dao) {
		super.setBaseDao(dao);
	}

	/** 门店增值业务 - 做付款*/
	@Override
	public Json addbranchPayServicer(List<PayDetailsBranch> list, HttpSession session) throws Exception {
		Json json = new Json();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		// 1.保存付款记录
		for (PayDetailsBranch p : list) {
			p.setOperator(admin.getName());
			payDetailsBranchService.save(p);
		}

		// 2.修改付款状态
		PayDetailsBranch payDetailsBranch = list.get(0);
		BranchPayServicer branchPayServicer = branchPayServicerService.find(payDetailsBranch.getPayId());
		branchPayServicer.setHasPaid(1); // 1:已付款
		branchPayServicer.setPayDate(payDetailsBranch.getDate());
		branchPayServicer.setOperator(admin.getName());
		branchPayServicerService.update(branchPayServicer);

		// 3.修改AddedServiceTransfer的状态
		AddedServiceTransfer addedServiceTransfer = addedServiceTransferService.find(branchPayServicer.getAddedServiceTransferId());
		addedServiceTransfer.setStatus(3);  // 3 已通过-已付
		addedServiceTransferService.update(addedServiceTransfer);
		
		// 4.修改AddedSevice的状态
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("addedServiceTransferId", branchPayServicer.getAddedServiceTransferId()));
		List<AddedServiceAndServiceTransfer> list2 = addedServiceAndServiceTransferService.findList(null, filters, null);
		
		for(AddedServiceAndServiceTransfer a :list2){
			AddedService addedService = addedServiceService.find(a.getAddedServiceId());
			addedService.setStatus(3);  // 状态置为 "3:已通过-已付"
			addedServiceService.update(addedService);
		}
		
		return json;
	}
}