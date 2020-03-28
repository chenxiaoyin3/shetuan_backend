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
 * 向供应商付款审核 - 指定供应商
 */
@SuppressWarnings("serial")
public class PayServicerReview_Servicer_Listener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        RuntimeService runtimeService = (RuntimeService) webApplicationContext.getBean(RuntimeService.class);
        try {
            // 获取流程变量 - 产品计调
            String supplierId = (String) runtimeService.getVariable(delegateTask.getExecutionId(), "supplierId");
            delegateTask.addCandidateUser(supplierId);
            /****调用微信接口 发送微信推送*****/
            List<String> users = new ArrayList<>();
            users.add(supplierId);
            String content = "您有新工作需要审核，请尽快完成。";
            SendMessageQyWx.sendWxMessage(QyWxConstants.WAI_BU_GONG_YING_SHANG_QYWX_APP_AGENT_ID, users, null, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
