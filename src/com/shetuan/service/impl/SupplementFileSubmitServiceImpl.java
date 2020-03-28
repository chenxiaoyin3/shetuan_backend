package com.shetuan.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.grain.util.FreemarkerUtils;
import com.grain.util.SettingUtils;
import com.hongyu.Json;
import com.hongyu.Setting;
import com.shetuan.entity.Supplement;
import com.shetuan.entity.SupplementFile;
import com.shetuan.service.SupplementFileSubmitService;
import com.shetuan.service.SupplementService;

@Service("SupplementFileSubmitServiceImpl")
public class SupplementFileSubmitServiceImpl implements SupplementFileSubmitService {
	@Resource(name = "SupplementServiceImpl")
	SupplementService supplementService;

	//1GB
	static final long FILE_LIMIT = 1073741824;
		
	@Override
	public Json supplementFileSubmit(@RequestBody Supplement supplement, HttpSession session) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
//			Supplement s=new Supplement();
//			s.setSupplementSummary(supplement.getSupplementSummary());
//			s.setSupplementDescription(supplement.getSupplementDescription());
//			s.setContacts(supplement.getContacts());
//			s.setContactsPhone(supplement.getContactsPhone());
			supplement.setApplyTime(new Timestamp(System.currentTimeMillis()));
			supplement.setAuditStatus(1);
			for (SupplementFile sf : supplement.getSupplementFile()) {
				sf.setSupplement(supplement);
			}
//			s.setSupplementFile(supplement.getSupplementFile());
			supplementService.save(supplement);
			j.setSuccess(true);
			j.setMsg("提交成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("提交失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json uploadFile(HttpServletRequest request, CommonsMultipartFile file) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			if (file.getSize()>FILE_LIMIT) {
				j.setSuccess(false);
				j.setMsg("文件过大");
				j.setObj(null);
				return j;
			}
			ServletContext servletContext = request.getServletContext();
			String realPath = servletContext.getRealPath("/upload");
			System.out.println(realPath);
			File file1 = new File(realPath);
			if (!file1.exists()) {
				file1.mkdir();
			}
			OutputStream out;
			InputStream in;
			String prefix = UUID.randomUUID().toString();
			prefix = prefix.replace("-", "");
			String fileName = prefix + "_" + file.getOriginalFilename();

//			Setting setting = SettingUtils.get();
//			String uploadPath = setting.getFileUploadPath();
//			Map<String, Object> model = new HashMap<String, Object>();
//			model.put("uuid", UUID.randomUUID().toString());
//			String path = FreemarkerUtils.process(uploadPath, model);
//			String destPath = "/resources"+ path + UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
//			String destPath = "/resources"+ path + fileName;
			List<String> responseArray=new ArrayList<>();
			String destPath = "/shetuan_backend/upload/" + fileName;
			System.out.println(destPath);
			responseArray.add(destPath);
//			out= new FileOutputStream(new File(realPath+"\\"+fileName));//仅适用于windows系统 上传不了linux系统
			out = new FileOutputStream(new File(realPath + "/" + fileName));//windows linux都能上传
			in = file.getInputStream();
			IOUtils.copy(in, out);
			out.close();
			in.close();
			j.setSuccess(true);
			j.setMsg("上传成功");
			j.setObj(responseArray);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("上传 失败" + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}

		return j;
	}

}
