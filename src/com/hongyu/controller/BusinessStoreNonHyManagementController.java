package com.hongyu.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.plugin.StoragePlugin;
import com.grain.plugin.file.FilePlugin;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessStore;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BusinessStoreService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.QrcodeUtil;

@Controller
@RequestMapping("/admin/business/businessStoreNonHyManagement/")
public class BusinessStoreNonHyManagementController {

	@Resource(name = "businessStoreServiceImpl")
	BusinessStoreService businessStoreService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name = "hyRoleServiceImpl")
	HyRoleService hyRoleService;

	@Value("${system.ymymWebSite}")
	private String ymymWebSite;

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, BusinessStore businessStore, HttpSession session, HttpServletRequest request) {
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
			/** 将数据按照微商排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createTime");
			orders.add(order);

			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("creator", hyAdmins);
			filters.add(filter);

			pageable.setFilters(filters);
			pageable.setOrders(orders);

			Page<BusinessStore> page = businessStoreService.findPage(pageable, businessStore);

			for (BusinessStore tmp : page.getRows()) {
				HyAdmin creator = tmp.getCreator();
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("storeName", tmp.getStoreName());
				m.put("address", tmp.getAddress());
				m.put("hyAdmin", tmp.getHyAdmin());
				m.put("state", tmp.getState());
				if (creator.equals(admin)) {
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
			json.setMsg("获取成功");
			json.setObj(hm);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			BusinessStore businessStore = businessStoreService.find(id);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(businessStore);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("modify")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json modify(BusinessStore businessStore, Long roleId, String introducerId, String storeAddress,
			String hyAdminAddress, HyAdmin hyAdmin) {
		Json json = new Json();
		try {

			hyAdmin.setRole(hyRoleService.find(roleId));
			hyAdmin.setAddress(hyAdminAddress);
			hyAdminService.update(hyAdmin, "username", "password", "department", "createDate", "position", "isOnjob",
					"contract", "areaQiche", "areaGuonei", "areaChujing", "isManager", "isLocked", "loginFailureCount",
					"lockedDate", "loginDate", "loginIp", "hyAdmin", "hyAdmins", "hySupplierContract","isEnabled");

			BusinessStore tmpBusinessStore = businessStoreService.find(businessStore.getId());
			WeBusiness weBusiness = tmpBusinessStore.getHeadWebusiness();

			weBusiness.setName(hyAdmin.getName());
			weBusiness.setMobile(hyAdmin.getMobile());
			weBusiness.setWechatAccount(hyAdmin.getWechat());
			weBusiness.setAddress(hyAdminAddress);
			weBusiness.setNameOfStore(businessStore.getStoreName());

			businessStore.setAddress(storeAddress);
			businessStore.setHeadWebusiness(weBusiness);
			businessStoreService.update(businessStore,  "hyAdmin", "creator", "createTime", "deadTime",
					"introducer");
			json.setSuccess(true);
			json.setMsg("更新成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("更新失败: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("add")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json add(BusinessStore businessStore, String storeAddress, String hyAdminAddress, HyAdmin hyAdmin,
			Long roleId, Long introducerId, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin creator = hyAdminService.find(username);
			hyAdmin.setAddress(hyAdminAddress);
			hyAdmin.setRole(hyRoleService.find(roleId));
			hyAdmin.setDepartment(creator.getDepartment());
			hyAdminService.save(hyAdmin);
			WeBusiness introducer = weBusinessService.find(introducerId);

			WeBusiness head = new WeBusiness();
			head.setName(hyAdmin.getName());
			head.setType(1);

			head.setAddress(hyAdmin.getAddress());
			head.setMobile(hyAdmin.getMobile());
			head.setUrl(hyAdmin.getWechat());
			head.setOperator(creator);
			head.setWechatAccount(hyAdmin.getWechat());
			head.setIntroducer(businessStore.getIntroducer());
			head.setIsLineWechatBusiness(false);
			head.setAccount(hyAdmin);
			businessStore.setHeadWebusiness(head);
			businessStore.setIntroducer(introducer);
			businessStore.setAddress(storeAddress);
			businessStore.setHyAdmin(hyAdmin);
			businessStore.setCreator(creator);
			businessStoreService.save(businessStore);
			head.setStoreId(businessStore.getId());
			head.setNameOfStore(businessStore.getStoreName());
			String url = ymymWebSite + "?uid=" + head.getId();
			head.setUrl(url);
			StoragePlugin filePlugin = new FilePlugin();
			String uuid = UUID.randomUUID() + "";
			String location = "/"+Constants.resourcesPath+"/qrcode/" + uuid + ".jpg";
			String tmp = System.getProperty("java.io.tmpdir") + "/upload_" + uuid + ".tmp";
			File file = QrcodeUtil.getQrcode(url, 200, tmp);
			filePlugin.upload(location, file, null);
			head.setQrcodeUrl(location);
			weBusinessService.update(head);
			json.setSuccess(true);
			json.setMsg("添加成功");
			json.setObj(null);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: " + e.getMessage());
			json.setObj(null);
		}
		return json;
	}

	@RequestMapping("getIntroducer")
	@ResponseBody
	public Json getIntroducer(@RequestParam(required=false) String name) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			if(name!=null&&!"".equals(name)){
				filters.add(Filter.like("name", name));
			}
			List<WeBusiness> weBusinesses = weBusinessService.findList(null, filters, null);
			List<Map<String, Object>> ans=new LinkedList<>();
			if(weBusinesses!=null&&weBusinesses.size()>0){
				for(WeBusiness tmp:weBusinesses){
					HashMap<String, Object> m=new HashMap<>();
					m.put("id", tmp.getId());
					m.put("name", tmp.getName());
					ans.add(m);
				}
				
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(ans);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败: " + e.getMessage());
			e.printStackTrace();
		}
		return json;

	}

}
