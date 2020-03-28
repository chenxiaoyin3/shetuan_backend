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
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketRefund;
import com.hongyu.entity.MhProductPicture;
import com.hongyu.entity.MhPwRefundRule;
import com.hongyu.entity.HyTicketHotelandscene.RefundTypeEnum;
import com.hongyu.entity.HyTicketHotelandsceneRoom;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketHotelandsceneRoomService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.HyTicketRefundService;
import com.hongyu.service.MhProductPictureService;
import com.hongyu.service.MhPwRefundRuleService;

/**门户完善酒加景相关接口*/
@Controller
@RequestMapping("/admin/menhuPerfect/jiujiajing/")
public class MhJiujiajingController {

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
	@Resource(name="hyTicketHotelandsceneServiceImpl")
	private HyTicketHotelandsceneService hyTicketHotelandsceneService;
	
	@Resource(name="hyTicketHotelandsceneRoomServiceImpl")
	private HyTicketHotelandsceneRoomService hyTicketHotelandsceneRoomService;
	
	@Resource(name="hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;
	
	@Resource(name="hyTicketRefundServiceImpl")
	private HyTicketRefundService hyTicketRefundService;
	
	@Resource(name="mhPwRefundRuleServiceImpl")
	private MhPwRefundRuleService mhPwRefundRuleService;
	
	@Resource(name="mhProductPictureServiceImpl")
	private MhProductPictureService mhProductPictureService;
	
	
	/**酒加景列表页*/
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable,Integer mhState,String productName,String productId) 
	{
		Json json=new Json();
		try {
			Map<String,Object> obj=new HashMap<String,Object>();
			List<Filter> filters=new ArrayList<>();
			
		    filters.add(Filter.eq("mhState",mhState));
			
			if(productName!=null) {
				filters.add(Filter.like("productName", productName));
			}
			if(productId!=null) {
				filters.add(Filter.eq("productId", productId));
			}
			pageable.setFilters(filters);
			List<Order> orders=new ArrayList<>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
			Page<HyTicketHotelandscene> page=hyTicketHotelandsceneService.findPage(pageable);
			List<HashMap<String, Object>> list = new ArrayList<>();
			if(page.getTotal()>0) {
				for(HyTicketHotelandscene hotelandscene:page.getRows()) {
					HashMap<String,Object> map=new HashMap<String,Object>();
					map.put("id", hotelandscene.getId());
					map.put("productName",hotelandscene.getProductName());
					map.put("productId",hotelandscene.getProductId());
					map.put("sceneStar",hotelandscene.getSceneStar());
					map.put("hotelStar", hotelandscene.getHotelStar());
					map.put("mhState",hotelandscene.getMhState());
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
	
	/**酒加景详情页*/
	@RequestMapping(value="detail/view")
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try {
			HyTicketHotelandscene hyTicketHotelandscene=hyTicketHotelandsceneService.find(id);
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("productId",hyTicketHotelandscene.getProductId());
			map.put("productName",hyTicketHotelandscene.getProductName());
			map.put("isRealName", hyTicketHotelandscene.getIsRealName());	
			map.put("days", hyTicketHotelandscene.getDays());
			map.put("priceContain", hyTicketHotelandscene.getPriceContain());
			map.put("reserveDays", hyTicketHotelandscene.getReserveDays());
			map.put("reserveTime", hyTicketHotelandscene.getReserveTime());
			map.put("reserveKnow", hyTicketHotelandscene.getReserveKnow());
			map.put("refundKnow", hyTicketHotelandscene.getRefundKnow());
			map.put("refundType", hyTicketHotelandscene.getRefundType()); //退款类型
			List<Map<String,Object>> list=new ArrayList<>();			
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("type", 2));//2-酒加景
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
			map.put("sceneName", hyTicketHotelandscene.getSceneName());
			if(hyTicketHotelandscene.getSceneArea()!=null) {
				map.put("sceneArea", hyTicketHotelandscene.getSceneArea().getFullName());
			}
			else {
				map.put("sceneArea", null);
			}
			map.put("sceneAddress", hyTicketHotelandscene.getSceneAddress());
			map.put("sceneStar", hyTicketHotelandscene.getSceneStar());
			map.put("sceneOpenTime", hyTicketHotelandscene.getSceneOpenTime());
			map.put("sceneCloseTime", hyTicketHotelandscene.getSceneCloseTime());
			map.put("exchangeTicketAddress", hyTicketHotelandscene.getExchangeTicketAddress());
			map.put("adultsTicketNum", hyTicketHotelandscene.getAdultsTicketNum());
			map.put("childrenTicketNum", hyTicketHotelandscene.getChildrenTicketNum());
			map.put("studentsTicketNum", hyTicketHotelandscene.getStudentsTicketNum());
			map.put("oldTicketNum", hyTicketHotelandscene.getOldTicketNum());
			map.put("hotelName", hyTicketHotelandscene.getHotelName());
			map.put("hotelStar", hyTicketHotelandscene.getHotelStar());
			if(hyTicketHotelandscene.getHotelArea()!=null) {
				map.put("hotelArea", hyTicketHotelandscene.getHotelArea().getFullName());
			}
			else {
				map.put("hotelArea", null);
			}
			map.put("hotelAddress", hyTicketHotelandscene.getHotelAddress());
			
			//以下为门户相关信息			
			map.put("mhOperator", hyTicketHotelandscene.getMhOperator());
			map.put("mhCreateTime", hyTicketHotelandscene.getMhCreateTime());
			map.put("mhProductName", hyTicketHotelandscene.getMhProductName());
			map.put("mhPriceContain", hyTicketHotelandscene.getMhPriceContain());
			map.put("mhReserveKnow", hyTicketHotelandscene.getMhReserveKnow());
			map.put("mhRefundKnow", hyTicketHotelandscene.getMhRefundKnow());
			map.put("mhRefundType", hyTicketHotelandscene.getMhRefundType());
			filters.clear();
			filters.add(Filter.eq("type", 2));//2-酒加景
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
			map.put("mhSceneName",hyTicketHotelandscene.getMhSceneName());
			map.put("mhSceneAddress", hyTicketHotelandscene.getMhSceneAddress());
			map.put("mhHotelName", hyTicketHotelandscene.getMhHotelName());
			map.put("mhHotelAddress", hyTicketHotelandscene.getMhHotelAddress());
			map.put("mhBriefIntroduction", hyTicketHotelandscene.getMhBriefIntroduction());
			map.put("mhIntroduction", hyTicketHotelandscene.getMhIntroduction());
			map.put("mhIsHot", hyTicketHotelandscene.getMhIsHot());
			
			//产品图片相关信息
			filters.clear();
			filters.add(Filter.eq("type", 4));//4-酒加景
			filters.add(Filter.eq("productId", hyTicketHotelandscene.getId()));
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
	
	static class WrapHotelandscene{
		private Long hotelandsceneId;
		private Integer tuikuanleixing; //因为退款类型mhRefundType无法接收枚举类型,单弄出来,改名
		private HyTicketHotelandscene hyTicketHotelandscene;
		private List<MhPwRefundRule> mhPwRefundRules=new ArrayList<>();
		private List<MhProductPicture> mhProductPictures=new ArrayList<>();
		
		public Long getHotelandsceneId() {
			return hotelandsceneId;
		}
		public void setHotelandsceneId(Long hotelandsceneId) {
			this.hotelandsceneId = hotelandsceneId;
		}
		public Integer getTuikuanleixing() {
			return tuikuanleixing;
		}
		public void setTuikuanleixing(Integer tuikuanleixing) {
			this.tuikuanleixing = tuikuanleixing;
		}
		public HyTicketHotelandscene getHyTicketHotelandscene() {
			return hyTicketHotelandscene;
		}
		public void setHyTicketHotelandscene(HyTicketHotelandscene hyTicketHotelandscene) {
			this.hyTicketHotelandscene = hyTicketHotelandscene;
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
	
	/**酒加景完善*/
	@RequestMapping(value="hotelandscenePerfect")
	@ResponseBody
	public Json hotelPerfect(@RequestBody WrapHotelandscene wraphotelandscene,HttpSession session) 
	{
		Json json=new Json();
		try {
			Long hotelandsceneId=wraphotelandscene.getHotelandsceneId();
			HyTicketHotelandscene hyTicketHotelandscene=hyTicketHotelandsceneService.find(hotelandsceneId);
			HyTicketHotelandscene hotelandscene=wraphotelandscene.getHyTicketHotelandscene();
			hyTicketHotelandscene.setMhProductName(hotelandscene.getMhProductName());
			hyTicketHotelandscene.setMhPriceContain(hotelandscene.getMhPriceContain());
			hyTicketHotelandscene.setMhReserveKnow(hotelandscene.getMhReserveKnow());
			hyTicketHotelandscene.setMhRefundKnow(hotelandscene.getMhRefundKnow());		
			hyTicketHotelandscene.setMhSceneName(hotelandscene.getMhSceneName());
			hyTicketHotelandscene.setMhSceneAddress(hotelandscene.getMhSceneAddress());
			hyTicketHotelandscene.setMhHotelName(hotelandscene.getMhHotelName());
			hyTicketHotelandscene.setMhHotelAddress(hotelandscene.getMhHotelAddress());
			hyTicketHotelandscene.setMhBriefIntroduction(hotelandscene.getMhBriefIntroduction());
			hyTicketHotelandscene.setMhIntroduction(hotelandscene.getMhIntroduction());
			hyTicketHotelandscene.setMhIsHot(hotelandscene.getMhIsHot());
			Integer tuikuanleixing=wraphotelandscene.getTuikuanleixing();
			if(tuikuanleixing==0) {
				hyTicketHotelandscene.setMhRefundType(RefundTypeEnum.quane);
			}
			else {
				hyTicketHotelandscene.setMhRefundType(RefundTypeEnum.jieti);
			}
			/**
			 * 获取当前用户
			 */
			if(hyTicketHotelandscene.getMhState()==null || hyTicketHotelandscene.getMhState()==0) {
				hyTicketHotelandscene.setMhCreateTime(new Date());
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				String operatorName=hyAdminService.find(username).getName();
				hyTicketHotelandscene.setMhOperator(operatorName);
			}
			else {
				hyTicketHotelandscene.setMhUpdateTime(new Date());
			}
			hyTicketHotelandscene.setMhState(1); //已完善
			hyTicketHotelandsceneService.update(hyTicketHotelandscene);
			
			//退款规则先删掉以前的,再增加最新的
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("type", 2)); //2-酒加景
			filters.add(Filter.eq("productId", hotelandsceneId));
			List<MhPwRefundRule> priRefundRules=mhPwRefundRuleService.findList(null,filters,null);
			//删掉以前的
			for(MhPwRefundRule refundRule:priRefundRules) {
				mhPwRefundRuleService.delete(refundRule);
			}
			
			//增加新的
			List<MhPwRefundRule> refundRules=wraphotelandscene.getMhPwRefundRules();
			for(MhPwRefundRule refund:refundRules) {
				refund.setType(2); //2-酒加景
				refund.setProductId(hotelandsceneId);
				mhPwRefundRuleService.save(refund);
			}
			
			//产品图片,删掉以前的,增加新的
			filters.clear();
			filters.add(Filter.eq("type", 4)); //4-酒加景
			filters.add(Filter.eq("productId", hotelandsceneId)); //酒加景id
			List<MhProductPicture> priProductPictures=mhProductPictureService.findList(null,filters,null);
			//删掉以前的
			for(MhProductPicture picture:priProductPictures) {
				mhProductPictureService.delete(picture);
			}
			
			//增加新的
			List<MhProductPicture> productPictures=wraphotelandscene.getMhProductPictures();
			for(MhProductPicture picture:productPictures) {
				picture.setType(4); //4-酒加景
				picture.setProductId(hotelandsceneId);
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
	
	/**酒加景房间列表*/
	@RequestMapping(value="roomList/view")
	@ResponseBody
	public Json roomList(Long hotelandsceneId)
	{
		Json json=new Json();
		try {
			HyTicketHotelandscene hyTicketHotelandscene=hyTicketHotelandsceneService.find(hotelandsceneId);
			List<HyTicketHotelandsceneRoom> hotelandsceneRooms=new ArrayList<>(hyTicketHotelandscene.getHyTicketHotelandsceneRooms());
			List<Map<String, Object>> list = new ArrayList<>();
			for(HyTicketHotelandsceneRoom room:hotelandsceneRooms) {
				if(room.getStatus()==true) {
					Map<String,Object> roomMap=new HashMap<String,Object>();
					roomMap.put("roomId", room.getId());
					roomMap.put("roomType", room.getRoomType()); //1-大床房,2-标准间,3-双床房
					roomMap.put("isWifi", room.getIsWifi());
					roomMap.put("isWindow", room.getIsWindow());
					roomMap.put("isBathroom", room.getIsBathroom());
					roomMap.put("available", room.getAvailable());
					roomMap.put("breakfast", room.getBreakfast()); //1-无早餐,2-1人早餐,3-两人早餐
					roomMap.put("auditStatus", room.getAuditStatus());
					roomMap.put("saleStatus", room.getSaleStatus());
					roomMap.put("mhIsSale", room.getMhIsSale()); //官网是否上线
					list.add(roomMap);
				}
			}
			Collections.sort(list, new Comparator<Map<String,Object>>() {
				@Override
				public int compare(Map<String,Object> o1,Map<String,Object> o2) {
					String pd1 = o1.get("roomId").toString();
					String pd2 = o2.get("roomId").toString();
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
	
    /**酒加景房间详情*/
	@RequestMapping(value="roomDetail/view")
	@ResponseBody
	public Json roomDetail(Long roomId)
	{
		Json json=new Json();
		try {
			HyTicketHotelandsceneRoom hotelandsceneRoom=hyTicketHotelandsceneRoomService.find(roomId);
			List<HyTicketPriceInbound> priceInbounds=new ArrayList<>(hotelandsceneRoom.getHyTicketPriceInbounds());
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("roomType", hotelandsceneRoom.getRoomType());
			map.put("isWifi", hotelandsceneRoom.getIsWifi());
			map.put("isWindow", hotelandsceneRoom.getIsWindow());
			map.put("isBathroom", hotelandsceneRoom.getIsBathroom());
			map.put("available", hotelandsceneRoom.getAvailable());
			map.put("breakfast", hotelandsceneRoom.getBreakfast());
			map.put("priceList", priceInbounds);
			
			//以下是门户相关信息
			map.put("mhIsSale", hotelandsceneRoom.getMhIsSale());
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
	
	/**价格完善*/
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
			HyTicketHotelandsceneRoom room=hyTicketHotelandsceneRoomService.find(roomId);
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
			hyTicketHotelandsceneRoomService.update(room);
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
			HyTicketHotelandsceneRoom room=hyTicketHotelandsceneRoomService.find(roomId);
			room.setMhIsSale(0); //门户下线
			hyTicketHotelandsceneRoomService.update(room);
			json.setSuccess(true);
			json.setMsg("下线成功");
		}
		catch(Exception e) {
			json.setSuccess(true);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
