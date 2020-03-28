package com.hongyu.util.qywxMessage.qywxUtilEntity;

import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 消息发送结果对象类。
 * 为了处理返回的json
 */
public class WxCpTextMessageSendResult{

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

  public static WxCpTextMessageSendResult fromJson(String json) {
	  ObjectMapper om=new ObjectMapper();
	  try {
		return om.readValue(json, WxCpTextMessageSendResult.class);
	} catch (JsonParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (JsonMappingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  //抛异常了，返回。。。
	  return null;
  }

  @JsonProperty("errcode")
  private Integer errCode;

  @JsonProperty("errmsg")
  private String errMsg;

  @JsonProperty("invaliduser")
  private String invalidUser;//可能是"user1 | user2 | user3"这么个串，不管了

  @JsonProperty("invalidparty")
  private String invalidParty;

  @JsonProperty("invalidtag")
  private String invalidTag;

public Integer getErrCode() {
	return errCode;
}

public void setErrCode(Integer errCode) {
	this.errCode = errCode;
}

public String getErrMsg() {
	return errMsg;
}

public void setErrMsg(String errMsg) {
	this.errMsg = errMsg;
}

public String getInvalidUser() {
	return invalidUser;
}

public void setInvalidUser(String invalidUser) {
	this.invalidUser = invalidUser;
}

public String getInvalidParty() {
	return invalidParty;
}

public void setInvalidParty(String invalidParty) {
	this.invalidParty = invalidParty;
}

public String getInvalidTag() {
	return invalidTag;
}

public void setInvalidTag(String invalidTag) {
	this.invalidTag = invalidTag;
}  
}
