package com.lzgyy.plugins.iot.service.mqtt.auth.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzgyy.plugins.iot.core.auth.service.IAuthService;
import com.lzgyy.plugins.iot.service.mqtt.broker.config.BrokerProperties;

import javax.annotation.PostConstruct;
import java.security.interfaces.RSAPrivateKey;

/**
 * 用户名和密码认证服务
 */
@Service
public class AuthService implements IAuthService {

	private RSAPrivateKey privateKey;
	
	/**
	 * 服务配置
	 */
	@Autowired
	private BrokerProperties brokerProperties;

	@Override
	public boolean checkValid(String username, String password) {
		if (StrUtil.isBlank(username)) return false;
		if (StrUtil.isBlank(password)) return false;
		RSA rsa = new RSA(privateKey, null);
		String value = rsa.encryptBcd(username, KeyType.PrivateKey);
		return value.equals(password) ? true : false;
	}

	@PostConstruct
	public void init() {
		privateKey = IoUtil.readObj(AuthService.class.getClassLoader().getResourceAsStream(brokerProperties.getAuthPrivateKeyFile()));
	}

}