package com.hongyu.controller.liyang;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.http.annotation.Contract;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.controller.lbc.hsyPurchaseController;
import com.hongyu.entity.BankList;
import com.hongyu.entity.Department;
import com.hongyu.entity.FddContract;
import com.hongyu.entity.FddContractTemplate;
import com.hongyu.entity.FddDayTripContract;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCompany;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroupPrice;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.MhGroupPrice;
import com.hongyu.entity.MhLine;
import com.hongyu.entity.MhLineTravels;
import com.hongyu.entity.PayandrefundRecord;
import com.hongyu.entity.Store;
import com.hongyu.entity.TransportEntity;
import com.hongyu.service.FddContractService;
import com.hongyu.service.FddContractTemplateService;
import com.hongyu.service.FddDayTripContractService;
import com.hongyu.service.HyGroupPriceService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.util.contract.ContractInfoAboutHy;
import com.hongyu.util.contract.FddContractUtil;
import com.hongyu.util.liyang.EmployeeUtil;


/**
 * 法大大合同签约的异步回调接口
 * @author liyang
 *
 */
@Controller
@RequestMapping("/fddNotify/")
public class FddNotifyController {
	@Resource(name = "fddContractTemplateServiceImpl")
	FddContractTemplateService fddContractTemplateService;
	
	@Resource(name = "fddContractServiceImpl")
	FddContractService fddContractService;
	
