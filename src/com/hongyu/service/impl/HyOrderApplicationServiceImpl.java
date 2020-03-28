package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.util.ActivitiUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CouponLine;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyRegulate;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.entity.HyTicketHotelandsceneRoom;
import com.hongyu.entity.HyTicketInbound;

import com.hongyu.entity.HyTicketScene;
import com.hongyu.entity.HyTicketSceneTicketManagement;
import com.hongyu.entity.HyVisa;
import com.hongyu.entity.HyTicketSubscribe;

import com.hongyu.entity.Store;
import com.hongyu.service.CouponLineService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationItemService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.HyTicketHotelRoomService;
import com.hongyu.service.HyTicketHotelService;
import com.hongyu.service.HyTicketHotelandsceneRoomService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.HyTicketSceneService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.service.HyVisaService;
import com.hongyu.service.HyTicketSubscribePriceService;
import com.hongyu.service.HyTicketSubscribeService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;
import com.hongyu.util.Constants.DeductPiaowu;

@Service("hyOrderApplicationServiceImpl")
public class HyOrderApplicationServiceImpl extends BaseServiceImpl<HyOrderApplication,Long> implements HyOrderApplicationService {

	@Resource(name="hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name="hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;
	
	@Resource(name="hyGroupServiceImpl")
	private HyGroupService hyGroupService;
	
	@Resource(name="couponLineServiceImpl")
	private CouponLineService couponLineService;
	
	@Resource(name="hyOrderApplicationItemServiceImpl")
	private HyOrderApplicationItemService hyOrderApplicationItemService;
	
	@Resource(name = "hyRegulateServiceImpl")
	private HyRegulateService hyRegulateService;
	
	@Resource(name = "hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	@Resource(name = "hyVisaServiceImpl")
	private HyVisaService hyVisaService;
	
	@Resource(name = "hyTicketSceneServiceImpl")
	private HyTicketSceneService hyTicketSceneService;
	
	@Resource(name = "hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;
	
	@Resource(name = "hyTicketSceneTicketManagementServiceImpl")
	private HyTicketSceneTicketManagementService hyTicketSceneTicketManagementService;
	
	@Resource(name="hyTicketSubscribeServiceImpl")
	private HyTicketSubscribeService hyTicketSubscribeService;
	
	@Resource(name="hyTicketSubscribePriceServiceImpl")
	private HyTicketSubscribePriceService hyTicketSubscribePriceService;
	
	@Resource(name="hyOrderApplicationDaoImpl")
	@Override
	public void setBaseDao(BaseDao<HyOrderApplication, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	public HyOrderApplication addStoreLineOrderApplicaton(HyOrderApplication app,HyAdmin admin){
		save(app);
		return app;
	}
	public void handleStoreTuiTuan(HyOrderApplication application)throws Exception{
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order.getJiesuanTuikuan() == null){
			order.setJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getWaimaiTuikuan() == null){
			order.setWaimaiTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianJiesuanTuikuan() == null){
			order.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianWaimaiTuikuan() == null){
			order.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);
		}
		
		//修改订单各种退款价格
		order.setJiesuanTuikuan(order.getJiesuanTuikuan().add(application.getJiesuanMoney()));
		order.setWaimaiTuikuan(order.getWaimaiTuikuan().add(application.getWaimaiMoney()));
		order.setBaoxianJiesuanTuikuan(order.getBaoxianJiesuanTuikuan().add(application.getBaoxianJiesuanMoney()));
		order.setBaoxianWaimaiTuikuan(order.getBaoxianWaimaiTuikuan().add(application.getBaoxianWaimaiMoney()));
		
		Integer returnPeople = 0;	//记录退团总人数
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
			if(orderItem.getNumberOfReturn() == null){
				orderItem.setNumberOfReturn(0);
			}
			//设置条目退团数量
			orderItem.setNumberOfReturn(orderItem.getNumberOfReturn()+item.getReturnQuantity());
			//设置总共退团的游客数量
			returnPeople += item.getReturnQuantity()*orderItem.getHyOrderCustomers().size();
			
			//取消退团游客电子券
			List<String> phones = new ArrayList<>();
			for(HyOrderCustomer customer:orderItem.getHyOrderCustomers()){
				phones.add(customer.getPhone());
			}
			//取消门店员工电子券
			phones.add(order.getOperator().getMobile());
			
			for(String phone:phones){
				List<Filter> couponFilters = new ArrayList<>();
				couponFilters.add(Filter.eq("bindPhone", phone));
				couponFilters.add(Filter.eq("groupId", order.getGroupId()));
				List<CouponLine> couponLines = couponLineService.findList(null,couponFilters,null);
				if(!couponLines.isEmpty()){
					for(CouponLine couponLine:couponLines){
						couponLine.setIsValid(false);
						couponLineService.update(couponLine);
					}
				}
			}
			
			hyOrderItemService.update(orderItem);
			
			
		}
		//修改订单人数
		if(order.getPeople() == null){
			order.setPeople(0);
		}
		order.setPeople(order.getPeople()-returnPeople);
		//根据人数判断退款状态,如果订单剩余人数为0，则全部已退款，否则为部分已退款
		//设置退款状态
		order.setRefundstatus(((order.getJiesuanTuikuan().add(order.getBaoxianWaimaiTuikuan()))
				.compareTo(order.getJiusuanMoney()))==0?2:3);
		
		//修改扣点金额
		if(order.getIfjiesuan()==false){	//如果没有结算
			if(order.getKoudianMethod().equals(Constants.DeductLine.rentou.ordinal())){
				//如果是人头扣点方式,扣点金额=订单人数*人头扣点
				order.setKoudianMoney(
						order.getHeadProportion().multiply(BigDecimal.valueOf(order.getPeople())));
			}else if(order.getKoudianMethod().equals(Constants.DeductLine.tuanke.ordinal())){
				order.setKoudianMoney(
						order.getProportion().multiply(
								order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())).multiply(BigDecimal.valueOf(0.01))
									.setScale(2, RoundingMode.HALF_UP));
			}
		}
		
		HyGroup group = hyGroupService.find(order.getGroupId());
		//修改总库存
		group.setStock(group.getStock()+returnPeople);
		//修改报名人数
		group.setSignupNumber(group.getSignupNumber()-returnPeople);
		
		//修改visitorNumber
		if(group.getRegulateId()!=null) {
			HyRegulate regulate = hyRegulateService.find(group.getRegulateId());
			regulate.setVisitorNum(regulate.getVisitorNum()-returnPeople);
		}
		
		hyGroupService.update(group);
		
		hyOrderService.update(order);
		
		
		
	}

