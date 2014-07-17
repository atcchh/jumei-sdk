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
	private  int timeout		= HTTPObject.TIME_OUT;
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
	/**
	 * 通过订单号获取单个订单的数据
	 * @param orderId
	 * @return
	 * @throws IOException
	 */
	public String getOrderById(long orderId) throws IOException{
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("order_id", orderId+"");
		parameters = getPreParameters(parameters);
		return HTTPObject.post(getCompleteUrl("/Order/GetOrderById"), parameters,getCharset(),getTimeout(),getConnectTimeout());//优先使用post
	}
	/**
	 * 通过给定的参数获取订单的数据
	 * @param parameters，参数应该至少包含：
	 * start_date，end_date,status,page,page_size等
	 * @throws IOException 请求网络可能在读取等问题
	 * @return，返回结果是一个json的字符串。
	 */
	public String getOrder(Map<String,String> parameters) throws IOException{
		parameters = getPreParameters(parameters);
		return HTTPObject.post(getCompleteUrl("Order/GetOrder"), parameters,getCharset(),getTimeout(),getConnectTimeout());
	}
	/**
	 * 获取聚美合作的快递合作商
	 * @return
	 * @throws IOException
	 * @throws JMOpenAPIException
	 */
	public String getLogistics() throws IOException{
		return HTTPObject.post(getCompleteUrl("Order/getLogistics"), getPreParameters(null),getCharset(),getTimeout(),getConnectTimeout());
	}
	/**
	 * 第三方ERP通过接口一获取订单成功后，返回给聚美，将订单修改为配货中状态（只将2状态的订单更新为7状态）。
	 * @param orderId 订单id(多个订单使用逗号隔开) 单此最多接受1000个订单批量备货
	 * @return
	 * @throws IOException
	 */
	public String setOrderStock(String[] orderId) throws IOException{
		Map<String,String> params = new HashMap<String,String>();
		params.put("order_ids",JMOpenApiUtils.arrayJoin(orderId, ","));
		return HTTPObject.post(getCompleteUrl("Order/SetOrderStock"), getPreParameters(params),getCharset(),getTimeout(),getConnectTimeout());
	}
	/**
	 * 第三方ERP通过该接口将已经发货订单的快递信息返回给聚美系统
	 * @param orderId 订单号
	 * @param logisticId 快递公司id(来自聚美快递列表)
	 * @param logisticTrackNo 快递单号
	 * @return
	 * @throws IOException 
	 */
	public String setShipping(String orderId,String logisticId,String logisticTrackNo) throws IOException{
		Map<String,String> params = new HashMap<String,String>();
		params.put("order_id", orderId);
		params.put("logistic_id",logisticId);
		params.put("logistic_track_no", logisticTrackNo);
		return HTTPObject.post(getCompleteUrl("Order/SetShipping"), getPreParameters(params),getCharset(),getTimeout(),getConnectTimeout());
	}
	/**
	 * 第三方ERP通过接口，实时更新某个sku的库存
	 * @param upcCode
	 * @param enableNum
	 * @return
	 * @throws IOException
	 */
	public String syncStock(String upcCode,String enableNum) throws IOException{
		Map<String,String> params = new HashMap<String,String>();
		params.put("upc_code", upcCode);
		params.put("enable_num",enableNum);
		return HTTPObject.post(getCompleteUrl("Stock/StockSync"), getPreParameters(params),getCharset(),getTimeout(),getConnectTimeout());
	
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
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
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
