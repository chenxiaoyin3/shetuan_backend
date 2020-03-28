package com.hongyu.controller.hzj03.coupon;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Filter.Operator;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Order.Direction;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CouponMoney;
import com.hongyu.entity.CouponMoneyHistory;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.SpecialtyCategory;
import com.hongyu.service.CouponMoneyHistoryService;
import com.hongyu.service.CouponMoneyService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.SpecialtyCategoryService;

/** 电子券 - 后台管理 - 金额折扣 */
@Controller
@RequestMapping("/admin/business/coupon")
public class CouponAdmin_CouponMoney_Controller {

	/** 电子券 类型: 商城赠送 */
	private static final String conpon_money_gift = "商城赠送";

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "couponMoneyHistoryServiceImpl")
	CouponMoneyHistoryService couponMoneyHistoryService;

	@Resource(name = "couponMoneyServiceImpl")
	CouponMoneyService couponMoneyService;

	@Resource(name = "specialtyCategoryServiceImpl")
	SpecialtyCategoryService specialtyCategoryService;
	// ******************************************************************************************************
	// ******************************************************************************************************
	// ******************************************************************************************************

	private List<HashMap<String, Object>> fieldFilter(SpecialtyCategory parent) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		if (parent.getChildSpecialtyCategory().size() > 0) {
			for (SpecialtyCategory child : parent.getChildSpecialtyCategory()) {
				if (child.getIsActive()) {
					HashMap<String, Object> hm = new HashMap<String, Object>();
					hm.put("value", child.getId());
					hm.put("label", child.getName());
					hm.put("children", fieldFilter(child));
					list.add(hm);
				}
			}
		}
		return list;
	}

	/** 祖先结点、父结点和孩子结点依次弹栈，形成从祖先结点到孩子结点的hashmap的链 */
	private HashMap<String, Object> helper(ArrayDeque<SpecialtyCategory> stack) {
		if (stack.isEmpty())
			return null;
		SpecialtyCategory s = stack.pop();
		HashMap<String, Object> map = new HashMap<>();
		map.put("value", s.getId());
		map.put("label", s.getName());
		map.put("children", helper(stack));
		return map;
	}

	/** 电子券-金额折扣-分区选择 只适用于商城赠送 */
	@RequestMapping({ "/treelist/view" })
	@ResponseBody
	public Json specialtyCategoryTreeList() {
		Json json = new Json();
		List<Filter> filters = new ArrayList<Filter>();
		Filter filter = new Filter("parent", Filter.Operator.isNull, null);
		filters.add(filter);
		List<Order> orders = new ArrayList<Order>();
		orders.add(Order.asc("id"));
		List<SpecialtyCategory> list = this.specialtyCategoryService.findList(null, filters, orders);
		List<HashMap<String, Object>> obj = new ArrayList<HashMap<String, Object>>();

		// 在所有的分区之前，增加全选选项
		HashMap<String, Object> map = new HashMap<>();
		map.put("value", -1); // -1表示针对所有的特产分区
		map.put("label", "全部品类");
		map.put("children", null);
		obj.add(map);

		for (SpecialtyCategory parent : list) {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("value", parent.getId());
			hm.put("label", parent.getName());
			hm.put("children", fieldFilter(parent));
			obj.add(hm);
		}

		json.setSuccess(true);
		json.setMsg("查询成功");
		json.setObj(obj);
		return json;
	}

	/** 电子券-金额折扣-列表 */
	@RequestMapping(value = "/couponmoney/list")
	@ResponseBody
	public Json couponMoneyList(Pageable pageable, CouponMoney couponMoney) {

		Json j = new Json();
		if (couponMoney == null) {
			couponMoney = new CouponMoney();
		}

		List<Order> orders = new ArrayList<>();
		orders.add(new Order("id", Direction.desc));// 倒序排序
		pageable.setOrders(orders);

		try {

			Page<CouponMoney> page = couponMoneyService.findPage(pageable, couponMoney);

			List<HashMap<String, Object>> rows = new ArrayList<>(page.getRows().size());
			for (CouponMoney c : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", c.getId());
				map.put("issueType", c.getIssueType());
				map.put("money", c.getMoney());
				map.put("rebateRatio", c.getRebateRatio());
				map.put("operator", c.getOperator());
				map.put("createTime", c.getCreateTime());
				map.put("isActive", c.getIsActive());
				map.put("couponCondition", c.getCouponCondition());
				map.put("canOverlay", c.getCanOverlay());
				map.put("endTime", c.getEndTime());
				map.put("specialtyCategoryId", c.getSpecialtyCategoryId());

				if (c.getSpecialtyCategoryId() == null)
					map.put("specialtyCategoryName", "");
				else if (c.getSpecialtyCategoryId() == -1) {
					map.put("specialtyCategoryName", "全部品类");
				} else {
					SpecialtyCategory specialtyCategory = specialtyCategoryService.find(c.getSpecialtyCategoryId());
					map.put("specialtyCategoryName", specialtyCategory == null ? "" : specialtyCategory.getName());
				}

				rows.add(map);
			}

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("rows", rows);
			obj.put("total", page.getTotal());
			obj.put("pageNumber", page.getPageNumber());
			obj.put("pageSize", page.getPageSize());

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

	/** 电子券-金额折扣-新建 */
	@RequestMapping(value = "/couponmoney/add")
	@ResponseBody
	public Json couponMoneyAdd(CouponMoney couponMoney, String endtime, HttpSession session) {
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);

		Json j = new Json();
		try {

			// 判断是否已经有这种电子券
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("issueType", Operator.like, couponMoney.getIssueType()));
			filters.add(new Filter("money", Operator.eq, couponMoney.getMoney()));
			filters.add(new Filter("rebateRatio", Operator.eq, couponMoney.getRebateRatio()));

			if (couponMoney.getIssueType().equals(conpon_money_gift)) {
				filters.add(new Filter("couponCondition", Operator.eq, couponMoney.getCouponCondition()));
			}

			List<CouponMoney> list = couponMoneyService.findList(null, filters, null);
			if (list != null && list.size() > 0) {
				j.setMsg("已存在该规格电子券,请修改使用条件");
				j.setSuccess(false);
				j.setObj(null);
				return j;
			}

			couponMoney.setIsActive(true);
			couponMoney.setCreateTime(new Date());
			couponMoney.setOperator(admin.getName());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date eTime = sdf.parse(endtime.substring(0, 10) + " " + "23:59:59");
			couponMoney.setEndTime(eTime);

			couponMoneyService.save(couponMoney);
			j.setMsg("新建成功");
			j.setSuccess(true);
			j.setObj(null);
		} catch (Exception e) {
			j.setMsg("新建失败");
			j.setSuccess(false);
			j.setObj(null);
		}

		return j;
	}

	/** 电子券-金额折扣-修改 */
	@RequestMapping(value = "/couponmoney/modify")
	@ResponseBody
	public Json couponMoneyModify(CouponMoney couponMoney, HttpSession session) {
		Json json = new Json();

		try {
			List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

			Long specialtyCategoryId = couponMoney.getSpecialtyCategoryId(); // 特产分区id
			if (specialtyCategoryId != -2) { // -2表示为非商城赠送电子券
				HashMap<String, Object> map = new HashMap<>();

				if (specialtyCategoryId == -1) {
					map.put("label", "全部品类");
					map.put("value", specialtyCategoryId);
					map.put("children", null);
				} else {
					SpecialtyCategory curr = specialtyCategoryService.find(specialtyCategoryId);
					ArrayDeque<SpecialtyCategory> stack = new ArrayDeque<>();
					stack.push(curr);
					while (stack.peek().getParent() != null) { // 依次将子结点、父节点和祖先结点压入栈中
						stack.push(curr.getParent());
					}
					map = helper(stack);
				}
				list.add(map);
			}

			CouponMoney c = couponMoneyService.find(couponMoney.getId());

			HashMap<String, Object> obj = new HashMap<>();
			obj.put("list", list);
			obj.put("couponMoney", c);

			json.setSuccess(true);
			json.setObj(obj);

		} catch (Exception e) {
			json.setSuccess(false);
			json.setObj(null);
		}
		return json;
	}

	/** 电子券-金额折扣-修改 保存 */ // 2018-04-24T23:59:59.123Z
	@RequestMapping(value = "/couponmoney/save")
	@ResponseBody
	public Json couponMoneySave(CouponMoney couponMoney, String endtime, HttpSession session) {
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);

		Json j = new Json();
		try {
			// 判断是否已经有这种电子券
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("issueType", Operator.like, couponMoney.getIssueType()));
			filters.add(new Filter("money", Operator.eq, couponMoney.getMoney()));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date eTime = sdf.parse(endtime.substring(0, 10) + " " + "23:59:59");
			filters.add(new Filter("endTime", Operator.eq, eTime));

			if (!couponMoney.getIssueType().contains(conpon_money_gift)) { // 不是商城赠送电子券
				filters.add(new Filter("rebateRatio", Operator.eq, couponMoney.getRebateRatio()));
			} else {
				filters.add(new Filter("canOverlay", Operator.eq, couponMoney.getCanOverlay()));
				filters.add(new Filter("specialtyCategoryId", Operator.eq, couponMoney.getSpecialtyCategoryId()));
				filters.add(new Filter("couponCondition", Operator.eq, couponMoney.getCouponCondition()));
			}

			List<CouponMoney> list = couponMoneyService.findList(null, filters, null);
			if (list != null && list.size() > 0) {
				j.setMsg("已存在该规格电子券,请修改使用条件");
				j.setSuccess(false);
				j.setObj(null);
				return j;
			}
			// 1.之前规格的电子券 加入到 hy_coupon_money_history表中
			CouponMoney c = couponMoneyService.find(couponMoney.getId());
			CouponMoneyHistory couponMoneyHistory = new CouponMoneyHistory(c);
			couponMoneyHistoryService.save(couponMoneyHistory);

			// 2.hy_coupon_money表中更新
			couponMoney.setIsActive(true);
			couponMoney.setCreateTime(new Date());
			couponMoney.setOperator(admin.getName());
			couponMoney.setEndTime(eTime);

			couponMoneyService.update(couponMoney, "issueType", "money", "isActive");
			j.setMsg("修改成功");
			j.setSuccess(true);
			j.setObj(null);
		} catch (Exception e) {
			j.setMsg("修改失败");
			j.setSuccess(false);
			j.setObj(null);
		}

		return j;
	}

	/** 电子券-金额折扣-历史 */
	@RequestMapping(value = "/couponmoney/history")
	@ResponseBody
	public Json couponMoneyHistory(Pageable pageable, CouponMoneyHistory couponMoneyHistory) {
		Json j = new Json();
		if (couponMoneyHistory == null) {
			couponMoneyHistory = new CouponMoneyHistory();
		}
		try {
			if (couponMoneyHistory.getPid() == null) {
				j.setMsg("查询失败");
				j.setSuccess(false);
				j.setObj(null);
				return j;
			}

			List<Order> orders = new ArrayList<>();
			orders.add(new Order("id", Direction.desc));// 倒序排序
			pageable.setOrders(orders);

			Page<CouponMoneyHistory> page = couponMoneyHistoryService.findPage(pageable, couponMoneyHistory);

			List<HashMap<String, Object>> rows = new ArrayList<>();
			for (CouponMoneyHistory c : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("issueType", c.getIssueType());
				map.put("money", c.getMoney());
				map.put("rebateRatio", c.getRebateRatio());
				map.put("operator", c.getOperator());
				map.put("createTime", c.getCreateTime());
				map.put("expireTime", c.getExpireTime());
				map.put("couponCondition", c.getCouponCondition());
				map.put("canOverlay", c.getCanOverlay());
				if (c.getSpecialtyCategoryId() == null)
					map.put("specialtyCategoryName", "");
				else if (c.getSpecialtyCategoryId() == -1) {
					map.put("specialtyCategoryName", "全部品类");
				} else {
					SpecialtyCategory specialtyCategory = specialtyCategoryService.find(c.getSpecialtyCategoryId());
					map.put("specialtyCategoryName", specialtyCategory == null ? "" : specialtyCategory.getName());
				}

				map.put("endTime", c.getEndTime());

				rows.add(map);
			}
			HashMap<String, Object> obj = new HashMap<>();
			obj.put("rows", rows);
			obj.put("total", page.getRows().size());
			obj.put("pageNumber", page.getPageNumber());
			obj.put("pageSize", page.getPageSize());

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

	/** 电子券-金额折扣-删除 */
	@RequestMapping(value = "/couponmoney/delete")
	@ResponseBody
	public Json couponMoneyDelete(Long id) {
		Json j = new Json();
		if (id == null) {
			j.setMsg("删除失败");
			j.setSuccess(false);
			return j;
		}
		try {
			// 1.删除hy_coupon_money表中的内容
			couponMoneyService.delete(id);
			// 2.删除hy_coupon_money_history表中的内容
			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter("pid", Operator.eq, id));
			List<CouponMoneyHistory> list = couponMoneyHistoryService.findList(null, filters, null);
			if (list != null && list.size() > 0) {
				for (CouponMoneyHistory c : list)
					couponMoneyHistoryService.delete(c);
			}
			j.setMsg("删除成功");
			j.setSuccess(true);
		} catch (Exception e) {
			j.setMsg("删除失败");
			j.setSuccess(false);
		}
		return j;
	}
}
