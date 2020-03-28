package com.hongyu.service.impl;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import com.hongyu.Pageable;
import com.hongyu.entity.*;
import com.hongyu.service.*;
import com.hongyu.util.ActivitiUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.DESUtils;
import com.hongyu.util.DateUtil;
import com.hongyu.util.SendMessageEMY;
import com.sun.org.apache.bcel.internal.generic.IFNULL;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.dao.PaymentSupplierDao;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.HySupplierContract.Settle;

/**
 * @author xyy
 * */
@Service("paymentSupplierServiceImpl")
public class PaymentSupplierServiceImpl extends BaseServiceImpl<PaymentSupplier, Long> implements PaymentSupplierService {
	@Resource(name = "payablesLineServiceImpl")
	private PayablesLineService payablesLineService;
	
	@Resource(name = "commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name = "payServicerServiceImpl")
	private PayServicerService payServicerService;

	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;

	@Resource(name = "payablesRefundItemServiceImpl")
	private PayablesRefundItemService payablesRefundItemService;

	@Resource(name = "payablesLineItemServiceImpl")
	private PayablesLineItemService payablesLineItemService;

	@Resource(name = "hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;

	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;

	@Resource(name = "receiptTotalServicerServiceImpl")
	private ReceiptTotalServicerService receiptTotalServicerService;

    @Resource(name = "payDetailsServiceImpl")
    private PayDetailsService payDetailsService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

	@Resource(name = "paymentSupplierDaoImpl")
	PaymentSupplierDao dao;
	
	@Resource(name = "paymentSupplierDaoImpl")
	public void setBaseDao(PaymentSupplierDao dao) {
		super.setBaseDao(dao);
	}

	/** 提前打款申请-首次提交 */
	@Override
	public Json addPaymentSupplierSubmit(Long id, Long supplierContractId, 
			List<Long> lineOrderIds,List<Long> hotelOrderIds,List<Long> ticketOrderIds,
			List<Long> hotelAndSceneIds,List<Long> visaOrderIds,
			List<Long> ticketSoldIds ,HttpSession session) throws Exception {
		Json json = new Json();

		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);

		HySupplierContract supplierContract = hySupplierContractService.find(supplierContractId);
		PayablesLine payablesLine = payablesLineService.find(id);
		// 启动流程 完成task
		HashMap<String, Object> map = new HashMap<>();
        // 不推迟到下个周期结算
		map.put("submit", "thisTime");
		ProcessInstance pi = runtimeService.startProcessInstanceByKey("payServicePre");
		Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
		Authentication.setAuthenticatedUserId(username);
		taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
		taskService.complete(task.getId(),map);

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
//		paymentSupplier.setOperator();
		paymentSupplier.setSupplierContract(supplierContract);
		// paymentSupplier.setMoneySum(moneySum);
		paymentSupplier.setCreateTime(new Date());
		paymentSupplier.setCreator(hyAdmin);
        // (0 未审核-未付) 1审核中-未付 2 已通过-未付 3已通过-已付 4已驳回-未付
		paymentSupplier.setStatus(1);
		// paymentSupplier.setPayDate(payDate);
		paymentSupplier.setProcessInstanceId(pi.getProcessInstanceId());
		// 审核步骤 1:待采购部经理审核  3:待市场部副总审核 4:待总公司财务审核
		paymentSupplier.setStep(1); 
		// 首次提交没有金额调整				
		paymentSupplier.setModified(0);  
		// 申请置为有效状态
		paymentSupplier.setIsValid(1);  
		// 申请来源为 1:供应商提交
		paymentSupplier.setApplySource(1);
		//结算日期
		paymentSupplier.setSettleDate(payablesLine.getDate()); 
		this.save(paymentSupplier);

		BigDecimal moneySum = BigDecimal.ZERO;
		BigDecimal koudianSum = BigDecimal.ZERO;
        // 产品计调
		HyAdmin operator = new HyAdmin();
		// 建立payables_line_item表、payables_refund_item表和 payment_supplier表的关联
		if (lineOrderIds != null && lineOrderIds.size() > 0) {
			operator = payablesLineItemService.find(lineOrderIds.get(0)).getOperator();
			for (Long i : lineOrderIds) {
				PayablesLineItem p = payablesLineItemService.find(i);
                // 0 未提交 1已提交
				p.setState(1);
				p.setPaymentLineId(paymentSupplier.getId());
				moneySum = moneySum.add(p.getMoney());
				if(p.getKoudian() != null){
				    koudianSum = koudianSum.add(p.getKoudian());
                }
				payablesLineItemService.update(p);
			}
		}
		
		// add by wj
		if (hotelOrderIds != null && hotelOrderIds.size() > 0) {
			operator = payablesLineItemService.find(hotelOrderIds.get(0)).getOperator();
			for (Long i : hotelOrderIds) {
				PayablesLineItem p = payablesLineItemService.find(i);
                // 0 未提交 1已提交
				p.setState(1);
				p.setPaymentLineId(paymentSupplier.getId());
				moneySum = moneySum.add(p.getMoney());
                if(p.getKoudian() != null){
                    koudianSum = koudianSum.add(p.getKoudian());
                }
				payablesLineItemService.update(p);
			}
		}
		if (ticketOrderIds != null && ticketOrderIds.size() > 0) {
			operator = payablesLineItemService.find(ticketOrderIds.get(0)).getOperator();
			for (Long i : ticketOrderIds) {
				PayablesLineItem p = payablesLineItemService.find(i);
				p.setState(1); // 0 未提交 1已提交
				p.setPaymentLineId(paymentSupplier.getId());
				moneySum = moneySum.add(p.getMoney());
                if(p.getKoudian() != null){
                    koudianSum = koudianSum.add(p.getKoudian());
                }
				payablesLineItemService.update(p);
			}
		}
		if (hotelAndSceneIds != null && hotelAndSceneIds.size() > 0) {
			operator = payablesLineItemService.find(hotelAndSceneIds.get(0)).getOperator();
			for (Long i : lineOrderIds) {
				PayablesLineItem p = payablesLineItemService.find(i);
				p.setState(1); // 0 未提交 1已提交
				p.setPaymentLineId(paymentSupplier.getId());
				moneySum = moneySum.add(p.getMoney());
                if(p.getKoudian() != null){
                    koudianSum = koudianSum.add(p.getKoudian());
                }
				payablesLineItemService.update(p);
			}
		}
		if (visaOrderIds != null && visaOrderIds.size() > 0) {
			operator = payablesLineItemService.find(visaOrderIds.get(0)).getOperator();
			for (Long i : lineOrderIds) {
				PayablesLineItem p = payablesLineItemService.find(i);
				p.setState(1); // 0 未提交 1已提交
				p.setPaymentLineId(paymentSupplier.getId());
				moneySum = moneySum.add(p.getMoney());
                if(p.getKoudian() != null){
                    koudianSum = koudianSum.add(p.getKoudian());
                }
				payablesLineItemService.update(p);
			}
		}
		if (ticketSoldIds != null && ticketSoldIds.size() > 0) {
			operator = payablesLineItemService.find(ticketSoldIds.get(0)).getOperator();
			for (Long i : lineOrderIds) {
				PayablesLineItem p = payablesLineItemService.find(i);
				p.setState(1); // 0 未提交 1已提交
				p.setPaymentLineId(paymentSupplier.getId());
				moneySum = moneySum.add(p.getMoney());
                if(p.getKoudian() != null){
                    koudianSum = koudianSum.add(p.getKoudian());
                }
				payablesLineItemService.update(p);
			}
		}
		
		
		// 更新paymentSupplier中的moneySum koudianSum和operator
		paymentSupplier.setOperator(operator);
		paymentSupplier.setMoneySum(moneySum);
		paymentSupplier.setKoudianSum(koudianSum);
		this.update(paymentSupplier);

		// 修改PayablesLine的金额      由于订单或退款的产生会修改payablesLine，需要考虑同步的问题！
		payablesLine.setMoney(payablesLine.getMoney().subtract(moneySum));
		payablesLineService.update(payablesLine);

		json.setMsg("操作成功");
		json.setSuccess(true);
		return json;
	}

	/** 打款单 - 即时提交*/
	@Override
	public Json addPaymentSuppierInstant(PayablesLine payablesLine) throws Exception {
		Json json = new Json();
		// 启动流程 完成task
		HashMap<String, Object> map = new HashMap<>();
		// 传入流程变量区别是即时还是T+N  
		map.put("isInstant", true);
		// 传入产品计调
		map.put("supplierId", payablesLine.getOperator().getUsername()); 
		// 不推迟到下个周期结算
		map.put("submit", "thisTime"); 
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
		paymentSupplier.setPayCode(code); // 打款确认单编号

		HySupplierContract supplierContract = payablesLine.getSupplierContract();
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
		this.save(paymentSupplier);

		BigDecimal moneySum = BigDecimal.ZERO;
		BigDecimal koudianSum = BigDecimal.ZERO;
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
            if(p.getKoudian() != null){
                koudianSum = koudianSum.add(p.getKoudian());
            }
			payablesLineItemService.update(p);
		}

		// 更新paymentSupplier中的moneySum koudianSum和operator
		paymentSupplier.setOperator(operator);
		paymentSupplier.setMoneySum(moneySum);
		paymentSupplier.setKoudianSum(koudianSum);
		// 修改PayablesLine的金额 由于订单或退款的产生会修改payablesLine，需要考虑同步的问题！
		payablesLine.setMoney(payablesLine.getMoney().subtract(moneySum)); 
		
		//add by wj
		//增加使用欠款
		String supplierName = supplierContract.getLiable().getUsername();
		filters.clear();
		filters.add(Filter.eq("supplierName", supplierName));
		List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null,filters,null);
		BigDecimal debt = BigDecimal.ZERO;
		//拿到欠款余额
		ReceiptTotalServicer receiptTotalServicer;
		if(receiptTotalServicers.size()!=0&& !receiptTotalServicers.isEmpty()){
			receiptTotalServicer = receiptTotalServicers.get(0);
			debt = debt.add(receiptTotalServicer.getBalance());
			 //判断欠款与本次付款金额的大小
			if(debt.compareTo(moneySum)>=0){//欠款多于应付款
				debt = debt.subtract(moneySum);
				paymentSupplier.setDebtamount(moneySum);
			}else{
				paymentSupplier.setDebtamount(debt);
				debt = new BigDecimal(0);
			}
			//更新欠款余额表
			receiptTotalServicer.setBalance(debt);
			receiptTotalServicerService.update(receiptTotalServicer);
		}	  
		this.update(paymentSupplier);

