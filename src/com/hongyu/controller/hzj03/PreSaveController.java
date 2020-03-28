package com.hongyu.controller.hzj03;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.BaseController;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.entity.Department;
import com.hongyu.entity.DistributorPreSave;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.StorePreSave;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.DistributorPreSaveService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.StorePreSaveService;

/**
 * 预存款(门店、分公司、分销商)
 */
@Controller
@RequestMapping("/admin")
public class PreSaveController extends BaseController {
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	// 门店预存款
	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;

	// 分公司预存款
	@Resource(name = "branchPreSaveServiceImpl")
	BranchPreSaveService branchPreSaveService;

	// 分销商预存款
	@Resource(name = "distributorPreSaveServiceImpl")
	DistributorPreSaveService distributorPreSaveService;

	
	/**
	 * 列表数据 - 门店预存款
	 */
	@RequestMapping(value = "/presave/store/view",method = RequestMethod.POST)
	@ResponseBody
	
	
	
//	public Json getDataGrid(Pageable pageable, StorePreSave queryParm,
//			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startTime,
//			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endTime, HttpSession session) {}

	public Json getDataGrid(Pageable pageable, StorePreSave queryParm, String startTime, String endTime,
			HttpSession session) {

		Json j = new Json();
		if (queryParm == null)
			queryParm = new StorePreSave();
		List<Filter> filters = new ArrayList<Filter>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals(""))
				filters.add(
						new Filter("date", Operator.ge, sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(new Filter("date", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
			pageable.setFilters(filters);

			Page<StorePreSave> page = storePreSaveService.findPage(pageable, queryParm);

			j.setSuccess(true);
			j.setMsg("操作成功");
			j.setObj(page);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	/**
	 * 列表数据 - 分公司预存款
	 * */
	@RequestMapping(value = "/branchpresave/datagrid/view")
	@ResponseBody
	public Json getDataGrid(Pageable pageable, BranchPreSave queryParm, String startTime, String endTime,
			HttpSession session) {
		
		Json j = new Json();
		if (queryParm == null)
			queryParm = new BranchPreSave();
		
		// 列表倒序
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("id"));
		pageable.setOrders(orders);
		
		
		List<Filter> filters = new ArrayList<Filter>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals(""))
				filters.add(new Filter("date", Operator.ge, sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(new Filter("date", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
			
			
			// 对分公司进程筛选
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			Department department = admin.getDepartment();
			
			if(department.getHyDepartmentModel().getName().startsWith("分公司")){
				filters.add(Filter.eq("branchId", department.getId()));
			}
			
			pageable.setFilters(filters);

			Page<BranchPreSave> page = branchPreSaveService.findPage(pageable, queryParm);
			j.setSuccess(true);
			j.setMsg("操作成功");
			j.setObj(page);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	/**
	 * 列表数据 - 分销商预存款
	 */
	@RequestMapping(value = "/distributorpresave/view")
	@ResponseBody
	public Json getDataGrid(Pageable pageable,DistributorPreSave queryParm, String startTime, String endTime,HttpSession session)
			throws Exception {


		Json j = new Json();
		if (queryParm == null)
			queryParm = new DistributorPreSave();
		List<Filter> filters = new ArrayList<Filter>();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (startTime != null && !startTime.equals(""))
				filters.add(new Filter("date", Operator.ge, sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			if (endTime != null && !endTime.equals(""))
				filters.add(new Filter("date", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
			pageable.setFilters(filters);

			Page<DistributorPreSave> page = distributorPreSaveService.findPage(pageable, queryParm);

			j.setSuccess(true);
			j.setMsg("操作成功");
			j.setObj(page);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}
}
