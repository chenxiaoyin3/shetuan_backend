package com.hongyu.wechatpay.util;

import com.hongyu.wechatpay.ConstantsOfficialAccount;
import com.hongyu.wechatpay.util.MD5;
import com.hongyu.wechatpay.util.PayCommonUtil;
import com.hongyu.wechatpay.util.XMLUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.util.*;

public class WechatTransferTest {

    private static String certsDir = "/root/data/apiclient_cert.p12";

    public static Map<String, String> transfer(String openid, String re_user_name, String partner_trade_no, String amount, String desc) {
        Map<String, String> map = new HashMap<String, String>(); // 定义一个返回MAP
        try {
            String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
            InetAddress ia = InetAddress.getLocalHost();
            String ip = ia.getHostAddress(); // 获取本机IP地址
            String uuid = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");// 随机获取UUID
            String mch_appid = ConstantsOfficialAccount.getInstance().getAPP_ID(); // appid
            String mchid = ConstantsOfficialAccount.getInstance().getMCH_ID(); //
            String key = ConstantsOfficialAccount.getInstance().getAPI_KEY() ; // key

            // 设置支付参数
            SortedMap<Object, Object> signParams = new TreeMap<Object, Object>();

            signParams.put("mch_appid", mch_appid); // 微信分配的公众账号ID（企业号corpid即为此appId）
            signParams.put("mchid", mchid);// 微信支付分配的商户号
            signParams.put("nonce_str", uuid); // 随机字符串，不长于32位
            signParams.put("partner_trade_no", partner_trade_no); // 商户订单号，需保持唯一性
            signParams.put("openid", openid); // 商户appid下，某用户的openid
            signParams.put("check_name", "OPTION_CHECK"); // NO_CHECK：不校验真实姓名
            // FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账）
            // OPTION_CHECK：针对已实名认证的用户才校验真实姓名（未实名认证用户不校验，可以转账成功）
            signParams.put("re_user_name", re_user_name);
            signParams.put("amount", amount); // 企业付款金额，单位为分
            signParams.put("desc", desc); // 企业付款操作说明信息。必填。
            signParams.put("spbill_create_ip", ip); // 调用接口的机器Ip地址

            // 生成支付签名，要采用URLENCODER的原始值进行MD5算法！

            String sign = PayCommonUtil.createSign("UTF-8", signParams, key);
            // System.out.println(sign);
            String data = "<xml><mch_appid>";
            data += mch_appid + "</mch_appid><mchid>"; // APPID
            data += mchid + "</mchid><nonce_str>"; // 商户ID
            data += uuid + "</nonce_str><partner_trade_no>"; // 随机字符串
            data += partner_trade_no + "</partner_trade_no><openid>"; // 订单号
            data += openid + "</openid><check_name>OPTION_CHECK</check_name>"; // 是否强制实名验证
            data += "<re_user_name>" + re_user_name + "</re_user_name><amount>";
            data += amount + "</amount><desc>"; // 企业付款金额，单位为分
            data += desc + "</desc><spbill_create_ip>"; // 企业付款操作说明信息。必填。
            data += ip + "</spbill_create_ip><sign>";// 调用接口的机器Ip地址
            data += sign + "</sign></xml>";// 签名
            System.out.println(data);
            // 获取证书，发送POST请求；
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream instream = new FileInputStream(new File(certsDir)); // 从配置文件里读取证书的路径信息
            keyStore.load(instream, mchid.toCharArray());// 证书密码是商户ID
            instream.close();
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchid.toCharArray()).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            HttpPost httpost = new HttpPost(url); //
            httpost.addHeader("Connection", "keep-alive");
            httpost.addHeader("Accept", "*/*");
            httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpost.addHeader("Host", "api.mch.weixin.qq.com");
            httpost.addHeader("X-Requested-With", "XMLHttpRequest");
            httpost.addHeader("Cache-Control", "max-age=0");
            httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
            httpost.setEntity(new StringEntity(data, "UTF-8"));
            CloseableHttpResponse response = httpclient.execute(httpost);
            HttpEntity entity = response.getEntity();

            String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            EntityUtils.consume(entity);
            // 把返回的字符串解释成DOM节点
            Map resultmap = XMLUtil.doXMLParse(jsonStr);

            String returnCode = (String) resultmap.get("result_code"); // 获取返回代码
            if (returnCode.equals("SUCCESS")) { // 判断返回码为成功还是失败
                String payment_no = (String) resultmap.get("payment_no"); // 获取支付流水号
                String payment_time = (String) resultmap.get("payment_time"); // 获取支付时间
                map.put("state", returnCode);
                map.put("payment_no", payment_no);
                map.put("payment_time", payment_time);
                return map;
            } else {
                String err_code = (String) resultmap.get("err_code"); // 获取错误代码
                String err_code_des = (String) resultmap.get("err_code_des");// 获取错误描述
                map.put("state", returnCode);// state
                map.put("err_code", err_code);// err_code
                map.put("err_code_des", err_code_des);// err_code_des
                return map;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return map;
        }
    }

    public static void main(String[] args) {
        String partner_trade_no = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Map<String, String> map = transfer("ocgJPv2iTS4PAOx3mAj530gwi114","李嘉",partner_trade_no,"150","舜之药分成");
        System.out.println(map);
    }
}
