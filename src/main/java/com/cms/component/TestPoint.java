package com.cms.component;

import org.springframework.stereotype.Component;

@Component
public class TestPoint {
	private Integer id;
	private String name;
	
	public TestPoint() {
		this.id = 909974;
		this.name = "xieyu";
	}
	
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
		return "[id=" + this.id + ", name=" + name + "]";
	}
}
