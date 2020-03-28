package com.hongyu.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
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
import com.hongyu.controller.WeBusinessDivideController.WeDivideInfo;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.WeDivideReport;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.service.WeDivideReportService;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/business/weBusinessHy/")
@Transactional(propagation=Propagation.REQUIRED)
public class WeBusinessHyController {
	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;

	@Resource(name = "departmentServiceImpl")
	private DepartmentService departmentService;
	
	
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, WeBusiness weBusiness, HttpSession session, HttpServletRequest request) {
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
			Filter filter2=Filter.eq("type", WeBusiness.hytype);
			filters.add(filter2);

			pageable.setFilters(filters);
			pageable.setOrders(orders);

			/** 找到分页的微商数据 */
			Page<WeBusiness> page = weBusinessService.findPage(pageable, weBusiness);

			for (WeBusiness tmp : page.getRows()) {
				HyAdmin creator = tmp.getOperator();

				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("name", tmp.getName());
				m.put("mobile", tmp.getMobile());
				m.put("address", tmp.getAddress());
				m.put("url", tmp.getUrl());
				m.put("qrcodeUrl", tmp.getQrcodeUrl());
				m.put("isActive", tmp.getIsActive());
				/** 当前用户对本条数据的操作权限 */
				if (creator.equals(admin)) {
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
			json.setSuccess(false);
			json.setMsg("查找失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("modify")
	@ResponseBody
	public Json modify(WeBusiness weBusiness){
		Json json=new Json();
		try {
			weBusinessService.update(weBusiness,"type","storeId"
					,"url","qrcodeUrl","registerTime","deadTime"
					,"isActive","operator","introducer","wechatOpenId"
					,"isLineWechatBusness","lineDivideProportion");
			json.setSuccess(true);
			json.setMsg("编辑成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try {
			WeBusiness weBusiness=weBusinessService.find(id);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(weBusiness);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	

}
