package com.hongyu.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.controller.HyDepotController.WrapHyDepot.LabelValue;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Order.Direction;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepot;
import com.hongyu.entity.HyDepotAdmin;
import com.hongyu.entity.Provider;
import com.hongyu.entity.Purchase;
import com.hongyu.entity.PurchaseInbound;
import com.hongyu.entity.PurchaseItem;
import com.hongyu.entity.PurchasePay;
import com.hongyu.entity.PurchaseShip;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.entity.SpecialtyLost;
import com.hongyu.entity.SpecialtyPrice;
import com.hongyu.entity.SpecialtySpecification;
import com.hongyu.entity.WeDivideProportion;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.Inbound;
import com.hongyu.service.BankListService;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepotAdminService;
import com.hongyu.service.HyDepotService;
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
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.hongyu.util.PurchaseSnGenerator;

import sun.swing.StringUIClientPropertyKey;

@Controller
@RequestMapping("/admin/business/purchase")
public class PurchaseController {
	
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
	
	
	//采购单管理的列表
	@RequestMapping(value = "/page/view")
	@ResponseBody
	public Json purchasePage(Purchase purchase, @DateTimeFormat(pattern="yyyy-MM-dd") Date startTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endTime, Long providerid, Pageable pageable, 
			HttpServletRequest request, HttpSession session) {
		Json json = new Json();
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		HyAdmin admin = hyAdminService.find(username);
//		
//		/** 
//		 * 获取用户权限范围
//		 */
//		CheckedOperation co = (CheckedOperation) request.getAttribute("co");
//		Set<Department> departments = (Set<Department>) request.getAttribute("range");
//		
		/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
//		Set<HyAdmin> hyAdmins = new HashSet<HyAdmin>();
//		hyAdmins.add(admin);
//		
//		/** 如果分配的是部门权限 */
//		if(departments.size() > 0){
//			
//			/**
//			 * 遍历所有分配的部门找到所有本部门及下属部门员工
//			 */
//			for(Department department : departments) {
//				
//				/** 加入本部门所有员工 */
//				hyAdmins.addAll(department.getHyAdmins());
//				
//				/** 找到所有下属部门 */
//				String treePaths = department.getTreePath() + department.getId() + ",";
//				List<Filter> filters = new ArrayList<Filter>();
//				Filter filter = Filter.like("treePath", treePaths);
//				filters.add(filter);
//				List<Department> subDepartments = departmentService.findList(null, filters, null);
//				
//				/** 加入下属部门所有员工 */
//				for(Department subDepartment : subDepartments) {
//					hyAdmins.addAll(subDepartment.getHyAdmins());
//				}
//			}
//		}
		
		try {
			if (providerid != null) {
				Provider provider = providerServiceImpl.find(providerid);
				if (provider == null) {
					json.setSuccess(false);
					json.setMsg("供应商不存在");
					return json;
				}
				purchase.setProvider(provider);
			}
			
			List<Filter> filters = new ArrayList<Filter>();
//			filters.add(Filter.in("creater", hyAdmins));
			if (startTime != null) {
				filters.add(new Filter("purchaseTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
			}
			if (endTime != null) {
				filters.add(new Filter("purchaseTime", Operator.le, DateUtil.getEndOfDay(endTime)));
			}
			pageable.setFilters(filters);
			purchase.setIsValid(true);
			Page<Purchase> page = purchaseServiceImpl.findPage(pageable, purchase);
			

			Map<String, Object> pageMap = new HashMap<String, Object>();
			pageMap.put("total", page.getTotal());
			pageMap.put("pageNumber", page.getPageNumber());
			pageMap.put("pageSize", page.getPageSize());
			
			List<Map> list = new ArrayList<Map>();
			for (Purchase p : page.getRows()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", p.getId());
				map.put("purchaseCode", p.getPurchaseCode());
				map.put("purchaseType", p.getPurchaseType());
				if (p.getProvider() != null) {
					map.put("providerName", p.getProvider().getProviderName());
				} else {
					map.put("providerName", null);
				}
				map.put("purchaser", p.getCreator().getName());
				map.put("purchaseTime", p.getPurchaseTime());
				map.put("status", p.getStatus());
				list.add(map);
			}
    		pageMap.put("rows", list);
			
			
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
	
	//经理查看采购详情
	@RequestMapping(value = "/purchasemanager/detail/view")
	@ResponseBody
	public Json purchaseManagerDetail(Long purchaseid, HttpSession session) {
		Json json = new Json();
		try {
			Purchase purchase = purchaseServiceImpl.find(purchaseid);
			if (purchase != null) {
//				Provider provider = purchase.getProvider();
//				if (provider != null) {
//					Provider p = new Provider();
//					p.setProviderName(provider.getProviderName());
//					p.setId(provider.getId());
//					purchase.setProvider(p);
//				}
//				if (purchase.getCreator() != null) {
//					HyAdmin admin = new HyAdmin();
//					admin.setUsername(purchase.getCreator().getUsername());
//					admin.setName(purchase.getCreator().getUsername());
//				}
				for (PurchaseItem item : purchase.getPurchaseItems()) {
					List<Filter> filters = new ArrayList<Filter>();
					filters.add(Filter.eq("purchaseItem", item));
					List<PurchaseInbound> inbounds = purchaseInboundServiceImpl.findList(null, filters, null);
					Integer inboundedQuantity = 0;
					for (PurchaseInbound inbound : inbounds) {
						inboundedQuantity += inbound.getInboundNumber();
					}
					item.setInboundedQuantity(inboundedQuantity);
					
					Integer lostQuantity = Integer.valueOf(0);
			        Integer auditingLostQuantity = Integer.valueOf(0);
			        filters.add(Filter.isNull("inbound"));
			        List<SpecialtyLost> losts = this.specialtyLostServiceImpl.findList(null, filters, null);
			        for (SpecialtyLost lost : losts) {
			            if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED) {
			              auditingLostQuantity = Integer.valueOf(auditingLostQuantity.intValue() + lost.getLostCount().intValue());
			            } else if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING) {
			              lostQuantity = Integer.valueOf(lostQuantity.intValue() + lost.getLostCount().intValue());
			            }
			        }
			        item.setLostQuantity(lostQuantity);
			        item.setAuditedLostQuantity(auditingLostQuantity);
			        item.setSpecialtyName(item.getSpecification().getSpecialty().getName());
				}
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(purchase);
				return json;
			} else {
				json.setSuccess(false);
				json.setMsg("采购单不存在");
				json.setObj(null);
				return json;
			}	
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		return json;
	}
	
	//采购物流列表
	@RequestMapping(value = "/purchasemanager/shiplist/view")
	@ResponseBody
	public Json purchaseMnagerShipDetail(Long purchaseid, HttpSession session) {
		Json json = new Json();
		try {
			Purchase purchase = purchaseServiceImpl.find(purchaseid);
			if (purchase != null) {
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(Filter.eq("purchase", purchase));
				List<PurchaseShip> list = purchaseShipServiceImpl.findList(null, filters, null);
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(list);
				return json;
			} else {
				json.setSuccess(false);
				json.setMsg("采购单不存在");
				json.setObj(null);
				return json;
			}	
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	//采购入库记录列表
	@RequestMapping(value = "/purchasemanager/inboundlist/view")
	@ResponseBody
	public Json purchaseManagerInboundList(Long purchaseid, HttpSession session) {
		Json json = new Json();
		try {
			Purchase purchase = purchaseServiceImpl.find(purchaseid);
			if (purchase != null) {
				List<Filter> filters = new ArrayList<Filter>();
				filters.add(Filter.eq("purchase", purchase));
				List<PurchaseInbound> list = purchaseInboundServiceImpl.findList(null, filters, null);
				for (PurchaseInbound inbound : list) {
					inbound.setSpecialtyName(inbound.getPurchaseItem().getSpecification().getSpecialty().getName());
				}
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(list);
				return json;
			} else {
				json.setSuccess(false);
				json.setMsg("采购单不存在");
				json.setObj(null);
				return json;
			}	
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	//采购管理查看详情
		@RequestMapping(value = "/purchasequdao/detail/view")
		@ResponseBody
		public Json purchaseQudaoDetail(Long purchaseid, HttpSession session) {
			Json json = new Json();
			try {
				Purchase purchase = purchaseServiceImpl.find(purchaseid);
				if (purchase != null) {
//					Provider provider = purchase.getProvider();
//					if (provider != null) {
//						Provider p = new Provider();
//						p.setProviderName(provider.getProviderName());
//						p.setId(provider.getId());
//						purchase.setProvider(p);
//					}
//					if (purchase.getCreator() != null) {
//						HyAdmin admin = new HyAdmin();
//						admin.setUsername(purchase.getCreator().getUsername());
//						admin.setName(purchase.getCreator().getUsername());
//					}
					for (PurchaseItem item : purchase.getPurchaseItems()) {
						List<Filter> filters = new ArrayList<Filter>();
						filters.add(Filter.eq("purchaseItem", item));
						List<PurchaseInbound> inbounds = purchaseInboundServiceImpl.findList(null, filters, null);
						Integer inboundedQuantity = 0;
						for (PurchaseInbound inbound : inbounds) {
							inboundedQuantity += inbound.getInboundNumber();
						}
						item.setInboundedQuantity(inboundedQuantity);
						Integer lostQuantity = Integer.valueOf(0);
				        Integer auditingLostQuantity = Integer.valueOf(0);
				        filters.add(Filter.isNull("inbound"));
				        List<SpecialtyLost> losts = this.specialtyLostServiceImpl.findList(null, filters, null);
				        for (SpecialtyLost lost : losts) {
				            if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED) {
				              auditingLostQuantity = Integer.valueOf(auditingLostQuantity.intValue() + lost.getLostCount().intValue());
				            } else if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING) {
				              lostQuantity = Integer.valueOf(lostQuantity.intValue() + lost.getLostCount().intValue());
				            }
				        }
				        item.setLostQuantity(lostQuantity);
				        item.setAuditedLostQuantity(auditingLostQuantity);
				        item.setSpecialtyName(item.getSpecification().getSpecialty().getName());
					}
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(purchase);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单不存在");
					json.setObj(null);
					return json;
				}	
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
				e.printStackTrace();
			}
			return json;
		}
		
		//采购物流列表
		@RequestMapping(value = "/purchasequdao/shiplist/view")
		@ResponseBody
		public Json purchaseQudaoShipDetail(Long purchaseid, HttpSession session) {
			Json json = new Json();
			try {
				Purchase purchase = purchaseServiceImpl.find(purchaseid);
				if (purchase != null) {
					List<Filter> filters = new ArrayList<Filter>();
					filters.add(Filter.eq("purchase", purchase));
					List<PurchaseShip> list = purchaseShipServiceImpl.findList(null, filters, null);
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(list);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单不存在");
					json.setObj(null);
					return json;
				}	
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
			}
			return json;
		}
		
		//采购入库记录列表
		@RequestMapping(value = "/purchasequdao/inboundlist/view")
		@ResponseBody
		public Json purchaseQudaoInboundList(Long purchaseid, HttpSession session) {
			Json json = new Json();
			try {
				Purchase purchase = purchaseServiceImpl.find(purchaseid);
				if (purchase != null) {
					List<Filter> filters = new ArrayList<Filter>();
					filters.add(Filter.eq("purchase", purchase));
					List<PurchaseInbound> list = purchaseInboundServiceImpl.findList(null, filters, null);
					for (PurchaseInbound inbound : list) {
						inbound.setSpecialtyName(inbound.getPurchaseItem().getSpecification().getSpecialty().getName());
					}
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(list);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单不存在");
					json.setObj(null);
					return json;
				}	
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
			}
			return json;
		}
		
		//库管员查看采购单详情，不包括物流列表和入库列表
		@RequestMapping(value = "/storekeeper/detail/view")
		@ResponseBody
		public Json purchaseStorekeeperDetail(Long purchaseid, HttpSession session) {
			Json json = new Json();
			try {
				Purchase purchase = purchaseServiceImpl.find(purchaseid);
				if (purchase != null) {
//					Provider provider = purchase.getProvider();
//					if (provider != null) {
//						Provider p = new Provider();
//						p.setProviderName(provider.getProviderName());
//						p.setId(provider.getId());
//						purchase.setProvider(p);
//					}
//					if (purchase.getCreator() != null) {
//						HyAdmin admin = new HyAdmin();
//						admin.setUsername(purchase.getCreator().getUsername());
//						admin.setName(purchase.getCreator().getUsername());
//					}
					for (PurchaseItem item : purchase.getPurchaseItems()) {
						List<Filter> filters = new ArrayList<Filter>();
						filters.add(Filter.eq("purchaseItem", item));
						List<PurchaseInbound> inbounds = purchaseInboundServiceImpl.findList(null, filters, null);
						Integer inboundedQuantity = 0;
						for (PurchaseInbound inbound : inbounds) {
							inboundedQuantity += inbound.getInboundNumber();
						}
						item.setInboundedQuantity(inboundedQuantity);
						Integer lostQuantity = Integer.valueOf(0);
				        Integer auditingLostQuantity = Integer.valueOf(0);
				        List<Filter> lostfilters = new ArrayList<Filter>();
				        lostfilters.add(Filter.eq("purchaseItem", item));
				        lostfilters.add(Filter.isNull("inbound"));
				        List<SpecialtyLost> losts = specialtyLostServiceImpl.findList(null, lostfilters, null);
				        for (SpecialtyLost lost : losts) {
				            if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED) {
				              auditingLostQuantity = Integer.valueOf(auditingLostQuantity.intValue() + lost.getLostCount().intValue());
				            } else if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING) {
				              lostQuantity = Integer.valueOf(lostQuantity.intValue() + lost.getLostCount().intValue());
				            }
				        }
				        item.setLostQuantity(lostQuantity);
				        item.setAuditedLostQuantity(auditingLostQuantity);
				        item.setSpecialtyName(item.getSpecification().getSpecialty().getName());
					}
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(purchase);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单不存在");
					json.setObj(null);
					return json;
				}	
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
				e.printStackTrace();
			}
			return json;
		}
		
		//采购单物流列表
		@RequestMapping(value = "/storekeeper/shiplist/view")
		@ResponseBody
		public Json purchaseStorekeeperShipDetail(Long purchaseid, HttpSession session) {
			Json json = new Json();
			try {
				Purchase purchase = purchaseServiceImpl.find(purchaseid);
				if (purchase != null) {
					List<Filter> filters = new ArrayList<Filter>();
					filters.add(Filter.eq("purchase", purchase));
					List<PurchaseShip> list = purchaseShipServiceImpl.findList(null, filters, null);
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(list);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单不存在");
					json.setObj(null);
					return json;
				}	
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
			}
			return json;
		}
		
		//采购入库记录列表
		@RequestMapping(value = "/storekeeper/inboundlist/view")
		@ResponseBody
		public Json purchaseStorekeeperInboundList(Long purchaseid, HttpSession session) {
			Json json = new Json();
			try {
				Purchase purchase = purchaseServiceImpl.find(purchaseid);
				if (purchase != null) {
					List<Filter> filters = new ArrayList<Filter>();
					filters.add(Filter.eq("purchase", purchase));
					List<PurchaseInbound> list = purchaseInboundServiceImpl.findList(null, filters, null);
					for (PurchaseInbound inbound : list) {
						inbound.setSpecialtyName(inbound.getPurchaseItem().getSpecification().getSpecialty().getName());
					}
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(list);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单不存在");
					json.setObj(null);
					return json;
				}	
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
			}
			return json;
		}
		
		//采购管理查看详情
		@RequestMapping(value = "/financer/detail/view")
		@ResponseBody
		public Json purchaseFinancerDetail(Long purchaseid, HttpSession session) {
			Json json = new Json();
			try {
				Purchase purchase = purchaseServiceImpl.find(purchaseid);
				if (purchase != null) {
//					Provider provider = purchase.getProvider();
//					if (provider != null) {
//						Provider p = new Provider();
//						p.setProviderName(provider.getProviderName());
//						p.setId(provider.getId());
//						purchase.setProvider(p);
//					}
//					if (purchase.getCreator() != null) {
//						HyAdmin admin = new HyAdmin();
//						admin.setUsername(purchase.getCreator().getUsername());
//						admin.setName(purchase.getCreator().getUsername());
//					}
					for (PurchaseItem item : purchase.getPurchaseItems()) {
						List<Filter> filters = new ArrayList<Filter>();
						filters.add(Filter.eq("purchaseItem", item));
						List<PurchaseInbound> inbounds = purchaseInboundServiceImpl.findList(null, filters, null);
						Integer inboundedQuantity = 0;
						for (PurchaseInbound inbound : inbounds) {
							inboundedQuantity += inbound.getInboundNumber();
						}
						item.setInboundedQuantity(inboundedQuantity);
						Integer lostQuantity = Integer.valueOf(0);
				        Integer auditingLostQuantity = Integer.valueOf(0);
				        filters.add(Filter.isNull("inbound"));
				        List<SpecialtyLost> losts = this.specialtyLostServiceImpl.findList(null, filters, null);
				        for (SpecialtyLost lost : losts) {
				            if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED) {
				              auditingLostQuantity = Integer.valueOf(auditingLostQuantity.intValue() + lost.getLostCount().intValue());
				            } else if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING) {
				              lostQuantity = Integer.valueOf(lostQuantity.intValue() + lost.getLostCount().intValue());
				            }
				        }
				        item.setLostQuantity(lostQuantity);
				        item.setAuditedLostQuantity(auditingLostQuantity);
				        item.setSpecialtyName(item.getSpecification().getSpecialty().getName());
					}
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(purchase);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单不存在");
					json.setObj(null);
					return json;
				}	
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
				e.printStackTrace();
			}
			return json;
		}
		
		//采购物流列表
		@RequestMapping(value = "/financer/shiplist/view")
		@ResponseBody
		public Json purchaseFinancerShipDetail(Long purchaseid, HttpSession session) {
			Json json = new Json();
			try {
				Purchase purchase = purchaseServiceImpl.find(purchaseid);
				if (purchase != null) {
					List<Filter> filters = new ArrayList<Filter>();
					filters.add(Filter.eq("purchase", purchase));
					List<PurchaseShip> list = purchaseShipServiceImpl.findList(null, filters, null);
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(list);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单不存在");
					json.setObj(null);
					return json;
				}	
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
			}
			return json;
		}
		
		//采购入库记录列表
		@RequestMapping(value = "/financer/inboundlist/view")
		@ResponseBody
		public Json purchaseFinancerInboundList(Long purchaseid, HttpSession session) {
			Json json = new Json();
			try {
				Purchase purchase = purchaseServiceImpl.find(purchaseid);
				if (purchase != null) {
					List<Filter> filters = new ArrayList<Filter>();
					filters.add(Filter.eq("purchase", purchase));
					List<PurchaseInbound> list = purchaseInboundServiceImpl.findList(null, filters, null);
					for (PurchaseInbound inbound : list) {
						inbound.setSpecialtyName(inbound.getPurchaseItem().getSpecification().getSpecialty().getName());
					}
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(list);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单不存在");
					json.setObj(null);
					return json;
				}	
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
			}
			return json;
		}
		
		//采购管理查看详情
		@RequestMapping(value = "/purchaseemployee/detail/view")
		@ResponseBody
		public Json purchaseDetail(Long purchaseid, HttpSession session) {
			Json json = new Json();
			try {
				Purchase purchase = purchaseServiceImpl.find(purchaseid);
				if (purchase != null) {
//					Provider provider = purchase.getProvider();
//					if (provider != null) {
//						Provider p = new Provider();
//						p.setProviderName(provider.getProviderName());
//						p.setId(provider.getId());
//						purchase.setProvider(p);
//					}
//					if (purchase.getCreator() != null) {
//						HyAdmin admin = new HyAdmin();
//						admin.setUsername(purchase.getCreator().getUsername());
//						admin.setName(purchase.getCreator().getUsername());
//					}
					for (PurchaseItem item : purchase.getPurchaseItems()) {
						List<Filter> filters = new ArrayList<Filter>();
						filters.add(Filter.eq("purchaseItem", item));
						List<PurchaseInbound> inbounds = purchaseInboundServiceImpl.findList(null, filters, null);
						Integer inboundedQuantity = 0;
						for (PurchaseInbound inbound : inbounds) {
							inboundedQuantity += inbound.getInboundNumber();
						}
						item.setInboundedQuantity(inboundedQuantity);
						Integer lostQuantity = Integer.valueOf(0);
				        Integer auditingLostQuantity = Integer.valueOf(0);
				        filters.add(Filter.isNull("inbound"));
				        List<SpecialtyLost> losts = this.specialtyLostServiceImpl.findList(null, filters, null);
				        for (SpecialtyLost lost : losts) {
				            if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED) {
				              auditingLostQuantity = Integer.valueOf(auditingLostQuantity.intValue() + lost.getLostCount().intValue());
				            } else if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING) {
				              lostQuantity = Integer.valueOf(lostQuantity.intValue() + lost.getLostCount().intValue());
				            }
				        }
				        item.setLostQuantity(lostQuantity);
				        item.setAuditedLostQuantity(auditingLostQuantity);
				        item.setSpecialtyName(item.getSpecification().getSpecialty().getName());
					}
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(purchase);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单不存在");
					json.setObj(null);
					return json;
				}	
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
				e.printStackTrace();
			}
			return json;
		}
		
		//采购物流列表
		@RequestMapping(value = "/purchaseemployee/shiplist/view")
		@ResponseBody
		public Json purchaseShipDetail(Long purchaseid, HttpSession session) {
			Json json = new Json();
			try {
				Purchase purchase = purchaseServiceImpl.find(purchaseid);
				if (purchase != null) {
					List<Filter> filters = new ArrayList<Filter>();
					filters.add(Filter.eq("purchase", purchase));
					List<PurchaseShip> list = purchaseShipServiceImpl.findList(null, filters, null);
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(list);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单不存在");
					json.setObj(null);
					return json;
				}	
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
			}
			return json;
		}
		
		//采购入库记录列表
		@RequestMapping(value = "/purchaseemployee/inboundlist/view")
		@ResponseBody
		public Json purchaseInboundList(Long purchaseid, HttpSession session) {
			Json json = new Json();
			try {
				Purchase purchase = purchaseServiceImpl.find(purchaseid);
				if (purchase != null) {
					List<Filter> filters = new ArrayList<Filter>();
					filters.add(Filter.eq("purchase", purchase));
					List<PurchaseInbound> list = purchaseInboundServiceImpl.findList(null, filters, null);
					for (PurchaseInbound inbound : list) {
						inbound.setSpecialtyName(inbound.getPurchaseItem().getSpecification().getSpecialty().getName());
					}
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(list);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单不存在");
					json.setObj(null);
					return json;
				}	
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
			}
			return json;
		}
	
	
	//修改采购单
	@RequestMapping(value = "/modify")
	@ResponseBody
	public Json purchaseModify(Purchase purchase, String purchaseDate, HttpSession session) {
		Json json = new Json();
		
		try {
			if (purchase.getPurchaseItems() != null) {
				for (PurchaseItem item : purchase.getPurchaseItems()) {
					SpecialtySpecification specification = specialtySpecificationSrv.find(item.getSpecification().getId());
					//设置每一个采购明细对应的特产规格
					item.setSpecification(specification);
					//设置每一个采购明细对应的采购单
					item.setPurchase(purchase);
				}
			}
			
			if (purchase.getProvider() != null) {
				Provider provider = providerServiceImpl.find(purchase.getProvider().getId());
				//设置采购单的供应商
				purchase.setProvider(provider);
			}
			
			purchase.setPurchaseTime(yyyy_MM_dd.parse(purchaseDate));
			purchaseServiceImpl.update(purchase, "status", "reviewTime", "reviewTime", "advanceTime"
					, "setDivideProportionTime", "setShipInfoTime", "balancePaySubmitTime", "balancePayTime"
					, "receiveTime", "creator", "createTime");
			json.setSuccess(true);
			json.setMsg("修改成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("修改失败");
			json.setObj(e);
		}
		
		return json;
	}
	
//		//每种角色查询自己的待办事项
//		@RequestMapping(value = "/taskpage/view")
//		@ResponseBody
//		public Json purchaseTaskToDo(Pageable pageable, HttpSession session) {
//			Json json = new Json();
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			
//			try {
//				//是否能查到asignee的
//				List<Task> taskList = taskService.createTaskQuery()
//						.taskCandidateUser(username)
//						.list();
//				List<String> processInstanceIds = new ArrayList<String>();
//				for (Task task : taskList) {
//					processInstanceIds.add(task.getProcessInstanceId());
//				}
//				
//				List<Filter> filters = new ArrayList<Filter>();
//				filters.add(Filter.in("processInstanceId", processInstanceIds));
//				List<Order> orders = new ArrayList<Order>();
//				orders.add(Order.asc("id"));
//				pageable.setFilters(filters);
//				pageable.setOrders(orders);
//				
//				Page<Purchase> page = purchaseServiceImpl.findPage(pageable, new Purchase());
//				json.setMsg("查询成功");
//				json.setSuccess(true);
//				json.setObj(page);
//			} catch (Exception e) {
//				json.setMsg("查询失败");
//				json.setSuccess(false);
//				json.setObj(e);
//				e.printStackTrace();
//			}
//			
//			
//			return json;
//		}
//		
//		//每种角色查询自己的历史经办事务
//		@RequestMapping(value = "/historytaskpage/view")
//		@ResponseBody
//		public Json purchaseHistoryTask(Pageable pageable, HttpSession session) {
//			Json json = new Json();
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			
//			try {
//				List<HistoricTaskInstance> histList=historyService.createHistoricTaskInstanceQuery()
//						.taskAssignee(username)
//						.list();
//				List<String> processInstances = new ArrayList<String>();
//				
//				//可能会影响效率
//				for (HistoricTaskInstance instance : histList) {
//					processInstances.add(instance.getProcessInstanceId());
//				}
//				
//				List<Filter> filters = new ArrayList<Filter>();
//				filters.add(Filter.in("processInstanceId", processInstances));
//				List<Order> orders = new ArrayList<Order>();
//				orders.add(Order.asc("id"));
//				pageable.setFilters(filters);
//				pageable.setOrders(orders);
//				
//				Page<Purchase> page = purchaseServiceImpl.findPage(pageable, new Purchase());
//				json.setMsg("查询成功");
//				json.setSuccess(true);
//				json.setObj(page);
//			} catch (Exception e) {
//				json.setMsg("查询失败");
//				json.setSuccess(false);
//				json.setObj(e);
//				e.printStackTrace();
//			}
//			
//			
//			return json;
//		}
		
		//采购部员工查询采购单列表
		@RequestMapping(value="/purchaseemployee/page/view")
		@ResponseBody
		public Json purchaseEmployeeList(@DateTimeFormat(pattern = "yyyy-MM-dd") Date startdate, @DateTimeFormat(pattern = "yyyy-MM-dd") Date enddate,
				Integer status, Integer purchasetype, String providername, Pageable pageable, HttpSession session) {
			Json j = new Json();
			
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin user = hyAdminService.find(username);
			
			//设置查询条件
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("creator", user));
			if (status != null) {
				filters.add(Filter.eq("status", status));
			}
			if (startdate != null) {
				filters.add(Filter.ge("purchaseTime", DateUtil.getStartOfDay(startdate)));
			}
			if (enddate != null) {
				filters.add(Filter.le("purchaseTime", DateUtil.getEndOfDay(enddate)));
			}
			if (providername != null) {
				List<Filter> providerfilters = new ArrayList<Filter>();
				providerfilters.add(Filter.like("providerName", providername));
				List<Provider> providers = providerServiceImpl.findList(null, providerfilters, null);
				if (providers.size() == 0) {
					Page<Purchase> page = new Page<>(new ArrayList<Purchase>(), 0, pageable);
					j.setSuccess(true);
					j.setObj(page);
					j.setMsg("查询成功");
					return j;
				}
				filters.add(Filter.in("provider", providers));
			}
			if (purchasetype != null) {
				filters.add(Filter.eq("purchaseType", purchasetype));
			}
			pageable.setFilters(filters);
			
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			
			try {
				Page<Purchase> page = purchaseServiceImpl.findPage(pageable);
				j.setSuccess(true);
				j.setObj(page);
				j.setMsg("查询成功");
			} catch (Exception e) {
				j.setSuccess(false);
				j.setObj(e);
				j.setMsg("查询失败");
				e.printStackTrace();
			}
			
			return j;
		}
		
		//====================================================================================================
		/**采购部员工 新建采购单*/
		@RequestMapping(value = "/purchaseemployee/add")
		@ResponseBody
		public Json purchaseAdd(@RequestBody Purchase purchase, HttpSession session) {
			Json json = new Json();
			//根据当前登录用户获取用户名称,并指定这个阶段任务的操作人
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin creator = hyAdminService.find(username);
			try {
				Map<String, Object> variables = new HashMap<String,Object>();
				variables.put("applyerName", username);
				//启动工作流程
				ProcessInstance pi= runtimeService.startProcessInstanceByKey("StockInProcess",variables); //"StockInProcess"为bpmn文件中的key
				purchase.setProcessInstanceId(pi.getProcessInstanceId());
				// 根据流程实例Id获取任务
				Task task=taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult(); 
				
				if (purchase.getPurchaseItems() != null) {
					BigDecimal totalMoney = new BigDecimal(0.00);
					for (PurchaseItem item : purchase.getPurchaseItems()) {
						SpecialtySpecification specification = specialtySpecificationSrv.find(item.getSpecification().getId());
						//设置每一个采购明细对应的特产规格
						item.setSpecification(specification);
						//设置每一个采购明细对应的采购单
						item.setPurchase(purchase);
						totalMoney = totalMoney.add(item.getCostPrice().multiply(new BigDecimal(item.getQuantity())));
					}
					purchase.setTotalMoney(totalMoney);
				}
			
				if (purchase.getProvider() != null) {
					Provider provider = providerServiceImpl.find(purchase.getProvider().getId());
					//设置采购单的供应商
					purchase.setProvider(provider);
				}
				purchase.setCreator(creator);				
				synchronized (lock) {
		  			  List<Filter> fs = new ArrayList<Filter>();
		  			  fs.add(Filter.in("type", SequenceTypeEnum.purchaseSn));
		  			  List<CommonSequence> ss = commonSequenceService.findList(null, fs, null);
		  			  CommonSequence c = ss.get(0);
		  			  Long value = c.getValue() + 1;
		  			  c.setValue(value);
		  			  commonSequenceService.update(c);
		  			  purchase.setPurchaseCode(PurchaseSnGenerator.getSN(value));
				  }
				purchaseServiceImpl.save(purchase);
				// 完成任务		
				taskService.complete(task.getId());
				json.setSuccess(true);
				json.setMsg("添加成功");
				json.setObj(null);
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("添加失败");
				json.setObj(e);
			}
		
			return json;
		}
		
		//====================================================================================================
		/**采购部员工修改被驳回的采购单*/
		@RequestMapping(value = "/purchaseemployee/modifyrejected")
		@ResponseBody
		public Json purchaseModifyRejectedPurchase(@RequestBody Purchase purchase, HttpSession session) {
			Json json = new Json();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin creator = hyAdminService.find(username);
			Purchase oldPurchase = purchaseServiceImpl.find(purchase.getId());
			try {
				if (oldPurchase.getStatus() == Constants.PURCHASE_STATUS_REJECTED) {
					if (purchase.getPurchaseItems() != null) {
						BigDecimal totalMoney = new BigDecimal(0);
						for (PurchaseItem item : purchase.getPurchaseItems()) {
							SpecialtySpecification specification = specialtySpecificationSrv.find(item.getSpecification().getId());
							//设置每一个采购明细对应的特产规格
							item.setSpecification(specification);
							item.setIsValid(true);
							//设置每一个采购明细对应的采购单
							item.setPurchase(oldPurchase);
							item.setState(false);
							totalMoney = totalMoney.add(item.getCostPrice().multiply(new BigDecimal(item.getQuantity())));
						}
						purchase.setTotalMoney(totalMoney);
					}
						
					if (purchase.getProvider() != null) {
						Provider provider = providerServiceImpl.find(purchase.getProvider().getId());
						//设置采购单的供应商
						purchase.setProvider(provider);
					}
						
					purchase.setStatus(Constants.PURCHASE_STATUS_WAIT_FOR_AUDITED);
					purchase.setPurchaseTime(new Date());
					purchase.setCreator(creator);
					purchase.setIsValid(true);
					//根据当前登录用户获取用户名称,并指定这个阶段任务的操作人
					Map<String, Object> variables = new HashMap<String,Object>();
					variables.put("applyerName", username);
					// 根据流程实例Id获取任务
					Task task=taskService.createTaskQuery().processInstanceId(oldPurchase.getProcessInstanceId()).singleResult();  
					purchaseServiceImpl.update(purchase, "purchaseCode", "advanceTime",
							"setDivideProportionTime", "setShipInfoTime", "balancePaySubmitTime", "balancePayTime", "receiveTime", "processInstanceId");
					// 完成 任务		
					taskService.complete(task.getId());
					json.setSuccess(true);
					json.setMsg("修改成功");
					json.setObj(null);
				} else {
					json.setSuccess(false);
					json.setMsg("采购单未处于可修改状态");
					json.setObj(null);
				}
				
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("修改失败");
				json.setObj(e);
			}
				
			return json;
		}
		
		//====================================================================================================
		/**采购部员工查看被驳回的理由*/
		@RequestMapping(value = "/purchaseemployee/rejectedreason/view")
		@ResponseBody
		public Json purchaseModifyRejectedPurchase(Long purchaseid, HttpSession session) {
			Json json = new Json();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin creator = hyAdminService.find(username);
			Purchase purchase = purchaseServiceImpl.find(purchaseid);
			try {
				if (purchase.getStatus() == Constants.PURCHASE_STATUS_REJECTED) {
					//获取审批意见
					List<Comment> commentList=null;
					commentList=taskService.getProcessInstanceComments(purchase.getProcessInstanceId());
					
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(commentList);
				} else {
					json.setSuccess(false);
					json.setMsg("查询失败");
					json.setObj(null);
				}
				
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(e);
			}
				
			return json;
		}
	

		
