package com.hongyu.controller.wj;

import static org.hamcrest.CoreMatchers.nullValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Filters;
import org.hibernate.cfg.JoinedSubclassFkSecondPass;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.HyTicketHotelOrderController;
import com.hongyu.controller.StoreAssignmentGuideOrderController;
import com.hongyu.controller.cwz.TicketAuditController;
import com.hongyu.controller.gdw.StoreLineController;
import com.hongyu.controller.gsbing.TicketHotelandsceneOrderCenterController;
import com.hongyu.controller.lbc.HyTicketSubscribeOrderCenterController;
import com.hongyu.controller.lbc.InsuranceOrderCenterController;
import com.hongyu.controller.liyang.StoreVisaOrderController;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyLineLabel;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyTicketHotel;
import com.hongyu.entity.HyTicketHotelandscene;
import com.hongyu.entity.HyTicketSubscribe;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.InsuranceOrder;
import com.hongyu.entity.InsurancePrice;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.entity.WithDrawCash;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.BranchPrePayDetailService;
import com.hongyu.service.BranchPrePayService;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineLabelService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HySupplierElementService;
import com.hongyu.service.HyTicketHotelService;
import com.hongyu.service.HyTicketHotelandsceneService;
import com.hongyu.service.HyTicketSubscribeService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StoreService;
import com.hongyu.service.WithDrawCashService;
import com.hongyu.util.Constants;

/**
 * 财务可看，门店预存款列表
 * 查询条件：门店名称+时间区间
 * 查询结果列表：门店、预存款收入、预存款支出、当前余额。
 * 点击门店名称能看到该门店的预存款收入、支出和提现明细。
 * 收入明细页点击退款类型可看对应订单的详情，具体参考支出明细页的点击订单编号页面。
 * @author wj
 *
 */
@Controller
@RequestMapping("/admin/storeRechargeStatics")
public class StoreRechargeController {
	
	

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
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "withDrawCashServiceImpl")
	WithDrawCashService withDrawCashService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	@Resource(name = "hyLineLabelServiceImpl")
	HyLineLabelService hyLineLabelService;
	@Resource(name="hyTicketSubscribeServiceImpl")
	HyTicketSubscribeService hyTicketSubscribeService;
	@Resource(name="hyTicketHotelServiceImpl")
	private HyTicketHotelService hyTicketHotelService;
	@Resource(name="hyTicketHotelandsceneServiceImpl")
	private HyTicketHotelandsceneService hyTicketHotelandsceneService;
	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;
	@Resource(name = "insuranceServiceImpl")
	private InsuranceService insuranceService;
	
