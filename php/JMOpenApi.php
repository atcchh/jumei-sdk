<?php
/**
 * PHP SDK for openapi2.0
 * 
 * 该类是根据聚美OpenAPI2.0文档写的,为了演示如何用PHP代码调用相关接口。
 * 核心是计算sign的值，也就是generateSign方法。具体算法文档有提到，代
 * 码依赖PHP的curl扩展和json扩展，否则可能不能使用。本次更新删除了部分
 * 不必要的代码。仅仅留下了极少的代码。最大程度的保持简单。(所以相关属性
 * 的getter/setter也没写了。  )
 * get/post只是为了实现一个wrapper出来给用户用。实际上他们也是不必要的。
 * 你可以直接使用authRequest方法指定接口名称，请求方法和请求参数列表。
 * 比如:
 *     authReqeust('/Order/GetLogistics','GET',array())
 * 就表示获取接口二:获取聚美合作的快递信息.
 * 优先推荐使用POST方式。所以总是建议使用post这个wrapper.
 *
 * @author xingchaof <xingchaof@jumei.com>
 */

require_once 'JMOpenApiException.php';

/**
 * An Simple JMOpenApi SDK for php .
 */
class JMOpenApi
{
    private $client_id;
    private $client_key;
    private $sign_key;
    private $base_url = 'http://www.openapi.com/'; // for test.
    private $time_out = 30;
    private $connect_timeout = 30;
    private $user_agent = 'JMOpenAPI 2.0';
    private $debug = false; // for debug

    /**
     * The construct for JMOpenApi.
     *
     * @param string $client_id  The client_id.
     * @param string $client_key The client_id.
     * @param string $sign       The sign_key.
     */
    public function __construct($client_id, $client_key, $sign)
    {
        $this->client_id  = $client_id;
        $this->client_key = $client_key;
        $this->sign_key   = $sign;
    }

    /**
     * GET wrapper for JMOpenApi.
     *
     * @param string $url        The url to get.
     * @param array  $parameters The parameters.
     *
     * @return array             The response from server.
     */
    public function get($url, array $parameters = array())
    {
        $response = $this->authRequest($url,'GET',$parameters);
        return json_decode($response,true);
    }

    /**
     * POST wrapper for JMOpenApi.
     *
     * @param string $url        The url to post.
     * @param array  $parameters The parameters.
     *
     * @return array             The response from server.
     */
    public function post($url, array $parameters = array())
    {
        $response = $this->authRequest($url,'POST',$parameters);
        return json_decode($response,true);
    }

    /**
     * Prepare parser the request.
     *
     * @param string $url        The url to request.
     * @param string $method     Such as post,get,delete,put and so on.
     * @param array  $parameters The parameters.
     *
     * @return mixed    $response       The responses.
     * @throws JMOpenApiException If method doese not support.
     */
    public function authRequest($url, $method = 'GET', array $parameters = array())
    {
        if (strrpos($url, 'http://') !== 0 && strrpos($url, 'https://') !== 0 ) {
            $url = $this->base_url.$url;
        }
        $parameters['client_key'] = $this->client_key;
        $parameters['client_id'] = $this->client_id;
        $parameters['sign'] = $this->generateSign($parameters,$this->sign_key);
        switch (strtoupper($method)) {
            case 'GET':
                $url = $url.'?'.http_build_query($parameters);
                return $this->request($url,'GET');
                break;
            case 'POST':
                $body = http_build_query($parameters);
                return $this->request($url,'POST',$body);
                break;
            default:
                throw new JMOpenApiException('No support method');
                // return $this->request($url,$method)
                break;
        }
    }

    /**
     * Make the HTTP request.
     *
     * @param string $url      The url to request.
     * @param string $method   The method name.
     * @param string $postdata The parameters.
     * @param array  $headers  The headers.
     *
     * @return mixed    $response   The responses.
     * @throws JMOpenApiException    If method does not support.
     */
    private function request($url, $method, $postdata = null, array $headers = array())
    {
        // curl settings for http
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_HTTP_VERSION, CURL_HTTP_VERSION_1_0);
        curl_setopt($ch, CURLOPT_USERAGENT, $this->user_agent);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, $this->connect_timeout);
        curl_setopt($ch, CURLOPT_TIMEOUT, $this->time_out);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_ENCODING, 'utf8');

        // parse for those wrapper
        switch (strtoupper($method)) {
            case 'GET':
                break;
            case 'POST':
                if (!empty($postdata)) {
                    curl_setopt($ch, CURLOPT_POST, true);
                    curl_setopt($ch, CURLOPT_POSTFIELDS, $postdata);
                }
                break;
            default:
                throw new JMOpenApiException('No support method');
                break;
        }

        curl_setopt($ch, CURLOPT_URL, $url);
        // curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        // curl_setopt($ch, CURLINFO_HEADER_OUT, true);
        $response = curl_exec($ch);
        if ($this->debug) {
            echo 'post data:\r\n';
            print_r($postdata);
            echo 'reqsonse:\r\n';
            print_r($response);
        }
        curl_close($ch);
        return $response;
    }

    /**
     * To genarate a sign_key.given by Jumei.com.
     *
     * @param array  $parameters The params.
     * @param string $vender_key The raw keys.
     *
     * @return string The signkey.
     */
    private function generateSign(array $parameters = array(), $vender_key = null)
    {
        ksort($parameters);
        $strtosigned = $vender_key;
        foreach ($parameters as $k => $v) {
            $strtosigned .= urldecode($k.$v);
        }
        unset($k, $v);
        $strtosigned .= $vender_key;
        return strtoupper(md5($strtosigned));
    }

}
