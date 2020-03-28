package com.hongyu.util.qywxMessage.qywxUtilEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 消息。文本消息。
 * 为了处理json封装的函数。发消息请求参数是json格式的
 */
public class WxCpTextMessage{
  @JsonProperty("touser")
  private String toUser;
  @JsonProperty("toparty")
  private String toParty;
//  private String toTag;//2018-11-20没用上tag，就不要了
  @JsonProperty("agentid")
  private Integer agentId;
  @JsonProperty("msgtype")
  private String msgType;//这个因为只发文本消息，就写死了2018-11-20
  @JsonProperty("text")
  private TextContent textContent;
////safe	必填？否	表示是否是保密消息，0表示否，1表示是，默认0
////private String safe;//不必填的就都不填了2018-11-20
  
  //这些是其他消息类型有的东西
//  private String mediaId;
//  private String thumbMediaId;
//  private String title;
//  private String description;
//  private String musicUrl;
//  private String hqMusicUrl;
//  private String url;
//  private String btnTxt;
//  private List<NewArticle> articles = new ArrayList<>();
//  private List<MpnewsArticle> mpnewsArticles = new ArrayList<>();


public String getToUser() {
	return toUser;
}

public void setToUser(String toUser) {
	this.toUser = toUser;
}

public String getToParty() {
	return toParty;
}
/**给部门，设置部门id*/
public void setToParty(String toParty) {
	this.toParty = toParty;
}

public Integer getAgentId() {
	return agentId;
}

public void setAgentId(Integer agentId) {
	this.agentId = agentId;
}

public String getMsgType() {
	return msgType;
}

public void setMsgType(String msgType) {
	this.msgType = msgType;
}

public TextContent getTextContent() {
	return textContent;
}
/**
 * <p><strong>没必要用这个设置消息内容，用setContent(String)设置消息内容就行</strong></p>
 * 为了保证和企业微信api的消息格式一样
 * @param textContent TextContent类
 */
public void setTextContent(TextContent textContent) {
	this.textContent = textContent;
}
/**
 * <p><strong>用这个设置消息内容就行</strong></p>
 * 自己加的，不是自动生成的getter setter。  
 * 直接设置里边的内容，而且就动一个对象。
 */
@JsonIgnore
public void setContent(String content){
	if(this.getTextContent()==null){
		this.setTextContent(new TextContent());
	}
	//有没有都set
	this.getTextContent().setContent(content);
}
  
  public String toJson() {
	//这里用Jackson
    ObjectMapper om=new ObjectMapper();
    try {
		return om.writeValueAsString(this);
	} catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		//这么返回其实不好，可是怎么写好呢？
		return "WxCpTextMessage用Jackson转json错了";
	}
  }
}
class TextContent{
	@JsonProperty("content")
	private String content;

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
