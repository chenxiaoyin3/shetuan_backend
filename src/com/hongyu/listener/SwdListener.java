package com.hongyu.listener;

import static com.hongyu.util.Constants.Shuaiweidan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

//实现这个接口 activity框架要实现这个 谁审核谁用：这个写一个就行
//在图里面和第二个流程“品控中心审核”绑定
public class SwdListener implements TaskListener {


	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		HyDepartmentModelService hyDepartmentModelService = webApplicationContext.getBean(HyDepartmentModelService.class);
		//1 find需要修改下  名称从一个数据库中找department model
		HyDepartmentModel model = hyDepartmentModelService.find("总公司品控中心");
		Set<Department> ds = model.getHyDepartments();
		Set<HyAdmin> admins = new HashSet<>();
		for(Department d : ds) {
			admins.addAll(d.getHyAdmins());
		}
		List<String> userIds = new ArrayList<>();
		for(HyAdmin admin : admins) {
			HyRole role = admin.getRole();
			if (role.getHyRoleAuthorities().size() > 0) {
				for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
					//CaigoubushenheTuiyajin是第二个要改的，是一个url，之后再改
					if (Shuaiweidan.equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
						delegateTask.addCandidateUser(admin.getUsername());
						userIds.add(admin.getUsername());
						break;
					}
				}
			}
		}
		String content = "您有新工作需要审核，请尽快完成。";
		SendMessageQyWx.sendWxMessage(QyWxConstants.PIN_KONG_ZHONG_XIN_QYWX_APP_AGENT_ID, userIds , null, content );
	}

}
