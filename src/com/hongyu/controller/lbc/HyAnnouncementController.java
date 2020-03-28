package com.hongyu.controller.lbc;

import static com.hongyu.util.Constants.fengongsi;
import static com.hongyu.util.Constants.zonggongsi;

import java.util.Date;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Order.Direction;
import com.hongyu.entity.Department;
import com.hongyu.entity.GroupPlaceholder;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyAnnouncement;
import com.hongyu.entity.HyAnnouncement.StoreRange;
import com.hongyu.entity.HyAnnouncement.SupplierRange;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HyDepartmentModel;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.Store;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAnnouncementService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.StoreService;
import com.hongyu.util.ArrayHandler;
import com.hongyu.util.BeanUtils;


@Controller
@RequestMapping("/admin/announcement/")
public class HyAnnouncementController {

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
	
	
	static class AnnouncementWrap{
		
		private Long id;
		
		/** 公告名称 */
		private String name;
		
		/** 内容*/
		private String content;
		
		/** 门店可见范围*/
		/** 0所有门店；1虹宇门店（包括直营门店）；2挂靠门店**/
		private Integer storeRange;
		
		/** 供应商可见范围*/
		/** 0所有供应商；1内部供应商；2外部供应商**/
		private Integer supplierRange;
		
		/** 总公司可见范围*/
		/** 0不可见 1可见**/
		private Integer range;
		
		
		/** 选择的部门 */
		/** 0所有部门**/
		private String departmentRange;
		
		/** 选择的公司 */
		/** 0所有分公司**/
		private String companyRange;


		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public Integer getStoreRange() {
			return storeRange;
		}

		public void setStoreRange(Integer storeRange) {
			this.storeRange = storeRange;
		}

		public Integer getSupplierRange() {
			return supplierRange;
		}

		public void setSupplierRange(Integer supplierRange) {
			this.supplierRange = supplierRange;
		}

		public Integer getRange() {
			return range;
		}

		public void setRange(Integer range) {
			this.range = range;
		}

		public String getDepartmentRange() {
			return departmentRange;
		}

		public void setDepartmentRange(String departmentRange) {
			this.departmentRange = departmentRange;
		}

		public String getCompanyRange() {
			return companyRange;
		}

