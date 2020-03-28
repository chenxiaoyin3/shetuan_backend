package com.hongyu.controller.wj;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.HyArea;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.BranchPrePayDetailService;
import com.hongyu.service.BranchPrePayService;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StoreService;

/**
 * 财务可看
 * 线下预存款收入报表
 * 
 * @author wj
 *
 */
@Controller
@RequestMapping("/admin/xianxiaRechargeStatics")
public class XianxiaRechargeStatics {


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
	
	@Resource(name="storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;
	
	@Resource(name="storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	
	@Resource(name = "branchBalanceServiceImpl")
	BranchBalanceService branchBalanceService;
	
	
	/**
	 * 线下充值报表
	 * @param startDay
	 * @param endDay
	 * @param type
	 * @param id
	 * @param pageable
	 * @param session
	 * @return
	 */
	@RequestMapping("/xianxaczlist/view")
	@ResponseBody
	public Json xianxiarecharge(@DateTimeFormat(iso=ISO.DATE)Date startDay,@DateTimeFormat(iso=ISO.DATE)Date endDay,Long areaId,Integer storeType,
			Integer type,Long id ,Pageable pageable,HttpSession session){
		Json json=new Json();
		try {
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal zero = BigDecimal.ZERO;
			List<HashMap<String, Object>> list = new ArrayList<>();
			if(type == null || type == 3){
				//门店预存款 ——线下充值，status=1
				StringBuffer sql1 = new StringBuffer();
				sql1.append(" select d.full_name,CAST(log.money AS DECIMAL(21, 3)),CAST(account.balance AS DECIMAL(21, 3)),log.create_date "
						//新增所属地区和门店类型
						+ " ,area.full_name areaName ,(case s.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype"
						+ " from hy_department d,hy_store_account_log log, hy_store s,hy_store_account account ,hy_area area"
						+ " where d.id = s.department_id and log.store_id = s.id and account.store = log.store_id "
						//新增
						+ " and s.area_id = area.id"
						+ " and log.type = 0 and log.status = 1 ");
				
				//分公司预存款记录，type=1
				StringBuffer sql2 = new StringBuffer();
				sql2.append("select s.department_name , CAST(s.amount AS DECIMAL(21, 3)),CAST(b.branch_balance AS DECIMAL(21, 3)),s.date "
						+ " from hy_branch_pre_save s,hy_branch_balance b "
						+ " where s.branch_id = b.branch_id"
						+ " and s.type = 1  ");
				if(startDay!=null && endDay!=null){
					if(startDay.compareTo(endDay)>0){
						throw new Exception("查询开始时间不能大于结束时间");
					}
					
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(endDay);
					calendar.add(Calendar.DATE, 1);
					Date nextEndDate = calendar.getTime();
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					String startday = formatter.format(startDay);  
					String endday = formatter.format(nextEndDate);
					
					sql1.append("and log.create_date>='");
					sql1.append(startday+"' ");
					sql1.append("and log.create_date<'");
					sql1.append(endday +"' ");
					sql2.append("and s.date>='");
					sql2.append(startday+"' ");
					sql2.append("and s.date<'");
					sql2.append(endday +"' ");
				}
				
				
				List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
				List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());
				
				
				for (Object[] objects : list1) {
					HashMap<String, Object> map = new HashMap<>();
					
					map.put("departmentName",objects[0]);
					map.put("amount", objects[1] );
					sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
					map.put("balance", objects[2] );
					map.put("date", objects[3]==null ? null : objects[3].toString().substring(0, 19));
					map.put("areaName", objects[4] == null ? null : objects[4].toString());
					map.put("storeType", objects[5] == null ? null : objects[5].toString());
					list.add(map);
				}
				for (Object[] objects : list2) {
					HashMap<String, Object> map = new HashMap<>();
					map.put("departmentName",objects[0]);
					map.put("amount", objects[1] );
					sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
					map.put("balance", objects[2] );
					map.put("date", objects[3]==null ? null : objects[3].toString().substring(0, 19));
					
					list.add(map);
				}
			}else if(type == 1){
				//门店预存款 ——线下充值，status=1
				StringBuffer sql1 = new StringBuffer();
				sql1.append("select d.full_name,CAST(log.money AS DECIMAL(21, 3)),CAST(account.balance AS DECIMAL(21, 3)),log.create_date "
						+ " ,area.full_name areaName ,(case s.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype "
						+ " from hy_department d,hy_store_account_log log, hy_store s,hy_store_account account ,hy_area area"
						+ " where d.id = s.department_id and log.store_id = s.id and account.store = log.store_id "
						+ " and s.area_id = area.id"
						+ " and log.type = 0 and log.status = 1 ");
				if(id != null){
					sql1.append("and log.store_id = "+id +" ");
				}
				if( areaId != null){
					sql1.append(" and s.area_id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
				}
				if( storeType != null){
					sql1.append(" and s.type = "+storeType);
				}
				if(startDay!=null && endDay!=null){
					if(startDay.compareTo(endDay)>0){
						throw new Exception("查询开始时间不能大于结束时间");
					}
					
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(endDay);
					calendar.add(Calendar.DATE, 1);
					Date nextEndDate = calendar.getTime();
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					String startday = formatter.format(startDay);  
					String endday = formatter.format(nextEndDate);
					
					sql1.append(" and log.create_date>='");
					sql1.append(startday+"' ");
					sql1.append(" and log.create_date<'");
					sql1.append(endday +"' ");
				}
				
				
				List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
				
				
				for (Object[] objects : list1) {
					HashMap<String, Object> map = new HashMap<>();
					
					map.put("departmentName",objects[0]);
					map.put("amount", objects[1] );
					sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
					map.put("balance", objects[2] );
					map.put("date", objects[3]==null ? null : objects[3].toString().substring(0, 19));
					map.put("areaName", objects[4] == null ? null : objects[4].toString());
					map.put("storeType", objects[5] == null ? null : objects[5].toString());
					list.add(map);
				}
			}else if(type ==2){
				
				
				//分公司预存款记录，type=1
				StringBuffer sql2 = new StringBuffer();
				sql2.append("select s.department_name , CAST(s.amount AS DECIMAL(21, 3)),CAST(b.branch_balance AS DECIMAL(21, 3)),s.date "
						+ " from hy_branch_pre_save s,hy_branch_balance b "
						+ " where s.branch_id = b.branch_id"
						+ " and s.type = 1   ");
				if(id!=null){
					sql2.append("and s.branch_id = "+ id +" ");
				}
				if(startDay!=null && endDay!=null){
					if(startDay.compareTo(endDay)>0){
						throw new Exception("查询开始时间不能大于结束时间");
					}
					
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(endDay);
					calendar.add(Calendar.DATE, 1);
					Date nextEndDate = calendar.getTime();
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					String startday = formatter.format(startDay);  
					String endday = formatter.format(nextEndDate);
					
					sql2.append("and s.date>='");
					sql2.append(startday+"' ");
					sql2.append("and s.date<'");
					sql2.append(endday +"' ");
				}
				
				
				List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());
				
				for (Object[] objects : list2) {
					HashMap<String, Object> map = new HashMap<>();
					map.put("departmentName",objects[0]);
					map.put("amount", objects[1] );
					sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
					map.put("balance", objects[2] );
					map.put("date", objects[3]==null ? null : objects[3].toString().substring(0, 19));
					
					list.add(map);
				}
			}
			
			HashMap<String, Object> ans = new HashMap<>();
			ans.put("sum", sum);
			ans.put("list", list);
			json.setObj(ans);
			

			json.setMsg("查询成功");
//			json.setObj(list);
			json.setSuccess(true);

		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("查询失败："+ e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}
	return json;
	}
	
	
	@RequestMapping("/download/xianxiaczlist/view")
	public void xianxiarechargedownload(@DateTimeFormat(pattern="yyyy-MM-dd") Date startDay, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date endDay,Integer type,Long id,Long areaId,Integer storeType,
			HttpSession session,HttpServletRequest request,HttpServletResponse response){
		try {
			List<xianxiaData> list = new ArrayList<>();
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal zero = BigDecimal.ZERO;
			if(type == null || type == 3){
				//门店预存款 ——线下充值，status=1
				StringBuffer sql1 = new StringBuffer();
				sql1.append(" select d.full_name,CAST(log.money AS DECIMAL(21, 3)),CAST(account.balance AS DECIMAL(21, 3)),log.create_date "
						//新增所属地区和门店类型
						+ " ,area.full_name areaName ,(case s.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype"
						+ " from hy_department d,hy_store_account_log log, hy_store s,hy_store_account account ,hy_area area"
						+ " where d.id = s.department_id and log.store_id = s.id and account.store = log.store_id "
						//新增
						+ " and s.area_id = area.id"
						+ " and log.type = 0 and log.status = 1 ");
				
				//分公司预存款记录，type=1
				StringBuffer sql2 = new StringBuffer();
				sql2.append("select s.department_name , CAST(s.amount AS DECIMAL(21, 3)),CAST(b.branch_balance AS DECIMAL(21, 3)),s.date "
						+ " from hy_branch_pre_save s,hy_branch_balance b "
						+ " where s.branch_id = b.branch_id"
						+ " and s.type = 1  ");
				if(startDay!=null && endDay!=null){
					if(startDay.compareTo(endDay)>0){
						throw new Exception("查询开始时间不能大于结束时间");
					}
					
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(endDay);
					calendar.add(Calendar.DATE, 1);
					Date nextEndDate = calendar.getTime();
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					String startday = formatter.format(startDay);  
					String endday = formatter.format(nextEndDate);
					
					sql1.append("and log.create_date>='");
					sql1.append(startday+"' ");
					sql1.append("and log.create_date<'");
					sql1.append(endday +"' ");
					sql2.append("and s.date>='");
					sql2.append(startday+"' ");
					sql2.append("and s.date<'");
					sql2.append(endday +"' ");
				}
				
				
				List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
				List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());
				
				for (Object[] objects : list1) {
					xianxiaData data = new xianxiaData();
					data.setDepartmentName(objects[0].toString());
					data.setAmount(objects[1]==null?BigDecimal.ZERO:(BigDecimal)objects[1]);
					sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
					data.setBalance(objects[2]==null?BigDecimal.ZERO:(BigDecimal)objects[2]);
					data.setDate(objects[3]==null ? null : objects[3].toString().substring(0, 19));	
					data.setAreaName(objects[4] == null ? null : objects[4].toString());
					data.setStoreType(objects[5] == null ? null : objects[5].toString());
					list.add(data);
				}
				for (Object[] objects : list2) {
					xianxiaData data = new xianxiaData();
					data.setDepartmentName(objects[0].toString());
					data.setAmount(objects[1]==null?BigDecimal.ZERO:(BigDecimal)objects[1]);
					sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
					data.setBalance(objects[2]==null?BigDecimal.ZERO:(BigDecimal)objects[2]);
					data.setDate(objects[3]==null ? null : objects[3].toString().substring(0, 19));		
					list.add(data);
				}
			}else if(type == 1){
				//门店预存款 ——线下充值，status=1
				StringBuffer sql1 = new StringBuffer();
				sql1.append("select d.full_name,CAST(log.money AS DECIMAL(21, 3)),CAST(account.balance AS DECIMAL(21, 3)),log.create_date "
						+ " ,area.full_name areaName ,(case s.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype "
						+ " from hy_department d,hy_store_account_log log, hy_store s,hy_store_account account ,hy_area area"
						+ " where d.id = s.department_id and log.store_id = s.id and account.store = log.store_id "
						+ " and s.area_id = area.id"
						+ " and log.type = 0 and log.status = 1 ");
				
				if(id != null){
					sql1.append("and log.store_id = "+id +" ");
				}
				
				if( areaId != null){
					sql1.append(" and s.area_id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
				}
				if( storeType != null){
					sql1.append(" and s.type = "+storeType);
				}
				
				if(startDay!=null && endDay!=null){
					if(startDay.compareTo(endDay)>0){
						throw new Exception("查询开始时间不能大于结束时间");
					}
					
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(endDay);
					calendar.add(Calendar.DATE, 1);
					Date nextEndDate = calendar.getTime();
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					String startday = formatter.format(startDay);  
					String endday = formatter.format(nextEndDate);
					
					sql1.append("and log.create_date>='");
					sql1.append(startday+"' ");
					sql1.append("and log.create_date<'");
					sql1.append(endday +"' ");
				}
				
				
				List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
				
				for (Object[] objects : list1) {
					xianxiaData data = new xianxiaData();
					data.setDepartmentName(objects[0].toString());
					data.setAmount(objects[1]==null?BigDecimal.ZERO:(BigDecimal)objects[1]);
					sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
					data.setBalance(objects[2]==null?BigDecimal.ZERO:(BigDecimal)objects[2]);
					data.setDate(objects[3]==null ? null : objects[3].toString().substring(0, 19));	
					data.setAreaName(objects[4] == null ? null : objects[4].toString());
					data.setStoreType(objects[5] == null ? null : objects[5].toString());
					list.add(data);
				}
			}else if(type == 2){
				
				//分公司预存款记录，type=1
				StringBuffer sql2 = new StringBuffer();
				sql2.append("select s.department_name , CAST(s.amount AS DECIMAL(21, 3)),CAST(b.branch_balance AS DECIMAL(21, 3)),s.date "
						+ " from hy_branch_pre_save s,hy_branch_balance b "
						+ " where s.branch_id = b.branch_id"
						+ " and s.type = 1  ");
				
				if(id!=null){
					sql2.append("and s.branch_id = "+ id +" ");
				}
				
				if(startDay!=null && endDay!=null){
					if(startDay.compareTo(endDay)>0){
						throw new Exception("查询开始时间不能大于结束时间");
					}
					
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(endDay);
					calendar.add(Calendar.DATE, 1);
					Date nextEndDate = calendar.getTime();
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					String startday = formatter.format(startDay);  
					String endday = formatter.format(nextEndDate);
					
					sql2.append(" and s.date>='");
					sql2.append(startday+"' ");
					sql2.append(" and s.date<'");
					sql2.append(endday +"' ");
				}
				
				
				List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());
				
				for (Object[] objects : list2) {
					xianxiaData data = new xianxiaData();
					data.setDepartmentName(objects[0].toString());
					data.setAmount(objects[1]==null?BigDecimal.ZERO:(BigDecimal)objects[1]);
					sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
					data.setBalance(objects[2]==null?BigDecimal.ZERO:(BigDecimal)objects[2]);
					data.setDate(objects[3]==null ? null : objects[3].toString().substring(0, 19));		
					list.add(data);
				}
			}
			xianxiaData data = new xianxiaData();
			data.setDepartmentName("合计");
			data.setAmount(sum);
			list.add(data);
			
			
			StringBuffer sb2 = new StringBuffer();
			sb2.append("门店线下充值报表");
			String fileName = "门店线下充值报表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "storeRechargeXianxia.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, list, fileName, tableTitle, configFile);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	

	public class xianxiaData{
		private BigDecimal amount;
		private String date;
		private BigDecimal balance;
		private String departmentName;
		private String areaName;
		private String storeType;
		public BigDecimal getAmount() {
			return amount;
		}
		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public BigDecimal getBalance() {
			return balance;
		}
		public void setBalance(BigDecimal balance) {
			this.balance = balance;
		}
		public String getDepartmentName() {
			return departmentName;
		}
		public void setDepartmentName(String departmentName) {
			this.departmentName = departmentName;
		}
		public String getAreaName() {
			return areaName;
		}
		public void setAreaName(String areaName) {
			this.areaName = areaName;
		}
		public String getStoreType() {
			return storeType;
		}
		public void setStoreType(String storeType) {
			this.storeType = storeType;
		}
		
		
	}
	
	
	@Resource(name = "hyAreaServiceImpl")
	HyAreaService hyAreaService;
	
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
	
	
	/**
	 * 获取门店或者分公司
	 * @param type
	 * @return
	 */
	@RequestMapping("/names")
	  @ResponseBody
	  public Json name(Integer type)
	  {
	    Json json = new Json();
	    try
	    {
	      List<HashMap<String, Object>> ans = new ArrayList();
	      List<Object[]> list = new ArrayList();
	      if (type.intValue() == 1)
	      {
	        String sql = "select id,store_name as name from hy_store ";
	        list = this.storeService.statis(sql);
	      }
	      else
	      {
	        String sql = "select department,company_name as name from hy_company ";
	        list = this.storeService.statis(sql);
	      }
	      for (Object[] object : list)
	      {
	        HashMap<String, Object> map = new HashMap();
	        map.put("id", object[0]);
	        map.put("name", object[1]);
	        ans.add(map);
	      }
	      json.setMsg("查询成功");
	      json.setObj(ans);
	      json.setSuccess(true);
	    }
	    catch (Exception e)
	    {
	      json.setMsg("查询失败：" + e.getMessage());
	      json.setSuccess(false);
	      e.printStackTrace();
	    }
	    return json;
	  }
	
	
}
