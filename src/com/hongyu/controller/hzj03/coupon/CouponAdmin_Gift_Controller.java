package com.hongyu.controller.hzj03.coupon;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Order.Direction;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CouponGift;
import com.hongyu.entity.WechatAccount;
import com.hongyu.service.CouponGiftService;
import com.hongyu.service.WechatAccountService;

/** 电子券 - 后台管理 - 商城赠送*/
@Controller
@RequestMapping("/admin/business/coupon")
public class CouponAdmin_Gift_Controller {
	/** 未绑定 0 */
	private static final int status_notbind = 0;
	
	@Resource(name = "couponGiftServiceImpl")
	CouponGiftService couponGiftService;

	@Resource(name = "wechatAccountServiceImpl")
	WechatAccountService wechatAccountService;


	/** 电子券-商城赠送-查询 */
	@RequestMapping(value = "/gift/query", method = RequestMethod.POST)
	@ResponseBody
	public Json couponGiftQuery(Pageable pageable, CouponGift couponGift) {
		Json j = new Json();

		if (couponGift == null) {
			couponGift = new CouponGift();
		}
		
		
		List<Order> orders = new ArrayList<>();
		orders.add(new Order("id",Direction.desc));//倒序排序
		pageable.setOrders(orders);

		try {
			Page<CouponGift> page = couponGiftService.findPage(pageable, couponGift);

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (CouponGift c : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("couponCode", c.getCouponCode());
				map.put("sum", c.getSum());
				map.put("state", c.getState());
				map.put("activationCode", c.getActivationCode());
				map.put("bindPhone", c.getBindPhone());
				WechatAccount w = wechatAccountService.find(c.getBindWechatAccountId());
				map.put("bindWechatAccountId", (w != null ? w.getWechatName() : ""));
				map.put("bindWechatTime", c.getBindWechatTime());
				map.put("couponCondition", c.getCouponCondition());
				map.put("useCouponAmount", c.getUseCouponAmount());
				map.put("isValid", c.getIsValid());
				map.put("expireTime", c.getExpireTime());

				rows.add(map);
			}

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("pageSize", pageable.getRows());
			obj.put("pageNumber", pageable.getPage());
			obj.put("total", page.getTotal());
			obj.put("rows", rows);

			j.setMsg("查询成功");
			j.setSuccess(true);
			j.setObj(obj);
		} catch (Exception e) {
			j.setMsg("查询失败");
			j.setSuccess(false);
			j.setObj(null);
		}
		return j;
	}

	/** 电子券-商城赠送-统计 */
	@RequestMapping(value = "/gift/statis", method = RequestMethod.POST)
	@ResponseBody
	public Json couponGiftStatis(String startTime, String endTime) {
		Json j = new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			StringBuilder jpql = new StringBuilder(
					"SELECT sum,COUNT(sum), SUM(CASE WHEN state = " + status_notbind
					+ " THEN 1 ELSE 0 END) FROM hy_coupon_gift WHERE 1=1 ");
			if (startTime != null && !startTime.equals("")) {
				Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
				jpql.append("  AND issue_time >=  " + "'" + sdf.format(sTime) + "'");
			}
			if (endTime != null && !endTime.equals("")) {
				Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
				jpql.append("  AND issue_time <=  " + "'" + sdf.format(eTime) + "'");
			}
			jpql.append(" GROUP BY sum Order BY sum");

			List<Object[]> list = couponGiftService.statis(jpql.toString());
			if (list == null || list.size() == 0) {
				j.setMsg("未获取到符合条件的结果");
				j.setSuccess(false);
				j.setObj(list);
				return j;
			}

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (Object[] o : list) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("sum", o[0]);
				map.put("num", o[1]); // 赠送数量
				map.put("notBind", o[2]); // 未绑定数量

				if (o[1] != null && o[2] != null) {
					int i = ((BigInteger) o[1]).intValue() - ((BigDecimal) o[2]).intValue(); // 绑定数量
					map.put("binded", i);
					map.put("total", (Float) o[0] * i); // 实际使用金额
				} else {
					map.put("binded", "");
					map.put("total", ""); // 实际使用金额
				}
				rows.add(map);
			}

			HashMap<String, Object> obj = new HashMap<String, Object>();
			obj.put("rows", rows);
			obj.put("startTime", startTime == null ? "" : startTime);
			obj.put("endTime", endTime == null ? "" : endTime);

