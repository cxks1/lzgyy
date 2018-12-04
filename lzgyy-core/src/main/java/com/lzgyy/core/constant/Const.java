package com.lzgyy.core.constant;

public final class Const {

    /**
     * <B>构造方法</B><BR>
     */
    private Const() {
    }
    
    /** 判断代码：是 */
    public static final String TRUE = "1";

    /** 判断代码：否 */
    public static final String FALSE = "0";
    
    /** 0数字 */
    public static final Integer ZERO = 0;

    /** 通用字符集编码 */
    public static final String CHARSET_UTF8 = "UTF-8";

    /** 中文字符集编码 */
    public static final String CHARSET_CHINESE = "GBK";

    /** 英文字符集编码 */
    public static final String CHARSET_LATIN = "ISO-8859-1";

    /** NULL字符串 */
    public static final String NULL = "null";

    /** 日期格式 */
    public static final String FORMAT_DATE = "yyyy-MM-dd";

    /** 日期时间格式 */
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /** 时间戳格式 */
    public static final String FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";
    
    /** 数据库默认方言 */
  	public static final String DB_NAME = "mysql";
  	
  	/** 分页 */
  	/** 开始记录页数 **/
  	public static final String PAGE_STARTPAGE = "startPage";
  	
  	/** 每页限制记录数 **/
  	public static final String PAGE_LIMITSIZE = "limitSize";
  	
  	/** 开始记录行数 */
  	public static final String PAGE_STARTSIZE = "startSize";
  	
  	/** 记录总页数 */
  	public static final String PAGE_TOTALPAGE = "totalPage";
  	
  	/** 记录总数 */
  	public static final String PAGE_TOTAL = "total";
  	
}