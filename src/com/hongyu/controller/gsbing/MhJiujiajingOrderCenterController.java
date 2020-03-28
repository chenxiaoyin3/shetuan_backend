package com.hongyu.controller.gsbing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.hongyu.controller.gsbing.TicketHotelandsceneOrderCenterController.MyOrderItem;
import com.hongyu.controller.gsbing.TicketHotelandsceneOrderCenterController.ReceiptRefund;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyReceiptRefund;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.entity.HyTicketHotelandsceneRoom;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.Store;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyReceiptRefundService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketHotelandsceneRoomService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.service.PiaowuConfirmService;
import com.hongyu.service.StoreService;
import com.hongyu.service.SupplierDismissOrderApplyService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
/**
 * 官网订单中心-酒加景
 * Author:GSbing
 */
@Controller
@RequestMapping("admin/mh_order_center/jiujiajing")
public class MhJiujiajingOrderCenterController {

	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	@Resource(name="hyTicketHotelandsceneServiceImpl")
	private HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	@Resource(name="hyTicketHotelandsceneRoomServiceImpl")
	private HyTicketHotelandsceneRoomService hyTicketHotelandsceneRoomService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	@Resource(name="commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name="hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;
	
	@Resource(name="hySupplierElementServiceImpl")
	private HySupplierElementService hySupplierElementService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name="storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name="hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	
	@Resource(name="hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	@Resource(name="hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;
	
	@Resource(name="hyOrderCustomerServiceImpl")
	private HyOrderCustomerService hyOrderCustomerService;
	
	@Resource(name="hyReceiptRefundServiceImpl")
	private HyReceiptRefundService hyReceiptRefundService;
	
	@Resource(name="hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name="supplierDismissOrderApplyServiceImpl")
	private SupplierDismissOrderApplyService supplierDismissOrderApplyService;
	                   
	@Resource (name = "piaowuConfirmServiceImpl")
	private PiaowuConfirmService piaowuConfirmService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	
	
	//订单详情
	@RequestMapping(value="detail/view")
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try {
			HyOrder hyOrder=hyOrderService.find(id);
			Map<String, Object> map = new HashMap<>();	
			map.put("orderNumber", hyOrder.getOrderNumber()); //订单号
			map.put("createTime", hyOrder.getCreatetime()); //下单时间
			map.put("name", hyOrder.getName());
			List<HyOrderItem> orderItems=hyOrder.getOrderItems();
			HyOrderItem hyOrderItem=orderItems.get(0);
			HyTicketHotelandscene hyTicketHotelandscene=hyTicketHotelandsceneService.find(hyOrderItem.getProductId());		
			map.put("productId", hyTicketHotelandscene.getProductId()); //产品编号
			map.put("paystatus", hyOrder.getPaystatus());
			map.put("checkstatus", hyOrder.getCheckstatus()); //确认状态
			map.put("refundstatus", hyOrder.getRefundstatus()); //退款状态
			map.put("status", hyOrder.getStatus());
			map.put("source", hyOrder.getSource()); //订单来源
			map.put("ifjiesuan", hyOrder.getIfjiesuan()); //是否结算
			map.put("startDate", hyOrderItem.getStartDate()); //服务开始日期
			map.put("discountedPrice", hyOrder.getDiscountedPrice()); //优惠金额
			map.put("adjustMoney", hyOrder.getAdjustMoney()); //调整金额
			Long storeId = hyOrder.getStoreId();
			if(storeId!=null){
				Store store = storeService.find(storeId);
				map.put("storeName", store==null?"":store.getStoreName());
			}else{
				map.put("storeName", "");
			}
			Date date=new Date();
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(date);
			cal1.set(Calendar.HOUR_OF_DAY, 0);
			cal1.set(Calendar.MINUTE, 0);
			cal1.set(Calendar.SECOND, 0);
			cal1.set(Calendar.MILLISECOND, 0);
			Date startDate=hyOrderItem.getEndDate(); //服务结束时间
			if(date.compareTo(startDate)>0) {
				map.put("productStatus", 1); //已使用
			}
			else {
				map.put("productStatus", 0); //未使用
			}
			map.put("orderMoney", hyOrder.getWaimaiMoney()); //网络销售部看到的是官网销售价
			map.put("quantity", hyOrderItem.getNumber()); //商品数量
			map.put("koudianMethod", hyOrder.getKoudianMethod());
			map.put("proportion", hyOrder.getProportion());
			map.put("koudianMoney", hyOrder.getKoudianMoney());
			map.put("contact", hyOrder.getContact()); //联系人
			map.put("telephone", hyOrder.getPhone());
			map.put("remark", hyOrder.getRemark());
			json.setSuccess(true);
			json.setObj(map);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//商品信息
	@RequestMapping("productList/view")
	@ResponseBody
	public Json productList(Long id)
	{
		Json json=new Json();
		try {
			HyOrder hyOrder=hyOrderService.find(id);
			List<Map<String, Object>> result = new ArrayList<>();
			List<HyOrderItem> orderItems=hyOrder.getOrderItems();
			for(HyOrderItem item:orderItems) {
				Map<String, Object> map=new HashMap<>();
				HyTicketHotelandscene hyTicketHotelandscene=hyTicketHotelandsceneService.find(item.getProductId());
				map.put("productId", hyTicketHotelandscene.getProductId()); //商品编号
				map.put("name", hyTicketHotelandscene.getProductName());
				HyTicketHotelandsceneRoom room=hyTicketHotelandsceneRoomService.find(item.getSpecificationId());
				map.put("roomType", room.getRoomType());
				map.put("quantity", item.getNumber());
				map.put("productPrice", hyOrder.getWaimaiMoney());
				result.add(map);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//游客信息
	@RequestMapping("customerList/view")
	@ResponseBody
	public Json customerList(Long id)
	{
		Json json=new Json();
		try {
			HyOrder hyOrder=hyOrderService.find(id);
		    List<HyOrderItem> orderItems=hyOrder.getOrderItems();
		    HyOrderItem hyOrderItem=orderItems.get(0);
		    List<HyOrderCustomer> orderCustmers=hyOrderItem.getHyOrderCustomers();
		    List<Map<String, Object>> result = new ArrayList<>();
		    for(HyOrderCustomer customer:orderCustmers) {
		    	Map<String, Object> map=new HashMap<>();
		    	map.put("name", customer.getName());
		    	map.put("certificate", customer.getCertificate());
		    	map.put("phone", customer.getPhone());
		    	result.add(map);
		    }
			json.setSuccess(true);
			json.setObj(result);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	// 实收付款记录列表
	@RequestMapping("receipt_refund_list/view")
	@ResponseBody
	public Json receiptRefundList(Long id,Integer type)
	{
		Json json=new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("order", order));
			filters.add(Filter.eq("type", type)); //0-收款；1-退款
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createTime"));
			List<HyReceiptRefund> receiptRefunds = hyReceiptRefundService.findList(null, filters, orders);	
			json.setSuccess(true);
			json.setObj(receiptRefunds);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	//收退款记录
	@RequestMapping(value = "payandrefund_record/list")
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
	
	//订单日志
	@RequestMapping("orderLogs/view")
	@ResponseBody
	public Json orderLogs(Long id)
	{
		Json json=new Json();
		try {
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("orderId", id));
			List<Order> orders = new LinkedList<>();
			orders.add(Order.desc("id"));
			List<HyOrderApplication> hyOrderApplications=hyOrderApplicationService.findList(null,filters,orders);
            List<Map<String, Object>> result = new LinkedList<>();
			for(HyOrderApplication hyOrderApplication : hyOrderApplications) {
				Map<String, Object> map = new HashMap<>();
				map.put("type", hyOrderApplication.getType());
				if( hyOrderApplication.getOperator()!=null) {
					map.put("operator", hyOrderApplication.getOperator().getName());
				}
				else {
					map.put("operator", null);
				}
				map.put("view", hyOrderApplication.getView()); //意见
				map.put("createTime", hyOrderApplication.getCreatetime()); //操作时间
				map.put("status", hyOrderApplication.getStatus()); //0驳回,1通过
				result.add(map);
			}
			json.setSuccess(true);
			json.setObj(result);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	
	/**官网不选门店订单中心*/
	@RequestMapping("menhu/list/view")
	@ResponseBody
	public Json menhuListview(Pageable pageable, HyOrder queryParam,@DateTimeFormat(pattern="yyyy-MM-dd") Date startTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime)
	{
		Json json=new Json();
		try {
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("type", 5)); //5-酒加景
			filters.add(Filter.eq("source", 1)); //订单来源,1-官网不选门店
			if(startTime != null) {
				filters.add(Filter.ge("createtime", DateUtil.getStartOfDay(startTime)));
			}
			if(endTime != null) {
				filters.add(Filter.le("createtime", DateUtil.getEndOfDay(endTime)));
			}
			List<Order> orders=new ArrayList<>();
			orders.add(Order.desc("createtime"));
			pageable.setOrders(orders);
			pageable.setFilters(filters);
			Page<HyOrder> page = hyOrderService.findPage(pageable,queryParam);
			filters.clear();
			List<Map<String, Object>> result = new LinkedList<>();
			Map<String, Object> obj = new HashMap<>();
			for(HyOrder hyOrder:page.getRows()) {
			    Map<String, Object> map = new HashMap<>();
				map.put("id", hyOrder.getId());
				map.put("status", hyOrder.getStatus()); //订单状态0待门店支付，1待门店确认，2待供应商确认，3供应商通过，4驳回待财务确认,5已驳回,6已取消
			    map.put("orderNumber", hyOrder.getOrderNumber());
			    map.put("name", hyOrder.getName());
			    List<HyOrderItem> orderItems=hyOrder.getOrderItems();
			    HyTicketHotelandscene hyTicketHotelandscene=hyTicketHotelandsceneService.find(orderItems.get(0).getProductId());
			    map.put("star", hyTicketHotelandscene.getHotelStar());
			    map.put("createTime", hyOrder.getCreatetime());
				result.add(map);					
			}
					
			obj.put("pageNumber", page.getPageNumber());
			obj.put("pageSize", page.getPageSize());
			obj.put("total", page.getTotal());
			obj.put("rows", result);
			json.setObj(obj);
			json.setSuccess(true);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//门店取消订单
	@RequestMapping("store_cancel_order")
	@ResponseBody
	public Json storeCancelOrder(Long id, HttpSession session)
	{
		Json json=new Json();
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			HyOrder hyOrder=hyOrderService.find(id);
			if (hyOrder == null) {
				throw new Exception("订单不存在");
			}
			if (hyOrder.getPaystatus().equals(Constants.HY_ORDER_PAY_STATUS_PAID)) {
				// 如果订单已支付
				throw new Exception("订单状态已支付，无法取消");
			}
			if (hyOrder.getStatus().equals(Constants.HY_ORDER_STATUS_CANCELED)) {
				throw new Exception("订单已经取消，不能重复取消");
			}
			// 设置订单状态为已取消
			hyOrder.setStatus(Constants.HY_ORDER_STATUS_CANCELED);
			
			//修改库存
			HyOrderItem orderItem=hyOrder.getOrderItems().get(0);
			Long priceId=orderItem.getPriceId();
		    List<Filter> filters=new ArrayList<>();
		    filters.add(Filter.eq("type", 1)); 
		    filters.add(Filter.eq("priceInboundId", priceId));
		    filters.add(Filter.eq("day", orderItem.getStartDate()));
			List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,filters,null);
			HyTicketInbound hyTicketInbound=ticketInbounds.get(0);
			hyTicketInbound.setInventory(hyTicketInbound.getInventory()+orderItem.getNumber());
			hyTicketInboundService.update(hyTicketInbound);
			
			//增加订单日志
			HyOrderApplication application = new HyOrderApplication();
			application.setContent("门店取消订单");
			application.setOperator(admin);
			application.setOrderId(id);
			application.setCreatetime(new Date());
			application.setStatus(HyOrderApplication.STATUS_ACCEPT);
			application.setType(HyOrderApplication.STORE_CANCEL_ORDER);
			application.setOrderNumber(hyOrder.getOrderNumber());
			hyOrderApplicationService.save(application);
			json.setSuccess(true);
			json.setMsg("取消成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//门店支付订单
	@RequestMapping(value = "storePay")
	@ResponseBody
	public Json storePay(Long id,HttpSession session)
	{
		Json json=new Json();
		try {
			json = hyOrderService.addStoreOrderPayment(id, session);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("支付错误:"+e.getMessage());
		}
		return json;
	}
	
	//供应商调整金额
	@RequestMapping(value = "adjust_money")
	@ResponseBody
	public Json adjustMoney(Long id, BigDecimal adjustMoney, HttpSession session) {
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单无效");
			}
			if (!order.getStatus().equals(Constants.HY_ORDER_STATUS_WAIT_STORE_PAY)) {
				throw new Exception("订单状态不对");
			}
			BigDecimal oldAjustMoney = order.getAdjustMoney();
			if(oldAjustMoney==null){
				oldAjustMoney=BigDecimal.valueOf(0);
			}
			//修改订单金额
			order.setWaimaiMoney(order.getWaimaiMoney().subtract(oldAjustMoney).add(adjustMoney));
			order.setJiusuanMoney(order.getJiusuanMoney().subtract(oldAjustMoney).add(adjustMoney));
			order.setJiesuanMoney1(order.getJiesuanMoney1().subtract(oldAjustMoney).add(adjustMoney));
			order.setAdjustMoney(adjustMoney);
			
			//修改扣点金额
			if(order.getIfjiesuan()==false){	//如果没有结算
				if(order.getKoudianMethod().equals(Constants.DeductPiaowu.liushui.ordinal())){
					order.setKoudianMoney(
							order.getProportion().multiply(
									order.getJiesuanMoney1()).multiply(BigDecimal.valueOf(0.01)).setScale(2, RoundingMode.HALF_UP));
				}
			}
			
			
			hyOrderService.update(order);
			json.setSuccess(true);
			json.setMsg("调整金额成功");

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("调整金额失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	
	// 供应商确认订单
	@RequestMapping(value = "supplier_confirm")
	@ResponseBody
	public Json supplierConfirm(Long id, String view, Integer status, HttpSession session)
	{
		Json json=new Json();
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			HyOrder hyOrder=hyOrderService.find(id);
			if (hyOrder == null) {
				throw new Exception("订单不存在");
			}
			if (!hyOrder.getStatus().equals(Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)) {
				throw new Exception("订单状态不对");
			}
			
			//供应商驳回
			if(status==0) {
				if (view == null || view.equals("")) {
					throw new Exception("请输入驳回意见");
				}
				supplierDismissOrderApplyService.addSupplierDismissOrderSubmit(id, view, session);
			}
			
			//供应商确认通过
			else {
				hyOrder.setStatus(Constants.HY_ORDER_STATUS_PROVIDER_ACCEPT);
				
				//下面生成打款记录
				boolean isConfirm = piaowuConfirmService.orderPiaowuConfirm(id, 4, session);
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

			hyOrderService.update(hyOrder);
			json.setSuccess(true);
			json.setMsg("确认成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("确认失败:"+e.getMessage());
		}
		return json;
	}
	
	// 添加实收付款记录
	static class ReceiptRefund {
		public Long orderId;
		public BigDecimal money;
		public Integer type;
		public String method;
		public Date collectionTime;
		public String remark;
		public String bankNum;
		public String cusName;
		public String cusBank;
		public String cusUninum;
		public String reason;
		public BigDecimal adjustMoney;
	}
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
		
	@RequestMapping(value = "receipt_refund/add", method = RequestMethod.POST)
	@ResponseBody
	public Json addReceiptRefund(@RequestBody ReceiptRefund body, HttpSession session) {
		/**
		* 获取当前用户
		*/
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);

		Json json = new Json();
		try {
			Long orderId = body.orderId; // 订单id
			if (orderId == null) {
				throw new Exception("没有订单参数");
			}
			HyOrder order = hyOrderService.find(orderId);
			if (order == null) {
				throw new Exception("没有有效订单");
			}
			BigDecimal money = body.money; // 收退款钱数

			if (money == null) {
				throw new Exception("传入的钱数有误");
			}

			Integer type = body.type; // 类型

			String method = body.method; // 收退款方式

			Date collectionTime = body.collectionTime; // 收退款时间
			String remark = body.remark; // 备注
				
			String bankNum = body.bankNum;	//银行卡号
				
			String cusName = body.cusName;	//游客姓名
				
			String cusBank = body.cusBank;	//游客银行
				
			String cusUninum = body.cusUninum;	//游客联行号
				
			String reason = body.reason;	//原因
				
			BigDecimal adjustMoney = body.adjustMoney;	//调整金额

			HyReceiptRefund receiptRefund = new HyReceiptRefund();

			receiptRefund.setCollectionTime(collectionTime);
			receiptRefund.setCreateTime(new Date());
			receiptRefund.setMethod(method);
				
			receiptRefund.setOperator(admin);
			receiptRefund.setOrder(order);
			receiptRefund.setRemark(remark);
			receiptRefund.setStore(storeService.findStore(admin));
			receiptRefund.setType(type);
			receiptRefund.setBankNum(bankNum);
			receiptRefund.setStatus(0);	//待分公司财务确认
			receiptRefund.setBranch(departmentService.findCompanyOfDepartment(admin.getDepartment()));
			receiptRefund.setCusName(cusName);
			receiptRefund.setCusBank(cusBank);
			receiptRefund.setCusUninum(cusUninum);
			receiptRefund.setReason(reason);
			receiptRefund.setAdjustMoney(adjustMoney==null?BigDecimal.ZERO:adjustMoney);
			receiptRefund.setMoney(money);
			hyReceiptRefundService.save(receiptRefund);
			json.setSuccess(true);
			json.setMsg("添加成功");
			json.setObj(receiptRefund.getId());

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("添加失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	
	static class MyOrderItem implements Serializable {
		private Long itemId;
		private String name;
		private Integer type;
		private Integer priceType;
		private Integer number;
		private Integer returnNumber;
		private BigDecimal jiesuanPrice;
		private BigDecimal jiesuanRefund;
		private BigDecimal waimaiPrice;
		private BigDecimal waimaiRefund;
		private BigDecimal baoxianJiesuanPrice;
		private BigDecimal baoxianJiesuanRefund;
		private BigDecimal baoxianWaimaiPrice;
		private BigDecimal baoxianWaimaiRefund;
		private String customerName;


		public Long getItemId() {
			return itemId;
		}

		public void setItemId(Long itemId) {
			this.itemId = itemId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}

		public Integer getPriceType() {
			return priceType;
		}

		public void setPriceType(Integer priceType) {
			this.priceType = priceType;
		}

		public Integer getNumber() {
			return number;
		}

		public void setNumber(Integer number) {
			this.number = number;
		}

		public Integer getReturnNumber() {
			return returnNumber;
		}

		public void setReturnNumber(Integer returnNumber) {
			this.returnNumber = returnNumber;
		}
		
		public BigDecimal getJiesuanPrice() {
			return jiesuanPrice;
		}

		public void setJiesuanPrice(BigDecimal jiesuanPrice) {
			this.jiesuanPrice = jiesuanPrice;
		}

		public BigDecimal getJiesuanRefund() {
			return jiesuanRefund;
		}

		public void setJiesuanRefund(BigDecimal jiesuanRefund) {
			this.jiesuanRefund = jiesuanRefund;
		}

		public BigDecimal getWaimaiPrice() {
			return waimaiPrice;
		}

		public void setWaimaiPrice(BigDecimal waimaiPrice) {
			this.waimaiPrice = waimaiPrice;
		}

		public BigDecimal getWaimaiRefund() {
			return waimaiRefund;
		}

		public void setWaimaiRefund(BigDecimal waimaiRefund) {
			this.waimaiRefund = waimaiRefund;
		}

		public BigDecimal getBaoxianJiesuanPrice() {
			return baoxianJiesuanPrice;
		}

		public void setBaoxianJiesuanPrice(BigDecimal baoxianJiesuanPrice) {
			this.baoxianJiesuanPrice = baoxianJiesuanPrice;
		}

		public BigDecimal getBaoxianJiesuanRefund() {
			return baoxianJiesuanRefund;
		}

		public void setBaoxianJiesuanRefund(BigDecimal baoxianJiesuanRefund) {
			this.baoxianJiesuanRefund = baoxianJiesuanRefund;
		}

		public BigDecimal getBaoxianWaimaiPrice() {
			return baoxianWaimaiPrice;
		}

		public void setBaoxianWaimaiPrice(BigDecimal baoxianWaimaiPrice) {
			this.baoxianWaimaiPrice = baoxianWaimaiPrice;
		}

		public BigDecimal getBaoxianWaimaiRefund() {
			return baoxianWaimaiRefund;
		}

		public void setBaoxianWaimaiRefund(BigDecimal baoxianWaimaiRefund) {
			this.baoxianWaimaiRefund = baoxianWaimaiRefund;
		}

		public String getCustomerName() {
			return customerName;
		}

		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}

	}
	
	/**
	 * 门店退款（售前）订单条目列表
	 */
	@RequestMapping(value = "store_refund_list")
	@ResponseBody
	public Json storeRefundList(Long id) {
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}

			/**找退款规则需要注意*/
			BigDecimal ticketRefundPercentage = hyOrderService.getTicketRefundPercentage(order).multiply(BigDecimal.valueOf(0.01));
			
			List<MyOrderItem> lists = new ArrayList<>();
			for (HyOrderItem item : order.getOrderItems()) {
				MyOrderItem myOrderItem = new MyOrderItem();
				myOrderItem.setItemId(item.getId());
				myOrderItem.setType(item.getType());
				myOrderItem.setPriceType(item.getPriceType());
				myOrderItem.setName(item.getName());
				myOrderItem.setReturnNumber(item.getNumberOfReturn());
				myOrderItem.setNumber(item.getNumber());
				myOrderItem.setJiesuanPrice(item.getJiesuanPrice());
				myOrderItem.setJiesuanRefund(item.getJiesuanPrice().multiply(ticketRefundPercentage));
				myOrderItem.setWaimaiPrice(item.getWaimaiPrice());
				myOrderItem.setWaimaiRefund(item.getWaimaiPrice().multiply(ticketRefundPercentage));
				myOrderItem.setBaoxianJiesuanPrice(BigDecimal.ZERO);
				myOrderItem.setBaoxianJiesuanRefund(myOrderItem.getBaoxianJiesuanPrice());
				myOrderItem.setBaoxianWaimaiPrice(BigDecimal.ZERO);
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
	
	//门店提交退款申请
	@RequestMapping(value = "store_refund/apply", method = RequestMethod.POST)
	@ResponseBody
	public Json storeRefundApply(@RequestBody HyOrderApplication application, HttpSession session) {
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
			HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(orderItem.getProductId());
			if(hyTicketHotelandscene==null) {
				throw new Exception("没有有效酒加景产品");
			}



			Map<String, Object> variables = new HashMap<>();
			/**找供应商需要注意*/
			//找出供应商
			HyAdmin provider = hyTicketHotelandscene.getCreator();
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
			application.setBaoxianJiesuanMoney(BigDecimal.ZERO);
			application.setBaoxianWaimaiMoney(BigDecimal.ZERO);
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
	
	//门店退款列表页
	@RequestMapping(value = "store_refund/list/view")
	@ResponseBody
	public Json storeCancelGroupList(Pageable pageable, Integer status, String providerName, HttpSession session) {
		return hyOrderApplicationService.getApplicationList(pageable, status, providerName, session, 
				HyOrderApplication.STORE_CANCEL_GROUP,5);	//酒加景订单类型为5
	}
	
	//门店售后
	@RequestMapping(value = "store_customer_service/list/view")
	@ResponseBody
	public Json storeCustomerServiceList(Pageable pageable, Integer status, String providerName, HttpSession session) {
		return hyOrderApplicationService.getApplicationList(pageable, status, providerName, session,
				HyOrderApplication.STORE_CUSTOMER_SERVICE,5);	//酒加景订单类型为5
	}
	
	
	//门店退款审核详情页
	@RequestMapping(value = { "store_refund/detail/view", "store_customer_service/detail/view" })
	@ResponseBody
	public Json storeRefundDetail(Long id) {
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
	
	
	
	@RequestMapping(value = "store_refund/audit", method = RequestMethod.POST)
	@ResponseBody
	public Json storeRefundAudit(Long id, String comment, Integer auditStatus, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyOrderApplication application = hyOrderApplicationService.find(id);
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
					hyOrderApplicationService.handleTicketHotelandsceneScg(application);
					
					application.setStatus(4);//已退款
					

					//售前退款财务审核通过，添加相关操作
					piaowuConfirmService.piaowuRefund(application, username, 4, "门店酒加景售前退款");
					
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
			taskService.claim(task.getId(), username);
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
	
	@RequestMapping(value = "scs_list")
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
	@RequestMapping(value = "store_customer_service/apply", method = RequestMethod.POST)
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
			HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(orderItem.getProductId());
			if(hyTicketHotelandscene==null) {
				throw new Exception("没有有效的酒加景产品");
			}



			Map<String, Object> variables = new HashMap<>();
			/**找供应商需要注意*/
			//找出供应商
			HyAdmin provider = hyTicketHotelandscene.getCreator();
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
			application.setBaoxianJiesuanMoney(BigDecimal.ZERO);
			application.setBaoxianWaimaiMoney(BigDecimal.ZERO);
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

	@RequestMapping(value = "store_customer_service/audit", method = RequestMethod.POST)
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
					hyOrderApplicationService.handleTicketHotelandsceneScs(application);
					
					application.setStatus(4);//已退款
					

					//售前退款财务审核通过，请王劼同学添加相关操作
					piaowuConfirmService.shouhouPiaowuRefund(application, username, 4, "门店酒加景售后退款");
					
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
			taskService.claim(task.getId(), username);
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

}
