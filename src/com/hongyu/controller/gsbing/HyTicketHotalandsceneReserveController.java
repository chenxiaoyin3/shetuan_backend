package com.hongyu.controller.gsbing;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.controller.gsbing.TicketMemberModelExcel.JiujiajingMember;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyPromotionActivity;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.HySupplierDeductPiaowu;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.entity.HyTicketHotelandsceneRoom;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.Store;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyPromotionActivityService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketHotelandsceneRoomService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.StoreService;
import com.hongyu.util.DateUtil;
import com.hongyu.util.Constants.DeductPiaowu;

/**
 * 票务酒加景产品订购
 */
@Controller
@RequestMapping("admin/hotelandsceneReserve/")
public class HyTicketHotalandsceneReserveController {
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	@Resource(name="hyTicketHotelandsceneServiceImpl")
	private HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	@Resource(name="hyTicketHotelandsceneRoomServiceImpl")
	private HyTicketHotelandsceneRoomService hyTicketHotelandsceneRoomService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	@Resource(name="commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name="hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;
	
	@Resource(name="hySupplierElementServiceImpl")
	private HySupplierElementService hySupplierElementService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name="storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name="hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	
	@Resource(name="hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	@Resource(name="hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;
	
	@Resource(name="hyOrderCustomerServiceImpl")
	private HyOrderCustomerService hyOrderCustomerService;
	
	@Resource(name="hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "hyPromotionActivityServiceImpl")
	private HyPromotionActivityService hyPromotionActivityService;
	
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(HyTicketHotelandscene queryParam,Pageable pageable)
	{
		Json json=new Json();
		try {
			Map<String,Object> map=new HashMap<String,Object>();
			List<HashMap<String, Object>> list = new ArrayList<>();
			List<Filter> hotelandsceneFilter=FilterUtil.getInstance().getFilter(queryParam);
			List<Order> orders = new ArrayList<Order>();
		    orders.add(Order.desc("createTime"));
			List<HyTicketHotelandscene> hotelandscenes=hyTicketHotelandsceneService.findList(null,hotelandsceneFilter,orders);
		    if(!hotelandscenes.isEmpty()) {
		    	for(HyTicketHotelandscene hotelandscene:hotelandscenes) {
		    		List<Filter> filters=new ArrayList<Filter>();
		    		filters.add(Filter.eq("hyTicketHotelandscene", hotelandscene));
		    		filters.add(Filter.eq("saleStatus", 2)); //筛选已上架的
		    		filters.add(Filter.eq("auditStatus", 3)); //筛选审核通过的
		    		filters.add(Filter.eq("status", true)); //筛选正常,未取消的
		    		List<HyTicketHotelandsceneRoom> hotelandsceneRooms=hyTicketHotelandsceneRoomService.findList(null,filters,null);
		    		filters.clear();
		    		List<BigDecimal> settleprices=new ArrayList<BigDecimal>();
		    		for(HyTicketHotelandsceneRoom room:hotelandsceneRooms) {
		    			List<HyTicketPriceInbound> priceList=new ArrayList<>(room.getHyTicketPriceInbounds());
		    			for(HyTicketPriceInbound inboundprice:priceList) {
		    				Date date=DateUtils.truncate(new Date(), Calendar.DATE);
		    				if(inboundprice.getEndDate().compareTo(date)>=0) {
		    					settleprices.add(inboundprice.getSettlementPrice());
		    				}
		    			}
	    			}
		    		if(settleprices.size()>0) {//有房间价格列表
		    			HashMap<String,Object> hotelMap=new HashMap<String,Object>();
		    			hotelMap.put("id",hotelandscene.getId());
		    			hotelMap.put("productName", hotelandscene.getProductName()); //产品名称
		    			hotelMap.put("days",hotelandscene.getDays()); //天数
		    			hotelMap.put("hotelStar",hotelandscene.getHotelStar()); //酒店星级
		    			BigDecimal lowestPrice=Collections.min(settleprices); //找出最低价格
		    			hotelMap.put("lowestPrice",lowestPrice); //最低价格
		    			list.add(hotelMap);
		    		}
		    	}
		    }	    
		    int page = pageable.getPage();
			int rows = pageable.getRows();
			map.put("pageNumber", page);
			map.put("pageSize", rows);
			map.put("total", list.size());
			map.put("rows", list.subList((page - 1) * rows, page * rows > list.size() ? list.size() : page * rows));
			json.setSuccess(true);
		    json.setObj(map);
		    json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="detail/view")
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try {
			HyTicketHotelandscene hotelandscene=hyTicketHotelandsceneService.find(id);
			Map<String,Object> obj=new HashMap<String,Object>();
			//套餐说明
			obj.put("days", hotelandscene.getDays()); //行程天数
			obj.put("priceContain", hotelandscene.getPriceContain()); //费用包含
			obj.put("reserveKnow", hotelandscene.getReserveKnow()); //预定须知
			obj.put("reserveDays", hotelandscene.getReserveDays()); //提前预订天数
			obj.put("reserveTime", hotelandscene.getReserveTime()); //提前预订时间
			obj.put("refundKnow", hotelandscene.getRefundKnow()); //退款说明
			obj.put("adultsTicketNum", hotelandscene.getAdultsTicketNum()); 
			obj.put("childrenTicketNum", hotelandscene.getChildrenTicketNum());
			obj.put("studentsTicketNum", hotelandscene.getStudentsTicketNum());
			obj.put("oldTicketNum", hotelandscene.getOldTicketNum());
			obj.put("isRealName",hotelandscene.getIsRealName()); //是否实名
			obj.put("introduction", hotelandscene.getIntroduction()); //推广文件
			
			//供应商信息
			if(hotelandscene.getTicketSupplier()!=null) {
				obj.put("supplier", hotelandscene.getTicketSupplier().getSupplierName());
			}
			else {
				obj.put("supplier", null);
			}
			obj.put("creator", hotelandscene.getCreator().getName());
			obj.put("telephone", hotelandscene.getCreator().getMobile());
			
			//景区信息
			obj.put("sceneName", hotelandscene.getSceneName());
			obj.put("sceneAddress", hotelandscene.getSceneAddress());
			obj.put("sceneArea", hotelandscene.getSceneArea().getFullName());
			obj.put("sceneStar", hotelandscene.getSceneStar());
			obj.put("sceneOpenTime", hotelandscene.getSceneOpenTime());
			obj.put("sceneCloseTime", hotelandscene.getSceneCloseTime());
			obj.put("exchangeTicketAddress", hotelandscene.getExchangeTicketAddress());	
			
			//酒店信息
			obj.put("hotelName", hotelandscene.getHotelName());
			obj.put("hotelStar", hotelandscene.getHotelStar());
			obj.put("hotelArea", hotelandscene.getHotelArea().getFullName());
			obj.put("hotelAddress", hotelandscene.getHotelAddress());
			
			//促销信息
			HyPromotionActivity promotionActivity=hotelandscene.getHyPromotionActivity();
			if(promotionActivity!=null) { //如果有促销,且审核通过的
				if(promotionActivity.getState()==1) {
					obj.put("promotionName", promotionActivity.getName()); //促销名称
					obj.put("promotionStartDate", promotionActivity.getStartDate()); //促销开始时间
					obj.put("promotionEndDate", promotionActivity.getEndDate()); //促销结束时间
					obj.put("promotionType", promotionActivity.getPromotionType()); //促销类型,0:满减,1:满折,2:每人(单位数量)减
					/** 满减促销满足的金额 **/
					obj.put("manjianPrice1", promotionActivity.getManjianPrice1()); 
					/** 满减促销减免的金额 **/
					obj.put("manjianPrice2", promotionActivity.getManjianPrice2());
					/** 打折折扣 **/
					obj.put("dazhe", promotionActivity.getDazhe());
					/** 每人减/按数量减金额 **/
					obj.put("meirenjian", promotionActivity.getMeirenjian());
				}			
			}
			
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(obj);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="priceDetail/view")
	@ResponseBody
	public Json priceDetail(Long id,Integer available,Integer roomType,Integer breakfast,Boolean isWifi,
			Boolean isWindow,Boolean isBathroom,@DateTimeFormat(pattern="yyyy-MM-dd") Date occuDate,Integer priceRange)
	{
		Json json=new Json();
		try {
			HyTicketHotelandscene hotelandscene=hyTicketHotelandsceneService.find(id);
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("hyTicketHotelandscene",hotelandscene));
			if(available!=null) {
				filters.add(Filter.eq("available", available));
			}
			if(roomType!=null) {
				filters.add(Filter.eq("roomType", roomType));
			}
			if(breakfast!=null) {
				filters.add(Filter.eq("breakfast", breakfast));
			}
			if(isWifi!=null) {
				filters.add(Filter.eq("isWifi", isWifi));
			}
			if(isWindow!=null) {
				filters.add(Filter.eq("isWindow", isWindow));
			}
			if(isBathroom!=null) {
				filters.add(Filter.eq("isBathroom", isBathroom));
			}
			List<HyTicketHotelandsceneRoom> hotelandsceneRooms=hyTicketHotelandsceneRoomService.findList(null,filters,null);
			if(hotelandsceneRooms.isEmpty()) {
		    	json.setMsg("查询成功");
		    	json.setSuccess(true);
		    	json.setObj(new ArrayList<>());
		    	return json;
		    }
			List<Filter> priceFilter=new ArrayList<Filter>();
		    priceFilter.add(Filter.in("hyTicketHotelandsceneRoom", hotelandsceneRooms));
		    if(occuDate!=null) {
		    	priceFilter.add(new Filter("startDate", Operator.le, DateUtil.getStartOfDay(occuDate)));
		    	priceFilter.add(new Filter("endDate", Operator.ge, DateUtil.getStartOfDay(occuDate)));
		    }
		    if(priceRange!=null) {
		    	//150以下
		    	if(priceRange==1) {
		    		priceFilter.add(Filter.lt("settlementPrice", 150));
		    	}
		    	
		    	//150-300
		    	else if(priceRange==2) {
		    		priceFilter.add(Filter.ge("settlementPrice", 150));
		    		priceFilter.add(Filter.le("settlementPrice", 300));
		    	}
		    	
		    	//301-450
		    	else if(priceRange==3) {
		    		priceFilter.add(Filter.gt("settlementPrice", 300));
		    		priceFilter.add(Filter.le("settlementPrice", 450));
		    	}
		    	
		    	//351-600
		    	else if(priceRange==4) {
		    		priceFilter.add(Filter.gt("settlementPrice", 451));
		    		priceFilter.add(Filter.le("settlementPrice", 600));
		    	}
		    	
		    	//600以上
		    	else if(priceRange==5) {
		    		priceFilter.add(Filter.gt("settlementPrice", 600));
		    	}
		    }
		    List<HyTicketPriceInbound> priceInbounds=hyTicketPriceInboundService.findList(null,priceFilter,null);
		    List<Map<String,Object>> obj=new ArrayList<>();
		    for(HyTicketPriceInbound price:priceInbounds) {
		    	Map<String,Object> map=new HashMap<String,Object>();
		    	map.put("priceId", price.getId());
		    	map.put("productId", price.getHyTicketHotelandsceneRoom().getHyTicketHotelandscene().getProductId());
		        map.put("roomType", price.getHyTicketHotelandsceneRoom().getRoomType());
		        map.put("isWifi",price.getHyTicketHotelandsceneRoom().getIsWifi());
		        map.put("isWindow",price.getHyTicketHotelandsceneRoom().getIsWindow());
		        map.put("isBathroom",price.getHyTicketHotelandsceneRoom().getIsBathroom());
		        map.put("available",price.getHyTicketHotelandsceneRoom().getAvailable());
		        map.put("breakfast",price.getHyTicketHotelandsceneRoom().getBreakfast());
		        map.put("displayPrice",price.getDisplayPrice());
		        map.put("sellPrice",price.getSellPrice());
		        map.put("settlementPrice",price.getSettlementPrice());
		        //增加库存量信息,added in 20190711
		        List<Filter> inboundFilters=new ArrayList<>();
		        inboundFilters.add(Filter.eq("type", 1)); //1-酒店,门票,酒加景等票务产品
		        inboundFilters.add(Filter.eq("priceInboundId", price.getId())); //价格库存id
		        inboundFilters.add(Filter.eq("day", occuDate));
		        List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,inboundFilters,null);
		        Integer inboundNumber=0;
		        //原则上只能找到一条
		        if(!ticketInbounds.isEmpty()) {
		        	inboundNumber=ticketInbounds.get(0).getInventory();
		        }
		        map.put("inboundNumber", inboundNumber);
		        obj.add(map);
		    }
		    json.setSuccess(true);
		    json.setMsg("查询成功");
		    json.setObj(obj);
		    return json;
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/*新建一个内部类,传参*/
	static class WrapOrder{
		private Long priceId;
		private Integer quantity;
		private Date occuDate;
		private String contact;
		private String telephone;
		private String remark;
		private List<HyOrderCustomer> customers=new ArrayList<>();
		public Long getPriceId() {
			return priceId;
		}
		public void setPriceId(Long priceId) {
			this.priceId = priceId;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		
		@DateTimeFormat(iso=ISO.DATE)
		public Date getOccuDate() {
			return occuDate;
		}
		public void setOccuDate(Date occuDate) {
			this.occuDate = occuDate;
		}
		
		public String getContact() {
			return contact;
		}
		public void setContact(String contact) {
			this.contact = contact;
		}
		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public List<HyOrderCustomer> getCustomers() {
			return customers;
		}
		public void setCustomers(List<HyOrderCustomer> customers) {
			this.customers = customers;
		}
	}
	@RequestMapping(value="hotelandsceneOrder")
	@ResponseBody
	public Json hotelandsceneOrder(@RequestBody WrapOrder wrapOrder,HttpSession session)
	{
		Json json=new Json();
		try {
			Integer quantity=wrapOrder.getQuantity();
			if(quantity!=1) {
				throw new Exception("该产品只能订购一张");
			}
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Store store = storeService.findStore(hyAdmin);
			if (store == null) {
				throw new Exception("所属门店不存在");
			}
			
			Long priceId=wrapOrder.getPriceId();
			HyTicketPriceInbound hyTicketPriceInbound=hyTicketPriceInboundService.find(priceId);
			HyTicketHotelandsceneRoom hotelandsceneRoom=hyTicketPriceInbound.getHyTicketHotelandsceneRoom();
			HyTicketHotelandscene hotelandscene=hotelandsceneRoom.getHyTicketHotelandscene();			
			Date occuDate=wrapOrder.getOccuDate();
			String contact=wrapOrder.getContact();
			String telephone=wrapOrder.getTelephone();
			String remark=wrapOrder.getRemark();
			List<HyOrderCustomer> customers=wrapOrder.getCustomers();
			
			//判断当日库存是否充足
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("priceInboundId", priceId));
			filters.add(Filter.eq("type", 1));
			filters.add(Filter.ge("day", DateUtil.getStartOfDay(occuDate)));
			filters.add(Filter.le("day", DateUtil.getEndOfDay(occuDate)));
			
			List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,filters,null);
			filters.clear();
			if(ticketInbounds.isEmpty()) {
				throw new Exception("该产品没有对应的库存");
			}
			//原则上同一日期,同一产品只能查出一条库存
			HyTicketInbound ticketInbound=ticketInbounds.get(0);
			if(ticketInbound.getInventory().compareTo(quantity)<0) {
				throw new Exception("库存不足");
			}
			
			//如果有足够的库存,则更新库存,用同步代码块，防止同步抢资源出错
			synchronized(ticketInbound) {
				ticketInbound.setInventory(ticketInbound.getInventory()-quantity);
				hyTicketInboundService.update(ticketInbound);
			}
			
			//产生订单
			HyOrder hyOrder=new HyOrder();
			//生成订单编号
			filters.add(Filter.eq("type", SequenceTypeEnum.orderSn));
			Long value=0L;
			synchronized(this) {		
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				CommonSequence c = ss.get(0);
				value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
			}
			filters.clear();
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			produc=dateStr + String.format("%05d", value);
			hyOrder.setOrderNumber(produc); //生成的订单编号
			hyOrder.setName(hyTicketPriceInbound.getHyTicketHotelandsceneRoom().getHyTicketHotelandscene().getProductName());
			hyOrder.setStatus(0);// 0门店待支付
			hyOrder.setPaystatus(0);// 0门店待支付
			hyOrder.setCheckstatus(0);// 门店待确认
			hyOrder.setGuideCheckStatus(0);	//供应商待确认状态
			hyOrder.setRefundstatus(0);// 门店未退款
			hyOrder.setType(5);	//酒加景订单
			hyOrder.setSource(0);	//门店来源
			hyOrder.setPeople(quantity);	//订购数量
			hyOrder.setStoreType(0);	//虹宇门店
			hyOrder.setStoreId(store.getId());	//门店id
			hyOrder.setOperator(hyAdmin);	//门店处理人i
			hyOrder.setCreatorId(username);	
			
			//调整金额待处理
			hyOrder.setAdjustMoney(BigDecimal.ZERO);	//调整金额为0
			
			//获取优惠活动
			HyPromotionActivity promotionActivity = hotelandscene.getHyPromotionActivity();
			//促销状态为审核通过
			if(promotionActivity!=null && promotionActivity.getState()==1) {
				hyOrder.setDiscountedType(promotionActivity.getPromotionType());
				hyOrder.setDiscountedId(promotionActivity.getId());
				//获取优惠金额
				hyOrder.setDiscountedPrice(hyPromotionActivityService.getDiscountedPriceByHyOrder(hyOrder, promotionActivity));
			}
			else {
				hyOrder.setDiscountedType(3);
				hyOrder.setDiscountedPrice(BigDecimal.ZERO);
			}
			
			
			
			//除保险之外的订单条目的总结算价,没有保险
			hyOrder.setJiesuanMoney1(hyTicketPriceInbound.getSettlementPrice().multiply(
					BigDecimal.valueOf(quantity)));
			//订单结算价,没有保险，
			hyOrder.setJiusuanMoney(hyOrder.getJiesuanMoney1());
			//
			hyOrder.setWaimaiMoney(hyTicketPriceInbound.getSellPrice().multiply(
					BigDecimal.valueOf(quantity)));
			hyOrder.setJiesuanTuikuan(BigDecimal.ZERO);	//结算退款价
			hyOrder.setWaimaiTuikuan(BigDecimal.ZERO);	//外卖退款价
			hyOrder.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);	//保险结算退款价
			hyOrder.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);	//保险外卖结算价
			hyOrder.setIfjiesuan(false);// 未结算
			hyOrder.setInsuranceOrderDownloadUrl(null);	//没有保险
			hyOrder.setJiesuantime(null);	//没有结算
			
			//计算扣点
			//找供应商合同
			filters.clear();
			filters.add(Filter.eq("liable", hotelandscene.getCreator()));
			filters.add(Filter.eq("contractStatus", ContractStatus.zhengchang));
			List<HySupplierContract> supplierContracts=hySupplierContractService.findList(null,filters,null);
			if(supplierContracts.isEmpty()) {
				json.setSuccess(false);
				json.setMsg("该供应商没有正常状态的合同");
				return json;
			}
			filters.clear();
			HySupplierContract hySupplierContract = supplierContracts.get(0);
			HySupplierDeductPiaowu hySupplierDeductPiaowu = hySupplierContract.getHySupplierDeductPiaowu();
			if(hySupplierDeductPiaowu==null) {
				json.setSuccess(false);
				json.setMsg("找不到票务扣点");
				return json;
			}
			hyOrder.setKoudianMethod(hySupplierDeductPiaowu.getDeductPiaowu().ordinal());	//扣点方式
			if(hyOrder.getKoudianMethod().equals(DeductPiaowu.liushui.ordinal())){
				//流水扣点
				hyOrder.setProportion(hySupplierDeductPiaowu.getLiushuiPiaowu());
				hyOrder.setKoudianMoney(hyOrder.getJiusuanMoney().multiply(
						hyOrder.getProportion().multiply(BigDecimal.valueOf(0.01))));
			}else{
				//人头扣点
				hyOrder.setHeadProportion(hySupplierDeductPiaowu.getRentouPiaowu());
				hyOrder.setKoudianMoney(hyOrder.getHeadProportion().multiply(
						BigDecimal.valueOf(hyOrder.getPeople())));
			}
			
			hyOrder.setContact(contact);	//联系人姓名
			hyOrder.setPhone(telephone);	//联系人电话
			hyOrder.setRemark(remark);	//备注			
			hyOrder.setCreatetime(new Date());	//订单创建时间
			hyOrder.setSupplier(hotelandscene.getCreator()); //酒加景产品的创建人
			hyOrder.setIsDivideStatistic(false);	//没有分成统计
			hyOrderService.save(hyOrder);
			
			//订单日志,插一条记录到hy_order_application
			HyOrderApplication hyOrderApplication=new HyOrderApplication();
			hyOrderApplication.setOperator(hyAdmin);
			hyOrderApplication.setCreatetime(new Date());
			hyOrderApplication.setStatus(1); //通过
			hyOrderApplication.setContent("门店下订单");
			hyOrderApplication.setOrderId(hyOrder.getId());
			hyOrderApplication.setOrderNumber(produc);
			hyOrderApplication.setType(8); //8-门店下订单
			hyOrderApplicationService.save(hyOrderApplication);
            
			//计算结束日期
			Calendar cld = Calendar.getInstance();
    		cld.setTime(occuDate);
    		cld.add(Calendar.DATE, hotelandscene.getDays()-1);
    		Date endDay=cld.getTime();
    		
    		//存储订单条目信息
    		HyOrderItem hyOrderItem = new HyOrderItem();
			hyOrderItem.setStatus(0);	//0为有效
			hyOrderItem.setName(hotelandscene.getProductName());	//订单条目名称
			hyOrderItem.setType(5);	//酒加景
			hyOrderItem.setPriceType(null);	//价格类型为null
			hyOrderItem.setJiesuanPrice(hyTicketPriceInbound.getSettlementPrice());	//结算价
			hyOrderItem.setWaimaiPrice(hyTicketPriceInbound.getSellPrice());	//外卖价
			hyOrderItem.setNumber(1);	//购买数量为1
			hyOrderItem.setNumberOfReturn(0);	//退货数量
			hyOrderItem.setOrder(hyOrder);	//所属订单
			hyOrderItem.setStartDate(occuDate); //服务开始日期
    		hyOrderItem.setEndDate(endDay); //服务结束日期设置开始日期加上服务天数
			hyOrderItem.setProductId(hotelandscene.getId());	//酒加景id
			hyOrderItem.setSpecificationId(hotelandsceneRoom.getId());	//房型id
			hyOrderItem.setPriceId(hyTicketPriceInbound.getId());	//价格id
			hyOrderItem.setOrder(hyOrder);
            hyOrderItemService.save(hyOrderItem);
            
    		//如果是实名制
    		if(hotelandscene.getIsRealName()==true) {
    			//顾客信息,一个订单条目对应多个游客信息
                for(HyOrderCustomer hyOrderCustomer:customers) {   
                	hyOrderCustomer.setOrderItem(hyOrderItem);
                    hyOrderCustomerService.save(hyOrderCustomer);
                }
    		}
            
            json.setSuccess(true);
            json.setMsg("下单成功");
            json.setObj(hyOrder.getId());
		}
		catch(Exception e) {
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 购买酒加景批量导入顾客信息下载模板
	 * @author GSbing
	 * @date 20190719
	 */
	@RequestMapping(value = "jiujiajing_order/get_excel")
	public void GetExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
        	String filefullname =System.getProperty("hongyu.webapp") + "download/酒加景批量导入游客信息表.xls";
            String fileName = "酒加景批量导入游客信息表.xls";
			File file = new File(filefullname);
			System.out.println(filefullname);
			System.out.println(file.getAbsolutePath());
			if (!file.exists()) {
			    request.setAttribute("message", "下载失败");
			    return;
                
            } else {

                // 设置相应头，控制浏览器下载该文件，这里就是会出现当你点击下载后，出现的下载地址框
                response.setHeader("content-disposition",
                        "attachment;filename=" + URLEncoder.encode("酒加景产品游客信息表.xls", "utf-8"));         
        		
        		response.setHeader("content-disposition",
        				"attachment;" + "filename=" + URLEncoder.encode(fileName, "UTF-8"));	
        		
        		response.setHeader("Connection", "close");
        		response.setHeader("Content-Type", "application/vnd.ms-excel");

        		//String zipfilefullname = userdir + zipFileName;
        		FileInputStream fis = new FileInputStream(file);
        		BufferedInputStream bis = new BufferedInputStream(fis);
        		ServletOutputStream sos = response.getOutputStream();
        		BufferedOutputStream bos = new BufferedOutputStream(sos);

        		byte[] bytes = new byte[1024];
        		int i = 0;
        		while ((i = bis.read(bytes, 0, bytes.length)) != -1) {
        			bos.write(bytes);
        		}
        		bos.flush();
        		bis.close();
        		bos.close();
            }
        }
        catch (Exception e) {
			// TODO: handle exception
        	request.setAttribute("message", "出现错误");
            e.printStackTrace();
		}
        return;	
	}
	
	/**
	 * 上传订购酒加景游客信息表模板
	 * @author GSbing
	 * @date 20190719
	 */
	@RequestMapping(value = "jiujiajing_order/upload_excel")
	@ResponseBody
	public Json UploadExcel(@RequestParam MultipartFile[] files) {
        Json json = new Json();
		try {
			if(files == null || files[0] == null) {
				json.setMsg("未接收到文件");
	        	json.setSuccess(false);
	        	json.setObj(null);
			}
			MultipartFile file = files[0];
			
        	List<JiujiajingMember> members = TicketMemberModelExcel.readJiujiajingMemberExcel(file.getInputStream());
        	List<Map<String,Object> > list = new ArrayList<>();
            for(JiujiajingMember member : members) {
            	Map<String, Object> map = new HashMap<>();
            	map.put("name", member.getName());
            	String zhengjianType=member.getCertificateType(); //证件类型
            	Integer certificateType=0;
            	if(zhengjianType.equals("身份证")) {
            		certificateType=0;
            	}
            	else if(zhengjianType.equals("护照")) {
            		certificateType=1;
            	}
            	else if(zhengjianType.equals("港澳通行证")) {
            		certificateType=2;
            	}
            	else if(zhengjianType.equals("士兵证")) {
            		certificateType=3;
            	}
            	else if(zhengjianType.equals("回乡证")) {
            		certificateType=4;
            	}
            	map.put("certificateType", certificateType);
            	map.put("certificate", member.getCertificate()); //证件号
            	map.put("phone", member.getPhone()); //手机号
            	list.add(map);
            }
        	
			json.setObj(list);
			json.setMsg("文件读取成功");
			json.setSuccess(true);
        }
        catch (Exception e) {
			// TODO: handle exception
        	json.setMsg("文件读取失败");
        	json.setSuccess(false);
        	json.setObj(null);
            
		}
		return json;
       
	}
}
