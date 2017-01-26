package com.match.sqlmodel;

public class BaseDaoTest {
	
	public static void main(String[] args) {
		BaseDao bd = new BaseDao<TestBean>("test_bean", TestBean.class, new String[]{"id"});
		TestBean bean = new TestBean();
		bean.setId("123");
		bean.setName("test");
		bd.addObject(bean);
		System.out.println(MBUtils.generateCreateTable(null, TestBean.class,new String[]{"src_url"}));
	}
}
