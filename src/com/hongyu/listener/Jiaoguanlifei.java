package com.hongyu.listener;

import static com.hongyu.util.Constants.JiaoguanlifeiListenerFinance;
import static com.hongyu.util.Constants.JiaoguanlifeiListenerURL;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hongyu.Filter;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

public class Jiaoguanlifei implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		webApplicationContext.getBean(HyAdminService.class);
		DepartmentService departmentService = webApplicationContext.getBean(DepartmentService.class);
		HyDepartmentModelService hyDepartmentModelService = webApplicationContext
				.getBean(HyDepartmentModelService.class);
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HyDepartmentModel model = hyDepartmentModelService.find(JiaoguanlifeiListenerFinance);
			List<Filter> filters = new ArrayList<>();
			Filter filter = Filter.eq("hyDepartmentModel", model);
			filters.add(filter);

			List<Department> departments = departmentService.findList(null, filters, null);
			List<HyAdmin> hyAdmins = new ArrayList<>();
			if (departments.size() > 0) {
				for (Department department : departments) {
					hyAdmins.addAll(department.getHyAdmins());
				}
			}
			if (hyAdmins == null || hyAdmins.size() == 0) {
				throw new NullPointerException("无法获取下一阶段审核人，请重新提交");
			} else {
				List<String> userIds = new ArrayList<>();
				for (HyAdmin admin : hyAdmins) {
					HyRole role = admin.getRole();
					if (role.getHyRoleAuthorities().size() > 0) {
						for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
							if (JiaoguanlifeiListenerURL.equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
								delegateTask.addCandidateUser(admin.getUsername());
								userIds.add(admin.getUsername());
								break;
							}
						}
					}
				}
				String content = "财务部：您有新工作需要审核，请尽快完成。";
				SendMessageQyWx.sendWxMessage(QyWxConstants.FEN_GONG_SI_QYWX_APP_AGENT_ID, userIds , null, content );
			}
		}

	}

}
