package com.lzgyy.core.dao;

import java.util.List;

/**
 * Dao支持类实现
 * @ClassName: CrudDao 
 * @author yuezx
 * @date 2016-08-13 下午2:59:48 
 * @param <T>
 */
public interface CrudDao<T>{

	/**
	 * 新增数据
	 * @param entity
	 * @return
	 */
	public int insert(T entity);
	
	/**
	 * 批量新增
	 * @param entitys
	 * @return
	 */
	public int insertBatch(List<T> entitys);
	
	/**
	 * 更新数据
	 * @param entity
	 * @return
	 */
	public int update(T entity);
	
	/**
	 * 批量更新
	 * @param entitys
	 * @return
	 */
	public int updateBatch(List<T> entitys);
	
	/**
	 * 恢复数据
	 * @param idsList
	 * @return
	 */
	public int updateIdsRestore(List<String> idsList);
	
	/**
	 * 获取单条数据
	 * @param id
	 * @return
	 */
	public T get(String id);
	
	/**
	 * 获取单条数据
	 * @param entity
	 * @return
	 */
	public T get(T entity);
	
	/**
	 * 查询所有数据列表
	 * @param entity
	 * @return
	 */
	public List<T> findList(T entity);
	
	
	/**
	 * 删除单条数据（逻辑删除）
	 * @param id
	 * @return
	 */
	public int delete(String id);
	
	/**
	 * 删除单条数据（逻辑删除）
	 * @param entity
	 * @return
	 */
	public int delete(T entity);
	
	/**
	 * 删除多条数据（逻辑删除）
	 * @param idsList
	 * @return
	 */
	public int deleteIds(List<String> idsList);
	
	/**
	 * 物理删除多条数据
	 * @param idsList
	 * @return
	 */
	public int deleteIdsPhysical(List<String> idsList);
	
}