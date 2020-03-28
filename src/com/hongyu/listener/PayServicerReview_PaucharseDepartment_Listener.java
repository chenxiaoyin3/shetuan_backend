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

/** 向供应商付款审核 - 指定采购部经理 */
@SuppressWarnings("serial")
public class PayServicerReview_PaucharseDepartment_Listener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		HyDepartmentModelService hyDepartmentModelService = webApplicationContext
				.getBean(HyDepartmentModelService.class);

		HyDepartmentModel model = hyDepartmentModelService.find("总公司采购部");
		Set<Department> ds = model.getHyDepartments();
		Set<HyAdmin> admins = new HashSet<>();
		for (Department d : ds) {
			admins.addAll(d.getHyAdmins());
		}
		List<String> users = new ArrayList<>();
		for (HyAdmin admin : admins) {
			HyRole role = admin.getRole();
			if((role.getName() != null && role.getName().contains("经理")) ||(role.getDescription() != null && role.getDescription().contains("经理"))){  // 根据角色名称判断
				if (role.getHyRoleAuthorities().size() > 0) {
					for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
						if ("admin/payServicer".equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
							delegateTask.addCandidateUser(admin.getUsername());
							users.add(admin.getUsername());
							break;
						}
					}
				}
			}
		}
		/****调用微信接口 发送微信推送*****/
		String content = "您有新工作需要审核，请尽快完成。";
		boolean success = SendMessageQyWx.sendWxMessage(QyWxConstants.CAI_GOU_BU_QYWX_APP_AGENT_ID,users,null,content);
		System.out.println("发送推送     " + success);
	}
}
