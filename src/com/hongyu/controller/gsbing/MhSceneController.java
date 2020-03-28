package com.hongyu.controller.gsbing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
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
import com.hongyu.entity.GNYLabel;
import com.hongyu.entity.GNYLabelProduct;
import com.hongyu.entity.HyTicketPriceInbound;
import com.hongyu.entity.HyTicketScene;
import com.hongyu.entity.HyTicketSceneTicketManagement;
import com.hongyu.entity.JDLabel;
import com.hongyu.entity.JDLabelProduct;
import com.hongyu.entity.MhProductPicture;
import com.hongyu.entity.ZBYLabel;
import com.hongyu.entity.ZBYLabelProduct;
import com.hongyu.entity.HyLine.LineType;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HySupplierService;
import com.hongyu.service.HyTicketPriceInboundService;
import com.hongyu.service.HyTicketSceneService;
import com.hongyu.service.HyTicketSceneTicketManagementService;
import com.hongyu.service.JDLabelProductService;
import com.hongyu.service.JDLabelService;
import com.hongyu.service.MhProductPictureService;

/**门户完善门票相关接口*/
@Controller
@RequestMapping("/admin/menhuPerfect/scene/")
public class MhSceneController {
	@Resource
	private TaskService taskService;

	@Resource
	private HistoryService historyService;
	
	@Resource
	private RuntimeService runtimeService;
	
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hySupplierServiceImpl")
	private HySupplierService hySupplierService;
	@Resource(name="hyTicketSceneServiceImpl")
	private HyTicketSceneService hyTicketSceneService;
	
	@Resource(name="hyTicketSceneTicketManagementServiceImpl")
	private HyTicketSceneTicketManagementService hyTicketSceneTicketManagementService;
	
	@Resource(name="hyTicketPriceInboundServiceImpl")
	private HyTicketPriceInboundService hyTicketPriceInboundService;
	
	@Resource(name="mhProductPictureServiceImpl")
	private MhProductPictureService mhProductPictureService;
	
	@Resource(name = "jdLabelServiceImpl")
	JDLabelService jdLabelService;
	
	@Resource(name = "jdLabelProductServiceImpl")
	JDLabelProductService jdLabelProductService;
	
