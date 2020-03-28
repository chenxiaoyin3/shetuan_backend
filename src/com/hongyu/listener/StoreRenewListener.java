package com.hongyu.listener;

import java.util.ArrayList;
import java.util.List;

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
import com.hongyu.Filter.Operator;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Store;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

/*
 * 门店申请续签
 */
public class StoreRenewListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();    
//		EmployeeService employeeService = webApplicationContext.getBean(EmployeeService.class);
		HyAdminService hyAdminService=webApplicationContext.getBean(HyAdminService.class);
		StoreService storeService=webApplicationContext.getBean(StoreService.class);
		RequestAttributes requestAttributes=RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			String username = (String) request.getSession().getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin= hyAdminService.find(username);
			List<Filter> filters=new ArrayList<>();
			Filter filter=new Filter("hyAdmin", Operator.eq, hyAdmin);
			filters.add(filter);
			Store store=storeService.findList(1, filters, null).get(0);
			delegateTask.addCandidateUser(store.getStoreAdder().getUsername());
			
			/**发送企业微信**/
			int agentId = Constants.AGENTID_LIANSUO;
			List<String> userIds = new ArrayList<>();
			userIds.add(store.getStoreAdder().getUsername());
			String messageContent = "连锁发展：您有新工作需要审核，请尽快完成。";
			SendMessageQyWx.sendWxMessage(agentId, userIds, null, messageContent);
	
		}
			
	}

}
