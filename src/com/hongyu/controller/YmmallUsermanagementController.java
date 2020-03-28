package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.CouponAward;
import com.hongyu.entity.CouponAwardAccount;
import com.hongyu.entity.CouponBalanceUse;
import com.hongyu.entity.VerificationCode;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.CouponAwardAccountService;
import com.hongyu.service.CouponAwardService;
import com.hongyu.service.CouponBalanceUseService;
import com.hongyu.service.VerificationCodeService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.service.WechatAccountService;
import com.hongyu.util.Constants;
import com.hongyu.util.CouponSNGenerator;
import com.hongyu.util.SendMessage;
import com.hongyu.util.SendMessageEMY;

@Controller
@RequestMapping("/ymmall/usermanagement")
public class YmmallUsermanagementController {
	@Resource(name = "couponBalanceUseServiceImpl")
	CouponBalanceUseService couponBalanceUseService;

	@Resource(name = "couponAwardServiceImpl")
	CouponAwardService couponAwardService;

	@Resource(name = "couponAwardAccountServiceImpl")
	CouponAwardAccountService couponAwardAccountService;

	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;

	@Resource(name = "verificationCodeServiceImpl")
	VerificationCodeService verificationCodeService;

	@Resource(name = "weBusinessServiceImpl")
	private WeBusinessService weBusinessService;
	