			j.setMsg("查询成功");
			j.setSuccess(true);
			j.setObj(obj);

		} catch (Exception e) {
			j.setMsg("查询失败");
			j.setSuccess(false);
			j.setObj(null);
		}
		return j;
	}

	/** 电子券-商城赠送-明细 */
	@RequestMapping(value = "/gift/detail", method = RequestMethod.POST)
	@ResponseBody
	public Json couponGiftDetail(String startTime, String endTime, Float sum, Pageable pageable) {
		Json j = new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			List<Filter> filters = new ArrayList<>();
			if (startTime != null && !startTime.equals("")) {
				filters.add(
						new Filter("issueTime", Operator.ge, sdf.parse(startTime.substring(0, 10) + " " + "00:00:00")));
			}
			if (endTime != null && !endTime.equals("")) {
				filters.add(
						new Filter("issueTime", Operator.le, sdf.parse(endTime.substring(0, 10) + " " + "23:59:59")));
			}
			if (sum != null) {
				filters.add(new Filter("sum", Operator.eq, sum));
			}
			pageable.setFilters(filters);
			
			List<Order> orders = new ArrayList<>();
			orders.add(new Order("id",Direction.desc));//倒序排序
			pageable.setOrders(orders);
			
			Page<CouponGift> page = couponGiftService.findPage(pageable);

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (CouponGift c : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("couponCode", c.getCouponCode());
				map.put("sum", c.getSum());
				map.put("state", c.getState());
				map.put("activationCode", c.getActivationCode());
				map.put("bindPhone", c.getBindPhone());
				WechatAccount w = wechatAccountService.find(c.getBindWechatAccountId());
				map.put("bindWechatAccountId", (w != null ? w.getWechatName() : ""));
				map.put("bindWechatTime", c.getBindWechatTime());
				map.put("couponCondition", c.getCouponCondition());
				map.put("useCouponAmount", c.getUseCouponAmount());
				map.put("isValid", c.getIsValid());
				map.put("expireTime", c.getExpireTime());

				rows.add(map);
			}

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("pageSize", pageable.getRows());
			obj.put("pageNumber", pageable.getPage());
			obj.put("total", page.getTotal());
			obj.put("rows", rows);

			j.setMsg("查询成功");
			j.setSuccess(true);
			j.setObj(obj);
		} catch (Exception e) {
			j.setMsg("查询失败");
			j.setSuccess(false);
			j.setObj(null);
		}
		return j;
	}



	
	
	/** 电子券-商城赠送-统计-导出Excel */
	@RequestMapping(value = "/gift/statis/excel")
//	@ResponseBody
	public void couponGiftStatisExcel(String startTime, String endTime,HttpServletRequest request,
			HttpServletResponse response) {
//		Json j = new Json();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			// 生成Excel表标题
			StringBuffer sb2 = new StringBuffer();
			
			if (startTime != null) {
				sb2.append(startTime.substring(0, 10));
			}
			sb2.append("——");
			if (endTime != null) {
				sb2.append(endTime.substring(0, 10));
			}
			
			sb2.append("商城赠送电子券统计");
			String fileName = "商城赠送电子券统计.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "couponGiftStatis.xml"; // 配置文件
			
			
			StringBuilder jpql = new StringBuilder(
					"SELECT sum,COUNT(sum), SUM(CASE WHEN state = " + status_notbind
					+ " THEN 1 ELSE 0 END) FROM hy_coupon_gift WHERE 1=1 ");
			if (startTime != null && !startTime.equals("")) {
				Date sTime = sdf.parse(startTime.substring(0, 10) + " " + "00:00:00");
				jpql.append("  AND issue_time >=  " + "'" + sdf.format(sTime) + "'");
			}
			if (endTime != null && !endTime.equals("")) {
				Date eTime = sdf.parse(endTime.substring(0, 10) + " " + "23:59:59");
				jpql.append("  AND issue_time <=  " + "'" + sdf.format(eTime) + "'");
			}
			jpql.append(" GROUP BY sum Order BY sum");

			List<Object[]> list = couponGiftService.statis(jpql.toString());
//			if (list == null || list.size() == 0) {
//				j.setMsg("未获取到符合条件的结果");
//				j.setSuccess(false);
//				j.setObj(list);
//				return j;
//			}
			
			List<CouponGiftStaticInfo> infos = new ArrayList<>();
			for(Object[] object : list){
				CouponGiftStaticInfo couponGiftStaticInfo = new CouponGiftStaticInfo();
				couponGiftStaticInfo.setSum((Float)object[0]);
				couponGiftStaticInfo.setNum((BigInteger)object[1]);
				int i = ((BigInteger) object[1]).intValue() - ((BigDecimal) object[2]).intValue();
				couponGiftStaticInfo.setTotal( (Float) object[0] * i);
				couponGiftStaticInfo.setBinded(i);
				couponGiftStaticInfo.setNotBind(((BigDecimal) object[2]));
				infos.add(couponGiftStaticInfo);
			}
			
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, infos, fileName, tableTitle, configFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
//		return null;
	}
	
	
	/** 电子券-商城赠送-统计-内部类*/
	public class CouponGiftStaticInfo{
		
		private Float sum;  // o[0]
		private BigInteger num;  // o[1]
		private BigDecimal notBind;  // o[2]
		private Integer binded;  //  ((BigInteger) o[1]).intValue() - ((BigDecimal) o[2]).intValue()
		private Float total;   //  (Float) o[0] * ( ((BigInteger) o[1]).intValue() - ((BigDecimal) o[2]).intValue() )
		public Float getSum() {
			return sum;
		}
		public void setSum(Float sum) {
			this.sum = sum;
		}
		public BigInteger getNum() {
			return num;
		}
		public void setNum(BigInteger num) {
			this.num = num;
		}
		public BigDecimal getNotBind() {
			return notBind;
		}
		public void setNotBind(BigDecimal notBind) {
			this.notBind = notBind;
		}
		public Integer getBinded() {
			return binded;
		}
		public void setBinded(Integer binded) {
			this.binded = binded;
		}
		public Float getTotal() {
			return total;
		}
		public void setTotal(Float total) {
			this.total = total;
		}
	}
}
