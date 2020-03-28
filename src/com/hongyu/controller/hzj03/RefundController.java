package com.hongyu.controller.hzj03;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.PurchaseController;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.BusinessOrderOutbound;
import com.hongyu.entity.BusinessOrderRefund;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Inbound;
import com.hongyu.entity.Purchase;
import com.hongyu.entity.PurchaseItem;
import com.hongyu.entity.ReturnedInboundDetail;
import com.hongyu.entity.SpecialtyPrice;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderOutboundService;
import com.hongyu.service.BusinessOrderRefundService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyVinboundService;
import com.hongyu.service.InboundService;
import com.hongyu.service.PurchaseService;
import com.hongyu.service.ReturnedInboundDetailService;
import com.hongyu.service.SpecialtyLostService;
import com.hongyu.service.SpecialtyPriceService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.util.Constants;
import com.hongyu.util.PurchaseSnGenerator;

/** 订单退款 退款入库 */
@Controller
@RequestMapping("/admin/business")
public class RefundController {

	/** 在订单表中设置 订单状态 10:待退款 */
	private final static int status_business_order_wait_for_refund = 11;

	/** 在订单表中设置 订单状态 9:待退货 */
	private final static int status_business_order_wait_for_reject = 10;

	/** 退货的损失类型固定为2(1 过期 2 破损) */
	private final static int lost_type = 2;

	/** 退货的责任方 固定为平台 */
	private final static int lost_response = 1;

	/** 退货商品表 新建记录 默认为 0待审核 */
	private final static int lost_status = 0;

	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderService;

	@Resource(name = "businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;

	@Resource(name = "businessOrderRefundServiceImpl")
	BusinessOrderRefundService businessOrderRefundService;

	@Resource(name = "specialtyLostServiceImpl")
	SpecialtyLostService specialtyLostService;

	@Resource(name = "returnedInboundDetailServiceImpl")
	ReturnedInboundDetailService returnedInboundDetailService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "specialtyServiceImpl")
	SpecialtyService specialtyService;

	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationService;

