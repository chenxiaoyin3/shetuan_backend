package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.dao.ProfitShareConfirmDao;
import com.hongyu.entity.BankList;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.ProfitShareConfirm;
import com.hongyu.entity.ProfitShareConfirmDetail;
import com.hongyu.entity.ProfitShareDetail;
import com.hongyu.entity.RefundInfo;
import com.hongyu.entity.Store;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.ProfitShareConfirmService;
import com.hongyu.service.RefundInfoService;
import com.hongyu.service.StoreService;
import com.hongyu.util.ProfitShareConfirmSNGenerator;

@Service("profitShareConfirmServiceImpl")
public class ProfitShareConfirmServiceImpl extends BaseServiceImpl<ProfitShareConfirm, Long>
		implements ProfitShareConfirmService {
	@Resource(name = "profitShareConfirmDaoImpl")
	ProfitShareConfirmDao dao;
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderServiceImpl;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyCompanyServiceImpl")
	HyCompanyService hyCompanyServiceImpl;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	HyOrderApplicationService hyOrderApplicationServiceImpl;
	
	@Resource(name="commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;
	
	@Resource(name = "refundInfoServiceImpl")
	RefundInfoService refundInfoServiceImpl;

	@Resource(name = "profitShareConfirmDaoImpl")
	public void setBaseDao(ProfitShareConfirmDao dao) {
		super.setBaseDao(dao);
	}

	@Override
	@Transactional
	public void calculateProfitshareConfirmPerMonth() throws Exception {
		String firstday = getLastMonthFirstDay();
		String lastday = getLastMonthLastDay();
		System.out.println("firstday" + firstday);
		System.out.println("lastday" + lastday);
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//查找这段时间开团的分公司id
//		String sql = "select c.id, count(a.id) from hy_group a left join hy_admin b on a.creator = b.username"
//				+ " left join hy_department c on b.department = c.id where a.audit_status = 2 and a.start_day >= " + "'" + firstday + "'"
//				+ " and a.end_day <= " + "'" + lastday + "'" + " and c.model like " + "'" + "分公司%" + "'" + " group by c.id";
		
		List<Department> companies = getBranchCompany();
		List<Filter> filters = new ArrayList<>();
		for (Department company : companies) {
			Set<Department> sons = getAllDepartmentsOfCompanny(company);
			if (sons.isEmpty())
				continue;
			filters.clear();
			filters.add(Filter.in("department", sons));
			List<Store> stores = storeService.findList(null, filters, null);
			if (!stores.isEmpty()) {
				ProfitShareConfirm confirm = new ProfitShareConfirm();
				HyCompany branchCompany = getBranchCompany(company);
				BigDecimal dividePercent = branchCompany.getDividePercent();
				BigDecimal divideAmount = new BigDecimal(0.00);
				BigDecimal refundAmount = new BigDecimal(0.00);
				for (Store store : stores) {
					Long storeId = store.getId();
					List<HyOrder> orders = this.getAllOrderOfStoreInDuration(storeId, firstday, lastday);
					if (!orders.isEmpty()) {
						//创建分公司分成确认单
						for (HyOrder order : orders) {
							ProfitShareConfirmDetail detail = new ProfitShareConfirmDetail();
							detail.setOrder(order);
							detail.setOrderCode(order.getOrderNumber());
//							detail.setAmount(order.getJiesuanMoney1().subtract(order.getDiscountedPrice()));
							detail.setAmount(order.getKoudianMoney());
							HyGroup group = hyGroupService.find(order.getGroupId());
							detail.setProductId(group.getLine().getPn());
							detail.setProductName(group.getLine().getName());
							detail.setProfitShareConfirmId(confirm);
							detail.setIsIncome(true);
							detail.setPercentBranch(dividePercent);
							BigDecimal shareprofit = detail.getAmount().multiply(dividePercent);
							detail.setShareProfit(shareprofit);
							divideAmount = divideAmount.add(shareprofit);
							confirm.getDetails().add(detail);
							//设置已分成
							order.setIsDivideStatistic(true);
							hyOrderServiceImpl.update(order);
						}
					}
				}
				
				List<Object[]> objectList = getRefundInfo(company.getId());
//				
				if (objectList.size() > 0) {
					for (Object[] objects : objectList) {
						Long refundInfoId = ((BigInteger)objects[0]).longValue();
						BigDecimal koudian = (BigDecimal) objects[1];
						Long orderId = ((BigInteger)objects[2]).longValue();
						RefundInfo orderApplication = refundInfoServiceImpl.find(refundInfoId);
						HyOrder order = hyOrderServiceImpl.find(orderId);
						ProfitShareConfirmDetail detail = new ProfitShareConfirmDetail();
						detail.setOrder(order);
						detail.setOrderCode(order.getOrderNumber());
						detail.setAmount(koudian);
						HyGroup group = hyGroupService.find(order.getGroupId());
						detail.setProductId(group.getLine().getPn());
						detail.setProductName(group.getLine().getName());
						detail.setProfitShareConfirmId(confirm);
						detail.setIsIncome(false);
						detail.setPercentBranch(dividePercent);
						// 考虑方便起见，直接用退款金额比上订单全款的比例再乘以扣点金额获得退还的分成比例
						BigDecimal refundShareprofit = detail.getAmount().multiply(dividePercent);
//						BigDecimal refundShareprofit = order.getKoudianMoney().multiply(refund.divide(order.getJiesuanMoney1())).multiply(dividePercent);
						detail.setShareProfit(refundShareprofit);
						refundAmount = refundAmount.add(refundShareprofit);
						//添加退款确认单
						confirm.getDetails().add(detail);
						orderApplication.setIfTongji(true);
						refundInfoServiceImpl.update(orderApplication);	
					}
				}
				
				if (confirm.getDetails().size() > 0) {
				    List<Filter> fs = new ArrayList<Filter>();
		  		    fs.add(Filter.in("type", SequenceTypeEnum.profitShareConfirm));
		  		    List<CommonSequence> ss = commonSequenceService.findList(null, fs, null);
		  		    CommonSequence c = ss.get(0);
		  		    Long value = c.getValue() + 1;
		  		    c.setValue(value);
		  			commonSequenceService.update(c);
		  			confirm.setConfirmNum(ProfitShareConfirmSNGenerator.getConfirmSN(value));
		  			confirm.setBranch(company);
		  			confirm.setBranchName(company.getName());
		  			confirm.setBillingCycleStart(sdf.parse(firstday));
		  			confirm.setBillingCycleEnd(sdf.parse(lastday));
		  			confirm.setGenerateDate(new Date());
		  			confirm.setAmount(divideAmount.subtract(refundAmount));
		  			confirm.setBankList(branchCompany.getBankLists().iterator().next());
					this.save(confirm);
				}
			}
		}
		
	}
	
	
	
	private Set<Department> getAllDepartmentsOfCompanny(Department company) {
		Set<Department> result = new HashSet<>();
		Queue<Department> queue = new LinkedList<>();
		queue.add(company);
		while (!queue.isEmpty()) {
			Department root = queue.poll();
			for (Department son : root.getHyDepartments()) {
				result.add(son);
				queue.add(son);
			}
		}
		return result;
	}
	
	private List<Department> getBranchCompany() {
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("treePath", ",1,"));
		filters.add(Filter.eq("isCompany", true));
		return departmentService.findList(null, filters, null);
	}
	
	
	private List<HyOrder> getAllOrderOfStoreInDuration(Long storeId, String startDay, String endDay) {
//		String sql = "select a.id, c.store_name from hy_order a inner join hy_group b on a.group_id = b.id inner join"
//				+ " hy_store c on a.store_id = c.id where a.store_type = 0 and c.id = " + storeId + " and b.end_day >= " 
//				+ "'" + startDay + "'" + " and b.end_day <= " + "'" +endDay + "'" + " and a.status = 3"; //status=3表示供应商确认的订单
		
		String sql = "select a.id, c.store_name from hy_order a inner join hy_group b on a.group_id = b.id inner join"
				+ " hy_store c on a.store_id = c.id where a.store_type = 0 and a.ifjiesuan = 1 and a.is_divide_statistic = 0 and c.id = " + storeId + " and a.status = 3"; //status=3表示供应商确认的订单
		List<Object[]> result = dao.findBysql(sql);
		if (!result.isEmpty()) {
			Long[] ids = new Long[result.size()];
			for (int i = 0; i < result.size(); i++) {
				ids[i] = ((BigInteger)result.get(i)[0]).longValue();
			}
			return hyOrderServiceImpl.findList(ids);
		} else {
			return new ArrayList<HyOrder>();
		}
	}
	
	/**
	 * Object[]中第一个是hy_order_application的id,第二个是退款金额，第三个是对应订单id
	 * @param departmentId
	 * @return
	 */
	private List<Object[]> getRefundOrderApplication(Long departmentId) {
		String sql = "select a.id, a.jiesuan_money, b.id as order_id from hy_order_application a inner join hy_order b on a.order_id = b.id"
				+ " inner join hy_store c on b.store_id = c.id inner join hy_department d on d.id = c.department_id"
                + " where a.status = 4 and a.is_sub_statis = 0 and a.type in (0,1,2) and b.store_type = 0 and d.tree_path like "
				+ "'" + "%" + departmentId.toString() + "%" + "'";
		
		List<Object[]> result = dao.findBysql(sql);
		return result;
	}
	
	private List<Object[]> getRefundInfo(Long departmentId) {
		String sql = "select a.id, a.koudian, b.id as order_id from hy_refund_info a inner join hy_order b on a.order_id = b.id"
				+ " inner join hy_store c on b.store_id = c.id inner join hy_department d on d.id = c.department_id"
                + " where a.if_tongji = 0 and a.state = 1 and a.type = 13 and b.store_type = 0 and d.tree_path like "
				+ "'" + "%" + departmentId.toString() + "%" + "'";
		
		List<Object[]> result = dao.findBysql(sql);
		return result;
	}
	
	private static String getLastMonthFirstDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(calendar.getTime());
	}
	
	private static String getLastMonthLastDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(calendar.getTime());
	}
	
	
	private HyCompany getBranchCompany(Department deparment) {
		List<Filter> filters = new ArrayList<>();
		filters.add(Filter.eq("hyDepartment", deparment));
		List<HyCompany> companies = hyCompanyServiceImpl.findList(null, filters, null);
		HyCompany company = companies.get(0);
		
		return company;
	}

	@Override
	public void calculateProfitshareConfirmCurMonth() throws Exception {
		String firstday = getCurtMonthFirstDay();
		String lastday = getCurMonthLastDay();
		System.out.println("firstday" + firstday);
		System.out.println("lastday" + lastday);
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//查找这段时间开团的分公司id
//		String sql = "select c.id, count(a.id) from hy_group a left join hy_admin b on a.creator = b.username"
//				+ " left join hy_department c on b.department = c.id where a.audit_status = 2 and a.start_day >= " + "'" + firstday + "'"
//				+ " and a.end_day <= " + "'" + lastday + "'" + " and c.model like " + "'" + "分公司%" + "'" + " group by c.id";
		
		List<Department> companies = getBranchCompany();
		List<Filter> filters = new ArrayList<>();
		for (Department company : companies) {
			Set<Department> sons = getAllDepartmentsOfCompanny(company);
			if (sons.isEmpty())
				continue;
			filters.clear();
			filters.add(Filter.in("department", sons));
			List<Store> stores = storeService.findList(null, filters, null);
			if (!stores.isEmpty()) {
				ProfitShareConfirm confirm = new ProfitShareConfirm();
				HyCompany branchCompany = getBranchCompany(company);
				BigDecimal dividePercent = branchCompany.getDividePercent();
				BigDecimal divideAmount = new BigDecimal(0.00);
				BigDecimal refundAmount = new BigDecimal(0.00);
				for (Store store : stores) {
					Long storeId = store.getId();
					List<HyOrder> orders = this.getAllOrderOfStoreInDuration(storeId, firstday, lastday);
					if (!orders.isEmpty()) {
						//创建分公司分成确认单
						for (HyOrder order : orders) {
							ProfitShareConfirmDetail detail = new ProfitShareConfirmDetail();
							detail.setOrder(order);
							detail.setOrderCode(order.getOrderNumber());
//							detail.setAmount(order.getJiesuanMoney1().subtract(order.getDiscountedPrice()));
							detail.setAmount(order.getKoudianMoney());
							HyGroup group = hyGroupService.find(order.getGroupId());
							detail.setProductId(group.getLine().getPn());
							detail.setProductName(group.getLine().getName());
							detail.setProfitShareConfirmId(confirm);
							detail.setIsIncome(true);
							detail.setPercentBranch(dividePercent);
							BigDecimal shareprofit = detail.getAmount().multiply(dividePercent);
							detail.setShareProfit(shareprofit);
							divideAmount = divideAmount.add(shareprofit);
							confirm.getDetails().add(detail);
							//设置已分成
							order.setIsDivideStatistic(true);
							hyOrderServiceImpl.update(order);
						}
					}
				}
				
				List<Object[]> objectList = getRefundInfo(company.getId());
//				
				if (objectList.size() > 0) {
					for (Object[] objects : objectList) {
						Long refundInfoId = ((BigInteger)objects[0]).longValue();
						BigDecimal koudian = (BigDecimal) objects[1];
						Long orderId = ((BigInteger)objects[2]).longValue();
						RefundInfo orderApplication = refundInfoServiceImpl.find(refundInfoId);
						HyOrder order = hyOrderServiceImpl.find(orderId);
						ProfitShareConfirmDetail detail = new ProfitShareConfirmDetail();
						detail.setOrder(order);
						detail.setOrderCode(order.getOrderNumber());
						detail.setAmount(koudian);
						HyGroup group = hyGroupService.find(order.getGroupId());
						detail.setProductId(group.getLine().getPn());
						detail.setProductName(group.getLine().getName());
						detail.setProfitShareConfirmId(confirm);
						detail.setIsIncome(false);
						detail.setPercentBranch(dividePercent);
						// 考虑方便起见，直接用退款金额比上订单全款的比例再乘以扣点金额获得退还的分成比例
						BigDecimal refundShareprofit = detail.getAmount().multiply(dividePercent);
//						BigDecimal refundShareprofit = order.getKoudianMoney().multiply(refund.divide(order.getJiesuanMoney1())).multiply(dividePercent);
						detail.setShareProfit(refundShareprofit);
						refundAmount = refundAmount.add(refundShareprofit);
						//添加退款确认单
						confirm.getDetails().add(detail);
						orderApplication.setIfTongji(true);
						refundInfoServiceImpl.update(orderApplication);	
					}
				}
				
				if (confirm.getDetails().size() > 0) {
				    List<Filter> fs = new ArrayList<Filter>();
		  		    fs.add(Filter.in("type", SequenceTypeEnum.profitShareConfirm));
		  		    List<CommonSequence> ss = commonSequenceService.findList(null, fs, null);
		  		    CommonSequence c = ss.get(0);
		  		    Long value = c.getValue() + 1;
		  		    c.setValue(value);
		  			commonSequenceService.update(c);
		  			confirm.setConfirmNum(ProfitShareConfirmSNGenerator.getConfirmSN(value));
		  			confirm.setBranch(company);
		  			confirm.setBranchName(company.getName());
		  			confirm.setBillingCycleStart(sdf.parse(firstday));
		  			confirm.setBillingCycleEnd(sdf.parse(lastday));
		  			confirm.setGenerateDate(new Date());
		  			confirm.setAmount(divideAmount.subtract(refundAmount));
		  			confirm.setBankList(branchCompany.getBankLists().iterator().next());
					this.save(confirm);
				}
			}
		}
		
	}
	
	private static String getCurtMonthFirstDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
//		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(calendar.getTime());
	}
	
	private static String getCurMonthLastDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
//		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(calendar.getTime());
	}
	
}