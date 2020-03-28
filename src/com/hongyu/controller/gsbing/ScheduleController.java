package com.hongyu.controller.gsbing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.iterators.FilterListIterator;
import org.hibernate.annotations.Filters;
import org.hibernate.validator.constraints.Mod11Check.ProcessingDirection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.Constants.AuditStatus;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

@Controller
@RequestMapping("admin/homepage/schedule")
//@RequestMapping("homepage/schedule")
public class ScheduleController {
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
		private String processName;
		private Integer count;
		public String getProcessId() {
			return processId;
		}
		public void setProcessId(String processId) {
			this.processId = processId;
		}
		public String getProcessName() {
			return processName;
		}
		public void setProcessName(String processName) {
			this.processName = processName;
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
	public Json listview(HttpSession session,HttpServletRequest request)
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

			Map<String,String> processMap=new HashMap<>();
			Map<String,Object> obj=new HashMap<String,Object>();
			
//			processMap.put("banlanceDueBranch", "分公司旅游元素付尾款审核");
//			processMap.put("banlanceDueCompany", "总公司旅游元素付尾款审核");
//			processMap.put("biangengkoudianprocess", "供应商合同变更扣点审核");
//			processMap.put("branchRecharge", "分公司充值审核");
//			processMap.put("branchsettleProcess", "分公司团结算审核");
//			processMap.put("cgbgysprocess", "供应商新建审核");
//			processMap.put("jiaoyajinprocess", "供应商缴押金审核");
//			processMap.put("cgbgystuiyajinprocess", "供应商退押金审核");
//			processMap.put("creditfhyProcess", "非虹宇门店授信审核");
//			processMap.put("dimissionManagement", "员工离职申请审核");
//			processMap.put("distributorPrechargePro", "分销商预充值审核");
//			processMap.put("distributorSettlementPro","分销商渠道结算审核");
//			processMap.put("groupbiankoudianprocess", "团变更费率审核");
//			processMap.put("hotelandsceneRoomPriceProcess", "票务部产品酒加景审核");
//			processMap.put("hotelRoomPriceProcess", "票务产品酒店审核");
//			processMap.put("jiaoguanlifei", "门店交管理费审核");
//			processMap.put("jiaoyajin", "门店交押金审核");
			processMap.put("LinePromotion", "促销审核");
			processMap.put("PaymentJiangtai", "江泰预充值审核");
			processMap.put("payServicePre", "提前打款打款单审核");
			processMap.put("payServicePreTN", "T+N自动打款打款单审核");
			processMap.put("prePay", "预付款审核");
			processMap.put("regulateprocess", "计调报账审核");
//			processMap.put("sceneticketPriceProcess", "票务产品电子门票审核");
//			processMap.put("StockInProcess", "商品入库审核");
//			processMap.put("storefhyProcess", "非虹宇门店新建审核");
//			processMap.put("storeLogout", "门店退出审核");
//			processMap.put("storeRecharge", "门店充值审核");
//			processMap.put("storeRegistration", "门店注册审核");
//			processMap.put("storeRenew", "门店续签审核");
			processMap.put("storeShouHou", "门店售后退款审核");
			processMap.put("storeTuiTuan", "门店退团审核");
//			processMap.put("subscribeTicket", "票务产品认购门票审核");
			processMap.put("suppilerDismissOrder", "驳回订单审核");
//			processMap.put("swdprocess", "甩尾单审核");
//			processMap.put("ticketPay", "向票务供应商打款审核");
//			processMap.put("tuiyajinprocess", "采购部供应商退部分押金审核");
			processMap.put("valueAdded", "门店增值服务打款审核");
			processMap.put("visaPriceProcess", "票务产品签证审核");
//			processMap.put("xianlushenheprocess", "线路供应商建团上产品审核");
			processMap.put("xiaotuanProcess", "消团审核");
			processMap.put("xuqianprocess", "供应商合同合同续签审核");
		
			
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
				if(supplierContracts == null || supplierContracts.size() == 0){
					throw new Exception("该账号没有正常的合同");
				}
				HySupplierContract supplierContract=supplierContracts.get(0);
				HySupplier hySupplier=supplierContract.getHySupplier(); //找到所属供应商
				
				//先只管线路
			    if(hySupplier.getIsLine()==true) {
			    	obj.put("accountRole", "gongyinshang"); //返回账号角色是供应商
			    	for(Task task:tasks) {

						String processDefinitionId=task.getProcessDefinitionId();
						String[] strs=processDefinitionId.split(":");
						//找出流程id
						String processId=strs[0];	
						String processInstanceId=task.getProcessInstanceId(); //流程实例id
						
						/**
						 * 自动打款申请
						 */
						if(processId.equals("payServicePreTN")) {
							List<Filter> filters=new ArrayList<>();
							filters.add(Filter.eq("status",1));  //筛选状态为审核中
							filters.add(Filter.eq("supplierContract", supplierContract)); //如果是合同负责人账号,按合同筛选
							//如果不是合同负责人,只查找自己负责的
							if(hyAdmin.getHyAdmin()!=null) {
								filters.add(Filter.eq("operator", hyAdmin));
							}
							List<PaymentSupplier> paymentSuppliers=paymentSupplierService.findList(null,filters,null);
							filters.clear();
							for(PaymentSupplier payment:paymentSuppliers) {
								if(processInstanceId.equals(payment.getProcessInstanceId())) {
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
										process.setProcessName(processMap.get(processId));
										wrapProcessList.add(process);
									}
								}
							}
						}
						
//						/**
//						 * 线路促销申请 
//						 */
//						else if(processId.equals("LinePromotion")) {
//							List<Filter> filters=new ArrayList<>();
//							filters.add(Filter.eq("state",0));  //筛选状态为审核中
//							filters.add(Filter.eq("isCaigouti", true));
//							//如果是合同负责人,能看到子账号下所有信息
//							if(hyAdmin.getHyAdmin()==null) {
//								List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
//								hyAdmins.add(hyAdmin);
//								filters.add(Filter.in("operator", hyAdmins));
//							}
//							//如果是子账号,只能看到自己负责的部分
//							else {
//								filters.add(Filter.eq("operator", hyAdmin));
//							}
//							List<LinePromotion> linepromotions=linePromotionService.findList(null,filters,null);
//							for(LinePromotion promotion:linepromotions) {
//								if(processInstanceId.equals(promotion.getProcessInstanceId())) {
//									//如果已经有一个该流程的代办事项,将数量加1
//									if(processList.contains(processId)) {
//										int num=processList.indexOf(processId);
//										int preCount=wrapProcessList.get(num).getCount();
//										wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
//									}
//									else {
//										processList.add(processId);
//										WrapProcess process=new WrapProcess();
//										process.setCount(1); //将数量设置成1
//										process.setProcessId(processId);
//										process.setProcessName(processMap.get(processId));
//										wrapProcessList.add(process);
//									}
//								}
//							}
//						}
						
						/**
						 * 门店提出售后退款
						 */
						else if(processId.equals("storeShouHou")) {
							List<Filter> filters=new ArrayList<>();
							filters.add(Filter.eq("status",0));  //待供应商审核
							filters.add(Filter.eq("type", HyOrderApplication.STORE_CUSTOMER_SERVICE)); //售后退款			
							List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filters,null);
							for(HyOrderApplication application:orderApplications) {
								Long orderId=application.getOrderId(); //订单id
								filters.clear();
								HyOrder hyOrder=hyOrderService.find(orderId);
								String processName = processMap.get(processId);
								if(hyOrder.getType() == 7){
									processId = "visaShouHou";
									processName = "签证订单售后";
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
//												process.setProcessName(processMap.get(processId));
												process.setProcessName(processName);
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
//												process.setProcessName(processMap.get(processId));
												process.setProcessName(processName);
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
								String processName = processMap.get(processId);
								if(hyOrder.getType() == 7){
									processId = "visaShouHou";
									processName = "签证订单售后";
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
//													process.setProcessName(processMap.get(processId));
													process.setProcessName(processName);
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
													process.setProcessName(processMap.get(processId));
													wrapProcessList.add(process);
												}
											}
										}
									}	
								}
							}
						}						
			    	}
			    	
			    	if(!processList.contains("payServicePreTN")) {
						WrapProcess process=new WrapProcess();
						process.setCount(0); //将数量设置成0
						process.setProcessId("payServicePreTN");
						process.setProcessName("T+N自动打款打款单审核");
						wrapProcessList.add(process);
					}
				    
					
					if(!processList.contains("storeTuiTuan")) {
						WrapProcess process=new WrapProcess();
						process.setCount(0); //将数量设置成0
						process.setProcessId("storeTuiTuan");
						process.setProcessName("门店退团申请");
						wrapProcessList.add(process);
					}
					if(!processList.contains("storeShouHou")) {
						WrapProcess process=new WrapProcess();
						process.setCount(0); //将数量设置成0
						process.setProcessId("storeShouHou");
						process.setProcessName("门店售后申请");
						wrapProcessList.add(process);
					}
					if(!processList.contains("visaTuiDing")) {
						WrapProcess process=new WrapProcess();
						process.setCount(0); //将数量设置成0
						process.setProcessId("visaTuiDing");
						process.setProcessName("签证退订申请");
						wrapProcessList.add(process);
					}
					if(!processList.contains("visaShouHou")) {
						WrapProcess process=new WrapProcess();
						process.setCount(0); //将数量设置成0
						process.setProcessId("visaShouHou");
						process.setProcessName("签证申请");
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
					process_promotion.setProcessName("线路促销审核");
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
					ticket_promotion.setProcessName("签证促销审核");
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
						process.setProcessName("订单确认");
						wrapProcessList.add(process);
					}  
					else {
						WrapProcess process=new WrapProcess();
						process.setCount(0); //待确定订单数量
						process.setProcessId("orderConfirm");
						process.setProcessName("订单待供应商确认");
						wrapProcessList.add(process);
					}
					
					/**
					 * 签证供应商待确认订单
					 */
					filterss.clear();
					filterss.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)); //2-待供应商确认
					filterss.add(Filter.eq("type", 7)); //先只管线路订单
					Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
					filterss.add(Filter.in("supplier", hyAdmins));
					
					
					List<HyOrder> visaOrders=hyOrderService.findList(null,filterss,null);
					if(visaOrders.size()>0) {
						WrapProcess process=new WrapProcess();
						process.setCount(visaOrders.size()); //待确定订单数量
						process.setProcessId("visaorderConfirm");
						process.setProcessName("签证订单确认");
						wrapProcessList.add(process);
					}  
					else {
						WrapProcess process=new WrapProcess();
						process.setCount(0); //待确定订单数量
						process.setProcessId("visaorderConfirm");
						process.setProcessName("签证订单确认");
						wrapProcessList.add(process);
					}
			    }
			    
			    //票务外部供应商
			    else {
			    	obj.put("accountRole", "ticketSupplier"); //返回账号角色是票务外部供应商
			    	piaowuwaibugongyingshang(wrapProcessList, tasks, hyAdmin, processList, username, supplierContract,session,request);
			    }
			}
			
			/**
			 * 如果登录账号的角色是门店 ,返回门店相关内容
			 * (分公司门店和非虹宇门店)
			 */
