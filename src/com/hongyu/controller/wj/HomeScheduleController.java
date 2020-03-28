package com.hongyu.controller.wj;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.iterators.FilterListIterator;
import org.hibernate.annotations.Filters;
import org.hibernate.validator.constraints.Mod11Check.ProcessingDirection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.service.BaseService;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.controller.BranchFinanceController;
import com.hongyu.entity.AddedServiceTransfer;
import com.hongyu.entity.BalanceDueApply;
import com.hongyu.entity.BranchPayServicer;
import com.hongyu.entity.BranchRecharge;
import com.hongyu.entity.Department;
import com.hongyu.entity.DepositServicer;
import com.hongyu.entity.GroupBiankoudian;
import com.hongyu.entity.GroupStoreCancel;
import com.hongyu.entity.Guide;
import com.hongyu.entity.Gysfzrtuichu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyDistributorPrechargeRecord;
import com.hongyu.entity.HyDistributorSettlement;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroupCancelAudit;
import com.hongyu.entity.HyGroupShenheSwd;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyPaymentpreJiangtai;
import com.hongyu.entity.HyPromotionActivity;
import com.hongyu.entity.HyReceiptRefund;
import com.hongyu.entity.HyRegulate;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.LinePromotion;
import com.hongyu.entity.PayDeposit;
import com.hongyu.entity.PayDepositBranch;
import com.hongyu.entity.PayGuider;
import com.hongyu.entity.PayServicer;
import com.hongyu.entity.PaySettlement;
import com.hongyu.entity.PayShareProfit;
import com.hongyu.entity.PayablesBranchsettle;
import com.hongyu.entity.PaymentSupplier;
import com.hongyu.entity.PrePaySupply;
import com.hongyu.entity.ProfitShareConfirm;
import com.hongyu.entity.RefundInfo;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreApplication;
import com.hongyu.entity.StoreRecharge;
import com.hongyu.entity.SupplierDismissOrderApply;
import com.hongyu.entity.WithDrawCash;
import com.hongyu.entity.XuqianEntity;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.listener.SubscribeTicketListener;
import com.hongyu.entity.HyTicketHotelRoom;
import com.hongyu.entity.HyTicketHotelandsceneRoom;
import com.hongyu.entity.HyTicketSceneTicketManagement;
import com.hongyu.entity.HyTicketSubscribe;
import com.hongyu.entity.HyVisa;
import com.hongyu.service.AddedServiceTransferService;
import com.hongyu.service.BalanceDueApplyService;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.BranchPayServicerService;
import com.hongyu.service.BranchRechargeService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.DepositServicerService;
import com.hongyu.service.GroupBiankoudianService;
import com.hongyu.service.GroupStoreCancelService;
import com.hongyu.service.GuideService;
import com.hongyu.service.GystuiyajinService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDistributorPrechargeRecordService;
import com.hongyu.service.HyDistributorSettlementService;
import com.hongyu.service.HyGroupCancelAuditService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyGroupShenheSwdService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyPaymentpreJiangtaiService;
import com.hongyu.service.HyPromotionActivityService;
import com.hongyu.service.HyReceiptRefundService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketHotelRoomService;
import com.hongyu.service.HyTicketHotelandsceneRoomService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.service.HyTicketSubscribeService;
import com.hongyu.service.HyVisaService;
import com.hongyu.service.LinePromotionService;
import com.hongyu.service.PayDepositBranchService;
import com.hongyu.service.PayDepositService;
import com.hongyu.service.PayGuiderService;
import com.hongyu.service.PayServicerService;
import com.hongyu.service.PaySettlementService;
import com.hongyu.service.PayShareProfitService;
import com.hongyu.service.PayablesBranchsettleService;
import com.hongyu.service.PaymentSupplierService;
import com.hongyu.service.PrePaySupplyService;
import com.hongyu.service.ProfitShareConfirmService;
import com.hongyu.service.RefundInfoService;
import com.hongyu.service.StoreApplicationService;
import com.hongyu.service.StoreRechargeService;
import com.hongyu.service.StoreService;
import com.hongyu.service.SupplierDismissOrderApplyService;
import com.hongyu.service.WithDrawCashService;
import com.hongyu.service.XuqianService;
import com.hongyu.util.ActivitiUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.Constants.AuditStatus;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;
//import com.hongyu.controller.gsbing.ScheduleController.WrapProcess;

@Controller
@RequestMapping("admin/homepage/schedule2")
//@RequestMapping("homepage/schedule")
public class HomeScheduleController {
	@Resource
	private TaskService taskService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;	
	
	@Resource(name = "paymentSupplierServiceImpl")
	PaymentSupplierService paymentSupplierService;	
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyGroupCancelAuditServiceImpl")
	HyGroupCancelAuditService hyGroupCancelAuditService;
	
	@Resource(name = "linePromotionServiceImpl")
	LinePromotionService linePromotionService;
	
