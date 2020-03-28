package com.hongyu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.dao.HyVinboundDao;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.HyGroupitemPromotion;
import com.hongyu.entity.HyGroupitemPromotionDetail;
import com.hongyu.entity.HyVinbound;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.service.HyGroupitemPromotionService;
import com.hongyu.service.HyVinboundService;
import com.hongyu.service.SpecialtySpecificationService;

@Service("hyVinboundServiceImpl")
public class HyVinboundServiceImpl extends BaseServiceImpl<HyVinbound, Long> implements HyVinboundService {
	
	
	@Resource(name="hyVinboundDaoImpl")
	private HyVinboundDao hyVinboundDao;
	
	@Resource(name="hyVinboundDaoImpl")
	public void setBaseDao(BaseDao dao){
		super.setBaseDao(dao);
	}
	
	public Integer abstractVinbound(Integer total,HyVinbound vinbound){
		vinbound.setSaleNumber(vinbound.getSaleNumber()+total);
		vinbound.setVinboundNumber(vinbound.getVinboundNumber()-total);;
		if(vinbound.getVinboundNumber()<0)vinbound.setVinboundNumber(0);
		vinbound.setVupdateTime(new Date());
		save(vinbound);	//保存修改后的虚拟库存
		return 0;
	}
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationService;
	
	@Resource(name = "hyGroupitemPromotionServiceImpl")
	HyGroupitemPromotionService hyGroupitemPromotionServiceImpl;
	
	public void updateOrderItemVinbound(BusinessOrderItem item){
		if(item.getType()==0){	//如果是普通商品
			List<Filter> filters = new ArrayList<>();
			SpecialtySpecification specification = specialtySpecificationService.find(item.getSpecialtySpecification());
			
			//获取父规格
			SpecialtySpecification fuSpecification = specialtySpecificationService.getParentSpecification(specification);
		
			
			filters.add(Filter.eq("specification", fuSpecification));
			List<HyVinbound> vinbounds = findList(1,filters,null);
			
			if(vinbounds==null || vinbounds.isEmpty()){
				return;	//虚拟库存没有暂时跳过处理
			}
			HyVinbound vinbound = vinbounds.get(0);
			//减虚拟库存
			abstractVinbound(item.getQuantity()*specification.getSaleNumber(),vinbound);
		}else{	//如果是组合商品
			//获取组合优惠活动对象
			HyGroupitemPromotion groupitemPromotion = 
					hyGroupitemPromotionServiceImpl.find(item.getSpecialty());
			//获取构建数量
			Integer quantity = item.getQuantity();
			//修改每件组合优惠明细条目的库存
			Set<HyGroupitemPromotionDetail> hyGroupitemPromotionDetails=
					groupitemPromotion.getHyGroupitemPromotionDetails();
			for(HyGroupitemPromotionDetail detail:hyGroupitemPromotionDetails){
				//获取明细购买数量
				Integer total = quantity*detail.getBuyNumber();
				List<Filter> filters = new ArrayList<>();
				SpecialtySpecification specification = specialtySpecificationService.find(detail.getItemSpecificationId().getId());
				
				//获取父规格
				SpecialtySpecification fuSpecification = specialtySpecificationService.getParentSpecification(specification);
			
				
				
				filters.add(Filter.eq("specification", fuSpecification));
				List<HyVinbound> vinbounds = findList(1,filters,null);
				if(vinbounds==null || vinbounds.isEmpty()){
					continue;	//虚拟库存没有暂时跳过处理
				}
				HyVinbound vinbound = vinbounds.get(0);
				//减虚拟库存
				abstractVinbound(total*specification.getSaleNumber(),vinbound);
			}
			
		}
	}
	
	public Integer plusVinbound(Integer total,HyVinbound vinbound){
		vinbound.setSaleNumber(vinbound.getSaleNumber()-total);
		vinbound.setVinboundNumber(vinbound.getVinboundNumber()+total);;
		if(vinbound.getSaleNumber()<0)vinbound.setSaleNumber(0);
		vinbound.setVupdateTime(new Date());
		save(vinbound);	//保存修改后的虚拟库存
		return 0;
	}
	
	public void returnOrderItemVinbound(BusinessOrderItem item){
		if(item.getType()==0){	//如果是普通商品
			List<Filter> filters = new ArrayList<>();
			SpecialtySpecification specification = specialtySpecificationService.find(item.getSpecialtySpecification());
			
			//获取父规格
			SpecialtySpecification fuSpecification = specialtySpecificationService.getParentSpecification(specification);
		
			
			filters.add(Filter.eq("specification", fuSpecification));
			List<HyVinbound> vinbounds = findList(1,filters,null);
			
			if(vinbounds==null || vinbounds.isEmpty()){
				return;	//虚拟库存没有暂时跳过处理
			}
			HyVinbound vinbound = vinbounds.get(0);
			//减虚拟库存
			plusVinbound((int)(item.getReturnQuantity()-Math.ceil(item.getLost1Quantity())-
					Math.ceil(item.getLost2Quantity()))*specification.getSaleNumber(),vinbound);
		}else{	//如果是组合商品
			//获取组合优惠活动对象
			HyGroupitemPromotion groupitemPromotion = 
					hyGroupitemPromotionServiceImpl.find(item.getSpecialty());
			//获取构建数量
			Integer quantity = (int)(item.getReturnQuantity()-Math.ceil(item.getLost1Quantity())-
					Math.ceil(item.getLost2Quantity()));
			//修改每件组合优惠明细条目的库存
			Set<HyGroupitemPromotionDetail> hyGroupitemPromotionDetails=
					groupitemPromotion.getHyGroupitemPromotionDetails();
			for(HyGroupitemPromotionDetail detail:hyGroupitemPromotionDetails){
				//获取明细退货数量
				Integer total = quantity*detail.getBuyNumber();
				List<Filter> filters = new ArrayList<>();
				SpecialtySpecification specification = specialtySpecificationService.find(detail.getItemSpecificationId().getId());
				//获取父规格
				SpecialtySpecification fuSpecification = specialtySpecificationService.getParentSpecification(specification);
				filters.add(Filter.eq("specification", fuSpecification));
				List<HyVinbound> vinbounds = findList(1,filters,null);
				if(vinbounds==null || vinbounds.isEmpty()){
					continue;	//虚拟库存没有暂时跳过处理
				}
				HyVinbound vinbound = vinbounds.get(0);
				//加虚拟库存
				plusVinbound(total*specification.getSaleNumber(),vinbound);
			}
			
		}
	}
}
