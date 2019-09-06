package com.remote.pageutil;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.PropertyException;
/*
 *分页插件
 */
@Intercepts({@Signature(type=StatementHandler.class,method="prepare",args={Connection.class,Integer.class})})
public class PagePlugin1 implements Interceptor {

 //数据库方言
 private static String dialect = "";
 //mapper.xml中需要拦截的ID（正则匹配）
 private static String pageSqlId = "";

 public Object intercept(Invocation ivk) throws Throwable {
     if (ivk.getTarget() instanceof RoutingStatementHandler) {
         RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
         BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(statementHandler,"delegate");
         MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(delegate,"mappedStatement");

         //拦截需要分页的sql
         if (mappedStatement.getId().matches(pageSqlId)) {
             BoundSql boundSql = delegate.getBoundSql();
             String sql = boundSql.getSql();
             //分页sql<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数，该参数不能为空
             Object parameterObject = boundSql.getParameterObject();
             if (parameterObject == null) {
                 throw new NullPointerException("parameterObject尚未实例化！");
             } else {
                 Page page = null;
                 Field pageField = null;
                 int count = 0;

                 //参数就是Page实体
                 if (parameterObject instanceof Page) {
                     page = (Page) parameterObject;
                 } else {
                     //参数为其他某个实体，该实体拥有page属性
                     pageField = ReflectHelper.getFieldByFieldName(parameterObject,"page");
                     if (pageField != null) {
                         page = (Page) ReflectHelper.getValueByFieldName(parameterObject,"page");
                         if (page == null) {
                             page = new Page();
                         }
                     }
                 }
                 if (page != null) {
                     Connection connection = (Connection) ivk.getArgs()[0];
                     //记录统计
                     String countSql = "select count(0) from (" + sql + ") tmp_count";
                     PreparedStatement countStmt = (PreparedStatement) connection.prepareStatement(countSql);
                     BoundSql countBs = new BoundSql(mappedStatement.getConfiguration(),countSql,boundSql.getParameterMappings(),parameterObject);
                     setParameters(countStmt,mappedStatement,countBs,parameterObject);
                     ResultSet rs = countStmt.executeQuery();

                     if (rs.next()) {
                         //根据拼接的sql获得分页信息总数
                         count = rs.getInt(1);
                     }
                     rs.close();
                     countStmt.close();
                 }
                 if (parameterObject instanceof Page) {
                     page = (Page) parameterObject;
                     page.setTotalCount(count);
                 } else {
                     if (pageField != null) {
                         page = (Page) ReflectHelper.getValueByFieldName(parameterObject,"page");
                         if (page == null)
                             page = new Page();
                         page.setTotalCount(count);
                         //通过反射，对实体对象设置分页对象
                         ReflectHelper.setValueByFieldName(parameterObject,"page",page);
                     } else {
                         throw new NoSuchFieldException(parameterObject.getClass().getName()+"不存在page属性");
                     }
                 }
                 //添加排序
                /* if (page.getSorter() != null && !"".equalsIgnoreCase(page.getSorter().trim()))
                     sql += " order by " + page.getSorter();*/
                 String pageSql = sql;
                 
                 pageSql = generatePageSql(sql,page.getNowPage(),page.getPageShow());
                 //将分页sql语句反射回BoundSql
                 ReflectHelper.setValueByFieldName(boundSql,"sql",pageSql);
             }
         }
     }
     return ivk.proceed();
 }

 /*
     *对sql中的参数进行设值
     *
     */
 private void setParameters(PreparedStatement ps,MappedStatement mappedStatement,BoundSql boundSql,Object parameterObject) throws SQLException {
     //？？？
     ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
     //获得sql中参数的映射集合
     List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
     if (parameterMappings != null) {
         //如果集合不为空，意思就是sql中有参数需要处理
         //得到mybatis配置信息管理器（管理mybatis所有的配置信息）
         Configuration configuration = mappedStatement.getConfiguration();
         //通过配置信息管理器获得一个TypeHandler注册器，有关TypeHandler看下面备注
         TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
         //获得设置参数工具类（在结果集映射里面也经常使用）
         MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
         for (int i = 0;i < parameterMappings.size();i++) {
             //得到参数映射中的单个
             ParameterMapping parameterMapping = parameterMappings.get(i);
             if (parameterMapping.getMode() != ParameterMode.OUT) {
                 Object value;
                 //得到属性名称
                 String propertyName = parameterMapping.getProperty();
                 //使用PropertyTokenizer类，对参数进行注入
                 PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                 if (parameterObject == null) {
                     //如果参数值为空，则参数值为空
                     value = null;
                 } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                     //如果包含该参数类型，则传递参数
                     value = parameterObject;
                 } else if (boundSql.hasAdditionalParameter(propertyName)) {
                     //---
                     value = boundSql.getAdditionalParameter(propertyName);
                 } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.getName())) {
                     //如果参数中包含函数
                     value = boundSql.getAdditionalParameter(prop.getName());
                     if (value != null) {
                         value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                     }
                 } else {
                     value = metaObject == null ? null : metaObject.getValue(propertyName);
                 }
                 //得到一个类型处理器
                 TypeHandler typeHandler = parameterMapping.getTypeHandler();
                 if (typeHandler == null) {
                     throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                 }
                 //使用类型处理器设置参数（搞了半天这里才是重点额）
                 typeHandler.setParameter(ps,i+1,value,parameterMapping.getJdbcType());
             }
         }
     }
 }

 /*
     *根据sql、页码、每页显示的记录数，生成分页sql
     */
 private String generatePageSql(String sql,int pageNumber,int pageSize) {
     //根据不同数据库计算各自的起始行号
     int begin = getBegin(pageNumber,pageSize);
     if ("oracle".equals(dialect)) {
         int end = begin + pageSize - 1;
         return "select * from (\n" +
             "select t.*,rownum rn from (" + sql + ") t\n" + 
             "where rownum <= " + end + ") where rn >=" + begin;
     } else if ("mysql".equals(dialect)) {
         return "select * from (" + sql + " limit " + begin + "," + pageSize + ") t";
     } else {
         return sql;
     }
 }

 /*
     *获取开始游标
     */
 private static int getBegin(int pageNumber,int pageSize) {
     if ("oracle".equals(dialect)) {
         return (pageNumber - 1) * pageSize + 1; 
     } else if ("mysql".equals(dialect)) {
         return (pageNumber - 1) * pageSize;
     }
     return 0;
 }

 public Object plugin(Object arg0) {
     return Plugin.wrap(arg0,this);
 }

 /*
     *读取配置文件，获得相关属性：方言、拦截的ID
     */
 public void setProperties(Properties p) {
     dialect = p.getProperty("dialect");
     if (dialect.equals("")) {
         try {
             throw new PropertyException("dialect property is not found!");
         } catch (PropertyException e) {
             e.printStackTrace();
         }
     }
     pageSqlId = p.getProperty("pageSqlId");
     if (pageSqlId.equals("")) {
         try {
             throw new PropertyException("pageSqlId property is not found!");
         } catch (PropertyException e) {
             e.printStackTrace();
         }
     }
 }
}