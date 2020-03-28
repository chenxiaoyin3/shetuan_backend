package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Article;
import com.hongyu.entity.CustomerService;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.CustomerServiceService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/customerService/")
public class CustomerServiceController {
	@Resource(name="customerServiceServiceImpl")
	CustomerServiceService customerServiceService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	@RequestMapping(value="add")
	@ResponseBody
	public Json add(CustomerService customerService,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			customerService.setOperator(hyAdmin);
			customerServiceService.save(customerService);
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping(value="edit")
	@ResponseBody
	public Json edit(CustomerService customerService){
		Json json=new Json();
		try {
			customerServiceService.update(customerService,"createDate","operator");
			json.setSuccess(true);
			json.setMsg("编辑成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping(value="delete")
	@ResponseBody
	public Json delete(Long id){
		Json json=new Json();
		try {
			customerServiceService.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("get/view")
	@ResponseBody
	public Json get(Long id){
		Json json=new Json();
		try {
			CustomerService customerService=customerServiceService.find(id);
			json.setSuccess(true);
			json.setMsg("查看成功");
			json.setObj(customerService);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查看失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json list(Pageable pageable,CustomerService customerService,@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)Date start,@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)Date end,HttpSession session,HttpServletRequest request){
		Json json=new Json();
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
			/** 将数据按照时间排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createDate");
			orders.add(order);
			pageable.setOrders(orders);
			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("operator", hyAdmins);
			filters.add(filter);
			if(start!=null){
				System.out.println("start: "+start);
				filters.add(Filter.ge("startDate", DateUtil.getStartOfDay(start)));
			}
			if(end!=null){
				System.out.println("end: "+end);
				filters.add(Filter.le("startDate", DateUtil.getEndOfDay(end)));
			}
			pageable.setFilters(filters);
			Page<CustomerService> page=customerServiceService.findPage(pageable, customerService);
			for (CustomerService tmp : page.getRows()) {
				HyAdmin creator = tmp.getOperator();
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("lineId", tmp.getLineId());
				m.put("lineName", tmp.getLineName());
				m.put("createDate", tmp.getCreateDate());
				m.put("modifyDate", tmp.getModifyDate());
				m.put("startDate", tmp.getStartDate());
				m.put("revisitDate", tmp.getRevisitDate());
				m.put("operator", tmp.getOperator());
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
			json.setMsg("获取成功");
			json.setObj(hm);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	

}
