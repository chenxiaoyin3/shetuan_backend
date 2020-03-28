package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.dao.BusinessOrderDao;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.HyGroupitemPromotion;
import com.hongyu.entity.HyPromotion;
import com.hongyu.entity.HyPromotionPic;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyImage;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyGroupitemPromotionService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.util.Constants;
import com.hongyu.util.OrderSNGenerator;

@Service("businessOrderServiceImpl")
public class BusinessOrderServiceImpl extends BaseServiceImpl<BusinessOrder, Long> implements BusinessOrderService {
	
	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyService;
	
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationService;
	
	@Resource(name = "hyGroupitemPromotionServiceImpl")
	HyGroupitemPromotionService hyGroupitemPromotionServiceImpl;
	
	@Resource(name="businessOrderDaoImpl")
	BusinessOrderDao businessOrderDaoImpl;
	
	@Resource(name="businessOrderDaoImpl")
	  public void setBaseDao(BusinessOrderDao dao)
	  {
	    super.setBaseDao(dao);
	  }
	
	public Map<String, Object> getOrderListItemMap(BusinessOrder bOrder){
    	Map<String, Object> map=new HashMap<>();
    	
    	map.put("id", bOrder.getId());	//订单id
		map.put("orderCode", bOrder.getOrderCode());	//订单编号
		map.put("orderState", bOrder.getOrderState());	//订单状态
		map.put("payMoney", bOrder.getTotalMoney());	//2019-08-06改成显示订单总金额
		map.put("isDivided", bOrder.getIsDivided());	//是否由供货商发货
		map.put("isAppraised", bOrder.getIsAppraised());	//是否评价
		map.put("orderTime", bOrder.getOrderTime());	//订单时间
		//订单条目
		Set<BusinessOrderItem> itemList=bOrder.getBusinessOrderItems();
		List<Map<String, Object>> items=new ArrayList<>();
		for(BusinessOrderItem bItem:itemList){
			Map<String, Object> itemMap=new HashMap<>();
			itemMap.put("id", bItem.getId());
			itemMap.put("quantity", bItem.getQuantity());
			int itemType=bItem.getType();
			if(itemType==0){	//普通商品
				Specialty specialty=specialtyService.find(bItem.getSpecialty());
				itemMap.put("name", specialty.getName());
				List<SpecialtyImage> images=specialty.getImages();
				for(SpecialtyImage image:images){
					if(image.getIsLogo()){
						itemMap.put("iconURL", image);
						break;
					}
				}
				SpecialtySpecification specialtySpecification=specialtySpecificationService.find(bItem.getSpecialtySpecification());
				itemMap.put("specification", specialtySpecification.getSpecification());
			}else{	//组合优惠
				HyGroupitemPromotion hyGroupitemPromotion=hyGroupitemPromotionServiceImpl.find(bItem.getSpecialty());

				HyPromotion hyPromotion=hyGroupitemPromotion.getPromotionId();
				itemMap.put("name", hyPromotion.getPromotionName());
				Set<HyPromotionPic> images=hyPromotion.getHyPromotionPics();
				for(HyPromotionPic image:images){
					if(image.getIsTag()){
						itemMap.put("iconURL", image);
					}
				}
				itemMap.put("specification", null);
			}
			itemMap.put("type", itemType);
			itemMap.put("salePrice", bItem.getSalePrice());
			
			itemMap.put("deliverName", bItem.getDeliverName());
			itemMap.put("deliverType", bItem.getDeliverType());
			itemMap.put("depotName", bItem.getDepotName());
			
			items.add(itemMap);	
		}
		map.put("orderItems", items);	//添加订单条目信息
    	
    	
    	return map;
	}
	
