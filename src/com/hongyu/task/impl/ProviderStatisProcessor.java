package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.Provider;
import com.hongyu.entity.ProviderSales;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderRefundService;
import com.hongyu.service.ProviderSalesService;
import com.hongyu.service.ProviderService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.task.Processor;
import com.hongyu.util.DateUtil;

/**
 * 统计供货商销售数据和合同状态
 * @author JZhong
 *
 */
@Component("providerStatisProcessor")
public class ProviderStatisProcessor implements Processor {

	@Resource(name = "providerServiceImpl")
	ProviderService providerServiceImpl;
	
	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyServiceImpl;
	
	@Resource(name="businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;
	
	@Resource(name = "providerSalesServiceImpl")
	ProviderSalesService providerSalesServiceImpl;
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		List<Filter> providerFilters = new ArrayList<Filter>();
//		providerFilters.add(Filter.eq("state", true));
		List<Provider> providers = providerServiceImpl.findAll();
		
		for (Provider provider : providers) {
//			List<Filter> specialtyFilters = new ArrayList<Filter>();
//			
//			specialtyFilters.add(Filter.eq("provider", provider));
//			
//			List<Specialty> specialties = specialtyServiceImpl.findList(null, specialtyFilters, null);
//			
//			for (Specialty specialty : specialties) {
//				for (SpecialtySpecification specification : specialty.getSpecifications()) {
//					List<Filter> orderItemFilters = new ArrayList<>();
//					//表示普通商品而不是组合优惠
//					orderItemFilters.add(Filter.eq("type", 0));
//					orderItemFilters.add(Filter.eq("specialty", specialty.getId()));
//					orderItemFilters.add(Filter.eq("specialtySpecification", specification.getId()));
//					Date preDate = DateUtil.getPreDay(new Date());
//					orderItemFilters.add(Filter.ge("createTime", DateUtil.getStartOfDay(preDate)));
//					orderItemFilters.add(Filter.le("createTime", DateUtil.getEndOfDay(preDate)));
//					
//					List<BusinessOrderItem> items = businessOrderItemService.findList(null, orderItemFilters, null);
//					
//					if (items.size() > 0) {
//						ProviderSales sales = new ProviderSales();
//						sales.setProviderId(provider);
//						sales.setSpecialtyId(specialty);
//						sales.setSpecialtySpecificationId(specification);
//						sales.setSalesTime(preDate);
//						Integer salesQuantity = 0;
//						BigDecimal salesAmount = new BigDecimal(0.0);
//						for (BusinessOrderItem item : items) {
//							if (item.getBusinessOrder().getIsShow() == false) {
//								salesQuantity += item.getQuantity();
//								salesAmount = salesAmount.add(item.getOriginalPrice().multiply(new BigDecimal(item.getQuantity())));
//							}
//						}
//						sales.setSalesQuantity(salesQuantity);
//						sales.setSalesAmount(salesAmount);
//						providerSalesServiceImpl.save(sales);
//					}
//				}
//			}
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String nowaStr = format.format(new Date());
			String startStr = format.format(provider.getStartTime());
			String endStr = format.format(DateUtil.getNextDay(provider.getEndTime()));
			if(nowaStr.equals(startStr)) {
				provider.setState(true);
				providerServiceImpl.update(provider);
			}
			if(nowaStr.equals(endStr)) {
				provider.setState(false);
				providerServiceImpl.update(provider);
			}
			
		}
	}

}
