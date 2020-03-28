package com.hongyu.listener;

import static com.hongyu.util.Constants.Promotion;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

public class Promotion_jidiao_auditListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		HyAdminService hyAdminService = webApplicationContext.getBean(HyAdminService.class);

		//获取计调账号
		RuntimeService runtimeService = (RuntimeService) webApplicationContext.getBean(RuntimeService.class);
		String jidiaoName = (String) runtimeService.getVariable(delegateTask.getExecutionId(), "operator"); //获取计调账号
		HyAdmin jidiao=hyAdminService.find(jidiaoName);
		HyRole role = jidiao.getRole();
		List<String> userIds = new ArrayList<>();
		if (role.getHyRoleAuthorities().size() > 0) {
			for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
				if (Promotion.equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
					delegateTask.addCandidateUser(jidiao.getUsername());
					userIds.add(jidiao.getUsername());
					break;
				}
			}
		}
		String content = "您有新工作需要审核，请尽快完成。";
		SendMessageQyWx.sendWxMessage(QyWxConstants.WAI_BU_GONG_YING_SHANG_QYWX_APP_AGENT_ID, userIds , null, content );
	}
}
