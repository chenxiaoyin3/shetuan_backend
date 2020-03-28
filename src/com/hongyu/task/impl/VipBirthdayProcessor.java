package com.hongyu.task.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.BusinessSystemSetting;
import com.hongyu.entity.Receiver;
import com.hongyu.entity.Vip;
import com.hongyu.service.BusinessSystemSettingService;
import com.hongyu.service.ReceiverService;
import com.hongyu.service.VipService;
import com.hongyu.service.ViplevelService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.task.Processor;
import com.hongyu.util.SendMessageEMY;

/**
 * 扫描微信会员生日的定时器
 *
 */
@Component("vipBirthdayProcessor")
public class VipBirthdayProcessor implements Processor {
	@Resource(name="wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;
	
	@Resource(name="vipServiceImpl")
	VipService vipService;
	
	@Resource(name="viplevelServiceImpl")
	ViplevelService viplevelService;
	
	@Resource(name="receiverServiceImpl")
	ReceiverService receiverService;
	
	@Resource(name="businessSystemSettingServiceImpl")
	BusinessSystemSettingService businessSystemSettingService;
	
	@Override
	public void process() {
		try {
			List<Vip> vipList=vipService.findAll();
			String str="尊敬的会员您好，游买有卖商城祝您生日快乐，幸福安康！感谢您一直以来的关注和支持！";
			StringBuilder sb1 = new StringBuilder();
			sb1.append(str);
			Date currentDate=new Date();	
			int baijinVip_count=0,huangjinVip_count=0;
			for(Vip vip:vipList) {
				Date birthday=vip.getBirthday();
				if(birthday!=null) {
					if(getMonthOfDate(birthday)==getMonthOfDate(currentDate)&&getDayOfDate(birthday)==getDayOfDate(currentDate)) {
						String phone=vip.getWechatAccount().getPhone();
						if(phone!=null) {
//							SendMessageEMY.businessSendMessage(phone, sb1.toString());
							//write by wj
							String message = "";
							SendMessageEMY.businessSendMessage(phone, message,6);
							
						}
						else {
							List<Filter> receFilter=new ArrayList<Filter>();
							receFilter.add(Filter.eq("wechat_id", vip.getWechatAccount().getId()));
							receFilter.add(Filter.eq("isVipAddress", true));
							List<Receiver> receiverList=receiverService.findList(null,receFilter,null);
							if(receiverList.size()>0) {
								if(receiverList.get(0).getReceiverMobile()!=null) {
//									SendMessageEMY.businessSendMessage(receiverList.get(0).getReceiverMobile(), sb1.toString());
									//write by wj
									String message = "";
									SendMessageEMY.businessSendMessage(receiverList.get(0).getReceiverMobile(), message,6);
								}	
							}
						}
						if(vip.getViplevelId()==2) {
							baijinVip_count++;
						}
						else if(vip.getViplevelId()==3) {
							huangjinVip_count++;
						}
					}
				}			
			}
			if(baijinVip_count>0 || huangjinVip_count>0) {
				StringBuilder sb2=new StringBuilder();
				String str2="今天生日的";						
				if(baijinVip_count>0 && huangjinVip_count==0) {
					str2=str2+"白金会员"+baijinVip_count+"人。";
				}
				else if(baijinVip_count==0 && huangjinVip_count>0) {
					str2=str2+"黄金会员"+huangjinVip_count+"人。";
				}
				else if(baijinVip_count>0 && huangjinVip_count>0) {
					str2=str2+"白金会员"+baijinVip_count+"人，";
					str2=str2+"黄金会员"+huangjinVip_count+"人。";
				}
				sb2.append(str2);
				List<Filter> filters=new ArrayList<Filter>();
				filters.add(Filter.like("settingName", "运营人员手机号"));
				List<BusinessSystemSetting> settingList=businessSystemSettingService.findList(null,filters,null);
				String yunyingPhone=settingList.get(0).getSettingValue();
//				SendMessageEMY.businessSendMessage(yunyingPhone, sb2.toString());
				//write by wj
				String num1 = baijinVip_count+"";
				String num2 = huangjinVip_count+"";
				String message = "{\"num1\":\""+num1+"\",\"num2\":\""+num2+"\"}";
				SendMessageEMY.businessSendMessage(yunyingPhone, message,7);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	    //获取月份
		public static int getMonthOfDate(Date date) {
		    int m = 0;
		    Calendar cd = Calendar.getInstance();
		    cd.setTime(date);
		    m = cd.get(Calendar.MONTH) + 1;
		    return m;
		}
		//获取日
		public static int getDayOfDate(Date date) {
		    int d = 0;
		    Calendar cd = Calendar.getInstance();
		    cd.setTime(date);
		    d = cd.get(Calendar.DAY_OF_MONTH);
		    return d;
	    }
}
