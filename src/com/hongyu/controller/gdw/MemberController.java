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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Member;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.MemberService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/member/")
public class MemberController {

	@Resource(name = "memberServiceImpl")
	MemberService memberService;
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@RequestMapping("addMember")
	@ResponseBody
	public Json add(Member member,HttpSession session){
		Json json=new Json();
		try {
			memberService.save(member);
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: "+e.getMessage());
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("editMember")
	@ResponseBody
	public Json edit(Member member){
		Json json=new Json();
		try {
			memberService.save(member);
			json.setSuccess(true);
			json.setMsg("修改成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("修改失败: "+e.getMessage());
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("deleteMember")
	@ResponseBody
	public Json delete(Long id){
		Json json=new Json();
		try {
			memberService.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("删除失败: "+e.getMessage());
			// TODO: handle exception
		}
		return json;
	}
	/*static class WrapMember{
		private Pageable pageable;
		private Member member;
		public Pageable getPageable() {
			return pageable;
		}
		public void setPageable(Pageable pageable) {
			this.pageable = pageable;
		}
		public Member getMember() {
			return member;
		}
		public void setMember(Member member) {
			this.member = member;
		}
	}*/
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,Member member,HttpSession session,HttpServletRequest request) {
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
			Page<Member> page=memberService.findPage(pageable,member);
			for(Member tmp:page.getRows()){
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("name", tmp.getName());
				m.put("phone", tmp.getPhone());
				m.put("idCard", tmp.getIdCard());
				m.put("memberNum", tmp.getMemberNum());
				m.put("createtime", tmp.getCreatetime());
				result.add(m);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("rows", result);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hm);
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
	@RequestMapping("checkExist")
	@ResponseBody
	public Json checkExist(String memberNum){
		Json json=new Json();
		try {
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("memberNum", memberNum));
			List<Member> lists=memberService.findList(null,filters,null);
			if(lists!=null&&lists.size()>0){
				json.setSuccess(false);
				json.setMsg("会员号已存在");
			}
			else{
				json.setSuccess(true);
				json.setMsg("可以注册");
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找错误，请重试");
		}
		return json;
	}
}
