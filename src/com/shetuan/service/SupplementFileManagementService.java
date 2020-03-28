package com.shetuan.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;

import com.hongyu.Json;
import com.hongyu.Pageable;

public interface SupplementFileManagementService {
	public Json list(Pageable pageable, @DateTimeFormat(pattern = "yyyy-MM-dd") Date applyStartTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date applyEndTime, Integer auditStatus,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date auditStartTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date auditEndTime, HttpSession session, HttpServletRequest request);

	public Json detailById(Long id);

	public Json auditById(Long id, Integer auditStatus, String auditResult, HttpSession session);
}
