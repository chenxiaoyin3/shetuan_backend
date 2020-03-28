package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hongyu.Filter;
import com.hongyu.entity.Provider;
import com.hongyu.entity.ProviderBalance;
import com.hongyu.entity.ProviderBalanceItem;
import com.hongyu.service.ProviderBalanceItemService;
import com.hongyu.service.ProviderBalanceService;
import com.hongyu.service.ProviderService;
import com.hongyu.task.Processor;
import com.hongyu.util.DateUtil;

//在供货商结算明细统计的时间段后进行
@Component("providerBalanceProcessor")
public class ProviderBalanceProcessor implements Processor {
	
	@Resource(name="providerBalanceItemServiceImpl")
	ProviderBalanceItemService providerBalanceItemServiceImpl;
	
	@Resource(name="providerBalanceServiceImpl")
	ProviderBalanceService providerBalanceService; 
	
	@Resource(name="providerServiceImpl")
	ProviderService providerServiceImpl;
	
	@Override
//	@Transactional
	public void process() {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date current = new Date();
//		try {
//			current = sdf.parse("2018-08-01");
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//先统计单结的
		Date yesterDay = DateUtil.getPreDay(current);
		Date yesterDayStart = DateUtil.getStartOfDay(yesterDay);
		Date yesterDayEnd= DateUtil.getEndOfDay(yesterDay);
		
		Calendar calendarMonthStart = Calendar.getInstance();
		calendarMonthStart.add(Calendar.MONTH, -1);
		calendarMonthStart.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar calendarPreMonthEnd = Calendar.getInstance();
		calendarPreMonthEnd.set(Calendar.DAY_OF_MONTH, 1); 
		calendarPreMonthEnd.add(Calendar.DATE, -1);
		
		Date preMonthStartDate = DateUtil.getStartOfDay(calendarMonthStart.getTime());
		Date preMonthEndDate = DateUtil.getEndOfDay(calendarPreMonthEnd.getTime());
		
		List<Filter> providerFilter = new ArrayList<Filter>();
		//单结的类型为0
		providerFilter.add(Filter.eq("balanceType", Integer.valueOf(0)));
		List<Provider> providers = providerServiceImpl.findList(null, providerFilter, null);
		
		List<Filter> balanceFilter = new ArrayList<Filter>();
		for (Provider provider : providers) {
			balanceFilter.clear();
			balanceFilter.add(Filter.ge("createTime", yesterDayStart));
			balanceFilter.add(Filter.le("createTime", yesterDayEnd));
			balanceFilter.add(Filter.eq("state", false));
			balanceFilter.add(Filter.eq("provider", provider));
			List<ProviderBalanceItem> items = providerBalanceItemServiceImpl.findList(null, balanceFilter, null);
			if (!items.isEmpty()) {
				ProviderBalance providerBalance = new ProviderBalance();
				BigDecimal balanceMoney = new BigDecimal(0.0);
				for (ProviderBalanceItem item : items) {
					balanceMoney = balanceMoney.add(item.getCostPrice());
				}
				
				for (ProviderBalanceItem item : items) {
					item.setBalanceTime(new Date());
					item.setState(true);
					providerBalanceItemServiceImpl.update(item);
				}
				
				providerBalance.setProvider(provider);
				providerBalance.setName(provider.getProviderName());
				providerBalance.setBalanceType(0);
				providerBalance.setBalanceMoney(balanceMoney);
				providerBalance.setStartTime(yesterDayStart);
				providerBalance.setEndTime(yesterDayEnd);
				providerBalance.setPayeeName(provider.getAccountName());
				providerBalance.setPayeeBank(provider.getBankName());
				providerBalance.setPayeeAccount(provider.getBankAccount());
				providerBalanceService.save(providerBalance);
			}
			
			//为什么还要考虑上个月的月结，因为有可能供货商由月结转成了日结
			balanceFilter.clear();
			balanceFilter.add(Filter.ge("createTime", preMonthStartDate));
			balanceFilter.add(Filter.le("createTime", preMonthEndDate));
			balanceFilter.add(Filter.eq("state", false));
			balanceFilter.add(Filter.eq("provider", provider));
			List<ProviderBalanceItem> monthItems = providerBalanceItemServiceImpl.findList(null, balanceFilter, null);
			if (!monthItems.isEmpty()) {
				ProviderBalance providerBalance = new ProviderBalance();
				BigDecimal balanceMoney = new BigDecimal(0.0);
				for (ProviderBalanceItem item : monthItems) {
					balanceMoney = balanceMoney.add(item.getCostPrice());
				}
				
				for (ProviderBalanceItem item : monthItems) {
					item.setBalanceTime(new Date());
					item.setState(true);
					providerBalanceItemServiceImpl.update(item);
				}
				
				providerBalance.setProvider(provider);
				providerBalance.setName(provider.getProviderName());
				providerBalance.setBalanceType(0);
				providerBalance.setBalanceMoney(balanceMoney);
				providerBalance.setStartTime(preMonthStartDate);
				providerBalance.setEndTime(preMonthEndDate);
				providerBalance.setPayeeName(provider.getAccountName());
				providerBalance.setPayeeBank(provider.getBankName());
				providerBalance.setPayeeAccount(provider.getBankAccount());
				providerBalanceService.save(providerBalance);
			}
		}
		
		//考虑月结
		providerFilter.clear();
		//查找结算类型是月结的供货商
		providerFilter.add(Filter.eq("balanceType", Integer.valueOf(1)));
		Calendar now = Calendar.getInstance();
		//查找结算日是当天的供货商
		providerFilter.add(Filter.eq("balanceDate", Integer.valueOf(now.get(Calendar.DAY_OF_MONTH))));
		List<Provider> monthProviders = providerServiceImpl.findList(null, providerFilter, null);
		for (Provider provider : monthProviders) {
			balanceFilter.clear();
			balanceFilter.add(Filter.ge("createTime", preMonthStartDate));
			balanceFilter.add(Filter.le("createTime", preMonthEndDate));
			balanceFilter.add(Filter.eq("state", false));
			balanceFilter.add(Filter.eq("provider", provider));
			List<ProviderBalanceItem> items = providerBalanceItemServiceImpl.findList(null, balanceFilter, null);
			if (!items.isEmpty()) {
				ProviderBalance providerBalance = new ProviderBalance();
				BigDecimal balanceMoney = new BigDecimal(0.0);
				for (ProviderBalanceItem item : items) {
					balanceMoney = balanceMoney.add(item.getCostPrice());
				}
				
				for (ProviderBalanceItem item : items) {
					item.setBalanceTime(new Date());
					item.setState(true);
					providerBalanceItemServiceImpl.update(item);
				}
				
				providerBalance.setProvider(provider);
				providerBalance.setName(provider.getProviderName());
				providerBalance.setBalanceType(0);
				providerBalance.setBalanceMoney(balanceMoney);
				providerBalance.setStartTime(preMonthStartDate);
				providerBalance.setEndTime(preMonthEndDate);
				providerBalance.setPayeeName(provider.getAccountName());
				providerBalance.setPayeeBank(provider.getBankName());
				providerBalance.setPayeeAccount(provider.getBankAccount());
				providerBalanceService.save(providerBalance);
			}
		}
		
	}

}
