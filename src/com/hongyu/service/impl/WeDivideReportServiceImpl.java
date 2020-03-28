package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.WeBusinessDivideController;
import com.hongyu.controller.WeBusinessDivideController.WrapBusinessDivide;
import com.hongyu.controller.WeBusinessDivideController.WrapBusinessDivideDetail;
import com.hongyu.controller.WeBusinessDivideController.WrapBusinessDivideDetailPage;
import com.hongyu.controller.WeBusinessDivideController.WrapBusinessDividePage;
import com.hongyu.controller.WeBusinessDivideController.WrapStoreDividePage;
import com.hongyu.dao.WeDivideReportDao;
import com.hongyu.entity.WeDivideReport;
import com.hongyu.service.WeDivideReportService;

@Service("weDivideReportServiceImpl")
public class WeDivideReportServiceImpl extends BaseServiceImpl<WeDivideReport, Long> implements WeDivideReportService {
	
	@Resource(name="weDivideReportDaoImpl")
	WeDivideReportDao weDivideReportDaoImpl;
	
	@Resource(name="weDivideReportDaoImpl")
	  public void setBaseDao(WeDivideReportDao dao)
	  {
	    super.setBaseDao(dao);
	  }

	@Override
	public WrapStoreDividePage findStoreDividePage(Pageable pageable, String startDate, String endDate,
			Integer type, String storeName, Boolean isPageable) {
		StringBuilder sqlBuilder = new StringBuilder();
		
		WrapStoreDividePage result = new WrapStoreDividePage();
		result.setList(new ArrayList<WeBusinessDivideController.WrapStoreDivide>());
		result.setTotal(new Long(0));
		
		//虹宇门店
		if (Integer.valueOf(0).equals(type)) {
			sqlBuilder.append("select c.id, c.store_name, sum(a.sales_amount), sum(a.divide_amount) from hy_we_divide_report a inner join "
					+ "hy_we_business b on a.we_business_id = b.id inner join hy_store c on b.store_id = c.id where b.type = 0 ");
			if (StringUtils.isNotEmpty(startDate)) {
				sqlBuilder.append("and unix_timestamp(a.sales_time) >= unix_timestamp('" + startDate + "') ");
			}
			if (StringUtils.isNotEmpty(endDate)) {
				sqlBuilder.append("and unix_timestamp(a.sales_time) <= unix_timestamp('" + endDate + "') ");
			}
			if (StringUtils.isNotEmpty(storeName)) {
				sqlBuilder.append("and c.store_name like '" + storeName + "%' ");
			}
			
			sqlBuilder.append("group by c.id ");
			
			List<Object[]> page = null;
			if (isPageable) {
				Page p = this.findPageBysql(sqlBuilder.toString(), pageable);
				result.setTotal(p.getTotal());
				page = p.getLstObj();
			} else {
				page = weDivideReportDaoImpl.findBysql(sqlBuilder.toString());
				result.setTotal(new Long(page.size()));
			}
			List<WeBusinessDivideController.WrapStoreDivide> list = new ArrayList<>();
			for (Object[] objects : page) {
				WeBusinessDivideController.WrapStoreDivide wrap = new WeBusinessDivideController.WrapStoreDivide();
				wrap.setStoreId(((BigInteger)objects[0]).longValue());
				wrap.setStoreName((String)objects[1]);
				wrap.setSalesAmount((BigDecimal)objects[2]);
				wrap.setDivideAmount((BigDecimal)objects[3]);
				wrap.setType(0);
				list.add(wrap);
			}
			result.setList(list);
		} else if (Integer.valueOf(1).equals(type)) {
			sqlBuilder.append("select c.id, c.store_name, sum(a.sales_amount), sum(a.divide_amount) from hy_we_divide_report a inner join "
					+ "hy_we_business b on a.we_business_id = b.id inner join hy_business_store c on b.store_id = c.id where b.type = 1 ");
			if (StringUtils.isNotEmpty(startDate)) {
				sqlBuilder.append("and unix_timestamp(a.sales_time) >= unix_timestamp('" + startDate + "') ");
			}
			if (StringUtils.isNotEmpty(endDate)) {
				sqlBuilder.append("and unix_timestamp(a.sales_time) <= unix_timestamp('" + endDate + "') ");
			}
			if (StringUtils.isNotEmpty(storeName)) {
				sqlBuilder.append("and c.store_name like '" + storeName + "%' ");
			}
			
			sqlBuilder.append("group by c.id ");
			
			List<Object[]> page = null;
			if (isPageable) {
				Page p = this.findPageBysql(sqlBuilder.toString(), pageable);
				result.setTotal(p.getTotal());
				page = p.getLstObj();
			} else {
				page = weDivideReportDaoImpl.findBysql(sqlBuilder.toString());
				result.setTotal(new Long(page.size()));
			}
			List<WeBusinessDivideController.WrapStoreDivide> list = new ArrayList<>();
			for (Object[] objects : page) {
				WeBusinessDivideController.WrapStoreDivide wrap = new WeBusinessDivideController.WrapStoreDivide();
				wrap.setStoreId(((BigInteger)objects[0]).longValue());
				wrap.setStoreName((String)objects[1]);
				wrap.setSalesAmount((BigDecimal)objects[2]);
				wrap.setDivideAmount((BigDecimal)objects[3]);
				wrap.setType(1);
				list.add(wrap);
			}
			result.setList(list);
		}
		
		return result;
	}

