package com.remote.customer.model;

import java.util.Date;

import com.remote.common.model.BaseModel;

public class CustomerModel extends BaseModel{
	private String customerId;
	private String pwd;
	private String showName;
	private String trueName;
	private Date registerTime;
	
	
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getShowName() {
		return showName;
	}
	public void setShowName(String showName) {
		this.showName = showName;
	}
	public String getTrueName() {
		return trueName;
	}
	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}
	
	public Date getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}
	
	
	@Override
	public String toString() {
		return "CustomerVo [uuid=" + getUuid() + ", customerId=" + customerId + ", pwd=" + pwd + ", showName=" + showName
				+ ", trueName=" + trueName + ", registerTime=" + registerTime + "]";
	}
	
}
