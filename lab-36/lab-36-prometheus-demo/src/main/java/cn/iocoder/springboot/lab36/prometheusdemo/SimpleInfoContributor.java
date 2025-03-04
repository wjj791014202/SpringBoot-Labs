package cn.iocoder.springboot.lab36.prometheusdemo;

import java.util.Collections;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class SimpleInfoContributor implements InfoContributor {

    @Override
    public void contribute(Builder builder) {
        builder.withDetail("demo",Collections.singletonMap("key", "value")).build();
    }
}