//		/**采购部员工 提交申请 启动流程*/
//		@RequestMapping(value = "/submit")
//		@ResponseBody
//		public Json purchaseSubmit(Long purchaseid, HttpSession session){
//			Json json = new Json();
//			Purchase purchase = purchaseServiceImpl.find(purchaseid);
//			//根据当前登录用户获取用户名称,并指定这个阶段任务的操作人
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			Map<String, Object> variables = new HashMap<String,Object>();
//			variables.put("applyerName", username);
//			ProcessInstance pi= runtimeService.startProcessInstanceByKey("StockInProcess",variables); //"StockInProcess"为bpmn文件中的key
//			purchase.setProcessInstanceId(pi.getProcessInstanceId());
//			// 根据流程实例Id获取任务
//			Task task=taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult(); 
//			// 完成 任务		
//			taskService.complete(task.getId()); 
//			//更新申请状态
//			return json;
//		}
		

		//采购部经理查询采购单列表
		@RequestMapping(value="/purchasemanager/page/view")
		@ResponseBody
		public Json purchaseManagerList(@DateTimeFormat(pattern="yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate, Integer status, String creator, 
				String purchaseCode, Pageable pageable, HttpSession session) {
			Json j = new Json();
		    
		    String username = (String)session.getAttribute("principal");
		    
		    List<Filter> filters = new ArrayList();
		    if (status != null) {
		    	if (status == 1) {
		    		filters.add(Filter.ne("status", Constants.PURCHASE_STATUS_WAIT_FOR_AUDITED));
		    	} else {
		    		filters.add(Filter.eq("status", status));
		    	}
		    }
		    if (startDate != null) {
		    	System.out.println(DateUtil.getStartOfDay(startDate));
		      filters.add(Filter.ge("purchaseTime", DateUtil.getStartOfDay(startDate)));
		    }
		    if (endDate != null) {
		    	System.out.println(DateUtil.getEndOfDay(endDate));
		      filters.add(Filter.le("purchaseTime", DateUtil.getEndOfDay(endDate)));
		    }
		    if (StringUtils.isNotEmpty(purchaseCode)) {
		      filters.add(Filter.like("purchaseCode", purchaseCode));
		    }
		    if (StringUtils.isNotEmpty(creator))
		    {
		      List<Filter> fs = new ArrayList();
		      fs.add(Filter.like("name", creator));
		      List<HyAdmin> hyadmins = this.hyAdminService.findList(null, fs, null);
		      filters.add(Filter.in("creator", hyadmins));
		    }
		    pageable.setFilters(filters);
		    
		    List<Order> orders = new ArrayList();
		    orders.add(Order.desc("id"));
		    pageable.setOrders(orders);
		    try
		    {
		      Page<Purchase> page = this.purchaseServiceImpl.findPage(pageable);
		      j.setSuccess(true);
		      j.setObj(page);
		      j.setMsg("查询成功");
		    }
		    catch (Exception e)
		    {
		      j.setSuccess(false);
		      j.setObj(e);
		      j.setMsg("查询失败");
		      e.printStackTrace();
		    }
		    return j;
		  }
		
		/**经理审核*/
		@RequestMapping(value = "/purchasemanager/audit")
		@ResponseBody
		public Json purchaseManagerAudit(Long purchaseid, Boolean isApproved, String comment, HttpSession session){
			Json json = new Json();
			//页面应传入任务id或流程实例id、申请表id 和 审核结果
			
//			Authentication.setAuthenticatedUserId(username); 
//			taskService.addComment(task.getId(), processInstanceId, comment);
//			taskService.complete(task.getId());
			Purchase purchase = purchaseServiceImpl.find(purchaseid);
			
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username); //获取当前用户
			// 根据流程实例Id获取任务
			Task task=taskService.createTaskQuery().processInstanceId(purchase.getProcessInstanceId()).singleResult(); 
			Map<String, Object> variables = new HashMap<String,Object>();
			
			try {
				//经理审核通过
				if (isApproved) {
					//根据condition,控制流程的走向
					variables.put("condition", purchase.getPurchaseType());
					variables.put("state", "true");
					
					//如果是全款
					if (purchase.getPurchaseType() == Constants.PURCHASE_TYPE_FULL_PAYMENT) {
						purchase.setStatus(Constants.PURCHASE_STATUS_WAIT_FOR_PAID);
						purchase.setBalancePaySubmitTime(purchase.getPurchaseTime());
						//生成待付款单
						PurchasePay pay = new PurchasePay();
						pay.setAdvanceAmount(purchase.getTotalMoney());
						pay.setPurchase(purchase);
						pay.setPayeeName(purchase.getProvider().getAccountName());
						pay.setPayeeBank(purchase.getProvider().getBankName());
						pay.setPayeeAccount(purchase.getProvider().getBankAccount());
						purchasePayServiceImpl.save(pay);
						
					} else if (purchase.getPurchaseType() == Constants.PURCHASE_TYPE_PARTIAL_PAYMENT ) { //如果是部分款
						purchase.setStatus(Constants.PURCHASE_STATUS_WAIT_FOR_PAID);
						//生成待付款单
						PurchasePay pay = new PurchasePay();
						pay.setAdvanceAmount(purchase.getAdvanceAmount());
						pay.setPurchase(purchase);
						pay.setPayeeName(purchase.getProvider().getAccountName());
						pay.setPayeeBank(purchase.getProvider().getBankName());
						pay.setPayeeAccount(purchase.getProvider().getBankAccount());
						purchasePayServiceImpl.save(pay);
					} else {
						purchase.setStatus(Constants.PURCHASE_STATUS_WAIT_FOR_SET_SHIP_INFO);
					}
//				purchaseServiceImpl.update(purchase, "purchaseCode", "purchaseType", "provider", "advanceAmount", "purchaseTime", "advanceTime",
//						"setDivideProportionTime", "setShipInfoTime", "balancePaySubmitTime", "balancePayTime", "receiveTime", "creator",
//						"isValid", "processInstanceId", "");
					//审核通过暂时不需要审核理由
//					Authentication.setAuthenticatedUserId(username); 
//					taskService.addComment(task.getId(), purchase.getProcessInstanceId(), comment);			
				} else {
					purchase.setStatus(Constants.PURCHASE_STATUS_REJECTED);
					Authentication.setAuthenticatedUserId(username); 
					taskService.addComment(task.getId(), purchase.getProcessInstanceId(), comment);
					variables.put("state", "false");
				}
				
				taskService.complete(task.getId(), variables);
				
				purchase.setReviewTime(new Date());
				purchaseServiceImpl.update(purchase);
				json.setSuccess(true);
				json.setMsg("审核成功");
				json.setObj(null);
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("审核失败");
				json.setObj(e);
				e.printStackTrace();
			}
				
			return json;
		}
		
		
		
		//采购部财务查询采购单付款记录
		@RequestMapping(value="/financer/page/view")
		@ResponseBody
		public Json purchaseFinancerList(@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
				Boolean status, String purchaseCode, Pageable pageable, HttpSession session) {
			//财务能查看全部采购单,status为false时表示查看待付款的，status为true时表示查看已付款的
			Json j = new Json();
			
			//设置查询条件
			List<Filter> filters = new ArrayList<Filter>();
			if (startDate != null) {
				filters.add(Filter.ge("purchaseTime", DateUtil.getStartOfDay(startDate)));
			}
			if (endDate != null) {
				filters.add(Filter.le("purchaseTime", DateUtil.getEndOfDay(endDate)));
			}
			if (status != null) {
				filters.add(Filter.eq("isPaid", status));
			}
			if (StringUtils.isNotEmpty(purchaseCode)) {
				List<Filter> fs = new ArrayList<Filter>();
				fs.add(Filter.like("purchaseCode", purchaseCode));
				List<Purchase> purchases = purchaseServiceImpl.findList(null, fs, null);
				if (purchases.size() == 0) {
					j.setSuccess(true);
			        j.setMsg("查询成功");
			        j.setObj(new Page<PurchasePay>(new ArrayList<PurchasePay>(), 0, pageable));
			        return j;
				} else {
					filters.add(Filter.in("purchase", purchases));
				}
			}
			
			pageable.setFilters(filters);
			
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			
			try {
				Page<PurchasePay> page = purchasePayServiceImpl.findPage(pageable);
				j.setSuccess(true);
				j.setObj(page);
				j.setMsg("查询成功");
			} catch (Exception e) {
				j.setSuccess(false);
				j.setObj(e);
				j.setMsg("查询失败");
				e.printStackTrace();
			}
			
			return j;
		}
		
		//采购部财务查询采购单付款记录
		@RequestMapping(value="/financer/banklist/view")
		@ResponseBody
		public Json purchaseFinancerBankList(HttpSession session) {
			Json j = new Json();
			try {
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("type", BankList.BankType.businessBank));
				List<BankList> banklist = bankListService.findList(null, filters, null);
				List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
				for (BankList list : banklist) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("payerName", list.getAccountName());
					m.put("bankName", list.getBankName());
					m.put("bankCode", list.getBankCode());
					m.put("bankAccount", list.getBankAccount());
					lists.add(m);
				}
				j.setSuccess(true);
				j.setMsg("查询成功");
				j.setObj(lists);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				j.setSuccess(false);
				j.setMsg("查询失败");
				j.setObj(e);
			}
			
			
			
			return j;
		}
		
		
