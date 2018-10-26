package com.cms.entity;

import java.io.Serializable;

public class TestEntity implements Serializable{
	private static final long serialVersionUID = 8150240700018118346L;
	
	private Integer id;
	private String name;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "[id=" + id + ", name=" + name + "]";
	}
}
