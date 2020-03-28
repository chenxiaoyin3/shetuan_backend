package com.hongyu.listener;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

/** 付尾款审核-总公司产品中心部门经理 */
@SuppressWarnings("serial")
public class BalanceDue_Company_DepartmentManager_Listener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		DepartmentService departmentService = webApplicationContext
				.getBean(DepartmentService.class);
		
		RuntimeService runtimeService = (RuntimeService)webApplicationContext.getBean(RuntimeService.class);
		// 审核申请人的部门id
		Long institutionId= (Long) runtimeService.getVariable(delegateTask.getExecutionId(), "institutionId");		
		Department department = departmentService.find(institutionId);
		Department hyDepartment=new Department();
		// 申请人为国内/汽车/出境的员工/经理, 获取其父部门
		hyDepartment=department.getHyDepartment();
		List<String> userIds = new ArrayList<>();
		for (HyAdmin admin : hyDepartment.getHyAdmins()) {
			HyRole role = admin.getRole();
			if((role.getName() != null && role.getName().contains("经理"))) {		
				if (role.getHyRoleAuthorities().size() > 0) {
					for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
						if ("admin/balanceDueApply".equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
							delegateTask.addCandidateUser(admin.getUsername());
							userIds.add(admin.getUsername());
							break;
						}
					}
				}
			}	
		}
		String content = "您有新工作需要审核，请尽快完成。";
		SendMessageQyWx.sendWxMessage(QyWxConstants.CHAN_PIN_BU_QYWX_APP_AGENT_ID, userIds , null, content );
	
	}
}
