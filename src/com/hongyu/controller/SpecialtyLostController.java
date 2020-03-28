package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.dialect.function.TrimFunctionTemplate.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessSystemSetting;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.entity.Inbound;
import com.hongyu.entity.Purchase;
import com.hongyu.entity.PurchaseInbound;
import com.hongyu.entity.PurchaseItem;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyLost;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BusinessSystemSettingService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.InboundService;
import com.hongyu.service.PurchaseInboundService;
import com.hongyu.service.PurchaseItemService;
import com.hongyu.service.PurchaseService;
import com.hongyu.service.SpecialtyLostService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.SpecialtySpecificationService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping({"/admin/business/specialtylost"})
public class SpecialtyLostController
{
  @Resource(name="specialtyLostServiceImpl")
  SpecialtyLostService specialtyLostServiceImpl;
  @Resource(name="hyAdminServiceImpl")
  HyAdminService hyAdminService;
  @Resource(name="specialtyServiceImpl")
  SpecialtyService specialtyServiceImpl;
  @Resource(name="departmentServiceImpl")
  private DepartmentService departmentService;
  @Resource(name="specialtySpecificationServiceImpl")
  SpecialtySpecificationService specialtySpecificationSrv;
  @Resource(name="purchaseServiceImpl")
  PurchaseService purchaseServiceImpl;
  @Resource(name="inboundServiceImpl")
  InboundService inboundServiceImpl;
  @Resource(name="purchaseItemServiceImpl")
  PurchaseItemService purchaseItemServiceImpl;
  @Resource(name="businessSystemSettingServiceImpl")
  BusinessSystemSettingService systemSettingSrv;
  @Resource(name = "purchaseInboundServiceImpl")
  PurchaseInboundService purchaseInboundServiceImpl;
  
  @RequestMapping({"/add"})
  @ResponseBody
  public Json specialtyLostAdd(Long inboundid, SpecialtyLost lost, HttpSession session)
  {
    Json j = new Json();
    
    String username = (String)session.getAttribute("principal");
    HyAdmin admin = (HyAdmin)this.hyAdminService.find(username);
    try
    {
    	Inbound inbound = inboundServiceImpl.find(inboundid);
    	if (lost.getLostCount() > inbound.getInboundNumber()) {
    		j.setSuccess(false);
        	j.setMsg("提交的损失数量大于库存数量");
        	j.setObj(null);
        	return j;
    	}
    	lost.setPurchaseCode(inbound.getPurchaseItem().getPurchase().getPurchaseCode());
    	lost.setPurchaseItem(inbound.getPurchaseItem());
      
    	lost.setInbound(inbound);
    	lost.setProductDate(inbound.getProductDate());
    	lost.setDurabilityPeriod(inbound.getDurabilityPeriod());
    	lost.setSpecialty(inbound.getSpecification().getSpecialty());
    	lost.setSpecialtySpecification(inbound.getSpecification());
    	lost.setOperator(admin);
      
    	this.specialtyLostServiceImpl.save(lost);
    	j.setSuccess(true);
    	j.setMsg("提交成功");
    	j.setObj(null);
    }
    catch (Exception e)
    {
    	j.setSuccess(false);
    	j.setMsg("提交失败");
    	j.setObj(e);
    	e.printStackTrace();
    }
    return j;
  }
  
