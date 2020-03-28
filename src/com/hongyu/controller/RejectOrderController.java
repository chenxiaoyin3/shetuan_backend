package com.hongyu.controller;

import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.util.ArrayHandler;
import com.hongyu.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reject_order")
public class RejectOrderController {

	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;

	@RequestMapping("/page/view")
	@ResponseBody
	public Json list(Pageable pageable, Date startTime, Date endTime){

		Json json = new Json();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
		try{

			StringBuilder sb = new StringBuilder("");
			sb.append(" from hy_order_application ap1,hy_admin a1,hy_order o1,hy_store st1,hy_group g1,hy_line l1,hy_supplier sup1");
			sb.append(" where ap1.type=6 and ap1.status=0 and ap1.operator_id=a1.username and ap1.order_id=o1.id and o1.type=1 and o1.store_id=st1.id" +
				" and o1.group_id=g1.id and g1.line=l1.id and l1.supplier=sup1.id");
			if(startTime!=null){
				sb.append(" and ap1.createtime >= '"+format.format(startTime)+"'");
			}
			if(endTime!=null){
				sb.append(" and ap1.createtime <= '"+format.format(endTime)+"'");
			}
//			sb.append(" and ap1.createtime between '"+format.format(startTime)+"' and '"+format.format(endTime)+"'");

			String sqlTotal = "select count(*)"+sb.toString();

			List ansTotal = hyOrderApplicationService.statis(sqlTotal);

			Integer total = ((BigInteger) ansTotal.get(0)).intValue();

			Integer start = (pageable.getPage()-1)*pageable.getRows();
			start = start>total?total:start;
			Integer end = start + pageable.getRows();
			end = end>total?total:end;

			String sqlList = "select o1.order_number ornum,o1.name orname,st1.store_name stname,ap1.createtime rejtime,CONCAT(sup1.supplier_name,a1.name) operator,ap1.view reason";
			sqlList += sb.toString();
			sqlList += " order by rejtime";
			sqlList += " limit "+start+","+end;

			List<Object[]> list = hyOrderApplicationService.statis(sqlList);

			String[] keys = new String[]{"ornum","orname","stname","rejtime","operator","reason"};

			List<Map<String, Object>> maps = new ArrayList<>();

			for(Object[] object:list) {
				maps.add(ArrayHandler.toMap(keys,object));
			}

			Page<Map<String, Object>> pages=new Page<>(maps,total,pageable);

			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(pages);

		}catch (Exception e){

			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}

}
