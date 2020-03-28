package com.hongyu.service;

import com.grain.service.BaseService;
import com.hongyu.entity.PayServicerBatch;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

/**
 * @author xyy
 * */
public interface PayServicerBatchService extends BaseService<PayServicerBatch, Long> {
    /** 待付款 批量处理列表页*/
    List<HashMap<String,Object>> getUnPaidBatchList(Integer state, String batchCode) throws Exception;
    /** 将无状态的待付款 修改为 有批次未执行*/
    void batchProcess(Long[] ids, HttpSession session, HttpServletResponse response) throws Exception;
    /** 将有批次未执行 修改为 无状态*/
    void batchCancel(Long[] ids) throws Exception;
    /** 将有批次未执行 修改为 有批次已执行*/
    void batchFinish(Long[] ids, HttpSession session) throws Exception;
}