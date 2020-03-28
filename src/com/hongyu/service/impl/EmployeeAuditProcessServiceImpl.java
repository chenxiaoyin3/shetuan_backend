package com.hongyu.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.grain.service.impl.BaseServiceImpl;
import com.hongyu.entity.EmployeeAuditProcess;
import com.hongyu.service.EmployeeAuditProcessService;

@Service("employeeAuditProcessServiceImpl")
public class EmployeeAuditProcessServiceImpl extends BaseServiceImpl<EmployeeAuditProcess, Long> implements EmployeeAuditProcessService {

}
