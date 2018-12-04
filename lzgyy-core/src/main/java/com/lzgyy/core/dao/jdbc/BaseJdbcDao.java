package com.lzgyy.core.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONObject;
import com.lzgyy.core.constant.Const;

/**
 * <B>系统名称：</B>通用系统功能<BR>
 * <B>模块名称：</B>数据访问通用功能<BR>
 * <B>中文类名：</B>基础数据访问<BR>
 * <B>概要说明：</B><BR>
 */
public class BaseJdbcDao {

    /** JSON数据行映射器 */
    private static final JsonRowMapper JSON_ROW_MAPPER = new JsonRowMapper();

    /** JDBC调用模板 */
    private JdbcTemplate jdbcTemplate;

    /** 启动时间 */
    private static Date startTime;
    
    /**
     * <B>方法名称：</B>初始化JDBC调用模板<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param dataSource 数据源
     */
    @Autowired
    public void initJdbcTemplate(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        if (startTime == null) {
            startTime = getCurrentTime();
        }
    }

    /**
     * <B>取得：</B>JDBC调用模板<BR>
     * 
     * @return JdbcTemplate JDBC调用模板
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * <B>取得：</B>启动时间<BR>
     * 
     * @return Date 启动时间
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 
     * <B>方法名称：</B>获取数据库的当前时间<BR>
     * <B>概要说明：</B><BR>
     * 
     * @return Date 当前时间
     */
    public Date getCurrentTime() {
    	if (Const.DB_NAME.equals("mysql")){
    		return this.getJdbcTemplate().queryForObject("SELECT NOW()", Date.class);
    	}else{
    		return this.getJdbcTemplate().queryForObject("SELECT SYSTIMESTAMP FROM DUAL", Date.class);
    	}
    }

    /**
     * <B>方法名称：</B>查询JSON列表<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param sql SQL语句
     * @param args 参数
     * @return List<JSONObject> JSON列表
     */
    public List<JSONObject> queryForJsonList(String sql, Object... args) {
        return this.jdbcTemplate.query(sql, JSON_ROW_MAPPER, args);
    }

    /**
     * <B>方法名称：</B>查询JSON数据<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param sql SQL语句
     * @param args 参数
     * @return JSONObject JSON数据
     */
    public JSONObject queryForJsonObject(String sql, Object... args) {
        List<JSONObject> jsonList = queryForJsonList(sql, args);
        if (jsonList == null || jsonList.size() < 1) {
            return null;
        }
        return jsonList.get(0);
    }

    /**
     * <B>方法名称：</B>查询文本<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param sql SQL语句
     * @param args 参数
     * @return String 文本
     */
    public String queryForString(String sql, Object... args) {
    	
    	List<String> dataList = this.jdbcTemplate.queryForList(sql, args, String.class);
        if (dataList == null || dataList.size() < 1) {
            return null;
        }
        return dataList.get(0);
    }

    /**
     * <B>方法名称：</B>拼接分页语句<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param sql       SQL语句
     * @param startSize 开始记录行数（0开始）
     * @param limitSize 每页限制记录数
     */
    public void appendPageSql(StringBuffer sql, Integer startSize, Integer limitSize) {
    	if (StringUtils.isNotBlank(sql) && null != startSize && null != limitSize){
    		if (Const.DB_NAME.equals("mysql")){
        		sql.append(" LIMIT " + startSize + "," + limitSize);
        	} else{
        		sql.insert(0, "SELECT * FROM (SELECT PAGE_VIEW.*, ROWNUM AS ROW_SEQ_NO FROM (");
                sql.append(") PAGE_VIEW WHERE ROWNUM <= " + (startSize + limitSize));
                sql.append(") WHERE ROW_SEQ_NO > " + startSize);
        	}
    	}
    }
    
    /**
     * <B>方法名称：</B>拼接分页语句<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param sql SQL语句
     * @param start 开始记录行数（0开始）
     * @param limit 每页限制记录数
     *//*
    public void appendPageSql(StringBuffer sql, int start, int limit) {
    	if (Const.DB_NAME.equals("mysql")){
    		sql.append(" LIMIT " + start + "," + (start + limit));
    	} else{
    		sql.insert(0, "SELECT * FROM (SELECT PAGE_VIEW.*, ROWNUM AS ROW_SEQ_NO FROM (");
            sql.append(") PAGE_VIEW WHERE ROWNUM <= " + (start + limit));
            sql.append(") WHERE ROW_SEQ_NO > " + start);
    	}
    }*/

    /**
     * <B>方法名称：</B>用于 in 通配符(?) 的拼接<BR>
     * <B>概要说明：</B>字段 in(?,?,?,?,?)<BR>
     * 
     * @param sql sql
     * @param sqlArgs 参数容器
     * @param params 参数的个数
     */
    public void appendSqlIn(StringBuffer sql, List<Object> sqlArgs, String[] params) {
        if (params != null && params.length > 0) {
            sql.append(" (");
            for (int i = 0; i < params.length; i++) {
                if (i == 0) {
                    sql.append("?");
                }
                else {
                    sql.append(",?");
                }
                sqlArgs.add(params[i]);
            }
            sql.append(") ");
        }
    }

