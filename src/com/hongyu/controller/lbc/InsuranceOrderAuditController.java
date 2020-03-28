package com.hongyu.controller.lbc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BranchBalance;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.RefundInfo;
import com.hongyu.entity.RefundRecords;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.entity.StorePreSave;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.GroupDivideService;
import com.hongyu.service.GroupMemberService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationItemService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyReceiptRefundService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.PayServicerService;
import com.hongyu.service.PayablesLineItemService;
import com.hongyu.service.PayablesLineService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.service.PaymentSupplierService;
import com.hongyu.service.ReceiptServicerService;
import com.hongyu.service.ReceiptTotalServicerService;
import com.hongyu.service.RefundInfoService;
import com.hongyu.service.RefundRecordsService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StorePreSaveService;
import com.hongyu.service.StoreService;
import com.hongyu.service.SupplierDismissOrderApplyService;


@Controller
@RequestMapping("/admin/insuranceorderaudit/")
public class InsuranceOrderAuditController {

	@Resource(name = "payablesLineItemServiceImpl")
	PayablesLineItemService payablesLineItemService;

	@Resource(name = "payablesLineServiceImpl")
	PayablesLineService payablesLineService;

	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;

	@Resource(name = "hyReceiptRefundServiceImpl")
	HyReceiptRefundService hyReceiptRefundService;

	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;

