package com.grain.service.user;

import java.util.List;

import com.grain.entity.user.Role;
import com.grain.service.BaseService;
import com.hongyu.EasyUItreeNode;
import com.hongyu.Principal;

/**
 * Service - 角色
 * 
 */
public interface RoleService extends BaseService<Role, Long> {

	List<EasyUItreeNode> tree(Principal p, String id);

	void grant(Role role, Long... ids);


}