package com.hongyu.controller.liyang;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.liyang.StoreVisaOrderController.MyOrderItem;
import com.hongyu.entity.BankList;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyReceiptRefund;
import com.hongyu.entity.HyVisa;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.ReceiptTotalServicer;
import com.hongyu.entity.Store;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
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
import com.hongyu.util.liyang.CountryUtil;
import com.hongyu.util.liyang.EmployeeUtil;
/**
 * 网络销售部签证订单管理中心
 * @author liyang
 * @version 2019年5月28日 下午5:40:33
 */
@Controller
@RequestMapping("/admin/mhVisaOrder/")
public class MhVisaOrderController {
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
	 * 网络销售部签证订单管理
	 * @param name
	 * @param orderNumber
	 * @param payStatus
	 * @param confirmStatus
	 * @param refundStatus
	 * @param source
	 * @param continentId
	 * @param leftCreateTime
	 * @param rightCreateTime
	 * @param pageable
	 * @param session
	 * @param request
	 * @param guestName
	 * @return
	 */
	@RequestMapping("listformh/view")
	@ResponseBody
	public Json list(String name,String orderNumber,Integer payStatus,
			Integer confirmStatus,Integer refundStatus,Integer source,
			Integer continentId,@DateTimeFormat(pattern="yyyy-MM-dd") Date leftCreateTime,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date rightCreateTime,Pageable pageable,
			HttpSession session,HttpServletRequest request,String guestName){
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
			if (payStatus != null) {
				filters.add(Filter.eq("paystatus",payStatus));
			}
			if(confirmStatus != null){
				filters.add(Filter.eq("checkstatus",confirmStatus));
			}
			if(refundStatus != null){
				filters.add(Filter.eq("refundstatus",refundStatus));
			}
			if(source != null && source >= 1 && source <= 2){
				filters.add(Filter.eq("source",source));
			}else {
				filters.add(Filter.ne("source", 0));
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
			filters.add(Filter.in("operator", hyAdmins));
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
				if(guestName!=null){
					boolean hasName = false;
					for(HyOrderItem item : tmp.getOrderItems()){
						for(HyOrderCustomer customer: item.getHyOrderCustomers()){
							if(customer.getName().contains(guestName)){
								hasName = true;
							}
							if(hasName)break;
						}
						if(hasName)break;
					}
					if(!hasName){
						continue;
					}
				}
				
				Map<String, Object> map = new HashMap<>();
				HyAdmin operator = tmp.getOperator();
				map.put("id", tmp.getId());
				map.put("status", tmp.getStatus());
				map.put("orderNumber", tmp.getOrderNumber());
				map.put("name", tmp.getName());
				map.put("source", tmp.getSource());
				map.put("continent", tmp.getXianlumingcheng());
				map.put("country", tmp.getXingchenggaiyao());
				int source1 = tmp.getSource();
				if(source1 != Constants.mendian){
					map.put("storeName", "");
				}else{
					Long storeId = tmp.getStoreId();
					Store store = storeService.find(storeId);
					map.put("storeName", store==null?"":store.getStoreName());
				}
				map.put("people", tmp.getPeople());
				
				//map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
				
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
				if(tmp.getSupplier()!=null){
					map.put("supplierName", tmp.getSupplier().getName());
				}
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
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
			json.setObj(null);
		}
		return json;
	}
//	/**
//	 * 网络销售部订单列表
//	 * @param name
//	 * @param orderNumber
//	 * @param payStatus
//	 * @param confirmStatus
//	 * @param refundStatus
//	 * @param continent
//	 * @param pageable
//	 * @param session
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping("listformh/view")
//	@ResponseBody
//	public Json listForMh(String name,String orderNumber,Integer payStatus,Integer confirmStatus,Integer refundStatus,
//			Integer continentId,Pageable pageable,HttpSession session,HttpServletRequest request){
//		Json json = new Json();
//		try {	
//			HyOrder hyOrder = new HyOrder();
//			hyOrder.setPaystatus(payStatus);
//			hyOrder.setCheckstatus(confirmStatus);
//			hyOrder.setRefundstatus(refundStatus);
//			hyOrder.setOrderNumber(orderNumber);
//			hyOrder.setName(name);
//			hyOrder.setXianlumingcheng(CountryUtil.getContinent(continentId));
////			/*只筛选官网不选门店的订单*/
////			hyOrder.setSource(1);
//			List<Filter> filters = new LinkedList<>();
//			filters.add(Filter.eq("type", 7));
//			/**要筛选官网不选门店和官网选门店两种*/
//			filters.add(Filter.ne("source", 0));
//			List<Order> orders = new ArrayList<>();
//			orders.add(Order.desc("createtime"));
//			pageable.setOrders(orders);
//			pageable.setFilters(filters);
//			Page<HyOrder> page = hyOrderService.findPage(pageable, hyOrder);
//			List<Map<String, Object>> result = new LinkedList<>();
//			for (HyOrder tmp : page.getRows()) {
//				Map<String, Object> map = new HashMap<>();
//				map.put("id", tmp.getId());
//				map.put("status", tmp.getStatus());
//				map.put("orderNumber", tmp.getOrderNumber());
//				map.put("name", tmp.getName());
//				map.put("source", tmp.getSource());
//				map.put("continent", tmp.getXianlumingcheng());
//				map.put("country", tmp.getXingchenggaiyao());
//				int source1 = tmp.getSource();
//				if(source1 != Constants.mendian){
//					map.put("storeName", "");
//				}else{
//					Long storeId = tmp.getStoreId();
//					Store store = storeService.find(storeId);
//					map.put("storeName", store==null?"":store.getStoreName());
//				}
//				map.put("people", tmp.getPeople());
//				
//				map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
//				
//				map.put("createtime", tmp.getCreatetime());
//				if(tmp.getSupplier()!=null)
//					map.put("supplierName", tmp.getSupplier().getName());			
//				result.add(map);
//			}
//			Map<String, Object> hMap = new HashMap<>();
//			hMap.put("pageNumber", page.getPageNumber());
//			hMap.put("pageSize", page.getPageSize());
//			hMap.put("total", page.getTotal());
//			hMap.put("rows", result);
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(hMap);
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("查询失败："+e.getMessage());
//			json.setObj(null);
//		}
//		return json;
//	}
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
			if(tmp.getSource()==0){
				map.put("jiesuanMoney1", tmp.getJiesuanMoney1());
				map.put("jiusuanMoney", tmp.getJiusuanMoney());
				map.put("waimaiMoney", tmp.getWaimaiMoney());
			}else{
				map.put("jiesuanMoney1", tmp.getWaimaiMoney());
				map.put("jiusuanMoney", tmp.getWaimaiMoney());
				map.put("waimaiMoney", tmp.getWaimaiMoney());
			}
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
			/**
			 * 暂时将官网订购签证的用户的收获地址存在huituanxinxi字段上
			 * 存储格式：省,市,区|收货人详细地址|姓名|电话
			 */
			String address = tmp.getHuituanxinxi();
			if (address != null && !"".equals(address)) {
				String[] datas = address.split("\\|");
				
				if (datas != null && datas.length == 4) {
					map.put("shrAddress", datas[0]+","+datas[1]);
					map.put("shrName", datas[2]);
					map.put("shrPhone", datas[3]);
				}			
			}
			map.put("orderItems", tmp.getOrderItems());
			map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
			map.put("isDivideStatistic", tmp.getIsDivideStatistic());
			if(tmp.getOrderItems()!=null && tmp.getOrderItems().size()>0){
				HyVisa visa = hyVisaService.find(tmp.getOrderItems().get(0).getProductId());
				map.put("supplierName", visa.getTicketSupplier().getSupplierName());
			}else{
				map.put("supplierName", "错误");
			}
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
	 * 获取门店类型
	 * @param session
	 * @return
	 */
	@RequestMapping("getStoreType/view")
	@ResponseBody
	public Json getStoreType(HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Store store = storeService.findStore(hyAdmin);
			if(store==null){
				json.setSuccess(false);
				json.setMsg("门店不存在");
			}else{
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(store.getStoreType());
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 门店退款（售前）订单条目列表
	 */
	@RequestMapping(value = "scg_list/view")
	@ResponseBody
	public Json scgList(Long id) {
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}
			//签证为全退，不需要计算退款比率
			//BigDecimal lineRefundPercentage = hyOrderService.getLineRefundPercentage(order).multiply(BigDecimal.valueOf(0.01));
			List<MyOrderItem> lists = new ArrayList<>();
			for (HyOrderItem item : order.getOrderItems()) {
				MyOrderItem myOrderItem = new MyOrderItem();
				myOrderItem.setItemId(item.getId());
				myOrderItem.setType(item.getType());
				myOrderItem.setPriceType(item.getPriceType());
				myOrderItem.setName(item.getName());
				myOrderItem.setCustomerName(item.getHyOrderCustomers().get(0).getName());
				myOrderItem.setNumber(item.getNumber());
				myOrderItem.setReturnNumber(item.getNumberOfReturn());
				myOrderItem.setJiesuanPrice(item.getJiesuanPrice());
				myOrderItem.setJiesuanRefund(item.getJiesuanPrice());
				myOrderItem.setWaimaiPrice(item.getWaimaiPrice());
				myOrderItem.setWaimaiRefund(item.getWaimaiPrice());
				myOrderItem.setBaoxianJiesuanPrice(hyOrderItemService.getBaoxianJiesuanPrice(item));
				myOrderItem.setBaoxianJiesuanRefund(myOrderItem.getBaoxianJiesuanPrice());
				myOrderItem.setBaoxianWaimaiPrice(hyOrderItemService.getBaoxianWaimaiPrice(item));
				myOrderItem.setBaoxianWaimaiRefund(myOrderItem.getBaoxianWaimaiPrice());
				lists.add(myOrderItem);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(lists);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 门店售后订单条目列表
	 */
	@RequestMapping(value = "scs_list/view")
	@ResponseBody
	public Json scsList(Long id) {
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}

			List<MyOrderItem> lists = new ArrayList<>();
			for (HyOrderItem item : order.getOrderItems()) {
				MyOrderItem myOrderItem = new MyOrderItem();
				myOrderItem.setItemId(item.getId());
				myOrderItem.setType(item.getType());
				myOrderItem.setPriceType(item.getPriceType());
				myOrderItem.setName(item.getName());
				myOrderItem.setNumber(item.getNumber());
				myOrderItem.setReturnNumber(item.getNumberOfReturn());
				myOrderItem.setJiesuanPrice(item.getJiesuanPrice());
				myOrderItem.setJiesuanRefund(BigDecimal.ZERO);
				myOrderItem.setWaimaiPrice(item.getWaimaiPrice());
				myOrderItem.setWaimaiRefund(BigDecimal.ZERO);
				myOrderItem.setBaoxianJiesuanPrice(hyOrderItemService.getBaoxianJiesuanPrice(item));
				myOrderItem.setBaoxianJiesuanRefund(BigDecimal.ZERO);
				myOrderItem.setBaoxianWaimaiPrice(hyOrderItemService.getBaoxianWaimaiPrice(item));
				myOrderItem.setBaoxianWaimaiRefund(BigDecimal.ZERO);

				if(!item.getHyOrderCustomers().isEmpty()){
					myOrderItem.setCustomerName(item.getHyOrderCustomers().get(0).getName());
				}
				lists.add(myOrderItem);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(lists);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	@Transactional
	@RequestMapping(value = "storeCancelVisa/apply", method = RequestMethod.POST)
	@ResponseBody
	public Json storeCancelVisaApply(@RequestBody HyOrderApplication application, HttpSession session) {
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(application.getOrderId());
			if (order == null) {
				throw new Exception("订单无效");
			}
			if(order.getIfjiesuan()==true) {
				json.setSuccess(false);
				json.setMsg("结算后不能售前退款");
				json.setObj(2);
				return json;
			}
			
			List<HyOrderItem> orderItems = order.getOrderItems();
			if(orderItems==null || orderItems.isEmpty()) {
				throw new Exception("没有有效订单条目");
			}
			HyOrderItem orderItem = orderItems.get(0);
			//HyTicketHotel hyTicketHotel = hyTicketHotelService.find(orderItem.getProductId());
			HyVisa hyVisa = hyVisaService.find(orderItem.getProductId());
			if(hyVisa==null) {
				throw new Exception("没有有效签证信息");
			}



			Map<String, Object> variables = new HashMap<>();
			/**找供应商需要注意*/
			//找出供应商
			HyAdmin provider = hyVisa.getCreator();
			// 指定审核供应商
			variables.put("provider", provider.getUsername());

			// 启动流程
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeTuiTuan", variables);
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();

			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), "发起申请:1");
			taskService.complete(task.getId(), variables);

			application.setContent("门店售前退款");
			application.setOperator(admin);
			application.setStatus(0); // 待供应商审核
			application.setCreatetime(new Date());
			application.setProcessInstanceId(task.getProcessInstanceId());
			application.setType(HyOrderApplication.STORE_CANCEL_GROUP);
			order.setRefundstatus(1); // 订单退款状态为退款中
			//
			hyOrderService.update(order);

			for (HyOrderApplicationItem item : application.getHyOrderApplicationItems()) {
				item.setHyOrderApplication(application);
			}

			hyOrderApplicationService.save(application);

			json.setSuccess(true);
			json.setMsg("门店售前退款申请成功");
			json.setObj(null);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("门店售前退款申请失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 门店售前退款列表
	 * @param pageable
	 * @param status
	 * @param providerName
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "storeCancelVisa/list/view")
	@ResponseBody
	public Json storeCancelVisaList(Pageable pageable, Integer status, String providerName, HttpSession session) {
		return hyOrderApplicationService.getApplicationList(pageable, status, providerName, session, 
				HyOrderApplication.STORE_CANCEL_GROUP,7);	//签证订单类型为7
	}
	/**
	 * 门店售后列表
	 * @param pageable
	 * @param status
	 * @param providerName
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "storeCustomerService/list/view")
	@ResponseBody
	public Json storeCustomerServiceList(Pageable pageable, Integer status, String providerName, HttpSession session) {
		return hyOrderApplicationService.getApplicationList(pageable, status, providerName, session,
				HyOrderApplication.STORE_CUSTOMER_SERVICE,7);	//签证订单类型为7
	}
	
	
	
	
	@RequestMapping(value = { "storeCancelVisa/detail/view", "storeCustomerService/detail/view" })
	@ResponseBody
	public Json storeCancelVisaDetail(Long id) {
		Json json = new Json();

		try {
			HyOrderApplication application = hyOrderApplicationService.find(id);
			if (application == null) {
				throw new Exception("没有有效的审核申请记录");
			}
			HyOrder order = hyOrderService.find(application.getOrderId());

			Map<String, Object> ans = new HashMap<>();

			/** 审核详情需要注意 */
			ans.put("application", hyOrderApplicationService.auditDetailHelper(application, application.getStatus()));
			/** 审核条目详情需要注意*/
			ans.put("applicationItems", hyOrderApplicationService.auditItemsHelper(application));

			/**
			 * 审核详情添加
			 */
			String processInstanceId = application.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);

			List<Map<String, Object>> auditList = new ArrayList<>();
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

				HyAdmin admin = hyAdminService.find(username);
				String name = "";
				if (admin != null) {
					name = admin.getName();
				}
				map.put("auditName", name);
				
				String fullMsg = comment.getFullMessage();
				
				String[] msgs = fullMsg.split(":");
				map.put("comment", msgs[0]);
				if (msgs[1].equals("0")) {
					map.put("result", "驳回");
				} else if (msgs[1].equals("1")) {
					map.put("result", "通过");
				}

				map.put("time", comment.getTime());

				auditList.add(map);
			}

			ans.put("auditRecords", auditList);

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
	 * 门店（售前）退款审核
	 * @param id
	 * @param comment
	 * @param auditStatus
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "storeCancelVisa/audit", method = RequestMethod.POST)
	@ResponseBody
	public Json storeCancelVisaAudit(Long id, String comment, Integer auditStatus, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyOrderApplication application = hyOrderApplicationService.find(id);
			String applyName = application.getOperator().getUsername(); // 找到提交申请的人
			String processInstanceId = application.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId.equals("")) {
				throw new Exception("审核出错，信息不完整，请重新申请");
			}

			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			HashMap<String, Object> map = new HashMap<>(); // 保存流转信息和流程变量信息
															// 下一阶段审核的部门

