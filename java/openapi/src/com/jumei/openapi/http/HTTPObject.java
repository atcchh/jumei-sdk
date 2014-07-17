package com.jumei.openapi.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jumei.openapi.JMOpenAPIConst;

/**
 * 
 * HTTPObject.java
 * 
 * @author xingchaof@jumei.com           
 *
 */
public class HTTPObject {
	public final static int TIME_OUT=30;
	public final static int CONNECT_TIME_OUT=30;
	/**
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String post(String url,Map<String, String> params) throws IOException{
		return post(url,params,JMOpenAPIConst.DEFAULT_CHARSET,TIME_OUT,CONNECT_TIME_OUT);
	}
	/**
	 * 
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String post(String url,Map<String, String> params,String charset) throws IOException{
		return post(url,params,charset,TIME_OUT,CONNECT_TIME_OUT);
	}
	/**
	 * 
	 * @param url
	 * @param params
	 * @param charset
	 * @param connectTimeout
	 * @param readTimeout
	 * @return
	 * @throws IOException
	 */
	public static String post(String url,Map<String, String> params,String charset,int connectTimeout,int readTimeout) throws IOException{
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		HttpURLConnection conn = null;
		try {
			conn = getConnection(new URL(url), "POST", ctype);
			conn.connect();
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		DataOutputStream out = new DataOutputStream(
				conn.getOutputStream());
		out.writeBytes(urlEncode(params));
		out.flush();
		out.close();
		String response = getResponseAsString(conn,charset);
		if(conn!=null)
			conn.disconnect();
		return response;
	}
	/**
	 * 
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String get(String url,Map<String, String> params) throws IOException{
		return get(url,params,JMOpenAPIConst.DEFAULT_CHARSET);
	}
	/**
	 * 
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String get(String url,Map<String, String> params,String charset) throws IOException{
		return get(url,params,charset,TIME_OUT,CONNECT_TIME_OUT);
	}
	/**
	 * 
	 * @param url
	 * @param params
	 * @param charset
	 * @param readTimeout
	 * @param connectTimeout
	 * @return
	 * @throws IOException
	 */
	public static String get(String url,Map<String, String> params,String charset,int readTimeout,int connectTimeout) throws IOException{
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		HttpURLConnection conn = null;
		try {
			conn = getConnection(new URL(getGetURL(url, params)), "POST", ctype);
			conn.connect();
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw e;
		}
		String response = getResponseAsString(conn, charset);
		if(conn!=null)
			conn.disconnect();
		return response;
	}
	/**
	 * 获取返回流
	 * @param conn HttpURLConnection Object.
	 * @param charset the charset
	 * @return String
	 * @throws IOException
	 */
	private static String getResponseAsString(HttpURLConnection conn,String charset) throws IOException {
		if(charset==null){
			charset=JMOpenAPIConst.DEFAULT_CHARSET;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream(),charset));
		String lines = null;
		StringBuffer sb = new StringBuffer();
		while ((lines = reader.readLine()) != null) {
			sb.append(lines);
		}
		if(reader!=null)
			reader.close();
		return sb.toString();
	}
	
	/**
	 * 讲URL及其参数组装为完整的请求url
	 * @param url
	 * @param parameters
	 * @return
	 */
	public static String getGetURL(String url, Map<String, String> parameters) {
		if(parameters==null){
			return url;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(url);
		sb.append("?");
		sb.append(urlEncode(parameters));
		return sb.toString();
	}
	/**
	 * 组装参数
	 * @param parameters
	 * @return
	 */
	public static String urlEncode(Map<String, String> parameters) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : parameters.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(urlEncode(key, value));
			sb.append("&");
		}
		// 如果给定的参数不为空，那么最后一次还补上的&就是多余的，需要删除
		if (!parameters.isEmpty()) {
			sb.deleteCharAt(sb.lastIndexOf("&"));// sb.length()-1;
		}
		return sb.toString();
	}
	/**
	 * 组装参数
	 * @param name
	 * @param value
	 * @return
	 */
	public static String urlEncode(String name, String value) {
		StringBuffer queryBuffer = new StringBuffer();
		try {
			queryBuffer.append(URLEncoder.encode(name, "UTF-8"));
			queryBuffer.append("=");
			queryBuffer.append(URLEncoder.encode(value, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryBuffer.toString();
	}
	/**
	 * 把url的请求参数替换为key=>value对
	 * @param query
	 * @return
	 */
	public Map<String, String> splitUrlQuery(String query) {
		Map<String, String> result = new HashMap<String, String>();
		String[] pairs = query.split("&");
		if (pairs != null && pairs.length > 0) {
			for (String pair : pairs) {
				String[] param = pair.split("=", 2);
				if (param != null && param.length == 2) {
					result.put(param[0], param[1]);
				}
			}
		}
		return result;
	}
	/**
	 *  获取到Connection对象
	 * @param url
	 * @param method
	 * @param ctype
	 * @return
	 * @throws IOException
	 */
	private static HttpURLConnection getConnection(URL url, String method, String ctype)
			throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html,text/json");
		conn.setRequestProperty("User-Agent", "JMOpenAPI 2.0 SDK for Java");
		if(ctype!=null)
			conn.setRequestProperty("Content-Type", ctype);
		return conn;
	}
}
