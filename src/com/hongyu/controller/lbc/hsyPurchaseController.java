package com.hongyu.controller.lbc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Provider;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BankListService;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.InboundService;
import com.hongyu.service.ProviderService;
import com.hongyu.service.PurchaseInboundService;
import com.hongyu.service.PurchaseItemService;
import com.hongyu.service.PurchasePayService;
import com.hongyu.service.PurchaseService;
import com.hongyu.service.PurchaseShipService;
import com.hongyu.service.SpecialtyCategoryService;
import com.hongyu.service.SpecialtyLostService;
import com.hongyu.service.SpecialtyPriceService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.service.WeDivideProportionService;
import com.hongyu.util.AuthorityUtils;


@Controller
public class hsyPurchaseController {

	@Resource(name = "purchaseServiceImpl")
	PurchaseService purchaseServiceImpl;
	
	@Resource(name = "providerServiceImpl")
	ProviderService providerServiceImpl;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="departmentServiceImpl")
	private DepartmentService departmentService;
	
	@Resource(name = "specialtySpecificationServiceImpl")
	SpecialtySpecificationService specialtySpecificationSrv;
	
	@Resource(name = "purchaseItemServiceImpl")
	PurchaseItemService purchaseItemServiceImpl;
	
	@Resource(name = "purchaseShipServiceImpl")
	PurchaseShipService purchaseShipServiceImpl;
	
	@Resource(name = "purchaseInboundServiceImpl")
	PurchaseInboundService purchaseInboundServiceImpl;
	
	@Resource(name="specialtyCategoryServiceImpl")
	SpecialtyCategoryService specialtyCategoryServiceImpl;
	
	@Resource(name="specialtyServiceImpl")
	SpecialtyService specialtyServiceImpl;
	
	@Resource(name="purchasePayServiceImpl")
	PurchasePayService purchasePayServiceImpl;
	
	@Resource(name="inboundServiceImpl")
	InboundService inboundServiceImpl;
	
	@Resource(name = "specialtyLostServiceImpl")
	SpecialtyLostService specialtyLostServiceImpl;
	
	@Resource(name = "specialtyPriceServiceImpl")
	SpecialtyPriceService specialtyPriceSrv;
	
	@Resource(name = "bankListServiceImpl")
	BankListService  bankListService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource
	private TaskService taskService;
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name="weDivideProportionServiceImpl")
	WeDivideProportionService proportionSrv;
	
	@Resource(name="businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;
	
	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderServiceImpl;
	
	@Resource(name="commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;
	
	public static Object lock = new Object();
	private static SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
	
	
	
	
	//采购单查询特产分区
	  @RequestMapping({"admin/business/specialty_turnover/annual_list_compare/categorytreelist/view"})
	  @ResponseBody
	  public Json specialtyCategoryTreeList()
	  {
	    Json json = new Json();
	    List<Filter> filters = new ArrayList<Filter>();
	    Filter filter = new Filter("parent", Filter.Operator.isNull, null);
	    filters.add(filter);
	    List<Order> orders = new ArrayList<Order>();
	    orders.add(Order.asc("id"));
	    List<SpecialtyCategory> list = this.specialtyCategoryServiceImpl.findList(null, filters, orders);
	    List<HashMap<String, Object>> obj = new ArrayList<HashMap<String, Object>>();
	    for (SpecialtyCategory parent : list)
	    {
	      HashMap<String, Object> hm = new HashMap<String, Object>();
	      hm.put("value", parent.getId());
	      hm.put("label", parent.getName());
	      hm.put("children", fieldFilter(parent));
	      obj.add(hm);
	    }
	    json.setSuccess(true);
	    json.setMsg("查询成功");
	    json.setObj(obj);
	    return json;
	  }
	  
	  
	  @RequestMapping(value = "admin/business/specialty_turnover/annual_list_compare/product/page/view")
	  @ResponseBody
	  public Json specialtyList(Specialty specialty, Pageable pageable, Long categoryid, Long providerid, HttpSession session, HttpServletRequest request)
	  {	
		  	Json json = new Json();
		  	List<Filter> filters = new ArrayList<Filter>();
		  	
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			
			filters.add(Filter.in("creator", hyAdmins));
			
		  try {
			  SpecialtyCategory category = null;
			  if (categoryid != null) {
				  category = specialtyCategoryServiceImpl.find(categoryid);
				  if (category != null) {
					  specialty.setCategory(category);
				  }
			  }
			  
			  if (StringUtils.isNotEmpty(specialty.getName())) {
			      
			      Filter filter = new Filter("name", Operator.like, specialty.getName());
			      filters.add(filter);
			      specialty.setName(null);
			  }
			  
			  if (providerid != null) {
				  Provider provider = providerServiceImpl.find(providerid);
				  if (provider == null) {
					  Page<Specialty> page1 = new Page<>(new ArrayList<Specialty>(), 0, pageable);
					  json.setSuccess(true);
					  json.setMsg("查询成功");
					  json.setObj(page1);
				  } else {
					  filters.add(Filter.eq("provider", provider));
				  }
				  
			  }
			  
			  pageable.setFilters(filters);
			  List<Order> orders = new ArrayList<Order>();
			  orders.add(Order.desc("id"));
			  pageable.setOrders(orders);
			  Page<Specialty> page = specialtyServiceImpl.findPage(pageable, specialty);
			  for (Specialty s : page.getRows()) {
				  s.setSpecialtiesForRecommendSpecialtyId(null);
				  s.setSpecialtiesForSpeciltyId(null);
				  
				  
				  HyAdmin creator = s.getCreator();
				  /** 当前用户对本条数据的操作权限 */
		  		  if(creator.equals(admin)){
		  			  if(co == CheckedOperation.view) {
		  				  s.setPrivilege("view");
		  			  } else {
		  				s.setPrivilege("edit");
		  			  }
		  		  } else{
		  			  if(co == CheckedOperation.edit) {
		  				s.setPrivilege("edit");
		  			  } else {
		  				s.setPrivilege("view");
		  			  }
		  		  }
			  }
			  json.setSuccess(true);
			  json.setMsg("查询成功");
			  json.setObj(page);
		  } catch (Exception e) {
			  json.setSuccess(false);
			  json.setMsg("查询失败");
			  json.setObj(e);
		  }
	      return json;
	  }
	  
	//递归筛选子分区
	 private List<HashMap<String, Object>> fieldFilter(SpecialtyCategory parent)
	  {
	    List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	    if (parent.getChildSpecialtyCategory().size() > 0) {
	      for (SpecialtyCategory child : parent.getChildSpecialtyCategory())
	      {	
	    	  if(child.getIsActive()) {
	    		  HashMap<String, Object> hm = new HashMap<String, Object>();
	    	      hm.put("value", child.getId());
	    	      hm.put("label", child.getName());
	    	      hm.put("children", fieldFilter(child));
	    	      list.add(hm);
	    	  }
	      }
	    }
	    return list;
	  }
}
