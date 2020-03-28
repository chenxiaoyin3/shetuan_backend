package com.hongyu.controller.hzj03.payservicer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.PayablesLine;
import com.hongyu.entity.PayablesLineItem;
import com.hongyu.entity.PaymentSupplier;
import com.hongyu.entity.SubmitConfirm;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.PayablesLineItemService;
import com.hongyu.service.PayablesLineService;
import com.hongyu.service.PayablesRefundItemService;
import com.hongyu.service.PaymentSupplierService;

/**
 * @author xyy
 *
 * 向供应商打款
 */
@Controller
@RequestMapping("/admin/confirmList")
public class ConfirmListController {
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;

	@Resource(name = "paymentSupplierServiceImpl")
	private PaymentSupplierService paymentSupplierService;

	@Resource(name = "payablesRefundItemServiceImpl")
	private PayablesRefundItemService payablesRefundItemService;

	@Resource(name = "payablesLineItemServiceImpl")
	private PayablesLineItemService payablesLineItemService;

	@Resource(name = "payablesLineServiceImpl")
	private PayablesLineService payablesLineService;

	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;

	/** 外部供应商应付款 - 未勾选 - 列表 */
	@RequestMapping("/list/view")
	@ResponseBody
	public Json getConfirmList(Pageable pageable, PayablesLine payablesLine,
			 HttpSession session,HttpServletRequest request) {
		Json json = new Json();
		if (payablesLine == null) {
			payablesLine = new PayablesLine();
		}
		try {
			/**
			 * 获取用户权限范围
			 */
			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("date"));
			pageable.setOrders(orders);
			
			List<Filter> filters = new LinkedList<>();
			//只挑选权限下供应商的PayablesLine条目
			filters.add(Filter.in("operator", hyAdmins));
			// 必须挑选大于0的PayablesLine条目
			filters.add(Filter.gt("money", 0));
			pageable.setFilters(filters);
			
			Page<PayablesLine> page = payablesLineService.findPage(pageable, payablesLine);
			
			List<HashMap<String,Object>> list = new LinkedList<>();
			for(PayablesLine p : page.getRows()){
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", p.getId());
				map.put("servicerName", p.getServicerName());
				map.put("operator", p.getOperator().getName());
				map.put("date", p.getDate());
				map.put("money", p.getMoney());
				list.add(map);
			}
			
			HashMap<String, Object> obj = new HashMap<>();
			obj.put("list", list);
			obj.put("pageNumber", pageable.getPage());
			obj.put("rows", pageable.getRows());
			obj.put("total", page.getTotal());
			
			json.setSuccess(true);
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			e.printStackTrace();
		}

