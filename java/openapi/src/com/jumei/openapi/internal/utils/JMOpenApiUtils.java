package com.jumei.openapi.internal.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * JMOpenApiUtils.java
 * 
 * JMOpenAPI 2.0 SDK for java
 * 
 * @author xingchaof@jumei.com           
 *
 */
public class JMOpenApiUtils {
	/**
	 * 通过请求参数及其给定的apikey生成所对应的signKey.
	 * 
	 * @param params
	 * @param apiKey
	 * @return
	 */
	public static String generateSignKey(Map<String,String> params,String apiKey) {
		StringBuilder query = new StringBuilder();
		//首尾都需要添加
		if(apiKey!=null&&apiKey.length()!=0){
			query.append(apiKey);
		}
		Set<String> sets = params.keySet();
		List<String> lst = new ArrayList<String>(sets);
		Collections.sort(lst);//按照字典顺序排序
		try {
			for(String key:lst){
				String result = java.net.URLDecoder.decode(key.concat(params.get(key)), "UTF-8");
				query.append(result);
			}
		} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		//首尾都需要添加
		if(apiKey!=null&&apiKey.length()!=0){
			query.append(apiKey);
		}
		return MD5Encrpt(query.toString().getBytes()).toUpperCase();
	}
	/**
	 * Md5 加密
	 * @param data
	 * @return
	 */
	public static String MD5Encrpt(byte[] data){
		MessageDigest digest = null;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(data);
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		byte[] mb = digest.digest();
		StringBuffer hexSb = new StringBuffer();
		for(int i=0;i<mb.length;i++){
			String tmp = Integer.toHexString(0xff&mb[i]);
			if(tmp.length()==1)
				hexSb.append('0');
			hexSb.append(tmp);
		}
		return hexSb.toString();
	}
	
	/**
	 * 
	 * @param aArr
	 * @param sSep
	 * @return
	 */
	public static String arrayJoin(String[] aArr, String sSep) {
	    StringBuilder sbStr = new StringBuilder();
	    for (int i = 0, il = aArr.length; i < il; i++) {
	        if (i > 0)
	            sbStr.append(sSep);
	        sbStr.append(aArr[i]);
	    }
	    return sbStr.toString();
	}
	
}
