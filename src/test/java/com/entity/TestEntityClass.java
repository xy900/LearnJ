package com.entity;

import java.util.Date;

public class TestEntityClass {
	private String id;
	private String name;
	private Integer no;
	private Date currTime;
	
	public TestEntityClass() {
		this.id = "1";
		this.name = "haha";
		this.no = 909974;
		this.currTime = new Date();
	}
	
	public TestEntityClass(String id, String name, Integer no, Date currTime) {
		this.id = id;
		this.name = name;
		this.no = no;
		this.currTime = currTime;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getNo() {
		return no;
	}
	public void setNo(Integer no) {
		this.no = no;
	}
	public Date getCurrTime() {
		return currTime;
	}
	public void setCurrTime(Date currTime) {
		this.currTime = currTime;
	}
	
	@Override
	public String toString() {
		return "[id:" + id + ", name:" + name + ", no:" + no + ", currTime:" + currTime + "]";
	}
}
