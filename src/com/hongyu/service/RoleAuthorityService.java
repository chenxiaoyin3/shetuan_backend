package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.HyRoleAuthority;

public interface RoleAuthorityService extends BaseService<HyRoleAuthority, Long> {
	//add by gxz 缓存权限
	HyRoleAuthority update(HyRoleAuthority roleAuthority);
	HyRoleAuthority update(HyRoleAuthority roleAuthority, String ...ignoreProperties);
	//end of add
}
