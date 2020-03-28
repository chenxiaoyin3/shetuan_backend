package com.hongyu.controller.cwz;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyReceiptRefund;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketScene;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.Store;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyReceiptRefundService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.HyTicketSceneService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.service.PiaowuConfirmService;
import com.hongyu.service.StoreService;
import com.hongyu.service.SupplierDismissOrderApplyService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.liyang.EmployeeUtil;

//这个作为供应商审核什么的接口 第三套流程


//接口负责接收后半部分的审核流程，复制到这里，上个接口不动
@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/ticket/audit/")
public class TicketAuditController {

	@Resource(name = "hyTicketSceneServiceImpl")
	private HyTicketSceneService hyTicketSceneService;
	
	@Resource(name = "hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;
	
	@Resource(name = "hyTicketSceneTicketManagementServiceImpl")
	private HyTicketSceneTicketManagementService hyTicketSceneTicketManagementService;
	
	@Resource(name = "hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	@Resource(name = "commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name = "hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "insuranceServiceImpl")
	private InsuranceService insuranceService;
	
	@Resource(name = "hyGroupServiceImpl")
	private HyGroupService hyGroupService;
	
	@Resource(name = "hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	
	@Resource(name="supplierDismissOrderApplyServiceImpl")
	private SupplierDismissOrderApplyService supplierDismissOrderApplyService;
	
	@Resource(name="hyReceiptRefundServiceImpl")
	private HyReceiptRefundService hyReceiptRefundService;
	
	@Resource(name="hyTicketHotelandsceneServiceImpl")
	private HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private TaskService taskService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	@Resource(name="hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;
	
	@Resource (name = "piaowuConfirmServiceImpl")
	private PiaowuConfirmService piaowuConfirmService;
	
	
	
	 //接下来就是跳到网络销售--订单中心--门票订单中心--门票订单详情  有必要限制权限 需要参照线路的（这个已经问了郭哥）
	 //先写个列表页 就是最开始的
	 //筛选条件：支付状态，确认状态，退款状态，星级(通过Item表来找)，来源，订单号 其他的都在order表
	 
	 //供应商用的是：     admin.swczyc.com/hyapi/admin/storeLineOrder/gys/list/view
	 //门店下单用的是 ：admin.swczyc.com/hyapi/admin/storeLineOrder/list/view 不一样
	 //供应商还需要重新做一个 这里的createTime是订单创建时间
	 @RequestMapping(value = "list/fifth")
		@ResponseBody
		public Json fifthPage(Integer paystatus, Integer checkstatus, Integer refundstatus,String startDate,String endDate,
				Integer star, Integer source, String createtime, String orderNumber, String ticketName, String customerName,
				HttpSession session, HttpServletRequest request, Pageable pageable) {//三个筛选条件
		 
			Json j = new Json();
			Map<String, Object> answer = new HashMap<>();
//			Page<HashMap<String, Object>> page = null;//分页
			try{
				
				Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
				
				List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
				//设定一个filter来做
				List<Filter> orderAndSceneFilter = new ArrayList<Filter>();
				//首先这个必须要是门票
				orderAndSceneFilter.add(Filter.eq("type", 4));
				//然后必须得是合适的人看到这个
				orderAndSceneFilter.add(Filter.in("operator", hyAdmins));
				if(paystatus != null){
					orderAndSceneFilter.add(Filter.eq("paystatus", paystatus));
				}
				if(checkstatus != null){
					orderAndSceneFilter.add(Filter.eq("checkstatus", checkstatus));
				}
				if(refundstatus != null){
					orderAndSceneFilter.add(Filter.eq("refundstatus", refundstatus));
				}
				//只有星级是连表查询的
//				if(star != null){
//					orderAndSceneFilter.add(Filter.eq("star", star));
//				}
				if(source != null){
					orderAndSceneFilter.add(Filter.eq("source", source));
				}
				
				if(orderNumber != null){
					orderAndSceneFilter.add(Filter.eq("orderNumber", orderNumber));
				}
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if(startDate != null) {
					orderAndSceneFilter.add(Filter.ge("createtime", sdf.parse(startDate.substring(0, 10) + " " + "00:00:00")));
				}
				if(endDate != null) {
					orderAndSceneFilter.add(Filter.le("createtime", sdf.parse(endDate.substring(0, 10) + " " + "23:59:59")));
				}
				//订单号 商品名称
				//这个日期需要转换一下 年月日时分秒
//				Date theCreatetime = DateUtil.stringToDate(createtime, DateUtil.YYYYMMDDHHMMSS);
//				if(createtime != null){
//					orderAndSceneFilter.add(Filter.eq("createtime", theCreatetime));
//				}
				
				//2018-11-19
				List<Order> myOrders = new ArrayList<>();
				myOrders.add(Order.desc("createtime"));
				
				//之后开始筛选
				List<HyOrder> myHyOrder = hyOrderService.findList(null, orderAndSceneFilter, myOrders);
				if(!myHyOrder.isEmpty()){
					//遍历一下 准备筛选星级
					for(HyOrder HyOrderItem : myHyOrder){
						//得到所有的Items
						List<HyOrderItem> myHyOrderItem = HyOrderItem.getOrderItems();
						//遍历Items看看哪个星级可以
						if(!myHyOrderItem.isEmpty()){
//							for(HyOrderItem HyOrderItems : myHyOrderItem){
							HyOrderItem HyOrderItems = myHyOrderItem.get(0);
							
							//added by GSbing in 20190716,增加按游客姓名筛选的功能
							if(customerName!=null) {
								boolean hasName = false;
								for(HyOrderCustomer customer:HyOrderItems.getHyOrderCustomers()) {
									if(customer.getName().contains(customerName)) {
										hasName=true;
									}
									if(hasName)
										break;
								}
								if(!hasName)
									continue;
							}
							
								//找到scene的ID，之后查找看看星级对不对
								Long sceneId = HyOrderItems.getProductId();
								List<Filter> SceneFilter = new ArrayList<Filter>();
								if(sceneId != null){
									SceneFilter.add(Filter.eq("id", sceneId));
								}
								if(star != null){
									SceneFilter.add(Filter.eq("star", star));
								}
								if(ticketName != null){
									SceneFilter.add(Filter.eq("sceneName", ticketName));
								}

								//筛选
								List<HyTicketScene> myTicketScene = hyTicketSceneService.findList(null, SceneFilter, null);
							
								//2018-11-19 加入 我觉得前端可能是去重了 在这里加上customer的信息
								List<HyOrderCustomer> myHyOrderCustomer = HyOrderItems.getHyOrderCustomers();
								
								//遍历看star
								for(HyTicketScene myHyTicketScene : myTicketScene){
									//筛选出来所有的变量都是符合条件的
									Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入信息
									touristsAttractionInfo.put("id", HyOrderItem.getId());
									touristsAttractionInfo.put("status", HyOrderItem.getStatus());
									touristsAttractionInfo.put("orderNumber", HyOrderItem.getOrderNumber());
									touristsAttractionInfo.put("name", HyOrderItem.getName());
									touristsAttractionInfo.put("star", myHyTicketScene.getStar());
									touristsAttractionInfo.put("source", HyOrderItem.getSource());
									touristsAttractionInfo.put("createtime", HyOrderItem.getCreatetime());
									//2018-11-19 在这里加入容错 实名制才用这个
									if(!myHyOrderCustomer.isEmpty())
										touristsAttractionInfo.put("customerName", myHyOrderCustomer.get(0).getName());
									//加入List中
									orderAndSceneTable.add((HashMap<String, Object>) touristsAttractionInfo);
								}
							}
						}
					}
				

				//固定套路
				int page = pageable.getPage();
				int rows = pageable.getRows();
				answer.put("total", orderAndSceneTable.size());
				answer.put("pageNumber", page);
				answer.put("pageSize", rows);
				//如果没有用分页信息，就要用这种方法，如果有，就要用前两种方法
				answer.put("rows", orderAndSceneTable.subList((page - 1) * rows, page * rows > orderAndSceneTable.size() ? orderAndSceneTable.size() : page * rows));
				
				//pageable.setPage(3);
				//page = new Page<>(orderAndSceneTable, orderAndSceneTable.size(), pageable);
			}catch (Exception e) {
				j.setSuccess(false);
				j.setMsg(e.getMessage());	
			}
			
			//最后返回空的json
			j.setSuccess(true);
			j.setObj(answer);
			j.setMsg("更新成功");
			return j;
			
		}
	 
		
	 //预付款界面的
	 @RequestMapping(value = "list/sixth/advance/payment")
		@ResponseBody
		public Json sixthPageAdvancePaymentDetail(Long id) {
			Json j = new Json();
			List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
			try{
				//很具传来的orderID来找到订单
				HyOrder myHyOrder = hyOrderService.find(id);
				if(myHyOrder != null){
					Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入信息
					//订单号
					touristsAttractionInfo.put("orderNumber", myHyOrder.getOrderNumber());
					//产品总数量
					touristsAttractionInfo.put("TotalNumber", myHyOrder.getPeople());
					
					List<HyOrderItem> HyOrderItems = myHyOrder.getOrderItems();
					HyOrderItem myHyOrderItem = null;
					if(!HyOrderItems.isEmpty())
						myHyOrderItem = HyOrderItems.get(0);
					if(myHyOrderItem != null){
						//产品ID
						touristsAttractionInfo.put("id", myHyOrderItem.getId());
						//产品数量
						touristsAttractionInfo.put("number", myHyOrderItem.getNumber());
						//产品信息(名称2)
						touristsAttractionInfo.put("name2", myHyOrderItem.getName());
					}
						
					//下单日期
					touristsAttractionInfo.put("createtime", myHyOrder.getCreatetime());
					//产品信息(名称1)
					touristsAttractionInfo.put("name1", myHyOrder.getName());
					//订单总额
					touristsAttractionInfo.put("jiusuanMoney", myHyOrder.getJiusuanMoney());
					//优惠金额
					touristsAttractionInfo.put("youhuiMoney", myHyOrder.getDiscountedPrice());
					//2019-5-9
					touristsAttractionInfo.put("jiesuanTuikuan", myHyOrder.getJiesuanTuikuan());
					orderAndSceneTable.add((HashMap<String, Object>) touristsAttractionInfo);
				}
				
			}catch (Exception e) {
				j.setSuccess(false);
				j.setMsg(e.getMessage());	
			}
			
			//最后返回空的json
			j.setSuccess(true);
			j.setObj(orderAndSceneTable);
			j.setMsg("更新成功");
			return j;
			
		}
	 
	 //这个写详情页 这个不需要获取权限
	 @RequestMapping(value = "list/sixth/orderDetail")
		@ResponseBody
		public Json sixthPageOrder(Long id) {
			Json j = new Json();
			List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
			try{
				//很具传来的orderID来找到订单
				HyOrder myHyOrder = hyOrderService.find(id);
				if(myHyOrder != null){
					Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入信息
					//订单号
					touristsAttractionInfo.put("orderNumber", myHyOrder.getOrderNumber());
					//产品名称
					touristsAttractionInfo.put("name", myHyOrder.getName());
					//支付状态
					touristsAttractionInfo.put("paystatus", myHyOrder.getPaystatus());
					//退款状态
					touristsAttractionInfo.put("refundstatus", myHyOrder.getRefundstatus());
					//订单状态
					touristsAttractionInfo.put("status", myHyOrder.getStatus());
					//订单金额
					touristsAttractionInfo.put("jiusuanMoney", myHyOrder.getJiusuanMoney());
					//扣点方式
					touristsAttractionInfo.put("koudianMethod", myHyOrder.getKoudianMethod());
					//扣点金额
					touristsAttractionInfo.put("koudianMoney", myHyOrder.getKoudianMoney());
					//联系电话
					touristsAttractionInfo.put("phone", myHyOrder.getPhone());
					//下单时间
					touristsAttractionInfo.put("createtime", myHyOrder.getCreatetime());
					//合同号
					touristsAttractionInfo.put("contractNumber", myHyOrder.getContractNumber());
					//确认状态
					touristsAttractionInfo.put("checkstatus", myHyOrder.getCheckstatus());
//					//产品状态 这个不用管
//					touristsAttractionInfo.put("checkstatus", myHyOrder.getCheckstatus());
					//订单来源
					touristsAttractionInfo.put("source", myHyOrder.getSource());
					//成交数量 
					List<HyOrderItem> HyOrderItems = myHyOrder.getOrderItems();
					HyOrderItem myHyOrderItem = null;
					if(!HyOrderItems.isEmpty())
						myHyOrderItem = HyOrderItems.get(0);
					if(myHyOrderItem != null)
						touristsAttractionInfo.put("numberFake", myHyOrderItem.getNumber());
					touristsAttractionInfo.put("number", myHyOrder.getPeople());
					//扣点比例
					touristsAttractionInfo.put("proportion", myHyOrder.getProportion());
					//联系人
					touristsAttractionInfo.put("contact", myHyOrder.getContact());
					//备注
					touristsAttractionInfo.put("remark", myHyOrder.getRemark());
					//是否结算
					touristsAttractionInfo.put("ifJiesuan", myHyOrder.getIfjiesuan());
					//还需要返回 优惠价格 原价（外卖价格感觉是） 调整金额
					touristsAttractionInfo.put("discountedPrice", myHyOrder.getDiscountedPrice());
					touristsAttractionInfo.put("waimaiMoney", myHyOrder.getWaimaiMoney());
					touristsAttractionInfo.put("adjustMoney", myHyOrder.getAdjustMoney());
					
					 //2018-11-15 bug之后添加
					int source1 = myHyOrder.getSource();
						if(source1 != Constants.mendian){
							touristsAttractionInfo.put("storeName", "");
						}else{
							Long storeId = myHyOrder.getStoreId();
							Store store = storeService.find(storeId);
							touristsAttractionInfo.put("storeName", store==null?"":store.getStoreName());
						}
					touristsAttractionInfo.put("discountedPrice", myHyOrder.getDiscountedPrice());
					touristsAttractionInfo.put("headProportion", myHyOrder.getHeadProportion());
					touristsAttractionInfo.put("jiesuanTuikuan", myHyOrder.getJiesuanTuikuan());
					//门票是否已经使用 需要自己用日期判断
					Date myCreateTime = myHyOrder.getCreatetime();
					int flag = myCreateTime.compareTo(new Date());
					//门票没有到期 -- 0  门票到期了 -- 1 在那一天是没到期
					if(flag > 0){
						touristsAttractionInfo.put("state", 1);
					} else {
						touristsAttractionInfo.put("state", 0);
					}
					
					orderAndSceneTable.add((HashMap<String, Object>) touristsAttractionInfo);
				}

			}catch (Exception e) {
				j.setSuccess(false);
				j.setMsg(e.getMessage());	
			}
			
			//最后返回空的json
			j.setSuccess(true);
			j.setObj(orderAndSceneTable);
			j.setMsg("更新成功");
			return j;
			
		}
	 
	 
	 
	 //这个写详情页 这个不需要获取权限
	 @RequestMapping(value = "list/sixth/customerDetail")
		@ResponseBody
		public Json sixthPageCustomer(Long id, Pageable pageable) {
			Json j = new Json();
			Page<HashMap<String, Object>> page = null;//分页
			List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
			try{
				//order--items--customers
				//很具传来的orderID来找到订单
				HyOrder myHyOrder = hyOrderService.find(id);
				List<HyOrderItem> HyOrderItems = myHyOrder.getOrderItems();
				if(!HyOrderItems.isEmpty()){
					for(HyOrderItem myHyOrderItems : HyOrderItems){
						List<HyOrderCustomer> HyOrderCustomers = myHyOrderItems.getHyOrderCustomers();
						if(!HyOrderCustomers.isEmpty()){
							for(HyOrderCustomer myHyOrderCustomer : HyOrderCustomers){
								//开始用Map赋值
								Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入信息
								touristsAttractionInfo.put("name", myHyOrderCustomer.getName());
								touristsAttractionInfo.put("number", myHyOrderCustomer.getCertificate());
								touristsAttractionInfo.put("phone", myHyOrderCustomer.getPhone());
								orderAndSceneTable.add((HashMap<String, Object>) touristsAttractionInfo);
							}
						}
					}
				}
				page = new Page<>(orderAndSceneTable, orderAndSceneTable.size(), pageable);
			}catch (Exception e) {
				j.setSuccess(false);
				j.setMsg(e.getMessage());	
			}
			
			//最后返回空的json
			j.setSuccess(true);
			j.setObj(page);
			j.setMsg("更新成功");
			return j;
			
		}
	 
	 
	 @RequestMapping(value = "list/sixth/commodityDetail")
		@ResponseBody
		public Json sixthPageCommodity(Long id) {
			Json j = new Json();
			//这个信息就只有一条
			List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
			try{
				//根据传来的orderID来找到订单
				HyOrder myHyOrder = hyOrderService.find(id);
				if(myHyOrder != null){
					List<HyOrderItem> HyOrderItems = myHyOrder.getOrderItems();
					if(!HyOrderItems.isEmpty()){
						//Items最多就只有一条
						HyOrderItem myHyOrderItem = HyOrderItems.get(0);
						Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入信息
						//状态
						touristsAttractionInfo.put("status", myHyOrder.getStatus());
						//商品编号
						touristsAttractionInfo.put("orderNumber", myHyOrder.getOrderNumber());
						//商品名称
						touristsAttractionInfo.put("name", myHyOrder.getName());
						//类型
						touristsAttractionInfo.put("type", myHyOrder.getType());
						//数量
						touristsAttractionInfo.put("number", myHyOrder.getPeople());
						//商品价格
						Long inboundId = myHyOrderItem.getPriceId();
						//用这个ID去找inbound表得到价格
						HyTicketPriceInbound hyTicketPriceInbound = hyTicketPriceInboundService.find(inboundId);
						if(hyTicketPriceInbound != null){
							//不确定 ！！！！！价格是不是结算价
							touristsAttractionInfo.put("price", myHyOrderItem.getJiesuanPrice());
							orderAndSceneTable.add((HashMap<String, Object>) touristsAttractionInfo);	
						}	
					}	
				}
			}catch (Exception e) {
				j.setSuccess(false);
				j.setMsg(e.getMessage());	
			}
			
			//最后返回空的json
			j.setSuccess(true);
			j.setObj(orderAndSceneTable);
			j.setMsg("更新成功");
			return j;
			
		}
	 
	 
	 //复用 这个接口感觉没问题 建勇那里能这么简略的写我也行
	 @RequestMapping(value = "list/sixth/refundAndPayDetail")
		@ResponseBody
		public Json sixthPageRefundAndPay(Long id, Integer type) {
		 Json json = new Json();
			try {

				HyOrder order = hyOrderService.find(id);
				if (order == null) {
					throw new Exception("订单不存在");
				}

				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("orderId", id));
				filters.add(Filter.eq("type", type));
				//2018-11-13 我看建勇加了
				List<Order> orders = new ArrayList<>();
				orders.add(Order.desc("createTime"));
				//从收-退款表里面找到记录，之后直接把记录返回给前端
				//type=0是付款  type=1是退款 
				//前端是否能够拿到相应的数据？能 建勇前端自己找数据的
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
	 
	 
	 
	 
	 //复用 前段找不到我再找  建勇的applicationList/view直接复用
	 //我觉得这个也没问题 前端要的精确的返回了
	 @RequestMapping(value = "list/sixth/dataDetail")
		@ResponseBody
		public Json sixthPageData(Long id) {
		 Json json = new Json();
			try {
				List<Filter> filters = new LinkedList<>();
				filters.add(Filter.eq("orderId", id));
				List<Order> orders = new LinkedList<>();
				orders.add(Order.desc("id"));
				List<HyOrderApplication> hyOrderApplications=hyOrderApplicationService.findList(null,filters,orders);
	            List<Map<String, Object>> result = new LinkedList<>();
				
				for(HyOrderApplication hyOrderApplication : hyOrderApplications) {
					Map<String, Object> map = new HashMap<>();
					map.put("type", hyOrderApplication.getType());//1
					map.put("createtime", hyOrderApplication.getCreatetime());//4
					map.put("id", hyOrderApplication.getId());//0
					HyAdmin myHyAdmin = hyOrderApplication.getOperator();
					String operatorName = null;
					if(myHyAdmin != null){
						operatorName = myHyAdmin.getName();
					}
					map.put("operator", operatorName);//2
//					map.put("outcome", hyOrderApplication.getOutcome());//5
					map.put("status", hyOrderApplication.getStatus());//5
					map.put("view", hyOrderApplication.getView());//3
					map.put("content", hyOrderApplication.getContent());
//					map.put("hyOrderApplicationItems", hyOrderApplication.getHyOrderApplicationItems());//6
					result.add(map);
				}	
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(result);
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询错误： " + e.getMessage());
				e.printStackTrace();
				// TODO: handle exception
			}
			return json;
		}
	 
	 
		//订单日志
		@RequestMapping("list/sixth/orderLogs/view")
		@ResponseBody
		public Json orderLogs(Long id)
		{
			Json json=new Json();
			try {
				List<Filter> filters = new LinkedList<>();
				filters.add(Filter.eq("orderId", id));
				List<Order> orders = new LinkedList<>();
				orders.add(Order.desc("id"));
				List<HyOrderApplication> hyOrderApplications=hyOrderApplicationService.findList(null,filters,orders);
	            List<Map<String, Object>> result = new LinkedList<>();
				for(HyOrderApplication hyOrderApplication : hyOrderApplications) {
					Map<String, Object> map = new HashMap<>();
					map.put("type", hyOrderApplication.getType());
					map.put("operator", hyOrderApplication.getOperator().getName());
					map.put("view", hyOrderApplication.getView()); //意见
					map.put("createTime", hyOrderApplication.getCreatetime()); //操作时间
					map.put("status", hyOrderApplication.getStatus()); //0驳回,1通过
					result.add(map);
				}
				json.setSuccess(true);
				json.setObj(result);
				json.setMsg("查询成功");
			}
			catch(Exception e) {
				json.setSuccess(false);
				json.setMsg(e.getMessage());
			}
			return json;
		}
	 
	 
	 //************************上面的接口是详情页列表页一类的*****************************
	 
	 //************************下面的接口是门店下单一类的*****************************
	 	//取消线路订单 这里变成取消门票订单 直接复用
		@RequestMapping(value = "list/seventh/store_cancel")
		@ResponseBody
		public Json cancel(Long id, HttpSession session) {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			Json json = new Json();
			try {
				//这个不能复用 注释掉建勇写的
				//hyOrderService.cancelOrder(id);
				//这里要做的是 改变状态  ---- 支付状态：待支付  订单状态：待门店支付 
				HyOrder order = hyOrderService.find(id);
				if (order == null) {
					throw new Exception("订单不存在");
				}
				if (order.getPaystatus().equals(Constants.HY_ORDER_PAY_STATUS_PAID)) {
					// 如果订单已支付
					throw new Exception("订单状态已支付，无法取消");
				}
				if (order.getStatus().equals(Constants.HY_ORDER_STATUS_CANCELED)) {
					throw new Exception("订单已经取消，不能重复取消");
				}
				// 设置订单状态为已取消
				order.setStatus(Constants.HY_ORDER_STATUS_CANCELED);
				hyOrderService.update(order);//更新回去
				
				//下面这个每次完成一个流程都需要写入一次 这里取消只有也需要
				HyOrderApplication application = new HyOrderApplication();
				application.setContent("门店取消订单");
				application.setOperator(admin);
				application.setOrderId(id);
				application.setCreatetime(new Date());
				application.setStatus(HyOrderApplication.STATUS_ACCEPT);
				application.setType(HyOrderApplication.STORE_CANCEL_ORDER);
				hyOrderApplicationService.save(application);

				//2018-11-7 加上库存
				List<HyOrderItem> myOrderItems = order.getOrderItems();
				HyOrderItem myOrderItem = myOrderItems.get(0);
				Integer myNumber = myOrderItem.getNumber();//数量
				
				Integer type = 1;//1.类型
				Date appointDate = myOrderItem.getStartDate();//2.预约日期
				Long inboundId = myOrderItem.getPriceId();//3.inboundId
				
				List<Filter> ticketInboundFilter = new ArrayList<Filter>();
				ticketInboundFilter.add(Filter.in("priceInboundId", inboundId));
				ticketInboundFilter.add(Filter.in("day", appointDate));
				ticketInboundFilter.add(Filter.in("type", type));//酒店这里是1
				List<HyTicketInbound> hyTicketInbounds = hyTicketInboundService.findList(null, ticketInboundFilter, null);
				HyTicketInbound myHyTicketInbound = null;
				if(!hyTicketInbounds.isEmpty()){
					myHyTicketInbound = hyTicketInbounds.get(0);
					//加上库存
					myHyTicketInbound.setInventory(myHyTicketInbound.getInventory() + myNumber);
					//更新回去
					hyTicketInboundService.update(myHyTicketInbound);
				}
				
				
				json.setSuccess(true);
				json.setMsg("取消成功");
				json.setObj(null);

			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("取消失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}

		//这里不需要再写接口了
		//是不是点返回之后，郭哥说不管 -- 那我就不写接口了
		//并且可以选择取消订单，取消后订单状态为已取消 (这个感觉才是需要向application表里面存的)
		//若不取消订单，则供应商可以进行调整金额，调整金额之后重新进行支付
	 
		
		
		//这个也是复用  这个是在支付页面点击--“查看详情”，之后又跳转回去 跳转的接口
		//问一问这个接口是不是一样的 这个不一定一样
		//!!!!!不是建勇写的 前端要的话 就给 不要就算了
		@RequestMapping("list/seventh/getStoreType/view")
		@ResponseBody
		public Json getStoreType(HttpSession session){
			Json json = new Json();
			try {
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				HyAdmin hyAdmin = hyAdminService.find(username);
				//我这里跟门店有关系么？用了前端会来找我
				Store store = storeService.findStore(hyAdmin);
				if(store==null){
					json.setSuccess(false);
					json.setMsg("门店不存在");
				}else{
					json.setSuccess(true);
					json.setMsg("获取成功");
					json.setObj(store.getStoreType());
				}
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("获取失败");
				e.printStackTrace();
			}
			return json;
		}
		
	 
		//复用支付的接口 这个和郭哥确认过可以用 准了
		//这个支付在addStoreOrderPayment的实现类里面改变了状态
		@RequestMapping(value = "list/seventh/pay")
		@ResponseBody
		public Json pay(Long id, HttpSession session) {
			Json json = new Json();
			try {
				//支付状态变为已支付，订单状态变为待供应商确认
				//状态要不要自己存？ 这个已经改了
				//里面的操作是必要的么？StoreAccountLog HyOrderApplication StorePreSave receiptOther receiptDetail的改变 行
				//另外 需不需要改变库存？ 不需要改库存
				
				//门店状态变为已确认 这个郭哥说没法做
				json = hyOrderService.addStoreOrderPayment(id, session);
			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("支付错误： " + e.getMessage());
				e.printStackTrace();
			}
			return json;

		}
		//************************上面的接口是门店下单一类的*****************************
		
		
		
		
		//******************************下面是退款还有售后的*********************************
	 
		//有一个向application表里存的还没有写，写在下订单的地方
		
		// 添加实收付款记录 这个应该是前端说的那个
		static class ReceiptRefund {
			public Long orderId;
			public BigDecimal money;
			public Integer type;
			public String method;
			public Date collectionTime;
			public String remark;
			public String bankNum;
			public String cusName;
			public String cusBank;
			public String cusUninum;
			public String reason;
			public BigDecimal adjustMoney;
		}
		
		@Resource(name = "hyCompanyServiceImpl")
		HyCompanyService hyCompanyService;
		
		@RequestMapping("bank/list/view")
		@ResponseBody
		public Json bankList(HttpSession session) {
			Json json = new Json();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			try {
				List<Filter> branchFilters = new  ArrayList<>();
				
				branchFilters.add(Filter.eq("hyDepartment", EmployeeUtil.getCompany(hyAdminService.find(username))));
				List<HyCompany> hyCompanys = hyCompanyService.findList(null,branchFilters,null);
				HyCompany hyCompany2 = null;
				Set<BankList> branchbankLists = null;
				if(hyCompanys.size()!=0&&!hyCompanys.isEmpty()){
					hyCompany2 =  hyCompanys.get(0);
					branchbankLists  = hyCompany2.getBankLists();
				}
				List<HashMap<String, Object>> res2 = new ArrayList<>();
				for(BankList branchbankList :branchbankLists){
					HashMap<String, Object> m = new HashMap<>();
					m.put("id", branchbankList.getId());
					m.put("branchAccountAlias", branchbankList.getAlias());
					m.put("branchBankName", branchbankList.getBankName());
					m.put("branchBankCode", branchbankList.getBankCode());
					m.put("branchBankType", branchbankList.getBankType());
					m.put("bankNum", branchbankList.getBankAccount());
					res2.add(m);
				}
				
				json.setObj(res2);
				json.setSuccess(true);
				json.setMsg("获取成功");
				
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				json.setSuccess(false);
				json.setMsg("获取失败");
				json.setObj(e);
			}
			return json;
		}
		
		
		
		@Resource(name = "departmentServiceImpl")
		DepartmentService departmentService;
		
		// 实收付款记录列表 2018-10-14晚添加
		@RequestMapping("receipt_refund/list/view")
		@ResponseBody
		public Json receiptRefundList(Long id, Integer type) {
			Json json = new Json();
			try {
				HyOrder order = hyOrderService.find(id);
				if (order == null) {
					throw new Exception("订单不存在");
				}

				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("order", order));
				filters.add(Filter.eq("type", type));
				List<Order> orders = new ArrayList<>();
				orders.add(Order.desc("createTime"));
				List<HyReceiptRefund> receiptRefunds = hyReceiptRefundService.findList(null, filters, orders);

				json.setSuccess(true);
				json.setMsg("查找成功");
				json.setObj(receiptRefunds);

			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("查找失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}

			
		@RequestMapping(value = "receipt_refund/add", method = RequestMethod.POST)
		@ResponseBody
		public Json addReceiptRefund(@RequestBody ReceiptRefund body, HttpSession session) {
			/**
			* 获取当前用户
			*/
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			Json json = new Json();
			try {
				Long orderId = body.orderId; // 订单id
				if (orderId == null) {
					throw new Exception("没有订单参数");
				}
				HyOrder order = hyOrderService.find(orderId);
				if (order == null) {
					throw new Exception("没有有效订单");
				}
				BigDecimal money = body.money; // 收退款钱数

				if (money == null) {
					throw new Exception("传入的钱数有误");
				}

				Integer type = body.type; // 类型

				String method = body.method; // 收退款方式

				Date collectionTime = body.collectionTime; // 收退款时间
				String remark = body.remark; // 备注
					
				String bankNum = body.bankNum;	//银行卡号
					
				String cusName = body.cusName;	//游客姓名
					
				String cusBank = body.cusBank;	//游客银行
					
				String cusUninum = body.cusUninum;	//游客联行号
					
				String reason = body.reason;	//原因
					
				BigDecimal adjustMoney = body.adjustMoney;	//调整金额

				HyReceiptRefund receiptRefund = new HyReceiptRefund();

				receiptRefund.setCollectionTime(collectionTime);
				receiptRefund.setCreateTime(new Date());
				receiptRefund.setMethod(method);
					
				receiptRefund.setOperator(admin);
				receiptRefund.setOrder(order);
				receiptRefund.setRemark(remark);
				receiptRefund.setStore(storeService.findStore(admin));
				receiptRefund.setType(type);
				receiptRefund.setBankNum(bankNum);
				receiptRefund.setStatus(0);	//待分公司财务确认
				receiptRefund.setBranch(departmentService.findCompanyOfDepartment(admin.getDepartment()));
				receiptRefund.setCusName(cusName);
				receiptRefund.setCusBank(cusBank);
				receiptRefund.setCusUninum(cusUninum);
				receiptRefund.setReason(reason);
				receiptRefund.setAdjustMoney(adjustMoney==null?BigDecimal.ZERO:adjustMoney);
				receiptRefund.setMoney(money);
				hyReceiptRefundService.save(receiptRefund);
				json.setSuccess(true);
				json.setMsg("添加成功");
				json.setObj(receiptRefund.getId());

			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("添加失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}
		
		
		static class MyOrderItem implements Serializable {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private Long itemId;
			private String name;
			private Integer type;
			private Integer priceType;
			private Integer number;
			private BigDecimal jiesuanPrice;
			private BigDecimal jiesuanRefund;
			private BigDecimal waimaiPrice;
			private BigDecimal waimaiRefund;
			private BigDecimal baoxianJiesuanPrice;
			private BigDecimal baoxianJiesuanRefund;
			private BigDecimal baoxianWaimaiPrice;
			private BigDecimal baoxianWaimaiRefund;
			//2018-11-16 修改一下 加一个字段
			private ArrayList<String> customerNames;
			private String customerName;
			private Integer numberOfReturn;//退货数量

			public ArrayList<String> getCustomerNames() {
				return customerNames;
			}

			public void setCustomerNames(ArrayList<String> customerNames) {
				this.customerNames = customerNames;
			}

			public Long getItemId() {
				return itemId;
			}

			public void setItemId(Long itemId) {
				this.itemId = itemId;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public Integer getType() {
				return type;
			}

			public void setType(Integer type) {
				this.type = type;
			}

			public Integer getPriceType() {
				return priceType;
			}

			public void setPriceType(Integer priceType) {
				this.priceType = priceType;
			}

			public Integer getNumber() {
				return number;
			}

			public void setNumber(Integer number) {
				this.number = number;
			}

			public BigDecimal getJiesuanPrice() {
				return jiesuanPrice;
			}

			public void setJiesuanPrice(BigDecimal jiesuanPrice) {
				this.jiesuanPrice = jiesuanPrice;
			}

			public BigDecimal getJiesuanRefund() {
				return jiesuanRefund;
			}

			public void setJiesuanRefund(BigDecimal jiesuanRefund) {
				this.jiesuanRefund = jiesuanRefund;
			}

			public BigDecimal getWaimaiPrice() {
				return waimaiPrice;
			}

			public void setWaimaiPrice(BigDecimal waimaiPrice) {
				this.waimaiPrice = waimaiPrice;
			}

			public BigDecimal getWaimaiRefund() {
				return waimaiRefund;
			}

			public void setWaimaiRefund(BigDecimal waimaiRefund) {
				this.waimaiRefund = waimaiRefund;
			}

			public BigDecimal getBaoxianJiesuanPrice() {
				return baoxianJiesuanPrice;
			}

			public void setBaoxianJiesuanPrice(BigDecimal baoxianJiesuanPrice) {
				this.baoxianJiesuanPrice = baoxianJiesuanPrice;
			}

			public BigDecimal getBaoxianJiesuanRefund() {
				return baoxianJiesuanRefund;
			}

			public void setBaoxianJiesuanRefund(BigDecimal baoxianJiesuanRefund) {
				this.baoxianJiesuanRefund = baoxianJiesuanRefund;
			}

			public BigDecimal getBaoxianWaimaiPrice() {
				return baoxianWaimaiPrice;
			}

			public void setBaoxianWaimaiPrice(BigDecimal baoxianWaimaiPrice) {
				this.baoxianWaimaiPrice = baoxianWaimaiPrice;
			}

			public BigDecimal getBaoxianWaimaiRefund() {
				return baoxianWaimaiRefund;
			}

			public void setBaoxianWaimaiRefund(BigDecimal baoxianWaimaiRefund) {
				this.baoxianWaimaiRefund = baoxianWaimaiRefund;
			}

			public String getCustomerName() {
				return customerName;
			}

			public void setCustomerName(String customerName) {
				this.customerName = customerName;
			}

			public Integer getNumberOfReturn() {
				return numberOfReturn;
			}

			public void setNumberOfReturn(Integer numberOfReturn) {
				this.numberOfReturn = numberOfReturn;
			}

		}
		
		
		/**
		 * 门店退款（售前）订单条目列表
		 */
		@RequestMapping(value = "store_refund_list")
		@ResponseBody
		public Json storeRefundList(Long id) {
			Json json = new Json();
			try {
				HyOrder order = hyOrderService.find(id);
				if (order == null) {
					throw new Exception("订单不存在");
				}

				/** 找退款规则需要注意 这里修改为1，100*0.01 */
				BigDecimal ticketRefundPercentage = new BigDecimal(1);
				
				List<MyOrderItem> lists = new ArrayList<>();
				for (HyOrderItem item : order.getOrderItems()) {
					//新建了一个Item
					MyOrderItem myOrderItem = new MyOrderItem();
					myOrderItem.setItemId(item.getId());
					myOrderItem.setType(item.getType());
					myOrderItem.setPriceType(item.getPriceType());
					myOrderItem.setName(item.getName());
					myOrderItem.setNumberOfReturn(item.getNumberOfReturn());
					//注意
					myOrderItem.setNumber(item.getNumber());
					List<HyOrderCustomer> hyOrderCustomer = item.getHyOrderCustomers();
					if(!hyOrderCustomer.isEmpty())
						myOrderItem.setCustomerName(item.getHyOrderCustomers().get(0).getName());
					myOrderItem.setJiesuanPrice(item.getJiesuanPrice());
					myOrderItem.setJiesuanRefund(item.getJiesuanPrice().multiply(ticketRefundPercentage));
					myOrderItem.setWaimaiPrice(item.getWaimaiPrice());
					myOrderItem.setWaimaiRefund(item.getWaimaiPrice().multiply(ticketRefundPercentage));
					myOrderItem.setBaoxianJiesuanPrice(BigDecimal.ZERO);//和李阳的不一样 不知道要不要修改
					myOrderItem.setBaoxianJiesuanRefund(myOrderItem.getBaoxianJiesuanPrice());
					myOrderItem.setBaoxianWaimaiPrice(BigDecimal.ZERO);
					myOrderItem.setBaoxianWaimaiRefund(myOrderItem.getBaoxianWaimaiPrice());
					//customer
					lists.add(myOrderItem);
				}
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(lists);
			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}	
		
		//门店提交退款申请
		//这个接口感觉也不需要修改 改的就是上一个 这个接口没有返回任何东西 
		//不知道会不会对customer表进行更新操作
		//李阳的接口 第一个scg_list/view 第二个storeCancelVisa/apply
		@RequestMapping(value = "store_refund/apply", method = RequestMethod.POST)
		@ResponseBody
		public Json storeRefundApply(@RequestBody HyOrderApplication application, HttpSession session) {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			Json json = new Json();
			try {
				HyOrder order = hyOrderService.find(application.getOrderId());
				if (order == null) {
					throw new Exception("订单无效");
				}
				if(order.getIfjiesuan()==true) {
					json.setSuccess(false);
					json.setMsg("结算后不能售前退款");
					json.setObj(2);
					return json;
				}
				
				List<HyOrderItem> orderItems = order.getOrderItems();
				if(orderItems==null || orderItems.isEmpty()) {
					throw new Exception("没有有效订单条目");
				}
				HyOrderItem orderItem = orderItems.get(0);
				HyTicketScene hyTicketScene = hyTicketSceneService.find(orderItem.getProductId());
				if(hyTicketScene==null) {
					throw new Exception("没有有效门票产品");//进行修改
				}



				Map<String, Object> variables = new HashMap<>();
				/**找供应商需要注意 这个对于门票来说，不需要改*/
				//找出供应商
				HyAdmin provider = hyTicketScene.getCreator();
				// 指定审核供应商
				variables.put("provider", provider.getUsername());

				// 启动流程
				ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeTuiTuan", variables);
				Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();

				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), pi.getProcessInstanceId(), "发起申请:1");
				taskService.complete(task.getId(), variables);

				application.setContent("门店售前退款");
				application.setOperator(admin);
				application.setStatus(0); // 待供应商审核
				application.setCreatetime(new Date());
				application.setProcessInstanceId(task.getProcessInstanceId());
				application.setType(HyOrderApplication.STORE_CANCEL_GROUP);
				order.setRefundstatus(1); // 订单退款状态为退款中
				//
				hyOrderService.update(order);
				//一个application有多个Item挨个遍历 把application存进去
				for (HyOrderApplicationItem item : application.getHyOrderApplicationItems()) {
					item.setHyOrderApplication(application);
				}

				hyOrderApplicationService.save(application);

				json.setSuccess(true);
				json.setMsg("门店售前退款申请成功");
				json.setObj(null);
			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("门店售前退款申请失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}
		
		
		
		@RequestMapping(value = "scs_list")
		@ResponseBody
		public Json scsList(Long id) {
			Json json = new Json();
			try {
				HyOrder order = hyOrderService.find(id);
				if (order == null) {
					throw new Exception("订单不存在");
				}

				List<MyOrderItem> lists = new ArrayList<>();
				for (HyOrderItem item : order.getOrderItems()) {
					MyOrderItem myOrderItem = new MyOrderItem();
					myOrderItem.setItemId(item.getId());
					myOrderItem.setType(item.getType());
					myOrderItem.setPriceType(item.getPriceType());
					myOrderItem.setName(item.getName());
					myOrderItem.setNumberOfReturn(item.getNumberOfReturn());
					myOrderItem.setNumber(item.getNumber());
					myOrderItem.setJiesuanPrice(item.getJiesuanPrice());
					myOrderItem.setJiesuanRefund(BigDecimal.ZERO);
					myOrderItem.setWaimaiPrice(item.getWaimaiPrice());
					myOrderItem.setWaimaiRefund(BigDecimal.ZERO);
					myOrderItem.setBaoxianJiesuanPrice(hyOrderItemService.getBaoxianJiesuanPrice(item));
					myOrderItem.setBaoxianJiesuanRefund(BigDecimal.ZERO);
					myOrderItem.setBaoxianWaimaiPrice(hyOrderItemService.getBaoxianWaimaiPrice(item));
					myOrderItem.setBaoxianWaimaiRefund(BigDecimal.ZERO);
					
//					//TODO 2018-11-16晚加上 这个加的位置不一定对
//					Long hyManagementTicketId = item.getSpecificationId();
//					HyTicketSceneTicketManagement hyTicketSceneTicketManagement = null;
//					List<String> customerNames = new ArrayList<>();//新建一个ArrayList
//					List<HyOrderCustomer> hyOrderCustomers = null;//这个拿取出来的
// 					Boolean isRealName = null;
//					if(hyManagementTicketId!=null){
//						//找到对象
//						hyTicketSceneTicketManagement = hyTicketSceneTicketManagementService.find(hyManagementTicketId);
//						isRealName = hyTicketSceneTicketManagement.getIsRealName();
//					}
//					//如果是
//					if(isRealName.equals(true)){
//						//得到所有的 hyOrderCustomers
//						hyOrderCustomers = item.getHyOrderCustomers();
//						if(!hyOrderCustomers.isEmpty())
//						for(HyOrderCustomer hyOrderCustomerItems : hyOrderCustomers){
//							//遍历拿出名字
//							customerNames.add(hyOrderCustomerItems.getName());
//						}
//						myOrderItem.setCustomerNames((ArrayList<String>) customerNames);
//					} else if(isRealName.equals(false)){
//						//不是实名制 这里什么都没有
//						myOrderItem.setCustomerNames((ArrayList<String>) customerNames);
//					}

					lists.add(myOrderItem);
				}
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(lists);
			} catch (Exception e) {
				
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}
		
		//这下面的接口可能改动比较大
		@Transactional
		@RequestMapping(value = "store_customer_service/apply", method = RequestMethod.POST)
		@ResponseBody
		public Json storeCustomerServiceApply(@RequestBody HyOrderApplication application, HttpSession session) {	
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			Json json = new Json();
			try {
				HyOrder order = hyOrderService.find(application.getOrderId());
				if (order == null) {
					throw new Exception("订单无效");
				}
				if(order.getIfjiesuan()==false) {
					json.setSuccess(false);
					json.setMsg("结算前不能售后退款");
					json.setObj(2);
					return json;
				}
				
				List<HyOrderItem> orderItems = order.getOrderItems();
				if(orderItems==null || orderItems.isEmpty()) {
					throw new Exception("没有有效订单条目");
				}
				HyOrderItem orderItem = orderItems.get(0);
				//！！！！这个明显需要修改
				HyTicketScene hyTicketScene = hyTicketSceneService.find(orderItem.getProductId());
				if(hyTicketScene==null) {
					throw new Exception("没有有效的门票产品");
				}



				Map<String, Object> variables = new HashMap<>();
				/**找供应商需要注意 这里修改完毕*/
				//找出供应商
				HyAdmin provider = hyTicketScene.getCreator();
				// 指定审核供应商
				variables.put("provider", provider.getUsername());

				// 启动流程
				ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeShouHou", variables);
				Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();

				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), pi.getProcessInstanceId(), "发起申请:1");
				taskService.complete(task.getId(), variables);

				application.setContent("门店售后退款");
				application.setOperator(admin);
				application.setStatus(0); // 待供应商审核
				application.setCreatetime(new Date());
				application.setProcessInstanceId(task.getProcessInstanceId());
				application.setType(HyOrderApplication.STORE_CUSTOMER_SERVICE);
				order.setRefundstatus(1); // 订单退款状态为退款中
				//
				hyOrderService.update(order);

				for (HyOrderApplicationItem item : application.getHyOrderApplicationItems()) {
					item.setHyOrderApplication(application);
				}

				hyOrderApplicationService.save(application);

				json.setSuccess(true);
				json.setMsg("门店售后申请成功");
				json.setObj(null);
			} catch (Exception e) {
				
				json.setSuccess(false);
				json.setMsg("门店售后申请失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}


}
