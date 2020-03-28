package com.hongyu.service.impl;

import java.math.BigDecimal;
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
import com.hongyu.entity.GroupSendGuide;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.RefundInfo;
import com.hongyu.entity.RefundRecords;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.entity.StorePreSave;
import com.hongyu.service.GroupSendGuideService;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.RefundInfoService;
import com.hongyu.service.RefundRecordsService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StorePreSaveService;
import com.hongyu.service.StoreService;

@Service("guideAssignmentServiceImpl")
public class GuideAssignmentServiceImpl extends BaseServiceImpl<GuideAssignment, Long>
		implements GuideAssignmentService {

	@Resource(name="groupSendGuideServiceImp")
	GroupSendGuideService groupSendGuideService;
	
	@Resource(name="guideServiceImpl")
	GuideService guideService;
	
	@Resource(name="hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;
	
	@Resource(name = "refundInfoServiceImpl")
	RefundInfoService refundInfoService;
	
	@Resource(name = "refundRecordsServiceImpl")
	RefundRecordsService refundRecordsService;
	
	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;	
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
	
	@Override
	public Json changeStatus(Long id, String comment,String username) throws Exception{
		// TODO Auto-generated method stub
		HyAdmin admin = hyAdminService.find(username);
		
		Json json = new Json();
		GuideAssignment guideAssignment = this.find(id);
		if(guideAssignment.getStatus()!=1){
			json.setSuccess(false);
			json.setMsg("当前状态不可取消");
			return json;
		}
		guideAssignment.setStatus(3);// 已取消
		guideAssignment.setQuxiaoDate(new Date());
		guideAssignment.setReason(comment);
		this.update(guideAssignment);
		if(guideAssignment.getAssignmentType()!=null&&guideAssignment.getAssignmentType()==1) {
			Long orderId=guideAssignment.getOrderId();
			HyOrder hyOrder=hyOrderService.find(orderId);
			hyOrder.setStatus(8);//导游取消
			hyOrder.setRefundstatus(2);
			hyOrderService.update(hyOrder);
			
			Long storeId = hyOrder.getStoreId();
			Date date = new Date();
			Store store = storeService.find(storeId);
			//修改门店预存款余额
			BigDecimal money = hyOrder.getJiusuanMoney().add(hyOrder.getTip()).subtract(hyOrder.getDiscountedPrice());
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("store",storeId));
			List<StoreAccount> list = storeAccountService.findList(null, filters, null);
			if(list.size()!=0){
				StoreAccount storeAccount = list.get(0);
				storeAccount.setBalance(storeAccount.getBalance().add(money));
				storeAccountService.update(storeAccount);
			}else{
				StoreAccount storeAccount = new StoreAccount();
				storeAccount.setStore(store);
				storeAccount.setBalance(money);
				storeAccountService.save(storeAccount);
			}
			
			//修改门店预存款记录表
			StoreAccountLog storeAccountLog = new StoreAccountLog();
			storeAccountLog.setStore(store);
			storeAccountLog.setType(7); //类型,0充值，1订单抵扣，2分成，3退团，4消团,5供应商驳回订单,6海报设计,7租借导游退款
			storeAccountLog.setStatus(1);  
			storeAccountLog.setMoney(money);
			storeAccountLog.setCreateDate(date);
			storeAccountLog.setProfile("租借导游退款");
			storeAccountLogService.save(storeAccountLog);
			
			//总公司财务中心门店预存款信息
			StorePreSave storePreSave = new StorePreSave();
			storePreSave.setAmount(money);
			storePreSave.setDate(date);
			storePreSave.setOrderCode(hyOrder.getOrderNumber());
			storePreSave.setOrderId(hyOrder.getId());
			StoreAccount storeAccount = storeAccountService.findList(null, filters, null).get(0);
			storePreSave.setPreSaveBalance(storeAccount.getBalance());
			storePreSave.setRemark("租借导游退款");
			storePreSave.setStoreId(storeId);
			storePreSave.setStoreName(store.getStoreName());
			storePreSave.setType(18);
			storePreSaveService.save(storePreSave);
				
			//生成已退款信息
			RefundInfo refundInfo = new RefundInfo();
			refundInfo.setAmount(money);
			refundInfo.setState(1);  //已付
			refundInfo.setType(12);  //租借导游退款
			refundInfo.setApplyDate(date);
			refundInfo.setApplyDate(date);
			refundInfo.setAppliName(username);
			refundInfo.setPayer(username);
			refundInfo.setRemark("租借导游退款");
			refundInfo.setOrderId(orderId);
			refundInfoService.save(refundInfo);	
			
			
			//生成退款记录
			RefundRecords records = new RefundRecords();
			records.setRefundInfoId(refundInfo.getId());
			records.setOrderCode(hyOrder.getOrderNumber());
			records.setOrderId(orderId);
			records.setRefundMethod((long) 1); //预存款方式
			records.setPayDate(date);
			HyAdmin hyAdmin = hyAdminService.find(username);
			if(hyAdmin!=null)
				records.setPayer(hyAdmin.getName());
			records.setAmount(money);
			records.setStoreId(storeId);
			records.setStoreName(store.getStoreName());
			records.setTouristName(hyOrder.getContact());
			records.setTouristAccount(store.getBankList().getBankAccount());  //门店账号
			records.setSignUpMethod(1);   //门店
			refundRecordsService.save(records);
			
			//生成订单日志
			HyOrderApplication application = new HyOrderApplication();
			application.setContent("导游取消退款");
			application.setOperator(admin);
			application.setOrderId(id);
			application.setCreatetime(date);
			application.setStatus(HyOrderApplication.STATUS_ACCEPT);
			application.setType(HyOrderApplication.PROVIDER_CANCEL_GROUP);
			hyOrderApplicationService.save(application);
			
			
			
		}
		if(guideAssignment.getAssignmentType()==2){
			GroupSendGuide groupSendGuide=groupSendGuideService.find(guideAssignment.getQiangdanId());
			Guide guide=guideService.find(guideAssignment.getGuideId());
			synchronized (groupSendGuide) {
				Integer sex=guide.getSex();
				if(groupSendGuide.getIsRestrictSex()!=null&&groupSendGuide.getIsRestrictSex()){
					if(sex==0){
						groupSendGuide.setWomanReceive(groupSendGuide.getWomanReceive()-1);
					}else{
						groupSendGuide.setManReceive(groupSendGuide.getManReceive()-1);
					}
				}else{
					groupSendGuide.setAllReceive(groupSendGuide.getAllReceive()-1);
				}
				groupSendGuideService.update(groupSendGuide);
			}
		}
		json.setSuccess(true);
		json.setMsg("取消成功");
		return json;
	}

	@Override
	@Resource(name = "guideAssignmentDaoImpl")
	public void setBaseDao(BaseDao<GuideAssignment, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}

}
