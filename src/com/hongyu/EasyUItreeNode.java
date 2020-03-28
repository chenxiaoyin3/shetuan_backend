package com.hongyu;

import java.util.List;

/**
 * EasyUI tree模型
 * 
 * @author 孙宇
 * 
 */
public class EasyUItreeNode implements java.io.Serializable,Comparable<EasyUItreeNode> {

	private static final long serialVersionUID = -2542255384078191646L;
	private String id;
	private String text;
	private String state = "open";// open,closed
	private boolean checked = false;
	private Object attributes;
	private List<EasyUItreeNode> children;
	private String iconCls;
	private String pid;
	private String url;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Object getAttributes() {
		return attributes;
	}

	public void setAttributes(Object attributes) {
		this.attributes = attributes;
	}

	public List<EasyUItreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<EasyUItreeNode> children) {
		this.children = children;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int compareTo(EasyUItreeNode o) {
		return Integer.parseInt(this.getId())-Integer.parseInt( o.getId());
	}

}
