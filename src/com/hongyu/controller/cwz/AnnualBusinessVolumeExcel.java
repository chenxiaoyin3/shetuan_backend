package com.hongyu.controller.cwz;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.Specialty;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.SpecialtyCategoryService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.util.DateUtil;

@RestController
// 上面的加一个“Rest”，加上下面这一句，后面就不用再加@responsebody了 表示返回是json格式
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/business/specialty_turnover/")
public class AnnualBusinessVolumeExcel {

	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderService;

	@Resource(name = "businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;

	@Resource(name = "specialtyServiceImpl")
	SpecialtyService specialtyService;
	
	@Resource(name = "specialtyCategoryServiceImpl")
	SpecialtyCategoryService specialtyCategoryService;

	// 前端传过来的都用包装类
	@RequestMapping(value = "annual_list/view/excel") // get
	public Json annualBusinessVolume(Integer year, Integer type, Long value, Pageable pageable, HttpServletRequest request, HttpServletResponse response) {
		// 接收前端传过来的json数据
		Json j = new Json();
		try {
			//把results传进去
			List<Wrap> results = new ArrayList<>();
			// 最后返回去
			Page<HashMap<String, Object>> page = null;
			StringBuffer excelTitle = new StringBuffer("" + year);
			
			
			// 知识点:filter加进去，如果筛选条件一样，就不用clear
			if (type == null) {
//				return this.getResults("fail1", false, page);
				type = 0; 
			}
			// 第一种情况
			if (type == 0) {
//				//把results传进去
//				List<Wrap> results = new ArrayList<>();
				//java传容器进去应该就是传指针
				page = selectAllAnnualRecords(year, pageable, results);
				j = this.getResults("success", true, page, results);
				excelTitle.append("年营业额总统计"); 
			
			} else if (type == 1) {
//				//把results传进去
//				List<Wrap> results = new ArrayList<>();
				page = selectAnnualRecordsByCategory(year, value, pageable, results, excelTitle);
				j = this.getResults("success", true, page, results);
				excelTitle.append("营业额总统计"); 
				
			} else if (type == 2) {
//				//把results传进去
//				List<Wrap> results = new ArrayList<>();
				page = selectAnnualRecordWithSpecialty(year, value, pageable, results, excelTitle);
				j = this.getResults("success", true, page, results);
				excelTitle.append("营业额总统计"); 
				
			} else {
				//把results传进去
				//List<Wrap> results = new ArrayList<>();
				return this.getResults("fail2", false, page, results);
			}
			
			//导出Excel的固定套路
			StringBuffer sb2 = new StringBuffer();
//			sb2.append("年营业额统计");
//			sb2.append("calculateAnnualTurnover");
			sb2.append(excelTitle);
			String fileName = "年营业额统计报表.xls";  // Excel文件名
//			String fileName = "annualTurnoverForm.xls";
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "annualTurnover.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
			
		} catch (Exception e) {
			// TODO: handle exception
			j.setSuccess(false);
			j.setMsg(e.getMessage());
			// TODO
			return null;
		}
		// !! this is essential, if you return j, then there will be an exception: javax.servlet.ServletException: 
		return null;//TODO
//		return j;
	}

	private Page<HashMap<String, Object>> selectAnnualRecordWithSpecialty(Integer year, Long value, Pageable pageable, List<Wrap> results, StringBuffer excelTitle)
			throws ParseException {
		Page<HashMap<String, Object>> page;
		// 返回特定产品的营业额
		// 每一个list里面是saleTime saleAmount和saleRate
		List<HashMap<String, Object>> annualTurnover = new ArrayList<>();
		BigDecimal yearResult = new BigDecimal(0.0);

		Specialty mySpecialty = specialtyService.find(value);
		excelTitle.append(mySpecialty.getName());
		
		// 问题：如果年份是空的怎么办？抛异常
		for (int month = 1; month <= 12; month++) {

			// 这个Map要放在list里面 要12个Map 都放到list里面
			Map<String, Object> annualTurnoverItem = new HashMap<String, Object>();
			String yearAndMonth = new String("");

			// 返回全部营业额
			List<Filter> adminFilter = new ArrayList<Filter>();
			adminFilter.add(Filter.eq("isValid", true));
			adminFilter.add(Filter.eq("isShow", false));
			adminFilter.add(Filter.eq("orderState", 6));
			// 是前端传来的年份筛选 如果是null就都查出来
			if (year != null) {

//				// 获得天数
//				Calendar cal = Calendar.getInstance();
//				cal.set(Calendar.YEAR, year);
//				cal.set(Calendar.MONTH, month);
//				int maxDays = cal.getActualMaximum(Calendar.DATE);
//
//				// 然后要用每一个月筛选 这个截字符串实现
//				String startDateString = year + "-" + month + "-1 00:00:00";
//				String endDateString = year + "-" + month + "-" + maxDays + " 23:59:59";
//
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date startTime = sdf.parse(startDateString);
//				Date endTime = sdf.parse(endDateString);
//
//				yearAndMonth = year + "-" + month;

				//2018-7-18 不知道这个year能不能行 因为是int类型
				String lastDayOfMonth = AnnualBusinessVolume.lastDayOfMonthString(year+"-"+month+"-01");
				
				String endDateString = lastDayOfMonth + " 23:59:59";
				String currentYearMonth = endDateString.substring(0, 7);
				String startDateString = currentYearMonth + "-01 00:00:00";
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startTime = sdf.parse(startDateString);
				Date endTime = sdf.parse(endDateString);
				yearAndMonth = currentYearMonth;
				
				
				// !!!!这里可能有错，筛选字段可能不对
				adminFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				adminFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));

			} else if (year == null) {

				// 赋值粘贴 让年份是最新一年

				// 获得天数
				Calendar cal = Calendar.getInstance();
				// 获得当前的年数
				int cal2 = cal.get(Calendar.YEAR);

//				cal.set(Calendar.YEAR, cal2);
//				cal.set(Calendar.MONTH, month);
//				int maxDays = cal.getActualMaximum(Calendar.DATE);
//
//				// 然后要用每一个月筛选 这个截字符串实现
//				String startDateString = year + "-" + month + "-1 00:00:00";
//				String endDateString = year + "-" + month + "-" + maxDays + " 23:59:59";
//				yearAndMonth = year + "-" + month;
//
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date startTime = sdf.parse(startDateString);
//				Date endTime = sdf.parse(endDateString);
				
				//2018-7-18 不知道这个year能不能行 因为是int类型
				String lastDayOfMonth = AnnualBusinessVolume.lastDayOfMonthString(cal2+"-"+month+"-01");
				
				String endDateString = lastDayOfMonth + " 23:59:59";
				String currentYearMonth = endDateString.substring(0, 7);
				String startDateString = currentYearMonth + "-01 00:00:00";
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startTime = sdf.parse(startDateString);
				Date endTime = sdf.parse(endDateString);
				yearAndMonth = currentYearMonth;
				

				// !!!!这里可能有错，筛选字段可能不对
				adminFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				adminFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
			}

			// Long和Integer都不能用filter.in
			List<BusinessOrder> businessOrder = businessOrderService.findList(null, adminFilter, null);
			// 去找到想要的ID

			// List<Long> BusinessOrderId = new ArrayList<Long>();
			// 遍历变量businessOrder里面的每一个BusinessOrder类型变量temp
			// for(BusinessOrder temp : businessOrder){
			// BusinessOrderId.add(temp.getId());
			// }

			List<Filter> adminFilter2 = new ArrayList<Filter>();
			adminFilter2.add(Filter.eq("isValid", true));
			adminFilter2.add(Filter.eq("isShow", false));
			adminFilter2.add(Filter.eq("orderState", 12));

			if (year != null) {

				// 获得天数
//				Calendar cal = Calendar.getInstance();
//				cal.set(Calendar.YEAR, year);
//				cal.set(Calendar.MONTH, month);
//				int maxDays = cal.getActualMaximum(Calendar.DATE);
//
//				// 然后要用每一个月筛选 这个截字符串实现
//				String startDateString = year + "-" + month + "-1 00:00:00";
//				String endDateString = year + "-" + month + "-" + maxDays + " 23:59:59";
//
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date startTime = sdf.parse(startDateString);
//				Date endTime = sdf.parse(endDateString);
//
//				yearAndMonth = year + "-" + month;
				
				//2018-7-18 不知道这个year能不能行 因为是int类型
				String lastDayOfMonth = AnnualBusinessVolume.lastDayOfMonthString(year+"-"+month+"-01");
				
				String endDateString = lastDayOfMonth + " 23:59:59";
				String currentYearMonth = endDateString.substring(0, 7);
				String startDateString = currentYearMonth + "-01 00:00:00";
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startTime = sdf.parse(startDateString);
				Date endTime = sdf.parse(endDateString);
				yearAndMonth = currentYearMonth;

				// !!!!这里可能有错，筛选字段可能不对
				adminFilter2.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				adminFilter2.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));

			} else if (year == null) {

				// 赋值粘贴 让年份是最新一年

				// 获得天数
				Calendar cal = Calendar.getInstance();
				// 获得当前的年数
				int cal2 = cal.get(Calendar.YEAR);

//				cal.set(Calendar.YEAR, cal2);
//				cal.set(Calendar.MONTH, month);
//				int maxDays = cal.getActualMaximum(Calendar.DATE);
//
//				// 然后要用每一个月筛选 这个截字符串实现
//				String startDateString = year + "-" + month + "-1 00:00:00";
//				String endDateString = year + "-" + month + "-" + maxDays + " 23:59:59";
//				yearAndMonth = year + "-" + month;
//
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date startTime = sdf.parse(startDateString);
//				Date endTime = sdf.parse(endDateString);
				
				//2018-7-18 不知道这个year能不能行 因为是int类型
				String lastDayOfMonth = AnnualBusinessVolume.lastDayOfMonthString(cal2+"-"+month+"-01");
				
				String endDateString = lastDayOfMonth + " 23:59:59";
				String currentYearMonth = endDateString.substring(0, 7);
				String startDateString = currentYearMonth + "-01 00:00:00";
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startTime = sdf.parse(startDateString);
				Date endTime = sdf.parse(endDateString);
				yearAndMonth = currentYearMonth;

				// !!!!这里可能有错，筛选字段可能不对
				adminFilter2.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				adminFilter2.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
			}

			List<BusinessOrder> businessOrder2 = businessOrderService.findList(null, adminFilter2, null);

			// 所有的BusinessOrder都加到一起就行了
			for (BusinessOrder temp : businessOrder2) {
				businessOrder.add(temp);
			}

			// 从BusinessOrder去找BusinessOrderItem
			List<BusinessOrderItem> businessOrderItem = new ArrayList<BusinessOrderItem>();
			List<Filter> adminFilter3 = new ArrayList<Filter>();

		
