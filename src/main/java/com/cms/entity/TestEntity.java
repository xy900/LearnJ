package com.cms.entity;

import java.io.Serializable;
import java.util.List;

public class TestEntity implements Serializable{
	private static final long serialVersionUID = 8150240700018118346L;
	
	private Integer id;
	private String name;
	private Integer pid;
	
	private List<TestEntity> child;
	private TestEntity parent;
	
	public List<TestEntity> getChild() {
		return child;
	}
	public void setChild(List<TestEntity> child) {
		this.child = child;
	}
	public TestEntity getParent() {
		return parent;
	}
	public void setParent(TestEntity parent) {
		this.parent = parent;
	}
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
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
		return "TestEntity [id=" + id + ", name=" + name + ", pid=" + pid + ", parent=" + parent + ", child=" + child
				+ "]";
	}
}
