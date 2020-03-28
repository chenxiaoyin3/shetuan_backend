package com.hongyu.controller.wj;

import java.math.BigDecimal;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.shiro.crypto.hash.Hash;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.RequestToViewNameTranslator;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.Filter.Operator;
import com.hongyu.controller.YmmallSpecialtyController.MapComparator;
import com.hongyu.entity.BranchPayServicer;
import com.hongyu.entity.BranchPrePay;
import com.hongyu.entity.BranchPrePayDetail;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.service.BranchPrePayDetailService;
import com.hongyu.service.BranchPrePayService;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HySupplierElementService;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_2;

import oracle.net.aso.l;

/**
 * 冲抵记录
 * @author wj
 *
 */

@Controller
@RequestMapping("/admin")
public class OffsetRecordsController {
	
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "branchPrePayServiceImpl")
	BranchPrePayService branchPrePayService;
	
	@Resource(name = "hySupplierElementServiceImpl")
	HySupplierElementService hySupplierElementService;
	
	@Resource(name = "branchPrePayDetailServiceImpl")
	BranchPrePayDetailService branchPrePayDetailService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "branchPreSaveServiceImpl")
	BranchPreSaveService branchPreSaveService;
	
	/**
	 * 预付款的冲抵记录
	 * 列表页
	 * 计调中心
	 * @param session
	 * @return
	 */
	@RequestMapping("/branchprepay/offset/list") 
	@ResponseBody
	public Json list(Integer state,String supplierName,Pageable pageable,HttpSession session){
		Json json = new Json();
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		Long departmentId = hyAdminService.find(username).getDepartment().getId();
		try {
			List<Filter> filters  = new ArrayList<>();
			filters.add(Filter.eq("departmentId", departmentId));
			if(state == 0){  //已充清
				BigDecimal balance = new BigDecimal(0);
				filters.add(Filter.eq("prePayBalance", balance));
			}else if(state == 1){//未充清
				BigDecimal balance = new BigDecimal(0);
				filters.add(Filter.ne("prePayBalance", balance));
			}
			if(supplierName!=null){
				filters.add(Filter.like("servicerName", supplierName));
			}
		
			pageable.setFilters(filters);
			Page<BranchPrePay> branchPrePays = branchPrePayService.findPage(pageable);
			List<HashMap<String, Object>> res = new ArrayList<>();
			for(BranchPrePay branchPrePay:branchPrePays.getRows()){
				HashMap<String, Object> m = new HashMap<>();
				HySupplierElement hySupplierElement = hySupplierElementService.find(branchPrePay.getSupplierElementId());
				m.put("supplierType", hySupplierElement.getSupplierType().name());
				m.put("supplierName", branchPrePay.getServicerName());
				m.put("departmentName",branchPrePay.getDepartmentName());
				m.put("balance", branchPrePay.getPrePayBalance());
				m.put("branchPrePayId", branchPrePay.getId());
				m.put("supplierTypeNum", hySupplierElement.getSupplierType().ordinal());
				res.add(m);
			}		
			Collections.sort(res, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Integer id1 = (Integer) o1.get("supplierTypeNum");
					Integer id2 = (Integer) o2.get("supplierTypeNum");
					return id2 < id1 ? 1 : -1;
				}
			});
			
			Map<String, Object> hMap=new HashMap<>();
			hMap.put("total", branchPrePays.getTotal());
			hMap.put("pageNumber", branchPrePays.getPageNumber());
			hMap.put("pageSize", branchPrePays.getPageSize());
			hMap.put("rows", res);

			json.setMsg("操作成功");
			json.setObj(hMap);
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("操作失败");
			json.setSuccess(false);
		}
		return json;
		
	}
	
	/**
	 * 预付款冲抵记录详情
	 * 计调中心
	 */
	@RequestMapping("/branchprepay/offset/detail")
	@ResponseBody
	public Json offsetdetail(Long id){
		Json json = new Json();
		try {
			List<HashMap<String, Object>> res1 = new ArrayList<>();
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("branchPrePayId", id));
			filters.add(Filter.eq("type", 1));
			List<BranchPrePayDetail> branchPrePayDetails = branchPrePayDetailService.findList(null,filters,null);
			for(BranchPrePayDetail branchPrePayDetail:branchPrePayDetails){
				HashMap<String, Object> map = new HashMap<>();
				map.put("appliname",branchPrePayDetail.getAppliname());
				map.put("paydate", branchPrePayDetail.getDate());
				map.put("remark", branchPrePayDetail.getRemark());
				map.put("amount", branchPrePayDetail.getAmount());
				res1.add(map);
			}
			
			filters.clear();
			filters.add(Filter.eq("branchPrePayId", id));
			filters.add(Filter.eq("type", 2));
			List<HashMap<String, Object>> res2 = new ArrayList<>();
			List<BranchPrePayDetail> branchPrePayDetails2 = branchPrePayDetailService.findList(null,filters,null);
			for(BranchPrePayDetail branchPrePayDetail:branchPrePayDetails2){
				HashMap<String,Object> map = new HashMap<>();
				map.put("date", branchPrePayDetail.getDate());
				map.put("appliname", hyAdminService.find(branchPrePayDetail.getAppliname()).getName());
				map.put("amount", branchPrePayDetail.getAmount());
				if(branchPrePayDetail.getGroupId()!=null){
					HyGroup group = hyGroupService.find(branchPrePayDetail.getGroupId());
					map.put("productId", group.getGroupLinePn());
					map.put("startDate", group.getStartDay());
				}
				res2.add(map);
			}
			
			HashMap<String, Object> res = new HashMap<>();
			res.put("chongzhi", res1);
			res.put("dikou", res2);
			json.setObj(res);
			
			json.setMsg("操作成功");
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("操作失败");
			json.setSuccess(false);
		}
		return json;
	}

	/**
	 * 预付款冲抵记录
	 * 总公司财务部
	 */
	@RequestMapping("/branchprepayoffset/list")  
	@ResponseBody
	public Json lists(Integer state,String supplierName,Pageable pageable,HttpSession session){
		Json json = new Json();
		
		try {
			List<Filter> filters  = new ArrayList<>();
			if(state == 0){  //已充清
				BigDecimal balance = new BigDecimal(0);
				filters.add(Filter.eq("prePayBalance", balance));
			}else if(state == 1){//未充清
				BigDecimal balance = new BigDecimal(0);
				filters.add(Filter.ne("prePayBalance", balance));
			}
			if(supplierName!=null){
				filters.add(Filter.like("servicerName", supplierName));
			}
			pageable.setFilters(filters);
			Page<BranchPrePay> branchPrePays = branchPrePayService.findPage(pageable);
			List<HashMap<String, Object>> res = new ArrayList<>();
			for(BranchPrePay branchPrePay:branchPrePays.getRows()){
				HashMap<String, Object> m = new HashMap<>();
				HySupplierElement hySupplierElement = hySupplierElementService.find(branchPrePay.getSupplierElementId());
				m.put("supplierType", hySupplierElement.getSupplierType().name());
				m.put("supplierName", branchPrePay.getServicerName());
				m.put("departmentName",branchPrePay.getDepartmentName());
				m.put("balance", branchPrePay.getPrePayBalance());
				m.put("branchPrePayId", branchPrePay.getId());
				m.put("supplierTypeNum", hySupplierElement.getSupplierType().ordinal());
				res.add(m);
			}		
			Collections.sort(res, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> o1, Map<String, Object> o2) {
					Integer id1 = (Integer) o1.get("supplierTypeNum");
					Integer id2 = (Integer) o2.get("supplierTypeNum");
					return id2 < id1 ? 1 : -1;
				}
			});
			Map<String, Object> hMap=new HashMap<>();
			hMap.put("total", branchPrePays.getTotal());
			hMap.put("pageNumber", branchPrePays.getPageNumber());
			hMap.put("pageSize", branchPrePays.getPageSize());
			hMap.put("rows", res);

			json.setMsg("操作成功");
			json.setObj(hMap);
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("操作失败");
			json.setSuccess(false);
		}
		return json;
		
	}
	
	/** 预付款冲抵记录详情
	 * 总公司财务
	 */
	@RequestMapping("/branchprepayoffset/detail")
	@ResponseBody
	public Json companyoffsetdetail(Long id){
		Json json = new Json();
		try {
			List<HashMap<String, Object>> res1 = new ArrayList<>();
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("branchPrePayId", id));
			filters.add(Filter.eq("type", 1));
			List<BranchPrePayDetail> branchPrePayDetails = branchPrePayDetailService.findList(null,filters,null);
			for(BranchPrePayDetail branchPrePayDetail:branchPrePayDetails){
				HashMap<String, Object> map = new HashMap<>();
				map.put("appliname",branchPrePayDetail.getAppliname());
				map.put("paydate", branchPrePayDetail.getDate());
				map.put("remark", branchPrePayDetail.getRemark());
				map.put("amount", branchPrePayDetail.getAmount());
				res1.add(map);
			}
			
			filters.clear();
			filters.add(Filter.eq("branchPrePayId", id));
			filters.add(Filter.eq("type", 2));
			List<HashMap<String, Object>> res2 = new ArrayList<>();
			List<BranchPrePayDetail> branchPrePayDetails2 = branchPrePayDetailService.findList(null,filters,null);
			for(BranchPrePayDetail branchPrePayDetail:branchPrePayDetails2){
				HashMap<String,Object> map = new HashMap<>();
				map.put("date", branchPrePayDetail.getDate());
				map.put("appliname", hyAdminService.find(branchPrePayDetail.getAppliname()).getName());
				map.put("amount", branchPrePayDetail.getAmount());
				if(branchPrePayDetail.getGroupId()!=null){
					HyGroup group = hyGroupService.find(branchPrePayDetail.getGroupId());
					map.put("productId", group.getGroupLinePn());
					map.put("startDate", group.getStartDay());
				}
				res2.add(map);
			}
			
			HashMap<String, Object> res = new HashMap<>();
			res.put("chongzhi", res1);
			res.put("dikou", res2);
			json.setObj(res);
			
			json.setMsg("操作成功");
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("操作失败");
			json.setSuccess(false);
		}
		return json;
	}
	
	
