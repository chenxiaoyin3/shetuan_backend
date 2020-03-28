package com.hongyu.interceptor;

import static com.hongyu.util.Constants.captchaPath;
import static com.hongyu.util.Constants.logoff;
import static com.hongyu.util.Constants.resourcesPath;
import static com.hongyu.util.Constants.submitPath;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.HyRoleAuthority;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.RoleAuthorityService;
import com.hongyu.util.Constants;
import com.hongyu.util.redis.RedisUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hongyu.CommonAttributes;
import com.hongyu.SessionListener;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HyRoleAuthority.CheckedRange;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;

/**
 * @author gxz
 * */
public class HyAdminInterceptor implements HandlerInterceptor {
    @Resource(name = "redisUtils")
    private RedisUtils redisUtils;

    @Resource(name = "hyAdminServiceImpl")
    private HyAdminService hyAdminService;

    @Resource(name = "hyRoleServiceImpl")
    private HyRoleService hyRoleService;

    @Resource(name = "hyRoleAuthorityServiceImpl")
    private RoleAuthorityService roleAuthorityService;

    @Resource(name = "departmentServiceImpl")
    private DepartmentService hyDepartmentService;

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {}

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception {}

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取访问的URI
        String contextPath = request.getContextPath();
        String targetURI = request.getRequestURI().replace(contextPath + "/", "");
        HttpSession session = request.getSession();

        // 请求的是静态资源文件(如 .js .css 图片)或者注销，则直接通过
        if (targetURI.contains(resourcesPath) || targetURI.equals(logoff) || targetURI.equals(captchaPath)) {
            return true;
        }

