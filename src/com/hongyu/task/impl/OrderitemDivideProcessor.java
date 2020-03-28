package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.HyGroupitemPromotion;
import com.hongyu.entity.HyGroupitemPromotionDetail;
import com.hongyu.entity.HyPromotion;
import com.hongyu.entity.HySingleitemPromotion;
import com.hongyu.entity.OrderItemDivide;
import com.hongyu.entity.OrderTransaction;
import com.hongyu.entity.SpecialtyPrice;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.entity.TwiceConsumeRecord;
import com.hongyu.entity.TwiceConsumeStatis;
import com.hongyu.entity.WeDivideModel;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.BusinessStoreService;
import com.hongyu.service.HyGroupitemPromotionService;
import com.hongyu.service.HyPromotionService;
import com.hongyu.service.HySingleitemPromotionService;
import com.hongyu.service.OrderItemDivideService;
import com.hongyu.service.OrderTransactionService;
import com.hongyu.service.SpecialtyPriceService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.service.StoreService;
import com.hongyu.service.TwiceConsumeRecordService;
import com.hongyu.service.TwiceConsumeStatisService;
import com.hongyu.service.WeDivideModelService;
import com.hongyu.service.WeDivideProportionService;
import com.hongyu.task.Processor;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.hongyu.util.OrderTransactionSNGenerator;

@Component("orderitemDivideProcessor")
public class OrderitemDivideProcessor implements Processor {
	
	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderServiceImpl;
	
	@Resource(name = "weDivideModelServiceImpl")
	WeDivideModelService modelSrv;
	
	@Resource(name="weDivideProportionServiceImpl")
	WeDivideProportionService proportionSrv;
	
	@Resource(name = "businessStoreServiceImpl")
	BusinessStoreService businessStoreService;
	
	@Resource(name="storeServiceImpl")
	StoreService storeService;
	
	@Resource(name="hyPromotionServiceImpl")
	HyPromotionService hyPromotionService;
	
	@Resource(name="orderItemDivideServiceImpl")
	OrderItemDivideService orderItemDivideService;
	
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationSrv;
	
	@Resource(name = "specialtyPriceServiceImpl")
	SpecialtyPriceService specialtyPriceSrv;
	
	@Resource(name="hySingleitemPromotionServiceImpl")
	HySingleitemPromotionService hySingleitemPromotionService;
	
	@Resource(name="hyGroupitemPromotionServiceImpl")
	HyGroupitemPromotionService hyGroupitemPromotionService;
	
	
	@Override
	@Transactional
	public void process() {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("orderState", Constants.BUSINESS_ORDER_STATUS_FINISH));
		filters.add(Filter.eq("isBalanced", false));
		filters.add(Filter.eq("isShow", false));
		filters.add(Filter.lt("balanceMoney", new BigDecimal(0.001)));
		
		List<WeDivideModel> model_list = modelSrv.findAll();
		WeDivideModel hongyu_model = null;
		WeDivideModel nonHongyu_model = null;
		WeDivideModel person_model = null;
		//查找三种微商类型的分成比例
		for (WeDivideModel model : model_list) {
			if (model.getIsValid()) {
				//虹宇门店类型
				if (model.getModelType().equals(Constants.WEBUSINESS_MODEL_HONGYU)) {
					hongyu_model = model;
				} else if (model.getModelType().equals(Constants.WEBUSINESS_MODEL_NON_HONGYU)) {
					nonHongyu_model = model;
				} else if (model.getModelType().equals(Constants.WEBUSINESS_MODEL_PERSON)) {
					person_model = model;
				}
			}
		}
		
//		List<WeDivideProportion> list = proportionSrv.findAll();
//		WeDivideProportion hongyu = null;
//		WeDivideProportion nonHongyu = null;
//		WeDivideProportion person = null;
//		//查找三种微商类型的分成比例
//		for (WeDivideProportion proportion : list) {
//			if (proportion.getIsValid()) {
//				//虹宇门店类型
//				if (proportion.getProportionType() == Constants.WEBUSINESS_TYPE_HONGYU) {
//					hongyu = proportion;
//				} else if (proportion.getProportionType() == Constants.WEBUSINESS_TYPE_NON_HONGYU) {
//					nonHongyu = proportion;
//				} else if (proportion.getProportionType() == Constants.WEBUSINESS_TYPE_PERSON) {
//					person = proportion;
//				}
//			}
//		}
		
		List<BusinessOrder> orders = businessOrderServiceImpl.findList(null, filters, null);
		
