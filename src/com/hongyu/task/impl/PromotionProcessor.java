package com.hongyu.task.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyPromotion;
import com.hongyu.entity.HyPromotion.PromotionStatus;
import com.hongyu.entity.HyPromotionActivity;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.entity.HyTicketSceneTicketManagement;
import com.hongyu.entity.HyTicketSubscribe;
import com.hongyu.entity.HyVisa;
import com.hongyu.entity.LinePromotion;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyPromotionActivityService;
import com.hongyu.service.HyPromotionService;
import com.hongyu.service.HyTicketHotelRoomService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.service.HyTicketSubscribeService;
import com.hongyu.service.HyVisaService;
import com.hongyu.service.LinePromotionService;
import com.hongyu.task.Processor;
import com.hongyu.util.Constants;


/** 商贸优惠和线路优惠的定时扫描 **/
@Component("promotionProcessor")
public class PromotionProcessor implements Processor {
	
	@Resource(name="hyPromotionServiceImpl")
	HyPromotionService hyPromotionService;
	 
	@Resource(name = "linePromotionServiceImpl")
	LinePromotionService linePromotionServiceImpl;
	 
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name="hyPromotionActivityServiceImpl")
	HyPromotionActivityService hyPromotionActivityServiceImpl;
	
	@Resource(name="hyTicketHotelRoomServiceImpl")
	HyTicketHotelRoomService hyTicketHotelRoomServiceImpl;
	
	@Resource(name="hyTicketSceneTicketManagementServiceImpl")
	HyTicketSceneTicketManagementService hyTicketSceneTicketManagementServiceImpl;
	
	@Resource(name="hyTicketHotelandsceneServiceImpl")
	HyTicketHotelandsceneService hyTicketHotelandsceneServiceImpl;
	
	@Resource(name="hyTicketSubscribeServiceImpl")
	HyTicketSubscribeService hyTicketSubscribeServiceImpl;
	
	@Resource(name="hyVisaServiceImpl")
	HyVisaService hyVisaServiceImpl;
	  
	@Override
	public void process() {
		// TODO Auto-generated method stub
		try{
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.SECOND, 5);
			Date currentDate = calendar.getTime();
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.lt("promotionEndtime", currentDate));
			filters.add(Filter.eq("status", PromotionStatus.进行中));
			List<HyPromotion> list = this.hyPromotionService.findList(null, filters, null);
			for(HyPromotion ele : list) {
				ele.setStatus(PromotionStatus.已结束);
				hyPromotionService.update(ele);
			}
			List<Filter> filters1 = new ArrayList<>();
			filters1.add(Filter.gt("promotionEndtime", currentDate));
			filters1.add(Filter.lt("promotionStarttime", currentDate));
			filters1.add(Filter.eq("status", PromotionStatus.未开始));
			List<HyPromotion> list1 = this.hyPromotionService.findList(null, filters1, null);
			for(HyPromotion ele : list1) {
				ele.setStatus(PromotionStatus.进行中);
				hyPromotionService.update(ele);
			}
			
			//到期的促销设置为失效
			List<Filter> linePromotionFilters = new ArrayList<>();
			linePromotionFilters.add(Filter.le("endDate", currentDate));
			linePromotionFilters.add(Filter.eq("isCancel", Boolean.FALSE));
			linePromotionFilters.add(Filter.eq("state", Constants.LINE_PROMOTION_STATUS_PASS));
			List<LinePromotion> linePromotions = linePromotionServiceImpl.findList(null, linePromotionFilters, null);
			for (LinePromotion promotion : linePromotions) {
				for (HyGroup group :promotion.getGroups()) {
					group.setIsPromotion(false);
					hyGroupService.update(group);
				}
				promotion.setIsCancel(true);
				promotion.setState(Constants.LINE_PROMOTION_STATUS_EXPIRED);
				linePromotionServiceImpl.update(promotion);
			}
			
//			linePromotionFilters.clear();
//			linePromotionFilters.add(Filter.gt("endDate", currentDate));
//			linePromotionFilters.add(Filter.le("startDate", currentDate));
//			linePromotionFilters.add(Filter.eq("isCancel", Boolean.TRUE));
//			linePromotionFilters.add(Filter.eq("state", Constants.LINE_PROMOTION_STATUS_PASS));
//			List<LinePromotion> linePromotionList = linePromotionServiceImpl.findList(null, linePromotionFilters, null);
//			for (LinePromotion promotion : linePromotionList) {
//				promotion.setIsCancel(false);
//				linePromotionServiceImpl.update(promotion);
//			}
			
			
			List<Filter> lineFilters = new ArrayList<>();
			lineFilters.add(Filter.eq("isPromotion", true));
			List<HyLine> lines = hyLineService.findList(null, lineFilters, null);
			for (HyLine line : lines) {
				List<Filter> groupFilters = new ArrayList<>();
				groupFilters.add(Filter.eq("line", line));
				groupFilters.add(Filter.eq("isPromotion", true));
				List<HyGroup> groups = hyGroupService.findList(null, groupFilters, null);
				if (groups.isEmpty()) {
					line.setIsPromotion(false);
					hyLineService.update(line);
				}
			}
			
			
			List<Filter> promotionActivityFilters = new ArrayList<>();
			promotionActivityFilters.add(Filter.le("endDate", currentDate));
			promotionActivityFilters.add(Filter.eq("state", Constants.PROMOTION_ACTIVITY_STATUS_PASS));
			List<HyPromotionActivity> activities = hyPromotionActivityServiceImpl.findList(null, promotionActivityFilters, null);
			for (HyPromotionActivity promotion : activities) {
				promotion.setState(Constants.PROMOTION_ACTIVITY_STATUS_EXPIRED);
				int activityType = promotion.getActivityType();
				switch (activityType) {
				case 0: {
					for (HyTicketSceneTicketManagement scene : promotion.getTicketScenes()) {
						scene.setHyPromotionActivity(null);
						hyTicketSceneTicketManagementServiceImpl.update(scene);
					}
					
					hyPromotionActivityServiceImpl.update(promotion);
					break;
				}
				case 1: {
					for (HyTicketHotelRoom room : promotion.getRooms()) {
						room.setPromotionActivity(null);
						hyTicketHotelRoomServiceImpl.update(room);
					}
					
					hyPromotionActivityServiceImpl.update(promotion);
					break;
				}
				case 2: {
					for (HyTicketHotelandscene hotelandscene : promotion.getHotelAndScenes()) {
						hotelandscene.setHyPromotionActivity(null);
						hyTicketHotelandsceneServiceImpl.update(hotelandscene);
					}
					
					hyPromotionActivityServiceImpl.update(promotion);
					break;
				}
				case 3: {
					for (HyTicketSubscribe subscribe : promotion.getTicketSubscribes()) {
						subscribe.setHyPromotionActivity(null);
						hyTicketSubscribeServiceImpl.update(subscribe);
					}
					
					hyPromotionActivityServiceImpl.update(promotion);
					break;
				}
				case 4: {
					for (HyVisa visa : promotion.getVisas()) {
						visa.setHyPromotionActivity(null);
						hyVisaServiceImpl.update(visa);
					}
					
					hyPromotionActivityServiceImpl.update(promotion);
					break;
				}
				default: {
					break;
				}
				}
			}
		} catch (Exception e) {
		      e.printStackTrace();
		}
			
	}

}
