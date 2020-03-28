package com.hongyu.controller.gxz04;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.CouponMoney;
import com.hongyu.entity.Department;
import com.hongyu.entity.GroupDivide;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroup.GroupStateEnum;
import com.hongyu.entity.HyGroupOtherprice;
import com.hongyu.entity.HyGroupPrice;
import com.hongyu.entity.HyGroupSpecialprice;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyLine.IsSaleEnum;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.entity.HyLineRefund;
import com.hongyu.entity.HyLineTravels;
import com.hongyu.entity.HyProviderRebate;
import com.hongyu.entity.HyRegulate;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.LineCatagoryEntity;
import com.hongyu.entity.Store;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.CouponMoneyService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.GroupBiankoudianService;
import com.hongyu.service.GroupDivideService;
import com.hongyu.service.GroupMemberService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyGroupOtherpriceService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineRefundService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyLineTravelsService;
import com.hongyu.service.HyProviderRebateService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.LineCatagoryService;
import com.hongyu.service.StoreService;
import com.hongyu.service.TransportService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants.AuditStatus;
/**
 * 线路产品Controller
 * @author guoxinze
 *
 */
@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/product/line/")
public class LineController {
	
	public static class Wrap {
		HyGroup hyGroup;
		List<Date> startDays = new ArrayList<>();
		public HyGroup getHyGroup() {
			return hyGroup;
		}
		public void setHyGroup(HyGroup hyGroup) {
			this.hyGroup = hyGroup;
		}
		public List<Date> getStartDays() {
			return startDays;
		}
		public void setStartDays(List<Date> startDays) {
			this.startDays = startDays;
		}	
	}
	
