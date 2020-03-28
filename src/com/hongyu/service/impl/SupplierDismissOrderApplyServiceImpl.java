package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.entity.*;
import com.hongyu.service.*;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.dao.SupplierDismissOrderApplyDao;

/**
 * @author xyy
 * */
@Service("supplierDismissOrderApplyServiceImpl")
public class SupplierDismissOrderApplyServiceImpl extends BaseServiceImpl<SupplierDismissOrderApply, Long>
		implements SupplierDismissOrderApplyService {

    @Resource(name = "supplierDismissOrderApplyDaoImpl")
    SupplierDismissOrderApplyDao dao;

    @Resource(name = "supplierDismissOrderApplyDaoImpl")
    public void setBaseDao(SupplierDismissOrderApplyDao dao) {
        super.setBaseDao(dao);
    }

	@Resource(name = "storePreSaveServiceImpl")
	private StorePreSaveService storePreSaveService;

	@Resource(name = "storeAccountLogServiceImpl")
	private StoreAccountLogService storeAccountLogService;

	@Resource(name = "storeAccountServiceImpl")
	private StoreAccountService storeAccountService;

	@Resource(name = "storeServiceImpl")
    private StoreService storeService;

	@Resource(name = "refundRecordsServiceImpl")
    private RefundRecordsService refundRecordsService;

	@Resource(name = "refundInfoServiceImpl")
    private RefundInfoService refundInfoService;

	@Resource(name = "supplierDismissOrderApplyServiceImpl")
    private SupplierDismissOrderApplyService supplierDismissOrderApplyService;

	@Resource(name = "hyGroupServiceImpl")
    private HyGroupService hyGroupService;
	
	@Resource(name = "hyTicketInboundServiceImpl")
    private HyTicketInboundService hyTicketInboundService;
	
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;

	@Resource(name = "hyOrderServiceImpl")
    private HyOrderService hyOrderService;

	@Resource(name = "hyAdminServiceImpl")
    private HyAdminService hyAdminService;
	
	@Resource(name = "hyRegulateServiceImpl")
    private HyRegulateService hyRegulateService;

	@Resource(name = "payandrefundRecordServiceImpl")
	private PayandrefundRecordService payandrefundRecordService;

	/** 供应商提交驳回申请 */
	@Override
	public Json addSupplierDismissOrderSubmit(Long orderId, String comment, HttpSession session) throws Exception {
		Json json = new Json();
		HyOrder hyOrder = hyOrderService.find(orderId);

		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);
		
		Map<String, Object> map = new HashMap(1);
		map.put("person","provider");

		ProcessInstance pi = runtimeService.startProcessInstanceByKey("suppilerDismissOrder",map);
		// 根据流程实例Id查询任务
		Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
		Authentication.setAuthenticatedUserId(username);
		taskService.addComment(task.getId(), pi.getProcessInstanceId(), comment + ":1");
		taskService.complete(task.getId());

		SupplierDismissOrderApply supplierDismissOrderApply = new SupplierDismissOrderApply();
		supplierDismissOrderApply.setOrderId(orderId);
		supplierDismissOrderApply.setCreateTime(new Date());
		supplierDismissOrderApply.setOperator(hyAdmin);
		// 待审核
		supplierDismissOrderApply.setStatus(0);
		// 供应商驳回申请
		supplierDismissOrderApply.setType(1);
		supplierDismissOrderApply.setProcessInstanceId(pi.getProcessInstanceId());
		// 应退给门店的金额
		BigDecimal money = hyOrder.getJiusuanMoney().add(hyOrder.getTip()).subtract(hyOrder.getStoreFanLi())
				.subtract(hyOrder.getDiscountedPrice());
		supplierDismissOrderApply.setMoney(money);
		System.out.println(money);
		this.save(supplierDismissOrderApply);
		// 供应商驳回提交成功后需要更新订单状态 4:供应商驳回
		hyOrder.setStatus(4);
		hyOrderService.update(hyOrder);

		json.setMsg("操作成功");
		json.setSuccess(true);
		return json;
	}
	@Resource(name = "branchBalanceServiceImpl")
	private BranchBalanceService branchBalanceService;

	@Resource(name = "branchPreSaveServiceImpl")
	private BranchPreSaveService branchPreSaveService;

	/** 供应商驳回订单申请 - 总公司财务审核 */
    @Override
    public Json addSupplierDismissOrderAudit(Long id, String comment, Integer state, HttpSession session) throws Exception {
        Json json = new Json();
        SupplierDismissOrderApply supplierDismissOrderApply = supplierDismissOrderApplyService.find(id);
        String username = (String) session.getAttribute(CommonAttributes.Principal);
        String processInstanceId = supplierDismissOrderApply.getProcessInstanceId();
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        Authentication.setAuthenticatedUserId(username);
        // 财务只能同意
        state = 1;
        taskService.addComment(task.getId(), processInstanceId, (comment == null ? " " : comment) + ":" + state);
        taskService.claim(task.getId(), username);
        taskService.complete(task.getId());
        // 置为已审核
        supplierDismissOrderApply.setStatus(1);
        supplierDismissOrderApplyService.update(supplierDismissOrderApply);

        HyOrder hyOrder = hyOrderService.find(supplierDismissOrderApply.getOrderId());
        Integer orderSource = hyOrder.getSource();
        // 订单来自门店 (即使为混合支付,亦视为完全使用门店预存款,订单被驳回后,将订单金额返还到总公司的门店预存款中)
        if (orderSource == 0) {
            Store store = storeService.find(hyOrder.getStoreId());
			// 判断门店类型 0虹宇门店，1挂靠门店，2直营门店，3非虹宇门店
			/*直营门店*/
			if (2 == store.getStoreType()) {
				// 直营门店store-->分公司连锁发展 getSuoshuDepartment()-->分公司getHyDepartment()
				Department department = store.getSuoshuDepartment().getHyDepartment();
				if(department == null){
					throw new Exception("直营门店对应的分公司不存在!");
				}

				// 修改BranchBalance
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("branchId", department.getId()));
				List<BranchBalance> list = branchBalanceService.findList(null, filters, null);
				if(list==null || list.isEmpty()){
					json.setMsg("直营门店对应的分公司不存在");
					json.setSuccess(true);
					return json;
				}
				BranchBalance branchBalance = list.get(0);
				synchronized (BranchBalance.class){

					// 修改BranchBalance
					branchBalance.setBranchBalance(branchBalance.getBranchBalance().add(supplierDismissOrderApply.getMoney()));
					branchBalanceService.update(branchBalance);
				}

				// 增加BranchPreSave
				BranchPreSave branchPreSave = new BranchPreSave();
				branchPreSave.setBranchId(department.getId());
				branchPreSave.setDepartmentName(department.getName());
				branchPreSave.setType(18); //供应商驳回
				branchPreSave.setDate(new Date());
				branchPreSave.setAmount(supplierDismissOrderApply.getMoney());
				branchPreSave.setPreSaveBalance(branchBalance.getBranchBalance());
				branchPreSave.setRemark(hyOrder.getRemark());
				branchPreSave.setOrderId(hyOrder.getId());
				branchPreSaveService.save(branchPreSave);
			} else {
				/*非直营的其他门店*/
				// 修改StoreAccount
				/* 修改门店的预存款余额 */
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("store", store));
				List<StoreAccount> list = storeAccountService.findList(null, filters, null);
				StoreAccount storeAccount = list.get(0);
				storeAccount.setBalance(storeAccount.getBalance().add(supplierDismissOrderApply.getMoney()));
				storeAccountService.update(storeAccount);

				/* 门店预存款修改记录*/
				StoreAccountLog storeAccountLog = new StoreAccountLog();
				storeAccountLog.setStore(store);
				// 类型,0充值，1订单抵扣，2分成，3退团，4消团， 5供应商驳回订单
				storeAccountLog.setType(5);
				// 1通过
				storeAccountLog.setStatus(1);
				storeAccountLog.setMoney(supplierDismissOrderApply.getMoney());
				// 订单编号
				storeAccountLog.setOrderSn(hyOrder.getOrderNumber());
				storeAccountLog.setCreateDate(new Date());
				storeAccountLogService.save(storeAccountLog);

				/* 财务中心-门店预存款记录修改*/
				StorePreSave storePreSave = new StorePreSave();
				storePreSave.setStoreId(store.getId());
				storePreSave.setStoreName(store.getStoreName());
				/*
				 * 1:门店充值 2:报名退款  3:报名冲抵
				 * 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵
				 * 7:保险弃撤 8:签证购买冲抵 9:签证退款
				 * 10:酒店销售 11:酒店退款 12:门店后返
				 * 13:供应商驳回订单 14:门店租导游 15:酒加景销售
				 * 16:酒加景退款 17:门店综合服务18：租借导游退款
				 * 19:保险退款 20:认购门票退款 21：门店门票退款
				 * 22:门店提现
				 */
				storePreSave.setType(13);
				storePreSave.setDate(new Date());
				storePreSave.setAmount(supplierDismissOrderApply.getMoney());
				storePreSave.setPreSaveBalance(storeAccount.getBalance());
				storePreSave.setOrderId(hyOrder.getId());
				storePreSave.setOrderCode(hyOrder.getOrderNumber());
				storePreSaveService.save(storePreSave);
			}



            /* 生成退款记录(已付款)  无需出纳操作*/
            // 退款信息表
            RefundInfo refundInfo = new RefundInfo();
            // 1:已付款
            refundInfo.setState(1);
            // 3:供应商驳回订单
            refundInfo.setType(3);
            refundInfo.setApplyDate(supplierDismissOrderApply.getCreateTime());
            refundInfo.setAppliName(supplierDismissOrderApply.getOperator().getName());
            refundInfo.setAmount(supplierDismissOrderApply.getMoney());
            refundInfo.setRemark(hyOrder.getRemark());
            refundInfo.setPayDate(new Date());
            refundInfo.setOrderId(hyOrderService.find(supplierDismissOrderApply.getOrderId()).getId());
            refundInfoService.save(refundInfo);

            // 退款记录表
            RefundRecords refundRecords = new RefundRecords();
            refundRecords.setRefundInfoId(refundInfo.getId());
            refundRecords.setOrderCode(hyOrder.getOrderNumber());
            refundRecords.setTouristName(hyOrder.getContact());
            refundRecords.setSignUpMethod(orderSource);
            refundRecords.setAmount(supplierDismissOrderApply.getMoney());
            refundRecords.setRefundMethod(1L);
            refundRecords.setPayDate(new Date());
     		refundRecords.setStoreName(store.getStoreName());
            refundRecords.setStoreId(hyOrder.getStoreId());
            refundRecords.setOrderId(hyOrder.getId());
            refundRecordsService.save(refundRecords);
        } else {
            // 订单来源为官网
            /* 生成退款记录(待付款)  需手动填写退款相关内容*/
            // 退款信息表
            RefundInfo refundInfo = new RefundInfo();
            // 0:未付款
            refundInfo.setState(0);
            // 3:供应商驳回订单
            refundInfo.setType(3);
            refundInfo.setApplyDate(supplierDismissOrderApply.getCreateTime());
            refundInfo.setAppliName(supplierDismissOrderApply.getOperator().getName());
            refundInfo.setAmount(supplierDismissOrderApply.getMoney());
            refundInfo.setRemark(hyOrder.getRemark());
            refundInfo.setPayDate(new Date());
            refundInfo.setOrderId(hyOrderService.find(supplierDismissOrderApply.getOrderId()).getId());
            refundInfoService.save(refundInfo);

            // 退款记录表
            RefundRecords refundRecords = new RefundRecords();
            refundRecords.setRefundInfoId(refundInfo.getId());
            refundRecords.setOrderCode(hyOrder.getOrderNumber());
            refundRecords.setTouristName(hyOrder.getContact());
            refundRecords.setSignUpMethod(orderSource);
            refundRecords.setAmount(supplierDismissOrderApply.getMoney());
            refundRecords.setPayDate(new Date());
            // 官网下单没有门店名称
            refundRecords.setStoreName(null);
            refundRecords.setStoreId(hyOrder.getStoreId());
            refundRecords.setOrderId(hyOrder.getId());
            refundRecordsService.save(refundRecords);
        }

		// 5.修改订单状态
        //订单状态0待门店支付，1待门店确认，2待供应商确认，3供应商通过，4驳回待财务确认,5已驳回,6已取消
		hyOrder.setStatus(5);
		hyOrder.setRefundstatus(2);
		//退款状态0未退款，1退款中，2全部已退款 3部分退款,4退款已驳回
		hyOrderService.update(hyOrder);

		// 6. 写退款记录表
		PayandrefundRecord record = new PayandrefundRecord();
		record.setOrderId(supplierDismissOrderApply.getOrderId());
		record.setMoney(supplierDismissOrderApply.getMoney());
        // 5预存款
		record.setPayMethod(5);
        //1退款
		record.setType(1);
        //1已退款
		record.setStatus(1);
		record.setCreatetime(new Date());
		payandrefundRecordService.save(record);

		// 7.修改库存和报名人数
		Integer type=hyOrder.getType();
		//如果是线路订单
		if(type==1) {
			Long groupId = hyOrder.getGroupId();
			HyGroup hyGroup = hyGroupService.find(groupId);
			synchronized(hyGroup){
				hyGroup.setStock(hyGroup.getStock() + hyOrder.getPeople());
				hyGroup.setSignupNumber(hyGroup.getSignupNumber() - hyOrder.getPeople());
				hyGroupService.update(hyGroup);
			}
			
			//added by GSbing,20190301,修改计调报账人数
			List<Filter> hyRegulateFilter = new ArrayList<>();
			hyRegulateFilter.add(Filter.eq("hyGroup", hyGroup.getId()));
			List<HyRegulate> hyRegulates = hyRegulateService.findList(null, hyRegulateFilter, null);
			if(hyRegulates.size() != 0) {
				HyRegulate hyRegulate = hyRegulates.get(0);
				synchronized(hyRegulate) {
					hyRegulate.setVisitorNum(hyGroup.getSignupNumber());
					hyRegulateService.update(hyRegulate);
				}		
			}
			
		}		
		else if(type==3) {	//如果是票务的酒店订单
			//恢复库存
			hyTicketInboundService.recoverTicketInboundByTicketOrder(hyOrder);
		}	
		//如果是票务酒加景订单
		else if(type==5) {
			//修改库存
			HyOrderItem orderItem=hyOrder.getOrderItems().get(0);
			Long priceId=orderItem.getPriceId();
			List<Filter> inboundfilter=new ArrayList<>();
			inboundfilter.add(Filter.eq("type", 1)); 
			inboundfilter.add(Filter.eq("priceInboundId", priceId));
			inboundfilter.add(Filter.eq("day", orderItem.getStartDate()));
			List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,inboundfilter,null);
		    HyTicketInbound hyTicketInbound=ticketInbounds.get(0);
			hyTicketInbound.setInventory(hyTicketInbound.getInventory()+orderItem.getNumber());
			hyTicketInboundService.update(hyTicketInbound);
		}
		//如果是门票的订单
		else if(type == 4) {
			//也是修改库存
			HyOrderItem orderItem=hyOrder.getOrderItems().get(0);
			Long priceId=orderItem.getPriceId();
			List<Filter> inboundfilter=new ArrayList<>();
			inboundfilter.add(Filter.eq("type", 1)); 
			inboundfilter.add(Filter.eq("priceInboundId", priceId));
			inboundfilter.add(Filter.eq("day", orderItem.getStartDate()));
			List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,inboundfilter,null);
		    HyTicketInbound hyTicketInbound=ticketInbounds.get(0);
			hyTicketInbound.setInventory(hyTicketInbound.getInventory()+orderItem.getNumber());
			hyTicketInboundService.update(hyTicketInbound);
		}

		json.setMsg("操作成功");
		json.setSuccess(true);
		return json;
	}
}