package com.hongyu.util.industrialBankUtil;

import java.util.HashMap;

import com.hongyu.util.industrialBankUtil.Service.QueryBalance;
import com.hongyu.util.industrialBankUtil.Service.GatePay.GpPay;
import com.hongyu.util.industrialBankUtil.Service.GatePay.GpQuery;
import com.hongyu.util.industrialBankUtil.Service.GatePay.GpRefund;
import com.hongyu.util.industrialBankUtil.Service.GatePay.GpRefundQuery;

public class EPay {

    /**
     * 网关支付交易跳转页面生成接口<br />
     * 该方法将生成跳转页面的全部HTML代码，商户直接输出该HTML代码至某个URL所对应的页面中，即可实现跳转，可以参考示例epay_redirect
     * .jsp<br />
     * [重要]各传入参数SDK都不作任何检查、过滤，请务必在传入前进行安全检查或过滤，保证传入参数的安全性，否则会导致安全问题。
     * 
     * @param order_no
     *            订单号
     * @param order_amount
     *            金额，单位元，两位小数，例：8.00
     * @param order_title
     *            订单标题
     * @param order_desc
     *            订单描述
     * @param remote_ip
     *            客户端IP地址
     * @return 跳转页面HTML代码
     */
    public static String gpPay(String order_no, String order_amount, String order_title, String order_desc,
            String remote_ip) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("order_no", order_no);
        params.put("order_amount", order_amount);
        params.put("order_title", order_title);
        params.put("order_desc", order_desc);
        params.put("order_ip", remote_ip);

        return new GpPay().build(params);
    }

    /**
     * 网关支付交易查询接口（查询指定日期）
     * 
     * @param order_no
     *            订单号
     * @param order_date
     *            订单日期，格式yyyyMMdd
     * @return json格式结果，返回结果包含字段请参看收付直通车代收接口文档
     */
    public static String gpQuery(String order_no, String order_date) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("order_no", order_no);
        params.put("order_date", order_date);

        return new GpQuery().exec(params);
    }

    /**
     * 网关支付交易查询接口（查询当天交易）
     * 
     * @param order_no
     *            订单号
     * @return json格式结果，返回结果包含字段请参看收付直通车代收接口文档
     */
    public static String gpQuery(String order_no) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("order_no", order_no);

        return new GpQuery().exec(params);
    }

    /**
     * 网关支付退款交易接口
     * 
     * @param order_no
     *            待退款订单号
     * @param order_date
     *            订单下单日期，格式yyyyMMdd
     * @param order_amount
     *            退款金额（不能大于原订单金额）
     * @return json格式结果，返回结果包含字段请参看收付直通车代收接口文档
     */
    public static String gpRefund(String order_no, String order_date, String order_amount) {
    	return gpRefund(order_no, order_date, order_amount, null);
    }
    
    /**
     * 网关支付退款交易接口
     * 
     * @param order_no
     *            待退款订单号
     * @param order_date
     *            订单下单日期，格式yyyyMMdd
     * @param order_amount
     *            退款金额（不能大于原订单金额）
     * @param trac_no
     *            商户跟踪号
     * @return json格式结果，返回结果包含字段请参看收付直通车代收接口文档
     */
    public static String gpRefund(String order_no, String order_date, String order_amount, String trac_no) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("order_no", order_no);
        params.put("order_date", order_date);
        params.put("order_amount", order_amount);
        if (trac_no != null)
        	params.put("trac_no", trac_no);

        return new GpRefund().exec(params);
    }

    /**
     * 网关支付退款交易结果查询接口（查询指定日期）
     * 
     * @param order_no
     *            退款的订单号
     * @param order_date
     *            订单下单日期，格式yyyyMMdd
     * @return json格式结果，返回结果包含字段请参看收付直通车代收接口文档
     */
    public static String gpRefundQuery(String order_no, String order_date) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("order_no", order_no);
        params.put("order_date", order_date);

        return new GpRefundQuery().exec(params);
    }

    /**
     * 网关支付退款交易结果查询接口（查询当天订单）
     * 
     * @param order_no
     *            退款的订单号
     * @return json格式结果，返回结果包含字段请参看收付直通车代收接口文档
     */
    public static String gpRefundQuery(String order_no) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("order_no", order_no);

        return new GpRefundQuery().exec(params);
    }

    /**
     * 商户结算账户余额查询
     * @return json格式结果，返回结果包含字段请参看收付直通车代收接口文档
     */
    public static String queryBalance() {
    	HashMap<String, String> params = new HashMap<String, String>();
    	return new QueryBalance().exec(params);
    }
}
