package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.BranchBalance;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.entity.BranchReceiptServicer;
import com.hongyu.entity.BranchReceiptTotalServicer;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyRegulate;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.PayablesLine;
import com.hongyu.entity.PayablesLineItem;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.ReceiptServicer;
import com.hongyu.entity.ReceiptTotalServicer;
import com.hongyu.entity.RefundInfo;
import com.hongyu.entity.RefundRecords;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.entity.StorePreSave;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.BranchReceiptServicerService;
import com.hongyu.service.BranchReceiptTotalServicerService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.PayablesLineItemService;
import com.hongyu.service.PayablesLineService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.service.PaymentSupplierService;
import com.hongyu.service.ReceiptServicerService;
import com.hongyu.service.ReceiptTotalServicerService;
import com.hongyu.service.RefundInfoService;
import com.hongyu.service.RefundPayServicerModifyService;
import com.hongyu.service.RefundRecordsService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StorePreSaveService;
import com.hongyu.util.Constants;
import com.hongyu.util.liyang.EmployeeUtil;

@Transactional
@Service("refundPayServicerModifyServiceImpl")
public class RefundPayServicerModifyServiceImpl implements RefundPayServicerModifyService{
	
	
	@Resource(name = "payablesLineServiceImpl")
	private PayablesLineService payablesLineService;
	
	@Resource(name = "payablesLineItemServiceImpl")
	private PayablesLineItemService payablesLineItemService;
	
	@Resource (name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "receiptTotalServicerServiceImpl")
	ReceiptTotalServicerService receiptTotalServicerService;
	
	@Resource(name = "receiptServicerServiceImpl")
	ReceiptServicerService receiptServicerService;
	
	@Resource(name = "refundInfoServiceImpl")
	RefundInfoService refundInfoService;
	
	@Resource(name = "refundRecordsServiceImpl")
	RefundRecordsService refundRecordsService;
	
	@Resource(name = "paymentSupplierServiceImpl")
	PaymentSupplierService paymentSupplierService;

	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;
	
	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	
	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;
	
	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	
	@Autowired
	BranchBalanceService branchBalanceService;

    @Autowired
	BranchPreSaveService branchPreSaveService;
	
    @Autowired
    HyRegulateService hyRegulateService;
    
    @Autowired
    BranchReceiptTotalServicerService branchReceiptTotalServicerService;
    
    @Autowired
    HyCompanyService hyCompanyService;
    
    @Autowired
    BranchReceiptServicerService branchReceiptServicerService;
    
    @Autowired
    HyOrderItemService hyOrderItemService;
    
	/**
	 * 线路退团以后
	 * 根据打款单有没有生成，进行更新
	 * 更新order表里的扣点金额
	 * 如果已经生成打款单，则存入欠款
	 * 如果没有生成打款单，则更新payablelines和payablelinesitem表（让其生成打款单的时候不统计该条订单）
	 * 
	 */
	public Json tuituan(HyOrder hyOrder,HyOrderApplication application,HySupplierContract contract,String username,Store store){
		Json json  = new Json();
		
		Integer tuikuanNum = 0;
		for(HyOrderApplicationItem item : application.getHyOrderApplicationItems()){
			HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
			if(orderItem.getType() == 1)
				tuikuanNum += item.getReturnQuantity();
		}
		
		BigDecimal koudian = new BigDecimal(0);
		if (hyOrder.getKoudianMethod().equals(Constants.DeductLine.tuanke.ordinal())) {
			koudian = application.getJiesuanMoney().multiply(hyOrder.getProportion()).multiply(BigDecimal.valueOf(0.01));
		}else if(hyOrder.getKoudianMethod().equals(Constants.DeductLine.rentou.ordinal())){
			koudian = hyOrder.getHeadProportion().multiply(new BigDecimal(tuikuanNum));
		}
		if(hyOrder.getIfjiesuan()){
			hyOrder.setKoudianMoney(hyOrder.getKoudianMoney().subtract(koudian));
			hyOrderService.update(hyOrder);
		}
		
		BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianWaimaiMoney());
//		BigDecimal qiankuan = tuiKuan.subtract(koudian);
//		BigDecimal gongyingshangTuikuan = application.getJiesuanMoney().subtract(koudian);
		BigDecimal qiankuan = application.getJiesuanMoney().subtract(koudian);
		
