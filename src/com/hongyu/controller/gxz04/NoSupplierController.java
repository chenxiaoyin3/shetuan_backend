package com.hongyu.controller.gxz04;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierService;
import com.hongyu.util.AuthorityUtils;

@RestController
@RequestMapping("/admin/nosupplier/purchase/")
@Transactional(propagation = Propagation.REQUIRED)
public class NoSupplierController {
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	/**
	 * 没签约供应商的列表页
	 */
	@RequestMapping(value="list/view")
	public Json nosupplierlist(Pageable pageable, String supplierName, HttpSession session, HttpServletRequest request) {
		Json j = new Json();
		try {
			List<HashMap<String, Object>> obj = new ArrayList<>();
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
			
			List<Filter> filters = new ArrayList<>();
			if(null != supplierName) {
				filters.add(Filter.eq("supplierName", supplierName));
			}		
			filters.add(Filter.in("operator", hyAdmins));
			
			List<HySupplier> suppliers = hySupplierService.findList(null, filters, null);
			List<HySupplier> result = new ArrayList<>();
			
			for(HySupplier supplier : suppliers) {
				if(supplier.getHySupplierContracts().isEmpty()) {
					result.add(supplier);
				}
			}
			
			for(HySupplier supplier : result) {
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", supplier.getId());
				m.put("supplierName", supplier.getSupplierName());
				m.put("isLine", supplier.getIsLine());
				m.put("isInner", supplier.getIsInner());
				m.put("isDijie", supplier.getIsDijie());
				m.put("operatorName", supplier.getOperator().getName());
				
				/** 当前用户对本条数据的操作权限 */
				if(supplier.getOperator().equals(admin)){
					if(co == CheckedOperation.view) {
						m.put("privilege", "view");
					} else {
						m.put("privilege", "edit");
					}
				} else{
					if(co == CheckedOperation.edit) {
						m.put("privilege", "edit");
					} else {
						m.put("privilege", "view");
					}
				}
				obj.add(m);
			}
			
			j.setMsg("查看详情成功");
			j.setSuccess(true);
			j.setObj(obj);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
