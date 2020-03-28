package com.hongyu;

/**
 * 公共参数
 * 
 */
public final class CommonAttributes {

	public static final String PORTAL_LOGIN = "portal";  //20180423  xyy  门户登录 session 使用
	public static final String Principal = "principal";		//session中的登录用户信息

	/** 日期格式配比 */
	public static final String[] DATE_PATTERNS = new String[] { "yyyy", "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss" };

	/** shopxx.xml文件路径 */
	public static final String GRAIN_XML_PATH = "/grain.xml";

	public static final  String SessionInfo =  "sessionInfo";
	
	/**
	 * 不可实例化
	 */
	private CommonAttributes() {
	}
}