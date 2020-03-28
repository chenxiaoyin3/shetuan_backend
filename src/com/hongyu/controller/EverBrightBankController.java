package com.hongyu.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.csii.payment.client.core.CebMerchantSignVerify;
import com.csii.payment.client.core.MerchantSignTool;
import com.csii.payment.client.entity.CebMerchantProperties;
import com.csii.payment.client.entity.CertificateInfo;
import com.csii.payment.client.entity.SignParameterObject;
import com.csii.payment.client.entity.VerifyParameterObject;
import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.BranchBalance;
import com.hongyu.entity.BranchPreSave;
import com.hongyu.entity.CommonSequence;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyVisaPic;
import com.hongyu.entity.CommonSequence.SequenceTypeEnum;
import com.hongyu.entity.Department;
import com.hongyu.entity.Store;
import com.hongyu.entity.StoreAccount;
import com.hongyu.entity.StoreAccountLog;
import com.hongyu.service.BranchBalanceService;
import com.hongyu.service.BranchPreSaveService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyVisaPicService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StoreService;
import com.hongyu.util.BankUtil;
import com.hongyu.util.Constants;
import com.hongyu.util.bankEntity.InterBankRequest;
import com.hongyu.util.bankEntity.LocalPayRequest;
import com.hongyu.util.bankEntity.QueryRequest;
import com.hongyu.util.industrialBankUtil.IPUtil;


/**
 * 光大银行对接接口
 */
@Controller
@RequestMapping("everBrightBank/")
public class EverBrightBankController {
	@Resource(name="storeAccountLogServiceImpl")
	private StoreAccountLogService storeAccountLogService;

	@Resource(name="storeAccountServiceImpl")
	private StoreAccountService storeAccountService;
	
