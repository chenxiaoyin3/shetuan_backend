package com.hongyu.util;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

/**
 * @author xyy
 * */
public class ActivitiUtils {

	/** 外部供应商打款单的proDefId 包括payServicePre和payServicePreTN两种情况*/
	public static final String PAY_SERVICE_PRE = "payServicePre";
	/** 付尾款的proDefId 包括banlanceDueCompany和banlanceDueBranch两种情况*/
	public static final String BAN_LANCE_DUE = "banlanceDue";
	/** 门店交押金的proDefId*/
	public static final String JIAO_YA_JIN= "jiaoyajin";


	// TODO 使用工具类返回WebApplicationContext TaskService HistoryService的单例

	/**
	 * 根据任务候选人 流程定义前缀获取待办任务
	 * */
	public static List<Task> getTaskList(String candidate, String procDefId) throws Exception{
		StringBuilder taskSQL = new StringBuilder("SELECT * FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '");
		taskSQL.append(candidate);
		taskSQL.append("') AND rt.PROC_DEF_ID_ LIKE '");
		taskSQL.append(procDefId);
		taskSQL.append("%'");
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		TaskService taskService = (TaskService) webApplicationContext.getBean(TaskService.class);
		return taskService.createNativeTaskQuery().sql(taskSQL.toString()).list();
	}

	/**
	 * 根据任务办理人 流程定义前缀获取已办任务
	 * */
	public static List<HistoricTaskInstance> getHistoryTaskList(String assignee, String proDefId) throws Exception{
		StringBuilder hisTaskSQL = new StringBuilder("SELECT * FROM ACT_HI_TASKINST  ht WHERE ht.ASSIGNEE_ = '");
		hisTaskSQL.append(assignee);
		hisTaskSQL.append("' AND ht.PROC_DEF_ID_ LIKE '");
		hisTaskSQL.append(proDefId);
		hisTaskSQL.append("%'");
		/* 当任务开启时  ACT_RU_TASK和ACT_HI_TASKINST都会写入数据
		   当任务完成时  ACT_RU_TASK中相应数据被删除 ACT_HI_TASKINST中包括END_TIME_  DURATION_  DELETE_REASON_等字段被更新
		 */
		hisTaskSQL.append(" AND ht.DELETE_REASON_ = 'completed'");
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		HistoryService historyService = (HistoryService) webApplicationContext.getBean(HistoryService.class);
		return historyService.createNativeHistoricTaskInstanceQuery().sql(hisTaskSQL.toString()).list();
	}
}
