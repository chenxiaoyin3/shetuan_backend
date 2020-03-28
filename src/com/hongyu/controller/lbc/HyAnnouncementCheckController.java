package com.hongyu.controller.lbc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyAnnouncement;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.Store;
import com.hongyu.entity.HyAnnouncement.StoreRange;
import com.hongyu.entity.HyAnnouncement.SupplierRange;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAnnouncementService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.StoreService;
import com.hongyu.util.ArrayHandler;

@Controller
@RequestMapping("/admin/announcement_check/")
public class HyAnnouncementCheckController {
	
	@Resource(name = "departmentServiceImpl")
	private DepartmentService hyDepartmentService;
	
	@Resource(name = "hyDepartmentModelServiceImpl")
	private HyDepartmentModelService hyDepartmentModelService;
	
	@Resource(name = "hyAnnouncementServiceImpl")
	private HyAnnouncementService hyAnnouncementService;
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService HySupplierService;
	
	
	
	/**
	 * 首页查询公告
	 * @param hyAnnouncement
	 * @return Json
	 */
	@RequestMapping(value="homepage/unsql/view", method = RequestMethod.GET)
	@ResponseBody
	public Json homePageView(Integer count, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			List<HashMap<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			
			//拿到登录人所在的部门
			Department department = hyAdmin.getDepartment();
			
			Store store = storeService.findStore(hyAdmin);
			
			//拿到父账号
			while(hyAdmin.getHyAdmin() != null) {
				hyAdmin = hyAdmin.getHyAdmin();
			}
			
			HySupplier hySupplier = null;
			//拿到供应商
			if(hyAdmin.getLiableContracts() != null && hyAdmin.getLiableContracts().size() != 0) {
				Iterator<HySupplierContract> iterator = hyAdmin.getLiableContracts().iterator();
				HySupplierContract hySupplierContract = iterator.next();
				hySupplier = hySupplierContract.getHySupplier();
			}
			
			
			
			//有效
			filters.add(Filter.eq("isValid", 1));
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("createTime"));
			
			//List<HyAnnouncement> hyAnnouncements = null;
			
			
			
			List<HyAnnouncement> hyAnnouncements = hyAnnouncementService.findList(null, filters, orders);
			
