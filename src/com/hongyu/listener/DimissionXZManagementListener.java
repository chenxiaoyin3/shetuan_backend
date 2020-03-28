package com.hongyu.listener;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hongyu.CommonAttributes;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.service.HyAdminService;
/**
 * 行政经理审核监听器
 * @author li_yang
 *
 */
@SuppressWarnings("serial")
public class DimissionXZManagementListener implements TaskListener{
	@Override
	public void notify(DelegateTask delegateTask) {

		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		// EmployeeService employeeService =
		// webApplicationContext.getBean(EmployeeService.class);
		HyAdminService hyAdminService = webApplicationContext.getBean(HyAdminService.class);
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department = hyAdmin.getDepartment();
			Set<HyAdmin> hyAdmins = department.getHyAdmins();
			if (hyAdmins == null || hyAdmins.size() == 0) {
				throw new NullPointerException("无法获取下一阶段审核人，请重新提交");
			} else {
				for (HyAdmin admin : hyAdmins) {
					HyRole role = admin.getRole();
					if (role.getName().indexOf("行政中心部门经理") > -1) {
						delegateTask.addCandidateUser(admin.getUsername());
//						break;
					}

				}
			}

		}

	}
}
