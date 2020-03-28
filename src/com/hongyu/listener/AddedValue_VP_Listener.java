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
import com.hongyu.entity.Store;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.StoreService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

/** 增值业务审核 - 指定分公司副总 */
@SuppressWarnings("serial")
public class AddedValue_VP_Listener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {

		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();

		HyRoleService hyRoleService = webApplicationContext.getBean(HyRoleService.class);
		DepartmentService departmentService = webApplicationContext.getBean(DepartmentService.class);
		HyAdminService hyAdminService = webApplicationContext.getBean(HyAdminService.class);
		StoreService storeService = webApplicationContext.getBean(StoreService.class);

		// 获取storeId
		RuntimeService runtimeService = (RuntimeService) webApplicationContext.getBean(RuntimeService.class);
		Long storeId = (Long) runtimeService.getVariable(delegateTask.getExecutionId(), "storeId"); // 获取门店id
		Store store = storeService.find(storeId);
		
		Department department = store.getDepartment();
		List<Filter> filters = new LinkedList<>();
		filters.add(Filter.like("name", "副总"));
		List<HyRole> roles = hyRoleService.findList(null, filters, null);
		List<Filter> filters2 = new LinkedList<>();
		filters2.add(Filter.in("role", roles));
		List<HyAdmin> hyAdmins;

		String treePath = department.getTreePath();
		String[] tpStrings = treePath.split(",");
		Long id = Long.parseLong(tpStrings[2]);
		Department department2 = departmentService.find(id);
		filters2.add(Filter.eq("department", department2));
		hyAdmins = hyAdminService.findList(null, filters2, null);

		if (hyAdmins == null || hyAdmins.size() == 0) {
			throw new NullPointerException("无法获取下一阶段审核人，请重新提交");
		} else {
			List<String> userIds = new ArrayList<>();
			for (HyAdmin admin : hyAdmins) {
				delegateTask.addCandidateUser(admin.getUsername());
				userIds.add(admin.getUsername());
			}
			String content = "您有新工作需要审核，请尽快完成。";
			SendMessageQyWx.sendWxMessage(QyWxConstants.FEN_GONG_SI_QYWX_APP_AGENT_ID, userIds , null, content );
		}
	}
}
