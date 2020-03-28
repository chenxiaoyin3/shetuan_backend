package com.hongyu.controller.wj;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.service.HyOrderService;

@Controller
@RequestMapping("/admin/homepage")
//@RequestMapping("/topstatics")
public class TopProductAndStoreStatics {
	
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	public HashMap<String, String> getDate(){	
		Date date = new Date();
		HashMap<String,String> map = new HashMap<String,String>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        int n1 = -1;// n为推迟的周数，-1上周，0本周，1下周，2下下周，依次类推
        int n2 = 0;
        if(cal1.get(Calendar.DAY_OF_WEEK) == 1){
        	n1 = -2;
        	n2 = -1;
        }
        
        cal1.add(Calendar.DATE, n1 * 7);
        cal1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        map.put("startDate", format.format(cal1.getTime()));
        
        Calendar cal2 = Calendar.getInstance();

        cal2.setTime(date);
        cal2.add(Calendar.DATE, n2*7);
        cal2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        map.put("endDate", format.format(cal2.getTime()));
        return map;
	}
	
	@RequestMapping("/topstatics/product")
	@ResponseBody
	public Json product(){
		Json json = new Json();
		try {
			HashMap<String, String> map = getDate();
			String startDate = map.get("startDate");
			String endDate = map.get("endDate");
			
			System.out.println(startDate);
			System.out.println(endDate);
			
			String sql1 = "select sum(item.number),item.type,l.name,o.status from hy_order_item item,hy_order o ,hy_group g,hy_line l "
					+ " where o.id = item.order_id and g.id = item.product_id and g.line = l.id  and item.type=1 and o.status=3"
                    + " and o.createtime>='"+startDate
                    +"' and o.createtime<='"+endDate
                    +"' group by l.id order by sum(item.number) desc,l.id desc limit 10";
			System.out.println(sql1);
			List<Object[]> list = hyOrderService.statis(sql1);
//			HashMap<String, String> product = new HashMap<>();
			List<String> product = new ArrayList<>();
			for(Object[] objects : list){
				String name = objects[2].toString();
//				String storeName = objects[4].toString();
//				String ans = name+"-"+storeName; 
				product.add(name);
			}
			
			String sql = "select sum(o.jiusuan_money),o.store_id,s.store_name from hy_order o,hy_store s"
					+ " where o.store_id = s.id " 
					+ " and o.createtime>='" + startDate 
					+ "' and o.createtime<='"+ endDate
					+ "' and o.type=1 and o.status=3 group by o.store_id order by sum(o.jiusuan_money) desc,o.store_id desc limit 10";

			System.out.println(sql);
			List<Object[]> list2 = hyOrderService.statis(sql);
			// HashMap<String, String> product = new HashMap<>();
			List<String> stores = new ArrayList<>();
			for (Object[] objects : list2) {
				String name = objects[2].toString();
				stores.add(name);
			}
			
			HashMap<String, Object> res = new HashMap<String,Object>();
			res.put("product",product);
			res.put("store", stores);
			
			json.setObj(res);
			json.setMsg("查询成功");
			json.setSuccess(true);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("查询失败");
			json.setSuccess(false);
		}
		return json;
	}
	
//	@RequestMapping("/store")
//	@ResponseBody
//	public Json store(){
//		Json json = new Json();
//		try {
//			HashMap<String, String> map = getDate();
//			String startDate = map.get("startDate");
//			String endDate = map.get("endDate");
//			
//			System.out.println(startDate);
//			System.out.println(endDate);
//			
//			String sql = "select sum(o.jiusuan_money),o.store_id,o.id,s.store_name from hy_order o,hy_store s" 
//						+ " where o.store_id = s.id "
//						+ " and o.createtime>='"+startDate
//						+ "' and o.createtime<='"+endDate
//						+ "' and o.type=1 and o.status=3 group by o.store_id order by sum(o.jiusuan_money) desc limit 10";
//
//			System.out.println(sql);
//			List<Object[]> list = hyOrderService.statis(sql);
////			HashMap<String, String> product = new HashMap<>();
//			List<String> product = new ArrayList<>();
//			for(Object[] objects : list){
//				String name = objects[3].toString();
//				product.add(name);
//			}
//			json.setObj(product);
//			json.setMsg("查询成功");
//			json.setSuccess(true);
//			
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			json.setMsg("查询失败");
//			json.setSuccess(false);
//		}
//		return json;
//		
//		
//	}
//	
	
	

}