//	/**
//	 * 分公司充值
//	 * 冲抵记录
//	 */
////	@RequestMapping("/branchpresave/offsetlist")
//	@ResponseBody
//	public Json prepayoffsetlist(Pageable page,String branchName,
//			String startTime, String endTime,HttpSession session){
//		Json json = new Json();
//		try {
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			String treePath = hyAdminService.find(username).getDepartment().getTreePath();
//			String[] strings = treePath.split(",");
//			List<Filter> filters = new ArrayList<>();
//			if (strings.length!=2 ){  //分公司财务
//				Long branchId = Long.parseLong(strings[2]);
//				filters.add(Filter.eq("branchId", branchId));
//			}
//			
//			if(branchName!=null&&!branchName.equals(""))
//				filters.add(Filter.eq("branchName",branchName));
//			
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			if (startTime != null && !startTime.equals(""))
//				filters.add(new Filter("date", Operator.ge,
//						sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
//			if (endTime != null && !endTime.equals(""))
//				filters.add(
//						new Filter("date", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
//			page.setFilters(filters);
//			
//			List<Order> orders = new ArrayList<>();
//			orders.add(Order.desc("date"));
//			page.setOrders(orders);
//			
//			Page<BranchPreSave> branchPreSaves = branchPreSaveService.findPage(page);
//			List<HashMap<String, Object>> res = new ArrayList<>();
//			for(BranchPreSave branchPreSave : branchPreSaves.getRows() ){
//				HashMap<String, Object> map = new HashMap<>();
//				map.put("branchName", branchPreSave.getBranchName());
//				if( branchPreSave.getType() == 2){
//					map.put("departmentName", branchPreSave.getDepartmentName());
//				}
//				map.put("type", branchPreSave.getType());
//				map.put("date", branchPreSave.getDate());
//				map.put("amount", branchPreSave.getAmount());
//				map.put("balance", branchPreSave.getPreSaveBalance());
//				map.put("remark", branchPreSave.getRemark());
//				res.add(map);
//			}
//			HashMap<String,Object> ans= new HashMap<>();
//			ans.put("total", branchPreSaves.getTotal());
//			ans.put("pageSize", branchPreSaves.getPageSize());
//			ans.put("pageNumger",branchPreSaves.getPageNumber());
//			ans.put("rows", branchPreSaves.getRows());
//			json.setObj(ans);
//			json.setMsg("操作成功");
//			json.setSuccess(true);
//		} catch (Exception e) {
//			// TODO: handle exception
//			json.setMsg("操作失败");
//			json.setSuccess(false);
//		}
//		return json;
//	}
//	
//	/**
//	 * 返回登陆是分公司还是总公司会计
//	 */
//	@RequestMapping("/branchpresave/offsetjudge")
//	@ResponseBody
//	public Json companyorbranch(HttpSession session){
//		Json json = new Json();
//		try {
//			String username = (String) session.getAttribute(CommonAttributes.Principal);
//			String treePath = hyAdminService.find(username).getDepartment().getTreePath();
//			String[] strings = treePath.split(",");
//			List<Filter> filters = new ArrayList<>();
//			if (strings.length!=2){ //分公司
//				json.setObj(false);
//			}else{                   //总公司
//				json.setObj(true);
//			}
//			json.setMsg("获取成功");
//			json.setSuccess(true);
//		} catch (Exception e) {
//			// TODO: handle exception
//			json.setMsg("获取失败");
//			json.setSuccess(false);
//		}
//		return json;
//	}
	
	
	
}
