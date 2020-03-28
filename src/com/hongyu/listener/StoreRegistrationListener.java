package com.hongyu.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hongyu.CommonAttributes;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.HyAdminService;
import com.hongyu.util.Constants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

@SuppressWarnings("serial")
public class StoreRegistrationListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {

		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		// EmployeeService employeeService =
		// webApplicationContext.getBean(EmployeeService.class);
		HyAdminService hyAdminService = webApplicationContext.getBean(HyAdminService.class);
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department = hyAdmin.getDepartment();
			Set<HyAdmin> hyAdmins = department.getHyAdmins();
			if (hyAdmins == null || hyAdmins.size() == 0) {
				throw new NullPointerException("无法获取下一阶段审核人，请重新提交");
			} else {
				
				int agentId = Constants.AGENTID_LIANSUO;
				List<String> userIds = new ArrayList<>();
				
				for (HyAdmin admin : hyAdmins) {
					HyRole role = admin.getRole();
					if (role.getName().indexOf("经理") > -1) {
						delegateTask.addCandidateUser(admin.getUsername());
						/**发送企业微信**/
						userIds.add(admin.getUsername());
//						break;
					}
				}
				String messageContent = "连锁发展：您有新工作需要审核，请尽快完成。";
				SendMessageQyWx.sendWxMessage(agentId, userIds, null, messageContent);
			}

		}

	}

}