//			adminFilter3.add(Filter.in("businessOrder", businessOrder));
//			businessOrderItem = businessOrderItemService.findList(null, adminFilter3, null);

			if(businessOrder!=null && businessOrder.size()>0){
				// add by cwz 2018-7-15
				if (value != null) {
					// 直接筛选specialtyID
					adminFilter3.add(Filter.eq("specialty", value));
				}
				adminFilter3.add(Filter.in("businessOrder", businessOrder));
				businessOrderItem = businessOrderItemService.findList(null, adminFilter3, null);
			}
			
			// 最后的营业额
			BigDecimal result = new BigDecimal(0.0);

			// 从BusinessOrderItem里面找数据计算营业额
			for (BusinessOrderItem temp : businessOrderItem) {

				if (temp.getQuantity() != null && temp.getReturnQuantity() != null && temp.getSalePrice() != null) {
					int quantity = temp.getQuantity().intValue();
					int returnQuantity = temp.getReturnQuantity().intValue();
					int netQuantity = quantity - returnQuantity;
					if(netQuantity < 0){
						//容错
						System.out.println("搞事情：退的比买的都多");
						netQuantity = 0;
					}
					
					BigDecimal netQuantityCal = new BigDecimal(netQuantity);
					BigDecimal salePrice = temp.getSalePrice();
					// 遍历把所有的营业额都加进去 保留两位小数点精度
					//2018-7-18
					result = result.add(salePrice.multiply(netQuantityCal));
					// .setScale(2, RoundingMode.HALF_UP) 这个只有除法的时候才能用
				} else {
					System.out.println("前端没有数据");
				}

			}
			SimpleDateFormat saleTimeSdf = new SimpleDateFormat("yyyy-MM");
			Date yearAndMonthDate = saleTimeSdf.parse(yearAndMonth);

			annualTurnoverItem.put("saleTime", yearAndMonthDate);
			annualTurnoverItem.put("saleAmount", result);

			
			// 第三个占比率先空着 之后再赋值进去
			// 向下转型 需要强转
			annualTurnover.add((HashMap<String, Object>) annualTurnoverItem);

			// 计算年销售量，便于计算比率
			yearResult = yearResult.add(result);
			if (month == 12) {
				BigDecimal number1 = BigDecimal.ZERO;
				BigDecimal rate = BigDecimal.ZERO;
				// 开始计算每个月的比率
				for (HashMap<String, Object> temp : annualTurnover) {
					// 这里也要强转 不知道会不会损失精度
					number1 = (BigDecimal) temp.get("saleAmount");
					int r = yearResult.compareTo(BigDecimal.ZERO);
					if(r != 0){
						rate = number1.divide(yearResult, 5, RoundingMode.HALF_UP);
					}
					// 趁机加进去
					temp.put("saleRate", rate);
				}
				
				String timeInputChinese = "";
				//Date timeInput = new Date();
				BigDecimal numberInput = BigDecimal.ZERO;
				BigDecimal rateInput = BigDecimal.ZERO;
				int monthChinese = 1;

				
				//需要遍历所有的数据
				//在这里向excel里面放值 把三个值加到Wrap类里面，再把wrap放到result里面（wrap的list）
				for(HashMap<String, Object> temp : annualTurnover){
					
					//这个挺奇妙的
					Wrap input = new Wrap();
					
					timeInputChinese = year + "年" + monthChinese + "月";
					//timeInput =  (Date) temp.get("saleTime");
					numberInput = (BigDecimal) temp.get("saleAmount");
					rateInput = (BigDecimal) temp.get("saleRate");
					
					input.setSaleTimeChinese(timeInputChinese);
					input.setSaleAmount(numberInput);
					input.setSaleRate(rateInput);
					
					//注意是results不是result
					results.add(input);
					monthChinese++;
				}
				
			}
		} // for循环结束

		// 就一页 12条
		page = new Page<>(annualTurnover, annualTurnover.size(), pageable);
		// 多次用filter，最后想要一个pagable的，应该什么时候用pagable筛选？最后一次用就行
		return page;
	}

	private Page<HashMap<String, Object>> selectAnnualRecordsByCategory(Integer year, Long value, Pageable pageable, List<Wrap> results, StringBuffer excelTitle)
			throws ParseException {
		Page<HashMap<String, Object>> page;
		// 返回特定类别的营业额
		// 思路：首先从speciality表里面查找CategoryID正确的项，然后拿到该表的id，作为额外的筛选条件
		// 每一个list里面是saleTime saleAmount和saleRate
		List<HashMap<String, Object>> annualTurnover = new ArrayList<>();
		BigDecimal yearResult = new BigDecimal(0.0);
		
		//2018-7-19 老师要categoryID 对应的名字 从category里面去找
		//下面这种就不能用
//		Filter categoryName = new Filter();
		SpecialtyCategory mySpecialtyCategory = specialtyCategoryService.find(value);
		excelTitle.append(mySpecialtyCategory.getName());
		

		// 问题：如果年份是空的怎么办？抛异常
		for (int month = 1; month <= 12; month++) {

			// 这个Map要放在list里面 要12个Map 都放到list里面
			Map<String, Object> annualTurnoverItem = new HashMap<String, Object>();
			String yearAndMonth = new String("");

			// 返回全部营业额
			List<Filter> adminFilter = new ArrayList<Filter>();
			adminFilter.add(Filter.eq("isValid", true));
			adminFilter.add(Filter.eq("isShow", false));
			adminFilter.add(Filter.eq("orderState", 6));

			// 是前端传来的年份筛选 如果是null就都查出来
			if (year != null) {

				// 获得天数
//				Calendar cal = Calendar.getInstance();
//				cal.set(Calendar.YEAR, year);
//				cal.set(Calendar.MONTH, month);
//				int maxDays = cal.getActualMaximum(Calendar.DATE);
//
//				// 然后要用每一个月筛选 这个截字符串实现
//				String startDateString = year + "-" + month + "-1 00:00:00";
//				String endDateString = year + "-" + month + "-" + maxDays + " 23:59:59";
//
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date startTime = sdf.parse(startDateString);
//				Date endTime = sdf.parse(endDateString);
//
//				yearAndMonth = year + "-" + month;
				
				//2018-7-18 不知道这个year能不能行 因为是int类型
				String lastDayOfMonth = AnnualBusinessVolume.lastDayOfMonthString(year+"-"+month+"-01");
				
				String endDateString = lastDayOfMonth + " 23:59:59";
				String currentYearMonth = endDateString.substring(0, 7);
				String startDateString = currentYearMonth + "-01 00:00:00";
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startTime = sdf.parse(startDateString);
				Date endTime = sdf.parse(endDateString);
				yearAndMonth = currentYearMonth;
				

				// !!!!这里可能有错，筛选字段可能不对
				adminFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				adminFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));

			} else if (year == null) {

				// 赋值粘贴 让年份是最新一年

				// 获得天数
				Calendar cal = Calendar.getInstance();
				// 获得当前的年数
				int cal2 = cal.get(Calendar.YEAR);

//				cal.set(Calendar.YEAR, cal2);
//				cal.set(Calendar.MONTH, month);
//				int maxDays = cal.getActualMaximum(Calendar.DATE);
//
//				// 然后要用每一个月筛选 这个截字符串实现
//				String startDateString = year + "-" + month + "-1 00:00:00";
//				String endDateString = year + "-" + month + "-" + maxDays + " 23:59:59";
//				yearAndMonth = year + "-" + month;
				
				//2018-7-18 不知道这个year能不能行 因为是int类型
				String lastDayOfMonth = AnnualBusinessVolume.lastDayOfMonthString(cal2+"-"+month+"-01");
				
				String endDateString = lastDayOfMonth + " 23:59:59";
				String currentYearMonth = endDateString.substring(0, 7);
				String startDateString = currentYearMonth + "-01 00:00:00";
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startTime = sdf.parse(startDateString);
				Date endTime = sdf.parse(endDateString);
				yearAndMonth = currentYearMonth;
				

//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date startTime = sdf.parse(startDateString);
//				Date endTime = sdf.parse(endDateString);

				// !!!!这里可能有错，筛选字段可能不对
				adminFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				adminFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
			}

			// Long和Integer都不能用filter.in
			List<BusinessOrder> businessOrder = businessOrderService.findList(null, adminFilter, null);
			// 去找到想要的ID

			List<Filter> adminFilter2 = new ArrayList<Filter>();
			adminFilter2.add(Filter.eq("isValid", true));
			adminFilter2.add(Filter.eq("isShow", false));
			adminFilter2.add(Filter.eq("orderState", 12));

			if (year != null) {

				// 获得天数
//				Calendar cal = Calendar.getInstance();
//				cal.set(Calendar.YEAR, year);
//				cal.set(Calendar.MONTH, month);
//				int maxDays = cal.getActualMaximum(Calendar.DATE);
//
//				// 然后要用每一个月筛选 这个截字符串实现
//				String startDateString = year + "-" + month + "-1 00:00:00";
//				String endDateString = year + "-" + month + "-" + maxDays + " 23:59:59";
//
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date startTime = sdf.parse(startDateString);
//				Date endTime = sdf.parse(endDateString);
//
//				yearAndMonth = year + "-" + month;
				
				//2018-7-18 不知道这个year能不能行 因为是int类型
				String lastDayOfMonth = AnnualBusinessVolume.lastDayOfMonthString(year+"-"+month+"-01");
				
				String endDateString = lastDayOfMonth + " 23:59:59";
				String currentYearMonth = endDateString.substring(0, 7);
				String startDateString = currentYearMonth + "-01 00:00:00";
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startTime = sdf.parse(startDateString);
				Date endTime = sdf.parse(endDateString);
				yearAndMonth = currentYearMonth;

				// !!!!这里可能有错，筛选字段可能不对
				adminFilter2.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				adminFilter2.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));

			} else if (year == null) {

				// 赋值粘贴 让年份是最新一年

				// 获得天数
				Calendar cal = Calendar.getInstance();
				// 获得当前的年数
				int cal2 = cal.get(Calendar.YEAR);

//				cal.set(Calendar.YEAR, cal2);
//				cal.set(Calendar.MONTH, month);
//				int maxDays = cal.getActualMaximum(Calendar.DATE);
//
//				// 然后要用每一个月筛选 这个截字符串实现
//				String startDateString = year + "-" + month + "-1 00:00:00";
//				String endDateString = year + "-" + month + "-" + maxDays + " 23:59:59";
//				yearAndMonth = year + "-" + month;
//
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date startTime = sdf.parse(startDateString);
//				Date endTime = sdf.parse(endDateString);

				//2018-7-18 不知道这个year能不能行 因为是int类型
				String lastDayOfMonth = AnnualBusinessVolume.lastDayOfMonthString(cal2+"-"+month+"-01");
				
				String endDateString = lastDayOfMonth + " 23:59:59";
				String currentYearMonth = endDateString.substring(0, 7);
				String startDateString = currentYearMonth + "-01 00:00:00";
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startTime = sdf.parse(startDateString);
				Date endTime = sdf.parse(endDateString);
				yearAndMonth = currentYearMonth;
				
				// !!!!这里可能有错，筛选字段可能不对
				adminFilter2.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				adminFilter2.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
			}

			List<BusinessOrder> businessOrder2 = businessOrderService.findList(null, adminFilter2, null);

			// 所有的BusinessOrder都加到一起就行了
			for (BusinessOrder temp : businessOrder2) {
				businessOrder.add(temp);
			}

			// 注意这个后面一定要初始化！！！！ 否则只能是null
			// 能不能就从刚刚查出来的businessItem里面找？？？
			List<Long> specialtyItemId = new ArrayList<Long>();
			List<BusinessOrderItem> businessOrderItem = new ArrayList<BusinessOrderItem>();
			List<BusinessOrderItem> businessOrderItem2 = new ArrayList<BusinessOrderItem>();
			List<BusinessOrderItem> businessOrderItem3 = new ArrayList<BusinessOrderItem>();

			// ！！！这个逻辑有问题
			// 从speciality这个表里面去找合适的CategoryID
			if (value != null) {

				List<Filter> adminFilter4 = new ArrayList<Filter>();
				// 因为传过来是一个long类型的
				adminFilter4.add(Filter.eq("category", value));
				List<Specialty> specialtyItem = specialtyService.findList(null, adminFilter4, null);
				for (Specialty temp : specialtyItem) {
					// 注意有时候这个是错的：不应该拿id 应该拿item
					// 要看数据库里面是什么
					specialtyItemId.add(temp.getId());
				}

				adminFilter4.clear();
				
				
				List<BusinessOrderItem> preBusinessOrderItem = null;
				// 这是个Long类型的List，只能循环来用filter
				// 因为Long类型的是主键，不会重复，所以可以遍历
				for (Long temp : specialtyItemId) {
					
					preBusinessOrderItem = new ArrayList<BusinessOrderItem>();
					
					// 把每一个Long类型的加进去
					if(temp != null){
						adminFilter4.add(Filter.eq("specialty", temp));
						preBusinessOrderItem = businessOrderItemService.findList(null, adminFilter4, null);
					} else {
						System.out.println("没有查到对应category的specialID");
					}
					// ！！！不能加到外面注意 因为每一次specialty不能同时等于多个值


					for (BusinessOrderItem temp1 : preBusinessOrderItem) {
						// 加到大集合里面去
						// 这里不应该找交集 应该找和下面筛选条件的并集！！！！！想办法解决
						businessOrderItem.add(temp1);
					}

					adminFilter4.clear();
				}

			}

			// 从BusinessOrder去找BusinessOrderItem
			// List<BusinessOrderItem> businessOrderItem = new
			// ArrayList<BusinessOrderItem>();
			List<Filter> adminFilter3 = new ArrayList<Filter>();

			// 因为是一个字段 我的list是preBusinessOrderItem，所以不好筛选，在已筛选的条件上继续筛选
			// adminFilter3.add(Filter.in("businessOrder",
			// preBusinessOrderItem));
			// 不能equal一个list 所以有list的id就 而且不会报错 下面这句是错的：
			// adminFilter3.add(Filter.eq("businessOrder", specialtyItemId));

			// 根据businessOrder得到的item
			if(businessOrder!=null && businessOrder.size()>0){
				
				adminFilter3.add(Filter.in("businessOrder", businessOrder));
				businessOrderItem2 = businessOrderItemService.findList(null, adminFilter3, null);
			}
			
			// 强行取交集
			// 根据businessOrder得到的item和根据specialty

			for (BusinessOrderItem temp : businessOrderItem) {
				for (BusinessOrderItem temp2 : businessOrderItem2) {
					if (temp.getId().equals(temp2.getId())) {
						businessOrderItem3.add(temp);
						break;
					}
				}
			}

			// 最后的营业额
			BigDecimal result = new BigDecimal(0.0);

			// 从BusinessOrderItem里面找数据计算营业额
			for (BusinessOrderItem temp : businessOrderItem3) {

				if (temp.getQuantity() != null && temp.getReturnQuantity() != null && temp.getSalePrice() != null) {
					int quantity = temp.getQuantity().intValue();
					int returnQuantity = temp.getReturnQuantity().intValue();
					int netQuantity = quantity - returnQuantity;
					if(netQuantity < 0){
						//容错
						System.out.println("搞事情：退的比买的都多");
						netQuantity = 0;
					}
					
					BigDecimal netQuantityCal = new BigDecimal(netQuantity);
					BigDecimal salePrice = temp.getSalePrice();
					
					// 遍历把所有的营业额都加进去 保留两位小数点精度
					result = result.add(salePrice.multiply(netQuantityCal));
					System.out.println("減完之後是：" + netQuantity + "乘完结果：" + salePrice.multiply(netQuantityCal) + "总结果："+ result);

					
					// .setScale(2, RoundingMode.HALF_UP) 这个只有除法的时候才能用
				} else {
					System.out.println("前端没有数据");
				}

			}
			SimpleDateFormat saleTimeSdf = new SimpleDateFormat("yyyy-MM");
			Date yearAndMonthDate = saleTimeSdf.parse(yearAndMonth);

			annualTurnoverItem.put("saleTime", yearAndMonthDate);
			annualTurnoverItem.put("saleAmount", result);

			// 第三个占比率先空着 之后再赋值进去
			// 向下转型 需要强转
			annualTurnover.add((HashMap<String, Object>) annualTurnoverItem);

			// 计算年销售量，便于计算比率
			yearResult = yearResult.add(result);
			if (month == 12) {
				BigDecimal number1 = BigDecimal.ZERO;
				BigDecimal rate = BigDecimal.ZERO;
				// 开始计算每个月的比率
				for (HashMap<String, Object> temp : annualTurnover) {
					// 这里也要强转 不知道会不会损失精度
					number1 = (BigDecimal) temp.get("saleAmount");
					//r=big_decimal.compareTo(BigDecimal.Zero) if(r==0) //等于
					int r = yearResult.compareTo(BigDecimal.ZERO);
					if(r != 0){
						rate = number1.divide(yearResult, 5, RoundingMode.HALF_UP);
					}
					// 趁机加进去
					temp.put("saleRate", rate);
				}
				
				String timeInputChinese = "";
				//Date timeInput = new Date();
				BigDecimal numberInput = BigDecimal.ZERO;
				BigDecimal rateInput = BigDecimal.ZERO;
				int monthChinese = 1;
				
				//需要遍历所有的数据
				//在这里向excel里面放值 把三个值加到Wrap类里面，再把wrap放到result里面（wrap的list）
				for(HashMap<String, Object> temp : annualTurnover){
					
					//这个挺奇妙的
					Wrap input = new Wrap();
					
					timeInputChinese = year + "年" + monthChinese + "月";
					//timeInput =  (Date) temp.get("saleTime");
					numberInput = (BigDecimal) temp.get("saleAmount");
					rateInput = (BigDecimal) temp.get("saleRate");
					
					input.setSaleTimeChinese(timeInputChinese);
					input.setSaleAmount(numberInput);
					input.setSaleRate(rateInput);
					
					//注意是results不是result
					results.add(input);
					monthChinese++;
				}
				
			}
		} // for循环结束

		// 就一页 12条
		page = new Page<>(annualTurnover, annualTurnover.size(), pageable);
		// 多次用filter，最后想要一个pagable的，应该什么时候用pagable筛选？最后一次用就行
		return page;
	}

	private Page<HashMap<String, Object>> selectAllAnnualRecords(Integer year, Pageable pageable, List<Wrap> results)
			throws ParseException {
		Page<HashMap<String, Object>> page = new Page<>();
		// 每一个list里面是saleTime saleAmount和saleRate
		List<HashMap<String, Object>> annualTurnover = new ArrayList<>();
		BigDecimal yearResult = new BigDecimal(0.0);
		//不要在循環里定義變量
		Map<String, Object> annualTurnoverItem = null;
		// 问题：如果年份是空的怎么办？抛异常
		for (int month = 1; month <= 12; month++) {
			// 这个Map要放在list里面 要12个Map 都放到list里面
			annualTurnoverItem = new HashMap<String, Object>();
			String yearAndMonth = "";

			// 返回全部营业额 business_order
			List<Filter> adminFilter = new ArrayList<Filter>();
			adminFilter.add(Filter.eq("isValid", true));
			adminFilter.add(Filter.eq("isShow", false));
			adminFilter.add(Filter.eq("orderState", 6));
			// 是前端传来的年份筛选 如果是null就都查出来
			if (year != null) {

				String lastDayOfMonth = AnnualBusinessVolume.lastDayOfMonthString(year+"-"+month+"-01");
//				// 获得天数
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				Date date1 = null;
//				try {
//					date1 = sdf.parse();
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(date1);
//				cal.set(Calendar.DAY_OF_MONTH, 1);
//				cal.roll(Calendar.DAY_OF_MONTH, -1);
//				
//				Calendar cal = Calendar.getInstance();
//				cal.set(Calendar.YEAR, year);
//				cal.set(Calendar.MONTH, month);
//				int maxDays = cal.getActualMaximum(Calendar.DATE);

				// 然后要用每一个月筛选 这个截字符串实现
				
				String endDateString = lastDayOfMonth + " 23:59:59";
				String currentYearMonth = endDateString.substring(0, 7);
				String startDateString = currentYearMonth + "-01 00:00:00";

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startTime = sdf.parse(startDateString);
				Date endTime = sdf.parse(endDateString);
				yearAndMonth = currentYearMonth;
				// !!!!这里可能有错，筛选字段可能不对
				adminFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				adminFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));

			} else if (year == null) {

				// 赋值粘贴 让年份是最新一年

				// 获得天数
				Calendar cal = Calendar.getInstance();
				// 获得当前的年数
				int cal2 = cal.get(Calendar.YEAR);

//				cal.set(Calendar.YEAR, cal2);
//				cal.set(Calendar.MONTH, month);
//				int maxDays = cal.getActualMaximum(Calendar.DATE);

				
				
				// 然后要用每一个月筛选 这个截字符串实现
//				String startDateString = year + "-" + month + "-1 00:00:00";
//				String endDateString = year + "-" + month + "-" + maxDays + " 23:59:59";
//				yearAndMonth = year + "-" + month;


				//2018-7-18 不知道这个year能不能行 因为是int类型
				String lastDayOfMonth = AnnualBusinessVolume.lastDayOfMonthString(cal2+"-"+month+"-01");
				
				String endDateString = lastDayOfMonth + " 23:59:59";
				String currentYearMonth = endDateString.substring(0, 7);
				String startDateString = currentYearMonth + "-01 00:00:00";
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startTime = sdf.parse(startDateString);
				Date endTime = sdf.parse(endDateString);
				yearAndMonth = currentYearMonth;
				
				// !!!!这里可能有错，筛选字段可能不对
				adminFilter.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				adminFilter.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
			}

			// Long和Integer都不能用filter.in
			List<BusinessOrder> businessOrder = businessOrderService.findList(null, adminFilter, null);
			// 去找到想要的ID

			// List<Long> BusinessOrderId = new ArrayList<Long>();
			// 遍历变量businessOrder里面的每一个BusinessOrder类型变量temp
			// for(BusinessOrder temp : businessOrder){
			// BusinessOrderId.add(temp.getId());
			// }

			List<Filter> adminFilter2 = new ArrayList<Filter>();
			adminFilter2.add(Filter.eq("isValid", true));
			adminFilter2.add(Filter.eq("isShow", false));
			adminFilter2.add(Filter.eq("orderState", 12));

			if (year != null) {

				// 获得天数
//				Calendar cal = Calendar.getInstance();
//				cal.set(Calendar.YEAR, year);
//				cal.set(Calendar.MONTH, month);
//				int maxDays = cal.getActualMaximum(Calendar.DATE);
//
//				// 然后要用每一个月筛选 这个截字符串实现
//				String startDateString = year + "-" + month + "-1 00:00:00";
//				String endDateString = year + "-" + month + "-" + maxDays + " 23:59:59";
//
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date startTime = sdf.parse(startDateString);
//				Date endTime = sdf.parse(endDateString);
//
//				yearAndMonth = year + "-" + month;
				
				
				//2018-7-18 不知道这个year能不能行 因为是int类型
				String lastDayOfMonth = AnnualBusinessVolume.lastDayOfMonthString(year+"-"+month+"-01");
				
				String endDateString = lastDayOfMonth + " 23:59:59";
				String currentYearMonth = endDateString.substring(0, 7);
				String startDateString = currentYearMonth + "-01 00:00:00";
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startTime = sdf.parse(startDateString);
				Date endTime = sdf.parse(endDateString);
				yearAndMonth = currentYearMonth;

				// !!!!这里可能有错，筛选字段可能不对
				adminFilter2.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				adminFilter2.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));

			} else if (year == null) {

				// 赋值粘贴 让年份是最新一年

				// 获得天数
				Calendar cal = Calendar.getInstance();
				// 获得当前的年数
				int cal2 = cal.get(Calendar.YEAR);

//				cal.set(Calendar.YEAR, cal2);
//				cal.set(Calendar.MONTH, month);
//				int maxDays = cal.getActualMaximum(Calendar.DATE);
//
//				// 然后要用每一个月筛选 这个截字符串实现
//				String startDateString = year + "-" + month + "-1 00:00:00";
//				String endDateString = year + "-" + month + "-" + maxDays + " 23:59:59";
//				yearAndMonth = year + "-" + month;
//
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date startTime = sdf.parse(startDateString);
//				Date endTime = sdf.parse(endDateString);
				
				//2018-7-18 不知道这个year能不能行 因为是int类型
				String lastDayOfMonth = AnnualBusinessVolume.lastDayOfMonthString(cal2+"-"+month+"-01");
				
				String endDateString = lastDayOfMonth + " 23:59:59";
				String currentYearMonth = endDateString.substring(0, 7);
				String startDateString = currentYearMonth + "-01 00:00:00";
				

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date startTime = sdf.parse(startDateString);
				Date endTime = sdf.parse(endDateString);
				yearAndMonth = currentYearMonth;

				// !!!!这里可能有错，筛选字段可能不对
				adminFilter2.add(new Filter("orderTime", Operator.ge, DateUtil.getStartOfDay(startTime)));
				adminFilter2.add(new Filter("orderTime", Operator.le, DateUtil.getEndOfDay(endTime)));
			}

			List<BusinessOrder> businessOrder2 = businessOrderService.findList(null, adminFilter2, null);

			// 所有的BusinessOrder都加到一起就行了
			businessOrder.addAll(businessOrder2);
			// 一個集合合併另一個集合
