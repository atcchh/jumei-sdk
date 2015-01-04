package com.jumei.openapi.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jumei.openapi.JMOpenAPIConst;
import com.jumei.openapi.JMOpenAPIException;
import com.jumei.openapi.JMOpenApi;

/**
 * Java SDK for openapi2.0
 * 
 * 该类是根据聚美OpenAPI2.0文档写的,为了演示如何用JAVA代码调用相关接口。
 * 核心是计算sign的值，也就是
 * com.jumei.openapi.internal.utils.JMOpenApiUtils.generateSignKey
 * 方法。具体算法文档有提到。本次更新删除了部分不必要的代码。仅仅留下了极少的代码。
 * 最大程度的保持简单易懂。
 * 你需要在JMOpenAPIConst这个类里面配置相关信息，或者你可以使用java的
 * properties文件。来代替此配置。因此SDK目的是演示作用，并没有实现json转化为具
 * 体对象的相关api.如果需要操作，推荐使用gson或者fastJson等相关的json包作为处理
 * 整个JMOpenApi只有一个方法为api,你可以制定请求方式为GET/POST.如果某个api不具
 * 有参数，传入null即可。否则以map格式传入key-value对。你也可以不指定请求参数，
 * 则使用默认的方式(POST)。
 * 
 * @author xingchaof <xingchaof@jumei.com>
 */

public class Mian {
	public static void main(String[] args) throws IOException, JMOpenAPIException {
		String clientId  = JMOpenAPIConst.CLIENT_ID;
		String clientKey = JMOpenAPIConst.CLIENT_KEY;
		String venderKey = JMOpenAPIConst.SIGN_KEY;
		JMOpenApi openApi = new JMOpenApi(clientId,clientKey,venderKey);
		//==========演示===========
		String resp = openApi.api("/Order/GetLogistics", "GET", null);
		System.out.println(resp);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("order_id", "300131257");
		resp = openApi.api("/Order/GetOrderById", parameters);
		System.out.println(resp);
	}
}
