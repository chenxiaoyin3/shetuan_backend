package com.hongyu.controller.gdw;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.plugin.StoragePlugin;
import com.grain.plugin.file.FilePlugin;
import com.grain.util.FilterUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BankList;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreApplication;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BankListService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.StoreApplicationService;
import com.hongyu.service.StoreService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.QrcodeUtil;

import oracle.net.aso.a;
import oracle.net.aso.r;

@Controller
@RequestMapping("/admin/chainDevelopment/store/")
@Transactional(propagation = Propagation.REQUIRED)
public class StoreController {
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

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

	@RequestMapping(value = "addStore")
	@ResponseBody
	public Json addStore(Store store, String storeAddress, String hyAdminAddress, BankList bankList, Long areaId,
			HyAdmin hyAdmin, Long roleId, BigDecimal lineDivideProportion, HttpSession httpSession) {
		Json json = new Json();
		try {
			String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
			// write by lbc
			//添加store名称若重复提醒前台名称不能重复功能
			String storeName = store.getStoreName();
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("storeName", storeName));
			
			List<Store> stores = storeService.findList(null, filters, null);
			if(stores.size() != 0) {
				json.setSuccess(false);
				json.setMsg("存在相同名称的门店，请更换门店名");
				return json;
			}
			
			
			Json json2 = storeService.saveStore(store, hyAdminAddress, hyAdminAddress, bankList, areaId, hyAdmin,
					roleId, lineDivideProportion, httpSession);			
			
			//如果不是非虹宇门店，则进入审核流程
			if(store.getStoreType()!=3) {
				StoreApplication storeApplication = (StoreApplication) json2.getObj();
				if (json2.isSuccess() == false) {
					json.setSuccess(false);
					json.setMsg(json2.getMsg());
					return json;
				}
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put("inputUser", username);
				// 启动流程
				ProcessInstance pi = runtimeService.startProcessInstanceByKey("storeRegistration", variables);
				// 根据流程实例Id查询任务
				Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
				// 完成 连锁发展员工注册门店任务

				Authentication.setAuthenticatedUserId(username);
				taskService.addComment(task.getId(), pi.getProcessInstanceId(), " :1");
				taskService.complete(task.getId());
				
				storeApplication.setProcessInstanceId(pi.getProcessInstanceId());
				storeApplicationService.update(storeApplication);
			}		
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("新增失败");
		}

