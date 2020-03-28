package com.hongyu.controller;

import com.hongyu.*;
import com.hongyu.entity.*;
import com.hongyu.service.*;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.persistence.criteria.Expression;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/admin/store_confirm_hyorder")
public class StoreConfirmHyOrderController {

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

	@Autowired
	private HyVisaService hyVisaService;

	@Resource(name = "fddContractServiceImpl")
	FddContractService fddContractService;
	@RequestMapping(value = "hyorders",method = GET)
	public Json list(Pageable pageable, @DateTimeFormat(pattern="yyyy-MM-dd") Date startTime,
	                 @DateTimeFormat(pattern="yyyy-MM-dd") Date endTime, HttpSession session,
	                 HttpServletRequest request,  Integer checkstatus,
	                   String orderNumber, String name ) {
		Json json = new Json();
		try {
//			HashMap<String, Object> hm = new HashMap<String, Object>();
//			List<HashMap<String, Object>> result = new ArrayList<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			//找到账号所属门店
			Store store = storeService.findStore(admin);




			HyOrder hyOrder = new HyOrder();
			hyOrder.setCheckstatus(checkstatus);
//			hyOrder.setRefundstatus(refundstatus);
			//hyOrder.setSource(source);
			hyOrder.setOrderNumber(orderNumber);
			hyOrder.setName(name);
			hyOrder.setStoreId(store.getId());
			//等待门店确认
			if(checkstatus!=null && checkstatus==0){
				hyOrder.setStatus(Constants.HY_ORDER_STATUS_WAIT_STORE_CONFIRM);
			}


			//hyOrder.setStatus(status);

			/**
			 * 获取用户权限范围
			 */
			HyRoleAuthority.CheckedOperation co = (HyRoleAuthority.CheckedOperation) request.getAttribute("co");
			Set<HyAdmin> hyAdmins= AuthorityUtils.getAdmins(session, request);

			List<Filter> filters = new LinkedList<>();

			//订单类型1线路，3酒店，4门票，5酒+景，7签证

			Collection<Integer>  list = new ArrayList<>();
			Collections.addAll(list,1,3,4,5,7);
			filters.add(Filter.in("type", list));
//			filters.add(Filter.in("operator", hyAdmins));
			//订单来源1官网不选门店，2官网选择门店
			filters.add(Filter.eq("source",2));
			if(startTime != null) {
				filters.add(Filter.ge("createtime", startTime));
			}
			if(endTime != null) {
				filters.add(Filter.le("createtime", endTime));
			}

//			if(status!=null) {
//				switch(status) {
//					case 0:{
//						filters.add(Filter.lt("status", 3));
//					}break;
//					case 1:{
//						filters.add(Filter.eq("status", 3));
//					}break;
//					case 2:{
//						filters.add(Filter.gt("status", 3));
//						filters.add(Filter.lt("status", 6));
//					}break;
//					case 3:{
//						filters.add(Filter.eq("status", 6));
//					}break;
//					default:break;
//				}
//				hyOrder.setCheckstatus(null);
//			}
//			if(contractStatus!=null){
//				if(contractStatus==1){
//					filters.add(Filter.isNotNull("contractId"));
//				}else{
//					filters.add(Filter.isNull("contractId"));
//				}
//			}


			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createtime"));
			pageable.setOrders(orders);
			pageable.setFilters(filters);
			Page<HyOrder> page = hyOrderService.findPage(pageable, hyOrder);
			List<Map<String, Object>> result = new LinkedList<>();
			for (HyOrder tmp : page.getRows()) {
				Map<String, Object> map = new HashMap<>();
				HyAdmin operator = tmp.getOperator();
				map.put("id", tmp.getId());
				map.put("status", tmp.getStatus());
				map.put("orderNumber", tmp.getOrderNumber());
				map.put("name", tmp.getName());
				map.put("jiusuanMoney", tmp.getJiusuanMoney());
				map.put("jiesuanMoney1", tmp.getJiesuanMoney1());
				map.put("type",tmp.getType());
				map.put("createtime", tmp.getCreatetime());
				map.put("source",tmp.getSource());

				map.put("checkstatus",tmp.getCheckstatus());


				if(tmp.getCheckstatus()!=0 || tmp.getStatus()!=Constants.HY_ORDER_STATUS_WAIT_STORE_CONFIRM){
					map.put("privilege","view");
				}else if (admin.equals(operator)) {
					if (co == HyRoleAuthority.CheckedOperation.view) {
						map.put("privilege", "view");
					} else {
						map.put("privilege", "edit");
					}
				} else {
					if (co == HyRoleAuthority.CheckedOperation.edit) {
						map.put("privilege", "edit");
					} else {
						map.put("privilege", "view");
					}
				}

				Long groupId = tmp.getGroupId();
				if(groupId!=null){
					HyGroup hyGroup = hyGroupService.find(groupId);
					if(hyGroup==null)
						throw new Exception("线路订单中id为"+tmp.getId()+"中的团id"+groupId+" 对应的团为null");
					map.put("linePn", hyGroup.getGroupLinePn());
					HyLine hyLine = hyGroup.getLine();
					map.put("provider", hyLine.getHySupplier());	//供应商信息
				}

				//签证订单供应商
				if(tmp.getType()==7){
					List<HyOrderItem> items = tmp.getOrderItems();
					if(items!=null && items.size()>0){
						HyOrderItem item = items.get(0);
						HyVisa hyVisa = hyVisaService.find(item.getProductId());
						HySupplier hyTicketSupplier = hyVisa.getTicketSupplier();
						map.put("provider",hyTicketSupplier);
					}
				}

				if(tmp.getSupplier()!=null){
					map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
					map.put("supplierName", tmp.getSupplier().getName());
				}


				Long storeId = tmp.getStoreId();
				if(storeId!=null){
					Store store1 = storeService.find(storeId);
					map.put("storeName", store1==null?"":store1.getStoreName());
				}else{
					map.put("storeName", "");
				}

				result.add(map);
			}
			Map<String, Object> hMap = new HashMap<>();
			hMap.put("pageNumber", page.getPageNumber());
			hMap.put("pageSize", page.getPageSize());
			hMap.put("total", page.getTotal());
			hMap.put("rows", result);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询错误： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("hyorder")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			HyOrder tmp = hyOrderService.find(id);
			Map<String, Object> map = new HashMap<>();
			map.put("id", tmp.getId());
			map.put("orderNumber", tmp.getOrderNumber());
			map.put("createtime", tmp.getCreatetime());
			map.put("name", tmp.getName());
			map.put("paystatus", tmp.getPaystatus());
			map.put("checkstatus", tmp.getCheckstatus());
			map.put("refundstatus", tmp.getRefundstatus());
			map.put("status", tmp.getStatus());
			map.put("source", tmp.getSource());
			map.put("type", tmp.getType());
			map.put("discountedPrice", tmp.getDiscountedPrice());
			map.put("jiesuanMoney1", tmp.getJiesuanMoney1());
			map.put("jiusuanMoney", tmp.getJiusuanMoney());
			map.put("adjustMoney", tmp.getAdjustMoney());
			map.put("waimaiMoney", tmp.getWaimaiMoney());
			map.put("jiesuanTuikuan", tmp.getJiesuanTuikuan());
			map.put("waimaiTuikuan", tmp.getWaimaiTuikuan());
			map.put("baoxianJiesuanTuikuan", tmp.getBaoxianJiesuanTuikuan());
			map.put("baoxianWaimaiTuikuan", tmp.getBaoxianWaimaiTuikuan());
			map.put("storeFanLi", tmp.getStoreFanLi());
			map.put("tip", tmp.getTip());
			map.put("contact", tmp.getContact());
			map.put("contactIdNumber", tmp.getContactIdNumber());
			map.put("phone", tmp.getPhone());
			map.put("remark", tmp.getRemark());

			map.put("people", tmp.getPeople());

			map.put("koudianMethod", tmp.getKoudianMethod());
			map.put("proportion", tmp.getProportion());
			map.put("headProportion", tmp.getHeadProportion());
			map.put("koudianMoney", tmp.getKoudianMoney());
			Long storeId = tmp.getStoreId();
			if(storeId!=null){
				Store store = storeService.find(storeId);
				map.put("storeName", store==null?"":store.getStoreName());
			}else{
				map.put("storeName", "");
			}

			Long groupId = tmp.getGroupId();
			if(groupId!=null){
				HyGroup hyGroup = hyGroupService.find(groupId);
				if(hyGroup==null)
					throw new Exception("线路订单中id为"+tmp.getId()+"中的团id"+groupId+" 对应的团为null");
				map.put("linePn", hyGroup.getGroupLinePn());
				HyLine hyLine = hyGroup.getLine();
				map.put("provider", hyLine.getHySupplier());	//供应商信息
			}
			if(tmp.getSupplier()!=null){
				map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
				map.put("supplierName", tmp.getSupplier().getName());
			}
//			map.put("guideCheckStatus", tmp.getGuideCheckStatus());
//			map.put("storeType", tmp.getStoreType());
//			map.put("storeId", tmp.getStoreId());


//			map.put("operator", tmp.getOperator());
//			map.put("creatorId", tmp.getCreatorId());
//			map.put("discountedType", tmp.getDiscountedType());
//			map.put("discountedId", tmp.getDiscountedId());
//			map.put("ifjiesuan", tmp.getIfjiesuan());
//			map.put("insuranceOrderDownloadUrl", tmp.getInsuranceOrderDownloadUrl());
//			map.put("departure", tmp.getDeparture());
//			map.put("fatuandate", tmp.getFatuandate());
//			map.put("tianshu", tmp.getTianshu());
//			map.put("huituanxinxi", tmp.getHuituanxinxi());
//			map.put("xianlumingcheng", tmp.getXianlumingcheng());
//			map.put("xianlutype", tmp.getXianlutype());
//			map.put("fuwutype", tmp.getFuwutype());
//			map.put("xingchenggaiyao", tmp.getXingchenggaiyao());
//			map.put("tipInstruction", tmp.getTipInstruction());
//			map.put("contractId", tmp.getContractId());
//			map.put("contractNumber", tmp.getContractNumber());
//			map.put("contractType", tmp.getContractType());
//			map.put("modifytime", tmp.getModifytime());
//			map.put("orderItems", tmp.getOrderItems());
//			map.put("groupId", tmp.getGroupId());
//			map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
//			map.put("isDivideStatistic", tmp.getIsDivideStatistic());
//
//			//查找额外保险信息
//			Insurance insurance = insuranceService.getExtraInsuranceOfOrder(tmp);
//			if(insurance!=null){
//
//				Integer days = tmp.getTianshu();
//
//				map.put("insurance",insurance.getRemark());
//
//				for(InsurancePrice price:insurance.getInsurancePrices()){
//					if(days.compareTo(price.getStartDay())>=0&&days.compareTo(price.getEndDay())<=0){
//						map.put("insuranceMoney", price.getSalePrice());
//						break;
//					}
//				}
//				if(!map.containsKey("insuranceMoney")){
//					throw new Exception("获取保险价格失败");
//				}
//			}
//			Long groupId = tmp.getGroupId();
//			if(groupId!=null){
//				HyGroup hyGroup = hyGroupService.find(groupId);
//				map.put("linePn", hyGroup.getGroupLinePn());
//				HyLine hyLine = hyGroup.getLine();
//				map.put("provider", hyLine.getHySupplier());	//供应商信息
//				map.put("rebate", hyGroup.getFanliMoney()==null?0:hyGroup.getFanliMoney());
//			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	// 门店确认订单
	@RequestMapping(value = "confirm")
	@ResponseBody
	public Json storeConfirm(Long id, String view, Integer status, HttpSession session) {
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}
			if (!order.getStatus().equals(Constants.HY_ORDER_STATUS_WAIT_STORE_CONFIRM)) {
				throw new Exception("订单状态不对");
			}

			if (status.equals(0)) {
				if (view == null || view.equals("")) {
					throw new Exception("驳回意见必填");
				}
				order.setStatus(Constants.HY_ORDER_STATUS_REJECT_WAIT_FINANCE);
				order.setCheckstatus(Constants.HY_ORDER_CHECK_STATUS_REJECT);

				// 开启驳回审核流程
				Map<String, Object> map = new HashMap<>();
				map.put("person", "store");

				ProcessInstance pi = runtimeService.startProcessInstanceByKey("suppilerDismissOrder", map);
				// 根据流程实例id查询任务
				Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), pi.getProcessInstanceId(), view + ":1");
				taskService.complete(task.getId());

				SupplierDismissOrderApply supplierDismissOrderApply = new SupplierDismissOrderApply();
				supplierDismissOrderApply.setOrderId(order.getId());
				supplierDismissOrderApply.setCreateTime(new Date());
				supplierDismissOrderApply.setOperator(admin);
				supplierDismissOrderApply.setStatus(0); // 待审核
				supplierDismissOrderApply.setType(0); // 门店驳回申请
				supplierDismissOrderApply.setProcessInstanceId(pi.getProcessInstanceId());
				supplierDismissOrderApply.setMoney(order.getJiusuanMoney()); // 应退给门店的金额
				System.out.println(order.getJiusuanMoney());
				supplierDismissOrderApplyService.save(supplierDismissOrderApply);

				/* add by liyang,change the insurance order status */
//				List<Filter> insurancefilters = new ArrayList<>();
//				insurancefilters.add(Filter.eq("orderId", id));
//				List<InsuranceOrder> insuranceOrders = insuranceOrderService.findList(null,insurancefilters,null);
//				if(!insuranceOrders.isEmpty()){
//					for(InsuranceOrder tmp:insuranceOrders){
//						//将保险状态设置为已取消状态
//						tmp.setStatus(2);
//						insuranceOrderService.update(tmp);
//					}
//				}

			} else {
				order.setStatus(Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM);
				order.setCheckstatus(Constants.HY_ORDER_CHECK_STATUS_ACCEPT);
			}

			HyOrderApplication application = new HyOrderApplication();
			application.setContent("门店确认订单");
			application.setView(view);
			application.setStatus(status);
			application.setOrderId(id);
			application.setCreatetime(new Date());
			application.setOperator(admin);
			application.setType(HyOrderApplication.STORE_CONFIRM_ORDER);
			hyOrderApplicationService.save(application);

			hyOrderService.update(order);
			json.setSuccess(true);
			json.setMsg("门店确认成功");
			json.setObj(null);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("门店确认失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;

	}

}
