package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDistributorManagement;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDistributorManagementService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/ticket/distributor/channelManagement/")
public class HyDistributorManagementController {
	@Resource(name="hyDistributorManagementServiceImpl")
	HyDistributorManagementService hyDistributorManagementService;
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(HyDistributorManagement hyDistributorManagement,HttpSession session)
	{
		Json json=new Json();
		try{
			hyDistributorManagement.setStatus(true);
			if(hyDistributorManagement.getSettleType()==1){
				hyDistributorManagement.setPrechargeBalance(new BigDecimal("0"));
			}
			hyDistributorManagement.setCreateTime(new Date());
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			hyDistributorManagement.setCreator(hyAdmin);
			hyDistributorManagementService.save(hyDistributorManagement);
			json.setSuccess(true);
			json.setMsg("添加成功！");
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="list/view", method = RequestMethod.POST)
	@ResponseBody
	public Json listview(Pageable pageable, HyDistributorManagement queryParam,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try{
			Map<String,Object> map=new HashMap<String,Object>();
			List<HashMap<String, Object>> list = new ArrayList<>();
			
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co=(CheckedOperation) request.getAttribute("co");
			
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.in("creator", hyAdmins));
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
			Page<HyDistributorManagement> page=this.hyDistributorManagementService.findPage(pageable, queryParam);
			if(page.getTotal()>0){
			    for(HyDistributorManagement distributorManagement:page.getRows())
			    {
				    HashMap<String,Object> shMap=new HashMap<String,Object>();
				    HyAdmin creator=distributorManagement.getCreator();
				    shMap.put("id",distributorManagement.getId());
				    shMap.put("name",distributorManagement.getName());
				    shMap.put("principal",distributorManagement.getPrincipal());
				    shMap.put("telephone",distributorManagement.getTelephone());
				    shMap.put("address",distributorManagement.getAddress());
				    shMap.put("settleType",distributorManagement.getSettleType());
				    shMap.put("remark",distributorManagement.getRemark());
				    shMap.put("status",distributorManagement.getStatus());
				    if(distributorManagement.getCreator()!=null){
				    	shMap.put("creator", creator.getName());
				    }
				    /** 当前用户对本条数据的操作权限 */
				    if(creator.equals(admin)){
				    	if(co==CheckedOperation.view){
				    		shMap.put("privilege", "view");
				    	}
				    	else{
				    		shMap.put("privilege", "edit");
				    	}
				    }
				    else{
				    	if(co==CheckedOperation.edit){
				    		shMap.put("privilege", "edit");
				    	}
				    	else{
				    		shMap.put("privilege", "view");
				    	}
				    }
				    list.add(shMap);
			    }		    
		    }
			map.put("rows", list);
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));
		    map.put("total",Long.valueOf(page.getTotal()));
		    json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="detail/view", method = RequestMethod.GET)
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try{
		    HyDistributorManagement hyDistributorManagement=hyDistributorManagementService.find(id);
		    json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(hyDistributorManagement);
		   }
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;		
	}
	
	@RequestMapping(value="modify", method = RequestMethod.POST)
	@ResponseBody
	public Json modify(HyDistributorManagement hyDistributorManagement,Long id){
		Json json=new Json();
		try{	
			hyDistributorManagement.setId(id);
			hyDistributorManagement.setModifyTime(new Date());
			hyDistributorManagementService.update(hyDistributorManagement,"status","creator","prechargeBalance",
					"createTime");
			json.setMsg("更新成功！");
			json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="changestatus")
	@ResponseBody
	public Json cancel(Long id){
		Json json=new Json();
		try{
			HyDistributorManagement hyDistributorManagement=hyDistributorManagementService.find(id);
			hyDistributorManagement.setStatus(!hyDistributorManagement.getStatus());
			hyDistributorManagementService.update(hyDistributorManagement);
			json.setSuccess(true);
			json.setMsg("操作成功");
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
}	
