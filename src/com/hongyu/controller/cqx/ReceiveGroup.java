package com.hongyu.controller.cqx;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hongyu.CommonAttributes;
import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.entity.GroupDivide;
import com.hongyu.entity.GroupMember;
import com.hongyu.entity.GroupSendGuide;
import com.hongyu.entity.GroupSendGuide.GuideStatusEnum;
import com.hongyu.entity.HyGroup.GroupStateEnum;
import com.hongyu.entity.Guide;
import com.hongyu.entity.GuideAssignment;
import com.hongyu.entity.HyAdmin;
import com.hongyu.entity.HyGroup;
import com.hongyu.entity.HyLine;
import com.hongyu.entity.HyOrder;
import com.hongyu.entity.HyOrderCustomer;
import com.hongyu.entity.HyOrderItem;
import com.hongyu.entity.HyOrderApplication;
import com.hongyu.entity.HyOrderApplicationItem;
import com.hongyu.entity.HyRegulate;
import com.hongyu.entity.HyRoleAuthority.CheckedOperation;
import com.hongyu.entity.Store;
import com.hongyu.service.GroupDivideService;
import com.hongyu.service.GroupMemberService;
import com.hongyu.service.GroupSendGuideService;
import com.hongyu.service.GuideAssignmentService;
import com.hongyu.service.GuideService;
import com.hongyu.service.HyAdminService;
import com.hongyu.service.HyGroupService;
import com.hongyu.service.HyLineService;
import com.hongyu.service.HyOrderApplicationItemService;
import com.hongyu.service.HyOrderApplicationService;
import com.hongyu.service.HyOrderItemService;
import com.hongyu.service.HyOrderService;
import com.hongyu.service.HyRegulateService;
import com.hongyu.service.LanguageService;
import com.hongyu.service.StoreService;
import com.hongyu.util.Constants.AuditStatus;
@RestController
@Transactional(propagation = Propagation.REQUIRED)
@RequestMapping("/admin/business/receiveGroup/")
public class ReceiveGroup {
	@Resource(name="hyAdminServiceImpl")
	private HyAdminService hyAdminService;
	
	@Resource(name="hyLineServiceImpl")
	private HyLineService hyLineService;
	
	@Resource(name="hyGroupServiceImpl")
	private HyGroupService hyGroupService;
	
	@Resource(name = "hyOrderServiceImpl")
	HyOrderService hyOrderService;
	
	@Resource(name = "hyOrderItemServiceImpl")
	HyOrderItemService hyOrderItemService;
	
	@Resource(name = "hyOrderApplicationServiceImpl")
	HyOrderApplicationService hyOrderApplicationService;
	
	@Resource(name = "hyOrderApplicationItemServiceImpl")
	private HyOrderApplicationItemService hyOrderApplicationItemService;
	
	@Resource(name = "hyRegulateServiceImpl")
	HyRegulateService hyRegulateService;
	
	@Resource(name = "groupMemberServiceImpl")
	private GroupMemberService groupMemberService;
	
	@Resource(name = "storeServiceImpl")
	private StoreService storeService;
	
	@Resource(name = "groupSendGuideServiceImp")
	private GroupSendGuideService groupSendGuideService;
	
	@Resource(name = "languageServiceImpl")
	LanguageService languageService;
	
	@Resource(name = "guideAssignmentServiceImpl")
	GuideAssignmentService guideAssignmentService;
	
	@Resource(name="guideServiceImpl")
	GuideService guideService;
	
