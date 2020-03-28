package com.hongyu.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.Design;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.ReceiptDetail;
import com.hongyu.entity.ReceiptOther;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.entity.StorePreSave;
import com.hongyu.service.DesignService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.ReceiptDetailsService;
import com.hongyu.service.ReceiptOtherService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StorePreSaveService;
import com.hongyu.service.StoreService;

@Service("designServiceImpl")
public class DesignServiceImpl extends BaseServiceImpl<Design, Long> implements DesignService {

	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;
	
	@Resource(name = "receiptDetailsServiceImpl")
	ReceiptDetailsService receiptDetailsService;
	
	@Resource(name = "receiptOtherServiceImpl")
	ReceiptOtherService receiptOtherService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;

	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@Override
	@Resource(name = "designDaoImpl")
	public void setBaseDao(BaseDao<Design, Long> baseDao) {
		super.setBaseDao(baseDao);
	}

	@Override
	public Json pay(Long id, HttpSession session) throws Exception {
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);
		Design design = this.find(id);
		int status = design.getStatus();
		if (status == 0) {// 0待估价
			json.setSuccess(false);
			json.setMsg("待行政人员估价，请稍后支付");
		} else if (status == 1) {// 1待支付
			Store store = storeService.findStore(hyAdmin);
			if (store == null) {
				json.setSuccess(false);
				json.setMsg("门店不存在");
				return json;
			} else {
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("store", store));
				List<StoreAccount> list = storeAccountService.findList(null, filters, null);
				StoreAccount storeAccount = list.get(0);
				synchronized (storeAccount) {
					if (storeAccount.getBalance().compareTo(design.getPrice()) < 0) {
						json.setSuccess(false);
						json.setMsg("余额不足，请重试");
						return json;
					}else{
						storeAccount.setBalance(storeAccount.getBalance().subtract(design.getPrice()));
						StoreAccountLog storeAccountLog=new StoreAccountLog();
						storeAccountLog.setStore(store);
						storeAccountLog.setType(6);
						storeAccountLog.setStatus(1);
						storeAccountLog.setMoney(design.getPrice());
//						storeAccountLog.setOrderSn(design.getId()+"");
						storeAccountLog.setProfile("门店海报设计支付");
						storeAccountLogService.save(storeAccountLog);
						design.setStatus(2);// 2门店支付完成，待行政人员确认
						this.update(design);
						
						
						// 修改StorePreSave
						StorePreSave storePreSave = new StorePreSave();
						storePreSave.setStoreName(store.getStoreName());

						// 1:门店充值 2:报名退款 3:报名冲抵 4:门店认购门票冲抵 5:电子门票退款 6:保险购买冲抵 7:保险弃撤 8:签证购买冲抵
						// 9:签证退款 10:酒店销售 11:酒店退款 12:门店后返 13:供应商驳回订单 14:门店租导游 15:酒加景销售 16:酒加景退款 17:门店综合服务
						storePreSave.setType(17);
						storePreSave.setDate(new Date());
						storePreSave.setAmount(design.getPrice());
						storePreSave.setPreSaveBalance(storeAccount.getBalance().subtract(design.getPrice()));
						storePreSaveService.save(storePreSave);

						// 总公司 - 收支记录 - 已收款记录
						ReceiptOther receiptOther = new ReceiptOther();
						/**
						 * 1:电子门票-门店 2:电子门票-微商 3:电子门票-官网 4:签证-门店 5:签证-微商 6:签证-官网 7:报名-门店 8:报名-微商
						 * 9:报名-官网 10:酒店-门店 11:酒店-官网 12:酒店-微商 13:门店认购门票 14:门店保险
						 * 
						 * 15:酒店-门店 16:酒店-官网 17:酒店-微商 18:门店租导游  19.门店综合服务
						 */
						
//						receiptOther.setOrderCode(hyOrder.getOrderNumber());
						receiptOther.setInstitution(store.getStoreName());
						receiptOther.setAmount(design.getPrice());
						receiptOther.setDate(new Date());
						receiptOtherService.save(receiptOther);

						// 总公司 - 收支记录 - 已收款详情
						ReceiptDetail receiptDetail = new ReceiptDetail();
						/**
						 * 1:ReceiptDepositStore 2:ReceiptDepositServicer 3:ReceiptStoreRecharge
						 * 4:ReceiptBranchRecharge 5:ReceiptDistributorRecharge
						 * 6:ReceiptBilliCycle 7:ReceiptOther
						 */
						receiptDetail.setReceiptType(7);
						receiptDetail.setReceiptId(receiptOther.getId());
						receiptDetail.setAmount(design.getPrice());
						// 1.转账 2.支付宝 3.微信支付 4.现金 5.预存款 6.刷卡
						receiptDetail.setPayMethod(5L); // 5 预存款
						receiptDetail.setDate(new Date());
//						receiptDetail.setRemark();
						receiptDetailsService.save(receiptDetail);
						
						
						json.setSuccess(true);
						json.setMsg("支付成功");
					}
				}	
			}
		} else {// 3行政人员已确认，设计中，4设计完成，门店确认已完成，5门店取消
			json.setSuccess(false);
			json.setMsg("该阶段无法支付或者支付完成，请仔细查看");
		}
		return json;
	}

}
