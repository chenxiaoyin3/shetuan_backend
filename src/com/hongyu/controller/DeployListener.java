/**
* 现有流程的资源已经部署到数据库 无需反复部署流程
* */
//package com.hongyu.controller;
//
//import javax.annotation.Resource;
//
//import org.activiti.engine.RepositoryService;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.stereotype.Service;
//
///** 在项目启动时进行流程部署 */
//@Service
//public class DeployListener implements ApplicationListener<ContextRefreshedEvent> {
//	@Resource
//	private RepositoryService repositoryService;
//
//	@Override
//	public void onApplicationEvent(ContextRefreshedEvent event) {
//		// root application context 没有parent，他就是老大.
//		if (event.getApplicationContext().getParent() == null) {
//			System.out.println("项目启动 流程部署启动");
//			try {
//				//
//				repositoryService.createDeployment().name("storeRegistration")
//						.addClasspathResource("com/hongyu/diagram/storeRegistration.bpmn")
//						.addClasspathResource("com/hongyu/diagram/storeRegistration.png").deploy();
//				
//				// 江泰预充款
//				repositoryService.createDeployment().name("PaymentJiangtai")
//						.addClasspathResource("com/hongyu/diagram/PaymentJiangtai.bpmn")
//						.addClasspathResource("com/hongyu/diagram/PaymentJiangtai.png").deploy();
//				
//				// 门店交管理费
//				repositoryService.createDeployment().name("jiaoguanlifei")
//						.addClasspathResource("com/hongyu/diagram/jiaoguanlifei.bpmn")
//						.addClasspathResource("com/hongyu/diagram/jiaoguanlifei.png").deploy();
//
//				// 门店交押金
//				repositoryService.createDeployment().name("jiaoyajin")
//						.addClasspathResource("com/hongyu/diagram/jiaoyajin.bpmn")
//						.addClasspathResource("com/hongyu/diagram/jiaoyajin.png").deploy();
//
//				// 门店退出
//				repositoryService.createDeployment().name("storeLogout")
//						.addClasspathResource("com/hongyu/diagram/storeLogout.bpmn")
//						.addClasspathResource("com/hongyu/diagram/storeLogout.png").deploy();
//
//				repositoryService.createDeployment().name("storeRenew")
//						.addClasspathResource("com/hongyu/diagram/storeRenew.bpmn")
//						.addClasspathResource("com/hongyu/diagram/storeRenew.png").deploy();
//
//				repositoryService.createDeployment().name("商城商品入库流程")
//						.addClasspathResource("com/hongyu/diagram/StockIn.bpmn")
//						.addClasspathResource("com/hongyu/diagram/StockIn.png").deploy();
//				
//				// 采购部新建供应商审核流程
//				repositoryService.createDeployment().name("caigoubugysxinjian")
//						.addClasspathResource("com/hongyu/diagram/cgbgongyingshang.bpmn")
//						.addClasspathResource("com/hongyu/diagram/cgbgongyingshang.png").deploy();
//
//				// 采购部供应商交押金审核流程
//				repositoryService.createDeployment().name("gongyingshangjiaoyajin")
//						.addClasspathResource("com/hongyu/diagram/cgbgysjiaoyajin.bpmn")
//						.addClasspathResource("com/hongyu/diagram/cgbgysjiaoyajin.png").deploy();
//
//				// 采购部供应商退押金审核流程
//				repositoryService.createDeployment().name("gongyingshangtuiyajin")
//						.addClasspathResource("com/hongyu/diagram/cgbgystuiyajin.bpmn")
//						.addClasspathResource("com/hongyu/diagram/cgbgystuiyajin.png").deploy();
//
//				// 采购部供应商变更扣点审核流程
//				repositoryService.createDeployment().name("biangengkoudian")
//						.addClasspathResource("com/hongyu/diagram/Biangengkoudian.bpmn")
//						.addClasspathResource("com/hongyu/diagram/Biangengkoudian.png").deploy();
//
//				// 采购部供应商续签审核流程
//				repositoryService.createDeployment().name("hetongxuqian")
//						.addClasspathResource("com/hongyu/diagram/Xuqian.bpmn")
//						.addClasspathResource("com/hongyu/diagram/Xuqian.png").deploy();
//
//				// 采购部供应商退还部分押金流程
//				repositoryService.createDeployment().name("团变更扣点")
//						.addClasspathResource("com/hongyu/diagram/GroupBiankoudian.bpmn")
//						.addClasspathResource("com/hongyu/diagram/GroupBiankoudian.png").deploy();
//				
//				// 团变更扣点流程
//				repositoryService.createDeployment().name("tuihuanbufenyajin")
//						.addClasspathResource("com/hongyu/diagram/Tuibufenyajin.bpmn")
//						.addClasspathResource("com/hongyu/diagram/Tuibufenyajin.png").deploy();
//
//				// 票务部分销商结算审核流程
//				repositoryService.createDeployment().name("distributorSettlement")
//						.addClasspathResource("com/hongyu/diagram/distributorSettlement.bpmn")
//						.addClasspathResource("com/hongyu/diagram/distributorSettlement.png").deploy();
//
//				// 票务部分销商预充值审核流程
//				repositoryService.createDeployment().name("distributorPrecharge")
//						.addClasspathResource("com/hongyu/diagram/distributorPrecharge.bpmn")
//						.addClasspathResource("com/hongyu/diagram/distributorPrecharge.png").deploy();
//				
//				// 给景区(门票分销商)付款审核流程
//				repositoryService.createDeployment().name("给景区(门票分销商)付款审核流程")
//						.addClasspathResource("com/hongyu/diagram/TicketPay.bpmn")
//						.addClasspathResource("com/hongyu/diagram/TicketPay.png").deploy();
//				
//				// 门店充值审核流程
//				repositoryService.createDeployment().name("门店充值审核流程")
//						.addClasspathResource("com/hongyu/diagram/StoreRecharge.bpmn")
//						.addClasspathResource("com/hongyu/diagram/StoreRecharge.png").deploy();
//				
//				// 供应商上产品审核流程
//				repositoryService.createDeployment().name("供应商上产品流程")
//						.addClasspathResource("com/hongyu/diagram/Xianlushenhe.bpmn")
//						.addClasspathResource("com/hongyu/diagram/Xianlushenhe.png").deploy();
//
//				// 票务部门票价格审核流程
//				repositoryService.createDeployment().name("票务部门票价格审核流程")
//						.addClasspathResource("com/hongyu/diagram/sceneticketPrice.bpmn")
//						.addClasspathResource("com/hongyu/diagram/sceneticketPrice.png").deploy();
//
//				// 票务部酒店房间价格审核流程
//				repositoryService.createDeployment().name("票务部酒店房间价格审核流程")
//						.addClasspathResource("com/hongyu/diagram/hotelRoomPrice.bpmn")
//						.addClasspathResource("com/hongyu/diagram/hotelRoomPrice.png").deploy();
//				
//				// 票务部酒加景房间价格审核流程
//				repositoryService.createDeployment().name("票务部酒加景房间价格审核流程")
//						.addClasspathResource("com/hongyu/diagram/hotelandsceneRoomPrice.bpmn")
//						.addClasspathResource("com/hongyu/diagram/hotelandsceneRoomPrice.png").deploy();
//
//				// 门店认购门票上架审核流程
//				repositoryService.createDeployment().name("门店认购门票审核流程")
//						.addClasspathResource("com/hongyu/diagram/subscribeTicket.bpmn")
//						.addClasspathResource("com/hongyu/diagram/subscribeTicket.png").deploy();
//				
//				// 门店增值业务付款审核流程
//				repositoryService.createDeployment().name("门店增值业务付款审核流程")
//						.addClasspathResource("com/hongyu/diagram/valueAdded.bpmn")
//						.addClasspathResource("com/hongyu/diagram/valueAdded.png").deploy();
//				
//				// (总公司、分公司)预付款审核流程
//				repositoryService.createDeployment().name("预付款审核流程")
//						.addClasspathResource("com/hongyu/diagram/prePay.bpmn")
//						.addClasspathResource("com/hongyu/diagram/prePay.png").deploy();
//				
//				// (总公司)付尾款审核流程
//				repositoryService.createDeployment().name("总公司付尾款审核流程")
//						.addClasspathResource("com/hongyu/diagram/balanceDueCompany.bpmn")
//						.addClasspathResource("com/hongyu/diagram/balanceDueCompany.png").deploy();
//
//				// (分公司)付尾款审核流程
//				repositoryService.createDeployment().name("分公司付尾款审核流程")
//						.addClasspathResource("com/hongyu/diagram/balanceDueBranch.bpmn")
//						.addClasspathResource("com/hongyu/diagram/balanceDueBranch.png").deploy();
//				
//				// 供应商驳回订单审核流程
//				repositoryService.createDeployment().name("供应商驳回订单审核流程")
//						.addClasspathResource("com/hongyu/diagram/supplierDismissOrder.bpmn")
//						.addClasspathResource("com/hongyu/diagram/supplierDismissOrder.png").deploy();
//
//				// 非虹宇门店创建审核流程
//				repositoryService.createDeployment().name("非虹宇门店创建审核流程")
//						.addClasspathResource("com/hongyu/diagram/storeFhynew.bpmn")
//						.addClasspathResource("com/hongyu/diagram/storeFhynew.png").deploy();
//				
//				// 给非虹宇门店授信审核流程
//				repositoryService.createDeployment().name("非虹宇门店授信审核流程")
//						.addClasspathResource("com/hongyu/diagram/creditFhy.bpmn")
//						.addClasspathResource("com/hongyu/diagram/creditFhy.png").deploy();
//				
//				// 计调提交促销审核流程
//				repositoryService.createDeployment().name("计调促销")
//						.addClasspathResource("com/hongyu/diagram/LinePromotion.bpmn")
//						.addClasspathResource("com/hongyu/diagram/LinePromotion.png").deploy();
//				
//				// 甩尾单审核流程
//				repositoryService.createDeployment().name("甩尾单流程")
//						.addClasspathResource("com/hongyu/diagram/SwdProcess.bpmn")
//						.addClasspathResource("com/hongyu/diagram/SwdProcess.png").deploy();
//				
				// 向供应商提前打款审核流程
