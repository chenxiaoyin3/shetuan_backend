package com.hongyu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.grain.dao.BaseDao;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.Department;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyServiceFeeCar;
import com.hongyu.entity.HyServiceFeeNoncar;
import com.hongyu.entity.Store;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyServiceFeeCarService;
import com.hongyu.service.HyServiceFeeNoncarService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.hongyu.util.OrderSNGenerator;

@Service("guideServiceImpl")
public class GuideServiceImpl extends BaseServiceImpl<Guide, Long> implements GuideService {

	@Resource(name = "hyServiceFeeCarServiceImpl")
	HyServiceFeeCarService hyServiceFeeCarService;

	@Resource(name = "hyServiceFeeNoncarServiceImpl")
	HyServiceFeeNoncarService hyServiceFeeNoncarService;

	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;

	@Override
	@Resource(name = "guideDaoImpl")
	public void setBaseDao(BaseDao<Guide, Long> baseDao) {
		// TODO Auto-generated method stub
		super.setBaseDao(baseDao);
	}
	@Override
	public boolean isAvailable(Long id, Date startDate, Date endDate) throws Exception {
		// TODO Auto-generated method stub

		List<Filter> filters = new LinkedList<>();
		filters.add(Filter.eq("guideId", id));
		filters.add(Filter.eq("status", 1));
		filters.add(Filter.le("startDate", startDate));
		filters.add(Filter.ge("endDate", startDate));
		List<GuideAssignment> list = guideAssignmentService.findList(null, filters, null);
		if (list != null && list.size() > 0) {
			return false;
		}

		List<Filter> filters2 = new LinkedList<>();
		filters2.add(Filter.eq("guideId", id));
		filters2.add(Filter.eq("status", 1));
		filters2.add(Filter.le("startDate", endDate));
		filters2.add(Filter.ge("endDate", endDate));
		List<GuideAssignment> list2 = guideAssignmentService.findList(null, filters2, null);
		if (list2 != null && list2.size() > 0) {
			return false;
		}

		List<Filter> filters3 = new LinkedList<>();
		filters3.add(Filter.eq("guideId", id));
		filters3.add(Filter.eq("status", 1));
		filters3.add(Filter.ge("startDate", startDate));
		filters3.add(Filter.le("endDate", endDate));
		List<GuideAssignment> list3 = guideAssignmentService.findList(null, filters3, null);
		if (list3 != null && list3.size() > 0) {
			return false;
		}
		return true;
	}

	@Override
	public Json caculate(LineType lineType, Integer serviceType, Boolean groupType, Integer star, Integer days)
			throws Exception {
		// TODO Auto-generated method stub
		Json json = new Json();
		BigDecimal serviceFee;
		List<Filter> filters = new LinkedList<>();
		filters.add(Filter.eq("groupType", groupType));
		filters.add(Filter.eq("star", star));
		if (serviceType == 0) {
			if (lineType == com.hongyu.entity.HyLine.LineType.qiche) {
				Integer tmpDay;
				if (groupType == false) {
					tmpDay = ((days >= 3) ? 3 : days);
				} else {
					tmpDay = ((days >= 5) ? 5 : days);
				}
				filters.add(Filter.eq("days", tmpDay));
				List<HyServiceFeeCar> lists = hyServiceFeeCarService.findList(null, filters, null);
				if (lists != null && lists.size() > 0) {
					BigDecimal price = lists.get(0).getPrice();
					if (groupType == true && days < 5) {
						serviceFee = price;
						json.setSuccess(true);
						json.setMsg("获取成功");
						json.setObj(serviceFee);
					} else {
						serviceFee = price.multiply(new BigDecimal(days));
						json.setSuccess(true);
						json.setMsg("获取成功");
						json.setObj(serviceFee);
					}
				} else {
					json.setSuccess(false);
					json.setMsg("导游服务费参数设置不完整，请检查");
					return json;
				}
			} else {
				filters.add(Filter.eq("lineType", lineType));
				List<HyServiceFeeNoncar> lists = hyServiceFeeNoncarService.findList(null, filters, null);
				if (lists != null && lists.size() > 0) {
					BigDecimal price = lists.get(0).getPrice();
					serviceFee = price.multiply(new BigDecimal(days));
					json.setSuccess(true);
					json.setMsg("获取成功");
					json.setObj(serviceFee);
				} else {
					json.setSuccess(false);
					json.setMsg("导游服务费参数设置不完整，请检查");
					return json;
				}
			}
		} else {
			List<Filter> filters1 = new ArrayList<>();
			filters.add(Filter.eq("eduleixing", Eduleixing.elseFee));
			List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters1, null);
			CommonShenheedu xiane = edu.get(0);
//			Xiane xiane = xianeService.find(Constants.elseFee);
			if (xiane == null) {
				json.setSuccess(false);
				json.setMsg("其他服务费参数设置缺失，请检查");
				return json;
			}
			serviceFee = xiane.getMoney().multiply(new BigDecimal(days));
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(serviceFee);
		}
		return json;
	}

}
