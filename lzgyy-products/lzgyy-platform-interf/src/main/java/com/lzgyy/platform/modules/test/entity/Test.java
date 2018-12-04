package com.lzgyy.platform.modules.test.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data /** @ToString, @EqualsAndHashCode, 所有属性的@Getter, 所有non-final属性的@Setter和@RequiredArgsConstructor的组合，通常情况下，我们使用这个注解就足够了 */
//@AllArgsConstructor /** 全参构造器 */
//@NoArgsConstructor  /** 无参构造器 */
public class Test implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/** 主键 **/
	private Long id;
	/** 名称 **/
	private String name;
	/** 创建人 **/
	private Long createUser;
	/** 创建时间 **/
	private Date createDate;
	/** 更新人 **/
	private Long updateUser;
	/** 更新时间 **/
	private Date updateDate;
	/** 删除标识 **/
	private String deleteState;

}