package com.hongyu.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BalanceDueApplyItem;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.entity.Inbound;
import com.hongyu.entity.PayablesBranchsettle;
import com.hongyu.entity.RegulategroupAccount;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.service.BalanceDueApplyItemService;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.PayablesBranchsettleService;
import com.hongyu.service.RegulategroupAccountService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants.DeductLine;

@Controller
@RequestMapping("/admin/branchsettle/payables")
public class PayablesBranchsettleController {
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@Resource(name="commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	CommonEdushenheService commonEdushenheService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "balanceDueApplyItemServiceImpl")
	BalanceDueApplyItemService balanceDueApplyItemService;
	
	@Resource(name = "regulategroupAccountServiceImpl")
	RegulategroupAccountService regulategroupAccountService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	@Resource(name = "payablesBranchsettleServiceImpl")
	PayablesBranchsettleService payablesBranchsettleService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	HyOrderApplicationService hyOrderApplicationService;
	
	@Autowired
	HyOrderItemService hyOrderItemService;
	/**
	 * 分公司团结算列表页
	 * Author:GSbing
	 */
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable,String supplierName,String productId,String creatorName,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try{
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();	
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co=(CheckedOperation) request.getAttribute("co");
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			List<Filter> groupFilter=new ArrayList<Filter>();
			groupFilter.add(Filter.in("creator",hyAdmins));			
			if(productId==null && supplierName==null){
				if(creatorName!=null&&!creatorName.equals("")){
					List<Filter> filter=new ArrayList<Filter>();
					filter.add(Filter.like("name",creatorName));
					List<HyAdmin> adminList=hyAdminService.findList(null,filter,null);
					if(adminList.size()==0){
						json.setMsg("查询成功");
					    json.setSuccess(true);
					    json.setObj(new Page<HyGroup>());
					}
					else{
						groupFilter.add(Filter.in("creator", adminList));
						List<HyGroup> groupList=hyGroupService.findList(null,groupFilter,null);
						if(groupList.size()==0){
							json.setMsg("查询成功");
						    json.setSuccess(true);
						    json.setObj(new Page<HyGroup>());
						}
						else{
							List<Filter> settleFilter=new ArrayList<>();
							settleFilter.add(Filter.in("hyGroup", groupList));
							settleFilter.add(Filter.eq("auditStatus",0));
							List<PayablesBranchsettle> settleList=payablesBranchsettleService.findList(null,settleFilter,null);
							if(settleList.isEmpty()){
								json.setMsg("查询成功");
							    json.setSuccess(true);
							    json.setObj(new Page<HyGroup>());
							}
							else{
								for(PayablesBranchsettle settle:settleList){
									HyGroup hyGroup=settle.getHyGroup();
									HyAdmin creator=hyGroup.getCreator();
									HashMap<String,Object> groupMap=new HashMap<String,Object>();
									groupMap.put("settleId", settle.getId());
									groupMap.put("productId", hyGroup.getLine().getPn());
									groupMap.put("startDate", hyGroup.getStartDay());
									groupMap.put("productName", hyGroup.getLine().getName());
									groupMap.put("supplierName", hyGroup.getLine().getHySupplier().getSupplierName());
									groupMap.put("creatorName", hyGroup.getCreator().getName());
									/*初始化金额等于0*/
									BigDecimal money=new BigDecimal(0);
									List<Filter> accountFilter=new ArrayList<>();
									accountFilter.add(Filter.eq("groupId", hyGroup));
		
									List<RegulategroupAccount> accountList=regulategroupAccountService.findList(null,accountFilter,null);
									if(accountList.size()>0){
										money=money.add(accountList.get(0).getAllIncome()); //总收入
										/*总收入-扣点-总支出+预付款*/
										/*计算公式修改,结算金额=总收入-扣点,20181006*/
//										money=money.subtract(accountList.get(0).getAllExpense());
									}
									/** add by wj 2019.06.28 计算扣点金额，并存如settle表 
									 *  扣点金额只计算 在计调提交之前没有退团的总扣点，也就是需要用现在订单的扣点金额 加上 在计调提交后 退团的订单扣点金额总数**/
									BigDecimal xianzaikoudian =BigDecimal.ZERO;
									BigDecimal koudian = BigDecimal.ZERO;
									
//									money = money.subtract(settle.getKoudianMoney()==null?BigDecimal.ZERO:settle.getKoudian());
									
//									if(hyGroup.getKoudianType().equals(DeductLine.tuanke)){
//										BigDecimal div=new BigDecimal(100);
//										/*总收入减去扣点,扣点为总收入乘以扣点百分比，保留两位小数,四舍五入*/
//										money=money.subtract((money.multiply(hyGroup.getPercentageKoudian()).divide(div)).setScale(2, RoundingMode.HALF_UP));
//									}
//									/*如果是人头扣点,用总收入减去每个人扣点乘以报名人数*/
//									else if(hyGroup.getKoudianType().equals(DeductLine.rentou)){
//										money=money.subtract(hyGroup.getPersonKoudian().multiply(new BigDecimal(hyGroup.getSignupNumber())));
//									}
									/** end of add **/
									/**计算公式修改,不再计算预付款,20181006*/
//									List<Filter> balanceFilter=new ArrayList<>();
//									balanceFilter.add(Filter.eq("groupId", hyGroup.getId()));
//									balanceFilter.add(Filter.eq("payStatus", 1));								
//									List<BalanceDueApplyItem> balanceList=balanceDueApplyItemService.findList(null,balanceFilter,null);
//									BigDecimal prepay=new BigDecimal(0);							
//									if(balanceList.size()>0){
//										/*将所有的usePrePay相加，得到预付款prepay*/
//										for(BalanceDueApplyItem balanceDueApplyItem:balanceList){
//											prepay=prepay.add(balanceDueApplyItem.getUsePrePay());
//										}
//									}	
//									money=money.add(prepay);
									BigDecimal refundMoney=new BigDecimal(0);//退款
									List<Filter> orderFilter=new ArrayList<>();
									orderFilter.add(Filter.eq("groupId",hyGroup.getId()));
									List<HyOrder> orderList=hyOrderService.findList(null,orderFilter,null);
									List<HyOrderApplication> appList=new ArrayList<HyOrderApplication>();
									
									if(orderList.size()>0){
										for(HyOrder hyOrder:orderList){
//											List<Filter> appFilter=new ArrayList<Filter>();
//											appFilter.add(Filter.eq("orderId", hyOrder.getId()));
//											appFilter.add(Filter.eq("status", 4)); //筛选已退款状态
//											List<HyOrderApplication> applicationList=hyOrderApplicationService.findList(null,appFilter,null);
//											if(applicationList.size()>0){
//												appList.addAll(applicationList);
//											}
											/** add by wj  2019.06.28 计算现在扣点（也就是订单表中存储的扣点）
											 *  由于application需要筛选 在计调之前的所有审核完成的数据，因此这里筛选条件不对，用下面sql进行查询 **/
											xianzaikoudian = xianzaikoudian.add(hyOrder.getKoudianMoney());
										}																	
//										if(appList.size()>0){
//											/*将所有的退款相加的最终的退款*/		
//											for(HyOrderApplication hyOrderApplication:appList){
//												refundMoney=refundMoney.add(hyOrderApplication.getJiesuanMoney()).setScale(2, RoundingMode.HALF_UP);
//										    }
//										}
									}
									// 找到 在退团或者 消团申请之后的订单 application id 
									String sql = "select pro.id from ACT_HI_TASKINST rt ,"
												+ "(SELECT regulate.apply_time,application.* FROM hy_order_application application,hy_order o ,hy_regulate regulate "
												+ " where o.id = application.order_id and application.status = 4 and o.group_id = "+ hyGroup.getId()+" and regulate.group_id = o.group_id) pro "
												+ " where ((rt.PROC_DEF_ID_ LIKE 'storeTuiTuan%') or (rt.PROC_DEF_ID_ LIKE 'xiaotuan%')) "
												+ " AND rt.TASK_DEF_KEY_ = 'usertask4' AND PROC_INST_ID_ = pro.process_instance_id "
												+ " AND rt.END_TIME_ >= pro.apply_time ";
									List<Object[]> applicationlist = hyOrderApplicationService.statis(sql);
									for(Object object:applicationlist){
										BigInteger id1 = (BigInteger)object;
										Long id = id1.longValue();
										HyOrderApplication application = hyOrderApplicationService.find(id);
										if(hyGroup.getKoudianType().equals(DeductLine.tuanke)){
											/*总收入减去扣点,扣点为总收入乘以扣点百分比，保留两位小数,四舍五入*/
											koudian = koudian.add(application.getJiesuanMoney().multiply(hyOrderService.find(application.getOrderId()).getProportion()).multiply(BigDecimal.valueOf(0.01)));
										}
										/*如果是人头扣点,用总收入减去每个人扣点乘以报名人数*/
										else if(hyGroup.getKoudianType().equals(DeductLine.rentou)){
											String sql1 = "select sum(number_of_return) from hy_order_item where type = 1 and order_id = " + application.getOrderId();
											List<Object[]> list1 = hyOrderService.statis(sql1);	
											if (!list1.isEmpty() &&list1.size()!=0&& list1.get(0)!=null) {
												Object iObject = list1.get(0);
												BigDecimal people = new BigDecimal(iObject.toString());
												koudian=koudian.add(hyGroup.getPersonKoudian().multiply(people));
											}		
										}
										refundMoney=refundMoney.add(application.getJiesuanMoney()).setScale(2, RoundingMode.HALF_UP);
									}
									koudian = xianzaikoudian.add(koudian);
									settle.setKoudianMoney(koudian);
									payablesBranchsettleService.update(settle);
									money=money.subtract(refundMoney).subtract(koudian);
									/** end of add **/
									groupMap.put("money", money);
									/** 当前用户对本条数据的操作权限 */
								    if(creator.equals(admin)){
								    	if(co==CheckedOperation.view){
								    		groupMap.put("privilege", "view");
								    	}
								    	else{
								    		groupMap.put("privilege", "edit");
								    	}
								    }
								    else{
								    	if(co==CheckedOperation.edit){
								    		groupMap.put("privilege", "edit");
								    	}
								    	else{
								    		groupMap.put("privilege", "view");
								    	}
								    }
									list.add(groupMap);
								}
								Collections.sort(list, new Comparator<Map<String, Object>>() {
									@Override
									public int compare(Map<String, Object> o1, Map<String, Object> o2) {
										Date date1 = (Date) o1.get("startDate");
										Date date2 = (Date) o2.get("startDate");
										return date1.compareTo(date2); 
									}
								});
							    Collections.reverse(list);
							    Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);
							    json.setMsg("查询成功");
							    json.setSuccess(true);
							    json.setObj(page);
							}				
						}					
					}
				}
				else{
					List<HyGroup> groupList=hyGroupService.findList(null,groupFilter,null);
					if(groupList.size()==0){
						json.setMsg("查询成功");
					    json.setSuccess(true);
					    json.setObj(new Page<HyGroup>());
					}
					else{
						List<Filter> settleFilter=new ArrayList<>();
						settleFilter.add(Filter.in("hyGroup", groupList));
						settleFilter.add(Filter.eq("auditStatus",0));
						List<PayablesBranchsettle> settleList=payablesBranchsettleService.findList(null,settleFilter,null);
						if(settleList.size()==0){
							json.setMsg("查询成功");
						    json.setSuccess(true);
						    json.setObj(new Page<HyGroup>());
						}
						else{
							for(PayablesBranchsettle settle:settleList){
								HyGroup hyGroup=settle.getHyGroup();
								HyAdmin creator=hyGroup.getCreator();
								HashMap<String,Object> groupMap=new HashMap<String,Object>();
								groupMap.put("settleId", settle.getId());
								groupMap.put("productId", hyGroup.getLine().getPn());
								groupMap.put("startDate", hyGroup.getStartDay());
								groupMap.put("productName", hyGroup.getLine().getName());
								groupMap.put("supplierName", hyGroup.getLine().getHySupplier().getSupplierName());
								groupMap.put("creatorName", hyGroup.getCreator().getName());
								/*初始化金额等于0*/
								BigDecimal money=new BigDecimal(0);
								List<Filter> accountFilter=new ArrayList<>();
								accountFilter.add(Filter.eq("groupId", hyGroup));
	
								List<RegulategroupAccount> accountList=regulategroupAccountService.findList(null,accountFilter,null);
								if(accountList.size()>0){
									money=money.add(accountList.get(0).getAllIncome()); //总收入
									/*总收入-扣点-总支出+预付款*/
									/**计算公式修改,结算金额=总收入-扣点*/
//									money=money.subtract(accountList.get(0).getAllExpense());
								}
								// add by wj 2019.06.28 扣点计算方式不对
//								if(hyGroup.getKoudianType().equals(DeductLine.tuanke)){
//									BigDecimal div=new BigDecimal(100);
//									/*总收入减去扣点,扣点为总收入乘以扣点百分比，保留两位小数,四舍五入*/
//									money=money.subtract((money.multiply(hyGroup.getPercentageKoudian()).divide(div)).setScale(2, RoundingMode.HALF_UP));
//								}
//								/*如果是人头扣点,用总收入减去每个人扣点乘以报名人数*/
//								else if(hyGroup.getKoudianType().equals(DeductLine.rentou)){
//									money=money.subtract(hyGroup.getPersonKoudian().multiply(new BigDecimal(hyGroup.getSignupNumber())));
//								}
								
                                /**计算公式修改,不再计算预付款*/
//								List<Filter> balanceFilter=new ArrayList<>();
//								balanceFilter.add(Filter.eq("groupId", hyGroup.getId()));
//								balanceFilter.add(Filter.eq("payStatus", 1));								
//								List<BalanceDueApplyItem> balanceList=balanceDueApplyItemService.findList(null,balanceFilter,null);
//								BigDecimal prepay=new BigDecimal(0);							
//								if(balanceList.size()>0){
//									/*将所有的usePrePay相加，得到预付款prepay*/
//									for(BalanceDueApplyItem balanceDueApplyItem:balanceList){
//										prepay=prepay.add(balanceDueApplyItem.getUsePrePay());
//									}
//								}	
//								money=money.add(prepay);
								BigDecimal xianzaikoudian =BigDecimal.ZERO;
								BigDecimal koudian = BigDecimal.ZERO;
								BigDecimal refundMoney=new BigDecimal(0);//退款
								List<Filter> orderFilter=new ArrayList<>();
								orderFilter.add(Filter.eq("groupId",hyGroup.getId()));
								List<HyOrder> orderList=hyOrderService.findList(null,orderFilter,null);
								List<HyOrderApplication> appList=new ArrayList<HyOrderApplication>();
								/** add by wj 2019.06.28**/
								if(orderList.size()>0){
									for(HyOrder hyOrder:orderList){
										/** add by wj  2019.06.28 计算现在扣点（也就是订单表中存储的扣点）
										 *  由于application需要筛选 在计调之前的所有审核完成的数据，因此这里筛选条件不对，用下面sql进行查询 **/
										xianzaikoudian = xianzaikoudian.add(hyOrder.getKoudianMoney());
									}												
								}
								// 找到 在退团或者 消团申请之后的订单 application id 
								String sql = "select pro.id from ACT_HI_TASKINST rt ,"
											+ "(SELECT regulate.apply_time,application.* FROM hy_order_application application,hy_order o ,hy_regulate regulate "
											+ " where o.id = application.order_id and application.status = 4 and o.group_id = "+ hyGroup.getId()+" and regulate.group_id = o.group_id) pro "
											+ " where ((rt.PROC_DEF_ID_ LIKE 'storeTuiTuan%') or (rt.PROC_DEF_ID_ LIKE 'xiaotuan%')) "
											+ " AND rt.TASK_DEF_KEY_ = 'usertask4' AND PROC_INST_ID_ = pro.process_instance_id "
											+ " AND rt.END_TIME_ >= pro.apply_time ";
								List<Object[]> applicationlist = hyOrderApplicationService.statis(sql);
								for(Object object:applicationlist){
									BigInteger id1 = (BigInteger)object;
									Long id = id1.longValue();
									HyOrderApplication application = hyOrderApplicationService.find(id);
									if(hyGroup.getKoudianType().equals(DeductLine.tuanke)){
										/*总收入减去扣点,扣点为总收入乘以扣点百分比，保留两位小数,四舍五入*/
										koudian = koudian.add(application.getJiesuanMoney().multiply(hyOrderService.find(application.getOrderId()).getProportion()).multiply(BigDecimal.valueOf(0.01)));
									}
									/*如果是人头扣点,用总收入减去每个人扣点乘以报名人数*/
									else if(hyGroup.getKoudianType().equals(DeductLine.rentou)){
										String sql1 = "select sum(number_of_return) from hy_order_item where type = 1 and order_id = " + application.getOrderId();
										List<Object[]> list1 = hyOrderService.statis(sql1);	
										if (!list1.isEmpty() &&list1.size()!=0&& list1.get(0)!=null) {
											Object iObject = list1.get(0);
											BigDecimal people = new BigDecimal(iObject.toString());
											koudian=koudian.add(hyGroup.getPersonKoudian().multiply(people));
										}		
									}
									refundMoney=refundMoney.add(application.getJiesuanMoney()).setScale(2, RoundingMode.HALF_UP);
								}
								koudian = xianzaikoudian.add(koudian);
								money=money.subtract(refundMoney).subtract(koudian);
								settle.setKoudianMoney(koudian);
								payablesBranchsettleService.update(settle);
//								if(orderList.size()>0){
//									for(HyOrder hyOrder:orderList){
//										List<Filter> appFilter=new ArrayList<Filter>();
//										appFilter.add(Filter.eq("orderId", hyOrder.getId()));
//										appFilter.add(Filter.eq("status", 4)); //筛选已退款状态
//										List<HyOrderApplication> applicationList=hyOrderApplicationService.findList(null,appFilter,null);
//										if(applicationList.size()>0){
//											appList.addAll(applicationList);
//										}									
//									}																	
//									if(appList.size()>0){
//										/*将所有的退款相加的最终的退款*/		
//										for(HyOrderApplication hyOrderApplication:appList){
//											refundMoney=refundMoney.add(hyOrderApplication.getJiesuanMoney()).setScale(2, RoundingMode.HALF_UP);
//									    }
//									}
//								}
//								money=money.subtract(refundMoney);
								groupMap.put("money", money);
								/** 当前用户对本条数据的操作权限 */
							    if(creator.equals(admin)){
							    	if(co==CheckedOperation.view){
							    		groupMap.put("privilege", "view");
							    	}
							    	else{
							    		groupMap.put("privilege", "edit");
							    	}
							    }
							    else{
							    	if(co==CheckedOperation.edit){
							    		groupMap.put("privilege", "edit");
							    	}
							    	else{
							    		groupMap.put("privilege", "view");
							    	}
							    }
								list.add(groupMap);
							}
							Collections.sort(list, new Comparator<Map<String, Object>>() {
								@Override
								public int compare(Map<String, Object> o1, Map<String, Object> o2) {
									Date date1 = (Date) o1.get("startDate");
									Date date2 = (Date) o2.get("startDate");
									return date1.compareTo(date2); 
								}
							});
						    Collections.reverse(list);
						    Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);
						    json.setMsg("查询成功");
						    json.setSuccess(true);
						    json.setObj(page);
						}				
					}					
				
				}
			}
			/*if(productId!=null&&supplierName!=null)*/
			else{
				List<Filter> lineFilter=new ArrayList<Filter>();
				if(productId!=null && !productId.equals("")){
					lineFilter.add(Filter.eq("pn", productId));
				}
				if(supplierName!=null && !supplierName.equals("")){
					List<Filter> supplierFilter=new ArrayList<Filter>();
					supplierFilter.add(Filter.like("supplierName", supplierName));
					List<HySupplier> hySupplierList=hySupplierService.findList(null,supplierFilter,null);
					if(hySupplierList.size()==0){
						json.setSuccess(true);
						json.setMsg("查询成功");
						json.setObj(new Page<HySupplier>());
					}
					else{
						lineFilter.add(Filter.in("hySupplier",hySupplierList));
					}
			    }
				List<HyLine> lineList=hyLineService.findList(null,lineFilter,null);
				if(lineList.size()==0){
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new Page<HyLine>());
				}
				else{
					groupFilter.add(Filter.in("line", lineList));
					if(creatorName!=null&&!creatorName.equals("")){
						List<Filter> filter=new ArrayList<Filter>();
						filter.add(Filter.like("name",creatorName));
						List<HyAdmin> adminList=hyAdminService.findList(null,filter,null);
						if(adminList.size()==0){
							json.setMsg("查询成功");
						    json.setSuccess(true);
						    json.setObj(new Page<HyGroup>());
						}
						else{
							groupFilter.add(Filter.in("creator", adminList));
							List<HyGroup> groupList=hyGroupService.findList(null,groupFilter,null);
							if(groupList.size()==0){
								json.setMsg("查询成功");
							    json.setSuccess(true);
							    json.setObj(new Page<HyGroup>());
							}
							else{
								List<Filter> settleFilter=new ArrayList<>();
								settleFilter.add(Filter.in("hyGroup", groupList));
								settleFilter.add(Filter.eq("auditStatus",0));
								List<PayablesBranchsettle> settleList=payablesBranchsettleService.findList(null,settleFilter,null);
								if(settleList.isEmpty()){
									json.setMsg("查询成功");
								    json.setSuccess(true);
								    json.setObj(new Page<HyGroup>());
								}
								else{
									for(PayablesBranchsettle settle:settleList){
										HyGroup hyGroup=settle.getHyGroup();
										HyAdmin creator=hyGroup.getCreator();
										HashMap<String,Object> groupMap=new HashMap<String,Object>();
										groupMap.put("settleId", settle.getId());
										groupMap.put("productId", hyGroup.getLine().getPn());
										groupMap.put("startDate", hyGroup.getStartDay());
										groupMap.put("productName", hyGroup.getLine().getName());
										groupMap.put("supplierName", hyGroup.getLine().getHySupplier().getSupplierName());
										groupMap.put("creatorName", hyGroup.getCreator().getName());
										/*初始化金额等于0*/
										BigDecimal money=new BigDecimal(0);
										List<Filter> accountFilter=new ArrayList<>();
										accountFilter.add(Filter.eq("groupId", hyGroup));
			
										List<RegulategroupAccount> accountList=regulategroupAccountService.findList(null,accountFilter,null);
										if(accountList.size()>0){
											money=money.add(accountList.get(0).getAllIncome()); //总收入
											/*总收入-扣点-总支出+预付款*/
											/**计算公式修改,结算金额=总收入-扣点,不再计算总支出*/
//											money=money.subtract(accountList.get(0).getAllExpense());
										}
//										if(hyGroup.getKoudianType().equals(DeductLine.tuanke)){
//											BigDecimal div=new BigDecimal(100);
//											/*总收入减去扣点,扣点为总收入乘以扣点百分比，保留两位小数,四舍五入*/
//											money=money.subtract((money.multiply(hyGroup.getPercentageKoudian()).divide(div)).setScale(2, RoundingMode.HALF_UP));
//										}
//										/*如果是人头扣点,用总收入减去每个人扣点乘以报名人数*/
//										else if(hyGroup.getKoudianType().equals(DeductLine.rentou)){
//											money=money.subtract(hyGroup.getPersonKoudian().multiply(new BigDecimal(hyGroup.getSignupNumber())));
//										}
										/**计算公式修改,不再计算预付款,20181006*/
//										List<Filter> balanceFilter=new ArrayList<>();
//										balanceFilter.add(Filter.eq("groupId", hyGroup.getId()));
//										balanceFilter.add(Filter.eq("payStatus", 1));								
//										List<BalanceDueApplyItem> balanceList=balanceDueApplyItemService.findList(null,balanceFilter,null);
//										BigDecimal prepay=new BigDecimal(0);							
//										if(balanceList.size()>0){
//											/*将所有的usePrePay相加，得到预付款prepay*/
//											for(BalanceDueApplyItem balanceDueApplyItem:balanceList){
//												prepay=prepay.add(balanceDueApplyItem.getUsePrePay());
//											}
//										}	
//										money=money.add(prepay);
//										BigDecimal refundMoney=new BigDecimal(0);//退款
//										List<Filter> orderFilter=new ArrayList<>();
//										orderFilter.add(Filter.eq("groupId",hyGroup.getId()));
//										List<HyOrder> orderList=hyOrderService.findList(null,orderFilter,null);
//										List<HyOrderApplication> appList=new ArrayList<HyOrderApplication>();
//										if(orderList.size()>0){
//											for(HyOrder hyOrder:orderList){
//												List<Filter> appFilter=new ArrayList<Filter>();
//												appFilter.add(Filter.in("orderId", hyOrder.getId()));
//												appFilter.add(Filter.eq("status", 4)); //筛选已退款状态
//												List<HyOrderApplication> applicationList=hyOrderApplicationService.findList(null,appFilter,null);
//												if(applicationList.size()>0){
//													appList.addAll(applicationList);
//												}									
//											}																	
//											if(appList.size()>0){
//												/*将所有的退款相加的最终的退款*/		
//												for(HyOrderApplication hyOrderApplication:appList){
//													refundMoney=refundMoney.add(hyOrderApplication.getJiesuanMoney()).setScale(2, RoundingMode.HALF_UP);
//											    }
//											}
//										}
//										money=money.subtract(refundMoney);
										BigDecimal xianzaikoudian =BigDecimal.ZERO;
										BigDecimal koudian = BigDecimal.ZERO;
										BigDecimal refundMoney=new BigDecimal(0);//退款
										List<Filter> orderFilter=new ArrayList<>();
										orderFilter.add(Filter.eq("groupId",hyGroup.getId()));
										List<HyOrder> orderList=hyOrderService.findList(null,orderFilter,null);
//										List<HyOrderApplication> appList=new ArrayList<HyOrderApplication>();
										/** add by wj 2019.06.28**/
										if(orderList.size()>0){
											for(HyOrder hyOrder:orderList){
												/** add by wj  2019.06.28 计算现在扣点（也就是订单表中存储的扣点）
												 *  由于application需要筛选 在计调之前的所有审核完成的数据，因此这里筛选条件不对，用下面sql进行查询 **/
												xianzaikoudian = xianzaikoudian.add(hyOrder.getKoudianMoney());
											}												
										}
										// 找到 在退团或者 消团申请之后的订单 application id 
										String sql = "select pro.id from ACT_HI_TASKINST rt ,"
													+ "(SELECT regulate.apply_time,application.* FROM hy_order_application application,hy_order o ,hy_regulate regulate "
													+ " where o.id = application.order_id and application.status = 4 and o.group_id = "+ hyGroup.getId()+" and regulate.group_id = o.group_id) pro "
													+ " where ((rt.PROC_DEF_ID_ LIKE 'storeTuiTuan%') or (rt.PROC_DEF_ID_ LIKE 'xiaotuan%')) "
													+ " AND rt.TASK_DEF_KEY_ = 'usertask4' AND PROC_INST_ID_ = pro.process_instance_id "
													+ " AND rt.END_TIME_ >= pro.apply_time ";
										List<Object[]> applicationlist = hyOrderApplicationService.statis(sql);
										for(Object object:applicationlist){
											BigInteger id1 = (BigInteger)object;
											Long id = id1.longValue();
											HyOrderApplication application = hyOrderApplicationService.find(id);
											if(hyGroup.getKoudianType().equals(DeductLine.tuanke)){
												/*总收入减去扣点,扣点为总收入乘以扣点百分比，保留两位小数,四舍五入*/
												koudian = koudian.add(application.getJiesuanMoney().multiply(hyOrderService.find(application.getOrderId()).getProportion()).multiply(BigDecimal.valueOf(0.01)));
											}
											/*如果是人头扣点,用总收入减去每个人扣点乘以报名人数*/
											else if(hyGroup.getKoudianType().equals(DeductLine.rentou)){
												String sql1 = "select sum(number_of_return) from hy_order_item where type = 1 and order_id = " + application.getOrderId();
												List<Object[]> list1 = hyOrderService.statis(sql1);	
												if (!list1.isEmpty() &&list1.size()!=0&& list1.get(0)!=null) {
													Object iObject = list1.get(0);
													BigDecimal people = new BigDecimal(iObject.toString());
													koudian=koudian.add(hyGroup.getPersonKoudian().multiply(people));
												}		
											}
											refundMoney=refundMoney.add(application.getJiesuanMoney()).setScale(2, RoundingMode.HALF_UP);
										}
										koudian = xianzaikoudian.add(koudian);
										money=money.subtract(refundMoney).subtract(koudian);
										settle.setKoudianMoney(koudian);
										payablesBranchsettleService.update(settle);
										/** end of add **/
										groupMap.put("money", money);
										/** 当前用户对本条数据的操作权限 */
									    if(creator.equals(admin)){
									    	if(co==CheckedOperation.view){
									    		groupMap.put("privilege", "view");
									    	}
									    	else{
									    		groupMap.put("privilege", "edit");
									    	}
									    }
									    else{
									    	if(co==CheckedOperation.edit){
									    		groupMap.put("privilege", "edit");
									    	}
									    	else{
									    		groupMap.put("privilege", "view");
									    	}
									    }
										list.add(groupMap);
									}
									Collections.sort(list, new Comparator<Map<String, Object>>() {
										@Override
										public int compare(Map<String, Object> o1, Map<String, Object> o2) {
											Date date1 = (Date) o1.get("startDate");
											Date date2 = (Date) o2.get("startDate");
											return date1.compareTo(date2); 
										}
									});
								    Collections.reverse(list);
								    Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);
								    json.setMsg("查询成功");
								    json.setSuccess(true);
								    json.setObj(page);
								}				
							}					
						}
					}
					else{
						List<HyGroup> groupList=hyGroupService.findList(null,groupFilter,null);
						if(groupList.size()==0){
							json.setMsg("查询成功");
						    json.setSuccess(true);
						    json.setObj(new Page<HyGroup>());
						}
						else{
							List<Filter> settleFilter=new ArrayList<>();
							settleFilter.add(Filter.in("hyGroup", groupList));
							settleFilter.add(Filter.eq("auditStatus",0));
							List<PayablesBranchsettle> settleList=payablesBranchsettleService.findList(null,settleFilter,null);
							if(settleList.isEmpty()){
								json.setMsg("查询成功");
							    json.setSuccess(true);
							    json.setObj(new Page<HyGroup>());
							}
							else{
								for(PayablesBranchsettle settle:settleList){
									HyGroup hyGroup=settle.getHyGroup();
									HyAdmin creator=hyGroup.getCreator();
									HashMap<String,Object> groupMap=new HashMap<String,Object>();
									groupMap.put("settleId", settle.getId());
									groupMap.put("productId", hyGroup.getLine().getPn());
									groupMap.put("startDate", hyGroup.getStartDay());
									groupMap.put("productName", hyGroup.getLine().getName());
									groupMap.put("supplierName", hyGroup.getLine().getHySupplier().getSupplierName());
									groupMap.put("creatorName", hyGroup.getCreator().getName());
									/*初始化金额等于0*/
									BigDecimal money=new BigDecimal(0);
									List<Filter> accountFilter=new ArrayList<>();
									accountFilter.add(Filter.eq("groupId", hyGroup));
		
									List<RegulategroupAccount> accountList=regulategroupAccountService.findList(null,accountFilter,null);
									if(accountList.size()>0){
										money=money.add(accountList.get(0).getAllIncome()); //总收入
										/*总收入-扣点-总支出+预付款*/
										/**计算公式修改,结算额度=总收入-扣点,不再计算总支出,20181006*/
//										money=money.subtract(accountList.get(0).getAllExpense());
									}
//									if(hyGroup.getKoudianType().equals(DeductLine.tuanke)){
//										BigDecimal div=new BigDecimal(100);
//										/*总收入减去扣点,扣点为总收入乘以扣点百分比，保留两位小数,四舍五入*/
//										money=money.subtract((money.multiply(hyGroup.getPercentageKoudian()).divide(div)).setScale(2, RoundingMode.HALF_UP));
//									}
//									/*如果是人头扣点,用总收入减去每个人扣点乘以报名人数*/
//									else if(hyGroup.getKoudianType().equals(DeductLine.rentou)){
//										money=money.subtract(hyGroup.getPersonKoudian().multiply(new BigDecimal(hyGroup.getSignupNumber())));
//									}
									/**计算公式修改,不再计算预付款,20181006*/
//									List<Filter> balanceFilter=new ArrayList<>();
//									balanceFilter.add(Filter.eq("groupId", hyGroup.getId()));
//									balanceFilter.add(Filter.eq("payStatus", 1));								
//									List<BalanceDueApplyItem> balanceList=balanceDueApplyItemService.findList(null,balanceFilter,null);
//									BigDecimal prepay=new BigDecimal(0);							
//									if(balanceList.size()>0){
//										/*将所有的usePrePay相加，得到预付款prepay*/
//										for(BalanceDueApplyItem balanceDueApplyItem:balanceList){
//											prepay=prepay.add(balanceDueApplyItem.getUsePrePay());
//										}
//									}	
//									money=money.add(prepay);
//									BigDecimal refundMoney=new BigDecimal(0);//退款
//									List<Filter> orderFilter=new ArrayList<>();
//									orderFilter.add(Filter.eq("groupId",hyGroup.getId()));
//									List<HyOrder> orderList=hyOrderService.findList(null,orderFilter,null);
//									List<HyOrderApplication> appList=new ArrayList<HyOrderApplication>();
//									if(orderList.size()>0){
//										for(HyOrder hyOrder:orderList){
//											List<Filter> appFilter=new ArrayList<Filter>();
//											appFilter.add(Filter.eq("orderId", hyOrder.getId()));
//											appFilter.add(Filter.eq("status", 4)); //筛选已退款状态
//											List<HyOrderApplication> applicationList=hyOrderApplicationService.findList(null,appFilter,null);
//											if(applicationList.size()>0){
//												appList.addAll(applicationList);
//											}									
//										}																	
//										if(appList.size()>0){
//											/*将所有的退款相加的最终的退款*/		
//											for(HyOrderApplication hyOrderApplication:appList){
//												refundMoney=refundMoney.add(hyOrderApplication.getJiesuanMoney()).setScale(2, RoundingMode.HALF_UP);
//										    }
//										}
//									}
//									money=money.subtract(refundMoney);
									/** add by wj 2019.06.28 **/
									BigDecimal xianzaikoudian =BigDecimal.ZERO;
									BigDecimal koudian = BigDecimal.ZERO;
									BigDecimal refundMoney=new BigDecimal(0);//退款
									List<Filter> orderFilter=new ArrayList<>();
									orderFilter.add(Filter.eq("groupId",hyGroup.getId()));
									List<HyOrder> orderList=hyOrderService.findList(null,orderFilter,null);
									List<HyOrderApplication> appList=new ArrayList<HyOrderApplication>();
									/** add by wj 2019.06.28**/
									if(orderList.size()>0){
										for(HyOrder hyOrder:orderList){
											/** add by wj  2019.06.28 计算现在扣点（也就是订单表中存储的扣点）
											 *  由于application需要筛选 在计调之前的所有审核完成的数据，因此这里筛选条件不对，用下面sql进行查询 **/
											xianzaikoudian = xianzaikoudian.add(hyOrder.getKoudianMoney());
										}												
									}
									// 找到 在退团或者 消团申请之后的订单 application id 
									String sql = "select pro.id from ACT_HI_TASKINST rt ,"
												+ "(SELECT regulate.apply_time,application.* FROM hy_order_application application,hy_order o ,hy_regulate regulate "
												+ " where o.id = application.order_id and application.status = 4 and o.group_id = "+ hyGroup.getId()+" and regulate.group_id = o.group_id) pro "
												+ " where ((rt.PROC_DEF_ID_ LIKE 'storeTuiTuan%') or (rt.PROC_DEF_ID_ LIKE 'xiaotuan%')) "
												+ " AND rt.TASK_DEF_KEY_ = 'usertask4' AND PROC_INST_ID_ = pro.process_instance_id "
												+ " AND rt.END_TIME_ >= pro.apply_time ";
									List<Object[]> applicationlist = hyOrderApplicationService.statis(sql);
									for(Object object:applicationlist){
										BigInteger id1 = (BigInteger)object;
										Long id = id1.longValue();
										HyOrderApplication application = hyOrderApplicationService.find(id);
										if(hyGroup.getKoudianType().equals(DeductLine.tuanke)){
											/*总收入减去扣点,扣点为总收入乘以扣点百分比，保留两位小数,四舍五入*/
											koudian = koudian.add(application.getJiesuanMoney().multiply(hyOrderService.find(application.getOrderId()).getProportion()).multiply(BigDecimal.valueOf(0.01)));
										}
										/*如果是人头扣点,用总收入减去每个人扣点乘以报名人数*/
										else if(hyGroup.getKoudianType().equals(DeductLine.rentou)){
											String sql1 = "select sum(number_of_return) from hy_order_item where type = 1 and order_id = " + application.getOrderId();
											List<Object[]> list1 = hyOrderService.statis(sql1);	
											if (!list1.isEmpty() &&list1.size()!=0&& list1.get(0)!=null) {
												Object iObject = list1.get(0);
												BigDecimal people = new BigDecimal(iObject.toString());
												koudian=koudian.add(hyGroup.getPersonKoudian().multiply(people));
											}		
										}
										refundMoney=refundMoney.add(application.getJiesuanMoney()).setScale(2, RoundingMode.HALF_UP);
									}
									koudian = xianzaikoudian.add(koudian);
									money=money.subtract(refundMoney).subtract(koudian);
									settle.setKoudianMoney(koudian);
									payablesBranchsettleService.update(settle);
									/** end of add **/
									groupMap.put("money", money);
									/** 当前用户对本条数据的操作权限 */
								    if(creator.equals(admin)){
								    	if(co==CheckedOperation.view){
								    		groupMap.put("privilege", "view");
								    	}
								    	else{
								    		groupMap.put("privilege", "edit");
								    	}
								    }
								    else{
								    	if(co==CheckedOperation.edit){
								    		groupMap.put("privilege", "edit");
								    	}
								    	else{
								    		groupMap.put("privilege", "view");
								    	}
								    }
									list.add(groupMap);
								}
								Collections.sort(list, new Comparator<Map<String, Object>>() {
									@Override
									public int compare(Map<String, Object> o1, Map<String, Object> o2) {
										Date date1 = (Date) o1.get("startDate");
										Date date2 = (Date) o2.get("startDate");
										return date1.compareTo(date2); 
									}
								});
							    Collections.reverse(list);
							    Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);
							    json.setMsg("查询成功");
							    json.setSuccess(true);
							    json.setObj(page);
							}				
						}					
					}
				}
			}				
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="detail/view")
	@ResponseBody
	public Json detail(Long settleId)
	{
		Json json=new Json();
		try{
			PayablesBranchsettle payablesBranchsettle=payablesBranchsettleService.find(settleId);
			HyGroup hyGroup=payablesBranchsettle.getHyGroup();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();	
			Map<String,Object> map=new HashMap<String,Object>();
			/*供应商信息*/
			map.put("supplierName", hyGroup.getLine().getHySupplier().getSupplierName());
			map.put("contactName", hyGroup.getLine().getHySupplier().getOperator().getName()); //供应商联系人
			map.put("bankName", hyGroup.getLine().getContract().getBankList().getBankName());
			map.put("accountName", hyGroup.getLine().getContract().getBankList().getAccountName());
			map.put("bankAccount", hyGroup.getLine().getContract().getBankList().getBankAccount());
			/*组团信息*/
			List<Map<String, Object>> grouplist = new ArrayList<Map<String, Object>>();	
			Map<String,Object> groupmap=new HashMap<String,Object>();
			groupmap.put("productId", hyGroup.getLine().getPn());
			groupmap.put("LineName", hyGroup.getLine().getName());
			groupmap.put("startDate", hyGroup.getStartDay());
			groupmap.put("creatorName", hyGroup.getCreator().getName());
			
			/*初始化金额等于0*/
			BigDecimal money=new BigDecimal(0);
			BigDecimal allIncome=new BigDecimal(0);
			List<Filter> accountFilter=new ArrayList<>();
			accountFilter.add(Filter.eq("groupId", hyGroup));
			List<RegulategroupAccount> accountList=regulategroupAccountService.findList(null,accountFilter,null);
			if(accountList.size()>0){
				allIncome=allIncome.add(accountList.get(0).getAllIncome()); //总收入
			}
			groupmap.put("allIncome", allIncome);
//			BigDecimal allExpense=new BigDecimal(0);
//			if(accountList.size()>0){
//				/*总收入-扣点-总支出+预付款*/
//				allExpense=allExpense.add(accountList.get(0).getAllExpense());
//			}
			/**计算公式修改,不再传总支出,20181006*/
//			groupmap.put("allExpense", allExpense);
			groupmap.put("signupNumber", hyGroup.getSignupNumber());
			/** add by wj 2019.06.28 **/
			BigDecimal koudian =payablesBranchsettle.getKoudianMoney();
			/** end of add **/
//			BigDecimal koudian=new BigDecimal(0);
//			if(hyGroup.getKoudianType().equals(DeductLine.tuanke)){
//				BigDecimal div=new BigDecimal(100);
//				/*扣点为总收入乘以扣点的百分数*/
//				koudian=koudian.add(allIncome.multiply(hyGroup.getPercentageKoudian()).divide(div).setScale(2, RoundingMode.HALF_UP));
//			}
//			/*如果是人头扣点,扣点等于每个人扣点乘以报名人数*/
//			else if(hyGroup.getKoudianType().equals(DeductLine.rentou)){
//				koudian=koudian.add(hyGroup.getPersonKoudian().multiply(new BigDecimal(hyGroup.getSignupNumber())));
//			}
			groupmap.put("koudian", koudian);
			/**计算公式修改,不再计算预付款,也不再传*/
//			List<Filter> balanceFilter=new ArrayList<>();
//			balanceFilter.add(Filter.eq("groupId", hyGroup.getId()));
//			balanceFilter.add(Filter.eq("payStatus", 1)); //付款状态为已付款								
//			List<BalanceDueApplyItem> balanceList=balanceDueApplyItemService.findList(null,balanceFilter,null);
//			BigDecimal prepay=new BigDecimal(0);							
//			if(balanceList.size()>0){
//				/*将所有的usePrePay相加，得到预付款prepay*/
//				for(BalanceDueApplyItem balanceDueApplyItem:balanceList){
//					prepay=prepay.add(balanceDueApplyItem.getUsePrePay());
//				}
//			}	
//			groupmap.put("prepay", prepay);
			BigDecimal refundMoney=new BigDecimal(0);//退款
			List<Filter> orderFilter=new ArrayList<>();
			orderFilter.add(Filter.eq("groupId",hyGroup.getId()));
			List<HyOrder> orderList=hyOrderService.findList(null,orderFilter,null);
			List<HyOrderApplication> appList=new ArrayList<HyOrderApplication>();
			/*退款信息*/
//			if(orderList.size()>0){
//				for(HyOrder hyOrder:orderList){
//					List<Filter> appFilter=new ArrayList<Filter>();
//					appFilter.add(Filter.eq("orderId", hyOrder.getId()));
//					appFilter.add(Filter.eq("status", 4)); //筛选已退款状态
//					List<HyOrderApplication> applicationList=hyOrderApplicationService.findList(null,appFilter,null);
//					if(applicationList.size()>0){
//						appList.addAll(applicationList);		
//					}									
//				}																	
//				if(appList.size()>0){
//					/*将所有的退款相加的最终的退款*/		
//					for(HyOrderApplication hyOrderApplication:appList){
//						refundMoney=refundMoney.add(hyOrderApplication.getJiesuanMoney()).setScale(2, RoundingMode.HALF_UP);
//						obj.put("orderNumber", hyOrderService.find(hyOrderApplication.getOrderId()).getOrderNumber()); //点单编号
//				        obj.put("productId", hyGroup.getLine().getPn());
//				        obj.put("lineName", hyGroup.getLine().getName());
//				        obj.put("startDate", hyGroup.getStartDay());
//				        obj.put("refundDate", hyOrderApplication.getCreatetime()); //退款日期
//			            /*游客联系人*/
//				        obj.put("contact", hyOrderService.find(hyOrderApplication.getOrderId()).getContact());
//					    obj.put("refundReq", hyOrderApplication.getView());
//					    obj.put("refundMoney", hyOrderApplication.getJiesuanMoney());
//					    list.add(obj);
//					}
//				}
//			}
			/*退款信息*/
			String sql = "select pro.id from ACT_HI_TASKINST rt ,"
					+ "(SELECT regulate.apply_time,application.* FROM hy_order_application application,hy_order o ,hy_regulate regulate "
					+ " where o.id = application.order_id and application.status = 4 and o.group_id = "
					+ hyGroup.getId() + " and regulate.group_id = o.group_id) pro "
					+ " where ((rt.PROC_DEF_ID_ LIKE 'storeTuiTuan%') or (rt.PROC_DEF_ID_ LIKE 'xiaotuan%')) "
					+ " AND rt.TASK_DEF_KEY_ = 'usertask4' AND PROC_INST_ID_ = pro.process_instance_id "
					+ " AND rt.END_TIME_ < pro.apply_time ";
			List<Object[]> applicationlist = hyOrderApplicationService.statis(sql);
			for (Object object : applicationlist) {
				HashMap<String,Object> obj=new HashMap<String,Object>();
				BigInteger id1 = (BigInteger) object;
				Long id = id1.longValue();
				HyOrderApplication hyOrderApplication = hyOrderApplicationService.find(id);
				refundMoney=refundMoney.add(hyOrderApplication.getJiesuanMoney()).setScale(2, RoundingMode.HALF_UP);
				obj.put("orderNumber", hyOrderService.find(hyOrderApplication.getOrderId()).getOrderNumber()); //点单编号
		        obj.put("productId", hyGroup.getLine().getPn());
		        obj.put("lineName", hyGroup.getLine().getName());
		        obj.put("startDate", hyGroup.getStartDay());
		        obj.put("refundDate", hyOrderApplication.getCreatetime()); //退款日期
	            /*游客联系人*/
		        obj.put("contact", hyOrderService.find(hyOrderApplication.getOrderId()).getContact());
			    obj.put("refundReq", hyOrderApplication.getView());
			    obj.put("refundMoney", hyOrderApplication.getJiesuanMoney());
			    list.add(obj);
			}
			/**计算公式修改,结算金额=总收入-扣点-退款,20181006*/
			money=money.add(allIncome).subtract(koudian).subtract(refundMoney);
//			money=money.add(allIncome).subtract(koudian).subtract(allExpense).add(prepay).subtract(refundMoney);
			groupmap.put("money", money);
			grouplist.add(groupmap);
			map.put("groupList", grouplist);
			/*退款相关信息*/
			map.put("refundList", list);
			map.put("subTotal", refundMoney);
			payablesBranchsettle.setShifuMoney(money);
			BigDecimal realMoney=new BigDecimal(0);
			/**计算公式修改,结算金额=总收入-扣点-退款,20181006*/
			payablesBranchsettle.setRealMoney(realMoney.add(allIncome).subtract(koudian));
			/** add by wj 2019.06.27**/
			payablesBranchsettle.setKoudianMoney(koudian);
			/** end of add **/
//			payablesBranchsettle.setRealMoney(realMoney.add(allIncome).subtract(koudian).subtract(allExpense).add(prepay));
			payablesBranchsettleService.update(payablesBranchsettle);
			json.setMsg("查询列表成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="payApply/submit")
	@ResponseBody
	public Json submit(Long settleId,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			PayablesBranchsettle payablesBranchsettle=payablesBranchsettleService.find(settleId);
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			ProcessInstance pi  = runtimeService.startProcessInstanceByKey("branchsettleProcess");
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			HashMap<String, Object> map = new HashMap<>();
			payablesBranchsettle.setApplyName(admin);
			payablesBranchsettle.setApplyTime(new Date());
			payablesBranchsettle.setProcessInstanceId(pi.getProcessInstanceId());
			payablesBranchsettle.setAuditStatus(1); //提交申请，审核中
			payablesBranchsettle.setApplySource(true); //首次提交申请
			List<Filter> filterList = new ArrayList<>();
			filterList.add(Filter.eq("eduleixing", Eduleixing.branchsettleLimit));
			List<CommonShenheedu> edu = commonEdushenheService.findList(null, filterList, null);
			BigDecimal money = edu.get(0).getMoney();
			if(payablesBranchsettle.getShifuMoney().doubleValue()>money.doubleValue()){
				map.put("money", "more");
				payablesBranchsettle.setStep(1);//待市场部副总限额审核
			}
			else{
				map.put("money", "less");
				payablesBranchsettle.setStep(2); //待财务部审核
			}
			payablesBranchsettle.setIsModify(false); //首次提交，没有金额调整
			/*打款单号自动产生*/
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("type", SequenceTypeEnum.branchSettlement));
			List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
			CommonSequence c = ss.get(0);
			Long value = c.getValue() + 1;
			c.setValue(value);
			commonSequenceService.update(c);
			produc=dateStr + "-" + String.format("%04d", value);
			payablesBranchsettle.setPayNumber(produc);
			payablesBranchsettleService.update(payablesBranchsettle);
			// 完成分公司团结算申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1"); 
			taskService.complete(task.getId(),map);
			json.setMsg("提交申请成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="settlementList/view")
	@ResponseBody
	public Json settlementView(Pageable pageable,Integer auditStatus,String supplierName,String applyName,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try{
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();		
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co=(CheckedOperation) request.getAttribute("co");
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			List<Filter> groupFilter=new ArrayList<Filter>();
			groupFilter.add(Filter.in("creator",hyAdmins));
			if(supplierName==null && applyName==null){
				List<HyGroup> groupList=hyGroupService.findList(null,groupFilter,null);
				if(groupList.size()==0){
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new Page<HyGroup>());
				}
				else{
					List<Filter> settleFilter=new ArrayList<Filter>();
					settleFilter.add(Filter.in("hyGroup", groupList));
					if(auditStatus != null){
						settleFilter.add(Filter.eq("auditStatus", auditStatus));
					}
					List<PayablesBranchsettle> settleList=payablesBranchsettleService.findList(null,settleFilter,null);
					if(settleList.isEmpty()){
						json.setMsg("查询成功");
					    json.setSuccess(true);
					    json.setObj(new Page<HyGroup>());
					}
					else{
						for(PayablesBranchsettle settle:settleList){
							HyGroup hyGroup=settle.getHyGroup();
							HashMap<String,Object> groupMap=new HashMap<String,Object>();
							HyAdmin creator=hyGroup.getCreator();
							groupMap.put("settleId", settle.getId());
							groupMap.put("payNumber", settle.getPayNumber());
							groupMap.put("supplierName", hyGroup.getLine().getHySupplier().getSupplierName());
							groupMap.put("contactName", hyGroup.getLine().getHySupplier().getOperator().getName()); //联系人
							groupMap.put("money", settle.getShifuMoney());
							groupMap.put("applyName", settle.getApplyName().getName()); //申请人
							groupMap.put("createDate", settle.getApplyTime());
							groupMap.put("status", settle.getAuditStatus());
							/** 当前用户对本条数据的操作权限 */
						    if(creator.equals(admin)){
						    	if(co==CheckedOperation.view){
						    		groupMap.put("privilege", "view");
						    	}
						    	else{
						    		groupMap.put("privilege", "edit");
						    	}
						    }
						    else{
						    	if(co==CheckedOperation.edit){
						    		groupMap.put("privilege", "edit");
						    	}
						    	else{
						    		groupMap.put("privilege", "view");
						    	}
						    }
							list.add(groupMap);
						}
						Collections.sort(list, new Comparator<Map<String, Object>>() {
							@Override
							public int compare(Map<String, Object> o1, Map<String, Object> o2) {
								Date date1 = (Date) o1.get("createDate");
								Date date2 = (Date) o2.get("createDate");
								return date1.compareTo(date2); 
							}
						});
					    Collections.reverse(list);
					    Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);
					    json.setMsg("查询成功");
					    json.setSuccess(true);
					    json.setObj(page);
				    }	
				}
			}
			
			else if(supplierName!=null && applyName==null){
				List<Filter> supplierFilter=new ArrayList<Filter>();
				supplierFilter.add(Filter.like("supplierName", supplierName));
				List<HySupplier> hySupplierList=hySupplierService.findList(null,supplierFilter,null);
				if(hySupplierList.size()==0){
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(new Page<HySupplier>());
			    }
				else{
					List<Filter> lineFilter=new ArrayList<>();
					lineFilter.add(Filter.in("hySupplier",hySupplierList));
					List<HyLine> lineList=hyLineService.findList(null,lineFilter,null);
					if(lineList.size()==0){
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new Page<HyGroup>());
				    }
					else{
						groupFilter.add(Filter.in("line", lineList));
						List<HyGroup> groupList=hyGroupService.findList(null,groupFilter,null);
						if(groupList.size()==0){
						json.setMsg("查询成功");
					    json.setSuccess(true);
					    json.setObj(new Page<HyGroup>());
					    }
						else{
							List<Filter> settleFilter=new ArrayList<>();
							settleFilter.add(Filter.in("hyGroup", groupList));
							if(auditStatus != null){
								settleFilter.add(Filter.eq("auditStatus",auditStatus));
							}
							List<PayablesBranchsettle> settleList=payablesBranchsettleService.findList(null,settleFilter,null);
							if(settleList.isEmpty()){
								json.setMsg("查询成功");
							    json.setSuccess(true);
							    json.setObj(new Page<HyGroup>());
							}
							else{
								for(PayablesBranchsettle settle:settleList){
									HyGroup hyGroup=settle.getHyGroup();
									HashMap<String,Object> groupMap=new HashMap<String,Object>();
									HyAdmin creator=hyGroup.getCreator();
									groupMap.put("settleId", settle.getId());
									groupMap.put("payNumber", settle.getPayNumber());
									groupMap.put("supplierName", hyGroup.getLine().getHySupplier().getSupplierName());
									groupMap.put("contactName", hyGroup.getLine().getHySupplier().getOperator().getName()); //联系人
									groupMap.put("money", settle.getShifuMoney());
									groupMap.put("applyName", settle.getApplyName().getName()); //申请人
									groupMap.put("createDate", settle.getApplyTime());
									groupMap.put("status", settle.getAuditStatus());
									/** 当前用户对本条数据的操作权限 */
								    if(creator.equals(admin)){
								    	if(co==CheckedOperation.view){
								    		groupMap.put("privilege", "view");
								    	}
								    	else{
								    		groupMap.put("privilege", "edit");
								    	}
								    }
								    else{
								    	if(co==CheckedOperation.edit){
								    		groupMap.put("privilege", "edit");
								    	}
								    	else{
								    		groupMap.put("privilege", "view");
								    	}
								    }
									list.add(groupMap);
								}
								Collections.sort(list, new Comparator<Map<String, Object>>() {
									@Override
									public int compare(Map<String, Object> o1, Map<String, Object> o2) {
										Date date1 = (Date) o1.get("createDate");
										Date date2 = (Date) o2.get("createDate");
										return date1.compareTo(date2); 
									}
								});
							    Collections.reverse(list);
							    Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);
							    json.setMsg("查询成功");
							    json.setSuccess(true);
							    json.setObj(page);
							}
						}
					}
				}
			}
			
			else if(supplierName==null && applyName!=null){
				List<Filter> filter=new ArrayList<Filter>();
				filter.add(Filter.like("name",applyName));
				List<HyAdmin> adminList=hyAdminService.findList(null,filter,null);
				if(adminList.size()==0){
				json.setMsg("查询成功");
			    json.setSuccess(true);
			    json.setObj(new Page<HyGroup>());
			    }
				else{
					List<Filter> settleFilter=new ArrayList<Filter>();
					settleFilter.add(Filter.in("applyName", adminList));
					if(auditStatus != null){
						settleFilter.add(Filter.eq("auditStatus",auditStatus));
					}
					List<PayablesBranchsettle> settleList=payablesBranchsettleService.findList(null,settleFilter,null);
					if(settleList.isEmpty()){
						json.setMsg("查询成功");
					    json.setSuccess(true);
					    json.setObj(new Page<HyGroup>());
					}
					else{
						for(PayablesBranchsettle settle:settleList){
							HyGroup hyGroup=settle.getHyGroup();
							HashMap<String,Object> groupMap=new HashMap<String,Object>();
							HyAdmin creator=hyGroup.getCreator();
							groupMap.put("settleId", settle.getId());
							groupMap.put("payNumber", settle.getPayNumber());
							groupMap.put("supplierName", hyGroup.getLine().getHySupplier().getSupplierName());
							groupMap.put("contactName", hyGroup.getLine().getHySupplier().getOperator().getName()); //联系人
							groupMap.put("money", settle.getShifuMoney());
							groupMap.put("applyName", settle.getApplyName().getName()); //申请人
							groupMap.put("createDate", settle.getApplyTime());
							groupMap.put("status", settle.getAuditStatus());
							/** 当前用户对本条数据的操作权限 */
						    if(creator.equals(admin)){
						    	if(co==CheckedOperation.view){
						    		groupMap.put("privilege", "view");
						    	}
						    	else{
						    		groupMap.put("privilege", "edit");
						    	}
						    }
						    else{
						    	if(co==CheckedOperation.edit){
						    		groupMap.put("privilege", "edit");
						    	}
						    	else{
						    		groupMap.put("privilege", "view");
						    	}
						    }
							list.add(groupMap);
						}
						Collections.sort(list, new Comparator<Map<String, Object>>() {
							@Override
							public int compare(Map<String, Object> o1, Map<String, Object> o2) {
								Date date1 = (Date) o1.get("createDate");
								Date date2 = (Date) o2.get("createDate");
								return date1.compareTo(date2); 
							}
						});
					    Collections.reverse(list);
					    Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);
					    json.setMsg("查询成功");
					    json.setSuccess(true);
					    json.setObj(page);
					}
				}
			}
			
			else{
				List<Filter> supplierFilter=new ArrayList<Filter>();
				supplierFilter.add(Filter.like("supplierName", supplierName));
				List<HySupplier> hySupplierList=hySupplierService.findList(null,supplierFilter,null);
				if(hySupplierList.size()==0){
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(new Page<HySupplier>());
				}
				else{
					List<Filter> lineFilter=new ArrayList<>();
					lineFilter.add(Filter.in("hySupplier",hySupplierList));
					List<HyLine> lineList=hyLineService.findList(null,lineFilter,null);
					if(lineList.size()==0){
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(new Page<HyGroup>());
				    }
					else{
						groupFilter.add(Filter.in("line", lineList));
						List<HyGroup> groupList=hyGroupService.findList(null,groupFilter,null);
						if(groupList.size()==0){
						json.setMsg("查询成功");
					    json.setSuccess(true);
					    json.setObj(new Page<HyGroup>());
					    }
						else{
							List<Filter> settleFilter=new ArrayList<Filter>();
							settleFilter.add(Filter.in("hyGroup", groupList));
							List<Filter> filter=new ArrayList<Filter>();
							filter.add(Filter.like("name",applyName));
							List<HyAdmin> adminList=hyAdminService.findList(null,filter,null);
							if(adminList.size()==0){
							json.setMsg("查询成功");
						    json.setSuccess(true);
						    json.setObj(new Page<HyGroup>());
						    }
							else{
								settleFilter.add(Filter.in("applyName", adminList));
								if(auditStatus != null){
									settleFilter.add(Filter.eq("auditStatus",auditStatus));
								}
								List<PayablesBranchsettle> settleList=payablesBranchsettleService.findList(null,settleFilter,null);
								if(settleList.isEmpty()){
									json.setMsg("查询成功");
								    json.setSuccess(true);
								    json.setObj(new Page<HyGroup>());
								}
								else{
									for(PayablesBranchsettle settle:settleList){
										HyGroup hyGroup=settle.getHyGroup();
										HashMap<String,Object> groupMap=new HashMap<String,Object>();
										HyAdmin creator=hyGroup.getCreator();
										groupMap.put("settleId", settle.getId());
										groupMap.put("payNumber", settle.getPayNumber());
										groupMap.put("supplierName", hyGroup.getLine().getHySupplier().getSupplierName());
										groupMap.put("contactName", hyGroup.getLine().getHySupplier().getOperator().getName()); //联系人
										groupMap.put("money", settle.getShifuMoney());
										groupMap.put("applyName", settle.getApplyName().getName()); //申请人
										groupMap.put("createDate", settle.getApplyTime());
										groupMap.put("status", settle.getAuditStatus());
										/** 当前用户对本条数据的操作权限 */
									    if(creator.equals(admin)){
									    	if(co==CheckedOperation.view){
									    		groupMap.put("privilege", "view");
									    	}
									    	else{
									    		groupMap.put("privilege", "edit");
									    	}
									    }
									    else{
									    	if(co==CheckedOperation.edit){
									    		groupMap.put("privilege", "edit");
									    	}
									    	else{
									    		groupMap.put("privilege", "view");
									    	}
									    }
										list.add(groupMap);
									}
									Collections.sort(list, new Comparator<Map<String, Object>>() {
										@Override
										public int compare(Map<String, Object> o1, Map<String, Object> o2) {
											Date date1 = (Date) o1.get("createDate");
											Date date2 = (Date) o2.get("createDate");
											return date1.compareTo(date2); 
										}
									});
								    Collections.reverse(list);
								    Page<Map<String,Object>> page=new Page<Map<String,Object>>(list,list.size(),pageable);
								    json.setMsg("查询成功");
								    json.setSuccess(true);
								    json.setObj(page);
								}
							}
						}
					}
				}
			}
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 打款单信息
	 */
	@RequestMapping(value="payBill/view")
	@ResponseBody
	public Json payBill(Long settleId)
	{
		Json json=new Json();
		try{
			PayablesBranchsettle payablesBranchsettle=payablesBranchsettleService.find(settleId);
			HyGroup hyGroup=payablesBranchsettle.getHyGroup();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();	
			Map<String,Object> map=new HashMap<String,Object>();
			String processInstanceId = payablesBranchsettle.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> auditList = new LinkedList<>();
			/*审核信息*/
			for (Comment comment : commentList) {
				Map<String, Object> auditMap = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				String step = "";
				if (task != null) {
					step = task.getName();
				}
				auditMap.put("step", step);
				String username = comment.getUserId();
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				auditMap.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				if (index < 0) {
					auditMap.put("comment", " ");
					auditMap.put("result", 1);
				} else {
					auditMap.put("comment", str.substring(0, index));
					auditMap.put("result", Integer.parseInt(str.substring(index + 1)));
				}
				auditMap.put("time", comment.getTime());
				auditList.add(auditMap);
			}
			map.put("auditList", auditList);
			/*供应商信息*/
			map.put("payNumber", payablesBranchsettle.getPayNumber());
			map.put("supplierName", hyGroup.getLine().getHySupplier().getSupplierName());
			map.put("accountName", hyGroup.getLine().getContract().getBankList().getAccountName()); //账户名称
			map.put("bankAccount", hyGroup.getLine().getContract().getBankList().getBankAccount());
			map.put("bankName", hyGroup.getLine().getContract().getBankList().getBankName()); //开户行
			map.put("bankCode", hyGroup.getLine().getContract().getBankList().getBankCode()); //银行联行号
			map.put("bankType", hyGroup.getLine().getContract().getBankList().getBankType()); //对公为false,对私为true
			map.put("contractCode", hyGroup.getLine().getContract().getContractCode()); //合同号
			map.put("liable", hyGroup.getLine().getContract().getLiable().getName()); //负责人姓名
			/*团结算信息*/
			List<Map<String, Object>> grouplist = new ArrayList<Map<String, Object>>();
			Map<String,Object> groupmap=new HashMap<String,Object>();
			groupmap.put("settleType", "团结算");
			groupmap.put("productId", hyGroup.getLine().getPn());
			groupmap.put("lineName", hyGroup.getLine().getName());
			groupmap.put("startDate", hyGroup.getStartDay());
			groupmap.put("creatorName", hyGroup.getCreator().getName()); //建团计调
			BigDecimal allIncome=new BigDecimal(0);
			List<Filter> accountFilter=new ArrayList<>();
			accountFilter.add(Filter.eq("groupId", hyGroup));

			List<RegulategroupAccount> accountList=regulategroupAccountService.findList(null,accountFilter,null);
			if(accountList.size()>0){
				allIncome=allIncome.add(accountList.get(0).getAllIncome()); //总收入
			}
			groupmap.put("allIncome", allIncome);
//			BigDecimal allExpense=new BigDecimal(0);
//			if(accountList.size()>0){
//				/*总收入-扣点-总支出+预付款*/
//				allExpense=allExpense.add(accountList.get(0).getAllExpense());
//			}
//			groupmap.put("allExpense", allExpense);
			groupmap.put("signupNumber", hyGroup.getSignupNumber());
			/** add by wj 2019.06.28**/
			BigDecimal koudian = payablesBranchsettle.getKoudianMoney();
//			BigDecimal koudian=new BigDecimal(0);
//			if(hyGroup.getKoudianType().equals(DeductLine.tuanke)){
//				BigDecimal div=new BigDecimal(100);
//				/*扣点为总收入乘以扣点的百分数*/
//				koudian=koudian.add(allIncome.multiply(hyGroup.getPercentageKoudian()).divide(div).setScale(2, RoundingMode.HALF_UP));
//			}
//			/*如果是人头扣点,扣点等于每个人扣点乘以报名人数*/
//			else if(hyGroup.getKoudianType().equals(DeductLine.rentou)){
//				koudian=koudian.add(hyGroup.getPersonKoudian().multiply(new BigDecimal(hyGroup.getSignupNumber())));
//			}
			groupmap.put("koudian", koudian);
//			List<Filter> balanceFilter=new ArrayList<>();
//			balanceFilter.add(Filter.eq("groupId", hyGroup.getId()));
//			balanceFilter.add(Filter.eq("payStatus", 1)); //付款状态为已付款								
//			List<BalanceDueApplyItem> balanceList=balanceDueApplyItemService.findList(null,balanceFilter,null);
//			BigDecimal prepay=new BigDecimal(0);							
//			if(balanceList.size()>0){
//				/*将所有的usePrePay相加，得到预付款prepay*/
//				for(BalanceDueApplyItem balanceDueApplyItem:balanceList){
//					prepay=prepay.add(balanceDueApplyItem.getUsePrePay());
//				}
//			}	
//			groupmap.put("prepay", prepay);
			groupmap.put("totalMoney",payablesBranchsettle.getRealMoney());
			grouplist.add(groupmap);
			map.put("groupList", grouplist);
			
//			/*退款信息*/
//			BigDecimal refundMoney=new BigDecimal(0);//退款
//			List<Filter> orderFilter=new ArrayList<>();
//			orderFilter.add(Filter.eq("groupId",hyGroup.getId()));
//			List<HyOrder> orderList=hyOrderService.findList(null,orderFilter,null);
//			List<HyOrderApplication> appList=new ArrayList<HyOrderApplication>();
//			/*退款信息*/
//			if(orderList.size()>0){
//				for(HyOrder hyOrder:orderList){
//					List<Filter> appFilter=new ArrayList<Filter>();
//					appFilter.add(Filter.in("orderId", hyOrder.getId()));
//					appFilter.add(Filter.eq("status", 4)); //筛选已退款状态
//					List<HyOrderApplication> applicationList=hyOrderApplicationService.findList(null,appFilter,null);
//					if(applicationList.size()>0){
//						appList.addAll(applicationList);	
//					}									
//				}																	
//				if(appList.size()>0){
//					/*将所有的退款相加的最终的退款*/		
//					for(HyOrderApplication hyOrderApplication:appList){
//						HashMap<String,Object> obj=new HashMap<String,Object>();
//						refundMoney=refundMoney.add(hyOrderApplication.getJiesuanMoney());
//						obj.put("settleType", "退款");
//						obj.put("orderNumber", hyOrderService.find(hyOrderApplication.getOrderId()).getOrderNumber()); //点单编号
//				        obj.put("productId", hyGroup.getLine().getPn());
//				        obj.put("lineName", hyGroup.getLine().getName());
//				        obj.put("startDate", hyGroup.getStartDay());
//				        obj.put("refundDate", hyOrderApplication.getCreatetime()); //退款日期
//				        /*游客联系人*/
//				        obj.put("contact", hyOrderService.find(hyOrderApplication.getOrderId()).getContact());
//					    obj.put("refundReq", hyOrderApplication.getView());
//					    obj.put("refundMoney", hyOrderApplication.getJiesuanMoney());
//					    list.add(obj);
//					}
//				}
//			}
			/*退款信息*/
			String sql = "select pro.id from ACT_HI_TASKINST rt ,"
					+ "(SELECT regulate.apply_time,application.* FROM hy_order_application application,hy_order o ,hy_regulate regulate "
					+ " where o.id = application.order_id and application.status = 4 and o.group_id = "
					+ hyGroup.getId() + " and regulate.group_id = o.group_id) pro "
					+ " where ((rt.PROC_DEF_ID_ LIKE 'storeTuiTuan%') or (rt.PROC_DEF_ID_ LIKE 'xiaotuan%')) "
					+ " AND rt.TASK_DEF_KEY_ = 'usertask4' AND PROC_INST_ID_ = pro.process_instance_id "
					+ " AND rt.END_TIME_ < pro.apply_time ";
			List<Object[]> applicationlist = hyOrderApplicationService.statis(sql);
			BigDecimal refundMoney=new BigDecimal(0);//退款
			for (Object object : applicationlist) {
				HashMap<String,Object> obj=new HashMap<String,Object>();
				BigInteger id1 = (BigInteger) object;
				Long id = id1.longValue();
				HyOrderApplication hyOrderApplication = hyOrderApplicationService.find(id);
				refundMoney=refundMoney.add(hyOrderApplication.getJiesuanMoney()).setScale(2, RoundingMode.HALF_UP);
				obj.put("orderNumber", hyOrderService.find(hyOrderApplication.getOrderId()).getOrderNumber()); //点单编号
		        obj.put("productId", hyGroup.getLine().getPn());
		        obj.put("lineName", hyGroup.getLine().getName());
		        obj.put("startDate", hyGroup.getStartDay());
		        obj.put("refundDate", hyOrderApplication.getCreatetime()); //退款日期
	            /*游客联系人*/
		        obj.put("contact", hyOrderService.find(hyOrderApplication.getOrderId()).getContact());
			    obj.put("refundReq", hyOrderApplication.getView());
			    obj.put("refundMoney", hyOrderApplication.getJiesuanMoney());
			    list.add(obj);
			}
			map.put("refundList", list);
			map.put("subTotal", refundMoney);
			//BigDecimal money=new BigDecimal(0);
			//money=money.add(allIncome).subtract(koudian).subtract(allExpense).add(prepay).subtract(refundMoney);
			map.put("yingfu", payablesBranchsettle.getShifuMoney());
			json.setMsg("查询列表成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="modify/submit")
	@ResponseBody
	public Json modifysubmit(Long settleId,Integer state,String adjustReason,BigDecimal adjustMoney,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			PayablesBranchsettle payablesBranchsettle=payablesBranchsettleService.find(settleId);
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			Task task = taskService.createTaskQuery().processInstanceId(payablesBranchsettle.getProcessInstanceId()).singleResult();
			HashMap<String, Object> map = new HashMap<>();
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("eduleixing", Eduleixing.branchsettleLimit));
			List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
			BigDecimal money = edu.get(0).getMoney();
			if(payablesBranchsettle.getShifuMoney().doubleValue()>money.doubleValue()){
				map.put("money", "more");
				payablesBranchsettle.setStep(1);//待市场部副总限额审核
			}
			else{
				map.put("money", "less");
				payablesBranchsettle.setStep(2); //待财务部审核
			}
			if(state == 2){ //不修改直接提交
				payablesBranchsettle.setAuditStatus(1); //设置为审核状态为审核中
			}
			else if(state==3){
				payablesBranchsettle.setAuditStatus(1); //设置为审核状态为审核中
				payablesBranchsettle.setIsModify(true);
				payablesBranchsettle.setAdjustMoney(adjustMoney);
				payablesBranchsettle.setAdjustReason(adjustReason);
				payablesBranchsettle.setShifuMoney(payablesBranchsettle.getShifuMoney().add(adjustMoney));
			}
			// 完成分公司团结算申请
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), payablesBranchsettle.getProcessInstanceId(), " :1");
			taskService.complete(task.getId(),map);
			payablesBranchsettleService.update(payablesBranchsettle);
			json.setMsg("提交申请成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
