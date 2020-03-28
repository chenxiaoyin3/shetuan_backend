package com.shetuan.service;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.shetuan.controller.OrganizationManagementController.WrapOrganization;
import com.shetuan.entity.Organization;

public interface OrganizationManagementService {
	public Json list(Pageable pageable,String name,boolean state);
	
	public Json detailById(Long organizationId);
	
	public boolean addOrganization(Organization organization,String username);
	
	public Json add(@RequestBody WrapOrganization wrapOrganization, HttpSession session);
	
	public Json edit(@RequestBody WrapOrganization wrapOrganization, HttpSession session);
	
	public Json deleteById(Long organizationId);
}
