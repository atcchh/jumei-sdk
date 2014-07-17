<?php
/**
 * PHP SDK for openapi2.0
 *
 * more details see:http://openapi.ext.jumei.com/
 *
 * PHP version 5
 *
 * Date : 2014.7.8
 *
 * @author xingchaof <xingchaof@jumei.com>
 */

require_once 'JMOpenApiException.php';

/**
 * An Simple JMOpenApi SDK for php .
 */
class JMOpenApi
{
    public $client_id;
    public $client_key;
    public $sign_key;
    public $base_url = 'http://www.openapi.com/'; // for test.
    public $http_code;
    public $time_out = 30;
    public $connect_timeout = 30;
    public $user_agent = 'JMOpenAPI 2.0';
    public $debug = false; // for debug

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
        print_r($response);
        return json_decode($response,true);
    }

    /**
     * DELETE wrapper for JMOpenApi.
     *
     * @param string $url        The url to delete.
     * @param array  $parameters The parameters.
     *
     * @return array             The response from server.
     */
    public function delete($url, array $parameters = array())
    {
        $response = $this->authRequest($url,'DELETE',$parameters);
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
    public function authRequest($url, $method, array $parameters = array())
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
            case 'DELETE':
                $body = http_build_query($parameters);
                return $this->request($url,'DELETE',$body);
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
    public function request($url, $method, $postdata = null, array $headers = array())
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
            case 'DELETE':
                throw new JMOpenApiException("No Support method");
                /*
                 if (!empty($postdata)) {
                     curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'DELETE');
                     $url = $url.'?'.$postdata;
                 }*/
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

    /**
     * 获取订单列表.
     *
     * @param integer $order_id The order id.
     *
     * @return array             The order details.
     */
    public function getOrderById($order_id)
    {
        return $this->post('Order/GetOrderById',array('order_id' => $order_id));
    }

    /**
     * 通过id获取订单的details.
     *
     * @param array $params The orders.
     *
     * @return array        The orders list.
     */
    public function getOrder(array $params = array())
    {
        return $this->post('Order/GetOrder',$params);
    }

    /**
     * 获取聚美合作的快递合作商.
     *
     * @return array The logistics list.
     */
    public function getLogistics()
    {
        return $this->post('Order/getLogistics');
    }

    /**
     * 第三方ERP通过接口一获取订单成功后，返回给聚美，将订单修改为配货中状态（只将2状态的订单更新为7状态）.
     *
     * @param array $param The parameters.
     *
     * @return array       The result.
     */
    public function setOrderStock(array $param = array())
    {
        return $this->post('Order/SetOrderStock',$param);
    }

    /**
     * 第三方ERP通过该接口将已经发货订单的快递信息返回给聚美系统.
     *
     * @param array $params The orderId,logisticId,logisticTrackNo.
     *
     * @return array The result
     */
    public function setShipping(array $params = array())
    {
        return $this->post('Order/SetShipping',$params);
    }

    /**
     * 第三方ERP通过接口，实时更新某个sku的库存.
     *
     * @param array $params The upc_code and enable_nubmer
     *
     * @return array        The result.
     */
    public function syncStock(array $params = array())
    {
        return $this->post('Stock/StockSync',$params);
    }

}
