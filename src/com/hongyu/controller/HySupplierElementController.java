package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyPromotion;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BankListService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.BeanUtils;

@Controller
@RequestMapping("/admin/supplier/element/")
public class HySupplierElementController {
	@Resource(name = "hySupplierElementServiceImpl")
	HySupplierElementService  hySupplierElementService;
	@Resource(name = "bankListServiceImpl")
	BankListService  bankListService;
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	//已修改-需要考虑新增和更新的时候名字不能重复的问题
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(HySupplierElement hySupplierElement, BankList bankList) {
		Json j = new Json();
		try{
			if(bankList.getBankAccount() != null){
				bankListService.save(bankList);
				hySupplierElement.setBankList(bankList);
				hySupplierElementService.save(hySupplierElement);
			}else {
				hySupplierElementService.save(hySupplierElement);
			}
			j.setSuccess(true);
			j.setMsg("添加成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg("供应商名称重复");
		}
		return j;
	}

	@RequestMapping(value="cancel", method = RequestMethod.GET)
	@ResponseBody
	public Json cancel(Long id) {
		Json j = new Json();
		try{
			HySupplierElement hySupplierElement = hySupplierElementService.find(id);
			hySupplierElement.setStatus(false);
			hySupplierElementService.update(hySupplierElement);
			j.setSuccess(true);
			j.setMsg("取消成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	@RequestMapping(value="restore", method = RequestMethod.GET)
	@ResponseBody
	public Json restore(Long id) {
		Json j = new Json();
		try{
			HySupplierElement hySupplierElement = hySupplierElementService.find(id);
			hySupplierElement.setStatus(true);
			hySupplierElementService.update(hySupplierElement);
			j.setSuccess(true);
			j.setMsg("恢复成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	@RequestMapping(value="update", method = RequestMethod.POST)
	@ResponseBody
	public Json update(HySupplierElement hySupplierElement, BankList bankList, Long supplierId, Long bankId) {
		Json j = new Json();
		try{	
			
			if(bankId != null){
				bankList.setId(bankId);
				hySupplierElement.setId(supplierId);
				bankListService.update(bankList,"type","alias");
				hySupplierElementService.update(hySupplierElement,"createTime","supplierType","status","bankList", "isShouru","supplierLine","operator");	
			}else {
				hySupplierElement.setId(supplierId);
				hySupplierElementService.update(hySupplierElement,"createTime","supplierType","status","bankList", "isShouru","supplierLine","operator");	
			}

			j.setSuccess(true);
			j.setMsg("更新成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping(value="list/view", method = RequestMethod.POST)
	@ResponseBody
	public Json list(Pageable pageable, HySupplierElement hySupplierElement, HttpSession session, HttpServletRequest request){
		Json j = new Json();
		try{
			
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
			filters.add(Filter.in("operator", hyAdmins));
			pageable.setFilters(filters);
	      
	        Page<HySupplierElement> page = this.hySupplierElementService.findPage(pageable, hySupplierElement);
	        if (page.getTotal() > 0) {
	        	
		        for (HySupplierElement element : page.getRows())
		        {
		          HashMap<String, Object> hm = new HashMap<String, Object>();
		          HyAdmin creater = element.getOperator();
		          hm.put("id", element.getId());
				  hm.put("name", element.getName());
				  hm.put("liableperson", element.getLiableperson());
				  hm.put("createtime", element.getCreateTime());
				  hm.put("operator", element.getOperator());
				  hm.put("status", element.getStatus());
		          if (element.getOperator() != null) {
		            hm.put("creator", element.getOperator().getName());
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
	        Collections.sort(lhm, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Date date1 = (Date) o1.get("createtime");
					Date date2 = (Date) o2.get("createtime");
					return date1.compareTo(date2); 
				}
			});
		    Collections.reverse(lhm);
	      result.put("pageSize", Integer.valueOf(page.getPageSize()));
	      result.put("pageNumber", Integer.valueOf(page.getPageNumber()));
	      result.put("total", Long.valueOf(page.getTotal()));
	      result.put("rows", lhm);
	      j.setSuccess(true);
	      j.setMsg("查询成功");
	      j.setObj(result);
	      } catch (Exception e) {
		      j.setSuccess(false);
		      j.setMsg(e.getMessage());
		  }
		  return j;
	}
	@RequestMapping(value="detail/view", method = RequestMethod.GET)
	@ResponseBody
	public Json detail(Long id) {
		Json j = new Json();
		HySupplierElement hySupplierElement = hySupplierElementService.find(id);
		j.setMsg("查询成功");
		j.setSuccess(true);
		j.setObj(hySupplierElement);
		return j;
	}
}
