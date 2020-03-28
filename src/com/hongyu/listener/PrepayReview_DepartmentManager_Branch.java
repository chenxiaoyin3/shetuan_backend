package com.hongyu.listener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.Filter;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

/** 预付款审核 - 指定部门经理审核 */
@SuppressWarnings("serial")
public class PrepayReview_DepartmentManager_Branch implements TaskListener{

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();

		HyRoleService hyRoleService = webApplicationContext.getBean(HyRoleService.class);
		DepartmentService departmentService = webApplicationContext.getBean(DepartmentService.class);
		HyAdminService hyAdminService = webApplicationContext.getBean(HyAdminService.class);

		// 获取部门Id   汽车部 国内部
		RuntimeService runtimeService = (RuntimeService) webApplicationContext.getBean(RuntimeService.class);
		Long departmentId =(Long) runtimeService.getVariable(delegateTask.getExecutionId(), "department");
//		Long departmentId = departmentid.longValue();
//		Long departmentId = (Long) runtimeService.getVariable(delegateTask.getExecutionId(), "departmentId"); // 获取门店id

		List<Filter> filters = new LinkedList<>();
		filters.add(Filter.like("name", "经理"));
		List<HyRole> roles = hyRoleService.findList(null, filters, null);

		List<Filter> filters2 = new LinkedList<>();
		filters2.add(Filter.in("role", roles));
		Department department = departmentService.find(departmentId);
		filters2.add(Filter.eq("department", department));
		List<HyAdmin> hyAdmins = hyAdminService.findList(null, filters2, null);

		if (hyAdmins == null || hyAdmins.size() == 0) {
			throw new NullPointerException("无法获取下一阶段审核人，请重新提交");
		} else {
			List<String> userIds = new ArrayList<>();
			for (HyAdmin admin : hyAdmins) {
				HyRole role = admin.getRole();
				if (role.getHyRoleAuthorities().size() > 0) {
					for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
						if ("admin/prePay/review".equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
							delegateTask.addCandidateUser(admin.getUsername());
							userIds.add(admin.getUsername());
							break;
						}
					}
				}
			}
			String content = "您有新工作需要审核，请尽快完成。";
			SendMessageQyWx.sendWxMessage(QyWxConstants.FEN_GONG_SI_QYWX_APP_AGENT_ID, userIds , null, content );
		}
	}

}
