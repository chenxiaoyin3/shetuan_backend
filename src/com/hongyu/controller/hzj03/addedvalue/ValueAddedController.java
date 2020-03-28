package com.hongyu.controller.hzj03.addedvalue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.*;
import com.hongyu.entity.*;
import com.hongyu.service.*;
import com.hongyu.util.DateUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Comment;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter.Operator;

/** 门店增值业务 */
@Controller
@RequestMapping(value = "admin/valueAdded")
public class ValueAddedController {
	@Resource(name = "addedServiceTransferServiceImpl")
	AddedServiceTransferService addedServiceTransferService;
	
	@Resource(name = "addedServiceAndServiceTransferServiceImpl")
	AddedServiceAndServiceTransferService addedServiceAndServiceTransferService;
	
	@Resource(name = "addedServiceServiceImpl")
	AddedServiceService addedServiceService;

	@Resource(name = "hyAddedServiceSupplierServiceImpl")
	HyAddedServiceSupplierService hyAddedServiceSupplierService;
	
	@Resource(name = "hyAdminServiceImpl")
	HyAdminService hyAdminService;

	@Resource(name = "storeServiceImpl")
	StoreService storeService;

	@Resource(name = "hyOrderServiceImpl")
    HyOrderService hyOrderService;

	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;

	/** 门店增值业务 - 增值业务供应商下拉列表框 */
	@RequestMapping(value = "/suppliers/view")
	@ResponseBody
    public Json getSupplier(HttpSession session) {
        Json json = new Json();
        try {
            List<Filter> filters = new LinkedList<>();
            filters.add(Filter.eq("store", getStoreBySession(session)));
            List<HyAddedServiceSupplier> list = hyAddedServiceSupplierService.findList(null, filters, null);
            json.setObj(list);
            json.setSuccess(true);
        } catch (Exception e) {
            json.setSuccess(false);
            json.setMsg("操作失败");
            e.printStackTrace();
        }
        return json;
    }

	/** 门店增值业务 - 增加 */
	@RequestMapping(value = "/add")
	@ResponseBody
	public Json addValueAddedService(@RequestBody List<addedValueWrap> addedServices, HttpSession session) {
		Json json = new Json();

		try {
			List<AddedService> list = new ArrayList<>(addedServices.size());
			for (addedValueWrap a : addedServices) {
				AddedService addedService = new AddedService();
                addedService.setOrderId(a.getOrderId());
                addedService.setOrderSn(a.getOrderSn());
                addedService.setMoney(a.getMoney());
                HyAddedServiceSupplier hyAddedServiceSupplier = hyAddedServiceSupplierService.find(a.getSupplierId());
                addedService.setSupplier(hyAddedServiceSupplier);
                addedService.setItem(a.getItem());
                HyOrder hyOrder = hyOrderService.find(a.getOrderId());
                addedService.setCheckoutTime(DateUtil.getDateAfterSpecifiedDays(hyOrder.getFatuandate(), a.getN()));
                addedService.setStoreId(getStoreBySession(session).getId());
                addedService.setCreatetime(new Date());
                String username = (String) session.getAttribute(CommonAttributes.Principal);
                HyAdmin admin = hyAdminService.find(username);
                addedService.setOperator(admin);
                addedService.setStatus(0);
                list.add(addedService);
			}

			json = addedServiceService.addValueAddedService(list);

		} catch (Exception e) {
			json.setMsg("操作失败");
			json.setSuccess(false);
			e.printStackTrace();
		}

		return json;
	}

