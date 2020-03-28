package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.WeDivideReport;
import com.hongyu.service.WeBusinessService;
import com.hongyu.service.WeDivideReportService;
import com.hongyu.task.Processor;
import com.hongyu.wechatpay.util.WechatTransferTest;

@Component("weBusinessDivideBalanceProcessor")
public class WeBusinessDivideBalanceProcessor implements Processor {
	
	private static Logger logger = LogManager.getLogger(WeBusinessDivideBalanceProcessor.class);
	
	@Resource(name = "weDivideReportServiceImpl")
	WeDivideReportService weDivideReportServiceImpl;
	
	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@Override
	public void process() {
		List<Filter> filters = new ArrayList<Filter>();
		List<Long> webusinessIds = weDivideReportServiceImpl.findWeBusinessSatisfyTransferCondition();
		if (!webusinessIds.isEmpty()) {
			DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String desc = "微信微商分成";
			for (Long id : webusinessIds) {
				filters.clear();
				WeBusiness webusiness = weBusinessService.find(id);
				filters.add(Filter.eq("weBusiness", webusiness));
				filters.add(Filter.eq("transfered", Boolean.FALSE));
				List<WeDivideReport> lists = weDivideReportServiceImpl.findList(null, filters, null);
				BigDecimal totalDivide = new BigDecimal(0.00);
				for (WeDivideReport report : lists) {
					totalDivide = totalDivide.add(report.getDivideAmount());
				}
				
				String partner_trade_no = sdf.format(new Date());
				Map<String, String> map = WechatTransferTest.transfer(webusiness.getWechatOpenId(), webusiness.getRealName(), partner_trade_no,
						totalDivide.multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString(), desc);
				if ("SUCCESS".equals(map.get("state"))) {
					for (WeDivideReport report : lists) {
						report.setTransfered(Boolean.TRUE);
						report.setTransferTime(new Date());
						weDivideReportServiceImpl.update(report);
						logger.info("微商分成日报表id: " + report.getId() + ", 分成成功!");
					}
					
				} else {
					logger.error("微商id: " + webusiness.getId() + ", 微商openid: " + webusiness.getWechatOpenId() + ", 分成失败!" + 
							     "state: " + map.get("state") + ", err_code: " + map.get("err_code") + ", err_code_des: " + 
							     map.get("err_code_des"));
				}
			}
		}
		
		
		
		
		
		
		
//		filters.add(Filter.eq("transfered", Boolean.FALSE));
//		
//		List<WeDivideReport> lists = weDivideReportServiceImpl.findList(null, filters, null);
//		DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//		String desc = "微信微商分成";
//		for (WeDivideReport report : lists) {
//			try {
//				String partner_trade_no = sdf.format(new Date());
//				Map<String, String> map = WechatTransferTest.transfer(report.getWeBusiness().getWechatOpenId(), report.getWeBusiness().getName(), partner_trade_no, report.getDivideAmount().multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString(), desc);
//				if ("SUCCESS".equals(map.get("state"))) {
//					report.setTransfered(Boolean.TRUE);
//					report.setTransferTime(new Date());
//					weDivideReportServiceImpl.update(report);
//				} else {
//					logger.error("微商id: " + report.getWeBusiness().getId() + ", 微商openid: " + report.getWeBusiness().getWechatOpenId());
//					logger.error(map.get("state"));
//					logger.error(map.get("err_code"));
//					logger.error(map.get("err_code_des"));
//				}
//			} catch (Exception e) {
//				logger.error("微商提成id: " + report.getId() +" 转账失败" );
//				e.printStackTrace();
//			}
//		}
		
	}

}