	@RequestMapping("/info")
	@ResponseBody
	public Json info(/* HttpSession session */Long wechat_id) {
		Json json = new Json();
		try {
			// Long wechat_id = (Long) session.getAttribute("wechat_id");
			WechatAccount wechatAccount = wechatAccountService.find(wechat_id);
			if (wechatAccount == null) {
				json.setSuccess(false);
				json.setMsg("账户不存在");
			} else {
				Map<String, Object> maps = new HashMap();
				maps.put("wechatAccount", wechatAccount);
				if(wechatAccount.getIsWeBusiness()) {
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("wechatOpenId",wechatAccount.getWechatOpenid()));
					List<WeBusiness> weBusinesses = weBusinessService.findList(null,filters,null);
					if(weBusinesses!=null && !weBusinesses.isEmpty()) {
						maps.put("weBusiness", weBusinesses.get(0));
					}else {
						maps.put("weBusiness", null);
					}
				}else {
					maps.put("weBusiness", null);
				}
				
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(maps);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("/validate_phone")
	@ResponseBody
	public Json validate_phone(String phone) {
		Json json = new Json();
		try {
			if (phone == null || phone.length() == 0) {
				json.setSuccess(false);
				json.setMsg("发送失败，手机号为空");
				return json;
			}
			int x;
	        String t = null;
	        Random r = new Random();
	        while (true) {
	            x = r.nextInt(999999);
	            if (x > 99999) {
	                System.out.println(x);
	                break;
	            } else continue;
	        }
	        t="验证码:" + x + " (有效期限10分钟)";
	        VerificationCode verificationCode = new VerificationCode();
			verificationCode.setPhone(phone);
			verificationCode.setVcode(x+"");
			verificationCodeService.save(verificationCode);
//	        SendMessageEMY.sendMessage(phone, t);
			
			//write by wj
			String str = "{\"code\":\""+x+"\"}";
			boolean isSuccess = SendMessageEMY.businessSendMessage(phone,str,1);
			if(isSuccess){
				json.setSuccess(true);
		        json.setMsg("发送成功");
			}else{
				json.setSuccess(false);
		        json.setMsg("发送失败");
			}
			
			json.setSuccess(true);
			json.setMsg("发送成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("发送失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("/bind_phone")
	@ResponseBody
	public Json bind_phone(String phone, String code, /* HttpSession session */Long wechat_id) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("phone", phone));
			List<WechatAccount> wechatAccounts = wechatAccountService.findList(null, filters, null);
			if (wechatAccounts.size() > 0) {
				json.setSuccess(false);
				json.setMsg("手机号已绑定");
				return json;
			}
			Date validDate=new Date(System.currentTimeMillis() - 600000);// addtime must not earlier thancurrenttime for 10min
			List<Filter> filters2 = new ArrayList<>();
			filters2.add(Filter.eq("phone", phone));
			filters2.add(Filter.eq("vcode", code));
			filters2.add(Filter.ge("createTime", validDate));
			List<VerificationCode> verificationCodes = verificationCodeService.findList(null, filters2, null);
			boolean verify = false;
			if (verificationCodes != null && verificationCodes.size() > 0) {
				verify = true;
			}
			if (verify) {
				// Long wechat_id = (Long) session.getAttribute("wechat_id");
				WechatAccount wechatAccount = wechatAccountService.find(wechat_id);
				if (wechatAccount == null) {
					json.setSuccess(false);
					json.setMsg("账户不存在");
				} else {
					if (wechatAccount.getPhone() != null && !wechatAccount.getPhone().equals("")) {
						json.setSuccess(false);
						json.setMsg("用户已绑定手机");
						return json;
					}
					wechatAccount.setPhone(phone);
					wechatAccount.setBindTime(new Date());
					wechatAccountService.update(wechatAccount);

					// 20180508 xyy 用户绑定手机后 将之前的分发的未绑定的奖励电子券进行绑定
					List<Filter> filters3 = new ArrayList<>();
					filters3.add(new Filter("receiverPhone", Operator.eq, phone));
					filters3.add(new Filter("bindNum", Operator.eq, 0)); // 未绑定
					List<CouponAwardAccount> list = couponAwardAccountService.findList(null, filters3,null);

					for (CouponAwardAccount couponAwardAccount : list) {
						filters3.clear();
						filters3.add(new Filter("couponAwardAccountId", Operator.eq, couponAwardAccount.getId()));
						List<CouponAward> list2 = couponAwardService.findList(null, filters3, null);
						for (CouponAward couponAward : list2) {
							// 在hy_oupon_award奖励电子券表中 更新数据
							couponAward.setState(1); // 0:未绑定 1:已绑定
							couponAward.setBindPhone(phone);
							couponAward.setBindPhoneTime(new Date());
							couponAward.setBindWechatAccountId(wechatAccount.getId());
							couponAward.setBindWechatTime(new Date());
							// if (c.getValidityPeriod() > 0) { //已经绑定，不需要设置过期时间
							// long expire = new Date().getTime() +
							// c.getValidityPeriod() *
							// 24 * 3600 * 1000;
							// Date d = new Date();
							// d.setTime(expire);
							// c.setExpireTime(d);
							// }
							couponAwardService.update(couponAward);

							// 在余额电子券使用记录表中增加数据
							CouponBalanceUse couponBalanceUse = new CouponBalanceUse();
							couponBalanceUse.setCouponId(couponAward.getId());
							couponBalanceUse.setWechatId(wechatAccount.getId());
							couponBalanceUse.setUseAmount(couponAward.getSum());
							couponBalanceUse.setUseTime(new Date());
							couponBalanceUse.setPhone(phone);
							couponBalanceUse.setCouponCode(code);
							couponBalanceUse.setType(2);
							couponBalanceUseService.save(couponBalanceUse);
						}
						// 更新奖励电子券统计表的绑定数量
						couponAwardAccount.setBindNum(couponAwardAccount.getNum());
						couponAwardAccountService.update(couponAwardAccount);

						// 修改微信账户的余额并更新
						
						if(wechatAccount.getTotalbalance() == null){
							wechatAccount.setTotalbalance(new BigDecimal(couponAwardAccount.getTotal()));
						}else{
							wechatAccount.setTotalbalance(
									wechatAccount.getTotalbalance().add(new BigDecimal(couponAwardAccount.getTotal())));
						}
						wechatAccountService.update(wechatAccount);
					}
				}
				json.setSuccess(true);
				json.setMsg("绑定成功");
			} else {
				json.setSuccess(false);
				json.setMsg("验证码无效");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("绑定失败");
			e.printStackTrace();
		}
		return json;
	}

}
