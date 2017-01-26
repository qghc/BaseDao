package com.match.sqlmodel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import cn.itcast.jdbc.TxQueryRunner;
/**
 * 基于dbutils的万能的BaseDao
 * @author 亓根火柴
 * @date 2017-1-26
 * @param <T>
 */
public class BaseDao<T> {
	
	private QueryRunner qr = new TxQueryRunner();
	/**
	 * 表名
	 */
	private String table;
	/**
	 * 对象类型
	 */
	private Class cls;
	/**
	 * 主键(联合主键)
	 */
	private String[] primaryKeys;
	/**
	 * 隐藏默认构造器，强制初始化各参数
	 */
	private BaseDao(){};
	
	public BaseDao(String tableName,Class cls,String[] primaryKeys){
		if((tableName==null)||tableName.equals("")){
			table = getSqlName(cls.getSimpleName());
		}else{
			table = tableName;
		}
		this.cls = cls;
		this.primaryKeys = primaryKeys;
	}
	/**
	 * 添加对象到数据库中
	 * @param obj 对象
	 * @return 成功返回true
	 */
	public boolean addObject(Object obj){
		Map<String,Object> result = generateInsertParams(table,obj,cls);
		String sql = (String) result.get("sql");
		Object params[] = (Object[]) result.get("params");
		printLog(sql, params);
		try {
			qr.update(sql, params);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 根据主键从数据库中删除该对象
	 * @param obj 对象
	 * @return 删除成功返回true
	 */
	public boolean deleteObject(Object obj){
		Map<String,Object> result = generateDeleteParams(table,obj,cls);
		String sql = (String) result.get("sql");
		Object params[] = (Object[]) result.get("params");
		printLog(sql, params);
		try {
			qr.update(sql,params);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 根据主键修改数据库中的对象
	 * @param obj 修改对象
	 * @return 修改成功返回true
	 */
	public boolean editObject(Object obj){
		Map<String,Object> result = generateEditParams(table,obj,cls);
		String sql = (String) result.get("sql");
		Object params[] = (Object[]) result.get("params");
		printLog(sql, params);
		try {
			qr.update(sql, params);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 根据主键查询对象
	 * @param obj 对象
	 * @return 查询出的对象
	 */
	public T queryObject(Object obj) {
		Map<String,Object> result = generateQueryParams(table,obj,cls);
		String sql = (String) result.get("sql");
		Object params[] = (Object[]) result.get("params");
		printLog(sql, params);
		try {
			return qr.query(sql, new BeanHandler<T>(cls), params);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 查询所有对象
	 * @return 对象列表
	 */
	public List<T> findAll(){
		String sql = "select * from "+table;
		try {
			return qr.query(sql, new BeanListHandler<T>(cls));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 生成插入语句和参数
	 * @param table 表名
	 * @param obj 对象
	 * @param cls 类型
	 * @return 语句和参数
	 */
	private Map<String, Object> generateInsertParams(String table, Object obj, Class cls) {
		Map<String, Object> data = new HashMap<String, Object>();
		//生成sql语句
		String[] fields = getAllFields(cls);
		StringBuffer sql = new StringBuffer("insert into "+table+"(");
		StringBuffer val = new StringBuffer("values(");
		for(String temp : fields){
			sql.append(temp+",");
			val.append("?,");
		}
		sql.deleteCharAt(sql.length()-1);
		val.deleteCharAt(val.length()-1);
		sql.append(") ");
		val.append(")");
		data.put("sql", sql.append(val).toString());
		//生成参数
		Object values[] = new Object[fields.length];
		for(int i = 0;i < fields.length;i++){
			values[i] = getValues(fields[i],obj,cls);
		}
		data.put("params", values);
		return data ;
	}
	/**
	 * 生成删除语句和参数
	 * @param table 表名
	 * @param obj 对象
	 * @param cls 类型
	 * @return 语句和参数
	 */
	private Map<String, Object> generateDeleteParams(String table, Object obj,Class cls) {
		Map<String, Object> data = new HashMap<String, Object>();
		//生成sql语句和参数
		StringBuffer sql = new StringBuffer("delete from "+table+" where ");
		Object values[] = new Object[primaryKeys.length];
		for(int i = 0;i < primaryKeys.length;i++){
			sql.append(primaryKeys[i] + "=? and ");
			values[i] = getValues(primaryKeys[i],obj,cls);
		}
		sql = sql.delete(sql.length()-4, sql.length());
		data.put("sql", sql.toString());
		data.put("params", values);
		return data;
	}
	/**
	 * 生成修改语句和参数
	 * @param table 表名
	 * @param obj 对象
	 * @param cls 类型
	 * @return 语句和参数
	 */
	private Map<String, Object> generateEditParams(String table, Object obj,Class cls) {
		Map<String, Object> data = new HashMap<String, Object>();
		//生成sql语句
		String[] fields = getAllFields(cls);
		List<String> pks = Arrays.asList(primaryKeys);
		StringBuffer sql = new StringBuffer("update "+table+" set ");
		for(String temp : fields){
			if(!pks.contains(temp)){
				sql.append(temp+"=?,");
			}
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" where ");
		for(String temp : primaryKeys){
			sql.append(temp+"=? and ");
		}
		sql = sql.delete(sql.length()-4, sql.length());
		data.put("sql", sql.toString());
		//生成参数
		Object values[] = new Object[fields.length];
		int j = 0;
		for(int i = 0;i < fields.length;i++){
			if(!pks.contains(fields[i])){
				values[j] = getValues(fields[i],obj,cls);
				j++;
			}
		}
		for(String temp : primaryKeys){
			values[j] = getValues(temp,obj,cls);
			j++;
		}
		data.put("params", values);
		return data ;
	}
	/**
	 * 生成查询语句和参数
	 * @param table 表名
	 * @param obj 对象
	 * @param cls 类型
	 * @return 语句和参数
	 */
	private Map<String, Object> generateQueryParams(String table, Object obj,Class cls) {
		Map<String, Object> data = new HashMap<String, Object>();
		//生成sql语句和参数
		StringBuffer sql = new StringBuffer("select * from "+table+" where ");
		Object values[] = new Object[primaryKeys.length];
		for(int i = 0;i < primaryKeys.length;i++){
			sql.append(primaryKeys[i] + "=? and ");
			values[i] = getValues(primaryKeys[i],obj,cls);
		}
		sql = sql.delete(sql.length()-4, sql.length());
		data.put("sql", sql.toString());
		data.put("params", values);
		return data;
	}
	/**
	 * 根据set方法和Class.getFields()，获取该类的所有域，包括公共域和私有域
	 * @param cls 类型
	 * @return
	 */
	private String[] getAllFields(Class cls) {
		Field[] pubFields = cls.getFields();
		List<String> setFields = new ArrayList<String>();
		Method[] methods = cls.getMethods();
		for(Method method : methods){
			if(method.getName().startsWith("set")){
				String field = method.getName().substring(3).toLowerCase();
				setFields.add(field);
			}
		}
		for(Field temp:pubFields){
			if(!setFields.contains(temp.getName())){
				setFields.add(temp.getName());
			}
		}
		return setFields.toArray(new String[setFields.size()]);
	}
	/**
	 * 根据域名获取该对象域的值
	 * @param field 域名
	 * @param obj 对象
	 * @param cls 类型
	 * @return
	 */
	private Object getValues(String field, Object obj, Class cls) {
		Method[] methods = cls.getMethods();
		String firstChar = field.charAt(0)+"";
		firstChar = firstChar.toUpperCase();
		String methodName = "get"+firstChar+field.substring(1);
		for(Method method : methods){
			if(method.getName().equals(methodName)){
				try {
					return method.invoke(obj, null);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private String getSqlName(String name){
		if((name == null)||(name.equals("")))return null;
		name = (name.charAt(0)+"").toLowerCase()+name.substring(1);
		int index = 0;
		while((index = contains(name))!=-1){
			char c = name.charAt(index);
			name = name.replaceFirst(c+"", ("_"+c).toLowerCase());
		}
		return name;
	}
	private int contains(String str){
		for(int i=0;i<str.length();i++){
			char c = str.charAt(i);
			if(('A'<=c)&&(c<='Z')){
				return i;
			}
		}return -1;
	}
	private void printLog(String sql, Object[] params) {
//		System.out.print("sql = "+sql+"\nparams = ");
//		for(int i=0;i<params.length-1;i++){
//			System.out.print(params[i]+",");
//		}System.out.println(params[params.length-1]);
	}
}
