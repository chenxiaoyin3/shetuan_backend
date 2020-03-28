package com.hongyu.controller.liyang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.annotations.Filters;
import org.hibernate.validator.internal.util.privilegedactions.GetAnnotationParameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.controller.lbc.MoBanExcelUtil;
import com.hongyu.controller.lbc.MoBanExcelUtil.Member;
import com.hongyu.controller.liyang.VisaCustomerExcelUtil.VisaMember;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyCountry;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyPromotionActivity;
import com.hongyu.entity.HyVisa;
import com.hongyu.entity.HyVisaPic;
import com.hongyu.entity.HyVisaPrices;
import com.hongyu.listener.VisaPriceListener;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyCompanyService;
import com.hongyu.service.HyCountryService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyPromotionActivityService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyVisaService;
import com.hongyu.service.StoreService;
import com.hongyu.service.impl.HyVisaServiceImpl;
import com.hongyu.util.DateUtil;
import com.hongyu.util.liyang.CountryUtil;

import jdk.nashorn.internal.ir.annotations.Ignore;
import sun.text.resources.cldr.es.FormatData_es_GQ;
/**
 * 签证订购中心
 * @author liyang
 * @version 2019年5月28日 下午5:38:41
 */
@Controller
@RequestMapping("/admin/storeOrderVisa/")
public class StoreOrderVisaController {
	@Resource(name = "hyAreaServiceImpl")
	HyAreaService hyAreaService;

	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;

	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "hyCompanyServiceImpl")
	HyCompanyService hyCompanyService;

	@Resource(name = "hySupplierServiceImpl")
	HySupplierService hySupplierService;

	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;

	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyVisaServiceImpl")
	HyVisaService hyVisaService;
	
	@Resource(name = "hyCountryServiceImpl")
	HyCountryService hyCountryService;
	
	@Resource(name = "hyPromotionActivityServiceImpl")
	HyPromotionActivityService hyPromotionActivityService;
	
	/**
	 * 产品列表页
	 * @param pageable
	 * @param continent
	 * @param country
	 * @param visaName
	 * @param session
	 * @return
	 */
	@RequestMapping("list/view")
	@ResponseBody
	public Json list(Pageable pageable,Integer continentId,Long countryId,String visaName,HttpSession session){
		Json json = new Json();
		try {
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin hyAdmin = hyAdminService.find(username);
			
			List<Filter> filters = new ArrayList<>();
			if(visaName!=null){
				filters.add(Filter.like("productName", visaName));
			}
			if(countryId!=null){
				HyCountry hyCountry = hyCountryService.find(countryId);
				filters.add(Filter.eq("country", hyCountry));
			}else{
				if(continentId!=null){
					List<Filter> filters2 = new ArrayList<>();
					filters2.add(Filter.eq("continent", CountryUtil.getContinent(continentId)));
					List<HyCountry> countries = hyCountryService.findList(null,filters2,null);
					filters.add(Filter.in("country", countries));
				}
			}	
			//审核状态已通过
			filters.add(Filter.eq("auditStatus",3));
			//已上架
			filters.add(Filter.eq("saleStatus",2));
			//状态为正常
			filters.add(Filter.eq("status",true));
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
			Page<HyVisa> page = hyVisaService.findPage(pageable);
			Map<String, Object> obj = new HashMap<String, Object>();
			List<Map<String, Object>> visas = new ArrayList<>();
			for(HyVisa visa:page.getRows()){
				Map<String, Object> map = new HashMap<>();
				map.put("id", visa.getId());
				map.put("productId", visa.getProductId());
				map.put("visaName", visa.getProductName());
				map.put("productType", visa.getVisaType());
				HyCountry country2 = visa.getCountry();
				if(country2 != null){
					map.put("continent", visa.getCountry().getContinent());
					map.put("country", visa.getCountry().getName());
				}
				map.put("supplierName", visa.getTicketSupplier().getSupplierName());
				map.put("operator", visa.getCreator().getName());
				visas.add(map);
			}
			obj.put("total", visas.size());
			obj.put("rows", visas);
			obj.put("pageSize", pageable.getPage());
			obj.put("pageNumber", pageable.getRows());
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(obj);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败： " + e.getMessage());
		}
		return json;
	}
	/**
	 * 签证详情页
	 * @param id
	 * @return
	 */
	@RequestMapping("detail/view")
	@ResponseBody
	public Json detail(Long id){
		Json json = new Json();
		try {
			HyVisa hyVisa = hyVisaService.find(id);
			if(hyVisa==null)
				throw new NullPointerException("找不到该订单");
			HashMap<String, Object> ans  = new HashMap<>();
			ans.put("id", hyVisa.getId());
			ans.put("productName", hyVisa.getProductName());
			ans.put("productId", hyVisa.getProductId());
			ans.put("visaType", hyVisa.getVisaType());
			ans.put("duration", hyVisa.getDuration());
			ans.put("times", hyVisa.getTimes());
			ans.put("isInterview", hyVisa.getIsInterview());
			ans.put("stayDays", hyVisa.getStayDays());
			ans.put("expireDays", hyVisa.getExpireDays());
			ans.put("serviceContent", hyVisa.getServiceContent());
			ans.put("priceContain", hyVisa.getPriceContain());
			ans.put("reserveRequirement", hyVisa.getReserveRequirement());
			ans.put("accessory",hyVisa.getAccessory());
			ans.put("introduce", hyVisa.getIntroduce());
			if(hyVisa.getTicketSupplier()!=null){
				ans.put("ticketSupplier", hyVisa.getTicketSupplier().getSupplierName());
			}
			if(hyVisa.getCreator()!=null){
				ans.put("creatorName", hyVisa.getCreator().getName());
				ans.put("creatorPhone", hyVisa.getCreator().getMobile());
			}
			if(hyVisa.getCountry()!=null){
				HyCountry country = hyVisa.getCountry();
				ans.put("countryId", country.getId());
				ans.put("continent", country.getContinent());
				ans.put("country",country.getName());
			}
			if(hyVisa.getHyPromotionActivity()!=null){
				HyPromotionActivity promotion = hyVisa.getHyPromotionActivity();
				if(promotion.getState()==1){
					Map<String, Object> map = new HashMap<>();
					map.put("id", promotion.getId());
					/** 计调 **/
					map.put("jidiao", promotion.getJidiao());
					/** 促销名称 **/
					map.put("name", promotion.getName());
					map.put("startDate", promotion.getStartDate());
					map.put("endDate", promotion.getEndDate());
					/** 优惠方式0每单满减，1每单打折，2每人减,3无促销**/
					map.put("promotionType", promotion.getPromotionType());
					/** 满减促销满足的金额 **/
					map.put("manjianPrice1", promotion.getManjianPrice1());
					/** 满减促销减免的金额 **/
					map.put("manjianPrice2", promotion.getManjianPrice2());
					/** 每人减/按数量减金额 **/
					map.put("meirenjian", promotion.getMeirenjian());
					/** 打折折扣 **/
					map.put("dazhe", promotion.getDazhe());
					/** 审核状态 0:待审核 1:通过 2:驳回  3:已过期 4:已取消**/
					map.put("state", promotion.getState());
					/** 备注 **/
					map.put("remark", promotion.getRemark());
					/** 活动类型 0:门票,1:酒店,2:酒+景,3:认购门票,4:签证 **/
					map.put("activityType", promotion.getActivityType());
					
					ans.put("promotionActivity", map);
				}
			}
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(ans);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败： " + e.getMessage());
		}
		return json;
	}
	/**
	 * 根据大洲返回所有洲内的国家
	 * @param continent
	 * @return
	 */
	@RequestMapping("areas")
	@ResponseBody
	public Json getAreas(Integer continentId){
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			if(continentId!=null){
				String continent = CountryUtil.getContinent(continentId);
				filters.add(Filter.eq("continent", continent));
			}
			List<HyCountry> hyCountrys = hyCountryService.findList(null,filters,null);
			List<Map<String, Object>> result = new ArrayList<>();
			for(HyCountry country:hyCountrys){
				Map<String, Object> map = new HashMap<>();
				map.put("continent", country.getContinent());
				map.put("name", country.getName());
				map.put("countryId", country.getId());
				result.add(map);
			}
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(result);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败： " + e.getMessage());
		}
		return json;
	}
	/**
	 * 根据所选时间获取签证的当前价格
	 * @param id
	 * @param date
	 * @return
	 */
	@RequestMapping("price")
	@ResponseBody
	public Json getPrice(Long id,@DateTimeFormat(pattern = "yyyy-MM-dd")Date date){
		Json json = new Json();
		try {
			HyVisa hyVisa = hyVisaService.find(id);
			if(hyVisa==null)
				throw new Exception("id为空或者不存在该id");
			List<HyVisaPrices> hyVisaPrices = hyVisa.getHyVisaPrices();
			Map<String, Object> ans = new HashMap<>();
			for(HyVisaPrices price : hyVisaPrices){
				if(date.after(DateUtil.getPreDay(price.getStartDate())) && date.before(DateUtil.getNextDay(price.getEndDate()))){
					ans.put("priceId", price.getId());
					ans.put("displayPrice", price.getDisplayPrice());
					ans.put("sellPrice", price.getSellPrice());
					ans.put("settlementPrice", price.getSettlementPrice());
					break;
				}
			}
			json.setMsg("查询成功");
			json.setSuccess(true);
			json.setObj(ans);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("查询失败： " + e.getMessage());
		}
		return json;
	}
	@RequestMapping("order")
	@ResponseBody
	public Json order(@RequestBody HyOrder hyOrder, HttpSession session) {
		Json json = new Json();
		try {
			json = hyOrderService.addVisaOrder(hyOrder, session);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("下单失败： " + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	@Resource(name="hyOrderCustomerServiceImpl")
	HyOrderCustomerService hyOrderCustomerService;
	/**
	 * 自动填充游客信息
	 * @param certificateType
	 * @param certificate
	 * @param productId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping("order_customer/info/view")
	@ResponseBody
	public Json orderCustomerInfo(Integer certificateType,String certificate,Long productId,
			Date startDate,Date endDate) {
		Json json = new Json();
		try {
			if(certificateType==null || certificate==null) {
				json.setSuccess(false);
				json.setMsg("缺少证件类型和证件号");
				json.setObj(null);
				return json;
			}
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//			StringBuilder sb = new StringBuilder("select hy.hy_order_customer.certificate_type,hy.hy_order_customer.certificate,"
//					+ "hy.hy_order_item.product_id,hy.hy_order_item.start_date,hy.hy_order_item.end_date"
//					+ " from hy.hy_order_customer,hy.hy_order_item"
//					+ " where hy.hy_order_customer.item_id=hy.hy_order_item.id");
//			sb.append(" and hy.hy_order_customer.certificate_type="+certificateType);
//			sb.append(" and hy.hy_order_customer.certificate='"+certificate+"'");
//			
//			if(productId!=null) {
//				sb.append(" and hy.hy_order_item.product_id="+productId);
//			}
//			if(startDate!=null) {
//				String startStr=format.format(startDate);
//				sb.append(" and DATE_FORMAT(hy.hy_order_item.end_date,'%Y-%m-%d')>='"+startStr+"'");
//				
//			}
//			if(endDate!=null) {
//				String endStr = format.format(endDate);
//				sb.append(" and DATE_FORMAT(hy.hy_order_item.start_date,'%Y-%m-%d')<='"+endStr+"'");
//			}
//			
//			String jpql = sb.toString();
//			List<Object[]> list = hyOrderCustomerService.statis(jpql);
//			if(list!=null && !list.isEmpty()) {
//				json.setSuccess(false);
//				json.setMsg("同一证件号不能同时报名重叠团期！");
//				json.setObj(list);
//				return json;
//			}
//			
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("certificateType",certificateType));
			filters.add(Filter.eq("certificate", certificate));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			List<HyOrderCustomer> customers = hyOrderCustomerService.findList(null,filters,orders);
			
			json.setSuccess(true);
			json.setMsg("查询成功");
			if(customers==null || customers.isEmpty()) {
				json.setObj(null);
			}else {
				json.setObj(customers.get(0));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("查询失败");
			json.setObj(e);
		}
		return json;
	}
	/**
	 * 下载保险订购人员模板
	 * @author LBC
	 */
	@RequestMapping(value = "getExcel")
	//@ResponseBody
	public void GetExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
        	String filefullname =System.getProperty("hongyu.webapp") + "download/签证批量导入客户信息表.xls";
            String fileName = "签证批量导入客户信息表.xls";
			File file = new File(filefullname);
			System.out.println(filefullname);
			System.out.println(file.getAbsolutePath());
			if (!file.exists()) {
			    request.setAttribute("message", "下载失败");
			    return;
                
            } else {

                // 设置相应头，控制浏览器下载该文件，这里就是会出现当你点击下载后，出现的下载地址框
                response.setHeader("content-disposition",
                        "attachment;filename=" + URLEncoder.encode("签证批量导入客户信息表.xls", "utf-8"));
                
                
               
        		
        		response.setHeader("content-disposition",
        				"attachment;" + "filename=" + URLEncoder.encode(fileName, "UTF-8"));	
        		
        		response.setHeader("Connection", "close");
        		response.setHeader("Content-Type", "application/vnd.ms-excel");

        		//String zipfilefullname = userdir + zipFileName;
        		FileInputStream fis = new FileInputStream(file);
        		BufferedInputStream bis = new BufferedInputStream(fis);
        		ServletOutputStream sos = response.getOutputStream();
        		BufferedOutputStream bos = new BufferedOutputStream(sos);

        		byte[] bytes = new byte[1024];
        		int i = 0;
        		while ((i = bis.read(bytes, 0, bytes.length)) != -1) {
        			bos.write(bytes);
        		}
        		bos.flush();
        		bis.close();
        		bos.close();
            }
        }
        catch (Exception e) {
			// TODO: handle exception
        	request.setAttribute("message", "出现错误");
            e.printStackTrace();
		}
        return;
		
	}
	
	/**
	 * 批量导入客人信息
	 * @param files
	 * @return
	 */
	@RequestMapping(value = "uploadExcel")
	@ResponseBody
	public Json UploadExcel(@RequestParam MultipartFile[] files) {
        Json json = new Json();
		try {
			if(files == null || files[0] == null) {
				json.setMsg("未接受到文件");
	        	json.setSuccess(false);
	        	json.setObj(null);
			}
			MultipartFile file = files[0];
			
        	List<VisaMember> members = VisaCustomerExcelUtil.readExcel(file.getInputStream());
        	List<Map<String,Object> > list = new ArrayList<>();
            for(VisaMember member : members) {
            	Map<String, Object> map = new HashMap<>();
            	map.put("name", member.getName());
            	map.put("certificateType", member.getCertificateType());
            	map.put("certificate", member.getCertificateNumber());
            	map.put("birthday", member.getBirthday());
            	map.put("gender", member.getSex());
            	map.put("xing", member.getXing());
            	map.put("ming", member.getMing());
            	map.put("youxiaoqi", member.getYouxiaoqi());
            	map.put("jiguan", member.getJiguan());
            	map.put("phone", member.getPhone());
            	list.add(map);
            }
        	
			json.setObj(list);
			json.setMsg("文件读取成功");
			json.setSuccess(true);

        }
        catch (Exception e) {
			// TODO: handle exception
        	json.setMsg("文件读取失败");
        	json.setSuccess(false);
        	json.setObj(null);
            
		}
		return json;		
	}

}
