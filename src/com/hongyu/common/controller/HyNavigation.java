package com.hongyu.common.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyAuthority;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAuthorityService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.RoleAuthorityService;
import java.util.Collections;
/**
 * 左侧导航栏和右上的Controller
 * @author guoxinze
 *
 */
@Controller
@RequestMapping("common/project/")
@Transactional
public class HyNavigation {
	
	@Resource(name = "hyRoleServiceImpl")
	private HyRoleService hyRoleService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyRoleAuthorityServiceImpl")
	private RoleAuthorityService roleAuthorityService;
	
	@Resource(name = "hyAuthorityServiceImpl")
	private HyAuthorityService authorityService;
	

	/**
	 * 根据当前用户找到左侧导航栏
	 */
	@RequestMapping(value="menu")
	@ResponseBody
	public Json getNavigations(HttpSession session) {
		Json j = new Json();
		try { 
		HashMap<String, Object> obj = new HashMap<>();
		List<HashMap<String, Object>> lh = new ArrayList<>();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin1 = hyAdminService.find(username);
		
		HyRole role = admin1.getRole();
		if(role == null) {
			j.setMsg("用户角色不存在");
			j.setSuccess(false);
			return j;
		}
		
		/** 管理员显示全部 */
		if (role != null && role.getName().equalsIgnoreCase("admin")) {	
			List<Filter> filters = new ArrayList<>();
			Filter filter = Filter.isNull("hyAuthority");
			Filter filter_1 = Filter.eq("isDisplay", true);
			filters.add(filter);
			filters.add(filter_1);
			
			List<Order> orders = new ArrayList<>();
			Order order = Order.asc("id");
			orders.add(order);
			List<HyAuthority> as = authorityService.findList(null, filters,orders);
			obj.put("menu", as);
		} else { //非管理员
			/** 根据角色找到权限 */
				System.out.println("**********************role is " + role.getName());
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("roles", role));
				List<Order> orders = new ArrayList<>();
				orders.add(Order.asc("authoritys"));
				List<HyRoleAuthority> roleAuthorities = roleAuthorityService.findList(null, filters, orders);
				
				if(roleAuthorities.size() == 0) {
					j.setMsg("用户没有权限");
					j.setSuccess(false);
					return j;
				}
				for(HyRoleAuthority a : roleAuthorities){
					HyAuthority au = a.getAuthoritys();
					
					if(au.getHyAuthority() == null && au.getIsDisplay()){
						LinkedHashMap<String, Object> hm = new LinkedHashMap<>();
						hm.put("name", au.getName());
						hm.put("icon", au.getIcon());
						hm.put("path", au.getUrl());
						hm.put("children", addChildren(au, role));
						lh.add(hm);
					}	
				}
				obj.put("menu", lh);
		}
		
			
			//原来的checkedRange
			List<HashMap<String, Object>> listhashmap = new ArrayList<>();
			
			/** 管理员显示全部 */
			if (role != null && role.getName().equalsIgnoreCase("admin")) {	
				List<HyAuthority> authorities = authorityService.findAll();
				for(HyAuthority authority : authorities) {
					
					if(authority.getIsDisplay()) {
						HashMap<String, Object> hm = new HashMap<>();
						hm.put("fullUrl", authority.getFullUrl());
						hm.put("co", CheckedOperation.edit);
						hm.put("id", authority.getId());
						listhashmap.add(hm);
					}					
				}
				obj.put("checkedRange", listhashmap);
			} else {
				/** 根据角色找到权限 */
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("roles", role));
				List<HyRoleAuthority> roleAuthorities1 = roleAuthorityService.findList(null, filters, null);
				
				for(HyRoleAuthority a : roleAuthorities1){
					HyAuthority au = a.getAuthoritys();
					
					if(au.getIsDisplay()){
						HashMap<String, Object> hm = new HashMap<>();
						hm.put("fullUrl", au.getFullUrl());
						hm.put("co", a.getOperationCheckedNumber());
						hm.put("id", au.getId());
						listhashmap.add(hm);
					}	
				}
				obj.put("checkedRange", listhashmap);
			}
			