			int countNum = 0;
			if(hyAnnouncements.size() > 0){
				for(HyAnnouncement hyAnnouncement : hyAnnouncements){
					HashMap<String,Object> comMap=new HashMap<String,Object>();
					Set<Department> departmentRange = hyAnnouncement.getDepartmentRange();
					Set<Department> companyRange = hyAnnouncement.getCompanyRange();
					StoreRange storeRange = hyAnnouncement.getStoreRange();
					SupplierRange supplierRange = hyAnnouncement.getSupplierRange();
					//如果总公司可见，或者所在的部门在分公司或者部门可见范围内，或者自己是门店且自己的门店类型或者全部门店可见，或者自己是供应商且自己的供应商类型或全部供应商可见
					if(hyAnnouncement.getRange() == 1
							|| (department != null && (departmentRange.contains(department) || companyRange.contains(department) || (department.getIsCompany() == true && hyAnnouncement.getIsAllCompany() == 1) || (department.getIsCompany() == false && hyAnnouncement.getIsAllDepartment() == 1)))
							|| (store != null && store.getStoreType() != null && (storeRange == StoreRange.all || (storeRange == StoreRange.guakaostore && store.getStoreType() == 1) || (storeRange == StoreRange.hongyustore && store.getStoreType() == 0) || (storeRange == StoreRange.zhiyingstore && store.getStoreType() == 2)))
							|| (hySupplier != null && (supplierRange == SupplierRange.all || (hySupplier.getIsInner().equals(false) && supplierRange == SupplierRange.outside) || (hySupplier.getIsInner().equals(true) && supplierRange == SupplierRange.inside)))) {
						
						HyAdmin creator=hyAnnouncement.getOperator();
						//HyGroup hygroup = groupplaceholder.getGroup();

						comMap.put("id", hyAnnouncement.getId());
						if(creator!=null) {
							comMap.put("creatorName", creator.getName());
						}
						comMap.put("createTime", hyAnnouncement.getCreateTime());
						comMap.put("updateTime", hyAnnouncement.getUpdateTime());
//						comMap.put("storeRange", hyAnnouncement.getStoreRange());
//						comMap.put("supplierRange", hyAnnouncement.getSupplierRange());
//						comMap.put("range", hyAnnouncement.getRange());
//						comMap.put("departmentRange", hyAnnouncement.getDepartmentRange());
//						comMap.put("companyRange", hyAnnouncement.getCompanyRange());
						comMap.put("content", hyAnnouncement.getContent());
						comMap.put("name", hyAnnouncement.getName());
						
						
						list.add(comMap);
						
						countNum++;
						
						if(countNum == count) {
							break;
						}
					}
					
					
					
					
				}
			}
			map.put("rows", list);

			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 首页查询公告
	 * @param hyAnnouncement
	 * @return Json
	 */
	@RequestMapping(value="homepage/view", method = RequestMethod.GET)
	@ResponseBody
	public Json homePageViewSQL(Integer count, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			List<Map<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			
			//拿到登录人所在的部门
			Department department = hyAdmin.getDepartment();
			
			Store store = storeService.findStore(hyAdmin);
			
			//拿到父账号
			while(hyAdmin.getHyAdmin() != null) {
				hyAdmin = hyAdmin.getHyAdmin();
			}
			
			HySupplier hySupplier = null;
			//拿到供应商
			if(hyAdmin.getLiableContracts() != null && hyAdmin.getLiableContracts().size() != 0) {
				Iterator<HySupplierContract> iterator = hyAdmin.getLiableContracts().iterator();
				HySupplierContract hySupplierContract = iterator.next();
				hySupplier = hySupplierContract.getHySupplier();
			}
			
			String[] attrs = new String[]{
					"id","creatorName","createTime","updateTime","content","name"
			};
			
			//订单type 2 认购门票
			
			StringBuilder totalSb = new StringBuilder("select count(*)");
			StringBuilder pageSb = new StringBuilder("select hyat.id, hyadmin.name as creator_name, hyat.create_time, hyat.update_time,"
					+ "hyat.content, hyat.name");
			StringBuilder sb = new StringBuilder(" from hy_announcement hyat, hy_admin hyadmin"
					+ " where hyat.operator=hyadmin.username and hyat.is_valid=1");
			
			//范围是总公司的能看到 and 和 or 语句结合 加括号
			sb.append(" and (hyat.head_office_range=1");
			
			if(store != null && store.getStoreType() != null){
				sb.append(" or hyat.store_range=0");
				//挂靠门店
				if(store.getStoreType() == 1) {
					sb.append(" or hyat.store_range=2");
				}
				//虹宇门店
				else if(store.getStoreType() == 0) {
					sb.append(" or hyat.store_range=1");
				}
				//直营门店
				else if(store.getStoreType() == 2) {
					sb.append(" or hyat.store_range=4");
				}
			}
			else if(hySupplier!=null){
				sb.append(" or hyat.supplier_range=0");
				//内部供应商
				if(hySupplier.getIsInner()) {
					sb.append(" or hyat.supplier_range=1");
				}
				else {
					sb.append(" or hyat.supplier_range=2");
				}
			}
			else if(department != null) {
				//所在部门是分公司
				if(department.getIsCompany() == true) {
					//分公司全部可见的 或 公司在hy_announcement_company表里的
					sb.append(" or hyat.is_all_company=1");
					sb.append(" or exists (select * from hy_announcement_company hyatc where hyatc.hy_company_announcement=hyat.id and hyatc.company_range=" + department.getId() + ")");
				}
				//所在部门是部门
				else {
					//部门全部可见的 或 部门在hy_announcement_department表里的
					sb.append(" or hyat.is_all_department=1");
					sb.append(" or exists (select * from hy_announcement_department hyatc where hyatc.hy_department_announcement=hyat.id and hyatc.department_range=" + department.getId() + ")");
				}
			}
			sb.append(")");
			
			
			
//			if(hyAdmins!=null && !hyAdmins.isEmpty()){
//				List<String> adminStrArr = new ArrayList<>();
//				for(HyAdmin hyAdmin:hyAdmins){
//					adminStrArr.add("'"+hyAdmin.getUsername()+"'");
//					
//				}
//				String adminStr = String.join(",",adminStrArr);
//				sb.append(" and o1.operator_id in ("+adminStr+")");
//			}
			

			List totals = hyAnnouncementService.statis(totalSb.append(sb).toString());
			Integer total = ((BigInteger)totals.get(0)).intValue();
			
			sb.append(" order by hyat.create_time desc");
			
			//Integer sqlStart = (pageable.getPage()-1)*pageable.getRows();
			//Integer sqlEnd = pageable.getPage()*pageable.getRows();
			//sb.append(" limit "+sqlStart+","+sqlEnd);
			sb.append(" limit "+ count);
			
			//有效
			//filters.add(Filter.eq("isValid", 1));
//			List<Order> orders = new ArrayList<Order>();
//			orders.add(Order.desc("createTime"));
			
			List<Object[]> objs = hyAnnouncementService.statis(pageSb.append(sb).toString());
			
			for(Object[] obj : objs){
				Map<String, Object> map1 = ArrayHandler.toMap(attrs, obj);
				
				
				list.add(map1);
			}
	
			map.put("rows", list);

			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 首页查询公告
	 * @param hyAnnouncement
	 * @return Json
	 */
	@RequestMapping(value="homepage/more/unsql/view", method = RequestMethod.GET)
	@ResponseBody
	public Json homePageMoreView(Pageable pageable, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			List<Map<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			
			//拿到登录人所在的部门
			Department department = hyAdmin.getDepartment();
			
			Store store = storeService.findStore(hyAdmin);
			
			//拿到父账号
			while(hyAdmin.getHyAdmin() != null) {
				hyAdmin = hyAdmin.getHyAdmin();
			}
			
			HySupplier hySupplier = null;
			//拿到供应商
			if(hyAdmin.getLiableContracts() != null && hyAdmin.getLiableContracts().size() != 0) {
				Iterator<HySupplierContract> iterator = hyAdmin.getLiableContracts().iterator();
				HySupplierContract hySupplierContract = iterator.next();
				hySupplier = hySupplierContract.getHySupplier();
			}
			
			
			
			//有效
			filters.add(Filter.eq("isValid", 1));
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("createTime"));
			
			List<HyAnnouncement> hyAnnouncements = hyAnnouncementService.findList(null, filters, orders);
			
			//int countNum = 0;
			if(hyAnnouncements.size() > 0){
				for(HyAnnouncement hyAnnouncement : hyAnnouncements){
					HashMap<String,Object> comMap=new HashMap<String,Object>();
					Set<Department> departmentRange = hyAnnouncement.getDepartmentRange();
					Set<Department> companyRange = hyAnnouncement.getCompanyRange();
					StoreRange storeRange = hyAnnouncement.getStoreRange();
					SupplierRange supplierRange = hyAnnouncement.getSupplierRange();
					//如果总公司可见，或者所在的部门在分公司或者部门可见范围内，或者自己是门店且自己的门店类型或者全部门店可见，或者自己是供应商且自己的供应商类型或全部供应商可见
					if(hyAnnouncement.getRange() == 1
							|| (department != null && (departmentRange.contains(department) || companyRange.contains(department) || (department.getIsCompany() == true && hyAnnouncement.getIsAllCompany() == 1) || (department.getIsCompany() == false && hyAnnouncement.getIsAllDepartment() == 1)))
							|| (store != null && (storeRange == StoreRange.all || (storeRange == StoreRange.guakaostore && store.getStoreType() == 1) || (storeRange == StoreRange.hongyustore && (store.getStoreType() == 0 || store.getStoreType() == 2)) ))
							|| (hySupplier != null && (supplierRange == SupplierRange.all || (hySupplier.getIsInner().equals(false) && supplierRange == SupplierRange.outside) || (hySupplier.getIsInner().equals(true) && supplierRange == SupplierRange.inside)))) {
						
						HyAdmin creator=hyAnnouncement.getOperator();
						//HyGroup hygroup = groupplaceholder.getGroup();

						comMap.put("id", hyAnnouncement.getId());
						if(creator!=null) {
							comMap.put("creatorName", creator.getName());
						}
						comMap.put("createTime", hyAnnouncement.getCreateTime());
						comMap.put("updateTime", hyAnnouncement.getUpdateTime());
//						comMap.put("storeRange", hyAnnouncement.getStoreRange());
//						comMap.put("supplierRange", hyAnnouncement.getSupplierRange());
//						comMap.put("range", hyAnnouncement.getRange());
//						comMap.put("departmentRange", hyAnnouncement.getDepartmentRange());
//						comMap.put("companyRange", hyAnnouncement.getCompanyRange());
						comMap.put("content", hyAnnouncement.getContent());
						comMap.put("name", hyAnnouncement.getName());
						
						
						list.add(comMap);
						
//						countNum++;
//						
//						if(countNum == count) {
//							break;
//						}
					}
				}
			}
			int page = pageable.getPage();
			int rows = pageable.getRows();
			Page<Map<String, Object>> pages = new Page<>(new ArrayList<Map<String, Object>>(), 0, pageable);
			if (list != null && !list.isEmpty()) {
				pages.setTotal(list.size());
				pages.setRows(list.subList((page - 1) * rows, page * rows > list.size() ? list.size() : page * rows));
			}
			

			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(pages);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		return json;
	}
	
	
	@RequestMapping(value="homepage/more/view", method = RequestMethod.GET)
	@ResponseBody
	public Json homePageMoreViewSQL(Pageable pageable, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			List<Map<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			
			//拿到登录人所在的部门
			Department department = hyAdmin.getDepartment();
			
			Store store = storeService.findStore(hyAdmin);
			
			//拿到父账号
			while(hyAdmin.getHyAdmin() != null) {
				hyAdmin = hyAdmin.getHyAdmin();
			}
			
			HySupplier hySupplier = null;
			//拿到供应商
			if(hyAdmin.getLiableContracts() != null && hyAdmin.getLiableContracts().size() != 0) {
				Iterator<HySupplierContract> iterator = hyAdmin.getLiableContracts().iterator();
				HySupplierContract hySupplierContract = iterator.next();
				hySupplier = hySupplierContract.getHySupplier();
			}
			
			String[] attrs = new String[]{
					"id","creatorName","createTime","updateTime","content","name"
			};
			
			//订单type 2 认购门票
			
			StringBuilder totalSb = new StringBuilder("select count(*)");
			StringBuilder pageSb = new StringBuilder("select hyat.id, hyadmin.name as creator_name, hyat.create_time, hyat.update_time,"
					+ "hyat.content, hyat.name");
			StringBuilder sb = new StringBuilder(" from hy_announcement hyat, hy_admin hyadmin"
					+ " where hyat.operator=hyadmin.username and hyat.is_valid=1");
			
			//范围是总公司的能看到 and 和 or 语句结合 加括号
			sb.append(" and (hyat.head_office_range=1");
			
			if(store != null && store.getStoreType() != null){
				sb.append(" or hyat.store_range=0");
				//挂靠门店
				if(store.getStoreType() == 1) {
					sb.append(" or hyat.store_range=2");
				}
				//虹宇门店
				else if(store.getStoreType() == 0) {
					sb.append(" or hyat.store_range=1");
				}
				//直营门店
				else if(store.getStoreType() == 2) {
					sb.append(" or hyat.store_range=4");
				}
			}
			else if(hySupplier!=null){
				sb.append(" or hyat.supplier_range=0");
				//内部供应商
				if(hySupplier.getIsInner()) {
					sb.append(" or hyat.supplier_range=1");
				}
				else {
					sb.append(" or hyat.supplier_range=2");
				}
			}
			else if(department != null) {
				//所在部门是分公司
				if(department.getIsCompany() == true) {
					//分公司全部可见的 或 公司在hy_announcement_company表里的
					sb.append(" or hyat.is_all_company=1");
					sb.append(" or exists (select * from hy_announcement_company hyatc where hyatc.hy_company_announcement=hyat.id and hyatc.company_range=" + department.getId() + ")");
				}
				//所在部门是部门
				else {
					//部门全部可见的 或 部门在hy_announcement_department表里的
					sb.append(" or hyat.is_all_department=1");
					sb.append(" or exists (select * from hy_announcement_department hyatc where hyatc.hy_department_announcement=hyat.id and hyatc.department_range=" + department.getId() + ")");
				}
			}
			sb.append(")");
			
			
			
//			if(hyAdmins!=null && !hyAdmins.isEmpty()){
//				List<String> adminStrArr = new ArrayList<>();
//				for(HyAdmin hyAdmin:hyAdmins){
//					adminStrArr.add("'"+hyAdmin.getUsername()+"'");
//					
//				}
//				String adminStr = String.join(",",adminStrArr);
//				sb.append(" and o1.operator_id in ("+adminStr+")");
//			}
			

			List totals = hyAnnouncementService.statis(totalSb.append(sb).toString());
			Integer total = ((BigInteger)totals.get(0)).intValue();
			
			sb.append(" order by hyat.create_time desc");
			
			Integer sqlStart = (pageable.getPage()-1)*pageable.getRows();
			Integer sqlEnd = pageable.getPage()*pageable.getRows();
			sb.append(" limit "+sqlStart+","+sqlEnd);
			//sb.append(" limit "+ count);
			
			//有效
			//filters.add(Filter.eq("isValid", 1));
//			List<Order> orders = new ArrayList<Order>();
//			orders.add(Order.desc("createTime"));
			
			List<Object[]> objs = hyAnnouncementService.statis(pageSb.append(sb).toString());
			
			for(Object[] obj : objs){
				Map<String, Object> map1 = ArrayHandler.toMap(attrs, obj);
				
				
				list.add(map1);
			}
	
			map.put("rows", list);
			
			Page<Map<String, Object>> page = new Page<>(list,total,pageable);

			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(page);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 查看公告详情
	 * @param hyAnnouncement
	 * @return Json
	 */
	@RequestMapping(value="detail", method = RequestMethod.GET)
	@ResponseBody
	public Json detail(Long id, HttpSession session) {
		Json j = new Json();
		
		if(id == null){
			j.setSuccess(false);
			j.setMsg("id不能为空");
			return j;
		}
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);
		
		HyAnnouncement hyAnnouncement = hyAnnouncementService.find(id);
		
		HashMap<String,Object> comMap=new HashMap<String,Object>();
		HyAdmin creator=hyAnnouncement.getOperator();
		//HyGroup hygroup = groupplaceholder.getGroup();

		comMap.put("id", hyAnnouncement.getId());
		if(creator!=null) {
			comMap.put("creatorName", creator.getName());
		}
		comMap.put("createTime", hyAnnouncement.getCreateTime());
		comMap.put("updateTime", hyAnnouncement.getUpdateTime());
		comMap.put("storeRange", hyAnnouncement.getStoreRange().ordinal());
		comMap.put("supplierRange", hyAnnouncement.getSupplierRange().ordinal());
		comMap.put("range", hyAnnouncement.getRange());
		comMap.put("departmentRange", hyAnnouncement.getDepartmentRangeIds());
		comMap.put("companyRange", hyAnnouncement.getCompanyRangeIds());
		comMap.put("content", hyAnnouncement.getContent());
		comMap.put("name", hyAnnouncement.getName());
		comMap.put("isAllDepartment", hyAnnouncement.getIsAllDepartment());
		comMap.put("isAllCompany", hyAnnouncement.getIsAllCompany());

		
		try{
			j.setObj(comMap);
			j.setSuccess(true);
			j.setMsg("查看详情成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg("查看失败！");
		}
		return j;
	}
}