	@Resource(name = "hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;

	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;

	@Resource
	private RuntimeService runtimeService;

	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;

	@Resource(name = "hyGroupServiceImpl")
	private HyGroupService hyGroupService;

	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;

	@Resource(name = "supplierDismissOrderApplyServiceImpl")
	SupplierDismissOrderApplyService supplierDismissOrderApplyService;

	@Resource(name = "groupMemberServiceImpl")
	GroupMemberService groupMemberService;

	@Resource(name = "groupDivideServiceImpl")
	GroupDivideService groupDivideService;
	
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
	
	@Resource(name = "refundInfoServiceImpl")
	RefundInfoService refundInfoService;
	
	@Resource(name = "refundRecordsServiceImpl")
	RefundRecordsService refundRecordsService;
	
	@Resource(name = "receiptTotalServicerServiceImpl")
	ReceiptTotalServicerService receiptTotalServicerService;
	
	@Resource(name = "receiptServicerServiceImpl")
	ReceiptServicerService receiptServicerService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;
	
	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;
	
	@Resource(name = "hyOrderApplicationItemServiceImpl")
	private HyOrderApplicationItemService hyOrderApplicationItemService;
	
	@Resource(name = "hyOrderCustomerServiceImpl")
	private HyOrderCustomerService hyOrderCustomerService;
	
	@Resource(name = "insuranceServiceImpl")
	private InsuranceService insuranceService;
	
	@Resource(name = "hyDepartmentModelServiceImpl")
	private HyDepartmentModelService hyDepartmentModelService;
	
	@Autowired
	BranchBalanceService branchBalanceService;

    @Autowired
	BranchPreSaveService branchPreSaveService;
	
	/**
	 * 审核列表
	 * @author LBC
	 */
	@RequestMapping(value = "order_refund/audit_list/view")
	@ResponseBody
	public Json OrderRefundAuditList(Pageable pageable,Integer status, @DateTimeFormat(pattern="yyyy-MM-dd") Date startTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime, HttpSession session) {
		return this.getAuditList(pageable, status, session, startTime, endTime);
	}

	public Json getAuditList(Pageable pageable, Integer status, HttpSession session, @DateTimeFormat(pattern="yyyy-MM-dd") Date startTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime) {
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		Json json = new Json();
		try {

			List<Filter> applyFilters = new ArrayList<>();
			if(status != null) {
				//通过的 已退款
				if(status == 0) {
					applyFilters.add(Filter.eq("status", 1));
				}
				//驳回的 已驳回
				else if(status == 1) {
					applyFilters.add(Filter.eq("status", 2));
				}
				//未审核（待财务审核）
				else if(status == 2){
					applyFilters.add(Filter.eq("status", 0));
				}
				
			}
			//门店撤保退款
			applyFilters.add(Filter.ge("type", HyOrderApplication.STORE_INSURANCE_REFUND));
			applyFilters.add(Filter.le("type", HyOrderApplication.OFFICIAL_WEBSITE_REFUND));
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createtime"));
			
			if(startTime != null) {
				applyFilters.add(Filter.ge("createtime", startTime));
			}
			if(endTime != null) {
				applyFilters.add(Filter.le("createtime", endTime));
			}
			
			List<HyOrderApplication> ans = hyOrderApplicationService.findList(null, applyFilters, orders);
			List<Map<String, Object> > list = new ArrayList<>();
			for(int i = 0; i < ans.size(); i++) {
				Map<String, Object> map = new HashMap<>();
				map.put("hyOrderApplication", ans.get(i));
				HyOrder hyOrder = hyOrderService.find(ans.get(i).getOrderId());
				map.put("insuranceName", hyOrder.getName());
				map.put("orderNumber", hyOrder.getOrderNumber());
				map.put("orderCreateTime", hyOrder.getCreatetime());
				map.put("store", storeService.find(hyOrder.getStoreId()));
				map.put("remark", ans.get(i).getView());
				list.add(map);
			}

			int page = pageable.getPage();
			int rows = pageable.getRows();
			Page<Map<String, Object>> pages = new Page<>(new ArrayList<Map<String, Object>>(), 0, pageable);
			if (list != null && !list.isEmpty()) {
				pages.setTotal(list.size());
				pages.setRows(list.subList((page - 1) * rows, page * rows > list.size() ? list.size() : page * rows));
			}

			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(pages);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 审核详情
	 * @author LBC
	 */
	//因为只有一个审核步骤，所以没有审核详情
	@RequestMapping(value = "order_refund/audit_detail/view" )
	@ResponseBody
	public Json OrderRefundAuditDetail(Long id) {
		Json json = new Json();

		try {
			HyOrderApplication application = hyOrderApplicationService.find(id);
			if (application == null) {
				throw new Exception("没有有效的审核申请记录");
			}
			HyOrder order = hyOrderService.find(application.getOrderId());

			Map<String, Object> ans = new HashMap<>();
			Map<String, Object> map = new HashMap<>();
			map.put("id", application.getId());
			map.put("status", application.getStatus());
			if(order == null){
				return null;
			}
			map.put("orderNumber", order.getOrderNumber());	//订单编号
			map.put("source", order.getSource());	//订单来源
			if(order.getStoreId()!=null){
				Store store = storeService.find(order.getStoreId());
				map.put("storeName", store.getStoreName());
			}
			
			
			map.put("contactName", order.getOperator().getName());	//门店联系人姓名
			map.put("contactPhone", order.getOperator().getMobile());	//门店联系人电话
			map.put("jiesuanMoney", order.getJiesuanMoney1());	//订单结算金额
			map.put("jiesuanRefund", application.getJiesuanMoney());	//结算退款金额
			map.put("waimaiMoney",order.getWaimaiMoney());	//订单外卖金额
			map.put("waimaiRefund", application.getWaimaiMoney());	//订单外卖退款
			map.put("baoxianJiesuanRefund", application.getBaoxianJiesuanMoney());	//保险结算退款金额
			map.put("baoxianWaimaiRefund", application.getBaoxianWaimaiMoney());	//保险外卖退款金额

			map.put("startDay", order.getFatuandate());	//保险日期
			map.put("tianshu", order.getTianshu());	//保险天数
			map.put("lineName", order.getXianlumingcheng());	//线路名称
			
			
			map.put("applyTime", application.getCreatetime());	//申请日期

			/** 审核详情 */
			ans.put("application", map);
			List<Map<String, Object>> list = new ArrayList<>();
			for(HyOrderApplicationItem item : application.getHyOrderApplicationItems()) {
				HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
				Map<String, Object> map1 = new HashMap<>();
				if(orderItem == null) {
					map1.put("id", item.getId());
					map1.put("itemId", null);
					map1.put("jiesuanRefund", item.getJiesuanRefund());	
					map1.put("waimaiRefund", item.getWaimaiRefund());
					map1.put("baoxianJiesuanRefund", item.getBaoxianJiesuanRefund());
					map1.put("baoxianWaimaiRefund", item.getBaoxianWaimaiRefund());
				}
				else {
					map1.put("id", item.getId());
					map1.put("itemId", orderItem.getId());
					map1.put("type", orderItem.getType());
					map1.put("priceType", orderItem.getPriceType());
					map1.put("name", orderItem.getName());
					map1.put("number", orderItem.getNumber());
					map1.put("jiesuanPrice", orderItem.getJiesuanPrice());
					map1.put("jiesuanRefund", item.getJiesuanRefund());
					map1.put("waimaiPrice", orderItem.getWaimaiPrice());
					map1.put("waimaiRefund", item.getWaimaiRefund());
					map1.put("baoxianJiesuanPrice", orderItem.getJiesuanPrice());
					map1.put("baoxianJiesuanRefund", item.getBaoxianJiesuanRefund());
					map1.put("baoxianWaimaiPrice", orderItem.getJiesuanPrice());
					map1.put("baoxianWaimaiRefund", item.getBaoxianWaimaiRefund());
					map1.put("status", orderItem.getStatus());
				}
				list.add(map1);
			}
			
			
			
			
			ans.put("applicationItems", list);


			json.setSuccess(true);
			json.setMsg("查看详情成功");
			json.setObj(ans);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查看详情失败");
			json.setObj(e.getMessage());
		}
		return json;
	}
	
	
	/**
	 * 退款审核
	 * @author LBC
	 */
	@Transactional
	@RequestMapping(value = "order_refund/audit", method = RequestMethod.POST)
	@ResponseBody
	public Json storeInsuranceOrderRefundAudit(Long id, String remark, Integer type, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			
			//用 orderapplication
			HyOrderApplication application = hyOrderApplicationService.find(id);
			String applyName = application.getOperator().getUsername(); // 找到提交申请的人
//			String processInstanceId = application.getProcessInstanceId();
//
//			if (processInstanceId == null || processInstanceId.equals("")) {
//				throw new Exception("审核出错，信息不完整，请重新申请");
//			}

//			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			HashMap<String, Object> map = new HashMap<>(); // 保存流转信息和流程变量信息
															// 下一阶段审核的部门

			if (type.equals(0)) { // 如果审核通过
				map.put("msg", "true");
				
				boolean isPartRefund = false;
				//查看是部分退款 还是全部退款
				//对每一个applicationOrderItem
				
				BigDecimal allRefund = new BigDecimal("0");
				
				HyOrder hyOrder = hyOrderService.find(application.getOrderId());
				
				//有效的orderitems的size
				int disabledSize = 0;
				for(HyOrderItem hyOrderItem : hyOrder.getOrderItems()) {
					if(hyOrderItem.getStatus().equals(1)) {
						//无效
						disabledSize ++;
					}
				}
				
				//有效的orderitem比退的多，那么只退了一部分
				if(hyOrder.getOrderItems().size() - disabledSize > application.getHyOrderApplicationItems().size()) {
					isPartRefund = true;
				}
				
				//退款的时候应该先退全款 再
//				for(HyOrderApplicationItem hyOrderApplicationItem : application.getHyOrderApplicationItems()) {
//					allRefund = allRefund.add(hyOrderApplicationItem.getBaoxianWaimaiRefund());
//				}
				
				//应该退全款
				allRefund = hyOrder.getWaimaiMoney();
			
				
				
				//不是部分退款 全部退款
				//if(isPartRefund == false) {
				/* add by liyang,change the insurance order status */
				//部分退款的话也变成撤保状态
				/**
				 * 因为只有撤保之后状态才能变成已撤保
				 * 在没有调用撤保接口之前先改变状态会出现数据混乱
				 */
//				List<Filter> insurancefilters = new ArrayList<>();
//				insurancefilters.add(Filter.eq("orderId", application.getOrderId()));
//				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null, insurancefilters, null);
//				if(!insuranceOrders.isEmpty()){
//					for(InsuranceOrder tmp:insuranceOrders){
//						//调用撤保接口
//						//Json res = insuranceOrderService.cancelInsuranceOrder(tmp.getId());					
//						tmp.setStatus(4);
//						insuranceOrderService.update(tmp);
//					}
//				}
				//}
				//部分退款则先退款再 重新下单
				Long[] orderIds = new Long[1];
				orderIds[0] = hyOrder.getId();
				if(isPartRefund) {
					json = insuranceOrderService.cancelOrder(orderIds);
					Map<String, Object> map2 = (Map<String, Object>)json.getObj();
					//List<HyOrderItem> orderItems = hyOrder.getOrderItems();
					List<Object> cancelSuccessIds = (List<Object>)map2.get("cancelSuccessIds"); 
					//退保成功
					if(cancelSuccessIds.size() >= 1) {
						//删除退款人的orderItem 之后重新下单
						
						//change in 2018/11/19 不在删除，而是将status置位1标识
//						for(HyOrderApplicationItem hyOrderApplicationItem : application.getHyOrderApplicationItems()) {
//							//删除
//							hyOrderItemService.delete(hyOrderApplicationItem.getItemId());
//							for(int i = 0; i < orderItems.size(); i++) {
//								if(orderItems.get(i).getId().equals(hyOrderApplicationItem.getItemId())) {
//									orderItems.remove(i);
//									i--;
//								}
//							}
//						}
//						hyOrder.setOrderItems(orderItems);
						
						
						//退款状态改为部分退款 不能再撤保
						//hyOrder.setRefundstatus(3);
						
						//重新下单到保险订单表
						insuranceOrderService.generateStoreInsuranceOrder(hyOrder, session);
						
						hyOrder.setRefundstatus(3);
						
						//下单到江泰
						json = insuranceOrderService.postOrderToJT(orderIds);
						Map<String,Object> map1 = (Map<String,Object>)json.getObj();
						List<Map<String,Object>> list1 = (List<Map<String,Object>>) (map.get("successIds"));
						//下单到江泰失败
						if(list1.size() < 1) {
							json.setSuccess(false);
							json.setMsg("重新下单到江泰失败： ");
							return json;
						}
						//支付
						json = hyOrderService.addInsuranceOrderPayment(hyOrder.getId(), session);
						hyOrderService.update(hyOrder);
					}
					else {
						return json;
					}
				}
				else {
					//全部退款
					json = insuranceOrderService.cancelOrder(orderIds);
					if(json.getObj() == null) {
						return json;
					}
				}
				
				
				// 门店退团退款审核成功，进行订单处理
				hyOrderApplicationService.handleBaoXianTuiKuan(application, isPartRefund);

				
				// write by wj
				application.setStatus(1); // 已退款
				
				Long orderId = application.getOrderId();
				Long storeId = hyOrder.getStoreId();
				Store store = storeService.find(storeId);
				
				
				//只有保险退款
				//写入退款记录   //预存款余额修改
				//BigDecimal tuiKuan = application.getBaoxianJiesuanMoney();
				BigDecimal tuiKuan = allRefund;
				RefundInfo refundInfo = new RefundInfo();
				refundInfo.setAmount(tuiKuan);
				refundInfo.setAppliName(application.getOperator().getName());
				refundInfo.setApplyDate(application.getCreatetime());
				Date date = new Date();
				refundInfo.setPayDate(date);
				refundInfo.setRemark("门店保险退款");
				refundInfo.setState(1);  //已付款
				refundInfo.setType(14);  //保险退款（游客退团，认为是一个类型）
				refundInfo.setOrderId(orderId);
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

					List<Filter> filters = new ArrayList<>();
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
					storeAccountLog.setType(13);
					storeAccountLog.setProfile("保险退款");
					storeAccountLogService.update(storeAccountLog);
					
					// 5、修改 总公司-财务中心-门店预存款表
					StorePreSave storePreSave = new StorePreSave();
					storePreSave.setStoreName(store.getStoreName());
					storePreSave.setStoreId(store.getId());
					storePreSave.setType(19); ////1:门店充值 2:报名退款  3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵 9:签证退款 10:酒店/酒加景销售 11:酒店/酒加景退款 12:门店后返 13:供应商驳回订单 14:
					//19保险退款
					storePreSave.setDate(date);
					storePreSave.setAmount(tuiKuan);
					storePreSave.setOrderCode(hyOrder.getOrderNumber());
					storePreSave.setOrderId(orderId);
					storePreSave.setPreSaveBalance(storeAccountService.findList(null, filters, null).get(0).getBalance());
					storePreSaveService.save(storePreSave);

				}
				
				
				
				
				
				//部分退款则先退款再 重新下单
//				if(isPartRefund) {
//					json = insuranceOrderService.cancelOrder(orderIds);
//					Map<String, Object> map2 = (Map<String, Object>)json.getObj();
//					//List<HyOrderItem> orderItems = hyOrder.getOrderItems();
//					List<Object> cancelSuccessIds = (List<Object>)map2.get("cancelSuccessIds"); 
//					//退保成功
//					if(cancelSuccessIds.size() >= 1) {
//						//删除退款人的orderItem 之后重新下单
//						
//						//change in 2018/11/19 不在删除，而是将status置位1标识
////						for(HyOrderApplicationItem hyOrderApplicationItem : application.getHyOrderApplicationItems()) {
////							//删除
////							hyOrderItemService.delete(hyOrderApplicationItem.getItemId());
////							for(int i = 0; i < orderItems.size(); i++) {
////								if(orderItems.get(i).getId().equals(hyOrderApplicationItem.getItemId())) {
////									orderItems.remove(i);
////									i--;
////								}
////							}
////						}
////						hyOrder.setOrderItems(orderItems);
//						
//						
//						//退款状态改为部分退款 不能再撤保
//						//hyOrder.setRefundstatus(3);
//						
//						//重新下单到保险订单表
//						insuranceOrderService.generateStoreInsuranceOrder(hyOrder, session);
//						
//						hyOrder.setRefundstatus(3);
//						
//						//下单到江泰
//						json = insuranceOrderService.postOrderToJT(orderIds);
//						Map<String,Object> map1 = (Map<String,Object>)json.getObj();
//						List<Map<String,Object>> list1 = (List<Map<String,Object>>) (map.get("successIds"));
//						//下单到江泰失败
//						if(list.size() < 1) {
//							json.setSuccess(false);
//							json.setMsg("重新下单到江泰失败： ");
//							return json;
//						}
//						//支付
//						json = hyOrderService.addInsuranceOrderPayment(hyOrder.getId(), session);
//						hyOrderService.update(hyOrder);
//					}
//					else {
//						return json;
//					}
//				}
//				else {
//					//全部退款
//					json = insuranceOrderService.cancelOrder(orderIds);
//					if(json.getObj() == null) {
//						return json;
//					}
//				}
				
				

			} else {
				map.put("msg", "false");
				application.setStatus(2); // 已驳回
				application.setView(remark);
				HyOrder order = hyOrderService.find(application.getOrderId());
				order.setRefundstatus(4); // 退款已驳回
				//order.setStatus(5);
				hyOrderService.update(order);

			}
			Authentication.setAuthenticatedUserId(username);
//			taskService.addComment(task.getId(), processInstanceId,
//					(comment == null ? "审核通过" : comment) + ":" + auditStatus);
//			taskService.complete(task.getId(), map);
			hyOrderApplicationService.update(application);
			json.setSuccess(true);
			json.setMsg("审核成功");
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;
	}
}
