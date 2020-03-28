package com.hongyu.listener;

import java.util.ArrayList;
import java.util.LinkedList;
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
import com.hongyu.entity.HyRole;
import com.hongyu.entity.Store;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

public class StoreLogoutManagerListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		HyAdminService hyAdminService = webApplicationContext.getBean(HyAdminService.class);
//		StoreService storeService = webApplicationContext.getBean(StoreService.class);
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department = hyAdmin.getDepartment();
//			List<Filter> filters = new LinkedList<>();
//			filters.add(Filter.eq("department", department));
//			List<Store> stores = storeService.findList(null, filters, null);
//			if (stores != null && stores.size() > 0) {
//				delegateTask.addCandidateUser(stores.get(0).getStoreAdder().getUsername());
//			} else {
				Department suoshu = department.getHyDepartment();
				Set<HyAdmin> hyAdmins = suoshu.getHyAdmins();
				if (hyAdmins == null || hyAdmins.size() == 0) {
					throw new NullPointerException("无法获取下一阶段审核人，请重新提交");
				} else {
					/**发送企业微信**/
					int agentId = Constants.AGENTID_LIANSUO;
					List<String> userIds = new ArrayList<>();
					
					for (HyAdmin admin : hyAdmins) {
						HyRole role = admin.getRole();
						if (role.getName().indexOf("经理") > -1) {
							delegateTask.addCandidateUser(admin.getUsername());
							userIds.add(admin.getUsername());
							break;
						}
					}
					String messageContent = "连锁发展：您有新工作需要审核，请尽快完成。";
					SendMessageQyWx.sendWxMessage(agentId, userIds, null, messageContent);

//				}
			}

		}
	}

}
