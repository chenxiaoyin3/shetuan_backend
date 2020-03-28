package com.hongyu.controller.hzj03.audit;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.util.ActivitiUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.DepositStore;
import com.hongyu.entity.DepositStoreBranch;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.ReceiptDepositStore;
import com.hongyu.entity.ReceiptDepositStoreBranch;
import com.hongyu.entity.ReceiptDetail;
import com.hongyu.entity.ReceiptDetailBranch;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreApplication;
import com.hongyu.service.DepositStoreBranchService;
import com.hongyu.service.DepositStoreService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.ReceiptDepositStoreBranchService;
import com.hongyu.service.ReceiptDepositStoreService;
import com.hongyu.service.ReceiptDetailBranchService;
import com.hongyu.service.ReceiptDetailsService;
import com.hongyu.service.StoreApplicationService;
import com.hongyu.service.StoreService;

/** 财务 - 审核 - 门店押金 */
@Controller
@RequestMapping("admin/accountant/storeDeposit")
public class AccountantReview_StoreDeposit_Controller {
	
	@Resource(name = "receiptDetailBranchServiceImpl")
	private ReceiptDetailBranchService receiptDetailBranchService;
	
	@Resource(name = "receiptDepositStoreBranchServiceImpl")
	private ReceiptDepositStoreBranchService receiptDepositStoreBranchService;

	@Resource(name = "depositStoreServiceImpl")
	private DepositStoreService depositStoreService;
	
	@Resource(name = "depositStoreBranchServiceImpl")
	private DepositStoreBranchService depositStoreBranchService;

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	@Resource(name = "receiptDetailsServiceImpl")
	private ReceiptDetailsService receiptDetailsService;

	@Resource(name = "receiptDepositStoreServiceImpl")
	private ReceiptDepositStoreService reeceiptDepositStoreService;

	@Resource(name = "storeApplicationServiceImpl")
	private StoreApplicationService storeApplicationService;

	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;

	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;