	@Resource(name = "couponMoneyServiceImpl")
	CouponMoneyService couponMoneyService;
	
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource
	private HistoryService historyService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "hyLineTravelsServiceImpl")
	HyLineTravelsService hyLineTravelsService;
	
	@Resource(name = "hyLineRefundServiceImpl")
	HyLineRefundService hyLineRefundService;
	
	@Resource(name = "transportServiceImpl")
	TransportService transportService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "departmentServiceImpl")
	private DepartmentService departmentService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyAreaServiceImpl")
	HyAreaService  hyAreaService;
	
	@Resource(name = "insuranceServiceImpl")
	InsuranceService insuranceService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name = "lineCatagoryServiceImpl")
	LineCatagoryService lineCatagoryService;
	
	@Resource(name="commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;
	
	@Resource(name = "hyGroupOtherpriceServiceImpl")
	HyGroupOtherpriceService hyGroupOtherpriceService;
	
	@Resource(name = "groupBiankoudianServiceImpl")
	GroupBiankoudianService groupBiankoudianService;
	
	@Resource(name = "groupDivideServiceImpl")
	GroupDivideService groupDivideService;
	
	@Resource(name = "groupMemberServiceImpl")
	GroupMemberService groupMemberService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@Resource(name = "hyProviderRebateServiceImpl")
	HyProviderRebateService hyProviderRebateService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	/**
	 * 线路的列表页
	 * @return
	 */
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable, HyLine hyLine, Long lineCategoryId,String supplierName,String operatorName,
					 HttpSession session, HttpServletRequest request) {
		Json j = new Json();
		try {
			Map<String, Object> obj = new HashMap<String, Object>();
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
			filters.add(Filter.in("operator", hyAdmins));
			if(lineCategoryId != null) {
				filters.add(Filter.eq("lineCategory", lineCatagoryService.find(lineCategoryId)));
			}
			
			//新增合同负责人可以看到子账号产品的逻辑
			
			Set<HySupplierContract> cs =admin.getLiableContracts();
			if(!cs.isEmpty()) {
				filters.add(Filter.in("contract", cs));
			}
			//按计调筛选,added by GSbing,20190804
			if(operatorName!=null) {
				List<Filter> adminFilter=new ArrayList<>();
				adminFilter.add(Filter.like("name",operatorName));
				List<HyAdmin> adminList=hyAdminService.findList(null,adminFilter,null);
				if(adminList.isEmpty()) {
					j.setSuccess(true);
					j.setMsg("获取列表成功");
					j.setObj(new HyLine());
					return j;
				}
				else {
					filters.add(Filter.in("operator", adminList));
				}
			}
			//按供应商名字筛选
			if(supplierName!=null) {
				List<Filter> supplierFilter=new ArrayList<>();
				supplierFilter.add(Filter.like("supplierName", supplierName));
				List<HySupplier> supplierList=hySupplierService.findList(null,supplierFilter,null);
				if(supplierList.isEmpty()) {
					j.setSuccess(true);
					j.setMsg("获取列表成功");
					j.setObj(new HyLine());
					return j;
				}
				else {
					filters.add(Filter.in("hySupplier", supplierList));
				}
 			}
				
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createDate"));
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<HyLine> lines = hyLineService.findPage(pageable, hyLine);
			
			if(lines.getRows().size() > 0) {
				for(HyLine line : lines.getRows()) {
					HashMap<String, Object> hm = new HashMap<String, Object>();
			        HyAdmin creater = line.getOperator();
			        hm.put("id", line.getId());
			        hm.put("pn", line.getPn());
			        hm.put("supplierName", line.getContract().getHySupplier().getSupplierName());
			        hm.put("lineName", line.getName());
			        String province = hyAreaService.find(line.getArea().getTreePaths().get(0)).getName(); //得到省份信息
			        hm.put("area", province);
			        hm.put("lineType", line.getLineType());
			        hm.put("lineCategory", line.getLineCategory());
			        hm.put("days", line.getDays());
			        
			        hm.put("latestGroup", line.getLatestGroup());
			        hm.put("lineAuditStatus", line.getLineAuditStatus());
			        hm.put("groupAuditStatus", line.getGroupAuditStatus());
			        hm.put("isSale", line.getIsSale());
			        hm.put("isInner", line.getIsInner());
			        hm.put("isCancel", line.getIsCancel());
			        hm.put("isEdit", line.getIsEdit());
			        hm.put("isTop", line.getIsTop());
			        
			        if (creater != null) {
			        	hm.put("operator", creater.getName());
			        }
			        
			      	/** 当前用户对本条数据的操作权限 */
					if(creater.equals(admin)){
						if(co == CheckedOperation.view) {
								hm.put("privilege", "view");
						} else {
								hm.put("privilege", "edit");
						}
					} else{
						if(co == CheckedOperation.edit) {
							hm.put("privilege", "edit");
						} else {
							hm.put("privilege", "view");
						}
					}
			        lhm.add(hm);
				}
			}
			
			obj.put("pageSize", Integer.valueOf(lines.getPageSize()));
			obj.put("pageNumber", Integer.valueOf(lines.getPageNumber()));
			obj.put("total", Long.valueOf(lines.getTotal()));
     		obj.put("rows", lhm);
			
			j.setSuccess(true);
			j.setMsg("获取列表成功");
			j.setObj(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 详情页 -包括新建页面和编辑页面
	 */
	@RequestMapping(value = "detail/view")
	public Json detail(Long id, HttpSession session) {
		Json j = new Json();
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			//********************** 页面公共的部分 **********************
			HashMap<String, Object> obj = new HashMap<>();
			//合同下拉列表
			List<Map<String, Object>> hetongs = new ArrayList<>();			
			//保险方案下拉列表
			List<Map<String, Object>> baoxians = new ArrayList<>();
			
			HySupplierContract contract = null;
			
			HyAdmin liable = admin;
			
			if(admin.getHyAdmin() != null) {
				liable = admin.getHyAdmin();
			}
	
			Set<HySupplierContract> cs = liable.getLiableContracts();
			for(HySupplierContract c : cs) {
				HashMap<String, Object> hm = new HashMap<>();
				if(c.getContractStatus() == ContractStatus.zhengchang) {
					hm.put("id", c.getId());
					hm.put("contractCode", c.getContractCode());
					contract = c;
					hetongs.add(hm);
					break;
				}
			}	
			
			
			obj.put("contracts", hetongs);
			if(contract != null) {
				if(admin.getHyAdmin() != null) {
					obj.put("chujingAreas", admin.getAreaChujing());
					obj.put("guoneiAreas", admin.getAreaGuonei());
					obj.put("qicheAreas", admin.getAreaQiche());
				} else {
					obj.put("chujingAreas", contract.getChujingAreas());
					obj.put("guoneiAreas", contract.getGuoneiAreas());
					obj.put("qicheAreas", contract.getQicheAreas());
				}
				
			} else if(id != null) {
				HyLine line = hyLineService.find(id);
				if(line.getArea() != null && line.getContract() != null) {
					obj.put("areaName", line.getArea().getName());					
					obj.put("contractCode", line.getContract().getContractCode());
				}
				
			} else { //如果过期不让新建线路
				throw new RuntimeException("合同过期，无法新建线路!");
			}
			
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.in("status", true));
			obj.put("transports", transportService.findList(null, filters, null));
			
			List<Insurance> ins = insuranceService.findAll();
			for(Insurance in : ins) {
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("id", in.getId());
				hm.put("insuranceCode", in.getInsuranceCode());
				hm.put("remark", in.getRemark());
				baoxians.add(hm);
			}
			obj.put("baoxian", baoxians);
			
			if(contract != null && contract.getHySupplier() != null) {
				obj.put("isInner", contract.getHySupplier().getIsInner());
			}
			
			
			//************* 判断是不是编辑的页面 *************
			if(id != null) {
				HyLine line = hyLineService.find(id);
				obj.put("line", line);
				obj.put("memoDetail", hyLineService.getMemoDetail(line));
			}
			j.setSuccess(true);
			j.setMsg("查看详情成功");
			j.setObj(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value = "add")
	public Json add(@RequestBody HyLine hyLine, HttpSession session) {
		Json j = new Json(); 
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin operator = hyAdminService.find(username);
			Boolean flag = false;
			//新增只有供应商合同为正常才可以新建线路
			if(operator.getHyAdmin() != null) {
				HyAdmin parent = operator.getHyAdmin();
				Set<HySupplierContract> supplierContracts = parent.getLiableContracts();
				for(HySupplierContract c : supplierContracts) {
					if(c.getContractStatus() == ContractStatus.zhengchang) {
						flag = true;
						break;
					}
				}
			} else {
				Set<HySupplierContract> supplierContracts1 = operator.getLiableContracts();
				for(HySupplierContract c : supplierContracts1) {
					if(c.getContractStatus() == ContractStatus.zhengchang) {
						flag = true;
						break;
					}
				}
			}
			if(flag == false) {
				j.setMsg("供应商合同状态错误");
				j.setSuccess(false);
				return j;
			}
			
			hyLine.setOperator(operator);
			
			//设置线路行程 
			if(!hyLine.getLineTravels().isEmpty()) {
				for(HyLineTravels t : hyLine.getLineTravels()) {
					t.setLine(hyLine);
					t.setTransport(transportService.find(t.getTransport().getId()));
				}
			}
			//设置线路退款
			if(!hyLine.getLineRefunds().isEmpty()) {
				for(HyLineRefund r : hyLine.getLineRefunds()) {
					r.setLine(hyLine);
				}
			}
			
			hyLine.setLowestPrice(BigDecimal.ZERO);
			hyLine.setIsSale(IsSaleEnum.weishang);
			hyLine.setLineAuditStatus(AuditStatus.unsubmitted);
			hyLine.setGroupAuditStatus(AuditStatus.unsubmitted);
			hyLine.setIsEdit(true);
			hyLine.setHits(0L);
			hyLine.setSaleCount(0);
			hyLine.setIsCancel(false);
			hyLine.setIsGuanwang(false);
			hyLine.setIsTop(false);//初始不置顶
			hyLine.setHySupplier(hySupplierContractService.find(hyLine.getContract().getId()).getHySupplier());
			/** 设置线路产品ID */
			String re = "";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd"); 
			String dateStr = format.format(cur);
			List<Filter> filters = new ArrayList<Filter>();
			if(hyLine.getLineType() == LineType.chujing) {			
				filters.add(Filter.in("type", SequenceTypeEnum.xianlucj));
				synchronized(CommonSequence.class) {
				    List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				    CommonSequence c = ss.get(0);
					Long value = c.getValue();
					c.setValue(++value);
					commonSequenceService.update(c);
					re = "CJ-" + dateStr + "-" + String.format("%04d", value);
				}			
				
			} else if (hyLine.getLineType() == LineType.guonei) {
				filters.add(Filter.in("type", SequenceTypeEnum.xianlugn));
				synchronized(CommonSequence.class) {
				    List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				    CommonSequence c = ss.get(0);
					Long value = c.getValue();
					c.setValue(++value);
					commonSequenceService.update(c);
					re = "GN-" + dateStr + "-" + String.format("%04d", value);
				}
				
			} else if(hyLine.getLineType() == LineType.qiche) {
				filters.add(Filter.in("type", SequenceTypeEnum.xianluqc));
				synchronized(CommonSequence.class) {
				    List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				    CommonSequence c = ss.get(0);
					Long value = c.getValue();
					c.setValue(++value);
					commonSequenceService.update(c);
					re = "QC-" + dateStr + "-" + String.format("%04d", value);
				}			
			}
			hyLine.setPn(re);	
			hyLine.setIsInner(hyLine.getHySupplier().getIsInner());
			
			//新增判断线路是否仅分公司可见的代码
			Department depart = hyLine.getOperator().getDepartment();
			List<Long> treePaths = depart.getTreePaths();
			if(treePaths.get(1) != null && departmentService.find(treePaths.get(1)).getIsCompany() == true) {
				hyLine.setCompany(departmentService.find(treePaths.get(1)));
			} else {
				hyLine.setCompany(departmentService.find(Long.valueOf(1)));
			}
			
		
			hyLineService.save(hyLine);
			j.setSuccess(true);
			j.setMsg("保存成功");		
			j.setObj(hyLine.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	/**
	 * 编辑线路产品
	 * @param hyLine
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "update")
	public Json update(@RequestBody HyLine hyLine, HttpSession session) {
		Json j = new Json(); 
		try {
			HyLine line = hyLineService.find(hyLine.getId());
			
			if(!line.getIsEdit()) {
				throw new RuntimeException("产品不可以编辑");
			}
			
			//更新线路行程 
			List<HyLineTravels> travels = line.getLineTravels();
			if(!hyLine.getLineTravels().isEmpty()) {
				for(HyLineTravels t : hyLine.getLineTravels()) {
					t.setLine(line);
				}
			}
			travels.clear();
			travels.addAll(hyLine.getLineTravels());
		
			//更新线路退款
			List<HyLineRefund> refunds = line.getLineRefunds();
			if(!hyLine.getLineRefunds().isEmpty()) {
				for(HyLineRefund r : hyLine.getLineRefunds()) {
					r.setLine(line);
				}
			}
			refunds.clear();
			refunds.addAll(hyLine.getLineRefunds());
			
			hyLineService.update(hyLine, "pn", "contract", "operator", "company", "lineType",
					"lowestPrice", "isSale", "lineAuditStatus", "groupAuditStatus", "latestGroup",
					"hits", "saleCount", "isEdit", "isCancel", "hySupplier", "isGuanwang","isInner",
					"isPromotion","isTop"
					);
			j.setSuccess(true);
			j.setMsg("更新成功");		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
		
	/**
	 * 获取线路二级分类
	 * @return
	 */
	@RequestMapping(value="catagory/view")
	public Json catagory(LineType yijifenlei) {
		Json j = new Json();
		try{
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("yijifenlei", yijifenlei));
			filters.add(Filter.eq("status", true));
			List<LineCatagoryEntity> lineCatagoryEntities = lineCatagoryService.findList(null, filters, null);
			j.setSuccess(true);
			j.setMsg("查看成功！");
			j.setObj(lineCatagoryEntities);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	

	/**
	 * 新增团-批量添加的
	 * @param wrap
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "addGroup")
	public Json addGroup(@RequestBody HyGroup group, HttpSession session) {
		Json j = new Json(); 
		try {
			
			//检测发团日期是否重复
			HyLine line = hyLineService.find(group.getLine().getId());
			List<Date> startDays = group.getStartDays();
			Boolean teamType=group.getTeamType();
			for(int i = 0; i < startDays.size(); i++) {
				if(hyGroupService.groupDayExist(startDays.get(i), line, null,teamType)) { //发生重复					
					throw new RuntimeException("发团日期重复");
				}
			}
			
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin operator = hyAdminService.find(username);
			
			
			for(int i = 0; i < startDays.size(); i++) {
				
				HyGroup hyGroup = (HyGroup) BeanUtils.cloneBean(group);
				
				if(!group.getHyGroupPrices().isEmpty()) { //拷贝price
					List<HyGroupPrice> groupPrices = new ArrayList<>();
					for(HyGroupPrice t : group.getHyGroupPrices()) {
						HyGroupPrice temp = new HyGroupPrice();
						temp = (HyGroupPrice) BeanUtils.cloneBean(t);
						groupPrices.add(temp);
					}
					hyGroup.setHyGroupPrices(groupPrices);
				}
				
				if(!group.getHyGroupSpecialprices().isEmpty()) { //拷贝特殊价格
					List<HyGroupSpecialprice> hyGroupSpecialprices = new ArrayList<>();
					for(HyGroupSpecialprice t : group.getHyGroupSpecialprices()) {
						HyGroupSpecialprice temp = new HyGroupSpecialprice();
						temp = (HyGroupSpecialprice) BeanUtils.cloneBean(t);
						hyGroupSpecialprices.add(temp);
					}
					hyGroup.setHyGroupSpecialprices(hyGroupSpecialprices);
				}
				
				if(!group.getHyGroupOtherprices().isEmpty()) { //拷贝其它价格
					List<HyGroupOtherprice> hyGroupOtherprices = new ArrayList<>();
					for(HyGroupOtherprice t : group.getHyGroupOtherprices()) {
						HyGroupOtherprice temp = new HyGroupOtherprice();
						temp = (HyGroupOtherprice) BeanUtils.cloneBean(t);
						hyGroupOtherprices.add(temp);
					}
					hyGroup.setHyGroupOtherprices(hyGroupOtherprices);
				}
				
				if(hyGroup.getLine() != null) {
					hyGroup.setLine(hyLineService.find(hyGroup.getLine().getId()));
				}
				
				if(hyGroup.getPublishStore() != null) {
					hyGroup.setPublishStore(storeService.find(hyGroup.getPublishStore().getId()));
				}

				//设置普通价格和最低价格
				if(hyGroup.getHyGroupPrices().size() > 0) {
					for(HyGroupPrice t : hyGroup.getHyGroupPrices()) {
						t.setAdultPrice6(t.getAdultPrice1());
						t.setOldPrice6(t.getOldPrice1());
						t.setStudentPrice6(t.getStudentPrice1());
						t.setChildrenPrice6(t.getChildrenPrice1());
						t.setAdultPrice4(t.getAdultPrice());
						t.setOldPrice4(t.getOldPrice());
						t.setStudentPrice4(t.getStudentPrice());
						t.setChildrenPrice4(t.getChildrenPrice());
						t.setHyGroup(hyGroup);
						//设置团的最低价格
						if(hyGroup.getLowestPrice() == null) {
							hyGroup.setLowestPrice(t.getAdultPrice1());
						} else {
							BigDecimal b = hyGroup.getLowestPrice().compareTo(t.getAdultPrice1()) > 0 ? t.getAdultPrice1() : hyGroup.getLowestPrice();
							hyGroup.setLowestPrice(b);
						}
						//设置线路的最低价格
						if(null != line) {
							if(line.getLowestPrice().compareTo(BigDecimal.ZERO) == 0) {
								line.setLowestPrice(t.getAdultPrice1());
							} else {
								BigDecimal b = line.getLowestPrice().compareTo(t.getAdultPrice1()) > 0 ? t.getAdultPrice1() : line.getLowestPrice();
								line.setLowestPrice(b);
							}
						}
						
					}
					hyLineService.update(line);
				}
				
				//设置特殊价格
				if(hyGroup.getHyGroupSpecialprices().size() > 0) {
					for(HyGroupSpecialprice s : hyGroup.getHyGroupSpecialprices()) {
						s.setSpecialPrice4(s.getSpecialPrice());
						s.setHyGroup(hyGroup);
					}
				}
				
				//设置其他价格
				if(hyGroup.getHyGroupOtherprices().size() > 0) {
					for(HyGroupOtherprice o : hyGroup.getHyGroupOtherprices()) {					
						
						o.setBuchuangweiPrice6(o.getBuchuangweiPrice1());
						o.setBumenpiaoPrice6(o.getBumenpiaoPrice1());
						o.setBuwopuPrice6(o.getBuwopuPrice1());
						o.setDanfangchaPrice6(o.getDanfangchaPrice1());
						o.setErtongzhanchaungPrice6(o.getErtongzhanchaungPrice1());
						
						o.setBuchuangweiPrice4(o.getBuchuangweiPrice());
						o.setBumenpiaoPrice4(o.getBumenpiaoPrice());
						o.setBuwopuPrice4(o.getBuwopuPrice());
						o.setDanfangchaPrice4(o.getDanfangchaPrice());
						o.setErtongzhanchaungPrice4(o.getErtongzhanchaungPrice());
						o.setHyGroup(hyGroup);
					}
				}
				
				//设置发布范围
				if(hyGroup.getTeamType() == true) {
					hyGroup.setPublishRange(storeService.find(hyGroup.getPublishStore().getId()).getStoreName());
					hyGroup.setPublishStore(storeService.find(hyGroup.getPublishStore().getId())); //设置发布门店
				} else {					
					hyGroup.setPublishRange(line.getCompany().getName());
				}
				
				hyGroup.setGroupCompany(line.getCompany());
				hyGroup.setStartDay(startDays.get(i));
				hyGroup.setEndDay(new Date(startDays.get(i).getTime() + (long) (line.getDays() - 1) * 24 * 60 * 60 * 1000));
				hyGroup.setSignupNumber(0);
				hyGroup.setOccupyNumber(0);
//				hyGroup.setStockRemain(hyGroup.getStock());
//				hyGroup.setRemainNumber(hyGroup.getStock());
				hyGroup.setAuditStatus(AuditStatus.unsubmitted);
				hyGroup.setIsDisplay(true);
				hyGroup.setGroupState(GroupStateEnum.daichutuan);
				hyGroup.setSaleTimes(0);
				hyGroup.setIsCancel(false);
				hyGroup.setCreator(operator);
				hyGroup.setAuditStatus(AuditStatus.unsubmitted);
				hyGroup.setGroupLineType(line.getLineType());
				hyGroup.setGroupLineName(line.getName());
				hyGroup.setGroupLinePn(line.getPn());
				hyGroup.setIsInner(line.getIsInner());		
				hyGroup.setIsPromotion(false);
				hyGroup.setOperatorName(line.getOperator().getName());
				
				//修改电子券的逻辑
				if(hyGroup.getIsCoupon()) { //如果有电子券
					CouponMoney c = couponMoneyService.find(hyGroup.getCouponId());
					hyGroup.setCouponMoney(BigDecimal.valueOf(c.getMoney())); //设置电子券金额
					hyGroup.setDiscount(BigDecimal.valueOf(c.getRebateRatio())); //设置电子券折扣
				}
				
				//新增团返利类型和金额 默认返利类型为无 返利金额为0
				if(null == hyGroup.getFanliType() && null == hyGroup.getFanliMoney()) {
					hyGroup.setFanliType(0);
					hyGroup.setFanliMoney(BigDecimal.ZERO);
				}
								
				hyGroupService.save(hyGroup);
						
			}
			
			j.setSuccess(true);
			j.setMsg("保存成功");		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 编辑团
	 * @param group
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "updateGroup")
	public Json updateGroup(@RequestBody HyGroup hyGroup, HttpSession session) {
		Json j = new Json(); 
		try {	
			
			//检测发团日期是否重复
//			HyLine line = hyLineService.find(hyGroup.getLine().getId());
//	        Boolean teamType=hyGroup.getTeamType();
//			if(hyGroupService.groupDayExist(hyGroup.getStartDay(), line, hyGroup.getId(),teamType)) { //发生重复	
//				throw new RuntimeException("发团日期重复");
//			}
			//判断发团日期是否重复，去掉自身编辑前的发团日期
			HyGroup priGroup=hyGroupService.find(hyGroup.getId());
			HyLine line = hyLineService.find(hyGroup.getLine().getId());
			List<Filter> groupFilters=new ArrayList<Filter>();
			groupFilters.add(Filter.eq("teamType", hyGroup.getTeamType()));
			groupFilters.add(Filter.eq("line", line));
			List<HyGroup> groups=hyGroupService.findList(null,groupFilters,null);
			for(HyGroup g:groups) {
				if((DateUtils.isSameDay(g.getStartDay(),hyGroup.getStartDay())&&(hyGroup.getTeamType().equals(priGroup.getTeamType()))&&(g.getId()!=priGroup.getId()))
						||(DateUtils.isSameDay(g.getStartDay(),hyGroup.getStartDay())&&(!hyGroup.getTeamType().equals(priGroup.getTeamType())))) {
					throw new RuntimeException("发团日期重复");
				}
			}

			
			HyGroup group = hyGroupService.find(hyGroup.getId());
				//编辑普通价格和最低价格
				List<HyGroupPrice> hgps = group.getHyGroupPrices();
				if(!hyGroup.getHyGroupPrices().isEmpty()) {
					for(HyGroupPrice t : hyGroup.getHyGroupPrices()) {
						t.setAdultPrice6(t.getAdultPrice1());
						t.setOldPrice6(t.getOldPrice1());
						t.setStudentPrice6(t.getStudentPrice1());
						t.setChildrenPrice6(t.getChildrenPrice1());
						t.setAdultPrice4(t.getAdultPrice());
						t.setOldPrice4(t.getOldPrice());
						t.setStudentPrice4(t.getStudentPrice());
						t.setChildrenPrice4(t.getChildrenPrice());
						t.setHyGroup(group);
						if(hyGroup.getLowestPrice() == null) {
							hyGroup.setLowestPrice(t.getAdultPrice1());
						} else {
							BigDecimal b = hyGroup.getLowestPrice().compareTo(t.getAdultPrice1()) > 0 ? t.getAdultPrice1() : hyGroup.getLowestPrice();
							hyGroup.setLowestPrice(b);
						}
						//设置线路的最低价格
						if(null != line) {
							if(line.getLowestPrice().compareTo(BigDecimal.ZERO) == 0) {
								line.setLowestPrice(t.getAdultPrice1());
							} else {
								BigDecimal b = line.getLowestPrice().compareTo(t.getAdultPrice1()) > 0 ? t.getAdultPrice1() : line.getLowestPrice();
								line.setLowestPrice(b);
							}
						}
						
					}

					hyLineService.update(line);
				}
				hgps.clear();
				hgps.addAll(hyGroup.getHyGroupPrices());
				
				//编辑特殊价格
				List<HyGroupSpecialprice> hgsps = group.getHyGroupSpecialprices();
				if(!hyGroup.getHyGroupSpecialprices().isEmpty()) {
					for(HyGroupSpecialprice s : hyGroup.getHyGroupSpecialprices()) {
						s.setSpecialPrice4(s.getSpecialPrice());
						s.setHyGroup(group);
					}
				}
				hgsps.clear();
				hgsps.addAll(hyGroup.getHyGroupSpecialprices());
				
				//编辑其他价格
				List<HyGroupOtherprice> hgops = group.getHyGroupOtherprices();
				if(!hyGroup.getHyGroupOtherprices().isEmpty()) {
					for(HyGroupOtherprice o : hyGroup.getHyGroupOtherprices()) {
						
						o.setBuchuangweiPrice6(o.getBuchuangweiPrice1());
						o.setBumenpiaoPrice6(o.getBuchuangweiPrice1());
						o.setBuwopuPrice6(o.getBuwopuPrice1());
						o.setDanfangchaPrice6(o.getDanfangchaPrice1());
						o.setErtongzhanchaungPrice6(o.getErtongzhanchaungPrice1());
						
						o.setBuchuangweiPrice4(o.getBuchuangweiPrice());
						o.setBumenpiaoPrice4(o.getBuchuangweiPrice());
						o.setBuwopuPrice4(o.getBuwopuPrice());
						o.setDanfangchaPrice4(o.getDanfangchaPrice());
						o.setErtongzhanchaungPrice4(o.getErtongzhanchaungPrice());
						
						o.setHyGroup(group);
					}
				}
				hgops.clear();
				hgops.addAll(hyGroup.getHyGroupOtherprices());
				
				
				//编辑发布范围
				if(hyGroup.getTeamType() == true) {
					hyGroup.setPublishRange(storeService.find(hyGroup.getPublishStore().getId()).getStoreName());
					hyGroup.setPublishStore(storeService.find(hyGroup.getPublishStore().getId())); //设置发布门店
				} else {
					Department depart = line.getOperator().getDepartment();
					List<Long> treePaths = depart.getTreePaths();
					if(treePaths.get(1) != null && departmentService.find(treePaths.get(1)).getIsCompany() == true) {
						hyGroup.setPublishRange(departmentService.find(treePaths.get(1)).getName());
					} else {
						hyGroup.setPublishRange("虹宇廊坊总公司");
					}
				}
				hyGroup.setEndDay(new Date(hyGroup.getStartDay().getTime() + (long) (line.getDays() - 1) * 24 * 60 * 60 * 1000));
				
				//编辑电子券 --- 因为没有在update的时候ignore掉，所以如果没有会更新掉
				if(hyGroup.getIsCoupon()) { //如果有电子券
					CouponMoney c = couponMoneyService.find(hyGroup.getCouponId());
					hyGroup.setCouponMoney(BigDecimal.valueOf(c.getMoney())); //设置电子券金额
					hyGroup.setDiscount(BigDecimal.valueOf(c.getRebateRatio())); //设置电子券折扣
				}
				//团返利类型和返利金额更新部分
				if(null == hyGroup.getFanliType() && null == hyGroup.getFanliMoney()) {
					hyGroup.setFanliType(0);
					hyGroup.setFanliMoney(BigDecimal.ZERO);
				}
				/**如果供应商编辑了这个团，则官网需要再次完善*/
				hyGroup.setMhState(0);
				hyGroupService.update(hyGroup, "line", "isInner", "stockRemain", "signupNumber", "occupyNumber", "groupCompany", "operatorName",
									  "remainNumber", "auditStatus", "isDisplay", "groupState", "saleTimes", "isCancel", "groupSendGuides",
									  "creator", "regulateId", "applyName", "processInstanceId", "groupLineType", "groupLineName", "groupLinePn",
									  "isPromotion"
									  );
			
			j.setSuccess(true);
			j.setMsg("更新成功");		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 门店搜索接口
	 * @param storeName 门店名称
	 * @return
	 */
	@RequestMapping(value="mendian/view")
	public Json mendian(String storeName) {
		Json j = new Json();
		try{
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.like("storeName", storeName));
			List<Store> stores = storeService.findList(null, filters, null);
			j.setSuccess(true);
			j.setMsg("查看成功！");
			j.setObj(stores);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 团详情列表
	 * @param id 团id
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "detailGroup/view")
	public Json detailGroup(Long id, HttpSession session) {
		Json j = new Json();
		try {
			
			HyGroup group = hyGroupService.find(id);
			
			j.setSuccess(true);
			j.setMsg("查看详情成功");
			j.setObj(group);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value = "listGroup/view")
	public Json listGroup(Pageable pageable, Long lineId, HyGroup hyGroup) {
		Json j = new Json();
		try {
			HashMap<String, Object> jiagebili = new HashMap<>();
			Map<String, Object> obj = new HashMap<String, Object>();
			List<Map<String, Object>> lhm = new ArrayList<>();
			
			HyLine line = hyLineService.find(lineId);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("line", line));
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createDate"));
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			
			Page<HyGroup> groups = hyGroupService.findPage(pageable, hyGroup);
			
			if(groups.getRows().size() > 0) {
				for(HyGroup group : groups.getRows()) {
					HashMap<String, Object> hm = new HashMap<String, Object>();
			        hm.put("id", group.getId());
			        hm.put("startDay", group.getStartDay());
			        hm.put("endDay", group.getEndDay());
			        hm.put("lowestPrice", group.getLowestPrice());
			        hm.put("teamType", group.getTeamType());
			        hm.put("publishRange", group.getPublishRange());
			        hm.put("stock", group.getStock());
			        hm.put("auditStatus", group.getAuditStatus());
			        hm.put("groupState", group.getGroupState());
			        hm.put("signupNumber", group.getSignupNumber());
			        hm.put("occupyNumber", group.getOccupyNumber());
			        List<HyGroupPrice> gps = new ArrayList<>(group.getHyGroupPrices());
			        if(!gps.isEmpty()) {
			        	hm.put("adultPrice", gps.get(0).getAdultPrice());
				        hm.put("adultPrice1", gps.get(0).getAdultPrice1());
			        }
			        
			        lhm.add(hm);
				}
			}
			
			obj.put("pageSize", Integer.valueOf(groups.getPageSize()));
			obj.put("pageNumber", Integer.valueOf(groups.getPageNumber()));
			obj.put("total", Long.valueOf(groups.getTotal()));
			if(line.getLineType() == LineType.guonei) {
				obj.put("koudianXianlu", line.getContract().getHySupplierDeductGuonei());
			} else if (line.getLineType() == LineType.chujing) {
				obj.put("koudianXianlu", line.getContract().getHySupplierDeductChujing());
			} else if (line.getLineType() == LineType.qiche) {
				obj.put("koudianXianlu", line.getContract().getHySupplierDeductQiche());
			}
			obj.put("endDate", line.getContract().getDeadDate().getTime() - (long)(line.getDays()-1)*24*60*60*1000);
			
			//加入价格比例
			filters.clear();
			filters.add(Filter.eq("eduleixing", Eduleixing.guoneijiagebili));
			List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
			BigDecimal money = edu.get(0).getMoney();
			jiagebili.put("guonei", money);
		
			filters.clear();
			filters.add(Filter.eq("eduleixing", Eduleixing.chujingjiagebili));
			List<CommonShenheedu> edu1 = commonEdushenheService.findList(null, filters, null);
			BigDecimal money1 = edu1.get(0).getMoney();
			jiagebili.put("chujing", money1);
		
			filters.clear();
			filters.add(Filter.eq("eduleixing", Eduleixing.qichejiagebili));
			List<CommonShenheedu> edu2 = commonEdushenheService.findList(null, filters, null);
			BigDecimal money2 = edu2.get(0).getMoney();
			jiagebili.put("qiche", money2);
			
			filters.clear();
			filters.add(Filter.eq("eduleixing", Eduleixing.piaowujiagebili));
			List<CommonShenheedu> edu3 = commonEdushenheService.findList(null, filters, null);
			BigDecimal money3 = edu3.get(0).getMoney();
			jiagebili.put("piaowu", money3);
			
     		obj.put("rows", lhm);
			obj.put("jiagebili", jiagebili);
			j.setSuccess(true);
			j.setMsg("获取列表成功");
			j.setObj(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 线路下线
	 * @param lineId 线路ID
	 * @return
	 */
	@RequestMapping(value="offline")
	public Json xiaxian(Long id) {
		Json j = new Json();
		try {
			HyLine line = hyLineService.find(id);
			
			line.setIsSale(IsSaleEnum.yixia);
			
			if (line.getIsEdit()) { //如果产品可编辑，需要设置团状态

				line.setLineAuditStatus(AuditStatus.unsubmitted); //未提交
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("groupState", GroupStateEnum.daichutuan));
				filters.add(Filter.eq("line", line));
				List<HyGroup> groups = hyGroupService.findList(null, filters, null); //待出团的团
				
				for(HyGroup group : groups) {
					group.setAuditStatus(AuditStatus.unsubmitted);
					hyGroupService.update(group); //将未出团团状态设置为未提交
				}
			}
			
			hyLineService.update(line);
			j.setMsg("线路下线成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 线路上线
	 * @param lineId
	 * @return
	 */
	@RequestMapping(value="online")
	public Json shangxian(Long id) {
		Json j = new Json();
		try {
			
			HyLine line = hyLineService.find(id);
			
			if(line == null || line.getIsEdit() && line.getLatestGroup() == null) { //产品可编辑不可以直接上线，需要通过提交审核上线
				throw new RuntimeException("线路不可以直接上线");
			}
	
			if(line.getLatestGroup().after(new Date())) { //如果最新团期还有效，直接上线
				line.setIsSale(IsSaleEnum.yishang);
				hyLineService.update(line);
			} else if (line.getLatestGroup().before(new Date())){
				throw new RuntimeException("上线失败，最新团期无效");
			}
			
			j.setSuccess(true);
			j.setMsg("上线成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 取消线路
	 * @param lineId
	 * @return
	 */
	@RequestMapping(value="cancel")
	public Json cancel(Long id) {
		Json j = new Json();
		try {
			HyLine line = hyLineService.find(id);
			line.setIsCancel(true);
			hyLineService.update(line);
			j.setMsg("取消成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	

	/**
	 * 恢复线路
	 * @param lineId
	 * @return
	 */
	@RequestMapping(value="restore") 
	public Json restore(Long id) {
		Json j = new Json();
		try {
			HyLine line = hyLineService.find(id);
			line.setIsCancel(false);
			hyLineService.update(line);
			j.setMsg("恢复成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 供应商提交团审核
	 * @param groupIds
	 * @param session
	 * @return
	 */
	@RequestMapping(value="submitAudit")
	public Json submitAudit(Long[] groupIds, HttpSession session) {
		Json j = new Json();
		try {
			HashMap<String, Object> map = new HashMap<>();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			
			if(groupIds == null || groupIds.length == 0) {
				throw new RuntimeException("参数错误");
			}
			
			for(Long id : groupIds) {
				HyGroup hyGroup = hyGroupService.find(id);
				HyLine line = hyGroup.getLine();
				LineType lineType = line.getLineType();
				String admin = line.getHySupplier().getOperator().getUsername(); //得到线路的采购部创建人
				map.put("admin", admin); //审核的时候指定审核人使用
				
				//如果符合价格规律就不用审核
				List<Filter> filters = new ArrayList<>();
				if(lineType == LineType.guonei) {
					filters.add(Filter.eq("eduleixing", Eduleixing.guoneijiagebili));
				} else if (lineType == LineType.chujing) {
					filters.add(Filter.eq("eduleixing", Eduleixing.chujingjiagebili));
				} else if (lineType == LineType.qiche) {
					filters.add(Filter.eq("eduleixing", Eduleixing.qichejiagebili));
				} //找到三种线路类型的价格比例
							
				List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
				BigDecimal money = edu.get(0).getMoney();
				Boolean flag = checkPrice(hyGroup, money); //计算是否符合价格规律
				
				//非首次提交且满足价格规律且没有申请特殊扣点 不需要审核
				if(line.getLineAuditStatus() == AuditStatus.pass && flag && !hyGroup.getIsSpecialKoudian()) {
					hyGroup.setAuditStatus(AuditStatus.pass);
					//新增不需要审核更新线路的最新团期
					if(line.getLatestGroup() == null) {
						line.setLatestGroup(hyGroup.getStartDay());
					} else {
						if(hyGroup.getStartDay().after(line.getLatestGroup())) {
							line.setLatestGroup(hyGroup.getStartDay());
							hyLineService.update(line);
						}
					}
					hyGroupService.update(hyGroup);
					
					if(line != null && line.getLineAuditStatus() != AuditStatus.pass) {
						line.setLineAuditStatus(AuditStatus.pass);
						hyLineService.update(line);
					}
					//新增不需要审核的表插入报账表一条数据
					if(line.getIsInner()) {
						HyRegulate regulate = new HyRegulate();
						regulate.setHyGroup(hyGroup.getId());
						regulate.setLineSn(line.getPn());
						regulate.setLineName(line.getName());
						regulate.setStartDate(hyGroup.getStartDay());
						regulate.setEndDate(hyGroup.getEndDay());
						regulate.setDays(line.getDays());
						regulate.setVisitorNum(0);
						regulate.setOperator(line.getOperator());
						regulate.setCreateTime(new Date());
						regulate.setStatus(0);
						regulate.setOperatorName(line.getOperator().getName());
						hyRegulateService.save(regulate);
						hyGroup.setRegulateId(regulate.getId());
					}
					
					
					//向groupdivide表中加入分团数据
					
					/*
					 * write by lbc
					 */
					
					if(hyGroup.getIsInner()) {
						//如果是内部的团
						GroupDivide groupDivide = new GroupDivide();
						groupDivide.setGroup(hyGroup);
						groupDivide.setGuide(null);
						
						//分团号初始化为A，团内人员总数初始化为0
						groupDivide.setSubGroupsn("A");
						groupDivide.setSubGroupNo(0);
						
						groupDivideService.save(groupDivide);	
						
						
						
					}
					
					
					
					
				} else { //非首次提交不满足价格规律 和首次提交需要审核
					/********************************驳回以后在原来基础上提交******************************/
					//新增线路审核代码
									
					if(hyGroup.getIsSpecialKoudian()) { //如果申请特殊扣点，需要经过采购审核
						map.put("result", "teshu");
					} else {
						map.put("result", "zhengchang");
					}
					Task task = null;
					if(hyGroup.getAuditStatus() == AuditStatus.notpass) { //如果驳回在原来基础上修改

						task = taskService.createTaskQuery().processInstanceId(hyGroup.getProcessInstanceId()).singleResult();
					
						hyGroup.setAuditStatus(AuditStatus.auditing);					
						hyGroup.setApplyTime(new Date());
						hyGroup.setApplyName(username);				
						
					

					} else { //第一次新建提交
						ProcessInstance pi = runtimeService.startProcessInstanceByKey("xianlushenheprocess");
						// 根据流程实例Id查询任务
						task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
						// 完成 

						hyGroup.setAuditStatus(AuditStatus.auditing);
						hyGroup.setProcessInstanceId(pi.getProcessInstanceId());
						hyGroup.setApplyTime(new Date());	
						hyGroup.setApplyName(username);
					}
					Authentication.setAuthenticatedUserId(username);
					taskService.addComment(task.getId(), hyGroup.getProcessInstanceId(), " :1");
					taskService.complete(task.getId(), map);	
					
					if(line != null && line.getLineAuditStatus() != AuditStatus.pass) {
						line.setLineAuditStatus(AuditStatus.auditing);
						hyLineService.update(line);
					}
					hyGroupService.update(hyGroup);
					
					/********************************       结束                    ******************************/		
				}
			}
			j.setMsg("提交审核成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 检查团是否符合价格规律
	 * @param hyGroup
	 * @return
	 */
	private boolean checkPrice(HyGroup hyGroup, BigDecimal percent) {
	
			List<HyGroupPrice> prices = hyGroup.getHyGroupPrices();
			List<HyGroupSpecialprice> specialPrices = hyGroup.getHyGroupSpecialprices();
			List<HyGroupOtherprice> otherprices = hyGroup.getHyGroupOtherprices();
			
			for(HyGroupPrice price : prices) {
				if(price.getAdultPrice1() != null && price.getAdultPrice() != null && price.getAdultPrice1().compareTo(price.getAdultPrice().multiply(percent)) > 0) {
					return false;
				}
				
				if(price.getChildrenPrice1() != null && price.getChildrenPrice() != null && price.getChildrenPrice1().compareTo(price.getChildrenPrice().multiply(percent)) > 0) {
					return false;
				}
				if(price.getOldPrice1() != null && price.getOldPrice() != null && price.getOldPrice1().compareTo(price.getOldPrice().multiply(percent)) > 0) {
					return false;
				}
				if(price.getStudentPrice1() != null && price.getStudentPrice() != null && price.getStudentPrice1().compareTo(price.getStudentPrice().multiply(percent)) > 0) {
					return false;
				}
			}
			
			for(HyGroupSpecialprice specialprice : specialPrices) {
				if(specialprice.getSpecialPrice1() != null && specialprice.getSpecialPrice() != null && specialprice.getSpecialPrice1().compareTo(specialprice.getSpecialPrice().multiply(percent)) > 0) {
					return false;
				}
			}
			
			
			for(HyGroupOtherprice otherprice : otherprices) {
				if(otherprice.getBuchuangweiPrice1() != null && otherprice.getBuchuangweiPrice() != null && otherprice.getBuchuangweiPrice1().compareTo(otherprice.getBuchuangweiPrice().multiply(percent)) > 0) {
					return false;
				}
				if(otherprice.getBuwopuPrice1() != null && otherprice.getBuwopuPrice() != null && otherprice.getBuwopuPrice1().compareTo(otherprice.getBuwopuPrice().multiply(percent)) > 0) {
					return false;
				}
				if(otherprice.getDanfangchaPrice1() != null && otherprice.getDanfangchaPrice() != null && otherprice.getDanfangchaPrice1().compareTo(otherprice.getDanfangchaPrice().multiply(percent)) > 0) {
					return false;
				}
				if(otherprice.getBumenpiaoPrice1() != null && otherprice.getBumenpiaoPrice() != null && otherprice.getBumenpiaoPrice1().compareTo(otherprice.getBumenpiaoPrice().multiply(percent)) > 0) {
					return false;
				}
				if(otherprice.getErtongzhanchaungPrice1() != null && otherprice.getErtongzhanchaungPrice() != null && otherprice.getErtongzhanchaungPrice1().compareTo(otherprice.getErtongzhanchaungPrice().multiply(percent)) > 0) {
					return false;
				}
			}
			return true;
		
	}
	/**
	 * 变更库存
	 * @author LBC
	 * @param groupId
	 * @param stock
	 * @return
	 */
	@RequestMapping(value="/changeStock")
	@ResponseBody
	public Json restore(Long groupId,Integer stock) {
		Json j = new Json();
		try{
			HyGroup hyGroup = hyGroupService.find(groupId);
			hyGroup.setStock(stock);;
			hyGroupService.update(hyGroup);
			j.setSuccess(true);
			j.setMsg("变更库存成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/** 获取所有可用的线路电子券*/
	@RequestMapping("/couponLine/view")
	@ResponseBody
	public Json getCouponLine(){
		Json json = new Json();
		
		try {
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.like("issueType","线路赠送"));
			filters.add(Filter.eq("isActive", true)); //找出状态为正常的线路电子券
			List<CouponMoney> list = couponMoneyService.findList(null, filters, null);
			
			json.setObj(list);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取线路赠送电子券失败");
			e.printStackTrace();
		}
		
		return json;
	}
	
	/** 产品中心无报名消团*/
	@RequestMapping("cancelGroup")
	@ResponseBody
	public Json cancelGroup(Long groupId)
	{
		Json json=new Json();
		try{
			HyGroup hyGroup=hyGroupService.find(groupId);
			hyGroup.setGroupState(GroupStateEnum.yiquxiao);
			hyGroup.setIsCancel(true);
			hyGroupService.update(hyGroup);
			json.setSuccess(true);
			json.setMsg("消团成功！");
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/** 产品置顶*/
	@RequestMapping("topLine")
	@ResponseBody
	public Json topLine(Long lineId)
	{
		Json json=new Json();
		try{
			HyLine hyLine=hyLineService.find(lineId);
			hyLine.setIsTop(true);
			hyLine.setTopEditTime(new Date());
			hyLineService.update(hyLine);
			json.setSuccess(true);
			json.setMsg("置顶成功！");
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/** 取消产品置顶*/
	@RequestMapping("cancelTopLine")
	@ResponseBody
	public Json cancelTopLine(Long lineId)
	{
		Json json=new Json();
		try{
			HyLine hyLine=hyLineService.find(lineId);
			hyLine.setIsTop(false);
			hyLineService.update(hyLine);
			json.setSuccess(true);
			json.setMsg("取消置顶成功！");
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 保险详情接口
	 */
	@RequestMapping("insurancedetail/view")
	@ResponseBody
	public Json insurancedetail(Long id)
	{
		Json json=new Json();
		try{
			Insurance insurance = insuranceService.find(id);
			json.setSuccess(true);
			json.setObj(insurance);
			json.setMsg("查看保险详情成功");
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 查找供应商返利详情
	 * @param id
	 * @return
	 */
	@RequestMapping("fanli/view")
	@ResponseBody
	public Json fanli(HttpSession session)
	{
		Json json=new Json();
		try{
			HashMap<String, Object> hm = new HashMap<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin operator = hyAdminService.find(username);
			HySupplier supplier = null;
			if(operator.getHyAdmin() != null) { //子账号
				HyAdmin parent = operator.getHyAdmin();
				Set<HySupplierContract> supplierContracts = parent.getLiableContracts();
				for(HySupplierContract c : supplierContracts) {
					if(c.getContractStatus() == ContractStatus.zhengchang) {
						supplier = c.getHySupplier();
						break;
					}
				}
			} else {
				Set<HySupplierContract> supplierContracts1 = operator.getLiableContracts();
				for(HySupplierContract c : supplierContracts1) {
					if(c.getContractStatus() == ContractStatus.zhengchang) {
						supplier = c.getHySupplier();
						break;
					}
				}
			}
			if(null != supplier) {
				Long id = supplier.getId();
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("supplierId", id));
				List<HyProviderRebate> rebates = hyProviderRebateService.findList(null, filters, null);
				if(!rebates.isEmpty()) {
					HyProviderRebate result = rebates.get(0);
					hm.put("isVip", supplier.getIsVip());
					hm.put("moneyZhengjia", result.getRebate());
					hm.put("moneyTejia", result.getBargainRebate());
				}
			}
		    
		    json.setObj(hm);
		    json.setSuccess(true);
			json.setMsg("查看返利成功");			
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
}
