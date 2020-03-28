package com.hongyu.common.controller;

import com.hongyu.wechatpay.ConstantsOfficialAccount;
import com.hongyu.wechatpay.WechatPayMainOffcialAccount;
import com.hongyu.wechatpay.bean.PayBean;
import com.hongyu.wechatpay.bean.ReqOfficialBean;
import com.hongyu.wechatpay.util.XMLUtil;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Controller
@RequestMapping("/pay/wechat")
public class WechatPayController {

    @RequestMapping(value={"/mp/{orderId}"},method=RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> detailWithWap(@PathVariable String orderId, @RequestParam Map<String, Object> params, @RequestBody Map<String, Object> models, HttpServletResponse servletResponse) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        PayBean payBean = new PayBean();
        payBean.setOrder(orderId);
        if (params.containsKey("total_fee")) {
            payBean.setAmount((String) params.get("total_fee"));
        }
        if (params.containsKey("body")) {
            payBean.setBody((String) params.get("body"));
        }
        if (params.containsKey("notify_url")) {
            payBean.setCallbackUrl((String) params.get("notify_url"));
        }
        if (params.containsKey("openid")) {
            payBean.setOpenId((String) params.get("openid"));
        }

        if (models.containsKey("total_fee")) {
            payBean.setAmount((String) models.get("total_fee"));
        }
        if (models.containsKey("body")) {
            payBean.setBody((String) models.get("body"));
        }
        if (models.containsKey("notify_url")) {
            payBean.setCallbackUrl((String) models.get("notify_url"));
        }
        if (models.containsKey("openid")) {
            payBean.setOpenId((String) models.get("openid"));
        }
        
        //回调地址，将来要修改
        //payBean.setCallbackUrl("http://www.tobyli16.com:8080/pay/wechat/notify/"+orderId);
        System.out.println("params size: " + params.size());
        System.out.println("openid: " + payBean.getOpenId());
        System.out.println("models size: "+ models.size());
        ReqOfficialBean reqBean = WechatPayMainOffcialAccount.getReqOfficial(payBean,1);

        System.out.println(payBean.getCallbackUrl());
        result.put("appId", reqBean.appId);
        result.put("timestamp", reqBean.timeStamp);
        result.put("nonceStr", reqBean.nonceStr);
        result.put("package", reqBean.packageValue);//prepayID
        result.put("signType", "MD5");
        result.put("paySign", reqBean.paySign);
        response.put("code", "1");
        response.put("result", result);
        System.out.println(response);
        return response;
    }

    
    @RequestMapping(value={"/mp/xcx/{orderId}"},method=RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> detailWithWapXcx(@PathVariable String orderId, @RequestParam Map<String, Object> params, @RequestBody Map<String, Object> models, HttpServletResponse servletResponse) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        PayBean payBean = new PayBean();
        payBean.setOrder(orderId);
        if (params.containsKey("total_fee")) {
            payBean.setAmount((String) params.get("total_fee"));
        }
        if (params.containsKey("body")) {
            payBean.setBody((String) params.get("body"));
        }
        if (params.containsKey("notify_url")) {
            payBean.setCallbackUrl((String) params.get("notify_url"));
        }
        if (params.containsKey("openid")) {
            payBean.setOpenId((String) params.get("openid"));
        }

        if (models.containsKey("total_fee")) {
            payBean.setAmount((String) models.get("total_fee"));
        }
        if (models.containsKey("body")) {
            payBean.setBody((String) models.get("body"));
        }
        if (models.containsKey("notify_url")) {
            payBean.setCallbackUrl((String) models.get("notify_url"));
        }
        if (models.containsKey("openid")) {
            payBean.setOpenId((String) models.get("openid"));
        }
        
        //回调地址，将来要修改
        //payBean.setCallbackUrl("http://www.tobyli16.com:8080/pay/wechat/notify/"+orderId);
        System.out.println("params size: " + params.size());
        System.out.println("openid: " + payBean.getOpenId());
        System.out.println("models size: "+ models.size());
        ReqOfficialBean reqBean = WechatPayMainOffcialAccount.getReqOfficial(payBean,2);

        System.out.println(payBean.getCallbackUrl());
        result.put("appId", reqBean.appId);
        result.put("timestamp", reqBean.timeStamp);
        result.put("nonceStr", reqBean.nonceStr);
        result.put("package", reqBean.packageValue);
        result.put("signType", "MD5");
        result.put("paySign", reqBean.paySign);
        response.put("code", "1");
        response.put("result", result);
        System.out.println(response);
        return response;
    }
    
    
    @RequestMapping(value={"/notify/{orderId}"},method=RequestMethod.POST)
    public void notify(@PathVariable String orderId, @RequestParam Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 读取参数
        InputStream inputStream;
        StringBuffer sb = new StringBuffer();
        inputStream = request.getInputStream();
        String s;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        in.close();
        inputStream.close();

        // 解析xml成map
        Map<String, String> m = new HashMap<String, String>();
        m = XMLUtil.doXMLParse(sb.toString());
        System.out.println("m=\n"+m);

        String resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
        BufferedOutputStream out = new BufferedOutputStream(
                response.getOutputStream());
        out.write(resXml.getBytes());
        out.flush();
        out.close();
        return;
    }

}