	@Resource(name = "hyPromotionActivityServiceImpl")
	HyPromotionActivityService hyPromotionActivityService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "groupStoreCancelServiceImpl")
	GroupStoreCancelService groupStoreCancelService;
	
	@Resource(name = "addedServiceTransferServiceImpl")
	AddedServiceTransferService addedServiceTransferService;
	
	@Resource(name = "storeApplicationServiceImpl")
	StoreApplicationService storeApplicationService;
	
	@Resource(name = "balanceDueApplyServiceImpl")
	BalanceDueApplyService balanceDueApplyService;
	
	@Resource(name = "prePaySupplyServiceImpl")
	PrePaySupplyService prePaySupplyService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@Resource(name = "payablesBranchsettleServiceImpl")
	PayablesBranchsettleService payablesBranchsettleService;
	
	@Resource(name = "hyPaymentpreJiangtaiServiceImpl")
	HyPaymentpreJiangtaiService hyPaymentpreJiangtaiService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "hyReceiptRefundServiceImpl")
	HyReceiptRefundService hyReceiptRefundService;
	
	@Resource(name = "profitShareConfirmServiceImpl")
	ProfitShareConfirmService profitShareConfirmService;
	
	@Resource(name = "storeRechargeServiceImpl")
	StoreRechargeService storeRechargeService;
	
	@Resource(name = "branchRechargeServiceImpl")
	BranchRechargeService branchRechargeService;
	
	@Resource(name = "withDrawCashServiceImpl")
	WithDrawCashService withDrawCashService;
	
	@Resource(name="depositServicerServiceImpl")
	DepositServicerService depositServicerService;
	
	@Resource(name = "gysfzrtuichuServiceImpl")
	private GystuiyajinService gystuiyajinService;
	
	@Resource(name = "supplierDismissOrderApplyServiceImpl")
	SupplierDismissOrderApplyService supplierDismissOrderApplyService;
	
	@Resource(name="hyDistributorPrechargeRecordServiceImpl")
	HyDistributorPrechargeRecordService hyDistributorPrechargeRecordService;
	
	@Resource(name="hyDistributorSettlementServiceImpl")
	HyDistributorSettlementService hyDistributorSettlementService;
	
	@Resource(name = "hyGroupShenheSwdServiceImpl")
	HyGroupShenheSwdService hyGroupShenheSwdService;
	
	@Resource(name = "groupBiankoudianServiceImpl")
	GroupBiankoudianService groupBiankoudianService;
	
	@Resource(name = "xuqianServiceImpl")
	XuqianService xuqianService;
	
	@Resource(name = "guideServiceImpl")
	GuideService guideService;
	
	@Resource(name = "hyTicketHotelandsceneRoomServiceImpl")
	HyTicketHotelandsceneRoomService hyTicketHotelandsceneRoomService;
	
	@Resource(name = "hyTicketHotelRoomServiceImpl")
	HyTicketHotelRoomService hyTicketHotelRoomService;
	
	@Resource(name = "hyTicketSceneTicketManagementServiceImpl")
	HyTicketSceneTicketManagementService hyTicketSceneTicketManagementService;
	
	@Resource(name = "hyVisaServiceImpl")
	HyVisaService hyVisaService;
	
	@Resource(name = "hyTicketSubscribeServiceImpl")
	HyTicketSubscribeService hyTicketSubscribeService;
	
	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;
	
	@Resource(name = "payDepositServiceImpl")
	PayDepositService payDepositService;
	
	@Resource(name = "paySettlementServiceImpl")
	PaySettlementService paySettlementService;
	
	@Resource(name = "payGuiderServiceImpl")
	PayGuiderService payGuiderService;
	
	@Resource(name = "payShareProfitServiceImpl")
	PayShareProfitService payShareProfitService;
	
	@Resource(name = "refundInfoServiceImpl")
	RefundInfoService refundInfoService;
	
	@Resource(name = "payDepositBranchServiceImpl")
	PayDepositBranchService payDepositBranchService;
	
	@Resource(name = "branchPayServicerServiceImpl")
	BranchPayServicerService  branchPayServicerService;
	
	
	
	static class WrapProcess{
		private String processId;
		private Integer count;
		public String getProcessId() {
			return processId;
		}
		public void setProcessId(String processId) {
			this.processId = processId;
		}
		public Integer getCount() {
			return count;
		}
		public void setCount(Integer count) {
			this.count = count;
		}	
	}
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(HttpSession session)
	{
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			HyDepartmentModel departmentModel=department.getHyDepartmentModel();
			String departmentModelName=departmentModel.getName();
			HyRole hyRole=hyAdmin.getRole();
			//取出所有未完成任务
			List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();

			Map<String,Object> obj=new HashMap<String,Object>();
			
			List<String> processList=new ArrayList<>();
			List<WrapProcess> wrapProcessList=new ArrayList<>();
			
			/**
			 * 如果登录账号是供应商,看到供应商的相关信息   
			*/
//			if(departmentModelName.contains("供应商")||departmentModelName.contains("汽车部")||
//					departmentModelName.contains("国内部")||departmentModelName.contains("出境部")) {
			if(departmentModelName.contains("供应商")){ //线路外部供应商
				
				List<Filter> filters2=new ArrayList<>();
				filters2.add(Filter.eq("liable", findPAdmin(hyAdmin)));
				filters2.add(Filter.eq("contractStatus", ContractStatus.zhengchang));
				List<HySupplierContract> supplierContracts=hySupplierContractService.findList(null,filters2,null);
				HySupplierContract supplierContract=supplierContracts.get(0);
				HySupplier hySupplier=supplierContract.getHySupplier(); //找到所属供应商
				
				//先只管线路
				if (hySupplier.getIsLine() == true) {
					obj.put("accountRole", "gongyinshang"); // 返回账号角色是供应商
					xianlugongyingshang(wrapProcessList, processList, username, hyAdmin);
				}
			    
			    //票务外部供应商
			    else {
			    	obj.put("accountRole", "ticketSupplier"); //返回账号角色是票务外部供应商
			    	piaowuwaibugongyingshang(wrapProcessList, processList, username, hyAdmin);
			    }
			}
			/**
			 * 如果登录账号的角色是门店 ,返回门店相关内容
			 * (分公司门店和非虹宇门店)  已修改
			 */
			else if(departmentModelName.contains("分公司门店") || departmentModelName.contains("非虹宇门店")) {
				obj.put("accountRole", "mendian");
				List<Filter> filteres=new ArrayList<>();
				filteres.add(Filter.eq("department", department)); //根据部门查找门店
				List<Store> stores=storeService.findList(null,filteres,null);
				if(stores.isEmpty()) {
					throw new RuntimeException("当前登录员工不属于任何门店");
				}
				Long storeId=stores.get(0).getId();//根据登录账号找到门店id
				mendianxiaotuan(wrapProcessList, processList, username, storeId);
				mendian(wrapProcessList, tasks, hyAdmin, processList, username, storeId);
			}
			/**
			 * 直营门店（经理）已修改
			 */
			else if(departmentModelName.contains("直营门店")&& hyRole.getName().contains("经理")){
				obj.put("accountRole", "zhiyingmendianjingli");
				
				List<Filter> filteres=new ArrayList<>();
				filteres.add(Filter.eq("department", department)); //根据部门查找门店
				List<Store> stores=storeService.findList(null,filteres,null);
				if(stores.isEmpty()) {
					throw new RuntimeException("当前登录员工不属于任何门店");
				}
				Long storeId=stores.get(0).getId();//根据登录账号找到门店id
				mendian(wrapProcessList, tasks, hyAdmin, processList, username, storeId);
				mendianxiaotuan(wrapProcessList, processList, username, storeId);
				mendianzengzhifuwu(wrapProcessList, processList, username, storeId);
			}
			/**
			 * 直营门店（员工）已修改
			 */
			else if(departmentModelName.contains("直营门店")&& !hyRole.getName().contains("经理")){
				obj.put("accountRole", "zhiyingmendianyuangong");
				
				List<Filter> filteres=new ArrayList<>();
				filteres.add(Filter.eq("department", department)); //根据部门查找门店
				List<Store> stores=storeService.findList(null,filteres,null);
				if(stores.isEmpty()) {
					throw new RuntimeException("当前登录员工不属于任何门店");
				}
				Long storeId=stores.get(0).getId();//根据登录账号找到门店id
				mendian(wrapProcessList, tasks, hyAdmin, processList, username, storeId);
				mendianxiaotuan(wrapProcessList, processList, username, storeId);
			}
			/**
			 * 连锁发展（经理） 已修改
			 */
			else if(departmentModelName.contains("连锁发展") && hyRole.getName().contains("经理")){
				obj.put("accountRole", "liansuofazhanjingli");
				mendianapplication(wrapProcessList, processList, username, 0, "storeRegistration");
				mendianapplication(wrapProcessList, processList, username, 4, "storeLogout");
			}
			/**
			 * 连锁发展（员工）已修改
			 */
			else if(departmentModelName.contains("连锁发展") && !hyRole.getName().contains("经理")){
				obj.put("accountRole", "liansuofazhanyuangong");
				//门店续签
				mendianapplication(wrapProcessList, processList, username, 2, "storeRenew");
				//门店退出
				mendianapplication(wrapProcessList, processList, username, 4, "storeLogout");
			}
			/**
			 * 总公司产品经理 已修改
			 */
			else if(departmentModelName.contains("总公司产品研发中心") && hyRole.getName().contains("经理")){
				obj.put("accountRole", "zonggongsichanpinjingli");
				zonggongsichanpinjingli(wrapProcessList, processList, username);
			}
			/**
			 * 分公司产品中心经理 已修改
			 */
			else if(departmentModelName.contains("分公司产品中心") && hyRole.getName().contains("经理")){
				obj.put("accountRole", "fengongsichanpinzhongxinjingli");
				fengongsichanpinzhongxinjingli(wrapProcessList, processList, username);
			}
			/**
			 * 市场部副总 已修改
			 */
			else if(departmentModelName.contains("市场部")){
				obj.put("accountRole", "shichangbufuzong");
				shichangbufuzong(wrapProcessList, processList, username);
			}
			/**
			 * 分公司副总 
			 */
			else if(departmentModelName.equals("分公司") && hyRole.getName().contains("副总")){
				obj.put("accountRole", "fengongsifuzong");
				fengongsifuzong(wrapProcessList, processList, username);
			}
			/**
			 * 总公司副总 已修改
			 */
			else if(departmentModelName.equals("总公司") && hyRole.getName().contains("副总") ){
				obj.put("accountRole", "zonggongsifuzong");
				zonggongsifuzong(wrapProcessList, processList, username);
			}
			/**
			 * 分公司财务 已修改
			 */
			else if(departmentModelName.equals("分公司财务部")){
				obj.put("accountRole", "fengongsicaiwu");
				fengongsicaiwu(wrapProcessList, processList, username, hyAdmin);
			}
			/**
			 * 总公司财务 已修改
			 */
			else if(departmentModelName.equals("总公司财务部")){
				obj.put("accountRole", "zongongsicaiwu");
				zonggongsicaiwu(wrapProcessList, processList, username,hyAdmin);
			}
			/**
			 * 品控员工（查票务上产品的无重审核）  已改
			 */
			else if(departmentModelName.contains("品控")){
				obj.put("accountRole", "pinkongyuangong");
				pinkongyuangong(wrapProcessList, processList, username,hyAdmin);
			}			
			/**
			 * 采购部经理（采购部）（判断角色）
			 */
			else if(departmentModelName.contains("采购部") && hyRole.getName().contains("经理")){
				obj.put("accountRole", "caigoubujingli");
				caigoubujingli(wrapProcessList, processList, username);
			}
			/**
			 * 采购部员工
			 */
			else if(departmentModelName.contains("采购部") && !hyRole.getName().contains("经理")){
				obj.put("accountRole", "caigoubuyuangong");
				caigoubuyuangong(wrapProcessList, processList, username);
			}
			/**
			 * 行政服务中心
			 */
			else if(departmentModelName.contains("行政服务")){
				obj.put("accountRole", "zonggongsixingzhengfuwuzhongxin");
				xingzhengfuwuzhongxin(wrapProcessList, processList, username);
			}
			/**
			 * 线路内部供应商（总公司出境部国内部）
			 */
			else if(departmentModelName.contains("出境部")||departmentModelName.contains("国内部")
					||departmentModelName.contains("总公司汽车部") ||departmentModelName.contains("活动部") ){
				obj.put("accountRole", "xianluneibugongyingshang");
				xianlugongyingshang(wrapProcessList, processList, username, hyAdmin);
			}
			/**
			 * 票务内部供应商（总公司票务部）
			 */
			else if(departmentModelName.contains("票务部")){
				obj.put("accountRole", "piaowuneibugongyingshang");
				piaowuneibugongyingshang(wrapProcessList, processList, username);
			}
			
			/**
			 * 分公司汽车部（经理）
			 */
			else if(departmentModelName.contains("分公司汽车部")&& hyRole.getName().contains("经理")){
				obj.put("accountRole","fengongsiqichebujingli");
			}
			
			/**
			 * 分公司汽车部（员工）
			 */
			else if(departmentModelName.contains("分公司汽车部")&& !hyRole.getName().contains("经理")){
				obj.put("accountRole","fengongsiqichebuyuangong");
			}
			/**
			 * 导游服务中心
			 */
			else if(departmentModelName.contains("导游")){
				obj.put("accountRole", "daoyoufuwuzhongxin");
			}
			else {
				obj.put("accountRole", "qita");
			}
			obj.put("processList", wrapProcessList);
			json.setSuccess(true);
			json.setMsg("搜索任务成功");
			json.setObj(obj);
		}
		catch(Exception e) {
			json.setSuccess(false);
			e.printStackTrace();
			json.setMsg(e.getMessage());
		}
		return json;
	}

	/**
	 * 线路外部供应商
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 * @param hyAdmin
	 */
	public void xianlugongyingshang(List<WrapProcess> wrapProcessList,List<String> processList,String username,HyAdmin hyAdmin){
		/**
		 * 自动打款申请
		 * 门店提出售后退款
		 * 门店提出退团申请
		 */
		String[] strings = { "payServicePreTN", "storeShouHou", "storeTuiTuan" };
		try{
			for (String string : strings) {
				List<Task> tasks2 = ActivitiUtils.getTaskList(username, string);
				String processId = string;
				if (string.equals("storeShouHou")) {
					for (Task task : tasks2) {
						String processInstanceId = task.getProcessInstanceId(); // 流程实例id
						List<Filter> filters = new ArrayList<>();
						filters.add(Filter.eq("status", 0)); // 待供应商审核
						filters.add(Filter.eq("type", HyOrderApplication.STORE_CANCEL_GROUP)); // 0-门店退团
						List<HyOrderApplication> orderApplications = hyOrderApplicationService.findList(null,
								filters, null);
						for (HyOrderApplication application : orderApplications) {
							Long orderId = application.getOrderId(); // 订单id
							filters.clear();
							HyOrder hyOrder = hyOrderService.find(orderId);
							if (hyOrder.getType() == 7) {
								processId = "visaShouHou";
							}
							// 如果登录账号为合同负责人
							if (hyAdmin.getHyAdmin() == null) {
								List<HyAdmin> hyAdmins = new ArrayList<>(hyAdmin.getHyAdmins());
								hyAdmins.add(hyAdmin);
								List<String> accounts = new ArrayList<>();
								for (HyAdmin admin : hyAdmins) {
									accounts.add(admin.getUsername());
								}
								if (hyOrder.getSupplier() != null) {
									if (accounts.contains(hyOrder.getSupplier().getUsername()) == true) {
										if (processInstanceId.equals(application.getProcessInstanceId())) {
											// 如果已经有一个改流程的代办事项,将数量加1
											if (processList.contains(processId)) {
												int num = processList.indexOf(processId);
												int preCount = wrapProcessList.get(num).getCount();
												wrapProcessList.get(num).setCount(preCount + 1); // 将数量加1
											} else {
												processList.add(processId);
												WrapProcess process = new WrapProcess();
												process.setCount(1); // 将数量设置成1
												process.setProcessId(processId);
												// process.setProcessName(processMap.get(processId));
												wrapProcessList.add(process);
											}
										}
									}
								}

							}
							// 如果登录账号为子账号
							else {
								if (hyOrder.getSupplier() != null) {
									if (hyOrder.getSupplier().getUsername().equals(username)) {
										if (processInstanceId.equals(application.getProcessInstanceId())) {
											// 如果已经有一个改流程的代办事项,将数量加1
											if (processList.contains(processId)) {
												int num = processList.indexOf(processId);
												int preCount = wrapProcessList.get(num).getCount();
												wrapProcessList.get(num).setCount(preCount + 1); // 将数量加1
											} else {
												processList.add(processId);
												WrapProcess process = new WrapProcess();
												process.setCount(1); // 将数量设置成1
												process.setProcessId(processId);
												wrapProcessList.add(process);
											}
										}
									}
								}
							}
						}
					}
				} else if (processId.equals("storeTuiTuan")){
					for(Task task : tasks2){
						String processInstanceId = task.getProcessInstanceId(); // 流程实例id
						List<Filter> filters=new ArrayList<>();
						filters.add(Filter.eq("status",0));  //待供应商审核
						filters.add(Filter.eq("type", HyOrderApplication.STORE_CANCEL_GROUP)); //0-门店退团			
						List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filters,null);
						for(HyOrderApplication application:orderApplications) {
							Long orderId=application.getOrderId(); //订单id
							filters.clear();
							HyOrder hyOrder=hyOrderService.find(orderId);
							if(hyOrder.getType() == 7){
								processId = "visaTuiDing";
							}
							//如果登录账号为合同负责人
							if(hyAdmin.getHyAdmin()==null) {
								List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
								hyAdmins.add(hyAdmin);
								List<String> accounts=new ArrayList<>();
								for(HyAdmin admin:hyAdmins) {
									accounts.add(admin.getUsername());
								}
								if(hyOrder.getSupplier()!=null) {
									if(accounts.contains(hyOrder.getSupplier().getUsername())==true) {
										if(processInstanceId.equals(application.getProcessInstanceId())) {
											//如果已经有一个改流程的代办事项,将数量加1
											if(processList.contains(processId)) {
												int num=processList.indexOf(processId);
												int preCount=wrapProcessList.get(num).getCount();
												wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
											}
											else {
												processList.add(processId);
												WrapProcess process=new WrapProcess();
												process.setCount(1); //将数量设置成1
												process.setProcessId(processId);
//												process.setProcessName(processMap.get(processId));
												wrapProcessList.add(process);
											}
										}
									}
								}
							}
							//如果登录账号为子账号
							else {
								if(hyOrder.getSupplier()!=null) {
									if(hyOrder.getSupplier().getUsername().equals(username)) {
										if(processInstanceId.equals(application.getProcessInstanceId())) {
											//如果已经有一个改流程的代办事项,将数量加1
											if(processList.contains(processId)) {
												int num=processList.indexOf(processId);
												int preCount=wrapProcessList.get(num).getCount();
												wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
											}
											else {
												processList.add(processId);
												WrapProcess process=new WrapProcess();
												process.setCount(1); //将数量设置成1
												process.setProcessId(processId);
												wrapProcessList.add(process);
											}
										}
									}
								}	
							}
						}						
					}
				}else{
					processList.add(processId);
					WrapProcess process = new WrapProcess();
					process.setCount(tasks2.size());
					process.setProcessId(string);
					wrapProcessList.add(process);
				}
			}
			if(!processList.contains("storeTuiTuan")) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId("storeTuiTuan");
				wrapProcessList.add(process);
			}
			if(!processList.contains("storeShouHou")) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId("storeShouHou");
				wrapProcessList.add(process);
			}
			if(!processList.contains("visaTuiDing")) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId("visaTuiDing");
				wrapProcessList.add(process);
			}
			if(!processList.contains("visaShouHou")) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId("visaShouHou");
				wrapProcessList.add(process);
			}
			/**
			 * 线路促销走流程好像有点问题,直接查数据库
			 */
			List<Filter> filterss=new ArrayList<>();
			filterss.add(Filter.eq("state",0));  //筛选状态为审核中
			filterss.add(Filter.eq("isCaigouti", true));
			//如果是合同负责人,能看到子账号下所有信息
			if(hyAdmin.getHyAdmin()==null) {
				List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
				hyAdmins.add(hyAdmin);
				filterss.add(Filter.in("operator", hyAdmins));
			}
			//如果是子账号,只能看到自己负责的部分
			else {
				filterss.add(Filter.eq("operator", hyAdmin));
			}
			List<LinePromotion> linepromotions=linePromotionService.findList(null,filterss,null);
			WrapProcess process_promotion=new WrapProcess();
			process_promotion.setCount(linepromotions.size());
			process_promotion.setProcessId("LinePromotion");
			wrapProcessList.add(process_promotion);
			
			/**
			 * 签证促销
			 */
			filterss.clear();
			filterss.add(Filter.eq("state", 0));
			filterss.add(Filter.eq("activityType", 4));
			filterss.add(Filter.eq("isCaigouti", true));
			//如果是合同负责人,能看到子账号下所有信息
			if(hyAdmin.getHyAdmin()==null) {
				List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
				hyAdmins.add(hyAdmin);
				filterss.add(Filter.in("jidiao", hyAdmins));
			}
			//如果是子账号,只能看到自己负责的部分
			else {
				filterss.add(Filter.eq("jidiao", hyAdmin));
			}
			List<HyPromotionActivity> activities = hyPromotionActivityService.findList(null,filterss,null);
			WrapProcess ticket_promotion=new WrapProcess();
			ticket_promotion.setCount(activities.size());
			ticket_promotion.setProcessId("visaPromotion");
			wrapProcessList.add(ticket_promotion);
			
			
			/**
			 * 供应商待确认订单,没有走流程,直接查数据库
			 */
			filterss.clear();
			filterss.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)); //2-待供应商确认
			filterss.add(Filter.eq("type", 1)); //先只管线路订单
			
			//如果是合同合同负责人,看所有订单
			if(hyAdmin.getHyAdmin()==null) {
				List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
				hyAdmins.add(hyAdmin);
				filterss.add(Filter.in("supplier", hyAdmins)); //产品创建人,团的计调
			}
			//如果不是合同合同负责人,筛选本人上的产品
			else {
				filterss.add(Filter.eq("supplier", hyAdmin)); //产品创建人,团的计调
			}
			
			List<HyOrder> hyOrders=hyOrderService.findList(null,filterss,null);
			if(hyOrders.size()>0) {
				WrapProcess process=new WrapProcess();
				process.setCount(hyOrders.size()); //待确定订单数量
				process.setProcessId("orderConfirm");
				wrapProcessList.add(process);
			}  
			else {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //待确定订单数量
				process.setProcessId("orderConfirm");
				wrapProcessList.add(process);
			}
			
			/**
			 * 签证供应商待确认订单
			 */
			filterss.clear();
			filterss.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)); //2-待供应商确认
			filterss.add(Filter.eq("type", 7)); //先只管线路订单
			
			//如果是合同合同负责人,看所有订单
			if(hyAdmin.getHyAdmin()==null) {
				List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
				hyAdmins.add(hyAdmin);
				filterss.add(Filter.in("supplier", hyAdmins)); //产品创建人,团的计调
			}
			//如果不是合同合同负责人,筛选本人上的产品
			else {
				filterss.add(Filter.eq("supplier", hyAdmin)); //产品创建人,团的计调
			}
			
			List<HyOrder> visaOrders=hyOrderService.findList(null,filterss,null);
			if(visaOrders.size()>0) {
				WrapProcess process=new WrapProcess();
				process.setCount(hyOrders.size()); //待确定订单数量
				process.setProcessId("visaorderConfirm");
				wrapProcessList.add(process);
			}else {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //待确定订单数量
				process.setProcessId("visaorderConfirm");
				wrapProcessList.add(process);
			}  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 票务外部供应商
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 * @param hyAdmin
	 */
	public void piaowuwaibugongyingshang(List<WrapProcess> wrapProcessList,List<String> processList,String username,HyAdmin hyAdmin){
		String[] strings = { "storeTuiTuan", "storeShouHou", "payServicePreTN" };
		try{
			for (String string : strings) {
				List<Task> tasks2 = ActivitiUtils.getTaskList(username, string);
				String processId = string;
				List<Filter> filters = new ArrayList<>();
				if (processId.equals("storeTuiTuan")) {
					for (Task task : tasks2) {
						filters.add(Filter.eq("status",0));  //0:待供应商确认 1:待品控员工审核 2：待财务审核
						filters.add(Filter.eq("type", HyOrderApplication.STORE_CANCEL_GROUP)); //0-门店退团			
						List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filters,null);
						String processInstanceId=task.getProcessInstanceId();
						for(HyOrderApplication application:orderApplications) {
							HyOrder order = hyOrderService.find(application.getOrderId());
							if(processInstanceId.equals(application.getProcessInstanceId())) {
								switch(order.getType()){
								case 3:{
									processId = "hotelTuiDing";
									break;
								}
								case 4:{
									processId = "ticketTuiDing";
									break;
								}
								case 5:{
									processId = "ticketandhotelTuiDing";
									break;
								}
								default:
									continue;
								}
								//如果已经有一个改流程的代办事项,将数量加1
								if(processList.contains(processId)) {
									int num=processList.indexOf(processId);
									int preCount=wrapProcessList.get(num).getCount();
									wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
								}
								else {
									processList.add(processId);
									WrapProcess process=new WrapProcess();
									process.setCount(1); //将数量设置成1
									process.setProcessId(processId);
									wrapProcessList.add(process);
								}
							}
						}
					}
				}else if(processId.equals("storeShouHou")){
					for(Task task:tasks2){
						filters.clear();
						filters.add(Filter.eq("status",0));  //0:待供应商确认 1:待品控员工审核 2：待财务审核
						filters.add(Filter.eq("type", HyOrderApplication.STORE_CUSTOMER_SERVICE)); //0-门店售后			
						List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filters,null);
						String processInstanceId=task.getProcessInstanceId();
						for(HyOrderApplication application:orderApplications) {
							HyOrder order = hyOrderService.find(application.getOrderId());
							if(processInstanceId.equals(application.getProcessInstanceId())) {
								switch(order.getType()){
								case 3:{
									processId = "hotelShouHou";
									break;
								}
								case 4:{
									processId = "ticketShouHou";
									break;
								}
								case 5:{
									processId = "ticketandhotelShouHou";
									break;
								}
								
								default:
									continue;
								}
								//如果已经有一个改流程的代办事项,将数量加1
								if(processList.contains(processId)) {
									int num=processList.indexOf(processId);
									int preCount=wrapProcessList.get(num).getCount();
									wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
								}
								else {
									processList.add(processId);
									WrapProcess process=new WrapProcess();
									process.setCount(1); //将数量设置成1
									process.setProcessId(processId);
									wrapProcessList.add(process);
								}
							}
						}
					}
				}else{
					processList.add(processId);
					WrapProcess process = new WrapProcess();
					process.setCount(tasks2.size());
					process.setProcessId(string);
					wrapProcessList.add(process);
				}
			}
			String processId = "";
			for(int i=3;i<=5;i++){
				switch(i){
				case 3:{
					processId = "hotelShouHou";
					break;
				}
				case 4:{
					processId = "ticketShouHou";
					break;
				}
				case 5:{
					processId = "ticketandhotelShouHou";
					break;
				}
				}
				
				if(!processList.contains(processId)) {
					WrapProcess process=new WrapProcess();
					process.setCount(0); //将数量设置成0
					process.setProcessId(processId);
					wrapProcessList.add(process);
				}
			}
			
			for(int i=3;i<=5;i++){
				switch(i){
				case 3:{
					processId = "hotelTuiDing";
					break;
				}
				case 4:{
					processId = "ticketTuiDing";
					break;
				}
				case 5:{
					processId = "ticketandhotelTuiDing";
					break;
				}
				default:
					break;
				}
				if(!processList.contains(processId)) {
					WrapProcess process=new WrapProcess();
					process.setCount(0); //将数量设置成0
					process.setProcessId(processId);
					wrapProcessList.add(process);
				}
			}
			
			/**
			 * 供应商待确认订单（暂时3种，不确认有没有保险）
			 */
			HyRole hyRole=hyAdmin.getRole();
			int[] orderConfirmList = {3,4,5};//订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
			
			List<Filter> filters = new ArrayList<>();
			for(int i : orderConfirmList){
				filters.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)); //2-待供应商确认
				filters.add(Filter.eq("type", i)); 
				
				//如果不是门店经理,只能看自己创建的
				if(!hyRole.getName().contains("经理")) {
					filters.add(Filter.eq("creatorId", username));
				}
				
				List<HyOrder> hyOrders=hyOrderService.findList(null,filters,null);
				
				switch (i) {
				case 3:{
					processId = "hotelOrderConfirm";
					break;
				}
				case 4:{
					processId = "menpiaoOrderConfirm";
					break;
				}
				case 5:{
					processId = "ticketandhotelOrderConfirm";
					break;
				}
				default:
					continue;
				}
				
				
				if(hyOrders.size()>0) {
					WrapProcess process=new WrapProcess();
					process.setCount(hyOrders.size()); //待确定订单数量
					process.setProcessId(processId);
					wrapProcessList.add(process);
				}  
				else {
					WrapProcess process=new WrapProcess();
					process.setCount(0); //待确定订单数量
					process.setProcessId(processId);
					wrapProcessList.add(process);
				}
			}
			
			List<Filter> filterss=new ArrayList<>();
			/**
			 * 供应商建促销（票务3种）（暂时三种）
			 */
			for(int i=0;i<=2;i++){
				filterss.clear();
				filterss.add(Filter.eq("state", 0));
				filterss.add(Filter.eq("activityType", i));
				filterss.add(Filter.eq("isCaigouti", true));
				List<HyPromotionActivity> activities = hyPromotionActivityService.findList(null,filterss,null);
				String name = "";
				String id = "";
				switch (i) {
				case 0:{
					id = "ticketPromotion";
					break;
				}
				case 1:{
					id = "hotelPromotion";
					break;
				}
				case 2:{
					id = "ticketandhotelPromotion";
					break;
					}
				
				default:
					continue;
				
			}
			
				WrapProcess ticket_promotion=new WrapProcess();
				ticket_promotion.setCount(activities.size());
				ticket_promotion.setProcessId(id);
				wrapProcessList.add(ticket_promotion);
			}
		}catch (Exception e) {
				// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 品控员工
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 * @param hyAdmin
	 */
	public void pinkongyuangong(List<WrapProcess> wrapProcessList,List<String> processList,String username,HyAdmin hyAdmin){
		/**
		 * 供应商退出，供应商新建团期,供应商甩尾单,供应商申请特殊费率（团变更费率）,供应商消团,采购部新建供应商,采购部变更扣点,采购部直接\变更续签,采购部申请退还剩余押金
		 */
		String[] strings = { "cgbgystuiyajinprocess" ,"xianlushenheprocess","swdprocess","groupbiankoudianprocess","xiaotuanProcess","cgbgysprocess","biangengkoudianprocess","xuqianprocess","tuiyajinprocess"};
		String[] datebase = {"hy_supplier_tuiyajin","hy_group","group_swd_shenhe","hy_group_biankoudian","hy_group_cancel_audit","hy_supplier_contract","hy_supplier_contract","hy_supplier_contract_xuqian","hy_supplier_tuiyajin"};
		String[] processes = {"process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_id","process_id","process_instance_id","process_instance_id"};
		for(int i =0;i<strings.length;i++){
			String string = strings[i];
			String sql = "select COUNT(*) from "+datebase[i]+" dai where dai."+processes[i]+" in ( "
					+" SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri "
					+"WHERE ri.USER_ID_ = '"+username+"') AND rt.PROC_DEF_ID_ LIKE '"+string+"%') and dai.audit_status = 1";
			List<Object[]> objects =  hyOrderService.statis(sql);
			Object object =  objects.get(0);
			
			processList.add(string);
			WrapProcess process = new WrapProcess();
			process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
			process.setProcessId(string);
			wrapProcessList.add(process);
			
		}
		/**
		 * 供应商上产品（票务5种）酒加景上产品，酒店房间上产品，景区门票上产品， 签证上产品，认购门票上产品
		 */
		String[] strings2 = {"hotelandsceneRoomPriceProcess","hotelRoomPriceProcess",
				"sceneticketPriceProcess","visaPriceProcess","subscribeTicket"};
		String[] datebase2 = {"hy_ticket_hotelandscene_room","hy_ticket_hotel_room",
				"hy_ticket_scene_ticket_management","hy_visa","hy_ticket_subscribe"};
		for(int i =0;i<strings2.length;i++){
			String string = strings2[i];
			String sql = "select COUNT(*) from "+datebase2[i]+" dai where dai.process_instance_id in ( "
					+" SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri "
					+"WHERE ri.USER_ID_ = '"+username+"') AND rt.PROC_DEF_ID_ LIKE '"+string+"%') and dai.audit_status = 2";
			List<Object[]> objects =  hyOrderService.statis(sql);
			Object object =  objects.get(0);		
			processList.add(string);
			WrapProcess process = new WrapProcess();
			process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
			process.setProcessId(string);
			wrapProcessList.add(process);
			
		}
		/**
		 * 供应商驳回订单
		 */
		String[] strings3 = {"suppilerDismissOrder"};
		String[] datebase3 = {"hy_supplier_dismiss_order_apply"};
		for(int i =0;i<strings3.length;i++){
			String string = strings3[i];
			String sql = "select COUNT(*) from "+datebase3[i]+" dai where dai.process_instance_id in ( "
					+" SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri "
					+"WHERE ri.USER_ID_ = '"+username+"') AND rt.PROC_DEF_ID_ LIKE '"+string+"%') and dai.status = 0";
			List<Object[]> objects =  hyOrderService.statis(sql);
			Object object =  objects.get(0);		
			processList.add(string);
			WrapProcess process = new WrapProcess();
			process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
			process.setProcessId(string);
			wrapProcessList.add(process);	
		}
		
		mendiantuituan(wrapProcessList, processList, 1, username);
		mendiantuituanpankong(wrapProcessList, processList);
	}
	/**
	 * 总公司财务
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 * @param hyAdmin
	 */
	public void zonggongsicaiwu(List<WrapProcess> wrapProcessList,List<String> processList,String username,HyAdmin hyAdmin) {
		// TODO Auto-generated method stub
		/**
		 * 门店交押金
		 */
		String string = "jiaoyajin";
		String sql = "select COUNT(*) from hy_store_application dai where dai.process_instance_id in ( "
				+" SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri "
				+"WHERE ri.USER_ID_ = '"+username+"') AND rt.PROC_DEF_ID_ LIKE 'jiaoyajin%') and dai.status = 0 and dai.type = 1";
		List<Object[]> objects =  hyOrderService.statis(sql);
		Object object =  objects.get(0);
		
		processList.add(string);
		WrapProcess process1 = new WrapProcess();
		process1.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
		process1.setProcessId(string);
		wrapProcessList.add(process1);
		
		/**
		 * 门店退出
		 */
		string = "storeLogout";
		sql = "select COUNT(*) from hy_store_application dai where dai.process_instance_id in ( "
				+" SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri "
				+"WHERE ri.USER_ID_ = '"+username+"') AND rt.PROC_DEF_ID_ LIKE 'storeLogout%') and dai.status = 0 and dai.type = 2";
		objects =  hyOrderService.statis(sql);
		object =  objects.get(0);
		
		processList.add(string);
		process1 = new WrapProcess();
		process1.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
		process1.setProcessId(string);
		wrapProcessList.add(process1);
		
		/**
		 * 门店充值,供应商缴纳押金,供应商退出,供应商驳回订单,供应商实时打款, 供应商消团,供应商预付款,供应商计调报账,供应商付尾款,分公司充值, 分公司团结算,江泰预充值,采购部申请退还剩余押金,采购部提前打款,分销商充值,分销商渠道结算
		 */
		String[] strings = {"storeRecharge","jiaoyajinprocess","cgbgystuiyajinprocess","suppilerDismissOrder","payServicePreTN","xiaotuanProcess","prePay","regulateprocess","banlanceDue","branchRecharge","branchsettleProcess","PaymentJiangtai","tuiyajinprocess","payServicePre","distributorPrechargePro","distributorSettlementPro"};
		String[] datebase = {"hy_store_recharge","hy_deposit_servicer","hy_supplier_tuiyajin","hy_supplier_dismiss_order_apply","hy_payment_supplier","hy_group_cancel_audit","hy_pre_pay_supply","hy_regulate","hy_balance_due_apply","hy_branch_recharge","hy_payables_branchsettle","hy_paymentpre_jiangtai","hy_supplier_tuiyajin","hy_payment_supplier","hy_distributor_precharge_record","hy_distributor_settlement"};
//		String[] processes = {"process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id","process_instance_id"};
		for(int i=0;i<strings.length;i++){
			StringBuffer buffer = new StringBuffer();
			buffer.append("select COUNT(*) from ");
			buffer.append(datebase[i]);
			buffer.append(" dai where dai.process_instance_id");
//			buffer.append(processes[i]);
			buffer.append(" in ( SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '");
			buffer.append(username);
			buffer.append("' ) AND rt.PROC_DEF_ID_ LIKE '");
			buffer.append(strings[i] + "%')");
			
			switch(strings[i]){
			case "storeRecharge" : buffer.append(" and dai.status = 0"); break;
			case "jiaoyajinprocess": buffer.append(" and dai.audit_status = 1 ");break;
			case "cgbgystuiyajinprocess":buffer.append(" and dai.audit_status = 1 ");break;
			case "suppilerDismissOrder":buffer.append(" and dai.status = 0");break;
			case "payServicePreTN" : buffer.append(" and dai.status = 1 and dai.step = 13");break;
			case "xiaotuanProcess" :  break;
			case "prePay":buffer.append(" and dai.state = 0 and dai.step = 2");break;
			case "regulateprocess": buffer.append(" and dai.status = 1");break;
			case "balanceDue":buffer.append(" and dai.status = 1 and dai.step = 3 ");break;
			case "branchRecharge" : buffer.append(" and dai.status = 1");break;
			case "branchsettleProcess" : buffer.append(" and dai.audit_status = 1 and dai.step = 2 ");break;
			case "PaymentJiangtai":buffer.append(" and application_status = 2");break;
			case "tuiyajinprocess" : buffer.append(" and dai.audit_status = 1");break;
			case "payServicePre":buffer.append(" and dai.status = 1 and dai.step = 4 ");break;
			case "distributorPrechargePro":buffer.append(" and dai.check_status = 1 ");break;
			case "distributorSettlementPro" : buffer.append(" and dai.check_status = 1 ");break;
			}
			
			objects =  hyOrderService.statis(buffer.toString());
			object =  objects.get(0);
			
			processList.add(strings[i]);
			process1 = new WrapProcess();
			process1.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
			process1.setProcessId(strings[i]);
			wrapProcessList.add(process1);
			
			
		}
		
		mendiantuituan(wrapProcessList, processList, 2, username);
		mendiantuituanpankong(wrapProcessList, processList);
		
		List<Filter> filters = new ArrayList<>();
		/**
		 * 门店提现 （未走流程）
		 */
		filters.clear();
		filters.add(Filter.eq("status", 0));
		List<WithDrawCash> withDrawCashs = withDrawCashService.findList(null,filters,null);
		if(withDrawCashs.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(withDrawCashs.size()); //待确定订单数量
			process.setProcessId("mendiantixian");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("mendiantixian");
			wrapProcessList.add(process);
		}
		
		
		/**
		 * 分公司分成  不走流程
		 */
		filters.clear();
		filters.add(Filter.eq("state", 1));
		List<ProfitShareConfirm> confirms = profitShareConfirmService.findList(null,filters,null);
		if(confirms.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(confirms.size()); //待确定订单数量
			process.setProcessId("fengongsifencheng");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("fengongsifencheng");
			wrapProcessList.add(process);
		}
		
		/**
		 * 保险退款  不走流程
		 */
		filters.clear();
		filters.add(Filter.eq("status", 0));
		filters.add(Filter.ge("type", 13));
		filters.add(Filter.le("type", 15));
		List<HyOrderApplication> applications = hyOrderApplicationService.findList(null,filters,null);
		if(applications.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(applications.size());
			process.setProcessId("insuranceTuiDing");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("insuranceTuiDing");
			wrapProcessList.add(process);
		}
		/**
		 * 待办事项
		 */
		//分公司预付款
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		filters.add(Filter.eq("type", 1));
		List<PayServicer> payServicers1 = payServicerService.findList(null,filters,null);
		if(payServicers1.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payServicers1.size()); //待确定订单数量
			process.setProcessId("fengongsiyufukuan1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("fengongsiyufukuan1");
			wrapProcessList.add(process);
		}
		
		//T+N打款
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		filters.add(Filter.eq("type", 2));
		List<PayServicer> payServicers2 = payServicerService.findList(null,filters,null);
		if(payServicers2.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payServicers2.size()); //待确定订单数量
			process.setProcessId("tjiandakuan1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("tjiandakuan1");
			wrapProcessList.add(process);
		}
		
	    //提前打款
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		filters.add(Filter.eq("type", 3));
		List<PayServicer> payServicers3 = payServicerService.findList(null,filters,null);
		if(payServicers3.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payServicers3.size()); //待确定订单数量
			process.setProcessId("tiqiandakuan1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("tiqiandakuan1");
			wrapProcessList.add(process);
		}
		
		//旅游元素供应商尾款
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		filters.add(Filter.eq("type", 4));
		List<PayServicer> payServicers4 = payServicerService.findList(null,filters,null);
		if(payServicers4.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payServicers4.size()); //待确定订单数量
			process.setProcessId("lvyouyuansugongyingshangweikuan1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("lvyouyuansugongyingshangweikuan1");
			wrapProcessList.add(process);
		}
		//江泰预充款 
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		filters.add(Filter.eq("type", 6));
		List<PayServicer> payServicers6 = payServicerService.findList(null,filters,null);
		if(payServicers6.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payServicers6.size()); //待确定订单数量
			process.setProcessId("jiangtaiyuchongzhi1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("jiangtaiyuchongzhi1");
			wrapProcessList.add(process);
		}
		
		
		//总公司预付款
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		filters.add(Filter.eq("type", 7));
		List<PayServicer> payServicers7 = payServicerService.findList(null,filters,null);
		if(payServicers7.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payServicers7.size()); //待确定订单数量
			process.setProcessId("zonggongsiyufukuan1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("zonggongsiyufukuan1");
			wrapProcessList.add(process);
		}
		
		//门店提现
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		filters.add(Filter.eq("type", 8));
		List<PayServicer> payServicers8 = payServicerService.findList(null,filters,null);
		if(payServicers8.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payServicers8.size()); //待确定订单数量
			process.setProcessId("mendiantixian1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("mendiantixian1");
			wrapProcessList.add(process);
		}
		
		//门店押金（保证金）
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		filters.add(Filter.eq("depositType", 1));
		List<PayDeposit> payDeposits1 = payDepositService.findList(null,filters,null);
		if(payDeposits1.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payDeposits1.size()); //待确定订单数量
			process.setProcessId("mendianyajin1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("mendianyajin1");
			wrapProcessList.add(process);
		}
		
		//供应商保证金
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		filters.add(Filter.eq("depositType", 2));
		List<PayDeposit> payDeposits2 = payDepositService.findList(null,filters,null);
		if(payDeposits2.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payDeposits2.size()); //待确定订单数量
			process.setProcessId("gongyingshangbaozhengjin1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("gongyingshangbaozhengjin1");
			wrapProcessList.add(process);
		}
		
		//导游报账应付款
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		filters.add(Filter.eq("type", 1));
		List<PayGuider> payGuider1 = payGuiderService.findList(null,filters,null);
		if(payGuider1.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payGuider1.size()); //待确定订单数量
			process.setProcessId("daoyoubaozhangyingfukuan1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("daoyoubaozhangyingfukuan1");
			wrapProcessList.add(process);
		}
		
		//导游费用
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		filters.add(Filter.eq("type", 2));
		List<PayGuider> payGuider2 = payGuiderService.findList(null,filters,null);
		if(payGuider2.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payGuider2.size()); //待确定订单数量
			process.setProcessId("daoyoufeiyong1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("daoyoufeiyong1");
			wrapProcessList.add(process);
		}
		
		//分公司分成
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		filters.add(Filter.eq("type", 1));
		List<PayShareProfit> payShareProfits = payShareProfitService.findList(null,filters,null);
		if(payShareProfits.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payShareProfits.size()); //待确定订单数量
			process.setProcessId("fengongsifencheng1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("fengongsifencheng1");
			wrapProcessList.add(process);
		}
		
		//分公司产品中心团结算
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		List<PaySettlement> paySettlements = paySettlementService.findList(null,filters,null);
		if(paySettlements.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(paySettlements.size()); //待确定订单数量
			process.setProcessId("tuanjiesuan1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("tuanjiesuan1");
			wrapProcessList.add(process);
		}
		
		//实退款
		filters.clear();
		filters.add(Filter.eq("state", 0));
		filters.add(Filter.eq("type", 1));
		List<RefundInfo> refundInfos = refundInfoService.findList(null,filters,null);
		if(refundInfos.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(refundInfos.size()); //待确定订单数量
			process.setProcessId("shituikuan1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("shituikuan1");
			wrapProcessList.add(process);
		}

	}
	
	/**
	 * 分公司财务
	 */
	public void fengongsicaiwu(List<WrapProcess> wrapProcessList,List<String> processList,String username,HyAdmin hyAdmin){
		/**
		 * 门店交管理费，增值服务打款
		 */
		String[] strings = {"jiaoguanlifei","valueAdded"};	
		String[] datebase = {"hy_store_application","hy_added_service_transfer"};
		for(int i=0;i<strings.length;i++){
			StringBuffer buffer = new StringBuffer();
			buffer.append("select COUNT(*) from ");
			buffer.append(datebase[i]);
			buffer.append(" dai where dai.process_instance_id");
//			buffer.append(processes[i]);
			buffer.append(" in ( SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '");
			buffer.append(username);
			buffer.append("' ) AND rt.PROC_DEF_ID_ LIKE '");
			buffer.append(strings[i] + "%')");
			
			switch(strings[i]){
			case "jiaoguanlifei":buffer.append(" and dai.type = 3 and dai.status = 0");break;
			case "hy_added_service_transfer":buffer.append(" and dai.step = 3 and dai.status = 1");break;
			}
			List<Object[]> objects =  hyOrderService.statis(buffer.toString());
			Object object =  objects.get(0);
			
			processList.add(strings[i]);
			WrapProcess process = new WrapProcess();
			process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
			process.setProcessId(strings[i]);
			wrapProcessList.add(process);
		}
		List<Filter> filters = new ArrayList<>();
		/**
		 * 直营门店实收款   （没走流程）
		 */
		filters.add(Filter.eq("type", 0));
		filters.add(Filter.eq("status", 0));
		Department branch = departmentService.findCompanyOfDepartment(hyAdmin.getDepartment());
		filters.add(Filter.eq("branch", branch));
		List<HyReceiptRefund> finances = hyReceiptRefundService.findList(null,filters,null);
		if(finances.size()>0) {
			WrapProcess process=new WrapProcess();
			process.setCount(finances.size()); //待确定订单数量
			process.setProcessId("zhiyingmendianshishoukuan");
			wrapProcessList.add(process);
		}  
		else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("zhiyingmendianshishoukuan");
			wrapProcessList.add(process);
		}
		
		/**
		 * 直营门店实退款（不走流程）
		 */
		filters.clear();
		filters.add(Filter.eq("type", 1));
		filters.add(Filter.eq("status", 0));
		filters.add(Filter.eq("branch", branch));
		List<HyReceiptRefund> refunds = hyReceiptRefundService.findList(null,filters,null);
		if(refunds.size()>0) {
			WrapProcess process=new WrapProcess();
			process.setCount(refunds.size()); //待确定订单数量
			process.setProcessId("zhiyingmendianshituikuan");
			wrapProcessList.add(process);
		}  
		else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("zhiyingmendianshituikuan");
			wrapProcessList.add(process);
		}
		/**
		 * 分公司分成  不走流程
		 */
		filters.clear();
		filters.add(Filter.eq("state", 1));
		filters.add(Filter.eq("branch", branch));
		List<ProfitShareConfirm> confirms = profitShareConfirmService.findList(null,filters,null);
		if(confirms.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(confirms.size()); //待确定订单数量
			process.setProcessId("fengongsifencheng");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("fengongsifencheng");
			wrapProcessList.add(process);
		}
		
		//增值业务付款
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		List<BranchPayServicer> branchPayServicers = branchPayServicerService.findList(null,filters,null);
		if(branchPayServicers.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(branchPayServicers.size()); //待确定订单数量
			process.setProcessId("zengzhiyewufukuan1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("zengzhiyewufukuan1");
			wrapProcessList.add(process);
		}
		
		//实退款
		filters.clear();
		filters.add(Filter.eq("hasPaid", 0));
		List<PayDepositBranch> payDepositBranchs = payDepositBranchService.findList(null,filters,null);
		if(payDepositBranchs.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(payDepositBranchs.size()); //待确定订单数量
			process.setProcessId("shituikuan1");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("shituikuan1");
			wrapProcessList.add(process);
		}
	}
	
	
	/**
	 * 门店的供应商未确认订单和门店未支付
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 * @param storeId
	 */
	public void mendian(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username,Long storeId){
		HyRole hyRole=hyAdmin.getRole();
		int[] orderConfirmList = {1,2,3,4,5,7};//订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
		String processId = "";
		String processName = "";
		
		List<Filter> filters = new ArrayList<>();
		for(int i : orderConfirmList){
			filters.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)); //2-待供应商确认
			filters.add(Filter.eq("type", i)); 
			filters.add(Filter.eq("storeId", storeId)); //查找本门店的
			
			//如果不是门店经理,只能看自己创建的
			if(!hyRole.getName().contains("经理")) {
				filters.add(Filter.eq("creatorId", username));
			}
			
			List<HyOrder> hyOrders=hyOrderService.findList(null,filters,null);
			
			switch (i) {
			case 1: {
				processId = "lineOrderConfirm";
				break;
			}
			case 2: {
				processId = "rengoumenpiaoOrderConfirm";
				break;
			}
			case 3:{
				processId = "hotelOrderConfirm";
				break;
			}
			case 4:{
				processId = "menpiaoOrderConfirm";
				break;
			}
			case 5:{
				processId = "ticketandhotelOrderConfirm";
				break;
			}
			case 7:{
				processId = "visaOrderConfirm";
				break;
			}
			default:
				break;
			}
			
			
			if(hyOrders.size()>0) {
				WrapProcess process=new WrapProcess();
				process.setCount(hyOrders.size()); //待确定订单数量
				process.setProcessId(processId);
				wrapProcessList.add(process);
			}  
			else {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //待确定订单数量
				process.setProcessId(processId);
				wrapProcessList.add(process);
			}
			filters.clear();
		}
		
		int[] orderPayList = { 0,1,2,3,4,5,6,7}; //订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
		for(int i : orderPayList){
			/**
			 * 门店未支付订单,没有走工作流,直接查数据库
			 */
			filters.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_STORE_PAY)); //0-待门店支付
			filters.add(Filter.eq("type", i)); //
