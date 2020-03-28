package com.hongyu.controller.gsbing;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyOrderService;
import com.hongyu.util.ArrayHandler;

/**目的地统计报表*/
@RestController
@RequestMapping("/admin/destination/statistics/")
public class DestinationStatisticsReportController {

	@Resource(name="hyAreaServiceImpl")
	private HyAreaService hyAreaService;
	
	@Resource(name="hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	
	/**目的地统计报表*/
	@RequestMapping(value = "list/view")
	@ResponseBody
	public Json listview(Integer lineType,String startTime,String endTime)
	{
		Json json=new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			StringBuilder jpql = new StringBuilder("select a1.id,a1.name,sum(o1.people)");
			jpql.append(" from hy_line h1,hy_order o1,hy_group g1,hy_area a1");
			jpql.append(" where o1.type=1 and o1.status=3");
			if(startTime!=null && !startTime.equals("")) {
				Date start = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
				jpql.append("  and createtime >=  " + "'" + sdf.format(start) + "'");
			}
			if(endTime!=null && !endTime.equals("")) {
				Date end = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
				jpql.append("  and createtime <=  " + "'" + sdf.format(end) + "'");
			}
			jpql.append(" and o1.group_id=g1.id and g1.line=h1.id and h1.area=a1.id");
			if(lineType!=null) {
				if(lineType==1) {
					jpql.append(" and (h1.line_type=0 or h1.line_type=1)");
				}
				else if(lineType==2) {
					jpql.append(" and h1.line_type=2");
				}
				else if(lineType==3) {
					jpql.append(" and h1.line_type=0");
				}
				else if(lineType==4) {
					jpql.append(" and h1.line_type=1");
				}
			}
			jpql.append(" group by a1.id");
			jpql.append(" order by sum(o1.people) desc");
			List<Object[]> list = hyOrderService.statis(jpql.toString());
			String[] keys = new String[] {"areaId","area","number"};
			List<Map<String, Object>> maps = new ArrayList<>();
			for(Object[] objects:list) {
				Object ob=objects[2];
				/**为什么是BigDecimal类型?我没有整明白*/
				if(ob instanceof BigDecimal) {
					BigDecimal value=(BigDecimal)ob;
					if(value.compareTo(BigDecimal.ZERO)>0) {
						maps.add(ArrayHandler.toMap(keys, objects));
					}
				}			
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(maps);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败");
			e.printStackTrace();
		}
		return json;
	}
	
	
	/**按照供应商统计*/
	@RequestMapping(value = "supplier/view")
	@ResponseBody
	public Json supplierview(Long areaId,Integer lineType,String startTime,String endTime)
	{
		Json json=new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			StringBuilder jpql = new StringBuilder("select s1.supplier_name,sum(o1.people)");
			jpql.append(" from hy_supplier s1,hy_line h1,hy_order o1,hy_group g1");
			jpql.append(" where o1.type=1 and o1.status=3 and o1.group_id=g1.id and g1.line=h1.id and h1.supplier=s1.id and h1.area=");
			jpql.append(areaId);
			if(startTime!=null && !startTime.equals("")) {
				Date start = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
				jpql.append("  and createtime >=  " + "'" + sdf.format(start) + "'");
			}
			if(endTime!=null && !endTime.equals("")) {
				Date end = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
				jpql.append("  and createtime <=  " + "'" + sdf.format(end) + "'");
			}
			if(lineType!=null) {
				if(lineType==1) {
					jpql.append(" and (h1.line_type=0 or h1.line_type=1)");
				}
				else if(lineType==2) {
					jpql.append(" and h1.line_type=2");
				}
				else if(lineType==3) {
					jpql.append(" and h1.line_type=0");
				}
				else if(lineType==4) {
					jpql.append(" and h1.line_type=1");
				}
			}
			jpql.append(" group by s1.id");
			jpql.append(" order by sum(o1.people) desc");
			List<Object[]> list = hyOrderService.statis(jpql.toString());
			String[] keys = new String[] {"supplier","number"};
			List<Map<String, Object>> maps = new ArrayList<>();
			for(Object[] objects:list) {
				Object ob=objects[1];
				/**为什么是BigDecimal类型?我没有整明白*/
				if(ob instanceof BigDecimal) {
					BigDecimal value=(BigDecimal)ob;
					if(value.compareTo(BigDecimal.ZERO)>0) {
						maps.add(ArrayHandler.toMap(keys, objects));
					}
				}			
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(maps);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败"+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
}
