package cn.iocoder.springboot.lab39.skywalkingdemo.consumerdemo.controller;

import io.opentracing.Span;
import io.opentracing.Tracer;
import javax.validation.valueextraction.ExtractedValue;
import org.apache.skywalking.apm.toolkit.opentracing.SkywalkingTracer;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @GetMapping("/opentracing")
    public String echo() {
        // 创建一个 Span
        Tracer tracer = new SkywalkingTracer();
        Span parent=tracer.buildSpan("custom_operation").withTag("mp", "芋道源码").startManual();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Span child=tracer.buildSpan("custom_operation1").withTag("组件", "芋道源码").withTag("mp", "芋道源码").asChildOf(parent).startManual();
        child.finish();
        parent.finish();
        // 返回
        test();
        test1();
        return "opentracing";
    }
    
    @Trace
    private void test(){
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void test1(){
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
