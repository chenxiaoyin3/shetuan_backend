package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.dao.RoleAuthorityDao;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.RoleAuthorityService;

@Service(value = "hyRoleAuthorityServiceImpl")
public class RoleAuthorityServiceImpl extends BaseServiceImpl<HyRoleAuthority, Long> 
implements RoleAuthorityService{
	@Resource(name = "hyRoleAuthorityDaoImpl")
	RoleAuthorityDao dao;
	
	@Resource(name = "hyRoleAuthorityDaoImpl")
	public void setBaseDao(RoleAuthorityDao dao){
		super.setBaseDao(dao);		
	}
	
    @Override
    //@CacheEvict(value = "hyRoleAuthority", allEntries = true)
    public HyRoleAuthority update(HyRoleAuthority roleAuthority){
    	return super.update(roleAuthority);
    }
    
    @Override
    //@CacheEvict(value = "hyRoleAuthority", allEntries = true)
    public HyRoleAuthority update(HyRoleAuthority roleAuthority, String ...ignoreProperties){
    	return super.update(roleAuthority, ignoreProperties);
    }
    
    @Override
    //@CacheEvict(value = "hyRoleAuthority", allEntries = true)
    public void delete(HyRoleAuthority roleAuthority) {
    	super.delete(roleAuthority);
    }

}