//			else if(departmentModelName.contains("门店")){
			else if(departmentModelName.contains("分公司门店") || departmentModelName.contains("非虹宇门店")) {
				obj.put("accountRole", "mendian");
			    List<Filter> filteres=new ArrayList<>();
				filteres.add(Filter.eq("department", department)); //根据部门查找门店
				List<Store> stores=storeService.findList(null,filteres,null);
				if(stores.isEmpty()) {
					throw new RuntimeException("当前登录员工不属于任何门店");
				}
				Long storeId=stores.get(0).getId();//根据登录账号找到门店id
				filteres.clear();
				for(Task task:tasks) {
					String processDefinitionId=task.getProcessDefinitionId();
					String[] strs=processDefinitionId.split(":");
					//找出流程id
					String processId=strs[0];	
					//消团审核
					if(processId.equals("xiaotuanProcess")) {
						String processInstanceId=task.getProcessInstanceId(); //流程实例id
						List<HyGroupCancelAudit> hyGroupCancelAudits=hyGroupCancelAuditService.findAll();
						for (HyGroupCancelAudit tmp : hyGroupCancelAudits) {
							if (processInstanceId.equals(tmp.getProcessInstanceId())) {
								if(task.getTaskDefinitionKey().equals("usertask2")) {
									//修改逻辑 从groupStoreCancel中找寻记录，若有就说明已经审核，不返回给前端了
									Long groupId = tmp.getHyGroup().getId();
									List<Filter> filters1 = new ArrayList<>();
									filters1.add(Filter.eq("groupId", groupId));
									filters1.add(Filter.eq("storeId", storeId));
									List<GroupStoreCancel> gscs = groupStoreCancelService.findList(null, filters1, null);
									//如果是空的,就没有审核过
									if(gscs.isEmpty()) {
										if(processList.contains("xiaotuanProcess")) {
											int num=processList.indexOf("xiaotuanProcess");
											int preCount=wrapProcessList.get(num).getCount();
											wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
										}
										else {
											processList.add("xiaotuanProcess");
											WrapProcess process=new WrapProcess();
											process.setCount(1); //将数量设置成1
											process.setProcessId("xiaotuanProcess");
											process.setProcessName("供应商消团");
											wrapProcessList.add(process);
										}
									}
								} 																					
							}
					    }					
					}				
				}
				
				if(!processList.contains("xiaotuanProcess")) {
					WrapProcess process=new WrapProcess();
					process.setCount(0); //将数量设置成0
					process.setProcessId("xiaotuanProcess");
					process.setProcessName("供应商消团");
					wrapProcessList.add(process);
				}
				
//			    /**
//				 * 供应商消团待审核,虽然走了工作流,但是这个审核需要所有门店都通过,所以这个不查流程,直接查数据库
//				 */
//				int xiaotuanNum=0;
//				filteres.add(Filter.eq("status",0));  //待门店审核
//				filteres.add(Filter.eq("type", HyOrderApplication.PROVIDER_CANCEL_GROUP)); //2-供应商消团
//				List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filteres,null);
//				for(HyOrderApplication application:orderApplications) {
//					Long orderId=application.getOrderId();
//					HyOrder hyOrder=hyOrderService.find(orderId);
//					//只查找该门店的,且是线路订单,未取消订单
//					if(storeId.equals(hyOrder.getStoreId())&&hyOrder.getType()==1&&hyOrder.getStatus()!=6) {
//						//如果是门店经理
//						if(hyRole.getName().contains("经理")) {
//							Long groupId=application.getCancleGroupId();
//							HyGroup hyGroup=hyGroupService.find(groupId);
//							filteres.clear();
//							filteres.add(Filter.eq("hyGroup", hyGroup));
//							filteres.add(Filter.eq("auditStatus", AuditStatus.auditing)); //审核中
//							List<HyGroupCancelAudit> cancelGroupAudits=hyGroupCancelAuditService.findList(null,filteres,null);
//							if(!cancelGroupAudits.isEmpty()) {
//
//								xiaotuanNum ++;
//							}	
//						}
//						
//						//如果登录账号的角色是门店员工
//						else {
//							//员工只能看到自己创建的数据
//							if(username.equals(hyOrder.getCreatorId())) {
//								Long groupId=application.getCancleGroupId();
//								HyGroup hyGroup=hyGroupService.find(groupId);
//								filteres.clear();
//								filteres.add(Filter.eq("hyGroup", hyGroup));
//								filteres.add(Filter.eq("auditStatus", AuditStatus.auditing)); //审核中
//								List<HyGroupCancelAudit> cancelGroupAudits=hyGroupCancelAuditService.findList(null,filteres,null);
//								if(!cancelGroupAudits.isEmpty()) {
//									//如果已经有一个改流程的代办事项,将数量加1
//									xiaotuanNum ++;
//								}	
//							}		
//						}
//					}			
//				}
//				WrapProcess process2=new WrapProcess();
//				process2.setCount(xiaotuanNum);
//				process2.setProcessId("cancelGroupProcess");
//				process2.setProcessName("供应商消团");
//				wrapProcessList.add(process2);
					
						    
//			    /**
//				 * 待供应商确认订单,没有走工作流,直接查数据库
//				 */
//				filteres.clear();
//				filteres.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)); //2-待供应商确认
//				filteres.add(Filter.eq("type", 1)); //先只管线路订单
//				filteres.add(Filter.eq("storeId", storeId)); //查找本门店的
//				
//				//如果不是门店经理,只能看自己创建的
//				if(!hyRole.getName().contains("经理")) {
//					filteres.add(Filter.eq("creatorId", username));
//				}
//				
//				List<HyOrder> hyOrders=hyOrderService.findList(null,filteres,null);
//				if(hyOrders.size()>0) {
//					WrapProcess process=new WrapProcess();
//					process.setCount(hyOrders.size()); //待确定订单数量
//					process.setProcessId("orderConfirm");
//					process.setProcessName("订单确认");
//					wrapProcessList.add(process);
//				}  
//				else {
//					WrapProcess process=new WrapProcess();
//					process.setCount(0); //待确定订单数量
//					process.setProcessId("orderConfirm");
//					process.setProcessName("订单待供应商确认");
//					wrapProcessList.add(process);
//				}
//			    
//			    /**
//				 * 门店未支付订单,没有走工作流,直接查数据库
//				 */
//				filteres.clear();
//				filteres.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_STORE_PAY)); //0-待门店支付
//				filteres.add(Filter.eq("type", 1)); //先只管线路订单
////				filteres.add(Filter.eq("paystatus", 0)); //门店待支付
//				filteres.add(Filter.eq("storeId", storeId)); //只筛选本门店的
//				
//				//如果不是经理,只能看到自己创建的数据;如果是经理,能看到整个门店的
//				if(!hyRole.getName().contains("经理")) {
//					filteres.add(Filter.eq("creatorId", username));
//				}
//				List<HyOrder> store_hyOrders=hyOrderService.findList(null,filteres,null);
//				if(store_hyOrders.size()>0) {
//					WrapProcess process=new WrapProcess();
//					process.setCount(store_hyOrders.size()); //待支付订单数量
//					process.setProcessId("orderPay");
//					process.setProcessName("订单支付");
//					wrapProcessList.add(process);
//				}
//				else {
//					WrapProcess process=new WrapProcess();
//					process.setCount(0); //待支付订单数量
//					process.setProcessId("orderPay");
//					process.setProcessName("订单支付");
//					wrapProcessList.add(process);
//				}	
				mendian(wrapProcessList,tasks,hyAdmin,processList,username,storeId);
			}
			
			/**
			 * 直营门店（经理）
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
				zhiyingmendianjingli(wrapProcessList, tasks, hyAdmin, processList, username, storeId);
			}
			/**
			 * 直营门店（员工）
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
				zhiyingmendianyuangong(wrapProcessList, tasks, hyAdmin, processList, username, storeId);
			}
			/**
			 * 连锁发展（经理）
			 */
			else if(departmentModelName.contains("连锁发展") && hyRole.getName().contains("经理")){
				obj.put("accountRole", "liansuofazhanjingli");
				liansuofazhanjingli(wrapProcessList, tasks, hyAdmin, processList, username);
				
			}
			/**
			 * 连锁发展（员工）
			 */
			else if(departmentModelName.contains("连锁发展") && !hyRole.getName().contains("经理")){
				obj.put("accountRole", "liansuofazhanyuangong");
				liansuofazhanyuangong(wrapProcessList, tasks, hyAdmin, processList, username);
			}
			/**
			 * 总公司产品经理
			 */
			else if(departmentModelName.contains("总公司产品研发中心") && hyRole.getName().contains("经理")){
				obj.put("accountRole", "zonggongsichanpinjingli");
				zonggongsichanpinjingli(wrapProcessList, tasks, hyAdmin, processList, username);
			}
			/**
			 * 分公司产品中心经理
			 */
			else if(departmentModelName.contains("分公司产品中心") && hyRole.getName().contains("经理")){
				obj.put("accountRole", "fengongsichanpinzhongxinjingli");
				fengongsichanpinzhongxinjingli(wrapProcessList, tasks, hyAdmin, processList, username);
			}
			/**
			 * 市场部副总
			 */
			else if(departmentModelName.contains("市场部")){
				obj.put("accountRole", "shichangbufuzong");
				shichangbufuzong(wrapProcessList, tasks, hyAdmin, processList, username);
			}
			/**
			 * 分公司副总
			 */
			else if(departmentModelName.equals("分公司") && hyRole.getName().contains("副总")){
				obj.put("accountRole", "fengongsifuzong");
				fengongsifuzong(wrapProcessList, tasks, hyAdmin, processList, username);
			}
			/**
			 * 总公司副总
			 */
			else if(departmentModelName.equals("总公司") && hyRole.getName().contains("副总") ){
				obj.put("accountRole", "zonggongsifuzong");
				zonggongsifuzong(wrapProcessList, tasks, hyAdmin, processList, username);
			}
			/**
			 * 分公司财务
			 */
			else if(departmentModelName.equals("分公司财务部")){
				obj.put("accountRole", "fengongsicaiwu");
				fengongsicaiwu(wrapProcessList, tasks, hyAdmin, processList, username);
			}
			/**
			 * 总公司财务
			 */
			else if(departmentModelName.equals("总公司财务部")){
				obj.put("accountRole", "zongongsicaiwu");
//				zonggongsicaiwu(wrapProcessList, tasks, hyAdmin, processList, username);
				zonggongsicaiwu(wrapProcessList, processList, username, hyAdmin);
			}
			/**
			 * 品控员工（查票务上产品的无重审核）
			 */
			else if(departmentModelName.contains("品控")){
				obj.put("accountRole", "pinkongyuangong");
				pinkongyuangong(wrapProcessList, processList, username, hyAdmin);
//				pinkongyuangong(wrapProcessList, tasks, hyAdmin, processList, username);
			}
			/**
			 * 采购部经理（采购部）（判断角色）
			 */
			else if(departmentModelName.contains("采购部") && hyRole.getName().contains("经理")){
				obj.put("accountRole", "caigoubujingli");
				caigoubujingli(wrapProcessList, tasks, hyAdmin, processList, username);
			}
			/**
			 * 采购部员工
			 */
			else if(departmentModelName.contains("采购部") && !hyRole.getName().contains("经理")){
				obj.put("accountRole", "caigoubuyuangong");
				caigoubuyuangong(wrapProcessList, tasks, hyAdmin, processList, username);
			}
			/**
			 * 行政服务中心
			 */
			else if(departmentModelName.contains("行政服务")){
				obj.put("accountRole", "zonggongsixingzhengfuwuzhongxin");
				xingzhengfuwuzhongxin(wrapProcessList, tasks, hyAdmin, processList, username);
				
			}
			/**
			 * 线路内部供应商（总公司出境部国内部）
			 */
			else if(departmentModelName.contains("出境部")||departmentModelName.contains("国内部")
					||departmentModelName.contains("总公司汽车部") ||departmentModelName.contains("活动部") ){
				obj.put("accountRole", "xianluneibugongyingshang");
				xianluneibugongyingshang(wrapProcessList, tasks, hyAdmin, processList, username);
				
			}
