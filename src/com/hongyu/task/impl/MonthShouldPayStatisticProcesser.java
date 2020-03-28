package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.annotations.Filters;
import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.MonthShouldPayInterT;
import com.hongyu.entity.PaymentSupplier;
import com.hongyu.entity.ReceiptServicer;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.MonthShouldPayInterTService;
import com.hongyu.service.PaymentSupplierService;
import com.hongyu.service.ReceiptServicerService;
import com.hongyu.task.Processor;


@Component("monthShouldPayStatisticProcessor")
public class MonthShouldPayStatisticProcesser implements Processor{

	//receiptServicer和paymentSupplier两个service
	@Resource(name = "receiptServicerServiceImpl")
	ReceiptServicerService receiptServicerService;
	
	@Resource(name = "paymentSupplierServiceImpl")
	PaymentSupplierService paymentSupplierService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	@Resource(name = "monthShouldPayInterTServiceImpl")
	MonthShouldPayInterTService monthShouldPayInterTService;
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		//第二天01:00:00进行写表操作
		//对每一个供应商，统计今天内的金额变动，添加到供应商对应的当月的行中
		//每个供应商的本月增加都是 未付 本月减少是已付
		try {
				
			Date date = new Date();
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			//上一天
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			//赋值给date
			date = calendar.getTime();
			//找到当前日期上一天所在月的第一天
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date dateForSelect = calendar.getTime();
			
			List<HySupplier> hySuppliers = hySupplierService.findAll();
			//对于每个供应商，找到自己今天的未付和已付
			for(HySupplier hySupplier : hySuppliers) {
				//查询数据库中有没有startdate大于等于date的行，没有就新建
				List<Filter> filters = new LinkedList<>();
				filters.add(Filter.eq("supplierName", hySupplier.getSupplierName()));
				filters.add(Filter.ge("startMonth", dateForSelect));
				MonthShouldPayInterT mspt = null; 
				List<MonthShouldPayInterT> mList = monthShouldPayInterTService.findList(null, filters, null);
				if(mList.size() == 0) {
					//新建一行
					mspt = new MonthShouldPayInterT();
					mspt.setMonthDecreaseMoney(new BigDecimal(0));
					mspt.setMonthIncreaseMoney(new BigDecimal(0));
					mspt.setStartMonth(dateForSelect);
					mspt.setSupplierName(hySupplier.getSupplierName());
					//初始金额，是查询之前所有的未付-已付
					//查找之前月份的结束值 如果之前月份没有值，则设置为0
					List<Filter> filters1 = new LinkedList<>();
					filters1.add(Filter.eq("supplierName", hySupplier.getSupplierName()));
					calendar.setTime(dateForSelect);
					//大于等于上个月的1号
					calendar.add(Calendar.MONTH, -1);
					filters1.add(Filter.ge("startMonth", calendar.getTime()));
					//小于这个月的一号
					filters1.add(Filter.lt("startMonth", dateForSelect));
					List<MonthShouldPayInterT> mList1 = monthShouldPayInterTService.findList(null, filters1, null);
					if(mList1.size() == 0) {
						mspt.setMonthStartMoney(new BigDecimal(0));
						mspt.setDebtMoney(new BigDecimal(0));
					}
					else {
						//设置为上个月结束值
						mspt.setMonthStartMoney(mList1.get(0).getMonthEndMoney());
						//设置为上个月供应商欠虹宇的欠款值
						mspt.setDebtMoney(mList1.get(0).getDebtMoney());
					}
					mspt.setMonthEndMoney(mspt.getMonthStartMoney());
					//写入数据库
					monthShouldPayInterTService.save(mspt);
				}
				else {
					mspt = mList.get(0);
				}
				//写入mspt
				List<Filter> filters2 = new LinkedList<>();
				filters2.add(Filter.eq("supplierName", hySupplier.getSupplierName()));
				//未付
				//filters2.add(Filter.ne("status", 3));
				//且除了已驳回的
				filters2.add(Filter.ne("status", 4));
				//创建时间大于等于今天00:00:00 小于今天23:59:59
				calendar.setTime(date);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				filters2.add(Filter.ge("createTime", calendar.getTime()));
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
				calendar.set(Calendar.MILLISECOND, 999);
				filters2.add(Filter.le("createTime", calendar.getTime()));
				List<PaymentSupplier> paymentSuppliers = paymentSupplierService.findList(null, filters2, null);
				for(PaymentSupplier paymentSupplier : paymentSuppliers) {
					if(paymentSupplier.getDebtamount() == null) {
						paymentSupplier.setDebtamount(new BigDecimal(0));
					}
					//对每一个筛选出的未支付打款单
					//增加本月增加
					mspt.setMonthIncreaseMoney(mspt.getMonthIncreaseMoney().add(paymentSupplier.getMoneySum()).subtract(paymentSupplier.getDebtamount()));
					//月末金额增加
					mspt.setMonthEndMoney(mspt.getMonthEndMoney().add(paymentSupplier.getMoneySum()).subtract(paymentSupplier.getDebtamount()));
				}
				//清空过滤器
				filters2.clear();
				filters2.add(Filter.eq("supplierName", hySupplier.getSupplierName()));
				//已付
				filters2.add(Filter.eq("status", 3));
				//创建时间大于等于今天00:00:00 小于今天23:59:59
				calendar.setTime(date);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				filters2.add(Filter.ge("payDate", calendar.getTime()));
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
				calendar.set(Calendar.MILLISECOND, 999);
				filters2.add(Filter.le("payDate", calendar.getTime()));
				List<PaymentSupplier> paymentSuppliers1 = paymentSupplierService.findList(null, filters2, null);
				for(PaymentSupplier paymentSupplier : paymentSuppliers1) {
					if(paymentSupplier.getDebtamount() == null) {
						paymentSupplier.setDebtamount(new BigDecimal(0));
					}
					//对每一个筛选出的已支付打款单
					//增加本月减少
					mspt.setMonthDecreaseMoney(mspt.getMonthDecreaseMoney().add(paymentSupplier.getMoneySum()).subtract(paymentSupplier.getDebtamount()));
					//月末金额减少
					mspt.setMonthEndMoney(mspt.getMonthEndMoney().subtract(paymentSupplier.getMoneySum()).add(paymentSupplier.getDebtamount()));
				}
				
				//对于每个今天增加的欠款
				//对每个供应商合同的负责人
				List<String> liables = new LinkedList<>();
				for(HySupplierContract hySupplierContract :hySupplier.getHySupplierContracts()) {
					liables.add(hySupplierContract.getLiable().getUsername());
				}
				//供应商未签约
				if(liables.size() == 0) {
					monthShouldPayInterTService.update(mspt);
					continue;
				}
				//找到供应商所有负责人后，作为筛选条件
				//统计供应商欠虹宇欠款的增加数和减少数
				filters2.clear();
				filters2.add(Filter.in("supplierName", liables));
				//增加欠款
				//filters2.add(Filter.eq("state", 0));
				//创建时间大于等于今天00:00:00 小于今天23:59:59
				calendar.setTime(date);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				filters2.add(Filter.ge("date", calendar.getTime()));
				calendar.set(Calendar.HOUR_OF_DAY, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
				calendar.set(Calendar.MILLISECOND, 999);
				filters2.add(Filter.le("date", calendar.getTime()));
				List<ReceiptServicer> receiptServicers = receiptServicerService.findList(null, filters2, null);
				for(ReceiptServicer receiptServicer : receiptServicers) {
					if(receiptServicer.getState() == 0) {
						//增加
						mspt.setDebtMoney(mspt.getDebtMoney().add(receiptServicer.getAmount()));
						//月末欠供应商金额减少
						mspt.setMonthEndMoney(mspt.getMonthEndMoney().subtract(receiptServicer.getAmount()));
					}
					else {
						//减少
						mspt.setDebtMoney(mspt.getDebtMoney().subtract(receiptServicer.getAmount()));
						//月末欠供应商金额增加
						mspt.setMonthEndMoney(mspt.getMonthEndMoney().add(receiptServicer.getAmount()));
					}
				}
				//更新数据库
				monthShouldPayInterTService.update(mspt);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
		}
		
		
	}

}
