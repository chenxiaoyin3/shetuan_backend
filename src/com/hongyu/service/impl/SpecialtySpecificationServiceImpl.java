package com.hongyu.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.dao.SpecialtySpecificationDao;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.HyGroupitemPromotion;
import com.hongyu.entity.HyGroupitemPromotionDetail;
import com.hongyu.entity.Inbound;
import com.hongyu.entity.Provider;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.service.HyGroupitemPromotionService;
import com.hongyu.service.SpecialtySpecificationService;

@Service("specialtySpecificationServiceImpl")
public class SpecialtySpecificationServiceImpl extends BaseServiceImpl<SpecialtySpecification, Long> implements SpecialtySpecificationService {
	
	@Resource(name = "hyGroupitemPromotionServiceImpl")
	HyGroupitemPromotionService hyGroupitemPromotionServiceImpl;
	
	  @Resource(name="specialtySpecificationDaoImpl")
	  SpecialtySpecificationDao specialtySpecificationDaoImpl;
	  
	  @Resource(name="specialtySpecificationDaoImpl")
	  public void setBaseDao(SpecialtySpecificationDao dao)
	  {
	    super.setBaseDao(dao);
	  }
	  
	  public SpecialtySpecification getParentSpecification(SpecialtySpecification specification){
			SpecialtySpecification fuSpecification = specification;
			if(fuSpecification.getParent()!=null && fuSpecification.getParent() != 0){
				fuSpecification = find(fuSpecification.getParent());
			}
			return fuSpecification;
	  }

	@Override
	public List<SpecialtySpecification> getSpecificationsOfProvider(Provider provider) {
		//这里是否需要考虑规格有效无效
		String sql = "select id, specification from hy_specialty_specification where specialty_id in (select id from hy_specialty where provider_id = " + provider.getId() + ")";
		List<Object[]> result = specialtySpecificationDaoImpl.findBysql(sql);
		
		
		if (result.size() > 0) {
			Long[] ids = new Long[result.size()];
			for (int i = 0; i < result.size(); i++) {
				ids[i] = ((BigInteger)(result.get(i)[0])).longValue();
			}
			
			List<SpecialtySpecification> specifications = this.findList(ids);
			return specifications;
		}
		
		return new ArrayList<SpecialtySpecification>();
	}

	@Override
	public void updateBaseInboundAndHasSold(BusinessOrderItem orderItem, Boolean isSale)throws Exception {
		// TODO Auto-generated method stub

		if(orderItem.getType()==0){	//如果是普通商品
			List<Filter> filters = new ArrayList<>();
			SpecialtySpecification specification = this.find(orderItem.getSpecialtySpecification());
			
			//获取父规格
			SpecialtySpecification fuSpecification = this.getParentSpecification(specification);
			if(fuSpecification==null){
				throw new Exception("无效规格异常");
			}
			//减库存
			Integer soldNumber = specification.getSaleNumber()*orderItem.getQuantity();
			
			if(isSale){
				fuSpecification.setBaseInbound(fuSpecification.getBaseInbound()-soldNumber);
				specification.setHasSold(specification.getHasSold()+orderItem.getQuantity());
			}else{
				fuSpecification.setBaseInbound(fuSpecification.getBaseInbound()+soldNumber);	
				specification.setHasSold(specification.getHasSold()-orderItem.getQuantity());
			}
			
			
			this.update(fuSpecification);
			this.update(specification);
			
		
			
		}else{	//如果是组合产品
			//获取组合优惠活动对象
			HyGroupitemPromotion groupitemPromotion = 
					hyGroupitemPromotionServiceImpl.find(orderItem.getSpecialty());
			//获取购买数量
			Integer quantity = orderItem.getQuantity();
			//修改组合优惠相关数量
			//2019-04-28  添加对组合优惠的已销售数量的修改
			if(isSale){
				groupitemPromotion.setPromoteNum(groupitemPromotion.getPromoteNum()-quantity);
				groupitemPromotion.setHavePromoted(groupitemPromotion.getHavePromoted() + quantity);
			}else {
				groupitemPromotion.setPromoteNum(groupitemPromotion.getPromoteNum()+quantity);
				groupitemPromotion.setHavePromoted(groupitemPromotion.getHavePromoted() - quantity);
			}
			//修改每件组合优惠明细条目的库存
			Set<HyGroupitemPromotionDetail> hyGroupitemPromotionDetails=
					groupitemPromotion.getHyGroupitemPromotionDetails();
			for(HyGroupitemPromotionDetail detail:hyGroupitemPromotionDetails){
				//获取明细购买数量
				Integer total = quantity*detail.getBuyNumber();
				List<Filter> filters = new ArrayList<>();
				SpecialtySpecification specification = this.find(detail.getItemSpecificationId().getId());
				
				//获取父规格
				SpecialtySpecification fuSpecification = this.getParentSpecification(specification);
				if(fuSpecification==null){
					throw new Exception("无效规格异常");
				}
				//减库存
				Integer soldNumber = specification.getSaleNumber()*total;
				
				if(isSale){
					fuSpecification.setBaseInbound(fuSpecification.getBaseInbound()-soldNumber);
					specification.setHasSold(specification.getHasSold()+total);
				}else{
					fuSpecification.setBaseInbound(fuSpecification.getBaseInbound()+soldNumber);
					specification.setHasSold(specification.getHasSold()-total);
				}
				
				
				this.update(fuSpecification);
				this.update(specification);
				
			}
			hyGroupitemPromotionServiceImpl.update(groupitemPromotion);
		}
		
		
	}

	@Override
	public Boolean isBaseInboundEnough(List<Map<String, Object>> orderItems)throws Exception {
		// TODO Auto-generated method stub
		
		for(Map<String, Object> orderItem:orderItems){
			if((Boolean)orderItem.get("isGroupPromotion")){	//如果是组合优惠
				//获取组合优惠活动对象
				HyGroupitemPromotion groupitemPromotion = 
						hyGroupitemPromotionServiceImpl.find(((Integer) orderItem.get("specialtyId")).longValue());
				//获取购买数量
				Integer quantity = (Integer) orderItem.get("quantity");
				//检查每件组合优惠明细条目的库存
				Set<HyGroupitemPromotionDetail> hyGroupitemPromotionDetails=
						groupitemPromotion.getHyGroupitemPromotionDetails();
				for(HyGroupitemPromotionDetail detail:hyGroupitemPromotionDetails){
					//获取明细购买数量
					Integer total = quantity*detail.getBuyNumber();

					SpecialtySpecification specification = this.find(detail.getItemSpecificationId().getId());
					//获取父规格
					SpecialtySpecification fuSpecification = this.getParentSpecification(specification);
					if(fuSpecification==null){
						throw new Exception("无效规格异常");
					}
					if(total*specification.getSaleNumber()>fuSpecification.getBaseInbound()){
						return false;
					}
				}
			}else{	//如果是普通产品
				
				Integer total = (Integer) orderItem.get("quantity");
				
				SpecialtySpecification specification = this.find(((Integer) orderItem.get("specialtySpecificationId")).longValue());
				
				//获取父规格
				SpecialtySpecification fuSpecification = this.getParentSpecification(specification);
				if(fuSpecification==null){
					throw new Exception("无效规格异常");
				}
				
				if(total*specification.getSaleNumber()>fuSpecification.getBaseInbound()){
					return false;
				}
			}
		}
		
		return true;
	}
}
