package com.shetuan.service;

import com.hongyu.Json;
import com.shetuan.entity.Supplement;
import com.shetuan.entity.SupplementFile;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public interface SupplementFileSubmitService {
	public Json supplementFileSubmit(@RequestBody Supplement supplement, HttpSession session);
	
	public Json uploadFile(HttpServletRequest request,@RequestParam(value="file") CommonsMultipartFile file);
}