	/**景区列表页*/
	@RequestMapping(value="list/view")
	@ResponseBody
	public Json listview(Pageable pageable,Integer mhState,String sceneName,String pn) 
	{
		Json json=new Json();
		try {
			Map<String,Object> obj=new HashMap<String,Object>();
			List<Filter> filters=new ArrayList<>();
			
		    filters.add(Filter.eq("mhState",mhState));
			
			if(sceneName!=null) {
				filters.add(Filter.like("sceneName", sceneName));
			}
			if(pn!=null) {
				filters.add(Filter.eq("pn", pn));
			}
			pageable.setFilters(filters);
			List<Order> orders=new ArrayList<>();
			orders.add(Order.desc("createTime"));
			pageable.setOrders(orders);
			Page<HyTicketScene> page=hyTicketSceneService.findPage(pageable);
			List<HashMap<String, Object>> list = new ArrayList<>();
			if(page.getTotal()>0) {
				for(HyTicketScene scene:page.getRows()) {
					HashMap<String,Object> map=new HashMap<String,Object>();
					map.put("id", scene.getId());
					map.put("sceneName",scene.getSceneName());
					map.put("pn",scene.getPn());
					map.put("star",scene.getStar());
					if(scene.getArea()!=null) {
						map.put("address",scene.getArea().getFullName()+scene.getSceneAddress());
					}
					else {
						map.put("address",scene.getSceneAddress());
					}
					map.put("mhState",scene.getMhState());
					list.add(map);
				}
			}
			obj.put("rows", list);
		    obj.put("pageNumber", Integer.valueOf(pageable.getPage()));
		    obj.put("pageSize", Integer.valueOf(pageable.getRows()));
		    obj.put("total",Long.valueOf(page.getTotal()));
			json.setSuccess(true);
			json.setObj(obj);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**景区详情页*/
	@RequestMapping(value="detail/view")
	@ResponseBody
	public Json detail(Long id)
	{
		Json json=new Json();
		try {
			HyTicketScene hyTicketScene=hyTicketSceneService.find(id);
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("sceneName",hyTicketScene.getSceneName());
			map.put("area", hyTicketScene.getArea().getFullName());	
			map.put("address", hyTicketScene.getSceneAddress());
			map.put("star", hyTicketScene.getStar());
			map.put("openTime", hyTicketScene.getOpenTime());
			map.put("closeTime", hyTicketScene.getCloseTime());
			map.put("ticketExchangeAddress", hyTicketScene.getTicketExchangeAddress());
			//以下为门户相关信息			
			map.put("mhOperator", hyTicketScene.getMhOperator());
			map.put("mhReserveReq", hyTicketScene.getMhReserveReq());
			map.put("mhCreateTime", hyTicketScene.getCreateTime());
			map.put("mhReserveReq", hyTicketScene.getMhReserveReq());
			map.put("mhsceneName", hyTicketScene.getMhSceneName());
			map.put("mhAddress", hyTicketScene.getMhSceneAddress());
			map.put("mhBriefIntroduction", hyTicketScene.getMhBriefIntroduction());
			map.put("mhIntroduction", hyTicketScene.getMhIntroduction());
			
			//产品图片相关信息
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("type", 3));//3-门票
			filters.add(Filter.eq("productId", hyTicketScene.getId()));
			List<MhProductPicture> productPictures=mhProductPictureService.findList(null,filters,null);
			List<Map<String,Object>> pictureList=new ArrayList<>();
			for(MhProductPicture picture:productPictures) {
				Map<String,Object> obj=new HashMap<>();
				obj.put("source", picture.getSource());
				obj.put("large", picture.getLarge());
				obj.put("medium", picture.getMedium());
				obj.put("thumbnail", picture.getThumbnail());
				obj.put("isMark", picture.getIsMark());
				pictureList.add(obj);
			}
			map.put("mhProductPictures", pictureList);
			
			//标签相关信息
			List<Filter> fs = new ArrayList<>();
			fs.add(Filter.eq("ticketSceneId", hyTicketScene.getId()));
			
			List<JDLabelProduct> jps = jdLabelProductService.findList(null,fs,null);
			List<JDLabel> jdLabels = new ArrayList<>();
			for(JDLabelProduct tmp:jps){
				JDLabel jLabel = jdLabelService.find(tmp.getLabelId());
				jdLabels.add(jLabel);
			}
			map.put("labels", jdLabels );
			
			json.setMsg("查询成功");
		    json.setSuccess(true);
		    json.setObj(map);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	static class WrapScene{
		private Long sceneId;
		private HyTicketScene hyTicketScene;
		private List<MhProductPicture> mhProductPictures=new ArrayList<>();
		public Long getSceneId() {
			return sceneId;
		}
		public void setSceneId(Long sceneId) {
			this.sceneId = sceneId;
		}
		public HyTicketScene getHyTicketScene() {
			return hyTicketScene;
		}
		public void setHyTicketScene(HyTicketScene hyTicketScene) {
			this.hyTicketScene = hyTicketScene;
		}
		public List<MhProductPicture> getMhProductPictures() {
			return mhProductPictures;
		}
		public void setMhProductPictures(List<MhProductPicture> mhProductPictures) {
			this.mhProductPictures = mhProductPictures;
		}
	}
	
	@RequestMapping(value="scenePerfect")
	@ResponseBody
	public Json scenePerfect(@RequestBody WrapScene wrapScene,HttpSession session) 
	{
		Json json=new Json();
		try {
			Long sceneId=wrapScene.getSceneId();
			HyTicketScene hyTicketScene=hyTicketSceneService.find(sceneId);
			HyTicketScene scene=wrapScene.getHyTicketScene();
			hyTicketScene.setMhSceneAddress(scene.getMhSceneAddress());
			hyTicketScene.setMhBriefIntroduction(scene.getMhBriefIntroduction());
			hyTicketScene.setMhIntroduction(scene.getMhIntroduction());
			hyTicketScene.setMhSceneName(scene.getMhSceneName());
			hyTicketScene.setMhReserveReq(scene.getMhReserveReq());
			/**
			 * 获取当前用户
			 */
			if(hyTicketScene.getMhState()==null || hyTicketScene.getMhState()==0) {
				hyTicketScene.setMhCreateTime(new Date());
				String username = (String) session.getAttribute(CommonAttributes.Principal);
				String operatorName=hyAdminService.find(username).getName();
				hyTicketScene.setMhOperator(operatorName);
			}
			else {
				hyTicketScene.setMhUpdateTime(new Date());
			}
			hyTicketScene.setMhState(1); //已完善
			hyTicketSceneService.update(hyTicketScene);
			
			//产品图片,删掉以前的,增加新的
			List<Filter> filters=new ArrayList<>();
			filters.add(Filter.eq("type", 3)); //3-门票
			filters.add(Filter.eq("productId", sceneId)); //景区id
			List<MhProductPicture> priProductPictures=mhProductPictureService.findList(null,filters,null);
			//删掉以前的
			for(MhProductPicture picture:priProductPictures) {
				mhProductPictureService.delete(picture);
			}
			
			//增加新的
			List<MhProductPicture> productPictures=wrapScene.getMhProductPictures();
			for(MhProductPicture picture:productPictures) {
				picture.setType(3); //3-门票
				picture.setProductId(sceneId);
				mhProductPictureService.save(picture);
			}
			json.setSuccess(true);
			json.setMsg("完善成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg("完善失败");
		}
		return json;
	}
	
	
	/**景区门票列表*/
	@RequestMapping(value="ticketList/view")
	@ResponseBody
	public Json ticketList(Long sceneId)
	{
		Json json=new Json();
		try {
			HyTicketScene hyTicketScene=hyTicketSceneService.find(sceneId);
			List<HyTicketSceneTicketManagement> sceneTickets=new ArrayList<>(hyTicketScene.getHyTicketSceneTickets());
			List<Map<String, Object>> list = new ArrayList<>();
			for(HyTicketSceneTicketManagement ticket:sceneTickets) {
				if(ticket.getStatus()==true) {
					Map<String,Object> roomMap=new HashMap<String,Object>();
					roomMap.put("ticketId", ticket.getId());
					roomMap.put("productId", ticket.getProductId());
					roomMap.put("productName", ticket.getProductName());
					roomMap.put("ticketType", ticket.getTicketType()); //1-成人票,2-学生票,3-儿童票,4-老人票
					roomMap.put("auditStatus", ticket.getAuditStatus());
					roomMap.put("saleStatus", ticket.getSaleStatus());
					roomMap.put("mhIsSale", ticket.getMhIsSale()); //官网是否上线
					list.add(roomMap);
				}
			}
			Collections.sort(list, new Comparator<Map<String,Object>>() {
				@Override
				public int compare(Map<String,Object> o1,Map<String,Object> o2) {
					String pd1 = o1.get("productId").toString();
					String pd2 = o2.get("productId").toString();
					return pd2.compareTo(pd1);  
				}
			});
			json.setSuccess(true);
			json.setObj(list);
			json.setMsg("查询成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**景区门票详情*/
	@RequestMapping(value="ticketDetail/view")
	@ResponseBody
	public Json ticketDetail(Long ticketId)
	{
		Json json=new Json();
		try {
			HyTicketSceneTicketManagement sceneTicket=hyTicketSceneTicketManagementService.find(ticketId);
			List<HyTicketPriceInbound> priceInbounds=new ArrayList<>(sceneTicket.getHyTicketPriceInbounds());
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("productId", sceneTicket.getProductId());
			map.put("productName", sceneTicket.getProductName());
			map.put("ticketType", sceneTicket.getTicketType()); //1-成人票,2-学生票,3-儿童票,4-老人票
			map.put("isReserve", sceneTicket.getIsReserve());
			map.put("days", sceneTicket.getDays());
			map.put("times", sceneTicket.getTimes());
			map.put("isRealName", sceneTicket.getIsRealName());
			map.put("refundReq", sceneTicket.getRefundReq());
			map.put("realNameRemark", sceneTicket.getRealNameRemark());
			map.put("reserveReq",sceneTicket.getReserveReq());		
			map.put("priceList", priceInbounds);
			
			//以下是门户相关信息
			map.put("mhProductName",sceneTicket.getMhProductName());
			map.put("mhReserveReq", sceneTicket.getMhReserveReq());
			map.put("mhRefundReq", sceneTicket.getMhRefundReq());
			map.put("mhIsSale", sceneTicket.getMhIsSale());
			json.setSuccess(true);
			json.setMsg("查询成功");
			json.setObj(map);
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**门票完善*/
	@RequestMapping(value="ticketPerfect")
	@ResponseBody
	public Json ticketPerfect(Long ticketId,String mhProductName,String mhReserveReq,String mhRefundReq)
	{
		Json json=new Json();
		try {
			HyTicketSceneTicketManagement ticket=hyTicketSceneTicketManagementService.find(ticketId);
			ticket.setMhProductName(mhProductName);
			ticket.setMhReserveReq(mhReserveReq);
			ticket.setMhRefundReq(mhRefundReq);
			hyTicketSceneTicketManagementService.update(ticket);
			json.setSuccess(true);
			json.setMsg("完善成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**价格完善*/
	@RequestMapping(value="pricePerfect")
	@ResponseBody
	public Json pricePerfect(Long priceId,BigDecimal mhDisplayPrice,BigDecimal mhSellPrice,BigDecimal mhPrice)
	{
		Json json=new Json();
		try {
			HyTicketPriceInbound price=hyTicketPriceInboundService.find(priceId);
			price.setMhDisplayPrice(mhDisplayPrice);
			price.setMhSellPrice(mhSellPrice);
			price.setMhPrice(mhPrice);
			hyTicketPriceInboundService.update(price);
			json.setSuccess(true);
			json.setMsg("完善成功");
		}
		catch(Exception e) {
			json.setSuccess(false);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**门户上线*/
	@RequestMapping(value="online")
	@ResponseBody
	public Json online(Long ticketId)
	{
		Json json=new Json();
		try {
			HyTicketSceneTicketManagement ticket=hyTicketSceneTicketManagementService.find(ticketId);
			if(ticket.getSaleStatus()!=2) {
				json.setSuccess(false);
				json.setMsg("供应商未上线");
				return json;
			}
			List<HyTicketPriceInbound> priceList=new ArrayList<>(ticket.getHyTicketPriceInbounds());
			for(HyTicketPriceInbound price:priceList) {
				if(price.getMhPrice()==null || price.getMhDisplayPrice()==null || price.getMhSellPrice()==null) {
					json.setSuccess(false);
					json.setMsg("门户相应价格未完善");
					return json;
				}
			}
			ticket.setMhIsSale(1); //门户上线
			hyTicketSceneTicketManagementService.update(ticket);
			json.setSuccess(true);
			json.setMsg("上线成功");
		}
		catch(Exception e) {
			json.setSuccess(true);
			json.setMsg(e.getMessage());
		}
		return json;
	}
	
	/**门户下线*/
	@RequestMapping(value="offline")
	@ResponseBody
	public Json offline(Long ticketId)
	{
		Json json=new Json();
		try {
			HyTicketSceneTicketManagement ticket=hyTicketSceneTicketManagementService.find(ticketId);
			ticket.setMhIsSale(0); //门户下线
			hyTicketSceneTicketManagementService.update(ticket);
			json.setSuccess(true);
			json.setMsg("下线成功");
		}
		catch(Exception e) {
			json.setSuccess(true);
			json.setMsg(e.getMessage());
		}
		return json;
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
	public Json getLabels(String name){
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.isNotNull("parent"));
			if(name != null)
				filters.add(Filter.like("fullName", name));
			List<JDLabel> jdLabels = jdLabelService.findList(null,filters,null);
			json.setObj(jdLabels);
			json.setMsg("获取成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("获取失败："+e.getMessage());
			json.setSuccess(false);
		}
		return json;
	}
	static class Wrap22{
		Long sceneId;
		List<Long> labelsIds;
		public Long getSceneId() {
			return sceneId;
		}
		public void setSceneId(Long sceneId) {
			this.sceneId = sceneId;
		}
		public List<Long> getLabelsIds() {
			return labelsIds;
		}
		public void setLabelsIds(List<Long> labelsIds) {
			this.labelsIds = labelsIds;
		}
		
	}
	@RequestMapping("label/update")
	@ResponseBody
	public Json updateLabels(@RequestBody Wrap22 wrap22){
		Json json = new Json();
		try {
			Long sceneId = wrap22.getSceneId();
			List<Long> oldlabelIds = wrap22.getLabelsIds();
			if(sceneId == null)
				throw new Exception("景点id不能为空");
			if(oldlabelIds == null)
				throw new Exception("labelIds参数不能为null");
			
			//需要对labelIds去重
			Set<Long> labelIds = new HashSet<>();
			labelIds.addAll(oldlabelIds);
			
			List<Filter> filters  = new ArrayList<>();
			filters.add(Filter.eq("ticketSceneId",sceneId));
			List<JDLabelProduct> jds = jdLabelProductService.findList(null,filters,null);
			for(JDLabelProduct jd:jds){
				jdLabelProductService.delete(jd);
			}
			for(Long labelId:labelIds){
				JDLabelProduct tmp = new JDLabelProduct();
				tmp.setLabelId(labelId);
				tmp.setTicketSceneId(sceneId);
				tmp.setSort(0);
				jdLabelProductService.save(tmp);
			}	
			json.setMsg("更新成功");
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("更新失败："+e.getMessage());
			json.setSuccess(false); 
		}
		return json;
	}
	
}
	