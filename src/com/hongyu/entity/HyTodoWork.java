package com.hongyu.entity;
// Generated 2017-12-24 21:20:19 by Hibernate Tools 3.6.0.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * HyTodoWork generated by hbm2java
 */
@Entity
@Table(name = "hy_todo_work")
public class HyTodoWork implements java.io.Serializable {

	private long id;
	private HyWorkflowModel hyWorkflowModel;
	private HyWorkflowStepModel hyWorkflowStepModel;
	private String tableName;
	private long tableId;
	private String stepName;
	private String url;
	private String workflowClassify;
	private Date createTime;
	private String workflowName;

	public HyTodoWork() {
	}

	public HyTodoWork(long id, HyWorkflowModel hyWorkflowModel, HyWorkflowStepModel hyWorkflowStepModel,
			String tableName, long tableId, String stepName, String url, String workflowClassify) {
		this.id = id;
		this.hyWorkflowModel = hyWorkflowModel;
		this.hyWorkflowStepModel = hyWorkflowStepModel;
		this.tableName = tableName;
		this.tableId = tableId;
		this.stepName = stepName;
		this.url = url;
		this.workflowClassify = workflowClassify;
	}

	public HyTodoWork(long id, HyWorkflowModel hyWorkflowModel, HyWorkflowStepModel hyWorkflowStepModel,
			String tableName, long tableId, String stepName, String url, String workflowClassify, Date createTime,
			String workflowName) {
		this.id = id;
		this.hyWorkflowModel = hyWorkflowModel;
		this.hyWorkflowStepModel = hyWorkflowStepModel;
		this.tableName = tableName;
		this.tableId = tableId;
		this.stepName = stepName;
		this.url = url;
		this.workflowClassify = workflowClassify;
		this.createTime = createTime;
		this.workflowName = workflowName;
	}

	@Id

	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "model_id", nullable = false)
	public HyWorkflowModel getHyWorkflowModel() {
		return this.hyWorkflowModel;
	}

	public void setHyWorkflowModel(HyWorkflowModel hyWorkflowModel) {
		this.hyWorkflowModel = hyWorkflowModel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "step_id", nullable = false)
	public HyWorkflowStepModel getHyWorkflowStepModel() {
		return this.hyWorkflowStepModel;
	}

	public void setHyWorkflowStepModel(HyWorkflowStepModel hyWorkflowStepModel) {
		this.hyWorkflowStepModel = hyWorkflowStepModel;
	}

	@Column(name = "table_name", nullable = false)
	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Column(name = "table_id", nullable = false)
	public long getTableId() {
		return this.tableId;
	}

	public void setTableId(long tableId) {
		this.tableId = tableId;
	}

	@Column(name = "step_name", nullable = false)
	public String getStepName() {
		return this.stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	@Column(name = "url", nullable = false)
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "workflow_classify", nullable = false)
	public String getWorkflowClassify() {
		return this.workflowClassify;
	}

	public void setWorkflowClassify(String workflowClassify) {
		this.workflowClassify = workflowClassify;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time", length = 19)
	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "workflow_name")
	public String getWorkflowName() {
		return this.workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

}