		return json;
	}

	/** 外部供应商应付款- 未勾选 - 详情 */
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json getConfirmListDetail(Long id) {
		Json json = new Json();
		HashMap<String, Object> obj = new HashMap<>();
		List<Filter> filters = new LinkedList<>();

		try {
			PayablesLine payablesLine = payablesLineService.find(id);
			// 获取供应商信息
			obj.put("supplier", payablesLine.getSupplier());
			obj.put("bankList", payablesLine.getSupplierContract().getBankList());

			List<PayablesLineItem> list = null;
			// 获取线路订单信息列表
			filters.add(Filter.eq("payablesLineId", id));
			filters.add(Filter.eq("productType", 1)); // 1线路 2酒店 3门票 4酒加景 5签证
														// 6认购门票
			filters.add(Filter.eq("state", 0)); // 0 未提交 1 已提交
			list = payablesLineItemService.findList(null, filters, null);

			List<HashMap<String, Object>> lineOrder = new LinkedList<>();
			for (PayablesLineItem p : list) {
				HashMap<String, Object> OrderItem = new HashMap<>();
				OrderItem.put("id", p.getId());
				OrderItem.put("orderNumber", p.getHyOrder().getOrderNumber());
				OrderItem.put("sn", p.getSn()); // 产品编号
				OrderItem.put("productName", p.getProductName());
				OrderItem.put("tDate", p.gettDate());
				OrderItem.put("contact", p.getHyOrder().getContact());
				OrderItem.put("lineType", p.getHyGroup().getTeamType()); // false
																			// 散客
																			// true
																			// 团队
				OrderItem.put("orderMoney", p.getOrderMoney());
				OrderItem.put("refundMoney", p.getRefunds());
				OrderItem.put("deductionPoint", p.getKoudian());
				OrderItem.put("money", p.getMoney()); // 应付款金额
				lineOrder.add(OrderItem);
			}

//			// 获取票务订单信息
//			filters.clear();
//			filters.add(Filter.eq("payablesLineId", id));
//			filters.add(Filter.gt("productType", 1)); // 2酒店 3门票 4酒加景 5签证 6认购门票
//			filters.add(Filter.eq("state", 0)); // 0 未提交 1 已提交
//			list = payablesLineItemService.findList(null, filters, null);
//
//			List<HashMap<String, Object>> ticketOrder = new LinkedList<>();
//			for (PayablesLineItem p : list) {
//				HashMap<String, Object> OrderItem = new HashMap<>();
//				OrderItem.put("id", p.getId());
//				OrderItem.put("orderNumber", p.getHyOrder().getOrderNumber());
//				OrderItem.put("sn", p.getSn()); // 产品编号
//				OrderItem.put("productName", p.getProductName());
//				OrderItem.put("tDate", p.gettDate());
//				OrderItem.put("contact", p.getHyOrder().getContact());
//				OrderItem.put("orderMoney", p.getOrderMoney());
//				OrderItem.put("refundMoney", p.getRefunds());
//				OrderItem.put("deductionPoint", p.getKoudian());
//				OrderItem.put("money", p.getMoney()); // 应付款金额
//				OrderItem.put("productType", p.getProductType());
//				ticketOrder.add(OrderItem);
//			}

//			List<PayablesRefundItem> list2 = null;
//			// 获取线路退款信息
//			filters.clear();
//			filters.add(Filter.eq("payablesLineId", id));
//			filters.add(Filter.eq("productType", 1)); // 1线路 2酒店 3门票 4酒加景 5签证
//														// 6认购门票
//			filters.add(Filter.eq("state", 0)); // 0 未提交 1 已提交
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
//			}
//
//			// 获取票务退款信息
//			filters.clear();
//			filters.add(Filter.eq("payablesLineId", id));
//			filters.add(Filter.gt("productType", 1)); // 1线路 2酒店 3门票 4酒加景 5签证
//														// 6认购门票
//			filters.add(Filter.eq("state", 0)); // 0 未提交 1 已提交
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
//			}

			
			//add by wj
			// 1线路  2酒店  3门票  4酒加景 5签证  6认购门票
			//获取酒店信息
			filters.clear();
			filters.add(Filter.eq("payablesLineId", id));
			filters.add(Filter.eq("productType", 2)); // 2酒店 3门票 4酒加景 5签证 6认购门票
			filters.add(Filter.eq("state", 0)); // 0 未提交 1 已提交
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
				
//				hotelOrderSum = hotelOrderSum.add(p.getMoney());  // 票务小计
			}
			//门票订单信息
			filters.clear();
			filters.add(Filter.eq("payablesLineId", id));
			filters.add(Filter.eq("productType", 3)); // 2酒店 3门票 4酒加景 5签证 6认购门票
			filters.add(Filter.eq("state", 0)); // 0 未提交 1 已提交
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
				
//                ticketOrderSum = ticketOrderSum.add(p.getMoney());  // 票务小计
			}
			//获取酒加景订单列表信息
			filters.clear();
			filters.add(Filter.eq("payablesLineId", id));
			filters.add(Filter.eq("productType", 4)); // 2酒店 3门票 4酒加景 5签证 6认购门票
			filters.add(Filter.eq("state", 0)); // 0 未提交 1 已提交
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
				
//                hotelAndSceneOrderSum = hotelAndSceneOrderSum.add(p.getMoney());  // 票务小计
			}
			//获取签证订单列表信息
			filters.clear();
			filters.add(Filter.eq("payablesLineId", id));
			filters.add(Filter.eq("productType", 5)); // 2酒店 3门票 4酒加景 5签证 6认购门票
			filters.add(Filter.eq("state", 0)); // 0 未提交 1 已提交
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
				
//                visaOrderSum = visaOrderSum.add(p.getMoney());  // 票务小计
			}
			
			//获取认购门票订单信息列表信息
			filters.clear();
			filters.add(Filter.eq("payablesLineId", id));
			filters.add(Filter.eq("productType", 6)); // 2酒店 3门票 4酒加景 5签证 6认购门票
			filters.add(Filter.eq("state", 0)); // 0 未提交 1 已提交
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
				
//                ticketSoldOrderSum = ticketSoldOrderSum.add(p.getMoney());  // 票务小计
			}
			
			
			
			obj.put("lineOrder", lineOrder);
			obj.put("hotelOrder", hotelOrder);
			obj.put("ticketOrder", ticketOrder);
			obj.put("hotelAndSceneOrder", hotelAndSceneOrder);
			obj.put("visaOrder", visaOrder);
			obj.put("ticketSoldOrder", ticketSoldOrder);
			
			