        // 未登录
        if (session.getAttribute(CommonAttributes.Principal) == null) {
            if (targetURI.equals(submitPath)) {
                return true;
            } else {
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                return false;
            }
        } else {
            // 已登录
            String username = (String) session.getAttribute(CommonAttributes.Principal);
            HttpSession tempSession = SessionListener.sessionMap.get(username);
            if (tempSession == null || !tempSession.equals(session)) {
                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                return false;
            }
//            String[] roleResult = 
            String roleId = getRoleId(username);
            String roleName = getRoleName(roleId);

            // 管理员不拦截
            if (roleId != null && "admin".equalsIgnoreCase(roleName)) {
                // 设置权限控制参数
                request.setAttribute("co", CheckedOperation.edit);
                request.setAttribute("cr", CheckedRange.company);
                request.setAttribute("range", new HashSet<>().add(hyDepartmentService.find(1L)));
                return true;
            }
            // 登陆后禁止再访问登陆界面与登陆路径
            if (targetURI.equals(submitPath)) {
                response.setStatus(HttpStatus.SC_FORBIDDEN);
                return false;
            } else {
                if (!hasAuthorize(Long.parseLong(roleId), targetURI, request)) {
                    response.setStatus(HttpStatus.SC_FORBIDDEN);
                    return false;
                }
                return true;
            }
        }
    }

    private  boolean hasAuthorize(Long roleId, String targetURI, HttpServletRequest request) {
        if (StringUtils.isEmpty(targetURI)){
            return false;
        }
        String[] uris = targetURI.split("/");
        String lastUri = uris[uris.length - 1];
        try {
            Map<String, String> requestUrls = getRequestUrls(roleId);

            for (Map.Entry<String, String> entry : requestUrls.entrySet()) {
                String url = entry.getValue();
                if (StringUtils.isNotBlank(url) && targetURI.contains(url)) {
                    Long hyRoleAuthorityId = Long.parseLong(entry.getKey());
                    CheckedOperation co = getCheckedOperation(hyRoleAuthorityId);
                    CheckedRange cr = getCheckedRange(hyRoleAuthorityId);
                    Set<Long> range = getDepartment(hyRoleAuthorityId);
                    // 设置权限控制参数
                    request.setAttribute("co", co);
                    request.setAttribute("cr", cr);
                    request.setAttribute("range", range);
                    request.setAttribute("roleAuthorityId", hyRoleAuthorityId);
                    //如果是编辑权限那么所有的方法都不进行拦截 如果是查看权限，拦截除了POST方法以外的请求
                    return co == CheckedOperation.edit || co == CheckedOperation.editIndividual || "view".equals(lastUri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    private String[] getRoleInfo(String username) {
//    	 String[] result = new String[2];
//    	 String key = TABLE_HY_ADMIN + ":" + username;
//         String roleId;
//         if(redisUtils.hasKey(key)){
//             roleId = redisUtils.getObject(key);
//         }else{
//             // TODO null的判断
//             HyAdmin hyAdmin = hyAdminService.find(username);
//             Long id = hyAdmin.getRole().getId();
//             roleId = id.toString();
//             // 将数据库的数据放到Redis
//             redisUtils.setObject(key, roleId);
//         }
//         result[0] = roleId;
//         //放roleName到缓存
//         String keyRole = TABLE_HY_ROLE + ":" + roleId;
//         String roleName;
//         if (redisUtils.hasKey(keyRole)) {
//             // TODO 强制转换
//             roleName = (String) redisUtils.getHV(keyRole, ROLE_NAME);
//             if (StringUtils.isNotEmpty(roleName)) {
//                 result[1] = roleName;
//             }
//         }
//         // TODO null的判断
//         HyRole hyRole = hyRoleService.find(roleId);
//         roleName = hyRole.getName();
//         // 将数据库的数据放到Redis
//         Map<String, String> map = new HashMap<>(1);
//         map.put(ROLE_NAME, roleName);
//         redisUtils.setMap(key, map);
//         
//    }
    
    
    private String getRoleId(String username) {
        String key = Constants.TABLE_HY_ADMIN + ":" + username;
        String roleId;
        if(redisUtils.hasKey(key)){
            roleId = redisUtils.getObject(key);
        }else{
            // TODO null的判断
            HyAdmin hyAdmin = hyAdminService.find(username);
            Long id = hyAdmin.getRole().getId();
            roleId = id.toString();
            // 将数据库的数据放到Redis
            redisUtils.setObject(key, roleId);
        }
        return roleId;
    }

    private String getRoleName(String id) {
        Long roleId = Long.parseLong(id);
        String key = Constants.TABLE_HY_ROLE + ":" + roleId;
        String roleName;
        if (redisUtils.hasKey(key)) {
            // TODO 强制转换
            roleName = (String) redisUtils.getHV(key, Constants.ROLE_NAME);
            if (StringUtils.isNotEmpty(roleName)) {
                return roleName;
            }
        }
        // TODO null的判断
        HyRole hyRole = hyRoleService.find(roleId);
        roleName = hyRole.getName();
        // 将数据库的数据放到Redis
        Map<String, String> map = new HashMap<>(1);
        map.put(Constants.ROLE_NAME, roleName);
        redisUtils.setMap(key, map);
        return roleName;
    }

    private Map<String, String> getRequestUrls(Long roleId) {
        String key = Constants.TABLE_HY_ROLE + ":" + roleId;
        Map<String, String> res = new HashMap<>();
        if (redisUtils.hasKey(key)) {
            res = redisUtils.getMap(key);
            // 必须将已有的("-1000",角色名称)的键值对移除!
            if(res.containsKey(Constants.ROLE_NAME)){
                res.remove(Constants.ROLE_NAME);
            }
            if (MapUtils.isNotEmpty(res)) {
                return res;
            }
        }
        // TODO null的判断
        Set<HyRoleAuthority> set = hyRoleService.find(roleId).getHyRoleAuthorities();
        for (HyRoleAuthority ra : set) {
            String requestUrl = ra.getAuthoritys().getRequestUrl();
            if(StringUtils.isNotEmpty(requestUrl)){
                res.put(ra.getId().toString(), requestUrl);
            }
        }
        // 将数据库的数据放到Redis
        redisUtils.setMap(key, res);
        return res;
    }

    private CheckedOperation getCheckedOperation(Long hyRoleAuthorityId) {
        String key = Constants.TABLE_HY_ROLE_AUTHORITY + ":" + hyRoleAuthorityId;
        CheckedOperation co;
        if (redisUtils.hasKey(key)) {
            // TODO 强制转换
            String str = (String) redisUtils.getHV(key, Constants.CHECKED_OPERATION);
            if (StringUtils.isNotEmpty(str)) {
                // Integer转为枚举
                return CheckedOperation.values()[Integer.parseInt(str)];
            }
        }
        // TODO null的判断
        HyRoleAuthority hyRoleAuthority = roleAuthorityService.find(hyRoleAuthorityId);
        co = hyRoleAuthority.getOperationCheckedNumber();
        // 将数据库的数据放到Redis
        Map<String, Object> map = new HashMap<>();
        map.put(Constants.CHECKED_OPERATION, String.valueOf(co.ordinal()));
        redisUtils.setMap(key, map);
        return co;
    }

    private CheckedRange getCheckedRange(Long hyRoleAuthorityId) {
        String key = Constants.TABLE_HY_ROLE_AUTHORITY + ":" + hyRoleAuthorityId;
        CheckedRange cr;
        if (redisUtils.hasKey(key)) {
            // TODO 强制转换
            String str = (String) redisUtils.getHV(key, Constants.CHECKED_RANGE);
            // Integer转为枚举
            if (StringUtils.isNotEmpty(str)) {
                return CheckedRange.values()[Integer.parseInt(str)];
            }
        }
        // TODO null的判断
        HyRoleAuthority hyRoleAuthority = roleAuthorityService.find(hyRoleAuthorityId);
        cr = hyRoleAuthority.getRangeCheckedNumber();
        // 将数据库的数据放到Redis
        Map<String, Object> map = new HashMap<>();
        map.put(Constants.CHECKED_RANGE, String.valueOf(cr.ordinal()));
        redisUtils.setMap(key, map);
        return cr;
    }

    @SuppressWarnings("unchecked")
	private Set<Long> getDepartment(Long hyRoleAuthorityId) {
    	 String key = Constants.TABLE_HY_ROLE_AUTHORITY + ":" + hyRoleAuthorityId;
         Set<Long> set = new HashSet<>();
         if(redisUtils.hasKey(key)){
             Object obj = redisUtils.getHV(key, Constants.DEPARTMENT);
             if(obj != null){
                 // TODO 强制转换
                 return (Set<Long>) obj;
             }
         }
        // TODO null的判断
        HyRoleAuthority hyRoleAuthority = roleAuthorityService.find(hyRoleAuthorityId);
        if(hyRoleAuthority == null) {
        	throw new RuntimeException("找不到角色的权限数据");
        }
        Set<Department> departments = hyRoleAuthority.getDepartments();
        if(departments == null) {
        	throw new RuntimeException("找不到角色部门的数据");
        }
        for(Department temp : departments) {
        	set.add(temp.getId());
        }
        // 将数据库的数据放到Redis
        Map<String, Object> map = new HashMap<>(1);
        map.put(Constants.DEPARTMENT, set);
        redisUtils.setMap(key, map);
        return set;
    }
}