	@Resource(name = "fddDayTripContractServiceImpl")
	FddDayTripContractService fddDayTripContractService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "hyGroupPriceServiceImpl")
	HyGroupPriceService hyGroupPriceService;
	
	
	@RequestMapping("autoSignNotifyUrl")
	public void getAutoSignResult(String transaction_id,String contract_id,
			String result_code,String result_desc,String download_url,
			String viewpdf_url,String timestamp,String msg_digest){
		if(result_code.equals("3000")){
			//签章成功
			if(contract_id.substring(0,2).equals("DX")){
				//如果是一日游合同
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("contractId", contract_id));
				List<FddDayTripContract> fddDayTripContracts = fddDayTripContractService.findList(null,filters,null);
				//设置合同状态为生成合同成功以及设置相应的下载和查看地址
				FddDayTripContract fdtc = fddDayTripContracts.get(0);
				//设置状态为虹宇已签约
				fdtc.setStatus(3);
				fdtc.setDownloadUrl(download_url);
				fdtc.setViewpdfUrl(viewpdf_url);
				fddDayTripContractService.update(fdtc);
			}else{
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("contractId", contract_id));
				List<FddContract> fddContracts = fddContractService.findList(null,filters,null);
				//设置合同状态为生成合同成功以及设置相应的下载和查看地址
				FddContract fc = fddContracts.get(0);
				//设置状态为虹宇已签约
				fc.setStatus(3);
				fc.setDownloadUrl(download_url);
				fc.setViewpdfUrl(viewpdf_url);
				fddContractService.update(fc);
			}		
		}	
	}
	/**
	 * 客户签章异步通知地址
	 * @param transaction_id
	 * @param contract_id
	 * @param result_code
	 * @param result_desc
	 * @param download_url
	 * @param viewpdf_url
	 * @param timestamp
	 * @param msg_digest
	 */
	@RequestMapping("customerSignNotifyUrl")
	public void getCustomerSignNotify(String transaction_id,String contract_id,
			String result_code,String result_desc,String download_url,
			String viewpdf_url,String timestamp,String msg_digest){
		if(result_code.equals("3000")){
			//签章成功
			FddContractUtil fddContractUtil = FddContractUtil.getInstance();
			if(contract_id.substring(0,2).equals("DX")){
				//如果是一日游合同
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("contractId", contract_id));
				List<FddDayTripContract> fddDayTripContracts = fddDayTripContractService.findList(null,filters,null);
				//设置合同状态为生成合同成功以及设置相应的下载和查看地址
				FddDayTripContract fdtc = fddDayTripContracts.get(0);
				//设置状态为客户已签约
				if(fdtc.getStatus() != 4){
					fdtc.setStatus(4);
					fdtc.setSignDate(new Date());
					fdtc.setDownloadUrl(download_url);
					fdtc.setViewpdfUrl(viewpdf_url);
					fddDayTripContractService.update(fdtc);
				}
				
			}else{
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("contractId", contract_id));
				List<FddContract> fddContracts = fddContractService.findList(null,filters,null);
				//设置合同状态为生成合同成功以及设置相应的下载和查看地址
				FddContract fc = fddContracts.get(0);
				//设置状态为客户已签约
				if(fc.getStatus() != 4){
					fc.setStatus(4);
					fc.setSignDate(new Date());
					fc.setDownloadUrl(download_url);
					fc.setViewpdfUrl(viewpdf_url);
					fddContractService.update(fc);
				}
			}
			//调用合同归档接口，确认签署完成
			fddContractUtil.ContractFiling(contract_id);
		}	
	}
	/**
	 * 客户签章同步通知地址
	 * @param transaction_id
	 * @param contract_id
	 * @param result_code
	 * @param result_desc
	 * @param download_url
	 * @param viewpdf_url
	 * @param timestamp
	 * @param msg_digest
	 */
	@RequestMapping("customerSignResultUrl")
	@ResponseBody
	public Json getCustomerSignResult(String transaction_id,String contract_id,
			String result_code,String result_desc,String download_url,
			String viewpdf_url,String timestamp,String msg_digest){
		Json json = new Json();
		if(result_code.equals("3000")){
			//签章成功
			HashMap<String, Object> hashMap = new HashMap<>();
			hashMap.put("download_url", download_url);
			hashMap.put("viewpdf_url", viewpdf_url);
			hashMap.put("contract_id", contract_id);
			json.setSuccess(true);
			json.setMsg("签章成功");
			json.setObj(hashMap);
		}else{
			HashMap<String, Object> hashMap = new HashMap<>();
			hashMap.put("result_code", result_code);
			hashMap.put("result_desc", result_desc);
			hashMap.put("contract_id", contract_id);
			json.setSuccess(false);
			json.setMsg("签章失败");
			json.setObj(hashMap);
		}
		return json;
	}
	/**
	 * 客户手机端签署成功之后的跳转页面，主要是提供下载地址和展示合同地址
	 * 但是没法解决中文乱码问题，只能使用英文字符
	 * @param transaction_id
	 * @param contract_id
	 * @param result_code
	 * @param result_desc
	 * @param download_url
	 * @param viewpdf_url
	 * @param timestamp
	 * @param msg_digest
	 * @return
	 */
	@RequestMapping("customerSignResultUrl2html")
	@ResponseBody
	public String getCustomerSignResult_html(String transaction_id,String contract_id,
			String result_code,String result_desc,String download_url,
			String viewpdf_url,String timestamp,String msg_digest){
		StringBuilder sb = new StringBuilder();
		if(result_code.equals("3000")){
			//签章成功
			sb.append("<html>");
			sb.append("<head>");
			sb.append("<meta http-equiv=\"content-type\" content=\"txt/html; charset=utf-8\" />");
			sb.append("<title>合同下载页</title>");
			sb.append("</head>");
			sb.append("<style></style>");
			sb.append("<body>");
			sb.append("<div style=\"text-align: center;background-color:rgb(54, 138, 247);border-radius: 20px;width: 90%;height: 90%;margin: auto;position: absolute;color: aliceblue;top:0;left:0;right:0;bottom:0\">");
			sb.append("<p style=\"text-align:center\">");
			sb.append("<button style=\"font-size:60;background-color:rgb(54, 138, 247);border-radius: 10px;border:1px;width: 60%;height: 40%;color:aliceblue \">Customer Sign Success!</button>");
			sb.append("</p>");
			sb.append("<p style=\"text-align:center\"><a href=\""+download_url+"\" ><button style=\"font-size:40;background-color:#fff;border-radius: 10px;border:1px;width: 60%;height: 20%;color: rgb(54, 138, 247)\">download</button></a></p>");
			sb.append("<p style=\"text-align:center;width: 60%;height: 5%;\"></p>");
			sb.append("<p style=\"text-align:center\"><a href=\""+viewpdf_url+"\" ><button style=\"font-size:40;background-color:rgb(54, 138, 247);border-radius: 10px;border-width:2px;border-style:solid;border-color:#fff;width: 60%;height: 20%;color: aliceblue\">viewpdf</button></a></p>");
			sb.append("</div>");
			sb.append("</body>");
			sb.append("</html>");
			
		}else{
			sb.append("<html>");
			sb.append("<style></style>");
			sb.append("<body><h1>sign failed!</h1></body>");
			sb.append("</html>");
		}
		return sb.toString();
	}
	/**
	 * 法大大签章完成之后跳转到该页面，显示下载按钮和展示按钮。
	 * 使用jsp页面解决了中文乱码问题
	 * @param transaction_id
	 * @param contract_id
	 * @param result_code
	 * @param result_desc
	 * @param download_url
	 * @param viewpdf_url
	 * @param timestamp
	 * @param msg_digest
	 * @return
	 */
	@RequestMapping("customerSignResultUrl2Jsp")
	public ModelAndView showSignResult_jsp(String transaction_id,String contract_id,
			String result_code,String result_desc,String download_url,
			String viewpdf_url,String timestamp,String msg_digest){
		ModelAndView mv = new ModelAndView();
		if(result_code.equals("3000")){
			mv.addObject("download_url",download_url);
			mv.addObject("viewpdf_url",viewpdf_url);
			mv.setViewName("fddNotify");
		}else{
			mv.setViewName("fddNotify");
		}
		return mv;
	}
	/**
	 * 根据订单id给官网返回一个合同
	 * @param id
	 * @return
	 */
	@RequestMapping("contract")
	@ResponseBody
	public Json getContract(Long orderId){
		Json json = new Json();
		try {
			//先判断合同类型
			HyOrder hyOrder = hyOrderService.find(orderId);
			FddContractUtil fddContractUtil = FddContractUtil.getInstance();
			int contractType = 0;
			//合同类型   0--国内一日游  1--国内游  2--境外游
			if(hyOrder.getXianlutype()==null){
				throw new Exception("线路类型为空");	
			}
			HyGroup hyGroup = hyGroupService.find(hyOrder.getGroupId());
			//获取线路信息
			HyLine line = hyGroup.getLine();
			MhLine mhLine = line.getMhLine();
			if(mhLine==null)
				throw new Exception("该线路没有完善过！");
			if(!line.getIsGuanwang()){
				throw new Exception("该线路没有在官网上线！！");
			}
			if(hyGroup.getMhState()==0){
				throw new Exception("该团还有没有经过网络销售部完善！");
			}
			if(hyOrder.getXianlutype()==0){
				//是一日游的合同
				contractType = 0;
				//FddDayTripContract fddDayTripContract = (FddDayTripContract)res.get("fddDayTripContract");
				
				//如果该订单没有之前没有创建过合同，就创建一个新的合同。
				FddDayTripContract fddDayTripContract = new FddDayTripContract();	
				//生成合同号，前缀“DT”+orderId
				fddDayTripContract.setContractId("DT"+hyOrder.getOrderNumber());
				//保存订单id
				fddDayTripContract.setOrderId(hyOrder.getId());
				//签署人
				fddDayTripContract.setCustomer(hyOrder.getContact());		
				//签署人号码
				fddDayTripContract.setCustomerPhone(hyOrder.getPhone());
				//签署人证件号
				fddDayTripContract.setCustomerIDNum(hyOrder.getContactIdNumber());
				//团里人数
				fddDayTripContract.setCustomerNum(hyOrder.getPeople());
				//除保险外的订单结算价
				fddDayTripContract.setTotalPrice(hyOrder.getJiesuanMoney1());
				
				
				fddDayTripContract.setLineInfo(mhLine.getBriefDescription());
				//获取线路信息
				MhLineTravels travels = mhLine.getMhLineTravels().get(0);
				
				fddDayTripContract.setLineInfo(travels.getRoute());
				TransportEntity transportEntity = travels.getTransport();
				/**设置交通信息*/
				fddDayTripContract.setTrafficType(0);
				fddDayTripContract.setTrafficStandard(transportEntity.getName());	
				/**设置团费包含内容*/
				fddDayTripContract.setFeeNote(mhLine.getFeeDescription());
				/**设置团费包含内容*/
				fddDayTripContract.setFeeNote(mhLine.getFeeDescription());
				List<Filter> filters = new ArrayList<>();
				filters.add(Filter.eq("orderId", hyOrder.getId()));
				List<PayandrefundRecord> payandrefundRecords = payandrefundRecordService.findList(null,filters,null);
				for(PayandrefundRecord tmp : payandrefundRecords){
					if(tmp.getType() == 0 && tmp.getStatus()== 1){
						//支付方式
						fddDayTripContract.setPaymentType(tmp.getPayMethod());
						//支付时间
						fddDayTripContract.setPaymentTime(tmp.getCreatetime());
						break;
					}
				}
				//此处填写票价，现在这个里面可以拿到priceId和productId，但是不知道去哪个表查
				List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
				for(HyOrderItem tmp : hyOrderItems){
					//筛选出成人和儿童，然后获取票价
					/**当订单条目类型type为1线路
					 * priceType：0普通成人价，1普通儿童价，2普通学生价，3普通老人价，4特殊价格
					 */
					if(tmp.getType() == 1 && tmp.getPriceType()==0){
						//获取成人价
						HyGroupPrice hyGroupPrice = hyGroupPriceService.find(tmp.getPriceId());
						MhGroupPrice mhGroupPrice = hyGroupPrice.getMhGroupPrice();
						if(mhGroupPrice==null)
							throw new Exception("没有官网成人价");
						fddDayTripContract.setAdultTicketPrice(mhGroupPrice.getMhAdultSalePrice());
						continue;
					}
					if(tmp.getType() == 1 && tmp.getPriceType()==1){
						//获取儿童价
						HyGroupPrice hyGroupPrice = hyGroupPriceService.find(tmp.getPriceId());
						MhGroupPrice mhGroupPrice = hyGroupPrice.getMhGroupPrice();
						if(mhGroupPrice==null)
							throw new Exception("没有官网儿童价");
						fddDayTripContract.setChildrenTicketPrice(mhGroupPrice.getMhChildrenSalePrice());
						continue;
					}
				}
				/*设置官网订单总价格是jiusuanmoney*/
				fddDayTripContract.setTotalPrice(hyOrder.getJiusuanMoney());
				fddDayTripContract.setHyOperator(ContractInfoAboutHy.hyWLXSBOperator);
				fddDayTripContract.setHyAddress(ContractInfoAboutHy.hyWLXSBAddress);
				fddDayTripContract.setHyName(ContractInfoAboutHy.hyName);
				fddDayTripContract.setHyPhone(ContractInfoAboutHy.hyPhone);
				//请求法大大生成该合同
				List<Filter> filters11 = new ArrayList<>();
				filters11.add(Filter.eq("type", 0));
				FddContractTemplate template = fddContractTemplateService.findList(null,filters11,null).get(0);
				System.out.println("模板id是"+template.getTemplateId());
				HyOrder dingdan = hyOrderService.find(fddDayTripContract.getOrderId());
				HashMap<String, Object> tables = fddDayTripContractService.getMhDynamicTables(dingdan);
				Json gcResult = fddContractUtil.generateDayTripContract(fddDayTripContract,template,tables);
				if(gcResult.isSuccess()){
					json.setMsg("获取合同样本成功");
					json.setSuccess(true);
					json.setObj(gcResult.getObj());
				}else{
					json.setMsg("获取合同样本失败："+gcResult.getMsg());
					json.setSuccess(false);
				}
			}else{
				//是国内或者境外游
				FddContract fddContract = fddContractUtil.hyOrder2fddContract(hyOrder);
				if(hyGroup!=null){
					fddContract.setEndTime(hyGroup.getEndDay());
				}
				//签署人身份证号码
				fddContract.setCustomerIDNum(hyOrder.getContactIdNumber());
				List<Filter> filters = new ArrayList<>();
				filters.clear();
				filters.add(Filter.eq("orderId", hyOrder.getId()));
				List<PayandrefundRecord> payandrefundRecords = payandrefundRecordService.findList(null,filters,null);
				for(PayandrefundRecord tmp : payandrefundRecords){
					if(tmp.getType() == 0 && tmp.getStatus()== 1){
						//支付方式
						fddContract.setPaymentMethod(tmp.getPayMethod());
						//支付时间
						fddContract.setPaymentTime(tmp.getCreatetime());
						break;
					}
				}
				List<HyOrderItem> hyOrderItems = hyOrder.getOrderItems();
				for(HyOrderItem tmp : hyOrderItems){
					//筛选出成人和儿童，然后获取票价
					/**当订单条目类型type为1线路
					 * priceType：0普通成人价，1普通儿童价，2普通学生价，3普通老人价，4特殊价格
					 */
					if(tmp.getType() == 1 && tmp.getPriceType()==0){
						//获取官网成人销售价。。。成人价
						
						HyGroupPrice hyGroupPrice = hyGroupPriceService.find(tmp.getPriceId());
						MhGroupPrice mhGroupPrice = hyGroupPrice.getMhGroupPrice();
						if(mhGroupPrice==null)
							throw new Exception("没有官网成人价");
						fddContract.setAdultTicketPrice(mhGroupPrice.getMhAdultSalePrice());
						continue;
					}
					if(tmp.getType() == 1 && tmp.getPriceType()==1){
						//获取儿童价
						HyGroupPrice hyGroupPrice = hyGroupPriceService.find(tmp.getPriceId());
						MhGroupPrice mhGroupPrice = hyGroupPrice.getMhGroupPrice();
						if(mhGroupPrice==null)
							throw new Exception("没有官网儿童价");
						fddContract.setChildrenTicketPrice(mhGroupPrice.getMhChildrenSalePrice());
						continue;
					}
				}
				List<MhLineTravels> lineTravels = mhLine.getMhLineTravels();
				int zhusutianshu = 0;
				for(MhLineTravels ml:lineTravels){
					if(ml.getIfAccommodation()==1){
						zhusutianshu++;
					}
				}
				fddContract.setStayAtHotelDays(zhusutianshu);
				//设置注意事项
				fddContract.setOtherNote(mhLine.getMattersNeedAttention());
				/*设置官网订单总价格是jiusuanmoney*/
				fddContract.setTotalPrice(hyOrder.getJiusuanMoney());
				fddContract.setHyOperator(ContractInfoAboutHy.hyWLXSBOperator);
				fddContract.setHyAddress(ContractInfoAboutHy.hyWLXSBAddress);
				fddContract.setHyName(ContractInfoAboutHy.hyName);
				fddContract.setHyPhone(ContractInfoAboutHy.hyPhone);
				//请求法大大生成该合同
				List<Filter> filters11 = new ArrayList<>();
				filters11.add(Filter.eq("type", fddContract.getType()));
				FddContractTemplate template = fddContractTemplateService.findList(null,filters11,null).get(0);
				//System.out.println("模板id是"+template.getTemplateId());
				HyOrder dingdan = hyOrderService.find(fddContract.getOrderId());
				HashMap<String, Object> tables = fddContractService.getMhDynamicTables(dingdan);
				Json gcResult = fddContractUtil.generateContract(fddContract,template,tables);
				if(gcResult.isSuccess()){
					json.setMsg("获取合同样本成功");
					json.setSuccess(true);
					json.setObj(gcResult.getObj());
				}else{
					json.setMsg("获取合同样本失败："+gcResult.getMsg());
					json.setSuccess(false);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("获取合同样本失败："+e.getMessage());
			json.setSuccess(false);
		}
		return json;
	}
}
