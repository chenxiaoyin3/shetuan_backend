package com.hongyu.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.grain.plugin.StoragePlugin;
import com.grain.plugin.file.FilePlugin;
import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.dao.GroupSendGuideDao;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.GroupSendGuide;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.entity.HyServiceFeeCar;
import com.hongyu.entity.HyServiceFeeNoncar;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.GroupSendGuide.GuideStatusEnum;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.GroupSendGuideService;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyServiceFeeCarService;
import com.hongyu.service.HyServiceFeeNoncarService;
import com.hongyu.util.Constants;
import com.hongyu.util.QrcodeUtil;

@Service(value = "groupSendGuideServiceImp")
public class GroupSendGuideServiceImp extends BaseServiceImpl<GroupSendGuide, Long> implements GroupSendGuideService {
	@Resource(name = "guideServiceImpl")
	GuideService guideService;

	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;

	@Resource(name = "groupSendGuideDaoImpl")
	GroupSendGuideDao dao;

	@Resource(name="hyServiceFeeCarServiceImpl")
	HyServiceFeeCarService hyServiceFeeCarService;
	
	@Resource(name="hyServiceFeeNoncarServiceImpl")
	HyServiceFeeNoncarService hyServiceFeeNoncarService;

	@Value("${guide.guideVisitorSite}")
	private String guideVisitorSite;
	
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	@Resource(name = "groupSendGuideDaoImpl")
	public void setBaseDao(GroupSendGuideDao dao) {
		super.setBaseDao(dao);
	}