	/**
	 * 门店增值业务-列表页
	 */
	@RequestMapping(value = "/valueadded/list/view")
	@ResponseBody
	public Json valueaddedlist(Long orderId){
		Json json = new Json();
		try {
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("orderId", orderId));
			List<AddedService> addedServices = addedServiceService.findList(null,filters,null);
			List<HashMap<String, Object>> obj = new ArrayList<>();
			for(AddedService addedService:addedServices){
				HashMap<String, Object> map = new HashMap<>();
				HashMap<String, Object> map2  = new HashMap<>();
				map.put("checkoutTime",DateUtil.getDaysBetweenTwoDates(hyOrderService.find(orderId).getFatuandate(),addedService.getCheckoutTime()));
				map.put("item", addedService.getItem());
				map.put("money", addedService.getMoney());
				map2.put("key", addedService.getSupplier().getId());
//				map2.put("label", storeService.find(addedService.getStoreId()).getStoreName());
				map2.put("label", addedService.getSupplier().getName());
				map.put("supplier", map2);
				map.put("type", 0);
				obj.add(map);	
			}		
			json.setObj(obj);
			json.setMsg("查询成功");
			json.setSuccess(true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			json.setMsg("查询失败");
			json.setSuccess(false);
		}
		return json;
	}
	
	
	
	
	/** 增值业务打款 - 按供应商打款 - 列表 */
	@RequestMapping(value = "/supplier/list/view")
	@ResponseBody
	public Json getVASBySupplier(Pageable pageable, String supplierName, HttpSession session) {
		Json json = new Json();
		List<HashMap<String, Object>> res = new ArrayList<>();
		try {
            Long storeId = getStoreBySession(session).getId();
			if (StringUtils.isEmpty(supplierName)) { // 使用sql进行统计
				String jpql = "	SELECT supplier_id , SUM(money),SUM(CASE WHEN status = 3 THEN money ELSE 0 END ),SUM(CASE WHEN status < 3  THEN money ELSE 0 END ) FROM hy_added_service WHERE store_id = " + storeId + " GROUP BY supplier_id ORDER BY supplier_id";
				List<Object[]> list = addedServiceService.statis(jpql);
				if (list != null && list.size() > 0) {
					for (Object[] object : list) {
						HashMap<String, Object> map = new HashMap<>();
						Long supplierId = ((BigInteger) object[0]).longValue();
						HyAddedServiceSupplier hyAddedServiceSupplier = hyAddedServiceSupplierService.find(supplierId);
						map.put("supplierId", supplierId);
						map.put("supplierName", hyAddedServiceSupplier.getName());
						map.put("shouldPay", object[1]);
						map.put("hasPaid", object[2]);
						map.put("notPay", object[3]);
						res.add(map);
					}
				}else{
					HashMap<String, Object> objQ = new HashMap<>();
					List<HashMap<String, Object>> listQ = new ArrayList<>();
					objQ.put("pageSize", 10);
					objQ.put("pageNumber", 1);
					objQ.put("rows",listQ);
					objQ.put("total",0 );
					json.setObj(objQ);
                    json.setMsg("未获取到符合条件的数据");
                    json.setSuccess(true);
                    return json;
                }
			} else { // 按增值业务供应商名称筛选
				List<Filter> filters = new ArrayList<>();

				List<Filter> filters1 = new ArrayList<>();
				filters1.add(Filter.like("name", supplierName));
				List<HyAddedServiceSupplier> hyAddedServiceSuppliers = hyAddedServiceSupplierService.findList(null,
						filters1, null);

				filters.add(Filter.in("supplier", hyAddedServiceSuppliers));
				filters.add(Filter.eq("storeId", storeId));
				List<AddedService> list = addedServiceService.findList(null, filters, null);

				if (list == null || list.size() == 0) {
					HashMap<String, Object> objQ = new HashMap<>();
					List<HashMap<String, Object>> listQ = new ArrayList<>();
					objQ.put("pageSize", 10);
					objQ.put("pageNumber", 1);
					objQ.put("rows",listQ);
					objQ.put("total",0 );
					json.setObj(objQ);
					json.setMsg("未获取到符合条件的数据");
					json.setSuccess(true);
					return json;
				}

				BigDecimal total = new BigDecimal("0.00");
				BigDecimal hasPay = new BigDecimal("0.00");

				for (AddedService as : list) {
					total.add(as.getMoney());
					if (as.getStatus() == 3) {
						hasPay.add(as.getMoney());
					}
				}
				HashMap<String, Object> map = new HashMap<>();
				map.put("supplierId", list.get(0).getSupplier().getId());
				map.put("supplierName", list.get(0).getSupplier().getName());
				map.put("shouldPay", total);
				map.put("hasPaid", hasPay);
				map.put("notPay", total.subtract(hasPay));
				res.add(map);

			}
			Map<String, Object> answer = new HashMap<>();
			int page = pageable.getPage();
			int rows = pageable.getRows();
			answer.put("total", res.size());
			answer.put("pageNumber", pageable.getPage());
			answer.put("pageSize", pageable.getRows());
			answer.put("rows", res.subList((page - 1) * rows, page * rows > res.size() ? res.size() : page * rows));
			json.setObj(answer);
			json.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("操作失败");
			json.setSuccess(false);
		}

		return json;
	}

