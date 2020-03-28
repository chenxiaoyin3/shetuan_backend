package com.hongyu.controller.hzj03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.BaseController;
import com.hongyu.entity.BranchPrePay;
import com.hongyu.entity.BranchPrePayDetail;
import com.hongyu.service.BranchPrePayDetailService;
import com.hongyu.service.BranchPrePayService;
import com.hongyu.service.HyAdminService;

/**
 * 财务中心 - 分公司预付款
 */
@Controller
@RequestMapping("/admin/branchprepay")
public class BranchPrePayController extends BaseController {
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	// 分公司预付款
	@Resource(name = "branchPrePayServiceImpl")
	BranchPrePayService branchPrePayServiceImpl;

	@Resource(name = "branchPrePayDetailServiceImpl")
	BranchPrePayDetailService branchPrePayDetailServiceImpl;

	/**
	 * 列表数据
	 */
	@RequestMapping("/datagrid/view")
	@ResponseBody
	public Json getDataGrid(Pageable pageable, BranchPrePay queryParm, HttpSession session) throws Exception {
		Json j = new Json();

		try {
			
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			String treePath = hyAdminService.find(username).getDepartment().getTreePath();
			String[] strings = treePath.split(",");
			
			if (queryParm == null)
				queryParm = new BranchPrePay();
			
			if(strings.length > 2){ // 对分公司进行筛选
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("branchId", Long.parseLong(strings[2])));
				pageable.setFilters(filters);
			}
			
			Page<BranchPrePay> page = branchPrePayServiceImpl.findPage(pageable, queryParm);
			
			j.setSuccess(true);
			j.setObj(page);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}

	@RequestMapping("/detail/view")
	@ResponseBody
	public Json getBranchPayDetail(Pageable pageable, BranchPrePay queryParm, HttpSession session) throws Exception {
		Json j = new Json();

		try {
			if (queryParm == null)
				queryParm = new BranchPrePay();
			BranchPrePay branchPrePay = branchPrePayServiceImpl.findPage(pageable, queryParm).getRows().get(0);

			Pageable pageable2 = new Pageable(1, 9999);
			BranchPrePayDetail queryParm2 = new BranchPrePayDetail();
			queryParm2.setBranchPrePayId(branchPrePay.getId());

			Page<BranchPrePayDetail> page = branchPrePayDetailServiceImpl.findPage(pageable2, queryParm2);
			List<BranchPrePayDetail> list = page.getRows();

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("branchPrePay", branchPrePay);
			obj.put("list", list);

			j.setSuccess(true);
			j.setObj(obj);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("操作失败");
		}
		return j;
	}
}
