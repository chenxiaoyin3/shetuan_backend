package com.hongyu.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


import com.hongyu.util.DateUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyPromotionActivity;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierDeductPiaowu;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.Store;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.service.CaptchaService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyPromotionActivityService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HyTicketHotelRoomService;
import com.hongyu.service.HyTicketHotelService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants.DeductPiaowu;

@Controller
@RequestMapping("admin/ticket_hotel_reserve/")
public class HyTicketHotelReserveController {
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hyTicketHotelServiceImpl")
	private HyTicketHotelService hyTicketHotelService;
	
	@Resource(name="hyTicketHotelRoomServiceImpl")
	private HyTicketHotelRoomService hyTicketHotelRoomService;
	
	/**
	 * 酒店分页
	 * @param hotelName
	 * @param address
	 * @param star
	 * @param pageable
	 * @param session
	 * @return
	 */
	@RequestMapping("page/view")
	@ResponseBody
	public Json pageView(String hotelName,String address,Integer star,
			Pageable pageable,HttpSession session){
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		Json json = new Json();
		try {
			
			List<Filter> hotelFilters = new ArrayList<>();
			if(hotelName!=null)
				hotelFilters.add(Filter.like("hotelName", hotelName));
			if(address!=null)
				hotelFilters.add(Filter.like("address", address));
			if(star!=null)
				hotelFilters.add(Filter.eq("star", star));
//			pageable.setFilters(hotelFilters);
			
			List<Order> hotelOrders = new ArrayList<Order>();
			hotelOrders.add(Order.desc("createTime"));
//			pageable.setOrders(hotelOrders);
			
//			Page<HyTicketHotel> page=hyTicketHotelService.findPage(pageable);
			List<HyTicketHotel> alList = hyTicketHotelService.findList(null,hotelFilters,hotelOrders);
			List<Map<String, Object>> list = new ArrayList<>();
			if(alList.size()>0){
				for(HyTicketHotel hyTicketHotel:alList){
					Map<String, Object> hotelMap = new HashMap<>();
					hotelMap.put("id", hyTicketHotel.getId());
					hotelMap.put("hotelName", hyTicketHotel.getHotelName());
					hotelMap.put("address", hyTicketHotel.getAddress());
					hotelMap.put("star", hyTicketHotel.getStar());
					List<HyTicketPriceInbound> inboundList=new ArrayList<>();
					Set<HyTicketHotelRoom> roomList =hyTicketHotel.getHyTicketHotelRooms();
					if(roomList.size()>0){
						for(HyTicketHotelRoom room:roomList){
							List<HyTicketPriceInbound> priceList=new ArrayList<>(room.getHyTicketPriceInbounds());
							inboundList.addAll(priceList);
						}
						if(inboundList.size()>0){
						    /* 找出最低价格 */
						    Collections.sort(inboundList, new Comparator<HyTicketPriceInbound>() {
								@Override
								public int compare(HyTicketPriceInbound o1,HyTicketPriceInbound o2) {
									BigDecimal price1 = o1.getSettlementPrice();
									BigDecimal price2 = o2.getSettlementPrice();
									return price1.compareTo(price2); 
								}
							});
						    hotelMap.put("lowestPrice", inboundList.get(0).getSettlementPrice());
						}else{
							continue;
						}
					}else{
//					    hotelMap.put("lowestPrice", null);
						continue;
					}
					//只返回有价格的有效酒店
					list.add(hotelMap);
				}
			}
			
			
			int page = pageable.getPage();
			int rows = pageable.getRows();
			Page<Map<String, Object>> pages = new Page<>(new ArrayList<Map<String, Object>>(), 0, pageable);
			if (list != null && !list.isEmpty()) {
				pages.setTotal(list.size());
				pages.setRows(list.subList((page - 1) * rows, page * rows > list.size() ? list.size() : page * rows));
			
			}

			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(pages);	

		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return json;
		
	}
	
	/**
	 * 酒店详情
	 * @param id
	 * @param session
	 * @return
	 */
	@RequestMapping("hotel_detail/view")
	@ResponseBody
	public Json storeDetail(Long id,HttpSession session){
		Json json = new Json();
		try {
			
			HyTicketHotel hyTicketHotel = hyTicketHotelService.find(id);
			if(hyTicketHotel==null){
				throw new Exception("没有有效的记录");
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hyTicketHotel);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		return json;
	}
	
	static class HyTicketHotelRoomReq extends HyTicketHotelRoom{
		private Date startDate;
		private Date endDate;
		private Integer price;
		
		@DateTimeFormat(iso=ISO.DATE)
		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		
		@DateTimeFormat(iso=ISO.DATE)
		public Date getEndDate() {
			return endDate;
		}
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
		public Integer getPrice() {
			return price;
		}
		public void setPrice(Integer price) {
			this.price = price;
		}
		
	}
	
	/**
	 * 酒店房间列表
	 * @param id
	 * @param roomReq
	 * @param session
	 * @return
	 */
	@RequestMapping("hotel_room_list/view")
	@ResponseBody
	public Json hotelRoomList(Long id,HyTicketHotelRoomReq roomReq,HttpSession session){
		Json json = new Json();
		try {
			Long days = DateUtil.getDaysBetweenTwoDates(roomReq.startDate,roomReq.endDate);
			if(days == 0){
				days = 1l;
			}
			String[] cols = new String[]{"roomId","productId","productName","roomType","isWifi","isWindow","isBathroom",
					"available","breakfast","priceId","inventory","displayPrice","sellPrice","settlementPrice","startDate","endDate","reserveDays","reserveTime"};
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			StringBuilder sb = new StringBuilder("select r1.id rid,r1.product_id,r1.product_name,r1.room_type,r1.is_wifi,r1.is_window,r1.is_bathroom,"
					+ "r1.available,r1.breakfast,p1.id pid,p1.inventory,p1.display_price,p1.sell_price,p1.settlement_price,p1.start_date,p1.end_date,r1.reserve_days,r1.reserve_time"
					+ " from hy_ticket_hotel_room r1,hy_ticket_price_inbound p1"
					+ " where r1.id=p1.room_id and r1.audit_status=3 and r1.sale_status=2 and r1.status=1");
			sb.append(" and "+days+" <= (select count(*) from hy_ticket_inbound i1,hy_ticket_price_inbound p2" +
					" where i1.price_inbound_id=p2.id and p2.room_id = r1.id" +
					" and DATE_FORMAT(i1.day,'%Y-%m-%d')>='"+format.format(roomReq.getStartDate())+"'" +
					" and DATE_FORMAT(i1.day,'%Y-%m-%d')<='"+format.format(roomReq.getEndDate())+"'"+
					")");

			if(id!=null){
				sb.append(" and r1.hotel="+id);
			}
//			if(roomReq.getStartDate()!=null){
//				sb.append(" and DATE_FORMAT(p1.end_data,'%Y-%m-%d')>='"+format.format(roomReq.getStartDate())+"'");
//			}
//			if(roomReq.getEndDate()!=null){
//				sb.append(" and DATE_FORMAT(p1.start_data,'%Y-%m-%d')<='"+format.format(roomReq.getEndDate())+"'");
//			}
			if(roomReq.getIsWifi()!=null){
				sb.append(" and r1.is_wifi="+roomReq.getIsWifi());
			}
			if(roomReq.getIsWindow()!=null){
				sb.append(" and r1.is_window="+roomReq.getIsWindow());
			}
			if(roomReq.getIsBathroom()!=null){
				sb.append(" and r1.is_bathroom="+roomReq.getIsBathroom());
			}
			if(roomReq.getBreakfast()!=null && roomReq.getBreakfast()>0){
				sb.append(" and r1.breakfast="+roomReq.getBreakfast());
			}
			if(roomReq.getAvailable()!=null && roomReq.getAvailable()>0){
				sb.append(" and r1.available="+roomReq.getAvailable());
			}
			if(roomReq.getRoomType()!=null && roomReq.getRoomType()>0){
				sb.append(" and r1.room_type="+roomReq.getRoomType());
			}
			if(roomReq.getPrice()!=null && roomReq.getPrice()>0){
				switch (roomReq.getPrice()) {
				case 1:
					sb.append(" and p1.settlement_price<150");
					break;
				case 2:
					sb.append(" and p1.settlement_price between 150 and 300");
					break;
				case 3:
					sb.append(" and p1.settlement_price between 301 and 450");
					break;
				case 4:
					sb.append(" and p1.settlement_price between 451 and 600");
					break;
				case 5:
					sb.append(" and p1.settlement_price>600");
					break;
				default:
					break;
				}
			}

			sb.append(" group by r1.id having p1.start_date=min(p1.start_date)");

			List<Object[]> ans = hyTicketHotelRoomService.statis(sb.toString());
			
			List<Map<String, Object>> objs = new ArrayList<>();
			for(Object[] item:ans){
				Map<String, Object> map = new HashMap<>();
				for(int i=0;i<item.length;i++){
					map.put(cols[i], item[i]);
				}
				objs.add(map);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(objs);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("查询失败");
			json.setSuccess(false);
			json.setObj(e);
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping("room_promotion/view")
	@ResponseBody
	public Json roomPromotion(Long id) {
		Json json = new Json();
		try {
			HyTicketHotelRoom room = hyTicketHotelRoomService.find(id);
			if(room.getPromotionActivity()!=null){
				HyPromotionActivity promotion = room.getPromotionActivity();
				Map<String, Object> map = new HashMap<>();
				map.put("id", promotion.getId());
				/** 计调 **/
				map.put("jidiao", promotion.getJidiao());
				/** 促销名称 **/
				map.put("name", promotion.getName());
				map.put("startDate", promotion.getStartDate());
				map.put("endDate", promotion.getEndDate());
				/** 优惠方式0每单满减，1每单打折，2每人减,3无促销**/
				map.put("promotionType", promotion.getPromotionType());
				/** 满减促销满足的金额 **/
				map.put("manjianPrice1", promotion.getManjianPrice1());
				/** 满减促销减免的金额 **/
				map.put("manjianPrice2", promotion.getManjianPrice2());
				/** 每人减/按数量减金额 **/
				map.put("meirenjian", promotion.getMeirenjian());
				/** 打折折扣 **/
				map.put("dazhe", promotion.getDazhe());
				/** 审核状态 0:待审核 1:通过 2:驳回  3:已过期 4:已取消**/
				map.put("state", promotion.getState());
				/** 备注 **/
				map.put("remark", promotion.getRemark());
				/** 活动类型 0:门票,1:酒店,2:酒+景,3:认购门票,4:签证 **/
				map.put("activityType", promotion.getActivityType());
				json.setObj(map);
			}
			
			json.setMsg("获取成功");
			json.setSuccess(true);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("获取失败");
			json.setSuccess(false);
			json.setObj(e);
		}
		return json;
	}
	public static class HotelReserveReq{
		private Long priceId;
		private Date startDate;
		private Date endDate;
		private Integer roomNum;
		private String contactName;
		private String contactPhone;
		private String remark;
		public Long getPriceId() {
			return priceId;
		}
		public void setPriceId(Long priceId) {
			this.priceId = priceId;
		}
		
		@DateTimeFormat(iso=ISO.DATE)
		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		
		@DateTimeFormat(iso=ISO.DATE)
		public Date getEndDate() {
			return endDate;
		}
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
		public Integer getRoomNum() {
			return roomNum;
		}
		public void setRoomNum(Integer roomNum) {
			this.roomNum = roomNum;
		}
		public String getContactName() {
			return contactName;
		}
		public void setContactName(String contactName) {
			this.contactName = contactName;
		}
		public String getContactPhone() {
			return contactPhone;
		}
		public void setContactPhone(String contactPhone) {
			this.contactPhone = contactPhone;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		

		
		
	}
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;
	
	@Resource(name="hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name = "hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	@Resource(name="hyTicketInboundServiceImpl")
	private HyTicketInboundService hyTicketInboundService;
	
	@Resource(name="hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "hyPromotionActivityServiceImpl")
	private HyPromotionActivityService hyPromotionActivityService;
	
	@RequestMapping(value="create_order",method=RequestMethod.POST)
	@ResponseBody
	public Json createOrder(@RequestBody HotelReserveReq hotelReserveReq,HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Store store = storeService.findStore(hyAdmin);
			if (store == null) {
				throw new Exception("所属门店不存在");
			}
			
			//房型价格库存
			HyTicketPriceInbound hyTicketPriceInbound = 
					hyTicketPriceInboundService.find(hotelReserveReq.getPriceId());
			if(hyTicketPriceInbound==null){
				throw new Exception("没有有效的价格库存");
			}



			HyTicketHotelRoom room = hyTicketPriceInbound.getHyTicketHotelRoom();

			if(room == null){
				throw new Exception("房间不存在");
			}

			List<Filter> priceFilters = new ArrayList<>();
			priceFilters.add(Filter.eq("hyTicketHotelRoom",room));
			List<HyTicketPriceInbound> priceInbounds = hyTicketPriceInboundService.findList(null,priceFilters,null);
			Collection<Long> priceIds = new ArrayList<>();
			for(HyTicketPriceInbound p:priceInbounds){
				priceIds.add(p.getId());
			}
			//判断库存够不够
			List<Filter> inboundFilters=new ArrayList<>();
			inboundFilters.add(Filter.in("priceInboundId", priceIds));
			inboundFilters.add(Filter.eq("type", 1));
			inboundFilters.add(Filter.ge("day", hotelReserveReq.getStartDate()));
			inboundFilters.add(Filter.le("day", hotelReserveReq.getEndDate()));
			List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,inboundFilters,null);
			Long days = DateUtil.getDaysBetweenTwoDates(hotelReserveReq.getStartDate(),hotelReserveReq.getEndDate());

			if(ticketInbounds.isEmpty()) {
				throw new Exception("该产品没有对应的库存");
			}

			synchronized(ticketInbounds){
				if(ticketInbounds.size()<days){
					throw new Exception("该房型库存不足");
				}
				//判断库存
				for(HyTicketInbound hyTicketInbound:ticketInbounds) {
					if(hyTicketInbound.getInventory()<hotelReserveReq.getRoomNum()){
						throw new Exception("该房型库存不足");
					}
				}
				//减库存
				for(HyTicketInbound hyTicketInbound:ticketInbounds) {
					hyTicketInbound.setInventory(hyTicketInbound.getInventory()-hotelReserveReq.getRoomNum());
					hyTicketInboundService.update(hyTicketInbound);
				}
			}
			
			//房型
			HyTicketHotelRoom hyTicketHotelRoom = hyTicketPriceInbound.getHyTicketHotelRoom();
			
			//酒店
			HyTicketHotel hyTicketHotel = hyTicketHotelRoom.getHyTicketHotel();
			
			//新建订单
			HyOrder hyOrder = new HyOrder();

			List<Integer> sns = new ArrayList<>();
			sns.add(SequenceTypeEnum.orderSn.ordinal());
			//生成订单编号
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.in("type", sns));
			Long value = 0L;
			synchronized (this) {
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				CommonSequence c = ss.get(0);
				if (c.getValue() >= 99999) {
					c.setValue(0l);
				}
				value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String nowaday = sdf.format(new Date());
			String code = nowaday + String.format("%05d", value);
			hyOrder.setOrderNumber(code);
			hyOrder.setName(hyTicketHotel.getHotelName()+"订购酒店订单");	//订单名称
			hyOrder.setStatus(0);// 0门店待支付
			hyOrder.setPaystatus(0);// 0门店待支付
			hyOrder.setCheckstatus(0);// 门店待确认
			hyOrder.setGuideCheckStatus(0);	//供应商待确认状态
			hyOrder.setRefundstatus(0);// 门店未退款
			hyOrder.setType(3);	//酒店订单
			hyOrder.setSource(0);	//门店来源
			hyOrder.setPeople(hotelReserveReq.getRoomNum());	//房间订购数量
			hyOrder.setStoreType(0);	//虹宇门店
			hyOrder.setStoreId(store.getId());	//门店id
			hyOrder.setOperator(hyAdmin);	//门店处理人i
			hyOrder.setCreatorId(username);	//创建者id
			
			//调整金额待处理
			hyOrder.setAdjustMoney(BigDecimal.ZERO);	//调整金额为0

			List<HyOrderItem> orderItems = new ArrayList<>();

			BigDecimal jiesuanMoney = BigDecimal.ZERO;
			BigDecimal waimaiMoney = BigDecimal.ZERO;
			for(HyTicketInbound hyTicketInbound : ticketInbounds){

				HyTicketPriceInbound priceInbound = hyTicketPriceInboundService.find(hyTicketInbound.getPriceInboundId());
				//订单条目
				HyOrderItem hyOrderItem = new HyOrderItem();
				hyOrderItem.setStatus(0);	//0为有效
				hyOrderItem.setStartDate(hyTicketInbound.getDay());	//开始时间
				hyOrderItem.setEndDate(hyTicketInbound.getDay());	//结束时间
				hyOrderItem.setName(hyTicketHotelRoom.getProductName());	//订单条目名称
				hyOrderItem.setType(3);	//酒店
				hyOrderItem.setPriceType(null);	//价格类型为null
				hyOrderItem.setJiesuanPrice(priceInbound.getSettlementPrice());	//结算价
				hyOrderItem.setWaimaiPrice(priceInbound.getSellPrice());	//外卖价
				hyOrderItem.setNumber(hotelReserveReq.getRoomNum());	//购买数量
				hyOrderItem.setNumberOfReturn(0);	//退货数量
				hyOrderItem.setOrder(hyOrder);	//所属订单
				hyOrderItem.setProductId(hyTicketHotel.getId());	//酒店id
				hyOrderItem.setSpecificationId(hyTicketHotelRoom.getId());	//房型id
				hyOrderItem.setPriceId(priceInbound.getId());	//价格id
				hyOrderItem.setHyOrderCustomers(null);	//顾客为null

				jiesuanMoney = jiesuanMoney.add(priceInbound.getSettlementPrice().multiply(BigDecimal.valueOf(hotelReserveReq.getRoomNum())));

				waimaiMoney = waimaiMoney.add(priceInbound.getSellPrice().multiply(BigDecimal.valueOf(hotelReserveReq.getRoomNum())));


				orderItems.add(hyOrderItem);


			}

			hyOrder.setOrderItems(orderItems);	//订单条目

			//除保险之外的订单条目的总结算价,没有保险
			hyOrder.setJiesuanMoney1(jiesuanMoney);
			//订单结算价,没有保险，
			hyOrder.setJiusuanMoney(hyOrder.getJiesuanMoney1());
			//外卖价
			hyOrder.setWaimaiMoney(waimaiMoney);
			hyOrder.setJiesuanTuikuan(BigDecimal.ZERO);	//结算退款价
			hyOrder.setWaimaiTuikuan(BigDecimal.ZERO);	//外卖退款价
			hyOrder.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);	//保险结算退款价
			hyOrder.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);	//保险外卖结算价
			hyOrder.setIfjiesuan(false);// 未结算
			hyOrder.setInsuranceOrderDownloadUrl(null);	//没有保险
			hyOrder.setJiesuantime(null);	//没有结算
			
			//获取优惠活动
			HyPromotionActivity promotionActivity = hyTicketHotelRoom.getPromotionActivity();
			if(promotionActivity==null) {
				hyOrder.setDiscountedType(3);
			}else {
				hyOrder.setDiscountedType(promotionActivity.getPromotionType());
				hyOrder.setDiscountedId(promotionActivity.getId());
			}
			//获取优惠金额
			hyOrder.setDiscountedPrice(
					hyPromotionActivityService.getDiscountedPriceByHyOrder(hyOrder, promotionActivity));

			//计算扣点
			//找供应商合同
			HySupplierContract hySupplierContract = hySupplierContractService.getByLiable(hyTicketHotel.getCreator());
			HySupplierDeductPiaowu hySupplierDeductPiaowu = hySupplierContract.getHySupplierDeductPiaowu();
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
			
			//HyOrder中 departure 到 xingchenggaiyao 属性为线路订单属性，均为null，
			
			//tip 相关也没有，为0
			hyOrder.setTip(BigDecimal.ZERO);
			
			hyOrder.setContact(hotelReserveReq.getContactName());	//联系人姓名
			hyOrder.setContactIdNumber(null);	//联系人身份证为null
			hyOrder.setPhone(hotelReserveReq.getContactPhone());	//联系人电话
			hyOrder.setRemark(hotelReserveReq.getRemark());	//备注
			
			//合同相关设为null
			
			hyOrder.setCreatetime(new Date());	//创建时间
			hyOrder.setModifytime(null);	//修改时间设为null




			
			hyOrder.setGroupId(null);	//没有团

			hyOrder.setSupplier(hyTicketHotel.getCreator());	//产品创建者，供应商账号
			
			hyOrder.setIsDivideStatistic(false);	//没有分成统计
			
			hyOrderService.save(hyOrder);
			
			json.setSuccess(true);
			json.setMsg("下单成功");
			json.setObj(hyOrder);
			
			//订单日志,插一条记录到hy_order_application
			HyOrderApplication hyOrderApplication=new HyOrderApplication();
			hyOrderApplication.setOperator(hyAdmin);
			hyOrderApplication.setCreatetime(new Date());
			hyOrderApplication.setStatus(1); //通过
			hyOrderApplication.setContent("门店下订单");
			hyOrderApplication.setOrderId(hyOrder.getId());
			hyOrderApplication.setOrderNumber(hyOrder.getOrderNumber());
			hyOrderApplication.setType(8); //8-门店下订单
			hyOrderApplicationService.save(hyOrderApplication);
			
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg(e.getMessage());
			json.setObj(e);
			e.printStackTrace();
		}
		return json;
	}

}
