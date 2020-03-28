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

public class StoreTuiTuanProviderListener implements TaskListener{

//	@Resource(name = "hyAdminServiceImpl")
//	private HyAdminService hyAdminService;
	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		HyAdminService hyAdminService = webApplicationContext.getBean(HyAdminServiceImpl.class);
		//需要供应商审核
		RuntimeService runtimeService = webApplicationContext.getBean(RuntimeService.class);
		String provider = (String)runtimeService.getVariable(delegateTask.getExecutionId(), "provider");	//供应商创建人
		delegateTask.addCandidateUser(provider);
		
		//给供应商发退团提醒
		HyAdmin hyAdmin = hyAdminService.find(provider);
		SendMessageEMY.sendMessage(hyAdmin.getMobile(), "", 9);
		List<String> userIds = new ArrayList<>();
		userIds.add(hyAdmin.getUsername());
		String content = "您有新工作需要审核，请尽快完成。";
		SendMessageQyWx.sendWxMessage(QyWxConstants.CHAN_PIN_BU_QYWX_APP_AGENT_ID, userIds , null, content );
		
	}

}
