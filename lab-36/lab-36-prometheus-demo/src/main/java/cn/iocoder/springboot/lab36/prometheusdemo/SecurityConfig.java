package cn.iocoder.springboot.lab36.prometheusdemo;

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // <1> 访问 EndPoint 地址，需要经过认证，并且拥有 ADMIN 角色
        http.regexMatcher("/actuator/beans").authorizeRequests((requests) ->
                requests.anyRequest().hasRole("ADMIN"));
        // <2> 开启 Basic Auth
        http.httpBasic();
    }

}
