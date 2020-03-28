package com.hongyu.controller.liyang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.Department;
import com.hongyu.entity.FddContract;
import com.hongyu.entity.FddDayTripContract;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroupPrice;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.Store;
import com.hongyu.service.FddContractService;
import com.hongyu.service.FddContractTemplateService;
import com.hongyu.service.FddDayTripContractService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupPriceService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRoleService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.util.liyang.CopyFromNotNullBeanUtilsBean;
/**
 * 一日游合同的生成修改和提交签署。
 * @author liyang
 *
 */
@Controller
@RequestMapping("/admin/storeLineOrderStore/fddOneDayTrip/")
public class FddDayTripContractController {
	@Resource(name = "fddDayTripContractServiceImpl")
	FddDayTripContractService fddDayTripContractService;
	/**
	 * 返回fddDayTripContract实体，里面已经有填充了可以从数据库获取的数据
	 * 1.已经有合同信息则从合同表中提取
	 * 2.如果没有，则新建一个合同
	 * @param orderId
	 * @param session
	 * @return
	 */
	@RequestMapping("fillIn")
	@ResponseBody
	public Json fillIn(Long orderId,HttpSession session){
		Json json = new Json();
		try {
				HashMap<String, Object> hm = fddDayTripContractService.fillIn(orderId, session);
				json.setMsg((String)hm.get("msg"));
				json.setObj(hm);
				json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	/**
	 * 接收前端传回的合同填充信息。
	 * 1、如果是新合同，则将合同save到合同表中
	 * 如果是重签的合同，则更新该合同
	 * 2、调用虹宇自动签章
	 * @param fddDayTripContract
	 * @return
	 */
	@RequestMapping("submit")
	@ResponseBody
	public Json generateContract(@RequestBody FddDayTripContract fddDayTripContract){
		Json json = new Json();
		try {
			HashMap<String, Object> hm = fddDayTripContractService.submit(fddDayTripContract);
			json.setMsg((String) hm.get("msg"));
			json.setObj(hm);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("生成合同失败： "+e.getMessage());
		}
		return json;
	}
	/**
	 * 
	 * @param id 合同表的主键id
	 * @return
	 */
	@RequestMapping("customerSign")
	@ResponseBody
	public Json extCustomerSign(Long id){
		Json json = new Json();
		try {
			String result = fddDayTripContractService.extCustomerSign(id);
			JSONObject jsonObject = JSONObject.parseObject(result);
			String code = jsonObject.getString("code");
			if(code.equals("3000")){
				json.setMsg("给客户发送信息成功，等待客户签章");
				json.setSuccess(true);
			}else{
				json.setMsg("给客户发送信息失败:"+jsonObject.getString("msg"));
				json.setSuccess(false);
			}			
		} catch (Exception e) {
			json.setMsg("给客户发送信息失败"+e.getMessage());
			json.setSuccess(false);
		}
		return json;
	}
	/**
	 * 合同详情页
	 * @param id
	 * @return
	 */
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json = new Json();
		try {
			FddDayTripContract fddDayTripContract = fddDayTripContractService.find(id);
			HashMap< String, Object> hashMap = new HashMap<>();
			if(fddDayTripContract!=null){
				hashMap.put("contractId", fddDayTripContract.getContractId());
				hashMap.put("status", fddDayTripContract.getStatus());
				hashMap.put("downloadUrl", fddDayTripContract.getDownloadUrl());
				hashMap.put("viewpdfUrl", fddDayTripContract.getViewpdfUrl());
			}
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(hashMap);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取失败:"+e.getMessage());
		}
		
		return json;
	}
	
}
