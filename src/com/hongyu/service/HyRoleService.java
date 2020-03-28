package com.hongyu.service;

import java.util.Set;

import com.grain.service.BaseService;
import com.hongyu.controller.HyRoleController.Auth;
import com.hongyu.entity.HyRole;

public interface HyRoleService extends BaseService<HyRole, Long> {
	void grant(Long roleId, Long... ids);
	void grantResources(Long roleId, Set<Auth> authorities);
	//add by guoxinze 角色缓存
	HyRole update(HyRole role);
	HyRole update(HyRole role, String ...ignoreProperties);
	//end of add
}
