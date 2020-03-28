package com.hongyu.util.qywxMessage.qywxUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.hongyu.util.qywxMessage.qywxUtilEntity.WxCpTextMessage;
import com.hongyu.util.qywxMessage.qywxUtilEntity.WxCpTextMessageSendResult;


/**
 * 微信通知。<br/>
 * 发企业微信的应用消息
 */
public class SendMessageQyWx {
	/**消息类型是"text"*/
	public static final String TEXT="text";
	
	/**
	 * 发送企业微信通知消息的函数有4个参数，如下：
	 * @param agentId int 应用id，企业微信里的应用标识；
	 * @param userIds List< String > 接收消息的用户id列表，与hy_admin的 username字段内容一致；
	 * @param departmentIds List< String > 接收消息的部门id列表，企业微信里的部门架构与虹宇后台管理系统中的部门架构一致；
	 * @param messageContent String 消息内容。
	 * @return boolean 是否成功发消息，成功true
	 * <p>其中agentId 必填；departmentIds只在向部门成员群发消息时填写，
	 * 多数情况下赋值null即可；参数userIds和departmentIds至少有一个不为null。</p>
	 */
	public static boolean sendWxMessage(int agentId, List<String> userIds, List<String> departmentIds, String messageContent){
		//获取想要的应用的access_token
				String access_token=AppAccessTokenUtil.getAccessToken(agentId);
				//构造给企业微信请求的url
				String requestUrl="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+access_token;
				
				//请求需要POST方法
				int CONNECT_TIMEOUT = 5000; // in milliseconds
			    String DEFAULT_ENCODING = "UTF-8";
				URL url;
				URLConnection conn;
				try {
					url = new URL(requestUrl);
					conn=url.openConnection();
				} catch (MalformedURLException e) {//new url的异常
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				} catch (IOException e) {//openConnection的异常
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				
				conn.setDoInput(true);
				conn.setDoOutput(true);
		        conn.setConnectTimeout(CONNECT_TIMEOUT);
		        conn.setReadTimeout(CONNECT_TIMEOUT);
		        //post请求的参数是json，需要设置
		        conn.setRequestProperty("Content-Type", "application/json");
		        
		     // 获取URLConnection对象对应的输出流
		        PrintWriter printWriter;
				try {
					printWriter = new PrintWriter(conn.getOutputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}		
				
		        // 发送请求参数
		        WxCpTextMessage message=new WxCpTextMessage();
		        message.setMsgType(TEXT);
		        
		        //这里没判断userIds和departmentIds都是null，这种情况会在这个函数最后的【结果信息】WxCpTextMessageSendResult显示，并且返回false
		        
		        //因为是String样的json值，例如"touser" : "UserID1|UserID2|UserID3"
				//这里直接把list变成这样子的String就行
		        if(userIds==null){
		        	message.setToUser(null);
		        }else{
					//试过多'|'没关系
					StringBuilder userIdsSb=new StringBuilder();
					for(String userId:userIds){
						userIdsSb.append(userId);
						userIdsSb.append('|');
					}
					message.setToUser(userIdsSb.toString());
		        }
				
		        if(departmentIds==null){
		        	message.setToParty(null);
		        }else{
					StringBuilder departmentIdsSb=new StringBuilder();
					for(String departmentId:departmentIds){
						departmentIdsSb.append(departmentId);
						departmentIdsSb.append('|');
					}
					message.setToParty(departmentIdsSb.toString());
		        }
		        
				message.setContent(messageContent);
			    message.setAgentId(agentId);
			    
			    System.out.println("【企业微信，发的消息是】"+message.toJson());
		        
			    printWriter.write(message.toJson());
		        // flush输出流的缓冲
		        printWriter.flush();
		        //开始获取数据
		        BufferedReader bis;
				try {
					bis = new BufferedReader(new InputStreamReader(conn.getInputStream(), DEFAULT_ENCODING));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
		        ByteArrayOutputStream bos = new ByteArrayOutputStream();
		        StringBuilder sb=new StringBuilder();
		        String line;
		        try {
					while((line=bis.readLine())!= null){
					    sb.append(line);
					    sb.append("\n");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
		        try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
		        System.out.println("【企业微信，结果信息】 "+sb.toString());
		        WxCpTextMessageSendResult sendResult=WxCpTextMessageSendResult.fromJson(sb.toString());
		        if(sendResult.getErrCode()==0)
		        	return true;
		        //错了返回false
		        return false;
	}
	
	/*
	请求示例：

	{
	   "touser" : "UserID1|UserID2|UserID3",
	   "toparty" : "PartyID1|PartyID2",
	   "totag" : "TagID1 | TagID2",
	   "msgtype" : "text",
	   "agentid" : 1,
	   "text" : {
	       "content" : "你的快递已到，请携带工卡前往邮件中心领取。\n出发前可查看<a href=\"http://work.weixin.qq.com\">邮件中心视频实况</a>，聪明避开排队。"
	   },
	   "safe":0
	}
	*/


	
//	/**
//	 * 现在有个重载函数，一个是两个String，另一个是两个List< String >如果都是null就会分不清
//	 * <p>发给一个人或者一个部门</p>
//	 * 微信通知的函数。4个参数<br/>
//	 * 这些参数是企业微信里的一些数据。<br/>
//	 * departmentId多数情况下不用写，写null就行<br/>
//	 * 参数userId和departmentId至少有一个不null
//	 * @param agentId int 应用id，企业微信里的应用
//	 * @param userId String 用户id，通知谁。对应hy_admin的主键
//	 * @param departmentId String 部门id，企业微信里的部门
//	 * @param messageContent String 消息内容
//	 * @return boolean 是否成功发消息，成功true
//	 */
//	public static boolean sendWxMessage(int agentId, String userId, String departmentId, String messageContent){
//		//获取想要的应用的access_token
//		String access_token=AppAccessTokenUtil.getAccessToken(agentId);
//		//构造给企业微信请求的url
//		String requestUrl="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+access_token;
//		
//		//请求需要POST方法
//		int CONNECT_TIMEOUT = 5000; // in milliseconds
//	    String DEFAULT_ENCODING = "UTF-8";
//		URL url;
//		URLConnection conn;
//		try {
//			url = new URL(requestUrl);
//			conn=url.openConnection();
//		} catch (MalformedURLException e) {//new url的异常
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		} catch (IOException e) {//openConnection的异常
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//		
//		conn.setDoInput(true);
//		conn.setDoOutput(true);
//        conn.setConnectTimeout(CONNECT_TIMEOUT);
//        conn.setReadTimeout(CONNECT_TIMEOUT);
//        //post请求的参数是json，需要设置
//        conn.setRequestProperty("Content-Type", "application/json");
//        
//     // 获取URLConnection对象对应的输出流
//        PrintWriter printWriter;
//		try {
//			printWriter = new PrintWriter(conn.getOutputStream());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}		
//        // 发送请求参数
//        WxCpTextMessage message=new WxCpTextMessage();
//        message.setMsgType(TEXT);
//		message.setToUser(userId);
//		message.setToParty(departmentId);
//		message.setContent(messageContent);
//	    message.setAgentId(agentId);
//	    
//	    System.out.println("【发的消息是】"+message.toJson());
//        
//	    printWriter.write(message.toJson());
//        // flush输出流的缓冲
//        printWriter.flush();
//        //开始获取数据
//        BufferedReader bis;
//		try {
//			bis = new BufferedReader(new InputStreamReader(conn.getInputStream(), DEFAULT_ENCODING));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        StringBuilder sb=new StringBuilder();
//        String line;
//        try {
//			while((line=bis.readLine())!= null){
//			    sb.append(line);
//			    sb.append("\n");
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//        try {
//			bos.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//        System.out.println("【结果信息】 "+sb.toString());
//        WxCpTextMessageSendResult sendResult=WxCpTextMessageSendResult.fromJson(sb.toString());
//        if(sendResult.getErrCode()==0)
//        	return true;
//        //错了返回false
//        return false;
//	}
	
}

