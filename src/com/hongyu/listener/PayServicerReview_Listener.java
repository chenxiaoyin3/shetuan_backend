package com.hongyu.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.Filter;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

/** 向供应商付款审核 - 指定分管市场的副总 */
@SuppressWarnings("serial")
public class PayServicerReview_Listener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		HyDepartmentModelService hyDepartmentModelService = webApplicationContext
				.getBean(HyDepartmentModelService.class);
		DepartmentService hyDepartmentModel = webApplicationContext.getBean(DepartmentService.class);
		// 无法确定对于副总的部门设置是在总公司或某个部门(如市场部)下
		// 获取总公司、总公司的一级部门(如市场部)所有的员工，再判断其名称和权限
		HyDepartmentModel model = hyDepartmentModelService.find("总公司");
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("hyDepartmentModel", model));
		List<Department> list = hyDepartmentModel.findList(null, filters, null);
		try {
			Set<HyAdmin> admins = new HashSet<>();
			Department departmentHeadquarters = list.get(0);
			admins.addAll(departmentHeadquarters.getHyAdmins());
			filters.clear();
			list.clear();
			filters.add(Filter.eq("hyDepartment", departmentHeadquarters));
			list = hyDepartmentModel.findList(null, filters, null);
			for (Department d : list) {
				// 排除掉总公司下的分公司
				if (d.getHyDepartmentModel().getName().startsWith("总公司")) {
					admins.addAll(d.getHyAdmins());
				}
			}

			List<String> users = new ArrayList<>();
			for (HyAdmin admin : admins) {
				HyRole role = admin.getRole();
				if (role.getName().contains("副总")) {
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
			boolean success = SendMessageQyWx.sendWxMessage(QyWxConstants.FU_ZONG_SHEN_HE_QYWX_APP_AGENT_ID,users,null,content);
			System.out.println("发送推送     " + success);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
