package com.hongyu.controller.wj;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.ejb.criteria.path.AbstractFromImpl.JoinScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.grain.service.user.AdminService;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.entity.BusinessOrder;
import com.hongyu.entity.BusinessOrderItem;
import com.hongyu.entity.CouponBigCustomer;
import com.hongyu.entity.CouponBigCustomerAccount;
import com.hongyu.entity.CouponSale;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.GuideSettlement;
import com.hongyu.entity.GuideSettlementDetail;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.HySupplierElement;
import com.hongyu.entity.PayGuider;
import com.hongyu.entity.PayServicer;
import com.hongyu.entity.Store;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.service.BusinessOrderItemService;
import com.hongyu.service.BusinessOrderService;
import com.hongyu.service.CouponBigCustomerAccountService;
import com.hongyu.service.CouponBigCustomerService;
import com.hongyu.service.CouponSaleService;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.GuideSettlementDetailService;
import com.hongyu.service.GuideSettlementService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.PayGuiderService;
import com.hongyu.service.PayServicerService;
import com.hongyu.service.PiaowuConfirmService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants;
import com.hongyu.util.SendMessageEMY;


/**预付款的
 * 冲抵记录表和预付款记录表显示
 *
 */
@Controller
//@RequestMapping("/admin/prePaySupply/offset")
@RequestMapping("/prePaySupply/offset")
public class OffsetRecordController {
	@Value("${coupon.urlForCouponSMS}")
	private String urlForCoupon;
	
	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;
	
	
	@RequestMapping("/list") 
	@ResponseBody
	public Json list(Long departmentId){
		Json json = new Json();
		try {
			
			
			json.setMsg("查询成功");
			json.setSuccess(false);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("查询失败");
			json.setSuccess(false);
		}
		
		return json;
		
	}
	
	
	
	
	
	
	
	
	/**
	 * 根据departmentid查询所有预付款记录
	 * @param departmentId
	 * @return
	 */
	
