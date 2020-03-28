package com.hongyu.controller.hzj03.audit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.hongyu.entity.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.controller.HyRoleController.Auth;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HyRoleAuthority.CheckedRange;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.MendianAuthorityService;
import com.hongyu.service.ReceiptDetailBranchService;
import com.hongyu.service.ReceiptManageFeeStoreService;
import com.hongyu.service.StoreApplicationService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;

/** 分公司 财务 - 审核 - 门店管理费 */
@Controller
@RequestMapping("admin/accountant/storeManageFee")
public class AccountantReview_StoreManageFee_Controller {

//	/** 待收款 - 门店管理费 - 未收款 */
//	private static final int NOT_RECEIVED = 0;
	/** 待收款 - 门店管理费 - 已收款 */
	private static final int RECEIVED = 1;



	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource(name = "receiptDetailBranchServiceImpl")
	ReceiptDetailBranchService receiptDetailBranchService;
	
	@Resource(name = "receiptManageFeeStoreServiceImpl")
	ReceiptManageFeeStoreService receiptManageFeeStoreService;

	@Resource(name = "storeApplicationServiceImpl")
	StoreApplicationService storeApplicationService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "hyRoleServiceImpl")
	HyRoleService hyRoleService;

	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;
	@Resource
	private RepositoryService repositoryService;
	
	@Resource(name = "mendianAuthorityServiceImpl")
	MendianAuthorityService mendianAuthorityService;

	/** 门店管理费-审核-列表 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json storePreSaveReviewList(Pageable pageable, Integer state, StoreApplication storeApplication,
			String startTime, String endTime, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			List<Filter> filters = FilterUtil.getInstance().getFilter(storeApplication);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !"".equals(startTime)) {
                filters.add(new Filter("createtime", Operator.ge, sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
            }
			if (endTime != null && !"".equals(endTime)){
                filters.add(new Filter("createtime", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
            }
            //0注册申请   1门店交押金     2门店申请续签         3交管理费      4退出申请
			filters.add(Filter.eq("type", 3));
			List<StoreApplication> storeApplications = storeApplicationService.findList(null, filters, null);
			List<Map<String, Object>> ans = new ArrayList<>();
			Map<String, Object> answer = new HashMap<>();
			if (state == null) {
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (StoreApplication tmp : storeApplications) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);
							m.put("createDate", tmp.getCreatetime());
							m.put("storeName", tmp.getStore().getStoreName());
							m.put("applicant", tmp.getOperator().getName());
							m.put("money", tmp.getMoney());
							m.put("applicationStatus", tmp.getApplicationStatus());
							ans.add(m);
						}
					}
				}
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (StoreApplication tmp : storeApplications) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);
							m.put("createDate", tmp.getCreatetime());
							m.put("storeName", tmp.getStore().getStoreName());
							m.put("applicant", tmp.getOperator().getName());
							m.put("money", tmp.getMoney());
							m.put("applicationStatus", tmp.getApplicationStatus());
							ans.add(m);
						}
					}
				}

				Collections.sort(ans, new Comparator<Map<String, Object>>() {
					@Override
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						long id1 = (long) o1.get("id");
						long id2 = (long) o2.get("id");
						return id2 > id1 ? 1 : -1;
					}
				});

			} else if (state == 0) {// 搜索未完成任务
				List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).orderByTaskCreateTime()
						.desc().list();
				for (Task task : tasks) {
					String processInstanceId = task.getProcessInstanceId();
					for (StoreApplication tmp : storeApplications) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 0);
							m.put("createDate", tmp.getCreatetime());
							m.put("storeName", tmp.getStore().getStoreName());
							m.put("applicant", tmp.getOperator().getName());
							m.put("money", tmp.getMoney());
							m.put("applicationStatus", tmp.getApplicationStatus());
							ans.add(m);
						}
					}
				}

			} else if (state == 1) {// 搜索已完成任务
				List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
						.orderByHistoricTaskInstanceStartTime().desc().finished().taskAssignee(username).list();
				for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
					String processInstanceId = historicTaskInstance.getProcessInstanceId();
					for (StoreApplication tmp : storeApplications) {
						if (processInstanceId.equals(tmp.getProcessInstanceId())) {
							HashMap<String, Object> m = new HashMap<>();
							m.put("id", tmp.getId());
							m.put("state", 1);
							m.put("createDate", tmp.getCreatetime());
							m.put("storeName", tmp.getStore().getStoreName());
							m.put("applicant", tmp.getOperator().getName());
							m.put("money", tmp.getMoney());
							m.put("applicationStatus", tmp.getApplicationStatus());
							ans.add(m);
						}
					}
				}
			}
			int page = pageable.getPage();
			int rows = pageable.getRows();
			answer.put("total", ans.size());
			answer.put("pageNumber", page);
			answer.put("pageSize", rows);
			answer.put("rows", ans.subList((page - 1) * rows, page * rows > ans.size() ? ans.size() : page * rows)); // 手动分页？
			json.setSuccess(true);
			
			if (ans.size() == 0) {
				json.setMsg("未获取到符合条件的数据");
			} else{
                json.setMsg("获取成功");
            }
			json.setObj(answer);
			return json;
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;
	}

	/** 门店管理费-审核- 详情 */
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
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
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(list);

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("list", list);

			// 门店信息
            obj.put("storeName", storeApplication.getStore().getStoreName());
            obj.put("applyDate", storeApplication.getCreatetime());
            obj.put("applicant", storeApplication.getOperator().getName());
            obj.put("money", storeApplication.getMoney());

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

            // 附件信息
			obj.put("accessory", storeApplication.getAccessory());
			
			// obj.put("remark", storeApplication.getRemark());

			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(obj);

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查找失败： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	/** 门店管理费-审核 - 操作 */
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

					// 门店管理费审核通过，直接生成已收款记录
					// 1、在hy_receipt_manage_fee_store表中写数据
					ReceiptManageFeeStore receiptManageFeeStore = new ReceiptManageFeeStore();
					receiptManageFeeStore.setState(RECEIVED);

					Department department = storeApplication.getStore().getDepartment();
					String treePath = department.getTreePath();
					String[] strs = treePath.split(",");
					Long branchId = Long.parseLong(strs[2]);

					receiptManageFeeStore.setBranchId(branchId);
					receiptManageFeeStore.setstoreName(storeApplication.getStore().getStoreName());
					receiptManageFeeStore.setPayer(storeApplication.getOperator().getName());
					receiptManageFeeStore.setAmount(storeApplication.getMoney());
					receiptManageFeeStore.setDate(storeApplication.getCreatetime());
					receiptManageFeeStoreService.save(receiptManageFeeStore);

					// 2、在hy_receipt_details_branch表中写入数据
					ReceiptDetailBranch receiptDetailBranch = new ReceiptDetailBranch();
					// 1:StoreManageFee 门店管理费 2:BranchProfitShare 分公司分成 3.StoreDepositBranch (挂靠)门店押金
					receiptDetailBranch.setReceiptType(1);
					receiptDetailBranch.setReceiptId(receiptManageFeeStore.getId());
					receiptDetailBranch.setAmount(storeApplication.getMoney());
					// 付款方式 1:转账 2:支付宝 3:微信支付 4:现金 5:预存款 6:刷卡
					receiptDetailBranch.setPayMethod(storeApplication.getPayment() + 0L);

					receiptDetailBranch.setAccountName(storeApplication.getPayeeBankAccount());
					receiptDetailBranch.setShroffAccount(storeApplication.getPayeeAccount());
					receiptDetailBranch.setBankName(storeApplication.getPayeeBank());
					receiptDetailBranch.setDate(storeApplication.getCreatetime());
					
					receiptDetailBranchService.save(receiptDetailBranch);

					

					// 返回给前台的数据中加入receiptManageFeeStore的id
					HashMap<String, Object> obj = new HashMap<>();
					obj.put("id", receiptManageFeeStore.getId());
					json.setObj(obj);

					// 管理费审核通过 Store中的状态字段
					Store store = storeApplication.getStore();
                    // 0未填写,1填写中，2待交纳,3缴纳中, 4已缴纳, 5 已过期
					store.setMstatus(4);
                    // 0未审核，1未激活，2激活，3强制激活
					store.setStatus(Constants.STORE_JI_HUO);
					store.setMpayday(new Date());
					storeService.update(store);
					
					//强制激活后，把门店经理角色改为激活后门店经理角色
					List<Filter> filters=new LinkedList<>();
					filters.add(Filter.eq("mendianType", store.getStoreType()));
					
					List<MendianAuthority> mendianAuthorities = mendianAuthorityService.findList(null, filters, null);
					filters.clear();
					String roleName="门店"+store.getStoreName()+"经理";
					filters.add(Filter.eq("name", roleName));
					List<HyRole> roles=hyRoleService.findList(null,filters,null);
					HyRole hyRole=new HyRole();
					if(!roles.isEmpty()) {
						hyRole=roles.get(0);
					}
					else {		
					    hyRole.setName("门店"+store.getStoreName()+"经理");
					    if(store.getStoreType() == 0) {
						     hyRole.setDescription("管理虹宇门店");
					    } else if(store.getStoreType() == 3) {
						     hyRole.setDescription("管理非虹宇门店");
					    }
					    hyRoleService.save(hyRole);
					}
					Set<Auth> auths = new HashSet<>();
				
					CheckedOperation co = CheckedOperation.edit;
					CheckedRange cr = CheckedRange.department;
					Long departmentId = store.getDepartment().getId();
					
					for(MendianAuthority tmp:mendianAuthorities){
						auths.add(generateAuth(co, cr, departmentId, tmp.getAuthorityId()));
					}
					
