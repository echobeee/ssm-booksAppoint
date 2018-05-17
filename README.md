# ssm新手练手项目bookAppointment 整合详细步骤 

**Spring+SpringMVC+Mybatis**

---
Spring+SpringMVC+Mybatis（mysql数据库） 的整合项目：一个用户登陆式图书预约系统
这个练手项目是模仿https://github.com/nize1989/ssm-BookAppointment 这个的，界面有点像哈哈哈，但是代码都是我自己手打的哦
先看看项目运行的效果，来了解其具体的功能

## 1.功能概述

这是登录页面，可以选择记住我，则下次登录已有cookie记录了帐号和密码
 ![登录][1]
 这是图书列表页面，可以看到每一行图书都有详细和预约功能，上面的导航栏有一个搜索框，可以按关键字搜索图书 （同时还实现了分页功能哦）
 ![图书列表][2]
 这是图书的详细页面，因为数据库本身就设计的比较简单，所以这个页面比较简陋啦嘻嘻，我们可以自己加属性的叻~
![图书详细][3]  
点击预约之后，会弹出一个确认框  
![确认预约][4]   
点击确认后，会有三种情况可能出现：预约成功，重复预约，库存不足   
这是预约成功页面   
![预约成功][5]  
同时，我们也可以直接进入图书列表页面，此时是未登录状态，要预约时则需要登录，此时会弹出登录框（有点丑哈哈哈）   
![页内登录][6]  
登录之后就可以预约啦~！（同时在这里登录，会清除cookie）   
最后一个功能是我的预约，可以查看我预约了哪些图书（也是挺简单的页面，比较懒所以只有图书id而且没有分页 --> 但是其实与上面分页差不了多少，所以我就懒得做了！●﹏●）
![我的预约][7]  
功能就到这里啦，接下来就是我的项目整个步骤了~！  

---

## 2.准备工作
### 2.1项目工程
因为自己没学过maven，而且这是第一次整合ssm，所以想着降低难度，所以就没有用maven   
这是工程中的包  
![package][8]    
这是配置文件，config也是一个source folder 所以是classpath下的   
![config][9]   
这是页面的目录，这次为了简单一点，所以使用的是jsp而不是html，jsp是放在web-inf下的（但同时也有练到了ajax和json）   
![pages][10]   
### 2.2 导包
分一下类：
mybatis相关的包（包括mysql和c3p0连接池的包）   
spring3.2的核心包    
spring-mybatis整合包    
json-spring包    
pagehelper相关包（用于mybatis分页）    
webmvc包（十分重要的！！）   
总的有31个包   
  
具体就请大家直接看github里面的lib中的jar包啦   

---

## 接下里就是真正的编码工作啦！

---

## 3.创建mysql表，初始化数据
schema.sql（直接从上面github那里copy过来的），但是因为分页的需求，我还自己加了一些重复的测试数据，在test包下，可以直接用junit run一下就行了 
```java
-- 创建数据库
CREATE DATABASE bookappoint;
USE bookappoint;

-- 创建图书表
CREATE TABLE `book` ( 
  `book_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '图书ID',
  `name` varchar(100) NOT NULL COMMENT '图书名称',
  `introd` varchar(1000) NOT NULL COMMENT '简介',
  `number` int(11) NOT NULL COMMENT '馆藏数量',
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='图书表';

-- 初始化图书数据
INSERT INTO `book` (`book_id`, `name`, `introd`,`number`)
VALUES
	(1000, 'Java程序设计', 'Java程序设计是一门balbal',10),
	(1001, '数据结构','数据结构是计算机存储、组织数据的方式。数据结构是指相互之间存在一种或多种特定关系的数据元素的集合。', 10),
	(1002, '设计模式','设计模式（Design Pattern）是一套被反复使用、多数人知晓的、经过分类的、代码设计经验的总结。',10),
	(1003, '编译原理','编译原理是计算机专业的一门重要专业课，旨在介绍编译程序构造的一般原理和基本方法。',10),
	(1004,'大学语文','基于长期的教学实践和学科思考，我们编写了这本《大学语文》教材balbal',10),
	(1005,'计算方法','计算方法又称“数值分析”。是为各种数学问题的数值解答研究提供最有效的算法。',10),
	(1006,'高等数学','广义地说，初等数学之外的数学都是高等数学，也有将中学较深入的代数、几何以及简单的集合论初步balbal',10);

