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

require_once "JMOpenApi.php";

 /**
  * Just for Test.
  */
class TestDemo extends PHPUnit_Framework_TestCase
{
    /**
     * Test for getlogist.
     * 
     * @return void
     */
    
    public function testGetLogist()
    {
        // Auth
        $test = new JMOpenAPI('2492','c23e916542d81afe429d91d5856b730b','00abede2bae6ebe0c3047b83e5ad75b48a7c111a');
        $res = $test->get('/Order/GetLogistics');
        $this->assertCount(91,$res['result']);
    }

    /**
     * Test for getOrder use method post.
     * 
     * @return void
     */
    
    public function testGetOrder()
    {
        $test = new JMOpenAPI('2492','c23e916542d81afe429d91d5856b730b','00abede2bae6ebe0c3047b83e5ad75b48a7c111a');
        $res = $test->post('/Order/GetOrder');
        $this->assertEquals(array('error' => 1,'message' => 'page error!'),$res);
    }

}