			if (auditStatus.equals(1)) { // 如果审核通过
				map.put("msg", "true");
				if (task.getTaskDefinitionKey().equals("usertask2")) { // 如果供应商
					// 设置下一阶段审核的部门 ---
					List<Filter> filters = new ArrayList<>();
					/**审核限额需要注意*/
					filters.add(Filter.eq("eduleixing", Eduleixing.storeTuiTuanLimit));
					List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
					BigDecimal money = edu.get(0).getMoney();
					//BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianJiesuanMoney());
					//签证不涉及到保险
					BigDecimal tuiKuan = application.getJiesuanMoney();
					if (tuiKuan.compareTo(money) > 0) { // 如果退款总额大于限额，
						map.put("money", "more"); // 设置需要品控中心限额审核
						application.setStatus(1); // 待品控限额审核
					} else { // 如果退款总额不大于限额
						map.put("money", "less"); // 设置财务审核
						application.setStatus(2); // 待财务审核
					}
				} else if (task.getTaskDefinitionKey().equals("usertask3")) { // 如果品控
					application.setStatus(2); // 待财务审核
				} else if (task.getTaskDefinitionKey().equals("usertask4")) {
	
					/**财务审核通过需要注意*/
					// 售前退款财务审核通过，进行订单处理
					hyOrderApplicationService.handleHyVisaScg(application);
					
					application.setStatus(4);//已退款
					//售前退款财务审核通过，请王劼同学添加相关操作
					piaowuConfirmService.piaowuRefund(application, username, 5, "门店签证售前退款");
					
				}

			} else {
				map.put("msg", "false");
				application.setStatus(5); // 已驳回
				HyOrder order = hyOrderService.find(application.getOrderId());
				order.setRefundstatus(4); // 退款已驳回
				hyOrderService.update(order);

			}
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), processInstanceId,
					(comment == null ? "审核通过" : comment) + ":" + auditStatus);
			taskService.complete(task.getId(), map);
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
	/**
	 * 门店售后申请
	 * @param application
	 * @param session
	 * @return
	 */
	@Transactional
	@RequestMapping(value = "storeCustomerService/apply", method = RequestMethod.POST)
	@ResponseBody
	public Json storeCustomerServiceApply(@RequestBody HyOrderApplication application, HttpSession session) {	
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(application.getOrderId());
			if (order == null) {
				throw new Exception("订单无效");
			}
			if(order.getIfjiesuan()==false) {
				json.setSuccess(false);
				json.setMsg("结算前不能售后退款");
				json.setObj(2);
				return json;
			}
			
			List<HyOrderItem> orderItems = order.getOrderItems();
			if(orderItems==null || orderItems.isEmpty()) {
				throw new Exception("没有有效订单条目");
			}
			HyOrderItem orderItem = orderItems.get(0);
			HyVisa hyVisa = hyVisaService.find(orderItem.getProductId());
			if(hyVisa==null) {
				throw new Exception("没有有效签证");
			}



			Map<String, Object> variables = new HashMap<>();
			/**找供应商需要注意*/
			//找出供应商
			HyAdmin provider = hyVisa.getCreator();
			// 指定审核供应商
			variables.put("provider", provider.getUsername());

			// 启动流程
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeShouHou", variables);
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();

			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), "发起申请:1");
			taskService.complete(task.getId(), variables);

			application.setContent("门店售后退款");
			application.setOperator(admin);
			application.setStatus(0); // 待供应商审核
			application.setCreatetime(new Date());
			application.setProcessInstanceId(task.getProcessInstanceId());
			application.setType(HyOrderApplication.STORE_CUSTOMER_SERVICE);
			order.setRefundstatus(1); // 订单退款状态为退款中
			//
			hyOrderService.update(order);

			for (HyOrderApplicationItem item : application.getHyOrderApplicationItems()) {
				item.setHyOrderApplication(application);
			}

			hyOrderApplicationService.save(application);

			json.setSuccess(true);
			json.setMsg("门店售后申请成功");
			json.setObj(null);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("门店售后申请失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 门店售后审核
	 * @param id
	 * @param comment
	 * @param auditStatus
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "storeCustomerService/audit", method = RequestMethod.POST)
	@ResponseBody
	public Json storeCustomerServiceAudit(Long id, String comment, Integer auditStatus, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyOrderApplication application = hyOrderApplicationService.find(id);
			String applyName = application.getOperator().getUsername(); // 找到提交申请的人
			String processInstanceId = application.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId.equals("")) {
				throw new Exception("审核出错，信息不完整，请重新申请");
			}

			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			HashMap<String, Object> map = new HashMap<>(); // 保存流转信息和流程变量信息
															// 下一阶段审核的部门

			if (auditStatus.equals(1)) { // 如果审核通过
				map.put("msg", "true");
				if (task.getTaskDefinitionKey().equals("usertask2")) { // 如果供应商
					// 设置下一阶段审核的部门 ---
					List<Filter> filters = new ArrayList<>();
					/**审核额度需要注意*/
					filters.add(Filter.eq("eduleixing", Eduleixing.storeShouHouLimit));
					List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
					BigDecimal money = edu.get(0).getMoney();
					//签证没有保险
					BigDecimal tuiKuan = application.getJiesuanMoney();
//					BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianJiesuanMoney());
					if (tuiKuan.compareTo(money) > 0) { // 如果退款总额大于限额，
						map.put("money", "more"); // 设置需要品控中心限额审核
						application.setStatus(1); // 待品控限额审核
					} else { // 如果退款总额不大于限额
						map.put("money", "less"); // 设置财务审核
						application.setStatus(2); // 待财务审核
					}
				} else if (task.getTaskDefinitionKey().equals("usertask3")) { // 如果品控
					application.setStatus(2); // 待财务审核
				} else if (task.getTaskDefinitionKey().equals("usertask4")) {
	
					/**财务审核通过需要注意*/
					// 售后退款财务审核通过，进行订单处理
					hyOrderApplicationService.handleHyVisaScs(application);
					
					application.setStatus(4);//已退款
					

					//售后退款财务审核通过，请王劼同学添加相关操作
					piaowuConfirmService.shouhouPiaowuRefund(application, username, 5, "门店签证售后退款");
					
				}

			} else {
				map.put("msg", "false");
				application.setStatus(5); // 已驳回
				HyOrder order = hyOrderService.find(application.getOrderId());
				order.setRefundstatus(4); // 退款已驳回
				hyOrderService.update(order);

			}
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), processInstanceId,
					(comment == null ? "审核通过" : comment) + ":" + auditStatus);
			taskService.complete(task.getId(), map);
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
	@RequestMapping("balance/view")
	@ResponseBody
	public Json getBalance(HttpSession session){
		Json json=new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		HyAdmin liable = hyAdminService.find(username);
		
		List<Filter> filters = new ArrayList<>();
//		filters.add(Filter.eq("liable", liable));	
//		List<HySupplierContract> supplierContracts = hySupplierContractService.findList(null, filters, null);
//		HySupplierContract contract = supplierContracts.get(0);
//		Long supplierId = contract.getHySupplier().getId();
		
		try {
//			filters.clear();
			filters.add(Filter.eq("supplierName",username));
			List<ReceiptTotalServicer> receiptTotalServicers = receiptTotalServicerService.findList(null,filters,null);
			BigDecimal balance = new BigDecimal(0);
			if(receiptTotalServicers.size()!=0){
				balance = balance.add(receiptTotalServicers.get(0).getBalance());
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(balance);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
		}
		return json;
	}
	@Resource(name = "hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	/**
	 * 银行账号列表
	 * @param session
	 * @return
	 */
	@RequestMapping("bank/list/view")
	@ResponseBody
	public Json bankList(HttpSession session) {
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		try {
			List<Filter> branchFilters = new  ArrayList<>();
			
			branchFilters.add(Filter.eq("hyDepartment", EmployeeUtil.getCompany(hyAdminService.find(username))));
			List<HyCompany> hyCompanys = hyCompanyService.findList(null,branchFilters,null);
			HyCompany hyCompany2 = null;
			Set<BankList> branchbankLists = null;
			if(hyCompanys.size()!=0&&!hyCompanys.isEmpty()){
				hyCompany2 =  hyCompanys.get(0);
				branchbankLists  = hyCompany2.getBankLists();
			}
			List<HashMap<String, Object>> res2 = new ArrayList<>();
			for(BankList branchbankList :branchbankLists){
				HashMap<String, Object> m = new HashMap<>();
				m.put("id", branchbankList.getId());
				m.put("branchAccountAlias", branchbankList.getAlias());
				m.put("branchBankName", branchbankList.getBankName());
				m.put("branchBankCode", branchbankList.getBankCode());
				m.put("branchBankType", branchbankList.getBankType());
				m.put("bankNum", branchbankList.getBankAccount());
				res2.add(m);
			}
			
			json.setObj(res2);
			json.setSuccess(true);
			json.setMsg("获取成功");
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("获取失败");
			json.setObj(e);
		}
		return json;
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
