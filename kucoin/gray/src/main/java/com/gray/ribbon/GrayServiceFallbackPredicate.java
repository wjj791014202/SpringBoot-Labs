package com.gray.ribbon;

import com.gray.utils.ApplicationContextHelper;
import com.kucoin.starter.chaincontext.ChainRequestContext;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.annotations.Nullable;

public class GrayServiceFallbackPredicate extends AbstractServerPredicate {
    public GrayServiceFallbackPredicate(GrayZoneAvoidanceRule rule) {
        super(rule);
    }

    public boolean apply(@Nullable PredicateKey input) {
        ChainRequestContext chainCtx = ChainRequestContext.getCurrentContext();
        if (chainCtx != null && chainCtx.get("SERVICE-ID") != null) {
            String serviceId = chainCtx.getString("SERVICE-ID");
            Map<String, String> serviceMeta = ((ServiceInfoExtractor) ApplicationContextHelper.getApplicationContext().getBean(ServiceInfoExtractor.class)).getMetadata(serviceId, input.getServer());
            String serviceVersion = (String)serviceMeta.get("versions");
            return StringUtils.isEmpty(serviceVersion) || "default".equals(serviceVersion);
        } else {
            return true;
        }
    }
}
