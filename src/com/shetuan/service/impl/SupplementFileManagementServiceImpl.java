package com.shetuan.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.shetuan.entity.Organization;
import com.shetuan.entity.Supplement;
import com.shetuan.service.SupplementFileManagementService;
import com.shetuan.service.SupplementFileService;
import com.shetuan.service.SupplementService;

@Service("SupplementFileManagementServiceImpl")
public class SupplementFileManagementServiceImpl implements SupplementFileManagementService {
	@Resource(name = "SupplementServiceImpl")
	SupplementService supplementService;

	@Resource(name = "SupplementFileServiceImpl")
	SupplementFileService supplementFileService;

	@Override
	public Json list(Pageable pageable, @DateTimeFormat(pattern = "yyyy-MM-dd") Date applyStartTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date applyEndTime, Integer auditStatus,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date auditStartTime,
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date auditEndTime, HttpSession session,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			/**
			 * 获取用户权限范围
			 */
			CheckedOperation co = (CheckedOperation) request.getAttribute("co");
			HashMap<String, Object> hm = new HashMap<String, Object>();
			List<HashMap<String, Object>> result = new ArrayList<>();
			List<Filter> filter = new ArrayList<Filter>();
			// 未审核的
			if (auditStatus.equals(0)) {
				filter.add(Filter.eq("auditStatus", 1));
			}
			// 已审核的
			else if (auditStatus.equals(1)) {
				filter.add(Filter.gt("auditStatus", 1));
			}
			if (applyStartTime != null && applyEndTime != null) {
				filter.add(Filter.ge("applyTime", applyStartTime));
				filter.add(Filter.le("applyTime", applyEndTime));
			}
			if (auditStartTime != null && auditEndTime != null) {
				filter.add(Filter.ge("auditTime", auditStartTime));
				filter.add(Filter.le("auditTime", auditEndTime));
			}
			pageable.setFilters(filter);
			Page<Supplement> page = supplementService.findPage(pageable);
			for (Supplement tmp : page.getRows()) {
				HashMap<String, Object> m = new HashMap<String, Object>();
				m.put("id", tmp.getId());
				m.put("supplementSummary", tmp.getSupplementSummary());
				m.put("supplementDescription", tmp.getSupplementDescription());
				m.put("contacts", tmp.getContacts());
				m.put("contactsPhone", tmp.getContactsPhone());
				m.put("applyTime", tmp.getApplyTime());
				m.put("auditStatus", tmp.getAuditStatus());
				m.put("auditorName", tmp.getAuditorName());
				m.put("auditTime", tmp.getAuditTime());
				// 如果角色权限是查看的话 那么这条数据就是查看
				if (co == CheckedOperation.editIndividual) {
					m.put("privilege", "view");
				} else {
					m.put("privilege", "edit");
				}
				result.add(m);
			}
			hm.put("total", page.getTotal());
			hm.put("pageNumber", page.getPageNumber());
			hm.put("pageSize", page.getPageSize());
			hm.put("result", result);
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(hm);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("获取失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json detailById(Long id) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			Supplement s = supplementService.find(id);
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("id", s.getId());
			m.put("supplementSummary", s.getSupplementSummary());
			m.put("supplementDescription", s.getSupplementDescription());
			m.put("contacts", s.getContacts());
			m.put("contactsPhone", s.getContactsPhone());
			m.put("applyTime", s.getApplyTime());
			m.put("auditStatus", s.getAuditStatus());
			m.put("auditorName", s.getAuditorName());
			m.put("auditTime", s.getApplyTime());
			m.put("auditResult", s.getAuditResult());
			m.put("supplementFiles", s.getSupplementFile());
			j.setSuccess(true);
			j.setMsg("获取成功");
			j.setObj(m);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("获取失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

	@Override
	public Json auditById(Long id, Integer auditStatus, String auditResult, HttpSession session) {
		// TODO Auto-generated method stub
		Json j = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			Supplement s = supplementService.find(id);
			s.setAuditorName(username);
			s.setAuditTime(new Timestamp(System.currentTimeMillis()));
			s.setAuditResult(auditResult);
			s.setAuditStatus(auditStatus);
			supplementService.update(s);
			j.setSuccess(true);
			j.setMsg("审核成功");
			j.setObj(null);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("审核失败: " + e.getMessage());
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}

}
