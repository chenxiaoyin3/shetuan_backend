package com.hongyu.controller;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessSystemSetting;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyRole;
import com.hongyu.entity.Provider;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.BusinessSystemSettingService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.ProviderService;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"admin/business/provider"})
public class ProviderController
{
  public static final int PROVIDER_TYPE_NORMAL = 0;
  public static final int PROVIDER_TYPE_SETTLE_IN = 1;
  public static final int BANK_ACCOUNT_TYPE_PUBLIC = 0;
  public static final int BANK_ACCOUNT_TYPE_PRIVATE = 1;
  @Resource(name="providerServiceImpl")
  ProviderService providerServiceImpl;
  @Resource(name="hyAdminServiceImpl")
  private HyAdminService hyAdminService;
  @Resource(name="hyRoleServiceImpl")
  private HyRoleService hyRoleService;
  
  @RequestMapping(value={"/add"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Json providerAdd(Provider provider, String username, String accountmobile, String accountwechat, String accountname, Long roleid, String accountaddress, HttpServletRequest request, HttpSession session)
  {
    Json json = new Json();
    String user = (String)session.getAttribute("principal");
    HyAdmin admin = (HyAdmin)hyAdminService.find(user);
    provider.setOperator(admin);
    try
    {
      HyAdmin provideraccount = new HyAdmin();
      provideraccount.setAddress(accountaddress);
      HyAdmin duplicatedAccount = hyAdminService.find(username);
      if (duplicatedAccount != null) {
    	  json.setSuccess(false);
          json.setMsg("用户名已存在");
          json.setObj(null);
          return json;
      }
      provideraccount.setUsername(username);
      provideraccount.setMobile(accountmobile);
      provideraccount.setWechat(accountwechat);
      provideraccount.setName(accountname);
      provideraccount.setRole((HyRole)this.hyRoleService.find(roleid));
      provideraccount.setDepartment(admin.getDepartment());
      
      Date now = new Date();
      if (now.compareTo(provider.getStartTime()) >= 0 && now.compareTo(provider.getEndTime()) < 0) {
    	  provider.setState(true);
      } else {
    	  provider.setState(false);
      }
      
      provider.setAccount(provideraccount);
      providerServiceImpl.save(provider);
      json.setSuccess(true);
      json.setMsg("添加成功");
      json.setObj(null);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("添加失败");
      json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping(value={"/page/view"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Json providerPage(Pageable pageable, Provider provider, HttpSession session, HttpServletRequest request)
  {
    Json json = new Json();
    
    /**
	 * 获取当前用户
	 */
	String username = (String) session.getAttribute(CommonAttributes.Principal);
	HyAdmin admin = hyAdminService.find(username);
    
	/** 
	 * 获取用户权限范围
	 */
	CheckedOperation co = (CheckedOperation) request.getAttribute("co");
	
    try
    {
      List<Filter> filters = new ArrayList();
      if (StringUtils.isNotBlank(provider.getProviderName()))
      {
        filters.add(new Filter("providerName", Filter.Operator.like, provider.getProviderName()));
        provider.setProviderName(null);
      }
      if (StringUtils.isNotBlank(provider.getContactorName()))
      {
        filters.add(new Filter("contactorName", Filter.Operator.like, provider.getContactorName()));
        provider.setContactorName(null);
      }
      pageable.setFilters(filters);
      List<Order> orders = new ArrayList<Order>();
      orders.add(Order.desc("id"));
      pageable.setOrders(orders);
      Page<Provider> page = this.providerServiceImpl.findPage(pageable, provider);
      
      List<Provider> providers = page.getRows();
      if (providers.size() == 0) {
    	  Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(new ArrayList<Map<String, Object>>(), 0, pageable);
    	  json.setSuccess(true);
          json.setMsg("查询成功");
          json.setObj(pageMap);
          return json;
      }
      
      List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
      for (Provider p : providers) {
    	  Map<String, Object> map = new HashMap<String, Object>();
    	  map.put("id", p.getId());
    	  map.put("providerType", p.getProviderType());
    	  map.put("providerName", p.getProviderName());
    	  map.put("address", p.getAddress());
    	  map.put("postcode", p.getPostcode());
    	  map.put("introduction", p.getIntroduction());
    	  map.put("isContracted", p.getIsContracted());
    	  map.put("contractNumber", p.getContractNumber());
    	  map.put("startTime", p.getStartTime());
    	  map.put("endTime", p.getEndTime());
    	  map.put("state", p.getState());
    	  map.put("contactorName", p.getContactorName());
    	  map.put("contactorMobile", p.getContactorMobile());
    	  map.put("contactorEmail", p.getContactorEmail());
    	  map.put("contactorWechat", p.getContactorWechat());
    	  map.put("contactorQq", p.getContactorQq());
    	  map.put("contactorPostcode", p.getContactorPostcode());
    	  map.put("bankName", p.getBankName());
    	  map.put("accountName", p.getAccountName());
    	  map.put("bankAccount", p.getBankAccount());
    	  map.put("accountType", p.getAccountType());
    	  map.put("remark", p.getRemark());
    	  map.put("bankCode", p.getBankCode());
    	  map.put("createTime", p.getCreateTime());
    	  map.put("modifyTime", p.getModifyTime());
    	  map.put("cancelTime", p.getCancelTime());
    	  map.put("account", p.getAccount());
    	  map.put("operatorName", p.getOperator().getName());
    	  
    	  HyAdmin operator = p.getOperator();
    	  
    	  /** 当前用户对本条数据的操作权限 */
  		  if(operator.equals(admin)){
  			  if(co == CheckedOperation.view) {
  				map.put("privilege", "view");
  			  } else {
  				map.put("privilege", "edit");
  			  }
  		  } else{
  			  if(co == CheckedOperation.edit) {
  				map.put("privilege", "edit");
  			  } else {
  				map.put("privilege", "view");
  			  }
  		  }
  		  
  		  list.add(map);
      }
      
      Page<Map<String, Object>> pageMap = new Page<Map<String, Object>>(list, page.getTotal(), pageable);
      json.setSuccess(true);
      json.setMsg("查询成功");
      json.setObj(pageMap);
    }
    catch (Exception e)
    { 
      e.printStackTrace();
      json.setSuccess(false);
      json.setMsg("查询失败");
      json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping(value={"/modify"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
  @ResponseBody
  public Json providerModify(Provider provider, String accountmobile, String accountwechat, String accountname, String accountaddress, HttpSession session)
  {
    Json json = new Json();
    Provider old = (Provider)this.providerServiceImpl.find(provider.getId());
    String user = (String)session.getAttribute("principal");
    HyAdmin admin = (HyAdmin)this.hyAdminService.find(user);
    try
    {
      old.setProviderType(provider.getProviderType());
      old.setProviderName(provider.getProviderName());
      old.setAddress(provider.getAddress());
      old.setBankAccount(provider.getBankAccount());
      old.setBankCode(provider.getBankCode());
      old.setBankName(provider.getBankName());
      old.setContactorEmail(provider.getContactorEmail());
      old.setContactorMobile(provider.getContactorMobile());
      old.setContactorName(provider.getContactorName());
      old.setContactorPostcode(provider.getContactorPostcode());
      old.setContactorQq(provider.getContactorQq());
      old.setContractNumber(provider.getContractNumber());
      old.setContactorWechat(provider.getContactorWechat());
      old.setStartTime(provider.getStartTime());
      old.setEndTime(provider.getEndTime());
      old.setIntroduction(provider.getIntroduction());
      old.setIsContracted(provider.getIsContracted());
      old.setPostcode(provider.getPostcode());
      old.setRemark(provider.getRemark());
      
      Date now = new Date();
      if (now.compareTo(provider.getStartTime()) >= 0 && now.compareTo(provider.getEndTime()) < 0) {
    	  provider.setState(true);
      } else {
    	  provider.setState(false);
      }
      old.setAccountName(provider.getAccountName());
      old.setOperator(admin);
      old.setBalanceType(provider.getBalanceType());
      old.setBalanceDate(provider.getBalanceDate());
      old.getAccount().setAddress(accountaddress);
      old.getAccount().setMobile(accountmobile);
      old.getAccount().setName(accountname);
      old.getAccount().setWechat(accountwechat);
      
      this.providerServiceImpl.update(old);
      json.setSuccess(true);
      json.setMsg("修改成功");
      json.setObj(null);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("修改失败");
      json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping(value={"/detail/view"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Json providerDetail(Long id)
  {
    Json json = new Json();
    try
    {
      Provider provider = (Provider)this.providerServiceImpl.find(id);
      json.setSuccess(true);
      json.setMsg("查询成功");
      json.setObj(provider);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("查询失败");
      json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping(value={"/delete"})
  @ResponseBody
  public Json providerDelete(Long id)
  {
    Json json = new Json();
    try
    {
      Provider p = (Provider)this.providerServiceImpl.find(id);
      p.setState(Boolean.valueOf(false));
      this.providerServiceImpl.update(p);
      json.setSuccess(true);
      json.setMsg("删除成功");
      json.setObj(null);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("删除失败");
      json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping(value={"/lock"})
  @ResponseBody
  public Json providerLock(Long id)
  {
    Json json = new Json();
    try
    {
      Provider p = (Provider)this.providerServiceImpl.find(id);
      p.setState(Boolean.valueOf(false));
      this.providerServiceImpl.update(p);
      json.setSuccess(true);
      json.setMsg("锁定成功");
      json.setObj(null);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("锁定失败");
      json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping(value={"/unlock"})
  @ResponseBody
  public Json providerUnlock(Long id)
  {
    Json json = new Json();
    try
    {
      Provider p = (Provider)this.providerServiceImpl.find(id);
      p.setState(Boolean.valueOf(true));
      this.providerServiceImpl.update(p);
      json.setSuccess(true);
      json.setMsg("解锁成功");
      json.setObj(null);
    }
    catch (Exception e)
    {
      json.setSuccess(false);
      json.setMsg("解锁失败");
      json.setObj(null);
    }
    return json;
  }
  
  @RequestMapping(value={"/list/view"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  @ResponseBody
  public Json providerList()
  {
    Json json = new Json();
    try
    {
      List<Provider> list = this.providerServiceImpl.findAll();
      json.setSuccess(true);
      json.setMsg("查询成功");
      json.setObj(list);
    }
    catch (Exception e)
    {	
      e.printStackTrace();
      json.setSuccess(false);
      json.setMsg("查询失败");
      json.setObj(null);
    }
    return json;
  }
  
	@Resource(name="businessSystemSettingServiceImpl")
	BusinessSystemSettingService systemSettingSrv;
	/**
	 * 获取某一个系统参数
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/settings/detail/view", method = RequestMethod.GET)
	@ResponseBody
	public Json systemSettingDetail(String name) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.like("settingName", name));
			filters.add(Filter.eq("isValid", true));
			
			List<BusinessSystemSetting> list = systemSettingSrv.findList(null,filters,null);
			
			if(list==null || list.isEmpty()) {
				throw new Exception("没有有效的参数");
			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(list.get(0));
			return json;
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			json.setObj(e.getMessage());
		}
		return json;
	}
}
