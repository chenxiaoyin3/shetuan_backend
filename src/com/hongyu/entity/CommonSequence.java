package com.hongyu.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 生成自增ID的Entity
 * @author guoxinze
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_common_sequence")
public class CommonSequence implements Serializable {
	public enum SequenceTypeEnum {
		/** 0.线路国内产品ID自增 */
		xianlugn,
		
		/** 1.线路出境产品ID自增 */
		xianlucj,
		
		/** 2.线路汽车产品ID自增 */
		xianluqc,
		
		/** 3.商贸订单ID自增 */
		businessOrderSuq,
		
		/** 4.票务部景区门票产品ID自增 */
		piaowubump,
		
		/**5.订单编号**/
		orderSn,
		
		/**6.导游自增id**/
		guideSn,

		/** 7.票务部酒店房间产品ID自增 */
		piaowubujdfj,
		
		/**8.特产编号**/
		specialtySn,
		
		/**9.采购单编号**/
		purchaseSn,

		/**10.门店认购门票产品id*/
		SubscribeTicket,
		
		/** 11.票务部酒加景产品ID自增 */
		piaowubujjj,
		
		/** 12.打款确认单编号*/
		supplierSettlement,
		
		/** 13.分公司分成确认单编号 **/
		profitShareConfirm,

		/** 14.分公司打款确认单编号*/
		branchSettlement,
		
		/** 15.商城销售电子券订单编号*/
		couponSaleOrderSn,
		
		/** 16.签证产品ID*/
		piaowuqianzheng,
		
		/** 17.门店充值订单编号*/
		mendianRecharge,
		
		/** 18.票务酒店产品pn*/
		piaowujiudianPn,
		
		/** 19.票务景区产品pn*/
		piaowujingquPn,
		
		/**20.分公司预充值网银充值订单sn*/
		bankBranchRecharge;
	}
	private Long id;
	private SequenceTypeEnum type;
	private Long value;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public SequenceTypeEnum getType() {
		return type;
	}
	public void setType(SequenceTypeEnum type) {
		this.type = type;
	}
	public Long getValue() {
		return value;
	}
	public void setValue(Long value) {
		this.value = value;
	}
	
}