//			/**
//			 * 线路内部供应商（分公司出境部国内部）
//			 */
//			else if(departmentModelName.contains("总公司出境部")){
//				obj.put("accountRole", "fengongsichujingbuguoneibu");
//				xianluneibugongyingshang(wrapProcessList, tasks, hyAdmin, processList, username);
//				
//			}
			/**
			 * 票务内部供应商（总公司票务部）
			 */
			else if(departmentModelName.contains("票务部")){
				obj.put("accountRole", "piaowuneibugongyingshang");
				piaowuneibugongyingshang(wrapProcessList, tasks, hyAdmin, processList, username);
				
			}
			
			/**
			 * 分公司汽车部（经理）
			 */
			else if(departmentModelName.contains("分公司汽车部")&& hyRole.getName().contains("经理")){
				obj.put("accountRole","fengongsiqichebujingli");
				fengongsiqichebujingli(wrapProcessList, tasks, hyAdmin, processList, username);
			}
			
			/**
			 * 分公司汽车部（员工）
			 */
			else if(departmentModelName.contains("分公司汽车部")&& !hyRole.getName().contains("经理")){
				obj.put("accountRole","fengongsiqichebuyuangong");
				fengongsiqichebuyuangong(wrapProcessList, tasks, hyAdmin, processList, username);
			}
			/**
			 * 导游服务中心
			 */
			else if(departmentModelName.contains("导游")){
				obj.put("accountRole", "daoyoufuwuzhongxin");
				daoyoufuwuzhongxin(wrapProcessList, tasks, hyAdmin, processList, username);
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
	 * 导游服务中心
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void daoyoufuwuzhongxin(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		/**
		 * 导游审核
		 */
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.ne("status", 1));
		filters.add(Filter.ne("status", 2));
		filters.add(Filter.ne("status", 3));
		filters.add(Filter.ne("status", 5));
		List<Guide> guides = guideService.findList(null,filters,null);
		
		if(guides.size()>0){
			WrapProcess process = new WrapProcess();
			process.setCount(guides.size()); //待确定订单数量
			process.setProcessId("daoyoushenhe");
			process.setProcessName("导游审核");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("daoyoushenhe");
			process.setProcessName("导游审核");
			wrapProcessList.add(process);
		}
	}
	
	
	/**
	 * 分公司汽车部员工
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void fengongsiqichebuyuangong(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
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
									process.setProcessName(processName);
									wrapProcessList.add(process);
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
										process.setProcessName(processName);
										wrapProcessList.add(process);
									}
								}
							}
						}	
					
				}
			}
			/**
			 * 采购部提前打款
			 */
			else if(processId.equals("payServicePre")){
				List<Filter> filters=new ArrayList<>();
				filters.add(Filter.eq("status", 1));
				filters.add(Filter.eq("step", 1));
				List<PaymentSupplier> paymentSuppliers=paymentSupplierService.findList(null,filters,null);
				for(PaymentSupplier payment:paymentSuppliers) {
					if(processInstanceId.equals(payment.getProcessInstanceId())) {
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
							process.setProcessName("提前打款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			
			/**
			 * 供应商退出 
			 */
			else if(processId.equals("cgbgystuiyajinprocess")){
				List<Filter> filters=new ArrayList<>();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<Gysfzrtuichu> gysfzrtuichus = gystuiyajinService.findList(null,filters,null);
				for(Gysfzrtuichu gysfzrtuichu: gysfzrtuichus){
					if(gysfzrtuichu.getProcessInstanceId().equals(processInstanceId)){
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
							process.setProcessName("供应商退出");
							wrapProcessList.add(process);
						}
					}
				}
			}	
			/**
			 * 供应商新建团期
			 */
			else if(processId.equals("xianlushenheprocess")){
				List<Filter> filters=new ArrayList<>();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<HyGroup> groups = hyGroupService.findList(null,filters,null);
				for(HyGroup group:groups){
					if(processInstanceId.equals(group.getProcessInstanceId())){
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
							process.setProcessName("供应商新建团期");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商申请特殊费率
			 */
			else if(processId.equals("groupbiankoudianprocess")){
				List<Filter> filters=new ArrayList<>();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<GroupBiankoudian> groupBiankoudians = groupBiankoudianService.findList(null,filters,null);
				for(GroupBiankoudian biankoudian:groupBiankoudians){
					if(processInstanceId.equals(biankoudian.getProcessInstanceId())){
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
							process.setProcessName("供应商申请特殊费率");
							wrapProcessList.add(process);
						}
					}
				}
			}
			
    	}
    	
		if(!processList.contains("payServicePre")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("payServicePre");
			process.setProcessName("提前打款");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("cgbgystuiyajinprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("cgbgystuiyajinprocess");
			process.setProcessName("供应商退出");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("xianlushenheprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("xianlushenheprocess");
			process.setProcessName("供应商新建团期");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("groupbiankoudianprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("groupbiankoudianprocess");
			process.setProcessName("供应商申请特殊费率");
			wrapProcessList.add(process);
		}	
		
		if(!processList.contains("storeTuiTuan")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeTuiTuan");
			process.setProcessName("门店退团申请");
			wrapProcessList.add(process);
		}
		if(!processList.contains("storeShouHou")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeShouHou");
			process.setProcessName("门店退团申请");
			wrapProcessList.add(process);
		}
		if(!processList.contains("rengoumenpiaoTuiDing")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("rengoumenpiaoTuiDing");
			process.setProcessName("认购门票退订申请");
			wrapProcessList.add(process);
		}
		if(!processList.contains("rengoumenpiaoShouHou")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("rengoumenpiaoShouHou");
			process.setProcessName("认购门票售后申请");
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
		process_promotion.setProcessName("线路促销审核");
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
		ticket_promotion.setProcessName(name);
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
			process.setProcessName("订单确认");
			wrapProcessList.add(process);
		}  
		else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("orderConfirm");
			process.setProcessName("订单待供应商确认");
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
			process.setCount(visaOrders.size()); //待确定订单数量
			process.setProcessId("rengoumenpiaoorderConfirm");
			process.setProcessName("认购门票订单确认");
			wrapProcessList.add(process);
		}  
		else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("rengoumenpiaoorderConfirm");
			process.setProcessName("认购门票订单确认");
			wrapProcessList.add(process);
		}
	}
	
	
	/**
	 * 分公司汽车部经理
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 * @param supplierContract
	 */
	public void fengongsiqichebujingli(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
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
									process.setProcessName(processName);
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
										process.setProcessName(processName);
										wrapProcessList.add(process);
									}
								}
							}
						}
					}
				}
			}
			//供应商付尾款
			else if(processId.equals("balanceDueBranch")){
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
							process.setProcessName("旅游元素付尾款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 采购部提前打款
			 */
			else if(processId.equals("payServicePre")){
				List<Filter> filters=new ArrayList<>();
				filters.add(Filter.eq("status", 1));
				filters.add(Filter.eq("step", 1));
				List<PaymentSupplier> paymentSuppliers=paymentSupplierService.findList(null,filters,null);
				for(PaymentSupplier payment:paymentSuppliers) {
					if(processInstanceId.equals(payment.getProcessInstanceId())) {
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
							process.setProcessName("提前打款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			
			/**
			 * 供应商退出 
			 */
			else if(processId.equals("cgbgystuiyajinprocess")){
				List<Filter> filters=new ArrayList<>();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<Gysfzrtuichu> gysfzrtuichus = gystuiyajinService.findList(null,filters,null);
				for(Gysfzrtuichu gysfzrtuichu: gysfzrtuichus){
					if(gysfzrtuichu.getProcessInstanceId().equals(processInstanceId)){
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
							process.setProcessName("供应商退出");
							wrapProcessList.add(process);
						}
					}
				}
			}	
			/**
			 * 供应商新建团期
			 */
			else if(processId.equals("xianlushenheprocess")){
				List<Filter> filters=new ArrayList<>();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<HyGroup> groups = hyGroupService.findList(null,filters,null);
				for(HyGroup group:groups){
					if(processInstanceId.equals(group.getProcessInstanceId())){
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
							process.setProcessName("供应商新建团期");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商申请特殊费率
			 */
			else if(processId.equals("groupbiankoudianprocess")){
				List<Filter> filters=new ArrayList<>();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<GroupBiankoudian> groupBiankoudians = groupBiankoudianService.findList(null,filters,null);
				for(GroupBiankoudian biankoudian:groupBiankoudians){
					if(processInstanceId.equals(biankoudian.getProcessInstanceId())){
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
							process.setProcessName("供应商申请特殊费率");
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
		
		if(!processList.contains("payServicePre")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("payServicePre");
			process.setProcessName("提前打款");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("cgbgystuiyajinprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("cgbgystuiyajinprocess");
			process.setProcessName("供应商退出");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("xianlushenheprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("xianlushenheprocess");
			process.setProcessName("供应商新建团期");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("groupbiankoudianprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("groupbiankoudianprocess");
			process.setProcessName("供应商申请特殊费率");
			wrapProcessList.add(process);
		}	
		
    	
		if (!processList.contains("balanceDue")) {
			WrapProcess process = new WrapProcess();
			process.setCount(0); // 将数量设置成0
			process.setProcessId("balanceDue");
			process.setProcessName("旅游元素付尾款");
			wrapProcessList.add(process);
		}
		
		
		if(!processList.contains("storeTuiTuan")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeTuiTuan");
			process.setProcessName("门店退团申请");
			wrapProcessList.add(process);
		}
		if(!processList.contains("storeShouHou")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeShouHou");
			process.setProcessName("门店售后申请");
			wrapProcessList.add(process);
		}
		if(!processList.contains("rengoumenpiaoTuiDing")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("rengoumenpiaoTuiDing");
			process.setProcessName("认购门票退订申请");
			wrapProcessList.add(process);
		}
		if(!processList.contains("rengoumenpiaoShouHou")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("rengoumenpiaoShouHou");
			process.setProcessName("认购门票售后申请");
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
		process_promotion.setProcessName("线路促销审核");
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
		ticket_promotion.setProcessName(name);
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
			process.setProcessName("订单确认");
			wrapProcessList.add(process);
		}  
		else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("orderConfirm");
			process.setProcessName("订单待供应商确认");
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
			process.setCount(visaOrders.size()); //待确定订单数量
			process.setProcessId("rengoumenpiaoorderConfirm");
			process.setProcessName("认购门票订单确认");
			wrapProcessList.add(process);
		}  
		else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("rengoumenpiaoorderConfirm");
			process.setProcessName("认购门票订单确认");
			wrapProcessList.add(process);
		}
	}
	
	
	
	/**
	 * 票务外部供应商
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 * @param supplierContract
	 */
	public void piaowuwaibugongyingshang(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username,HySupplierContract supplierContract,HttpSession session,HttpServletRequest request){
		for(Task task : tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			
			if(processId.equals("storeTuiTuan")) {
				filters.add(Filter.eq("status",0));  //0:待供应商确认 1:待品控员工审核 2：待财务审核
				filters.add(Filter.eq("type", HyOrderApplication.STORE_CANCEL_GROUP)); //0-门店退团			
				List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filters,null);
				String processName = "";
				for(HyOrderApplication application:orderApplications) {
					HyOrder order = hyOrderService.find(application.getOrderId());
					if(processInstanceId.equals(application.getProcessInstanceId())) {
						switch(order.getType()){
						case 3:{
							processId = "hotelTuiDing";
							processName = "酒店订单退订";
							break;
						}
						case 4:{
							processId = "ticketTuiDing";
							processName = "门票订单退订";
							break;
						}
						case 5:{
							processId = "ticketandhotelTuiDing";
							processName = "酒加景订单退订";
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
							process.setProcessName(processName);
							wrapProcessList.add(process);
						}
					}
				}
			}	
			/**
			 * 门店售后
			 */
			else if(processId.equals("storeShouHou")) {
				filters.clear();
				filters.add(Filter.eq("status",0));  //0:待供应商确认 1:待品控员工审核 2：待财务审核
				filters.add(Filter.eq("type", HyOrderApplication.STORE_CUSTOMER_SERVICE)); //0-门店售后			
				List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filters,null);
				String processName = "";
				for(HyOrderApplication application:orderApplications) {
					HyOrder order = hyOrderService.find(application.getOrderId());
					if(processInstanceId.equals(application.getProcessInstanceId())) {
						switch(order.getType()){
						case 3:{
							processId = "hotelShouHou";
							processName = "酒店订单售后";
							break;
						}
						case 4:{
							processId = "ticketShouHou";
							processName = "门票订单售后";
							break;
						}
						case 5:{
							processId = "ticketandhotelShouHou";
							processName = "酒加景订单售后";
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
							process.setProcessName(processName);
							wrapProcessList.add(process);
						}
					}
				}
			}	
			/**
			 * T+N自动打款打款单审核
			 */
			else if(processId.equals("payServicePreTN")) {
				filters.clear();
				filters.add(Filter.eq("status",1));  //筛选状态为审核中
				filters.add(Filter.eq("supplierContract", supplierContract)); //如果是合同负责人账号,按合同筛选
				//如果不是合同负责人,只查找自己负责的
				if(hyAdmin.getHyAdmin()!=null) {
					filters.add(Filter.eq("operator", hyAdmin));
				}
				List<PaymentSupplier> paymentSuppliers=paymentSupplierService.findList(null,filters,null);
				filters.clear();
				for(PaymentSupplier payment:paymentSuppliers) {
					if(processInstanceId.equals(payment.getProcessInstanceId())) {
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
							process.setProcessName("T+N自动打款打款单审核");
							wrapProcessList.add(process);
						}
					}
				}
			}
			
			
			
		}
		
		/**
		 * 判空
		 */
		
		if(!processList.contains("payServicePreTN")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("payServicePreTN");
			process.setProcessName("T+N自动打款打款单审核");
			wrapProcessList.add(process);
		}
		
		
		String processId = "";
		String processName = "";
		for(int i=3;i<=5;i++){
			switch(i){
			case 3:{
				processId = "hotelShouHou";
				processName = "酒店订单售后";
				break;
			}
			case 4:{
				processId = "ticketShouHou";
				processName = "门票订单售后";
				break;
			}
			case 5:{
				processId = "ticketandhotelShouHou";
				processName = "酒加景订单售后";
				break;
			}
			}
			
			if(!processList.contains(processId)) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId(processId);
				process.setProcessName(processName);
				wrapProcessList.add(process);
			}
		}
		
		for(int i=3;i<=5;i++){
			switch(i){
			case 3:{
				processId = "hotelTuiDing";
				processName = "酒店订单退订";
				break;
			}
			case 4:{
				processId = "ticketTuiDing";
				processName = "门票订单退订";
				break;
			}
			case 5:{
				processId = "ticketandhotelTuiDing";
				processName = "酒加景订单退订";
				break;
			}
			default:
				break;
			}
			if(!processList.contains(processId)) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId(processId);
				process.setProcessName(processName);
				wrapProcessList.add(process);
			}
		}
		
		
		
		
		
		/**
		 * 供应商待确认订单（暂时3种，不确认有没有保险）
		 */
//		// 酒店
//		Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
//		StringBuilder sb = new StringBuilder("select count(*) from hy_order o1,hy_order_item i1,hy_ticket_hotel h1"
//				+ " where o1.type=3 and o1.id=i1.order_id and i1.product_id=h1.id");
//		if(hyAdmins!=null && !hyAdmins.isEmpty()){
//			List<String> adminStrArr = new ArrayList<>();
//			for(HyAdmin admin:hyAdmins){
//				adminStrArr.add("'"+admin.getUsername()+"'");
//				
//			}
//			String adminStr = String.join(",",adminStrArr);
//			sb.append(" and h1.creator in ("+adminStr+")");
//		}
//		List<Object[]> objects =  hyOrderService.statis(sb.toString());
//		Object object =  objects.get(0);
//		
//		processList.add("hotelOrderConfirm");
//		WrapProcess process1 = new WrapProcess();
//		process1.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
//		process1.setProcessId("hotelOrderConfirm");
//		wrapProcessList.add(process1);
		
		
		HyRole hyRole=hyAdmin.getRole();
		int[] orderConfirmList = {3,4,5};//订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
		Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
		List<Filter> filters = new ArrayList<>();
		for(int i : orderConfirmList){					
			filters.clear();
			filters.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)); //2-待供应商确认
			filters.add(Filter.eq("type", i)); 
			filters.add(Filter.in("supplier", hyAdmins));
			
//			//如果不是门店经理,只能看自己创建的
//			if(!hyRole.getName().contains("经理")) {
//				filters.add(Filter.eq("creatorId", username));
//			}
			
			List<HyOrder> hyOrders=hyOrderService.findList(null,filters,null);
			
			switch (i) {
			case 3:{
				processId = "hotelOrderConfirm";
				processName = "酒店订单待供应商确认";
				break;
			}
			case 4:{
				processId = "menpiaoOrderConfirm";
				processName = "门票订单待供应商确认";
				break;
			}
			case 5:{
				processId = "ticketandhotelOrderConfirm";
				processName = "酒加景订单待供应商确认";
				break;
			}
			default:
				continue;
			}
			
			
			if(hyOrders.size()>0) {
				WrapProcess process=new WrapProcess();
				process.setCount(hyOrders.size()); //待确定订单数量
				process.setProcessId(processId);
				process.setProcessName(processName);
				wrapProcessList.add(process);
			}  
			else {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //待确定订单数量
				process.setProcessId(processId);
				process.setProcessName(processName);
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
				name = "门票促销审核";
				id = "ticketPromotion";
				break;
			}
			case 1:{
				name = "酒店促销审核";
				id = "hotelPromotion";
				break;
			}
			case 2:{
				name = "酒加景促销审核";
				id = "ticketandhotelPromotion";
				break;
				}
			
			default:
				continue;
			
		}
		
			WrapProcess ticket_promotion=new WrapProcess();
			ticket_promotion.setCount(activities.size());
			ticket_promotion.setProcessId(id);
			ticket_promotion.setProcessName(name);
			wrapProcessList.add(ticket_promotion);
		}
		
		
		
		
	}
	
	/**
	 * 行政服务中心
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void xingzhengfuwuzhongxin(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task:tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			
			/**
			 * 江泰预充值  
			 */
			if(processId.equals("PaymentJiangtai")){
				filters.clear();
				filters.add(Filter.eq("applicationStatus", HyPaymentpreJiangtai.daiqueren));
				List<HyPaymentpreJiangtai> jiangtais = hyPaymentpreJiangtaiService.findList(null,filters,null);
				for(HyPaymentpreJiangtai jiangtai : jiangtais){
					if(jiangtai.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("江泰预充值");
							wrapProcessList.add(process);
						}
					}
				}
			}
		}
		
		if(!processList.contains("PaymentJiangtai")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("PaymentJiangtai");
			process.setProcessName("江泰预充值");
			wrapProcessList.add(process);
		}	
//		/**
//		 * 导游审核
//		 */
//		List<Filter> filters = new ArrayList<>();
//		filters.add(Filter.ne("status", 1));
//		filters.add(Filter.ne("status", 2));
//		filters.add(Filter.ne("status", 3));
//		filters.add(Filter.ne("status", 5));
//		List<Guide> guides = guideService.findList(null,filters,null);
//		
//		if(guides.size()>0){
//			WrapProcess process = new WrapProcess();
//			process.setCount(guides.size()); //待确定订单数量
//			process.setProcessId("daoyoushenhe");
//			process.setProcessName("导游审核");
//			wrapProcessList.add(process);
//		}else {
//			WrapProcess process=new WrapProcess();
//			process.setCount(0); //待确定订单数量
//			process.setProcessId("daoyoushenhe");
//			process.setProcessName("导游审核");
//			wrapProcessList.add(process);
//		}
	}
	
	/**
	 * 采购部员工 
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void caigoubuyuangong(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task:tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			
			/**
			 * 供应商退出 
			 */
			if(processId.equals("cgbgystuiyajinprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<Gysfzrtuichu> gysfzrtuichus = gystuiyajinService.findList(null,filters,null);
				for(Gysfzrtuichu gysfzrtuichu: gysfzrtuichus){
					if(gysfzrtuichu.getProcessInstanceId().equals(processInstanceId)){
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
							process.setProcessName("供应商退出");
							wrapProcessList.add(process);
						}
					}
				}
			}	
			
			/**
			 * 供应商新建团期
			 */
			else if(processId.equals("xianlushenheprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<HyGroup> groups = hyGroupService.findList(null,filters,null);
				for(HyGroup group:groups){
					if(processInstanceId.equals(group.getProcessInstanceId())){
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
							process.setProcessName("供应商新建团期");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商申请特殊费率
			 */
			else if(processId.equals("groupbiankoudianprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<GroupBiankoudian> groupBiankoudians = groupBiankoudianService.findList(null,filters,null);
				for(GroupBiankoudian biankoudian:groupBiankoudians){
					if(processInstanceId.equals(biankoudian.getProcessInstanceId())){
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
							process.setProcessName("供应商申请特殊费率");
							wrapProcessList.add(process);
						}
					}
				}
			}
		}

		if(!processList.contains("cgbgystuiyajinprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("cgbgystuiyajinprocess");
			process.setProcessName("供应商退出");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("xianlushenheprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("xianlushenheprocess");
			process.setProcessName("供应商新建团期");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("groupbiankoudianprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("groupbiankoudianprocess");
			process.setProcessName("供应商申请特殊费率");
			wrapProcessList.add(process);
		}	
	}
	
	
	/**
	 * 采购部经理 
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void caigoubujingli(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task:tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();	
			
			/**
			 * 采购部提前打款
			 */
			if(processId.equals("payServicePre")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				filters.add(Filter.eq("step", 1));
				List<PaymentSupplier> paymentSuppliers=paymentSupplierService.findList(null,filters,null);
				for(PaymentSupplier payment:paymentSuppliers) {
					if(processInstanceId.equals(payment.getProcessInstanceId())) {
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
							process.setProcessName("提前打款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			
			/**
			 * 供应商退出 
			 */
			else if(processId.equals("cgbgystuiyajinprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<Gysfzrtuichu> gysfzrtuichus = gystuiyajinService.findList(null,filters,null);
				for(Gysfzrtuichu gysfzrtuichu: gysfzrtuichus){
					if(gysfzrtuichu.getProcessInstanceId().equals(processInstanceId)){
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
							process.setProcessName("供应商退出");
							wrapProcessList.add(process);
						}
					}
				}
			}	
			/**
			 * 供应商新建团期
			 */
			else if(processId.equals("xianlushenheprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<HyGroup> groups = hyGroupService.findList(null,filters,null);
				for(HyGroup group:groups){
					if(processInstanceId.equals(group.getProcessInstanceId())){
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
							process.setProcessName("供应商新建团期");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商申请特殊费率
			 */
			else if(processId.equals("groupbiankoudianprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<GroupBiankoudian> groupBiankoudians = groupBiankoudianService.findList(null,filters,null);
				for(GroupBiankoudian biankoudian:groupBiankoudians){
					if(processInstanceId.equals(biankoudian.getProcessInstanceId())){
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
							process.setProcessName("供应商申请特殊费率");
							wrapProcessList.add(process);
						}
					}
				}
			}
		}
		if(!processList.contains("payServicePre")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("payServicePre");
			process.setProcessName("提前打款");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("cgbgystuiyajinprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("cgbgystuiyajinprocess");
			process.setProcessName("供应商退出");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("xianlushenheprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("xianlushenheprocess");
			process.setProcessName("供应商新建团期");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("groupbiankoudianprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("groupbiankoudianprocess");
			process.setProcessName("供应商申请特殊费率");
			wrapProcessList.add(process);
		}	
	}
	
	/**
	 * 品控员工新
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
		
		/**
		 * 门店退团和售后
		 */
		mendiantuituan(wrapProcessList, processList, 1, username);
		mendiantuituanpankong(wrapProcessList, processList);
		
		/**
		 * 线路促销走流程好像有点问题,直接查数据库
		 */
		List<Filter> filterss=new ArrayList<>();
		filterss.add(Filter.eq("state",0));  //筛选状态为审核中
		filterss.add(Filter.eq("isCaigouti", false)); //供应商提交
		List<LinePromotion> linepromotions=linePromotionService.findList(null,filterss,null);
		WrapProcess process_promotion=new WrapProcess();
		process_promotion.setCount(linepromotions.size());
		process_promotion.setProcessId("LinePromotion");
		process_promotion.setProcessName("线路促销审核");
		wrapProcessList.add(process_promotion);
		
		/**
		 * 供应商建促销（票务5种）
		 */
		for(int i=0;i<=4;i++){
			filterss.clear();
			filterss.add(Filter.eq("state", 0));
			filterss.add(Filter.eq("activityType", i));
			filterss.add(Filter.eq("isCaigouti", false));
			List<HyPromotionActivity> activities = hyPromotionActivityService.findList(null,filterss,null);
			String name = new String();
			String id = new String();
			switch (i) {
			case 0:{
				name = "门票促销审核";
				id = "ticketPromotion";
				break;
			}
			case 1:{
				name = "酒店促销审核";
				id = "hotelPromotion";
				break;
			}
			case 2:{
				name = "酒加景促销审核";
				id = "ticketandhotelPromotion";
				break;
			}
			case 3:{
				name = "认购门票促销审核";
				id = "rengoumenpiaoPromotion";
				break;
			}
			case 4:{
				name = "签证促销审核";
				id = "visaPromotion";
				break;
			}
			default:
				break;
			}

			WrapProcess ticket_promotion=new WrapProcess();
			ticket_promotion.setCount(activities.size());
			ticket_promotion.setProcessId(id);
			ticket_promotion.setProcessName(name);
			wrapProcessList.add(ticket_promotion);
		}
		
		
	}
	
	
	/**
	 * 品控员工 （还差票务5种上产品审核，已完成）
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void pinkongyuangong1(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		Integer gongyingshangtuiyajin = 0;
		for(Task task:tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();	
			
			/**
			 * 门店退团（6种） 门店售后（6种）
			 */
			if(processId.equals("storeTuiTuan") || processId.equals("storeShouHou")){
				mendiantuituan(wrapProcessList, task, processId, processInstanceId, processList, 1);
			}
			/**
			 * 供应商退出
			 */
			else if(processId.equals("cgbgystuiyajinprocess")){
				gongyingshangtuiyajin++;
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<Gysfzrtuichu> gysfzrtuichus = gystuiyajinService.findList(null,filters,null);
				for(Gysfzrtuichu gysfzrtuichu: gysfzrtuichus){
					if(gysfzrtuichu.getProcessInstanceId().equals(processInstanceId)){
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
							process.setProcessName("供应商退出");
							wrapProcessList.add(process);
						}
					}
				}
			}	
			/**
			 * 供应商新建团期（线路）
			 */
			else if(processId.equals("xianlushenheprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<HyGroup> groups = hyGroupService.findList(null,filters,null);
				for(HyGroup group:groups){
					if(processInstanceId.equals(group.getProcessInstanceId())){
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
							process.setProcessName("供应商新建团期");
							wrapProcessList.add(process);
						}
					}
				}
			}
			
			/**
			 * 供应商上产品（票务5种）
			 */
			/**
			 * 酒加景上产品
			 */
			else if(processId.equals("hotelandsceneRoomPriceProcess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", 2));
				List<HyTicketHotelandsceneRoom> hotelandsceneRooms = hyTicketHotelandsceneRoomService.findList(null,filters,null);
				for(HyTicketHotelandsceneRoom room : hotelandsceneRooms){
					if(processInstanceId.equals(room.getProcessInstanceId())){
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
							process.setProcessName("酒加景上产品");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 酒店房间上产品
			 */
			else if(processId.equals("hotelRoomPriceProcess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", 2));
				List<HyTicketHotelRoom> hotelRooms = hyTicketHotelRoomService.findList(null,filters,null);
				for(HyTicketHotelRoom room : hotelRooms){
					if(processInstanceId.equals(room.getProcessInstanceId())){
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
							process.setProcessName("酒店房间上产品");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 景区门票上产品
			 */
			else if(processId.equals("sceneticketPriceProcess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus",2));
				List<HyTicketSceneTicketManagement> managements = hyTicketSceneTicketManagementService.findList(null,filters,null);
				for(HyTicketSceneTicketManagement management:managements){
					if(processInstanceId.equals(management.getProcessInstanceId())){
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
							process.setProcessName("景区门票上产品");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 签证上产品
			 */
			else if(processId.equals("visaPriceProcess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus",2));
				List<HyVisa> visas = hyVisaService.findList(null,filters,null);
				for(HyVisa visa:visas){
					if(processInstanceId.equals(visa.getProcessInstanceId())){
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
							process.setProcessName("签证上产品");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 认购门票上产品
			 */
			else if(processId.equals("subscribeTicket")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", 2));
				List<HyTicketSubscribe> ticketSubscribes = hyTicketSubscribeService.findList(null,filters,null);
				for(HyTicketSubscribe ticketSubscribe:ticketSubscribes){
					if(processInstanceId.equals(ticketSubscribe.getProcessInstanceId())){
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
							process.setProcessName("认购门票上产品");
							wrapProcessList.add(process);
						}
					}
				}
			}
			
			
			/**
			 * 供应商驳回订单
			 */
			else if(processId.equals("suppilerDismissOrder")){
				filters.clear();
				filters.add(Filter.eq("status", 0));
				List<SupplierDismissOrderApply> applies = supplierDismissOrderApplyService.findList(null,filters,null);
				for(SupplierDismissOrderApply apply:applies){
					if(processInstanceId.equals(apply.getProcessInstanceId())){
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
							process.setProcessName("供应商驳回订单");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商甩尾单
			 */
			else if(processId.equals("swdprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<HyGroupShenheSwd> swds = hyGroupShenheSwdService.findList(null,filters,null);
				
				for(HyGroupShenheSwd swd : swds){
					if(processInstanceId.equals(swd.getProcessInstanceId())){
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
							process.setProcessName("供应商甩尾单");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商申请特殊费率
			 */
			else if(processId.equals("groupbiankoudianprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<GroupBiankoudian> groupBiankoudians = groupBiankoudianService.findList(null,filters,null);
				for(GroupBiankoudian biankoudian:groupBiankoudians){
					if(processInstanceId.equals(biankoudian.getProcessInstanceId())){
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
							process.setProcessName("供应商申请特殊费率");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商消团
			 */
			else if(processId.equals("xiaotuanProcess")) {
				List<HyGroupCancelAudit> hyGroupCancelAudits=hyGroupCancelAuditService.findAll();
				for (HyGroupCancelAudit tmp : hyGroupCancelAudits) {
					if (processInstanceId.equals(tmp.getProcessInstanceId())) {
						if(processList.contains("xiaotuanProcess")) {
							int num=processList.indexOf("xiaotuanProcess");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}
						else {
							processList.add("xiaotuanProcess");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("xiaotuanProcess");
							process.setProcessName("供应商消团");
							wrapProcessList.add(process);
						} 																				
					}
			    }					
			}			
			/**
			 * 采购部新建供应商
			 */
			else if(processId.equals("cgbgysprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<HySupplierContract> contracts = hySupplierContractService.findList(null,filters,null);
				for(HySupplierContract contract:contracts){
					if (processInstanceId.equals(contract.getProcessId())) {
						if(processList.contains("cgbgysprocess")) {
							int num=processList.indexOf("cgbgysprocess");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}
						else {
							processList.add("cgbgysprocess");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("cgbgysprocess");
							process.setProcessName("采购部新建供应商");
							wrapProcessList.add(process);
						} 																				
					}
			    }			
			}
			/**
			 * 采购部变更扣点
			 */
			else if(processId.equals("biangengkoudianprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<HySupplierContract> contracts = hySupplierContractService.findList(null,filters,null);
				for(HySupplierContract contract:contracts){
					if (processInstanceId.equals(contract.getProcessId())) {
						if(processList.contains("biangengkoudianprocess")) {
							int num=processList.indexOf("biangengkoudianprocess");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}
						else {
							processList.add("biangengkoudianprocess");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("biangengkoudianprocess");
							process.setProcessName("采购部变更扣点");
							wrapProcessList.add(process);
						} 																				
					}
			    }		
			}
			
			/**
			 * 采购部直接\变更续签
			 */
			else if(processId.equals("xuqianprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<XuqianEntity> xuqianEntities = xuqianService.findList(null,filters,null);
				for(XuqianEntity entity : xuqianEntities){
					if (processInstanceId.equals(entity.getProcessInstanceId())) {
						if(processList.contains("xuqianprocess")) {
							int num=processList.indexOf("xuqianprocess");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}
						else {
							processList.add("xuqianprocess");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("xuqianprocess");
							process.setProcessName("采购部直接或变更续签");
							wrapProcessList.add(process);
						} 																				
					}
				}
			}
			/**
			 * 采购部申请退还剩余押金
			 */
			else if(processId.equals("tuiyajinprocess")){
				filters.clear();
				filters.add(Filter.eq("applicationStatus", HyPaymentpreJiangtai.vicePresidentCheck));
				List<HyPaymentpreJiangtai> jiangtais = hyPaymentpreJiangtaiService.findList(null,filters,null);
				for(HyPaymentpreJiangtai jiangtai : jiangtais){
					if(jiangtai.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("采购部申请退还剩余押金");
							wrapProcessList.add(process);
						}
					}
				}
			}

		}
		/**
		 * 线路促销走流程好像有点问题,直接查数据库
		 */
		List<Filter> filterss=new ArrayList<>();
		filterss.add(Filter.eq("state",0));  //筛选状态为审核中
		filterss.add(Filter.eq("isCaigouti", false)); //供应商提交
		List<LinePromotion> linepromotions=linePromotionService.findList(null,filterss,null);
		WrapProcess process_promotion=new WrapProcess();
		process_promotion.setCount(linepromotions.size());
		process_promotion.setProcessId("LinePromotion");
		process_promotion.setProcessName("线路促销审核");
		wrapProcessList.add(process_promotion);
		
		/**
		 * 供应商建促销（票务5种）
		 */
		for(int i=0;i<=4;i++){
			filterss.clear();
			filterss.add(Filter.eq("state", 0));
			filterss.add(Filter.eq("activityType", i));
			filterss.add(Filter.eq("isCaigouti", false));
			List<HyPromotionActivity> activities = hyPromotionActivityService.findList(null,filterss,null);
			String name = new String();
			String id = new String();
			switch (i) {
			case 0:{
				name = "门票促销审核";
				id = "ticketPromotion";
				break;
			}
			case 1:{
				name = "酒店促销审核";
				id = "hotelPromotion";
				break;
			}
			case 2:{
				name = "酒加景促销审核";
				id = "ticketandhotelPromotion";
				break;
			}
			case 3:{
				name = "认购门票促销审核";
				id = "rengoumenpiaoPromotion";
				break;
			}
			case 4:{
				name = "签证促销审核";
				id = "visaPromotion";
				break;
			}
			default:
				break;
			}

			WrapProcess ticket_promotion=new WrapProcess();
			ticket_promotion.setCount(activities.size());
			ticket_promotion.setProcessId(id);
			ticket_promotion.setProcessName(name);
			wrapProcessList.add(ticket_promotion);
		}
		
		if(!processList.contains("subscribeTicket")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("subscribeTicket");
			process.setProcessName("认购门票上产品");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("hotelRoomPriceProcess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("hotelRoomPriceProcess");
			process.setProcessName("酒店房间上产品");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("sceneticketPriceProcess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("sceneticketPriceProcess");
			process.setProcessName("景区门票上产品");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("visaPriceProcess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("visaPriceProcess");
			process.setProcessName("签证上产品");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("hotelandsceneRoomPriceProcess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("hotelandsceneRoomPriceProcess");
			process.setProcessName("酒加景上产品");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("xuqianprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("xuqianprocess");
			process.setProcessName("采购部直接或变更续签");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("biangengkoudianprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("biangengkoudianprocess");
			process.setProcessName("采购部变更扣点");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("cgbgysprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("cgbgysprocess");
			process.setProcessName("采购部新建供应商");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("groupbiankoudianprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("groupbiankoudianprocess");
			process.setProcessName("供应商申请特殊费率");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("swdprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("swdprocess");
			process.setProcessName("供应商甩尾单");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("tuiyajinprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("tuiyajinprocess");
			process.setProcessName("采购部申请退还剩余押金");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("xiaotuanProcess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("xiaotuanProcess");
			process.setProcessName("供应商消团");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("suppilerDismissOrder")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("suppilerDismissOrder");
			process.setProcessName("供应商驳回订单");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("cgbgystuiyajinprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("cgbgystuiyajinprocess");
			process.setProcessName("供应商退出");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("xianlushenheprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("xianlushenheprocess");
			process.setProcessName("供应商新建团期");
			wrapProcessList.add(process);
		}	
		System.out.println(gongyingshangtuiyajin);
		
		mendiantuituanpankong(wrapProcessList, processList);
	}
	
	/**
	 * 总公司财务 新
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
			case "banlanceDue":buffer.append(" and dai.status = 1 and dai.step = 3 ");break;
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
			
			String s = strings[i];
			if(strings[i].equals("banlanceDue")){
				s="balanceDue";
			}
			
			processList.add(s);
			process1 = new WrapProcess();
			process1.setCount(Integer.valueOf(object.toString())); // 将数量设置成1
			process1.setProcessId(s);
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
	 * 总公司财务
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void zonggongsicaiwu(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task:tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			/**
			 * 门店缴纳押金
			 */
			if(processId.equals("jiaoyajin")){
				filters.clear();
				filters.add(Filter.eq("type", 1));
				filters.add(Filter.eq("applicationStatus", StoreApplication.init));
				List<StoreApplication> applications = storeApplicationService.findList(null,filters,null);
				for(StoreApplication application : applications){
					if(processInstanceId.equals(application.getProcessInstanceId())){
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
							process.setProcessName("门店缴纳押金");
							wrapProcessList.add(process);
						}	
					}
				}
			}
			/**
			 * 门店退出
			 */
			else if (processId.equals("storeLogout")) {
				filters.clear();
				filters.add(Filter.eq("type", 4));
				filters.add(Filter.eq("applicationStatus", StoreApplication.vicePresident));
				List<StoreApplication> storeApplications = storeApplicationService.findList(null, filters, null);
				for (StoreApplication storeApplication : storeApplications) {
					if (storeApplication.getProcessInstanceId().equals(processInstanceId)) {
						if (processList.contains(processId)) {
							int num = processList.indexOf(processId);
							int preCount = wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount + 1); // 将数量加1
						} else {
							processList.add(processId);
							WrapProcess process = new WrapProcess();
							process.setCount(1); // 将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("门店退出");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 门店充值
			 */
			else if(processId.equals("storeRecharge")){
				filters.clear();
				filters.add(Filter.eq("status", 0));
				List<StoreRecharge> storeRecharges = storeRechargeService.findList(null,filters,null);
				for(StoreRecharge storeRecharge:storeRecharges){
					if (storeRecharge.getProcessInstanceId().equals(processInstanceId)) {
						if (processList.contains(processId)) {
							int num = processList.indexOf(processId);
							int preCount = wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount + 1); // 将数量加1
						} else {
							processList.add(processId);
							WrapProcess process = new WrapProcess();
							process.setCount(1); // 将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("门店充值");
							wrapProcessList.add(process);
						}
					}
				}
			}	
			/**
			 * 门店退团和售后 分6种
			 * 待财务审核  status = 2
			 */
			else if(processId.equals("storeTuiTuan") || processId.equals("storeShouHou")){
				mendiantuituan(wrapProcessList, task, processId, processInstanceId, processList, 2);
			}
			
			/**
			 * 供应商缴纳押金
			 */
			else if(processId.equals("jiaoyajinprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<DepositServicer> depositServicers = depositServicerService.findList(null,filters,null);
				for(DepositServicer depositServicer:depositServicers){
					if(processInstanceId.equals(depositServicer.getProcessInstanceId())){
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
							process.setProcessName("供应商缴纳押金");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商退出
			 */
			else if(processId.equals("cgbgystuiyajinprocess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", AuditStatus.auditing));
				List<Gysfzrtuichu> gysfzrtuichus = gystuiyajinService.findList(null,filters,null);
				for(Gysfzrtuichu gysfzrtuichu: gysfzrtuichus){
					if(gysfzrtuichu.getProcessInstanceId().equals(processInstanceId)){
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
							process.setProcessName("供应商退出");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商驳回订单
			 */
			else if(processId.equals("suppilerDismissOrder")){
				filters.clear();
				filters.add(Filter.eq("status", 0));
				List<SupplierDismissOrderApply> applies = supplierDismissOrderApplyService.findList(null,filters,null);
				for(SupplierDismissOrderApply apply:applies){
					if(processInstanceId.equals(apply.getProcessInstanceId())){
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
							process.setProcessName("供应商驳回订单");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商实时打款
			 */
			else if(processId.equals("payServicePreTN")) {
				filters.add(Filter.eq("status",1));  //筛选状态为审核中
				filters.add(Filter.eq("step", 13));   //待市场部副总审核
				List<PaymentSupplier> paymentSuppliers=paymentSupplierService.findList(null,filters,null);
				filters.clear();
				for(PaymentSupplier payment:paymentSuppliers) {
					if(processInstanceId.equals(payment.getProcessInstanceId())) {
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
							process.setProcessName("供应商实时打款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商消团
			 */
			else if(processId.equals("xiaotuanProcess")) {
				List<HyGroupCancelAudit> hyGroupCancelAudits=hyGroupCancelAuditService.findAll();
				for (HyGroupCancelAudit tmp : hyGroupCancelAudits) {
					if (processInstanceId.equals(tmp.getProcessInstanceId())) {
						if(processList.contains("xiaotuanProcess")) {
							int num=processList.indexOf("xiaotuanProcess");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}
						else {
							processList.add("xiaotuanProcess");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("xiaotuanProcess");
							process.setProcessName("供应商消团");
							wrapProcessList.add(process);
						} 																				
					}
			    }					
			}				
			/**
			 * 供应商预付款
			 */
			else if(processId.equals("prePay")){
				filters.clear();
				filters.add(Filter.eq("state", 0));
				filters.add(Filter.eq("step", 2));
				List<PrePaySupply> paySupplies = prePaySupplyService.findList(null,filters,null);
				for(PrePaySupply prePaySupply:paySupplies){
					if(prePaySupply.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains("prePay")){
							int num = processList.indexOf("prePay");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add("prePay");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("预付款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商计调报账
			 */
			else if(processId.equals("regulateprocess")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				List<HyRegulate> hyRegulates = hyRegulateService.findList(null,filters,null);
				for(HyRegulate hyRegulate : hyRegulates){
					if(hyRegulate.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("计调报帐");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 供应商付尾款
			 */
			else if(processId.equals("banlanceDueCompany") || processId.equals("banlanceDueBranch")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				filters.add(Filter.eq("step", 3));
				List<BalanceDueApply> balanceDueApplies = balanceDueApplyService.findList(null,filters,null);
				for(BalanceDueApply balanceDueApply : balanceDueApplies){
					if(balanceDueApply.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains("balanceDue")){
							int num = processList.indexOf("balanceDue");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add("balanceDue");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("balanceDue");
							process.setProcessName("旅游元素付尾款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			
			/**
			 * 分公司充值
			 */
			else if(processId.equals("branchRecharge")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				List<BranchRecharge> branchRecharges = branchRechargeService.findList(null,filters,null);
				for(BranchRecharge branchRecharge : branchRecharges){
					if(processInstanceId.equals(branchRecharge.getProcessInstanceId())){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("分公司充值");
							wrapProcessList.add(process);
						}
					}
				}
 			} 
			
			/**
			 * 分公司团结算
			 */
			else if(processId.equals("branchsettleProcess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", 1));
				filters.add(Filter.eq("step", 2));
				List<PayablesBranchsettle> settles = payablesBranchsettleService.findList(null,filters,null);
				for(PayablesBranchsettle settle:settles){
					if(settle.getProcessInstanceId().equals(processInstanceId)){
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
							process.setProcessName("分公司团结算");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 江泰预充值
			 */
			else if(processId.equals("PaymentJiangtai")){
				filters.clear();
				filters.add(Filter.eq("applicationStatus", HyPaymentpreJiangtai.vicePresidentCheck));
				List<HyPaymentpreJiangtai> jiangtais = hyPaymentpreJiangtaiService.findList(null,filters,null);
				for(HyPaymentpreJiangtai jiangtai : jiangtais){
					if(jiangtai.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("江泰预充值");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 采购部申请退还剩余押金
			 */
			else if(processId.equals("tuiyajinprocess")){
				filters.clear();
				filters.add(Filter.eq("applicationStatus", HyPaymentpreJiangtai.vicePresidentCheck));
				List<HyPaymentpreJiangtai> jiangtais = hyPaymentpreJiangtaiService.findList(null,filters,null);
				for(HyPaymentpreJiangtai jiangtai : jiangtais){
					if(jiangtai.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("采购部申请退还剩余押金");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 采购部提前打款
			 */
			else if(processId.equals("payServicePre")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				filters.add(Filter.eq("step", 4));
				List<PaymentSupplier> paymentSuppliers=paymentSupplierService.findList(null,filters,null);
				for(PaymentSupplier payment:paymentSuppliers) {
					if(processInstanceId.equals(payment.getProcessInstanceId())) {
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
							process.setProcessName("提前打款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 分销商充值
			 */
			else if(processId.equals("distributorPrechargePro")){
				filters.clear();
				filters.add(Filter.eq("checkStatus", 1));
				List<HyDistributorPrechargeRecord> records = hyDistributorPrechargeRecordService.findList(null,filters,null);
				for(HyDistributorPrechargeRecord record:records){
					if(processInstanceId.equals(record.getProcessInstanceId())) {
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
							process.setProcessName("分销商充值");
							wrapProcessList.add(process);
						}
					}
				}
			}
			
			/**
			 * 分销商渠道结算
			 */
			else if(processId.equals("distributorSettlementPro")){
				filters.clear();
				filters.add(Filter.eq("checkStatus", 1));
				List<HyDistributorSettlement> settlements = hyDistributorSettlementService.findList(null,filters,null);
				for(HyDistributorSettlement settlement : settlements){
					if(processInstanceId.equals(settlement.getProcessInstanceId())) {
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
							process.setProcessName("分销商渠道结算");
							wrapProcessList.add(process);
						}
					}
				}
			}
		}
		if(!processList.contains("balanceDue")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("balanceDue");
			process.setProcessName("旅游元素付尾款");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("jiaoyajin")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("jiaoyajin");
			process.setProcessName("门店交押金");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("storeLogout")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeLogout");
			process.setProcessName("门店退出");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("storeRecharge")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeRecharge");
			process.setProcessName("门店充值");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("jiaoyajinprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("jiaoyajinprocess");
			process.setProcessName("供应商缴纳押金");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("cgbgystuiyajinprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("cgbgystuiyajinprocess");
			process.setProcessName("供应商退出");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("suppilerDismissOrder")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("suppilerDismissOrder");
			process.setProcessName("供应商驳回订单");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("payServicePreTN")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("payServicePreTN");
			process.setProcessName("供应商实时打款");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("xiaotuanProcess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("xiaotuanProcess");
			process.setProcessName("供应商消团");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("prePay")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("prePay");
			process.setProcessName("供应商预付款");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("regulateprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("regulateprocess");
			process.setProcessName("供应商计调报账");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("branchRecharge")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("branchRecharge");
			process.setProcessName("分公司充值");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("branchsettleProcess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("branchsettleProcess");
			process.setProcessName("分公司团结算");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("PaymentJiangtai")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("PaymentJiangtai");
			process.setProcessName("江泰预充值");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("tuiyajinprocess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("tuiyajinprocess");
			process.setProcessName("采购部申请退还剩余押金");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("payServicePre")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("payServicePre");
			process.setProcessName("采购部提前打款");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("distributorPrechargePro")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("distributorPrechargePro");
			process.setProcessName("分销商充值");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("distributorSettlementPro")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("distributorSettlementPro");
			process.setProcessName("分销商渠道结算");
			wrapProcessList.add(process);
		}	
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
			process.setProcessName("门店提现");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("mendiantixian");
			process.setProcessName("门店提现");
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
			process.setProcessName("分公司分成");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("fengongsifencheng");
			process.setProcessName("分公司分成");
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
			process.setProcessName("保险订单退款");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("insuranceTuiDing");
			process.setProcessName("保险订单退款");
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
			process.setProcessName("分公司预付款");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("fengongsiyufukuan1");
			process.setProcessName("分公司预付款");
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
			process.setProcessName("T+N打款");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("tjiandakuan1");
			process.setProcessName("T+N打款");
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
			process.setProcessName("提前打款");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("tiqiandakuan1");
			process.setProcessName("提前打款");
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
			process.setProcessName("旅游元素供应商尾款");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("lvyouyuansugongyingshangweikuan1");
			process.setProcessName("旅游元素供应商尾款");
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
			process.setProcessName("江泰预充款 ");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("jiangtaiyuchongzhi1");
			process.setProcessName("江泰预充款 ");
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
			process.setProcessName("总公司预付款 ");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("zonggongsiyufukuan1");
			process.setProcessName("总公司预付款 ");
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
			process.setProcessName("门店提现 ");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("mendiantixian1");
			process.setProcessName("门店提现 ");
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
			process.setProcessName("门店押金 ");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("mendianyajin1");
			process.setProcessName("门店押金 ");
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
			process.setProcessName("供应商保证金 ");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("gongyingshangbaozhengjin1");
			process.setProcessName("供应商保证金 ");
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
			process.setProcessName("导游报账应付款");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("daoyoubaozhangyingfukuan1");
			process.setProcessName("导游报账应付款");
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
			process.setProcessName("导游费用");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("daoyoufeiyong1");
			process.setProcessName("导游费用");
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
			process.setProcessName("分公司分成");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("fengongsifencheng1");
			process.setProcessName("分公司分成");
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
			process.setProcessName("分公司产品中心团结算");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("tuanjiesuan1");
			process.setProcessName("分公司产品中心团结算");
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
			process.setProcessName("实退款");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("shituikuan1");
			process.setProcessName("实退款");
			wrapProcessList.add(process);
		}
		
		
		
		
		
	}
	
	/**
	 * 分公司财务
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void fengongsicaiwu(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task : tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			
			/**
			 * 门店交管理费
			 */
			if(processId.equals("jiaoguanlifei")){
				filters.clear();
				filters.add(Filter.eq("type", 3));//交管理费
				filters.add(Filter.eq("applicationStatus",StoreApplication.init));
				List<StoreApplication> applications = storeApplicationService.findList(null,filters,null);
				for(StoreApplication application:applications){
					if(application.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("交管理费");
							wrapProcessList.add(process);
						}
					}
				}	
			}

			/**
			 * 增值服务打款
			 */
			else if(processId.equals("valueAdded")){
				filters.clear();
				filters.add(Filter.eq("step", 3));
				filters.add(Filter.eq("status", 1));
				List<AddedServiceTransfer> addedServices = addedServiceTransferService.findList(null,filters,null);
				for(AddedServiceTransfer tmp:addedServices){
					if (processInstanceId.equals(tmp.getProcessInstanceId())){
						if(processList.contains(processId)) {
							int num=processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}
						else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("valueAdded");
							process.setProcessName("增值服务打款");
							wrapProcessList.add(process);
						}
					}
				}
			}
		}
		
		if(!processList.contains("jiaoguanlifei")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("jiaoguanlifei");
			process.setProcessName("交管理费");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("valueAdded")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("valueAdded");
			process.setProcessName("增值服务打款");
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
			process.setProcessName("直营门店实收款");
			wrapProcessList.add(process);
		}  
		else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("zhiyingmendianshishoukuan");
			process.setProcessName("直营门店实收款");
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
			process.setProcessName("直营门店实退款");
			wrapProcessList.add(process);
		}  
		else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("zhiyingmendianshituikuan");
			process.setProcessName("直营门店实退款");
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
			process.setProcessName("分公司分成");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("fengongsifencheng");
			process.setProcessName("分公司分成");
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
			process.setProcessName("增值业务付款");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("zengzhiyewufukuan1");
			process.setProcessName("增值业务付款");
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
			process.setProcessName("实退款");
			wrapProcessList.add(process);
		}else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("shituikuan1");
			process.setProcessName("实退款");
			wrapProcessList.add(process);
		}
		
		
		
		
	}
	
	
	/**
	 * 总公司副总
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void zonggongsifuzong(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task:tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			
			/**
			 * 门店退出
			 */
			if(processId.equals("storeLogout")){
				filters.clear();
				filters.add(Filter.eq("type", 4));
				filters.add(Filter.eq("applicationStatus", StoreApplication.managerCheck));
				List<StoreApplication> storeApplications = storeApplicationService.findList(null,filters,null);
				for(StoreApplication storeApplication:storeApplications){
					if(storeApplication.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("门店退出");
							wrapProcessList.add(process);
						}
					}
				}
			}			
			/**
			 * 总公司预付款
			 */
			else if(processId.equals("prePay")){
				filters.clear();
				filters.add(Filter.eq("state", 0));
				filters.add(Filter.eq("step", 1));
				List<PrePaySupply> paySupplies = prePaySupplyService.findList(null,filters,null);
				for(PrePaySupply prePaySupply:paySupplies){
					if(prePaySupply.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains("prePay")){
							int num = processList.indexOf("prePay");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add("prePay");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("预付款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 总公司计调报账
			 */
			else if(processId.equals("regulateprocess")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				List<HyRegulate> hyRegulates = hyRegulateService.findList(null,filters,null);
				for(HyRegulate hyRegulate : hyRegulates){
					if(processDefinitionId.equals("usertask2") && hyRegulate.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("计调报帐");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 总公司供应商付尾款
			 */
			else if(processId.equals("banlanceDueCompany")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				filters.add(Filter.eq("step", 2));
				List<BalanceDueApply> balanceDueApplies = balanceDueApplyService.findList(null,filters,null);
				for(BalanceDueApply balanceDueApply : balanceDueApplies){
					if(balanceDueApply.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains("balanceDue")){
							int num = processList.indexOf("balanceDue");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add("balanceDue");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("balanceDue");
							process.setProcessName("旅游元素付尾款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 江泰预充值
			 */
			else if(processId.equals("PaymentJiangtai")){
				filters.clear();
				filters.add(Filter.eq("applicationStatus", HyPaymentpreJiangtai.managerCheck));
				List<HyPaymentpreJiangtai> jiangtais = hyPaymentpreJiangtaiService.findList(null,filters,null);
				for(HyPaymentpreJiangtai jiangtai : jiangtais){
					if(jiangtai.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("江泰预充值");
							wrapProcessList.add(process);
						}
					}
				}
			}

		}
		if(!processList.contains("storeLogout")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeLogout");
			process.setProcessName("门店退出");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("prePay")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("prePay");
			process.setProcessName("预付款");
			wrapProcessList.add(process);
		}
		if(!processList.contains("regulateprocess")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("regulateprocess");
			process.setProcessName("计调报帐");
			wrapProcessList.add(process);
		}
		if(!processList.contains("balanceDue")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("balanceDue");
			process.setProcessName("旅游元素付尾款");
			wrapProcessList.add(process);
		}
		if(!processList.contains("PaymentJiangtai")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("PaymentJiangtai");
			process.setProcessName("江泰预充值");
			wrapProcessList.add(process);
		}
	}
	
	
	/**
	 * 分公司副总
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void fengongsifuzong(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task:tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			
			/**
			 * 分公司预付款
			 */
			if(processId.equals("prePay")){
				filters.clear();
				filters.add(Filter.eq("state", 0));
				filters.add(Filter.eq("step", 0));
				List<PrePaySupply> paySupplies = prePaySupplyService.findList(null,filters,null);
				for(PrePaySupply prePaySupply:paySupplies){
					if(prePaySupply.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains("prePay")){
							int num = processList.indexOf("prePay");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add("prePay");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("prePay");
							process.setProcessName("预付款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 分公司计调报帐
			 */
			else if(processId.equals("regulateprocess")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				List<HyRegulate> hyRegulates = hyRegulateService.findList(null,filters,null);
				for(HyRegulate hyRegulate : hyRegulates){
					if(processDefinitionId.equals("usertask3") && hyRegulate.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("计调报帐");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 分公司旅游元素付尾款
			 */
			if(processId.equals("banlanceDueBranch")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				filters.add(Filter.eq("step", 2));
				List<BalanceDueApply> balanceDueApplies = balanceDueApplyService.findList(null,filters,null);
				for(BalanceDueApply balanceDueApply : balanceDueApplies){
					if(balanceDueApply.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains("balanceDue")){
							int num = processList.indexOf("balanceDue");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add("balanceDue");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("balanceDue");
							process.setProcessName("旅游元素付尾款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 增值服务打款
			 */
			else if(processId.equals("valueAdded")){
				filters.clear();
				filters.add(Filter.eq("step", 2));
				filters.add(Filter.eq("status", 1));
				List<AddedServiceTransfer> addedServices = addedServiceTransferService.findList(null,filters,null);
				for(AddedServiceTransfer tmp:addedServices){
					if (processInstanceId.equals(tmp.getProcessInstanceId())){
						if(processList.contains(processId)) {
							int num=processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}
						else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("valueAdded");
							process.setProcessName("增值服务打款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			
		}
		if(!processList.contains("prePay")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("prePay");
			process.setProcessName("预付款");
			wrapProcessList.add(process);
		}
		if(!processList.contains("regulateprocess")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("regulateprocess");
			process.setProcessName("计调报帐");
			wrapProcessList.add(process);
		}
		if(!processList.contains("balanceDue")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("balanceDue");
			process.setProcessName("旅游元素付尾款");
			wrapProcessList.add(process);
		}
		if(!processList.contains("valueAdded")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("valueAdded");
			process.setProcessName("增值服务打款");
			wrapProcessList.add(process);
		}
	}
	
	/**
	 * 市场部副总
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void shichangbufuzong(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task:tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			
			/**
			 * 自动打款申请
			 */
			if(processId.equals("payServicePreTN")) {
				filters.add(Filter.eq("status",1));  //筛选状态为审核中
				filters.add(Filter.eq("step", 12));   //待市场部副总审核
				List<PaymentSupplier> paymentSuppliers=paymentSupplierService.findList(null,filters,null);
				filters.clear();
				for(PaymentSupplier payment:paymentSuppliers) {
					if(processInstanceId.equals(payment.getProcessInstanceId())) {
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
							process.setProcessName("供应商实时打款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 提前打款
			 */
			else if(processId.equals("payServicePre")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				filters.add(Filter.eq("step", 3));
				List<PaymentSupplier> paymentSuppliers=paymentSupplierService.findList(null,filters,null);
				for(PaymentSupplier payment:paymentSuppliers) {
					if(processInstanceId.equals(payment.getProcessInstanceId())) {
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
							process.setProcessName("提前打款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 分公司团结算
			 */
			else if(processId.equals("branchsettleProcess")){
				filters.clear();
				filters.add(Filter.eq("auditStatus", 1));
				filters.add(Filter.eq("step", 1));
				List<PayablesBranchsettle> settles = payablesBranchsettleService.findList(null,filters,null);
				for(PayablesBranchsettle settle:settles){
					if(settle.getProcessInstanceId().equals(processInstanceId)){
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
							process.setProcessName("分公司团结算");
							wrapProcessList.add(process);
						}
					}
				}
			}
		}
		if(!processList.contains("payServicePreTN")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("payServicePreTN");
			process.setProcessName("供应商实时结算");
			wrapProcessList.add(process);
		}
		if(!processList.contains("payServicePre")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("payServicePre");
			process.setProcessName("提前打款");
			wrapProcessList.add(process);
		}
		if(!processList.contains("branchsettleProcess")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("branchsettleProcess");
			process.setProcessName("分公司团结算");
			wrapProcessList.add(process);
		}
		
		
	}
	
	/**
	 * 分公司产品中心经理
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void fengongsichanpinzhongxinjingli(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task:tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			
			/**
			 * 分公司预付款
			 */
			if(processId.equals("prePay")){
				filters.clear();
				filters.add(Filter.eq("state", 0));
				filters.add(Filter.eq("step", 0));
				List<PrePaySupply> paySupplies = prePaySupplyService.findList(null,filters,null);
				for(PrePaySupply prePaySupply:paySupplies){
					if(prePaySupply.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains("prePay")){
							int num = processList.indexOf("prePay");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add("prePay");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("prePay");
							process.setProcessName("预付款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 分公司计调报帐
			 */
			else if(processId.equals("regulateprocess")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				List<HyRegulate> hyRegulates = hyRegulateService.findList(null,filters,null);
				for(HyRegulate hyRegulate : hyRegulates){
					if(processDefinitionId.equals("usertask2") && hyRegulate.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("计调报帐");
							wrapProcessList.add(process);
						}
					}
				}
			}
		}
		if(!processList.contains("prePay")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("prePay");
			process.setProcessName("预付款");
			wrapProcessList.add(process);
		}
		if(!processList.contains("regulateprocess")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("regulateprocess");
			process.setProcessName("计调报帐");
			wrapProcessList.add(process);
		}
		
	}
	
	/**
	 * 总公司产品中心经理
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void zonggongsichanpinjingli(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task:tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			
			/**
			 * 总公司旅游元素付尾款
			 */
			if(processId.equals("banlanceDueCompany")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				filters.add(Filter.eq("step", 1));
				List<BalanceDueApply> balanceDueApplies = balanceDueApplyService.findList(null,filters,null);
				for(BalanceDueApply balanceDueApply : balanceDueApplies){
					if(balanceDueApply.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains("balanceDue")){
							int num = processList.indexOf("balanceDue");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add("balanceDue");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("balanceDue");
							process.setProcessName("旅游元素付尾款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 总公司预付款
			 */
			else if(processId.equals("prePay")){
				filters.clear();
				filters.add(Filter.eq("state", 0));
				filters.add(Filter.eq("step", 0));
				List<PrePaySupply> paySupplies = prePaySupplyService.findList(null,filters,null);
				for(PrePaySupply prePaySupply:paySupplies){
					if(prePaySupply.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains("prePay")){
							int num = processList.indexOf("prePay");
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add("prePay");
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("预付款");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 总公司计调报账
			 */
			else if(processId.equals("regulateprocess")){
				filters.clear();
				filters.add(Filter.eq("status", 1));
				List<HyRegulate> hyRegulates = hyRegulateService.findList(null,filters,null);
				for(HyRegulate hyRegulate : hyRegulates){
					if(processDefinitionId.equals("usertask2") && hyRegulate.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("计调报帐");
							wrapProcessList.add(process);
						}
					}
				}
			}
		}
		if(!processList.contains("balanceDue")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("balanceDue");
			process.setProcessName("旅游元素付尾款");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("prePay")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("prePay");
			process.setProcessName("预付款");
			wrapProcessList.add(process);
		}
		if(!processList.contains("regulateprocess")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("regulateprocess");
			process.setProcessName("计调报帐");
			wrapProcessList.add(process);
		}
	}
	
	/**
	 * 连锁发展经理
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void liansuofazhanjingli(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task: tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			
			/**
			 * 虹宇门店和直营门店门店新建
			 */			
			if(processId.equals("storeRegistration")){
				filters.add(Filter.eq("type", 0));
				filters.add(Filter.eq("applicationStatus", 0));
				List<StoreApplication> storeApplications = storeApplicationService.findList(null,filters,null);
				for(StoreApplication storeApplication:storeApplications){
					if(storeApplication.getProcessInstanceId().equals(processInstanceId)){
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
							process.setProcessName("新建门店");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 门店退出
			 */
			else if(processId.equals("storeLogout")){
				filters.clear();
				filters.add(Filter.eq("type", 4));
				filters.add(Filter.eq("applicationStatus", 0));
				List<StoreApplication> storeApplications = storeApplicationService.findList(null,filters,null);
				for(StoreApplication storeApplication:storeApplications){
					if(storeApplication.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("门店退出");
							wrapProcessList.add(process);
						}
					}
				}
			}
		}
		if(!processList.contains("storeRegistration")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeRegistration");
			process.setProcessName("新建门店");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("storeLogout")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeLogout");
			process.setProcessName("门店退出");
			wrapProcessList.add(process);
		}
	}
	/**
	 * 连锁发展员工
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void liansuofazhanyuangong(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task: tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			
			/**
			 * 门店续签
			 */
			if(processId.equals("storeRenew")){
				filters.clear();
				filters.add(Filter.eq("type", 2));
				filters.add(Filter.eq("applicationStatus", 0));
				List<StoreApplication> storeApplications = storeApplicationService.findList(null,filters,null);
				for(StoreApplication storeApplication:storeApplications){
					if(storeApplication.getProcessInstanceId().equals(processInstanceId)){
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
							process.setProcessName("门店续签");
							wrapProcessList.add(process);
						}
					}
				}
			}
			/**
			 * 门店退出
			 */
			else if(processId.equals("storeLogout")){
				filters.clear();
				filters.add(Filter.eq("type", 4));
				filters.add(Filter.eq("applicationStatus", 0));
				List<StoreApplication> storeApplications = storeApplicationService.findList(null,filters,null);
				for(StoreApplication storeApplication:storeApplications){
					if(storeApplication.getProcessInstanceId().equals(processInstanceId)){
						if(processList.contains(processId)){
							int num = processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId(processId);
							process.setProcessName("门店退出");
							wrapProcessList.add(process);
						}
					}
				}
			}
		}
		if(!processList.contains("storeRenew")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeRenew");
			process.setProcessName("门店续签");
			wrapProcessList.add(process);
		}	
		if(!processList.contains("storeLogout")){
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeLogout");
			process.setProcessName("门店退出");
			wrapProcessList.add(process);
		}
	}
	
	/**
	 * 直营门店员工
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 * @param storeId
	 */
	public void zhiyingmendianyuangong(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username,Long storeId){
		for(Task task:tasks) {
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];	
			
			//消团审核
			if(processId.equals("xiaotuanProcess")) {
				String processInstanceId=task.getProcessInstanceId(); //流程实例id
				List<HyGroupCancelAudit> hyGroupCancelAudits=hyGroupCancelAuditService.findAll();
				for (HyGroupCancelAudit tmp : hyGroupCancelAudits) {
					if (processInstanceId.equals(tmp.getProcessInstanceId())) {
						if(task.getTaskDefinitionKey().equals("usertask2")) {
							//修改逻辑 从groupStoreCancel中找寻记录，若有就说明已经审核，不返回给前端了
							Long groupId = tmp.getHyGroup().getId();
							List<Filter> filters1 = new ArrayList<>();
							filters1.add(Filter.eq("groupId", groupId));
							filters1.add(Filter.eq("storeId", storeId));
							List<GroupStoreCancel> gscs = groupStoreCancelService.findList(null, filters1, null);
							//如果是空的,就没有审核过
							if(gscs.isEmpty()) {
								if(processList.contains("xiaotuanProcess")) {
									int num=processList.indexOf("xiaotuanProcess");
									int preCount=wrapProcessList.get(num).getCount();
									wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
								}
								else {
									processList.add("xiaotuanProcess");
									WrapProcess process=new WrapProcess();
									process.setCount(1); //将数量设置成1
									process.setProcessId("xiaotuanProcess");
									process.setProcessName("供应商消团");
									wrapProcessList.add(process);
								}
							}
						} 																					
					}
			    }					
			}
		}
		if(!processList.contains("xiaotuanProcess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("xiaotuanProcess");
			process.setProcessName("供应商消团");
			wrapProcessList.add(process);
		}	
		mendian(wrapProcessList, tasks, hyAdmin, processList, username, storeId);
	}
	
	/**
	 * 直营门店经理的消团审核，增值服务打款，门店未确认订单和门店未支付订单
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 * @param storeId
	 */
	public void zhiyingmendianjingli(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username,Long storeId){
		List<Filter> filteres = new ArrayList<>();
		for(Task task:tasks) {
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];	
			
			//消团审核
			if(processId.equals("xiaotuanProcess")) {
				String processInstanceId=task.getProcessInstanceId(); //流程实例id
				List<HyGroupCancelAudit> hyGroupCancelAudits=hyGroupCancelAuditService.findAll();
				for (HyGroupCancelAudit tmp : hyGroupCancelAudits) {
					if (processInstanceId.equals(tmp.getProcessInstanceId())) {
						if(task.getTaskDefinitionKey().equals("usertask2")) {
							//修改逻辑 从groupStoreCancel中找寻记录，若有就说明已经审核，不返回给前端了
							Long groupId = tmp.getHyGroup().getId();
							List<Filter> filters1 = new ArrayList<>();
							filters1.add(Filter.eq("groupId", groupId));
							filters1.add(Filter.eq("storeId", storeId));
							List<GroupStoreCancel> gscs = groupStoreCancelService.findList(null, filters1, null);
							//如果是空的,就没有审核过
							if(gscs.isEmpty()) {
								if(processList.contains("xiaotuanProcess")) {
									int num=processList.indexOf("xiaotuanProcess");
									int preCount=wrapProcessList.get(num).getCount();
									wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
								}
								else {
									processList.add("xiaotuanProcess");
									WrapProcess process=new WrapProcess();
									process.setCount(1); //将数量设置成1
									process.setProcessId("xiaotuanProcess");
									process.setProcessName("供应商消团");
									wrapProcessList.add(process);
								}
							}
						} 																					
					}
			    }					
			}
			//增值服务打款
			else if(processId.equals("valueAdded")){
				String processInstanceId = task.getProcessInstanceId();
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("step", 1));
				List<AddedServiceTransfer> addedServices = addedServiceTransferService.findList(null,filters,null);
				for(AddedServiceTransfer tmp:addedServices){
					if (processInstanceId.equals(tmp.getProcessInstanceId())){
						if(processList.contains(processId)) {
							int num=processList.indexOf(processId);
							int preCount=wrapProcessList.get(num).getCount();
							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
						}
						else {
							processList.add(processId);
							WrapProcess process=new WrapProcess();
							process.setCount(1); //将数量设置成1
							process.setProcessId("valueAdded");
							process.setProcessName("增值服务打款");
							wrapProcessList.add(process);
						}
					}
				}
			}
		}
		if(!processList.contains("valueAdded")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("valueAdded");
			process.setProcessName("增值服务打款");
			wrapProcessList.add(process);
		}
		if(!processList.contains("xiaotuanProcess")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("xiaotuanProcess");
			process.setProcessName("供应商消团");
			wrapProcessList.add(process);
		}	
		mendian(wrapProcessList, tasks, hyAdmin, processList, username, storeId);		    
	   
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
				processName = "线路订单待供应商确认";
				break;
			}
			case 2: {
				processId = "rengoumenpiaoOrderConfirm";
				processName = "认购门票订单待供应商确认";
				break;
			}
			case 3:{
				processId = "hotelOrderConfirm";
				processName = "酒店订单待供应商确认";
				break;
			}
			case 4:{
				processId = "menpiaoOrderConfirm";
				processName = "门票订单待供应商确认";
				break;
			}
			case 5:{
				processId = "ticketandhotelOrderConfirm";
				processName = "酒加景订单待供应商确认";
				break;
			}
			case 7:{
				processId = "visaOrderConfirm";
				processName = "签证订单待供应商确认";
				break;
			}
			default:
				break;
			}
			
			
			if(hyOrders.size()>0) {
				WrapProcess process=new WrapProcess();
				process.setCount(hyOrders.size()); //待确定订单数量
				process.setProcessId(processId);
				process.setProcessName(processName);
				wrapProcessList.add(process);
			}  
			else {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //待确定订单数量
				process.setProcessId(processId);
				process.setProcessName(processName);
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
				processName = "订购导游订单未支付";
				break;
			}
			case 1: {
				processId = "lineOrderPay";
				processName = "线路订单未支付";
				break;
			}
			case 2:{
				processId = "rengoumenpiaoOrderPay";
				processName = "认购门票订单未支付";
				break;
			}
			case 3:{
				processId = "hotelOrderPay";
				processName = "酒店订单未支付";
				break;
			}
			case 4:{
				processId = "ticketOrderPay";
				processName = "门票订单未支付";
				break;
			}
			case 5:{
				processId = "ticketandhotelOrderPay";
				processName = "酒加景订单未支付";
				break;
			}
			case 6:{
				processId = "insuranceOrderPay";
				processName = "保险订单未支付";
				break;
			}
			case 7:{
				processId = "visaOrderPay";
				processName = "签证订单未支付";
				break;
			}
			default:
				break;		
			}
				
			if(store_hyOrders.size()>0) {
				WrapProcess process=new WrapProcess();
				process.setCount(store_hyOrders.size()); //待支付订单数量
				process.setProcessId(processId);
				process.setProcessName(processName);
				wrapProcessList.add(process);
			}
			else {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //待支付订单数量
				process.setProcessId(processId);
				process.setProcessName(processName);
				wrapProcessList.add(process);
			}
			filters.clear();
		}
	}
	
	/**
	 * 6种退团和售后
	 * @param wrapProcessList
	 * @param task
	 * @param processId
	 * @param processInstanceId
	 * @param processList
	 * @param status
	 */
	public void mendiantuituan(List<WrapProcess> wrapProcessList,Task task,String processId,String processInstanceId,List<String> processList,Integer status){
		List<Filter> filters = new ArrayList<>();
		if(processId.equals("storeTuiTuan")) {
			filters.add(Filter.eq("status",status));  //1:待品控员工审核 2：待财务审核
			filters.add(Filter.eq("type", HyOrderApplication.STORE_CANCEL_GROUP)); //0-门店退团			
			List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filters,null);
			String processName = "";
			for(HyOrderApplication application:orderApplications) {
				HyOrder order = hyOrderService.find(application.getOrderId());
				if(processInstanceId.equals(application.getProcessInstanceId())) {
					switch(order.getType()){
					case 1: {
						processId = "storeTuiTuan";
						processName = "线路订单退团";
						break;
					}
					case 2: {
						processId = "rengoumenpiaoTuiDing";
						processName = "认购门票退订";
						break;
					}
					case 3:{
						processId = "hotelTuiDing";
						processName = "酒店订单退订";
						break;
					}
					case 4:{
						processId = "ticketTuiDing";
						processName = "门票订单退订";
						break;
					}
					case 5:{
						processId = "ticketandhotelTuiDing";
						processName = "酒加景订单退订";
						break;
					}
					case 7:{
						processId = "visaTuiDing";
						processName = "签证订单退订";
						break;
					}
					default:
						break;
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
						process.setProcessName(processName);
						wrapProcessList.add(process);
						}
				}
			}
		}	
		/**
		 * 门店售后
		 */
		else if(processId.equals("storeShouHou")) {
			filters.clear();
			filters.add(Filter.eq("status",status));  //待财务审核
			filters.add(Filter.eq("type", HyOrderApplication.STORE_CUSTOMER_SERVICE)); //0-门店售后			
			List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filters,null);
			String processName = "";
			for(HyOrderApplication application:orderApplications) {
				HyOrder order = hyOrderService.find(application.getOrderId());
				if(processInstanceId.equals(application.getProcessInstanceId())) {
					switch(order.getType()){
					case 1: {
						processId = "storeShouHou";
						processName = "线路订单售后";
						break;
					}
					case 2: {
						processId = "rengoumenpiaoShouHou";
						processName = "认购门票售后";
						break;
					}
					case 3:{
						processId = "hotelShouHou";
						processName = "酒店订单售后";
						break;
					}
					case 4:{
						processId = "ticketShouHou";
						processName = "门票订单售后";
						break;
					}
					case 5:{
						processId = "ticketandhotelShouHou";
						processName = "酒加景订单售后";
						break;
					}
					case 7:{
						processId = "visaShouHou";
						processName = "签证订单售后";
						break;
					}
					default:
						break;
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
						process.setProcessName(processName);
						wrapProcessList.add(process);
					}
				}
			}
		}	
	}
	

	/**
	 * 6种退团和售后 新
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
			//status  1:待品控员工审核 2：待财务审核
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
	 * 票务内部供应商
	 * @author LBC
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void piaowuneibugongyingshang(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
		for(Task task : tasks){
			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];
			String processInstanceId=task.getProcessInstanceId();
			
			List<Filter> filters = new ArrayList<>();
			
			if(processId.equals("storeTuiTuan")) {
				filters.add(Filter.eq("status",0));  //0:待供应商确认 1:待品控员工审核 2：待财务审核
				filters.add(Filter.eq("type", HyOrderApplication.STORE_CANCEL_GROUP)); //0-门店退团			
				List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filters,null);
				String processName = "";
				for(HyOrderApplication application:orderApplications) {
					HyOrder order = hyOrderService.find(application.getOrderId());
					if(processInstanceId.equals(application.getProcessInstanceId())) {
						switch(order.getType()){
						case 3:{
							processId = "hotelTuiDing";
							processName = "酒店订单退订";
							break;
						}
						case 4:{
							processId = "ticketTuiDing";
							processName = "门票订单退订";
							break;
						}
						case 5:{
							processId = "ticketandhotelTuiDing";
							processName = "酒加景订单退订";
							break;
						}
						default:
							break;
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
							process.setProcessName(processName);
							wrapProcessList.add(process);
						}
					}
				}
			}	
			/**
			 * 门店售后
			 */
			else if(processId.equals("storeShouHou")) {
				filters.clear();
				filters.add(Filter.eq("status",0));  //0:待供应商确认 1:待品控员工审核 2：待财务审核
				filters.add(Filter.eq("type", HyOrderApplication.STORE_CUSTOMER_SERVICE)); //0-门店售后			
				List<HyOrderApplication> orderApplications=hyOrderApplicationService.findList(null,filters,null);
				String processName = "";
				for(HyOrderApplication application:orderApplications) {
					HyOrder order = hyOrderService.find(application.getOrderId());
					if(processInstanceId.equals(application.getProcessInstanceId())) {
						switch(order.getType()){
						case 3:{
							processId = "hotelShouHou";
							processName = "酒店订单售后";
							break;
						}
						case 4:{
							processId = "ticketShouHou";
							processName = "门票订单售后";
							break;
						}
						case 5:{
							processId = "ticketandhotelShouHou";
							processName = "酒加景订单售后";
							break;
						}
						
						default:
							break;
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
							process.setProcessName(processName);
							wrapProcessList.add(process);
						}
					}
				}
			}
			//票务内部供应商没有打款单审核
//			/**
//			 * T+N自动打款打款单审核
//			 */
//			else if(processId.equals("payServicePreTN")) {
//				filters.clear();
//				filters.add(Filter.eq("status",1));  //筛选状态为审核中
//				filters.add(Filter.eq("supplierContract", supplierContract)); //如果是合同负责人账号,按合同筛选
//				//如果不是合同负责人,只查找自己负责的
//				if(hyAdmin.getHyAdmin()!=null) {
//					filters.add(Filter.eq("operator", hyAdmin));
//				}
//				List<PaymentSupplier> paymentSuppliers=paymentSupplierService.findList(null,filters,null);
//				filters.clear();
//				for(PaymentSupplier payment:paymentSuppliers) {
//					if(processInstanceId.equals(payment.getProcessInstanceId())) {
//						//如果已经有一个改流程的代办事项,将数量加1
//						if(processList.contains(processId)) {
//							int num=processList.indexOf(processId);
//							int preCount=wrapProcessList.get(num).getCount();
//							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
//						}
//						else {
//							processList.add(processId);
//							WrapProcess process=new WrapProcess();
//							process.setCount(1); //将数量设置成1
//							process.setProcessId(processId);
//							process.setProcessName("T+N自动打款打款单审核");
//							wrapProcessList.add(process);
//						}
//					}
//				}
//			}
			
			
			
		}
		
		/**
		 * 判空
		 */
		String processId = "";
		String processName = "";
		for(int i=3;i<=6;i++){
			switch(i){
			case 3:{
				processId = "hotelShouHou";
				processName = "酒店订单售后";
				break;
			}
			case 4:{
				processId = "ticketShouHou";
				processName = "门票订单售后";
				break;
			}
			case 5:{
				processId = "ticketandhotelShouHou";
				processName = "酒加景订单售后";
				break;
			}
			default:
				break;
			}
			if(!processList.contains(processId)) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId(processId);
				process.setProcessName(processName);
				wrapProcessList.add(process);
			}
		}
		
		for(int i=3;i<=6;i++){
			switch(i){
			case 3:{
				processId = "hotelTuiDing";
				processName = "酒店订单退订";
				break;
			}
			case 4:{
				processId = "ticketTuiDing";
				processName = "门票订单退订";
				break;
			}
			case 5:{
				processId = "ticketandhotelTuiDing";
				processName = "酒加景订单退订";
				break;
			}
			default:
				break;
			}
			if(!processList.contains(processId)) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId(processId);
				process.setProcessName(processName);
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
			filters.clear();
			filters.add(Filter.eq("status", Constants.HY_ORDER_STATUS_WAIT_PROVIDER_CONFIRM)); //2-待供应商确认
			filters.add(Filter.eq("type", i)); 
			//如果是合同合同负责人,看所有订单
			if(hyAdmin.getHyAdmin()==null) {
				List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
				hyAdmins.add(hyAdmin);
				filters.add(Filter.in("supplier", hyAdmins)); //产品创建人,团的计调
			}
			//如果不是合同合同负责人,筛选本人上的产品
			else {
				filters.add(Filter.eq("supplier", hyAdmin)); //产品创建人,团的计调
			}
			
			List<HyOrder> hyOrders=hyOrderService.findList(null,filters,null);
			
			switch (i) {
			case 3:{
				processId = "hotelOrderConfirm";
				processName = "酒店订单待供应商确认";
				break;
			}
			case 4:{
				processId = "menpiaoOrderConfirm";
				processName = "门票订单待供应商确认";
				break;
			}
			case 5:{
				processId = "ticketandhotelOrderConfirm";
				processName = "酒加景订单待供应商确认";
				break;
			}
			default:
				break;
			}
			
			
			if(hyOrders.size()>0) {
				WrapProcess process=new WrapProcess();
				process.setCount(hyOrders.size()); //待确定订单数量
				process.setProcessId(processId);
				process.setProcessName(processName);
				wrapProcessList.add(process);
			}  
			else {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //待确定订单数量
				process.setProcessId(processId);
				process.setProcessName(processName);
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
				name = "门票促销审核";
				id = "ticketPromotion";
				break;
			}
			case 1:{
				name = "酒店促销审核";
				id = "hotelPromotion";
				break;
			}
			case 2:{
				name = "酒加景促销审核";
				id = "ticketandhotelPromotion";
				break;
				}
			}
			WrapProcess ticket_promotion=new WrapProcess();
			ticket_promotion.setCount(activities.size());
			ticket_promotion.setProcessId(id);
			ticket_promotion.setProcessName(name);
			wrapProcessList.add(ticket_promotion);
		}
		
		
		
		
	}
	
	/**
	 * 线路内部供应商
	 * @author LBC
	 * @param wrapProcessList
	 * @param tasks
	 * @param hyAdmin
	 * @param processList
	 * @param username
	 */
	public void xianluneibugongyingshang(List<WrapProcess> wrapProcessList,List<Task> tasks,HyAdmin hyAdmin,List<String> processList,String username){
    	for(Task task:tasks) {

			String processDefinitionId=task.getProcessDefinitionId();
			String[] strs=processDefinitionId.split(":");
			//找出流程id
			String processId=strs[0];	
			String processInstanceId=task.getProcessInstanceId(); //流程实例id
			
//			//内部没有打款单
//			/**
//			 * 自动打款申请
//			 */
//			if(processId.equals("payServicePreTN")) {
//				List<Filter> filters=new ArrayList<>();
//				filters.add(Filter.eq("status",1));  //筛选状态为审核中
//				filters.add(Filter.eq("supplierContract", supplierContract)); //如果是合同负责人账号,按合同筛选
//				//如果不是合同负责人,只查找自己负责的
//				if(hyAdmin.getHyAdmin()!=null) {
//					filters.add(Filter.eq("operator", hyAdmin));
//				}
//				List<PaymentSupplier> paymentSuppliers=paymentSupplierService.findList(null,filters,null);
//				filters.clear();
//				for(PaymentSupplier payment:paymentSuppliers) {
//					if(processInstanceId.equals(payment.getProcessInstanceId())) {
//						//如果已经有一个改流程的代办事项,将数量加1
//						if(processList.contains(processId)) {
//							int num=processList.indexOf(processId);
//							int preCount=wrapProcessList.get(num).getCount();
//							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
//						}
//						else {
//							processList.add(processId);
//							WrapProcess process=new WrapProcess();
//							process.setCount(1); //将数量设置成1
//							process.setProcessId(processId);
//							process.setProcessName("T+N自动打款打款单审核");
//							wrapProcessList.add(process);
//						}
//					}
//				}
//			}
			
//			/**
//			 * 线路促销申请 
//			 */
//			else if(processId.equals("LinePromotion")) {
//				List<Filter> filters=new ArrayList<>();
//				filters.add(Filter.eq("state",0));  //筛选状态为审核中
//				filters.add(Filter.eq("isCaigouti", true));
//				//如果是合同负责人,能看到子账号下所有信息
//				if(hyAdmin.getHyAdmin()==null) {
//					List<HyAdmin> hyAdmins=new ArrayList<>(hyAdmin.getHyAdmins());
//					hyAdmins.add(hyAdmin);
//					filters.add(Filter.in("operator", hyAdmins));
//				}
//				//如果是子账号,只能看到自己负责的部分
//				else {
//					filters.add(Filter.eq("operator", hyAdmin));
//				}
//				List<LinePromotion> linepromotions=linePromotionService.findList(null,filters,null);
//				for(LinePromotion promotion:linepromotions) {
//					if(processInstanceId.equals(promotion.getProcessInstanceId())) {
//						//如果已经有一个该流程的代办事项,将数量加1
//						if(processList.contains(processId)) {
//							int num=processList.indexOf(processId);
//							int preCount=wrapProcessList.get(num).getCount();
//							wrapProcessList.get(num).setCount(preCount+1);  //将数量加1
//						}
//						else {
//							processList.add(processId);
//							WrapProcess process=new WrapProcess();
//							process.setCount(1); //将数量设置成1
//							process.setProcessId(processId);
//							process.setProcessName(processMap.get(processId));
//							wrapProcessList.add(process);
//						}
//					}
//				}
//			}
			
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
									process.setProcessName(processName);
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
									process.setProcessName(processName);
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
										process.setProcessName(processName);
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
										process.setProcessName(processName);
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
							process.setProcessName("旅游元素付尾款");
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
    			process.setProcessName("旅游元素付尾款");
    			wrapProcessList.add(process);
    		}
		}
		
		if(!processList.contains("storeTuiTuan")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeTuiTuan");
			process.setProcessName("门店退团申请");
			wrapProcessList.add(process);
		}
		if(!processList.contains("storeShouHou")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("storeShouHou");
			process.setProcessName("门店退团申请");
			wrapProcessList.add(process);
		}
		if(!processList.contains("rengoumenpiaoTuiDing")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("rengoumenpiaoTuiDing");
			process.setProcessName("认购门票退订申请");
			wrapProcessList.add(process);
		}
		if(!processList.contains("rengoumenpiaoShouHou")) {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //将数量设置成0
			process.setProcessId("rengoumenpiaoShouHou");
			process.setProcessName("认购门票售后申请");
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
		process_promotion.setProcessName("线路促销审核");
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
		ticket_promotion.setProcessName(name);
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
			process.setProcessName("订单确认");
			wrapProcessList.add(process);
		}  
		else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("orderConfirm");
			process.setProcessName("订单待供应商确认");
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
			process.setCount(visaOrders.size()); //待确定订单数量
			process.setProcessId("rengoumenpiaoorderConfirm");
			process.setProcessName("认购门票订单确认");
			wrapProcessList.add(process);
		}  
		else {
			WrapProcess process=new WrapProcess();
			process.setCount(0); //待确定订单数量
			process.setProcessId("rengoumenpiaoorderConfirm");
			process.setProcessName("认购门票订单确认");
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
				processName = "线路订单售后";
				break;
			}
			case 2: {
				processId = "rengoumenpiaoShouHou";
				processName = "认购门票售后";
				break;
			}
			case 3:{
				processId = "hotelShouHou";
				processName = "酒店订单售后";
				break;
			}
			case 4:{
				processId = "ticketShouHou";
				processName = "门票订单售后";
				break;
			}
			case 5:{
				processId = "ticketandhotelShouHou";
				processName = "酒加景订单售后";
				break;
			}
			case 6:{
				processId = "visaShouHou";
				processName = "签证订单售后";
				break;
			}
			default:
				break;
			}
			if(!processList.contains(processId)) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId(processId);
				process.setProcessName(processName);
				wrapProcessList.add(process);
			}
		}
		
		for(int i=1;i<=6;i++){
			switch(i){
			case 1: {
				processId = "storeTuiTuan";
				processName = "线路订单退团";
				break;
			}
			case 2: {
				processId = "rengoumenpiaoTuiDing";
				processName = "认购门票退订";
				break;
			}
			case 3:{
				processId = "hotelTuiDing";
				processName = "酒店订单退订";
				break;
			}
			case 4:{
				processId = "ticketTuiDing";
				processName = "门票订单退订";
				break;
			}
			case 5:{
				processId = "ticketandhotelTuiDing";
				processName = "酒加景订单退订";
				break;
			}
			case 6:{
				processId = "visaTuiDing";
				processName = "签证订单退订";
				break;
			}
			default:
				break;
			}
			if(!processList.contains(processId)) {
				WrapProcess process=new WrapProcess();
				process.setCount(0); //将数量设置成0
				process.setProcessId(processId);
				process.setProcessName(processName);
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
