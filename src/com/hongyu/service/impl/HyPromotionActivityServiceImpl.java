package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.dao.HyPromotionActivityDao;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyPromotionActivity;
import com.hongyu.entity.LinePromotion;
import com.hongyu.service.HyPromotionActivityService;
import com.hongyu.util.Constants;

@Service("hyPromotionActivityServiceImpl")
public class HyPromotionActivityServiceImpl extends BaseServiceImpl<HyPromotionActivity, Long> implements HyPromotionActivityService {
	
	@Resource(name = "hyPromotionActivityDaoImpl")
	HyPromotionActivityDao dao;
	
	@Resource(name = "hyPromotionActivityDaoImpl")
	public void setBaseDao(HyPromotionActivityDao dao){
		super.setBaseDao(dao);		
	}

	@Override
	public Page<HyPromotionActivity> findAuditPage(HyAdmin auditor, Pageable pageable, HyPromotionActivity query) {
//		if (query.getActivityType() == Constants.PROMOTION_ACTIVITY_TYPE_RESERVED_TICKET)
//		HyDepartmentModel d = auditor.getDepartment().getHyDepartmentModel();
		
		HyDepartmentModel d = auditor.getDepartment().getHyDepartmentModel();
		if (d.getName().equals("总公司品控中心")) {   //说明只能审核供应商新建的促销
			String sql = "select a.id, a.name from hy_promotion_activity a where a.is_caigouti = 0";
			if (query != null && query.getState() != null) {
				sql += " and a.state = " + query.getState();
			}
			sql += " and a.activity_type = " + query.getActivityType();
			sql += " order by a.id desc";
			Page<List<Object[]>> page = this.findPageBysql(sql, pageable);
			List<Object[]> lst = page.getLstObj();
			Long[] ids = new Long[lst.size()];
			for (int i = 0; i < lst.size(); i++) {
				ids[i] = ((BigInteger)lst.get(i)[0]).longValue();
			}
			List<HyPromotionActivity> activities = this.findList(ids);
			Page<HyPromotionActivity> result = new Page<HyPromotionActivity>(activities, page.getTotal(), pageable);
			return result;
		} else {   //否则就是供应商或者汽车部，只能审核采购部新建的关于供应商自己的产品的促销
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("jidiao", auditor));
			filters.add(Filter.eq("isCaigouti", Boolean.TRUE));
			filters.add(Filter.eq("activityType", query.getActivityType()));
			if (query != null && query.getState() != null) {
				filters.add(Filter.eq("state", query.getState()));
			}
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<HyPromotionActivity> result = this.findPage(pageable);
			return result;
		}
//		
		// 不是认购门票
//		if (!query.getActivityType().equals(Constants.PROMOTION_ACTIVITY_TYPE_RESERVED_TICKET)) {
//			
//		} else {   // 认购门票，筛选逻辑与线路促销一致
//			
//		}
		
	}
	
	@Override
	public BigDecimal getDiscountedPriceByHyOrder(HyOrder hyOrder, HyPromotionActivity promotionActivity) {
		// TODO Auto-generated method stub
		BigDecimal discountedPrice = new BigDecimal(0);// 优惠金额
		
		if(hyOrder.getDiscountedType().equals(0)) {	//满减
			BigDecimal btimes = hyOrder.getJiesuanMoney1().divide(promotionActivity.getManjianPrice1(),2,BigDecimal.ROUND_HALF_UP);
			discountedPrice = promotionActivity.getManjianPrice2().multiply(btimes);
		}else if(hyOrder.getDiscountedType().equals(1)) {	//打折
			BigDecimal dazhe = BigDecimal.valueOf(1).subtract(promotionActivity.getDazhe());
			discountedPrice = hyOrder.getJiesuanMoney1().multiply(dazhe);
		}else if(hyOrder.getDiscountedType().equals(2)) {
			discountedPrice = promotionActivity.getMeirenjian().multiply(BigDecimal.valueOf(hyOrder.getPeople()));
		}
		
		return discountedPrice;
	}
}
