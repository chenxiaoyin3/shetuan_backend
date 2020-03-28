package com.hongyu.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class SendMessage {

    public static String sendMessage(String cellphone, String usage) {

        int x;
        String t = null;
        Random r = new Random();
        while (true) {
            x = r.nextInt(999999);
            if (x > 99999) {
                System.out.println(x);
                break;
            } else continue;
        }
        try {
            if (usage.equals("0")) {
                t = URLEncoder.encode("验证码:" + x + " (有效期限10分钟) 【虹宇】", "utf-8");
            } else {
                t = URLEncoder.encode(usage + " 【虹宇】", "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";

        }
        boolean sr = sendPost("http://smsapi.c123.cn/OpenPlatform/OpenApi?", "action=sendOnce&ac=" + "1001@501054830001" + "&authkey=" + "5200D1440A978FE0C4212BA298B3D70A" + "&cgid=" + 184 + "&c=" + t + "&m=" + cellphone);
        String account = "13488689615";
        String password = "yuntaobupt1234";
        String taskId = account + "_" + new SimpleDateFormat("yyyyMMddHHss").format(new Date()) + "_http_" + Math.round((Math.random()) * 100000);
        String param = "account=" + account + "&password=" + password + "&content=" + t + "&sendtime=&phonelist=" + cellphone + "&taskId=" + taskId;
        System.out.println(param);
        String ret = sendGet("http://sms.huoni.cn:8080/smshttp/infoSend", param);
        System.out.println(ret);
        return x+"";

    }

    public static boolean sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送失败" + e);
            return false;

        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("result: "+result);
        return true;
    }

    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

}
