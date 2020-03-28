package com.hongyu.controller.lbc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.controller.BaseController;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HySupplier;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.MonthShouldPayInterT;
import com.hongyu.entity.PaymentSupplier;
import com.hongyu.entity.ReceiptServicer;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.MonthShouldPayInterTService;
import com.hongyu.service.PaymentSupplierService;
import com.hongyu.service.ReceiptServicerService;
import com.hongyu.service.SpecialtyCategoryService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.StoreService;
import com.hongyu.util.ArrayHandler;


//应付款报表
//1。应付款对账表
//2。应付款月报表

@Controller
@RequestMapping("admin/account_payable/")
public class CheckAccountPayableController {
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "hyRoleServiceImpl")
	HyRoleService hyRoleService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;
	
	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderService;
	
	@Resource(name = "businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;
	
	@Resource(name = "specialtyServiceImpl")
	SpecialtyService specialtyService;
	
	@Resource(name = "specialtyCategoryServiceImpl")
	SpecialtyCategoryService specialtyCategoryService;
	
	@Resource(name = "hyDepartmentModelServiceImpl")
	HyDepartmentModelService hyDepartmentModelService;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "receiptServicerServiceImpl")
	ReceiptServicerService receiptServicerService;
	
	@Resource(name = "paymentSupplierServiceImpl")
	PaymentSupplierService paymentSupplierService;
	
	
	@Resource(name = "monthShouldPayInterTServiceImpl")
	MonthShouldPayInterTService monthShouldPayInterTService;
	
	BaseController baseController = new BaseController();
	
	public static class Wrap {
		String supplierName;
		//月初虹宇欠供应商金额
		BigDecimal monthStartMoney;
		//供应商欠虹宇总欠款
		BigDecimal debtMoney;
		//当月增加欠款
		BigDecimal monthIncreaseMoney;
		//当月减少欠款
		BigDecimal monthDecreaseMoney;
		//月末金额
		BigDecimal monthEndMoney;
		public String getSupplierName() {
			return supplierName;
		}
		public void setSupplierName(String supplierName) {
			this.supplierName = supplierName;
		}
		public BigDecimal getMonthStartMoney() {
			return monthStartMoney;
		}
		public void setMonthStartMoney(BigDecimal monthStartMoney) {
			this.monthStartMoney = monthStartMoney;
		}
		public BigDecimal getDebtMoney() {
			return debtMoney;
		}
		public void setDebtMoney(BigDecimal debtMoney) {
			this.debtMoney = debtMoney;
		}
		public BigDecimal getMonthIncreaseMoney() {
			return monthIncreaseMoney;
		}
		public void setMonthIncreaseMoney(BigDecimal monthIncreaseMoney) {
			this.monthIncreaseMoney = monthIncreaseMoney;
		}
		public BigDecimal getMonthDecreaseMoney() {
			return monthDecreaseMoney;
		}
		public void setMonthDecreaseMoney(BigDecimal monthDecreaseMoney) {
			this.monthDecreaseMoney = monthDecreaseMoney;
		}
		public BigDecimal getMonthEndMoney() {
			return monthEndMoney;
		}
		public void setMonthEndMoney(BigDecimal monthEndMoney) {
			this.monthEndMoney = monthEndMoney;
		}
	}
	
	//应付款月报表
	//查询条件：日期
	@RequestMapping(value="check")
	@ResponseBody
	public Json AccountPayableList(@DateTimeFormat(pattern = "yyyy-MM-dd") Date date){
		
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try{
			//线路订单 供应商通过 
			if(date == null){
				date = new Date();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date startDate = calendar.getTime();
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			Date endDate = calendar.getTime();
			//供应商名称 历史应付 历史已付 历史欠付
			StringBuilder sb = new StringBuilder("");
			sb.append("SELECT supplier_name, month_start_money, debt_money, month_increase_money, month_decrease_money, month_end_money from hy_month_should_pay_intert ");
			//此处不用加group by
			sb.append(" where start_month between '" + format.format(startDate) + "' and '" + format.format(endDate) + "' order by month_start_money desc");
			
			System.out.println(sb);
			List<Object[]> list = hyOrderService.statis(sb.toString());		
			String[] keys = new String[]{"supplierName","monthStartMoney","debtMoney","monthIncreaseMoney","monthDecreaseMoney","monthEndMoney"};
			List<Map<String, Object>> maps = new LinkedList<>();
			
			for(Object[] objects : list){
				maps.add(ArrayHandler.toMap(keys, objects));
			}
			BigDecimal sum = new BigDecimal(0);
			for(Map<String, Object> map : maps) {
				sum = sum.add((BigDecimal)map.get("monthStartMoney"));
			}
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("sum", sum);
			resultMap.put("list", maps);
			
//			Map<String, Object> resultMap = new HashMap<>();
//			for(Map<String, Object> m : maps) {
//				resultMap.putAll(m);
//			}
			//System.out.println(resultMap.toString());
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(resultMap);
		
		}catch (Exception e){
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	@RequestMapping(value="/get_excel_check")
	public void export2Excel4(HttpServletRequest request,
			HttpServletResponse response, String fileName,
			String tableTitle, String configFile,
			HttpSession session, @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		//Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			
			if(date == null){
				date = new Date();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date startDate = calendar.getTime();
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			Date endDate = calendar.getTime();
			//供应商名称 历史应付 历史已付 历史欠付
			StringBuilder sb = new StringBuilder("");
			sb.append("SELECT supplier_name, month_start_money, debt_money, month_increase_money, month_decrease_money, month_end_money from hy_month_should_pay_intert ");
			//此处不用加group by
			sb.append(" where start_month between '" + format.format(startDate) + "' and '" + format.format(endDate) + "' order by month_start_money desc");
			
			System.out.println(sb);
			List<Object[]> list = hyOrderService.statis(sb.toString());		
			String[] keys = new String[]{"supplierName","monthStartMoney","debtMoney","monthIncreaseMoney","monthDecreaseMoney","monthEndMoney"};
			
			
			
			
			List<Map<String, Object>> maps = new LinkedList<>();
			
			for(Object[] objects : list){
				maps.add(ArrayHandler.toMap(keys, objects));
			}
			BigDecimal sum = new BigDecimal(0);
			for(Map<String, Object> map : maps) {
				sum = sum.add((BigDecimal)map.get("monthStartMoney"));
			}
			
			List<Wrap> tempList = new ArrayList<>();
			Wrap wrap = new Wrap();
			wrap.setSupplierName("月初合计");
			wrap.setMonthStartMoney(sum);
			tempList.add(wrap);
			for(int i = 0; i < maps.size(); i++) {
				Wrap wrap1 = new Wrap();
				wrap1.setMonthStartMoney( (BigDecimal) (maps.get(i).get("monthStartMoney")) );
				wrap1.setMonthIncreaseMoney( (BigDecimal) (maps.get(i).get("monthIncreaseMoney")) );
				wrap1.setMonthDecreaseMoney( (BigDecimal) (maps.get(i).get("monthDecreaseMoney")) );
				wrap1.setDebtMoney( (BigDecimal) (maps.get(i).get("debtMoney")) );
				wrap1.setMonthEndMoney( (BigDecimal) (maps.get(i).get("monthEndMoney")) );
				wrap1.setSupplierName( (String) (maps.get(i).get("supplierName")) );
				
				tempList.add(wrap1);
				
			}
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM");
			String title = "应付款月报表";
			title = format1.format(startDate) + " " + title;

			baseController.export2Excel(request, response, tempList, "应付款月报表.xls", title , "moneyShouldPayStatistics.xml");
			
			
//			json.setSuccess(true);
//			json.setMsg("导出excel成功");
//			json.setObj(null);

			
		} catch (Exception e) {
//			json.setSuccess(false);
//			json.setMsg("查询失败");
//			json.setObj(null);
			e.printStackTrace();
		}
		
		
		//return json;
	}
	
	//初始化中间表
	@RequestMapping(value="intertable/init")
	@ResponseBody
	public Json InitInterTable(@DateTimeFormat(pattern = "yyyy-MM-dd") Date date){
		
		Json json = new Json();
		try{
			//初始化今天之前的中间表 从2018年6月开始
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			Calendar calendar2 = Calendar.getInstance();
			//确保会调用process(今天)
			calendar2.set(Calendar.HOUR_OF_DAY, 0);
			//2018-07-01
			calendar2.set(2018, 9, 1, 0, 0, 0);
			System.out.println(calendar2.getTime());
			while(calendar2.compareTo(calendar) <= 0) {
				process(calendar2.getTime());
				calendar2.add(Calendar.MONTH, 1);
			}
			
		}catch (Exception e){
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	public void process(Date date) {
		// TODO Auto-generated method stub

		//对每一个供应商，统计今天内的金额变动，添加到供应商对应的当月的行中
		//每个供应商的本月增加都是 未付 本月减少是已付
		try {
				
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			//上一天
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			//赋值给date
			
			//找到当前日期上一天所在月的第一天
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			date = calendar.getTime();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Date dateForSelect = calendar.getTime();
			
			List<HySupplier> hySuppliers = hySupplierService.findAll();
			//对于每个供应商，找到自己今天的未付和已付
			for(HySupplier hySupplier : hySuppliers) {
				System.out.println(hySupplier.getSupplierName());
				//没有合同 
				if(hySupplier.getHySupplierContracts().size() == 0) {
					continue;
				}
				//查询数据库中有没有startdate大于等于date的行，没有就新建
				List<Filter> filters = new LinkedList<>();
				filters.add(Filter.eq("supplierName", hySupplier.getSupplierName()));
				filters.add(Filter.ge("startMonth", dateForSelect));
				MonthShouldPayInterT mspt = null; 
				List<MonthShouldPayInterT> mList = monthShouldPayInterTService.findList(null, filters, null);
				if(mList.size() == 0) {
					//新建一行
					mspt = new MonthShouldPayInterT();
					mspt.setMonthDecreaseMoney(new BigDecimal(0));
					mspt.setMonthIncreaseMoney(new BigDecimal(0));
					mspt.setStartMonth(dateForSelect);
					mspt.setSupplierName(hySupplier.getSupplierName());
					//初始金额，是查询之前所有的未付-已付
					//查找之前月份的结束值 如果之前月份没有值，则设置为0
					List<Filter> filters1 = new LinkedList<>();
					filters1.add(Filter.eq("supplierName", hySupplier.getSupplierName()));
					//大于等于上个月的1号
					calendar.setTime(dateForSelect);
					calendar.add(Calendar.MONTH, -1);
					filters1.add(Filter.ge("startMonth", calendar.getTime()));
					System.out.println(calendar.getTime());
					System.out.println(dateForSelect);
					//小于这个月的一号
					filters1.add(Filter.lt("startMonth", dateForSelect));
					List<MonthShouldPayInterT> mList1 = monthShouldPayInterTService.findList(null, filters1, null);
					if(mList1.size() == 0) {
						mspt.setMonthStartMoney(new BigDecimal(0));
						mspt.setDebtMoney(new BigDecimal(0));
					}
					else {
						//设置为上个月结束值
						mspt.setMonthStartMoney(mList1.get(0).getMonthEndMoney());
						//设置为上个月供应商欠虹宇的欠款值
						mspt.setDebtMoney(mList1.get(0).getDebtMoney());
					}
					mspt.setMonthEndMoney(mspt.getMonthStartMoney());
					//写入数据库
					monthShouldPayInterTService.save(mspt);
				}
				else {
					mspt = mList.get(0);
				}
				//写入mspt
				List<Filter> filters2 = new LinkedList<>();
				filters2.add(Filter.eq("supplierName", hySupplier.getSupplierName()));
				//未付
				//filters2.add(Filter.ne("status", 3));
				//且除了已驳回的
				filters2.add(Filter.ne("status", 4));
				//创建时间大于等于这个月1号00:00:00 小于下个月第一天0:0:0
				calendar.setTime(date);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				filters2.add(Filter.ge("createTime", calendar.getTime()));
				//下一个月的第一天
				calendar.add(Calendar.MONTH, 1);
//				calendar.set(Calendar.HOUR_OF_DAY, 23);
//				calendar.set(Calendar.MINUTE, 59);
//				calendar.set(Calendar.SECOND, 59);
//				calendar.set(Calendar.MILLISECOND, 999);
				filters2.add(Filter.lt("createTime", calendar.getTime()));
				List<PaymentSupplier> paymentSuppliers = paymentSupplierService.findList(null, filters2, null);
				for(PaymentSupplier paymentSupplier : paymentSuppliers) {
					if(paymentSupplier.getDebtamount() == null) {
						paymentSupplier.setDebtamount(new BigDecimal(0));
					}
					//对每一个筛选出的未支付打款单
					//增加本月增加
					mspt.setMonthIncreaseMoney(mspt.getMonthIncreaseMoney().add(paymentSupplier.getMoneySum()).subtract(paymentSupplier.getDebtamount()));
					//月末金额增加
					mspt.setMonthEndMoney(mspt.getMonthEndMoney().add(paymentSupplier.getMoneySum()).subtract(paymentSupplier.getDebtamount()));
				}
				//清空过滤器
				filters2.clear();
				filters2.add(Filter.eq("supplierName", hySupplier.getSupplierName()));
				//已付
				filters2.add(Filter.eq("status", 3));
				//创建时间大于等于这个月1号00:00:00 小于下个月第一天0:0:0
				calendar.setTime(date);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				filters2.add(Filter.ge("payDate", calendar.getTime()));
				calendar.add(Calendar.MONTH, 1);
				filters2.add(Filter.lt("payDate", calendar.getTime()));
				List<PaymentSupplier> paymentSuppliers1 = paymentSupplierService.findList(null, filters2, null);
				for(PaymentSupplier paymentSupplier : paymentSuppliers1) {
					if(paymentSupplier.getDebtamount() == null) {
						paymentSupplier.setDebtamount(new BigDecimal(0));
					}
					//对每一个筛选出的已支付打款单
					//增加本月减少
					mspt.setMonthDecreaseMoney(mspt.getMonthDecreaseMoney().add(paymentSupplier.getMoneySum()).subtract(paymentSupplier.getDebtamount()));
					//月末金额减少
					mspt.setMonthEndMoney(mspt.getMonthEndMoney().subtract(paymentSupplier.getMoneySum()).add(paymentSupplier.getDebtamount()));
				}
				
				//对于每个今天增加的欠款
				//对每个供应商合同的负责人
				List<String> liables = new LinkedList<>();
				for(HySupplierContract hySupplierContract :hySupplier.getHySupplierContracts()) {
					liables.add(hySupplierContract.getLiable().getUsername());
				}
				//供应商未签约
//				if(liables.size() == 0) {
//					monthShouldPayInterTService.update(mspt);
//					continue;
//				}
				//找到供应商所有负责人后，作为筛选条件
				//统计供应商欠虹宇欠款的增加数和减少数
				filters2.clear();
				filters2.add(Filter.in("supplierName", liables));
				//增加欠款
				//filters2.add(Filter.eq("state", 0));
				//创建时间大于等于这个月1号00:00:00 小于下个月第一天0:0:0
				calendar.setTime(date);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				filters2.add(Filter.ge("date", calendar.getTime()));
				calendar.add(Calendar.MONTH, 1);
				filters2.add(Filter.lt("date", calendar.getTime()));
				List<ReceiptServicer> receiptServicers = receiptServicerService.findList(null, filters2, null);
				for(ReceiptServicer receiptServicer : receiptServicers) {
					if(receiptServicer.getState() == 0) {
						//增加
						mspt.setDebtMoney(mspt.getDebtMoney().add(receiptServicer.getAmount()));
						//月末欠供应商金额减少
						mspt.setMonthEndMoney(mspt.getMonthEndMoney().subtract(receiptServicer.getAmount()));
					}
					else {
						//减少
						mspt.setDebtMoney(mspt.getDebtMoney().subtract(receiptServicer.getAmount()));
						//月末欠供应商金额增加
						mspt.setMonthEndMoney(mspt.getMonthEndMoney().add(receiptServicer.getAmount()));
					}
				}
				//更新数据库
				monthShouldPayInterTService.update(mspt);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			System.err.println(e);
		}
		
		
	}
	
}
