package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.dao.PointrecordDao;
import com.hongyu.entity.CouponBalanceUse;
import com.hongyu.entity.Pointrecord;
import com.hongyu.entity.Vip;
import com.hongyu.entity.Viplevel;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.CouponBalanceUseService;
import com.hongyu.service.PointrecordService;
import com.hongyu.service.VipService;
import com.hongyu.service.ViplevelService;
import com.hongyu.service.WechatAccountService;
import com.sun.beans.WeakCache;
@Service(value = "pointrecordServiceImpl")
public class PointrecordServiceImpl extends BaseServiceImpl<Pointrecord,Long> implements PointrecordService {
	
	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;
	
	@Resource(name = "viplevelServiceImpl")
	ViplevelService viplevelService;
	
	@Resource(name = "pointrecordDaoImpl")
	PointrecordDao dao;
	
	@Resource(name = "vipServiceImpl")
	VipService vipService;

	@Resource(name = "pointrecordDaoImpl")
	public void setBaseDao(PointrecordDao dao) {
		super.setBaseDao(dao);
	}


	@Resource(name = "couponBalanceUseServiceImpl")
	CouponBalanceUseService couponBalanceUseService;
	
	
	@Override
	public void changeUserPoint(Long wechatId, Integer changevalue, String reason) throws Exception {
		// TODO Auto-generated method stub
		WechatAccount wechatAccount = wechatAccountService.find(wechatId);
		if(wechatAccount==null) {
			throw new Exception("微信用户无效");
		}
		//添加积分记录
		Pointrecord pointrecord = new Pointrecord();
		pointrecord.setWechatAccount(wechatAccount);
		pointrecord.setChangevalue(changevalue);
		pointrecord.setReason(reason);
		pointrecord.setCreateTime(new Date());
		if(reason.equals("兑换余额电子券")) {
			if(changevalue%10!=0) {
				throw new Exception("兑换积分必须是10的倍数");
			}
			//添加兑换余额记录
			pointrecord.setBalance(BigDecimal.valueOf(Math.abs(changevalue)/10));
			//修改用户余额
			if(wechatAccount.getTotalbalance()==null) {
				wechatAccount.setTotalbalance(BigDecimal.ZERO);
			}
			wechatAccount.setTotalbalance(wechatAccount.getTotalbalance().add(pointrecord.getBalance()));
			
			//添加余额兑换记录
			CouponBalanceUse couponBalanceUse = new CouponBalanceUse();
			couponBalanceUse.setPhone(wechatAccount.getPhone());
			couponBalanceUse.setType(5);	//5积分兑换
			couponBalanceUse.setState(1);
			couponBalanceUse.setUseAmount(pointrecord.getBalance().floatValue());
			couponBalanceUse.setUseTime(new Date());
			couponBalanceUse.setWechatId(wechatAccount.getId());
			couponBalanceUseService.save(couponBalanceUse);
			
		}else {
			//不是兑换余额类型的记录，则要修改用户总积分
			wechatAccount.setTotalpoint(wechatAccount.getTotalpoint()+pointrecord.getChangevalue());
			//积分变化记录余额为0
			pointrecord.setBalance(BigDecimal.ZERO);
			//根据用户总积分，更新用户会员等级
			List<Viplevel> viplevels = viplevelService.findAll();
			for(Viplevel viplevel:viplevels) {
				Integer totalPoint = wechatAccount.getTotalpoint();
				if(totalPoint<=viplevel.getEndvalue()) {
					if(totalPoint<=viplevel.getStartvalue() && wechatAccount.getIsVip().equals(false)) {
						//如果低于最低值而且用户本身不是vip，则说明不能成为会员
						break;
					}
					//如果总积分在一定范围内，设置会员等级
					if(wechatAccount.getIsVip().equals(false)) {
						wechatAccount.setIsVip(true);
					}
					List<Filter> vipFilters = new ArrayList<>();
					vipFilters.add(Filter.eq("wechatAccount", wechatAccount));
					List<Vip> vips = vipService.findList(null,vipFilters,null);
					if(vips==null || vips.isEmpty()) {
						//如果没有会员,则新建会员记录
						Vip newVip = new Vip();
						newVip.setViplevelId(viplevel.getId());
						newVip.setWechatAccount(wechatAccount);
						newVip.setCreateTime(new Date());
						vipService.save(newVip);
					}else {
						Vip oldVip = vips.get(0);
						oldVip.setViplevelId(viplevel.getId());
						vipService.update(oldVip);
					}
					break;
				}
			}
			
		}
		
		//修改用户可用积分
		if(wechatAccount.getPoint()==null) {
			wechatAccount.setPoint(0);
		}
		wechatAccount.setPoint(wechatAccount.getPoint()+pointrecord.getChangevalue());
		
		//保存记录修改
		wechatAccountService.update(wechatAccount);
		this.save(pointrecord);
		
		
		
	}




}
