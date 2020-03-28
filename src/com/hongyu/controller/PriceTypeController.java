package com.hongyu.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.PriceType;
import com.hongyu.service.PriceTypeService;

@Controller
@RequestMapping("/settings/pricetype/")
public class PriceTypeController {
	@Resource(name = "priceTypeServiceImpl")
	PriceTypeService  priceTypeService;
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(@RequestBody PriceType priceType) {
		Json j = new Json();
		try{
			priceType.setStatus(true);
			priceTypeService.save(priceType);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="cancel")
	@ResponseBody
	public Json cancel(Long id) {
		Json j = new Json();
		try{
			PriceType priceType = priceTypeService.find(id);
			priceType.setStatus(false);
			priceTypeService.update(priceType);
			j.setSuccess(true);
			j.setMsg("取消成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	@RequestMapping(value="update")
	@ResponseBody
	public Json update(@RequestBody PriceType priceType) {
		Json j = new Json();
		try{
			priceTypeService.update(priceType);
			j.setSuccess(true);
			j.setMsg("更新成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	@RequestMapping(value="list")
	@ResponseBody
	public Json list(Pageable pageable, PriceType priceType){
		Json j = new Json();
		Page<PriceType> page=priceTypeService.findPage(pageable,priceType);
		j.setSuccess(true);
		j.setMsg("查询成功");
		j.setObj(page);
		return j;
	}

	@RequestMapping(value="restore")
	@ResponseBody
	public Json restore(Long id) {
		Json j = new Json();
		try{
			PriceType priceType = priceTypeService.find(id);
			priceType.setStatus(true);
			priceTypeService.update(priceType);
			j.setSuccess(true);
			j.setMsg("恢复成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
