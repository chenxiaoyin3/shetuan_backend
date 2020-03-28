/**
 * 供应商返利表
 */
package com.hongyu.controller.gsbing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyProviderRebate;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyProviderRebateService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.util.AuthorityUtils;

@RestController
@RequestMapping("admin/providerRebate/")
public class ProviderRebateController {

	@Resource(name = "hyProviderRebateServiceImpl")
	HyProviderRebateService hyProviderRebateService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;
	
	
	//供应商返利列表
	@RequestMapping("list/view")
	@ResponseBody
	public Json listview(Pageable pageable,HyProviderRebate hyProviderRebate,HttpSession session,HttpServletRequest request)
	{
		Json json=new Json();
		try {
			Map<String,Object> map=new HashMap<String,Object>();
			List<Map<String, Object>> list = new ArrayList<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			/** 
			 * 获取用户权限范围
			 */
			CheckedOperation co=(CheckedOperation) request.getAttribute("co");
			
			/** 所有符合条件的账号 ,默认可以看到自己创建的数据 */
			Set<HyAdmin> hyAdmins = AuthorityUtils.getAdmins(session, request);
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.in("creator", hyAdmins));
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
            orders.add(Order.desc("createTime")); //按创建时间倒序排列
			pageable.setOrders(orders);
			Page<HyProviderRebate> page=hyProviderRebateService.findPage(pageable,hyProviderRebate);
			if(page.getTotal()>0) {
				for(HyProviderRebate providerRebate:page.getRows()) {
					HyAdmin creator=providerRebate.getCreator();
					Map<String,Object> proMap=new HashMap<String,Object>();
					proMap.put("id", providerRebate.getId());
					proMap.put("providerName", providerRebate.getProviderName());
					proMap.put("contractNumber", providerRebate.getContractNumber());
					proMap.put("rebate", providerRebate.getRebate());
					proMap.put("bargainRebate", providerRebate.getBargainRebate());
					proMap.put("createTime", providerRebate.getCreateTime());
					
					  /** 当前用户对本条数据的操作权限 */
				    if(creator.equals(admin)){
				    	if(co==CheckedOperation.view){
				    		proMap.put("privilege", "view");
				    	}
				    	else{
				    		proMap.put("privilege", "edit");
				    	}
				    }
				    else{
				    	if(co==CheckedOperation.edit){
				    		proMap.put("privilege", "edit");
				    	}
				    	else{
				    		proMap.put("privilege", "view");
				    	}
				    }
				    list.add(proMap);
				}
			}
			map.put("rows", list);
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));
		    map.put("total",Long.valueOf(page.getTotal()));
		    json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//新建供应商返利
	@RequestMapping("add")
	@ResponseBody
	public Json add(HyProviderRebate hyProviderRebate,HttpSession session)
	{
		Json json=new Json();
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			hyProviderRebate.setCreator(admin);
			hyProviderRebate.setCreateTime(new Date());		
			String contractNumber=hyProviderRebate.getContractNumber();
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("contractCode", contractNumber)); //按合同编号查找合同
			List<HySupplierContract> supplierContracts=hySupplierContractService.findList(null,filters,null);
			HySupplierContract hySupplierContract=supplierContracts.get(0);
			Long supplierId=hySupplierContract.getHySupplier().getId();
			hyProviderRebate.setSupplierId(supplierId);
			hyProviderRebateService.save(hyProviderRebate);
			hySupplierContract.setIsRebate(true); //设置合同为已返利
			hySupplierContract.setRebate(hyProviderRebate.getRebate());
			hySupplierContract.setBargainRebate(hyProviderRebate.getBargainRebate());
			hySupplierContractService.update(hySupplierContract);
			
			//修改新建返利的供应商为VIP供应商,20190621 added
			HySupplier hySupplier=hySupplierContract.getHySupplier();
			hySupplier.setIsVip(true); //设置为VIP供应商
			hySupplierService.update(hySupplier);
			
			json.setSuccess(true);
			json.setMsg("添加成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//编辑供应商返利
	@RequestMapping("edit")
	@ResponseBody
	public Json edit(Long rebateId,BigDecimal rebate,BigDecimal bargainRebate)
	{
		Json json=new Json();
		try {
			HyProviderRebate hyProviderRebate=hyProviderRebateService.find(rebateId);
			hyProviderRebate.setRebate(rebate);
			hyProviderRebate.setBargainRebate(bargainRebate);
			hyProviderRebate.setModifyTime(new Date());
			hyProviderRebateService.update(hyProviderRebate);
			String contractNumber=hyProviderRebate.getContractNumber();
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("contractCode", contractNumber)); //按合同编号查找合同
			List<HySupplierContract> supplierContracts=hySupplierContractService.findList(null,filters,null);
			HySupplierContract hySupplierContract=supplierContracts.get(0);
			hySupplierContract.setRebate(rebate);
			hySupplierContract.setBargainRebate(bargainRebate);
			hySupplierContractService.update(hySupplierContract);
			json.setSuccess(true);
			json.setMsg("修改成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
		
		
	//供应商列表
	@RequestMapping("providerList")
	@ResponseBody
	public Json providerList(String supplierName,HttpSession session)
	{
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("operator", admin));
			filters.add(Filter.like("supplierName", supplierName));
			List<HySupplier> hySuppliers=hySupplierService.findList(null,filters,null);
			List<Map<String, Object>> list = new ArrayList<>();
			for(HySupplier hySupplier:hySuppliers) {
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("providerId", hySupplier.getId());
				map.put("providerName", hySupplier.getSupplierName());
				list.add(map);
			}
		    json.setSuccess(true);
		    json.setObj(list);;
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//合同下拉列表
	@RequestMapping("contractList")
	@ResponseBody
	public Json contractList(Long providerId)
	{
		Json json=new Json();
		try {
			HySupplier hySupplier=hySupplierService.find(providerId);
			List<HySupplierContract> supplierContracts=new ArrayList<>();
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("hySupplier", hySupplier));
			filters.add(Filter.isNull("isRebate")); //筛选返利为NULL的合同
			filters.add(Filter.eq("contractStatus", ContractStatus.zhengchang)); //筛选合同状态为正常的
			supplierContracts.addAll(hySupplierContractService.findList(null,filters,null));
			filters.clear();
			filters.add(Filter.eq("hySupplier", hySupplier));
			filters.add(Filter.eq("isRebate", false)); //筛选未返利的合同
			filters.add(Filter.eq("contractStatus", ContractStatus.zhengchang)); //筛选合同状态为正常的
			supplierContracts.addAll(hySupplierContractService.findList(null,filters,null));
			List<Map<String, Object>> list = new ArrayList<>();
			for(HySupplierContract contract:supplierContracts) {
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("contractNumber", contract.getContractCode());
				list.add(map);
			}
			json.setSuccess(true);
			json.setObj(list);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
}
