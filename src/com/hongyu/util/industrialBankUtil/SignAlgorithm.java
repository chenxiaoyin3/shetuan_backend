package com.hongyu.util.industrialBankUtil;

import java.util.HashMap;
import java.util.Map;

public class SignAlgorithm {

    private final static String DEFAULT_SIGN_TYPE = "SHA1";
    private final static Map<String, String> signAlg = new HashMap<String, String>() {
        {
            put("cib.epay.acquire.easypay.acctAuth", "SHA1");
            put("cib.epay.acquire.easypay.quickAuthSMS", "RSA");
            put("cib.epay.acquire.checkSms", "RSA");
            put("cib.epay.acquire.easypay.cancelAuth", "SHA1");
            put("cib.epay.acquire.easypay.acctAuth.query", "SHA1");
            put("cib.epay.acquire.easypay", "RSA");
            put("cib.epay.acquire.easypay.query", "SHA1");
            put("cib.epay.acquire.easypay.refund", "RSA");
            put("cib.epay.acquire.easypay.refund.query", "SHA1");
            put("cib.epay.acquire.authAndPay", "RSA");
            put("cib.epay.acquire.easypay.quickAuth", "RSA");

            put("cib.epay.acquire.cashier.netPay", "SHA1");
            put("cib.epay.acquire.cashier.query", "SHA1");
            put("cib.epay.acquire.cashier.refund", "RSA");
            put("cib.epay.acquire.cashier.refund.query", "SHA1");

            put("cib.epay.payment.getMrch", "RSA");
            put("cib.epay.payment.pay", "RSA");
            put("cib.epay.payment.get", "RSA");

            put("cib.epay.acquire.settleFile", "SHA1");
            put("cib.epay.payment.receiptFile", "SHA1");
            put("cib.epay.payment.batchPayApi", "RSA");
            
            put("cib.epay.acquire.easypay.entrustAuth", "SHA1");
            put("cib.epay.acquire.easypay.entrustAuthQuery", "SHA1");
            put("cib.epay.acquire.easypay.entrustCancelAuth", "SHA1");
            put("cib.epay.acquire.easypay.entrustQuickAuth", "SHA1");
            put("cib.epay.acquire.quickpay.query", "SHA1");
            put("cib.epay.acquire.singleauth.query", "SHA1");
            put("cib.epay.acquire.singleauth.quickSingleAuth", "SHA1");
            put("cib.epay.acquire.batchQuickpay", "RSA");
            put("cib.epay.acquire.batchQuickQueryApi", "RSA");
            put("cib.epay.acquire.quickpay.corp.query", "RSA");
            put("cib.epay.acquire.account.queryBalance", "RSA");
            put("cib.epay.acquire.quickpay.corp.pay", "RSA");
            put("cib.epay.acquire.quickpay", "RSA");
        }
    };

    public static String get(String service) {
        if (signAlg.containsKey(service))
            return signAlg.get(service);
        else
            return DEFAULT_SIGN_TYPE;
    }


}
