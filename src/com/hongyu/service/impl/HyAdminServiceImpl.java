package com.hongyu.service.impl;

import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grain.service.impl.BaseServiceImpl;
import com.grain.util.StringUtil;
import com.hongyu.dao.HyAdminDao;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HyRoleAuthority.CheckedRange;
import com.hongyu.interceptor.HyAdminInterceptor;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.RoleAuthorityService;
import com.hongyu.util.Constants;
import com.hongyu.util.redis.RedisUtils;

/**
 * @author gxz
 * */
@Service(value = "hyAdminServiceImpl")
public class HyAdminServiceImpl extends BaseServiceImpl<HyAdmin, String> implements HyAdminService {
	@Resource(name = "hyAdminDaoImpl")
	HyAdminDao dao;
	
	@Resource(name = "hyRoleAuthorityServiceImpl")
	private RoleAuthorityService roleAuthorityService;
	
    @Resource(name = "redisUtils")
    private RedisUtils redisUtils;
    
  
	
	@Resource(name = "hyAdminDaoImpl")
	public void setBaseDao(HyAdminDao dao){
		super.setBaseDao(dao);		
	}

    @Override
   // @Cacheable("hyAdmin")
    public HyAdmin find(String username){
        return super.find(username);
    }
    
    @Override
   // @Cacheable("hyAdmin")
    public void save(HyAdmin admin){
    	//新增角色的时候清空部门权限缓存
    	String prex = Constants.TABLE_HY_DEPARTMENT_AUTHORITY + ":" + "*";
    	redisUtils.deleteBypPrex(prex);
        super.save(admin);
    }

    @Override
	@Transactional
	//@CacheEvict(value = "hyAdmin", allEntries = true)
	public void updatePassword(HyAdmin admin) {
		if (admin.getPassword()!=null){
			String encryptpw = StringUtil.encodePassword(admin.getPassword(),"MD5");
			admin.setPassword(encryptpw);
		}
		super.update(admin);
	}
    
    @Override
    //@CacheEvict(value = "hyAdmin", allEntries = true)
    public HyAdmin update(HyAdmin admin){
    	//start of add，更新用户角色的时候清空redis缓存
    	String key = Constants.TABLE_HY_ADMIN + ":" + admin.getUsername();
    	if (redisUtils.hasKey(key)) {
    		redisUtils.delete(key);
        }  	
    	//end of add
    	//start of add清空部门权限缓存
    	HyAdmin original = super.find(admin.getUsername());
    	//如果角色的部门发生变化 清空缓存
    	String prex = Constants.TABLE_HY_DEPARTMENT_AUTHORITY + ":" + "*";
    	if(!original.getDepartment().equals(admin.getDepartment())) {
    		redisUtils.deleteBypPrex(prex);
    	}
    	//end of add
    	return super.update(admin);
    }
    
    @Override
    //@CacheEvict(value = "hyAdmin", allEntries = true)
    public HyAdmin update(HyAdmin admin, String ...ignoreProperties){
    	//start of add，更新用户角色的时候清空redis缓存
    	String key = Constants.TABLE_HY_ADMIN + ":" + admin.getUsername();
    	if (redisUtils.hasKey(key)) {
    		redisUtils.delete(key);
        }  	
    	//end of add
    	return super.update(admin, ignoreProperties);
    }
        

	@Override
	public boolean loginCheck(String username, String password) {
		HyAdmin ad = dao.find(username);
		if(ad == null || !ad.getIsEnabled()){
            //如果账号被设置为无效登录也失败
            return false;
        }
		String encryptpw = StringUtil.encodePassword(password,"MD5");
		return ad.getPassword().equalsIgnoreCase(encryptpw);
	}

    @Override
    public boolean hasAuthorize(HyRole role, String targetURI, HttpServletRequest request) {
        if (targetURI == null) return false;
        String[] uris = targetURI.split("/");
        String lastUri = uris[uris.length - 1];
        Set<HyRoleAuthority> hyRoleAuthorities = role.getHyRoleAuthorities();

        if (hyRoleAuthorities.isEmpty())
            return false;
        try {
            for (HyRoleAuthority ra : hyRoleAuthorities) {
                if (ra.getAuthoritys() == null)
                    continue;
                String url = ra.getAuthoritys().getRequestUrl();

                if (StringUtils.isNotBlank(url) && targetURI.contains(url)) {

                    CheckedOperation co = ra.getOperationCheckedNumber();
                    Set<Department> range = ra.getDepartments();

                    CheckedRange cr = ra.getRangeCheckedNumber();
                    /**
                     * 设置权限控制参数
                     */
                    request.setAttribute("co", co);
                    request.setAttribute("cr", cr);
                    request.setAttribute("range", range);

                    /**
                     * 如果是编辑权限那么所有的方法都不进行拦截
                     * 如果是查看权限，拦截除了POST方法以外的请求
                     */
                    if (co == CheckedOperation.edit || co == CheckedOperation.editIndividual) {
                        return true;
                    } else if (lastUri.equals("view"))
                        return true;
                    else
                        return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

	/**
	 * 禁用用户账号
	 */
	@Override
	public void invalidateAdmin(String username) {
		HyAdmin admin = dao.find(username);
		admin.setIsEnabled(false);
		update(admin);
	}

	@Override
	public void validateAdmin(String username) {
		HyAdmin admin = dao.find(username);
		admin.setIsEnabled(true);
		update(admin);
	}

	@Override
	public void putView(String username) {
		HyRole hyRole = this.find(username).getRole();
		Set<HyRoleAuthority> hyRoleAuthorities = hyRole.getHyRoleAuthorities();
		for(HyRoleAuthority temp : hyRoleAuthorities) {
			temp.setOperationCheckedNumber(CheckedOperation.view);
			roleAuthorityService.update(temp);
		}
	}
	

}
