package com.hongyu.util.qywxMessage.testSendMsgControllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.util.qywxMessage.QyWxConstants;
import com.hongyu.util.qywxMessage.qywxUtil.SendMessageQyWx;


@RequestMapping("/sendqywxmsg/test")
@Controller
public class SendQyWxMessageTestController {
	
	/**
	 * 测试接口通
	 */
	@RequestMapping("/hello4")
	@ResponseBody
	public String hello4(){
		return "hello4";
	}
	
	
	@RequestMapping("/sendto/twouser/waibugongyingshangapp1")
	@ResponseBody
	public String sendFengzhuangTwoUser1000002(String user1,String user2){
		List<String> users=new ArrayList<>();
		users.add(user1);
		users.add(user2);
		
		if(SendMessageQyWx.sendWxMessage(QyWxConstants.WAI_BU_GONG_YING_SHANG_QYWX_APP_AGENT_ID,users,null, "测试发消息。现在时间"+new Date()))
				return "success";
		return "failure";
	}
	
	@RequestMapping("/sendto/twouser/hongyumendianapp1")
	@ResponseBody
	public String sendFengzhuangTwoUser1000003(String user1,String user2){
		List<String> users=new ArrayList<>();
		users.add(user1);
		users.add(user2);
		
		if(SendMessageQyWx.sendWxMessage(QyWxConstants.HONG_YU_MEN_DIAN_QYWX_APP_AGENT_ID,users,null, "测试发消息。现在时间"+new Date()))
				return "success";
		return "failure";
	}
	@RequestMapping("/sendto/twouser/zongbucaiwuapp1")
	@ResponseBody
	public String sendFengzhuangTwoUser1000004(String user1,String user2){
		List<String> users=new ArrayList<>();
		users.add(user1);
		users.add(user2);
		
		if(SendMessageQyWx.sendWxMessage(QyWxConstants.ZONG_BU_CAI_WU_QYWX_APP_AGENT_ID,users,null, "测试发消息。现在时间"+new Date()))
				return "success";
		return "failure";
	}
	@RequestMapping("/sendto/twouser/caigoubuapp1")
	@ResponseBody
	public String sendFengzhuangTwoUser1000005(String user1,String user2){
		List<String> users=new ArrayList<>();
		users.add(user1);
		users.add(user2);
		
		if(SendMessageQyWx.sendWxMessage(QyWxConstants.CAI_GOU_BU_QYWX_APP_AGENT_ID,users,null, "测试发消息。现在时间"+new Date()))
				return "success";
		return "failure";
	}
	@RequestMapping("/sendto/twouser/chanpinbuapp1")
	@ResponseBody
	public String sendFengzhuangTwoUser1000006(String user1,String user2){
		List<String> users=new ArrayList<>();
		users.add(user1);
		users.add(user2);
		
		if(SendMessageQyWx.sendWxMessage(QyWxConstants.CHAN_PIN_BU_QYWX_APP_AGENT_ID,users,null, "测试发消息。现在时间"+new Date()))
				return "success";
		return "failure";
	}
	@RequestMapping("/sendto/twouser/pinkongzhongxinapp1")
	@ResponseBody
	public String sendFengzhuangTwoUser1000007(String user1,String user2){
		List<String> users=new ArrayList<>();
		users.add(user1);
		users.add(user2);
		
		if(SendMessageQyWx.sendWxMessage(QyWxConstants.PIN_KONG_ZHONG_XIN_QYWX_APP_AGENT_ID,users,null, "测试发消息。现在时间"+new Date()))
				return "success";
		return "failure";
	}
	@RequestMapping("/sendto/twouser/xingzhengzhongxinapp1")
	@ResponseBody
	public String sendFengzhuangTwoUser1000008(String user1,String user2){
		List<String> users=new ArrayList<>();
		users.add(user1);
		users.add(user2);
		
		if(SendMessageQyWx.sendWxMessage(QyWxConstants.XING_ZHENG_ZHONG_XIN_QYWX_APP_AGENT_ID,users,null, "测试发消息。现在时间"+new Date()))
				return "success";
		return "failure";
	}
	@RequestMapping("/sendto/twouser/daoyouzhongxinapp1")
	@ResponseBody
	public String sendFengzhuangTwoUser1000009(String user1,String user2){
		List<String> users=new ArrayList<>();
		users.add(user1);
		users.add(user2);
		
		if(SendMessageQyWx.sendWxMessage(QyWxConstants.DAO_YOU_ZHONG_XIN_QYWX_APP_AGENT_ID,users,null, "测试发消息。现在时间"+new Date()))
				return "success";
		return "failure";
	}
	@RequestMapping("/sendto/twouser/fengongsiapp1")
	@ResponseBody
	public String sendFengzhuangTwoUser1000010(String user1,String user2){
		List<String> users=new ArrayList<>();
		users.add(user1);
		users.add(user2);
		
		if(SendMessageQyWx.sendWxMessage(QyWxConstants.FEN_GONG_SI_QYWX_APP_AGENT_ID,users,null, "测试发消息。现在时间"+new Date()))
				return "success";
		return "failure";
	}
	@RequestMapping("/sendto/twouser/fuzongshenheapp1")
	@ResponseBody
	public String sendFengzhuangTwoUser1000011(String user1,String user2){
		List<String> users=new ArrayList<>();
		users.add(user1);
		users.add(user2);
		
		if(SendMessageQyWx.sendWxMessage(QyWxConstants.FU_ZONG_SHEN_HE_QYWX_APP_AGENT_ID,users,null, "测试发消息。现在时间"+new Date()))
				return "success";
		return "failure";
	}
}