	/** 增值业务打款 - 按供应商打款 - 详情 */
	@RequestMapping("supplier/detail/view")
	@ResponseBody
	public Json getVASDetailBySuppler(Long supplierId) {
		Json json = new Json();

		HashMap<String, Object> map = new HashMap<>();
		try {

			// 增值业务供应商信息
			HyAddedServiceSupplier hyAddedServiceSupplier = hyAddedServiceSupplierService.find(supplierId);
			map.put("supplier", hyAddedServiceSupplier);

			// 增值服务项目列表(查询hy_added_service表)
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("supplier", hyAddedServiceSupplier));
			filters.add(Filter.eq("status", 0)); // 0 未审核-未付(已驳回-未付)
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			List<AddedService> unPaylist = addedServiceService.findList(null, filters, orders);
			map.put("list", unPaylist);

			// 申请记录(查询hy_added_service_transfer表)
			filters.clear();
			filters.add(Filter.eq("supplier", hyAddedServiceSupplier));
			filters.add(new Filter("status", Operator.gt, 0));
			filters.add(new Filter("status", Operator.lt, 4));
			List<AddedServiceTransfer> auditList = addedServiceTransferService.findList(null, filters, orders);
			map.put("auditList", auditList);

			json.setObj(map);
			json.setSuccess(true);

		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			e.printStackTrace();
		}