	@Resource(name = "groupDivideServiceImpl")
	private GroupDivideService groupDivideService;
	/**
	 * 鎺ュ洟璇︽儏鍒楄〃椤�
	 * @param pageable
	 * @param hyGroup
	 * @param session
	 * @param request
	 * @return
	 */
	//列表页
	@RequestMapping(value="list/view")
	public Json list(Pageable pageable,HyGroup hyGroup,
			HttpSession session,String start,String end,HttpServletRequest request) {
		Json j=new Json();
		try {
			Map<String,Object> obj=new HashMap<String,Object>();
			List<HashMap<String,Object>> result=new ArrayList<>();
			SimpleDateFormat simpleFormat=new SimpleDateFormat("yyyy-MM-dd");
			Date fromDate=simpleFormat.parse(start);
			Calendar calendar=new GregorianCalendar();
			calendar.setTime(fromDate);
			calendar.add(Calendar.DATE, -1);//鏃ユ湡鍑�1
			fromDate =calendar.getTime();
			Date toDate=simpleFormat.parse(end);
			calendar.setTime(toDate);
			calendar.add(Calendar.DATE, 1);//鏃ユ湡鍔�1
			toDate =calendar.getTime();
			/**
			 * 鑾峰彇褰撳墠鐢ㄦ埛
			 */
			String username=(String)session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin=hyAdminService.find(username);
			/** 
			 * 鑾峰彇鐢ㄦ埛鏉冮檺鑼冨洿
			 */
			CheckedOperation co=(CheckedOperation)request.getAttribute("co");
			List<Filter> filters=new ArrayList<Filter>();
			filters.add(Filter.gt("startDay", fromDate));
			filters.add(Filter.lt("startDay", toDate));
			filters.add(Filter.eq("auditStatus", AuditStatus.pass));
			List<Order> orders=new ArrayList<>();
			orders.add(Order.desc("createDate"));
			pageable.setFilters(filters);
			pageable.setOrders(orders);
			Page<HyGroup> groups=hyGroupService.findPage(pageable, hyGroup);
			if(groups.getRows().size()>0) {
				for(HyGroup group:groups.getRows()) {
					HyAdmin creator=group.getCreator();
					HashMap<String,Object> hm=new HashMap<String,Object>();
					HyLine line=group.getLine();
					hm.put("id", group.getId());
					hm.put("pn", line.getPn());
					hm.put("startDay",group.getStartDay());
					hm.put("endDay",group.getEndDay());
					hm.put("name", line.getName());
			        hm.put("teamType", group.getTeamType());
			        hm.put("publishRange", group.getPublishRange());
			        hm.put("stock", group.getStock());
			        hm.put("remainNumber", group.getRemainNumber());
			        hm.put("signupNumber", group.getSignupNumber());
			        hm.put("occupyNumber", group.getOccupyNumber());
			        hm.put("supplierName", line.getContract().getHySupplier().getSupplierName());
			        hm.put("operatorName",group.getOperatorName());
//			        hm.put("operator", line.getOperator().getName());
			        hm.put("groupState", group.getGroupState());
			        hm.put("name", line.getName());
			        hm.put("lineType", line.getLineType());
			        hm.put("koudianType", group.getKoudianType());
			        hm.put("percentageKoudian", group.getPercentageKoudian());
			        hm.put("personKoudian", group.getPersonKoudian());
			        hm.put("isDisplay", group.getIsDisplay());
			        hm.put("isInner", line.getIsInner());
			        List<Filter> filtersOfOrder=new ArrayList<Filter>();
					filtersOfOrder.add(Filter.eq("groupId", group.getId()));
					List<HyOrder> orderList=hyOrderService.findList(null, filtersOfOrder, null);					
					Integer people=0;
					for(HyOrder hyOrder:orderList) {
						List<HyOrderItem> orderItems=hyOrder.getOrderItems();
						for(HyOrderItem item:orderItems) {
							people=people+item.getNumber();//绠楁�讳汉鏁�
			        	}
					}
					hm.put("people", people);
			        BigDecimal totalMoney=BigDecimal.ZERO;
			        List<Filter> filters1=new ArrayList<>();
			        filters1.add(Filter.eq("groupId", group.getId()));
			        filters1.add(Filter.eq("status", 3));//绛涢�変緵搴斿晢閫氳繃鐨勮鍗�
			        List<HyOrder> hyOrders=hyOrderService.findList(null,filters1,null);
			        for(HyOrder temp:hyOrders) {
			        	List<HyOrderItem> orderItems=temp.getOrderItems();//榛樿鎸塷rderId绛涢��
			        	Integer returnQuantity=0;
			        	for(HyOrderItem item:orderItems) {
			        		returnQuantity=returnQuantity+item.getNumberOfReturn();//閫�璐ф暟閲�
			        	}
			        	if(temp.getPeople()>returnQuantity) {
			        		totalMoney=totalMoney.add(temp.getJiesuanMoney1().subtract(temp.getJiesuanTuikuan()))
			        				.subtract(temp.getStoreFanLi().subtract(temp.getDiscountedPrice()));
			        	}//totalMoney+(闄や繚闄╀箣澶栫殑璁㈠崟鏉＄洰鐨勬�荤粨绠椾环-缁撶畻閫�娆句环)-(闂ㄥ簵杩斿埄-浼樻儬閲戦)
			        }
			        hm.put("totalMoney",totalMoney);
			        if(group.getRegulateId()!=null) {
			        	HyRegulate regulate=hyRegulateService.find(group.getRegulateId());
			        	hm.put("jidiaoStatus", regulate.getStatus());
			        }
			        result.add(hm);
				}
			}
			obj.put("pageSize",Integer.valueOf(groups.getPageSize()));
			obj.put("pageNumber", Integer.valueOf(groups.getPageNumber()));
			obj.put("total", Long.valueOf(groups.getTotal()));
			obj.put("rows", result);
			j.setSuccess(true);
			j.setMsg("鑾峰彇鍒楄〃鎴愬姛");
			j.setObj(obj);
		}
		catch(Exception e) {
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 鍥㈠拰绾胯矾鐨勮鎯�
	 * @param id
	 * @return
	 */
	@RequestMapping(value="detail/view")
	public Json detail(Long id) {
		Json j = new Json();
		try {
			HyGroup hyGroup = hyGroupService.find(id);
			HyLine line = hyGroup.getLine();
			Map<String, Object> map = new HashMap<String, Object>();
			
			map.put("group", hyGroup); //鍥㈣鎯�
			map.put("line", line); //绾胯矾璇︽儏
				
			j.setMsg("鏌ョ湅鎴愬姛");
			j.setSuccess(true);
			j.setObj(map);
		} catch(Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	/**
	 * 鍥㈡垚鍛樿鎯�
	 * @param id
	 * @return
	 */
	@RequestMapping(value="groupMember/view")
	@ResponseBody
	public Json GroupMemberList(HttpSession session,Long group_id,String orderNumber,String operatorName,String name) {
		Json j=new Json();
		try {
			List<HashMap<String,Object>> list=new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			List<Filter> filters=new ArrayList<Filter>();
			HyGroup group=hyGroupService.find(group_id);
			filters.add(Filter.eq("hyGroup", group));//鏈夐棶棰�
			List<Order> orders=new ArrayList<Order>();
					//鎵惧埌鎵�鏈夌殑groupmember 
					List<GroupMember> groupMemberList = groupMemberService.findList(null, filters, null);
					int total = 0;
					
					long lastOrderItemId = -1;
					long lastOrderId = -1;
					for(GroupMember groupMember : groupMemberList){
						//閬嶅巻鎵�鏈夌殑order
						HyOrder hyOrder = groupMember.getHyOrder();
						
						if(hyOrder == null) {
							continue;
						}
						
						HashMap<String,Object> comMap = new HashMap<String,Object>();
						comMap.put("orderId",hyOrder.getId());
						comMap.put("departure",hyOrder.getDeparture());
						Store store = storeService.find(hyOrder.getStoreId());
						if(store!=null) {
							comMap.put("source", store.getStoreName());//闂ㄥ簵鍚嶇О
						}
						if(hyOrder.getOperator()!=null) {
							comMap.put("operatorName", hyOrder.getOperator().getName());//鎶ュ悕鍛樺伐 鎶ュ悕璁¤皟
							comMap.put("operatorMobile", hyOrder.getOperator().getMobile());//鍛樺伐鐢佃瘽
						}
						
						HyOrderCustomer hyOrderCustomer = groupMember.getHyOrderCustomer();
						HyOrderItem hyOrderItem = groupMember.getHyOrderItem();
						comMap.put("id", groupMember.getId());
						comMap.put("orderNumber", hyOrder.getOrderNumber());//璁㈠崟缂栧彿
						
						comMap.put("orderJiesuanMoney", hyOrder.getJiusuanMoney());
						comMap.put("orderJiesuanMoneyWithoutInsurance", hyOrder.getJiesuanMoney1());
						//鎬荤粨绠椾环锛堢畻浜嗕繚闄╋級
						comMap.put("yiShouPrice", hyOrder.getJiesuanMoney1().subtract(hyOrder.getJiesuanTuikuan()));
						comMap.put("qianShouPrice", hyOrder.getJiesuanTuikuan().negate());
						lastOrderId = hyOrder.getId();
						
						if(hyOrderCustomer!=null) {
							comMap.put("name", hyOrderCustomer.getName());//瀹汉濮撳悕
							comMap.put("gender", hyOrderCustomer.getGender());
							comMap.put("type", hyOrderCustomer.getType());//绫诲瀷
							comMap.put("certificatetType", hyOrderCustomer.getCertificateType());
							comMap.put("certificatet", hyOrderCustomer.getCertificate());
							comMap.put("phone",hyOrderCustomer.getPhone());
						}
						if(hyOrderItem != null) {
							//鏄壒娈婁环鏍�
							if(hyOrderItem.getPriceType() == 4){
								comMap.put("isSpecialPrice", 1);
							}
							else {
								comMap.put("isSpecialPrice", 0);
							}
							//鍜屼笂涓�涓浉绛� 閭ｄ箞浼�0锛屽惁鍒欎紶瀹為檯浠锋牸
							if(hyOrderItem.getId().equals(lastOrderItemId)) {
								comMap.put("settlementPrice", 0);
							}
							else {
								comMap.put("settlementPrice", hyOrderItem.getJiesuanPrice());//缁撶畻浠�
								
								lastOrderItemId = hyOrderItem.getId();
							}
							
						}
						
						comMap.put("subGroupsn", groupMember.getSubGroupsn());
						list.add(comMap);		
					}
					Iterator<HashMap<String,Object>> iterOrderNumber = list.iterator();
					if(orderNumber!=null) {
						while(iterOrderNumber.hasNext()){
							String temp=(String)iterOrderNumber.next().get("orderNumber");
							if(!temp.equals(orderNumber)) {
								iterOrderNumber.remove();
							}
						}
					}
					Iterator<HashMap<String,Object>> iterOrderNumber2 = list.iterator();
					if(operatorName!=null) {
						while(iterOrderNumber2.hasNext()){
							String temp=(String)iterOrderNumber2.next().get("operatorName");
							if(!temp.equals(operatorName)) {
								iterOrderNumber2.remove();
							}
						}
					}
					Iterator<HashMap<String,Object>> iterOrderNumber3 = list.iterator();
					if(name!=null) {
						while(iterOrderNumber3.hasNext()){
							String temp=(String)iterOrderNumber3.next().get("name");
							if(!temp.equals(name)) {
								iterOrderNumber3.remove();
							}
						}
					}
					
					map.put("rows", list);
				    map.put("total",list.size());
					
					j.setSuccess(true);
					j.setMsg("鏌ヨ鎴愬姛");
					j.setObj(map);
		}
		catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("鏌ヨ澶辫触");
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}
	
	/**
	 *閫�鍥㈡垚鍛樿鎯�
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "quitGroupMember/view")
	public Json quitGroupMember(HttpSession session,Long group_id) {
		Json j=new Json();
		try {
			List<HashMap<String,Object>> result =new ArrayList<>();
			Map<String,Object> map=new HashMap<String,Object>();
			HyGroup hyGroup=hyGroupService.find(group_id);
			List<HyOrder> hyOrderList=hyOrderService.findList(group_id);
			List<HyOrderApplicationItem> allHyOrderApplicationItemList=new ArrayList<>();
			for(HyOrder hyOrder:hyOrderList) {
				List<HyOrderApplication> hyOrderApplicationList=hyOrderApplicationService.findList(hyOrder.getId());
				for(HyOrderApplication hyOrderApplication:hyOrderApplicationList) {
					List<HyOrderApplicationItem> hyOrderAppplicationItemList=hyOrderApplication.getHyOrderApplicationItems();
					allHyOrderApplicationItemList.addAll(hyOrderAppplicationItemList);
				}
			}
			for(HyOrderApplicationItem temp:allHyOrderApplicationItemList) {
				map.put("refundPrice", temp.getJiesuanRefund());
			}
			List<HyOrderCustomer> allHyOrderCustomerList=new ArrayList<>();
			for(HyOrderApplicationItem hyOrderApplicationItem:allHyOrderApplicationItemList) {
				List<HyOrderItem> hyOrderItemList=hyOrderItemService.findList(hyOrderApplicationItem.getItemId());
				for(HyOrderItem hyOrderItem:hyOrderItemList) {
					List<HyOrderCustomer> hyOrderCustomerList=hyOrderItem.getHyOrderCustomers();
					allHyOrderCustomerList.addAll(hyOrderCustomerList);
				}
			}
			for(HyOrderCustomer temp:allHyOrderCustomerList) {
				map.put("name", temp.getName());
				map.put("type", temp.getType());
			}
			map.put("rows", result);
		    map.put("total",allHyOrderCustomerList.size());
			
			j.setSuccess(true);
			j.setMsg("鏌ヨ鎴愬姛");
			j.setObj(map);
		}
		catch(Exception e){
			j.setSuccess(false);
			j.setMsg("鏌ヨ澶辫触");
			j.setObj(null);
			e.printStackTrace();
		}
		return j;
	}
	
	/**
	 * 瀵兼父鍒楄〃
	 * @param id 鍥D
	 * @return
	 */
	@RequestMapping(value = "publishDetail/view")
	public Json publishDetail(Long groupId) {
		Json j = new Json(); 
		try {
			Map<String, Object> obj = new HashMap<String, Object>();
			List<Map<String, Object>> result = new ArrayList<>();
			HyGroup group = hyGroupService.find(groupId);
			
			List<Filter> filters = new ArrayList<>();//鏌ユ壘鐘舵�佷负姝ｅ父鐨�
			filters.add(Filter.eq("groupId", groupId));
			List<GuideAssignment> b = guideAssignmentService.findList(null, filters, null);
			for(GuideAssignment temp : b) {
				HashMap<String, Object> hm = new HashMap<String,Object>();
				Guide guide = guideService.find(temp.getGuideId());
				hm.put("id", temp.getId());
				hm.put("gId",guide.getId());
				hm.put("guideId", guide.getGuideSn());
				hm.put("name", guide.getName());
				hm.put("phone", guide.getPhone());
				result.add(hm);
			}
			obj.put("guideAssignments", result); //瀵兼父娲鹃仯鍒楄〃
			
			j.setSuccess(true);
			j.setMsg("鏌ョ湅鎴愬姛");		
			j.setObj(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			j.setSuccess(false);
			j.setMsg(e.getMessage());
		}
		return j;
	}
	
	@RequestMapping(value="/subList/view")
	@ResponseBody
	public Json GroupDivideInfomation(HttpSession session,Long group_id){
		Json json = new Json();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Filter> filters = new ArrayList<Filter>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		try {
			HyGroup group = hyGroupService.find(group_id);
			filters.add(Filter.eq("group", group));
			List<GroupDivide> groupDivides = groupDivideService.findList(null, filters, null);
			for(GroupDivide groupDivide : groupDivides) {
				HashMap<String,Object> comMap=new HashMap<String,Object>();
				comMap.put("id", groupDivide.getId());
				comMap.put("group_id", group_id);
				comMap.put("subGroupsn", groupDivide.getSubGroupsn());
				comMap.put("subGroupNo", groupDivide.getSubGroupNo());
				//comMap.put("guide", groupDivide.getGuide());
				if(groupDivide.getGuide()!=null) {
					HashMap<String,Object> guideMap = new HashMap<String,Object>();
					guideMap.put("guide_id", groupDivide.getGuide().getId());
					guideMap.put("guide_name", groupDivide.getGuide().getName());
					comMap.put("guide",guideMap);
				}
				else {
					comMap.put("guide",null);
				}
				list.add(comMap);
			}
			
			//map.put("groupDivide", list);	
			json.setSuccess(true);
			json.setMsg("鏌ヨ鎴愬姛");
			json.setObj(list);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("鏌ヨ澶辫触");
			json.setObj(null);
			e.printStackTrace();
		}
		return json;
		
	}
	
	@RequestMapping(value="/guideList/view")
	@ResponseBody
	public Json GuideList(HttpSession session,Long group_id){
		Json json = new Json();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Filter> filters = new ArrayList<Filter>();
		List<HashMap<String, Object>> list = new ArrayList<>();
		try {
			//HyGroup group = hyGroupService.find(group_id);
			filters.add(Filter.eq("groupId", group_id));
			filters.add(Filter.eq("serviceType", 0));
			//鐘舵�佷负姝ｅ父鐨�
			filters.add(Filter.eq("status", 1));
			List<GuideAssignment> guideAssignments = guideAssignmentService.findList(null, filters, null);
			for(GuideAssignment guideAssignment : guideAssignments) {
				HashMap<String,Object> comMap=new HashMap<String,Object>();
				comMap.put("guide_id", guideAssignment.getGuideId());
				comMap.put("guide_name", guideService.find(guideAssignment.getGuideId()).getName());
				list.add(comMap);
			}
			//map.put("guideList", list);
			
			
			json.setSuccess(true);
			json.setMsg("鏌ヨ鎴愬姛");
			json.setObj(list);
			
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("鏌ヨ澶辫触");
			json.setObj(null);
			e.printStackTrace();
		}
		return json;
	}
}