  @RequestMapping({"/audit"})
  @ResponseBody
  public Json specialtyLostAudit(Long lostid, Boolean isapproved, String comment, HttpSession session)
  {
    Json j = new Json();
    
    String username = (String)session.getAttribute("principal");
    HyAdmin admin = (HyAdmin)this.hyAdminService.find(username);
    try
    {
      SpecialtyLost lost = (SpecialtyLost)this.specialtyLostServiceImpl.find(lostid);
      PurchaseItem item = lost.getPurchaseItem();
      lost.setChecker(admin);
      lost.setCheckTime(new Date());
      if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED) {
    	  if (isapproved)
          {
            lost.setStatus(Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING);
            if (lost.getInbound() != null)
            {
              Integer lostNum = lost.getLostCount();
              Inbound i = lost.getInbound();
              i.setInboundNumber(i.getInboundNumber() - lostNum);
              
              //修改父规格基本库存
              SpecialtySpecification specification = i.getSpecification();
              specification.setBaseInbound(specification.getBaseInbound()-lostNum);
              specialtySpecificationSrv.update(specification);
              
              inboundServiceImpl.update(i);
            }
          }
          else
          {
            lost.setStatus(Constants.SPECIALTY_LOST_STATUS_FAIL_AUDITING);
            lost.setReason(comment);
          }
    	  this.specialtyLostServiceImpl.update(lost);
    	  
    	  if (isapproved) {
    		  //判断是否是入库损失
        	  if (lost.getInbound() == null) {
        		  
        		  List<Filter> fls = new ArrayList<Filter>();
        		  List<Filter> lostfls = new ArrayList<Filter>();
        		  fls.add(Filter.eq("purchaseItem", item));
        		  List<PurchaseInbound> pis = purchaseInboundServiceImpl.findList(null, fls, null);
        		  lostfls.add(Filter.eq("purchaseItem", item));
        		  lostfls.add(Filter.isNull("inbound"));
        		  lostfls.add(Filter.eq("status", Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING));
        		  //查询已审核的损失产品
        		  List<SpecialtyLost> is = specialtyLostServiceImpl.findList(null, lostfls, null);
        		  Integer total = 0;
        		  for (PurchaseInbound p : pis) {
        			  total += p.getInboundNumber();
        		  }
        		  for (SpecialtyLost sl : is) {
        			  total += sl.getLostCount();
        		  }
        		  if (total == item.getQuantity()) {
        			  item.setState(true);
        			  purchaseItemServiceImpl.update(item);
        		  }
        	  }
    	  }
    	 
    	  
    	  
    	  j.setSuccess(true);
          j.setMsg("审核成功");
          j.setObj(null);
      } else {
    	  j.setSuccess(false);
          j.setMsg("损失产品已被审核");
          j.setObj(null);
      } 
      
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg("审核失败");
      j.setObj(e);
      e.printStackTrace();
    }
    return j;
  }
  
  @RequestMapping({"/productiondate/view"})
  @ResponseBody
  public Json getProductionDateAndDurability(Long purchaseitemid, HttpSession session)
  {
    Json j = new Json();
    try
    {
      PurchaseItem item = (PurchaseItem)this.purchaseItemServiceImpl.find(purchaseitemid);
      if (item != null)
      {
        List<Filter> filters = new ArrayList();
        filters.add(Filter.eq("purchaseItem", item));
        List<Inbound> inbounds = this.inboundServiceImpl.findList(null, filters, null);
        List<Map<String, Object>> list = new ArrayList();
        for (Inbound i : inbounds)
        {
          Map<String, Object> map = new HashMap();
          map.put("inboundid", i.getId());
          map.put("productDate", i.getProductDate());
          map.put("durabilityPeriod", i.getDurabilityPeriod());
          list.add(map);
        }
        j.setSuccess(true);
        j.setMsg("查询成功");
        j.setObj(list);
      }
      else
      {
        j.setSuccess(false);
        j.setMsg("采购明细不存在");
        j.setObj(new ArrayList());
      }
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg("查询失败");
      j.setObj(new ArrayList());
      e.printStackTrace();
    }
    return j;
  }
  
  @RequestMapping({"/detail/view"})
  @ResponseBody
  public Json specialtyLostDetail(Long lostid, HttpSession session)
  {
    Json j = new Json();
    try
    {
      SpecialtyLost lost = (SpecialtyLost)this.specialtyLostServiceImpl.find(lostid);
      if (lost != null)
      {
        lost.setSpecialtyName(lost.getSpecialty().getName());
        lost.setSpecification(lost.getSpecialtySpecification().getSpecification());
        j.setSuccess(true);
        j.setMsg("查询成功");
        j.setObj(lost);
      }
      else
      {
        j.setSuccess(false);
        j.setMsg("损失产品不存在");
        j.setObj(null);
      }
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg("查询失败");
      j.setObj(e);
      e.printStackTrace();
    }
    return j;
  }
  
  @RequestMapping({"/page/view"})
  @ResponseBody
  public Json specialtyLostPage(Integer lostType, String creator, Integer status, String purchaseCode, String specialtyName, Pageable pageable, HttpSession session, HttpServletRequest request)
  {
    Json j = new Json();
    /**
	 * 获取当前用户
	 */
//	String username = (String) session.getAttribute(CommonAttributes.Principal);
//	HyAdmin admin = hyAdminService.find(username);
	
	/** 
	 * 获取用户权限范围
	 */
//	CheckedOperation co = (CheckedOperation) request.getAttribute("co");
//	Set<Department> departments = (Set<Department>) request.getAttribute("range");
	
	/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
//	Set<HyAdmin> hyAdmins = new HashSet<HyAdmin>();
//	hyAdmins.add(admin);
//	
//	/** 如果分配的是部门权限 */
//	if(departments.size() > 0){
//		
//		/**
//		 * 遍历所有分配的部门找到所有本部门及下属部门员工
//		 */
//		for(Department department : departments) {
//			
//			/** 加入本部门所有员工 */
//			hyAdmins.addAll(department.getHyAdmins());
//			
//			/** 找到所有下属部门 */
//			String treePaths = department.getTreePath() + department.getId() + ",";
//			List<Filter> filters = new ArrayList<Filter>();
//			Filter filter = Filter.like("treePath", treePaths);
//			filters.add(filter);
//			List<Department> subDepartments = departmentService.findList(null, filters, null);
//			
//			/** 加入下属部门所有员工 */
//			for(Department subDepartment : subDepartments) {
//				hyAdmins.addAll(subDepartment.getHyAdmins());
//			}
//		}
//	}
	Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
    try
    {
      List<Filter> filters = new ArrayList();
      filters.add(Filter.in("operator", hyAdmins));
//      if (status != null) {
//        if (status.intValue() == 0)
//        {
//          filters.add(Filter.eq("status", Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED));
//        }
//        else if (status.intValue() == 1)
//        {
//          filters.add(Filter.ge("status", Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING));
//          filters.add(Filter.le("status", Constants.SPECIALTY_LOST_STATUS_FAIL_AUDITING));
//        }
//      }
//      if (creator != null)
//      {
//        Object ft = new ArrayList();
//        ((List)ft).add(Filter.like("name", creator));
//        List<HyAdmin> creators = this.hyAdminService.findList(null, (List)ft, null);
//        if (creators.size() > 0) {
//          filters.add(Filter.in("operator", creators));
//        }
//      }
      if (StringUtils.isNotEmpty(specialtyName)) {
    	  List<Filter> fs = new ArrayList<Filter>();
    	  fs.add(Filter.like("name", specialtyName));
    	  List<Specialty> lists = specialtyServiceImpl.findList(null, fs, null);
    	  if (lists.size() == 0) {
    		  j.setSuccess(true);
    	      j.setMsg("查询成功");
    	      j.setObj(new Page<SpecialtyLost>());
    	      return j;
    	  } else {
    		  filters.add(Filter.in("specialty", lists));
    	  }
      }
      
      if (StringUtils.isNotEmpty(purchaseCode)) {
    	  filters.add(Filter.like("purchaseCode", purchaseCode));
      }
      
      if (status != null) {
//          if (status.intValue() == 0)
//          {
//            filters.add(Filter.eq("status", Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED));
//          }
//          else if (status.intValue() == 1)
//          {
//            filters.add(Filter.ge("status", Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING));
//            filters.add(Filter.le("status", Constants.SPECIALTY_LOST_STATUS_FAIL_AUDITING));
//          }
    	  
    	  filters.add(Filter.eq("status", status));
        }
        if (creator != null)
        {
          List<Filter> ft = new ArrayList<Filter>();
          ft.add(Filter.like("name", creator));
          List<HyAdmin> creators = this.hyAdminService.findList(null, ft, null);
          if (creators.size() == 0) {
        	  j.setSuccess(true);
              j.setMsg("查询成功");
              j.setObj(new Page<SpecialtyLost>());
              return j;
          } else {
        	  filters.add(Filter.in("operator", creators));
          }
        }
        
        if (lostType != null) {
        	filters.add(Filter.eq("lostType", lostType));
        }
        
      pageable.setFilters(filters);
      List<Order> orders = new ArrayList<Order>();
	  orders.add(Order.desc("id"));
	  pageable.setOrders(orders);
      Page<SpecialtyLost> page = this.specialtyLostServiceImpl.findPage(pageable);
      for (SpecialtyLost lost : page.getRows())
      {
        lost.setSpecialtyName(lost.getSpecialty().getName());
        lost.setSpecification(lost.getSpecialtySpecification().getSpecification());
      }
      j.setSuccess(true);
      j.setMsg("查询成功");
      j.setObj(page);
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg("查询失败");
      j.setObj(e);
      e.printStackTrace();
    }
    return j;
  }
  
  @RequestMapping({"/manager/page/view"})
  @ResponseBody
  public Json specialtyLostManagerPage(Integer lostType, String creator, Integer status, String purchaseCode, String specialtyName, Pageable pageable, HttpSession session, HttpServletRequest request)
  {
    Json j = new Json();
    /**
	 * 获取当前用户
	 */
	String username = (String) session.getAttribute(CommonAttributes.Principal);
	HyAdmin admin = hyAdminService.find(username);
	
	/** 
	 * 获取用户权限范围
	 */
//	CheckedOperation co = (CheckedOperation) request.getAttribute("co");
//	Set<Department> departments = (Set<Department>) request.getAttribute("range");
//	
//	/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
//	Set<HyAdmin> hyAdmins = new HashSet<HyAdmin>();
//	hyAdmins.add(admin);
//	
//	/** 如果分配的是部门权限 */
//	if(departments.size() > 0){
//		
//		/**
//		 * 遍历所有分配的部门找到所有本部门及下属部门员工
//		 */
//		for(Department department : departments) {
//			
//			/** 加入本部门所有员工 */
//			hyAdmins.addAll(department.getHyAdmins());
//			
//			/** 找到所有下属部门 */
//			String treePaths = department.getTreePath() + department.getId() + ",";
//			List<Filter> filters = new ArrayList<Filter>();
//			Filter filter = Filter.like("treePath", treePaths);
//			filters.add(filter);
//			List<Department> subDepartments = departmentService.findList(null, filters, null);
//			
//			/** 加入下属部门所有员工 */
//			for(Department subDepartment : subDepartments) {
//				hyAdmins.addAll(subDepartment.getHyAdmins());
//			}
//		}
//	}
	Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
    try
    {
      List<Filter> filters = new ArrayList();
      filters.add(Filter.in("operator", hyAdmins));
//      if (status != null) {
//        if (status.intValue() == 0)
//        {
//          filters.add(Filter.eq("status", Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED));
//        }
//        else if (status.intValue() == 1)
//        {
//          filters.add(Filter.ge("status", Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING));
//          filters.add(Filter.le("status", Constants.SPECIALTY_LOST_STATUS_FAIL_AUDITING));
//        }
//      }
//      if (creator != null)
//      {
//        Object ft = new ArrayList();
//        ((List)ft).add(Filter.like("name", creator));
//        List<HyAdmin> creators = this.hyAdminService.findList(null, (List)ft, null);
//        if (creators.size() > 0) {
//          filters.add(Filter.in("operator", creators));
//        }
//      }
      if (StringUtils.isNotEmpty(specialtyName)) {
    	  List<Filter> fs = new ArrayList<Filter>();
    	  fs.add(Filter.like("name", specialtyName));
    	  List<Specialty> lists = specialtyServiceImpl.findList(null, fs, null);
    	  if (lists.size() == 0) {
    		  j.setSuccess(true);
    	      j.setMsg("查询成功");
    	      j.setObj(new Page<SpecialtyLost>());
    	      return j;
    	  } else {
    		  filters.add(Filter.in("specialty", lists));
    	  }
      }
      
      if (StringUtils.isNotEmpty(purchaseCode)) {
    	  filters.add(Filter.like("purchaseCode", purchaseCode));
      }
      
      if (status != null) {
          if (status.intValue() == 0)
          {
            filters.add(Filter.eq("status", Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED));
          }
          else if (status.intValue() == 1)
          {
            filters.add(Filter.ge("status", Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING));
            filters.add(Filter.le("status", Constants.SPECIALTY_LOST_STATUS_FAIL_AUDITING));
          }
    	  
//    	  filters.add(Filter.eq("status", status));
        }
        if (StringUtils.isNotEmpty(creator))
        {
          List<Filter> ft = new ArrayList<Filter>();
          ft.add(Filter.like("name", creator));
          List<HyAdmin> creators = this.hyAdminService.findList(null, ft, null);
          if (creators.size() == 0) {
        	  j.setSuccess(true);
              j.setMsg("查询成功");
              j.setObj(new Page<SpecialtyLost>());
              return j;
          } else {
        	  filters.add(Filter.in("operator", creators));
          }
        }
        
      pageable.setFilters(filters);
      List<Order> orders = new ArrayList<Order>();
	  orders.add(Order.desc("id"));
	  pageable.setOrders(orders);
      Page<SpecialtyLost> page = this.specialtyLostServiceImpl.findPage(pageable);
      for (SpecialtyLost lost : page.getRows())
      {
        lost.setSpecialtyName(lost.getSpecialty().getName());
        lost.setSpecification(lost.getSpecialtySpecification().getSpecification());
      }
      j.setSuccess(true);
      j.setMsg("查询成功");
      j.setObj(page);
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg("查询失败");
      j.setObj(e);
      e.printStackTrace();
    }
    return j;
  }
  
  /**
   * 损失产品修改
   * @param lost
   * @param session
   * @return
   */
  @RequestMapping({"/modify"})
  @ResponseBody
  public Json specialtyLostModify(SpecialtyLost lost, HttpSession session)
  {
    Json j = new Json();
    try
    {
      SpecialtyLost old = specialtyLostServiceImpl.find(lost.getId());
      if (old.getStatus() == Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING) {
    	  j.setSuccess(false);
          j.setMsg("损失产品已审核通过，不允许修改");
          j.setObj(null);
          return j;
      } else if (old.getStatus() == Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED) {
    	  old.setLostCount(lost.getLostCount());
    	  old.setLostType(lost.getLostType());
    	  old.setLoster(lost.getLoster());
    	  old.setLostReason(lost.getLostReason());
      } else {
    	  old.setLostCount(lost.getLostCount());
    	  old.setLostType(lost.getLostType());
    	  old.setLoster(lost.getLoster());
    	  old.setLostReason(lost.getLostReason());
    	  old.setStatus(Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED);
      }
      specialtyLostServiceImpl.update(old);
      j.setSuccess(true);
      j.setMsg("修改成功");
      j.setObj(null);
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg("修改失败");
      j.setObj(e);
      e.printStackTrace();
    }
    return j;
  }
  
  @RequestMapping({"/delete"})
  @ResponseBody
  public Json specialtyLostDelete(Long lostid, HttpSession session)
  {
    Json j = new Json();
    String username = (String) session.getAttribute(CommonAttributes.Principal);
    try
    {
      SpecialtyLost lost = (SpecialtyLost)this.specialtyLostServiceImpl.find(lostid);
      if (lost != null)
      {
        if (lost.getOperator().getUsername().equals(username)) {
        	specialtyLostServiceImpl.delete(lost);
        	j.setSuccess(true);
            j.setMsg("删除成功");
            j.setObj(lost);
        } else {
        	j.setSuccess(false);
            j.setMsg("无删除权限");
            j.setObj(null);
        }   
      }
      else
      {
        j.setSuccess(false);
        j.setMsg("损失产品不存在");
        j.setObj(null);
      }
    }
    catch (Exception e)
    {
      j.setSuccess(false);
      j.setMsg("查询失败");
      j.setObj(e);
      e.printStackTrace();
    }
    return j;
  }
  
  @RequestMapping({"/outofdate/page/view"})
  @ResponseBody
  public Json specialtyLostOutOfDate(Integer days, String specialtyname, Pageable pageable, HttpSession session, HttpServletRequest request)
  {
    Json j = new Json();
    if (days == null) {
    	j.setSuccess(false);
        j.setMsg("请填写距过期天数");
        j.setObj(null);
        return j;
    }
    try {
		Date date = DateUtil.getDateAfterSpecifiedDays(new Date(), days);
		
		
		List<Filter> filters = new ArrayList<Filter>();
		if (specialtyname != null && !"".equals(specialtyname)) {
			List<Filter> fs = new ArrayList<>();
			fs.add(Filter.like("name", specialtyname));
			List<Specialty> specialties = specialtyServiceImpl.findList(null, fs, null);
			if (specialties.size() == 0) {
				j.setSuccess(true);
		        j.setMsg("查询成功");
		        j.setObj(new Page<Map<String, Object>>(new ArrayList<Map<String, Object>>(), 0, pageable));
		        return j;
			} else {
				List<SpecialtySpecification> specifications = new ArrayList<SpecialtySpecification>();
				for (Specialty specialty : specialties) {
					specifications.addAll(specialty.getSpecifications());
				}
				filters.add(Filter.in("specification", specifications));
			}
		}
		
		filters.add(Filter.le("expiration", date));
		filters.add(Filter.gt("inboundNumber", 0));
		pageable.setFilters(filters);
		Page<Inbound> page = inboundServiceImpl.findPage(pageable);
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		for (Inbound inbound : page.getRows()) {

	
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", inbound.getId());
			map.put("specialtyName", inbound.getSpecification().getSpecialty().getName());
			map.put("specification", inbound.getSpecification().getSpecification());
			map.put("inboundNumber", inbound.getInboundNumber());
			map.put("productDate", inbound.getProductDate());
			map.put("durabilityPeriod", inbound.getDurabilityPeriod());
			map.put("expiration", inbound.getExpiration());
			map.put("outofdate", DateUtil.getDaysBetweenTwoDates(DateUtil.getStartOfDay(new Date()), inbound.getExpiration()));
			
			List<Filter> lostFilters = new ArrayList<>();
			lostFilters.add(Filter.eq("inbound", inbound));
			lostFilters.add(Filter.ne("status", 1));
			List<SpecialtyLost> losts = specialtyLostServiceImpl.findList(null,lostFilters,null);
			if(losts!=null && !losts.isEmpty()) {
				map.put("canLost", false);
			}else {
				map.put("canLost", true);
			}
			lists.add(map);
		}
		Page<Map<String, Object>> result = new Page<Map<String, Object>>(lists, page.getTotal(), pageable);
		j.setSuccess(true);
        j.setMsg("查询成功");
        j.setObj(result);
		
	} catch (Exception e) {
		j.setSuccess(false);
        j.setMsg("查询失败");
        j.setObj(e);
		e.printStackTrace();
	}
    
    
    return j;
  }
  
  @RequestMapping({"/outofdate/defaultdays"})
  @ResponseBody
  public Json defaultDaysForOutOfDate() {
	  Json json = new Json();
	  try {
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("isValid", Boolean.valueOf(true)));
		  filters.add(Filter.eq("settingName", Constants.BUSINESS_SYSTEM_PARAMETER_TYPE_EXPIRATION_DAYS));
		  List<BusinessSystemSetting> list = systemSettingSrv.findList(null, filters, null);
		  if (list.size() > 0) {
			  BusinessSystemSetting setting = list.get(0);
			  Map<String, Object> map = new HashMap<String, Object>();
			  map.put("days",Integer.valueOf(setting.getSettingValue()));
			  map.put("settingName", setting.getSettingName());
			  json.setSuccess(true);
			  json.setMsg("查询成功");
			  json.setObj(map);
		  }
	} catch (Exception e) {
		json.setSuccess(false);
		json.setMsg("查询失败");
		json.setObj(e);
		e.printStackTrace();
	}
	  
	  return json;
  }
}
