package com.hongyu.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.rest.service.api.identity.UserInfoResponse;
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

/** 预付款审核 - 指定财务审核人 - 总公司 */
@SuppressWarnings("serial")
public class AccountantReview_PrePay_Listener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		HyDepartmentModelService hyDepartmentModelService = webApplicationContext
				.getBean(HyDepartmentModelService.class);

		HyDepartmentModel model = hyDepartmentModelService.find("总公司财务部");
		Set<Department> ds = model.getHyDepartments();
		Set<HyAdmin> admins = new HashSet<>();
		for (Department d : ds) {
			admins.addAll(d.getHyAdmins());
		}
		List<String> users = new ArrayList<>();
		for (HyAdmin admin : admins) {
			HyRole role = admin.getRole();
			if (role.getHyRoleAuthorities().size() > 0) {
				for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
					if ("admin/prePay/review".equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
						delegateTask.addCandidateUser(admin.getUsername());
						users.add(admin.getUsername());
						break;
					}
				}
			}
		}
		/****调用微信接口 发送微信推送*****/
		String content = "您有新工作需要审核，请尽快完成。";
		SendMessageQyWx.sendWxMessage(QyWxConstants.ZONG_BU_CAI_WU_QYWX_APP_AGENT_ID, users , null, content );
	
	}
}