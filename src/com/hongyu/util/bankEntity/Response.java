package com.hongyu.util.bankEntity;

public class Response {
	private String Plain;
	private String ResponseCode;
	private String Signature;
	public String getPlain() {
		return Plain;
	}
	public void setPlain(String plain) {
		Plain = plain;
	}
	public String getResponseCode() {
		return ResponseCode;
	}
	public void setResponseCode(String responseCode) {
		ResponseCode = responseCode;
	}
	public String getSignature() {
		return Signature;
	}
	public void setSignature(String signature) {
		Signature = signature;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Plain: "+this.getPlain()+"\n"+"ResponseCode: "+this.getResponseCode()+"\n"+"Signature: "+this.getSignature();
	}
}
