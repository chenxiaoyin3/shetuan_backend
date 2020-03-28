package com.hongyu.controller.gdw;

import java.math.BigDecimal;
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
import com.hongyu.service.DepartmentService;
import com.hongyu.service.DesignService;
import com.hongyu.service.HyAdminService;

@Controller
@RequestMapping("/admin/designManagement/")
public class DesignManagementController {
	@Resource(name = "designServiceImpl")
	DesignService designService;
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@RequestMapping(value = "evaluate")
	@ResponseBody
	public Json evaluate(Long id, BigDecimal price) {
		Json json = new Json();
		try {
			Design design = designService.find(id);
			int status = design.getStatus();
			if (status == 0) {// 待估价
				design.setPrice(price);
				design.setStatus(1);// 已估价,待门店支付
				designService.update(design);
				json.setSuccess(true);
				json.setMsg("估价成功");
			} else {// 1已估价，待支付，2待行政确认，3设计中，4门店确认已完成，5门店已取消
				json.setSuccess(false);
				json.setMsg("该阶段无法估价或已经估价，请仔细查看");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("估价出错: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("confirm")
	@ResponseBody
	public Json confirm(Long id){
		Json json=new Json();
		try {
			Design design=designService.find(id);
			int status=design.getStatus();
			if(status<2){
				json.setSuccess(false);
				json.setMsg("该阶段无法确认，请仔细查看");
			}else if(status==2){
				design.setStatus(3);
				designService.update(design);
				json.setSuccess(true);
				json.setMsg("确认成功");
			}else{
				json.setSuccess(false);
				json.setMsg("该阶段无法确认或已确认，请仔细看看");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("确认出错: "+e.getMessage());
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

			/** 将数据按照时间排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.asc("createDate");
			orders.add(order);
			pageable.setOrders(orders);

			Page<Design> page = designService.findPage(pageable, design);
			for (Design tmp : page.getRows()) {
				HyAdmin creator = tmp.getProposer();
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("storeName", tmp.getStore()==null?"":tmp.getStore().getStoreName());
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
			json.setMsg("获取失败: "+e.getMessage());
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
			json.setMsg("获取失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
