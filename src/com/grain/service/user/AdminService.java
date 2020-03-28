package com.grain.service.user;


import com.grain.entity.user.Admin;
import com.grain.service.BaseService;
import com.hongyu.Page;
import com.hongyu.Pageable;

/**
 * Service - 管理员
 * 
 */
public interface AdminService extends BaseService<Admin, String> {

	/**
	 * 判断用户名是否存在
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 用户名是否存在
	 */
	boolean usernameExists(String username);

	/**
	 * 根据用户名查找管理员
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 管理员，若不存在则返回null
	 */
	Admin findByUsername(String username);


	/**
	 * 判断管理员是否登录
	 * 
	 * @return 管理员是否登录
	 */
	boolean isAuthenticated();

	/**
	 * 获取当前登录管理员
	 * 
	 * @return 当前登录管理员,若不存在则返回null
	 */
	Admin getCurrent();

	/**
	 * 获取当前登录用户名
	 * 
	 * @return 当前登录用户名,若不存在则返回null
	 */
	String getCurrentUsername();

	boolean hasAutohrize(String username, String targetURI);

	boolean loginCheck(Admin admin);

	Page<Admin> findPage(Pageable pageable, Admin queryParm);

	void updatePassword(Admin su);

}