		return json;
	}

	/** 增值业务打款 - 按项目打款 - 列表 */
	@RequestMapping("item/list/view")
	@ResponseBody
	public Json getVASListByItem(Pageable pageable, Integer status, String orderSn, String item, String supplierName, HttpSession session) {
		Json json = new Json();
		List<HyAddedServiceSupplier> hyAddedServiceSuppliers = new ArrayList<>();
		if (StringUtils.isNotEmpty(supplierName)) {
			List<Filter> filters1 = new ArrayList<>();
			filters1.add(Filter.like("name", supplierName));
			hyAddedServiceSuppliers = hyAddedServiceSupplierService.findList(null, filters1, null);
			if (CollectionUtils.isEmpty(hyAddedServiceSuppliers)) {
				HashMap<String, Object> objQ = new HashMap<>();
				List<HashMap<String, Object>> listQ = new ArrayList<>();
				objQ.put("pageSize", 10);
				objQ.put("pageNumber", 1);
				objQ.put("rows",listQ);
				objQ.put("total",0 );
				json.setObj(objQ);
				json.setMsg("未获取到符合条件的数据");
				json.setSuccess(true);
				return json;
			}
		}

		// 筛选条件
		List<Filter> filters = new ArrayList<>();
		if(null != orderSn){
            filters.add(Filter.like("orderSn", orderSn));
        }
		if(null != item){
            filters.add(Filter.like("item", item));
        }
		if(null != status){
            filters.add(Filter.eq("status", status)); // 0 未审核   1已审核   2 待付款   3 已付款     不传  全部
        }
		if(CollectionUtils.isNotEmpty(hyAddedServiceSuppliers)){
            filters.add(Filter.in("supplier", hyAddedServiceSuppliers));
        }


        Long storeId = getStoreBySession(session).getId();
        filters.add(Filter.eq("storeId", storeId));
		pageable.setFilters(filters);

		// 按订单号倒序
		List<Order> orders = new ArrayList<>();
		orders.add(Order.desc("orderSn"));
		pageable.setOrders(orders);

		try {
			Page<AddedService> page = addedServiceService.findPage(pageable);
			json.setObj(page);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setMsg("操作失败");
			json.setSuccess(false);
			e.printStackTrace();
		}
		return json;
	}
	
	/** 增值业务付款 - 按项目打款 - 详情 */
	@RequestMapping("item/detail/view")
	@ResponseBody
	public Json getVASDetailByItem(Long id) {
		Json json = new Json();

		HashMap<String, Object> map = new HashMap<>();
		try {
			// 供应商信息
			AddedService addedService = addedServiceService.find(id);
			map.put("supplier", addedService.getSupplier());

			// 增值服务项目
			List<AddedService> addedServiceList = new ArrayList<>();
			addedServiceList.add(addedService);
			map.put("list", addedServiceList);

			// 申请记录
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("addedServiceId", addedService.getId()));
			List<Order> orders = new ArrayList<>();
			orders.add(Order.desc("id"));
			List<AddedServiceAndServiceTransfer> list = addedServiceAndServiceTransferService.findList(null, filters,
					orders);
			if (list == null || list.size() == 0) {
				map.put("auditList", null);
			} else {
				AddedServiceTransfer a = addedServiceTransferService.find(list.get(0).getAddedServiceTransferId());
				List<AddedServiceTransfer> auditList = new ArrayList<>();
				auditList.add(a);
				map.put("auditList", auditList);
			}

			json.setObj(map);
			json.setSuccess(true);
		} catch (Exception e) {
			json.setSuccess(false);
			json.setMsg("操作失败");
			e.printStackTrace();
		}

		return json;
	}
	
	
	/** 打款记录 - 审核 打款 详情(按供应商、按项目)*/
	@RequestMapping("auditPay/detail/view")
	@ResponseBody
	public Json getAuditPayDetail(Long id, HttpSession session){
		Json json = new Json();
		HashMap<String, Object> obj = new HashMap<>();
		
		try {
			AddedServiceTransfer a = addedServiceTransferService.find(id);
			
			// 审核步骤
			String processInstanceId = a.getProcessInstanceId();
			List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
			Collections.reverse(commentList);
			List<Map<String, Object>> list = new LinkedList<>();
			for (Comment comment : commentList) {
				Map<String, Object> map = new HashMap<>();
				String taskId = comment.getTaskId();
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId)
						.singleResult();
				String step = "";
				if (task != null) {
					step = task.getName();
				}
				map.put("step", step);
				String username = comment.getUserId();
				HyAdmin hyAdmin = hyAdminService.find(username);
				String name = "";
				if (hyAdmin != null) {
					name = hyAdmin.getName();
				}
				map.put("name", name);
				String str = comment.getFullMessage();
				int index = str.lastIndexOf(":");
				if (index < 0) {
					map.put("comment", " ");
					map.put("result", 1);
				} else {
					map.put("comment", str.substring(0, index));
					map.put("result", Integer.parseInt(str.substring(index + 1)));
				}
				map.put("time", comment.getTime());

				list.add(map);
			}

			obj.put("auditlist", list);
			
			// 增值业务
			List<AddedService> addedServiceList = new ArrayList<>();
			List<Filter> filters = new ArrayList<>();
			filters.add(Filter.eq("addedServiceTransferId", id));
			List<AddedServiceAndServiceTransfer> asastList = addedServiceAndServiceTransferService.findList(null, filters, null);
			for(AddedServiceAndServiceTransfer asast : asastList){
				AddedService addedService = addedServiceService.find(asast.getAddedServiceId());
				addedServiceList.add(addedService);
			}
			obj.put("addedServiceList", addedServiceList);
			
			// 收款人信息
			obj.put("supplier", a.getSupplier());
			// 申请信息
			obj.put("applyList", a);
			
			json.setObj(obj);
			json.setMsg("操作成功");
			json.setSuccess(true);
		} catch (Exception e) {
		    e.printStackTrace();
			json.setSuccess(false);
			json.setMsg("操作失败");
		}
		
		return json;
	}
	
	/** 提前付款申请*/
	@RequestMapping("/applySubmit")
	@ResponseBody
	public Json applySubmit(@RequestBody List<Long> ids,HttpSession session){
		Json json = new Json();
		
		try {
			json = addedServiceService.insertApplySubmit(ids, session);
		} catch (Exception e) {
			e.printStackTrace();
			json.setMsg("操作失败");
			json.setSuccess(false);
		}
		
		return json;
	}

	/**
	 * 根据session获取门店
	 * */
	private Store getStoreBySession(HttpSession session){
		try{
			String username = (String) session.getAttribute(CommonAttributes.Principal);
			HyAdmin admin = hyAdminService.find(username);
			Department department = admin.getDepartment();
			List<Filter> filters = new LinkedList<>();
			filters.add(Filter.eq("department", department));
			List<Store> stores = storeService.findList(null, filters, null);
			return stores.get(0);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 内部类 - 新建增值服务
	 * */
	private static class addedValueWrap{
	    private Long orderId;
	    private String orderSn;
        private BigDecimal money;
		private Long supplierId;
		private String item;
		private Integer n;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public String getOrderSn() {
            return orderSn;
        }

        public void setOrderSn(String orderSn) {
            this.orderSn = orderSn;
        }

        public BigDecimal getMoney() {
            return money;
        }

        public void setMoney(BigDecimal money) {
            this.money = money;
        }

        public Long getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(Long supplierId) {
            this.supplierId = supplierId;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public Integer getN() {
            return n;
        }

        public void setN(Integer n) {
            this.n = n;
        }
    }
}
