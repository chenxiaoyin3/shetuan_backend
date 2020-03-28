package com.hongyu.listener;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.entity.HyAdmin;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.impl.HyAdminServiceImpl;
import com.hongyu.util.SendMessageEMY;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

public class StoreShouHouProviderListener implements TaskListener  {
//	@Resource(name = "hyAdminServiceImpl")
//	private HyAdminService hyAdminService;
	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		HyAdminService hyAdminService = context.getBean(HyAdminServiceImpl.class);	
		//需要供应商审核
		RuntimeService runtimeService = context.getBean(RuntimeService.class);
		String provider = (String)runtimeService.getVariable(delegateTask.getExecutionId(), "provider");
		delegateTask.addCandidateUser(provider);
		
		//给供应商发退团提醒
		HyAdmin hyAdmin = hyAdminService.find(provider);
		List<String> userIds = new ArrayList<>();
		userIds.add(hyAdmin.getUsername());
		String content = "您有新工作需要审核，请尽快完成。";
		SendMessageQyWx.sendWxMessage(QyWxConstants.CHAN_PIN_BU_QYWX_APP_AGENT_ID, userIds , null, content );
	}

}
