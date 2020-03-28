package com.hongyu.util.qywxMessage.qywxUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtilEntity.WxAccessToken;


/**
 * 管理企业微信应用的access_token的工具类。
 * 这里用内存存access_token。
 * 	每次获取AccessToken都 <br/>
 * 1判断存在
 * 2判断过期
 * 3得到结果
 * <br/>
 * 别的方案有用缓存存AccessToken的，也有持久化存的。
 */
public class AppAccessTokenUtil {
	
	private final static int CONNECT_TIMEOUT = 5000; // in milliseconds
    private final static String DEFAULT_ENCODING = "UTF-8";
    
    private static Map<Integer, WxAccessToken> tkmap=new HashMap<>(); 
    
    /**
     * 全局的是否正在刷新access token的锁
     */
    private final static Object globalAccessTokenRefreshLock = new Object();
    
    
    /**
     * 不可实例化
     */
    private AppAccessTokenUtil(){
    }
    
    /**
     * 控制企业微信应用的access_token的获取。
     * 应用的access_token没有或者过期了就请求新的。
     * 否则就用存着的
     * 
     * @param agentId 企业微信里应用的标识
     * @return access_token 的字符串
     */
    public static String getAccessToken(int agentId){
    	//没请求过
    	//判断   1有没accesstoken，即有没有这个应用的WxAccessToken对象  2过期没有
    	if(tkmap.get(agentId)==null ||
    			tkmap.get(agentId).isAccessTokenExpired()){
    		
    		System.out.println("企业微信应用"+agentId+"的access_token是空的,or expired，获取新的");
    		return getNewAccessToken(agentId);
    	}

		System.out.println("企业微信应用"+agentId+"的access_token存在且没过期，直接返回");
    	return tkmap.get(agentId).getAccessToken(); 
    }
    
    /**
     * <p>慎用！</p>
     * 只是用在新获取access_token，需要调企业微信相关api
     * <br/>
     * 如果各方面原因，access_token不是按照最初定的时间过期，就在这里请求获得新的。
     * 也就是说这里可以强制请求，但是不一定得到新的。
     * @param agentId int类型，应用id
     * @return access_token的字符串
     * <p>
     * 2018-11-21没实现......
     * 官方文档里说，企业微信可能会出于运营需要，提前使access_token失效，
     * 开发者应实现access_token失效时重新获取的逻辑。</p>
     */
	public static String getNewAccessToken(int agentId){
		synchronized(globalAccessTokenRefreshLock){
			if(tkmap.get(agentId)==null ||
	    			tkmap.get(agentId).isAccessTokenExpired()){
				//因为要建立几个应用，一个应用对应一个secret，大概要建表来存
				//先获取secret
				//之后应该是查数据库
				String appSecret=QyWxConstants.getSecretByAgentId(agentId);
				
				/** GET， 获取access_token */
				//这个url的参数里，secret和agentId对应
				String requestUrl="https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+QyWxConstants.CORP_ID+"&corpsecret="+appSecret;
				URL url;
				try {
					url = new URL(requestUrl);
					URLConnection conn=url.openConnection();
					conn.setDoOutput(true);
		            conn.setConnectTimeout(CONNECT_TIMEOUT);
		            conn.setReadTimeout(CONNECT_TIMEOUT);
		            
		            BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream(), DEFAULT_ENCODING));
		            StringBuilder sb=new StringBuilder();
		            String line=null;
		            while((line=reader.readLine())!=null){
		            	sb.append(line);
		            	//sb.append("\r\n");
		            }
		            System.out.println("获取access_token返回的json:  "+sb.toString());//这是返回的json字符串
		            //这里用Jackson
		            ObjectMapper om=new ObjectMapper();
		            TokenResultWrap tokenResultWrap=om.readValue(sb.toString(), TokenResultWrap.class);
		            
		            //判断成功与否
		            if(tokenResultWrap.getErrcode()==0){
		            	System.out.println("获得AccessToken成功");
		            	
	//	            	存储怎么组织？
	//	            	要不要数据库存？
	//	            	程序里又怎么办？
	//	            	如果内存来存，是用什么结构？
	//	1. 一个类config所有需要存储的都存下来？一个service有一个Config的对象，一个应用有一个service
	//	   2.现在这个类里边是一个map，一个agentId对应一个accesstoken，map有所有agentId      
                /**这里更新access_token要不要锁？*/
		            	if(!tkmap.containsKey(agentId)){
		            		WxAccessToken wxAccessToken=new WxAccessToken();
		            		tkmap.put(agentId, wxAccessToken);
		            	}
		            	tkmap.get(agentId).setAccessToken(tokenResultWrap.getAccess_token());
	            		//预留200ms，模仿的，，，，，有道理。。。。
		            	//过期的时间戳基本是现在的+7200*1000ms的值
		            	tkmap.get(agentId).setExpiresTime(System.currentTimeMillis()-200+tokenResultWrap.getExpires_in()*1000);
		            }
		            else{//请求access_token有错，怎么办？
		            	//???
		            	//上边有system.out返回信息
		            }
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return tkmap.get(agentId).getAccessToken();//有错就先返回个null
	}
	
	static class TokenResultWrap{
		/** 0是成功  */
		private int errcode;
		private String errmsg;
		private String access_token;
		/**   秒    */
		private int expires_in =-1;//=-1是2018-11-19加的，不知道有没有用
		
		/** 0是成功  */
		public int getErrcode() {
			return errcode;
		}
		public void setErrcode(int errcode) {
			this.errcode = errcode;
		}
		public String getErrmsg() {
			return errmsg;
		}
		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}
		public String getAccess_token() {
			return access_token;
		}
		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}
		/**   秒    */
		public int getExpires_in() {
			return expires_in;
		}
		/**   秒    */
		public void setExpires_in(int expires_in) {
			this.expires_in = expires_in;
		}
	}
}
