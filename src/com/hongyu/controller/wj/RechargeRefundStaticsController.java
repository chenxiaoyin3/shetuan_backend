package com.hongyu.controller.wj;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Json;
import com.hongyu.controller.wj.PaymentSupplierStaticsController.PayStatics;
import com.hongyu.entity.Store;
import com.hongyu.entity.HyLine.RefundTypeEnum;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreService;

/**
 * 门店预充值退款报表
 * 1.查询条件：退款的门店、订单编号、产品编号、产品名称、团期（发团、回团日期）、退款时间；
 * 2.列表表头：退款门店、订单编号、产品编号、产品名称、团期、报名计调、退款金额、退款时间、退款类型、审核时间；
 * 3.不需要分页
 * 4.需要导出Excel文件。
 * @author wj
 *
 */
@Controller
@RequestMapping("/admin/rechargeRefundStatics")
public class RechargeRefundStaticsController {
	
	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;
	
	@Resource(name="storeServiceImpl")
	StoreService storeService;
	
	@RequestMapping("/list/view") 
	@ResponseBody
	public Json list(String storeName,String orderSn,String productSn,String productName,Integer retype,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date fatuanDate1,@DateTimeFormat(pattern="yyyy-MM-dd") Date fatuanDate2,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date refundDate1,@DateTimeFormat(pattern="yyyy-MM-dd") Date refundDate2){
		
		Json json = new Json ();
		try {
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("select * from (");
			buffer.append(" select res2.store_name storename,res2.order_number ordernumber,res2.fatuandate fatuandate,CAST(res2.money AS DECIMAL(21, 3)) money,res2.jidiao jidiao,res2.refundtype refundtype ,res2.date refunddate, ");
            buffer.append(" case res2.type ");
            buffer.append(" when 0 then (select name from hy_guide g where g.id = res2.productid) ");
            buffer.append(" when 1 then (select group_line_name from hy_group where id = res2.productid) ");
            buffer.append(" when 2 then (select scene_name from hy_ticket_subscribe s where s.id = res2.productid)");
            buffer.append(" when 3 then (select hotel_name from hy_ticket_hotel h where h.id =res2.productid) ");
            buffer.append(" when 4 then (select scene_name from hy_ticket_scene s where s.id = res2.productid) ");
            buffer.append(" when 5 then (select product_name from hy_ticket_hotelandscene h where h.id = res2.productid) ");
            buffer.append(" when 6 then (select remark from hy_insurance i where i.id = res2.productid)");
            buffer.append(" when 7 then (select product_name from hy_visa v where v.id = res2.productid) ");
            buffer.append(" end  productname, case res2.type ");
            buffer.append(" when 0 then (select sn from hy_guide g where g.id = res2.productid)");
            buffer.append(" when 1 then (select group_line_pn from hy_group where id = res2.productid) ");
            buffer.append(" when 2 then (select sn from hy_ticket_subscribe s where s.id = res2.productid)");
            buffer.append(" when 3 then (select pn from hy_ticket_hotel h where h.id =res2.productid)");
            buffer.append(" when 4 then (select pn from hy_ticket_scene s where s.id = res2.productid)");
            buffer.append(" when 5 then (select product_id from hy_ticket_hotelandscene h where h.id = res2.productid)");
            buffer.append(" when 6 then (select insurance_code from hy_insurance i where i.id = res2.productid)");
            buffer.append(" when 7 then (select product_id from hy_visa v where v.id = res2.productid)");
            buffer.append(" end  productsn from");
            buffer.append(" (select store.store_name,o.order_number ,o.type,(select name from hy_admin where o.supplier = username) jidiao,o.supplier,res1.* ,");
            buffer.append(" (select item.product_id from hy_order_item item where item.order_id = res1.orderid limit 1)as productid");
            buffer.append(" from hy_store store,hy_order o,");
            buffer.append(" (select o.id orderid,o.store_id storeid,o.fatuandate,log.money money,log.create_date date,log.type refundtype from hy_store_account_log log ,hy_order o");
            buffer.append(" where log.type>2 and log.type!=6 and log.type!=14");
            buffer.append(" and log.order_sn = o.order_number) res1 ");
            buffer.append(" where store.id = res1.storeid and o.id = res1.orderid) res2 ) res3 ");
            
            boolean useWhere = false;
            if(storeName != null){
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	buffer.append(" res3.storename = '" + storeName +"' ");
            }
            if(orderSn != null){
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	buffer.append(" res3.ordernumber = '" + orderSn +"' ");
            }
            if(productSn != null){
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	buffer.append(" res3.productsn = '" + productSn +"' ");
            }
            if(productName != null){
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	buffer.append(" res3.productname = '" + productName +"' ");
            }
            if(fatuanDate1 != null && fatuanDate2 != null){
            	if(fatuanDate1 == null || fatuanDate2 == null || fatuanDate1.compareTo(fatuanDate2) > 0){
    				throw new Exception("请输入正确的起始时间和结束时间");
    			}
            	
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	Calendar calendar = new GregorianCalendar();
    			calendar.setTime(fatuanDate2);
    			calendar.add(Calendar.DATE, 1);
    			Date nextEndDate = calendar.getTime();
    			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
    			String start = formatter.format(fatuanDate1);  
    			String end = formatter.format(nextEndDate);           	
            	buffer.append(" res3.fatuandate >= '" + start +"' and res3.fatuandate < '"+end + "' ");
            }
            if(refundDate1 != null && refundDate2 != null){
            	if(refundDate1 == null || refundDate2 == null || refundDate1.compareTo(refundDate2) > 0){
    				throw new Exception("请输入正确的起始时间和结束时间");
    			}
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	Calendar calendar = new GregorianCalendar();
    			calendar.setTime(refundDate2);
    			calendar.add(Calendar.DATE, 1);
    			Date nextEndDate = calendar.getTime();
    			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
    			String start = formatter.format(refundDate1);  
    			String end = formatter.format(nextEndDate);           	
            	buffer.append(" res3.refunddate >= '" + start +"' and res3.refunddate < '"+end + "' ");
            }
            if(retype != null){
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	buffer.append("res3.refundtype = "+ retype);
            }
            
            buffer.append(" order by refunddate desc");
            
            List<Object[]> list1 = new ArrayList<>();
			list1 = storeAccountLogService.statis(buffer.toString());
			List<RechargeRefund> ans = new ArrayList<>();
			for(Object[] objects : list1){
				RechargeRefund rechargeRefund = new RechargeRefund();
				rechargeRefund.setStoreName(objects[0] == null ? null: objects[0].toString());
				rechargeRefund.setOrderSn(objects[1] == null ? null:objects[1].toString());
				rechargeRefund.setFatuanDate(objects[2] == null ? null:objects[2].toString());
				rechargeRefund.setMoney((BigDecimal)objects[3]);
				rechargeRefund.setJidiao(objects[4] == null ? null:objects[4].toString());
				if(objects[5]!=null){
					Integer type = Integer.valueOf(objects[5].toString());
					//类型，2分成，3退团，4消团， 5供应商驳回订单,7租借导游退款
					//8酒店退款  9 门票退款  10酒加景退款 11签证退款 12认购门票退款13保险退款 
					String refundType  = new String();
					switch(type){
					case 3:refundType = "退团";break;
					case 4:refundType = "消团";break;
					case 5:refundType = "供应商驳回订单";break;
					case 7:refundType = "租借导游退款";break;
					case 8:refundType = "酒店退款";break;
					case 9:refundType = "门票退款";break;
					case 10:refundType = "酒加景退款";break;
					case 11:refundType = "签证退款";break;
					case 12:refundType = "认购门票退款";break;
					case 13:refundType = "保险退款";break;
					default:
						refundType = "未知类型";
					}
					rechargeRefund.setRefundType(refundType);
				}	
				rechargeRefund.setRefunddate(objects[6] == null ? null:objects[6].toString().substring(0,19));
				rechargeRefund.setProductName(objects[7] == null ? null:objects[7].toString());
				rechargeRefund.setProductSn(objects[8] == null ? null:objects[8].toString());
				ans.add(rechargeRefund);
			}
      
			json.setObj(ans);
			json.setMsg("查询成功");
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("查询失败："+ e.getMessage());
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
	}
	
	@RequestMapping("/list/view/download") 
	public void list(String storeName,String orderSn,String productSn,String productName,Integer retype,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date fatuanDate1,@DateTimeFormat(pattern="yyyy-MM-dd") Date fatuanDate2,
			@DateTimeFormat(pattern="yyyy-MM-dd") Date refundDate1,@DateTimeFormat(pattern="yyyy-MM-dd") Date refundDate2,
			HttpSession session,HttpServletRequest request,HttpServletResponse response){
		
		try {
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("select * from (");
			buffer.append(" select res2.store_name storename,res2.order_number ordernumber,res2.fatuandate fatuandate,CAST(res2.money AS DECIMAL(21, 3)) money,res2.jidiao jidiao,res2.refundtype refundtype ,res2.date refunddate, ");
            buffer.append(" case res2.type ");
            buffer.append(" when 0 then (select name from hy_guide g where g.id = res2.productid) ");
            buffer.append(" when 1 then (select group_line_name from hy_group where id = res2.productid) ");
            buffer.append(" when 2 then (select scene_name from hy_ticket_subscribe s where s.id = res2.productid)");
            buffer.append(" when 3 then (select hotel_name from hy_ticket_hotel h where h.id =res2.productid) ");
            buffer.append(" when 4 then (select scene_name from hy_ticket_scene s where s.id = res2.productid) ");
            buffer.append(" when 5 then (select product_name from hy_ticket_hotelandscene h where h.id = res2.productid) ");
            buffer.append(" when 6 then (select remark from hy_insurance i where i.id = res2.productid)");
            buffer.append(" when 7 then (select product_name from hy_visa v where v.id = res2.productid) ");
            buffer.append(" end  productname, case res2.type ");
            buffer.append(" when 0 then (select sn from hy_guide g where g.id = res2.productid)");
            buffer.append(" when 1 then (select group_line_pn from hy_group where id = res2.productid) ");
            buffer.append(" when 2 then (select sn from hy_ticket_subscribe s where s.id = res2.productid)");
            buffer.append(" when 3 then (select pn from hy_ticket_hotel h where h.id =res2.productid)");
            buffer.append(" when 4 then (select pn from hy_ticket_scene s where s.id = res2.productid)");
            buffer.append(" when 5 then (select product_id from hy_ticket_hotelandscene h where h.id = res2.productid)");
            buffer.append(" when 6 then (select insurance_code from hy_insurance i where i.id = res2.productid)");
            buffer.append(" when 7 then (select product_id from hy_visa v where v.id = res2.productid)");
            buffer.append(" end  productsn from");
            buffer.append(" (select store.store_name,o.order_number ,o.type,(select name from hy_admin where o.supplier = username) jidiao,o.supplier,res1.* ,");
            buffer.append(" (select item.product_id from hy_order_item item where item.order_id = res1.orderid limit 1)as productid");
            buffer.append(" from hy_store store,hy_order o,");
            buffer.append(" (select o.id orderid,o.store_id storeid,o.fatuandate,log.money money,log.create_date date,log.type refundtype from hy_store_account_log log ,hy_order o");
            buffer.append(" where log.type>2 and log.type!=6 and log.type!=14");
            buffer.append(" and log.order_sn = o.order_number) res1 ");
            buffer.append(" where store.id = res1.storeid and o.id = res1.orderid) res2 ) res3 ");
            
            boolean useWhere = false;
            if(storeName != null){
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	buffer.append(" res3.storename = '" + storeName +"' ");
            }
            if(orderSn != null){
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	buffer.append(" res3.ordernumber = '" + orderSn +"' ");
            }
            if(productSn != null){
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	buffer.append(" res3.productsn = '" + productSn +"' ");
            }
            if(productName != null){
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	buffer.append(" res3.productname = '" + productName +"' ");
            }
            if(fatuanDate1 != null && fatuanDate2 != null){
            	if(fatuanDate1 == null || fatuanDate2 == null || fatuanDate1.compareTo(fatuanDate2) > 0){
    				throw new Exception("请输入正确的起始时间和结束时间");
    			}
            	
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	Calendar calendar = new GregorianCalendar();
    			calendar.setTime(fatuanDate2);
    			calendar.add(Calendar.DATE, 1);
    			Date nextEndDate = calendar.getTime();
    			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
    			String start = formatter.format(fatuanDate1);  
    			String end = formatter.format(nextEndDate);           	
            	buffer.append(" res3.fatuandate >= '" + start +"' and res3.fatuandate < '"+end + "' ");
            }
            if(refundDate1 != null && refundDate2 != null){
            	if(refundDate1 == null || refundDate2 == null || refundDate1.compareTo(refundDate2) > 0){
    				throw new Exception("请输入正确的起始时间和结束时间");
    			}
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	Calendar calendar = new GregorianCalendar();
    			calendar.setTime(refundDate2);
    			calendar.add(Calendar.DATE, 1);
    			Date nextEndDate = calendar.getTime();
    			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
    			String start = formatter.format(refundDate1);  
    			String end = formatter.format(nextEndDate);           	
            	buffer.append(" res3.refunddate >= '" + start +"' and res3.refunddate < '"+end + "' ");
            }
            if(retype != null){
            	if(useWhere){ //有了where
            		buffer.append(" and ");
            	}else{
            		buffer.append(" where ");
            		useWhere = true;
            	}
            	buffer.append("res3.refundtype = "+ retype);
            }
            
            buffer.append(" order by refunddate desc");
            List<Object[]> list1 = new ArrayList<>();
			list1 = storeAccountLogService.statis(buffer.toString());
			List<RechargeRefund> ans = new ArrayList<>();
			for(Object[] objects : list1){
				RechargeRefund rechargeRefund = new RechargeRefund();
				rechargeRefund.setStoreName(objects[0] == null ? null: objects[0].toString());
				rechargeRefund.setOrderSn(objects[1] == null ? null:objects[1].toString());
				rechargeRefund.setFatuanDate(objects[2] == null ? null:objects[2].toString());
				rechargeRefund.setMoney((BigDecimal)objects[3]);
				rechargeRefund.setJidiao(objects[4] == null ? null:objects[4].toString());
				if(objects[5]!=null){
					Integer type = Integer.valueOf(objects[5].toString());
					//类型，2分成，3退团，4消团， 5供应商驳回订单,7租借导游退款
					//8酒店退款  9 门票退款  10酒加景退款 11签证退款 12认购门票退款13保险退款 
					String refundType  = new String();
					switch(type){
					case 3:refundType = "退团";break;
					case 4:refundType = "消团";break;
					case 5:refundType = "供应商驳回订单";break;
					case 7:refundType = "租借导游退款";break;
					case 8:refundType = "酒店退款";break;
					case 9:refundType = "门票退款";break;
					case 10:refundType = "酒加景退款";break;
					case 11:refundType = "签证退款";break;
					case 12:refundType = "认购门票退款";break;
					case 13:refundType = "保险退款";break;
					default:
						refundType = "未知类型";
					}
					rechargeRefund.setRefundType(refundType);
				}	
				rechargeRefund.setRefunddate(objects[6] == null ? null:objects[6].toString().substring(0,19));
				rechargeRefund.setProductName(objects[7] == null ? null:objects[7].toString());
				rechargeRefund.setProductSn(objects[8] == null ? null:objects[8].toString());
				ans.add(rechargeRefund);
			}
			
			StringBuffer sb2 = new StringBuffer();
			sb2.append("门店预充值退还报表");
			String fileName = "门店预充值退还报表.xls";  // Excel文件名
			String tableTitle = sb2.toString();   // Excel表标题
			String configFile = "storeRechargeRefund.xml"; // 配置文件
			com.grain.controller.BaseController excelCon = new com.grain.controller.BaseController();
			excelCon.export2Excel(request, response, ans, fileName, tableTitle, configFile);
      
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@RequestMapping("/store/list") 
	@ResponseBody
	public Json store(){
		Json json = new Json();
		try {
//			List<Filter> filters = new LinkedList<>();
//			filters.add(Filter.eq("pstatus", value))
//			List<Store> stores = storeService.findList(null,filters,null);
			List<Store> stores = storeService.findAll();
			List<HashMap<String, Object>> list = new LinkedList<>();
			HashMap<String, Object> quanbu = new HashMap<>();
			quanbu.put("storeName", "全部");
			quanbu.put("storeId", null);
			list.add(quanbu);
			for(Store store : stores){
				HashMap<String, Object> map = new HashMap<>();
				map.put("storeName", store.getStoreName());
				map.put("storeId", store.getId());
				list.add(map);
			}
			json.setObj(list);
			json.setMsg("查询成功");
			json.setSuccess(true);
		
		} catch (Exception e) {
			// TODO: handle exception
			json.setMsg("查询失败");
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
		
		
	}
	
	/**
	 * 退款门店、订单编号、产品编号、产品名称、团期、报名计调、退款金额、退款时间、退款类型；
	 * @author wj
	 *
	 */
	public class RechargeRefund{
		private String storeName;
		private String orderSn;
		private String productSn;
		private String productName;
		private String fatuanDate;
		private String jidiao;
		private BigDecimal money;
		private String refunddate;
		private String refundType;
		public String getStoreName() {
			return storeName;
		}
		public void setStoreName(String storeName) {
			this.storeName = storeName;
		}
		public String getOrderSn() {
			return orderSn;
		}
		public void setOrderSn(String orderSn) {
			this.orderSn = orderSn;
		}
		public String getProductSn() {
			return productSn;
		}
		public void setProductSn(String productSn) {
			this.productSn = productSn;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public String getFatuanDate() {
			return fatuanDate;
		}
		public void setFatuanDate(String fatuanDate) {
			this.fatuanDate = fatuanDate;
		}
		public String getJidiao() {
			return jidiao;
		}
		public void setJidiao(String jidiao) {
			this.jidiao = jidiao;
		}
		public BigDecimal getMoney() {
			return money;
		}
		public void setMoney(BigDecimal money) {
			this.money = money;
		}
		public String getRefunddate() {
			return refunddate;
		}
		public void setRefunddate(String refunddate) {
			this.refunddate = refunddate;
		}
		public String getRefundType() {
			return refundType;
		}
		public void setRefundType(String refundType) {
			this.refundType = refundType;
		}

	}
	

}
