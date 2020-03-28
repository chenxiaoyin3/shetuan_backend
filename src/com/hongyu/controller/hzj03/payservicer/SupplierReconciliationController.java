package com.hongyu.controller.hzj03.payservicer;

import com.hongyu.Json;
import com.hongyu.service.PayablesLineItemService;
import com.hongyu.service.PaymentSupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xyy on 2019/5/6.
 *
 * @author xyy
 *
 * 财务 - 供应商对账表（所有供应商确认打款单的总额、扣点总额、应打款总额、欠付金额）
 */
@Controller
@RequestMapping(value = "admin/reconciliation")
public class SupplierReconciliationController {

    @Resource(name = "paymentSupplierServiceImpl")
    private PaymentSupplierService paymentSupplierService;

    @Resource(name = "payablesLineItemServiceImpl")
    private PayablesLineItemService payablesLineItemService;

    /**
     * 供应商对账表
     * @param startDate 起始日期
     * @param endDate 结束日期
     * @param name 供应商名称
     * */
    @RequestMapping(value = "/list/view")
    @ResponseBody
    public Json getSupplierReconciliationList(String startDate, String endDate, String name) {
        Json j = new Json();
        try{
            List<HashMap<String, Object>> obj = paymentSupplierService.getSupplierReconciliationList(startDate, endDate, name);
            j.setObj(obj);
            j.setSuccess(true);
            j.setMsg("操作成功");
        }catch(Exception e){
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }

    /**
     * 供应商对账表详情
     * @param str 经过DESUtils加密的字符串
     * */
    @RequestMapping(value = "/detail/view")
    @ResponseBody
    public Json getSupplierReconciliationDetail(String str){
        Json j = new Json();
        try{
            List<HashMap<String, Object>> obj = payablesLineItemService.getSupplierReconciliationDetail(str);
            j.setObj(obj);
            j.setSuccess(true);
            j.setMsg("操作成功");
        }catch(Exception e){
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }
}
