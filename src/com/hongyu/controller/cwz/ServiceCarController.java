package com.hongyu.controller.cwz;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyServiceFeeCar;
import com.hongyu.entity.HyServiceFeeNoncar;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.service.HyServiceFeeCarService;
import com.hongyu.service.HyServiceFeeNoncarService;


//做分页
@RestController
//上面的加一个“Rest”，加上下面这一句，后面就不用再加@responsebody了 表示返回是json格式
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/service/test/")
public class ServiceCarController {

	@Resource(name = "hyServiceFeeCarServiceImpl")
	private HyServiceFeeCarService hyServiceFeeCarService;
	
	@Resource(name = "hyServiceFeeNoncarServiceImpl")
	private HyServiceFeeNoncarService hyServiceFeeNoncarService;
	
	@RequestMapping(value = "fee/car")
	@ResponseBody
	public Json feeOFCar(Boolean groupType, Integer days, Integer star, Pageable pageable) {//三个筛选条件
		Json j = new Json();
		Page<HashMap<String, Object>> page = null;//分页
		try{
			
			List<HashMap<String, Object>> serviceCarTable = new ArrayList<>();//存储MAP用
			//设定一个filter来做
			List<Filter> serviceCarFilter = new ArrayList<Filter>();
			//加入筛选
			if(groupType != null){
				serviceCarFilter.add(Filter.eq("groupType", groupType));
			}
			if(days != null){
				serviceCarFilter.add(Filter.eq("days", days));
			}
			if(star != null){
				serviceCarFilter.add(Filter.eq("star", star));
			}
			List<HyServiceFeeCar> hyServiceFeeCarList = hyServiceFeeCarService.findList(null, serviceCarFilter, null);
 			
			//遍历把信息都放进去
			if(!hyServiceFeeCarList.isEmpty())
			for(HyServiceFeeCar hyServiceFeeCarItems : hyServiceFeeCarList){
				//存储数据用 每次新建一个
				Map<String, Object> serviceCarTableItem = new HashMap<String, Object>();
				serviceCarTableItem.put("id", hyServiceFeeCarItems.getId());
				serviceCarTableItem.put("groupType", hyServiceFeeCarItems.getGroupType());
				serviceCarTableItem.put("days", hyServiceFeeCarItems.getDays());
				serviceCarTableItem.put("star", hyServiceFeeCarItems.getStar());
				serviceCarTableItem.put("price", hyServiceFeeCarItems.getPrice());
				serviceCarTable.add((HashMap<String, Object>) serviceCarTableItem);
 			}
			
			page = new Page<>(serviceCarTable, serviceCarTable.size(), pageable);
			
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());	
		}
		
		//最后返回空的json
		j.setSuccess(true);
		j.setObj(page);
		j.setMsg("更新成功");
		return j;
		
	}
	
	@RequestMapping(value = "fee/noncar")
	@ResponseBody
	public Json feeOfNonCar(LineType lineType, Boolean groupType, Integer star, Pageable pageable) {//三个筛选条件
		Json j = new Json();
		Page<HashMap<String, Object>> page = null;//分页
		try{
			
			List<HashMap<String, Object>> serviceCarTable = new ArrayList<>();//存储MAP用
			//设定一个filter来做
			List<Filter> serviceCarFilter = new ArrayList<Filter>();
			//加入筛选
			if(groupType != null){
				serviceCarFilter.add(Filter.eq("groupType", groupType));
			}
			if(lineType != null){
				serviceCarFilter.add(Filter.eq("lineType", lineType));
			}
			if(star != null){
				serviceCarFilter.add(Filter.eq("star", star));
			}
			List<HyServiceFeeNoncar> hyServiceFeeCarList = hyServiceFeeNoncarService.findList(null, serviceCarFilter, null);
 			
			//遍历把信息都放进去
			if(!hyServiceFeeCarList.isEmpty())
			for(HyServiceFeeNoncar hyServiceFeeCarItems : hyServiceFeeCarList){
				//存储数据用 每次新建一个
				Map<String, Object> serviceCarTableItem = new HashMap<String, Object>();
				serviceCarTableItem.put("id", hyServiceFeeCarItems.getId());
				serviceCarTableItem.put("groupType", hyServiceFeeCarItems.getGroupType());
				serviceCarTableItem.put("lineType", hyServiceFeeCarItems.getLineType());
				serviceCarTableItem.put("star", hyServiceFeeCarItems.getStar());
				serviceCarTableItem.put("price", hyServiceFeeCarItems.getPrice());
				serviceCarTable.add((HashMap<String, Object>) serviceCarTableItem);
 			}
			
			page = new Page<>(serviceCarTable, serviceCarTable.size(), pageable);
			
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());	
		}
		
		//最后返回空的json
		j.setSuccess(true);
		j.setObj(page);
		j.setMsg("更新成功");
		return j;
		
	}
	
	//前端上传价格
	@RequestMapping(value = "fee/car/edit")
	@ResponseBody
	public Json editFeeOfCar(Integer id, BigDecimal price) {//三个筛选条件
		Json j = new Json();
		
		HyServiceFeeCar myHyServiceFeeCar = null;
		try{
			if(id != null){
				myHyServiceFeeCar = hyServiceFeeCarService.find(id);
				if(myHyServiceFeeCar != null){
					myHyServiceFeeCar.setPrice(price);
				}
			}
		hyServiceFeeCarService.update(myHyServiceFeeCar);
			
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());	
		}
		
		//最后返回空的json
		j.setSuccess(true);
		j.setMsg("更新成功");
		return j;
		
	}
	
	
	
	@RequestMapping(value = "fee/noncar/edit")
	@ResponseBody
	public Json editFeeOfNonCar(Integer id, BigDecimal price) {//三个筛选条件
		Json j = new Json();
		
		HyServiceFeeNoncar myHyServiceFeeNonCar = null;
		try{
			if(id != null){
				myHyServiceFeeNonCar = hyServiceFeeNoncarService.find(id);
				if(myHyServiceFeeNonCar != null){
					myHyServiceFeeNonCar.setPrice(price);
				}
			}
		hyServiceFeeNoncarService.update(myHyServiceFeeNonCar);
			
			
		}catch (Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());	
		}
		
		//最后返回空的json
		j.setSuccess(true);
		j.setMsg("更新成功");
		return j;
		
	}
	
}
