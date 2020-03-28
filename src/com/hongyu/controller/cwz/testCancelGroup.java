package com.hongyu.controller.cwz;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.GroupDivide;
import com.hongyu.entity.GroupMember;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroupCancelAudit;
import com.hongyu.entity.HyGroup.GroupStateEnum;
import com.hongyu.service.GroupDivideService;
import com.hongyu.service.GroupMemberService;
import com.hongyu.service.HyGroupCancelAuditService;
import com.hongyu.service.HyGroupService;

@Controller
@RequestMapping("/admin/storeLineOrder/")
public class testCancelGroup {

	@Resource(name = "groupMemberServiceImpl")
	private GroupMemberService groupMemberService;

	@Resource(name = "groupDivideServiceImpl")
	private GroupDivideService groupDivideService;
	
	@Resource(name = "hyGroupCancelAuditServiceImpl")
	HyGroupCancelAuditService hyGroupCancelAuditService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
		
		@RequestMapping(value = "test/cancel/group", method = RequestMethod.POST)
		@ResponseBody
		public Json audit(Long id) {
			Json j = new Json();
			try{
				
				
				HyGroupCancelAudit hyGroupCancelAudit = hyGroupCancelAuditService.find(id);			
				HyGroup group = hyGroupCancelAudit.getHyGroup();
				
				group.setIsCancel(true);
				group.setGroupState(GroupStateEnum.yiquxiao);
				hyGroupService.update(group);
				
				List<Filter> filters1 = new ArrayList<>();
				filters1.add(Filter.eq("group", group));
				List<GroupDivide> groupDivides = groupDivideService.findList(null, filters1, null);
				for(GroupDivide groupDivide : groupDivides) {
					groupDivideService.delete(groupDivide);
				}
				//增加删除另一个表
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("hyGroup", group));
				List<GroupMember> groupMembers = groupMemberService.findList(null, filters2, null);
				for(GroupMember groupMember : groupMembers) {
					groupMemberService.delete(groupMember);
				}
				
				
			}catch (Exception e) {
				j.setSuccess(false);
				j.setMsg(e.getMessage());	
			}
			
			//最后返回空的json
			j.setSuccess(true);
			j.setMsg("更新成功");
			return j;
			
			
		}
		
	

}
