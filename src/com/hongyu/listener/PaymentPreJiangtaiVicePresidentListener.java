package com.hongyu.listener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.Filter;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;
import com.hongyu.util.Constants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

public class PaymentPreJiangtaiVicePresidentListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		HyAdminService hyAdminService = webApplicationContext.getBean(HyAdminService.class);
		HyRoleService hyRoleService = webApplicationContext.getBean(HyRoleService.class);
		List<Filter> filters = new LinkedList<>();
		filters.add(Filter.like("name", "副总"));
		List<HyRole> roles = hyRoleService.findList(null, filters, null);
		List<Filter> filters2 = new LinkedList<>();
		filters2.add(Filter.in("role", roles));
		List<HyAdmin> hyAdmins = hyAdminService.findList(null, filters2, null);
		if (hyAdmins == null || hyAdmins.size() == 0) {
			throw new NullPointerException("无法获取下一阶段审核人，请重新提交");
		} else {
			/**发送企业微信**/
			int agentId = Constants.AGENTID_FUZONG;
			List<String> userIds = new ArrayList<>();
			for (HyAdmin admin : hyAdmins) {
				HyRole role = admin.getRole();
				if (role.getHyRoleAuthorities().size() > 0) {
					for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
						if ("admin/hyPaymentpreJiangtaiManager".equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
							delegateTask.addCandidateUser(admin.getUsername());
							userIds.add(admin.getUsername());
							break;
						}
					}
				}
			}
			String messageContent = "您有新工作需要审核，请尽快完成。";
			SendMessageQyWx.sendWxMessage(agentId, userIds, null, messageContent);

		}
	}

}
