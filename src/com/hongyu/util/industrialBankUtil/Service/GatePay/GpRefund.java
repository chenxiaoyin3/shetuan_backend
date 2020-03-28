package com.hongyu.util.industrialBankUtil.Service.GatePay;

import java.util.Map;

import com.hongyu.util.industrialBankUtil.Configure;
import com.hongyu.util.industrialBankUtil.DateTimeUtil;
import com.hongyu.util.industrialBankUtil.SignAlgorithm;
import com.hongyu.util.industrialBankUtil.Signature;
import com.hongyu.util.industrialBankUtil.Service.IPostService;

/**
 * 网关支付退款交易接口
 */
public class GpRefund extends IPostService {

    private static final String SERVICE_NAME = "cib.epay.acquire.cashier.refund";
    private static final String SERVICE_VER = "02";
    private static final String SERVICE_VER3 = "03";

    public String exec(Map<String, String> params) {

        params.put("appid", Configure.getAppid());
        params.put("service", SERVICE_NAME);
        if(params.get("trac_no") != null) {
        	params.put("ver", SERVICE_VER3);
        } else {
        	params.put("ver", SERVICE_VER);
        }
        params.put("timestamp", DateTimeUtil.getDateTime());
        params.put("sign_type", SignAlgorithm.get(SERVICE_NAME));
        params.put("mac", Signature.generateMAC(params));

        return txn(Configure.isDevEnv() ? Configure.GP_DEV_API : Configure.GP_PROD_API, params);
    }

}
