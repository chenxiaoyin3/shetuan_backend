package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.Pageable;
import com.hongyu.entity.*;
import com.hongyu.service.*;
import com.hongyu.util.ActivitiUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.dao.BalanceDueApplyDao;
import com.hongyu.entity.CommonShenheedu.Eduleixing;

/**
 * @author xyy
 */
@Service("balanceDueApplyServiceImpl")
public class BalanceDueApplyServiceImpl extends BaseServiceImpl<BalanceDueApply, Long> implements BalanceDueApplyService {

    @Resource(name = "branchPrePayDetailServiceImpl")
    private BranchPrePayDetailService branchPrePayDetailService;

    @Resource(name = "prePaySupplyServiceImpl")
    PrePaySupplyService prePaySupplyService;

    @Resource(name = "departmentServiceImpl")
    private DepartmentService departmentService;

    @Resource(name = "hyRoleServiceImpl")
    HyRoleService hyRoleService;

    @Resource(name = "hyPayablesElementServiceImpl")
    private HyPayablesElementService hyPayablesElementService;

    @Resource(name = "payServicerServiceImpl")
    private PayServicerService payServicerService;

    @Resource(name = "commonEdushenheServiceImpl")
    private CommonEdushenheService commonEdushenheService;

    @Resource(name = "balanceDueApplyItemServiceImpl")
    private BalanceDueApplyItemService balanceDueApplyItemService;

    @Resource(name = "hyAdminServiceImpl")
    private HyAdminService hyAdminService;

    @Resource(name = "branchPrePayServiceImpl")
    private BranchPrePayService branchPrePayService;

    @Resource(name = "bankListServiceImpl")
    private BankListService bankListService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource(name = "balanceDueApplyDaoImpl")
    BalanceDueApplyDao dao;

    @Resource(name = "balanceDueApplyDaoImpl")
    public void setBaseDao(BalanceDueApplyDao dao) {
        super.setBaseDao(dao);
    }

    /**
     * 付尾款审核 - 提交
     */
    @Override
    public Json addApply(List<HashMap<String, Object>> list, HttpSession session) {
        Json json = new Json();
        String username = (String) session.getAttribute(CommonAttributes.Principal);
        HyAdmin hyAdmin = hyAdminService.find(username);
        Department department = hyAdmin.getDepartment();
        Long institutionId = department.getId();
        BalanceDueApply balanceDueApply = new BalanceDueApply();
        balanceDueApply.setInstitutionId(institutionId);
        Long id = (Long) list.get(0).get("id");
        HyPayablesElement hyPayablesElement = hyPayablesElementService.find(id);
        balanceDueApply.setSupplierElement(hyPayablesElement.getHySupplierElement());
        balanceDueApply.setOperator(hyAdmin);
        balanceDueApply.setCreateTime(new Date());
        // 1 审核中-未付
        balanceDueApply.setStatus(1);
        // 1 待 (总公司/分公司) 产品中心经理审核
        balanceDueApply.setStep(1);
        this.save(balanceDueApply);

        BigDecimal money = BigDecimal.ZERO;
        BigDecimal money_use_pre_pay_sum = BigDecimal.ZERO;
        for (HashMap<String, Object> map : list) {
            Long i = (Long) map.get("id");
            BigDecimal prePayMoney = (BigDecimal) map.get("prePayMoney");
            BigDecimal transferMoney = (BigDecimal) map.get("transferMoney");
            HyPayablesElement pe = hyPayablesElementService.find(i);
            HyGroup hyGroup = pe.getHyGroup();

            BalanceDueApplyItem item = new BalanceDueApplyItem();
            item.setBalanceDueApplyId(balanceDueApply.getId());
            item.setGroupId(hyGroup.getId());
            item.setProductId(hyGroup.getGroupLinePn());
            item.setLaunchDate(hyGroup.getStartDay());
            item.setLineName(hyGroup.getGroupLineName());
            // 建团计调
            item.setOperator(hyGroup.getCreator());
            // 报名人数
            item.setAmount(hyGroup.getSignupNumber());
            item.setUsePrePay(prePayMoney);
            item.setPayMoney(transferMoney);
            item.setApplier(hyAdmin);
            item.setCreateTime(new Date());
            item.setHyPayablesElementId(pe.getId());
            balanceDueApplyItemService.save(item);
            money = money.add(transferMoney);
            money_use_pre_pay_sum = money_use_pre_pay_sum.add(prePayMoney);

            // 在申请提交之后，直接修改已付和欠付      2018-07-14  xyy
            pe.setPaid(hyPayablesElement.getPaid().add(prePayMoney).add(transferMoney));
            pe.setDebt(hyPayablesElement.getDebt().subtract(prePayMoney).subtract(transferMoney));
            hyPayablesElementService.update(pe);

        }

        // 如果使用了预付款， 提交申请后直接修改预付款
        if (money_use_pre_pay_sum != null && (money_use_pre_pay_sum.doubleValue() > 0)) {
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("supplierElementId", hyPayablesElement.getHySupplierElement().getId()));
            filters.add(Filter.eq("departmentId", department.getId()));
            List<BranchPrePay> list2 = branchPrePayService.findList(null, filters, null);
            BranchPrePay branchPrePay = list2.get(0);
            branchPrePay.setPrePayBalance(branchPrePay.getPrePayBalance().subtract(money_use_pre_pay_sum));
            branchPrePayService.update(branchPrePay);
        }
        balanceDueApply.setMoney(money);

