package com.hongyu.listener;

import static com.hongyu.util.Constants.XiaotuanAudit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.Filter;
import com.hongyu.entity.Department;
import com.hongyu.entity.GroupXiaotuan;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.entity.Store;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.StoreService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

public class StoreAuditXiaotuanListener implements TaskListener {
	
	
	@Override
	public void notify(DelegateTask delegateTask) {
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		
		//需要财务审核之前的审核步骤中传递门店的流程变量
		RuntimeService runtimeService = (RuntimeService)webApplicationContext.getBean(RuntimeService.class);
		Set<Department> stores = (Set<Department>) runtimeService.getVariable(delegateTask.getExecutionId(), "storesNo"); //指定要审核的报名门店
//		Set<Store> stores=(HashSet)runtimeService.getVariable(delegateTask.getExecutionId(), "storesNo"); //指定要审核的报名门店
		HyOrderItemService hyOrderItemService=webApplicationContext.getBean(HyOrderItemService.class);
		HyOrderService hyOrderService=webApplicationContext.getBean(HyOrderService.class);
		StoreService storeService=webApplicationContext.getBean(StoreService.class);
//		List<Filter> filters=new ArrayList<Filter>();
//		filters.add(Filter.ne("refundstatus", 1)); 
//		filters.add(Filter.ne("refundstatus", 2)); 
//		filters.add(Filter.ne("refundstatus", 4));
//		filters.add(Filter.eq("checkstatus", 1));
//		filters.add(Filter.eq("status", 3));
//		filters.add(Filter.eq("groupId", groupId));
//		List<HyOrder> hyOrders = hyOrderService.findList(null, filters, null);
//		filters.clear();
//		Set<Department> stores = new HashSet<>(); //所有报名的e门店					
//		for (HyOrder temp : hyOrders) { 		
//			if (temp.getStoreId() != null) {
//				Store store = storeService.find(temp.getStoreId());
//				if(store.getDepartment() != null) {
//					stores.add(store.getDepartment()); //将部门作为流程变量
//				}
//			}
//		}
		Set<HyAdmin> admins = new HashSet<>();
		for (Department temp : stores) {
			admins.addAll(temp.getHyAdmins()); //找到门店下所有员工
		}
	
		List<String> userIds = new ArrayList<>();
		for(HyAdmin admin : admins) {
			HyRole role = admin.getRole();
			if (role.getHyRoleAuthorities().size() > 0) {
				for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
					if (XiaotuanAudit.equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
						delegateTask.addCandidateUser(admin.getUsername());
						userIds.add(admin.getUsername());
						break;
					}
				}
			}
		}
		String content = "您有新工作需要审核，请尽快完成。";
		int agentId = QyWxConstants.HONG_YU_MEN_DIAN_QYWX_APP_AGENT_ID;
		SendMessageQyWx.sendWxMessage(agentId, userIds , null, content );
	}
}