//		/**
//		 * 
//		 * 财务付款
//		 *
//		 */
//		@RequestMapping("/financer/pay")
//		@ResponseBody
//		public Json puchaseFinancerPay(PurchasePay pay, HttpSession session) {
//			Json json = new Json();
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin admin = hyAdminService.find(username); //获取当前用户
//			PurchasePay old = purchasePayServiceImpl.find(pay.getId());
//			Purchase purchase = old.getPurchase();
//			
//			
//			try {
//				if (purchase.getStatus() == Constants.PURCHASE_STATUS_WAIT_FOR_PAID) {
//					Task task=taskService.createTaskQuery().processInstanceId(
//							purchase.getProcessInstanceId()).singleResult();
//					pay.setPurchase(purchase);
//					
//					purchasePayServiceImpl.save(pay);
//					//不需要考虑是全款还是部分款
//					purchase.setStatus(Constants.PURCHASE_STATUS_WAIT_FOR_SET_DIVIDE_PROPORTION);
//					purchase.setAdvanceTime(new Date());
//					purchaseServiceImpl.update(purchase);
//					
//					taskService.claim(task.getId(), username);
//					taskService.complete(task.getId());
//					json.setSuccess(true);
//					json.setMsg("提交成功");
//					json.setObj(null);
//				} else {
//					json.setSuccess(false);
//					json.setMsg("采购单未处于可付款状态");
//					json.setObj(null);
//				}		
//				
//			} catch (Exception e) {
//				json.setSuccess(false);
//				json.setMsg("采购单未处于可付款状态");
//				json.setObj(e);
//				e.printStackTrace();
//			}
//			
//			return json;
//		}
		
		
//		//渠道销售查询采购单列表
//		@RequestMapping(value="/purchasequdao/page/view")
//		@ResponseBody
//		public Json purchaseQudaoSalesList(@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
//				Integer status, Pageable pageable, HttpSession session) {
//			//渠道销售能查看全部采购单,status为0时表示查看待设置提成比例的，status为1时表示查看已设置提成比例的
//			Json j = new Json();
//			
//			//设置查询条件
//			List<Filter> filters = new ArrayList<Filter>();
//			if (startDate != null) {
//				filters.add(Filter.ge("purchaseTime", DateUtil.getStartOfDay(startDate)));
//			}
//			if (endDate != null) {
//				filters.add(Filter.le("purchaseTime", DateUtil.getEndOfDay(endDate)));
//			}
//			//查找待设置提成比例的
//			if (status == 0) {
//				filters.add(Filter.eq("status", Constants.PURCHASE_STATUS_WAIT_FOR_SET_DIVIDE_PROPORTION));
//			} else {
//				//已设置提成比例的
//				List<Integer> statuses = new ArrayList<Integer>();
//				statuses.add(Constants.PURCHASE_STATUS_WAIT_FOR_SET_SHIP_INFO);
//				statuses.add(Constants.PURCHASE_STATUS_INBOUNDING);
//				statuses.add(Constants.PURCHASE_STATUS_FINISH_INBOUND);
//				statuses.add(Constants.PURCHASE_STATUS_WAIT_FOR_BALANCE);
//				statuses.add(Constants.PURCHASE_STATUS_FINISH_BALANCE);
//				filters.add(Filter.in("status", statuses));
//			}
//			
//			pageable.setFilters(filters);
//			
//			List<Order> orders = new ArrayList<Order>();
//			orders.add(Order.asc("id"));
//			pageable.setOrders(orders);
//			
//			try {
//				Page<Purchase> page = purchaseServiceImpl.findPage(pageable);
//				j.setSuccess(true);
//				j.setObj(page);
//				j.setMsg("查询成功");
//			} catch (Exception e) {
//				j.setSuccess(false);
//				j.setObj(e);
//				j.setMsg("查询失败");
//				e.printStackTrace();
//			}
//			
//			return j;
//		}
		
		//渠道销售查询采购单明细列表
		@RequestMapping(value="/purchasequdao/page/view")
		@ResponseBody
		public Json purchaseQudaoSalesList(Integer status, String purchasecode, Pageable pageable, HttpSession session) {
			//渠道销售能查看全部采购单,status为0时表示查看待设置提成比例的，status为1时表示查看已设置提成比例的
			Json j = new Json();
			
			//设置查询条件
			List<Filter> filters = new ArrayList<Filter>();
			//查找待设置提成比例的
			if ((status != null)) {
				if (status == 0) {
					filters.add(Filter.eq("setState", false));
				} else {
					filters.add(Filter.eq("setState", true));
				}
			}
			
			if (purchasecode != null)
			{
			    List<Filter> ft = new ArrayList();
			    ft.add(Filter.like("purchaseCode", purchasecode));
//			    if (status != null) {
//			    	if (status == 0) {
//			    		ft.add(Filter.eq("status", Constants.PURCHASE_STATUS_WAIT_FOR_SET_DIVIDE_PROPORTION));
//			    	} else if (status == 1) {
//			    		ft.add(Filter.gt("status", Constants.PURCHASE_STATUS_WAIT_FOR_SET_DIVIDE_PROPORTION));
//			    		ft.add(Filter.le("status", Constants.PURCHASE_STATUS_FINISH_BALANCE));
//			    	}
//			    } else {
//			    	ft.add(Filter.ge("status", Constants.PURCHASE_STATUS_WAIT_FOR_SET_DIVIDE_PROPORTION));
//		    		ft.add(Filter.le("status", Constants.PURCHASE_STATUS_FINISH_BALANCE));
//			    }
			    List<Purchase> purchases = purchaseServiceImpl.findList(null, ft, null);
			    if (purchases.size() > 0) {
			    	filters.add(Filter.in("purchase", purchases));
			    } else {
			    	j.setSuccess(false);
					j.setObj(new Page<PurchaseItem>(new ArrayList<PurchaseItem>(), 0, pageable));
					j.setMsg("查询成功");
					return j;
			    }
			} 
//			else {
//				List<Filter> ft = new ArrayList();
//				 if (status != null) {
//				    	if (status == 0) {
//				    		ft.add(Filter.eq("status", Constants.PURCHASE_STATUS_WAIT_FOR_SET_DIVIDE_PROPORTION));
//				    	} else if (status == 1) {
//				    		ft.add(Filter.gt("status", Constants.PURCHASE_STATUS_WAIT_FOR_SET_DIVIDE_PROPORTION));
//				    		ft.add(Filter.le("status", Constants.PURCHASE_STATUS_FINISH_BALANCE));
//				    	}
//				 } else {
//				    	ft.add(Filter.ge("status", Constants.PURCHASE_STATUS_WAIT_FOR_SET_DIVIDE_PROPORTION));
//			    		ft.add(Filter.le("status", Constants.PURCHASE_STATUS_FINISH_BALANCE));
//				 }
//				 List<Purchase> purchases = purchaseServiceImpl.findList(null, ft, null);
//				    if (purchases.size() > 0) {
//				    	filters.add(Filter.in("purchase", purchases));
//				    } else {
//				    	j.setSuccess(false);
//						j.setObj(new Page<PurchaseItem>(new ArrayList<PurchaseItem>(), 0, pageable));
//						j.setMsg("查询成功");
//						return j;
//				    }
//			}
			
			
			pageable.setFilters(filters);
			
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			
			try {
				Page<PurchaseItem> page = purchaseItemServiceImpl.findPage(pageable);
				for (PurchaseItem item : page.getRows()) {
					item.setSpecialtyName(item.getSpecification().getSpecialty().getName());
					item.setPurchaseCode(item.getPurchase().getPurchaseCode());
				}
				j.setSuccess(true);
				j.setObj(page);
				j.setMsg("查询成功");
			} catch (Exception e) {
				j.setSuccess(false);
				j.setObj(e);
				j.setMsg("查询失败");
				e.printStackTrace();
			}
			
			return j;
		}
		
		
