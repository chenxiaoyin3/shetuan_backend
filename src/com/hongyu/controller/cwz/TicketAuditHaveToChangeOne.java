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
import com.hongyu.entity.HyStoreFhynew;
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
import com.hongyu.service.impl.StoreServiceImpl;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.SendMessageEMY;
import com.hongyu.util.liyang.EmployeeUtil;

//这个作为供应商审核什么的接口 第三套流程


//接口负责接收后半部分的审核流程，复制到这里，上个接口不动
@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/firstticket/firstaudit/")
public class TicketAuditHaveToChangeOne {

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
	
	
	 
	 	 //供应商重新做的做在这里
		 @RequestMapping(value = "list/fifth/gys/view")
			@ResponseBody
			//筛选条件：三个状态，订单创建时间（createTime），订单名称（name），订单来源（source）
			public Json fifthPageGys(Integer paystatus, Integer checkstatus, Integer refundstatus,String startDate,String endDate,
					Integer source, String createtime, String name, HttpSession session, String orderNumber, String ticketName,
					HttpServletRequest request, Pageable pageable) {//三个筛选条件
				Json j = new Json();
				Map<String, Object> answer = new HashMap<>();
				//Page<HashMap<String, Object>> page = null;//分页
				try{
					

					Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
					
					List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
					//设定一个filter来做
					List<Filter> orderAndSceneFilter = new ArrayList<Filter>();
					//首先这个必须要是门票
					orderAndSceneFilter.add(Filter.eq("type", 4));
					//然后必须得是合适的人看到这个
					orderAndSceneFilter.add(Filter.in("supplier", hyAdmins));
					if(name != null){
						orderAndSceneFilter.add(Filter.eq("name", name));
					}
					if(paystatus != null){
						orderAndSceneFilter.add(Filter.eq("paystatus", paystatus));
					}
					if(checkstatus != null){
						orderAndSceneFilter.add(Filter.eq("checkstatus", checkstatus));
					}
					if(refundstatus != null){
						orderAndSceneFilter.add(Filter.eq("refundstatus", refundstatus));
					}
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
//					//这个日期需要转换一下 年月日时分秒
//					Date theCreatetime = DateUtil.stringToDate(createtime, DateUtil.YYYYMMDDHHMMSS);
//					if(createtime != null){
//						orderAndSceneFilter.add(Filter.eq("createtime", theCreatetime));
//					}
//					//2018-11-19
					List<Order> myOrders = new ArrayList<>();
					myOrders.add(Order.desc("createtime"));
					//之后开始筛选
					List<HyOrder> myHyOrder = hyOrderService.findList(null, orderAndSceneFilter, myOrders);
					
					//注意 ！！！！有可能多个orderItem都归属于一个order，这种在供应商那里情况出来的景区信息是一样的
					//但是怎么做区分是哪一张门票呢 我想是用customer 否则不好标识
					if(!myHyOrder.isEmpty()){
						//遍历一下 准备筛选星级
						for(HyOrder HyOrderItem : myHyOrder){
							//得到所有的Items
							List<HyOrderItem> myHyOrderItem = HyOrderItem.getOrderItems();
							//遍历Items看看哪个星级可以
							if(!myHyOrderItem.isEmpty()){
								HyOrderItem HyOrderItems = myHyOrderItem.get(0);
									//找到scene的ID，之后查找看看星级对不对
									Long sceneId = HyOrderItems.getProductId();
									List<Filter> SceneFilter = new ArrayList<Filter>();
									//没有别的筛选条件 这里只用sceneId筛选相同景区
									if(sceneId != null){
										SceneFilter.add(Filter.eq("id", sceneId));
									}
									if(ticketName != null){
										SceneFilter.add(Filter.eq("sceneName", ticketName));
									}
									//筛选
									//这里的sceneID是可以重复的，就是说订购门票的时候，如果实名制就重复存
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
					
					
				//	page = new Page<>(orderAndSceneTable, orderAndSceneTable.size(), pageable);
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
	 
	
	 
		//************************上面的接口是门店下单一类的*****************************
		
		//************************下面的接口是供应商确认一类的*****************************
		//进页面 如果是查看、编辑 都是对应detail/view接口 这个在门店下单已经复用过
		//之后查询留六类信息也都一样 都是复用过的接口
		//就剩下供应商确认 驳回什么的 审核信息
		
		//如果是确认 那么对应的接口是：provider_confirm 郭哥说这个一点都不用动
		// 供应商确认订单 
		@RequestMapping(value = "list/eighth/supplier_confirm")
		@ResponseBody
		public Json supplierConfirm(Long id, String view, Integer status, HttpSession session)
		{
			Json json=new Json();
			try {
				/**
				 * 获取当前用户
				 */
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				HyAdmin admin = hyAdminService.find(username);
				HyOrder hyOrder = hyOrderService.find(id);
				if (hyOrder == null) {
					throw new Exception("订单不存在");
				}
				if (!hyOrder.getStatus().equals(Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)) {
					throw new Exception("订单状态不对");
				}
				
				//供应商驳回
				if(status==0) {
					if (view == null || view.equals("")) {
						throw new Exception("请输入驳回意见");
					}
					supplierDismissOrderApplyService.addSupplierDismissOrderSubmit(id, view, session);
					//add by wj 2019-07-07  添加短信提示  供应商驳回订单
//					String phone = storeService.find(hyOrder.getStoreId()).getHyAdmin().getMobile();
//					if (hyOrder.getPhone() != null) {
//						phone = hyOrder.getPhone();
//					} else {
//						phone = hyOrder.getPhone();
//					}
					String phone = null;
					Long storeId = hyOrder.getStoreId();
					if(storeId!=null){
						phone = storeService.find(storeId).getHyAdmin().getMobile();
					}
					SendMessageEMY.sendMessage(phone,"",20);
					SendMessageEMY.sendMessage(phone,"",20);
				}
				
				//供应商确认通过
				else {
					hyOrder.setStatus(Constants.HY_ORDER_STATUS_PROVIDER_ACCEPT);
					
					//下面生成打款记录
					boolean isConfirm = piaowuConfirmService.orderPiaowuConfirm(id, 3, session);
					System.out.println(isConfirm);
					
				}
				
				HyOrderApplication application = new HyOrderApplication();
				application.setContent("供应商确认订单");
				application.setView(view);
				application.setStatus(status);
				application.setOrderId(id);
				application.setCreatetime(new Date());
				application.setOperator(admin);
				application.setType(HyOrderApplication.PROVIDER_CONFIRM_ORDER); // 供应商确认订单
				hyOrderApplicationService.save(application);

				hyOrderService.update(hyOrder);
				json.setSuccess(true);
				json.setMsg("确认成功");
			}
			catch(Exception e) {
				json.setSuccess(false);
				json.setMsg("确认失败:"+e.getMessage());
			}
			return json;
		}
		
		
		//供应商调整金额
		@RequestMapping(value = "list/eighth/adjust_money")
		@ResponseBody
		public Json adjustMoney(Long id, BigDecimal adjustMoney, HttpSession session) {
			Json json = new Json();
			try {
				HyOrder order = hyOrderService.find(id);
				if (order == null) {
					throw new Exception("订单无效");
				}
				if (!order.getStatus().equals(Constants.HY_ORDER_STATUS_WAIT_STORE_PAY)) {
					throw new Exception("订单状态不对");
				}
				BigDecimal oldAjustMoney = order.getAdjustMoney();
				if(oldAjustMoney==null){
					oldAjustMoney=BigDecimal.valueOf(0);
				}
				//修改订单金额
				order.setWaimaiMoney(order.getWaimaiMoney().subtract(oldAjustMoney).add(adjustMoney));
				order.setJiusuanMoney(order.getJiusuanMoney().subtract(oldAjustMoney).add(adjustMoney));
				order.setJiesuanMoney1(order.getJiesuanMoney1().subtract(oldAjustMoney).add(adjustMoney));
				order.setAdjustMoney(adjustMoney);
				
				//修改扣点金额
				if(order.getIfjiesuan()==false){	//如果没有结算
					if(order.getKoudianMethod().equals(Constants.DeductPiaowu.liushui)){
						order.setKoudianMoney(
								order.getProportion().multiply(
										order.getJiesuanMoney1()).multiply(BigDecimal.valueOf(0.01)).setScale(2, RoundingMode.HALF_UP));
					}
				}
				
				
				hyOrderService.update(order);
				json.setSuccess(true);
				json.setMsg("调整金额成功");

			} catch (Exception e) {
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg("调整金额失败");
				json.setObj(e.getMessage());
				e.printStackTrace();
			}
			return json;
		}
		
		//供应商确认订单，如果驳回的话。我看了逻辑，驳回订单的审核流程应该可以共用。各种类型的订单不一样的地方就是在财务审核通过之后，按照订单类型作不同的操作
		//修改一个controller：在hzj03---audit里面的AccountantReview_SupplierDismissOrder_Controller
		//顺便修改一个Service：找最下面的supplierDismissOrderApplyService实现类修改完毕（修改了addSupplierDismissOrderAudit）
		//url：admin/accountant/dissmissOrder/list/view  /detail/view  /audit
		//以上的都不需要复制到自己的controller
		
		//这里把上述文件改变之后，完成了财务的审核流程
		
		
		
		
		
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
		
		@Resource(name = "departmentServiceImpl")
		DepartmentService departmentService;
	
		
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
		



}
