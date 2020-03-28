package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.dao.VipDao;
import com.hongyu.entity.Vip;
import com.hongyu.entity.Viplevel;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.VipService;
import com.hongyu.service.ViplevelService;
import com.hongyu.service.WechatAccountService;

@Service(value = "vipServiceImpl")
public class VipServiceImpl extends BaseServiceImpl<Vip,Long> implements VipService {
	
	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;
	
	@Resource(name = "viplevelServiceImpl")
	ViplevelService viplevelService;
	
	@Resource(name = "vipDaoImpl")
	VipDao dao;

	@Resource(name = "vipDaoImpl")
	public void setBaseDao(VipDao dao) {
		super.setBaseDao(dao);
	}

	@Override
	public Viplevel getViplevelByWechatAccountId(Long id) throws Exception {
		// TODO Auto-generated method stub
		
		WechatAccount wechatAccount = wechatAccountService.find(id);
		if(wechatAccount==null) {
			return null;
			
		}
		if(wechatAccount.getIsVip().equals(false)) {
			//不是会员
			return null;
		}else {
			List<Filter> vipFilters = new ArrayList<>();
			vipFilters.add(Filter.eq("wechatAccount", wechatAccount));
			List<Vip> vips = this.findList(null,vipFilters,null);
			if(vips==null || vips.isEmpty()) {
				return null;
			}
			Vip vip = vips.get(0);
			Viplevel viplevel = viplevelService.find(vip.getViplevelId());
			if(viplevel==null) {
				return null;
			}
			return viplevel;
		}
	}

	@Override
	public void setVip318(WechatAccount wechatAccount, BigDecimal money) {
		// TODO Auto-generated method stub
		if(wechatAccount==null) {
			return;	
		}
		BigDecimal vipMoney = BigDecimal.valueOf(318);
		if(money.compareTo(vipMoney)>=0 && wechatAccount.getIsVip().equals(false)) {
			//获取会员等级
			List<Viplevel> viplevels = viplevelService.findAll();
			//设置账号为VIP
			wechatAccount.setIsVip(true);
			
			List<Filter> vipFilters = new ArrayList<>();
			vipFilters.add(Filter.eq("wechatAccount", wechatAccount));
			List<Vip> vips = this.findList(null,vipFilters,null);
			if(vips==null || vips.isEmpty()) {
				//如果没有会员,则新建会员记录
				Vip newVip = new Vip();
				newVip.setViplevelId(viplevels.get(0).getId());
				newVip.setWechatAccount(wechatAccount);
				newVip.setCreateTime(new Date());
				this.save(newVip);
			}else {
				Vip oldVip = vips.get(0);
				oldVip.setViplevelId(viplevels.get(0).getId());
				this.update(oldVip);
			}
			//保存微信用户信息
			wechatAccountService.update(wechatAccount);
			
		}
	}
}
