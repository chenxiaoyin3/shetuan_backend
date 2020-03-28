package com.hongyu.listener;

import static com.hongyu.util.Constants.CaigoubushenheTuiyajin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

public class QicheTuiyajinListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		HyDepartmentModelService hyDepartmentModelService = webApplicationContext.getBean(HyDepartmentModelService.class);
		DepartmentService departmentService = webApplicationContext.getBean(DepartmentService.class);
		HyAdminService hyAdminService = webApplicationContext.getBean(HyAdminService.class);
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
		Department d = hyAdminService.find(username).getDepartment();
		List<Long> treePaths = d.getTreePaths();
		Long id = treePaths.get(1);
		HyDepartmentModel model = hyDepartmentModelService.find("分公司汽车部");
		
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("hyDepartmentModel", model));
		filters.add(Filter.like("treePath", ",1," + id + ","));
		List<Department> result = departmentService.findList(null, filters, null);
		
		Set<HyAdmin> admins = new HashSet<>();
		for(Department depart : result) {
			admins.addAll(depart.getHyAdmins());
		}

		List<String> userIds = new ArrayList<>();
		for(HyAdmin admin : admins) {
			HyRole role = admin.getRole();
			if (role.getHyRoleAuthorities().size() > 0) {
				for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
					if (CaigoubushenheTuiyajin.equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
						delegateTask.addCandidateUser(admin.getUsername());
						userIds.add(admin.getUsername());
						break;
					}
				}
			}
		}
		String content = "汽车部：您有新工作需要审核，请尽快完成。";
		SendMessageQyWx.sendWxMessage(QyWxConstants.FEN_GONG_SI_QYWX_APP_AGENT_ID, userIds , null, content );
	}

}
