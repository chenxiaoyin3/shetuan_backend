package com.hongyu.controller.lbc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.w3c.dom.ls.LSInput;

import com.grain.controller.BaseController;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Pageable;
import com.hongyu.controller.lbc.AnnualSalesVolumeContrastController.Wrap;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.entity.Store;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.SpecialtyCategoryService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.StoreService;
import com.hongyu.util.ArrayHandler;




@Controller
@RequestMapping("admin/business/supplier_business")
public class SupplierBusinessStatisticsController {
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "hyRoleServiceImpl")
	HyRoleService hyRoleService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;
	
	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderService;
	
	@Resource(name = "businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;
	
	@Resource(name = "specialtyServiceImpl")
	SpecialtyService specialtyService;
	
	@Resource(name = "specialtyCategoryServiceImpl")
	SpecialtyCategoryService specialtyCategoryService;
	
	@Resource(name = "hyDepartmentModelServiceImpl")
	HyDepartmentModelService hyDepartmentModelService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	BaseController baseController = new BaseController();
	
	
	public static class Wrap1{
		String rank = "";
		String name = "";
		int supplierNum = 0;
		int orderNum = 0;
		int customerNum = 0;
		BigDecimal moneyNum = new BigDecimal("0");
		
		
		public String getRank() {
			return rank;
		}
		public void setRank(String rank) {
			this.rank = rank;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getSupplierNum() {
			return supplierNum;
		}
		public void setSupplierNum(int supplierNum) {
			this.supplierNum = supplierNum;
		}
		public int getOrderNum() {
			return orderNum;
		}
		public void setOrderNum(int orderNum) {
			this.orderNum = orderNum;
		}
		public int getCustomerNum() {
			return customerNum;
		}
		public void setCustomerNum(int customerNum) {
			this.customerNum = customerNum;
		}
		public BigDecimal getMoneyNum() {
			return moneyNum;
		}
		public void setMoneyNum(BigDecimal moneyNum) {
			this.moneyNum = moneyNum;
		}
		
	}
	
	public static class Wrap2{
		String month = "";
		int totalOrderNum = 0;
		int totalCustomerNum = 0;
		BigDecimal totalMoneyNum = new BigDecimal("0");
		public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public int getTotalOrderNum() {
			return totalOrderNum;
		}
		public void setTotalOrderNum(int totalOrderNum) {
			this.totalOrderNum = totalOrderNum;
		}
		public int getTotalCustomerNum() {
			return totalCustomerNum;
		}
		public void setTotalCustomerNum(int totalCustomerNum) {
			this.totalCustomerNum = totalCustomerNum;
		}
		public BigDecimal getTotalMoneyNum() {
			return totalMoneyNum;
		}
		public void setTotalMoneyNum(BigDecimal totalMoneyNum) {
			this.totalMoneyNum = totalMoneyNum;
		}
		
	}
	
	
	public static class Wrap{
		BigDecimal saleJan = new BigDecimal(0);
		BigDecimal saleFeb = new BigDecimal(0);
		BigDecimal saleMar = new BigDecimal(0);
		BigDecimal saleApr = new BigDecimal(0);
		BigDecimal saleMay = new BigDecimal(0);
		BigDecimal saleJun = new BigDecimal(0);
		BigDecimal saleJul = new BigDecimal(0);
		BigDecimal saleAug = new BigDecimal(0);
		BigDecimal saleSep = new BigDecimal(0);
		BigDecimal saleOct = new BigDecimal(0);
		BigDecimal saleNov = new BigDecimal(0);
		BigDecimal saleDec = new BigDecimal(0);
		Integer saleTime = new Integer(0);
		public Integer getSaleTime() {
			return saleTime;
		}
		public void setSaleTime(Integer saleTime) {
			this.saleTime = saleTime;
		}
		public BigDecimal getSaleJan() {
			return saleJan;
		}
		public void setSaleJan(BigDecimal saleJan) {
			this.saleJan = saleJan;
		}
		public BigDecimal getSaleFeb() {
			return saleFeb;
		}
		public void setSaleFeb(BigDecimal saleFeb) {
			this.saleFeb = saleFeb;
		}
		public BigDecimal getSaleMar() {
			return saleMar;
		}
		public void setSaleMar(BigDecimal saleMar) {
			this.saleMar = saleMar;
		}
		public BigDecimal getSaleApr() {
			return saleApr;
		}
		public void setSaleApr(BigDecimal saleApr) {
			this.saleApr = saleApr;
		}
		public BigDecimal getSaleMay() {
			return saleMay;
		}
		public void setSaleMay(BigDecimal saleMay) {
			this.saleMay = saleMay;
		}
		public BigDecimal getSaleJun() {
			return saleJun;
		}
		public void setSaleJun(BigDecimal saleJun) {
			this.saleJun = saleJun;
		}
		public BigDecimal getSaleJul() {
			return saleJul;
		}
		public void setSaleJul(BigDecimal saleJul) {
			this.saleJul = saleJul;
		}
		public BigDecimal getSaleAug() {
			return saleAug;
		}
		public void setSaleAug(BigDecimal saleAug) {
			this.saleAug = saleAug;
		}
		public BigDecimal getSaleSep() {
			return saleSep;
		}
		public void setSaleSep(BigDecimal saleSep) {
			this.saleSep = saleSep;
		}
		public BigDecimal getSaleOct() {
			return saleOct;
		}
		public void setSaleOct(BigDecimal saleOct) {
			this.saleOct = saleOct;
		}
		public BigDecimal getSaleNov() {
			return saleNov;
		}
		public void setSaleNov(BigDecimal saleNov) {
			this.saleNov = saleNov;
		}
		public BigDecimal getSaleDec() {
			return saleDec;
		}
		public void setSaleDec(BigDecimal saleDec) {
			this.saleDec = saleDec;
		}
		
		
		
	}
	
//	map2.put("totalSupplierNum", totalSupplierNum);
//	map2.put("totalOrderNum", totalOrderNum);
//	map2.put("totalCustomerNum", totalCustomerNum);
//	map2.put("totalMoneyNum", totalMoneyNum);
	
	public static class Wrap3{
		String sort;
		String supplierName;
		Integer orderNum;
		Integer peopleNum;
		BigDecimal money;
		public String getSort() {
			return sort;
		}
		public void setSort(String sort) {
			this.sort = sort;
		}
		public String getSupplierName() {
			return supplierName;
		}
		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}
		public Integer getOrderNum() {
			return orderNum;
		}
		public void setOrderNum(Integer orderNum) {
			this.orderNum = orderNum;
		}
		public Integer getPeopleNum() {
			return peopleNum;
		}
		public void setPeopleNum(Integer peopleNum) {
			this.peopleNum = peopleNum;
		}
		public BigDecimal getMoney() {
			return money;
		}
		public void setMoney(BigDecimal money) {
			this.money = money;
		}
		
		
	}
	
	public static class Wrap4 {
		String orderNum;
		String productId;
		String productName;
		String startDate;
		String createDate;
		String contactor;
		Integer people;
		String store;
		String operator;
		BigDecimal orderMoney;
		String payTime;
		public String getOrderNum() {
			return orderNum;
		}
		public void setOrderNum(String orderNum) {
			this.orderNum = orderNum;
		}
		public String getProductId() {
			return productId;
		}
		public void setProductId(String productId) {
			this.productId = productId;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public String getStartDate() {
			return startDate;
		}
		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}
		public String getCreateDate() {
			return createDate;
		}
		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
		public String getContactor() {
			return contactor;
		}
		public void setContactor(String contactor) {
			this.contactor = contactor;
		}
		public Integer getPeople() {
			return people;
		}
		public void setPeople(Integer people) {
			this.people = people;
		}
		public String getStore() {
			return store;
		}
		public void setStore(String store) {
			this.store = store;
		}
		public String getOperator() {
			return operator;
		}
		public void setOperator(String operator) {
			this.operator = operator;
		}
		public BigDecimal getOrderMoney() {
			return orderMoney;
		}
		public void setOrderMoney(BigDecimal orderMoney) {
			this.orderMoney = orderMoney;
		}
		public String getPayTime() {
			return payTime;
		}
		public void setPayTime(String payTime) {
			this.payTime = payTime;
		}
		
	}
	
	@RequestMapping(value="/store_list")
	@ResponseBody
	public Json StoreList(HttpSession session) {
		Json json = new Json();
		try {
			List<Map<String, Object>> list = new ArrayList<>();
			List<Store> storeList = storeService.findAll();
			for(Store store : storeList) {
				Map<String, Object> map = new HashMap<>();
				map.put("storeId", store.getId());
				map.put("storeName", store.getStoreName());
				list.add(map);
			}
			
			
			json.setSuccess(true);
			json.setMsg("查询门店列表成功");
			json.setObj(list);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询门店列表失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		return json;
	}
	
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value="/caigoubu_manager_view")
	@ResponseBody
	public Json SupplierStatistics1(HttpSession session, @DateTimeFormat(pattern="yyyy-MM") Date startDate, @DateTimeFormat(pattern="yyyy-MM") Date endDate, Integer type){
		Json json = new Json();
		try {

			List<HashMap<String, Object>> list = new ArrayList<>();
			
			
			
			//年份，月份，每月营业额
			//Map<Integer, ArrayList<BigDecimal>> yearMap = new HashMap<Integer, ArrayList<BigDecimal>>();
			
//			int pageRow = pageable.getRows();
//			int pageNumber = pageable.getPage();
			//按订单数 0  按收客人数 1  按总计金额 2
			if(type == null) {
				type = 0;
			}
			
			//对于每一个采购部员工，如果管理供应商，则有，否则没有
			HyDepartmentModel hyDepartmentModel = hyDepartmentModelService.find("总公司采购部");
			
			List<Filter> departmentFilters = new ArrayList<>();
			departmentFilters.add(Filter.eq("hyDepartmentModel", hyDepartmentModel));
			
			//部门是总公司采购部
			Department department = departmentService.findList(null, departmentFilters, null).get(0);
			
			
			
			
			//1015为总公司采购部员工
			//HyRole hyRole = hyRoleService.find(Long.valueOf("1015"));
			//得到所有的采购部员工
			List<Filter> adminFilters = new ArrayList<>();
			adminFilters.add(Filter.eq("department", department));

			List<HyAdmin> caigoubuWorkers = hyAdminService.findList(null, adminFilters, null);
			int totalSupplierNum = 0;
			int totalOrderNum = 0;
			int totalCustomerNum = 0;
			BigDecimal totalMoneyNum = new BigDecimal("0");
			
			List<HyAdmin> caigoubuYuanGongs = new ArrayList<>();   
			
			//筛选采购部员工
			for(int i = 0; i < caigoubuWorkers.size();i++) {
				if (caigoubuWorkers.get(i).getRole().getName().contains("员工")) {
					caigoubuYuanGongs.add(caigoubuWorkers.get(i));
				}
			}
			
			//对每一个采购部员工
			for(HyAdmin caigoubuWorker : caigoubuYuanGongs) {
				HashMap<String,Object> map=new HashMap<String,Object>();
				//计算管理供应商的数量
				List<Filter> supplierFilter = new ArrayList<>();
				supplierFilter.add(Filter.eq("operator", caigoubuWorker));
				
				map.put("caigoubuWorker", caigoubuWorker.getName());
				
				List<HySupplier> hySuppliers = hySupplierService.findList(null,supplierFilter,null);
				//得到了所有供应商的数量
				if(hySuppliers != null && hySuppliers.size() > 0) {
					totalSupplierNum += hySuppliers.size();
					map.put("supplierNum", hySuppliers.size());
					//对每一个供应商，查找对应的订单
					
					//找到所有供应商的合同
					List<Filter> contractFilter = new ArrayList<>();
					contractFilter.add(Filter.in("hySupplier", hySuppliers));
					List<HySupplierContract> hySupplierContracts = hySupplierContractService.findList(null, contractFilter, null);
					if(hySupplierContracts == null || hySupplierContracts.size() == 0) {
						map.put("orderNum", 0);
						map.put("customerNum", 0);
						map.put("moneyNum", new BigDecimal("0"));
						list.add(map);
						continue;
					}
					List<HyAdmin> hyAdmins = new ArrayList<>();
					//对于所有合同，得到负责人
					for(int i = 0; i < hySupplierContracts.size(); i++) {
						hyAdmins.add(hySupplierContracts.get(i).getLiable());
					}
					//list转set 再转回list
					Set<HyAdmin> adminSet = new HashSet<>(hyAdmins);
					List<HyAdmin> adminList = new ArrayList<>(adminSet);
					
					
					List<Filter> orderFilter = new ArrayList<>();
					orderFilter.add(Filter.in("supplier", adminList));
					//订单过滤器中加入时间过滤
					if(startDate!=null) {
						orderFilter.add(Filter.ge("createtime", startDate));
					}
					if(endDate!=null) {
						orderFilter.add(Filter.le("createtime", endDate));
					}
					
					//线路订单 已支付
					orderFilter.add(Filter.eq("type", 1));
					orderFilter.add(Filter.eq("paystatus", 1));
	
						
						
					List<HyOrder> hyOrders = hyOrderService.findList(null, orderFilter, null);
					if(hyOrders == null || hyOrders.size() == 0) {
						map.put("orderNum", 0);
						map.put("customerNum", 0);
						map.put("moneyNum", new BigDecimal("0"));
					}
					else {
						map.put("orderNum", hyOrders.size());
						totalOrderNum += hyOrders.size();
						BigDecimal moneyNum = new BigDecimal("0");
						//对每一个订单，查找对应的所有客户和金额
						//总计金额计算
						int customerNum = 0;
						for(HyOrder hyOrder : hyOrders) {
							//moneyNum = moneyNum.add(hyOrder.getJiesuanMoney1());
							if(hyOrder.getJiesuanMoney1() != null) {
								moneyNum = moneyNum.add(hyOrder.getJiesuanMoney1());
							}
							//减去退款金额
							if(hyOrder.getJiesuanTuikuan() != null) {
								moneyNum = moneyNum.subtract((hyOrder.getJiesuanTuikuan()));
							}
							
							List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
							if(hyOrderItems == null || hyOrderItems.size() == 0) {
								//map.put("customerNum", 0);
							}
							else {
								for(HyOrderItem hyOrderItem : hyOrderItems) {
									List<HyOrderCustomer> hyOrderCustomers = hyOrderItem.getHyOrderCustomers();
									if(hyOrderCustomers == null || hyOrderCustomers.size() == 0) {
										//map.put("customerNum", 0);
									}
									else {
										//map.put("customerNum", hyOrderCustomers.size());
										customerNum += hyOrderCustomers.size();
										totalCustomerNum += hyOrderCustomers.size();
									}
								}
								
							}
						}
						map.put("customerNum", customerNum);
						map.put("moneyNum", moneyNum);
						totalMoneyNum = totalMoneyNum.add(moneyNum);
					}
						
						
					
					//得到应该插入的位置 插入排序就行
					int insertPos = -1;
					for(int i = 0; i < list.size(); i++) {
						//按订单数排序
						if(type == 0) {
							if((int)(list.get(i).get("orderNum")) < (int)(map.get("orderNum"))) {
								insertPos = i;
								break;
							}
						}
						//按收客人数排序
						else if(type == 1) {
							if((int)(list.get(i).get("customerNum")) < (int)(map.get("customerNum"))) {
								insertPos = i;
								break;
							}
						}
						//按总计金额排序
						else {
							if( ((BigDecimal)(list.get(i).get("moneyNum"))).compareTo( (BigDecimal)(map.get("moneyNum"))) == -1) {
								insertPos = i;
								break;
							}
						}
						
					}
					//插入
					if(insertPos!=-1) {
						list.add(insertPos, map);
					}
					else {
						list.add(map);
					}
					

				}
				else {
					map.put("caigoubuWorker", caigoubuWorker.getName());
					map.put("supplierNum", 0);
					map.put("orderNum", 0);
					map.put("customerNum", 0);
					map.put("moneyNum", new BigDecimal("0"));
					//插入到list最后一个
					list.add(map);
				}
				
				
			}	
			//list最后一行为总计
			HashMap<String,Object> map=new HashMap<String,Object>();
			if(caigoubuYuanGongs == null || caigoubuYuanGongs.size() == 0) {
				map.put("totalCaigoubuWorker", 0);
				map.put("totalSupplierNum", 0);
				map.put("totalOrderNum", 0);
				map.put("totalCustomerNum", 0);
				map.put("totalMoneyNum", new BigDecimal("0"));
			}
			else {
				map.put("totalCaigoubuWorker", caigoubuYuanGongs.size());
				map.put("totalSupplierNum", totalSupplierNum);
				map.put("totalOrderNum", totalOrderNum);
				map.put("totalCustomerNum", totalCustomerNum);
				map.put("totalMoneyNum", totalMoneyNum);
			}
			
			list.add(map);
			
			Map<String,Object> map1=new HashMap<String,Object>();
			
			map1.put("rows", list);
		    map1.put("total",list.size());
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map1);

			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		
		return json;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value="/shichangbu_manager_view")
	@ResponseBody
	public Json SupplierStatistics2(HttpSession session, @DateTimeFormat(pattern="yyyy-MM") Date startDate, @DateTimeFormat(pattern="yyyy-MM") Date endDate){
		Json json = new Json();
		try {

			List<HashMap<String, Object>> list = new ArrayList<>();
			
			if(startDate == null || endDate == null) {
				Calendar calendar = Calendar.getInstance();
				//得到当月的
				calendar.setTime(new Date());
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				startDate = calendar.getTime();
				
				calendar.add(Calendar.MONTH, 1);
				endDate = calendar.getTime();
			}
			//根据开始年月和结束年月求出总月份数和开始月份数
			//总月份数
			int monthNum = MyCalendar.monthsBetweenDate(startDate, endDate);
			
			Date now = startDate;
			Date next = MyCalendar.monthAfter(now);
			
			
			//对于每一个采购部员工，如果管理供应商，则有，否则没有
			
			//对于每一个采购部员工，如果管理供应商，则有，否则没有
			HyDepartmentModel hyDepartmentModel = hyDepartmentModelService.find("总公司采购部");
			
			List<Filter> departmentFilters = new ArrayList<>();
			departmentFilters.add(Filter.eq("hyDepartmentModel", hyDepartmentModel));
			
			//部门是总公司采购部
			Department department = departmentService.findList(null, departmentFilters, null).get(0);
			
			
			
			
			//1015为总公司采购部员工
			//HyRole hyRole = hyRoleService.find(Long.valueOf("1015"));
			//得到所有的采购部员工和经理
			List<Filter> adminFilters = new ArrayList<>();
			adminFilters.add(Filter.eq("department", department));

			List<HyAdmin> caigoubuWorkers = hyAdminService.findList(null, adminFilters, null);

			
//			List<HyAdmin> caigoubuYuanGongs = new ArrayList<>(); 
//			
//			//筛选采购部员工
//			for(int i = 0; i < caigoubuWorkers.size();i++) {
//				if (caigoubuWorkers.get(i).getRole().getName().contains("员工")) {
//					caigoubuYuanGongs.add(caigoubuWorkers.get(i));
//				}
//			}

			
			int all_orders = 0;
			int all_customers = 0;
			BigDecimal all_money = new BigDecimal("0");
			
			for(int i = 0; i < monthNum; i++ ) {
				int totalOrderNum = 0;
				int totalCustomerNum = 0;
				BigDecimal totalMoneyNum = new BigDecimal("0");
				
				
				HashMap<String,Object> map=new HashMap<String,Object>();
				//对每一个采购部员工
				for(HyAdmin caigoubuWorker : caigoubuWorkers) {
					
					//计算管理供应商的数量
					List<Filter> supplierFilter = new ArrayList<>();
					supplierFilter.add(Filter.eq("operator", caigoubuWorker));
					List<HySupplier> hySuppliers = hySupplierService.findList(null,supplierFilter,null);
					//得到了所有供应商的数量
					if(hySuppliers != null && hySuppliers.size() > 0) {
						//对每一个供应商，查找对应的订单
						//对每一个供应商，查找对应的订单
						
						//找到所有供应商的合同
						List<Filter> contractFilter = new ArrayList<>();
						contractFilter.add(Filter.in("hySupplier", hySuppliers));
						List<HySupplierContract> hySupplierContracts = hySupplierContractService.findList(null, contractFilter, null);
						if(hySupplierContracts == null || hySupplierContracts.size() == 0) {
							map.put("totalOrderNum", 0);
							map.put("totalCustomerNum", 0);
							map.put("totalMoneyNum", new BigDecimal("0"));
							list.add(map);
							continue;
						}
						List<HyAdmin> hyAdmins = new ArrayList<>();
						//对于所有合同，得到负责人
						for(int j = 0; j < hySupplierContracts.size(); j++) {
							hyAdmins.add(hySupplierContracts.get(j).getLiable());
						}
						//list转set 再转回list
						Set<HyAdmin> adminSet = new HashSet<>(hyAdmins);
						List<HyAdmin> adminList = new ArrayList<>(adminSet);
						
						
						List<Filter> orderFilter = new ArrayList<>();
						orderFilter.add(Filter.in("supplier", adminList));
						
						//订单过滤器中加入时间过滤
						orderFilter.add(Filter.ge("fatuandate", now));
						orderFilter.add(Filter.le("fatuandate", next));
						
						//支付状态为已支付
						//线路订单 已支付
						orderFilter.add(Filter.eq("type", 1));
						orderFilter.add(Filter.eq("paystatus", 1));
						
						List<HyOrder> hyOrders = hyOrderService.findList(null, orderFilter, null);
						if(hyOrders == null || hyOrders.size() == 0) {
							
						}
						else {
							totalOrderNum += hyOrders.size();
							BigDecimal moneyNum = new BigDecimal("0");
							//对每一个订单，查找对应的所有客户和金额
							//总计金额计算
							//int customerNum = 0;
							for(HyOrder hyOrder : hyOrders) {
								if(hyOrder.getJiesuanMoney1() != null) {
									moneyNum = moneyNum.add(hyOrder.getJiesuanMoney1());
								}
								//减去退款金额
								if(hyOrder.getJiesuanTuikuan() != null) {
									moneyNum = moneyNum.subtract((hyOrder.getJiesuanTuikuan()));
								}
								
								List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
								if(hyOrderItems == null || hyOrderItems.size() == 0) {
									
								}
								else {
									for(HyOrderItem hyOrderItem : hyOrderItems) {
										List<HyOrderCustomer> hyOrderCustomers = hyOrderItem.getHyOrderCustomers();
										if(hyOrderCustomers == null || hyOrderCustomers.size() == 0) {
											
										}
										else {
											totalCustomerNum += hyOrderCustomers.size();
										}
									}
									
								}
							}
							totalMoneyNum = totalMoneyNum.add(moneyNum);
						}
						
						
						//插入
						
						all_customers += totalCustomerNum;
						all_orders += totalOrderNum;
						all_money = all_money.add(totalMoneyNum);
								
					}
					else {
						
						//插入到list最后一个
						
					}
					
				}
				SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM");
				
				
				map.put("month",bartDateFormat.format(now));
				map.put("totalOrderNum", totalOrderNum);
				map.put("totalCustomerNum", totalCustomerNum);
				map.put("totalMoneyNum", totalMoneyNum);
				list.add(map);
				
				now = next;
				next = MyCalendar.monthAfter(now);
				
			}
			
			
			//list最后一行为总计
			HashMap<String,Object> map=new HashMap<String,Object>();
			if(caigoubuWorkers == null || caigoubuWorkers.size() == 0) {
				map.put("all_customers", 0);
				map.put("all_orders", 0);
				map.put("all_money", new BigDecimal("0"));
			}
			else {
				map.put("all_customers", all_customers);
				map.put("all_orders", all_orders);
				map.put("all_money", all_money);
			}
			
			list.add(map);
			
//			List<HashMap<String, Object>> pageList = new ArrayList<>();
//			
//			//分页
//			for(int i = (pageNumber - 1) * pageRow; i < list.size() && i < pageNumber * pageRow; i++) {
//				pageList.add(list.get(i));	
//			}
			
			Map<String,Object> map1=new HashMap<String,Object>();
			
			map1.put("rows", list);
//		    map.put("pageNumber", pageable.getPage());
//		    map.put("pageSize", pageable.getRows());
		    map1.put("total",list.size());
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map1);

			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		
		return json;
	}
	
	@RequestMapping(value="/caigoubu_yuangong_view")
	@ResponseBody
	public Json SupplierStatistics3(HttpSession session, @DateTimeFormat(pattern="yyyy-MM") Date startDate, @DateTimeFormat(pattern="yyyy-MM") Date endDate, Integer type){
		Json json = new Json();
		try {

			List<HashMap<String, Object>> list = new ArrayList<>();
			
			
			
			//年份，月份，每月营业额
			//Map<Integer, ArrayList<BigDecimal>> yearMap = new HashMap<Integer, ArrayList<BigDecimal>>();
			
//			int pageRow = pageable.getRows();
//			int pageNumber = pageable.getPage();
			//按订单数 0  按收客人数 1  按总计金额 2
			if(type == null) {
				type = 0;
			}
			
			//对于每一个采购部员工，如果管理供应商，则有，否则没有
//			HyDepartmentModel hyDepartmentModel = hyDepartmentModelService.find("总公司采购部");
//			
//			List<Filter> departmentFilters = new ArrayList<>();
//			departmentFilters.add(Filter.eq("hyDepartmentModel", hyDepartmentModel));
			
			//部门是总公司采购部
//			Department department = departmentService.findList(null, departmentFilters, null).get(0);
			
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			
			//1015为总公司采购部员工
			//HyRole hyRole = hyRoleService.find(Long.valueOf("1015"));
			//得到所有的采购部员工
//			List<Filter> adminFilters = new ArrayList<>();
//			adminFilters.add(Filter.eq("department", department));

//			List<HyAdmin> caigoubuWorkers = hyAdminService.findList(null, adminFilters, null);
			//供应商总数量
			
			
//			List<HyAdmin> caigoubuYuanGongs = new ArrayList<>(); 
			
//			//筛选采购部员工
//			for(int i = 0; i < caigoubuWorkers.size();i++) {
//				if (caigoubuWorkers.get(i).getRole().getName().contains("员工")) {
//					caigoubuYuanGongs.add(caigoubuWorkers.get(i));
//				}
//			}
			
			//对每一个采购部员工
			//for(HyAdmin caigoubuWorker : caigoubuYuanGongs) {
			
			//查看自己管理的供应商
			List<Filter> supplierFilter = new ArrayList<>();
			supplierFilter.add(Filter.eq("operator", admin));
			
			//map.put("caigoubuWorker", caigoubuWorker.getName());
			int totalSupplierNum = 0;
			//订单总数量
			int totalOrderNum = 0;
			//总收客人数
			int totalCustomerNum = 0;
			//总金额
			BigDecimal totalMoneyNum = new BigDecimal("0");
			List<HySupplier> hySuppliers = hySupplierService.findList(null,supplierFilter,null);
			totalSupplierNum += hySuppliers.size();
			//订单数，收客人数，总计金额
			for(HySupplier hySupplier : hySuppliers) {
				
				
				//对每一个供应商
				HashMap<String, Object> map=new HashMap<String, Object>();
				map.put("supplierName", hySupplier.getSupplierName());
				//找到供应商对应的合同
				List<Filter> contractFilter = new ArrayList<>();
				contractFilter.add(Filter.eq("hySupplier", hySupplier));
				List<HySupplierContract> hySupplierContracts = hySupplierContractService.findList(null, contractFilter, null);
				if(hySupplierContracts == null || hySupplierContracts.size() == 0) {
					//订单数
					map.put("orderNum", 0);
					//收客人数
					map.put("customerNum", 0);
					//总计金额
					map.put("moneyNum", new BigDecimal("0"));
					list.add(map);
					continue;
				}
				

				List<HyAdmin> hyAdmins = new ArrayList<>();
				//对于所有合同，得到负责人
				for(int i = 0; i < hySupplierContracts.size(); i++) {
					hyAdmins.add(hySupplierContracts.get(i).getLiable());
				}
				//list转set 再转回list
				Set<HyAdmin> adminSet = new HashSet<>(hyAdmins);
				List<HyAdmin> adminList = new ArrayList<>(adminSet);
				
				
				List<Filter> orderFilter = new ArrayList<>();
				orderFilter.add(Filter.in("supplier", adminList));
				
				
				//线路订单 已支付
				orderFilter.add(Filter.eq("type", 1));
				orderFilter.add(Filter.eq("paystatus", 1));
				//订单过滤器中加入时间过滤
				if(startDate!=null) {
					orderFilter.add(Filter.ge("fatuandate", startDate));
				}
				if(endDate!=null) {
					orderFilter.add(Filter.le("fatuandate", endDate));
				}
				
				List<HyOrder> hyOrders = hyOrderService.findList(null, orderFilter, null);
				if(hyOrders == null || hyOrders.size() == 0) {
					map.put("orderNum", 0);
					map.put("customerNum", 0);
					map.put("moneyNum", new BigDecimal("0"));
				}
				else {
					map.put("orderNum", hyOrders.size());
					//总订单数增加
					totalOrderNum += hyOrders.size();
					BigDecimal moneyNum = new BigDecimal("0");
					//对每一个订单，查找对应的所有客户和金额
					//总计金额计算
					int customerNum = 0;
					for(HyOrder hyOrder : hyOrders) {
						//moneyNum = moneyNum.add(hyOrder.getJiesuanMoney1());
						if(hyOrder.getJiesuanMoney1() != null) {
							moneyNum = moneyNum.add(hyOrder.getJiesuanMoney1());
						}
						//减去退款金额
						if(hyOrder.getJiesuanTuikuan() != null) {
							moneyNum = moneyNum.subtract((hyOrder.getJiesuanTuikuan()));
						}
						
						List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
						if(hyOrderItems == null || hyOrderItems.size() == 0) {
							//map.put("customerNum", 0);
						}
						else {
							for(HyOrderItem hyOrderItem : hyOrderItems) {
								List<HyOrderCustomer> hyOrderCustomers = hyOrderItem.getHyOrderCustomers();
								if(hyOrderCustomers == null || hyOrderCustomers.size() == 0) {
									//map.put("customerNum", 0);
								}
								else {
									//map.put("customerNum", hyOrderCustomers.size());
									customerNum += hyOrderCustomers.size();
									totalCustomerNum += hyOrderCustomers.size();
								}
							}
							
						}
					}
					map.put("customerNum", customerNum);
					map.put("moneyNum", moneyNum);
					totalMoneyNum = totalMoneyNum.add(moneyNum);
				}
				int insertPos = -1;
				for(int i = 0; i < list.size(); i++) {
					//按订单数排序
					if(type == 0) {
						if((int)(list.get(i).get("orderNum")) < (int)(map.get("orderNum"))) {
							insertPos = i;
							break;
						}
					}
					//按收客人数排序
					else if(type == 1) {
						if((int)(list.get(i).get("customerNum")) < (int)(map.get("customerNum"))) {
							insertPos = i;
							break;
						}
					}
					//按总计金额排序
					else {
						if( ((BigDecimal)(list.get(i).get("moneyNum"))).compareTo( (BigDecimal)(map.get("moneyNum"))) == -1) {
							insertPos = i;
							break;
						}
					}
					
				}
				//插入
				if(insertPos!=-1) {
					list.add(insertPos, map);
				}
				else {
					list.add(map);
				}
				
			}
			
				
//
//			else {
//				map.put("caigoubuWorker", caigoubuWorker.getName());
//				map.put("supplierNum", 0);
//				map.put("orderNum", 0);
//				map.put("customerNum", 0);
//				map.put("moneyNum", new BigDecimal("0"));
//				//插入到list最后一个
//				list.add(map);
//			}
				
				
			//}	
			//list最后一行为总计
			HashMap<String,Object> map2=new HashMap<String,Object>();
			if(hySuppliers == null || hySuppliers.size() == 0) {
				map2.put("totalSupplierNum", 0);
				map2.put("totalOrderNum", 0);
				map2.put("totalCustomerNum", 0);
				map2.put("totalMoneyNum", new BigDecimal("0"));
			}
			else {
				map2.put("totalSupplierNum", totalSupplierNum);
				map2.put("totalOrderNum", totalOrderNum);
				map2.put("totalCustomerNum", totalCustomerNum);
				map2.put("totalMoneyNum", totalMoneyNum);
			}
			
			list.add(map2);
			
			
			HashMap<String,Object> map3=new HashMap<String,Object>();
			map3.put("rows", list);
		    map3.put("total",list.size());
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map3);

			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		
		return json;
	}
	
	@RequestMapping(value="/gys_view")
	@ResponseBody
	public Json SupplierStatistics4(HttpSession session, @DateTimeFormat(pattern="yyyy-MM") Date startDate, @DateTimeFormat(pattern="yyyy-MM") Date endDate, Long storeId, Integer type){
		Json json = new Json();
		try {
			//按订单数 0  按收客人数 1  按总计金额 2
			if(type == null) {
				type = 0;
			}
			
			
			
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			
			List<Map<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			
			//拿到登录人所在的部门
//			Department department = hyAdmin.getDepartment();
//			
//			Store store = storeService.findStore(hyAdmin);
			
			//拿到父账号
//			while(admin.getHyAdmin() != null) {
//				admin = admin.getHyAdmin();
//			}
//			
//			HySupplier hySupplier = null;
//			//拿到供应商
//			if(admin.getLiableContracts() != null && admin.getLiableContracts().size() != 0) {
//				Iterator<HySupplierContract> iterator = admin.getLiableContracts().iterator();
//				HySupplierContract hySupplierContract = iterator.next();
//				hySupplier = hySupplierContract.getHySupplier();
//			}
//			if(hySupplier == null) {
//				json.setMsg("父账号不是供应商");
//				json.setSuccess(false);
//				return json;
//			}
			//Long supplierId = hySupplier.getId();
			
			String[] attrs = new String[]{
					"orderId","productId","productName","groupStartTime","orderCreateTime","contactor","peopleNum","storeName","operatorName","orderMoney","orderPayTime","orderRefundMoney",
			};
			
			//订单type 2 认购门票
			
			StringBuilder totalSb = new StringBuilder("select sum(hyorder.people)");
			//StringBuilder sumSb = new StringBuilder("select sum(order_money)");
			StringBuilder pageSb = new StringBuilder("select distinct hyorder.order_number, hyline.pn, hygroup.group_line_name, hygroup.start_day, hyorder.createtime,"
					+ " hyorder.contact, hyorder.people, store.store_name, hyadmin.name, hyorder.jiesuan_money1, hyorder.pay_time, hyorder.jiesuan_tuikuan");
			StringBuilder sb = new StringBuilder(" from hy_order hyorder, hy_admin hyadmin, hy_group hygroup, hy_store store, hy_order_item hyorderitem, hy_line hyline"
					+ " where hyline.id=hygroup.line and hyorder.operator_id=hyadmin.username and hyorder.group_id=hygroup.id and hyorderitem.order_id=hyorder.id and hyorder.store_id=store.id and hyorderitem.product_id=hygroup.id");
			//已支付 门店下单 线路订单
			sb.append(" and hyorder.type=1 and hyorder.source=0 and hyorder.paystatus=1 and hyorder.status=3 and hyorder.supplier='"+admin.getUsername()+"'");
			
			System.out.println("数据库语句为" + sb);
			//范围是总公司的能看到 and 和 or 语句结合 加括号
			
			if(storeId != null){
				sb.append(" and hyorder.store_id="+storeId+" and store.id="+storeId);
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			
			if(startDate != null){
				calendar.setTime(startDate);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				startDate = calendar.getTime();
				sb.append(" and hygroup.start_day >= '"+ sdf.format(startDate)+"'");
			}
			if(endDate != null) {
				//所在部门是分公司
				calendar.setTime(endDate);
				//增加一个月
				calendar.add(Calendar.MONTH, 1);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				endDate = calendar.getTime();
				sb.append(" and hygroup.start_day <= '"+ sdf.format(endDate)+"'");
			}
			
			
			
//			if(hyAdmins!=null && !hyAdmins.isEmpty()){
//				List<String> adminStrArr = new ArrayList<>();
//				for(HyAdmin hyAdmin:hyAdmins){
//					adminStrArr.add("'"+hyAdmin.getUsername()+"'");
//					
//				}
//				String adminStr = String.join(",",adminStrArr);
//				sb.append(" and o1.operator_id in ("+adminStr+")");
//			}
			

			List totals = hyOrderService.statis(totalSb.append(sb).toString());
			Integer total = null;
			if(totals == null || totals.size() == 0 || totals.get(0) == null) {
				total = 0;
			}
			else {
				total = ((BigDecimal)totals.get(0)).intValue();
			}
			
			
			sb.append(" order by hyorder.createtime desc");
			
			System.out.println("数据库有条件的语句为" + sb);
			
			//Integer sqlStart = (pageable.getPage()-1)*pageable.getRows();
			//Integer sqlEnd = pageable.getPage()*pageable.getRows();
			//sb.append(" limit "+sqlStart+","+sqlEnd);
			
			//sb.append(" limit "+ count);
			
			//有效
			//filters.add(Filter.eq("isValid", 1));
//			List<Order> orders = new ArrayList<Order>();
//			orders.add(Order.desc("createTime"));
			
			List<Object[]> objs = hyOrderService.statis(pageSb.append(sb).toString());
			
			BigDecimal totalMoney = new BigDecimal(0);
			for(Object[] obj : objs){
				Map<String, Object> map1 = ArrayHandler.toMap(attrs, obj);
				
				BigDecimal orderMoney = (BigDecimal)obj[9];
				if(obj[11]!=null) {
					BigDecimal orderRefundMoney = (BigDecimal)obj[11];
					orderMoney = orderMoney.subtract(orderRefundMoney);
					map1.put("rOrderMoney", orderMoney);
				}
				else {
					map1.put("rOrderMoney", orderMoney);
				}
				totalMoney = totalMoney.add(orderMoney);
				
				list.add(map1);
			}			
			
			Map<String, Object> map2 = new HashMap<>();
			map2.put("totalMoney", totalMoney);
			map2.put("totalPeopleNum", total);
			list.add(map2);
	
			map.put("rows", list);
			
			
			
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);

			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		
		return json;
	}
	
	
	//导出excel
	@RequestMapping(value="/get_excel_caigoubu")
	@ResponseBody
	public Json export2Excel1(HttpServletRequest request,
			HttpServletResponse response, String fileName,
			String tableTitle, String configFile,
			HttpSession session, @DateTimeFormat(pattern="yyyy-MM") Date startDate, @DateTimeFormat(pattern="yyyy-MM") Date endDate, 
			Integer type) {
		
		Json json = new Json();
		
		try {
			
			List<HashMap<String, Object>> list = new ArrayList<>();
			
			
			
			//年份，月份，每月营业额
			//Map<Integer, ArrayList<BigDecimal>> yearMap = new HashMap<Integer, ArrayList<BigDecimal>>();
			
//			int pageRow = pageable.getRows();
//			int pageNumber = pageable.getPage();
			//按订单数 0  按收客人数 1  按总计金额 2
			if(type == null) {
				type = 0;
			}
			
			//对于每一个采购部员工，如果管理供应商，则有，否则没有
			
			//对于每一个采购部员工，如果管理供应商，则有，否则没有
			HyDepartmentModel hyDepartmentModel = hyDepartmentModelService.find("总公司采购部");
			
			List<Filter> departmentFilters = new ArrayList<>();
			departmentFilters.add(Filter.eq("hyDepartmentModel", hyDepartmentModel));
			
			//部门是总公司采购部
			Department department = departmentService.findList(null, departmentFilters, null).get(0);
			
			
			
			
			//1015为总公司采购部员工
			//HyRole hyRole = hyRoleService.find(Long.valueOf("1015"));
			//得到所有的采购部员工
			List<Filter> adminFilters = new ArrayList<>();
			adminFilters.add(Filter.eq("department", department));

			List<HyAdmin> caigoubuWorkers = hyAdminService.findList(null, adminFilters, null);
			int totalSupplierNum = 0;
			int totalOrderNum = 0;
			int totalCustomerNum = 0;
			BigDecimal totalMoneyNum = new BigDecimal("0");
			
			List<HyAdmin> caigoubuYuanGongs = new ArrayList<>(); 
			
			//筛选采购部员工
			for(int i = 0; i < caigoubuWorkers.size();i++) {
				if (caigoubuWorkers.get(i).getRole().getName().contains("员工")) {
					caigoubuYuanGongs.add(caigoubuWorkers.get(i));
				}
			}

			//对每一个采购部员工
			for(HyAdmin caigoubuWorker : caigoubuYuanGongs) {
				HashMap<String,Object> map=new HashMap<String,Object>();
				//计算管理供应商的数量
				List<Filter> supplierFilter = new ArrayList<>();
				supplierFilter.add(Filter.eq("operator", caigoubuWorker));
				
				map.put("caigoubuWorker", caigoubuWorker.getName());
				
				List<HySupplier> hySuppliers = hySupplierService.findList(null,supplierFilter,null);
				//得到了所有供应商的数量
				if(hySuppliers != null && hySuppliers.size() > 0) {
					totalSupplierNum += hySuppliers.size();
					map.put("supplierNum", hySuppliers.size());
					//对每一个供应商，查找对应的订单
					
					//找到所有供应商的合同
					List<Filter> contractFilter = new ArrayList<>();
					contractFilter.add(Filter.in("hySupplier", hySuppliers));
					List<HySupplierContract> hySupplierContracts = hySupplierContractService.findList(null, contractFilter, null);
					if(hySupplierContracts == null || hySupplierContracts.size() == 0) {
						map.put("orderNum", 0);
						map.put("customerNum", 0);
						map.put("moneyNum", new BigDecimal("0"));
						list.add(map);
						continue;
					}
					List<HyAdmin> hyAdmins = new ArrayList<>();
					//对于所有合同，得到负责人
					for(int i = 0; i < hySupplierContracts.size(); i++) {
						hyAdmins.add(hySupplierContracts.get(i).getLiable());
					}
					//list转set 再转回list
					Set<HyAdmin> adminSet = new HashSet<>(hyAdmins);
					List<HyAdmin> adminList = new ArrayList<>(adminSet);
					
					
					List<Filter> orderFilter = new ArrayList<>();
					orderFilter.add(Filter.in("supplier", adminList));
					//订单过滤器中加入时间过滤
					if(startDate!=null) {
						orderFilter.add(Filter.ge("fatuandate", startDate));
					}
					if(endDate!=null) {
						orderFilter.add(Filter.le("fatuandate", endDate));
					}
					
					//线路订单 已支付
					orderFilter.add(Filter.eq("type", 1));
					orderFilter.add(Filter.eq("paystatus", 1));
	
						
						
					List<HyOrder> hyOrders = hyOrderService.findList(null, orderFilter, null);
					if(hyOrders == null || hyOrders.size() == 0) {
						map.put("orderNum", 0);
						map.put("customerNum", 0);
						map.put("moneyNum", new BigDecimal("0"));
					}
					else {
						map.put("orderNum", hyOrders.size());
						totalOrderNum += hyOrders.size();
						BigDecimal moneyNum = new BigDecimal("0");
						//对每一个订单，查找对应的所有客户和金额
						//总计金额计算
						int customerNum = 0;
						for(HyOrder hyOrder : hyOrders) {
							if(hyOrder.getJiesuanMoney1() != null) {
								moneyNum = moneyNum.add(hyOrder.getJiesuanMoney1());
							}
							//减去退款金额
							if(hyOrder.getJiesuanTuikuan() != null) {
								moneyNum = moneyNum.subtract((hyOrder.getJiesuanTuikuan()));
							}
							//moneyNum = moneyNum.add(hyOrder.getJiesuanMoney1());
							List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
							if(hyOrderItems == null || hyOrderItems.size() == 0) {
								//map.put("customerNum", 0);
							}
							else {
								for(HyOrderItem hyOrderItem : hyOrderItems) {
									List<HyOrderCustomer> hyOrderCustomers = hyOrderItem.getHyOrderCustomers();
									if(hyOrderCustomers == null || hyOrderCustomers.size() == 0) {
										//map.put("customerNum", 0);
									}
									else {
										//map.put("customerNum", hyOrderCustomers.size());
										customerNum += hyOrderCustomers.size();
										totalCustomerNum += hyOrderCustomers.size();
									}
								}
								
							}
						}
						map.put("customerNum", customerNum);
						map.put("moneyNum", moneyNum);
						totalMoneyNum = totalMoneyNum.add(moneyNum);
					}
						
						
					
					//得到应该插入的位置 插入排序就行
					int insertPos = -1;
					for(int i = 0; i < list.size(); i++) {
						//按订单数排序
						if(type == 0) {
							if((int)(list.get(i).get("orderNum")) < (int)(map.get("orderNum"))) {
								insertPos = i;
								break;
							}
						}
						//按收客人数排序
						else if(type == 1) {
							if((int)(list.get(i).get("customerNum")) < (int)(map.get("customerNum"))) {
								insertPos = i;
								break;
							}
						}
						//按总计金额排序
						else {
							if( ((BigDecimal)(list.get(i).get("moneyNum"))).compareTo( (BigDecimal)(map.get("moneyNum"))) == -1) {
								insertPos = i;
								break;
							}
						}
						
					}
					//插入
					if(insertPos!=-1) {
						list.add(insertPos, map);
					}
					else {
						list.add(map);
					}
					

				}
				else {
					map.put("caigoubuWorker", caigoubuWorker.getName());
					map.put("supplierNum", 0);
					map.put("orderNum", 0);
					map.put("customerNum", 0);
					map.put("moneyNum", new BigDecimal("0"));
					//插入到list最后一个
					list.add(map);
				}
				
				
			}	
			//list最后一行为总计
			HashMap<String,Object> map=new HashMap<String,Object>();
			if(caigoubuWorkers == null || caigoubuWorkers.size() == 0) {
				map.put("totalCaigoubuWorker", 0);
				map.put("totalSupplierNum", 0);
				map.put("totalOrderNum", 0);
				map.put("totalCustomerNum", 0);
				map.put("totalMoneyNum", new BigDecimal("0"));
			}
			else {
				map.put("totalCaigoubuWorker", caigoubuWorkers.size());
				map.put("totalSupplierNum", totalSupplierNum);
				map.put("totalOrderNum", totalOrderNum);
				map.put("totalCustomerNum", totalCustomerNum);
				map.put("totalMoneyNum", totalMoneyNum);
			}
			
			list.add(map);
			
			List<Wrap1> tempList = new ArrayList<>();
			for(int i = 0; i < list.size(); i++) {
				if(i == list.size() - 1) {
					Wrap1 wrap1 = new Wrap1();
					wrap1.setCustomerNum( (int) list.get(i).get("totalCustomerNum"));
					wrap1.setName( ((int) list.get(i).get("totalCaigoubuWorker")) + "");
					wrap1.setOrderNum( (int) list.get(i).get("totalOrderNum"));
					wrap1.setMoneyNum( (BigDecimal) list.get(i).get("totalMoneyNum"));
					wrap1.setRank("总计");
					wrap1.setSupplierNum( (int) list.get(i).get("totalSupplierNum"));
					tempList.add(wrap1);
					continue;
				}
				Wrap1 wrap1 = new Wrap1();
				wrap1.setCustomerNum( (int) list.get(i).get("customerNum"));
				wrap1.setName( (String) list.get(i).get("caigoubuWorker"));
				wrap1.setOrderNum( (int) list.get(i).get("orderNum"));
				wrap1.setMoneyNum( (BigDecimal) list.get(i).get("moneyNum"));
				wrap1.setRank((i + 1) + "");
				wrap1.setSupplierNum( (int) list.get(i).get("supplierNum"));
				
				tempList.add(wrap1);
				
			}
			
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM");

		
			String title = "供应商业务统计表";
			if(startDate!=null && endDate!=null) {
				title = bartDateFormat.format(startDate) + "---" + bartDateFormat.format(endDate) + " " + title;
			}
			if(type == 0) {
				title += "（按订单数排名）";
			}
			else if(type == 1) {
				title += "（按收客人数排名）";
			}
			else {
				title += "（按总计金额排名）";
			}

			baseController.export2Excel(request, response, tempList, "供应商业务统计表.xls", title , "supplierBusinessStatisticsCaigoubu.xml");
			json.setSuccess(true);
			json.setMsg("导出excel成功");
			json.setObj(null);
			
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("导出excel失败");
			json.setObj(null);
			e.printStackTrace();
		}
		return null;
		
	}
	
	//导出excel
	@RequestMapping(value="/get_excel_shichangbu")
	@ResponseBody
	public Json export2Excel2(HttpServletRequest request,
			HttpServletResponse response, String fileName,
			String tableTitle, String configFile,
			HttpSession session, @DateTimeFormat(pattern="yyyy-MM") Date startDate, @DateTimeFormat(pattern="yyyy-MM") Date endDate, 
			Integer type) {
		
		Json json = new Json();
		
		try {
			
			List<HashMap<String, Object>> list = new ArrayList<>();
			
			if(startDate == null || endDate == null) {
				Calendar calendar = Calendar.getInstance();
				//得到当月的
				calendar.setTime(new Date());
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				startDate = calendar.getTime();
				
				calendar.add(Calendar.MONTH, 1);
				endDate = calendar.getTime();
			}
			//根据开始年月和结束年月求出总月份数和开始月份数
			//总月份数
			int monthNum = MyCalendar.monthsBetweenDate(startDate, endDate);
			
			Date now = startDate;
			Date next = MyCalendar.monthAfter(now);
			
			
			//对于每一个采购部员工，如果管理供应商，则有，否则没有
			
			//对于每一个采购部员工，如果管理供应商，则有，否则没有
			HyDepartmentModel hyDepartmentModel = hyDepartmentModelService.find("总公司采购部");
			
			List<Filter> departmentFilters = new ArrayList<>();
			departmentFilters.add(Filter.eq("hyDepartmentModel", hyDepartmentModel));
			
			//部门是总公司采购部
			Department department = departmentService.findList(null, departmentFilters, null).get(0);
			
			
			
			
			//1015为总公司采购部员工
			//HyRole hyRole = hyRoleService.find(Long.valueOf("1015"));
			//得到所有的采购部员工和经理
			List<Filter> adminFilters = new ArrayList<>();
			adminFilters.add(Filter.eq("department", department));

			List<HyAdmin> caigoubuWorkers = hyAdminService.findList(null, adminFilters, null);

			
//			List<HyAdmin> caigoubuYuanGongs = new ArrayList<>(); 
//			
//			//筛选采购部员工
//			for(int i = 0; i < caigoubuWorkers.size();i++) {
//				if (caigoubuWorkers.get(i).getRole().getName().contains("员工")) {
//					caigoubuYuanGongs.add(caigoubuWorkers.get(i));
//				}
//			}

			
			int all_orders = 0;
			int all_customers = 0;
			BigDecimal all_money = new BigDecimal("0");
			
			for(int i = 0; i < monthNum; i++ ) {
				int totalOrderNum = 0;
				int totalCustomerNum = 0;
				BigDecimal totalMoneyNum = new BigDecimal("0");
				
				
				HashMap<String,Object> map=new HashMap<String,Object>();
				//对每一个采购部员工
				for(HyAdmin caigoubuWorker : caigoubuWorkers) {
					
					//计算管理供应商的数量
					List<Filter> supplierFilter = new ArrayList<>();
					supplierFilter.add(Filter.eq("operator", caigoubuWorker));
					List<HySupplier> hySuppliers = hySupplierService.findList(null,supplierFilter,null);
					//得到了所有供应商的数量
					if(hySuppliers != null && hySuppliers.size() > 0) {
						//对每一个供应商，查找对应的订单
						//对每一个供应商，查找对应的订单
						
						//找到所有供应商的合同
						List<Filter> contractFilter = new ArrayList<>();
						contractFilter.add(Filter.in("hySupplier", hySuppliers));
						List<HySupplierContract> hySupplierContracts = hySupplierContractService.findList(null, contractFilter, null);
						if(hySupplierContracts == null || hySupplierContracts.size() == 0) {
							map.put("totalOrderNum", 0);
							map.put("totalCustomerNum", 0);
							map.put("totalMoneyNum", new BigDecimal("0"));
							list.add(map);
							continue;
						}
						List<HyAdmin> hyAdmins = new ArrayList<>();
						//对于所有合同，得到负责人
						for(int j = 0; j < hySupplierContracts.size(); j++) {
							hyAdmins.add(hySupplierContracts.get(j).getLiable());
						}
						//list转set 再转回list
						Set<HyAdmin> adminSet = new HashSet<>(hyAdmins);
						List<HyAdmin> adminList = new ArrayList<>(adminSet);
						
						
						List<Filter> orderFilter = new ArrayList<>();
						orderFilter.add(Filter.in("supplier", adminList));
						
						//订单过滤器中加入时间过滤
						orderFilter.add(Filter.ge("fatuandate", now));
						orderFilter.add(Filter.le("fatuandate", next));
						
						//支付状态为已支付
						//线路订单 已支付
						orderFilter.add(Filter.eq("type", 1));
						orderFilter.add(Filter.eq("paystatus", 1));
						
						List<HyOrder> hyOrders = hyOrderService.findList(null, orderFilter, null);
						if(hyOrders == null || hyOrders.size() == 0) {
							
						}
						else {
							totalOrderNum += hyOrders.size();
							BigDecimal moneyNum = new BigDecimal("0");
							//对每一个订单，查找对应的所有客户和金额
							//总计金额计算
							//int customerNum = 0;
							for(HyOrder hyOrder : hyOrders) {
								if(hyOrder.getJiesuanMoney1() != null) {
									moneyNum = moneyNum.add(hyOrder.getJiesuanMoney1());
								}
								//减去退款金额
								if(hyOrder.getJiesuanTuikuan() != null) {
									moneyNum = moneyNum.subtract((hyOrder.getJiesuanTuikuan()));
								}
								
								List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
								if(hyOrderItems == null || hyOrderItems.size() == 0) {
									
								}
								else {
									for(HyOrderItem hyOrderItem : hyOrderItems) {
										List<HyOrderCustomer> hyOrderCustomers = hyOrderItem.getHyOrderCustomers();
										if(hyOrderCustomers == null || hyOrderCustomers.size() == 0) {
											
										}
										else {
											totalCustomerNum += hyOrderCustomers.size();
										}
									}
									
								}
							}
							totalMoneyNum = totalMoneyNum.add(moneyNum);
						}
						
						
						//插入
						
						all_customers += totalCustomerNum;
						all_orders += totalOrderNum;
						all_money = all_money.add(totalMoneyNum);
								
					}
					else {
						
						//插入到list最后一个
						
					}
					
				}
				SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM");
				
				
				map.put("month",bartDateFormat.format(now));
				map.put("totalOrderNum", totalOrderNum);
				map.put("totalCustomerNum", totalCustomerNum);
				map.put("totalMoneyNum", totalMoneyNum);
				list.add(map);
				
				now = next;
				next = MyCalendar.monthAfter(now);
				
			}
			
			
			//list最后一行为总计
			HashMap<String,Object> map=new HashMap<String,Object>();
			if(caigoubuWorkers == null || caigoubuWorkers.size() == 0) {
				map.put("all_customers", 0);
				map.put("all_orders", 0);
				map.put("all_money", new BigDecimal("0"));
			}
			else {
				map.put("all_customers", all_customers);
				map.put("all_orders", all_orders);
				map.put("all_money", all_money);
			}
			
			list.add(map);
			
			List<Wrap2> tempList = new ArrayList<>();
			for(int i = 0; i < list.size(); i++) {
				if(i == list.size() - 1) {
					Wrap2 wrap2 = new Wrap2();
					wrap2.setMonth("总计");
					wrap2.setTotalCustomerNum( (int) list.get(i).get("all_customers"));
					wrap2.setTotalMoneyNum( (BigDecimal) list.get(i).get("all_money"));
					wrap2.setTotalOrderNum( (int) list.get(i).get("all_orders"));
					tempList.add(wrap2);
					continue;
				}
				Wrap2 wrap2 = new Wrap2();
				wrap2.setMonth( (String) list.get(i).get("month"));
				wrap2.setTotalCustomerNum( (int) list.get(i).get("totalCustomerNum"));
				wrap2.setTotalMoneyNum( (BigDecimal) list.get(i).get("totalMoneyNum"));
				wrap2.setTotalOrderNum( (int) list.get(i).get("totalOrderNum"));
				
				
				tempList.add(wrap2);
				
			}

		
			String title = "供应商业务统计表（给市场部经理）";
			

			baseController.export2Excel(request, response, tempList, "供应商业务统计表.xls", title , "supplierBusinessStatisticsShichangbu.xml");
			json.setSuccess(true);
			json.setMsg("导出excel成功");
			json.setObj(null);
			
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("导出excel失败");
			json.setObj(null);
			e.printStackTrace();
		}
		return null;
		
	}
	
	@RequestMapping(value="/get_excel_caigoubu_yuangong")
	@ResponseBody
	public Json export2Excel3(HttpServletRequest request,
			HttpServletResponse response, String fileName,
			String tableTitle, String configFile,
			HttpSession session, @DateTimeFormat(pattern="yyyy-MM") Date startDate, @DateTimeFormat(pattern="yyyy-MM") Date endDate, Integer type) {
		Json json = new Json();
		try {

			List<HashMap<String, Object>> list = new ArrayList<>();
			
			
			
			//年份，月份，每月营业额
			//Map<Integer, ArrayList<BigDecimal>> yearMap = new HashMap<Integer, ArrayList<BigDecimal>>();
			
//			int pageRow = pageable.getRows();
//			int pageNumber = pageable.getPage();
			//按订单数 0  按收客人数 1  按总计金额 2
			if(type == null) {
				type = 0;
			}
			
			//对于每一个采购部员工，如果管理供应商，则有，否则没有
//			HyDepartmentModel hyDepartmentModel = hyDepartmentModelService.find("总公司采购部");
//			
//			List<Filter> departmentFilters = new ArrayList<>();
//			departmentFilters.add(Filter.eq("hyDepartmentModel", hyDepartmentModel));
			
			//部门是总公司采购部
//			Department department = departmentService.findList(null, departmentFilters, null).get(0);
			
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			
			//1015为总公司采购部员工
			//HyRole hyRole = hyRoleService.find(Long.valueOf("1015"));
			//得到所有的采购部员工
//			List<Filter> adminFilters = new ArrayList<>();
//			adminFilters.add(Filter.eq("department", department));

//			List<HyAdmin> caigoubuWorkers = hyAdminService.findList(null, adminFilters, null);
			//供应商总数量
			
			
//			List<HyAdmin> caigoubuYuanGongs = new ArrayList<>(); 
			
//			//筛选采购部员工
//			for(int i = 0; i < caigoubuWorkers.size();i++) {
//				if (caigoubuWorkers.get(i).getRole().getName().contains("员工")) {
//					caigoubuYuanGongs.add(caigoubuWorkers.get(i));
//				}
//			}
			
			//对每一个采购部员工
			//for(HyAdmin caigoubuWorker : caigoubuYuanGongs) {
			
			//查看自己管理的供应商
			List<Filter> supplierFilter = new ArrayList<>();
			supplierFilter.add(Filter.eq("operator", admin));
			
			//map.put("caigoubuWorker", caigoubuWorker.getName());
			int totalSupplierNum = 0;
			//订单总数量
			int totalOrderNum = 0;
			//总收客人数
			int totalCustomerNum = 0;
			//总金额
			BigDecimal totalMoneyNum = new BigDecimal("0");
			List<HySupplier> hySuppliers = hySupplierService.findList(null,supplierFilter,null);
			totalSupplierNum += hySuppliers.size();
			//订单数，收客人数，总计金额
			for(HySupplier hySupplier : hySuppliers) {
				
				
				//对每一个供应商
				HashMap<String, Object> map=new HashMap<String, Object>();
				map.put("supplierName", hySupplier.getSupplierName());
				//找到供应商对应的合同
				List<Filter> contractFilter = new ArrayList<>();
				contractFilter.add(Filter.eq("hySupplier", hySupplier));
				List<HySupplierContract> hySupplierContracts = hySupplierContractService.findList(null, contractFilter, null);
				if(hySupplierContracts == null || hySupplierContracts.size() == 0) {
					//订单数
					map.put("orderNum", 0);
					//收客人数
					map.put("customerNum", 0);
					//总计金额
					map.put("moneyNum", new BigDecimal("0"));
					list.add(map);
					continue;
				}
				

				List<HyAdmin> hyAdmins = new ArrayList<>();
				//对于所有合同，得到负责人
				for(int i = 0; i < hySupplierContracts.size(); i++) {
					hyAdmins.add(hySupplierContracts.get(i).getLiable());
				}
				//list转set 再转回list
				Set<HyAdmin> adminSet = new HashSet<>(hyAdmins);
				List<HyAdmin> adminList = new ArrayList<>(adminSet);
				
				
				List<Filter> orderFilter = new ArrayList<>();
				orderFilter.add(Filter.in("supplier", adminList));
				
				
				//线路订单 已支付
				orderFilter.add(Filter.eq("type", 1));
				orderFilter.add(Filter.eq("paystatus", 1));
				//订单过滤器中加入时间过滤
				if(startDate!=null) {
					orderFilter.add(Filter.ge("fatuandate", startDate));
				}
				if(endDate!=null) {
					orderFilter.add(Filter.le("fatuandate", endDate));
				}
				
				List<HyOrder> hyOrders = hyOrderService.findList(null, orderFilter, null);
				if(hyOrders == null || hyOrders.size() == 0) {
					map.put("orderNum", 0);
					map.put("customerNum", 0);
					map.put("moneyNum", new BigDecimal("0"));
				}
				else {
					map.put("orderNum", hyOrders.size());
					//总订单数增加
					totalOrderNum += hyOrders.size();
					BigDecimal moneyNum = new BigDecimal("0");
					//对每一个订单，查找对应的所有客户和金额
					//总计金额计算
					int customerNum = 0;
					for(HyOrder hyOrder : hyOrders) {
						//moneyNum = moneyNum.add(hyOrder.getJiesuanMoney1());
						if(hyOrder.getJiesuanMoney1() != null) {
							moneyNum = moneyNum.add(hyOrder.getJiesuanMoney1());
						}
						//减去退款金额
						if(hyOrder.getJiesuanTuikuan() != null) {
							moneyNum = moneyNum.subtract((hyOrder.getJiesuanTuikuan()));
						}
						
						List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
						if(hyOrderItems == null || hyOrderItems.size() == 0) {
							//map.put("customerNum", 0);
						}
						else {
							for(HyOrderItem hyOrderItem : hyOrderItems) {
								List<HyOrderCustomer> hyOrderCustomers = hyOrderItem.getHyOrderCustomers();
								if(hyOrderCustomers == null || hyOrderCustomers.size() == 0) {
									//map.put("customerNum", 0);
								}
								else {
									//map.put("customerNum", hyOrderCustomers.size());
									customerNum += hyOrderCustomers.size();
									totalCustomerNum += hyOrderCustomers.size();
								}
							}
							
						}
					}
					map.put("customerNum", customerNum);
					map.put("moneyNum", moneyNum);
					totalMoneyNum = totalMoneyNum.add(moneyNum);
				}
				int insertPos = -1;
				for(int i = 0; i < list.size(); i++) {
					//按订单数排序
					if(type == 0) {
						if((int)(list.get(i).get("orderNum")) < (int)(map.get("orderNum"))) {
							insertPos = i;
							break;
						}
					}
					//按收客人数排序
					else if(type == 1) {
						if((int)(list.get(i).get("customerNum")) < (int)(map.get("customerNum"))) {
							insertPos = i;
							break;
						}
					}
					//按总计金额排序
					else {
						if( ((BigDecimal)(list.get(i).get("moneyNum"))).compareTo( (BigDecimal)(map.get("moneyNum"))) == -1) {
							insertPos = i;
							break;
						}
					}
					
				}
				//插入
				if(insertPos!=-1) {
					list.add(insertPos, map);
				}
				else {
					list.add(map);
				}
				
			}
			
				
//
//			else {
//				map.put("caigoubuWorker", caigoubuWorker.getName());
//				map.put("supplierNum", 0);
//				map.put("orderNum", 0);
//				map.put("customerNum", 0);
//				map.put("moneyNum", new BigDecimal("0"));
//				//插入到list最后一个
//				list.add(map);
//			}
				
				
			//}	
			//list最后一行为总计
			HashMap<String,Object> map2=new HashMap<String,Object>();
			if(hySuppliers == null || hySuppliers.size() == 0) {
				map2.put("totalSupplierNum", 0);
				map2.put("totalOrderNum", 0);
				map2.put("totalCustomerNum", 0);
				map2.put("totalMoneyNum", new BigDecimal("0"));
			}
			else {
				map2.put("totalSupplierNum", totalSupplierNum);
				map2.put("totalOrderNum", totalOrderNum);
				map2.put("totalCustomerNum", totalCustomerNum);
				map2.put("totalMoneyNum", totalMoneyNum);
			}
			
			list.add(map2);
			
			
			
			List<Wrap3> tempList = new ArrayList<>();
			for(int i = 0; i < list.size(); i++) {
				if(i == list.size() - 1) {
					Wrap3 wrap3 = new Wrap3();
					wrap3.setPeopleNum( (int) list.get(i).get("totalCustomerNum"));
					wrap3.setSupplierName( ((int) list.get(i).get("totalSupplierNum")) + "");
					wrap3.setOrderNum( (int) list.get(i).get("totalOrderNum"));
					wrap3.setMoney( (BigDecimal) list.get(i).get("totalMoneyNum"));
					wrap3.setSort("总计");
					tempList.add(wrap3);
					continue;
				}
				Wrap3 wrap3 = new Wrap3();
				wrap3.setPeopleNum( (int) list.get(i).get("customerNum"));
				//wrap3.setName( (String) list.get(i).get("caigoubuWorker"));
				wrap3.setOrderNum( (int) list.get(i).get("orderNum"));
				wrap3.setMoney( (BigDecimal) list.get(i).get("moneyNum"));
				wrap3.setSort((i + 1) + "");
				wrap3.setSupplierName( (String) list.get(i).get("supplierName") );
				//wrap3.setSupplierNum( (int) list.get(i).get("supplierNum"));
				
				tempList.add(wrap3);
				
			}
			
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		
			String title = "供应商业务统计表（给供应商对接人）";
			if(startDate!=null && endDate!=null) {
				title = bartDateFormat.format(startDate) + "---" + bartDateFormat.format(endDate) + " " + title;
			}
			if(type == 0) {
				title += "（按订单数排名）";
			}
			else if(type == 1) {
				title += "（按收客人数排名）";
			}
			else {
				title += "（按总计金额排名）";
			}

			baseController.export2Excel(request, response, tempList, "供应商业务统计表.xls", title , "supplierBusinessStatisticsCaigoubuYuanGong.xml");
			
			
			
			
//			HashMap<String,Object> map3=new HashMap<String,Object>();
//			map3.put("rows", list);
//		    map3.put("total",list.size());
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(null);

			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	@RequestMapping(value="/get_excel_gys")
	@ResponseBody
	public Json export2Excel4(HttpServletRequest request,
			HttpServletResponse response, String fileName,
			String tableTitle, String configFile,
			HttpSession session, @DateTimeFormat(pattern="yyyy-MM") Date startDate, @DateTimeFormat(pattern="yyyy-MM") Date endDate, Integer type, Long storeId) {
		Json json = new Json();
		try {
			//按订单数 0  按收客人数 1  按总计金额 2
			if(type == null) {
				type = 0;
			}
			
			
			
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			
			List<Map<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			
			//拿到登录人所在的部门
//			Department department = hyAdmin.getDepartment();
//			
//			Store store = storeService.findStore(hyAdmin);
			
			//拿到父账号
//			while(admin.getHyAdmin() != null) {
//				admin = admin.getHyAdmin();
//			}
//			
//			HySupplier hySupplier = null;
//			//拿到供应商
//			if(admin.getLiableContracts() != null && admin.getLiableContracts().size() != 0) {
//				Iterator<HySupplierContract> iterator = admin.getLiableContracts().iterator();
//				HySupplierContract hySupplierContract = iterator.next();
//				hySupplier = hySupplierContract.getHySupplier();
//			}
//			if(hySupplier == null) {
//				json.setMsg("父账号不是供应商");
//				json.setSuccess(false);
//				return json;
//			}
			//Long supplierId = hySupplier.getId();
			
			String[] attrs = new String[]{
					"orderId","productId","productName","groupStartTime","orderCreateTime","contactor","peopleNum","storeName","operatorName","orderMoney","orderPayTime","orderRefundMoney",
			};
			
			//订单type 2 认购门票
			
			StringBuilder totalSb = new StringBuilder("select sum(hyorder.people)");
			//StringBuilder sumSb = new StringBuilder("select sum(order_money)");
			StringBuilder pageSb = new StringBuilder("select distinct hyorder.order_number, hyline.pn, hygroup.group_line_name, hygroup.start_day, hyorder.createtime,"
					+ " hyorder.contact, hyorder.people, store.store_name, hyadmin.name, hyorder.jiesuan_money1, hyorder.pay_time, hyorder.jiesuan_tuikuan");
			StringBuilder sb = new StringBuilder(" from hy_order hyorder, hy_admin hyadmin, hy_group hygroup, hy_store store, hy_order_item hyorderitem, hy_line hyline"
					+ " where hyline.id=hygroup.line and hyorder.operator_id=hyadmin.username and hyorder.group_id=hygroup.id and hyorderitem.order_id=hyorder.id and hyorder.store_id=store.id and hyorderitem.product_id=hygroup.id");
			//已支付 门店下单 线路订单
			sb.append(" and hyorder.type=1 and hyorder.source=0 and hyorder.paystatus=1 and hyorder.status=3 and hyorder.supplier='"+admin.getUsername()+"'");
			
			System.out.println("数据库语句为" + sb);
			//范围是总公司的能看到 and 和 or 语句结合 加括号
			
			if(storeId != null){
				sb.append(" and hyorder.store_id="+storeId+" and store.id="+storeId);
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			Calendar calendar = Calendar.getInstance();
			if(startDate != null){
				calendar.setTime(startDate);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				startDate = calendar.getTime();
				sb.append(" and hygroup.start_day >= '"+ sdf.format(startDate)+"'");
			}
			if(endDate != null) {
				//所在部门是分公司
				calendar.setTime(endDate);
				//增加一个月
				calendar.add(Calendar.MONTH, 1);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				endDate = calendar.getTime();
				sb.append(" and hygroup.start_day <= '"+ sdf.format(endDate)+"'");
			}
			
			
			
//			if(hyAdmins!=null && !hyAdmins.isEmpty()){
//				List<String> adminStrArr = new ArrayList<>();
//				for(HyAdmin hyAdmin:hyAdmins){
//					adminStrArr.add("'"+hyAdmin.getUsername()+"'");
//					
//				}
//				String adminStr = String.join(",",adminStrArr);
//				sb.append(" and o1.operator_id in ("+adminStr+")");
//			}
			

			List totals = hyOrderService.statis(totalSb.append(sb).toString());
			Integer total = null;
			if(totals == null || totals.size() == 0 || totals.get(0) == null) {
				total = 0;
			}
			else {
				total = ((BigDecimal)totals.get(0)).intValue();
			}
			
			
			sb.append(" order by hyorder.createtime desc");
			
			System.out.println("数据库有条件的语句为" + sb);
			
			//Integer sqlStart = (pageable.getPage()-1)*pageable.getRows();
			//Integer sqlEnd = pageable.getPage()*pageable.getRows();
			//sb.append(" limit "+sqlStart+","+sqlEnd);
			
			//sb.append(" limit "+ count);
			
			//有效
			//filters.add(Filter.eq("isValid", 1));
//			List<Order> orders = new ArrayList<Order>();
//			orders.add(Order.desc("createTime"));
			
			List<Object[]> objs = hyOrderService.statis(pageSb.append(sb).toString());
			
			BigDecimal totalMoney = new BigDecimal(0);
			for(Object[] obj : objs){
				Map<String, Object> map1 = ArrayHandler.toMap(attrs, obj);
				
				BigDecimal orderMoney = (BigDecimal)obj[9];
				if(obj[11]!=null) {
					BigDecimal orderRefundMoney = (BigDecimal)obj[11];
					orderMoney = orderMoney.subtract(orderRefundMoney);
					map1.put("rOrderMoney", orderMoney);
				}
				else {
					map1.put("rOrderMoney", orderMoney);
				}
				totalMoney = totalMoney.add(orderMoney);
				
				list.add(map1);
			}			
			
			Map<String, Object> map2 = new HashMap<>();
			map2.put("totalMoney", totalMoney);
			map2.put("totalPeopleNum", total);
			list.add(map2);
			
			SimpleDateFormat paytimef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
			
			//"orderId","productId","productName","groupStartTime","orderCreateTime","contactor","peopleNum",
			//"storeName","operatorName","orderMoney","orderPayTime","orderRefundMoney",
			List<Wrap4> tempList = new ArrayList<>();
			for(int i = 0; i < list.size(); i++) {
				if(i == list.size() - 1) {
					Wrap4 wrap4 = new Wrap4();
//					wrap4.setPayTime(paytimef.format( (Date)(list.get(i).get("orderPayTime") )));
					wrap4.setPeople( (int) list.get(i).get("totalPeopleNum"));
//					wrap4.setContactor( ((String) list.get(i).get("contactor")) + "");
//					wrap4.setCreateDate(sdff.format( (Date)(list.get(i).get("orderCreateTime") )));
//					wrap4.setStartDate(sdff.format( (Date)(list.get(i).get("groupStartTime") )));
//					wrap4.setOperator(((String) list.get(i).get("operatorName")) + "");
					wrap4.setOrderMoney( (BigDecimal) list.get(i).get("totalMoney"));
//					wrap4.setOrderNum((String) list.get(i).get("orderId") + "");
//					wrap4.setProductId( (Long) list.get(i).get("productId"));
//					wrap4.setProductName((String) list.get(i).get("productName") + "");
					tempList.add(wrap4);
					continue;
				}
				Wrap4 wrap4 = new Wrap4();
				wrap4.setPayTime(paytimef.format( (Date)(list.get(i).get("orderPayTime") )));
				wrap4.setPeople( (int) list.get(i).get("peopleNum"));
				wrap4.setContactor( ((String) list.get(i).get("contactor")) + "");
				wrap4.setCreateDate(sdff.format( (Date)(list.get(i).get("orderCreateTime") )));
				wrap4.setStartDate(sdff.format( (Date)(list.get(i).get("groupStartTime") )));
				wrap4.setOperator(((String) list.get(i).get("operatorName")) + "");
				wrap4.setOrderMoney( (BigDecimal) list.get(i).get("rOrderMoney"));
				wrap4.setOrderNum((String) list.get(i).get("orderId") + "");
				wrap4.setProductId( (String) list.get(i).get("productId"));
				wrap4.setProductName((String) list.get(i).get("productName") + "");
				//wrap3.setSupplierNum( (int) list.get(i).get("supplierNum"));
				
				tempList.add(wrap4);
				
			}
			
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			String storeName = storeService.find(storeId).getStoreName();

		
			String title = "供应商业务统计表（给供应商）" + " " + storeName;
			if(startDate!=null && endDate!=null) {
				title = bartDateFormat.format(startDate) + "---" + bartDateFormat.format(endDate) + " " + title;
			}
//			if(type == 0) {
//				title += "（按订单数排名）";
//			}
//			else if(type == 1) {
//				title += "（按收客人数排名）";
//			}
//			else {
//				title += "（按总计金额排名）";
//			}

			baseController.export2Excel(request, response, tempList, "供应商业务统计表.xls", title , "supplierBusinessStatisticsGys.xml");
			
			
			
			
	
			//map.put("rows", list);
			
			
			
			
			json.setSuccess(true);
			json.setMsg("导出excel成功");
			json.setObj(null);

			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		
		return json;
	}

	
}
