package com.jumei.openapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jumei.openapi.http.HTTPObject;
import com.jumei.openapi.internal.utils.JMOpenApiUtils;

/**
 * 
 * JMOpenApi.java
 * 
 * @author xingchaof@jumei.com           
 * JMOpenAPI的主类
 */
public class JMOpenApi {
	private  String baseURL 	= JMOpenAPIConst.BASE_URL;
	private  String clientKey   = JMOpenAPIConst.CLIENT_KEY;
	private  String signKey 	= JMOpenAPIConst.SIGN_KEY;
	private  String clientId    = JMOpenAPIConst.CLIENT_ID;
	private  String charset     = JMOpenAPIConst.DEFAULT_CHARSET;
	private  int readTimeout		= HTTPObject.READ_TIME_OUT;
	private  int connectTimeout	= HTTPObject.CONNECT_TIME_OUT;
	
	public JMOpenApi(){
		
	}
	public JMOpenApi(String clientId,String clientKey,String venderKey){
		if(clientKey==null||clientId==null||venderKey==null||clientId.equals("")||clientKey.equals("")||venderKey.equals("")){
			try {
				throw new JMOpenAPIException("client_id,client_key and venderKey must not be null or empty！");
			} catch (JMOpenAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.clientId = clientId;
		this.clientKey = clientKey;
		this.signKey = venderKey;
	}

	public String getCompleteUrl(String url){
		if(url.toLowerCase().startsWith("http"))//http:// or https://
			return url;
		return getBaseURL().concat(url);
	}
	private  Map<String, String> getPreParameters(Map<String,String> parameters){
		if(parameters==null)
			parameters = new HashMap<String,String>();
		parameters.put("client_id", getClientId());
		parameters.put("client_key", getClientKey());
		parameters.put("sign", JMOpenApiUtils.generateSignKey(parameters, getSignKey()));
		return parameters;
	}
	
	public String api(String apiName, Map<String,String> parameters) throws IOException, JMOpenAPIException{
		String currentURL = this.getCompleteUrl(apiName);
		return this.api(currentURL, "POST", parameters);
	}
	
	public String api(String apiName, String method, Map<String,String> parameters) throws IOException, JMOpenAPIException{
		parameters = this.getPreParameters(parameters);
		String currentURL = this.getCompleteUrl(apiName);
		String resp = null;
		if (method.equalsIgnoreCase("GET")) {
			resp = HTTPObject.get(currentURL, parameters, charset, connectTimeout, readTimeout);
		} else if (method.equalsIgnoreCase("POST")){
			resp = HTTPObject.post(currentURL, parameters, charset, connectTimeout, readTimeout);
		} else {
			throw new JMOpenAPIException("No support method!");
		}
		return resp;
	}
	public void setBaseURL(String baseURL){
		this.baseURL = baseURL;
	}
	public String getBaseURL() {
		return baseURL;
	}
	public String getClientKey() {
		return clientKey;
	}
	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}
	public String getSignKey() {
		return signKey;
	}
	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public int getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
}
