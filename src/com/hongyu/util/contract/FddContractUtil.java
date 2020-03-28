package com.hongyu.util.contract;

import java.io.File;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fadada.sdk.client.FddClientBase;
import com.fadada.sdk.client.FddClientExtra;
import com.fadada.sdk.client.request.SignResultQueryRequest;
import com.fadada.sdk.util.crypt.FddEncryptTool;
import com.fadada.sdk.util.http.HttpsUtil;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.FddContract;
import com.hongyu.entity.FddContractTemplate;
import com.hongyu.entity.FddDayTripContract;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.service.FddContractTemplateService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.util.Constants;
import com.hongyu.util.DateUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.internal.ws.handler.ServerSOAPHandlerTube;


/**
 * 合同流程
 * 1、获取客户的customerId
 * 2、上传合同模板（如果用现有的模板则可以不用上传）
 * 3、根据填充的合同内容填入合同模板生成新的合同
 * 4、调用自动签章签虹宇的章
 * 5、调用手动签章，让客户签章
 * 6、检查签章结果，归档。
 * @author li_yang
 *
 */
public class FddContractUtil {	
	private final String APP_ID = Constants.FDD_APP_ID;
	private final String APP_SECRET = Constants.FDD_APP_SECRET;
	private final String VERSION = Constants.FDD_VERSION;
	private final String URL = Constants.FDD_URL;
	private final String HYNAME = Constants.FDD_HYNAME;//公司名称：	河北虹宇国际旅行社有限公司
	private final String HYCUSTOMERID = Constants.FDD_HYCUSTOMERID;//客户编号：	28A5FF14BDE80CB145E61E0DA9E01444
	private FddClientBase clientBase;
	private FddClientExtra clientExtra;
	private static FddContractUtil fddContractUtil;
	/**
	 * 私有构造函数，为了构建单例
	 */
	private FddContractUtil(){	
		this.clientBase = new FddClientBase(APP_ID, APP_SECRET, VERSION, URL);
		this.clientExtra = new FddClientExtra(APP_ID, APP_SECRET, VERSION, URL);
	}
	/**
	 * 获取法大大合同工具类对象(单例)
	 * @return 工具类对象
	 */
	public  static FddContractUtil getInstance(){
		if(fddContractUtil == null){
			fddContractUtil = new FddContractUtil();
		}
		return fddContractUtil;
		
	}
//	public static void main(String[] args){
//		//先获取fdd工具类实例
//		FddContractUtil fcu = FddContractUtil.getFddContractUtil();
//		/*为签署人获取CA证书号，即customerId*/
////		CARequestEntity cEntity = new CARequestEntity();
////		cEntity.setEmail("123456@163.com");
////		cEntity.setIdCard("622827199512234938");
////		cEntity.setIdenType("0");
////		cEntity.setName("倾城");
////		cEntity.setPhone("13366021228");
////		String customerId = fcu.getCustomId(cEntity);
////		if(customerId == null){
////			System.out.println("获取失败");
////		}
////		System.out.println(customerId);
//		String customerId = "D96D2BB5804EE1202B794D8EC78FE395";
//		
//		
//	}
	/**
	 * 获取用户的CA证书号码即customer_id
	 * @param caRequestEntity	请求实体。
	 * @return
	 */
	public String getCustomerCAId(CARequestEntity caRequestEntity){
		String customer_id="";
		//此处实现申请逻辑
		String response = this.clientBase.invokeSyncPersonAuto(caRequestEntity.getName(),caRequestEntity.getEmail(),
				caRequestEntity.getIdCard(), caRequestEntity.getIdenType(), caRequestEntity.getPhone());
		System.out.println(response);
		JSONObject jsonObject = JSONObject.parseObject(response);
		//System.out.println(jsonObject.get("customer_id"));
		customer_id = (String) jsonObject.get("customer_id");
		
		return customer_id; 
	}
	/**
	 * 上传合同模板
	 * @param file	pdf文件
	 * @param templateId	模板id
	 * @return
	 */
	public Boolean uploadTemplate(File file,String templateId){
		String response = this.clientBase.invokeUploadTemplate(templateId, file, null);
		System.out.println(response);
		JSONObject jsonObject = JSONObject.parseObject(response);
		String result = (String) jsonObject.get("result");
		if(result.equals("success")){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 生成境内或境外合同
	 * @param fddcontract
	 * @return
	 */
	public Json generateContract(FddContract fddContract,FddContractTemplate template,HashMap<String, Object> tables){
		GenerateContractRequest gcr = fC2Gcr(fddContract,template,tables);
		Json json = new Json();
		String response = clientBase.invokeGenerateContract(gcr.getTemplateId(), gcr.getContractId(), gcr.getDocTitle(), 
				null, null, gcr.getParamter_map(), gcr.getDynamic_table());
		//System.out.println(response);
		JSONObject jsonObject = JSONObject.parseObject(response);
		String result = (String) jsonObject.get("result");
		if(result.equals("success")){
			json.setSuccess(true);
			json.setMsg("生成合同成功");
			HashMap<String,Object> map = new HashMap<>();
			map.put("download_url", jsonObject.get("download_url"));
			map.put("viewpdf_url", jsonObject.get("viewpdf_url"));
			json.setObj(map);
			return json;
		}else{
			json.setSuccess(false);
			json.setMsg((String)jsonObject.get("msg"));
			return json;
		}	
	}
	/**
	 * 生成一日游合同
	 * @param fddContract
	 * @param template
	 * @param tables
	 * @return
	 */
	public Json generateDayTripContract(FddDayTripContract fddDayTripContract,FddContractTemplate template,HashMap<String, Object> tables){
		GenerateContractRequest gcr = fDTC2Gcr(fddDayTripContract,template,tables);
		Json json = new Json();
		String response = clientBase.invokeGenerateContract(gcr.getTemplateId(), gcr.getContractId(), gcr.getDocTitle(), 
				null, null, gcr.getParamter_map(), gcr.getDynamic_table());
		//System.out.println("生成结果："+response);
		JSONObject jsonObject = JSONObject.parseObject(response);
		String result = (String) jsonObject.get("result");
		if(result.equals("success")){
			json.setSuccess(true);
			json.setMsg("生成合同成功");
			HashMap<String,Object> map = new HashMap<>();
			map.put("download_url", jsonObject.get("download_url"));
			map.put("viewpdf_url", jsonObject.get("viewpdf_url"));
			json.setObj(map);
			return json;
		}else{
			json.setSuccess(false);
			json.setMsg((String)jsonObject.get("msg"));
			return json;
		}	
	}
	
	/**
	 * 自动签章
	 * @param autoSignRequest
	 * @return
	 */
	public Json autoSignContract(AutoSignRequest autoSignRequest){
		Json json = new Json();
		String response = clientBase.invokeExtSignAuto(autoSignRequest.getTransactionId(),
				autoSignRequest.getCustomerId(), autoSignRequest.getClientRole(),
				autoSignRequest.getContractId(), autoSignRequest.getDocTitle(),
				autoSignRequest.getKeyword(), autoSignRequest.getKeyStrategy(),
				autoSignRequest.getNotifyUrl());
		System.out.println(response);
		JSONObject jsonObject = JSONObject.parseObject(response);
		String result = (String) jsonObject.get("result");
		if(result.equals("success")){
			json.setSuccess(true);
			json.setMsg("虹宇自动签章成功");
			HashMap<String,Object> map = new HashMap<>();
			map.put("download_url", jsonObject.get("download_url"));
			map.put("viewpdf_url", jsonObject.get("viewpdf_url"));
			json.setObj(map);
			return json;
		}else{
			json.setSuccess(false);
			json.setMsg((String)jsonObject.get("msg"));
			return json;
		}	
	}
	/**
	 * 指定坐标的签章方式（用于一日游签章）
	 * @param autoSignRequest
	 * @return
	 */
	public Json autoSignDayTripContract(AutoSignRequest autoSignRequest){
		Json json = new Json();
		String response = clientBase.invokeExtSignAutoXY(autoSignRequest.getTransactionId(),
				autoSignRequest.getCustomerId(), autoSignRequest.getClientRole(),
				autoSignRequest.getContractId(), autoSignRequest.getDocTitle(),
				autoSignRequest.getSignaturePositions(), autoSignRequest.getNotifyUrl());
		//System.out.println(response);
		JSONObject jsonObject = JSONObject.parseObject(response);
		String result = (String) jsonObject.get("result");
		if(result.equals("success")){
			json.setSuccess(true);
			json.setMsg("虹宇自动签章成功");
			HashMap<String,Object> map = new HashMap<>();
			map.put("download_url", jsonObject.get("download_url"));
			map.put("viewpdf_url", jsonObject.get("viewpdf_url"));
			json.setObj(map);
			return json;
		}else{
			json.setSuccess(false);
			json.setMsg((String)jsonObject.get("msg"));
			return json;
		}	
	}
	
	/**
	 * 手动签章
	 * 主要用于客户签章
	 * 通过设置method来设置是否设置有效期
	 * @param signRequest
	 * @return
	 */
	public String extSign(SignRequest signRequest){
		String sign_url = "";
		if(signRequest.getMethod()){
			sign_url = this.clientExtra.invokeExtSignValidation(signRequest.getTransactionId(), signRequest.getCustomerId(), 
					signRequest.getContractId(),signRequest.getDocTitle(), null,
					signRequest.getValidity(),signRequest.getQuantity(),
					signRequest.getReturnUrl(), signRequest.getNotifyUrl());
		}else{
			sign_url = this.clientBase.invokeExtSign(signRequest.getTransactionId(), signRequest.getCustomerId(), 
					signRequest.getContractId(),signRequest.getDocTitle(), null,
					signRequest.getReturnUrl(), signRequest.getNotifyUrl());
		}
		return sign_url;
	} 
	/**
	 * 手动签章(发送短信直接链接手动签署)
	 * 主要用于客户签章
	 * @param signRequest
	 * @return
	 */
	public String pushDoc_extSign(SignRequest signRequest){
		String baseUrl = Constants.FDD_URL+"pushdoc_extsign.api";
		
		String timeStamp = HttpsUtil.getTimeStamp();
        StringBuilder sb = new StringBuilder(baseUrl);
        try {
            String msgDigest;
            // Base64(SHA1(app_id+md5(timestamp)+SHA1(app_secret+ contract_id+transaction_id+push_type+customer_id+sign_keyword)))
            String sha1 = FddEncryptTool.sha1(APP_ID + FddEncryptTool.md5Digest(timeStamp) + FddEncryptTool.sha1(APP_SECRET +signRequest.getContractId()
            			+signRequest.getTransactionId()+"1"+ signRequest.getCustomerId()+signRequest.getKeyword()));
            msgDigest = new String(FddEncryptTool.Base64Encode(sha1.getBytes()));

            sb.append("?app_id=").append(APP_ID);
            sb.append("&v=").append(VERSION);
            sb.append("&timestamp=").append(timeStamp);
            //将push_type默认设置为1，表示一定要发送短信
            sb.append("&push_type=").append("1");
            sb.append("&transaction_id=").append(signRequest.getTransactionId());
            sb.append("&customer_id=").append(signRequest.getCustomerId());
            sb.append("&contract_id=").append(signRequest.getContractId());
            sb.append("&sign_keyword=").append(URLEncoder.encode(signRequest.getKeyword(), HttpsUtil.charset));
            /*增加坐标定位   2019.4.11  by liyang*/
//            sb.append("&position_type=").append(signRequest.getPositionType());
//            sb.append("&signature_positions=").append(URLEncoder.encode(signRequest.getSignaturePositions(), HttpsUtil.charset));
            sb.append("&doc_title=").append(URLEncoder.encode(signRequest.getDocTitle(), HttpsUtil.charset));
//	            sb.append("&return_url=").append(URLEncoder.encode(return_url, HttpsUtil.charset));
//	            sb.append("&notify_url=").append(URLEncoder.encode(notify_url, HttpsUtil.charset));
            sb.append("&return_url=").append(URLEncoder.encode(signRequest.getReturnUrl(), HttpsUtil.charset));
            sb.append("&notify_url=").append(URLEncoder.encode(signRequest.getNotifyUrl(), HttpsUtil.charset));
            sb.append("&msg_digest=").append(msgDigest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return sb.toString();
	} 
 
	/**
	 * 查询合同签署状态
	 * @param request
	 * @return
	 */
	public Json querySignResult(SignResultQueryRequest request){
		Json json  = new Json();
//		SignResultQueryRequest request = new SignResultQueryRequest();
//		request.setContractId("3");
//		request.setCustomerId("28A5FF14BDE80CB145E61E0DA9E01444");
//		request.setTransactionId("order4");
		String response = clientBase.invokeQuerySignResult(request);
		//System.out.println(response);
		JSONObject jsonObject = JSONObject.parseObject(response);
		String code = (String)jsonObject.get("code");
		if(code.equals("1")){
			JSONObject data = JSONObject.parseObject((String)jsonObject.get("data")); 
			String result = data.getString("result");
			if(result.equals("3000")){
				json.setSuccess(true);
				json.setMsg("签章成功");
				HashMap<String,Object> map = new HashMap<>();
				map.put("download_url", data.get("download_url"));
				map.put("view_url", data.get("view_url"));
				json.setObj(map);
				return json;
			}else if(result.equals("9999")){
				json.setSuccess(true);
				json.setMsg("待签署");
				return json;
			}
			else{
				json.setSuccess(false);
				json.setMsg("签章失败");
				return json;
			}
		}else{
			json.setSuccess(false);
			json.setMsg("获取失败");
			return json;
		}
		
	}
	/**
	 * 调用法大大归档接口将合同归档。
	 * @param contractId
	 * @return
	 */
	public Boolean ContractFiling(String contractId){
		String response = this.clientBase.invokeContractFilling(contractId);
		//System.out.println(response);
		JSONObject jsonObject = JSONObject.parseObject(response);
		String code =  jsonObject.getString("code");
		if(code.equals("1000")){
			return true;
		}else{
			return false;
		}		
	}
	/**
	 * 修改客户的信息，仅限邮箱和电话号码
	 * @param customerId
	 * @param email
	 * @param mobile
	 * @return
	 */
	public Boolean ChangeCustomerInfo(String customerId,String email,String mobile){
		String response = this.clientExtra.invokeInfoChange(customerId, email, mobile);
		//System.out.println(response);
		JSONObject jsonObject = JSONObject.parseObject(response);
		String code = jsonObject.getString("code");
		if(code.equals("1000")){
			return true;
		}else{
			return false;	
		}
	}
	/**
	 * 获取指定合同号的合同hash值
	 * @param contractId
	 * @return
	 */
	public String getContractHash(String contractId){
		String response = this.clientExtra.invokeGetContractHash(contractId);
		//System.out.println(response);
		JSONObject jsonObject = JSONObject.parseObject(response);
		String code = jsonObject.getString("code");
		if(code.equals("1000")){
			JSONObject data = jsonObject.getJSONObject("data");
			String hash = data.getString("fileHash");
			return hash;
		}else{
			return null;
		}	
	}
	/**
	 * 封装境内和境外合同需要填充的信息。
	 * @return
	 */
	public FddContract hyOrder2fddContract(HyOrder hyOrder){
		
		FddContract fddContract = new FddContract();;
		try {			
			//保存订单id
			fddContract.setOrderId(hyOrder.getId());
			//合同类型   0--国内一日游  1--国内游  2--境外游
			Integer contractType = 0;
			if(hyOrder.getXianlutype()!=null){
				if(hyOrder.getXianlutype()>1){
					contractType = 2;
					//生成合同号，chujing前缀“CJ”+orderId
					fddContract.setContractId("CJ"+hyOrder.getOrderNumber());
				}else{
					contractType = 1;
					//生成合同号，guonei前缀“GN”+orderId
					fddContract.setContractId("GN"+hyOrder.getOrderNumber());
				}
			}	
			fddContract.setType(contractType);
			//团里人数
			fddContract.setCustomerNum(hyOrder.getPeople());
			//除保险外的订单结算价
			fddContract.setTotalPrice(hyOrder.getJiusuanMoney());
			//发团日期
			fddContract.setStartTime(hyOrder.getFatuandate());
			//出团天数
			fddContract.setDays(hyOrder.getTianshu());
//			//设置虹宇的常量信息
			fddContract.setHyAddress(ContractInfoAboutHy.hyAddress);
			fddContract.setHyEmail(ContractInfoAboutHy.hyEmail);
			fddContract.setHyId(ContractInfoAboutHy.hyId);
			fddContract.setHyName(ContractInfoAboutHy.hyName);
			fddContract.setHyPhone(ContractInfoAboutHy.hyPhone);
			fddContract.setHyPostcode(ContractInfoAboutHy.hyPostcode);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("hyOrder2fddContract出错"+e.getMessage());
		}		
		return fddContract;
	}
	public FddDayTripContract hyOrder2fddDayTripContract(HyOrder hyOrder){
		
		FddDayTripContract fddDayTripContract = new FddDayTripContract();
		try {
			
			//生成合同号，danxian前缀“DX”orderId
			fddDayTripContract.setContractId("DX"+hyOrder.getOrderNumber());
			//保存订单id
			fddDayTripContract.setOrderId(hyOrder.getId());
			//团里人数
			fddDayTripContract.setCustomerNum(hyOrder.getPeople());
			//除保险外的订单结算价
			fddDayTripContract.setTotalPrice(hyOrder.getJiusuanMoney());
			
			//设置虹宇的常量信息
			fddDayTripContract.setHyAddress(ContractInfoAboutHy.hyAddress);
			fddDayTripContract.setHyEmail(ContractInfoAboutHy.hyEmail);
			fddDayTripContract.setHyId(ContractInfoAboutHy.hyId);
			fddDayTripContract.setHyName(ContractInfoAboutHy.hyName);
			fddDayTripContract.setHyPhone(ContractInfoAboutHy.hyPhone);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("hyOrder2fddContract出错"+e.getMessage());
		}		
		return fddDayTripContract;
	}
	/**
	 * 将合同实体转化成对应的gcr实体
	 * @param fddContract
	 * @return
	 */
	public GenerateContractRequest fC2Gcr(FddContract fddContract,FddContractTemplate template,HashMap<String, Object> tables){
		GenerateContractRequest gcr = new GenerateContractRequest();
		try {
//			List<Filter> filters = new ArrayList<>();
//			System.out.println("gcr中fdd.type="+fddContract.getType());
//			filters.add(Filter.eq("type", 1));
//			FddContractTemplate template = fddContractTemplateService.findList(null,filters,null).get(0);
			gcr.setTemplateId(template.getTemplateId());
			//设置生成的合同的名称。使用合同模板名+时间
			gcr.setDocTitle(template.getTemplateName()+DateUtil.getfileDate(new Date()));
			//设置合同编号
			gcr.setContractId(fddContract.getContractId());
			//设置上传的字段表
			JSONObject paramter_map = new JSONObject();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			if (fddContract.getCustomer()!=null) {
				paramter_map.put("customer", fddContract.getCustomer());
			}
			if(fddContract.getHyName()!=null){
				paramter_map.put("hyName", fddContract.getHyName());
			}
			if(fddContract.getCustomerNum()!=null){
				paramter_map.put("customerNum", fddContract.getCustomerNum().toString());
			}
			if(fddContract.getHyId()!=null){
				paramter_map.put("hyId", fddContract.getHyId());
			}
			if(fddContract.getContractId()!=null){
				paramter_map.put("contractId", fddContract.getContractId());
			}
			
			
			if(fddContract.getStartTime()!=null){
				paramter_map.put("startTime", df.format(fddContract.getStartTime()));
			}
			if(fddContract.getEndTime()!=null){
				paramter_map.put("endTime", df.format(fddContract.getEndTime()));
			}
			if(fddContract.getDays()!=null){
				paramter_map.put("groupDays", fddContract.getDays().toString());
			}
			if(fddContract.getStayAtHotelDays()!=null){
				paramter_map.put("stayAtHotelDays", fddContract.getStayAtHotelDays().toString());
			}
			if(fddContract.getAdultTicketPrice()!=null){	
				paramter_map.put("adultTicketPrice", fddContract.getAdultTicketPrice().toString());
			}
			if(fddContract.getChildrenTicketPrice()!=null){
				paramter_map.put("childrenTicketPrice", fddContract.getChildrenTicketPrice().toString());
			}
			if(fddContract.getGuideServiceFee()!=null){
				paramter_map.put("guideServiceFee", fddContract.getGuideServiceFee().toString());
			}
			if(fddContract.getTotalPrice()!=null){
				paramter_map.put("totalPrice", fddContract.getTotalPrice().toString());
			}
			if(fddContract.getPaymentTime()!=null){
				paramter_map.put("paymentTime", df.format(fddContract.getPaymentTime()));
			}
			if(fddContract.getPaymentMethod()!=null){
				// 1.转账 2.支付宝 3.微信支付 4.现金 5.预存款 6.刷卡
				String method = "";
				switch (fddContract.getPaymentMethod()) {
				case 1:
					method = "转账";
					break;
				case 2:
					method = "支付宝";
					break;
				case 3:
					method = "微信支付";
					break;
				case 4:
					method = "现金";
					break;
				case 5:
					method = "预存款";
					break;
				case 6:
					method = "刷卡";
					break;
				default:
					method = "其他方式";
					break;
				}
				paramter_map.put("paymentType", method);	
			}
			if(fddContract.getBuyInsuranceType()!=null){
				paramter_map.put("buyInsuranceType", fddContract.getBuyInsuranceType().toString());
			}
			if(fddContract.getInsuranceName()!=null){
				paramter_map.put("insuranceName", fddContract.getInsuranceName());
			}
			if(fddContract.getMinGroupPersonNum()!=null){
				paramter_map.put("minGroupPersonNum", fddContract.getMinGroupPersonNum().toString());
			}
				
			if(fddContract.getCancelMethod1()==1){
				paramter_map.put("cancelGroupMethod1","同意");
			}else{
				paramter_map.put("cancelGroupMethod1","不同意");
			}
			paramter_map.put("otherTravelAgency", fddContract.getOtherTravelAgency());
			if(fddContract.getCancelMethod2()==1){
				paramter_map.put("cancelGroupMethod2","同意");
			}else{
				paramter_map.put("cancelGroupMethod2","不同意");
			}
			if(fddContract.getCancelMethod3()==1){
				paramter_map.put("cancelGroupMethod3","同意");
			}else{
				paramter_map.put("cancelGroupMethod3","不同意");
			}
			if(fddContract.getCancelMethod4()==1){
				paramter_map.put("cancelGroupMethod4","同意");
			}else{
				paramter_map.put("cancelGroupMethod4","不同意");
			}
			if(fddContract.getIfPintuan()==1){
				paramter_map.put("ifPintuan", "同意");
			}else{
				paramter_map.put("ifPintuan", "不同意");
			}
			
			if(fddContract.getPintuanTravelAgency()!=null){
				paramter_map.put("pintuanTravelAgency", fddContract.getPintuanTravelAgency());
			}
			if(fddContract.getJudgementType()!=null){
				paramter_map.put("judgementType", fddContract.getJudgementType().toString());
			}
			if(fddContract.getArbitrationCommittee()!=null){
				paramter_map.put("arbitrationCommittee", fddContract.getArbitrationCommittee());
			}
			if(fddContract.getOtherNote()!=null){
				paramter_map.put("otherNote", fddContract.getOtherNote());
			}
			if(fddContract.getContractNum()!=null){
				paramter_map.put("contractNum", fddContract.getContractNum().toString());
			}
			if(fddContract.getOneHaveNum()!=null){
				paramter_map.put("oneHaveNum", fddContract.getOneHaveNum().toString());
			}
			
			paramter_map.put("customerSign","");
			if(fddContract.getCustomerIDNum()!=null){
				paramter_map.put("customerIDNum", fddContract.getCustomerIDNum());
			}
			if(fddContract.getHyOperator()!=null){
				paramter_map.put("hyOperator", fddContract.getHyOperator());
			}
			if(fddContract.getCustomerAddress()!=null){
				paramter_map.put("customerAddress", fddContract.getCustomerAddress());
			}
			if(fddContract.getHyAddress()!=null){
				paramter_map.put("hyAddress", fddContract.getHyAddress());
			}
			if(fddContract.getCustomerPhone()!=null){
				paramter_map.put("customerPhone", fddContract.getCustomerPhone());
			}
			if(fddContract.getHyPhone()!=null){
				paramter_map.put("hyPhone", fddContract.getHyPhone());
			}
			if(fddContract.getCustomerPostcode()!=null){
				paramter_map.put("customerPostcode", fddContract.getCustomerPostcode());
			}
			if(fddContract.getHyPostcode()!=null){
				paramter_map.put("hyPostcode", fddContract.getHyPostcode());
			}
			if(fddContract.getCustomerEmail()!=null){
				paramter_map.put("customerEmail", fddContract.getCustomerEmail());
			}
			if(fddContract.getHyEmail()!=null){
				paramter_map.put("hyEmail", fddContract.getHyEmail());
			}
			if(fddContract.getCustomerSignTime()!=null){
				paramter_map.put("customerSignTime", df.format(fddContract.getCustomerSignTime()));
			}
			if(fddContract.getHySignTime()!=null){
				paramter_map.put("hySignTime", df.format(fddContract.getHySignTime()));
			}
			if(fddContract.getSignAddress()!=null){
				paramter_map.put("signAddress", fddContract.getSignAddress());
			}
			paramter_map.put("hyComplaintsHotline", "12301");

			gcr.setParamter_map(paramter_map.toJSONString());
			/*******以下是动态表单********/
			
			JSONArray dynamic_tables = new JSONArray();
			
			
			//附件一：游客名单表
			List<HashMap<String, Object>> customers = (List<HashMap<String, Object>>) tables.get("orderCustomers");
			if(!customers.isEmpty()){
				JSONObject orderCustomers = new JSONObject();
				/**设置表格插入的方式为按关键字插入**/
				orderCustomers.put("insertWay", 1);
				/**设置表格插入到模板的页数**/
				//orderCustomers.put("pageBegin", 17);
				/**设置表格插入的关键字**/
				orderCustomers.put("keyword", "附件一：游客名单表");
				/**设置正文的行高**/
				orderCustomers.put("cellHeight", 25);
				/**设置表格标题**/
				//orderCustomers.put("theFirstHeader", "附件1");
				/**设置表头文字居中**/
				orderCustomers.put("headersAlignment", 1);
				/**设置表头字段**/
				orderCustomers.put("headers", new String[]{"姓名","证件号码","性别","民族","是否儿童","手机号码","身体状况"});
				/**设置单元格文字水平居中**/
				orderCustomers.put("cellHorizontalAlignment", 1);
				/**设置单元格文字垂直居中**/
				orderCustomers.put("cellVerticalAlignment", 5);
				/**设置列宽比例**/
				orderCustomers.put("colWidthPercent", new int[]{2,5,2,2,4,4,4});
				
				String[][] datas = new String[customers.size()][7];
				for(int i=0;i<customers.size();i++){
					String[] customer = new String[7];
					HashMap<String, Object> hMap = customers.get(i);
					customer[0] = (String) hMap.get("name");
					customer[1] = (String) hMap.get("certificate");
					customer[2] = (String) hMap.get("gender");
					customer[3] = (String) hMap.get("national");
					customer[4] = (String) hMap.get("ifChildren");
					customer[5] = (String) hMap.get("phone");
					customer[6] = (String) hMap.get("health");
					datas[i] = customer;
				}
				/**设置表格数据**/
				orderCustomers.put("datas", datas);
				dynamic_tables.add(orderCustomers);
			}
			
			//附件二：线路行程单
			
			List<HashMap<String, Object>> travels = (List<HashMap<String, Object>>) tables.get("lineTravels");
			if(!travels.isEmpty()){
				JSONObject lineTravels = new JSONObject();
				String linePn = (String) travels.get(0).get("linePn");
				String lineName = (String) travels.get(0).get("lineName");
				String startTime = df.format( travels.get(0).get("startTime"));
				/**设置表格插入的方式为按关键字插入**/
				lineTravels.put("insertWay", 1);
				/**设置表格插入的关键字**/
				lineTravels.put("keyword", "附件二：线路行程单");
				/**设置表格插入到模板的页数**/
				//lineTravels.put("pageBegin", 18);
				/**设置正文的行高:因为线路信息长度不确定，所以自己扩展行高吧**/
				//lineTravels.put("cellHeight", 30);
				/**设置表格标题**/
				lineTravels.put("theFirstHeader", lineName+"("+linePn+") 开团日期："+startTime);
				/**设置表头文字居中**/
				lineTravels.put("headersAlignment", 1);
				/**设置表头字段**/
				lineTravels.put("headers", new String[]{"天数","交通","线路信息","住宿","早餐","午餐","晚餐"});
				/**设置单元格文字水平居中**/
				lineTravels.put("cellHorizontalAlignment", 1);
				/**设置单元格文字垂直居中**/
				lineTravels.put("cellVerticalAlignment", 5);
				/**设置列宽比例**/
				lineTravels.put("colWidthPercent", new int[]{1,2,7,3,1,1,1});
				String[][] datas11 = new String[travels.size()][7];
				for(int i=0;i<travels.size();i++){
					String[] travel= new String[7];
					HashMap<String, Object> hMap = travels.get(i);
					travel[0] = i+1+"";
					travel[1] = (String) hMap.get("traffic");
					travel[2] = (String) hMap.get("route");
					travel[3] = (String) hMap.get("restaurant");
					travel[4] = (String) hMap.get("isBreakfast");
					travel[5] = (String) hMap.get("isLunch");
					travel[6] = (String) hMap.get("isDinner");
					datas11[i] = travel;
				}
				/**设置表格数据**/
				lineTravels.put("datas", datas11);
				dynamic_tables.add(lineTravels);
			}
			
			
			//附件三：自愿购物协议
			List<HashMap<String, Object>> shoppings = (List<HashMap<String, Object>>) tables.get("shoppings");
			if(!shoppings.isEmpty()){
				JSONObject purchase = new JSONObject();
				
				/**设置表格插入到模板的页数**/
				purchase.put("pageBegin", 19);
				/**设置正文的行高:因为线路信息长度不确定，所以自己扩展行高吧**/
				//lineTravels.put("cellHeight", 30);
				/**设置表格插入的方式为按关键字插入**/
				purchase.put("insertWay", 1);
				/**设置表格插入的关键字**/
				purchase.put("keyword", "附件三：自愿购物补充协议");
				/**设置表格标题**/
				purchase.put("theFirstHeader", "附件三:自愿购物补充协议");
				/**设置表头文字居中**/
				purchase.put("headersAlignment", 1);
				/**设置表头字段**/
				purchase.put("headers", new String[]{"具体时间","地点","购物场所名称","主要商品信息","最长停留时间","其他说明","旅游者签名同意"});
				/**设置单元格文字水平居中**/
				purchase.put("cellHorizontalAlignment", 1);
				/**设置单元格文字垂直居中**/
				purchase.put("cellVerticalAlignment", 5);
				/**设置列宽比例**/
				purchase.put("colWidthPercent", new int[]{1,1,1,1,1,1,1});
				String[][] datas22 = new String[shoppings.size()][7];
				for(int i=0;i<shoppings.size();i++){
					String[] buy= new String[7];
					HashMap<String, Object> hMap = shoppings.get(i);
					//暂时无数据来源
					buy[0] = (String) hMap.get("time");
					buy[1] = (String) hMap.get("address");
					buy[2] = (String) hMap.get("storeName");
					buy[3] = (String) hMap.get("goodInfo");
					buy[4] = (String) hMap.get("duration");
					buy[5] = (String) hMap.get("otherInfo");
					buy[6] = (String) hMap.get("customerName");
					datas22[i] = buy;
				}
				/**设置表格数据**/
				purchase.put("datas", datas22);
				dynamic_tables.add(purchase);
			}
			
			
			
			//附件四：自愿付费项目协议
			List<HashMap<String, Object>> payItems = (List<HashMap<String, Object>>) tables.get("payItems");
			if(!payItems.isEmpty()){
				JSONObject items = new JSONObject();
				
				/**设置表格插入到模板的页数**/
//				items.put("pageBegin", 19);
				/**设置正文的行高:因为线路信息长度不确定，所以自己扩展行高吧**/
				//lineTravels.put("cellHeight", 30);
				/**设置表格插入的方式为按关键字插入**/
				items.put("insertWay", 1);
				/**设置表格插入的关键字**/
				items.put("keyword", "附件四：自愿参加另行付费旅游项目补充协议");
				/**设置表格标题**/
				items.put("theFirstHeader", "附件四:自愿参加另行付费旅游项目补充协议");
				/**设置表头文字居中**/
				items.put("headersAlignment", 1);
				/**设置表头字段**/
				items.put("headers", new String[]{"具体时间","地点","项目名称和内容","费用（元）","项目时常（分钟）","其他说明","旅游者签名同意"});
				/**设置单元格文字水平居中**/
				items.put("cellHorizontalAlignment", 1);
				/**设置单元格文字垂直居中**/
				items.put("cellVerticalAlignment", 5);
				/**设置列宽比例**/
				items.put("colWidthPercent", new int[]{1,1,1,1,1,1,1});
				String[][] datas33 = new String[payItems.size()][7];
				for(int i=0;i<payItems.size();i++){
					String[] buy= new String[7];
					HashMap<String, Object> hMap = payItems.get(i);
					//暂时无数据来源
					buy[0] = (String) hMap.get("time");
					buy[1] = (String) hMap.get("address");
					buy[2] = (String) hMap.get("itemDescription");
					buy[3] = (String) hMap.get("money");
					buy[4] = (String) hMap.get("duration");
					buy[5] = (String) hMap.get("otherInfo");
					buy[6] = (String) hMap.get("customerName");
					datas33[i] = buy;
				}
				/**设置表格数据**/
				items.put("datas", datas33);
				dynamic_tables.add(items);
			}
			gcr.setDynamic_table(dynamic_tables.toJSONString());
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("fC2Gcr出错"+e.getMessage());
		}
		
		//gcr.setParamter_map(paramter_map);
		
		
		return gcr;
	}
	/**
	 * 将一日游合同实体转化成GCR实体
	 * @param fddDayTripContract
	 * @param template
	 * @param tables
	 * @return
	 */
	public GenerateContractRequest fDTC2Gcr(FddDayTripContract fddDayTripContract,FddContractTemplate template,HashMap<String, Object> tables){
		GenerateContractRequest gcr = new GenerateContractRequest();
		try {
			gcr.setTemplateId(template.getTemplateId());
			//设置生成的合同的名称。使用合同模板名+时间
			gcr.setDocTitle(template.getTemplateName()+DateUtil.getfileDate(new Date()));
			//设置合同编号
			gcr.setContractId(fddDayTripContract.getContractId());
			//设置上传的字段表
			JSONObject paramter_map = new JSONObject();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			if (fddDayTripContract.getCustomer()!=null) {
				paramter_map.put("customer", fddDayTripContract.getCustomer());
			}
			if (fddDayTripContract.getCustomerIDNum()!=null) {
				paramter_map.put("customerIDNum", fddDayTripContract.getCustomerIDNum());
			}
			if (fddDayTripContract.getCustomerNum()!=null) {
				paramter_map.put("customerNum", fddDayTripContract.getCustomerNum().toString());
			}
			
			if(fddDayTripContract.getHyName()!=null){
				paramter_map.put("hyName", fddDayTripContract.getHyName());
			}
			if(fddDayTripContract.getHyId()!=null){
				paramter_map.put("hyId", fddDayTripContract.getHyId());
			}
			if(fddDayTripContract.getContractId()!=null){
				paramter_map.put("contractId", fddDayTripContract.getContractId());
			}
			
			
			if(fddDayTripContract.getGuideName()!=null){
				paramter_map.put("guideName", fddDayTripContract.getGuideName());
			}
			if(fddDayTripContract.getGuideIDNum()!=null){
				paramter_map.put("guideIDNum", fddDayTripContract.getGuideIDNum());
			}
			if(fddDayTripContract.getGuidePhone()!=null){
				paramter_map.put("guidePhone", fddDayTripContract.getGuidePhone());
			}
			
			
			if(fddDayTripContract.getIfShopping()!=null){
				paramter_map.put("ifShopping", fddDayTripContract.getIfShopping()==0?"否":"是");
			}
			if(fddDayTripContract.getShoppingAddress()!=null){
				paramter_map.put("shoppingAddress", fddDayTripContract.getShoppingAddress());
			}
			if(fddDayTripContract.getLineInfo()!=null){
				paramter_map.put("lineInfo", fddDayTripContract.getLineInfo());
			}
			
			
			
			if(fddDayTripContract.getTrafficType()!=null){
				paramter_map.put("trafficType", fddDayTripContract.getTrafficType()==0?"包车游":"合车游");
			}
			if(fddDayTripContract.getBusNumber()!=null){
				paramter_map.put("busNumber", fddDayTripContract.getBusNumber());
			}
			if(fddDayTripContract.getDriver()!=null){
				paramter_map.put("driver", fddDayTripContract.getDriver());
			}
			if(fddDayTripContract.getIfAirConditioner()!=null){
				paramter_map.put("ifAirConditioner", fddDayTripContract.getIfAirConditioner()==0?"否":"是");
			}
			if(fddDayTripContract.getTrafficStandard()!=null){
				paramter_map.put("trafficStandard", fddDayTripContract.getTrafficStandard());
			}
			
			
			if(fddDayTripContract.getBreakfastAddress()!=null){
				paramter_map.put("breakfastAddress", fddDayTripContract.getBreakfastAddress());
			}
			if(fddDayTripContract.getBreakfastStandard()!=null){
				paramter_map.put("breakfastStandard", fddDayTripContract.getBreakfastStandard());
			}
			if(fddDayTripContract.getLunchAddress()!=null){
				paramter_map.put("lunchAddress", fddDayTripContract.getLunchAddress());
			}
			if(fddDayTripContract.getLunchStandard()!=null){
				paramter_map.put("lunchStandard", fddDayTripContract.getLunchStandard());
			}
			if(fddDayTripContract.getDinnerAddress()!=null){
				paramter_map.put("dinnerAddress", fddDayTripContract.getDinnerAddress());
			}
			if(fddDayTripContract.getDinnerStandard()!=null){
				paramter_map.put("dinnerStandard", fddDayTripContract.getDinnerStandard());
			}
			
			
			
			if(fddDayTripContract.getAdultNum()!=null){
				paramter_map.put("adultNum", fddDayTripContract.getAdultNum().toString());
			}
			if(fddDayTripContract.getChildrenNum()!=null){
				paramter_map.put("childrenNum", fddDayTripContract.getChildrenNum().toString());
			}
			if(fddDayTripContract.getAdultTicketPrice()!=null){	
				paramter_map.put("adultTicketPrice", fddDayTripContract.getAdultTicketPrice().toString());
			}
			if(fddDayTripContract.getChildrenTicketPrice()!=null){
				paramter_map.put("childrenTicketPrice", fddDayTripContract.getChildrenTicketPrice().toString());
			}
			if(fddDayTripContract.getTotalPrice()!=null){
				paramter_map.put("totalPrice", fddDayTripContract.getTotalPrice().toString());
			}
			if(fddDayTripContract.getFeeNote()!=null){
				paramter_map.put("feeNote", fddDayTripContract.getFeeNote());
			}
			if(fddDayTripContract.getHaveNot()!=null){
				paramter_map.put("haveNot", fddDayTripContract.getHaveNot());
			}
			if(fddDayTripContract.getPaymentTime()!=null){
				paramter_map.put("paymentTime", df.format(fddDayTripContract.getPaymentTime()));
			}
			if(fddDayTripContract.getPaymentType()!=null){
				// 1.转账 2.支付宝 3.微信支付 4.现金 5.预存款 6.刷卡
				String type = "";
				switch (fddDayTripContract.getPaymentType()) {
				case 1:
					type = "转账";
					break;
				case 2:
					type = "支付宝";
					break;
				case 3:
					type = "微信支付";
					break;
				case 4:
					type = "现金";
					break;
				case 5:
					type = "预存款";
					break;
				case 6:
					type = "刷卡";
					break;
				default:
					type = "其他方式";
					break;
				}
				paramter_map.put("paymentType", type);	
			}
			
			
			if(fddDayTripContract.getNegotiationPhone()!=null){
				paramter_map.put("negotiationPhone", fddDayTripContract.getNegotiationPhone());
			}
			if(fddDayTripContract.getComplainPhone()!=null){
				paramter_map.put("complainPhone", fddDayTripContract.getComplainPhone());
			}
			if(fddDayTripContract.getJudgementType()!=null){
				paramter_map.put("judgementType", fddDayTripContract.getJudgementType().toString());
			}
			if(fddDayTripContract.getArbitrationCommittee()!=null){
				paramter_map.put("arbitrationCommittee", fddDayTripContract.getArbitrationCommittee());
			}
			if(fddDayTripContract.getOtherNote()!=null){
				paramter_map.put("otherNote", fddDayTripContract.getOtherNote());
			}
	
			
			if(fddDayTripContract.getCustomerPhone()!=null){
				paramter_map.put("customerPhone", fddDayTripContract.getCustomerPhone());
			}
			if(fddDayTripContract.getCustomerAddress()!=null){
				paramter_map.put("customerAddress", fddDayTripContract.getCustomerAddress());
			}
			if(fddDayTripContract.getCustomerEmail()!=null){
				paramter_map.put("customerEmail", fddDayTripContract.getCustomerEmail());
			}
			if(fddDayTripContract.getCustomerSignTime()!=null){
				paramter_map.put("customerSignTime", df.format(fddDayTripContract.getCustomerSignTime()));
			}
			if(fddDayTripContract.getSignAddress()!=null){
				paramter_map.put("signAddress", fddDayTripContract.getSignAddress());
			}
			if(fddDayTripContract.getHySignTime()!=null){
				paramter_map.put("hySignTime", df.format(fddDayTripContract.getHySignTime()));
			}
			if(fddDayTripContract.getHyEmail()!=null){
				paramter_map.put("hyEmail", fddDayTripContract.getHyEmail());
			}
			if(fddDayTripContract.getHyAddress()!=null){
				paramter_map.put("hyAddress", fddDayTripContract.getHyAddress());
			}
			if(fddDayTripContract.getHyOperator()!=null){
				paramter_map.put("hyOperator", fddDayTripContract.getHyOperator());
			}
			if(fddDayTripContract.getHyPhone()!=null){
				paramter_map.put("hyPhone", fddDayTripContract.getHyPhone());
			}

			gcr.setParamter_map(paramter_map.toJSONString());
			/*******以下是动态表单********/
			
			JSONArray dynamic_tables = new JSONArray();
			
			
			//附件一：游客名单表
			List<HashMap<String, Object>> customers = (List<HashMap<String, Object>>) tables.get("orderCustomers");
			if(!customers.isEmpty()){
				JSONObject orderCustomers = new JSONObject();
				/**设置表格插入的方式为按关键字插入**/
				orderCustomers.put("insertWay", 1);
				/**设置表格插入到模板的页数**/
				//orderCustomers.put("pageBegin", 17);
				/**设置表格插入的关键字**/
				orderCustomers.put("keyword", "附件一：游客名单表");
				/**设置正文的行高**/
				orderCustomers.put("cellHeight", 25);
				/**设置表格标题**/
				//orderCustomers.put("theFirstHeader", "附件1");
				/**设置表头文字居中**/
				orderCustomers.put("headersAlignment", 1);
				/**设置表头字段**/
				orderCustomers.put("headers", new String[]{"姓名","证件号码","性别","民族","是否儿童","手机号码","身体状况"});
				/**设置单元格文字水平居中**/
				orderCustomers.put("cellHorizontalAlignment", 1);
				/**设置单元格文字垂直居中**/
				orderCustomers.put("cellVerticalAlignment", 5);
				/**设置列宽比例**/
				orderCustomers.put("colWidthPercent", new int[]{2,5,2,2,4,4,4});
				
				String[][] datas = new String[customers.size()][7];
				for(int i=0;i<customers.size();i++){
					String[] customer = new String[7];
					HashMap<String, Object> hMap = customers.get(i);
					customer[0] = (String) hMap.get("name");
					customer[1] = (String) hMap.get("certificate");
					customer[2] = (String) hMap.get("gender");
					customer[3] = (String) hMap.get("national");
					customer[4] = (String) hMap.get("ifChildren");
					customer[5] = (String) hMap.get("phone");
					customer[6] = (String) hMap.get("health");
					datas[i] = customer;
				}
				/**设置表格数据**/
				orderCustomers.put("datas", datas);
				dynamic_tables.add(orderCustomers);
			}
			
			//附件二：线路行程单
			
			List<HashMap<String, Object>> travels = (List<HashMap<String, Object>>) tables.get("lineTravels");
			if(!travels.isEmpty()){
				JSONObject lineTravels = new JSONObject();
				String linePn = (String) travels.get(0).get("linePn");
				String lineName = (String) travels.get(0).get("lineName");
				String startTime = df.format( travels.get(0).get("startTime"));
				/**设置表格插入的方式为按关键字插入**/
				lineTravels.put("insertWay", 1);
				/**设置表格插入的关键字**/
				lineTravels.put("keyword", "附件二：线路行程单");
				/**设置表格插入到模板的页数**/
				//lineTravels.put("pageBegin", 18);
				/**设置正文的行高:因为线路信息长度不确定，所以自己扩展行高吧**/
				//lineTravels.put("cellHeight", 30);
				/**设置表格标题**/
				lineTravels.put("theFirstHeader", lineName+"("+linePn+") 开团日期："+startTime);
				/**设置表头文字居中**/
				lineTravels.put("headersAlignment", 1);
				/**设置表头字段**/
				lineTravels.put("headers", new String[]{"天数","交通","线路信息","住宿","早餐","午餐","晚餐"});
				/**设置单元格文字水平居中**/
				lineTravels.put("cellHorizontalAlignment", 1);
				/**设置单元格文字垂直居中**/
				lineTravels.put("cellVerticalAlignment", 5);
				/**设置列宽比例**/
				lineTravels.put("colWidthPercent", new int[]{1,2,7,3,1,1,1});
				String[][] datas11 = new String[travels.size()][7];
				for(int i=0;i<travels.size();i++){
					String[] travel= new String[7];
					HashMap<String, Object> hMap = travels.get(i);
					travel[0] = i+1+"";
					travel[1] = (String) hMap.get("traffic");
					travel[2] = (String) hMap.get("route");
					travel[3] = (String) hMap.get("restaurant");
					travel[4] = (String) hMap.get("isBreakfast");
					travel[5] = (String) hMap.get("isLunch");
					travel[6] = (String) hMap.get("isDinner");
					datas11[i] = travel;
				}
				/**设置表格数据**/
				lineTravels.put("datas", datas11);
				dynamic_tables.add(lineTravels);
			}
			
			
			//附件三：自愿购物协议
			List<HashMap<String, Object>> shoppings = (List<HashMap<String, Object>>) tables.get("shoppings");
			if(!shoppings.isEmpty()){
				JSONObject purchase = new JSONObject();
	
				/**设置表格插入到模板的页数**/
				purchase.put("pageBegin", 19);
				/**设置正文的行高:因为线路信息长度不确定，所以自己扩展行高吧**/
				//lineTravels.put("cellHeight", 30);
				/**设置表格标题**/
				purchase.put("theFirstHeader", "附件3:自愿购物活动补充协议");
				/**设置表头文字居中**/
				purchase.put("headersAlignment", 1);
				/**设置表头字段**/
				purchase.put("headers", new String[]{"具体时间","地点","购物场所名称","主要商品信息","最长停留时间","其他说明","旅游者签名同意"});
				/**设置单元格文字水平居中**/
				purchase.put("cellHorizontalAlignment", 1);
				/**设置单元格文字垂直居中**/
				purchase.put("cellVerticalAlignment", 5);
				/**设置列宽比例**/
				purchase.put("colWidthPercent", new int[]{1,1,1,1,1,1,1});
				String[][] datas22 = new String[shoppings.size()][7];
				for(int i=0;i<shoppings.size();i++){
					String[] buy= new String[7];
					HashMap<String, Object> hMap = shoppings.get(i);
					//暂时无数据来源
//					buy[0] = (String) hMap.get("traffic");
//					buy[1] = (String) hMap.get("traffic");
//					buy[2] = (String) hMap.get("route");
//					buy[3] = (String) hMap.get("restaurant");
//					buy[4] = (String) hMap.get("isBreakfast");
//					buy[5] = (String) hMap.get("isLunch");
//					buy[6] = (String) hMap.get("isDinner");
					datas22[i] = buy;
				}
				/**设置表格数据**/
				purchase.put("datas", datas22);
				dynamic_tables.add(purchase);
			}
			
			
			
			//附件四：自愿付费项目协议
			List<HashMap<String, Object>> payItems = (List<HashMap<String, Object>>) tables.get("payItems");
			if(!payItems.isEmpty()){
				JSONObject items = new JSONObject();
				
				/**设置表格插入到模板的页数**/
				items.put("pageBegin", 19);
				/**设置正文的行高:因为线路信息长度不确定，所以自己扩展行高吧**/
				//lineTravels.put("cellHeight", 30);
				/**设置表格标题**/
				items.put("theFirstHeader", "附件3:自愿购物活动补充协议");
				/**设置表头文字居中**/
				items.put("headersAlignment", 1);
				/**设置表头字段**/
				items.put("headers", new String[]{"具体时间","地点","项目名称和内容","费用（元）","项目时常（分钟）","其他说明","旅游者签名同意"});
				/**设置单元格文字水平居中**/
				items.put("cellHorizontalAlignment", 1);
				/**设置单元格文字垂直居中**/
				items.put("cellVerticalAlignment", 5);
				/**设置列宽比例**/
				items.put("colWidthPercent", new int[]{1,1,1,1,1,1,1});
				String[][] datas33 = new String[payItems.size()][7];
				for(int i=0;i<payItems.size();i++){
					String[] buy= new String[7];
					HashMap<String, Object> hMap = payItems.get(i);
					//暂时无数据来源
//					buy[0] = (String) hMap.get("traffic");
//					buy[1] = (String) hMap.get("traffic");
//					buy[2] = (String) hMap.get("route");
//					buy[3] = (String) hMap.get("restaurant");
//					buy[4] = (String) hMap.get("isBreakfast");
//					buy[5] = (String) hMap.get("isLunch");
//					buy[6] = (String) hMap.get("isDinner");
					datas33[i] = buy;
				}
				/**设置表格数据**/
				items.put("datas", datas33);
				dynamic_tables.add(items);
			}
			gcr.setDynamic_table(dynamic_tables.toJSONString());
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("fC2Gcr出错"+e.getMessage());
		}
		
		//gcr.setParamter_map(paramter_map);
		
		
		return gcr;
	}
	
}
