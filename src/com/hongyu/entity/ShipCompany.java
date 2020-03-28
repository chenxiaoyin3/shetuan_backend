package com.hongyu.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"
})
@Table(name="hy_ship_company")
public class ShipCompany implements Serializable {
    private Long id;
    private String shipCompany;
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="id", unique=true, nullable=false)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="ship_company")
	public String getShipCompany() {
		return shipCompany;
	}
	public void setShipCompany(String shipCompany) {
		this.shipCompany = shipCompany;
	}
}
