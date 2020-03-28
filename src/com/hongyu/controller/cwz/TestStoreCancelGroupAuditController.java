package com.hongyu.controller.cwz;

import java.math.BigDecimal;
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
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.service.GroupDivideService;
import com.hongyu.service.GroupMemberService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationItemService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;


@Controller
@RequestMapping("/admin/storeLineOrder/")
public class TestStoreCancelGroupAuditController {
	
	//用到的几个Service
	@Resource(name = "groupMemberServiceImpl")
	private GroupMemberService groupMemberService;

	@Resource(name = "groupDivideServiceImpl")
	private GroupDivideService groupDivideService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	@Resource(name = "hyGroupServiceImpl")
	private HyGroupService hyGroupService;
	
	//源代码中没有
	@Resource(name = "hyOrderApplicationItemServiceImpl")
	private HyOrderApplicationItemService hyOrderApplicationItemService;
	
	@Resource(name = "hyOrderCustomerServiceImpl")
	private HyOrderCustomerService hyOrderCustomerService;
	
	@Resource(name = "hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;

	@RequestMapping(value = "test/audit", method = RequestMethod.POST)
	@ResponseBody
	public Json storeCancelGroupAudit(Long id) {
		
		Json j = new Json();
		try{
			
			//一个纯后台的接口 2018-9-11测试成功
			
			//第一步 找到很多的 HyOrderApplication，放在List里面
//			List<Filter> hyOrderApplicationServiceFilter = new ArrayList<>();
//			hyOrderApplicationServiceFilter.add(Filter.ge("status", 3));
//			hyOrderApplicationServiceFilter.add(Filter.eq("type", 0));
//			List<HyOrderApplication> hyOrderApplications = hyOrderApplicationService.findList(null, hyOrderApplicationServiceFilter, null);
			
			//先测试，再看什么时候写这个容错
			HyOrderApplication application = hyOrderApplicationService.find(id);
			
			//第二步 拿到groupID备用
			Long groupID = null;
			HyGroup group = null;
//			if (hyOrderApplications.size() > 0) {
				Long order_id;
				//每次出来只有一个值 按照逻辑来的
//				order_id = hyOrderApplications.get(0).getOrderId();
				
				order_id = application.getOrderId();
				HyOrder order = hyOrderService.find(order_id);
				group = hyGroupService.find(order.getGroupId());
				groupID = group.getId();
//			}
			
			//第三步 想拿到orderCustom这个名字
			
			//第一小步 通过HyOrderApplication找到它的item表
			//数据库中发现，item根本没有外键，这里不做修改（不用外键也行）
			List<HyOrderCustomer> HyOrderCustomerAll = new ArrayList<>();
//			if (hyOrderApplications.size() > 0) {
				HyOrderApplication myHyOrderApplication = null;
				//得到唯一的myHyOrderApplication
//				myHyOrderApplication = hyOrderApplications.get(0);
				myHyOrderApplication = application;
//				List<Filter> hyOrderApplicationItemFilter = new ArrayList<>();
				
				//第二小步 筛选相应ID，结算>0的那些Item 不确定对不对
				//一个orderApplication查出下面多个Item
//				hyOrderApplicationItemFilter.add(Filter.eq("hyOrderApplication", myHyOrderApplication));
//				hyOrderApplicationItemFilter.add(Filter.gt("jiesuanRefund", 0));
				List<HyOrderApplicationItem> hyOrderApplicationItems = new ArrayList<>();
				//去筛选退款金额大于0的
				List<HyOrderApplicationItem> hyOrderApplicationItemsPre = new ArrayList<>();
				
				hyOrderApplicationItemsPre = myHyOrderApplication.getHyOrderApplicationItems();
//				hyOrderApplicationItems = hyOrderApplicationItemService.findList(null, hyOrderApplicationItemFilter, null);
				for(HyOrderApplicationItem items: hyOrderApplicationItemsPre){
					BigDecimal zero = new BigDecimal("0");
					int flag = items.getJiesuanRefund().compareTo(zero);
					if(flag == 1){//等于1是 结果大于0
						hyOrderApplicationItems.add(items);
					}
				}
				
				
				//第三小步 根据HyOrderApplicationItem找到其中的order_customer
				//需要根据orderItem的ID去查orderCustomer 一个Item查出多个orderCustomer
				if(!hyOrderApplicationItems.isEmpty()){
					List<Filter> hyOrderCustomerFilter = new ArrayList<>();
					for(HyOrderApplicationItem myItems: hyOrderApplicationItems){
						//从HyOrderApplicationItem得到orderItem 
						//TODO 顿一下
						HyOrderItem hyOrderItem = hyOrderItemService.find(myItems.getItemId());
						hyOrderCustomerFilter.add(Filter.eq("orderItem", hyOrderItem));
						List<HyOrderCustomer> HyOrderCustomers = new ArrayList<>();
						HyOrderCustomers = hyOrderCustomerService.findList(null, hyOrderCustomerFilter, null);
						//每次找到的所有customer类 都加到大数组里面
						for(HyOrderCustomer hyOrderCustomer: HyOrderCustomers){
							HyOrderCustomerAll.add(hyOrderCustomer);
						}
						hyOrderCustomerFilter.clear();
						
					}
				}
//			}
			
			//第四步 删除member和修改devide表格里面的数据
			//没有元素是true 有元素是false 想让他有元素是true 进循环 加“!”
			List<String> subGroupNumbers = new ArrayList<>();
			if(groupID != null && !HyOrderCustomerAll.isEmpty()){
			
				//第一小步 在group_menber表格里面 用容错项找ABCD什么的 把这一条数据删除
				for(HyOrderCustomer hyOrderCustomerItem: HyOrderCustomerAll){
					List<Filter> groupMemberFilter = new ArrayList<>();
					groupMemberFilter.add(Filter.eq("hyGroup", group));
					//这里需要循环做 不能直接写HyOrderCustomerAll 因为每次的customer都不一样
					groupMemberFilter.add(Filter.eq("hyOrderCustomer", hyOrderCustomerItem));
					List<GroupMember> ourGroupMember = groupMemberService.findList(null, groupMemberFilter, null);
					//删除之前先确定有值
					if(!ourGroupMember.isEmpty()){
						//遍历得出所有的 Item，进而得到ABCD
						for(GroupMember groupMemberItem: ourGroupMember){
							subGroupNumbers.add(groupMemberItem.getSubGroupsn());
						}
						//每次找到的member都不一样 每次删除每次的
						for(GroupMember groupMemberItem: ourGroupMember){
							groupMemberService.delete(groupMemberItem);
						}
					
					}
				}
				
				//第二小步 在group_devide表格里面 用ABCD和group_id去找sub_group_no数量减一
				//遍历刚刚的ABCD数组 如果不是空值
				if(!subGroupNumbers.isEmpty()){
					for(String groupSN: subGroupNumbers){
						List<Filter> groupDevideFilter = new ArrayList<>();
						groupDevideFilter.add(Filter.eq("group", group));
						groupDevideFilter.add(Filter.eq("subGroupsn", groupSN));
						List<GroupDivide> tempGroupDivide = groupDivideService.findList(null, groupDevideFilter, null);
						if(!tempGroupDivide.isEmpty()){
							//遍历把 数量减一 更新回去
							for(GroupDivide groupDivideItem: tempGroupDivide){
								Integer tempNumber = groupDivideItem.getSubGroupNo();
								tempNumber--;
								groupDivideItem.setSubGroupNo(tempNumber);
								//更新回去
								groupDivideService.update(groupDivideItem);
							}	
						}
					}
				}
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
