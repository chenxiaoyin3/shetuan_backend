package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.dao.PaymentSupplierDao;
import com.hongyu.entity.BranchBalance;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderItem;
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
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.HySupplierContract.Settle;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HyTicketHotelRoomService;
import com.hongyu.service.HyTicketHotelService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketSceneService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.service.HyTicketSubscribeService;
import com.hongyu.service.HyVisaService;
import com.hongyu.service.PayServicerService;
import com.hongyu.service.PayablesLineItemService;
import com.hongyu.service.PayablesLineService;
import com.hongyu.service.PayablesRefundItemService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.service.PaymentSupplierService;
import com.hongyu.service.PiaowuConfirmService;
import com.hongyu.service.ReceiptServicerService;
import com.hongyu.service.ReceiptTotalServicerService;
import com.hongyu.service.RefundInfoService;
import com.hongyu.service.RefundRecordsService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StorePreSaveService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.sun.scenario.effect.impl.BufferUtil;

@Transactional
@Service("piaowuConfirmServiceImpl")
public class PiaowuConfirmServiceImpl implements PiaowuConfirmService{

	@Resource(name = "payablesLineServiceImpl")
	PayablesLineService payablesLineService;
	
	@Resource(name = "commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;
	
	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;

	@Resource(name = "commonEdushenheServiceImpl")
	CommonEdushenheService commonEdushenheService;

	@Resource(name = "payablesRefundItemServiceImpl")
	PayablesRefundItemService payablesRefundItemService;

	@Resource(name = "payablesLineItemServiceImpl")
	PayablesLineItemService payablesLineItemService;

	@Resource(name = "hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;

	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "paymentSupplierDaoImpl")
	PaymentSupplierDao dao;

	@Resource(name = "receiptTotalServicerServiceImpl")
	ReceiptTotalServicerService receiptTotalServicerService;
	
	@Resource(name = "paymentSupplierServiceImpl")
	PaymentSupplierService paymentSupplierService;
	

	@Resource (name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	HyOrderApplicationService hyOrderApplicationService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "receiptServicerServiceImpl")
	ReceiptServicerService receiptServicerService;
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	@Resource(name = "refundInfoServiceImpl")
	RefundInfoService refundInfoService;
	
	@Resource(name = "refundRecordsServiceImpl")
	RefundRecordsService refundRecordsService;
	
	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;

	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	
	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;
	
	@Resource(name = "hyTicketHotelServiceImpl")
	HyTicketHotelService hyTicketHotelService;
	
	@Resource(name = "hyOrderItemServiceImpl")
	HyOrderItemService hyOrderItemService;

	
	@Resource(name ="hyTicketHotelRoomServiceImpl")
	HyTicketHotelRoomService hyTicketHotelRoomService;
	
	@Resource(name = "hyTicketSceneTicketManagementServiceImpl")
	HyTicketSceneTicketManagementService hyTicketSceneTicketManagementService;
	
	@Resource(name = "hyTicketHotelandsceneServiceImpl")
	HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	@Resource(name = "hyTicketSubscribeServiceImpl")
	HyTicketSubscribeService hyTicketSubscribeService;
	
	@Resource(name = "hyVisaServiceImpl")
	HyVisaService hyVisaService;
	
	@Resource(name = "hyTicketSceneServiceImpl")
	HyTicketSceneService hyTicketSceneService;
	
	@Autowired
	BranchBalanceService branchBalanceService;

    @Autowired
	BranchPreSaveService branchPreSaveService;

	
	
	
	/*
	//票务  提前打款申请--首次提交
	@Override
	public Json addPiaowuSupplierSubmit(Long id, Long supplierContractId, List<Long> lineOrderIds, List<Long> ticketOrders,
			List<Long> lineRefunds, List<Long> ticketRefundIds, HttpSession session) 
					throws Exception {
		Json json = new Json();

		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);

		HySupplierContract supplierContract = hySupplierContractService.find(supplierContractId);
		PayablesLine payablesLine = payablesLineService.find(id);
		// 启动流程 完成task
		HashMap<String, Object> map = new HashMap<>();
        // 传入合同Id
		map.put("supplierContractId", supplierContractId);
        // 不推迟到下个周期结算
		map.put("submit", "thisTime");
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("PiaowuTNConfirm");
		Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
		Authentication.setAuthenticatedUserId(username);
		taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
		taskService.complete(task.getId(),map);

		// 新建申请PaymentSupplier
		PaymentSupplier paymentSupplier = new PaymentSupplier();

		List<Filter> filters = new LinkedList<>();
		filters.add(Filter.eq("type", SequenceTypeEnum.supplierSettlement));
		Long value = 0L;
		synchronized (this) {
			List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
			CommonSequence c = ss.get(0);
			if (c.getValue() >= 99999) {
				c.setValue(0L);
			}
			value = c.getValue() + 1;
			c.setValue(value);
			commonSequenceService.update(c);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowaday = sdf.format(new Date());
		String code = nowaday + String.format("%06d", value);
		paymentSupplier.setPayCode(code); // 打款确认单编号
		
		paymentSupplier.setSupplierName(supplierContract.getHySupplier().getSupplierName());
//		paymentSupplier.setOperator();
		paymentSupplier.setSupplierContract(supplierContract);
		// paymentSupplier.setMoneySum(moneySum);
		paymentSupplier.setCreateTime(new Date());
		paymentSupplier.setCreator(hyAdmin);
		paymentSupplier.setStatus(1); // (0 未审核-未付) 1审核中-未付 2 已通过-未付 3已通过-已付
										// 4已驳回-未付
		// paymentSupplier.setPayDate(payDate);
		paymentSupplier.setProcessInstanceId(pi.getProcessInstanceId());
		// 审核步骤 1:待采购部经理审核  3:待市场部副总审核 4:待总公司财务审核
		paymentSupplier.setStep(1); 
		// 首次提交没有金额调整				
		paymentSupplier.setModified(0);  
		// 申请置为有效状态
		paymentSupplier.setIsValid(1);  
		// 申请来源为 1:采购部员工提交
		paymentSupplier.setApplySource(1);
		//结算日期
		paymentSupplier.setSettleDate(payablesLine.getDate()); 
		paymentSupplierService.save(paymentSupplier);

		BigDecimal moneySum = new BigDecimal(0.00);
		HyAdmin operator = new HyAdmin();  // 产品计调
		// 建立payables_line_item表、payables_refund_item表和 payment_supplier表的关联
		if (lineOrderIds != null && lineOrderIds.size() > 0) {
			operator = payablesLineItemService.find(lineOrderIds.get(0)).getOperator();
			for (Long i : lineOrderIds) {
				PayablesLineItem p = payablesLineItemService.find(i);
				p.setState(1); // 0 未提交 1已提交
				p.setPaymentLineId(paymentSupplier.getId());
				moneySum = moneySum.add(p.getMoney());
				payablesLineItemService.update(p);
			}
		}
		if (ticketOrders != null && ticketOrders.size() > 0) {
			operator = payablesLineItemService.find(ticketOrders.get(0)).getOperator();
			for (Long i : ticketOrders) {
				PayablesLineItem p = payablesLineItemService.find(i);
				p.setState(1); // 0 未提交 1已提交
				p.setPaymentLineId(paymentSupplier.getId());
				moneySum = moneySum.add(p.getMoney());
				payablesLineItemService.update(p);
			}
		}
		if (lineRefunds != null && lineRefunds.size() > 0) {
			for (Long i : lineRefunds) {
				PayablesRefundItem p = payablesRefundItemService.find(i);
				p.setState(1); // 0 未提交 1已提交
				p.setPaymentLineId(paymentSupplier.getId());
				moneySum = moneySum.subtract(p.getRefundMoney());
				payablesRefundItemService.update(p);
			}
		}
		if (ticketRefundIds != null && ticketRefundIds.size() > 0) {
			for (Long i : ticketRefundIds) {
				PayablesRefundItem p = payablesRefundItemService.find(i);
				p.setState(1); // 0 未提交 1已提交
				p.setPaymentLineId(paymentSupplier.getId());
				moneySum = moneySum.subtract(p.getRefundMoney());
				payablesRefundItemService.update(p);
			}
		}
		
		// 更新paymentSupplier中的MoneySum和operator
		paymentSupplier.setOperator(operator);
		paymentSupplier.setMoneySum(moneySum);
		paymentSupplierService.update(paymentSupplier);
		
		
		
		// 修改PayablesLine的金额      由于订单或退款的产生会修改payablesLine，需要考虑同步的问题！
		
		payablesLine.setMoney(payablesLine.getMoney().subtract(moneySum));
		payablesLineService.update(payablesLine);
		
		
		json.setMsg("操作成功");
		json.setSuccess(true);
		return json;
	}
	//票务打款单    -- 实时打款  --  提交
	@Override
	public Json addPiaowuSupplierInstant(PayablesLine payablesLine) throws Exception {
		Json json = new Json();
		HySupplierContract supplierContract = payablesLine.getSupplierContract();
		// 启动流程 完成task
		HashMap<String, Object> map = new HashMap<>();
		map.put("supplierContractId", supplierContract.getId()); // 传入合同Id
		map.put("submit", "thisTime"); // 不推迟到下个周期结算
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("PiaowuTNConfirm");
		Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
		Authentication.setAuthenticatedUserId("");
		taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
		taskService.complete(task.getId(), map);

		// 新建申请PaymentSupplier
		PaymentSupplier paymentSupplier = new PaymentSupplier();

		List<Filter> filters = new LinkedList<>();
		filters.add(Filter.eq("type", SequenceTypeEnum.supplierSettlement));
		Long value;
		synchronized (this) {
			List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
			CommonSequence c = ss.get(0);
			if (c.getValue() >= 99999) {
				c.setValue(0L);
			}
			value = c.getValue() + 1;
			c.setValue(value);
			commonSequenceService.update(c);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowaday = sdf.format(new Date());
		String code = nowaday + String.format("%06d", value);
		paymentSupplier.setPayCode(code); // 打款确认单编号

		paymentSupplier.setSupplierName(supplierContract.getHySupplier().getSupplierName());
		// paymentSupplier.setOperator();
		paymentSupplier.setSupplierContract(supplierContract);
		// paymentSupplier.setMoneySum(moneySum);
		paymentSupplier.setCreateTime(new Date());
		paymentSupplier.setCreator(null);
		// (0 未审核-未付) 1审核中-未付 2 已通过-未付 3已通过-已付 4已驳回-未付
		paymentSupplier.setStatus(1); 
		// paymentSupplier.setPayDate(payDate);
		paymentSupplier.setProcessInstanceId(pi.getProcessInstanceId());
		// 审核步骤(提前付款使用) 0:被驳回待申请人处理 1:待采购部经理审核 3:待市场部副总限额审核 4:待总公司财务审核
        // 审核步骤(T+N付款  即时付款使用) 10:被驳回待财务处理 11:待供应商确认 12:待市场部副总限额审核 13:待总公司财务审核
		paymentSupplier.setStep(11); 
		// 首次提交没有金额调整
		paymentSupplier.setModified(0); 
		// 申请置为有效状态
		paymentSupplier.setIsValid(1);
		// 申请来源为 0:自动提交产生
		paymentSupplier.setApplySource(0); 
		// 结算日期
		paymentSupplier.setSettleDate(payablesLine.getDate()); 
		paymentSupplierService.save(paymentSupplier);

		BigDecimal moneySum = new BigDecimal(0.00);
		// 产品计调
		HyAdmin operator = new HyAdmin(); 

		// 建立payables_line_item表、payables_refund_item表和 payment_supplier表的关联
		filters.clear();
		filters.add(Filter.eq("payablesLineId", payablesLine.getId()));
		List<PayablesLineItem> list = payablesLineItemService.findList(null, filters, null);
		if (list != null && !list.isEmpty()) {
			operator = list.get(0).getOperator();
		}
		for (PayablesLineItem p : list) {
			// 0 未提交 1已提交
			p.setState(1); 
			p.setPaymentLineId(paymentSupplier.getId());
			moneySum = moneySum.add(p.getMoney());
			payablesLineItemService.update(p);
		}

		filters.clear();
		filters.add(Filter.eq("payablesLineId", payablesLine.getId()));
		List<PayablesRefundItem> list2 = payablesRefundItemService.findList(null, filters, null);
		for (PayablesRefundItem p : list2) {
			p.setState(1); // 0 未提交 1已提交
			p.setPaymentLineId(paymentSupplier.getId());
			moneySum = moneySum.subtract(p.getRefundMoney());
			payablesRefundItemService.update(p);
		}

		// 更新paymentSupplier中的MoneySum和operator
		paymentSupplier.setOperator(operator);
		paymentSupplier.setMoneySum(moneySum);
		// 修改PayablesLine的金额 由于订单或退款的产生会修改payablesLine，需要考虑同步的问题！
		payablesLine.setMoney(payablesLine.getMoney().subtract(moneySum)); 
		
		//add by wj
		//增加使用欠款
		String supplierName = supplierContract.getLiable().getUsername();
		filters.clear();
		filters.add(Filter.eq("supplierName", supplierName));
		List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null,filters,null);
		BigDecimal debt = new BigDecimal("0.0");//拿到欠款余额
		ReceiptTotalServicer receiptTotalServicer = new ReceiptTotalServicer();
		if(receiptTotalServicers.size()!=0&& !receiptTotalServicers.isEmpty()){
			receiptTotalServicer = receiptTotalServicers.get(0);
			debt = debt.add(receiptTotalServicer.getBalance());
			 //判断欠款与本次付款金额的大小
			if(debt.compareTo(moneySum)>=0){//欠款多于应付款
				debt = debt.subtract(moneySum);
				paymentSupplier.setDebtamount(moneySum);
			}else{
				paymentSupplier.setDebtamount(debt);
				debt = new BigDecimal("0.0");
			}
			//更新欠款余额表
			receiptTotalServicer.setBalance(debt);
			receiptTotalServicerService.update(receiptTotalServicer);
		}	  
		paymentSupplierService.update(paymentSupplier);

		payablesLineService.update(payablesLine);
		json.setMsg("操作成功");
		json.setSuccess(true);
		return json;
	}
	//票务 -- 打款申请 --定时扫描提交
	@Override
	public void addPiaowuSuppierAuto(Long supplierContractId) throws Exception {
		 HySupplierContract supplierContract = hySupplierContractService.find(supplierContractId);
	        List<Filter> f = new ArrayList<>();
	        f.add(Filter.eq("supplierContract", supplierContract));
	        f.add(Filter.le("date", DateUtil.getEndOfDay(new Date())));
	        List<PayablesLine> list = payablesLineService.findList(null, f, null);

	        if(CollectionUtils.isEmpty(list)){
	            return;
	        }
	        // 启动流程 完成task
	        HashMap<String, Object> map = new HashMap<>();
	        map.put("supplierContractId", supplierContract.getId()); // 传入合同Id
	        map.put("submit", "thisTime"); // 不推迟到下个周期结算
	        ProcessInstance pi = runtimeService.startProcessInstanceByKey("payServicePreTN");
	        Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
	        Authentication.setAuthenticatedUserId("");
	        taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
	        taskService.complete(task.getId(), map);

	        // 新建申请PaymentSupplier
	        PaymentSupplier paymentSupplier = new PaymentSupplier();

	        List<Filter> filters = new LinkedList<>();
	        filters.add(Filter.eq("type", SequenceTypeEnum.supplierSettlement));
	        Long value;
	        synchronized (this) {
	            List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
	            CommonSequence c = ss.get(0);
	            if (c.getValue() >= 99999) {
	                c.setValue(0L);
	            }
	            value = c.getValue() + 1;
	            c.setValue(value);
	            commonSequenceService.update(c);
	        }
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	        String nowaday = sdf.format(new Date());
	        String code = nowaday + String.format("%06d", value);
	        // 打款确认单编号
	        paymentSupplier.setPayCode(code);

	        paymentSupplier.setSupplierName(supplierContract.getHySupplier().getSupplierName());
	        // paymentSupplier.setOperator();
	        paymentSupplier.setSupplierContract(supplierContract);
	        // paymentSupplier.setMoneySum(moneySum);
	        paymentSupplier.setCreateTime(new Date());
	        paymentSupplier.setCreator(null);
	        // (0 未审核-未付) 1审核中-未付 2 已通过-未付 3已通过-已付 4已驳回-未付
	        paymentSupplier.setStatus(1);
	        // paymentSupplier.setPayDate(payDate);
	        paymentSupplier.setProcessInstanceId(pi.getProcessInstanceId());
	        // 审核步骤(提前付款使用) 0:被驳回待申请人处理 1:待采购部经理审核 3:待市场部副总限额审核 4:待总公司财务审核
	        // 审核步骤(T+N付款  即时付款使用) 10:被驳回待财务处理 11:待供应商确认 12:待市场部副总限额审核 13:待总公司财务审核
	        paymentSupplier.setStep(11); 
	        // 首次提交没有金额调整
	        paymentSupplier.setModified(0); 
	        // 申请置为有效状态
	        paymentSupplier.setIsValid(1); 
	        // 申请来源为 0:自动提交产生
	        paymentSupplier.setApplySource(0); 
	        // 多个不同结算日期的payablesLine合并得到一个paymentSupplier,打款申请paymentSupplier中的结算日期直接设为当前日期
	        paymentSupplier.setSettleDate(new Date());
	        paymentSupplierService.save(paymentSupplier);

	        BigDecimal moneySumForPaymentSupplier = new BigDecimal(0);
	        HyAdmin operator = new HyAdmin(); // 产品计调
	        // 建立payables_line_item表和 payment_supplier表的关联
	        for (PayablesLine payablesLine : list) {
	            filters.clear();
	            filters.add(Filter.eq("payablesLineId", payablesLine.getId()));
	            // 未提交审核(state为0)的item
	            filters.add(Filter.eq("state", 0));
	            List<PayablesLineItem> l = payablesLineItemService.findList(null, filters, null);
	            if (l != null && !l.isEmpty()) {
	                operator = l.get(0).getOperator();
	            }
	            BigDecimal moneySum = new BigDecimal(0); 
	            for (PayablesLineItem p : l) {
	                p.setState(1); // 0 未提交 1已提交
	                p.setPaymentLineId(paymentSupplier.getId());
	                payablesLineItemService.update(p);
	                moneySum = moneySum.add(p.getMoney());
	            }
	            // 修改PayablesLine的金额 因为全部选中 应该为0
	            payablesLine.setMoney(payablesLine.getMoney().subtract(moneySum));
	            moneySumForPaymentSupplier = moneySumForPaymentSupplier.add(moneySum);
	        }
	        // 更新paymentSupplier中的MoneySum和operator
	        // TODO 这里是多个payablesline按合同合并为一个paymentSupplier operator的一致性存疑
	        paymentSupplier.setOperator(operator);
	        paymentSupplier.setMoneySum(moneySumForPaymentSupplier);


	        // TODO 欠款的使用需要以合同为单位  这里以登录名称作为合同的唯一判定
	        //add by wj
	        //增加使用欠款
	        String supplierName = supplierContract.getLiable().getUsername();
	        filters.clear();
	        filters.add(Filter.eq("supplierName", supplierName));
	        List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null,filters,null);
	        BigDecimal debt = new BigDecimal("0.0");//拿到欠款余额
	        ReceiptTotalServicer receiptTotalServicer = new ReceiptTotalServicer();
	        if(receiptTotalServicers.size()!=0&& !receiptTotalServicers.isEmpty()){
	            receiptTotalServicer = receiptTotalServicers.get(0);
	            debt = debt.add(receiptTotalServicer.getBalance());
	            //判断欠款与本次付款金额的大小
	            if(debt.compareTo(moneySumForPaymentSupplier)>=0){//欠款多于应付款
	                debt = debt.subtract(moneySumForPaymentSupplier);
	                paymentSupplier.setDebtamount(moneySumForPaymentSupplier);
	            }else{
	                paymentSupplier.setDebtamount(debt);
	                debt = new BigDecimal("0.0");
	            }
	            //更新欠款余额表
	            receiptTotalServicer.setBalance(debt);
	            receiptTotalServicerService.update(receiptTotalServicer);
	        }

	        paymentSupplierService.update(paymentSupplier);
		
	}
	//票务 --打款申请 -- 审核
	@Override
	public Json addPiaowuSupplierAudit(Long id, String comment, Integer state, String dismissRemark,
			BigDecimal modifyAmount, HttpSession session) throws Exception {
		Json json = new Json();

		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		HyAdmin hyAdmin = hyAdminService.find(username);
		PaymentSupplier paymentSupplier = paymentSupplierService.find(id);
		String processInstanceId = paymentSupplier.getProcessInstanceId();

		if (processInstanceId == null || processInstanceId == "") {
			json.setSuccess(false);
			json.setMsg("审核出错，信息不完整，请重新申请");
		} else {
			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();

			HashMap<String, Object> map = new HashMap<>();

			Integer applySource = paymentSupplier.getApplySource();
			
			if(applySource == 1){  // 申请来源为采购部员工手动提交-提前付款
				
				if (state == 1) { // 审核通过
					map.put("result", "tongguo");
					// 删除提前打款审核流程的的第二步 待供应商确认
					if (paymentSupplier.getStep() == 1) { // 若当前步骤为 1:待采购部经理审核
						paymentSupplier.setStep(3);
						List<Filter> filters = new ArrayList<>();
						filters.add(Filter.eq("eduleixing", Eduleixing.payServicerLimit));
						List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
						BigDecimal money = edu.get(0).getMoney();
						if (paymentSupplier.getMoneySum().doubleValue() > money.doubleValue()) { // 超过额度
							map.put("money", "more");
							paymentSupplier.setStep(3); // 步骤置为 3:待市场部副总审核
						} else {
							map.put("money", "less");
							paymentSupplier.setStep(4); // 步骤置为 4:待总公司财务审核
						}
					}
					else if (paymentSupplier.getStep() == 3) { // 若当前状态为"3:待市场部副总副总审核"
						paymentSupplier.setStep(4); // 步骤置为 3:待总公司财务财务审核
					} else if (paymentSupplier.getStep() == 4) { // 若当前状态为
																	// 4:待总公司财务审核
						// 修改paymentSupplier的审核状态
						paymentSupplier.setStatus(2); // 状态 置为"2:已通过-未付"
						

						// 生成 总公司 收支记录 - 待付款
						PayServicer payServicer = new PayServicer();
						// payServicer.setDepartmentId(departmentId);
						payServicer.setReviewId(id);
						payServicer.setHasPaid(0); // 0 未付
						payServicer.setType(3); // //1:分公司预付款 2:T+N 3:提前打款
												// 4:旅游元素供应商尾款 5:向酒店/门票/酒加景供应商付款
												// 6:江泰预充值 7:总公司预付款
						payServicer.setApplyDate(paymentSupplier.getCreateTime());
						payServicer.setAppliName(paymentSupplier.getCreator().getName());
						payServicer.setServicerId(paymentSupplier.getSupplierContract().getHySupplier().getId());
						payServicer.setServicerName(paymentSupplier.getSupplierName());
						payServicer.setConfirmCode(paymentSupplier.getPayCode());
						payServicer.setAmount(paymentSupplier.getMoneySum());
						// payServicer.setRemark(remark);
						payServicer.setBankListId(paymentSupplier.getSupplierContract().getBankList().getId());
						payServicerService.save(payServicer);

					}
				} else if (state == 0) { // 审核未通过
					map.put("result", "bohui");
					paymentSupplier.setStatus(4); // 1审核中-未付 2 已通过-未付 3已通过-已付
													// 4已驳回-未付
					paymentSupplier.setStep(0); // 步骤置为 0:被驳回待申请人处理
				}
				
				
			}else{ // 申请来源为系统自动产生 - T+N付款
				if(state == 1){ // 审核通过
					map.put("result", "tongguo");
					if(paymentSupplier.getStep() == 11){ // 若当前步骤为 11:待供应商确认
						List<Filter> filters = new ArrayList<>();
						filters.add(Filter.eq("eduleixing", Eduleixing.payServicerLimit));
						List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
						BigDecimal money = edu.get(0).getMoney();
						if (paymentSupplier.getMoneySum().doubleValue() > money.doubleValue()) { // 超过额度
							map.put("money", "more");
							paymentSupplier.setStep(12); // 步骤置为 12:待市场部副总限额审核
						} else {
							map.put("money", "less");
							paymentSupplier.setStep(13); // 步骤置为 13:待总公司财务审核
						}
					}else if(paymentSupplier.getStep() == 12){ // 若当期步骤为 12:待市场部副总限额审核
						paymentSupplier.setStep(13);
					}else if(paymentSupplier.getStep() == 13){ // 若当前步骤为 13:待总公司财务审核
						// 修改paymentSupplier的审核状态
						paymentSupplier.setStatus(2); // 状态 置为"2:已通过-未付"

						// 生成 总公司 收支记录 - 待付款
						PayServicer payServicer = new PayServicer();
						// payServicer.setDepartmentId(departmentId);
						payServicer.setReviewId(id);
						payServicer.setHasPaid(0); // 0 未付
						payServicer.setType(2); // //1:分公司预付款 2:T+N 3:提前打款
												// 4:旅游元素供应商尾款 5:向酒店/门票/酒加景供应商付款
												// 6:江泰预充值 7:总公司预付款
						payServicer.setApplyDate(paymentSupplier.getCreateTime());
						payServicer.setAppliName(null == paymentSupplier.getCreator()?"":paymentSupplier.getCreator().getName());
						payServicer.setServicerId(paymentSupplier.getSupplierContract().getHySupplier().getId());
						payServicer.setServicerName(paymentSupplier.getSupplierName());
						payServicer.setConfirmCode(paymentSupplier.getPayCode());
						
						//add by wj
						if(paymentSupplier.getDebtamount()!=null){
							payServicer.setAmount(paymentSupplier.getMoneySum().subtract(paymentSupplier.getDebtamount()));
						}else{
							payServicer.setAmount(paymentSupplier.getMoneySum());
						}
//						payServicer.setAmount(paymentSupplier.getMoneySum());
						// payServicer.setRemark(remark);
						payServicer.setBankListId(paymentSupplier.getSupplierContract().getBankList().getId());
						payServicerService.save(payServicer);
					}
					
				}else if(state == 0){ // 审核未通过
					map.put("result", "bohui");
					paymentSupplier.setStatus(4); // 1审核中-未付 2 已通过-未付 3已通过-已付 4已驳回-未付
					paymentSupplier.setStep(10); // 步骤置为 0:被驳回待财务处理
					if(paymentSupplier.getDebtamount()!=null 
							&& paymentSupplier.getDebtamount().compareTo(new BigDecimal("0.0"))!=0){
						String supplierName = paymentSupplier.getSupplierContract().getLiable().getUsername();
						List<Filter> filters = new ArrayList<>();
						filters.add(Filter.eq("supplierName", supplierName));
						List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null,filters,null);
						if(receiptTotalServicers.size()!=0 && receiptTotalServicers!=null){
							receiptTotalServicers.get(0).setBalance(receiptTotalServicers.get(0).getBalance().add(paymentSupplier.getDebtamount()));
							receiptTotalServicerService.update(receiptTotalServicers.get(0));
						}
					}
				}
			}
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), processInstanceId, (comment == null ? " " : comment) + ":" + state);
			taskService.complete(task.getId(), map);
			paymentSupplierService.update(paymentSupplier);

			json.setSuccess(true);
			json.setMsg("审核完成");
		}
		return json;
	}
	//（提前打款）提交人对被驳回的申请进行修改
	@Override
	public Json updatePiaowuApply(Long id, Integer type, String dismissRemark, BigDecimal modifyAmount,
			HttpSession session) {
		// TODO Auto-generated method stub
		return null;
	}
	//(自动打款)财务 对被驳回进行修改
	@Override
	public Json updatePiaowuApply2(Long id, Integer type, String dismissRemark, BigDecimal modifyAmount,
			HttpSession session) {
		// TODO Auto-generated method stub
		return null;
	}

	*/
	
	
	/**
	 * 票务  产品订购  供应商审核 完成后  的财务记录
	 * id 为orderId
	 * type: 1线路 2酒店 3门票 4酒加景 5签证 6认购门票
	 */
	public boolean orderPiaowuConfirm(Long id,Integer type,HttpSession session){
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		if(admin.getHyAdmin()!=null) admin = admin.getHyAdmin();
//		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);

                // 供应商确认订单后  生成打款记录 
                BigDecimal money = order.getJiesuanMoney1().subtract(order.getDiscountedPrice());

                HyOrderItem hyOrderItem = order.getOrderItems().get(0);
                // prodectId实际上为groupId
                Long ticketId = hyOrderItem.getProductId();

                // 获取合同
                List<Filter> filters = new ArrayList<>();
//                filters.add(Filter.eq("ContractStatus", ContractStatus.zhengchang.ordinal()));
                filters.add(Filter.eq("liable", admin));
                HySupplierContract contract =  new HySupplierContract();
                List<HySupplierContract> contracts = hySupplierContractService.findList(null,filters,null);

                for(HySupplierContract con : contracts){
                	if(con.getContractStatus().equals(ContractStatus.zhengchang)){
                		contract = con;
                	}
                }
                if(contract.getId() == null) 
                	throw new Exception("没有正常状态的合同");
                if(!contract.getHySupplier().getIsInner()){  //外部供应商
                	// 获取结算方式
                    Settle settle = contract.getSettle();
                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();

                    // 如果是实时结  或T+N的N为负但下单日期过了应结算日期   直接开启打款审批
                    // TODO 可能造成的问题是  同一个合同 同一结算日期 在hy_payables_line中有多条,  条目分散,需要在定时器扫描时进行合并
                    Date tnDate = null;
                    if (Settle.tjiaN.equals(settle)) {
                    	Date startDate = new Date();
                    	startDate = hyOrderItem.getStartDate();
                        tnDate = DateUtil.getDateAfterSpecifiedDays(startDate, contract.getDateN());
                    }
                    if (Settle.shishijie.equals(settle) || (Settle.tjiaN.equals(settle) && order.getCreatetime().after(tnDate))) {
                        // 在payableLine中增加数据
                        PayablesLine payablesLine = new PayablesLine();
                        payablesLine.setServicerName(contract.getHySupplier().getSupplierName());
//                        payablesLine.setOperator(contract.getCreater());
                        
                        HyAdmin operator = new HyAdmin();
                        switch(type){
                		case 2:{
                			 operator = hyTicketHotelRoomService.find(hyOrderItem.getSpecificationId()).getHyTicketHotel().getCreator();
                			break;
                		}
                		case 3:{
                			 operator = hyTicketSceneTicketManagementService.find(hyOrderItem.getProductId()).getHyTicketScene().getCreator();
                			break;
                		}
                		case 4:{
                			 operator = hyTicketHotelandsceneService.find(hyOrderItem.getProductId()).getCreator();
                			break;
                		}
                		case 6:{
                			 operator = hyTicketSubscribeService.find(hyOrderItem.getProductId()).getCreator();
                			break;
                		}
                		case 5:{
                			 operator = hyVisaService.find(hyOrderItem.getProductId()).getCreator();
                			break;
                		}
                        }
                		payablesLine.setOperator(operator);
                        payablesLine.setSupplier(contract.getHySupplier());
                        payablesLine.setSupplierContract(contract);
                        /**0票务 1线路*/
                        payablesLine.setSupplierType(0);
                        payablesLine.setDate(date);
                        // jiesuanMoney1 - youhuiMoney + tiaozhengMoney
                        payablesLine.setMoney(money.subtract(order.getKoudianMoney()));
                        payablesLineService.save(payablesLine);

                        //实时结算和 t+n结算 tDate都一样，tDate对实时结算来讲没有影响
                        Date startDate = new Date();
                    	startDate = hyOrderItem.getStartDate();
                        // 在payableLineItem中增加数据
                        savePayablesLineItem(payablesLine, order, contract, date, money,type,hyOrderItem,startDate);
                        //
                        paymentSupplierService.addPaymentSuppierInstant(payablesLine);
                    }
                    // 非实时的方式
                    else {
                        // 月结
                        if (Settle.yuejie.equals(settle)) {
                            calendar.add(Calendar.MONTH, 1);
                            calendar.set(Calendar.DATE, 1);
                            date = calendar.getTime();
                        }
                        // 半月结
                        else if (Settle.banyuejie.equals(settle)) {
                            int d = calendar.get(Calendar.DATE);
                            if (d >= 15) {
                                calendar.add(Calendar.MONTH, 1);
                                calendar.set(Calendar.DATE, 1);
                                date = calendar.getTime();
                            } else {
                                calendar.set(Calendar.DATE, 15);
                                date = calendar.getTime();
                            }
                        }
                        // 周结
                        else if (Settle.zhoujie.equals(settle)) {
                            // c为下周周三
                            Calendar c = Calendar.getInstance();
                            int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
                            if (day_of_week == 0) {
                                day_of_week = 7;
                            }
                            c.add(Calendar.DATE, -day_of_week + 10);
                            date = c.getTime();
                        }
                        // T+N结算
                        else if (Settle.tjiaN.equals(settle)) {
                            date = tnDate;
                        }
                        // 对于非实时  (contract, 结算日期) 应当唯一确定一条 payablesLine
                        filters.clear();
                        filters.add(Filter.eq("supplierContract", contract));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateString = sdf.format(date);
                        filters.add(new Filter("date", Operator.ge, sdf.parse(dateString.substring(0, 10) + " " + "00:00:00")));
                        filters.add(new Filter("date", Operator.le, sdf.parse(dateString.substring(0, 10) + " " + "23:59:59")));
                        List<PayablesLine> payablesLines = payablesLineService.findList(null, filters, null);

                        Date startDate = new Date();
                    	startDate = hyOrderItem.getStartDate();
                        if (CollectionUtils.isNotEmpty(payablesLines)) {
                            PayablesLine payablesLine = payablesLines.get(0);
                            payablesLine.setMoney(payablesLine.getMoney().add(money).subtract(order.getKoudianMoney()));
                            payablesLineService.update(payablesLine);

                            // 在payableLineItem中增加数据
                            savePayablesLineItem(payablesLine, order, contract, date, money,type,hyOrderItem,startDate);
                        } else {
                            // 在payableLine中增加数据
                            PayablesLine payablesLine = new PayablesLine();
                            payablesLine.setServicerName(contract.getHySupplier().getSupplierName());
//                            payablesLine.setOperator(contract.getCreater());
                            
                            HyAdmin operator = new HyAdmin();
                            switch(type){
                    		case 2:{
                    			 operator = hyTicketHotelRoomService.find(hyOrderItem.getSpecificationId()).getHyTicketHotel().getCreator();
                    			break;
                    		}
                    		case 3:{
                    			 operator = hyTicketSceneTicketManagementService.find(hyOrderItem.getSpecificationId()).getHyTicketScene().getCreator();
                    			break;
                    		}
                    		case 4:{
                    			 operator = hyTicketHotelandsceneService.find(hyOrderItem.getProductId()).getCreator();
                    			break;
                    		}
                    		case 6:{
                    			 operator = hyTicketSubscribeService.find(hyOrderItem.getProductId()).getCreator();
                    			break;
                    		}
                    		case 5:{
                    			 operator = hyVisaService.find(hyOrderItem.getProductId()).getCreator();
                    			break;
                    		}
                            }
                    		payablesLine.setOperator(operator);
                            
                            
                            
                            payablesLine.setSupplier(contract.getHySupplier());
                            payablesLine.setSupplierContract(contract);
                            // 0票务 1线路
                            payablesLine.setSupplierType(0);
                            payablesLine.setDate(date);
                            // jiesuanMoney1 - youhuiMoney + tiaozhengMoney
                            payablesLine.setMoney(money.subtract(order.getKoudianMoney()));
                            payablesLineService.save(payablesLine);

                            // 在payableLineItem中增加数据
                            savePayablesLineItem(payablesLine, order, contract, date, money,type,hyOrderItem,startDate);
                        }
                    }
                }
     

                
                return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void savePayablesLineItem(PayablesLine payablesLine, HyOrder order, HySupplierContract contract,
			Date date, BigDecimal money,Integer type,HyOrderItem hyOrderItem,Date tDate) {
		PayablesLineItem item = new PayablesLineItem();
		item.setPayablesLineId(payablesLine.getId());
		item.setHyOrder(order);
		item.setSupplierContract(contract);
		item.setOperator(contract.getCreater());
		// 1线路 2酒店 3门票 4酒加景 5签证 6认购门票
		item.setProductType(type);
		item.setTicketId(hyOrderItem.getProductId());
		String sn = "";
		String productName = "";
		HyAdmin operator = new HyAdmin();
		switch(type){
		case 2:{
			sn = hyTicketHotelRoomService.find(hyOrderItem.getSpecificationId()).getProductId();
			String hotelName = hyTicketHotelService.find(hyOrderItem.getProductId()).getHotelName();
			String roomName = hyTicketHotelRoomService.find(hyOrderItem.getSpecificationId()).getProductName();
			productName = hotelName + "-" + roomName;
			operator = hyTicketHotelRoomService.find(hyOrderItem.getSpecificationId()).getHyTicketHotel().getCreator();
			break;
		}
		case 3:{
			sn = hyTicketSceneTicketManagementService.find(hyOrderItem.getSpecificationId()).getProductId();
			productName = hyTicketSceneTicketManagementService.find(hyOrderItem.getSpecificationId()).getProductName();
			operator = hyTicketSceneTicketManagementService.find(hyOrderItem.getSpecificationId()).getHyTicketScene().getCreator();
			break;
		}
		case 4:{
			sn = hyTicketHotelandsceneService.find(hyOrderItem.getProductId()).getProductId();
			productName = hyTicketHotelandsceneService.find(hyOrderItem.getProductId()).getProductName();
			operator = hyTicketHotelandsceneService.find(hyOrderItem.getProductId()).getCreator();
			break;
		}
		case 6:{
			sn = hyTicketSubscribeService.find(hyOrderItem.getProductId()).getProductId();
			productName = hyTicketSubscribeService.find(hyOrderItem.getProductId()).getSceneName();
			operator = hyTicketSubscribeService.find(hyOrderItem.getProductId()).getCreator();
			break;
		}
		case 5:{
			sn = hyVisaService.find(hyOrderItem.getProductId()).getProductId();
			productName = hyVisaService.find(hyOrderItem.getProductId()).getProductName();
			operator = hyVisaService.find(hyOrderItem.getProductId()).getCreator();
			break;
		}
		default:
			break;
		}
		item.setOperator(operator);
		item.setSn(sn);
		item.setProductName(productName);
		item.settDate(tDate);
		item.setSettleDate(date);
		item.setOrderMoney(money);
		item.setKoudian(order.getKoudianMoney());
		item.setMoney(money.subtract(order.getKoudianMoney()));
		// 0:未提交 1:已提交
		item.setState(0);
		payablesLineItemService.save(item);
	}

	
	/**
	 * 票务售后退款
	 * type: 1线路 2酒店 3门票 4酒加景 5签证 6认购门票
	 * remark : 酒店售后，门票售后，酒加景售后，签证售后，认购门票售后
	 */
	// type: 2酒店 3门票 4酒加景 5签证 6认购门票
	//       10  8   10    9   15
	@Override
	public boolean shouhouPiaowuRefund(HyOrderApplication application,String username, Integer type,String remark) {
		
		try {
			// add by wj
			HyOrder hyOrder = hyOrderService.find(application.getOrderId());
			HyOrderItem hyOrderItem = hyOrder.getOrderItems().get(0);
			Long productId = hyOrderItem.getProductId(); // prodectId实际上为groupId
			HySupplierContract contract =  new HySupplierContract();
			HyAdmin admin = new HyAdmin();
			
			switch (type) {
			case 2:  //酒店
				admin = hyTicketHotelRoomService.find(hyOrderItem.getSpecificationId()).getHyTicketHotel().getCreator();
				break;
			case 3:
				admin = hyTicketSceneTicketManagementService.find(hyOrderItem.getSpecificationId()).getHyTicketScene().getCreator();
				break;
			case 4:  //酒加景
				admin = hyTicketHotelandsceneService.find(productId).getCreator();
				break;
			case 5: //签证
				admin = hyVisaService.find(productId).getCreator();
				break;
			case 6:
				admin = hyTicketSubscribeService.find(productId).getCreator();
				break;
			default:
				break;
			}
			
			List<Filter> filters = new ArrayList<>();
			if(admin.getHyAdmin()!=null) admin = admin.getHyAdmin();
            filters.add(Filter.eq("liable", admin));
            List<HySupplierContract> contracts = hySupplierContractService.findList(null,filters,null);
            for(HySupplierContract con : contracts){
            	if(con.getContractStatus().equals(ContractStatus.zhengchang)){
            		contract = con;
            	}
            }
            if(contract.getId() == null) 
            	throw new Exception("没有正常状态的合同");

			BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianWaimaiMoney());
			BigDecimal balance = new BigDecimal(0);
			BigDecimal qiankuan = application.getJiesuanMoney();
			if (hyOrder.getKoudianMethod().equals(Constants.DeductLine.tuanke.ordinal())) {
				qiankuan =qiankuan.subtract(application.getJiesuanMoney().multiply(hyOrder.getProportion()).multiply(BigDecimal.valueOf(0.01)));
			}
			
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
				receiptTotalServicer.setBalance(qiankuan);
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

			Date date = new Date();
			

			// add by wj 更新扣点
			BigDecimal koudian = new BigDecimal(0);
			
			if (hyOrder.getKoudianMethod().equals(Constants.DeductLine.tuanke.ordinal())) {
				koudian = application.getJiesuanMoney().multiply(hyOrder.getProportion()).multiply(BigDecimal.valueOf(0.01));
			}
			boolean ifSameMonth = isSameMonth(receiptServicer.getDate(), hyOrder.getJiesuantime());

			

			PayandrefundRecord record = new PayandrefundRecord();
			record.setOrderId(hyOrder.getId());
			record.setMoney(tuiKuan);
			record.setPayMethod(5); // 5预存款
			record.setType(1); // 1退款
			record.setStatus(1); // 1已退款
			record.setCreatetime(date);
			payandrefundRecordService.save(record);

			if(hyOrder.getSource() == 0){
				// 更新预存款相关部分
				Long storeId = hyOrder.getStoreId();
				Store store = storeService.find(storeId);
				
				// 写入退款记录 //预存款余额修改
				RefundInfo refundInfo = new RefundInfo();
				refundInfo.setAmount(tuiKuan);
				refundInfo.setAppliName(application.getOperator().getName());
				refundInfo.setApplyDate(application.getCreatetime());
				refundInfo.setPayDate(date);
				refundInfo.setRemark(remark);
				refundInfo.setState(1); // 已付款
				refundInfo.setType(13);  //门店售后
//				switch(type){
//				case 2:refundInfo.setType(10);break; //酒店
//				case 3:refundInfo.setType(8);break; //门票
//				case 4:refundInfo.setType(10);break; //酒加景
//				case 5:refundInfo.setType(9);break; //签证
//				case 6:refundInfo.setType(15); //认购门票	
//				}

				refundInfo.setKoudian(koudian);
				refundInfo.setOrderId(hyOrder.getId());
				
				BigDecimal orderkoudian = hyOrder.getKoudianMoney();
				orderkoudian = orderkoudian.subtract(koudian);
				hyOrder.setKoudianMoney(orderkoudian);
				hyOrderService.update(hyOrder);
				
				if (ifSameMonth) {
				
					refundInfo.setIfTongji(true);
				} else {
					refundInfo.setIfTongji(false);
				}

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
				records.setStoreId(storeId);
				records.setStoreName(store.getStoreName());
				records.setTouristName(hyOrder.getContact());
				records.setTouristAccount(store.getBankList().getBankAccount()); // 门店账号
				records.setSignUpMethod(1); // 门店
				refundRecordsService.save(records);
				
				// 预存款余额表
				// 3、修改门店预存款表 并发情况下的数据一致性！

				if(store.getStoreType() != 2){
					List<Filter> filters2 = new ArrayList<>();
					filters2.add(Filter.eq("store", store));
					List<StoreAccount> list = storeAccountService.findList(null, filters2, null);
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
					storeAccountLog.setType(type+6);
					storeAccountLog.setProfile(remark);
					storeAccountLogService.update(storeAccountLog);

					// 5、修改 总公司-财务中心-门店预存款表
					StorePreSave storePreSave = new StorePreSave();
					storePreSave.setStoreName(store.getStoreName());
					storePreSave.setStoreId(storeId);
					switch(type){
					case 2:storePreSave.setType(11);break; //酒店
					case 3:storePreSave.setType(21);break; //门票
					case 4:storePreSave.setType(16);break; //酒加景
					case 5:storePreSave.setType(9);break; //签证
					case 6:storePreSave.setType(20); //认购门票
					}
												////  //1:门店充值 2:报名退款  3:报名冲抵 4:门店认购门票冲抵 
												//  5:电子门票退款 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵 9:签证退款 
												//	10:酒店销售 11:酒店退款 12:门店后返 13:供应商驳回订单 14:门店租导游 
					 							//  15:酒加景销售 16:酒加景退款 17:门店综合服务18：租借导游退款 19:保险退款20:认购门票退款 21：门店门票退款 
					storePreSave.setDate(date);
					storePreSave.setAmount(tuiKuan);
					storePreSave.setOrderCode(hyOrder.getOrderNumber());
					storePreSave.setOrderId(hyOrder.getId());
					storePreSave.setPreSaveBalance(storeAccountService.findList(null, filters2, null).get(0).getBalance());
					storePreSaveService.save(storePreSave);
				}else{
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
					StringBuffer buffer = new StringBuffer();
					switch(type){
					case 2:{
						branchPreSave.setType(12);
						buffer.append("酒店退款");
						break; //酒店
					}
					case 3:{
						branchPreSave.setType(13);
						buffer.append("门票退款");
						break; //门票
					}
					case 4:{
						branchPreSave.setType(14);
						buffer.append("酒加景退款");
						break; //酒加景
					}
					case 5:{
						branchPreSave.setType(15);
						buffer.append("签证退款");
						break; //签证
					}
					case 6:{
						branchPreSave.setType(16);
						buffer.append("认购门票退款");//认购门票
					}
					default:{
						branchPreSave.setType(-1);
						buffer.append("不属于票务5种情况退款");
					}
					}
					branchPreSave.setRemark(buffer.toString());
//					branchPreSave.setType(11); //退团
					branchPreSaveService.save(branchPreSave);
				}
			}
			
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	/**
	 * 票务退款
	 * @param application  
	 * @param username
	 * @param type  1线路 2酒店 3门票 4酒加景 5签证 6认购门票
	 * @param remark：门店酒店退款，门店门票退款，门店酒加景退款，门店签证退款，门店认购门票退款
	 * @return
	 */
	public boolean piaowuRefund(HyOrderApplication application,String username,Integer type,String remark){
		try {
//			application.setStatus(4); // 已退款
			
			Long orderId = application.getOrderId();
			HyOrder hyOrder = hyOrderService.find(orderId);
			Long storeId = hyOrder.getStoreId();
			Store store = storeService.find(storeId);
			
			HyOrderItem hyOrderItem = hyOrder.getOrderItems().get(0);
			Long productId = hyOrderItem.getProductId(); // prodectId实际上为groupId
			HySupplierContract contract =  new HySupplierContract();
			HyAdmin admin = new HyAdmin();
			
			//更新订单表的扣点
			BigDecimal koudian = new BigDecimal(0);
			if (hyOrder.getKoudianMethod().equals(Constants.DeductLine.tuanke.ordinal())) {
				koudian = application.getJiesuanMoney().multiply(hyOrder.getProportion()).multiply(BigDecimal.valueOf(0.01));
			}
			hyOrder.setKoudianMoney(hyOrder.getKoudianMoney().subtract(koudian));
			hyOrderService.update(hyOrder);
			
			BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianWaimaiMoney());
			BigDecimal qiankuan = application.getJiesuanMoney().subtract(koudian);
			
			
	
			
			
            List<Filter> filters = new ArrayList<>();
//            filters.clear();
			filters.add(Filter.eq("hyOrder",hyOrder));
			List<PayablesLineItem> items = payablesLineItemService.findList(null,filters,null);
			if(!items.isEmpty()){
				PayablesLineItem item = items.get(0);
				
				// 找到盖产品对应的供应商合同账号
				switch (type) {
				case 2:  //酒店
					admin = hyTicketHotelRoomService.find(hyOrderItem.getSpecificationId()).getHyTicketHotel().getCreator();
					break;
				case 3:
					admin = hyTicketSceneTicketManagementService.find(hyOrderItem.getSpecificationId()).getHyTicketScene().getCreator();
					break;
				case 4:  //酒加景
					admin = hyTicketHotelandsceneService.find(productId).getCreator();
					break;
				case 5: //签证
					admin = hyVisaService.find(productId).getCreator();
					break;
				case 6:
					admin = hyTicketSubscribeService.find(productId).getCreator();
					break;
				default:
					break;
				}
				
				
				List<Filter> filters1 = new ArrayList<>();
				if(admin.getHyAdmin()!=null) admin = admin.getHyAdmin();
	            filters.add(Filter.eq("liable", admin));
	            List<HySupplierContract> contracts = hySupplierContractService.findList(null,filters1,null);
	            for(HySupplierContract con : contracts){
	            	if(con.getContractStatus().equals(ContractStatus.zhengchang)){
	            		contract = con;
	            	}
	            }
	            if(contract.getId() == null) 
	            	throw new Exception("没有正常状态的合同");
				
				//打款单未提交
				if(item.getState() == 0){
					//更新payablelines
					PayablesLine payablesLine = payablesLineService.find(item.getPayablesLineId());
					payablesLine.setMoney(payablesLine.getMoney().subtract(tuiKuan));
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
						receiptTotalServicer.setBalance(qiankuan);
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
			
			Date date = new Date();
			

			PayandrefundRecord record = new PayandrefundRecord();
			record.setOrderId(orderId);
			record.setMoney(tuiKuan);
			record.setPayMethod(5);	//5预存款
			record.setType(1);	//1退款
			record.setStatus(1);	//1已退款
			record.setCreatetime(date);
			payandrefundRecordService.save(record);
					
			//预存款余额表
			// 3、修改门店预存款表      并发情况下的数据一致性！
			if(hyOrder.getSource()==0){
				
				//写入退款记录   //预存款余额修改
				RefundInfo refundInfo = new RefundInfo();
				refundInfo.setAmount(tuiKuan);
				refundInfo.setAppliName(application.getOperator().getName());
				refundInfo.setApplyDate(application.getCreatetime());
				refundInfo.setPayDate(date);
				refundInfo.setRemark(remark);
				refundInfo.setState(1);  //已付款
				refundInfo.setOrderId(orderId);
				switch(type){
				case 2:refundInfo.setType(10);break; //酒店
				case 3:refundInfo.setType(8);break; //门票
				case 4:refundInfo.setType(10);break; //酒加景
				case 5:refundInfo.setType(9);break; //签证
				case 6:refundInfo.setType(15); //认购门票	
				} 
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
				records.setStoreId(storeId);
				records.setStoreName(store.getStoreName());
				records.setTouristName(hyOrder.getContact());
				records.setTouristAccount(store.getBankList().getBankAccount());  //门店账号
				records.setSignUpMethod(1);   //门店
				refundRecordsService.save(records);
				
				if(store.getStoreType()!=2){
//					List<Filter> filters = new ArrayList<>();
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
					storeAccountLog.setType(type+6);
					storeAccountLog.setProfile(remark);
					storeAccountLogService.update(storeAccountLog);

					// 5、修改 总公司-财务中心-门店预存款表
					StorePreSave storePreSave = new StorePreSave();
					storePreSave.setStoreName(store.getStoreName());
					storePreSave.setStoreId(storeId);
					switch(type){
						case 2:storePreSave.setType(11);break; //酒店
						case 3:storePreSave.setType(21);break; //门票
						case 4:storePreSave.setType(16);break; //酒加景
						case 5:storePreSave.setType(9);break; //签证
						case 6:storePreSave.setType(20); //认购门票
					}
					storePreSave.setDate(date);
					storePreSave.setAmount(tuiKuan);
					storePreSave.setOrderCode(hyOrder.getOrderNumber());
					storePreSave.setOrderId(orderId);
					storePreSave.setPreSaveBalance(storeAccountService.findList(null, filters, null).get(0).getBalance());
					storePreSaveService.save(storePreSave);
				}else{
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
					StringBuffer buffer = new StringBuffer();
					switch(type){
						case 2:{
							branchPreSave.setType(12);
							buffer.append("酒店退款");
							break; //酒店
						}
						case 3:{
							branchPreSave.setType(13);
							buffer.append("门票退款");
							break; //门票
						}
						case 4:{
							branchPreSave.setType(14);
							buffer.append("酒加景退款");
							break; //酒加景
						}
						case 5:{
							branchPreSave.setType(15);
							buffer.append("签证退款");
							break; //签证
						}
						case 6:{
							branchPreSave.setType(16);
							buffer.append("认购门票退款");//认购门票
						}
						default:{
							branchPreSave.setType(-1);
							buffer.append("不属于票务5种情况退款");
						}
					}
					branchPreSave.setRemark(buffer.toString());
//				branchPreSave.setType(11); //退团
					branchPreSaveService.save(branchPreSave);
				}
			}

			
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	
	public boolean isSameMonth(Date date1,Date date2){
		try {
               Calendar cal1 = Calendar.getInstance();
               cal1.setTime(date1);

               Calendar cal2 = Calendar.getInstance();
               cal2.setTime(date2);

               boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
                       .get(Calendar.YEAR);
               boolean isSameMonth = isSameYear
                       && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
               return isSameMonth;
        }catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
        return false;
	}

	
	
}
