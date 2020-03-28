package com.hongyu.controller.gxz04;

import java.util.ArrayList;
import java.util.List;

/**
 * 上传文件公共接口-供下载使用
 */
import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.CommonUploadFileEntity;
import com.hongyu.service.CommonUploadFileService;

@RestController
@RequestMapping("/admin/generalsettings/upload/")
public class UploadFileController {
	@Resource(name="commonUploadFileServiceImpl")
	CommonUploadFileService uploadFileService;
	
	@RequestMapping(value="add", method = RequestMethod.POST)
	@ResponseBody
	public Json add(CommonUploadFileEntity entity) {
		Json j = new Json();
		try{
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("type", entity.getType()));
			List<CommonUploadFileEntity> oldEntity = uploadFileService.findList(null, filters, null);
			if(!oldEntity.isEmpty()) {
				CommonUploadFileEntity a = oldEntity.get(0);
				a.setFileUrl(entity.getFileUrl());
				uploadFileService.update(a);
			} else {
				uploadFileService.save(entity);
			}
			j.setSuccess(true);
			j.setMsg("添加成功！");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="detail/view", method = RequestMethod.GET)
	@ResponseBody
	public Json detail() {
		Json j = new Json();
		try{
			List<CommonUploadFileEntity> entities = uploadFileService.findAll();
			j.setSuccess(true);
			j.setMsg("查看成功！");
			j.setObj(entities);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
}
