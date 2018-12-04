package com.lzgyy.common.codec;

import java.security.MessageDigest;
/**
 * MD5加解密
 * @author yuezx
 * @date 2015-12-2 
 */
public class Md5Utils {
	
	/*** 
     * MD5加码 生成32位md5码 (不建议使用,没有指定编码)
     */  
    public static String MD5Encode(String inStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++)  
            byteArray[i] = (byte) charArray[i];  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16)  
                hexValue.append("0");  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
    }
    
    
    /**
     * 生成指定字符编码的MD5加密的字符串
     * @param inStr			输入字符串
     * @param charsetname	字符编码
     * @return
     */
    public static String MD5Encode(String inStr, String charsetname) {
		String resultString = null;
		try {
			resultString = new String(inStr);
			MessageDigest md = MessageDigest.getInstance("MD5");
			if (charsetname == null || "".equals(charsetname))
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes()));
			else
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charsetname)));
		} catch (Exception exception) {
		}
		return resultString;
	}
    
    private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			resultSb.append(byteToHexString(b[i]));
		return resultSb.toString();
	}
    
    private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
    
    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
    		"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    
    public static void main(String args[]){
    	System.out.println(MD5Encode("superadmin你好"));
    	System.out.println(MD5Encode("superadmin你好","UTF-8"));
    	System.out.println(MD5Encode("superadmin你好","ISO-8859-1"));
    	System.out.println(MD5Encode("superadmin你好","unicode"));
    	System.out.println(MD5Encode("superadmin你好","ASCII"));
    	System.out.println(MD5Encode("superadmin你好","GB2312"));
    	System.out.println(MD5Encode("superadmin你好","GBK"));
    	System.out.println(MD5Encode("superadmin你好","Big5"));
    }
}
