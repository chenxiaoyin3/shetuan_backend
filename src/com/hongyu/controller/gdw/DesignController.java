package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.Design;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.DesignService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StoreService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/design/")
public class DesignController {
	@Resource(name = "designServiceImpl")
	DesignService designService;
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;

	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@RequestMapping(value = "add")
	@ResponseBody
	public Json add(Design design, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			design.setProposer(hyAdmin);
			Department department = hyAdmin.getDepartment();
			Store store = department.getStore();
			design.setStore(store);
			designService.save(design);
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping(value = "cancle")
	@ResponseBody
	public Json cancle(Long id) {
		Json json = new Json();
		try {
			Design design = designService.find(id);
			int status = design.getStatus();
			if (status < 2) {// 0待估价，1待支付，可取消状态
				design.setStatus(5);// 5已取消
				designService.update(design);
				json.setSuccess(true);
				json.setMsg("取消成功");
			} else {// 2待行政确认，3设计中，4已完成，5已取消
				json.setSuccess(false);
				json.setMsg("该阶段无法取消，请仔细查看");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("取消出错: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("pay")
	@ResponseBody
	public Json pay(Long id, HttpSession session) {
		Json json = new Json();
		try {
			json=designService.pay(id, session);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("支付出错: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("complete")
	@ResponseBody
	public Json complete(Long id) {
		Json json = new Json();
		try {
			Design design = designService.find(id);
			int status = design.getStatus();
			if (status < 3) {// 0待行政估价,1待门店支付，2待行政确认设计
				json.setSuccess(false);
				json.setMsg("该阶段无法完成，请等待");
			}
			if (status == 3) {// 待门店确认完成
				design.setStatus(4);// 4门店确认已完成
				designService.update(design);
				json.setSuccess(true);
				json.setMsg("确认成功");
			} else {// 4已确认，5已取消
				json.setSuccess(false);
				json.setMsg("该阶段无法完成或已完成，请确认");
			}

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("完成出错: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("get/view")
	@ResponseBody
	public Json get(Long id) {
		Json json = new Json();
		try {
			Design design = designService.find(id);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(design);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping(value = "edit")
	@ResponseBody
	public Json edit(Design design) {
		Json json = new Json();
		try {
			designService.update(design, "type", "status", "store", "proposer", "createDate");
			json.setSuccess(true);
			json.setMsg("修改成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("修改失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, Design design, HttpSession session, HttpServletRequest request) {
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
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			/** 将数据按照时间排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.asc("createDate");
			orders.add(order);

			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("proposer", hyAdmins);
			filters.add(filter);

			pageable.setFilters(filters);
			pageable.setOrders(orders);

			Page<Design> page = designService.findPage(pageable, design);
			for (Design tmp : page.getRows()) {
				HyAdmin creator = tmp.getProposer();
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("storeName", tmp.getStore() == null ? "" : tmp.getStore().getStoreName());
				m.put("type", tmp.getType());
				m.put("topic", tmp.getTopic());
				m.put("content", tmp.getContent());
				m.put("price", tmp.getPrice());
				m.put("mail", tmp.getMail());
				m.put("phone", tmp.getPhone());
				m.put("proposer", tmp.getProposer());
				m.put("operator", tmp.getOperator());
				m.put("createDate", tmp.getCreateDate());
				m.put("modifyDate", tmp.getModifyDate());
				m.put("status", tmp.getStatus());
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
			json.setMsg("获取失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
