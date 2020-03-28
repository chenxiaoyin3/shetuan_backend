package com.hongyu.controller;

import com.hongyu.*;
import com.hongyu.controller.StoreLineOrderController.MyOrderItem;
import com.hongyu.entity.*;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.*;
import com.hongyu.util.ArrayHandler;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.SendMessageEMY;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

@Controller
@RequestMapping("admin/ticket_hotel_order_scg/")
public class HyTicketHotelOrderScgController {
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name="hyTicketHotelServiceImpl")
	private HyTicketHotelService hyTicketHotelService;
	
	@Resource(name="hyTicketHotelRoomServiceImpl")
	private HyTicketHotelRoomService hyTicketHotelRoomService;
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	@RequestMapping("page/view")
	@ResponseBody
	public Json pageView(Pageable pageable,Integer payStatus,Integer refundStatus,Integer star,
			String hotelName,String orderNumber,HttpSession session,HttpServletRequest request){
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
					"id","status","orderNumber","hotelName","star","startDate","createTime","operator"
			};
			StringBuilder totalSb = new StringBuilder("select count(*)");
			StringBuilder pageSb = new StringBuilder("select o1.id,o1.status,o1.order_number,h1.hotel_name,"
					+ "h1.star,i1.start_date,o1.createtime,o1.operator_id");
			StringBuilder sb = new StringBuilder(" from hy_order o1,hy_order_item i1,hy_ticket_hotel h1"
					+ " where o1.type=3 and o1.id=i1.order_id and i1.product_id=h1.id");

			if(payStatus!=null){
				sb.append(" and o1.paystatus="+payStatus);
			}
			if(refundStatus!=null){
				sb.append(" and o1.refundstatus="+refundStatus);
			}
			if(star!=null){
				sb.append(" and h1.star="+star);
			}
			if(hotelName!=null){
				sb.append(" and hotel_name like '%"+hotelName+"%'");
			}
			if(orderNumber!=null){
				sb.append(" and order_number like '%"+orderNumber+"%'");
			}

			if(hyAdmins!=null && !hyAdmins.isEmpty()){
				List<String> adminStrArr = new ArrayList<>();
				for(HyAdmin hyAdmin:hyAdmins){
					adminStrArr.add("'"+hyAdmin.getUsername()+"'");

				}
				String adminStr = String.join(",",adminStrArr);
				sb.append(" and o1.operator_id in ("+adminStr+")");
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

	@RequestMapping("mh/page/view")
	@ResponseBody
	public Json mhPageView(Pageable pageable,Integer payStatus,Integer refundStatus,Integer star,
	                     String hotelName,String orderNumber,HttpSession session,HttpServletRequest request){
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
//			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);

			String[] attrs = new String[]{
				"id","status","orderNumber","hotelName","star","startDate","createTime","operator"
			};
			StringBuilder totalSb = new StringBuilder("select count(*)");
			StringBuilder pageSb = new StringBuilder("select o1.id,o1.status,o1.order_number,h1.hotel_name,"
				+ "h1.star,i1.start_date,o1.createtime,o1.operator_id");
			StringBuilder sb = new StringBuilder(" from hy_order o1,hy_order_item i1,hy_ticket_hotel h1"
				+ " where o1.type=3 and o1.id=i1.order_id and i1.product_id=h1.id and o1.source=1");

			if(payStatus!=null){
				sb.append(" and o1.paystatus="+payStatus);
			}
			if(refundStatus!=null){
				sb.append(" and o1.refundstatus="+refundStatus);
			}
			if(star!=null){
				sb.append(" and h1.star="+star);
			}
			if(hotelName!=null){
				sb.append(" and hotel_name like '%"+hotelName+"%'");
			}
			if(orderNumber!=null){
				sb.append(" and order_number like '%"+orderNumber+"%'");
			}

//			if(hyAdmins!=null && !hyAdmins.isEmpty()){
//				List<String> adminStrArr = new ArrayList<>();
//				for(HyAdmin hyAdmin:hyAdmins){
//					adminStrArr.add("'"+hyAdmin.getUsername()+"'");
//
//				}
//				String adminStr = String.join(",",adminStrArr);
//				sb.append(" and o1.operator_id in ("+adminStr+")");
//			}


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
	

	@RequestMapping("gys_page/view")
	@ResponseBody
	public Json gysPageView(Pageable pageable,Integer payStatus,Integer refundStatus,Integer star,
			String hotelName,String orderNumber,HttpSession session,HttpServletRequest request){
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
					"id","status","orderNumber","hotelName","star","startDate","createTime","operator"
			};
			StringBuilder totalSb = new StringBuilder("select count(*)");
			StringBuilder pageSb = new StringBuilder("select o1.id,o1.status,o1.order_number,h1.hotel_name,"
					+ "h1.star,i1.start_date,o1.createtime,o1.operator_id");
			StringBuilder sb = new StringBuilder(" from hy_order o1,hy_order_item i1,hy_ticket_hotel h1"
					+ " where o1.type=3 and o1.id=i1.order_id and i1.product_id=h1.id");
			
			if(payStatus!=null){
				sb.append(" and o1.paystatus="+payStatus);
			}
			if(refundStatus!=null){
				sb.append(" and o1.refundstatus="+refundStatus);
			}
			if(star!=null){
				sb.append(" and h1.star="+star);
			}
			if(hotelName!=null){
				sb.append(" and hotel_name like '%"+hotelName+"%'");
			}
			if(orderNumber!=null){
				sb.append(" and order_number like '%"+orderNumber+"%'");
			}
			
			if(hyAdmins!=null && !hyAdmins.isEmpty()){
				List<String> adminStrArr = new ArrayList<>();
				for(HyAdmin hyAdmin:hyAdmins){
					adminStrArr.add("'"+hyAdmin.getUsername()+"'");
					
				}
				String adminStr = String.join(",",adminStrArr);
				sb.append(" and h1.creator in ("+adminStr+")");
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
			HyTicketHotel hyTicketHotel = hyTicketHotelService.find(hyOrderItem.getProductId());
			Map<String, Object> map = new HashMap<>();
			map.put("id", hyOrder.getId());
			map.put("orderNumber", hyOrder.getOrderNumber());
			map.put("createTime", hyOrder.getCreatetime());
			map.put("productName", hyTicketHotel.getHotelName());
			map.put("startDate", hyOrderItem.getStartDate());
			map.put("star", hyTicketHotel.getStar());
			map.put("payStatus", hyOrder.getPaystatus());
			map.put("refundStatus", hyOrder.getRefundstatus());
			map.put("productStatus", (new Date()).compareTo(hyOrderItem.getStartDate())<0?false:true);
			map.put("status", hyOrder.getStatus());
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
			map.put("contact", hyOrder.getContact());
			map.put("phone", hyOrder.getPhone());
			map.put("remark", hyOrder.getRemark());
			//调整金额
			map.put("adjustMoney",hyOrder.getAdjustMoney());
			//是否结算
			map.put("ifjiesuan", hyOrder.getIfjiesuan());
			//优惠金额
			map.put("discountedPrice", hyOrder.getDiscountedPrice());
			//离开日期
			map.put("endDate", hyOrderItem.getEndDate());
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		}catch (Exception e) {
			// TODO: handle exceptio
			json.setSuccess(true);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
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
				HyTicketHotel hyTicketHotel = hyTicketHotelService.find(hyOrderItem.getProductId());
				HyTicketHotelRoom hyTicketHotelRoom = hyTicketHotelRoomService.find(hyOrderItem.getSpecificationId());
				map.put("id", hyOrderItem.getId());
				map.put("productId", hyOrderItem.getProductId());
				map.put("productName", hyOrderItem.getName());
				map.put("roomType", hyTicketHotelRoom.getRoomType());
				map.put("quantity", hyOrderItem.getNumber());
				map.put("jiesuanMoney", hyOrderItem.getJiesuanPrice());
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
	

	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
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

	
	//门店支付订单
	@RequestMapping(value = "pay")
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
			json.setObj(e);
			e.printStackTrace();
		}
		return json;
	}
	
	@Resource(name="hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;

	// 取消酒店订单
	@RequestMapping(value = "cancel")
	@ResponseBody
	public Json cancel(Long id, HttpSession session) {
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
			if (order.getPaystatus().equals(Constants.HY_ORDER_PAY_STATUS_PAID)) {
				// 如果订单已支付
				throw new Exception("订单状态已支付，无法取消");
			}
			if (order.getStatus().equals(Constants.HY_ORDER_STATUS_CANCELED)) {
				throw new Exception("订单已经取消，不能重复取消");
			}
			
			// 设置订单状态为已取消
			order.setStatus(Constants.HY_ORDER_STATUS_CANCELED);
			
			//恢复库存
			hyTicketInboundService.recoverTicketInboundByTicketOrder(order);
			
			HyOrderApplication application = new HyOrderApplication();
			application.setContent("门店取消订单");
			application.setOperator(admin);
			application.setOrderId(id);
			application.setCreatetime(new Date());
			application.setStatus(HyOrderApplication.STATUS_ACCEPT);
			application.setType(HyOrderApplication.STORE_CANCEL_ORDER);
			hyOrderApplicationService.save(application);

			hyOrderService.update(order);
			json.setSuccess(true);
			json.setMsg("取消成功");
			json.setObj(null);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("取消失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	@Resource(name = "supplierDismissOrderApplyServiceImpl")
	SupplierDismissOrderApplyService supplierDismissOrderApplyService;

	
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
				
			} else {
				
				
				order.setStatus(Constants.HY_ORDER_STATUS_PROVIDER_ACCEPT);
				// 如果供应商通过
				//请王劼同学在此处添加逻辑
				boolean isConfirm = piaowuConfirmService.orderPiaowuConfirm(id, 2, session);
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
			BigDecimal ticketRefundPercentage = hyOrderService.getTicketRefundPercentage(order).multiply(BigDecimal.valueOf(0.01));
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
	
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Transactional
	@RequestMapping(value = "store_cancel_group/apply", method = RequestMethod.POST)
	@ResponseBody
	public Json storeCancelGroupApply(@RequestBody HyOrderApplication application, HttpSession session) {
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
			HyTicketHotel hyTicketHotel = hyTicketHotelService.find(orderItem.getProductId());
			if(hyTicketHotel==null) {
				throw new Exception("没有有效酒店");
			}



			Map<String, Object> variables = new HashMap<>();
			/**找供应商需要注意*/
			//找出供应商
			HyAdmin provider = hyTicketHotel.getCreator();
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
	
	@RequestMapping(value = "store_cancel_group/list/view")
	@ResponseBody
	public Json storeCancelGroupList(Pageable pageable, Integer status, String providerName, HttpSession session) {
		return hyOrderApplicationService.getApplicationList(pageable, status, providerName, session, 
				HyOrderApplication.STORE_CANCEL_GROUP,3);	//酒店订单类型为3
	}
	@RequestMapping(value = "store_customer_service/list/view")
	@ResponseBody
	public Json storeCustomerServiceList(Pageable pageable, Integer status, String providerName, HttpSession session) {
		return hyOrderApplicationService.getApplicationList(pageable, status, providerName, session,
				HyOrderApplication.STORE_CUSTOMER_SERVICE,3);	//酒店订单类型为3
	}
	
	
	
	
	@RequestMapping(value = { "store_cancel_group/detail/view", "store_customer_service/detail/view" })
	@ResponseBody
	public Json storeCancelGroupDetail(Long id) {
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
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	@Resource(name = "piaowuConfirmServiceImpl")
	private PiaowuConfirmService piaowuConfirmService;
	
	@RequestMapping(value = "store_cancel_group/audit", method = RequestMethod.POST)
	@ResponseBody
	public Json storeCancelGroupAudit(Long id, String comment, Integer auditStatus, HttpSession session) {
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
					BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianJiesuanMoney());
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
					hyOrderApplicationService.handleTicketHotelScg(application);
					
					application.setStatus(4);//已退款
					

					//售前退款财务审核通过，请王劼同学添加相关操作
					piaowuConfirmService.piaowuRefund(application, username, 2, "门店酒店售前退款");
					
				}

			} else {
				map.put("msg", "false");
				application.setStatus(5); // 已驳回
				HyOrder order = hyOrderService.find(application.getOrderId());
				order.setRefundstatus(4); // 退款已驳回
				hyOrderService.update(order);
				
				
				// add by wj 2019/7/20  酒店退订驳回短信提示
				HyAdmin admin = order.getOperator();
				if(admin != null){
					String phone = admin.getMobile();
					SendMessageEMY.sendMessage(phone, "", 19);
				}

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
				myOrderItem.setReturnNumber(item.getNumberOfReturn());
				myOrderItem.setNumber(item.getNumber());
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
			HyTicketHotel hyTicketHotel = hyTicketHotelService.find(orderItem.getProductId());
			if(hyTicketHotel==null) {
				throw new Exception("没有有效酒店");
			}



			Map<String, Object> variables = new HashMap<>();
			/**找供应商需要注意*/
			//找出供应商
			HyAdmin provider = hyTicketHotel.getCreator();
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
					BigDecimal tuiKuan = application.getJiesuanMoney().add(application.getBaoxianJiesuanMoney());
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
					hyOrderApplicationService.handleTicketHotelScs(application);
					
					application.setStatus(4);//已退款
					

					//售前退款财务审核通过，请王劼同学添加相关操作
					piaowuConfirmService.shouhouPiaowuRefund(application, username, 2, "门店酒店售后退款");
					
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
	

}

