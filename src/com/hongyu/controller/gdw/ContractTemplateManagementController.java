package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
import com.hongyu.entity.ContractTemplate;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.ContractTemplateService;
import com.hongyu.service.HyAdminService;
import com.hongyu.util.AuthorityUtils;


@Controller
@RequestMapping("/admin/contractTemplateManagement/")
public class ContractTemplateManagementController {

	@Resource(name="contractTemplateServiceImpl")
	ContractTemplateService contractTemplateService;
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,ContractTemplate contractTemplate,HttpSession session,HttpServletRequest request){
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.in("operator", hyAdmins));
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createDate");
			orders.add(order);
			pageable.setOrders(orders);
			Page<ContractTemplate> page=contractTemplateService.findPage(pageable,contractTemplate);
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			for(ContractTemplate tmp:page.getRows()){
				HyAdmin creator = tmp.getOperator();
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("createDate", tmp.getCreateDate());
				m.put("modifyDate", tmp.getModifyDate());
				m.put("title", tmp.getTitle());
				m.put("type", tmp.getType());
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
	
	@RequestMapping("add")
	@ResponseBody
	public Json add(ContractTemplate contractTemplate,HttpSession session){
		Json json=new Json();
		try {
			String username=(String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			contractTemplate.setOperator(hyAdmin);
			contractTemplateService.save(contractTemplate);
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败： "+e.getMessage());
			// TODO: handle exception
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping("modify")
	@ResponseBody
	public Json modify(ContractTemplate contractTemplate){
		Json json=new Json();
		try {
			contractTemplateService.update(contractTemplate
					,"createDate","operator");
			json.setSuccess(true);
			json.setMsg("编辑成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("detail/view")
	@ResponseBody
	public Json deail(Long id){
		Json json=new Json();
		try {
			ContractTemplate contractTemplate=contractTemplateService.find(id);
			json.setSuccess(true);
			json.setMsg("编辑成功");
			json.setObj(contractTemplate);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败: "+e.getMessage());
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
			contractTemplateService.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
}
