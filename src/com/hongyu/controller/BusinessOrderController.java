package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.hongyu.entity.*;
import com.hongyu.service.*;
import com.hongyu.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.CharArrayMap.EntrySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.controller.BusinessOrderController.RefundObj.RefundItem;
import com.hongyu.controller.HyDepotController.WrapHyDepot.LabelValue;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.OrderTransactionSNGenerator;
import com.sun.mail.imap.protocol.Item;

import static com.hongyu.util.Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY;


@Controller
//@RequestMapping("/admin/business")
public class BusinessOrderController {

	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderServiceImpl;
	
	@Resource(name = "businessOrderRefundServiceImpl")
	BusinessOrderRefundService businessOrderRefundServiceImpl;
	
	@Resource(name="businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;

	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationService;
	  
	@Resource(name = "inboundServiceImpl")
	InboundService inboundService;
	
	@Resource(name = "hyGroupitemPromotionServiceImpl")
	HyGroupitemPromotionService hyGroupitemPromotionServiceImpl;
	
	
	@Resource(name= "hyVinboundServiceImpl")
	HyVinboundService hyVinboundService;
	
	@Resource(name="returnedInboundDetailServiceImpl")
	ReturnedInboundDetailService returnedInboundDetailSrv;
	
	@Resource(name="businessOrderOutboundServiceImpl")
	BusinessOrderOutboundService businessOrderOutboundService;
	
	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyServiceImpl;
	
	@Resource(name="hyPromotionServiceImpl")
	HyPromotionService hyPromotionService;
	
	@Resource(name="wechatAccountServiceImpl")
	WechatAccountService wechatAccountServiceImpl;
	
	@Resource(name = "orderTransactionServiceImpl")
	OrderTransactionService orderTransactionServiceImpl;
	
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationSrv;
	
	/**
	 * 订单列表
	 * 
	 * @param session
	 * @param pageable
	 * @param query
	 * @return
	 */
	@RequestMapping("/admin/business/order/page/view")
	@ResponseBody
	public Json businessOrderPage(HttpSession session, Pageable pageable, BusinessOrder query) {
		Json json = new Json();

		try {
			List<Filter> filters = new ArrayList<Filter>();
			if (StringUtils.isNotEmpty(query.getOrderPhone())) {
				filters.add(Filter.like("orderPhone", query.getOrderPhone()));
			}
			if (StringUtils.isNotEmpty(query.getOrderCode())) {
				filters.add(Filter.like("orderCode", query.getOrderCode()));
			}
			
			if (query.getOrderState() != null && query.getOrderState() == -1) {
				filters.add(Filter.ne("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_PAY));
			} else if (query.getOrderState() != null && query.getOrderState() == -2) {
				filters.add(Filter.ne("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_PAY));
				filters.add(Filter.ne("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REVIEW));
			} else if (query.getOrderState() != null && query.getOrderState() == -8) {
				filters.add(Filter.ne("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_PAY));
				filters.add(Filter.ne("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REVIEW));
				filters.add(Filter.ne("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_INBOUND));
				filters.add(Filter.ne("orderState", BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY));
				filters.add(Filter.ne("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE));
				filters.add(Filter.ne("orderState", Constants.BUSINESS_ORDER_STATUS_HAS_RECEIVED));
				filters.add(Filter.ne("orderState", Constants.BUSINESS_ORDER_STATUS_FINISH));
				filters.add(Filter.ne("orderState", Constants.BUSINESS_ORDER_STATUS_CANCELED));
			} else if (query.getOrderState() != null) {
				filters.add(Filter.eq("orderState", query.getOrderState()));
			}

			// 查询条件：客人姓名
			if(StringUtils.isNotEmpty(query.getReceiverName())){
//				List<Filter> wechatFilters = new ArrayList<>();
//				wechatFilters.add(Filter.like("wechatName",query.getReceiverName()));
//				List<WechatAccount> wechatAccounts = wechatAccountServiceImpl.findList(null,wechatFilters,null);
//				if(wechatAccounts!=null && !wechatAccounts.isEmpty()){
//					filters.add(Filter.eq("wechatAccount",wechatAccounts.get(0)));
//				}
				filters.add(Filter.like("receiverName",query.getReceiverName()));
				
			}

			// 查询条件：开始时间
			if(query.getOrderTime() != null){
				filters.add(Filter.ge("orderTime",query.getOrderTime()));
			}

			//查询条件：结束时间
			if(query.getCompleteTime() != null){
				filters.add(Filter.le("orderTime", DateUtil.getEndOfDay(query.getCompleteTime())));
			}


			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<BusinessOrder> page = businessOrderServiceImpl.findPage(pageable);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}

		return json;
	}

	@RequestMapping("/admin/business/order/modify/receive")
	@ResponseBody
	public Json modifyReceive(BusinessOrder businessOrder){
		Json json = new Json();
		try{

			if(businessOrder.getId()==null){
				throw new Exception("订单id不存在");
			}

			BusinessOrder oldOrder = businessOrderServiceImpl.find(businessOrder.getId());
			if(oldOrder == null){
				throw new Exception("所查订单不存在");
			}

			if(oldOrder.getOrderState() > BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY){
				throw new Exception("订单无法修改收获地址");
			}
			oldOrder.setReceiverName(businessOrder.getReceiverName());
			oldOrder.setReceiverAddress(businessOrder.getReceiverAddress());
			oldOrder.setReceiverPhone(businessOrder.getReceiverPhone());

			businessOrderServiceImpl.update(oldOrder);

			json.setSuccess(true);
			json.setMsg("修改成功");
			json.setObj(oldOrder);

		}catch (Exception e){

			json.setSuccess(false);
			json.setMsg("修改失败");
			json.setObj(e);
			e.printStackTrace();
		}
		return json;
	}

	@Resource(name="specialtyPriceServiceImpl")
	SpecialtyPriceService specialtyPriceServiceImpl;


	@Resource(name="weDivideModelServiceImpl")
	WeDivideModelService weDivideModelService;
	@Resource(name="weBusinessServiceImpl")
	WeBusinessService weBusinessService;
	@RequestMapping("/admin/business/order/detail/view")
	@ResponseBody
	public Json businessOrderDetail(HttpSession session, Long orderid) {
		Json json = new Json();

		try {
			BusinessOrder order = businessOrderServiceImpl.find(orderid);
			List<Filter> fs = new ArrayList<Filter>();
			fs.add(Filter.eq("businessOrder", order));
//			List<ReturnedInboundDetail> list = returnedInboundDetailSrv.findList(null, fs, null);
//			for (ReturnedInboundDetail detail : list) {
//				detail.setBusinessOrder(null);
//			}
			Map<String, Object> map = new HashMap<>();
			map.put("order",order);
			List<Map<String, Object>> orderItems = new ArrayList<>();
			for (BusinessOrderItem item : order.getBusinessOrderItems()) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("id", item.getId());
				m.put("name", businessOrderItemService.getSpecialtyName(item));
				m.put("specificationname", businessOrderItemService.getSpecificationName(item));
				m.put("purchaseItem", item.getPurchaseItem());
				m.put("quantity", item.getQuantity());
				m.put("outboundQuantity", item.getOutboundQuantity());
				m.put("outboundTime", item.getOutboundTime());
				m.put("operator", item.getOperator());
				m.put("returnQuantity", item.getReturnQuantity());
				m.put("salePrice", item.getSalePrice());
				m.put("totalSalePrice",item.getSalePrice().multiply(
					BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())));

				m.put("originalPrice", item.getOriginalPrice());
				m.put("isappraised", item.getIsappraised());
				m.put("promotionId", item.getPromotionId());
				m.put("type", item.getType());
				m.put("isDelivered", item.getIsDelivered());
				m.put("deliverName", item.getDeliverName());
				m.put("deliverType", item.getDeliverType());
				m.put("depotName", item.getDepotName());
				if (item.getPromotionId() != null) {
					m.put("promotionName", item.getPromotionId().getPromotionName());
					m.put("promotionRule", item.getPromotionId().getPromotionRule());
					m.put("promotionType", item.getPromotionId().getPromotionType());
				} else {
					m.put("promotionName", null);
					m.put("promotionRule", null);
					m.put("promotionType", null);
				}
				
				//获取可发货仓库列表
				if(item.getType()==0){	//如果是普通商品
					List<Filter> filters = new ArrayList<>();
					SpecialtySpecification specification = specialtySpecificationService.find(item.getSpecialtySpecification());

					SpecialtySpecification s = specification;
					//找价格
					//先去价格变化表里面查
					List<Filter> priceFilters=new ArrayList<Filter>();
					priceFilters.add(Filter.eq("specification", s));
					priceFilters.add(Filter.eq("isActive", true));
					List<SpecialtyPrice> specialtyPrices=specialtyPriceServiceImpl.findList(null,priceFilters,null);

					if(specialtyPrices==null || specialtyPrices.isEmpty()){
						continue;
					}
					SpecialtyPrice price=specialtyPrices.get(0);
					m.put("costPrice",price.getCostPrice());
					m.put("totalCostPrice",price.getCostPrice().multiply(
						BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())));

					s.setMarketPrice(price.getMarketPrice());
					s.setPlatformPrice(price.getPlatformPrice());
					//运费
					s.setDeliverPrice(price.getDeliverPrice());

					//找提成比例
					Long weChatId=(Long)session.getAttribute("wechat_id");
					WechatAccount wechatAccount = order.getWechatAccount();
					if(true) {

						WeBusiness weBusiness = order.getWeBusiness();

						if(weBusiness!=null){
							switch (weBusiness.getType()) {
								case 0:
									//找提成模型
									List<Filter> filters4=new ArrayList<>();
									filters4.add(Filter.eq("modelType","虹宇门店"));
									filters4.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels=weDivideModelService.findList(null,filters4,null);
									m.put("divideRatio", price.getStoreDivide().multiply(weDivideModels.get(0).getWeBusiness()));
									break;
								case 1:
									//找提成模型
									List<Filter> filters5=new ArrayList<>();
									filters5.add(Filter.eq("modelType","非虹宇门店"));
									filters5.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels1=weDivideModelService.findList(null,filters5,null);
									m.put("divideRatio", price.getExterStoreDivide().multiply(weDivideModels1.get(0).getWeBusiness()));
									break;
								case 2:
									//找提成模型
									List<Filter> filters6=new ArrayList<>();
									filters6.add(Filter.eq("modelType","个人商贸"));
									filters6.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels2=weDivideModelService.findList(null,filters6,null);
									m.put("divideRatio", price.getBusinessPersonDivide().multiply(weDivideModels2.get(0).getWeBusiness()));
									break;
								default:
									break;
							}
						}else{
							m.put("divideRatio",null);
						}
					}else {
						m.put("divideRatio",null);
					}

					//提成金额
					if(order.getBalanceMoney()==null || order.getBalanceMoney().doubleValue() == 0){

						if(m.get("divideRatio")!=null) {
							BigDecimal divideMoney = (item.getSalePrice().subtract(price.getCostPrice()).subtract(
								price.getDeliverPrice()).multiply((BigDecimal)m.get("divideRatio")));
							m.put("divideMoney",divideMoney);
							m.put("totalDivideMoney",divideMoney.multiply(
								BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
						}

					}else{
						m.put("divideMoney",0);
						m.put("totalDivideMoney",0);
					}



					Integer packNumber = specification.getSaleNumber();
					//获取父规格
					SpecialtySpecification fuSpecification = specialtySpecificationService.getParentSpecification(specification);
				
					m.put("depotList", getDepotList(fuSpecification.getId(), item.getQuantity()*packNumber));


					if(item.getPromotionId()!=null && item.getPromotionId().getDivideMoney()!=BigDecimal.ZERO){
						m.put("divideMoney",item.getPromotionId().getDivideMoney());
						m.put("totalDivideMoney",item.getPromotionId().getDivideMoney().multiply(
							BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity()
						)).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
					}
					orderItems.add(m);
					
				}else{	//如果是组合产品
					List<String> depotList = new ArrayList<>();
					//获取组合优惠活动对象
					HyGroupitemPromotion groupitemPromotion = 
							hyGroupitemPromotionServiceImpl.find(item.getSpecialty());

					if (groupitemPromotion.getBusinessPersonDivide() != null) {
						groupitemPromotion.getBusinessPersonDivide().setOperator(null);
					}
					if (groupitemPromotion.getStoreDivide() != null) {
						groupitemPromotion.getStoreDivide().setOperator(null);
					}
					if (groupitemPromotion.getExterStoreDivide() != null) {
						groupitemPromotion.getExterStoreDivide().setOperator(null);
					}

					//找提成比例
					Long weChatId=(Long)session.getAttribute("wechat_id");
					WechatAccount wechatAccount = order.getWechatAccount();


					BigDecimal costPrice = BigDecimal.ZERO; //总成本价
					BigDecimal deliverPrice = BigDecimal.ZERO;  //总运费


					for(HyGroupitemPromotionDetail detail : groupitemPromotion.getHyGroupitemPromotionDetails()) {
						SpecialtySpecification s = detail.getItemSpecificationId();
						//找价格
						//先去价格变化表里面查
						List<Filter> priceFilters = new ArrayList<Filter>();
						priceFilters.add(Filter.eq("specification", s));
						priceFilters.add(Filter.eq("isActive", true));
						List<SpecialtyPrice> specialtyPrices = specialtyPriceServiceImpl.findList(null, priceFilters, null);

						if (specialtyPrices == null || specialtyPrices.isEmpty()) {
							continue;
						}
						SpecialtyPrice price = specialtyPrices.get(0);
						s.setMarketPrice(price.getMarketPrice());
						s.setPlatformPrice(price.getPlatformPrice());
						//运费
						s.setDeliverPrice(price.getDeliverPrice());

						costPrice = costPrice.add(price.getCostPrice());
						deliverPrice = deliverPrice.add(price.getDeliverPrice());

						//找提成比例
						if (true) {
							WeBusiness weBusiness = order.getWeBusiness();

							if(weBusiness!=null){
								switch (weBusiness.getType()) {
									case 0:
										//找提成模型
										List<Filter> filters4 = new ArrayList<>();
										filters4.add(Filter.eq("modelType", "虹宇门店"));
										filters4.add(Filter.eq("isValid", true));
										List<WeDivideModel> weDivideModels = weDivideModelService.findList(null, filters4, null);
										m.put("divideRatio", price.getStoreDivide().multiply(weDivideModels.get(0).getWeBusiness()));
										break;
									case 1:
										//找提成模型
										List<Filter> filters5 = new ArrayList<>();
										filters5.add(Filter.eq("modelType", "非虹宇门店"));
										filters5.add(Filter.eq("isValid", true));
										List<WeDivideModel> weDivideModels1 = weDivideModelService.findList(null, filters5, null);
										m.put("divideRatio", price.getExterStoreDivide().multiply(weDivideModels1.get(0).getWeBusiness()));
										break;
									case 2:
										//找提成模型
										List<Filter> filters6 = new ArrayList<>();
										filters6.add(Filter.eq("modelType", "个人商贸"));
										filters6.add(Filter.eq("isValid", true));
										List<WeDivideModel> weDivideModels2 = weDivideModelService.findList(null, filters6, null);
										m.put("divideRatio", price.getBusinessPersonDivide().multiply(weDivideModels2.get(0).getWeBusiness()));
										break;
									default:
										break;
								}
							} else {
								m.put("divideRatio", null);
							}
						} else {
							m.put("divideRatio", null);
						}
						//提成金额
						if (m.get("divideRatio") != null) {
							s.setDividMoney(item.getSalePrice().subtract(price.getCostPrice()).subtract(
								price.getDeliverPrice()).multiply((BigDecimal) m.get("divideRatio")).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数。可能没用，也改一下。
						}

					}


					if(true) {
						WeBusiness weBusiness = order.getWeBusiness();

						if(weBusiness!=null){
							switch (weBusiness.getType()) {
								case 0:
									//找提成模型
									List<Filter> filters4=new ArrayList<>();
									filters4.add(Filter.eq("modelType","虹宇门店"));
									filters4.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels=weDivideModelService.findList(null,filters4,null);
									m.put("divideRatio", groupitemPromotion.getStoreDivide().getProportion().multiply(weDivideModels.get(0).getWeBusiness()));
									break;
								case 1:
									//找提成模型
									List<Filter> filters5=new ArrayList<>();
									filters5.add(Filter.eq("modelType","非虹宇门店"));
									filters5.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels1=weDivideModelService.findList(null,filters5,null);
									m.put("divideRatio", groupitemPromotion.getExterStoreDivide().getProportion().multiply(weDivideModels1.get(0).getWeBusiness()));
									break;
								case 2:
									//找提成模型
									List<Filter> filters6=new ArrayList<>();
									filters6.add(Filter.eq("modelType","个人商贸"));
									filters6.add(Filter.eq("isValid", true));
									List<WeDivideModel> weDivideModels2=weDivideModelService.findList(null,filters6,null);
									m.put("divideRatio", groupitemPromotion.getBusinessPersonDivide().getProportion().multiply(weDivideModels2.get(0).getWeBusiness()));
									break;
								default:
									break;
							}
						}else{
							m.put("divideRatio",null);
						}
					}else {
						m.put("divideRatio",null);
					}

					m.put("costPrice",costPrice);
					m.put("totalCostPrice",costPrice.multiply(
						BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())));

					//提成金额
					if(order.getBalanceMoney()==null || order.getBalanceMoney().doubleValue() == 0){

						if(m.get("divideRatio")!=null) {
							BigDecimal divideMoney = (item.getSalePrice().subtract(costPrice).subtract(
								deliverPrice).multiply((BigDecimal)m.get("divideRatio")));
							m.put("divideMoney",divideMoney);
							m.put("totalDivideMoney",divideMoney.multiply(
								BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity())).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
						}

					}else{
						m.put("divideMoney",0);
						m.put("totalDivideMoney",0);
					}

						//获取构建数量
					Integer quantity = item.getQuantity();
					//修改每件组合优惠明细条目的库存
					Set<HyGroupitemPromotionDetail> hyGroupitemPromotionDetails=
							groupitemPromotion.getHyGroupitemPromotionDetails();
					for(HyGroupitemPromotionDetail detail:hyGroupitemPromotionDetails){
						//获取明细购买数量
						
						List<Filter> filters = new ArrayList<>();
						SpecialtySpecification specification = specialtySpecificationService.find(detail.getItemSpecificationId().getId());
						
						Integer packNumber = specification.getSaleNumber();
						//获取父规格
						SpecialtySpecification fuSpecification = specialtySpecificationService.getParentSpecification(specification);
					
						List<String> subDepotList = getDepotList(fuSpecification.getId(), item.getQuantity()*packNumber);
						
						List<String> tmp = new ArrayList<>();
						if(depotList==null || depotList.isEmpty()) {
							tmp.addAll(subDepotList);
						}else {
							for(String depot:depotList) {
								for(String subDepot:subDepotList) {
									if(depot.equals(subDepot)) {
										tmp.add(depot);
										
									}
								}
							}
						}
						
						depotList = tmp;
						if(depotList == null || depotList.isEmpty()) {
							break;
						}
						
					}


					if(item.getPromotionId().getDivideMoney()!=BigDecimal.ZERO){
						m.put("divideMoney",item.getPromotionId().getDivideMoney());
						m.put("totalDivideMoney",item.getPromotionId().getDivideMoney().multiply(
							BigDecimal.valueOf(item.getQuantity()-item.getReturnQuantity()
							)).setScale(0, BigDecimal.ROUND_DOWN));//最后展示的分成钱数向下取整，舍弃小数
					}
					
					m.put("depotList",depotList);
					orderItems.add(m);
				}
				
			}
			map.put("orderItems",orderItems);
			//设置物流信息
			Map<String, Object> shipMap = new HashMap<String, Object>();
			if (order.getShip() != null) {
				shipMap.put("type", order.getShip().getType());
				shipMap.put("receiveType", order.getReceiveType());
				shipMap.put("receiverName", order.getReceiverName());
				shipMap.put("receiverPhone", order.getReceiverPhone());
				shipMap.put("receiverAddress", order.getReceiverAddress());
				shipMap.put("receiverRemark", order.getReceiverRemark());
				shipMap.put("recordTime", order.getShip().getRecordTime());
				shipMap.put("deliveror", order.getShip().getDeliverOperator().getName());
				shipMap.put("shipCompany", order.getShip().getShipCompany());
				shipMap.put("shipCode", order.getShip().getShipCode());
				map.put("ship", shipMap);
			} else {
				map.put("ship", null);
			}
			
			//退货条目
			List<Map<String, Object>> returnedInboundDetailList = new ArrayList<>();
			
			for (BusinessOrderItem item : order.getBusinessOrderItems()) {
				if (item.getReturnQuantity() != null && item.getReturnQuantity() > 0) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("itemId", item.getId());
					m.put("name", businessOrderItemService.getSpecialtyName(item));
					m.put("specification", businessOrderItemService.getSpecificationName(item));
					//普通商品
//					if(item.getType() == 0) {
//						Specialty s = specialtyServiceImpl.find(item.getSpecialty());
//						SpecialtySpecification specification = specialtySpecificationService.find(item.getSpecialtySpecification());
//						if (s != null && specification != null) {
//							m.put("name", s.getName());
//							m.put("specification", specification.getSpecification());
//						} else {
//							throw new Exception("illegal data");
//						}
//					} else if (item.getType() == 1) {  //组合优惠
//						HyPromotion promotion = item.getPromotionId();
//						if (promotion != null) {
//							m.put("name", promotion.getPromotionName());
//							
//							m.put("specification", promotion.getPromotionName());
//						} else {
//							throw new Exception("illegal data");
//						}
//					}
					m.put("inboundQuantity", item.getReturnQuantity());
					returnedInboundDetailList.add(m);
				}
			}
//			for (ReturnedInboundDetail detail : list) {
//				Map<String, Object> m = new HashMap<String, Object>();
//				m.put("returnInboundId", detail.getId());
//				//普通商品
//				if (detail.getBusinessOrderItem().getType() == 0) {
//					Specialty s = specialtyServiceImpl.find(detail.getBusinessOrderItem().getSpecialty());
//					SpecialtySpecification specification = specialtySpecificationService.find(detail.getBusinessOrderItem().getSpecialtySpecification());
//					if (s != null && specification != null) {
//						m.put("name", s.getName() + " " +specification.getSpecification());
//					} else {
//						throw new Exception("illegal data");
//					}
//				} else if (detail.getBusinessOrderItem().getType() == 1) {   //组合优惠
//					HyPromotion promotion = detail.getBusinessOrderItem().getPromotionId();
//					if (promotion != null) {
//						m.put("name", promotion.getPromotionName() + " 组合优惠" );
//					} else {
//						throw new Exception("illegal data");
//					}
//				}
//				m.put("inboundQuantity", detail.getInboundQuantity());
//				returnedInboundDetailList.add(m);
//			}
			map.put("returnedInboundList", returnedInboundDetailList);
			
			List<BusinessOrderOutbound> outbounds = new ArrayList<BusinessOrderOutbound>();
			for (BusinessOrderItem item : order.getBusinessOrderItems()) {
				fs.clear();
				fs.add(Filter.eq("businessOrderItem", item));
				List<BusinessOrderOutbound> items = businessOrderOutboundService.findList(null, fs, null);
				if (items.size() > 0) {
					outbounds.add(items.get(0));
				}
			}
			map.put("outboundList", outbounds);
			order.setBusinessOrderItems(null);
			fs.clear();
			fs.add(Filter.eq("businessOrder", order));
			List<BusinessOrderRefund> refunds = businessOrderRefundServiceImpl.findList(null, fs, null);
			if (refunds.size()>0) {
				map.put("orderRefund", refunds.get(0));
			} else {
				map.put("orderRefund", null);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}

		return json;
	}
	
	/*
	 * 同意订单退货
	 */
	
	static class RefundObj{
		public Long id;	//订单id
		public BigDecimal eRefundAmount;	//少退金额
		static class RefundItem{
			public Long id;	//条目id
			public Integer isDelivered;	//是否退货
			public Double lost1Quantity;	//售后损失数量
		}
		
		public List<RefundItem> items; 
	}
	@RequestMapping(value="/admin/business/orderreturn/confirmreturn",method=RequestMethod.POST)
	@ResponseBody
	public Json businessOrderSetReceived(HttpSession session, @RequestBody RefundObj body) {
		Json json = new Json();

		try {
			BusinessOrder order = businessOrderServiceImpl.find(body.id);
			if (order == null) {
				json.setSuccess(false);
				json.setMsg("不存在指定的订单");
				json.setObj(null);
				return json;
			}
			if (order.getOrderState() != Constants.BUSINESS_ORDER_STATUS_APPLY_RETURN_GOODS_TO_CONFIRM) {
				json.setSuccess(false);
				json.setMsg("订单当前状态未处于退货待确认状态");
				json.setObj(null);
				return json;
			}
			
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("businessOrder", order));
			List<BusinessOrderRefund> refund_list = businessOrderRefundServiceImpl.findList(null, filters, null);
			if (refund_list.size() > 0) {
				BusinessOrderRefund refund = refund_list.get(0);	//获取退款订单
				refund.seteRefundAmount(body.eRefundAmount);	//设置少退金额
				//余额支付退款金额比例
				BigDecimal radio = refund.getrRefundAmount().divide(
						refund.getRefundTotalamount(),8,BigDecimal.ROUND_HALF_DOWN);
				//实际退款总额（退款总额-少退金额）
				BigDecimal totalRefund = refund.getRefundTotalamount().subtract(body.eRefundAmount);
				refund.setRefundTotalamount(totalRefund);	//设置退款订单实际退款总额
				
				order.setRefoundMoney(totalRefund);	//设置订单实际退款总额
				
				refund.setrRefundAmount(totalRefund.multiply(radio));	//设置余额支付退款金额
				refund.setqRefundAmount(totalRefund.subtract(refund.getrRefundAmount()));	//设置其他支付退款金额
				//设置责任方，根据订单发货类型判断责任方,1平台，2供应商
				refund.setResponsibleParty(refund.getDeliverType()+1);
				
				//设置退款确认时间
				refund.setRefundAcceptTime(new Date());
				
				Boolean isDelivered = false;
				//遍历退货订单条目
				for(RefundItem item:body.items){
					BusinessOrderItem orderItem = businessOrderItemService.find(item.id);
					orderItem.setIsDelivered(item.isDelivered);	//设置订单条目是否退货
					if(item.isDelivered == 1)isDelivered = true;
					orderItem.setLost1Quantity(item.lost1Quantity);	//售后损失数量
					businessOrderItemService.update(orderItem);
				}
				refund.setIsDelivered(isDelivered);	//设置退款订单是否退货
				
				//设置退款订单状态
				if (refund.getIsDelivered()) {
					order.setOrderState(Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_RETURN_GOODS);
					refund.setState(Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_RETURN_PRODUCT);
				} else {
					order.setOrderState(Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REFUND);
					refund.setState(Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_REFUND_MONEY);
				}
				businessOrderRefundServiceImpl.update(refund);
				businessOrderServiceImpl.update(order);
			} else {
				throw new Exception("缺少退款单！");
			}
			
			
			json.setSuccess(true);
			json.setMsg("设置成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("设置失败");
			json.setObj(e);
			e.printStackTrace();
		}

		return json;
	}
	
	/*
	 * 填写退货物流
	 */
	@RequestMapping("/admin/business/orderreturn/returnship")
	@ResponseBody
	public Json businessOrderSetReceived(HttpSession session, Long orderid, String shiper, String shipcode, BigDecimal shipFee, String receiverName, String receiverPhone) {
		Json json = new Json();

		try {
			BusinessOrder order = businessOrderServiceImpl.find(orderid);
			if (order == null) {
				json.setSuccess(false);
				json.setMsg("不存在指定的订单");
				json.setObj(null);
				return json;
			}
			if (order.getOrderState() != Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_RETURN_GOODS) {
				json.setSuccess(false);
				json.setMsg("订单当前状态未处于等待退货的状态");
				json.setObj(null);
				return json;
			}
			
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("businessOrder", order));
			List<BusinessOrderRefund> refund_list = businessOrderRefundServiceImpl.findList(null, filters, null);
			if (refund_list.size() == 0)
				throw new Exception("缺少退款单");
			BusinessOrderRefund refund = refund_list.get(0);
			refund.setState(Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_RETURN_INBOUND);
			refund.setRefundShiper(shiper);
			refund.setRefundShipCode(shipcode);
			refund.setReceiverName(receiverName);
			refund.setReceivePhone(receiverPhone);
			refund.setRefundShipFee(shipFee);
			refund.setShipTime(new Date());
			businessOrderRefundServiceImpl.save(refund);
			
			//入库完成后才设置为退货待入库
			order.setOrderState(Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_RETURN_GOODS_INBOUND);
			businessOrderServiceImpl.update(order);
			json.setSuccess(true);
			json.setMsg("设置成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("设置失败");
			json.setObj(e);
			e.printStackTrace();
		}

		return json;
	}
	/*
	 * 审核订单通过
	 */
	
	//建立订单审核内部类
	public static class VerifyOrder{
		public Long id;
		public Boolean isDivided;
		public static class OrderItem{
			public Long id;
			public Integer deliverType;
			public String depotName;
		}
		public List<OrderItem> items;
	}
	
	//接口
	@RequestMapping(value="/admin/business/order/verify_agree",method=RequestMethod.POST)
	@ResponseBody
	public Json verifyAgree(@RequestBody VerifyOrder body,HttpSession session){
		
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		
		Json json=new Json();
		
		try {
			BusinessOrder order = businessOrderServiceImpl.find(body.id);	//获取订单
			if(order==null){
				json.setSuccess(false);
				json.setMsg("订单不存在");
				json.setObj(null);
				return json;
			}
			if(order.getOrderState()!=Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REVIEW){
				json.setSuccess(false);
				json.setMsg("订单状态不对");
				json.setObj(null);
				return json;
			}
			
			Map<String, List<BusinessOrderItem>> map=new HashMap<>();
			//构建发货商-list<businessOrderItem> map
			for(VerifyOrder.OrderItem item:body.items){
				BusinessOrderItem orderItem = businessOrderItemService.find(item.id);
//				if(item.deliverType==0 || orderItem.getType()== 1){ //如果由平台发货或是组合产品
//					orderItem.setDeliverType(0);	//设置由平台发货
//					orderItem.setDepotName(item.depotName);	//设置发货仓库
//					if(map.containsKey("平台")==false){
//						map.put("平台", new ArrayList<BusinessOrderItem>());
//					}
//					map.get("平台").add(orderItem);	//添加orderItem到相应供应商list中
//				}else{
//					orderItem.setDeliverType(1);	//设置由供货商发货
//					orderItem.setDepotName(null);	//设置发货仓库为null
//					String deliverName = orderItem.getDeliverName();
//					if(deliverName == null){
//						json.setSuccess(false);
//						json.setMsg("数据异常，订单条目供应商不存在");
//						json.setObj(null);
//						return json;
//					}
//					if(map.containsKey(deliverName)==false){
//						map.put(deliverName, new ArrayList<BusinessOrderItem>());
//					}
//					map.get(deliverName).add(orderItem);	//添加orderItem到相应供应商list中
//				}
				//改成组合优惠也可以供应商发货
				if(item.deliverType==0){ //如果由平台发货
					orderItem.setDeliverType(0);	//设置由平台发货
					orderItem.setDepotName(item.depotName);	//设置发货仓库
					if(map.containsKey("平台")==false){
						map.put("平台", new ArrayList<BusinessOrderItem>());
					}
					map.get("平台").add(orderItem);	//添加orderItem到相应供应商list中
				}else{
					orderItem.setDeliverType(1);	//设置由供货商发货
					orderItem.setDepotName(null);	//设置发货仓库为null
					String deliverName = orderItem.getDeliverName();
					//如果是组合优惠供应商发货
					if(orderItem.getType()==1){
						//判断组成组合优惠的产品是否是一个供应商
						HyGroupitemPromotion hyGroupitemPromotion = hyGroupitemPromotionServiceImpl.find(orderItem.getSpecialty());
						if(hyGroupitemPromotion==null){
							json.setSuccess(false);
							json.setMsg("数据异常，订单条目组合优惠不存在");
							json.setObj(null);
							return json;
						}
						java.util.Iterator<HyGroupitemPromotionDetail> hyGroupitemPromotionDetailIterator = hyGroupitemPromotion.getHyGroupitemPromotionDetails().iterator();
						if(hyGroupitemPromotionDetailIterator.hasNext()){
							deliverName = hyGroupitemPromotionDetailIterator.next().getItemId().getProvider().getProviderName();
						}
						while(hyGroupitemPromotionDetailIterator.hasNext()){
							Provider tmpProvider = hyGroupitemPromotionDetailIterator.next().getItemId().getProvider();
							if (!deliverName.equals(tmpProvider.getProviderName())) {
								json.setSuccess(false);
								json.setMsg("审核失败。组成组合优惠产品由多个供应商供货，不能供应商发货。");
								json.setObj(null);
								return json;
							}
						}
						orderItem.setDeliverName(deliverName);//会持久化
					}
					if(deliverName == null){
						json.setSuccess(false);
						json.setMsg("数据异常，订单条目供应商不存在");
						json.setObj(null);
						return json;
					}
					if(map.containsKey(deliverName)==false){
						map.put(deliverName, new ArrayList<BusinessOrderItem>());
					}
					map.get(deliverName).add(orderItem);	//添加orderItem到相应供应商list中
				}
			}
			
			//判断是否有平台发货，库存是否不足
			if(map.containsKey("平台")){
				List<BusinessOrderItem> items = map.get("平台");
				BusinessOrderItem item = inboundService.isInboundEnough(items);
				if(item!=null){
					//判断库存，如果库存不足
					json.setSuccess(false);
					json.setMsg("库存不足");
					json.setObj(item);
					return json;
				}
			}
			
			
			order.setReviewer(admin);	//设置审核人
			order.setIsDivided(body.isDivided);	//设置是否由供应商发货
			order.setParentOrderId(0L);	//设置父订单id为0
			//如果map长度为一
			if(map.size()==1){
				//不需要拆分订单
				order.setIsShow(false);	//不是原始凭据
				order.setOrderState(map.containsKey("平台")?Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_INBOUND:
					BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY);
				order.setAuditStatus(1);
				order.setReviewTime(new Date());
				
			}else{	//否则需要拆分订单
				order.setIsShow(true);	//设置为是原始凭证
				for(Entry<String, List<BusinessOrderItem>> item:map.entrySet()){
					//创建子订单
					BusinessOrder subOrder = businessOrderServiceImpl.createSubOrder(order,item.getKey(),item.getValue());
				}
				order.setOrderState(Constants.BUSINESS_ORDER_STATUS_FINISH);	//设置父订单为完成状态
				order.setReviewTime(new Date());
			}
			
			businessOrderServiceImpl.save(order);	//保存父订单信息
			
			//修改所有子订单条目库存
			for(Map.Entry<String,List<BusinessOrderItem>> entry:map.entrySet()){
				if(!entry.getKey().equals("平台")){	//如果由供应商发货
					List<BusinessOrderItem> items = entry.getValue();
					for(BusinessOrderItem item:items){
						hyVinboundService.updateOrderItemVinbound(item);
						item.setOutboundQuantity(item.getQuantity());
						item.setOperator(username);
					}
				}
			}
			
			json.setSuccess(true);
			json.setMsg("审核通过");
			json.setObj(null);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg(e.getMessage());
			json.setObj(null);
			return json;
		}
		
		
		return json;
	}

	@Autowired
	BusinessOrderOutboundService orderOutboundService;

	@Autowired
	WechatAccountBalanceService wechatAccountBalanceService;


	@RequestMapping(value = { "/admin/business/order/cancel" })
	@ResponseBody
	Json cancel(@RequestParam("order_id") Long id) {
		Json json = new Json();
		try {
			BusinessOrder businessOrder = businessOrderServiceImpl.find(id);
			if (businessOrder == null) {
				json.setSuccess(true);
				json.setMsg("订单不存在");
				json.setObj(null);
			}

			int state = businessOrder.getOrderState();
			if (state <= Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY) {

				//恢复规格商品的库存和销量
				for(BusinessOrderItem orderItem:businessOrder.getBusinessOrderItems()){
					specialtySpecificationService.updateBaseInboundAndHasSold(orderItem, false);

					//修改优惠数量
					Boolean isGroupPromotion = orderItem.getType()==0?false:true;
					//2019-04-28改：组合优惠相关数量的修改在specialtySpecificationService.updateBaseInboundAndHasSold中进行。
					if (!isGroupPromotion) {// 如果是普通产品
						if(orderItem.getPromotionId()!=null) {
							// 获取购买数量
							Integer quantity = (Integer) orderItem.getQuantity();
							// 获取优惠明细
							HySingleitemPromotion singleitemPromotion = hySingleitemPromotionServiceImpl.getValidSingleitemPromotion(
								orderItem.getSpecialtySpecification(),orderItem.getPromotionId().getId());
							// 如果参加了优惠活动
							if (singleitemPromotion != null) {

								// 修改优惠数量
								singleitemPromotion.setPromoteNum(singleitemPromotion.getPromoteNum() + quantity);
								singleitemPromotion.setHavePromoted(singleitemPromotion.getHavePromoted() - quantity);

								hySingleitemPromotionServiceImpl.update(singleitemPromotion);
							}
						}
					}
				}

				//发货类型是平台，状态是待发货的时候可以取消订单，需要将库存加回去
				if(state == Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY &&
					businessOrder.getIsDivided() == false){

					Set<BusinessOrderItem> orderItems = businessOrder.getBusinessOrderItems();

					List<Filter> outboundFilters = new ArrayList<>();
					outboundFilters.add(Filter.in("businessOrderItem",orderItems));

					List<BusinessOrderOutbound> orderOutbounds =
						orderOutboundService.findList(null,outboundFilters,null);

					for(BusinessOrderOutbound orderOutbound:orderOutbounds){

						Inbound inbound = orderOutbound.getInbound();

						inbound.setInboundNumber(inbound.getInboundNumber()+orderOutbound.getOutboundQuantity());
						inbound.setSaleNumber(inbound.getSaleNumber()-orderOutbound.getOutboundQuantity());

						inboundService.update(inbound);
						orderOutbound.setIsValid(false);
						orderOutboundService.update(orderOutbound);

					}

				}



				// 如果是未支付或未出库，则可以取消订单
				// 如果使用了余额支付，将余额退回用户
				if(state>Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_PAY) {
					//如果已支付，要退款

					// BusinessOrderRefund Entity
					BusinessOrderRefund bRefund = new BusinessOrderRefund();
					BusinessOrder bOrder = businessOrder;

					bRefund.setBusinessOrder(bOrder); // 订单
					// bRefund.setIsDelivered(isDelivered); // 是否退货
					bRefund.setDeliverType(0); // 发货类型

					bRefund.setRefundReason("取消订单"); // 退款理由

					Date refundDate = new Date();
					bRefund.setRefundApplyTime(refundDate); // 退款申请时间
					bRefund.setRefundAcceptTime(refundDate);	//退款确认时间
					bRefund.setInboundTime(refundDate);	//设置入库时间
					bRefund.setShipTime(refundDate);	//设置物流时间
					bRefund.setReturnCompleteTime(refundDate);	//设置退货完成世界

					bRefund.setWechat(bOrder.getWechatAccount()); // 下单微信账户

					/* 退款金额以及设置退货条目 */
					BigDecimal orderTotal = bOrder.getTotalMoney().add(bOrder.getShipFee()); // 订单优惠前总额
					BigDecimal orderBalance = bOrder.getBalanceMoney(); // 余额支付金额
					BigDecimal orderPay = bOrder.getPayMoney(); // 其他支付金额

					BigDecimal rRefundAmount = orderBalance; // 余额支付退款金额
					BigDecimal qRefundAmount = orderPay; // 其他支付退款金额
					BigDecimal refundAmount = rRefundAmount.add(qRefundAmount); // 货物退款金额

					bRefund.setrRefundAmount(rRefundAmount); // 设置余额支付退款金额
					bRefund.setqRefundAmount(qRefundAmount); // 设置其他支付退款金额
					bRefund.setRefundAmount(refundAmount); // 设置货物退款金额
					bRefund.seteRefundAmount(BigDecimal.ZERO);	//设置少退金额
					bRefund.setRefundShipFee(BigDecimal.ZERO);	//设置退货物流费
					bRefund.setRefundTotalamount(refundAmount); // 设置应退款总额


					bRefund.setIsDelivered(false);	//设置是否退货
					bRefund.setResponsibleParty(1);	//设置责任方
					bRefund.setState(Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_REFUND_MONEY); // 状态为待退款
					businessOrderRefundServiceImpl.save(bRefund); // 保存退款订单信息

					bOrder.setRefoundMoney(refundAmount); // 订单设置退款金额
					bOrder.setOrderState(Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REFUND); // 设置订单为待退款状态
					businessOrderServiceImpl.save(bOrder); // 保存订单信息



//					if(!businessOrder.getBalanceMoney().equals(BigDecimal.ZERO)){
//						WechatAccount account = businessOrder.getWechatAccount();
//						account.setTotalbalance(account.getTotalbalance().add(businessOrder.getBalanceMoney()));
//						wechatAccountService.update(account);
//					}
//					//生成交易记录
//					OrderTransaction transaction = new OrderTransaction();
//					transaction.setBusinessOrder(businessOrder);
//					transaction.setSerialNum(OrderTransactionSNGenerator.getSN(true));
//					transaction.setWechatBalance(businessOrder.getBalanceMoney());
//					transaction.setPayAccount(businessOrder.getWechatAccount().getWechatOpenid());
//					transaction.setPayment(businessOrder.getPayMoney());
//					//微信支付
//					transaction.setPayType(1);
//					transaction.setPayment(businessOrder.getPayMoney());
//					//退款
//					transaction.setPayFlow(2);
//					transaction.setPayTime(businessOrder.getPayTime());
//					orderTransactionServiceImpl.save(transaction);
				}else {
					WechatAccount account = businessOrder.getWechatAccount();
					//恢复账户余额并保存
					account.setTotalbalance(account.getTotalbalance().add(businessOrder.getBalanceMoney()));
					wechatAccountServiceImpl.update(account);
					if(businessOrder.getBalanceMoney() != null && businessOrder.getBalanceMoney().compareTo(BigDecimal.ZERO) > 0){
						WechatAccountBalance wechatAccountBalance = new WechatAccountBalance();
						wechatAccountBalance.setWechatAccountId(businessOrder.getWechatAccount().getId());
						wechatAccountBalance.setType(WechatAccountBalance.WechatAccountBalanceType.refund);
						wechatAccountBalance.setCreateTime(new Date());
						wechatAccountBalance.setAmount(businessOrder.getBalanceMoney());
						wechatAccountBalance.setSurplus(account.getTotalbalance());
						wechatAccountBalanceService.save(wechatAccountBalance);
					}
					//取消订单
					businessOrder.setOrderState(Constants.BUSINESS_ORDER_STATUS_CANCELED);
					businessOrder.setOrderCancelTime(new Date());
					businessOrderServiceImpl.save(businessOrder);
				}

				json.setSuccess(true);
				json.setMsg("取消订单成功");
				Map<String, Object> obj = new HashMap<>();
				obj.put("id", businessOrder.getId());
				obj.put("state", businessOrder.getOrderState());
				json.setObj(obj);
			} else {
				json.setSuccess(true);
				json.setMsg("订单状态错误，无法取消订单");
				Map<String, Object> obj = new HashMap<>();
				obj.put("id", businessOrder.getId());
				obj.put("state", businessOrder.getOrderState());
				json.setObj(obj);
			}

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("发生异常");
			json.setObj(e);
		}
		return json;
	}


	@Resource(name = "hySingleitemPromotionServiceImpl")
	HySingleitemPromotionService hySingleitemPromotionServiceImpl;

	@Resource(name = "couponGiftServiceImpl")
	CouponGiftService couponGiftService;

	/**
	 * 拒审订单
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/admin/business/order/verify_deny")
	@ResponseBody
	public Json verifyDeny(Long id) {
		Json json = new Json();
		try {
			BusinessOrder businessOrder = businessOrderServiceImpl.find(id);
			if (businessOrder == null) {
				json.setSuccess(true);
				json.setMsg("订单不存在");
				json.setObj(null);
			}

			int state = businessOrder.getOrderState();
			if (state == Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REVIEW) {
				
				//恢复规格商品的库存和销量
				for(BusinessOrderItem orderItem:businessOrder.getBusinessOrderItems()){
					specialtySpecificationService.updateBaseInboundAndHasSold(orderItem, false);
					
					//修改优惠数量
					Boolean isGroupPromotion = orderItem.getType()==0?false:true;
					//2019-04-28改：组合优惠相关数量的修改在specialtySpecificationService.updateBaseInboundAndHasSold中进行。
					if (!isGroupPromotion) {// 如果是普通产品
						if(orderItem.getPromotionId()!=null) {
							// 获取购买数量
							Integer quantity = (Integer) orderItem.getQuantity();
							// 获取优惠明细
							HySingleitemPromotion singleitemPromotion = hySingleitemPromotionServiceImpl.getValidSingleitemPromotion(
									orderItem.getSpecialtySpecification(),orderItem.getPromotionId().getId());
							// 如果参加了优惠活动
							if (singleitemPromotion != null) {

								// 修改优惠数量
								singleitemPromotion.setPromoteNum(singleitemPromotion.getPromoteNum() + quantity);
								singleitemPromotion.setHavePromoted(singleitemPromotion.getHavePromoted() - quantity);
								
								hySingleitemPromotionServiceImpl.update(singleitemPromotion);
							}
						}
					}
				}
				
				// 如果是未支付或未出库，则可以取消订单
				// 如果使用了余额支付，将余额退回用户
				if(state>Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_PAY) {
					//如果已支付，要退款
					
					// BusinessOrderRefund Entity
					BusinessOrderRefund bRefund = new BusinessOrderRefund();
					BusinessOrder bOrder = businessOrder;

					bRefund.setBusinessOrder(bOrder); // 订单
					// bRefund.setIsDelivered(isDelivered); // 是否退货
					bRefund.setDeliverType(0); // 发货类型

					bRefund.setRefundReason("订单拒审"); // 退款理由
					
					Date refundDate = new Date();
					bRefund.setRefundApplyTime(refundDate); // 退款申请时间
					bRefund.setRefundAcceptTime(refundDate);	//退款确认时间
					bRefund.setInboundTime(refundDate);	//设置入库时间
					bRefund.setShipTime(refundDate);	//设置物流时间
					bRefund.setReturnCompleteTime(refundDate);	//设置退货完成世界
					
					bRefund.setWechat(bOrder.getWechatAccount()); // 下单微信账户

					/* 退款金额以及设置退货条目 */
					BigDecimal orderTotal = bOrder.getTotalMoney().add(bOrder.getShipFee()); // 订单优惠前总额
					BigDecimal orderBalance = bOrder.getBalanceMoney(); // 余额支付金额
					BigDecimal orderPay = bOrder.getPayMoney(); // 其他支付金额
					
					BigDecimal rRefundAmount = orderBalance; // 余额支付退款金额
					BigDecimal qRefundAmount = orderPay; // 其他支付退款金额
					BigDecimal refundAmount = rRefundAmount.add(qRefundAmount); // 货物退款金额

					bRefund.setrRefundAmount(rRefundAmount); // 设置余额支付退款金额
					bRefund.setqRefundAmount(qRefundAmount); // 设置其他支付退款金额
					bRefund.setRefundAmount(refundAmount); // 设置货物退款金额
					bRefund.seteRefundAmount(BigDecimal.ZERO);	//设置少退金额
					bRefund.setRefundShipFee(BigDecimal.ZERO);	//设置退货物流费
					bRefund.setRefundTotalamount(refundAmount); // 设置应退款总额
					

					bRefund.setIsDelivered(false);	//设置是否退货
					bRefund.setResponsibleParty(1);	//设置责任方
					bRefund.setState(Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_REFUND_MONEY); // 状态为待退款
					businessOrderRefundServiceImpl.save(bRefund); // 保存退款订单信息

					bOrder.setRefoundMoney(refundAmount); // 订单设置退款金额
					bOrder.setOrderState(Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REFUND); // 设置订单为待退款状态
					bOrder.setAuditStatus(2); //已拒审
					businessOrderServiceImpl.save(bOrder); // 保存订单信息

					
					
//					if(!businessOrder.getBalanceMoney().equals(BigDecimal.ZERO)){
//						WechatAccount account = businessOrder.getWechatAccount();
//						account.setTotalbalance(account.getTotalbalance().add(businessOrder.getBalanceMoney()));
//						wechatAccountService.update(account);
//					}
//					//生成交易记录
//					OrderTransaction transaction = new OrderTransaction();
//					transaction.setBusinessOrder(businessOrder);
//					transaction.setSerialNum(OrderTransactionSNGenerator.getSN(true));
//					transaction.setWechatBalance(businessOrder.getBalanceMoney());
//					transaction.setPayAccount(businessOrder.getWechatAccount().getWechatOpenid());
//					transaction.setPayment(businessOrder.getPayMoney());
//					//微信支付
//					transaction.setPayType(1);
//					transaction.setPayment(businessOrder.getPayMoney());
//					//退款
//					transaction.setPayFlow(2);
//					transaction.setPayTime(businessOrder.getPayTime());
//					orderTransactionServiceImpl.save(transaction);
				}else {
					WechatAccount account = businessOrder.getWechatAccount();
					//恢复账户余额并保存
					account.setTotalbalance(account.getTotalbalance().add(businessOrder.getBalanceMoney()));
					wechatAccountServiceImpl.update(account);
					//取消订单
					businessOrder.setOrderState(Constants.BUSINESS_ORDER_STATUS_CANCELED);
					businessOrder.setOrderCancelTime(new Date());
					businessOrder.setAuditStatus(2); //已拒审
					businessOrderServiceImpl.save(businessOrder);
				}

				//恢复电子券
				if(businessOrder.getCouponId()!=null && businessOrder.getCouponId()!=""){
					String[] couponIdStrs = businessOrder.getCouponId().split(";");
					for(int i=0;i<couponIdStrs.length;i++){
						CouponGift coupon = couponGiftService.find(Long.valueOf(couponIdStrs[i]));
						if (coupon != null) {
							// 设置为已使用
							coupon.setState(0/* 未使用 */);
							// 设置使用时间
							coupon.setUseTime(null);
							couponGiftService.update(coupon);
						}
					}
				}
				json.setSuccess(true);
				json.setMsg("订单拒审成功");
				Map<String, Object> obj = new HashMap<>();
				obj.put("id", businessOrder.getId());
				obj.put("state", businessOrder.getOrderState());
				json.setObj(obj);
			} else {
				json.setSuccess(true);
				json.setMsg("订单状态错误，无法拒审订单");
				Map<String, Object> obj = new HashMap<>();
				obj.put("id", businessOrder.getId());
				obj.put("state", businessOrder.getOrderState());
				json.setObj(obj);
			}

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("发生异常");
			json.setObj(e);
		}
		return json;
	}
	
	
	public List<String> getDepotList(Long specificationId,Integer quantity){
		List<Inbound> inbounds = inboundService.getInboundListBySpecificationId(specificationId,0);
		
		Map<String, Integer> map = new HashMap<>();
		for(Inbound inbound:inbounds){
			if(!map.containsKey(inbound.getDepotCode())){
				map.put(inbound.getDepotCode(), 0);
			}
			String depotName = inbound.getDepotCode();
			map.put(depotName, map.get(depotName)+inbound.getInboundNumber());
		}
		
		List<String> list = new ArrayList<>();
		for(Map.Entry<String, Integer> entry:map.entrySet()){
			if(entry.getValue()>=quantity){
				list.add(entry.getKey());
			}
		}
		
		return list;
	}
	

	/*
	 * 设置订单状态为已退款状态
	 */
	@RequestMapping("/admin/business/orderreturn/confirmrefund")
	@ResponseBody
	public Json businessOrderSetRefund(HttpSession session, Long orderid) {
		Json json = new Json();

		try {
			BusinessOrder order = businessOrderServiceImpl.find(orderid);
			if (order == null) {
				json.setSuccess(false);
				json.setMsg("不存在指定的订单");
				json.setObj(null);
				return json;
			}
			if (order.getOrderState() != Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_REFUND) {
				json.setSuccess(false);
				json.setMsg("订单当前状态不可设置为已退款状态");
				json.setObj(null);
				return json;
			}

			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("businessOrder", order));
			List<BusinessOrderRefund> refund_list = businessOrderRefundServiceImpl.findList(null, filters, null);
			if (refund_list.size() == 0)
				throw new Exception("缺少退款单");
			BusinessOrderRefund refund = refund_list.get(0);
			//退还余额到
			WechatAccount account = order.getWechatAccount();
			account.setTotalbalance(account.getTotalbalance().add(refund.getrRefundAmount()));
			
			order.setOrderState(Constants.BUSINESS_ORDER_STATUS_FINISH_REFUND);
			order.setCompleteTime(new Date());
			
			//生成交易记录
			OrderTransaction transaction = new OrderTransaction();
			transaction.setBusinessOrder(order);
			transaction.setSerialNum(OrderTransactionSNGenerator.getSN(true));
			transaction.setWechatBalance(order.getBalanceMoney());
			transaction.setPayAccount(order.getWechatAccount().getWechatOpenid());
			transaction.setPayment(order.getPayMoney());
			//微信支付
			transaction.setPayType(1);
			transaction.setPayment(order.getPayMoney());
			//退款
			transaction.setPayFlow(2);
			transaction.setPayTime(order.getPayTime());
			orderTransactionServiceImpl.save(transaction);
			businessOrderServiceImpl.update(order);
			wechatAccountServiceImpl.update(account);
			
			json.setSuccess(true);
			json.setMsg("设置成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("设置失败");
			json.setObj(null);
			e.printStackTrace();
		}

		return json;
	}
	
	@Resource(name = "twiceConsumeStatisServiceImpl")
	TwiceConsumeStatisService twiceConsumeStatisService;
	
	@Resource(name = "twiceConsumeRecordServiceImpl")
	TwiceConsumeRecordService twiceConsumeRecordService;
	
	/*
	 * 确认订单完成
	 */
	@RequestMapping("/admin/business/order/confirm_finish")
	@ResponseBody
	public Json businessOrderConfirm(HttpSession session, Long orderid) {
		Json json = new Json();

		try {
			BusinessOrder order = businessOrderServiceImpl.find(orderid);
			if(!order.getOrderState().equals(Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_RECEIVE) ||
					!order.getOrderState().equals(Constants.BUSINESS_ORDER_STATUS_HAS_RECEIVED)) {
				json.setSuccess(false);
				json.setMsg("订单状态不对");
				json.setObj(null);
				return json;
			}
			for (BusinessOrderItem item : order.getBusinessOrderItems()) {
				//修改已售
				if (item.getType() == 0) {
					if (item.getSpecialtySpecification() != null) {
						SpecialtySpecification spec = specialtySpecificationSrv.find(item.getSpecialtySpecification());
						if (spec != null) {
							spec.setHasSold(spec.getHasSold()+item.getQuantity()-item.getReturnQuantity());
							specialtySpecificationSrv.save(spec);
						}
					}
				}
			}
			order.setCompleteTime(new Date());
			order.setOrderState(Constants.BUSINESS_ORDER_STATUS_FINISH);
			businessOrderServiceImpl.update(order);
			
			//增加二次消费统计

			Filter filter = new Filter("wechat_id", Operator.eq, order.getWechatAccount().getId());
			long count = twiceConsumeRecordService.count(filter);
			if (count == 0) { //二次消费记录表中没有该用户 
				if (order.getCouponMoney().doubleValue() > 0) { // 订单必须使用电子券
					TwiceConsumeRecord twiceConsumeRecord = new TwiceConsumeRecord();
					twiceConsumeRecord.setWechat_id(order.getWechatAccount().getId());
					twiceConsumeRecord.setConsumer(order.getWechatAccount().getWechatName());
					twiceConsumeRecord.setPhone(order.getOrderPhone());
					twiceConsumeRecord.setOrderCode(order.getOrderCode());
					twiceConsumeRecord.setPayment(order.getTotalMoney().floatValue());
					twiceConsumeRecord.setCouponAmount(order.getCouponMoney().floatValue());
					twiceConsumeRecord.setWechatBalanceAmount(order.getBalanceMoney().floatValue());
					twiceConsumeRecord.setCashAmount(order.getPayMoney().floatValue());
					twiceConsumeRecord.setConsumeTime(order.getOrderTime());
					twiceConsumeRecord.setState(Constants.DUMMY_TWICE_CONSUME); //该次订单之后全款订单视为二次消费
					
					twiceConsumeRecordService.save(twiceConsumeRecord);
				} 
			}
			else { //二次消费记录表中有该用户
				if(order.getPayMoney() == order.getShouldPayMoney()){ // 是有效的二次消费订单(全款)
					//1.修改二次消费记录表
					TwiceConsumeRecord twiceConsumeRecord = new TwiceConsumeRecord();
					twiceConsumeRecord.setWechat_id(order.getWechatAccount().getId());
					twiceConsumeRecord.setConsumer(order.getWechatAccount().getWechatName());
					twiceConsumeRecord.setPhone(order.getOrderPhone());
					twiceConsumeRecord.setOrderCode(order.getOrderCode());
					twiceConsumeRecord.setPayment(order.getTotalMoney().floatValue());
					twiceConsumeRecord.setCouponAmount(order.getCouponMoney().floatValue());
					twiceConsumeRecord.setWechatBalanceAmount(order.getBalanceMoney().floatValue());
					twiceConsumeRecord.setCashAmount(order.getPayMoney().floatValue());
					twiceConsumeRecord.setConsumeTime(order.getOrderTime());
					twiceConsumeRecord.setState(Constants.REAL_TWICE_CONSUME); //该订单视为二次消费
					
					twiceConsumeRecordService.save(twiceConsumeRecord);
					
					
					//2.修改二次消费统计表
					List<Filter> filters3 = new ArrayList<>();
					filters3.add(new Filter("wechatId",Operator.eq,order.getWechatAccount().getId()));
					List<TwiceConsumeStatis> list = twiceConsumeStatisService.findList(null, filters3, null);
					if(list == null || list.size() == 0){ //二次消费统计表中没有该用户  则新建
						TwiceConsumeStatis twiceConsumeStatis = new TwiceConsumeStatis();
						twiceConsumeStatis.setConsumer(order.getWechatAccount().getWechatName());
						twiceConsumeStatis.setPhone(order.getOrderPhone());
						twiceConsumeStatis.setConsumeCount(1); //初始化消费次数
						twiceConsumeStatis.setTotalAmount(order.getTotalMoney().floatValue());
						twiceConsumeStatis.setWechatId(order.getWechatAccount().getId());
						twiceConsumeStatisService.save(twiceConsumeStatis);
					}else{//二次消费统计表中已经有该用户
						TwiceConsumeStatis twiceConsumeStatis = list.get(0);
						twiceConsumeStatis.setConsumeCount(twiceConsumeStatis.getConsumeCount() + 1);
						twiceConsumeStatis.setTotalAmount(twiceConsumeStatis.getTotalAmount() + order.getTotalMoney().floatValue());
						twiceConsumeStatisService.update(twiceConsumeStatis);  //更新二次消费统计表
					}
				}
			}
			json.setSuccess(true);
			json.setMsg("设置成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("设置失败");
			json.setObj(null);
			e.printStackTrace();
		}

		return json;
	}
	
}

