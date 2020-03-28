package com.hongyu.listener;

import com.hongyu.Filter;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyAuthority;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class StockInListenerFull
  implements TaskListener
{
  public void notify(DelegateTask delegateTask)
  {
    WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    
    DepartmentService departmentService = (DepartmentService)webApplicationContext.getBean(DepartmentService.class);
    HyDepartmentModelService hyDepartmentModelService = 
      (HyDepartmentModelService)webApplicationContext.getBean(HyDepartmentModelService.class);
    
    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
    
    String username = (String)request.getSession().getAttribute("principal");
    HyAdminService hyAdminService = (HyAdminService)webApplicationContext.getBean(HyAdminService.class);
    HyAdmin hyAdmin = (HyAdmin)hyAdminService.find(username);
    Set<HyRoleAuthority> hyRoleAuthorities = hyAdmin.getRole().getHyRoleAuthorities();
    
    boolean isManager = false;boolean isFinance = false;boolean isSaler = false;boolean isEmployee = false;boolean isWarehouser = false;
    for (HyRoleAuthority h : hyRoleAuthorities)
    {
      String requestURL = h.getAuthoritys().getRequestUrl();
      if ("admin/business/purchase/purchaseemployee".equals(requestURL)) {
        isManager = true;
      } else if ("admin/business/purchase/purchasemanager".equals(requestURL)) {
        isFinance = true;
      } else if ("admin/business/purchase/qudao/purchasequdao".equals(requestURL)) {
        isSaler = true;
      } else if ("admin/business/purchase/storekeeper".equals(requestURL)) {
        isEmployee = true;
      } else if ("admin/business/purchase/financer".equals(requestURL)) {
        isWarehouser = true;
      }
    }
    if (isManager) {
      addCandidate(delegateTask, "总公司商贸部财务部", "admin/business/purchase/financer", hyDepartmentModelService, departmentService);
    } else if (isFinance) {
      addCandidate(delegateTask, "总公司商贸部渠道销售部", "admin/business/purchase/qudao/purchasequdao", hyDepartmentModelService, departmentService);
    } else if (isSaler) {
      addCandidate(delegateTask, "总公司商贸部采购部", "admin/business/purchase/purchaseemployee", hyDepartmentModelService, departmentService);
    } else if (isEmployee) {
      addCandidate(delegateTask, "总公司商贸部采购部", "admin/business/purchase/storekeeper", hyDepartmentModelService, departmentService);
    }
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
