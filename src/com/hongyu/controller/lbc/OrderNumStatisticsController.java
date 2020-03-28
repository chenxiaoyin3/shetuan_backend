package com.hongyu.controller.lbc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.SpecialtyCategoryService;
import com.hongyu.service.SpecialtyService;
import com.hongyu.service.StoreService;
import com.hongyu.util.ArrayHandler;

@Controller
@RequestMapping("admin/store_line_order_statis_order_num/order_num")
public class OrderNumStatisticsController {
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
	
	public static class Wrap1 {
		String month;
		Integer orderNum;
		Double ratio;
	}
	
	public static class Wrap2 {
		String quarter;
		Integer orderNum;
		Double ratio;
	}
	
	
	
	//统计新的订单
	//月度 
	@RequestMapping(value="/month_view")
	@ResponseBody
	public Json turnoverMonthList(@DateTimeFormat(pattern = "yyyy-MM") Date start,
            @DateTimeFormat(pattern = "yyyy-MM")Date end){
		
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try{
	
			StringBuilder sb = new StringBuilder("");
			sb.append("select DATE_FORMAT(o1.fatuandate,'%Y-%m') y_m,IFNULL(count(*),0) order_num, 1.0 ratio");
			//线路订单 供应商通过 
			sb.append(" from hy_order o1 where o1.type=1 and o1.status=3 and o1.fatuandate is not null");
			if(start==null){
				start = new Date();
			}
			if(end==null){
				end = new Date();
			}
			
			sb.append(" and DATE_FORMAT(o1.fatuandate,'%Y-%m') between '"+format.format(start)+"' and '"+format.format(end)+"'");
			//按月groupby了
			sb.append(" group by DATE_FORMAT(o1.fatuandate,'%Y-%m')");
			
			List<Object[]> list = hyOrderService.statis(sb.toString());
			if(list == null || list.size() == 0) {
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(list);
				return json;
			}
			Integer pre = Integer.valueOf(list.get(0)[1].toString());
			for(int i=1;i<list.size();i++){
				Integer now = Integer.valueOf(list.get(i)[1].toString());
				if(pre==0.0){
					list.get(i)[2] = 1.0;
				}else{
					//String.format("%.2f", d)
					list.get(i)[2] = (double) ((now - pre) / pre);
				}
				
				pre = now;
			}
			
			String[] keys = new String[]{"y_m","orderNum","ratio"};
			List<Map<String, Object>> maps = new LinkedList<>();
			
			for(Object[] objects : list){
				maps.add(ArrayHandler.toMap(keys, objects));
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(maps);
		
		}catch (Exception e){
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	//季度
	@RequestMapping(value="/quarter_view")
	@ResponseBody
	public Json turnoverQuarterList(@DateTimeFormat(pattern = "yyyy-MM") Date start,
            @DateTimeFormat(pattern = "yyyy-MM")Date end){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try{
		
			StringBuilder sb = new StringBuilder("");
			//xxxx年季度
			sb.append("select concat(date_format(o1.fatuandate, '%Y'),'季度',FLOOR((date_format(o1.fatuandate, '%m')+2)/3)) y_q,IFNULL(count(*),0) order_num,1.0 ratio");
			sb.append(" from hy_order o1 where o1.type=1 and o1.status=3 and o1.fatuandate is not null");
			if(start==null){
				start = new Date();
			}
			if(end==null){
				end = new Date();
			}
			
			sb.append(" and DATE_FORMAT(o1.fatuandate,'%Y-%m') between '"+format.format(start)+"' and '"+format.format(end)+"'");
			sb.append(" group by concat(date_format(o1.fatuandate, '%Y'),'季度',FLOOR((date_format(o1.fatuandate, '%m')+2)/3))");
			
			List<Object[]> list = hyOrderService.statis(sb.toString());
			if(list == null || list.size() == 0) {
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(list);
				return json;
			}
			
			Integer pre = Integer.valueOf(list.get(0)[1].toString());
			for(int i=1;i<list.size();i++){
				Integer now = Integer.valueOf(list.get(i)[1].toString());
				if(pre.doubleValue()==0.0){
					list.get(i)[2] = 1.0;
				}else{
					list.get(i)[2] = (double) ((now - pre) / pre);
				}
			
				pre = now;
			}
			
			String[] keys = new String[]{"y_q","orderNum","ratio"};
			List<Map<String, Object>> maps = new LinkedList<>();
			
			for(Object[] objects : list){
				maps.add(ArrayHandler.toMap(keys, objects));
			}
		
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(maps);
		
		}catch (Exception e){
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		
		return json;
	}
	
	//统计新的订单
	//月度 
	@RequestMapping(value="/get_excel_month_view")
	@ResponseBody
	public Json turnoverMonthExcel(@DateTimeFormat(pattern = "yyyy-MM") Date start,
            @DateTimeFormat(pattern = "yyyy-MM")Date end){
		
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try{
	
			StringBuilder sb = new StringBuilder("");
			sb.append("select DATE_FORMAT(o1.fatuandate,'%Y-%m') y_m,IFNULL(count(*),0) order_num, 1.0 ratio");
			//线路订单 供应商通过 
			sb.append(" from hy_order o1 where o1.type=1 and o1.status=3 and o1.fatuandate is not null");
			if(start==null){
				start = new Date();
			}
			if(end==null){
				end = new Date();
			}
			
			sb.append(" and DATE_FORMAT(o1.fatuandate,'%Y-%m') between '"+format.format(start)+"' and '"+format.format(end)+"'");
			//按月groupby了
			sb.append(" group by DATE_FORMAT(o1.fatuandate,'%Y-%m')");
			
			List<Object[]> list = hyOrderService.statis(sb.toString());
			if(list == null || list.size() == 0) {
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(list);
				return json;
			}
			Integer pre = Integer.valueOf(list.get(0)[1].toString());
			for(int i=1;i<list.size();i++){
				Integer now = Integer.valueOf(list.get(i)[1].toString());
				if(pre==0.0){
					list.get(i)[2] = 1.0;
				}else{
					//String.format("%.2f", d)
					list.get(i)[2] = (double) ((now - pre) / pre);
				}
				
				pre = now;
			}
			
			String[] keys = new String[]{"y_m","orderNum","ratio"};
			List<Map<String, Object>> maps = new LinkedList<>();
			
			for(Object[] objects : list){
				maps.add(ArrayHandler.toMap(keys, objects));
			}
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(maps);
		
		}catch (Exception e){
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	//季度
	@RequestMapping(value="/get_excel_quarter_view")
	@ResponseBody
	public Json turnoverQuarterExcel(@DateTimeFormat(pattern = "yyyy-MM") Date start,
            @DateTimeFormat(pattern = "yyyy-MM")Date end){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try{
		
			StringBuilder sb = new StringBuilder("");
			//xxxx年季度
			sb.append("select concat(date_format(o1.fatuandate, '%Y'),'季度',FLOOR((date_format(o1.fatuandate, '%m')+2)/3)) y_q,IFNULL(count(*),0) order_num,1.0 ratio");
			sb.append(" from hy_order o1 where o1.type=1 and o1.status=3 and o1.fatuandate is not null");
			if(start==null){
				start = new Date();
			}
			if(end==null){
				end = new Date();
			}
			
			sb.append(" and DATE_FORMAT(o1.fatuandate,'%Y-%m') between '"+format.format(start)+"' and '"+format.format(end)+"'");
			sb.append(" group by concat(date_format(o1.fatuandate, '%Y'),'季度',FLOOR((date_format(o1.fatuandate, '%m')+2)/3))");
			
			List<Object[]> list = hyOrderService.statis(sb.toString());
			if(list == null || list.size() == 0) {
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(list);
				return json;
			}
			
			Integer pre = Integer.valueOf(list.get(0)[1].toString());
			for(int i=1;i<list.size();i++){
				Integer now = Integer.valueOf(list.get(i)[1].toString());
				if(pre.doubleValue()==0.0){
					list.get(i)[2] = 1.0;
				}else{
					list.get(i)[2] = (double) ((now - pre) / pre);
				}
			
				pre = now;
			}
			
			String[] keys = new String[]{"y_q","orderNum","ratio"};
			List<Map<String, Object>> maps = new LinkedList<>();
			
			for(Object[] objects : list){
				maps.add(ArrayHandler.toMap(keys, objects));
			}
		
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(maps);
		
		}catch (Exception e){
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		
		return json;
	}
}
