package com.hongyu.controller.hzj03.incomeandexpenses;

import com.hongyu.Json;

import com.hongyu.service.PayServicerBatchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xyy on 2019/4/13.
 *
 * @author xyy
 *
 * 待付款的批量处理
 */
@Controller
@RequestMapping("/admin/unpaidbatch")
public class UnPaidBatchController {

    @Resource(name = "payServicerBatchServiceImpl")
    private PayServicerBatchService payServicerBatchService;

    /**
     * 待付款批量处理列表页  
     * @param state 0:无状态  1: 有批次未执行  2:有批次已执行
     * @param batchcode 批次码
     * 
     * */
    @RequestMapping("/list/view")
    @ResponseBody
    public Json getUnPaidBatchList(Integer state, String batchcode){
        Json j = new Json();
        try{
            List<HashMap<String, Object>> res = payServicerBatchService.getUnPaidBatchList(state, batchcode);
            j.setObj(res);
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
     * 无状态 ->有批次 未执行(进行批次操作)
     * */
    @RequestMapping("/process")
    public void batchProcess(Long[] ids, HttpSession session, HttpServletResponse response) {
        try {
            payServicerBatchService.batchProcess(ids, session, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 有批次 未执行 -> 无状态(取消之前的批次操作)
     * */
    @RequestMapping("/cancel")
    @ResponseBody
    public Json batchCancel(Long[] ids){
        Json j = new Json();
        try{
            payServicerBatchService.batchCancel(ids);
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
     * 有批次 未执行 -> 有批次 已执行(即已付款)
     *
     * */
    @RequestMapping("/finish")
    @ResponseBody
    public Json batchFinish(Long[] ids, HttpSession session){
        Json j = new Json();
        try{
            payServicerBatchService.batchFinish(ids, session);
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
