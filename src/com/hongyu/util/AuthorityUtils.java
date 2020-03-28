package com.hongyu.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRoleAuthority.CheckedRange;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.util.redis.RedisUtils;

public class AuthorityUtils {

	public static Set<HyAdmin> getAdmins(HttpSession session, HttpServletRequest request) {

		try {
			Set<HyAdmin> hyAdmins = new HashSet<HyAdmin>();
			Set<String> uns = new HashSet<>();
			List<Filter> filters = new ArrayList<>();
			/** 反射获取服务实体类 */
			WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
			HyAdminService hyAdminService = webApplicationContext.getBean(HyAdminService.class);
			DepartmentService departmentService=webApplicationContext.getBean(DepartmentService.class);
			RedisUtils redisUtils=webApplicationContext.getBean(RedisUtils.class);
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			/** 默认可以看到自己创建的数据 */
			hyAdmins.add(admin);

			/** 获取当前权限范围 */
			CheckedRange cr = (CheckedRange) request.getAttribute("cr");
			//start of add新增，如果是个人权限，直接返回该用户
			if(cr != null && cr == CheckedRange.individual) {
				return hyAdmins;
			}
			//end of add		

			/** 如果是总公司的权限，取出所有的用户 */
			if(cr != null && cr == CheckedRange.company) {
				hyAdmins.addAll(hyAdminService.findAll());
				return hyAdmins;
			}
			

			//先从redis中获取员工信息
			/** 获取角色权限ID */
			Long hyRoleAuthorityId = (Long) request.getAttribute("roleAuthorityId");
			if (null == hyRoleAuthorityId) {
				throw new RuntimeException("角色权限ID为空");
			}
			Set<String> usernames = new HashSet<>();
			String key = Constants.TABLE_HY_DEPARTMENT_AUTHORITY + ":" + hyRoleAuthorityId;

			if(redisUtils.hasKey(key)){
				Object obj = redisUtils.getHV(key, Constants.HYADMINS);
				if(obj != null){
					usernames = (Set<String>) obj;
					if (usernames.isEmpty()) {
						return hyAdmins;
					}					
					filters.add(Filter.in("username", usernames));
					hyAdmins.addAll(hyAdminService.findList(null, filters, null));
					return hyAdmins;
				}
			}

			//end of redis获取信息


			// 若数据不存在将数据库的数据放到Redis
			/** 获取分配的部门权限 */
			Set<Long> departments = (Set<Long>) request.getAttribute("range");
			Set<HyAdmin> tempHyAdmins = new HashSet<>();
//			filters.clear();
//			if (!departs.isEmpty()) {
//		        filters.add(Filter.in("id", departs));
//		    }
//			Set<Department> departments = new HashSet<>(departmentService.findList(null, filters, null));

			/** 如果分配的是部门权限 */
			if(departments.size() > 0){

				/**
				 * 遍历所有分配的部门找到所有本部门及下属部门员工
				 */
				for(Long temp : departments) {
					Department department = departmentService.find(temp);

					/** 加入本部门所有员工 */
					tempHyAdmins.addAll(department.getHyAdmins());

					/** 找到所有下属部门 */
					String treePaths = department.getTreePath() + department.getId() + ",";
					List<Filter> filters1 = new ArrayList<Filter>();
					Filter filter = Filter.like("treePath", treePaths);
					filters1.add(filter);
					List<Department> subDepartments = departmentService.findList(null, filters1, null);

					/** 加入下属部门所有员工 */
					for(Department subDepartment : subDepartments) {
						tempHyAdmins.addAll(subDepartment.getHyAdmins());
					}
				}
			}
			for (HyAdmin temp : tempHyAdmins) {
				uns.add(temp.getUsername());			
			}
			Map<String, Object> map = new HashMap<>();
			map.put(Constants.HYADMINS, uns);
			redisUtils.setMap(key, map);
			tempHyAdmins.add(admin);
			return tempHyAdmins;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