	@Resource(name="commonSequenceServiceImp")
	private CommonSequenceService commonSequenceService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name="hyOrderServiceImpl")
	private HyOrderService hyOrderService;
	
	@Resource(name="hyVisaPicServiceImpl")
	private HyVisaPicService hyVisaPicService;
	
	@Resource(name="branchPreSaveServiceImpl")
	private BranchPreSaveService branchPreSaveService;
	
	@Resource(name="branchBalanceServiceImpl")
	private BranchBalanceService branchBalanceService;
	
	@Value("${cebbank.merUrl}")
	private String merUrl;

	@Value("${cebbank.merUrl2}")
	private String merUrl2;

	@Value("${cebbank.merUrl1}")
	private String merUrl1;

    @Value("${cebbank.orderUrl}")
    private String orderUrl;

    @Value("${cebbank.branchRechargeForeURL}")
    private String branchRechargeForeURL;

    @Value("${cebbank.branchRechargeRedirectURL}")
    private String branchRechargeRedirectURL;

    @Value("${cebbank.branchRechargeBehiUrl}")
    private String branchRechargeBehiUrl;

	/**
	 * 门店充值,用光大银行 
	 */
	@RequestMapping(value="store/recharge")
	@ResponseBody
	public Json recharge(BigDecimal money,Integer type,String bankNo,HttpSession session)
	{
		Json json=new Json();
		try {	
			Map<String,Object> map=new HashMap<String,Object>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("department", department));
			List<Store> stores=storeService.findList(null,filters,null);
			filters.clear();
			Store store=stores.get(0);
			StoreAccountLog storeAccountLog=new StoreAccountLog();
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			filters.add(Filter.eq("type", SequenceTypeEnum.mendianRecharge));
			synchronized(CommonSequence.class) {
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="MDCZ" + dateStr + String.format("%04d", value);
			}
			storeAccountLog.setChargeOrderSn(produc);
			storeAccountLog.setMoney(money.setScale(2, BigDecimal.ROUND_HALF_UP));
			storeAccountLog.setStore(store);
			storeAccountLog.setType(0); //充值
			storeAccountLog.setProfile("网银充值");
			storeAccountLog.setStatus(4); //未成功支付
			storeAccountLogService.save(storeAccountLog);
			
		    StringBuilder plain=new StringBuilder();	
		    SignParameterObject signParam = new SignParameterObject(); //电子签名参数
			//本行B2C支付
			if(type==1) {
				//准备参数,调光大银行接口
				LocalPayRequest localPayRequest=new LocalPayRequest();
				localPayRequest.setTransId("IPER");
				//商户代码应该填什么?测试模式,商户号用370310000004
//				localPayRequest.setMerchantId("370310000004"); //测试环境商户代码
				localPayRequest.setMerchantId("752010000019"); //生产环境商户代码
				localPayRequest.setOrderId(produc); //订单号
				localPayRequest.setTransAmt(money.setScale(2, BigDecimal.ROUND_HALF_UP)); //交易金额
				Date date=new Date();
				DateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
				String dateFormat=format2.format(date);
				localPayRequest.setTransDateTime(dateFormat); //交易时间
				localPayRequest.setCurrencyType("01"); //币种为人民币
				localPayRequest.setCustomerName(store.getStoreName());
				localPayRequest.setMerSecName("");
				localPayRequest.setProductInfo("门店充值");
				localPayRequest.setCustomerEmail("");
				//暂时使用123服务器地址
//				localPayRequest.setMerURL("http://admin.swczyc.com/hyapi/everBrightBank/bankrequest"); //商户URL-用于后台通知商户
				//测试环境下必须用ip地址
				localPayRequest.setMerURL(merUrl); //商户URL-用于后台通知商户
				//暂时使用123服务器地址
				localPayRequest.setMerURL1(merUrl2); //商户URL1-用于后台通知商户失败或默认情况下，引导客户回商户页面
				//获取本机IP
				localPayRequest.setPayIp(IPUtil.getLocalIp());
				localPayRequest.setMsgExt("充值");
				
//				signParam.setMerchantId("370310000004");// 商户号,测试用商户号都是370310000004
				signParam.setMerchantId("752010000019"); //生产环境商户号
				signParam.setTransId("IPER"); //B2C方式
				plain=plain.append(BankUtil.generatePlain(localPayRequest));
			}
			
			//跨行B2C支付
			else if(type==2) {
				//准备参数,调光大银行接口
				InterBankRequest interBankRequest=new InterBankRequest();	
				interBankRequest.setTransId("IPER"); //交易代码
//				interBankRequest.setMerchantId("365010000043"); //测试环境商户代码
				interBankRequest.setMerchantId("752010000019"); //生产环境商户代码

				//商户代码应该填什么?测试模式,跨行支付商户号用365010000043
				interBankRequest.setMerchantId("365010000043"); //商户代码
				interBankRequest.setOrderId(produc); //订单号
				interBankRequest.setTransAmt(money.setScale(2, BigDecimal.ROUND_HALF_UP)); //交易金额
				Date date=new Date();
				DateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
				String dateFormat=format2.format(date);
				interBankRequest.setTransDateTime(dateFormat); //交易时间
				interBankRequest.setCurrencyType("01"); //币种为人民币
				interBankRequest.setPayBankNo(bankNo); //25工商银行,29农业银行
				interBankRequest.setCustomerName(store.getStoreName());
				interBankRequest.setMerSecName("二级商户");
				interBankRequest.setProductInfo("门店充值");
				interBankRequest.setCustomerEmail("");
				//暂时使用123服务器地址
//				localPayRequest.setMerURL("http://admin.swczyc.com/hyapi/everBrightBank/bankrequest"); //商户URL-用于后台通知商户
				//测试环境下必须用ip地址
				interBankRequest.setMerURL(merUrl); //商户URL-用于后台通知商户
				//暂时使用123服务器地址
				interBankRequest.setMerURL1(merUrl2); //商户URL1-用于后台通知商户失败或默认情况下，引导客户回商户页面
//				interBankRequest.setMerURL1("http://admin.swczyc.com/hongyup/#/storeManagement/generalService/rechargeRecord"); //商户URL1-用于后台通知商户失败或默认情况下，引导客户回商户页面

				//获取本机IP
				interBankRequest.setPayIp(IPUtil.getLocalIp());
				interBankRequest.setMsgExt("充值");
				
//				signParam.setMerchantId("365010000043");// 商户号,跨行支付测试用商户号都是365010000043
				signParam.setMerchantId("752010000019");// 生产环境用商户号
				signParam.setTransId("IPER"); //B2C方式
				plain=plain.append(BankUtil.generatePlain(interBankRequest));
			}
			
			else {
				throw new RuntimeException("不存在的类型");
			}
			
			signParam.setPlain(plain.toString());// 明文
			signParam.setCharset("GBK");// 明文使用的字符集
			signParam.setType(0);// 0-普通报文,1-XML报文签名(使用JDK),2-XML报文签名(使用Apache)
			signParam.setAlgorithm("MD5withRSA");// 签名算法
			String sign = MerchantSignTool.sign(signParam);
			map.put("Plain", plain);
			map.put("Signature", sign);
			json.setMsg("传输成功");
			json.setObj(map);
			json.setSuccess(true);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("传输参数失败");
			e.printStackTrace();
		}
		return json;
	}

	
	/**
	 * 网银支付订单
	 */
	@RequestMapping(value="storeOrder/pay")
	@ResponseBody
	public Json orderPay(Long orderId,HttpSession session)
	{
		Json json=new Json();
		try {
			Map<String,Object> map=new HashMap<String,Object>();
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department=hyAdmin.getDepartment();
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.eq("department", department));
			List<Store> stores=storeService.findList(null,filters,null);
			filters.clear();
			//找到所属门店
			Store store=stores.get(0);
			filters.add(Filter.eq("store", store));
			List<StoreAccount> storeAccounts = storeAccountService.findList(null, filters, null);
			filters.clear();
			//找出门店余额
			StoreAccount storeAccount=storeAccounts.get(0);
			HyOrder hyOrder=hyOrderService.find(orderId);
			String orderNum=hyOrder.getOrderNumber();
			BigDecimal orderMoney=BigDecimal.ZERO;
			//6-保险,如果是保险订单,则计算外卖价
			if(hyOrder.getType()==6) {
				orderMoney=orderMoney.add(hyOrder.getWaimaiMoney());
			}
			else {
				//订单金额要减去门店返利值
				orderMoney = orderMoney.add(hyOrder.getJiusuanMoney());
				if(hyOrder.getTip()!=null) {
					orderMoney=orderMoney.add(hyOrder.getTip());
				}
				if(hyOrder.getDiscountedPrice()!=null) {
					orderMoney=orderMoney.subtract(hyOrder.getDiscountedPrice());
				}
				if(hyOrder.getStoreFanLi()!=null) {
					orderMoney=orderMoney.subtract(hyOrder.getStoreFanLi());
				}
			}
			
			//算出应付款
			BigDecimal yingfuMoney=orderMoney.subtract(storeAccount.getBalance());
				
			StringBuilder plain=new StringBuilder();	
		    SignParameterObject signParam = new SignParameterObject(); //电子签名参数
		    //准备参数,调光大银行接口
			LocalPayRequest localPayRequest=new LocalPayRequest();
			localPayRequest.setTransId("IPER");
//			localPayRequest.setMerchantId("370310000004"); //测试环境商户代码
			localPayRequest.setMerchantId("752010000019"); //生产环境商户代码
			localPayRequest.setOrderId(orderNum); //订单号
			//交易金额应该是 订单金额-门店预存款
			localPayRequest.setTransAmt(yingfuMoney.setScale(2, BigDecimal.ROUND_HALF_UP)); //交易金额
			Date date=new Date();
			DateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
			String dateFormat=format2.format(date);
			localPayRequest.setTransDateTime(dateFormat); //交易时间
			localPayRequest.setCurrencyType("01"); //币种为人民币
			localPayRequest.setCustomerName("");
			localPayRequest.setMerSecName("");
			localPayRequest.setProductInfo("订单支付");
			localPayRequest.setCustomerEmail("");
			//暂时使用123服务器地址
//			localPayRequest.setMerURL("http://admin.swczyc.com/hyapi/everBrightBank/bankrequest"); //商户URL-用于后台通知商户
			//测试环境下必须用ip地址
			localPayRequest.setMerURL(orderUrl); //商户URL-用于后台通知商户
			//暂时使用123服务器地址
			localPayRequest.setMerURL1(merUrl2); //商户URL1-用于后台通知商户失败或默认情况下，引导客户回商户页面
			//获取本机IP
			localPayRequest.setPayIp(IPUtil.getLocalIp());
			localPayRequest.setMsgExt("充值");
			
//			signParam.setMerchantId("370310000004");// 商户号,测试用商户号都是370310000004
			signParam.setMerchantId("752010000019"); //生产环境商户号
			signParam.setTransId("IPER"); //B2C方式
			plain=plain.append(BankUtil.generatePlain(localPayRequest));
			
			signParam.setPlain(plain.toString());// 明文
			signParam.setCharset("GBK");// 明文使用的字符集
			signParam.setType(0);// 0-普通报文,1-XML报文签名(使用JDK),2-XML报文签名(使用Apache)
			signParam.setAlgorithm("MD5withRSA");// 签名算法
			String sign = MerchantSignTool.sign(signParam);
			map.put("Plain", plain);
			map.put("Signature", sign);
			json.setMsg("传输成功");
			json.setObj(map);
			json.setSuccess(true);	
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("传输参数失败");
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 分公司充值,用光大银行 
	 */
	@RequestMapping(value="branch/recharge")
	@ResponseBody
	public Json branchRecharge(BigDecimal money,HttpSession session)
	{
		Json json=new Json();
		try {	
			Map<String,Object> map=new HashMap<String,Object>();
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			Department department=getCompany(hyAdmin);
			Long branchId=department.getId();
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("branchId", branchId));
			List<BranchBalance> balanceList=branchBalanceService.findList(null,filters,null);
			BranchBalance branceBalance=new BranchBalance();
			//如果找不到分公司对应余额,就创建一条
			if(balanceList.isEmpty()) {
				BranchBalance branchBalance1=new BranchBalance();
				branchBalance1.setBranchId(branchId);
				branchBalance1.setBranchBalance(BigDecimal.ZERO);
				branchBalanceService.save(branchBalance1);
				branceBalance=branchBalance1;
			}
			else {
				//一个分公司只有一条余额记录
				branceBalance=balanceList.get(0);
			}	
			BranchPreSave preSave=new BranchPreSave();
			preSave.setBranchId(branchId);
			preSave.setBranchName(department.getName());
			preSave.setDepartmentName(hyAdminService.find(username).getDepartment().getFullName());
			preSave.setType(7); //网银充值未成功
			preSave.setDate(new Date());
			preSave.setAmount(money);
			preSave.setPreSaveBalance(branceBalance.getBranchBalance());
		    
			//自动生成订单编号
			String produc="";
			Date cur = new Date();
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			String dateStr = format.format(cur);
			filters.clear();
			filters.add(Filter.eq("type", SequenceTypeEnum.bankBranchRecharge));
			synchronized(CommonSequence.class) {
				List<CommonSequence> ss = commonSequenceService.findList(null, filters, null);
				CommonSequence c = ss.get(0);
				Long value = c.getValue() + 1;
				c.setValue(value);
				commonSequenceService.update(c);
				produc="BCZ" + dateStr + String.format("%04d", value);
			}
			preSave.setBankChargeOrderSn(produc);
			branchPreSaveService.save(preSave);
			
		    StringBuilder plain=new StringBuilder();	
		    SignParameterObject signParam = new SignParameterObject(); //电子签名参数
			//本行B2C支付
			//准备参数,调光大银行接口
		    LocalPayRequest localPayRequest=new LocalPayRequest();
			localPayRequest.setTransId("IPER");
//			localPayRequest.setMerchantId("370310000004"); //测试环境商户代码
			localPayRequest.setMerchantId("752010000019"); //生产环境商户代码
			localPayRequest.setOrderId(produc); //订单号
			localPayRequest.setTransAmt(money.setScale(2, BigDecimal.ROUND_HALF_UP)); //交易金额
			Date date=new Date();
			DateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
			String dateFormat=format2.format(date);
			localPayRequest.setTransDateTime(dateFormat); //交易时间
			localPayRequest.setCurrencyType("01"); //币种为人民币
			localPayRequest.setCustomerName(department.getName());
			localPayRequest.setMerSecName("");
			localPayRequest.setProductInfo("分公司充值");
			localPayRequest.setCustomerEmail("");
			//测试环境下必须用ip地址
			localPayRequest.setMerURL(branchRechargeBehiUrl); //商户URL-用于后台通知商户
			//暂时使用123服务器地址
			localPayRequest.setMerURL1(branchRechargeRedirectURL); //商户URL1-用于后台通知商户失败或默认情况下，引导客户回商户页面
			//获取本机IP
			localPayRequest.setPayIp(IPUtil.getLocalIp());
			localPayRequest.setMsgExt("充值");
				
//			signParam.setMerchantId("370310000004");// 商户号,测试用商户号都是370310000004
			signParam.setMerchantId("752010000019"); //生产环境商户号
			signParam.setTransId("IPER"); //B2C方式
			plain=plain.append(BankUtil.generatePlain(localPayRequest));
			
			signParam.setPlain(plain.toString());// 明文
			signParam.setCharset("GBK");// 明文使用的字符集
			signParam.setType(0);// 0-普通报文,1-XML报文签名(使用JDK),2-XML报文签名(使用Apache)
			signParam.setAlgorithm("MD5withRSA");// 签名算法
			String sign = MerchantSignTool.sign(signParam);
			map.put("Plain", plain);
			map.put("Signature", sign);
			json.setMsg("传输成功");
			json.setObj(map);
			json.setSuccess(true);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("传输参数失败");
			e.printStackTrace();
		}
		return json;
	}
	
	//银行回调接口
	@RequestMapping(value="bankrequest")
	public void bankRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
