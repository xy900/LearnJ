package com.cms.entity;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class User implements Serializable{
	private static final long serialVersionUID = -4899973939730634971L;
	
	private Integer id;
	private String userName;
	@JsonIgnore
	private String passwd;
	private Integer passCount;
	private Integer failCount;
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @return the passwd
	 */
	public String getPasswd() {
		return passwd;
	}
	/**
	 * @return the passCount
	 */
	public Integer getPassCount() {
		return passCount;
	}
	/**
	 * @return the failCount
	 */
	public Integer getFailCount() {
		return failCount;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @param passwd the passwd to set
	 */
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	/**
	 * @param passCount the passCount to set
	 */
	public void setPassCount(Integer passCount) {
		this.passCount = passCount;
	}
	/**
	 * @param failCount the failCount to set
	 */
	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}
	public User(Integer id, String userName, String passwd, Integer passCount, Integer failCount) {
		super();
		this.id = id;
		this.userName = userName;
		this.passwd = passwd;
		this.passCount = passCount;
		this.failCount = failCount;
	}
	public User() {
		super();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", passwd=" + passwd + ", passCount=" + passCount
				+ ", failCount=" + failCount + "]";
	}
}