	@Override
	public void handleBaoXianTuiKuan(HyOrderApplication application, boolean isPartRefund) throws Exception{
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order.getJiesuanTuikuan() == null){
			order.setJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getWaimaiTuikuan() == null){
			order.setWaimaiTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianJiesuanTuikuan() == null){
			order.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianWaimaiTuikuan() == null){
			order.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);
		}
		
		//修改订单各种退款价格
		//order.setJiesuanTuikuan(order.getJiesuanTuikuan().add(application.getJiesuanMoney()));
		//order.setWaimaiTuikuan(order.getWaimaiTuikuan().add(application.getWaimaiMoney()));
		order.setBaoxianJiesuanTuikuan(order.getBaoxianJiesuanTuikuan().add(application.getBaoxianJiesuanMoney()));
		order.setBaoxianWaimaiTuikuan(order.getBaoxianWaimaiTuikuan().add(application.getBaoxianWaimaiMoney()));
		
		//设置部分已退款
		if(isPartRefund) {
			order.setRefundstatus(3);
		}
		else {
			//全部已退款
			order.setRefundstatus(2);
		}
		//已确认
		order.setStatus(3);
		
//				//修改扣点金额
//				if(order.getIfjiesuan()==false){	//如果没有结算
//					if(order.getKoudianMethod().equals(Constants.DeductLine.rentou)){
//						//因为售后人数不变，所以人头扣点不变
//						
//					}else if(order.getKoudianMethod().equals(Constants.DeductLine.tuanke)){
//						order.setKoudianMoney(
//								order.getProportion().multiply(
//										order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())).multiply(BigDecimal.valueOf(0.01))
//											.setScale(2, RoundingMode.HALF_UP));
//					}
//				}
		
		//售后退款人数不变，团的人数也不变
		
		List<HyOrderItem> hyOrderItems = order.getOrderItems();
		
		//售后退款人数不变
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			for(HyOrderItem orderItem: hyOrderItems) {
				if(orderItem.getId().equals(item.getItemId())) {
					//置退货数量
					orderItem.setNumberOfReturn(item.getReturnQuantity());
					if(orderItem.getNumberOfReturn().equals(1)) {
						//被退了 则失效
						orderItem.setStatus(1);
					}
				}
			}
