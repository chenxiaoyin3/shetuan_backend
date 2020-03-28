package com.hongyu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.BankList.BankType;
import com.hongyu.service.BankListService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HySupplierService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/ticket/supplier")
public class PiaowubuGongyingshangController {
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "bankListServiceImpl")
	BankListService bankListService;
	
	@Resource(name = "hyAreaServiceImpl")
	HyAreaService areaService;
	
	//新增供应商
	@RequestMapping(value = "add")
	@ResponseBody
	public Json add(HySupplier gys, BankList bank, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			bank.setType(BankType.bank);
			bankListService.save(bank);
			gys.setBankId(bank);
			gys.setOperator(admin);
			gys.setIsCancel(false);
			gys.setIsLine(false);
			gys.setIsInner(true);
			hySupplierService.save(gys);
			json.setMsg("添加成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	//供应商列表
	@RequestMapping(value = "list/view")
	@ResponseBody
	public Json list(Pageable pageable, HySupplier gys, HttpSession session, HttpServletRequest request) {
		Json j = new Json();
		try {
			Map<String, Object> result = new HashMap<String, Object>();
			List<HashMap<String, Object>> lhm = new ArrayList<>();
			
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.in("creator", hyAdmins));
			pageable.setFilters(filters);
	      
	      Page<HySupplier> page = this.hySupplierService.findPage(pageable, gys);
	      if (page.getRows().size() > 0) {
	        for (HySupplier p : page.getRows())
	        {
	          HashMap<String, Object> hm = new HashMap<String, Object>();
	          HyAdmin creater = p.getOperator();
	          hm.put("id", p.getId());
	          hm.put("supplierName", p.getSupplierName());//供应商名称
	          hm.put("createTime", p.getCreateDate());//创建时间
	          hm.put("adminName", p.getAdminName());//负责人姓名
	          hm.put("creator", p.getOperator().getName());//创建人
	          if (p.getOperator() != null) {
	            hm.put("creator", p.getOperator().getName());
	          }
	      		/** 当前用户对本条数据的操作权限 */
				if(creater.equals(admin)){
					if(co == CheckedOperation.view) {
						hm.put("privilege", "view");
					} else {
						hm.put("privilege", "edit");
					}
				} else{
					if(co == CheckedOperation.edit) {
						hm.put("privilege", "edit");
					} else {
						hm.put("privilege", "view");
					}
				}
	          lhm.add(hm);
	        }
	      }
	      result.put("pageSize", Integer.valueOf(page.getPageSize()));
	      result.put("pageNumber", Integer.valueOf(page.getPageNumber()));
	      result.put("total", Long.valueOf(page.getTotal()));
	      result.put("rows", lhm);
	      j.setSuccess(true);
	      j.setMsg("查询成功");
	      j.setObj(result);
	
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查看列表失败: "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return j;
	}
	
		//供应商详情
		@RequestMapping(value="detail/view")
		@ResponseBody
		public Json detail(Long id) {
			Json j = new Json();
			try {
				HySupplier gys = hySupplierService.find(id);
				j.setMsg("查看成功");
				j.setSuccess(true);
				j.setObj(gys);
			} catch(Exception e) {
				// TODO Auto-generated catch block
				j.setSuccess(false);
				j.setMsg(e.getMessage());
			}
			return j;
		}
	
	//编辑供应商
	@RequestMapping(value = "edit")
	@ResponseBody
	public Json edit(HySupplier gys, BankList bank, Long bId, Long sId) {
		Json j = new Json();
		try {
			bank.setId(bId);
			gys.setId(sId);
			bankListService.update(bank, "type");
			hySupplierService.update(gys, "operator", "isCancel", "bankId","isLine", "isInner", "isDijie", "isCaigouqian", "isActive", "supplierStatus", "operator", "hySupplierContracts");
			j.setMsg("更新成功");
			j.setSuccess(true);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	//取消供应商
	@RequestMapping(value = "cancel")
	@ResponseBody
	public Json cancel(Long id) {
		Json j = new Json();
		try {
			HySupplier gys = hySupplierService.find(id);
			gys.setIsCancel(true);
			hySupplierService.update(gys);
			j.setMsg("取消成功");
			j.setSuccess(true);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	//恢复供应商
	@RequestMapping(value = "restore")
	@ResponseBody
	public Json restore(Long id) {
		Json j = new Json();
		try {
			HySupplier gys = hySupplierService.find(id);
			gys.setIsCancel(false);
			hySupplierService.update(gys);
			j.setMsg("恢复成功");
			j.setSuccess(true);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
}
