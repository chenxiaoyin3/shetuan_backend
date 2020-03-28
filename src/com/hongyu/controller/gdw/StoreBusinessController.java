package com.hongyu.controller.gdw;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.impl.util.json.HTTP;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.hongyu.entity.Store;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BusinessStoreService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StoreService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.QrcodeUtil;

@Controller
@RequestMapping("/admin/storeBusinessController/")
public class StoreBusinessController {
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name = "businessStoreServiceImpl")
	BusinessStoreService businessStoreService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Value("${system.ymymWebSite}")
    private String ymymWebSite;

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, WeBusiness weBusiness, HttpSession session, HttpServletRequest request) {
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

			/** 将数据按照创建时间排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("registerTime");
			orders.add(order);

			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.in("operator", hyAdmins));

			filters.add(Filter.eq("type", WeBusiness.hytype));

			pageable.setFilters(filters);
			pageable.setOrders(orders);

			/** 找到分页的员工数据 */
			Page<WeBusiness> page = weBusinessService.findPage(pageable, weBusiness);

			/** 遍历当前页员工数据，返回前端需要的数据格式 */
			for (WeBusiness tmp : page.getRows()) {
				HyAdmin creater = tmp.getOperator();

				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("name", tmp.getName());
				m.put("mobile", tmp.getMobile());
				m.put("address", tmp.getAccount());
				m.put("wechatAccount", tmp.getWechatAccount());
				m.put("url", tmp.getUrl());
				m.put("qrcodeUrl", tmp.getQrcodeUrl());
				m.put("registerTime", tmp.getRegisterTime());
				m.put("isActive", tmp.getIsActive());
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
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			WeBusiness weBusiness = weBusinessService.find(id);
			if (weBusiness == null) {
				json.setSuccess(false);
				json.setMsg("微商不存在");
			} else {
				json.setSuccess(true);
				json.setMsg("查找成功");
				json.setObj(weBusiness);
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("edit")
	@ResponseBody
	public Json edit(WeBusiness weBusiness) {
		Json json = new Json();
		try {
			WeBusiness tmp = weBusinessService.find(weBusiness.getId());
			if (tmp != null) {
				if (tmp.getStoreId() != null) {
					BusinessStore businessStore = businessStoreService.find(tmp.getStoreId());
					if (businessStore != null) {
						weBusiness.setNameOfStore(businessStore.getStoreName());
					}
				}
				weBusinessService.update(weBusiness, "storeId", "type", "url", "qrcodeUrl", "registerTime", "deadTime",
						"isActive", "operator", "account", "introducer", "wechatOpenId", "isLineWechatBusiness",
						"lineDivideProportion");
				json.setSuccess(true);
				json.setMsg("编辑成功");
			} else {
				json.setSuccess(false);
				json.setMsg("编辑失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑错误： " + e.getMessage());
			// TODO: handle exception
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("add")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json add(WeBusiness weBusiness, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			List<Filter> filters = new ArrayList<Filter>();
			Department department = hyAdmin.getDepartment();
			filters.add(Filter.eq("department", department));
			List<Store> list = storeService.findList(null, filters, null);
			if (list == null || list.size() == 0) {
				json.setSuccess(false);
				json.setMsg("本人不属于门店员工，无法创建");
			} else {
				Store store = list.get(0);
				weBusiness.setType(WeBusiness.hytype);
				weBusiness.setOperator(hyAdmin);
				weBusiness.setStoreId(store.getId());
				weBusiness.setNameOfStore(store.getStoreName());
				weBusinessService.save(weBusiness);
				String url = ymymWebSite + "?uid=" + weBusiness.getId();
				weBusiness.setUrl(url);
				StoragePlugin filePlugin = new FilePlugin();
				String uuid = UUID.randomUUID() + "";
				String location = "/"+Constants.resourcesPath+"/qrcode/" + uuid + ".jpg";
				String tmp = System.getProperty("java.io.tmpdir") + "/upload_" + uuid + ".tmp";
				File file = QrcodeUtil.getQrcode(url, 200, tmp);
				filePlugin.upload(location, file, null);
				weBusiness.setQrcodeUrl(location);
				weBusinessService.update(weBusiness);
				json.setSuccess(true);
				json.setMsg("创建成功");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("创建失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