-- 创建预约图书表
CREATE TABLE `appointment` (
  `book_id` bigint(20) NOT NULL COMMENT '图书ID',
  `student_id` bigint(20) NOT NULL COMMENT '学号',
  `appoint_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '预约时间' ,
  PRIMARY KEY (`book_id`, `student_id`),
  INDEX `idx_appoint_time` (`appoint_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='预约图书表';
-- 创建学生数据表
CREATE TABLE `student`(
	`student_id` bigint(20) NOT NULL COMMENT '学生ID',
	`password`  bigint(20) NOT NULL COMMENT '密码',
	PRIMARY KEY(`student_id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='学生统计表';	
-- 初始化学生数据
INSERT INTO `student`(`student_id`,`password`) 
VALUES
	(3211200801,346543),
	(3211200802,754323),
	(3211200803,674554),
	(3211200804,542344),
	(3211200805,298383),
	(3211200806,873973),
	(3211200807,193737),
	(3211200808,873092);
```

---

## 4.实体类创建
实体类创建，我推荐大家可以用mybatis的逆向工程创建，入门比较简单，且它创建的实体类以及mapper.java,mapper.xml都比较全面好用，就不用自己来打mapper了，但是涉及到多表还是需要自己来写sql模版的。   
mybatis的逆向工程在生成实体类时，除了entity本身还会生成一个example，这个是一个sql语句的白话版java代码（有点像hibernate？），就可以执行很多查询了，只需要创建内部类criteria就ok啦。    
这次的表比较简单，所以单用逆向工程生成的文件就可以满足我们的mysql访问需求了~     
这是mybatis逆向工程的生成代码，直接复制然后运行就ok的（当然还是要有配置文件和jar包）    
```java
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

public class GeneratorSqlmap {

    public void generator() throws Exception{

        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        //指定 逆向工程配置文件
        File configFile = new File("generatorConfig.xml"); 
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,
                callback, warnings);
        myBatisGenerator.generate(null);

    } 
    public static void main(String[] args) throws Exception {
        try {
            GeneratorSqlmap generatorSqlmap = new GeneratorSqlmap();
            generatorSqlmap.generator();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
```
配置文件，每一个语句的意思都有注释啦~~
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
  PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
  "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <context id="testTables" targetRuntime="MyBatis3">
        <commentGenerator>
            <!-- 是否去除自动生成的注释 true：是 ： false:否 -->
            <property name="suppressAllComments" value="true" />
        </commentGenerator>
        <!--数据库连接的信息：驱动类、连接地址、用户名、密码 -->
        <jdbcConnection driverClass="com.mysql.jdbc.Driver"
            connectionURL="jdbc:mysql://localhost:3306/bookappoint" userId="root"
            password="1234">
        </jdbcConnection>
        <!-- <jdbcConnection driverClass="oracle.jdbc.OracleDriver"
            connectionURL="jdbc:oracle:thin:@127.0.0.1:1521:yycg" 
            userId="yycg"
            password="yycg">
        </jdbcConnection> -->

        <!-- 默认false，把JDBC DECIMAL 和 NUMERIC 类型解析为 Integer，为 true时把JDBC DECIMAL 和 
            NUMERIC 类型解析为java.math.BigDecimal -->
        <javaTypeResolver>
            <property name="forceBigDecimals" value="false" />
        </javaTypeResolver>

        <!-- targetProject:生成PO类的位置 -->
        <javaModelGenerator targetPackage="cn.echo.books.pojo"
            targetProject=".\src">
            <!-- enableSubPackages:是否让schema作为包的后缀 -->
            <property name="enableSubPackages" value="false" />
            <!-- 从数据库返回的值被清理前后的空格 -->
            <property name="trimStrings" value="true" />
        </javaModelGenerator>
        <!-- targetProject:mapper映射文件生成的位置 -->
        <sqlMapGenerator targetPackage="cn.echo.books.mapper" 
            targetProject=".\src">
            <!-- enableSubPackages:是否让schema作为包的后缀 -->
            <property name="enableSubPackages" value="false" />
        </sqlMapGenerator>
        <!-- targetPackage：mapper接口生成的位置 -->
        <javaClientGenerator type="XMLMAPPER"
            targetPackage="cn.echo.books.mapper" 
            targetProject=".\src">
            <!-- enableSubPackages:是否让schema作为包的后缀 -->
            <property name="enableSubPackages" value="false" />
        </javaClientGenerator>
        <!-- 指定数据库表 -->
        <table schema="" tableName="appointment"></table>
        <table schema="" tableName="book"></table>
        <table schema="" tableName="student"></table>
       

        <!-- 有些表的字段需要指定java类型
         <table schema="" tableName="">
            <columnOverride column="" javaType="" />
        </table> -->
    </context>
</generatorConfiguration>
```
就会生成7个pojo实体类：
```java
Student.java, StudentExample.java, Book.java, BookExample.java, Appointment.java, AppointmentKey.java, AppointmentExample.java
```
这里就不一一展示代码给大家啦，其中Example的用法可以看下面Service层方法调用就ok了，挺简单的呢~
<br>
以及会生成mapper包，即dao层，其中有mapper.xml和mapper.java（接口），即mybatis的框架用法   
既然有了mapper.xml是不是一定会想起mybatis的配置文件呢！    
没错啦，但是不要急，这里我没有直接写mybatis的配置文件，而是与spring整合在了一起（当然也可以有mybatis的配置文件，spring再导入也是ok的）  
由于spring的配置十分多，所以我们分成了四个配置文件对于其controller，service，transaction，dao层的配置   
我们先看看对于dao层的spring配置   
几点是比较重要的要注意：
1. 首先引入db.properties,即mysql数据库的相关配置，driver，url，username，password这四个，（解耦！！方便！！）
2. 然后是数据库连接池的配置，这里用的是c3p0的，关于连接池的配置不止这些，大家可以上网搜索再根据需要加配置
3. 配置sqlSessionFactory，注入连接池等属性，若想引入mybatis的配置文件，则可在此加入这个语句
```xml
 <property name="configLocation" value="classpath:mybatis-config.xml" />
```
4. **Spring自动代理(重点！！！)**   
配置扫描mapper包，动态实现mapper接口，自动创建代理对象，注入到spring容器中    
**application-dao.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
	http://www.springframework.org/schema/beans/spring-beans.xsd
	http://www.springframework.org/schema/context
	http://www.springframework.org/schema/context/spring-context.xsd
	http://www.springframework.org/schema/aop
	http://www.springframework.org/schema/aop/spring-aop.xsd
	http://www.springframework.org/schema/tx 
	http://www.springframework.org/schema/tx/spring-tx.xsd">
	
<!-- 1.配置数据库相关参数   properties的属性：${url} -->
	<context:property-placeholder location="classpath:db.properties" />

	<!-- 2.数据库连接池 -->
	<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
		<!-- 配置连接池属性 -->
		<property name="driverClass" value="${jdbc.driver}" />
		<property name="jdbcUrl" value="${jdbc.url}" />
		<property name="user" value="${jdbc.username}" />
		<property name="password" value="${jdbc.password}" />

		<!-- c3p0连接池的私有属性 -->
		<property name="maxPoolSize" value="30" />
		<property name="minPoolSize" value="10" />
		<!-- 关闭连接后不自动commit -->
		<property name="autoCommitOnClose" value="false" />
		<!-- 获取连接超时时间 -->
		<property name="checkoutTimeout" value="10000" />
		<!-- 当获取连接失败重试次数 -->
		<property name="acquireRetryAttempts" value="2" />
	</bean>

		<!-- 3.配置SqlSessionFactory对象 -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<!-- 注入数据库连接池 -->
		<property name="dataSource" ref="dataSource" />
		<!-- 配置MyBaties全局配置文件:mybatis-config.xml -->
		<!-- <property name="configLocation" value="classpath:mybatis-config.xml" /> -->
		<!-- 扫描entity包 使用别名 -->
		<property name="typeAliasesPackage" value="cn.echo.books.pojo" /> 
		<!-- 扫描sql配置文件:mapper需要的xml文件 -->
		<property name="mapperLocations" value="classpath:cn/echo/books/mapper/*.xml" />
		<!-- pageHelper插件配置 -->
		  <property name="plugins">
            <array>
                <bean class="com.github.pagehelper.PageInterceptor">
                    <property name="properties">
                        <value>
                        	<!-- 数据库方言为mysql，若为oracle数据库，则在这里改 -->
                            helperDialect=mysql
                        </value>
                    </property>
                </bean>
            </array>
        </property> 
	</bean>

	<!-- 4.配置扫描Dao接口包，动态实现mapper接口，自动创建代理对象，注入到spring容器中 -->
	<!-- mapper代理开发 -->
	<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
		<!-- 注入sqlSessionFactory -->
		<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
		<!-- 给出需要扫描Dao接口包 -->
		<property name="basePackage" value="cn.echo.books.mapper" /> 
	</bean>
	
</beans>
```
**db.properties**
```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc\:mysql\://localhost\:3306/bookappoint
jdbc.username=
jdbc.password=

```
至此，mapperceng（dao层）就是结束了，此时就是写测试代码了，我只创建了一个测试文件，供整个项目使用，（测试十分重要，建议大家在写完一个模块都要写）
spring与Junit的整合也要学会哦~！    
在测试类前面加上这两句
```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-*.xml")
```
然后在类中用@Autowired来自动注入类就可以像普通项目的junit测试一样使用啦，如：
```java
@Autowired
private BookMapper bookMapper;
```
就是注入了BookMapper代理对象

---

## 5.service层
service层是体现了业务逻辑的一个层，对于我们一些简单的项目，可能大家会觉得没有什么用，就是照搬了dao层的函数，但对于复杂的项目，service层则会显得十分重要，如校验，权限验证，控制业务等这些业务逻辑，同时service层也是事务开启、提交、回滚的层。然后供给controller层（即web层）

---
### 5.1 service接口
首先是写出service接口，其实可能大家会疑惑为什么要一直写接口，而不是直接写实现类，这个很重要一点就是为了解耦，这样会让我们更加方便地进行代码修改和扩展，而减少代码代价。
首先是**BookService.java**
```java
package cn.echo.books.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

import cn.echo.books.pojo.Book;

public interface BookService {
	/*
	 * 查询一本书
	 */
	public Book findById(long bookId);
	/*
	 * 查询所有书（分页）
	 */
	public PageInfo<Book> findAll(int page, int pageSize);
	/*
	 * 关键词查询书
	 */
	public PageInfo<Book> findBooksByKey(String keyword, int page, int pageSize);
	/*
	 * 减少书的数量
	 * 返回值为库存量
	 */
	public int reduceOneBook(long bookId);
}
```
**UserService.java**
```java
package cn.echo.books.service;

import cn.echo.books.pojo.Student;

public interface UserService {
	public Student login(long id, long password) throws Exception;
}

```
**AppointService.java**
```java
package cn.echo.books.service;

import java.util.List;

import cn.echo.books.pojo.Appointment;

public interface AppointService {
	/*
	 * 查找学生的预约请求
	 */
	public List<Appointment> findAppointmentsByStudent(long id);
	/*
	 * 预约请求
	 */
	public Appointment appoint(long stu_id, long book_id);
}

```
大家会发现，service的代码好简单！其实是我们这次项目的逻辑比较简单，业务比较简单，但是其实也是有将mapper层的方法组合形成一定的业务逻辑的（大家千万不要觉得service层没啥用！）   
### 5.2 自定义异常类
接下来应该是service层的实现了，但是在写之前我们要想一个问题，就是对于service层出现的一些问题，如我们要怎么实现一些情况：库存不足，密码错误，重复预约等，我们可以用函数返回值来实现，但这样有点低级，同时可读性和可扩展性不是很高，这个时候我们就可以想另一种方法，没错，就是异常！   
我们可以自定义异常类**UserException**和**AppointException**，来达到这个效果，一旦出现上述情况，我们就抛出自定义异常，由controller层去catch到去处理！这样问题就会简单很多，还不必修改我们的service层接口的函数。
定义异常类是很简单的，只需要继承了Exception或者RuntimeException（看你自己的需求），然后写出其构造函数就可以了，我们这里继承了RuntimeException    
**UserException**
```java
package cn.echo.books.exception;

/**
 * 
 * @Description: 用户异常
 * 暂时：登录失败
 * @CreateTime: 2018-5-15 下午12:04:56 
 * @author: bee
 */
public class UserException extends RuntimeException {

	public UserException() {
		super();
	}

	public UserException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserException(String message) {
		super(message);
	}

	public UserException(Throwable cause) {
		super(cause);
	}
	 
}

```
**AppointException**
```java
package cn.echo.books.exception;

/**
 * 
 * @Description: 预约时会出现的异常
 * 如：库存不足、重复预约、其他异常
 * @CreateTime: 2018-5-15 下午12:04:56 
 * @author: bee
 */
public class AppointException extends RuntimeException {

	public AppointException() {
		super();
	}

	public AppointException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppointException(String message) {
		super(message);
	}

	public AppointException(Throwable cause) {
		super(cause);
	}
	 
}
```
定义上述异常后，在出现了一些异常情况我们可以直接抛出，并加上失败信息
```java
 throw new UserException("用户名或密码错误");
 throw new AppointException("库存不足");
```
然后controller层可以catch到直接获取信息
```java
try{
    ...
} catch(UserException e){
    String msg = e.getMessage();//获取错误信息
}
```
定义完了异常类，我们就可以写我们的Service接口实现类了！
### 5.3 servie接口实现
实现service层接口，主要是三个点要注意：   
1. 实现类加上@Service注解，让spring知道这是一个组件，注入其spring容器中
2. 对于注入的属性，加上Autowired（和@Qualifier()-->看自己的需求）
3. spring配置文件
其他的就是按照Service层的逻辑来实现就ok了   
**UserServiceImpl.java**
```java
package cn.echo.books.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.echo.books.exception.UserException;
import cn.echo.books.mapper.StudentMapper;
import cn.echo.books.pojo.Student;
import cn.echo.books.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private StudentMapper studentMapper;

	@Override
	public Student login(long id, long password) throws Exception {
		Student student = studentMapper.selectByPrimaryKey(id);
		if(student == null || !student.getPassword().equals(password)){
			throw new UserException("用户名或密码错误！");
		} else {
			return student;
		}
	}
}

```
**BookServiceImpl**    
主要需要注意的一点是，这里使用到了PageHelper分页插件，用法也很简单    
在查询前，调用函数
```
PageHelper.startPage(page, pageSize);
```
page是页号，pageSize是一页的记录数     
然后将返回的list封装到PageInfo中，这个类有关于分页的信息，如当前页号，总页数，上一页，下一页等
```
PageInfo<Book> pages = new PageInfo<Book>(list);
```
要获取其列表信息，可以直接page.getList()则是刚刚得到的list
```java
package cn.echo.books.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.echo.books.mapper.BookMapper;
import cn.echo.books.pojo.Book;
import cn.echo.books.pojo.BookExample;
import cn.echo.books.service.BookService;

@Service
public class BookServiceImpl implements BookService {
	@Autowired
	@Qualifier(value="bookMapper")
	private BookMapper bookMapper;
	
	/**
	 * 通过id查找书
	 * @param  id 
	 */
	@Override
	public Book findById(long id) {
	 	return bookMapper.selectByPrimaryKey(id);
	}

	/**
	 * 查询所有的书目
	 * @param  page     [页号]
	 * @param  pageSize [一页的记录数]
	 */
	@Override
	public PageInfo<Book> findAll(int page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		List<Book> list = bookMapper.selectAll();
		PageInfo<Book> pages = new PageInfo<Book>(list);
		return pages;
	}

	/**
	 * 通过关键词查找书目
	 * @param  keyword  [关键词]
	 * @param  page     [页号]
	 * @param  pageSize [一页的记录数]
	 */
	@Override
	public PageInfo<Book> findBooksByKey(String keyword, int page, int pageSize) {
		BookExample example = new BookExample();
		BookExample.Criteria criteria = example.createCriteria();
		criteria.andNameLike("%" + keyword +"%");
		BookExample.Criteria criteria1 = example.createCriteria();
		criteria1.andIntrodLike("%" + keyword +"%");
		example.or(criteria1);
		PageHelper.startPage(page, pageSize);
		PageInfo<Book> books = new PageInfo<Book>(bookMapper.selectByExample(example));
		return  books;
	}

	/**
	 * 借出一本书,返回剩余库存量
	 * @param  bookId [description]
	 * @return        [description]
	 */
	@Override
	public int reduceOneBook(long bookId) {
		Book book = bookMapper.selectByPrimaryKey(bookId);
		book.setNumber(book.getNumber()-1);
		bookMapper.updateByPrimaryKey(book);
		return book.getNumber();
	}

}
```
**AppointServiceImpl**   
这就是我们业务逻辑最凸显的service了，在其appoint预约函数中，需要遵循业务逻辑，即： 
1. 查看有无库存，无库存则抛出AppointException
2. 查看是否已经预约过了，若已预约，则抛出AppointException
3. 执行插入预约
4. 返回该预约对象
这里要注意的一点是，这里是有事务的，所以一旦异常产生，事务是会回滚的，所以不用担心，数据会出现偏差，如上方的reduceOneBook函数
```java
package cn.echo.books.service.impl;

import java.util.Date;
import java.util.List;

import oracle.net.aso.a;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.echo.books.exception.AppointException;
import cn.echo.books.mapper.AppointmentMapper;
import cn.echo.books.mapper.BookMapper;
import cn.echo.books.pojo.Appointment;
import cn.echo.books.pojo.AppointmentExample;
import cn.echo.books.pojo.AppointmentKey;
import cn.echo.books.pojo.BookExample.Criteria;
import cn.echo.books.service.AppointService;
import cn.echo.books.service.BookService;

@Service
public class AppointServiceImpl implements AppointService {

	@Autowired
	AppointmentMapper appointmentMapper;
	
	@Autowired
	BookService bookService;
	
	@Override
	public List<Appointment> findAppointmentsByStudent(long id) {
		AppointmentExample appointmentExample = new AppointmentExample();
		AppointmentExample.Criteria criteria = appointmentExample.createCriteria();
		criteria.andStudentIdEqualTo(id);
		return appointmentMapper.selectByExample(appointmentExample);	
	}

	@Override
	@Transactional
	public Appointment appoint(long stu_id, long book_id) {
		/*
		 * 查看库存
		 * 事务会回滚
		 */
		try {
		int update = bookService.reduceOneBook(book_id);
		if(update < 0){
				throw new AppointException("库存不足！");
		} else {
				AppointmentKey key = new AppointmentKey();
				key.setBookId(book_id);
				key.setStudentId(stu_id);
					/*
				 * 查看是否预约过
				 */
				Appointment appointment	= appointmentMapper.selectByPrimaryKey(key);
				if(appointment != null){
					throw new AppointException("重复预约！");
				}
				/*
				 * 插入预约！、
				 */
				appointment = new Appointment();
				appointment.setBookId(book_id);
				appointment.setStudentId(stu_id);
				Date appointTime = new Date();
				appointment.setAppointTime(appointTime);
				appointmentMapper.insert(appointment);
				return appointment;
			}
		} catch(AppointException e) {
			throw e;
		} catch(Exception e1) {
			throw new AppointException("未知错误：" + e1.getMessage());
		}
	}
}

```
### 5.4 spring与service层的配置文件    
事务的配置    
**applicationContext-transaction.xml**   
其中注释的语句可以代替掉那一大长串，作用是一样的    
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
	http://www.springframework.org/schema/beans/spring-beans.xsd
	http://www.springframework.org/schema/context
	http://www.springframework.org/schema/context/spring-context.xsd
	http://www.springframework.org/schema/aop
	http://www.springframework.org/schema/aop/spring-aop.xsd
	http://www.springframework.org/schema/tx 
	http://www.springframework.org/schema/tx/spring-tx.xsd">
	
<!-- 扫描service包下所有使用注解的类型 -->
	<!-- <context:component-scan base-package="com.imooc.appoint.service"></context:component-scan> -->

	<!-- 事务管理器 -->
<bean id="transactionManager"
		class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource"></property>
</bean>

<!-- 通知 -->
<tx:advice id="txAdvice" transaction-manager="transactionManager">
	<tx:attributes>
		<tx:method name="save*" propagation="REQUIRED"/>
		<tx:method name="insert*" propagation="REQUIRED"/>
		<tx:method name="update*" propagation="REQUIRED"/>
		<tx:method name="delete*" propagation="REQUIRED"/>
		<!-- 这个就是AppointServiceImpl中的预约函数 -->
		<tx:method name="appoint" propagation="REQUIRED"/>
		<tx:method name="find*" propagation="SUPPORTS" read-only="true"/>
		<tx:method name="select*" propagation="SUPPORTS" read-only="true"/>
		<tx:method name="get*" propagation="SUPPORTS" read-only="true"/>
	</tx:attributes>
</tx:advice>
<!-- 定义切面 -->
<aop:config>
	<aop:advisor advice-ref="txAdvice"
				 pointcut="execution(* cn.echo.books.service.impl.*.*(..))"/>
</aop:config>

<!-- 配置基于注解的声明式事务 -->
	<!-- <tx:annotation-driven transaction-manager="transactionManager" />
 -->
</beans>
```
**applicationContext-service.xml**
其实这个可以整合到上面去，只是。。。我为了让自己更清楚其分工，就分开了，这里面的语句就是扫描service包为了发现其中的组件，即刚刚的@Service @Autowired这些注解
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:aop="http://www.springframework.org/schema/aop"
	xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="http://www.springframework.org/schema/beans 
	http://www.springframework.org/schema/beans/spring-beans.xsd
	http://www.springframework.org/schema/context
	http://www.springframework.org/schema/context/spring-context.xsd
	http://www.springframework.org/schema/aop
	http://www.springframework.org/schema/aop/spring-aop.xsd
	http://www.springframework.org/schema/tx 
	http://www.springframework.org/schema/tx/spring-tx.xsd">
	
	<context:component-scan base-package="cn.echo.books.service.impl"/>
</beans>
```

---

## 6.web层（Controller）
接下来就是学习难点的web层了，即SpringMVC的强大的地方了，这里有哪些点需要我们注意的呢？
1. 静态资源解析，要保证静态资源不被mvc-dispatcher拦截，如css,js,img等文件
2. 处理器适配器与映射器的配置（其实一行代码就可以搞定）
3. 拦截器，作为权限认证，对于一些需要登录的功能，则由拦截器拦截未登录的
4.  扫描controller中的注解组件，如@Controller,@ResponseBody,@RequestParam,@RequestMapping（这些不懂的要去好好补一下springmvc的知识啦）
5.  配置ViewResolver，设置好前缀后缀，如前缀是/WEB-INF/jsp/，后缀是.jsp，则在controller函数返回时，返回"books",即是返回到/WEB-INF/jsp/books.jsp页面了
重点文件来啦！！    
**springMVC.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
    xmlns:tx="http://www.springframework.org/schema/tx" xmlns:jdbc="http://www.springframework.org/schema/jdbc"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:mvc="http://www.springframework.org/schema/mvc"
    xsi:schemaLocation="http://www.springframework.org/schema/jdbc http://www.springframework.org/schema/jdbc/spring-jdbc-3.0.xsd
        http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.0.xsd
        http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd
        http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.0.xsd 
        http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-3.2.xsd">


<!-- 静态资源解析 -->
    <mvc:resources location="/jsp/js/" mapping="/js/**"/>
    <mvc:resources location="/jsp/img/" mapping="/img/**"/>
	<mvc:resources location="/jsp/css/" mapping="/css/**"/>
	<!-- 用了component-scan就不用这个l -->
    <!-- <context:annotation-config/> -->

 <!-- 处理器适配器 都要实现HandlerAdapter接口 -->
    <mvc:default-servlet-handler />
    
    <mvc:annotation-driven/>
    
    	<!-- 拦截器 -->
    <mvc:interceptors>
		<mvc:interceptor>
			<mvc:mapping path="/**"/>
			<bean class="cn.echo.books.interceptor.Interceptor"></bean>
		</mvc:interceptor>    
    </mvc:interceptors>
    
    <!-- 可以扫描controller包中的组件 -->
     <context:component-scan base-package="cn.echo.books.controller">
           <context:include-filter type="annotation" 
          expression="org.springframework.stereotype.Controller"/> 
    </context:component-scan> 

<!-- <bean class="cn.echo.books.controller.BookController"/> -->
    <!-- <mvc:annotation-driven conversion-service="conversionService"/> -->
    
    
    <!-- handler -->
    <!-- <bean name="/queryItems.action" class="cn.echo.ssm.Controller"/> -->
    

    <!-- 处理器映射器 -->
    <!-- 将bean的name作为url查找，需要在配置handler时候指定beanname -->
    <!-- <bean class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping"/> -->



    <!-- 前缀后缀是视图转发到的jsp页面路径的前缀后缀 -->
    <bean
        class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass"
            value="org.springframework.web.servlet.view.JstlView" />
        <property name="prefix" value="/WEB-INF/jsp/" />
        <property name="suffix" value=".jsp" />
    </bean>
</beans>
```
然后还要修改web.xml文件，加上mvc配置     
**web.xml**   
这里我相信根据注释，大家都能看懂的，毕竟这是javaweb的基础了！  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="2.5" 
	xmlns="http://java.sun.com/xml/ns/javaee" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee 
	http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
  <display-name></display-name>	
  
  <!-- spring的配置文件-->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath:spring/applicationContext-*.xml</param-value>
	</context-param>
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
  
  <!-- spring mvc核心：分发servlet -->
	<servlet>
		<servlet-name>mvc-dispatcher</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<!-- spring mvc的配置文件 -->
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<!-- 配置映射器适配器 -->
			<param-value>classpath:spring/springMVC.xml</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<!-- 设置默认的处理器 使得这些文件不被拦截 -->
	 <servlet-mapping>
	    <servlet-name>default</servlet-name>
	    <url-pattern>*.js</url-pattern>
	    <url-pattern>*.css</url-pattern>
	    <url-pattern>/assets/*"</url-pattern>
	    <url-pattern>/img/*</url-pattern>
	  </servlet-mapping>
	<servlet-mapping>
		<servlet-name>mvc-dispatcher</servlet-name>
		<!-- 两种都可以 -->
		<!-- <url-pattern>*.action</url-pattern> -->
		<!-- 下面这种是REST的配置 -->
		<url-pattern>/</url-pattern>
	</servlet-mapping>
	
	<!-- 处理编码的过滤器 -->
	<filter>
		<filter-name>CharacterEncodingFilter</filter-name>
		<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>utf-8</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>CharacterEncodingFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	
  <welcome-file-list>
    <welcome-file>/WEB-INF/login.jsp</welcome-file>
  </welcome-file-list>
</web-app>

```
接下来便是我们的controller的实现类了    
**BookController.java**      
代码难的地方并不多，这里我讲一下一些我琢磨了挺久的点
@RequestMapping()这里是浏览器的地址入口，你要进入这个函数这要用其@RequestMapping里的字符串了，如@RequestMapping("login)，则你就要在浏览器中输入或者在你的jsp或html页面中输入localhost:8080/books/login了（这里要注意我们刚刚在web.xml中配置了路径映射是"/"，若是"‘*.action"则要变为localhost:8080/books/login.action）  
@RequestParam是使用在参数中的，用的比较多的两个属性值是required和defaultValue 一个是是否必须传入该参数，另一个是默认值
还有一个点是cookie的使用，cookie必须设置maxAge才可用，否则默认是立刻过期的，当然我们可以利用这个属性，让我们把原本设置的cookie使其立刻过期。即：
```java
cookie.setMaxAge(0);//单位是s，一小时=>60 * 60
```
其他的都是挺简单的啦，利用controller的函数的内置参数绑定（request，response，session，modelandview，简单类型）可以简化了我们很多的代码~   
不过我建议大家在写controller层是时与jsp开发一起写，方便我们测试和及时调试
```java
package cn.echo.books.controller;


import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import sun.util.logging.resources.logging;

import com.github.pagehelper.PageInfo;

import cn.echo.books.exception.AppointException;
import cn.echo.books.exception.UserException;
import cn.echo.books.pojo.Appointment;
import cn.echo.books.pojo.Book;
import cn.echo.books.pojo.Student;
import cn.echo.books.service.AppointService;
import cn.echo.books.service.BookService;
import cn.echo.books.service.UserService;

@Controller()
public class BookController {

	//注入service
	//该bean在service.xml中注册，才能被autowired
	@Autowired
	private BookService bookService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AppointService appointService;
	
	@RequestMapping("inlogin")
	public String in(Model model, HttpServletRequest request){
		return "login";
	}
	
	/**
	 * 
	 * @Description: 登录功能
	 * @author: bee
	 * @CreateTime: 2018-5-15 下午2:39:45 
	 */
	@RequestMapping("login")
	public String login(Model model, HttpSession session, HttpServletResponse response, 
			@RequestParam(required=false,defaultValue="1")long id, 
			@RequestParam(required=false)long password, 
			@RequestParam(required=false,defaultValue="0")int remember) {
		try {
			Student student = userService.login(id, password);
			PageInfo<Book> bookList = bookService.findAll(1, 7);
			model.addAttribute("bookList", bookList);
			session.setAttribute("stu", student);
			Cookie cookie1 = new Cookie("id", Long.toString(id));
			Cookie cookie2 = new Cookie("password", Long.toString(password));
			Cookie cookie3 = new Cookie("stuid", Long.toString(id));// 让jsp页面使用的
			if(remember == 1){//保存cookie			
				cookie1.setMaxAge(6 * 60 * 60);//6h
				cookie2.setMaxAge(6 * 60 * 60);//6h
				cookie3.setMaxAge(6 * 60 * 60);

			} else { //不同帐号就清楚缓存  
				cookie1.setMaxAge(0);//0.5h
				cookie2.setMaxAge(0);//0.5h
				cookie3.setMaxAge(30 * 60);
			}
				response.addCookie(cookie1);
				response.addCookie(cookie2);
				response.addCookie(cookie3);
			return "books";
			
		} catch (UserException e) {
			model.addAttribute("msg", e.getMessage());
			return "login";
		} catch (Exception e1) {
			model.addAttribute("msg","内部错误！");
			e1.printStackTrace();
			return "error";
		} 
	}
	
	/**
	 * 页面内登录
	 */
	@RequestMapping("logon")
	public @ResponseBody String logon(HttpServletResponse response, HttpSession session,
			@RequestParam(required=false)long id, 
			@RequestParam(required=false)long password){
		try {
			Student student = userService.login(id, password);
			session.setAttribute("stu", student);
			Cookie cookie1 = new Cookie("id", Long.toString(id));
			Cookie cookie2 = new Cookie("password", Long.toString(password));
			Cookie cookie3 = new Cookie("stuid", Long.toString(id));
			cookie1.setMaxAge(0);//清空登录缓存
			cookie2.setMaxAge(0);
			cookie3.setMaxAge(30 * 60);//0.5h
			response.addCookie(cookie1);
			response.addCookie(cookie2);
			response.addCookie(cookie3);
			return "SUCCESS";
		} catch (UserException e) {
			return "FALSE";
		} catch (Exception e1) {
			return "FALSE";
		} 
	}
	
	
	/**
	 * 
	 * @Description: 注销功能
	 * @author: bee
	 * @CreateTime: 2018-5-15 下午2:40:09 
	 */
	@RequestMapping("logout")
	public String logout(HttpSession session, HttpServletResponse response) {
		Cookie cookie1 = new Cookie("stuid", "0");//为了清空缓存
		cookie1.setMaxAge(0);
		response.addCookie(cookie1);
		session.invalidate();
		return "login";
	}
	
	/**
	 * 查询所有书目
	 */
	@RequestMapping("findAllBooks")
	public String findAllBooks(Model model,
						@RequestParam(required=false,defaultValue="1")int page,
						@RequestParam(required=false,defaultValue="7")int pageSize) {
		/*
		 * 得到分页的list
		 * 传出去的是一个pageInfo的列表
		 * 
		 */
		PageInfo<Book> bookList = bookService.findAll(page, pageSize);
		model.addAttribute("bookList", bookList);
		return "books";
	}
	
	/**
	 * 查找某本书
	 */
	@RequestMapping("findBook")
	public String findBook(Model model, long bookId) {
		Book book = bookService.findById(bookId);
		model.addAttribute("book", book);
		
		return "detail";
	}
	
	/**
	 * 预约书目
	 * @param  model   
	 * @param  session 
	 * @param  bookId  
	 * @return        
	 */
	@RequestMapping("appoint")
	public String bookAppointment(Model model, HttpSession session, long bookId) {
		Student student = (Student) session.getAttribute("stu");
		long stu_id = student.getStudentId();
		try {
			Appointment appointment = appointService.appoint(stu_id, bookId);
			model.addAttribute("appoint", appointment);
			return "confirm";
		} catch (AppointException e) {
			model.addAttribute("msg",e.getMessage());
			model.addAttribute("book",bookService.findById(bookId));
			return "detail";
		}
	}
	
	/**
	 * 查看我的预约
	 * @param  model   [description]
	 * @param  session [description]
	 * @return         [description]
	 */
	@RequestMapping("myAppoint")
	public String myAppointment(Model model, HttpSession session) {
		Student student = (Student) session.getAttribute("stu");
		long stuId = student.getStudentId();
		List<Appointment> myAppointments = appointService.findAppointmentsByStudent(stuId);
		model.addAttribute("appointList", myAppointments);
		return "myAppointment";
	}
	
	@RequestMapping("findBooks") 
	public String findBooksBykey(Model model, String keyword,
			@RequestParam(required=false,defaultValue="1")int page,
			@RequestParam(required=false,defaultValue="7")int pageSize) {
		PageInfo<Book> bookList = bookService.findBooksByKey(keyword, page, pageSize);
		model.addAttribute("bookList", bookList);
		return "books";
	}
}
```
---

## 7. 拦截器
对于登录权限认证的拦截器，只实现了preHandle函数，通过url和session判断是否放行    
**Interceptor.java**
```java
package cn.echo.books.interceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


public class Interceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object object) throws Exception {
		String url = request.getRequestURI();
		if(url.indexOf("log") >= 0 || url.indexOf("find") >=0) {//如果是登录页面，放行
			return true;
		}
		HttpSession session = request.getSession();
		if(session.getAttribute("stu") == null) {
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
			return false;
		}
		return true;
	}

}

```
到这里后端的开发就基本结束了，接下来就是剩下前端！

---

## 8. 前端页面
我的前端基础比较差，所以选择了jsp页面来开发，而不是html，因为jsp内置了javaweb的很多对象，方便了开发，但我保证下次一定用html！！！（哭了。。）   
开发前端其实主要就是要与controller的联系了，其他的就是页面的美观程度，我这次使用的是bootstrap框架，也能开发出勉强能看的页面，但是目前好像用的比较多的框架是vue和react，所以对前端有兴趣的童鞋可以去看看这两个~我就...（不说了不说了。。）
我就直接上代码啦！（这次主要是ssm为重点所以前端也没什么好讲的了哈哈哈哈）    
**图书列表页面books.jsp**
```jsp
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="common/tag.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>图书列表</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
    <title>图书列表</title>
    <%@include file="common/head.jsp" %>
    
  <script type="text/javascript">
  function mytime(){
      var a = new Date();
      var b = a.toLocaleTimeString();
      var c = a.toLocaleDateString();
      document.getElementById("appoint-box").innerHTML = c+"&nbsp"+b;
      }
  setInterval(function() {mytime()},1000);
  </script>
  </head>
  
  <body>
  <nav class="navbar navbar-default" role="navigation">
	<div class="container-fluid">
	<div class="navbar-header">
		<a class="navbar-brand" href="<%=request.getContextPath() %>/findAllBooks">BookAppointment</a>
	</div>

	<div>
        <form action="<%=request.getContextPath() %>/findBooks" class="navbar-form navbar-left" role="search">
            <div class="form-group">
                <input type="text" class="form-control" style="width: 500px" name="keyword" placeholder="搜索书目">
            </div>
            <input type="submit" class="btn btn-info" value="search"/>
        </form>
    </div>	

	<div class="navbar-right">
		<ul class="nav navbar-nav navbar-right">
			<li><a href="<%=request.getContextPath() %>/myAppoint">我的预约</a></li>
           <li class="navbar-text">您好！${stu.studentId }</li>
           <c:choose>
           <c:when test="${stu eq null }">
           		<li><a href="<%=request.getContextPath() %>/inlogin">请登录</a></li>
           </c:when>
           <c:otherwise>
           		<li><a href="<%=request.getContextPath() %>/logout">注销</a></li>
           </c:otherwise>
           </c:choose>
           <li class="navbar-text" id="appoint-box" style="color: red"></li>
        </ul>
	</div>

	</div>
   </nav>
  	<div class="panel panel-primary">
	    <div class="panel-heading text-center">
	        <h3 class="panel-title">图书列表</h3>
	    </div>
    <div class="panel-body">
    	<table class="table table-hover">
	        <tr>
	        	<th>图书id</th>
	        	<th>书名</th>
	        	<th>库存</th>
	        	<th>详细</th>    
	        	<th>预约图书</th>
	        </tr>
	        <c:forEach items="${bookList.list }" var="book">
	        <tr>
		        <td>${book.bookId }</td>
		        <td>${book.name }</td>
		        <td>${book.number }</td>
		        <td><a href="<%=request.getContextPath() %>/findBook?bookId=${book.bookId}" class="btn btn-info" target="_blank" >详细</a></td>
	        	<td><button type="button" class="btn btn-info appoint" value="${book.bookId }">
	        	预约
	        	</button>
	        	</td>
	        </tr>
   			</c:forEach>
   		</table>
   		<!-- 信息删除确认 -->  
		<div class="modal fade" id="confirmModal">  
		  <div class="modal-dialog">  
		    <div class="modal-content message_align">  
		      <div class="modal-header">  
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>  
		        <h4 class="modal-title">请先登录哦</h4>  
		      </div>  
		      <div class="modal-body">  
		        <div class="row">
                    <div class="col-xs-6 col-xs-offset-2">
                        <input type="text" name="id" id="studentIdKey"
                               placeholder="学号" class="form-control" >
                    </div>
                    <div class="col-xs-6 col-xs-offset-2">
                        <input type="password" name="password" id="passwordKey"
                               placeholder="密码" class="form-control">
                    </div>
                </div>
		      </div>  
		      <div class="modal-footer">  
		         <input type="hidden" id="url"/>  
		         <span id="studentMessage" class="glyphicon"> </span> 
		         <button type="button" id="studentBtn" class="btn btn-success">
                    <span class="glyphicon glyphicon-student"></span>
                    	登录
                </button>  
		      </div>  
		    </div><!-- /.modal-content -->  
		  </div><!-- /.modal-dialog -->  
		</div><!-- /.modal -->  
   			<%-- 分页
   				1. 总页数小于10 则直接显示1-10
   				2.否则 常规状态
   					判断头溢出
   					判断尾溢出
   			 --%>
   			 <div class="text-center">
   			 <a href="<%=request.getContextPath() %>/findAllBooks?pageSize=7&page=1">首页</a>
   			 <c:if test="${bookList.hasPreviousPage }">
   			 <a href="<%=request.getContextPath() %>/findAllBooks?pageSize=7&page=${bookList.prePage}">上一页</a>
   			 </c:if>
   			 <c:choose>
   			 <%-- 若总页数小于10，全部显示 --%>
   			 	<c:when test="${bookList.pages < 10 }">
   			 		<c:set var="begin" value="1"></c:set>
   			 		<c:set var="end" value="${bookList.pages }"/>
   			 	</c:when>
   			 <%-- 否则，常规显示 --%>
   			 <c:otherwise>
   			 	<c:set var="begin" value="${bookList.pageNum-4 }"/>
   			 	<c:set var="end" value="${bookList.pageNum+5 }"/>
   			 	<%-- 头部溢出 --%>
   			 	<c:if test="${begin<1 }">
   			 		<c:set var="begin" value="1"/>
   			 		<c:set var="end" value="10"/>
   			 	</c:if>
   			 	<%-- 尾部溢出 --%>
   			 	<c:if test="${end>bookList.pages }">
   			 		<c:set var="begin" value="${bookList.pages-9 }"/>
   			 		<c:set var="end" value="${bookList.pages }"/>
   			 	</c:if>
   			 </c:otherwise>
   			 </c:choose>
   			 <c:forEach var="i" begin="${begin }" end="${end }" >
   			 	<c:choose>
   			 		<c:when test="${i eq bookList.pageNum }">
   			 		[${i }]
   			 		</c:when>
   			 		<c:otherwise>
   			 		<a href="<%=request.getContextPath() %>/findAllBooks?pageSize=7&page=${i}">[${i }]</a>
   			 		</c:otherwise>
   			 	</c:choose>
   			 </c:forEach>
   			 <c:if test="${bookList.hasNextPage }">
   			 <a href="<%=request.getContextPath() %>/findAllBooks?pageSize=7&page=${bookList.nextPage}">下一页</a>
   			 </c:if>
   			 <a href="<%=request.getContextPath() %>/findAllBooks?pageSize=7&page=${bookList.pages}">尾页</a>
   			 
   			 </div>
   		</div>
</div>
  
  
	<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
	
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
 	<%--jQuery Cookie操作插件--%>
	<script src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
 	<script src="js/bookappoint.js"></script>
  </body>
  
</html>
			
```
**详细页面detail.jsp**
```jsp
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="common/tag.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>图书详情</title>
    <%@include file="common/head.jsp" %>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript">
	function confirm(){
		var url = "<%=request.getContextPath() %>/appoint?bookId=${book.bookId}";
		$("#url").val(url);
		$("#confirmModal").modal();
	}
	
	function urlSubmit(){
		var url=$.trim($("#url").val());//获取会话中的隐藏属性URL  
		window.location.href=url;    
	}
	
	</script>

  </head>
  
  <body>
    <div class="panel panel-info">
	    <div class="panel-heading">
	        <h3 class="panel-title">
	          	${book.name }<br>
	          	${msg }
	          	
	        </h3>
	    </div>
	    <div class="panel-body">
	       	图书id：${book.bookId } <br>
	       	书名：${book.name } <br>
	       	书目介绍：${book.introd }<br>
	       	库存：${book.number }<br>
	       	<a onclick="confirm()" class="btn btn-info" target="_blank" >预约图书</a>
	    </div>
	</div>
	<!-- 信息删除确认 -->  
	<div class="modal fade" id="confirmModal">  
	  <div class="modal-dialog">  
	    <div class="modal-content message_align">  
	      <div class="modal-header">  
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>  
	        <h4 class="modal-title">提示信息</h4>  
	      </div>  
	      <div class="modal-body">  
	        <p>您确认要预约吗？</p>  
	      </div>  
	      <div class="modal-footer">  
	         <input type="hidden" id="url"/>  
	         <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>  
	         <a  onclick="urlSubmit()" class="btn btn-primary" data-dismiss="modal">确定</a>  
	      </div>  
	    </div><!-- /.modal-content -->  
	  </div><!-- /.modal-dialog -->  
	</div><!-- /.modal -->  
	
	<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
	
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
  </body>
</html>

```
**确认预约页面confirm.jsp**
```jsp
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="common/tag.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>确认预约</title>
    <%@include file="common/head.jsp" %>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
    <div class="panel panel-info">
	    <div class="panel-heading">
	        <h3 class="panel-title">
	          	预约成功！
	        </h3>
	    </div>
	    <div class="panel-body">
	       	图书id：${appoint.bookId } <br>
		        预约者id：${appoint.studentId }<br>
		        预约时间：<fmt:formatDate value="${appoint.appointTime}" pattern="yyyy-MM-dd HH:mm"/>  <br>
	    </div>
	</div>
	<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
	
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
  </body>
</html>

```
**登录页面login.jsp**
```jsp
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="common/tag.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
  	<title>登录</title>
    <%@include file="common/head.jsp" %>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css">


  </head>
  
  <body>
  <form action="<%=request.getContextPath()%>/login" method="post">
    <div class="container">
        <div class="form row">
            <div class="form-horizontal col-md-offset-3" id="login_form">
                <h3 class="form-title">LOGIN</h3>
                <div style="color: red">${msg }</div>
                <div class="col-md-9">
                    <div class="form-group">
                        <i class="fa fa-user fa-lg"></i>
                        <input class="form-control required" type="text" placeholder="用户名" id="username" name="id" autofocus="autofocus" maxlength="20" value="${cookie.id==null?null:cookie.id.value }"/>
                    </div>
                    <div class="form-group">
                            <i class="fa fa-lock fa-lg"></i>
                            <input class="form-control required" type="password" placeholder="密码" id="password" name="password" maxlength="8" value="${cookie.password==null?null:cookie.password.value }"/>
                    </div>
                    <div class="form-group">
                        <label class="checkbox">
                            <input type="checkbox" name="remember" value="1"/>记住我
                        </label>
                    </div>
                    <div class="form-group col-md-offset-9">
                        <button type="submit" class="btn btn-success pull-right" name="submit">登录</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </form>
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
	
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
</body>

</html>

```
**我的预约myAppointment.jsp**
```jsp
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="common/tag.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>图书列表</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
    <title>图书列表</title>
    <%@include file="common/head.jsp" %>

  </head>
  
  <body>
  <nav class="navbar navbar-default" role="navigation">
	<div class="container-fluid">
	<div class="navbar-header">
		<a class="navbar-brand" href="<%=request.getContextPath() %>/findAllBooks">BookAppointment</a>
	</div>

	<div>
        <form action="<%=request.getContextPath() %>/findBooks" class="navbar-form navbar-left" role="search">
            <div class="form-group">
                <input type="text" class="form-control" style="width: 500px" name="keyword" placeholder="搜索书目">
            </div>
            <input type="submit" class="btn btn-default" value="search"/>
        </form>
    </div>	

	<div class="navbar-right">
		<ul class="nav navbar-nav navbar-right">
			<li><a href="<%=request.getContextPath() %>/myAppoint">我的预约</a></li>
           <li class="navbar-text">您好！${stu.studentId }</li>
           <li><a href="<%=request.getContextPath() %>/logout">注销</a></li>
        </ul>
	</div>

	</div>
   </nav>
  	<div class="panel panel-primary">
	    <div class="panel-heading text-center">
	        <h3 class="panel-title">我的预约</h3>
	    </div>
    <div class="panel-body">
    	<table class="table table-hover">
	        <tr>
	        	<th>图书id</th>
	        	<th>预约id</th>
	        	<th>时间</th>
	        	<th>详细</th>    
	        	<th>预约图书</th>
	        </tr>
	        <c:forEach items="${appointList }" var="appoint">
	        <tr>
		        <td>${appoint.bookId }</td>
		        <td>${appoint.studentId }</td>
		        <td><fmt:formatDate value="${appoint.appointTime}" pattern="yyyy-MM-dd HH:mm"/></td>
		        <td><a href="<%=request.getContextPath() %>/findBook?bookId=${appoint.bookId}" class="btn btn-info" target="_blank" >详细</a></td>
	        	<td><a href="<%=request.getContextPath() %>/appoint?bookId=${appoint.bookId}" class="btn btn-info" target="_blank" >预约</a></td>
	        </tr>
   			</c:forEach>
   		</table>
    </div>
    
</div>
  
  
	<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>
	
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>
  </body>
  
</html>
			
```
**错误页面error.jsp（这个页面并没有开发，请大家自行美化）**
```jsp
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'welcome.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
 ${msg}
  </body>
</html>

```
**bookappoint.js**
```javascript
// 该js功能：
// 预约前需要登录
// 要引入jquery cookie插件
window.onload = function(){
    	$('.appoint').click(function(){
    		console.log($(this).attr("value"));
    		confirm($(this).attr("value"));
    	});
    	$('#studentBtn').click(urlSubmit);
    	
    	function confirm(bookId){
    	    	var url = "logon";
    	    	$("#url").val(url);
    	    	var id =$.cookie("stuid");
    	    	console.log(id);
    	    	if(id == undefined){ //只有session中没有该属性才要登录框
    	    		$("#confirmModal").modal();
    	    	} else { //否则直接预约
    	    		url = "appoint?bookId="+bookId;
    	    		window.location.href=url;    
    	    	}
    	    }

    	   function urlSubmit(){
    	    	var url=$.trim($("#url").val());//获取会话中的隐藏属性URL 
    	    	var params = {};// 获取帐号密码
    	    	params.id = $('#studentIdKey').val();
    	    	console.log(params.id);
    	    	if(params.id == ''){
    	    		params.id = 1;
    	    	}
    	    	params.password = $('#passwordKey').val();
    	    	if(params.password == ''){
    	    		params.password = 1;
    	    	}
    	    	$.ajax({
    	    		type:"post",
    	    		url:url,
    	    		data:params,
    	    		datatype:'josn', 
    	    	//	async:false,     
    	    		success:function(data){
    	    			if(data == "SUCCESS"){
    	    				window.location.reload();//页面刷新
    	    				alert("登录成功！");
    	    			}else{
    	    				$('#studentMessage').hide().html('<label class="label label-danger">帐号或密码错误!</label>').show(300);
    	    			}
    	    		}
    	    	});    
    	    }
    	   
    };
```
**登录页面的样式文件style.css(我认为蛮好看的哦，图片转换)**
```style.css
@CHARSET "UTF-8";
body{
    background: url("../img/1.jpg");
    animation-name:myfirst;
    animation-duration:12s;
    /*变换时间*/
    animation-delay:2s;
    /*动画开始时间*/
    animation-iteration-count:infinite;
    /*下一周期循环播放*/
    animation-play-state:running;
    /*动画开始运行*/
}
@keyframes myfirst
{
    0%   {background:url("../img/1.jpg");}
    34%  {background:url("../img/2.jpg");}
    67%  {background:url("../img/3.jpg");}
    100% {background:url("../img/1.jpg");}
}
.form{background: rgba(255,255,255,0.2);width:400px;margin:120px auto;}
/*阴影*/
.fa{display: inline-block;top: 27px;left: 6px;position: relative;color: #ccc;}
input[type="text"],input[type="password"]{padding-left:26px;}
.checkbox{padding-left:21px;}
```
PS:前端页面因为我比较懒，所以有些应该实现的相同功能我并没有实现，请见谅！（懒癌嘛~！）

---

## 9. 总结
到这里就整个ssm-读者登录图书预约项目就结束啦（完结撒花！！）   
那也想的太美了！
我们最后还是要总结一下：
这次的ssm项目整合开发运用到的ssm只是都是比较浅层的，功能实现也比较简单，用到的知识点：  
mybatis主要是文件配置，逆向工程，pageHelper分页插件，spring的详细配置，spring注解的使用，servie层属性的注入，springmvc controller层的编写，拦截器的使用，自定义异常类  
这次没有用的spring的另一个core AOP，大家可以自己开发开发，因为这也是spring一个很核心重要的点，下次项目我就会打算用上的啦，同时还有这样的知识点：文件上传下载，全局异常处理，json使用等等

这里顺便记录一下自己遇见的一个坑：    
大家在整合ssm时，要注意spring的版本与jdk的兼容，sprng3.x配对的是jdk1.7及以前，而jdk1.8则只能兼容sprng4.x，否则运行会报错。当大家出现了这样的报错时，除了要注意大家使用的jdk时，也要注意tomcat的jdk版本（被这个坑了很久！！），tomcat的jdk版本也是需要匹配的~   
还有pageHelper的配置，网上有的人博客中方言那里是使用dialect=mysql，这是旧的版本，对于新的版本5.x（我只测试了这个）要用helperDialect=mysql，否则在连接数据库时会出现异常

到此为止，所有开发结束了，源码和jar包都上传到github中，喜欢的大家给个star吧嘻嘻，写了一个下午的readme累的哦   
谢谢大家！（民国鞠躬）   

  [1]: https://github.com/echobeee/ssm-booksAppoint/blob/master/img/login.png
  [2]: https://github.com/echobeee/ssm-booksAppoint/blob/master/img/books.png
  [3]: https://github.com/echobeee/ssm-booksAppoint/blob/master/img/detail.png
  [4]: https://github.com/echobeee/ssm-booksAppoint/blob/master/img/confirm.png
  [5]: https://github.com/echobeee/ssm-booksAppoint/blob/master/img/success.png
  [6]: https://github.com/echobeee/ssm-booksAppoint/blob/master/img/logon.png
  [7]: https://github.com/echobeee/ssm-booksAppoint/blob/master/img/my.png
  [8]: https://github.com/echobeee/ssm-booksAppoint/blob/master/img/package.jpg
  [9]: https://github.com/echobeee/ssm-booksAppoint/blob/master/img/application.jpg
  [10]: https://github.com/echobeee/ssm-booksAppoint/blob/master/img/webroot.jpg
