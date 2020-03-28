package com.hongyu.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.hongyu.Filter;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.entity.Store;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.StoreService;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;

/**
 * 增值业务审核 - 指定分公司财务审核人
 */
@SuppressWarnings("serial")
public class AccountantReview_addedValue_Listener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {

        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        DepartmentService departmentService = webApplicationContext.getBean(DepartmentService.class);
        HyDepartmentModelService hyDepartmentModelService = webApplicationContext.getBean(HyDepartmentModelService.class);
        StoreService storeService = webApplicationContext.getBean(StoreService.class);
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();

        if (requestAttributes != null) {
            // 通过storeId获取分公司id
            RuntimeService runtimeService = (RuntimeService) webApplicationContext.getBean(RuntimeService.class);
            // 获取门店id
            Long storeId = (Long) runtimeService.getVariable(delegateTask.getExecutionId(), "storeId");
            Store store = storeService.find(storeId);
            Department department = store.getDepartment();
            String[] treePaths = department.getTreePath().split(",");
            // 分公司departmentId
            Long id = Long.parseLong(treePaths[2]);

            HyDepartmentModel dm = hyDepartmentModelService.find("分公司财务部");
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("hyDepartmentModel", dm));
            filters.add(Filter.eq("treePath", ",1," + id + ","));
            List<Department> departments = departmentService.findList(null, filters, null);
            Set<HyAdmin> admins = departments.get(0).getHyAdmins();

            if (admins == null || admins.size() == 0) {
                throw new NullPointerException("无法获取下一阶段审核人，请重新提交");
            } else {
                List<String> users = new ArrayList<>();
                for (HyAdmin admin : admins) {
                    HyRole role = admin.getRole();
                    if (role.getHyRoleAuthorities().size() > 0) {
                        for (HyRoleAuthority hyRoleAuthority : role.getHyRoleAuthorities()) {
                            if ("admin/addedValue".equals(hyRoleAuthority.getAuthoritys().getRequestUrl())) {
                                delegateTask.addCandidateUser(admin.getUsername());
                                users.add(admin.getUsername());
                                break;
                            }
                        }
                    }
                }
                /**发送微信提醒**/
                String message = "财务部：您有新工作需要审核，请尽快完成。";
                SendMessageQyWx.sendWxMessage(QyWxConstants.FEN_GONG_SI_QYWX_APP_AGENT_ID, users, null, message);
            }
        }
    }
}
