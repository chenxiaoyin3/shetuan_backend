package com.hongyu.controller.liyang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.controller.BaseController;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.controller.liyang.InsuranceStatisticsController.Monthly;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.InsuranceOrder;
import com.hongyu.entity.Store;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.StoreService;
import com.hongyu.util.DateUtil;
/**
 * 保险月明细表
 * @author liyang
 *
 */
@Controller
@RequestMapping("/admin/insuranceMonthlyStatistics/")
public class InsuranceMonthlyStatisticController {
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "insuranceServiceImpl")
	InsuranceService insuranceService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	@Resource(name = "hyAreaServiceImpl")
	HyAreaService hyAreaService;
	
	BaseController baseController = new BaseController();
	
	public static class Monthly{
		private String month;
		private String area = "";
		private String storeType = "";
		private BigDecimal tqReceivedMoney = new BigDecimal(0);
		private BigDecimal zzReceivedMoney = new BigDecimal(0);
		private BigDecimal wsReceivedMoney = new BigDecimal(0);
		private BigDecimal receivedSum = new BigDecimal(0);
		private BigDecimal paySum = new BigDecimal(0);
		private BigDecimal profitSum = new BigDecimal(0);
		public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public BigDecimal getTqReceivedMoney() {
			return tqReceivedMoney;
		}
		public void setTqReceivedMoney(BigDecimal tqReceivedMoney) {
			this.tqReceivedMoney = tqReceivedMoney;
		}
		public BigDecimal getZzReceivedMoney() {
			return zzReceivedMoney;
		}
		public void setZzReceivedMoney(BigDecimal zzReceivedMoney) {
			this.zzReceivedMoney = zzReceivedMoney;
		}
		public BigDecimal getWsReceivedMoney() {
			return wsReceivedMoney;
		}
		public void setWsReceivedMoney(BigDecimal wsReceivedMoney) {
			this.wsReceivedMoney = wsReceivedMoney;
		}
		public BigDecimal getReceivedSum() {
			return receivedSum;
		}
		public void setReceivedSum(BigDecimal receivedSum) {
			this.receivedSum = receivedSum;
		}
		public BigDecimal getPaySum() {
			return paySum;
		}
		public void setPaySum(BigDecimal paySum) {
			this.paySum = paySum;
		}
		public BigDecimal getProfitSum() {
			return profitSum;
		}
		public void setProfitSum(BigDecimal profitSum) {
			this.profitSum = profitSum;
		}
		public String getArea() {
			return area;
		}
		public void setArea(String area) {
			this.area = area;
		}
		public String getStoreType() {
			return storeType;
		}
		public void setStoreType(String storeType) {
			this.storeType = storeType;
		}
		
	}
	@RequestMapping("monthly/view")
	@ResponseBody
	public Json insuranceMonthly22(@RequestParam(required=true) @DateTimeFormat(pattern="yyyy-MM") Date startTime,
			@RequestParam(required=true) @DateTimeFormat(pattern="yyyy-MM") Date endTime,
			Long areaId,Integer storeType){
		Json json = new Json();
		try {
			DateFormat dFormat = new SimpleDateFormat("yyyy-MM");
			String st = "2000-01";
			String et = "3000-01";
			if(startTime!=null){
				st = dFormat.format(startTime);
			}
			if(endTime!=null){
				et = dFormat.format(endTime);
			}
			List<HashMap<String, Object>> result = new ArrayList<>();
			
			String areaName = "";
			String typeName = "";
			
			//String sql_select = "select sum(io.received_money),sum(io.shifu_money),sum(io.profit) ";
			String sql_select = "select io.received_money,io.shifu_money,io.profit ";
			String sql_from = "from hy_insurance_order as io ";

			String conditions = "";
			if (areaId != null || storeType != null) {
				sql_from += ",hy_order as o,hy_store as s ";
				conditions += " and io.order_id = o.id and o.store_id = s.id ";
			}
			if (areaId != null && areaId > 0) {
				conditions += " and s.area_id = "+areaId+" ";
				
				HyArea hyArea = hyAreaService.find(areaId);
				if (hyArea != null && hyArea.getFullName() != null) {
					areaName = hyArea.getFullName();
				}
			}
			if (storeType != null) {
				conditions += "and s.type = "+storeType+" ";
				if (storeType == 0 ) {
					typeName = "虹宇门店";
				}else{
					typeName = "非虹宇门店";
				}
			}
			while(st.compareTo(et)<=0){
				HashMap<String, Object> map = new HashMap<>();
				map.put("month", st);
				String sql_where = "where date_format(io.insured_time,'%Y-%m')='"+st+"' ";
				
				String tq_where = sql_where + " and io.type=0 and io.status=3 "+conditions;
				String tq_real_sql = sql_select + sql_from + tq_where;
				//System.out.println("tq_real_sq = "+tq_real_sql );
				
				//求团期投保总额
				List<Object[]> tqList = insuranceOrderService.statis(tq_real_sql);
				BigDecimal tqReceivedMoney = new BigDecimal(0);
				BigDecimal tqPayMoney = new BigDecimal(0);
				BigDecimal tqProfitMoney = new BigDecimal(0);
				if(!tqList.isEmpty() && tqList.size()>0 && tqList.get(0)!=null){
					for(Object[] tmp:tqList){
						tqReceivedMoney = tqReceivedMoney.add((BigDecimal)tmp[0]);
						tqPayMoney = tqPayMoney.add((BigDecimal)tmp[1]);
						tqProfitMoney = tqProfitMoney.add((BigDecimal)tmp[2]);
					}
				}
				
				//求自主投保总额
				String zz_where = sql_where + " and io.type=1 and io.status=3 "+conditions;
				String zz_real_sql = sql_select + sql_from + zz_where;
				//System.out.println("zz_real_sq = "+zz_real_sql );
				List<Object[]> zzList = insuranceOrderService.statis(zz_real_sql);
				BigDecimal zzReceivedMoney = new BigDecimal(0);
				BigDecimal zzPayMoney = new BigDecimal(0);
				BigDecimal zzProfitMoney = new BigDecimal(0);
				if(zzList != null && zzList.size()>0 && zzList.get(0)!=null){
					for(Object[] tmp:zzList){
						zzReceivedMoney = zzReceivedMoney.add((BigDecimal)tmp[0]);
						zzPayMoney = zzPayMoney.add((BigDecimal)tmp[1]);
						zzProfitMoney = zzProfitMoney.add((BigDecimal)tmp[2]);
					}
				}	
			
				//求网上投保总额
				String ws_where = sql_where + " and io.type=2 and io.status=3 "+conditions;
				String ws_real_sql = sql_select + sql_from + ws_where;
				//System.out.println("ws_real_sql = "+ws_real_sql );
				
				List<Object[]> wsList = insuranceOrderService.statis(ws_real_sql);
				BigDecimal wsReceivedMoney = new BigDecimal(0);
				BigDecimal wsPayMoney = new BigDecimal(0);
				BigDecimal wsProfitMoney = new BigDecimal(0);
				//如果关于门店的筛选条件不为空，那就统计官网的，否则，不统计
				if (areaId != null || storeType != null) {
					if(!wsList.isEmpty() && wsList.size()>0 && wsList.get(0)!=null){
						for(Object[] tmp:wsList){
							wsReceivedMoney = wsReceivedMoney.add((BigDecimal)tmp[0]);
							wsPayMoney = wsPayMoney.add((BigDecimal)tmp[1]);
							wsProfitMoney = wsProfitMoney.add((BigDecimal)tmp[2]);
						}
					}
				}

				map.put("tqReceivedSum", tqReceivedMoney);
				map.put("zzReceivedSum", zzReceivedMoney);
				map.put("wsReceivedSum", wsReceivedMoney);
				map.put("area", areaName);
				map.put("storyType", typeName);
				map.put("receivedSum", tqReceivedMoney.add(zzReceivedMoney).add(wsReceivedMoney));
				map.put("paySum", tqPayMoney.add(zzPayMoney).add(wsPayMoney));
				map.put("profitSum", tqProfitMoney.add(zzProfitMoney).add(wsProfitMoney));
				result.add(map);
				st = DateUtil.getNextMonth(st);
			}
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setMsg("查询失败："+e.getMessage());
			json.setSuccess(false);
		}
		return json;
	}
	@RequestMapping("monthlyToExcel/view")
	public void monthlyToExcel2(@RequestParam(required=true) @DateTimeFormat(pattern="yyyy-MM") Date startTime,@RequestParam(required=true) @DateTimeFormat(pattern="yyyy-MM") Date endTime,
							HttpServletResponse response,HttpServletRequest request,
							Long areaId,Integer storeType){
		try {
			DateFormat dFormat = new SimpleDateFormat("yyyy-MM");
			String st = "2000-01";
			String et = "3000-01";
			if(startTime!=null){
				st = dFormat.format(startTime);
			}
			if(endTime!=null){
				et = dFormat.format(endTime);
			}
			List<Monthly> result = new ArrayList<>();
			
			String areaName = "";
			String typeName = "";
			
			//String sql_select = "select sum(io.received_money),sum(io.shifu_money),sum(io.profit) ";
			String sql_select = "select io.received_money,io.shifu_money,io.profit ";
			String sql_from = "from hy_insurance_order as io ";

			String conditions = "";
			if (areaId != null || storeType != null) {
				sql_from += ",hy_order as o,hy_store as s ";
				conditions += " and io.order_id = o.id and o.store_id = s.id ";
			}
			if (areaId != null && areaId > 0) {
				conditions += " and s.area_id = "+areaId+" ";
				
				HyArea hyArea = hyAreaService.find(areaId);
				if (hyArea != null && hyArea.getFullName() != null) {
					areaName = hyArea.getFullName();
				}
			}
			if (storeType != null) {
				conditions += "and s.type = "+storeType+" ";
				if (storeType == 0 ) {
					typeName = "虹宇门店";
				}else{
					typeName = "非虹宇门店";
				}
			}
			while(st.compareTo(et)<=0){
				Monthly monthly = new Monthly();
				monthly.setMonth(st);
				String sql_where = "where date_format(io.insured_time,'%Y-%m')='"+st+"' ";
				
				String tq_where = sql_where + " and io.type=0 and io.status=3 "+conditions;
				String tq_real_sql = sql_select + sql_from + tq_where;
				//System.out.println("tq_real_sq = "+tq_real_sql );
				
				//求团期投保总额
				List<Object[]> tqList = insuranceOrderService.statis(tq_real_sql);
				BigDecimal tqReceivedMoney = new BigDecimal(0);
				BigDecimal tqPayMoney = new BigDecimal(0);
				BigDecimal tqProfitMoney = new BigDecimal(0);
				if(!tqList.isEmpty() && tqList.size()>0 && tqList.get(0)!=null){
					for(Object[] tmp:tqList){
						tqReceivedMoney = tqReceivedMoney.add((BigDecimal)tmp[0]);
						tqPayMoney = tqPayMoney.add((BigDecimal)tmp[1]);
						tqProfitMoney = tqProfitMoney.add((BigDecimal)tmp[2]);
					}
				}
				
				//求自主投保总额
				String zz_where = sql_where + " and io.type=1 and io.status=3 "+conditions;
				String zz_real_sql = sql_select + sql_from + zz_where;
				//System.out.println("zz_real_sq = "+zz_real_sql );
				List<Object[]> zzList = insuranceOrderService.statis(zz_real_sql);
				BigDecimal zzReceivedMoney = new BigDecimal(0);
				BigDecimal zzPayMoney = new BigDecimal(0);
				BigDecimal zzProfitMoney = new BigDecimal(0);
				if(zzList != null && zzList.size()>0 && zzList.get(0)!=null){
					for(Object[] tmp:zzList){
						zzReceivedMoney = zzReceivedMoney.add((BigDecimal)tmp[0]);
						zzPayMoney = zzPayMoney.add((BigDecimal)tmp[1]);
						zzProfitMoney = zzProfitMoney.add((BigDecimal)tmp[2]);
					}
				}	
			
				//求网上投保总额
				String ws_where = sql_where + " and io.type=2 and io.status=3 "+conditions;
				String ws_real_sql = sql_select + sql_from + ws_where;
				//System.out.println("ws_real_sql = "+ws_real_sql );
				
				List<Object[]> wsList = insuranceOrderService.statis(ws_real_sql);
				BigDecimal wsReceivedMoney = new BigDecimal(0);
				BigDecimal wsPayMoney = new BigDecimal(0);
				BigDecimal wsProfitMoney = new BigDecimal(0);
				//如果关于门店的筛选条件不为空，那就统计官网的，否则，不统计
				if (areaId != null || storeType != null) {
					if(!wsList.isEmpty() && wsList.size()>0 && wsList.get(0)!=null){
						for(Object[] tmp:wsList){
							wsReceivedMoney = wsReceivedMoney.add((BigDecimal)tmp[0]);
							wsPayMoney = wsPayMoney.add((BigDecimal)tmp[1]);
							wsProfitMoney = wsProfitMoney.add((BigDecimal)tmp[2]);
						}
					}
				}

				monthly.setTqReceivedMoney(tqReceivedMoney);
				monthly.setZzReceivedMoney(zzReceivedMoney);
				monthly.setWsReceivedMoney(wsReceivedMoney);
				monthly.setArea(areaName);
				monthly.setStoreType(typeName);
				monthly.setReceivedSum(tqReceivedMoney.add(zzReceivedMoney).add(wsReceivedMoney));
				monthly.setPaySum(tqPayMoney.add(zzPayMoney).add(wsPayMoney));
				monthly.setProfitSum(tqProfitMoney.add(zzProfitMoney).add(wsProfitMoney));
				result.add(monthly);
				st = DateUtil.getNextMonth(st);
			}
			StringBuilder title = new StringBuilder("保险月汇总表");
			title.append("("+dFormat.format(startTime)+" ~ "+dFormat.format(endTime)+")");
			baseController.export2Excel(request, response, result, "保险月汇总统计表.xls", title.toString(), "insuranceOrderMonthly.xml");	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("storeList")
	@ResponseBody
	public Json getStoreList(HttpSession session){
		Json json = new Json();
		try {
			//获取用户
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			String jpql = "select id,store_name from hy_store"; 
			List<Object[]> stores = storeService.statis(jpql);
			List<HashMap<String, Object>> result = new ArrayList<>();
			for(Object[] tmp:stores){
				BigInteger storeId = (BigInteger)tmp[0];
				if(admin.getRole().getName().contains("门店员工") || admin.getRole().getName().contains("门店经理")){
					if(admin.getDepartment().getStore().getId()!=storeId.longValue())
						continue;
				}
				HashMap<String, Object> hMap = new HashMap<>();
				hMap.put("id", (BigInteger)tmp[0]);
				hMap.put("storeName", (String)tmp[1]);
				result.add(hMap);
			}

			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}
	@RequestMapping("employeeList")
	@ResponseBody
	public Json getEmployeeList(Long storeId){
		Json json = new Json();
		try {
			Store store = storeService.find(storeId);
			Set<HyAdmin> admins = store.getDepartment().getHyAdmins();
			List<HashMap<String, Object>> result = new ArrayList<>();
			for(HyAdmin admin:admins){
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("username", admin.getUsername());
				hm.put("employeeName", admin.getName());
				result.add(hm);
			}
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}
	@RequestMapping("customerList")
	@ResponseBody
	public Json getCustomerList(Long orderId){
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderId", orderId));
			InsuranceOrder insuranceOrder = insuranceOrderService.findList(null,filters,null).get(0);
			
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(insuranceOrder.getPolicyHolders());
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}
	/**
	 * 获取区域  级联框 （第一级 id=0查询）
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/areacomboxlist/view")
	@ResponseBody
	public Json getSubAreas(Long id) {
		Json j = new Json();
		try {
			HashMap<String, Object> hashMap = new HashMap<>();
			HyArea parent = hyAreaService.find(id);
			List<HashMap<String, Object>> obj = new ArrayList<>();
			if (parent != null && parent.getHyAreas().size() > 0) {
				for (HyArea child : parent.getHyAreas()) {
					if (child.getStatus()) {
						HashMap<String, Object> hm = new HashMap<>();
						hm.put("value", child.getId());
						hm.put("label", child.getName());
						hm.put("isLeaf", child.getHyAreas().size() == 0);
						obj.add(hm);
					}
				}
			}
			hashMap.put("total", parent.getHyAreas().size());
			hashMap.put("data", obj);
			j.setSuccess(true);
			j.setMsg("查找成功！");
			j.setObj(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return j;
	}
	@RequestMapping("supplierList")
	@ResponseBody
	public Json getSupplierList(){
		Json json = new Json();
		try {
			String sql = "select id, supplier_name from hy_supplier";
			List<Object[]> list = hySupplierService.statis(sql);
			
			List<HashMap<String, Object>> result = new ArrayList<>();
			for(Object[] tmp:list){
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", tmp[0]);
				map.put("name", tmp[1]);
				result.add(map);
			}
			
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}
	@RequestMapping("permission")
	@ResponseBody
	public Json getPermission(HttpSession session,HttpServletRequest request){
		Json json = new Json();
		try {
			//获取用户
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			HashMap<String, Object> result = new HashMap<>();
			if(admin.getRole().getName().contains("门店员工")){
				result.put("permission", "0");
				//获取门店id
				result.put("storeId",admin.getDepartment().getStore().getId());
				result.put("operatorName", admin.getName());
			}
			else if(admin.getRole().getName().contains("门店经理")){
				result.put("permission", "1");
				//获取门店id
				result.put("storeId",admin.getDepartment().getStore().getId());
			}else{
				result.put("permission", "2");
			}		
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}
}
