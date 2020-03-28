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
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

import antlr.Utils;
/**
 * 产品中心经理审核报账申请 ---部门经理审核
 * @author guoxinze
 *
 */
public class RegulateTask2 implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		
		//需要财务审核之前的审核步骤中传递creator的流程变量
		RuntimeService runtimeService = (RuntimeService)webApplicationContext.getBean(RuntimeService.class);
		Department department = (Department) runtimeService.getVariable(delegateTask.getExecutionId(), "department"); //供应商创建人
		String name = department.getFullName();
		Set<HyAdmin> admins = department.getHyAdmins();//获取员工	
		List<String> userIds = new ArrayList<>();		
		//找到有权限的经理
		for(HyAdmin admin : admins) {		
			HyRole role = admin.getRole();
			if (role.getHyRoleAuthorities().size() > 0 && role.getName().contains("经理")) {//找有权限的部门经理
				for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
					if (Regulate.equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) { 
						delegateTask.addCandidateUser(admin.getUsername());
						userIds.add(admin.getUsername());
						break;
					}
				}
			}
		}
//		while(!department.getIsCompany()){
//			//如果当前部门不是公司，就找到他的父部门继续判断。
//			department = department.getHyDepartment();
//		}
		
//		if(department.getId() == 1){
//			String content = "您有新工作需要审核，请尽快完成。";
//			int agentId = QyWxConstants.CHAN_PIN_BU_QYWX_APP_AGENT_ID;
//			SendMessageQyWx.sendWxMessage(agentId, userIds , null, content );
//		}else{
			String content = "产品中心：您有新工作需要审核，请尽快完成。";
			int agentId = QyWxConstants.CHAN_PIN_BU_QYWX_APP_AGENT_ID;
			SendMessageQyWx.sendWxMessage(agentId, userIds , null, content );
//		}
	}

}
