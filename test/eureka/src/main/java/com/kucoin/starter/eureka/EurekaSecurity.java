package com.kucoin.starter.eureka;


import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.AuthorizedUrl;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@ConditionalOnClass({WebSecurityConfigurerAdapter.class})
@ConditionalOnMissingBean({WebSecurityConfigurerAdapter.class})
@ConditionalOnWebApplication(
        type = Type.SERVLET
)
public class EurekaSecurity extends WebSecurityConfigurerAdapter {
    public EurekaSecurity() {
    }

    protected void configure(HttpSecurity http) throws Exception {
        ((HttpSecurity)((AuthorizedUrl)((AuthorizedUrl)((AuthorizedUrl)((HttpSecurity)((HttpSecurity)http.csrf().disable()).sessionManagement().disable()).authorizeRequests().requestMatchers(new RequestMatcher[]{EndpointRequest.to(new String[]{"health", "info", "prometheus"})})).permitAll().requestMatchers(new RequestMatcher[]{EndpointRequest.toAnyEndpoint()})).authenticated().anyRequest()).permitAll().and()).httpBasic();
    }
}
