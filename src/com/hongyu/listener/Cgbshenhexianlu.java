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
 * 申请特殊扣点的时候采购部审核团
 * @author guoxinze
 *
 */
public class Cgbshenhexianlu implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		
		//需要财务审核之前的审核步骤中传递creator的流程变量
		RuntimeService runtimeService = (RuntimeService)webApplicationContext.getBean(RuntimeService.class);
		String admin = (String) runtimeService.getVariable(delegateTask.getExecutionId(), "admin"); //提交申请人
		delegateTask.addCandidateUser(admin);
		
		/**微信推送**/
		List<String> userIds = new ArrayList<>();
		userIds.add(admin);
		String content = "您有新工作需要审核，请尽快完成。";
		SendMessageQyWx.sendWxMessage(QyWxConstants.CAI_GOU_BU_QYWX_APP_AGENT_ID, userIds , null, content );
	
		
	}

}