	/** 门店押金审核-列表 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json storePreSaveReviewList(Pageable pageable, Integer state, String startTime, String endTime, HttpSession session) {
		Json j = new Json();
		int page = pageable.getPage();
		int rows = pageable.getRows();
        String username = (String) session.getAttribute(CommonAttributes.Principal);
		Map<String, Object> answer = new HashedMap();
		List<Map<String, Object>> ans = new LinkedList<>();
		// TODO 子查询优化
		StringBuilder sql = new StringBuilder("SELECT sa.id, sa.createtime, (SELECT hy_store.store_name FROM hy_store WHERE hy_store.id = sa.store_id) AS store_name, (SELECT hy_admin.name FROM hy_admin WHERE hy_admin.username = sa.operator) AS name,sa.money,sa.status,sa.process_instance_id FROM hy_store_application sa WHERE type = 1");
		if(StringUtils.isNotBlank(startTime)){
		    sql.append(" AND sa.createtime >= '");
		    sql.append(startTime.substring(0, 10));
		    sql.append(" ");
		    sql.append("00:00:00'");
        }
        if (StringUtils.isNotBlank(endTime)) {
            sql.append(" AND  sa.createtime <= '");
            sql.append(endTime.substring(0, 10));
            sql.append(" ");
            sql.append("23:59:59'");
        }
        // 筛选条件: 审批状态
        sql.append(" AND sa.process_instance_id IN (");
        HashSet<String> taskProcessInstanceIdSet = new HashSet<>();
        try{
            if(state == null){
                // TODO 只通过一次操作获取待办任务和已完成任务
                List<Task> tasks = ActivitiUtils.getTaskList(username, ActivitiUtils.JIAO_YA_JIN);
                List<HistoricTaskInstance> hisTasks = ActivitiUtils.getHistoryTaskList(username, ActivitiUtils.JIAO_YA_JIN);
                // 没有任何待办或已办任务
                if(CollectionUtils.isEmpty(tasks) && CollectionUtils.isEmpty(hisTasks)){
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
            } else if(state == 0){
                List<Task> tasks = ActivitiUtils.getTaskList(username, ActivitiUtils.JIAO_YA_JIN);
                if(CollectionUtils.isEmpty(tasks)){
                    j.setSuccess(true);
                    j.setMsg("未获取到符合条件的数据");
                    return j;
                }
                for (Task task : tasks) {
                    sql.append(task.getProcessInstanceId());
                    sql.append(",");
                }
            } else if(state == 1){
                      /*搜索已完成任务*/
                List<HistoricTaskInstance> hisTasks = ActivitiUtils.getHistoryTaskList(username, ActivitiUtils.JIAO_YA_JIN);
                // 没有任何已办任务
                if(CollectionUtils.isEmpty(hisTasks)){
                    j.setSuccess(true);
                    j.setMsg("未获取到符合条件的数据");
                    return j;
                }
                for (HistoricTaskInstance hisTask : hisTasks) {
                    sql.append(hisTask.getProcessInstanceId());
                    sql.append(",");
                }
            }
            // 删除多余的逗号
            sql.deleteCharAt(sql.length() - 1);
            // 按hy_store_application的id降序
            sql.append(") ORDER BY sa.id DESC LIMIT ");
            // 分页
            sql.append((page - 1) * rows);
            sql.append(",");
            sql.append(rows);
            List<Object[]> list = storeApplicationService.statis(sql.toString());
            // SQL_CALC_FOUND_ROWS和FOUND_ROWS()的配合使用 TODO 并发情况下对FOUND_ROWS()的结果的影响
            BigInteger total = (BigInteger) storeApplicationService.getSingleResultByNativeQuery("SELECT FOUND_ROWS()");
            answer.put("total", total);
            /*
            id                  obj[0]
            createtime          obj[1]
            store_name          obj[2]
            name                obj[3]
            money               obj[4]
            status              obj[5]
            process_instance_id obj[6]
            */
            for(Object[] obj : list){
                HashMap<String, Object> m = new HashMap<>();
                m.put("id", obj[0]);
                if (null == state) {
                    String processInstanceId = (String) obj[6];
                    // 当筛选“全部”的情况，只要当前待办任务的流程实例id集合中，包含了该任务的流程实例id，则说明该任务处于待办状态 state 0:待审核  1:已审核
                    m.put("state", taskProcessInstanceIdSet.contains(processInstanceId) ? 0 : 1);
                } else {
                    m.put("state", state);
                }
                m.put("createDate", obj[1]);
                m.put("storeName", obj[2]);
                m.put("applicant", obj[3]);
                m.put("money", obj[4]);
                m.put("applicationStatus", obj[5]);
                ans.add(m);
            }
            answer.put("pageNumber", page);
            answer.put("pageSize", rows);
            answer.put("rows", ans);
            if (total.compareTo(BigInteger.ZERO) == 0) {
                j.setMsg("未获取到符合条件的数据");
            } else {
                j.setMsg("获取成功");
            }
            j.setObj(answer);
            j.setSuccess(true);
        }catch (Exception e){
            j.setSuccess(false);
            j.setMsg("获取失败");
            e.printStackTrace();
        }
        return j;
	}

	/** 审核详情 */
	@RequestMapping("detail/view")
	@ResponseBody
	public Json getHistoryComments(Long id) {
		Json json = new Json();
		try {
			StoreApplication storeApplication = storeApplicationService.find(id);
			String processInstanceId = storeApplication.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> list = new LinkedList<>();
			for (Comment comment : commentList) {
				Map<String, Object> map = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				String step = "";
				if (task != null) {
					step = task.getName();
				}
				map.put("step", step);
				String username = comment.getUserId();
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				map.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				if (index < 0) {
					map.put("comment", " ");
					map.put("result", 1);
				} else {
					map.put("comment", str.substring(0, index));
					map.put("result", Integer.parseInt(str.substring(index + 1)));
				}
				map.put("time", comment.getTime());

				list.add(map);
			}

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("list", list);

			// 门店信息

            obj.put("storeName", storeApplication.getStore().getStoreName());
            obj.put("applyDate", storeApplication.getCreatetime());
            obj.put("applicant", storeApplication.getOperator().getName());
            obj.put("money", storeApplication.getMoney());
            obj.put("balance", storeApplication.getBalance());

            // 付款方式  0 线上  1转账   2 刷卡  3 现金
            obj.put("payment", storeApplication.getPayment());

            if(1L == storeApplication.getPayment()){
                // 账户名称
                obj.put("accountName", storeApplication.getPayerName());
                // 银行名称
                obj.put("bankName", storeApplication.getPayerBank());
                // 银行联行号
                obj.put("bankCode", storeApplication.getPayerBankAccount());
                // 对公对私  对公是false 对私是true
                obj.put("bankType", storeApplication.getPayerBankType());
                // 帐号
                obj.put("bankAccount", storeApplication.getPayerAccount());
            }else{
                // 非转账方式 信息置空

                // 账户名称
                obj.put("accountName", "");
                // 银行名称
                obj.put("bankName", "");
                // 银行联行号
                obj.put("bankCode", "");
                // 对公对私
                obj.put("bankType", "");
                // 帐号
                obj.put("bankAccount", "");
            }


			obj.put("accessory", storeApplication.getAccessory()); // 附件信息
			
			// obj.put("remark", storeApplication.getRemark());

			json.setSuccess(true);
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}
		return json;

	}

	/** 财务审核 */
	@RequestMapping("audit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json audit(Long id, String comment, Integer state, HttpSession session) {
		Json json = new Json();

		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			StoreApplication storeApplication = storeApplicationService.find(id);
			String processInstanceId = storeApplication.getProcessInstanceId();

			if (processInstanceId == null || processInstanceId == "") {
				json.setSuccess(false);
				json.setMsg("审核出错，信息不完整，请重新申请");
			} else {
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				if (state == 1) {
					storeApplication.setApplicationStatus(3);
					
					
					// 门店押金审核通过，直接生成已收款记录,返回前台待收款记录的id
					
					Integer storeType = storeApplication.getStore().getStoreType();
					if(storeType == 0){  //0:虹宇门店
						
						// 1、在hy_receipt_deposit_store表中写数据
						ReceiptDepositStore receiptDepositStore = new ReceiptDepositStore();
						receiptDepositStore.setState(1); //
						receiptDepositStore.setstoreName(storeApplication.getStore().getStoreName());
						receiptDepositStore.setPayer(storeApplication.getPayerName());
						receiptDepositStore.setAmount(storeApplication.getMoney());
						receiptDepositStore.setDate(storeApplication.getCreatetime());

						reeceiptDepositStoreService.save(receiptDepositStore);

						// 2、在ReceiptDeatails表中写数据
						ReceiptDetail receiptDetail = new ReceiptDetail();
						receiptDetail.setReceiptType(1); // 1:门店押金-收款
						receiptDetail.setReceiptId(receiptDepositStore.getId());
						receiptDetail.setAmount(storeApplication.getMoney());
						receiptDetail.setPayMethod(1L); // 付款方式 1:转账 2:支付宝 3:微信支付
														// 4:现金 5:预存款 6:刷卡

						receiptDetail.setAccountName(storeApplication.getPayeeBankAccount());
						receiptDetail.setShroffAccount(storeApplication.getPayeeAccount());
						receiptDetail.setBankName(storeApplication.getPayeeBank());
						receiptDetail.setDate(storeApplication.getCreatetime());

						receiptDetailsService.save(receiptDetail);
						
						// 3、财务中心-门店保证金表 增加数据
						DepositStore depositStore = new DepositStore();
						depositStore.setStoreName(receiptDepositStore.getStoreName());
						depositStore.setType(1); // 1:交纳 2:退还
						depositStore.setDate(receiptDepositStore.getDate());
						depositStore.setAmount(receiptDepositStore.getAmount());
						// depositStore.setRemark();
						depositStoreService.save(depositStore);
						
					}
					else if(storeType == 1){  //1:挂靠门店
						// 1、在hy_receipt_deposit_store_branch表中写数据
						ReceiptDepositStoreBranch receiptDepositStoreBranch = new ReceiptDepositStoreBranch();
						receiptDepositStoreBranch.setState(1); //
						receiptDepositStoreBranch.setStoreName(storeApplication.getStore().getStoreName());
						receiptDepositStoreBranch.setPayer(storeApplication.getPayerName());
						receiptDepositStoreBranch.setAmount(storeApplication.getMoney());
						receiptDepositStoreBranch.setDate(storeApplication.getCreatetime());
						receiptDepositStoreBranch.setBranchId(storeApplication.getStore().getDepartment().getHyDepartment().getHyDepartment().getId());
						receiptDepositStoreBranchService.save(receiptDepositStoreBranch);
						
						// 2、在ReceiptDeatailBranch表中写数据
						ReceiptDetailBranch receiptDetailBranch = new ReceiptDetailBranch();
						receiptDetailBranch.setReceiptType(3); // 3:(挂靠)门店押金
						receiptDetailBranch.setReceiptId(receiptDepositStoreBranch.getId());
						receiptDetailBranch.setAmount(receiptDepositStoreBranch.getAmount());
						receiptDetailBranch.setPayMethod(storeApplication.getPayment() + 0L);
						receiptDetailBranch.setAccountName(storeApplication.getPayeeBankAccount());
						receiptDetailBranch.setShroffAccount(storeApplication.getPayeeAccount());
						receiptDetailBranch.setBankName(storeApplication.getPayeeBank());
						receiptDetailBranch.setDate(storeApplication.getCreatetime());
						
						receiptDetailBranchService.save(receiptDetailBranch);
		
						
						// 3、分公司财务中心-门店保证金表 增加数据
						DepositStoreBranch depositStoreBranch = new DepositStoreBranch();
						depositStoreBranch.setStoreName(receiptDepositStoreBranch.getStoreName());
						depositStoreBranch.setType(1); // 1:交纳 2:退还
						depositStoreBranch.setDate(receiptDepositStoreBranch.getDate());
						depositStoreBranch.setAmount(receiptDepositStoreBranch.getAmount());
						depositStoreBranch.setBranchId(storeApplication.getStore().getDepartment().getHyDepartment().getHyDepartment().getId());
						// depositStore.setRemark();
						depositStoreBranchService.save(depositStoreBranch);
						
					}

					// 押金审核通过 Store中的状态字段
					Store store = storeApplication.getStore();
					store.setPstatus(2); // pstatus 0待缴纳，1交纳中，2已缴纳
					store.setPpayday(new Date());
					storeService.update(store);
				} else if (state == 0) {
					Store store = storeApplication.getStore();
					storeApplication.setApplicationStatus(-1);

					// 押金审核驳回Store中的状态字段
					store.setPstatus(0); // pstatus 0待缴纳，1交纳中，2已缴纳
					
					storeService.update(store);
				}
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), processInstanceId,
						(comment == null ? " " : comment) + ":" + state);
				taskService.claim(task.getId(), username);
				taskService.complete(task.getId());
				storeApplicationService.update(storeApplication);
				json.setSuccess(true);
				json.setMsg("审核完成");
			}

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;

	}
}
