# BaseDao
dbutils的好用之处不必多说，但是在使用的过程中发现要对每个Bean写一个BeanDao，来实现Bean的访问，虽然一个Bean只需要写一次，但是对于复杂的Bean来说，其属性众多，写起来也是非常费时间的。所有我想写一个基于dbutils的万能BaseDao来一劳永逸。
# 1、BaseDao之前的BeanDao的添加实现：
public boolean addTestBean(TestBean testBean){  
    QueryRunner qr = new TxQueryRunner();  
    String sql = "insert into group values(?,?,?,?,?,?,?)";  
    Object params[] = {testBean.getTitle(),testBean.getCount(),testBean.getClick(),  
            testBean.getLen(),testBean.getId(),testBean.getName(),testBean.getChild()};  
    try {  
        qr.update(sql, params);  
        return true;  
    } catch (SQLException e) {  
        e.printStackTrace();  
        return false;  
    }  
}  
# 2、BaseDao的添加实现是万能的，所有使用如下：
BaseDao bd = new BaseDao<TestBean>("test_bean", TestBean.class, new String[]{"id"});  
TestBean bean = new TestBean();  
bean.setId("123");  
bean.setName("test");  
bd.addObject(bean);  
可见BaseDao还是非常好用的，只需实例化的时传入3个参数，然后就随意的增删改查了。第1个参数是表名，如果传入null，则会使用Bean的名称作表名（TestBean ==> test_bean），第2个参数为Bean的class，第3个参数为主键，可以为多键联合主键。
3、这里除了BaseDao的源码外还有一个工具类：MBUtils包含了BaseDao中用到的反射工具，都是刚开始写的，所以功能不是很多，但是还有个很好用的功能是可以根据Bean生成创建表的sql语句，方便Bean属性过多时使用非常方便，但是非常简易，以后再优化。
使用非常简单，一行代码：
MBUtils.generateCreateTable(null, TestBean.class,new String[]{"id"})  
其中第1个参数是表名，如果传入null，则会使用Bean的名称作表名（TestBean ==> test_bean）。效果如下：
CREATE TABLE 'test_bean'(  
    'name' varchar(255),  
    'click' varchar(255),  
    'id' varchar(255),  
    'count' int(32),  
    'title' varchar(255),  
    'child' varchar(255),  
    'len' varchar(255),  
     PRIMARY KEY ('id')  
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;  
# 4、依赖的jar包
c3p0-0.9.2-pre1.jar  
commons-dbutils-1.4.jar  
commons-logging-1.1.1.jar  
itcast-tools-1.4.2.jar  
mchange-commons-0.2.jar  
mysql-connector-java-5.1.28-bin.jar  
下载地址：
# 5、关于c3p0的使用，需要将c3p0-config.xml文件放到src目录下，文件名不可更改。然后配置一下内容：
![image](https://github.com/qghc/BaseDao/raw/master/screenshots/c3p0.jpg)
