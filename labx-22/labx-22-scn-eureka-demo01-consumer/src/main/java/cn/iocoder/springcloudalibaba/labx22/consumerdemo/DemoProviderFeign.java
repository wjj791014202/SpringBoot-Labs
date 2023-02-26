package cn.iocoder.springcloudalibaba.labx22.consumerdemo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "demo-provider")
public interface DemoProviderFeign {

    @GetMapping("/echo")
    String echo(@RequestParam("name") String name);
}
