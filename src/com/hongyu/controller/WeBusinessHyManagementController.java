package com.hongyu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessStore;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.Store;
import com.hongyu.service.BusinessStoreService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StoreService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/business/weBusinessHyManagement")
@Transactional(propagation = Propagation.REQUIRED)
public class WeBusinessHyManagementController {
	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,WeBusiness weBusiness, HttpSession session,
			HttpServletRequest request) {
		Json json = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			/**
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
			/** 将数据按照微商排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("registerTime");
			orders.add(order);

			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("operator", hyAdmins);
			filters.add(filter);
			Filter filter2 = Filter.eq("type", WeBusiness.hytype);
			filters.add(filter2);

//			/** 查出所有符合门店名称的门店id */
//			if(storeName!=null&&!storeName.equals("")){
//				List<Filter> filters2 = new ArrayList<>();
//				filters2.add(Filter.like("storeName", storeName));
//				List<Store> lists = storeService.findList(null, filters2, null);
//				List<Long> longs = new ArrayList<>();
//				for (Store tmp : lists) {
//					longs.add(tmp.getId());
//				}
//				filters.add(Filter.in("storeId", longs));
//			}

			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<WeBusiness> page = weBusinessService.findPage(pageable, weBusiness);
			/** 遍历当前页员工数据，返回前端需要的数据格式 */
			for (WeBusiness tmp : page.getRows()) {
				HyAdmin creater = tmp.getOperator();

				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("name", tmp.getName());
				m.put("mobile", tmp.getMobile());
				m.put("nameOfStore", tmp.getNameOfStore());
				m.put("address", tmp.getAddress());
				m.put("url", tmp.getUrl());
				m.put("qrcodeUrl", tmp.getQrcodeUrl());
				m.put("isActive", tmp.getIsActive());
				if (creater.equals(admin)) {
					if (co == CheckedOperation.view) {
						m.put("privilege", "view");
					} else {
						m.put("privilege", "edit");
					}
				} else {
					if (co == CheckedOperation.edit) {
						m.put("privilege", "edit");
					} else {
						m.put("privilege", "view");
					}
				}
				result.add(m);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("rows", result);
			json.setSuccess(true);
			json.setMsg("查找成功！");
			json.setObj(hm);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找失败");
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			WeBusiness weBusiness = weBusinessService.find(id);
			hm.put("id", weBusiness.getId());
			hm.put("name", weBusiness.getName());
			hm.put("mobile", weBusiness.getMobile());
			hm.put("NameOfStore", weBusiness.getNameOfStore());
			hm.put("address", weBusiness.getAddress());
			hm.put("isActive", weBusiness.getIsActive());
			hm.put("url", weBusiness.getUrl());
			hm.put("qrcodeUrl", weBusiness.getQrcodeUrl());
			hm.put("wechatAccount", weBusiness.getWechatAccount());
			hm.put("wechatOpenId", weBusiness.getWechatOpenId());
			hm.put("shopName",weBusiness.getShopName());
			hm.put("logo", weBusiness.getLogo());
			json.setSuccess(true);
			json.setMsg("查找成功");
			json.setObj(hm);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查找失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("changeStatus")
	@ResponseBody
	public Json changeStatus(Long id) {
		Json json = new Json();
		try {
			WeBusiness weBusiness = weBusinessService.find(id);
			weBusiness.setIsActive(weBusiness.getIsActive() ? false : true);
			weBusinessService.update(weBusiness);
			json.setSuccess(true);
			json.setMsg("更改成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("更烦失败");
			e.printStackTrace();
		}
		return json;
	}

}