//		static class SetDivideProportion {
//			Long purchaseid;
//			List<PurchaseItem> items;
//			public Long getPurchaseid() {
//				return purchaseid;
//			}
//			public void setPurchaseid(Long purchaseid) {
//				this.purchaseid = purchaseid;
//			}
//			public List<PurchaseItem> getItems() {
//				return items;
//			}
//			public void setItems(List<PurchaseItem> items) {
//				this.items = items;
//			}
//		}
//		//渠道销售设置提成比例
//		@RequestMapping(value = "/qudao/setproportion")
//		@ResponseBody
//		public Json purchaseSalesSetDivideProportion(@RequestBody SetDivideProportion setDivideProportion, HttpSession session) {
//			Json json = new Json();
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin admin = hyAdminService.find(username); //获取当前用户
//			Purchase purchase = purchaseServiceImpl.find(setDivideProportion.getPurchaseid());
//			try {
//				if (purchase.getStatus() == Constants.PURCHASE_STATUS_WAIT_FOR_SET_DIVIDE_PROPORTION) {
//					// 根据流程实例Id获取任务
//					Task task=taskService.createTaskQuery().processInstanceId(purchase.getProcessInstanceId()).singleResult(); 
//					
//					for (PurchaseItem item : purchase.getPurchaseItems()) {
//						for (PurchaseItem toModify : setDivideProportion.getItems()) {
//							if (toModify.getId() == item.getId()) {
//								item.setStoreDivide(toModify.getStoreDivide());
//								item.setBusinessPersonDivide(toModify.getBusinessPersonDivide());
//								item.setExterStoreDivide(toModify.getExterStoreDivide());
//								break;
//							}
//						}
//					}
//					
//					purchase.setSetDivideProportionTime(new Date());
//					purchase.setPurchaseType(Constants.PURCHASE_STATUS_WAIT_FOR_SET_SHIP_INFO);
//					purchaseServiceImpl.update(purchase);
//					taskService.claim(task.getId(), username);
//					taskService.complete(task.getId());
//					json.setSuccess(true);
//					json.setMsg("设置成功");
//					json.setObj(null);
//					return json;
//				} else {
//					json.setSuccess(false);
//					json.setMsg("采购单未处于待设置提成比例状态");
//					json.setObj(null);
//					return json;
//				}
//				
//			} catch (Exception e) {
//				json.setSuccess(false);
//				json.setMsg("设置失败");
//				json.setObj(e);
//				e.printStackTrace();
//			}
//			
//			
//			return json;
//			
//		}
		
		//渠道销售设置提成比例