	@Resource(name = "specialtyPriceServiceImpl")
	SpecialtyPriceService specialtyPriceSrv;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name = "purchaseServiceImpl")
	PurchaseService purchaseServiceImpl;
	
	@Resource(name="commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;

	/** 订单退款-售后确认 */
	@RequestMapping(value = "/refund/confirm")
	@ResponseBody
	public Json refundSubmit(Long businessOrderRefundId) {
		Json j = new Json();

		try {
			BusinessOrderRefund businessOrderRefund = businessOrderRefundService.find(businessOrderRefundId);
			businessOrderRefund.setState(2); // 售后确认 状态变为"待消费者退货"
			businessOrderRefundService.update(businessOrderRefund);

			j.setMsg("");
			j.setSuccess(true);

		} catch (Exception e) {
			j.setMsg("");
			j.setSuccess(false);
			return j;
		}

		return j;
	}

	/** 订单退款-售后填写 消费者退货的物流信息 */
	@RequestMapping(value = "/refund/deliveryinfo")
	@ResponseBody
	public Json refundDeliveryInfo(Long businessOrderRefundId, String refundShipper, String refundShipCode) {
		Json j = new Json();

		try {
			BusinessOrderRefund businessOrderRefund = businessOrderRefundService.find(businessOrderRefundId);
			businessOrderRefund.setRefundShiper(refundShipper);// 退货物流公司
			businessOrderRefund.setRefundShipCode(refundShipCode);// 退货物流单号
			businessOrderRefund.setShipTime(new Date()); // 设定售后填写物流信息的时间为退货发货时间
			businessOrderRefund.getBusinessOrder().setOrderState(status_business_order_wait_for_reject); // 在订单表中设置状态为待退货

			businessOrderRefundService.save(businessOrderRefund);
			j.setMsg("填写退货物流信息完成");
			j.setSuccess(true);

		} catch (Exception e) {
			j.setMsg("填写退货物流信息失败");
			j.setSuccess(false);
			return j;
		}

		return j;
	}

	/** 订单退款-库管-列表 */
	@RequestMapping(value = "/refund/inbound/list")
	@ResponseBody
	public Json refundInboundList(Pageable pageable, Integer state, String orderCode, String orderPhone) {
		Json j = new Json();

		try {
			List<Filter> oFilters = new ArrayList<>();
			oFilters.add(Filter.eq("orderCode", orderCode));
			oFilters.add(Filter.eq("orderPhone", orderPhone));
			List<BusinessOrder> borders = businessOrderService.findList(null,oFilters,null);
			
			
			if(borders==null || borders.isEmpty()){
				HashMap<String, Object> obj = new HashMap<>();
				obj.put("pageSize", pageable.getRows());
				obj.put("pageNumber", pageable.getPage());
				obj.put("total", 0);
				obj.put("rows", new ArrayList<BusinessOrderRefund>());
				
				j.setSuccess(true);
				j.setMsg("没有查询到有效记录");
				j.setObj(obj);
				return j;
			}
			
			List<Filter> filters = new ArrayList<>();
			if (state != null) {
				if (state == 0) { // 待入库
					filters.add(new Filter("state", Operator.eq, 3));
				} else if (state == 1) { // 已入库
					filters.add(new Filter("state", Operator.ge, 4));
				}
			} else {
				filters.add(new Filter("state", Operator.ge, 3));// 全部
			}

			filters.add(Filter.in("businessOrder", borders));
			
			filters.add(Filter.eq("isDelivered", true));
			pageable.setFilters(filters);
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			
			Page<BusinessOrderRefund> page = businessOrderRefundService.findPage(pageable);

			if(page==null || page.getRows() == null || page.getRows().isEmpty()){
				HashMap<String, Object> obj = new HashMap<>();
				obj.put("pageSize", pageable.getRows());
				obj.put("pageNumber", pageable.getPage());
				obj.put("total", 0);
				obj.put("rows", new ArrayList<BusinessOrderRefund>());
				
				j.setSuccess(true);
				j.setMsg("没有查询到有效的数组");
				j.setObj(obj);
				return j;
			}
			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (BusinessOrderRefund b : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", b.getId());
				map.put("orderId", b.getBusinessOrder().getOrderCode()); // 订单编号
				map.put("phoneNum", b.getBusinessOrder().getOrderPhone()); // 下单手机号
				map.put("deliverType", b.getDeliverType());	//发货类型，0平台，1供应商
				map.put("orderName", b.getBusinessOrder().getWechatAccount().getWechatName()); // 微信昵称作为下单人
				map.put("receiver", b.getReceiverName()); // 发货人
				map.put("shiper", b.getRefundShiper()); // 物流公司
				map.put("shipCode", b.getRefundShipCode()); // 快递单号
				map.put("state", b.getState() == 3 ? 0 : 1); // 状态

				rows.add(map);
			}

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("pageSize", pageable.getRows());
			obj.put("pageNumber", pageable.getPage());
			obj.put("total", page.getTotal());
			obj.put("rows", rows);

			j.setMsg("查询成功");
			j.setSuccess(true);
			j.setObj(obj);
		} catch (Exception e) {
			j.setMsg("查询失败");
			j.setSuccess(false);
			return j;
		}

		return j;
	}

	/** 订单退款-库管-详情 - 未入库 */
	@RequestMapping(value = "/refund/inbound/detail")
	@ResponseBody
	public Json refundInboundDetail(Long id) {
		Json j = new Json();

		try {
			BusinessOrderRefund b = businessOrderRefundService.find(id);
			HashMap<String, Object> info = new HashMap<>();
			info.put("orderId", b.getBusinessOrder().getOrderCode()); // 订单编号
			info.put("phoneNum", b.getBusinessOrder().getOrderPhone()); // 下单手机号
			info.put("orderName", b.getBusinessOrder().getWechatAccount().getWechatName()); // 微信昵称作为下单人
			info.put("receiver", b.getReceiverName()); // 发货人
			info.put("shiper", b.getRefundShiper()); // 物流公司
			info.put("shipCode", b.getRefundShipCode()); // 快递单号
			info.put("deliverType", b.getDeliverType());	//发货类型
			info.put("state", b.getState());

			List<HashMap<String, Object>> list = new ArrayList<>();
			Set<BusinessOrderItem> set = b.getBusinessOrder().getBusinessOrderItems();

			for (BusinessOrderItem boi : set) {
				HashMap<String, Object> map = new HashMap<>();

				map.put("id", boi.getId());	//订单条目id
				map.put("productName", businessOrderItemService.getSpecialtyName(boi)); // 产品名称
				map.put("isDelivered", boi.getIsDelivered());	//是否退货
				map.put("specification", businessOrderItemService.getSpecificationName(boi)); // 产品规格
				map.put("quantity", boi.getQuantity()); // 销售数量
				map.put("returnQuantity",boi.getReturnQuantity());	//退货数量
				map.put("lost2Quantity",boi.getLost2Quantity());	//库管损失数量
				map.put("lost1Quantity", boi.getLost1Quantity());	//售后损失数量
				

				list.add(map);
			}

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("info", info);
			obj.put("list", list);

			j.setMsg("");
			j.setSuccess(true);
			j.setObj(obj);

		} catch (Exception e) {
			j.setMsg("");
			j.setSuccess(false);
			j.setObj(null);
			return j;
		}

		return j;
	}
	

	/** 订单退款-库管-详情 - 已入库 */
	@RequestMapping(value = "/refund/inbound/storeLib")
	@ResponseBody
	public Json refundInboundStoreLib(Long id) {
		Json j = new Json();

		try {
			BusinessOrderRefund b = businessOrderRefundService.find(id);
			HashMap<String, Object> info = new HashMap<>();
			info.put("orderId", b.getBusinessOrder().getOrderCode()); // 订单编号
			info.put("phoneNum", b.getBusinessOrder().getOrderPhone()); // 下单手机号
			info.put("orderName", b.getBusinessOrder().getWechatAccount().getWechatName()); // 微信昵称作为下单人
			info.put("receiver", b.getReceiverName()); // 发货人
			info.put("shiper", b.getRefundShiper()); // 物流公司
			info.put("shipCode", b.getRefundShipCode()); // 快递单号

			info.put("state", b.getState());

			List<HashMap<String, Object>> list = new ArrayList<>();
			Set<BusinessOrderItem> set = b.getBusinessOrder().getBusinessOrderItems();

			for (BusinessOrderItem boi : set) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", boi.getId());
				map.put("productName", businessOrderItemService.getSpecialtyName(boi)); // 产品名称
				map.put("isDelivered", boi.getIsDelivered());	//是否退货
				map.put("specification", businessOrderItemService.getSpecificationName(boi)); // 产品规格
				map.put("quantity", boi.getQuantity()); // 销售数量
				map.put("returnQuantity", boi.getReturnQuantity());	//退货数量
				map.put("lost2Quantity",boi.getLost2Quantity());	//库管损失数量

				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("businessOrderItem", boi));
				List<ReturnedInboundDetail> l = returnedInboundDetailService.findList(null, filters, null);
				if(l!=null && !l.isEmpty()){
					ReturnedInboundDetail r = l.get(0);

					map.put("operator", r.getOperator().getName()); // 入库人
					map.put("inboundTime", r.getInboundTime()); // 入库时间
				}
				

				list.add(map);
			}
			HashMap<String, Object> obj = new HashMap<>();
			obj.put("info", info);
			obj.put("list", list);

			j.setMsg("");
			j.setSuccess(true);
			j.setObj(obj);

		} catch (Exception e) {
			j.setMsg("");
			j.setSuccess(false);
			j.setObj(null);
			return j;
		}

		return j;
	}

	@Resource(name="businessOrderOutboundServiceImpl")
	BusinessOrderOutboundService businessOrderOutboundService;
	  
	
	/** 订单退款-库管-确认 */
	@RequestMapping(value = "/refund/inbound/confirm")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json refundInboundConfirm(Long id, HttpSession session) {

		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		Json j = new Json();
		Date d = new Date();
		try {
			// 1.修改订单退款表、订单表的状态
			BusinessOrderRefund b = businessOrderRefundService.find(id);
			b.setState(4); // 库管确认之后 状态转为 4 待财务付款
			b.setInboundTime(d); // 设置退货入库时间
			b.getBusinessOrder().setOrderState(Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REFUND); // 在订单表中设置订单状态为11待退款
			b.setState(Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_REFUND_MONEY);
			businessOrderRefundService.update(b);

			
			j.setMsg("确认成功");
			j.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg("确认失败");
			j.setSuccess(false);
			return j;
		}
		return j;
	}
	
	public static class OrderItemOutbound{
		private Long id;	//订单条目出库记录id
		private Long inboundId;	//仓库库存id
		private Long orderItemId;	//订单条目id
		private String specialtyName;	//特产名称
		private String specificationName;	//规格名称
		private Date productDate;	//生产日期
		private Integer durabilityPeriod;	//保质期
		private Integer inboundNumber;	//库存数量
		private Integer outboundQuantity;	//出库数量
		private String depotName;	//仓库名称
		private Integer returnQuantity;	//入库数量
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getInboundId() {
			return inboundId;
		}
		public void setInboundId(Long inboundId) {
			this.inboundId = inboundId;
		}
		public Long getOrderItemId() {
			return orderItemId;
		}
		public void setOrderItemId(Long orderItemId) {
			this.orderItemId = orderItemId;
		}
		public String getSpecialtyName() {
			return specialtyName;
		}
		public void setSpecialtyName(String specialtyName) {
			this.specialtyName = specialtyName;
		}
		public String getSpecificationName() {
			return specificationName;
		}
		public void setSpecificationName(String specificationName) {
			this.specificationName = specificationName;
		}
		public Date getProductDate() {
			return productDate;
		}
		public void setProductDate(Date productDate) {
			this.productDate = productDate;
		}
		public Integer getDurabilityPeriod() {
			return durabilityPeriod;
		}
		public void setDurabilityPeriod(Integer durabilityPeriod) {
			this.durabilityPeriod = durabilityPeriod;
		}
		public Integer getInboundNumber() {
			return inboundNumber;
		}
		public void setInboundNumber(Integer inboundNumber) {
			this.inboundNumber = inboundNumber;
		}
		public Integer getOutboundQuantity() {
			return outboundQuantity;
		}
		public void setOutboundQuantity(Integer outboundQuantity) {
			this.outboundQuantity = outboundQuantity;
		}
		public String getDepotName() {
			return depotName;
		}
		public void setDepotName(String depotName) {
			this.depotName = depotName;
		}
		public Integer getReturnQuantity() {
			return returnQuantity;
		}
		public void setReturnQuantity(Integer returnQuantity) {
			this.returnQuantity = returnQuantity;
		}
		
		
	}
	/** 获取某订单条目的出库记录列表 */
	@RequestMapping(value="/refund/inbound/order_item_outbound/list")
	@ResponseBody
	public Json getOrderItemOutbound(@RequestParam Long id){
		Json json = new Json();
		try{
			BusinessOrderItem orderItem = businessOrderItemService.find(id);
			if(orderItem==null){
				throw new Exception("不存在有效的订单条目信息");
			}
			List<Filter> filters1 = new ArrayList<>();
			filters1.add(Filter.eq("businessOrderItem", orderItem));
			List<BusinessOrderOutbound> outbounds = businessOrderOutboundService.findList(null,filters1,null);
			//包装返回结果
			List<OrderItemOutbound> list = new ArrayList<>();
			
			for(BusinessOrderOutbound outbound:outbounds){
				OrderItemOutbound obj = new OrderItemOutbound();
				obj.id = outbound.getId();
				obj.inboundId = outbound.getInbound().getId();
				obj.orderItemId = outbound.getBusinessOrderItem().getId();
				obj.specialtyName = outbound.getInbound().getSpecification().getSpecialty().getName();
				obj.specificationName = outbound.getInbound().getSpecification().getSpecification();
				obj.productDate = outbound.getInbound().getProductDate();
				obj.durabilityPeriod = outbound.getInbound().getDurabilityPeriod();
				obj.inboundNumber = outbound.getInbound().getInboundNumber();
				obj.outboundQuantity = outbound.getOutboundQuantity();
				obj.depotName = outbound.getDepotCode();
				obj.returnQuantity = outbound.getReturnQuantity();	
				list.add(obj);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(list);
		}catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg(e.getMessage());
			json.setObj(null);
			return json;
		}
		return json;
	}
	
	@Resource(name="inboundServiceImpl")
	InboundService inboundService;
	
	/** 平台发货条目仓库入库 */
	@RequestMapping(value="/refund/inbound/return_inbound",method=RequestMethod.POST)
	@ResponseBody
	public Json returnInbound(@RequestBody Map<String, Object> body, HttpSession session){
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		Json json = new Json();
		try{
			Long orderItemId = ((Integer)body.get("id")).longValue();
			BusinessOrderItem orderItem = businessOrderItemService.find(orderItemId);
			if(orderItem==null)
				throw new Exception("没有找到有效的订单条目");
			
			Double lost2Quantity =body.get("lost2Quantity").getClass().equals(Double.class)?
					(Double)(body.get("lost2Quantity")):
					((Integer)(body.get("lost2Quantity"))).doubleValue();
			orderItem.setLost2Quantity(lost2Quantity);	//设置库管损失数量
			List<Map<String, Object>> list = (List<Map<String,Object>>)(body.get("inboundItems"));
			//判断入库数量是否等于退货数量
			Double radio = (orderItem.getReturnQuantity()-Math.ceil(orderItem.getLost1Quantity())-
					Math.ceil(orderItem.getLost2Quantity()))*1.0/orderItem.getQuantity();	//获取应入库数量占销售数量的比例
			Map<SpecialtySpecification, Integer> outMap = new HashMap<>();	//父规格出库数量MAP
			Map<SpecialtySpecification, Integer> inMap = new HashMap<>();	//父规格入库数量MAP
			for(Map<String, Object> item:list){
				Long outboundId = ((Integer)item.get("outboundId")).longValue();
				Integer returnQuantity = (Integer)item.get("returnQuantity");

				BusinessOrderOutbound outbound = businessOrderOutboundService.find(outboundId);
				if(outbound == null){
					throw new Exception("无效的出库记录id："+outboundId);
				}
				
				SpecialtySpecification specification = outbound.getInbound().getSpecification();
				if(!outMap.containsKey(specification)){
					outMap.put(specification, 0);
				}
				if(!inMap.containsKey(specification)){
					inMap.put(specification, 0);
				}

				outMap.put(specification, outMap.get(specification)+outbound.getOutboundQuantity());
				inMap.put(specification, inMap.get(specification)+returnQuantity);
			}
			
			for(SpecialtySpecification specification:outMap.keySet()){
				Integer outQuantity = outMap.get(specification);
				Integer inQuantity = inMap.get(specification);
				if(Math.abs(inQuantity*1.0/outQuantity-radio) > 0.00000001){
					throw new Exception("入库数量不等于应退货数量");
					
				}
			}
			
			//更新父规格基本库存
			for(SpecialtySpecification sp:inMap.keySet()) {
				Integer inQuantity = inMap.get(sp);
				sp.setBaseInbound(sp.getBaseInbound()+inQuantity);
				specialtySpecificationService.update(sp);
			}
			
			
			for(Map<String, Object> item:list){
				Long outboundId = ((Integer)item.get("outboundId")).longValue();
				Integer returnQuantity = (Integer)item.get("returnQuantity");
				if(returnQuantity == 0)
					continue;	//如果入库数量为0，则不做操作
				BusinessOrderOutbound outbound = businessOrderOutboundService.find(outboundId);
				if(outbound == null){
					throw new Exception("无效的出库记录id："+outboundId);
				}
				outbound.setReturnQuantity(returnQuantity);
				Inbound inbound = outbound.getInbound();
				inbound.setInboundNumber(inbound.getInboundNumber()+returnQuantity);
				//更新库存
				inboundService.update(inbound);
				businessOrderOutboundService.update(outbound);
				
				// 2.修改退货入库明细表
				ReturnedInboundDetail returnedInboundDetail = new ReturnedInboundDetail();
				returnedInboundDetail.setBusinessOrder(orderItem.getBusinessOrder());
				returnedInboundDetail.setBusinessOrderItem(orderItem);
				returnedInboundDetail.setDepotCode(outbound.getDepotCode());
				returnedInboundDetail.setInbound(inbound);
				returnedInboundDetail.setInboundQuantity(returnQuantity);
				returnedInboundDetail.setInboundTime(new Date());
				returnedInboundDetail.setOperator(admin);
				returnedInboundDetailService.save(returnedInboundDetail);
			}
			businessOrderItemService.update(orderItem);
			json.setSuccess(true);
			json.setMsg("入库成功");
			json.setObj(null);

		}catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("入库失败");
			json.setObj(e.getStackTrace());
			return json;
		}
		return json;
	}
	
	@Resource(name= "hyVinboundServiceImpl")
	HyVinboundService hyVinboundService;
	
	/** 供货商发货仓库入库 */
	@RequestMapping(value="/refund/inbound/return_vinbound",method=RequestMethod.POST)
	@ResponseBody
	public Json returnVinbound(@RequestBody Map<String, Object> bodyObj,HttpSession session){
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		Purchase purchase = new Purchase();
		purchase.setPurchaseItems(new ArrayList<PurchaseItem>());
		BigDecimal totalMoney = new BigDecimal(0);
		
		Json json = new Json();
		try{
			List<Map<String, Object>> body = (List<Map<String,Object>>)bodyObj.get("items");
			Long id = ((Integer)bodyObj.get("id")).longValue();
			
			for(Map<String, Object> item:body){
				Long orderItemId = ((Integer)item.get("id")).longValue();
				Double lost2Quantity =item.get("lost2Quantity").getClass().equals(Double.class)?
						(Double)(item.get("lost2Quantity")):
						((Integer)(item.get("lost2Quantity"))).doubleValue();

				BusinessOrderItem orderItem = businessOrderItemService.find(orderItemId);
				
				if(orderItem==null)
					throw new Exception("没有有效的订单条目");
				
				orderItem.setLost2Quantity(lost2Quantity);
				
				//更新虚拟库存
				hyVinboundService.returnOrderItemVinbound(orderItem);
				
				
				

				businessOrderItemService.update(orderItem);
				
				//add at 2018/5/31 by zjl
				SpecialtySpecification specification = specialtySpecificationService.find(orderItem.getSpecialtySpecification());
				List<Filter> fs = new ArrayList<Filter>();
				fs.add(Filter.eq("specification", specification));
				fs.add(Filter.eq("isActive", true));
				List<Order> orders = new ArrayList<Order>();
     			orders.add(Order.desc("id"));
				List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, fs, orders);
				PurchaseItem purchaseItem = new PurchaseItem();
				purchaseItem.setCostPrice(prices.get(0).getCostPrice());
				purchaseItem.setSalePrice(prices.get(0).getPlatformPrice());
				purchaseItem.setMarketPrice(prices.get(0).getMarketPrice());
				boolean flag1 = false;
				boolean flag2 = false;
				//判断阈值
				if (new BigDecimal(orderItem.getLost1Quantity()).compareTo(new BigDecimal(0.01)) > 0) {
					flag1 = true;
				}
				if (new BigDecimal(orderItem.getLost2Quantity()).compareTo(new BigDecimal(0.01)) > 0) {
					flag2 = true;
				}
				int quantity = orderItem.getReturnQuantity();
				if (flag1) {
					quantity -= (int)Math.ceil(orderItem.getLost1Quantity());
				}
				if (flag2) {
					quantity -= (int)Math.ceil(orderItem.getLost2Quantity());
				}
				purchaseItem.setQuantity(quantity);
				purchaseItem.setSpecification(specification);
				purchaseItem.setPurchase(purchase);
				purchase.getPurchaseItems().add(purchaseItem);
				purchase.setProvider(specification.getSpecialty().getProvider());
				totalMoney = totalMoney.add(businessOrderItemService.getCostPriceOfOrderitem(orderItem).multiply(new BigDecimal(quantity)));
			}
			
			//add at 2018/5/31 by zjl
			HyAdmin creator = hyAdminService.find(Constants.REFUND_PURCHASE_ACCOUNT);
			purchase.setCreator(creator);
			synchronized (PurchaseController.lock) {
	  			  List<Filter> fs = new ArrayList<Filter>();
	  			  fs.add(Filter.in("type", SequenceTypeEnum.purchaseSn));
	  			  List<CommonSequence> ss = commonSequenceService.findList(null, fs, null);
	  			  CommonSequence c = ss.get(0);
	  			  Long value = c.getValue() + 1;
	  			  c.setValue(value);
	  			  commonSequenceService.update(c);
	  			  purchase.setPurchaseCode(PurchaseSnGenerator.getSN(value));
			}
			purchase.setTotalMoney(totalMoney);
			purchase.setAdvanceAmount(new BigDecimal(0.0));
			purchase.setPurchaseType(Constants.PURCHASE_TYPE_PAY_ON_DELIVERY);
			
			//启动退货采购的工作流，并设置必要信息
			Map<String, Object> variables = new HashMap<String,Object>();
			variables.put("applyerName", Constants.REFUND_PURCHASE_ACCOUNT);
			ProcessInstance pi= runtimeService.startProcessInstanceByKey("StockInProcess",variables); //"StockInProcess"为bpmn文件中的key
			purchase.setProcessInstanceId(pi.getProcessInstanceId());
			purchase.setStatus(Constants.PURCHASE_STATUS_INBOUNDING);
			
			// 1.修改订单退款表、订单表的状态
			BusinessOrderRefund b = businessOrderRefundService.find(id);
			b.setState(4); // 库管确认之后 状态转为 4 待财务付款
			b.setInboundTime(new Date()); // 设置退货入库时间
			b.getBusinessOrder().setOrderState(Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REFUND); // 在订单表中设置订单状态为11待退款
			b.setState(Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_REFUND_MONEY);
			businessOrderRefundService.update(b);
			purchaseServiceImpl.save(purchase);
			
			json.setSuccess(true);
			json.setMsg("入库成功");
			json.setObj(null);
			return json;
		}catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg(e.getMessage());
			json.setObj(null);
			e.printStackTrace();
			return json;
		}
		
		
	}
	
}
