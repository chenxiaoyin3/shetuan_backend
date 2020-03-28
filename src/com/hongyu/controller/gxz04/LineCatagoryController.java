package com.hongyu.controller.gxz04;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.LineCatagoryEntity;
import com.hongyu.service.LineCatagoryService;
/**
 * 参数设置-线路二级分类Controller
 * @author guoxinze
 *
 */
@RestController
@RequestMapping("/admin/generalsettings/linecatagory/")
public class LineCatagoryController {
	
	@Resource(name = "lineCatagoryServiceImpl")
	LineCatagoryService lineCatagoryService;
	
	@RequestMapping(value="add")
	public Json add(LineCatagoryEntity entity) {
		Json j = new Json();
		try{
			lineCatagoryService.save(entity);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable, LineCatagoryEntity entity) {
		Json j = new Json();
		try{
			Page<LineCatagoryEntity> lineCatagoryEntities = lineCatagoryService.findPage(pageable, entity);
			j.setSuccess(true);
			j.setMsg("查看成功！");
			j.setObj(lineCatagoryEntities);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="cancel")
	public Json cancel(Long id) {
		Json j = new Json();
		try{
			LineCatagoryEntity lineCatagoryEntity = lineCatagoryService.find(id);
			lineCatagoryEntity.setStatus(false);
			lineCatagoryService.update(lineCatagoryEntity);
			j.setSuccess(true);
			j.setMsg("取消成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="restore")
	public Json restore(Long id) {
		Json j = new Json();
		try{
			LineCatagoryEntity lineCatagoryEntity = lineCatagoryService.find(id);
			lineCatagoryEntity.setStatus(true);
			lineCatagoryService.update(lineCatagoryEntity);
			j.setSuccess(true);
			j.setMsg("恢复成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="update")
	public Json update(LineCatagoryEntity entity) {
		Json j = new Json();
		try{
			lineCatagoryService.update(entity, "status");
			j.setSuccess(true);
			j.setMsg("编辑成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