	@Override
	public WrapBusinessDividePage findBusinessDividePage(Pageable pageable, String startDate, String endDate,
			Integer type, Long storeId, String businessName, Boolean isPageable) {
		StringBuilder sqlBuilder = new StringBuilder();
		WrapBusinessDividePage result = new WrapBusinessDividePage();
		result.setList(new ArrayList<WeBusinessDivideController.WrapBusinessDivide>());
		result.setTotal(new Long(0));
		
		//type不为null以为着从平台分成页面进入的或者门店分成页面进去的
		if (type != null) {
			//虹宇门店
			if (Integer.valueOf(0).equals(type)) {
				sqlBuilder.append("select b.id, b.name, SUM(a.sales_amount), SUM(a.divide_amount), b.type from hy_we_divide_report a inner "
						+ "join hy_we_business b on a.we_business_id = b.id inner join hy_store c on b.store_id = c.id where 1 = 1 ");
				sqlBuilder.append("and b.type = 0 ");
				sqlBuilder.append("and b.store_id = " + storeId + " ");
			} else if (Integer.valueOf(1).equals(type)) {
				sqlBuilder.append("select b.id, b.name, SUM(a.sales_amount), SUM(a.divide_amount), b.type from hy_we_divide_report a inner "
						+ "join hy_we_business b on a.we_business_id = b.id inner join hy_business_store c on b.store_id = c.id where 1 = 1 ");
				sqlBuilder.append("and b.type = 1 ");
				sqlBuilder.append("and b.store_id = " + storeId + " ");
			} else if (Integer.valueOf(2).equals(type)) {
				sqlBuilder.append("select b.id, b.name, SUM(a.sales_amount), SUM(a.divide_amount), b.type from hy_we_divide_report a inner "
						+ "join hy_we_business b on a.we_business_id = b.id where 1 = 1 ");
				sqlBuilder.append("and b.type = 2 ");
			}
		} else {
			//这个是考虑直接从微商页面进去的
			sqlBuilder.append("select b.id, b.name, SUM(a.sales_amount), SUM(a.divide_amount), b.type from hy_we_divide_report a inner "
					+ "join hy_we_business b on a.we_business_id = b.id where 1 = 1 ");
		}
		
		if (StringUtils.isNotEmpty(startDate)) {
			sqlBuilder.append("and unix_timestamp(a.sales_time) >= unix_timestamp('" + startDate + "') ");
		}
		if (StringUtils.isNotEmpty(endDate)) {
			sqlBuilder.append("and unix_timestamp(a.sales_time) <= unix_timestamp('" + endDate + "') ");
		}
		if (StringUtils.isNotEmpty(businessName)) {
			sqlBuilder.append("and b.name like '" + businessName +"%' ");
		}
		
		sqlBuilder.append("group by b.id");
		
		List<Object[]> page = null;
		if (isPageable) {
			Page p = this.findPageBysql(sqlBuilder.toString(), pageable);
			result.setTotal(p.getTotal());
			page = p.getLstObj();
		} else {
			page = weDivideReportDaoImpl.findBysql(sqlBuilder.toString());
			result.setTotal(new Long(page.size()));
		}
		
		List<WrapBusinessDivide> list = new ArrayList<>();
		for (Object[] objects : page) {
			WrapBusinessDivide wrap = new WrapBusinessDivide();
			wrap.setBusinessId(((BigInteger)objects[0]).longValue());
			wrap.setBusinessName((String)objects[1]);
			wrap.setSalesAmount((BigDecimal)objects[2]);
			wrap.setDivideAmount((BigDecimal)objects[3]);
			wrap.setType(((BigInteger)objects[0]).intValue());
			list.add(wrap);
		}
		result.setList(list);
		
		return result;
	}

	@Override
	public WrapBusinessDividePage findBusinessDetailDividePage(Pageable pageable, String startDate, String endDate,
			Integer type, Long storeId, Long businessId, Boolean isPageable) {
		StringBuilder sqlBuilder = new StringBuilder();
		WrapBusinessDivideDetailPage result = new WrapBusinessDivideDetailPage();
		result.setList(new ArrayList<WrapBusinessDivideDetail>());
		result.setTotal(new Long(0));
		
		
		if (businessId != null) {
			
		}
		
		return null;
	}

	@Override
	public List<Long> findWeBusinessSatisfyTransferCondition() {
		String sql = "select we_business_id, sum(divide_amount) from hy_we_divide_report where transfered = 0 group by we_business_id having sum(divide_amount) > 1.00";
		List<Object[]> queryResult = weDivideReportDaoImpl.findBysql(sql);
		if (!queryResult.isEmpty()) {
			List<Long> res = new ArrayList<>();
			for (Object[] objs :queryResult) {
				Long id = ((BigInteger)objs[0]).longValue();
				res.add(id);
			}
			return res;
		} else {
			return new ArrayList<Long>();
		}		
	}
}