	@Override
	public Json addOrder(Long id, Long guideId) {
		// TODO Auto-generated method stub
		Json json = new Json();
		try {
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("qiangdanId", id));
			filters.add(Filter.eq("guideId", guideId));
			filters.add(Filter.ne("status", 2));//找出不等于驳回的
			filters.add(Filter.ne("status", 3));//找出不等于取消的
			List<GuideAssignment> guideAssignments=guideAssignmentService.findList(null, filters, null);
			if(guideAssignments!=null&&guideAssignments.size()>0){
				json.setSuccess(false);
				json.setMsg("该导游已抢单，不能重复抢单");
				return json;
			}
			GroupSendGuide groupSendGuide = this.find(id);
			Guide guide = guideService.find(guideId);
			Integer sex = guide.getSex();
			if (groupSendGuide.getIsRestrictSex()) {
				synchronized (groupSendGuide) {
					if (groupSendGuide.getStatus() != GuideStatusEnum.zhengchang) {
						json.setSuccess(false);
						json.setMsg("当前状态不可抢单");
						return json;
					} else {
						if (sex == 0) {// 女
							if (groupSendGuide.getWomanNo() > groupSendGuide.getWomanReceive()) {
								Integer womenReceive = groupSendGuide.getWomanReceive();
								groupSendGuide.setWomanReceive(womenReceive + 1);
								this.update(groupSendGuide);
								Json json2=generateGuideAssianment(guide, groupSendGuide);
								if(json2.isSuccess()==true){
									json.setSuccess(true);
									json.setMsg("抢单成功");
								}else{
									json=json2;
								}
							} else {
								json.setSuccess(false);
								json.setMsg("余量不足，抢单失败");
							}
						}
						if (sex == 1) {// 男
							if (groupSendGuide.getManNo() > groupSendGuide.getManReceive()) {
								Integer manReceive = groupSendGuide.getManReceive();
								groupSendGuide.setManReceive(manReceive + 1);
								this.update(groupSendGuide);
								Json json2=generateGuideAssianment(guide, groupSendGuide);
								if(json2.isSuccess()==true){
									json.setSuccess(true);
									json.setMsg("抢单成功");
								}else{
									json=json2;
								}
							} else {
								json.setSuccess(false);
								json.setMsg("余量不足，抢单失败");
							}
						}
					}
				}

			} else {
				synchronized (groupSendGuide) {
					if (groupSendGuide.getGuideNo() > groupSendGuide.getAllReceive()) {
						Integer allReceive = groupSendGuide.getAllReceive();
						groupSendGuide.setAllReceive(allReceive + 1);
						this.update(groupSendGuide);
						Json json2=generateGuideAssianment(guide, groupSendGuide);
						if(json2.isSuccess()==true){
							json.setSuccess(true);
							json.setMsg("抢单成功");
						}else{
							json=json2;
						}
					} else {
						json.setSuccess(false);
						json.setMsg("余量不足，抢单失败");
					}
				}
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("抢单错误: " + e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	private Json generateGuideAssianment(Guide guide, GroupSendGuide groupSendGuide) throws Exception {
		Json json=new Json();
		GuideAssignment guideAssignment = new GuideAssignment();
		guideAssignment.setGuideId(guide.getId());
		if(groupSendGuide.getTeamType()) {
			guideAssignment.setTeamType(1);
		} else {
			guideAssignment.setTeamType(0);
		}
		guideAssignment.setLineType(groupSendGuide.getLineType());
		guideAssignment.setAssignmentType(2);
		guideAssignment.setGroupId(groupSendGuide.getGroup().getId());
		guideAssignment.setOrderId(null);
		
		Integer serviceType = groupSendGuide.getServiceType();
		guideAssignment.setServiceType(serviceType);
		
		guideAssignment.setQiangdanId(groupSendGuide.getId());
		guideAssignment.setStartDate(groupSendGuide.getStartTime());
		guideAssignment.setEndDate(groupSendGuide.getEndTime());
		Integer days = groupSendGuide.getDays();
		guideAssignment.setDays(days);
		
		String name = null;
		HyGroup group = groupSendGuide.getGroup();
		HyLine line;
		if (group == null) {
			json.setSuccess(false);
			json.setMsg("指定团不存在");
			return json;
		} else {
			line = group.getLine();
			if (line == null) {
				json.setSuccess(false);
				json.setMsg("团所属线路不存在");
				return json;
			} else {
				name = line.getName();
			}
		}
		guideAssignment.setLineName(name);
		
		guideAssignment.setTip(groupSendGuide.getXiaofei());
		
		Boolean teamType=groupSendGuide.getTeamType();
		List<Filter> filters=new LinkedList<>();
		filters.add(Filter.eq("groupType", teamType));
		filters.add(Filter.eq("star", guide.getZongheLevel()));
		LineType lineType=groupSendGuide.getLineType();
		BigDecimal serviceFee;
		if (serviceType == 0) {
			guideAssignment.setTravelProfile(groupSendGuide.getFuwuneirong());
			if(lineType==com.hongyu.entity.HyLine.LineType.qiche){
				Integer tmpDay;
				if(teamType==false){
					tmpDay=((days>=3)?3:days);
				}else{
					tmpDay=((days>=5)?5:days);
				}
				filters.add(Filter.eq("days", tmpDay));
				List<HyServiceFeeCar> lists=hyServiceFeeCarService.findList(null, filters, null);
				if(lists!=null&&lists.size()>0){
					BigDecimal price=lists.get(0).getPrice();
					if(teamType==true&&days<5){
						serviceFee=price;
					}else{
						serviceFee=price.multiply(new BigDecimal(days));
					}
				}else{
					json.setSuccess(false);
					json.setMsg("导游服务费参数设置不完整，请检查");
					return json;
				}
			}else{
				filters.add(Filter.eq("lineType", lineType));
				List<HyServiceFeeNoncar> lists=hyServiceFeeNoncarService.findList(null, filters, null);
				if(lists!=null&&lists.size()>0){
					BigDecimal price=lists.get(0).getPrice();
					serviceFee=price.multiply(new BigDecimal(days));
				}else{
					json.setSuccess(false);
					json.setMsg("导游服务费参数设置不完整，请检查");
					return json;
				}
			}
		} else {
			guideAssignment.setElseService(groupSendGuide.getFuwuneirong());
			List<Filter> filters1 = new ArrayList<>();
			filters.add(Filter.eq("eduleixing", Eduleixing.elseFee));
			List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters1, null);
			CommonShenheedu xiane = edu.get(0);
//			Xiane xiane=xianeService.find(Constants.elseFee);
			if(xiane==null){
				json.setSuccess(false);
				json.setMsg("其他服务费参数设置缺失，请检查");
				return json;
			}
			serviceFee=xiane.getMoney().multiply(new BigDecimal(days));
		}
		guideAssignment.setServiceFee(serviceFee);
		guideAssignment.setTotalFee(guideAssignment.getTip().add(guideAssignment.getServiceFee()));
		guideAssignment.setOperator(groupSendGuide.getOperator() == null ? "" : groupSendGuide.getOperator().getName());
		guideAssignment.setPaiqianDate(groupSendGuide.getCancelTime());
		guideAssignment.setConfirmDate(new Date());
		guideAssignment.setStatus(1);
		guideAssignmentService.save(guideAssignment);
		Long groupId=guideAssignment.getGroupId();
		Long assignmentId =guideAssignment.getId();
		String url = guideVisitorSite + "groupId=" + groupId+"&assignmentId="+assignmentId;
		StoragePlugin filePlugin = new FilePlugin();
		String uuid = UUID.randomUUID() + "";
		String location = "/"+Constants.resourcesPath+"/qrcode/" + uuid + ".jpg";
		String tmp = System.getProperty("java.io.tmpdir") + "/upload_" + uuid + ".tmp";
		File file = QrcodeUtil.getQrcode(url, 200, tmp);
		filePlugin.upload(location, file, null);
		guideAssignment.setVisitorFeedbackQRcode(location);
		guideAssignmentService.update(guideAssignment);
		json.setSuccess(true);
		json.setMsg("派团信息更新成功");
		return json;
	}
}
