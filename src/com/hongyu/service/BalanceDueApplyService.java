package com.hongyu.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.grain.service.BaseService;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.entity.BalanceDueApply;

/**
 * @author xyy
 * */
public interface BalanceDueApplyService extends BaseService<BalanceDueApply, Long> {
	/** 付尾款审核 - 申请*/
	Json addApply(List<HashMap<String, Object>> list, HttpSession session) throws Exception;
	/** 付尾款审核 - 操作*/
	Json insertBalanceDueApply(Long id, String comment, Integer state, HttpSession session) throws Exception;
	/** (总公司、分公司)申请付尾款审核 - 列表 */
    Json balanceDueApplyReviewList(Pageable pageable, String startDate, String endDate, Integer state, String username) throws Exception;
}