		return json;
	}

	@RequestMapping(value = "getRoles/view")
	@ResponseBody
	public Json getRoles(HttpSession session) {
		Json j = new Json();

		try {
			/**
			 * 获取当前用户角色
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyRole role = hyAdminService.find(username).getRole();

			/** 获取子角色 */
			Set<HyRole> subRoles = role.getHyRolesForSubroles();
			if (subRoles.size() > 0) {
				Iterator<HyRole> iterator = subRoles.iterator();
				while (iterator.hasNext()) {
					HyRole subRole = iterator.next();
					if (!subRole.getStatus())
						iterator.remove();
				}
			}
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(subRoles);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}

	@RequestMapping("getStores/view")
	@ResponseBody
	public Json getStores(Pageable pageable, Store store, HyAdmin hyAdmin, HttpServletRequest request,
			HttpSession session) {
		Json json = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			/**
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
			/** 将数据按照门店注册时间排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("registerDate");
			orders.add(order);
			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("storeAdder", hyAdmins);
			filters.add(filter);
			filters.add(Filter.ne("status",10));

			pageable.setFilters(filters);
			pageable.setOrders(orders);

			/** 找到分页的门店数据 */
			Page<Store> page = storeService.findPage(pageable, store);

			for (Store tmp : page.getRows()) {
				HyAdmin creater = tmp.getStoreAdder();
				HashMap<String, Object> m = new HashMap<String, Object>();
				//add by cqx 20190705
				String tempTreePath=tmp.getHyArea().getTreePath();//找区域路径
				tempTreePath= tempTreePath.substring(1, tempTreePath.length());//把第一个字符去除
				String[] areaID=tempTreePath.split(",");//解析
				String attachedArea="";
				for(String id:areaID) {
					HyArea hyArea=hyAreaService.find(Long.parseLong(id));
					attachedArea=attachedArea.concat(hyArea.getName());
				}
				attachedArea.concat(tmp.getHyArea().getName());
				//end by cqx 20190705
				m.put("id", tmp.getId());
				m.put("storeName", tmp.getStoreName());
				m.put("hyAdmin", tmp.getHyAdmin());
				m.put("area", attachedArea);
				m.put("storeAdder", tmp.getStoreAdder());
				m.put("registerDate", tmp.getRegisterDate());
				m.put("validDate", tmp.getValidDate());
				m.put("status", tmp.getStatus());
				//添加storetype
				m.put("storeType", tmp.getStoreType());

				/** 当前用户对本条数据的操作权限 */
				if (creater.equals(admin)) {
					if (co == CheckedOperation.view) {
						m.put("privilege", "view");
					} else {
						m.put("privilege", "edit");
					}
				} else {
					if (co == CheckedOperation.edit) {
						m.put("privilege", "edit");
					} else {
						m.put("privilege", "view");
					}
				}
				result.add(m);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("rows", result);
			json.setSuccess(true);
			json.setMsg("查找成功！");
			json.setObj(hm);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查找失败");
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping(value = "editStore")
	@ResponseBody
	public Json editStore(Store store, String storeAddress, String hyAdminAddress, Long storeId, Long areaId,
			Long bankId, Long roleId, BankList bankList, HyAdmin hyAdmin) {
		Json json = new Json();
		try {
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
//			bankListService.update(bankList, "hyCompany", "alias");
//			store.setAddress(storeAddress);
//			storeService.update(store, "hyAdmin", "bankList", "department", "suoshuDepartment", "status", "storeType",
//					"pstatus", "ppayday", "managementFee", "mpayday", "mstatus", "registerDate", "contract",
//					"validDate", "storeAdder", "uniqueCode", "businessLicense", "businessCertificate", "credits",
//					"deposit", "special", "waibuStoreType", "headWebusiness");
//			json.setSuccess(true);
//			json.setMsg("更改成功");
			json=storeService.editStore(store, storeAddress, hyAdminAddress, storeId, areaId, bankId, roleId, bankList, hyAdmin);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("更改失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("getStore/view")
	@ResponseBody
	public Json getStore(Long id) {
		Json json = new Json();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Store store = storeService.find(id);
			map.put("id", store.getId());
			map.put("storeName", store.getStoreName());
			map.put("suoshuDepartment", store.getSuoshuDepartment());
			map.put("address", store.getAddress());
			map.put("storeType", store.getStoreType());
			HyArea hyArea = store.getHyArea();
			map.put("areaId", hyArea.getId());
			map.put("ids", hyArea.getTreePaths());
			map.put("fullName", hyArea.getFullName());
			map.put("pledge", store.getPledge());
			map.put("bankList", store.getBankList());
			map.put("hyAdmin", store.getHyAdmin());
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("store", store));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			List<StoreApplication> storeApplications = storeApplicationService.findList(null, filters, orders);
			if(storeApplications==null||storeApplications.size()==0){
				map.put("comment","");
			}else{
				map.put("comment", storeApplications.get(0).getComment());
			}
			json.setSuccess(true);
			json.setMsg("查找成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(true);
			json.setMsg("查找失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("getDepartment/view")
	@ResponseBody
	public Json getDepartment(HttpSession httpSession) {
		String username = (String) httpSession.getAttribute(CommonAttributes.Principal);
		Json json = new Json();
		try {
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department = hyAdmin.getDepartment();
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(department);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;

	}

	@RequestMapping(value = "areacomboxlist/view")
	@ResponseBody
	public Json getSubAreas(Long id) {
		Json j = new Json();
		try {
			HashMap<String, Object> hashMap = new HashMap<>();
			HyArea parent = hyAreaService.find(id);
			List<HashMap<String, Object>> obj = new ArrayList<>();
			if (parent != null && parent.getHyAreas().size() > 0) {
				for (HyArea child : parent.getHyAreas()) {
					if (child.getStatus()) {
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return j;
	}

}
