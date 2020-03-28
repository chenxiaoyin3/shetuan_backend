package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.sym.Name;
import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.MhGroupPrice;
import com.hongyu.entity.MhLine;
import com.hongyu.entity.HyGroup.GroupStateEnum;
import com.hongyu.entity.HyGroupPrice;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.MhLineService;
import com.hongyu.util.Constants.AuditStatus;
@Service("mhLineServiceImpl")
public class MhLineServiceImpl extends BaseServiceImpl<MhLine, Long> implements MhLineService{
	
	
	@Override
	@Resource(name = "mhLineDaoImpl")
	public void setBaseDao(BaseDao<MhLine, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	/**
	 * 获取当前官网上线的线路的最低价格：
	 * 也就是未出团的所有在官网上销售的团的最低价格
	 */
	@Override
	public BigDecimal getMhLineLowestPrice(HyLine hyLine) throws Exception {
		/**断定这里必定是完善过的团*/
		BigDecimal lowestPrice=BigDecimal.ZERO;
		List<Filter> filters=new ArrayList<Filter>();
		filters.add(Filter.eq("groupState", GroupStateEnum.daichutuan)); //筛选出团状态为待出团
		filters.add(Filter.eq("auditStatus", AuditStatus.pass)); //审核已通过的团
		filters.add(Filter.eq("line", hyLine));
		//状态为已完善
		filters.add(Filter.eq("mhState", 1));
		List<HyGroup> hyGroups=hyGroupService.findList(null,filters,null);
		if(!hyGroups.isEmpty()) {
			lowestPrice=this.getMhGroupLowestPrice(hyGroups.get(0));		
			for(HyGroup hyGroup:hyGroups) {
				BigDecimal bp = this.getMhGroupLowestPrice(hyGroup);
				lowestPrice=bp.compareTo(lowestPrice)<0?bp:lowestPrice;
			}
		}
		return lowestPrice;
	}
	/**
	 * 获取官网上线的团的最低价格
	 */
	@Override
	public BigDecimal getMhGroupLowestPrice(HyGroup group) throws Exception {
		List<HyGroupPrice> prices = group.getHyGroupPrices();
		BigDecimal bp = BigDecimal.ZERO;
		bp = prices.get(0).getMhGroupPrice().getMhAdultSalePrice();
		for(HyGroupPrice price:prices){
			MhGroupPrice mhGroupPrice = price.getMhGroupPrice();
			if(bp.compareTo(mhGroupPrice.getMhAdultSalePrice())>=0){
				bp = mhGroupPrice.getMhAdultSalePrice();
			}
		}
		
		return bp;
	}
	
	
}
