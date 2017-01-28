package com.match.sqlmodel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * MySql & Bean Utils
 * @author 亓根火柴
 * @date 2017-1-27
 */
public class MBUtils {
	/**
	 * 简易地生成数据表创建语句
	 * @param table
	 * @param cls
	 * @param primaryKeys
	 * @return
	 */
	public static String generateCreateTable(String table,Class cls,String[] primaryKeys){
		StringBuffer sql = new StringBuffer();
		List<String> pks = Arrays.asList(primaryKeys);
		if((table == null)||table.equals("")){
			table = getSqlName(cls.getSimpleName());
		}
		sql.append("CREATE TABLE "+table+"(\n");
		String[] fields = getAllFields(cls);
		for(String field:fields){
			sql.append("\t"+getSqlName(field)+" ");
			String type = getFieldType(field,cls);
			if(pks.contains(field)){
				sql.append(type+" NOT NULL,\n");
			}else{
				sql.append(type+",\n");
			}
		}
		sql.append("\tPRIMARY KEY (");
		for(String pk:pks){
			sql.append(pk+",");
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(")\n) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;");
		return sql.toString();
	}

	/**
	 * 根据set方法和Class.getFields()，获取该类的所有域，包括公共域和私有域
	 * @param cls 类型
	 * @return
	 */
	public static String[] getAllFields(Class cls) {
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
	public Object getValues(String field, Object obj, Class cls) {
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
	/**
	 * 获取域的类型和大小
	 * @param field
	 * @param cls
	 * @return
	 */
	private static String getFieldType(String field,Class cls) {
		Method[] methods = cls.getMethods();
		String methodName = "get"+getFirstUpperString(field);
		for(Method method:methods){
			if(method.getName().equals(methodName)){
				Class clss = method.getReturnType();
				String typeName = clss.getSimpleName();
				if(typeName.equals("String")){
					typeName = "varchar(255)";
				}else if(typeName.equals("Date")){
					typeName = "datatime()";
				}else if(typeName.equals("int")){
					typeName = "int(32)";
				}
				return typeName;
			}
		}
		return null;
	}
	/**
	 * 获取数据库用的名称（TestBean==>test_bean）
	 * @param name
	 * @return
	 */
	public static String getSqlName(String name){
		if((name == null)||(name.equals("")))return null;
		name = (name.charAt(0)+"").toLowerCase()+name.substring(1);
		int index = 0;
		while((index = contains(name))!=-1){
			char c = name.charAt(index);
			name = name.replaceFirst(c+"", ("_"+c).toLowerCase());
		}
		return name;
	}
	/**
	 * 返回字符串中第一个大写字母的索引
	 * @param str
	 * @return 没有大写字母返回-1
	 */
	public static int contains(String str){
		for(int i=0;i<str.length();i++){
			char c = str.charAt(i);
			if(('A'<=c)&&(c<='Z')){
				return i;
			}
		}return -1;
	}
	/**
	 * 将字符串的第一个字符大写
	 * @param str
	 * @return
	 */
	public static String getFirstUpperString(String str){
		String firstChar = str.charAt(0)+"";
		firstChar = firstChar.toUpperCase();
		return firstChar+str.substring(1);
	}
	/**
	 * 将字符串的第一个字符小写
	 * @param str
	 * @return
	 */
	public static String getFirstLowString(String str){
		String firstChar = str.charAt(0)+"";
		firstChar = firstChar.toLowerCase();
		return firstChar+str.substring(1);
	}
}
