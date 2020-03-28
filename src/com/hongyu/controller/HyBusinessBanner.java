package com.hongyu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessBanner;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.BusinessBanner.BannerType;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BusinessBannerService;
import com.hongyu.service.HyAdminService;
import com.hongyu.util.AuthorityUtils;

@RestController
@RequestMapping("/admin/banner")
public class HyBusinessBanner {
	
	@Resource(name="businessBannerServiceImpl")
	private BusinessBannerService businessBannerService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	/**
	 * 广告列表
	 * @param pageable
	 * @param businessBanner
	 * @return
	 */
	@RequestMapping(value="/list/view")
	public Json bannerList(Pageable pageable, BusinessBanner businessBanner, HttpSession session, HttpServletRequest request){
		Json j = new Json();
		try {
			Map<String, Object> result = new HashMap<String, Object>();
			List<HashMap<String, Object>> lhm = new ArrayList<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.in("creator", hyAdmins));
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<>();
			orders.add(Order.asc("state"));
			orders.add(Order.asc("type"));
			pageable.setOrders(orders);
	      
	        Page<BusinessBanner> page = this.businessBannerService.findPage(pageable, businessBanner);
	        if (page.getRows().size() > 0) {
	            for (BusinessBanner p : page.getRows())
	            {
	              HashMap<String, Object> hm = new HashMap<String, Object>();
	              HyAdmin creater = p.getCreator();
	              hm.put("id", p.getId());
	              hm.put("createDate", p.getCreateDate());
	              hm.put("modifyDate", p.getModifyDate());
	              hm.put("order", p.getOrder());
	              hm.put("title", p.getTitle());
	              hm.put("img", p.getImg());
	              hm.put("link", p.getLink());
	              hm.put("type", p.getType());
	              hm.put("targetId", p.getTargetId());
	              hm.put("state", p.getState());
	              hm.put("startTime", p.getStartTime());
	              hm.put("endTime", p.getEndTime());
	              hm.put("pvPrice",p.getPvPrice());
	              hm.put("uvPrice", p.getUvPrice());
	              if (p.getCreator() != null) {
	                hm.put("creator", p.getCreator().getName());
	              }
	          	/** 当前用户对本条数据的操作权限 */
	    			if(creater.equals(admin)){
	    				if(co == CheckedOperation.view) {
	    					hm.put("privilege", "view");
	    				} else {
	    					hm.put("privilege", "edit");
	    				}
	    			} else{
	    				if(co == CheckedOperation.edit) {
	    					hm.put("privilege", "edit");
	    				} else {
	    					hm.put("privilege", "view");
	    				}
	    			}
	              lhm.add(hm);
	            }
	          }
	          result.put("pageSize", Integer.valueOf(page.getPageSize()));
	          result.put("pageNumber", Integer.valueOf(page.getPageNumber()));
	          result.put("total", Long.valueOf(page.getTotal()));
	          result.put("rows", lhm);
	          j.setSuccess(true);
	          j.setMsg("查询成功");
	          j.setObj(result);
	          return j;
	        }
	        catch (Exception e)
	        {
	          j.setSuccess(false);
	          j.setMsg(e.getMessage());
	        }
	        return j;
	}
	
	@RequestMapping(value="/add")
	public Json add(BusinessBanner businessBanner){
		Json j = new Json();
		try {
			businessBanner.setType(BannerType.广告);
			businessBannerService.save(businessBanner);
			j.setSuccess(true);
			j.setMsg("添加成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="/edit")
	public Json edit(BusinessBanner businessBanner){
		Json j = new Json();
		try {
			businessBannerService.update(businessBanner, "type", "targetId", "startTime", "endTime",
										"pvPrice", "uvPrice", "creator", "createDate");
			j.setSuccess(true);
			j.setMsg("编辑成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	
	@RequestMapping(value="/detail/view")
	public Json detail(Long id){
		Json j = new Json();
		try {
			BusinessBanner banner = businessBannerService.find(id);
			if(banner == null) {
				j.setMsg("查询不存在");
				j.setSuccess(false);
				return j;
			}
			j.setSuccess(true);
			j.setMsg("查看成功");
			j.setObj(banner);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
