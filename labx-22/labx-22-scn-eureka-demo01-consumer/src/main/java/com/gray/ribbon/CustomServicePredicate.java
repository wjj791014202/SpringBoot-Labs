package com.gray.ribbon;

import com.gray.GrayProperties;
import com.gray.ribbon.ServiceInfoExtractor;
import com.gray.utils.ApplicationContextHelper;
import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PredicateKey;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.PatternMatchUtils;
import reactor.util.annotation.Nullable;

public class CustomServicePredicate extends AbstractServerPredicate {
    private GrayProperties grayProperties;

    public CustomServicePredicate(GrayProperties grayProperties) {
        this.grayProperties = grayProperties;
    }

    public void setRule(IRule rule) {
        this.rule = rule;
    }

    public boolean apply(@Nullable PredicateKey input) {
        ChainRequestContext chainCtx = ChainRequestContext.getCurrentContext();
        if (chainCtx != null && chainCtx.get("SERVICE-ID") != null) {
            String requestCurrency = (String)chainCtx.get(this.grayProperties.getCustom().getRequestKey());
            if (requestCurrency == null) {
                requestCurrency = "default_currency";
            }

            String serviceId = chainCtx.getString("SERVICE-ID");
            Map<String, String> serviceMeta = ((ServiceInfoExtractor) ApplicationContextHelper.getApplicationContext().getBean(ServiceInfoExtractor.class)).getMetadata(serviceId, input.getServer());
            return serviceMeta.get("versions") != null ? false : this.matchCurrencies(requestCurrency, (String)serviceMeta.get(this.grayProperties.getCustom().getServiceKey()));
        } else {
            return true;
        }
    }

    private boolean matchCurrencies(String requestCurrency, String serviceCurrencies) {
        return StringUtils.isEmpty(serviceCurrencies) ? false : Stream.of(serviceCurrencies.split(",")).anyMatch((currency) -> {
            return PatternMatchUtils.simpleMatch(currency, requestCurrency);
        });
    }
}

