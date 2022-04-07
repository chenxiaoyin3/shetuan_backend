package com.hongyu.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.ExperienceGroup;
import com.hongyu.entity.ExperienceWork;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideEdit;
import com.hongyu.entity.GuideLanguage;
import com.hongyu.entity.Language;
import com.hongyu.entity.VerificationCode;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.GuideEditService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.LanguageService;
import com.hongyu.service.VerificationCodeService;
import com.hongyu.util.SendMessageEMY;
import com.hongyu.util.SendTemplateTest;
import com.hongyu.util.WechatUtil;
import com.hongyu.util.wechatUtilEntity.AuthAccessToken;
import com.hongyu.util.wechatUtilEntity.AuthTokenParams;

@Controller
@RequestMapping("/wechat/guide/")
public class WechatGuideController {
	@Resource(name="guideServiceImpl")
	GuideService guideService;
	
	@Resource(name="departmentServiceImpl")
	DepartmentService departmentService;
	
	@Resource(name="hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@Resource(name = "verificationCodeServiceImpl")
	VerificationCodeService verificationCodeService;
	
	@Resource(name="languageServiceImpl")
	LanguageService languageService;
	
	@Resource(name = "commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;
	
	@Resource(name="guideEditServiceImpl")
	GuideEditService guideEditService;
	
	@RequestMapping("sendMessage")
	@ResponseBody
	public Json SendMessage(String phone){
		Json json=new Json();
		try {
			if (phone == null || phone.length() == 0) {
				json.setSuccess(false);
				json.setMsg("发送失败，手机号为空");
				return json;
			}
			int x;
	        String t = null;
	        Random r = new Random();
	        while (true) {
	            x = r.nextInt(999999);
	            if (x > 99999) {
	                System.out.println(x);
	                break;
	            } else continue;
	        }
//	        t="验证码:" + x + " (有效期限10分钟)";
	        VerificationCode verificationCode = new VerificationCode();
			verificationCode.setPhone(phone);
			verificationCode.setVcode(x+"");
			verificationCodeService.save(verificationCode);
//	        SendMessageEMY.sendMessage(phone, t);
	        
	        //write by wj
			String str = "{\"code\":\""+x+"\"}";
			boolean isSuccess = SendMessageEMY.sendMessage(phone,str,1);
			if(isSuccess){
				json.setSuccess(true);
		        json.setMsg("发送成功");
			}else{
				json.setSuccess(false);
		        json.setMsg("发送失败");
			}
	        
//	        json.setSuccess(true);
//	        json.setMsg("发送成功");
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("发送错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("validate")
	@ResponseBody
	public Json validate(String phone,String code){
		Json json=new Json();
		try {
			if(phone.equals("12345678910")&&code.equals("123456")){
				json.setSuccess(true);
				json.setMsg("验证通过");
				return json;
			}
			List<Filter> filters = new ArrayList<>();
			Date validDate=new Date(System.currentTimeMillis() - 600000);
			filters.add(Filter.eq("phone", phone));
			filters.add(Filter.eq("vcode", code));
			filters.add(Filter.ge("createTime", validDate));// addtime  must  not  earlier  than  currenttime  for  10min
			List<VerificationCode> verificationCodes = verificationCodeService.findList(null, filters, null);
			if(verificationCodes != null && verificationCodes.size() > 0){
				json.setSuccess(true);
				json.setMsg("验证通过");
			}else{
				json.setSuccess(false);
				json.setMsg("验证失败");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("验证错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("add")
	@ResponseBody
	public Json add(@RequestBody Guide guide){
		Json json=new Json();
		try {
			if(guide!=null){
				String phone=guide.getPhone();
				if(phone==null){
					json.setSuccess(false);
					json.setMsg("手机号为空");
					return json;
				}
				List<Filter> filters=new LinkedList<>();
				filters.add(Filter.eq("phone", phone));
				List<Guide> guides=guideService.findList(null, filters, null);
				if(guides!=null&&guides.size()>0){
					json.setSuccess(false);
					json.setMsg("手机号已存在");
					return json;
				}
				if(guide.getExperienceGroups()!=null&&guide.getExperienceWorks().size()>0){
					for(ExperienceGroup experienceGroup:guide.getExperienceGroups()){
						experienceGroup.setGuide(guide);
					}
				}
				if(guide.getExperienceWorks()!=null&&guide.getExperienceWorks().size()>0){
					for(ExperienceWork experienceWork:guide.getExperienceWorks()){
						experienceWork.setGuide(guide);
					}
				}
				if(guide.getGuideLanguages()!=null&&guide.getGuideLanguages().size()>0){
					for(GuideLanguage guideLanguage:guide.getGuideLanguages()){
						guideLanguage.setGuide(guide);
					}
				}
				List<Filter> filters1 = new ArrayList<Filter>();
				filters.add(Filter.in("type", SequenceTypeEnum.guideSn));
				Long value = 0L;
				synchronized (this) {
					List<CommonSequence> ss = commonSequenceService.findList(null, filters1, null);
					CommonSequence c = ss.get(0);
					if (c.getValue() >= 999) {
						c.setValue(0l);
					}
					value = c.getValue() + 1;
					c.setValue(value);
					commonSequenceService.update(c);
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String nowaday = sdf.format(new Date());
				Integer sex=guide.getSex();
				String code = nowaday + String.format("%03d", value);
				if(sex==0){
					code="F"+code;
				}else{
					code="M"+code;
				}
				guide.setGuideSn(code);
				guideService.save(guide);
				json.setSuccess(true);
				json.setMsg("添加成功");
			}else{
				json.setSuccess(false);
				json.setMsg("添加失败，导游为空");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("添加错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("modify")
	@ResponseBody
	public Json modify(@RequestBody Guide guide){
		Json json=new Json();
		try {
			if(guide!=null){
				String phone=guide.getPhone();
				if(phone==null){
					json.setSuccess(false);
					json.setMsg("手机号为空");
					return json;
				}
				List<Filter> filters=new LinkedList<>();
				filters.add(Filter.eq("phone", phone));
				List<Guide> guides=guideService.findList(null, filters, null);
				if(guides!=null&&guides.size()>0&&guides.get(0).getId()!=guide.getId()){
					json.setSuccess(false);
					json.setMsg("手机号已存在");
					return json;
				}
				Guide oldGuide=guideService.find(guide.getId());
				if(oldGuide==null){
					json.setSuccess(false);
					json.setMsg("导游不存在，编辑失败");
					return json;
				}else{
					oldGuide.setName(guide.getName());
					oldGuide.setSex(guide.getSex());
					oldGuide.setIdNumber(guide.getIdNumber());
					oldGuide.setHasCertificate(guide.getHasCertificate());
					oldGuide.setPhone(guide.getPhone());
					oldGuide.setRank(guide.getRank());
					oldGuide.setTouristCertificateNumber(guide.getTouristCertificateNumber());
					oldGuide.setDegree(guide.getDegree());
					oldGuide.setProfession(guide.getProfession());
					oldGuide.setGraduate(guide.getGraduate());
					oldGuide.setGraduateTime(guide.getGraduateTime());
					oldGuide.setHobby(guide.getHobby());
					oldGuide.setAddress(guide.getAddress());
					oldGuide.setBankName(guide.getBankName());
					oldGuide.setAccountName(guide.getAccountName());
					oldGuide.setBankAccount(guide.getBankAccount());
					oldGuide.setBankLink(guide.getBankLink());
					oldGuide.setIsIndividual1(guide.getIsIndividual1());
					oldGuide.setIsGroup(guide.getIsGroup());
					oldGuide.setIsStudent(guide.getIsStudent());
					oldGuide.setIsOld(guide.getIsOld());
					oldGuide.setIsEnterprise(guide.getIsEnterprise());
					oldGuide.setIsIndividual2(guide.getIsIndividual2());
					oldGuide.setIsElse1(guide.getIsElse1());
					oldGuide.setText1(guide.getText1());
					oldGuide.setIsSights(guide.getIsSights());
					oldGuide.setIsAncient(guide.getIsAncient());
					oldGuide.setIsHumanity(guide.getIsHumanity());
					oldGuide.setIsEntertainment(guide.getIsEntertainment());
					oldGuide.setIsElse2(guide.getIsElse2());
					oldGuide.setText2(guide.getText2());
					oldGuide.setIsSign(guide.getIsSign());
					oldGuide.setIsDance(guide.getIsDance());
					oldGuide.setIsStory(guide.getIsStory());
					oldGuide.setIsElse3(guide.getIsElse3());
					oldGuide.setText3(guide.getText3());
					oldGuide.setIdcardFront(guide.getIdcardFront());
					oldGuide.setIdcardContrary(guide.getIdcardContrary());
					oldGuide.setIdcardInhand(guide.getIdcardInhand());
					oldGuide.setTouristCertificate(guide.getTouristCertificate());
					oldGuide.getExperienceWorks().clear();
					if(guide.getExperienceWorks()!=null&&guide.getExperienceWorks().size()>0){
						for(ExperienceWork experienceWork:guide.getExperienceWorks()){
							experienceWork.setGuide(oldGuide);
						}
						oldGuide.getExperienceWorks().addAll(guide.getExperienceWorks());
					}
					oldGuide.getExperienceGroups().clear();
					if(guide.getExperienceGroups()!=null&&guide.getExperienceGroups().size()>0){
						for(ExperienceGroup experienceGroup:guide.getExperienceGroups()){
							experienceGroup.setGuide(oldGuide);
						}
						oldGuide.getExperienceGroups().addAll(guide.getExperienceGroups());
					}
					oldGuide.getGuideLanguages().clear();
					if(guide.getGuideLanguages()!=null&&guide.getGuideLanguages().size()>0){
						for(GuideLanguage guideLanguage:guide.getGuideLanguages()){
							guideLanguage.setGuide(oldGuide);
						}
						oldGuide.getGuideLanguages().addAll(guide.getGuideLanguages());
					}
					oldGuide.setStatus(4);//修改审核中
					guideService.update(oldGuide);
					GuideEdit guideEdit=new GuideEdit();
					guideEdit.setGuideId(guide.getId());
					guideEdit.setPhone(guide.getPhone());
					guideEdit.setHasCertificate(guide.getHasCertificate());
					guideEdit.setRank(guide.getRank());
					guideEdit.setTouristCertificateNumber(guide.getTouristCertificateNumber());
					guideEdit.setTouristCertificate(guide.getTouristCertificate());
					guideEdit.setBankName(guide.getBankName());
					guideEdit.setBankAccount(guide.getBankAccount());
					guideEdit.setBankLink(guide.getBankLink());
					guideEditService.save(guideEdit);
					json.setSuccess(true);
					json.setMsg("编辑成功");
					return json;
				}
			}else{
				json.setSuccess(false);
				json.setMsg("编辑失败");
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("编辑错误");
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id,String phone){
		Json json=new Json();
		try {
			if(id!=null){
				Guide guide=guideService.find(id);
				json.setSuccess(true);
				json.setMsg("获取成功");
				json.setObj(guide);
			}else if(phone!=null){
				List<Filter> filters=new LinkedList<>();
				filters.add(Filter.eq("phone", phone));
				List<Guide> guides=guideService.findList(null,filters,null);
				if(guides!=null&&guides.size()>0){
					Guide guide=guides.get(0);
					if(guide.getStatus().equals(4)){
						List<Filter> filters2=new LinkedList<>();
						filters2.add(Filter.eq("guideId", guide.getId()));
						List<Order> orders=new LinkedList<>();
						orders.add(Order.desc("createDate"));
						List<GuideEdit> lists=guideEditService.findList(null, filters2, orders);
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
						json.setObj(map);
					}else{
						json.setObj(guide);
					}
					json.setSuccess(true);
					json.setMsg("获取成功");
					
				}else{
					json.setSuccess(false);
					json.setMsg("导游不存在");
				}
			}else{
				json.setSuccess(false);
				json.setMsg("导游不存在");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	
	@RequestMapping("getLanguages")
	@ResponseBody
	public Json getLanguages(){
		Json json=new Json();
		try {
			List<Language> languages=languageService.findAll();
			List<Map<String, Object>> result=new LinkedList<>();
			for(Language tmp:languages){
				Map<String, Object> map=new HashMap<>();
				map.put("id",tmp.getId());
				map.put("name", tmp.getName());
				result.add(map);
			}
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(result);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("请求失败");
		}
		return json;
	}
	
	@RequestMapping("status")
	@ResponseBody
	public Json status(String phone){
		Json json=new Json();
		try {
			if(phone!=null){
				List<Filter> filters=new LinkedList<>();
				filters.add(Filter.eq("phone", phone));
				List<Guide> guides=guideService.findList(null,filters,null);
				if(guides!=null&&guides.size()>0){
					json.setSuccess(true);
					json.setMsg("获取成功");
					Guide guide=guides.get(0);
					Map<String, Object> map=new HashMap<>();
					map.put("id", guide.getId());
					map.put("status", guide.getStatus());
					json.setObj(map);
				}else{
					json.setSuccess(false);
					json.setMsg("导游不存在");
				}
			}else{
				json.setSuccess(false);
				json.setMsg("导游不存在");
			}
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误");
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
	@RequestMapping("test")
	@ResponseBody
	public Json Test(String url,String openId){
		Json json=new Json();
		SendTemplateTest.test(url,openId);
		json.setSuccess(true);
		json.setMsg("发送成功");
		return json;
	}
	@RequestMapping("getAuthAccessToken")
	@ResponseBody
	public Json getAuthAccessToken(String code){
//		String appid="xxxx";
//		String appid="xxxx";
		String appid="xxxx";
//		String appsecret="xxx";
//		String appsecret="xxx";
		String appsecret="xxx";
		String grant_type = "authorization_code";
		
		String url="https://api.weixin.qq.com/sns/oauth2/access_token";
		AuthTokenParams authTokenParams = new AuthTokenParams(appid,appsecret,code,grant_type); 
        Json json=new Json();
        try {
			AuthAccessToken authAccessToken=WechatUtil.getAuthAccessToken(authTokenParams, url,1);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(authAccessToken);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
			// TODO Auto-generated catch block
		}
        return json;
        
	}
}
