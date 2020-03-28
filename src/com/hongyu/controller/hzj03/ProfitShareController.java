package com.hongyu.controller.hzj03;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.BaseController;
import com.hongyu.entity.ProfitShareDetail;
import com.hongyu.entity.ProfitShareStore;
import com.hongyu.entity.ProfitShareWechatBusiness;
import com.hongyu.service.ProfitShareDetailService;
import com.hongyu.service.ProfitShareStoreService;
import com.hongyu.service.ProfitShareWechatBusinessService;

/**
 * 后返(微商、门店)
 * */
@Controller
@RequestMapping("/admin")
public class ProfitShareController extends BaseController {
	private final Integer MAX_PAGE_SIZE = 10000;

	// 微商后返
	@Resource(name = "profitShareWechatBusinessServiceImpl")
	ProfitShareWechatBusinessService profitShareWechatBusinessServiceImpl;

	// 门店后返
	@Resource(name = "profitShareStoreServiceImpl")
	ProfitShareStoreService profitShareStoreServiceImpl;
	
	
	//后返详情
	@Resource(name = "profitShareDetailServiceImpl")
	ProfitShareDetailService profitShareDetailServiceImpl;
	
	/**
	 * 微商后返列表
	 */
	@RequestMapping(value = "/profitsharewechatbusiness/datagrid", method = RequestMethod.GET)
	@ResponseBody
	public Json getDataGrid(Pageable pageable,ProfitShareWechatBusiness queryParm, String startTime, String endTime,HttpSession session)
			throws Exception {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		
		if (queryParm == null)
			queryParm = new ProfitShareWechatBusiness();
		
		List<Filter> filters = new ArrayList<Filter>();
		Filter fl;
		if (startTime != null) {
			fl = new Filter();
			fl.setValue(formatter.parse(startTime));
			fl.setOperator(Operator.ge);
			fl.setProperty("payDate");
			filters.add(fl);
		}
		if (endTime != null) {
			fl = new Filter();
			fl.setValue(formatter.parse(endTime));
			fl.setOperator(Operator.le);
			fl.setProperty("payDate");
			filters.add(fl);
		}
		pageable.setFilters(filters);
		
		Page<ProfitShareWechatBusiness> page = profitShareWechatBusinessServiceImpl.findPage(pageable, queryParm);
		
		Json j = new Json();
		j.setSuccess(true);
		j.setMsg("");
		j.setObj(page);
		return j;
	}
	
	
	/**
	 * 微商后返详情
	 * */
	@RequestMapping(value = "/profitsharewechatbusiness/detail", method = RequestMethod.GET)
	@ResponseBody
	public Json getDetail(Long id, HttpSession session)
			throws Exception {
		
		ProfitShareWechatBusiness p = profitShareWechatBusinessServiceImpl.find(id);
		HashMap<String,Object> map = new HashMap<>();
		
		Pageable pageable = new Pageable(1,MAX_PAGE_SIZE - 1);  //获取所有的记录
		List<Filter> filters = new ArrayList<Filter>();
		Filter fl;
		if(p.getBillinyCycleStart() != null) {
			fl = new Filter();
			fl.setValue(p.getBillinyCycleStart());
			fl.setOperator(Operator.ge);
			fl.setProperty("date");
			filters.add(fl);
		}
		if(p.getBillinyCycleEnd() != null) {
			fl = new Filter();
			fl.setValue(p.getBillinyCycleEnd());
			fl.setOperator(Operator.le);
			fl.setProperty("date");
			filters.add(fl);
		}
		pageable.setFilters(filters);	
		ProfitShareDetail queryParm =new ProfitShareDetail();
		queryParm.setWechatBusinessId(p.getWeChatBusinessId());	
		Page<ProfitShareDetail> page = profitShareDetailServiceImpl.findPage(pageable, queryParm);
		
		
		map.put("weChatBusinessName", p.getWechatBusinessName());
		map.put("billingCycleStart", p.getBillinyCycleStart());
		map.put("billingCycleEnd", p.getBillinyCycleEnd());
		map.put("profitShareDetail", page.getRows());

		Json j = new Json();
		j.setSuccess(true);
		j.setMsg("");
		j.setObj(map);
		return j;
	}
	
	
	
	/**
	 * 门店后返列表
	 */
	@RequestMapping(value = "/profitsharestore/datagrid",method = RequestMethod.GET)
	@ResponseBody
	public Json getDataGridStore(Pageable pageable,ProfitShareStore queryParm, String startTime, String endTime,HttpSession session)
			throws Exception {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if (queryParm == null)
			queryParm = new ProfitShareStore();
		
		List<Filter> filters = new ArrayList<Filter>();
		Filter fl;
		if (startTime != null) {
			fl = new Filter();
			fl.setValue(formatter.parse(startTime));
			fl.setOperator(Operator.ge);
			fl.setProperty("payDate");
			filters.add(fl);
		}
		if (endTime != null) {
			fl = new Filter();
			fl.setValue(formatter.parse(endTime));
			fl.setOperator(Operator.le);
			fl.setProperty("payDate");
			filters.add(fl);
		}
		pageable.setFilters(filters);
		
		Page<ProfitShareStore> page = profitShareStoreServiceImpl.findPage(pageable, queryParm);
		
		Json j = new Json();
		j.setSuccess(true);
		j.setMsg("");
		j.setObj(page);
		return j;
	}


	
	/**
	 * 门店后返详情
	 * */
	@RequestMapping(value = "/profitsharestore/detail", method = RequestMethod.GET)
	@ResponseBody
	public Json getDetailStore(Long id, HttpSession session)
			throws Exception {
		
		
		ProfitShareStore p = profitShareStoreServiceImpl.find(id);
		HashMap<String,Object> map = new HashMap<>();
		
		Pageable pageable = new Pageable(1,MAX_PAGE_SIZE - 1);  //获取所有的记录
		List<Filter> filters = new ArrayList<Filter>();
		Filter fl;
		if(p.getBillingCycleStart() != null) {
			fl = new Filter();
			fl.setValue(p.getBillingCycleStart());
			fl.setOperator(Operator.ge);
			fl.setProperty("date");
			filters.add(fl);
		}
		if(p.getBillingCycleEnd() != null) {
			fl = new Filter();
			fl.setValue(p.getBillingCycleEnd());
			fl.setOperator(Operator.le);
			fl.setProperty("date");
			filters.add(fl);
		}
		pageable.setFilters(filters);
		
		ProfitShareDetail queryParm =new ProfitShareDetail();
		queryParm.setStoreId(p.getStoreId());
		Page<ProfitShareDetail> page = profitShareDetailServiceImpl.findPage(pageable, queryParm);
		
		
		map.put("storeName", p.getStoreName());
		map.put("billingCycleStart", p.getBillingCycleStart());
		map.put("billingCycleEnd", p.getBillingCycleEnd());
		map.put("profitShareDetail", page.getRows());

		Json j = new Json();
		j.setSuccess(true);
		j.setMsg("");
		j.setObj(map);
		return j;
		
	}
}
