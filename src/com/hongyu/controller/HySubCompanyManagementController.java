package com.hongyu.controller;

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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.entity.BankList;
import com.hongyu.entity.BankList.BankType;
import com.hongyu.entity.BankList.Yinhangleixing;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BankListService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyRoleService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/channel/subCompanyManagement/")

public class HySubCompanyManagementController {
	@Resource(name="hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	@Resource(name = "bankListServiceImpl")
	BankListService  bankListService;
	
	@Resource(name="hyDepartmentModelServiceImpl")
	HyDepartmentModelService hyDepartmentModeService;
	
	@Resource(name="hyRoleServiceImpl")
	HyRoleService hyRoleService;
	
	@RequestMapping(value="list/view", method = RequestMethod.POST)
	@ResponseBody
	public Json listview(Pageable pageable,Integer status,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try{
			HyCompany hyCompany=new HyCompany();
			Map<String,Object> map=new HashMap<String,Object>();
			List<HashMap<String, Object>> list = new ArrayList<>();
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
			
			List<Filter> companyFilter=new ArrayList<Filter>();
			companyFilter.add(Filter.in("creator", hyAdmins));
			companyFilter.add(new Filter("isHead",Operator.eq,false));
			if(status == null)
			{
				pageable.setFilters(companyFilter);
				Page<HyCompany> page=hyCompanyService.findPage(pageable, hyCompany);
				if(page.getTotal()>0){
					for(HyCompany company:page.getRows()){
						HashMap<String,Object> comMap=new HashMap<String,Object>();
						HyAdmin creator=company.getCreator();
						comMap.put("id", company.getID());
						comMap.put("companyName", company.getCompanyName());
						comMap.put("legalPerson", company.getLegalPerson());
						comMap.put("telephone", company.getMobile());
						comMap.put("address", company.getAddress());
						if(company.getCreator()!=null){
							comMap.put("creator", creator.getName());
					    }
						
						/** 当前用户对本条数据的操作权限 */
						if(creator.equals(admin)){
					    	if(co==CheckedOperation.view){
					    		comMap.put("privilege", "view");
					    	}
					    	else{
					    		comMap.put("privilege", "edit");
					    	}
					    }
					    else{
					    	if(co==CheckedOperation.edit){
					    		comMap.put("privilege", "edit");
					    	}
					    	else{
					    		comMap.put("privilege", "view");
					    	}
					    }
						list.add(comMap);
					}
				}
				map.put("rows", list);
			    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
			    map.put("pageSize", Integer.valueOf(pageable.getRows()));
			    map.put("total",Long.valueOf(page.getTotal()));
				json.setMsg("查询成功");
			    json.setSuccess(true);
			    json.setObj(map);	
			}
			else
			{
				List<Filter> filter = new ArrayList<Filter>();
				filter.add(new Filter("status",Operator.eq,status));
				List<Department> departmentList=departmentService.findList(null,filter,null);
				if(departmentList.size()>0){
					companyFilter.add(Filter.in("hyDepartment",departmentList));
					pageable.setFilters(companyFilter);
					Page<HyCompany> page=hyCompanyService.findPage(pageable, hyCompany);
					if(page.getTotal()>0){
						for(HyCompany company:page.getRows()){
							HashMap<String,Object> comMap=new HashMap<String,Object>();
							HyAdmin creator=company.getCreator();
							comMap.put("id", company.getID());
							comMap.put("companyName", company.getCompanyName());
							comMap.put("legalPerson", company.getLegalPerson());
							comMap.put("telephone", company.getMobile());
							comMap.put("address", company.getAddress());
							if(company.getCreator()!=null){
								comMap.put("creator", creator.getName());
						    }
							
							/** 当前用户对本条数据的操作权限 */
							if(creator.equals(admin)){
						    	if(co==CheckedOperation.view){
						    		comMap.put("privilege", "view");
						    	}
						    	else{
						    		comMap.put("privilege", "edit");
						    	}
						    }
						    else{
						    	if(co==CheckedOperation.edit){
						    		comMap.put("privilege", "edit");
						    	}
						    	else{
						    		comMap.put("privilege", "view");
						    	}
						    }
							list.add(comMap);
						}
					}
					map.put("rows", list);
				    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
				    map.put("pageSize", Integer.valueOf(pageable.getRows()));
				    map.put("total",Long.valueOf(page.getTotal()));
					json.setMsg("查询成功");
				    json.setSuccess(true);
				    json.setObj(map);	
				}
				else{
					json.setMsg("查询成功");
				    json.setSuccess(true);
				    json.setObj(new Page<HyCompany>());
				}
			}	
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="detail/view", method = RequestMethod.GET)
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try{
			HyCompany hyCompany=hyCompanyService.find(id);
			HashMap<String, Object> map = new HashMap<>();
		    List<Map<String, Object>> list = new LinkedList<>();
		    List<BankList> bankLists=new ArrayList<BankList>(hyCompany.getBankLists());	 
		    if(bankLists.size()>0) {
		    	for(BankList bankList:bankLists){
			    	Map<String, Object> bankMap = new HashMap<String, Object>();
			    	bankMap.put("bankId", bankList.getId());
			    	bankMap.put("accountName", bankList.getAccountName());
			    	bankMap.put("bankName", bankList.getBankName());
			    	bankMap.put("bankAccount", bankList.getBankAccount());
			    	bankMap.put("bankCode", bankList.getBankCode());  	
			    	list.add(bankMap);
			    }
		    }    
		    map.put("bankList", list);
		    map.put("companyName", hyCompany.getCompanyName());
		    map.put("legalPerson", hyCompany.getLegalPerson());
		    map.put("dividePercent", hyCompany.getDividePercent());
		    map.put("telephone", hyCompany.getMobile());
		    map.put("address", hyCompany.getAddress());
		    map.put("otherAuthority", hyCompany.getOtherAuthority());
		    map.put("companyPic", hyCompany.getCompanyPic());
		    map.put("period", hyCompany.getPeriod());
		    map.put("principalAccount", hyCompany.getPrincipal().getUsername());
		    //20181009新增分公司负责人的姓名和手机号
		    map.put("principalName", hyCompany.getPrincipal().getName());
		    map.put("principalMobile",hyCompany.getPrincipal().getMobile());
		    
		    map.put("department",hyCompany.getPrincipal().getDepartment().getName());
		    map.put("departmentId", hyCompany.getPrincipal().getDepartment().getId());
		    map.put("role", hyCompany.getPrincipal().getRole().getName());
		    map.put("roleId", hyCompany.getPrincipal().getRole().getId());
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	/*新建一个包装类,传参数*/
	static class WrapHyCompany{
		private HyCompany hyCompany;
		private BankList bankList;
		private String pricipalAccount;
		private String pricipalName;
		private String pricipalMobile;
//		private Long departmentId;
		private Long roleId;
		public HyCompany getHyCompany() {
			return hyCompany;
		}
		public void setHyCompany(HyCompany hyCompany) {
			this.hyCompany = hyCompany;
		}
		public BankList getBankList() {
			return bankList;
		}
		public void setBankList(BankList bankList) {
			this.bankList = bankList;
		}
		public String getPricipalAccount() {
			return pricipalAccount;
		}
		public void setPricipalAccount(String pricipalAccount) {
			this.pricipalAccount = pricipalAccount;
		}
		public String getPricipalName() {
			return pricipalName;
		}
		public void setPricipalName(String pricipalName) {
			this.pricipalName = pricipalName;
		}
		public String getPricipalMobile() {
			return pricipalMobile;
		}
		public void setPricipalMobile(String pricipalMobile) {
			this.pricipalMobile = pricipalMobile;
		}
		//		public Long getDepartmentId() {
//			return departmentId;
//		}
//		public void setDepartmentId(Long departmentId) {
//			this.departmentId = departmentId;
//		}
		public Long getRoleId() {
			return roleId;
		}
		public void setRoleId(Long roleId) {
			this.roleId = roleId;
		}
	}
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(@RequestBody WrapHyCompany wrapHyCompany,HttpSession httpSession)
	{
		Json json=new Json();
		try{
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			String pricipalAccount=wrapHyCompany.getPricipalAccount();
			String pricipalName=wrapHyCompany.getPricipalName();
			String pricipalMobile=wrapHyCompany.getPricipalMobile();
			List<HyAdmin> adminList=hyAdminService.findAll();
			List<String> list=new ArrayList<String>();
			for(HyAdmin hyAdminadmin:adminList){
				list.add(hyAdminadmin.getUsername());
			}
			if(list.contains(pricipalAccount)){
				json.setMsg("该账号已存在!");
			    json.setSuccess(false);
			}
			else{
				HyCompany hyCompany=wrapHyCompany.getHyCompany();
				List<HyCompany> companys=hyCompanyService.findAll();
				for(HyCompany tmp:companys) {
					if(tmp.getCompanyName().equals(hyCompany.getCompanyName())) {
						json.setMsg("该分公司名称已存在");
						json.setSuccess(false);
						return json;
					}
				}
				BankList bankList=wrapHyCompany.getBankList();
//				Long departmentId=wrapHyCompany.getDepartmentId();
				Long roleId=wrapHyCompany.getRoleId();
				HyRole role=hyRoleService.find(roleId);
				Department hyDepartment=departmentService.find(1L);
				Department comDepartment=new Department();
				comDepartment.setHyDepartment(hyDepartment);
				HyDepartmentModel departmentModel=hyDepartmentModeService.find("分公司");
				comDepartment.setHyDepartmentModel(departmentModel);
				comDepartment.setName(hyCompany.getCompanyName());
				comDepartment.setIsCompany(true);
				comDepartment.setCreateDate(new Date());
				comDepartment.setCreator(admin);
				departmentService.save(comDepartment);
//				Department department=departmentService.find(departmentId);	
				HyAdmin adAccount=new HyAdmin();
				adAccount.setUsername(pricipalAccount);
				adAccount.setName(pricipalName);
				adAccount.setMobile(pricipalMobile);
				adAccount.setDepartment(comDepartment);
				adAccount.setRole(role);
				adAccount.setCreateDate(new Date());
				adAccount.setIsEnabled(true);
				hyAdminService.save(adAccount);
				hyCompany.setCreator(admin);
				hyCompany.setPrincipal(adAccount);
				hyCompany.setIsHead(false);
				hyCompany.setHyDepartment(comDepartment);
				hyCompanyService.save(hyCompany);
				bankList.setYhlx(Yinhangleixing.fengongsi);
				bankList.setType(BankType.bank);
				bankList.setBankType(false);
				bankList.setBankListStatus(1); //0-删除,1-正常
				bankList.setHyCompany(hyCompany);
				bankListService.save(bankList);
				json.setMsg("添加成功");
			    json.setSuccess(true);
			}	
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	static class WrapCompany{
		private HyCompany hyCompany;
		private BankList bankList;
		private String pricipalAccount;
		private String pricipalName;
		private String pricipalMobile;
//		private Long departmentId;
		private Long roleId;
		private Long id;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public HyCompany getHyCompany() {
			return hyCompany;
		}
		public void setHyCompany(HyCompany hyCompany) {
			this.hyCompany = hyCompany;
		}
		public BankList getBankList() {
			return bankList;
		}
		public void setBankList(BankList bankList) {
			this.bankList = bankList;
		}
		public String getPricipalAccount() {
			return pricipalAccount;
		}
		public void setPricipalAccount(String pricipalAccount) {
			this.pricipalAccount = pricipalAccount;
		}	
        public String getPricipalName() {
			return pricipalName;
		}
		public void setPricipalName(String pricipalName) {
			this.pricipalName = pricipalName;
		}
		public String getPricipalMobile() {
			return pricipalMobile;
		}
		public void setPricipalMobile(String pricipalMobile) {
			this.pricipalMobile = pricipalMobile;
		}
		//		public Long getDepartmentId() {
//			return departmentId;
//		}
//		public void setDepartmentId(Long departmentId) {
//			this.departmentId = departmentId;
//		}
		public Long getRoleId() {
			return roleId;
		}
		public void setRoleId(Long roleId) {
			this.roleId = roleId;
		}
		
	}
	@RequestMapping("edit")
	@ResponseBody
	public Json modify(@RequestBody WrapCompany wrapCompany)
	{
		Json json=new Json();
		try{
			String pricipalAccount=wrapCompany.getPricipalAccount();
			HyAdmin hyAdmin=new HyAdmin();
			HyCompany hyCompany=wrapCompany.getHyCompany();
			Long id=wrapCompany.getId();
			List<HyCompany> companys=hyCompanyService.findAll();
			for(HyCompany tmp:companys) {
				if(tmp.getCompanyName().equals(hyCompany.getCompanyName())&&tmp.getID()!=id) {
					json.setSuccess(false);
					json.setMsg("该分公司名称已存在");
					return json;
				}
			}
			BankList bankList=wrapCompany.getBankList();
//			Long departmentId=wrapCompany.getDepartmentId();
			Long roleId=wrapCompany.getRoleId();
			HyRole role=hyRoleService.find(roleId);
//			Department department=departmentService.find(departmentId);
//			hyAdmin.setDepartment(department);
			hyAdmin.setRole(role);
			hyAdmin.setUsername(pricipalAccount);
			hyAdmin.setName(wrapCompany.getPricipalName());
			hyAdmin.setMobile(wrapCompany.getPricipalMobile());
			hyAdmin.setModifyDate(new Date());
			hyAdminService.update(hyAdmin,"creator","hyAdmin","password","department","isEnabled","position","isOnjob","wechat",
					"wechatUrl","qq","address","isManager","isLocked","loginFailureCount","lockedDate",
					"loginDate","loginIp");
			HyCompany company=hyCompanyService.find(id);
			Department comDepartment=company.getHyDepartment();
			comDepartment.setName(hyCompany.getCompanyName());
			departmentService.update(comDepartment);
			hyCompany.setID(id);
			hyCompany.setPrincipal(hyAdmin);
			hyCompany.setHyDepartment(comDepartment);
			hyCompanyService.update(hyCompany,"isHead","creator","dividePercent");
			bankList.setHyCompany(hyCompany);
			bankListService.update(bankList,"type","bankType","alias","yhlx","bankListStatus");
			json.setMsg("修改成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	
//	@RequestMapping(value="departmentList")
//	@ResponseBody
//	public Json departmentList()
//	{
//		Json json=new Json();
//		try{
//			List<Department> list=departmentService.findAll();
//			List<HashMap<String,Object>> obj=new ArrayList<HashMap<String,Object>>();
//			for(Department department:list)
//			{
//				HashMap<String, Object> map=new HashMap<String, Object>();
//				map.put("departmentId", department.getId());
//				map.put("departmentName", department.getName());
//				obj.add(map);
//			}
//			json.setMsg("列表成功");
//		    json.setSuccess(true);
//		    json.setObj(obj);
//		}
//		catch(Exception e){
//			json.setSuccess(false);
//			json.setMsg(e.getMessage());
//		}
//		return json;
//	}
	
	@RequestMapping(value="roleList")
	@ResponseBody
	public Json roleList(HttpSession httpSession)
	{
		Json json=new Json();
		try{
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			HyRole hyRole=admin.getRole();
			Set<HyRole> hyRoleSet=hyRole.getHyRolesForSubroles();
			List<HyRole> roleList=new ArrayList<HyRole>(hyRoleSet);
			List<HashMap<String,Object>> obj=new ArrayList<HashMap<String,Object>>();
			for(HyRole role:roleList)
			{
				 HashMap<String, Object> map=new HashMap<String, Object>();
				 map.put("id", role.getId());
				 map.put("name", role.getName());
				 obj.add(map);
			}
			json.setMsg("列表成功");
		    json.setSuccess(true);
		    json.setObj(obj);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="cancel")
	@ResponseBody
	public Json cancel(Long id)
	{
		Json json=new Json();
		try{
			HyCompany hyCompany=hyCompanyService.find(id);
			Department department=hyCompany.getHyDepartment();
			department.setStatus(0);
			departmentService.update(department);
			json.setMsg("取消成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@RequestMapping(value="restore")
	@ResponseBody
	public Json restore(Long id)
	{
		Json json=new Json();
		try{
			HyCompany hyCompany=hyCompanyService.find(id);
			Department department=hyCompany.getHyDepartment();
			department.setStatus(1);
			departmentService.update(department);
			json.setMsg("恢复成功");
		    json.setSuccess(true);
		}
		catch(Exception e){
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
