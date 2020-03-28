package com.shetuan.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.grain.service.FileService;
import com.hongyu.Json;
import com.hongyu.FileInfo.FileType;
import com.shetuan.entity.Supplement;
import com.shetuan.entity.SupplementFile;
import com.shetuan.service.SupplementFileSubmitService;

@RestController
@RequestMapping("/shetuan_officalWebsite/")
public class SupplementFileSubmitController {
	@Resource(name = "fileServiceImpl")
	FileService fileService;
	
	//1GB
	static final long FILE_LIMIT = 1073741824;
	
	@Resource(name = "SupplementFileSubmitServiceImpl")
	SupplementFileSubmitService supplementFileSubmitService;
	
	@RequestMapping(value="fileSupplementSubmit")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED)
	public Json supplementFileSubmit(@RequestBody Supplement supplement, HttpSession session) {
		Json j=new Json();
		j=supplementFileSubmitService.supplementFileSubmit(supplement, session);
		return j;
	}
	
	@RequestMapping("upload")
	@ResponseBody
	public Json uploadFile(HttpServletRequest request,@RequestParam CommonsMultipartFile[] files){
		Json j = new Json();		
		for(CommonsMultipartFile file:files) {
			j=supplementFileSubmitService.uploadFile(request, file);
		}
		
		return j;
	}
}