//			if(orderItem.getNumberOfReturn() == null){
//				orderItem.setNumberOfReturn(0);
//			}
//			//设置条目退团数量
//			//保险条目退的数量只可能是1或者0
//			orderItem.setNumberOfReturn(item.getReturnQuantity());
//			
//			hyOrderItemService.update(orderItem);


		}
		
		order.setOrderItems(hyOrderItems);

		hyOrderService.update(order);



	}
	
	public void handleStoreShouHou(HyOrderApplication application)throws Exception{
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order.getJiesuanTuikuan() == null){
			order.setJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getWaimaiTuikuan() == null){
			order.setWaimaiTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianJiesuanTuikuan() == null){
			order.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianWaimaiTuikuan() == null){
			order.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);
		}
		
		//修改订单各种退款价格
		order.setJiesuanTuikuan(order.getJiesuanTuikuan().add(application.getJiesuanMoney()));
		order.setWaimaiTuikuan(order.getWaimaiTuikuan().add(application.getWaimaiMoney()));
		order.setBaoxianJiesuanTuikuan(order.getBaoxianJiesuanTuikuan().add(application.getBaoxianJiesuanMoney()));
		order.setBaoxianWaimaiTuikuan(order.getBaoxianWaimaiTuikuan().add(application.getBaoxianWaimaiMoney()));
		
		//售后退款人数不变
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
			if(orderItem.getNumberOfReturn() == null){
				orderItem.setNumberOfReturn(0);
			}
			//设置条目退团数量
			orderItem.setNumberOfReturn(orderItem.getNumberOfReturn()+item.getReturnQuantity());
			
			hyOrderItemService.update(orderItem);
			
			
		}

		//设置退款状态
		order.setRefundstatus(((order.getJiesuanTuikuan().add(order.getBaoxianWaimaiTuikuan()))
				.compareTo(order.getJiusuanMoney()))==0?2:3);
		
		//修改扣点金额
		if(order.getIfjiesuan()==false){	//如果没有结算
			if(order.getKoudianMethod().equals(Constants.DeductLine.rentou.ordinal())){
				//因为售后人数不变，所以人头扣点不变
				
			}else if(order.getKoudianMethod().equals(Constants.DeductLine.tuanke.ordinal())){
				order.setKoudianMoney(
						order.getProportion().multiply(
								order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())).multiply(BigDecimal.valueOf(0.01))
									.setScale(2, RoundingMode.HALF_UP));
			}
		}
		
		//售后退款人数不变，团的人数也不变
		
		hyOrderService.update(order);
		
		
		
	}
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	@Resource(name="hyTicketHotelServiceImpl")
	private HyTicketHotelService hyTicketHotelService;
	
	@Resource(name="hyTicketHotelandsceneServiceImpl")
	private HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	@Resource(name="hyTicketHotelRoomServiceImpl")
	private HyTicketHotelRoomService hyTicketHotelRoomService;
	
	@Resource(name="hyTicketHotelandsceneRoomServiceImpl")
	private HyTicketHotelandsceneRoomService hyTicketHotelandsceneRoomService;
	
	@Override
	public Map<String, Object> auditDetailHelper(HyOrderApplication application,Integer status) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<>();
		map.put("id", application.getId());
		map.put("status", status);
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order == null){
			return null;
		}
		map.put("orderNumber", order.getOrderNumber());	//订单编号
		map.put("source", order.getSource());	//订单来源
		if(order.getStoreId()!=null){
			Store store = storeService.find(order.getStoreId());
			map.put("storeName", store.getStoreName());
		}
		
		
		map.put("contactName", order.getOperator().getName());	//门店联系人姓名
		map.put("contactPhone", order.getOperator().getMobile());	//门店联系人电话
		map.put("jiesuanMoney", order.getJiesuanMoney1());	//供应商结算金额
		map.put("jiesuanRefund", application.getJiesuanMoney());	//结算退款金额
		map.put("waimaiMoney",order.getWaimaiMoney());	//订单外卖金额
		map.put("waimaiRefund", application.getWaimaiMoney());	//订单外卖金额
		map.put("baoxianJiesuanRefund", application.getBaoxianJiesuanMoney());	//保险结算退款金额
		map.put("baoxianWaimaiRefund", application.getBaoxianWaimaiMoney());	//保险外卖退款金额
		map.put("applyTime", application.getCreatetime());	//申请日期

		map.put("shkMoney",order.getJiusuanMoney());	//收款金额
		map.put("shkTime", order.getPayTime());	//收款日期
		
		if(order.getType()==1) {	//如果是线路订单
			HyGroup group = hyGroupService.find(order.getGroupId());
			if(group == null){
				return null;
			}

			HyAdmin creator = group.getCreator();
			if(creator!=null){	//建团计调
				map.put("creator", creator.getName());
			}
			HyLine line = group.getLine();
			map.put("pn", line.getPn());	//产品ID
			map.put("startDay", group.getStartDay());	//发团日期
			map.put("endDay", group.getEndDay());	//回团日期
			map.put("name", line.getName());	//线路名称
			HySupplier supplier = line.getHySupplier();
			map.put("supplierName", supplier.getSupplierName());	//供应商名称
		}
		else if(order.getType() == 2) {//如果是认购门票订单
			List<HyOrderItem> orderItems = order.getOrderItems();
			if(orderItems==null || orderItems.isEmpty()) {
				return null;
			}
			HyOrderItem orderItem = orderItems.get(0);
			
			HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(orderItem.getProductId());
			if(hyTicketSubscribe==null) {
				return null;
			}		
			HyAdmin creator = hyTicketSubscribe.getCreator();
			if(creator!=null){	//建团计调
				map.put("creator", creator.getName());
			}
			map.put("pn", hyTicketSubscribe.getId());	//产品ID
			map.put("startDay", orderItem.getStartDate());	//开始日期
			map.put("sceneName", hyTicketSubscribe.getSceneName());	//酒店名称
			HySupplier supplier = hyTicketSubscribe.getTicketSupplier();
			if(supplier!=null) {
				map.put("supplierName", supplier.getSupplierName());	//供应商名称
			}			
		}

		else if(order.getType()==3) {	//如果是酒店订单

			List<HyOrderItem> orderItems = order.getOrderItems();
			if(orderItems==null || orderItems.isEmpty()) {
				return null;
			}
			HyOrderItem orderItem = orderItems.get(0);
			HyTicketHotel hyTicketHotel = hyTicketHotelService.find(orderItem.getProductId());
			if(hyTicketHotel==null) {
				return null;
			}
			
			HyTicketHotelRoom hyTicketHotelRoom = hyTicketHotelRoomService.find(orderItem.getSpecificationId());
			if(hyTicketHotelRoom == null) {
				return null;
			}
			
			
			HyAdmin creator = hyTicketHotel.getCreator();

			
		
			if(creator!=null){	//建团计调
				map.put("creator", creator.getName());
			}
			
			map.put("pn", hyTicketHotelRoom.getProductId());	//产品ID
			map.put("startDay", orderItem.getStartDate());	//发团日期
			map.put("endDay", orderItem.getEndDate());	//回团日期
			map.put("name", hyTicketHotel.getHotelName());	//酒店名称
			HySupplier supplier = hyTicketHotel.getTicketSupplier();
		}
		
		//如果是门票订单
		else if( order.getType() == 4 ) {
			List<HyOrderItem> orderItems = order.getOrderItems();
			if(orderItems==null || orderItems.isEmpty()) {
				return null;
			}
			HyOrderItem orderItem = orderItems.get(0);

			
			//productID里面放的是hyTicketScene.getId()
			HyTicketScene hyTicketScene = hyTicketSceneService.find(orderItem.getProductId());
			if(hyTicketScene == null) {
				return null;
			}
			//specificationId是hyTicketSceneTicketManagement.getId()
			HyTicketSceneTicketManagement hyTicketSceneTicketManagement = hyTicketSceneTicketManagementService.find(orderItem.getSpecificationId());
			if(hyTicketSceneTicketManagement == null) {
				return null;
			}
			HyAdmin creator = hyTicketSceneTicketManagement.getOperator();

			
			
			if(creator!=null){	//建团计调
				map.put("creator", creator.getName());
			}
			
			map.put("pn", hyTicketSceneTicketManagement.getProductId());	//产品ID
			map.put("startDay", orderItem.getStartDate());	//发团日期
			map.put("endDay", orderItem.getEndDate());	//回团日期
			map.put("name", hyTicketSceneTicketManagement.getProductName());	//产品名称
			HySupplier supplier = hyTicketScene.getTicketSupplier();
			
			if(supplier!=null) {
				map.put("supplierName", supplier.getSupplierName());	//供应商名称
			}
			
			//这是我陈文志的
		}
		
		//如果是酒加景订单
		else if(order.getType()==5) {
			List<HyOrderItem> orderItems = order.getOrderItems();
			if(orderItems==null || orderItems.isEmpty()) {
				return null;
			}
			HyOrderItem orderItem = orderItems.get(0);
			
			HyTicketHotelandscene hyTicketHotelandscene = hyTicketHotelandsceneService.find(orderItem.getProductId());
			if(hyTicketHotelandscene==null) {
				return null;
			}
			
			HyTicketHotelandsceneRoom hyTicketHotelandsceneRoom = hyTicketHotelandsceneRoomService.find(orderItem.getSpecificationId());
			if(hyTicketHotelandsceneRoom == null) {
				return null;
			}
			
			
			HyAdmin creator = order.getOperator();
			if(creator!=null){	//产品创建人
				map.put("creator", creator.getName());
			}
			map.put("pn", hyTicketHotelandscene.getProductId());	//产品ID
			map.put("startDay", orderItem.getStartDate());	//发团日期
			map.put("endDay", orderItem.getEndDate());	//回团日期
			map.put("name", hyTicketHotelandscene.getProductName());	//产品名称
			HySupplier supplier = hyTicketHotelandscene.getTicketSupplier();
			if(supplier!=null) {
				map.put("supplierName", supplier.getSupplierName());	//供应商名称
			}
		}
	
		//如果是签证订单
		else if(order.getType()==7) {
			List<HyOrderItem> orderItems = order.getOrderItems();
			if(orderItems==null || orderItems.isEmpty()) {
				return null;
			}
			HyOrderItem orderItem = orderItems.get(0);
			if(orderItem.getProductId() == null)
				return null;
			HyVisa hyVisa = hyVisaService.find(orderItem.getProductId());
			if(hyVisa==null)
				return null;
//			HySupplier supplier = hyVisa.getTicketSupplier();
			HyAdmin creator = order.getOperator();
			if(creator!=null){	//产品创建人
				if(hyVisa.getCreator()!=null)
					map.put("creator", hyVisa.getCreator().getName());
			}
			map.put("productId", hyVisa.getProductId());	//产品ID
			map.put("startDay", orderItem.getStartDate());	//发团日期
			map.put("name", hyVisa.getProductName());	//产品名称
			HySupplier supplier = hyVisa.getTicketSupplier();
			if(supplier!=null) {
				map.put("supplierName", supplier.getSupplierName());	//供应商名称
			}
		}
		return map;
	}

	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Override
	public List<Map<String, Object>> auditItemsHelper(HyOrderApplication application) throws Exception{
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = new ArrayList<>();
		
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			Map<String, Object> map = hyOrderApplicationItemService.auditItemHelper(item);
			
			if(map!=null){
				map.put("returnNumber", item.getReturnQuantity());
				list.add(map);
			}
		}
		return list;
		
	}
	@Override
	public Json getApplicationList(Pageable pageable, Integer status, String providerName, HttpSession session,
			Integer type,Integer orderType) {
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		Json json = new Json();
		try {

			String processDefinitionID = "";
			List<Filter> applyFilters = new ArrayList<>();
			if (type.equals(HyOrderApplication.STORE_CANCEL_GROUP)) { // 门店退团类型的申请
				applyFilters.add(Filter.eq("type", HyOrderApplication.STORE_CANCEL_GROUP));
				processDefinitionID = "storeTuiTuan";
			} else if (type.equals(HyOrderApplication.STORE_CUSTOMER_SERVICE)) {
				applyFilters.add(Filter.eq("type", HyOrderApplication.STORE_CUSTOMER_SERVICE));
				processDefinitionID = "storeShouHou";
			}
			List<Order> applyOrders = new ArrayList<>();
			applyOrders.add(Order.desc("createtime"));
			
			List<HyOrderApplication> applications = new ArrayList<>();
			if (providerName != null) { // 如果查询某一供应商的
				/**查找供应商的订单，增加订单类型判断，需要注意*/
				List<Long> orderIds = hyOrderService.getOrderIdsByProviderName(providerName,orderType);
				if(orderIds!=null && orderIds.size()>0){
					for (Long orderId : orderIds) {
						List<Filter> tmpFilters = new ArrayList<>(applyFilters);
						tmpFilters.add(Filter.eq("orderId", orderId));
						applications.addAll(this.findList(null, tmpFilters, applyOrders));
					}
				}
			} else {
				/**根据订单类型获取所有订单ID，需要注意*/
				List<Filter> orderFilters = new ArrayList<>();
				if(orderType!=null) {
					orderFilters.add(Filter.eq("type", orderType));
				}
				List<HyOrder> orders = hyOrderService.findList(null,orderFilters,applyOrders);
				for(HyOrder order:orders) {
					List<Filter> tmpFilters = new ArrayList<>(applyFilters);
					tmpFilters.add(Filter.eq("orderId", order.getId()));
					applications.addAll(this.findList(null, tmpFilters, null));
				}
			}
			List<Map<String, Object>> ans = new ArrayList<>();
			if (status == null) { // 全部数据
//				List<Task> tasks = ActivitiUtils.getTaskList(username, processDefinitionID);
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyOrderApplication application : applications) {
						if (processInstanceId.equals(application.getProcessInstanceId())) {
							// 待审核数据
							/**获取审核详情需要注意*/
							ans.add(this.auditDetailHelper(application, 0));
						}
					}
				}
//				List<HistoricTaskInstance> historicTaskInstances = ActivitiUtils.getHistoryTaskList(username, processDefinitionID);
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskCandidateUser(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyOrderApplication application : applications) {
						if (processInstanceId.equals(application.getProcessInstanceId())) {
							// 已审核数据
							ans.add(this.auditDetailHelper(application, 1));
						}
					}
				}

			} else if (status.equals(0)) { // 未审核
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (HyOrderApplication application : applications) {
						if (processInstanceId.equals(application.getProcessInstanceId())) {
							// 待审核数据
							ans.add(this.auditDetailHelper(application, 0));
						}
					}
				}
			} else if (status.equals(1)) { // 已审核
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskCandidateUser(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (HyOrderApplication application : applications) {
						if (processInstanceId.equals(application.getProcessInstanceId())) {
							// 已审核数据
							ans.add(this.auditDetailHelper(application, 1));
						}
					}
				}
			}

			ans.sort(new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					return ((Date)o2.get("applyTime")).compareTo((Date)o1.get("applyTime"));
				}
			});

			int page = pageable.getPage();
			int rows = pageable.getRows();
			Page<Map<String, Object>> pages = new Page<>(new ArrayList<Map<String, Object>>(), 0, pageable);
			if (ans != null && !ans.isEmpty()) {
				pages.setTotal(ans.size());
				pages.setRows(ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows));
			}

			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(pages);

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	
	@Override
	public void handleTicketHotelScg(HyOrderApplication application) throws Exception {
		// TODO Auto-generated method stub
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order.getJiesuanTuikuan() == null){
			order.setJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getWaimaiTuikuan() == null){
			order.setWaimaiTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianJiesuanTuikuan() == null){
			order.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianWaimaiTuikuan() == null){
			order.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);
		}
		
		//修改订单各种退款价格
		order.setJiesuanTuikuan(order.getJiesuanTuikuan().add(application.getJiesuanMoney()));
		order.setWaimaiTuikuan(order.getWaimaiTuikuan().add(application.getWaimaiMoney()));
		order.setBaoxianJiesuanTuikuan(order.getBaoxianJiesuanTuikuan().add(application.getBaoxianJiesuanMoney()));
		order.setBaoxianWaimaiTuikuan(order.getBaoxianWaimaiTuikuan().add(application.getBaoxianWaimaiMoney()));
		
		Integer returnPeople = 0;	//记录退团总人数
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
			if(orderItem.getNumberOfReturn() == null){
				orderItem.setNumberOfReturn(0);
			}
			//设置条目退团数量
			orderItem.setNumberOfReturn(orderItem.getNumberOfReturn()+item.getReturnQuantity());
			//设置总共退团的游客数量
			returnPeople += item.getReturnQuantity();
			
			hyOrderItemService.update(orderItem);
			
			//修改库存
			hyTicketInboundService.recoverTicketInboundByTicketOrderItem(orderItem, item.getReturnQuantity());
			
		}
		//修改订单人数
		if(order.getPeople() == null){
			order.setPeople(0);
		}
		order.setPeople(order.getPeople()-returnPeople);
		//根据人数判断退款状态,如果订单剩余人数为0，则全部已退款，否则为部分已退款
		order.setRefundstatus(order.getPeople().equals(0)?2:3);
		
		//修改扣点金额
		if(order.getIfjiesuan()==false){	//如果没有结算
			if(order.getKoudianMethod().equals(Constants.DeductPiaowu.rentou.ordinal())){
				//如果是人头扣点方式,扣点金额=订单人数*人头扣点
				order.setKoudianMoney(
						order.getHeadProportion().multiply(BigDecimal.valueOf(order.getPeople())));
			}else if(order.getKoudianMethod().equals(DeductPiaowu.liushui.ordinal())){
				order.setKoudianMoney(
						order.getProportion().multiply(
								order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())).multiply(BigDecimal.valueOf(0.01))
									.setScale(2, RoundingMode.HALF_UP));
			}
		}

		hyOrderService.update(order);	
	}
	@Override
	public void handleTicketHotelScs(HyOrderApplication application) throws Exception {
		// TODO Auto-generated method stub
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order.getJiesuanTuikuan() == null){
			order.setJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getWaimaiTuikuan() == null){
			order.setWaimaiTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianJiesuanTuikuan() == null){
			order.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianWaimaiTuikuan() == null){
			order.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);
		}
		
		//修改订单各种退款价格
		order.setJiesuanTuikuan(order.getJiesuanTuikuan().add(application.getJiesuanMoney()));
		order.setWaimaiTuikuan(order.getWaimaiTuikuan().add(application.getWaimaiMoney()));
		order.setBaoxianJiesuanTuikuan(order.getBaoxianJiesuanTuikuan().add(application.getBaoxianJiesuanMoney()));
		order.setBaoxianWaimaiTuikuan(order.getBaoxianWaimaiTuikuan().add(application.getBaoxianWaimaiMoney()));
		
		Integer returnPeople = 0;	//记录退团总人数
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
			if(orderItem.getNumberOfReturn() == null){
				orderItem.setNumberOfReturn(0);
			}
			//设置条目退团数量
			orderItem.setNumberOfReturn(orderItem.getNumberOfReturn()+item.getReturnQuantity());
			//设置总共退团的游客数量
			returnPeople += item.getReturnQuantity();
			
			hyOrderItemService.update(orderItem);
			
			//售后不修改库存
			//hyTicketInboundService.recoverTicketInboundByTicketOrderItem(orderItem, item.getReturnQuantity());
			
		}
		//售后不修改订单人数
