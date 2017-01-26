package com.match.sqlmodel;

public class TestBean {
	private String title;
	private int count;
	private String click;
	private String len;
	private String id;
	private String name;
	private String child;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getClick() {
		return click;
	}
	public void setClick(String click) {
		this.click = click;
	}
	public String getLen() {
		return len;
	}
	public void setLen(String len) {
		this.len = len;
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
	public String getChild() {
		return child;
	}
	public void setChild(String child) {
		this.child = child;
	}
	@Override
	public String toString() {
		return "TestBean [title=" + title + ", count=" + count + ", click="
				+ click + ", len=" + len + ", id=" + id + ", name=" + name
				+ ", child=" + child + "]";
	}
}
