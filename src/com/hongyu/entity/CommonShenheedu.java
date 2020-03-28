package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
@Table(name="hy_common_shenheedu")
public class CommonShenheedu implements Serializable {
	
	public enum Eduleixing {
		/** 0供应商退押金副总限额审核 */
		tuiyajinfuzong,
		/** 1供应商退部分押金副总限额审核 */
		tuibufenyajinfuzong,
		/** 2国内产品价格比例 */
		guoneijiagebili,
		
		/**3出境产品价格比例*/
		chujingjiagebili,
		
		/**4汽车游价格比例*/
		qichejiagebili,
		
		/**5票务产品价格比例*/
		piaowujiagebili,
		
		/**6 分公司产品中心申请付尾款副总限额*/
		balanceDueBranchLimit,
		
		/**7 总公司产品中心申请付尾款副总限额*/
		balanceDueCompanyLimit,
		
		/**8 门店增值业务副总限额*/
		valueAddedLimit,
		
		/**9 预付款副总限额*/
		prePayLimit,
		
		/**10 向外部供应商打款限额*/
		payServicerLimit,
		
		/**11 消团品控审核限额*/
		xiaotuanLimit,
		
		/** 12 报账审核额度 （副总）*/
		regulateLimit,
		
		/** 13 门店退团审核额度 */
		storeTuiTuanLimit,
		
		/** 14 门店售后审核额度 */
		storeShouHouLimit,
		
		/** 15 分公司团结算市场部副总审核额度 */
	    branchsettleLimit,
	    
	    /** 16门店充值额度 */
		storeChongZhiLimit,
		
		/** 17门店提现额度 */
	    storeTiXianLimit,
	    
	    /** 18占个位置，谁的自己改掉 */
	    zhanwei1,
	    /** 19占个位置，谁的自己改掉*/
	    zhanwei2,
	    
	    /** 20江泰预充值副总审核限额 */
	    fzjiangtai,
	    
	    /** 21门店退出副总审核限额 */
	    storeLogout,
	    
	    /** 22其他服务费*/
	    elseFee,
	    
	    /** 23预付款分公司产品中心限额审核*/
	    prepayBranch,
	    /** 24预付款总公司产品中心限额审核*/
	    prepayCompany,
   
	    /**25计调报账产品中心经理限额审核*/
	    regulateJingli,
	   
	}
	
	private Long id;
	
	/** 限额审核额度 */
	@Digits(integer=10, fraction=2)
	private BigDecimal money;

	/** 额度的类型，是哪个流程图中的 */
	private Eduleixing eduleixing;
	
	private Date createTime;
	
	private Date modifyTime;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Eduleixing getEduleixing() {
		return eduleixing;
	}

	public void setEduleixing(Eduleixing eduleixing) {
		this.eduleixing = eduleixing;
	}
	
	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	@PrePersist
	public void prePersist() {
		Date time = new Date();
		this.createTime = time;
		this.modifyTime = time;
	}
	
	@PreUpdate
	public void preUpdate() {
		this.modifyTime = new Date();
	}
}
