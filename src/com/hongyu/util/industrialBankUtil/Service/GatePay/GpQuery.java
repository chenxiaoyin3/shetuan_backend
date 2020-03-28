package com.hongyu.util.industrialBankUtil.Service.GatePay;

import java.util.Map;

import com.hongyu.util.industrialBankUtil.Configure;
import com.hongyu.util.industrialBankUtil.DateTimeUtil;
import com.hongyu.util.industrialBankUtil.SignAlgorithm;
import com.hongyu.util.industrialBankUtil.Signature;
import com.hongyu.util.industrialBankUtil.Service.IPostService;

public class GpQuery extends IPostService {
    private static final String SERVICE_NAME = "cib.epay.acquire.cashier.query";
    private static final String SERVICE_VER = "03";

    public String exec(Map<String, String> params) {

        if (!params.containsKey("order_date")) {
            params.put("order_date", DateTimeUtil.getDate());
        }
        params.put("appid", Configure.getAppid());
        params.put("service", SERVICE_NAME);
        params.put("ver", SERVICE_VER);
        params.put("timestamp", DateTimeUtil.getDateTime());
        params.put("sign_type", SignAlgorithm.get(SERVICE_NAME));
        params.put("mac", Signature.generateMAC(params));

        return txn(Configure.isDevEnv() ? Configure.GP_DEV_API : Configure.GP_PROD_API, params);
    }

}