//			filteres.add(Filter.eq("paystatus", 0)); //门店待支付
			filters.add(Filter.eq("storeId", storeId)); //只筛选本门店的
			
			//如果不是经理,只能看到自己创建的数据;如果是经理,能看到整个门店的
			if(!hyRole.getName().contains("经理")) {
				filters.add(Filter.eq("creatorId", username));
			}
			List<HyOrder> store_hyOrders=hyOrderService.findList(null,filters,null);
			
			switch(i){
			case 0:{
				processId = "guideOrderPay";
				break;
			}
			case 1: {
				processId = "lineOrderPay";
				break;
			}
			case 2:{
				processId = "rengoumenpiaoOrderPay";
				break;
			}
			case 3:{
				processId = "hotelOrderPay";
				break;
			}
			case 4:{
				processId = "ticketOrderPay";
				break;
			}
			case 5:{
				processId = "ticketandhotelOrderPay";
				break;
			}
			case 6:{
				processId = "insuranceOrderPay";
				break;
			}
			case 7:{
				processId = "visaOrderPay";
				break;
			}
			default:
				break;		
			}
				
			if(store_hyOrders.size()>0) {
				WrapProcess process=new WrapProcess();
				process.setCount(store_hyOrders.size()); //待支付订单数量
				process.setProcessId(processId);
				wrapProcessList.add(process);
			}
			else {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //待支付订单数量
				process.setProcessId(processId);
				wrapProcessList.add(process);
			}
			filters.clear();
		}
	}
	/**
	 * 门店待确认的消团数据
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 * @param storeId
	 */
	public void mendianxiaotuan(List<WrapProcess> wrapProcessList,List<String> processList,String username,Long storeId){
		String sql = "select count(*) from hy_group_cancel_audit dai where dai.process_instance_id in ( "
				+ " SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '"
				+ username +"') AND rt.PROC_DEF_ID_ LIKE 'xiaotuanProcess%') and dai.group_id not in "
						+ " (select dai.group_id from hy_groupstorecancel cancel where cancel.group_id = dai.group_id and cancel.store_Id = "+storeId+")";
		List<Object[]> objects = hyAdminService.statis(sql);
		Object object =  objects.get(0);
		
		processList.add("xiaotuanProcess");
		WrapProcess process = new WrapProcess();
		process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
		process.setProcessId("xiaotuanProcess");
		wrapProcessList.add(process);
	}
	
	/**
	 * 增值服务打款
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 * @param storeId
	 */
	public void mendianzengzhifuwu(List<WrapProcess> wrapProcessList,List<String> processList,String username,Long storeId){
		String sql = "select count(*) from hy_added_service_transfer dai where dai.process_instance_id in ( "
				+ " SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '"
				+ username +"') AND rt.PROC_DEF_ID_ LIKE 'valueAdded%') and dai.step = 1 ";
						
		List<Object[]> objects = hyAdminService.statis(sql);
		Object object =  objects.get(0);
		
		processList.add("valueAdded");
		WrapProcess process = new WrapProcess();
		process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
		process.setProcessId("valueAdded");
		wrapProcessList.add(process);
	}
	
	/**
	 * 总公司产品中心经理
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 */
	public void zonggongsichanpinjingli(List<WrapProcess> wrapProcessList,List<String> processList,String username){
		//总公司旅游元素付尾款,总公司预付款,总公司计调报账
		String[] strings = {"banlanceDueCompany","prePay","regulateprocess"};	
		String[] datebase = {"hy_balance_due_apply","hy_pre_pay_supply","hy_regulate"};
		for(int i=0;i<strings.length;i++){
			StringBuffer buffer = new StringBuffer();
			buffer.append("select COUNT(*) from ");
			buffer.append(datebase[i]);
			buffer.append(" dai where dai.process_instance_id");
//			buffer.append(processes[i]);
			buffer.append(" in ( SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '");
			buffer.append(username);
			buffer.append("' ) AND rt.PROC_DEF_ID_ LIKE '");
			buffer.append(strings[i] + "%')");
			
			switch(strings[i]){
			case "banlanceDueCompany":buffer.append(" and dai.status = 1 and dai.step = 1");break;
			case "prePay":buffer.append(" and dai.step = 0 and dai.status = 0");break;
			case "regulateprocess":buffer.append(" and dai.status = 1");break;
			}
			List<Object[]> objects =  hyOrderService.statis(buffer.toString());
			Object object =  objects.get(0);
			
			processList.add(strings[i]);
			WrapProcess process = new WrapProcess();
			process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
			process.setProcessId(strings[i]);
			wrapProcessList.add(process);
		}
	}
	
	/**
	 * 分公司产品中心经理
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 */
	public void fengongsichanpinzhongxinjingli(List<WrapProcess> wrapProcessList,List<String> processList,String username){
		// 分公司预付款,分公司计调报帐
		String[] strings = {  "prePay", "regulateprocess" };
		String[] datebase = { "hy_pre_pay_supply", "hy_regulate" };
		for (int i = 0; i < strings.length; i++) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("select COUNT(*) from ");
			buffer.append(datebase[i]);
			buffer.append(" dai where dai.process_instance_id");
			// buffer.append(processes[i]);
			buffer.append(
					" in ( SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '");
			buffer.append(username);
			buffer.append("' ) AND rt.PROC_DEF_ID_ LIKE '");
			buffer.append(strings[i] + "%')");

			switch (strings[i]) {
			case "prePay":
				buffer.append(" and dai.step = 0 and dai.status = 0");
				break;
			case "regulateprocess":
				buffer.append(" and dai.status = 1");break;
			}
			List<Object[]> objects = hyOrderService.statis(buffer.toString());
			Object object = objects.get(0);

			processList.add(strings[i]);
			WrapProcess process = new WrapProcess();
			process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
			process.setProcessId(strings[i]);
			wrapProcessList.add(process);
		}
	}
	
	/**
	 * 市场部副总
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 */
	public void shichangbufuzong(List<WrapProcess> wrapProcessList,List<String> processList,String username){
		// 自动打款申请,提前打款,分公司团结算
		String[] strings = {"payServicePreTN",  "payServicePre", "branchsettleProcess" };
		String[] datebase = { "hy_payment_supplier", "hy_payment_supplier" ,"hy_payables_branchsettle"};
		for (int i = 0; i < strings.length; i++) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("select COUNT(*) from ");
			buffer.append(datebase[i]);
			buffer.append(" dai where dai.process_instance_id");
			// buffer.append(processes[i]);
			buffer.append(
					" in ( SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '");
			buffer.append(username);
			buffer.append("' ) AND rt.PROC_DEF_ID_ LIKE '");
			buffer.append(strings[i] + "%')");

			switch (strings[i]) {
			case "payServicePreTN":
				buffer.append(" and dai.step = 12 and dai.status = 1");
				break;
			case "payServicePre":
				buffer.append(" and dai.step = 3 and dai.status = 1");
				break;
			case "branchsettleProcess":
				buffer.append(" and dai.audit_status = 1");break;
			}
			List<Object[]> objects = hyOrderService.statis(buffer.toString());
			Object object = objects.get(0);

			processList.add(strings[i]);
			WrapProcess process = new WrapProcess();
			process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
			process.setProcessId(strings[i]);
			wrapProcessList.add(process);
		}
	}
	
	/**
	 * 分公司副总
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 */
	public void fengongsifuzong(List<WrapProcess> wrapProcessList,List<String> processList,String username){
		// 分公司预付款,分公司计调报帐,分公司旅游元素付尾款,增值服务打款
		String[] strings = { "prePay", "regulateprocess", "banlanceDueBranch","valueAdded" };
		String[] datebase = { "hy_pre_pay_supply", "hy_regulate", "hy_balance_due_apply","hy_added_service_transfer" };
		for (int i = 0; i < strings.length; i++) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("select COUNT(*) from ");
			buffer.append(datebase[i]);
			buffer.append(" dai where dai.process_instance_id");
			// buffer.append(processes[i]);
			buffer.append(
					" in ( SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '");
			buffer.append(username);
			buffer.append("' ) AND rt.PROC_DEF_ID_ LIKE '");
			buffer.append(strings[i] + "%')");

			switch (strings[i]) {
			case "prePay":
				buffer.append(" and dai.step = 0 and dai.state = 0");
				break;
			case "regulateprocess":
				buffer.append(" and dai.status = 1");
				break;
			case "banlanceDueBranch":
				buffer.append(" and dai.status = 1 and dai.step = 2");
				break;
			case "valueAdded":
				buffer.append(" and dai.status = 1 and dai.step = 2");
				break;
			}
			List<Object[]> objects = hyOrderService.statis(buffer.toString());
			Object object = objects.get(0);
			String string = strings[i];
			if(string.equals("banlanceDueBranch"))
				string = "balanceDue";

			processList.add(string);
			WrapProcess process = new WrapProcess();
			process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
			process.setProcessId(string);
			wrapProcessList.add(process);
		}
	}
	
	/**
	 * 总公司副总
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 */
	public void zonggongsifuzong(List<WrapProcess> wrapProcessList,List<String> processList,String username){
		//门店退出 ，总公司预付款，总公司计调报账， 总公司供应商付尾款， 江泰预充值
		String[] strings = {"storeLogout", "prePay", "regulateprocess", "banlanceDueCompany","PaymentJiangtai" };
		String[] datebase = { "hy_store_application","hy_pre_pay_supply", "hy_regulate", "hy_balance_due_apply","hy_paymentpre_jiangtai" };
		for (int i = 0; i < strings.length; i++) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("select COUNT(*) from ");
			buffer.append(datebase[i]);
			buffer.append(" dai where dai.process_instance_id");
			// buffer.append(processes[i]);
			buffer.append(
					" in ( SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '");
			buffer.append(username);
			buffer.append("' ) AND rt.PROC_DEF_ID_ LIKE '");
			buffer.append(strings[i] + "%')");

			switch (strings[i]) {
			case "storeLogout":
				buffer.append(" and dai.type = 4 and dai.status = 1");
				break;
			case "prePay":
				buffer.append(" and dai.step = 1 and dai.state = 0");
				break;
			case "regulateprocess":
				buffer.append(" and dai.status = 1");
				break;
			case "banlanceDueCompany":
				buffer.append(" and dai.status = 1 and dai.step = 2");
				break;
			case "PaymentJiangtai":
				buffer.append(" and dai.application_status = 1");
				break;
			}
			List<Object[]> objects = hyOrderService.statis(buffer.toString());
			Object object = objects.get(0);
			String string = strings[i];
			if(string.equals("banlanceDueCompany"))
				string = "balanceDue";

			processList.add(string);
			WrapProcess process = new WrapProcess();
			process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
			process.setProcessId(string);
			wrapProcessList.add(process);
		}
	}
	
	/**
	 * 采购部员工
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 */
	public void caigoubuyuangong(List<WrapProcess> wrapProcessList,List<String> processList,String username){
		//供应商退出  供应商新建团期 供应商申请特殊费率
		String[] strings = {"cgbgystuiyajinprocess", "xianlushenheprocess", "groupbiankoudianprocess"  };
		String[] datebase = { "hy_supplier_tuiyajin","hy_group", "hy_regulate", "hy_group_biankoudian" };
		for (int i = 0; i < strings.length; i++) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("select COUNT(*) from ");
			buffer.append(datebase[i]);
			buffer.append(" dai where dai.process_instance_id");
			// buffer.append(processes[i]);
			buffer.append(
					" in ( SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '");
			buffer.append(username);
			buffer.append("' ) AND rt.PROC_DEF_ID_ LIKE '");
			buffer.append(strings[i] + "%') and dai.audit_status = 1");

			List<Object[]> objects = hyOrderService.statis(buffer.toString());
			Object object = objects.get(0);

			processList.add(strings[i]);
			WrapProcess process = new WrapProcess();
			process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
			process.setProcessId(strings[i]);
			wrapProcessList.add(process);
		}
	}
	/**
	 * 采购部经理
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 */
	public void caigoubujingli(List<WrapProcess> wrapProcessList,List<String> processList,String username){
		// 提前打款
		String sql = "select count(*) from hy_payment_supplier dai where dai.process_instance_id in ( "
				+ " SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '"
				+ username +"') AND rt.PROC_DEF_ID_ LIKE 'payServicePre%') and dai.step = 1 and dai.status = 1";
						
		List<Object[]> objects = hyAdminService.statis(sql);
		Object object =  objects.get(0);
		
		processList.add("payServicePre");
		WrapProcess process = new WrapProcess();
		process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
		process.setProcessId("payServicePre");
		wrapProcessList.add(process);
		
		//供应商退出  供应商新建团期 供应商申请特殊费率
		caigoubujingli(wrapProcessList, processList, username);
	}
	
	/**
	 * 行政服务中心
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 */
	public void xingzhengfuwuzhongxin(List<WrapProcess> wrapProcessList,List<String> processList,String username){
		String sql = "select count(*) from hy_paymentpre_jiangtai dai where dai.process_instance_id in ( "
				+ " SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri WHERE ri.USER_ID_ = '"
				+ username +"') AND rt.PROC_DEF_ID_ LIKE 'PaymentJiangtai%') and dai.application_status = 0 ";
						
		List<Object[]> objects = hyAdminService.statis(sql);
		Object object =  objects.get(0);
		
		processList.add("PaymentJiangtai");
		WrapProcess process = new WrapProcess();
		process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
		process.setProcessId("PaymentJiangtai");
		wrapProcessList.add(process);
	}
	/**
	 * 票务内部供应商
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 */
	public void piaowuneibugongyingshang(List<WrapProcess> wrapProcessList,List<String> processList,String username){
		//门店退团和售后
		mendiantuituan(wrapProcessList, processList, 0, username);
		
		/**
		 * 判空
		 */
		String processId = "";
		for(int i=3;i<=6;i++){
			switch(i){
			case 3:{
				processId = "hotelShouHou";
				break;
			}
			case 4:{
				processId = "ticketShouHou";
				break;
			}
			case 5:{
				processId = "ticketandhotelShouHou";
				break;
			}
			default:
				break;
			}
			if(!processList.contains(processId)) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId(processId);
				wrapProcessList.add(process);
			}
		}
		
		for(int i=3;i<=6;i++){
			switch(i){
			case 3:{
				processId = "hotelTuiDing";
				break;
			}
			case 4:{
				processId = "ticketTuiDing";
				break;
			}
			case 5:{
				processId = "ticketandhotelTuiDing";
				break;
			}
			default:
				break;
			}
			if(!processList.contains(processId)) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId(processId);
				wrapProcessList.add(process);
			}
		}
		
		/**
		 * 供应商待确认订单（暂时3种，不确认有没有保险）
		 */
		HyAdmin hyAdmin =  hyAdminService.find(username);
		HyRole hyRole=hyAdmin.getRole();
		int[] orderConfirmList = {3,4,5};//订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
		
		List<Filter> filters = new ArrayList<>();
		for(int i : orderConfirmList){
			filters.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)); //2-待供应商确认
			filters.add(Filter.eq("type", i)); 
			
			//如果不是门店经理,只能看自己创建的
			if(!hyRole.getName().contains("经理")) {
				filters.add(Filter.eq("creatorId", username));
			}
			
			List<HyOrder> hyOrders=hyOrderService.findList(null,filters,null);
			String id = "";
			switch (i) {
			case 3:{
				id = "hotelOrderConfirm";
				break;
			}
			case 4:{
				id = "menpiaoOrderConfirm";
				break;
			}
			case 5:{
				id = "ticketandhotelOrderConfirm";
				break;
			}
			default:
				break;
			}
			
			
			if(hyOrders.size()>0) {
				WrapProcess process=new WrapProcess();
				process.setCount(hyOrders.size()); //待确定订单数量
				process.setProcessId(id);
				wrapProcessList.add(process);
			}  
			else {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //待确定订单数量
				process.setProcessId(id);
				wrapProcessList.add(process);
			}
		}
		
		List<Filter> filterss=new ArrayList<>();
		/**
		 * 供应商建促销（票务3种）（暂时三种）
		 */
		for(int i=0;i<=2;i++){
			filterss.clear();
			filterss.add(Filter.eq("state", 0));
			filterss.add(Filter.eq("activityType", i));
			List<HyPromotionActivity> activities = hyPromotionActivityService.findList(null,filterss,null);
			String name = "";
			String id = "";
			switch (i) {
			case 0:{
				id = "ticketPromotion";
				break;
			}
			case 1:{
				id = "hotelPromotion";
				break;
			}
			case 2:{
				id = "ticketandhotelPromotion";
				break;
				}
			}
			WrapProcess ticket_promotion=new WrapProcess();
			ticket_promotion.setCount(activities.size());
			ticket_promotion.setProcessId(id);
			wrapProcessList.add(ticket_promotion);
		}
	}
	
	/**
	 * 线路内部供应商
	 * @param wrapProcessList
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void xianluneibugongyingshang(List<WrapProcess> wrapProcessList,HyAdmin hyAdmin,List<String> processList,String username){
		List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(username).list();
		for(Task task:tasks) {

			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];	
			String processInstanceId=task.getProcessInstanceId(); //流程实例id
			
			
			/**
			 * 门店提出售后退款
			 */
			if(processId.equals("storeShouHou")) {
				List<Filter> filters=new ArrayList<>();
				filters.add(Filter.eq("status",0));  //待供应商审核
				filters.add(Filter.eq("type", HyOrderApplication.STORE_CUSTOMER_SERVICE)); //售后退款			
				List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filters,null);
				for(HyOrderApplication application:orderApplications) {
					Long orderId=application.getOrderId(); //订单id
					filters.clear();
					String processName = "线路售后退款审核";
					HyOrder hyOrder=hyOrderService.find(orderId);
					if(hyOrder.getType() == 2){
						processId = "rengoumenpiaoShouHou";
						processName = "认购门票售后申请";
					}else if(hyOrder.getType()!=1){
						continue;
					}
					//如果登录账号为合同负责人
					if(hyAdmin.getHyAdmin()==null) {
						List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
						hyAdmins.add(hyAdmin);
						List<String> accounts=new ArrayList<>();
						for(HyAdmin admin:hyAdmins) {
							accounts.add(admin.getUsername());
						}
						if(accounts.contains(hyOrder.getSupplier().getUsername())) {
							if(processInstanceId.equals(application.getProcessInstanceId())) {
								//如果已经有一个改流程的代办事项,将数量加1
								if(processList.contains(processId)) {
									int num=processList.indexOf(processId);
									int preCount=wrapProcessList.get(num).getCount();
									wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
								}
								else {
									processList.add(processId);
									WrapProcess process=new WrapProcess();
									process.setCount(1); //将数量设置成1
									process.setProcessId(processId);
//									process.setProcessName(processMap.get(processId));
									wrapProcessList.add(process);
								}
							}
						}
					}
					//如果登录账号为子账号
					else {
						if(hyOrder.getSupplier().getUsername().equals(username)) {
							if(processInstanceId.equals(application.getProcessInstanceId())) {
								//如果已经有一个改流程的代办事项,将数量加1
								if(processList.contains(processId)) {
									int num=processList.indexOf(processId);
									int preCount=wrapProcessList.get(num).getCount();
									wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
								}
								else {
									processList.add(processId);
									WrapProcess process=new WrapProcess();
									process.setCount(1); //将数量设置成1
									process.setProcessId(processId);
//									process.setProcessName(processMap.get(processId));
									wrapProcessList.add(process);
								}
							}
						}
					}
				}
			}
			
			/**
			 * 门店提出退团申请
			 */
			else if(processId.equals("storeTuiTuan")) {
				List<Filter> filters=new ArrayList<>();
				filters.add(Filter.eq("status",0));  //待供应商审核
				filters.add(Filter.eq("type", HyOrderApplication.STORE_CANCEL_GROUP)); //0-门店退团			
				List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filters,null);
				for(HyOrderApplication application:orderApplications) {
					Long orderId=application.getOrderId(); //订单id
					filters.clear();
					HyOrder hyOrder=hyOrderService.find(orderId);
					String processName = "门店退团审核";
					if(hyOrder.getType() == 2){
						processId = "rengoumenpiaoTuiDing";
						processName = "认购门票退订申请";
					}else if(hyOrder.getType()!=1){
						continue;
					}
					//如果登录账号为合同负责人
					if(hyAdmin.getHyAdmin()==null) {
						List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
						hyAdmins.add(hyAdmin);
						List<String> accounts=new ArrayList<>();
						for(HyAdmin admin:hyAdmins) {
							accounts.add(admin.getUsername());
						}
						if(hyOrder.getSupplier()!=null) {
							if(accounts.contains(hyOrder.getSupplier().getUsername())==true) {
								if(processInstanceId.equals(application.getProcessInstanceId())) {
									//如果已经有一个改流程的代办事项,将数量加1
									if(processList.contains(processId)) {
										int num=processList.indexOf(processId);
										int preCount=wrapProcessList.get(num).getCount();
										wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
									}
									else {
										processList.add(processId);
										WrapProcess process=new WrapProcess();
										process.setCount(1); //将数量设置成1
										process.setProcessId(processId);
//										process.setProcessName(processMap.get(processId));
										wrapProcessList.add(process);
									}
								}
							}
						}
						
					}
					//如果登录账号为子账号
					else {
						if(hyOrder.getSupplier()!=null) {
							if(hyOrder.getSupplier().getUsername().equals(username)) {
								if(processInstanceId.equals(application.getProcessInstanceId())) {
									//如果已经有一个改流程的代办事项,将数量加1
									if(processList.contains(processId)) {
										int num=processList.indexOf(processId);
										int preCount=wrapProcessList.get(num).getCount();
										wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
									}
									else {
										processList.add(processId);
										WrapProcess process=new WrapProcess();
										process.setCount(1); //将数量设置成1
										process.setProcessId(processId);
										wrapProcessList.add(process);
									}
								}
							}
						}	
					}
				}
			}
			//供应商付尾款
			else if(processId.equals("banlanceDueCompany") || processId.equals("banlanceDueBranch")){
				if(hyAdmin.getHyAdmin()!=null) {
					//不是经理
					continue;
				}
				List<Filter> filters=new ArrayList<>();
				filters.add(Filter.eq("status", 1));
				//待(总公司/分公司)产品中心经理审核
				filters.add(Filter.eq("step", 1));
				List<BalanceDueApply> balanceDueApplies = balanceDueApplyService.findList(null,filters,null);
				for(BalanceDueApply balanceDueApply : balanceDueApplies){
					if(balanceDueApply.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains("balanceDue")){
							int num = wrapProcessList.indexOf("balanceDue");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add("balanceDue");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("balanceDue");
							wrapProcessList.add(process);
						}
					}
				}
			}
			
    	}
    	
    	//内部没有打款单