//				repositoryService.createDeployment().name("向供应商提前打款审核流程")
//						.addClasspathResource("com/hongyu/diagram/payServicerPre.bpmn")
//						.addClasspathResource("com/hongyu/diagram/payServicerPre.png").deploy();
//
//				// 消团审核流程
//				repositoryService.createDeployment().name("消团审核流程")
//						.addClasspathResource("com/hongyu/diagram/xiaotuan.bpmn")
//						.addClasspathResource("com/hongyu/diagram/xiaotuan.png").deploy();
//				
//				// T+N打款审核流程
//				repositoryService.createDeployment().name("T+N打款审核流程")
//						.addClasspathResource("com/hongyu/diagram/payServicerPreTN.bpmn")
//						.addClasspathResource("com/hongyu/diagram/payServicerPreTN.png").deploy();
//				
//				// 计调报账审核流程
//				repositoryService.createDeployment().name("计调报账审核流程")
//						.addClasspathResource("com/hongyu/diagram/Regulate.bpmn")
//						.addClasspathResource("com/hongyu/diagram/Regulate.png").deploy();
//				
//				// 门店退团审核流程
//				repositoryService.createDeployment().name("门店退团审核流程")
//						.addClasspathResource("com/hongyu/diagram/storeTuiTuan.bpmn")
//						.addClasspathResource("com/hongyu/diagram/storeTuiTuan.png").deploy();
//				
//				// 门店售后审核流程
//				repositoryService.createDeployment().name("门店售后审核流程")
//						.addClasspathResource("com/hongyu/diagram/storeShouHou.bpmn")
//						.addClasspathResource("com/hongyu/diagram/storeShouHou.png").deploy();
//						
//				// 分公司团结算审核流程
//				repositoryService.createDeployment().name("分公司团结算审核流程")
//						.addClasspathResource("com/hongyu/diagram/branchsettle.bpmn")
//						.addClasspathResource("com/hongyu/diagram/branchsettle.png").deploy();
//				// 离职管理审核流程
//				repositoryService.createDeployment().name("离职管理审核流程")
//						.addClasspathResource("com/hongyu/diagram/dimissionProcess.bpmn")
//						.addClasspathResource("com/hongyu/diagram/dimissionProcess.png").deploy();
//				
//				// 签证价格审核流程
//				repositoryService.createDeployment().name("签证价格审核流程")
//						.addClasspathResource("com/hongyu/diagram/visaPrice.bpmn")
//						.addClasspathResource("com/hongyu/diagram/visaPrice.png").deploy();
//				
//				// 分公司充值审核流程
//				repositoryService.createDeployment().name("分公司充值审核流程")
//				.addClasspathResource("com/hongyu/diagram/BranchRecharge.bpmn")
//				.addClasspathResource("com/hongyu/diagram/BranchRecharge.png").deploy();
//				System.out.println("项目启动 流程部署完成");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//}
