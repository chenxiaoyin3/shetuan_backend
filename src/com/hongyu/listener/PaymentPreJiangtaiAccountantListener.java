package com.hongyu.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyAuthority;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAuthorityService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.RoleAuthorityService;
import com.hongyu.util.Constants;
//import com.sun.org.apache.xpath.internal.operations.String;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

/** 江泰预充款审核 - 指定财务审核人 */
public class PaymentPreJiangtaiAccountantListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
//		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
//		HyAuthorityService hyAuthorityService = webApplicationContext.getBean(HyAuthorityService.class);
//		RoleAuthorityService roleAuthorityService = webApplicationContext.getBean(RoleAuthorityService.class);
//
//		List<Filter> filters3 = new ArrayList<>();
//		filters3.add(new Filter("requestUrl",Operator.eq,"admin/accountant/jiangtaiPreSave"));
//		List<HyAuthority> list = hyAuthorityService.findList(null, filters3, null);
//		filters3.clear();
//		filters3.add(new Filter("authoritys",Operator.eq,list.get(0)));
//		List<HyRoleAuthority> ras = roleAuthorityService.findList(null, filters3, null);
//		for (HyRoleAuthority ra : ras) {
//			HyRole role = ra.getRoles();
//			Set<HyAdmin> admins = role.getHyAdmins();
//			for (HyAdmin admin : admins) {
//				delegateTask.addCandidateUser(admin.getUsername());
//			}
//		}
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		HyDepartmentModelService hyDepartmentModelService = context.getBean(HyDepartmentModelService.class);
		HyDepartmentModel model = hyDepartmentModelService.find("总公司财务部");
		Set<Department> ds = model.getHyDepartments();
		Set<HyAdmin> admins = new HashSet<>();
		for(Department d:ds){
			admins.addAll(d.getHyAdmins());
		}
		List<String> userIds = new ArrayList<>();
		for(HyAdmin admin:admins){
			HyRole role = admin.getRole();
			if(role.getHyRoleAuthorities().size()>0){
				for(HyRoleAuthority hyRoleAuthority:role.getHyRoleAuthorities()){
					if(Constants.CaiwuAuditStoreTuiTuan.equals(hyRoleAuthority.getAuthoritys().getRequestUrl())){
						delegateTask.addCandidateUser(admin.getUsername());
						userIds.add(admin.getUsername());
					}
				}
			}
		}	
		String content = "您有新工作需要审核，请尽快完成。";
		SendMessageQyWx.sendWxMessage(QyWxConstants.ZONG_BU_CAI_WU_QYWX_APP_AGENT_ID, userIds , null, content );

	}

}
