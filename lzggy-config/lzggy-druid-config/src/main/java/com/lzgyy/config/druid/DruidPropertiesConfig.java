package com.lzgyy.config.druid;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * druid配置
 */
@ConfigurationProperties(prefix = "druid.access")
public class DruidPropertiesConfig {

	private String whitelist;
	private String username;
	private String password;
	private String logSlowSql;
	private String resetEnable;
	
	public String getWhitelist() {
		return whitelist;
	}
	public void setWhitelist(String whitelist) {
		this.whitelist = whitelist;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLogSlowSql() {
		return logSlowSql;
	}
	public void setLogSlowSql(String logSlowSql) {
		this.logSlowSql = logSlowSql;
	}
	public String getResetEnable() {
		return resetEnable;
	}
	public void setResetEnable(String resetEnable) {
		this.resetEnable = resetEnable;
	}
}