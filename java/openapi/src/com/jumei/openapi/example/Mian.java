package com.jumei.openapi.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jumei.openapi.JMOpenAPIConst;
import com.jumei.openapi.JMOpenApi;

/**
 * For example.
 * Mian.java
 * 
 * @author xingchaof@jumei.com           
 *
 */
public class Mian {
	public static void main(String[] args) throws IOException {
		String clientId  = JMOpenAPIConst.CLIENT_ID;
		String clientKey = JMOpenAPIConst.CLIENT_KEY;
		String venderKey = JMOpenAPIConst.SIGN_KEY;
		//默认需要该三个参数进行验证，当然可以使用无参数构造，然后使用setter
		JMOpenApi api = new JMOpenApi(clientId,clientKey,venderKey);
		//JMOpenApi api = new JMOpenApi();
		//api.setClientId(clientId); //and so on.
		
		System.out.println("获取页面URL的origin：类似于javascript的[window.location.origin]");
		System.out.println(api.getBaseURL());
		
		System.out.println("api 演示：");
		//api 1
		System.out.println("获取聚美合作的快递合作商：");
		System.out.println(api.getLogistics());//api 1.
		
		//api 2
		System.out.println("通过id获取订单:");
		System.out.println(api.getOrderById(135944067));//api 2
		
		/**
		 * 接下来是需要参数的另外几个api.具体参考文档提供。
		 */
		//api 3
		System.out.println("批量获取订单：");
		Map<String,String> params = new HashMap<String,String>();
		//时间不是必须。但格式就是这样
		//params.put("start_date","2013-11-24 17:15:00");
		//params.put("end_date","2013-12-10 17:15:00");
		params.put("status","2");
		params.put("page", "1");
		params.put("page_size", "5");
		System.out.println(api.getOrder(params));//api 3
		
		//api 4
		System.out.println("备货：");
		String[] order_ids={"4353453543","3453465546"};//订单序列
		System.out.println(api.setOrderStock(order_ids));//api 4 
		
		//api 5
		System.out.println("订单发货：");
		String orderId= "32423423";//订单号
		String logisticId="31";//快递公司id(来自聚美快递列表).参见api 1
		String logisticTrackNo="3425324532453";//快递单号
		System.out.println(api.setShipping(orderId, logisticId, logisticTrackNo));//api 5
		
		//api 6 
		System.out.println("SKU库存同步接口");
		String upcCode="TSI34356";//upc_code
		String enableNum = "1234";//should be integer.
		System.out.println(api.syncStock(upcCode,enableNum));//api 6
		
	}
}
