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

import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;


/**
 * 副总审核计调报账申请 
 * @author GSbing
 *
 */
public class RegulateTask3Listener implements TaskListener {
	@Override
	public void notify(DelegateTask delegateTask) {
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		DepartmentService departmentService = webApplicationContext.getBean(DepartmentService.class);
		RuntimeService runtimeService = (RuntimeService)webApplicationContext.getBean(RuntimeService.class);
		Department department = (Department) runtimeService.getVariable(delegateTask.getExecutionId(), "department"); //供应商创建人		
		while(!department.getIsCompany()){
			//如果当前部门不是公司，就找到他的父部门继续判断。
			department = department.getHyDepartment();
		}
		Set<HyAdmin> admins = new HashSet<>();
		admins.addAll(department.getHyAdmins());
		List<String> userIds = new ArrayList<>();
		for (HyAdmin admin : admins) {
			HyRole role = admin.getRole();
			if(role.getName().contains("副总")) {
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
		}
		String content = "您有新工作需要审核，请尽快完成。";
		int agentId = QyWxConstants.FU_ZONG_SHEN_HE_QYWX_APP_AGENT_ID;
		SendMessageQyWx.sendWxMessage(agentId, userIds , null, content );
	}
}