	//创建子订单
	public BusinessOrder createSubOrder(BusinessOrder order,String deliverName,List<BusinessOrderItem> orderItems){
		
		//获取子订单总价
		BigDecimal subTotal = BigDecimal.ZERO;
		for(BusinessOrderItem item:orderItems){
			subTotal=subTotal.add(item.getOriginalPrice().multiply(BigDecimal.valueOf(item.getQuantity().longValue())));
		}
		BigDecimal subRatio = subTotal.divide(order.getTotalMoney().subtract(order.getShipFee()), 6, BigDecimal.ROUND_HALF_DOWN);
		//创建子订单
		BusinessOrder subOrder = new BusinessOrder();
		subOrder.setOrderPhone(order.getOrderPhone());	//设置下单手机号
		subOrder.setTotalMoney(order.getTotalMoney().multiply(subRatio));	//设置总价
		subOrder.setPromotionAmount(order.getPromotionAmount().multiply(subRatio));	//设置优惠金额
		subOrder.setShipFee(order.getShipFee().multiply(subRatio));	//设置快递费
		subOrder.setShouldPayMoney(order.getShouldPayMoney().multiply(subRatio));	//设置应付金额
		subOrder.setCouponMoney(order.getCouponMoney().multiply(subRatio));	//设置电子卷金额
		subOrder.setBalanceMoney(order.getBalanceMoney().multiply(subRatio));	//设置余额金额
		subOrder.setPayMoney(order.getPayMoney().multiply(subRatio));	//设置支付金额
		subOrder.setReceiverRemark(order.getReceiverRemark());	//设置收货备注
		subOrder.setReceiverAddress(order.getReceiverAddress());	//设置收货地址
		subOrder.setReceiverName(order.getReceiverName());	//设置收货人姓名
		subOrder.setReceiverPhone(order.getReceiverPhone());	//设置收货人手机号
		subOrder.setReceiveType(order.getReceiveType());	//设置收货类型
		subOrder.setWechatAccount(order.getWechatAccount());	//设置微信下单账户
		subOrder.setWeBusiness(order.getWeBusiness());	//设置微商
		subOrder.setPortalUserId(order.getPortalUserId());//设置官网用户id 2019-06-22加

		subOrder.setOrderCode(this.getOrderCode());	//设置订单编号
		subOrder.setOrderTime(order.getOrderTime());	//设置下单时间
		subOrder.setPayTime(order.getPayTime());	//设置支付时间
		subOrder.setCouponId(order.getCouponId());	//设置电子卷id
		subOrder.setIsValid(true);	//设置为有效
		
		subOrder.setReviewer(order.getReviewer());	//设置审核人
		subOrder.setParentOrderId(order.getId());	//设置父订单id
		Boolean isDeliver = deliverName.equals("平台")==false;
		subOrder.setIsDivided(isDeliver);	//设置是否由供应商发货
		subOrder.setIsShow(false);	//不是原始凭证
		
		subOrder.setOrderState(isDeliver?Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY:
			Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_INBOUND);	//设置订单状态
		subOrder.setReviewTime(new Date());
		subOrder.setAuditStatus(1);
		
		//设置订单条目
		Set<BusinessOrderItem> businessOrderItems = new HashSet<>();
		for(BusinessOrderItem item:orderItems){
			item.setBusinessOrder(subOrder);	//设置该订单条目的订单id为子订单的id
			businessOrderItems.add(item);
		}
		subOrder.setBusinessOrderItems(businessOrderItems);
		save(subOrder);
		return subOrder;
	}

	@Override
	public Boolean havePromotions(BusinessOrder order) {
		// TODO Auto-generated method stub
		Set<BusinessOrderItem> items = order.getBusinessOrderItems();
		for(BusinessOrderItem item:items) {
			if(item.getPromotionId()!=null) {
				return true;
			}
		}
		
		return false;
	}

	@Resource(name = "commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;
	
	@Override
	 public String getOrderCode() {
		// TODO Auto-generated method stub
		// 生成订单编号
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.in("type", SequenceTypeEnum.businessOrderSuq));
		Long value = 0L;
		synchronized (this) {
			List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
			CommonSequence c = ss.get(0);
			value = c.getValue() + 1;
			c.setValue(value);
			commonSequenceService.update(c);

		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowaday = sdf.format(new Date());
		String code = nowaday + String.format("%08d", value); // SN至少为8位,不足补零
		return code;
	}
}