		// 外部供应商
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("hyOrder",hyOrder));
		List<PayablesLineItem> items = payablesLineItemService.findList(null,filters,null);
		if(!items.isEmpty()){
			PayablesLineItem item = items.get(0);
			
			//打款单未提交
			if(item.getState() == 0){
				//更新payablelines
				PayablesLine payablesLine = payablesLineService.find(item.getPayablesLineId());
				payablesLine.setMoney(payablesLine.getMoney().subtract(qiankuan));
				payablesLineService.update(payablesLine);
				
				//更新payablelines_item
				item.setState(2);//将状态改为已退款
				item.setMoney(item.getMoney().subtract(qiankuan));
				item.setRefunds(qiankuan);
				payablesLineItemService.update(item);
		
			}else if(item.getState() == 1){//订单已提交打款单，更新欠款
				BigDecimal balance = new BigDecimal(0);
				filters.clear();
				filters.add(Filter.eq("supplierName", contract.getLiable().getUsername()));
				List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null, filters, null);

				// 更新欠款
				if (receiptTotalServicers.size() != 0) {
					ReceiptTotalServicer receiptTotalServicer = receiptTotalServicers.get(0);
					balance = receiptTotalServicer.getBalance();
					balance = balance.add(qiankuan);
					receiptTotalServicer.setBalance(balance);
					receiptTotalServicerService.update(receiptTotalServicer);
				} else {
					ReceiptTotalServicer receiptTotalServicer = new ReceiptTotalServicer();
					receiptTotalServicer.setSupplierName(contract.getLiable().getUsername());
					balance = balance.add(qiankuan);
					receiptTotalServicer.setBalance(balance);
					receiptTotalServicerService.save(receiptTotalServicer);
				}

				// 写入售后退消团收支明细 -- 收
				ReceiptServicer receiptServicer = new ReceiptServicer();
				receiptServicer.setAmount(qiankuan);
				receiptServicer.setDate(new Date());
				receiptServicer.setOrderOrPayServicerId(hyOrder.getId());
				receiptServicer.setOperator(hyAdminService.find(username).getName());
				receiptServicer.setSupplierName(contract.getLiable().getUsername());
				receiptServicer.setState(0); // 存入欠款
				receiptServicer.setBalance(balance);
				receiptServicerService.save(receiptServicer);
			}
			
		}
		// 分公司或总公司产品中心
		filters.clear();
		filters.add(Filter.eq("hyGroup",hyOrder.getGroupId()));
		List<HyRegulate> regulates = hyRegulateService.findList(null,filters,null);
		if(!regulates.isEmpty()){
			HyRegulate regulate = regulates.get(0);
			Department department = EmployeeUtil.getCompany(regulate.getOperator());
			filters.clear();
			filters.add(Filter.eq("hyDepartment", department));
			List<HyCompany> companies = hyCompanyService.findList(null,filters,null);
			HyCompany company = companies.get(0);
			
			// 计调报账已经提交后 
			if(regulate.getStatus()>0 && regulate.getStatus()<3){
				
				regulate.setVisitorNum(regulate.getVisitorNum() + tuikuanNum);
				hyRegulateService.update(regulate);
				
				if(department.getId()!=1){
					BigDecimal balance = new BigDecimal(0);
					filters.clear();
					filters.add(Filter.eq("companyId", company.getID()));
					List<BranchReceiptTotalServicer> branchReceiptTotalServicers = branchReceiptTotalServicerService.findList(null, filters, null);
					if (branchReceiptTotalServicers.size() != 0) {
						BranchReceiptTotalServicer branchReceiptTotalServicer = branchReceiptTotalServicers.get(0);
						balance = branchReceiptTotalServicer.getBalance();
						balance = balance.add(qiankuan);
						branchReceiptTotalServicer.setBalance(balance);
						branchReceiptTotalServicerService.update(branchReceiptTotalServicer);
					} else {
						BranchReceiptTotalServicer branchReceiptTotalServicer = new BranchReceiptTotalServicer();
						branchReceiptTotalServicer.setCompanyId(company.getID());
						balance = balance.add(qiankuan);
						branchReceiptTotalServicer.setBalance(balance);
						branchReceiptTotalServicerService.save(branchReceiptTotalServicer);
					}
					
					// 写入分公司欠款收支明细 -- 收
					BranchReceiptServicer branchReceiptServicer = new BranchReceiptServicer();
					branchReceiptServicer.setAmount(qiankuan);
					branchReceiptServicer.setDate(new Date());
					branchReceiptServicer.setOrderOrSettleId(hyOrder.getId());
					branchReceiptServicer.setOperator(hyAdminService.find(username).getName());
					branchReceiptServicer.setCompanyId(company.getID());
					branchReceiptServicer.setState(0); // 存入欠款
					branchReceiptServicer.setBalance(balance);
					branchReceiptServicerService.save(branchReceiptServicer);
					
				}
			}
		}
		Date date = new Date();
		PayandrefundRecord record = new PayandrefundRecord();
		record.setOrderId(hyOrder.getId());
		record.setMoney(tuiKuan);
		record.setPayMethod(5);	//5预存款
		record.setType(1);	//1退款
		record.setStatus(1);	//1已退款
		record.setCreatetime(date);
		payandrefundRecordService.save(record);
		
		
		if(store != null){
			RefundInfo refundInfo = new RefundInfo();
			refundInfo.setAmount(tuiKuan);
			refundInfo.setAppliName(application.getOperator().getName());
			refundInfo.setApplyDate(application.getCreatetime());
			refundInfo.setPayDate(date);
			refundInfo.setRemark("门店退团退款");
			refundInfo.setState(1);  //已付款
			refundInfo.setType(1);  //门店退团（游客退团，认为是一个类型）
			refundInfo.setOrderId(hyOrder.getId());
			refundInfoService.save(refundInfo);
			
			
			//生成退款记录
			RefundRecords records = new RefundRecords();
			records.setRefundInfoId(refundInfo.getId());
			records.setOrderCode(hyOrder.getOrderNumber());
			records.setOrderId(hyOrder.getId());
			records.setRefundMethod((long) 1); //预存款方式
			records.setPayDate(date);
			HyAdmin hyAdmin = hyAdminService.find(username);
			if(hyAdmin!=null)
				records.setPayer(hyAdmin.getName());
			records.setAmount(tuiKuan);
			records.setStoreId(store.getId());
			records.setStoreName(store.getStoreName());
			records.setTouristName(hyOrder.getContact());
			records.setTouristAccount(store.getBankList().getBankAccount());  //门店账号
			records.setSignUpMethod(1);   //门店
			refundRecordsService.save(records);



			if(store.getStoreType()==2){
				Department department = store.getSuoshuDepartment();
				while(!department.getIsCompany()){
					department = department.getHyDepartment();
				}				
				HyCompany company = department.getHyCompany();
				//修改分公司余额
				List<Filter> branchBalanceFilters = new ArrayList<>();
				branchBalanceFilters.add(Filter.eq("branchId",department.getId()));

				List<BranchBalance> branchBalances = branchBalanceService.findList(null,branchBalanceFilters,null);
				if(branchBalances.size()!=0){
					BranchBalance branchBalance = branchBalances.get(0);
					branchBalance.setBranchBalance(branchBalance.getBranchBalance().add(tuiKuan));
					branchBalanceService.update(branchBalance);
				}else{
					BranchBalance branchBalance = new BranchBalance();
					branchBalance.setBranchId(store.getDepartment().getId());
					branchBalance.setBranchBalance(tuiKuan);
					branchBalanceService.save(branchBalance);
				}
				//分公司预存款记录
				BranchPreSave branchPreSave = new BranchPreSave();
				branchPreSave.setBranchName(company.getCompanyName());
				branchPreSave.setPreSaveBalance(branchBalances.get(0).getBranchBalance());
				branchPreSave.setAmount(tuiKuan);
				branchPreSave.setBranchId(store.getDepartment().getId());
				branchPreSave.setDate(new Date());
				branchPreSave.setDepartmentName(store.getDepartment().getName());
				branchPreSave.setOrderId(hyOrder.getId());
				branchPreSave.setRemark("门店退团");
				branchPreSave.setType(10); //退团
				branchPreSaveService.save(branchPreSave);

			}else{
				//预存款余额表
				// 3、修改门店预存款表      并发情况下的数据一致性！

//				List<Filter> filters = new ArrayList<>();
				filters.clear();
				filters.add(Filter.eq("store", store));
				List<StoreAccount> list = storeAccountService.findList(null, filters, null);
				if(list.size()!=0){
					StoreAccount storeAccount = list.get(0);
					storeAccount.setBalance(storeAccount.getBalance().add(tuiKuan));
					storeAccountService.update(storeAccount);
				}else{
					StoreAccount storeAccount = new StoreAccount();
					storeAccount.setStore(store);
					storeAccount.setBalance(tuiKuan);
					storeAccountService.save(storeAccount);
				}

				// 4、修改门店预存款记录表
				StoreAccountLog storeAccountLog = new StoreAccountLog();
				storeAccountLog.setStatus(1);
				storeAccountLog.setCreateDate(application.getCreatetime());
				storeAccountLog.setMoney(tuiKuan);
				storeAccountLog.setOrderSn(hyOrder.getOrderNumber());
				storeAccountLog.setStore(store);
				storeAccountLog.setType(3);
				storeAccountLog.setProfile("门店退团");
				storeAccountLogService.update(storeAccountLog);

				// 5、修改 总公司-财务中心-门店预存款表
				StorePreSave storePreSave = new StorePreSave();
				storePreSave.setStoreId(store.getId());
				storePreSave.setStoreName(store.getStoreName());
				storePreSave.setType(2); ////1:门店充值 2:报名退款  3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵 9:签证退款 10:酒店/酒加景销售 11:酒店/酒加景退款 12:门店后返 13:供应商驳回订单 14:
				storePreSave.setDate(date);
				storePreSave.setAmount(tuiKuan);
				storePreSave.setOrderCode(hyOrder.getOrderNumber());
				storePreSave.setOrderId(hyOrder.getId());
				storePreSave.setPreSaveBalance(storeAccountService.findList(null, filters, null).get(0).getBalance());
				storePreSaveService.save(storePreSave);
		}
		
		

		}
		json.setMsg("操作成功");
		json.setSuccess(true);
		return json;
	}

	/**
	 * 供应商消团
	 * 根据供应商是否确认订单，打款单是否提交 进行分类
	 */
	public Json xiaotuan(HyOrder hyOrder,HyOrderApplication application,HySupplierContract contract,String username,Store store){
		Json json = new Json();
		
		BigDecimal koudian = new BigDecimal(0);
		if (hyOrder.getKoudianMethod().equals(Constants.DeductLine.tuanke.ordinal())) {
			koudian = application.getJiesuanMoney().multiply(hyOrder.getProportion()).multiply(BigDecimal.valueOf(0.01));
		}
		if(hyOrder.getIfjiesuan()){
			hyOrder.setKoudianMoney(hyOrder.getKoudianMoney().subtract(koudian));
			hyOrderService.update(hyOrder);
		}
//		hyOrder.setKoudianMoney(hyOrder.getKoudianMoney().subtract(koudian));
//		hyOrderService.update(hyOrder);
		
		BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianWaimaiMoney());
