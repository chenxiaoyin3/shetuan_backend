package com.hongyu.controller.lbc;


import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.GroupDivide;
import com.hongyu.entity.GroupMember;
import com.hongyu.entity.GroupPlaceholder;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.Store;
import com.hongyu.service.GroupDivideService;
import com.hongyu.service.GroupMemberService;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.StoreService;
import com.hongyu.wrapper.lbc.GroupDivides;
import com.hongyu.wrapper.lbc.GroupMemberListWrapper;


@Controller
@RequestMapping("/admin/linegroup/member")
public class GroupMemberController {
	
	@Resource(name = "groupMemberServiceImpl")
	private GroupMemberService groupMemberService;
	
	@Resource(name = "groupDivideServiceImpl")
	private GroupDivideService groupDivideService;
	
	@Resource(name = "hyGroupServiceImpl")
	private HyGroupService hyGroupService;
	
	@Resource(name = "guideServiceImpl")
	private GuideService guideService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	private GuideAssignmentService guideAssignmentService;
	
	@Resource(name = "hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	@Resource(name = "hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;
	
	@Resource(name = "hyOrderCustomerServiceImpl")
	private HyOrderCustomerService hyOrderCustomerService;
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	
	
	@RequestMapping(value="/list/view")
	@ResponseBody
	public Json GroupMemberList(HttpSession session,Long group_id){
		Json json = new Json();
		try {
			List<HashMap<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			HyGroup group = hyGroupService.find(group_id);
			filters.add(Filter.eq("hyGroup", group));		
			//filters.add(Filter.eq("status", 0));
			List<Order> orders = new ArrayList<Order>();
			
			
			//找到所有的groupmember 
			List<GroupMember> groupMemberList = groupMemberService.findList(null, filters, null);
			int total = 0;
			
			long lastOrderItemId = -1;
			long lastOrderId = -1;
			for(GroupMember groupMember : groupMemberList){
				
				HyOrder hyOrder = groupMember.getHyOrder();
				if(hyOrder == null) {
					continue;
				}
				
				HashMap<String,Object> comMap = new HashMap<String,Object>();
				comMap.put("order_id",hyOrder.getId());
				comMap.put("departure",hyOrder.getDeparture());
				Store store = storeService.find(hyOrder.getStoreId());
				if(store!=null) {
					comMap.put("source", store.getStoreName());
				}
				if(hyOrder.getOperator()!=null) {
					comMap.put("operator_name", hyOrder.getOperator().getName());
					comMap.put("operator_mobile", hyOrder.getOperator().getMobile());
				}
				
				HyOrderCustomer hyOrderCustomer = groupMember.getHyOrderCustomer();
				HyOrderItem hyOrderItem = groupMember.getHyOrderItem();
				//comMap.put("orderJiesuanMoney", hyOrder.getJiusuanMoney());
				//comMap.put("orderJiesuanMoney1", hyOrder.getJiesuanMoney1());
				comMap.put("id", groupMember.getId());
				
//				if(hyOrder.getId().equals(lastOrderId)) {
//					comMap.put("orderNumber", "");
//					comMap.put("orderJiesuanMoney", 0);
//					comMap.put("orderJiesuanMoneyWithoutInsurance", 0);
//				}
//				else {
				comMap.put("orderNumber", hyOrder.getOrderNumber());
				comMap.put("orderJiesuanMoney", hyOrder.getJiusuanMoney());
				comMap.put("orderJiesuanMoneyWithoutInsurance", hyOrder.getJiesuanMoney1());
//				}
				//总结算价（算了保险）
				
				lastOrderId = hyOrder.getId();
				
				if(hyOrderCustomer!=null) {
					comMap.put("name", hyOrderCustomer.getName());
					comMap.put("gender", hyOrderCustomer.getGender());
					comMap.put("type", hyOrderCustomer.getType());
					comMap.put("certificatetType", hyOrderCustomer.getCertificateType());
					comMap.put("certificatet", hyOrderCustomer.getCertificate());
					comMap.put("phone",hyOrderCustomer.getPhone());
					//comMap.put("settlementPrice", hyOrderCustomer.getSettlementPrice());
				}
				if(hyOrderItem != null) {
					//是特殊价格
					if(hyOrderItem.getPriceType() == 4){
						comMap.put("isSpecialPrice", 1);
					}
					else {
						comMap.put("isSpecialPrice", 0);
					}
					//和上一个相等 那么传0，否则传实际价格
					if(hyOrderItem.getId().equals(lastOrderItemId)) {
						comMap.put("settlementPrice", 0);
					}
					else {
						comMap.put("settlementPrice", hyOrderItem.getJiesuanPrice());
						lastOrderItemId = hyOrderItem.getId();
					}
					
				}
				
				comMap.put("subGroupsn", groupMember.getSubGroupsn());
				list.add(comMap);		
			}	

			map.put("rows", list);
		    map.put("total",groupMemberList.size());
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		return json;
	}
	
	@RequestMapping(value="/subList/view")
	@ResponseBody
	public Json GroupDivideInfomation(HttpSession session,Long group_id){
		Json json = new Json();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Filter> filters = new ArrayList<Filter>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		try {
			HyGroup group = hyGroupService.find(group_id);
			filters.add(Filter.eq("group", group));
			List<GroupDivide> groupDivides = groupDivideService.findList(null, filters, null);
			for(GroupDivide groupDivide : groupDivides) {
				HashMap<String,Object> comMap=new HashMap<String,Object>();
				comMap.put("id", groupDivide.getId());
				comMap.put("group_id", group_id);
				comMap.put("subGroupsn", groupDivide.getSubGroupsn());
				comMap.put("subGroupNo", groupDivide.getSubGroupNo());
				//comMap.put("guide", groupDivide.getGuide());
				if(groupDivide.getGuide()!=null) {
					HashMap<String,Object> guideMap = new HashMap<String,Object>();
					guideMap.put("guide_id", groupDivide.getGuide().getId());
					guideMap.put("guide_name", groupDivide.getGuide().getName());
					comMap.put("guide",guideMap);
				}
				else {
					comMap.put("guide",null);
				}
				list.add(comMap);
			}
			
			//map.put("groupDivide", list);	
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(list);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		return json;
		
	}
	
	@RequestMapping(value="/guideList/view")
	@ResponseBody
	public Json GuideList(HttpSession session,Long group_id){
		Json json = new Json();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Filter> filters = new ArrayList<Filter>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		try {
			//HyGroup group = hyGroupService.find(group_id);
			filters.add(Filter.eq("groupId", group_id));
			filters.add(Filter.eq("serviceType", 0));
			//状态为正常的
			filters.add(Filter.eq("status", 1));
			List<GuideAssignment> guideAssignments = guideAssignmentService.findList(null, filters, null);
			for(GuideAssignment guideAssignment : guideAssignments) {
				HashMap<String,Object> comMap=new HashMap<String,Object>();
				comMap.put("guide_id", guideAssignment.getGuideId());
				comMap.put("guide_name", guideService.find(guideAssignment.getGuideId()).getName());
				list.add(comMap);
			}
			//map.put("guideList", list);
			
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(list);
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping(value="/changeSubGuide")
	@ResponseBody
	public Json ChangeSubGuide(@RequestBody GroupDivides groupDivides) {
		Json j = new Json();
		try{
			for(GroupDivide groupDivide : groupDivides.getGroupDivides()) {
				GroupDivide groupDivide1 = groupDivideService.find(groupDivide.getId());
				groupDivide1.setGuide(guideService.find(groupDivide.getGuide().getId()));
				groupDivideService.update(groupDivide1);
			}
			
			j.setSuccess(true);
			j.setMsg("更新导游成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="/changeSub")
	@ResponseBody
	public Json ChangeSub(@RequestBody GroupMemberListWrapper groupMemberListWrapper) {
		Json j = new Json();
		List<Filter> filters = new ArrayList<Filter>();
		try{
			//更新memberlist中所有groupmember的分团号
			//26个字母，0-25
			int[] num = new int[26];
			//初始化数组
			for(int i = 0; i < 26; i++){
				num[i] = 0;
			}
			int m = 0;
			int length = groupMemberListWrapper.getGroupMembers().size();
			
			long last_order_id = -1;
			String last_subgroup_sn = "";
			
			//得到hygroup 
			HyGroup group = hyGroupService.find(groupMemberListWrapper.getGroup_id());
			//找到hygroup下所有的groupmember
			filters.add(Filter.eq("hyGroup", group));
//			List<GroupMember> groupMembers1 = groupMemberService.findList(null,filters,null);
			
//			//计算分了几个团 初始化每个团的人数
//			for(GroupMember groupMember : groupMembers1) {
//				num[groupMember.getSubGroupsn().toCharArray()[0] - 'A'] ++;
//			}
			
//			int subCount1 = 0;
//			for(int i = 0; i < 26; i++) {
//				if(num[i] > 0) {
//					subCount1 ++;
//				}
//			}
			
//			//分团数减少
//			if(subCount1 < groupMemberListWrapper.getSubGroupNum()) {
//				//多出来的团号改成1
//				
//				
//				
//			}
			
			//更改的groupmember
			
			for(GroupMember groupMember : groupMemberListWrapper.getGroupMembers()) {
				GroupMember groupMember1 = groupMemberService.find(groupMember.getId());
				
				if(groupMember1.getHyOrder().getId() == last_order_id) {
					//原分团号人数-1 新分团号人数+1
//					num[groupMember1.getSubGroupsn().toCharArray()[0] - 'A'] -= 1;
					num[last_subgroup_sn.toCharArray()[0] - 'A'] += 1;
					groupMember1.setSubGroupsn(last_subgroup_sn);
					groupMemberService.update(groupMember1);
					continue;
				}
				
				//相同group 相同order的都设置成一个团号
				filters.add(Filter.eq("hyOrder", groupMember1.getHyOrder()));
//				List<GroupMember> groupMembers2 = groupMemberService.findList(null,filters,null);
//				
//				for(GroupMember groupMember2 : groupMembers2) {
//					//原分团号人数-1 新分团号人数+1
//					num[groupMember2.getSubGroupsn().toCharArray()[0] - 'A'] -= 1;
//					num[groupMember.getSubGroupsn().toCharArray()[0] - 'A'] += 1;
//					groupMember2.setSubGroupsn(groupMember.getSubGroupsn());
//					groupMemberService.update(groupMember2);
//				}

				groupMember1.setSubGroupsn(groupMember.getSubGroupsn());
				last_subgroup_sn = groupMember.getSubGroupsn();
				num[groupMember1.getSubGroupsn().toCharArray()[0] - 'A'] += 1;
				groupMemberService.update(groupMember1);
				
				HyOrder order = groupMember1.getHyOrder();
				last_order_id = order.getId();
			}

			
//			//计算分了几个团
//			int subCount = 0;
//			for(int i = 0; i < 26; i++) {
//				if(num[i] > 0) {
//					subCount ++;
//				}
//			}
			
			List<Filter> filters3 = new ArrayList<Filter>();
			filters3.add(Filter.eq("group", group));
			
			List<GroupDivide> groupDivides = groupDivideService.findList(null, filters3, null);
			for(GroupDivide groupDivide : groupDivides) {
				groupDivideService.delete(groupDivide);
			}
			//插入新的分团记录 计算groupmember中不同分团的人数各有多少 放入subGroupNo中
			for(int i = 0; i < groupMemberListWrapper.getSubGroupNum(); i++) {
				GroupDivide groupDivide = new GroupDivide();
				groupDivide.setGroup(group);
				groupDivide.setSubGroupsn(""+(char)('A'+i));
				groupDivide.setSubGroupNo(num[i]);
				//暂时不加入导游
				groupDivide.setGuide(null);
				groupDivideService.save(groupDivide);
			}

			j.setSuccess(true);
			j.setMsg("更新分团信息成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
