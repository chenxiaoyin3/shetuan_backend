package com.hongyu.controller.wj;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
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

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BranchBalance;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreAccountLog;
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
import com.sun.javafx.collections.MappingChange.Map;

/**
 * 财务可看 预存款收入报表（只统计线上充值）
 * 
 * @author wj
 *
 */
@Controller
@RequestMapping("/admin/xianshangRechargeStatics")
public class RechargeStatics {

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

	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;

	@Resource(name = "branchBalanceServiceImpl")
	BranchBalanceService branchBalanceService;

	@Resource(name = "hyAreaServiceImpl")
	HyAreaService hyAreaService;
	
	
	/**
	 * 线上充值报表
	 * 
	 * @param start
	 * @param end
	 * @param type
	 * @param id
	 * @param pageable
	 * @param session
	 * @return
	 */
	@RequestMapping("/save/list")
	@ResponseBody
	public Json recharge(@DateTimeFormat(iso = ISO.DATE) Date start, @DateTimeFormat(iso = ISO.DATE) Date end,
			Integer type, Long id,Long areaId,Integer storeType, Pageable pageable, HttpSession session) {
		Json json = new Json();
		try {
			List<HashMap<String, Object>> list = new ArrayList<>();
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal zero = BigDecimal.ZERO;
			if (type == null || type == 3) {
				// 门店预存款
				StringBuffer sql1 = new StringBuffer();
				sql1.append(
						" select d.full_name,CAST(log.money AS DECIMAL(21, 3)),CAST(account.balance AS DECIMAL(21, 3)),log.create_date ,area.full_name areaName ,(case s.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype"
								+ " from hy_department d,hy_store_account_log log, hy_store s,hy_store_account account,hy_area area "
								+ " where d.id = s.department_id and log.store_id = s.id and account.store = log.store_id and s.area_id = area.id"
								+ " and log.type = 0 and log.status = 5 ");

				// 分公司预存款记录
				StringBuffer sql2 = new StringBuffer();
				sql2.append(
						" select s.department_name , CAST(s.amount AS DECIMAL(21, 3)),CAST(b.branch_balance AS DECIMAL(21, 3)),s.date "
								+ " from hy_branch_pre_save s,hy_branch_balance b " + " where s.branch_id = b.branch_id"
								+ " and s.type = 8  ");
				if (start != null && end != null) {
					if (start.compareTo(end) > 0) {
						throw new Exception("查询开始时间不能大于结束时间");
					}

					Calendar calendar = new GregorianCalendar();
					calendar.setTime(end);
					calendar.add(Calendar.DATE, 1);
					Date nextEndDate = calendar.getTime();

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String startday = formatter.format(start);
					String endday = formatter.format(nextEndDate);

					sql1.append(" and log.create_date>='");
					sql1.append(startday + "' ");
					sql1.append(" and log.create_date<'");
					sql1.append(endday + "' ");
					sql2.append(" and s.date>='");
					sql2.append(startday + "' ");
					sql2.append(" and s.date<'");
					sql2.append(endday + "' ");
				}

				List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
				List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());

				for (Object[] objects : list1) {
					HashMap<String, Object> map = new HashMap<>();

					map.put("departmentName", objects[0]);
					map.put("amount", objects[1]);
					sum = sum.add(objects[1] == null ? zero : (BigDecimal) objects[1]);
					map.put("balance", objects[2]);
					map.put("date", objects[3] == null ? null : objects[3].toString().substring(0, 19));
					map.put("areaName", objects[4] == null ? null : objects[4].toString());
					map.put("storeType", objects[5] == null ? null : objects[5].toString());
					list.add(map);
				}
				for (Object[] objects : list2) {
					HashMap<String, Object> map = new HashMap<>();
					map.put("departmentName", objects[0]);
					map.put("amount", objects[1]);
					sum = sum.add(objects[1] == null ? zero : (BigDecimal) objects[1]);
					map.put("balance", objects[2]);
					map.put("date", objects[3] == null ? null : objects[3].toString().substring(0, 19));

					list.add(map);
				}
			} else if (type == 1) {// 门店名称
				// 门店预存款
				StringBuffer sql1 = new StringBuffer();
				sql1.append(
						" select d.full_name,CAST(log.money AS DECIMAL(21, 3)),CAST(account.balance AS DECIMAL(21, 3)),log.create_date ,area.full_name areaName ,(case s.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype "
								+ " from hy_department d,hy_store_account_log log, hy_store s,hy_store_account account ,hy_area area"
								+ " where d.id = s.department_id and log.store_id = s.id and account.store = log.store_id and s.area_id = area.id"
								+ " and log.type = 0 and log.status = 5 ");

				if (id != null) {
					sql1.append("and log.store_id = " + id + " ");
				}

				if (start != null && end != null) {
					if (start.compareTo(end) > 0) {
						throw new Exception("查询开始时间不能大于结束时间");
					}

					Calendar calendar = new GregorianCalendar();
					calendar.setTime(end);
					calendar.add(Calendar.DATE, 1);
					Date nextEndDate = calendar.getTime();

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String startday = formatter.format(start);
					String endday = formatter.format(nextEndDate);

					sql1.append(" and log.create_date>='");
					sql1.append(startday + "' ");
					sql1.append(" and log.create_date<'");
					sql1.append(endday + "' ");
				}
				
				if( areaId != null){
					sql1.append(" and s.area_id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
				}
				if( storeType != null){
					sql1.append(" and s.type = "+storeType);
				}

				System.out.println(sql1);
				List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
				for (Object[] objects : list1) {
					HashMap<String, Object> map = new HashMap<>();

					map.put("departmentName", objects[0]);
					map.put("amount", objects[1]);
					sum = sum.add(objects[1] == null ? zero : (BigDecimal) objects[1]);
					map.put("balance", objects[2]);
					map.put("date", objects[3] == null ? null : objects[3].toString().substring(0, 19));
					map.put("areaName", objects[4] == null ? null : objects[4].toString());
					map.put("storeType", objects[5] == null ? null : objects[5].toString());
					list.add(map);
				}
			} else if (type == 2) {
				// 分公司预存款记录
				StringBuffer sql2 = new StringBuffer();
				sql2.append(
						"select s.department_name , CAST(s.amount AS DECIMAL(21, 3)),CAST(b.branch_balance AS DECIMAL(21, 3)),s.date "
								+ " from hy_branch_pre_save s,hy_branch_balance b " + " where s.branch_id = b.branch_id"
								+ " and s.type = 8  ");

				if (id != null) {
					sql2.append("and s.branch_id = " + id + " ");
				}

				if (start != null && end != null) {
					if (start.compareTo(end) > 0) {
						throw new Exception("查询开始时间不能大于结束时间");
					}

					Calendar calendar = new GregorianCalendar();
					calendar.setTime(end);
					calendar.add(Calendar.DATE, 1);
					Date nextEndDate = calendar.getTime();

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String startday = formatter.format(start);
					String endday = formatter.format(nextEndDate);
					sql2.append("and s.date>='");
					sql2.append(startday + "' ");
					sql2.append("and s.date<'");
					sql2.append(endday + "' ");
				}
				List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());
				for (Object[] objects : list2) {
					HashMap<String, Object> map = new HashMap<>();
					map.put("departmentName", objects[0]);
					map.put("amount", objects[1]);
					sum = sum.add(objects[1] == null ? zero : (BigDecimal) objects[1]);
					map.put("balance", objects[2]);
					map.put("date", objects[3] == null ? null : objects[3].toString().substring(0, 19));

					list.add(map);
				}
			}

			HashMap<String, Object> ans = new HashMap<>();
			ans.put("sum", sum);
			ans.put("list", list);
			json.setObj(ans);

			json.setMsg("查询成功");
			// json.setObj(list);
			json.setSuccess(true);

		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("查询失败：" + e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("/download/save/list")
	public void downloadrecharge(@DateTimeFormat(iso = ISO.DATE) Date start, @DateTimeFormat(iso = ISO.DATE) Date end,
			Integer type, Long id,Long areaId,Integer storeType, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		try {
			List<RechargeDate> rechargeDates = new ArrayList<>();
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal zero = BigDecimal.ZERO;
			if (type == null || type == 3) {
				// 门店预存款
				StringBuffer sql1 = new StringBuffer();
				sql1.append(
						" select d.full_name,CAST(log.money AS DECIMAL(21, 3)),CAST(account.balance AS DECIMAL(21, 3)),log.create_date ,area.full_name areaName ,(case s.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype"
								+ " from hy_department d,hy_store_account_log log, hy_store s,hy_store_account account,hy_area area "
								+ " where d.id = s.department_id and log.store_id = s.id and account.store = log.store_id and s.area_id = area.id"
								+ " and log.type = 0 and log.status = 5 ");
				// 分公司预存款记录
				StringBuffer sql2 = new StringBuffer();
				sql2.append(
						"select s.department_name , CAST(s.amount AS DECIMAL(21, 3)),CAST(b.branch_balance AS DECIMAL(21, 3)),s.date "
								+ " from hy_branch_pre_save s,hy_branch_balance b " + " where s.branch_id = b.branch_id"
								+ " and s.type = 8  ");
				if (start != null && end != null) {
					if (start.compareTo(end) > 0) {
						throw new Exception("查询开始时间不能大于结束时间");
					}
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(end);
					calendar.add(Calendar.DATE, 1);
					Date nextEndDate = calendar.getTime();

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String startday = formatter.format(start);
					String endday = formatter.format(nextEndDate);

					sql1.append("and log.create_date>='");
					sql1.append(startday + "' ");
					sql1.append("and log.create_date<'");
					sql1.append(endday + "' ");
					sql2.append("and s.date>='");
					sql2.append(startday + "' ");
					sql2.append("and s.date<'");
					sql2.append(endday + "' ");
				}

				List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
				List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());
				for (Object[] objects : list1) {
					RechargeDate rechargeDate = new RechargeDate();
					rechargeDate.setDepartmentName(objects[0] == null ? null : objects[0].toString());
					rechargeDate.setAmount(objects[1] == null ? null : objects[1].toString());
					sum = sum.add(objects[1] == null ? zero : (BigDecimal) objects[1]);
					rechargeDate.setBalance(objects[2] == null ? null : objects[2].toString());
					rechargeDate.setDate(objects[3] == null ? null : objects[3].toString().substring(0, 19));
					rechargeDate.setAreaName(objects[4] == null ? null : objects[4].toString());
					rechargeDate.setStoreType(objects[5] == null ? null : objects[5].toString());

					rechargeDates.add(rechargeDate);
				}
				for (Object[] objects : list2) {

					RechargeDate rechargeDate = new RechargeDate();
					rechargeDate.setDepartmentName(objects[0] == null ? null : objects[0].toString());
					rechargeDate.setAmount(objects[1] == null ? null : objects[1].toString());
					sum = sum.add(objects[1] == null ? zero : (BigDecimal) objects[1]);
					rechargeDate.setBalance(objects[2] == null ? null : objects[2].toString());
					rechargeDate.setDate(objects[3] == null ? null : objects[3].toString().substring(0, 19));

					// if(objects[3]!=null){
					// String string = objects[3].toString();
					// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd
					// HH:mm:ss");
					// rechargeDate.setDate(sdf.parse(string));
					// }
					rechargeDates.add(rechargeDate);
				}
			} else if (type == 1) {
				// 门店预存款
				StringBuffer sql1 = new StringBuffer();
				sql1.append(
						" select d.full_name,CAST(log.money AS DECIMAL(21, 3)),CAST(account.balance AS DECIMAL(21, 3)),log.create_date ,area.full_name areaName ,(case s.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype "
								+ " from hy_department d,hy_store_account_log log, hy_store s,hy_store_account account ,hy_area area"
								+ " where d.id = s.department_id and log.store_id = s.id and account.store = log.store_id and s.area_id = area.id"
								+ " and log.type = 0 and log.status = 5 ");
				if (id != null) {
					sql1.append("and log.store_id = " + id + " ");
				}

				if (start != null && end != null) {
					if (start.compareTo(end) > 0) {
						throw new Exception("查询开始时间不能大于结束时间");
					}
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(end);
					calendar.add(Calendar.DATE, 1);
					Date nextEndDate = calendar.getTime();

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String startday = formatter.format(start);
					String endday = formatter.format(nextEndDate);

					sql1.append("and log.create_date>='");
					sql1.append(startday + "' ");
					sql1.append("and log.create_date<'");
					sql1.append(endday + "' ");
				}
				
				if( areaId != null){
					sql1.append(" and s.area_id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
				}
				if( storeType != null){
					sql1.append(" and s.type = "+storeType);
				}

				List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
				for (Object[] objects : list1) {
					RechargeDate rechargeDate = new RechargeDate();
					rechargeDate.setDepartmentName(objects[0] == null ? null : objects[0].toString());
					rechargeDate.setAmount(objects[1] == null ? null : objects[1].toString());
					sum = sum.add(objects[1] == null ? zero : (BigDecimal) objects[1]);
					rechargeDate.setBalance(objects[2] == null ? null : objects[2].toString());
					rechargeDate.setDate(objects[3] == null ? null : objects[3].toString().substring(0, 19));
					rechargeDate.setAreaName(objects[4] == null ? null : objects[4].toString());
					rechargeDate.setStoreType(objects[5] == null ? null : objects[5].toString());

					rechargeDates.add(rechargeDate);
				}
			} else if (type == 2) {
				// 分公司预存款记录
				StringBuffer sql2 = new StringBuffer();
				sql2.append(
						"select s.department_name , CAST(s.amount AS DECIMAL(21, 3)),CAST(b.branch_balance AS DECIMAL(21, 3)),s.date "
								+ " from hy_branch_pre_save s,hy_branch_balance b " + " where s.branch_id = b.branch_id"
								+ " and s.type = 8  ");
				if (id != null) {
					sql2.append("and s.branch_id = " + id + " ");
				}
				if (start != null && end != null) {
					if (start.compareTo(end) > 0) {
						throw new Exception("查询开始时间不能大于结束时间");
					}
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(end);
					calendar.add(Calendar.DATE, 1);
					Date nextEndDate = calendar.getTime();

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String startday = formatter.format(start);
					String endday = formatter.format(nextEndDate);

					sql2.append("and s.date>='");
					sql2.append(startday + "' ");
					sql2.append("and s.date<'");
					sql2.append(endday + "' ");
				}

				List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());
				for (Object[] objects : list2) {

					RechargeDate rechargeDate = new RechargeDate();
					rechargeDate.setDepartmentName(objects[0] == null ? null : objects[0].toString());
					rechargeDate.setAmount(objects[1] == null ? null : objects[1].toString());
					sum = sum.add(objects[1] == null ? zero : (BigDecimal) objects[1]);
					rechargeDate.setBalance(objects[2] == null ? null : objects[2].toString());
					rechargeDate.setDate(objects[3] == null ? null : objects[3].toString().substring(0, 19));

					// if(objects[3]!=null){
					// String string = objects[3].toString();
					// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd
					// HH:mm:ss");
					// rechargeDate.setDate(sdf.parse(string));
					// }
					rechargeDates.add(rechargeDate);
				}
			}

			RechargeDate rechargeDate = new RechargeDate();
			rechargeDate.setDepartmentName("合计");
			rechargeDate.setAmount(sum.toString());
			rechargeDates.add(rechargeDate);

			StringBuffer sb2 = new StringBuffer();
			sb2.append("预存款收入报表");
			String fileName = "预存款收入报表.xls"; // Excel文件名
			String tableTitle = sb2.toString(); // Excel表标题
			String configFile = "presaveInStatics.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, rechargeDates, fileName, tableTitle, configFile);
			//

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/*
	 * //线下充值记录（可以根据门店查询）
	 * 
	 * @RequestMapping("/xianxaczlist/view")
	 * 
	 * @ResponseBody public Json
	 * xianxiarecharge(@DateTimeFormat(pattern="yyyy-MM-dd") Date startDay,
	 * 
	 * @DateTimeFormat(pattern="yyyy-MM-dd") Date endDay,Long storeId){ Json
	 * json = new Json(); try { List<Filter> filters= new ArrayList<>();
	 * filters.add(Filter.eq("status", 1)); filters.add(Filter.eq("type",0));
	 * if(startDay!=null && endDay!=null){ if(startDay.compareTo(endDay)>0){
	 * throw new Exception("查询开始时间不能大于结束时间"); } } if(startDay!=null){
	 * filters.add(Filter.ge("createDate", startDay)); } if(endDay!=null){
	 * filters.add(Filter.le("createDate", endDay)); }
	 * 
	 * List<StoreAccountLog> storeAccountLogs =
	 * storeAccountLogService.findList(null,filters,null); //
	 * Page<StoreAccountLog> page=storeAccountLogService.findPage(pageable);
	 * List<HashMap<String, Object>> list = new ArrayList<>(); BigDecimal sum =
	 * BigDecimal.ZERO; for(StoreAccountLog log : storeAccountLogs){
	 * HashMap<String, Object> map = new HashMap<>(); map.put("id",
	 * log.getId()); map.put("money", log.getMoney()); map.put("createDate",
	 * log.getCreateDate()); map.put("profile", log.getProfile());
	 * map.put("storeName", log.getStore().getStoreName()); sum =
	 * sum.add(log.getMoney()); list.add(map); } HashMap<String, Object> ans =
	 * new HashMap<>(); ans.put("sum", sum); ans.put("list", list);
	 * 
	 * json.setSuccess(true); json.setMsg("查找成功"); json.setObj(ans);
	 * 
	 * } catch (Exception e) { // TODO: handle exception json.setSuccess(false);
	 * json.setMsg("查询失败:"+e.getMessage()); json.setObj(e); } return json; }
	 *//**
		 * 线下充值报表
		 * 
		 * @param startDay
		 * @param endDay
		 * @param type
		 * @param id
		 * @param pageable
		 * @param session
		 * @return
		 *//*
		 * @RequestMapping("/xianxaczlist/view")
		 * 
		 * @ResponseBody public Json
		 * xianxiarecharge(@DateTimeFormat(iso=ISO.DATE)Date
		 * startDay,@DateTimeFormat(iso=ISO.DATE)Date endDay, Integer type,Long
		 * id ,Pageable pageable,HttpSession session){ Json json=new Json(); try
		 * { BigDecimal sum = BigDecimal.ZERO; BigDecimal zero =
		 * BigDecimal.ZERO; List<HashMap<String, Object>> list = new
		 * ArrayList<>(); if(type == null || type == 3){ //门店预存款 ——线下充值，status=1
		 * StringBuffer sql1 = new StringBuffer(); sql1.
		 * append("select d.full_name,CAST(log.money AS DECIMAL(21, 3)),CAST(account.balance AS DECIMAL(21, 3)),log.create_date "
		 * +
		 * "from hy_department d,hy_store_account_log log, hy_store s,hy_store_account account "
		 * +
		 * "where d.id = s.department_id and log.store_id = s.id and account.store = log.store_id "
		 * + " and log.type = 0 and log.status = 1 ");
		 * 
		 * //分公司预存款记录，type=1 StringBuffer sql2 = new StringBuffer(); sql2.
		 * append("select s.department_name , CAST(s.amount AS DECIMAL(21, 3)),CAST(b.branch_balance AS DECIMAL(21, 3)),s.date "
		 * + " from hy_branch_pre_save s,hy_branch_balance b " +
		 * " where s.branch_id = b.branch_id" + " and s.type = 1  ");
		 * if(startDay!=null && endDay!=null){ if(startDay.compareTo(endDay)>0){
		 * throw new Exception("查询开始时间不能大于结束时间"); }
		 * 
		 * Calendar calendar = new GregorianCalendar();
		 * calendar.setTime(endDay); calendar.add(Calendar.DATE, 1); Date
		 * nextEndDate = calendar.getTime();
		 * 
		 * SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 * String startday = formatter.format(startDay); String endday =
		 * formatter.format(nextEndDate);
		 * 
		 * sql1.append("and log.create_date>='"); sql1.append(startday+"' ");
		 * sql1.append("and log.create_date<'"); sql1.append(endday +"' ");
		 * sql2.append("and s.date>='"); sql2.append(startday+"' ");
		 * sql2.append("and s.date<'"); sql2.append(endday +"' "); }
		 * 
		 * 
		 * List<Object[]> list1 =
		 * storeAccountLogService.statis(sql1.toString()); List<Object[]> list2
		 * = storeAccountLogService.statis(sql2.toString());
		 * 
		 * 
		 * for (Object[] objects : list1) { HashMap<String, Object> map = new
		 * HashMap<>();
		 * 
		 * map.put("departmentName",objects[0]); map.put("amount", objects[1] );
		 * sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
		 * map.put("balance", objects[2] ); map.put("date", objects[3]==null ?
		 * null : objects[3].toString().substring(0, 19)); list.add(map); } for
		 * (Object[] objects : list2) { HashMap<String, Object> map = new
		 * HashMap<>(); map.put("departmentName",objects[0]); map.put("amount",
		 * objects[1] ); sum = sum.add(objects[1] == null
		 * ?zero:(BigDecimal)objects[1]); map.put("balance", objects[2] );
		 * map.put("date", objects[3]==null ? null :
		 * objects[3].toString().substring(0, 19));
		 * 
		 * list.add(map); } }else if(type == 1){ //门店预存款 ——线下充值，status=1
		 * StringBuffer sql1 = new StringBuffer(); sql1.
		 * append("select d.full_name,CAST(log.money AS DECIMAL(21, 3)),CAST(account.balance AS DECIMAL(21, 3)),log.create_date "
		 * +
		 * "from hy_department d,hy_store_account_log log, hy_store s,hy_store_account account "
		 * +
		 * "where d.id = s.department_id and log.store_id = s.id and account.store = log.store_id "
		 * + " and log.type = 0 and log.status = 1 "); if(id != null){
		 * sql1.append("and log.store_id = "+id +" "); }
		 * 
		 * if(startDay!=null && endDay!=null){ if(startDay.compareTo(endDay)>0){
		 * throw new Exception("查询开始时间不能大于结束时间"); }
		 * 
		 * Calendar calendar = new GregorianCalendar();
		 * calendar.setTime(endDay); calendar.add(Calendar.DATE, 1); Date
		 * nextEndDate = calendar.getTime();
		 * 
		 * SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 * String startday = formatter.format(startDay); String endday =
		 * formatter.format(nextEndDate);
		 * 
		 * sql1.append("and log.create_date>='"); sql1.append(startday+"' ");
		 * sql1.append("and log.create_date<'"); sql1.append(endday +"' "); }
		 * 
		 * 
		 * List<Object[]> list1 =
		 * storeAccountLogService.statis(sql1.toString());
		 * 
		 * 
		 * for (Object[] objects : list1) { HashMap<String, Object> map = new
		 * HashMap<>();
		 * 
		 * map.put("departmentName",objects[0]); map.put("amount", objects[1] );
		 * sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
		 * map.put("balance", objects[2] ); map.put("date", objects[3]==null ?
		 * null : objects[3].toString().substring(0, 19)); list.add(map); }
		 * }else if(type ==2){
		 * 
		 * 
		 * //分公司预存款记录，type=1 StringBuffer sql2 = new StringBuffer(); sql2.
		 * append("select s.department_name , CAST(s.amount AS DECIMAL(21, 3)),CAST(b.branch_balance AS DECIMAL(21, 3)),s.date "
		 * + " from hy_branch_pre_save s,hy_branch_balance b " +
		 * " where s.branch_id = b.branch_id" + " and s.type = 1   ");
		 * if(id!=null){ sql2.append("and s.branch_id = "+ id +" "); }
		 * if(startDay!=null && endDay!=null){ if(startDay.compareTo(endDay)>0){
		 * throw new Exception("查询开始时间不能大于结束时间"); }
		 * 
		 * Calendar calendar = new GregorianCalendar();
		 * calendar.setTime(endDay); calendar.add(Calendar.DATE, 1); Date
		 * nextEndDate = calendar.getTime();
		 * 
		 * SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 * String startday = formatter.format(startDay); String endday =
		 * formatter.format(nextEndDate);
		 * 
		 * sql2.append("and s.date>='"); sql2.append(startday+"' ");
		 * sql2.append("and s.date<'"); sql2.append(endday +"' "); }
		 * 
		 * 
		 * List<Object[]> list2 =
		 * storeAccountLogService.statis(sql2.toString());
		 * 
		 * for (Object[] objects : list2) { HashMap<String, Object> map = new
		 * HashMap<>(); map.put("departmentName",objects[0]); map.put("amount",
		 * objects[1] ); sum = sum.add(objects[1] == null
		 * ?zero:(BigDecimal)objects[1]); map.put("balance", objects[2] );
		 * map.put("date", objects[3]==null ? null :
		 * objects[3].toString().substring(0, 19));
		 * 
		 * list.add(map); } }
		 * 
		 * HashMap<String, Object> ans = new HashMap<>(); ans.put("sum", sum);
		 * ans.put("list", list); json.setObj(ans);
		 * 
		 * 
		 * json.setMsg("查询成功"); // json.setObj(list); json.setSuccess(true);
		 * 
		 * } catch (Exception e) { // TODO: handle exception
		 * json.setMsg("查询失败："+ e.getMessage()); json.setSuccess(false);
		 * e.printStackTrace(); } return json; }
		 * 
		 * 
		 * @RequestMapping("/download/xianxiaczlist/view") public void
		 * xianxiarechargedownload(@DateTimeFormat(pattern="yyyy-MM-dd") Date
		 * startDay,
		 * 
		 * @DateTimeFormat(pattern="yyyy-MM-dd") Date endDay,Integer type,Long
		 * id, HttpSession session,HttpServletRequest
		 * request,HttpServletResponse response){ try { List<xianxiaData> list =
		 * new ArrayList<>(); BigDecimal sum = BigDecimal.ZERO; BigDecimal zero
		 * = BigDecimal.ZERO; if(type == null || type == 3){ //门店预存款
		 * ——线下充值，status=1 StringBuffer sql1 = new StringBuffer(); sql1.
		 * append("select d.full_name,CAST(log.money AS DECIMAL(21, 3)),CAST(account.balance AS DECIMAL(21, 3)),log.create_date "
		 * +
		 * "from hy_department d,hy_store_account_log log, hy_store s,hy_store_account account "
		 * +
		 * "where d.id = s.department_id and log.store_id = s.id and account.store = log.store_id "
		 * + " and log.type = 0 and log.status = 1 ");
		 * 
		 * //分公司预存款记录，type=1 StringBuffer sql2 = new StringBuffer(); sql2.
		 * append("select s.department_name , CAST(s.amount AS DECIMAL(21, 3)),CAST(b.branch_balance AS DECIMAL(21, 3)),s.date "
		 * + " from hy_branch_pre_save s,hy_branch_balance b " +
		 * " where s.branch_id = b.branch_id" + " and s.type = 1  ");
		 * if(startDay!=null && endDay!=null){ if(startDay.compareTo(endDay)>0){
		 * throw new Exception("查询开始时间不能大于结束时间"); }
		 * 
		 * Calendar calendar = new GregorianCalendar();
		 * calendar.setTime(endDay); calendar.add(Calendar.DATE, 1); Date
		 * nextEndDate = calendar.getTime();
		 * 
		 * SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 * String startday = formatter.format(startDay); String endday =
		 * formatter.format(nextEndDate);
		 * 
		 * sql1.append("and log.create_date>='"); sql1.append(startday+"' ");
		 * sql1.append("and log.create_date<'"); sql1.append(endday +"' ");
		 * sql2.append("and s.date>='"); sql2.append(startday+"' ");
		 * sql2.append("and s.date<'"); sql2.append(endday +"' "); }
		 * 
		 * 
		 * List<Object[]> list1 =
		 * storeAccountLogService.statis(sql1.toString()); List<Object[]> list2
		 * = storeAccountLogService.statis(sql2.toString());
		 * 
		 * for (Object[] objects : list1) { xianxiaData data = new
		 * xianxiaData(); data.setDepartmentName(objects[0].toString());
		 * data.setAmount(objects[1]==null?BigDecimal.ZERO:(BigDecimal)objects[1
		 * ]); sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
		 * data.setBalance(objects[2]==null?BigDecimal.ZERO:(BigDecimal)objects[
		 * 2]); data.setDate(objects[3]==null ? null :
		 * objects[3].toString().substring(0, 19)); list.add(data); } for
		 * (Object[] objects : list2) { xianxiaData data = new xianxiaData();
		 * data.setDepartmentName(objects[0].toString());
		 * data.setAmount(objects[1]==null?BigDecimal.ZERO:(BigDecimal)objects[1
		 * ]); sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
		 * data.setBalance(objects[2]==null?BigDecimal.ZERO:(BigDecimal)objects[
		 * 2]); data.setDate(objects[3]==null ? null :
		 * objects[3].toString().substring(0, 19)); list.add(data); } }else
		 * if(type == 1){ //门店预存款 ——线下充值，status=1 StringBuffer sql1 = new
		 * StringBuffer(); sql1.
		 * append("select d.full_name,CAST(log.money AS DECIMAL(21, 3)),CAST(account.balance AS DECIMAL(21, 3)),log.create_date "
		 * +
		 * "from hy_department d,hy_store_account_log log, hy_store s,hy_store_account account "
		 * +
		 * "where d.id = s.department_id and log.store_id = s.id and account.store = log.store_id "
		 * + " and log.type = 0 and log.status = 1  ");
		 * 
		 * if(id != null){ sql1.append("and log.store_id = "+id +" "); }
		 * if(startDay!=null && endDay!=null){ if(startDay.compareTo(endDay)>0){
		 * throw new Exception("查询开始时间不能大于结束时间"); }
		 * 
		 * Calendar calendar = new GregorianCalendar();
		 * calendar.setTime(endDay); calendar.add(Calendar.DATE, 1); Date
		 * nextEndDate = calendar.getTime();
		 * 
		 * SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 * String startday = formatter.format(startDay); String endday =
		 * formatter.format(nextEndDate);
		 * 
		 * sql1.append("and log.create_date>='"); sql1.append(startday+"' ");
		 * sql1.append("and log.create_date<'"); sql1.append(endday +"' "); }
		 * 
		 * 
		 * List<Object[]> list1 =
		 * storeAccountLogService.statis(sql1.toString());
		 * 
		 * for (Object[] objects : list1) { xianxiaData data = new
		 * xianxiaData(); data.setDepartmentName(objects[0].toString());
		 * data.setAmount(objects[1]==null?BigDecimal.ZERO:(BigDecimal)objects[1
		 * ]); sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
		 * data.setBalance(objects[2]==null?BigDecimal.ZERO:(BigDecimal)objects[
		 * 2]); data.setDate(objects[3]==null ? null :
		 * objects[3].toString().substring(0, 19)); list.add(data); } }else
		 * if(type == 2){
		 * 
		 * //分公司预存款记录，type=1 StringBuffer sql2 = new StringBuffer(); sql2.
		 * append("select s.department_name , CAST(s.amount AS DECIMAL(21, 3)),CAST(b.branch_balance AS DECIMAL(21, 3)),s.date "
		 * + " from hy_branch_pre_save s,hy_branch_balance b " +
		 * " where s.branch_id = b.branch_id" + " and s.type = 1  ");
		 * 
		 * if(id!=null){ sql2.append("and s.branch_id = "+ id +" "); }
		 * 
		 * if(startDay!=null && endDay!=null){ if(startDay.compareTo(endDay)>0){
		 * throw new Exception("查询开始时间不能大于结束时间"); }
		 * 
		 * Calendar calendar = new GregorianCalendar();
		 * calendar.setTime(endDay); calendar.add(Calendar.DATE, 1); Date
		 * nextEndDate = calendar.getTime();
		 * 
		 * SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 * String startday = formatter.format(startDay); String endday =
		 * formatter.format(nextEndDate);
		 * 
		 * sql2.append("and s.date>='"); sql2.append(startday+"' ");
		 * sql2.append("and s.date<'"); sql2.append(endday +"' "); }
		 * 
		 * 
		 * List<Object[]> list2 =
		 * storeAccountLogService.statis(sql2.toString());
		 * 
		 * for (Object[] objects : list2) { xianxiaData data = new
		 * xianxiaData(); data.setDepartmentName(objects[0].toString());
		 * data.setAmount(objects[1]==null?BigDecimal.ZERO:(BigDecimal)objects[1
		 * ]); sum = sum.add(objects[1] == null ?zero:(BigDecimal)objects[1]);
		 * data.setBalance(objects[2]==null?BigDecimal.ZERO:(BigDecimal)objects[
		 * 2]); data.setDate(objects[3]==null ? null :
		 * objects[3].toString().substring(0, 19)); list.add(data); } }
		 * xianxiaData data = new xianxiaData(); data.setDepartmentName("合计");
		 * data.setAmount(sum); list.add(data);
		 * 
		 * 
		 * StringBuffer sb2 = new StringBuffer(); sb2.append("门店线下充值报表"); String
		 * fileName = "门店线下充值报表.xls"; // Excel文件名 String tableTitle =
		 * sb2.toString(); // Excel表标题 String configFile =
		 * "storeRechargeXianxia.xml"; // 配置文件
		 * com.grain.controller.BaseController excelCon = new
		 * com.grain.controller.BaseController(); excelCon.export2Excel(request,
		 * response, list, fileName, tableTitle, configFile);
		 * 
		 * 
		 * } catch (Exception e) { // TODO: handle exception
		 * e.printStackTrace(); } }
		 */
	/*
	 * @RequestMapping("/download/xianxiaczlist/view")
	 * 
	 * @ResponseBody public Json
	 * xianxiarechargedownload(@DateTimeFormat(pattern="yyyy-MM-dd") Date
	 * startDay,
	 * 
	 * @DateTimeFormat(pattern="yyyy-MM-dd") Date endDay,Long storeId,
	 * HttpSession session,HttpServletRequest request,HttpServletResponse
	 * response){ Json json = new Json(); try { List<Filter> filters = new
	 * ArrayList<>();
	 * 
	 * filters.add(Filter.eq("status", 1)); filters.add(Filter.eq("type",0));
	 * if(storeId != null){ Store store = storeService.find(storeId);
	 * filters.add(Filter.eq("store", store)); } if(startDay!=null &&
	 * endDay!=null){ if(startDay.compareTo(endDay)>0){ throw new
	 * Exception("查询开始时间不能大于结束时间"); } } if(startDay !=null){
	 * filters.add(Filter.ge("createDate", startDay)); } if(endDay != null){
	 * filters.add(Filter.le("createDate", endDay)); }
	 * 
	 * List<StoreAccountLog> storeAccountLogs =
	 * storeAccountLogService.findList(null,filters,null); //
	 * Page<StoreAccountLog> page=storeAccountLogService.findPage(pageable);
	 * List<xianxiaData> ans = new ArrayList<>(); for(StoreAccountLog log :
	 * storeAccountLogs){ xianxiaData data = new xianxiaData();
	 * data.setId(log.getId());
	 * data.setStoreName(log.getStore().getStoreName());
	 * data.setMoney(log.getMoney()); data.setProfile(log.getProfile());
	 * data.setCreateDate(log.getCreateDate()); ans.add(data); }
	 * 
	 * StringBuffer sb2 = new StringBuffer(); sb2.append("门店线下充值报表"); String
	 * fileName = "门店线下充值报表.xls"; // Excel文件名 String tableTitle =
	 * sb2.toString(); // Excel表标题 String configFile =
	 * "storeRechargeXianxia.xml"; // 配置文件 com.grain.controller.BaseController
	 * excelCon = new com.grain.controller.BaseController();
	 * excelCon.export2Excel(request, response, ans, fileName, tableTitle,
	 * configFile);
	 * 
	 * json.setSuccess(true); json.setMsg("查找成功"); json.setObj(ans);
	 * 
	 * } catch (Exception e) { // TODO: handle exception json.setSuccess(false);
	 * json.setMsg("查询失败:"+e.getMessage()); json.setObj(e); } return json; }
	 */

	/**
	 * 获取门店或者分公司
	 * 
	 * @param type
	 * @return
	 */
	@RequestMapping("/names")
	@ResponseBody
	public Json name(Integer type) {
		Json json = new Json();
		try {
			List<HashMap<String, Object>> ans = new ArrayList();
			List<Object[]> list = new ArrayList();
			if (type.intValue() == 1) {
				String sql = "select id,store_name as name from hy_store ";
				list = this.storeService.statis(sql);
			} else {
				String sql = "select department,company_name as name from hy_company ";
				list = this.storeService.statis(sql);
			}
			for (Object[] object : list) {
				HashMap<String, Object> map = new HashMap();
				map.put("id", object[0]);
				map.put("name", object[1]);
				ans.add(map);
			}
			json.setMsg("查询成功");
			json.setObj(ans);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("查询失败：" + e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
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
	
	
	public class RechargeDate {
		private String departmentName;
		private String amount;
		private String balance;
		private String date;
		private String storeType;
		private String areaName;

		public String getDepartmentName() {
			return departmentName;
		}

		public void setDepartmentName(String departmentName) {
			this.departmentName = departmentName;
		}

		public String getAmount() {
			return amount;
		}

		public void setAmount(String amount) {
			this.amount = amount;
		}

		public String getBalance() {
			return balance;
		}

		public void setBalance(String balance) {
			this.balance = balance;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getStoreType() {
			return storeType;
		}

		public void setStoreType(String storeType) {
			this.storeType = storeType;
		}

		public String getAreaName() {
			return areaName;
		}

		public void setAreaName(String areaName) {
			this.areaName = areaName;
		}
		
	}

}