			//原来的currentUser
			Map<String, Object> map = new HashMap();
			map.put("name", admin1.getUsername());
			map.put("realName", admin1.getName());
			map.put("avatar", null);
			map.put("userid", null);
			map.put("notifyCount", null);
			map.put("roleName",admin1.getRole().getName());


			obj.put("currentuser", map);
				
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(obj);
		} catch (Exception e) {
			// TODO: handle exception
			j.setSuccess(false);
			j.setMsg("查询失败");
			j.setObj(e.getMessage());
		}
		
		return j;
	}
	
	public List<HashMap<String, Object>> addChildren(HyAuthority parent, HyRole role) {
		List<HashMap<String, Object>> list = new ArrayList();
		if(parent.getHyAuthorities().size() > 0){
			for(HyAuthority child: parent.getHyAuthorities()) {
				LinkedHashMap<String, Object> hm = new LinkedHashMap<String, Object>();
				
				/**
				 * 判断是否有子权限
				 */
				List<Filter> filters = new ArrayList<>();
				Filter filter1 = Filter.eq("roles", role);
				Filter filter2 = Filter.eq("authoritys", child);
				filters.add(filter1);
				filters.add(filter2);
				List<Order> orders = new ArrayList<>();
				orders.add(Order.asc("id"));
				
				List<HyRoleAuthority> ra = roleAuthorityService.findList(null, filters, orders);
				
				if(child.getIsDisplay() && ra.size() > 0 ) {
					hm.put("name", child.getName());
					hm.put("icon", child.getIcon());
					hm.put("path", child.getUrl());
					hm.put("children", addChildren(child, role));
					list.add(hm);
				}
			}
		}
		
		return list;
	}
	/**
	 * 根据当前用户找到左侧导航栏
	 */
//	@RequestMapping(value="currentuser", method = RequestMethod.GET)
//	@ResponseBody
//	public Map getCurrentUser(HttpSession session) {
//		/**
//		 * 获取当前用户
//		 */
//		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		Map<String, Object> map = new HashMap();
//		map.put("name", username);
//		map.put("avatar", null);
//		map.put("userid", null);
//		map.put("notifyCount", null);
//		return map;
//	}

	/**
	 * 返回导航栏对应的权限范围和fullURL
	 */
//	@RequestMapping(value = "checkedRange", method = RequestMethod.GET)
//	@ResponseBody
//	public Json checkedRange(HttpSession session) {
//		Json j = new Json();
//		List<HashMap<String, Object>> lh = new ArrayList<>();
//		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		
//		HyRole role = hyAdminService.find(username).getRole();
//		
//		/** 管理员显示全部 */
//		if (role != null && role.getName().equalsIgnoreCase("admin")) {	
//			List<HyAuthority> authorities = authorityService.findAll();
//			for(HyAuthority authority : authorities) {
//				HashMap<String, Object> hm = new HashMap<>();
//				hm.put("fullUrl", authority.getFullUrl());
//				hm.put("co", CheckedOperation.edit);
//				hm.put("id", authority.getId());
//				lh.add(hm);
//			}
//			j.setSuccess(true);
//			j.setMsg("查询成功");
//			j.setObj(lh);
//			return j;
//		}
//		
//		/** 根据角色找到权限 */
//		List<Filter> filters = new ArrayList<>();
//		filters.add(Filter.eq("roles", role));
//		List<HyRoleAuthority> roleAuthorities = roleAuthorityService.findList(null, filters, null);
//		
//		for(HyRoleAuthority a : roleAuthorities){
//			HyAuthority au = a.getAuthoritys();
//			
//			if(au.getIsDisplay()){
//				HashMap<String, Object> hm = new HashMap();
//				hm.put("fullUrl", au.getFullUrl());
//				hm.put("co", a.getOperationCheckedNumber());
//				hm.put("id", au.getId());
//				lh.add(hm);
//			}	
//		}
//		
//		j.setSuccess(true);
//		j.setMsg("查询成功");
//		j.setObj(lh);
//		
//		return j;
//	}
}