        HashMap<String, Object> map = new HashMap<>();
        // 传入申请人的部门id
        map.put("institutionId", institutionId);

        /**判断提交人是否经理,如果是经理跳过产品中心经理审核进入下一步,如果不是经理,进入经理审核步骤.added by GSbing,20180813*/
        HyRole hyRole = hyAdmin.getRole();
        if (hyRole.getName().contains("经理")) {
            map.put("role", "isJingli"); // 传入申请人的角色是否经理
            boolean isBranch = department.getName().startsWith("总公司") ? false : true;
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("eduleixing", isBranch ? Eduleixing.balanceDueBranchLimit : Eduleixing.balanceDueCompanyLimit));
            List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
            BigDecimal limit = edu.get(0).getMoney();
            // 超过额度
            if (balanceDueApply.getMoney().doubleValue() > limit.doubleValue()) {
                map.put("money", "more");
                // 2:待(总公司/分公司)副总(限额)审核
                balanceDueApply.setStep(2);
            } else {
                map.put("money", "less");
                // 3:待总公司财务审核
                balanceDueApply.setStep(3);
            }
        } else {
            // 传入申请人的角色是否经理
            map.put("role", "notJingli");
        }

        String departmentFullName = department.getFullName();
        // 如果是总公司
        if (!departmentFullName.contains("分公司")) {
            ProcessInstance pi = runtimeService.startProcessInstanceByKey("banlanceDueCompany");
            Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
            Authentication.setAuthenticatedUserId(username);
            taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
            taskService.complete(task.getId(), map);
            balanceDueApply.setProcessInstanceId(pi.getProcessInstanceId());
        } else {
            // 如果是分公司
            map.put("institutionId", institutionId);
            ProcessInstance pi = runtimeService.startProcessInstanceByKey("banlanceDueBranch");
            Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
            Authentication.setAuthenticatedUserId(username);
            taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
            taskService.complete(task.getId(), map);
            balanceDueApply.setProcessInstanceId(pi.getProcessInstanceId());
        }
        this.update(balanceDueApply);
        json.setMsg("操作成功");
        json.setSuccess(true);
        return json;
    }

    /**
     * 付尾款审核 - 操作
     */
    @Override
    public Json insertBalanceDueApply(Long id, String comment, Integer state, HttpSession session) throws Exception {
        Json json = new Json();
        String username = (String) session.getAttribute(CommonAttributes.Principal);
        BalanceDueApply balanceDueApply = this.find(id);
        String processInstanceId = balanceDueApply.getProcessInstanceId();
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();

        // 判断是分公司或总公司
        Long institutionId = balanceDueApply.getInstitutionId();
        Department department = departmentService.find(institutionId);
        boolean isBranch = department.getName().startsWith("总公司") ? false : true;

        HashMap<String, Object> map = new HashMap<>();
        if (state == 1) { // 审核通过
            map.put("result", "tongguo");
            if (balanceDueApply.getStep() == 1) { // 1:待(总公司/分公司)产品中心经理审核
                List<Filter> filters = new ArrayList<>();
                filters.add(Filter.eq("eduleixing", isBranch ? Eduleixing.balanceDueBranchLimit : Eduleixing.balanceDueCompanyLimit));
                List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
                BigDecimal limit = edu.get(0).getMoney();

                if (balanceDueApply.getMoney().doubleValue() > limit.doubleValue()) { // 超过额度
                    map.put("money", "more");
                    balanceDueApply.setStep(2);  // 2:待(总公司/分公司)副总(限额)审核
                } else {
                    map.put("money", "less");
                    balanceDueApply.setStep(3);  // 3:待总公司财务审核
                }
            } else if (balanceDueApply.getStep() == 2) { // 2:待(总公司/分公司)副总(限额)审核
                balanceDueApply.setStep(3);  // 3:待总公司财务审核
            } else if (balanceDueApply.getStep() == 3) {

                if (balanceDueApply.getMoney().doubleValue() > 0) {
                    // 状态 置为"2:已通过-未付"
                    balanceDueApply.setStatus(2);

                    // 生成  总公司财务中心 - 待付款
                    PayServicer payServicer = new PayServicer();
                    payServicer.setDepartmentId(institutionId);
                    payServicer.setReviewId(id);
                    // 0 未付
                    payServicer.setHasPaid(0);
                    // 1:预付款 2:T+N 3:提前打款 4:旅游元素供应商尾款 5:向酒店/门票/酒加景供应商付款 6:江泰预充值
                    payServicer.setType(4);
                    payServicer.setApplyDate(balanceDueApply.getCreateTime());
                    payServicer.setAppliName(balanceDueApply.getOperator().getName());
                    payServicer.setServicerId(balanceDueApply.getSupplierElement().getId());
                    payServicer.setServicerName(balanceDueApply.getSupplierElement().getName());
                    payServicer.setAmount(balanceDueApply.getMoney());
//				payServicer.setRemark(remark);
                    BankList bankList = getBankListByBalanceDueApply(balanceDueApply);
                    payServicer.setBankListId(bankList.getId());
                    payServicer.setAccountName(bankList.getAccountName());
                    payServicer.setBankName(bankList.getBankName());
                    payServicer.setBankCode(bankList.getBankCode());
                    payServicer.setBankType(bankList.getBankType() ? 0 : 1);
                    payServicer.setBankAccount(bankList.getBankAccount());
                    payServicerService.save(payServicer);
                } else {
                    // 只使用预付款,不需要进行转账(balanceDueApply.getMoney() = 0)的情况下,不需要发起待付款,且状态直接置为"3:已通过-已付"

                    // 1.修改BalanceDueApply的付款状态
                    balanceDueApply.setPayDate(new Date());
                    // 3:已通过-已付
                    balanceDueApply.setStatus(3);

                    // 2.修改BalanceDueApplyItem 和 HyPayablesElement
                    List<Filter> filters = new ArrayList<>();
                    filters.add(Filter.eq("balanceDueApplyId", balanceDueApply.getId()));
                    List<BalanceDueApplyItem> list = balanceDueApplyItemService.findList(null, filters, null);
                    BigDecimal money_use_pre_pay_sum = new BigDecimal(0);
                    Long groupId = 0L;
                    for (BalanceDueApplyItem bDueApplyItem : list) {
                        groupId = bDueApplyItem.getGroupId();
                        // 1已付
                        bDueApplyItem.setPayStatus(1);
                        balanceDueApplyItemService.update(bDueApplyItem);
                        money_use_pre_pay_sum = money_use_pre_pay_sum.add(bDueApplyItem.getUsePrePay());
                    }
                    // 4.生成预付款的冲抵记录
                    filters.clear();
                    filters.add(Filter.eq("supplierElementId", balanceDueApply.getSupplierElement().getId()));
                    filters.add(Filter.eq("departmentId", balanceDueApply.getInstitutionId()));
                    List<BranchPrePay> list2 = branchPrePayService.findList(null, filters, null);
                    BranchPrePay branchPrePay = list2.get(0);

                    BranchPrePayDetail branchPrePayDetail = new BranchPrePayDetail();
                    // 1 预付给供应商(充值)  2使用预付款(冲抵)
                    branchPrePayDetail.setType(2);
                    branchPrePayDetail.setBranchPrePayId(branchPrePay.getId());
                    branchPrePayDetail.setDate(balanceDueApply.getPayDate());
                    branchPrePayDetail.setAmount(money_use_pre_pay_sum);
                    branchPrePayDetail.setAppliname(balanceDueApply.getOperator().getUsername());
                    branchPrePayDetail.setPrePayBalance(branchPrePay.getPrePayBalance());
                    branchPrePayDetail.setGroupId(groupId);
                    branchPrePayDetailService.save(branchPrePayDetail);
                }
            }
        } else if (state == 0) {
            // 审核未通过
            map.put("result", "bohui");
            // 状态置为"4已驳回-未付"
            balanceDueApply.setStatus(4);
            // 驳回后 将提交申请时修改的已付和欠付  以及使用的预付款金额复原    20180714 xyy
            List<Filter> filters = new ArrayList<>();
            filters.add(Filter.eq("balanceDueApplyId", balanceDueApply.getId()));
            List<BalanceDueApplyItem> list = balanceDueApplyItemService.findList(null, filters, null);
            BigDecimal money_use_pre_pay_sum = new BigDecimal(0);
            for (BalanceDueApplyItem bDueApplyItem : list) {
                HyPayablesElement hyPayablesElement = hyPayablesElementService.find(bDueApplyItem.getHyPayablesElementId());
                BigDecimal sum = bDueApplyItem.getUsePrePay().add(bDueApplyItem.getPayMoney());
                hyPayablesElement.setPaid(hyPayablesElement.getPaid().subtract(sum));
                hyPayablesElement.setDebt(hyPayablesElement.getDebt().add(sum));
                hyPayablesElementService.update(hyPayablesElement);
                money_use_pre_pay_sum = money_use_pre_pay_sum.add(bDueApplyItem.getUsePrePay());
            }
            if (money_use_pre_pay_sum != null && money_use_pre_pay_sum.doubleValue() > 0) {
                List<Filter> filters2 = new ArrayList<>();
                filters.add(Filter.eq("supplierElement", balanceDueApply.getSupplierElement()));
                filters.add(Filter.eq("departmentId", department.getId()));
                List<BranchPrePay> list2 = branchPrePayService.findList(null, filters2, null);
                BranchPrePay branchPrePay = list2.get(0);
                branchPrePay.setPrePayBalance(branchPrePay.getPrePayBalance().add(money_use_pre_pay_sum));
                branchPrePayService.update(branchPrePay);
            }
        }
        Authentication.setAuthenticatedUserId(username);
        taskService.claim(task.getId(), username);
        taskService.addComment(task.getId(), processInstanceId, (comment == null ? " " : comment) + ":" + state);
        taskService.complete(task.getId(), map);
        this.update(balanceDueApply);

        json.setSuccess(true);
        json.setMsg("操作成功");
        return json;
    }

    /**
     * (总公司、分公司)申请付尾款审核 - 列表
     */
    @Override
    public Json balanceDueApplyReviewList(Pageable pageable, String startDate, String endDate, Integer state, String username) throws Exception {
        Json j = new Json();
        int page = pageable.getPage();
        int rows = pageable.getRows();
        Map<String, Object> answer = new HashMap<>();
        List<Map<String, Object>> ans = new LinkedList<>();

        StringBuilder sql = new StringBuilder("SELECT SQL_CALC_FOUND_ROWS bda_part.id,hy_supplier_element.name, bda_part.money, hy_admin.name AS applier, bda_part.create_time,bda_part.group_id, bda_part.launch_date, bda_part.line_name,bda_part.amount, bda_part.use_pre_pay, bda_part.pay_money,(SELECT hy_group.group_line_pn FROM hy_group WHERE bda_part.group_id = hy_group.id),(SELECT name FROM hy_admin WHERE hy_admin.username = (SELECT hy_group.creator FROM hy_group WHERE bda_part.group_id = hy_group.id)), bda_part.process_instance_id FROM (SELECT bda.id,bda.supplier_id, bda.money, bda.operator_id, bda.create_time, bda.process_instance_id, bdai.group_id, bdai.launch_date, bdai.line_name, bdai.amount, bdai.use_pre_pay, bdai.pay_money FROM hy_balance_due_apply bda RIGHT JOIN hy_balance_due_apply_item bdai ON bda.id = bdai.balance_due_apply_id WHERE 1=1 ");
        if (StringUtils.isNotBlank(startDate)) {
            sql.append(" AND bda.create_time >= '");
            sql.append(startDate.substring(0, 10));
            sql.append(" ");
            sql.append("00:00:00'");
        }
        if (StringUtils.isNotBlank(endDate)) {
            sql.append(" AND bda.create_time <= '");
            sql.append(endDate.substring(0, 10));
            sql.append(" ");
            sql.append("23:59:59'");
        }
        sql.append("  AND bda.process_instance_id IN (");

        HashSet<String> taskProcessInstanceIdSet = new HashSet<>();
        if (state == null) {
            // TODO 只通过一次操作获取待办任务和已完成任务
            List<Task> tasks = ActivitiUtils.getTaskList(username, ActivitiUtils.BAN_LANCE_DUE);
            List<HistoricTaskInstance> hisTasks = ActivitiUtils.getHistoryTaskList(username, ActivitiUtils.BAN_LANCE_DUE);
            // 没有任何的待办或已办任务
            if (CollectionUtils.isEmpty(tasks) && CollectionUtils.isEmpty(hisTasks)) {
                j.setSuccess(true);
                j.setMsg("未获取到符合条件的数据");
                return j;
            }
            for (Task task : tasks) {
                sql.append(task.getProcessInstanceId());
                sql.append(",");
                taskProcessInstanceIdSet.add(task.getProcessInstanceId());
            }
            for (HistoricTaskInstance hisTask : hisTasks) {
                sql.append(hisTask.getProcessInstanceId());
                sql.append(",");
            }
        } else if (state == 0) {
             /* 搜索未完成任务*/
            List<Task> tasks = ActivitiUtils.getTaskList(username, ActivitiUtils.BAN_LANCE_DUE);
            // 没有任何待办任务
            if (CollectionUtils.isEmpty(tasks)) {
                j.setSuccess(true);
                j.setMsg("未获取到符合条件的数据");
                return j;
            }
            for (Task task : tasks) {
                sql.append(task.getProcessInstanceId());
                sql.append(",");
            }
        } else if (state == 1) {
             /*搜索已完成任务*/
            List<HistoricTaskInstance> hisTasks = ActivitiUtils.getHistoryTaskList(username, ActivitiUtils.BAN_LANCE_DUE);
            // 没有任何已办任务
            if (CollectionUtils.isEmpty(hisTasks)) {
                j.setSuccess(true);
                j.setMsg("未获取到符合条件的数据");
                return j;
            }
            for (HistoricTaskInstance hisTask : hisTasks) {
                sql.append(hisTask.getProcessInstanceId());
                sql.append(",");
            }

        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")) AS bda_part LEFT JOIN hy_supplier_element ON bda_part.supplier_id = hy_supplier_element.id LEFT JOIN hy_admin ON bda_part.operator_id = hy_admin.username");
        // 按BalanceDueApply的id降序
        sql.append(" ORDER BY bda_part.id DESC LIMIT ");
        // 分页
        sql.append((page - 1) * rows);
        sql.append(",");
        sql.append(rows);

        List<Object[]> list = super.statis(sql.toString());
        // SQL_CALC_FOUND_ROWS和FOUND_ROWS()的配合使用 TODO 并发情况下对FOUND_ROWS()的结果的影响
        BigInteger total = (BigInteger) super.getSingleResultByNativeQuery("SELECT FOUND_ROWS()");
        answer.put("total", total);

        /*
         obj[0] hy_balance_due_apply表的id
         obj[1] 旅游元素供应商名称
         obj[2] 本次应付金额
         obj[3] 申请人
         obj[4] 申请日期
         obj[5] hy_group表的id
         obj[6] 发团日期
         obj[7] 线路名称
         obj[8] 人数
         obj[9] BalanceDueApplyItem的usePrePay
         obj[10] BalanceDueApplyItem的payMoney
         obj[11] 产品编号
         obj[12] 建团计调
         obj[13] 流程实例id
         */
        for (Object[] obj : list) {
            HashMap<String, Object> m = new HashMap<>();
            m.put("id", obj[0]);
            if (null == state) {
                String processInstanceId = (String) obj[13];
                // 当筛选“全部”的情况，只要当前待办任务的流程实例id集合中，包含了该任务的流程实例id，则说明该任务处于待办状态 state 0:待审核  1:已审核
                m.put("state", taskProcessInstanceIdSet.contains(processInstanceId) ? 0 : 1);
            } else {
                m.put("state", state);
            }
            m.put("moneySum", obj[2]);
            // 获取hy_balance_due_apply对应的hy_balance_due_apply_item,并对use_pre_pay和pay_money分别求和
            Object[] o = getUsePrePaySumAndPayMoneySum(Long.parseLong(String.valueOf(obj[0])));
            m.put("prePayMoneySum", o[0]);
            m.put("transferMoneySum", o[1]);
            m.put("applier", obj[3]);
            m.put("createTime", obj[4]);
            m.put("supplierName", obj[1]);
            //------- 1 ——> n -------
            BigDecimal prePayMoney = (BigDecimal) obj[9];
            BigDecimal transferMoney = (BigDecimal) obj[10];
            m.put("prePayMoney", prePayMoney);
            m.put("transferMoney", transferMoney);
            m.put("money", prePayMoney.add(transferMoney));
            m.put("sn", obj[11]);
            m.put("launchDate", obj[6]);
            m.put("lineName", obj[7]);
            m.put("amount", obj[8]);
            m.put("creator", obj[12]);
            ans.add(m);
        }
        answer.put("pageNumber", page);
        answer.put("pageSize", rows);
        answer.put("rows", ans);
        j.setSuccess(true);
        if (ans.size() == 0) {
            j.setMsg("未获取到符合条件的数据");
        } else {
            j.setMsg("获取成功");
        }
        j.setObj(answer);
        return j;
    }

    /**
     * 获取hy_balance_due_apply对应的hy_balance_due_apply_item,并对use_pre_pay和pay_money分别求和
     */
    private Object[] getUsePrePaySumAndPayMoneySum(Long id) throws Exception {
        List<Object[]> usePrePaySumAndPayMoneySum = super.statis("SELECT SUM(use_pre_pay), SUM(pay_money) FROM hy_balance_due_apply_item WHERE balance_due_apply_id = " + id);
        if (CollectionUtils.isEmpty(usePrePaySumAndPayMoneySum)) {
            throw new Exception("id" + id + "无对应的BalanceDueApplyItem");
        }
        return usePrePaySumAndPayMoneySum.get(0);
    }

    /**
     * 根据BalanceDueApply获取BankList
     */
    private BankList getBankListByBalanceDueApply(BalanceDueApply balanceDueApply) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT hy_supplier_contract.id, hy_supplier_contract.bank_list FROM hy_balance_due_apply_item LEFT JOIN hy_payables_element ON hy_balance_due_apply_item.payables_element_id = hy_payables_element.id LEFT JOIN hy_supplier_contract ON hy_payables_element.contract_id = hy_supplier_contract.id WHERE balance_due_apply_id = ");
        sql.append(balanceDueApply.getId());
        List<Object[]> list = super.statis(sql.toString());
        if (CollectionUtils.isEmpty(list)) {
            throw new Exception(balanceDueApply.getId() + "对应的HySupplierElement没有相应的bankList");
        }
        Long id = null;
        // TODO 多个HyPayablesElement的contractId可能会不同？
        for (Object[] obj : list) {
            if (obj[1] != null) {
                id = Long.parseLong(String.valueOf(obj[1]));
            }
        }
        // 1)直接从HySupplierElement获取银行帐号
        if (id == null) {
            return balanceDueApply.getSupplierElement().getBankList();
        }
        // 2)从balanceDueApply的hy_payables_element的hy_supplier_contract获取合同对应的银行帐号
        return bankListService.find(id);
    }
}