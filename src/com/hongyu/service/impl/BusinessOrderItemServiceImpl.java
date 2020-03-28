package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.dao.BusinessOrderItemDao;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.HyGroupitemPromotion;
import com.hongyu.entity.HyPromotion;
import com.hongyu.entity.HyPromotionPic;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyImage;
import com.hongyu.entity.SpecialtyPrice;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.HyGroupitemPromotionService;
import com.hongyu.service.SpecialtyPriceService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;

import junit.framework.Assert;
@Service("businessOrderItemServiceImpl")
public class BusinessOrderItemServiceImpl extends BaseServiceImpl<BusinessOrderItem, Long> implements BusinessOrderItemService{

	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyService;
	
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationService;
	
	@Resource(name = "hyGroupitemPromotionServiceImpl")
	HyGroupitemPromotionService hyGroupitemPromotionServiceImpl;
	
	@Resource(name = "specialtyPriceServiceImpl")
	SpecialtyPriceService specialtyPriceSrv;
	
	@Resource(name="businessOrderItemDaoImpl")
	private BusinessOrderItemDao businessOrderItemDao;
	@Override
	@Resource(name="businessOrderItemDaoImpl")
	public void setBaseDao(BaseDao<BusinessOrderItem, Long> baseDao) {

		super.setBaseDao(baseDao);
	}
	
	public List<BusinessOrderItem> getItemsByBusinessOrder(BusinessOrder bOrder){
		List<Filter> filters=new ArrayList<>();
		filters.add(Filter.eq("businessOrder", bOrder));
		List<BusinessOrderItem> items=businessOrderItemDao.findList(null,null,filters, null);
		return items;
		
	}
	
	public List<Map<String, Object>> getRefundItemMapList(BusinessOrder order){
		List<Map<String, Object>> refundList=new ArrayList<>();
		
		List<Filter> filters=new ArrayList<>();
		filters.add(Filter.gt("returnQuantity", 0));
		filters.add(Filter.eq("businessOrder", order));
		List<BusinessOrderItem> itemList=businessOrderItemDao.findList(null, null, filters, null);
		
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
			}else{
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
			itemMap.put("returnQuantity",bItem.getReturnQuantity());
			refundList.add(itemMap);	
		}
		
		return refundList;
	}
	
	public String getSpecialtyName(BusinessOrderItem bItem){
		int itemType=bItem.getType();
		if(itemType==0){	//普通商品
			Specialty specialty=specialtyService.find(bItem.getSpecialty());
			return specialty.getName();
		}else{	//组合优惠
			HyGroupitemPromotion hyGroupitemPromotion=hyGroupitemPromotionServiceImpl.find(bItem.getSpecialty());

			HyPromotion hyPromotion=hyGroupitemPromotion.getPromotionId();
			return hyPromotion.getPromotionName();
		}
	}
	
	public String getSpecificationName(BusinessOrderItem bItem){
		int itemType = bItem.getType();
		if(itemType==0){
			SpecialtySpecification specification=specialtySpecificationService.find(bItem.getSpecialtySpecification());
			return specification.getSpecification();
		}else{
			return null;
		}
	}
	
	
	/**
	 * 查询某规格在一段时间内已完成或者已完成退款的并且未结算的订单，并返回订单明细
	 */
	@Override
	public List<BusinessOrderItem> getItemsOfSpecificationInDuration(SpecialtySpecification specification, Date start,
			Date end, Integer deliverType) {
		Assert.assertNotNull(specification);
		Assert.assertNotNull(start);
		Assert.assertNotNull(end);
		Assert.assertNotNull(deliverType);
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String startTime = sdf.format(start);
		String endTime = sdf.format(end);
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select id, order_id from hy_business_order_item where specialty_specification_id = " + specification.getId() /** + " and deliver_type = " + deliverType**/);
		sqlBuilder.append(" and order_id in (select id from hy_business_order where (order_state = 6 or order_state = 12) and is_divided = 1 and UNIX_TIMESTAMP(complete_time) >= UNIX_TIMESTAMP('" 
		+ startTime + "')" + "and UNIX_TIMESTAMP(complete_time) <= UNIX_TIMESTAMP('" + endTime + "') and is_balance = 0)");
		
		List<Object[]> res = businessOrderItemDao.findBysql(sqlBuilder.toString());
		if (res.size() > 0) {
			Long[] ids = new Long[res.size()];
			int i = 0;
			for (Object[] objs : res) {
				ids[i] = ((BigInteger)(objs[0])).longValue();
				i++;
			}
			
			List<BusinessOrderItem> items = this.findList(ids);
			return items;
		}
		
		return new ArrayList<BusinessOrderItem>();
	}

	@Override
	public BigDecimal getCostPriceOfOrderitem(BusinessOrderItem item) throws Exception {
		if(item.getType().equals(1)) {
			throw new Exception("不考虑组合产品");
		}
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.le("createTime", item.getCreateTime()));
		SpecialtySpecification specification = specialtySpecificationService.find(item.getSpecialtySpecification());
		
		filters.add(Filter.eq("specification", specification));
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.desc("id"));
		List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, filters, orders);
		if (!prices.isEmpty()) {
			return prices.get(0).getCostPrice();
		} else {
			throw new Exception("价格信息表没有符合条件的价格信息");
		}
	}

	@Override
	public String getSpecialtyCode(BusinessOrderItem bItem) {
		int itemType=bItem.getType();
		if(itemType==0){	//普通商品
			Specialty specialty=specialtyService.find(bItem.getSpecialty());
			return specialty.getCode();
		}else{	//组合优惠
			return "无";
		}
	}

}
