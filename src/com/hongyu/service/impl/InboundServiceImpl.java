package com.hongyu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.Order;
import com.hongyu.dao.InboundDao;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.BusinessOrderOutbound;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroupitemPromotion;
import com.hongyu.entity.HyGroupitemPromotionDetail;
import com.hongyu.entity.Inbound;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.service.BusinessOrderOutboundService;
import com.hongyu.service.HyGroupitemPromotionService;
import com.hongyu.service.InboundService;
import com.hongyu.service.SpecialtySpecificationService;

@Service(value = "inboundServiceImpl")
public class InboundServiceImpl extends BaseServiceImpl<Inbound, Long> implements InboundService {
	
	@Resource(name="businessOrderOutboundServiceImpl")
	BusinessOrderOutboundService businessOrderOutboundService;
	
	@Resource(name = "inboundDaoImpl")
	InboundDao inboundDaoImpl;
	
	@Resource(name = "inboundDaoImpl")
	public void setBaseDao(InboundDao dao) {
		super.setBaseDao(dao);	
	}
	
	@Transactional
	public Integer substractInbound(Integer total,List<Inbound> inbounds, BusinessOrderItem orderItem, HyAdmin username){
		if(inbounds==null || inbounds.isEmpty()){
			return total;
		}
		for(Inbound inbound:inbounds){
			if(total>inbound.getInboundNumber()){	//如果当前生产日期库存不足
				Integer number= inbound.getInboundNumber();
				inbound.setInboundNumber(0);	//减去所有库存
				inbound.setSaleNumber(inbound.getSaleNumber()+number);
				inbound.setUpdateTime(new Date());
				total-=number;
				BusinessOrderOutbound orderOutbound = new BusinessOrderOutbound();
				orderOutbound.setBusinessOrderItem(orderItem);
				orderOutbound.setInbound(inbound);
				orderOutbound.setDepotCode(inbound.getDepotCode());
				orderOutbound.setOutboundQuantity(number);
				orderOutbound.setOutboundTime(new Date());
				orderOutbound.setOperator(username);
				businessOrderOutboundService.save(orderOutbound);
				save(inbound);	//更新库存
			}else{	//如果当前生产日期库存充足
				BusinessOrderOutbound orderOutbound = new BusinessOrderOutbound();
				orderOutbound.setBusinessOrderItem(orderItem);
				orderOutbound.setInbound(inbound);
				orderOutbound.setDepotCode(inbound.getDepotCode());
				orderOutbound.setOutboundQuantity(total);
				orderOutbound.setOutboundTime(new Date());
				orderOutbound.setOperator(username);
				inbound.setInboundNumber(inbound.getInboundNumber()-total);
				inbound.setSaleNumber(inbound.getSaleNumber()+total);
				inbound.setUpdateTime(new Date());
				total=0;			
				businessOrderOutboundService.save(orderOutbound);
				save(inbound);	//更新库存
				break;
			}
		}
		return total;
	}
	
	public Integer getInboundTotal(List<Inbound> inbounds){
		Integer total=0;
		for(Inbound inbound:inbounds){
			total+=inbound.getInboundNumber();
		}
		return total;
		
	}
	
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationService;
	
	@Resource(name = "hyGroupitemPromotionServiceImpl")
	HyGroupitemPromotionService hyGroupitemPromotionServiceImpl;
	
