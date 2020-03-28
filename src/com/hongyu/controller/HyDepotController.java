package com.hongyu.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javax.xml.soap.Detail;

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
import com.hongyu.controller.HyDepotController.WrapHyDepot.LabelValue;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyDepot;
import com.hongyu.entity.HyDepotAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepotAdminService;
import com.hongyu.service.HyDepotService;
import com.hongyu.service.HyRoleService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.DepotSNGenerator;

@Controller
@RequestMapping("/admin/business/depot")
public class HyDepotController {
	
	@Resource(name="hyDepotServiceImpl")
	HyDepotService hyDepotService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hyDepotAdminServiceImpl")
	private HyDepotAdminService hyDepotAdminService;
	  
	//添加仓库
	public static class WrapHyDepot{
		public static class LabelValue{
			public String label;
			public String value;
		}
		
		
		private Long id;
		private String code;
		private String name;
		private String address;
		private HyAdmin creator;
		private Date createTime;
		private Boolean isValid;
		
		
		
		private List<LabelValue> manage;
		
		public List<LabelValue> getManage(){
			return manage;
		}
		public void setManage(List<LabelValue> manage){
			this.manage=manage;
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public HyAdmin getCreator() {
			return creator;
		}
		public void setCreator(HyAdmin creator) {
			this.creator = creator;
		}
		public Date getCreateTime() {
			return createTime;
		}
		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}
		public Boolean getIsValid() {
			return isValid;
		}
		public void setIsValid(Boolean isValid) {
			this.isValid = isValid;
		}
	}
	@RequestMapping(value="/add",method=RequestMethod.POST)
	@ResponseBody
	public Json add(@RequestBody WrapHyDepot hyDepot,HttpSession session){
		
	  	/**
		* 获取当前用户
		*/
	  	String username = (String) session.getAttribute(CommonAttributes.Principal);
	  	HyAdmin admin = hyAdminService.find(username);		
	  	
		Json json = new Json();
		try {
			
			String name=hyDepot.getName();
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("name", name));
			List<HyDepot> list = hyDepotService.findList(null, filters, null);
			if(list!=null && !list.isEmpty()){
				json.setMsg("添加失败，仓库名字已存在");
				json.setSuccess(false);
				json.setObj(null);
				return json;
			}
			
			HyDepot depot = new HyDepot();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String nowaday = sdf.format(new Date());
			String code = nowaday + DepotSNGenerator.getDepotSN(); // SN至少为8位,不足补零
			depot.setCode(code);	//设置仓库编号
			depot.setCreateTime(new Date());	//设置创建时间
			depot.setIsValid(true);	//设置是否有效
			depot.setCreator(admin);
			depot.setName(hyDepot.getName());
			depot.setAddress(hyDepot.getAddress());
			hyDepotService.save(depot);	
			
			List<LabelValue> manage = hyDepot.getManage();
			for(LabelValue lb : manage){
				HyDepotAdmin depotAdmin = new HyDepotAdmin();
				depotAdmin.setDepotId(depot.getId());
				depotAdmin.setAdminName(lb.value);
				depotAdmin.setIsValid(true);
				hyDepotAdminService.save(depotAdmin);
			}
			
			json.setMsg("创建成功");
			json.setSuccess(true);
			json.setObj(null);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("创建失败");                                                
			json.setSuccess(false);
			json.setObj(e);
		}
		
