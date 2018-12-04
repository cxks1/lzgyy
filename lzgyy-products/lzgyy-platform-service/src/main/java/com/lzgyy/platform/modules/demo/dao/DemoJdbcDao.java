package com.lzgyy.platform.modules.demo.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSONObject;
import com.lzgyy.core.dao.jdbc.BaseJdbcDao;

@Repository("demoJdbcDao")
public class DemoJdbcDao extends BaseJdbcDao {
	
	/** 表名 **/
	private static final String SQL_TABLE_NAME = "demo";
	/** 主键 **/
	private static final String SQL_TABLE_COLUMNS_PRIMARY_KEY = "id"; 
	/** 列名 **/
	//private static final String[] SQL_TABLE_COLUMNS_ARR = new String[]{"id", "name", "create_user", "create_date", "update_user", "update_date", "delete_state"};
	private static final String[] SQL_TABLE_COLUMNS_ARR = new String[]{"id", "name", "create_user", "update_user", "delete_state"};
	private static final String SQL_TABLE_COLUMNS = StringUtils.join(SQL_TABLE_COLUMNS_ARR, ",");
	/** 查询 **/
	private static final String SQL_SELECT_DEMO = "SELECT " + SQL_TABLE_COLUMNS + " FROM " + SQL_TABLE_NAME;
	
	public List<JSONObject> get(JSONObject jsonParam){
		
		StringBuffer sql = new StringBuffer();
		sql.append(SQL_SELECT_DEMO + " WHERE 1 = 1 ");
		List<Object> sqlArgs = new ArrayList<Object>();
		addWhereCondition(sql, sqlArgs, jsonParam);
		return this.queryForJsonList(sql.toString(), sqlArgs.toArray());
	}
	
	/**
     * <B>方法名称：</B>查询列表<BR>
     * <B>概要说明：</B><BR>
     * 
     * @param jsonParam
     * @param startSize
     * @param limitSize
     * @param sql SQL语句
     * @param args 参数
     * @return JSONObject JSON数据
     */
	public List<JSONObject> getList(JSONObject jsonParam, Integer startSize, Integer limitSize){
		
		StringBuffer sql = new StringBuffer();
		sql.append(SQL_SELECT_DEMO + " WHERE 1 = 1 ");
		List<Object> sqlArgs = new ArrayList<Object>();
		addWhereCondition(sql, sqlArgs, jsonParam);
		super.appendPageSql(sql, startSize, limitSize);
		return this.queryForJsonList(sql.toString(), sqlArgs.toArray());
	}
	
	private void addWhereCondition(StringBuffer sql, List<Object> sqlArgs, JSONObject jsonParam) {
        
		if (jsonParam != null && jsonParam.size() > 0){
			if (SQL_TABLE_COLUMNS_ARR != null && SQL_TABLE_COLUMNS_ARR.length > 0){
				for (int i = 0; i < SQL_TABLE_COLUMNS_ARR.length; i ++){
					if (StringUtils.isNotBlank(jsonParam.getString(SQL_TABLE_COLUMNS_ARR[i]))){
						sql.append(" AND " + SQL_TABLE_COLUMNS_ARR[i] + " = ? ");
						sqlArgs.add(jsonParam.getString(SQL_TABLE_COLUMNS_ARR[i]));
					}
				}
			}
		}
	}

	public int getTotal(JSONObject jsonParam){
		
        StringBuffer sql = new StringBuffer();
		sql.append(" SELECT COUNT(*) FROM " + SQL_TABLE_NAME + " WHERE 1 = 1 ");
        List<Object> sqlArgs = new ArrayList<Object>();
        addWhereCondition(sql, sqlArgs, jsonParam);
        return super.getJdbcTemplate().queryForObject(sql.toString(), Integer.class, sqlArgs.toArray());		
	}
	
	public int insert(JSONObject json) throws Exception{
		
		StringBuffer sql = new StringBuffer();
		sql.append(" INSERT INTO " + SQL_TABLE_NAME + " ( ")
		   .append(SQL_TABLE_COLUMNS)
		   .append(" ) VALUES ( ");
		if (SQL_TABLE_COLUMNS_ARR != null && SQL_TABLE_COLUMNS_ARR.length > 0){
			for (int i = 0; i < SQL_TABLE_COLUMNS_ARR.length; i ++){
			   sql.append(SQL_TABLE_COLUMNS_ARR[i]);
			   if (i <= SQL_TABLE_COLUMNS_ARR.length-1){
				   sql.append(" , ");
			   }
			}
		}
		sql.append(" ) ");
		
		List<Object> argsList = new ArrayList<Object>();
		if (SQL_TABLE_COLUMNS_ARR != null && SQL_TABLE_COLUMNS_ARR.length > 0){
		   for (int i = 0; i < SQL_TABLE_COLUMNS_ARR.length; i ++){
			   argsList.add(json.getString(SQL_TABLE_COLUMNS_ARR[i]));
		   }
		}
		
        Object[] args = argsList.toArray();
        return super.getJdbcTemplate().update(sql.toString() , args);
	}
	
    public int update(JSONObject json) throws Exception {
    	
    	StringBuffer sql = new StringBuffer();
    	
    	List<Object> argsList = new ArrayList<Object>();
    	// 获取有效列名
    	List<String> validColumnsList = new ArrayList<String>();
    	if (SQL_TABLE_COLUMNS_ARR != null && SQL_TABLE_COLUMNS_ARR.length > 0){
		   for (int i = 0; i < SQL_TABLE_COLUMNS_ARR.length; i ++){
			   if (StringUtils.isNotBlank(json.getString(SQL_TABLE_COLUMNS_ARR[i]))){
				   validColumnsList.add(SQL_TABLE_COLUMNS_ARR[i]);
				   argsList.add(json.getString(SQL_TABLE_COLUMNS_ARR[i]));
			   }
		   }
		}
    	
    	sql.append(" UPDATE " + SQL_TABLE_NAME);
    	if (validColumnsList != null && validColumnsList.size() > 0){
		   for (int i = 0; i < validColumnsList.size(); i ++){
			   sql.append(validColumnsList.get(i) + " = ? ");
		   }
		}
    	sql.append(" WHERE " + SQL_TABLE_COLUMNS_PRIMARY_KEY + " = ? " );
    	argsList.add(json.getString(SQL_TABLE_COLUMNS_PRIMARY_KEY));
    	
        Object[] args = argsList.toArray();
        return super.getJdbcTemplate().update(sql.toString() , args);
    }

	public JSONObject get(Integer id) {
		
		return this.queryForJsonObject(SQL_SELECT_DEMO + " WHERE " + SQL_TABLE_COLUMNS_PRIMARY_KEY + " = ? ", id);
	}
	
	public int deletePhysical(List<Integer> idsList) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE FROM " + SQL_TABLE_NAME + " WHERE " + SQL_TABLE_COLUMNS_PRIMARY_KEY + " IN ( ");
		if (idsList != null && idsList.size() > 0){
			for (int i = 0; i < idsList.size() ; i++){
				sql.append("'" + idsList.get(i) + "'");
				if (i <= idsList.size()-1){
					sql.append(",");
				}
			}
		}
		sql.append(" ) ");
		return super.getJdbcTemplate().update(sql.toString(), idsList.toArray(new Integer[]{})); 
	}
}