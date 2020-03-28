package com.hongyu.controller;

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
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.WeBusiness;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BusinessStoreService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.WeBusinessService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants;
import com.hongyu.util.QrcodeUtil;

@Controller
@RequestMapping("/admin/business/webusinessNonHy/")
@Transactional(propagation=Propagation.REQUIRED)
public class WeBusinessNonHyController {
	@Resource(name = "weBusinessServiceImpl")
	WeBusinessService weBusinessService;

	@Resource(name="businessStoreServiceImpl")
	BusinessStoreService businessStoreService;
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;

	@Resource(name = "departmentServiceImpl")
	private DepartmentService departmentService;

	@Value("${system.ymymWebSite}")
	private String ymymWebSite;

//	@RequestMapping("list/view")
//	@ResponseBody
//	public Json list(Pageable pageable, WeBusiness weBusiness, HttpSession session, HttpServletRequest request) {
//		Json json = new Json();
//		try {
//			HashMap<String, Object> hm = new HashMap<String, Object>();
//			List<HashMap<String, Object>> result = new ArrayList<>();
//			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
//			String username=(String)session.getAttribute(CommonAttributes.Principal);
//			HyAdmin admin=hyAdminService.find(username);
//			
//			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
//			
//			for(HyAdmin tmp:hyAdmins){
//				System.out.println(tmp.getName());
//			}
//			
//			/** 将数据按照注册时间排序 */
//			List<Order> orders = new ArrayList<Order>();
//			Order order = Order.desc("registerTime");
//			orders.add(order);
//	
//			/** 数据按照创建人筛选 */
//			List<Filter> filters = new ArrayList<Filter>();
//			Filter filter = Filter.in("operator", hyAdmins);
//			filters.add(filter);
//			Filter filter2=Filter.eq("type", 1);
//			filters.add(filter2);
//	
//			pageable.setFilters(filters);
//			pageable.setOrders(orders);
//	
//			/** 找到分页的微商数据 */
//			Page<WeBusiness> page = weBusinessService.findPage(pageable, weBusiness);
//	
//			for (WeBusiness tmp : page.getRows()) {
//				HyAdmin creator = tmp.getOperator();
//	
//				HashMap<String, Object> m = new HashMap<String, Object>();
//				m.put("id", tmp.getId());
//				m.put("name", tmp.getName());
//				m.put("mobile", tmp.getMobile());
//				m.put("address", tmp.getAddress());
//				m.put("url", tmp.getUrl());
//				m.put("qrcodeUrl", tmp.getQrcodeUrl());
//				m.put("isActive", tmp.getIsActive());
//				/** 当前用户对本条数据的操作权限 */
//				if (creator.equals(admin)) {
//					if (co == CheckedOperation.view) {
//						m.put("privilege", "view");
//					} else {
//						m.put("privilege", "edit");
//					}
//				} else {
//					if (co == CheckedOperation.edit) {
//						m.put("privilege", "edit");
//					} else {
//						m.put("privilege", "view");
//					}
//				}
//				result.add(m);
//			}
//			hm.put("total", page.getTotal());
//			hm.put("pageNumber", page.getPageNumber());
//			hm.put("pageSize", page.getPageSize());
//			hm.put("rows", result);
//			json.setSuccess(true);
//			json.setMsg("查找成功！");
//			json.setObj(hm);
//	
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("查找失败");
//			e.printStackTrace();
//			// TODO: handle exception
//		}
//		return json;
//	}

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, WeBusiness weBusiness, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin=hyAdminService.find(username);
			
			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
			
			for(HyAdmin tmp:hyAdmins){
				System.out.println(tmp.getName());
			}
			
			/** 将数据按照注册时间排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("registerTime");
			orders.add(order);

			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("operator", hyAdmins);
			filters.add(filter);
			Filter filter2=Filter.eq("type", 1);
			filters.add(filter2);

			pageable.setFilters(filters);
			pageable.setOrders(orders);

			/** 找到分页的微商数据 */
			Page<WeBusiness> page = weBusinessService.findPage(pageable, weBusiness);

			for (WeBusiness tmp : page.getRows()) {
				HyAdmin creator = tmp.getOperator();

				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("name", tmp.getName());
				m.put("mobile", tmp.getMobile());
				m.put("address", tmp.getAddress());
				m.put("url", tmp.getUrl());
				m.put("qrcodeUrl", tmp.getQrcodeUrl());
				m.put("isActive", tmp.getIsActive());
				/** 当前用户对本条数据的操作权限 */
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
			json.setMsg("查找成功！");
			json.setObj(hm);

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查找失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("add")
	@ResponseBody
	public Json add(WeBusiness weBusiness,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("account", hyAdmin));
			List<WeBusiness> result=weBusinessService.findList(null,filters,null);
			WeBusiness user=result.get(0);
			
			weBusiness.setStoreId(user.getStoreId());
			weBusiness.setNameOfStore(businessStoreService.find(user.getStoreId()).getStoreName());
			weBusiness.setOperator(hyAdmin);
			weBusiness.setType(WeBusiness.nonhytype);
			weBusiness.setIntroducer(user.getIntroducer());
			weBusinessService.save(weBusiness);
			String url=ymymWebSite+"?uid="+weBusiness.getId();
			weBusiness.setUrl(url);
			StoragePlugin filePlugin=new FilePlugin();
			String uuid=UUID.randomUUID()+"";
			String location = "/"+Constants.resourcesPath+"/qrcode/" + uuid + ".jpg";
			String tmp=System.getProperty("java.io.tmpdir") + "/upload_" + uuid + ".tmp";
			File file=QrcodeUtil.getQrcode(url, 200,tmp);
			filePlugin.upload(location, file, null);
			weBusiness.setQrcodeUrl(location);
			weBusinessService.update(weBusiness);
			json.setSuccess(true);
			json.setMsg("添加成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("modify")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json modify(WeBusiness weBusiness){
		Json json=new Json();
		try {
			WeBusiness tmp=weBusinessService.find(weBusiness.getId());
			weBusiness.setNameOfStore(businessStoreService.find(tmp.getStoreId()).getStoreName());
			weBusinessService.update(weBusiness,"type","storeId"
					,"url","qrcodeUrl","registerTime","deadTime"
					,"isActive","operator","introducer","wechatOpenId"
					,"isLineWechatBusness","lineDivideProportion");
			json.setSuccess(true);
			json.setMsg("编辑成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try {
			WeBusiness weBusiness=weBusinessService.find(id);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(weBusiness);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
