package com.hongyu.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler",
})
@Table(name="hy_we_divide_proportion")
public class WeDivideProportion
  implements Serializable
{
  private Long id;
  private Integer proportionType;
  private BigDecimal proportion;
  private Date createTime;
  private Date endTime;
  private Boolean isValid;
  private HyAdmin operator;
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column(name="id", unique=true, nullable=false)
  public Long getId()
  {
    return this.id;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  @Column(name="proportion", precision=10)
  public BigDecimal getProportion()
  {
    return this.proportion;
  }
  
  public void setProportion(BigDecimal proportion)
  {
    this.proportion = proportion;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="create_time", length=19)
  public Date getCreateTime()
  {
    return this.createTime;
  }
  
  public void setCreateTime(Date createTime)
  {
    this.createTime = createTime;
  }
  
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="end_time", length=19)
  public Date getEndTime()
  {
    return this.endTime;
  }
  
  public void setEndTime(Date endTime)
  {
    this.endTime = endTime;
  }
  
  @Column(name="is_valid")
  public Boolean getIsValid()
  {
    return this.isValid;
  }
  
  public void setIsValid(Boolean isValid)
  {
    this.isValid = isValid;
  }
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="operator")
  public HyAdmin getOperator() {
	return operator;
}

public void setOperator(HyAdmin operator) {
	this.operator = operator;
}

@PrePersist
  public void setPrepersist() {
	  this.createTime = new Date();
	  this.setIsValid(true);
  }

public Integer getProportionType() {
	return proportionType;
}

public void setProportionType(Integer proportionType) {
	this.proportionType = proportionType;
}
}
