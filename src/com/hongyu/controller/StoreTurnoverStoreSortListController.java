package com.hongyu.controller;

import com.hongyu.CommonAttributes;
import com.hongyu.Json;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyOrderService;
import com.hongyu.util.ArrayHandler;
import com.hongyu.util.ExcelTitleUtils;
import com.hongyu.util.OrderTurnoverBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("admin/store_turnover_sort_list/")
public class StoreTurnoverStoreSortListController {
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	
	/**
	 * 给门店经理看，分员工
	 */
	@RequestMapping("store_manager/list/view")
	@ResponseBody
	public Json storeManagerList(@RequestParam(value="startTime",required=false)Date startTime,
			@RequestParam(value="endTime",required=false)Date endTime,
			Integer condition,
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
			StringBuilder sb = new StringBuilder("select hy_admin.name staffName,count(*) orderNum,"
					+ "sum(hy_order.people) customerNum,sum(hy_order.jiusuan_money) turnover"
					+ " from hy_order,hy_admin"
					+ " where hy_order.operator_id=hy_admin.username and hy_order.type=1 and hy_order.status=3");
			if(department.getId()!=1){
				sb.append(" and hy_admin.department="+String.valueOf(department.getId()));
			}
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
			sb.append(" group by hy_order.operator_id");
			if(condition==null || condition==0){
				condition=3;
			}
			switch (condition) {
			case 1:
				sb.append(" order by orderNum desc");
				break;
			case 2:
				sb.append(" order by customerNum desc");
				break;
			default:
				sb.append(" order by turnover desc");
				break;
			}
			
			String jpql = sb.toString();
			List<Object[]> list = hyOrderService.statis(jpql);
			if(list==null || list.isEmpty()){
				json.setSuccess(true);
				json.setMsg("查询成功");  
				json.setObj(new ArrayList<>());
				return json;
			}
			
			List<Map<String, Object>> maps = new LinkedList<>();
			String[] keys = {"staffName","orderNum","customerNum","turnover"};
			
			Object[] tmp = {BigInteger.valueOf(0),BigInteger.valueOf(0),BigDecimal.valueOf(0),BigDecimal.valueOf(0)};
			for(Object[] objects:list){
				tmp[0]=((BigInteger)tmp[0]).add(BigInteger.valueOf(1));
				tmp[1]=((BigInteger)tmp[1]).add((BigInteger)objects[1]);
				tmp[2]=((BigDecimal)tmp[2]).add((BigDecimal)objects[2]);
				tmp[3]=((BigDecimal)tmp[3]).add((BigDecimal)objects[3]);
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
	 * 给门店经理看，分员工
	 */
	@RequestMapping("store_manager/list/excel")
	public String storeManagerListExcel(@RequestParam(value="startTime",required=false)Date startTime,
			@RequestParam(value="endTime",required=false)Date endTime,
			Integer condition,
			HttpSession session,HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			
			json = storeManagerList(startTime, endTime, condition, session);
			if(json.isSuccess()==false){
				return null;
			}
			List<Map<String, Object>> maps = (List<Map<String, Object>>)json.getObj();
			List<OrderTurnoverBean> results = new LinkedList<>();
			for(Map<String, Object> map:maps){
				results.add(new OrderTurnoverBean(map));
			}
			String fileName = "门店营业额统计表（给门店经理）.xls";  // Excel文件名
			String tableTitle = ExcelTitleUtils.doOrderTurnover(startTime, endTime, condition, " 门店营业额统计表（分员工）");   // Excel表标题
			String configFile = "storeTurnoverStoreManager.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	/**
	 * 给连锁发展员工，分门店
	 */
	@RequestMapping("lsfz_staff/list/view")
	@ResponseBody
	public Json lsfzStaffList(@RequestParam(value="startTime",required=false)Date startTime,
			@RequestParam(value="endTime",required=false)Date endTime,
			Integer condition,
			@RequestParam(value="username",required=false)String username,
			HttpSession session){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if(username==null){
				username = (String)session.getAttribute(CommonAttributes.Principal);
			}
			HyAdmin admin = hyAdminService.find(username);
			if(admin==null){
				throw new Exception("用户名不存在");
			}
			Department department = admin.getDepartment();
			StringBuilder sb = new StringBuilder("select hy_store.store_name storeName,count(*) orderNum,"
					+ "sum(hy_order.people) customerNum,sum(hy_order.jiusuan_money) turnover,hy_store.id storeId"
					+ " from hy_order,hy_store"
					+ " where hy_order.store_id=hy_store.id and hy_order.type=1 and hy_order.status=3");
			if(department.getId()!=1){
				sb.append(" and hy_store.store_adder='"+username+"'");
			}
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
			sb.append(" group by hy_store.id");
			if(condition==null || condition==0){
				condition=3;
			}
			switch (condition) {
			case 1:
				sb.append(" order by orderNum desc");
				break;
			case 2:
				sb.append(" order by customerNum desc");
				break;
			default:
				sb.append(" order by turnover desc");
				break;
			}
			
			String jpql = sb.toString();
			List<Object[]> list = hyOrderService.statis(jpql);
			if(list==null || list.isEmpty()){
				json.setSuccess(true);
				json.setMsg("查询成功");  
				json.setObj(new ArrayList<>());
				return json;
			}
			
			List<Map<String, Object>> maps = new LinkedList<>();
			String[] keys = {"storeName","orderNum","customerNum","turnover","storeId"};
			
			Object[] tmp = {BigInteger.valueOf(0),BigInteger.valueOf(0),BigDecimal.valueOf(0),BigDecimal.valueOf(0),BigInteger.valueOf(0)};
			for(Object[] objects:list){
				tmp[0]=((BigInteger)tmp[0]).add(BigInteger.valueOf(1));
				tmp[1]=((BigInteger)tmp[1]).add((BigInteger)objects[1]);
				tmp[2]=((BigDecimal)tmp[2]).add((BigDecimal)objects[2]);
				tmp[3]=((BigDecimal)tmp[3]).add((BigDecimal)objects[3]);
				tmp[4]=((BigInteger)tmp[4]).add(BigInteger.valueOf(1));
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
	 * 给连锁发展员工，分门店
	 */
	@RequestMapping("lsfz_staff/list/excel")
	public String lsfzStaffListExcel(@RequestParam(value="startTime",required=false)Date startTime,
			@RequestParam(value="endTime",required=false)Date endTime,
			Integer condition,
			@RequestParam(value="username",required=false)String username,
			HttpSession session,HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			//获取json
			json = lsfzStaffList(startTime, endTime, condition,username, session);
			if(json.isSuccess()==false){
				return null;
			}
			List<Map<String, Object>> maps = (List<Map<String, Object>>)json.getObj();
			List<OrderTurnoverBean> results = new LinkedList<>();
			for(Map<String, Object> map:maps){
				results.add(new OrderTurnoverBean(map));
			}
			String fileName = "门店营业额统计表（给连锁发展员工）.xls";  // Excel文件名
			String tableTitle = ExcelTitleUtils.doOrderTurnover(startTime, endTime, condition, " 门店营业额统计表（分门店）");   // Excel表标题
			String configFile = "storeTurnoverLsfzStaff.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 给连锁发展员工，分门店
	 */
	@RequestMapping("store_finance/list/view")
	@ResponseBody
	public Json storeFinanceList(@RequestParam(value="startTime",required=false)Date startTime,
							  @RequestParam(value="endTime",required=false)Date endTime,
							  Integer condition,
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
			StringBuilder sb = new StringBuilder("select hy_store.store_name storeName,count(*) orderNum,"
					+ "sum(hy_order.people) customerNum,sum(hy_order.jiusuan_money) turnover,hy_store.id storeId"
					+ " from hy_order,hy_store"
					+ " where hy_order.store_id=hy_store.id and hy_order.type=1 and hy_order.status=3");
			if(startTime!=null){
				String startStr = format.format(startTime);
				sb.append(" and DATE_FORMAT(hy_order.fatuandate,'%Y-%m-%d')>='"+startStr+"'");
			}
			if(endTime!=null){
				String endStr = format.format(endTime);
				sb.append(" and DATE_FORMAT(hy_order.fatuandate,'%Y-%m-%d')<='"+endStr+"'");
			}else{
				sb.append(" and DATE_FORMAT(hy_order.fatuandate,'%Y-%m-%d')<=NOW()");
			}
			sb.append(" group by hy_store.id");
			if(condition==null || condition==0){
				condition=3;
			}
			switch (condition) {
				case 1:
					sb.append(" order by orderNum desc");
					break;
				case 2:
					sb.append(" order by customerNum desc");
					break;
				default:
					sb.append(" order by turnover desc");
					break;
			}

			String jpql = sb.toString();
			List<Object[]> list = hyOrderService.statis(jpql);
			if(list==null || list.isEmpty()){
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(new ArrayList<>());
				return json;
			}

			List<Map<String, Object>> maps = new LinkedList<>();
			String[] keys = {"storeName","orderNum","customerNum","turnover","storeId"};

			Object[] tmp = {BigInteger.valueOf(0),BigInteger.valueOf(0),BigDecimal.valueOf(0),BigDecimal.valueOf(0),BigInteger.valueOf(0)};
			for(Object[] objects:list){
				tmp[0]=((BigInteger)tmp[0]).add(BigInteger.valueOf(1));
				tmp[1]=((BigInteger)tmp[1]).add((BigInteger)objects[1]);
				tmp[2]=((BigDecimal)tmp[2]).add((BigDecimal)objects[2]);
				tmp[3]=((BigDecimal)tmp[3]).add((BigDecimal)objects[3]);
				tmp[4]=((BigInteger)tmp[4]).add(BigInteger.valueOf(1));
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
	 * 给连锁发展员工，分门店
	 */
	@RequestMapping("store_finance/list/excel")
	public String storeFinanceListExcel(@RequestParam(value="startTime",required=false)Date startTime,
									 @RequestParam(value="endTime",required=false)Date endTime,
									 Integer condition,
									 HttpSession session,HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			//获取json
			json = storeFinanceList(startTime, endTime, condition, session);
			if(json.isSuccess()==false){
				return null;
			}
			List<Map<String, Object>> maps = (List<Map<String, Object>>)json.getObj();
			List<OrderTurnoverBean> results = new LinkedList<>();
			for(Map<String, Object> map:maps){
				results.add(new OrderTurnoverBean(map));
			}
			String fileName = "门店营业额统计表（给总公司财务）.xls";  // Excel文件名
			String tableTitle = ExcelTitleUtils.doOrderTurnover(startTime, endTime, condition, " 门店营业额统计表（给总公司财务）");   // Excel表标题
			String configFile = "storeTurnoverFinance.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 门店内供应商排名
	 */
	@RequestMapping("store_provider/list/view")
	@ResponseBody
	public Json storeProviderList(@RequestParam(value="startTime",required=false)Date startTime,
						 @RequestParam(value="endTime",required=false)Date endTime,
						 Long storeId,
						 Integer condition,
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
					+ "sum(hy_order.people) customerNum,hy_admin.name providerName,count(*) orderNum"
					+ " from hy_order,hy_admin where hy_order.type=1 and hy_order.status=3 and hy_order.supplier=hy_admin.username");

			if(storeId != null){
				sb.append( " and hy_order.store_id="+storeId);
			}
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
			sb.append(" group by hy_order.id");

			switch (condition) {
				case 1:
					sb.append(" order by orderNum desc");
					break;
				case 2:
					sb.append(" order by customerNum desc");
					break;
				default:
					sb.append(" order by turnover desc");
					break;
			}

			String jpql = sb.toString();
			List<Object[]> list = hyOrderService.statis(jpql);
			if(list==null || list.isEmpty()){
				json.setSuccess(true);
				json.setMsg("查询成功");
				json.setObj(new ArrayList<>());
				return json;
			}

			List<Map<String, Object>> maps = new LinkedList<>();
			String[] keys = {"turnover","customerNum","providerName","orderNum"};

			Object[] tmp = {BigDecimal.valueOf(0),BigDecimal.valueOf(0),BigInteger.valueOf(0),BigInteger.valueOf(0)};
			for(Object[] objects:list){
				tmp[0]=((BigDecimal)tmp[0]).add((BigDecimal)objects[0]);
				tmp[1]=((BigDecimal)tmp[1]).add((BigDecimal)objects[1]);
				tmp[2]=((BigInteger)tmp[2]).add(BigInteger.valueOf(1));
				tmp[3]=((BigInteger)tmp[3]).add((BigInteger)objects[3]);
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
	@RequestMapping("store_provider/list/excel")
	public String storeProviderExcel(@RequestParam(value="startTime",required=false)Date startTime,
								@RequestParam(value="endTime",required=false)Date endTime,
								Integer condition,
								Long storeId,
								HttpSession session,HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			//获取json
			json = storeProviderList(startTime, endTime,storeId,condition, session);
			if(json.isSuccess()==false){
				return null;
			}
			List<Map<String, Object>> maps = (List<Map<String, Object>>)json.getObj();
			List<OrderTurnoverBean> results = new LinkedList<>();
			for(Map<String, Object> map:maps){
				results.add(new OrderTurnoverBean(map));
			}
			String fileName = "门店各供应商营业额排名表.xls";  // Excel文件名
			String tableTitle = ExcelTitleUtils.doOrderTurnover(startTime, endTime, 3, " 门店各供应商销售额排名");   // Excel表标题
			String configFile = "storeProviderTurnoverSortList.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);


		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	/**
	 * 给连锁发展部经理，分连锁发展员工
	 */
	@RequestMapping("lsfz_manager/list/view")
	@ResponseBody
	public Json lsfzManagerList(@RequestParam(value="startTime",required=false)Date startTime,
			@RequestParam(value="endTime",required=false)Date endTime,
			Integer condition,
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
			StringBuilder sb = new StringBuilder("select hy_admin.name staffName,count(distinct hy_store.id) storeNum,"
					+ "count(*) orderNum,sum(hy_order.people) customerNum,sum(hy_order.jiusuan_money) turnover,hy_admin.username username"
					+ " from hy_order,hy_store,hy_admin"
					+ " where hy_order.status=3 and hy_order.type=1 and hy_order.store_id=hy_store.id and hy_admin.username=hy_store.store_adder");
			if(department.getId()!=1){
				sb.append(" and hy_admin.department="+department.getId());
			}
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
			sb.append(" group by hy_store.store_adder");
			if(condition==null || condition==0){
				condition=3;
			}
			switch (condition) {
			case 1:
				sb.append(" order by orderNum desc");
				break;
			case 2:
				sb.append(" order by customerNum desc");
				break;
			default:
				sb.append(" order by turnover desc");
				break;
			}
			
			String jpql = sb.toString();
			List<Object[]> list = hyOrderService.statis(jpql);
			if(list==null || list.isEmpty()){
				json.setSuccess(true);
				json.setMsg("查询成功");  
				json.setObj(new ArrayList<>());
				return json;
			}
			
			List<Map<String, Object>> maps = new LinkedList<>();
			String[] keys = {"staffName","storeNum","orderNum","customerNum","turnover","username"};
			
			Object[] tmp = {BigInteger.valueOf(0),BigInteger.valueOf(0),BigInteger.valueOf(0),
					BigDecimal.valueOf(0),BigDecimal.valueOf(0),BigInteger.valueOf(0),};
			for(Object[] objects:list){
				tmp[0]=((BigInteger)tmp[0]).add(BigInteger.valueOf(1));
				tmp[1]=((BigInteger)tmp[1]).add((BigInteger)objects[1]);
				tmp[2]=((BigInteger)tmp[2]).add((BigInteger)objects[2]);
				tmp[3]=((BigDecimal)tmp[3]).add((BigDecimal)objects[3]);
				tmp[4]=((BigDecimal)tmp[4]).add((BigDecimal)objects[4]);
				tmp[5]=((BigInteger)tmp[5]).add(BigInteger.valueOf(1));
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
	 * 给连锁发展部经理，分连锁发展员工
	 */
	@RequestMapping("lsfz_manager/list/excel")
	public String lsfzManagerListExcel(@RequestParam(value="startTime",required=false)Date startTime,
			@RequestParam(value="endTime",required=false)Date endTime,
			Integer condition,
			HttpSession session,HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			//获取json
			json = lsfzManagerList(startTime, endTime, condition, session);
			if(json.isSuccess()==false){
				return null;
			}
			List<Map<String, Object>> maps = (List<Map<String, Object>>)json.getObj();
			List<OrderTurnoverBean> results = new LinkedList<>();
			for(Map<String, Object> map:maps){
				results.add(new OrderTurnoverBean(map));
			}
			String fileName = "门店营业额统计表（给连锁发展部经理）.xls";  // Excel文件名
			String tableTitle = ExcelTitleUtils.doOrderTurnover(startTime, endTime, condition, " 门店营业额统计表（分连锁发展员工）");   // Excel表标题
			String configFile = "storeTurnoverLsfzManager.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	/**
	 * 给渠道销售，按分公司
	 */
	@RequestMapping("qdxs/list/view")
	@ResponseBody
	public Json qdxsList(@RequestParam(value="startTime",required=false)Date startTime,
			@RequestParam(value="endTime",required=false)Date endTime,
			Integer condition,
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
			StringBuilder sb = new StringBuilder("select hd2.name branchName,count(distinct hy_store.id) storeNum,"
					+ "count(*) orderNum,sum(hy_order.people) customerNum,sum(hy_order.jiusuan_money) turnover"
					+ " from hy_order,hy_store,hy_department hd1,hy_department hd2"
					+ " where hy_order.type=1 and hy_order.status=3 and hy_order.store_id=hy_store.id and hy_store.department_id=hd1.id"
					+ " and hd2.model='分公司' and hd1.tree_path like CONCAT('%,',hd2.id,',%')");

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
			sb.append(" group by hd2.id");
			if(condition==null || condition==0){
				condition=3;
			}
			switch (condition) {
			case 1:
				sb.append(" order by orderNum desc");
				break;
			case 2:
				sb.append(" order by customerNum desc");
				break;
			default:
				sb.append(" order by turnover desc");
				break;
			}
			
			String jpql = sb.toString();
			List<Object[]> list = hyOrderService.statis(jpql);
			if(list==null || list.isEmpty()){
				json.setSuccess(true);
				json.setMsg("查询成功");  
				json.setObj(new ArrayList<>());
				return json;
			}
			
			List<Map<String, Object>> maps = new LinkedList<>();
			String[] keys = {"branchName","storeNum","orderNum","customerNum","turnover"};
			
			Object[] tmp = {BigInteger.valueOf(0),BigInteger.valueOf(0),BigInteger.valueOf(0),BigDecimal.valueOf(0),BigDecimal.valueOf(0)};
			for(Object[] objects:list){
				tmp[0]=((BigInteger)tmp[0]).add(BigInteger.valueOf(1));
				tmp[1]=((BigInteger)tmp[1]).add((BigInteger)objects[1]);
				tmp[2]=((BigInteger)tmp[2]).add((BigInteger)objects[2]);
				tmp[3]=((BigDecimal)tmp[3]).add((BigDecimal)objects[3]);
				tmp[4]=((BigDecimal)tmp[4]).add((BigDecimal)objects[4]);
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
	 * 给渠道销售，按分公司
	 */
	@RequestMapping("qdxs/list/excel")
	public String qdxsListExcel(@RequestParam(value="startTime",required=false)Date startTime,
			@RequestParam(value="endTime",required=false)Date endTime,
			Integer condition,
			HttpSession session,HttpServletRequest request, HttpServletResponse response){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			//获取json
			json = qdxsList(startTime, endTime, condition, session);
			if(json.isSuccess()==false){
				return null;
			}
			List<Map<String, Object>> maps = (List<Map<String, Object>>)json.getObj();
			List<OrderTurnoverBean> results = new LinkedList<>();
			for(Map<String, Object> map:maps){
				results.add(new OrderTurnoverBean(map));
			}
			String fileName = "门店营业额统计表（给渠道销售）.xls";  // Excel文件名
			String tableTitle = ExcelTitleUtils.doOrderTurnover(startTime, endTime, condition, " 门店营业额统计表（按分公司）");   // Excel表标题
			String configFile = "storeTurnoverQdxs.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	/**
	 * 集团各门店营业额排名
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
					+ "sum(hy_order.people) customerNum,hy_store.store_name storeName,max(hd2.name) branchName"
					+ " from hy_order,hy_store,hy_department hd1,hy_department hd2"
					+ " where hy_order.type=1 and hy_order.status=3 and hy_order.store_id=hy_store.id and hy_store.department_id=hd1.id"
					+ " and hd2.model='分公司' and hd1.tree_path like CONCAT('%,',hd2.id,',%')");

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
			sb.append(" group by hy_store.id");
			
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
			String[] keys = {"turnover","customerNum","storeName","branchName"};
			
			Object[] tmp = {BigDecimal.valueOf(0),BigDecimal.valueOf(0),BigInteger.valueOf(0),""};
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
	 * 集团各门店营业额排名
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
			String fileName = "门店营业额排名表.xls";  // Excel文件名
			String tableTitle = ExcelTitleUtils.doOrderTurnover(startTime, endTime, 3, " 集团各门店营业额排名");   // Excel表标题
			String configFile = "storeTurnoverSortList.xml"; // 配置文件
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
