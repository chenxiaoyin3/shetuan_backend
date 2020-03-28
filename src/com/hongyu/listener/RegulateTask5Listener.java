package com.hongyu.listener;

import static com.hongyu.util.Constants.Regulate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.Filter;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

/**
 * 产品中心经理审核报账申请 ---总公司为总公司产品中心经理，分公司为分公司产品中心经理审核
 * @author GSbing
 *
 */
public class RegulateTask5Listener implements TaskListener {
	
	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		DepartmentService departmentService = webApplicationContext.getBean(DepartmentService.class);
		RuntimeService runtimeService = (RuntimeService)webApplicationContext.getBean(RuntimeService.class);
		Department department = (Department) runtimeService.getVariable(delegateTask.getExecutionId(), "department"); //供应商创建人
		
		while(!department.getIsCompany()){
			//如果当前部门不是公司，就找到他的父部门继续判断。
			department = department.getHyDepartment();
		}
		Long companyDepartmentId=department.getId();
		HyCompany company=department.getHyCompany();
		HyDepartmentModelService hyDepartmentModelService = webApplicationContext.getBean(HyDepartmentModelService.class);
		Set<HyAdmin> admins = new HashSet<>();
		//如果是总公司
		if(company.getIsHead()==true) {
			HyDepartmentModel model = hyDepartmentModelService.find("总公司产品研发中心");
			Set<Department> ds = model.getHyDepartments();
			for(Department d : ds) {
				admins.addAll(d.getHyAdmins());
			}
		}
		//如果是分公司
		else {
			HyDepartmentModel model = hyDepartmentModelService.find("分公司产品中心");
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("hyDepartmentModel", model));
			List<Department> departments = departmentService.findList(null, filters, null);
		    for(Department dp:departments) {
		    	if(dp.getId().equals(companyDepartmentId)) {
		    		admins.addAll(dp.getHyAdmins());
		    	}
		    }
		}
		List<String> userIds = new ArrayList<>();
		for (HyAdmin admin : admins) {
			HyRole role = admin.getRole();
			if (role.getHyRoleAuthorities().size() > 0) {
				for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
					if (Regulate.equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
						delegateTask.addCandidateUser(admin.getUsername());
						userIds.add(admin.getUsername());
						break;
					}
				}
			}
		}
		String content = "产品中心：您有新工作需要审核，请尽快完成。";
		SendMessageQyWx.sendWxMessage(QyWxConstants.CHAN_PIN_BU_QYWX_APP_AGENT_ID, userIds , null, content );
	}
}
