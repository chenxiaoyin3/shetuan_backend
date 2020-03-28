package com.hongyu.controller.liyang;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.JScrollPane;

import org.apache.commons.collections.functors.IfClosure;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.annotations.Delete;
import org.apache.poi.hssf.record.CRNCountRecord;
import org.apache.taglibs.standard.tag.common.core.ForTokensSupport;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.CJYLabel;
import com.hongyu.entity.CJYLabelProduct;
import com.hongyu.entity.CommonShenheedu;
import com.hongyu.entity.CouponMoney;
import com.hongyu.entity.Department;
import com.hongyu.entity.GNYLabel;
import com.hongyu.entity.GNYLabelProduct;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyGroupOtherprice;
import com.hongyu.entity.HyGroupPrice;
import com.hongyu.entity.HyGroupSpecialprice;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyLineRefund;
import com.hongyu.entity.HyLineTravels;
import com.hongyu.entity.HySupplierContract;
import com.hongyu.entity.Insurance;
import com.hongyu.entity.InsurancePrice;
import com.hongyu.entity.JDLabel;
import com.hongyu.entity.MhGroupOtherPrice;
import com.hongyu.entity.MhGroupPrice;
import com.hongyu.entity.MhLine;
import com.hongyu.entity.MhLineRefund;
import com.hongyu.entity.MhLineTravels;
import com.hongyu.entity.MhProductPicture;
import com.hongyu.entity.TransportEntity;
import com.hongyu.entity.ZBYLabel;
import com.hongyu.entity.ZBYLabelProduct;
import com.hongyu.entity.CommonShenheedu.Eduleixing;
import com.hongyu.entity.HyGroup.GroupStateEnum;
import com.hongyu.entity.HyLine.IsSaleEnum;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.HySupplierContract.ContractStatus;
import com.hongyu.service.CJYLabelProductService;
import com.hongyu.service.CJYLabelService;
import com.hongyu.service.CommonEdushenheService;
import com.hongyu.service.CommonSequenceService;
import com.hongyu.service.DepartmentService;
import com.hongyu.service.GNYLabelProductService;
import com.hongyu.service.GNYLabelService;
import com.hongyu.service.GroupBiankoudianService;
import com.hongyu.service.GroupDivideService;
import com.hongyu.service.GroupMemberService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyAreaService;
import com.hongyu.service.HyGroupOtherpriceService;
import com.hongyu.service.HyGroupPriceService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineRefundService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyLineTravelsService;
import com.hongyu.service.HyProviderRebateService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.HySupplierContractService;
import com.hongyu.service.InsuranceService;
import com.hongyu.service.LineCatagoryService;
import com.hongyu.service.MhGroupOtherPriceService;
import com.hongyu.service.MhGroupPriceService;
import com.hongyu.service.MhLineRefundService;
import com.hongyu.service.MhLineService;
import com.hongyu.service.MhLineTravelsService;
import com.hongyu.service.MhProductPictureService;
import com.hongyu.service.StoreService;
import com.hongyu.service.TransportService;
import com.hongyu.service.ZBYLabelProductService;
import com.hongyu.service.ZBYLabelService;
import com.hongyu.util.AuthorityUtils;
import com.hongyu.util.Constants.AuditStatus;

import javassist.ClassMap;
/**
 * 线路完善
 * @author liyang
 * @version 2019年1月5日 下午12:48:04
 */
@Controller
@RequestMapping("admin/perfect/line/")
public class LinePerfectController {
	@Resource(name = "commonEdushenheServiceImpl")
	private CommonEdushenheService commonEdushenheService;
	
	@Resource(name = "storeServiceImpl")
	StoreService storeService;
	
	@Resource(name = "hyLineServiceImpl")
	HyLineService hyLineService;
	
	@Resource(name = "hyLineTravelsServiceImpl")
	HyLineTravelsService hyLineTravelsService;
	
	@Resource(name = "hyLineRefundServiceImpl")
	HyLineRefundService hyLineRefundService;
	
	@Resource(name = "transportServiceImpl")
	TransportService transportService;
	
	@Resource(name = "hyGroupServiceImpl")
	HyGroupService hyGroupService;
	