	@RequestMapping("/paylist") 
	@ResponseBody
	public Json supplierList(Long departmentId){
		
		Json json = new Json();
		try {
			BigDecimal paySum = new BigDecimal(0);
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("departmentId",departmentId ));
			List<PayServicer> payServicer = payServicerService.findList(null, filters, null);
			List<HashMap<String, Object>> res = new ArrayList<>();
			HashMap<String, Object> map = new HashMap<>();
			
			for(PayServicer p: payServicer){
				HashMap<String, Object>  m = new HashMap<>();
				m.put("appliName", p.getAppliName());  //申请人名称
				m.put("applyDate", p.getApplyDate());
				m.put("payDate", p.getPayDate());
				m.put("remark", p.getRemark());
				m.put("amount", p.getAmount());
				
				paySum = paySum.add(p.getAmount());
				res.add(m);
			}
			map.put("list", res);
			map.put("paySum", paySum);
			
			
			json.setObj(map);		
			json.setSuccess(true);
			json.setMsg("查询成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("查询失败");
		}
		return json;
		
		
		
	}
	
	
	/**
	 * 冲抵记录列表 根据departmentid拿到该部门的所有冲抵记录
	 * @param departmentid  //0：已冲清  1：未冲清
	 * @return
	 */
	@RequestMapping("/paidlist") 
	@ResponseBody
	public Json offsetList(Long departmentid){
		Json json = new Json();
		try {
			BigDecimal pay;
			BigDecimal balance;
			
			
			
			
			
			json.setSuccess(true);
			json.setMsg("查询成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("查询失败");
		}
		return json;
		
		
	}

	
	
	@Resource(name = "guideSettlementDetailServiceImpl")
	GuideSettlementDetailService guideSettlementDetailService;
	
	@Resource(name = "guideServiceImpl")
	GuideService guideService;
	
	@Resource(name = "payGuiderServiceImpl")
	PayGuiderService payGuiderService;
	
	@Resource(name = "guideSettlementServiceImpl")
	GuideSettlementService guideSettlementService;
	
	@RequestMapping("/guide")
	public Json guide(){
		Json json = new Json();
		System.out.println("导游周期结算");
		
		Date date = new Date();
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);//取前一个月的同一天  
		Date startDate = cal.getTime();  
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String end = sDateFormat.format(date);
		Date tDate = new Date();
		try {
			 tDate = sDateFormat.parse(end);
			System.out.println(tDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		
		try {
			StringBuilder jpql = new StringBuilder(
					"SELECT guider_id,sum(account_payable) from hy_guide_settlement_detail WHERE `status`=0 "
					+ "and is_can_settle = true GROUP BY guider_id ");
			List<Object[]> list = guideSettlementDetailService.statis(jpql.toString());
		
			for (Object[] o : list) {
				System.out.println(o[0]);
				
				Integer guiderId = (Integer) o[0];
				Long guider =guiderId.longValue();
				BigDecimal amount = (BigDecimal) o[1];
				
				Guide g = guideService.find(guider);
				GuideSettlement guideSettlement = new GuideSettlement();
				guideSettlement.setEndDate(tDate);
				guideSettlement.setStartDate(startDate);
				guideSettlement.setGuideId(guiderId);
				guideSettlement.setName(g.getName());
				guideSettlement.setSn(g.getTouristCertificateNumber());
				guideSettlement.setTotalAmount(amount);			
				
				guideSettlementService.save(guideSettlement);
				
				PayGuider payGuider = new PayGuider();
				payGuider.setGuiderId(guider);
				payGuider.setType(2);
				payGuider.setHasPaid(0);
				payGuider.setGuider(g.getName());
				payGuider.setAmount(amount);
				payGuider.setBankName(g.getBankName());
				payGuider.setAccountName(g.getAccountName());
				payGuider.setBankAccount(g.getBankAccount());
				payGuider.setBankLink(g.getBankLink());			
				payGuider.setSettlementId(guideSettlement.getId());
				payGuiderService.save(payGuider);
	
			}
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("endDate", tDate));

			List<GuideSettlement> list2 = guideSettlementService.findList(null,filters,null);
			for(GuideSettlement g:list2){
				List<Filter> filters2 = new ArrayList<>();
				filters2.add(Filter.eq("status", 0));
				filters2.add(Filter.eq("guiderId", g.getGuideId()));
				List<GuideSettlementDetail> guideSettlementDetails = guideSettlementDetailService.findList(null,filters2,null);
				for(GuideSettlementDetail gui:guideSettlementDetails){
					gui.setSettlementId(g.getId());
					gui.setStatus(1);
					guideSettlementDetailService.update(gui);
				}
			}			
			System.out.println("导游周期结算成功");		
			
			json.setMsg("失败");
			json.setSuccess(false);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("成功");
			json.setSuccess(true);
			System.out.println("导游周期结算失败");
		}
		return json;
	}
	
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@RequestMapping("/settlement")
	@ResponseBody
	public  Json insertGuideSettlementDetail(String ceshi1){
		Json json = new Json();
		try {
//			Date today = new Date();
//			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//			String stoday = sDateFormat.format(today);
//			String ceshi = "2018-08-03";
//			
//			List<Filter> filters = new ArrayList<>();
//			filters.add(Filter.eq("guideCheckStatus", 1));
//			filters.add(Filter.eq("status", 3));
//			filters.add(Filter.eq("huituanxinxi", ceshi));
//			List<HyOrder> orders = hyOrderService.findList(null,filters,null);
//			
//			for(HyOrder hyorder:orders){
//				
//				Long orderId = hyorder.getId();
//				HyOrder hyOrder = hyOrderService.find(orderId);
//				List<Filter> filters2 = new ArrayList<>();
//				filters2.add(Filter.eq("orderId", orderId));
//				List<GuideAssignment> guideAssignments = guideAssignmentService.findList(null,filters2,null);
//				Store store = storeService.find(hyOrder.getStoreId());
//				
//				
//				GuideSettlementDetail guideSettlementDetail = new GuideSettlementDetail();
//				guideSettlementDetail.setOrderId(orderId);
//				
//				if(guideAssignments.size()!=0){
//					GuideAssignment guideAssignment = guideAssignments.get(0);
//					guideSettlementDetail.setPaiqianId(guideAssignment.getId());
//					guideSettlementDetail.setServiceFee(guideAssignment.getServiceFee());
//					guideSettlementDetail.setAccountPayable(guideAssignment.getTotalFee());
//					guideSettlementDetail.setGuiderId(guideAssignment.getGuideId());
//				}else{
//					guideSettlementDetail.setPaiqianId(null);
//				}
//				guideSettlementDetail.setGroupId(hyOrder.getGroupId());
//				guideSettlementDetail.setDispatchType(1);
//				guideSettlementDetail.setServiceType(hyOrder.getFuwutype());
//				guideSettlementDetail.setStartDate(hyOrder.getFatuandate());
//				guideSettlementDetail.setLine(hyOrder.getXianlumingcheng());
//				guideSettlementDetail.setDays(hyOrder.getTianshu());
//				guideSettlementDetail.setRentStoreId(hyOrder.getStoreId());
//				guideSettlementDetail.setRentStore(store.getStoreName());
//				guideSettlementDetail.setTip(hyOrder.getTip());
//				guideSettlementDetail.setDeductFee(new BigDecimal(0));
//				guideSettlementDetail.setIsCanSettle(true);
//				guideSettlementDetail.setStatus(0);
//				guideSettlementDetailService.save(guideSettlementDetail);	
//				
//				hyorder.setStatus(9);
//				hyOrderService.update(hyorder);
//				
//				
//				
//				
//			}
			
			Date today = new Date();
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String stoday = sDateFormat.format(today);
			String ceshi = "2018-08-08";
			Date date = sDateFormat.parse(stoday);
			Date dceshi = sDateFormat.parse(ceshi1);
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("status", 1));   //已确认
			filters.add(Filter.eq("endDate", dceshi));   //结束日期

			List<GuideAssignment> guideAssignments = guideAssignmentService.findList(null,filters,null);
			
			for(GuideAssignment guideAssignment:guideAssignments){
				
				GuideSettlementDetail guideSettlementDetail = new GuideSettlementDetail();
				guideSettlementDetail.setOrderId(guideAssignment.getOrderId());
				guideSettlementDetail.setPaiqianId(guideAssignment.getId());
				guideSettlementDetail.setServiceFee(guideAssignment.getServiceFee());
				guideSettlementDetail.setAccountPayable(guideAssignment.getTotalFee());
				guideSettlementDetail.setGuiderId(guideAssignment.getGuideId());
				guideSettlementDetail.setGroupId(guideAssignment.getGroupId());
				guideSettlementDetail.setDispatchType(guideAssignment.getAssignmentType());
				guideSettlementDetail.setServiceType(guideAssignment.getServiceType());
				guideSettlementDetail.setStartDate(guideAssignment.getStartDate());
				guideSettlementDetail.setLine(guideAssignment.getLineName());
				guideSettlementDetail.setDays(guideAssignment.getDays());
				guideSettlementDetail.setTip(guideAssignment.getTip());
				guideSettlementDetail.setDeductFee(new BigDecimal(0));
				guideSettlementDetail.setIsCanSettle(true);
				guideSettlementDetail.setStatus(0);
				
				Long orderId = guideAssignment.getOrderId();
				if(orderId!=null){
					HyOrder hyOrder = hyOrderService.find(orderId);
					guideSettlementDetail.setRentStoreId(hyOrder.getStoreId());
					
					List<Filter> filters2 = new ArrayList<>();
					filters2.add(Filter.eq("orderId", orderId));
					Store store = storeService.find(hyOrder.getStoreId());
					guideSettlementDetail.setRentStore(store.getStoreName());
					
					if(hyOrder.getType()==0){
						hyOrder.setStatus(9);
						hyOrderService.update(hyOrder);
					}
					
				}
				
				
				guideSettlementDetailService.save(guideSettlementDetail);	
				System.out.println("生成租界导游结详情");
			}
		System.out.println("生成租界导游结详情成功");
			
			
		json.setMsg("成功");
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("失败");
			json.setSuccess(false);
		}
		
		return json;
		
		
	}
	
