package com.hongyu.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Json;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyOrderService;
import com.hongyu.util.ArrayHandler;
import com.hongyu.util.ExcelTitleUtils;
import com.hongyu.util.OrderTurnoverBean;

@Controller
@RequestMapping("admin/provider_turnover/")
public class ProviderTurnoverController {
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	/**
	 * 集团各供应商销售额排名
	 */
	@RequestMapping("sort_list/view")
	@ResponseBody
	public Json sortList(@RequestParam(value="startTime",required=false)Date startTime,
			@RequestParam(value="endTime",required=false)Date endTime,
			HttpSession session){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String username = (String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			if(admin==null){
				throw new Exception("用户名不存在");
			}
			Department department = admin.getDepartment();
			StringBuilder sb = new StringBuilder("select sum(hy_order.jiusuan_money) turnover,"
					+ "sum(hy_order.people) customerNum,hy_admin.name providerName"
					+ " from hy_order,hy_admin where hy_order.type=1 and hy_order.supplier=hy_admin.username");

			if(startTime!=null){
				String startStr = format.format(startTime);
				sb.append(" and DATE_FORMAT(DATE_ADD(hy_order.fatuandate,interval hy_order.tianshu day),'%Y-%m-%d')>='"+startStr+"'");
			}
			if(endTime!=null){
				String endStr = format.format(endTime);
				sb.append(" and DATE_FORMAT(DATE_ADD(hy_order.fatuandate,interval hy_order.tianshu day),'%Y-%m-%d')<='"+endStr+"'");
			}else{
				sb.append(" and DATE_ADD(hy_order.fatuandate,interval hy_order.tianshu day)<=NOW()");
			}
			sb.append(" group by hy_order.supplier");
			
			sb.append(" order by turnover desc");
			
			String jpql = sb.toString();
			List<Object[]> list = hyOrderService.statis(jpql);
			if(list==null || list.isEmpty()){
				json.setSuccess(true);
				json.setMsg("查询成功");  
				json.setObj(new ArrayList<>());
				return json;
			}
			
			List<Map<String, Object>> maps = new LinkedList<>();
			String[] keys = {"turnover","customerNum","providerName"};
			
			Object[] tmp = {BigDecimal.valueOf(0),BigDecimal.valueOf(0),BigInteger.valueOf(0)};
			for(Object[] objects:list){
				tmp[0]=((BigDecimal)tmp[0]).add((BigDecimal)objects[0]);
				tmp[1]=((BigDecimal)tmp[1]).add((BigDecimal)objects[1]);
				tmp[2]=((BigInteger)tmp[2]).add(BigInteger.valueOf(1));
				maps.add(ArrayHandler.toMap(keys,objects));
			}
			
			maps.add(ArrayHandler.toMap(keys, tmp));
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(maps);
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	
	/**
	 * 导出excel
	 * 集团各供应商销售额排名
	 */
	@RequestMapping("sort_list/excel")
	public String sortListExcel(@RequestParam(value="startTime",required=false)Date startTime,
			@RequestParam(value="endTime",required=false)Date endTime,
			HttpSession session,HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			//获取json
			json = sortList(startTime, endTime, session);
			if(json.isSuccess()==false){
				return null;
			}
			List<Map<String, Object>> maps = (List<Map<String, Object>>)json.getObj();
			List<OrderTurnoverBean> results = new LinkedList<>();
			for(Map<String, Object> map:maps){
				results.add(new OrderTurnoverBean(map));
			}
			String fileName = "供应商营业额排名表.xls";  // Excel文件名
			String tableTitle = ExcelTitleUtils.doOrderTurnover(startTime, endTime, 3, " 集团各供应商销售额排名");   // Excel表标题
			String configFile = "providerTurnoverSortList.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return null;
	}

}
