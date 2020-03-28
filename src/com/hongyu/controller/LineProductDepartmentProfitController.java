package com.hongyu.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyRegulate;
import com.hongyu.entity.HyRegulateitemElement;
import com.hongyu.entity.HyRegulateitemGuide;
import com.hongyu.entity.RegulategroupAccount;
import com.hongyu.entity.Store;
import com.hongyu.entity.HySupplierElement.SupplierType;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.HyRegulateitemElementService;
import com.hongyu.service.HyRegulateitemGuideService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.RegulategroupAccountService;
import com.hongyu.service.StoreService;
import com.hongyu.util.DateUtil;

/**
 * 线路产品部门利润报表
 * author:GSbing
 */
@Controller
@RequestMapping("admin/lineProduct/departmentProfit")
public class LineProductDepartmentProfitController {

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@Resource(name = "regulategroupAccountServiceImpl")
	RegulategroupAccountService regulategroupAccountService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name = "guideServiceImpl")
	GuideService guideService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyRegulateitemElementServiceImpl")
	HyRegulateitemElementService hyRegulateitemElementService;
	
	@Resource(name = "hyRegulateitemGuideServiceImpl")
	HyRegulateitemGuideService hyRegulateitemGuideService;
	
	
	
	/**
	 * 利润单详情
	*/
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detaiview(String productId,@DateTimeFormat(pattern="yyyy-MM-dd") Date startDay,HttpSession session)
	{
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
		    HyAdmin hyAdmin=hyAdminService.find(username);
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("groupLinePn", productId));
			filters.add(Filter.eq("startDay", startDay));
			List<HyGroup> hyGroups=hyGroupService.findList(null,filters,null);
			filters.clear();
			HyGroup hyGroup=hyGroups.get(0);
		    Map<String, Object> obj = new HashMap<>();
			List<Map<String,Object>> groupInfoList=new ArrayList<>();
			//添加团信息
			Map<String,Object> groupMap=new HashMap<String,Object>();
			groupMap.put("productId", productId); //产品ID
			groupMap.put("startDay", startDay); //发团日期
			groupMap.put("signupNumber", hyGroup.getSignupNumber()); //报名人数
			
			filters.add(Filter.eq("groupId", hyGroup.getId())); 
			//在单团核算表中找出该团的总收入,总支出和利润
			List<RegulategroupAccount> regulategroupAccounts=regulategroupAccountService.findList(null,filters,null);
			filters.clear();
			if(regulategroupAccounts.isEmpty()) {
				groupMap.put("guide", null);
				groupMap.put("allIncome", null);
				groupMap.put("allExpense", null);
				groupMap.put("profit", null);
			}
			else {
				groupMap.put("guide", regulategroupAccounts.get(0).getGuide()); //导游
				groupMap.put("allIncome", regulategroupAccounts.get(0).getAllIncome()); //总收入
				groupMap.put("allExpense", regulategroupAccounts.get(0).getAllExpense()); //总支出
				groupMap.put("profit", regulategroupAccounts.get(0).getProfit()); //利润
			}
			groupInfoList.add(groupMap);
			
			obj.put("groupInfoList",groupInfoList); //将团信息添加进去
			
			List<Map<String,Object>> incomeList=new ArrayList<>();
			filters.add(Filter.eq("groupId", hyGroup.getId()));
			//根据团找订单
			List<HyOrder> hyOrders=hyOrderService.findList(null,filters,null);
			filters.clear();
			Integer peopleTotal=0;
			BigDecimal incomeTotal=BigDecimal.ZERO; 
			for(HyOrder hyOrder:hyOrders) {
				 Map<String, Object> incomeMap = new HashMap<>();
				 incomeMap.put("orderNumber", hyOrder.getOrderNumber());  //订单号
				 incomeMap.put("people", hyOrder.getPeople());  //人数
				 //找到订单所属门店
				 Store store=storeService.find(hyOrder.getStoreId()); 
				 incomeMap.put("storeName", store.getStoreName()); //门店
				 
				 //计算团款
				 BigDecimal groupMoney=BigDecimal.ZERO;
				 groupMoney = groupMoney.add(hyOrder.getJiesuanMoney1()).add(hyOrder.getAdjustMoney()).subtract(hyOrder.getDiscountedPrice());
				 
				 incomeMap.put("income", groupMoney); //收入
				 incomeTotal=incomeTotal.add(groupMoney);
				 peopleTotal=peopleTotal+hyOrder.getPeople();
				 incomeList.add(incomeMap);				 
			}
			obj.put("incomeList", incomeList); //团款收入数组列表
			obj.put("incomeTotal", incomeTotal); //团款收入合计
			obj.put("peopleTotal", peopleTotal); //人数合计
			
			//找出购物条目
			filters.add(Filter.eq("hyGroup", hyGroup));
			filters.add(Filter.eq("type", SupplierType.shopping));
			List<HyRegulateitemElement> shopElements=hyRegulateitemElementService.findList(null,filters,null);
			filters.clear();
			BigDecimal shopMoney=BigDecimal.ZERO;
			for(HyRegulateitemElement element:shopElements) {
				shopMoney=shopMoney.add(element.getMoney());
			}
			obj.put("shopMoney", shopMoney);
			
			//找出游客自费收入条目
			filters.add(Filter.eq("hyGroup", hyGroup));
			filters.add(Filter.eq("type", SupplierType.selfpay));
			List<HyRegulateitemElement> selfPayElements=hyRegulateitemElementService.findList(null,filters,null);
			filters.clear();
			BigDecimal selfPayMoney=BigDecimal.ZERO;
			for(HyRegulateitemElement element:selfPayElements) {
				selfPayMoney=selfPayMoney.add(element.getMoney());
			}
			obj.put("selfPayMoney", selfPayMoney);
			
			//找出其他收入条目
			filters.add(Filter.eq("hyGroup", hyGroup));
			filters.add(Filter.eq("type", SupplierType.otherincome));
			List<HyRegulateitemElement> otherincomeElements=hyRegulateitemElementService.findList(null,filters,null);
			filters.clear();
			BigDecimal otherincomeMoney=BigDecimal.ZERO;
			for(HyRegulateitemElement element:otherincomeElements) {
				otherincomeMoney=otherincomeMoney.add(element.getMoney());
			}
			obj.put("otherincomeMoney", otherincomeMoney);
			
			
			List<Map<String,Object>> expenseList=new ArrayList<>();
			BigDecimal expenseTotal=BigDecimal.ZERO;
