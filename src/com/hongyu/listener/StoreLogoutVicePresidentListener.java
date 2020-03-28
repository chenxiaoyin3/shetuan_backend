package com.hongyu.listener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;
import com.hongyu.util.Constants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

public class StoreLogoutVicePresidentListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		HyAdminService hyAdminService = webApplicationContext.getBean(HyAdminService.class);
		HyRoleService hyRoleService = webApplicationContext.getBean(HyRoleService.class);
		RuntimeService runtimeService = (RuntimeService)webApplicationContext.getBean(RuntimeService.class);
		DepartmentService departmentService = webApplicationContext.getBean(DepartmentService.class);
		Integer storeType= (Integer) runtimeService.getVariable(delegateTask.getExecutionId(), "storeType");
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);
		Department department = hyAdmin.getDepartment();
		List<Filter> filters = new LinkedList<>();
		filters.add(Filter.like("name", "副总"));
		List<HyRole> roles = hyRoleService.findList(null, filters, null);
		List<Filter> filters2 = new LinkedList<>();
		filters2.add(Filter.in("role", roles));
		List<HyAdmin> hyAdmins;
		if(storeType==0){//0虹宇门店
			String treePath = department.getTreePath();
			String[] tpStrings = treePath.split(",");
			Long id = Long.parseLong(tpStrings[2]);
			Department department2=departmentService.find(id);
			filters2.add(Filter.eq("department", department2));
			hyAdmins= hyAdminService.findList(null, filters2, null);
		}else{//1挂靠门店
			String treePath = department.getTreePath();
			String[] tpStrings = treePath.split(",");
			Long id = Long.parseLong(tpStrings[2]);
			Department department2=departmentService.find(id);
			filters2.add(Filter.eq("department", department2));
			hyAdmins= hyAdminService.findList(null, filters2, null);
		}
		/**发送企业微信**/
		int agentId = Constants.AGENTID_FUZONG;
		List<String> userIds = new ArrayList<>();
		
		if (hyAdmins == null || hyAdmins.size() == 0) {
			throw new NullPointerException("无法获取下一阶段审核人，请重新提交");
		} else {
			for (HyAdmin admin : hyAdmins) {
				HyRole role = admin.getRole();
				if (role.getHyRoleAuthorities().size() > 0) {
					for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
						if ("admin/storeApplicationLogout".equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
							delegateTask.addCandidateUser(admin.getUsername());
							/**发送企业微信**/
							userIds.add(admin.getUsername());
							break;
						}
					}
				}
			}
		}
		
		String messageContent = "您有新工作需要审核，请尽快完成。";
		SendMessageQyWx.sendWxMessage(agentId, userIds, null, messageContent);
	}

}
