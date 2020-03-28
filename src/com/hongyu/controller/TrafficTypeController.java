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
import com.hongyu.entity.TrafficType;
import com.hongyu.entity.TransportEntity;
import com.hongyu.service.TrafficTypeService;
import com.hongyu.service.TransportService;
/**
 * 交通类型Controller
 * @author guoxinze
 *
 */
@Controller
@RequestMapping("/admin/generalsettings/transport/")
public class TrafficTypeController {
	@Resource(name = "transportServiceImpl")
	TransportService transportService;
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(TransportEntity trafficType) {
		Json j = new Json();
		try{
			trafficType.setStatus(true);
			transportService.save(trafficType);
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
			TransportEntity trafficType = transportService.find(id);
			trafficType.setStatus(false);
			transportService.update(trafficType);
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
	public Json update(TransportEntity trafficType) {
		Json j = new Json();
		try{
			transportService.update(trafficType);
			j.setSuccess(true);
			j.setMsg("更新成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json list(Pageable pageable, TransportEntity trafficType){
		Json j = new Json();
		Page<TransportEntity> page=transportService.findPage(pageable,trafficType);
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
			TransportEntity trafficType = transportService.find(id);
			trafficType.setStatus(true);
			transportService.update(trafficType);
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
