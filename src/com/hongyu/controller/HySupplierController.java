package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.BiangengkoudianEntity;
import com.hongyu.entity.CommonUploadFileEntity;
import com.hongyu.entity.Department;
import com.hongyu.entity.Gysfzrtuichu;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.entity.HySupplierDeductChujing;
import com.hongyu.entity.HySupplierDeductGuonei;
import com.hongyu.entity.HySupplierDeductPiaowu;
import com.hongyu.entity.HySupplierDeductQianzheng;
import com.hongyu.entity.HySupplierDeductQiche;
import com.hongyu.entity.HySupplierDeductRengou;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.HySupplierElement.SupplierType;
import com.hongyu.entity.XuqianEntity;
import com.hongyu.entity.CommonUploadFileEntity.UploadTypeEnum;
import com.hongyu.entity.XuqianEntity.Xuqianleixing;
import com.hongyu.service.BankListService;
import com.hongyu.service.BiangengkoudianService;
import com.hongyu.service.CommonUploadFileService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.GystuiyajinService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierDeductChujingService;
import com.hongyu.service.HySupplierDeductGuoneiService;
import com.hongyu.service.HySupplierDeductPiaowuService;
import com.hongyu.service.HySupplierDeductQianzhengService;
import com.hongyu.service.HySupplierDeductQicheService;
import com.hongyu.service.HySupplierDeductRengouService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.XuqianService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants.AuditStatus;
import com.hongyu.util.DateUtil;

/**
 * Controller-采购部供应商
 * @author guoxinze
 *
 */
@RestController
@RequestMapping("/admin/supplier/purchase/")
@Transactional(propagation = Propagation.REQUIRED)
public class HySupplierController {
	
	@Resource
	private TaskService taskService;

	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	
	
	@Resource(name = "hySupplierElementServiceImpl")
	HySupplierElementService  hySupplierElementService;
	
	@Resource(name="hySupplierContractServiceImpl")
	private HySupplierContractService hySupplierContractService;
	
	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	@Resource(name="hySupplierDeductChujingServiceImpl")
	private HySupplierDeductChujingService hySupplierDeductChujingService;
	
	@Resource(name="hySupplierDeductGuoneiServiceImpl")
	private HySupplierDeductGuoneiService hySupplierDeductGuoneiService;
	
	@Resource(name="hySupplierDeductQicheServiceImpl")
	private HySupplierDeductQicheService hySupplierDeductQicheService;
	
	@Resource(name="hySupplierDeductPiaowuServiceImpl")
	private HySupplierDeductPiaowuService hySupplierDeductPiaowuService;
	
	@Resource(name="hySupplierDeductRengouServiceImpl")
	private HySupplierDeductRengouService hySupplierDeductRengouService;
	
	@Resource(name="hySupplierDeductQianzhengServiceImpl")
	private HySupplierDeductQianzhengService hySupplierDeductQianzhengService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hyRoleServiceImpl")
	private HyRoleService hyRoleService;
	
	@Resource(name="departmentServiceImpl")
	private DepartmentService departmentService;
	
	@Resource(name="bankListServiceImpl")
	private BankListService bankListService;
	
	@Resource(name="hyDepartmentModelServiceImpl")
	private HyDepartmentModelService departmentModelService;
	
	@Resource(name = "biangengkoudianServiceImpl")
	private BiangengkoudianService biangengkoudianService;
	
	@Resource(name = "xuqianServiceImpl")
	private XuqianService xuqianService;
	
	@Resource(name = "gysfzrtuichuServiceImpl")
	private GystuiyajinService gystuiyajinService;
	
	@Resource(name = "commonUploadFileServiceImpl")
	private CommonUploadFileService commonUploadFileService;
	
//	/**
//	 * 没签约供应商的列表页
//	 */
//	@RequestMapping(value="nosupplierlist/view")
//	public Json nosupplierlist(Pageable pageable, String supplierName, HttpSession session, HttpServletRequest request) {
//		Json j = new Json();
//		try {
//			List<HashMap<String, Object>> obj = new ArrayList<>();
//			/**
//			 * 获取当前用户
//			 */
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin admin = hyAdminService.find(username);
//			
//			/** 
//			 * 获取用户权限范围
//			 */
//			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
//			
//			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
//			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
//			
//			List<Filter> filters = new ArrayList<>();
//			if(null != supplierName) {
//				filters.add(Filter.eq("supplierName", supplierName));
//			}		
//			filters.add(Filter.in("operator", hyAdmins));
//			
//			List<HySupplier> suppliers = hySupplierService.findList(null, filters, null);
//			List<HySupplier> result = new ArrayList<>();
//			
//			for(HySupplier supplier : suppliers) {
//				if(supplier.getHySupplierContracts().isEmpty()) {
//					result.add(supplier);
//				}
//			}
//			
//			for(HySupplier supplier : result) {
//				HashMap<String, Object> m = new HashMap<String, Object>();
//				m.put("id", supplier.getId());
//				m.put("supplierName", supplier.getSupplierName());
//				m.put("isLine", supplier.getIsLine());
//				m.put("isInner", supplier.getIsInner());
//				m.put("isDijie", supplier.getIsDijie());
//				m.put("operatorName", supplier.getOperator().getName());
//				
//				/** 当前用户对本条数据的操作权限 */
//				if(supplier.getOperator().equals(admin)){
//					if(co == CheckedOperation.view) {
//						m.put("privilege", "view");
//					} else {
//						m.put("privilege", "edit");
//					}
//				} else{
//					if(co == CheckedOperation.edit) {
//						m.put("privilege", "edit");
//					} else {
//						m.put("privilege", "view");
//					}
//				}
//				obj.add(m);
//			}
//			
//			j.setMsg("查看详情成功");
//			j.setSuccess(true);
//			j.setObj(obj);
//		} catch(Exception e) {
//			// TODO Auto-generated catch block
//			j.setSuccess(false);
//			j.setMsg(e.getMessage());
//		}
//		return j;
//	}

