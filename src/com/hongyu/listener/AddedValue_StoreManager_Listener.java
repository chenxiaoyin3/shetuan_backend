package com.hongyu.listener;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.hongyu.entity.Store;
import com.hongyu.service.StoreService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;

/** 增值业务审核 - 指定门店经理 */
@SuppressWarnings("serial")
public class AddedValue_StoreManager_Listener implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {

		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		StoreService  storeService = webApplicationContext.getBean(StoreService.class);
		
		//获取storeId
		RuntimeService runtimeService = (RuntimeService)webApplicationContext.getBean(RuntimeService.class);
		
		try{
			Long storeId= (Long) runtimeService.getVariable(delegateTask.getExecutionId(), "storeId"); //获取门店id
			List<String> userIds = new ArrayList<>();
			if (requestAttributes != null) {
				Store store = storeService.find(storeId);
				delegateTask.addCandidateUser(store.getHyAdmin().getUsername());  //指定门店经理
				
				/**发送微信推送**/
				userIds.add(store.getHyAdmin().getUsername());
				String content = "您有新工作需要审核，请尽快完成。";
				SendMessageQyWx.sendWxMessage(QyWxConstants.HONG_YU_MEN_DIAN_QYWX_APP_AGENT_ID, userIds , null, content );
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
