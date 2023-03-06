package com.gray.ribbon;

import com.netflix.loadbalancer.Server;
import java.util.Map;
import org.springframework.cloud.netflix.ribbon.DefaultServerIntrospector;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

public class ServiceInfoExtractor {
    private SpringClientFactory springClientFactory;

    public ServiceInfoExtractor(SpringClientFactory clientFactory) {
        this.springClientFactory = clientFactory;
    }

    public ServerIntrospector getServerIntrospector(String serviceId) {
        ServerIntrospector serverIntrospector = (ServerIntrospector)this.springClientFactory.getInstance(serviceId, ServerIntrospector.class);
        if (serverIntrospector == null) {
            serverIntrospector = new DefaultServerIntrospector();
        }

        return (ServerIntrospector)serverIntrospector;
    }

    public Map<String, String> getMetadata(String serviceId, Server server) {
        return this.getServerIntrospector(serviceId).getMetadata(server);
    }
}
