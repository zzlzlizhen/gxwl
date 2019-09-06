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
import org.springframework.jdbc.support.JdbcUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.PropertyException;

@Intercepts({@Signature(type=StatementHandler.class,method="prepare",args={Connection.class,Integer.class})})
public class PagePlugin  implements Interceptor {

	 //数据库方言
    private static String dialect = "";
    //mapper.xml中需要拦截的ID（正则匹配）
    private static String pageSqlId = "";


    /**
     * 拦截后要执行的方法 
     */
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        if( target instanceof  RoutingStatementHandler){
        	//设置分页属性,更改查询结果为分页结果
        	 RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
             BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(statementHandler,"delegate");
             MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(delegate,"mappedStatement");
             
             if(mappedStatement.getId().matches(pageSqlId)) {
            	 BoundSql boundSql = delegate.getBoundSql();
            	 //分页sql<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数，该参数不能为空
                 Object parameterObject = boundSql.getParameterObject();
                 
                 if (parameterObject == null) {
                     throw new NullPointerException("parameterObject尚未实例化！");
                 } else {
                	  Connection connection = (Connection) invocation.getArgs()[0];
                	  String sql = boundSql.getSql();
                	  //记录统计
                      String countSql = "select count(0) from (" + sql + ") tmp_count";
                      PreparedStatement countStmt = connection.prepareStatement(countSql);
                      BoundSql countBs = new BoundSql(mappedStatement.getConfiguration(),countSql,boundSql.getParameterMappings(),parameterObject);
                      setParameters(countStmt,mappedStatement,countBs,parameterObject);
                      ResultSet rs = countStmt.executeQuery();
                      int count = 0;
                      if (rs.next()) {
                          //根据拼接的sql获得分页信息总数
                          count = rs.getInt(1);
                      }
                      rs.close();
                      countStmt.close();
                      
                      Page page = null;
                    //参数就是Page实体
                      if (parameterObject instanceof Page) {
                          page =  (Page) parameterObject;
                          page.setTotalCount(count);
                      } else {
                          //参数为其他某个实体，该实体拥有page属性
                    	  Field pageField = (Field) ReflectHelper.getFieldByFieldName(parameterObject,"page");
                          if (pageField != null) {
                              page = (Page) ReflectHelper.getValueByFieldName(parameterObject,"page");
                              if (page == null) {
                                  page = new Page();
                              }
                              page.setTotalCount(count);
                              //通过反射，对实体对象设置分页对象
                              ReflectHelper.setValueByFieldName(parameterObject,"page",page);
                              
                          }else{
                              throw new NoSuchFieldException(parameterObject.getClass().getName()+"不存在 page 属性！");
                          }
                      }
                      String pageSql = generatePageSql(sql,page);
                      ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql); //将分页sql语句反射回BoundSql.
                 }
             }
        }
        return invocation.proceed();
    }

    /**
     * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void setParameters(PreparedStatement ps,MappedStatement mappedStatement,BoundSql boundSql,Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null: configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)&& boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter "+ propertyName + " of statement "+ mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }
    /**
     * 根据数据库方言，生成特定的分页sql
     * @param sql
     * @param page
     * @return
     */
    private String generatePageSql(String sql,Page page){
        if(page!=null && dialect.trim().length() > 0){
            StringBuffer pageSql = new StringBuffer();
            if("mysql".equals(dialect)){
                pageSql.append(sql);
                pageSql.append(" limit "+page.getStart()+","+page.getPageShow());
            }else if("oracle".equals(dialect)){
                pageSql.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
                pageSql.append(sql);
                //pageSql.append(") as tmp_tb where ROWNUM<=");
                pageSql.append(") tmp_tb where ROWNUM<=");
                pageSql.append(page.getStart()+page.getPageShow());
                pageSql.append(") where row_id>");
                pageSql.append(page.getStart());
            }
            return pageSql.toString();
        }else{
            return sql;
        }
    }
    /**
     * 拦截器对应的封装原始对象的方法 
     */
    public Object plugin(Object target) {
        if( target instanceof  RoutingStatementHandler || target instanceof Executor){
            return Plugin.wrap(target, this);
        }else{
            return target;
        }
    }

    public void setProperties(Properties p) {
        dialect = p.getProperty("dialect");
        if (Tools.isEmpty(dialect)) {
            try {
                throw new PropertyException("dialect property is not found!");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }
        pageSqlId = p.getProperty("pageSqlId");
        if (Tools.isEmpty(pageSqlId)) {
            try {
                throw new PropertyException("pageSqlId property is not found!");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行 count 操作
     * @param connection  数据库连接
     * @param sql          sql
     * @param parameterHandler  参数设置处理器
     * @return
     */
    private Long count(Connection connection,String sql,ParameterHandler parameterHandler){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(countSql(sql));
            parameterHandler.setParameters(preparedStatement);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return (Long) JdbcUtils.getResultSetValue(resultSet, 1, Long.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(preparedStatement);
        }
        return 0l;
    }

    /**
     * 生成 COUNT 语句
     * @param sql 生成前SQL
     * @return
     */
    public String countSql(String sql){
        return new StringBuffer("SELECT COUNT(1) AS ROW_COUNT FROM ( ").append(sql).append(" ) AS COUNT_QUERY").toString();
    }

}