package com.hongyu.controller.gsbing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.entity.HyTicketHotel.RefundTypeEnum;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketRefund;
import com.hongyu.entity.MhProductPicture;
import com.hongyu.entity.MhPwRefundRule;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketHotelRoomService;
import com.hongyu.service.HyTicketHotelService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.HyTicketRefundService;
import com.hongyu.service.MhProductPictureService;
import com.hongyu.service.MhPwRefundRuleService;


/**门户完善酒店相关接口*/
@Controller
@RequestMapping("/admin/menhuPerfect/hotel/")
public class MhHotelController {
	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	@Resource(name="hyTicketHotelServiceImpl")
	private HyTicketHotelService hyTicketHotelService;
	
	@Resource(name="hyTicketHotelRoomServiceImpl")
	private HyTicketHotelRoomService hyTicketHotelRoomService;
	
	@Resource(name="hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;
	
	@Resource(name="hyTicketRefundServiceImpl")
	private HyTicketRefundService hyTicketRefundService;
	
	@Resource(name="mhPwRefundRuleServiceImpl")
	private MhPwRefundRuleService mhPwRefundRuleService;
	
	@Resource(name="mhProductPictureServiceImpl")
	private MhProductPictureService mhProductPictureService;
	
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable,Integer mhState,String hotelName,String pn) 
	{
		Json json=new Json();
		try {
			Map<String,Object> obj=new HashMap<String,Object>();
			List<Filter> filters=new ArrayList<>();
			
		    filters.add(Filter.eq("mhState",mhState));
			
			if(hotelName!=null) {
				filters.add(Filter.like("hotelName", hotelName));
			}
			if(pn!=null) {
				filters.add(Filter.eq("pn", pn));
			}
			pageable.setFilters(filters);
			List<Order> orders=new ArrayList<>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
			Page<HyTicketHotel> page=hyTicketHotelService.findPage(pageable);
			List<HashMap<String, Object>> list = new ArrayList<>();
			if(page.getTotal()>0) {
				for(HyTicketHotel hotel:page.getRows()) {
					HashMap<String,Object> map=new HashMap<String,Object>();
					map.put("id", hotel.getId());
					map.put("hotelName",hotel.getHotelName());
					map.put("pn",hotel.getPn());
					map.put("star",hotel.getStar());
					if(hotel.getArea()!=null) {
						map.put("address",hotel.getArea().getFullName()+hotel.getAddress());
					}
					else {
						map.put("address",hotel.getAddress());
					}
					map.put("mhState",hotel.getMhState());
					list.add(map);
				}
			}
			obj.put("rows", list);
		    obj.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    obj.put("pageSize", Integer.valueOf(pageable.getRows()));
		    obj.put("total",Long.valueOf(page.getTotal()));
			json.setSuccess(true);
			json.setObj(obj);
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
			HyTicketHotel hyTicketHotel=hyTicketHotelService.find(id);
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("hotelName",hyTicketHotel.getHotelName());
			map.put("area", hyTicketHotel.getArea().getFullName());	
			map.put("refundType", hyTicketHotel.getRefundType()); //退款类型
			List<Map<String,Object>> list=new ArrayList<>();			
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("type", 1));//1-酒店
			filters.add(Filter.eq("productId", id));
			List<HyTicketRefund> ticketRefunds=hyTicketRefundService.findList(null,filters,null);
			for(HyTicketRefund hyTicketRefund:ticketRefunds) {
				Map<String,Object> obj=new HashMap<>();
				obj.put("startDay", hyTicketRefund.getStartDay());
				obj.put("startTime", hyTicketRefund.getStartTime());
				obj.put("endDay", hyTicketRefund.getEndDay());
				obj.put("endTime", hyTicketRefund.getEndTime());
				obj.put("percentage", hyTicketRefund.getPercentage());
				list.add(obj);
			}
			map.put("hyTicketRefunds",list);
			map.put("address", hyTicketHotel.getAddress());
			map.put("star", hyTicketHotel.getStar());
			map.put("reserveKnow", hyTicketHotel.getReserveKnow());
			map.put("refundReq", hyTicketHotel.getRefundReq());
			
			//以下为门户相关信息			
			map.put("mhOperator", hyTicketHotel.getMhOperator());
			map.put("mhCreateTime", hyTicketHotel.getCreateTime());
			map.put("mhHotelName", hyTicketHotel.getMhHotelName());
			map.put("mhAddress", hyTicketHotel.getMhAddress());
			map.put("mhReserveKnow", hyTicketHotel.getMhRefundReq());
			map.put("mhRefundReq", hyTicketHotel.getMhRefundReq());
			map.put("mhRefundType", hyTicketHotel.getMhRefundType());
			filters.clear();
			filters.add(Filter.eq("type", 1));//1-酒店
			filters.add(Filter.eq("productId", id));
			List<MhPwRefundRule> pwRefundRules=mhPwRefundRuleService.findList(null,filters,null);
			List<Map<String,Object>> mhRefundList=new ArrayList<>();	
			for(MhPwRefundRule refundRule:pwRefundRules) {
				Map<String,Object> obj=new HashMap<>();
				obj.put("startDay", refundRule.getStartDay());
				obj.put("startTime", refundRule.getStartTime());
				obj.put("endDay", refundRule.getEndDay());
				obj.put("endTime", refundRule.getEndTime());
				obj.put("percentage", refundRule.getPercentage());
				mhRefundList.add(obj);
			}
			map.put("mhPwRefundRules", mhRefundList);
			map.put("mhBriefIntroduction", hyTicketHotel.getMhBriefIntroduction());
			map.put("mhIntroduction", hyTicketHotel.getMhIntroduction());
			map.put("mhIsHot", hyTicketHotel.getMhIsHot());
			
			//产品图片相关信息
			filters.clear();
			filters.add(Filter.eq("type", 2));//2-酒店
			filters.add(Filter.eq("productId", hyTicketHotel.getId()));
			List<MhProductPicture> productPictures=mhProductPictureService.findList(null,filters,null);
			List<Map<String,Object>> pictureList=new ArrayList<>();
			for(MhProductPicture picture:productPictures) {
				Map<String,Object> obj=new HashMap<>();
				obj.put("source", picture.getSource());
				obj.put("large", picture.getLarge());
				obj.put("medium", picture.getMedium());
				obj.put("thumbnail", picture.getThumbnail());
				obj.put("isMark", picture.getIsMark());
				pictureList.add(obj);
			}
			map.put("mhProductPictures", pictureList);
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	static class WrapHotel{
		private Long hotelId;
		private Integer tuikuanleixing; //因为退款类型mhRefundType无法接收枚举类型,单弄出来,改名
		private HyTicketHotel hyTicketHotel;
		private List<MhPwRefundRule> mhPwRefundRules=new ArrayList<>();
		private List<MhProductPicture> mhProductPictures=new ArrayList<>();
		public Long getHotelId() {
			return hotelId;
		}
		public void setHotelId(Long hotelId) {
			this.hotelId = hotelId;
		}
		public Integer getTuikuanleixing() {
			return tuikuanleixing;
		}
		public void setTuikuanleixing(Integer tuikuanleixing) {
			this.tuikuanleixing = tuikuanleixing;
		}
		public HyTicketHotel getHyTicketHotel() {
			return hyTicketHotel;
		}
		public void setHyTicketHotel(HyTicketHotel hyTicketHotel) {
			this.hyTicketHotel = hyTicketHotel;
		}
		public List<MhPwRefundRule> getMhPwRefundRules() {
			return mhPwRefundRules;
		}
		public void setMhPwRefundRules(List<MhPwRefundRule> mhPwRefundRules) {
			this.mhPwRefundRules = mhPwRefundRules;
		}
		public List<MhProductPicture> getMhProductPictures() {
			return mhProductPictures;
		}
		public void setMhProductPictures(List<MhProductPicture> mhProductPictures) {
			this.mhProductPictures = mhProductPictures;
		}
	}
	
	@RequestMapping(value="hotelPerfect")
	@ResponseBody
	public Json hotelPerfect(@RequestBody WrapHotel wraphotel,HttpSession session) 
	{
		Json json=new Json();
		try {
			Long hotelId=wraphotel.getHotelId();
			HyTicketHotel hyTicketHotel=hyTicketHotelService.find(hotelId);
			HyTicketHotel hotel=wraphotel.getHyTicketHotel();
			hyTicketHotel.setMhAddress(hotel.getMhAddress());
			hyTicketHotel.setMhBriefIntroduction(hotel.getMhBriefIntroduction());
			hyTicketHotel.setMhIntroduction(hotel.getMhIntroduction());
			hyTicketHotel.setMhHotelName(hotel.getMhHotelName());
			hyTicketHotel.setMhReserveKnow(hotel.getMhReserveKnow());
			hyTicketHotel.setMhRefundReq(hotel.getMhRefundReq());
			hyTicketHotel.setMhIsHot(hotel.getMhIsHot());
			Integer tuikuanleixing=wraphotel.getTuikuanleixing();
			if(tuikuanleixing==0) {
				hyTicketHotel.setMhRefundType(RefundTypeEnum.quane);
			}
			else {
				hyTicketHotel.setMhRefundType(RefundTypeEnum.jieti);
			}
			/**
			 * 获取当前用户
			 */
			if(hyTicketHotel.getMhState()==null || hyTicketHotel.getMhState()==0) {
				hyTicketHotel.setMhCreateTime(new Date());
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				String operatorName=hyAdminService.find(username).getName();
				hyTicketHotel.setMhOperator(operatorName);
			}
			else {
				hyTicketHotel.setMhUpdateTime(new Date());
			}
			hyTicketHotel.setMhState(1); //已完善
			hyTicketHotelService.update(hyTicketHotel);
			
			//退款规则先删掉以前的,再增加最新的
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("type", 1));
			filters.add(Filter.eq("productId", hotelId));
			List<MhPwRefundRule> priRefundRules=mhPwRefundRuleService.findList(null,filters,null);
			//删掉以前的
			for(MhPwRefundRule refundRule:priRefundRules) {
				mhPwRefundRuleService.delete(refundRule);
			}
			
			//增加新的
			List<MhPwRefundRule> refundRules=wraphotel.getMhPwRefundRules();
			for(MhPwRefundRule refund:refundRules) {
				refund.setType(1); //1-酒店
				refund.setProductId(hotelId);
				mhPwRefundRuleService.save(refund);
			}
			
			//产品图片,删掉以前的,增加新的
			filters.clear();
			filters.add(Filter.eq("type", 2)); //2-酒店
			filters.add(Filter.eq("productId", hotelId)); //酒店id
			List<MhProductPicture> priProductPictures=mhProductPictureService.findList(null,filters,null);
			//删掉以前的
			for(MhProductPicture picture:priProductPictures) {
				mhProductPictureService.delete(picture);
			}
			
			//增加新的
			List<MhProductPicture> productPictures=wraphotel.getMhProductPictures();
			for(MhProductPicture picture:productPictures) {
				picture.setType(2); //2-酒店
				picture.setProductId(hotelId);
				mhProductPictureService.save(picture);
			}
			json.setSuccess(true);
			json.setMsg("完善成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("完善失败");
		}
		return json;
	}
	
	@RequestMapping(value="roomList/view")
	@ResponseBody
	public Json roomList(Long hotelId)
	{
		Json json=new Json();
		try {
			HyTicketHotel hyTicketHotel=hyTicketHotelService.find(hotelId);
			List<HyTicketHotelRoom> hotelRooms=new ArrayList<>(hyTicketHotel.getHyTicketHotelRooms());
			List<Map<String, Object>> list = new ArrayList<>();
			for(HyTicketHotelRoom room:hotelRooms) {
				if(room.getStatus()==true) {
					Map<String,Object> roomMap=new HashMap<String,Object>();
					roomMap.put("roomId", room.getId());
					roomMap.put("productId", room.getProductId());
					roomMap.put("productName", room.getProductName());
					roomMap.put("roomType", room.getRoomType());
					roomMap.put("auditStatus", room.getAuditStatus());
					roomMap.put("saleStatus", room.getSaleStatus());
					roomMap.put("mhIsSale", room.getMhIsSale()); //官网是否上线
					list.add(roomMap);
				}
			}
			Collections.sort(list, new Comparator<Map<String,Object>>() {
				@Override
				public int compare(Map<String,Object> o1,Map<String,Object> o2) {
					String pd1 = o1.get("productId").toString();
					String pd2 = o2.get("productId").toString();
					return pd2.compareTo(pd1); 
				}
			});
			json.setSuccess(true);
			json.setObj(list);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="roomDetail/view")
	@ResponseBody
	public Json roomDetail(Long roomId)
	{
		Json json=new Json();
		try {
			HyTicketHotelRoom hotelRoom=hyTicketHotelRoomService.find(roomId);
			List<HyTicketPriceInbound> priceInbounds=new ArrayList<>(hotelRoom.getHyTicketPriceInbounds());
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("productId", hotelRoom.getProductId());
			map.put("productName", hotelRoom.getProductName());
			map.put("roomType", hotelRoom.getRoomType());
			map.put("isWifi", hotelRoom.getIsWifi());
			map.put("isWindow", hotelRoom.getIsWindow());
			map.put("isBathroom", hotelRoom.getIsBathroom());
			map.put("available", hotelRoom.getAvailable());
			map.put("breakfast", hotelRoom.getBreakfast());
			map.put("reserveDays", hotelRoom.getReserveDays());
			map.put("reserveTime",hotelRoom.getReserveTime());
			map.put("priceList", priceInbounds);
			
			//以下是门户相关信息
			map.put("mhProductName",hotelRoom.getMhProductName());
			map.put("mhIsSale", hotelRoom.getMhIsSale());
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	
	@RequestMapping(value="roomPerfect")
	@ResponseBody
	public Json roomPerfect(Long roomId,String mhProductName)
	{
		Json json=new Json();
		try {
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(roomId);
			room.setMhProductName(mhProductName);
			hyTicketHotelRoomService.update(room);
			json.setSuccess(true);
			json.setMsg("完善成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="pricePerfect")
	@ResponseBody
	public Json pricePerfect(Long priceId,BigDecimal mhDisplayPrice,BigDecimal mhSellPrice,BigDecimal mhPrice)
	{
		Json json=new Json();
		try {
			HyTicketPriceInbound price=hyTicketPriceInboundService.find(priceId);
			price.setMhDisplayPrice(mhDisplayPrice);
			price.setMhSellPrice(mhSellPrice);
			price.setMhPrice(mhPrice);
			hyTicketPriceInboundService.update(price);
			json.setSuccess(true);
			json.setMsg("完善成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**门户上线*/
	@RequestMapping(value="online")
	@ResponseBody
	public Json online(Long roomId)
	{
		Json json=new Json();
		try {
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(roomId);
			if(room.getSaleStatus()!=2) {
				json.setSuccess(false);
				json.setMsg("供应商未上线");
				return json;
			}
			List<HyTicketPriceInbound> priceList=new ArrayList<>(room.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound price:priceList) {
				if(price.getMhPrice()==null || price.getMhDisplayPrice()==null || price.getMhSellPrice()==null) {
					json.setSuccess(false);
					json.setMsg("门户相应价格未完善");
					return json;
				}
			}
			room.setMhIsSale(1); //门户上线
			hyTicketHotelRoomService.update(room);
			json.setSuccess(true);
			json.setMsg("上线成功");
		}
		catch(Exception e) {
			json.setSuccess(true);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**门户下线*/
	@RequestMapping(value="offline")
	@ResponseBody
	public Json offline(Long roomId)
	{
		Json json=new Json();
		try {
			HyTicketHotelRoom room=hyTicketHotelRoomService.find(roomId);
			room.setMhIsSale(0); //门户下线
			hyTicketHotelRoomService.update(room);
			json.setSuccess(true);
			json.setMsg("下线成功");
		}
		catch(Exception e) {
			json.setSuccess(true);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	
	
	
	
//	@RequestMapping(value="list/view")
//	@ResponseBody
//	public Json listview(Pageable pageable,Integer state,String hotelName,String pn)
//	{
//		Json json=new Json();
//		try {
//			String[] attrs = new String[]{
//					"id","hotelName","pn","star","address"
//			};
//			StringBuilder totalSb = new StringBuilder("select count(*)");
//			StringBuilder pageSb = new StringBuilder("select h1.ID,h1.hotel_name,h1.pn,h1.star,h1.address");
//			StringBuilder sb = new StringBuilder(" from hy_ticket_hotel h1");
//			if(state==0) {
//				sb.append(" where h1.ID not in (select root_id from mh_hotel)");
//			}
//			else if(state==1) {
//				sb.append(" where h1.ID in (select root_id from mh_hotel)");
//			}
//			else {
//				json.setSuccess(false);
//				json.setMsg("所传完善状态错误!");
//				return json;
//			}
//			if(hotelName!=null) {
//				sb.append(" and h1.hotel_name like '%"+hotelName+"%'");
//			}
//			if(pn!=null) {
//				sb.append(" and h1.pn="+pn);
//			}
//			
//			List totals = hyTicketHotelService.statis(totalSb.append(sb).toString());
//			Integer total = ((BigInteger)totals.get(0)).intValue();
//			
//			sb.append(" order by h1.ID desc");
//			Integer sqlStart = (pageable.getPage()-1)*pageable.getRows();
//			Integer sqlEnd = pageable.getPage()*pageable.getRows();
//			sb.append(" limit "+sqlStart+","+sqlEnd);
//			
//			List<Object[]> objs = hyTicketHotelService.statis(pageSb.append(sb).toString());
//			List<Map<String, Object>> rows = new ArrayList<>();
//			for(Object[] obj : objs) {
//				Map<String, Object> map = ArrayHandler.toMap(attrs, obj);
//				map.put("state", state);
//				rows.add(map);
//			}
//			Page<Map<String, Object>> page = new Page<>(rows,total,pageable);
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(page);
//		}
//		catch(Exception e) {
//			json.setSuccess(false);
//			json.setMsg(e.getMessage());
//		}
//		return json;
//	}
//	
//	/**未完善的详情页,从门店用的酒店表hy_ticket_hotel中取数据*/
//	@RequestMapping(value="notPerfect/detail/view")
//	@ResponseBody
//	public Json notPerfectDetail(Long id)
//	{
//		Json json=new Json();
//		try {
//			HyTicketHotel hyTicketHotel=hyTicketHotelService.find(id);
//			Map<String,Object> map=new HashMap<String,Object>();
//			map.put("hotelName",hyTicketHotel.getHotelName());
//			map.put("area", hyTicketHotel.getArea().getFullName());	
//			map.put("refundType", hyTicketHotel.getRefundType()); //退款类型
//			List<Map<String,Object>> list=new ArrayList<>();			
//			List<Filter> filters=new ArrayList<>();
//			filters.add(Filter.eq("type", 1));//1-酒店
//			filters.add(Filter.eq("productId", id));
//			List<HyTicketRefund> ticketRefunds=hyTicketRefundService.findList(null,filters,null);
//			for(HyTicketRefund hyTicketRefund:ticketRefunds) {
//				Map<String,Object> obj=new HashMap<>();
//				obj.put("startDay", hyTicketRefund.getStartDay());
//				obj.put("startTime", hyTicketRefund.getStartTime());
//				obj.put("endDay", hyTicketRefund.getEndDay());
//				obj.put("endTime", hyTicketRefund.getEndTime());
//				obj.put("percentage", hyTicketRefund.getPercentage());
//				list.add(obj);
//			}
//			map.put("hyTicketRefunds",list);
//			map.put("address", hyTicketHotel.getAddress());
//			map.put("star", hyTicketHotel.getStar());
//			map.put("reserveKnow", hyTicketHotel.getReserveKnow());
//			map.put("refundReq", hyTicketHotel.getRefundReq());
//			map.put("introduction", hyTicketHotel.getIntroduction()); //产品介绍
//			json.setSuccess(true);
//			json.setObj(map);
//			json.setMsg("查询成功");
//		}
//		catch(Exception e) {
//			json.setSuccess(false);
//			json.setMsg(e.getMessage());
//		}
//		return json;
//	}
//	
//	/**已完善详情页*/
//	@RequestMapping(value="perfect/detail/view")
//	@ResponseBody
//	public Json perfectDetail(Long id)
//	{
//		Json json=new Json();
//		try {
//			HyTicketHotel hyTicketHotel=hyTicketHotelService.find(id);
//			Map<String,Object> map=new HashMap<String,Object>();
//			//门店用的酒店信息
//			map.put("storehotelName",hyTicketHotel.getHotelName());
//			map.put("storearea", hyTicketHotel.getArea().getFullName());	
//			map.put("storerefundType", hyTicketHotel.getRefundType()); //退款类型
//			List<Map<String,Object>> list=new ArrayList<>();			
//			List<Filter> filters=new ArrayList<>();
//			filters.add(Filter.eq("type", 1));//1-酒店
//			filters.add(Filter.eq("productId", id));
//			List<HyTicketRefund> ticketRefunds=hyTicketRefundService.findList(null,filters,null);
//			for(HyTicketRefund hyTicketRefund:ticketRefunds) {
//				Map<String,Object> obj=new HashMap<>();
//				obj.put("startDay", hyTicketRefund.getStartDay());
//				obj.put("startTime", hyTicketRefund.getStartTime());
//				obj.put("endDay", hyTicketRefund.getEndDay());
//				obj.put("endTime", hyTicketRefund.getEndTime());
//				obj.put("percentage", hyTicketRefund.getPercentage());
//				list.add(obj);
//			}
//			map.put("storehyTicketRefunds",list);
//			map.put("storeaddress", hyTicketHotel.getAddress());
//			map.put("storestar", hyTicketHotel.getStar());
//			map.put("storereserveKnow", hyTicketHotel.getReserveKnow());
//			map.put("storerefundReq", hyTicketHotel.getRefundReq());
//			filters.clear();
//			filters.add(Filter.eq("rootId", id));
//			List<MhHotel> mhHotels=mhHotelService.findList(null,filters,null);
//			MhHotel mhHotel=mhHotels.get(0);
//			map.put("mhhotelName",mhHotel.getHotelName());
//			map.put("mhreserveKnow", mhHotel.getReserveKnow());
//			map.put("mhrefundReq", mhHotel.getRefundReq());
//			map.put("mhrefundType", mhHotel.getRefundType());
//			filters.clear();
//			filters.add(Filter.eq("type", 1));//1-酒店
//			filters.add(Filter.eq("productId", mhHotel.getId()));
//			List<MhPwRefundRule> mhPwRefundRules=mhPwRefundRuleService.findList(null,filters,null);
//			for(MhPwRefundRule mhPwRefundRule:mhPwRefundRules) {
//				Map<String,Object> obj=new HashMap<>();
//				obj.put("startDay", mhPwRefundRule.getStartDay());
//				obj.put("startTime", mhPwRefundRule.getStartTime());
//				obj.put("endDay", mhPwRefundRule.getEndDay());
//				obj.put("endTime", mhPwRefundRule.getEndTime());
//				obj.put("percentage", mhPwRefundRule.getPercentage());
//				list.add(obj);
//			}
//			map.put("mhPwRefundRules",list);
//			map.put("mhbriefIntroduction", mhHotel.getBriefIntroduction());
//			map.put("mhintroduction", mhHotel.getIntroduction());
//			map.put("mhisHot", mhHotel.getIsHot());
//			map.put("mhoperator", mhHotel.getOperator());
//			map.put("mhcreateTime", mhHotel.getCreateTime());
//			
//			//产品相关图片
//			filters.clear();
//			filters.add(Filter.eq("type", 2));//2-酒店
//			filters.add(Filter.eq("productId", mhHotel.getId()));
//			List<MhProductPicture> productPictures=mhProductPictureService.findList(null,filters,null);
//			for(MhProductPicture picture:productPictures) {
//				Map<String,Object> obj=new HashMap<>();
//				obj.put("source", picture.getSource());
//				obj.put("large", picture.getLarge());
//				obj.put("medium", picture.getMedium());
//				obj.put("thumbnail", picture.getThumbnail());
//				obj.put("isMark", picture.getIsMark());
//				list.add(obj);
//			}
//			json.setSuccess(true);
//			json.setObj(map);
//			json.setMsg("查询成功");
//		}
//		catch(Exception e) {
//			json.setSuccess(false);
//			json.setMsg(e.getMessage());
//		}
//		return json;
//	}
//	
//	static class WrapMhHotel{
//		private Long rootHotelId;
//		private MhHotel mhHotel;
//		private List<MhPwRefundRule> mhPwRefundRules=new ArrayList<>();
//		private List<MhProductPicture> mhProductPictures=new ArrayList<>();
//		public Long getRootHotelId() {
//			return rootHotelId;
//		}
//		public void setRootHotelId(Long rootHotelId) {
//			this.rootHotelId = rootHotelId;
//		}
//		public MhHotel getMhHotel() {
//			return mhHotel;
//		}
//		public void setMhHotel(MhHotel mhHotel) {
//			this.mhHotel = mhHotel;
//		}
//		public List<MhPwRefundRule> getMhPwRefundRules() {
//			return mhPwRefundRules;
//		}
//		public void setMhPwRefundRules(List<MhPwRefundRule> mhPwRefundRules) {
//			this.mhPwRefundRules = mhPwRefundRules;
//		}
//		public List<MhProductPicture> getMhProductPictures() {
//			return mhProductPictures;
//		}
//		public void setMhProductPictures(List<MhProductPicture> mhProductPictures) {
//			this.mhProductPictures = mhProductPictures;
//		}
//	}
//	
//	/**完善产品*/
//	@RequestMapping(value="perfect")
//	@ResponseBody
//	public Json perfect(@RequestBody WrapMhHotel wrapMhHotel,HttpSession session)
//	{
//		Json json=new Json();
//		try {
//			Long rootHotelId=wrapMhHotel.getRootHotelId();
//			List<Filter> filters=new ArrayList<>();
//			filters.add(Filter.eq("rootId",rootHotelId));
//			List<HyTicketHotel> hyTicketHotels=hyTicketHotelService.findList(null,filters,null);
//			filters.clear();
//			if(!hyTicketHotels.isEmpty()) {
//				json.setSuccess(false);
//				json.setMsg("该产品已完善");
//				return json;
//			}
//			/**
//			 * 获取当前用户
//			 */
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			MhHotel mhHotel=wrapMhHotel.getMhHotel();
//			mhHotel.setOperator(username);
//			mhHotel.setCreateTime(new Date());
//			mhHotel.setRootId(rootHotelId);
//			mhHotelService.save(mhHotel);
//			List<MhPwRefundRule> mhPwRefundRules=wrapMhHotel.getMhPwRefundRules();
//			for(MhPwRefundRule refundRule:mhPwRefundRules) {
//				refundRule.setType(1); //1酒店
//				refundRule.setProductId(mhHotel.getId());
//				mhPwRefundRuleService.save(refundRule);
//			}
//			
//			//产品的相关图片
//			List<MhProductPicture> productPictures=wrapMhHotel.getMhProductPictures();
//			for(MhProductPicture picture:productPictures) {
//				picture.setType(2); //2-酒店
//				picture.setProductId(mhHotel.getId());
//				mhProductPictureService.save(picture);
//			}
//			json.setSuccess(true);
//			json.setMsg("完善成功");
//		}
//		catch(Exception e) {
//			json.setSuccess(false);
//			json.setMsg(e.getMessage());
//		}
//		return json;
//	}
//	
//	/**已完善的产品编辑*/
//	@RequestMapping(value="edit")
//	@ResponseBody
//	public Json edit(@RequestBody WrapMhHotel wrapMhHotel)
//	{
//		Json json=new Json();
//		try {
//			Long rootHotelId=wrapMhHotel.getRootHotelId();
//			MhHotel mhHotel=wrapMhHotel.getMhHotel();
//			mhHotel.setCreateTime(new Date());
//			mhHotel.setRootId(rootHotelId);
//			mhHotelService.save(mhHotel);
//			List<MhPwRefundRule> mhPwRefundRules=wrapMhHotel.getMhPwRefundRules();
//			for(MhPwRefundRule refundRule:mhPwRefundRules) {
//				refundRule.setType(1); //1酒店
//				refundRule.setProductId(mhHotel.getId());
//				mhPwRefundRuleService.save(refundRule);
//			}
//			
//			//产品的相关图片
//			List<MhProductPicture> productPictures=wrapMhHotel.getMhProductPictures();
//			for(MhProductPicture picture:productPictures) {
//				picture.setType(2); //2-酒店
//				picture.setProductId(mhHotel.getId());
//				mhProductPictureService.save(picture);
//			}
//			json.setSuccess(true);
//			json.setMsg("编辑成功");
//		
//		}
//		catch(Exception e) {
//			json.setSuccess(false);
//			json.setMsg(e.getMessage());
//		}
//		return json;
//	}
//	
//	/**酒店房间列表*/
//	@RequestMapping(value="roomList/view")
//	@ResponseBody
//	public Json roomList(Pageable pageable,Integer state,Long rootHotelId) 
//	{
//		Json json=new Json();
//		try {
//			String[] attrs = new String[]{
//					"id","productId","productName","roomType","saleStatus"
//			};
//			StringBuilder totalSb = new StringBuilder("select count(*)");
//			StringBuilder pageSb = new StringBuilder("select h1.ID,h1.product_id,h1.product_name,h1.room_type,h1.sale_status");
//			StringBuilder sb = new StringBuilder(" from hy_ticket_hotel_room h1");
//			sb.append(" where hotel="+rootHotelId);
//			if(state==0) {
//				sb.append(" and h1.ID not in (select root_id from mh_hotel_room)");
//			}
//			else if(state==1) {
//				sb.append(" and h1.ID in (select root_id from mh_hotel_room)");
//			}
//			else {
//				json.setSuccess(false);
//				json.setMsg("所传完善状态错误!");
//				return json;
//			}
//			
//			List totals = hyTicketHotelRoomService.statis(totalSb.append(sb).toString());
//			Integer total = ((BigInteger)totals.get(0)).intValue();
//			
//			sb.append(" order by h1.ID desc");
//			Integer sqlStart = (pageable.getPage()-1)*pageable.getRows();
//			Integer sqlEnd = pageable.getPage()*pageable.getRows();
//			sb.append(" limit "+sqlStart+","+sqlEnd);
//			
//			List<Object[]> objs = hyTicketHotelRoomService.statis(pageSb.append(sb).toString());
//			List<Map<String, Object>> rows = new ArrayList<>();
//			for(Object[] obj : objs) {
//				Map<String, Object> map = ArrayHandler.toMap(attrs, obj);
//				map.put("state", state);
//				rows.add(map);
//			}
//			Page<Map<String, Object>> page = new Page<>(rows,total,pageable);
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(page);
//		}
//		catch(Exception e) {
//			json.setSuccess(false);
//			json.setMsg(e.getMessage());
//		}
//		return json;
//	}
//	
//	
//	/**完善酒店房间*/
//	@RequestMapping(value="roomPerfect")
//	@ResponseBody
//	public Json roomPerfect(@RequestBody MhHotelRoom mhHotelRoom,HttpSession session)
//	{
//		Json json=new Json();
//		try {
//			Long rootId=mhHotelRoom.getRootId();
//			List<Filter> filters=new ArrayList<>();
//			filters.add(Filter.eq("rootId", rootId));
//			List<MhHotelRoom> mhHotelRooms=mhHotelRoomService.findList(null,filters,null);
//			if(!mhHotelRooms.isEmpty()) {
//				json.setSuccess(false);
//				json.setMsg("该产品已经完善");
//				return json;
//			}
//			List<MhTicketPrice> mhTicketPrices=new ArrayList<>(mhHotelRoom.getMhTicketPrices());
//			//默认不上线,手动上线
//			mhHotelRoom.setIsSale(false);
//			//找到门店用酒店id
//			Long rootHotelId=hyTicketHotelRoomService.find(rootId).getHyTicketHotel().getId();
//			filters.clear();
//			filters.add(Filter.eq("rootId", rootHotelId));
//			List<MhHotel> mhHotels=mhHotelService.findList(null,filters,null);
//			if(mhHotels.isEmpty()) {
//				json.setSuccess(false);
//				json.setMsg("该酒店尚未完善,请先完善酒店");
//				return json;
//			}
//		    mhHotelRoom.setMhHotel(mhHotels.get(0));
//		    mhHotelRoomService.save(mhHotelRoom);
//		    /**
//			 * 获取当前用户
//			 */
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//		    for(MhTicketPrice price:mhTicketPrices) {
//		    	price.setMhHotelRoom(mhHotelRoom);
//		    	price.setOperator(username);
//		    	price.setCreateTime(new Date());
//		    	mhTicketPriceService.save(price);
//		    }
//		}
//		catch(Exception e) {
//			json.setSuccess(false);
//			json.setMsg(e.getMessage());
//		}
//		return json;
//	}
}