//		@RequestMapping(value = "/purchasequdao/setproportion")
//		@ResponseBody
//		public Json purchaseSalesSetDivideProportion(Long itemid, BigDecimal storeDivide, BigDecimal exterStoreDivide, BigDecimal businessPersonDivide, HttpSession session) {
//			Json json = new Json();
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin admin = hyAdminService.find(username); //获取当前用户
//			PurchaseItem item = purchaseItemServiceImpl.find(itemid);
//			Purchase purchase = item.getPurchase();
//			try {
//				if (purchase.getStatus() == Constants.PURCHASE_STATUS_WAIT_FOR_SET_DIVIDE_PROPORTION) {
//					boolean finished = true;
//					for (PurchaseItem pt : purchase.getPurchaseItems()) {
//						if (pt.getId() != itemid && !pt.getSetState()) {
//							finished = false;
//							break;
//						}
//					}
//					
//					item.setBusinessPersonDivide(businessPersonDivide);
//					item.setExterStoreDivide(exterStoreDivide);
//					item.setStoreDivide(storeDivide);
//					item.setSetProportionOperator(admin);
//					item.setSetProportionTime(new Date());
//					item.setSetState(true);
//				
//					//如果可以完成设置提成比例状态
//					if (finished) {
//						// 根据流程实例Id获取任务
//						purchase.setStatus(Constants.PURCHASE_STATUS_WAIT_FOR_SET_SHIP_INFO);
//						purchase.setSetDivideProportionTime(new Date());
//						purchaseServiceImpl.update(purchase);
//						Task task=taskService.createTaskQuery().processInstanceId(purchase.getProcessInstanceId()).singleResult();
//						taskService.complete(task.getId());
//					} else {
//						purchaseServiceImpl.update(purchase);
//					}
//					json.setSuccess(true);
//					json.setMsg("设置成功");
//					json.setObj(null);
//					return json;
//					
//				} else {
//					json.setSuccess(false);
//					json.setMsg("采购单未处于待设置提成比例状态");
//					json.setObj(null);
//					return json;
//				}
//				
//			} catch (Exception e) {
//				json.setSuccess(false);
//				json.setMsg("设置失败");
//				json.setObj(e);
//				e.printStackTrace();
//			}
//			
//			
//			return json;
//			
//		}
		
		static class SetShipInfo {
			Long purchaseid;
			List<PurchaseShip> shipinfos;
			public Long getPurchaseid() {
				return purchaseid;
			}
			public void setPurchaseid(Long purchaseid) {
				this.purchaseid = purchaseid;
			}
			public List<PurchaseShip> getShipinfos() {
				return shipinfos;
			}
			public void setShipinfos(List<PurchaseShip> shipinfos) {
				this.shipinfos = shipinfos;
			}
		}
		
		//采购部员工设置物流信息
		@RequestMapping(value = "/purchaseemployee/setshipinfo")
		@ResponseBody
		public Json purchaseSetShipInfo(@RequestBody SetShipInfo wrapper, HttpSession session) {
			Json json = new Json();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username); //获取当前用户
			Purchase purchase = purchaseServiceImpl.find(wrapper.getPurchaseid());
			try {
				if (purchase.getStatus() == Constants.PURCHASE_STATUS_WAIT_FOR_SET_SHIP_INFO) {
					// 根据流程实例Id获取任务
					Task task=taskService.createTaskQuery().processInstanceId(purchase.getProcessInstanceId()).singleResult(); 
					
					for (PurchaseShip ship : wrapper.getShipinfos()) {
						ship.setPurchase(purchase);
						purchaseShipServiceImpl.save(ship);
						
					}
					
					//设置状态为入库中
					purchase.setStatus(Constants.PURCHASE_STATUS_INBOUNDING);
					purchase.setSetShipInfoTime(new Date());
					purchaseServiceImpl.update(purchase);
					taskService.claim(task.getId(), username);
					taskService.complete(task.getId());
					json.setSuccess(true);
					json.setMsg("设置成功");
					json.setObj(null);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单未处于待设置物流信息状态");
					json.setObj(null);
					return json;
				}
				
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("设置失败");
				json.setObj(e);
				e.printStackTrace();
			}
					
			return json;
			
		}
		