//	public void bankRequest()
	{
		// 银行请求商户：Plain,ResponseCode,Signature 
	    String recode = request.getParameter("ResponseCode"); 
	    String plain = request.getParameter("Plain"); 
	    String signature = request.getParameter("Signature");
	   // String recode="0000";
	    
	    /**测试能够进入该接口*/
//	    HyVisaPic pic=new HyVisaPic();
//		pic.setLarge(plain);
//		pic.setMedium(recode);
//		pic.setSource(signature);
//		hyVisaPicService.save(pic);
		PrintWriter writer=response.getWriter();
	    try {
	    	VerifyParameterObject verifyParam=new VerifyParameterObject();
	    	
		    verifyParam.setPlain(plain);//明文
		    verifyParam.setPlainCharset("GBK");//明文使用的字符集
		    verifyParam.setSign(signature);//签名串
		    verifyParam.setType(0);//0-普通报文
		    verifyParam.setAlgorithm("MD5withRSA");//签名算法
		    //boolean verify=MerchantSignTool.verify(verifyParam);
		    boolean verify = CebMerchantSignVerify.merchantVerifyPayGate_ABA(signature, plain);
		    if(verify=false){
		    	System.out.println("验签失败");
		        throw new Exception("验签失败,非有效内容或签名");
		    }
		    else {
		    	/**切割该字符需要转义*/
			    String[] pairs = plain.split("~\\|~");		    

			    //获取商户号
			    String merchantIdSerial=pairs[1]; //得到merchantId=xxxxx
			    String[] merchants=merchantIdSerial.split("=");
			    String merchantId=merchants[1]; //得到商户号
			    
				 //获取订单编号
				 String orderIdSerial=pairs[2]; //得到orderId=xxxxx
				 
				 String[] orderIds=orderIdSerial.split("=");
				 String orderId=orderIds[1];
			
				 //根据orderId查找门店充值记录
				 List<Filter> filters=new ArrayList<>();
				 filters.add(Filter.eq("chargeOrderSn", orderId));
				 //原则上一个订单编号只能查出来一条充值记录
				 List<StoreAccountLog> storeAccountLogs=storeAccountLogService.findList(null,filters,null);
				 StoreAccountLog accountLog=storeAccountLogs.get(0);
				 /**
				  * 如果门店的预存款状态为 4网银充值不成功，则将状态改为 5充值成功；
				  * 如果状态已经是5,为了避免银行的重复回调，则不再改变门店预存款余额
				  * added by GSbing,20190817
				  * */
				 if(accountLog.getStatus()==4) {
					 accountLog.setStatus(5); //5-支付成功updatestoreAccountLogService
					 //改变状态为“充值成功”
					 storeAccountLogService.update(accountLog);
					 Store store=accountLog.getStore(); //找到所属门店
					 filters.clear();
					 filters.add(Filter.eq("store", store));
					 //找出门店账户表,将门店余额增加
					 List<StoreAccount> storeAccounts=storeAccountService.findList(null,filters,null);
					 //一个门店只有一个账户
					 StoreAccount storeAccount=storeAccounts.get(0);
					 //将充值金额加上
					 storeAccount.setBalance(storeAccount.getBalance().add(accountLog.getMoney()));
					 storeAccountService.update(storeAccount);
				 }
					 
				 String result="Plain=";
				 
				 String plain2=pairs[1]+"~|~"; //merchantId
				 plain2=plain2+pairs[2]+"~|~"; //orderId
				 plain2=plain2+pairs[4]+"~|~"; //transDateTime
				 plain2=plain2+"procStatus=1"+"~|~";
				 //先用123服务器的
//				 response=response+"merURL2=http://admin.swczyc.com/hongyup/#/storeManagement/generalService/rechargeRecord";
				 plain2=plain2+"merURL2=";
				 plain2=plain2+merUrl2;
//				 plain2=plain2+Constants.merUrl2+"?";
//				 plain2=plain2+pairs[1]+"&"+pairs[2];
//				 plain2=plain2+"merURL2=http://admin.swczyc.com/hongyup/#/storeManagement/generalService/rechargeRecord?";
				 
				 SignParameterObject signParam = new SignParameterObject();
				 signParam.setMerchantId(merchantId);
				 signParam.setPlain(plain2);
				 signParam.setCharset("GBK");
				 signParam.setType(0);//0-普通报文
				 signParam.setAlgorithm("MD5withRSA");//签名算法
				 //返回给银行的签名信息
				 String signature2=MerchantSignTool.sign(signParam);
//				 response=response+"\r\n"+"ResponseCode=0000"+"\r\n";
				 result=result+plain2;
				 result=result+"\r\n";
				 result=result+"Signature="+signature2;
				 writer.print(result);
				 writer.flush();
				 writer.close();
	        }
	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	
	    //订单网银支付后台回调接口
		@RequestMapping(value="orderpay/request")
		public void orderpayRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
			// 银行请求商户：Plain,ResponseCode,Signature 
		    String recode = request.getParameter("ResponseCode"); 
		    String plain = request.getParameter("Plain"); 
		    String signature = request.getParameter("Signature");
			PrintWriter writer=response.getWriter();
		    try {
		    	VerifyParameterObject verifyParam=new VerifyParameterObject();
			    verifyParam.setPlain(plain);//明文
			    verifyParam.setPlainCharset("GBK");//明文使用的字符集
			    verifyParam.setSign(signature);//签名串
			    verifyParam.setType(0);//0-普通报文
			    verifyParam.setAlgorithm("MD5withRSA");//签名算法
//			    boolean verify=MerchantSignTool.verify(verifyParam);
			    boolean verify = CebMerchantSignVerify.merchantVerifyPayGate_ABA(signature, plain);
			    if(verify=false){
			    	System.out.println("验签失败");
			        throw new Exception("验签失败,非有效内容或签名");
			    }
			    else {
			    	/**切割该字符需要转义*/
				    String[] pairs = plain.split("~\\|~");		    

				    //获取商户号
				    String merchantIdSerial=pairs[1]; //得到merchantId=xxxxx
				    String[] merchants=merchantIdSerial.split("=");
				    String merchantId=merchants[1]; //得到商户号
				    
					 //获取订单编号
					 String orderIdSerial=pairs[2]; //得到orderId=xxxxx
					 String[] orderIds=orderIdSerial.split("=");
					 String orderId=orderIds[1];
					 
					 //获取交易金额
					 String transAmtSerial=pairs[3]; //得到transAmt=xxxxx
					 String[] transAmts=transAmtSerial.split("=");
					 String transAmt=transAmts[1]; //得到交易金额的字符串
					 
					 hyOrderService.addStoreOrderBankPayment(orderId,transAmt);
					 
					 String result="Plain=";
					 String plain2=pairs[1]+"~|~"; //merchantId
					 plain2=plain2+pairs[2]+"~|~"; //orderId
					 plain2=plain2+pairs[4]+"~|~"; //transDateTime
					 plain2=plain2+"procStatus=1"+"~|~";
					 //先用123服务器的
//					 response=response+"merURL2=http://admin.swczyc.com/hongyup/#/storeManagement/generalService/rechargeRecord";
					 plain2=plain2+"merURL2=";
					 plain2=plain2+merUrl2;
//					 plain2=plain2+Constants.merUrl2+"?";
//					 plain2=plain2+pairs[1]+"&"+pairs[2];
					 
					 SignParameterObject signParam = new SignParameterObject();
					 signParam.setMerchantId(merchantId);
					 signParam.setPlain(plain2);
					 signParam.setCharset("GBK");
					 signParam.setType(0);//0-普通报文
					 signParam.setAlgorithm("MD5withRSA");//签名算法
					 //返回给银行的签名信息
					 String signature2=MerchantSignTool.sign(signParam);
//					 response=response+"\r\n"+"ResponseCode=0000"+"\r\n";
					 result=result+plain2;
					 result=result+"\r\n";
					 result=result+"Signature="+signature2;
					 writer.print(result);
					 writer.flush();
					 writer.close();
		        }
		    }
		    catch(Exception e) {
		    	e.printStackTrace();
		    }
		}

		//网银分公司充值后台回调接口
		@RequestMapping(value="branchrecharge/request")
		public void branchRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
			// 银行请求商户：Plain,ResponseCode,Signature 
		    String recode = request.getParameter("ResponseCode"); 
		    String plain = request.getParameter("Plain"); 
		    String signature = request.getParameter("Signature");
		   // String recode="0000";
		    
			PrintWriter writer=response.getWriter();
		    try {
		    	VerifyParameterObject verifyParam=new VerifyParameterObject();
		    	
			    verifyParam.setPlain(plain);//明文
			    verifyParam.setPlainCharset("GBK");//明文使用的字符集
			    verifyParam.setSign(signature);//签名串
			    verifyParam.setType(0);//0-普通报文
			    verifyParam.setAlgorithm("MD5withRSA");//签名算法
			    //boolean verify=MerchantSignTool.verify(verifyParam);
			    boolean verify = CebMerchantSignVerify.merchantVerifyPayGate_ABA(signature, plain);
			    if(verify=false){
			    	System.out.println("验签失败");
			        throw new Exception("验签失败,非有效内容或签名");
			    }
			    else {
			    	/**切割该字符需要转义*/
				    String[] pairs = plain.split("~\\|~");		    

				    //获取商户号
				    String merchantIdSerial=pairs[1]; //得到merchantId=xxxxx
				    String[] merchants=merchantIdSerial.split("=");
				    String merchantId=merchants[1]; //得到商户号
				    
					 //获取订单编号
					 String orderIdSerial=pairs[2]; //得到orderId=xxxxx
					 
					 String[] orderIds=orderIdSerial.split("=");
					 String orderId=orderIds[1];
				
					 //根据orderId查找分公司充值记录
					 List<Filter> filters=new ArrayList<>();
					 filters.add(Filter.eq("bankChargeOrderSn", orderId));
					 //原则上一个订单编号只能查出来一条充值记录
					 List<BranchPreSave> branchPreSaveList=branchPreSaveService.findList(null,filters,null);
					 BranchPreSave branchPreSave=branchPreSaveList.get(0);
					 //修改状态为"成功支付"
					 branchPreSave.setType(8); //8-支付成功
					 BigDecimal preSaveBalance=branchPreSave.getPreSaveBalance().add(branchPreSave.getAmount());
					 //修改余额,加上充值金额
					 branchPreSave.setPreSaveBalance(preSaveBalance);
					 branchPreSaveService.update(branchPreSave);
					 Long branchId=branchPreSave.getBranchId(); //找到所属分公司
					 filters.clear();
					 filters.add(Filter.eq("branchId", branchId));
					 //找出分公司账户表,将分公司余额增加
					 List<BranchBalance> branchBalanceList=branchBalanceService.findList(null,filters,null);
					 //一个分公司只有一个账户
					 BranchBalance branchBalance=branchBalanceList.get(0);
					 //将充值金额加上
					 branchBalance.setBranchBalance(branchBalance.getBranchBalance().add(preSaveBalance));
					 branchBalanceService.update(branchBalance);
					 
					 String result="Plain=";
					 
					 String plain2=pairs[1]+"~|~"; //merchantId
					 plain2=plain2+pairs[2]+"~|~"; //orderId
					 plain2=plain2+pairs[4]+"~|~"; //transDateTime
					 plain2=plain2+"procStatus=1"+"~|~";
					 //先用123服务器的
//					 response=response+"merURL2=http://admin.swczyc.com/hongyup/#/storeManagement/generalService/rechargeRecord";
					 plain2=plain2+"merURL2=";
					 plain2=plain2+branchRechargeRedirectURL;
//					 plain2=plain2+Constants.merUrl2+"?";
//					 plain2=plain2+pairs[1]+"&"+pairs[2];
					 
					 SignParameterObject signParam = new SignParameterObject();
					 signParam.setMerchantId(merchantId);
					 signParam.setPlain(plain2);
					 signParam.setCharset("GBK");
					 signParam.setType(0);//0-普通报文
					 signParam.setAlgorithm("MD5withRSA");//签名算法
					 //返回给银行的签名信息
					 String signature2=MerchantSignTool.sign(signParam);
//					 response=response+"\r\n"+"ResponseCode=0000"+"\r\n";
					 result=result+plain2;
					 result=result+"\r\n";
					 result=result+"Signature="+signature2;
					 writer.print(result);
					 writer.flush();
					 writer.close();
		        }
		    }
		    catch(Exception e) {
		    	e.printStackTrace();
		    }
		}	
		
	/**
	 * 光大银行前台回调中转接口
	 * */
