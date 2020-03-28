package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.annotations.Check;
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
import com.hongyu.entity.Expose;
import com.hongyu.entity.ExposeReply;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.ExposeReplyService;
import com.hongyu.service.ExposeService;
import com.hongyu.service.HyAdminService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/expose/")
public class ExposeController {
	@Resource(name="exposeServiceImpl")
	ExposeService exposeService;
	
	@Resource(name="exposeReplyServiceImpl")
	ExposeReplyService exposeReplyService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	
	@RequestMapping(value="addExpose")
	@ResponseBody
	public Json addExpose(Expose expose,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			expose.setProposer(hyAdmin);
			expose.setDepartment(hyAdmin.getDepartment());
			exposeService.save(expose);
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping(value="addExposeReply")
	@ResponseBody
	public Json addExposeReply(ExposeReply exposeReply,Long exposeId,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			Expose expose=exposeService.find(exposeId);
			HyAdmin operator=expose.getOperator();
			if(operator==null){
				if(checkOperation(username)){
					exposeReply.setExpose(expose);
					exposeReply.setOperator(hyAdmin);
					exposeReplyService.save(exposeReply);
					json.setSuccess(true);
					json.setMsg("添加成功");
				}else{
					json.setSuccess(false);
					json.setMsg("添加失败，本人没有权限回复");
				}
			}else if(operator.getUsername()!=username){
				json.setSuccess(false);
				json.setMsg("本曝光已处理，只能由 "+operator.getName()+" 继续处理");
			}else{
				exposeReply.setExpose(expose);
				exposeReply.setOperator(hyAdmin);
				exposeReplyService.save(exposeReply);
				json.setSuccess(true);
				json.setMsg("添加成功");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("checkOperation")
	@ResponseBody
	public Json checkOperation(HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			if(checkOperation(username)){
				json.setSuccess(true);
				json.setMsg("可评论");
			}else{
				json.setSuccess(false);
				json.setMsg("不可评论");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("检查失败： "+e.getMessage());
			// TODO: handle exception
		}
		return json;
	}
	private boolean checkOperation(String username) {
		// TODO Auto-generated method stub
		if(username==null)return false;
		HyAdmin hyAdmin=hyAdminService.find(username);
		Department department=hyAdmin.getDepartment();
		if(department==null)return false;
		String departmentName=department.getName();
		if(departmentName.indexOf("连锁发展")>-1
				||departmentName.indexOf("品控")>-1
				||departmentName.indexOf("行政")>-1){
			return true;
		}
		return false;
	}

	@RequestMapping("get/view")
	@ResponseBody
	public Json get(Long id){
		Json json=new Json();
		try {
			Expose expose=exposeService.find(id);
			json.setSuccess(true);
			json.setMsg("查找成功");
			json.setObj(expose);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查找失败: "+e.getMessage());
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
			for(Iterator<ExposeReply> iterator=exposeService.find(id).getExposeReplies().iterator();iterator.hasNext();){
				ExposeReply exposeReply=iterator.next();
				if (exposeReply==null) {
					iterator.remove();
				}else {
					exposeReplyService.delete(exposeReply);
				}
			}
			exposeService.delete(id);
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
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,Expose expose,HttpSession session,HttpServletRequest request){
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

			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("proposer", hyAdmins);
			filters.add(filter);
			
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<Expose> page=exposeService.findPage(pageable, expose);
			for(Expose tmp:page.getRows()){
				HyAdmin creator=tmp.getProposer();
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("exposedPerson", tmp.getExposedPerson());
				m.put("content", tmp.getContent());
				m.put("proposer", tmp.getProposer());
				m.put("operator", tmp.getOperator());
				m.put("status", tmp.getStatus());
				m.put("phone", tmp.getPhone());
				m.put("department", tmp.getDepartment());
				m.put("createDate", tmp.getCreateDate());
				m.put("modifyDate", tmp.getModifyDate());
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
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
