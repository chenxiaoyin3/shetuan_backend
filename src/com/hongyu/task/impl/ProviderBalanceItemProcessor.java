package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hongyu.Filter;
import com.hongyu.dao.ProviderBalanceItemDao;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.Provider;
import com.hongyu.entity.ProviderBalanceItem;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.ProviderBalanceItemService;
import com.hongyu.service.ProviderService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.task.Processor;
import com.hongyu.util.DateUtil;

//每天在订单设置完成状态的时间段后进行
@Component("providerBalanceItemProcessor")
public class ProviderBalanceItemProcessor implements Processor {
	
	private static Logger logger = LogManager.getLogger(ProviderBalanceItemProcessor.class);
	
	@Resource(name="providerBalanceItemServiceImpl")
	ProviderBalanceItemService providerBalanceItemServiceImpl;
	
	@Resource(name="providerServiceImpl")
	ProviderService providerServiceImpl;
	
	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderServiceImpl;
	
	@Resource(name="businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;
	
	@Resource(name="specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationSrv;
	
	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyServiceImpl;
	
	@Override
	@Transactional
	public void process() {
		Date current = new Date();
		Date yesterDay = DateUtil.getPreDay(current);
		Date yesterDayStart = DateUtil.getStartOfDay(yesterDay);
		Date yesterDayEnd= DateUtil.getEndOfDay(yesterDay);
		Date createTime = DateUtils.addMinutes(DateUtils.ceiling(yesterDay, Calendar.DATE), -1);
		
		List<Provider> providers = providerServiceImpl.findAll();
		for (Provider provider : providers) {
			List<SpecialtySpecification> specifications = specialtySpecificationSrv.getSpecificationsOfProvider(provider);
			for (SpecialtySpecification specification : specifications) {
				//只需要查找供货商发货的
				List<BusinessOrderItem> orderitems = businessOrderItemService.getItemsOfSpecificationInDuration(specification, yesterDayStart, yesterDayEnd, 1);
				if (!orderitems.isEmpty()) {
					
					for (BusinessOrderItem item : orderitems) {
						ProviderBalanceItem balanceItem = new ProviderBalanceItem();
						balanceItem.setProvider(provider);
						balanceItem.setName(provider.getProviderName());
						balanceItem.setSpecialty(specification.getSpecialty());
						balanceItem.setSpecification(specification);
						
						BigDecimal costPrice = new BigDecimal(0.0);
						BigDecimal saleMoney = new BigDecimal(0.0);
						int saleCount = 0;
						try {
							BigDecimal cost = businessOrderItemService.getCostPriceOfOrderitem(item);
							BigDecimal quantity = new BigDecimal(item.getQuantity());
							if (item.getLost1Quantity() != null) {
								BigDecimal count = quantity.subtract(new BigDecimal(item.getLost1Quantity())).subtract(new BigDecimal(item.getLost2Quantity()));
								costPrice = costPrice.add(cost.multiply(count));
							}
							saleCount += item.getQuantity()-item.getReturnQuantity();
							saleMoney = saleMoney.add(item.getOriginalPrice().multiply(new BigDecimal(saleCount)));
							balanceItem.setCostPrice(costPrice);
							balanceItem.setSaleCount(saleCount);
							balanceItem.setSaleMoney(saleMoney);
							balanceItem.setCreateTime(createTime);
							if (item.getLost1Quantity() != null) {
								balanceItem.setLostCount(new BigDecimal(item.getLost1Quantity()).add(new BigDecimal(item.getLost2Quantity())));
							} else {
								balanceItem.setLostCount(new BigDecimal(0));
							}
							
							providerBalanceItemServiceImpl.save(balanceItem);
							BusinessOrder order = item.getBusinessOrder();
							//设置为已统计结算结果
							order.setIsBalance(true);
							businessOrderServiceImpl.update(order);
						} catch (Exception e) {
							logger.error("订单明细" + item.getId() + ": " + e.getMessage());
							e.printStackTrace();
							throw new RuntimeException(e.getMessage());
						}
						
					}	
				}
			}
		}
	}
	
}
