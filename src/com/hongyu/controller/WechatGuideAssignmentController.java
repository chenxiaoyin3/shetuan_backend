package com.hongyu.controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.jca.cci.core.RecordExtractor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.plugin.StoragePlugin;
import com.grain.plugin.file.FilePlugin;
import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.RefundInfo;
import com.hongyu.entity.RefundRecords;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.entity.StorePreSave;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.service.RefundInfoService;
import com.hongyu.service.RefundRecordsService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StorePreSaveService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.hongyu.util.QrcodeUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;

@Controller
@RequestMapping("/wechat/guideGroup/")
public class WechatGuideAssignmentController {
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
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
		
	@Resource(name ="payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;

	@Value("${guide.guideVisitorSite}")
	private String guideVisitorSite;
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,Integer status, Integer guideId,Integer balanceStatus,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate){
		Json json=new Json();
		try {
			List<Filter>filters=new LinkedList<>();
			if(status!=null){
				filters.add(Filter.eq("status", status));
			}
			if(guideId!=null){
				filters.add(Filter.eq("guideId", guideId));
			}
			if(balanceStatus!=null){
				filters.add(Filter.eq("balanceStatus", balanceStatus));
			}
			if(startDate!=null){
				filters.add(Filter.ge("startDate", startDate));
			}
			if(endDate!=null){
				filters.add(Filter.le("startDate", endDate));
			}
			pageable.setFilters(filters);
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			
			Page<GuideAssignment> page=guideAssignmentService.findPage(pageable);
			List<Map<String, Object>> result=new LinkedList<>();
			for(GuideAssignment tmp:page.getRows()){
				Map<String, Object> m=new HashMap<>();
				m.put("id", tmp.getId());
				m.put("createDate", tmp.getCreateDate());
				m.put("modifyDate", tmp.getModifyDate());
				m.put("operator", tmp.getOperator());
				m.put("operatorPhone", tmp.getOperatorPhone());
				m.put("lineType", tmp.getLineType());
				m.put("assignmentType", tmp.getAssignmentType());
				m.put("serviceType", tmp.getServiceType());
				m.put("startDate", tmp.getStartDate());
				m.put("endDate", tmp.getEndDate());
				m.put("teamType", tmp.getTeamType());
				m.put("days", tmp.getDays());
				m.put("lineName", tmp.getLineName());
				m.put("status", tmp.getStatus());
				m.put("balanceStatus", tmp.getBalanceStatus());
				m.put("tip", tmp.getTip());
				m.put("serviceFee", tmp.getServiceFee());
				m.put("totalFee", tmp.getTotalFee());
				m.put("visitorFeedbackQrcode", tmp.getVisitorFeedbackQRcode());
				m.put("balanceStatus", tmp.getBalanceStatus());
				result.add(m);
			}
			Map<String, Object> hMap=new HashMap<>();
			hMap.put("total", page.getTotal());
			hMap.put("pageNumber", page.getPageNumber());
			hMap.put("pageSize", page.getPageSize());
			hMap.put("rows", result);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try {
			GuideAssignment guideAssignment=guideAssignmentService.find(id);
			if(guideAssignment!=null){
				Map<String, Object> map = new HashMap<>();
				map.put("id", guideAssignment.getId());
				map.put("createDate", guideAssignment.getCreateDate());
				map.put("modifyDate", guideAssignment.getModifyDate());
				map.put("guideId", guideAssignment.getGuideId());
				map.put("assignmentType", guideAssignment.getAssignmentType());
				map.put("groupId", guideAssignment.getGroupId());
				map.put("lineType", guideAssignment.getLineType());
				map.put("teamType", guideAssignment.getTeamType());
				map.put("orderId", guideAssignment.getOrderId());
				map.put("serviceType", guideAssignment.getServiceType());
				map.put("qiangdanId", guideAssignment.getQiangdanId());
				map.put("startDate", guideAssignment.getStartDate());
				map.put("endDate", guideAssignment.getEndDate());
				map.put("days", guideAssignment.getDays());
				map.put("lineName", guideAssignment.getLineName());
				map.put("travelProfile", guideAssignment.getTravelProfile());
				map.put("elseService", guideAssignment.getElseService());
				map.put("tip", guideAssignment.getTip());
				map.put("serviceFee", guideAssignment.getServiceFee());
				map.put("totalFee", guideAssignment.getTotalFee());
				HyAdmin hyAdmin = hyAdminService.find(guideAssignment.getOperator());
				map.put("operator", hyAdmin == null ? "" : hyAdmin.getName());
				map.put("operatorPhone", guideAssignment.getOperatorPhone());
				map.put("paiqianDate", guideAssignment.getPaiqianDate());
				map.put("confirmDate", guideAssignment.getConfirmDate());
				map.put("quxiaoDate", guideAssignment.getQuxiaoDate());
				map.put("status", guideAssignment.getStatus());
				map.put("reason", guideAssignment.getReason());
				map.put("visitorFeedbackQRcode", guideAssignment.getVisitorFeedbackQRcode());
				map.put("balanceStatus", guideAssignment.getBalanceStatus());
				map.put("reviewStatus", guideAssignment.getReviewStatus());
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(map);
			}else{
				json.setSuccess(false);
				json.setMsg("获取失败，记录不存在");
			}
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	

	
	@RequestMapping("confirm")
	@ResponseBody
	public Json confirm(Long id,Integer state,String comment,HttpSession session){
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		Json json=new Json();
		try {
			GuideAssignment guideAssignment=guideAssignmentService.find(id);
			if(state==1){
				guideAssignment.setStatus(1);//已确认
				Long groupId=guideAssignment.getGroupId();
				Long assignmentId =guideAssignment.getId();
				if(guideAssignment.getAssignmentType()!=null&&guideAssignment.getAssignmentType()==1) {
					Long orderId=guideAssignment.getOrderId();
					HyOrder hyOrder=hyOrderService.find(orderId);
					hyOrder.setStatus(3);//导游确认
					hyOrder.setGuideCheckStatus(1);	//导游已确认
					hyOrderService.update(hyOrder);
				}
				String url = guideVisitorSite + "groupId=" + groupId+"&assignmentId="+assignmentId;
				StoragePlugin filePlugin = new FilePlugin();
				String uuid = UUID.randomUUID() + "";
				String location = "/"+Constants.resourcesPath+"/qrcode/" + uuid + ".jpg";
				String tmp = System.getProperty("java.io.tmpdir") + "/upload_" + uuid + ".tmp";
				File file = QrcodeUtil.getQrcode(url, 200, tmp);
				filePlugin.upload(location, file, null);
				guideAssignment.setVisitorFeedbackQRcode(location);
			}else{
				guideAssignment.setStatus(2);//已驳回
				guideAssignment.setReason(comment);
				if(guideAssignment.getAssignmentType()!=null&&guideAssignment.getAssignmentType()==1) {
					Long orderId=guideAssignment.getOrderId();
					HyOrder hyOrder=hyOrderService.find(orderId);
					hyOrder.setStatus(6);//导游驳回
					hyOrder.setGuideCheckStatus(2);	//导游已驳回
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
						records.setPayer(hyAdmin.getName());;
					records.setAmount(money);
					records.setStoreId(storeId);
					records.setStoreName(store.getStoreName());
					records.setTouristName(hyOrder.getContact());
					records.setTouristAccount(store.getBankList().getBankAccount());  //门店账号
					records.setSignUpMethod(1);   //门店
					refundRecordsService.save(records);		
					
					//退款记录
					PayandrefundRecord payandrefundRecord = new PayandrefundRecord();
					payandrefundRecord.setCreatetime(date);
					payandrefundRecord.setMoney(money);
					payandrefundRecord.setOrderId(orderId);
					payandrefundRecord.setPayMethod(5);
					payandrefundRecord.setStatus(1);
					payandrefundRecord.setType(1);
					payandrefundRecordService.save(payandrefundRecord);
					
					
					
					//生成订单日志
					HyOrderApplication application = new HyOrderApplication();
					application.setContent("导游驳回退款");
					application.setOperator(admin);
					application.setOrderId(orderId);
					application.setCreatetime(date);
					application.setStatus(HyOrderApplication.STATUS_ACCEPT);
					application.setType(HyOrderApplication.PROVIDER_REJECT_ORDER);
					hyOrderApplicationService.save(application);
					
				}
			}
			guideAssignment.setConfirmDate(new Date());
			guideAssignmentService.update(guideAssignment);
			json.setSuccess(true);
			json.setMsg("确认成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("确认错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("cancle")
	@ResponseBody
	public Json cancle(Long id,String comment,HttpSession session){
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal); 
			json=guideAssignmentService.changeStatus(id, comment,username);
			
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("取消错误");
			e.printStackTrace();
		}
		return json;
			
	}
	
	

}
