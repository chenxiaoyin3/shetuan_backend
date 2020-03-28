
package com.hongyu.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.entity.*;
import com.hongyu.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.JsonUtils;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.hongyu.util.WechatUtil;
import com.hongyu.util.wechatUtilEntity.TemplateMsgResult;
import com.hongyu.util.wechatUtilEntity.WechatTemplateMsg;
import com.hongyu.wrapper.lbc.GuideReviewFormWrapper;


@Controller
@RequestMapping("admin/store_assignment_guide_order/")
public class StoreAssignmentGuideOrderController {
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	
	@Resource(name = "guideServiceImpl")
	GuideService guideService;

	@Value("${guide.guideAssignmentSite}")
	private String guideAssignmentSite;
	
	/**
	 * 订单列表
	 * @param pageable
	 * @param hyOrder
	 * @param session
	 * @return
	 */
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,HyOrder hyOrder,HttpSession session) {
		Json json = new Json();
		try {
			String username = (String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("type", 0));	//租借导游类型订单
			filters.add(Filter.eq("operator", hyAdmin));	//当前门店操作人
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createtime"));
			pageable.setOrders(orders);
			Page<HyOrder> page = hyOrderService.findPage(pageable,hyOrder);
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 订单详情
	 * @param id
	 * @return
	 */
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			HyOrder hyOrder = hyOrderService.find(id);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hyOrder);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： ");
			json.setObj(e.getMessage());
			
			// TODO: handle exception
		}
		return json;
	}
	
	/**
	 * 商品详情
	 * @param id
	 * @return
	 */
	@RequestMapping("product_detail/view")
	@ResponseBody
	public Json productDetail(Pageable pageable,Long id) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("id", id));
			pageable.setFilters(filters);
			
			Page<HyOrder> hyOrders = hyOrderService.findPage(pageable);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hyOrders);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： ");
			json.setObj(e.getMessage());
			
			// TODO: handle exception
		}
		return json;
	}
	
	/**
	 * 收退款记录
	 * @param id
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "payandrefund_record_list/view")
	@ResponseBody
	public Json payAndRefundList(Long id, Integer type) {
		Json json = new Json();
		try {

			HyOrder order = hyOrderService.find(id);
			if (order == null) {
				throw new Exception("订单不存在");
			}

			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderId", id));
			filters.add(Filter.eq("type", type));

			List<PayandrefundRecord> records = payandrefundRecordService.findList(null, filters, null);

			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(records);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
		}
		return json;
	}
	

	/**
	 * 订单日志
	 * @param pageable
	 * @param id
	 * @return
	 */
	@RequestMapping("application_list/view")
	@ResponseBody
	public Json applicationList(Pageable pageable, Long id) {
		Json json = new Json();
		try {
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("orderId", id));
			pageable.setFilters(filters);
			List<Order> orders = new LinkedList<>();

			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<HyOrderApplication> page = hyOrderApplicationService.findPage(pageable);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询错误： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	


	
	
	
	/**
	 * 支付订单
	 * @param id
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "pay")
	@ResponseBody
	public Json pay(Long id, HttpSession session) {
		Json json = new Json();
		try {
			json = hyOrderService.addStoreOrderPayment(id, session);
			if(json.isSuccess()==true) {	//如果支付成功
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				HyAdmin hyAdmin = hyAdminService.find(username);
				
				HyOrder hyOrder = hyOrderService.find(id);
				HyOrderItem hyOrderItem = hyOrder.getOrderItems().get(0);
				
				Long guideId = hyOrderItem.getProductId();
				Guide guide = guideService.find(guideId);
				
				List<Filter> assignFilters = new ArrayList<>();
				assignFilters.add(Filter.eq("orderId", id));
				List<GuideAssignment> assignments = guideAssignmentService.findList(null,assignFilters,null);
				if(assignments==null || assignments.isEmpty()) {
					throw new Exception("没有有效的派遣信息");
				}
				GuideAssignment guideAssignment = assignments.get(0);
				Integer xianlu = hyOrder.getXianlutype();
				LineType lineType;
				Boolean groupType;
				Integer teamType1;
				if (xianlu == 0 || xianlu == 1) {
					lineType = LineType.qiche;
				} else if (xianlu == 2 || xianlu == 3) {
					lineType = LineType.guonei;
				} else {
					lineType = LineType.chujing;
				}
				if ((xianlu & 1) == 1) {
					groupType = true;
					teamType1=1;
				} else {
					groupType = false;
					teamType1=0;
				}
				Integer serviceType = hyOrder.getFuwutype();
				Integer star = guide.getZongheLevel();
				Integer days = hyOrderItem.getNumber();
				Json json2 = guideService.caculate(lineType, serviceType, groupType, star, days);
				BigDecimal serviceFee;
				if (!json2.isSuccess()) {
					json = json2;
					return json;
				} else {
					serviceFee = (BigDecimal) json2.getObj();
				}
//				GuideAssignment guideAssignment = new GuideAssignment();
//				guideAssignment.setLineType(lineType);
//				guideAssignment.setTeamType(teamType1);
//				guideAssignment.setGuideId(hyOrderItem.getProductId());
//				guideAssignment.setAssignmentType(1);
//				guideAssignment.setStartDate(hyOrderItem.getStartDate());
//				guideAssignment.setEndDate(DateUtil.getDateAfterSpecifiedDays(hyOrderItem.getStartDate(),
//						hyOrderItem.getNumber()-1));
//				guideAssignment.setDays(hyOrderItem.getNumber());
//				guideAssignment.setServiceFee(serviceFee);
//
//				//导游派遣信息
//				guideAssignment.setOrderId(hyOrder.getId());
//				guideAssignment.setServiceType(hyOrder.getFuwutype());
//				guideAssignment.setLineName(hyOrder.getXianlumingcheng());
//				guideAssignment.setTravelProfile(hyOrder.getXingchenggaiyao());
//				guideAssignment.setTip(hyOrder.getTip());
//				guideAssignment.setTotalFee(guideAssignment.getTip().add(guideAssignment.getServiceFee()));
//				guideAssignment.setOperator(username);
//				guideAssignment.setOperatorPhone(hyAdmin.getMobile());
//				guideAssignment.setPaiqianDate(new Date());
//				guideAssignment.setStatus(0);
//				guideAssignmentService.save(guideAssignment);

				/**
				 * 以下给导游推送消息的代码，支付成功后再调用，这里下单时不需要
				 */
				//发推送消息
				 TemplateMsgResult templateMsgResult = null;
					TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
					// 根据具体模板参数组装
					params.put("first", WechatTemplateMsg.item("您好，河北虹宇国际旅行社给您派团啦！", "#000000"));
					String lineName=hyOrder.getXianlumingcheng();
					params.put("keyword1", WechatTemplateMsg.item(lineName, "#000000"));
					

					String teamType="";
					if(groupType==true){
						teamType="团客";
					}else{
						teamType="散客";
					}
					params.put("keyword2", WechatTemplateMsg.item(teamType, "#000000"));
					
					SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd");
					String serviceTime="";
					serviceTime+= simpleDateFormat.format(hyOrder.getFatuandate())+"-";
					serviceTime+=simpleDateFormat.format(DateUtil.getDateAfterSpecifiedDays(hyOrder.getFatuandate(),hyOrder.getTianshu()));
					params.put("keyword3", WechatTemplateMsg.item(serviceTime, "#000000"));
					

					String serviceTypeStr="";
					if(serviceType==0){
						serviceTypeStr="全陪服务";
					}else{
						serviceTypeStr="其他服务";
					}
					params.put("keyword4", WechatTemplateMsg.item(serviceTypeStr, "#000000"));
					
					String serviceFeeStr="";
					serviceFeeStr=serviceFee.add(hyOrder.getTip())+" 元";
					params.put("keyword5", WechatTemplateMsg.item(serviceFeeStr, "#000000"));
					params.put("remark", WechatTemplateMsg.item("请点击详情查看，并尽快确认！", "#000000"));
					WechatTemplateMsg wechatTemplateMsg = new WechatTemplateMsg();
					wechatTemplateMsg.setTemplate_id(Constants.Store_RentGuide_Template);
					wechatTemplateMsg.setTouser(guide.getOpenId());
					String url=guideAssignmentSite+"id="+guideAssignment.getId();
					wechatTemplateMsg.setUrl(url);
					wechatTemplateMsg.setData(params);
					String data = JsonUtils.toJson(wechatTemplateMsg);
					templateMsgResult = WechatUtil.storeRent(data);
					System.out.println(JsonUtils.toJson(templateMsgResult));
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("支付错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;

	}
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;

	/**
	 * 门店取消订单
	 * @param id
	 * @param comment
	 * @param session
	 * @return
	 */
	@RequestMapping("store_cancel")
	@ResponseBody
	public Json storeCancel(Long id,String comment,HttpSession session){
		String username = (String) session.getAttribute(CommonAttributes.Principal); 
		HyAdmin admin = hyAdminService.find(username);
		Json json=new Json();
		try {
			HyOrder order = hyOrderService.find(id);

			if (order == null) {
				throw new Exception("订单不存在");
			}
			if (order.getPaystatus().equals(Constants.HY_ORDER_PAY_STATUS_PAID)) {
				// 如果订单已支付
				throw new Exception("订单状态已支付，无法取消");
			}
			
			HyOrderItem hyOrderItem = order.getOrderItems().get(0);
			Long guideId = hyOrderItem.getProductId();
			Guide guide = guideService.find(guideId);
			
			//修改导游派遣表状态
			List<Filter> assignFilters = new ArrayList<>();
			assignFilters.add(Filter.eq("orderId", id));
			List<GuideAssignment> assignments = guideAssignmentService.findList(null,assignFilters,null);
			if(assignments==null || assignments.isEmpty()) {
				throw new Exception("没有有效的派遣信息");
			}
			GuideAssignment guideAssignment = assignments.get(0);


			guideAssignment.setStatus(3);// 已取消
			guideAssignment.setQuxiaoDate(new Date());
			guideAssignment.setReason(comment);
			guideAssignmentService.update(guideAssignment);

			Integer xianlu = order.getXianlutype();
			LineType lineType;
			Boolean groupType;
			Integer teamType1;
			if (xianlu == 0 || xianlu == 1) {
				lineType = LineType.qiche;
			} else if (xianlu == 2 || xianlu == 3) {
				lineType = LineType.guonei;
			} else {
				lineType = LineType.chujing;
			}
			if ((xianlu & 1) == 1) {
				groupType = true;
				teamType1=1;
			} else {
				groupType = false;
				teamType1=0;
			}
			Integer serviceType = order.getFuwutype();
			Integer star = guide.getZongheLevel();
			Integer days = hyOrderItem.getNumber();
			Json json2 = guideService.caculate(lineType, serviceType, groupType, star, days);
			BigDecimal serviceFee;
			if (!json2.isSuccess()) {
				json = json2;
				return json;
			} else {
				serviceFee = (BigDecimal) json2.getObj();
			}
			/**
			 * 以下给导游推送消息的代码，支付成功后再调用，这里下单时不需要
			 */
			//发推送消息
			 TemplateMsgResult templateMsgResult = null;
				TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
				// 根据具体模板参数组装
				params.put("first", WechatTemplateMsg.item("您好，河北虹宇国际旅行社取消给您的派团啦！", "#000000"));
				String lineName=order.getXianlumingcheng();
				params.put("keyword1", WechatTemplateMsg.item(lineName, "#000000"));
				

				String teamType="";
				if(groupType==true){
					teamType="团客";
				}else{
					teamType="散客";
				}
				params.put("keyword2", WechatTemplateMsg.item(teamType, "#000000"));
				
				SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd");
				String serviceTime="";
				serviceTime+= simpleDateFormat.format(order.getFatuandate())+"-";
				serviceTime+=simpleDateFormat.format(DateUtil.getDateAfterSpecifiedDays(order.getFatuandate(),order.getTianshu()));
				params.put("keyword3", WechatTemplateMsg.item(serviceTime, "#000000"));
				

				String serviceTypeStr="";
				if(serviceType==0){
					serviceTypeStr="全陪服务";
				}else{
					serviceTypeStr="其他服务";
				}
				params.put("keyword4", WechatTemplateMsg.item(serviceTypeStr, "#000000"));
				
				String serviceFeeStr="";
				serviceFeeStr=serviceFee.add(order.getTip())+" 元";
				params.put("keyword5", WechatTemplateMsg.item(serviceFeeStr, "#000000"));
				params.put("remark", WechatTemplateMsg.item("请点击详情查看，并尽快确认！", "#000000"));
				WechatTemplateMsg wechatTemplateMsg = new WechatTemplateMsg();
				wechatTemplateMsg.setTemplate_id(Constants.Store_RentGuideCancel_Template);
				wechatTemplateMsg.setTouser(guide.getOpenId());
				String url=guideAssignmentSite+"id="+guideAssignment.getId();
				wechatTemplateMsg.setUrl(url);
				wechatTemplateMsg.setData(params);
				String data = JsonUtils.toJson(wechatTemplateMsg);
				templateMsgResult = WechatUtil.storeRent(data);
				System.out.println(JsonUtils.toJson(templateMsgResult));
			
			// 设置订单状态为已取消
			order.setStatus(5);	//门店已取消
			
			//生成订单日志
			HyOrderApplication application = new HyOrderApplication();
			application.setContent("门店取消订单");
			application.setOperator(admin);
			application.setOrderId(id);
			application.setCreatetime(new Date());
			application.setStatus(HyOrderApplication.STATUS_ACCEPT);
			application.setType(HyOrderApplication.STORE_CANCEL_ORDER);
			hyOrderApplicationService.save(application);

			hyOrderService.update(order);
			json.setSuccess(true);
			json.setMsg("取消成功");
			json.setObj(null);
						
			
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("取消错误");
			e.printStackTrace();
		}
		return json;
			
	}
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	
	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;
	
	@Resource(name = "refundInfoServiceImpl")
	RefundInfoService refundInfoService;
	
	@Resource(name = "refundRecordsServiceImpl")
	RefundRecordsService refundRecordsService;
	
	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;

	@Autowired
	BranchBalanceService branchBalanceService;

	@Autowired
	BranchPreSaveService branchPreSaveService;
	
	@RequestMapping("store_refund")
	@ResponseBody
	public Json storeRefund(Long id,String comment,HttpSession session){
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		
		Date date = new Date();
		Json json = new Json();
		try {
			HyOrder hyOrder = hyOrderService.find(id);
			if(hyOrder == null) {
				throw new Exception("没有有效订单");
			}
			if(hyOrder.getStatus()==7 && hyOrder.getRefundstatus()==2){
				throw new Exception("订单已退款");
			}
			
			List<Filter> assignFilters = new ArrayList<>();
			assignFilters.add(Filter.eq("orderId", id));
			List<GuideAssignment> assignments = guideAssignmentService.findList(null,assignFilters,null);
			if(assignments==null || assignments.isEmpty()) {
				throw new Exception("没有有效的派遣信息");
			}
			GuideAssignment guideAssignment = assignments.get(0);
			
			//取消导游派遣表
			guideAssignment.setStatus(3);	//已取消
			guideAssignment.setQuxiaoDate(date);
			guideAssignment.setReason(comment);
			guideAssignmentService.update(guideAssignment);
			
			//取消订单
			hyOrder.setStatus(7);	//门店已退款
			hyOrder.setRefundstatus(2);	//全部已退款
			hyOrderService.update(hyOrder);
			
			Long storeId = hyOrder.getStoreId();
			Store store = storeService.find(storeId);
			//修改门店预存款余额
			BigDecimal money = hyOrder.getJiusuanMoney().add(hyOrder.getTip()).subtract(hyOrder.getDiscountedPrice());


			if(store.getStoreType()==2){
				Department department = store.getSuoshuDepartment();
				while(!department.getIsCompany()){
					department = department.getHyDepartment();
				}				
				HyCompany company = department.getHyCompany();
				//修改分公司余额
				List<Filter> branchBalanceFilters = new ArrayList<>();
				branchBalanceFilters.add(Filter.eq("branchId",department.getId()));

				List<BranchBalance> branchBalances = branchBalanceService.findList(null,branchBalanceFilters,null);
				if(branchBalances.size()!=0){
					BranchBalance branchBalance = branchBalances.get(0);
					branchBalance.setBranchBalance(branchBalance.getBranchBalance().add(money));
					branchBalanceService.update(branchBalance);
				}else{
					BranchBalance branchBalance = new BranchBalance();
					branchBalance.setBranchId(store.getDepartment().getId());
					branchBalance.setBranchBalance(money);
					branchBalanceService.save(branchBalance);
				}
				//分公司预存款记录
				BranchPreSave branchPreSave = new BranchPreSave();
				branchPreSave.setBranchName(company.getCompanyName());
				branchPreSave.setPreSaveBalance(branchBalances.get(0).getBranchBalance());
				branchPreSave.setAmount(money);
				branchPreSave.setBranchId(store.getDepartment().getId());
				branchPreSave.setDate(new Date());
				branchPreSave.setDepartmentName(store.getDepartment().getName());
				branchPreSave.setOrderId(hyOrder.getId());
				branchPreSave.setRemark("租借导游退款");
				branchPreSave.setType(17); //派遣导游退款
				branchPreSaveService.save(branchPreSave);

			}else{
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("store", storeId));
				List<StoreAccount> list = storeAccountService.findList(null,filters,null);
				if(list!=null && !list.isEmpty()) {
					StoreAccount storeAccount = list.get(0);
					storeAccount.setBalance(storeAccount.getBalance().add(money));
					storeAccountService.update(storeAccount);
				}else {
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
				storeAccountLog.setOrderSn(hyOrder.getOrderNumber());
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

			}




				
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
			refundInfo.setOrderId(hyOrder.getId());
			refundInfoService.save(refundInfo);	
			
			
			//生成退款记录
			RefundRecords records = new RefundRecords();
			records.setRefundInfoId(refundInfo.getId());
			records.setOrderCode(hyOrder.getOrderNumber());
			records.setOrderId(hyOrder.getId());
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
			application.setContent("门店取消退款");
			application.setOperator(admin);
			application.setOrderId(id);
			application.setCreatetime(date);
			application.setStatus(HyOrderApplication.STATUS_ACCEPT);
			application.setType(HyOrderApplication.STORE_CUSTOMER_SERVICE);
			hyOrderApplicationService.save(application);
			
			// 添加退款记录
			PayandrefundRecord record = new PayandrefundRecord();
			record.setOrderId(id);
			record.setMoney(money);
			record.setPayMethod(5);	//5预存款
			record.setType(1);	//1退款
			record.setStatus(1);	//1已退款
			record.setCreatetime(new Date());
			payandrefundRecordService.save(record);
			
			HyOrderItem hyOrderItem = hyOrder.getOrderItems().get(0);
			Long guideId = hyOrderItem.getProductId();
			Guide guide = guideService.find(guideId);
			
			/**
			 * 以下给导游推送消息的代码，门店取消定订单退款，给导游推送信息
			 */
			//发推送消息
			 TemplateMsgResult templateMsgResult = null;
				TreeMap<String, TreeMap<String, String>> params = new TreeMap<>();
				// 根据具体模板参数组装
				params.put("first", WechatTemplateMsg.item("您好，河北虹宇国际旅行社取消了您的订单！", "#000000"));
				String lineName=hyOrder.getXianlumingcheng();
				params.put("keyword1", WechatTemplateMsg.item(lineName, "#000000"));
				
				Integer xianlu = hyOrder.getXianlutype();
				Boolean groupType;
				if ((xianlu & 1) == 1) {
					groupType = true;
				} else {
					groupType = false;
				}
				String teamType="";
				if(groupType==true){
					teamType="团客";
				}else{
					teamType="散客";
				}
				params.put("keyword2", WechatTemplateMsg.item(teamType, "#000000"));
				
				SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd");
				String serviceTime="";
				serviceTime+= simpleDateFormat.format(hyOrder.getFatuandate())+"-";
				serviceTime+=simpleDateFormat.format(DateUtil.getDateAfterSpecifiedDays(hyOrder.getFatuandate(),hyOrder.getTianshu()));
				params.put("keyword3", WechatTemplateMsg.item(serviceTime, "#000000"));
				

				Integer serviceType = hyOrder.getFuwutype();
				String serviceTypeStr="";
				if(serviceType==0){
					serviceTypeStr="全陪服务";
				}else{
					serviceTypeStr="其他服务";
				}
				params.put("keyword4", WechatTemplateMsg.item(serviceTypeStr, "#000000"));
				
				BigDecimal serviceFee = hyOrder.getJiusuanMoney();
				String serviceFeeStr="";
				serviceFeeStr=serviceFee.add(hyOrder.getTip())+" 元";
				params.put("keyword5", WechatTemplateMsg.item(serviceFeeStr, "#000000"));
				params.put("remark", WechatTemplateMsg.item("请点击详情查看，并尽快确认！", "#000000"));
				WechatTemplateMsg wechatTemplateMsg = new WechatTemplateMsg();
				wechatTemplateMsg.setTemplate_id(Constants.Store_RentGuideCancel_Template);
				wechatTemplateMsg.setTouser(guide.getOpenId());
				String url=guideAssignmentSite+"id="+guideAssignment.getId();
				wechatTemplateMsg.setUrl(url);
				wechatTemplateMsg.setData(params);
				String data = JsonUtils.toJson(wechatTemplateMsg);
				templateMsgResult = WechatUtil.storeRent(data);
				System.out.println(JsonUtils.toJson(templateMsgResult));
			
			
			json.setSuccess(true);
			json.setMsg("取消成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("取消失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		
		return json;
		
		
	}
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name = "reviewFormServiceImpl")
	ReviewFormService reviewFormService;
	
	@Resource(name = "reviewFormItemServiceImpl")
	ReviewFormItemService reviewFormItemService;
	
	@Resource(name = "guideReviewDetailServiceImpl")
	GuideReviewDetailService guideReviewDetailService; 
	
	@Resource(name = "guideReviewFormServiceImpl")
	GuideReviewFormService guideReviewFormService;
	
	@Resource(name = "guideReviewFormScoreServiceImpl")
	GuideReviewFormScoreService guideReviewFormScoreService;

	
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	
	/**
	 * 根据订单id来查询评价表
	 * @param orderID 订单id
	 * @return	按道理这两个id应该唯一确定一个评价表
	 */
	
	//得到导游评价单
	@RequestMapping("editReviewForm/detail/view")
	@ResponseBody
	public Json getReviewFormById(HttpSession session,Long orderID){
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		//HyAdmin admin = hyAdminService.find(username);
		try{
			HyOrder hyOrder = hyOrderService.find(orderID);
			//根据orderID查到guideAssignment
			List<Filter> guideAssignmentfilters = new ArrayList<Filter>();
			guideAssignmentfilters.add(Filter.eq("orderId", orderID));
			List<GuideAssignment> guideAssignments = guideAssignmentService.findList(null, guideAssignmentfilters, null);
			if(guideAssignments.size() == 0) {
				json.setSuccess(false);
				json.setMsg("此订单号无对应的派遣导游");
				return json;
			}
			GuideAssignment guideAssignment = guideAssignments.get(0);
			
			//Long id = guideAssignment.getGroupId();
			Long guideId = guideAssignment.getGuideId();
			
			//HyGroup hyGroup = hyGroupService.find(id);
			Guide guide = guideService.find(guideId);
			
			//创建导游评价单模板 导游评价单id是2
			List<Filter> reviewformfilters = new ArrayList<>();
			reviewformfilters.add(Filter.eq("reviewFormType",2));
			List<ReviewForm> reviewForms = reviewFormService.findList(null, reviewformfilters, null);
			ReviewForm reviewForm;
			if(reviewForms.size() > 0) {
				reviewForm = reviewForms.get(0);
			}
			else {
				json.setSuccess(false);
				json.setMsg("无导游评价单模板");
				return json;
			}
			
			//查询review_form_id是导游评价单的review_form_item
			List<Filter> reviewformitemfilters = new ArrayList<>();
			reviewformitemfilters.add(Filter.eq("reviewForm",reviewForm));
			List<ReviewFormItem> reviewFormItems = reviewFormItemService.findList(null, reviewformitemfilters, null);
			
			HashMap<String, Object> result = new HashMap<>();
			
			GuideReviewForm guideReviewForm = null;
			/*
			 * 如果list为空,说明没有该实体
			 * 如果list不为空，将第一个实体返回
			 */
			result.put("title",reviewForm.getTitle());
			result.put("content",reviewForm.getContent());
			result.put("reviewFormItems", reviewFormItems);
			
			/*
			 * 根据属性创建过滤器列表，筛选特定实体
			 */
			List<Filter> filters = new ArrayList<>();
			Filter filter1 = Filter.eq("orderId",orderID);
			filters.add(filter1);
			Filter filter2 = Filter.eq("guideId",guideId);
			filters.add(filter2);
			/*
			 * 按照创建日期排序
			 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("id");
			orders.add(order);
			/*
			 * 获取在此过滤情况下所有的实体数
			 */
			List<GuideReviewForm> guideReviewFormList = 
					guideReviewFormService.findList(null,filters,orders);
			if(guideReviewFormList.size() > 0){
				guideReviewForm = guideReviewFormList.get(0);
			}
			if(guideReviewForm == null) {
				//创建导游评价单
				guideReviewForm = new GuideReviewForm();
				guideReviewForm.setGroupId(hyOrder.getGroupId());
				guideReviewForm.setGuideId(guideId);
				//线路名称
				guideReviewForm.setLine(guideAssignment.getLineName());
				guideReviewForm.setGuideName(guide.getName());
				guideReviewForm.setGuideSn(guide.getGuideSn());
				guideReviewForm.setGuideType(guideAssignment.getServiceType());
				guideReviewForm.setOrderId(guideAssignment.getOrderId());
				guideReviewForm.setPaiqianId(guideAssignment.getId());
				//门店评价
				guideReviewForm.setReviewType(1);
				guideReviewForm.setOrderId(guideAssignment.getOrderId());
				guideReviewForm.setStartDate(guideAssignment.getStartDate());
				guideReviewForm.setReviewer(username);
				guideReviewFormService.save(guideReviewForm);
			}
			//guideReviewForm.setPhone(guide.getPhone());
			result.put("guideReviewForm", guideReviewForm);
			json.setSuccess(true);
			json.setMsg("获取成功,生成新评价单");
			json.setObj(result);

		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("获取错误: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	
	//更新导游评价单
	@RequestMapping("editReviewForm/detail/update")
	@ResponseBody
	public Json updateReviewFormById(@RequestBody GuideReviewFormWrapper guideReviewFormWrapper, HttpSession session){
		Json json = new Json();
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			//HyAdmin admin = hyAdminService.find(username);
			
			GuideAssignment guideAssignment = guideAssignmentService.find(guideReviewFormWrapper.getGuideAssignment_id());
			
			if(guideAssignment == null) {
				json.setSuccess(false);
				json.setMsg("无此派遣ID");
				return json;
			}
			
			Long groupId = guideAssignment.getGroupId();
			Long guideId = guideAssignment.getGuideId();
			
			//HyGroup hyGroup = hyGroupService.find(groupId);
			//Guide guide = guideService.find(guideId);
			
			//1 已评价
			if(guideAssignment.getOrderId() == null) {
				json.setSuccess(false);
				json.setMsg("此派遣导游无对应订单号");
				return json;
			}
			
			HyOrder hyOrder = hyOrderService.find(guideAssignment.getOrderId());
			hyOrder.setStatus(10);
			hyOrderService.update(hyOrder);
			//guideAssignmentService.update(guideAssignment);
			
			//更新guide_review_form
			GuideReviewForm guideReviewForm = guideReviewFormService.find(guideReviewFormWrapper.getGuideReviewForm_id());
			guideReviewForm.setAdvice(guideReviewFormWrapper.getAdvice());
			guideReviewForm.setPhone(guideReviewFormWrapper.getPhone());
			guideReviewForm.setReviewTime(new Date());
			
			
			List<GuideReviewFormScore> guideReviewFormScores = guideReviewFormWrapper.getGuideReviewFormScores();
			
			int totalScore = 0;
			//更新guideReviewFormScore
			//每个guideReviewFormScore set guideReviewForm_id
			for(GuideReviewFormScore guideReviewFormScore : guideReviewFormScores) {
				guideReviewFormScore.setGuideReviewForm(guideReviewForm);
				//导游服务项目
				//guideReviewFormScore.setServiceType(0);
				guideReviewFormScoreService.save(guideReviewFormScore);
				totalScore += guideReviewFormScore.getScoreItem() * 25;
				
			}
			
			//暂时不知道totalscore计算方法
			guideReviewForm.setScoreTotal((int)(totalScore/guideReviewFormScores.size()));
			//评价人名字填当前登录的用户名
			guideReviewForm.setReviewer(username);
			guideReviewFormService.update(guideReviewForm);
			
			
			
			List<Filter> guideReviewDetailFilter1 = new ArrayList<Filter>();
			guideReviewDetailFilter1.add(Filter.eq("guideId",guideId));
			guideReviewDetailFilter1.add(Filter.eq("orderId", hyOrder.getId()));
			guideReviewDetailFilter1.add(Filter.eq("type", 1));
			List<GuideReviewDetail> guideReviewDetails2 = guideReviewDetailService.findList(null, guideReviewDetailFilter1, null);
			
			if(guideReviewDetails2.size() == 0) {
				//更新guideReviewFormDetail
				GuideReviewDetail guideReviewDetail = new GuideReviewDetail();
				//门店租借
				guideReviewDetail.setType(1);		
				guideReviewDetail.setGroupId(groupId);
				guideReviewDetail.setGuideId(guideId);
				guideReviewDetail.setGuideType(guideAssignment.getServiceType());
				guideReviewDetail.setPaiqianId(guideReviewFormWrapper.getGuideAssignment_id());
				guideReviewDetail.setScore((int)(totalScore/guideReviewFormScores.size()));
				guideReviewDetail.setStartDate(guideAssignment.getStartDate());
				guideReviewDetail.setOrderId(guideAssignment.getOrderId());
				guideReviewDetail.setTestDate(new Date());
				guideReviewDetailService.save(guideReviewDetail);
			}
			else {
				GuideReviewDetail guideReviewDetail = guideReviewDetails2.get(0);
				guideReviewDetail.setType(1);		
				guideReviewDetail.setGroupId(groupId);
				guideReviewDetail.setGuideId(guideId);
				guideReviewDetail.setGuideType(guideAssignment.getServiceType());
				guideReviewDetail.setPaiqianId(guideReviewFormWrapper.getGuideAssignment_id());
				guideReviewDetail.setScore((int)(totalScore/guideReviewFormScores.size()));
				guideReviewDetail.setStartDate(guideAssignment.getStartDate());
				guideReviewDetail.setOrderId(guideAssignment.getOrderId());
				guideReviewDetail.setTestDate(new Date());
				guideReviewDetailService.update(guideReviewDetail);
			}
			
			List<Filter> guideReviewDetailFilter = new ArrayList<Filter>();
			guideReviewDetailFilter.add(Filter.eq("guideId",guideId));
			List<GuideReviewDetail> guideReviewDetails = guideReviewDetailService.findList(null, guideReviewDetailFilter, null);
			int avgscore = 0;
			for(GuideReviewDetail guideReviewDetail1:guideReviewDetails) {
				avgscore += guideReviewDetail1.getScore();
			}
			if(guideReviewDetails.size() == 0) {
				avgscore = 0;
			}
			else {
				avgscore = (int)(avgscore / guideReviewDetails.size());
			}
			
			//根据guideId找到guide
			Guide guide = guideService.find(guideId);
			if(avgscore < 50) {
				guide.setZongheLevel(0);
			}
			else if(avgscore >=50 && avgscore <= 59) {
				guide.setZongheLevel(1);
			}
			else if(avgscore >=60 && avgscore <= 69) {
				guide.setZongheLevel(2);
			}
			else if(avgscore >=70 && avgscore <= 79) {
				guide.setZongheLevel(3);
			}
			else if(avgscore >=80 && avgscore <= 89) {
				guide.setZongheLevel(4);
			}
			else {
				guide.setZongheLevel(5);
			}
			guideService.update(guide);
			
			
			json.setSuccess(true);
			json.setMsg("更新成功,编辑原评价单");

		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("获取错误: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
}