package com.hongyu.controller.lbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.sym.Name2;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.GroupPlaceholder;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.Store;
import com.hongyu.service.GroupPlaceholderService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;
import com.hongyu.util.SendMessageEMY;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;



@Controller
public class GroupPlaceHolderController {
	@Resource(name = "GroupPlaceholderServiceImpl")
	GroupPlaceholderService groupPlaceholderService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@RequestMapping(value="/admin/linegroup/occupy/list/view")
	@ResponseBody
	public Json GroupPlaceholderList(Pageable pageable,HttpSession session,Long group_id){
		Json json = new Json();
		try {
			List<HashMap<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			HyGroup group = hyGroupService.find(group_id);
			filters.add(Filter.eq("group", group));
//			if(store_type != null) {
//				filters.add(Filter.eq("store_type", store_type));
//			}
			
			
			filters.add(Filter.eq("status", 0));
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<GroupPlaceholder> page = groupPlaceholderService.findPage(pageable);
			
			if(page.getTotal()>0){
				for(GroupPlaceholder groupplaceholder:page.getRows()){
					HashMap<String,Object> comMap=new HashMap<String,Object>();
					HyAdmin creator=groupplaceholder.getCreator();
					//HyGroup hygroup = groupplaceholder.getGroup();
					Store hystore = storeService.find(groupplaceholder.getStore_id());
					comMap.put("id", groupplaceholder.getId());
					if(hystore!=null) {
						comMap.put("storeName", hystore.getStoreName());
					}
					if(creator!=null) {
						comMap.put("creatorName", creator.getName());
					}
					comMap.put("phone", groupplaceholder.getSignup_phone());
					comMap.put("number", groupplaceholder.getNumber());
					comMap.put("status", false);
//					
//					/** 当前用户对本条数据的操作权限 */
//					if(creator.equals(admin)){
//				    	if(co==CheckedOperation.view){
//				    		comMap.put("privilege", "view");
//				    	}
//				    	else{
//				    		comMap.put("privilege", "edit");
//				    	}
//				    }
//				    else{
//				    	if(co==CheckedOperation.edit){
//				    		comMap.put("privilege", "edit");
//				    	}
//				    	else{
//				    		comMap.put("privilege", "view");
//				    	}
//				    }
					list.add(comMap);
				}
			}
			map.put("rows", list);
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));
		    map.put("total",Long.valueOf(page.getTotal()));
			
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
	
	@RequestMapping(value="/admin/linegroup/occupy/cancel")
	@ResponseBody
	public Json restore(Long id) {
		Json j = new Json();
		try{
			GroupPlaceholder groupPlaceholder = groupPlaceholderService.find(id);
			groupPlaceholder.setStatus(true);
			//占位数量减少，剩余数量增加
			HyGroup group = groupPlaceholder.getGroup();
//			if(group.getRemainNumber()==null) {
//				//库存-占位=剩余
//				group.setRemainNumber(group.getStock() - group.getOccupyNumber());
//			}
			group.setOccupyNumber(group.getOccupyNumber() - groupPlaceholder.getNumber());
			group.setStock(group.getStock() + groupPlaceholder.getNumber());
			groupPlaceholder.setGroup(group);
			groupPlaceholderService.update(groupPlaceholder);
			String name = group.getGroupLineName();
			String name2 = group.getStartDay()+"";
			//xx线路      xx出发
			String templateParam = "{\"name\":\"" + name + "\",\"name2\":\"" + name2 +"\"}";
			//门店取消占位
			SendMessageEMY.sendMessage(groupPlaceholder.getCreator().getMobile(), templateParam, 11);
			
			String content = "线路名称为" + name + "起始日期为" + name2 + "的团取消占位";
			List<String> userIds = new ArrayList<>();
			userIds.add(groupPlaceholder.getCreator().getUsername());
			//通知外部供应商
			SendMessageQyWx.sendWxMessage(QyWxConstants.WAI_BU_GONG_YING_SHANG_QYWX_APP_AGENT_ID, userIds, null, content);
			j.setSuccess(true);
			j.setMsg("取消占位成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
}