		payablesLineService.update(payablesLine);
		json.setMsg("操作成功");
		json.setSuccess(true);
		return json;
	}

	/** 打款申请 - 定时扫描提交*/
	@Override
	public void addPaymentSuppierAuto(Long supplierContractId, String operator) throws Exception {
        HySupplierContract supplierContract = hySupplierContractService.find(supplierContractId);
        List<Filter> f = new ArrayList<>();
        f.add(Filter.eq("supplierContract", supplierContract));
        HyAdmin operatorAdmin = hyAdminService.find(operator);
        f.add(Filter.eq("operator", operatorAdmin));
        f.add(Filter.le("date", DateUtil.getEndOfDay(new Date())));
        // money大于0的PayablesLine需要定时器生成打款审核
        f.add(Filter.gt("money", 0));
        List<PayablesLine> list = payablesLineService.findList(null, f, null);

        if(CollectionUtils.isEmpty(list)){
            return;
        }
        // 启动流程 完成task
        HashMap<String, Object> map = new HashMap<>();
        // 传入流程变量区别是即时还是T+N
     	map.put("isInstant", false);
        // 传入产品计调
        map.put("supplierId", operator);
		// 不推迟到下个周期结算
        map.put("submit", "thisTime");
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
        this.save(paymentSupplier);

        BigDecimal moneySumForPaymentSupplier = BigDecimal.ZERO;
        BigDecimal koudianSumForPaymentSupplier = BigDecimal.ZERO;
        // 建立payables_line_item表和 payment_supplier表的关联
        for (PayablesLine payablesLine : list) {
            filters.clear();
            filters.add(Filter.eq("payablesLineId", payablesLine.getId()));
            // 未提交审核(state为0/2)的item
            filters.add(Filter.ne("state", 1));
            List<PayablesLineItem> l = payablesLineItemService.findList(null, filters, null);
            BigDecimal moneySum = BigDecimal.ZERO;
            BigDecimal koudianSum = BigDecimal.ZERO;
            for (PayablesLineItem p : l) {
                // 0 未提交 1已提交
                p.setState(1);
                p.setPaymentLineId(paymentSupplier.getId());
                payablesLineItemService.update(p);
                moneySum = moneySum.add(p.getMoney());
                if(p.getKoudian() != null){
                    koudianSum = koudianSum.add(p.getKoudian());
                }
            }
            // 修改PayablesLine的金额 因为全部选中 应该为0
            payablesLine.setMoney(payablesLine.getMoney().subtract(moneySum));
            moneySumForPaymentSupplier = moneySumForPaymentSupplier.add(moneySum);
            koudianSumForPaymentSupplier = koudianSumForPaymentSupplier.add(koudianSum);
        }
        // 更新paymentSupplier中的MoneySum和operator
        HyAdmin admin = hyAdminService.find(operator);
        paymentSupplier.setOperator(admin);
        paymentSupplier.setMoneySum(moneySumForPaymentSupplier);
        paymentSupplier.setKoudianSum(koudianSumForPaymentSupplier);

        // 欠款的使用需要以合同为单位  这里以登录名称作为合同的唯一判定
        //add by wj
        //增加使用欠款
        String supplierName = supplierContract.getLiable().getUsername();
        filters.clear();
        filters.add(Filter.eq("supplierName", supplierName));
        List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null,filters,null);
        BigDecimal debt = new BigDecimal(0);//拿到欠款余额
        ReceiptTotalServicer receiptTotalServicer;
        if(receiptTotalServicers.size()!=0&& !receiptTotalServicers.isEmpty()){
            receiptTotalServicer = receiptTotalServicers.get(0);
            debt = debt.add(receiptTotalServicer.getBalance());
            //判断欠款与本次付款金额的大小
            if(debt.compareTo(moneySumForPaymentSupplier)>=0){//欠款多于应付款
                debt = debt.subtract(moneySumForPaymentSupplier);
                paymentSupplier.setDebtamount(moneySumForPaymentSupplier);
            }else{
                paymentSupplier.setDebtamount(debt);
                debt = new BigDecimal(0);
            }
            //更新欠款余额表
            receiptTotalServicer.setBalance(debt);
            receiptTotalServicerService.update(receiptTotalServicer);
        }

        this.update(paymentSupplier);
	}

	/** 打款申请 - 审核 */
	@Override
	public Json addPaymentSupplierAudit(Long id, String comment, Integer state,String dismissRemark,BigDecimal modifyAmount, HttpSession session) throws Exception {
		Json json = new Json();

		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		HyAdmin hyAdmin = hyAdminService.find(username);
		PaymentSupplier paymentSupplier = this.find(id);
		String processInstanceId = paymentSupplier.getProcessInstanceId();

		if (processInstanceId == null || processInstanceId == "") {
			json.setSuccess(false);
			json.setMsg("审核出错，信息不完整，请重新申请");
		} else {
			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();

			HashMap<String, Object> map = new HashMap<>();

			Integer applySource = paymentSupplier.getApplySource();
			
			if(applySource == 1){  // 申请来源为供应商手动提交-提前付款
				
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
					
					HySupplierContract contract = paymentSupplier.getSupplierContract();
					if(contract!=null){
						String phone = contract.getLiable().getMobile();
						SendMessageEMY.sendMessage(phone, "", 18);
					}
					
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
					
					HySupplierContract contract = paymentSupplier.getSupplierContract();
					if(contract!=null){
						String phone = contract.getLiable().getMobile();
						SendMessageEMY.sendMessage(phone, "", 18);
					}
				}
			}
			Authentication.setAuthenticatedUserId(username);
			taskService.claim(task.getId(), username);
			taskService.addComment(task.getId(), processInstanceId, (comment == null ? " " : comment) + ":" + state);
			taskService.complete(task.getId(), map);
			this.update(paymentSupplier);

			json.setSuccess(true);
			json.setMsg("审核完成");
		}

		return json;
	}

	/** 提交人 对被驳回的申请进行修改*/
	@Override
	public Json updateApply(Long id,Integer state, String dismissRemark,BigDecimal modifyAmount, HttpSession session) throws Exception {
		Json json = new Json();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		HyAdmin hyAdmin = hyAdminService.find(username);
		PaymentSupplier paymentSupplier = this.find(id);
		Task task = taskService.createTaskQuery().processInstanceId(paymentSupplier.getProcessInstanceId()).singleResult();
		HashMap<String, Object> map = new HashMap<>();
		
		if(state == 2){ //不修改直接提交   ${submit=='thisTime'}
			map.put("submit", "thisTime");
			paymentSupplier.setStep(1); // 步骤置为 1:待采购部经理审核
			paymentSupplier.setStatus(1);  // 状态置为 1:审核中-未付
		}else if(state == 3){ // 修改后提交
			map.put("submit", "thisTime");
			paymentSupplier.setStep(1); // 步骤置为 1:待采购部经理审核
			paymentSupplier.setStatus(1);  // 状态置为 1:审核中-未付
			
			paymentSupplier.setModified(1); // 进行金额调整
			paymentSupplier.setDismissRemark(dismissRemark);
			paymentSupplier.setModifyAmount(modifyAmount);
			paymentSupplier.setMoneySum(paymentSupplier.getMoneySum().add(modifyAmount)); // 修改付款金额
		}else if(state == 4){ // 推到下一周期结算 // ${submit=='nextTime'}
			map.put("submit", "nextTime"); 
			// 将此次申请置为无效 0 无效  1有效
			paymentSupplier.setIsValid(0);
			// 释放此次申请所关联的订单条目和退款条目
			
			BigDecimal moneySum = BigDecimal.ZERO;
			Long payablesLineId;
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("paymentLineId", paymentSupplier.getId()));
			filters.add(Filter.eq("state", 1));
			List<PayablesLineItem> list = payablesLineItemService.findList(null, filters, null);
			payablesLineId = list.get(0).getPayablesLineId();
			List<PayablesRefundItem> list2 = payablesRefundItemService.findList(null, filters, null);
			
			Date newSettleDate = getDelaySettleDate(paymentSupplier.getSettleDate(), paymentSupplier.getSupplierContract());
			
			for(PayablesLineItem p : list){
                // 置为未提交
				p.setState(0);
				p.setSettleDate(newSettleDate);
				moneySum = moneySum.add(p.getMoney());
				payablesLineItemService.update(p);
			}
			
			for(PayablesRefundItem p : list2){
				p.setState(0);
				moneySum = moneySum.subtract(p.getRefundMoney());
				payablesRefundItemService.update(p);
			}
			
			// 修改PayablesLine的金额     
			PayablesLine payablesLine = payablesLineService.find(payablesLineId);
			payablesLine.setMoney(payablesLine.getMoney().add(moneySum));
			payablesLineService.update(payablesLine);
			
		}
		
		Authentication.setAuthenticatedUserId(username);
		taskService.addComment(task.getId(), paymentSupplier.getProcessInstanceId(), " :1");
        taskService.claim(task.getId(), username);
		taskService.complete(task.getId(),map);
		this.update(paymentSupplier);
		
		json.setSuccess(true);
		json.setMsg("操作成功");
		
		return json;
	}

	/** 财务 对被驳回的申请进行修改*/
	@Override
	public Json updateApply2(Long id,Integer state, String dismissRemark,BigDecimal modifyAmount, HttpSession session) throws Exception {
		Json json = new Json();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		HyAdmin hyAdmin = hyAdminService.find(username);
		PaymentSupplier paymentSupplier = this.find(id);
		Task task = taskService.createTaskQuery().processInstanceId(paymentSupplier.getProcessInstanceId()).singleResult();
		HashMap<String, Object> map = new HashMap<>();
		
		if(state == 2){ //不修改直接提交   ${submit=='thisTime'}
			map.put("submit", "thisTime");
			paymentSupplier.setStep(11); // 步骤置为 11:待供应商确认
			paymentSupplier.setStatus(1);  // 状态置为 1:审核中-未付
		}else if(state == 3){ // 修改后提交
			map.put("submit", "thisTime");
			paymentSupplier.setStep(11); // 步骤置为 11:待供应商确认
			paymentSupplier.setStatus(1);  // 状态置为 1:审核中-未付
			
			paymentSupplier.setModified(1); // 进行金额调整
			paymentSupplier.setDismissRemark(dismissRemark);
			paymentSupplier.setModifyAmount(modifyAmount);
			paymentSupplier.setMoneySum(paymentSupplier.getMoneySum().add(modifyAmount)); // 修改付款金额
		}else if(state == 4){ // 推到下一周期结算 // ${submit=='nextTime'}
			map.put("submit", "nextTime"); 
			// 将此次申请置为无效
			paymentSupplier.setIsValid(0); // 0 无效  1有效
			// 释放此次申请所关联的订单条目和退款条目
			
			BigDecimal moneySum = BigDecimal.ZERO;
			Long payablesLineId;
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("paymentLineId", paymentSupplier.getId()));
			filters.add(Filter.eq("state", 1));
			List<PayablesLineItem> list = payablesLineItemService.findList(null, filters, null);
			payablesLineId = list.get(0).getPayablesLineId();
			List<PayablesRefundItem> list2 = payablesRefundItemService.findList(null, filters, null);
			
			Date newSettleDate = getDelaySettleDate(paymentSupplier.getSettleDate(), paymentSupplier.getSupplierContract());
			
			for(PayablesLineItem p : list){
				// 置为未提交
				p.setState(0); 
				p.setSettleDate(newSettleDate);
				moneySum = moneySum.add(p.getMoney());
				payablesLineItemService.update(p);
			}
			
			for(PayablesRefundItem p : list2){
				p.setState(0);
				moneySum = moneySum.subtract(p.getRefundMoney());
				payablesRefundItemService.update(p);
			}
			
			// 修改PayablesLine的金额     
			PayablesLine payablesLine = payablesLineService.find(payablesLineId);
			payablesLine.setMoney(payablesLine.getMoney().add(moneySum));
			payablesLine.setDate(newSettleDate);
			payablesLineService.update(payablesLine);
			
		}
		
		Authentication.setAuthenticatedUserId(username);
		taskService.claim(task.getId(), username);
		taskService.addComment(task.getId(), paymentSupplier.getProcessInstanceId(), " :1");
		taskService.complete(task.getId(),map);
		this.update(paymentSupplier);
		
		json.setSuccess(true);
		json.setMsg("操作成功");
		
		return json;
	}

    /** 获取推迟之后的结算日期*/
	private Date getDelaySettleDate(Date settleDate, HySupplierContract contract) throws Exception {
        // 获取结算方式
		Settle settle = contract.getSettle();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(settleDate);
        // 月结 推迟一个月
		if (Settle.yuejie.equals(settle)) {
			calendar.add(Calendar.MONTH, 1);
		} else if (Settle.banyuejie.equals(settle)) {
            // 半月结  推迟半个月
			calendar.add(Calendar.DATE, 15);
		} else if (Settle.zhoujie.equals(settle)) {
            // 周结 推迟七天
			calendar.add(Calendar.DATE, 7);
		}
		//对于月结 半月结或周结 才能推迟到一个确定的日期 对于实时或T+N 不修改结算日期 由定时器对各payablesLine按合同进行合并
//		else if (Settle.shishijie.equals(settle)) { // 实时结算  推迟到第二天
//			calendar.add(Calendar.DATE, 1);
//		} else if (Settle.tjiaN.equals(settle)) { // T+N结算  推迟N天
//			calendar.add(Calendar.DATE, contract.getDateN());
//		}
		return calendar.getTime();
	}

	@Autowired
	HyOrderService hyOrderService;

    /** 向供应商付款审核 - 列表*/
    @Override
    public Json payServierPreReviewList(Pageable pageable, Integer state, String supplierName, String orderNumber, String payCode, String sn, HttpSession session) throws Exception {
        Json j = new Json();
    	int page = pageable.getPage();
        int rows = pageable.getRows();
        HashMap<String, Object> answer = new HashMap<>();
        String username = (String) session.getAttribute(CommonAttributes.Principal);
        List<Map<String, Object>> ans = new LinkedList<>();
        HashSet<Long> paymentSupplierSet = new HashSet<>();

        if(StringUtils.isNotBlank(orderNumber)){
            // 根据orderNumber获取hy_payment_supplier的id的集合
            StringBuilder sqlOrderNumber = new StringBuilder("SELECT hy_order.order_number, pli.payment_line_id FROM hy_order LEFT JOIN hy_payables_line_item AS pli ON hy_order.id = pli.order_id WHERE hy_order.order_number LIKE '%");
            sqlOrderNumber.append(orderNumber);
            sqlOrderNumber.append("%'");
            List<Object[]> list = super.statis(sqlOrderNumber.toString());
            if(CollectionUtils.isEmpty(list)){
                j.setSuccess(true);
                j.setMsg("未获取到符合条件的数据");
                return j;
            }
            for (Object[] obj: list) {
                // TODO 根据后面的调用 是否有必要Object转BigIntegr转Long
                if(obj[1] != null){
                    paymentSupplierSet.add(((BigInteger)obj[1]).longValue());
                }
            }
            if(CollectionUtils.isEmpty(paymentSupplierSet)){
                j.setSuccess(true);
                j.setMsg("未获取到符合条件的数据");
                return j;
            }
        }
        StringBuilder sql = new StringBuilder("SELECT SQL_CALC_FOUND_ROWS ps.id, ps.pay_code, ps.supplier_name, ps.operator, (SELECT name  FROM hy_admin WHERE hy_admin.username = ps.operator) AS operator_name, ps.money_sum, (SELECT name FROM hy_admin WHERE hy_admin.username = ps.creator) AS creator, ps.create_time, ps.pay_date, ps.status,hy_order.order_number,pli.sn,pli.product_name,pli.t_date,pli.order_money,pli.refunds,pli.koudian,(SELECT store_name FROM hy_store WHERE hy_store.id = hy_order.store_id) AS store_name,pli.money,(SELECT name FROM hy_admin WHERE hy_admin.username = (SELECT operator_id FROM hy_store WHERE hy_store.id = hy_order.store_id)) AS op, ps.process_instance_id, hy_order.people, hy_order.contact, (SELECT hy_bank_list.bank_account FROM hy_bank_list WHERE hy_bank_list.id = (SELECT hy_supplier_contract.bank_list FROM hy_supplier_contract WHERE hy_supplier_contract.id = ps.supplier_contract)) AS bank_account, hy_order.jiusuan_money, (SELECT hy_bank_list.bank_name FROM hy_bank_list WHERE hy_bank_list.id = (SELECT hy_supplier_contract.bank_list FROM hy_supplier_contract WHERE hy_supplier_contract.id = ps.supplier_contract)) AS bank_name, ps.step step, hy_order.id orderid, hy_order.discounted_price, hy_order.store_fan_li, hy_order.adjust_money FROM (hy_payment_supplier AS ps RIGHT JOIN hy_payables_line_item  AS pli ON ps.id = pli.payment_line_id) LEFT JOIN hy_order ON pli.order_id = hy_order.id WHERE pli.state = 1 AND ps.is_valid = 1");

        // 筛选条件: 供应商名称
        if(StringUtils.isNotBlank(supplierName)){
            sql.append(" AND ps.supplier_name LIKE '%");
            sql.append(supplierName);
            sql.append("%'");
        }
        // 筛选条件: 订单编号
        if(CollectionUtils.isNotEmpty(paymentSupplierSet)){
            sql.append(" AND ps.id IN (");
            for (Long id : paymentSupplierSet) {
                sql.append(id);
                sql.append(",");
            }
            // 删除多余的逗号
            sql.deleteCharAt(sql.length() - 1);
            sql.append(")");
        }
        //筛选条件：打款单编号
		if(StringUtils.isNotBlank(payCode)){
        	sql.append(" AND ps.pay_code like '%");
        	sql.append(payCode);
        	sql.append("%'");
		}

		//筛选条件：产品编号
		if(StringUtils.isNotBlank(sn)){
			sql.append(" AND pli.sn like '%");
			sql.append(sn);
			sql.append("%'");
		}


        // 筛选条件: 审批状态
        sql.append(" AND ps.process_instance_id IN (");
        HashSet<String> taskProcessInstanceIdSet = new HashSet<>();
        if (state == null) {
            // TODO 只通过一次操作获取待办任务和已完成任务
        	List<Task> tasks = ActivitiUtils.getTaskList(username, ActivitiUtils.PAY_SERVICE_PRE);
            List<HistoricTaskInstance> hisTasks = ActivitiUtils.getHistoryTaskList(username, ActivitiUtils.PAY_SERVICE_PRE);
            // 没有任何待办或已办任务
            if(CollectionUtils.isEmpty(tasks) && CollectionUtils.isEmpty(hisTasks)){
            	j.setSuccess(true);
            	j.setMsg("未获取到符合条件的数据");
            	return j;
            }
            for (Task task : tasks) {
                sql.append(task.getProcessInstanceId());
                sql.append(",");
                taskProcessInstanceIdSet.add(task.getProcessInstanceId());
            }
            for (HistoricTaskInstance hisTask : hisTasks) {
                sql.append(hisTask.getProcessInstanceId());
                sql.append(",");
            }
        } else if (state == 0) {
            /* 搜索未完成任务*/
			List<Task> tasks = ActivitiUtils.getTaskList(username, ActivitiUtils.PAY_SERVICE_PRE);
            // 没有任何待办任务
            if(CollectionUtils.isEmpty(tasks)){
            	j.setSuccess(true);
            	j.setMsg("未获取到符合条件的数据");
            	return j;
            }
            for (Task task : tasks) {
                sql.append(task.getProcessInstanceId());
                sql.append(",");
            }
        } else if (state == 1) {
            /*搜索已完成任务*/
			List<HistoricTaskInstance> hisTasks = ActivitiUtils.getHistoryTaskList(username, ActivitiUtils.PAY_SERVICE_PRE);
            // 没有任何已办任务
            if(CollectionUtils.isEmpty(hisTasks)){
            	j.setSuccess(true);
            	j.setMsg("未获取到符合条件的数据");
            	return j;
            }
            for (HistoricTaskInstance hisTask : hisTasks) {
                sql.append(hisTask.getProcessInstanceId());
                sql.append(",");
            }
        }
        // 删除多余的逗号
        sql.deleteCharAt(sql.length() - 1);
        // 按hy_payment_supplier的id降序
        sql.append(") ORDER BY ps.id DESC LIMIT ");
        // 分页
        sql.append((page - 1) * rows);
        sql.append(",");
        sql.append(rows);
        List<Object[]> list = super.statis(sql.toString());
        // SQL_CALC_FOUND_ROWS和FOUND_ROWS()的配合使用 TODO 并发情况下对FOUND_ROWS()的结果的影响
        BigInteger total = (BigInteger) super.getSingleResultByNativeQuery("SELECT FOUND_ROWS()");
        answer.put("total", total);

        /*
         id                     obj[0]
         pay_code               obj[1]
         supplier_name          obj[2]
         operator               obj[3]
         operator_name          obj[4]
         money_sum              obj[5]
         creator                obj[6]
         create_time            obj[7]
         pay_date               obj[8]
         status                 obj[9]
         process_instance_id    obj[20]
         bank_account           obj[23]
         bank_name              obj[25]
         step					obj[26]
         orderid				obj[27]
         -------  1 ——> n -------
         order_number           obj[10]
         sn                     obj[11]
         product_name           obj[12]
         t_date                 obj[13]
         order_money            obj[14]
         refunds                obj[15]
         koudian                obj[16]
         store_name             obj[17]
         money                  obj[18]
         op                     obj[19]
         people                 obj[21]
         contact                obj[22]
         jiusuan_money          obj[24]
         discounted_price       obj[28]
         store_fan_li           obj[29]
         adjust_money           obj[30]
        */
        for (Object[] obj : list) {
            HashMap<String, Object> m = new HashMap<>();
            m.put("id", obj[0]);
			if (null == state) {
				String processInstanceId = (String) obj[20];
				// 当筛选“全部”的情况，只要当前待办任务的流程实例id集合中，包含了该任务的流程实例id，则说明该任务处于待办状态
				// state 0:待审核  1:已审核
                m.put("state", taskProcessInstanceIdSet.contains(processInstanceId) ? 0 : 1);
            } else {
				m.put("state", state);
			}
            m.put("payCode", obj[1]);
            m.put("supplierName", obj[2]);
            m.put("operator", obj[3] == null ? "" : obj[3]);
            m.put("operatorName", obj[4]);
            m.put("moneySum", obj[5]);
            m.put("creator", obj[6] == null ? "系统提交" : obj[6]);
            m.put("createTime", obj[7]);
            m.put("payTime", obj[8]);
            m.put("status", obj[9]);
            m.put("orderNumber", obj[10]);

            //顯示扣點方式

			HyOrder hyOrder = hyOrderService.find(((BigInteger)obj[27]).longValue());
			if(hyOrder!=null){
				if(hyOrder.getKoudianMethod()==null){
					m.put("koudianMethod","");
				}else if(hyOrder.getKoudianMethod().equals(Constants.DeductLine.tuanke.ordinal())){
					m.put("koudianMethod", "团客 "+ (hyOrder.getProportion()==null?0:hyOrder.getProportion())+"%");
				}else{
					m.put("koudianMethod","人头 " +(hyOrder.getHeadProportion()==null?0:hyOrder.getHeadProportion())+"元/人");
				}
			}else{
				m.put("koudianMethod","");
			}

			//income = jiusuan_money
			BigDecimal income = (BigDecimal) obj[24];
//			if(null != obj[28]) {
//				income = income.subtract((BigDecimal) obj[28]);
//			}
//			if(null != obj[29]) {
//				BigDecimal tmp = (BigDecimal) obj[29];
//				income = income.subtract(tmp);
//			}
//			if(null != obj[30]) {
//				income = income.subtract((BigDecimal) obj[30]);
//			}
            m.put("income", income);

            m.put("sn", obj[11]);
            m.put("productName", obj[12]);
            m.put("tDate", obj[13]);
            m.put("orderMoney", obj[14]);
            m.put("refunds", obj[15]);
            m.put("koudian", obj[16]);
            m.put("storeName", obj[17]);
            m.put("money", obj[18]);
            m.put("op", obj[19]);
            m.put("people", obj[21]);
            m.put("contact", obj[22]);
            m.put("bankAccount", obj[23]);
            m.put("bankName", obj[25]);
            m.put("step",obj[26]);
            ans.add(m);
        }
        answer.put("pageNumber", page);
        answer.put("pageSize", rows);
        answer.put("rows", ans);

		if (total.compareTo(BigInteger.ZERO) == 0) {
			j.setMsg("未获取到符合条件的数据");
		} else {
            j.setMsg("获取成功");
        }
        j.setObj(answer);
        j.setSuccess(true);
        return j;
    }

	@Override
	public Json cancelAudit(Long id, HttpSession session)throws Exception {
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);
		PaymentSupplier paymentSupplier = this.find(id);
		if(paymentSupplier==null){
			throw new Exception("无效的打款单");
		}

		if(paymentSupplier.getApplySource().equals(0)) {    //0 系统自动产生 1采购部员工手动提交
			// 启动流程 完成task
			HashMap<String, Object> map = new HashMap<>();
			// 传入流程变量区别是即时还是T+N
			map.put("isInstant", true);
			// 传入产品计调
			map.put("supplierId", paymentSupplier.getOperator().getUsername());
			// 不推迟到下个周期结算
			map.put("submit", "thisTime");
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("payServicePreTN");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			Authentication.setAuthenticatedUserId("");
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId(), map);

			// (0 未审核-未付) 1审核中-未付 2 已通过-未付 3已通过-已付 4已驳回-未付
			paymentSupplier.setStatus(1);
			// paymentSupplier.setPayDate(payDate);
			paymentSupplier.setProcessInstanceId(pi.getProcessInstanceId());
			// 审核步骤(提前付款使用) 0:被驳回待申请人处理 1:待采购部经理审核 3:待市场部副总限额审核 4:待总公司财务审核
			// 审核步骤(T+N付款  即时付款使用) 10:被驳回待财务处理 11:待供应商确认 12:待市场部副总限额审核 13:待总公司财务审核
			paymentSupplier.setStep(11);
		}else{

			// 启动流程 完成task
			HashMap<String, Object> map = new HashMap<>();
			// 不推迟到下个周期结算
			map.put("submit", "thisTime");
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("payServicePre");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId(),map);

			// (0 未审核-未付) 1审核中-未付 2 已通过-未付 3已通过-已付 4已驳回-未付
			paymentSupplier.setStatus(1);
			// paymentSupplier.setPayDate(payDate);
			paymentSupplier.setProcessInstanceId(pi.getProcessInstanceId());
			// 审核步骤(提前付款使用) 0:被驳回待申请人处理 1:待采购部经理审核 3:待市场部副总限额审核 4:待总公司财务审核
			// 审核步骤(T+N付款  即时付款使用) 10:被驳回待财务处理 11:待供应商确认 12:待市场部副总限额审核 13:待总公司财务审核
			paymentSupplier.setStep(1);
		}

		json.setSuccess(true);
		json.setMsg("取消成功");
		json.setObj(paymentSupplier);
		return json;
	}

	/** 向供应商付款审核 - 详情*/
    @Override
    public HashMap<String, Object> getHistoryComments(Long id) throws Exception {
        HashMap<String, Object> obj = new HashMap<>();
        PaymentSupplier paymentSupplier = this.find(id);

        // 审核步骤
        String processInstanceId = paymentSupplier.getProcessInstanceId();
        List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
        Collections.reverse(commentList);
        List<Map<String, Object>> auditlist = new LinkedList<>();
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
            if (index < 0) {
                map.put("comment", " ");
                map.put("result", 1);
            } else {
                map.put("comment", str.substring(0, index));
                map.put("result", Integer.parseInt(str.substring(index + 1)));
            }
            map.put("time", comment.getTime());

            auditlist.add(map);
        }

        obj.put("auditlist", auditlist);



        // 供应商信息
        obj.put("payCode", paymentSupplier.getPayCode());
        HySupplier hySupplier = paymentSupplier.getSupplierContract().getHySupplier();
        obj.put("supplierName", hySupplier.getSupplierName());
        obj.put("bankList", paymentSupplier.getSupplierContract().getBankList());
        obj.put("contractCode", paymentSupplier.getSupplierContract().getContractCode());
        obj.put("operator", hySupplier.getOperator().getName());



        // 订单列表信息
        BigDecimal lineOrderSum = BigDecimal.ZERO;
        BigDecimal hotelOrderSum = BigDecimal.ZERO;
        BigDecimal ticketOrderSum = BigDecimal.ZERO;
        BigDecimal hotelAndSceneOrderSum = BigDecimal.ZERO;
        BigDecimal visaOrderSum = BigDecimal.ZERO;
        BigDecimal ticketSoldOrderSum = BigDecimal.ZERO;

        BigDecimal lineRefundSum = BigDecimal.ZERO;
        BigDecimal ticketRefundSum = BigDecimal.ZERO;


        List<Filter> filters = new LinkedList<>();
        List<PayablesLineItem> list = null;
        // 获取线路订单信息列表
        filters.add(Filter.eq("paymentLineId", id));
        filters.add(Filter.eq("productType", 1)); // 1线路 2酒店 3门票 4酒加景 5签证
        // 6认购门票
        filters.add(Filter.eq("state", 1)); // 0 未提交 1 已提交
        list = payablesLineItemService.findList(null, filters, null);

        List<HashMap<String, Object>> lineOrder = new LinkedList<>();
        for (PayablesLineItem p : list) {
            HashMap<String, Object> orderItem = new HashMap<>();
            orderItem.put("id", p.getId());
            orderItem.put("orderNumber", p.getHyOrder().getOrderNumber());
            orderItem.put("sn", p.getHyGroup().getLine().getPn()); // 产品编号
            orderItem.put("productName", p.getProductName());
            orderItem.put("tDate", p.getHyGroup().getStartDay());
            orderItem.put("contact", p.getHyOrder().getContact());
            orderItem.put("lineType", p.getHyGroup().getTeamType());
            orderItem.put("orderMoney", p.getOrderMoney());
            orderItem.put("refundMoney", p.getRefunds());
            orderItem.put("deductionPoint", p.getKoudian());
            orderItem.put("money", p.getMoney()); // 应付款金额
            
            
            //add by wj 2019-07-07
            HyOrder hyOrder = p.getHyOrder();
            BigDecimal money=hyOrder.getJiusuanMoney();
			if(hyOrder.getTip()!=null) {
				money=money.add(hyOrder.getTip());
			}
			if(hyOrder.getDiscountedPrice()!=null) {
				money=money.subtract(hyOrder.getDiscountedPrice());
			}
			if(hyOrder.getStoreFanLi()!=null) {
				money=money.subtract(hyOrder.getStoreFanLi());
			}
            orderItem.put("ReceiptMoney",money);//计算保险金额
            orderItem.put("ReceiptDate", hyOrder.getCreatetime());
            // end of add
            
            lineOrder.add(orderItem);

            lineOrderSum = lineOrderSum.add(p.getMoney()); // 线路小计
        }