	/**
	 * 供应商详情-新增返回pid 用于前端判断是续签合同还是新建合同
	 * @param id
	 * @return
	 */
	@RequestMapping(value="supplierdetail/view", method = RequestMethod.GET)
	public Json detail(Long id) {
		Json j = new Json();
		List<Filter> filters = new ArrayList<>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> areaMap = new HashMap<String, Object>();
		Map<String, Object> operatorMap = new HashMap<String, Object>();
		
		try {
			HySupplier supplier = hySupplierService.find(id);
			map.put("id",supplier.getId());
			map.put("supplierName", supplier.getSupplierName());
			map.put("isLine", supplier.getIsLine());
			map.put("isInner", supplier.getIsInner());
			map.put("isDijie", supplier.getIsDijie());
			map.put("isCaigouqian", supplier.getIsCaigouqian());
			map.put("intro", supplier.getIntro());
			map.put("pinpaiName", supplier.getPinpaiName());
			map.put("isVip", supplier.getIsVip());
			
			HyArea area = supplier.getArea();
			List<Long> areaIds = area.getTreePaths();
			areaMap.put("id", area.getId());
			areaMap.put("ids", areaIds);
			areaMap.put("fullName", area.getFullName());
			map.put("area", areaMap);
			
			map.put("address", supplier.getAddress());
			map.put("yycode", supplier.getYycode());
			map.put("yy", supplier.getYy());
			map.put("jycode", supplier.getJycode());
			map.put("jy", supplier.getJy());
			map.put("supplierStatus", supplier.getSupplierStatus());
			
			operatorMap.put("username", supplier.getOperator().getUsername());
			operatorMap.put("name", supplier.getOperator().getName());
			map.put("operator", operatorMap);
			
			filters.clear();
			filters.add(Filter.eq("type", UploadTypeEnum.gystuibufenyajin));
			List<CommonUploadFileEntity> files = commonUploadFileService.findList(null, filters, null);
			
			if(!files.isEmpty() && files.get(0) != null) {
				map.put("quitFileUrl", files.get(0).getFileUrl());
			}
			
			Set<HySupplierContract> contracts = supplier.getHySupplierContracts();

			for(HySupplierContract c : contracts) {
				c.setSubAdmins(new ArrayList<>(c.getLiable().getHyAdmins()));
			}

			map.put("hySupplierContracts", contracts);
			j.setMsg("查看详情成功");
			j.setSuccess(true);
			j.setObj(map);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}

	/**
	 * 新增供应商 - 返回Obj里面 isLine isCaigouQian id
	 * @param id
	 * @return
	 */
	@RequestMapping(value="addsupplier", method = RequestMethod.POST)
	public Json addSupplier(HySupplier hySupplier, Long areaId, HttpSession session) {
		Json j = new Json();
		try{
			Map<String, Object> map = new HashMap<String, Object>();
			/** 得到当前用户所属部门 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			Department department = hyAdminService.find(username).getDepartment();
			
			/**
			 * 判断是否是采购部所签供应商
			 */
			if(department != null) {
				HyDepartmentModel departmentModel = department.getHyDepartmentModel();
				if(departmentModel.getName().equals("总公司采购部")) {
					hySupplier.setIsCaigouqian(true);
				}else {
					hySupplier.setIsCaigouqian(false);
				}
			}
			HyArea area = hyAreaService.find(areaId);
			hySupplier.setArea(area);
			hySupplierService.save(hySupplier);
			map.put("isLine", hySupplier.getIsLine());
			map.put("isCaigouqian", hySupplier.getIsCaigouqian());
			map.put("id", hySupplier.getId());

			j.setSuccess(true);
			j.setMsg("添加成功！");
			j.setObj(map);

		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg("供应商名称重复");
		}
		return j;
	}
	
	/**
	 * 新增合同
	 * @param id
	 * @return
	 */
	@RequestMapping(value="addsuppliercontract", method = RequestMethod.POST)
	public Json addContract(HySupplierContract hySupplierContract, Long supplierId, @DateTimeFormat(iso=ISO.DATE_TIME)Date sDate, @DateTimeFormat(iso=ISO.DATE_TIME)Date eDate,
			 				HyAdmin liable, Long roleId, Long departmentId, BankList bankList,
							HySupplierDeductChujing chujing, HySupplierDeductGuonei guonei,
							HySupplierDeductQiche qiche, HySupplierDeductPiaowu piaowu,
							HySupplierDeductQianzheng qianzheng, HySupplierDeductRengou rengou,
							Long[] cjAreas, Long[] gnAreas, Long[] qcAreas) {
		
		Json j = new Json();
		try{
			HySupplier supplier = hySupplierService.find(supplierId);
			hySupplierContract.setHySupplier(supplier);
			
			if(qianzheng.getDeductQianzheng() != null) {
				hySupplierContract.setHySupplierDeductQianzheng(qianzheng);
			}
			
			if(chujing.getDeductChujing() != null) {
				hySupplierContract.setHySupplierDeductChujing(chujing);
			}
			
			if(guonei.getDeductGuonei() != null) {
				hySupplierContract.setHySupplierDeductGuonei(guonei);
			}
			
			if(piaowu.getDeductPiaowu() != null) {
				hySupplierContract.setHySupplierDeductPiaowu(piaowu);
			}	
			
			if(rengou.getDeductRengou() != null) {
				hySupplierContract.setHySupplierDeductRengou(rengou);
			}
			
			if(qiche.getDeductQiche() != null){
				hySupplierContract.setHySupplierDeductQiche(qiche);
			}
			
			Department hyDepartment = departmentService.find(departmentId);
			HyRole role = hyRoleService.find(roleId);
			
			liable.setRole(role);
			liable.setDepartment(hyDepartment);
			
			hySupplierContract.setBankList(bankList);
			
			hySupplierContract.setLiable(liable);
//			System.err.println(sDate);
//			System.err.println(DateUtil.getStartOfDay(sDate));
//			System.err.println(DateUtil.getEndOfDay(eDate));
			hySupplierContract.setStartDate(DateUtil.getStartOfDay(sDate));
			hySupplierContract.setDeadDate(DateUtil.getEndOfDay(eDate));
			
			Set<HyArea> cAreas = new HashSet<HyArea>();
			if(cjAreas != null && cjAreas.length > 0) {
				for(Long id : cjAreas)
					cAreas.add(hyAreaService.find(id));
				hySupplierContract.setChujingAreas(cAreas);
			}
			
			Set<HyArea> gAreas = new HashSet<HyArea>();
			if(gnAreas != null && gnAreas.length > 0) {
				for(Long id : gnAreas)
					gAreas.add(hyAreaService.find(id));
				hySupplierContract.setGuoneiAreas(gAreas);
			}
			
			Set<HyArea> qAreas = new HashSet<HyArea>();
			if(qcAreas != null && qcAreas.length > 0) {
				for(Long id : qcAreas)
					qAreas.add(hyAreaService.find(id));
				hySupplierContract.setQicheAreas(qAreas);
			}
			
			hySupplierContractService.save(hySupplierContract);

			j.setSuccess(true);
			j.setMsg("添加成功！");
			j.setObj(hySupplierContract.getId()); //20180509修改，为了变更续签的时候使用

		}catch (Exception e) {
			// TODO Auto-generated catch block			
			j.setSuccess(false);
			e.printStackTrace();
			j.setMsg("合同号或负责人账号已存在!");
		}
		return j;
	}
	
	/**
	 * 合同列表，待加入按照供应商查询部分
	 * @param pageable
	 * @param hySupplier
	 * @param hySupplierContract
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value="list/view")
	public Json contractList(Pageable pageable, ContractStatus contractStatus, AuditStatus auditStatus,
			String supplierName, String contractCode, String liableName, String operatorName,
				Boolean isInner, Boolean isLine, Boolean isDijie, HySupplierContract hySupplierContract, 
				HttpSession session, HttpServletRequest request) {
		
			Json j = new Json();
				
			try {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				List<HashMap<String, Object>> result = new ArrayList<>();
			    
				//added by GSbing,按供应的最新合同时间倒序
				List<HySupplier> supplierList=new ArrayList<>();				
				
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

				/** 将数据按照供应商排序 */
				List<Order> orders = new ArrayList<Order>();
				Order order = Order.asc("hySupplier");
				orders.add(order);
				
				/** 数据按照创建人筛选 */
				List<Filter> filters = new ArrayList<Filter>();
				Filter filter = Filter.in("creater", hyAdmins);
				if(StringUtils.isNotBlank(contractCode)) {
					Filter filter6 = Filter.like("contractCode", contractCode);
					filters.add(filter6);
				}
				if(null != contractStatus) {
					Filter filter7 = Filter.eq("contractStatus", contractStatus);
					filters.add(filter7);
				}
				if(null != auditStatus) {
					Filter filter8 = Filter.eq("auditStatus", auditStatus);
					filters.add(filter8);
				}
				
				filters.add(filter);
				
				/** 查询出满足条件供应商 */
				List<Filter> filters1 = new ArrayList<Filter>();
				if(isInner != null){
					Filter filter1 = Filter.eq("isInner", isInner);
					filters1.add(filter1);
				}
				if(isLine != null) {
					Filter filter2 = Filter.eq("isLine", isLine);
					filters1.add(filter2);
				}
				if(isDijie != null) {
					Filter filter3 = Filter.eq("isDijie", isDijie);
					filters1.add(filter3);
				}
				if(StringUtils.isNotBlank(supplierName)) {
					Filter filter5 = Filter.like("supplierName", supplierName);
					filters1.add(filter5);
				}
			
				
				if(filters1.size() > 0) {
					Pageable pg = new Pageable();
					pg.setFilters(filters1);
					pg.setRows(1000);
					Page<HySupplier> page = hySupplierService.findPage(pg);
					
					//added by GSbing,20190227
					supplierList.addAll(page.getRows()); 
					
					if(page.getRows().size() == 0) {
						HashMap<String, Object> resultHashMap = new HashMap<>();
						resultHashMap.put("total", 0);
						resultHashMap.put("pageNumber", pageable.getPage());
						resultHashMap.put("pageSize", pageable.getRows());
						resultHashMap.put("rows", new ArrayList<>());
						j.setSuccess(true);
						j.setMsg("数据不存在");
						j.setObj(resultHashMap);
						return j;
					}
					Filter filter4 = Filter.in("hySupplier", page.getRows());
					filters.add(filter4);

				}
						
				/** 根据负责人查询 */
				if(StringUtils.isNotBlank(liableName)) {
					List<Filter> filters2 = new ArrayList<Filter>();
					Filter filter6 = Filter.like("name", liableName);
					filters2.add(filter6);
					Pageable pg = new Pageable();
					pg.setFilters(filters2);
					pg.setRows(1000);
					Page<HyAdmin> page = hyAdminService.findPage(pg);
					if(page.getRows().size() == 0) {
						HashMap<String, Object> resultHashMap = new HashMap<>();
						resultHashMap.put("total", 0);
						resultHashMap.put("pageNumber", pageable.getPage());
						resultHashMap.put("pageSize", pageable.getRows());
						resultHashMap.put("rows", new ArrayList<>());
						j.setSuccess(true);
						j.setMsg("数据不存在");
						j.setObj(resultHashMap);
						return j;
					}
					Filter filter7 = Filter.in("liable", page.getRows());
					filters.add(filter7);
				}
				
				/** 根据创建人查询 */  
				if(StringUtils.isNotBlank(operatorName)) {
					List<Filter> filters3 = new ArrayList<Filter>();
					Filter filter8 = Filter.like("name", operatorName);
					filters3.add(filter8);
					Pageable pg = new Pageable();
					pg.setFilters(filters3);
					pg.setRows(1000);
					Page<HyAdmin> page = hyAdminService.findPage(pg);
					if(page.getRows().size() == 0) {
						HashMap<String, Object> resultHashMap = new HashMap<>();
						resultHashMap.put("total", 0);
						resultHashMap.put("pageNumber", pageable.getPage());
						resultHashMap.put("pageSize", pageable.getRows());
						resultHashMap.put("rows", new ArrayList<>());
						j.setSuccess(true);
						j.setMsg("数据不存在");
						j.setObj(resultHashMap);
						return j;
					}
					Filter filter9 = Filter.in("creater", page.getRows());
					filters.add(filter9);
				}
				
				/** 找到分页的合同数据 */
				List<HySupplierContract> contractList = hySupplierContractService.findList(null,filters,orders);
				
				/** 遍历当前页合同数据，返回前端需要的数据格式 */
				for(HySupplierContract contract : contractList) {
					
					/** 找到合同创建人所属部门 */
					HyAdmin creater = contract.getCreater();
					HySupplier supplier = contract.getHySupplier();
					
					HashMap<String, Object> m = new HashMap<String, Object>();
					m.put("id", contract.getId());
					m.put("supplierId", supplier.getId());
					m.put("supplierName", supplier.getSupplierName());
					m.put("isVip", supplier.getIsVip());
					m.put("pinpaiName", supplier.getPinpaiName());
					m.put("supplierStatus", supplier.getSupplierStatus());
					m.put("isLine", supplier.getIsLine());
					m.put("isInner", supplier.getIsInner());
					m.put("isDijie", supplier.getIsDijie());
					m.put("isCaigouqian", supplier.getIsCaigouqian());
					m.put("contractCode", contract.getContractCode());
					m.put("liableName", contract.getLiable().getName());
					m.put("startDate", contract.getStartDate());
					m.put("endDate", contract.getDeadDate());
					m.put("auditStatus", contract.getAuditStatus());
					m.put("contractStatus", contract.getContractStatus());
					m.put("operatorName", supplier.getOperator().getName());
					
					/** 当前用户对本条数据的操作权限 */
					if(creater.equals(admin)){
						if(co == CheckedOperation.view) {
							m.put("privilege", "view");
						} else {
							m.put("privilege", "edit");
						}
					} else{
						if(co == CheckedOperation.edit) {
							m.put("privilege", "edit");
						} else {
							m.put("privilege", "view");
						}
					}
					result.add(m);
				}
				
				//added by GSbing,20190227,按供应商的最新合同时间排序
				Collections.sort(result,new Comparator<HashMap<String, Object>>() {
					@Override
					public int compare(HashMap<String, Object> m1,HashMap<String, Object> m2) {
						int returnNum=0;
						Long supplierId1=(Long)m1.get("supplierId");
						Long supplierId2=(Long)m2.get("supplierId");
						HySupplier h1=hySupplierService.find(supplierId1);
						HySupplier h2=hySupplierService.find(supplierId2);
						List<HySupplierContract> supplierContracts1=new ArrayList<>(h1.getHySupplierContracts());
						List<HySupplierContract> supplierContracts2=new ArrayList<>(h2.getHySupplierContracts());
						List<Date> dates1=new ArrayList<>();
					    for(HySupplierContract temp:supplierContracts1) {
					    	dates1.add(temp.getStartDate());
					    }
					    List<Date> dates2=new ArrayList<>();
					    for(HySupplierContract temp:supplierContracts2) {
					    	dates2.add(temp.getStartDate());
					    }
					    //分别找出最大日期
					    Date latestDate1=Collections.max(dates1);
					    Date latestDate2=Collections.max(dates2);
					    if(latestDate1.compareTo(latestDate2)==0) {
					    	if(h1.getSupplierName().compareTo(h2.getSupplierName())==0) {
					    		Date startDate1=(Date)m1.get("startDate");
					    		Date startDate2=(Date)m2.get("startDate");
					    		returnNum = startDate2.compareTo(startDate1);
					    	}
					    	else {
					    		returnNum = h1.getSupplierName().compareTo(h2.getSupplierName());
					    	}
					    }
					    else {
					    	returnNum = latestDate2.compareTo(latestDate1);
					    }
					    return returnNum;
					}
				});
				
				int page = pageable.getPage();
				int rows = pageable.getRows();
				hm.put("pageNumber", page);
				hm.put("pageSize", rows);
				hm.put("total", result.size());
				hm.put("rows", result.subList((page - 1) * rows, page * rows > result.size() ? result.size() : page * rows));
				j.setSuccess(true);
				j.setMsg("查找成功！");
				j.setObj(hm);
			} catch(Exception e) {
				// TODO Auto-generated catch block
				j.setSuccess(false);
				j.setMsg("查找失败");
			}
			
