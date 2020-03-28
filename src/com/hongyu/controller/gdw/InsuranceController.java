package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Department;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.InsuranceAttach;
import com.hongyu.entity.InsurancePrice;
import com.hongyu.entity.InsuranceTime;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.InsuranceAttachService;
import com.hongyu.service.InsurancePriceService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.InsuranceTimeService;
import com.hongyu.util.AuthorityUtils;

@Controller
@RequestMapping("/admin/insurance/")
public class InsuranceController {

	@Resource(name = "insuranceServiceImpl")
	InsuranceService insuranceService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name = "insuranceAttachServiceImpl")
	InsuranceAttachService insuranceAttachService;

	@Resource(name = "insurancePriceServiceImpl")
	InsurancePriceService insurancePriceService;

	@Resource(name = "insuranceTimeServiceImpl")
	InsuranceTimeService insuranceTimeService;

	@RequestMapping("getInsurances/view")
	@ResponseBody
	public Json getInsurances(Pageable pageable, Insurance insurance, HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);

			/**
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			Set<HyAdmin> hyAdmins=AuthorityUtils.getAdmins(session, request);
			/** 将数据按照时间排序 */
			List<Order> orders = new ArrayList<Order>();
			Order order = Order.desc("createDate");
			orders.add(order);

			/** 数据按照创建人筛选 */
			List<Filter> filters = new ArrayList<Filter>();
			Filter filter = Filter.in("operator", hyAdmins);
			filters.add(filter);

			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<Insurance> page = insuranceService.findPage(pageable, insurance);
			for (Insurance tmp : page.getRows()) {
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("classify", tmp.getClassify());
				m.put("insuranceCode", tmp.getInsuranceCode());
				m.put("remark", tmp.getRemark());
				result.add(m);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("rows", result);
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(hm);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}

		return json;
	}

	@RequestMapping("addInsurance")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json addInsurance(@RequestBody Insurance insurance, HttpSession session) {
		Json json = new Json();
		try {
			// Insurance insurance=wrapInsurance.getInsurance();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			if (insurance != null) {
				if (insurance.getInsuranceAttachs() != null && insurance.getInsuranceAttachs().size() > 0) {
					for (InsuranceAttach insuranceAttach : insurance.getInsuranceAttachs()) {
						insuranceAttach.setInsurance(insurance);
					}
				}
				if (insurance.getInsurancePrices() != null && insurance.getInsurancePrices().size() > 0) {
					for (InsurancePrice insurancePrice : insurance.getInsurancePrices()) {
						insurancePrice.setInsurance(insurance);
					}
				}
				if (insurance.getInsuranceTimes() != null && insurance.getInsuranceTimes().size() > 0) {
					for (InsuranceTime insuranceTime : insurance.getInsuranceTimes()) {
						insuranceTime.setInsurance(insurance);
					}
				}
				insurance.setOperator(hyAdmin);
				insuranceService.save(insurance);
				json.setSuccess(true);
				json.setMsg("添加成功");
				// json.setObj(insurance);
			} else {
				json.setSuccess(false);
				json.setMsg("添加失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加失败: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("getInsurance/view")
	@ResponseBody
	public Json getInsurance(Long id) {
		Json json = new Json();
		try {
			Insurance insurance = insuranceService.find(id);
			if (insurance == null) {
				json.setSuccess(false);
				json.setMsg("获取失败");
			} else {
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(insurance);
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误: " + e.getMessage());
			e.printStackTrace();

			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("editInsurance")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json editInsurance(@RequestBody Insurance insurance) {
		Json json = new Json();
		try {
			Insurance oldInsurance = insuranceService.find(insurance.getId());
			if (oldInsurance != null) {
				oldInsurance.setClassify(insurance.getClassify());
				oldInsurance.setInsuranceCode(insurance.getInsuranceCode());
				oldInsurance.setRemark(insurance.getRemark());
				if (insurance.getInsuranceTimes() != null && insurance.getInsuranceTimes().size() > 0) {
					oldInsurance.getInsuranceTimes().clear();
					for (InsuranceTime insuranceTime : insurance.getInsuranceTimes()) {
						insuranceTime.setInsurance(oldInsurance);
						}
					oldInsurance.getInsuranceTimes().addAll(insurance.getInsuranceTimes());
				}
				if (insurance.getInsurancePrices() != null && insurance.getInsurancePrices().size() > 0) {
					oldInsurance.getInsurancePrices().clear();
					for (InsurancePrice insurancePrice : insurance.getInsurancePrices()) {
						insurancePrice.setInsurance(oldInsurance);
					}
					oldInsurance.getInsurancePrices().addAll(insurance.getInsurancePrices());
				}
				if (insurance.getInsuranceAttachs() != null && insurance.getInsuranceAttachs().size() > 0) {
					List<InsuranceAttach> oldInsuranceAttachs = oldInsurance.getInsuranceAttachs();
					oldInsurance.getInsuranceAttachs().clear();
					for (InsuranceAttach insuranceAttach : insurance.getInsuranceAttachs()) {
						for (InsuranceAttach oldInsuranceAttach : oldInsuranceAttachs) {
							if (insuranceAttach.getId() == oldInsuranceAttach.getId()) {
								insuranceAttach.setCreateDate(oldInsuranceAttach.getCreateDate());
							}
						}
						insuranceAttach.setInsurance(oldInsurance);
					}
					oldInsurance.getInsuranceAttachs().addAll(insurance.getInsuranceAttachs());
				}
				insuranceService.update(oldInsurance);
				json.setSuccess(true);
				json.setMsg("编辑成功");
			} else {
				json.setSuccess(false);
				json.setMsg("编辑失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("编辑失败: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("deleteInsurance")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json deleteInsurance(Long id) {
		Json json = new Json();
		try {
			Insurance insurance = insuranceService.find(id);
			for (Iterator<InsurancePrice> iterator = insurance.getInsurancePrices().iterator(); iterator.hasNext();) {
				InsurancePrice insurancePrice = iterator.next();
				insurancePriceService.delete(insurancePrice);
			}
			for (Iterator<InsuranceTime> iterator = insurance.getInsuranceTimes().iterator(); iterator.hasNext();) {
				InsuranceTime insuranceTime = iterator.next();
				insuranceTimeService.delete(insuranceTime);
			}
			for (Iterator<InsuranceAttach> iterator = insurance.getInsuranceAttachs().iterator(); iterator.hasNext();) {
				InsuranceAttach insuranceAttach = iterator.next();
				insuranceAttachService.delete(insuranceAttach);
			}
			insuranceService.delete(id);
			json.setSuccess(true);
			json.setMsg("删除成功");
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("删除失败: " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
}
