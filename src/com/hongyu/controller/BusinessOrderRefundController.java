package com.hongyu.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.hongyu.entity.*;
import com.hongyu.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hongyu.Filter;
import com.hongyu.Json;
import com.hongyu.Order;
import com.hongyu.Page;
import com.hongyu.Pageable;
import com.hongyu.util.Constants;
import com.hongyu.util.OrderTransactionSNGenerator;

@Controller
@RequestMapping("/admin/business/orderrefund")
public class BusinessOrderRefundController {
	
	@Resource(name = "businessOrderRefundServiceImpl")
	BusinessOrderRefundService businessOrderRefundServiceImpl;
	
	@Resource(name = "businessOrderServiceImpl")
	BusinessOrderService businessOrderServiceImpl;
	
	@Resource(name="wechatAccountServiceImpl")
	WechatAccountService wechatAccountServiceImpl;
	
	@RequestMapping({"/page/view"})
	@ResponseBody
	public Json businessOrderRefundPage(Integer state, String orderCode, Pageable pageable, HttpSession session) {
		Json j = new Json();
		
		try {
			List<Filter> filters = new ArrayList<Filter>();
			if (state != null) {
				filters.add(Filter.eq("state", state));
			} else {
				filters.add(Filter.ge("state", Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_REFUND_MONEY));
				filters.add(Filter.le("state", Constants.BUSINESS_ORDER_REFUND_STATUS_FINISH));
			}
			if (StringUtils.isNotEmpty(orderCode)) {
				List<Filter> fs = new ArrayList<Filter>();
				fs.add(Filter.like("orderCode", orderCode));
				List<BusinessOrder> list = businessOrderServiceImpl.findList(null, fs, null);
				if (list.size() == 0) {
					Page<BusinessOrderRefund> page = new Page<BusinessOrderRefund>(new ArrayList<BusinessOrderRefund>(), 0, pageable);
					j.setSuccess(true);
					j.setMsg("查询成功");
					j.setObj(page);
					return j;
				}
				filters.add(Filter.in("businessOrder", list));
			}
			pageable.setFilters(filters);
			List<Order> orders = new ArrayList<Order>();
			orders.add(Order.desc("id"));
			pageable.setOrders(orders);
			Page<BusinessOrderRefund> page = businessOrderRefundServiceImpl.findPage(pageable);
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(page);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		return j;
	}
	
	@RequestMapping({"/detail/view"})
	@ResponseBody
	public Json businessOrderRefundDetail(Long id, HttpSession session) {
		Json j = new Json();
		
		try {
			if (id == null) {
				j.setSuccess(false);
				j.setMsg("缺少参数");
				j.setObj(null);
				return j;
			}
			BusinessOrderRefund refund = businessOrderRefundServiceImpl.find(id);
			if (refund == null) {
				j.setSuccess(false);
				j.setMsg("指定的订单退款记录不存在");
				j.setObj(null);
				return j;
			}
			j.setSuccess(true);
			j.setMsg("查询成功");
			j.setObj(refund);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("查询失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		return j;
	}
	
//	@RequestMapping({"/agreerefund"})
//	@ResponseBody
//	public Json businessOrderRefundAgree(Long id, HttpSession session) {
//		Json j = new Json();
//		
//		try {
//			if (id == null) {
//				j.setSuccess(false);
//				j.setMsg("缺少参数");
//				j.setObj(null);
//				return j;
//			}
//			BusinessOrderRefund refund = businessOrderRefundServiceImpl.find(id);
//			if (refund == null) {
//				j.setSuccess(false);
//				j.setMsg("指定的订单退款记录不存在");
//				j.setObj(null);
//				return j;
//			}
//			if (refund.getState() != Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_CONFIRM) {
//				j.setSuccess(false);
//				j.setMsg("当前退款订单未处于待售后人员确认状态");
//				j.setObj(null);
//				return j;
//			}
//			refund.setState(Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_RETURN_PRODUCT);
//			businessOrderRefundServiceImpl.save(refund);
//			j.setSuccess(true);
//			j.setMsg("设置成功");
//			j.setObj(refund);
//		} catch (Exception e) {
//			j.setSuccess(false);
//			j.setMsg("设置失败");
//			j.setObj(e);
//			e.printStackTrace();
//		}
//		
//		return j;
//	}
	
//	@RequestMapping({"/consumerreturn"})
//	@ResponseBody
//	public Json businessOrderRefundConsumerReturn(Long id, HttpSession session) {
//		Json j = new Json();
//		
//		try {
//			if (id == null) {
//				j.setSuccess(false);
//				j.setMsg("缺少参数");
//				j.setObj(null);
//				return j;
//			}
//			BusinessOrderRefund refund = businessOrderRefundServiceImpl.find(id);
//			if (refund == null) {
//				j.setSuccess(false);
//				j.setMsg("指定的订单退款记录不存在");
//				j.setObj(null);
//				return j;
//			}
//			if (refund.getState() != Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_RETURN_PRODUCT) {
//				j.setSuccess(false);
//				j.setMsg("当前退款订单未处于待消费者退货");
//				j.setObj(null);
//				return j;
//			}
//			refund.setState(Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_RETURN_INBOUND);
//			businessOrderRefundServiceImpl.save(refund);
//			j.setSuccess(true);
//			j.setMsg("设置成功");
//			j.setObj(refund);
//		} catch (Exception e) {
//			j.setSuccess(false);
//			j.setMsg("设置失败");
//			j.setObj(e);
//			e.printStackTrace();
//		}
//		
//		return j;
//	}
	@Resource(name = "orderTransactionServiceImpl")
	OrderTransactionService orderTransactionServiceImpl;
	
	@Resource(name = "pointrecordServiceImpl")
	PointrecordService pointrecordService;

	@Resource(name = "wechatAccountBalanceServiceImpl")
	private WechatAccountBalanceService wechatAccountBalanceService;
	@RequestMapping({"/finishrefund"})
	@ResponseBody
	public Json businessOrderRefundFinishRefund(Long refundid, HttpSession session) {
		Json j = new Json();
		
		try {
			if (refundid == null) {
				j.setSuccess(false);
				j.setMsg("缺少参数");
				j.setObj(null);
				return j;
			}
			BusinessOrderRefund refund = businessOrderRefundServiceImpl.find(refundid);
			if (refund == null) {
				j.setSuccess(false);
				j.setMsg("指定的订单退款记录不存在");
				j.setObj(null);
				return j;
			}
			if (refund.getState() != Constants.BUSINESS_ORDER_REFUND_STATUS_WAIT_FOR_REFUND_MONEY) {
				j.setSuccess(false);
				j.setMsg("当前退款订单未处于待退款状态");
				j.setObj(null);
				return j;
			}
			
			BusinessOrder order = refund.getBusinessOrder();
			WechatAccount account = order.getWechatAccount();
			//恢复账户余额并保存
			account.setTotalbalance(account.getTotalbalance().add(refund.getrRefundAmount()));
			wechatAccountServiceImpl.save(account);

			if(order.getBalanceMoney() != null && order.getBalanceMoney().compareTo(BigDecimal.ZERO) > 0){
				WechatAccountBalance wechatAccountBalance = new WechatAccountBalance();
				wechatAccountBalance.setWechatAccountId(order.getWechatAccount().getId());
				wechatAccountBalance.setType(WechatAccountBalance.WechatAccountBalanceType.refund);
				wechatAccountBalance.setCreateTime(new Date());
				wechatAccountBalance.setAmount(refund.getrRefundAmount());
				wechatAccountBalance.setSurplus(account.getTotalbalance());
				wechatAccountBalanceService.save(wechatAccountBalance);
			}
			
			//设置订单状态为完成退款
			order.setOrderState(Constants.BUSINESS_ORDER_STATUS_FINISH_REFUND);
			order.setCompleteTime(new Date());
			businessOrderServiceImpl.save(order);
			
			//设置退款订单状态为完成退款
			refund.setState(Constants.BUSINESS_ORDER_REFUND_STATUS_FINISH);
			refund.setReturnMoneyTime(new Date());
			businessOrderRefundServiceImpl.save(refund);
			
			//生成交易记录
			OrderTransaction transaction = new OrderTransaction();
			transaction.setBusinessOrder(order);
			transaction.setSerialNum(OrderTransactionSNGenerator.getSN(true));
			transaction.setWechatBalance(refund.getrRefundAmount());
			transaction.setPayAccount(order.getWechatAccount().getWechatOpenid());

			//微信支付
			transaction.setPayType(1);
			transaction.setPayment(refund.getqRefundAmount());
			//退款
			transaction.setPayFlow(2);
			transaction.setPayTime(order.getPayTime());
			orderTransactionServiceImpl.save(transaction);
			
			//订单完成退款，修改用户积分
			if(!businessOrderServiceImpl.havePromotions(order)) {
				//没有参加过优惠活动
				if(order.getShouldPayMoney().equals(order.getPayMoney())) {
					//如果本订单的全部用现金支付
					Integer money = order.getRefoundMoney().intValue();
					Integer changevalue = -(money/10);
					//满足条件，扣除用户积分，每10元扣除 1 积分，负数
					if(changevalue!=0) {
						pointrecordService.changeUserPoint(order.getWechatAccount().getId(),changevalue , "退货");
					}
				}
			}
			
			j.setSuccess(true);
			j.setMsg("设置成功");
			j.setObj(refund);
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("设置失败");
			j.setObj(e);
			e.printStackTrace();
		}
		
		return j;
	}
	
	
}
