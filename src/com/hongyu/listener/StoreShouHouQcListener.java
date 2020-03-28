package com.hongyu.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.util.Constants;
import com.hongyu.util.SendMessageEMY;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

public class StoreShouHouQcListener implements TaskListener{

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		HyDepartmentModelService hyDepartmentModelService = context.getBean(HyDepartmentModelService.class);
		HyDepartmentModel model = hyDepartmentModelService.find("总公司品控中心");
		Set<Department> ds = model.getHyDepartments();
		Set<HyAdmin> admins = new HashSet<>();
		for(Department d:ds){
			admins.addAll(d.getHyAdmins());
		}
		
		List<String> userIds = new ArrayList<>();
		
		for(HyAdmin admin:admins){
			HyRole role = admin.getRole();
			if(role.getHyRoleAuthorities().size()>0){
				for(HyRoleAuthority hyRoleAuthority:role.getHyRoleAuthorities()){
					if(Constants.PinkongAuditStoreShouHou.equals(hyRoleAuthority.getAuthoritys().getRequestUrl())){
						delegateTask.addCandidateUser(admin.getUsername());
						//发退团提醒
						userIds.add(admin.getUsername());
					}
				}
			}
		}
		
		String content = "您有新工作需要审核，请尽快完成。";
		SendMessageQyWx.sendWxMessage(QyWxConstants.PIN_KONG_ZHONG_XIN_QYWX_APP_AGENT_ID, userIds , null, content );
		
	}

}