//		static class SetInboundRecord {
//			Long purchaseid;
//			Long purchaseitemid;
//			List<PurchaseInbound> inbounds;
//			public Long getPurchaseid() {
//				return purchaseid;
//			}
//			public void setPurchaseid(Long purchaseid) {
//				this.purchaseid = purchaseid;
//			}
//			public List<PurchaseInbound> getInbounds() {
//				return inbounds;
//			}
//			public void setInbounds(List<PurchaseInbound> inbounds) {
//				this.inbounds = inbounds;
//			}
//			public Long getPurchaseitemid() {
//				return purchaseitemid;
//			}
//			public void setPurchaseitemid(Long purchaseitemid) {
//				this.purchaseitemid = purchaseitemid;
//			}
//			
//		}
		
		//库管部查询采购单列表
		@RequestMapping(value="/storekeeper/page/view")
		@ResponseBody
		public Json purchaseStoreKeeperList(String purchaser, String provider, Integer status, Pageable pageable, HttpSession session) {
			//库管部能查看全部采购单,status为0时表示查看入库中的，status为1时表示查看已入库的（包括后面几个状态）
			Json j = new Json();
			
			//设置查询条件
			List<Filter> filters = new ArrayList<Filter>();
			if (provider != null)
		    {
		      List<Filter> fs = new ArrayList();
		      fs.add(Filter.like("providerName", provider));
		      List<Provider> providers = this.providerServiceImpl.findList(null, fs, null);
		      if (providers.size() > 0) {
		          filters.add(Filter.in("provider", providers));
		      } else {
		    	  Page<Purchase> page = new Page<Purchase>(new ArrayList<Purchase>(), 0, pageable);
		    	  j.setSuccess(true);
				  j.setObj(page);
				  j.setMsg("查询成功");
				  return j;
		      }
		    }
		    if (purchaser != null)
		    {
		      List<Filter> fs = new ArrayList();
		      fs.add(Filter.like("name", purchaser));
		      List<HyAdmin> targets = this.hyAdminService.findList(null, fs, null);
		      if (targets.size() > 0) {
		        filters.add(Filter.in("creator", targets));
		      } else {
		    	  Page<Purchase> page = new Page<Purchase>(new ArrayList<Purchase>(), 0, pageable);
		    	  j.setSuccess(true);
				  j.setObj(page);
				  j.setMsg("查询成功");
				  return j;
		      }
		    }
		    if (status != null)
		    {
		      if (status.intValue() == 0)
		      {
		        filters.add(Filter.eq("status", Constants.PURCHASE_STATUS_INBOUNDING));
		      }
		      else
		      {
		        filters.add(Filter.gt("status", Constants.PURCHASE_STATUS_INBOUNDING));
		        filters.add(Filter.le("status", Constants.PURCHASE_STATUS_FINISH_BALANCE));
		      }
		    }
		    else
		    {
		      filters.add(Filter.ge("status", Constants.PURCHASE_STATUS_INBOUNDING));
		      filters.add(Filter.le("status", Constants.PURCHASE_STATUS_FINISH_BALANCE));
		    }
			
			pageable.setFilters(filters);
			
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			
			try {
				Page<Purchase> page = purchaseServiceImpl.findPage(pageable);
				j.setSuccess(true);
				j.setObj(page);
				j.setMsg("查询成功");
			} catch (Exception e) {
				j.setSuccess(false);
				j.setObj(e);
				j.setMsg("查询失败");
				e.printStackTrace();
			}
			
			return j;
		}
		
		//库管部设置入库记录,不结束入库中状态
		@RequestMapping(value = "/storekeeper/setinboundrecord")
		@ResponseBody
		public Json purchaseSetInboundRecord(PurchaseInbound purchaseinbound, Long purchaseid, Long purchaseitemid, HttpSession session) {
			Json json = new Json();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username); //获取当前用户
			Purchase purchase = purchaseServiceImpl.find(purchaseid);
			PurchaseItem purchaseitem = purchaseItemServiceImpl.find(purchaseitemid);
			try {
				//判断是否入库中状态
				if (purchase.getStatus() == Constants.PURCHASE_STATUS_INBOUNDING) {
					List<Filter> filters = new ArrayList<Filter>();
					//同一个采购明细的，同一个生产日期的，同一个保质日期，同一个仓库的合并到库存表
					filters.add(Filter.eq("purchaseItem", purchaseitem));
					filters.add(Filter.eq("productDate", purchaseinbound.getProductDate()));
					filters.add(Filter.eq("durabilityPeriod", purchaseinbound.getDurabilityPeriod()));
					filters.add(Filter.eq("depotCode", purchaseinbound.getDepotCode()));
					List<Inbound> inbounds = inboundServiceImpl.findList(null, filters, null);
					if (inbounds.size() > 0) {//有则合并到已有库存
						Inbound toMergeInbound = inbounds.get(0);
						
						purchaseinbound.setInboundOperator(admin);
						purchaseinbound.setSpecification(purchaseitem.getSpecification());
						purchaseinbound.setPurchase(purchase);
						purchaseinbound.setPurchaseItem(purchaseitem);
						//合并的库存记录
						toMergeInbound.setInboundNumber(toMergeInbound.getInboundNumber()+purchaseinbound.getInboundNumber());
						
						//增加父规格的基本库存
						SpecialtySpecification specification = purchaseitem.getSpecification();
						specification.setBaseInbound(specification.getBaseInbound()+purchaseinbound.getInboundNumber());
						specialtySpecificationSrv.update(specification);
						
						purchaseInboundServiceImpl.save(purchaseinbound);
						toMergeInbound.setUpdateTime(new Date());
						inboundServiceImpl.update(toMergeInbound);
					} else {//新建库存
						Inbound toInsertInbound = new Inbound();
						
						purchaseinbound.setInboundOperator(admin);
						purchaseinbound.setSpecification(purchaseitem.getSpecification());
						purchaseinbound.setPurchase(purchase);
						purchaseinbound.setPurchaseItem(purchaseitem);
						
						toInsertInbound.setPurchaseItem(purchaseitem);
						toInsertInbound.setInboundNumber(purchaseinbound.getInboundNumber());
						toInsertInbound.setProductDate(purchaseinbound.getProductDate());
						toInsertInbound.setDepotCode(purchaseinbound.getDepotCode());
						toInsertInbound.setDurabilityPeriod(purchaseinbound.getDurabilityPeriod());
						toInsertInbound.setSpecification(purchaseitem.getSpecification());
						toInsertInbound.setExpiration(DateUtil.getDateAfterSpecifiedDays(purchaseinbound.getProductDate(), purchaseinbound.getDurabilityPeriod()));
						
						
						//增加父规格的基本库存
						SpecialtySpecification specification = purchaseitem.getSpecification();
						specification.setBaseInbound(specification.getBaseInbound()+purchaseinbound.getInboundNumber());
						specialtySpecificationSrv.update(specification);
						
						
						purchaseInboundServiceImpl.save(purchaseinbound);
						inboundServiceImpl.save(toInsertInbound);
					}
					
					//统计所有采购明细的入库数量和损失数量是否和采购明细的购买数量一致，一致则更新采购明细的入库状态为已入库
					List<Filter> fs = new ArrayList<Filter>();
					fs.add(Filter.eq("purchaseItem", purchaseitem));
					List<PurchaseInbound> list = purchaseInboundServiceImpl.findList(null, fs, null);
					
					
					List<Filter> lostfilters = new ArrayList<Filter>();
					lostfilters.add(Filter.eq("purchaseItem", purchaseitem));
					lostfilters.add(Filter.isNull("inbound"));
					List<SpecialtyLost> losts = this.specialtyLostServiceImpl.findList(null, lostfilters, null);
					Integer hasInboundedNumber = 0;
					Integer lostNumber = 0;
					//不用考虑是否通过审核
					for (SpecialtyLost lost : losts) {
						lostNumber += lost.getLostCount();
//				        if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING) {
//				        	lostNumber += lost.getLostCount();
//				        }
				    }
					if (list.size() > 0) {
						for (PurchaseInbound inbound : list) {
							hasInboundedNumber += inbound.getInboundNumber();
						}
						if ((hasInboundedNumber+lostNumber) == purchaseitem.getQuantity()) {
							purchaseitem.setState(true);
							purchaseItemServiceImpl.update(purchaseitem);
						}
					}
							
					json.setSuccess(true);
					json.setMsg("设置成功");
					json.setObj(null);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单未处于可设置入库信息的状态");
					json.setObj(null);
					return json;
				}
				
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("设置失败");
				json.setObj(e);
				e.printStackTrace();
			}
			
			
			return json;
			
		}
		
		//库管部设置完成入库,结束入库中状态
		@RequestMapping(value = "/storekeeper/finishinbound")
		@ResponseBody
		public Json purchaseSetInboundRecord(Long purchaseid, HttpSession session) {
			Json json = new Json();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username); //获取当前用户
			
			Purchase purchase = purchaseServiceImpl.find(purchaseid);
			try {
				//判断是否入库中状态
				if (purchase.getStatus() == Constants.PURCHASE_STATUS_INBOUNDING) {
					// 根据流程实例Id获取任务
					Task task=taskService.createTaskQuery().processInstanceId(purchase.getProcessInstanceId()).singleResult(); 
					
					for (PurchaseItem item : purchase.getPurchaseItems()) {
						if (!item.getState()) {
							//统计所有采购明细的入库数量和损失数量是否和采购明细的购买数量一致，一致则更新采购明细的入库状态为已入库
							List<Filter> fs = new ArrayList<Filter>();
							fs.add(Filter.eq("purchaseItem", item));
							List<PurchaseInbound> list = purchaseInboundServiceImpl.findList(null, fs, null);
							
							
							List<Filter> lostfilters = new ArrayList<Filter>();
							lostfilters.add(Filter.eq("purchaseItem", item));
							lostfilters.add(Filter.isNull("inbound"));
							List<SpecialtyLost> losts = this.specialtyLostServiceImpl.findList(null, lostfilters, null);
							Integer hasInboundedNumber = 0;
							Integer lostNumber = 0;
							//不用考虑是否通过审核
							for (SpecialtyLost lost : losts) {
								lostNumber += lost.getLostCount();
//						        if (lost.getStatus() == Constants.SPECIALTY_LOST_STATUS_PASS_AUDITING) {
//						        	lostNumber += lost.getLostCount();
//						        }
						    }
							if (list.size() > 0) {
								for (PurchaseInbound inbound : list) {
									hasInboundedNumber += inbound.getInboundNumber();
								}
								if ((hasInboundedNumber+lostNumber) == item.getQuantity()) {
									item.setState(true);
									purchaseItemServiceImpl.update(item);
								} else {
									json.setSuccess(false);
									json.setMsg("未完成所有采购单明细的入库");
									json.setObj(null);
									return json;
								}
							}
						}
					}
					
					//add at 2018/5/31 by zjl
					//如果是退货采购
					if (purchase.getCreator().getUsername().equals(Constants.REFUND_PURCHASE_ACCOUNT)) {
//						for (PurchaseItem purchaseItem : purchase.getPurchaseItems()) {
//							//若未上架，立即上架
//							Specialty s = purchaseItem.getSpecification().getSpecialty();
//							if (s.getSaleState() == 0) {
//								s.setSaleState(1);
//								s.setPutonTime(new Date());
//								specialtyServiceImpl.update(s);
//							}
//						}
						
						taskService.claim(task.getId(), username);
						taskService.complete(task.getId());
						purchase.setStatus(Constants.PURCHASE_STATUS_FINISH_BALANCE);
						purchase.setReceiveTime(new Date());
						purchaseServiceImpl.update(purchase);
						//立刻结束流程
						json.setSuccess(true);
						json.setMsg("设置成功");
						json.setObj(null);
						return json;
					}
					
					
					//更新价格表
					List<Filter> fs = new ArrayList<Filter>();
					for (PurchaseItem purchaseItem : purchase.getPurchaseItems()) {
						SpecialtySpecification specification = purchaseItem.getSpecification();
						fs.clear();
						fs.add(Filter.eq("specification", specification));
						fs.add(Filter.eq("isActive", true));
						List<Order> orders = new ArrayList<Order>();
		     			orders.add(Order.desc("id"));
						List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, fs, orders);
						if (prices.size() > 0) {
							SpecialtyPrice oldprice = prices.get(0);
							 if( oldprice.getMarketPrice().compareTo(purchaseItem.getMarketPrice()) !=0 ||
			     			     oldprice.getCostPrice().compareTo(purchaseItem.getCostPrice()) !=0     ||
			     				 oldprice.getPlatformPrice().compareTo(purchaseItem.getSalePrice()) != 0) {
		     						 SpecialtyPrice newprice = new SpecialtyPrice();
									 newprice.setMarketPrice(purchaseItem.getMarketPrice());
			     					 newprice.setCostPrice(purchaseItem.getCostPrice());
			     					 newprice.setPlatformPrice(purchaseItem.getSalePrice());
			     					 newprice.setDeliverPrice(oldprice.getDeliverPrice());
			     					 newprice.setCreatorName(oldprice.getCreatorName());
			     					 newprice.setSpecification(specification);
			     					 newprice.setSpecialty(specification.getSpecialty());
			     					 newprice.setBusinessPersonDivide(oldprice.getBusinessPersonDivide());
			     					 newprice.setStoreDivide(oldprice.getStoreDivide());
			     					 newprice.setExterStoreDivide(oldprice.getExterStoreDivide());
			     					 newprice.setDeliverPrice(oldprice.getDeliverPrice());
			     					 oldprice.setDeadTime(new Date());
			     					 oldprice.setIsActive(false);
			     					 specialtyPriceSrv.update(oldprice);
			     					 specialtyPriceSrv.save(newprice);	  
			     		     }
							  
						}
						
						//若未上架，立即上架
//						Specialty s = purchaseItem.getSpecification().getSpecialty();
//						if (s.getSaleState() == 0) {
//							s.setSaleState(1);
//							s.setPutonTime(new Date());
//							specialtyServiceImpl.update(s);
//						}
					}
					
					//全款付款
					if (purchase.getPurchaseType() == Constants.PURCHASE_TYPE_FULL_PAYMENT) {
						purchase.setStatus(Constants.PURCHASE_STATUS_FINISH_BALANCE);
						purchase.setReceiveTime(new Date());
						purchaseServiceImpl.update(purchase);	
					} else { //部分付款或者货到付款
						purchase.setStatus(Constants.PURCHASE_STATUS_FINISH_INBOUND);
						purchase.setReceiveTime(new Date());
						purchaseServiceImpl.update(purchase);
					}
					
					taskService.claim(task.getId(), username);
					taskService.complete(task.getId());
					json.setSuccess(true);
					json.setMsg("设置成功");
					json.setObj(null);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单未处于可结束入库的状态");
					json.setObj(null);
					return json;
				}
				
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("设置失败");
				json.setObj(e);
				e.printStackTrace();
			}
			
			return json;
		}
		
		//采购部员工提请采购结算
		@RequestMapping(value = "/purchaseemployee/applybalance")
		@ResponseBody
		public Json purchaseApplyBalance(Long purchaseid, HttpSession session) {
			Json json = new Json();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username); //获取当前用户
			Purchase purchase = purchaseServiceImpl.find(purchaseid);
			try {
				if (purchase.getStatus() == Constants.PURCHASE_STATUS_FINISH_INBOUND) {
					// 根据流程实例Id获取任务
					Task task=taskService.createTaskQuery().processInstanceId(purchase.getProcessInstanceId()).singleResult();
					
					//统计损失产品价值
					BigDecimal lostMoney = new BigDecimal(0);
					for (PurchaseItem item : purchase.getPurchaseItems()) {
						List<Filter> lostfilters = new ArrayList<Filter>();
						//查找入库时报道的损失，不考虑审核状态
						lostfilters.add(Filter.eq("purchaseItem", item));
						lostfilters.add(Filter.isNull("inbound"));
						List<SpecialtyLost> losts = this.specialtyLostServiceImpl.findList(null, lostfilters, null);
						Integer counts = 0;
						for (SpecialtyLost lost : losts) {
							if (lost.getStatus().equals(Constants.SPECIALTY_LOST_STATUS_WAIT_FOR_AUDITED) ||  lost.getStatus().equals(Constants.SPECIALTY_LOST_STATUS_FAIL_AUDITING)) {
								json.setSuccess(false);
								json.setMsg("采购单存在未审核或审核失败的计损单！");
								json.setObj(null);
								return json;
							}
							counts += lost.getLostCount();
						}
						lostMoney = lostMoney.add(item.getCostPrice().multiply(new BigDecimal(counts)));
					}
					//生成待付款单
					if (purchase.getPurchaseType() == Constants.PURCHASE_TYPE_PARTIAL_PAYMENT) {
						PurchasePay pay = new PurchasePay();
						pay.setAdvanceAmount(purchase.getTotalMoney().subtract(purchase.getAdvanceAmount()).subtract(lostMoney));
						pay.setPurchase(purchase);
						pay.setPayeeName(purchase.getProvider().getAccountName());
						pay.setPayeeBank(purchase.getProvider().getBankName());
						pay.setPayeeAccount(purchase.getProvider().getBankAccount());
						purchasePayServiceImpl.save(pay);
					} else if (purchase.getPurchaseType() == Constants.PURCHASE_TYPE_PAY_ON_DELIVERY) {
						PurchasePay pay = new PurchasePay();
						pay.setAdvanceAmount(purchase.getTotalMoney().subtract(lostMoney));
						pay.setPurchase(purchase);
						pay.setPayeeName(purchase.getProvider().getAccountName());
						pay.setPayeeBank(purchase.getProvider().getBankName());
						pay.setPayeeAccount(purchase.getProvider().getBankAccount());
						purchasePayServiceImpl.save(pay);
					}
					
					//设置状态为待结算
					purchase.setStatus(Constants.PURCHASE_STATUS_WAIT_FOR_BALANCE);
					purchase.setBalancePaySubmitTime(new Date());
					purchaseServiceImpl.update(purchase);
					taskService.claim(task.getId(), username);
					taskService.complete(task.getId());
					json.setSuccess(true);
					json.setMsg("设置成功");
					json.setObj(null);
					return json;
				} else {
					json.setSuccess(false);
					json.setMsg("采购单未处于可设置提交结算的状态");
					json.setObj(null);
					return json;
				}
				
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("设置失败");
				json.setObj(e);
				e.printStackTrace();
			}
					
			return json;
			
		}
		
		/**
		 * 
		 * 财务付款
		 *
		 */
		@RequestMapping("/financer/pay")
		@ResponseBody
		public Json puchaseFinancerPay(PurchasePay pay, HttpSession session) {
			Json json = new Json();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username); //获取当前用户
			
			PurchasePay old = purchasePayServiceImpl.find(pay.getId());
			Purchase purchase = old.getPurchase();
			old.setPayerAccount(pay.getPayerAccount());
			old.setPayerBank(pay.getPayerBank());
			old.setPayerName(pay.getPayerName());
			old.setIsPaid(true);
			old.setOperator(admin);
			old.setPayTime(pay.getPayTime());
			
			try {
				if (purchase.getStatus() == Constants.PURCHASE_STATUS_WAIT_FOR_BALANCE) {
					Task task=taskService.createTaskQuery().processInstanceId(
							purchase.getProcessInstanceId()).singleResult();
					
					//完成结算
					purchasePayServiceImpl.update(old);
					purchase.setStatus(Constants.PURCHASE_STATUS_FINISH_BALANCE);
					purchase.setBalancePayTime(new Date());
					purchaseServiceImpl.update(purchase);
					
					taskService.claim(task.getId(), username);
					taskService.complete(task.getId());
					
//					List<Filter> fs = new ArrayList<Filter>();
					//更新价格表
//					for (PurchaseItem purchaseItem : purchase.getPurchaseItems()) {
//						SpecialtySpecification specification = purchaseItem.getSpecification();
//						fs.clear();
//						fs.add(Filter.eq("specification", specification));
//						List<SpecialtyPrice> prices = specialtyPriceSrv.findList(null, fs, null);
//						if (prices.size() > 0) {
//							 SpecialtyPrice oldprice = prices.get(0);
//							 SpecialtyPrice newprice = new SpecialtyPrice();
//							 newprice.setMarketPrice(purchaseItem.getMarketPrice());
//	     					 newprice.setCostPrice(purchaseItem.getCostPrice());
//	     					 newprice.setPlatformPrice(purchaseItem.getSalePrice());
//	     					 newprice.setCreatorName(purchaseItem.getSetProportionOperator().getName());
//	     					 newprice.setSpecification(specification);
//	     					 newprice.setSpecialty(specification.getSpecialty());
//	     					 newprice.setBusinessPersonDivide(oldprice.getBusinessPersonDivide());
//	     					 newprice.setStoreDivide(oldprice.getStoreDivide());
//	     					 newprice.setExterStoreDivide(oldprice.getExterStoreDivide());
//	     					 newprice.setDeliverPrice(oldprice.getDeliverPrice());
//	     					 oldprice.setDeadTime(new Date());
//	     					 oldprice.setIsActive(false);
//	     					 specialtyPriceSrv.update(oldprice);
//	     					 specialtyPriceSrv.save(newprice);
//						}
//					}
					json.setSuccess(true);
					json.setMsg("提交成功");
					json.setObj(null);
				} else if (purchase.getStatus() == Constants.PURCHASE_STATUS_WAIT_FOR_PAID) {
					Task task=taskService.createTaskQuery().processInstanceId(
							purchase.getProcessInstanceId()).singleResult();
					//完成结算
					purchasePayServiceImpl.update(old);
					purchase.setStatus(Constants.PURCHASE_STATUS_WAIT_FOR_SET_SHIP_INFO);
					purchase.setAdvanceTime(new Date());
					purchaseServiceImpl.update(purchase);
					
					taskService.claim(task.getId(), username);
					taskService.complete(task.getId());
					json.setSuccess(true);
					json.setMsg("提交成功");
					json.setObj(null);
				}
				else {
					json.setSuccess(false);
					json.setMsg("采购单未处于可付款状态");
					json.setObj(null);
				}		
				
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("提交失败");
				json.setObj(e);
				e.printStackTrace();
			}
			
			return json;
		}
	
	//采购单查询供应商
	@RequestMapping(value = "/purchaseemployee/providerlist/view", method = RequestMethod.GET)
	@ResponseBody
	public Json providerList() {
		Json json = new Json();
		
		try {
			List<Provider> list = providerServiceImpl.findAll();
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(list);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
		}
		
		return json;
	}
	
	//查询供应商采购单
	@RequestMapping(value = "/purchaseemployee/providerorderpage/view")
	@ResponseBody
	public Json providerOrder(@DateTimeFormat(iso=ISO.DATE) Date startdate, @DateTimeFormat(iso=ISO.DATE) Date enddate, String providername, String specialtyname, 
			String receivername, String receiverphone, Pageable pageable) {
		Json json = new Json();
		
		try {
			List<Filter> filters = new ArrayList<Filter>();
			List<Filter> orderFilters = new ArrayList<Filter>();
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				Date yesterDayStart = DateUtil.getStartOfDay(yesterDay);
				Date yesterDayEnd= DateUtil.getEndOfDay(yesterDay);
				filters.add(Filter.ge("createTime", yesterDayStart));
				filters.add(Filter.le("createTime", yesterDayEnd));
				orderFilters.add(Filter.ge("orderTime", yesterDayStart));
				orderFilters.add(Filter.le("orderTime", yesterDayEnd));
				
			} 
			if (startdate != null) {
				Date start = DateUtil.getStartOfDay(startdate);
				filters.add(Filter.ge("createTime", start));
				orderFilters.add(Filter.ge("orderTime", start));
			} 
			if (enddate != null) {
				Date end = DateUtil.getEndOfDay(enddate);
				filters.add(Filter.le("createTime", end));
				orderFilters.add(Filter.le("orderTime", end));
			}
			
			//考虑供货商姓名
			if (StringUtils.isNotEmpty(providername)) {
				filters.add(Filter.like("deliverName", providername));
			}
			//商品
			filters.add(Filter.eq("type", 0));
			
			//订单处于待发货状态
			orderFilters.add(Filter.eq("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY));
			//供货商发货
			orderFilters.add(Filter.eq("isDivided", true));
			orderFilters.add(Filter.eq("isShow", false));
			if (StringUtils.isNotEmpty(receivername)) {
				orderFilters.add(Filter.like("receiverName", receivername));
			}
			if (StringUtils.isNotEmpty(receiverphone)) {
				orderFilters.add(Filter.like("receiverPhone", receiverphone));
			}
			List<BusinessOrder> orders = businessOrderServiceImpl.findList(null, orderFilters, null);
			if (orders.isEmpty()) {
				Page<Map<String, Object>> page = new Page<>(new ArrayList<Map<String, Object>>(), 0, pageable);
				json.setObj(page);
				json.setMsg("查询成功");
				json.setSuccess(true);
				return json;
			} else {
				filters.add(Filter.in("businessOrder", orders));
			}
			pageable.setFilters(filters);
			pageable.setOrderProperty("id");
			pageable.setOrderDirection(Direction.desc);
			Page<BusinessOrderItem> page = businessOrderItemService.findPage(pageable);
			List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
			for (BusinessOrderItem item :page.getRows()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", item.getId());
				map.put("providerName", item.getDeliverName());
				Specialty specialty = specialtyServiceImpl.find(item.getSpecialty());
				if(StringUtils.isNotEmpty(specialtyname) && !StringUtils.contains(specialty.getName(), specialtyname)) {
					continue;
				}
				map.put("specialtyName", specialty.getName());
				SpecialtySpecification specification = specialtySpecificationSrv.find(item.getSpecialtySpecification());
				map.put("specification", specification.getSpecification());
				map.put("quantity", item.getQuantity());
				map.put("orderCode", item.getBusinessOrder().getOrderCode());
				map.put("receiverName", item.getBusinessOrder().getReceiverName());
				map.put("receiverPhone", item.getBusinessOrder().getReceiverPhone());
				map.put("receiverAddress", item.getBusinessOrder().getReceiverAddress());
				map.put("costPrice", businessOrderItemService.getCostPriceOfOrderitem(item));
				
				/** wayne 180813*/
				map.put("salePrice", item.getSalePrice());	//单价
				
				BusinessOrder bOrder = item.getBusinessOrder();
				BigDecimal payMoney =  item.getSalePrice().multiply(
						bOrder.getPayMoney().divide(bOrder.getShouldPayMoney(),3,BigDecimal.ROUND_HALF_DOWN));
				map.put("payMoney",payMoney);	//现金支付
				BigDecimal balanceMoney = item.getSalePrice().multiply(
						bOrder.getBalanceMoney().divide(bOrder.getShouldPayMoney(),3,BigDecimal.ROUND_HALF_DOWN));
				map.put("balanceMoney", balanceMoney);	//余额支付
				map.put("couponMoney", item.getSalePrice().subtract(payMoney).subtract(balanceMoney));	//一次电子券
				map.put("payTime", bOrder.getPayTime());	//支付时间	
				map.put("weBusinessName", bOrder.getWeBusiness().getName());	//微商姓名
				map.put("storeName", bOrder.getWeBusiness().getNameOfStore());	//所属门店

				map.put("remark", item.getBusinessOrder().getReceiverRemark());
				maps.add(map);
			}
			Page<Map<String, Object>> mapPage = new Page<Map<String, Object>>(maps, page.getTotal(), pageable);
						
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(mapPage);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
			e.printStackTrace();
		}
		
		return json;
	}
	
	//生成供应商采购单excel表格
	@RequestMapping(value = "/purchaseemployee/providerorderexcel/view")
	public String providerOrderExcel(@DateTimeFormat(iso=ISO.DATE)Date startdate, @DateTimeFormat(iso=ISO.DATE)Date enddate, 
			String providername, String specialtyname, String receivername, String receiverphone, 
			HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		
		try {
			List<Filter> filters = new ArrayList<Filter>();
			//考虑日期范围
			if (startdate == null && enddate == null) {
				Date current = new Date();
				Date yesterDay = DateUtil.getPreDay(current);
				Date yesterDayStart = DateUtil.getStartOfDay(yesterDay);
				Date yesterDayEnd= DateUtil.getEndOfDay(yesterDay);
				startdate = yesterDayStart;
				enddate = yesterDayEnd;
				filters.add(Filter.ge("createTime", yesterDayStart));
				filters.add(Filter.le("createTime", yesterDayEnd));
				
			} 
			if (startdate != null) {
				Date start = DateUtil.getStartOfDay(startdate);
				filters.add(Filter.ge("createTime", start));
			} 
			if (enddate != null) {
				Date end = DateUtil.getEndOfDay(enddate);
				filters.add(Filter.le("createTime", end));
			}
			
			//考虑供货商姓名
			if (StringUtils.isNotEmpty(providername)) {
				filters.add(Filter.like("deliverName", providername));
			}
			//供货商发货
			filters.add(Filter.le("deliverType", 1));
			//商品
			filters.add(Filter.eq("type", 0));
			List<Filter> orderFilters = new ArrayList<Filter>();
			//订单处于待发货状态
			orderFilters.add(Filter.eq("orderState", Constants.BUSINESS_ORDER_STATUS_WAIT_FOR_DELIVERY));
			orderFilters.add(Filter.eq("isDivided", true));
			orderFilters.add(Filter.eq("isShow", false));
			if (StringUtils.isNotEmpty(receivername)) {
				orderFilters.add(Filter.like("receiverName", receivername));
			}
			if (StringUtils.isNotEmpty(receiverphone)) {
				orderFilters.add(Filter.like("receiverPhone", receiverphone));
			}
			
			
			List<BusinessOrder> orders = businessOrderServiceImpl.findList(null, orderFilters, null);
			if (orders.isEmpty()) {
				List<ProviderOrder> results = new ArrayList<ProviderOrder>();
				// 生成Excel表标题
				StringBuffer sb2 = new StringBuffer();
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				sb2.append("供货商销售量统计_"+format.format(startdate)+"-"+format.format(enddate));
				String fileName = "供货商销售量报表_"+format.format(startdate)+"-"+format.format(enddate)+".xls";  // Excel文件名
				String tableTitle = sb2.toString();   // Excel表标题
				String configFile = "providerOrder.xml"; // 配置文件
				com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
				excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
				return null;
			} else {
				filters.add(Filter.in("businessOrder", orders));
			}
			
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(Order.desc("id"));
			List<BusinessOrderItem> list = businessOrderItemService.findList(null, filters, orderList);
			List<ProviderOrder> results = new ArrayList<ProviderOrder>();
			for (BusinessOrderItem item : list) {
				ProviderOrder map = new ProviderOrder();
				map.setProviderName(item.getDeliverName());
				Specialty specialty = specialtyServiceImpl.find(item.getSpecialty());
				map.setSpecialtyName(specialty.getName());
				SpecialtySpecification specification = specialtySpecificationSrv.find(item.getSpecialtySpecification());
				map.setSpecification(specification.getSpecification());
				map.setQuantity(item.getQuantity());
				map.setOrderCode(item.getBusinessOrder().getOrderCode());
				map.setReceiverName(item.getBusinessOrder().getReceiverName());
				map.setReceiverPhone(item.getBusinessOrder().getReceiverPhone());
				map.setReceiverAddress(item.getBusinessOrder().getReceiverAddress());
				map.setCostPrice(businessOrderItemService.getCostPriceOfOrderitem(item));
				map.setRemark(item.getBusinessOrder().getReceiverRemark());
				results.add(map);
			}
			// 生成Excel表标题
			StringBuffer sb2 = new StringBuffer();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			sb2.append("供货商销售量统计_"+format.format(startdate)+"-"+format.format(enddate));
			String fileName = "供货商销售量报表_"+format.format(startdate)+"-"+format.format(enddate)+".xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "providerOrder.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			return null;
		}
		
		return null;
	}
	
	
	public static class ProviderOrder {
		private String providerName;
		private String specialtyName;
		private String specification;
		private Integer quantity;
		private String orderCode;
		private String receiverName;
		private String receiverPhone;
		private String receiverAddress;
		private BigDecimal costPrice;
		private String remark;
		public String getProviderName() {
			return providerName;
		}
		public void setProviderName(String providerName) {
			this.providerName = providerName;
		}
		public String getSpecialtyName() {
			return specialtyName;
		}
		public void setSpecialtyName(String specialtyName) {
			this.specialtyName = specialtyName;
		}
		public String getSpecification() {
			return specification;
		}
		public void setSpecification(String specification) {
			this.specification = specification;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public String getOrderCode() {
			return orderCode;
		}
		public void setOrderCode(String orderCode) {
			this.orderCode = orderCode;
		}
		public String getReceiverName() {
			return receiverName;
		}
		public void setReceiverName(String receiverName) {
			this.receiverName = receiverName;
		}
		public String getReceiverPhone() {
			return receiverPhone;
		}
		public void setReceiverPhone(String receiverPhone) {
			this.receiverPhone = receiverPhone;
		}
		public String getReceiverAddress() {
			return receiverAddress;
		}
		public void setReceiverAddress(String receiverAddress) {
			this.receiverAddress = receiverAddress;
		}
		public BigDecimal getCostPrice() {
			return costPrice;
		}
		public void setCostPrice(BigDecimal costPrice) {
			this.costPrice = costPrice;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		
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
	 
	  //采购单查询特产分区
	  @RequestMapping({"/purchaseemployee/categorytreelist/view"})
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
	  
	  	//查询指定供应商和指定分区的产品
	    @RequestMapping("/purchaseemployee/specialtylist/view")
	    @ResponseBody
	    public Json specialtyPutDown(Long providerid, Long categoryid)
	    {	
	  	  Json json = new Json();
	  	  
	  	  
	  	  try {
	  		  Provider provider = providerServiceImpl.find(providerid);
		  	  SpecialtyCategory category = specialtyCategoryServiceImpl.find(categoryid);
		  	  List<Filter> filters = new ArrayList<Filter>();
		  	  filters.add(Filter.eq("provider", provider));
		  	  filters.add(Filter.eq("category", category));
		  	  List<Specialty> specialties = specialtyServiceImpl.findList(null, filters, null);
		  	  json.setSuccess(true);
	  		  json.setMsg("查询成功");
	  		  json.setObj(specialties);
		  } catch (Exception e) {
			  json.setSuccess(false);
	  		  json.setMsg("查询失败");
	  		  json.setObj(null);
		  }

	      return json;
	    }
	    
	    //查询指定特产的所有规格
	  	@RequestMapping("/purchaseemployee/specificationlist/view")
		@ResponseBody
		public Json specialtySpecificationInSelectedCategory(Long specialtyid) {
			Json json = new Json();
			if (specialtyid == null) {
				json.setSuccess(false);
				json.setMsg("没有指定特产");
				json.setObj(null);
				return json;
			}
			
			try {
				Specialty specialty = specialtyServiceImpl.find(specialtyid);
				if (specialty != null) {
					List<Map> result = new ArrayList<Map>();
					for (SpecialtySpecification specification : specialty.getSpecifications()) {
						if (specification.getParent() == 0) {
							List<Filter> filterlist = new ArrayList<Filter>();
			     			filterlist.add(Filter.eq("specification", specification));
			     			filterlist.add(Filter.eq("isActive", true));
//			     			List<Order> orders = new ArrayList<Order>();
//			     			orders.add(Order.desc("id"));
			     			List<SpecialtyPrice> list = specialtyPriceSrv.findList(null, filterlist, null);
			     			if (list.size()>0) {
			     				Map<String, Object> map = new HashMap<String, Object>();
								map.put("value", specification.getId());
								map.put("label", specification.getSpecification());
								result.add(map);
			     			}						
						}
					}
					
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(result);
				} else {
					json.setSuccess(false);
					json.setMsg("找不到指定特产");
					json.setObj(null);
				}
				
				return json;
			} catch (Exception e) {
				json.setSuccess(false);
				json.setMsg("查询失败");
				json.setObj(null);
			}
			
			return json;
		}
	  	
	  	@RequestMapping({"/purchasequdao/proportion/view"})
	    @ResponseBody
	    public Json getDivideProportion(Integer purchasetype)
	    {
	      Json j = new Json();
	      try
	      {
	        List<Filter> filters = new ArrayList();
	        filters.add(Filter.eq("proportionType", purchasetype));
	        List<WeDivideProportion> list = this.proportionSrv.findList(null, filters, null);
	        j.setMsg("查询成功");
	        j.setSuccess(true);
	        j.setObj(list);
	      }
	      catch (Exception e)
	      {
	        j.setMsg("查询失败");
	        j.setSuccess(false);
	        j.setObj(e);
	        e.printStackTrace();
	      }
	      return j;
	    }
	  	
	  	@RequestMapping({"/storekeeper/specialtylost/add"})
	    @ResponseBody
	    public Json specialtyLostAdd(SpecialtyLost lost, Long purchaseitemid, HttpSession session)
	    {
	      Json j = new Json();
	      try
	      {
	        String username = (String)session.getAttribute("principal");
	        HyAdmin admin = (HyAdmin)this.hyAdminService.find(username);
	        
	        PurchaseItem purchaseitem = (PurchaseItem)this.purchaseItemServiceImpl.find(purchaseitemid);
	        SpecialtySpecification specification = purchaseitem.getSpecification();
	        Specialty specialty = specification.getSpecialty();
	        lost.setPurchaseItem(purchaseitem);
	        lost.setSpecialtySpecification(specification);
	        lost.setSpecialty(specialty);
	        lost.setPurchaseCode(purchaseitem.getPurchase().getPurchaseCode());
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
	  	
		@Resource(name="hyDepotServiceImpl")
		HyDepotService hyDepotService;
		
		
		@Resource(name="hyDepotAdminServiceImpl")
		private HyDepotAdminService hyDepotAdminService;
	  	
	  	//获取仓库列表
		@RequestMapping("/storekeeper/depot/list/view")
		@ResponseBody
		public Json depotList(HyDepot depot,HttpSession session,HttpServletRequest request){
			Json json = new Json();
			
			List<Map<String, Object>> lhm = new ArrayList<>();
			
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
			
			List<Filter> filters = new ArrayList<Filter>();
			//filters.add(Filter.in("creator", hyAdmins));
			filters.add(Filter.eq("isValid", depot.getIsValid()));
			
			try{
				List<HyDepot> list = hyDepotService.findList(null,filters,null);
				if(list.size()>0){
					for(HyDepot hyDepot:list){
						Map<String, Object> hMap = new HashMap<>();
						hMap.put("id", hyDepot.getId());
						hMap.put("code", hyDepot.getCode());
						hMap.put("name",hyDepot.getName());
						hMap.put("address", hyDepot.getAddress());
						hMap.put("creator", hyDepot.getCreator()==null?null:hyDepot.getCreator().getName());
						hMap.put("creatorTime", hyDepot.getCreateTime());
						hMap.put("isValid", hyDepot.getIsValid());
						
						List<Filter> adminFilters = new ArrayList<>();
						adminFilters.add(Filter.eq("depotId", hyDepot.getId()));
						adminFilters.add(Filter.eq("isValid", true));
						List<HyDepotAdmin> depotAdmins = hyDepotAdminService.findList(null,adminFilters,null);
						List<LabelValue> labelValues = new ArrayList<>();
						for(HyDepotAdmin depotAdmin:depotAdmins){
							LabelValue labelValue = new LabelValue();
							labelValue.label = labelValue.value = depotAdmin.getAdminName();
							labelValues.add(labelValue);
						}
						hMap.put("manage", labelValues);
						
						
						/** 当前用户对本条数据的操作权限 */
						if(hyDepot.getCreator().equals(admin)){
							if(co == CheckedOperation.view) {
								hMap.put("privilege", "view");
							} else {
								hMap.put("privilege", "edit");
							}
						} else{
							if(co == CheckedOperation.edit) {
								hMap.put("privilege", "edit");
							} else {
								hMap.put("privilege", "view");
							}
						}
						
						lhm.add(hMap);
					}
				}
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(lhm);
		    }catch (Exception e) {
					
				// TODO: handle exception
				json.setSuccess(false);
				json.setMsg(e.getMessage());
			}
		    return json;
		}
		
		
		//获取价格信息
		@RequestMapping("/purchaseemployee/specificatiprice/view")
		@ResponseBody
		public Json getSpecificationPrice(Long specificationid,HttpSession session,HttpServletRequest request){
			Json json = new Json();
			
			
			
			try {
				SpecialtySpecification specification = specialtySpecificationSrv.find(specificationid);
				if (specification == null) {
					json.setObj(null);
					json.setSuccess(false);
					json.setMsg("不存在指定的特产规格");
					return json;
				}
				List<Filter> filterlist = new ArrayList<Filter>();
				filterlist.add(Filter.eq("specification", specification));
				filterlist.add(Filter.eq("isActive", true));
				List<SpecialtyPrice> list = specialtyPriceSrv.findList(null, filterlist, null);
				if (list.size() > 0) {
					SpecialtyPrice price = list.get(0);
					Map<String, Object> map = new HashMap<String, Object>();
					
					map.put("marketPrice", price.getMarketPrice());
					map.put("platformPrice", price.getPlatformPrice());
					map.put("costPrice", price.getCostPrice());
					map.put("deliverPrice", price.getDeliverPrice());
					json.setObj(map);
					json.setSuccess(true);
					json.setMsg("查询成功");
				} else {
					json.setObj(null);
					json.setSuccess(false);
					json.setMsg("当前特产规格未设置价格");
				}
			} catch (Exception e) {
				e.printStackTrace();
				json.setObj(e);
				json.setSuccess(false);
				json.setMsg("查询失败");
			}
			return json;
		}
}

