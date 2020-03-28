package com.hongyu.controller.lbc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
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

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Pageable;
import com.hongyu.controller.lbc.MoBanExcelUtil.Member;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.Store;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.GroupDivideService;
import com.hongyu.service.GroupMemberService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyDepartmentModelService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyOrderApplicationItemService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderCustomerService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyReceiptRefundService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.InsuranceOrderService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.PayServicerService;
import com.hongyu.service.PayablesLineItemService;
import com.hongyu.service.PayablesLineService;
import com.hongyu.service.PayandrefundRecordService;
import com.hongyu.service.PaymentSupplierService;
import com.hongyu.service.ReceiptServicerService;
import com.hongyu.service.ReceiptTotalServicerService;
import com.hongyu.service.RefundInfoService;
import com.hongyu.service.RefundRecordsService;
import com.hongyu.service.StoreAccountLogService;
import com.hongyu.service.StoreAccountService;
import com.hongyu.service.StorePreSaveService;
import com.hongyu.service.StoreService;
import com.hongyu.service.SupplierDismissOrderApplyService;


@Controller
@RequestMapping("/admin/insuranceorderreserve/")
public class InsuranceOrderReserveController {
	@Resource(name = "payablesLineItemServiceImpl")
	PayablesLineItemService payablesLineItemService;

	@Resource(name = "payablesLineServiceImpl")
	PayablesLineService payablesLineService;

	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;

	@Resource(name = "hyReceiptRefundServiceImpl")
	HyReceiptRefundService hyReceiptRefundService;

	@Resource(name = "hyAdminServiceImpl")
	private HyAdminService hyAdminService;

	@Resource(name = "storeServiceImpl")
	private StoreService storeService;

	@Resource(name = "hyOrderApplicationServiceImpl")
	private HyOrderApplicationService hyOrderApplicationService;

	@Resource(name = "hyOrderItemServiceImpl")
	private HyOrderItemService hyOrderItemService;

	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;

	@Resource
	private RuntimeService runtimeService;

	@Resource(name = "payandrefundRecordServiceImpl")
	PayandrefundRecordService payandrefundRecordService;

	@Resource(name = "hyGroupServiceImpl")
	private HyGroupService hyGroupService;

	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;

	@Resource(name = "supplierDismissOrderApplyServiceImpl")
	SupplierDismissOrderApplyService supplierDismissOrderApplyService;

	@Resource(name = "groupMemberServiceImpl")
	GroupMemberService groupMemberService;

	@Resource(name = "groupDivideServiceImpl")
	GroupDivideService groupDivideService;
	
	@Resource(name = "paymentSupplierServiceImpl")
	PaymentSupplierService paymentSupplierService;

	@Resource(name = "storePreSaveServiceImpl")
	StorePreSaveService storePreSaveService;
	
	@Resource(name = "storeAccountLogServiceImpl")
	StoreAccountLogService storeAccountLogService;

	@Resource(name = "storeAccountServiceImpl")
	StoreAccountService storeAccountService;
	
	@Resource(name = "insuranceOrderServiceImpl")
	InsuranceOrderService insuranceOrderService;
	
	@Resource(name = "refundInfoServiceImpl")
	RefundInfoService refundInfoService;
	
	@Resource(name = "refundRecordsServiceImpl")
	RefundRecordsService refundRecordsService;
	
	@Resource(name = "receiptTotalServicerServiceImpl")
	ReceiptTotalServicerService receiptTotalServicerService;
	
	@Resource(name = "receiptServicerServiceImpl")
	ReceiptServicerService receiptServicerService;
	