//		if(order.getPeople() == null){
//			order.setPeople(0);
//		}
//		order.setPeople(order.getPeople()-returnPeople);
		//根据人数判断退款状态,如果订单剩余人数为0，则全部已退款，否则为部分已退款
		order.setRefundstatus(order.getPeople().equals(0)?2:3);
		
		//修改扣点金额
		if(order.getIfjiesuan()==false){	//如果没有结算
			if(order.getKoudianMethod().equals(Constants.DeductPiaowu.rentou.ordinal())){
				//售后不修改人头扣点
//				order.setKoudianMoney(
//						order.getHeadProportion().multiply(BigDecimal.valueOf(order.getPeople())));
			}else if(order.getKoudianMethod().equals(DeductPiaowu.liushui.ordinal())){
				order.setKoudianMoney(
						order.getProportion().multiply(
								order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())).multiply(BigDecimal.valueOf(0.01))
									.setScale(2, RoundingMode.HALF_UP));
			}
		}

		hyOrderService.update(order);	
		
	}
	
	@Override
	public void handleTicketSubscribeScs(HyOrderApplication application) throws Exception {
		// TODO Auto-generated method stub
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order.getJiesuanTuikuan() == null){
			order.setJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getWaimaiTuikuan() == null){
			order.setWaimaiTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianJiesuanTuikuan() == null){
			order.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianWaimaiTuikuan() == null){
			order.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);
		}
		
		//修改订单各种退款价格
		order.setJiesuanTuikuan(order.getJiesuanTuikuan().add(application.getJiesuanMoney()));
		order.setWaimaiTuikuan(order.getWaimaiTuikuan().add(application.getWaimaiMoney()));
		order.setBaoxianJiesuanTuikuan(order.getBaoxianJiesuanTuikuan().add(application.getBaoxianJiesuanMoney()));
		order.setBaoxianWaimaiTuikuan(order.getBaoxianWaimaiTuikuan().add(application.getBaoxianWaimaiMoney()));
		
		Integer returnPeople = 0;	//记录退团总人数
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
			if(orderItem.getNumberOfReturn() == null){
				orderItem.setNumberOfReturn(0);
			}
			//设置条目退团数量
			orderItem.setNumberOfReturn(orderItem.getNumberOfReturn()+item.getReturnQuantity());
			//设置总共退团的游客数量
			returnPeople += item.getReturnQuantity();
			
			hyOrderItemService.update(orderItem);
			
			//售后不修改库存
			//hyTicketInboundService.recoverTicketInboundByTicketOrderItem(orderItem, item.getReturnQuantity());
			
		}
		//售后不修改订单人数
