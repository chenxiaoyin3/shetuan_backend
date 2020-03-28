package com.hongyu.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.Store;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.StoreService;
import com.hongyu.service.impl.StoreServiceImpl;
/**
 * hongyu后台的导游派团信息管理
 * 1、计调指派
 * 2、门店租借
 * 3、导游抢单列表
 * @author li_yang
 *
 */
@Controller
@RequestMapping("/admin/guideGroup/")
public class GuideAssignmentController {
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminServiceImpl;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name = "guideServiceImpl")
	GuideService guideServiceImpl;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupServiceImpl;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineServiceImpl;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderServiceImpl;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeServiceImpl;
	
	/**
	 * 获取计调指派的导游列表
	 * @param pageable	分页信息
	 * @param status	状态
	 * @param guideName	导游姓名
	 * @param startDate	开始时间
	 * @param endDate	结束时间
	 * @return
	 */
	@RequestMapping("jdlist/view")
	@ResponseBody
	public Json jdlist(HttpSession session,Pageable pageable,Integer status, String guideName,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate){		
		Json json=new Json();
		try {
			/** 得到当前用户 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin liable = hyAdminServiceImpl.find(username);
			
			List<Filter> filters = new LinkedList<>();

			if(status!=null){
				filters.add(Filter.eq("status", status));
			}
			
			//此项只过滤计调指派的单,派遣类型是0；
			filters.add(Filter.eq("assignmentType", 0));
			if(startDate!=null){
				filters.add(Filter.ge("startDate", startDate));
			}
			if(endDate!=null){
				filters.add(Filter.le("endDate", endDate));
			}
			List<Guide> guides = null;
			if(guideName!=null){
				//先根据guideName获取guideId，但是可能会有重名
				Guide guide = new Guide();
				guide.setName(guideName);
				List<Filter> tmpfilters = new LinkedList<>();
				tmpfilters.add(Filter.eq("name", guideName));
				guides = guideServiceImpl.findList(null, tmpfilters, null);
			}
			List<GuideAssignment> guideAssignments = new ArrayList<>();
			if(guides==null){
				guideAssignments = guideAssignmentService.findList(null,filters,null);
			}else{
				for(int i=0;i<guides.size();i++){
					List<Filter> tmpfilters = new LinkedList<>();
					tmpfilters.addAll(filters);
					tmpfilters.add(Filter.eq("guideId",guides.get(i).getId() ));
					List<GuideAssignment> tmp = guideAssignmentService.findList(null,tmpfilters,null);
					guideAssignments.addAll(tmp);
				}
			}
			List<Map<String, Object>> result=new LinkedList<>();
			Page<GuideAssignment> page = null;
			for(GuideAssignment tmp:guideAssignments){				
				Map<String, Object> m=new HashMap<>();
				//产品ID，需要根据团Id来获取线路然后根据线路获取里面的产品ID
				if(tmp.getGuideId()!=null){
					Guide guide  = guideServiceImpl.find(tmp.getGuideId());
					if(guide != null){
						//导游编号	
						m.put("guideSn",guide.getGuideSn());
						//导游姓名
						m.put("guideName", guide.getName());
						//联系电话
						m.put("guidePhone", guide.getPhone());
					}else{
						continue;
					}
				}
				if(tmp.getGroupId()!=null){
					HyGroup hyGroup = hyGroupServiceImpl.find(tmp.getGroupId());
					if(hyGroup!=null){
						HyLine hyline = hyGroup.getLine();
						m.put("pn",hyline.getPn());
					}	
				}				
				//派遣单主键
				m.put("id", tmp.getId());
				//导游id
				m.put("guideId", tmp.getGuideId());			
				//创建日期
				m.put("createDate", tmp.getCreateDate());
				//修改日期
				m.put("modifyDate", tmp.getModifyDate());
				//派遣类型
				m.put("assignmentType", tmp.getAssignmentType());
				
				//线路类型,added by Gsbing,20180801
				HyGroup hyGroup=hyGroupServiceImpl.find(tmp.getGroupId());
				if(hyGroup!=null){
					m.put("lineType", hyGroup.getGroupLineType());
					m.put("teamType", hyGroup.getTeamType());
				}
				//服务类型
				m.put("serviceType", tmp.getServiceType());
				//开始日期
				m.put("startDate", tmp.getStartDate());
				//天数
				m.put("days", tmp.getDays());
				//线路名称
				m.put("lineName", tmp.getLineName());
				//状态
				m.put("status", tmp.getStatus());
				//建团计调
				m.put("operator",tmp.getOperator());
				//行程概要
				m.put("travelProfile",tmp.getTravelProfile());
				
				m.put("balanceStatus", tmp.getBalanceStatus());
				m.put("tip", tmp.getTip());
				m.put("serviceFee", tmp.getServiceFee());
				m.put("totalFee", tmp.getTotalFee());
				m.put("visitorFeedbackQrcode", tmp.getVisitorFeedbackQRcode());
				m.put("balanceStatus", tmp.getBalanceStatus());
				result.add(m);
			}	
			Map<String, Object> hMap=new HashMap<>();
			int pg = pageable.getPage();
			int rows = pageable.getRows();
			hMap.put("total", result.size());
			hMap.put("pageNumber", pg);
			hMap.put("pageSize", rows);
			hMap.put("rows", result.subList((pg-1)*rows, pg*rows>result.size()?result.size():pg*rows));
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败"+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 门店租借列表
	 * @param pageable	分页信息
	 * @param status	状态
	 * @param guideName	导游姓名
	 * @param startDate	开始时间
	 * @param endDate	结束时间
	 * @return
	 */
	@RequestMapping("zjlist/view")
	@ResponseBody
	public Json zjlist(Pageable pageable,Integer status, String guideName,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate){		
		Json json=new Json();
		try {
			List<Filter>filters=new LinkedList<>();
			if(status!=null){
				filters.add(Filter.eq("status", status));
			}
			//此项只过滤门店租借的单,派遣类型是1；
			filters.add(Filter.eq("assignmentType", 1));
			if(startDate!=null){
				filters.add(Filter.ge("startDate", startDate));
			}
			if(endDate!=null){
				filters.add(Filter.le("endDate", endDate));
			}
			List<Guide> guides = null;
			if(guideName!=null){
				//先根据guideName获取guideId，但是可能会有重名
				Guide guide = new Guide();
				guide.setName(guideName);
				List<Filter> tmpfilters = new LinkedList<>();
				tmpfilters.add(Filter.eq("name", guideName));
				guides = guideServiceImpl.findList(null, tmpfilters, null);
			}
			List<GuideAssignment> guideAssignments = new ArrayList<>();
			if(guides==null){
				guideAssignments = guideAssignmentService.findList(null,filters,null);
			}else{
				for(int i=0;i<guides.size();i++){
					List<Filter> tmpfilters = new LinkedList<>();
					tmpfilters.addAll(filters);
					tmpfilters.add(Filter.eq("guideId",guides.get(i).getId() ));
					List<GuideAssignment> tmp = guideAssignmentService.findList(null,tmpfilters,null);
					guideAssignments.addAll(tmp);
				}
			}
			List<Map<String, Object>> result=new LinkedList<>();
			for(GuideAssignment tmp:guideAssignments){
				Map<String, Object> m=new HashMap<>();
				if(tmp.getGuideId()!=null){
					Guide guide  = guideServiceImpl.find(tmp.getGuideId());
					if(guide != null){
						//导游编号	
						m.put("guideSn",guide.getGuideSn());
						//导游姓名
						m.put("guideName", guide.getName());
						//联系电话
						m.put("guidePhone", guide.getPhone());
					}else{
						continue;
					}
				}
				if(tmp.getGroupId()!=null){
					HyGroup hyGroup = hyGroupServiceImpl.find(tmp.getGroupId());
					if(hyGroup!=null){
						HyLine hyline = hyGroup.getLine();
						m.put("pn",hyline.getPn());
					}		
				}
				//派遣单主键
				m.put("id", tmp.getId());
				//导游id
				m.put("guideId", tmp.getGuideId());

				//创建日期
				m.put("createDate", tmp.getCreateDate());
				//修改日期
				m.put("modifyDate", tmp.getModifyDate());
				//派遣类型
				m.put("assignmentType", tmp.getAssignmentType());
				//服务类型
				m.put("serviceType", tmp.getServiceType());
				//开始日期
				m.put("startDate", tmp.getStartDate());
				//天数
				m.put("days", tmp.getDays());
				//线路名称
				m.put("lineName", tmp.getLineName());
				//状态
				m.put("status", tmp.getStatus());
				//建团计调
				m.put("operator",tmp.getOperator());
				//行程概要
				m.put("travelProfile",tmp.getTravelProfile());
				//租借门店：该通过orderId查到订单中的门店Id
				if(tmp.getOrderId()!=null){
					HyOrder order = hyOrderServiceImpl.find(tmp.getOrderId());
					if(order!=null){
						Store store = storeServiceImpl.find(order.getStoreId());	
						m.put("storeName", store.getStoreName());
					}
					
				}
				m.put("tip", tmp.getTip());
				m.put("serviceFee", tmp.getServiceFee());
				m.put("totalFee", tmp.getTotalFee());
				m.put("visitorFeedbackQrcode", tmp.getVisitorFeedbackQRcode());
				m.put("balanceStatus", tmp.getBalanceStatus());
				result.add(m);
			}
			Map<String, Object> hMap=new HashMap<>();
			int pg = pageable.getPage();
			int rows = pageable.getRows();
			hMap.put("total", result.size());
			hMap.put("pageNumber", pg);
			hMap.put("pageSize", rows);
			hMap.put("rows", result.subList((pg-1)*rows, pg*rows>result.size()?result.size():pg*rows));
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败"+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	/**
	 * 获取导游抢单的列表
	 * @param pageable
	 * @param status
	 * @param guideName
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping("qdlist/view")
	@ResponseBody
	public Json qdlist(Pageable pageable,Integer status, String guideName,@DateTimeFormat(iso=ISO.DATE)Date startDate,@DateTimeFormat(iso=ISO.DATE)Date endDate){		
		Json json=new Json();
		try {
			List<Filter>filters=new LinkedList<>();
			if(status!=null){
				filters.add(Filter.eq("status", status));
			}
			//此项只过滤导游抢单的单,派遣类型是2；
			filters.add(Filter.eq("assignmentType", 2));
			if(startDate!=null){
				filters.add(Filter.ge("startDate", startDate));
			}
			if(endDate!=null){
				filters.add(Filter.le("endDate", endDate));
			}
			List<Guide> guides = null;
			if(guideName!=null){
				//先根据guideName获取guideId，但是可能会有重名
				Guide guide = new Guide();
				guide.setName(guideName);
				List<Filter> tmpfilters = new LinkedList<>();
				tmpfilters.add(Filter.eq("name", guideName));
				guides = guideServiceImpl.findList(null, tmpfilters, null);
			}
			List<GuideAssignment> guideAssignments = new ArrayList<>();
			if(guides==null){
				guideAssignments = guideAssignmentService.findList(null,filters,null);
			}else{
				for(int i=0;i<guides.size();i++){
					List<Filter> tmpfilters = new LinkedList<>();
					tmpfilters.addAll(filters);
					tmpfilters.add(Filter.eq("guideId",guides.get(i).getId() ));
					List<GuideAssignment> tmp = guideAssignmentService.findList(null,tmpfilters,null);
					guideAssignments.addAll(tmp);
				}
			}
			List<Map<String, Object>> result=new LinkedList<>();
			for(GuideAssignment tmp:guideAssignments){
				Map<String, Object> m=new HashMap<>();
				//产品ID，需要根据团Id来获取线路然后根据线路获取里面的产品ID
				if(tmp.getGuideId()!=null){
					Guide guide  = guideServiceImpl.find(tmp.getGuideId());
					if(guide != null){
						//导游编号	
						m.put("guideSn",guide.getGuideSn());
						//导游姓名
						m.put("guideName", guide.getName());
						//联系电话
						m.put("guidePhone", guide.getPhone());
					}else{
						continue;
					}
				}
				if(tmp.getGroupId()!=null){
					HyGroup hyGroup = hyGroupServiceImpl.find(tmp.getGroupId());
					if(hyGroup!=null){
						HyLine hyline = hyGroup.getLine();
						m.put("pn",hyline.getPn());
					}		
				}
				//派遣单主键
				m.put("id", tmp.getId());
				//导游id
				m.put("guideId", tmp.getGuideId());
				//创建日期,也可称之为抢单时间。
				m.put("createDate", tmp.getCreateDate());
				//修改日期
				m.put("modifyDate", tmp.getModifyDate());
				//派遣类型
				m.put("assignmentType", tmp.getAssignmentType());
				//服务类型
				m.put("serviceType", tmp.getServiceType());
				//开始日期
				m.put("startDate", tmp.getStartDate());
				//天数
				m.put("days", tmp.getDays());
				//线路名称
				m.put("lineName", tmp.getLineName());
				//状态
				m.put("status", tmp.getStatus());
				//建团计调
				m.put("operator",tmp.getOperator());
				//行程概要
				m.put("travelProfile",tmp.getTravelProfile());
				m.put("qiangdanId", tmp.getQiangdanId());
				result.add(m);
			}
			Map<String, Object> hMap=new HashMap<>();
			int pg = pageable.getPage();
			int rows = pageable.getRows();
			hMap.put("total", result.size());
			hMap.put("pageNumber", pg);
			hMap.put("pageSize", rows);
			hMap.put("rows", result.subList((pg-1)*rows, pg*rows>result.size()?result.size():pg*rows));
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败"+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
}