//			obj.put("ticketOrder", ticketOrder);
//			obj.put("lineRefund", lineRefund);
//			obj.put("ticketRefund", ticketRefund);
			
			obj.put("contractId",payablesLine.getSupplierContract().getId());
			obj.put("contractCode", payablesLine.getSupplierContract().getContractCode());

			json.setSuccess(true);
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			e.printStackTrace();
		}

		return json;
	}

	/** 供应商 - 提前打款 - 首次提交 */
	@RequestMapping("/submit")
	@ResponseBody
	public Json prePay( Long id,
	 Long contractId,	
	 Long[] lineOrderIds,
	 Long[] hotelOrderIds,
	 Long[] ticketOrderIds,
	 Long[] hotelAndSceneIds,
	 Long[] visaOrderIds,
	 Long[] ticketSoldIds,
//	 Long[] lineRefundIds,
//	 Long[] ticketRefundIds,
	 HttpSession httpSession) {
		Json json = new Json();

		try {
				
			List<Long> lineIds = null;
			List<Long> ticketIds = null;
			List<Long> hotelIds = null;
			List<Long> hotelSceneIds = null;
			List<Long> visaIds = null;
			List<Long> tSoldIds = null;
			
//			List<Long> LRefundIds = null;
//			List<Long> TRefundIds = null;

			if (lineOrderIds != null){
                lineIds = Arrays.asList(lineOrderIds);
            }
			if (ticketOrderIds != null){
                ticketIds = Arrays.asList(ticketOrderIds);
            }
			if (hotelOrderIds != null){
                hotelIds = Arrays.asList(hotelOrderIds);
            }
			if (hotelAndSceneIds != null){
                hotelSceneIds = Arrays.asList(hotelAndSceneIds);
            }
			if (visaOrderIds != null){
                visaIds = Arrays.asList(visaOrderIds);
            }
			if (ticketSoldIds != null){
                tSoldIds = Arrays.asList(ticketSoldIds);
            }

			
//			if (lineRefundIds != null)
//				LRefundIds = Arrays.asList(lineRefundIds);
//			if (ticketRefundIds != null)
//				TRefundIds = Arrays.asList(ticketRefundIds);
				
			json = paymentSupplierService.addPaymentSupplierSubmit(id, contractId, lineIds,hotelIds, ticketIds, hotelSceneIds,
					visaIds,tSoldIds, httpSession);

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			e.printStackTrace();
		}

		return json;
	}

	/** 提交人 - 供应商 - 提交的申请的列表页 */
	@RequestMapping("/applyList/view")
	@ResponseBody
	public Json getApplyList(Pageable pageable, Integer state, HttpSession session) {
		Json json = new Json();

		try {
			// 倒序
			List<Order> orders = new LinkedList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);

			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);

			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("isValid", 1)); // 有效的申请
			filters.add(Filter.eq("applySource", 1));  // 申请来源为供应商提交
			filters.add(Filter.eq("creator", hyAdmin)); // 只能看到自己提交的申请

			if (state != null) {
				if (state == 0) { // 审核中
					filters.add(Filter.eq("status", 1));
				} else if (state == 1) { // 已审核
					filters.add(Filter.gt("status", 1));
					filters.add(Filter.lt("status", 4));
				} else if (state == 2) { // 已驳回
					filters.add(Filter.eq("status", 4));
				}
			}
			pageable.setFilters(filters);
			Page<PaymentSupplier> page = paymentSupplierService.findPage(pageable);

			List<HashMap<String, Object>> list = new LinkedList<>();
			for (PaymentSupplier p : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", p.getId());
				map.put("payCode", p.getPayCode());
				map.put("supplierName", p.getSupplierName());
				map.put("operator", p.getOperator().getName());
				map.put("money", p.getMoneySum());
				map.put("applier", p.getCreator().getName());
				map.put("createTime", p.getCreateTime());
				Integer status = null;
				if (p.getStatus() == 1) {
					status = 0;
				} else if (p.getStatus() == 2 || p.getStatus() == 3) {
					status = 1;
				} else if (p.getStatus() == 4) {
					status = 2;
				}
				map.put("status", status);
				list.add(map);
			}

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("list", list);
			obj.put("total", page.getTotal());
			obj.put("page", pageable.getPage());
			obj.put("rows", pageable.getRows());

			json.setObj(obj);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("操作失败");
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
	}

	/** 提交人 - 申请列表页 - 详情 */
	@RequestMapping(value = "/submitDetail/view")
	@ResponseBody
	public Json getAuditDetail(Long id) {
		Json json = new Json();

		HashMap<String, Object> obj = new HashMap<>();

		try {
			PaymentSupplier paymentSupplier = paymentSupplierService.find(id);

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
			BigDecimal lineOrderSum = new BigDecimal(0.00);
			BigDecimal hotelOrderSum = new BigDecimal(0.00);
			BigDecimal ticketOrderSum = new BigDecimal(0.00);
			BigDecimal hotelAndSceneOrderSum = new BigDecimal(0.00);
			BigDecimal visaOrderSum = new BigDecimal(0.00);
			BigDecimal ticketSoldOrderSum = new BigDecimal(0.00);
			
//			BigDecimal ticketOrderSum = new BigDecimal(0.00);
//			BigDecimal lineRefundSum = new BigDecimal(0.00);
//			BigDecimal ticketRefundSum = new BigDecimal(0.00);

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
				HashMap<String, Object> OrderItem = new HashMap<>();
				OrderItem.put("id", p.getId());
				OrderItem.put("orderNumber", p.getHyOrder().getOrderNumber());
				OrderItem.put("sn", p.getHyGroup().getLine().getPn()); // 产品编号
				OrderItem.put("productName", p.getProductName());
				OrderItem.put("tDate", p.getHyGroup().getStartDay());
				OrderItem.put("contact", p.getHyOrder().getContact());
				OrderItem.put("lineType", p.getHyGroup().getTeamType());
				OrderItem.put("orderMoney", p.getOrderMoney());
				OrderItem.put("refundMoney", p.getRefunds());
				OrderItem.put("deductionPoint", p.getKoudian());
				OrderItem.put("money", p.getMoney()); // 应付款金额
				lineOrder.add(OrderItem);

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
//				HashMap<String, Object> OrderItem = new HashMap<>();
//				OrderItem.put("id", p.getId());
//				OrderItem.put("orderNumber", p.getHyOrder().getOrderNumber());
//				OrderItem.put("sn", p.getSn()); // 产品编号
//				OrderItem.put("productName", p.getProductName());
//				OrderItem.put("tDate", p.gettDate());
//				OrderItem.put("contact", p.getHyOrder().getContact());
//				OrderItem.put("orderMoney", p.getOrderMoney());
//				OrderItem.put("refundMoney", p.getRefunds());
//				OrderItem.put("deductionPoint", p.getKoudian());
//				OrderItem.put("money", p.getMoney()); // 应付款金额
//				ticketOrder.add(OrderItem);
//
//				ticketOrderSum = ticketOrderSum.add(p.getMoney()); // 票务小计
//			}
//
//			List<PayablesRefundItem> list2 = null;
//			// 获取线路退款信息
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
//				ticketRefundSum = ticketRefundSum.add(p.getRefundMoney()); // 票务退款小计
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
			
//			obj.put("ticketOrder", ticketOrder);
//			obj.put("lineRefund", lineRefund);
//			obj.put("ticketRefund", ticketRefund);

			obj.put("lineOrderSum", lineOrderSum);
			obj.put("hotelOrderSum", hotelOrderSum);
			obj.put("ticketOrderSum", ticketOrderSum);
			obj.put("hotelAndSceneOrderSum", hotelAndSceneOrderSum);
			obj.put("visaOrderSum", visaOrderSum);
			obj.put("ticketSoldOrderSum", ticketSoldOrderSum);
			
//			obj.put("ticketOrderSum", ticketOrderSum);
//			obj.put("lineRefundSum", lineRefundSum);
//			obj.put("ticketRefundSum", ticketRefundSum);

			obj.put("total", paymentSupplier.getMoneySum()); 
			obj.put("modified", paymentSupplier.getModified()); //是否进行账目调账
			obj.put("modifyAmount", paymentSupplier.getModifyAmount()); 
			obj.put("dismissRemark", paymentSupplier.getDismissRemark());
			obj.put("status", paymentSupplier.getStatus()); // 审核状态 

			json.setObj(obj);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			e.printStackTrace();
		}

		return json;
	}

	/** 提交人 - 申请详情页 - 驳回处理*/
	@RequestMapping("/modify")
	@ResponseBody
	public Json modifyAndSubmit(Long id,Integer type, String dismissRemark,BigDecimal modifyAmount, HttpSession session){
		Json json = new Json();
		
		try {
			json = paymentSupplierService.updateApply(id,type, dismissRemark,modifyAmount, session);
		} catch (Exception e) {
			json.setMsg("操作失败");
			json.setSuccess(false);
		}
		
		return json;
	}
	
	/**提交人 - 申请列表页 - 详情  xls导出*/
	@RequestMapping(value = "/downloadexcel")
	public void branchRechargeReviewList( @RequestBody SubmitConfirm submitConfirm,
			HttpServletRequest request,HttpServletResponse response) {
		Json json = new Json();
		try {
			List<SubmitConfirm> results = new ArrayList<SubmitConfirm>();
			results.add(submitConfirm);
			
			StringBuffer sb2 = new StringBuffer();
			sb2.append("供应商打款确认单");
			String fileName = "供应商打款确认单.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "confirmSubmit.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("下载失败");
			json.setSuccess(false);
		}
	}
}
