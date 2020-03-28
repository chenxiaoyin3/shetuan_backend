package com.hongyu.controller;

import com.hongyu.Json;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyValidProductService;
import com.hongyu.util.ArrayHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/store_line_order_statis_turnover")
public class StoreLineOrderStatisTurnoverController {

	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;

	@Autowired
	HyValidProductService hyValidProductService;

	@RequestMapping("/turnover/month_list/view")
	@ResponseBody
	public Json turnoverMonthList(@DateTimeFormat(pattern = "yyyy-MM") Date start,
	                         @DateTimeFormat(pattern = "yyyy-MM")Date end){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try{

			StringBuilder sb = new StringBuilder("");
			sb.append("select DATE_FORMAT(o1.fatuandate,'%Y-%m') y_m,IFNULL(sum(o1.jiusuan_money+o1.tip-o1.discounted_price-o1.store_fan_li),0) money,1.0 ratio");
			sb.append(" from hy_order o1 where o1.type=1 and o1.status=3 and o1.fatuandate is not null");
			if(start==null){
				start = new Date();
			}
			if(end==null){
				end = new Date();
			}

			sb.append(" and DATE_FORMAT(o1.fatuandate,'%Y-%m') between '"+format.format(start)+"' and '"+format.format(end)+"'");
			sb.append(" group by DATE_FORMAT(o1.fatuandate,'%Y-%m')");

			List<Object[]> list = hyOrderService.statis(sb.toString());

			if(list!=null && !list.isEmpty()){
				BigDecimal pre = (BigDecimal)list.get(0)[1];
				for(int i=1;i<list.size();i++){
					BigDecimal now = (BigDecimal)list.get(i)[1];
					if(pre.doubleValue()==0.0){
						list.get(i)[2] = 1.0;
					}else{
						list.get(i)[2] = (now.subtract(pre)).divide(pre,2, RoundingMode.HALF_UP);
					}

					pre = now;
				}
			}


			String[] keys = new String[]{"y_m","money","ratio"};
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

	@RequestMapping("/turnover/quarter_list/view")
	@ResponseBody
	public Json turnoverQuarterList(@DateTimeFormat(pattern = "yyyy-MM") Date start,
	                         @DateTimeFormat(pattern = "yyyy-MM")Date end){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try{

			StringBuilder sb = new StringBuilder("");
			sb.append("select concat(date_format(o1.fatuandate, '%Y'),'季度',FLOOR((date_format(o1.fatuandate, '%m')+2)/3)) y_q,IFNULL(sum(o1.jiusuan_money+o1.tip-o1.discounted_price-o1.store_fan_li),0) money,1.0 ratio");
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

			if(list!=null && !list.isEmpty()){
				BigDecimal pre = (BigDecimal)list.get(0)[1];
				for(int i=1;i<list.size();i++){
					BigDecimal now = (BigDecimal)list.get(i)[1];
					if(pre.doubleValue()==0.0){
						list.get(i)[2] = 1.0;
					}else{
						list.get(i)[2] = (now.subtract(pre)).divide(pre,2, RoundingMode.HALF_UP);
					}

					pre = now;
				}
			}


			String[] keys = new String[]{"y_q","money","ratio"};
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

	@RequestMapping("/product/month_list/view")
	@ResponseBody
	public Json productMonthList(@DateTimeFormat(pattern = "yyyy-MM") Date start,
	                        @DateTimeFormat(pattern = "yyyy-MM")Date end){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try{

			StringBuilder sb = new StringBuilder("");

			sb.append("select DATE_FORMAT(p1.recordtime,'%Y-%m') y_m,p1.quantity quantity,1.0 ratio");

			if(start==null){
				start = new Date();
			}
			if(end==null){
				end = new Date();
			}

			sb.append(" from hy_valid_product p1 where");
			sb.append(" DATE_FORMAT(p1.recordtime,'%Y-%m') between '"+format.format(start)+"' and '"+format.format(end)+"'");

			List<Object[]> list = hyValidProductService.statis(sb.toString());

			if(list!=null && !list.isEmpty()){
				BigDecimal pre = BigDecimal.valueOf((Integer)list.get(0)[1]);
				for(int i=1;i<list.size();i++){
					BigDecimal now = BigDecimal.valueOf((Integer)list.get(i)[1]);
					if(pre.doubleValue()==0.0){
						list.get(i)[2] = 1.0;
					}else{
						list.get(i)[2] = (now.subtract(pre)).divide(pre,2, RoundingMode.HALF_UP);
					}

					pre = now;
				}
			}


			String[] keys = new String[]{"y_m","quantity","ratio"};

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

	@RequestMapping("/product/quarter_list/view")
	@ResponseBody
	public Json productQuarterList(@DateTimeFormat(pattern = "yyyy-MM") Date start,
	                             @DateTimeFormat(pattern = "yyyy-MM")Date end){
		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		try{

			StringBuilder sb = new StringBuilder("");

			sb.append("select CONCAT(DATE_FORMAT(p1.recordtime,'%Y'),'季度',FLOOR((date_format(p1.recordtime, '%m')+2)/3)) y_m,p1.quantity quantity,1.0 ratio");

			if(start==null){
				start = new Date();
			}
			if(end==null){
				end = new Date();
			}

			sb.append(" from hy_valid_product p1 where DATE_FORMAT(p1.recordtime,'%m')%3=0");
			sb.append(" and DATE_FORMAT(p1.recordtime,'%Y-%m') between '"+format.format(start)+"' and '"+format.format(end)+"'");

			List<Object[]> list = hyValidProductService.statis(sb.toString());

			if(list!=null && !list.isEmpty()){
				BigDecimal pre = BigDecimal.valueOf((Integer)list.get(0)[1]);
				for(int i=1;i<list.size();i++){
					BigDecimal now = BigDecimal.valueOf((Integer)list.get(i)[1]);
					if(pre.doubleValue()==0.0){
						list.get(i)[2] = 1.0;
					}else{
						list.get(i)[2] = (now.subtract(pre)).divide(pre,2, RoundingMode.HALF_UP);
					}

					pre = now;
				}
			}


			String[] keys = new String[]{"y_q","quantity","ratio"};

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
