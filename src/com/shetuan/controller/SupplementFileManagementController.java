package com.shetuan.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.service.SupplementFileManagementService;

@RestController
@RequestMapping("/admin/supplementFileManagement/")
public class SupplementFileManagementController {
	@Resource(name = "SupplementFileManagementServiceImpl")
	SupplementFileManagementService supplementFileManagementService;
	
	public static class AuditParam{
		private Long id;
		private Integer auditStatus;
		private String auditResult;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Integer getAuditStatus() {
			return auditStatus;
		}
		public void setAuditStatus(Integer auditStatus) {
			this.auditStatus = auditStatus;
		}
		public String getAuditResult() {
			return auditResult;
		}
		public void setAuditResult(String auditResult) {
			this.auditResult = auditResult;
		}
		
	}
	
	@RequestMapping(value = "list")
	@ResponseBody
	public Json list(Pageable pageable, @DateTimeFormat(pattern = "yyyy-MM-dd") Date applyStartTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date applyEndTime, Integer auditStatus,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date auditStartTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date auditEndTime, HttpSession session,
			HttpServletRequest request) {
		Json j = new Json();
		j = supplementFileManagementService.list(pageable, applyStartTime, applyEndTime, auditStatus, auditStartTime,
				auditEndTime, session, request);
		return j;
	}

	@RequestMapping(value = "detailById")
	@ResponseBody
	public Json detailById(Long id) {
		Json j = new Json();
		j = supplementFileManagementService.detailById(id);
		return j;
	}

//	@Transactional(propagation = Propagation.REQUIRED)
//	@RequestMapping(value = "auditById")
//	@ResponseBody
//	public Json auditById(Long id, Integer auditStatus, String auditResult, HttpSession session) {
//		Json j = new Json();
//		j = supplementFileManagementService.auditById(id, auditStatus, auditResult, session);
//		return j;
//	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@RequestMapping(value = "auditById")
	@ResponseBody
	public Json auditById(@RequestBody AuditParam auditParam, HttpSession session) {
		Json j = new Json();
		Long id=auditParam.getId();
		Integer auditStatus=auditParam.getAuditStatus();
		String auditResult=auditParam.getAuditResult();
		j = supplementFileManagementService.auditById(id, auditStatus, auditResult, session);
		return j;
	}
}
