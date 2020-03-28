package com.hongyu.controller.liyang;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Store;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyOrderService;
import com.hongyu.util.DateUtil;

/**
 * 门店交易额统计：用于首页展示
 * @author liyang
 * @version 2018年12月12日 上午11:08:23
 */
@Controller
@RequestMapping("/admin/homepage/transactionStatistics/")
public class TransactionStatisticsController {
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@RequestMapping("statistics")
	@ResponseBody
	public Json transactionStatistics(HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			HashMap<String, Object> map = new HashMap<>();

			String today = DateUtil.getBirthday(new Date());
			String yesterday = DateUtil.getBirthday(DateUtil.getPreDay(new Date())); 
			StringBuilder sqlToday = new StringBuilder();
			StringBuilder sqlYesterday = new StringBuilder();
			StringBuilder sqlYear= new StringBuilder();
			
			sqlToday.append("select sum(jiusuan_money) ");
			sqlToday.append("from hy_order where status=3 and type=1 ");
			sqlYear.append("select DATE_FORMAT(createtime,'%Y-%m') months,count(id) order_count,sum(jiusuan_money) money ");
			sqlYear.append("from hy_order where status=3 and type=1 ");
			
			//判断当前是否是门店
			if(isStore(hyAdmin)){
				
				StringBuilder suffix = new StringBuilder(); 
				if(hyAdmin.getRole().getName().contains("经理")){
					Store store = hyAdmin.getDepartment().getStore();
					suffix.append("and store_id="+store.getId()+" ");
				}else{
					suffix.append("and operator_id='"+username+"' ");
				}
				sqlToday.append(suffix);
				sqlYesterday.append(sqlToday);	
				sqlToday.append("and DATE(createtime)='"+today+"'");
				sqlYesterday.append("and DATE(createtime)='"+yesterday+"'");
				sqlYear.append(suffix);
				sqlYear.append("group by months");
//				System.out.println(sqlToday);
//				System.out.println(sqlYesterday);
//				System.out.println(sqlYear);
				
			}
			else if(isSupplier(hyAdmin)){	
				StringBuilder suffix = new StringBuilder();
				if(hyAdmin.getHyAdmin()==null){
					//说明此帐号的负责人帐号
					Set<HyAdmin> hyAdmins = hyAdmin.getHyAdmins();			
					suffix.append("and supplier IN ('"+username+"'");
					if(hyAdmins.size()>0){
						for(HyAdmin tmp:hyAdmins){
							suffix.append(",'"+tmp.getUsername()+"'");
						}		
					}
					suffix.append(") ");
				}else{
					//是子帐号
					suffix.append("and supplier='"+username+"' ");
				}						
				sqlToday.append(suffix);
				sqlYesterday.append(sqlToday);	
				sqlToday.append("and DATE(createtime)='"+today+"'");
				sqlYesterday.append("and DATE(createtime)='"+yesterday+"'");	
				sqlYear.append(suffix);
				sqlYear.append("group by months");
//				System.out.println(sqlToday);
//				System.out.println(sqlYesterday);
//				System.out.println(sqlYear);			
			}else{
				throw new Exception("既不是门店也不是供应商");
			}
			List<Object[]> todaySum = hyOrderService.statis(sqlToday.toString());
			List<Object[]> yesterdaySum = hyOrderService.statis(sqlYesterday.toString());
			List<Object[]> months= hyOrderService.statis(sqlYear.toString());
			
			List<MonthTransactionDetail> monthDetails = new ArrayList<>();
			BigDecimal yearSum = BigDecimal.ZERO;
			if(months!=null && months.size()>0 && months.get(0)!=null){
				for(Object[] tmp:months){
					MonthTransactionDetail detail = new MonthTransactionDetail();
					String month = (String)tmp[0];
					String[] ym = month.split("-");
					detail.setYear(ym[0]);
					detail.setMonth(ym[1]);
					detail.setRank(Integer.valueOf(ym[1]));
					detail.setOrderCount((BigInteger)tmp[1]);
					
					BigDecimal money = (BigDecimal)tmp[2];
					detail.setMoney(money);
					yearSum = yearSum.add(money);
					monthDetails.add(detail);
				}
			}			
			if(todaySum!=null && todaySum.size()>0 && todaySum.get(0)!=null){
				map.put("todaySum", todaySum.get(0));
			}else{
				map.put("todaySum", 0);
			}
			if(yesterdaySum!=null && yesterdaySum.size()>0 && yesterdaySum.get(0)!=null){
				map.put("yesterdaySum", yesterdaySum.get(0));
			}else{
				map.put("yesterdaySum", 0);
			}
			map.put("yearSum", yearSum);
			map.put("monthDetails", monthDetails);
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(map);
		} catch (Exception e) {
			json.setMsg("查询失败"+e.getMessage());
			json.setSuccess(false);
			json.setObj(null);
		}
		return json;
	}
	
	/**
	 * 今日营业额统计
	 * @param session
	 * @return
	 */
	@RequestMapping("today/list")
	@ResponseBody
	public Json todayTransactionStatistics(Pageable pageable,HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			HashMap<String, Object> map = new HashMap<>();
	
			BigDecimal sum = BigDecimal.ZERO;
			List<DayTransactionDetail> details = new ArrayList<>();
			
			String today = DateUtil.getBirthday(new Date());
			StringBuilder sql = new StringBuilder();
			//判断当前是否是门店
			if(isStore(hyAdmin)){
				sql.append("select id,order_number,name,people,operator_id,jiusuan_money,createtime ");
				sql.append("from hy_order where status=3 ");
				if(hyAdmin.getIsManager()){
					Store store = hyAdmin.getDepartment().getStore();
					sql.append("and store_id="+store.getId()+" ");
				}else{
					sql.append("and operator_id='"+username+"' ");
				}
				sql.append("and DATE(createtime)='"+today+"'");
								
				System.out.println(sql.toString());
				
			}
			else if(isSupplier(hyAdmin)){				
				sql.append("select id,order_number,name,people,supplier_id,jiusuan_money,createtime ");
				sql.append("from hy_order where status=3 ");
				if(hyAdmin.getHyAdmin()==null){
					//说明此帐号的负责人帐号
					Set<HyAdmin> hyAdmins = hyAdmin.getHyAdmins();
					sql.append("and supplier IN ('"+username+"'");
					if(hyAdmins.size()>0){
						for(HyAdmin tmp:hyAdmins){
							sql.append(",'"+tmp.getUsername()+"'");
						}		
					}
					sql.append(") ");
				}else{
					//是子帐号
					sql.append("and supplier='"+username+"' ");
				}
				sql.append("and DATE(createtime)='"+today+"'");						
				System.out.println(sql.toString());				
			}else{
				//do nothing
			}
			List<Object[]> list = hyOrderService.statis(sql.toString());
			
			
			for(Object[] tmp:list){
				DayTransactionDetail detail = new DayTransactionDetail();
				detail.setId((Long)tmp[0]);
				detail.setOrderNumber((String)tmp[1]);
				detail.setName((String)tmp[2]);
				detail.setPeople((Integer)tmp[3]);
				detail.setOperatorId((String)tmp[4]);
				detail.setCreateTime((Date)tmp[6]);
				BigDecimal money = (BigDecimal)tmp[5];
				sum = sum.add(money);
				details.add(detail);
			}
			map.put("sum", sum);
			map.put("details", details);
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(map);
			
		} catch (Exception e) {
			json.setMsg("查询失败"+e.getMessage());
			json.setSuccess(false);
			json.setObj(null);
		}
		return json;
	}
	/**
	 * 昨日营业额统计
	 * @param session
	 * @return
	 */
	@RequestMapping("yesterday")
	@ResponseBody
	public Json yesterdayTransactionStatistics(HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			
			HashMap<String, Object> map = new HashMap<>();
			BigDecimal sum = BigDecimal.ZERO;
			List<DayTransactionDetail> details = new ArrayList<>();
			
			String yesterday = DateUtil.getBirthday(DateUtil.getPreDay(new Date()));
			StringBuilder sql = new StringBuilder();
			//判断当前是否是门店
			if(isStore(hyAdmin)){
				
				sql.append("select id,order_number,name,people,operator_id,jiusuan_money,createtime ");
				sql.append("from hy_order where status=3 ");
				if(hyAdmin.getIsManager()){
					Store store = hyAdmin.getDepartment().getStore();
					sql.append("and store_id="+store.getId()+" ");
				}else{
					sql.append("and operator_id='"+username+"' ");
				}
				sql.append("and DATE(createtime)='"+yesterday+"'");
								
				System.out.println(sql.toString());	
			}else if(isSupplier(hyAdmin)){
				sql.append("select id,order_number,name,people,supplier_id,jiusuan_money,createtime ");
				sql.append("from hy_order where status=3 ");
				if(hyAdmin.getHyAdmin()==null){
					//说明此帐号的负责人帐号
					Set<HyAdmin> hyAdmins = hyAdmin.getHyAdmins();
					sql.append("and supplier IN ('"+username+"'");
					if(hyAdmins.size()>0){
						for(HyAdmin tmp:hyAdmins){
							sql.append(",'"+tmp.getUsername()+"'");
						}		
					}
					sql.append(") ");
				}else{
					//是子帐号
					sql.append("and supplier='"+username+"' ");
				}
				sql.append("and DATE(createtime)='"+yesterday+"'");
								
				System.out.println(sql.toString());
				
			}else{
				//do nothing
			}
			List<Object[]> list = hyOrderService.statis(sql.toString());			
			for(Object[] tmp:list){
				DayTransactionDetail detail = new DayTransactionDetail();
				detail.setId((Long)tmp[0]);
				detail.setOrderNumber((String)tmp[1]);
				detail.setName((String)tmp[2]);
				detail.setPeople((Integer)tmp[3]);
				detail.setOperatorId((String)tmp[4]);
				detail.setCreateTime((Date)tmp[6]);
				BigDecimal money = (BigDecimal)tmp[5];
				sum = sum.add(money);
				details.add(detail);
			}
			map.put("sum", sum);
			map.put("details", details);
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(map);
		} catch (Exception e) {
			json.setMsg("查询失败"+e.getMessage());
			json.setSuccess(false);
			json.setObj(null);
		}
		return json;
	}
	/**
	 * 年交易额统计，按月统计
	 * @param session
	 * @return
	 */
	@RequestMapping("year")
	@ResponseBody
	public Json yearTransactionStatistics(HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			HashMap<String, Object> map = new HashMap<>();
			BigDecimal sum = BigDecimal.ZERO;
			List<MonthTransactionDetail> details = new ArrayList<>();
			
			StringBuilder sql = new StringBuilder();
			sql.append("select DATE_FORMAT(createtime,'%Y-%m') months,count(id) order_count,sum(jiusuan_money) money ");
			sql.append("from hy_order where status=3 ");
			
			//判断当前是否是门店
			if(isStore(hyAdmin)){
				
				if(hyAdmin.getIsManager()){
					Store store = hyAdmin.getDepartment().getStore();
					sql.append("and store_id="+store.getId()+" ");
				}else{
					sql.append("and operator_id='"+username+"' ");
				}
				sql.append("group by months");
								
				System.out.println(sql.toString());
				
			}else if(isSupplier(hyAdmin)){
				if(hyAdmin.getHyAdmin()==null){
					//说明此帐号的负责人帐号
					Set<HyAdmin> hyAdmins = hyAdmin.getHyAdmins();
					sql.append("and supplier IN ('"+username+"'");
					if(hyAdmins.size()>0){
						for(HyAdmin tmp:hyAdmins){
							sql.append(",'"+tmp.getUsername()+"'");
						}		
					}
					sql.append(") ");
				}else{
					//是子帐号
					sql.append("and supplier='"+username+"' ");
				}
				sql.append("group by months");
								
				System.out.println(sql.toString());
				
			}else{
				//do nothing
			}

			List<Object[]> list = hyOrderService.statis(sql.toString());
			for(Object[] tmp:list){
				MonthTransactionDetail detail = new MonthTransactionDetail();
				String month = (String)tmp[0];
				String[] ym = month.split("-");
				detail.setYear(ym[0]);
				detail.setMonth(ym[1]);
				detail.setRank(Integer.valueOf(ym[1]));
				detail.setOrderCount((BigInteger)tmp[1]);
				
				BigDecimal money = (BigDecimal)tmp[2];
				detail.setMoney(money);
				sum = sum.add(money);
				details.add(detail);
			}
			map.put("sum", sum);
			map.put("details", details);
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(map);
		} catch (Exception e) {
			json.setMsg("查询失败"+e.getMessage());
			json.setSuccess(false);
			json.setObj(null);
		}
		return json;
	}
	
	public boolean isStore(HyAdmin admin){
		if(admin.getDepartment()!=null){
			Department department = admin.getDepartment();
			if(department.getHyDepartmentModel()!=null){
				String name = department.getHyDepartmentModel().getName();
				if(name!=null && name.contains("门店"))					
					return true;
			}
		}
		return false;
	}
	public boolean isSupplier(HyAdmin admin){
		if(admin.getDepartment()!=null){
			Department department = admin.getDepartment();
			if(department.getHyDepartmentModel()!=null){
				String name = department.getHyDepartmentModel().getName();
				if(name!=null && (name.contains("供应商") 
						|| name.contains("出境部")
						|| name.contains("国内部")
						|| name.contains("汽车部")))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 日销售额交易明细包装类
	 * @author liyang
	 * @version 2018年12月12日 下午1:52:40
	 */
	static class DayTransactionDetail{
		/*订单id*/
		Long id;
		/*订单编号*/
		String orderNumber;
		/*订单名称*/
		String name;
		/*订单包含人数*/
		Integer people;
		/*操作人*/
		String operatorId;
		/*交易额*/
		BigDecimal money;
		/*创建时间*/
		Date createTime;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getOrderNumber() {
			return orderNumber;
		}
		public void setOrderNumber(String orderNumber) {
			this.orderNumber = orderNumber;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getPeople() {
			return people;
		}
		public void setPeople(Integer people) {
			this.people = people;
		}
		public String getOperatorId() {
			return operatorId;
		}
		public void setOperatorId(String operatorId) {
			this.operatorId = operatorId;
		}
		public BigDecimal getMoney() {
			return money;
		}
		public void setMoney(BigDecimal money) {
			this.money = money;
		}
		public Date getCreateTime() {
			return createTime;
		}
		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}
		
	}
	/**
	 * 月交易额明细包装类
	 * @author liyang
	 * @version 2018年12月12日 下午2:17:46
	 */
	static class MonthTransactionDetail{
		/*年份说明*/
		String year;
		/*月份说明*/
		String month;
		/*月份顺序*/
		Integer rank;
		/*订单数量*/
		BigInteger orderCount;
		/*总交易额*/
		BigDecimal money;
		
		public String getYear() {
			return year;
		}
		public void setYear(String year) {
			this.year = year;
		}
		public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public Integer getRank() {
			return rank;
		}
		public void setRank(Integer rank) {
			this.rank = rank;
		}
		public BigInteger getOrderCount() {
			return orderCount;
		}
		public void setOrderCount(BigInteger orderCount) {
			this.orderCount = orderCount;
		}
		public BigDecimal getMoney() {
			return money;
		}
		public void setMoney(BigDecimal money) {
			this.money = money;
		}
		
	}
}
