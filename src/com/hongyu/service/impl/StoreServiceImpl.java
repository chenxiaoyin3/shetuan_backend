package com.hongyu.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.grain.plugin.StoragePlugin;
import com.grain.plugin.file.FilePlugin;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.controller.HyRoleController.Auth;
import com.hongyu.dao.StoreDao;
import com.hongyu.entity.BankList;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.MendianAuthority;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreApplication;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HyRoleAuthority.CheckedRange;
import com.hongyu.service.BankListService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.MendianAuthorityService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StoreApplicationService;
import com.hongyu.service.StoreService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.util.Constants;
import com.hongyu.util.QrcodeUtil;

@Service("storeServiceImpl")
public class StoreServiceImpl extends BaseServiceImpl<Store, Long> implements StoreService {
	@Resource(name = "storeDaoImpl")
	StoreDao storeDao;

	@Resource(name = "storeApplicationServiceImpl")
	StoreApplicationService storeApplicationService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "bankListServiceImpl")
	BankListService bankListService;

	@Resource(name = "hyRoleServiceImpl")
	HyRoleService hyRoleService;

	@Resource(name = "hyDepartmentModelServiceImpl")
	HyDepartmentModelService hyDepartmentModelService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name = "hyAreaServiceImpl")
	HyAreaService hyAreaService;

	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;
	@Resource(name="storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	@Resource(name = "mendianAuthorityServiceImpl")
	MendianAuthorityService mendianAuthorityService;

	@Value("${system.ymymWebSite}")
	private String ymymWebSite;

	@Resource
	private TaskService taskService;
	@Resource(name = "storeDaoImpl")
	public void setBaseDao(StoreDao dao) {
		super.setBaseDao(dao);
	}
	@Transactional(propagation=Propagation.REQUIRED)
	public Json saveStore(Store store, String storeAddress, String hyAdminAddress, BankList bankList, Long areaId,
			HyAdmin hyAdmin, Long roleId, BigDecimal lineDivideProportion, HttpSession httpSession) {
		String username = (String) httpSession.getAttribute(CommonAttributes.Principal);

		Json json = new Json();
		HyAdmin user;
		Department department;
		StoreApplication storeApplication = new StoreApplication();
		try {
			user = hyAdminService.find(username);
			department = new Department();
			//判断是否非虹宇门店
			if(store.getStoreType()==3) {//如果是非虹宇门店
				List<Filter> filters=new ArrayList<>();
				HyDepartmentModel hyDepartmentModel=hyDepartmentModelService.find("总公司品控中心");
				filters.add(Filter.eq("hyDepartmentModel",hyDepartmentModel));
				List<Department> departments=departmentService.findList(null,filters,null);
				filters.clear();
				Department hyDepartment=departments.get(0); //得到父部门"总公司品控中心"
				department.setHyDepartment(hyDepartment);
				department.setHyDepartmentModel(hyDepartmentModelService.find("非虹宇门店"));
				department.setName(store.getStoreName());
				department.setIsCompany(false);
				department.setCreator(user);
				departmentService.save(department);
			}
			else {//不是非虹宇门店
				Department superdepartment = hyAdminService.find(username).getDepartment();
				department.setHyDepartment(superdepartment);
				//department.setHyDepartmentModel(hyDepartmentModelService.find(Constants.fengongsimendian));
				department.setHyDepartmentModel(hyDepartmentModelService.find(Constants.zhiyingmendian)); //gxz将分公司门店修改为直营门店 20190116
				department.setName(store.getStoreName());
				department.setIsCompany(false);
				department.setCreator(user);
				departmentService.save(department);
			}
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("部门保存失败，请重试");
			e.printStackTrace();
			return json;
		}
		try {
			HyArea hyArea = hyAreaService.find(areaId);
			store.setHyArea(hyArea);
			store.setAddress(storeAddress);
			store.setStoreAdder(user);
			store.setDepartment(department);
			store.setSuoshuDepartment(user.getDepartment());
			//HyRole role=new HyRole();
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("mendianType", store.getStoreType()));
			
			List<MendianAuthority> mendianAuthorities = mendianAuthorityService.findList(null, filters, null);
			HyRole hyRole = new HyRole();
//			hyRole.setName("门店"+store.getStoreName()+"经理");
//			if(store.getStoreType() == 0) {
//				hyRole.setDescription("管理虹宇门店");
//			} else if(store.getStoreType() == 3) {
//				hyRole.setDescription("管理非虹宇门店");
//			}
//			hyRoleService.save(hyRole);
//			Set<Auth> auths = new HashSet<>();
//		
//			CheckedOperation co = CheckedOperation.edit;
//			CheckedRange cr = CheckedRange.department;
//			Long departmentId = store.getDepartment().getId();
//			
//			for(MendianAuthority tmp:mendianAuthorities){
//				auths.add(generateAuth(co, cr, departmentId, tmp.getAuthorityId()));
//			}	
//			hyRoleService.grantResources(role.getId(), auths);
			if(store.getStoreType()==3) {
				hyRole.setName("门店"+store.getStoreName()+"经理");
				hyRole.setDescription("管理非虹宇门店");
				hyRoleService.save(hyRole);
				Set<Auth> auths = new HashSet<>();
			
				CheckedOperation co = CheckedOperation.edit;
				CheckedRange cr = CheckedRange.department;
				Long departmentId = store.getDepartment().getId();
				
				for(MendianAuthority tmp:mendianAuthorities){
					auths.add(generateAuth(co, cr, departmentId, tmp.getAuthorityId()));
				}	
				hyRoleService.grantResources(hyRole.getId(), auths);
			}
			else {
				if(roleId!=null) {
					hyRole = hyRoleService.find(roleId);
				}								
			}
			hyAdmin.setAddress(hyAdminAddress);
			hyAdmin.setRole(hyRole);
			//如果是非虹宇门店,直接将账号激活
			if(store.getStoreType()==3) {
				hyAdmin.setIsEnabled(true);
			}
			hyAdmin.setDepartment(department);
			hyAdminService.save(hyAdmin);
		} catch (Exception e) {
			departmentService.delete(department);
			json.setSuccess(false);
			json.setMsg("添加账户失败，请重试");
			e.printStackTrace();
			return json;
		}
		WeBusiness business;
		try {
			business = new WeBusiness();
			business.setName(hyAdmin.getName());
			business.setType(0);
			business.setAddress(hyAdmin.getAddress());
			business.setStoreId(store.getId());
			business.setNameOfStore(store.getStoreName());
			business.setMobile(hyAdmin.getMobile());
			business.setWechatAccount(hyAdmin.getWechat());
			business.setIsLineWechatBusiness(true);
			business.setOperator(user);
			business.setAccount(hyAdmin);
			business.setLineDivideProportion(lineDivideProportion);
			weBusinessService.save(business);
		} catch (Exception e) {
			departmentService.delete(department);
			hyAdminService.delete(hyAdmin);
			json.setSuccess(false);
			json.setMsg("添加微商失败，请重试");
			e.printStackTrace();
			return json;
		}
		try {
			String url = ymymWebSite + "?uid=" + business.getId();
			business.setUrl(url);
			StoragePlugin filePlugin = new FilePlugin();
			String uuid = UUID.randomUUID() + "";
			String location = "/"+Constants.resourcesPath+"/qrcode/" + uuid + ".jpg";
			String tmp = System.getProperty("java.io.tmpdir") + "/upload_" + uuid + ".tmp";
			File file = QrcodeUtil.getQrcode(url, 200, tmp);
			filePlugin.upload(location, file, null);
			business.setQrcodeUrl(location);
			weBusinessService.update(business);
		} catch (Exception e) {
			departmentService.delete(department);
			hyAdminService.delete(hyAdmin);
			weBusinessService.delete(business);
			json.setSuccess(false);
			json.setMsg("更新微商二维码失败，请重试");
			e.printStackTrace();
			return json;
		}
		try {
			store.setHyAdmin(hyAdmin);
			bankListService.save(bankList);
		} catch (Exception e) {
			departmentService.delete(department);
			hyAdminService.delete(hyAdmin);
			weBusinessService.delete(business);
			json.setSuccess(false);
			json.setMsg("保存银行信息出错，请重试");
			e.printStackTrace();
			return json;
		}
		
		try {
			StoreAccount storeAccount=new StoreAccount();
			storeAccount.setStore(store);
			storeAccountService.save(storeAccount);
			//store.setStoreAccount(storeAccount);
			store.setBankList(bankList);
			if(store.getStoreType()==3) {//非虹宇门店
				store.setStatus(Constants.STORE_JI_HUO); //设置成激活状态
			}else{
				store.setStatus(Constants.STORE_DAI_SHEN_HE);
			}
			this.save(store);
			business.setStoreId(store.getId());
			weBusinessService.update(business);
			
		} catch (Exception e) {
			departmentService.delete(department);
			hyAdminService.delete(hyAdmin);
			weBusinessService.delete(business);
			bankListService.delete(bankList);
			json.setSuccess(false);
			json.setMsg("保存门店信息出错，请重试");
			e.printStackTrace();
			return json;
		}
		
		if(store.getStoreType()!=3) { //不是非虹宇门店
			
			try {
				storeApplication.setStore(store);
				// storeApplication.setDepartmentId(user.getDepartment().getId());
				storeApplication.setOperator(user);
				storeApplication.setType(0);
				storeApplicationService.save(storeApplication);
			} catch (Exception e) {
				departmentService.delete(department);
				hyAdminService.delete(hyAdmin);
				weBusinessService.delete(business);
				bankListService.delete(bankList);
				this.delete(store);
				json.setSuccess(false);
				json.setMsg("保存门店申请信息出错，请重试");
				e.printStackTrace();
				return json;
			}
		}
		json.setSuccess(true);
		json.setMsg("添加成功");
		if(store.getStoreType()!=3) { //不是非虹宇门店
			json.setObj(storeApplication);
		}		
		return json;
	}
	
	private static Auth generateAuth(CheckedOperation co,CheckedRange cr,Long departmentId,Long id){
		Auth auth=new Auth();
		auth.setCo(co);
		auth.setCr(cr);
		Set<Long> departs7 = new HashSet<>();
		departs7.add(departmentId);
		auth.setDepartments(departs7);
		auth.setId(id);
		return auth;
	}
	
	@Override
	public Json editStore(Store store, String storeAddress, String hyAdminAddress, Long storeId, Long areaId,
			Long bankId, Long roleId, BankList bankList, HyAdmin hyAdmin) {
		// TODO Auto-generated method stub
		Json json = new Json();
		try {
			store.setId(storeId);
			bankList.setId(bankId);
			HyArea hyArea = hyAreaService.find(areaId);
			store.setHyArea(hyArea);
			Integer status = this.find(storeId).getStatus();;
			if(status==Constants.STORE_SHEN_HE_WEI_TONG_GUO){
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("store", store));
				List<Order> orders = new ArrayList<>();
				orders.add(Order.desc("id"));
				List<StoreApplication> sas = storeApplicationService.findList(null, filters, orders);
				StoreApplication storeApplication = sas.get(0);
				storeApplication.setApplicationStatus(StoreApplication.init);
				storeApplicationService.update(storeApplication);
				String processInstanceId = storeApplication.getProcessInstanceId();
				Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
				taskService.complete(task.getId());
			}
			if(roleId!=null) {
				hyAdmin.setRole(hyRoleService.find(roleId));
			}
			hyAdmin.setAddress(hyAdminAddress);
			if(store.getStoreType()==3) {//如果是非虹宇门店
				hyAdminService.update(hyAdmin, "username", "password", "department", "isEnabled", "createDate", "position",
						"isOnjob", "wechatUrl", "contract", "areaQiche", "areaGuonei", "areaChujing", "isManager",
						"isLocked", "loginFailureCount", "lockedDate", "loginDate", "loginIp", "hyAdmin", "hyAdmins",
						"hySupplierContract", "role");
			}
			else {
				hyAdminService.update(hyAdmin, "username", "password", "department", "isEnabled", "createDate", "position",
						"isOnjob", "wechatUrl", "contract", "areaQiche", "areaGuonei", "areaChujing", "isManager",
						"isLocked", "loginFailureCount", "lockedDate", "loginDate", "loginIp", "hyAdmin", "hyAdmins",
						"hySupplierContract", "");
			}		
			bankListService.update(bankList,"type","bankType","hyCompany", "alias");
			store.setAddress(storeAddress);
			if(status==Constants.STORE_SHEN_HE_WEI_TONG_GUO){
				store.setStatus(Constants.STORE_DAI_SHEN_HE);
				this.update(store,"storeAccount", "hyAdmin", "bankList", "department", "suoshuDepartment", "storeType",
						"pstatus", "ppayday", "managementFee", "mpayday", "mstatus", "registerDate", "contract",
						"validDate", "storeAdder", "uniqueCode", "businessLicense", "businessCertificate", "credits",
						"deposit", "special", "waibuStoreType", "headWebusiness");
			}else{
				this.update(store,"storeAccount", "hyAdmin", "bankList", "department", "suoshuDepartment", "status", "storeType",
						"pstatus", "ppayday", "managementFee", "mpayday", "mstatus", "registerDate", "contract",
						"validDate", "storeAdder", "uniqueCode", "businessLicense", "businessCertificate", "credits",
						"deposit", "special", "waibuStoreType", "headWebusiness");
			}
			
			json.setSuccess(true);
			json.setMsg("更改成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("更改失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
//	@Override
//	public Json editStore(Store store, String storeAddress, String hyAdminAddress, Long storeId, Long areaId,
//			Long bankId, Long roleId, BankList bankList, HyAdmin hyAdmin) {
//		// TODO Auto-generated method stub
//		Json json = new Json();
//		try {
//			store.setId(storeId);
//			bankList.setId(bankId);
//			HyArea hyArea = hyAreaService.find(areaId);
//			store.setHyArea(hyArea);
//	
//			hyAdmin.setRole(hyRoleService.find(roleId));
//			hyAdmin.setAddress(hyAdminAddress);
//			hyAdminService.update(hyAdmin, "username", "password", "department", "isEnabled", "createDate", "position",
//					"isOnjob", "wechatUrl", "contract", "areaQiche", "areaGuonei", "areaChujing", "isManager",
//					"isLocked", "loginFailureCount", "lockedDate", "loginDate", "loginIp", "hyAdmin", "hyAdmins",
//					"hySupplierContract", "");
//			bankListService.update(bankList,"type","bankType","hyCompany", "alias");
//			store.setAddress(storeAddress);
//			this.update(store,"storeAccount", "hyAdmin", "bankList", "department", "suoshuDepartment", "status", "storeType",
//					"pstatus", "ppayday", "managementFee", "mpayday", "mstatus", "registerDate", "contract",
//					"validDate", "storeAdder", "uniqueCode", "businessLicense", "businessCertificate", "credits",
//					"deposit", "special", "waibuStoreType", "headWebusiness");
//			json.setSuccess(true);
//			json.setMsg("更改成功");
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("更改失败");
//			e.printStackTrace();
//			// TODO: handle exception
//		}
//		return json;
//	}
	@Override
	public Store findStore(HyAdmin hyAdmin) {
		Department department = hyAdmin.getDepartment();
		List<Filter> filters = new LinkedList<>();
		filters.add(Filter.eq("department", department));
		List<Store> stores = this.findList(null, filters, null);
		if (stores != null && stores.size() > 0) {
			return stores.get(0);
		} else {
			return null;
		}
	}
}
