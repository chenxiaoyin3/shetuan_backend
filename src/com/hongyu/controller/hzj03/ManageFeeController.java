package com.hongyu.controller.hzj03;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.BaseController;
import com.hongyu.entity.ManageFee;
import com.hongyu.service.ManageFeeService;

/**
 * 门店管理费 - 分公司- 财务中心
 */
@Controller
@RequestMapping("/admin/managefee")
public class ManageFeeController extends BaseController {

	@Resource(name = "manageFeeServiceImpl")
	ManageFeeService manageFeeService;

	/**
	 * 列表数据
	 */
	@RequestMapping("/datagrid")
	@ResponseBody
	public Json getDataGrid(Pageable pageable, ManageFee queryParm, String startTime, String endTime,
			HttpSession session) {
		Json j = new Json();
		if (queryParm == null)
			queryParm = new ManageFee();
		List<Filter> filters = new ArrayList<Filter>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals(""))
				filters.add(new Filter("date", Operator.ge, sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(new Filter("date", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
			pageable.setFilters(filters);

			Page<ManageFee> page = manageFeeService.findPage(pageable, queryParm);

			j.setSuccess(true);
			j.setObj(page);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}
}