		public void setCompanyRange(String companyRange) {
			this.companyRange = companyRange;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
		
		
	}
	
	
	/**
	 * 发布公告者查询公告
	 * @param hyAnnouncement
	 * @return Json
	 */
	@RequestMapping(value="creator/view", method = RequestMethod.GET)
	@ResponseBody
	public Json creatorView(Pageable pageable, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			List<HashMap<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			
			filters.add(Filter.eq("operator", hyAdmin));
			
			//有效
			filters.add(Filter.eq("isValid", 1));
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
			Page<HyAnnouncement> page = hyAnnouncementService.findPage(pageable);
			
			if(page.getTotal()>0){
				for(HyAnnouncement hyAnnouncement : page.getRows()){
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
					comMap.put("departmentRange", hyAnnouncement.getDepartmentRange());
					comMap.put("companyRange", hyAnnouncement.getCompanyRange());
					comMap.put("content", hyAnnouncement.getContent());
					comMap.put("name", hyAnnouncement.getName());
					comMap.put("isAllDepartment", hyAnnouncement.getIsAllDepartment());
					comMap.put("isAllCompany", hyAnnouncement.getIsAllCompany());
					
					
					list.add(comMap);
				}
			}
			map.put("rows", list);
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));
		    map.put("total",Long.valueOf(page.getTotal()));
			
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
	
//	/**
//	 * 首页查询公告
//	 * @param hyAnnouncement
//	 * @return Json
//	 */
//	@RequestMapping(value="homepage/unsql/view", method = RequestMethod.GET)
//	@ResponseBody
//	public Json homePageView(Integer count, HttpSession session) {
//		Json json = new Json();
//		try {
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin hyAdmin = hyAdminService.find(username);
//			List<HashMap<String, Object>> list = new ArrayList<>();
//			Map<String,Object> map=new HashMap<String,Object>();
//			List<Filter> filters = new ArrayList<Filter>();
//			
//			//拿到登录人所在的部门
//			Department department = hyAdmin.getDepartment();
//			
//			Store store = storeService.findStore(hyAdmin);
//			
//			//拿到父账号
//			while(hyAdmin.getHyAdmin() != null) {
//				hyAdmin = hyAdmin.getHyAdmin();
//			}
//			
//			HySupplier hySupplier = null;
//			//拿到供应商
//			if(hyAdmin.getLiableContracts() != null && hyAdmin.getLiableContracts().size() != 0) {
//				Iterator<HySupplierContract> iterator = hyAdmin.getLiableContracts().iterator();
//				HySupplierContract hySupplierContract = iterator.next();
//				hySupplier = hySupplierContract.getHySupplier();
//			}
//			
//			
//			
//			//有效
//			filters.add(Filter.eq("isValid", 1));
//			List<Order> orders = new ArrayList<Order>();
//			orders.add(Order.desc("createTime"));
//			
//			//List<HyAnnouncement> hyAnnouncements = null;
//			
//			
//			
//			List<HyAnnouncement> hyAnnouncements = hyAnnouncementService.findList(null, filters, orders);
//			
//			int countNum = 0;
//			if(hyAnnouncements.size() > 0){
//				for(HyAnnouncement hyAnnouncement : hyAnnouncements){
//					HashMap<String,Object> comMap=new HashMap<String,Object>();
//					Set<Department> departmentRange = hyAnnouncement.getDepartmentRange();
//					Set<Department> companyRange = hyAnnouncement.getCompanyRange();
//					StoreRange storeRange = hyAnnouncement.getStoreRange();
//					SupplierRange supplierRange = hyAnnouncement.getSupplierRange();
//					//如果总公司可见，或者所在的部门在分公司或者部门可见范围内，或者自己是门店且自己的门店类型或者全部门店可见，或者自己是供应商且自己的供应商类型或全部供应商可见
//					if(hyAnnouncement.getRange() == 1
//							|| (department != null && (departmentRange.contains(department) || companyRange.contains(department) || (department.getIsCompany() == true && hyAnnouncement.getIsAllCompany() == 1) || (department.getIsCompany() == false && hyAnnouncement.getIsAllDepartment() == 1)))
//							|| (store != null && store.getStoreType() != null && (storeRange == StoreRange.all || (storeRange == StoreRange.guakaostore && store.getStoreType() == 1) || (storeRange == StoreRange.hongyustore && store.getStoreType() == 0) || (storeRange == StoreRange.zhiyingstore && store.getStoreType() == 2)))
//							|| (hySupplier != null && (supplierRange == SupplierRange.all || (hySupplier.getIsInner().equals(false) && supplierRange == SupplierRange.outside) || (hySupplier.getIsInner().equals(true) && supplierRange == SupplierRange.inside)))) {
//						
//						HyAdmin creator=hyAnnouncement.getOperator();
//						//HyGroup hygroup = groupplaceholder.getGroup();
//
//						comMap.put("id", hyAnnouncement.getId());
//						if(creator!=null) {
//							comMap.put("creatorName", creator.getName());
//						}
//						comMap.put("createTime", hyAnnouncement.getCreateTime());
//						comMap.put("updateTime", hyAnnouncement.getUpdateTime());
////						comMap.put("storeRange", hyAnnouncement.getStoreRange());
////						comMap.put("supplierRange", hyAnnouncement.getSupplierRange());
////						comMap.put("range", hyAnnouncement.getRange());
////						comMap.put("departmentRange", hyAnnouncement.getDepartmentRange());
////						comMap.put("companyRange", hyAnnouncement.getCompanyRange());
//						comMap.put("content", hyAnnouncement.getContent());
//						comMap.put("name", hyAnnouncement.getName());
//						
//						
//						list.add(comMap);
//						
//						countNum++;
//						
//						if(countNum == count) {
//							break;
//						}
//					}
//					
//					
//					
//					
//				}
//			}
//			map.put("rows", list);
//
//			
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(map);
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("查询失败");
//			json.setObj(null);
//			e.printStackTrace();
//		}
//		
//		return json;
//	}
//	
//	/**
//	 * 首页查询公告
//	 * @param hyAnnouncement
//	 * @return Json
//	 */
//	@RequestMapping(value="homepage/view", method = RequestMethod.GET)
//	@ResponseBody
//	public Json homePageViewSQL(Integer count, HttpSession session) {
//		Json json = new Json();
//		try {
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin hyAdmin = hyAdminService.find(username);
//			List<Map<String, Object>> list = new ArrayList<>();
//			Map<String,Object> map=new HashMap<String,Object>();
//			List<Filter> filters = new ArrayList<Filter>();
//			
//			//拿到登录人所在的部门
//			Department department = hyAdmin.getDepartment();
//			
//			Store store = storeService.findStore(hyAdmin);
//			
//			//拿到父账号
//			while(hyAdmin.getHyAdmin() != null) {
//				hyAdmin = hyAdmin.getHyAdmin();
//			}
//			
//			HySupplier hySupplier = null;
//			//拿到供应商
//			if(hyAdmin.getLiableContracts() != null && hyAdmin.getLiableContracts().size() != 0) {
//				Iterator<HySupplierContract> iterator = hyAdmin.getLiableContracts().iterator();
//				HySupplierContract hySupplierContract = iterator.next();
//				hySupplier = hySupplierContract.getHySupplier();
//			}
//			
//			String[] attrs = new String[]{
//					"id","creatorName","createTime","updateTime","content","name"
//			};
//			
//			//订单type 2 认购门票
//			
//			StringBuilder totalSb = new StringBuilder("select count(*)");
//			StringBuilder pageSb = new StringBuilder("select hyat.id, hyadmin.name as creator_name, hyat.create_time, hyat.update_time,"
//					+ "hyat.content, hyat.name");
//			StringBuilder sb = new StringBuilder(" from hy_announcement hyat, hy_admin hyadmin"
//					+ " where hyat.operator=hyadmin.username and hyat.is_valid=1");
//			
//			//范围是总公司的能看到 and 和 or 语句结合 加括号
//			sb.append(" and (hyat.head_office_range=1");
//			
//			if(store != null && store.getStoreType() != null){
//				sb.append(" or hyat.store_range=0");
//				//挂靠门店
//				if(store.getStoreType() == 1) {
//					sb.append(" or hyat.store_range=2");
//				}
//				//虹宇门店
//				else if(store.getStoreType() == 0) {
//					sb.append(" or hyat.store_range=1");
//				}
//				//直营门店
//				else if(store.getStoreType() == 2) {
//					sb.append(" or hyat.store_range=4");
//				}
//			}
//			else if(hySupplier!=null){
//				sb.append(" or hyat.supplier_range=0");
//				//内部供应商
//				if(hySupplier.getIsInner()) {
//					sb.append(" or hyat.supplier_range=1");
//				}
//				else {
//					sb.append(" or hyat.supplier_range=2");
//				}
//			}
//			else if(department != null) {
//				//所在部门是分公司
//				if(department.getIsCompany() == true) {
//					//分公司全部可见的 或 公司在hy_announcement_company表里的
//					sb.append(" or hyat.is_all_company=1");
//					sb.append(" or exists (select * from hy_announcement_company hyatc where hyatc.hy_company_announcement=hyat.id and hyatc.company_range=" + department.getId() + ")");
//				}
//				//所在部门是部门
//				else {
//					//部门全部可见的 或 部门在hy_announcement_department表里的
//					sb.append(" or hyat.is_all_department=1");
//					sb.append(" or exists (select * from hy_announcement_department hyatc where hyatc.hy_department_announcement=hyat.id and hyatc.department_range=" + department.getId() + ")");
//				}
//			}
//			sb.append(")");
//			
//			
//			
////			if(hyAdmins!=null && !hyAdmins.isEmpty()){
////				List<String> adminStrArr = new ArrayList<>();
////				for(HyAdmin hyAdmin:hyAdmins){
////					adminStrArr.add("'"+hyAdmin.getUsername()+"'");
////					
////				}
////				String adminStr = String.join(",",adminStrArr);
////				sb.append(" and o1.operator_id in ("+adminStr+")");
////			}
//			
//
//			List totals = hyAnnouncementService.statis(totalSb.append(sb).toString());
//			Integer total = ((BigInteger)totals.get(0)).intValue();
//			
//			sb.append(" order by hyat.create_time desc");
//			
//			//Integer sqlStart = (pageable.getPage()-1)*pageable.getRows();
//			//Integer sqlEnd = pageable.getPage()*pageable.getRows();
//			//sb.append(" limit "+sqlStart+","+sqlEnd);
//			sb.append(" limit "+ count);
//			
//			//有效
//			//filters.add(Filter.eq("isValid", 1));
////			List<Order> orders = new ArrayList<Order>();
////			orders.add(Order.desc("createTime"));
//			
//			List<Object[]> objs = hyAnnouncementService.statis(pageSb.append(sb).toString());
//			
//			for(Object[] obj : objs){
//				Map<String, Object> map1 = ArrayHandler.toMap(attrs, obj);
//				
//				
//				list.add(map1);
//			}
//	
//			map.put("rows", list);
//
//			
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(map);
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("查询失败");
//			json.setObj(null);
//			e.printStackTrace();
//		}
//		
//		return json;
//	}
//	
//	/**
//	 * 首页查询公告
//	 * @param hyAnnouncement
//	 * @return Json
//	 */
//	@RequestMapping(value="homepage/more/unsql/view", method = RequestMethod.GET)
//	@ResponseBody
//	public Json homePageMoreView(Pageable pageable, HttpSession session) {
//		Json json = new Json();
//		try {
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin hyAdmin = hyAdminService.find(username);
//			List<Map<String, Object>> list = new ArrayList<>();
//			Map<String,Object> map=new HashMap<String,Object>();
//			List<Filter> filters = new ArrayList<Filter>();
//			
//			//拿到登录人所在的部门
//			Department department = hyAdmin.getDepartment();
//			
//			Store store = storeService.findStore(hyAdmin);
//			
//			//拿到父账号
//			while(hyAdmin.getHyAdmin() != null) {
//				hyAdmin = hyAdmin.getHyAdmin();
//			}
//			
//			HySupplier hySupplier = null;
//			//拿到供应商
//			if(hyAdmin.getLiableContracts() != null && hyAdmin.getLiableContracts().size() != 0) {
//				Iterator<HySupplierContract> iterator = hyAdmin.getLiableContracts().iterator();
//				HySupplierContract hySupplierContract = iterator.next();
//				hySupplier = hySupplierContract.getHySupplier();
//			}
//			
//			
//			
//			//有效
//			filters.add(Filter.eq("isValid", 1));
//			List<Order> orders = new ArrayList<Order>();
//			orders.add(Order.desc("createTime"));
//			
//			List<HyAnnouncement> hyAnnouncements = hyAnnouncementService.findList(null, filters, orders);
//			
//			//int countNum = 0;
//			if(hyAnnouncements.size() > 0){
//				for(HyAnnouncement hyAnnouncement : hyAnnouncements){
//					HashMap<String,Object> comMap=new HashMap<String,Object>();
//					Set<Department> departmentRange = hyAnnouncement.getDepartmentRange();
//					Set<Department> companyRange = hyAnnouncement.getCompanyRange();
//					StoreRange storeRange = hyAnnouncement.getStoreRange();
//					SupplierRange supplierRange = hyAnnouncement.getSupplierRange();
//					//如果总公司可见，或者所在的部门在分公司或者部门可见范围内，或者自己是门店且自己的门店类型或者全部门店可见，或者自己是供应商且自己的供应商类型或全部供应商可见
//					if(hyAnnouncement.getRange() == 1
//							|| (department != null && (departmentRange.contains(department) || companyRange.contains(department) || (department.getIsCompany() == true && hyAnnouncement.getIsAllCompany() == 1) || (department.getIsCompany() == false && hyAnnouncement.getIsAllDepartment() == 1)))
//							|| (store != null && (storeRange == StoreRange.all || (storeRange == StoreRange.guakaostore && store.getStoreType() == 1) || (storeRange == StoreRange.hongyustore && (store.getStoreType() == 0 || store.getStoreType() == 2)) ))
//							|| (hySupplier != null && (supplierRange == SupplierRange.all || (hySupplier.getIsInner().equals(false) && supplierRange == SupplierRange.outside) || (hySupplier.getIsInner().equals(true) && supplierRange == SupplierRange.inside)))) {
//						
//						HyAdmin creator=hyAnnouncement.getOperator();
//						//HyGroup hygroup = groupplaceholder.getGroup();
//
//						comMap.put("id", hyAnnouncement.getId());
//						if(creator!=null) {
//							comMap.put("creatorName", creator.getName());
//						}
//						comMap.put("createTime", hyAnnouncement.getCreateTime());
//						comMap.put("updateTime", hyAnnouncement.getUpdateTime());
////						comMap.put("storeRange", hyAnnouncement.getStoreRange());
////						comMap.put("supplierRange", hyAnnouncement.getSupplierRange());
////						comMap.put("range", hyAnnouncement.getRange());
////						comMap.put("departmentRange", hyAnnouncement.getDepartmentRange());
////						comMap.put("companyRange", hyAnnouncement.getCompanyRange());
//						comMap.put("content", hyAnnouncement.getContent());
//						comMap.put("name", hyAnnouncement.getName());
//						
//						
//						list.add(comMap);
//						
////						countNum++;
////						
////						if(countNum == count) {
////							break;
////						}
//					}
//				}
//			}
//			int page = pageable.getPage();
//			int rows = pageable.getRows();
//			Page<Map<String, Object>> pages = new Page<>(new ArrayList<Map<String, Object>>(), 0, pageable);
//			if (list != null && !list.isEmpty()) {
//				pages.setTotal(list.size());
//				pages.setRows(list.subList((page - 1) * rows, page * rows > list.size() ? list.size() : page * rows));
//			}
//			
//
//			
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(pages);
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("查询失败");
//			json.setObj(null);
//			e.printStackTrace();
//		}
//		
//		return json;
//	}
//	
//	
//	@RequestMapping(value="homepage/more/view", method = RequestMethod.GET)
//	@ResponseBody
//	public Json homePageMoreViewSQL(Pageable pageable, HttpSession session) {
//		Json json = new Json();
//		try {
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			HyAdmin hyAdmin = hyAdminService.find(username);
//			List<Map<String, Object>> list = new ArrayList<>();
//			Map<String,Object> map=new HashMap<String,Object>();
//			List<Filter> filters = new ArrayList<Filter>();
//			
//			//拿到登录人所在的部门
//			Department department = hyAdmin.getDepartment();
//			
//			Store store = storeService.findStore(hyAdmin);
//			
//			//拿到父账号
//			while(hyAdmin.getHyAdmin() != null) {
//				hyAdmin = hyAdmin.getHyAdmin();
//			}
//			
//			HySupplier hySupplier = null;
//			//拿到供应商
//			if(hyAdmin.getLiableContracts() != null && hyAdmin.getLiableContracts().size() != 0) {
//				Iterator<HySupplierContract> iterator = hyAdmin.getLiableContracts().iterator();
//				HySupplierContract hySupplierContract = iterator.next();
//				hySupplier = hySupplierContract.getHySupplier();
//			}
//			
//			String[] attrs = new String[]{
//					"id","creatorName","createTime","updateTime","content","name"
//			};
//			
//			//订单type 2 认购门票
//			
//			StringBuilder totalSb = new StringBuilder("select count(*)");
//			StringBuilder pageSb = new StringBuilder("select hyat.id, hyadmin.name as creator_name, hyat.create_time, hyat.update_time,"
//					+ "hyat.content, hyat.name");
//			StringBuilder sb = new StringBuilder(" from hy_announcement hyat, hy_admin hyadmin"
//					+ " where hyat.operator=hyadmin.username and hyat.is_valid=1");
//			
//			//范围是总公司的能看到 and 和 or 语句结合 加括号
//			sb.append(" and (hyat.head_office_range=1");
//			
//			if(store != null && store.getStoreType() != null){
//				sb.append(" or hyat.store_range=0");
//				//挂靠门店
//				if(store.getStoreType() == 1) {
//					sb.append(" or hyat.store_range=2");
//				}
//				//虹宇门店
//				else if(store.getStoreType() == 0) {
//					sb.append(" or hyat.store_range=1");
//				}
//				//直营门店
//				else if(store.getStoreType() == 2) {
//					sb.append(" or hyat.store_range=4");
//				}
//			}
//			else if(hySupplier!=null){
//				sb.append(" or hyat.supplier_range=0");
//				//内部供应商
//				if(hySupplier.getIsInner()) {
//					sb.append(" or hyat.supplier_range=1");
//				}
//				else {
//					sb.append(" or hyat.supplier_range=2");
//				}
//			}
//			else if(department != null) {
//				//所在部门是分公司
//				if(department.getIsCompany() == true) {
//					//分公司全部可见的 或 公司在hy_announcement_company表里的
//					sb.append(" or hyat.is_all_company=1");
//					sb.append(" or exists (select * from hy_announcement_company hyatc where hyatc.hy_company_announcement=hyat.id and hyatc.company_range=" + department.getId() + ")");
//				}
//				//所在部门是部门
//				else {
//					//部门全部可见的 或 部门在hy_announcement_department表里的
//					sb.append(" or hyat.is_all_department=1");
//					sb.append(" or exists (select * from hy_announcement_department hyatc where hyatc.hy_department_announcement=hyat.id and hyatc.department_range=" + department.getId() + ")");
//				}
//			}
//			sb.append(")");
//			
//			
//			
////			if(hyAdmins!=null && !hyAdmins.isEmpty()){
////				List<String> adminStrArr = new ArrayList<>();
////				for(HyAdmin hyAdmin:hyAdmins){
////					adminStrArr.add("'"+hyAdmin.getUsername()+"'");
////					
////				}
////				String adminStr = String.join(",",adminStrArr);
////				sb.append(" and o1.operator_id in ("+adminStr+")");
////			}
//			
//
//			List totals = hyAnnouncementService.statis(totalSb.append(sb).toString());
//			Integer total = ((BigInteger)totals.get(0)).intValue();
//			
//			sb.append(" order by hyat.create_time desc");
//			
//			Integer sqlStart = (pageable.getPage()-1)*pageable.getRows();
//			Integer sqlEnd = pageable.getPage()*pageable.getRows();
//			sb.append(" limit "+sqlStart+","+sqlEnd);
//			//sb.append(" limit "+ count);
//			
//			//有效
//			//filters.add(Filter.eq("isValid", 1));
////			List<Order> orders = new ArrayList<Order>();
////			orders.add(Order.desc("createTime"));
//			
//			List<Object[]> objs = hyAnnouncementService.statis(pageSb.append(sb).toString());
//			
//			for(Object[] obj : objs){
//				Map<String, Object> map1 = ArrayHandler.toMap(attrs, obj);
//				
//				
//				list.add(map1);
//			}
//	
//			map.put("rows", list);
//			
//			Page<Map<String, Object>> page = new Page<>(list,total,pageable);
//
//			
//			json.setSuccess(true);
//			json.setMsg("查询成功");
//			json.setObj(page);
//		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("查询失败");
//			json.setObj(null);
//			e.printStackTrace();
//		}
//		
//		return json;
//	}
//	
//	/**
//	 * 查看公告详情
//	 * @param hyAnnouncement
//	 * @return Json
//	 */
//	@RequestMapping(value="detail", method = RequestMethod.GET)
//	@ResponseBody
//	public Json detail(Long id, HttpSession session) {
//		Json j = new Json();
//		
//		if(id == null){
//			j.setSuccess(false);
//			j.setMsg("id不能为空");
//			return j;
//		}
//		String username = (String) session.getAttribute(CommonAttributes.Principal);
//		HyAdmin hyAdmin = hyAdminService.find(username);
//		
//		HyAnnouncement hyAnnouncement = hyAnnouncementService.find(id);
//		
//		HashMap<String,Object> comMap=new HashMap<String,Object>();
//		HyAdmin creator=hyAnnouncement.getOperator();
//		//HyGroup hygroup = groupplaceholder.getGroup();
//
//		comMap.put("id", hyAnnouncement.getId());
//		if(creator!=null) {
//			comMap.put("creatorName", creator.getName());
//		}
//		comMap.put("createTime", hyAnnouncement.getCreateTime());
//		comMap.put("updateTime", hyAnnouncement.getUpdateTime());
//		comMap.put("storeRange", hyAnnouncement.getStoreRange().ordinal());
//		comMap.put("supplierRange", hyAnnouncement.getSupplierRange().ordinal());
//		comMap.put("range", hyAnnouncement.getRange());
//		comMap.put("departmentRange", hyAnnouncement.getDepartmentRangeIds());
//		comMap.put("companyRange", hyAnnouncement.getCompanyRangeIds());
//		comMap.put("content", hyAnnouncement.getContent());
//		comMap.put("name", hyAnnouncement.getName());
//		comMap.put("isAllDepartment", hyAnnouncement.getIsAllDepartment());
//		comMap.put("isAllCompany", hyAnnouncement.getIsAllCompany());
//
//		
//		try{
//			j.setObj(comMap);
//			j.setSuccess(true);
//			j.setMsg("查看详情成功！");
//		}catch (Exception e) {
//			// TODO Auto-generated catch block
//			j.setSuccess(false);
//			j.setMsg("查看失败！");
//		}
//		return j;
//	}
	
	
	/**
	 * 新增公告
	 * @param hyAnnouncement
	 * @return Json
	 */
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(@RequestBody AnnouncementWrap announcementWrap, HttpSession session) {
		Json j = new Json();
		
		if(BeanUtils.isBlank(announcementWrap)){
			j.setSuccess(false);
			j.setMsg("公告对象不能为空");
			return j;
		}
		
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			
			HyAnnouncement hyAnnouncement = new HyAnnouncement();
			hyAnnouncement.setContent(announcementWrap.getContent());
			hyAnnouncement.setName(announcementWrap.getName());
			hyAnnouncement.setCreateTime(new Date());
			hyAnnouncement.setOperator(hyAdmin);
			hyAnnouncement.setIsValid(1);
			hyAnnouncement.setRange(announcementWrap.getRange());
			if(announcementWrap.getStoreRange() == null) {
				hyAnnouncement.setStoreRange(StoreRange.disable);
			}
			else {
				hyAnnouncement.setStoreRange(StoreRange.values()[announcementWrap.getStoreRange()]);
			}
			if(announcementWrap.getSupplierRange() == null) {
				hyAnnouncement.setSupplierRange(SupplierRange.disable);
			}
			else {
				hyAnnouncement.setSupplierRange(SupplierRange.values()[announcementWrap.getSupplierRange()]);
			}
			
			if(announcementWrap.getCompanyRange() != null) {
				if(announcementWrap.getCompanyRange().equals("0")) {
					//全部分公司
					hyAnnouncement.setIsAllCompany(1);
				}
				else {
					String[] strs = announcementWrap.getCompanyRange().split(",");
					Set<Department> departmentRange = new HashSet<>();
					for(int i = 0; i < strs.length; i++) {
						Department department = hyDepartmentService.find(Long.valueOf(strs[i]));
						if(department != null) {
							departmentRange.add(department);
						}
					}
					hyAnnouncement.setCompanyRange(departmentRange);
					hyAnnouncement.setIsAllCompany(0);
				}
				
			}
			else {
				hyAnnouncement.setIsAllCompany(0);
			}
			
			if(announcementWrap.getDepartmentRange() != null) {
				if(announcementWrap.getDepartmentRange().equals("0")) {
					//全部分公司
					hyAnnouncement.setIsAllDepartment(1);
				}
				else {
					String[] strs = announcementWrap.getDepartmentRange().split(",");
					Set<Department> departmentRange = new HashSet<>();
					for(int i = 0; i < strs.length; i++) {
						Department department = hyDepartmentService.find(Long.valueOf(strs[i]));
						if(department != null) {
							departmentRange.add(department);
						}
					}
					hyAnnouncement.setDepartmentRange(departmentRange);
					hyAnnouncement.setIsAllDepartment(0);
				}
				
			}
			else {
				hyAnnouncement.setIsAllDepartment(0);
			}

			hyAnnouncementService.save(hyAnnouncement);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg("添加失败！");
		}
		return j;
	}
	
	/**
	 * 删除公告
	 * @param hyAnnouncement
	 * @return Json
	 */
	@RequestMapping(value="delete", method = RequestMethod.POST)
	@ResponseBody
	public Json delete(Long id, HttpSession session) {
		Json j = new Json();
		
		if(id == null){
			j.setSuccess(false);
			j.setMsg("id不能为空");
			return j;
		}
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin hyAdmin = hyAdminService.find(username);
		
		HyAnnouncement hyAnnouncement = hyAnnouncementService.find(id);
		//置为无效
		hyAnnouncement.setIsValid(0);
		
		try{
			hyAnnouncementService.update(hyAnnouncement);
			j.setSuccess(true);
			j.setMsg("删除成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg("删除失败！");
		}
		return j;
	}
	
	/**
	 * 修改公告
	 * @param hyAnnouncement
	 * @return Json
	 */
	@RequestMapping(value="change", method = RequestMethod.POST)
	@ResponseBody
	public Json change(@RequestBody AnnouncementWrap announcementWrap, HttpSession session) {
		Json j = new Json();
		
		if(BeanUtils.isBlank(announcementWrap)){
			j.setSuccess(false);
			j.setMsg("公告对象不能为空");
			return j;
		}
		
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			
			HyAnnouncement hyAnnouncement = hyAnnouncementService.find(announcementWrap.getId());
			hyAnnouncement.setContent(announcementWrap.getContent());
			hyAnnouncement.setName(announcementWrap.getName());
			hyAnnouncement.setUpdateTime(new Date());
			hyAnnouncement.setRange(announcementWrap.getRange());
			//hyAnnouncement.setOperator(hyAdmin);
			hyAnnouncement.setIsValid(1);
			if(announcementWrap.getStoreRange() == null) {
				hyAnnouncement.setStoreRange(StoreRange.disable);
			}
			else {
				hyAnnouncement.setStoreRange(StoreRange.values()[announcementWrap.getStoreRange()]);
			}
			if(announcementWrap.getSupplierRange() == null) {
				hyAnnouncement.setSupplierRange(SupplierRange.disable);
			}
			else {
				hyAnnouncement.setSupplierRange(SupplierRange.values()[announcementWrap.getSupplierRange()]);
			}
			
			if(announcementWrap.getCompanyRange() != null) {
				if(announcementWrap.getCompanyRange().equals("0")) {
					//全部分公司
					hyAnnouncement.setIsAllCompany(1);
					//清理set
					hyAnnouncement.setCompanyRange(new HashSet<Department>(0));
				}
				else {
					String[] strs = announcementWrap.getCompanyRange().split(",");
					Set<Department> departmentRange = new HashSet<>();
					for(int i = 0; i < strs.length; i++) {
						Department department = hyDepartmentService.find(Long.valueOf(strs[i]));
						if(department != null) {
							departmentRange.add(department);
						}
					}
					hyAnnouncement.setCompanyRange(departmentRange);
					hyAnnouncement.setIsAllCompany(0);
				}
				
			}
			
			if(announcementWrap.getDepartmentRange() != null) {
				if(announcementWrap.getDepartmentRange().equals("0")) {
					//全部部门
					hyAnnouncement.setIsAllDepartment(1);
					//清理set
					hyAnnouncement.setDepartmentRange(new HashSet<Department>(0));
				}
				else {
					String[] strs = announcementWrap.getDepartmentRange().split(",");
					Set<Department> departmentRange = new HashSet<>();
					for(int i = 0; i < strs.length; i++) {
						Department department = hyDepartmentService.find(Long.valueOf(strs[i]));
						if(department != null) {
							departmentRange.add(department);
						}
					}
					hyAnnouncement.setDepartmentRange(departmentRange);
					hyAnnouncement.setIsAllDepartment(0);
				}
				
			}

			hyAnnouncementService.update(hyAnnouncement);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg("添加失败！");
		}
		return j;
	}
	
	/**
	 * 获取分公司列表
	 * @return
	 */
	@RequestMapping(value="subcompany/view", method = RequestMethod.GET)
	@ResponseBody
	public Json subcompany(){
		Json j = new Json();
		try{	
			HyDepartmentModel model = hyDepartmentModelService.find(fengongsi);
			/** 找到所有子公司信息 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.eq("hyDepartmentModel", model);
			Filter filter1 = Filter.eq("status", 1);
			filters.add(filter);
			filters.add(filter1);
			List<Order> orders = new ArrayList<Order>();
			Order order = new Order("id",Direction.asc);
			orders.add(order);
			List<Department> departments = hyDepartmentService.findList(null,filters,orders);
			j.setSuccess(true);
			j.setMsg("查询成功！");
			j.setObj(departments);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;	
	}
	
	/**
	 * 获取部门列表
	 * @return
	 */
	@RequestMapping(value="department/view", method = RequestMethod.GET)
	@ResponseBody
	public Json department(){
		Json j = new Json();
		try{
			List<HashMap<String, Object>> lhm = new ArrayList<HashMap<String, Object>>();
			List<Filter> filters = new ArrayList<Filter>();
			Filter f = Filter.eq("status", 1);
			Filter f1 = Filter.eq("isCompany", false);

			filters.add(f);
			filters.add(f1);
			List<Order> orders = new ArrayList<>();
			Order order = Order.asc("id");
			orders.add(order);
			List<Department> departs = hyDepartmentService.findList(null, filters, orders);
		
			for(Department depart : departs){
				if(depart.getHyDepartment() != null && depart.getHyDepartment().getIsCompany() == true) {
					HashMap<String, Object> hm = new HashMap<String, Object>();
					hm.put("id", depart.getId());
					hm.put("fullName", depart.getFullName());
					hm.put("children", addChildren(depart));
					lhm.add(hm);
				}			
			}
			j.setSuccess(true);
			j.setMsg("查询成功！");
			j.setObj(lhm);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	public List<HashMap<String, Object>> addChildren(Department parent) {
		List<HashMap<String, Object>> list = new ArrayList();
		if(parent.getHyDepartments().size() > 0){
			for(Department child : parent.getHyDepartments()) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("id", child.getId());
				hm.put("fullName", child.getFullName());
				hm.put("children", addChildren(child));
				list.add(hm);
			}
		}
		return list;
	}
}
