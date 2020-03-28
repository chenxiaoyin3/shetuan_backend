package com.hongyu.controller.lbc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Resource;
import javax.mail.search.IntegerComparisonTerm;
import javax.servlet.http.HttpSession;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyPromotionActivity;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierDeductPiaowu;
import com.hongyu.entity.HySupplierDeductRengou;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.entity.HyTicketInbound;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketSubscribe;
import com.hongyu.entity.HyTicketSubscribePrice;
import com.hongyu.entity.Store;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.service.CaptchaService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyPromotionActivityService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HyTicketHotelRoomService;
import com.hongyu.service.HyTicketHotelService;
import com.hongyu.service.HyTicketInboundService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.HyTicketSubscribePriceService;
import com.hongyu.service.HyTicketSubscribeService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;
import com.hongyu.util.Constants.DeductPiaowu;

import oracle.net.aso.f;
import oracle.net.aso.i;

@Controller
@RequestMapping("admin/ticket_subscribe_reserve/")
public class HyTicketSubscribeReserveController {
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hyTicketSubscribeServiceImpl")
	private HyTicketSubscribeService hyTicketSubscribeService;
	
	@Resource(name="hyTicketSubscribePriceServiceImpl")
	private HyTicketSubscribePriceService hyTicketSubscribePriceService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	@Resource(name = "hyPromotionActivityServiceImpl")
	private HyPromotionActivityService hyPromotionActivityService;
	/**
	 * 认购门票分页
	 * @param sceneName
	 * @param address
	 * @param star
	 * @param pageable
	 * @param session
	 * @return
	 */
	@RequestMapping("page/view")
	@ResponseBody
	public Json pageView(String sceneName,Long area_id,Integer star,
			Pageable pageable,HttpSession session){
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		Json json = new Json();
		try {
			
			List<Filter> ticketSubscribeFilters = new ArrayList<>();
			ticketSubscribeFilters.add(Filter.eq("saleStatus", 2));
			if(sceneName!=null)
				ticketSubscribeFilters.add(Filter.like("sceneName", sceneName));
			if(area_id!=null) {
				HyArea hyArea = hyAreaService.find(area_id);
				List<HyArea> hyAreas= new ArrayList<>();
				//bfs
				Queue<HyArea> queue = new LinkedList<>();
				queue.add(hyArea);
				while(!queue.isEmpty()) {
					HyArea hyArea2 = queue.poll();
					hyAreas.add(hyArea2);
					if(hyArea2.getHyAreas().size() > 0) {
						queue.addAll(hyArea2.getHyAreas());
					}
				}
				ticketSubscribeFilters.add(Filter.in("area", hyAreas));
			}
			if(star!=null)
				ticketSubscribeFilters.add(Filter.eq("star", star));
//			pageable.setFilters(hotelFilters);
			
			List<Order> hotelOrders = new ArrayList<Order>();
			hotelOrders.add(Order.desc("createTime"));
//			pageable.setOrders(hotelOrders);
			
//			Page<HyTicketHotel> page=hyTicketHotelService.findPage(pageable);
			List<HyTicketSubscribe> alList = hyTicketSubscribeService.findList(null,ticketSubscribeFilters,hotelOrders);
			List<Map<String, Object>> list = new ArrayList<>();
			if(alList.size()>0){
				for(HyTicketSubscribe hyTicketSubscribe : alList){
					Map<String, Object> ticketSubMap = new HashMap<>();
					ticketSubMap.put("id", hyTicketSubscribe.getId());
					ticketSubMap.put("sceneName", hyTicketSubscribe.getSceneName());
					ticketSubMap.put("sceneaddress", hyTicketSubscribe.getSceneAddress());
					ticketSubMap.put("star", hyTicketSubscribe.getStar());
					//加上推广详解
					ticketSubMap.put("introduction", hyTicketSubscribe.getIntroduction());
					List<Filter> priceFilters = new ArrayList<>();
					Date appointmentTime = new Date();
					priceFilters.add(Filter.eq("ticketSubscribe", hyTicketSubscribe));
					//结束时间要大于当前时间
					priceFilters.add(Filter.ge("endDate", appointmentTime));
//					priceFilters.add(Filter.le("startDate", appointmentTime));
					List<HyTicketSubscribePrice> hyTicketSubscribePrices = hyTicketSubscribePriceService.findList(null, priceFilters, null);
					//找到对应的价格
					if(hyTicketSubscribePrices.size() == 0) {
						//throw new Exception("当前认购门票没有有效价格记录");
						continue;
					}
					Date startDate = null;
					Date endDate = null;
					for(HyTicketSubscribePrice hyTicketSubscribePrice : hyTicketSubscribePrices) {
						if(startDate == null) {
							startDate = hyTicketSubscribePrice.getStartDate();
						}
						else {
							if(startDate.compareTo(hyTicketSubscribePrice.getStartDate()) > 0) {
								startDate = hyTicketSubscribePrice.getStartDate();
							}
						}
						if(endDate == null) {
							endDate = hyTicketSubscribePrice.getEndDate();
						}
						else {
							if(endDate.compareTo(hyTicketSubscribePrice.getEndDate()) < 0) {
								endDate = hyTicketSubscribePrice.getEndDate();
							}
						}
						
					}
					
					ticketSubMap.put("startDate", startDate);
					ticketSubMap.put("endDate", endDate);
					
					List<Filter> filters = new ArrayList<>();
					filters.add(Filter.eq("ticketSubscribe", hyTicketSubscribe));
					//找出票价对应的认购门票
					//按成人价排位
					List<HyTicketSubscribePrice> priceList = hyTicketSubscribePriceService.findList(null, filters, null);
					
					if(priceList.size() == 0) {
						ticketSubMap.put("lowestPrice", null);
					}
					else {
						//记录最小的位置
						Collections.sort(priceList, new Comparator<HyTicketSubscribePrice>() {
	
							@Override
							public int compare(HyTicketSubscribePrice arg0, HyTicketSubscribePrice arg1) {
								// TODO Auto-generated method stub
								return arg0.getAdultSettlePrice().compareTo(arg1.getAdultSettlePrice());
							}
							
						});
						ticketSubMap.put("lowestPrice", priceList.get(0).getAdultSettlePrice());
					}
//					if(roomList.size()>0){
//						for(HyTicketHotelRoom room:roomList){
//							List<HyTicketPriceInbound> priceList=new ArrayList<>(room.getHyTicketPriceInbounds());
//							inboundList.addAll(priceList);
//						}
//						if(inboundList.size()>0){
//						    /* 找出最低价格 */
//						    Collections.sort(inboundList, new Comparator<HyTicketPriceInbound>() {
//								@Override
//								public int compare(HyTicketPriceInbound o1,HyTicketPriceInbound o2) {
//									BigDecimal price1 = o1.getSettlementPrice();
//									BigDecimal price2 = o2.getSettlementPrice();
//									return price1.compareTo(price2); 
//								}
//							});
//						    hotelMap.put("lowestPrice", inboundList.get(0).getSettlementPrice());
//						}else{
//							continue;
//						}
//					}else{
////					    hotelMap.put("lowestPrice", null);
//						continue;
//					}
					//只返回有价格的有效酒店
					
					list.add(ticketSubMap);
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
	 * 认购门票详情
	 * @param id
	 * @param appointmentTime
	 * @param session
	 * @return
	 */
	@RequestMapping("ticket_subscribe_detail/view")
	@ResponseBody
	public Json storeDetail(Long id ,@DateTimeFormat(pattern="yyyy-MM-dd") Date appointmentTime,HttpSession session){
		Json json = new Json();
		try {
			//HyTicketSubScribePriceInventory hyTicketSubScribePriceInventory = new HyTicketSubScribePriceInventory();
			HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(id);
			if(hyTicketSubscribe==null){
				json.setSuccess(false);
				json.setMsg("没有有效认购门票记录");
				json.setObj(hyTicketSubscribe);
				return json;
			}
			//查找价格信息
			List<Filter> priceFilters = new ArrayList<>();
			priceFilters.add(Filter.eq("ticketSubscribe", hyTicketSubscribe));
			priceFilters.add(Filter.ge("endDate", appointmentTime));
			priceFilters.add(Filter.le("startDate", appointmentTime));
			List<HyTicketSubscribePrice> hyTicketSubscribePrices = hyTicketSubscribePriceService.findList(null, priceFilters, null);
			if(hyTicketSubscribePrices.size() == 0) {
				json.setSuccess(true);
				json.setMsg("当前认购门票没有有效价格记录");
				json.setObj(hyTicketSubscribe);
				return json;
			}
			//hyTicketSubScribePriceInventory.setHyTicketSubscribePrices(hyTicketSubscribePrices);
			
			int nullSize = 0;
			
			//List<HyTicketSubscribePriceAndInventory> hyTicketSubscribePriceAndInventories = new ArrayList<>();
			for(HyTicketSubscribePrice hyTicketSubscribePrice : hyTicketSubscribePrices) {
				List<Filter> inboundFilters = new ArrayList<>();
				inboundFilters.add(Filter.eq("priceInboundId", hyTicketSubscribePrice.getId()));
				inboundFilters.add(Filter.eq("day", appointmentTime));
				List<HyTicketInbound> hyTicketInbounds = hyTicketInboundService.findList(null, inboundFilters, null);
				if(hyTicketInbounds.size() == 0) {
					hyTicketSubscribePrice.setInventory(null);
					//throw new Exception("当前认购门票当前时间没有库存记录");
					nullSize++;
					continue;
				}
				//放inventory
				hyTicketSubscribePrice.setInventory(hyTicketInbounds.get(0).getInventory());
				
			}
			
			hyTicketSubscribe.setHyTicketSubscribePrices(hyTicketSubscribePrices);
			
			if(hyTicketSubscribePrices.size() == nullSize) {
				json.setSuccess(true);
				json.setMsg("当前认购门票没有有效库存记录");
				json.setObj(hyTicketSubscribe);
				return json;
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hyTicketSubscribe);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		return json;
	}
	
	@SuppressWarnings("serial")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	class HyTicketSubScribePriceInventory implements java.io.Serializable{
		
		
		private List<HyTicketSubscribePriceAndInventory> hyTicketSubscribePrices;
		private HyTicketSubscribe hyTicketSubscribe;
		
		

		public HyTicketSubscribe getHyTicketSubscribe() {
			return hyTicketSubscribe;
		}

		public void setHyTicketSubscribe(HyTicketSubscribe hyTicketSubscribe) {
			this.hyTicketSubscribe = hyTicketSubscribe;
		}

		public List<HyTicketSubscribePriceAndInventory> getHyTicketSubscribePrices() {
			return hyTicketSubscribePrices;
		}

		public void setHyTicketSubscribePrices(List<HyTicketSubscribePriceAndInventory> hyTicketSubscribePrices) {
			this.hyTicketSubscribePrices = hyTicketSubscribePrices;
		}

		
		
	}
	
	
	@SuppressWarnings("serial")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	class HyTicketSubscribePriceAndInventory implements java.io.Serializable{
		
		private HyTicketSubscribePrice hyTicketSubscribePrice;
		private Integer ticketInventory;
		
		public HyTicketSubscribePrice getHyTicketSubscribePrice() {
			return hyTicketSubscribePrice;
		}
		public void setHyTicketSubscribePrice(HyTicketSubscribePrice hyTicketSubscribePrice) {
			this.hyTicketSubscribePrice = hyTicketSubscribePrice;
		}
		public Integer getTicketInventory() {
			return ticketInventory;
		}
		public void setTicketInventory(Integer ticketInventory) {
			this.ticketInventory = ticketInventory;
		}
		
		
		
	}
	
//	/**
//	 * 酒店房间列表
//	 * @param id
//	 * @param roomReq
//	 * @param session
//	 * @return
//	 */
//	@RequestMapping("hotel_room_list/view")
//	@ResponseBody
//	public Json hotelRoomList(Long id,HyTicketHotelRoomReq roomReq,HttpSession session){
//		Json json = new Json();
//		try {
//			String[] cols = new String[]{"roomId","productId","productName","roomType","isWifi","isWindow","isBathroom",
//					"available","breakfast","priceId","inventory","displayPrice","sellPrice","settlementPrice","startDate","endDate"};
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//			StringBuilder sb = new StringBuilder("select r1.id rid,r1.product_id,r1.product_name,r1.room_type,r1.is_wifi,r1.is_window,r1.is_bathroom,"
//					+ "r1.available,r1.breakfast,p1.id pid,p1.inventory,p1.display_price,p1.sell_price,p1.settlement_price,p1.start_date,p1.end_date"
//					+ " from hy_ticket_hotel_room r1,hy_ticket_price_inbound p1"
//					+ " where r1.id=p1.room_id and r1.audit_status=3 and r1.sale_status=2 and r1.status=1");
//			if(id!=null){
//				sb.append(" and r1.hotel="+id);
//			}
//			if(roomReq.getStartDate()!=null){
//				sb.append(" and DATE_FORMAT(p1.start_date,'%Y-%m-%d')<='"+format.format(roomReq.getStartDate())+"'");
//			}
//			if(roomReq.getEndDate()!=null){
//				sb.append(" and DATE_FORMAT(p1.end_date,'%Y-%m-%d')>='"+format.format(roomReq.getEndDate())+"'");
//			}
//			if(roomReq.getIsWifi()!=null){
//				sb.append(" and r1.is_wifi="+roomReq.getIsWifi());
//			}
//			if(roomReq.getIsWindow()!=null){
//				sb.append(" and r1.is_window="+roomReq.getIsWindow());
//			}
//			if(roomReq.getIsBathroom()!=null){
//				sb.append(" and r1.is_bathroom="+roomReq.getIsBathroom());
//			}
//			if(roomReq.getBreakfast()!=null && roomReq.getBreakfast()>0){
//				sb.append(" and r1.breakfast="+roomReq.getBreakfast());
//			}
//			if(roomReq.getAvailable()!=null && roomReq.getAvailable()>0){
//				sb.append(" and r1.available="+roomReq.getAvailable());
//			}
//			if(roomReq.getRoomType()!=null && roomReq.getRoomType()>0){
//				sb.append(" and r1.room_type="+roomReq.getRoomType());
//			}
//			if(roomReq.getPrice()!=null && roomReq.getPrice()>0){
//				switch (roomReq.getPrice()) {
//				case 1:
//					sb.append(" and p1.settlement_price<150");
//					break;
//				case 2:
//					sb.append(" and p1.settlement_price between 150 and 300");
//					break;
//				case 3:
//					sb.append(" and p1.settlement_price between 301 and 450");
//					break;
//				case 4:
//					sb.append(" and p1.settlement_price between 451 and 600");
//					break;
//				case 5:
//					sb.append(" and p1.settlement_price>600");
//					break;
//				default:
//					break;
//				}
//			}
//			
//			List<Object[]> ans = hyTicketHotelRoomService.statis(sb.toString());
//			
//			List<Map<String, Object>> objs = new ArrayList<>();
//			for(Object[] item:ans){
//				Map<String, Object> map = new HashMap<>();
//				for(int i=0;i<item.length;i++){
//					map.put(cols[i], item[i]);
//				}
//				objs.add(map);
//			}
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(objs);
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			json.setMsg("查询失败");
//			json.setSuccess(false);
//			json.setObj(e);
//			e.printStackTrace();
//		}
//		return json;
//	}
	
	public static class TicketSubscribeReserveReq{
		
		private Long priceId;
		@DateTimeFormat(pattern="yyyy-MM-dd")
		private Date date;
		private Integer adultNum;
		private Integer kidNum;
		private Integer studentNum;
		private Integer oldNum;
		
		
		private String guideName;
		private String guidePhone;
		private String remark;
		
		public Long getPriceId() {
			return priceId;
		}
		public void setPriceId(Long priceId) {
			this.priceId = priceId;
		}
		@DateTimeFormat(pattern="yyyy-MM-dd")
		public Date getDate() {
			return date;
		}
		@DateTimeFormat(pattern="yyyy-MM-dd")   
		public void setDate(Date date) {
			this.date = date;
		}
		public Integer getAdultNum() {
			return adultNum;
		}
		public void setAdultNum(Integer adultNum) {
			this.adultNum = adultNum;
		}
		public Integer getKidNum() {
			return kidNum;
		}
		public void setKidNum(Integer kidNum) {
			this.kidNum = kidNum;
		}
		public Integer getStudentNum() {
			return studentNum;
		}
		public void setStudentNum(Integer studentNum) {
			this.studentNum = studentNum;
		}
		public Integer getOldNum() {
			return oldNum;
		}
		public void setOldNum(Integer oldNum) {
			this.oldNum = oldNum;
		}
		public String getGuideName() {
			return guideName;
		}
		public void setGuideName(String guideName) {
			this.guideName = guideName;
		}
		public String getGuidePhone() {
			return guidePhone;
		}
		public void setGuidePhone(String guidePhone) {
			this.guidePhone = guidePhone;
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
	
	@RequestMapping(value="create_order",method=RequestMethod.POST)
	@ResponseBody
	public Json createOrder(@RequestBody TicketSubscribeReserveReq ticketSubscribeReserveReq,HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Store store = storeService.findStore(hyAdmin);
			if (store == null) {
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
				return json;
			}
			
			//认购门票价格库存
			HyTicketSubscribePrice hyTicketSubscribePrice = 
					hyTicketSubscribePriceService.find(ticketSubscribeReserveReq.getPriceId());
			if(hyTicketSubscribePrice==null){
				json.setSuccess(false);
				json.setMsg("没有有效的认购门票价格");
				return json;
			}
			
			if(ticketSubscribeReserveReq.getAdultNum() == null) {
				ticketSubscribeReserveReq.setAdultNum(0);
			}
			if(ticketSubscribeReserveReq.getKidNum() == null) {
				ticketSubscribeReserveReq.setKidNum(0);
			}
			if(ticketSubscribeReserveReq.getOldNum() == null) {
				ticketSubscribeReserveReq.setOldNum(0);
			}
			if(ticketSubscribeReserveReq.getStudentNum() == null) {
				ticketSubscribeReserveReq.setStudentNum(0);
			}
			
			Integer ticketTotalNum = ticketSubscribeReserveReq.getAdultNum() + ticketSubscribeReserveReq.getKidNum() + 
					ticketSubscribeReserveReq.getOldNum() + ticketSubscribeReserveReq.getStudentNum();
			
			HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribePrice.getTicketSubscribe();

			//判断库存够不够
			List<Filter> inboundFilters=new ArrayList<>();
			inboundFilters.add(Filter.eq("priceInboundId", hyTicketSubscribePrice.getId()));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(ticketSubscribeReserveReq.getDate());
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			//认购门票
			inboundFilters.add(Filter.eq("type", 2));
			inboundFilters.add(Filter.eq("day", calendar.getTime()));
//			inboundFilters.add(Filter.ge("day", hotelReserveReq.getStartDate()));
//			inboundFilters.add(Filter.le("day", hotelReserveReq.getEndDate()));
			List<HyTicketInbound> ticketInbounds=hyTicketInboundService.findList(null,inboundFilters,null);
			
			if(ticketInbounds.isEmpty()) {
				json.setSuccess(false);
				json.setMsg("该产品没有对应的库存");
				return json;
			}

			synchronized(ticketInbounds){
				HyTicketInbound hyTicketInbound = ticketInbounds.get(0);
				//判断库存
				if(ticketTotalNum > hyTicketInbound.getInventory()) {
					json.setSuccess(false);
					json.setMsg("该认购门票库存不足");
					return json;
				}
				
//				for(HyTicketInbound hyTicketInbound:ticketInbounds) {
//					if(hyTicketInbound.getInventory()<hotelReserveReq.getRoomNum()){
//						throw new Exception("该房型库存不足");
//					}
//				}
				//减库存
				hyTicketInbound.setInventory(hyTicketInbound.getInventory() - ticketTotalNum);
			    hyTicketInboundService.update(hyTicketInbound);
//				for(HyTicketInbound hyTicketInbound:ticketInbounds) {
//					hyTicketInbound.setInventory(hyTicketInbound.getInventory()-hotelReserveReq.getRoomNum());
//					hyTicketInboundService.update(hyTicketInbound);
//				}
			}
			
//			//房型
//			HyTicketHotelRoom hyTicketHotelRoom = hyTicketPriceInbound.getHyTicketHotelRoom();
//			
//			//酒店
//			HyTicketHotel hyTicketHotel = hyTicketHotelRoom.getHyTicketHotel();
			
			//新建订单
			HyOrder hyOrder = new HyOrder();
			
			//生成订单编号
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.in("type", SequenceTypeEnum.orderSn));
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
			hyOrder.setName(hyTicketSubscribe.getSceneName()+"订购认购门票订单");	//订单名称
			hyOrder.setStatus(0);// 0门店待支付
			hyOrder.setPaystatus(0);// 0门店待支付
			hyOrder.setCheckstatus(0);// 门店待确认
			hyOrder.setGuideCheckStatus(0);	//供应商待确认状态
			hyOrder.setRefundstatus(0);// 门店未退款
			hyOrder.setType(2);	//认购门票订单
			hyOrder.setSource(0);	//门店来源
			hyOrder.setPeople(ticketTotalNum);	//房间订购数量
			if(store.getStoreType() == null) {
				hyOrder.setStoreType(0);	//虹宇门店
			}
			else {
				hyOrder.setStoreType(store.getStoreType() == 0 ? 0 : 1);	//虹宇门店
			}
			
			hyOrder.setStoreId(store.getId());	//门店id
			hyOrder.setOperator(hyAdmin);	//门店处理人i
			hyOrder.setCreatorId(username);	//创建者id
			
			//调整金额待处理
			hyOrder.setAdjustMoney(BigDecimal.ZERO);	//调整金额为0
			
			//优惠待处理
			hyOrder.setDiscountedType(3);	//无优惠
			hyOrder.setDiscountedId(null);	//无优惠
			hyOrder.setDiscountedPrice(BigDecimal.ZERO);	//优惠金额为0
			
			//获取优惠活动
//			HyPromotionActivity promotionActivity = hyTicketSubscribe.getHyPromotionActivity();
//			if(promotionActivity==null) {
//				hyOrder.setDiscountedType(3);
//			}else {
//				hyOrder.setDiscountedType(promotionActivity.getPromotionType());
//				hyOrder.setDiscountedId(promotionActivity.getId());
//			}
//			//获取优惠金额
//			hyOrder.setDiscountedPrice(
//					hyPromotionActivityService.getDiscountedPriceByHyOrder(hyOrder, promotionActivity));
			
			
			//计算总结算价和总外卖价
			BigDecimal totalSettlementPrice = new BigDecimal(0);
			BigDecimal totalAudltSettlementPrice = new BigDecimal(0);
			BigDecimal totalOldSettlementPrice = new BigDecimal(0);
			BigDecimal totalKidSettlementPrice = new BigDecimal(0);
			BigDecimal totalStudentSettlementPrice = new BigDecimal(0);
			
			
			if(hyTicketSubscribePrice.getAdultSettlePrice() == null) {
				hyTicketSubscribePrice.setAdultSettlePrice(new BigDecimal(0));
			}
			if(hyTicketSubscribePrice.getOldSettlePrice() == null) {
				hyTicketSubscribePrice.setOldSettlePrice(new BigDecimal(0));
			}
			if(hyTicketSubscribePrice.getChildSettlePrice() == null) {
				hyTicketSubscribePrice.setChildSettlePrice(new BigDecimal(0));
			}
			if(hyTicketSubscribePrice.getStudentSettlePrice() == null) {
				hyTicketSubscribePrice.setStudentSettlePrice(new BigDecimal(0));
			}
			
			totalAudltSettlementPrice = hyTicketSubscribePrice.getAdultSettlePrice().multiply(new BigDecimal(ticketSubscribeReserveReq.getAdultNum()));
			totalOldSettlementPrice = hyTicketSubscribePrice.getOldSettlePrice().multiply(new BigDecimal(ticketSubscribeReserveReq.getOldNum()));
			totalKidSettlementPrice = hyTicketSubscribePrice.getChildSettlePrice().multiply(new BigDecimal(ticketSubscribeReserveReq.getKidNum()));
			totalStudentSettlementPrice = hyTicketSubscribePrice.getStudentSettlePrice().multiply(new BigDecimal(ticketSubscribeReserveReq.getStudentNum()));
			
			totalSettlementPrice = totalAudltSettlementPrice.add(totalOldSettlementPrice).add(totalKidSettlementPrice).add(totalStudentSettlementPrice);
			
			BigDecimal totalOutsalePrice = new BigDecimal(0);
			BigDecimal totalAudltOutsalePrice = new BigDecimal(0);
			BigDecimal totalOldOutsalePrice = new BigDecimal(0);
			BigDecimal totalKidOutsalePrice = new BigDecimal(0);
			BigDecimal totalStudentOutsalePrice = new BigDecimal(0);
			
			if(hyTicketSubscribePrice.getAdultOutsalePrice() == null) {
				hyTicketSubscribePrice.setAdultOutsalePrice(new BigDecimal(0));
			}
			if(hyTicketSubscribePrice.getOldOutsalePrice() == null) {
				hyTicketSubscribePrice.setOldOutsalePrice(new BigDecimal(0));
			}
			if(hyTicketSubscribePrice.getChildOutPrice() == null) {
				hyTicketSubscribePrice.setChildOutPrice(new BigDecimal(0));
			}
			if(hyTicketSubscribePrice.getStudentOutsalePrice() == null) {
				hyTicketSubscribePrice.setStudentOutsalePrice(new BigDecimal(0));
			}
			
			totalAudltOutsalePrice = hyTicketSubscribePrice.getAdultOutsalePrice().multiply(new BigDecimal(ticketSubscribeReserveReq.getAdultNum()));
			totalOldOutsalePrice = hyTicketSubscribePrice.getOldOutsalePrice().multiply(new BigDecimal(ticketSubscribeReserveReq.getOldNum()));
			totalKidOutsalePrice = hyTicketSubscribePrice.getChildOutPrice().multiply(new BigDecimal(ticketSubscribeReserveReq.getKidNum()));
			totalStudentOutsalePrice = hyTicketSubscribePrice.getStudentOutsalePrice().multiply(new BigDecimal(ticketSubscribeReserveReq.getStudentNum()));
			
			totalOutsalePrice = totalAudltOutsalePrice.add(totalOldOutsalePrice).add(totalKidOutsalePrice).add(totalStudentOutsalePrice);
			
			//除保险之外的订单条目的总结算价,没有保险
			hyOrder.setJiesuanMoney1(totalSettlementPrice);
			//订单结算价,没有保险，
			hyOrder.setJiusuanMoney(hyOrder.getJiesuanMoney1());
			//外卖价
			hyOrder.setWaimaiMoney(totalOutsalePrice);
			
			hyOrder.setJiesuanTuikuan(BigDecimal.ZERO);	//结算退款价
			hyOrder.setWaimaiTuikuan(BigDecimal.ZERO);	//外卖退款价
			hyOrder.setBaoxianJiesuanTuikuan(BigDecimal.ZERO);	//保险结算退款价
			hyOrder.setBaoxianWaimaiTuikuan(BigDecimal.ZERO);	//保险外卖结算价
			hyOrder.setIfjiesuan(false);// 未结算
			hyOrder.setInsuranceOrderDownloadUrl(null);	//没有保险
			hyOrder.setJiesuantime(null);	//没有结算
			
			//计算扣点
			//找供应商合同
			//HySupplierContract hySupplierContract = hySupplierContractService.getByHySupplier(hyTicketSubscribe.getTicketSupplier());
			HySupplierContract hySupplierContract = hySupplierContractService.getByLiable(hyTicketSubscribe.getCreator());
			if(hySupplierContract == null) {
				json.setSuccess(false);
				json.setMsg("合同为空");
				return json;
			}
			//计算扣点
			//找供应商合同
			HySupplierDeductRengou hySupplierDeductRengou = hySupplierContract.getHySupplierDeductRengou();
			
			if(hySupplierDeductRengou == null) {
				json.setSuccess(false);
				json.setMsg("认购门票折扣为空");
				return json;
			}
			
			hyOrder.setKoudianMethod(hySupplierDeductRengou.getDeductRengou().ordinal());	//扣点方式
			if(hyOrder.getKoudianMethod().equals(Constants.DeductPiaowu.liushui.ordinal())){
				//流水扣点
				hyOrder.setProportion(hySupplierDeductRengou.getLiushuiRengou());
				hyOrder.setKoudianMoney(hyOrder.getJiusuanMoney().multiply(
						hyOrder.getProportion().multiply(BigDecimal.valueOf(0.01))));
			}else{
				//人头扣点
				hyOrder.setHeadProportion(hySupplierDeductRengou.getRentouRengou());
				hyOrder.setKoudianMoney(hyOrder.getHeadProportion().multiply(
						BigDecimal.valueOf(hyOrder.getPeople())));
			}
			
			
			//HyOrder中 departure 到 xingchenggaiyao 属性为线路订单属性，均为null，
			
			//tip 相关也没有，为0
			hyOrder.setTip(BigDecimal.ZERO);
			
			hyOrder.setContact(ticketSubscribeReserveReq.getGuideName());	//联系人姓名
			hyOrder.setContactIdNumber(null);	//联系人身份证为null
			hyOrder.setPhone(ticketSubscribeReserveReq.getGuidePhone());	//联系人电话
			hyOrder.setRemark(ticketSubscribeReserveReq.getRemark());	//备注
			
			//合同相关设为null
			
			hyOrder.setCreatetime(new Date());	//创建时间
			hyOrder.setModifytime(null);	//修改时间设为null
			
			List<HyOrderItem> orderItems = new ArrayList<>();
			
			
			//小孩，老人，学生，成人各对应一个订单条目
			if(ticketSubscribeReserveReq.getAdultNum() > 0) {
				//订单条目
				HyOrderItem hyOrderItem = new HyOrderItem();
				hyOrderItem.setStatus(0);	//0为有效
				hyOrderItem.setStartDate(hyTicketSubscribePrice.getStartDate());	//开始时间
				hyOrderItem.setEndDate(hyTicketSubscribePrice.getEndDate());	//结束时间
				hyOrderItem.setName(hyTicketSubscribe.getSceneName());	//订单条目名称
				hyOrderItem.setType(2);	//认购门票
				hyOrderItem.setPriceType(0);	//普通成人价
				hyOrderItem.setJiesuanPrice(totalAudltSettlementPrice);	//结算价
				hyOrderItem.setWaimaiPrice(totalAudltOutsalePrice);	//外卖价
				hyOrderItem.setNumber(ticketSubscribeReserveReq.getAdultNum());	//购买数量
				hyOrderItem.setNumberOfReturn(0);	//退货数量
				hyOrderItem.setOrder(hyOrder);	//所属订单
				hyOrderItem.setProductId(hyTicketSubscribe.getId());	//认购门票id
				hyOrderItem.setSpecificationId(ticketInbounds.get(0).getId());	//当天的库存id
				hyOrderItem.setPriceId(hyTicketSubscribePrice.getId());	//价格id
				hyOrderItem.setHyOrderCustomers(null);	//顾客为null
				hyOrderItem.setNumberOfReturn(0);
				orderItems.add(hyOrderItem);
			}
			//kid
			if(ticketSubscribeReserveReq.getKidNum() > 0) {
				//订单条目
				HyOrderItem hyOrderItem = new HyOrderItem();
				hyOrderItem.setStatus(0);	//0为有效
				hyOrderItem.setStartDate(hyTicketSubscribePrice.getStartDate());	//开始时间
				hyOrderItem.setEndDate(hyTicketSubscribePrice.getEndDate());	//结束时间
				hyOrderItem.setName(hyTicketSubscribe.getSceneName());	//订单条目名称
				hyOrderItem.setType(2);	//认购门票
				hyOrderItem.setPriceType(1); //普通儿童价
				hyOrderItem.setJiesuanPrice(totalKidSettlementPrice);	//结算价
				hyOrderItem.setWaimaiPrice(totalKidOutsalePrice);	//外卖价
				hyOrderItem.setNumber(ticketSubscribeReserveReq.getKidNum());	//购买数量
				hyOrderItem.setNumberOfReturn(0);	//退货数量
				hyOrderItem.setOrder(hyOrder);	//所属订单
				hyOrderItem.setProductId(hyTicketSubscribe.getId());	//认购门票id
				hyOrderItem.setSpecificationId(ticketInbounds.get(0).getId());	//当天的库存id
				hyOrderItem.setPriceId(hyTicketSubscribePrice.getId());	//价格id
				hyOrderItem.setHyOrderCustomers(null);	//顾客为null
				hyOrderItem.setNumberOfReturn(0);
				orderItems.add(hyOrderItem);
			}
			//old
			if(ticketSubscribeReserveReq.getOldNum() > 0) {
				//订单条目
				HyOrderItem hyOrderItem = new HyOrderItem();
				hyOrderItem.setStatus(0);	//0为有效
				hyOrderItem.setStartDate(hyTicketSubscribePrice.getStartDate());	//开始时间
				hyOrderItem.setEndDate(hyTicketSubscribePrice.getEndDate());	//结束时间
				hyOrderItem.setName(hyTicketSubscribe.getSceneName());	//订单条目名称
				hyOrderItem.setType(2);	//认购门票
				hyOrderItem.setPriceType(3);	//价格类型为普通老人价
				hyOrderItem.setJiesuanPrice(totalOldSettlementPrice);	//结算价
				hyOrderItem.setWaimaiPrice(totalOldOutsalePrice);	//外卖价
				hyOrderItem.setNumber(ticketSubscribeReserveReq.getOldNum());	//购买数量
				hyOrderItem.setNumberOfReturn(0);	//退货数量
				hyOrderItem.setOrder(hyOrder);	//所属订单
				hyOrderItem.setProductId(hyTicketSubscribe.getId());	//认购门票id
				hyOrderItem.setSpecificationId(ticketInbounds.get(0).getId());	//当天的库存id
				hyOrderItem.setPriceId(hyTicketSubscribePrice.getId());	//价格id
				hyOrderItem.setHyOrderCustomers(null);	//顾客为null
				hyOrderItem.setNumberOfReturn(0);
				orderItems.add(hyOrderItem);
			}
			
			if(ticketSubscribeReserveReq.getStudentNum() > 0) {
				//订单条目
				HyOrderItem hyOrderItem = new HyOrderItem();
				hyOrderItem.setStatus(0);	//0为有效
				hyOrderItem.setStartDate(hyTicketSubscribePrice.getStartDate());	//开始时间
				hyOrderItem.setEndDate(hyTicketSubscribePrice.getEndDate());	//结束时间
				hyOrderItem.setName(hyTicketSubscribe.getSceneName());	//订单条目名称
				hyOrderItem.setType(2);	//认购门票
				hyOrderItem.setPriceType(2);	//价格类型为null
				hyOrderItem.setJiesuanPrice(totalStudentSettlementPrice);	//结算价
				hyOrderItem.setWaimaiPrice(totalStudentOutsalePrice);	//外卖价
				hyOrderItem.setNumber(ticketSubscribeReserveReq.getStudentNum());	//购买数量
				hyOrderItem.setNumberOfReturn(0);	//退货数量
				hyOrderItem.setOrder(hyOrder);	//所属订单
				hyOrderItem.setProductId(hyTicketSubscribe.getId());	//认购门票id
				hyOrderItem.setSpecificationId(ticketInbounds.get(0).getId());	//当天的库存id
				hyOrderItem.setPriceId(hyTicketSubscribePrice.getId());	//价格id
				hyOrderItem.setHyOrderCustomers(null);	//顾客为null
				hyOrderItem.setNumberOfReturn(0);
				orderItems.add(hyOrderItem);
			}
			
			//获取优惠活动
			//优惠待处理
			//当前优惠判断是，如果有优惠的话，当前产品的promotionId不为空，直接通过这个计算，优惠金额
			//如果没有优惠或者优惠过期，则promotionId为null
			
			if(hyTicketSubscribe.getHyPromotionActivity()!=null ){
				HyPromotionActivity promotionActivity = hyTicketSubscribe.getHyPromotionActivity();
				if(promotionActivity.getState()==1 && promotionActivity.getActivityType()==3){
					//当优惠状态为通过（正常）且优惠类型为签证时
					hyOrder.setDiscountedId(promotionActivity.getId());
					int promotionType = promotionActivity.getPromotionType();
					hyOrder.setDiscountedType(promotionType);
					//0每单满减，1每单打折，2每人减,3无促销
					BigDecimal discountPrice = BigDecimal.ZERO;
					BigDecimal jiusuanMoney = hyOrder.getJiusuanMoney();
					if(promotionType==0){
						if(jiusuanMoney.compareTo(promotionActivity.getManjianPrice1())>0){
							//满足满减条件
							discountPrice = promotionActivity.getManjianPrice2();
						}
					}
					if(promotionType==1){
						discountPrice = promotionActivity.getDazhe().multiply(jiusuanMoney);
					}
					if(promotionType==2){
						discountPrice = promotionActivity.getMeirenjian().multiply(new BigDecimal(hyOrder.getPeople()));
					}
					hyOrder.setDiscountedPrice(discountPrice);	//优惠金额为0
				}
				
			}else{
				hyOrder.setDiscountedType(3);	//无优惠
				hyOrder.setDiscountedId(null);	//无优惠
				hyOrder.setDiscountedPrice(BigDecimal.ZERO);	//优惠金额为0
			}		
			
			
			
			hyOrder.setOrderItems(orderItems);	//订单条目
			
			hyOrder.setGroupId(null);	//没有团
			hyOrder.setSupplier(hyTicketSubscribe.getCreator());	//产品创建者
			
			hyOrder.setIsDivideStatistic(false);	//没有分成统计
			
			hyOrderService.save(hyOrder);
			
			
			
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
			
			json.setSuccess(true);
			json.setMsg("下单成功");
			json.setObj(null);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg(e.getMessage());
			json.setObj(e);
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 由父区域的ID得到全部的子区域
	 * @param id
	 * @return
	 */
	@RequestMapping(value="areacomboxlist/view", method = RequestMethod.GET)
	@ResponseBody
	public Json getSubAreas(Long id) {
		Json j = new Json();
		try {
			HashMap<String, Object> hashMap = new HashMap<>();
			HyArea parent = hyAreaService.find(id);
			List<HashMap<String, Object>> obj = new ArrayList<>();
			if(parent != null && parent.getHyAreas().size() > 0) {
				for (HyArea child : parent.getHyAreas()) {
					if(child.getStatus()) {
						HashMap<String, Object> hm = new HashMap<>();
						hm.put("value", child.getId());
						hm.put("label", child.getName());
						hm.put("isLeaf", child.getHyAreas().size() == 0);
						obj.add(hm);
					}
				}
			}
			hashMap.put("total", parent.getHyAreas().size());
			hashMap.put("data", obj);
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(obj);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}

}
