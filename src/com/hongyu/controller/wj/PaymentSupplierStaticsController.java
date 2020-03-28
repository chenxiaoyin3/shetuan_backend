package com.hongyu.controller.wj;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.stat.TableStat.Name;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PascalCaseStrategy;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.controller.wj.StoreRechargeController.PreSave;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.service.PaymentSupplierService;

/**
 * 财务应付款对账单
 * 首先列表页 统计：应付，已付，待付
 * 按打款单生成时间和供应商名称查询
 * 
 * @author wj
 *
 */
@Controller
@RequestMapping("/admin/linePaymentStatic")
public class PaymentSupplierStaticsController {
	
	@Resource(name = "paymentSupplierServiceImpl")
	PaymentSupplierService paymentSupplierService;
	
	
	/**
	 * 输入供应商，打款单生成时间，查询历史应付，已付，欠付，当期应付 已付，欠付 和 总的应付 已付，欠付
	 * @param startDate
	 * @param endDate
	 * @param supplierName
	 * @param session
	 * @return
	 */
	@RequestMapping("/list/view")
	@ResponseBody
	public Json list(@DateTimeFormat(pattern="yyyy-MM-dd")Date startDate,@DateTimeFormat(pattern="yyyy-MM-dd")Date endDate,
			String supplierName,HttpSession session){
		Json json = new Json();
		try {
			List<PayStatics> answer = new ArrayList<>();
			if(startDate == null || endDate == null || startDate.compareTo(endDate) > 0){
				throw new Exception("请输入正确的起始时间和结束时间");
			}
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(endDate);
			calendar.add(Calendar.DATE, 1);
			Date nextEndDate = calendar.getTime();
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
			String start = formatter.format(startDate);  
			String end = formatter.format(nextEndDate);
			
			//查询所有
			if(supplierName == null){
				StringBuffer buffer = new StringBuffer();
				StringBuffer sql1 = new StringBuffer();
				sql1.append(" select ans1.name,ans1.yingfu,ans2.yifu ,ans1.yingfu-ans2.yifu,ans3.yingfu as yingfu1,ans4.yifu as yingfu2,ans3.yingfu-ans4.yifu,ans1.yingfu+ans3.yingfu,ans2.yifu+ans4.yifu, ans1.yingfu+ans3.yingfu-ans2.yifu-ans4.yifu ,ans1.id from ");
				sql1.append(" (select supplier.supplier_name name ,IFNULL(res1.money,0) yingfu ,supplier.id id from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time < '" + start+"' ");
				sql1.append(" and s.is_valid = 1 and s.status != 4 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id "
						+ " group by su.id) res1 on supplier.id = res1.id where supplier.is_inner = 0) as ans1, ");
	
				StringBuffer sql2 = new StringBuffer();
				sql2.append("(select supplier.supplier_name name ,IFNULL(res1.money,0) yifu ,supplier.id id from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time < '" + start+"' ");
				sql2.append(" and s.is_valid = 1 and s.status =3 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id"
						+ " group by su.id) res1 on supplier.id = res1.id where supplier.is_inner = 0) as ans2,");
				
				StringBuffer sql3 = new StringBuffer();
				sql3.append(" (select supplier.supplier_name name ,IFNULL(res1.money,0) yingfu ,supplier.id id from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time >= '"+start+"' " + "and s.create_time <'" + end +"'");
				sql3.append(" and s.is_valid = 1 and s.status != 4 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id"
						+ " group by su.id) res1 on supplier.id = res1.id where supplier.is_inner = 0) as ans3,");
	
				StringBuffer sql4 = new StringBuffer();
				sql4.append("(select supplier.supplier_name name ,IFNULL(res2.money,0) yifu,supplier.id id  from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time >= '"+start+"' " + "and s.create_time <'" + end +"'");
				sql4.append(" and s.is_valid = 1 and s.status = 3 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id"
						+ " group by su.id) res2 on supplier.id = res2.id where supplier.is_inner = 0) as ans4");
				
				buffer.append(sql1.toString());
				buffer.append(sql2.toString());
				buffer.append(sql3.toString());
				buffer.append(sql4.toString());
				buffer.append(" where ans1.id = ans2.id and ans2.id = ans3.id and ans3.id = ans4.id");
				System.out.println(buffer.toString());
				
				List<Object[]> list1 = new ArrayList<>();
				list1 = paymentSupplierService.statis(buffer.toString());
				for(Object[] objects : list1){
					PayStatics payStatics = new PayStatics();
					payStatics.setSupplierName(objects[0].toString());
					payStatics.setHistoryYingfu((BigDecimal)objects[1]);
					payStatics.setHistoryYifu((BigDecimal)objects[2]);
					payStatics.setHistoryDaifu((BigDecimal)objects[3]);
					payStatics.setThisYingfu((BigDecimal)objects[4]);
					payStatics.setThisYifu((BigDecimal)objects[5]);
					payStatics.setThisDaifu((BigDecimal)objects[6]);
					payStatics.setYingfu((BigDecimal)objects[7]);
					payStatics.setYifu((BigDecimal)objects[8]);
					payStatics.setDaifu((BigDecimal)objects[9]);
					payStatics.setSupplierId(Long.valueOf(objects[10].toString()));
					answer.add(payStatics);
				}
			}else{
				StringBuffer buffer = new StringBuffer();
				StringBuffer sql1 = new StringBuffer();
				sql1.append(" select ans1.name,ans1.yingfu,ans2.yifu ,ans1.yingfu-ans2.yifu,ans3.yingfu as yingfu1,ans4.yifu as yingfu2,ans3.yingfu-ans4.yifu,ans1.yingfu+ans3.yingfu,ans2.yifu+ans4.yifu, ans1.yingfu+ans3.yingfu-ans2.yifu-ans4.yifu ,ans1.id from ");
				sql1.append(" (select supplier.supplier_name name ,IFNULL(res1.money,0) yingfu ,supplier.id id from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time < '" + start+"' ");
				sql1.append(" and s.is_valid = 1 and s.status != 4 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id "
						+ " group by su.id) res1 on supplier.id = res1.id where supplier.supplier_name like '%"+supplierName+"%' "
						+ " ) as ans1, ");
	
				StringBuffer sql2 = new StringBuffer();
				sql2.append("(select supplier.supplier_name name ,IFNULL(res1.money,0) yifu ,supplier.id id from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time < '" + start+"' ");
				sql2.append(" and s.is_valid = 1 and s.status =3 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id"
						+ " group by su.id) res1 on supplier.id = res1.id where supplier.supplier_name like '%"+supplierName+"%' "
						+ " ) as ans2,");
				
				StringBuffer sql3 = new StringBuffer();
				sql3.append(" (select supplier.supplier_name name ,IFNULL(res1.money,0) yingfu ,supplier.id id from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time >= '"+start+"' " + "and s.create_time <'" + end +"'");
				sql3.append(" and s.is_valid = 1 and s.status != 4 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id"
						+ " group by su.id) res1 on supplier.id = res1.id where supplier.supplier_name like '%"+supplierName+"%' "
						+ " ) as ans3,");
	
				StringBuffer sql4 = new StringBuffer();
				sql4.append("(select supplier.supplier_name name ,IFNULL(res2.money,0) yifu,supplier.id id  from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time >= '"+start+"' " + "and s.create_time <'" + end +"'");
				sql4.append(" and s.is_valid = 1 and s.status = 3 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id"
						+ " group by su.id) res2 on supplier.id = res2.id where supplier.supplier_name like '%"+supplierName+"%' "
						+ " ) as ans4");
				
				buffer.append(sql1.toString());
				buffer.append(sql2.toString());
				buffer.append(sql3.toString());
				buffer.append(sql4.toString());
				buffer.append(" where ans1.id = ans2.id and ans2.id = ans3.id and ans3.id = ans4.id");
				System.out.println(buffer.toString());
				
				List<Object[]> list1 = new ArrayList<>();
				list1 = paymentSupplierService.statis(buffer.toString());
				for(Object[] objects : list1){
					PayStatics payStatics = new PayStatics();
					payStatics.setSupplierName(objects[0].toString());
					payStatics.setHistoryYingfu((BigDecimal)objects[1]);
					payStatics.setHistoryYifu((BigDecimal)objects[2]);
					payStatics.setHistoryDaifu((BigDecimal)objects[3]);
					payStatics.setThisYingfu((BigDecimal)objects[4]);
					payStatics.setThisYifu((BigDecimal)objects[5]);
					payStatics.setThisDaifu((BigDecimal)objects[6]);
					payStatics.setYingfu((BigDecimal)objects[7]);
					payStatics.setYifu((BigDecimal)objects[8]);
					payStatics.setDaifu((BigDecimal)objects[9]);
					payStatics.setSupplierId(Long.valueOf(String.valueOf(objects[10])));
					answer.add(payStatics);
				}
			}
		
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
	
	@RequestMapping("/detail/view")
	@ResponseBody
	public Json detail(@DateTimeFormat(pattern="yyyy-MM-dd")Date startDate,@DateTimeFormat(pattern="yyyy-MM-dd")Date endDate,
			Long supplierId,HttpSession session){
		Json json = new Json();
		try {
			List<Details> answer = new ArrayList<>();
			if(startDate == null || endDate == null || startDate.compareTo(endDate) > 0){
				throw new Exception("请输入正确的起始时间和结束时间");
			}
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(endDate);
			calendar.add(Calendar.DATE, 1);
			Date nextEndDate = calendar.getTime();
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
			String start = formatter.format(startDate);  
			String end = formatter.format(nextEndDate);
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("select res.xianlumingcheng,res.order_number,res.fatuandate,res.enddate,res.contact,res.name,su.supplier_name,res.money as yingfu, case res.status when 3 then money else  0 end as yifu,case res.status when 3 then 0 else  money end as qianfu,res.people from ");
			buffer.append(" (select o.xianlumingcheng,o.order_number,o.fatuandate,o.tianshu,DATE_ADD(o.fatuandate,INTERVAL (o.tianshu-1) DAY) as enddate,o.contact,hy_admin.name,l.supplier,s.status,item.money ,o.people"
					+ " from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c,hy_payables_line_item item ,hy_order o,hy_admin ,hy_group g,hy_line l "
					+ " where s.is_valid = 1 and s.status != 4 and su.id = "+ supplierId+" ");
			buffer.append(" and s.supplier_contract = c.id and c.supplier_id = su.id and hy_admin.username = o.supplier ");
			buffer.append(" and item.payment_line_id = s.id and o.id = item.order_id and o.group_id = g.id and g.line = l.id ");
			buffer.append(" and s.create_time >= '"+start+"' and s.create_time < '"+end +"'  )res,hy_supplier su ");
			buffer.append(" where su.id = res.supplier order by qianfu desc");
			
			List<Object[]> list1 = new ArrayList<>();
			list1 = paymentSupplierService.statis(buffer.toString());
			
			for(Object[] objects : list1){
				Details details = new Details();
				details.setLineName(objects[0].toString());
				details.setOrderSn(objects[1].toString());
				details.setStartDate((Date)objects[2]);
				details.setEndDate((Date)objects[3]);
				details.setContract(objects[4].toString());
				details.setJidiao(objects[5].toString());
				details.setSupplierName(objects[6].toString());
				details.setYingfu((BigDecimal)objects[7]);
				details.setYifu((BigDecimal)objects[8]);
				details.setDaifu((BigDecimal)objects[9]);
				details.setPeople(Integer.valueOf(objects[10].toString()));
				answer.add(details);
			}
			
			
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
	
	
	@RequestMapping("/list/view/download")
	public void listdownload(@DateTimeFormat(pattern="yyyy-MM-dd")Date startDate,@DateTimeFormat(pattern="yyyy-MM-dd")Date endDate,
			String supplierName,HttpSession session,HttpServletRequest request,HttpServletResponse response){
		try {
			List<PayStatics> answer = new ArrayList<>();
			if(startDate == null || endDate == null || startDate.compareTo(endDate) > 0){
				throw new Exception("请输入正确的起始时间和结束时间");
			}
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(endDate);
			calendar.add(Calendar.DATE, 1);
			Date nextEndDate = calendar.getTime();
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
			String start = formatter.format(startDate);  
			String end = formatter.format(nextEndDate);
			
			//查询所有
			if(supplierName == null){
				StringBuffer buffer = new StringBuffer();
				StringBuffer sql1 = new StringBuffer();
				sql1.append(" select ans1.name,ans1.yingfu,ans2.yifu ,ans1.yingfu-ans2.yifu,ans3.yingfu as yingfu1,ans4.yifu as yingfu2,ans3.yingfu-ans4.yifu,ans1.yingfu+ans3.yingfu,ans2.yifu+ans4.yifu, ans1.yingfu+ans3.yingfu-ans2.yifu-ans4.yifu ,ans1.id from ");
				sql1.append(" (select supplier.supplier_name name ,IFNULL(res1.money,0) yingfu ,supplier.id id from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time < '" + start+"' ");
				sql1.append(" and s.is_valid = 1 and s.status != 4 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id "
						+ " group by su.id) res1 on supplier.id = res1.id where supplier.is_inner = 0) as ans1, ");
	
				StringBuffer sql2 = new StringBuffer();
				sql2.append("(select supplier.supplier_name name ,IFNULL(res1.money,0) yifu ,supplier.id id from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time < '" + start+"' ");
				sql2.append(" and s.is_valid = 1 and s.status =3 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id"
						+ " group by su.id) res1 on supplier.id = res1.id where supplier.is_inner = 0) as ans2,");
				
				StringBuffer sql3 = new StringBuffer();
				sql3.append(" (select supplier.supplier_name name ,IFNULL(res1.money,0) yingfu ,supplier.id id from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time >= '"+start+"' " + "and s.create_time <'" + end +"'");
				sql3.append(" and s.is_valid = 1 and s.status != 4 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id"
						+ " group by su.id) res1 on supplier.id = res1.id where supplier.is_inner = 0) as ans3,");
	
				StringBuffer sql4 = new StringBuffer();
				sql4.append("(select supplier.supplier_name name ,IFNULL(res2.money,0) yifu,supplier.id id  from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time >= '"+start+"' " + "and s.create_time <'" + end +"'");
				sql4.append(" and s.is_valid = 1 and s.status = 3 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id"
						+ " group by su.id) res2 on supplier.id = res2.id where supplier.is_inner = 0) as ans4");
				
				buffer.append(sql1.toString());
				buffer.append(sql2.toString());
				buffer.append(sql3.toString());
				buffer.append(sql4.toString());
				buffer.append(" where ans1.id = ans2.id and ans2.id = ans3.id and ans3.id = ans4.id");
				System.out.println(buffer.toString());
				
				List<Object[]> list1 = new ArrayList<>();
				list1 = paymentSupplierService.statis(buffer.toString());
				for(Object[] objects : list1){
					PayStatics payStatics = new PayStatics();
					payStatics.setSupplierName(objects[0].toString());
					payStatics.setHistoryYingfu((BigDecimal)objects[1]);
					payStatics.setHistoryYifu((BigDecimal)objects[2]);
					payStatics.setHistoryDaifu((BigDecimal)objects[3]);
					payStatics.setThisYingfu((BigDecimal)objects[4]);
					payStatics.setThisYifu((BigDecimal)objects[5]);
					payStatics.setThisDaifu((BigDecimal)objects[6]);
					payStatics.setYingfu((BigDecimal)objects[7]);
					payStatics.setYifu((BigDecimal)objects[8]);
					payStatics.setDaifu((BigDecimal)objects[9]);
					payStatics.setSupplierId(Long.valueOf(objects[10].toString()));
					answer.add(payStatics);
				}
			}else{
				StringBuffer buffer = new StringBuffer();
				StringBuffer sql1 = new StringBuffer();
				sql1.append(" select ans1.name,ans1.yingfu,ans2.yifu ,ans1.yingfu-ans2.yifu,ans3.yingfu as yingfu1,ans4.yifu as yingfu2,ans3.yingfu-ans4.yifu,ans1.yingfu+ans3.yingfu,ans2.yifu+ans4.yifu, ans1.yingfu+ans3.yingfu-ans2.yifu-ans4.yifu ,ans1.id from ");
				sql1.append(" (select supplier.supplier_name name ,IFNULL(res1.money,0) yingfu ,supplier.id id from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time < '" + start+"' ");
				sql1.append(" and s.is_valid = 1 and s.status != 4 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id "
						+ " group by su.id) res1 on supplier.id = res1.id where supplier.supplier_name like '%"+supplierName+"%' "
						+ " ) as ans1, ");
	
				StringBuffer sql2 = new StringBuffer();
				sql2.append("(select supplier.supplier_name name ,IFNULL(res1.money,0) yifu ,supplier.id id from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time < '" + start+"' ");
				sql2.append(" and s.is_valid = 1 and s.status =3 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id"
						+ " group by su.id) res1 on supplier.id = res1.id where supplier.supplier_name like '%"+supplierName+"%' "
						+ " ) as ans2,");
				
				StringBuffer sql3 = new StringBuffer();
				sql3.append(" (select supplier.supplier_name name ,IFNULL(res1.money,0) yingfu ,supplier.id id from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time >= '"+start+"' " + "and s.create_time <'" + end +"'");
				sql3.append(" and s.is_valid = 1 and s.status != 4 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id"
						+ " group by su.id) res1 on supplier.id = res1.id where supplier.supplier_name like '%"+supplierName+"%' "
						+ " ) as ans3,");
	
				StringBuffer sql4 = new StringBuffer();
				sql4.append("(select supplier.supplier_name name ,IFNULL(res2.money,0) yifu,supplier.id id  from hy_supplier supplier left join "
						+ " (select IFNULL(IFNULL(sum(s.money_sum),0)-IFNULL(sum(s.debtamount),0),0) as money,su.id id from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c"
						+ " where s.create_time >= '"+start+"' " + "and s.create_time <'" + end +"'");
				sql4.append(" and s.is_valid = 1 and s.status = 3 "
						+ " and s.supplier_contract = c.id and c.supplier_id = su.id"
						+ " group by su.id) res2 on supplier.id = res2.id where supplier.supplier_name like '%"+supplierName+"%' "
						+ " ) as ans4");
				
				buffer.append(sql1.toString());
				buffer.append(sql2.toString());
				buffer.append(sql3.toString());
				buffer.append(sql4.toString());
				buffer.append(" where ans1.id = ans2.id and ans2.id = ans3.id and ans3.id = ans4.id");
				System.out.println(buffer.toString());
				
				List<Object[]> list1 = new ArrayList<>();
				list1 = paymentSupplierService.statis(buffer.toString());
				for(Object[] objects : list1){
					PayStatics payStatics = new PayStatics();
					payStatics.setSupplierName(objects[0].toString());
					payStatics.setHistoryYingfu((BigDecimal)objects[1]);
					payStatics.setHistoryYifu((BigDecimal)objects[2]);
					payStatics.setHistoryDaifu((BigDecimal)objects[3]);
					payStatics.setThisYingfu((BigDecimal)objects[4]);
					payStatics.setThisYifu((BigDecimal)objects[5]);
					payStatics.setThisDaifu((BigDecimal)objects[6]);
					payStatics.setYingfu((BigDecimal)objects[7]);
					payStatics.setYifu((BigDecimal)objects[8]);
					payStatics.setDaifu((BigDecimal)objects[9]);
					payStatics.setSupplierId(Long.valueOf(String.valueOf(objects[10])));
					answer.add(payStatics);
				}
			}
			StringBuffer sb2 = new StringBuffer();
			sb2.append("应付款对账表");
			String fileName = "应付款对账表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "linepaymentStatics.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, answer, fileName, tableTitle, configFile);
		
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/detail/view/download")
	public void detaildownload(@DateTimeFormat(pattern="yyyy-MM-dd")Date startDate,@DateTimeFormat(pattern="yyyy-MM-dd")Date endDate,
			Long supplierId,HttpSession session,HttpServletRequest request,HttpServletResponse response){
		try {
			List<Details> answer = new ArrayList<>();
			if(startDate == null || endDate == null || startDate.compareTo(endDate) > 0){
				throw new Exception("请输入正确的起始时间和结束时间");
			}
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(endDate);
			calendar.add(Calendar.DATE, 1);
			Date nextEndDate = calendar.getTime();
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
			String start = formatter.format(startDate);  
			String end = formatter.format(nextEndDate);
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("select res.xianlumingcheng,res.order_number,res.fatuandate,res.enddate,res.contact,res.name,su.supplier_name,res.money as yingfu, case res.status when 3 then money else  0 end as yifu,case res.status when 3 then 0 else  money end as qianfu,res.people from ");
			buffer.append(" (select o.xianlumingcheng,o.order_number,o.fatuandate,o.tianshu,DATE_ADD(o.fatuandate,INTERVAL (o.tianshu-1) DAY) as enddate,o.contact,hy_admin.name,l.supplier,s.status,item.money ,o.people"
					+ " from hy_payment_supplier s ,hy_supplier su ,hy_supplier_contract c,hy_payables_line_item item ,hy_order o,hy_admin ,hy_group g,hy_line l "
					+ " where s.is_valid = 1 and s.status != 4 and su.id = "+ supplierId+" ");
			buffer.append(" and s.supplier_contract = c.id and c.supplier_id = su.id and hy_admin.username = o.supplier ");
			buffer.append(" and item.payment_line_id = s.id and o.id = item.order_id and o.group_id = g.id and g.line = l.id ");
			buffer.append(" and s.create_time >= '"+start+"' and s.create_time < '"+end +"'  )res,hy_supplier su ");
			buffer.append(" where su.id = res.supplier order by qianfu desc");
			
			List<Object[]> list1 = new ArrayList<>();
			list1 = paymentSupplierService.statis(buffer.toString());
			
			for(Object[] objects : list1){
				Details details = new Details();
				details.setLineName(objects[0].toString());
				details.setOrderSn(objects[1].toString());
				details.setStartDate((Date)objects[2]);
				details.setEndDate((Date)objects[3]);
				details.setContract(objects[4].toString());
				details.setJidiao(objects[5].toString());
				details.setSupplierName(objects[6].toString());
				details.setYingfu((BigDecimal)objects[7]);
				details.setYifu((BigDecimal)objects[8]);
				details.setDaifu((BigDecimal)objects[9]);
				details.setPeople(Integer.valueOf(objects[10].toString()));
				answer.add(details);
			}
			
			StringBuffer sb2 = new StringBuffer();
			sb2.append("应付款对账详情表");
			String fileName = "应付款对账详情表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "linepaymentDetailStatics.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, answer, fileName, tableTitle, configFile);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	
	
	public class PayStatics{
		private String supplierName;
		private BigDecimal historyYingfu;
		private BigDecimal historyYifu;
		private BigDecimal historyDaifu;
		
		private BigDecimal thisYingfu;
		private BigDecimal thisYifu;
		private BigDecimal thisDaifu;
		
		private BigDecimal yingfu;
		private BigDecimal yifu;
		private BigDecimal daifu;
		
		private Long supplierId;
		
		public String getSupplierName() {
			return supplierName;
		}
		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}
		public BigDecimal getYingfu() {
			return yingfu;
		}
		public void setYingfu(BigDecimal yingfu) {
			this.yingfu = yingfu;
		}
		public BigDecimal getYifu() {
			return yifu;
		}
		public void setYifu(BigDecimal yifu) {
			this.yifu = yifu;
		}
		public BigDecimal getDaifu() {
			return daifu;
		}
		public void setDaifu(BigDecimal daifu) {
			this.daifu = daifu;
		}
		public BigDecimal getHistoryYingfu() {
			return historyYingfu;
		}
		public void setHistoryYingfu(BigDecimal historyYingfu) {
			this.historyYingfu = historyYingfu;
		}
		public BigDecimal getHistoryYifu() {
			return historyYifu;
		}
		public void setHistoryYifu(BigDecimal historyYifu) {
			this.historyYifu = historyYifu;
		}
		public BigDecimal getHistoryDaifu() {
			return historyDaifu;
		}
		public void setHistoryDaifu(BigDecimal historyDaifu) {
			this.historyDaifu = historyDaifu;
		}
		public BigDecimal getThisYingfu() {
			return thisYingfu;
		}
		public void setThisYingfu(BigDecimal thisYingfu) {
			this.thisYingfu = thisYingfu;
		}
		public BigDecimal getThisYifu() {
			return thisYifu;
		}
		public void setThisYifu(BigDecimal thisYifu) {
			this.thisYifu = thisYifu;
		}
		public BigDecimal getThisDaifu() {
			return thisDaifu;
		}
		public void setThisDaifu(BigDecimal thisDaifu) {
			this.thisDaifu = thisDaifu;
		}
		public Long getSupplierId() {
			return supplierId;
		}
		public void setSupplierId(Long supplierId) {
			this.supplierId = supplierId;
		}
		
		
		
	}
	public class Details{
		private String lineName; //线路名称
		private String orderSn;  //订单编号
		private Date startDate; //发团日期
		private Date endDate;  //回团日期
		private String jidiao;//计调
		private String contract;//联系人
		private String supplierName;//供应商名称
		private Integer people; //人数
		private BigDecimal yingfu; //应付款
		private BigDecimal yifu;  //已付款
		private BigDecimal daifu; //待付款
		public String getLineName() {
			return lineName;
		}
		public void setLineName(String lineName) {
			this.lineName = lineName;
		}
		public String getOrderSn() {
			return orderSn;
		}
		public void setOrderSn(String orderSn) {
			this.orderSn = orderSn;
		}
		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		public Date getEndDate() {
			return endDate;
		}
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
		public String getJidiao() {
			return jidiao;
		}
		public void setJidiao(String jidiao) {
			this.jidiao = jidiao;
		}
		public String getContract() {
			return contract;
		}
		public void setContract(String contract) {
			this.contract = contract;
		}
		public String getSupplierName() {
			return supplierName;
		}
		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}
		public Integer getPeople() {
			return people;
		}
		public void setPeople(Integer people) {
			this.people = people;
		}
		public BigDecimal getYingfu() {
			return yingfu;
		}
		public void setYingfu(BigDecimal yingfu) {
			this.yingfu = yingfu;
		}
		public BigDecimal getYifu() {
			return yifu;
		}
		public void setYifu(BigDecimal yifu) {
			this.yifu = yifu;
		}
		public BigDecimal getDaifu() {
			return daifu;
		}
		public void setDaifu(BigDecimal daifu) {
			this.daifu = daifu;
		}
		
		
		
		
		
	}

}