//			for (BusinessOrder temp : businessOrder2) {
//				businessOrder.add(temp);
//			}

			// 从BusinessOrder去找BusinessOrderItem
			List<BusinessOrderItem> businessOrderItem = new ArrayList<>();
			List<Filter> adminFilter3 = new ArrayList<>();
			if(businessOrder!=null && businessOrder.size()>0){
				adminFilter3.add(Filter.in("businessOrder", businessOrder));
				businessOrderItem = businessOrderItemService.findList(null, adminFilter3, null);
			}

			// 最后的营业额
			BigDecimal result = new BigDecimal(0.0);

			// 从BusinessOrderItem里面找数据计算营业额
			for (BusinessOrderItem temp : businessOrderItem) {

				if (temp.getQuantity() != null && temp.getReturnQuantity() != null && temp.getSalePrice() != null) {
					int quantity = temp.getQuantity().intValue();
					int returnQuantity = temp.getReturnQuantity().intValue();
					int netQuantity = quantity - returnQuantity;
					if(netQuantity < 0){
						//容错
						System.out.println("搞事情：退的比买的都多");
						netQuantity = 0;
					}
					
					BigDecimal netQuantityCal = new BigDecimal(netQuantity);
					BigDecimal salePrice = temp.getSalePrice();
					// 遍历把所有的营业额都加进去 保留两位小数点精度
					result = result.add(salePrice.multiply(netQuantityCal));//TODO
					//System.out.println("減完之後是：" + netQuantity + "乘完结果：" + salePrice.multiply(netQuantityCal) + "总结果："+ result);
					
					// .setScale(2, RoundingMode.HALF_UP) 这个只有除法的时候才能用
				} else {
					System.out.println("前端没有数据");
				}
			}
			SimpleDateFormat saleTimeSdf = new SimpleDateFormat("yyyy-MM");
			Date yearAndMonthDate = saleTimeSdf.parse(yearAndMonth);
			annualTurnoverItem.put("saleTime", yearAndMonthDate);
			annualTurnoverItem.put("saleAmount", result);
			// 第三个占比率先空着 之后再赋值进去
			// 向下转型 需要强转
			annualTurnover.add((HashMap<String, Object>) annualTurnoverItem);
			// 计算年销售量，便于计算比率
			// 注意這個一定要是有左邊的等於號
			yearResult = yearResult.add(result);
			if (month == 12) {
				// 开始计算每个月的比率
				BigDecimal number1 = BigDecimal.ZERO;
				BigDecimal rate = BigDecimal.ZERO;
				for (HashMap<String, Object> temp : annualTurnover) {
					// 这里也要强转 不知道会不会损失精度
					number1 = (BigDecimal) temp.get("saleAmount");
					int r = yearResult.compareTo(BigDecimal.ZERO);
					if(r != 0){
						rate = number1.divide(yearResult, 5, RoundingMode.HALF_UP);
					}
					//.setScale(2, RoundingMode.HALF_UP)
					// 趁机加进去
					temp.put("saleRate", rate);
				}//到这里 所有的数据就齐了
				
				//Date timeInput = new Date();
				String timeInputChinese = "";
				BigDecimal numberInput = BigDecimal.ZERO;
				BigDecimal rateInput = BigDecimal.ZERO;
				int monthChinese = 1;
				
				//需要遍历所有的数据
				//在这里向excel里面放值 把三个值加到Wrap类里面，再把wrap放到result里面（wrap的list）
				for(HashMap<String, Object> temp : annualTurnover){
					
					//这个挺奇妙的
					Wrap input = new Wrap();
					
					//2018-8-5 由于传过去的年月要中文，在此处修改
					//timeInput =  (Date) temp.get("saleTime");
					timeInputChinese = year + "年" + monthChinese + "月";
					numberInput = (BigDecimal) temp.get("saleAmount");
					rateInput = (BigDecimal) temp.get("saleRate");
					
					//input.setSaleTime(timeInput);
					input.setSaleTimeChinese(timeInputChinese);
					input.setSaleAmount(numberInput);
					input.setSaleRate(rateInput);
					
					//注意是results不是result
					results.add(input);
					monthChinese++;
				}
				
			}
		} // for循环结束

		// 就一页 12条
		page = new Page<>(annualTurnover, annualTurnover.size(), pageable);
		// 多次用filter，最后想要一个pagable的，应该什么时候用pagable筛选？最后一次用就行
		return page;
	}

	private Json getResults(String msg, boolean success, Page<HashMap<String, Object>> page, List<Wrap> results) {
		Json json = new Json();
		json.setMsg(msg);
		json.setSuccess(success);
		json.setObj(page);
		return json;
	}
	
	public static String getNextDateString(String fromDate)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(fromDate);
		System.out.println(sdf.format(date));
		return sdf.format(getLastDate(date));
	}

	private static Date getLastDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 1);
		return cal.getTime();
	}

	public static Date lastDayOfMonth(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null;
		try {
			date1 = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.roll(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}
	
	public static String lastDayOfMonthString(String date){
		Date last = lastDayOfMonth(date);
		return dateToString(last);
	}
	
	public Date toYYYYMMDDDate(Date from) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date to = null;
		try {
			to = sdf.parse(sdf.format(from));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return to;
	}

	public static String dateToString(Date from) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(from);
	}
	
	//创建包装类，便于excel
	public static class Wrap{
		
		private Date saleTime;
		private BigDecimal saleAmount;
		private BigDecimal saleRate;
		//中文的年月 2018-8-5
		private String saleTimeChinese;
		
		public String getSaleTimeChinese() {
			return saleTimeChinese;
		}
		public void setSaleTimeChinese(String saleTimeChinese) {
			this.saleTimeChinese = saleTimeChinese;
		}
		public Date getSaleTime() {
			return saleTime;
		}
		public void setSaleTime(Date saleTime) {
			this.saleTime = saleTime;
		}
		public BigDecimal getSaleAmount() {
			return saleAmount;
		}
		public void setSaleAmount(BigDecimal saleAmount) {
			this.saleAmount = saleAmount;
		}
		public BigDecimal getSaleRate() {
			return saleRate;
		}
		public void setSaleRate(BigDecimal saleRate) {
			this.saleRate = saleRate;
		}
	}
	

}