	public BusinessOrderItem isInboundEnough(List<BusinessOrderItem> items){
		for(BusinessOrderItem item:items){
			if(item.getType()==0){	//如果是普通商品
				List<Filter> filters = new ArrayList<>();
				SpecialtySpecification specification = specialtySpecificationService.find(item.getSpecialtySpecification());
				
				//获取父规格
				SpecialtySpecification fuSpecification = specialtySpecificationService.getParentSpecification(specification);
			
				
				
				
				filters.add(Filter.eq("specification", fuSpecification));
				filters.add(Filter.eq("depotCode", item.getDepotName()));
				List<Order> orders = new ArrayList<>();
				orders.add(new Order("productDate",Order.Direction.desc));
				List<Inbound> inbounds = this.findList(null, filters, orders);
				Integer inboundsTotal = this.getInboundTotal(inbounds);
				Integer total = item.getQuantity()*specification.getSaleNumber();
				if(inboundsTotal<total){
					return item;
				}
			}else{	//如果是组合产品
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
					
					List<Filter> filters = new ArrayList<>();
					SpecialtySpecification specification = specialtySpecificationService.find(detail.getItemSpecificationId().getId());
					
					//获取父规格
					SpecialtySpecification fuSpecification = specialtySpecificationService.getParentSpecification(specification);
				
					
					filters.add(Filter.eq("specification", fuSpecification));
					filters.add(Filter.eq("depotCode", item.getDepotName()));
					List<Order> orders = new ArrayList<>();
					orders.add(new Order("productDate",Order.Direction.desc));
					List<Inbound> inbounds = findList(null, filters, orders);
					Integer inboundsTotal = getInboundTotal(inbounds);
					Integer total = quantity*detail.getBuyNumber()*specification.getSaleNumber();
					
					if(inboundsTotal<total){
						return item;
					}
				}
			}
		}
		return null;
	}
	
	public List<Inbound> getInboundListBySpecificationId(Long specificationId,Integer quantity){
		List<Filter> filters = new ArrayList<>();
		SpecialtySpecification specification = specialtySpecificationService.find(specificationId);
		filters.add(Filter.eq("specification", specification));
		filters.add(Filter.ge("inboundNumber", quantity));
		List<Inbound> inbounds = this.findList(null, filters, null);
		return inbounds;
	}
	
	public void updateOrderItemInbound(BusinessOrderItem item, HyAdmin username){
		if(item.getType()==0){	//如果是普通商品
			List<Filter> filters = new ArrayList<>();
			SpecialtySpecification specification = specialtySpecificationService.find(item.getSpecialtySpecification());
			
			//获取父规格
			SpecialtySpecification fuSpecification = specialtySpecificationService.getParentSpecification(specification);
		
			
			
			
			
			filters.add(Filter.eq("specification", fuSpecification));
			filters.add(Filter.eq("depotCode", item.getDepotName()));
			filters.add(Filter.gt("inboundNumber", 0));
			List<Order> orders = new ArrayList<>();
			orders.add(new Order("productDate",Order.Direction.desc));
			List<Inbound> inbounds = findList(null, filters, orders);
			//减库存
			substractInbound(item.getQuantity()*specification.getSaleNumber(), inbounds, item, username);
		
			
		}else{	//如果是组合产品
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
				filters.add(Filter.eq("depotCode", item.getDepotName()));
				filters.add(Filter.gt("inboundNumber", 0));
				List<Order> orders = new ArrayList<>();
				orders.add(new Order("productDate",Order.Direction.desc));
				List<Inbound> inbounds = findList(null, filters, orders);
				//减库存
				substractInbound(total*specification.getSaleNumber(),inbounds, item, username);
			}
		}
	}
	
	public Integer findInboundUniqueSpecificationTotal(Long categoryId, String specialtyName) {
		//查找所有
		StringBuilder sqlBuilder= new StringBuilder();
		if (categoryId != null && specialtyName != null) { 
			sqlBuilder.append("SELECT count(inbounds.inbound_number), count(inbounds.sale_number), inbounds.depot_code "
					+ "FROM (SELECT hy_inbound.* FROM hy_inbound, hy_specialty_specification WHERE hy_inbound.specialty_specification_id = hy_specialty_specification.id "
					+ "AND hy_specialty_specification.specialty_id IN (SELECT id FROM hy_specialty WHERE category_id = "+ categoryId + " AND name LIKE '"+ specialtyName  +"%')) inbounds GROUP BY inbounds.specialty_specification_id, inbounds.depot_code");
		} else if (categoryId != null) {
			sqlBuilder.append("SELECT count(inbounds.inbound_number), count(inbounds.sale_number), inbounds.depot_code "
					+ "FROM (SELECT hy_inbound.* FROM hy_inbound, hy_specialty_specification WHERE hy_inbound.specialty_specification_id = hy_specialty_specification.id "
					+ "AND hy_specialty_specification.specialty_id IN (SELECT id FROM hy_specialty WHERE category_id = "+ categoryId +")) inbounds GROUP BY inbounds.specialty_specification_id, inbounds.depot_code");
		} else if (specialtyName != null) {
			sqlBuilder.append("SELECT count(inbounds.inbound_number), count(inbounds.sale_number), inbounds.depot_code "
					+ "FROM (SELECT hy_inbound.* FROM hy_inbound, hy_specialty_specification WHERE hy_inbound.specialty_specification_id = hy_specialty_specification.id "
					+ "AND hy_specialty_specification.specialty_id IN (SELECT id FROM hy_specialty WHERE name LIKE '"+ specialtyName  +"%')) inbounds GROUP BY inbounds.specialty_specification_id, inbounds.depot_code");
		} else {
			sqlBuilder.append("SELECT count(inbounds.inbound_number), count(inbounds.sale_number), inbounds.depot_code "
					+ "FROM hy_inbound inbounds GROUP BY inbounds.specialty_specification_id, inbounds.depot_code");
		}
		List<Object[]> res = inboundDaoImpl.findBysql(sqlBuilder.toString());
		return res.size();
	}
	
	public List<Map<String, Object>> getMergedInboundByPage(int page, int pageSize, Long categoryId, String specialtyName) {
		StringBuilder sqlBuilder= new StringBuilder();
		if (categoryId != null && specialtyName != null) { 
			sqlBuilder.append("SELECT count(inbounds.inbound_number), count(inbounds.sale_number),inbounds.depot_code, "
					+ "hy_specialty.name, hy_specialty_specification.specification "
					+ "FROM (SELECT hy_inbound.* FROM hy_inbound, hy_specialty_specification WHERE hy_inbound.specialty_specification_id = "
					+ "hy_specialty_specification.id AND hy_specialty_specification.specialty_id IN (SELECT id FROM hy_specialty WHERE "
					+ "category_id = "+ categoryId + " AND name LIKE '"+ specialtyName  +"%')) inbounds left join hy_specialty_specification on inbounds.specialty_specification_id = hy_specialty_specification.id "
					+ "left join hy_specialty on hy_specialty_specification.specialty_id = hy_specialty.id GROUP BY inbounds.specialty_specification_id, inbounds.depot_code ");
		} else if (categoryId != null) {
			sqlBuilder.append("SELECT count(inbounds.inbound_number), count(inbounds.sale_number),inbounds.depot_code, "
					+ "hy_specialty.name, hy_specialty_specification.specification "
					+ "FROM (SELECT hy_inbound.* FROM hy_inbound, hy_specialty_specification WHERE hy_inbound.specialty_specification_id = "
					+ "hy_specialty_specification.id AND hy_specialty_specification.specialty_id IN (SELECT id FROM hy_specialty WHERE "
					+ "category_id = "+ categoryId + ")) inbounds left join hy_specialty_specification on inbounds.specialty_specification_id = hy_specialty_specification.id "
					+ "left join hy_specialty on hy_specialty_specification.specialty_id = hy_specialty.id GROUP BY inbounds.specialty_specification_id, inbounds.depot_code ");
		} else if (specialtyName != null) {
			sqlBuilder.append("SELECT count(inbounds.inbound_number), count(inbounds.sale_number),inbounds.depot_code, "
					+ "hy_specialty.name, hy_specialty_specification.specification "
					+ "FROM (SELECT hy_inbound.* FROM hy_inbound, hy_specialty_specification WHERE hy_inbound.specialty_specification_id = "
					+ "hy_specialty_specification.id AND hy_specialty_specification.specialty_id IN (SELECT id FROM hy_specialty WHERE "
					+ "name LIKE '"+ specialtyName  +"%')) inbounds left join hy_specialty_specification on inbounds.specialty_specification_id = hy_specialty_specification.id "
					+ "left join hy_specialty on hy_specialty_specification.specialty_id = hy_specialty.id GROUP BY inbounds.specialty_specification_id, inbounds.depot_code ");
		} else {
			sqlBuilder.append("SELECT count(inbounds.inbound_number), count(inbounds.sale_number),inbounds.depot_code, "
					+ "hy_specialty.name, hy_specialty_specification.specification "
					+ "FROM (SELECT hy_inbound.* FROM hy_inbound) inbounds left join hy_specialty_specification on inbounds.specialty_specification_id = hy_specialty_specification.id "
					+ "left join hy_specialty on hy_specialty_specification.specialty_id = hy_specialty.id GROUP BY inbounds.specialty_specification_id, inbounds.depot_code ");
		}
		
		Integer startIndex = (page-1)*pageSize;
		Integer offset = pageSize;
		sqlBuilder.append("LIMIT "+ startIndex.toString() + ", " + offset.toString());
		List<Object[]> res = inboundDaoImpl.findBysql(sqlBuilder.toString());
		
		List<Map<String, Object>> result = new ArrayList<>();
		for (Object[] objects : res) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("inboundNumber", objects[0]);
			m.put("saleNumber", objects[1]);
			m.put("depotCode", objects[2]);
			m.put("specialtyName", objects[3]);
			m.put("specification", objects[4]);
			result.add(m);
		}
		return result;
	}
}
