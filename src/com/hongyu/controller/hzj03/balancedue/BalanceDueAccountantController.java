package com.hongyu.controller.hzj03.balancedue;

import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.service.HyPayablesElementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * Created by xyy on 2019/5/8.
 *
 * @author xyy
 *
 * 旅游元素付尾款 - 仅供会计查看
 */
@Controller
@RequestMapping("/admin/balancedueaccountant")
public class BalanceDueAccountantController {

    @Resource(name = "hyPayablesElementServiceImpl")
    private HyPayablesElementService hyPayablesElementService;

    /** 旅游元素供应商 - 列表(按单位、按团) */
    @RequestMapping("/list/view")
    @ResponseBody
    public Json getList(Pageable pageable, Integer status, String name, String startDate, String endDate, String sn,
                        Integer supplierType) {
        Json json = new Json();
        try {
            HashMap<String, Object> obj = hyPayablesElementService.getList(pageable, status, name, startDate, endDate, sn, supplierType);
            json.setObj(obj);
            json.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            json.setSuccess(false);
            json.setMsg("操作失败");
        }
        return json;
    }
}
