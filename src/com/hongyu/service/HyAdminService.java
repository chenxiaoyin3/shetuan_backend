package com.hongyu.service;

import javax.servlet.http.HttpServletRequest;

import com.grain.service.BaseService;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;

public interface HyAdminService extends BaseService<HyAdmin, String> {
	HyAdmin find(String username);
	//add by gxz 缓存加一层
	HyAdmin update(HyAdmin admin);
	HyAdmin update(HyAdmin admin, String ...ignoreProperties);
	void save(HyAdmin admin);
	//end of add
	void updatePassword(HyAdmin admin);
	boolean loginCheck(String username, String password);
	boolean hasAuthorize(HyRole role, String targetURI, HttpServletRequest request);
	/**
	 * 将用户账号置为无效
	 * @param username
	 */
	void invalidateAdmin(String username);
	/**
	 * 将用户账号置为有效
	 * @param username
	 */
	void validateAdmin(String username);
	
	/**
	 * 将用户账号全部置为查看权限
	 */
	void putView(String username);
}