//    	if(!processList.contains("payServicePreTN")) {
//			WrapProcess process=new WrapProcess();
//			process.setCount(0); //将数量设置成0
//			process.setProcessId("payServicePreTN");
//			process.setProcessName("T+N自动打款打款单审核");
//			wrapProcessList.add(process);
//		}
    	if(hyAdmin.getHyAdmin()==null) {
    		if(!processList.contains("balanceDue")) {
    			WrapProcess process=new WrapProcess();
    			process.setCount(0); //将数量设置成0
    			process.setProcessId("balanceDue");
    			wrapProcessList.add(process);
    		}
		}
		
		if(!processList.contains("storeTuiTuan")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeTuiTuan");
			wrapProcessList.add(process);
		}
		if(!processList.contains("storeShouHou")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeShouHou");
			wrapProcessList.add(process);
		}
		if(!processList.contains("rengoumenpiaoTuiDing")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("rengoumenpiaoTuiDing");
			wrapProcessList.add(process);
		}
		if(!processList.contains("rengoumenpiaoShouHou")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("rengoumenpiaoShouHou");
			wrapProcessList.add(process);
		}
		/**
		 * 线路促销走流程好像有点问题,直接查数据库
		 */
		List<Filter> filterss=new ArrayList<>();
		filterss.add(Filter.eq("state",0));  //筛选状态为审核中
		filterss.add(Filter.eq("isCaigouti", true));
		//如果是合同负责人,能看到子账号下所有信息
		if(hyAdmin.getHyAdmin()==null) {
			List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
			hyAdmins.add(hyAdmin);
			filterss.add(Filter.in("operator", hyAdmins));
		}
		//如果是子账号,只能看到自己负责的部分
		else {
			filterss.add(Filter.eq("operator", hyAdmin));
		}
		List<LinePromotion> linepromotions=linePromotionService.findList(null,filterss,null);
		WrapProcess process_promotion=new WrapProcess();
		process_promotion.setCount(linepromotions.size());
		process_promotion.setProcessId("LinePromotion");
		wrapProcessList.add(process_promotion);
		
		/**
		 * 认购门票促销审核
		 */
		
		filterss.clear();
		filterss.add(Filter.eq("state", 0));
		filterss.add(Filter.eq("activityType", 3));
		filterss.add(Filter.eq("isCaigouti", true));
		//如果是合同负责人,能看到子账号下所有信息
				if(hyAdmin.getHyAdmin()==null) {
					List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
					hyAdmins.add(hyAdmin);
					filterss.add(Filter.in("jidiao", hyAdmins));
				}
				//如果是子账号,只能看到自己负责的部分
				else {
					filterss.add(Filter.eq("jidiao", hyAdmin));
				}
		List<HyPromotionActivity> activities = hyPromotionActivityService.findList(null,filterss,null);
		String name = "认购门票促销审核";
		String id = "rengoumenpiaoPromotion";
		WrapProcess ticket_promotion=new WrapProcess();
		ticket_promotion.setCount(activities.size());
		ticket_promotion.setProcessId(id);
		wrapProcessList.add(ticket_promotion);
		
		
		
		/**
		 * 供应商待确认订单,没有走流程,直接查数据库
		 */
		filterss.clear();
		filterss.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)); //2-待供应商确认
		filterss.add(Filter.eq("type", 1)); //先只管线路订单
		
		//如果是合同合同负责人,看所有订单
		if(hyAdmin.getHyAdmin()==null) {
			List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
			hyAdmins.add(hyAdmin);
			filterss.add(Filter.in("supplier", hyAdmins)); //产品创建人,团的计调
		}
		//如果不是合同合同负责人,筛选本人上的产品
		else {
			filterss.add(Filter.eq("supplier", hyAdmin)); //产品创建人,团的计调
		}
		
		List<HyOrder> hyOrders=hyOrderService.findList(null,filterss,null);
		if(hyOrders.size()>0) {
			WrapProcess process=new WrapProcess();
			process.setCount(hyOrders.size()); //待确定订单数量
			process.setProcessId("orderConfirm");
			wrapProcessList.add(process);
		}  
		else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("orderConfirm");
			wrapProcessList.add(process);
		}
		
		/**
		 * 认购门票供应商待确认订单
		 */
		filterss.clear();
		filterss.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)); //2-待供应商确认
		filterss.add(Filter.eq("type", 2)); //认购门票订单
		
		//如果是合同合同负责人,看所有订单
		if(hyAdmin.getHyAdmin()==null) {
			List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
			hyAdmins.add(hyAdmin);
			filterss.add(Filter.in("supplier", hyAdmins)); //产品创建人,团的计调
		}
		//如果不是合同合同负责人,筛选本人上的产品
		else {
			filterss.add(Filter.eq("supplier", hyAdmin)); //产品创建人,团的计调
		}
		
		List<HyOrder> visaOrders=hyOrderService.findList(null,filterss,null);
		if(visaOrders.size()>0) {
			WrapProcess process=new WrapProcess();
			process.setCount(hyOrders.size()); //待确定订单数量
			process.setProcessId("rengoumenpiaoorderConfirm");
			wrapProcessList.add(process);
		}  
		else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("rengoumenpiaoorderConfirm");
			wrapProcessList.add(process);
		}
		
		
		
		
	}
	
	/**
	 * 0新建门店，1门店交押金，2门店续签，3门店交管理费，4门店退出
	 * @param wrapProcessList
	 * @param processList
	 * @param username
	 * @param type
	 */
	public void mendianapplication(List<WrapProcess> wrapProcessList,List<String> processList,String username,Integer type,String string){
		String sql = "select COUNT(*) from hy_store_application dai where dai.process_instance_id in ( "
				+" SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri "
				+"WHERE ri.USER_ID_ = '"+username+"') AND rt.PROC_DEF_ID_ LIKE '"+string+"%') and dai.status = 0 and dai.type = " + type;
		List<Object[]> objects =  hyOrderService.statis(sql);
		Object object =  objects.get(0);
		
		processList.add(string);
		WrapProcess process = new WrapProcess();
		process.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
		process.setProcessId(string);
		wrapProcessList.add(process);
		
	}
	/**
	 * 6种退团和售后
	 * @param wrapProcessList
	 * @param task
	 * @param processList
	 * @param status 1:待品控员工审核 2：待财务审核
	 * @param username
	 */
	public void mendiantuituan(List<WrapProcess> wrapProcessList,List<String> processList,Integer status,String username){
		List<Filter> filters = new ArrayList<>();
		/**
		 * 门店退团
		 */
			//status  0待供应商审核 1:待品控员工审核 2：待财务审核
			String sql = "select o.type,count(*) from hy_order_application application,hy_order o where application.process_instance_id in ( "
						+" SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri " 
						+" WHERE ri.USER_ID_ = '"+username +"') AND rt.PROC_DEF_ID_ LIKE 'storeTuiTuan%') and application.order_id = o.id "
						+" and application.type = 0 and application.status = "+ status +" group by o.type";
			List<Object[]> objects = hyOrderApplicationService.statis(sql);
			for(Object[] object:objects){
				Integer type = Integer.valueOf(object[0].toString());
				Integer count = Integer.valueOf(object[1].toString());
				String processId = "";
				switch(type){
				case 1: {
					processId = "storeTuiTuan";
					break;
				}
				case 2: {
					processId = "rengoumenpiaoTuiDing";
					break;
				}
				case 3:{
					processId = "hotelTuiDing";
					break;
				}
				case 4:{
					processId = "ticketTuiDing";
					break;
				}
				case 5:{
					processId = "ticketandhotelTuiDing";
					break;
				}
				case 7:{
					processId = "visaTuiDing";
					break;
				}
				default:
					break;
				}
				
				processList.add(processId);
				WrapProcess process = new WrapProcess();
				process.setCount(count); 
				process.setProcessId(processId);
				wrapProcessList.add(process);	
			}
			
			/**
			 * 门店售后
			 */
			//status  1:待品控员工审核 2：待财务审核
			String sql2 = "select o.type,count(*) from hy_order_application application,hy_order o where application.process_instance_id in ( "
						+" SELECT rt.PROC_INST_ID_ FROM ACT_RU_TASK rt WHERE rt.ID_ IN (SELECT ri.TASK_ID_ FROM ACT_RU_IDENTITYLINK ri " 
						+" WHERE ri.USER_ID_ = '"+username +"') AND rt.PROC_DEF_ID_ LIKE 'storeShouHou%') and application.order_id = o.id "
						+" and application.type = 1 and application.status = "+ status +" group by o.type";
			List<Object[]> objects2 = hyOrderApplicationService.statis(sql2);
			for(Object[] object:objects2){
				Integer type = Integer.valueOf(object[0].toString());
				Integer count = Integer.valueOf(object[1].toString());
				String processId = "";
				switch(type){
				case 1: {
					processId = "storeShouHou";
					break;
				}
				case 2: {
					processId = "rengoumenpiaoShouHou";
					break;
				}
				case 3:{
					processId = "hotelShouHou";
					break;
				}
				case 4:{
					processId = "ticketShouHou";
					break;
				}
				case 5:{
					processId = "ticketandhotelShouHou";
					break;
				}
				case 7:{
					processId = "visaShouHou";
					break;
				}
				default:
					break;
				}
				
				processList.add(processId);
				WrapProcess process = new WrapProcess();
				process.setCount(count); 
				process.setProcessId(processId);
				wrapProcessList.add(process);	
			}	
	}

	/**
	 * 门店售后和退订的判空操作
	 * @param wrapProcessList
	 * @param processList
	 */
	public void mendiantuituanpankong(List<WrapProcess> wrapProcessList,List<String> processList){
		String processId = "";
		String processName = "";
		for(int i=1;i<=6;i++){
			switch(i){
			case 1: {
				processId = "storeShouHou";
				break;
			}
			case 2: {
				processId = "rengoumenpiaoShouHou";
				break;
			}
			case 3:{
				processId = "hotelShouHou";
				break;
			}
			case 4:{
				processId = "ticketShouHou";
				break;
			}
			case 5:{
				processId = "ticketandhotelShouHou";
				break;
			}
			case 6:{
				processId = "visaShouHou";
				break;
			}
			default:
				break;
			}
			if(!processList.contains(processId)) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId(processId);
				wrapProcessList.add(process);
			}
		}
		
		for(int i=1;i<=6;i++){
			switch(i){
			case 1: {
				processId = "storeTuiTuan";
				break;
			}
			case 2: {
				processId = "rengoumenpiaoTuiDing";
				break;
			}
			case 3:{
				processId = "hotelTuiDing";
				break;
			}
			case 4:{
				processId = "ticketTuiDing";
				break;
			}
			case 5:{
				processId = "ticketandhotelTuiDing";
				break;
			}
			case 6:{
				processId = "visaTuiDing";
				break;
			}
			default:
				break;
			}
			if(!processList.contains(processId)) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId(processId);
				wrapProcessList.add(process);
			}
		}

	}
	
	@RequestMapping(value="/role")
	@ResponseBody
	public Json findRole(HttpSession session){
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			HyDepartmentModel departmentModel=department.getHyDepartmentModel();
			String departmentModelName=departmentModel.getName();
			HyRole hyRole=hyAdmin.getRole();
			Map<String,Object> obj=new HashMap<String,Object>();
			
			if(departmentModelName.contains("供应商")){ //线路外部供应商
				
				List<Filter> filters2=new ArrayList<>();
				filters2.add(Filter.eq("liable", findPAdmin(hyAdmin)));
				filters2.add(Filter.eq("contractStatus", ContractStatus.zhengchang));
				List<HySupplierContract> supplierContracts=hySupplierContractService.findList(null,filters2,null);
				HySupplierContract supplierContract=supplierContracts.get(0);
				HySupplier hySupplier=supplierContract.getHySupplier(); //找到所属供应商
				
				//先只管线路
			    if(hySupplier.getIsLine()==true) {
			    	obj.put("accountRole", "gongyinshang"); 
			    }else {
			    	obj.put("accountRole", "ticketSupplier"); //返回账号角色是票务外部供应商
			    }
			}
			
			/**
			 * 如果登录账号的角色是门店 ,返回门店相关内容
			 * (分公司门店和非虹宇门店)
			 */
//			else if(departmentModelName.contains("门店")){
			else if(departmentModelName.contains("分公司门店") || departmentModelName.contains("非虹宇门店")) {
				obj.put("accountRole", "mendian");
			}
			else if(departmentModelName.contains("直营门店")&& hyRole.getName().contains("经理")){
				obj.put("accountRole", "zhiyingmendianjingli");
			}
			/**
			 * 直营门店（员工）
			 */
			else if(departmentModelName.contains("直营门店")&& !hyRole.getName().contains("经理")){
				obj.put("accountRole", "zhiyingmendianyuangong");
			}
			/**
			 * 连锁发展（经理）
			 */
			else if(departmentModelName.contains("连锁发展") && hyRole.getName().contains("经理")){
				obj.put("accountRole", "liansuofazhanjingli");
			}
			/**
			 * 连锁发展（员工）
			 */
			else if(departmentModelName.contains("连锁发展") && !hyRole.getName().contains("经理")){
				obj.put("accountRole", "liansuofazhanyuangong");
			}
			/**
			 * 总公司产品经理
			 */
			else if(departmentModelName.contains("总公司产品研发中心") && hyRole.getName().contains("经理")){
				obj.put("accountRole", "zonggongsichanpinjingli");
			}
			/**
			 * 分公司产品中心经理
			 */
			else if(departmentModelName.contains("分公司产品中心") && hyRole.getName().contains("经理")){
				obj.put("accountRole", "fengongsichanpinzhongxinjingli");
			}
			/**
			 * 市场部副总
			 */
			else if(departmentModelName.contains("市场部")){
				obj.put("accountRole", "shichangbufuzong");
			}
			/**
			 * 分公司副总
			 */
			else if(departmentModelName.equals("分公司") && hyRole.getName().contains("副总")){
				obj.put("accountRole", "fengongsifuzong");
			}
			/**
			 * 总公司副总
			 */
			else if(departmentModelName.equals("总公司") && hyRole.getName().contains("副总") ){
				obj.put("accountRole", "zonggongsifuzong");
			}
			/**
			 * 分公司财务
			 */
			else if(departmentModelName.equals("分公司财务部")){
				obj.put("accountRole", "fengongsicaiwu");
			}
			/**
			 * 总公司财务
			 */
			else if(departmentModelName.equals("总公司财务部")){
				obj.put("accountRole", "zongongsicaiwu");
			}
			/**
			 * 品控员工（查票务上产品的无重审核）
			 */
			else if(departmentModelName.contains("品控")){
				obj.put("accountRole", "pinkongyuangong");
			}
			/**
			 * 采购部经理（采购部）（判断角色）
			 */
			else if(departmentModelName.contains("采购部") && hyRole.getName().contains("经理")){
				obj.put("accountRole", "caigoubujingli");
			}
			/**
			 * 采购部员工
			 */
			else if(departmentModelName.contains("采购部") && !hyRole.getName().contains("经理")){
				obj.put("accountRole", "caigoubuyuangong");
			}
			/**
			 * 行政服务中心
			 */
			else if(departmentModelName.contains("行政服务")){
				obj.put("accountRole", "zonggongsixingzhengfuwuzhongxin");
				
			}
			/**
			 * 线路内部供应商（总公司出境部国内部）
			 */
			else if(departmentModelName.contains("出境部")||departmentModelName.contains("国内部")
					||departmentModelName.contains("总公司汽车部") ||departmentModelName.contains("活动部") ){
				obj.put("accountRole", "xianluneibugongyingshang");
			}
			/**
			 * 票务内部供应商（总公司票务部）
			 */
			else if(departmentModelName.contains("票务部")){
				obj.put("accountRole", "piaowuneibugongyingshang");
			}
			/**
			 * 分公司汽车部（经理）
			 */
			else if(departmentModelName.contains("汽车部")&& hyRole.getName().contains("经理")){
				obj.put("accountRole","fengongsiqichebujingli");
			}
			
			/**
			 * 分公司汽车部（员工）
			 */
			else if(departmentModelName.contains("汽车部")&& !hyRole.getName().contains("经理")){
				obj.put("accountRole","fengongsiqichebuyuangong");
			}
			/**
			 * 导游服务中心
			 */
			else if(departmentModelName.contains("导游")){
				obj.put("accountRole", "daoyoufuwuzhongxin");
			}
			/*
			 * 活动部
			 */
			else if(hyRole.getName().contains("活动部")){
				obj.put("accountRole", "huodongbu");
			}
			else {
				obj.put("accountRole", "qita");
			}
			json.setMsg("获取角色成功");
			json.setSuccess(true);
			json.setObj(obj);
			
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("获取角色失败");
			json.setSuccess(false);
		}
		return json;
	}
	
	public HyAdmin findPAdmin(HyAdmin admin) {
		HyAdmin hyAdmin=new HyAdmin();
		try {
			//如果是父帐号,即合同负责人
			if(admin.getHyAdmin()==null) {
				hyAdmin=admin;
			}
			//如果是子账号,查找其父帐号
			else {
				while(admin.getHyAdmin()!=null) {
					admin=admin.getHyAdmin();
				}
				hyAdmin=admin;
			}
		}
		catch(Exception e) {		
		    e.printStackTrace();
		}
		return hyAdmin;
	}
}