//			List<HyRegulateitemElement> hyRegulateitemElements=new ArrayList<>();
			
			//查找用餐支出
			filters.add(Filter.eq("hyGroup", hyGroup));
			filters.add(Filter.eq("type", SupplierType.catering));
			
			//找出用餐支出条目
			List<HyRegulateitemElement> restaurantElements=hyRegulateitemElementService.findList(null,filters,null);
			filters.clear();
			for(HyRegulateitemElement element:restaurantElements) {
				Map<String, Object> map = new HashMap<>();
				map.put("supplierName", element.getSupplierName()); //供应商名称
				map.put("money", element.getMoney()); //金额
				expenseTotal=expenseTotal.add(element.getMoney());
				expenseList.add(map);
			}
			
			//找出门票支出条目
			filters.add(Filter.eq("hyGroup", hyGroup));
			filters.add(Filter.eq("type", SupplierType.ticket));
			List<HyRegulateitemElement> ticketElements=hyRegulateitemElementService.findList(null,filters,null);
			filters.clear();
			for(HyRegulateitemElement element:ticketElements) {
				Map<String, Object> map = new HashMap<>();
				map.put("supplierName", element.getSupplierName()); //供应商名称
				map.put("money", element.getMoney()); //金额
				expenseTotal=expenseTotal.add(element.getMoney());
				expenseList.add(map);
			}
			
			//找出车辆支出条目,计算车辆支出
			filters.add(Filter.eq("hyGroup", hyGroup));
			filters.add(Filter.eq("type", SupplierType.car));
			List<HyRegulateitemElement> carElements=hyRegulateitemElementService.findList(null,filters,null);
			filters.clear();
			for(HyRegulateitemElement element:carElements) {
				Map<String, Object> map = new HashMap<>();
				map.put("supplierName", element.getSupplierName()); //供应商名称
				map.put("money", element.getMoney()); //金额
				expenseTotal=expenseTotal.add(element.getMoney());
				expenseList.add(map);
			}
			
			//找出大交通支出条目,计算大交通支出
			filters.add(Filter.eq("hyGroup", hyGroup));
			filters.add(Filter.eq("type", SupplierType.traffic));
			List<HyRegulateitemElement> trafficElements=hyRegulateitemElementService.findList(null,filters,null);
			filters.clear();
			for(HyRegulateitemElement element:trafficElements) {
				Map<String, Object> map = new HashMap<>();
				map.put("supplierName", element.getSupplierName()); //供应商名称
				map.put("money", element.getMoney()); //金额
				expenseTotal=expenseTotal.add(element.getMoney());
				expenseList.add(map);
			}
			
			//找出住宿支出条目,计算住宿支出
			filters.add(Filter.eq("hyGroup", hyGroup));
			filters.add(Filter.eq("type", SupplierType.hotel));
			List<HyRegulateitemElement> hotelElements=hyRegulateitemElementService.findList(null,filters,null);
			filters.clear();
			for(HyRegulateitemElement element:hotelElements) {
				Map<String, Object> map = new HashMap<>();
				map.put("supplierName", element.getSupplierName()); //供应商名称
				map.put("money", element.getMoney()); //金额
				expenseTotal=expenseTotal.add(element.getMoney());
				expenseList.add(map);
			}
			
			//找出保险支出条目,计算保险支出
			filters.add(Filter.eq("hyGroup", hyGroup));
			filters.add(Filter.eq("type", SupplierType.insurance));
			List<HyRegulateitemElement> insuranceElements=hyRegulateitemElementService.findList(null,filters,null);
			filters.clear();
			for(HyRegulateitemElement element:insuranceElements) {
				Map<String, Object> map = new HashMap<>();
				map.put("supplierName", element.getSupplierName()); //供应商名称
				map.put("money", element.getMoney()); //金额
				expenseTotal=expenseTotal.add(element.getMoney());
				expenseList.add(map);
			}
			
			//找出电子券支出条目,计算电子券支出
			filters.add(Filter.eq("hyGroup", hyGroup));
			filters.add(Filter.eq("type", SupplierType.coupon));
			List<HyRegulateitemElement> couponElements=hyRegulateitemElementService.findList(null,filters,null);
			filters.clear();
			for(HyRegulateitemElement element:couponElements) {
				Map<String, Object> map = new HashMap<>();
				map.put("supplierName", element.getSupplierName()); //供应商名称
				map.put("money", element.getMoney()); //金额
				expenseTotal=expenseTotal.add(element.getMoney());
				expenseList.add(map);
			}
			
			//找出地接支出条目,计算地接支出
			filters.add(Filter.eq("hyGroup", hyGroup));
			filters.add(Filter.eq("type", SupplierType.elementlocal));
			List<HyRegulateitemElement> dijieElements=hyRegulateitemElementService.findList(null,filters,null);
			filters.clear();
			for(HyRegulateitemElement element:dijieElements) {
				Map<String, Object> map = new HashMap<>();
				map.put("supplierName", element.getSupplierName()); //供应商名称
				map.put("money", element.getMoney()); //金额
				expenseTotal=expenseTotal.add(element.getMoney());
				expenseList.add(map);
			}
			
			//找出其他支出条目,计算其他支出
			filters.add(Filter.eq("hyGroup", hyGroup));
			filters.add(Filter.eq("type", SupplierType.otherexpend));
			List<HyRegulateitemElement> otherexpendElements=hyRegulateitemElementService.findList(null,filters,null);
			filters.clear();
			for(HyRegulateitemElement element:otherexpendElements) {
				Map<String, Object> map = new HashMap<>();
				map.put("supplierName", element.getSupplierName()); //供应商名称
				map.put("money", element.getMoney()); //金额
				expenseTotal=expenseTotal.add(element.getMoney());
				expenseList.add(map);
			}
			
			//计算导游服务费支出
			filters.add(Filter.eq("hyGroup", hyGroup));
			List<HyRegulateitemGuide> hyRegulateitemGuides=hyRegulateitemGuideService.findList(null,filters,null);
			filters.clear();
			BigDecimal guideExpense=BigDecimal.ZERO;
			Map<String, Object> guideMap = new HashMap<>();
			for(HyRegulateitemGuide element:hyRegulateitemGuides) {
				guideExpense=guideExpense.add(element.getYingfu());
			}
			expenseTotal=expenseTotal.add(guideExpense);
			guideMap.put("supplierName", "导游服务费");
			guideMap.put("money", guideExpense);
			expenseList.add(guideMap);
			obj.put("expenseList", expenseList); //支出详情
			obj.put("expenseTotal", expenseTotal); //支出合计
			
			//因详情页需要账号名称和部门
			obj.put("accountName", hyAdmin.getName());
			String departmentName=hyAdmin.getDepartment().getFullName();
			obj.put("departmentName", departmentName);
			json.setSuccess(true);
			json.setObj(obj);
			json.setMsg("查询详情成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	
	/**
	 * 部门经理登录查询接口
	*/
	@RequestMapping("managerList/view")
	@ResponseBody
	public Json managerListview(@DateTimeFormat(pattern="yyyy-MM-dd") Date startTime,@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime,HttpSession session)
	{
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			Set<HyAdmin> hyAdmins=department.getHyAdmins(); //找出该部门所有员工
			List<Filter> filters=new ArrayList<>();
			Map<String,Object> obj=new HashMap<>();
			List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
			BigDecimal profitTotal=BigDecimal.ZERO;
			for(HyAdmin admin:hyAdmins) {
				Map<String,Object> map=new HashMap<>();
				filters.add(Filter.eq("creator", admin));
				filters.add(Filter.eq("isInner", true));
				if(startTime!=null) {
					filters.add(new Filter("startDay", Operator.ge, DateUtil.getStartOfDay(startTime)));
				}
				if(endTime!=null) {
					filters.add(new Filter("startDay", Operator.le, DateUtil.getStartOfDay(endTime)));
				}
				List<HyGroup> hyGroups=hyGroupService.findList(null,filters,null);
				filters.clear();
				BigDecimal profit=BigDecimal.ZERO;
				for(HyGroup hyGroup:hyGroups) {
					Long groupId=hyGroup.getId();
					filters.add(Filter.eq("hyGroup", groupId)); //根据团找该团的计调
					List<HyRegulate> hyRegulates=hyRegulateService.findList(null,filters,null);
					filters.clear();
					if(hyRegulates.size()>0) {
						HyRegulate hyRegulate=hyRegulates.get(0); //得到计调报账
						if(hyRegulate.getStatus()==2) { //计调报账申请通过
							filters.add(Filter.eq("groupId", hyGroup.getId())); 
							//在单团核算表中找出该团的总收入,总支出和利润
							List<RegulategroupAccount> regulategroupAccounts=regulategroupAccountService.findList(null,filters,null);
							filters.clear();
							if(regulategroupAccounts.size()>0) {
								//先按照一团一核算
								profit=profit.add(regulategroupAccounts.get(0).getProfit()); //得到该团的利润
							}
						}
					}
				}
				profitTotal=profitTotal.add(profit);
				if(profit!=BigDecimal.ZERO) {
					map.put("employeeName", admin.getName());  //员工姓名
					map.put("employeeAccount",admin.getUsername());
					map.put("profit", profit);  //利润
					list.add(map);
				}
			}
			obj.put("employeeList", list);
			obj.put("profitTotal", profitTotal);
			json.setSuccess(true);
			json.setObj(obj);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	public class InnerSupplierProfitManager{
		private String employeeName;
		private String profit;
		public String getEmployeeName() {
			return employeeName;
		}
		public void setEmployeeName(String employeeName) {
			this.employeeName = employeeName;
		}
		public String getProfit() {
			return profit;
		}
		public void setProfit(String profit) {
			this.profit = profit;
		}
	}
	
	/**
	 * 部门经理登录导出excel接口
	*/
	@RequestMapping("managerList/excel")
	public String managerExcel(@DateTimeFormat(pattern="yyyy-MM-dd") Date startTime,@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime,
			HttpSession session,HttpServletRequest request, HttpServletResponse response)
	{
		try {

			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			Set<HyAdmin> hyAdmins=department.getHyAdmins(); //找出该部门所有员工
			List<Filter> filters=new ArrayList<>();
			List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
			List<InnerSupplierProfitManager> results=new ArrayList<>();
			BigDecimal profitTotal=BigDecimal.ZERO;
			for(HyAdmin admin:hyAdmins) {
				InnerSupplierProfitManager map=new InnerSupplierProfitManager();
				filters.add(Filter.eq("creator", admin));
				filters.add(Filter.eq("isInner", true));
				if(startTime!=null) {
					filters.add(new Filter("startDay", Operator.ge, DateUtil.getStartOfDay(startTime)));
				}
				if(endTime!=null) {
					filters.add(new Filter("startDay", Operator.le, DateUtil.getStartOfDay(endTime)));
				}
				List<HyGroup> hyGroups=hyGroupService.findList(null,filters,null);
				filters.clear();
				BigDecimal profit=BigDecimal.ZERO;
				for(HyGroup hyGroup:hyGroups) {
					Long groupId=hyGroup.getId();
					filters.add(Filter.eq("hyGroup", groupId)); //根据团找该团的计调
					List<HyRegulate> hyRegulates=hyRegulateService.findList(null,filters,null);
					filters.clear();
					if(hyRegulates.size()>0) {
						HyRegulate hyRegulate=hyRegulates.get(0); //得到计调报账
						if(hyRegulate.getStatus()==2) { //计调报账申请通过
							filters.add(Filter.eq("groupId", hyGroup.getId())); 
							//在单团核算表中找出该团的总收入,总支出和利润
							List<RegulategroupAccount> regulategroupAccounts=regulategroupAccountService.findList(null,filters,null);
							filters.clear();
							if(regulategroupAccounts.size()>0) {
								//先按照一团一核算
								profit=profit.add(regulategroupAccounts.get(0).getProfit()); //得到该团的利润
							}
						}
					}
				}
				/**只有利润不为0才返回*/
				if(profit!=BigDecimal.ZERO) {
					map.setEmployeeName(admin.getName());  //员工姓名
					map.setProfit(profit.toString());  //利润
					profitTotal=profitTotal.add(profit);
					results.add(map);
				}
			}
			InnerSupplierProfitManager total=new InnerSupplierProfitManager();
			total.setEmployeeName("小计");
			total.setProfit(profitTotal.toString());
			results.add(total);
			// 生成Excel表标题
			StringBuffer sb2 = new StringBuffer();
			if(startTime!=null) {	
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				sb2.append(df.format(startTime));
				sb2.append("-");
			}			
			if(endTime!=null) {
				sb2.append("-");
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				sb2.append(df.format(endTime));
			}
			sb2.append("线路产品利润统计");
			String fileName = "内部供应商利润报表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "innerSupplierProfitManager.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);		
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

}
