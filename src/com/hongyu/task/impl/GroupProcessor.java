package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroup.GroupStateEnum;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyLine.IsSaleEnum;
import com.hongyu.entity.MhLine;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.MhLineService;
import com.hongyu.task.Processor;
/**
 * 改变团状态的定时器
 * @author guoxinze
 *
 */
@Component("groupProcessor")
public class GroupProcessor implements Processor {
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "mhLineServiceImpl")
	MhLineService mhLineService;
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		try{
			List<Filter> filters = new ArrayList<>();
			List<HyGroup> hyGroups = null;
			filters.add(Filter.eq("groupState", GroupStateEnum.daichutuan));
			hyGroups = hyGroupService.findList(null, filters, null);
			
			//如果团的发团日期在当前时期以前，就设置为行程中
			for(HyGroup group : hyGroups) {
				if(group.getStartDay().before(new Date())) {
					group.setGroupState(GroupStateEnum.xingchengzhong);
					hyGroupService.update(group);
				}
			}
			
			filters.clear();
			filters.add(Filter.eq("groupState", GroupStateEnum.xingchengzhong));
			hyGroups = hyGroupService.findList(null, filters, null);
			
			for(HyGroup group : hyGroups) {
				if(group.getEndDay().before(new Date())) { //如果团已结束 置为已结束
					group.setGroupState(GroupStateEnum.yijieshu);
					hyGroupService.update(group);
				}
			}
			
			//根据线路的最新团期设置下线
			filters.clear();
			filters.add(Filter.eq("isSale", IsSaleEnum.yishang));
			List<HyLine> lines = hyLineService.findList(null, filters, null);
			
			for(HyLine line : lines) {
				if(line.getLatestGroup().before(new Date())) { //如果最新团期还在当前时间以前 设置产品下线
					line.setIsSale(IsSaleEnum.yixia);
					hyLineService.update(line); //更新线路状态
					
				}
				BigDecimal lineLowestPrice=hyLineService.getLineLowestPrice(line); //获取线路所有团的最低价格
				//更新线路的最低价格
				line.setLowestPrice(lineLowestPrice);
				hyLineService.update(line);
				
				/**add by liyang 20190125*/
				/**如果当前线路在官网销售*/
				if(line.getIsGuanwang()!=null && line.getIsGuanwang()){
					MhLine mhLine = line.getMhLine();
					/**如果所有的团都过期了，那就将线路下线*/
					if(line.getLatestGroup().before(new Date())){
						mhLine.setIsSale(IsSaleEnum.yixia);
						mhLineService.update(mhLine);
					}
					/**更新线路的最低价格*/
					BigDecimal mhLineBottomPrice = mhLineService.getMhLineLowestPrice(line);
					mhLine.setBottomPrice(mhLineBottomPrice);
					mhLineService.update(mhLine);
				}
			}
			
		} catch (Exception e) {
		      e.printStackTrace();
		}
	}

}
