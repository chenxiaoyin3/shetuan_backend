package com.hongyu.util.industrialBankUtil.Service;

import java.util.Map;

import com.hongyu.util.industrialBankUtil.Configure;
import com.hongyu.util.industrialBankUtil.DateTimeUtil;
import com.hongyu.util.industrialBankUtil.SignAlgorithm;
import com.hongyu.util.industrialBankUtil.Signature;

/**
 * 商户结算账户余额查询接口
 */
public class QueryBalance extends IPostService {

    private static final String SERVICE_NAME = "cib.epay.acquire.account.queryBalance";
    private static final String SERVICE_VER = "01";

    public String exec(Map<String, String> params) {
    	params.put("appid", Configure.getAppid());
        params.put("service", SERVICE_NAME);
        params.put("ver", SERVICE_VER);
        params.put("timestamp", DateTimeUtil.getDateTime());
        params.put("sign_type", SignAlgorithm.get(SERVICE_NAME));
        params.put("mac", Signature.generateMAC(params));
        return txn(Configure.isDevEnv() ? Configure.EP_DEV_API : Configure.EP_PROD_API, params);
    }

}
