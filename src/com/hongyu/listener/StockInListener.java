package com.hongyu.listener;

import static com.hongyu.util.Constants.Business_Purchase;
import static com.hongyu.util.Constants.Purchase_Audit;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.hongyu.Filter;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;

//采购入库的任务监听器————只针对采购部经理
public class StockInListener implements TaskListener {
	@Override
	public void notify(DelegateTask delegateTask) {
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		webApplicationContext.getBean(HyAdminService.class);
		DepartmentService departmentService = webApplicationContext.getBean(DepartmentService.class);
		HyDepartmentModelService hyDepartmentModelService = webApplicationContext
				.getBean(HyDepartmentModelService.class);
		HyDepartmentModel model = hyDepartmentModelService.find(Business_Purchase);
		List<Filter> filters = new ArrayList<>();
		Filter filter = Filter.eq("hyDepartmentModel", model);
		filters.add(filter);
		List<Department> departments = departmentService.findList(null, filters, null);
		List<HyAdmin> hyAdmins = new ArrayList<>();
		if (departments.size() > 0) {
			for (Department department : departments)
				hyAdmins.addAll(department.getHyAdmins());
		}
		for (HyAdmin admin : hyAdmins) {
			HyRole role = admin.getRole();
			if (role.getHyRoleAuthorities().size() > 0) {
				for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
					if (Purchase_Audit.equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
						delegateTask.addCandidateUser(admin.getUsername());
						break;
					}
				}
			}
		}
	}

}
