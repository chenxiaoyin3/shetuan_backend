package com.hongyu.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.dao.HySubscribeTicketDao;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HySubscribeTicket;
import com.hongyu.entity.HySubscribeTicket.SaleStatus;
import com.hongyu.entity.HySupplier;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySubscribeTicketService;
import com.hongyu.util.Constants.AuditStatus;

@Service("hySubscribeTicketServiceImpl")
public class HySubscribeTicketServiceImpl extends BaseServiceImpl<HySubscribeTicket, Long>
		implements HySubscribeTicketService {
	@Resource
	private TaskService taskService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name = "commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;
	
	@Resource(name = "hySubscribeTicketDaoImpl")
	HySubscribeTicketDao dao;

	@Resource(name = "hySubscribeTicketDaoImpl")
	public void setBaseDao(HySubscribeTicketDao dao) {
		super.setBaseDao(dao);
	}
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Override
	public Json addSubscribeTicket(HySubscribeTicket hySubscribeTicket, HttpSession session) throws Exception {
		Json json = new Json();
		
		// 新建前对价格日期是否重叠的判断
		
		
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);
		HySupplier hySupplier = hyAdmin.getLiableContracts().iterator().next().getHySupplier();
		
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.in("type", SequenceTypeEnum.SubscribeTicket));
		Long value = 0L;
		synchronized (this) {
			List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
			CommonSequence c = ss.get(0);
			if (c.getValue() >= 9999) {
				c.setValue(0l);
			}
			value = c.getValue() + 1;
			c.setValue(value);
			commonSequenceService.update(c);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String nowaday = sdf.format(new Date());
		String sn = "RGMP-" +  nowaday + "-" + String.format("%04d", value);
		
		hySubscribeTicket.setCreater(hyAdmin); //创建人
		hySubscribeTicket.setSupplier(hySupplier);
		hySubscribeTicket.setSn(sn);
		hySubscribeTicket.setCreateTime(new Date());
		hySubscribeTicket.setAuditStatus(AuditStatus.unsubmitted); // 审核状态 未提交
		hySubscribeTicket.getSaleStatus();
		hySubscribeTicket.setSaleStatus(SaleStatus.weishangjia); // 上架状态 未上架
		
		this.save(hySubscribeTicket);
		
		json.setSuccess(true);
		json.setMsg("操作成功");
		
		return json;
	}

	@Override
	public Json insertSubscribeTicketAudit(Long id, HttpSession session) throws Exception {
		Json json = new Json();
		
		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		HyAdmin hyAdmin = hyAdminService.find(username);
		
		HySubscribeTicket hySubscribeTicket = this.find(id);
		AuditStatus auditStatus = hySubscribeTicket.getAuditStatus();
		if (auditStatus == AuditStatus.unsubmitted) { // 首次提交 需要启动流程
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("subscribeTicket");
			// 根据流程实例Id查询任务
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			hySubscribeTicket.setAuditStatus(AuditStatus.auditing);
			hySubscribeTicket.setApplyTime(new Date());
			hySubscribeTicket.setProcessInstanceId(pi.getProcessInstanceId());
			this.update(hySubscribeTicket);
			
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), hySubscribeTicket.getProcessInstanceId(), " :1");
			taskService.complete(task.getId());
		} else if (auditStatus == AuditStatus.notpass) { // 被驳回后重新提交
			Task task = taskService.createTaskQuery().processInstanceId(hySubscribeTicket.getProcessInstanceId()).singleResult();
			hySubscribeTicket.setAuditStatus(AuditStatus.auditing);
			hySubscribeTicket.setApplyTime(new Date()); // 重新提交后重置申请日期?
			this.update(hySubscribeTicket);
			
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), hySubscribeTicket.getProcessInstanceId(), " :1");
			taskService.complete(task.getId());
		}
		json.setSuccess(true);
		json.setMsg("操作成功");
		
		return json;
	}
}