//		BigDecimal qiankuan = tuiKuan.subtract(koudian);
		BigDecimal qiankuan = application.getJiesuanMoney().subtract(koudian);
		
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("hyOrder",hyOrder));
		List<PayablesLineItem> items = payablesLineItemService.findList(null,filters,null);
		//如果在供应商确认之前消团，则items为空，将不会更改打款单，也没有欠款，只给门店退款即可
		if(!items.isEmpty()){
			PayablesLineItem item = items.get(0);
			
			//打款单未提交
			if(item.getState() == 0){
				//更新payablelines
				PayablesLine payablesLine = payablesLineService.find(item.getPayablesLineId());
				payablesLine.setMoney(payablesLine.getMoney().subtract(qiankuan));
				payablesLineService.update(payablesLine);
				
				//更新payablelines_item
				item.setState(2);//将状态改为已退款
				item.setMoney(item.getMoney().subtract(qiankuan));
				item.setRefunds(qiankuan);
				payablesLineItemService.update(item);
		
			}else if(item.getState() == 1){//订单已提交打款单，更新欠款
				BigDecimal balance = new BigDecimal(0);
				filters.clear();
				filters.add(Filter.eq("supplierName", contract.getLiable().getUsername()));
				List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null, filters, null);

				// 更新欠款
				if (receiptTotalServicers.size() != 0) {
					ReceiptTotalServicer receiptTotalServicer = receiptTotalServicers.get(0);
					balance = receiptTotalServicer.getBalance();
					balance = balance.add(qiankuan);
					receiptTotalServicer.setBalance(balance);
					receiptTotalServicerService.update(receiptTotalServicer);
				} else {
					ReceiptTotalServicer receiptTotalServicer = new ReceiptTotalServicer();
					receiptTotalServicer.setSupplierName(contract.getLiable().getUsername());
					balance = balance.add(qiankuan);
					receiptTotalServicer.setBalance(balance);
					receiptTotalServicerService.save(receiptTotalServicer);
				}	

				// 写入售后退消团收支明细 -- 收
				ReceiptServicer receiptServicer = new ReceiptServicer();
				receiptServicer.setAmount(qiankuan);
				receiptServicer.setDate(new Date());
				receiptServicer.setOrderOrPayServicerId(hyOrder.getId());
				receiptServicer.setOperator(hyAdminService.find(username).getName());
				receiptServicer.setSupplierName(contract.getLiable().getUsername());
				receiptServicer.setState(0); // 存入欠款
				receiptServicer.setBalance(balance);
				receiptServicerService.save(receiptServicer);
			}
		}

		// 分公司产品中心
		filters.clear();
		filters.add(Filter.eq("hyGroup", hyOrder.getGroupId()));
		List<HyRegulate> regulates = hyRegulateService.findList(null, filters, null);
		if (!regulates.isEmpty()) {
			HyRegulate regulate = regulates.get(0);
			Department department = EmployeeUtil.getCompany(regulate.getOperator());
			filters.clear();
			filters.add(Filter.eq("hyDepartment", department));
			List<HyCompany> companies = hyCompanyService.findList(null, filters, null);
			HyCompany company = companies.get(0);
			// 如果是分公司供应商
			if (department.getId() != 1 && regulate.getStatus() != 0) {
				BigDecimal balance = new BigDecimal(0);
				filters.clear();
				filters.add(Filter.eq("company_id", company.getID()));
				List<BranchReceiptTotalServicer> branchReceiptTotalServicers = branchReceiptTotalServicerService
						.findList(null, filters, null);
				if (branchReceiptTotalServicers.size() != 0) {
					BranchReceiptTotalServicer branchReceiptTotalServicer = branchReceiptTotalServicers.get(0);
					balance = branchReceiptTotalServicer.getBalance();
					balance = balance.add(qiankuan);
					branchReceiptTotalServicer.setBalance(balance);
					branchReceiptTotalServicerService.update(branchReceiptTotalServicer);
				} else {
					BranchReceiptTotalServicer branchReceiptTotalServicer = new BranchReceiptTotalServicer();
					branchReceiptTotalServicer.setCompanyId(company.getID());
					balance = balance.add(qiankuan);
					branchReceiptTotalServicer.setBalance(balance);
					branchReceiptTotalServicerService.save(branchReceiptTotalServicer);
				}

				// 写入分公司欠款收支明细 -- 收
				BranchReceiptServicer branchReceiptServicer = new BranchReceiptServicer();
				branchReceiptServicer.setAmount(qiankuan);
				branchReceiptServicer.setDate(new Date());
				branchReceiptServicer.setOrderOrSettleId(hyOrder.getId());
				branchReceiptServicer.setOperator(hyAdminService.find(username).getName());
				branchReceiptServicer.setCompanyId(company.getID());
				branchReceiptServicer.setState(0); // 存入欠款
				branchReceiptServicer.setBalance(balance);
				branchReceiptServicerService.save(branchReceiptServicer);

			}
		}
		

		// 写入退款记录 //预存款余额修改
		// BigDecimal tuiKuan = hyOrder.getJiesuanTuikuan();
		RefundInfo refundInfo = new RefundInfo();
		refundInfo.setAmount(tuiKuan);
		refundInfo.setAppliName(application.getOperator().getName());
		refundInfo.setApplyDate(application.getCreatetime());
		Date date = new Date();
		refundInfo.setPayDate(date);
		refundInfo.setRemark("供应商消团退款");
		refundInfo.setState(1); // 已付款
		refundInfo.setType(2); // 供应商消团
		refundInfo.setOrderId(hyOrder.getId());
		refundInfoService.save(refundInfo);

		// 生成退款记录
		RefundRecords records = new RefundRecords();
		records.setRefundInfoId(refundInfo.getId());
		records.setOrderCode(hyOrder.getOrderNumber());
		records.setOrderId(hyOrder.getId());
		records.setRefundMethod((long) 1); // 预存款方式
		records.setPayDate(date);
		HyAdmin hyAdmin = hyAdminService.find(username);
		if (hyAdmin != null)
			records.setPayer(hyAdmin.getName());
		records.setAmount(tuiKuan);
		records.setStoreId(store.getId());
		records.setStoreName(store.getStoreName());
		records.setTouristName(hyOrder.getContact());
		records.setTouristAccount(store.getBankList().getBankAccount()); // 门店账号
		records.setSignUpMethod(1); // 门店
		refundRecordsService.save(records);

		PayandrefundRecord record = new PayandrefundRecord();
		record.setOrderId(hyOrder.getId());
		record.setMoney(tuiKuan);
		record.setPayMethod(5); // 5预存款
		record.setType(1); // 1退款
		record.setStatus(1); // 1已退款
		record.setCreatetime(date);
		payandrefundRecordService.save(record);

		// 预存款余额表
		// 3、修改门店预存款表 并发情况下的数据一致性！

		filters.clear();
		filters.add(Filter.eq("store", store));
		List<StoreAccount> list = storeAccountService.findList(null, filters, null);
		if (list.size() != 0) {
			StoreAccount storeAccount = list.get(0);
			storeAccount.setBalance(storeAccount.getBalance().add(tuiKuan));
			storeAccountService.update(storeAccount);
		} else {
			StoreAccount storeAccount = new StoreAccount();
			storeAccount.setStore(store);
			storeAccount.setBalance(tuiKuan);
			storeAccountService.save(storeAccount);
		}

		// 4、修改门店预存款记录表
		StoreAccountLog storeAccountLog = new StoreAccountLog();
		storeAccountLog.setStatus(1);
		storeAccountLog.setCreateDate(application.getCreatetime());
		storeAccountLog.setMoney(tuiKuan);
		storeAccountLog.setOrderSn(hyOrder.getOrderNumber());
		storeAccountLog.setStore(store);
		storeAccountLog.setType(4); // 供应商消团
		storeAccountLog.setProfile("供应商消团");
		storeAccountLogService.update(storeAccountLog);

		// 5、修改 总公司-财务中心-门店预存款表
		StorePreSave storePreSave = new StorePreSave();
		storePreSave.setStoreId(store.getId());
		storePreSave.setStoreName(store.getStoreName());
		storePreSave.setType(2); //// 1:门店充值 2:报名退款 3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款
									//// 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵 9:签证退款
									//// 10:酒店/酒加景销售 11:酒店/酒加景退款 12:门店后返
									//// 13:供应商驳回订单 14:
		storePreSave.setDate(date);
		storePreSave.setAmount(tuiKuan);
		storePreSave.setOrderCode(hyOrder.getOrderNumber());
		storePreSave.setOrderId(hyOrder.getId());
		storePreSave.setPreSaveBalance(storeAccountService.findList(null, filters, null).get(0).getBalance());
		storePreSaveService.save(storePreSave);
		
		
		
		
		json.setMsg("操作成功");
		json.setSuccess(true);
		return json;
	}
	
	
	
}