	@Resource(name = "couponSaleServiceImpl")
	CouponSaleService couponSaleService;

	@Resource(name = "couponBigCustomerServiceImpl")
	CouponBigCustomerService couponBigCustomerService;
	
	@RequestMapping("/test")
	@ResponseBody
	public Json send(){
		Json json = new Json();
//		CouponSale couponSale = couponSaleService.find((long)12);/
//		String sum = couponSale.getSum() + "元";
//		String num = couponSale.getCouponCode();
//		String code = couponSale.getActivationCode();
////		String phone = couponSale.getReceiverPhone().toString();
//		String phone = "15210780793".toString();
//		
//		String message = "{\"sum\":\""+sum+"\",\"num\":\""+num+"\",\"code\":\""+code+"\"}";
//		System.out.println(message);
//		boolean isSuccess = SendMessageEMY.businessSendMessage(phone,message,3);
//		System.out.println(isSuccess);
		
		
		CouponBigCustomer list = couponBigCustomerService.find((long)689);
		String num = list.getCouponCode();
		String code = list.getActivationCode();
		String phone = "15210780793".toString();
		String sum = list.getSum() + "元";
		String message = "{\"sum\":\""+sum+"\",\"num\":\""+num+"\",\"code\":\""+code+"\"}";
		SendMessageEMY.sendMessage(phone,message,3);
		json.setMsg("success");
		json.setSuccess(true);
		return json;
	}
	
