package com.hongyu.common.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.grain.service.FileService;
import com.hongyu.FileInfo.FileType;
import com.hongyu.Json;

@Controller
@RequestMapping("/resource")
public class FileController {
	@Resource(name = "fileServiceImpl")
	FileService fileService;
	
	//50MB
//	static final long FILE_LIMIT = 52428800;
	//1GB
	static final long FILE_LIMIT = 1073741824;
	
	
	@RequestMapping("/image/upload")
	@ResponseBody
	public Json uploadImages(@RequestParam MultipartFile[] files) {
		Json json = new Json();		
		
		for (MultipartFile file : files) {
			if (file.getSize()>FILE_LIMIT) {
				json.setSuccess(false);
				json.setMsg("文件过大");
				json.setObj(null);
				return json;
			}
			if (!fileService.isValid(FileType.image, file)) {
				json.setSuccess(false);
				json.setMsg("文件格式不支持");
				json.setObj(null);
				return json;
			}
		}
		
		List<String> urls = new ArrayList<String>();
		for (MultipartFile file : files) {
			String url = fileService.upload(FileType.image, file);
			if (url == null) {
				json.setSuccess(false);
				json.setMsg("服务器异常");
				json.setObj(null);
				return json;
			}
			urls.add(url);
		}
		
		json.setSuccess(true);
		json.setMsg("上传成功");
		json.setObj(urls);
		return json;
	}
	
	@RequestMapping("/image/multisize/upload")
	@ResponseBody
	public Json uploadMultisizeImages(@RequestParam MultipartFile[] files) {
		Json json = new Json();		
		
		for (MultipartFile file : files) {
			if (file.getSize()>FILE_LIMIT) {
				json.setSuccess(false);
				json.setMsg("文件过大");
				json.setObj(null);
				return json;
			}
			if (!fileService.isValid(FileType.image, file)) {
				json.setSuccess(false);
				json.setMsg("文件格式不支持");
				json.setObj(null);
				return json;
			}
		}
		
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		for (MultipartFile file : files) {
			Map<String, String> map = fileService.build(file);
			maps.add(map);
		}
		
		json.setSuccess(true);
		json.setMsg("上传成功");
		json.setObj(maps);
		return json;
		
		
	}
	
	@RequestMapping("/video/upload")
	@ResponseBody
	public Json uploadVideos(@RequestParam MultipartFile[] files) {
		Json json = new Json();		
		
		for (MultipartFile file : files) {
			if (file.getSize()>FILE_LIMIT) {
				json.setSuccess(false);
				json.setMsg("文件过大");
				json.setObj(null);
				return json;
			}
			if (!fileService.isValid(FileType.media, file)) {
				json.setSuccess(false);
				json.setMsg("文件格式不支持");
				json.setObj(null);
				return json;
			}
		}
		
		List<String> urls = new ArrayList<String>();
		for (MultipartFile file : files) {
			String url = fileService.upload(FileType.media, file);
			if (url == null) {
				json.setSuccess(false);
				json.setMsg("服务器异常");
				json.setObj(null);
				return json;
			}
			urls.add(url);
		}
		
		json.setSuccess(true);
		json.setMsg("上传成功");
		json.setObj(urls);
		return json;
	}
	
	@RequestMapping("/file/upload")
	@ResponseBody
	public Json uploadFiles(@RequestParam MultipartFile[] files) {
		Json json = new Json();		
		
		for (MultipartFile file : files) {
			if (file.getSize()>FILE_LIMIT) {
				json.setSuccess(false);
				json.setMsg("文件过大");
				json.setObj(null);
				return json;
			}
			if (!fileService.isValid(FileType.file, file)) {
				json.setSuccess(false);
				json.setMsg("文件格式不支持");
				json.setObj(null);
				return json;
			}
		}
		
		List<String> urls = new ArrayList<String>();
		for (MultipartFile file : files) {
			String url = fileService.upload(FileType.file, file);
			if (url == null) {
				json.setSuccess(false);
				json.setMsg("服务器异常");
				json.setObj(null);
				return json;
			}
			urls.add(url);
		}
		
		json.setSuccess(true);
		json.setMsg("上传成功");
		json.setObj(urls);
		return json;
	}
}