//	@RequestMapping("/list/view")
//	@ResponseBody
//	public Json list(@DateTimeFormat(pattern="yyyy-MM-dd")Date startDate,@DateTimeFormat(pattern="yyyy-MM-dd")Date endDate,Long storeId,HttpSession session){
//		Json json = new Json();
//		try {
//			List<Store> stores = new ArrayList<>();
//			if(storeId!=null){
//				Store store = storeService.find(storeId);
//				stores.add(store);
//			}else{
//				stores = storeService.findAll();
//			}
//			List<PreSave> res = new ArrayList<>();
//			
//			String startday = "";
//			String endday = "";
//			if(startDate != null && endDate != null){
//				Calendar calendar = new GregorianCalendar();
//				calendar.setTime(endDate);
//				calendar.add(Calendar.DATE, 1);
//				Date nextEndDate = calendar.getTime();
//				
//				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
//				startday = formatter.format(startDate);  
//				endday = formatter.format(nextEndDate);
//			}
//			
//			
//			for(Store store : stores){
//				PreSave preSave = new PreSave();
//				preSave.setStoreName(store.getStoreName());
//				
//				StringBuffer sql1 = new StringBuffer();
//				StringBuffer sql2 = new StringBuffer();
//				sql1.append("select CAST(sum(log.money) AS DECIMAL(21, 3)) from ");
//				sql2.append("select CAST(sum(log.money) AS DECIMAL(21, 3)) from ");
//				if(startDate != null && endDate != null){			
//					sql1.append("(select log.money,log.store_id,log.type,log.`status` from hy_store_account_log log where log.create_date >= "
//							+ startday +" and log.create_date< "+ endday + " ) log");
//					sql2.append("(select log.money,log.store_id,log.type,log.`status` from hy_store_account_log log where log.create_date >= "
//							+ startday +" and log.create_date< "+ endday + " ) log");
//				}else{
//					sql1.append("hy_store_account_log log ");
//					sql2.append("hy_store_account_log log ");
//				}
//				
//				
//				
//				//优化
//				
//				sql1.append(" ,hy_store store where store.id = log.store_id and log.type<>1 "
//						+ " and log.type<>6 and log.type<>14 and (log.status =1 or log.status =5)"
//						+ " and log.store_id = " + store.getId());
//				sql2.append(",hy_store store where log.store_id = store.id "
//						+ " and (log.type=1 or log.type = 6 or log.type=14) and log.status = 1 and log.store_id = " + store.getId() +" ");
//				
//				
//				
////				//收入
////				StringBuffer sql1 = new StringBuffer();
////				sql1.append("select CAST(sum(log.money) AS DECIMAL(21, 3)) from hy_store_account_log log,hy_store store "
////						+ "where log.store_id = store.id "
////						+ "and log.type<>1 and log.type<>6 and log.type<>14 "
////						+ " and (log.status =1 or log.status =5) "
////						+ "and log.store_id = " + store.getId() +" ");
////				//支出
////				StringBuffer sql2 = new StringBuffer();
////				sql2.append("select CAST(sum(log.money) AS DECIMAL(21, 3)) from hy_store_account_log log,hy_store store "
////						+ "where log.store_id = store.id "
////						+ "and (log.type=1 or log.type = 6 or log.type=14) and log.status = 1 and log.store_id = " + store.getId() +" ");
////				if(startDate != null && endDate != null){
////					sql1.append(" and log.create_date >= " + startDate +" " + " and log.create_date <= " + endDate);
////					sql2.append(" and log.create_date >= " + startDate +" " + " and log.create_date <= " + endDate);
////				}
//				List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
//				List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());
//				BigDecimal income = BigDecimal.ZERO;
//				BigDecimal outcome = BigDecimal.ZERO;
//				BigDecimal balance = BigDecimal.ZERO;
//				if(list1!=null && !list1.isEmpty() && list1.get(0)!=null){
//					Object objects =list1.get(0);
//					income = income.add(objects == null ?BigDecimal.ZERO:(BigDecimal)objects);
//				}
//				if(list2!=null && !list2.isEmpty() && list2.get(0)!=null){
//					Object objects =list2.get(0);
//					outcome = outcome.add(objects == null ?BigDecimal.ZERO:(BigDecimal)objects);
//				}				
//				
//				List<Filter> filters2 = new ArrayList<>();
//				filters2.add(Filter.eq("store", store));
//				List<StoreAccount> storeAccounts = storeAccountService.findList(null,filters2,null);
//				if(!storeAccounts.isEmpty()){
//					balance = balance.add(storeAccounts.get(0).getBalance());
//				}
//				balance.setScale(3, BigDecimal.ROUND_HALF_UP);
//				preSave.setIncome(income);
//				preSave.setOutcome(outcome);
//				preSave.setBalance(balance);
//				preSave.setStoreId(store.getId());
//				res.add(preSave);
//			}
//			
//			
//			json.setSuccess(true);
//			json.setMsg("获取成功");
//			json.setObj(res);
//		} catch (Exception e) {
//			// TODO: handle exception
//			json.setSuccess(false);
//			json.setMsg("获取错误： "+e.getMessage());
//			e.printStackTrace();
//		}
//		return json;
//	}
	
	
	@RequestMapping("/list/view")
	@ResponseBody
	public Json list(@DateTimeFormat(pattern="yyyy-MM-dd")Date startDate,@DateTimeFormat(pattern="yyyy-MM-dd")Date endDate,Long areaId,Integer storeType,
			Pageable pageable,Long storeId,HttpSession session){
		Json json = new Json();
		try {
			List<PreSave> res = new ArrayList<>();
			
			String startday = "";
			String endday = "";
			if(startDate != null && endDate != null){
				
				if(startDate.compareTo(endDate) > 0){
					json.setSuccess(false);
					json.setMsg("查询开始时间不能大于结束时间");
					return json;
				}
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(endDate);
				calendar.add(Calendar.DATE, 1);
				Date nextEndDate = calendar.getTime();
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
				startday = formatter.format(startDate);  
				endday = formatter.format(nextEndDate);
			}
			if(storeId != null){
				Store store = storeService.find(storeId);
				if(store == null){
					throw new Exception("没有该门店");
				}
				StringBuffer sql1 = new StringBuffer();
				StringBuffer sql2 = new StringBuffer();
				PreSave preSave = new PreSave();
				
				//收入
				sql1.append("select CAST(sum(log.money) AS DECIMAL(21, 3)) from hy_store_account_log log,hy_store store "
					+ "where log.store_id = store.id "
					+ "and log.type<>1 and log.type<>6 and log.type<>14 "
					+ " and (log.status =1 or log.status =5) "
					+ "and log.store_id = " + storeId +" ");
				//支出
				sql2.append("select CAST(sum(log.money) AS DECIMAL(21, 3)) from hy_store_account_log log,hy_store store "
					+ "where log.store_id = store.id "
					+ "and (log.type=1 or log.type = 6 or log.type=14) and log.status = 1 and log.store_id = " + storeId +" ");
				
				if(startDate != null && endDate != null){
					sql1.append(" and log.create_date >= '" + startday +"' " + " and log.create_date < '" + endday+"' ");
					sql2.append(" and log.create_date >= '" + startday +"' " + " and log.create_date < '" + endday+"' ");
				}
				List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
				List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());
				
				BigDecimal income = BigDecimal.ZERO;
				BigDecimal outcome = BigDecimal.ZERO;
				BigDecimal balance = BigDecimal.ZERO;
				balance.setScale(3, BigDecimal.ROUND_HALF_UP);
				
				if(list1!=null && !list1.isEmpty() && list1.get(0)!=null){
					Object objects =list1.get(0);
					income = income.add(objects == null ?BigDecimal.ZERO:(BigDecimal)objects);
				}
				if(list2!=null && !list2.isEmpty() && list2.get(0)!=null){
					Object objects =list2.get(0);
					outcome = outcome.add(objects == null ?BigDecimal.ZERO:(BigDecimal)objects);
				}				
				
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("store", store));
				List<StoreAccount> storeAccounts = storeAccountService.findList(null,filters2,null);
				if(!storeAccounts.isEmpty()){
					balance = balance.add(storeAccounts.get(0).getBalance().setScale(3, BigDecimal.ROUND_HALF_UP));
				}
				preSave.setIncome(income);
				preSave.setOutcome(outcome);
				preSave.setBalance(balance);
				preSave.setStoreId(store.getId());
				preSave.setStoreName(store.getStoreName());
				preSave.setAreaName(store.getHyArea()==null?null:store.getHyArea().getFullName());
				Integer storetype1 = store.getStoreType();
				String storetype = null;
				//0虹宇门店，2直营门店，3非虹宇门店
				switch (storetype1) {
				case 0:storetype = "虹宇门店";break;
				case 2:storetype = "直营门店";break;	
				case 3:storetype = "非虹宇门店";break;
				default:
					break;
				}
				preSave.setStoreType(storetype);
				res.add(preSave);
			}else if(startDate != null && endDate !=null){
				StringBuffer sql = new StringBuffer();
				StringBuffer sql1 = new StringBuffer();
				StringBuffer sql2 = new StringBuffer();
				
				sql.append(" select sum1.income,sum2.outcome,CAST(account.balance AS DECIMAL(21, 3)) balance, sum1.store_name,sum1.id,area.full_name,(case store.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype from  ");
				sql1.append(" (select CAST(sum(log.money) AS DECIMAL(21, 3)) income,log.store_id id,"
						+ " store.store_name from hy_store_account_log log,hy_store store"
						+ " where log.store_id = store.id and store.status != 10"
						+ " and log.type<>1 and log.type<>6 and log.type<>14 and (log.status =1 or log.status =5) ");
				sql2.append(" (select CAST(sum(log.money) AS DECIMAL(21, 3)) outcome,log.store_id id "
						+ " from hy_store_account_log log,hy_store store "
						+ " where log.store_id = store.id and (log.type=1 or log.type = 6 or log.type=14) and log.status = 1 and store.status != 10");
				if( areaId != null){
					sql1.append(" and store.area_id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
					sql2.append(" and store.area_id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
				}
				if( storeType != null){
					sql1.append(" and store.type = "+storeType);
					sql2.append(" and store.type = "+storeType);
				}
//				if(startDate != null && endDate !=null){
					sql1.append(" and log.create_date >= '"+ startday +"' and log.create_date < '" + endday+"' ");
					sql2.append(" and log.create_date >= '"+ startday +"' and log.create_date < '" + endday+"' ");
//				}
				sql1.append(" group by log.store_id) sum1 ,");
				sql2.append(" group by log.store_id) sum2,hy_store_account account,hy_store store,hy_area area "
						+ " where sum1.id = sum2.id and sum1.id = account.store and sum1.id = store.id and store.area_id = area.id "
						+ " order by balance desc");
				sql.append(sql1.toString());
				sql.append(sql2.toString());
				List<Object[]> list = storeAccountLogService.statis(sql.toString());			
				
				for (Object[] objects : list) {
					PreSave preSave = new PreSave();
					preSave.setIncome(objects[0]==null?BigDecimal.ZERO:(BigDecimal)objects[0]);
					preSave.setOutcome(objects[1]==null?BigDecimal.ZERO:(BigDecimal)objects[1]);
					preSave.setBalance(objects[2]==null?BigDecimal.ZERO:(BigDecimal)objects[2]);
					preSave.setStoreName(objects[3]==null?"":objects[3].toString());
					preSave.setAreaName(objects[5]==null?"":objects[5].toString());
					preSave.setStoreType(objects[6]==null?"":objects[6].toString());
					Long storeid = (long)0;
					if(objects[4] != null){
						BigInteger temp =(BigInteger) objects[4];
						storeid = temp.longValue();
					}
					preSave.setStoreId(storeid);
					res.add(preSave);
				}
			}else {
				StringBuffer sql = new StringBuffer();
				sql.append(" select outcome.*,area.full_name, (case store.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype from hy_store store, hy_area area,");
				sql.append(" (select sum1.income,sum2.outcome,CAST(account.balance AS DECIMAL(21, 3)) balance, sum1.store_name,sum1.id from ");
				sql.append(" (select store.id,IFNULL(res2.income,0) income,store.store_name,store.status,store.area_id ,store.type from hy_store store left join ");
				sql.append(" (select CAST(sum(log.money) AS DECIMAL(21, 3)) income,log.store_id id,store.store_name ");
				sql.append(" from hy_store store left join hy_store_account_log log on log.store_id = store.id ");
				sql.append(" where log.type<>1 and log.type<>6 and log.type<>14 and (log.status =1 or log.status =5) ");
				sql.append(" group by store.id) res2 on store.id = res2.id");
				sql.append(" ) sum1 ,");
				sql.append(" (select store.id,IFNULL(res1.outcome,0) outcome from hy_store store left join ");
				sql.append(" (select CAST(sum(log.money) AS DECIMAL(21, 3)) outcome,log.store_id id from hy_store store join hy_store_account_log log on log.store_id = store.id ");
				sql.append(" where  (log.type=1 or log.type = 6 or log.type=14) and log.status = 1 ");
				sql.append(" group by store.id) res1 on store.id = res1.id) sum2 left join hy_store_account account on sum2.id = account.store");
				sql.append(" where sum1.id = sum2.id and sum1.status != 10) outcome");
				sql.append(" where outcome.id = store.id and store.area_id = area.id");
				
				if( areaId != null){
					sql.append(" and area.id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
				}
				if( storeType != null){
					sql.append(" and store.type = "+storeType);
				}
				
				sql.append(" order by balance desc");
				
				List<Object[]> list = storeAccountLogService.statis(sql.toString());			
				
				for (Object[] objects : list) {
					PreSave preSave = new PreSave();
					preSave.setIncome(objects[0]==null?BigDecimal.ZERO:(BigDecimal)objects[0]);
					preSave.setOutcome(objects[1]==null?BigDecimal.ZERO:(BigDecimal)objects[1]);
					preSave.setBalance(objects[2]==null?BigDecimal.ZERO:(BigDecimal)objects[2]);
					preSave.setStoreName(objects[3]==null?"":objects[3].toString());
					preSave.setAreaName(objects[5]==null?"":objects[5].toString());
					preSave.setStoreType(objects[6]==null?"":objects[6].toString());
					Long storeid = (long)0;
					if(objects[4] != null){
						BigInteger temp =(BigInteger) objects[4];
						storeid = temp.longValue();
					}
					preSave.setStoreId(storeid);
					res.add(preSave);
				}
			}
			
			int page = pageable.getPage();
			int rows = pageable.getRows();
			Map<String, Object> answer = new HashMap<>();
			answer.put("total", res.size());
			answer.put("pageNumber", page);
			answer.put("pageSize", rows);
			answer.put("rows", res.subList((page - 1) * rows, page * rows > res.size() ? res.size() : page * rows)); // 手动分页？

			json.setObj(answer);
			json.setSuccess(true);
			json.setMsg("获取成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	
	
	
	
	@RequestMapping("/download/list/view")
	public void downloadlist(@DateTimeFormat(pattern="yyyy-MM-dd")Date startDate,@DateTimeFormat(pattern="yyyy-MM-dd")Date endDate,Long storeId,Long areaId,Integer storeType,
			HttpSession session,HttpServletRequest request,HttpServletResponse response){
		try {
			List<PreSave> res = new ArrayList<>();
			
			String startday = "";
			String endday = "";
			if(startDate != null && endDate != null){
				
				if(startDate.compareTo(endDate) > 0){
					throw new Exception("查询开始时间不能大于结束时间");
				}
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(endDate);
				calendar.add(Calendar.DATE, 1);
				Date nextEndDate = calendar.getTime();
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
				startday = formatter.format(startDate);  
				endday = formatter.format(nextEndDate);
			}
			if(storeId != null){
				Store store = storeService.find(storeId);
				if(store == null){
					throw new Exception("没有该门店");
				}
				StringBuffer sql1 = new StringBuffer();
				StringBuffer sql2 = new StringBuffer();
				PreSave preSave = new PreSave();
				
				//收入
				sql1.append("select CAST(sum(log.money) AS DECIMAL(21, 3)) from hy_store_account_log log,hy_store store "
					+ "where log.store_id = store.id "
					+ "and log.type<>1 and log.type<>6 and log.type<>14 "
					+ " and (log.status =1 or log.status =5) "
					+ "and log.store_id = " + storeId +" ");
				//支出
				sql2.append("select CAST(sum(log.money) AS DECIMAL(21, 3)) from hy_store_account_log log,hy_store store "
					+ "where log.store_id = store.id "
					+ "and (log.type=1 or log.type = 6 or log.type=14) and log.status = 1 and log.store_id = " + storeId +" ");
				
				if(startDate != null && endDate != null){
					sql1.append(" and log.create_date >= '" + startday +"' " + " and log.create_date < '" + endday+"' ");
					sql2.append(" and log.create_date >= '" + startday +"' " + " and log.create_date < '" + endday+"' ");
				}
				List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
				List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());
				
				BigDecimal income = BigDecimal.ZERO;
				BigDecimal outcome = BigDecimal.ZERO;
				BigDecimal balance = BigDecimal.ZERO;
				balance.setScale(3, BigDecimal.ROUND_HALF_UP);
				
				if(list1!=null && !list1.isEmpty() && list1.get(0)!=null){
					Object objects =list1.get(0);
					income = income.add(objects == null ?BigDecimal.ZERO:(BigDecimal)objects);
				}
				if(list2!=null && !list2.isEmpty() && list2.get(0)!=null){
					Object objects =list2.get(0);
					outcome = outcome.add(objects == null ?BigDecimal.ZERO:(BigDecimal)objects);
				}				
				
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("store", store));
				List<StoreAccount> storeAccounts = storeAccountService.findList(null,filters2,null);
				if(!storeAccounts.isEmpty()){
					balance = balance.add(storeAccounts.get(0).getBalance().setScale(3, BigDecimal.ROUND_HALF_UP));
				}
				preSave.setIncome(income);
				preSave.setOutcome(outcome);
				preSave.setBalance(balance);
				preSave.setStoreId(store.getId());
				preSave.setStoreName(store.getStoreName());
				preSave.setAreaName(store.getHyArea()==null?null:store.getHyArea().getFullName());
				Integer storetype1 = store.getStoreType();
				String storetype = null;
				//0虹宇门店，2直营门店，3非虹宇门店
				switch (storetype1) {
				case 0:storetype = "虹宇门店";break;
				case 2:storetype = "直营门店";break;	
				case 3:storetype = "非虹宇门店";break;
				default:
					break;
				}
				preSave.setStoreType(storetype);
				res.add(preSave);
			}else if(startDate != null && endDate !=null){
				StringBuffer sql = new StringBuffer();
				StringBuffer sql1 = new StringBuffer();
				StringBuffer sql2 = new StringBuffer();
				
				sql.append(" select sum1.income,sum2.outcome,CAST(account.balance AS DECIMAL(21, 3)) balance, sum1.store_name,sum1.id,area.full_name,(case store.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype from  ");
				sql1.append(" (select CAST(sum(log.money) AS DECIMAL(21, 3)) income,log.store_id id,"
						+ " store.store_name from hy_store_account_log log,hy_store store"
						+ " where log.store_id = store.id and store.status != 10"
						+ " and log.type<>1 and log.type<>6 and log.type<>14 and (log.status =1 or log.status =5) ");
				sql2.append(" (select CAST(sum(log.money) AS DECIMAL(21, 3)) outcome,log.store_id id "
						+ " from hy_store_account_log log,hy_store store "
						+ " where log.store_id = store.id and (log.type=1 or log.type = 6 or log.type=14) and log.status = 1 and store.status != 10");
				if( areaId != null){
					sql1.append(" and store.area_id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
					sql2.append(" and store.area_id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
				}
				if( storeType != null){
					sql1.append(" and store.type = "+storeType);
					sql2.append(" and store.type = "+storeType);
				}
//				if(startDate != null && endDate !=null){
					sql1.append(" and log.create_date >= '"+ startday +"' and log.create_date < '" + endday+"' ");
					sql2.append(" and log.create_date >= '"+ startday +"' and log.create_date < '" + endday+"' ");
//				}
				sql1.append(" group by log.store_id) sum1 ,");
				sql2.append(" group by log.store_id) sum2,hy_store_account account,hy_store store,hy_area area "
						+ " where sum1.id = sum2.id and sum1.id = account.store and sum1.id = store.id and store.area_id = area.id "
						+ " order by balance desc");
				sql.append(sql1.toString());
				sql.append(sql2.toString());
				System.out.println(sql);
				List<Object[]> list = storeAccountLogService.statis(sql.toString());			
				
				for (Object[] objects : list) {
					PreSave preSave = new PreSave();
					preSave.setIncome(objects[0]==null?BigDecimal.ZERO:(BigDecimal)objects[0]);
					preSave.setOutcome(objects[1]==null?BigDecimal.ZERO:(BigDecimal)objects[1]);
					preSave.setBalance(objects[2]==null?BigDecimal.ZERO:(BigDecimal)objects[2]);
					preSave.setStoreName(objects[3]==null?"":objects[3].toString());
					preSave.setAreaName(objects[5]==null?"":objects[5].toString());
					preSave.setStoreType(objects[6]==null?"":objects[6].toString());
					Long storeid = (long)0;
					if(objects[4] != null){
						BigInteger temp =(BigInteger) objects[4];
						storeid = temp.longValue();
					}
					preSave.setStoreId(storeid);
					res.add(preSave);
				}
			}else {
				StringBuffer sql = new StringBuffer();
				sql.append(" select outcome.*,area.full_name, (case store.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype from hy_store store, hy_area area,");
				sql.append(" (select sum1.income,sum2.outcome,CAST(account.balance AS DECIMAL(21, 3)) balance, sum1.store_name,sum1.id from ");
				sql.append(" (select store.id,IFNULL(res2.income,0) income,store.store_name,store.status,store.area_id ,store.type from hy_store store left join ");
				sql.append(" (select CAST(sum(log.money) AS DECIMAL(21, 3)) income,log.store_id id,store.store_name ");
				sql.append(" from hy_store store left join hy_store_account_log log on log.store_id = store.id ");
				sql.append(" where log.type<>1 and log.type<>6 and log.type<>14 and (log.status =1 or log.status =5) ");
				sql.append(" group by store.id) res2 on store.id = res2.id");
				sql.append(" ) sum1 ,");
				sql.append(" (select store.id,IFNULL(res1.outcome,0) outcome from hy_store store left join ");
				sql.append(" (select CAST(sum(log.money) AS DECIMAL(21, 3)) outcome,log.store_id id from hy_store store join hy_store_account_log log on log.store_id = store.id ");
				sql.append(" where  (log.type=1 or log.type = 6 or log.type=14) and log.status = 1 ");
				sql.append(" group by store.id) res1 on store.id = res1.id) sum2 left join hy_store_account account on sum2.id = account.store");
				sql.append(" where sum1.id = sum2.id and sum1.status != 10) outcome");
				sql.append(" where outcome.id = store.id and store.area_id = area.id");
				
				if( areaId != null){
					sql.append(" and area.id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
				}
				if( storeType != null){
					sql.append(" and store.type = "+storeType);
				}
				
				sql.append(" order by balance desc");
				
				List<Object[]> list = storeAccountLogService.statis(sql.toString());			
				
				for (Object[] objects : list) {
					PreSave preSave = new PreSave();
					preSave.setIncome(objects[0]==null?BigDecimal.ZERO:(BigDecimal)objects[0]);
					preSave.setOutcome(objects[1]==null?BigDecimal.ZERO:(BigDecimal)objects[1]);
					preSave.setBalance(objects[2]==null?BigDecimal.ZERO:(BigDecimal)objects[2]);
					preSave.setStoreName(objects[3]==null?"":objects[3].toString());
					preSave.setAreaName(objects[5]==null?"":objects[5].toString());
					preSave.setStoreType(objects[6]==null?"":objects[6].toString());
					Long storeid = (long)0;
					if(objects[4] != null){
						BigInteger temp =(BigInteger) objects[4];
						storeid = temp.longValue();
					}
					preSave.setStoreId(storeid);
					res.add(preSave);
				}
			}
			
			StringBuffer sb2 = new StringBuffer();
			sb2.append("门店预存款收支报表");
			String fileName = "门店预存款收支报表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "storePresaveOffsetStatics.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, res, fileName, tableTitle, configFile);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
	 * 充值列表
	 * @param storeId
	 * @param pageable
	 * @param startDate
	 * @param endDate
	 * @param session
	 * @return
	 */
//	@RequestMapping("/czlist/view") 
	@ResponseBody
	public Json czlist(Long storeId,Pageable pageable,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,HttpSession session){
		Json json=new Json();
		try {
//			String username=(String)session.getAttribute(CommonAttributes.Principal);
//			HyAdmin hyAdmin=hyAdminService.find(username);
//			Department department=hyAdmin.getDepartment();
//			List<Filter> filters=new LinkedList<>();
//			filters.add(Filter.eq("department", department));
//			List<Store> stores=storeService.findList(null, filters, null);
			Store store = storeService.find(storeId);
			if(store!=null){
				List<Filter> filters2=new LinkedList<>();
				filters2.add(Filter.eq("store", store));
				if(startDate!=null){
					filters2.add(Filter.ge("createDate", startDate));
				}
				if(endDate!=null){
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					calendar.add(Calendar.DATE, 1);
					endDate  = calendar.getTime();
					filters2.add(Filter.lt("createDate", endDate));
				}
				filters2.add(Filter.ne("type", 1));//1报名抵扣记录
				filters2.add(Filter.ne("type", 6));//6海报设计抵扣
				filters2.add(Filter.ne("type", 14));//14门店提现
				filters2.add(Filter.ne("status", 4)); //不统计未成功支付的订单
				pageable.setFilters(filters2);
				List<Order> orders = new ArrayList<>();
				orders.add(Order.desc("createDate"));
				pageable.setOrders(orders);
				Page<StoreAccountLog> page=storeAccountLogService.findPage(pageable);
				List<Map<String, Object>> result=new LinkedList<>();
				BigDecimal sum  = BigDecimal.ZERO;
				for(StoreAccountLog tmp:page.getRows()){
					Map<String, Object> map=new HashMap<>();
					map.put("id", tmp.getId());
					map.put("status", tmp.getStatus());
					map.put("type", tmp.getType());
					map.put("money", tmp.getMoney());
					map.put("createDate", tmp.getCreateDate());
					map.put("profile", tmp.getProfile());
					map.put("orderSn", tmp.getOrderSn());
					List<Filter> filters3 = new ArrayList<>();
					filters3.add(Filter.eq("orderNumber", tmp.getOrderSn()));
					List<HyOrder> orders2 = hyOrderService.findList(null,filters3,null);
					if(!orders2.isEmpty() &&orders2.size()>0){
						map.put("orderId", orders2.get(0).getId());
						map.put("orderType", orders2.get(0).getType());
					}
					sum = sum.add(tmp.getMoney());
					result.add(map);
				}
				Map<String, Object> hMap=new HashMap<>();
				hMap.put("total", page.getTotal());
				hMap.put("pageNumber", page.getPageNumber());
				hMap.put("pageSize", page.getPageSize());
				hMap.put("rows", result);
				hMap.put("sum", sum);
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(hMap);
			}else{
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping("/czlist/view") 
	@ResponseBody
	public Json czlistNew(Long storeId,Pageable pageable,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,HttpSession session){
		Json json=new Json();
		try {
			Store store = storeService.find(storeId);
			if(store!=null){
				StringBuffer buffer = new StringBuffer();
				StringBuffer buffer2 = new StringBuffer();
				buffer.append(" select l.id,l.status,l.type,CAST(l.money AS DECIMAL(21, 3)),l.create_date,l.profile,l.order_sn,o.id orderid,o.type ordertype from hy_store_account_log l left join hy_order o on l.order_sn = o.order_number");
				buffer.append(" where l.type<>1 and l.type<>6 and l.type<> 14 and l.status<>4");
				buffer.append(" and l.store_id =" + storeId );
				//计算总计
				buffer2.append(" select CAST(sum(l.money) AS DECIMAL(21, 3)) from hy_store_account_log l left join hy_order o on l.order_sn = o.order_number");
				buffer2.append(" where l.type<>1 and l.type<>6 and l.type<> 14 and l.status<>4");
				buffer2.append(" and l.store_id =" + storeId );
				
				if(startDate!=null){
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					String start = formatter.format(startDate);  
					
					buffer.append(" and l.create_date >='"+start+"'");
					buffer2.append(" and l.create_date >='"+start.toString()+"'");
				}
				if(endDate!=null){
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					calendar.add(Calendar.DATE, 1);
					endDate  = calendar.getTime();
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					String end = formatter.format(endDate);
					
					buffer.append(" and l.create_date <'"+end.toString()+"'");
					buffer2.append(" and l.create_date <'"+end.toString()+"'");
				}
				buffer.append(" order by l.create_date DESC");
				List<Object[]> list = storeAccountLogService.statis(buffer.toString());
				int page = pageable.getPage();
				int rows = pageable.getRows();
				List<Object[]> sublist = list.subList((page - 1) * rows, page * rows > list.size() ? list.size() : page * rows);
				BigDecimal sum = BigDecimal.ZERO;
				List<Map<String, Object>> result=new LinkedList<>();
				for(Object[] temp : sublist){
					Map<String, Object> map=new HashMap<>();
					map.put("id", temp[0]);
					map.put("status", temp[1]);
					map.put("type", temp[2]);
					map.put("money", temp[3]);
					map.put("createDate", temp[4]);
					map.put("profile", temp[5]);
					map.put("orderSn", temp[6]);
					map.put("orderId", temp[7]);
					map.put("orderType", temp[8]);
					
					sum = sum.add(temp[3] == null ? BigDecimal.ZERO :new BigDecimal(temp[3].toString()));
					result.add(map);
				}
				
				List<Object[]> list2 = hyOrderService.statis(buffer2.toString());
				BigDecimal allmoney = BigDecimal.ZERO;
				if (!list2.isEmpty() &&list2.size()!=0&& list2.get(0)!=null) {
					Object iObject = list2.get(0);
					allmoney = new BigDecimal(iObject.toString());
				}
				
				Map<String, Object> hMap=new HashMap<>();
				hMap.put("total", list.size());
				hMap.put("pageNumber", page);
				hMap.put("pageSize", rows);
				hMap.put("rows", result);
				hMap.put("sum", sum);
				hMap.put("all", allmoney);
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(hMap);
			}else{
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 抵扣列表
	 * @param storeId
	 * @param pageable
	 * @param startDate
	 * @param endDate
	 * @param session
	 * @return
	 */
//	@RequestMapping("/dklist/view")
	@ResponseBody
	public Json dklist(Long storeId,Pageable pageable,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,HttpSession session){
		Json json=new Json();
		try {
//			String username=(String)session.getAttribute(CommonAttributes.Principal);
//			HyAdmin hyAdmin=hyAdminService.find(username);
//			Department department=hyAdmin.getDepartment();
//			List<Filter> filters=new LinkedList<>();
//			filters.add(Filter.eq("department", department));
			Store store = storeService.find(storeId);
			if(store!=null){
				List<Filter> filters2=new LinkedList<>();
				filters2.add(Filter.eq("store", store));
				if(startDate!=null){
					filters2.add(Filter.ge("createDate", startDate));
				}
				if(endDate!=null){
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					calendar.add(Calendar.DATE, 1);
					endDate  = calendar.getTime();
					filters2.add(Filter.lt("createDate", endDate));
				}
				filters2.add(Filter.ge("type", 1));
				filters2.add(Filter.le("type", 6));
//				filters2.add(Filter.ne("type", 0));//0充值
				filters2.add(Filter.ne("type", 2));//2分成
				filters2.add(Filter.ne("type", 3));//3退团
				filters2.add(Filter.ne("type", 4));//2消团
				filters2.add(Filter.ne("type", 5));//2供应商驳回
//				filters2.add(Filter.ne("type", 7));//7租借导游退款
				pageable.setFilters(filters2);
				List<Order> orders = new ArrayList<>();
				orders.add(Order.desc("createDate"));
				pageable.setOrders(orders);
				Page<StoreAccountLog> page=storeAccountLogService.findPage(pageable);
				List<Map<String, Object>> result=new LinkedList<>();
				BigDecimal sum = BigDecimal.ZERO;
				for(StoreAccountLog tmp:page.getRows()){
					Map<String, Object> map=new HashMap<>();
					map.put("id", tmp.getId());
					map.put("status", tmp.getStatus());
					map.put("type", tmp.getType());
					map.put("money", tmp.getMoney());
					map.put("createDate", tmp.getCreateDate());
					map.put("profile", tmp.getProfile());
					map.put("orderSn", tmp.getOrderSn());
					List<Filter> filters3 = new ArrayList<>();
					filters3.add(Filter.eq("orderNumber", tmp.getOrderSn()));
					List<HyOrder> orders2 = hyOrderService.findList(null,filters3,null);
					if(!orders2.isEmpty() &&orders2.size()>0){
						map.put("orderId", orders2.get(0).getId());
						map.put("orderType", orders2.get(0).getType());
					}
					sum = sum.add(tmp.getMoney());
					result.add(map);
				}
				Map<String, Object> hMap=new HashMap<>();
				hMap.put("total", page.getTotal());
				hMap.put("pageNumber", page.getPageNumber());
				hMap.put("pageSize", page.getPageSize());
				hMap.put("rows", result);
				hMap.put("sum",sum);
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(hMap);
			}else{
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping("/dklist/view") 
	@ResponseBody
	public Json dklistNew(Long storeId,Pageable pageable,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,HttpSession session){
		Json json=new Json();
		try {
			Store store = storeService.find(storeId);
			if(store!=null){
				StringBuffer buffer = new StringBuffer();
				StringBuffer buffer2 = new StringBuffer();
				buffer.append(" select l.id,l.status,l.type,CAST(l.money AS DECIMAL(21, 3)),l.create_date,l.profile,l.order_sn,o.id orderid,o.type ordertype from hy_store_account_log l left join hy_order o on l.order_sn = o.order_number");
				buffer.append(" where (l.type=1 or l.type=6) ");
				buffer.append(" and l.store_id =" + storeId );
				//计算总计
				buffer2.append(" select CAST(sum(l.money) AS DECIMAL(21, 3)) from hy_store_account_log l left join hy_order o on l.order_sn = o.order_number");
				buffer2.append(" where (l.type=1 or l.type=6) ");
				buffer2.append(" and l.store_id =" + storeId );
				
				if(startDate!=null){
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					String start = formatter.format(startDate);  
					
					buffer.append(" and l.create_date >='"+start+"'");
					buffer2.append(" and l.create_date >='"+start.toString()+"'");
				}
				if(endDate!=null){
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					calendar.add(Calendar.DATE, 1);
					endDate  = calendar.getTime();
					
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
					String end = formatter.format(endDate);
					
					buffer.append(" and l.create_date <'"+end.toString()+"'");
					buffer2.append(" and l.create_date <'"+end.toString()+"'");
				}
				buffer.append(" order by l.create_date DESC");
				List<Object[]> list = storeAccountLogService.statis(buffer.toString());
				int page = pageable.getPage();
				int rows = pageable.getRows();
				List<Object[]> sublist = list.subList((page - 1) * rows, page * rows > list.size() ? list.size() : page * rows);
				BigDecimal sum = BigDecimal.ZERO;
				List<Map<String, Object>> result=new LinkedList<>();
				for(Object[] temp : sublist){
					Map<String, Object> map=new HashMap<>();
					map.put("id", temp[0]);
					map.put("status", temp[1]);
					map.put("type", temp[2]);
					map.put("money", temp[3]);
					map.put("createDate", temp[4]);
					map.put("profile", temp[5]);
					map.put("orderSn", temp[6]);
					map.put("orderId", temp[7]);
					map.put("orderType", temp[8]);
					
					sum = sum.add(temp[3] == null ? BigDecimal.ZERO :new BigDecimal(temp[3].toString()));
					result.add(map);
				}
				
				List<Object[]> list2 = hyOrderService.statis(buffer2.toString());
				BigDecimal allmoney = BigDecimal.ZERO;
				if (!list2.isEmpty() &&list2.size()!=0&& list2.get(0)!=null) {
					Object iObject = list2.get(0);
					allmoney = new BigDecimal(iObject.toString());
				}
				
				Map<String, Object> hMap=new HashMap<>();
				hMap.put("total", list.size());
				hMap.put("pageNumber", page);
				hMap.put("pageSize", rows);
				hMap.put("rows", result);
				hMap.put("sum", sum);
				hMap.put("all", allmoney);
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(hMap);
			}else{
				json.setSuccess(false);
				json.setMsg("所属门店不存在");
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 提现列表
	 * @param pageable
	 * @param session
	 * @param storeId
	 * @param applyStartTime
	 * @param applyEndTime
	 * @return
	 */
	@RequestMapping(value="/txlist/view")
	@ResponseBody
	public Json WithDrawCashRecordList(Pageable pageable, HttpSession session, Long storeId,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date applyStartTime, 
			@DateTimeFormat(pattern="yyyy-MM-dd") Date applyEndTime){
		
		//看提现记录的时候只能看到自己门店的提现记录
		Json json = new Json();
		try {
			Calendar calendar = Calendar.getInstance();

			Store store = storeService.find(storeId);
			
			List<HashMap<String, Object>> list = new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters = new ArrayList<Filter>();
			
			filters.add(Filter.eq("storeId", store.getId()));
						
			
			if(applyStartTime != null) {
				filters.add(Filter.ge("applyTime", applyStartTime));
			}
			if(applyEndTime != null) {
				calendar.setTime(applyEndTime);
				calendar.add(Calendar.DATE, 1);
				applyEndTime = calendar.getTime();
				filters.add(Filter.lt("applyTime", applyEndTime));
			}
			
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("applyTime"));
			pageable.setOrders(orders);
			Page<WithDrawCash> page = withDrawCashService.findPage(pageable);
			
			if(page.getTotal() > 0){
				for(WithDrawCash withDrawCash : page.getRows()){
					HashMap<String,Object> comMap=new HashMap<String,Object>();
					
					comMap.put("id", withDrawCash.getId());
					comMap.put("storeId", withDrawCash.getStoreId());
					comMap.put("storeName", withDrawCash.getStoreName());
					comMap.put("cash", withDrawCash.getCash());
					comMap.put("status", withDrawCash.getStatus());
					comMap.put("applyTime", withDrawCash.getApplyTime());
					comMap.put("financeAuditTime", withDrawCash.getFinanceAuditTime());
					comMap.put("auditor", withDrawCash.getAuditor());
					comMap.put("payTime", withDrawCash.getPayTime());
					comMap.put("payer", withDrawCash.getPayer());
					comMap.put("rejectRemark", withDrawCash.getRejectRemark());
					list.add(comMap);
				}
			}
			map.put("rows", list);
		    map.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    map.put("pageSize", Integer.valueOf(pageable.getRows()));
		    map.put("total",Long.valueOf(page.getTotal()));
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(null);
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * 支出订单明细
	 */
	@RequestMapping("/orderDetail/view")
	@ResponseBody
	public Json orderDetail(String orderSn) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderNumber", orderSn));
			List<HyOrder> orders = hyOrderService.findList(null,filters,null);
			if(orders==null || orders.isEmpty()) {
				json.setSuccess(false);
				json.setMsg("订单编号无效");
				json.setObj(null);
				return json;
			}
			
			json.setSuccess(true);
			json.setMsg("查找成功");
			json.setObj(orders.get(0));
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查找失败");
			json.setObj(e);
		}
		return json;
		
	} 
	
	/**
	 * 获取门店当前余额
	 * @param storeId
	 * @param session
	 * @return
	 */
	@RequestMapping("/getBalance/view")
	@ResponseBody
	public Json getBalance(Long storeId,HttpSession session){
		Json json=new Json();
		try {
					
			Store store=storeService.find(storeId);
			if(store==null){
				json.setSuccess(false);
				json.setMsg("门店不存在");
			}else{
				List<Filter> filters2=new LinkedList<>();
				filters2.add(Filter.eq("store", store));
				List<StoreAccount> storeAccounts=storeAccountService.findList(null,filters2,null);
				if(storeAccounts!=null&&storeAccounts.size()>0){
					StoreAccount storeAccount=storeAccounts.get(0);
					json.setSuccess(true);
					json.setMsg("查询成功");
					json.setObj(storeAccount.getBalance());
				}else{
					json.setSuccess(false);
					json.setMsg("账户不存在");
				}
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	
	/**
	 * 获取相应订单详情
	 * @param id
	 * @param type //订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
	 * @return
	 */
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json detail(Long id,Integer type,HttpSession session){
		Json json = new Json();
		try {
			switch (type) {
			case 0: {
				json = daoyoudetail(id);
				break;
			}
			case 1: {
				json = xianludetail(id);
				break;
			}
			case 2: {
				json = rengoumenpiaodetailView(id);
				break;
			}
			case 3: {
				json = hoteldetailView(id);
				break;
			}
			case 4: {
				json = sixthPageOrder(id);
				break;
			}
			case 5: {
				json = hotelandscenedetail(id);
				break;
			}
			case 6: {
				json = insurancdetail(id);
				break;
			}
			case 7: {
				json = visadetail(id);
				break;
			}
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	
	@RequestMapping("/store/list") 
	@ResponseBody
	public Json store(){
		Json json = new Json();
		try {
//			List<Filter> filters = new LinkedList<>();
//			filters.add(Filter.eq("pstatus", value))
//			List<Store> stores = storeService.findList(null,filters,null);
			List<Store> stores = storeService.findAll();
			List<HashMap<String, Object>> list = new LinkedList<>();
			HashMap<String, Object> quanbu = new HashMap<>();
			quanbu.put("storeName", "全部");
			quanbu.put("storeId", null);
			list.add(quanbu);
			for(Store store : stores){
				HashMap<String, Object> map = new HashMap<>();
				map.put("storeName", store.getStoreName());
				map.put("storeId", store.getId());
				list.add(map);
			}
			json.setObj(list);
			json.setMsg("查询成功");
			json.setSuccess(true);
		
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("查询失败");
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
		
		
	}
	
	
	//导游详情页
	public Json daoyoudetail(Long id) {
		Json json = new Json();
		try {
			HyOrder hyOrder = hyOrderService.find(id);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hyOrder);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： ");
			json.setObj(e.getMessage());
		}
		return json;
	}
	
	
	//线路详情页
	public Json xianludetail(Long id) {
		Json json = new Json();
		try {
			HyOrder tmp = hyOrderService.find(id);
			if(tmp==null)
				throw new NullPointerException("找不到对应的订单");
			Map<String, Object> map = new HashMap<>();	
			map.put("id", tmp.getId());
			map.put("orderNumber", tmp.getOrderNumber());
			map.put("name", tmp.getName());
			map.put("status", tmp.getStatus());
			map.put("paystatus", tmp.getPaystatus());
			map.put("checkstatus", tmp.getCheckstatus());
			map.put("guideCheckStatus", tmp.getGuideCheckStatus());
			map.put("refundstatus", tmp.getRefundstatus());
			map.put("type", tmp.getType());
			map.put("source", tmp.getSource());
			map.put("people", tmp.getPeople());
			map.put("storeType", tmp.getStoreType());
			map.put("storeId", tmp.getStoreId());
			Long storeId = tmp.getStoreId();
			if(storeId!=null){
				Store store = storeService.find(storeId);
				map.put("storeName", store==null?"":store.getStoreName());
			}else{
				map.put("storeName", "");
			}
			map.put("operator", tmp.getOperator());
			map.put("creatorId", tmp.getCreatorId());
			map.put("adjustMoney", tmp.getAdjustMoney());
			map.put("discountedType", tmp.getDiscountedType());
			map.put("discountedId", tmp.getDiscountedId());
			map.put("discountedPrice", tmp.getDiscountedPrice());
			map.put("jiesuanMoney1", tmp.getJiesuanMoney1());
			map.put("jiusuanMoney", tmp.getJiusuanMoney());
			map.put("waimaiMoney", tmp.getWaimaiMoney());
			map.put("jiesuanTuikuan", tmp.getJiesuanTuikuan());
			map.put("waimaiTuikuan", tmp.getWaimaiTuikuan());
			map.put("baoxianJiesuanTuikuan", tmp.getBaoxianJiesuanTuikuan());
			map.put("baoxianWaimaiTuikuan", tmp.getBaoxianWaimaiTuikuan());
			map.put("storeFanLi", tmp.getStoreFanLi());
			map.put("ifjiesuan", tmp.getIfjiesuan());
			map.put("insuranceOrderDownloadUrl", tmp.getInsuranceOrderDownloadUrl());
			map.put("koudianMethod", tmp.getKoudianMethod());
			map.put("proportion", tmp.getProportion());
			map.put("headProportion", tmp.getHeadProportion());
			map.put("koudianMoney", tmp.getKoudianMoney());
			map.put("departure", tmp.getDeparture());
			map.put("fatuandate", tmp.getFatuandate());
			map.put("tianshu", tmp.getTianshu());
			map.put("huituanxinxi", tmp.getHuituanxinxi());
			map.put("xianlumingcheng", tmp.getXianlumingcheng());
			map.put("xianlutype", tmp.getXianlutype());
			map.put("fuwutype", tmp.getFuwutype());
			map.put("xingchenggaiyao", tmp.getXingchenggaiyao());
			map.put("tip", tmp.getTip());
			map.put("tipInstruction", tmp.getTipInstruction());
			map.put("contact", tmp.getContact());
			map.put("contactIdNumber", tmp.getContactIdNumber());
			map.put("phone", tmp.getPhone());
			map.put("remark", tmp.getRemark());
			map.put("contractId", tmp.getContractId());
			
			map.put("contractNumber", tmp.getContractNumber());
			map.put("contractType", tmp.getContractType());
			map.put("createtime", tmp.getCreatetime());
			map.put("modifytime", tmp.getModifytime());
			
			map.put("orderItems", tmp.getOrderItems());
			map.put("groupId", tmp.getGroupId());
			map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
			map.put("isDivideStatistic", tmp.getIsDivideStatistic());
			
			//查找额外保险信息
			Insurance insurance = insuranceService.getExtraInsuranceOfOrder(tmp);
			if(insurance!=null){

				Integer days = tmp.getTianshu();
				
				map.put("insurance",insurance.getRemark());
				
				for(InsurancePrice price:insurance.getInsurancePrices()){
					if(days.compareTo(price.getStartDay())>=0&&days.compareTo(price.getEndDay())<=0){
						map.put("insuranceMoney", price.getSalePrice());
						break;
					}
				}
				if(!map.containsKey("insuranceMoney")){
					throw new Exception("获取保险价格失败");
				}
			}
			Long groupId = tmp.getGroupId();
			if(groupId!=null){
				HyGroup hyGroup = hyGroupService.find(groupId);
				map.put("linePn", hyGroup.getGroupLinePn());
				HyLine hyLine = hyGroup.getLine();
				map.put("provider", hyLine.getHySupplier());	//供应商信息
				map.put("rebate", hyGroup.getFanliMoney()==null?0:hyGroup.getFanliMoney());
			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	//订购门票详情页
	public Json rengoumenpiaodetailView(Long id) {
		Json json = new Json();
		try {
			HyOrder hyOrder = hyOrderService.find(id);
			if(hyOrder==null) {
				throw new Exception("订单不存在");
			}
			List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
			if(hyOrderItems==null || hyOrderItems.isEmpty()) {

				throw new Exception("没有订单条目数据");
			}
			HyOrderItem hyOrderItem = hyOrderItems.get(0);
			HyTicketSubscribe hyTicketSubscribe = hyTicketSubscribeService.find(hyOrderItem.getProductId());
			Map<String, Object> map = new HashMap<>();
			map.put("id", hyOrder.getId());
			map.put("orderNumber", hyOrder.getOrderNumber());
			map.put("createTime", hyOrder.getCreatetime());
			map.put("sceneName", hyTicketSubscribe.getSceneName());
			map.put("startDate", hyOrderItem.getStartDate());
			map.put("star", hyTicketSubscribe.getStar());
			map.put("payStatus", hyOrder.getPaystatus());
			map.put("refundStatus", hyOrder.getRefundstatus());
			map.put("productStatus", (new Date()).compareTo(hyOrderItem.getStartDate())<0?false:true);
			map.put("status", hyOrder.getStatus());
			map.put("adjustMoney", hyOrder.getAdjustMoney());
			map.put("source", hyOrder.getSource());
			Long storeId = hyOrder.getStoreId();
			if(storeId!=null){
				Store store = storeService.find(storeId);
				map.put("storeName", store==null?"":store.getStoreName());
			}else{
				map.put("storeName", "");
			}
			map.put("jiesuanMoney1", hyOrder.getJiesuanMoney1());
			map.put("waimaiMoney", hyOrder.getWaimaiMoney());
			map.put("people", hyOrder.getPeople());
			map.put("koudianMethod", hyOrder.getKoudianMethod());
			map.put("proportion", hyOrder.getProportion());
			map.put("headProportion", hyOrder.getHeadProportion());
			map.put("koudianMoney", hyOrder.getKoudianMoney());
			map.put("jiesuanTuikuan", hyOrder.getJiesuanTuikuan());
			map.put("discountId", hyOrder.getDiscountedId());
			map.put("discountType", hyOrder.getDiscountedType());
			map.put("discountPrice", hyOrder.getDiscountedPrice());
			map.put("contact", hyOrder.getContact());
			map.put("phone", hyOrder.getPhone());
			map.put("remark", hyOrder.getRemark());
			map.put("ifJiesuan", hyOrder.getIfjiesuan());
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		}catch (Exception e) {
			// TODO: handle exceptio
			json.setSuccess(true);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	//酒店详情页
	public Json hoteldetailView(Long id) {
		Json json = new Json();
		try {
			HyOrder hyOrder = hyOrderService.find(id);
			if(hyOrder==null) {
				throw new Exception("订单不存在");
			}
			List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
			if(hyOrderItems==null || hyOrderItems.isEmpty()) {

				throw new Exception("没有订单条目数据");
			}
			HyOrderItem hyOrderItem = hyOrderItems.get(0);
			HyTicketHotel hyTicketHotel = hyTicketHotelService.find(hyOrderItem.getProductId());
			Map<String, Object> map = new HashMap<>();
			map.put("id", hyOrder.getId());
			map.put("orderNumber", hyOrder.getOrderNumber());
			map.put("createTime", hyOrder.getCreatetime());
			map.put("productName", hyTicketHotel.getHotelName());
			map.put("startDate", hyOrderItem.getStartDate());
			map.put("star", hyTicketHotel.getStar());
			map.put("payStatus", hyOrder.getPaystatus());
			map.put("refundStatus", hyOrder.getRefundstatus());
			map.put("productStatus", (new Date()).compareTo(hyOrderItem.getStartDate())<0?false:true);
			map.put("status", hyOrder.getStatus());
			map.put("source", hyOrder.getSource());
			Long storeId = hyOrder.getStoreId();
			if(storeId!=null){
				Store store = storeService.find(storeId);
				map.put("storeName", store==null?"":store.getStoreName());
			}else{
				map.put("storeName", "");
			}
			map.put("jiesuanMoney1", hyOrder.getJiesuanMoney1());
			map.put("waimaiMoney", hyOrder.getWaimaiMoney());
			map.put("people", hyOrder.getPeople());
			map.put("koudianMethod", hyOrder.getKoudianMethod());
			map.put("proportion", hyOrder.getProportion());
			map.put("headProportion", hyOrder.getHeadProportion());
			map.put("koudianMoney", hyOrder.getKoudianMoney());
			map.put("contact", hyOrder.getContact());
			map.put("phone", hyOrder.getPhone());
			map.put("remark", hyOrder.getRemark());
			//调整金额
			map.put("adjustMoney",hyOrder.getAdjustMoney());
			//是否结算
			map.put("ifjiesuan", hyOrder.getIfjiesuan());
			//优惠金额
			map.put("discountedPrice", hyOrder.getDiscountedPrice());
			//离开日期
			map.put("endDate", hyOrderItem.getEndDate());
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		}catch (Exception e) {
			// TODO: handle exceptio
			json.setSuccess(true);
			json.setMsg("查询失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	//门票详情页
	public Json sixthPageOrder(Long id) {
		Json j = new Json();
		List<HashMap<String, Object>> orderAndSceneTable = new ArrayList<>();//存储MAP用
		try{
			//很具传来的orderID来找到订单
			HyOrder myHyOrder = hyOrderService.find(id);
			if(myHyOrder != null){
				Map<String, Object> touristsAttractionInfo = new HashMap<String, Object>();//加入信息
				//订单号
				touristsAttractionInfo.put("orderNumber", myHyOrder.getOrderNumber());
				//产品名称
				touristsAttractionInfo.put("name", myHyOrder.getName());
				//支付状态
				touristsAttractionInfo.put("paystatus", myHyOrder.getPaystatus());
				//退款状态
				touristsAttractionInfo.put("refundstatus", myHyOrder.getRefundstatus());
				//订单状态
				touristsAttractionInfo.put("status", myHyOrder.getStatus());
				//订单金额
				touristsAttractionInfo.put("jiusuanMoney", myHyOrder.getJiusuanMoney());
				//扣点方式
				touristsAttractionInfo.put("koudianMethod", myHyOrder.getKoudianMethod());
				//扣点金额
				touristsAttractionInfo.put("koudianMoney", myHyOrder.getKoudianMoney());
				//联系电话
				touristsAttractionInfo.put("phone", myHyOrder.getPhone());
				//下单时间
				touristsAttractionInfo.put("createtime", myHyOrder.getCreatetime());
				//合同号
				touristsAttractionInfo.put("contractNumber", myHyOrder.getContractNumber());
				//确认状态
				touristsAttractionInfo.put("checkstatus", myHyOrder.getCheckstatus());
//				//产品状态 这个不用管
//				touristsAttractionInfo.put("checkstatus", myHyOrder.getCheckstatus());
				//订单来源
				touristsAttractionInfo.put("source", myHyOrder.getSource());
				//成交数量 
				List<HyOrderItem> HyOrderItems = myHyOrder.getOrderItems();
				HyOrderItem myHyOrderItem = null;
				if(!HyOrderItems.isEmpty())
					myHyOrderItem = HyOrderItems.get(0);
				if(myHyOrderItem != null)
					touristsAttractionInfo.put("numberFake", myHyOrderItem.getNumber());
				touristsAttractionInfo.put("number", myHyOrder.getPeople());
				//扣点比例
				touristsAttractionInfo.put("proportion", myHyOrder.getProportion());
				//联系人
				touristsAttractionInfo.put("contact", myHyOrder.getContact());
				//备注
				touristsAttractionInfo.put("remark", myHyOrder.getRemark());
				//是否结算
				touristsAttractionInfo.put("ifJiesuan", myHyOrder.getIfjiesuan());
				//还需要返回 优惠价格 原价（外卖价格感觉是） 调整金额
				touristsAttractionInfo.put("discountedPrice", myHyOrder.getDiscountedPrice());
				touristsAttractionInfo.put("waimaiMoney", myHyOrder.getWaimaiMoney());
				touristsAttractionInfo.put("adjustMoney", myHyOrder.getAdjustMoney());
				
				 //2018-11-15 bug之后添加
				int source1 = myHyOrder.getSource();
					if(source1 != Constants.mendian){
						touristsAttractionInfo.put("storeName", "");
					}else{
						Long storeId = myHyOrder.getStoreId();
						Store store = storeService.find(storeId);
						touristsAttractionInfo.put("storeName", store==null?"":store.getStoreName());
					}
				touristsAttractionInfo.put("discountedPrice", myHyOrder.getDiscountedPrice());
				touristsAttractionInfo.put("headProportion", myHyOrder.getHeadProportion());
				touristsAttractionInfo.put("jiesuanTuikuan", myHyOrder.getJiesuanTuikuan());
				//门票是否已经使用 需要自己用日期判断
				Date myCreateTime = myHyOrder.getCreatetime();
				int flag = myCreateTime.compareTo(new Date());
				//门票没有到期 -- 0  门票到期了 -- 1 在那一天是没到期
				if(flag > 0){
					touristsAttractionInfo.put("state", 1);
				} else {
					touristsAttractionInfo.put("state", 0);
				}
				
//				orderAndSceneTable.add((HashMap<String, Object>) touristsAttractionInfo);
				j.setObj(touristsAttractionInfo);
			}
			j.setSuccess(true);
			j.setMsg("获取成功");
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("获取失败：" + e.getMessage());
			j.setMsg(e.getMessage());	
		}
//		//最后返回空的json
//		j.setSuccess(true);
//		
//		j.setMsg("更新成功");
		return j;
		
	}
	
	//酒加景详情页
	public Json hotelandscenedetail(Long id)
	{
		Json json=new Json();
		try {
			HyOrder hyOrder=hyOrderService.find(id);
			Map<String, Object> map = new HashMap<>();	
			map.put("orderNumber", hyOrder.getOrderNumber()); //订单号
			map.put("createTime", hyOrder.getCreatetime()); //下单时间
			map.put("name", hyOrder.getName());
			List<HyOrderItem> orderItems=hyOrder.getOrderItems();
			HyOrderItem hyOrderItem=orderItems.get(0);
			HyTicketHotelandscene hyTicketHotelandscene=hyTicketHotelandsceneService.find(hyOrderItem.getProductId());		
			map.put("productId", hyTicketHotelandscene.getProductId()); //产品编号
			map.put("paystatus", hyOrder.getPaystatus());
			map.put("checkstatus", hyOrder.getCheckstatus()); //确认状态
			map.put("refundstatus", hyOrder.getRefundstatus()); //退款状态
			map.put("status", hyOrder.getStatus());
			map.put("source", hyOrder.getSource()); //订单来源
			map.put("ifjiesuan", hyOrder.getIfjiesuan()); //是否结算
			map.put("startDate", hyOrderItem.getStartDate()); //服务开始日期
			map.put("discountedPrice", hyOrder.getDiscountedPrice()); //优惠金额
			map.put("adjustMoney", hyOrder.getAdjustMoney()); //调整金额
			Long storeId = hyOrder.getStoreId();
			if(storeId!=null){
				Store store = storeService.find(storeId);
				map.put("storeName", store==null?"":store.getStoreName());
			}else{
				map.put("storeName", "");
			}
			Date date=new Date();
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(date);
			cal1.set(Calendar.HOUR_OF_DAY, 0);
			cal1.set(Calendar.MINUTE, 0);
			cal1.set(Calendar.SECOND, 0);
			cal1.set(Calendar.MILLISECOND, 0);
			Date startDate=hyOrderItem.getEndDate(); //服务结束时间
			if(date.compareTo(startDate)>0) {
				map.put("productStatus", 1); //已使用
			}
			else {
				map.put("productStatus", 0); //未使用
			}
			map.put("orderMoney", hyOrder.getJiusuanMoney());
			map.put("quantity", hyOrderItem.getNumber()); //商品数量
			map.put("koudianMethod", hyOrder.getKoudianMethod());
			map.put("proportion", hyOrder.getProportion());
			map.put("koudianMoney", hyOrder.getKoudianMoney());
			map.put("contact", hyOrder.getContact()); //联系人
			map.put("telephone", hyOrder.getPhone());
			map.put("remark", hyOrder.getRemark());
			json.setSuccess(true);
			json.setObj(map);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	//保险详情页
	public Json insurancdetail(Long id) {
		Json json = new Json();
		try {
			
			HyOrder tmp = hyOrderService.find(id);
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderId", tmp.getId()));
			//退款状态是部分退款
			if(tmp.getRefundstatus() == 3) {
				//过滤掉已撤保的
				filters.add(Filter.ne("status", 4));
			}
			
			InsuranceOrder insuranceOrder = insuranceOrderService.findList(null, filters, null).get(0);
			Map<String, Object> map = new HashMap<>();	
			map.put("id", tmp.getId());
			map.put("orderNumber", tmp.getOrderNumber());
			map.put("name", tmp.getName());
			map.put("status", tmp.getStatus());
			map.put("paystatus", tmp.getPaystatus());
			map.put("refundstatus", tmp.getRefundstatus());
//			map.put("type", tmp.getType());
			map.put("people", tmp.getPeople());
			map.put("tianshu", tmp.getTianshu());
			map.put("contact", tmp.getContact());
//			Long storeId = tmp.getStoreId();
//			if(storeId!=null){
//				Store store = storeService.find(storeId);
//				map.put("storeName", store==null?"":store.getStoreName());
//			}else{
//				map.put("storeName", "");
//			}
			map.put("storeType", tmp.getStoreType());
			map.put("operator", tmp.getOperator());
			map.put("creatorId", tmp.getCreatorId());
			map.put("waimaiMoney", tmp.getWaimaiMoney());
			map.put("baoxianJiesuanTuikuan", tmp.getBaoxianJiesuanTuikuan());
			map.put("baoxianWaimaiTuikuan", tmp.getBaoxianWaimaiTuikuan());
			map.put("ifjiesuan", tmp.getIfjiesuan());
			map.put("insuranceOrderDownloadUrl", tmp.getInsuranceOrderDownloadUrl());
			map.put("departure", tmp.getDeparture());
			map.put("fatuandate", tmp.getFatuandate());
			map.put("tianshu", tmp.getTianshu());
			map.put("xianlumingcheng", tmp.getXianlumingcheng());
			//国内 境外
			map.put("type", tmp.getXianlutype());
			map.put("contact", tmp.getContact());
			map.put("phone", tmp.getPhone());
			map.put("remark", tmp.getRemark());
			
			map.put("createtime", tmp.getCreatetime());
			map.put("modifytime", tmp.getModifytime());
			
			map.put("orderItems", tmp.getOrderItems());
			map.put("insurancestatus", insuranceOrder.getStatus());

//			//查找额外保险信息
//			Insurance insurance = insuranceService.getExtraInsuranceOfOrder(tmp);
//			if(insurance!=null){
//				map.put("insurance",insurance.getInsuranceCode());
//				map.put("insuranceMoney", insurance.getInsurancePrices());
//			}
//			Long groupId = tmp.getGroupId();
//			if(groupId!=null){
//				HyGroup hyGroup = hyGroupService.find(groupId);
//				map.put("linePn", hyGroup.getGroupLinePn());
//			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	//签证详情页
	public Json visadetail(Long id){
		Json json = new Json();
		try {
			HyOrder tmp = hyOrderService.find(id);
			if(tmp==null)
				throw new NullPointerException("找不到该订单");
			Map<String, Object> map = new HashMap<>();	
			map.put("id", tmp.getId());
			map.put("orderNumber", tmp.getOrderNumber());
			map.put("name", tmp.getName());
			map.put("status", tmp.getStatus());
			map.put("paystatus", tmp.getPaystatus());
			map.put("checkstatus", tmp.getCheckstatus());
			map.put("refundstatus", tmp.getRefundstatus());
			map.put("type", tmp.getType());
			map.put("source", tmp.getSource());
			map.put("people", tmp.getPeople());
			map.put("storeType", tmp.getStoreType());
			map.put("storeId", tmp.getStoreId());
			Long storeId = tmp.getStoreId();
			if(storeId!=null){
				Store store = storeService.find(storeId);
				map.put("storeName", store==null?"":store.getStoreName());
			}else{
				map.put("storeName", "");
			}
			map.put("operator", tmp.getOperator());
			map.put("creatorId", tmp.getCreatorId());
			map.put("adjustMoney", tmp.getAdjustMoney());
			map.put("discountedType", tmp.getDiscountedType());
			map.put("discountedId", tmp.getDiscountedId());
			map.put("discountedPrice", tmp.getDiscountedPrice());
			map.put("jiesuanMoney1", tmp.getJiesuanMoney1());
			map.put("jiusuanMoney", tmp.getJiusuanMoney());
			map.put("waimaiMoney", tmp.getWaimaiMoney());
			map.put("jiesuanTuikuan", tmp.getJiesuanTuikuan());
			map.put("waimaiTuikuan", tmp.getWaimaiTuikuan());
			map.put("baoxianJiesuanTuikuan", tmp.getBaoxianJiesuanTuikuan());
			map.put("baoxianWaimaiTuikuan", tmp.getBaoxianWaimaiTuikuan());
			map.put("ifjiesuan", tmp.getIfjiesuan());
			map.put("koudianMethod", tmp.getKoudianMethod());
			map.put("proportion", tmp.getProportion());
			map.put("headProportion", tmp.getHeadProportion());
			map.put("koudianMoney", tmp.getKoudianMoney());
			map.put("fatuandate", tmp.getFatuandate());
			map.put("continent", tmp.getXianlumingcheng());
			map.put("country", tmp.getXingchenggaiyao());
			map.put("contact", tmp.getContact());
			map.put("contactIdNumber", tmp.getContactIdNumber());
			map.put("phone", tmp.getPhone());
			map.put("remark", tmp.getRemark());;
			map.put("createtime", tmp.getCreatetime());
			map.put("modifytime", tmp.getModifytime());
			
			map.put("orderItems", tmp.getOrderItems());
			map.put("supplier", tmp.getSupplier());	//计调，供应商对接人信息
			map.put("isDivideStatistic", tmp.getIsDivideStatistic());
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(map);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	
	public class PreSave{
		private String storeName;
		private BigDecimal income;
		private BigDecimal outcome;
		private BigDecimal balance;
		private Long storeId;
		private String areaName;
		private String storeType;
		public String getStoreName() {
			return storeName;
		}
		public void setStoreName(String storeName) {
			this.storeName = storeName;
		}
		public BigDecimal getIncome() {
			return income;
		}
		public void setIncome(BigDecimal income) {
			this.income = income;
		}
		public BigDecimal getOutcome() {
			return outcome;
		}
		public void setOutcome(BigDecimal outcome) {
			this.outcome = outcome;
		}
		public BigDecimal getBalance() {
			return balance;
		}
		public void setBalance(BigDecimal balance) {
			this.balance = balance;
		}
		public Long getStoreId() {
			return storeId;
		}
		public void setStoreId(Long storeId) {
			this.storeId = storeId;
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
	

}