			return j;
	}
	
	/**
	 * 查找中国下属所有省的信息
	 * @return
	 */
	@RequestMapping(value="province/view", method = RequestMethod.GET)
	public Json getProvince() {
		Json j = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<>();
			HyArea pArea = hyAreaService.find(0L); //找到全国
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.eq("hyArea", pArea);
			Filter filter1 = Filter.eq("status", true);
			filters.add(filter1);
			filters.add(filter);
			List<HyArea> list = hyAreaService.findList(null, filters, null);
			hm.put("total", list.size());
			hm.put("data", list);
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(list);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 得到国外国家信息
	 * @return
	 */
	@RequestMapping(value="country/view", method = RequestMethod.GET)
	public Json getCountry() {
		Json j = new Json();
		try {
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.eq("treePath", ",1,");
			Filter filter1 = Filter.eq("status", true);
			filters.add(filter1);
			filters.add(filter);
			List<HyArea> list = hyAreaService.findList(null, filters, null);
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(list);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 由父区域的ID得到全部的子区域
	 * @param id
	 * @return
	 */
	@RequestMapping(value="areacomboxlist/view", method = RequestMethod.GET)
	public Json getSubAreas(Long id) {
		Json j = new Json();
		try {
			HashMap<String, Object> hashMap = new HashMap<>();
			HyArea parent = hyAreaService.find(id);
			List<HashMap<String, Object>> obj = new ArrayList<>();
			if(parent != null && parent.getHyAreas().size() > 0) {
				for (HyArea child : parent.getHyAreas()) {
					if(child.getStatus()) {
						HashMap<String, Object> hm = new HashMap<>();
						hm.put("value", child.getId());
						hm.put("label", child.getName());
						hm.put("isLeaf", child.getHyAreas().size() == 0);
						obj.add(hm);
					}
				}
			}
			hashMap.put("total", parent.getHyAreas().size());
			hashMap.put("data", obj);
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(obj);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="hebing/view", method = RequestMethod.GET)
	public Json hebing(HttpSession session) {
		Json j = new Json();
		try {
			//部门的实体类
			HashMap<String, Object> hm = new HashMap<>();
			/** 得到当前用户所属部门 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			Department department = hyAdminService.find(username).getDepartment();
			/**
			 * 判断是否是采购部
			 */
			Set<Department> subDepartments = new HashSet<Department>();
			if(department != null) {
				HyDepartmentModel departmentModel = department.getHyDepartmentModel();
				if(departmentModel.getName().equals("总公司采购部")) {
					/** 直属下级 */
					Set<Department> directDepartments = department.getHyDepartments();
					if(directDepartments.size() > 0) {
						subDepartments.addAll(directDepartments);
					}
					
					/** 总公司、分公司产品中心直属下级部门 */
					HyDepartmentModel model1 = departmentModelService.find("总公司产品研发中心");
					HyDepartmentModel model2 = departmentModelService.find("分公司产品中心");
					HyDepartmentModel model3 = departmentModelService.find("总公司票务部");
					Set<HyDepartmentModel> models = new HashSet<>();
					models.add(model1);
					models.add(model2);
					models.add(model3);
					
					List<Filter> filters = new ArrayList<Filter>();
					Filter filter = Filter.in("hyDepartmentModel", models);
					filters.add(filter);
					List<Department> parentDepartments = departmentService.findList(null, filters, null);
					if(parentDepartments.size() > 0) {
						for(Department parentDepartment : parentDepartments) {	
							subDepartments.addAll(parentDepartment.getHyDepartments());
						}
					}
				}else if(departmentModel.getName().equals("分公司汽车部")){
					/** 找到所有的分公司汽车部 */
					List<Filter> filters = new ArrayList<Filter>();
					Filter filter = Filter.eq("hyDepartmentModel", departmentModel);
					filters.add(filter);
					List<Department> parentDepartments = departmentService.findList(null, filters, null);
					
					if(parentDepartments.size() > 0) {
						for(Department parentDepartment : parentDepartments) {	
							subDepartments.addAll(parentDepartment.getHyDepartments());
						}
					}	
				}
			}
			hm.put("total", subDepartments.size());
			hm.put("data", subDepartments);
			
			
			
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 更新供应商
	 * @param hySupplier 供应商
	 * @param areaId 区域ID
	 * @return
	 */
	@RequestMapping(value="updatesupplier", method = RequestMethod.POST)
	public Json updateSupplier(HySupplier hySupplier, Long areaId) {
		Json j = new Json();
		try {
			HyArea hyArea = hyAreaService.find(areaId);
			hySupplier.setArea(hyArea);
			hySupplierService.update(hySupplier, "isLine", "isInner", "isDijie", "isCaigouqian", "isActive", "supplierStatus", "operator", "hySupplierContracts");
			j.setSuccess(true);
			j.setMsg("更新成功！");
			//新增编辑供应商同步更新地接供应商 先不修改
//			if(null != hySupplier && hySupplier.getIsDijie() == true) {
//				List<Filter> fs = new ArrayList<>();
//				fs.add(Filter.eq("supplierLine", hySupplier.getId()));
//				fs.add(Filter.eq("supplierType", SupplierType.linelocal));
//				List<HySupplierElement> elements = hySupplierElementService.findList(null, fs, null);
//				if(!elements.isEmpty() && null != elements.get(0)) {
//					HySupplierElement element = elements.get(0);
//					element.setName(hySupplier.getSupplierName());
//					element.setLiableperson(hySupplier.getOperator().getName());
//					element.setTelephone(hySupplier.getOperator().getMobile());
//					element.setSupplierLine(hySupplier.getId());				
//					element.setIsShouru(false);
//					hySupplierElementService.update(element);
//				}
//			}
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 更新合同--新增变更续签的时候更新被驳回的续签合同的部分
	 * @param hySupplierContract 合同信息
	 * @param sDate 合同开始日期
	 * @param eDate 合同结束日期
	 * @param liable 负责人
	 * @param roleId 负责人角色ID
	 * @param departmentId 负责人部门ID
	 * @param bankList 银行信息
	 * @param chujing 出境扣点
	 * @param guonei 国内扣点
	 * @param qiche 汽车扣点
	 * @param piaowu 票务扣点
	 * @param qianzheng 签证扣点
	 * @param rengou 认购扣点
	 * @param cjAreas 出境区域
	 * @param gnAreas 国内区域
	 * @param qcAreas 汽车区域
	 * @return
	 */
	@RequestMapping(value="updatesuppliercontract", method = RequestMethod.POST)
	public Json updateContract(HySupplierContract hySupplierContract, @DateTimeFormat(iso=ISO.DATE_TIME)Date sDate, @DateTimeFormat(iso=ISO.DATE_TIME)Date eDate,
				HyAdmin liable, Long roleId, Long departmentId, BankList bankList,
			HySupplierDeductChujing chujing, HySupplierDeductGuonei guonei,
			HySupplierDeductQiche qiche, HySupplierDeductPiaowu piaowu,
			HySupplierDeductQianzheng qianzheng, HySupplierDeductRengou rengou,
			Long[] cjAreas, Long[] gnAreas, Long[] qcAreas, Long bankId,
			Long supplierId, Long cjDeductId, Long gnDeductId, Long qcDeductId, Long pwDeductId,
			Long qzDeductId, Long rgDeductId) {
		
		Json j = new Json();
		try {
			
			if(qianzheng.getDeductQianzheng() != null) {
				if(qzDeductId != null)
					qianzheng.setId(qzDeductId);
				hySupplierContract.setHySupplierDeductQianzheng(qianzheng);
			}
			
			if(chujing.getDeductChujing() != null) {
				if(cjDeductId != null)
					chujing.setId(cjDeductId);
				hySupplierContract.setHySupplierDeductChujing(chujing);
			}
			
			if(guonei.getDeductGuonei() != null) {
				if(gnDeductId != null)
					guonei.setId(gnDeductId);
				hySupplierContract.setHySupplierDeductGuonei(guonei);
			}
			
			if(piaowu.getDeductPiaowu() != null) {
				if(pwDeductId != null)
					piaowu.setId(pwDeductId);
				hySupplierContract.setHySupplierDeductPiaowu(piaowu);
			}	
			
			if(rengou.getDeductRengou() != null) {
				if(rgDeductId != null)
					rengou.setId(rgDeductId);
				hySupplierContract.setHySupplierDeductRengou(rengou);
			}
			
			if(qiche.getDeductQiche() != null){
				if(qcDeductId != null)
					qiche.setId(qcDeductId);
				hySupplierContract.setHySupplierDeductQiche(qiche);
			}
			
			Department hyDepartment = departmentService.find(departmentId);
			HyRole role = hyRoleService.find(roleId);
			
			//zjl add at 01/25
			HyAdmin oldAdmin = hyAdminService.find(liable.getUsername());
			liable.setPassword(oldAdmin.getPassword());
			
			liable.setRole(role);
			liable.setDepartment(hyDepartment);
			
			//钟俊林写的
			BankList oldBankList = bankListService.find(bankId);
			bankList.setId(bankId);
			bankList.setType(oldBankList.getType());
			
			
			hySupplierContract.setBankList(bankList);
			hySupplierContract.setLiable(liable);
			
			hySupplierContract.setStartDate(DateUtil.getStartOfDay(sDate));
			hySupplierContract.setDeadDate(DateUtil.getEndOfDay(eDate));
			
			Set<HyArea> cAreas = new HashSet<HyArea>();
			if(cjAreas != null && cjAreas.length > 0) {
				for(Long id : cjAreas)
					cAreas.add(hyAreaService.find(id));
				hySupplierContract.setChujingAreas(cAreas);	
			}
			//新增，如果供应商合同区域改变，将子账号区域也取消
			if(null != hySupplierContract.getLiable()) {
				HyAdmin li = hySupplierContract.getLiable();
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("hyAdmin", li));
				List<HyAdmin> sons = hyAdminService.findList(null ,filters, null);
				for(HyAdmin temp : sons) {
					Set<HyArea> areas = temp.getAreaChujing();
					boolean flag = false;
					
					for(Iterator<HyArea> iterator = areas.iterator(); iterator.hasNext();) {
						if(!cAreas.contains(iterator.next())) {
							flag = true;
							iterator.remove();
						}
					}
					
					if(flag) {
						temp.setAreaChujing(areas);
						hyAdminService.update(temp);
					}
					
				}
			}
			
			Set<HyArea> gAreas = new HashSet<HyArea>();
			if(gnAreas != null && gnAreas.length > 0) {
				for(Long id : gnAreas)
					gAreas.add(hyAreaService.find(id));
				hySupplierContract.setGuoneiAreas(gAreas);
			}
			
			if(null != hySupplierContract.getLiable()) {
				HyAdmin li = hySupplierContract.getLiable();
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("hyAdmin", li));
				List<HyAdmin> sons = hyAdminService.findList(null ,filters, null);
				for(HyAdmin temp : sons) {
					Set<HyArea> areas = temp.getAreaGuonei();
					boolean flag = false;
					
					for(Iterator<HyArea> iterator = areas.iterator(); iterator.hasNext();) {
						if(!gAreas.contains(iterator.next())) {
							flag = true;
							iterator.remove();
						}
					}
					
					if(flag) {
						temp.setAreaGuonei(areas);
						hyAdminService.update(temp);
					}
					
				}
			}
			
			Set<HyArea> qAreas = new HashSet<HyArea>();
			if(qcAreas != null && qcAreas.length > 0) {
				for(Long id : qcAreas)
					qAreas.add(hyAreaService.find(id));
				hySupplierContract.setQicheAreas(qAreas);
			}
			if(null != hySupplierContract.getLiable()) {
				HyAdmin li = hySupplierContract.getLiable();
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("hyAdmin", li));
				List<HyAdmin> sons = hyAdminService.findList(null ,filters, null);

				for(HyAdmin temp : sons) {
					Set<HyArea> areas = temp.getAreaQiche();
					boolean flag = false;
					
					for(Iterator<HyArea> iterator = areas.iterator(); iterator.hasNext();) {
						if(!qAreas.contains(iterator.next())) {
							flag = true;
							iterator.remove();
						}
					}
					
					if(flag) {
						temp.setAreaQiche(areas);
						hyAdminService.update(temp);
					}
					
				}
			}
			
			hySupplierContract.setShouldpayDeposit(hySupplierContract.getDeposit());
			hySupplierContract.setReturnDeposit(hySupplierContract.getDeposit());
			
			hySupplierContractService.update(hySupplierContract, "hySupplier", "auditStatus", "contractStatus",
											"xuqianEntities","creater", "hySupplierContract", "hySupplierContracts", 
											"subAdmins", "processId", "applyTime");
			j.setSuccess(true);
			j.setMsg("更新成功！");
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 获取当前用户直属下级部门
	 * @param session
	 * @return
	 */
	@RequestMapping(value="department/view", method = RequestMethod.GET)
	public Json getDepartments(HttpSession session) {
		Json j = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<>();
			/** 得到当前用户所属部门 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			Department department = hyAdminService.find(username).getDepartment();
			/**
			 * 判断是否是采购部
			 */
			Set<Department> subDepartments = new HashSet<Department>();
			if(department != null) {
				HyDepartmentModel departmentModel = department.getHyDepartmentModel();
				if(departmentModel.getName().equals("总公司采购部")) {
					/** 直属下级 */
					Set<Department> directDepartments = department.getHyDepartments();
					if(directDepartments.size() > 0) {
						subDepartments.addAll(directDepartments);
					}
					
					/** 总公司、分公司产品中心直属下级部门 */
					HyDepartmentModel model1 = departmentModelService.find("总公司产品研发中心");
					HyDepartmentModel model2 = departmentModelService.find("分公司产品中心");
					HyDepartmentModel model3 = departmentModelService.find("总公司票务部");
					Set<HyDepartmentModel> models = new HashSet<>();
					models.add(model1);
					models.add(model2);
					models.add(model3);
					
					List<Filter> filters = new ArrayList<Filter>();
					Filter filter = Filter.in("hyDepartmentModel", models);
					filters.add(filter);
					List<Department> parentDepartments = departmentService.findList(null, filters, null);
					if(parentDepartments.size() > 0) {
						for(Department parentDepartment : parentDepartments) {	
							subDepartments.addAll(parentDepartment.getHyDepartments());
						}
					}
				}else if(departmentModel.getName().equals("分公司汽车部")){
					/** 找到所有的分公司汽车部 */
					List<Filter> filters = new ArrayList<Filter>();
					Filter filter = Filter.eq("hyDepartmentModel", departmentModel);
					filters.add(filter);
					List<Department> parentDepartments = departmentService.findList(null, filters, null);
					
					if(parentDepartments.size() > 0) {
						for(Department parentDepartment : parentDepartments) {	
							subDepartments.addAll(parentDepartment.getHyDepartments());
						}
					}	
				}
			}
			hm.put("total", subDepartments.size());
			hm.put("data", subDepartments);
			j.setSuccess(true);
			j.setMsg("查看成功！");
			j.setObj(subDepartments);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 供应商合同提交审核--允许修改以后重新提交
	 * @param 供应商合同ID数组
	 * @return
	 */
	@RequestMapping(value="submitAudit")
	public Json submitAudit(Long[] contractIds, HttpSession session) {
		Json j = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			if(contractIds == null || contractIds.length == 0) {
				throw new RuntimeException("参数错误");
			}
			
			for(Long id : contractIds) {
				HySupplierContract a = hySupplierContractService.find(id);
				/********************************驳回以后在原来基础上提交******************************/
				if(a.getAuditStatus() == AuditStatus.notpass) {
					Task task = taskService.createTaskQuery().processInstanceId(a.getProcessId()).singleResult();
					a.setAuditStatus(AuditStatus.auditing);
					a.setContractStatus(ContractStatus.shenhezhong);
					HySupplier s = a.getHySupplier();
					if(s != null && s.getSupplierStatus() != AuditStatus.pass) {
						s.setSupplierStatus(AuditStatus.auditing);
					}
					a.setApplyTime(new Date());
					hySupplierContractService.update(a);	
					Authentication.setAuthenticatedUserId(username);
					taskService.addComment(task.getId(), a.getProcessId(), " :1");
					taskService.complete(task.getId());
				} else {
					ProcessInstance pi = runtimeService.startProcessInstanceByKey("cgbgysprocess");
					// 根据流程实例Id查询任务
					Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
					// 完成 

					a.setAuditStatus(AuditStatus.auditing);
					a.setContractStatus(ContractStatus.shenhezhong);
					HySupplier s = a.getHySupplier();
					if(s != null && s.getSupplierStatus() != AuditStatus.pass) {
						s.setSupplierStatus(AuditStatus.auditing);
					}
					a.setProcessId(pi.getProcessInstanceId());
					a.setApplyTime(new Date());
					hySupplierContractService.update(a);
					Authentication.setAuthenticatedUserId(username);
					taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
					taskService.complete(task.getId());
				}			
				/********************************       结束                    ******************************/		
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
	 * 供应商变更扣点的申请
	 * @param chujing
	 * @param guonei
	 * @param qiche
	 * @param piaowu
	 * @param qianzheng
	 * @param rengou
	 * @param cjDeductId
	 * @param gnDeductId
	 * @param qcDeductId
	 * @param pwDeductId
	 * @param qzDeductId
	 * @param rgDeductId
	 * @param session
	 * @return
	 */
	@RequestMapping(value="changdeduct")
	public Json changdeduct(Long contractId, HySupplierDeductChujing chujing, HySupplierDeductGuonei guonei,
							HySupplierDeductQiche qiche, HySupplierDeductPiaowu piaowu,
						    HySupplierDeductQianzheng qianzheng, HySupplierDeductRengou rengou, 
						    Long cjDeductId, Long gnDeductId, Long qcDeductId, Long pwDeductId,
							Long qzDeductId, Long rgDeductId, HttpSession session) {
		Json j = new Json();
		try {
			//开启变更扣点的流程
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("biangengkoudianprocess");
			HySupplierContract contract = hySupplierContractService.find(contractId);
			contract.setAuditStatus(AuditStatus.auditing); //修改，变更扣点将合同状态设置为审核中，这时候合同的所有产品不能购买
			hySupplierContractService.update(contract); 
			// 根据流程实例Id查询任务
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			// 完成任务，根据监听器设置下一步审核人
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId());	
			
			/*******************************************设置业务数据**********************************************/
			BiangengkoudianEntity entity = new BiangengkoudianEntity();
			if(qianzheng.getDeductQianzheng() != null) {
				hySupplierDeductQianzhengService.save(qianzheng);
				if(qzDeductId != null) {
					entity.setDeductQianzhengOld(qzDeductId);
					entity.setDeductQianzhengNew(qianzheng.getId());
				}
			}
			
			if(chujing.getDeductChujing() != null) {
				hySupplierDeductChujingService.save(chujing);
				if(cjDeductId != null) {
					entity.setDeductChujingOld(cjDeductId);
					entity.setDeductChujingNew(chujing.getId());
				}
			}
			
			if(guonei.getDeductGuonei() != null) {
				hySupplierDeductGuoneiService.save(guonei);
				if(gnDeductId != null) {
					entity.setDeductGuoneiOld(gnDeductId);
					entity.setDeductGuoneiNew(guonei.getId());
				}
			}
			
			if(piaowu.getDeductPiaowu() != null) {
				hySupplierDeductPiaowuService.save(piaowu);
				if(pwDeductId != null) {
					entity.setDeductPiaowuOld(pwDeductId);
					entity.setDeductPiaowuNew(piaowu.getId());
				}
			}	
			
			if(rengou.getDeductRengou() != null) {
				hySupplierDeductRengouService.save(rengou);
				if(rgDeductId != null) {
					entity.setDeductRengouOld(rgDeductId);
					entity.setDeductRengouNew(rengou.getId());
				}
			}
			
			if(qiche.getDeductQiche() != null){
				hySupplierDeductQicheService.save(qiche);
				if(qcDeductId != null) {
					entity.setDeductQicheOld(qcDeductId);
					entity.setDeductQicheNew(qiche.getId());
				}
			}
			entity.setContractId(contract);
			entity.setApplyName(username);
			entity.setApplyTime(new Date());
			entity.setAuditStatus(AuditStatus.auditing);
			entity.setProcessInstanceId(pi.getProcessInstanceId());
			biangengkoudianService.save(entity);
			j.setMsg("提交变更扣点申请成功");
			j.setSuccess(true);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 重置用户密码为 12345
	 * @param username
	 * @return
	 */
	@RequestMapping(value="resetpw",method = RequestMethod.POST)
	public Json resetpw(String username) {
		Json j = new Json();
		HyAdmin hyAdmin = hyAdminService.find(username);
		hyAdmin.setPassword("12345");
		hyAdminService.updatePassword(hyAdmin);
		j.setSuccess(true);
		j.setMsg("重置成功！");
		return j;
	}
	
	/**
	 * 新建续签合同 (包括直接续签和变更续签)
	 * @param xuqian 直接续签合同信息
	 * @param originalContratId 直接续签的原合同id
	 * @param yibohuiContractId 已经被驳回重新提交的续签合同id
	 * @param contract 变更续签的合同信息
	 * @param supplierId 变更续签合同供应商的id
	 * @param sDate 变更续签合同生效日期
	 * @param eDate	变更续签合同结束日期
	 * @param liable 合同负责人
	 * @param roleId 负责人角色信息
	 * @param departmentId 负责人部门信息
	 * @param bankList 合同银行信息
	 * @param chujing 出境扣点
	 * @param guonei 国内扣点
	 * @param qiche 汽车扣点
	 * @param piaowu 票务扣点
	 * @param qianzheng 签证扣点
	 * @param rengou 人头扣点
	 * @param cjAreas 合同出境区域
	 * @param gnAreas 合同国内区域
	 * @param qcAreas 合同汽车区域
	 * @param session
	 * @return
	 */
	@RequestMapping(value="xuqian")
	public Json xuqian(XuqianEntity xuqian, Long originalContratId, Long yibohuiContractId, HySupplierContract contract, 
				Long supplierId, @DateTimeFormat(iso=ISO.DATE_TIME)Date sDate, @DateTimeFormat(iso=ISO.DATE_TIME)Date eDate,
				Long madeRoleId, BankList bankList, String liableUsername,
				HySupplierDeductChujing chujing, HySupplierDeductGuonei guonei,
				HySupplierDeductQiche qiche, HySupplierDeductPiaowu piaowu,
				HySupplierDeductQianzheng qianzheng, HySupplierDeductRengou rengou,
				Long[] cjAreas, Long[] gnAreas, Long[] qcAreas, HttpSession session) {
		
		Json j = new Json();
		try {
			//开启续签的流程
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HySupplierContract originalContract = hySupplierContractService.find(originalContratId);
			if(xuqian != null && xuqian.getXqlx() == Xuqianleixing.zhijie) { //续签类型为直接续签，每次都重新开启流程实例		
				
				xuqian.setEndDate(DateUtil.getEndOfDay(xuqian.getEndDate()));
				ProcessInstance pi = runtimeService.startProcessInstanceByKey("xuqianprocess");
				// 根据流程实例Id查询任务
				Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
				//设置续签的业务数据
				xuqian.setApplyName(username);
				xuqian.setAuditStatus(AuditStatus.auditing);
				xuqian.setApplyTime(new Date());
				xuqian.setProcessInstanceId(pi.getProcessInstanceId());
				xuqian.setContractId(originalContract);
				xuqianService.save(xuqian);			
				//完成任务
				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), xuqian.getProcessInstanceId(), " :1");
				taskService.complete(task.getId());
				
			} else if(xuqian != null && xuqian.getXqlx() == Xuqianleixing.biangeng) { //续签类型为变更续签
				//变更续签包括两种情况
				//1、第一次变更续签  只传过来原合同的id 新开启一个流程实例
				//2、在被驳回的基础上修改续签 传过来原合同id 旧的被驳回的合同的id 新开启一个流程实例
				

					ProcessInstance pi = runtimeService.startProcessInstanceByKey("xuqianprocess");
					// 根据流程实例Id查询任务
					Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
					//设置续签的业务数据
					xuqian.setApplyName(username);
					xuqian.setAuditStatus(AuditStatus.auditing);
					xuqian.setApplyTime(new Date());
					xuqian.setProcessInstanceId(pi.getProcessInstanceId());
					//将续签的合同信息保存在合同表中
					Long newContractId = addXuqianContract(originalContratId, yibohuiContractId, contract, supplierId, sDate, eDate, 
											bankList, liableUsername, chujing, guonei, qiche, piaowu, 
											qianzheng, rengou, cjAreas, gnAreas, qcAreas);
					HySupplierContract newContract = hySupplierContractService.find(newContractId);
					HyRole r = hyRoleService.find(madeRoleId);
					xuqian.setContractId(newContract);
					xuqian.setRoleId(r);
					xuqianService.save(xuqian);			
					//完成任务
					Authentication.setAuthenticatedUserId(username);
					taskService.addComment(task.getId(), xuqian.getProcessInstanceId(), " :1");
					taskService.complete(task.getId());
					
			}
			j.setMsg("提交续签申请成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 采购部退还供应商部分押金申请
	 * @param contractId 退还的合同ID
	 * @param tc 
	 * @param session
	 * @return
	 */
	@RequestMapping(value="tuiyajin")
	public Json tuiyajin(Long contractId, Gysfzrtuichu tc, HttpSession session) {
		Json j = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			//开启退部分押金的流程
			ProcessInstance pi = runtimeService.startProcessInstanceByKey("tuiyajinprocess");
			// 根据流程实例Id查询任务
			Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
			
			//新增退部分押金申请的记录
			HySupplierContract c = hySupplierContractService.find(contractId);
			if(c == null) {
				throw new RuntimeException("合同不存在");
			} else {
				tc.setContract(c);
			}
			tc.setApplierName(username);
			tc.setApplyTime(new Date());
			tc.setAuditStatus(AuditStatus.auditing);
			tc.setProcessInstanceId(pi.getProcessInstanceId());
			gystuiyajinService.save(tc);
			Authentication.setAuthenticatedUserId(username);
			taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
			taskService.complete(task.getId());	
			j.setMsg("提交退部分押金申请成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	
	
	
	/**
	 * 增加续签合同的接口--在增加续签合同的时候负责人信息和供应商信息不能修改，只能修改续签表中的负责人信息
	 * @param originalContratId
	 * @param yibohuiContractId
	 * @param hySupplierContract
	 * @param supplierId
	 * @param sDate
	 * @param eDate
	 * @param liable
	 * @param roleId
	 * @param departmentId
	 * @param bankList
	 * @param chujing
	 * @param guonei
	 * @param qiche
	 * @param piaowu
	 * @param qianzheng
	 * @param rengou
	 * @param cjAreas
	 * @param gnAreas
	 * @param qcAreas
	 * @return
	 */
	public Long addXuqianContract(Long originalContratId, Long yibohuiContractId, HySupplierContract hySupplierContract, Long supplierId, 
			@DateTimeFormat(iso=ISO.DATE_TIME)Date sDate, @DateTimeFormat(iso=ISO.DATE_TIME)Date eDate,
			BankList bankList, String username,
			HySupplierDeductChujing chujing, HySupplierDeductGuonei guonei,
			HySupplierDeductQiche qiche, HySupplierDeductPiaowu piaowu,
			HySupplierDeductQianzheng qianzheng, HySupplierDeductRengou rengou,
			Long[] cjAreas, Long[] gnAreas, Long[] qcAreas) {
		Json j = new Json();
		try{
			HySupplierContract originalContract = hySupplierContractService.find(originalContratId);
			HyAdmin liable = hyAdminService.find(username); //合同负责人
			HySupplier supplier = hySupplierService.find(supplierId);
			hySupplierContract.setHySupplier(supplier);
			
			if(qianzheng.getDeductQianzheng() != null) {
				hySupplierContract.setHySupplierDeductQianzheng(qianzheng);
			}
			
			if(chujing.getDeductChujing() != null) {
				hySupplierContract.setHySupplierDeductChujing(chujing);
			}
			
			if(guonei.getDeductGuonei() != null) {
				hySupplierContract.setHySupplierDeductGuonei(guonei);
			}
			
			if(piaowu.getDeductPiaowu() != null) {
				hySupplierContract.setHySupplierDeductPiaowu(piaowu);
			}	
			
			if(rengou.getDeductRengou() != null) {
				hySupplierContract.setHySupplierDeductRengou(rengou);
			}
			
			if(qiche.getDeductQiche() != null){
				hySupplierContract.setHySupplierDeductQiche(qiche);
			}
	
			hySupplierContract.setBankList(bankList);
			System.err.println(sDate);
			System.err.println(DateUtil.getStartOfDay(sDate));
			System.err.println(DateUtil.getEndOfDay(eDate));
			hySupplierContract.setStartDate(DateUtil.getStartOfDay(sDate));
			hySupplierContract.setDeadDate(DateUtil.getEndOfDay(eDate));
			
			Set<HyArea> cAreas = new HashSet<HyArea>();
			if(cjAreas != null && cjAreas.length > 0) {
				for(Long id : cjAreas)
					cAreas.add(hyAreaService.find(id));
				hySupplierContract.setChujingAreas(cAreas);
			}
			
			Set<HyArea> gAreas = new HashSet<HyArea>();
			if(gnAreas != null && gnAreas.length > 0) {
				for(Long id : gnAreas)
					gAreas.add(hyAreaService.find(id));
				hySupplierContract.setGuoneiAreas(gAreas);
			}
			
			Set<HyArea> qAreas = new HashSet<HyArea>();
			if(qcAreas != null && qcAreas.length > 0) {
				for(Long id : qcAreas)
					qAreas.add(hyAreaService.find(id));
				hySupplierContract.setQicheAreas(qAreas);
			}
			hySupplierContract.setLiable(liable);
			hySupplierContract.setHySupplierContract(originalContract);//设置新合同的父合同
			hySupplierContract.setAuditStatus(AuditStatus.auditing);
			hySupplierContract.setContractStatus(ContractStatus.shenhezhong);
			
			if(yibohuiContractId != null) { //如果是在原来被驳回的基础上修改，需要先删除原来被驳回的合同，因为可能会出现合同号重复
				HySupplierContract c = hySupplierContractService.find(yibohuiContractId);
				hySupplierContractService.delete(c);
			}
			
			hySupplierContractService.save(hySupplierContract);
			

		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			e.printStackTrace();
			j.setMsg(e.getMessage());
		}
		return hySupplierContract.getId();
	}
	
	/**
	 * 判断合同是否续签
	 * added by Gsbing 20180714
	 * @param contractId 续签的合同ID
	 * @param session
	 * @return
	 */
	@RequestMapping(value="searchXuqian/view")
	public Json searchXuqian(Long contractId)
	{
		Json json=new Json();
		try {
			HySupplierContract contract=hySupplierContractService.find(contractId);
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("hySupplierContract", contract));
			List<HySupplierContract> supplierContracts=hySupplierContractService.findList(null,filters,null);
			HashMap<String,Object> map=new HashMap<String,Object>();
			if(supplierContracts.isEmpty()) {
				map.put("status",false);
				map.put("contractStatus",ContractStatus.weitijiao);
				map.put("auditStatus",AuditStatus.unsubmitted);
			}
			else {
				map.put("status",true);
				map.put("contractStatus",supplierContracts.get(0).getContractStatus());
				map.put("auditStatus",supplierContracts.get(0).getAuditStatus());
			}
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 编辑合同日期
	 * @param startDate
	 * @param deadDate
	 * @return
	 */
	@RequestMapping(value="updateDate")
	public Json updateDate(@DateTimeFormat(iso=ISO.DATE_TIME)Date startDate, @DateTimeFormat(iso=ISO.DATE_TIME)Date deadDate, Long id) {
		Json json=new Json();
		try {
			HySupplierContract contract = hySupplierContractService.find(id);
			if(contract == null) {
				json.setMsg("合同不存在");
				json.setSuccess(false);
				return json;
			}
			contract.setStartDate(startDate);
			contract.setDeadDate(deadDate);
			hySupplierContractService.update(contract);
		    json.setSuccess(true);
		    json.setMsg("更新合同日期成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="updateDeposit")
	public Json updateDeposit(Long contractId, BigDecimal deposit) {
		Json json=new Json();
		try {
			HySupplierContract contract = hySupplierContractService.find(contractId);
			if(contract == null) {
				json.setMsg("合同不存在");
				json.setSuccess(false);
				return json;
			}
			BigDecimal res = deposit.subtract(contract.getDeposit());
			contract.setShouldpayDeposit(contract.getShouldpayDeposit().add(res));
			contract.setReturnDeposit(contract.getReturnDeposit().add(res));
			contract.setDeposit(deposit);		
			hySupplierContractService.update(contract);
		    json.setSuccess(true);
		    json.setMsg("更新合同押金成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
}