//		if(order.getPeople() == null){
//			order.setPeople(0);
//		}
//		order.setPeople(order.getPeople()-returnPeople);
		//根据人数判断退款状态,如果订单剩余人数为0，则全部已退款，否则为部分已退款
		order.setRefundstatus(order.getPeople().equals(0)?2:3);
		
		//修改扣点金额
		if(order.getIfjiesuan()==false){	//如果没有结算
			if(order.getKoudianMethod().equals(Constants.DeductPiaowu.rentou.ordinal())){
				//售后不修改人头扣点
//				order.setKoudianMoney(
//						order.getHeadProportion().multiply(BigDecimal.valueOf(order.getPeople())));
			}else if(order.getKoudianMethod().equals(DeductPiaowu.liushui.ordinal())){
				order.setKoudianMoney(
						order.getProportion().multiply(
								order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())).multiply(BigDecimal.valueOf(0.01))
									.setScale(2, RoundingMode.HALF_UP));
			}
		}

		hyOrderService.update(order);	
		
	}
	
	/**
	 * 门店退酒加景售前退款成功订单处理
	 * @author GSbing
	 */
	@Override
	public void handleTicketHotelandsceneScg(HyOrderApplication application) throws Exception {
		// TODO Auto-generated method stub
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order.getJiesuanTuikuan() == null){
			order.setJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getWaimaiTuikuan() == null){
			order.setWaimaiTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianJiesuanTuikuan() == null){
			order.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianWaimaiTuikuan() == null){
			order.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);
		}
		
		//修改订单各种退款价格
		order.setJiesuanTuikuan(order.getJiesuanTuikuan().add(application.getJiesuanMoney()));
		order.setWaimaiTuikuan(order.getWaimaiTuikuan().add(application.getWaimaiMoney()));
		order.setBaoxianJiesuanTuikuan(order.getBaoxianJiesuanTuikuan().add(application.getBaoxianJiesuanMoney()));
		order.setBaoxianWaimaiTuikuan(order.getBaoxianWaimaiTuikuan().add(application.getBaoxianWaimaiMoney()));
		
		Integer returnPeople = 0;	//记录退团总人数
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
			if(orderItem.getNumberOfReturn() == null){
				orderItem.setNumberOfReturn(0);
			}
			//设置条目退团数量
			orderItem.setNumberOfReturn(orderItem.getNumberOfReturn()+item.getReturnQuantity());
			//设置总共退团的游客数量
			returnPeople += item.getReturnQuantity();
			
			hyOrderItemService.update(orderItem);
			
			//修改库存
			List<Filter> inboundFilters=new ArrayList<>();
			inboundFilters.add(Filter.eq("priceInboundId", orderItem.getPriceId()));
			inboundFilters.add(Filter.eq("type", 1));
			inboundFilters.add(Filter.eq("day", orderItem.getStartDate()));
			List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,inboundFilters,null);
			
			if(ticketInbounds.isEmpty()) {
				throw new Exception("该产品没有对应的库存");
			}

			synchronized(ticketInbounds){
				//恢复库存
				for(HyTicketInbound hyTicketInbound:ticketInbounds) {
					hyTicketInbound.setInventory(hyTicketInbound.getInventory()+item.getReturnQuantity());
					hyTicketInboundService.update(hyTicketInbound);
				}
			}
		}
		//修改订单人数
		if(order.getPeople() == null){
			order.setPeople(0);
		}
		order.setPeople(order.getPeople()-returnPeople);
		//根据人数判断退款状态,如果订单剩余人数为0，则全部已退款，否则为部分已退款
		order.setRefundstatus(order.getPeople().equals(0)?2:3);
		
		//修改扣点金额
		if(order.getIfjiesuan()==false){	//如果没有结算
			if(order.getKoudianMethod().equals(Constants.DeductPiaowu.rentou.ordinal())){
				//如果是人头扣点方式,扣点金额=订单人数*人头扣点
				order.setKoudianMoney(
						order.getHeadProportion().multiply(BigDecimal.valueOf(order.getPeople())));
			}else if(order.getKoudianMethod().equals(DeductPiaowu.liushui.ordinal())){
				order.setKoudianMoney(
						order.getProportion().multiply(
								order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())).multiply(BigDecimal.valueOf(0.01))
									.setScale(2, RoundingMode.HALF_UP));
			}
		}

		hyOrderService.update(order);	
	}
	
	/**
	 * 门店退酒加景售后退款成功订单处理
	 * @author GSbing
	 */
	@Override
	public void handleTicketHotelandsceneScs(HyOrderApplication application) throws Exception {
		// TODO Auto-generated method stub
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order.getJiesuanTuikuan() == null){
			order.setJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getWaimaiTuikuan() == null){
			order.setWaimaiTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianJiesuanTuikuan() == null){
			order.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianWaimaiTuikuan() == null){
			order.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);
		}
		
		//修改订单各种退款价格
		order.setJiesuanTuikuan(order.getJiesuanTuikuan().add(application.getJiesuanMoney()));
		order.setWaimaiTuikuan(order.getWaimaiTuikuan().add(application.getWaimaiMoney()));
		order.setBaoxianJiesuanTuikuan(order.getBaoxianJiesuanTuikuan().add(application.getBaoxianJiesuanMoney()));
		order.setBaoxianWaimaiTuikuan(order.getBaoxianWaimaiTuikuan().add(application.getBaoxianWaimaiMoney()));
		
		Integer returnPeople = 0;	//记录退团总人数
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
			if(orderItem.getNumberOfReturn() == null){
				orderItem.setNumberOfReturn(0);
			}
			//设置条目退团数量
			orderItem.setNumberOfReturn(orderItem.getNumberOfReturn()+item.getReturnQuantity());
			//设置总共退团的游客数量
			returnPeople += item.getReturnQuantity();
			
			hyOrderItemService.update(orderItem);
			
			//售后不修改库存
			//hyTicketInboundService.recoverTicketInboundByTicketOrderItem(orderItem, item.getReturnQuantity());
			
		}
		//售后不修改订单人数
