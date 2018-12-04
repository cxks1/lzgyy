package com.lzgyy.plugins.iot.service.mqtt.auth.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

import java.io.InputStream;
import java.security.PrivateKey;
import java.util.Scanner;

/**
 * 密码
 */
public class PwdUtil {
	
	/**
	 * 通过用户名和私钥生成密码
	 */
	public static void main(String[] args) {
		System.out.println();
		System.out.print("输入需要获取密码的用户名: ");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String value = scanner.nextLine();
		InputStream is = PwdUtil.class.getClassLoader().getResourceAsStream("keystore/auth-private.key");
		PrivateKey privateKey = IoUtil.readObj(is);
		RSA rsa = new RSA(privateKey, null);
		System.out.println("用户名: " + value + " 对应生成的密码为: " + rsa.encryptBcd(value, KeyType.PrivateKey));
		
	}
	
}