//	@RequestMapping("guangdaResultUrl2Jsp")
//	public ModelAndView showSignResult_jsp(){
//		ModelAndView mv = new ModelAndView();
//		mv.addObject("return_url",Constants.merUrl1);
//		mv.setViewName("GuangdaReturn");
//		return mv;
//	}
	
	/**光大银行前台回调重定向*/
	@RequestMapping("guangdaRedirect")
	public void guangdaRedirectUrl(HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.sendRedirect(merUrl1);
	}
	
	/**光大银行分公司充值前台回调重定向*/
	@RequestMapping("branchrecharge/redirect")
	public void branchRedirectUrl(HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.sendRedirect(branchRechargeForeURL);
	}
	
	/**单笔订单查询接口*/
	@RequestMapping(value="iqsr")
	@ResponseBody
	public Json iqsr(String originalorderId,String originalTransDateTime)
	{
		Json json=new Json();
		try {		
			Map<String,Object> map=new HashMap<String,Object>();
			String plain="transId=IQSR~|~";
//			plain=plain+"merchantId=365010000043~|~";
			plain=plain+"merchantId=370310000004~|~";
			plain=plain+"originalorderId=";
			plain=plain+originalorderId;
			plain=plain+"~|~originalTransDateTime=";
			plain=plain+originalTransDateTime;
			
			SignParameterObject signParam = new SignParameterObject();
			signParam.setMerchantId("370310000004");// 商户号,测试用商户号都是370310000004
//			signParam.setMerchantId("365010000043");//跨行支付用365010000043
			signParam.setTransId("IQSR"); //单笔订单查询
			signParam.setPlain(plain);// 明文
			signParam.setCharset("GBK");// 明文使用的字符集
			signParam.setType(0);// 0-普通报文,1-XML报文签名(使用JDK),2-XML报文签名(使用Apache)
			signParam.setAlgorithm("MD5withRSA");// 签名算法
			String sign = MerchantSignTool.sign(signParam);

			map.put("Plain", plain);
			map.put("Signature", sign);
			json.setMsg("传输成功");
			json.setObj(map);
			json.setSuccess(true);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("传输参数失败");
			e.printStackTrace();
		}
		return json;
	}	
	
	//根据账号找分公司
    public Department getCompany(HyAdmin hyAdmin){
		Department department = hyAdmin.getDepartment();
		while(!department.getIsCompany()){
			//如果当前部门不是公司，就找到他的父部门继续判断。
			department = department.getHyDepartment();
		}
		return department;
	}
}