//		if(order.getPeople() == null){
//			order.setPeople(0);
//		}
//		order.setPeople(order.getPeople()-returnPeople);
		//根据人数判断退款状态,如果订单剩余人数为0，则全部已退款，否则为部分已退款
		order.setRefundstatus(order.getPeople().equals(0)?2:3);
		
		//修改扣点金额
		if(order.getIfjiesuan()==false){	//如果没有结算
			if(order.getKoudianMethod().equals(Constants.DeductPiaowu.rentou.ordinal())){
				//售后不修改人头扣点
//				order.setKoudianMoney(
//						order.getHeadProportion().multiply(BigDecimal.valueOf(order.getPeople())));
			}else if(order.getKoudianMethod().equals(DeductPiaowu.liushui.ordinal())){
				order.setKoudianMoney(
						order.getProportion().multiply(
								order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())).multiply(BigDecimal.valueOf(0.01))
									.setScale(2, RoundingMode.HALF_UP));
			}
		}

		hyOrderService.update(order);	
		
	}
	
	
	/**
	 * 门店退门票售前退款成功订单处理
	 * @author cwz 纯copy
	 */
	@Override
	public void handleTicketSceneScg(HyOrderApplication application) throws Exception {
		// TODO Auto-generated method stub
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order.getJiesuanTuikuan() == null){
			order.setJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getWaimaiTuikuan() == null){
			order.setWaimaiTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianJiesuanTuikuan() == null){
			order.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianWaimaiTuikuan() == null){
			order.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);
		}
		
		//修改订单各种退款价格
		order.setJiesuanTuikuan(order.getJiesuanTuikuan().add(application.getJiesuanMoney()));
		order.setWaimaiTuikuan(order.getWaimaiTuikuan().add(application.getWaimaiMoney()));
		order.setBaoxianJiesuanTuikuan(order.getBaoxianJiesuanTuikuan().add(application.getBaoxianJiesuanMoney()));
		order.setBaoxianWaimaiTuikuan(order.getBaoxianWaimaiTuikuan().add(application.getBaoxianWaimaiMoney()));
		
		Integer returnPeople = 0;	//记录退团总人数
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
			if(orderItem.getNumberOfReturn() == null){
				orderItem.setNumberOfReturn(0);
			}
			//设置条目退团数量
			orderItem.setNumberOfReturn(orderItem.getNumberOfReturn()+item.getReturnQuantity());
			//设置总共退团的游客数量
			returnPeople += item.getReturnQuantity();
			
			hyOrderItemService.update(orderItem);
			
			//修改库存
			List<Filter> inboundFilters=new ArrayList<>();
			inboundFilters.add(Filter.eq("priceInboundId", orderItem.getPriceId()));
			inboundFilters.add(Filter.eq("type", 1));
			inboundFilters.add(Filter.eq("day", orderItem.getStartDate()));
			List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,inboundFilters,null);
			
			if(ticketInbounds.isEmpty()) {
				throw new Exception("该产品没有对应的库存");
			}

			synchronized(ticketInbounds){
				//恢复库存
				for(HyTicketInbound hyTicketInbound:ticketInbounds) {
					hyTicketInbound.setInventory(hyTicketInbound.getInventory()+item.getReturnQuantity());
					hyTicketInboundService.update(hyTicketInbound);
				}
			}
		}
		//修改订单人数
		if(order.getPeople() == null){
			order.setPeople(0);
		}
		order.setPeople(order.getPeople()-returnPeople);
		//根据人数判断退款状态,如果订单剩余人数为0，则全部已退款，否则为部分已退款
		order.setRefundstatus(order.getPeople().equals(0)?2:3);
		
		//修改扣点金额
		if(order.getIfjiesuan()==false){	//如果没有结算
			if(order.getKoudianMethod().equals(Constants.DeductPiaowu.rentou)){
				//如果是人头扣点方式,扣点金额=订单人数*人头扣点
				order.setKoudianMoney(
						order.getHeadProportion().multiply(BigDecimal.valueOf(order.getPeople())));
			}else if(order.getKoudianMethod().equals(DeductPiaowu.liushui)){
				order.setKoudianMoney(
						order.getProportion().multiply(
								order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())).multiply(BigDecimal.valueOf(0.01))
									.setScale(2, RoundingMode.HALF_UP));
			}
		}

		hyOrderService.update(order);	
	}
	
	/**
	 * 门店退门票售后退款成功订单处理
	 * @author cwz 纯copy
	 */
	@Override
	public void handleTicketSceneScs(HyOrderApplication application) throws Exception {
		// TODO Auto-generated method stub
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order.getJiesuanTuikuan() == null){
			order.setJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getWaimaiTuikuan() == null){
			order.setWaimaiTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianJiesuanTuikuan() == null){
			order.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianWaimaiTuikuan() == null){
			order.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);
		}
		
		//修改订单各种退款价格
		order.setJiesuanTuikuan(order.getJiesuanTuikuan().add(application.getJiesuanMoney()));
		order.setWaimaiTuikuan(order.getWaimaiTuikuan().add(application.getWaimaiMoney()));
		order.setBaoxianJiesuanTuikuan(order.getBaoxianJiesuanTuikuan().add(application.getBaoxianJiesuanMoney()));
		order.setBaoxianWaimaiTuikuan(order.getBaoxianWaimaiTuikuan().add(application.getBaoxianWaimaiMoney()));
		
		Integer returnPeople = 0;	//记录退团总人数
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
			if(orderItem.getNumberOfReturn() == null){
				orderItem.setNumberOfReturn(0);
			}
			//设置条目退团数量
			orderItem.setNumberOfReturn(orderItem.getNumberOfReturn()+item.getReturnQuantity());
			//设置总共退团的游客数量
			returnPeople += item.getReturnQuantity();
			
			hyOrderItemService.update(orderItem);
			
			//售后不修改库存
			//hyTicketInboundService.recoverTicketInboundByTicketOrderItem(orderItem, item.getReturnQuantity());
			
		}
		//售后不修改订单人数
