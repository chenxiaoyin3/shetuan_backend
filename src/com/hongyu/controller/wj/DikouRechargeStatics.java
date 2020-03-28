package com.hongyu.controller.wj;

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

import org.hibernate.dialect.function.AnsiTrimEmulationFunction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.HyArea;
import com.hongyu.entity.Store;
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
import com.sun.org.apache.xpath.internal.operations.And;

/**
 * 财务可看
 * 预存款支出报表
 * 
 * @author wj
 *
 */

@Controller
@RequestMapping("/admin/dikouRechargeStatics")
public class DikouRechargeStatics {

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
	
	@Resource(name = "hyAreaServiceImpl")
	HyAreaService hyAreaService;
	
	
	/**
	 * 抵扣报表
	 * @param startDate
	 * @param endDate
	 * @param storeId
	 * @param pageable
	 * @param session
	 * @return
	 */
	@RequestMapping("/dikou/list") 
	@ResponseBody
	public Json dikou(@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,Long storeId,Long areaId,Integer storeType,Pageable pageable,HttpSession session){
		Json json=new Json();
		try {
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal zero = BigDecimal.ZERO;
			//门店充值订单抵扣
			StringBuffer sql1 = new StringBuffer();
			sql1.append(" select d.full_name , CAST(l.money AS DECIMAL(21, 3)), CAST(o.jiesuan_tuikuan AS DECIMAL(21, 3)),CAST(o.baoxian_waimai_tuikuan AS DECIMAL(21, 3)), l.order_sn ,l.create_date ,o.type "
					//新增门店所属地区和门店类型
					+ " ,area.full_name as areaname,(case s.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype " 
					//保险
					+ " ,(case o.type when 1 then o.jiusuan_money-o.jiesuan_money1 else 0.00 end)  baoxianmoney ,o.jiusuan_money"
					+ " from hy_store_account_log l,hy_department d,hy_store s ,hy_order o ,hy_area area"
					+ " where s.id = l.store_id and s.department_id = d.id and l.order_sn = o.order_number "
					// 新增
					+ " and s.area_id = area.id"
					+ " and l.type = 1 and o.status = 3 ");
			
			//分公司充值订单抵扣
			StringBuffer sql2 = new StringBuffer();
			sql2.append("select d.full_name,CAST(save.amount AS DECIMAL(21, 3)),CAST(o.jiesuan_tuikuan AS DECIMAL(21, 3)),CAST(o.baoxian_waimai_tuikuan AS DECIMAL(21, 3)), o.order_number ,save.date ,o.type "
					//新增门店所属地区和门店类型
					+ " ,area.full_name as areaname,(case s.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype "
					+ " ,(case o.type when 1 then o.jiusuan_money-o.jiesuan_money1 else 0.00 end)  baoxianmoney ,o.jiusuan_money"
					+ " from hy_branch_pre_save save ,hy_department d,hy_store s ,hy_order o,hy_area area "
					+ " where o.id = save.order_id  and save.branch_id = d.id and o.store_id = s.id  "
					// 新增
					+ " and s.area_id = area.id"
					+ " and save.type = 6 ");
			
			if( areaId != null){
				sql1.append(" and s.area_id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
				sql2.append(" and s.area_id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
			}
			if( storeType != null){
				sql1.append(" and s.type = "+storeType);
				sql2.append(" and s.type = "+storeType);
			}
			
			if(startDate!=null && endDate!=null){
				if(startDate.compareTo(endDate)>0){
					throw new Exception("查询开始时间不能大于结束时间");
				}
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(endDate);
				calendar.add(Calendar.DATE, 1);
				Date nextEndDate = calendar.getTime();
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
				String today = formatter.format(startDate);  
				String tomorrow = formatter.format(nextEndDate);
				
				sql1.append(" and l.create_date>='");
				sql1.append(today+"' ");
				sql1.append(" and l.create_date<'");
				sql1.append(tomorrow +"' ");
				sql2.append(" and save.date>='");
				sql2.append(today+"' ");
				sql2.append(" and save.date<'");
				sql2.append(tomorrow +"' ");
			}
			if(storeId != null){
				sql1.append(" and l.store_id = ");
				sql1.append(storeId);
				
				sql2.append(" and s.id = ");
				sql2.append(storeId);
			}
			List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
			List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());
			List<HashMap<String, Object>> list = new ArrayList<>();
			
			BigDecimal jiesuan = new BigDecimal("0.00");
			BigDecimal jiesuanTuikuan = new BigDecimal("0.00");
			BigDecimal baoxianjiesuanTuikuan = new BigDecimal("0.00");
			BigDecimal zuizhong = new BigDecimal("0.00");
			BigDecimal baoxianzongji = BigDecimal.ZERO;
			BigDecimal exceptbaoxianzongji = BigDecimal.ZERO;
			
			for (Object[] objects : list1) {
				HashMap<String, Object> map = new HashMap<>();
				jiesuan = new BigDecimal("0.00");
				jiesuanTuikuan = new BigDecimal("0.00");
				baoxianjiesuanTuikuan = new BigDecimal("0.00");
				zuizhong = new BigDecimal("0.00");
				
//				map.put("departmentName",objects[0] == null ?"null":objects[0].toString());
//				map.put("amount", objects[1] == null ?"null":objects[1].toString());
//				map.put("orderSn", objects[2] == null ?"null":objects[2].toString());
//				map.put("date", objects[3] == null ?"null":objects[3].toString());
				map.put("departmentName",objects[0]);
				
				if(objects[1] != null){
					jiesuan = (BigDecimal)objects[1];
				}
				if(objects[2] != null){
					jiesuanTuikuan = (BigDecimal)objects[2];
				}
				if(objects[3] != null){
					baoxianjiesuanTuikuan = (BigDecimal)objects[3];
				}
				zuizhong = jiesuan.subtract(jiesuanTuikuan).subtract(baoxianjiesuanTuikuan);
				if(zuizhong.compareTo(zero) == 0) continue;
				sum = sum.add(zuizhong);
				map.put("amount", zuizhong);
				map.put("orderSn", objects[4] );
				map.put("date", objects[5]==null ? null : objects[5].toString().substring(0, 19));
				map.put("type",objects[6]);
				map.put("areaName",objects[7]==null ? null : objects[7].toString());
				map.put("storeType",objects[8]==null ? null : objects[8].toString());
				map.put("insuranceAmount",objects[9]==null ? null : objects[9].toString());
				map.put("qitaAmount",objects[9]==null?zuizhong:zuizhong.subtract((BigDecimal)objects[9]));
				
				if(objects[9]!=null){
					baoxianzongji = baoxianzongji.add((BigDecimal)objects[9]);
					exceptbaoxianzongji = exceptbaoxianzongji.add(zuizhong).subtract((BigDecimal)objects[9]);
				}
				
				list.add(map);
			}
			for (Object[] objects : list2) {
				HashMap<String, Object> map = new HashMap<>();
				jiesuan = new BigDecimal("0.00");
				jiesuanTuikuan = new BigDecimal("0.00");
				baoxianjiesuanTuikuan = new BigDecimal("0.00");
				zuizhong = new BigDecimal("0.00");
				
//				map.put("departmentName",objects[0] == null ?"null":objects[0].toString());
//				map.put("amount", objects[1] == null ?"null":objects[1].toString());
//				map.put("orderSn", objects[2] == null ?"null":objects[2].toString());
//				map.put("date", objects[3] == null ?"null":objects[3].toString());
				map.put("departmentName",objects[0]);
				
				if(objects[1] != null){
					jiesuan = (BigDecimal)objects[1];
				}
				if(objects[2] != null){
					jiesuanTuikuan = (BigDecimal)objects[2];
				}
				if(objects[3] != null){
					baoxianjiesuanTuikuan = (BigDecimal)objects[3];
				}
				zuizhong = jiesuan.subtract(jiesuanTuikuan).subtract(baoxianjiesuanTuikuan);
				if(zuizhong.compareTo(zero) == 0) continue;
				sum = sum.add(zuizhong);
				map.put("amount", zuizhong);
				map.put("orderSn", objects[4] );
				map.put("date", objects[5]==null ? null : objects[5].toString().substring(0, 19));
				map.put("type",objects[6]);
				map.put("areaName",objects[7]==null ? null : objects[7].toString());
				map.put("storeType",objects[8]==null ? null : objects[8].toString());
				map.put("insuranceAmount",objects[9]==null ? null : objects[9].toString());
				map.put("qitaAmount",objects[9]==null?zuizhong:zuizhong.subtract((BigDecimal)objects[9]));
				list.add(map);
				
				
				if(objects[9]!=null){
					baoxianzongji = baoxianzongji.add((BigDecimal)objects[9]);
					exceptbaoxianzongji = exceptbaoxianzongji.add(zuizhong).subtract((BigDecimal)objects[9]);
				}
			}
			
			HashMap<String, Object> ans = new HashMap<>();
			ans.put("sum", sum);
			ans.put("baoxianSum", baoxianzongji);
			ans.put("qitaSum", exceptbaoxianzongji);
			ans.put("list", list);
			json.setObj(ans);
	
//			json.setObj(list);
			json.setMsg("查询成功");
			json.setSuccess(true);
		
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("查询失败:"+e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
	}


	@RequestMapping("/download/dikou/list") 
	public void downLoaddikou(@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate,Long storeId,Long areaId,Integer storeType,HttpSession session,HttpServletRequest request,HttpServletResponse response){
		try {
			BigDecimal sum = BigDecimal.ZERO;
			BigDecimal zero = BigDecimal.ZERO;
			BigDecimal insuranceSum = BigDecimal.ZERO;
			BigDecimal dikouMoneyExceptInsurance = zero;
			BigDecimal baoxianzongji = BigDecimal.ZERO;
			BigDecimal exceptbaoxianzongji = BigDecimal.ZERO;
			//门店充值订单抵扣
			StringBuffer sql1 = new StringBuffer();
			sql1.append(" select d.full_name , CAST(l.money AS DECIMAL(21, 3)), CAST(o.jiesuan_tuikuan AS DECIMAL(21, 3)),CAST(o.baoxian_waimai_tuikuan AS DECIMAL(21, 3)), l.order_sn ,l.create_date ,o.type "
					//新增门店所属地区和门店类型
					+ " ,area.full_name as areaname,(case s.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype " 
					//保险
					+ " ,(case o.type when 1 then o.jiusuan_money-o.jiesuan_money1 else 0.00 end)  baoxianmoney ,o.jiusuan_money"
					+ " from hy_store_account_log l,hy_department d,hy_store s ,hy_order o ,hy_area area"
					+ " where s.id = l.store_id and s.department_id = d.id and l.order_sn = o.order_number "
					// 新增
					+ " and s.area_id = area.id"
					+ " and l.type = 1 and o.status = 3 ");
			
			//分公司充值订单抵扣
			StringBuffer sql2 = new StringBuffer();
			sql2.append("select d.full_name,CAST(save.amount AS DECIMAL(21, 3)),CAST(o.jiesuan_tuikuan AS DECIMAL(21, 3)),CAST(o.baoxian_waimai_tuikuan AS DECIMAL(21, 3)), o.order_number ,save.date ,o.type "
					//新增门店所属地区和门店类型
					+ " ,area.full_name as areaname,(case s.type when 0 then '虹宇门店' when 2 then '直营门店' when 3 then '非虹宇门店' end ) storetype "
					+ " ,(case o.type when 1 then o.jiusuan_money-o.jiesuan_money1 else 0.00 end)  baoxianmoney ,o.jiusuan_money"
					+ " from hy_branch_pre_save save ,hy_department d,hy_store s ,hy_order o,hy_area area "
					+ " where o.id = save.order_id  and save.branch_id = d.id and o.store_id = s.id  "
					// 新增
					+ " and s.area_id = area.id"
					+ " and save.type = 6 ");
			
			if( areaId != null){
				sql1.append(" and s.area_id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
				sql2.append(" and s.area_id in (select id from hy_area where pId = " + areaId +" or id = " + areaId +")");
			}
			if( storeType != null){
				sql1.append(" and s.type = "+storeType);
				sql2.append(" and s.type = "+storeType);
			}
			
			if(startDate!=null && endDate!=null){
				if(startDate.compareTo(endDate)>0){
					throw new Exception("查询开始时间不能大于结束时间");
				}
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(endDate);
				calendar.add(Calendar.DATE, 1);
				Date nextEndDate = calendar.getTime();
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
				String today = formatter.format(startDate);  
				String tomorrow = formatter.format(nextEndDate);
				
				sql1.append(" and l.create_date>='");
				sql1.append(today+"' ");
				sql1.append(" and l.create_date<'");
				sql1.append(tomorrow +"' ");
				sql2.append(" and save.date>='");
				sql2.append(today+"' ");
				sql2.append(" and save.date<'");
				sql2.append(tomorrow +"' ");
			}
			if(storeId != null){
				sql1.append(" and l.store_id = ");
				sql1.append(storeId);
				
				sql2.append(" and s.id = ");
				sql2.append(storeId);
			}
			List<Object[]> list1 = storeAccountLogService.statis(sql1.toString());
			List<Object[]> list2 = storeAccountLogService.statis(sql2.toString());
			BigDecimal jiesuan = new BigDecimal("0.00");
			BigDecimal jiesuanTuikuan = new BigDecimal("0.00");
			BigDecimal baoxianjiesuanTuikuan = new BigDecimal("0.00");
			BigDecimal zuizhong = new BigDecimal("0.00");

//			List<HashMap<String, Object>> list = new ArrayList<>();
			List<DikouDate> dikouDates = new ArrayList<>();
			
			
			for (Object[] objects : list1) {
				jiesuan = new BigDecimal("0.00");
				jiesuanTuikuan = new BigDecimal("0.00");
				baoxianjiesuanTuikuan = new BigDecimal("0.00");
				zuizhong = new BigDecimal("0.00");
				
				if(objects[1] != null){
					jiesuan = (BigDecimal)objects[1];
				}
				if(objects[2] != null){
					jiesuanTuikuan = (BigDecimal)objects[2];
				}
				if(objects[3] != null){
					baoxianjiesuanTuikuan = (BigDecimal)objects[3];
				}
				zuizhong = jiesuan.subtract(jiesuanTuikuan).subtract(baoxianjiesuanTuikuan);
				if(zuizhong.compareTo(zero) == 0) continue;
				sum = sum.add(zuizhong);
				
				
				DikouDate dikouDate = new DikouDate();
				
				dikouDate.setDepartmentName(objects[0]==null?null:objects[0].toString());
				dikouDate.setAmount(zuizhong);
				dikouDate.setOrderSn(objects[4]==null?null:objects[4].toString());
				dikouDate.setDate(objects[5]==null?null:objects[5].toString().substring(0, 19));
				String type = "";
				//订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
				if(objects[6]!=null){
					switch(objects[6].toString()){
					case "0": type = "导游租赁订单";break;
					case "1": type = "线路订单";break;
					case "2": type = "订购门票订单";break;
					case "3": type = "酒店订单";break;
					case "4": type = "门票订单";break;
					case "5": type = "酒+景订单";break;
					case "6": type = "保险订单";break;
					case "7": type = "签证订单";break;
					}
				}
				
				dikouDate.setType(type);
				dikouDate.setAreaName(objects[7]==null?null:objects[7].toString());
				dikouDate.setStoreType(objects[8]==null?null:objects[8].toString());
				dikouDate.setInsuranceAmount(objects[9]==null ? zero : (BigDecimal)objects[9]);
				dikouDate.setQitaAmount(dikouDate.getAmount().subtract(dikouDate.getInsuranceAmount()));
				
				insuranceSum = insuranceSum.add(dikouDate.getInsuranceAmount());
				dikouMoneyExceptInsurance = dikouMoneyExceptInsurance.add(dikouDate.getQitaAmount());
				
				
//				if(objects[3]!=null){
//					String string = objects[3].toString();
//			        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			        dikouDate.setDate(sdf.parse(string));
//				}
				dikouDates.add(dikouDate);
				
//				map.put("departmentName",objects[0]);
//				map.put("amount", objects[1] );
//				map.put("orderSn", objects[2] );
//				map.put("date", objects[3]);
//				list.add(map);
			}
			for (Object[] objects : list2) {
				HashMap<String, Object> map = new HashMap<>();
				jiesuan = new BigDecimal("0.00");
				jiesuanTuikuan = new BigDecimal("0.00");
				baoxianjiesuanTuikuan = new BigDecimal("0.00");
				zuizhong = new BigDecimal("0.00");
				
				if(objects[1] != null){
					jiesuan = (BigDecimal)objects[1];
				}
				if(objects[2] != null){
					jiesuanTuikuan = (BigDecimal)objects[2];
				}
				if(objects[3] != null){
					baoxianjiesuanTuikuan = (BigDecimal)objects[3];
				}
				zuizhong = jiesuan.subtract(jiesuanTuikuan).subtract(baoxianjiesuanTuikuan);
				if(zuizhong.compareTo(zero) == 0) continue;
				sum = sum.add(zuizhong);

				
				DikouDate dikouDate = new DikouDate();
				
				dikouDate.setDepartmentName(objects[0]==null?null:objects[0].toString());
				dikouDate.setAmount(zuizhong);
				dikouDate.setOrderSn(objects[4]==null?null:objects[4].toString());
				dikouDate.setDate(objects[5]==null?null:objects[5].toString().substring(0, 19));
				String type = "";
				//订单类型0导游租赁，1线路，2订购门票，3酒店，4门票，5酒+景，6保险，7签证
				if(objects[6]!=null){
					switch(objects[6].toString()){
					case "0": type = "导游租赁订单";break;
					case "1": type = "线路订单";break;
					case "2": type = "订购门票订单";break;
					case "3": type = "酒店订单";break;
					case "4": type = "门票订单";break;
					case "5": type = "酒+景订单";break;
					case "6": type = "保险订单";break;
					case "7": type = "签证订单";break;
					}
				}
				
				dikouDate.setType(type);
				dikouDate.setAreaName(objects[7]==null?null:objects[7].toString());
				dikouDate.setStoreType(objects[8]==null?null:objects[8].toString());
				dikouDate.setInsuranceAmount(objects[9]==null ? zero : (BigDecimal)objects[9]);
				dikouDate.setQitaAmount(dikouDate.getAmount().subtract(dikouDate.getInsuranceAmount()));
				dikouDates.add(dikouDate);
				
				insuranceSum = insuranceSum.add(dikouDate.getInsuranceAmount());
				dikouMoneyExceptInsurance = dikouMoneyExceptInsurance.add(dikouDate.getQitaAmount());
				
			}
			
			DikouDate dikouDate = new DikouDate();
			dikouDate.setDepartmentName("合计");
			dikouDate.setAmount(sum);
			dikouDate.setInsuranceAmount(insuranceSum);
			dikouDate.setQitaAmount(dikouMoneyExceptInsurance);
			dikouDates.add(dikouDate);
			
	
			StringBuffer sb2 = new StringBuffer();
			sb2.append("预存款支出报表");
			String fileName = "预存款支出报表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "presaveOutStatics.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, dikouDates, fileName, tableTitle, configFile);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public class DikouDate{
		private String departmentName;
		private BigDecimal amount;
		private String orderSn;
		private String date;
		private String type;
		private String areaName;
		private String storeType;
		private BigDecimal insuranceAmount;
		private BigDecimal qitaAmount;
		
		
		
		public BigDecimal getInsuranceAmount() {
			return insuranceAmount;
		}
		public void setInsuranceAmount(BigDecimal insuranceAmount) {
			this.insuranceAmount = insuranceAmount;
		}
		public BigDecimal getQitaAmount() {
			return qitaAmount;
		}
		public void setQitaAmount(BigDecimal qitaAmount) {
			this.qitaAmount = qitaAmount;
		}
		public String getDepartmentName() {
			return departmentName;
		}
		public void setDepartmentName(String departmentName) {
			this.departmentName = departmentName;
		}
		public BigDecimal getAmount() {
			return amount;
		}
		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}
		public String getOrderSn() {
			return orderSn;
		}
		public void setOrderSn(String orderSn) {
			this.orderSn = orderSn;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
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
	
}