	@Resource(name = "hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;
	
	@Resource(name = "payServicerServiceImpl")
	PayServicerService payServicerService;
	
	@Resource(name = "hyOrderApplicationItemServiceImpl")
	private HyOrderApplicationItemService hyOrderApplicationItemService;
	
	@Resource(name = "hyOrderCustomerServiceImpl")
	private HyOrderCustomerService hyOrderCustomerService;
	
	@Resource(name = "insuranceServiceImpl")
	private InsuranceService insuranceService;
	
	@Resource(name = "hyDepartmentModelServiceImpl")
	private HyDepartmentModelService hyDepartmentModelService;
	
	public static class Wrap{
		HyOrder hyOrder;
		Date startTime;
		Date endTime;
		public HyOrder getHyOrder() {
			return hyOrder;
		}
		public void setHyOrder(HyOrder hyOrder) {
			this.hyOrder = hyOrder;
		}
		public Date getStartTime() {
			return startTime;
		}
		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}
		public Date getEndTime() {
			return endTime;
		}
		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}
		
	}
	
	@Resource(name = "departmentServiceImpl")
	DepartmentService departmentService;
	

	
	/**
	 * 保险列表
	 * @author LBC
	 */
	@RequestMapping(value = "order_pay/list/view")
	@ResponseBody
	public Json OrderPayList(Pageable pageable, Integer type, String name) {
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			if(type != null) {
				filters.add(Filter.eq("classify", type));
			}
			if(name != null) {
				filters.add(Filter.like("remark", name));
			}
			List<Insurance> insurances = insuranceService.findList(null, filters, null);
			
			int pageNumber = pageable.getPage();
			int pageSize = pageable.getRows();
			Map<String, Object> hMap = new HashMap<>();
			hMap.put("pageNumber", pageNumber);
			hMap.put("pageSize", pageSize);
			hMap.put("total", insurances.size());
			hMap.put("rows", insurances.subList((pageNumber - 1) * pageSize,
					pageNumber * pageSize > insurances.size() ? insurances.size() : pageNumber * pageSize));
			json.setMsg("获取成功");
			json.setSuccess(true);
			json.setObj(hMap);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取列表失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 保险详情
	 * @author LBC
	 */
	@RequestMapping(value = "order_pay/detail/view")
	@ResponseBody
	public Json OrderPayDetail(Pageable pageable, Long id) {
		Json json = new Json();
		try {
			Insurance insurance = insuranceService.find(id);
			Map<String, Object> map = new HashMap<String, Object>(); 
			map.put("Insurance", insurance);
			json.setMsg("获取成功");
			json.setSuccess(true);
			json.setObj(map);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取列表失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	static class WrapOrder{
		private Long insurance_id;
		private HyOrder hyOrder;
		public Long getInsurance_id() {
			return insurance_id;
		}
		public void setInsurance_id(Long insurance_id) {
			this.insurance_id = insurance_id;
		}
		public HyOrder getHyOrder() {
			return hyOrder;
		}
		public void setHyOrder(HyOrder hyOrder) {
			this.hyOrder = hyOrder;
		}
	}
	
	/**
	 * 下订单
	 * @author LBC
	 */
	@RequestMapping(value = "order_pay/add_order")
	@ResponseBody
	public Json OrderPayAddOrder(HttpSession session, @RequestBody WrapOrder wrapOrder) {
		Json json = new Json();
		/**
		 * 获取当前用户
		 */
		String username = (String) session.getAttribute(CommonAttributes.Principal);
		HyAdmin admin = hyAdminService.find(username);
		try {
			if(wrapOrder == null) {
				json.setSuccess(false);
				json.setMsg("未能收到前台数据");
				return json;
			}
			if(wrapOrder.getHyOrder() == null) {
				json.setSuccess(false);
				json.setMsg("未能收到订单数据");
				return json;
			}
			Store store = storeService.findStore(admin);
			if(store == null) {
				wrapOrder.getHyOrder().setContact(admin.getName());
			}
			else {
				wrapOrder.getHyOrder().setContact(store.getStoreName() + admin.getName());
			}
			Insurance insurance = insuranceService.find(wrapOrder.getInsurance_id());
			if(insurance == null) {
				json.setSuccess(false);
				json.setMsg("不存在此保险，请检查");
				return json;
			}
			wrapOrder.getHyOrder().setInsurance(insurance);
			wrapOrder.getHyOrder().setName(insurance.getRemark());
			wrapOrder.getHyOrder().setXianlutype(insurance.getClassify());
			
			wrapOrder.getHyOrder().setPhone(admin.getMobile());
			//调用service中的接口
			json = hyOrderService.addInsuranceOrder(wrapOrder.getHyOrder(), session);
			
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取列表失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	
	/**
	 * 游客信息（输入身份证 返回）
	 * @author LBC
	 */
	@RequestMapping(value = "order_pay/customer_information")
	@ResponseBody
	public Json CustomerInformation(HttpSession session, Integer type, String certificate) {
		Json json = new Json();
		try {
			
			if(type == null) {
				json.setSuccess(false);
				json.setMsg("未传证件类型");
				return json;
			}
			if(certificate == null) {
				json.setSuccess(false);
				json.setMsg("未传证件号");
				return json;
			}
			
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("certificateType", type));
			filters.add(Filter.eq("certificate", certificate));
			
			List<HyOrderCustomer> hyOrderCustomers = hyOrderCustomerService.findList(null, filters, null);
			if(hyOrderCustomers.size() > 0) {
				json.setMsg("获取成功");
				json.setObj(hyOrderCustomers.get(0));
			}
			else {
				json.setMsg("查无此人");
				json.setObj(null);
			}
			
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			json.setSuccess(false);
			json.setMsg("获取列表失败");
			json.setObj(e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	
	/**
	 * 下载保险订购人员模板
	 * @author LBC
	 */
	@RequestMapping(value = "order_pay/get_excel")
	//@ResponseBody
	public void GetExcel(HttpServletRequest request, HttpServletResponse response) {
//		response.setContentType("text/html;charset=utf-8");
//        response.setCharacterEncoding("utf-8");
        try {
        	//C:\Users\LBC\workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\hy_backend\download\投保人员信息表.xls
        	String filefullname =System.getProperty("hongyu.webapp") + "download/投保人员信息表birthday.xls";
            String fileName = "投保人员信息表.xls";
			File file = new File(filefullname);
			System.out.println(filefullname);
			System.out.println(file.getAbsolutePath());
			if (!file.exists()) {
			    request.setAttribute("message", "下载失败");
			    return;
                
            } else {

                // 设置相应头，控制浏览器下载该文件，这里就是会出现当你点击下载后，出现的下载地址框
                response.setHeader("content-disposition",
                        "attachment;filename=" + URLEncoder.encode("投保人员信息表.xls", "utf-8"));
                
                
               
        		
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
	 * 上传保险订购人员信息模板接口1
	 * @author LBC
	 */
	@RequestMapping(value = "order_pay/upload_excel")
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
			
//        	String localfileName = "WebRoot/upload/投保人员信息表.xls";
//        	File localfile = new File(localfileName);
//        	file.transferTo(localfile);
        	List<Member> members = MoBanExcelUtil.readExcelBirthday(file.getInputStream());
        	List<Map<String,Object> > list = new ArrayList<>();
            for(Member member : members) {
            	Map<String, Object> map = new HashMap<>();
            	map.put("name", member.getName());
            	map.put("certificateType", member.getCertificateType());
            	map.put("certificate", member.getCertificateNumber());
            	map.put("age", member.getAge());
            	map.put("gender", member.getSex());
            	//map.put("phone", member.getPhone());
            	list.add(map);
            }
        	
			json.setObj(list);
			json.setMsg("文件读取成功");
			json.setSuccess(true);
			//删除excel
//			if(!localfile.exists()) {
//				//
//			}
//			else {
//				if(localfile.isFile()) {
//					localfile.delete();
//				}
//			}

        }
        catch (Exception e) {
			// TODO: handle exception
        	json.setMsg("文件读取失败");
        	json.setSuccess(false);
        	json.setObj(null);
            
		}
		return json;
       
		
	}
	
	/**
	 * 上传保险订购人员信息模板接口2
	 * @author LBC
	 */
	@RequestMapping(value = "order_pay/upload_excel_1")
	@ResponseBody
	public Json UploadExcel(@RequestParam MultipartFile file) {
        Json json = new Json();
		try {
			if(file == null) {
				json.setMsg("未接受到文件");
	        	json.setSuccess(false);
	        	json.setObj(null);
			}
			
//        	String localfileName = "WebRoot/upload/投保人员信息表111.xls";
//        	//String localfileName = System.getProperty("java.io.tmpdir") + "/upload_" + UUID.randomUUID() + ".xls";
//        	File localfile = new File(localfileName);
//        	file.transferTo(localfile);
//        	System.out.println(localfileName);
//        	System.out.println(localfile.getAbsolutePath());
//
//        	
//        	//List<Member> members = MoBanExcelUtil.readExcel(localfileName);
        	List<Member> members = MoBanExcelUtil.readExcelBirthday(file.getInputStream());
        	List<Map<String,Object> > list = new ArrayList<>();
            for(Member member : members) {
            	Map<String, Object> map = new HashMap<>();
            	map.put("name", member.getName());
            	map.put("certificateType", member.getCertificateType());
            	map.put("certificate", member.getCertificateNumber());
            	map.put("age", member.getAge());
            	map.put("gender", member.getSex());
            	//map.put("phone", member.getPhone());
            	list.add(map);
            }
        	
			json.setObj(list);
			json.setMsg("文件读取成功");
			json.setSuccess(true);
			//删除excel
//			if(!localfile.exists()) {
//				//
//			}
//			else {
//				if(localfile.isFile()) {
//					localfile.delete();
//				}
//			}

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
