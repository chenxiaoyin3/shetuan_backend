package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Article;
import com.hongyu.entity.BankList;
import com.hongyu.entity.HyAddedServiceSupplier;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BankListService;
import com.hongyu.service.HyAddedServiceSupplierService;
import com.hongyu.service.HyAdminService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/hyAddedServiceSupplier/")
public class HyAddedServiceSupplierController {
	@Resource(name="hyAddedServiceSupplierServiceImpl")
	HyAddedServiceSupplierService hyAddedServiceSupplierService;
	
	@Resource(name="bankListServiceImpl")
	BankListService bankListService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@RequestMapping("add")
	@ResponseBody
	public Json add(@RequestBody HyAddedServiceSupplier hyAddedServiceSupplier,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			json=hyAddedServiceSupplierService.addSupplier(hyAddedServiceSupplier,hyAdmin);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("modify")
	@ResponseBody
	public Json modify(@RequestBody HyAddedServiceSupplier hyAddedServiceSupplier){
		Json json=new Json();
		try {
			json=hyAddedServiceSupplierService.editSuppier(hyAddedServiceSupplier);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("delete")
	@ResponseBody
	public Json delete(Long id){
		Json json=new Json();
		try {
//			HyAddedServiceSupplier addedServiceSupplier=hyAddedServiceSupplierService.find(id);
			
//			bankListService.delete(addedServiceSupplier.getBankList());
			hyAddedServiceSupplierService.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败");
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
			HyAddedServiceSupplier addedServiceSupplier=hyAddedServiceSupplierService.find(id);
			json.setMsg("获取成功");
			json.setObj(addedServiceSupplier);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,HyAddedServiceSupplier hyAddedServiceSupplier,HttpSession session,HttpServletRequest request){
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
			Order order = Order.desc("createtime");
			orders.add(order);

			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("operator", hyAdmins);
			filters.add(filter);

			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<HyAddedServiceSupplier> page=hyAddedServiceSupplierService.findPage(pageable, hyAddedServiceSupplier);
			for (HyAddedServiceSupplier tmp : page.getRows()) {
				HyAdmin creator = tmp.getOperator();
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("name", tmp.getName());
				m.put("contact", tmp.getContact());
				m.put("phone", tmp.getPhone());
				m.put("createtime", tmp.getCreatetime());
				m.put("modifytime", tmp.getModifytime());
				m.put("operator", tmp.getOperator());
				m.put("bankList", tmp.getBankList());
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
