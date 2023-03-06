package com.kucoin.starter.eureka.endpoint;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.discovery.EurekaClient;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.cloud.netflix.eureka.CloudEurekaInstanceConfig;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaAutoServiceRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestControllerEndpoint(
        id = "eureka/kucoin"
)
public class EurekaKucoinOperationEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(EurekaKucoinOperationEndpoint.class);
    private EurekaClient eurekaClient;
    private EurekaServiceRegistry serviceRegistry;
    private EurekaRegistration registration;
    private EurekaAutoServiceRegistration eurekaAutoServiceRegistration;

    public EurekaKucoinOperationEndpoint(ApplicationContext applicationContext, EurekaClient eurekaClient, EurekaServiceRegistry serviceRegistry, CloudEurekaInstanceConfig instanceConfig, ApplicationInfoManager applicationInfoManager, ObjectProvider<HealthCheckHandler> healthCheckHandler) {
        this.eurekaClient = eurekaClient;
        this.serviceRegistry = serviceRegistry;
        this.registration = EurekaRegistration.builder(instanceConfig).with(applicationInfoManager).with(eurekaClient).with(healthCheckHandler).build();
        this.eurekaAutoServiceRegistration = new EurekaAutoServiceRegistration(applicationContext, serviceRegistry, this.registration);
    }

    @PostMapping({"up"})
    public String up() {
        try {
            this.eurekaAutoServiceRegistration.start();
            return InstanceStatus.UP.toString();
        } catch (Exception var2) {
            LOG.error("fail to up", var2);
            return InstanceStatus.UNKNOWN.toString();
        }
    }

    @PostMapping({"/shutdown"})
    public String kucoinShutdown() {
        try {
            this.eurekaClient.shutdown();
            return InstanceStatus.OUT_OF_SERVICE.toString();
        } catch (Exception var2) {
            LOG.error("fail to shutdown", var2);
            return InstanceStatus.UNKNOWN.toString();
        }
    }

    @PostMapping({"/down"})
    public String down() {
        try {
            this.eurekaAutoServiceRegistration.stop();
            return InstanceStatus.DOWN.toString();
        } catch (Exception var2) {
            LOG.error("fail to down", var2);
            return InstanceStatus.UNKNOWN.toString();
        }
    }

    @GetMapping({"/status"})
    public Object getStatus() {
        try {
            HashMap<String, Object> statusMap = (HashMap)this.serviceRegistry.getStatus(this.registration);
            return statusMap.get("status");
        } catch (Exception var2) {
            LOG.error("fail to getStatus", var2);
            return InstanceStatus.UNKNOWN.toString();
        }
    }
}