//			// 获取票务订单信息
//			filters.clear();
//			filters.add(Filter.eq("paymentLineId", id));
//			filters.add(Filter.gt("productType", 1)); // 2酒店 3门票 4酒加景 5签证 6认购门票
//			filters.add(Filter.eq("state", 1)); // 0 未提交 1 已提交
//			list = payablesLineItemService.findList(null, filters, null);
//
//			List<HashMap<String, Object>> ticketOrder = new LinkedList<>();
//			for (PayablesLineItem p : list) {
//				HashMap<String, Object> orderItem = new HashMap<>();
//                orderItem.put("id", p.getId());
//                orderItem.put("orderNumber", p.getHyOrder().getOrderNumber());
//                orderItem.put("sn", p.getSn()); // 产品编号
//                orderItem.put("productName", p.getProductName());
//                orderItem.put("tDate", p.gettDate());
//                orderItem.put("contact", p.getHyOrder().getContact());
//                orderItem.put("orderMoney", p.getOrderMoney());
//                orderItem.put("refundMoney", p.getRefunds());
//                orderItem.put("deductionPoint", p.getKoudian());
//                orderItem.put("money", p.getMoney()); // 应付款金额
//				ticketOrder.add(orderItem);
//
//				ticketOrderSum = ticketOrderSum.add(p.getMoney());  // 票务小计
//			}

