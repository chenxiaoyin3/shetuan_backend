package com.hongyu.controller;

import static org.hamcrest.CoreMatchers.nullValue;

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
@RequestMapping("/admin/business/weBusinessPersonalManagement/")
@Transactional(propagation=Propagation.REQUIRED)
public class WeBusinessPersonalManagementController {

	@Resource(name="weBusinessServiceImpl")
	WeBusinessService weBusinessService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name="businessStoreServiceImpl")
	BusinessStoreService businessStoreService;

	@Value("${system.ymymWebSite}")
	private String ymymWebSite;
	
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,WeBusiness weBusiness,HttpSession session,HttpServletRequest request){
		Json json=new Json();
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
			Set<HyAdmin>hyAdmins=AuthorityUtils.getAdmins(session, request);
			/** 将数据按照微商排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("registerTime");
			orders.add(order);


			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("operator", hyAdmins);
			filters.add(filter);
			Filter filter2=Filter.eq("type", WeBusiness.personaltype);
			filters.add(filter2);
			
//			/**查出所有符合门店名称的门店id*/
//			if(storeName!=null&&!storeName.equals("")){
//				List<Filter> filters2=new ArrayList<>();
//				filters2.add(Filter.like("storeName", storeName));
//				List<BusinessStore> lists=businessStoreService.findList(null,filters2, null);
//				List<Long> longs=new ArrayList<>();
//				for(BusinessStore tmp:lists){
//					longs.add(tmp.getId());
//				}
//				filters.add(Filter.in("storeId",longs));
//			}
			
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<WeBusiness> page=weBusinessService.findPage(pageable,weBusiness);
			/** 遍历当前页员工数据，返回前端需要的数据格式 */
			for(WeBusiness tmp:page.getRows()){
				HyAdmin creater=tmp.getOperator();
				
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("name", tmp.getName());
				m.put("mobile", tmp.getMobile());
				m.put("address", tmp.getAddress());
				m.put("url", tmp.getUrl());
				m.put("originUrl",tmp.getOriginUrl());
				m.put("qrcodeUrl", tmp.getQrcodeUrl());
				m.put("isActive", tmp.getIsActive());
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
			json.setMsg("查找失败");
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json=new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			WeBusiness weBusiness=weBusinessService.find(id);
			hm.put("id",weBusiness.getId());
			hm.put("name",weBusiness.getName());
			hm.put("mobile", weBusiness.getMobile());
			hm.put("nameOfStore", weBusiness.getNameOfStore());
			hm.put("address", weBusiness.getAddress());
			hm.put("isActive",weBusiness.getIsActive());
			hm.put("url", weBusiness.getUrl());
			hm.put("originUrl",weBusiness.getOriginUrl());
			hm.put("qrcodeUrl", weBusiness.getQrcodeUrl());
			hm.put("wechatAccount", weBusiness.getWechatAccount());
			hm.put("wechatOpenId",weBusiness.getWechatOpenId());
			hm.put("shopName",weBusiness.getShopName());
			hm.put("logo", weBusiness.getLogo());
			if(weBusiness.getIntroducer()!=null) {
				hm.put("referrer",weBusiness.getIntroducer().getName());
			}
			else {
				hm.put("referrer","无推荐人");
			}
			json.setSuccess(true);
			json.setMsg("查找成功");
			json.setObj(hm);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查找失败");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("changeStatus")
	@ResponseBody
	public Json changeStatus(Long id){
		Json json=new Json();
		try{
			WeBusiness weBusiness=weBusinessService.find(id);
			weBusiness.setIsActive(weBusiness.getIsActive()?false:true);
			weBusinessService.update(weBusiness);
			json.setSuccess(true);
			json.setMsg("更改成功");
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("更改失败");
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping("modify")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json modify(WeBusiness weBusiness){
		Json json=new Json();
		try{
			weBusinessService.update(weBusiness,"type","storeId","nameOfStore"
					,"url","qrcodeUrl","registerTime","deadTime"
					,"operator","introducer"
					,"wechatOpenId","isLineWechatBusness","lineDivideProportion,logo");
			json.setSuccess(true);
			json.setMsg("编辑成功");
		}catch(Exception e){
			json.setSuccess(false);
			json.setMsg("编辑失败");
			e.printStackTrace();
		}
		return json;
	}
	@RequestMapping("add")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json add(WeBusiness weBusiness,Long introducerId,HttpSession session){
		Json json=new Json();
		try {
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin=hyAdminService.find(username);;
			weBusiness.setOperator(hyAdmin);
			weBusiness.setType(WeBusiness.personaltype);
			WeBusiness introducer=weBusinessService.find(introducerId);
			weBusiness.setIntroducer(introducer);
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
			
			//设置logo和门店名称默认值
			weBusiness.setShopName(Constants.WE_BUSINESS_DEF_STORE_NAME);
			weBusiness.setLogo(Constants.WE_BUSINESS_DEF_LOGO);
			
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
	
	/*
	 * @author LBC
	 * @function get the referrer list
	 */
	
	
	@RequestMapping("referrer")
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
	
	@RequestMapping("/remove")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json remove(Long id) {
		Json json = new Json();
		try {
			WeBusiness weBusiness = weBusinessService.find(id);
			if(weBusiness==null) {
				throw new Exception("微商不存在或已删除");
			}
			
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("introducer", weBusiness));
			
			List<WeBusiness> weBusinesses = weBusinessService.findList(null,filters,null);
			
			for(WeBusiness weBusiness2:weBusinesses) {
				weBusiness2.setIntroducer(null);
				weBusinessService.update(weBusiness2);
			}
			
			weBusinessService.delete(id);
			
			json.setSuccess(true);
			json.setMsg("删除成功");
			json.setObj(null);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("删除失败");
			json.setObj(e);
		
		}
		
		return json;
	}
	
	
}
