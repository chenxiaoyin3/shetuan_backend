package com.hongyu.listener;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;
/**
 * 团变更扣点listener
 * @author guoxinze
 *
 */
public class GroupBiankoudianListener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		
		//需要财务审核之前的审核步骤中传递creator的流程变量
		RuntimeService runtimeService = (RuntimeService)webApplicationContext.getBean(RuntimeService.class);
		String admin = (String) runtimeService.getVariable(delegateTask.getExecutionId(), "admin"); //供应商创建人
		delegateTask.addCandidateUser(admin);
		List<String> userIds = new ArrayList<>();
		userIds.add(admin);
		String content = "您有新工作需要审核，请尽快完成。";
		int agentId = QyWxConstants.CAI_GOU_BU_QYWX_APP_AGENT_ID;
		SendMessageQyWx.sendWxMessage(agentId, userIds , null, content );
	}

}
