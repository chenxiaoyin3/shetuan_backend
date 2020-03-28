package com.hongyu.controller.hzj03;

import com.hongyu.Json;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.WechatAccountBalance;
import com.hongyu.service.WechatAccountBalanceService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by xyy on 2019/4/18.
 *
 * @author xyy
 *
 * 商城 - 用户余额变化表
 */
@Controller
@RequestMapping("/ymmall/wechataccount/balance")
public class YmmallWechatAccountBalanceController {
    @Resource(name = "wechatAccountBalanceServiceImpl")
    private WechatAccountBalanceService wechatAccountBalanceService;

    @RequestMapping("/list/view")
    @ResponseBody
    public Json getUnPaidBatchList(Pageable pageable, Long wechatid) {
        Json j = new Json();
        try {
            Page<WechatAccountBalance> res = wechatAccountBalanceService.getWechatAccountBalanceList(pageable, wechatid);
            j.setObj(res);
            j.setSuccess(true);
            j.setMsg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("操作失败");
        }
        return j;
    }
}
