package com.hongyu.task.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hongyu.Filter;
import com.hongyu.entity.BranchBalance;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyProviderRebate;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StorePreSave;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyProviderRebateService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StorePreSaveService;
import com.hongyu.service.StoreService;
import com.hongyu.task.Processor;

@Component("rebateProcessor")
public class RebateProcessor implements Processor{

	@Resource(name = "hyProviderRebateServiceImpl")
	HyProviderRebateService hyProviderRebateService;
	
	@Resource(name = "hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	@Resource(name = "hyGroupServiceImpl")
	private HyGroupService hyGroupService;
	
	@Resource(name = "hyLineServiceImpl")
	private HyLineService hyLineService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name = "storeAccountServiceImpl")
	private StoreAccountService storeAccountService;
	
	@Resource(name = "storePreSaveServiceImpl")
	private StorePreSaveService storePreSaveService;
	
	@Resource(name = "branchPreSaveServiceImpl")
	private BranchPreSaveService branchPreSaveService;
	
	@Resource(name = "branchBalanceServiceImpl")
	private BranchBalanceService branchBalanceService;
	
	@Resource(name = "departmentServiceImpl")
	private DepartmentService departmentService;
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		
		try {
			//第一步 遍历所有order 如果当前日期=发团日期+天数
			List<HyOrder> myHyOrder = new ArrayList<>();
			//List<Filter> orderFilter = new ArrayList<Filter>();
			//所有的order
			myHyOrder = hyOrderService.findList(null, null, null);
			//遍历
			Date currentDate = new Date();
			//便于计算
//			SimpleDateFormat currentFormat = new SimpleDateFormat("yyyy-MM-dd"); 
//			SimpleDateFormat depatureFormat = new SimpleDateFormat("yyyy-MM-dd"); 
			
//			currentFormat.format(currentDate);
//			Date defaultDate = new Date(0);
//			depatureFormat.format(defaultDate);
			
			for(HyOrder hyOrderItem : myHyOrder){
				Date myDepartureDate = hyOrderItem.getFatuandate();
				Integer myDays = hyOrderItem.getTianshu();
				//myDay天之后的日期
				Date secondDate = null;
				if(myDepartureDate != null && myDays != null){
					secondDate = new Date(myDepartureDate.getTime() + (long)myDays*24*60*60*1000);
				}
				
				//如果日期相等(今天的日期=发团+间隔期) 开始进行操作
				if(secondDate != null && myDays != null && currentDate != null)
				if(isSameDay(currentDate,secondDate)){
					//判断storeType是否是等于2
					Long myStoreId = hyOrderItem.getStoreId();
					Store myStore = storeService.find(myStoreId);
					//用于判断是给谁充值
					String flag = "store为null";
					//返利金额
					BigDecimal myRebate = new BigDecimal(0);
					//Integer peopleNumber = hyOrderItem.getPeople();
					BigDecimal peopleNumber = new BigDecimal(hyOrderItem.getPeople());
					
					if(myStore != null){
						//门店=2 等于说是直营 
						if(myStore.getStoreType() != null)
						if(myStore.getStoreType() == 2){
							flag = "分公司";
						} else {
							flag = "门店";
						}
						
						//计算返利金额 如果不知道返给谁还不如不算
						Long groupId = hyOrderItem.getGroupId();
						//order--group
						HyGroup myHyGroup = hyGroupService.find(groupId);
						if(myHyGroup != null){
							//group--line
							HyLine myLine = myHyGroup.getLine();
							if(myLine != null){
								//line--contract
								HySupplierContract myHySupplierContract = myLine.getContract();
								if(myHySupplierContract != null){
									//contract--rebate
									String myContractCode = myHySupplierContract.getContractCode();
									List<Filter> rebateFilter = new ArrayList<Filter>();
									rebateFilter.add(Filter.eq("contractNumber", myContractCode));
									//这个code在contract表里是不会重复的
									List<HyProviderRebate> hyProviderRebateList = new ArrayList<>(); 
									hyProviderRebateList = hyProviderRebateService.findList(null, rebateFilter, null);
									if(!hyProviderRebateList.isEmpty()){
										//赋值
										myRebate = hyProviderRebateList.get(0).getRebate();
									}
								}
							}
						}
					}
					
					//计算返利金额
					BigDecimal rebateMoney = myRebate.multiply(peopleNumber);
					
					//向分公司或者门店充值
					if(flag == "门店"){
						//向门店充值
						//根据storeID筛选
						List<Filter> storeAccountFilter = new ArrayList<Filter>();
						
						List<StoreAccount> storeAccountList = new ArrayList<>();
						storeAccountFilter.add(Filter.eq("store", myStore));
						storeAccountList = storeAccountService.findList(null, storeAccountFilter, null);
						//更改一下价格存回去
						//这里也是 如果storeAccountList是空的 干脆别存了
						if(!storeAccountList.isEmpty()){
							StoreAccount thisStoreAccount = storeAccountList.get(0);
							BigDecimal accountMoney = thisStoreAccount.getBalance();
							BigDecimal totalMoney = accountMoney.add(rebateMoney);
							thisStoreAccount.setBalance(totalMoney);
							storeAccountService.update(thisStoreAccount);
							
							//开始向数据库pre_save里插入数据
							StorePreSave myStorePreSave = new StorePreSave();
							myStorePreSave.setAmount(rebateMoney);//返利金额
							myStorePreSave.setDate(new Date());
							myStorePreSave.setOrderCode(hyOrderItem.getOrderNumber());
							myStorePreSave.setOrderId(hyOrderItem.getId());
							myStorePreSave.setPreSaveBalance(totalMoney);
							myStorePreSave.setRemark("供应商线路返利");
							myStorePreSave.setStoreId(myStoreId);
							myStorePreSave.setStoreName(myStore.getStoreName());
							myStorePreSave.setType(15);
							
							storePreSaveService.save(myStorePreSave);
						}
					} else if(flag == "分公司"){
						//向分公司充值
						Department hyDepartment = new Department();
						Long departmentId = null;
						//一样把余额表更新
						if(myStore != null){
							hyDepartment = myStore.getDepartment();
							if(hyDepartment != null){
								departmentId = hyDepartment.getId();
							}
						}
						//更新价格
						BigDecimal totalMoney = null;
						if(departmentId != null){
							List<Filter> branchAccountFilter = new ArrayList<Filter>();
							branchAccountFilter.add(Filter.eq("branchId", departmentId));
							List<BranchBalance> branchBalanceList = new ArrayList<>();
							branchBalanceList = branchBalanceService.findList(null, branchAccountFilter, null);
							//这里如果拿到了这个List，才能向下走
							//没有balance 那么也别往preSave表里存得了
							if(!branchBalanceList.isEmpty()){
								
								BranchBalance myBranchBalance = branchBalanceList.get(0);
								BigDecimal newBranchBalance = myBranchBalance.getBranchBalance();
								totalMoney = newBranchBalance.add(rebateMoney);
								
								myBranchBalance.setBranchBalance(totalMoney);
								branchBalanceService.update(myBranchBalance);
								
								//插入一条记录 准备工作
								String subCompanyId = null;
								Department myDepartment = departmentService.find(departmentId);
								if(myDepartment != null){
									String treePath = myDepartment.getTreePath();
									String[] strArray = null;
									strArray = treePath.split(",");
									//找到分公司的ID
									subCompanyId = strArray[2];
								}
								
								//String -- Long
								Long subCompanyIdNumber = Long.parseLong(subCompanyId);
								//用找到的子公司的ID去找到子公司的部门信息
								Department mySubCompany = departmentService.find(subCompanyIdNumber);
								
								//下面开始真正在branch_pre_save里面插入数据
								BranchPreSave myBranchPreSave = new BranchPreSave();
								myBranchPreSave.setAmount(rebateMoney);
								myBranchPreSave.setBranchId(departmentId);
								if(mySubCompany != null){
									myBranchPreSave.setBranchName(mySubCompany.getName());
								} else {
									myBranchPreSave.setBranchName("不大清楚");
								}
								myBranchPreSave.setDate(new Date());
								if(myDepartment != null){
									myBranchPreSave.setDepartmentName(myDepartment.getFullName());
								} else {
									myBranchPreSave.setBranchName("不大清楚");
								}
								myBranchPreSave.setPreSaveBalance(totalMoney);
								myBranchPreSave.setRemark("供应商线路返利");
								myBranchPreSave.setType(5);
								myBranchPreSave.setOrderId(hyOrderItem.getId());
								
								branchPreSaveService.save(myBranchPreSave);
								
							}
						
						}
						
					}
							
				}
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}		
		
	}

	
	public boolean isSameDay(Date date1, Date date2) {
	        if(date1 != null && date2 != null) {
	            Calendar cal1 = Calendar.getInstance();
	            cal1.setTime(date1);
	            Calendar cal2 = Calendar.getInstance();
	            cal2.setTime(date2);
	            return isSameDay(cal1, cal2);
	        } else {
	            throw new IllegalArgumentException("The date must not be null");
	        }
	 }

	 
	public boolean isSameDay(Calendar cal1, Calendar cal2) {
	        if(cal1 != null && cal2 != null) {
	            return cal1.get(0) == cal2.get(0) && cal1.get(1) == cal2.get(1) && cal1.get(6) == cal2.get(6);
	        } else {
	            throw new IllegalArgumentException("The date must not be null");
	        }
	 }
	    
	

}
