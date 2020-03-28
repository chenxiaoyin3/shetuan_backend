//package com.hongyu.task.impl;
//
//import com.hongyu.task.Processor;
//import com.hongyu.task.TaskTimer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
//
//@Component
//public class TaskTimeImpl implements TaskTimer {
//	
//	@Resource(name="serialNumberProcessor")
//	Processor serialNumProcessor;
//	
//	@Resource(name="orderitemDivideProcessor")
//	Processor orderitemDivideProcessor;
//	
//	@Resource(name="updateOrderProcessor")
//	Processor updateOrderProcessor;
//	
//	@Resource(name="weBusinessDivideDailyReportProcessor")
//	Processor weBusinessDivideDailyReportProcessor;
//	
//	@Resource(name="timerManagement")
//	TimerManagement management;
//	
//	@Resource(name="timerTaskExecutor")
//	TimerTaskExecutor executor;
//	
//	@Resource(name="promotionProcessor")
//	Processor promotionProcessor;
//	
//	@Resource(name = "couponProcessor")
//	Processor couponProcessor;
//	
//	@Resource(name = "payServicerProcessor")
//	Processor payServicerProcessor;
//	
//	@Resource(name = "valueAddedProcessor")
//	Processor valueAddedProcessor;
//	
//	@Resource(name="productPutOnAndPutOffProcessor")
//	Processor productPutOnAndPutOffProcessor;
//	
//	@Resource(name="providerBalanceProcessor")
//	Processor providerBalanceProcessor;
//	
//	@Resource(name="sequenceGeneratorProcessor")
//	Processor sequenceGeneratorProcessor;
//	
//	@Resource(name="providerBalanceItemProcessor")
//	Processor providerBalanceItemProcessor;
//	
//	@Resource(name="weBusinessDivideBalanceProcessor")
//	Processor weBusinessDivideBalanceProcessor;
//	
//	@Resource(name = "groupProcessor")
//	Processor groupProcessor;
//	
//	@Resource(name="storeManagementFeeProcessor")
//	Processor storeManagementFeeProcessor;
//	
//	@Resource(name="profitShareConfirmProcessor")
//	Processor profitShareConfirmProcessor;
//
//	@Resource(name="guiderPeriodicSettlementProcessor")
//	Processor guiderPeriodicSettlementProcessor;
//
//
//	@Resource(name = "jtInsureProcessor")
//	Processor jtInsureProcessor;
//	
//	@Resource(name = "njtInsureProcessor")
//	Processor njtInsureProcessor;
//	
//	@Resource(name = "finishedGroupItemOrderProcessor")
//	Processor finishedGroupItemOrderProcessor;
//	
//	@Resource(name = "vipBirthdayProcessor")
//	Processor vipBirthdayProcessor;
//	
//	@Resource(name = "vipUserProcessor")
//	Processor vipUserProcessor;
//	
//	@Resource(name = "storeGuideSettlementProcessor")
//	Processor storeGuideSettlementProcessor;
//	
//	@Resource(name = "hetongProcessor")
//	Processor hetongProcessor;
//
//	@Resource(name = "guideAssignmentViewProcessor")
//	Processor guideAssignmentViewProcessor;
//	
//	@Resource(name = "providerStatisProcessor")
//	Processor providerStatisProcessor;
//	
//	@Resource(name = "rebateProcessor")
//	Processor rebateProcessor;
//	
//	@Resource(name = "cancelOrderProcessor")
//	Processor cancelOrderProcessor;
//	
//	@Resource(name = "groupPlaceholderProcessor")
//	GroupPlaceholderProcessor groupPlaceholderProcessor;
//	
//	@Resource(name = "adminitratorUploadFileProcessor")
//	Processor adminitratorUploadFileProcessor;
//	
//	@Resource(name = "monthShouldPayStatisticProcessor")
//	Processor monthShouldPayStatisticProcessor;
//	
//	@Resource(name = "insuranceOrderRefundAutoRejectProcessor")
//	Processor insuranceOrderRefundAutoRejectProcessor;
//	
//	@Resource(name = "insuranceOrderAutoCancelProcessor")
//	Processor insuranceOrderAutoCancelProcessor;
//
//	@Autowired
//	ValidProductProcessor validProductProcessor;
//	
//	
//	@Scheduled(cron = "0 0 0 * * ?")
//	public void scheduledTask1() {
//		serialNumProcessor.process();
//	}
//	
////	@Scheduled(cron = "*/5 * * * * ?")
////	public void scheduledTask2() {
////		updateOrderProcessor.process();
////	}
//	//优惠活动，一小时扫一次
////	@Scheduled(cron="0 */3 * * * ?")
//	@Scheduled(cron = "0 0 * * * ?")
//	public void scheduledTask3() {
//		System.out.println("scheduledTask3");
//		promotionProcessor.process();
//	}
//	
//
////	@Scheduled(cron = "*/60 * * * * ?")
////	public void scheduledTask4() {
////		TimerTask task = null;
////		while ((task = management.removeTaskIfExpired()) != null) {
////			executor.addTimerTask(task);
////			task = null;
////		}
////	}
//	
////	@Scheduled(cron = "0 */2 * * * ?")
//	@Scheduled(cron = "0 30 2 * * ?")
//	public void scheduledTask4() {
//		providerBalanceItemProcessor.process();
//		providerBalanceProcessor.process();
//	}
//	
//	
//	@Scheduled(cron = "0 0 1 * * ?")
//	public void scheduledTask5() {
//		updateOrderProcessor.process();
//		providerStatisProcessor.process();
//	}
//	
//	@Scheduled(cron = "0 15 1 * * ?")
//	public void scheduledTaskGuideAssignmentView() {
//		guideAssignmentViewProcessor.process();
//	}
//	
////	@Scheduled(cron = "0 */2 * * * ?")
//	@Scheduled(cron = "0 30 1 * * ?")
//	public void scheduledTask6() {
//		orderitemDivideProcessor.process();
//		weBusinessDivideDailyReportProcessor.process();
//	}
//	
////	@Scheduled(cron = "0 0 * * * ?")
////	public void weBusinessBalanceTask() {
////		weBusinessDivideBalanceProcessor.process();
////	}
//	
//	@Scheduled(cron = "0 0 2 * * ?")
//	public void scheduledTask7() {
////		weBusinessDivideDailyReportProcessor.process();
//		vipUserProcessor.process();
//	}
//	
//	@Scheduled(cron = "0 0 * * * ?")
//	public void scheduledTask8() {
//		productPutOnAndPutOffProcessor.process();
//	}
//	
//	/** 电子券过期 扫描 */
//	@Scheduled(cron = "0 0 3 * * ?")
//	public void scheduledTask10(){
//		couponProcessor.process();
//	}
//	
//	//每天5点定时生成打款单
//	@Scheduled(cron = "0 0 5 * * ?")
//	public void scheduledPayServer(){
//		payServicerProcessor.process();
//		System.out.println("打款单扫描完成");
//	}
//	
//	@Scheduled(cron = "0 0 4 * * ?")
//	public void scheduledValueAdded(){
//		valueAddedProcessor.process();
//	}
//	
//	/** 线路产品ID自增扫描器 每天早晨0点重置*/
//	@Scheduled(cron = "0 0 0 * * ?")
//	public void scheduledTask9() {
//		sequenceGeneratorProcessor.process();
//	}
//	
//
//	//团状态改变
//	@Scheduled(cron = "0 30 5 * * ?")
//	public void scheduledTask11() {
////		System.out.println("扫描团状态");
//		groupProcessor.process();
//	}
//
//	@Scheduled(cron="0 0 0 * * ?")
//	public void storeManagementFeeProcessor(){
//		storeManagementFeeProcessor.process();
//		
//	}
//	
//	//每月一号一点五十触发
//	@Scheduled(cron="0 50 1 1 * ?")
//	public void ProfitShareConfirmProcessor() {
//		profitShareConfirmProcessor.process();
//	}
//	
//	
//	/** 周期结算生成 - */ 
//	@Scheduled(cron="0 0 4 5 * ?")   //每月5号4点
//	public void guiderPeriodicSettlementProcessor(){
//		guiderPeriodicSettlementProcessor.process();
//	}
//	
//	/** 自动投保到江泰 - */ 
//	@Scheduled(cron="0 0 22 * * ?")   //每天晚上10:00自动投保
//	public void jtInsureProcessor(){
//		System.out.println("江泰投保启动");
//		//jtInsureProcessor.process();
//		njtInsureProcessor.process();
//	}
//	
//	/**统计当天回团的团的所有历史订单信息，写入到finishedGroupItemOrder表中 */ 
//	@Scheduled(cron="0 0 22 * * ?")   //每天晚上10:00统计？
//	public void finishedGroupItemOrderProcessor(){
//		finishedGroupItemOrderProcessor.process();
//	}
//	
//	/**每天上午9点扫描会员生日*/
//	@Scheduled(cron="0 0 9 * * ?")
//	public void vipBirthdayProcessor() {
//		vipBirthdayProcessor.process();
//	}
//	
//	/** 生成导游结算详情 - */ 
//	@Scheduled(cron="0 0 23 * * ?")   //每天23点扫描
//	public void storeGuideSettlementProcessor(){
//		storeGuideSettlementProcessor.process();
//	}
//	
//	/** 合同过期扫描 - */ 
//	//@Scheduled(cron="0 30 0 * * ?")   //每天0点30分扫描
//	@Scheduled(cron="0 0 23 * * ?")  
//	public void contractProcessor(){
//		System.out.println("合同扫描启动");
//		hetongProcessor.process();
//	}
//	
//	/** 返利定时器  */
//	@Scheduled(cron="0 0 1 * * ?")   //每天凌晨1点扫描
//	public void rebateProcessor(){
//		rebateProcessor.process();
//	}
//	
//	/** 订单取消定时器 */
//	@Scheduled(cron="1 */5 * * * ?")	//每5分钟扫描一次
//	public void cancelOrderProcessor(){
//		cancelOrderProcessor.process();
//	}
//	
//	/**占位清除定时器*/
//	@Scheduled(cron = "0 0 * * * ?")
//	public void deleteGroupPlaceholder(){
//		groupPlaceholderProcessor.process();
//	}
//	
//	@Resource(name = "providerCancelOrderProcessor")
//	private Processor providerCancelOrderProcessor;
//	
//	/** 订单供应商未确认取消订单定时器 */
//	@Scheduled(cron = "2 */10 * * * ?")	//每10分钟扫描一次
//	public void providerCancelOrderProcessor() {
//		providerCancelOrderProcessor.process();
//		
//	}
//
//	/** 每个月末扫描一次 */
//	@Scheduled(cron = "0 0 23 28 * ?")
//	public void validProductProcessor(){
//		validProductProcessor.process();
//	}
//	
//	/** 行政部上传的文件的有效期定时器*/
//	@Scheduled(cron = "0 0 1 * * ?")	//每天凌晨1点扫描
//	public void validFileProcessor() {
//		adminitratorUploadFileProcessor.process();
//	}
//	
//	/** 应付款月报表定时器  **/
//	@Scheduled(cron = "0 0 1 * * ?")	//每天凌晨1点扫描
//	public void monthShouldPayStatisticProcessor() {
//		monthShouldPayStatisticProcessor.process();
//	}
//	
//	/** 保险退款自动驳回定时器 */
//	@Scheduled(cron="1 */10 * * * ?")	//每10分钟扫描一次
//	public void insuranceOrderRefundAutoRejectProcessor(){
//		insuranceOrderRefundAutoRejectProcessor.process();
//		
//	}
//	
//	/** 保险订单自动取消定时器 */
//	@Scheduled(cron="5 */10 * * * ?")	//每10分钟扫描一次
//	public void insuranceOrderAutoCancelProcessor() {
//		insuranceOrderAutoCancelProcessor.process();
//	}
//}
//