		return json;
	}
	
	@RequestMapping(value="/modify",method=RequestMethod.POST)
	@ResponseBody
	public Json modify(@RequestBody WrapHyDepot hyDepot,HttpSession session){
		
	  	/**
		* 获取当前用户
		*/
	  	String username = (String) session.getAttribute(CommonAttributes.Principal);
	  	HyAdmin admin = hyAdminService.find(username);		
	  	
		Json json = new Json();
		try {
			
			HyDepot depot = hyDepotService.find(hyDepot.getId());
			if(depot==null){
				json.setSuccess(false);
				json.setMsg("仓库不存在");
				json.setObj(null);
				return json;
			}
			
			depot.setName(hyDepot.getName());
			depot.setAddress(hyDepot.getAddress());
			
			depot.setIsValid(hyDepot.getIsValid());	//设置是否有效
			hyDepotService.save(depot);	
			
			List<LabelValue> manage = hyDepot.getManage();
			List<Filter> adminFilters = new ArrayList<>();
			adminFilters.add(Filter.eq("depotId", depot.getId()));
			List<HyDepotAdmin> depotAdmins = hyDepotAdminService.findList(null,adminFilters,null);
			List<String> unmodifyAdmins = new ArrayList<>(); 
			
			for(LabelValue lb:manage){
				for(HyDepotAdmin depotAdmin : depotAdmins){
					if(lb.value.equals(depotAdmin.getAdminName())){
						unmodifyAdmins.add(lb.value);
					}
				}
				
			}
			
			Iterator<LabelValue> lblItr = manage.iterator();
			while(lblItr.hasNext()){
				LabelValue lb= lblItr.next();
				if(unmodifyAdmins.contains(lb.value)){
					lblItr.remove();
				}else{
					HyDepotAdmin depotAdmin = new HyDepotAdmin();
					depotAdmin.setDepotId(depot.getId());
					depotAdmin.setAdminName(lb.value);
					depotAdmin.setIsValid(true);
					hyDepotAdminService.save(depotAdmin);
				}
			}
			
			Iterator<HyDepotAdmin> daItr = depotAdmins.iterator();
			while(daItr.hasNext()){
				HyDepotAdmin depotAdmin = daItr.next();
				if(unmodifyAdmins.contains(depotAdmin.getAdminName())){
					depotAdmin.setIsValid(true);
				}else{
					depotAdmin.setIsValid(false);
				}
				hyDepotAdminService.save(depotAdmin);
			}
			
			json.setMsg("修改成功");
			json.setSuccess(true);
			json.setObj(null);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("修改失败");                                                
			json.setSuccess(false);
			json.setObj(e);
		}
		
		return json;
	}
	
	
	//获取仓库分页列表
	@RequestMapping("/page/view")
	@ResponseBody
	public Json page(Pageable pageable,HyDepot depot,HttpSession session,HttpServletRequest request){
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
		pageable.setFilters(filters);
		
		try{
			Page<HyDepot> pages = hyDepotService.findPage(pageable,depot); 
			if(pages.getRows().size()>0){
				for(HyDepot hyDepot:pages.getRows()){
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
			
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("pageSize", Integer.valueOf(pages.getPageSize()));
		    result.put("pageNumber", Integer.valueOf(pages.getPageNumber()));
		    result.put("total", Long.valueOf(pages.getTotal()));
		    result.put("rows", lhm);
		    
		    json.setSuccess(true);
		    json.setMsg("查询成功");
		    json.setObj(result);
		    
		}catch (Exception e) {
			
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//获取仓库列表
	@RequestMapping("/list/view")
	@ResponseBody
	public Json list(HyDepot depot,HttpSession session,HttpServletRequest request){
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
	
	
	
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json = new Json();
		try {
			HyDepot hyDepot = hyDepotService.find(id);
			if(hyDepot==null){
				json.setMsg("该仓库不存在");
				json.setSuccess(false);
			}else{
				
				WrapHyDepot wrapHyDepot = new WrapHyDepot();
				
				wrapHyDepot.setId(hyDepot.getId());
				wrapHyDepot.setCode(hyDepot.getCode());
				wrapHyDepot.setAddress(hyDepot.getAddress());
				wrapHyDepot.setCreateTime(hyDepot.getCreateTime());
				wrapHyDepot.setCreator(hyDepot.getCreator());
				wrapHyDepot.setIsValid(hyDepot.getIsValid());
				wrapHyDepot.setName(hyDepot.getName());
				
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
				wrapHyDepot.setManage(labelValues);
				
				
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(wrapHyDepot);
			}
			
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	@Resource(name="hyRoleServiceImpl")
	private HyRoleService hyRoleService;
	
	@RequestMapping(value="/depot_admin/list/view")
	@ResponseBody
	public Json getList() {
		Json j = new Json();
		try {
			List<Filter> roleFilters = new ArrayList<>();
			roleFilters.add(Filter.like("name", "库管员"));
			List<HyRole> roles = hyRoleService.findList(null,roleFilters,null);
			if(roles==null || roles.isEmpty()){
				throw new Exception("不存在库管员的角色");
			}
			HyRole role = roles.get(0);

			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("role", role));
			List<HyAdmin> list = hyAdminService.findList(null,filters,null);
			
			List<LabelValue> labelValues = new ArrayList<>();
			for(HyAdmin depotAdmin:list){
				LabelValue labelValue = new LabelValue();
				labelValue.label = labelValue.value = depotAdmin.getUsername();
				labelValues.add(labelValue);
			}
			
			
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(labelValues);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		
		return j;
	}
}
