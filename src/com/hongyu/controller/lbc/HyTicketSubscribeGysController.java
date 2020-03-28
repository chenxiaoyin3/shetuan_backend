package com.hongyu.controller.lbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
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
import com.hongyu.controller.lbc.HyTicketSubscribeOrderCenterController.MyOrderItem;
import com.hongyu.controller.lbc.HyTicketSubscribeOrderCenterController.ReceiptRefund;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyReceiptRefund;
import com.hongyu.entity.HyTicketSubscribe;
import com.hongyu.entity.HyTicketSubscribePrice;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.Store;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyReceiptRefundService;
import com.hongyu.service.HyTicketSubscribePriceService;
import com.hongyu.service.HyTicketSubscribeService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.service.PiaowuConfirmService;
import com.hongyu.service.StoreService;
import com.hongyu.service.SupplierDismissOrderApplyService;
import com.hongyu.util.ArrayHandler;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.SendMessageEMY;


@Controller
@RequestMapping("admin/ticket_subscribe_gys/")
public class HyTicketSubscribeGysController {
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name="hyTicketSubscribeServiceImpl")
	private HyTicketSubscribeService hyTicketSubscribeService;
	
	@Resource(name="hyTicketSubscribePriceServiceImpl")
	private HyTicketSubscribePriceService hyTicketSubscribePriceService;
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name = "supplierDismissOrderApplyServiceImpl")
	SupplierDismissOrderApplyService supplierDismissOrderApplyService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	@Resource(name = "piaowuConfirmServiceImpl")
	private PiaowuConfirmService piaowuConfirmService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
	
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;

	/**
	 * 供应商订单中心列表
	 * @author LBC
	 */
	@RequestMapping("gys_page/view")
	@ResponseBody
	public Json gysPageView(Pageable pageable,Integer payStatus,Integer refundStatus,Integer star,
			String sceneName,String orderNumber,HttpSession session,HttpServletRequest request,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endDate){
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
			
			String[] attrs = new String[]{
					"id","status","adjustMoney","orderNumber","sceneName","star","startDate","createTime","operator","source"
			};
			
			//订单type 2 认购门票
			
			StringBuilder totalSb = new StringBuilder("select count(*)");
			StringBuilder pageSb = new StringBuilder("select o1.id,o1.status,o1.adjust_money,o1.order_number,h1.scene_name,"
					+ "h1.star,i1.start_date,o1.createtime,o1.operator_id,o1.source");
			StringBuilder sb = new StringBuilder(" from hy_order o1,hy_order_item i1,hy_ticket_subscribe h1"
					+ " where o1.type=2 and o1.id=i1.order_id and i1.product_id=h1.id and i1.price_type=0");
			
			if(payStatus!=null){
				sb.append(" and o1.paystatus="+payStatus);
			}
			if(refundStatus!=null){
				sb.append(" and o1.refundstatus="+refundStatus);
			}
			if(star!=null){
				sb.append(" and h1.star="+star);
			}
			if(sceneName!=null){
				sb.append(" and h1.scene_name like '%"+sceneName+"%'");
			}
			if(orderNumber!=null){
				sb.append(" and o1.order_number like '%"+orderNumber+"%'");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(startDate != null) {
				sb.append(" and o1.createtime >= '" + sdf.format(startDate) + "'");
			}
			if(endDate != null) {
				sb.append(" and o1.createtime <= '" + sdf.format(endDate) + "'");
			}
			
			if(hyAdmins!=null && !hyAdmins.isEmpty()){
				List<String> adminStrArr = new ArrayList<>();
				for(HyAdmin hyAdmin:hyAdmins){
					adminStrArr.add("'"+hyAdmin.getUsername()+"'");
					
				}
				String adminStr = String.join(",",adminStrArr);
				sb.append(" and o1.supplier in ("+adminStr+")");
			}
			

			List totals = hyOrderService.statis(totalSb.append(sb).toString());
			Integer total = ((BigInteger)totals.get(0)).intValue();
			
			sb.append(" order by o1.createtime desc");
			
			Integer sqlStart = (pageable.getPage()-1)*pageable.getRows();
			Integer sqlEnd = pageable.getPage()*pageable.getRows();
			sb.append(" limit "+sqlStart+","+sqlEnd);
			
			List<Object[]> objs = hyOrderService.statis(pageSb.append(sb).toString());
		
			List<Map<String, Object>> rows = new ArrayList<>();
			for(Object[] obj : objs){
				Map<String, Object> map = ArrayHandler.toMap(attrs, obj);
				
				if(((String)map.get("operator")).equals(
						admin.getUsername())){
					if (co == CheckedOperation.view) {
						map.put("privilege", "view");
					} else {
						map.put("privilege", "edit");
					}
				}else{
					if (co == CheckedOperation.edit) {
						map.put("privilege", "edit");
					} else {
						map.put("privilege", "view");
					}
				}
				
				rows.add(map);
			}
			

			Page<Map<String, Object>> page = new Page<>(rows,total,pageable);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg(e.getMessage());
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 供应商确认订单
	 * @author LBC
	 */
	// 供应商确认订单
	@RequestMapping(value = "provider_confirm")
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
				// 如果供应商通过
				//请王劼同学在此处添加逻辑
				boolean isConfirm = piaowuConfirmService.orderPiaowuConfirm(id, 6, session);
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
	 * @author LBC
	 */
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detailView(Long id,HttpSession session) {
		Json json = new Json();
		try {
			HyOrder hyOrder = hyOrderService.find(id);
			if(hyOrder==null) {
				throw new Exception("订单不存在");
			}
			List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
			if(hyOrderItems==null || hyOrderItems.isEmpty()) {

				throw new Exception("没有订单条目数据");
			}
			HyOrderItem hyOrderItem = hyOrderItems.get(0);
			HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(hyOrderItem.getProductId());
			Map<String, Object> map = new HashMap<>();
			map.put("id", hyOrder.getId());
			map.put("orderNumber", hyOrder.getOrderNumber());
			map.put("createTime", hyOrder.getCreatetime());
			map.put("sceneName", hyTicketSubscribe.getSceneName());
			map.put("startDate", hyOrderItem.getStartDate());
			map.put("star", hyTicketSubscribe.getStar());
			map.put("payStatus", hyOrder.getPaystatus());
			map.put("refundStatus", hyOrder.getRefundstatus());
			map.put("productStatus", (new Date()).compareTo(hyOrderItem.getStartDate())<0?false:true);
			map.put("status", hyOrder.getStatus());
			map.put("adjustMoney", hyOrder.getAdjustMoney());
			map.put("source", hyOrder.getSource());
			Long storeId = hyOrder.getStoreId();
			if(storeId!=null){
				Store store = storeService.find(storeId);
				map.put("storeName", store==null?"":store.getStoreName());
			}else{
				map.put("storeName", "");
			}
			map.put("jiesuanMoney1", hyOrder.getJiesuanMoney1());
			map.put("waimaiMoney", hyOrder.getWaimaiMoney());
			map.put("people", hyOrder.getPeople());
			map.put("koudianMethod", hyOrder.getKoudianMethod());
			map.put("proportion", hyOrder.getProportion());
			map.put("headProportion", hyOrder.getHeadProportion());
			map.put("koudianMoney", hyOrder.getKoudianMoney());
			map.put("jiesuanTuikuan", hyOrder.getJiesuanTuikuan());
			map.put("discountId", hyOrder.getDiscountedId());
			map.put("discountType", hyOrder.getDiscountedType());
			map.put("discountPrice", hyOrder.getDiscountedPrice());
			map.put("contact", hyOrder.getContact());
			map.put("phone", hyOrder.getPhone());
			map.put("remark", hyOrder.getRemark());
			map.put("ifJiesuan", hyOrder.getIfjiesuan());
			//加上推广详解
			map.put("introduction", hyTicketSubscribe.getIntroduction());
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		}catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(true);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 订单条目详情
	 * @author LBC
	 */
	@RequestMapping("item_detail/view")
	@ResponseBody
	public Json itemDetailView(Long id,HttpSession session) {
		
		Json json = new Json();
		try {
			HyOrder hyOrder = hyOrderService.find(id);
			if(hyOrder==null) {
				throw new Exception("订单不存在");
			}
			List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
			if(hyOrderItems==null || hyOrderItems.isEmpty()) {

				throw new Exception("没有订单条目数据");
			}
			
			List<Map<String, Object>> list = new ArrayList<>();
			for(HyOrderItem hyOrderItem:hyOrderItems) {
				Map<String, Object> map = new HashMap<>();
				HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(hyOrderItem.getProductId());
				//价格id找到门票价格
				HyTicketSubscribePrice hyTicketSubscribePrice = hyTicketSubscribePriceService.find(hyOrderItem.getPriceId());
				map.put("id", hyOrderItem.getId());
				map.put("productId", hyOrderItem.getProductId());
				map.put("productName", hyOrderItem.getName());
				map.put("openTime", hyTicketSubscribe.getOpenTime());
				map.put("closeTime", hyTicketSubscribe.getCloseTime());
				map.put("ticketExchangeAddress", hyTicketSubscribe.getTicketExchangeAddress());
				map.put("quantity", hyOrderItem.getNumber());
				map.put("jiesuanMoney", hyOrderItem.getJiesuanPrice());
				map.put("waimaiMoney", hyOrderItem.getWaimaiPrice());
				map.put("returnNum", hyOrderItem.getNumberOfReturn());
				list.add(map);
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(list);
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();

		}
		return json;
		
	}
	
	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	/**
	 * 收退款记录
	 * @param id
	 * @param type
	 * @return
	 */
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
	

	/**
	 * 订单日志
	 * @param pageable
	 * @param id
	 * @return
	 */
	@RequestMapping("application_list/view")
	@ResponseBody
	public Json applicationList(Pageable pageable, Long id) {
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
				map.put("cancleGroupId", hyOrderApplication.getCancleGroupId());
				map.put("content", hyOrderApplication.getContent());
				map.put("createtime", hyOrderApplication.getCreatetime());
				map.put("id", hyOrderApplication.getId());
				map.put("isSubstatis", hyOrderApplication.getIsSubStatis());
				map.put("jiesuanMoney", hyOrderApplication.getJiesuanMoney());
				if(hyOrderApplication.getOperator()!=null) {
					map.put("operator", hyOrderApplication.getOperator().getName());
				}else {
					map.put("operator", "");
				}
				
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
			// TODO: handle exception
		}
		return json;
	}
	
	@Resource(name = "hyReceiptRefundServiceImpl")
	HyReceiptRefundService hyReceiptRefundService;
	// 实收付款记录列表
	@RequestMapping("receipt_refund/list")
	@ResponseBody
	public Json receiptRefundList(Long id, Integer type) {
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
	
	/**
	 * 添加实收付款记录
	 * @author LBC
	 */
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
	
	@Resource(name = "hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;
	/**
	 * 门店退款（售前）订单条目列表
	 */
	@RequestMapping(value = "scg_list")
	@ResponseBody
	public Json scgList(Long id) {
		Json json = new Json();
		try {
			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}

			/**找退款规则需要注意*/
			//票务退款比例
			BigDecimal ticketRefundPercentage = hyOrderService.getTicketRefundPercentage(order).multiply(BigDecimal.valueOf(0.01));
			List<MyOrderItem> lists = new ArrayList<>();
			for (HyOrderItem item : order.getOrderItems()) {
				MyOrderItem myOrderItem = new MyOrderItem();
				myOrderItem.setItemId(item.getId());
				myOrderItem.setType(item.getType());
				myOrderItem.setPriceType(item.getPriceType());
				myOrderItem.setName(item.getName());
				myOrderItem.setNumber(item.getNumber());
				myOrderItem.setJiesuanPrice(item.getJiesuanPrice());
				myOrderItem.setJiesuanRefund(item.getJiesuanPrice().multiply(ticketRefundPercentage));
				myOrderItem.setWaimaiPrice(item.getWaimaiPrice());
				myOrderItem.setWaimaiRefund(item.getWaimaiPrice().multiply(ticketRefundPercentage));
				myOrderItem.setBaoxianJiesuanPrice(BigDecimal.ZERO);
				myOrderItem.setBaoxianJiesuanRefund(myOrderItem.getBaoxianJiesuanPrice());
				myOrderItem.setBaoxianWaimaiPrice(BigDecimal.ZERO);
				myOrderItem.setBaoxianWaimaiRefund(myOrderItem.getBaoxianWaimaiPrice());
				myOrderItem.setReturnNum(item.getNumberOfReturn() == null ? 0 : item.getNumberOfReturn());
				
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
	 * 调整金额
	 * 
	 * @param id
	 * @param adjustMoney
	 * @param session
	 * @return
	 */
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
}