	@Resource(name = "couponBigCustomerAccountServiceImpl")
	CouponBigCustomerAccountService couponBigCustomerAccountService;
	@RequestMapping("/test2")
	@ResponseBody
	public Json send2(){
		Json json = new Json();
		
		CouponBigCustomer list = couponBigCustomerService.find((long)689);
		StringBuilder sb = new StringBuilder(list.getSuffixurl());
		sb.insert(0, urlForCoupon);
//		String str = "【虹宇国际旅行社】您已成功购买了" + amount + "张" + list.get(0).getSum() + "元的电子券！您可以在 " + sb.toString()
//				+ " 中进行查看!";
		
		//write by wj
		CouponBigCustomerAccount c = couponBigCustomerAccountService.find((long)54);
		final int amount = c.getNum();
		String sum = amount + "张" + list.getSum() + "元";
		String code = sb.toString();
		String message = "{\"sum\":\""+sum+"\",\"code\":\""+code+"\"}";
		System.out.println(message);
		String phone = "15210780793".toString();
		boolean isSuccess = SendMessageEMY.sendMessage(phone, message,4);
		return json;
	}
	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderService;
	@Resource(name = "businessOrderItemServiceImpl")
	BusinessOrderItemService businessOrderItemService;
	
	@RequestMapping("/test3")
	@ResponseBody
	public Json send3(){
		Json json = new Json();
		BusinessOrder businessOrder =businessOrderService.find((long)5);
		StringBuilder sb1 = new StringBuilder();
		for (BusinessOrderItem item : businessOrder.getBusinessOrderItems()) {
			sb1.append(businessOrderItemService.getSpecialtyName(item) + "("
					+ businessOrderItemService.getSpecificationName(item) + "*" + item.getQuantity() + ")；");
		}
		String amount = businessOrder.getPayMoney().setScale(2, BigDecimal.ROUND_HALF_UP) + "元";
		String code = businessOrder.getOrderCode();
		String product = sb1.toString();
		String phone = "15210780793".toString();
		String message = "{\"amount\":\""+amount+"\",\"code\":\""+code+"\",\"product\":\""+product+"\"}";
//		SendMessageEMY.sendMessage(phone, sb1.toString());
		SendMessageEMY.businessSendMessage(phone,message,5);
		
		
		json.setMsg("success");
		return json;
	}
	
	@Resource(name = "piaowuConfirmServiceImpl")
	PiaowuConfirmService piaowuConfirmService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;
	
	@RequestMapping("/test44")
	@ResponseBody
	public void test44(){
		HyAdmin liable = hyAdminService.find("1112wgys");
		List<Filter> filters = new ArrayList<>();
//      filters.add(Filter.eq("ContractStatus", ContractStatus.zhengchang.ordinal()));
      filters.add(Filter.eq("liable", liable));
      HySupplierContract contract =  new HySupplierContract();
      List<HySupplierContract> contracts = hySupplierContractService.findList(null,filters,null);

      for(HySupplierContract con : contracts){
      	if(con.getContractStatus().equals(ContractStatus.zhengchang)){
      		contract = con;
      	}
      }
      if(contract.getId() == null)
		try {
			throw new Exception("没有正常状态的合同");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
