package com.hongyu.util.qywxMessage;

/**
 * CORP_ID  企业ID，可以写死
 * <br/>
 * 还有一些应用的id是常数。
 * <br/>
 * 有很多应用，一个应用对应一个secret。以后可能会存数据库
 */
public class QyWxConstants {
	/**企业ID*/
	public final static String CORP_ID="wwee9914edea18fc97"; 
	
	/**根据应用标识AgentId返回它的secret
	 */
	public static String getSecretByAgentId(int agentId){
		switch (agentId) {
			case WAI_BU_GONG_YING_SHANG_QYWX_APP_AGENT_ID:return WAI_BU_GONG_YING_SHANG_QYWX_APP_SECRET;
			case HONG_YU_MEN_DIAN_QYWX_APP_AGENT_ID:return HONG_YU_MEN_DIAN_QYWX_APP_SECRET;
			case ZONG_BU_CAI_WU_QYWX_APP_AGENT_ID:return ZONG_BU_CAI_WU_QYWX_APP_SECRET;
			case CAI_GOU_BU_QYWX_APP_AGENT_ID:return CAI_GOU_BU_QYWX_APP_SECRET;
			case CHAN_PIN_BU_QYWX_APP_AGENT_ID:return CHAN_PIN_BU_QYWX_APP_SECRET;
			case PIN_KONG_ZHONG_XIN_QYWX_APP_AGENT_ID:return PIN_KONG_ZHONG_XIN_QYWX_APP_SECRET;
			case XING_ZHENG_ZHONG_XIN_QYWX_APP_AGENT_ID:return XING_ZHENG_ZHONG_XIN_QYWX_APP_SECRET;
			case DAO_YOU_ZHONG_XIN_QYWX_APP_AGENT_ID:return DAO_YOU_ZHONG_XIN_QYWX_APP_SECRET;
			case FEN_GONG_SI_QYWX_APP_AGENT_ID:return FEN_GONG_SI_QYWX_APP_SECRET;
			case FU_ZONG_SHEN_HE_QYWX_APP_AGENT_ID:return FU_ZONG_SHEN_HE_QYWX_APP_SECRET;
			
			//都不是，就是没这个secret，瞎返回一个，暂时没考虑后果
			default:return "没这个secret";
		}
	}
	
	//应用标识
	/**外部供应商agentId=1000002*/
	public static final int WAI_BU_GONG_YING_SHANG_QYWX_APP_AGENT_ID=1000002;
	/**虹宇门店agentId=1000003*/
	public static final int HONG_YU_MEN_DIAN_QYWX_APP_AGENT_ID=1000003;
	/**总部财务agentId=1000004*/
	public static final int ZONG_BU_CAI_WU_QYWX_APP_AGENT_ID=1000004;
	/**采购部agentId=1000005*/
	public static final int CAI_GOU_BU_QYWX_APP_AGENT_ID=1000005;
	/**产品部agentId=1000006*/
	public static final int CHAN_PIN_BU_QYWX_APP_AGENT_ID=1000006;
	/**品控中心agentId=1000007*/
	public static final int PIN_KONG_ZHONG_XIN_QYWX_APP_AGENT_ID=1000007;
	/**行政中心agentId=1000008*/
	public static final int XING_ZHENG_ZHONG_XIN_QYWX_APP_AGENT_ID=1000008;
	/**导游中心agentId=1000009*/
	public static final int DAO_YOU_ZHONG_XIN_QYWX_APP_AGENT_ID=1000009;
	/**分公司agentId=1000010*/
	public static final int FEN_GONG_SI_QYWX_APP_AGENT_ID=1000010;
	/**副总审核agentId=1000011*/
	public static final int FU_ZONG_SHEN_HE_QYWX_APP_AGENT_ID=1000011;
	
	//应用的secret
	/**外部供应商secret*/
	public static final String WAI_BU_GONG_YING_SHANG_QYWX_APP_SECRET="Pz8M1jSUN8tUc_VD9EUeuc7kA1vVl2fTJQIPvg7L4Ok";
	/**虹宇门店secret*/
	public static final String HONG_YU_MEN_DIAN_QYWX_APP_SECRET="zDAFtufYxnbxhBtLdseulFnhHGv3wxa-Cb1Fzx9To0A";
	/**总部财务secret*/
	public static final String ZONG_BU_CAI_WU_QYWX_APP_SECRET="wwAB8C2fZQSVxmWXHeCs2TVP1vSefineXlJM6Al5BbY";
	/**采购部secret*/
	public static final String CAI_GOU_BU_QYWX_APP_SECRET="JiveSDssauTASIdAIurXEWsq_kX1Nb_6zeS1J07WVlo";
	/**产品部secret*/
	public static final String CHAN_PIN_BU_QYWX_APP_SECRET="wGHKO5k1DULcHzZwWoaRVQkKai4ey_9eJzshsGOEwhM";
	/**品控中心secret*/
	public static final String PIN_KONG_ZHONG_XIN_QYWX_APP_SECRET="cSo6mh82tIr61hm9LjExNnbs5hU0m-7uzfQ55U07r0U";
	/**行政中心secret*/
	public static final String XING_ZHENG_ZHONG_XIN_QYWX_APP_SECRET="SJ7WNGRQszBNB3RnqxcjuuK06okhODIflzz1KzRAdzg";
	/**导游中心secret*/
	public static final String DAO_YOU_ZHONG_XIN_QYWX_APP_SECRET="qVr6YaH-rAHWt1GKCsEV5cKzpV0rKsnPxXa1_VLpx3U";
	/**分公司secret*/
	public static final String FEN_GONG_SI_QYWX_APP_SECRET="u8CpnI44hcAmtbaZwN8ObQS4W6owtt6bnPtVZAAXe2s";
	/**副总审核secret*/
	public static final String FU_ZONG_SHEN_HE_QYWX_APP_SECRET="hOF2hLlLvqDl22EJdCYOYJrW01kJTy8zOVCR6ZjPBf0";
	
}