		//还需要加上退款完成的订单
		filters.clear();
		filters.add(Filter.eq("orderState", Constants.BUSINESS_ORDER_STATUS_FINISH_REFUND));
		filters.add(Filter.eq("isBalanced", false));
		filters.add(Filter.eq("isShow", false));
		filters.add(Filter.lt("balanceMoney", new BigDecimal(0.001)));
		List<BusinessOrder> finishRefundOrders = businessOrderServiceImpl.findList(null, filters, null);
		orders.addAll(finishRefundOrders);
		//遍历订单
		for (BusinessOrder order : orders) {
			//2019-08-07改bug。不对从取消订单变到已退款的订单进行分成。这样的订单钱一定全退了，没钱可分。
			if(Constants.BUSINESS_ORDER_STATUS_FINISH_REFUND.equals(order.getOrderState()) && order.getShip()==null){
				//全退款还有订单取消到了已退款状态的情况，用order.getShip()==null没有物流信息判断是用户或者管理系统取消订单，然后订单状态转移到已退款的。
				continue;
			}
			
			//遍历当前订单所有明细，生成订单分成明细
			for (BusinessOrderItem item : order.getBusinessOrderItems()) {
				// 赠品继续
				if (item.getIsGift())
					continue;
				OrderItemDivide divide = new OrderItemDivide();
				divide.setBusinessOrder(order);
				divide.setBusinessOrderItem(item);
				divide.setOrdertime(order.getOrderTime());
				divide.setAcceptTime(order.getCompleteTime());
				//设置分成微商
				divide.setWeBusiness(order.getWeBusiness());
				BigDecimal total = null;
				BigDecimal totalDivide = null;
				
				//计算分成比例，如果不参与活动
				if (item.getPromotionId() == null) {
					List<Filter> priceFilters = new ArrayList<Filter>();
					SpecialtySpecification specification = specialtySpecificationSrv.find(item.getSpecialtySpecification());
					if (specification != null) {
						priceFilters.add(Filter.eq("specification", specification));
						priceFilters.add(Filter.eq("isActive", true));
						List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, priceFilters, null);
						if (prices.size() > 0) {
							// 目前使用的供货价是当前结算时间的供货价
							SpecialtyPrice price = prices.get(0);
							total = item.getSalePrice().subtract(price.getCostPrice()).subtract(price.getDeliverPrice())
									.multiply(new BigDecimal(item.getQuantity()-item.getReturnQuantity()));
							totalDivide = total.multiply(prices.get(0).getBusinessPersonDivide());
							if (totalDivide.compareTo(BigDecimal.ZERO)<=0)
								continue;
							divide.setTotalAmount(total);
							//微商
							BigDecimal webusinessamount = totalDivide.multiply(person_model.getWeBusiness()).setScale(0, BigDecimal.ROUND_DOWN);//2019-06-20改成取整，舍弃小数，上边有限制这里不会有小于0的
							divide.setWeBusinessAmount(webusinessamount);
							//门店
							divide.setmWeBusinessAmount(BigDecimal.ZERO);
							divide.setmWeBusiness(null);
							// 存在推荐人
							if (order.getWeBusiness().getIntroducer() != null) {
								BigDecimal introduceramount = totalDivide.multiply(person_model.getIntroducer()).setScale(0, BigDecimal.ROUND_DOWN);//2019-06-20改成取整，舍弃小数，上边有限制这里不会有小于0的
								divide.setrWeBusinessAmount(introduceramount);
								divide.setrWeBusiness(order.getWeBusiness().getIntroducer());
							} else {
								divide.setrWeBusinessAmount(BigDecimal.ZERO);
								divide.setrWeBusiness(null);
							}
							//个人微商没有门店分成
//							divide.setmWeBusiness(businessStoreService.find(order.getWeBusiness().getStoreId()).getHeadWebusiness());
						} else {
							continue;
						}
						
					} else {
						continue;
					}
					
				} else {  //参与活动
					
					HyPromotion promotion = item.getPromotionId();
					divide.setTotalAmount(promotion.getDivideMoney().multiply(new BigDecimal(item.getQuantity()-item.getReturnQuantity())).setScale(0, BigDecimal.ROUND_DOWN));//2019-06-20改成取整，舍弃小数
					divide.setWeBusinessAmount(divide.getTotalAmount());
					//门店，不分成
					divide.setmWeBusinessAmount(BigDecimal.ZERO);
					divide.setmWeBusiness(null);
					// 存在推荐人，不分成
					divide.setrWeBusinessAmount(BigDecimal.ZERO);
					divide.setrWeBusiness(null);
					
					//普通商品
//					if (item.getType() == 0) {
//						divide.setTotalAmount(promotion.getDivideMoney().multiply(new BigDecimal(item.getQuantity()-item.getReturnQuantity())));
//						divide.setWeBusinessAmount(divide.getTotalAmount());
//						//门店
//						divide.setmWeBusinessAmount(BigDecimal.ZERO);
//						divide.setmWeBusiness(null);
//						// 存在推荐人
//						divide.setrWeBusinessAmount(BigDecimal.ZERO);
//						divide.setrWeBusiness(null);
						
//						List<Filter> priceFilters = new ArrayList<Filter>();
//						List<Filter> promotionItemFilters = new ArrayList<Filter>();
//						SpecialtySpecification specification = specialtySpecificationSrv.find(item.getSpecialtySpecification());
//						if (specification != null) {
////							promotionItemFilters.add(Filter.eq("specificationId", specification));
////							promotionItemFilters.add(Filter.eq("hyPromotion", promotion));
////							List<HySingleitemPromotion> promotionItems = hySingleitemPromotionService.findList(null, promotionItemFilters, null);
//							divide.setTotalAmount(promotion.getDivideMoney().multiply(new BigDecimal(item.getQuantity()-item.getReturnQuantity())));
//							divide.setWeBusinessAmount(divide.getTotalAmount());
//							//门店
//							divide.setmWeBusinessAmount(BigDecimal.ZERO);
//							divide.setmWeBusiness(null);
//							// 存在推荐人
//							divide.setrWeBusinessAmount(BigDecimal.ZERO);
//							divide.setrWeBusiness(null);
							
							
//							priceFilters.add(Filter.eq("specification", specification));
//							priceFilters.add(Filter.eq("isActive", true));
//							List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, priceFilters, null);
//							if (promotionItems.size() > 0) {
//								// 目前使用的供货价是当前结算时间的供货价
//								System.out.println("特产Id:" + specification.getId());
//								SpecialtyPrice price = prices.get(0);
//								total = item.getSalePrice().subtract(price.getCostPrice())
//										.multiply(new BigDecimal(item.getQuantity()-item.getReturnQuantity()));
//								totalDivide = total.multiply(promotionItems.get(0).getBusinessPersonDivide().getProportion());
//								if (totalDivide.compareTo(BigDecimal.ZERO)<=0)
//									continue;
//								divide.setTotalAmount(total);
//								//微商
//								BigDecimal webusinessamount = totalDivide.multiply(person_model.getWeBusiness()).setScale(2, RoundingMode.HALF_DOWN);
//								divide.setWeBusinessAmount(webusinessamount);
//								//门店
//								divide.setmWeBusinessAmount(BigDecimal.ZERO);
//								divide.setmWeBusiness(null);
//								// 存在推荐人
//								if (order.getWeBusiness().getIntroducer() != null) {
//									BigDecimal introduceramount = totalDivide.multiply(person_model.getIntroducer()).setScale(2, RoundingMode.HALF_DOWN);
//									divide.setrWeBusinessAmount(introduceramount);
//									divide.setrWeBusiness(order.getWeBusiness().getIntroducer());
//								} else {
//									divide.setrWeBusinessAmount(BigDecimal.ZERO);
//									divide.setrWeBusiness(null);
//								}
//								//个人微商没有门店分成
////								divide.setmWeBusiness(businessStoreService.find(order.getWeBusiness().getStoreId()).getHeadWebusiness());
//							} else {
//								continue;
//							}
//						} else {
//							continue;
//						}
						
//					} else if (item.getType() == 1) {    //组合优惠活动
//						HyGroupitemPromotion groupPromotion = hyGroupitemPromotionService.find(item.getSpecialty());
//						if (groupPromotion != null) {
//							total = item.getSalePrice();
//							Set<HyGroupitemPromotionDetail> set = groupPromotion.getHyGroupitemPromotionDetails();
//							for (HyGroupitemPromotionDetail detail : set) {
//								List<Filter> priceFilters = new ArrayList<Filter>();
//								priceFilters.add(Filter.eq("specification", detail.getItemSpecificationId()));
//								priceFilters.add(Filter.eq("isActive", true));
//								List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, priceFilters, null);
//								total = total.subtract(new BigDecimal(detail.getBuyNumber()).multiply(prices.get(0).getCostPrice()));
//							}
//							total = total.multiply(new BigDecimal(item.getQuantity()-item.getReturnQuantity()));
//							totalDivide = total.multiply(groupPromotion.getBusinessPersonDivide().getProportion());
//							if (totalDivide.compareTo(BigDecimal.ZERO)<=0)
//								continue;
//							divide.setTotalAmount(total);
//							//微商
//							BigDecimal webusinessamount = totalDivide.multiply(person_model.getWeBusiness()).setScale(2, RoundingMode.HALF_DOWN);
//							divide.setWeBusinessAmount(webusinessamount);
//							//门店
//							divide.setmWeBusinessAmount(BigDecimal.ZERO);
//							divide.setmWeBusiness(null);
//							// 存在推荐人
//							if (order.getWeBusiness().getIntroducer() != null) {
//								BigDecimal introduceramount = totalDivide.multiply(person_model.getIntroducer()).setScale(2, RoundingMode.HALF_DOWN);
//								divide.setrWeBusinessAmount(introduceramount);
//								divide.setrWeBusiness(order.getWeBusiness().getIntroducer());
//							} else {
//								divide.setrWeBusinessAmount(BigDecimal.ZERO);
//								divide.setrWeBusiness(null);
//							}
//							//个人微商没有门店分成
////							divide.setmWeBusiness(businessStoreService.find(order.getWeBusiness().getStoreId()).getHeadWebusiness());
//						} else {
//							continue;
//						}
//					} else {
//						continue;
//					}
				}
				orderItemDivideService.save(divide);
			}
			order.setIsBalanced(true);
			businessOrderServiceImpl.update(order);			
		}		
	}
}