//		if(order.getPeople() == null){
//			order.setPeople(0);
//		}
//		order.setPeople(order.getPeople()-returnPeople);
		//根据人数判断退款状态,如果订单剩余人数为0，则全部已退款，否则为部分已退款
		order.setRefundstatus(order.getPeople().equals(0)?2:3);
		
		//修改扣点金额
		if(order.getIfjiesuan()==false){	//如果没有结算
			if(order.getKoudianMethod().equals(Constants.DeductPiaowu.rentou)){
				//售后不修改人头扣点
//				order.setKoudianMoney(
//						order.getHeadProportion().multiply(BigDecimal.valueOf(order.getPeople())));
			}else if(order.getKoudianMethod().equals(DeductPiaowu.liushui)){
				order.setKoudianMoney(
						order.getProportion().multiply(
								order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())).multiply(BigDecimal.valueOf(0.01))
									.setScale(2, RoundingMode.HALF_UP));
			}
		}

		hyOrderService.update(order);	
		
	}
	
	
	/**
	 * 门店退签证售前退款成功订单处理
	 * @author liyang
	 */
	@Override
	public void handleHyVisaScg(HyOrderApplication application) throws Exception {
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order.getJiesuanTuikuan() == null){
			order.setJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getWaimaiTuikuan() == null){
			order.setWaimaiTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianJiesuanTuikuan() == null){
			order.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianWaimaiTuikuan() == null){
			order.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);
		}
		
		//修改订单各种退款价格
		order.setJiesuanTuikuan(order.getJiesuanTuikuan().add(application.getJiesuanMoney()));
		order.setWaimaiTuikuan(order.getWaimaiTuikuan().add(application.getWaimaiMoney()));
		order.setBaoxianJiesuanTuikuan(order.getBaoxianJiesuanTuikuan().add(application.getBaoxianJiesuanMoney()));
		order.setBaoxianWaimaiTuikuan(order.getBaoxianWaimaiTuikuan().add(application.getBaoxianWaimaiMoney()));
		
		Integer returnPeople = 0;	//记录退团总人数
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
			if(orderItem.getNumberOfReturn() == null){
				orderItem.setNumberOfReturn(0);
			}
			//设置条目退团数量
			orderItem.setNumberOfReturn(orderItem.getNumberOfReturn()+item.getReturnQuantity());
			//设置总共退团的游客数量
			returnPeople += item.getReturnQuantity();
			
			hyOrderItemService.update(orderItem);
			
		}
		//修改订单人数
		if(order.getPeople() == null){
			order.setPeople(0);
		}
		order.setPeople(order.getPeople()-returnPeople);
		//根据人数判断退款状态,如果订单剩余人数为0，则全部已退款，否则为部分已退款
		order.setRefundstatus(order.getPeople().equals(0)?2:3);
		
		//修改扣点金额
		if(order.getIfjiesuan()==false){	//如果没有结算
			if(order.getKoudianMethod().equals(Constants.DeductPiaowu.rentou.ordinal())){
				//如果是人头扣点方式,扣点金额=订单人数*人头扣点
				order.setKoudianMoney(
						order.getHeadProportion().multiply(BigDecimal.valueOf(order.getPeople())));
			}else if(order.getKoudianMethod().equals(DeductPiaowu.liushui.ordinal())){
				order.setKoudianMoney(
						order.getProportion().multiply(
								order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())).multiply(BigDecimal.valueOf(0.01))
									.setScale(2, RoundingMode.HALF_UP));
			}
		}
		hyOrderService.update(order);	
	}
	/**
	 * 门店退签证售后成功订单处理
	 * @author liyang
	 */
	@Override
	public void handleHyVisaScs(HyOrderApplication application) throws Exception {
		HyOrder order = hyOrderService.find(application.getOrderId());
		if(order.getJiesuanTuikuan() == null){
			order.setJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getWaimaiTuikuan() == null){
			order.setWaimaiTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianJiesuanTuikuan() == null){
			order.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);
		}
		if(order.getBaoxianWaimaiTuikuan() == null){
			order.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);
		}
		
		//修改订单各种退款价格
		order.setJiesuanTuikuan(order.getJiesuanTuikuan().add(application.getJiesuanMoney()));
		order.setWaimaiTuikuan(order.getWaimaiTuikuan().add(application.getWaimaiMoney()));
		order.setBaoxianJiesuanTuikuan(order.getBaoxianJiesuanTuikuan().add(application.getBaoxianJiesuanMoney()));
		order.setBaoxianWaimaiTuikuan(order.getBaoxianWaimaiTuikuan().add(application.getBaoxianWaimaiMoney()));
		
		Integer returnPeople = 0;	//记录退团总人数
		for(HyOrderApplicationItem item:application.getHyOrderApplicationItems()){
			HyOrderItem orderItem = hyOrderItemService.find(item.getItemId());
			if(orderItem.getNumberOfReturn() == null){
				orderItem.setNumberOfReturn(0);
			}
			//设置条目退团数量
			orderItem.setNumberOfReturn(orderItem.getNumberOfReturn()+item.getReturnQuantity());
			//设置总共退团的游客数量
			returnPeople += item.getReturnQuantity();
			
			hyOrderItemService.update(orderItem);
					
		}
		//售后不修改订单人数
//		if(order.getPeople() == null){
//			order.setPeople(0);
//		}
//		order.setPeople(order.getPeople()-returnPeople);
		//根据人数判断退款状态,如果订单剩余人数为0，则全部已退款，否则为部分已退款
		order.setRefundstatus(order.getPeople().equals(0)?2:3);
		
		//修改扣点金额
		if(order.getIfjiesuan()==false){	//如果没有结算
			if(order.getKoudianMethod().equals(Constants.DeductPiaowu.rentou.ordinal())){
				//售后不修改人头扣点
//				order.setKoudianMoney(
//						order.getHeadProportion().multiply(BigDecimal.valueOf(order.getPeople())));
			}else if(order.getKoudianMethod().equals(DeductPiaowu.liushui.ordinal())){
				order.setKoudianMoney(
						order.getProportion().multiply(
								order.getJiesuanMoney1().subtract(order.getJiesuanTuikuan())).multiply(BigDecimal.valueOf(0.01))
									.setScale(2, RoundingMode.HALF_UP));
			}
		}
		hyOrderService.update(order);		
	}
	
}

