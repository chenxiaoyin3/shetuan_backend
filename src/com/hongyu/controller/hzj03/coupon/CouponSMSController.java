package com.hongyu.controller.hzj03.coupon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hongyu.Filter;
import com.hongyu.entity.CouponBigCustomer;
import com.hongyu.entity.CouponSale;
import com.hongyu.entity.CouponSaleAccount;
import com.hongyu.entitycustom.CouponForSMS;
import com.hongyu.service.CouponBigCustomerService;
import com.hongyu.service.CouponSaleAccountService;
import com.hongyu.service.CouponSaleService;

/** 电子券 - 短信网址 查看 */
@Controller
@RequestMapping("/couponcode")
public class CouponSMSController {
	
	@Resource(name = "couponSaleServiceImpl")
	CouponSaleService couponSaleService;
	
	@Resource(name = "couponBigCustomerServiceImpl")
	CouponBigCustomerService couponBigCustomerService;
	
	@Resource(name = "couponSaleAccountServiceImpl")
	CouponSaleAccountService couponSaleAccountService;
	
	/** 电子券-*/
	//通过@PathVariable获取路径中的参数
	@RequestMapping(value = "/{date}/{phone}/{illegalCode}")
	public String couponMoneyAdd(@PathVariable String date, @PathVariable String phone,
			@PathVariable String illegalCode, HashMap<String, Object> map) {

		try {

			StringBuilder sb = new StringBuilder("/");
			sb.append(date);
			sb.append("/");
			sb.append(phone);
			sb.append("/");
			sb.append(illegalCode);

			String suffixurl = sb.toString();

			// 根据url后半部分查询电子券
			List<CouponForSMS> couponList = new ArrayList<>();
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("suffixurl", suffixurl));
			
			List<CouponBigCustomer> list = couponBigCustomerService.findList(null, filters, null);
			if(list != null && !list.isEmpty()){ //大客户购买电子券
				for (CouponBigCustomer c : list) {
					CouponForSMS couponForSMS = new CouponForSMS();
					couponForSMS.setCode(c.getCouponCode());
					couponForSMS.setActivateCode(c.getActivationCode());
					couponForSMS.setIsActivied(c.getState() == 0 ? "未激活" : "已激活");
					couponList.add(couponForSMS);
				}

				map.put("couponList", couponList);
			}else{ //商城销售电子券
				
				List<CouponSaleAccount> list2 = couponSaleAccountService.findList(null, filters, null);
				Long coupontSaleAccountId = list2.get(0).getId();
				
				filters.clear();
				filters.add(Filter.eq("couponSaleAccountId", coupontSaleAccountId));
				List<CouponSale> list3 = couponSaleService.findList(null, filters, null);
				
				for (CouponSale c : list3) {
					CouponForSMS couponForSMS = new CouponForSMS();
					couponForSMS.setCode(c.getCouponCode());
					couponForSMS.setActivateCode(c.getActivationCode());
					couponForSMS.setIsActivied(c.getState() == 0 ? "未激活" : "已激活");
					couponList.add(couponForSMS);
				}

				map.put("couponList", couponList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "couponSMS";
	}
}
