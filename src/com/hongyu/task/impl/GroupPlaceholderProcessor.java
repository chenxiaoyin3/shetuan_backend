package com.hongyu.task.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.GroupPlaceholder;
import com.hongyu.entity.HyGroup;
import com.hongyu.service.GroupPlaceholderService;
import com.hongyu.service.HyGroupService;
import com.hongyu.task.Processor;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.hongyu.util.SendMessageEMY;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

@Component("groupPlaceholderProcessor")
public class GroupPlaceholderProcessor implements Processor{

	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "GroupPlaceholderServiceImpl")
	GroupPlaceholderService groupPlaceholderService;
	@Override
	public void process() {
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("status", false));
		
		Integer valid = -12;
		Date validDate = DateUtil.getDateAfterSpecifiedHours(new Date(), valid);
		
		filters.add(Filter.le("createTime", validDate));
		
		List<GroupPlaceholder> groupPlaceholders = groupPlaceholderService.findList(null, filters, null);
		for(GroupPlaceholder tmp:groupPlaceholders){
			HyGroup hyGroup = tmp.getGroup();
			if(hyGroup == null){
				continue;
			}
			synchronized (hyGroup) {
				hyGroup.setStock(hyGroup.getStock()+tmp.getNumber());
				hyGroup.setOccupyNumber(hyGroup.getOccupyNumber()+tmp.getNumber());
				hyGroupService.update(hyGroup);
				tmp.setStatus(true);
				groupPlaceholderService.update(tmp);
			}
			String phone = tmp.getCreator().getMobile();
			String name = tmp.getGroup().getGroupLineName();
			String name2 = tmp.getGroup().getStartDay()+"";
			String params = "{\"name\":\""+name+"\",\"name2\":\""+name2+"\"}";
			SendMessageEMY.sendMessage(phone,params,13);
			
			/**发送企业微信**/
			int agentId = Constants.AGENTID_STORE;
			List<String> userIds = new ArrayList<>();
			userIds.add(tmp.getCreator().getUsername());
			String messageContent = "您的 "+name+" 线路， "+name2+" 出发的占位已超时，系统已取消，请知晓。";
			SendMessageQyWx.sendWxMessage(agentId, userIds, null, messageContent);
		}
	}

}
