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
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

/** 付尾款审核-总公司副总限额审核 */
@SuppressWarnings("serial")
public class BalanceDue_Company_VP_Listener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		HyDepartmentModelService hyDepartmentModelService = webApplicationContext
				.getBean(HyDepartmentModelService.class);
		HyDepartmentModel model = hyDepartmentModelService.find("总公司");
		Set<Department> ds = model.getHyDepartments();
		Set<HyAdmin> admins = new HashSet<>();
		for (Department d : ds) {
			admins.addAll(d.getHyAdmins());
		}
		List<String> userIds = new ArrayList<>();
		for (HyAdmin admin : admins) {
			HyRole role = admin.getRole();
			if ((role.getName() != null && role.getName().contains("副总"))
					|| (role.getDescription() != null && role.getDescription().contains("副总"))) { // 根据角色名称判断
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
		SendMessageQyWx.sendWxMessage(QyWxConstants.FU_ZONG_SHEN_HE_QYWX_APP_AGENT_ID, userIds , null, content );
	
	}

}
