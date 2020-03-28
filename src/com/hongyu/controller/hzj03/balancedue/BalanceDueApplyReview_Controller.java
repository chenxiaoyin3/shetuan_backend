package com.hongyu.controller.hzj03.balancedue;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.*;
import com.hongyu.entity.HyLine.RefundTypeEnum;
import com.hongyu.service.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author xyy
 * (总公司、分公司)付尾款审核 */
@Controller
@RequestMapping("admin/balanceDueApply")
public class BalanceDueApplyReview_Controller {
	@Resource(name = "hyGroupServiceImpl")
	private HyGroupService hyGroupService;
	
	@Resource(name = "hyLineServiceImpl")
    private HyLineService hyLineService;
	
	@Resource(name = "balanceDueApplyItemServiceImpl")
    private BalanceDueApplyItemService balanceDueApplyItemService;
	
	@Resource(name = "balanceDueApplyServiceImpl")
    private BalanceDueApplyService balanceDueApplyService;
	
	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;

	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;

	/** (总公司、分公司)申请付尾款审核 - 列表 */
	@RequestMapping(value = "/list/view")
	@ResponseBody
	public Json balanceDueApplyReviewList(Pageable pageable,String startDate, String endDate, Integer state, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			json = balanceDueApplyService.balanceDueApplyReviewList(pageable, startDate, endDate, state, username);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败");
			e.printStackTrace();
		}
		return json;
	}

	/** (总公司、分公司)申请付尾款审核 - 详情 */
	@RequestMapping(value = "/detail/view")
	@ResponseBody
	public Json getHistoryComments(Long id) {
		Json json = new Json();
		HashMap<String, Object> obj = new HashMap<>();

		try {
			BalanceDueApply balanceDueApply = balanceDueApplyService.find(id);

			// 审核步骤
			String processInstanceId = balanceDueApply.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> list = new LinkedList<>();
			for (Comment comment : commentList) {
				Map<String, Object> map = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				String step = "";
				if (task != null) {
					step = task.getName();
				}
				map.put("step", step);
				String username = comment.getUserId();
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				map.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				if (index < 0) {
					map.put("comment", " ");
					map.put("result", 1);
				} else {
					map.put("comment", str.substring(0, index));
					map.put("result", Integer.parseInt(str.substring(index + 1)));
				}
				map.put("time", comment.getTime());

				list.add(map);
			}

			obj.put("auditlist", list);

			// 供应商信息
			obj.put("supplierName", balanceDueApply.getSupplierElement().getName());
			obj.put("bankList", balanceDueApply.getSupplierElement().getBankList());
			obj.put("contact", balanceDueApply.getOperator().getName());
			obj.put("supplierType", balanceDueApply.getSupplierElement().getSupplierType());
			obj.put("money", balanceDueApply.getMoney());
			obj.put("applyDate", balanceDueApply.getCreateTime());

			// 付款信息
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("balanceDueApplyId", id));
			List<BalanceDueApplyItem> bDueApplyItems = balanceDueApplyItemService.findList(null, filters, null);
			
			List<HashMap<String, Object>> items = new ArrayList<>();
			for(BalanceDueApplyItem b : bDueApplyItems){
				HashMap<String, Object> map = new HashMap<>();
				HyGroup hyGroup = hyGroupService.find(b.getGroupId());
				map.put("sn", hyGroup.getGroupLinePn());
				map.put("launchDate", b.getLaunchDate());
				map.put("lineName", b.getLineName());
				map.put("creator", hyGroup.getCreator().getName());
				map.put("amount", b.getAmount());
				map.put("usePrePay", b.getUsePrePay());
				map.put("transferMoney", b.getPayMoney());
				map.put("applier", b.getApplier().getName());
				map.put("applyDate", b.getCreateTime());
				
				items.add(map);
			}
			
			
			obj.put("groupInfo", items);

			json.setObj(obj);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
		}
		return json;
	}

	/** (总公司、分公司)申请付尾款审核  (产品中心部门经理、分公司副总、分公司财务) */
	@RequestMapping("/audit")
	@ResponseBody
	public Json audit(Long id, String comment, Integer state, HttpSession session) {
		Json json = new Json();
		try {
			json = balanceDueApplyService.insertBalanceDueApply(id, comment, state, session);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核失败");
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping("/product/view")
	@ResponseBody
	public Json productview(String pn)
	{
		Json json=new Json();
		try {
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("pn", pn));
			List<HyLine> hyLines=hyLineService.findList(null,filters,null);
			HyLine hyLine=hyLines.get(0);
			HashMap<String,Object> map=new HashMap<String,Object>();
			map.put("contractCode", hyLine.getContract().getContractCode());
			map.put("pn",hyLine.getPn()); //productID
			map.put("name", hyLine.getName()); //线路名称
			map.put("lineCategory",hyLine.getLineCategory().getName());
			map.put("area",hyLine.getArea().getFullName());
			map.put("isInsurance", hyLine.getIsInsurance());
			map.put("isHeadInsurance",hyLine.getIsHeadInsurance());
			if(hyLine.getIsInsurance()==true) {
				map.put("insuranceCode",hyLine.getInsurance().getInsuranceCode());
				map.put("insuranceRmark",hyLine.getInsurance().getRemark());
			}
			else {
				map.put("insuranceCode",null);
				map.put("insuranceRmark",null);
			}
			map.put("days",hyLine.getDays());
			map.put("refundType",hyLine.getRefundType());
			List<Map<String, Object>> lineInfos = new ArrayList<>();
			List<HyLineTravels> lineTravelList=hyLine.getLineTravels();
			if(lineTravelList.size()>0) {
				for(HyLineTravels hyLineTravels:lineTravelList) {
					HashMap<String,Object> lineMap=new HashMap<String,Object>();
					lineMap.put("name", hyLineTravels.getTransport().getName());
					lineMap.put("route",hyLineTravels.getRoute());
					lineMap.put("isBreakfast",hyLineTravels.getIsBreakfast());
					lineMap.put("isLunch", hyLineTravels.getIsLunch());
					lineMap.put("isDinner",hyLineTravels.getIsDinner());
					lineMap.put("restaurant",hyLineTravels.getRestaurant());
					lineInfos.add(lineMap);
				}
			}
			map.put("lineTravels",lineInfos);
			
			List<Map<String, Object>> refundInfos = new ArrayList<>();
			List<HyLineRefund> lineRefundList=hyLine.getLineRefunds();
			if(hyLine.getRefundType()==RefundTypeEnum.jieti) {			
				if(lineRefundList.size()>0) {
					for(HyLineRefund hyLineRefund:lineRefundList) {
						HashMap<String,Object> refundMap=new HashMap<String,Object>();
						refundMap.put("startDay", hyLineRefund.getStartDay());
						refundMap.put("startTime", hyLineRefund.getStartTime());
						refundMap.put("endDay",hyLineRefund.getEndDay());
						refundMap.put("endTime",hyLineRefund.getEndTime());
						refundMap.put("percentage",hyLineRefund.getPercentage());
						refundInfos.add(refundMap);
					}
				}
			}
			map.put("lineRefunds",refundInfos);
			map.put("cancelMemo",hyLine.getCancelMemo());
			map.put("memo",hyLine.getMemo());
			map.put("memoInner", hyLine.getMemoInner());
			map.put("introduction", hyLine.getIntroduction());
			json.setMsg("获取成功");
			json.setSuccess(true);
			json.setObj(map);
		}
		catch(Exception e) {
			e.printStackTrace();
			json.setMsg("获取失败");
			json.setSuccess(false);
		}
		return json;
	}
}
