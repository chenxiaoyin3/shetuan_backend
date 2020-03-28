package com.hongyu.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HySupplier;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.StoreService;
import com.hongyu.util.ArrayHandler;
import com.hongyu.util.ExcelTitleUtils;
import com.hongyu.util.OrderTurnoverBean;

@Controller
@RequestMapping("admin/vip_line_order_statis")
public class VIPLineOrderStatisController {
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@RequestMapping("list_by_month")
	@ResponseBody
	public Json listByMonth(@DateTimeFormat(pattern="yyyy-MM")Date start,@DateTimeFormat(pattern="yyyy-MM")Date end,String store,String provider) {
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		if(start==null) {
			start = new Date();
		}
		if(end==null) {
			end = new Date();
		}

		String ym = format.format(start)+" 至 "+format.format(end);
		try {
			StringBuilder sb = new StringBuilder("select '"+ym+"' y_m,"
					+ "d2.name branch,s1.store_name store,sp1.supplier_name provider,sum(o1.people) people,sum(o1.store_fan_li) money");
			sb.append(" from hy_order o1,hy_group g1,hy_line l1,hy_supplier sp1,hy_store s1,hy_department d1,hy_department d2");
			sb.append(" where o1.store_fan_li>0 and o1.status=3 and o1.type=1");

			sb.append(" and DATE_FORMAT(DATE_ADD(o1.fatuandate,interval o1.tianshu day),'%Y-%m') between '"+format.format(start)+"' and '"+format.format(end)+"'");
			sb.append(" and o1.group_id=g1.id and g1.line=l1.id and l1.supplier=sp1.id and o1.store_id=s1.id and s1.department_id=d1.id and d2.model='总公司' and d1.tree_path like CONCAT('%,',d2.id,',%')");
			if(store!=null) {
				sb.append(" and s1.store_name like '%"+store+"%'");
			}
			if(provider!=null) {
				sb.append(" and sp1.supplier_name like '%"+provider+"%'");
			}
			sb.append(" group by d2.id,s1.id,sp1.id");
			sb.append(" order by people desc");
			
			List<Object[]> list = hyOrderService.statis(sb.toString());
			Object[] objs = new Object[]{BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO, BigDecimal.ZERO,BigDecimal.ZERO};
			for(Object[] objs1:list){
				objs[0] = ((BigInteger)objs[0]).add(BigInteger.valueOf(1));
				objs[1] = ((BigInteger)objs[1]).add(BigInteger.valueOf(1));
				objs[2] = ((BigInteger)objs[2]).add(BigInteger.valueOf(1));
				objs[3] = ((BigInteger)objs[3]).add(BigInteger.valueOf(1));
				objs[4] = ((BigDecimal)objs[4]).add((BigDecimal)objs1[4]);
				objs[5] = ((BigDecimal)objs[5]).add((BigDecimal)objs1[5]);
			}

			list.add(objs);

			String[] keys = new String[] {"y_m","branch","store","provider","people","money"};
			List<Map<String, Object>> maps = new ArrayList<>();
			
			for(Object[] objects:list) {
				maps.add(ArrayHandler.toMap(keys, objects));
			}
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
	
	public static class ListByMonthBean{
		private Map<String, Object> maps;
		public ListByMonthBean(Map<String, Object> maps) {
			this.maps = maps;
		}
		public Object getY_m() {
			return maps.get("y_m");
		}
		public Object getBranch() {
			return maps.get("branch");
		}
		public Object getStore() {
			return maps.get("store");
		}
		public Object getProvider() {
			return maps.get("provider");
		}
		public Object getPeople() {
			return maps.get("people");
		}
		public Object getMoney() {
			return maps.get("money");
		}
		public Object getPr(){return maps.get("pr");}
		public Object getMr(){return maps.get("mr");}
		public Object getFanli(){return maps.get("fanli");}

	}
	/**
	 * 导出excel
	 * 给门店经理看，分员工
	 */
	@RequestMapping("list_by_month/excel")
	public String listByMonthExcel(@DateTimeFormat(pattern="yyyy-MM")Date start,@DateTimeFormat(pattern="yyyy-MM")Date end,String store,String provider,
			HttpSession session,HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try {
			json = listByMonth(start, end, store, provider);
			if(json.isSuccess()==false){
				return null;
			}
			List<Map<String, Object>> maps = (List<Map<String, Object>>)json.getObj();
			
			List<ListByMonthBean> results = new ArrayList<>();
			for(Map<String, Object> map:maps){
				results.add(new ListByMonthBean(map));
			}
			
			String fileName = "VIP政策门店统计表.xls";  // Excel文件名
			String tableTitle = "VIP政策门店统计表";   // Excel表标题
			String configFile = "vipLineOrderStatis.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@RequestMapping("people_list_by_month")
	@ResponseBody
	public Json peopleListByMonth(@DateTimeFormat(pattern="yyyy-MM")Date start,@DateTimeFormat(pattern="yyyy-MM")Date end) {
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		String ym = format.format(start)+" 至 "+format.format(end);
		try {
			StringBuilder sb = new StringBuilder("select '"+ym+"' y_m,"
				+ "sp1.supplier_name provider,sum(o1.people) people,0.0 pr,"
				+ "sum(o1.jiusuan_money+o1.tip-o1.discounted_price-o1.store_fan_li) money,0.0 mr,sum(o1.store_fan_li) fanli");
			sb.append(" from hy_order o1,hy_group g1,hy_line l1,hy_supplier sp1");
			sb.append(" where o1.store_fan_li>0 and o1.status=3 and o1.type=1");
			if(start==null) {
				start = new Date();
			}
			if(end==null) {
				end = new Date();
			}
			sb.append(" and DATE_FORMAT(DATE_ADD(o1.fatuandate,interval o1.tianshu day),'%Y-%m') between '"+format.format(start)+"' and '"+format.format(end)+"'");
			sb.append(" and o1.group_id=g1.id and g1.line=l1.id and l1.supplier=sp1.id");

			sb.append(" group by sp1.id");
			sb.append(" order by people desc");

			List<Object[]> list = hyOrderService.statis(sb.toString());
			Object[] objs = new Object[]{BigInteger.ZERO,BigInteger.ZERO,BigDecimal.ZERO,BigDecimal.valueOf(1), BigDecimal.ZERO,BigDecimal.valueOf(1),BigDecimal.ZERO};
			for(Object[] objs1:list){
				objs[0] = ((BigInteger)objs[0]).add(BigInteger.valueOf(1));
				objs[1] = ((BigInteger)objs[1]).add(BigInteger.valueOf(1));

				objs[2] = ((BigDecimal)objs[2]).add((BigDecimal)objs1[2]);
				objs[4] = ((BigDecimal)objs[4]).add((BigDecimal)objs1[4]);
				objs[6] = ((BigDecimal)objs[6]).add((BigDecimal)objs1[6]);
			}

			for(Object[] objs1:list){
				objs1[3] = ((BigDecimal)objs1[2]).divide((BigDecimal)objs[2],2, RoundingMode.HALF_UP);
				objs1[5] = ((BigDecimal)objs1[4]).divide((BigDecimal)objs[4],2, RoundingMode.HALF_UP);
			}

			list.add(objs);


			String[] keys = new String[] {"y_m","provider","people","pr","money","mr","fanli"};
			List<Map<String, Object>> maps = new ArrayList<>();

			for(Object[] objects:list) {
				maps.add(ArrayHandler.toMap(keys, objects));
			}
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
	@RequestMapping("people_list_by_month/excel")
	public String peopleListByMonthExcel(@DateTimeFormat(pattern="yyyy-MM")Date start,@DateTimeFormat(pattern="yyyy-MM")Date end,
	                               HttpSession session,HttpServletRequest request, HttpServletResponse response) {
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try {
			json = peopleListByMonth(start, end);
			if(json.isSuccess()==false){
				return null;
			}
			List<Map<String, Object>> maps = (List<Map<String, Object>>)json.getObj();

			List<ListByMonthBean> results = new ArrayList<>();
			for(Map<String, Object> map:maps){
				results.add(new ListByMonthBean(map));
			}

			String fileName = "VIP政策收客统计表.xls";  // Excel文件名
			String tableTitle = "VIP政策收客统计表";   // Excel表标题
			String configFile = "vipLineOrderPeopleStatis.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, results, fileName, tableTitle, configFile);


		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;
	
	//供应商列表
	@RequestMapping("providerList")
	@ResponseBody
	public Json providerList(String supplierName,HttpSession session)
	{
		Json json=new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("operator", admin));
			if(supplierName!=null) {
				filters.add(Filter.like("supplierName", supplierName));
			}
			List<HySupplier> hySuppliers=hySupplierService.findList(null,filters,null);
			List<Map<String, Object>> list = new ArrayList<>();
			for(HySupplier hySupplier:hySuppliers) {
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("providerId", hySupplier.getId());
				map.put("providerName", hySupplier.getSupplierName());
				list.add(map);
			}
		    json.setSuccess(true);
		    json.setObj(list);;
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	@RequestMapping("storeList")
	@ResponseBody
	public Json getStoreList(HttpSession session){
		Json json = new Json();
		try {
			//获取用户
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			String jpql = "select id,store_name from hy_store"; 
			List<Object[]> stores = storeService.statis(jpql);
			List<HashMap<String, Object>> result = new ArrayList<>();
			for(Object[] tmp:stores){
				BigInteger storeId = (BigInteger)tmp[0];
				if(admin.getRole().getName().contains("门店员工") || admin.getRole().getName().contains("门店经理")){
					if(admin.getDepartment().getStore().getId()!=storeId.longValue())
						continue;
				}
				HashMap<String, Object> hMap = new HashMap<>();
				hMap.put("id", (BigInteger)tmp[0]);
				hMap.put("storeName", (String)tmp[1]);
				result.add(hMap);
			}

			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败："+e.getMessage());
		}
		return json;
	}

}
