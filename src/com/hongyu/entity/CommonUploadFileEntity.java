package com.hongyu.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grain.entity.BaseEntity;
@SuppressWarnings("serial")
@Entity
@Table(name = "hy_common_uploadfile")
public class CommonUploadFileEntity extends BaseEntity {
	public enum UploadTypeEnum {
		/** 供应商退押金文件模板 */
		gystuiyajin,
		
		/** 供应商退部分押金文件模板 */
		gystuibufenyajin,
		
		/** 门店退出模板 */
		mendiantuichu,
	}
	
	/** 文件模板类型 */
	private UploadTypeEnum type;
	
	/** 文件url */
	private String fileUrl;

	@JsonProperty
	@Column(name = "type", nullable = false)
	public UploadTypeEnum getType() {
		return type;
	}

	public void setType(UploadTypeEnum type) {
		this.type = type;
	}

	@JsonProperty
	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

}