	@Resource(name = "departmentServiceImpl")
	private DepartmentService departmentService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name = "hyAreaServiceImpl")
	HyAreaService  hyAreaService;
	
	@Resource(name = "insuranceServiceImpl")
	InsuranceService insuranceService;
	
	@Resource(name="hySupplierContractServiceImpl")
	HySupplierContractService hySupplierContractService;
	
	@Resource(name = "lineCatagoryServiceImpl")
	LineCatagoryService lineCatagoryService;
	
	@Resource(name="commonSequenceServiceImp")
	CommonSequenceService commonSequenceService;
	
	@Resource(name = "hyGroupOtherpriceServiceImpl")
	HyGroupOtherpriceService hyGroupOtherpriceService;
	
	@Resource(name = "hyGroupPriceServiceImpl")
	HyGroupPriceService hyGroupPriceService;

	@Resource(name = "mhLineServiceImpl")
	MhLineService mhLineService;
	
	@Resource(name = "mhLineRefundServiceImpl")
	MhLineRefundService mhLineRefundService;
	
	@Resource(name = "mhLineTravelsServiceImpl")
	MhLineTravelsService mhLineTravelsService;
	
	@Resource(name = "mhGroupPriceServiceImpl")
	MhGroupPriceService mhGroupPriceService;
	
	@Resource(name = "mhGroupOtherPriceServiceImpl")
	MhGroupOtherPriceService mhGroupOtherPriceService;
	
	@Resource(name = "mhProductPictureServiceImpl")
	MhProductPictureService mhProductPictureService;
	
	@Resource(name = "gnyLabelServiceImpl")
	GNYLabelService gnyLabelService;
	
	@Resource(name = "gnyLabelProductServiceImpl")
	GNYLabelProductService gnyLabelProductService;
	
	@Resource(name = "cjyLabelServiceImpl")
	CJYLabelService cjyLabelService;
	
	@Resource(name = "cjyLabelProductServiceImpl")
	CJYLabelProductService cjyLabelProductService;
	
	@Resource(name = "zbyLabelServiceImpl")
	ZBYLabelService zbyLabelService;
	
	@Resource(name = "zbyLabelProductServiceImpl")
	ZBYLabelProductService zbyLabelProductService;
	
	/**
	 * 线路产品列表
	 * @param pageable
	 * @param hyLine
	 * @param lineCategory
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json list(Pageable pageable, HyLine hyLine,
					 HttpSession session, HttpServletRequest request) {
		Json json = new Json();
		try {
			Map<String, Object> obj = new HashMap<String, Object>();
			List<Map<String, Object>> lhm = new ArrayList<>();			
			List<Filter> filters = new ArrayList<Filter>();
			/*当前网络销售部可以编辑所有的产品*/						
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createDate"));
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			hyLine.setIsSale(IsSaleEnum.yishang);
			hyLine.setLineAuditStatus(AuditStatus.pass);
			Page<HyLine> lines = hyLineService.findPage(pageable, hyLine);
			
			if(lines.getRows().size() > 0) {
				for(HyLine line : lines.getRows()) {
					HashMap<String, Object> hm = new HashMap<String, Object>();
			        HyAdmin creater = line.getOperator();
			        hm.put("id", line.getId());
			        hm.put("pn", line.getPn());
			        hm.put("supplierName", line.getContract().getHySupplier().getSupplierName());
			        hm.put("lineName", line.getName());
			        String province = hyAreaService.find(line.getArea().getTreePaths().get(0)).getName(); //得到省份信息
			        hm.put("area", province);
			        hm.put("lineType", line.getLineType());
			        hm.put("lineCategory", line.getLineCategory());
			        hm.put("days", line.getDays());
			        
			        hm.put("latestGroup", line.getLatestGroup());
			        hm.put("lineAuditStatus", line.getLineAuditStatus());
			        hm.put("groupAuditStatus", line.getGroupAuditStatus());
			        hm.put("isSale", line.getIsSale());
			        hm.put("isInner", line.getIsInner());
			        hm.put("isCancel", line.getIsCancel());
			        hm.put("isEdit", line.getIsEdit());
			        hm.put("isTop", line.getIsTop());
			        
			        //使用isGuanwang字段来判断当前线路是否被完善过
			        hm.put("isGuanwang", line.getIsGuanwang());
			        if(line.getIsGuanwang()){
			        	//如果已经完善过了
			        	MhLine mhLine = line.getMhLine();
			        	hm.put("mhLineId", mhLine.getId());
			        	hm.put("mhIsSale", mhLine.getIsSale());
			        }else{
			        	hm.put("mhLineId", null);
			        	hm.put("mhIsSale", IsSaleEnum.weishang);
			        }
			        if (creater != null) {
			        	hm.put("operator", creater.getName());
			        }
			      
			        lhm.add(hm);
				}
			}
			
			obj.put("pageSize", Integer.valueOf(lines.getPageSize()));
			obj.put("pageNumber", Integer.valueOf(lines.getPageNumber()));
			obj.put("total", Long.valueOf(lines.getTotal()));
     		obj.put("rows", lhm);
			
			json.setSuccess(true);
			json.setMsg("获取列表成功");
			json.setObj(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 详情页 -包括新建页面和编辑页面
	 */
	@RequestMapping(value = "detail/view")
	@ResponseBody
	public Json detail(Long id, HttpSession session) {
		Json json = new Json();
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			//********************** 页面公共的部分 **********************
			HashMap<String, Object> obj = new HashMap<>();
			//合同下拉列表
			List<Map<String, Object>> hetongs = new ArrayList<>();			
			//保险方案下拉列表
			List<Map<String, Object>> baoxians = new ArrayList<>();
			
			HySupplierContract contract = null;
			
			HyAdmin liable = admin;
			
			if(admin.getHyAdmin() != null) {
				liable = admin.getHyAdmin();
			}
	
			Set<HySupplierContract> cs = liable.getLiableContracts();
			for(HySupplierContract c : cs) {
				HashMap<String, Object> hm = new HashMap<>();
				if(c.getContractStatus() == ContractStatus.zhengchang) {
					hm.put("id", c.getId());
					hm.put("contractCode", c.getContractCode());
					contract = c;
					hetongs.add(hm);
					break;
				}
			}	
			
			
			obj.put("contracts", hetongs);
			if(contract != null) {
				if(admin.getHyAdmin() != null) {
					obj.put("chujingAreas", admin.getAreaChujing());
					obj.put("guoneiAreas", admin.getAreaGuonei());
					obj.put("qicheAreas", admin.getAreaQiche());
				} else {
					obj.put("chujingAreas", contract.getChujingAreas());
					obj.put("guoneiAreas", contract.getGuoneiAreas());
					obj.put("qicheAreas", contract.getQicheAreas());
				}
				
			} else if(id != null) {
				HyLine line = hyLineService.find(id);
				if(line.getArea() != null && line.getContract() != null) {
					obj.put("areaName", line.getArea().getName());					
					obj.put("contractCode", line.getContract().getContractCode());
				}
				
			} else { //如果过期不让新建线路
				throw new RuntimeException("合同过期，无法新建线路!");
			}
			
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.in("status", true));
			obj.put("transports", transportService.findList(null, filters, null));
			
			List<Insurance> ins = insuranceService.findAll();
			for(Insurance in : ins) {
				HashMap<String, Object> hm = new HashMap<>();
				hm.put("id", in.getId());
				hm.put("insuranceCode", in.getInsuranceCode());
				hm.put("remark", in.getRemark());
				baoxians.add(hm);
			}
			obj.put("baoxian", baoxians);
			
			if(contract != null && contract.getHySupplier() != null) {
				obj.put("isInner", contract.getHySupplier().getIsInner());
			}
			
			
			//************* 判断是不是编辑的页面 *************
			if(id != null) {
				HyLine line = hyLineService.find(id);
				obj.put("line", line);
				List<Filter> fs = new ArrayList<>();
				fs.add(Filter.eq("lineId", line.getId()));
				if(line.getLineType().equals(LineType.qiche)){
					List<ZBYLabelProduct> zps = zbyLabelProductService.findList(null,fs,null);
					List<ZBYLabel> zbyLabels = new ArrayList<>();
					for(ZBYLabelProduct tmp:zps){
						ZBYLabel zLabel = zbyLabelService.find(tmp.getLabelId());
						zbyLabels.add(zLabel);
					}
					obj.put("labels", zbyLabels );
				}
				if(line.getLineType().equals(LineType.guonei)){
					List<GNYLabelProduct> gps = gnyLabelProductService.findList(null,fs,null);
					List<GNYLabel> gnyLabels = new ArrayList<>();
					for(GNYLabelProduct tmp:gps){
						GNYLabel gLabel = gnyLabelService.find(tmp.getLabelId());
						gnyLabels.add(gLabel);
					}
					obj.put("labels", gnyLabels );
				}
				if(line.getLineType().equals(LineType.chujing)){
					List<CJYLabelProduct> cps = cjyLabelProductService.findList(null,fs,null);
					List<CJYLabel> cjyLabels = new ArrayList<>();
					for(CJYLabelProduct tmp:cps){
						CJYLabel cLabel = cjyLabelService.find(tmp.getLabelId());
						cjyLabels.add(cLabel);
					}
					obj.put("labels", cjyLabels );
				}
				if(line.getIsGuanwang()){
					//如果该线路已经完善过，返回已经完善的内容再次编辑
					MhLine mhLine = line.getMhLine();
					obj.put("mhLine", mhLine);
					List<Filter> filters2 = new ArrayList<>();
					filters2.add(Filter.eq("productId", mhLine.getId()));
					filters2.add(Filter.eq("type", 1));
					List<MhProductPicture> productPictures = mhProductPictureService.findList(null,filters2,null);
					if(productPictures!=null){
						obj.put("pictures", productPictures);
					}
				}else{
					MhLine mhLine = new MhLine();
					mhLine.setFeeDescription("");
					mhLine.setName(line.getName());
					mhLine.setSort(0);
					mhLine.setIsSale(IsSaleEnum.weishang);
					mhLine.setRefundType(line.getRefundType());
					mhLine.setOutboundMemo(line.getOutboundMemo());
					mhLine.setChujingFileUrl(line.getOutbound());
					mhLine.setIntroduction(line.getIntroduction());
					mhLine.setBottomPrice(line.getLowestPrice());
					mhLine.setSaleCount(0);
					mhLine.setBriefDescription("");
					mhLine.setFeeDescription("");
					mhLine.setBookingInformation("");
					List<MhLineRefund> mhLineRefunds = new ArrayList<>();
					if(line.getLineRefunds()!=null)
						for(HyLineRefund refund:line.getLineRefunds()){
							MhLineRefund mr = new MhLineRefund();
							mr.setId(refund.getId());
							mr.setStartDay(refund.getStartDay());
							mr.setStartTime(refund.getStartTime());
							mr.setEndDay(refund.getEndDay());
							mr.setEndTime(refund.getEndTime());
							mr.setPercentage(refund.getPercentage());
							mhLineRefunds.add(mr);
						}
					mhLine.setMhLineRefunds(mhLineRefunds);
					List<MhLineTravels> mhLineTravels = new ArrayList<>();
					if(line.getLineTravels()!=null)
						for(HyLineTravels t:line.getLineTravels()){
							MhLineTravels mt = new MhLineTravels();
							mt.setId(t.getId());
							mt.setTransport(t.getTransport());
							mt.setIsBreakfast(t.getIsBreakfast());
							mt.setIsLunch(t.getIsLunch());
							mt.setIsDinner(t.getIsDinner());
							mt.setRoute(t.getRoute());
							mt.setRestaurant(t.getRestaurant());
							
							mhLineTravels.add(mt);
						}
					mhLine.setMhLineTravels(mhLineTravels);
					obj.put("mhLine", mhLine);
					List<MhProductPicture> pictures = new ArrayList<>();
					obj.put("pictures", pictures);
				}
				
			}			
			json.setSuccess(true);
			json.setMsg("查看详情成功");
			json.setObj(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	static class Wrap11{
		MhLine mhline;
		Long lineId;
		List<MhProductPicture> pictures;
		public MhLine getMhline() {
			return mhline;
		}
		public void setMhline(MhLine mhline) {
			this.mhline = mhline;
		}
		public Long getLineId() {
			return lineId;
		}
		public void setLineId(Long lineId) {
			this.lineId = lineId;
		}
		public List<MhProductPicture> getPictures() {
			return pictures;
		}
		public void setPictures(List<MhProductPicture> pictures) {
			this.pictures = pictures;
		}
		
	}
	/**
	 * 编辑线路产品
	 * @param hyLine
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "update")
	@ResponseBody
	public Json update(@RequestBody Wrap11 wrap11, HttpSession session) {
		Json j = new Json(); 
		try {
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			
			MhLine mhLine = wrap11.getMhline();
			Long lineId = wrap11.getLineId();
			List<MhProductPicture> pictures = wrap11.getPictures();
			
			if(lineId!=null){
				HyLine  hyLine = hyLineService.find(lineId);
				
				if(hyLine.getIsGuanwang()){
					//如果已经完善过了，就更新所有字段
					MhLine curr = hyLine.getMhLine();
					if(mhLine.getName()!=null){
						curr.setName(mhLine.getName());
					}
					if(mhLine.getBookingInformation()!=null){
						curr.setBookingInformation(mhLine.getBookingInformation());
					}
					if(mhLine.getBottomPrice()!=null){
						curr.setBottomPrice(mhLine.getBottomPrice());
					}
					//线路完善的最低价格取当前成人结算价最低价，当完善了团价格之后应该要保存所有团中的最低成人结算价
					if(mhLine.getBriefDescription()!=null){
						curr.setBriefDescription(mhLine.getBriefDescription());
					}
					if(mhLine.getChujingFileUrl()!=null){
						curr.setChujingFileUrl(mhLine.getChujingFileUrl());
					}
					if(mhLine.getFeeDescription()!=null){
						curr.setFeeDescription(mhLine.getFeeDescription());
					}
					if(mhLine.getIntroduction()!=null){
						curr.setIntroduction(mhLine.getIntroduction());
					}
					if(mhLine.getMattersNeedAttention()!=null){
						curr.setMattersNeedAttention(mhLine.getMattersNeedAttention());
					}
					if(mhLine.getOperator()!=null){
						curr.setOperator(admin);
					}
					if(mhLine.getOutboundMemo()!=null){
						curr.setOutboundMemo(mhLine.getOutboundMemo());
					}
					if(mhLine.getRecommendReason()!=null){
						curr.setRecommendReason(mhLine.getRecommendReason());
					}
					if(mhLine.getRefundType()!=null){
						curr.setRefundType(mhLine.getRefundType());
					}
					if(mhLine.getSaleCount()!=null){
						curr.setSaleCount(mhLine.getSaleCount());
					}
					if(mhLine.getSort()!=null){
						curr.setSort(mhLine.getSort());
					}
					if(mhLine.getIfSelfPaying()==null){
						curr.setIfSelfPaying(0);
					}else{
						curr.setIfSelfPaying(mhLine.getIfSelfPaying());
					}
					if(mhLine.getIfShopping()==null){
						curr.setIfShopping(0);
					}else{
						curr.setIfShopping(mhLine.getIfShopping());
					}
					//更新线路行程
					List<MhLineTravels> travels = curr.getMhLineTravels();
					if(!mhLine.getMhLineTravels().isEmpty()) {
						for(MhLineTravels t : mhLine.getMhLineTravels()) {
							t.setId(null);
							t.setMhLine(curr);
							t.setOperator(admin);
							if(t.getIfAccommodation()==null)
								t.setIfAccommodation(0);
								
						}
					}
					travels.clear();
					travels.addAll(mhLine.getMhLineTravels());
					//更新线路行程 
					List<MhLineRefund> refunds = curr.getMhLineRefunds();
					if(!mhLine.getMhLineTravels().isEmpty()) {
						for(MhLineRefund r : mhLine.getMhLineRefunds()) {
							r.setId(null);
							r.setMhLine(curr);
							r.setOperator(admin);
						}
					}
					refunds.clear();
					refunds.addAll(mhLine.getMhLineRefunds());
					if(pictures!=null && pictures.size()>0){
						//将之前的图片全部删掉，保存新的图片
						List<Filter> filters =  new ArrayList<>();
						filters.add(Filter.eq("type", 1));
						Long productId = null;
						if(mhLine.getId()!=null){
							productId= mhLine.getId();
						}else if(hyLine.getMhLine()!=null && hyLine.getMhLine().getId()!=null){
							productId=hyLine.getMhLine().getId();
						}else{
							throw new Exception("mhLineId 为空，编辑异常！");
						}
						filters.add(Filter.eq("productId", productId));
						List<MhProductPicture> ordpics = mhProductPictureService.findList(null,filters,null);
						for(MhProductPicture tmp:ordpics){
							mhProductPictureService.delete(tmp);
						}
						for(MhProductPicture tmp:pictures){
							tmp.setProductId(curr.getId());
							tmp.setType(1);
							mhProductPictureService.save(tmp);
						}
					}
					mhLineService.update(curr);
					j.setSuccess(true);
					j.setMsg("更新成功");	
					
				}else{
					//如果是首次完善
					mhLine.setOperator(admin);
					mhLine.setHyLine(hyLine);
					mhLine.setIsSale(IsSaleEnum.weishang);
					mhLine.setBottomPrice(hyLine.getLowestPrice());
					if(mhLine.getIfSelfPaying()==null)
						mhLine.setIfSelfPaying(0);
					if(mhLine.getIfShopping()==null)
						mhLine.setIfShopping(0);
					//更新线路行程 
					if(!mhLine.getMhLineTravels().isEmpty()) {
						for(MhLineTravels t : mhLine.getMhLineTravels()) {
							t.setId(null);
							t.setMhLine(mhLine);
							t.setOperator(admin);
							if(t.getIfAccommodation()==null)
								t.setIfAccommodation(0);
						}
					}
					//更新线路退款
					if(!mhLine.getMhLineRefunds().isEmpty()) {
						for(MhLineRefund r : mhLine.getMhLineRefunds()) {
							r.setId(null);
							r.setMhLine(mhLine);
							r.setOperator(admin);
						}
					}
					
					mhLine.setHyLine(hyLine);
					mhLineService.save(mhLine);
					hyLine.setIsGuanwang(true);
					hyLineService.update(hyLine);
					if(pictures!=null && pictures.size()>0){
						for(MhProductPicture tmp:pictures){
							tmp.setProductId(mhLine.getId());
							tmp.setType(1);
							mhProductPictureService.save(tmp);
						}
					}
					j.setSuccess(true);
					j.setMsg("完善成功");	
				}		
			}else{
				throw new Exception("没有对应的门店线路产品id");
			}		
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	/**
	 * 线路下线
	 * @param lineId mhline线路ID
	 * @return
	 */
	@RequestMapping(value="offline")
	@ResponseBody
	public Json xiaxian(Long id) {
		Json j = new Json();
		try {
			MhLine line = mhLineService.find(id);
			if(line==null)
				throw new Exception("找不到该线路");
			line.setIsSale(IsSaleEnum.yixia);
		
			mhLineService.update(line);
			j.setMsg("线路下线成功");
			j.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 线路上线
	 * @param mhlineId
	 * @return
	 */
	@RequestMapping(value="online")
	@ResponseBody
	public Json shangxian(Long id) {
		Json j = new Json();
		try {
			
			MhLine line = mhLineService.find(id);
			HyLine hyLine = line.getHyLine();
			if(line == null || hyLine==null || hyLine.getLatestGroup() == null) { //产品可编辑不可以直接上线，需要通过提交审核上线
				throw new RuntimeException("线路不可以直接上线");
			}
	
			if(hyLine.getLatestGroup().after(new Date()) && hyLine.getIsSale().equals(IsSaleEnum.yishang)) { //如果最新团期还有效，直接上线
				line.setIsSale(IsSaleEnum.yishang);
				mhLineService.update(line);
			} else if (hyLine.getLatestGroup().before(new Date())){
				throw new RuntimeException("上线失败，最新团期无效");
			}
			
			j.setSuccess(true);
			j.setMsg("上线成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	public static class Wrap {
		HyGroup hyGroup;
		List<Date> startDays = new ArrayList<>();
		public HyGroup getHyGroup() {
			return hyGroup;
		}
		public void setHyGroup(HyGroup hyGroup) {
			this.hyGroup = hyGroup;
		}
		public List<Date> getStartDays() {
			return startDays;
		}
		public void setStartDays(List<Date> startDays) {
			this.startDays = startDays;
		}	
	}
	
	@RequestMapping(value = "group/list/view")
	@ResponseBody
	public Json listGroup(Pageable pageable, Long lineId, HyGroup hyGroup) {
		Json j = new Json();
		try {
			HashMap<String, Object> jiagebili = new HashMap<>();
			Map<String, Object> obj = new HashMap<String, Object>();
			List<Map<String, Object>> lhm = new ArrayList<>();
			
			HyLine line = hyLineService.find(lineId);
			List<Filter> filters = new ArrayList<Filter>();
			filters.add(Filter.eq("line", line));
			
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("createDate"));
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			//必须是散客的团
			hyGroup.setTeamType(false);
			hyGroup.setAuditStatus(AuditStatus.pass);
			Page<HyGroup> groups = hyGroupService.findPage(pageable, hyGroup);
			
			if(groups.getRows().size() > 0) {
				for(HyGroup group : groups.getRows()) {
					HashMap<String, Object> hm = new HashMap<String, Object>();
			        hm.put("id", group.getId());
			        hm.put("startDay", group.getStartDay());
			        hm.put("endDay", group.getEndDay());
			        hm.put("lowestPrice", group.getLowestPrice());
			        hm.put("teamType", group.getTeamType());
			        hm.put("publishRange", group.getPublishRange());
			        hm.put("stock", group.getStock());
			        hm.put("auditStatus", group.getAuditStatus());
			        hm.put("groupState", group.getGroupState());
			        hm.put("signupNumber", group.getSignupNumber());
			        hm.put("occupyNumber", group.getOccupyNumber());
			        hm.put("isPerfect", group.getMhState()==null?0:group.getMhState());
			        List<HyGroupPrice> gps = new ArrayList<>(group.getHyGroupPrices());
			        if(!gps.isEmpty()) {
			        	hm.put("adultPrice", gps.get(0).getAdultPrice());
				        hm.put("adultPrice1", gps.get(0).getAdultPrice1());
			        }
			        
			        lhm.add(hm);
				}
			}
			
			obj.put("pageSize", Integer.valueOf(groups.getPageSize()));
			obj.put("pageNumber", Integer.valueOf(groups.getPageNumber()));
			obj.put("total", Long.valueOf(groups.getTotal()));
			if(line.getLineType() == LineType.guonei) {
				obj.put("koudianXianlu", line.getContract().getHySupplierDeductGuonei());
			} else if (line.getLineType() == LineType.chujing) {
				obj.put("koudianXianlu", line.getContract().getHySupplierDeductChujing());
			} else if (line.getLineType() == LineType.qiche) {
				obj.put("koudianXianlu", line.getContract().getHySupplierDeductQiche());
			}
			
			//加入价格比例
			filters.clear();
			filters.add(Filter.eq("eduleixing", Eduleixing.guoneijiagebili));
			List<CommonShenheedu> edu = commonEdushenheService.findList(null, filters, null);
			BigDecimal money = edu.get(0).getMoney();
			jiagebili.put("guonei", money);
		
			filters.clear();
			filters.add(Filter.eq("eduleixing", Eduleixing.chujingjiagebili));
			List<CommonShenheedu> edu1 = commonEdushenheService.findList(null, filters, null);
			BigDecimal money1 = edu1.get(0).getMoney();
			jiagebili.put("chujing", money1);
		
			filters.clear();
			filters.add(Filter.eq("eduleixing", Eduleixing.qichejiagebili));
			List<CommonShenheedu> edu2 = commonEdushenheService.findList(null, filters, null);
			BigDecimal money2 = edu2.get(0).getMoney();
			jiagebili.put("qiche", money2);
			
			filters.clear();
			filters.add(Filter.eq("eduleixing", Eduleixing.piaowujiagebili));
			List<CommonShenheedu> edu3 = commonEdushenheService.findList(null, filters, null);
			BigDecimal money3 = edu3.get(0).getMoney();
			jiagebili.put("piaowu", money3);
			
     		obj.put("rows", lhm);
			obj.put("jiagebili", jiagebili);
			j.setSuccess(true);
			j.setMsg("获取列表成功");
			j.setObj(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 团详情列表
	 * @param id 团id
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "group/detail/view")
	@ResponseBody
	public Json detailGroup(Long id, HttpSession session) {
		Json j = new Json();
		try {
			HyGroup group = hyGroupService.find(id);
			
			
			List<HyGroupPrice> hyGroupPrices = group.getHyGroupPrices();
			for(HyGroupPrice groupPrice:hyGroupPrices){
				if(groupPrice.getMhGroupPrice()==null){
					groupPrice.setMhGroupPrice(new MhGroupPrice());
				}
			}
			List<HyGroupOtherprice> hyGroupOtherprices = group.getHyGroupOtherprices();
			for(HyGroupOtherprice otherprice:hyGroupOtherprices){
				if(otherprice.getMhGroupOtherPrice()==null){
					otherprice.setMhGroupOtherPrice(new MhGroupOtherPrice());
				}
			}
			
			
			j.setSuccess(true);
			j.setMsg("查看详情成功");
			j.setObj(group);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	/**
	 * 编辑团
	 * @param group
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "group/update")
	@ResponseBody
	public Json updateGroup(@RequestBody HyGroup hyGroup, HttpSession session) {
		Json j = new Json(); 
		try {			
			/**
			 * 获取当前用户
			 */
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin operator = hyAdminService.find(username);
			
			HyLine line = hyLineService.find(hyGroup.getLine().getId());
			if(line.getMhLine()==null ){
				throw new Exception("请先完善对应线路！");
			}			
			MhLine mhLine = line.getMhLine();
			if(mhLine.getBottomPrice()==null){
				mhLine.setBottomPrice(BigDecimal.ZERO);
			}
			HyGroup group = hyGroupService.find(hyGroup.getId());
			//编辑普通价格和最低价格
			if(!hyGroup.getHyGroupPrices().isEmpty()) {
				for(HyGroupPrice t : hyGroup.getHyGroupPrices()) {
					HyGroupPrice curr = hyGroupPriceService.find(t.getId());
					if(curr.getMhGroupPrice()==null){
						//首次完善
						MhGroupPrice mhGroupPrice = t.getMhGroupPrice();
						mhGroupPrice.setHyGroup(group);
						mhGroupPrice.setHyGroupPrice(curr);
						mhGroupPrice.setOperator(operator);						
						//设置官网完善线路的最低价格
						if(null != mhLine) {
							if(mhLine.getBottomPrice().compareTo(BigDecimal.ZERO) == 0) {
								mhLine.setBottomPrice(mhGroupPrice.getMhAdultSalePrice());
							} else {
								BigDecimal c = mhLine.getBottomPrice().compareTo(mhGroupPrice.getMhAdultSalePrice()) > 0 ? mhGroupPrice.getMhAdultSalePrice():mhLine.getBottomPrice();
								mhLine.setBottomPrice(c);
							}
						}
						
						mhGroupPriceService.save(mhGroupPrice);
					}else{						
						MhGroupPrice old = curr.getMhGroupPrice();
						MhGroupPrice xin = t.getMhGroupPrice();
						old.setOperator(operator);
						old.setMhAdultSalePrice(xin.getMhAdultSalePrice());
						old.setMhAdultWaimaiPrice(xin.getMhAdultWaimaiPrice());
						old.setMhChildrenSalePrice(xin.getMhChildrenSalePrice());
						old.setMhChildrenWaimaiPrice(xin.getMhChildrenWaimaiPrice());
						old.setMhOldSalePrice(xin.getMhOldSalePrice());
						old.setMhOldWaimaiPrice(xin.getMhOldWaimaiPrice());
						old.setMhStudentSalePrice(xin.getMhStudentSalePrice());
						old.setMhStudentWaimaiPrice(xin.getMhStudentWaimaiPrice());
						//设置官网完善线路的最低价格
						if(null != mhLine) {
							if(mhLine.getBottomPrice().compareTo(BigDecimal.ZERO) == 0) {
								mhLine.setBottomPrice(xin.getMhAdultSalePrice());
							} else {
								BigDecimal c = mhLine.getBottomPrice().compareTo(xin.getMhAdultSalePrice()) > 0 ? xin.getMhAdultSalePrice():mhLine.getBottomPrice();
								mhLine.setBottomPrice(c);
							}
						}
						
						mhGroupPriceService.update(old);
					}
				}

				mhLineService.update(mhLine);
			}					
			//编辑其他价格
			List<HyGroupOtherprice> hgops = group.getHyGroupOtherprices();
			if(!hyGroup.getHyGroupOtherprices().isEmpty()) {
				for(HyGroupOtherprice o : hyGroup.getHyGroupOtherprices()) {
					HyGroupOtherprice curr = hyGroupOtherpriceService.find(o.getId());
					if(curr.getMhGroupOtherPrice()==null){
						//首次完善
						MhGroupOtherPrice mhGroupOtherPrice = o.getMhGroupOtherPrice();
						mhGroupOtherPrice.setHyGroup(group);
						mhGroupOtherPrice.setHyGroupOtherprice(curr);
						mhGroupOtherPrice.setOperator(operator);
						mhGroupOtherPriceService.save(mhGroupOtherPrice);
					}else{
						MhGroupOtherPrice old = curr.getMhGroupOtherPrice();
						MhGroupOtherPrice xin = o.getMhGroupOtherPrice();
						old.setMhBuchuangweiSalePrice(xin.getMhBuchuangweiSalePrice());
						old.setMhBuchuangweiWaimaiPrice(xin.getMhBuchuangweiWaimaiPrice());
						old.setMhBumenpiaoSalePrice(xin.getMhBumenpiaoSalePrice());
						old.setMhBumenpiaoWaimaiPrice(xin.getMhBumenpiaoWaimaiPrice());
						old.setMhBuwopuSalePrice(xin.getMhBuwopuSalePrice());
						old.setMhBuwopuWaimaiPrice(xin.getMhBuwopuWaimaiPrice());
						old.setMhDanfangchaSalePrice(xin.getMhDanfangchaSalePrice());
						old.setMhDanfangchaWaimaiPrice(xin.getMhDanfangchaWaimaiPrice());
						old.setMhErtongzhanchuangSalePrice(xin.getMhErtongzhanchuangSalePrice());
						old.setMhErtongzhanchuangWaimaiPrice(xin.getMhErtongzhanchuangWaimaiPrice());
						old.setOperator(operator);
						mhGroupOtherPriceService.update(old);
					}
				}
			}
			group.setMhState(1);	
			hyGroupService.update(group);
			j.setSuccess(true);
			j.setMsg("更新成功");		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	/**
	 * 返回该线路产品对应的标签
	 * @param id
	 * @param type 0-周边游 1-国内游 2-出境游
	 * @param name
	 * @return
	 */
	@RequestMapping("label/list")
	@ResponseBody
	public Json getLabels(LineType type,String name){
		Json json = new Json();
		try {
			if(type == null)
				throw new Exception("线路类型不能为空");
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.isNotNull("parent"));
			if(name != null)
				filters.add(Filter.like("fullName", name));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			if(type.equals(LineType.qiche)){
				//当前类型为周边游
				List<ZBYLabel> zbyLabels = zbyLabelService.findList(null,filters,orders);
				json.setObj(zbyLabels);
			}
			
			if(type.equals(LineType.guonei)){
				//当前类型为国内游
				List<GNYLabel> gnyLabels = gnyLabelService.findList(null,filters,orders);
				json.setObj(gnyLabels);
			}
			
			if(type.equals(LineType.chujing)){
				//当前类型为出境游
				List<CJYLabel> cjyLabels = cjyLabelService.findList(null,filters,orders);
				json.setObj(cjyLabels);
			}
			json.setMsg("获取成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("获取失败："+e.getMessage());
			json.setSuccess(false);
		}
		return json;
	}
	static class Wrap22{
		LineType type;
		Long lineId;
		List<Long> labelIds;
		public LineType getType() {
			return type;
		}
		public void setType(LineType type) {
			this.type = type;
		}
		public Long getLineId() {
			return lineId;
		}
		public void setLineId(Long lineId) {
			this.lineId = lineId;
		}
		public List<Long> getLabelIds() {
			return labelIds;
		}
		public void setLabelIds(List<Long> labelIds) {
			this.labelIds = labelIds;
		}
		
	}
	@RequestMapping("label/update")
	@ResponseBody
	public Json updateLabels(@RequestBody Wrap22 wrap22){
		Json json = new Json();
		try {
			LineType type = wrap22.getType();
			Long lineId = wrap22.getLineId();
			List<Long> oldlabelIds = wrap22.getLabelIds();
			
			if(type == null)
				throw new Exception("线路类型不能为空");
			if(lineId == null)
				throw new Exception("线路id不能为空");
			if(oldlabelIds == null)
				throw new Exception("labelIds参数不能为null");
			
			//需要对labelIds去重
			Set<Long> labelIds = new HashSet<>();
			labelIds.addAll(oldlabelIds);
				
			List<Filter> filters  = new ArrayList<>();
			filters.add(Filter.eq("lineId",lineId));
			if(type.equals(LineType.qiche)){
				List<ZBYLabelProduct> zbys = zbyLabelProductService.findList(null,filters,null);
				for(ZBYLabelProduct zby:zbys){
					zbyLabelProductService.delete(zby);
				}
				for(Long labelId:labelIds){
					ZBYLabelProduct tmp = new ZBYLabelProduct();
					tmp.setLabelId(labelId);
					tmp.setLineId(lineId);
					tmp.setSort(0);
					zbyLabelProductService.save(tmp);
				}
			}
			if(type.equals(LineType.guonei)){
				List<GNYLabelProduct> gnys = gnyLabelProductService.findList(null,filters,null);
				for(GNYLabelProduct gny:gnys){
					gnyLabelProductService.delete(gny);
				}
				for(Long labelId:labelIds){
					GNYLabelProduct tmp = new GNYLabelProduct();
					tmp.setLabelId(labelId);
					tmp.setLineId(lineId);
					tmp.setSort(0);
					gnyLabelProductService.save(tmp);
				}
			}
			if(type.equals(LineType.chujing)){
				List<CJYLabelProduct> cjys = cjyLabelProductService.findList(null,filters,null);
				for(CJYLabelProduct cjy:cjys){
					cjyLabelProductService.delete(cjy);
				}
				
				for(Long labelId:labelIds){
					CJYLabelProduct tmp = new CJYLabelProduct();
					tmp.setLabelId(labelId);
					tmp.setLineId(lineId);
					tmp.setSort(0);
					cjyLabelProductService.save(tmp);
				}
			}	
			json.setMsg("更新成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("更新失败："+e.getMessage());
			json.setSuccess(false); 
		}
		return json;
	}
	@RequestMapping("label/full")
	@ResponseBody
	public Json setfull(){
		Json json = new Json();
		try {
//			List<Filter> filters = new ArrayList<>();
//			filters.add(Filter.isNotNull("parent"));
//			if(name != null)
//				filters.add(Filter.like("fullName", name));
			List<ZBYLabel> zbyLabels = zbyLabelService.findAll();
			for(ZBYLabel label:zbyLabels){
				if(label.getParent()==null)
					label.setFullName(label.getName());
				else
					label.setFullName(label.getParent().getName()+"/"+label.getName());
				zbyLabelService.update(label);
			}
			List<GNYLabel> gnyLabels = gnyLabelService.findAll();
			for(GNYLabel label:gnyLabels){
				if(label.getParent()==null)
					label.setFullName(label.getName());
				else
					label.setFullName(label.getParent().getName()+"/"+label.getName());
				gnyLabelService.update(label);
			}
			List<CJYLabel> cjyLabels = cjyLabelService.findAll();
			for(CJYLabel label:cjyLabels){
				if(label.getParent()==null)
					label.setFullName(label.getName());
				else
					label.setFullName(label.getParent().getName()+"/"+label.getName());
				cjyLabelService.update(label);
			}
			json.setMsg("获取成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("获取失败："+e.getMessage());
			json.setSuccess(false);
		}
		return json;
	}
	@RequestMapping("getInsurances/view")
	@ResponseBody
	public Json getInsurances(Pageable pageable,Long groupId){
		Json json=new Json();
		try {
			HyGroup hyGroup=hyGroupService.find(groupId);
			LineType lineType=hyGroup.getLine().getLineType();
			Integer classify;
			if(lineType.equals(LineType.chujing)){
				classify=1;
			}else{
				classify=0;
			}
			List<Filter> filters=new LinkedList<>();
			filters.add(Filter.eq("classify", classify));
			Integer days=hyGroup.getLine().getDays();
			pageable.setFilters(filters);
			Page<Insurance> page=insuranceService.findPage(pageable);
			List<Map<String, Object>> result=new LinkedList<>();
			for(Insurance tmp:page.getRows()){
				Map<String, Object> map=new HashMap<>();
				map.put("id", tmp.getId());
				map.put("createDate", tmp.getCreateDate());
				map.put("modifyDate", tmp.getModifyDate());
				map.put("classify", tmp.getClassify());
				map.put("insuranceCode", tmp.getInsuranceCode());
				map.put("remark", tmp.getRemark());
				map.put("insuranceAttachs", tmp.getInsuranceAttachs());
				List<InsurancePrice> insurancePrices=new LinkedList<>();
				for(InsurancePrice price:tmp.getInsurancePrices()){
					if(days.compareTo(price.getStartDay())>=0&&days.compareTo(price.getEndDay())<=0){
						insurancePrices.add(price);
					}
				}
				map.put("insurancePrices", insurancePrices);
				map.put("insuranceTimes", tmp.getInsuranceTimes());
				result.add(map);
			}
			Page<Map<String, Object>>page2=new Page<>(result, page.getTotal(), pageable);
			json.setSuccess(true);
			json.setMsg("获取成功");
			json.setObj(page2);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("获取错误： "+e.getMessage());
			e.printStackTrace();
			// TODO: handle exception
		}
		return json;
	}
}
