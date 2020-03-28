package com.grain.service.impl.user;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.grain.util.StringUtil;
import com.hongyu.CommonAttributes;
import com.hongyu.Principal;
import com.grain.dao.user.AdminDao;
import com.grain.entity.user.Admin;
import com.grain.entity.user.OpRegister;
import com.grain.service.impl.BaseServiceImpl;
import com.grain.service.user.AdminService;

/**
 * Service - 管理员
 * 
 */
@Service("adminServiceImpl")
public class AdminServiceImpl extends BaseServiceImpl<Admin, String> implements AdminService {

	@Resource(name = "adminDaoImpl")
	private AdminDao adminDao;

	@Resource(name = "adminDaoImpl")
	public void setBaseDao(AdminDao adminDao) {
		super.setBaseDao(adminDao);
	}

	@Transactional(readOnly = true)
	public boolean usernameExists(String username) {
		return adminDao.usernameExists(username);
	}

	@Transactional(readOnly = true)
	public Admin findByUsername(String username) {
		return adminDao.findByUsername(username);
	}


	//是否经过鉴权(不支持session盗取)
	@Transactional(readOnly = true)
	public boolean isAuthenticated() {	
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(CommonAttributes.Principal);
			if (principal != null) {
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly = true)
	public Admin getCurrent() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(CommonAttributes.Principal);
			if (principal != null) {
				return findByUsername(principal.getUsername());
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	public String getCurrentUsername() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes != null) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Principal principal = (Principal) request.getSession().getAttribute(CommonAttributes.Principal);
			if (principal != null) {
				return principal.getUsername();
			}
		}
		return null;
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void save(Admin admin) {
		if (admin.getPassword()!=null){
			String encryptpw = StringUtil.encodePassword(admin.getPassword(),
					"MD5");
			admin.setPassword(encryptpw);
		}
		super.save(admin);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public Admin update(Admin admin) {
		Admin a = find(admin.getUsername());
		admin.setPassword(a.getPassword());         //找回原来的密码
		return super.update(admin);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void  updatePassword(Admin admin) {
		if (admin.getPassword()!=null){
			String encryptpw = StringUtil.encodePassword(admin.getPassword(),
					"MD5");
			admin.setPassword(encryptpw);
		}
		update(admin);
	}
	
	
	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public Admin update(Admin admin, String... ignoreProperties) {
		Admin a = find(admin.getUsername());
		admin.setPassword(a.getPassword());         //找回原来的密码
		return super.update(admin, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void delete(String id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void delete(String... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "authorization", allEntries = true)
	public void delete(Admin admin) {
		super.delete(admin);
	}

	//鉴权
	@Override
	public boolean hasAutohrize(String username, String targetURI) {
		// TODO Auto-generated method stub
		Admin ad = findByUsername(username);
		if (ad.getRole().getOps().isEmpty()) return false;
		for (OpRegister op : ad.getRole().getOps()){
			if (op.getUrl()!=null && targetURI.contains(op.getUrl()))	//缺省含子目录	//op.getUrl().equalsIgnoreCase(targetURI)
				return true;
		}
		return false;
	}

	@Override
	public boolean loginCheck(Admin admin) {
		Admin ad=findByUsername(admin.getUsername());
		String encryptpw = StringUtil.encodePassword(admin.getPassword(),
				"MD5");
		if (ad!=null){
			if (ad.getPassword().equalsIgnoreCase(encryptpw))
				return true;
		}
		return false;
	}

}