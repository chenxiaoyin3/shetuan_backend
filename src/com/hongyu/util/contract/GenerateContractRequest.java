package com.hongyu.util.contract;

public class GenerateContractRequest {
	/**
	 * 根据指定模板生成合同，成功则返回合同的下载和查看地址。失败则返回原因
	 * @param templateId	合同模板id
	 * @param contractId	合同编号
	 * @param docTitle		合同标题
	 * @param paramter_map	需要填如合同的标题(是json类型转化而来的)
	 * @param dynamic_table	动态表单。可以为空
	 * @return
	 */
	private String templateId;
	private String contractId;
	private String docTitle;
	private String paramter_map;
	private String dynamic_table;
	
	public GenerateContractRequest() {
		super();
	}
	/**
	 * 根据指定模板生成合同，成功则返回合同的下载和查看地址。失败则返回原因
	 * @param templateId	合同模板id
	 * @param contractId	合同编号
	 * @param docTitle		合同标题
	 * @param paramter_map	需要填如合同的标题(是json类型转化而来的)
	 * @param dynamic_table	动态表单。可以为空
	 * @return
	 */
	public GenerateContractRequest(String templateId, String contractId, String docTitle, String paramter_map,
			String dynamic_table) {
		super();
		this.templateId = templateId;
		this.contractId = contractId;
		this.docTitle = docTitle;
		this.paramter_map = paramter_map;
		this.dynamic_table = dynamic_table;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	public String getDocTitle() {
		return docTitle;
	}
	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}
	public String getParamter_map() {
		return paramter_map;
	}
	public void setParamter_map(String paramter_map) {
		this.paramter_map = paramter_map;
	}
	public String getDynamic_table() {
		return dynamic_table;
	}
	public void setDynamic_table(String dynamic_table) {
		this.dynamic_table = dynamic_table;
	}
	
}
