package com.hongyu.service.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.controller.HyRoleController.Auth;
import com.hongyu.dao.DepartmentDao;
import com.hongyu.dao.HyAuthorityDao;
import com.hongyu.dao.HyRoleDao;
import com.hongyu.dao.RoleAuthorityDao;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HyRoleAuthority.CheckedRange;
import com.hongyu.service.HyRoleService;
import com.hongyu.util.Constants;
import com.hongyu.util.redis.RedisUtils;
@Service(value = "hyRoleServiceImpl")
public class HyRoleServiceImpl extends BaseServiceImpl<HyRole, Long> implements HyRoleService {
	@Resource(name = "hyRoleDaoImpl")
	HyRoleDao roleDao;
	
	@Resource(name = "hyRoleAuthorityDaoImpl")
	RoleAuthorityDao roleAuthorityDao;
	
	@Resource(name = "hyAuthorityDaoImpl")
	HyAuthorityDao authorityDao;
	
	@Resource(name = "departmentDaoImpl")
	DepartmentDao  hyDepartmentDao;
		
    @Resource(name = "redisUtils")
    private RedisUtils redisUtils;

	
	@Resource(name = "hyRoleDaoImpl")
	public void setBaseDao(HyRoleDao dao){
		super.setBaseDao(dao);		
	}

	@Override
	@Transactional(readOnly = false)
	public void grant(Long roleId, Long... ids) {
		HyRole parent = roleDao.find(roleId);
		
		Set<HyRole> subRoles = new HashSet<HyRole>();
		for (Long id : ids) {
			HyRole subRole = roleDao.find(id);
			subRoles.add(subRole);
		}
		parent.setHyRolesForSubroles(subRoles);
		update(parent);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void grantResources(Long roleId, Set<Auth> authorities) {
		HyRole r = roleDao.find(roleId);
		Set<HyRoleAuthority> ra = r.getHyRoleAuthorities();

		for(HyRoleAuthority roleAuthority : ra){
			roleAuthorityDao.remove(roleAuthority);
		}
		
		for(Auth authority : authorities){
			HyRoleAuthority a = new HyRoleAuthority();
			a.setRoles(r);
			a.setAuthoritys(authorityDao.find(authority.getId()));
			if(authority.getCr() != null){
				a.setRangeCheckedNumber(authority.getCr());
			}
			if(authority.getCo() != null) {
				a.setOperationCheckedNumber(authority.getCo());
			}
			if(authority.getDepartments().size() > 0) {
				Set<Department> departments = new HashSet<Department>();
				for(Long id : authority.getDepartments()) {
					Department department = hyDepartmentDao.find(id);
					departments.add(department);
				}
				a.setDepartments(departments);
			}
			roleAuthorityDao.persist(a);
		}
		
		//start of add新增授权改变的时候清空redis缓存
		String id = roleId.toString();
		String key = Constants.TABLE_HY_ROLE + ":" + id;
		if (redisUtils.hasKey(key)) {
	         //遍历集合删除roleAuthority的内容
			 Map<String, String> res = redisUtils.getMap(key);
			 if(MapUtils.isNotEmpty(res)) {
				  for (Map.Entry<String, String> entry : res.entrySet()) {
					  String hyRoleAuthorityId = entry.getKey();
					  String innerKey = Constants.TABLE_HY_ROLE_AUTHORITY + ":" + hyRoleAuthorityId;
					  if(redisUtils.hasKey(innerKey)) {
						  redisUtils.delete(innerKey);
					  }
				  }   
			 }
			 //删除role的缓存内容
	         redisUtils.delete(key);
	    }
		//end of add
	}
	
    @Override
    //@CacheEvict(value = "hyRole", allEntries = true)
    public HyRole update(HyRole role){
    	//start of add更新角色的时候清redis缓存
    	String key = Constants.TABLE_HY_ROLE + ":" + role.getId();
        if (redisUtils.hasKey(key)) {
            // TODO 强制转换
            redisUtils.delete(key);
        }
    	//end of add
    	return super.update(role);
    }
    
    @Override
    //@CacheEvict(value = "hyRole", allEntries = true)
    public HyRole update(HyRole role, String ...ignoreProperties){
    	//start of add更新角色的时候清redis缓存
    	String key = Constants.TABLE_HY_ROLE + ":" + role.getId();
        if (redisUtils.hasKey(key)) {
            // TODO 强制转换
            redisUtils.delete(key);
        }
    	//end of add
    	return super.update(role, ignoreProperties);
    }
    
    @Override
    //@CacheEvict(value = "hyRole", allEntries = true)
    public void delete(HyRole role) {
    	super.delete(role);
    }

}
