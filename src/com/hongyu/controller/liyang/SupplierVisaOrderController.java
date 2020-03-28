package com.hongyu.controller.liyang;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyReceiptRefund;
import com.hongyu.entity.HyVisa;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.Store;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyOrderApplicationItemService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyReceiptRefundService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HyVisaService;
import com.hongyu.service.PayServicerService;
import com.hongyu.service.PayablesLineItemService;
import com.hongyu.service.PayablesLineService;
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
import com.hongyu.service.SupplierDismissOrderApplyService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.hongyu.util.SendMessageEMY;
import com.hongyu.util.liyang.CountryUtil;
/**
 * 供应商签证订单管理中心
 * @author liyang
 * @version 2019年5月28日 下午5:39:56
 */
@Controller
@RequestMapping("/admin/SupplierVisaOrder/")
public class SupplierVisaOrderController {
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

	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;

	@Resource(name = "supplierDismissOrderApplyServiceImpl")
	SupplierDismissOrderApplyService supplierDismissOrderApplyService;
	
	@Resource(name = "paymentSupplierServiceImpl")
	PaymentSupplierService paymentSupplierService;

	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;
	
	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	
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
	
	@Resource(name=  "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "hyVisaServiceImpl")
	HyVisaService hyVisaService;
	
	@Resource(name = "piaowuConfirmServiceImpl")
	private PiaowuConfirmService piaowuConfirmService;
	
	
	/**
	 * 供应商查看订单列表
	 * @param name
	 * @param orderNumber
	 * @param payStatus
	 * @param confirmStatus
	 * @param refundStatus
	 * @param continentId
	 * @param pageable
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("listForProvider/view")
	@ResponseBody
	public Json listForProvider(String name,String orderNumber,Integer payStatus,
			Integer confirmStatus,Integer refundStatus,
			Integer continentId,Pageable pageable,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date leftCreateTime,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date rightCreateTime,
			HttpSession session,HttpServletRequest request){
		Json json = new Json();
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			/**
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
			
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("type", 7));
			filters.add(Filter.in("supplier", hyAdmins));
			if (payStatus != null) {
				filters.add(Filter.eq("paystatus",payStatus));
			}
			if(confirmStatus != null){
				filters.add(Filter.eq("checkstatus",confirmStatus));
			}
			if(refundStatus != null){
				filters.add(Filter.eq("refundstatus",refundStatus));
			}
			if(orderNumber != null){
				filters.add(Filter.like("orderNumber",orderNumber));
			}
			if(name != null){
				filters.add(Filter.like("name",name));
			}
			if(continentId != null){
				filters.add(Filter.eq("xianlumingcheng", CountryUtil.getContinent(continentId)));
			}
			Date lDate = new Date();
			if (leftCreateTime != null){
				lDate = DateUtil.getStartOfDay(leftCreateTime);
			}else{
				lDate = DateUtil.getStartOfDay(lDate);
			}
			Date rDate = new Date();
			if(rightCreateTime != null){
				rDate = DateUtil.getEndOfDay(rightCreateTime);
			}else{
				rDate = DateUtil.getEndOfDay(rDate);
			}
			filters.add(Filter.ge("createtime", lDate));
			filters.add(Filter.le("createtime", rDate));
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createtime"));

			List<HyOrder> hyOrders = hyOrderService.findList(null, filters,orders);
			List<Map<String, Object>> result = new LinkedList<>();
			for (HyOrder tmp : hyOrders) {
				Map<String, Object> map = new HashMap<>();
				HyAdmin operator = tmp.getOperator();
				if(tmp.getOrderItems()!=null && tmp.getOrderItems().size()>0){
					HyVisa hyVisa = hyVisaService.find(tmp.getOrderItems().get(0).getProductId());
					map.put("productId", hyVisa.getProductId());
					//map.put("productName", hyVisa.getProductName());
				}
				
				map.put("id", tmp.getId());
				map.put("status", tmp.getStatus());
				map.put("orderNumber", tmp.getOrderNumber());
				map.put("name", tmp.getName());
				map.put("source", tmp.getSource());
				map.put("continent", tmp.getXianlumingcheng());
				map.put("country", tmp.getXingchenggaiyao());
				map.put("fatuanDate", tmp.getFatuandate());
				map.put("discountPrice", tmp.getDiscountedPrice());
				map.put("jiusuanMoney", tmp.getJiusuanMoney());
				map.put("jiesuanMoney1", tmp.getJiesuanMoney1());
				int source1 = tmp.getSource();
				if(source1 != Constants.mendian){
					map.put("storeName", "");
				}else{
					Long storeId = tmp.getStoreId();
					Store store = storeService.find(storeId);
					map.put("storeName", store==null?"":store.getStoreName());
				}
				map.put("people", tmp.getPeople());
				
				map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
				
				map.put("createtime", tmp.getCreatetime());
				if (operator.equals(admin)) {
					if (co == CheckedOperation.view) {
						map.put("privilege", "view");
					} else {
						map.put("privilege", "edit");
					}
				} else {
					if (co == CheckedOperation.edit) {
						map.put("privilege", "edit");
					} else {
						map.put("privilege", "view");
					}
				}
				if(tmp.getSupplier()!=null)
					map.put("supplierName", tmp.getSupplier().getName());			
				result.add(map);
			}
			Map<String, Object> hMap = new HashMap<>();
			int page = pageable.getPage();
			int rows = pageable.getRows();
			hMap.put("pageNumber", page);
			hMap.put("pageSize", rows);
			hMap.put("total", result.size());
			hMap.put("rows", result.subList((page - 1) * rows, page * rows > result.size() ? result.size() : page * rows));
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hMap);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return json;
	}
	
	/**
	 * 供应商确认签证订单
	 * @param id
	 * @param view
	 * @param status
	 * @param session
	 * @return
	 */
	@RequestMapping("providerConfirm")
	@ResponseBody
	public Json providerConfirm(Long id, String view, Integer status, HttpSession session) {
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
			if (!order.getStatus().equals(Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)) {
				throw new Exception("订单状态不对");
			}

			if (status.equals(0)) { // 如果供应商驳回
				if (view == null || view.equals("")) {
					throw new Exception("驳回意见必填");
				}
				supplierDismissOrderApplyService.addSupplierDismissOrderSubmit(id, view, session);
				//add by wj 2019-07-07  添加短信提示  供应商驳回订单
				String phone = null;
				Long storeId = order.getStoreId();
				if(storeId!=null){
					phone = storeService.find(storeId).getHyAdmin().getMobile();
				}
				SendMessageEMY.sendMessage(phone,"",20);
			} else {
				order.setStatus(Constants.HY_ORDER_STATUS_PROVIDER_ACCEPT);
				/*welcome*/
				boolean isConfirm = piaowuConfirmService.orderPiaowuConfirm(id, 5, session);
				System.out.println(isConfirm);
            }

			HyOrderApplication application = new HyOrderApplication();
			application.setContent("供应商确认订单");
			application.setView(view);
			application.setStatus(status);
			application.setOrderId(id);
			application.setCreatetime(new Date());
			application.setOperator(admin);
			application.setType(HyOrderApplication.PROVIDER_CONFIRM_ORDER); // 供应商确认订单
			hyOrderApplicationService.save(application);
			hyOrderService.update(order);				
			json.setSuccess(true);
			json.setMsg("供应商确认成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("供应商确认失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 订单详情
	 * @param id
	 * @return
	 */
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json = new Json();
		try {
			HyOrder tmp = hyOrderService.find(id);
			if(tmp==null)
				throw new NullPointerException("找不到该订单");
			Map<String, Object> map = new HashMap<>();	
			map.put("id", tmp.getId());
			map.put("orderNumber", tmp.getOrderNumber());
			map.put("name", tmp.getName());
			map.put("status", tmp.getStatus());
			map.put("paystatus", tmp.getPaystatus());
			map.put("checkstatus", tmp.getCheckstatus());
			map.put("refundstatus", tmp.getRefundstatus());
			map.put("type", tmp.getType());
			map.put("source", tmp.getSource());
			map.put("people", tmp.getPeople());
			map.put("storeType", tmp.getStoreType());
			map.put("storeId", tmp.getStoreId());
			Long storeId = tmp.getStoreId();
			if(storeId!=null){
				Store store = storeService.find(storeId);
				map.put("storeName", store==null?"":store.getStoreName());
			}else{
				map.put("storeName", "");
			}
			map.put("operator", tmp.getOperator());
			map.put("creatorId", tmp.getCreatorId());
			map.put("adjustMoney", tmp.getAdjustMoney());
			map.put("discountedType", tmp.getDiscountedType());
			map.put("discountedId", tmp.getDiscountedId());
			map.put("discountedPrice", tmp.getDiscountedPrice());
			map.put("jiesuanMoney1", tmp.getJiesuanMoney1());
			map.put("jiusuanMoney", tmp.getJiusuanMoney());
			map.put("waimaiMoney", tmp.getWaimaiMoney());
			map.put("jiesuanTuikuan", tmp.getJiesuanTuikuan());
			map.put("waimaiTuikuan", tmp.getWaimaiTuikuan());
			map.put("baoxianJiesuanTuikuan", tmp.getBaoxianJiesuanTuikuan());
			map.put("baoxianWaimaiTuikuan", tmp.getBaoxianWaimaiTuikuan());
			map.put("ifjiesuan", tmp.getIfjiesuan());
			map.put("koudianMethod", tmp.getKoudianMethod());
			map.put("proportion", tmp.getProportion());
			map.put("headProportion", tmp.getHeadProportion());
			map.put("koudianMoney", tmp.getKoudianMoney());
			map.put("fatuandate", tmp.getFatuandate());
			map.put("continent", tmp.getXianlumingcheng());
			map.put("country", tmp.getXingchenggaiyao());
			map.put("contact", tmp.getContact());
			map.put("contactIdNumber", tmp.getContactIdNumber());
			map.put("phone", tmp.getPhone());
			map.put("remark", tmp.getRemark());;
			map.put("createtime", tmp.getCreatetime());
			map.put("modifytime", tmp.getModifytime());
			
			map.put("orderItems", tmp.getOrderItems());
			map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
			map.put("isDivideStatistic", tmp.getIsDivideStatistic());
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 订单条目-商品信息
	 * @param id
	 * @param pageable
	 * @return
	 */
	@RequestMapping("orderItemList/view")
	@ResponseBody
	public Json orderItemList(Long id,Pageable pageable){
		Json json = new Json();
		try {
			HyOrder hyOrder = hyOrderService.find(id);
			List<Map<String, Object>> result = new LinkedList<>();
			for (HyOrderItem tmp : hyOrder.getOrderItems()) {
				Map<String, Object> map = new HashMap<>();
				map.put("id", tmp.getId());
				HyVisa visa = hyVisaService.find(tmp.getProductId());
				if(visa!=null){
					map.put("productId", visa.getProductId());
					map.put("visaType", visa.getVisaType());
				}else{
					map.put("productId", "");
					map.put("visaType", null);
				}
				map.put("name", tmp.getName());
				map.put("type", tmp.getType());
				map.put("number", tmp.getName());
				map.put("numberOfReturn", tmp.getNumberOfReturn());
				map.put("jiesuanPrice", tmp.getJiesuanPrice());
				map.put("waimaiPrice", tmp.getWaimaiPrice());
				result.add(map);
			}
			int pageNumber = pageable.getPage();
			int pageSize = pageable.getRows();
			Map<String, Object> hMap = new HashMap<>();
			hMap.put("pageNumber", pageNumber);
			hMap.put("pageSize", pageSize);
			hMap.put("total", result.size());
			hMap.put("rows", result.subList((pageNumber - 1) * pageSize,
					pageNumber * pageSize > result.size() ? result.size() : pageNumber * pageSize));
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 游客信息列表
	 * @param id
	 * @param pageable
	 * @return
	 */
	@RequestMapping("customerList/view")
	@ResponseBody
	public Json customerList(Long id,Pageable pageable){
		Json json = new Json();
		try {
			HyOrder hyOrder = hyOrderService.find(id);
			List<HyOrderCustomer> result = new LinkedList<>();
			for (HyOrderItem tmp : hyOrder.getOrderItems()) {
				if (tmp.getHyOrderCustomers() != null && tmp.getHyOrderCustomers().size() > 0) {
					result.addAll(tmp.getHyOrderCustomers());
				}
			}
			int pg = pageable.getPage();
			int rows = pageable.getRows();
			Map<String, Object> map = new HashMap<>();
			map.put("total", result.size());
			map.put("pageNumber", pg);
			map.put("pageSize", rows);
			map.put("rows", result.subList((pg - 1) * rows, pg * rows > result.size() ? result.size() : pg * rows));
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 收退款记录
	 * @param id
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "payAndRefundList/view")
	@ResponseBody
	public Json payAndRefundList(Long id, Integer type) {
		Json json = new Json();
		try {

			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}

			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderId", id));
			filters.add(Filter.eq("type", type));

			List<PayandrefundRecord> records = payandrefundRecordService.findList(null, filters, null);
			for(PayandrefundRecord pr: records){
				Long orderId = pr.getOrderId();
				HyOrder hyOrder = hyOrderService.find(orderId);
				pr.setMoney(hyOrder.getJiesuanMoney1());
				
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(records);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
		}
		return json;
	}
	/**
	 * 实收付款记录列表
	 * @param id
	 * @param type
	 * @return
	 */
	@RequestMapping("receiptAndRefundList/view")
	@ResponseBody
	public Json receiptAndRefundList(Long id,Integer type){
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}

			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("order", order));
			filters.add(Filter.eq("type", type));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createTime"));
			List<HyReceiptRefund> receiptRefunds = hyReceiptRefundService.findList(null, filters, orders);

			json.setSuccess(true);
			json.setMsg("查找成功");
			json.setObj(receiptRefunds);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 订单日志
	 * @param id
	 * @param pageable
	 * @return
	 */
	@RequestMapping("orderApplications/view")
	@ResponseBody
	public Json orderApplications(Long id,Pageable pageable){
		Json json = new Json();
		try {
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("orderId", id));
			pageable.setFilters(filters);
			List<Order> orders = new LinkedList<>();

			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<HyOrderApplication> page = hyOrderApplicationService.findPage(pageable);
			List<Map<String, Object>> result = new LinkedList<>();
			
			for(HyOrderApplication hyOrderApplication : page.getRows()) {
				Map<String, Object> map = new HashMap<>();
				map.put("baoxianJiesuanMoney", hyOrderApplication.getBaoxianJiesuanMoney());
				map.put("baoxianWaimaiMoney", hyOrderApplication.getBaoxianWaimaiMoney());
				map.put("content", hyOrderApplication.getContent());
				map.put("createtime", hyOrderApplication.getCreatetime());
				map.put("id", hyOrderApplication.getId());
				map.put("isSubstatis", hyOrderApplication.getIsSubStatis());
				map.put("jiesuanMoney", hyOrderApplication.getJiesuanMoney());
				if(hyOrderApplication.getOperator()!=null)
					map.put("operator", hyOrderApplication.getOperator().getName());
				else
					map.put("operator","");
				map.put("orderId", hyOrderApplication.getOrderId());
				map.put("orderNumber", hyOrderApplication.getOrderNumber());
				map.put("outcome", hyOrderApplication.getOutcome());
				map.put("processInstanceId", hyOrderApplication.getProcessInstanceId());
				map.put("status", hyOrderApplication.getStatus());
				map.put("type", hyOrderApplication.getType());
				map.put("view", hyOrderApplication.getView());
				map.put("waimaiMoney", hyOrderApplication.getWaimaiMoney());
				map.put("hyOrderApplicationItems", hyOrderApplication.getHyOrderApplicationItems());			
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
		}
		return json;
	}
}
