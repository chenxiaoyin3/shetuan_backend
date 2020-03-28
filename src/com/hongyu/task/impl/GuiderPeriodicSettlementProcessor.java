package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideSettlement;
import com.hongyu.entity.GuideSettlementDetail;
import com.hongyu.entity.PayGuider;
import com.hongyu.service.GuideService;
import com.hongyu.service.GuideSettlementDetailService;
import com.hongyu.service.GuideSettlementService;
import com.hongyu.service.PayGuiderService;
import com.hongyu.task.Processor;


/**导游周期结算**/
@Component("guiderPeriodicSettlementProcessor")
public class GuiderPeriodicSettlementProcessor implements Processor{
	
	@Resource(name = "guideSettlementServiceImpl")
	GuideSettlementService guideSettlementService;
	
	@Resource(name = "guideSettlementDetailServiceImpl")
	GuideSettlementDetailService guideSettlementDetailService;
	
	@Resource(name = "guideServiceImpl")
	GuideService guideService;
	
	@Resource(name = "payGuiderServiceImpl")
	PayGuiderService payGuiderService;
	
	
	@Override
	public void process() {
		System.out.println("导游周期结算");
		
		Date date = new Date();
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);//取前一个月的同一天  
		Date startDate = cal.getTime();  
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String end = sDateFormat.format(date);
		Date tDate = new Date();
		try {
			 tDate = sDateFormat.parse(end);
			System.out.println(tDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		
		try {
			StringBuilder jpql = new StringBuilder(
					"SELECT guider_id,sum(account_payable) from hy_guide_settlement_detail WHERE `status`=0 "
					+ "and is_can_settle = true GROUP BY guider_id ");
			List<Object[]> list = guideSettlementDetailService.statis(jpql.toString());
		
			for (Object[] o : list) {
				System.out.println(o[0]);
				
				Integer guiderId = (Integer) o[0];
				Long guider =guiderId.longValue();
				BigDecimal amount = (BigDecimal) o[1];
				
				Guide g = guideService.find(guider);
				GuideSettlement guideSettlement = new GuideSettlement();
				guideSettlement.setEndDate(tDate);
				guideSettlement.setStartDate(startDate);
				guideSettlement.setGuideId(guiderId);
				guideSettlement.setName(g.getName());
				guideSettlement.setSn(g.getTouristCertificateNumber());
				guideSettlement.setTotalAmount(amount);			
				
				guideSettlementService.save(guideSettlement);
				
				PayGuider payGuider = new PayGuider();
				payGuider.setGuiderId(guider);
				payGuider.setType(2);
				payGuider.setHasPaid(0);
				payGuider.setGuider(g.getName());
				payGuider.setAmount(amount);
				payGuider.setBankName(g.getBankName());
				payGuider.setAccountName(g.getAccountName());
				payGuider.setBankAccount(g.getBankAccount());
				payGuider.setBankLink(g.getBankLink());			
				payGuider.setSettlementId(guideSettlement.getId());
				payGuiderService.save(payGuider);
	
			}
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("endDate", tDate));

			List<GuideSettlement> list2 = guideSettlementService.findList(null,filters,null);
			for(GuideSettlement g:list2){
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("status", 0));
				filters2.add(Filter.eq("guiderId", g.getGuideId()));
				List<GuideSettlementDetail> guideSettlementDetails = guideSettlementDetailService.findList(null,filters2,null);
				for(GuideSettlementDetail gui:guideSettlementDetails){
					gui.setSettlementId(g.getId());
					gui.setStatus(1);
					guideSettlementDetailService.update(gui);
				}
			}			
			System.out.println("导游周期结算成功");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("导游周期结算失败");
		}
		
	}
	
	
	
	
	
	
	

}
