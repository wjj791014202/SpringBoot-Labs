package cn.iocoder.springcloudalibaba.labx22.consumerdemo;

import com.gray.GrayConstants;
import com.kucoin.starter.chaincontext.ChainRequestContext;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients(basePackages = {"cn.iocoder.springcloudalibaba.labx22.consumerdemo"})
@EnableDiscoveryClient
@EnableZuulProxy
public class DemoConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoConsumerApplication.class, args);
    }

    @RestController
    static class TestController {

        @Autowired
        private DiscoveryClient discoveryClient;
        @Autowired
        private RestTemplate restTemplate;
        @Autowired
        private LoadBalancerClient loadBalancerClient;

        @Resource
        DemoProviderFeign demoProviderFeign;

        @GetMapping("/hello")
        public String hello(String name) {
            // 获得服务 `demo-provider` 的一个实例
            ChainRequestContext.getCurrentContext().put(GrayConstants.DEFAULT_REQUEST_KEY, "btc");
//            ServiceInstance instance;
//            if (true) {
//                // 获取服务 `demo-provider` 对应的实例列表
//                List<ServiceInstance> instances = discoveryClient.getInstances("demo-provider");
//                // 选择第一个
//                instance = instances.size() > 0 ? instances.get(0) : null;
//            } else {
//                instance = loadBalancerClient.choose("demo-provider");
//            }
//            // 发起调用
//            if (instance == null) {
//                throw new IllegalStateException("获取不到实例");
//            }
//            String targetUrl = instance.getUri() + "/echo?name=" + name;
//            String response = restTemplate.getForObject(targetUrl, String.class);
            // 返回结果
            return "consumer:" + demoProviderFeign.echo(name);
        }

    }

}
