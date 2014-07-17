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
        $test = new JMOpenAPI('72','2ffd7cbdbe876867c987373f3d367f0f','a5103842f0e8d43c17c70a14b6aec80f0008a567');
        $res = $test->get('Order/GetLogistics');
        $this->assertArrayHasKey('error',$res);
        $this->assertCount(91,$res['result']);
    }

    /**
     * Test for getOrder use method post.
     * 
     * @return void
     */
    
    public function testGetOrder()
    {
        $test = new JMOpenAPI('72','2ffd7cbdbe876867c987373f3d367f0f','a5103842f0e8d43c17c70a14b6aec80f0008a567');
        $res = $test->post('Order/GetOrder');
        $this->assertEquals(array('error'=>1,'message'=>'page error!'),$res);
    }

    /**
     * Test Exceptions.
     * 
     * @expectedException JMOpenApiException
     *
     * @return void
     */
    public function testJMOpenApiException()
    {
        $test = new JMOpenAPI('72','2ffd7cbdbe876867c987373f3d367f0f','a5103842f0e8d43c17c70a14b6aec80f0008a567');
        $res = $test->delete('Order/GetLogistics');
        $this->assertNull("Should not be here"); // should not be go here
    }

}
