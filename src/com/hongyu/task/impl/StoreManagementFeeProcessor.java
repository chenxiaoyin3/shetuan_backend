package com.hongyu.task.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.Store;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.StoreService;
import com.hongyu.task.Processor;
import com.hongyu.util.Constants;
@Component("storeManagementFeeProcessor")
public class StoreManagementFeeProcessor implements Processor{

	@Resource(name="storeServiceImpl")
	StoreService storeService;
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	@Resource(name="hyRoleServiceImpl")
	HyRoleService hyRoleService;
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Override
	public void process() {
		List<Filter> filters=new LinkedList<>();
		filters.add(Filter.eq("status", Constants.STORE_JI_HUO));
		filters.add(Filter.lt("validDate", new Date()));
		List<Store> stores=storeService.findList(null,filters,null);
		if(stores!=null&&stores.size()>0){
			for(Store store:stores){
				store.setMstatus(5);//设置管理费过期
				store.setStatus(Constants.STORE_SHEN_HE_TONG_GUO_DONG_JIE_ZHONG);//设置门店状态未激活
				storeService.update(store);
				
				/** 将该门店下所有的登录账号得角色都修改成为"虹宇门店经理未激活"
				 * added by GSbing,20190709
				 */
				Department department=store.getDepartment(); //找到门店所属部门
				Set<HyAdmin> hyAdmins=department.getHyAdmins(); //找到该部门所有员工
				List<Filter> roleFilters=new ArrayList<>();
				roleFilters.add(Filter.eq("name", "虹宇门店经理未激活"));
				List<HyRole> roles=hyRoleService.findList(null,filters,null);
				HyRole role=roles.get(0); //默认只有一个角色
				for(HyAdmin admin:hyAdmins) {
					admin.setRole(role);
					hyAdminService.update(admin);
				}
			}
		}
		// TODO Auto-generated method stub
		
	}

}