//					Auth auth1 = new Auth();
//					auth1.setCo(CheckedOperation.edit);
//					auth1.setCr(CheckedRange.department);
//					Set<Long> departs1 = new HashSet<>();
//					departs1.add(store.getDepartment().getId());
//					auth1.setDepartments(departs1);
//					auth1.setId(42040000L);
//					auths.add(auth1);
					
//					CheckedOperation co = CheckedOperation.edit;
//					CheckedRange cr = CheckedRange.department;
//					Long departmentId = store.getDepartment().getId();
//					Auth auth1 = new Auth();
//					auth1.setCo(CheckedOperation.edit);
//					auth1.setCr(CheckedRange.department);
//					Set<Long> departs1 = new HashSet<>();
//					departs1.add(store.getDepartment().getId());
//					auth1.setDepartments(departs1);
//					auth1.setId(42040000L);
//					auths.add(auth1);
//					auths.add(generateAuth(co, cr, departmentId, 42000000L));
//					/**综合服务*/
//					auths.add(generateAuth(co, cr, departmentId, 42010000L));
//					auths.add(generateAuth(co, cr, departmentId, 42010100L));
//					auths.add(generateAuth(co, cr, departmentId, 42010200L));
//					auths.add(generateAuth(co, cr, departmentId, 42010300L));
//					auths.add(generateAuth(co, cr, departmentId, 42010400L));
//					auths.add(generateAuth(co, cr, departmentId, 42010500L));
//					/**门店员工*/
//					auths.add(generateAuth(co, cr, departmentId, 42020000L));
//					auths.add(generateAuth(co, cr, departmentId, 42020100L));
//					auths.add(generateAuth(co, cr, departmentId, 42020200L));
//					/**产品订购,未完成*/
//					auths.add(generateAuth(co, cr, departmentId, 42030000L));
//					auths.add(generateAuth(co, cr, departmentId, 42030100L));
//					auths.add(generateAuth(co, cr, departmentId, 42030200L));
//					auths.add(generateAuth(co, cr, departmentId, 42030300L));
//					auths.add(generateAuth(co, cr, departmentId, 42030400L));
//					/**订单中心*/
//					auths.add(generateAuth(co, cr, departmentId, 42040000L));
//					auths.add(generateAuth(co, cr, departmentId, 42040100L));
//					auths.add(generateAuth(co, cr, departmentId, 42040200L));
//					auths.add(generateAuth(co, cr, departmentId, 42040300L));
//					auths.add(generateAuth(co, cr, departmentId, 42040400L));
//					/**门店信息*/
//					auths.add(generateAuth(co, cr, departmentId, 42050000L));
//					auths.add(generateAuth(co, cr, departmentId, 42050100L));
//					auths.add(generateAuth(co, cr, departmentId, 42050200L));
//					/**审核中心*/
//					auths.add(generateAuth(co, cr, departmentId, 50000000L));
//					auths.add(generateAuth(co, cr, departmentId, 50040000L));
//					auths.add(generateAuth(co, cr, departmentId, 50040300L));
//					Integer storeType = store.getStoreType();
//					if(storeType.equals(Constants.hyStore)){
//					}else if(storeType.equals(Constants.gkStore)){
//					}else if(storeType.equals(Constants.zyStore)){
//						auths.add(generateAuth(co, cr, departmentId, 42060000L));
//						auths.add(generateAuth(co, cr, departmentId, 42060100L));
//						auths.add(generateAuth(co, cr, departmentId, 42060200L));
//						auths.add(generateAuth(co, cr, departmentId, 42070000L));
//						auths.add(generateAuth(co, cr, departmentId, 52000000L));
//						auths.add(generateAuth(co, cr, departmentId, 52070000L));
//					}
//					
//					
					hyRoleService.grantResources(hyRole.getId(), auths);
					
						
					HyAdmin hyAdmin=store.getHyAdmin();
					hyAdmin.setRole(hyRole);
					hyAdminService.update(hyAdmin);
					
				} else if (state == 0) {
					storeApplication.setApplicationStatus(-1);

					// 管理费审核驳回 Store中的状态字段
					Store store = storeApplication.getStore();
					
					store.setMstatus(2); // 0未填写,1填写中，2待交纳,3缴纳中, 4已缴纳, 5 已过期
					storeService.update(store);
				}
				Authentication.setAuthenticatedUserId(username);
				taskService.claim(task.getId(), username);
				taskService.addComment(task.getId(), processInstanceId, comment == null ? " " : comment + ":" + state);
				taskService.complete(task.getId());
				storeApplicationService.update(storeApplication);
				json.setSuccess(true);
				json.setMsg("审核成功");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;
	}
	private static Auth generateAuth(CheckedOperation co,CheckedRange cr,Long departmentId,Long id){
		Auth auth=new Auth();
		auth.setCo(co);
		auth.setCr(cr);
		Set<Long> departs7 = new HashSet<>();
		departs7.add(departmentId);
		auth.setDepartments(departs7);
		auth.setId(id);
		return auth;
	}
}
