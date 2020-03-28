package com.hongyu.listener;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.Filter;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyDepartmentModelService;

public class StockInListenerStorageKeeper
  implements TaskListener
{
  public void notify(DelegateTask delegateTask)
  {
    WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
    DepartmentService departmentService = (DepartmentService)webApplicationContext.getBean(DepartmentService.class);
    HyDepartmentModelService hyDepartmentModelService = (HyDepartmentModelService)webApplicationContext.getBean(HyDepartmentModelService.class);
    
    HistoryService historyService = (HistoryService)webApplicationContext.getBean(HistoryService.class);
    
    List<HistoricTaskInstance> list = 
      ((HistoricTaskInstanceQuery)historyService.createHistoricTaskInstanceQuery().processInstanceId(delegateTask.getProcessInstanceId()).orderByHistoricTaskInstanceEndTime().desc())
      .list();
    
    addCandidate(delegateTask, "总公司商贸部采购部", "admin/business/purchase/storekeeper", hyDepartmentModelService, departmentService);
    
//    String taskName = ((HistoricTaskInstance)list.get(0)).getName();
//		if ("采购部经理审核".equals(taskName)) {
//			addCandidate(delegateTask, "总公司商贸部财务部", "admin/business/purchase/financer", hyDepartmentModelService,
//					departmentService);
//		} else if ("商贸财务预付款".equals(taskName)) {
//			addCandidate(delegateTask, "总公司商贸部渠道销售部", "admin/business/purchase/qudao/purchasequdao",
//					hyDepartmentModelService, departmentService);
//		} else if ("渠道销售部设置提成比例".equals(taskName)) {
//			addCandidate(delegateTask, "总公司商贸部采购部", "admin/business/purchase/purchaseemployee",
//					hyDepartmentModelService, departmentService);
//		} else if ("采购部填写物流信息".equals(taskName)) {
//			addCandidate(delegateTask, "总公司商贸部采购部", "admin/business/purchase/storekeeper", hyDepartmentModelService,
//					departmentService);
//		} else if ("库管入库".equals(taskName)) {
//			addCandidate(delegateTask, "总公司商贸部采购部", "admin/business/purchase/purchaseemployee",
//					hyDepartmentModelService, departmentService);
//		} else if ("采购部提请结算".equals(taskName)) {
//			addCandidate(delegateTask, "总公司商贸部财务部", "admin/business/purchase/financer", hyDepartmentModelService,
//					departmentService);
//		}
  }
  
  public void addCandidate(DelegateTask delegateTask, String departMentModel, String authority, HyDepartmentModelService hyDepartmentModelService, DepartmentService departmentService)
  {
    HyDepartmentModel model = (HyDepartmentModel)hyDepartmentModelService.find(departMentModel);
    List<Filter> filters = new ArrayList();
    Filter filter = Filter.eq("hyDepartmentModel", model);
    filters.add(filter);
    List<Department> departments = departmentService.findList(null, filters, null);
    List<HyAdmin> hyAdmins = new ArrayList();
    if (departments.size() > 0) {
      for (Department department : departments) {
        hyAdmins.addAll(department.getHyAdmins());
      }
    }
    for (HyAdmin admin : hyAdmins)
    {
      HyRole role = admin.getRole();
      if (role.getHyRoleAuthorities().size() > 0) {
        for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
          if (authority.equals(hyRoleAuthority.getAuthoritys().getRequestUrl()))
          {
            delegateTask.addCandidateUser(admin.getUsername());
            break;
          }
        }
      }
    }
  }
}
