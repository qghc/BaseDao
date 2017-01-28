package com.match.sqlmodel.test;

import com.match.sqlmodel.BaseDao;
import com.match.sqlmodel.MBUtils;

public class BaseDaoTest {
	
	public static void main(String[] args) {
//		BaseDao bd = new BaseDao<TestBean>("test_bean", TestBean.class, new String[]{"id"});
		BaseDao bd = TestBean.createBaseDao();
		TestBean bean = new TestBean();
		bean.setId("123");
		bean.setName("test");
		bd.addObject(bean);
		System.out.println(MBUtils.generateCreateTable(null, TestBean.class,new String[]{"src_url"}));
	}
}