//			List<PayablesRefundItem> list2 = null;
        // 获取线路退款信息
//			filters.clear();
//			filters.add(Filter.eq("paymentLineId", id));
//			filters.add(Filter.eq("productType", 1)); // 1线路 2酒店 3门票 4酒加景 5签证
//														// 6认购门票
//			filters.add(Filter.eq("state", 1)); // 0 未提交 1 已提交
//			list2 = payablesRefundItemService.findList(null, filters, null);
//
//			List<HashMap<String, Object>> lineRefund = new LinkedList<>();
//			for (PayablesRefundItem p : list2) {
//				HashMap<String, Object> refundItem = new HashMap<>();
//				refundItem.put("id", p.getId());
//				refundItem.put("orderNumber", p.getHyOrder().getOrderNumber());
//				refundItem.put("sn", p.getHyGroup().getLine().getPn()); // 产品编号
//				refundItem.put("productName", p.getHyGroup().getLine().getName());
//				refundItem.put("contact", p.getHyOrder().getContact());
//				refundItem.put("refundDate", p.getRefundDate());
//				refundItem.put("refundMoney", p.getRefundMoney());
//				refundItem.put("remark", p.getRemark());
//				lineRefund.add(refundItem);
//
//				lineRefundSum = lineRefundSum.add(p.getRefundMoney()); // 线路退款小计
//			}
//
//			// 获取票务退款信息
//			filters.clear();
//			filters.add(Filter.eq("paymentLineId", id));
//			filters.add(Filter.gt("productType", 1)); // 1线路 2酒店 3门票 4酒加景 5签证
//														// 6认购门票
//			filters.add(Filter.eq("state", 1)); // 0 未提交 1 已提交
//			list2 = payablesRefundItemService.findList(null, filters, null);
//
//			List<HashMap<String, Object>> ticketRefund = new LinkedList<>();
//			for (PayablesRefundItem p : list2) {
//				HashMap<String, Object> refundItem = new HashMap<>();
//				refundItem.put("id", p.getId());
//				refundItem.put("orderNumber", p.getHyOrder().getOrderNumber());
//				refundItem.put("sn", p.getSn());
//				refundItem.put("productName", p.getProductName());
//				refundItem.put("contact", p.getHyOrder().getContact());
//				refundItem.put("refundDate", p.getRefundDate());
//				refundItem.put("refundMoney", p.getRefundMoney());
//				refundItem.put("remark", p.getRemark());
//				ticketRefund.add(refundItem);
//
//				ticketRefundSum = ticketRefundSum.add(p.getRefundMoney());  // 票务退款小计
//			}
        //add by wj
        // 1线路  2酒店  3门票  4酒加景 5签证  6认购门票
        //获取酒店信息
        filters.clear();
        filters.add(Filter.eq("paymentLineId", id));
        filters.add(Filter.eq("productType", 2)); // 2酒店 3门票 4酒加景 5签证 6认购门票
        filters.add(Filter.eq("state", 1)); // 0 未提交 1 已提交
        list = payablesLineItemService.findList(null, filters, null);

        List<HashMap<String, Object>> hotelOrder = new LinkedList<>();
        for (PayablesLineItem p : list) {
            HashMap<String, Object> orderItem = new HashMap<>();
            orderItem.put("id", p.getId());
            orderItem.put("orderNumber", p.getHyOrder().getOrderNumber());
            orderItem.put("sn", p.getSn()); // 产品编号
            orderItem.put("hotelName", p.getProductName());
            orderItem.put("orderDate", p.gettDate());
            orderItem.put("contact", p.getHyOrder().getContact());
            orderItem.put("orderMoney", p.getOrderMoney());
            orderItem.put("refundMoney", p.getRefunds());
            orderItem.put("deductionPoint", p.getKoudian());
            orderItem.put("money", p.getMoney()); // 应付款金额
            hotelOrder.add(orderItem);

            hotelOrderSum = hotelOrderSum.add(p.getMoney());  // 票务小计
        }
        //门票订单信息
        filters.clear();
        filters.add(Filter.eq("paymentLineId", id));
        filters.add(Filter.eq("productType", 3)); // 2酒店 3门票 4酒加景 5签证 6认购门票
        filters.add(Filter.eq("state", 1)); // 0 未提交 1 已提交
        list = payablesLineItemService.findList(null, filters, null);

        List<HashMap<String, Object>> ticketOrder = new LinkedList<>();
        for (PayablesLineItem p : list) {
            HashMap<String, Object> orderItem = new HashMap<>();
            orderItem.put("id", p.getId());
            orderItem.put("orderNumber", p.getHyOrder().getOrderNumber());
            orderItem.put("sn", p.getSn()); // 产品编号
            orderItem.put("ticketlName", p.getProductName());
            orderItem.put("orderDate", p.gettDate());
            orderItem.put("contact", p.getHyOrder().getContact());
            orderItem.put("orderMoney", p.getOrderMoney());
            orderItem.put("refundMoney", p.getRefunds());
            orderItem.put("deductionPoint", p.getKoudian());
            orderItem.put("money", p.getMoney()); // 应付款金额
            ticketOrder.add(orderItem);

            ticketOrderSum = ticketOrderSum.add(p.getMoney());  // 票务小计
        }
        //获取酒加景订单列表信息
        filters.clear();
        filters.add(Filter.eq("paymentLineId", id));
        filters.add(Filter.eq("productType", 4)); // 2酒店 3门票 4酒加景 5签证 6认购门票
        filters.add(Filter.eq("state", 1)); // 0 未提交 1 已提交
        list = payablesLineItemService.findList(null, filters, null);

        List<HashMap<String, Object>> hotelAndSceneOrder = new LinkedList<>();
        for (PayablesLineItem p : list) {
            HashMap<String, Object> orderItem = new HashMap<>();
            orderItem.put("id", p.getId());
            orderItem.put("orderNumber", p.getHyOrder().getOrderNumber());
            orderItem.put("sn", p.getSn()); // 产品编号
            orderItem.put("productName", p.getProductName());
            orderItem.put("orderDate", p.gettDate());
            orderItem.put("contact", p.getHyOrder().getContact());
            orderItem.put("orderMoney", p.getOrderMoney());
            orderItem.put("refundMoney", p.getRefunds());
            orderItem.put("deductionPoint", p.getKoudian());
            orderItem.put("money", p.getMoney()); // 应付款金额
            hotelAndSceneOrder.add(orderItem);

            hotelAndSceneOrderSum = hotelAndSceneOrderSum.add(p.getMoney());  // 票务小计
        }
        //获取签证订单列表信息
        filters.clear();
        filters.add(Filter.eq("paymentLineId", id));
        filters.add(Filter.eq("productType", 5)); // 2酒店 3门票 4酒加景 5签证 6认购门票
        filters.add(Filter.eq("state", 1)); // 0 未提交 1 已提交
        list = payablesLineItemService.findList(null, filters, null);

        List<HashMap<String, Object>> visaOrder = new LinkedList<>();
        for (PayablesLineItem p : list) {
            HashMap<String, Object> orderItem = new HashMap<>();
            orderItem.put("id", p.getId());
            orderItem.put("orderNumber", p.getHyOrder().getOrderNumber());
            orderItem.put("sn", p.getSn()); // 产品编号
            orderItem.put("visaName", p.getProductName());
            orderItem.put("orderDate", p.gettDate());
            orderItem.put("contact", p.getHyOrder().getContact());
            orderItem.put("orderMoney", p.getOrderMoney());
            orderItem.put("refundMoney", p.getRefunds());
            orderItem.put("deductionPoint", p.getKoudian());
            orderItem.put("money", p.getMoney()); // 应付款金额
            visaOrder.add(orderItem);

            visaOrderSum = visaOrderSum.add(p.getMoney());  // 票务小计
        }

        //获取认购门票订单信息列表信息
        filters.clear();
        filters.add(Filter.eq("paymentLineId", id));
        filters.add(Filter.eq("productType", 6)); // 2酒店 3门票 4酒加景 5签证 6认购门票
        filters.add(Filter.eq("state", 1)); // 0 未提交 1 已提交
        list = payablesLineItemService.findList(null, filters, null);

        List<HashMap<String, Object>> ticketSoldOrder = new LinkedList<>();
        for (PayablesLineItem p : list) {
            HashMap<String, Object> orderItem = new HashMap<>();
            orderItem.put("id", p.getId());
            orderItem.put("orderNumber", p.getHyOrder().getOrderNumber());
            orderItem.put("sn", p.getSn()); // 产品编号
            orderItem.put("ticketSoldName", p.getProductName());
            orderItem.put("orderDate", p.gettDate());
            orderItem.put("contact", p.getHyOrder().getContact());
            orderItem.put("orderMoney", p.getOrderMoney());
            orderItem.put("refundMoney", p.getRefunds());
            orderItem.put("deductionPoint", p.getKoudian());
            orderItem.put("money", p.getMoney()); // 应付款金额
            ticketSoldOrder.add(orderItem);

            ticketSoldOrderSum = ticketSoldOrderSum.add(p.getMoney());  // 票务小计
        }




        //获取支付信息
        List<HashMap<String, Object>> paymentList = new LinkedList<>();
        filters.clear();
        filters.add(Filter.eq("reviewId", id));
        List<PayServicer> payServicers = payServicerService.findList(null,filters,null);
        for(PayServicer payServicer:payServicers){
            filters.clear();
            filters.add(Filter.eq("payId", payServicer.getId()));
            List<PayDetails> payDetails = payDetailsService.findList(null,filters,null);
            for(PayDetails p:payDetails){
                HashMap<String, Object> payItem = new HashMap<>();
                payItem.put("payMethod", p.getPayMethod());
                payItem.put("account", p.getAccount());
                payItem.put("amount", p.getAmount());
                payItem.put("date", p.getDate());
                payItem.put("operator", p.getOperator());
                paymentList.add(payItem);
            }
        }
        //本次使用欠款金额,本次应付款,总金额
        //add by wj
        BigDecimal benciyingfukuan = paymentSupplier.getMoneySum();
        BigDecimal debt = new BigDecimal("0.0");
        if(paymentSupplier.getDebtamount()!=null ){
            debt = paymentSupplier.getDebtamount();
            benciyingfukuan = benciyingfukuan.subtract(debt);
        }
        obj.put("benciyingfukuan",benciyingfukuan);
        obj.put("useDebt",debt);


        obj.put("lineOrder", lineOrder);
        obj.put("hotelOrder", hotelOrder);
        obj.put("ticketOrder", ticketOrder);
        obj.put("hotelAndSceneOrder", hotelAndSceneOrder);
        obj.put("visaOrder", visaOrder);
        obj.put("ticketSoldOrder", ticketSoldOrder);


        obj.put("paymentList", paymentList);

        obj.put("lineOrderSum", lineOrderSum);
        obj.put("hotelOrderSum", hotelOrderSum);
        obj.put("ticketOrderSum", ticketOrderSum);
        obj.put("hotelAndSceneOrderSum", hotelAndSceneOrderSum);
        obj.put("visaOrderSum", visaOrderSum);
        obj.put("ticketSoldOrderSum", ticketSoldOrderSum);


        //add by wj
        obj.put("total", paymentSupplier.getMoneySum());
        // 是否进行账目调账
        obj.put("modified", paymentSupplier.getModified());
        obj.put("modifyAmount", paymentSupplier.getModifyAmount());
        obj.put("dismissRemark", paymentSupplier.getDismissRemark());
        // 审核状态
        obj.put("status", paymentSupplier.getStatus());

        // 只对自动打款且step=10时  审核详情页才需要将财务调账的组件进行显示
        obj.put("step", paymentSupplier.getStep());

        return obj;
    }

    /** 供应商对账表 列表*/
    @Override
    public List<HashMap<String, Object>> getSupplierReconciliationList(String startDate, String endDate, String name) throws Exception {
        List<HashMap<String, Object>> res = new LinkedList<>();
        // ANY_VALUE 需要MySQL5.7之后的版本
        StringBuilder sql = new StringBuilder("SELECT GROUP_CONCAT(psgroup.id), ANY_VALUE(psgroup.supplier_name), SUM(psgroup.money_sum), SUM(psgroup.koudian_sum),SUM(IF(psgroup.status = 3, psgroup.money_sum, 0)),SUM(psgroup.debtamount) FROM (SELECT ps.id, ps.supplier_name, ps.supplier_contract, ps.money_sum, ps.koudian_sum, ps.modified, ps.modify_amount, ps.debtamount, ps.status FROM hy_payment_supplier ps WHERE ps.is_valid = 1 ");

        if(StringUtils.isNotBlank(startDate)){
            sql.append(" AND ps.create_time >= '");
            sql.append(startDate.substring(0, 10)+ " " + "00:00:00");
            sql.append("'");
        }
        if(StringUtils.isNotBlank(endDate)){
            sql.append(" AND ps.create_time <= '");
            sql.append(endDate.substring(0, 10)+ " " + "23:59:59");
            sql.append("'");
        }
        if(StringUtils.isNotBlank(name)){
            sql.append(" AND ps.supplier_name LIKE '%");
            sql.append(name);
            sql.append("%'");
        }
        sql.append(") AS psgroup LEFT JOIN hy_supplier_contract ON psgroup.supplier_contract = hy_supplier_contract.id GROUP BY hy_supplier_contract.supplier_id");

        /*
        obj[0] GROUP_CONCAT(psgroup.id) hy_payment_supplier的id串  形如"23,56,64,63"
        obj[1] supplier_name　供应商名称
        obj[2] 同一供应商的应付金额之和(已减去扣点)
        obj[3] 同一供应商的扣点金额之和
        obj[4] 同一供应商的已付金额之和
        obj[5] 同一供应商的使用欠款之和

        应付 + 扣点 = 订单金额
        */
        List<Object[]> objects = super.statis(sql.toString());
        for (Object[] obj : objects) {
            HashMap<String, Object> map = new HashMap<>();
            String ids = String.valueOf(obj[0]);
            map.put("str", DESUtils.getEncryptString(ids));
            map.put("supplierName", obj[1]);
            BigDecimal moneySum = (BigDecimal) obj[2];
            BigDecimal koudianSum = obj[3] == null ? BigDecimal.ZERO : (BigDecimal) obj[3];
            map.put("orderMoney", moneySum.add(koudianSum));
            map.put("deduction", koudianSum);
            map.put("hasPay", obj[4] == null ? 0 : obj[4]);
            map.put("own", obj[5] == null ? 0 : obj[5]);
            res.add(map);
        }
        return res;
    }

	/**
	 * 打款单审核列表Excel导出的列表数据的获取
	 * 基于payServierPreReviewList() 删除分页相关
	 * 未获取到数据返回空的List<Map<String, Object>>
	 * */
    @Override
	public List<Map<String, Object>> getAuditList(Integer state, String supplierName, String orderNumber,String username) throws Exception{
		List<Map<String, Object>> ans = new LinkedList<>();
		HashSet<Long> paymentSupplierSet = new HashSet<>();
		if(StringUtils.isNotBlank(orderNumber)){
			// 根据orderNumber获取hy_payment_supplier的id的集合
			StringBuilder sqlOrderNumber = new StringBuilder("SELECT hy_order.order_number, pli.payment_line_id FROM hy_order LEFT JOIN hy_payables_line_item AS pli ON hy_order.id = pli.order_id WHERE hy_order.order_number LIKE '%");
			sqlOrderNumber.append(orderNumber);
			sqlOrderNumber.append("%'");
			List<Object[]> list = super.statis(sqlOrderNumber.toString());
			if(CollectionUtils.isEmpty(list)){
				return ans;
			}
			for (Object[] obj: list) {
				// TODO 根据后面的调用 是否有必要Object转BigIntegr转Long
				if(obj[1] != null){
					paymentSupplierSet.add(((BigInteger)obj[1]).longValue());
				}
			}
			if(CollectionUtils.isEmpty(paymentSupplierSet)){
				return ans;
			}
		}
		StringBuilder sql = new StringBuilder("SELECT ps.id, ps.pay_code, ps.supplier_name, ps.operator, (SELECT name  FROM hy_admin WHERE hy_admin.username = ps.operator) AS operator_name, ps.money_sum, (SELECT name FROM hy_admin WHERE hy_admin.username = ps.creator) AS creator, ps.create_time, ps.pay_date, ps.status,hy_order.order_number,pli.sn,pli.product_name,pli.t_date,pli.order_money,pli.refunds,pli.koudian,(SELECT store_name FROM hy_store WHERE hy_store.id = hy_order.store_id) AS store_name,pli.money,(SELECT name FROM hy_admin WHERE hy_admin.username = (SELECT operator_id FROM hy_store WHERE hy_store.id = hy_order.store_id)) AS op, ps.process_instance_id, hy_order.people, hy_order.contact, (SELECT hy_bank_list.bank_account FROM hy_bank_list WHERE hy_bank_list.id = (SELECT hy_supplier_contract.bank_list FROM hy_supplier_contract WHERE hy_supplier_contract.id = ps.supplier_contract)) AS bank_account, hy_order.jiusuan_money,(SELECT hy_bank_list.bank_name FROM hy_bank_list WHERE hy_bank_list.id = (SELECT hy_supplier_contract.bank_list FROM hy_supplier_contract WHERE hy_supplier_contract.id = ps.supplier_contract)) AS bank_name FROM (hy_payment_supplier AS ps RIGHT JOIN hy_payables_line_item  AS pli ON ps.id = pli.payment_line_id) LEFT JOIN hy_order ON pli.order_id = hy_order.id WHERE pli.state = 1 AND ps.is_valid = 1");

		// 筛选条件: 供应商名称
		if(StringUtils.isNotBlank(supplierName)){
			sql.append(" AND ps.supplier_name LIKE '%");
			sql.append(supplierName);
			sql.append("%'");
		}
		// 筛选条件: 订单编号
		if(CollectionUtils.isNotEmpty(paymentSupplierSet)){
			sql.append(" AND ps.id IN (");
			for (Long id : paymentSupplierSet) {
				sql.append(id);
				sql.append(",");
			}
			// 删除多余的逗号
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");
		}
		// 筛选条件: 审批状态
		sql.append(" AND ps.process_instance_id IN (");
		HashSet<String> taskProcessInstanceIdSet = new HashSet<>();
		if (state == null) {
			// TODO 只通过一次操作获取待办任务和已完成任务
			List<Task> tasks = ActivitiUtils.getTaskList(username, ActivitiUtils.PAY_SERVICE_PRE);
			List<HistoricTaskInstance> hisTasks = ActivitiUtils.getHistoryTaskList(username, ActivitiUtils.PAY_SERVICE_PRE);
			// 没有任何待办或已办任务
			if(CollectionUtils.isEmpty(tasks) && CollectionUtils.isEmpty(hisTasks)){
				return ans;
			}
			for (Task task : tasks) {
				sql.append(task.getProcessInstanceId());
				sql.append(",");
				taskProcessInstanceIdSet.add(task.getProcessInstanceId());
			}
			for (HistoricTaskInstance hisTask : hisTasks) {
				sql.append(hisTask.getProcessInstanceId());
				sql.append(",");
			}
		} else if (state == 0) {
            /* 搜索未完成任务*/
			List<Task> tasks = ActivitiUtils.getTaskList(username, ActivitiUtils.PAY_SERVICE_PRE);
			// 没有任何待办任务
			if(CollectionUtils.isEmpty(tasks)){
				return ans;
			}
			for (Task task : tasks) {
				sql.append(task.getProcessInstanceId());
				sql.append(",");
			}
		} else if (state == 1) {
            /*搜索已完成任务*/
			List<HistoricTaskInstance> hisTasks = ActivitiUtils.getHistoryTaskList(username, ActivitiUtils.PAY_SERVICE_PRE);
			// 没有任何已办任务
			if(CollectionUtils.isEmpty(hisTasks)){
				return ans;
			}
			for (HistoricTaskInstance hisTask : hisTasks) {
				sql.append(hisTask.getProcessInstanceId());
				sql.append(",");
			}
		}
		// 删除多余的逗号
		sql.deleteCharAt(sql.length() - 1);
		// 按hy_payment_supplier的id降序
		sql.append(") ORDER BY ps.id DESC");
		List<Object[]> list = super.statis(sql.toString());
        /*
         id                     obj[0]
         pay_code               obj[1]
         supplier_name          obj[2]
         operator               obj[3]
         operator_name          obj[4]
         money_sum              obj[5]
         creator                obj[6]
         create_time            obj[7]
         pay_date               obj[8]
         status                 obj[9]
         process_instance_id    obj[20]
         bank_account           obj[23]
         bank_name              obj[25]
         -------  1 ——> n -------
         order_number           obj[10]
         sn                     obj[11]
         product_name           obj[12]
         t_date                 obj[13]
         order_money            obj[14]
         refunds                obj[15]
         koudian                obj[16]
         store_name             obj[17]
         money                  obj[18]
         op                     obj[19]
         people                 obj[21]
         contact                obj[22]
         jiusuan_money          obj[24]
        */
		for (Object[] obj : list) {
			HashMap<String, Object> m = new HashMap<>();
			if (null == state) {
				String processInstanceId = (String) obj[20];
				// 当筛选“全部”的情况，只要当前待办任务的流程实例id集合中，包含了该任务的流程实例id，则说明该任务处于待办状态
				// state 0:待审核  1:已审核
				m.put("state", taskProcessInstanceIdSet.contains(processInstanceId) ? "待审核" : "已审核");
			} else {
				m.put("state", state == 0 ? "待审核" : "已审核");
			}
			m.put("payCode", obj[1]);
			m.put("supplierName", obj[2]);
			m.put("operator", obj[3] == null ? "" : obj[3]);
			m.put("operatorName", obj[4]);
			m.put("moneySum", obj[5]);
			m.put("createTime", obj[7]);
			m.put("payTime", obj[8]);
			m.put("status", obj[9]);
			m.put("orderNumber", obj[10]);
			m.put("sn", obj[11]);
			m.put("productName", obj[12]);
			m.put("tDate", obj[13]);
			m.put("orderMoney", obj[14]);
			m.put("koudian", obj[16]);
			m.put("storeName", obj[17]);
			m.put("money", obj[18]);
			m.put("op", obj[19]);
			m.put("people", obj[21]);
			m.put("contact", obj[22]);
			m.put("bankAccount", obj[23]);
			//m.put("income", obj[24]);

			//income = jiusuan_money - adjust_money - discounted_price - store_fan_li
			BigDecimal income = (BigDecimal) obj[24];
			if(obj.length>=29) {//add by cqx 20190709
				if(null != obj[28]) {
					income = income.subtract((BigDecimal) obj[28]);
				}
			}
			if(obj.length>=30) {//add by cqx 20190709
				if(null != obj[29]) {
					income = income.subtract((BigDecimal) obj[29]);
				}
			}
			if(obj.length>=31) {//add by cqx 20190709
				if(null != obj[30]) {
					income = income.subtract((BigDecimal) obj[30]);
				}
			}
            m.put("income", income);
            
			m.put("bankName", obj[25]);
			ans.add(m);
		}
		return ans;
	}
}
