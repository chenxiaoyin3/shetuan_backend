package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.Pageable;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyPayablesElement;

import java.util.HashMap;

public interface HyPayablesElementService extends BaseService<HyPayablesElement, Long> {
    /**
     * 计调
     * 获取 未付款-按单位付款 未付款-按团付款 已付款-按团付款 3种列表*/
    HashMap<String, Object> getList(Pageable pageable, Integer status, String name, String startDate, String endDate, String sn,
                                    Integer supplierType, HyAdmin hyAdmin) throws Exception;
    /**
     * 会计
     * 获取 未付款-按单位付款 未付款-按团付款 已付款-按团付款 3种列表*/
    HashMap<String, Object> getList(Pageable pageable, Integer status, String name, String startDate, String endDate, String sn,
                                    Integer supplierType) throws Exception;
}
