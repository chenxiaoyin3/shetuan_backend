package com.hongyu.controller.gdw;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.GroupDivide;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.GuideEdit;
import com.hongyu.entity.GuideLanguage;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideEditService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.util.DateUtil;

@Controller
@RequestMapping("/admin/guideAudit/")
public class GuideAuditController {
	@Resource(name = "guideServiceImpl")
	GuideService guideService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name="guideEditServiceImpl")
	GuideEditService guideEditService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name="hyGroupServiceImpl")
	HyGroupService hyGroupService;

	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable, Guide guide) {
		Json json = new Json();
		try {
			Page<Guide> page = guideService.findPage(pageable, guide);
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			for (Guide tmp : page.getRows()) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", tmp.getId());
				map.put("name", tmp.getName());
				map.put("sex", tmp.getSex());
				map.put("rank", tmp.getRank());
				map.put("touristCertificateNumber", tmp.getTouristCertificateNumber());
				map.put("IDNumber", tmp.getIdNumber());
				map.put("phone", tmp.getPhone());
				map.put("guideSn", tmp.getGuideSn());
				map.put("applicationTime", tmp.getApplicationTime());
				map.put("status", tmp.getStatus());
				result.add(map);
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
			json.setMsg("查询错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}

	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id) {
		Json json = new Json();
		try {
			Guide guide = guideService.find(id);
			if (guide != null) {
				Integer status=guide.getStatus();
				if(status.equals(4)){
					List<Filter> filters=new LinkedList<>();
					filters.add(Filter.eq("guideId", guide.getId()));
					List<Order> orders=new LinkedList<>();
					orders.add(Order.desc("createDate"));
					List<GuideEdit> lists=guideEditService.findList(null, filters, orders);
					GuideEdit guideEdit=lists.get(0);
					Map<String,Object> map=new HashMap<>();
					map.put("id", guide.getId());
					map.put("guideSn", guide.getGuideSn());
					map.put("name", guide.getName());
					map.put("sex", guide.getSex());
					map.put("idNumber", guide.getIdNumber());
					map.put("hasCertificate", guideEdit.getHasCertificate());
					map.put("phone", guideEdit.getPhone());
					map.put("rank", guideEdit.getRank());
					map.put("touristCertificateNumber", guideEdit.getTouristCertificateNumber());
					map.put("degree", guide.getDegree());
					map.put("profession", guide.getProfession());
					map.put("graduate", guide.getGraduate());
					map.put("graduateTime", guide.getGraduateTime());
					map.put("hobby", guide.getHobby());
					map.put("address", guide.getAddress());
					map.put("bankName", guideEdit.getBankName());
					map.put("accountName", guide.getAccountName());
					map.put("bankAccount", guideEdit.getBankAccount());
					map.put("bankLink", guideEdit.getBankLink());
					map.put("applicationTime", guide.getApplicationTime());
					map.put("modifyDate", guide.getModifyDate());
					map.put("status", guide.getStatus());
					map.put("isIndividual1", guide.getIsIndividual1());
					map.put("isGroup", guide.getIsGroup());
					map.put("isStudent", guide.getIsStudent());
					map.put("isOld", guide.getIsOld());
					map.put("isEnterprise", guide.getIsEnterprise());
					map.put("isIndividual2", guide.getIsIndividual2());
					map.put("isElse1", guide.getIsElse1());
					map.put("text1", guide.getText1());
					map.put("isSights", guide.getIsSights());
					map.put("isAncient", guide.getIsAncient());
					map.put("isHumanity", guide.getIsHumanity());
					map.put("isEntertainment", guide.getIsEntertainment());
					map.put("isElse2", guide.getIsElse2());
					map.put("text2", guide.getText2());
					map.put("isSign", guide.getIsSign());
					map.put("isDance", guide.getIsDance());
					map.put("isStory", guide.getIsStory());
					map.put("isElse3", guide.getIsElse3());
					map.put("text3", guide.getText3());
					map.put("idcardFront", guide.getIdcardFront());
					map.put("idcardContrary", guide.getIdcardContrary());
					map.put("idcardInhand", guide.getIdcardInhand());
					map.put("touristCertificate", guideEdit.getTouristCertificate());
					map.put("auditor", guide.getAuditor());
					map.put("auditoradvice", guide.getAuditoradvice());
					map.put("audittime", guide.getAudittime());
					map.put("zongheLevel", guide.getZongheLevel());
					map.put("openId", guide.getOpenId());
					map.put("experienceGroups", guide.getExperienceGroups());
					map.put("experienceWorks", guide.getExperienceWorks());
					map.put("guideLanguages", guide.getGuideLanguages());
					int size = guide.getGuideLanguages().size();
					String language = "";
					List<GuideLanguage> gLanguages = guide.getGuideLanguages();
					for(int i=0;i<size;i++){
						language += gLanguages.get(i).getName();
						language += " ";
					}
					map.put("languageName", language);
					
					json.setObj(map);
				}else {
//					Map<String,Object> map=new HashMap<>();
//					int size = guide.getGuideLanguages().size();
//					String language = "";
//					List<GuideLanguage> gLanguages = guide.getGuideLanguages();
//					for(int i=0;i<size;i++){
//						language += gLanguages.get(i).getName();
//						language += " ";
//					}
//					map.put("languageName", language);
//					map.put("guide", guide);
//					json.setObj(map);
					json.setObj(guide);
				}
				json.setSuccess(true);
				json.setMsg("获取成功");
				
			} else {
				json.setSuccess(false);
				json.setMsg("导游不存在");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误");
			// TODO: handle exception
			e.printStackTrace();
		}
		return json;
	}

	@RequestMapping("audit")
	@ResponseBody
	public Json audit(Long id, Integer state, String comment, HttpSession session) {
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Guide guide = guideService.find(id);
			if (guide != null) {
				// String phone=guide.getPhone();
				// if(phone==null){
				// json.setSuccess(false);
				// json.setMsg("手机号为空");
				// return json;
				// }
				// List<Filter> filters=new LinkedList<>();
				// filters.add(Filter.eq("phone", phone));
				// List<Guide> guides=guideService.findList(null, filters,
				// null);
				// if(guides!=null&&guides.size()>0&&guides.get(0).getId()!=id){
				// json.setSuccess(false);
				// json.setMsg("手机号已存在");
				// return json;
				// }
				Integer status = guide.getStatus();
				if (status.equals(0)) {
					if (state == 1) {
						guide.setStatus(1);// 注册已通过
					} else {
						guide.setStatus(2);// 注册已驳回
					}
				}else if(status.equals(4)){
					if(state==1){
						List<Filter> filters=new LinkedList<>();
						filters.add(Filter.eq("guideId", guide.getId()));
						List<Order> orders=new LinkedList<>();
						orders.add(Order.desc("createDate"));
						List<GuideEdit> lists=guideEditService.findList(null, filters, orders);
						GuideEdit guideEdit=lists.get(0);
						guide.setPhone(guideEdit.getPhone());
						guide.setHasCertificate(guideEdit.getHasCertificate());
						guide.setRank(guideEdit.getRank());
						guide.setTouristCertificateNumber(guideEdit.getTouristCertificateNumber());
						guide.setTouristCertificate(guideEdit.getTouristCertificate());
						guide.setBankName(guideEdit.getBankName());
						guide.setBankAccount(guideEdit.getBankAccount());
						guide.setBankLink(guideEdit.getBankLink());
						guide.setStatus(1);
					}else{
						guide.setStatus(5);//修改已驳回
					}
				}
				guide.setAuditor(hyAdmin == null ? "" : hyAdmin.getName());
				guide.setAuditoradvice(comment);
				guide.setAudittime(new Date());
				guideService.update(guide);
				json.setSuccess(true);
				json.setMsg("审核成功");
			} else {
				json.setSuccess(false);
				json.setMsg("导游不存在");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("审核错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	/**
	 * 导游的排班信息
	 * @param pageable
	 * @param id
	 * @return
	 */
	@RequestMapping("arrangement/view")
	@ResponseBody
	public Json guideArrangement(Pageable pageable,Long id) {
		Json json = new Json();
		try {
			Date  today=DateUtil.getStartOfDay(new Date());
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.ge("endDate", today));
			filters.add(Filter.eq("status", 1));
			Map<String, Object> hashMap=new HashMap<>();
	
			filters.add(Filter.eq("guideId",id));
			pageable.setFilters(filters);
			Page<GuideAssignment> page=guideAssignmentService.findPage(pageable);
			List<Map<String, Object>>result=new LinkedList<>();
			for(GuideAssignment tmp:page.getRows()){
				Map<String, Object> map=new HashMap<>();
				Guide guide = guideService.find(tmp.getGuideId());
				map.put("guideId", guide.getId());
				map.put("guideName", guide.getName());
				map.put("guideSn", guide.getGuideSn());
				map.put("lineName", tmp.getLineName());
				map.put("startDate", tmp.getStartDate());
				map.put("endDate", tmp.getEndDate());
				map.put("operator", tmp.getOperator());
				map.put("operatorPhone", tmp.getOperatorPhone());
				if(tmp.getAssignmentType()==1||tmp.getGroupId()==null){
					map.put("signupNumber", "人数不详");
				}else{
					HyGroup hyGroup=hyGroupService.find(tmp.getGroupId());
					if(hyGroup==null){
						map.put("signupNumber", "人数不详");
					}else{
						map.put("signupNumber", hyGroup.getSignupNumber()+"");
					}
				}
				result.add(map);
			}
			
			hashMap.put("pageNumber", page.getPageNumber());
			hashMap.put("pageSize", page.getPageSize());
			hashMap.put("total", page.getTotal());
			hashMap.put("rows", result);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hashMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
}