    /**
     * <B>方法名称：</B>适应SQL列名<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param c 原列名
     * @return String 调整后列名
     */
    public static String c(String c) {
        if (StringUtils.isBlank(c)) {
            return null;
        }
        return c.trim().toUpperCase();
    }

    /**
     * <B>方法名称：</B>适应SQL参数<BR>
     * <B>概要说明：</B>防止SQL注入问题<BR>
     * 
     * @param v 参数
     * @return String 调整后参数
     */
    public static String v(String v) {
        if (StringUtils.isBlank(v)) {
            return null;
        }
        return v.trim().replaceAll("'", "''");
    }

    /**
     * <B>方法名称：</B>获取日期文本值<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param rs 结果集
     * @param column 列名
     * @return String 文本值
     * @throws SQLException SQL异常错误
     */
    protected String getDate(ResultSet rs, String column) throws SQLException {
        Date date = rs.getDate(column);
        if (date == null) {
            return null;
        }
        return DateFormatUtils.format(date, Const.FORMAT_DATE);
    }

    /**
     * <B>方法名称：</B>获取日期时间文本值<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param rs 结果集
     * @param column 列名
     * @return String 文本值
     * @throws SQLException SQL异常错误
     */
    protected String getDateTime(ResultSet rs, String column) throws SQLException {
        Date date = rs.getDate(column);
        if (date == null) {
            return null;
        }
        return DateFormatUtils.format(date, Const.FORMAT_DATETIME);
    }

    /**
     * <B>方法名称：</B>获取时间戳文本值<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param rs 结果集
     * @param column 列名
     * @return String 文本值
     * @throws SQLException SQL异常错误
     */
    protected String getTimestamp(ResultSet rs, String column) throws SQLException {
        Date date = rs.getDate(column);
        if (date == null) {
            return null;
        }
        return DateFormatUtils.format(date, Const.FORMAT_TIMESTAMP);
    }
    
    /**
     * <B>方法名称：</B>单表INSERT方法<BR>
     * <B>概要说明：</B>单表INSERT方法<BR>
     * @param tableName 表名
     * @param data JSONObject对象
     */
    protected int insert(String tableName, JSONObject data) {
        
    	if (data.size() <= 0) {
            return 0;
        }
    	
        StringBuffer sql = new StringBuffer();
        sql.append(" INSERT INTO ");
        sql.append(tableName + " ( ");
    	
    	Set<Entry<String, Object>> set = data.entrySet();
    	List<Object> sqlArgs = new ArrayList<Object>();
    	for (Iterator<Entry<String, Object>> iterator = set.iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = (Entry<String, Object>) iterator.next();
			sql.append(entry.getKey() + ",");
			sqlArgs.add(entry.getValue());
		}

        sql.delete(sql.length() - 1, sql.length());
        sql.append(" ) VALUES ( ");
        for (int i = 0; i < set.size(); i++) {
            sql.append("?,");
        }
        
        sql.delete(sql.length() - 1, sql.length());
        sql.append(" ) ");
        
        return this.getJdbcTemplate().update(sql.toString(), sqlArgs.toArray()); 
    }
    
    
    /**
     * <B>方法名称：</B>批量新增数据方法<BR>
     * <B>概要说明：</B><BR>
     * @param tableName 数据库表名称
     * @param list 插入数据集合
     */
    protected void insertBatch(String tableName, final List<LinkedHashMap<String, Object>> list) {
        
        if (list.size() <= 0) {
            return;
        }
        
        LinkedHashMap<String, Object> linkedHashMap = list.get(0);
        
        StringBuffer sql = new StringBuffer();
        sql.append(" INSERT INTO ");
        sql.append(tableName + " ( ");
        
        final String[] keyset =  (String[]) linkedHashMap.keySet().toArray(new String[linkedHashMap.size()]);
        
        for (int i = 0; i < linkedHashMap.size(); i++) {
            sql.append(keyset[i] + ",");
        }
        
        sql.delete(sql.length() - 1, sql.length());
       
        sql.append(" ) VALUES ( ");
        for (int i = 0; i < linkedHashMap.size(); i++) {
            sql.append("?,");
        }
        
        sql.delete(sql.length() - 1, sql.length());
        sql.append(" ) ");
        
        this.getJdbcTemplate().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                LinkedHashMap<String, Object>  map = list.get(i);
                Object[] valueset = map.values().toArray(new Object[map.size()]);
                int len = map.keySet().size();
                for (int j = 0; j < len; j++) {
                    ps.setObject(j + 1, valueset[j]);
                }
            }
            public int getBatchSize() {
                return list.size();
            }
      });
    } 
}